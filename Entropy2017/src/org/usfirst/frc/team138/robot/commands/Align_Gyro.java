package org.usfirst.frc.team138.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team138.robot.Sensors;

public class Align_Gyro extends Command {
	double LocalAngle=0;
	
	public Align_Gyro(double Angle) {
		Sensors.alignRobotHeading(Angle);
		LocalAngle=Angle;
		this.setInterruptible(false);
	}
	
	protected void execute()
	{
		Sensors.alignRobotHeading(LocalAngle);
	}
	
	protected void initialize() {
		setTimeout(1.0);
	}
	protected boolean isFinished() {
		return isTimedOut();
	}

}
