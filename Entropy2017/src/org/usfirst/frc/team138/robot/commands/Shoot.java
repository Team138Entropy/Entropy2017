package org.usfirst.frc.team138.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team138.robot.Robot;

public class Shoot extends Command{
	
	public Shoot(){
		requires(Robot.shooter);
		setTimeout(10);
	}
	
	public Shoot(double timeout)
	{
		requires(Robot.shooter);
		setTimeout(timeout);
	}

	protected void initialize() {
		Robot.shooter.startShooter();
	}

	protected void execute() {
		if (timeSinceInitialized() > 0.3)
		{
			Robot.shooter.releaseGate();
		}
	}

	protected boolean isFinished() {
		return isTimedOut();
	}

	protected void end() {
		Robot.shooter.stopShooter();
		Robot.shooter.engageGate();
	}

	protected void interrupted() {
		end();
	}

}
