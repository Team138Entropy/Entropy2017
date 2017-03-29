# -*- coding: utf-8 -*-
"""
Created on Thu Jan 21 16:44:18 2016
@author: jeffrey.f.bryant
"""

import sys, traceback
import math
import time
import numpy as np
import cv2
import socket
import json
import driversStation as ds

currentTimeMs = lambda: int(round(time.time() * 1000))

showImages = False
showDebugImages = False
debugPrint = False
printReport = False
UDP_IP = "localhost"
UDP_PORT = 5802
reportSocket = None
resolution = (640,480)
detectThreshold = 0.95
targetOn = False
displayThresholdStart = currentTimeMs()
displayThresholdTimeout = 15000

if __name__ == '__main__':
    """
    Main test program
    """
    print "FindTarget1  OpenCV Version:",cv2.__version__
    

def image2World(img):
    """
    Convert image coordinates to world coordinates for
    reporting. img is the (x,y) image coordinates with (0,0)
    at the upper left. World coordinates have (0,0) at the center and
    1.0 is the largest side. The aspect is preserved.
    """
    center = (resolution[0]/2,resolution[1]/2)
    if resolution[0] > resolution[1]:
        scale = resolution[0]/2.0
    else:
        scale = resolution[1]/2.0
    x = (img[0]-center[0]) / scale
    y =  -(img[1]-center[1]) / scale
    return x,y

def printTgt(tgt):
    """ 
    Print out the target parameters on the console
    Stop when we are missing one.
    """
    
    try:
        print "--------target -------------"
        valid = tgt['match']
        print "  Valid=",valid
        
        bounds = tgt['bounds']
        print "  Bounds= ",bounds
        
        labels = tgt['labels'] 
        print "  Labels= ",labels.ravel()
        
        points = tgt['corners']
        print "  Corners= ",points
         
        lines = tgt['lines']
        print "  Lines= ",lines
    except:
        pass
    

def reportSetup(addr,port):
    """ 
    Setup the UDP socket to send the periodic report
    """
    
    global UDP_IP,UDP_PORT,reportSocket

    UDP_IP = addr
    UDP_PORT = port

    print "UDP target IP:", UDP_IP
    print "UDP target port:", UDP_PORT

    reportSocket = socket.socket(socket.AF_INET, # Internet
                         socket.SOCK_DGRAM) # UDP
                         
    print "Socket Setup:",reportSocket

def sendReport(collectTime,targets):
    """ 
    Send a traget report to the robot at the end of a frame. It is formated
    as a JSON message dictionary using the keywords listed below:
    """
    sendTime = currentTimeMs()
    rpt = {'id':'tgt'}
    rpt['sendTime'] = sendTime
    rpt['collectTime'] = collectTime;
    rpt['resolution'] = resolution
    rpt['distance'] = 10.0
    if len(targets) > 0:
        t = targets[0]
        
        rpt['match'] = t['match']
        bounds = t['bounds']
        x = bounds[0] + bounds[2]/2
        y = bounds[1] + bounds[3]/2
        aim = image2World((x,y))
        rpt['aim'] = aim
        
#    count = 0
#    for t in targets:
#        tgtName = 'tgt' + str(count)
#        rpt[tgtName] = t
    msg = json.dumps(rpt)
    if (printReport):
        print "<<<<<<<<<<<<<<<<<<<<<<RobotMsg>>>>>>>>>>>>>>>>>"
        print msg
        print "<<<<<<<<<<<<<<<<<<<<<<<<End>>>>>>>>>>>>>>>>>>>>"
        
    if (reportSocket != None):
        reportSocket.sendto(msg, (UDP_IP, UDP_PORT))
    
        
