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
	boolean arcTurn = false;
	
	//************************************************
	//PID CONSTANTS

	static double kPRotate = 0.012;
	static double kPDrive = 0.2;
	static double kI = 0.0;
	static double kD = 0.0;

	//*******************************************
	
	//Degree Tolerance
	//within how many degrees will you be capable of turning
	static double ToleranceDegrees = 1.0;
	
	/**
	 * Drives straight for the specified distance
	 * @param speedArg The speed, from -1.0 to 1.0, the robot will drive. Negative speeds go backwards
	 * @param distanceArg The distance to drive, in inches. Uses the absolute value of encoders, so use positive distances
	 */
	public AutoDrive(double speedArg, double distanceArg){
		requires(Robot.drivetrain);
		rotateInPlace = false;
		driveSpeed = speedArg;
		driveDistance = distanceArg;
		turnController = new PIDController(kPDrive, kI, kD, Sensors.gyro, this);
	}
	
	/**
	 * Rotates to an angle
	 * @param angle Angle, in degrees, to turn to. Negative angles turn left, positive angles turn right
	 */
	public AutoDrive(double angle){
		requires(Robot.drivetrain);
		rotateInPlace = true;
		targetAngle = angle;
		turnController = new PIDController(kPRotate, kI, kD, Sensors.gyro, this);
	}
	
	/**
	 * Drives the robot the specified number of inches to the side while rotating to the angle specified !!TESTING!!
	 * @param speed The speed, from -1.0 to 1.0, the robot will drive. Negative speeds go backwards
	 * @param angle Angle, in degrees, to turn to. Negative angles turn left, positive angles turn right
	 * @param offsetInches Number of inches the robot is off center of the target
	 */
	public AutoDrive(double speed, double angle, double offsetInches)
	{
		requires(Robot.drivetrain);
		rotateInPlace = false;
		arcTurn = true;
		targetAngle = angle;
		driveSpeed = speed;
		// Arc length
		driveDistance = 2 * Math.PI * angle / 360 * offsetInches / Math.sin(angle);
		
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
			boolean result;
			if (rotateInPlace)
			{
				if (targetAngle > 0)
				{
					result = Sensors.gyro.getAngle() >= targetAngle;
				}
				else
				{
					result = Sensors.gyro.getAngle() <= targetAngle;
				}
			}
			else
			{
				result = (Math.abs(Sensors.getLeftDistance()) >= driveDistance) || (Math.abs(Sensors.getRightDistance()) >= driveDistance);
			}
			if (result)
			{
				Robot.drivetrain.drive(0.0, 0.0);
				isDone = true;
			}		
			else
			{
				if (arcTurn)
				{
					turnController.setSetpoint(targetAngle * 
							(Math.abs(Sensors.getLeftDistance()) + Math.abs(Sensors.getRightDistance())) / 2
							/ driveDistance);
				}
				Robot.drivetrain.drive(driveSpeed, rotateToAngleRate);
				
				if (lastRightDistance == Sensors.getRightDistance() || lastLeftDistance == Sensors.getLeftDistance()) 
				{
					if (stallCounter == 25) 
					{
						turnController.setSetpoint(2);
						//isDone = true;
						//areMotorsStalled = true;
					}
					if(stallCounter == 50)
					{
						isDone = true;
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
			double minSpeed = 0.8;
			if (output > minSpeed || output < -minSpeed)
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
