package org.usfirst.frc.team2930.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team2930.robot.Robot;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

/**
 *
 */
public class LeftOppositeScaleGroup extends CommandGroup {

    public LeftOppositeScaleGroup(Robot robot) {
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
    	
    	// Intake is closed
    	// Intake is up
    	// Move arm to position 11
    	addSequential(new MoveArmToPositionCommand(robot, 11));
    	addSequential(new WaitCommand(0.5));
		// Close grasper
    	addSequential(new ManipulateCPPSTTM(robot, false));
    	addSequential(new WaitCommand(0.25));
    	//Open the intake
    	addSequential(new OpenIntakeCommand(robot, true));
    	//In open space
    	Point2D.Double toPoint = new Double(46.96, 250.00);
    	addSequential(new DriveToPointGroup(robot, toPoint));
    	//Across the field
    	toPoint.setLocation(246.23, 250.00);
    	addSequential(new DriveToPointGroup(robot, toPoint));
    	addSequential(new MoveElevatorToPositionCommand(robot, robot.ELEVATOR_PLACING_VALUE));
    	addSequential(new MoveArmToPositionCommand(robot, robot.ARM_TOP_VALUE));
    	addSequential(new WaitCommand(1));
    	//At scale
    	toPoint.setLocation(246.23, 300.73);
    	addSequential(new DriveToPointGroup(robot, toPoint, false, true));
    	addSequential(new WaitCommand(1.5));
    	//Place cube
    	addSequential(new ManipulateCPPSTTM(robot, true));
    	addSequential(new WaitCommand(1));
    	//Back up
    	toPoint.setLocation(246.23, 250.00);
    	addSequential(new DriveToPointGroup(robot, toPoint, true, true));
    	addSequential(new MoveElevatorToPositionCommand(robot, robot.ELEVATOR_BOTTOM_VALUE));
    	addSequential(new MoveArmToPositionCommand(robot, robot.ARM_BOTTOM_VALUE));
    	addSequential(new WaitCommand(1));
    }
}