def seperateByColor(img,K):
    """
    Use K Means clustering to segment a colored image
    The return is an array of segment numbers for each pixel
    and the corresponding colors in an array indexed by segment
    
    """
    
    # flatten on a list of colors
    Z = img.reshape((-1,3))

    # convert to np.float32
    Z = np.float32(Z)
    
    # define criteria, number of clusters(K) and apply kmeans()
    criteria = (cv2.TERM_CRITERIA_EPS + cv2.TERM_CRITERIA_MAX_ITER, 10, 1.0)
    ret,label,center=cv2.kmeans(Z,K,None,criteria,10,cv2.KMEANS_RANDOM_CENTERS)
    length = img.shape[0]
    width = img.shape[1]
    label = label.reshape((length,width))
    center = np.uint8(center)
    
    return label,center
    
from matplotlib import pyplot as plt
def seperateByColor1(img,K):
    """
    TODO: Work in progress
    Use colormap histogram to seperate binary images
    by brightness and hue
    """
    
    hsv_map = np.zeros((180, 256, 3), np.uint8)
    h, s = np.indices(hsv_map.shape[:2])
    hsv_map[:,:,0] = h
    hsv_map[:,:,1] = s
    hsv_map[:,:,2] = 255
    hsv_map = cv2.cvtColor(hsv_map, cv2.COLOR_HSV2BGR)
    cv2.imshow('hsv_map', hsv_map)
   
    hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)
    dark = hsv[...,2] < 1
    hsv[dark] = 0
    h = cv2.calcHist( [hsv], [0,1], None, [180, 256], [0, 180, 0, 256] )

    vis = hsv_map*h[:,:,np.newaxis] / 255.0 / 20.0
    cv2.imshow('hist', vis)
    
    plt.ion()
    v = hsv[:,:,2]
    sums,bins = np.histogram(v.ravel(),255)
    sums[0] = 0
    plt.clf()
    plt.plot(sums)
   
    
def sortedColorList(colors): 
    """
    Sort a list of colors by a simple magnitude approx
    and return tuples of (magnitude,index,color)
    """
    colorList = []
    n = 0
    for c in colors:
        mag = int(c[0]) + int(c[1]) + int(c[2])
        colorList.append((mag,n,c))
        n = n + 1
    colorList = sorted(colorList,reverse=True)
    return colorList
    
def adaptiveThreshold(img,percentile):
    """
    Threshold an image by producting a histogram of the intensities
    and then a cumulative array and setting the threshold at the
    percentile value (fraction between 0 and 1.0)
    """
    sums,bins = np.histogram(img.ravel(),256)
    cumsums = np.cumsum(sums)
    threshold = 0
    limit = cumsums[255] * percentile
    for k in cumsums:
        if k < limit:
            threshold = threshold + 1
        else:
            break
    ret,img1 = cv2.threshold(img,threshold,255,cv2.THRESH_BINARY)
    return img1
    
    
def makeImage(mask,layers,colors):
    """
    Render image segments segments in binary
    Note: layers are selected from brightest to dim
    """
    img = np.zeros(mask.shape,dtype=np.uint8)
    colorList = sortedColorList(colors)
    
    for layer in layers:
        sortedLayer = colorList[layer][1]
        select = (mask == sortedLayer)
        img[select] = 255
    return img
    
 
def makeImageColor(mask,layers,colors):
    """
    Render image segments segments preserving segment color
    Note: layers are selected from brightest to dim
    """
    
    newShape = (mask.shape[0],mask.shape[1],3)
    img = np.zeros(newShape,dtype=np.uint8)
    colorList = sortedColorList(colors)
    
    for layer in layers:
        sortedLayer = colorList[layer][1]
        select = (mask == sortedLayer)
        img[select] = colorList[layer][2]
           
    return img

#
def cleanupImage(img,minSize):
    """
    cleanup an image removing low level noise
    """
    kernel = np.ones((minSize,minSize),np.uint8)
    ret = cv2.morphologyEx(img, cv2.MORPH_OPEN, kernel)
    return ret
    
def removeDupContours(inContours):
    """
    Remove duplicates that start at the same location and have the same bounds
    """
    if len(inContours) == 0:
        return []
    ret = []
    last = inContours[0]
    ret.append(last)
    lastx,lasty,lastw,lasth = cv2.boundingRect(last)
    for c in inContours:
        x,y,w,h = cv2.boundingRect(c)
        if (x != lastx) or (y != lasty) or (h != lasth) or (w != lastw):
            ret.append(c);
            last = c;
            lastx,lasty,lastw,lasth = cv2.boundingRect(last)
    return ret
    
