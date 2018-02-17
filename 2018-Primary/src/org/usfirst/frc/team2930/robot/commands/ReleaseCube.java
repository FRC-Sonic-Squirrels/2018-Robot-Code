package org.usfirst.frc.team2930.robot.commands;

import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.command.Command;

import org.usfirst.frc.team2930.robot.*;

/**
 *
 */
public class ReleaseCube extends Command {
	
	Robot robot;
	int wait;

    public ReleaseCube(Robot robot) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	this.robot = robot;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	robot.copyrightedPatentPendingSquirrelThumbTM.set(Value.kForward);
    	wait = 0;
    	while (wait < 100) {
    		wait++;
    	}
    	robot.copyrightedPatentPendingSquirrelThumbTM.set(Value.kReverse);
    	wait++;
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	if (wait == 100) {
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
