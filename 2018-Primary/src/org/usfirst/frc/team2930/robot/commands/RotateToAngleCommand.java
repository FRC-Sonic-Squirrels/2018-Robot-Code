package org.usfirst.frc.team2930.robot.commands;

import edu.wpi.first.wpilibj.command.Command;

import org.usfirst.frc.team2930.robot.*;

/**
 *
 */
public class RotateToAngleCommand extends Command {
	
	private Robot thisRobot;
	private double angle;

    public RotateToAngleCommand(Robot robot, double toAngle) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	thisRobot = robot;
    	angle = toAngle;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	thisRobot.PIDRotate.reset();
    	thisRobot.PIDRotate.setSetpoint(angle);
    	thisRobot.PIDRotate.enable();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	thisRobot.johnBotsDriveTrainOfPain.arcadeDrive(0, thisRobot.PIDRotateOutput.getOutput());
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	/*if (thisRobot.PIDRotate.onTarget()) {
    		return true;
    	}*/
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    	thisRobot.PIDRotate.disable();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
