package org.usfirst.frc.team138.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team138.robot.subsystems.*;
import org.usfirst.frc.team138.robot.commands.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	// Interface with players
	public static OI oi;
    SendableChooser<String> teamChooser;
    SendableChooser<String> startPosChooser;
    SendableChooser<String> autoModeChooser;
        
    // Subsystems
    public static final Drivetrain drivetrain = new Drivetrain();
    public static final ClimbingMechanism climbingMechanism = new ClimbingMechanism();
    public static final Claw claw = new Claw();
    public static final Shooter shooter = new Shooter();
    
    // Commands
    AutonomousCommand autonomousCommand;
    
    // Global constants
    public static String mode; // "auto" or "teleop"

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
    	// Interface
		oi = new OI();
		Sensors.initialize();
		
		// Smart Dashboard Initialization
		Sensors.updateSmartDashboard();
		SmartDashboard.putData(Scheduler.getInstance());
		
		teamChooser = new SendableChooser<String>();
		teamChooser.addDefault("Red Alliance", "red");
		teamChooser.addObject("Blue Alliance", "blue");
		SmartDashboard.putData("Team:", teamChooser);
		
		startPosChooser = new SendableChooser<String>();
		startPosChooser.addObject("Left", "left");
		startPosChooser.addDefault("Middle", "middle");
		startPosChooser.addObject("Right", "right");
		SmartDashboard.putData("Starting Position:", startPosChooser);
		
		autoModeChooser = new SendableChooser<String>();
		autoModeChooser.addDefault("Cross Line", "line");
		autoModeChooser.addObject("Place Gear", "gear");
		autoModeChooser.addObject("Place Gear and Shoot High", "gearAndShoot");
		autoModeChooser.addObject("Place Gear and Release Hopper", "gearAndHopper");
		autoModeChooser.addObject("Test" , "test");
		SmartDashboard.putData("Auto Mode:", autoModeChooser);
    }
	
	/**
     * This function is called once each time the robot enters Disabled mode.
     * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
     */
    public void disabledInit(){

    }
	
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select between different autonomous modes
	 * using the dashboard. The sendable chooser code works with the Java SmartDashboard. If you prefer the LabVIEW
	 * Dashboard, remove all of the chooser code and uncomment the getString code to get the auto name from the text box
	 * below the Gyro
	 *
	 * You can add additional auto modes by adding additional commands to the chooser code above (like the commented example)
	 * or additional comparisons to the switch structure below with additional strings & commands.
	 */
    public void autonomousInit() {
    	mode = "auto";
        autonomousCommand = new AutonomousCommand(teamChooser.getSelected(), 
        		startPosChooser.getSelected(),
        		autoModeChooser.getSelected());
        autonomousCommand.start();
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
        Sensors.updateSmartDashboard();
    }

    public void teleopInit() {
    	mode = "teleop";
        if (autonomousCommand != null) {
        	autonomousCommand.cancel();
        }
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        Scheduler.getInstance().run();
        Sensors.updateSmartDashboard();
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
        LiveWindow.run();
    }
}
