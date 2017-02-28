package org.usfirst.frc.team138.robot;

import org.usfirst.frc.team138.robot.subsystems.vision2017.Entropy2017Targeting;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Sensors {
	public static ADXRS450_Gyro gyro;
	
	static Encoder leftEncoder;
	static Encoder rightEncoder;
	
	static UsbCamera gearCamera;
	//static Servo gearCameraServo = new Servo(RobotMap.CAMERA_TILT_PORT);
	static Relay gearCameraLight = new Relay(RobotMap.GEAR_CAMERA_LIGHT_PORT);
	static UsbCamera ropeAndShooterCamera;
	public static Entropy2017Targeting cameraProcessor;
	
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
        gearCamera.setResolution(640, 480);
        gearCamera.setFPS(12);
        
        ropeAndShooterCamera = CameraServer.getInstance().startAutomaticCapture("Rope and Shooter Feed", 1);
        ropeAndShooterCamera.setResolution(640, 480);
        ropeAndShooterCamera.setFPS(12);
    	
    	cameraProcessor = new Entropy2017Targeting(gearCamera, ropeAndShooterCamera);
		cameraProcessor.start();
	}
	
	public static void turnOnCameraLight(boolean gear)
	{
		if (gear)
		{
			gearCameraLight.set(Relay.Value.kForward);
		}
		else
		{
			gearCameraLight.set(Relay.Value.kForward);
		}
	}
	
	public static void turnOffCameraLight(boolean gear)
	{
		if (gear)
		{
			gearCameraLight.set(Relay.Value.kOff);
		}
		else
		{
			gearCameraLight.set(Relay.Value.kOff);
		}
	}
	
	public static void targetingCameraMode(boolean gear)
	{
		if (gear)
		{
			gearCamera.setExposureManual(0);
	        gearCamera.setBrightness(0);
		}
		else
		{
			ropeAndShooterCamera.setExposureManual(0);
			ropeAndShooterCamera.setBrightness(0);
		}
	}
	
	public static void standardCameraMode(boolean gear)
	{
		if (gear)
		{
			gearCamera.setBrightness(20);
			gearCamera.setExposureAuto();
		}
		else
		{
			ropeAndShooterCamera.setBrightness(20);
			ropeAndShooterCamera.setExposureAuto();
		}
	}
	
	public static void gearAcqTiltAngle()
	{
		//gearCameraServo.set(0.0);
	}
	
	public static void gearPlaceTiltAngle()
	{
		//gearCameraServo.set(0.2);
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
	
	public static void updateSmartDashboard(){
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
		SmartDashboard.putNumber("Angle:", gyro.getAngle());
	}
}
