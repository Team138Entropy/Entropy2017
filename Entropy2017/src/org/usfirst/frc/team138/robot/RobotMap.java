package org.usfirst.frc.team138.robot;
/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
	
	// CAN Bus Assignments
	public final static int LEFT_MOTOR_CHANNEL_FRONT = 2;
	public final static int LEFT_MOTOR_CHANNEL_BACK = 3;
	public final static int RIGHT_MOTOR_CHANNEL_FRONT = 4;
	public final static int RIGHT_MOTOR_CHANNEL_BACK = 5;
	
	// PWM
	public final static int ROPE_CLIMBING_WINCH_PORT = 0;
	public final static int CAMERA_TILT_PORT = 5;
	public final static int FUEL_CONVEYER_PORT = 7;
	public final static int FUEL_SHOOTER_WHEEL_PORT = 9;
	public final static double ROPE_PULSE_SPEED = 0.6;
	public final static double ROPE_CLIMB_SPEED = 1.0;
	public final static double FUEL_CONVEYER_SPEED = 0.5;
	public final static double FUEL_ACQ_SPEED = -0.5;
	public final static double FUEL_SHOOT_SPEED = 0.8;
	
	// GPIO
	public final static int LEFT_ENCODER_PORT_A = 0;
	public final static int LEFT_ENCODER_PORT_B = 1;
	public final static int RIGHT_ENCODER_PORT_A = 3;
	public final static int RIGHT_ENCODER_PORT_B = 2;
	public final static int GEAR_DETECTOR_PORT = 9;
	
	// Analog Input
	public final static int RANGEFINDER_PORT = 0;
	public final static int GEAR_DETECTOR_BEAM_PORT = 1;
	public final static int SHOOTER_ENCODER_PORT = 2;
	
	// Pneumatic Control Module
	public final static int GEAR_GRIPPER_PORT = 0;
	public final static int GEAR_WRIST_PORT = 1;
	public final static int GEAR_CHUTE_GUARD_PORT = 2;
	public final static int GEAR_RAM_PORT = 3;
	public final static int SHOOTER_GATE_PORT = -1;
	public final static int ROPE_GRABBING_SOLENOID_PORT = 4;
	
	// Relay
	public final static int GEAR_CAMERA_LIGHT_PORT = 0;
	public final static int SHOOTER_CAMERA_LIGHT_PORT = 1;
}
