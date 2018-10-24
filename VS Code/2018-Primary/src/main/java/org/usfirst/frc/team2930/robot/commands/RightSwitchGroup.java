package org.usfirst.frc.team2930.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team2930.robot.Robot;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

/**
 *
 */
public class RightSwitchGroup extends CommandGroup {

    public RightSwitchGroup(Robot robot) {
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
    	Point2D.Double toPoint = new Double(277.65, 26.00);
    	// Intake is closed
    	// Intake is up
    	// Move arm to position 22
    	addSequential(new MoveArmToPositionCommand(robot, 22));
    	addSequential(new DriveToPointGroup(robot, toPoint));
		// Close grasper
    	addSequential(new ManipulateCPPSTTM(robot, false));
    	toPoint = new Double(277.65, 166.89);
    	addSequential(new DriveToPointGroup(robot, toPoint));
    	// Move arm to ARM_PLACING
    	addSequential(new MoveArmToPositionCommand(robot, robot.ARM_PLACING_VALUE));
    	addSequential(new WaitCommand(0.125));
    	//Face switch
    	addSequential(new RotateToAngleCommand(robot, 90));
    	// Open intake
    	addSequential(new OpenIntakeCommand(robot, true));
    	//Touching switch
    	toPoint.setLocation(258.85, 166.89);
    	addSequential(new DriveToPointGroup(robot, toPoint));
    	//Place cube
    	// Open grasper
    	addSequential(new ManipulateCPPSTTM(robot, true));
    }
}
