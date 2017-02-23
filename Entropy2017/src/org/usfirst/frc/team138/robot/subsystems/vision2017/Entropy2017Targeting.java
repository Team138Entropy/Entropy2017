package org.usfirst.frc.team138.robot.subsystems.vision2017;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Entropy2017Targeting extends Thread {
	// Widget Name
	public static final String NAME = "ENTROPY-2017-TARGETING 2:18:2017";
	
	// Random unique identifier for the camera - leave this - otherwise, you will get a warning
	private static final long serialVersionUID = -412351776843653585L;
	
	
	enum Parameters	{
		lowHue,
		highHue,
		lowSat,
		highSat,
		highVal,
		lowVal,
		DilateSize,   // 4.0
		DilateKernel,
		CROSS_HAIR_X,
		CROSS_HAIR_Y,
		CROSS_HAIR_SIZE,
		MilliSecsBetweenPics,   // 1000
		ErodeSize,   // 4.0
		AspectMin,   // 1.0
		AspectMax,   // 5.0
		AreaMin,     // 3000
		AreaMax,     // 5000
		HeightMin,   // 25.0
		SaveRaw,
		SaveProcessed
	};
	
	static String rootFolder = "C:\\Users\\Team138\\Vision2017";
	
	// Network tables
	private NetworkTable table;
	
	// Keep track of frame number and processing results
	private long m_frameNumber = 0;

	private long highGoalx = 1000;
	private long highGoaly = 1000;
	private long highGoalxRange = 1000;
	
	private long pegyheight = 1000;
	private long pegxspace = 1000;
	private long pegx = 1000;
	private long pegy = 1000;
	private ArrayList<Double> angleList = new ArrayList<Double>();
	
	
	// Image Results to send back to robot
	// NOTE:  By convention, data in the array is as follows:
	//       imgResults[0] is frameNumber
	//       imgResults[1] is pegx   (1000.0 is a "magic number" meaning no target found)
	//       imgResults[2] is pegxspace   (1000.0 is a "magic number" meaning no target found)
	//       imgResults[3] is pegyheight   (1000.0 is a "magic number" meaning no target found)
	//       imgResults[4] is highGoalx (1000.0 is a "magic number" meaning no target found)
	//       imgResults[5] is highGoalxRange (1000.0 is a "magic number" meaning no target found)
	
	// Date object for saving images
	private Date m_lastSnapshotTime;
	private Date m_lastRawSnapshotTime;
	//private Date m_lastProcSnapshotTime;
	
	//private ParameterFile m_pt;
	private Properties theProperties;
	
	private String  SmartDashboardPath = "/SmartDashboard/extensions/";
	
	// test Main function to process a folder with images
	int framesToProcess = 0;
	
	public void run() {        
        CvSink cvSink = CameraServer.getInstance().getVideo();        
        Mat source = new Mat();
        
        while(!Thread.interrupted()) {
        	if (framesToProcess > 0)
        	{
        		cvSink.grabFrame(source);
                processImage(source);
                framesToProcess--;
        	}
        }
	}
	
	public ArrayList<Double> getCorrectionAngles()
	{
		@SuppressWarnings("unchecked")
		ArrayList<Double> temp = (ArrayList<Double>) angleList.clone();
		angleList.clear();
		return temp;
	}
	
	public void processFrames(int numFrames)
	{
		framesToProcess = numFrames;
	}
	
	public static void main(String[] args)
	{
//		
//		
//		String inputFolder = rootFolder + "\\LED Peg";
//		
//		String outputFolder = rootFolder + "\\LEDPeg_output";
//				
//		File folder = new File(inputFolder);
//		File[] listOfFiles = folder.listFiles();
//		
//		Entropy2017Targeting imageProcessor = new Entropy2017Targeting();
//		
//		for(File f : listOfFiles)
//		{
//			System.out.println();
//			System.out.println("---------------------------------------------------------------------------------------------");
//			System.out.println();
//			
//			Mat ourImage = Imgcodecs.imread(f.getPath());
//			System.out.println("File: " + f.getPath());
//			imageProcessor.processImage(ourImage);
//			Imgcodecs.imwrite(outputFolder + "\\"+ f.getName()+".png", imageProcessor.outputImage);
//			
//			
//			System.out.println("Output "+ f.getName());
//		}
//		//imageProcessor.disconnect();
//		System.out.println("Done.");
	}
	
	public Entropy2017Targeting() {
		m_frameNumber = 0;
		m_lastSnapshotTime = new Date();
		m_lastRawSnapshotTime = new Date();
		
		loadParameters();
	}
	
	Mat outputImage;
	protected void processImage(Mat m) {
		
		// default all returns to robot to no value
		pegx = 1000;
		pegy = 1000;
		pegyheight = 1000;
		pegxspace = 1000;
		
		highGoalx = 1000;
		highGoaly = 1000;
		highGoalxRange = 1000;
		
		//save the raw image
		//saveRawImage(m);
		
		// increment the frameNumber
		m_frameNumber++;
		
		if ( theProperties == null ) {
			//m_error = "parameter table missing";
			return; 
		}
		Mat step1 = getHSVThreshold(m);
		
		findPeg(step1);
		
		// TODO: Debug later
		//findHighGoal(step1);
		
		int width = m.cols();
		if (pegx != 1000)
		{
			angleList.add((double)((pegx-width/2))/width * 60.0);
		}
		
		
				
		if (pegx < 1000) {
			drawTarget(m, pegx, pegy,true);
		}
		
		if (highGoalx < 1000) {
			drawTarget(m, highGoalx, highGoaly,false);
		}
		
		outputImage = m;
		
		return;
    }
	
	/**
	 * Process pixels in the correct color range and cleanup the image
	 * @param m Input image
	 * @return Cleaned up image
	 */
	
	private Mat getHSVThreshold(Mat m) {
		
		// convert BGR values to HSV values
		Mat hsv = new Mat();
		Imgproc.cvtColor(m, hsv, Imgproc.COLOR_BGR2HSV);
		
		
		Mat inRange = new Mat();
		Core.inRange(
				hsv, 
				new Scalar(Double.parseDouble(theProperties.getProperty("lowHue")), 
					Double.parseDouble(theProperties.getProperty("lowSat")),
					Double.parseDouble(theProperties.getProperty("lowVal"))),
				new Scalar(Double.parseDouble(theProperties.getProperty("highHue")),
							Double.parseDouble(theProperties.getProperty("highSat")),
							Double.parseDouble(theProperties.getProperty("highVal"))), 
				inRange);
		
		Mat grey = new Mat();
		Imgproc.cvtColor(m, grey, Imgproc.COLOR_BGR2GRAY);
		Core.bitwise_and(grey, inRange, grey);

        Imgcodecs.imwrite(rootFolder + "/1_Post_inRange" + ".png", grey);
		
		return grey;
	}
	
	/**
	 * Debug routine to print peaks
	 * @param name
	 * @param peaks
	 */
	private static void printPeaks(String name,List<PeakLoc> peaks) {
		System.out.println("Peaks for " + name);
		for (int k=0;k<peaks.size();k++) {
			System.out.println("peak[" + k + "]= " + peaks.get(k).getStart() + " , " + peaks.get(k).getStop());
		}
		
		
	}
	
	/**
	 * Sum the rows or columns of a matrix
	 * @param m input 2D matrix as unsigned bytes
	 * @param byRow true is to sumy the matrix by row; otherwise by colums
	 * @return integer array of sums
	 */
	private static long[] sums(Mat m,boolean byRow) {
		
		int rows = m.rows();
		int cols = m.cols();
		byte[] data = new byte[rows*cols];
		long[] retSums = null;
		
		int status = m.get(0, 0,data);
		
		long total = 0;
		for (int k=0;k<data.length;k++) {
			total += Byte.toUnsignedInt(data[k]);
		}
		
		if (byRow) {
			retSums = new long[cols];
			for (int col=0;col<cols;col++) {
				retSums[col] = 0;
				for (int row=0;row<rows;row++) {
					int k = row*cols+col;
					retSums[col] += Byte.toUnsignedInt(data[k]);
				}
			}
		}
		else {
			retSums = new long[rows];
			for (int row=0;row<rows;row++) {
				retSums[row] = 0;
				for (int col=0;col<cols;col++) {
					int k = row*cols+col;
					retSums[row] += Byte.toUnsignedInt(data[k]);
				}
			}
		}
		
//		System.out.println("" + m.rows()+ "  " + m.cols());
//		System.out.println(status);
//		System.out.println(total);
		int total1 = 0;
		for (int k=0;k<retSums.length;k++) {
			total1+= retSums[k];
		}
		//System.out.println(total1);
	
		return retSums;
		
	}
	private void findPeg(Mat m){
		
	    long[] xsums = sums(m,true);
	    long[] ysums = sums(m,false);
	    
	    List<PeakLoc> ypeaks = findPeaks(ysums);
	    List<PeakLoc> xpeaks = findPeaks(xsums);
		 
		//locate peaks (all of them) in sums, for peg it will be 2 x peaks, 1 y peak
	    if ((xpeaks.size() == 2) && (ypeaks.size() >0)){
	    	pegx = (xpeaks.get(1).getStart() + xpeaks.get(0).getStop()) /2;
	    	pegxspace = xpeaks.get(1).getStart() - xpeaks.get(0).getStop();
	    	pegyheight = ypeaks.get(0).getStop() - ypeaks.get(0).getStart();
	    	pegy = ypeaks.get(0).getStart() + pegyheight/2;
	    	//System.out.println("pegx = " + pegx + " , " + "pegxspace = " + pegxspace + " , " + "pegyheight = " + pegyheight); 
	    }
	    
	}
	/**
	 * 
	 * @param m
	 * @param x
	 * @param y
	 */
	private void drawTarget(Mat m, long x, long y,boolean peg){
		
		int size = 40;
		Scalar color;
		if (peg) {
			color = new Scalar(0,0,255);
		}
		else {
			color = new Scalar(0,255,255);
		}
		
		Imgproc.line(m, new Point(x-size,y), new Point(x+size,y), color,2);
		Imgproc.line(m, new Point(x,y-size), new Point(x,y+size), color,2);
		for (int k =0;k<4;k++) {
			Imgproc.circle(m, new Point(x,y), (k+1)*size/4, color);
		}
		
	}
	
	/**
	 * locate the high goal
	 * @param m
	 */
	private void findHighGoal(Mat m) {
		
		/*
		 * private long highGoalx = 1000;
	       private long highGoaly = 1000;
	       private long highGoalxRange = 1000;
		 */
		
		// crop the image looking at top only
		int heightThreshold = m.height();
		Rect rectCrop = new Rect(0,0,m.width(),heightThreshold);
        Mat imageROI = m.submat(rectCrop);
        
        Imgcodecs.imwrite(rootFolder + "/1_Crop" + ".png", imageROI);
        
        
        // locate all the possible candidates
        long[] xsums = sums(imageROI,true);
        
	    List<PeakLoc> xpeaks = findPeaks(xsums,10);
	    
	    // look for 2 peaks in each candidate
	    for (int k=0;k<xpeaks.size();k++) {
	    	long xstart = xpeaks.get(k).getStart();
	    	long xstop = xpeaks.get(k).getStop();
	    			
	    	// Create an image within the peak
	    	Rect rectQual = new Rect((int)xstart,0,(int)(xstop-xstart),heightThreshold);
	        Mat imageQual = m.submat(rectQual);
	        
	        // sum within y within the peak
	        long[] ysums = sums(imageQual,false);
		    List<PeakLoc> ypeaks = findPeaks(ysums,10);
		    
		    // only process if we have 2 peaks (two circles)
		    if (ypeaks.size() == 2) {
		    	int height0 = (int) (ypeaks.get(0).getStop() - ypeaks.get(0).getStart());
		    	int height1 = (int) (ypeaks.get(1).getStop() - ypeaks.get(1).getStart());
		    	int space = (int) (ypeaks.get(0).getStop() - ypeaks.get(1).getStart());
		    	
		    	// compute the ratios; top is twice as big as bottom and space is same as bottom
		    	double heightRatio = (double)height0/height1;
		    	double spaceRatio = (double)space/height1;
		    	
		    	// check the porportions
		    	double hightRatioTol = 0.25;
		    	double spacetRatioTol = 0.5;
		    	if ((Math.abs(heightRatio-2.0) <= hightRatioTol) && ((Math.abs(spaceRatio-1.0) <= spacetRatioTol))) {
		    		
		    		// save targeting info and quit
		    		highGoalx = (xstop+xstart)/2;
		    		highGoaly = ypeaks.get(0).getStop();
		    		highGoalxRange = xstop-xstart;
		    		break;
		    		
		   
		    	} // size matches
		    	
		    } // #peaks in y is 2
		    
		    
	    } // for each peak in x
		
	
		
	}
	
	/**
	 * Peak location
	 * @author Team138
	 *
	 */
	private static class PeakLoc {
		private long start;
		private long stop;
		public PeakLoc(long start, long stop) {
			super();
			this.start = start;
			this.stop = stop;
		}
		public long getStart() {
			return start;
		}
		public void setStart(long start) {
			this.start = start;
		}
		public long getStop() {
			return stop;
		}
		public void setStop(long stop) {
			this.stop = stop;
		}
		
	}
	
	public static void main1(String[] args) {
		
		System.out.println("Start");
		long[] testData = new long[10];
		for (int k=0;k<testData.length;k++) {
			testData[k] = 0;
		}
		testData[2] = 10;
		testData[3] = 12;
		testData[7] = 15;
		testData[8] = 19;
		
		List<PeakLoc> peaks = findPeaks(testData);
		System.out.println(peaks);
		
	}
	private static List<PeakLoc> findPeaks(long[] sums,long minWidth) {
		
		long maxVal = Arrays.stream(sums).max().getAsLong();
		ArrayList<PeakLoc> ret = new ArrayList<PeakLoc>();
		boolean looking = true;
		long start = 0;
		
		for (int k=0;k<sums.length;k++) {
			if (looking){
				if ((sums[k]) > (maxVal/2)){
					looking = false;
					start = k;
				}
			}
			else{
				if ((sums[k]) < (maxVal/4)){
					looking = true;
					long width = (k-1)-start;
					if (width >= minWidth) {
					    ret.add(new PeakLoc(start, k-1));
					}
				}
			}
			
		}
		if (looking == false){
			ret.add(new PeakLoc(start, sums.length - 1));
		}
		
		return ret;
	}
	
	private static List<PeakLoc> findPeaks(long[] sums) {
		
		List<PeakLoc> ret = findPeaks(sums,3);
		
		return ret;
	}
	
	private void updateNetworkTables(){
		
	}
	


	private void saveProcessedImage(Mat m) {
		//if (m_pt.get(Parameters.SaveProcessed.ordinal()) > 0.5) {
		if (Double.parseDouble(theProperties.getProperty("SaveProcessed", "0.0")) > 0.5) {
			Date now = new Date();
			if (now.getTime() - m_lastSnapshotTime.getTime() >= Double.parseDouble(theProperties.getProperty("MilliSecsBetweenPics"))) {
				String fileName = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS").format(now);
				Imgcodecs.imwrite("C:/StrongholdImages/Camera_" + fileName + ".jpg", m);
				m_lastSnapshotTime = new Date();
			}
		}
	}
	
	private void saveRawImage(Mat m) {
		if (Double.parseDouble(theProperties.getProperty("SaveRaw", "0.0")) > 0.5) {
			Date now = new Date();
			if (now.getTime() - m_lastRawSnapshotTime.getTime() >= 1000) {
				String fileName = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS").format(now);
				Imgcodecs.imwrite("C:/StrongholdRawImages/Camera_" + fileName + ".jpg", m);
				m_lastRawSnapshotTime = new Date();
			}
		}
	}
	
	private void loadParameters() {
		theProperties = new Properties();
		
		theProperties.setProperty("lowHue", "70");
		theProperties.setProperty("highHue", "100");
		theProperties.setProperty("lowSat", "100");
		theProperties.setProperty("highSat", "255");
		theProperties.setProperty("lowVal", "50");
		theProperties.setProperty("highVal", "255");
		theProperties.setProperty("DilateSize", "4.0");
		theProperties.setProperty("DilateKernel", "2");
		theProperties.setProperty("CROSS_HAIR_X", "200");
		theProperties.setProperty("CROSS_HAIR_Y", "100");
		theProperties.setProperty("CROSS_HAIR_SIZE", "30");
		theProperties.setProperty("MilliSecsBetweenPics", "1000");
		theProperties.setProperty("ErodeSize", "4.0");
		theProperties.setProperty("AspectMin", "0.5");
		theProperties.setProperty("AspectMax", "5.0");
		theProperties.setProperty("AreaMin", "1000");
		theProperties.setProperty("AreaMax", "5000");
	}

}
