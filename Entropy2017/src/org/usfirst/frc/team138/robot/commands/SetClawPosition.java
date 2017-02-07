package org.usfirst.frc.team138.robot.commands;

import org.usfirst.frc.team138.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class SetClawPosition extends Command {

	boolean isOpen;
	
	public SetClawPosition(boolean open){
		requires(Robot.claw);
		isOpen = open;
	}

	protected void initialize() {
		if (isOpen == Robot.claw.isOpen()) {
			setTimeout(0.0);
		} else if (isOpen){
			Robot.claw.acquireGear();
			setTimeout(0.25);
		} else {
			Robot.claw.releaseGear();
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