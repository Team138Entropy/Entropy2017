package org.usfirst.frc.team138.robot.subsystems;

import org.usfirst.frc.team138.robot.RobotMap;
import org.usfirst.frc.team138.robot.commands.Climb;

import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.Subsystem;

public class ClimbingMechanism extends Subsystem{
	
	// Define Climbing Parts Here
	Talon climbingMotor = new Talon(RobotMap.ROPE_CLIMBING_WINCH_PORT);

	protected void initDefaultCommand() {	
		setDefaultCommand(new Climb());
	}

	public void setSpeed(double speed)
	{
		climbingMotor.setSpeed(speed);
	}
	
}
