package org.usfirst.frc.team2930.robot.commands;

import edu.wpi.first.wpilibj.command.Command;

import org.usfirst.frc.team2930.robot.*;

/**
 *
 */
public class MoveArmToPosition extends Command {
	
	Robot robot;
	double height;

    public MoveArmToPosition(Robot robot, double height) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	this.robot = robot;
    	this.height = height;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	robot.armEncoder.reset();
    	robot.PIDArm.reset();
    	robot.PIDArm.enable();
    	robot.PIDArm.setSetpoint(height);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	robot.arm.set(robot.PIDArmOutput.getOutput());
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	if (robot.PIDArm.onTarget()) {
    		return true;
    	}
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    	robot.PIDArm.disable();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
