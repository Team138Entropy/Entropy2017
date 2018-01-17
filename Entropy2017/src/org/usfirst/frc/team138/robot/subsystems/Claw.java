package org.usfirst.frc.team138.robot.subsystems;

import org.usfirst.frc.team138.robot.RobotMap;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Subsystem;

public class Claw extends Subsystem{
	
	// Define Claw Parts Here
	Compressor compressor = new Compressor();
	Solenoid gripperSolenoid = new Solenoid(RobotMap.GEAR_GRIPPER_PORT);
	Solenoid wristSolenoid = new Solenoid(RobotMap.GEAR_WRIST_PORT);
	Solenoid chuteGuardSolenoid = new Solenoid(RobotMap.GEAR_CHUTE_GUARD_PORT);
	Solenoid ramSolenoid = new Solenoid(RobotMap.GEAR_RAM_PORT);
	Jaguar j = new Jaguar(4);
	
	boolean clawIsOpen = false;
	boolean wristIsUp = true;
	boolean guardIsUp = true;
	boolean ramExtended = false;

	protected void initDefaultCommand() {
	}
	
	public void stopCompressor()
	{
		compressor.stop();
	}
	
	public void startCompressor()
	{
		compressor.start();
	}
	public void slickNick()
	{
		j.set(.5);
	}
	public void slickNick2()
	{
		j.set(-.5);
	}
	public void slickNick3()
	{
		j.set(0);
	}
	public void extendRam(){
		ramSolenoid.set(true);
		ramExtended = true;
	}
	
	public void retractRam(){
		ramSolenoid.set(false);
		ramExtended = false;
	}
	
	public boolean ramExtended(){
		return ramExtended;
	}
	
	public void guardDown(){
		chuteGuardSolenoid.set(true);
		guardIsUp = false;
	}
	
	public void guardUp(){
		chuteGuardSolenoid.set(false);
		guardIsUp = true;
	}
	
	public boolean guardIsUp(){
		return guardIsUp;
	}
	
	public void wristUp(){
		wristSolenoid.set(false);
		wristIsUp = true;
	}
	
	public void wristDown(){
		wristSolenoid.set(true);
		wristIsUp = false;
	}
	
	public boolean wristIsUp() {
		return wristIsUp;
	}
	
	public void closeClaw(){
		gripperSolenoid.set(false);
		clawIsOpen = false;
	}
	
	public void openClaw(){
		gripperSolenoid.set(true);
		clawIsOpen = true;
	}
	
	public boolean clawIsOpen() {
		return clawIsOpen;
	}
}
