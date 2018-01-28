package org.usfirst.frc.team2930.robot.commands;

import org.usfirst.frc.team2930.robot.Robot;

import org.usfirst.frc.team2930.robot.*;
import org.usfirst.frc.team2930.robot.commands.*;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class DriveToPointGroup extends CommandGroup {
	
	public DriveToPointGroup(Robot robot, double x, double y) {
		this(robot, x, y, false);
	}

    public DriveToPointGroup(Robot robot, double x, double y, boolean isReversed) {
        // Add Commands here:
        // e.g. addSequential(new Command1());
        //      addSequential(new Command2());
        // these will run in order.

        // To run multiple commands at the same time,
        // use addParallel()
        // e.g. addParallel(new Command1());
        //      addSequential(new Command2());
        // Command1 and Command2 will run in parallel.

        // A command group will require all of the subsystems that each member
        // would require.
        // e.g. if Command1 requires chassis, and Command2 requires arm,
        // a CommandGroup containing them would require both the chassis and the
        // arm.
    	
    	Robot thisRobot = robot;
    	double toX = x;
    	double toY = y;
    	double xDistance = toX - robot.currentX;
		double yDistance = toY - robot.currentY;
		double theta = Math.toDegrees(Math.atan2(xDistance, yDistance));
		double distance = Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2));
		
		if (isReversed) {
			distance = -distance;
			theta = theta > 0 ? theta - 180 : theta + 180;
		}
		addSequential(new RotateToAngleCommand(thisRobot, theta));
		addSequential(new DriveByDistanceCommand(thisRobot, distance, theta));
    	thisRobot.currentX = toX;
    	thisRobot.currentY = toY;
    }
}
