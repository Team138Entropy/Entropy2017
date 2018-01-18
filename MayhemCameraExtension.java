/*
 * Export as a JAR file.
 * Put the jar file in:
 * C:/Users/user/SmartDashboard/extensions
 * 
 */
import edu.wpi.first.smartdashboard.gui.StaticWidget;
import edu.wpi.first.smartdashboard.properties.IntegerProperty;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.properties.StringProperty;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import org.jfree.chart.encoders.ImageFormat;
import org.opencv.core.*;
import java.awt.image.*;

public class MayhemCameraExtension extends StaticWidget {

	//static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
	//static {System.loadLibrary("C:\\Users\\user\\SmartDashboard\\extensions\\opencv-310.jar");}
	//static {System.loadLibrary("opencv_java310");}
	//static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
	//static {System.load("opencv_java310");}
	//static {System.load("C:/Users/user/SmartDashboard/extensions/opencv_java310.dll");}
	//static {System.load("C:/Users/user/SmartDashboard/extensions/opencv_java310.dll");}
	

	static
	{
		// RJD: this seems to load the opencv library
		//System.load("C:/streeter/workspace/SimpleJavaCVProject/lib/x64/opencv_java310.dll");
		//System.load(System.getProperty("user.home")+"/SmartDashboard/extensions/opencv_java310.dll");
		System.load("C:\\opencv\\build\\java\\x64\\opencv_java310.dll");
		
	}

/**
	 * 
	 */
	private static final long serialVersionUID = 2417738228140338679L;

    public static final String NAME = "MAYHEM-Cam-Base 2016-04-22 13:53";

    private static final int[] START_BYTES = new int[]{0xFF, 0xD8};
    private static final int[] END_BYTES = new int[]{0xFF, 0xD9};

    private boolean ipChanged = true;
    private String ipString = null;
    private double rotateAngleRad = 0;
    private long lastFPSCheck = 0;
    private int lastFPS = 0;
    private int fpsCounter = 0;
    public class BGThread extends Thread {

        boolean destroyed = false;

        public BGThread() {
            super("Camera Viewer Background");
        }

        long lastRepaint = 0;
        @Override
        public void run() {
            URLConnection connection = null;
            InputStream stream = null;
            ByteArrayOutputStream imageBuffer = new ByteArrayOutputStream();
            while (!destroyed) {
                try{
                    System.out.println("Connecting to camera");
                    ipChanged = false;
                    URL url = new URL("http://"+ipString+"/mjpg/video.mjpg");
                    connection = url.openConnection();
                    connection.setReadTimeout(250);
                    stream = connection.getInputStream();

                    while(!destroyed && !ipChanged){
                        while(System.currentTimeMillis()-lastRepaint<10){
                            stream.skip(stream.available());
                            Thread.sleep(1);
                        }
                        stream.skip(stream.available());

                        imageBuffer.reset();
                        for(int i = 0; i<START_BYTES.length;){
                            int b = stream.read();
                            if(b==START_BYTES[i])
                                i++;
                            else
                                i = 0;
                        }
                        for(int i = 0; i<START_BYTES.length;++i)
                            imageBuffer.write(START_BYTES[i]);

                        for(int i = 0; i<END_BYTES.length;){
                            int b = stream.read();
                            imageBuffer.write(b);
                            if(b==END_BYTES[i])
                                i++;
                            else
                                i = 0;
                        }

                        fpsCounter++;
                        if(System.currentTimeMillis()-lastFPSCheck>500){
                            lastFPSCheck = System.currentTimeMillis();
                            lastFPS = fpsCounter*2;
                            fpsCounter = 0;
                        }

                        lastRepaint = System.currentTimeMillis();
                        ByteArrayInputStream tmpStream = new ByteArrayInputStream(imageBuffer.toByteArray());
                        imageToDraw = ImageIO.read(tmpStream);
                        System.out.println(System.currentTimeMillis()-lastRepaint);
                        repaint();
                    }

                } catch(Exception e){
                    imageToDraw = null;
                    repaint();
                    e.printStackTrace();
                }

                if(!ipChanged){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {}
                }
            }

        }

        @Override
        public void destroy() {
            destroyed = true;
        }
    }
    private BufferedImage imageToDraw;
    private BGThread bgThread = new BGThread();
    public final StringProperty ipProperty = new StringProperty(this, "Camera IP Address or mDNS name", "10.15.19.11");
    public final IntegerProperty rotateProperty = new IntegerProperty(this, "Degrees Rotation", 0);
    
    @Override
    public void init() {
        setPreferredSize(new Dimension(300, 300));
        ipString = ipProperty.getSaveValue();
        rotateAngleRad = Math.toRadians(rotateProperty.getValue());
        bgThread.start();
        revalidate();
        repaint();
    }

