package org.usfirst.frc.team138.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class FloorAcquire extends CommandGroup{
	
	public FloorAcquire(){
		addSequential(new SetWristPosition(false));
		addSequential(new SetClawPosition(true));
	}

}
