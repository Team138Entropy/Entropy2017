import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Entropy2017CameraExtension extends MayhemCameraExtension {
	// Widget Name
	public static final String NAME = "Entropy-Cam 2017-02-08";
	
	// Random unique identifier for the camera - leave this - otherwise, you will get a warning
	private static final long serialVersionUID = -412351776843654999L;
	
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
	
	// Python image processor
	PythonImageProcessor ip;
	
	// test Main function to process a folder with images
	public static void main(String[] args) throws FileNotFoundException
	{
		
		String inputFolder = "C:/Users/Team138/Vision2017/LED Peg";
		
		
		String outputFolder = "C:/Users/Team138/Vision2017/LED Peg";
		
		File folder = new File(inputFolder);
		File[] listOfFiles = folder.listFiles();
		
		Entropy2017CameraExtension imageProcessor = new Entropy2017CameraExtension();
		
		for(File f : listOfFiles)
		{
			Mat ourImage = Imgcodecs.imread(f.getPath());
			System.out.println("File: " + f.getName());
			imageProcessor.processImage(ourImage);
			Imgcodecs.imwrite(outputFolder + "/output/"+ f.getName()+".png", imageProcessor.outputImage);
			Imgcodecs.imwrite(outputFolder + "/output/"+ f.getName()+"_Clipped.png", imageProcessor.clippedImage);
			
			System.out.println("Output "+ f.getName());
		}
		imageProcessor.disconnect();
		System.out.println("Done.");
	}
	
	
	
	public Entropy2017CameraExtension() throws FileNotFoundException {
		
		try	{
			table = NetworkTable.getTable("datatable"); // data table of the robot
		}
		catch(Exception ex)	{
			m_error = ex.getMessage();
		}
		
		ip = new PythonImageProcessor();
		
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
		//saveRawImage(m);
		
		// increment the frameNumber
		m_frameNumber++;
		outputImage = m;
		clippedImage = m;
		
		this.procMat = m;
		
		ip.findPeg(m, m);
		
		if ( m_pt == null ) {
			m_error = "parameter table missing";
			return; 
		}
		Mat step1 = getHSVThreshold(m);
		
		// TODO: Put Python code here
		locatePeg(step1,m);
		
		outputImage = m;
		//saveProcessedImage(outputImage);
		return;
    }
	

	private Mat locatePeg(Mat original, Mat m) {
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
		
		saveProcessedImage(original);
		return original;		
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
	


		Imgproc.cvtColor(inRange, clippedImage, Imgproc.COLOR_GRAY2BGR);

		return inRange;
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
