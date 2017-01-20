package org.usfirst.frc.team138.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.RobotDrive;
import org.usfirst.frc.team138.robot.commands.TeleopDrive;
import org.usfirst.frc.team138.robot.RobotMap;

public class Drivetrain extends Subsystem{

	private static double CONTROLLER_DEAD_ZONE = 0.05;
	
	RobotDrive drivetrain = new RobotDrive(RobotMap.LEFT_MOTOR_CHANNEL_FRONT, 
			RobotMap.LEFT_MOTOR_CHANNEL_BACK,
			RobotMap.RIGHT_MOTOR_CHANNEL_FRONT, 
			RobotMap.RIGHT_MOTOR_CHANNEL_BACK);
	
	protected void initDefaultCommand() {
		setDefaultCommand(new TeleopDrive());
	}
	
	public void drive(double moveSpeed, double rotateSpeed)
	{
		drivetrain.arcadeDrive(moveSpeed, rotateSpeed);
	}

	public void driveTank(double leftSpeed, double rightSpeed) 
	{
		drivetrain.tankDrive(leftSpeed, rightSpeed);
	}
	
	public void autoDrive(double leftSpeed, double rightSpeed) 
	{
		drivetrain.setLeftRightMotorOutputs(leftSpeed, rightSpeed);
	}
	
	public void driveRobot(double moveSpeed, double rotateSpeed)
	{
		// Filter input speeds
		moveSpeed = applyDeadZone(moveSpeed);
		rotateSpeed = applyDeadZone(rotateSpeed);
				
		// Motor Speeds on both the left and right sides
		double leftMotorSpeed  = getLeftMotorSpeed(moveSpeed, rotateSpeed);
		double rightMotorSpeed = getRightMotorSpeed(moveSpeed, rotateSpeed);
		
		drivetrain.setLeftRightMotorOutputs(leftMotorSpeed, rightMotorSpeed);
	}
	
	double getLeftMotorSpeed(double moveSpeed, double rotateSpeed)
	{
		int[] indices = {16, 16};

		indices = getIndex(moveSpeed, rotateSpeed);
			
		if(rotateSpeed < 0)
		{
			indices[0] = 32 - indices[0];
		}

		return DriveTable.Drive_Matrix[indices[1]][indices[0]];
	}
	
	double getRightMotorSpeed(double moveSpeed, double rotateSpeed)
	{
		int[] indices = {16, 16};

		indices = getIndex(moveSpeed, rotateSpeed);
			
		if(rotateSpeed > 0)
		{
			indices[0] = 32 - indices[0];
		}

		return DriveTable.Drive_Matrix[indices[1]][indices[0]];
	}
	
	int[] getIndex(double moveSpeed, double rotateSpeed)
	{
		double diff1 = 0;
		double diff2 = 0;
		int[] returnIndex = {0, 0};

		double[] arrayPtr = DriveTable.Drive_Lookup_X;
		int arrayLength = DriveTable.Drive_Lookup_X.length;
		double minRotate = arrayPtr[0];
		double maxRotate = arrayPtr[arrayLength-1];

		double rotateValue = limitValue(rotateSpeed, minRotate, maxRotate);

		for(int i = 0; i < arrayLength; i++) 
		{
			if(i+1 >= arrayLength || range(rotateValue, arrayPtr[i], arrayPtr[i+1]))
			{
				//Assume match found
				if((i + 1) >= arrayLength)
				{
					returnIndex[0] = i;	
				}
				else
				{
					diff1 = Math.abs(rotateValue - arrayPtr[i]);
					diff2 = Math.abs(rotateValue - arrayPtr[i+1]);

					if(diff1 < diff2)
					{
						returnIndex[0] = i;
					}
					else
					{
						returnIndex[0] = i + 1;
					}
				}
				break;
			}
		}
		
		arrayPtr = DriveTable.Drive_Lookup_Y;
		arrayLength = DriveTable.Drive_Lookup_Y.length;
		double moveValue = limitValue(moveSpeed, arrayPtr[arrayLength], arrayPtr[0]);

		for( int i = 0; i < arrayLength; i++) 
		{
			if(i+1 >= arrayLength || range(moveValue, arrayPtr[i], arrayPtr[i+1]))
			{
				//Assume match found
				if((i + 1) >= arrayLength)
				{
					returnIndex[1] = i;	
				}
				else
				{
					diff1 = Math.abs(moveValue - arrayPtr[i]);
					diff2 = Math.abs(moveValue - arrayPtr[i+1]);

					if(diff1 < diff2)
					{
						returnIndex[1] = i;
					}
					else
					{
						returnIndex[1] = i + 1;
					}
				}
				break;
			}
		}
		
		return returnIndex;
	}
	
	boolean range(double testValue, double bound1, double bound2) 
	{  
		return (((bound1 <= testValue) && (testValue <= bound2)) ||
				((bound1 >= testValue) && (testValue >= bound2)));
	}

	
	double limitValue(double testValue, double lowerBound, double upperBound)
	{
		if(testValue > upperBound)
		{
			return upperBound;
		}
		else if(testValue < lowerBound)
		{
			return lowerBound;
		}
		else
		{
			return testValue;
		}
	}
	
	double applyDeadZone(double speed)
	{
		double finalSpeed;
		
		if ( Math.abs(speed) < CONTROLLER_DEAD_ZONE) {
			finalSpeed = 0;
		}
		else {
			finalSpeed = speed;
		}
		return finalSpeed;
	}
}
