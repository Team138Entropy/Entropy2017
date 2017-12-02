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
		System.out.println("In Align_Gyro");
		Sensors.alignRobotHeading(LocalAngle);
	}
	
	protected void initialize() {
		setTimeout(0.5);
	}
	protected boolean isFinished() {
		return isTimedOut();
	}

}
