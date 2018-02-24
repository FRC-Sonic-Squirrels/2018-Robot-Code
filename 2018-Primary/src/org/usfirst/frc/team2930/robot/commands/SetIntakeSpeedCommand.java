package org.usfirst.frc.team2930.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team2930.robot.Robot;

/**
 *
 */
public class SetIntakeSpeedCommand extends Command {
	
	private Robot robot;
	private double speed;

    public SetIntakeSpeedCommand(Robot robot, double speed) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
		this.robot = robot;
		this.speed = speed;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	robot.intakeSpeed = speed;
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	return true;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
