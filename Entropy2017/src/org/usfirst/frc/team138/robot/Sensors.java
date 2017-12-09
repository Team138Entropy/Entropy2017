package org.usfirst.frc.team138.robot;

import org.usfirst.frc.team138.robot.subsystems.vision2017.Entropy2017Targeting;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Relay;
//import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Sensors {
	public static ADXRS450_Gyro gyro; 
	
	
	static Encoder leftEncoder;
	static Encoder rightEncoder;
	
	static UsbCamera gearCamera;
	static Relay cameraLight = new Relay(RobotMap.GEAR_CAMERA_LIGHT_PORT);
	static UsbCamera groundCamera;
	public static Entropy2017Targeting cameraProcessor;
	
	public static double gyroBias=0;
	
	public static void initialize() {
        gyro = new ADXRS450_Gyro();
        gyro.calibrate();
        gyro.reset();
        
        leftEncoder = new Encoder(RobotMap.LEFT_ENCODER_PORT_A, RobotMap.LEFT_ENCODER_PORT_B);
		rightEncoder = new Encoder(RobotMap.RIGHT_ENCODER_PORT_A, RobotMap.RIGHT_ENCODER_PORT_B);
    	leftEncoder.setDistancePerPulse(0.124);
    	rightEncoder.setDistancePerPulse(0.124);
    	resetEncoders();
    	
    	gearCamera = CameraServer.getInstance().startAutomaticCapture("Gear Feed", 0);
        gearCamera.setResolution(320, 240);
        gearCamera.setFPS(20);
        
        groundCamera = CameraServer.getInstance().startAutomaticCapture("Ground Feed", 1);
        groundCamera.setResolution(320, 240);
        groundCamera.setFPS(20);
    	
    	cameraProcessor = new Entropy2017Targeting(gearCamera, groundCamera);
		cameraProcessor.start();
		// Nominal gyro bias, assumes robot is facing "forward" (+Y = 90 Degrees)
		// at initialization
		gyroBias=getRobotHeading()-90;

	}
	
	public static void setCameraLight(boolean on)
	{
		if (on)
		{
			cameraLight.set(Relay.Value.kForward);
		}
		else
		{
			cameraLight.set(Relay.Value.kOff);
		}
	}
	
	public static void targetingCameraMode()
	{
		gearCamera.setExposureManual(0);
		gearCamera.setBrightness(0);
	}
	
	public static void standardCameraMode()
	{
		gearCamera.setBrightness(20);
		gearCamera.setExposureAuto();
	}
	
	public static double getLeftDistance() {
		return leftEncoder.getDistance();
	}
	
	public static double getRightDistance() {
		return rightEncoder.getDistance();
	}
	
	public static void resetEncoders() {
		leftEncoder.reset();
		rightEncoder.reset();
	}
	
	public static void alignRobotHeading (double Angle) {
		gyroBias=gyroBias + Constants.gyroAlpha * (Utility.diffAngles(getRobotHeading(), Angle));
	}

	public static double getRobotHeading() {
		// Return current robot heading in Field Coordinates
		// and wrapped to +/- 180 degree range.
		// "0" Degrees is to the right in Field Coordinates
		double heading=Constants.gyroDir*gyro.getAngle() + Constants.gyroOffset - gyroBias;
		// gyro accumulates angles over multiple rotations,
		// heading needs to be wrapped to range +/- 180 in order
		// to be compared to joystick heading
		heading=Utility.angleWrap(heading);	
		return heading;
	}
	
	public static double getRobotHeadingRate() {
		// Return current robot rate of rotation (
		double rate=Constants.gyroDir * gyro.getRate();
		return rate;
	}
	
	
	
	public static void updateSmartDashboard(){
		double [] userCmd;
		
		if (Robot.claw.clawIsOpen())
		{
			SmartDashboard.putString("Claw State:", "Open");
		}
		else
		{
			SmartDashboard.putString("Claw State:", "Closed");
		}
		
		if (Robot.claw.wristIsUp())
		{
			SmartDashboard.putString("Wrist Position:", "Up");
		}
		else
		{
			SmartDashboard.putString("Wrist Position:", "Down");
		}
		
		if (Robot.claw.guardIsUp())
		{
			SmartDashboard.putString("Guard Position:", "Up");
		}
		else
		{
			SmartDashboard.putString("Guard Position:", "Down");
		}
		
		if (Robot.claw.ramExtended())
		{
			SmartDashboard.putString("Ram Position:", "Extended");
		}
		else
		{
			SmartDashboard.putString("Ram Position:", "Retracted");
		}
		SmartDashboard.putNumber("Left Encoder:", leftEncoder.getDistance());
		SmartDashboard.putNumber("Right Encoder:", rightEncoder.getDistance());
		// User command (joystick)
		userCmd = OI.getFieldCommand();
		SmartDashboard.putNumber("Cmd Angle:", userCmd[1]);
		SmartDashboard.putNumber("Magn:", userCmd[0]);
		// Robot heading (in Field Coordinates)
		SmartDashboard.putNumber("Gyro Bias:", gyroBias);
		SmartDashboard.putNumber("Robot Heading:", getRobotHeading());
		SmartDashboard.putNumber("Rotation Rate:", getRobotHeadingRate());
	}
}
