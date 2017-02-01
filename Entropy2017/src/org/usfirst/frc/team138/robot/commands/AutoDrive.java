package org.usfirst.frc.team138.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team138.robot.Robot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;

public class AutoDrive extends Command implements PIDOutput{
	
	boolean isDone = false;
	PIDController turnController;
	double rotateToAngleRate = 0.0;
	double distance;
	double difference;
	double lastRightDistance = 0.0;
	double lastLeftDistance = 0.0;
	int stallCounter = 0;
	boolean areMotorsStalled = false;
	
	//************************************************
	//PID CONSTANTS

	//kP Values for the 2016 boulder robot
	//.03 turning
	//.15 drive straighting

	static double kP = .03;
	static double kI = 0.0;
	static double kD = 0.0;

	//*******************************************

	double driveSpeed = 0.0;
	double driveDistance = 0.0;
	
	//Degree Tolerance
	//within how many degrees will you be capable of turning
	static double ToleranceDegrees = 2.0;
	
	//Drive Stright, for some power and some distance
	public AutoDrive(double speedArg, double distanceArg){
		requires(Robot.drivetrain);
		double angle = Robot.gyro.getAngle();
		double desiredAngleToHold = angle;
		driveSpeed = speedArg;
		driveDistance = distanceArg;
		turnController = new PIDController(kP, kI, kD, Robot.gyro, this);
		turnController.setAbsoluteTolerance(ToleranceDegrees);         
		turnController.setInputRange(-360.0,  360.0);
	    turnController.setOutputRange(-1.0, 1);
		turnController.setSetpoint(desiredAngleToHold);
	    turnController.setContinuous(true);
	    turnController.enable();
	}
	
	//rotates to an angle
	public AutoDrive(double angle){
		requires(Robot.drivetrain);
		turnController = new PIDController(kP, kI, kD, Robot.gyro, this);
		turnController.setAbsoluteTolerance(ToleranceDegrees);         
		turnController.setInputRange(-360.0,  360.0);
	    turnController.setOutputRange(-1.0, 1);
		turnController.setSetpoint(angle);
	    turnController.setContinuous(true);
	    turnController.enable();
	}

	protected void initialize() {
	}

	protected void execute() {
		if (areMotorsStalled) 
		{
			Robot.drivetrain.drive(0.0, 0.0);
		}
		else
		{
			if ((Math.abs(Robot.drivetrain.leftEncoderGet()) + Math.abs(Robot.drivetrain.rightEncoderGet())) / 2 >= distance )
			{
				Robot.drivetrain.drive(0.0, 0.0);
				Robot.drivetrain.resetEncoders();
			
				SmartDashboard.putNumber("Left Encoder:", Robot.drivetrain.leftEncoderGet());
				SmartDashboard.putNumber("Right Encoder:", Robot.drivetrain.rightEncoderGet());
			
				isDone = true;
			}		
			else
			{
				Robot.drivetrain.driveWithTable(driveSpeed, rotateToAngleRate);
			
				//get lowest Encoder Values
				if( Math.abs(Robot.drivetrain.leftEncoderGet()) < Math.abs(Robot.drivetrain.rightEncoderGet())){
					difference = Math.abs(Robot.drivetrain.leftEncoderGet());
				} else {
					difference = Math.abs(Robot.drivetrain.rightEncoderGet());
				}		
				
				if (lastRightDistance == Robot.drivetrain.rightEncoderGet() || lastLeftDistance == Robot.drivetrain.leftEncoderGet()) 
				{
					if (stallCounter == 75) 
					{
						areMotorsStalled = true;
					}
					stallCounter++;
				}
				else
				{
					stallCounter = 0;
				}
				
				lastRightDistance = Robot.drivetrain.rightEncoderGet();
				lastLeftDistance = Robot.drivetrain.leftEncoderGet();
				
				SmartDashboard.putNumber("Distance", (Math.abs(Robot.drivetrain.leftEncoderGet()) + Math.abs(Robot.drivetrain.rightEncoderGet())) / 2);
				SmartDashboard.putNumber("Left Encoder:", Robot.drivetrain.leftEncoderGet());
				SmartDashboard.putNumber("Right Encoder:", Robot.drivetrain.rightEncoderGet());
			}
		}
	}

	protected boolean isFinished() {
		return isDone;
	}

	protected void end() {
	}

	protected void interrupted() {
	}

	public void pidWrite(double output) {
		rotateToAngleRate = output;		
	}

}
