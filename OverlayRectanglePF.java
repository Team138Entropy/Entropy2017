import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class OverlayRectanglePF extends MayhemCameraExtension {
	// Widget Name
	public static final String NAME = "MAYHEM-Cam 2016-04-22 15:19";
	
	// Random unique identifier for the camera - leave this - otherwise, you will get a warning
	private static final long serialVersionUID = -412351776843654594L;
	
	// Processed Matrix
	Mat procMat;
	
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
	
	// Network tables
	private NetworkTable table;
	
	// Keep track of frame number and processing results
	private long m_frameNumber = 0;
	private double m_centerX = 1000.0;
	
	// Image Results to send back to robot
	// NOTE:  By convention, data in the array is as follows:
	//       imgResults[0] is frameNumber
	//       imgResults[1] is centerX   (1000.0 is a "magic number" meaning no target found)
	private double[] m_imgResults = {0.0, 1000.0};
	
	// Date object for saving images
	private Date m_lastSnapshotTime;
	private Date m_lastRawSnapshotTime;
	//private Date m_lastProcSnapshotTime;
	
	private ParameterFile m_pt;
	
	private String  SmartDashboardPath = "/SmartDashboard/extensions/";
	
	// test Main function to process a folder with images
	public static void main(String[] args) throws FileNotFoundException
	{
		//String inputFolder = "C:/Users/user/TestImages/TechBinder/Input";//"C:/Users/user/TestImages/willsinputimages";
		//String inputFolder = "C:/Users/user/Desktop/UNH";
		String inputFolder = "C:\\Users\\Team138\\Downloads\\2017VisionExample\\Vision Images\\LED Peg";
		
		//String outputFolder = "C:/Users/user/TestImages/TechBinder/Output";//"C:/Users/user/TestImages/willsoutputimages";
		String outputFolder = "C:\\Users\\Team138\\Downloads\\2017VisionExample\\Vision Images\\LED Peg\\output";
		
		File folder = new File(inputFolder);
		File[] listOfFiles = folder.listFiles();
		
		OverlayRectanglePF imageProcessor = new OverlayRectanglePF();
		
		for(File f : listOfFiles)
		{
			Mat ourImage = Imgcodecs.imread(f.getPath());
			System.out.println("File: " + f.getName());
			imageProcessor.processImage(ourImage);
			Imgcodecs.imwrite(outputFolder + "/"+ f.getName()+".png", imageProcessor.outputImage);
			Imgcodecs.imwrite(outputFolder + "/"+ f.getName()+"_Clipped.png", imageProcessor.clippedImage);
			
			System.out.println("Output "+ f.getName());
		}
		imageProcessor.disconnect();
		System.out.println("Done.");
	}
	
	public OverlayRectanglePF() throws FileNotFoundException {
		
		try	{
			table = NetworkTable.getTable("datatable"); // data table of the robot
		}
		catch(Exception ex)	{
			m_error = ex.getMessage();
		}
		
		m_frameNumber = 0;
		m_lastSnapshotTime = new Date();
		m_lastRawSnapshotTime = new Date();
		
		try	{
			m_pt = new ParameterFile(System.getProperty("user.home")+SmartDashboardPath+"parameters.txt", Parameters.class);
		}
		catch(Exception ex)	{
			
		}
	}
	
	Mat outputImage;
	Mat clippedImage;
	protected void processImage(Mat m) {

		//save the raw image
		saveRawImage(m);
		
		// increment the frameNumber
		m_frameNumber++;
		
		this.procMat = m;
		
		if ( m_pt == null ) {
			m_error = "parameter table missing";
			return; 
		}
		Mat step1 = getHSVThreshold(m);
		createRectangle(m, step1);
		outputImage = m;
		saveProcessedImage(outputImage);
		return ;
    }
	
	private Mat getHSVThreshold(Mat m) {

		// clip out the bright lights
//		Mat brightLights = new Mat();
//		Core.inRange(m, 
//				new Scalar(0,200,0), 
//				new Scalar(255,255,255), 
//				brightLights);
//		// invert the bright lights
//		Imgproc.cvtColor(brightLights, brightLights, Imgproc.COLOR_GRAY2BGR);
		
		// invert the brightlights so the lights are black (0) and everything else is white (255)
//		Mat invertcolormatrix= new Mat(brightLights.rows(),brightLights.cols(), brightLights.type(), new Scalar(255,255,255));
//		Core.subtract(invertcolormatrix, brightLights, brightLights);
//		Core.bitwise_and(m, brightLights, m);
	
		Mat blurred = new Mat();
		// blur the image to fill in the holes
		Imgproc.medianBlur(m, blurred, 1);
		
		// convert BGR (RGB) values to HSV values
		Mat hsv = new Mat();
		Imgproc.cvtColor(blurred, hsv, Imgproc.COLOR_BGR2HSV);
		
		Mat inRange = new Mat();
		// determine HSV values that fit within the thresholds to isolate the retro-reflective tape
		Core.inRange(hsv, new Scalar(m_pt.get(Parameters.lowHue.ordinal()), 
				m_pt.get(Parameters.lowSat.ordinal()), 
				m_pt.get(Parameters.lowVal.ordinal())), 
				new Scalar(m_pt.get(Parameters.highHue.ordinal()),
				m_pt.get(Parameters.highSat.ordinal()), 
				m_pt.get(Parameters.highVal.ordinal())), inRange); 
	
//Imgcodecs.imwrite("C:/Users/user/TestImages/TechBinder/Output/" + "1_Post_inRange" + ".png", m);
		
		// make the goal's lines "thicker" in order to smooth out jagged edges
		Mat dilated = new Mat();
		Mat dilateKernel = Imgproc.getStructuringElement(Imgproc.MORPH_DILATE,  
				new Size(m_pt.get(Parameters.DilateSize.ordinal()), m_pt.get(Parameters.DilateSize.ordinal())));
		Imgproc.dilate(inRange, dilated, dilateKernel, new Point(-1,-1), (int)m_pt.get(Parameters.DilateKernel.ordinal()));
		
//Imgcodecs.imwrite("C:/Users/user/TestImages/TechBinder/Output/" + "2_Post_dilate" + ".png", m);

		// erode the goal back down a bit
		Mat eroded = new Mat();
		Mat erodeKernel = Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, 
				new Size(m_pt.get(Parameters.ErodeSize.ordinal()), m_pt.get(Parameters.ErodeSize.ordinal())));
		Imgproc.erode(dilated, eroded, erodeKernel, new Point(-1,-1), 2);

//Imgcodecs.imwrite("C:/Users/user/TestImages/TechBinder/Output/" + "3_Post_erode" + ".png", m);
		//clippedImage = eroded;
		clippedImage = new Mat();
		Imgproc.cvtColor(eroded, clippedImage, Imgproc.COLOR_GRAY2BGR);
//		Imgproc.cvtColor(inRange, clippedImage, Imgproc.COLOR_GRAY2BGR);

		return eroded;
//		return inRange;
	}
	
	private Mat createRectangle(Mat original, Mat m) {
		// create an ArrayList to find the "contours" (aka "shapes") within the image
		Mat heirarchy = new Mat();
		ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(m, contours, heirarchy, Imgproc.RETR_EXTERNAL, 
				Imgproc.CHAIN_APPROX_SIMPLE);

//Imgcodecs.imwrite("C:/Users/user/TestImages/TechBinder/Output/" + "4_Post_findContours" + ".png", m);

		double widestAspect = 0.0;
		double widest = 0.0;
		Rect target = null;
		DecimalFormat df = new DecimalFormat("0.000");
	
		// convert the color to BGR (aka RGB) so we can draw colored rectangles on it
		//Imgproc.cvtColor(m, m, Imgproc.COLOR_GRAY2BGR);
//		
		// create the edges of the 
		for(MatOfPoint mop : contours){
			Rect rect = Imgproc.boundingRect(mop);
			
			Imgproc.rectangle(original, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar(0, 0, 255), 1); 
					
			double aspectRatio = (double) rect.width / rect.height;
		
			double area = rect.width * rect.height;
			double minAspect = m_pt.get(Parameters.AspectMin.ordinal());
			double maxAspect = m_pt.get(Parameters.AspectMax.ordinal());
			double maxArea = m_pt.get(Parameters.AreaMax.ordinal());
			double minArea = m_pt.get(Parameters.AreaMin.ordinal());
			double minHeight = m_pt.get(Parameters.HeightMin.ordinal());//25.0;
			
			if (aspectRatio > minAspect && 
				aspectRatio < maxAspect && 
				area < maxArea && 
				area > minArea &&
				rect.height >= minHeight ) 
			{
				if( rect.width > widest)
				{
					widest = rect.width;
					widestAspect = aspectRatio;
					target = rect;
				}
				Imgproc.rectangle(original, new Point(rect.x, rect.y), new Point(rect.x+rect.width, rect.y+rect.height), new Scalar (255, 0, 0), 5);
			}
		}
//Imgcodecs.imwrite("C:/Users/user/TestImages/TechBinder/Output/" + "5_Post_rectangles" + ".png", m);
		
		if ( target != null) {
			// found a target; determine the "centerX" value
			m_centerX = target.x + target.width/2 - m_pt.get(Parameters.CROSS_HAIR_X.ordinal());
			
			// write processing results onto image for display on the gui
			Imgproc.putText(original, "Width: " + target.width + ", Height: " + target.height + "", new Point(0, 210), 1, 1, new Scalar (0, 255, 0));
			Imgproc.putText(original, "Aspect Ratio: " + df.format(widestAspect), new Point (0, 230), 1, 1, new Scalar (0, 255, 0));

			Imgproc.rectangle(original, new Point(target.x,target.y), new Point(target.x+target.width,target.y+target.height), new Scalar(0, 255, 0), 1);
			Imgproc.line(original, new Point(target.x+target.width/2,target.y+target.height/2), new Point(target.x+target.width/2,target.y+target.height/2), new Scalar(0, 255, 0));
			
		} else {
			// no target found, set centerX to the "magic number" of 1000 to indicate no target found
			m_centerX = 1000;  
		}
		
		// send back the processing results in a NumberArray so that the frame and centerX values stay together
		m_imgResults[0] = m_frameNumber;
		m_imgResults[1] = m_centerX;
		table.putNumberArray("ImgResults", m_imgResults);
		
		// create the cross hairs on the image from parameters
		double crossX = m_pt.get(Parameters.CROSS_HAIR_X.ordinal());
		double crossY = m_pt.get(Parameters.CROSS_HAIR_Y.ordinal());
		double crossSize = m_pt.get(Parameters.CROSS_HAIR_SIZE.ordinal());
		
		// draw the cross hairs on the image
		Imgproc.line(original, new Point(crossX-crossSize, crossY), 
				new Point(crossX+crossSize, crossY), new Scalar(0, 255, 0));
		Imgproc.line(original, new Point(crossX, crossY-crossSize), 
				new Point(crossX, crossY+crossSize), new Scalar(0, 255, 0));
//Imgcodecs.imwrite("C:/Users/user/TestImages/TechBinder/Output/" + "6_Post_final" + ".png", m);
		
		saveProcessedImage(original);
		return original;
	}

	private void saveProcessedImage(Mat m) {
		if (m_pt.get(Parameters.SaveProcessed.ordinal()) > 0.5) {
			Date now = new Date();
			if (now.getTime() - m_lastSnapshotTime.getTime() >= m_pt.get(Parameters.MilliSecsBetweenPics.ordinal())) {
				String fileName = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS").format(now);
				Imgcodecs.imwrite("C:/StrongholdImages/Camera_" + fileName + ".jpg", m);
				m_lastSnapshotTime = new Date();
			}
		}
	}
	
	private void saveRawImage(Mat m) {
		if (m_pt.get(Parameters.SaveRaw.ordinal()) > 0.5) {
			Date now = new Date();
			if (now.getTime() - m_lastRawSnapshotTime.getTime() >= 1000) {
				String fileName = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS").format(now);
				Imgcodecs.imwrite("C:/StrongholdRawImages/Camera_" + fileName + ".jpg", m);
				m_lastRawSnapshotTime = new Date();
			}
		}
	}

}
