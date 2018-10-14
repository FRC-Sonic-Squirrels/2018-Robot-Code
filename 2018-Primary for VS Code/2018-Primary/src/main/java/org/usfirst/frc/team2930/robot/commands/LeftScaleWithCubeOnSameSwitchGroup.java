package org.usfirst.frc.team2930.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team2930.robot.Robot;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

/**
 *
 */
public class LeftScaleWithCubeOnSameSwitchGroup extends CommandGroup {

    public LeftScaleWithCubeOnSameSwitchGroup(Robot robot) {
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
    	
    	//Slightly forward
    	Point2D.Double toPoint = new Double(46.96, 26.00);
    	// Intake is closed
    	// Intake is up
    	// Move arm to position 11
    	addSequential(new MoveArmToPositionCommand(robot, 11));
    	addSequential(new DriveToPointGroup(robot, toPoint));
		// Close grasper
    	addSequential(new ManipulateCPPSTTM(robot, false));
    	//Open the intake
    	addSequential(new OpenIntakeCommand(robot, true));
    	//In open space
    	toPoint.setLocation(46.96, 232.99);
    	addSequential(new DriveToPointGroup(robot, toPoint));
    	addParallel(new MoveElevatorToPositionCommand(robot, robot.ELEVATOR_TOP_VALUE));
    	addParallel(new MoveArmToPositionCommand(robot, robot.ARM_TOP_VALUE));
    	//At scale ready to be placed
    	toPoint.setLocation(59.96, 279.99);
    	addSequential(new DriveToPointGroup(robot, toPoint));
    	//Place on scale
    	addSequential(new ManipulateCPPSTTM(robot, true));
    	addSequential(new WaitCommand(0.25));
    	addSequential(new ManipulateCPPSTTM(robot, false));
    	//Back up
    	toPoint.setLocation(46.96, 232.99);
    	addSequential(new DriveToPointGroup(robot, toPoint, true));
    	//Turn to cube
    	toPoint.setLocation(68.94, 220.14);
    	addSequential(new DriveToPointGroup(robot, toPoint));
    	addParallel(new MoveElevatorToPositionCommand(robot, robot.ELEVATOR_BOTTOM_VALUE));
    	addParallel(new MoveArmToPositionCommand(robot, robot.ARM_BOTTOM_VALUE));
    	//Grab cube
    	toPoint.setLocation(toPoint.getX() + 5, toPoint.getY() - 5);
    	addSequential(new GrabCubeGroup(robot, toPoint));
    	//Move forward to place on switch
    	toPoint.setLocation(107.69, 223.08);
    	addSequential(new DriveToPointGroup(robot, toPoint));
    	//Move to switch
    	toPoint.setLocation(107.69, 216.34);
    	addSequential(new DriveToPointGroup(robot, toPoint));
    	//Eject onto switch
    	addSequential(new MoveArmToPositionCommand(robot, robot.ARM_PLACING_VALUE));
    	addSequential(new WaitCommand(0.25));
    	addSequential(new ManipulateCPPSTTM(robot, true));
    }
}
