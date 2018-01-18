
import jep.*;
import org.opencv.core.*;

public class PythonImageProcessor {
	
	Jep jep;
	
	public PythonImageProcessor() {
	
		try(Jep jep = new Jep(false)) {
			   float[] f = new float[] { 1.0f, 2.1f, 3.3f, 4.5f, 5.6f, 6.7f };
			   NDArray<float[]> nd = new NDArray<>(f, 3, 2);
			   jep.set("x", nd);
			   jep.eval("print x");
		
		} catch (JepException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void findPeg(Mat inImg,Mat outImg) {

			try(Jep jep = new Jep(false)) {
			    jep.eval("import cv2");
			    jep.eval("print cv2.__version__");
			    jep.eval("import sys");
			    jep.eval("sys.path.append('C:/Users/Team138/Vision2017')");
			    jep.eval("import vision2017");
		
			    jep.eval("vision2017.filename='C:/Users/Team138/Vision2017/LED Peg/1ftH3ftD0Angle0Brightness.jpg'");
			    jep.eval("vision2017.processFile()");
			    //jep.invoke("vision2017.processFile1","'C:/Users/Team138/Vision2017/LED Peg/1ftH3ftD0Angle0Brightness.jpg'");
			    Object xcenter = jep.getValue("vision2017.xcenter");
			    Object ycenter = jep.getValue("vision2017.ycenter");
			    Object width = jep.getValue("vision2017.width");
			    System.out.println("Xcenter " + xcenter);
			    System.out.println("Ycenter " + ycenter);
			    System.out.println("Width " + width);
			  
			} catch (JepException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