    @Override
    public void propertyChanged(Property property) {
        if (property == ipProperty) {
            ipString = ipProperty.getSaveValue();
            ipChanged = true;
        }
        if (property == rotateProperty) {
            rotateAngleRad = Math.toRadians(rotateProperty.getValue());
        }

    }

    @Override
    public void disconnect() {
        bgThread.destroy();
        super.disconnect();
    }

    private int m_noConnectionCount = 0;
    protected String m_error = "";
    
    @Override
    protected void paintComponent(Graphics g) {
        BufferedImage drawnImage = imageToDraw; 
        try
        {
	        if (drawnImage != null) {
	        	// cast the Graphics context into a Graphics2D
	            Graphics2D g2d = (Graphics2D)g;
	            
	            // get the existing Graphics transform and copy it so that we can perform scaling and rotation
	            AffineTransform origXform = g2d.getTransform();
	            AffineTransform newXform = (AffineTransform)(origXform.clone());
	            
	            // find the center of the original image
	            int origImageWidth = drawnImage.getWidth();
	            int origImageHeight = drawnImage.getHeight();
	            int imageCenterX = origImageWidth/2;
	            int imageCenterY = origImageHeight/2;
	            
	            // perform the desired scaling
	            double panelWidth = getBounds().width;
	            double panelHeight = getBounds().height;
	            double panelCenterX = panelWidth/2.0;
	            double panelCenterY = panelHeight/2.0;
	            double rotatedImageWidth = origImageWidth * Math.abs(Math.cos(rotateAngleRad)) + origImageHeight * Math.abs(Math.sin(rotateAngleRad));
	            double rotatedImageHeight = origImageWidth * Math.abs(Math.sin(rotateAngleRad)) + origImageHeight * Math.abs(Math.cos(rotateAngleRad));         
	            		
	            // compute scaling needed
	            double scale = Math.min(panelWidth / rotatedImageWidth, panelHeight / rotatedImageHeight);
	                      
	            // set the transform before drawing the image
	            // 1 - translate the origin to the center of the panel
	            // 2 - perform the desired rotation (rotation will be about origin)
	            // 3 - perform the desired scaling (will scale centered about origin)
	            newXform.translate(panelCenterX,  panelCenterY);
	            newXform.rotate(rotateAngleRad);
	            newXform.scale(scale, scale);
	            g2d.setTransform(newXform);
	
	            Mat m = new Mat(); 
	            m = bufferedImageToMat(drawnImage);
	            processImage(m);
	            drawnImage = matToBufferedImage(m);
	            
	            // draw image so that the center of the image is at the "origin"; the transform will take care of the rotation and scaling
	            g2d.drawImage(drawnImage, -imageCenterX, -imageCenterY, null);
	            
	            // restore the original transform
	            g2d.setTransform(origXform);
	            
	            g.setColor(Color.PINK);
	            g.drawString("FPS: "+lastFPS, 10, 10);
	        } else {
	            g.setColor(Color.PINK);
	            g.fillRect(0, 0, getBounds().width, getBounds().height);
	            g.setColor(Color.BLACK);
	            g.drawString("NO CONNECTION " + m_noConnectionCount++, 10, 10);
	        }
	        if( m_error.length() > 0 )
	        {
	        	g.drawString("Error" + m_error, 0, 80);
	        }
        }
        catch(Exception e)
        {
            g.setColor(Color.YELLOW);
            g.fillRect(0, 0, getBounds().width, getBounds().height);
            g.setColor(Color.BLACK);
            g.drawString("EXCEPTION: " + e.getMessage(), 10, 10);     
        }
   
    }
    
    /**
     * Pass in an openCV Matrix, get out the processed openCV Matrix.
     * Override this method to derive classes from this.
     * @param m
     */
    protected void processImage(Mat m)
    {
    
    }
    
	public Mat bufferedImageToMat(BufferedImage bi) 
	{
		Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
		byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		mat.put(0, 0, data);
		return mat;
		
//		return new Mat();
	}

    public BufferedImage matToBufferedImage(Mat m)
    {
    	// Create an empty image in matching format
    	BufferedImage buf = new BufferedImage(m.width(), m.height(), BufferedImage.TYPE_3BYTE_BGR);

    	// Get the BufferedImage's backing array and copy the pixels directly into it
    	byte[] data = ((DataBufferByte) buf.getRaster().getDataBuffer()).getData();
    	m.get(0, 0, data);   
    	
    	return buf;
    	
    	//return new BufferedImage(1,2, BufferedImage.TYPE_3BYTE_BGR);
    }
}



