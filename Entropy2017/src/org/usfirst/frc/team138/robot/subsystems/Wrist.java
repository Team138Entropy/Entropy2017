package org.usfirst.frc.team138.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;

public class Wrist extends Subsystem{
	
	// Define Wrist Parts Here
	//
	
	boolean isUp = false;

	protected void initDefaultCommand() {
	}
	
	public void setUp(){
		isUp = true;
	}
	
	public void setDown(){
		isUp = false;
	}
	
	public boolean isUp() {
		return isUp;
	}
}