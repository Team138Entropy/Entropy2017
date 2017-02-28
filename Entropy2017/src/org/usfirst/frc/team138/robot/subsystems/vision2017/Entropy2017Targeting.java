package org.usfirst.frc.team138.robot.subsystems.vision2017;

import org.opencv.imgproc.Imgproc;
import org.usfirst.frc.team138.robot.Sensors;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;

/**
 * Vision processing thread to look for gear placement pegs and high goal targets in the
 * FIRST Robotics 2017 competition, Steamworks
 * @author Team 138 Entropy
 *
 */
public class Entropy2017Targeting extends Thread {
	// DEBUG_ENABLED creates a debug stream on the SmartDashboard that draws the target, lagging for 12 frames
	public static final boolean DEBUG_ENABLED = false;
	
	// Constants to find correction angle
	private static final double pixelsPerDegree = 17.0;
	private static final double cameraOffsetInches = -5.1;
	private static final double shooterOffsetInches = 12;
	private static final double pegGapInches = 6.25;
	private static final double pegWidthInches = 10.25;
	private static final double pegHeightInches = 5;
	private static final double highGoalHeightInches = 10;
	private static final double highGoalGapInches = 2;
	private static final double highGoalWidthInches = 10;
	
	// Path for testing images from PC
	private static String rootFolder = "C:\\Users\\Team138\\Vision2017";
	
	// I don't know what these are necessary for but I'll leave them in anyway
	private Date m_lastSnapshotTime;
	private Date m_lastRawSnapshotTime;
	@SuppressWarnings("unused")
	private static final long serialVersionUID = -412351776843653585L; //Unique identifier; Is it needed?
	
	// Camera Pointers
	static UsbCamera gearCamera;
	static UsbCamera ropeAndShooterCamera;

	// Processing Variables
	private Properties theProperties;
	private ArrayList<TargetInformation> infoList = new ArrayList<TargetInformation>();
	private CvSink pegSink;
    private CvSink shooterSink;
	private int framesToProcess = 0;
	private boolean processingForPeg = true;
	private boolean cancelled = false;
	private boolean done = false;
	private Point lastKnownTarget = new Point();
	private int frameLag = 0;
	
