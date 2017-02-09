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
    //// CREATING BUTTONS
    // One type of button is a joystick button which is any button on a joystick.
    // You create one by telling it which joystick it's on and which button
    // number it is.
    Joystick driverStick = new Joystick(0);
    Joystick operatorStick = new Joystick(1);
    
    Button floorGearAcqButton = new JoystickButton(driverStick, 3);
    Button loadStnGearAcqButton = new JoystickButton(driverStick, 2);
    
    Button toggleGearRamButton = new JoystickButton(operatorStick, 1);
    Button toggleChuteGuardButton = new JoystickButton(operatorStick, 4);
    Button toggleWristButton = new JoystickButton(operatorStick, 5);
    Button toggleClawButton = new JoystickButton(operatorStick, 6);
    //Button climbButton = new JoystickButton(operatorStick, some button);
    
    public OI(){
    	floorGearAcqButton.whenPressed(new FloorGearAcquire());
    	loadStnGearAcqButton.whenPressed(new LoadStationGearAcquire());
    	toggleGearRamButton.whenPressed(new PushGear());
    	toggleChuteGuardButton.whenPressed(new SetGuardPosition());
    	toggleWristButton.whenPressed(new SetWristPosition());
    	toggleClawButton.whenPressed(new SetClawPosition());
    	//climbButton.whileHeld(new Climb());
    }
    
    // There are a few additional built in buttons you can use. Additionally,
    // by subclassing Button you can create custom triggers and bind those to
    // commands the same as any other Button.
    
    //// TRIGGERING COMMANDS WITH BUTTONS
    // Once you have a button, it's trivial to bind it to a button in one of
    // three ways:
    
    // Start the command when the button is pressed and let it run the command
    // until it is finished as determined by it's isFinished method.
    // button.whenPressed(new ExampleCommand());
    
    // Run the command while the button is being held down and interrupt it once
    // the button is released.
    // button.whileHeld(new ExampleCommand());
    
    // Start the command when the button is released  and let it run the command
    // until it is finished as determined by it's isFinished method.
    
	public double getMoveSpeed()
	{
		return driverStick.getRawAxis(1);
	}
	
	public double getRotateSpeed()
	{
		return driverStick.getRawAxis(4);
	}
}

