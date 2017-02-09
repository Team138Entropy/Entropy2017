package org.usfirst.frc.team138.robot.commands;

import org.usfirst.frc.team138.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class SetWristPosition extends Command {

	boolean isUp;
	
	public SetWristPosition(boolean flipUp){
		requires(Robot.claw);
		isUp = flipUp;
	}
	
	public SetWristPosition(){
		requires(Robot.claw);
		isUp = !Robot.claw.wristIsUp();
	}

	protected void initialize() {		
		if (!isUp == Robot.claw.wristIsUp() && !Robot.claw.clawIsOpen()) {
			if (!Robot.claw.guardIsUp()){
				Robot.claw.guardUp();
			}
			if (isUp) {
				Robot.claw.wristUp();
				setTimeout(0.3);
			} else {
				Robot.claw.wristDown();
				setTimeout(0.0);
			}
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