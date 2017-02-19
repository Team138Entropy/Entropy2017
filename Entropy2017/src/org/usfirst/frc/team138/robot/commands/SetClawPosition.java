package org.usfirst.frc.team138.robot.commands;

import org.usfirst.frc.team138.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class SetClawPosition extends Command {

	boolean isOpen;
	boolean toggleMode = false;
	
	public SetClawPosition(boolean open){
		requires(Robot.claw);
		isOpen = open;
	}
	
	public SetClawPosition(){
		requires(Robot.claw);
		toggleMode = true;
	}

	protected void initialize() {
		if (toggleMode)
		{
			isOpen = !Robot.claw.clawIsOpen();
		}
		if (isOpen == Robot.claw.clawIsOpen()) {
			setTimeout(0.0);
		} else if (isOpen){
			Robot.claw.openClaw();
			setTimeout(0.2);
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