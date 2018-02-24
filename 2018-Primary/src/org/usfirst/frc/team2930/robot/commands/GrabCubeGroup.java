package org.usfirst.frc.team2930.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team2930.robot.Robot;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

/**
 *
 */
public class GrabCubeGroup extends CommandGroup {

    public GrabCubeGroup(Robot robot, Point2D.Double toPoint) {
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
    	
    	addSequential(new OpenIntakeCommand(robot, true));
		addSequential(new AngleIntakeCommand(robot, false));
		addSequential(new SetIntakeSpeedCommand(robot, 1));
		double originalX = robot.currentPoint.getX();
		double originalY = robot.currentPoint.getY();
		addSequential(new DriveToPointGroup(robot, toPoint));
		addSequential(new OpenIntakeCommand(robot, false));
		addSequential(new WaitCommand(0.25));
		addSequential(new SetIntakeSpeedCommand(robot, 0));
		addSequential(new DriveToPointGroup(robot, originalX, originalY, true));
		addSequential(new WaitCommand(0.5));
		addSequential(new AngleIntakeCommand(robot, true));
		addSequential(new WaitCommand(0.25));
    }
}
