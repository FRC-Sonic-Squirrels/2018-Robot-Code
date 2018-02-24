package org.usfirst.frc.team2930.robot.commands;

import org.usfirst.frc.team2930.robot.Robot;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class DriveToPointGroup extends CommandGroup {
	
	public DriveToPointGroup(Robot robot, double x, double y) {
		this(robot, x, y, false);
	}
	public DriveToPointGroup(Robot robot, double x, double y, boolean isReversed) {
		this(robot, new Double(x, y), isReversed);
	}
	
	public DriveToPointGroup(Robot robot, Point2D.Double toPoint) {
		this(robot, toPoint, false);
	}

    public DriveToPointGroup(Robot robot, Point2D.Double toPoint, boolean isReversed) {
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
    	
    	double xDistance = toPoint.getX() - robot.currentPoint.getX();
		double yDistance = toPoint.getY() - robot.currentPoint.getY();
		double theta = Math.toDegrees(Math.atan2(xDistance, yDistance));
		double distance = Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2));
		
		if (isReversed) {
			distance = -distance;
			theta = theta > 0 ? theta - 180 : theta + 180;
		}
		addSequential(new RotateToAngleCommand(robot, theta));
		addSequential(new DriveByDistanceCommand(robot, distance, theta));
    	robot.currentPoint.setLocation(toPoint.getX(), toPoint.getY());
    }
}
