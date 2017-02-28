package org.usfirst.frc.team138.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team138.robot.Robot;

public class TeleopDrive extends Command{
	
	public TeleopDrive(){
		requires(Robot.drivetrain);
	}

	protected void initialize() {
	}

	protected void execute() {
		Robot.drivetrain.driveWithTable(Robot.oi.getMoveSpeed(), Robot.oi.getRotateSpeed());
	}

	protected boolean isFinished() {
		return false;
	}

	protected void end() {
	}

	protected void interrupted() {
	}

}
