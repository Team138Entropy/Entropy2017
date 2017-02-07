package org.usfirst.frc.team138.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class LoadStationGearAcquire extends CommandGroup {

	public LoadStationGearAcquire(){
		addSequential(new SetClawPosition(true));
		addSequential(new SetWristPosition(true));
	}
}
