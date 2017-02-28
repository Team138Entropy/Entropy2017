package org.usfirst.frc.team138.robot.subsystems;

import org.usfirst.frc.team138.robot.RobotMap;

import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.Subsystem;

public class Shooter extends Subsystem{
	
	// Define Shooter Parts Here
	Talon shooter = new Talon(RobotMap.FUEL_SHOOTER_WHEEL_PORT);

	protected void initDefaultCommand() {
	}
	
	public void startShooter(){
		shooter.setSpeed(RobotMap.FUEL_SHOOT_SPEED);
	}
	
	public void stopShooter(){
		shooter.setSpeed(0.0);
	}
	
	public void releaseGate(){
		
	}
	
	public void engageGate(){
		
	}

}
