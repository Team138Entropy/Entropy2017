package org.usfirst.frc.team138.robot.commands;

import org.usfirst.frc.team138.robot.Robot;
import java.util.ArrayList;
import org.usfirst.frc.team138.robot.Sensors;
import org.usfirst.frc.team138.robot.subsystems.vision2017.Entropy2017Targeting;

import edu.wpi.first.wpilibj.command.Command;

public class VisionCorrect extends Command {
	
	AutoDrive driveCommand;
	boolean isDone = false;
	int framesToAverage;
	int counter = 0;
	boolean processingForPeg;
	ArrayList<Entropy2017Targeting.TargetInformation> infoList = new ArrayList<Entropy2017Targeting.TargetInformation>();
	
	public VisionCorrect(boolean processingForPeg, int numFrames){
		this.setInterruptible(false);
		requires(Robot.drivetrain);
		requires(Robot.claw);
		this.processingForPeg = processingForPeg;
		framesToAverage = numFrames;
	}

	protected void initialize() {
		Robot.claw.stopCompressor();
		Sensors.cameraProcessor.processFrames(framesToAverage, processingForPeg);
		isDone = false;
		driveCommand = null;
		counter = 0;
	}

	protected void execute() {
		if (Robot.oi.autoRoutinesCancelled())
		{
			if (getGroup() != null)
			{
				getGroup().cancel();
			}
			else
			{
				isDone = true;	
			}
		}
		else
		{
			if (driveCommand == null)
			{
				Robot.drivetrain.drive(0, 0);
				infoList.addAll(Sensors.cameraProcessor.getTargetInformation());
				if (infoList.size() == framesToAverage)
				{
					Entropy2017Targeting.TargetInformation cumulation = new Entropy2017Targeting.TargetInformation();
					int targetsFound = framesToAverage;
					for (Entropy2017Targeting.TargetInformation info : infoList)
					{
						if (info.targetFound)
						{
							cumulation.add(info);
						}
						else
						{
							targetsFound--;
						}
					}
					if (targetsFound > 0)
					{
						System.out.println("Correction Angle: " +  cumulation.correctionAngle / targetsFound);
						
						driveCommand = new AutoDrive(cumulation.correctionAngle / targetsFound);
						driveCommand.initialize();
					}
					else
					{
						if (getGroup() != null && Robot.mode == "teleop")
						{
							getGroup().cancel();
						}
						else
						{
							isDone = true;	
						}
					}
				}
			}
			else
			{
				driveCommand.execute();
			}
		}
	}

	protected boolean isFinished() {
		if (driveCommand != null)
		{
			return driveCommand.isFinished() || isDone;
		}
		else
		{
			return isDone;
		}
	}

	protected void end() {
		Robot.claw.startCompressor();
		Sensors.cameraProcessor.cancelProcessing();
		infoList.clear();
		
		System.out.println("VisionCorrect Ended");
	}

	protected void interrupted() {
		end();
	}

}