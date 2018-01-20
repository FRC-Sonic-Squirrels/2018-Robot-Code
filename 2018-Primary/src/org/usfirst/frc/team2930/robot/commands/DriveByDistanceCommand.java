package org.usfirst.frc.team2930.robot.commands;

import edu.wpi.first.wpilibj.command.Command;

import org.usfirst.frc.team2930.robot.*;

/**
 *
 */
public class DriveByDistanceCommand extends Command {
	
	private Robot thisRobot;
	private double distance;

    public DriveByDistanceCommand(Robot robot, double moveValue) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	thisRobot = robot;
    	distance = moveValue;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	thisRobot.encoderReset();
    	thisRobot.PIDDrive.reset();
    	thisRobot.PIDDrive.enable();
    	thisRobot.PIDDrive.setSetpoint(distance);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	thisRobot.johnBotsDriveTrainOfPain.arcadeDrive(thisRobot.PIDDriveOutput.getOutput(), 0);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	if (thisRobot.PIDDrive.onTarget()) {
    		return true;
    	}
    	return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    	thisRobot.PIDDrive.disable();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
