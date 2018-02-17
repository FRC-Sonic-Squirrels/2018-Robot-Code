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
    	thisRobot.encoderReset();
    	thisRobot.PIDDrive.reset();
    	thisRobot.PIDDrive.enable();
    	thisRobot.PIDDrive.setSetpoint(distance);
    	thisRobot.PIDRotate.reset();
    	thisRobot.PIDRotate.setSetpoint(angle);
    	thisRobot.PIDRotate.enable();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	thisRobot.johnBotsDriveTrainOfPain.arcadeDrive(
    			-thisRobot.PIDDriveOutput.getOutput(),
    			thisRobot.PIDRotateOutput.getOutput());
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	if (thisRobot.PIDDrive.onTarget() && Math.abs(thisRobot.encoderAverageRate.pidGet()) < 1.0) {
    		return true;
    	}
    	return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    	thisRobot.PIDDrive.disable();
    	thisRobot.PIDRotate.disable();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
