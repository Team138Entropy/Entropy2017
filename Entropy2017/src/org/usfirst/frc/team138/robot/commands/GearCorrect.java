package org.usfirst.frc.team138.robot.commands;

import edu.wpi.first.wpilibj.command.Command;

public class GearCorrect extends Command {
	
	Command driveCommand;
	public GearCorrect(){
	}

	protected void initialize() {
		driveCommand = new AutoDrive(0/*get from camera*/);
		driveCommand.start();
	}

	protected void execute() {
		
	}

	protected boolean isFinished() {
		return !driveCommand.isRunning();
	}

	protected void end() {
	}

	protected void interrupted() {
	}

}