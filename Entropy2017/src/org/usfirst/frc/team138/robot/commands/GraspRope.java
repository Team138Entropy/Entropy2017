package org.usfirst.frc.team138.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team138.robot.Robot;

public class GraspRope extends Command{
	
	public GraspRope(){
		requires(Robot.climbingMechanism);
	}

	protected void initialize() {
		Robot.climbingMechanism.setGrasper(!Robot.climbingMechanism.getOpen());
		setTimeout(0.5);
	}

	protected void execute() {
	}

	protected boolean isFinished() {
		return isTimedOut();
	}

	protected void end() {
	}

	protected void interrupted() {
	}

}
