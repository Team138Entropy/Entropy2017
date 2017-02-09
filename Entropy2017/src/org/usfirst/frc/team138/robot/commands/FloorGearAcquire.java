package org.usfirst.frc.team138.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class FloorGearAcquire extends CommandGroup {

	public FloorGearAcquire(){
		addSequential(new SetWristPosition(false));
		addSequential(new SetClawPosition(false));
	}
}