	/**
	 * Vision processing thread to look for gear placement pegs and high goal targets
	 * @param gearCam previously created gear camera
	 * @param ropeAndShooterCam previously created rope and shooter camera
	 */
	public Entropy2017Targeting(UsbCamera gearCam, UsbCamera ropeAndShooterCam) {
		gearCamera = gearCam;
		ropeAndShooterCamera = ropeAndShooterCam;
		
		if (gearCam != null && ropeAndShooterCam != null)
		{
			pegSink = CameraServer.getInstance().getVideo(gearCamera);
	        shooterSink = CameraServer.getInstance().getVideo(ropeAndShooterCamera);
		}
		m_lastSnapshotTime = new Date();
		m_lastRawSnapshotTime = new Date();
		
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
	
	public void run() {
		if (pegSink == null || shooterSink == null)
		{
			return;
		}
		if (DEBUG_ENABLED)
		{
			Sensors.turnOnCameraLight(true);
			Sensors.turnOnCameraLight(false);
		}
		CvSource debugStream = CameraServer.getInstance().putVideo("Debug Stream", 640, 480);
        Mat frame = new Mat();
        
        while(!done) {
        	if (!DEBUG_ENABLED)
        	{
        		synchronized(this)
            	{
            		try {
    					this.wait();
    				} catch (InterruptedException e) {}
    	        	try {
    					this.wait(40);
    				} catch (InterruptedException e) {}
            	}
            	
            	while (framesToProcess > 0)
            	{
            		try
            		{
            			if (!cancelled)
                		{
                			if (processingForPeg)
                    		{
                    			pegSink.grabFrame(frame);
                    		}
                    		else
                    		{
                    			shooterSink.grabFrame(frame);
                    		}
                            processImage(frame);
                            System.out.println("Frame: " + framesToProcess);
                            framesToProcess--;
                		}
                		else
                		{
                			framesToProcess = 0;
                			getTargetInformation();
                			cancelled = false;
                		}
            		}
            		catch (Exception e)
            		{
            			System.out.println("failed somewhere in processing");
            		}
            	}
            	Sensors.standardCameraMode(processingForPeg);
            	Sensors.turnOffCameraLight(processingForPeg);
        	}
        	else
        	{
        		if (processingForPeg)
        		{
        			pegSink.grabFrame(frame);
        		}
        		else
        		{
        			shooterSink.grabFrame(frame);
        		}
        		if (framesToProcess > 0)
            	{
            		if (!cancelled)
            		{
                        processImage(frame);
                        framesToProcess--;
            		}
            		else
            		{
            			framesToProcess = 0;
            			getTargetInformation();
            			cancelled = false;
            		}
            	}
        		if (frameLag > 0)
        		{
        			frameLag--;
        			drawTarget(frame, (long)lastKnownTarget.x, (long)lastKnownTarget.y);
        			if (frameLag == 0)
            		{
            			Sensors.standardCameraMode(processingForPeg);
            		}
        		}
        		debugStream.putFrame(frame);
        	}
        }
	}
	
	public void shutdownThread()
	{
		done = true;
		if (!DEBUG_ENABLED)
		{
			this.notify();
		}
	}
	
	public ArrayList<TargetInformation> getTargetInformation()
	{
		@SuppressWarnings("unchecked")
		ArrayList<TargetInformation> ret = (ArrayList<TargetInformation>) infoList.clone();
		infoList.clear();
		return ret;
	}
	
	/**
	 * Process the specified number of frames for a target
	 * @param numFrames number of frames to process
	 * @param targetingPeg true to look for peg target, false to look for high goal target
	 */
	public void processFrames(int numFrames, boolean targetingPeg)
	{
		Sensors.targetingCameraMode(targetingPeg);
		Sensors.turnOnCameraLight(targetingPeg);
		framesToProcess = numFrames;
		processingForPeg = targetingPeg;
		if (!DEBUG_ENABLED)
		{
			synchronized(this)
			{
				this.notify();
			}
		}
	}
	
	public void cancelProcessing()
	{
		if (framesToProcess > 0)
		{
			cancelled = true;
		}
	}
	
	/**
	 * Test main function to find targets from root folder
	 * @param args unused
	 */
	public static void main(String[] args)
	{
		String inputFolder = rootFolder + "\\LED Peg";
		String outputFolder = rootFolder + "\\LEDPeg_output";
				
		File folder = new File(inputFolder);
		File[] listOfFiles = folder.listFiles();
		
		Entropy2017Targeting imageProcessor = new Entropy2017Targeting(null, null);
		imageProcessor.processingForPeg = true;
		
		for(File f : listOfFiles)
		{
			System.out.println();
			System.out.println("---------------------------------------------------------------------------------------------");
			System.out.println();
			System.out.println("File: " + f.getPath());
			
			Mat ourImage = Imgcodecs.imread(f.getPath());
			imageProcessor.processImage(ourImage);
			Imgcodecs.imwrite(outputFolder + "\\"+ f.getName()+".png", ourImage);
			
			System.out.println("Completed file.");
		}
		System.out.println("Processed all images.");
	}

	/**
	 * Processes an image for either peg or high goal targets
	 * @param image
	 */
	private void processImage(Mat image) {
		TargetInformation targetInfo;
		
		Mat cleanedImage = getHSVThreshold(image);
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		//vvvvvvvvvvvvvvvvvvv FUTURE YEARS LOOK HERE, THIS IS WHAT YOU WILL WANT TO REPLACE vvvvvvvvvvvvvvvvvvv//
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		if (processingForPeg)
		{
			targetInfo = findPeg(cleanedImage);
		}
		else
		{
			targetInfo = findHighGoal(cleanedImage);
		}
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		//^^^^^^^^^^^^^^^^^^^ FUTURE YEARS LOOK HERE, THIS IS WHAT YOU WILL WANT TO REPLACE ^^^^^^^^^^^^^^^^^^^//
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// If debug mode is enabled, the target will be drawn on the 
		// image for 12 frames at the last known position
		if (targetInfo.targetFound)
		{
			lastKnownTarget.x = (double)targetInfo.aimX;
			lastKnownTarget.y = (double)targetInfo.y;
			frameLag = 12;
		}
		
		infoList.add(targetInfo);
		return;
    }

	/**
	 * Process pixels in the correct color range and cleanup the image
	 * @param image image to clean
	 * @return cleaned up image
	 */
	private Mat getHSVThreshold(Mat image) {
		
		// convert BGR values to HSV values
		Mat hsv = new Mat();
		Imgproc.cvtColor(image, hsv, Imgproc.COLOR_BGR2HSV);
		
		
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
		Imgproc.cvtColor(image, grey, Imgproc.COLOR_BGR2GRAY);
		Core.bitwise_and(grey, inRange, grey);

        Imgcodecs.imwrite(rootFolder + "/1_Post_inRange" + ".png", grey);
		
		return grey;
	}
	
	/**
	 * Locate the peg target in an image, if any
	 * @param image input image
	 * @return all information about target, or returns blank TargetInformation if no targets found
	 */
	private TargetInformation findPeg(Mat image) {
		TargetInformation ret = new TargetInformation();
		ret.targetingPeg = true;
		
	    long[] xsums = sums(image, true);
	    long[] ysums = sums(image, false);
	    
	    List<PeakLoc> ypeaks = findPeaks(ysums);
	    List<PeakLoc> xpeaks = findPeaks(xsums);
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		//vvvvvvvvvvvvvvvvvvv FUTURE YEARS LOOK HERE, THIS IS WHAT YOU WILL WANT TO REPLACE vvvvvvvvvvvvvvvvvvv//
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
	    
	    // Target is only found if we have exactly 2 x peaks, representing both of the pieces of tape
	    if ((xpeaks.size() == 2) && (ypeaks.size() > 0)){
	    	ret.targetFound = true;
	    	ret.x = (xpeaks.get(1).getStart() + xpeaks.get(0).getStop()) / 2;
	    	ret.gap = xpeaks.get(1).getStart() - xpeaks.get(0).getStop();
	    	ret.width = xpeaks.get(1).getStop() - xpeaks.get(0).getStart();
	    	ret.height = ypeaks.get(0).getStop() - ypeaks.get(0).getStart();
	    	ret.y = ypeaks.get(0).getStart() + ret.height/2;
	    	
    		double pixelsPerInch = ret.gap / pegGapInches;
	    	if (xpeaks.get(0).isTruePeak() && xpeaks.get(1).isTruePeak())
	    	{
	    		pixelsPerInch = (pixelsPerInch + ret.width / pegWidthInches) / 2;
	    	}
	    	if (ypeaks.get(0).isTruePeak())
	    	{
	    		pixelsPerInch = (pixelsPerInch + ret.width / pegHeightInches) / 2;
	    	}	    	
	    	ret.aimX = ret.x + cameraOffsetInches * pixelsPerInch;
	    	
	    	ret.correctionAngle = (double)((ret.aimX - image.cols() / 2)) / pixelsPerDegree;
	    }
	    else
	    {
	    	ret.targetFound = false;
	    }
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		//^^^^^^^^^^^^^^^^^^^ FUTURE YEARS LOOK HERE, THIS IS WHAT YOU WILL WANT TO REPLACE ^^^^^^^^^^^^^^^^^^^//
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
	    
	    return ret;
	}
	
	/**
	 * Locate the high goal in an image, if any
	 * @param image input image
	 * @return all information about target, or returns blank TargetInformation if no targets found
	 */
	private TargetInformation findHighGoal(Mat image) {
		TargetInformation ret = new TargetInformation();
		ret.targetingPeg = false;
		
        long[] xsums = sums(image, true);
        long[] ysums = sums(image, false);
        
	    List<PeakLoc> xpeaks = findPeaks(xsums, 10);
	    List<PeakLoc> ypeaks = findPeaks(ysums, 10);

	    if (ypeaks.size() == 2 && xpeaks.size() > 0) {
	    	ret.targetFound = true;
	    	ret.x = (xpeaks.get(0).getStop() - xpeaks.get(0).getStart()) / 2;
	    	ret.gap = ypeaks.get(1).getStart() - ypeaks.get(0).getStop();
	    	ret.width = xpeaks.get(0).getStop() - xpeaks.get(0).getStart();
	    	ret.height = ypeaks.get(1).getStop() - ypeaks.get(0).getStart();
	    	ret.y = (ypeaks.get(0).getStop() + ypeaks.get(1).getStart())/2;
	    	
	    	double pixelsPerInch = ret.gap / highGoalGapInches;
	    	if (xpeaks.get(0).isTruePeak() && xpeaks.get(1).isTruePeak())
	    	{
	    		pixelsPerInch = (pixelsPerInch + ret.width / highGoalWidthInches) / 2;
	    	}
	    	if (ypeaks.get(0).isTruePeak())
	    	{
	    		if (xpeaks.get(0).isTruePeak() && xpeaks.get(1).isTruePeak())
	    		{
	    			pixelsPerInch = (pixelsPerInch * 2 + ret.width / highGoalHeightInches) / 3;
	    		}
	    		else
	    		{
	    			pixelsPerInch = (pixelsPerInch + ret.width / highGoalHeightInches) / 2;
	    		}
	    	} 	
	    	
	    	ret.aimX = ret.x + (cameraOffsetInches - shooterOffsetInches) * pixelsPerInch;
	    	
	    	ret.correctionAngle = (double)((ret.aimX - image.cols() / 2)) / pixelsPerDegree;
	    }
	    else
	    {
	    	ret.targetFound = false;
	    }
	    
	    return ret;
	}
	
	/**
	 * Draws a reticle on the image at the specified coordinates
	 * @param image canvas image to draw on
	 * @param x target x coordinate
	 * @param y target y coordinate
	 */
	private void drawTarget(Mat image, long x, long y){
		
		int size = 40;
		Scalar color;
		color = new Scalar(0,0,255);
		
		Imgproc.line(image, new Point(x-size,y), new Point(x+size,y), color,2);
		Imgproc.line(image, new Point(x,y-size), new Point(x,y+size), color,2);
		for (int k =0;k<4;k++) {
			Imgproc.circle(image, new Point(x,y), (k+1)*size/4, color);
		}
		
	}
	
	/**
	 * Sum the rows or columns of a matrix
	 * @param m input 2D matrix as unsigned bytes
	 * @param byRow true sums the matrix by row; otherwise by columns
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
		
		int total1 = 0;
		for (int k=0; k < retSums.length; k++) {
			total1 += retSums[k];
		}
	
		return retSums;
	}
	
	/**
	 * Peak information storage struct
	 */
	private static class PeakLoc {
		private long start;
		private long stop;
		private boolean truePeak;
		public PeakLoc(long start, long stop, boolean truePeak) {
			super();
			this.start = start;
			this.stop = stop;
			this.truePeak = truePeak;
		}
		public long getStart() {
			return start;
		}
		public long getStop() {
			return stop;
		}		
		public boolean isTruePeak() {
			return this.truePeak;
		}
		
		@SuppressWarnings("unused")
		public void print() {
			System.out.println("Peak Range: " + this.getStart() + " - " + this.getStop());
		}
	}
	
	/**
	 * Finds value spikes in an array 
	 * @param sums the array of values
	 * @param minWidth the minimum number of pixels wide the spike needs to be to be considered a peak
	 * @return a list of peak information
	 */
	private static List<PeakLoc> findPeaks(long[] sums,long minWidth) {
		long maxVal = Arrays.stream(sums).max().getAsLong();
		ArrayList<PeakLoc> ret = new ArrayList<PeakLoc>();
		boolean looking = true;
		long start = 0;
		
		for (int k=0; k < sums.length;k++) {
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
					    ret.add(new PeakLoc(start, k-1, start == 0));
					}
				}
			}
			
		}
		if (looking == false){
			ret.add(new PeakLoc(start, sums.length - 1, false));
		}
		
		return ret;
	}
	
	/**
	 * Finds value spikes in an array, assumes a minimum of 3 pixels wide for a spike to be considered a peak
	 * @param sums the array of values
	 * @return a list of peak information
	 */
	private static List<PeakLoc> findPeaks(long[] sums) {
		return findPeaks(sums, 3);
	}
	
	/**
	 * Container struct for all target information including position, which target type,
	 * if the target was found, and how much the robot needs to rotate to center the target
	 */
	public static class TargetInformation
	{
		public boolean targetingPeg = true; //false targets high goal
		public boolean targetFound = false;
		public long x = 0; //pixels from left of image
		public long y = 0; //pixels from top of image
		public long height = 0; //height of target in pixels
		public long width = 0; //width of target in pixels
		public long gap = 0; //gap between peaks in pixels
		public double aimX = 0; //xPos to center in camera
		public double correctionAngle = 0; //how many degrees the robot needs to rotate to center target
		
		public void add(TargetInformation other)
		{
			if (this.targetingPeg == other.targetingPeg)
			{
				this.x += other.x;
				this.y += other.y;
				this.height += other.y;
				this.width += other.width;
				this.gap += other.gap;
				this.aimX += other.aimX;
				this.correctionAngle += other.correctionAngle;
			}
		}
	}

	@SuppressWarnings("unused")
	private void saveProcessedImage(Mat m) {
		if (Double.parseDouble(theProperties.getProperty("SaveProcessed", "0.0")) > 0.5) {
			Date now = new Date();
			if (now.getTime() - m_lastSnapshotTime.getTime() >= Double.parseDouble(theProperties.getProperty("MilliSecsBetweenPics"))) {
				String fileName = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS").format(now);
				Imgcodecs.imwrite("C:/StrongholdImages/Camera_" + fileName + ".jpg", m);
				m_lastSnapshotTime = new Date();
			}
		}
	}
	
	@SuppressWarnings("unused")
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

}
