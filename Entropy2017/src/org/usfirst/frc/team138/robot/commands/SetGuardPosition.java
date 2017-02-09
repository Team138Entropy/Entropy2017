package org.usfirst.frc.team138.robot.commands;

import org.usfirst.frc.team138.robot.Robot;
import edu.wpi.first.wpilibj.command.Command;

public class SetGuardPosition extends Command {

	boolean isUp;
	
	public SetGuardPosition(boolean up){
		requires(Robot.claw);
		isUp = up;
	}
	
	public SetGuardPosition(){
		requires(Robot.claw);
		isUp = !Robot.claw.guardIsUp();
	}

	protected void initialize() {
		if (!isUp == Robot.claw.guardIsUp() && Robot.claw.wristIsUp()) {
			if (isUp){
				Robot.claw.guardUp();
			} else {
				Robot.claw.guardDown();
			}
		} 
	}

	protected void execute() {
		
	}

	protected boolean isFinished() {
		return true;
	}

	protected void end() {
	}

	protected void interrupted() {
	}

}