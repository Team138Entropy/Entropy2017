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

	static double kPRotate = 0.012;
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
	}
	
	//rotates to an angle
	public AutoDrive(double angle){
		requires(Robot.drivetrain);
		rotateInPlace = true;
		targetAngle = angle;
		turnController = new PIDController(kPRotate, kI, kD, Sensors.gyro, this);
	}

	public void initialize() {
		Sensors.resetEncoders();
		Sensors.gyro.reset();
		
		turnController.setAbsoluteTolerance(ToleranceDegrees);         
	    turnController.setOutputRange(-1.0, 1);
	    turnController.setContinuous(true);
		turnController.setInputRange(360.0, 360.0);
		if (rotateInPlace)
		{
			turnController.setSetpoint(targetAngle);
		}
		else 
		{
			turnController.setSetpoint(0);
		}
		turnController.enable();
	}

	public void execute() {
		if (areMotorsStalled) 
		{
			Robot.drivetrain.drive(0.0, 0.0);
			System.out.println("Stalled");
		}
		else
		{
			boolean done;
			if (rotateInPlace)
			{
				if (targetAngle > 0)
				{
					done = Sensors.gyro.getAngle() >= targetAngle;
				}
				else
				{
					done = Sensors.gyro.getAngle() <= targetAngle;
				}
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
				
				if (lastRightDistance == Sensors.getRightDistance() || lastLeftDistance == Sensors.getLeftDistance()) 
				{
					if (stallCounter == 75) 
					{
						isDone = true;
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

	public boolean isFinished() {
		return isDone;
	}

	public void end() {
	}

	protected void interrupted() {
	}

	public void pidWrite(double output) {
		output = -output;
		if (rotateInPlace)
		{
			double minSpeed = 0.7;
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
			rotateToAngleRate =  output;
		}		
	}
	
}
