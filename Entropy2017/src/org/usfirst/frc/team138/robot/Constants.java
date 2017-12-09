package org.usfirst.frc.team138.robot;
/*
 * Constant values used throughout robot code.
 * In "C" this would be a header file, but alas, this is Java
 */
public class Constants {

	// System Constants
		public final static double Ts=.02; // Sample period, in seconds
	
		// DriveMotor PID Configuration
		public final static int ClosedLoop = 0;
		public final static double MOTOR_KP = 1.0;
		public final static double MOTOR_KI = 0.0; // .02
		public final static double MOTOR_KD = 0.0;
		public final static double MOTOR_KF = 2.0;
		// DriveMotor Current Limit
		public final static int CURRENT_LIMIT = 20;
		
		// Enable/Disable Gyro
		public final static boolean USE_GYRO = true;
		// Enable/Disable IMU 
		public final static boolean USE_IMU = false;
		
		// Deadband applied to Joystick, when
		// magnitude is less than deadBand, then set Magnitude to 0
		public final static double joystickDeadband = 0.09;

	
		// Offset to align heading reported by gyro with Field Coordinates
		// heading should report "90" when robot is facing away from operator
		// ie: when robot is facing in +Y direction of Field coordinates.
		// Heading = gyro + gyroOffset
		public final static double gyroOffset = 90; // Degrees
		
		// gyroDirection corrects heading returned by gyro to increase when rotating
		// CCW - conventional Cartesian coordinate system where +rotation is defined by
		// right hand rule as CCW about +Z axis.  If X is to the right and +Y is ahead, then
		// +Z is pointed up from playing field and therefore yaw rotation is CCW about Z axis.
		public final static double gyroDir = -1; // +/-1
		
		// Low pass filter on gyro Bias - simple integrator that nulls
		// difference between reported heading and 90 degrees when user
		// presses the "alignHeading" button on driverStick
		// Filter eqn:  gyroBias(i+1) = gyroBias(i) + Alpha * (Heading - 90)
		// where Alpha = Ts*2*pi*Freq
		//   Ts is sample period (20 mSec for FRC)
		//   Freq is location of filter pole in Hz
		public final static double gyroAlpha = .02*6.28*2; // ~ 2 Hz @ 20 mSec sample rate

		// Proportional Gain applied to heading error
		// commands rotateSpeed = headingGain * headingError
		public static double headingGain = .25; // .07
		
		// Derivative Gain applied to heading error
		// commands rotateSpeed = headingGain * headingError - RotationRate * headingVelGain
		public static double headingVelGain = 0.02; // 

		// Integral Gain applied to heading error
		// commands rotateSpeed = headingGain * headingError - RotationRate * headingVelGain + cumHeadingError * headingIntGain
		public static double headingIntGain = 0; // 

		// Limit max rotate speed
		public static double maxRotateSpeed = 1; // Degrees_per_second
		
		// Reverse or Turn - decision criteria
		// If abs(headingError) > turnRange, then drive in reverse while aligning
		// rear of robot with cmd heading.
		// Otherwise, turn to align front of robot with cmd heading
		public final static double turnRange = 95; // Degrees
		
		// moveSpeedScale - moveSpeed sent to arcadeDrive when Magnitude = 1;
		public static double moveSpeedScale = 1; // units?
		
		// rotateSpeedScale - rotateSpeed sent to arcadeDrive when Magnitude = 1;
		public final static double rotateSpeedScale = 1; // units?
		
		// rotateFdFwdBias - apply a bias to the left/right motor speed command
		// based on sign(<speed>).  Do Not apply the bias if the abs(speed)<minSpeed
		public static double headingFdFwdBias = 0; // units are fractions of full speed
		
		// headingMinBiasSpeed - do NOT apply headingFdFwdBias to <speed> commands if 
		// abs(<speed>) < headingMinBiasSpeed.  This avoids chatter when the joystick is near center.
		public static double headingMinBiasSpeed = 0.01; // units are fractions of full speed

}
