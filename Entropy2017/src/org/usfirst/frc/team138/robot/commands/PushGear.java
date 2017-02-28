package org.usfirst.frc.team138.robot.commands;

import org.usfirst.frc.team138.robot.Robot;
import edu.wpi.first.wpilibj.command.Command;

public class PushGear extends Command {

	boolean isExtended;
	boolean cycleMode = false;
	
	public PushGear(boolean extended){
		requires(Robot.claw);
		isExtended = extended;
	}
	
	public PushGear(){
		requires(Robot.claw);
		cycleMode = true;
	}

	protected void initialize() {
		if (cycleMode)
		{
			isExtended = true;
		}
		if (Robot.claw.wristIsUp() && Robot.claw.clawIsOpen()) {
			if (isExtended){
				Robot.claw.extendRam();
			} else {
				Robot.claw.retractRam();
			}
		} 
	}

	protected void execute() {
		
	}

	protected boolean isFinished() {
		return !cycleMode;
	}

	protected void end() {
		if (cycleMode){
			Robot.claw.retractRam();
		}
	}

	protected void interrupted() {
		end();
	}

}