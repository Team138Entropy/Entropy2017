import java.util.Arrays;
import java.util.*;
import java.io.*;

import java.nio.file.*;

//import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class ParameterFile implements Runnable {
	
	double[] m_Parameterlist;
	Class<? extends Enum<?>> m_E;
	String m_Filename;
	WatchService myWatcher;
	
	public ParameterFile(String filename, Class<? extends Enum<?>> e) throws IOException {
		m_E = e;
		m_Filename = filename;
		int sep = filename.lastIndexOf('/'); // refer to a specifc directory

		Path toWatch = Paths.get(filename.substring(0, sep)); // watch changes that occur 
		
		// make a new watch service that we can register interest in 
        // directories and files with.
        myWatcher = toWatch.getFileSystem().newWatchService();
 
        // start the file watcher thread below
        Thread th = new Thread(this, "FileWatcher");
        th.start();
 
        // register a file
        toWatch.register(myWatcher, ENTRY_MODIFY); 
		
		Load();
	}
	
	private void Load() throws FileNotFoundException
	{
		Map<String, Integer> map = new HashMap<String, Integer>();
		
		String[] list = getNames(m_E);
		m_Parameterlist = new double[list.length];
			
		for(int i = 0; i < list.length; i++)
		{
			System.out.println("Add to Map: " + list[i]);
			map.put(list[i], i);
		}
		
		Scanner sc = new Scanner(new File(m_Filename));
		List<String> lines = new ArrayList<String>();
		while (sc.hasNextLine()) {
		  lines.add(sc.nextLine());
		}
		sc.close();
		
		String[] arr = lines.toArray(new String[0]);
		for(int i = 0; i < arr.length; i++)
		{
//			System.out.println("File:" + arr[i]);
			
			String[] parts = arr[i].split("=");
			if(parts.length < 2 ){
				continue;
			}
			String part1 = parts[0]; 
			String part2 = parts[1]; 
			
//			System.out.println("part1= " + part1);
//			System.out.println("part2= " + part2);
			
			if(map.containsKey(part1.trim()))
			{
				int p=map.get(part1.trim());
				
				//this allows one to add a comment after the declaration of value
				double value = Double.parseDouble(part2.trim().split(" ")[0]);
				m_Parameterlist[p] = value;
				//System.out.println("p:" + p + "=" + value );
			}
			
		}
	}
	public static String[] getNames(Class<? extends Enum<?>> e) {
		// takes enum, converts it to an array of strings
	    return Arrays.toString(e.getEnumConstants()).replaceAll("^.|.$", "").split(", "); 
	}
	 @Override
     public void run() {
         try {
             // get the first event before looping
             WatchKey key = myWatcher.take();
             while(key != null) {
                 // we have a polled event, now we traverse it and 
                 // receive all the states from it
                 for (WatchEvent event : key.pollEvents()) {
                     System.out.printf("Received %s event for file: %s\n",
                                       event.kind(), event.context() );
                     Load();
                 }
                 key.reset();
                 key = myWatcher.take();
             }
         } catch (InterruptedException e) {
             e.printStackTrace();
         } catch (FileNotFoundException e) {
			e.printStackTrace();
		}
         System.out.println("Stopping thread");
     }
	public double get(int p) {
		return m_Parameterlist[p];
	}	 
	enum testparameters
	{
		lowHue,
		highHue,
		lowSat,
		highSat,
		highVal,
		lowVal,
		dilateSize,
		DilateKernel,
		CROSS_HAIR_X,
		CROSS_HAIR_Y,
		CROSS_HAIR_SIZE,
		MilliSecsBetweenPics,//1000
		ErodeSize,//4.0
		AspectMin,//1.0
		AspectMax,//5.0
		AreaMin,//3000
		AreaMax, //5000
		MorphKernelX,
		MorphKernelY,
		ApproxPolyDPEpsilon,
		contourIdx,
		SaveRaw,
		SaveProcessed
	};
	private static String  SmartDashboardPath = "/SmartDashboard/extensions/";

	public static void main(String[] arg) {
		 try {
			ParameterFile pt = new ParameterFile (System.getProperty("user.home")+ SmartDashboardPath +"parameters.txt", testparameters.class);
			
			Thread.sleep(90000);
			
			System.out.println("The value of lowHue is " + pt.get(testparameters.lowHue.ordinal()));
		 }
		 catch(Exception ex) {
			 ex.printStackTrace();
		 }
	}


}