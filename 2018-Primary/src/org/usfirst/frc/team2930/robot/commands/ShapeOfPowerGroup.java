package org.usfirst.frc.team2930.robot.commands;

import org.usfirst.frc.team2930.robot.Robot;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class ShapeOfPowerGroup extends CommandGroup {

    public ShapeOfPowerGroup(Robot robot) {
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
    	//Triangle of Power
    	/*double toX = 2.5;
    	double toY = 2.5 * Math.sqrt(3);
    	addSequential(new DriveToPointGroup(thisRobot, toX, toY));
    	toX = 5;
    	toY = 0;
    	addSequential(new DriveToPointGroup(thisRobot, toX, toY));
    	toX = 0;
    	toY = 0;
    	addSequential(new DriveToPointGroup(thisRobot, toX, toY));
    	toX = 2.5;
    	toY = 2.5 * Math.sqrt(3);
    	addSequential(new DriveToPointGroup(thisRobot, toX, toY, true));
    	toX = 5;
    	toY = 0;
    	addSequential(new DriveToPointGroup(thisRobot, toX, toY, true));
    	toX = 0;
    	toY = 0;
    	addSequential(new DriveToPointGroup(thisRobot, toX, toY, true));
    	addSequential(new RotateToAngleCommand(thisRobot, 0));*/
    	Point2D.Double toPoint = new Double(0, 5);
    	addSequential(new DriveToPointGroup(thisRobot, toPoint));
    	toPoint.setLocation(0, 0);
    	addSequential(new DriveToPointGroup(thisRobot, toPoint));
    	addSequential(new RotateToAngleCommand(thisRobot, 0));
    }
}
