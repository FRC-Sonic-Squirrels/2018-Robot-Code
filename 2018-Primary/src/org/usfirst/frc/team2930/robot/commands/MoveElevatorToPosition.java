package org.usfirst.frc.team2930.robot.commands;

import edu.wpi.first.wpilibj.command.Command;

import org.usfirst.frc.team2930.robot.*;

/**
 *
 */
public class MoveElevatorToPosition extends Command {
	
	Robot robot;
	double height;

    public MoveElevatorToPosition(Robot robot, double height) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	this.robot = robot;
    	this.height = height;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	robot.PIDElevator.reset();
    	robot.PIDElevator.enable();
    	robot.PIDElevator.setSetpoint(height);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	robot.elevator1.set(robot.PIDElevatorOutput.getOutput());
    	robot.elevator2.set(robot.PIDElevatorOutput.getOutput());
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	if (robot.PIDElevator.onTarget()) {
    		return true;
    	}
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    	robot.PIDElevator.disable();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
