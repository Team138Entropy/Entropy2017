package org.usfirst.frc.team138.robot.commands;

import org.usfirst.frc.team138.robot.Robot;
import org.usfirst.frc.team138.robot.RobotMap;
import org.usfirst.frc.team138.robot.OI;

import edu.wpi.first.wpilibj.command.Command;

public class PulseClimb extends Command {

	private static final double pulseDuration = 0.5;
	
	public PulseClimb()
	{
		requires(Robot.climbingMechanism);
	}
	
	protected void execute()
	{
		if (Math.floor(timeSinceInitialized() / pulseDuration) % 2 == 0)
		{
			Robot.climbingMechanism.setSpeed(RobotMap.ROPE_PULSE_SPEED);
		}
		else
		{
			Robot.climbingMechanism.setSpeed(0);
		}
	}
	
	protected boolean isFinished() {
		return OI.autoRoutinesCancelled();
	}

}
