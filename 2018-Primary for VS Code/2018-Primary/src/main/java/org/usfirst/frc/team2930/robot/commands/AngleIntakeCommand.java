package org.usfirst.frc.team2930.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team2930.robot.Robot;
import edu.wpi.first.wpilibj.DoubleSolenoid;

/**
 *
 */
public class AngleIntakeCommand extends Command {
	
	private Robot robot;
	private boolean up;

    public AngleIntakeCommand(Robot robot, boolean up) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	this.robot = robot;
    	this.up = up;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	if (up) {
    		robot.intakeAngle.set(DoubleSolenoid.Value.kForward);
    	}
    	else {
    		robot.intakeAngle.set(DoubleSolenoid.Value.kReverse);
    	}
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
