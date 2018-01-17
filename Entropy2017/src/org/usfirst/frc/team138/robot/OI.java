package org.usfirst.frc.team138.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
//import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team138.robot.commands.*;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public final class OI {
    static Joystick driverStick = new Joystick(0);
    static Joystick operatorStick = new Joystick(1);
    
    static Button toggleGearRamButton 			= new JoystickButton(operatorStick, 1);
    static Button toggleRopeGrabberButton 		= new JoystickButton(operatorStick, 2);
    static Button chuteAcquireButton			= new JoystickButton(operatorStick, 3);
    static Button floorAcquireButton	 		= new JoystickButton(operatorStick, 4);
    static Button toggleWristButton 			= new JoystickButton(operatorStick, 5);
    static Button toggleClawButton 			= new JoystickButton(operatorStick, 6);
    static Button shootButton 					= new JoystickButton(operatorStick, 7);
    static Button zeroTurn                     = new JoystickButton(operatorStick, 8);
    static Button autoPositionShooterButton 	= new JoystickButton(operatorStick, 9);
    static Button autoGearPlaceButton 			= new JoystickButton(operatorStick, 10);
    static Button cancelAutoRoutinesButton 	= new JoystickButton(operatorStick, 11);
    
    static Button driverAutoGearButton 		= new JoystickButton(driverStick, 8); // was 4
    
 //   static Button Align_0Deg					= new JoystickButton(driverStick,2);
    static double lastX=0;
    static double LastY=0;
    
    public OI(){
    	toggleGearRamButton.whileHeld(new PushGear());
    	chuteAcquireButton.whenPressed(new ChuteAcquire());
    	//floorAcquireButton.whenPressed(new FloorAcquire());
    	toggleWristButton.whenPressed(new SetWristPosition());
    	toggleClawButton.whenPressed(new SetClawPosition());
    	toggleRopeGrabberButton.whenPressed(new GraspRope());
    	//autoPositionShooterButton.whenPressed(new VisionCorrect(false, 4));
    	//autoGearPlaceButton.whenPressed(new VisionCorrect2Step(4));
    	driverAutoGearButton.whenPressed(new VisionCorrect(true, 4));
    	
    	// Buttons to Align gyro with Cardinal directions
    //	Align_0Deg.whileHeld( new Align_Gyro(0));
    	
    	
    }
    
    public static boolean autoRoutinesCancelled()
    {
    	System.out.println("cancelled auto routines");
    	return cancelAutoRoutinesButton.get();
    }
    
	public static double getMoveSpeed()
	{
		return driverStick.getRawAxis(1);
	}
	
	public static double getRotateSpeed()
	{ // Re-defined to be left/right of Left Hand joystick
		return driverStick.getRawAxis(0); // was 4 (Right hand joystick)
	}
	
	public static double getClimbSpeed()
	{
		return operatorStick.getRawAxis(1);
	}
	
	public static boolean useFieldCoord() {
		/*
		 *  For operation with EXTREME 3D PRO joystick
		 * Field coordinates is assumed by default.  
		 * However, if operator depresses button 11, then joystick
		 *  axes are interpreted in Robot coordinates 
		 */
		if (driverStick.getRawButton(11))
			return false;
		else
			return true;
	}
	
	public static boolean isReverse() {
		return driverStick.getRawButton(2);
	}
	
	public static boolean isFullSpeed() {
		return driverStick.getRawButton(1);
	}
	
	
	public static double [] getFieldCommand()
	{
		double Magnitude, Direction, x, y;
		double [] result = new double[2];
		// Coeff for cubic polynomial map btwn joystick and magnitude
		/*
		double A=.3;
		double B=.25;
		double C=-.1852;
		double D=.85734;*/
			// Linear, with offset
		double A=.1;
		double B=1;
		double C=0;
		double D=0;
		
		
		double z;

		y=-driverStick.getRawAxis(1); // Inverted Y axis so "fwd" = +90 degrees
		x=driverStick.getRawAxis(0);
		Magnitude=Math.sqrt(x*x+y*y);
		// Apply deadband to avoid "creep" when joystick
		// does not return "0" at center position.
		if (Magnitude<Constants.joystickDeadband)
			Magnitude=0;
		else {		
			//
			// Cubic Polynomial maps between raw Joystick and Magnitude
			z=Magnitude-Constants.joystickDeadband;		
			Magnitude=A+B*z+C*z*z+D*z*z*z;		
			// Normalize to maximum of +/-1
			if (Math.abs(Magnitude)>1)
				Magnitude = Magnitude/Math.abs(Magnitude);
		}
		
		result[0]=Magnitude;
		
		// Filter joystick coordinates using simple exponential filter
		lastX=Constants.rotateAlpha*x + (1-Constants.rotateAlpha)*lastX;
		LastY=Constants.rotateAlpha*y + (1-Constants.rotateAlpha)*LastY;
		// Direction, in degrees, in range +/- 180
		Direction=180/Math.PI*Math.atan2(LastY, lastX);
		// Joystick "0" degrees is to the "right", so that
		// angles are reported in conventional Cartesian coordinate system
		// with +X to the right and +Y is ahead (forward relative to operator).
		result[1]=Direction;
		// Note - must manage offset between gyro heading (raw heading reported as "0"
		// when robot starts and is facing in the +Y in Field (Cartesian) coordinates.
		// The offset between gyro and operator (joystick) is managed in the Sensors class.
		
		return result;
		
	}
	

	public static boolean isZeroTurn()
	{
		boolean zt;
		// Execute a zero-turn (rotate about robot center) if zero-turn button is pressed
		zt=driverStick.getRawButton(3) | driverStick.getRawButton(4);
		return zt;
	}
	
	public static int isNullBias()
	{ // reset gyro Bias to ordinal directions based 
		float x=driverStick.getPOV();
		// on POV button
		// return: 0=0Deg, 1=90, 2=-90; 3=180; -1=none
		if (x<0)
			return -1;
		// Map POV coordinates to Field Coordinate directions
		// Invert sign and apply offset
		x=90-x; 
		if (x > -45 & x < 45) //  "B" = 0 Degree
			return 0;
		if (x > 45 & x < 135) // "Y" = 90 Degree
			return 1;
		if ( x > -135 & x < -45) // "A" = -90 Degree
			return 2;
		if (x > -225 & x < -135) // "X" = +/-180 Degree
			return 3;
		return -1;
	}
    
} // :D

