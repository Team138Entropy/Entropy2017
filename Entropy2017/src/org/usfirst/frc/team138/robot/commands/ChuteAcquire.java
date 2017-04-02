package org.usfirst.frc.team138.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class ChuteAcquire extends CommandGroup
{
	public ChuteAcquire()
	{
		addSequential(new SetWristPosition(true));
		addSequential(new SetClawPosition(true));
		addSequential(new FindGearCloseClaw());
	}
}
