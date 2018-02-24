package org.usfirst.frc.team2930.robot.commands;

import edu.wpi.first.wpilibj.command.Command;

import org.usfirst.frc.team2930.robot.*;

/**
 *
 */
public class DriveByDistanceCommand extends Command {
	
	private Robot thisRobot;
	private double distance;
	private double angle;

    public DriveByDistanceCommand(Robot robot, double distanceInInches, double angleInDegrees) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	thisRobot = robot;
    	distance = distanceInInches;
    	angle = angleInDegrees;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	thisRobot.driveEncoderReset();
    	thisRobot.drivePID.setSetpoint(distance);
    	thisRobot.rotatePID.setSetpoint(angle);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	if (thisRobot.drivePID.onTarget() && Math.abs(thisRobot.encoderAverageRate.pidGet()) < 1.0) {
    		return true;
    	}
    	return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
