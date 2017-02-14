package org.usfirst.frc.team138.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutonomousCommand extends CommandGroup {

	public AutonomousCommand(String team, String startPos, String autoMode){
		// Test Mode
		if (autoMode == "test")
		{
			addSequential(new AutoDrive(90));
		}
		
		// This auto mode crosses the line and that's it. This is the default
		if (autoMode == "line")
		{
			if (startPos == "middle")
			{
				addSequential(new AutoDrive(0.5, 40));
			}
			else
			{
				addSequential(new AutoDrive(0.5, 45));
			}
		}
		
		// This auto mode places the gear on the peg depending on the starting position
		if (autoMode == "gear" || autoMode == "gearAndShoot" || autoMode == "gearAndHopper")
		{
			if (startPos == "left")
			{
				addSequential(new AutoDrive(0.5, 10));
				addSequential(new AutoDrive(45));
				addSequential(new AutoDrive(0.5, 10));
				addSequential(new PushGear());
				addSequential(new PushGear());
			}
			if (startPos == "middle")
			{
				addSequential(new AutoDrive(0.5, 30));
				addSequential(new PushGear());
				addSequential(new PushGear());
			}
			if (startPos == "right")
			{
				addSequential(new AutoDrive(0.5, 10));
				addSequential(new AutoDrive(-45));
				addSequential(new AutoDrive(0.5, 10));
				addSequential(new PushGear());
				addSequential(new PushGear());
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