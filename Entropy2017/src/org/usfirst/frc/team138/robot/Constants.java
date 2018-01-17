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
		public static double headingGain = .05; // .07
		
		// Derivative Gain applied to heading error
		// commands rotateSpeed = headingGain * headingError - RotationRate * headingVelGain
		public static double headingVelGain = 0.0; // 

		// Integral Gain applied to heading error
		// commands rotateSpeed = headingGain * headingError - RotationRate * headingVelGain + cumHeadingError * headingIntGain
		public static double headingIntGain = 0.0; // 

		// Limit max rotate speed
		public static double maxRotateSpeed = .5; // 
		
		// Reverse or Turn - decision criteria
		// If abs(headingError) > turnRange, then drive in reverse while aligning
		// rear of robot with cmd heading.
		// Otherwise, turn to align front of robot with cmd heading
		public final static double turnRange = 95; // Degrees
		
		// When driving in Field Coordinates,  the normal move speed (not rotate speed)
		// is restricted to +/-maxSlowMoveSpeed.  However, if the "high speed" button
		// is pressed, the restriction on moveSpeed is lifted (max is now full speed)
		public static double maxSlowMoveSpeed = 0.5; // 
		
		
		// rotateFdFwdBias - apply a bias to the left/right motor speed command
		// based on sign(<speed>).  Do Not apply the bias if the abs(speed)<minSpeed
		public static double headingFdFwdBias = 0; // units are fractions of full speed
		
		// headingMinBiasSpeed - do NOT apply headingFdFwdBias to <speed> commands if 
		// abs(<speed>) < headingMinBiasSpeed.  This avoids chatter when the joystick is near center.
		public static double headingMinBiasSpeed = 0.01; // units are fractions of full speed

		// Low pass filter on joystick heading - 
		// Filter eqn:  heading(i+1) =joystickDir(i)*Alpha + (1-Alpha)*(heading(i)
		// where Alpha = Ts*2*pi*Freq
		//   Ts is sample period (20 mSec for FRC)
		//   Freq is location of filter pole in Hz
		public static double rotateAlpha = .02*6.28*1;
		
		// Gain Factor that boosts PID gains applied to heading error when Zero Turn button
		// is pressed.
		public static double zeroTurnGainFactor = 3.0;

		// rotateMaxSpeed- limit on rotate speed when movespeed is non-zero
		public final static double rotateMaxSpeed = .1; 

		// Max speed allowed during Zero Turn.   For normal moves (non-zero movespeed), 
		// the max rotate speed is severely limited to avoid rapid changes in heading.
		// However, during zero turn, much greater voltage commands are required to overcome
		// stiction in the drive train.  Therefore, if the zero turn button is pressed,
		// the limits on rotate speed are increased.
		public static double zeroTurnMaxSpeed = 1.0;
		
		// Normally, driveWithFieldCoord will try to align robot heading with the joystick direction.
		// However, if the headingError is large than revRange, the rear of the robot will be aligned
		// with the joystick direction (ie: move in reverse).
		public static double revRange = 200;
		
		// Hysteresis is included in the reverse logic.  If the headingError is close to the fwd/rev
		// threshold, we add hysteresis to avoid chatter.  If the robot was moving forward and the 
		// headingErrof exceeds revRange, then the robot will start moving in reverse and the threshold
		// between fwd/rev is changed to revHyst.  revHyst should always be less than revRange.
		public static double revHyst = 125;
}
