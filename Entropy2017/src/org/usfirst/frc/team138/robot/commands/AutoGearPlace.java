package org.usfirst.frc.team138.robot.commands;

//import org.usfirst.frc.team138.robot.Robot;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoGearPlace extends CommandGroup {
	
	public AutoGearPlace() {
		this.setInterruptible(false);
		
		addSequential(new VisionCorrect(true, 4));
		//Robot stops driving forward when acoustic rangefinder is less than threshold
		addSequential(new SetClawPosition(true));
		addSequential(new PushGear());
		addSequential(new PushGear());
		addSequential(new AutoDrive(-0.5, 12));
	}
}
