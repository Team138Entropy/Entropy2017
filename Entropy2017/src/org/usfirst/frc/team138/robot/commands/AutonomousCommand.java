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
	private boolean doExtraDrive = true;
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
		
		// This auto mode places the gear on the peg depending on the starting position
		if (autoMode == "gear")
		{
			if (startPos == "left")
			{
				if (team == "red")
				{   // tested with competition robot on practice field
					addSequential(new AutoDrive(0.65, 90));
					addSequential(new AutoDrive(52.5));
					addSequential(new VisionCorrect(true, 4));
					addSequential(new AutoDrive(0.65, 30));
				}
				if (team == "blue")
				{   // based on mirror of "red-right"
					addSequential(new AutoDrive(0.65, 90));
					addSequential(new AutoDrive(52.5));
					addSequential(new VisionCorrect(true, 4));
					addSequential(new AutoDrive(0.65, 30));
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
     			addSequential(new AutoDrive(127.5));
				addSequential(new AutoDrive(-0.7, 175));
				if (team == "blue" && doExtraDrive)
				{
					addSequential(new AutoDrive(90));
					addSequential(new AutoDrive(-0.7, 120));
					addSequential(new AutoDrive(-90));
				}
			}
			if (startPos == "middle")
			{   // tested with competition robot on practice field
				addSequential(new AutoDrive(0.65, 40));
				addSequential(new VisionCorrect(true, 4));
				addSequential(new AutoDrive(0.65, 20));
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
			if (startPos == "right" && doExtraDrive)
			{
				if (team == "red")
				{   // tested with competition robot on practice field
					addSequential(new AutoDrive(0.65, 89));
					addSequential(new AutoDrive(-52.5));
					addSequential(new VisionCorrect(true, 4));
					addSequential(new AutoDrive(0.65, 30));
				}
				if (team == "blue")
				{   // based on mirror of "red left"
					addSequential(new AutoDrive(0.65, 89));
					addSequential(new AutoDrive(-52.5));
					addSequential(new VisionCorrect(true, 4));
					addSequential(new AutoDrive(0.65, 30));
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
				if (team == "red")
				{
					addSequential(new AutoDrive(-90));
					addSequential(new AutoDrive(-0.7, 120));
					addSequential(new AutoDrive(90));
				}
			}
		}
	}
}