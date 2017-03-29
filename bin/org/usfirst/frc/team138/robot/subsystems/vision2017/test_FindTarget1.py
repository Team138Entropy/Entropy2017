# -*- coding: utf-8 -*-
"""
Created on Tue Feb 09 11:21:24 2016

@author: jeffrey.f.bryant
"""

import cv2
import FindTarget1 as ft
import driversStation as ds

############## Test all the files in the RealFullField directory
from os import listdir
from os.path import isfile, join


def wpiTest(imgdir,maxSize,resize):
    """
    Perform an analysis on all the .jpg files in a directory
    """
    results = []
    
    onlyfiles = [f for f in listdir(imgdir) if isfile(join(imgdir, f))]
    for fn in onlyfiles:
        path = join(imgdir, fn)
        if fn[-4:].lower() == '.jpg':
            imgInput = cv2.imread(path)
            
           #Resise the image if desired
            if (imgInput.shape[0] > maxSize[0]) and (imgInput.shape[1] > maxSize[1]) and resize:
                imgResized =cv2.resize(imgInput,maxSize)
            else:
                imgResized = imgInput
            print 20*'=',fn,imgResized.shape
            collectTime = ft.currentTimeMs()
            #targets,colors = ft.fullColorSearch(imgResized,0,0,True)
            targets = ft.subImageSearch(imgResized,True)
            ft.sendReport(collectTime,targets)
            if len(targets) > 0:
                ft.printTgt(targets[0])
            ft.drawAnnotatedImage(imgResized,targets)
            results.append((fn,targets))      # fast execution
            
            ch = 0xFF & cv2.waitKey(100)
            
            if ch == 27:
                break
            
            elif (ch == ord('t')):
                ft.displayThresholdMode(10000)
                
            elif (ch == ord('u')):
                ft.displayThresholdMode(10000)
                ft.detectThreshold = ft.detectThreshold + 0.01
                
            elif (ch == ord('d')):
                ft.detectThreshold = ft.detectThreshold - 0.01
                ft.displayThresholdMode(10000)
            
########### Main Entry Point           
if __name__ == '__main__':
    """
    Main test program
    """
    
    
    print "OpenCV Version:",cv2.__version__
    ft.showImages = True
    ft.showDebugImages = False
    trackTargets = False
    resize=True
    maxSize = (640,480)
    batchTest = True
    fullColorTest = False
    subImageTest = True
    
    ft.reportSetup('loopback',5802)
    ft.printReport = False
    ds.init('loopback',5800)
    
    ft.debugPrint = True
    
    # Read and resize the image
    #imgDir = 'C:\\Users\\jeffrey.f.bryant\\Desktop\\First2016\\'
    imgDir = 'C:\\Users\\jeffrey.f.bryant\\Desktop\\First2016\\RealFullField\\'
    #imgDir = 'C:\\Users\\jeffrey.f.bryant\\Desktop\\First2016\\CalShots\\'
    
    if (batchTest):
        wpiTest(imgDir,maxSize,resize)
              
    if (fullColorTest or subImageTest):
        imgInput = cv2.imread(imgDir + 'IMG_1087.JPG')
        #imgInput = cv2.imread(imgDir + 'IMG_1088.JPG')
        #imgInput = cv2.imread(imgDir + 'IMG_1091.JPG')   
        #imgInput = cv2.imread(imgDir + '192.JPG')
        #imgInput = cv2.imread(imgDir + '5.JPG')
        #imgInput = cv2.imread(imgDir + 'IMG_4347.JPG')
        
        ds.sendImage(imgInput)

        #Resise the image if desired
        if (imgInput.shape[0] > maxSize[0]) and (imgInput.shape[1] > maxSize[1]) and resize:
            imgResized =cv2.resize(imgInput,maxSize)
        else:
            imgResized = imgInput
            
        # perform full search and 10 tracking updates
        if fullColorTest:
            targets,colors = ft.fullColorSearch(imgResized,0,0,True)
            if trackTargets and len(targets) > 0:
                for k in range(10):
                    targets,colors = ft.track(imgResized,targets[0],colors)
                    
            for t in targets:
                ft.printTgt(t)
                
        if subImageTest:
            colors = ([],2)
            targets = ft.subImageSearch(imgResized,True)
            if trackTargets and len(targets) > 0:
                for k in range(10):
                    trackTargets,colors = ft.track(imgResized,targets[0],colors)
                    
            for t in targets:
                ft.printTgt(t)
                
            ft.sendReport(ft.currentTimeMs(),targets)
 
        # display results
        if (ft.showImages):
            ft.drawAnnotatedImage(imgResized,targets)
            while True:
                ch = 0xFF & cv2.waitKey(1)
                if ch == 27:
                    break
    cv2.destroyAllWindows()