package org.usfirst.frc.team138.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team138.robot.OI;
import org.usfirst.frc.team138.robot.Robot;

public class Climb extends Command{
	
	public Climb(){
		requires(Robot.climbingMechanism);
	}

	protected void initialize() {
	}

	protected void execute() {
		Robot.climbingMechanism.startClimb();
	}

	protected boolean isFinished() {
		return false;
	}

	protected void end() {
	}

	protected void interrupted() {
		Robot.climbingMechanism.stopClimb();
	}

}
