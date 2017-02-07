package org.usfirst.frc.team138.robot.commands;

import org.usfirst.frc.team138.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class SetWristPosition extends Command {

	boolean isUp;
	
	public SetWristPosition(boolean flipUp){
		requires(Robot.wrist);
		isUp = flipUp;
	}

	protected void initialize() {
		if (isUp == Robot.wrist.isUp()) {
			setTimeout(0.0);
		} else if (isUp)
		{
			Robot.wrist.setUp();
			setTimeout(0.3);
		} else {
			Robot.wrist.setDown();
			setTimeout(0.0);
		}
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