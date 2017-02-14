package org.usfirst.frc.team138.robot.commands;

import org.usfirst.frc.team138.robot.Robot;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoGearPlace extends CommandGroup {
	
	public AutoGearPlace() {
		requires(Robot.drivetrain);
		this.setInterruptible(false);
		
		//Gear peg vision target is within FOV
		//Vision system creates error function to steer robot to target
		//Robot stops driving forward when acoustic rangefinder is les than threshold
		addSequential(new SetClawPosition(true));
		addSequential(new PushGear());
		addSequential(new PushGear());
		addSequential(new AutoDrive(-0.5, 12));
	}
}