def drawCrosshair(img,x,y,match):
    """
    Draw a crosshair on the image at the specified coordinates
    """    
    size = 21
    if (match):
        color = (0,0,255)
    else:
        color = (0,255,255)
    thickness = 2
    
    cv2.line(img,(x-size,y),(x+size,y),color,thickness)
    cv2.line(img,(x,y-size),(x,y+size),color,thickness)
    for k in range(4):
        cv2.circle(img,(x,y),k*size/3,color,1)
    
def drawTargets(img,targets):
    """ 
    Draw all the targets on the specified image
    """
    for t in targets:
        bounds = t['bounds']
        x = bounds[0] + bounds[2]/2
        y = bounds[1] + bounds[3]/2
        drawCrosshair(img,int(x),int(y),t['match'])
        
        
def drawAimPoint(img,distance):
    """
    Draw an aim point adjusting for distance
    """
    height,width,colors = img.shape
    boxWidth = 90
    boxHeight = 60
    color = (255,255,255)
    
    point1 = (width/2 - boxWidth/2,height/2 - boxHeight/2)
    point2 = (width/2 + boxWidth/2,height/2 + boxHeight/2)
    cv2.rectangle(img,point1,point2,color,2)
    
    center = (width/2,height/2)
    cv2.circle(img,center,3,color,-1)
    
    return
    
def vline(img,color,offset,thickness):
     
    height,width,colors = img.shape
    point1 = (offset,0)
    point2 = (offset,height-1)
    cv2.line(img,point1,point2,color,thickness)
    
def hline(img,color,offset,length,thickness):
     
    height,width,colors = img.shape
    point1 = (width/2-length/2,offset)
    point2 = (width/2+length/2,offset)
    cv2.line(img,point1,point2,color,thickness)

def drawAimCrosshairs(img):
    """
    Draw a targeting scale
    """
    height,width,colors = img.shape
    color = (255,255,255)
    vline(img,color,width/2,2)
    vline(img,color,width/2-width/10,1)
    vline(img,color,width/2+width/10,1)
    
    hline(img,color,height/2,width/10,2)
    inc = height/4
    for k in range(3):
        hline(img,color,height/2+(k+1)*inc,width/10,1)
        hline(img,color,height/2-(k+1)*inc,width/10,1)
                
            
def qualifyImage(img):
    """ qualify an image by computing the pixel count around
        the left,right,bottom and inside quarters.
        The majority of the pixels must be around the
        outside parameters excluding the top
        """
    h,w = img.shape
    qw = w/4
    qh = h/4
    
    # The image must be approc the right shape no side is < 4x the other
    bigSide = w
    if (bigSide < h):
        bigSide = h
    if (h*4 < bigSide):
        return False
    if (w*4 < bigSide):
        return False
        
    # Look at image shape by summing pixels in each of 4 regions
    suml = np.sum(img[:h,:qw],dtype=np.float) # left 1/4
    sumr = np.sum(img[:h:,3*qw:],dtype=np.float) # right 1/4
    sumb = np.sum(img[qh*3:,qw:3*qw],dtype=np.float) # bottom exluding left,right
    sumi = np.sum(img[0:qh*3,qw:qw*3],dtype=np.float) # inside 
    sumt = suml+sumr+sumb

    if (sumt == 0):
        return False
        
#TODO: Set the threshold
    ratio = sumi / sumt    
    if ratio < 0.18 and h > 30 and w > 30:
        if (showImages and showDebugImages):
            cv2.imshow('Match',img)
        #print ratio,"detected",w,h,sumi,suml,sumr,sumb
        return True
    else:
        #print ratio,"NotDetected",w,h,sumi,suml,sumr,sumb
        return False
                
