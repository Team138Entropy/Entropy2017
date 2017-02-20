package org.usfirst.frc.team138.robot.subsystems;

import org.usfirst.frc.team138.robot.RobotMap;
import org.usfirst.frc.team138.robot.commands.Climb;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.Subsystem;

public class ClimbingMechanism extends Subsystem{
	
	// Define Climbing Parts Here
	Talon climbingMotor = new Talon(RobotMap.ROPE_CLIMBING_WINCH_PORT);
	Solenoid ropeGrasper = new Solenoid(RobotMap.ROPE_GRABBING_SOLENOID_PORT);
	
	boolean isOpen = true;

	protected void initDefaultCommand() {	
		setDefaultCommand(new Climb());
	}

	public void setSpeed(double speed)
	{
		climbingMotor.setSpeed(speed);
	}
	
	public void setGrasper(boolean open)
	{
		isOpen = open;
		ropeGrasper.set(!open);
	}
	
	public boolean getOpen()
	{
		return isOpen;
	}
	
}
