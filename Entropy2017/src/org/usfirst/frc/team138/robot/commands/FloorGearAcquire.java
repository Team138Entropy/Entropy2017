package org.usfirst.frc.team138.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class FloorGearAcquire extends CommandGroup {

	public FloorGearAcquire(){
		addParallel(new SetClawPosition(false));
		addParallel(new SetWristPosition(false));
	}
}