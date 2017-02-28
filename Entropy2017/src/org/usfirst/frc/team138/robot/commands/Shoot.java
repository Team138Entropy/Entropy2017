package org.usfirst.frc.team138.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team138.robot.Robot;

public class Shoot extends Command{
	
	public Shoot(){
		requires(Robot.shooter);
	}

	protected void initialize() {
		Robot.shooter.startShooter();
	}

	protected void execute() {
	}

	protected boolean isFinished() {
		return false;
	}

	protected void end() {
		Robot.shooter.stopShooter();
	}

	protected void interrupted() {
		end();
	}

}
