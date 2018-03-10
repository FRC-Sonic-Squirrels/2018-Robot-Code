package org.usfirst.frc.team2930.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team2930.robot.Robot;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

/**
 *
 */
public class RightScaleGroup extends CommandGroup {

    public RightScaleGroup(Robot robot) {
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
    	
    	//In open space
    	Point2D.Double toPoint = new Double(277.65, 232.99);
    	addSequential(new DriveToPointGroup(robot, toPoint.getX(), toPoint.getY()));
    	addSequential(new MoveElevatorToPositionCommand(robot, robot.ELEVATOR_TOP_VALUE));
    	addSequential(new MoveArmToPositionCommand(robot, robot.ARM_PLACING_VALUE));
    	//At scale ready to be placed
    	toPoint.setLocation(268.80, 284.73);
    	addSequential(new DriveToPointGroup(robot, toPoint.getX(), toPoint.getY()));
    	//Place cube
    	addSequential(new ManipulateCPPSTTM(robot, true));
    	addSequential(new WaitCommand(0.25));
    	addSequential(new ManipulateCPPSTTM(robot, false));
    	//Back up
    	toPoint.setLocation(277.65, 232.99);
    	addSequential(new DriveToPointGroup(robot, toPoint, true));
    	addSequential(new MoveElevatorToPositionCommand(robot, robot.ELEVATOR_BOTTOM_VALUE));
    	addSequential(new MoveArmToPositionCommand(robot, robot.ARM_BOTTOM_VALUE));
    }
}