def qualifyImage1(img):
    """ 
    TODO: qualifyImage1: This routine does not do much to elimiate false targets
        Qualify an image by computing line segment slopes for lines
        computed by the hough transform and checking if the majority of 
        the slopes form clusters (sets of parallel lines)
        
        return pass/fail,the vertical and horiz angles for
               the parallel lines
    
    """
    vertLimit = 15
    nbins = 32
    dims = img.shape
    minLen = dims[0]
    if (minLen > dims[1]):
        minLen = dims[1]
    minLen = minLen * 1 / 2
    if (minLen > 100):
        minLen = 100
    
    lines = cv2.HoughLines(img,3,np.pi/90,minLen)
    thetas = lines[:,0,1] * 180 / np.pi
    hist,edges = np.histogram(thetas,bins=nbins)
    zeroCount = sum(hist == 0)
    ratio = float(zeroCount)/nbins
    
    # Construct a list of vertical lines (+- 10 degrees from straight vertical)
    verts1 = thetas[thetas < vertLimit]
    verts2 = thetas[thetas > (180.0-vertLimit)]
    verts2 = verts2 - 180.0 # handle wrap around
    verts1 = np.append(verts1,verts2)
    
    # Construct a list of the others (not vertical)
    set1 = thetas > vertLimit
    set2 = thetas < (180.0-vertLimit)
    set3 = np.logical_and(set1,set2)
    others = thetas[set3]
       
    # Compute the mean values for return
    if (len(verts1) > 0):
        avgv = np.mean(verts1)
    else:
        avgv = 0.0
    if (len(others) > 0):
        avgh = np.mean(others)
    else:
        avgh = 90.0
    
    if (debugPrint):
        print 'QualifyImage1 ratio: ',ratio,' Segs: ', len(thetas)
        print '  hist:',hist
        print '  vert slope = ',avgv, ' horiz slope= ',avgh
    
    return ratio > 0.4,avgv,avgh
    
def calcVector(p1,p2):
    """
    calculate a line vector in polar form from two points. The return
    is the length of the line and the slope in degrees
    """
    dx = p2[0]-p1[0]
    dy = p2[1]-p1[1]
    mag = math.sqrt(dx * dx + dy * dy)
    theta = math.atan2(dy,dx) * 180.0 / math.pi + 90.0
    return mag,theta
    
def sortCorners(tgt):
    """
    Sort the corners and enter into a dictionary with keys for
    'ul','ll','ur','lr' the value stored is the (x,y) coordinate
    The return is an updated target dictionary with the corner points
    and edge lines upadted
    """

    try:
        corners = tgt['centers']
        ind = np.lexsort((corners[:,1],corners[:,0]))
        scorners = corners[ind]
    
        xcenter = np.mean(scorners[:,0])
        ycenter = np.mean(scorners[:,1])
        
        points = {}
        for x,y in scorners:
            if (x < xcenter):
                if (y < ycenter):
                    points['ul'] = (x,y)
                else:
                    points['ll'] = (x,y)
            else:
                if (y < ycenter):
                    points['ur'] = (x,y)
                else:
                    points['lr'] = (x,y)
                    
        tgt['corners'] = points
                  
        
        lines = {}
        if (len(points) < 4): # Must be a rect or trapesoid
            tgt['match'] = False
        else:
            lines['left']   = calcVector(points['ll'],points['ul'])
            lines['right']  = calcVector(points['lr'],points['ur'])
            lines['bottom'] =  calcVector(points['ll'],points['lr'])
            lines['top']    =  calcVector(points['ul'],points['ur'])
            tgt['lines'] = lines
    except:
        pass

    
def checkSides(s1,s2):
    """
    Check opposite sides to make sure they are nearly the same length and
    parallel to each other. Return is true is they meet the criteria.
    """
    avlen = (s1[0] + s2[0]) /2
    deltaLen = abs(s1[0] - s2[0])
    ratio1 = deltaLen / avlen
    if (ratio1 < 0.2):
        lenMatch = True
    else:
        lenMatch = False
        
    deltaTheta = abs(s1[1] - s2[1])
    if (deltaTheta > 345.0):
        deltaTheta = 360.0-deltaTheta
    if (deltaTheta < 15.0):
        thetaMatch = True
    else:
        thetaMatch = False
        
    match = thetaMatch and lenMatch
        
    if (debugPrint):
        print "checkSides:",s1[0],s2[0],ratio1,s1[1],s2[1],deltaTheta
        print "checkSides=",match
            
    return match
    
        
