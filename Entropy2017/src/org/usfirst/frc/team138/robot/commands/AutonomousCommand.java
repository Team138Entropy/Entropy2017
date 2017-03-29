package org.usfirst.frc.team138.robot.commands;

// BedfordBase branch started 2017-03-25 - jmcg
// 1. Increase "advance to neutral zone" distance to 10 feet"
// 2. Mirror "advance to neutral zone" for left starting position
// 3. Mark which moves have been tested with competition robot on practice field
// 4. Mirror dialed-in values from "Red" alliance positions to corresponding "Blue" positions
// note that values from red-left end up in blue-right while red-right end up in blue-left
// except for negating the turn angles

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutonomousCommand extends CommandGroup {

	public AutonomousCommand(String team, String startPos, String autoMode){
		// Test Mode
		if (autoMode == "test")
		{
			addSequential(new VisionCorrect(true, 4));
			addSequential(new AutoDrive(5));
			addSequential(new VisionCorrect(true, 4));
			addSequential(new AutoDrive(5));
			addSequential(new VisionCorrect(true, 4));
		}
		
		// This auto mode crosses the line and that's it. This is the default
		if (autoMode == "line")
		{
			if (startPos == "middle")
			{
				addSequential(new AutoDrive(0.6, 65));
			}
			else
			{
				addSequential(new AutoDrive(0.6, 90));
			}
		}
		
		// This auto mode places the gear on the peg depending on the starting position
		if (autoMode == "gear" || autoMode == "gearAndShoot" || autoMode == "gearAndHopper")
		{
			if (startPos == "left")
			{
				if (team == "red")
				{   // tested with competition robot on practice field
					addSequential(new AutoDrive(0.65, 96));
					addSequential(new AutoDrive(52.5));
					addSequential(new VisionCorrect(true, 4));
					addSequential(new AutoDrive(0.6, 29));
				}
				if (team == "blue")
				{   // based on mirror of "red-right"
					addSequential(new AutoDrive(0.65, 96));
					addSequential(new AutoDrive(52.5));
					addSequential(new VisionCorrect(true, 4));
					addSequential(new AutoDrive(0.65, 29));
				}
				addSequential(new SetClawPosition(true));
				addSequential(new PushGear(true));
				addSequential(new Wait(0.5));
				addSequential(new AutoDrive(-0.7, 20));
				addSequential(new PushGear(false));
				addSequential(new Wait(0.1));
				addSequential(new SetClawPosition(false));
				// drive to neutral zone after placement of peg (left)
     			addSequential(new AutoDrive(-0.7,22));
     			addSequential(new AutoDrive(125));
				addSequential(new AutoDrive(-0.7, 175));
			}
			if (startPos == "middle")
			{   // tested with competition robot on practice field
				//addSequential(new AutoDrive(0.65, )); WIP
				addSequential(new SetClawPosition(true));
				addSequential(new PushGear(true));
				addSequential(new Wait(0.5));
				addSequential(new PushGear(false));
				addSequential(new Wait(0.1));
				addSequential(new PushGear(true));
				addSequential(new Wait(0.5));
				addSequential(new AutoDrive(-0.7, 15));
				addSequential(new PushGear(false));
				addSequential(new Wait(0.1));
				addSequential(new SetClawPosition(false));
			}
			if (startPos == "right")
			{
				if (team == "red")
				{   // tested with competition robot on practice field
					addSequential(new AutoDrive(0.65, 96));
					addSequential(new AutoDrive(-52.5));
					addSequential(new VisionCorrect(true, 4));
					addSequential(new AutoDrive(0.6, 29));
				}
				if (team == "blue")
				{   // based on mirror of "red left"
					addSequential(new AutoDrive(0.65, 96));
					addSequential(new AutoDrive(-52.5));
					addSequential(new VisionCorrect(true, 4));
					addSequential(new AutoDrive(0.6, 29));
				}
				addSequential(new SetClawPosition(true));
				addSequential(new PushGear(true));
				addSequential(new Wait(0.5));
				addSequential(new PushGear(false));
				addSequential(new Wait(0.1));
				addSequential(new PushGear(true));
				addSequential(new Wait(0.5));
				addSequential(new AutoDrive(-0.7, 20));
				addSequential(new PushGear(false));
				addSequential(new Wait(0.1));
				addSequential(new SetClawPosition(false));
				// drive to neutral zone after placement of peg (right)
     			addSequential(new AutoDrive(-0.7,22));
     			addSequential(new AutoDrive(-125));
				addSequential(new AutoDrive(-0.7, 175));
			}
			
			// This mode then shoots fuel into the goal depending on team and start position
			if (autoMode == "gearAndShoot")
			{
				if (team == "red")
				{
					if (startPos == "right")
					{
						addSequential(new AutoDrive(-0.75, 10));
						addSequential(new AutoDrive(40));
						addSequential(new VisionCorrect(false, 4));
						addSequential(new Shoot(6));
					}
					if (startPos == "middle")
					{
						addSequential(new AutoDrive(-0.75, 10));
						addSequential(new AutoDrive(-75));
						addSequential(new VisionCorrect(false, 4));
						addSequential(new Shoot(6));
					}
				}
				if (team == "blue")
				{
					if (startPos == "left")
					{
						addSequential(new AutoDrive(-0.75, 10));
						addSequential(new AutoDrive(-40));
						addSequential(new VisionCorrect(false, 4));
						addSequential(new Shoot(6));
					}
					if (startPos == "middle")
					{
						addSequential(new AutoDrive(-0.75, 10));
						addSequential(new AutoDrive(75));
						addSequential(new VisionCorrect(false, 4));
						addSequential(new Shoot(6));
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