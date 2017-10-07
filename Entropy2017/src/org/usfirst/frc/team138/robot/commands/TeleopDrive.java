package org.usfirst.frc.team138.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team138.robot.Robot;
import org.usfirst.frc.team138.robot.subsystems.Claw;

public class TeleopDrive extends Command{
	
	public TeleopDrive(){
		requires(Robot.drivetrain);
	}

	protected void initialize() {
	}

	protected void execute() {
		//
		// the Robot.drivetrain.driveWithTable may not be necessary
		// 10/7/17 - pre RiverRage
		//
		if (Robot.oi.getMoveSpeed() < 0 && Robot.claw.wristIsUp() == false) {
			Robot.claw.wristUp();
		    Robot.drivetrain.driveWithTable(Robot.oi.getMoveSpeed(), Robot.oi.getRotateSpeed());
		} else {
			Robot.drivetrain.driveWithTable(Robot.oi.getMoveSpeed(), Robot.oi.getRotateSpeed());
		}
	}

	protected boolean isFinished() {
		return false;
	}

	protected void end() {
	}

	protected void interrupted() {
	}

}
