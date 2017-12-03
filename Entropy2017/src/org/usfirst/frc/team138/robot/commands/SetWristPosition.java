package org.usfirst.frc.team138.robot.commands;

import org.usfirst.frc.team138.robot.Robot;
//import org.usfirst.frc.team138.robot.Sensors;

import edu.wpi.first.wpilibj.command.Command;

public class SetWristPosition extends Command {

	boolean isUp;
	boolean toggleMode = false;
	
	public SetWristPosition(boolean flipUp){
		requires(Robot.claw);
		isUp = flipUp;
	}
	
	public SetWristPosition(){
		requires(Robot.claw);
		toggleMode = true;
	}

	protected void initialize() {		
		if (toggleMode)
		{
			isUp = !Robot.claw.wristIsUp();
		}
		if (!isUp == Robot.claw.wristIsUp()) {
			if (Robot.claw.clawIsOpen())
			{
				Robot.claw.closeClaw();
			}
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