package org.usfirst.frc.team138.robot.commands;

import org.usfirst.frc.team138.robot.Robot;
import edu.wpi.first.wpilibj.command.Command;

public class PushGear extends Command {

	boolean isExtended;
	boolean toggleMode = false;
	
	public PushGear(boolean extended){
		requires(Robot.claw);
		isExtended = extended;
	}
	
	public PushGear(){
		requires(Robot.claw);
		toggleMode = true;
	}

	protected void initialize() {
		if (toggleMode)
		{
			isExtended = !Robot.claw.ramExtended();
		}
		if (!isExtended == Robot.claw.ramExtended() && Robot.claw.wristIsUp()) {
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
		return true;
	}

	protected void end() {
	}

	protected void interrupted() {
	}

}