def qualifyImage2(img,params):
    """Qualify an image using the good features to track algorithm
    The params dictionary is updated with the measured parameters.
    The 'match' item indicates we have a good target to shoot at.
    """
    
    # Create a new image with a border so the goodFeaturesToTrack works
    border = 5
    height,width = img.shape
    width1 = width + border*2
    height1 = height + border*2
    testImg = np.zeros((height1,width1),dtype=np.ubyte)
    testImg[border:border+height,border:border+width] = img[:,:]
    
    try:
    
        #Smooth the image and find the features
        n = 5
        kernel = np.ones((n,n),np.float32)/n/10
        dst = cv2.filter2D(testImg,-1,kernel)
        features = cv2.goodFeaturesToTrack(dst,8,0.05,5)
        
        # Look for 4 clusters that should be at the extrimes
        # TODO: Check return from goodFeaturesToTrack
        criteria = (cv2.TERM_CRITERIA_EPS + cv2.TERM_CRITERIA_MAX_ITER, 10, 1.0)
        ret,label,center=cv2.kmeans(features,4,None,criteria,10,cv2.KMEANS_RANDOM_CENTERS)
         
        #plot them
        if not(features is None):
            features = np.int0(features)
            params['features'] = features - border # place orig coordinate system
            for i in features:
                x,y = i.ravel()
                cv2.circle(dst,(x,y),3,255,-1)
                
        # Compute the metrics (corners and lines)
        if not(center is None):
            params['centers'] = center - border # place orig coordinate system
            params['labels'] = label
            center = np.int0(center)
            for i in center:
                x,y = i
                cv2.circle(dst,(x,y),5,255,-1)
                
            # validate we are looking at our target ( 4 clusters with 2 points each)
            test = sorted(label.copy())
            good = True
            even = test[0::2]
            odd = test[1::2]
            good = (even == odd) and len(features) == 8
            params['match'] = good
            sortCorners(params)
            
            try:            
                # Check the length and orientation of the corner lines
                if len(params['lines']) == 4:
                    lines = params['lines']
                    vert = checkSides(lines['left'],lines['right'])
                    horiz = checkSides(lines['top'],lines['bottom'])
                    if (horiz and vert):
                        params['match'] = True
            except:
                pass
        
        if (showDebugImages):
            cv2.imshow('corners',dst)

    except:
        print "Exception in qualifyImage2:"
        print '-'*60
        traceback.print_exc(file=sys.stdout)
        print '-'*60
        params['match'] = False
        if (showDebugImages):
            cv2.imshow('corners Exception',testImg)   
                    
def qualifyImage3(img):
    """ 
    TODO: qualifyImage3: Work in progress
    Find regions using simpleblob detection work in progress
    """
    
    # Setup SimpleBlobDetector parameters.
    params = cv2.SimpleBlobDetector_Params()
     
#==============================================================================
#     # Change thresholds
    params.minThreshold = 10;
    params.maxThreshold = 200;
#      
#     # Filter by Area.
#     params.filterByArea = True
#     params.minArea = 1500
#      
#     # Filter by Circularity
#     params.filterByCircularity = True
#     params.minCircularity = 0.1
#      
#     # Filter by Convexity
#     params.filterByConvexity = True
#     params.minConvexity = 0.87
#      
#     # Filter by Inertia
#     params.filterByInertia = True
#     params.minInertiaRatio = 0.01
#==============================================================================
     
    # Create a detector with the parameters
    ver = (cv2.__version__).split('.')
    if int(ver[0]) < 3 :
        detector = cv2.SimpleBlobDetector(params)
    else : 
        detector = cv2.SimpleBlobDetector_create(params)
    
     
    # Detect blobs.
    keypoints = detector.detect(img)
 
    # Draw detected blobs as red circles.
    # cv2.DRAW_MATCHES_FLAGS_DRAW_RICH_KEYPOINTS ensures the size of the circle corresponds to the size of blob
    im_with_keypoints = cv2.drawKeypoints(img, keypoints, np.array([]), (0,0,255), cv2.DRAW_MATCHES_FLAGS_DRAW_RICH_KEYPOINTS)
     
    # Show keypoints
    cv2.imshow("Keypoints", im_with_keypoints)
    
    
