package org.usfirst.frc.team138.robot.commands;

import org.usfirst.frc.team138.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class SetClawPosition extends Command {

	boolean isOpen;
	
	public SetClawPosition(boolean open){
		requires(Robot.claw);
		isOpen = open;
	}
	
	public SetClawPosition(){
		requires(Robot.claw);
		isOpen = !Robot.claw.clawIsOpen();
	}

	protected void initialize() {
		if (isOpen == Robot.claw.clawIsOpen()) {
			setTimeout(0.0);
		} else if (isOpen){
			Robot.claw.openClaw();
			setTimeout(0.0);
		} else {
			Robot.claw.closeClaw();
			setTimeout(0.25);
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