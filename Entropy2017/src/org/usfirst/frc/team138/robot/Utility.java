package org.usfirst.frc.team138.robot;
/*
 * Useful routines
 */
public class Utility {
	public static double angleWrap(double angle) {
		// wrap angle to range +/-180 degrees
		double result;
		result=(angle % 360); // Modulo operator, result in range +/-360
		if (result<-180)
			result+=360;
		if (result > 180)
			result-=360;
		// result in range +/-180
		return result;
	}
	
	public static double diffAngles(double angle1, double angle2) {
		// returns unwrapped difference between two wrapped angles
		// angles are assumed to wrap at +/-180 degree boundary
		// result = angle1-angle2
		double result=0;
		result=angle1-angle2;
		if (result<-180) result+=360;
		if (result>180) result-=360;
		return result;
	}
}
