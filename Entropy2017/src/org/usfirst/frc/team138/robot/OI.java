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
		return driverStick.getRawAxis(1); // was 4 (Right hand joystick)
	}
	
	public static double getClimbSpeed()
	{
		return operatorStick.getRawAxis(1);
	}
	
	public static boolean useFieldCoord() {
		/*
		 * Use driverStick axes 0,1 for motion in Robot coords
		 * Use driverStick axes 4,5 for Field Coord motion
		 * Compare magnitude of both joysticks to determine
		 * which coordinate system operator is commanding.
		 * Normally, the operator will only use one or the other
		 * joystick.  However, in case both are off-center, 
		 * select commands from the one with greater magnitude.
		 * In the case of a "tie", preference given to Robot Coordinates.
		 * */
		double x,y;
		double RC;
		double [] FC;
		x=getMoveSpeed();
		y=getRotateSpeed();
		RC=x*x+y*y;
		//
		FC=getFieldCommand();
		if (RC>=FC[0])
			return false;
		else
			return true;
	}
	
	public static double [] getFieldCommand()
	{
		double Magnitude, Direction, x, y;
		double [] result = new double[2];
		y=-driverStick.getRawAxis(5); // Inverted Y axis so "fwd" = +90 degrees
		x=driverStick.getRawAxis(4);
		Magnitude=Math.sqrt(x*x+y*y);
		// Apply deadband to avoid "creep" when joystick
		// does not return "0" at center position.
		if (Magnitude<Constants.joystickDeadband)
			Magnitude=0;
		// Normalize to maximum of +/-1
		if (Math.abs(Magnitude)>1)
			Magnitude = Magnitude/Math.abs(Magnitude);
		result[0]=Magnitude;
		// Direction, in degrees, in range +/- 180
		Direction=180/Math.PI*Math.atan2(y, x);
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
		// Execute a zero-turn (rotate about robot center) if zero-turn button is pressed
		return driverStick.getRawButton(6);
	}
	
	public static int isNullBias()
	{ // reset gyro Bias to ordinal directions based 
		// on which button (1-4) on driver joystick is pressed
		// buttons are labeled "A" "B" "X" "Y"
		// return -1 if no button pressed
		// return: 0=0Deg, 1=90, 2=-90; 3=180; -1=none
		if (driverStick.getRawButton(2)) //  "B" = 0 Degree
			return 0;
		if (driverStick.getRawButton(4)) // "Y" = 90 Degree
			return 1;
		if (driverStick.getRawButton(1)) // "A" = -90 Degree
			return 2;
		if (driverStick.getRawButton(3)) // "X" = +/-180 Degree
			return 3;
		return -1;
	}
    
} // :D

