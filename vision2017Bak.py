# -*- coding: utf-8 -*-
"""
Created on Fri Jan 27 12:47:59 2017

@author: Jeffrey Bryant
"""
from os import listdir
from os.path import isfile, join
import cv2
import numpy as np
import matplotlib.pyplot as plt


    
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

nColors = 4    
def processImage(img,display=False):
    
   
    # Convert to HSV
    img1 = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)
    
    # filter by color and intensity
    lower_blue = np.array([60,100,40])
    upper_blue = np.array([100,255,255])
    mask = cv2.inRange(img1, lower_blue, upper_blue)

    # mask off the grayscale image
    gray = img1[:,:,2]
    ret = cv2.bitwise_and(gray,gray, mask= mask)
    cv2.imshow('ret',ret)
    
    # sum in x and y looking the two vertical bars
    xsum = np.sum(ret,0)
    ysum = np.sum(ret,1)
    
    xpeaks = findPeaks(xsum)
    ypeaks = findPeaks(ysum)
    print 'X=',xpeaks,'    Y=',ypeaks
    
    if len(xpeaks) == 2 and len(ypeaks) > 0:
        xcenter = xpeaks[0][1] + (xpeaks[1][0]-xpeaks[0][1]) / 2
        yend = len(ypeaks)-1
        ycenter = ypeaks[0][0] + (ypeaks[yend][1]-ypeaks[0][0]) / 2
        drawCrosshair(img,xcenter,ycenter,True)
    
    cv2.imshow('Input',img)
    
    if display:
        plt.figure(1)
        plt.subplot(2,1,1)
        plt.plot(xsum)
        
        
        plt.subplot(2,1,2)
        plt.plot(ysum)
        
        plt.show(10)
    
    return ret
    
    

    

def thresholdImage(img):
    pass

def findGearTarget(img):
    pass

def findHighGoalTarget(img):
    pass

def findPeaks(arr):
    ret = []
    looking = True
    start = 0
    maxVal = max(arr)
    for k in range(len(arr)):
        if looking:
            if arr[k] > maxVal / 2:
                looking = False
                start = k
        else:
            if arr[k] < maxVal / 4:
                looking = True
                ret.append((start,k))
    if not looking:
        ret.append((start,len(arr)-1))
        
    return ret
                

def processDirectory(imgdir):
    """
    Perform an analysis on all the .jpg files in a directory
    """
    
    onlyfiles = [f for f in listdir(imgdir) if isfile(join(imgdir, f))]
    for fn in onlyfiles:
        path = join(imgdir, fn)
        if fn[-4:].lower() == '.jpg':
            imgInput = cv2.imread(path)
            

            ch = 0xFF & cv2.waitKey(1000)
            
            if ch == 27:
                break
            
            processed = processImage(imgInput,True)
            
            
  
def processLive():
    
    cap = cv2.VideoCapture(0)
    while True:
        ret, frame = cap.read()
        processed = processImage(frame,True)
        #cv2.imshow('raw',frame)
        ch = 0xFF & cv2.waitKey(10)
            
        if ch == 27:
            break
       

if __name__ == '__main__':
    """
    Main test program
    """
    print "OpenCV Version:",cv2.__version__
    
    processDirectory("LED Peg")
    #processLive()
    #processDirectory("LED Boiler")
    #processDirectory("Red Boiler")
    #processDirectory("Blue Boiler")
    


