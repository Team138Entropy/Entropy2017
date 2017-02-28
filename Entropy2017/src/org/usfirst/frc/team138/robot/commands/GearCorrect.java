package org.usfirst.frc.team138.robot.commands;

import org.usfirst.frc.team138.robot.Robot;
import java.util.ArrayList;
import org.usfirst.frc.team138.robot.Sensors;
import org.usfirst.frc.team138.robot.subsystems.vision2017.Entropy2017Targeting;

import edu.wpi.first.wpilibj.command.Command;

public class GearCorrect extends Command {
	
	AutoDrive driveCommand;
	boolean isDone = false;
	int framesToAverage;
	int counter = 0;
	ArrayList<Entropy2017Targeting.TargetInformation> infoList = new ArrayList<Entropy2017Targeting.TargetInformation>();
	
	public GearCorrect(int numFrames){
		framesToAverage = numFrames;
		this.setInterruptible(false);
	}

	protected void initialize() {
		Sensors.cameraProcessor.processFrames(framesToAverage, true);
		isDone = false;
		driveCommand = null;
		counter = 0;
	}

	protected void execute() {
		if (driveCommand == null)
		{
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
					driveCommand = new AutoDrive(cumulation.correctionAngle / targetsFound);
					System.out.println("Correction Angle: " +  cumulation.correctionAngle / targetsFound);
					System.out.println("Target Peg X: " + cumulation.aimX / targetsFound);
					System.out.println("Real Peg X: " + cumulation.x / targetsFound);
					//isDone = true;
					
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
		System.out.println("GearCorrect Ended");
		infoList.clear();
	}

	protected void interrupted() {
		Sensors.cameraProcessor.cancelProcessing();
		end();
	}

}