def locateContours(img,xoffset,yoffset):
    """
    locate contours and apply a simple set of metrics
    to validate the contour is a target. The result is a
    list of dictionaries the represent each potential
    target.
    """
    retParams = []
    
     #Find contours and sort largest to smallest
    edged = cv2.Canny(img, 30, 200)
    img1, contours, hierarchy = cv2.findContours(edged.copy(),cv2.RETR_TREE,cv2.CHAIN_APPROX_SIMPLE)
    contours = sorted(contours, key = cv2.contourArea, reverse = True)[:75]
    if (debugPrint):
        print "Contours:",len(contours)
    contours = removeDupContours(contours)
    if (debugPrint):    
        print "# of No Duplicate Contours:",len(contours)

    # Test each contour determining if it is a target
    for cnt in contours:
        x,y,w,h = cv2.boundingRect(cnt)
        
        testImg = img[y:y+h,x:x+w] # pull out the binary image
        isTarget = qualifyImage(testImg)
        
        if (isTarget):
            #testImg1 = edged[y:y+h,x:x+w]
            #reallyIsTarget,vslope,hslope = qualifyImage1(testImg1)
            reallyIsTarget = True
            if (reallyIsTarget):
                rect = (x+xoffset,y+yoffset,w,h)
                params = {}
                params['bounds'] = rect
                qualifyImage2(testImg,params)
                retParams.append(params)
    
        
    if (showImages and showDebugImages):
        cv2.imshow('Edged', edged)
    return retParams
    


#return the angle,distance and camera orientation to target
def getOrientation(img,bounds,corners):
    """ 
    TODO: getOrientation implement this function
    """
    pass

def processImage(img,select,nColors,xoffset,yoffset):
    """ 
    Seperate out the image using colors
    and locate all targets. img is the input color image,
    select is a list of color planes (brightest..dimest),
    n colors is the number of colors (0 is use gray adaptive threshold)
    xoffset and yoffset are coordinates of the upper left of the image in
    the global image. The return is a list of target dictionries.
"""
    
    targets = []
    
    try:
        if (debugPrint):
            print "#######################"
            print "NColors: ",nColors," select: ",select
            
        # Adaptive threshold on green
        if nColors < 0 and nColors > -1.0:
            gray = img[:,:,1]
            blur = cv2.GaussianBlur(gray,(5,5),0)
            img1 = adaptiveThreshold(blur,-nColors) 
        
        # 0 is gray
        elif int(nColors) == 0:
            gray = cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)
            blur = cv2.GaussianBlur(gray,(5,5),0)
            ret,img1 = cv2.threshold(blur,0,255,cv2.THRESH_BINARY+cv2.THRESH_OTSU) 
            #newThr = (255-ret) * .5 + ret
            #ret,img1 = cv2.threshold(blur,int(newThr),255,cv2.THRESH_BINARY)  
            
        # -1 is green only
        elif int(nColors) == -1:
            gray = img[:,:,1]
            blur = cv2.GaussianBlur(gray,(5,5),0)
            ret,img1 = cv2.threshold(blur,0,255,cv2.THRESH_BINARY+cv2.THRESH_OTSU) 
            #newThr = (255-ret) * .5 + ret
            #ret,img1 = cv2.threshold(blur,int(newThr),255,cv2.THRESH_BINARY) 
            
        elif int(nColors) == -2:
            gray = img[:,:,1]
            #blur = cv2.GaussianBlur(gray,(5,5),0)
            ret,img1 = cv2.threshold(gray,255,cv2.THRESH_BINARY+cv2.ADAPTIVE_THRESH_GAUSSIAN_C,11,2) 
            print "Adaptive = ",ret
            

            
        else:
        
            # Seperate out by colors and get the brightest
            mask,colors = seperateByColor(img,int(nColors))
            img1 = makeImage(mask,select,colors)
           
        # Cleanup the binary image
        img2 = cleanupImage(img1,5)
        
        #Locate all the contours
        targets = locateContours(img2,xoffset,yoffset)
        if (debugPrint):
            print 'Found ',len(targets),' Targets'   
      
        if (showImages and showDebugImages):
            cv2.imshow('Detected',img2)
            
        #qualifyImage3(gray)
            
    except :
       
        print "Exception in processImage:"
        print '-'*60
        traceback.print_exc(file=sys.stdout)
        print '-'*60
            
    return targets
    
