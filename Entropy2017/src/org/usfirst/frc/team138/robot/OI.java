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
public class OI {
    Joystick driverStick = new Joystick(0);
    Joystick operatorStick = new Joystick(1);
    
    Button toggleGearRamButton 			= new JoystickButton(operatorStick, 1);
    Button toggleRopeGrabberButton 		= new JoystickButton(operatorStick, 2);
    Button chuteAcquireButton			= new JoystickButton(operatorStick, 3);
    Button floorAcquireButton	 		= new JoystickButton(operatorStick, 4);
    Button toggleWristButton 			= new JoystickButton(operatorStick, 5);
    Button toggleClawButton 			= new JoystickButton(operatorStick, 6);
    Button shootButton 					= new JoystickButton(operatorStick, 7);
    Button zeroTurn                     = new JoystickButton(operatorStick, 8);
    Button autoPositionShooterButton 	= new JoystickButton(operatorStick, 9);
    Button autoGearPlaceButton 			= new JoystickButton(operatorStick, 10);
    Button cancelAutoRoutinesButton 	= new JoystickButton(operatorStick, 11);
    
    Button driverAutoGearButton 		= new JoystickButton(driverStick, 4);
    
    Button AlignGyro0					= new JoystickButton(driverStick,2);
    Button AlignGyro90					= new JoystickButton(driverStick,4);
    Button AlignGyro_90					= new JoystickButton(driverStick,1);
    Button AlignGyro_180				= new JoystickButton(driverStick,3);
    
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
    	
    	
    }
    
    public boolean autoRoutinesCancelled()
    {
    	System.out.println("cancelled auto routines");
    	return cancelAutoRoutinesButton.get();
    }
    
	public double getMoveSpeed()
	{
		return driverStick.getRawAxis(1);
	}
	
	public double getRotateSpeed()
	{
		return driverStick.getRawAxis(4);
	}
	
	public double getClimbSpeed()
	{
		return operatorStick.getRawAxis(1);
	}
	
	public double [] getFieldCommand()
	{
		double Magnitude, Direction, x, y;
		double [] result = new double[2];
		y=-driverStick.getRawAxis(1); // Inverted Y axis so "fwd" = +90 degrees
		x=driverStick.getRawAxis(0);
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
	

	public boolean isZeroTurn()
	{
		// Execute a zero-turn (rotate about robot center) if zero-turn button is pressed
		return driverStick.getRawButton(5);
	}
	
	public int isNullBias()
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

