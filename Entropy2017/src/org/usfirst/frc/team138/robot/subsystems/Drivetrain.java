package org.usfirst.frc.team138.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.RobotDrive;
import org.usfirst.frc.team138.robot.commands.TeleopDrive;
import com.ctre.CANTalon;

import org.usfirst.frc.team138.robot.Constants;
import org.usfirst.frc.team138.robot.OI;
import org.usfirst.frc.team138.robot.RobotMap;
import org.usfirst.frc.team138.robot.Sensors;
import org.usfirst.frc.team138.robot.Utility;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;



public class Drivetrain extends Subsystem{
	private static double CONTROLLER_DEAD_ZONE = 0.09;
	

	RobotDrive drivetrain;
	
	// Integral heading error (used in Field Coordinates)
	private static double cumHeadingError=0;
	
	
	protected void initDefaultCommand() {		
		CANTalon frontLeftTalon = new CANTalon(RobotMap.LEFT_MOTOR_CHANNEL_FRONT);
		CANTalon backLeftTalon = new CANTalon(RobotMap.LEFT_MOTOR_CHANNEL_BACK);
		CANTalon frontRightTalon = new CANTalon(RobotMap.RIGHT_MOTOR_CHANNEL_FRONT);
		CANTalon backRightTalon = new CANTalon(RobotMap.RIGHT_MOTOR_CHANNEL_BACK);
				
		drivetrain = new RobotDrive(frontLeftTalon, backLeftTalon,
				frontRightTalon, backRightTalon);
		
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
	
	public void driveWithFieldCoord() {
		// use arcadeDrive to drive with Field Coordinates
		double [] userCmd=OI.getFieldCommand();
		double headingError=0;
		double rotateSpeed=0;
		double moveSpeed=0;
		double leftSpeed=0;
		double rightSpeed=0;
		double totalSpeed=0;
		
		if (userCmd[0]>Constants.joystickDeadband)
		{ // Nothing to do if magnitude is within deadband of 0,
			// if Magnitude > deadBand, a move is required
			headingError = Utility.diffAngles(userCmd[1], Sensors.getRobotHeading());
			cumHeadingError+=Constants.Ts * headingError;

			if (OI.isZeroTurn())
			{ // Do zero turn
				// We always rotate to align robot heading with joystick when zero turn button
				// is depressed
				moveSpeed=0;
			}
			else
			{ // Move (and possibly rotate to align heading)
				if (Math.abs(headingError) <= Constants.turnRange)
				{ // Align robot front with cmd heading
					moveSpeed=userCmd[0] * Constants.moveSpeedScale;
				}
				else
				{ // Align rear of robot with cmd heading:
					// Woops - need to "remove" the increment to cumHeadingError added above
					cumHeadingError -= Constants.Ts * headingError;
					// Add 180 to current heading, wrap to range of +/-180
					// then unwrap difference with command direction to result in headingError
					headingError = Utility.diffAngles(userCmd[1], Utility.angleWrap(Sensors.getRobotHeading()+180));
					// Now update cumHeadingError with corrected headingError
					cumHeadingError+=Constants.Ts * headingError;
					// userCmd[0] is magnitude of speed, 
					// since we're moving backwards (relative to robot), need to invert
					// sign of moveSpeed to command wheels in reverse.
					moveSpeed=-userCmd[0] * Constants.moveSpeedScale;
				}
			}
			// PID control to align robot with user heading cmd
			rotateSpeed=Constants.rotateSpeedScale*( headingError * Constants.headingGain  // Proportional Gain
					- Sensors.getRobotHeadingRate() * Constants.headingVelGain            // Derivative Gain (applied to gyro rate only, therefore "-sign")
					+ cumHeadingError * Constants.headingIntGain);                         // Integral Gain
			rotateSpeed=limitValue(rotateSpeed, -Constants.maxRotateSpeed, Constants.maxRotateSpeed);			
			
			// Tank Drive permits independent control over left and right wheel speeds
			// This is required to be able to command ZeroTurn moves.
			leftSpeed=moveSpeed-rotateSpeed;
			rightSpeed=moveSpeed+rotateSpeed;
			// Constrain vector magnitude of wheel speeds to range of +/- 1.0;
			totalSpeed=Math.sqrt(leftSpeed*leftSpeed + rightSpeed*rightSpeed);
			if (totalSpeed>1)
			{
				leftSpeed=leftSpeed/totalSpeed;
				rightSpeed=rightSpeed/totalSpeed;
			}
			// Apply Bias to overcome stiction, but only if speed > minSpede
			if (leftSpeed > Constants.headingMinBiasSpeed)
				leftSpeed += Constants.headingFdFwdBias;
			if (leftSpeed < -Constants.headingMinBiasSpeed)
				leftSpeed -= Constants.headingFdFwdBias;
			if (rightSpeed > Constants.headingMinBiasSpeed)
				rightSpeed += Constants.headingFdFwdBias;
			if (rightSpeed < -Constants.headingMinBiasSpeed)
				rightSpeed -= Constants.headingFdFwdBias;
		
			drivetrain.tankDrive(leftSpeed, rightSpeed);
			SmartDashboard.putNumber("Heading Error:", headingError);
		}
		SmartDashboard.putNumber("Left Speed:", leftSpeed);
		SmartDashboard.putNumber("Right Speed:", rightSpeed);
	}
		
	public void driveWithTable(double moveSpeed, double rotateSpeed)
	{
		cumHeadingError=0; // Zero cumulative heading error when not using Field Coord
		//rotateSpeed = -rotateSpeed;
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

		return DriveTable.Drive_Matrix_2017[indices[1]][indices[0]];
	}
	
	double getRightMotorSpeed(double moveSpeed, double rotateSpeed)
	{
		int[] indices = {16, 16};

		indices = getIndex(moveSpeed, rotateSpeed);
		indices[0] = 32 - indices[0];

		return DriveTable.Drive_Matrix_2017[indices[1]][indices[0]];
	}
	
	int[] getIndex(double moveSpeed, double rotateSpeed)
	{		
		double diff1 = 0;
		double diff2 = 0;
		// [0] is x, [1] is y
		int[] returnIndex = {0, 0};

		double[] arrayPtr = DriveTable.Drive_Lookup_X;
		int arrayLength = DriveTable.Drive_Lookup_X.length;

		double rotateValue = limitValue(rotateSpeed, arrayPtr[0], arrayPtr[arrayLength-1]);

		for(int i = 0; i < arrayLength; i++) 
		{
			if(i+1 >= arrayLength || inRange(rotateValue, arrayPtr[i], arrayPtr[i+1]))
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
		double moveValue = limitValue(moveSpeed, arrayPtr[0], arrayPtr[arrayLength - 1]);

		for( int i = 0; i < arrayLength; i++) 
		{
			if(i+1 >= arrayLength || inRange(moveValue, arrayPtr[i], arrayPtr[i+1]))
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
	
	boolean inRange(double testValue, double bound1, double bound2) 
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
