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
	static Relay cameraLight = new Relay(RobotMap.GEAR_CAMERA_LIGHT_PORT);
	static UsbCamera groundCamera;
	public static Entropy2017Targeting cameraProcessor;
	
	public static void initialize() {
        gyro = new ADXRS450_Gyro();
        gyro.calibrate();
        gyro.reset();
        
        leftEncoder = new Encoder(RobotMap.LEFT_ENCODER_PORT_A, RobotMap.LEFT_ENCODER_PORT_B);
		rightEncoder = new Encoder(RobotMap.RIGHT_ENCODER_PORT_A, RobotMap.RIGHT_ENCODER_PORT_B);
    	leftEncoder.setDistancePerPulse(0.165);
    	rightEncoder.setDistancePerPulse(0.165);
    	resetEncoders();
    	
    	gearCamera = CameraServer.getInstance().startAutomaticCapture("Gear Feed", 0);
        gearCamera.setResolution(320, 240);
        gearCamera.setFPS(20);
        
        groundCamera = CameraServer.getInstance().startAutomaticCapture("Ground Feed", 1);
        groundCamera.setResolution(320, 240);
        groundCamera.setFPS(20);
    	
    	cameraProcessor = new Entropy2017Targeting(gearCamera, groundCamera);
		cameraProcessor.start();
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