def canQuit(targetList):
    """
    Return true if the target list contains at least one valid target
    """
    for t in targetList:
        try:
            valid = t['match']
            if valid:
                return True
        except:
            pass 
    return False
    
def subImageSearch(img,quitEarly=False):
    """
    Perform a search using a binary thresholding algorithm on the whole image
    and then splitting the image into segments and trying again until the
    search works
    """
    
    regions = [
    
    (0.0,0.0,1.0), # Full
    
    (0.25,0.25,0.5), # Center
    
    (0.0,0.25,0.5),  # Left and right of center
    (0.5,0.25,0.5),

    (0.25,0.0,0.5),  # Top center,left,right
    (0.0,0.0,0.5),
    (0.5,0.0,0.5),

    ]
    
    start = time.clock()
    
    retTargets = []
    
    # Create a sum image for each region and process
    for r in regions:
        w,h,c = img.shape
        left = r[0] * w
        top = r[1] * h
        width = r[2] * w
        height = r[2] * h
        rect = (left,top,width,height)
        img1 = img[top:top+width,left:left+height]
        
        if (debugPrint):
            print
            print 'subImageSearch: ',rect
        subTargets = processImage(img1,[],-detectThreshold,left,top)
        print "  Targets:",len(subTargets)
        for t in subTargets:
            retTargets.append(t)
        if quitEarly and canQuit(retTargets):
            break
        
    end = time.clock()
    if (debugPrint):
        print "subImageSearch Time = ",end-start
        
    return retTargets
        
    
def fullColorSearch(img,xoffset,yoffset,quitEarly=False):
    """
    Perform full search trying all color combinations in the list below
    and returning the set that works the best.
    """
    combinations = [
        ([],-detectThreshold),
        ([],-1,),
        ([],-2,),
        ([0],4)]
        #([0],8)]

    start = time.clock()
    bestColors = []
    bestTargets = []
    
    # Loop through all the targets 
    for k in combinations:
        targets = processImage(img.copy(),k[0],k[1],xoffset,yoffset)
        if len(bestTargets) == 0:
            bestTargets = targets
            bestColors = k
        if canQuit(targets):
            if not canQuit(bestTargets) == 0:
                bestTargets = targets
                bestColors = k
            if quitEarly:
                break
 
    end = time.clock()
    if (debugPrint):
        print "fullColorSearch Time = ",end-start
    return bestTargets,bestColors
    
def offset(targets,yof,xof):
    """
    Offset targets(x,y) found in a sub-image
    """
    ret = []
    for t in targets:
        ofs = (t[0]+yof,t[1]+xof,t[2],t[3])
        ret.append(ofs)
    return ret
    

        
