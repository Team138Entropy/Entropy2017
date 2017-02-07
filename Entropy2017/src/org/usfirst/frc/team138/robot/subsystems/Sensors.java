package org.usfirst.frc.team138.robot.subsystems;

import org.usfirst.frc.team138.robot.RobotMap;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Encoder;

public class Sensors {
	public static ADXRS450_Gyro gyro;
	static double prevAngle = 0;
	
	static Encoder leftEncoder;
	static Encoder rightEncoder;
	
	public Sensors() {
        gyro = new ADXRS450_Gyro();
        gyro.calibrate();
        gyro.reset();
        
        leftEncoder = new Encoder(RobotMap.LEFT_ENCODER_PORT_A, RobotMap.LEFT_ENCODER_PORT_B);
		rightEncoder = new Encoder(RobotMap.RIGHT_ENCODER_PORT_A, RobotMap.RIGHT_ENCODER_PORT_B);
    	leftEncoder.setDistancePerPulse(0.1);
    	rightEncoder.setDistancePerPulse(0.1);
    	resetEncoders();
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
}
