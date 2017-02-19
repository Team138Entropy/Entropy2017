package org.usfirst.frc.team138.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team138.robot.Robot;
import org.usfirst.frc.team138.robot.Sensors;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;

public class AutoDrive extends Command implements PIDOutput{
	
	boolean isDone = false;
	PIDController turnController;
	double rotateToAngleRate = 0.0;
	double lastRightDistance = 0.0;
	double lastLeftDistance = 0.0;
	int stallCounter = 0;
	boolean areMotorsStalled = false;
	boolean rotateInPlace;
	double driveSpeed = 0.0;
	double driveDistance = 0.0;
	double targetAngle = 0.0;
	
	//************************************************
	//PID CONSTANTS

	//kP Values for the 2016 boulder robot
	//.03 turning
	//.15 drive straighting

	static double kPRotate = 0.01;
	static double kPDrive = 0.2;
	static double kI = 0.0;
	static double kD = 0.0;

	//*******************************************
	
	//Degree Tolerance
	//within how many degrees will you be capable of turning
	static double ToleranceDegrees = 1.0;
	
	//Drive Straight, for some power and some distance
	public AutoDrive(double speedArg, double distanceArg){
		requires(Robot.drivetrain);
		rotateInPlace = false;
		driveSpeed = speedArg;
		driveDistance = distanceArg;
		turnController = new PIDController(kPDrive, kI, kD, Sensors.gyro, this);
		turnController.setAbsoluteTolerance(ToleranceDegrees);         
		turnController.setInputRange(-360.0,  360.0);
	    turnController.setOutputRange(-1.0, 1);
	    turnController.setContinuous(true);
	}
	
	//rotates to an angle
	public AutoDrive(double angle){
		requires(Robot.drivetrain);
		rotateInPlace = true;
		targetAngle = angle;
		turnController = new PIDController(kPRotate, kI, kD, Sensors.gyro, this);
		turnController.setAbsoluteTolerance(ToleranceDegrees);         
		turnController.setInputRange(-360.0,  360.0);
	    turnController.setOutputRange(-1.0, 1);
	    turnController.setContinuous(true);
	}

	protected void initialize() {
		Sensors.resetEncoders();
		if (rotateInPlace)
		{
			turnController.setSetpoint(targetAngle);
		}
		else 
		{
			turnController.setSetpoint(Sensors.gyro.getAngle());
		}
		turnController.enable();
	}

	protected void execute() {
		if (areMotorsStalled) 
		{
			Robot.drivetrain.drive(0.0, 0.0);
		}
		else
		{
			boolean done;
			if (rotateInPlace)
			{
				done = Sensors.gyro.getAngle() >= targetAngle;
			}
			else
			{
				done = (Math.abs(Sensors.getLeftDistance()) + Math.abs(Sensors.getRightDistance())) / 2 >= driveDistance;
			}
			if (done)
			{
				Robot.drivetrain.drive(0.0, 0.0);
				isDone = true;
			}		
			else
			{
				Robot.drivetrain.drive(driveSpeed, rotateToAngleRate);
				System.out.println("Rate: " + rotateToAngleRate);
				
				if (lastRightDistance == Sensors.getRightDistance() || lastLeftDistance == Sensors.getLeftDistance()) 
				{
					if (stallCounter == 75) 
					{
						//areMotorsStalled = true;
					}
					stallCounter++;
				}
				else
				{
					stallCounter = 0;
				}	
				lastRightDistance = Sensors.getRightDistance();
				lastLeftDistance = Sensors.getLeftDistance();
				
				SmartDashboard.putNumber("Rotate to Angle Rate", rotateToAngleRate);
			}
		}
	}

	protected boolean isFinished() {
		return isDone;
	}

	protected void end() {
		System.out.println("Ended");
	}

	protected void interrupted() {
	}

	public void pidWrite(double output) {
		if (rotateInPlace)
		{
			output = -output;
			double minSpeed = 0.575;
			if (output > minSpeed|| output < -minSpeed)
			{
				rotateToAngleRate = output;
			}
			else if (targetAngle > 0)
			{
				rotateToAngleRate = -minSpeed;
			}
			else
			{
				rotateToAngleRate = minSpeed;
			}
		}
		else
		{
			if (driveSpeed < 0)
			{
				rotateToAngleRate = -1 * output;
			}
			else
			{
				rotateToAngleRate = -1 * output;
			}
		}		
	}
	
}
