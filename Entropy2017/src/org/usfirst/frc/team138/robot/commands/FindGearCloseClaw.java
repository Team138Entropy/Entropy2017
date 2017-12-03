package org.usfirst.frc.team138.robot.commands;

import org.usfirst.frc.team138.robot.Robot;
import org.usfirst.frc.team138.robot.Sensors;
import org.usfirst.frc.team138.robot.OI;

import edu.wpi.first.wpilibj.command.Command;

public class FindGearCloseClaw extends Command {

	boolean isDone = false;
	
	public FindGearCloseClaw()
	{
		requires(Robot.claw);
	}
	
	protected void initialize()
	{
		isDone = false;
		Sensors.cameraProcessor.processFrames(100, false);
	}
	
	protected void execute()
	{
		if (OI.autoRoutinesCancelled())
		{
			isDone = true;
		}
		if (Sensors.cameraProcessor.foundGear())
		{
			Robot.claw.closeClaw();
			isDone = true;
		}
	}
	
	protected boolean isFinished() 
	{
		return isDone;
	}
	
	protected void end()
	{
		Sensors.cameraProcessor.cancelProcessing();
	}

	protected void interrupted() {
		end();
	}

}
