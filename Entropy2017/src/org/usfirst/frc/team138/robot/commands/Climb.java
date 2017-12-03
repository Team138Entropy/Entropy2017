package org.usfirst.frc.team138.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team138.robot.Robot;
import org.usfirst.frc.team138.robot.RobotMap;
import org.usfirst.frc.team138.robot.OI;

public class Climb extends Command{
	
	final double threshold = 0.5;
	
	public Climb(){
		requires(Robot.climbingMechanism);
	}

	protected void initialize() {
	}

	protected void execute() {
		if(OI.getClimbSpeed() > threshold)
		{
			Robot.climbingMechanism.setSpeed(RobotMap.ROPE_CLIMB_SPEED);
		}
		else if(OI.getClimbSpeed() < -threshold)
		{
			Robot.climbingMechanism.setSpeed(RobotMap.ROPE_PULSE_SPEED);
		}
		else
		{
			Robot.climbingMechanism.setSpeed(0.0);
		}
	}

	protected boolean isFinished() {
		return false;
	}

	protected void end() {
		Robot.climbingMechanism.setSpeed(0.0);
	}

	protected void interrupted() {
	}

}
