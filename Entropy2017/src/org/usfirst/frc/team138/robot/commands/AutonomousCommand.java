package org.usfirst.frc.team138.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutonomousCommand extends CommandGroup {

	public AutonomousCommand(String team, String startPos, String autoMode){
		// THE SPOKE IS UP
		// Test Mode
		if (autoMode == "test")
		{
			addSequential(new AutoDrive(5));
		}
		
		// This auto mode crosses the line and that's it. This is the default
		if (autoMode == "line")
		{
			if (startPos == "middle")
			{
				addSequential(new AutoDrive(-0.6, 50));
			}
			else
			{
				addSequential(new AutoDrive(-0.6, 50));
			}
		}
		
		// This auto mode places the gear on the peg depending on the starting position
		if (autoMode == "gear" || autoMode == "gearAndShoot" || autoMode == "gearAndHopper")
		{
			if (startPos == "left")
			{
				addSequential(new AutoDrive(-0.6, 96));
				addSequential(new AutoDrive(30));
				addSequential(new AutoDrive(-0.6, 7));
				addSequential(new PushGear());
				addSequential(new PushGear());
				addSequential(new AutoDrive(0.6, 12));
			}
			if (startPos == "middle")
			{
				addSequential(new AutoDrive(-0.7, 65));
				addSequential(new GearCorrect());
				addSequential(new AutoDrive(-0.6, 8));
				addSequential(new SetClawPosition(true));
				addSequential(new PushGear());
				addSequential(new PushGear());
				addSequential(new AutoDrive(1.0, 15));
				addSequential(new SetClawPosition(false));
			}
			if (startPos == "right")
			{
				addSequential(new AutoDrive(-0.6, 86));
				addSequential(new AutoDrive(-30));
				addSequential(new AutoDrive(-0.6, 30));
				addSequential(new PushGear());
				addSequential(new PushGear());
				addSequential(new AutoDrive(0.6, 12));
			}
			
			// This mode then shoots fuel into the goal depending on team and start position
			if (autoMode == "gearAndShoot")
			{
				if (team == "red")
				{
					if (startPos == "right")
					{
						addSequential(new AutoDrive(-0.5, 10));
						// Correct Rotation
						// Shoot
					}
					if (startPos == "middle")
					{
						addSequential(new AutoDrive(-0.5, 10));
						addSequential(new AutoDrive(90));
						// Correct Rotation
						// Shoot
					}
				}
				if (team == "blue")
				{
					if (startPos == "left")
					{
						addSequential(new AutoDrive(-0.5, 10));
						// Correct Rotation
						// Shoot
					}
					if (startPos == "middle")
					{
						addSequential(new AutoDrive(-0.5, 10));
						addSequential(new AutoDrive(-90));
						// Correct Rotation
						// Shoot
					}
				}
			}
			
			// This mode then activates the hopper depending on team and start position
			if (autoMode == "gearAndHopper")
			{
				if (team == "red" && startPos == "right")
				{
					addSequential(new AutoDrive(-0.5, 10));
					addSequential(new AutoDrive(45));
					addSequential(new AutoDrive(0.5, 10));
					addSequential(new AutoDrive(90));
					addSequential(new AutoDrive(0.5, 20));
				}
				if (team == "blue" && startPos == "left")
				{
					addSequential(new AutoDrive(-0.5, 10));
					addSequential(new AutoDrive(-45));
					addSequential(new AutoDrive(0.5, 10));
					addSequential(new AutoDrive(-90));
					addSequential(new AutoDrive(0.5, 20));
				}
			}
		}
	}
}