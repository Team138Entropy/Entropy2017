package org.usfirst.frc.team138.robot;

import java.util.ArrayList;

import org.usfirst.frc.team138.robot.subsystems.vision2017.Entropy2017Targeting;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Sensors {
	public static ADXRS450_Gyro gyro;
	static double prevAngle = 0;
	
	static Encoder leftEncoder;
	static Encoder rightEncoder;
	
	static UsbCamera camera;
	public static Entropy2017Targeting cameraProcessor;
	
	//static Servo cameraServo = new Servo(RobotMap.CAMERA_TILT_PORT);
	
	public static void initialize() {
        gyro = new ADXRS450_Gyro();
        gyro.calibrate();
        gyro.reset();
        
        leftEncoder = new Encoder(RobotMap.LEFT_ENCODER_PORT_A, RobotMap.LEFT_ENCODER_PORT_B);
		rightEncoder = new Encoder(RobotMap.RIGHT_ENCODER_PORT_A, RobotMap.RIGHT_ENCODER_PORT_B);
    	leftEncoder.setDistancePerPulse(0.124);
    	rightEncoder.setDistancePerPulse(0.124);
    	resetEncoders();
    	
    	camera = CameraServer.getInstance().startAutomaticCapture();
        camera.setResolution(640, 480);
        camera.setFPS(12);
        System.out.println(camera.getBrightness());
    	
    	cameraProcessor = new Entropy2017Targeting();
		cameraProcessor.start();
	}
	
	public static void targetingCameraMode()
	{
		camera.setExposureManual(0);
        camera.setBrightness(0);
	}
	
	public static void standardCameraMode()
	{
		camera.setExposureAuto();
        camera.setBrightness(50);
	}
	
	public static void gearAcqTiltAngle()
	{
		//cameraServo.set(0.0);
	}
	
	public static void gearPlaceTiltAngle()
	{
		//cameraServo.set(0.2);
	}
	
	public static void setCurrentPos() {
		prevAngle = gyro.getAngle();
	}
	
	public static double getRelativeAngle() {
		return gyro.getAngle() - prevAngle;
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
	
	public static boolean haveGear() {
		return true;
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
