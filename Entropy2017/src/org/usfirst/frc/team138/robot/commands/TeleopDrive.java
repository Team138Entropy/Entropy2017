package org.usfirst.frc.team138.robot.commands;

import edu.wpi.first.wpilibj.command.Command;

import org.usfirst.frc.team138.robot.Robot;
import org.usfirst.frc.team138.robot.OI;
//import org.usfirst.frc.team138.robot.subsystems.Claw;

public class TeleopDrive extends Command{
	
	float clawUpPoint = 0.1f;
	
	public TeleopDrive(){
		requires(Robot.drivetrain);
	}

	protected void initialize() {
	}

	protected void execute() {
		//
		// the first Robot.drivetrain.driveWithTable may not be necessary
		// 10/7/17 - pre RiverRage
		// TODO: remove
		//
//		if (Robot.oi.getMoveSpeed() < clawUpPoint && Robot.claw.wristIsUp() == false) {
//			Robot.claw.wristUp();
//		    Robot.drivetrain.driveWithTable(Robot.oi.getMoveSpeed(), Robot.oi.getRotateSpeed());
//		} else {
//			Robot.drivetrain.driveWithTable(Robot.oi.getMoveSpeed(), Robot.oi.getRotateSpeed());
//		}
		if (!OI.useFieldCoord())
			Robot.drivetrain.driveWithTable(OI.getMoveSpeed(), OI.getRotateSpeed());
		else
			Robot.drivetrain.driveWithFieldCoord();
	}

	protected boolean isFinished() {
		return false;
	}

	protected void end() {
	}

	protected void interrupted() {
	}

}
