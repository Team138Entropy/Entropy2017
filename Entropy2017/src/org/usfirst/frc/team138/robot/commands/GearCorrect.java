package org.usfirst.frc.team138.robot.commands;

import org.usfirst.frc.team138.robot.Sensors;

import edu.wpi.first.wpilibj.command.Command;

public class GearCorrect extends Command {
	
	Command driveCommand;
	int framesToAverage;
	ArrayList<Double> angles
	
	public GearCorrect(int numFrames){
		framesToAverage = numFrames;
	}

	protected void initialize() {
		Sensors.targetingCameraMode();
		Sensors.cameraProcessor.processFrames(framesToAverage);
	}

	protected void execute() {
		
		if (false)
		{
			driveCommand = new AutoDrive(0/*get from camera*/);
			driveCommand.start();
		}
	}

	protected boolean isFinished() {
		return !driveCommand.isRunning();
	}

	protected void end() {
		Sensors.standardCameraMode();
	}

	protected void interrupted() {
		end();
	}

}