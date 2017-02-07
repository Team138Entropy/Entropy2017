package org.usfirst.frc.team138.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;

public class Claw extends Subsystem{
	
	// Define Claw Parts Here
	//
	
	boolean isOpen = false;

	protected void initDefaultCommand() {
	}
	
	public void acquireGear(){
		isOpen = false;
	}
	
	public void releaseGear(){
		isOpen = true;
	}
	
	public boolean isOpen() {
		return isOpen;
	}
}
