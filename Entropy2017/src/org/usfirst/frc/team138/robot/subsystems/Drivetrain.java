package org.usfirst.frc.team138.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.RobotDrive;
import org.usfirst.frc.team138.robot.commands.TeleopDrive;

public class Drivetrain extends Subsystem{

	static int LEFT_MOTOR_CHANNEL = 0;
	static int RIGHT_MOTOR_CHANNEL = 1;
	RobotDrive drivetrain = new RobotDrive(LEFT_MOTOR_CHANNEL, RIGHT_MOTOR_CHANNEL);
	
	protected void initDefaultCommand() {
		setDefaultCommand(new TeleopDrive());
	}
	
	public void drive(double moveSpeed, double rotateSpeed)
	{
		drivetrain.arcadeDrive(moveSpeed, rotateSpeed);
	}

}