def track(img,target,colors):
    """
    update a target track using the last target location and
    color parameters. If this fail expand the color search and
    then finally scan the whole image using a color search
    """
    
    if (debugPrint):
        print
        print ">>>>>>>>>>>>>>>> Start Track <<<<<<<<<<<<<<<<<<<<"
    
    try:
        
        start = time.clock()
    
        # Compute the region of interest (+- 50 %)
        region = target['bounds']
        extraw = int(0.5 * region[2])
        extrah = int(0.5 * region[3])
        left= region[0] - extraw
        top = region[1] - extrah
        width = region[2] + extraw*2
        height = region[3] + extrah*2
       
        
        # Clip the search area to the inside of the image
        if (left < 0): left = 0
        if (top < 0): top = 0
        right = left+width
        bottom = top+height
        (maxh,maxw,ncolors) = img.shape
        if (right > maxw):
            width = width - (right-maxw)
        if (bottom > maxh):
            height = height - (bottom-maxh)
            
        # Now do a simple match using last colorset and cutout image
        img1 = img[top:top+height,left:left+width]
        targets = processImage(img1.copy(),colors[0],colors[1],left,top)
        end = time.clock()
        if (debugPrint):
            print "Track Time: %10.3f  ms" % ((end-start) * 1000.0)
        
        
        # No luck with same colors do full color search on selected region
        if (len(targets) == 0):
            if (debugPrint):
                print ">>>>>>>>>>> Same color Track fail; trying Full color <<<<<<<<"
            targets,colors = fullColorSearch(img1.copy(),left,top)
     
            
        # TODO:  be smarter about expanding the search region
        if (len(targets) == 0):
            if (debugPrint):
                print ">>>>>>>>>>>>>Track Failed; performing full search <<<<<<<<<<<"
            targets,colors = fullColorSearch(img.copy(),0,0)
            
    except:
        targets = []
        colors = []
        print "Exception in track:"
        print '-'*60
        traceback.print_exc(file=sys.stdout)
        print '-'*60
        
    return targets,colors
    
def drawThreshold(img):
    """ 
    mark an image red where the pixel values are above the threshold
    """
    font = cv2.FONT_HERSHEY_SIMPLEX
    gray = img[:,:,1]
    blur = cv2.GaussianBlur(gray,(5,5),0)
    img1 = adaptiveThreshold(blur,detectThreshold)
    img1Not = cv2.bitwise_not(img1)
    
    zeros = np.zeros_like(img)
    fg = cv2.bitwise_or(zeros,(0,0,255),mask = img1)
    bg = cv2.bitwise_and(img,(255,255,255),mask = img1Not)
    
    newImg = cv2.add(fg,bg)
    s = "detectThreshold = "+ "%5.2f" % detectThreshold
    cv2.putText(newImg,s,(10,50), font, 1,(0,0,255),1,cv2.LINE_AA)

    return newImg

    
def displayThresholdMode(timeout=30000):
    """
    Set the display to annotate values in red above the threshold for
    a limited period of time set by the timeout parameter (ms)
    """
    global displayThresholdTimeout
    global displayThresholdStart
    displayThresholdTimeout = timeout
    displayThresholdStart =  currentTimeMs()
    
def drawAnnotatedImage(img,targets):
    """
    Draw the image with the aim point and targets
    """
    
    now = currentTimeMs()
    if (now < (displayThresholdStart + displayThresholdTimeout)):    
        img = drawThreshold(img)

    font = cv2.FONT_HERSHEY_SIMPLEX
    #drawAimPoint(img,100.0)
    drawAimCrosshairs(img)
    
    if targetOn:
        cv2.putText(img,'targetOn',(10,75), font, 1,(255,255,255),1,cv2.LINE_AA)
    else:
        cv2.putText(img,'targetOff',(10,75), font, 1,(255,255,255),1,cv2.LINE_AA)


    if len(targets) > 0:
        drawTargets(img,targets)
        
    if (showImages):
        cv2.imshow('Input', img)
    
    if (ds.socket != None):
        ds.sendImage(img)
        
def showAnnotatedImage(img,targets,waitTimeMs):
    global showImages
    """
    Draw the image with the aim point and targets and wait for it to display
    """
    oldShowImages = showImages
    showImages = True
    drawAnnotatedImage(img,targets)
    while True:
        ch = 0xFF & cv2.waitKey(waitTimeMs)
        if ch == 27:
            break
    showImages = oldShowImages
    
def startWorkers():
    pass
            


        