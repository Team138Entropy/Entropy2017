package org.usfirst.frc.team138.robot.commands;

import org.usfirst.frc.team138.robot.Robot;
import java.util.ArrayList;
import org.usfirst.frc.team138.robot.Sensors;
import org.usfirst.frc.team138.robot.OI;
import org.usfirst.frc.team138.robot.subsystems.vision2017.Entropy2017Targeting;

import edu.wpi.first.wpilibj.command.Command;

public class VisionCorrect2Step extends Command {
	
	AutoDrive driveCommandPart1, driveCommandPart2, driveCommandPart3;
	boolean isDone = false;
	int framesToAverage;
	int counter = 0;
	int currentPart = 1;
	ArrayList<Entropy2017Targeting.TargetInformation> infoList = new ArrayList<Entropy2017Targeting.TargetInformation>();
	
	public VisionCorrect2Step(int numFrames){
		this.setInterruptible(false);
		requires(Robot.drivetrain);
		requires(Robot.claw);
		framesToAverage = numFrames;
	}

	protected void initialize() {
		Sensors.cameraProcessor.processFrames(framesToAverage, true);
		isDone = false;
		driveCommandPart1 = null;
		driveCommandPart2 = null;
		driveCommandPart3 = null;
		currentPart = 1;
		counter = 0;
	}

	protected void execute() {
		if (OI.autoRoutinesCancelled())
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
			if (driveCommandPart1 == null)
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
						cumulation.divideAll(targetsFound);
						double degreesPerRadian = 57.2958;
						double distanceFromTarget = 5 / Math.tan((cumulation.height / Entropy2017Targeting.pixelsPerYDegree) / degreesPerRadian);
						double angle = Math.acos((cumulation.gap * cumulation.pixelsPerInch) / 6.25) * degreesPerRadian;
						if (cumulation.rightOfTarget)
						{
							driveCommandPart1 = new AutoDrive(cumulation.correctionAngle - angle);
						}
						else
						{
							driveCommandPart1 = new AutoDrive(cumulation.correctionAngle + angle);
						}
						
						driveCommandPart2 = new AutoDrive(0.75, (distanceFromTarget * 0.5) / Math.cos(angle / degreesPerRadian));
						if (cumulation.rightOfTarget)
						{
							driveCommandPart3 = new AutoDrive(90 - angle);
						}
						else
						{
							driveCommandPart3 = new AutoDrive(angle - 90);
						}
						driveCommandPart1.initialize();
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
				System.out.println("Vision Correction Step: " + currentPart);
				if (currentPart == 1)
				{
					driveCommandPart1.execute();
					if (driveCommandPart1.isFinished())
					{
						driveCommandPart2.initialize();
						currentPart = 2;
					}
				}
				else if (currentPart == 2)
				{
					driveCommandPart2.execute();
					if (driveCommandPart2.isFinished())
					{
						driveCommandPart3.initialize();
						currentPart = 3;
					}
				}
				else if (currentPart == 3)
				{
					driveCommandPart3.execute();
					if (driveCommandPart3.isFinished())
					{
						currentPart = 4;
					}
				}
			}
		}
	}

	protected boolean isFinished() {
		if (driveCommandPart1 != null)
		{
			return currentPart > 3 || isDone;
		}
		else
		{
			return isDone;
		}
	}

	protected void end() {
		Sensors.cameraProcessor.cancelProcessing();
		infoList.clear();
		
		System.out.println("VisionCorrect Ended");
	}

	protected void interrupted() {
		end();
	}

}