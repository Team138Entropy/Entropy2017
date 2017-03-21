package org.usfirst.frc.team138.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutonomousCommand extends CommandGroup {

	public AutonomousCommand(String team, String startPos, String autoMode){
		// Test Mode
		if (autoMode == "test")
		{
<<<<<<< HEAD
			addSequential(new VisionCorrect(true, 4));
			addSequential(new AutoDrive(0.6, 6));
=======
			//addSequential(new VisionCorrect(true, 4));
			addSequential(new AutoDrive(90));
			addSequential(new AutoDrive(0.75, 2));
//			addSequential(new AutoDrive(0.7, 25));
			//addSequential(new VisionCorrect(true, 4));
//			addSequential(new AutoDrive(-60));
			//addSequential(new VisionCorrect(true, 4));
>>>>>>> branch 'master' of https://github.com/Team138Entropy/Entropy2017.git
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
				{
<<<<<<< HEAD
					addSequential(new AutoDrive(0.90, 84));
					addSequential(new AutoDrive(70));
					addSequential(new VisionCorrect(true, 4));
					addSequential(new AutoDrive(0.75, 36));
=======
					
					/*
					 * 
					 * Original
					 * addSequential(new AutoDrive(0.65, 88));
					addSequential(new AutoDrive(52.5));
					//addSequential(new VisionCorrect(true, 4));
					addSequential(new AutoDrive(0.6, 19));
					 * 
					 */
					addSequential(new AutoDrive(0.65, 78));
					addSequential(new AutoDrive(56.5));
					addSequential(new VisionCorrect(true, 4));
					addSequential(new AutoDrive(0.6, 21));
>>>>>>> branch 'master' of https://github.com/Team138Entropy/Entropy2017.git
				}
				if (team == "blue")
				{
<<<<<<< HEAD
					addSequential(new AutoDrive(0.90, 72));
					addSequential(new AutoDrive(70));
					addSequential(new VisionCorrect(true, 4));
					addSequential(new AutoDrive(0.75, 36));
=======
					addSequential(new AutoDrive(0.70, 76));
					addSequential(new AutoDrive(65));
					//addSequential(new VisionCorrect(true, 4));
					addSequential(new AutoDrive(0.6, 22));
>>>>>>> branch 'master' of https://github.com/Team138Entropy/Entropy2017.git
				}
				addSequential(new SetClawPosition(true));
				addSequential(new PushGear(true));
				addSequential(new Wait(0.2));
				addSequential(new AutoDrive(-0.7, 20));
				addSequential(new PushGear(false));
				addSequential(new Wait(0.1));
				addSequential(new SetClawPosition(false));
			}
			if (startPos == "middle")
			{
				/*
				 * Original Autonomous Granite State
				 * 
				 * addSequential(new AutoDrive(0.75, 40));
				//addSequential(new VisionCorrect(true, 4));
				addSequential(new AutoDrive(0.6, 29));
				addSequential(new SetClawPosition(true));
				addSequential(new PushGear(true));
				addSequential(new Wait(0.2));
				addSequential(new AutoDrive(-0.7, 15));
				addSequential(new PushGear(false));
				addSequential(new Wait(0.1));
				addSequential(new SetClawPosition(false));
				 * 
				 */
				
				addSequential(new AutoDrive(0.65, 40));
				addSequential(new VisionCorrect(true, 4));
				addSequential(new AutoDrive(0.6, 29));
				addSequential(new SetClawPosition(true));
				addSequential(new PushGear(true));
				addSequential(new Wait(0.2));
				addSequential(new AutoDrive(-0.7, 15));
				addSequential(new PushGear(false));
				addSequential(new Wait(0.1));
				addSequential(new SetClawPosition(false));
			}
			if (startPos == "right")
			{
				if (team == "red")
				{
<<<<<<< HEAD
					addSequential(new AutoDrive(0.75, 76));
					addSequential(new AutoDrive(-52.5));
					addSequential(new VisionCorrect(true, 4));
=======
					
					/*
					 * ORIGINAL GRANITE STATE WORKING
					 * 
					addSequential(new AutoDrive(0.65, 74.5));
					addSequential(new AutoDrive(-54.0));
					//addSequential(new VisionCorrect(true, 2));
					addSequential(new AutoDrive(0.6, 20));
					 * 
					 */
										
					addSequential(new AutoDrive(0.65, 74.5));
					addSequential(new AutoDrive(-54.0));
					//addSequential(new VisionCorrect(true, 2));
>>>>>>> branch 'master' of https://github.com/Team138Entropy/Entropy2017
					addSequential(new AutoDrive(0.6, 20));
				}
				if (team == "blue")
				{
					addSequential(new AutoDrive(0.75, 88));
					addSequential(new AutoDrive(-52.5));
					//addSequential(new VisionCorrect(true, 4));
					addSequential(new AutoDrive(0.6, 19));
				}
				addSequential(new SetClawPosition(true));
				addSequential(new PushGear(true));
				addSequential(new Wait(0.2));
				addSequential(new AutoDrive(-0.7, 24));
				addSequential(new PushGear(false));
				addSequential(new Wait(0.1));
				addSequential(new SetClawPosition(false));
				addSequential(new AutoDrive(-120));
				addSequential(new AutoDrive(-0.7,48));
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