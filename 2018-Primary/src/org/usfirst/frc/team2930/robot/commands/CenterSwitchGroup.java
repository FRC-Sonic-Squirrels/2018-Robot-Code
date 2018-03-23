package org.usfirst.frc.team2930.robot.commands;

import org.usfirst.frc.team2930.robot.Robot;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class CenterSwitchGroup extends CommandGroup {

    public CenterSwitchGroup(Robot robot) {
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
    	Point2D.Double toPoint = new Double(167.58, 35.00);
    	// Intake is closed
    	// Intake is up
    	// Move arm to position 11
    	addSequential(new MoveArmToPositionCommand(robot, 11));
    	addSequential(new DriveToPointGroup(robot, toPoint));
		// Close grasper
    	addSequential(new ManipulateCPPSTTM(robot, false));
    	if (robot.gameData.charAt(0) == 'L') {
    		//Switch angled
    		toPoint.setLocation(110.34,  112.88);
    		addSequential(new DriveToPointGroup(robot, toPoint));
    		// Open intake
        	addSequential(new OpenIntakeCommand(robot, true));
        	// Move arm to ARM_PLACING
        	addSequential(new MoveArmToPositionCommand(robot, robot.ARM_PLACING_VALUE));
        	//Switch head-on
    		toPoint.setLocation(110.34, 125);
    		addSequential(new DriveToPointGroup(robot, toPoint));
    	}
    	else {
    		//Switch angled
        	toPoint.setLocation(216.04, 112.88);
        	addSequential(new DriveToPointGroup(robot, toPoint));
        	// Open intake
        	addSequential(new OpenIntakeCommand(robot, true));
        	// Move arm to ARM_PLACING
        	addSequential(new MoveArmToPositionCommand(robot, robot.ARM_PLACING_VALUE));
        	//Switch head-on
        	toPoint.setLocation(216.04, 125);
        	addSequential(new DriveToPointGroup(robot, toPoint));
    	}
    	//Open grasper
    	addSequential(new ManipulateCPPSTTM(robot, true));
    	addSequential(new WaitCommand(0.25));
    	addSequential(new MoveArmToPositionCommand(robot, robot.ARM_BOTTOM_VALUE));
    	addSequential(new WaitCommand(0.5));
    	//Back up
    	if (robot.gameData.charAt(0) == 'L') {
    		toPoint.setLocation(110.34,  112.88);
    	}
    	else {
    		toPoint.setLocation(216.04, 112.88);
    	}
    	addSequential(new DriveToPointGroup(robot, toPoint, true));
    	addSequential(new WaitCommand(1));
    	//In front of pyramid
    	toPoint.setLocation(180.00, 65.00);
    	addSequential(new DriveToPointGroup(robot, toPoint, true));
    	addSequential(new SetIntakeSpeedCommand(robot, 1));
    	addSequential(new WaitCommand(1));
    	//Literally in front of pyramid
    	toPoint.setLocation(180.00, 101.57);
    	addSequential(new AngleIntakeCommand(robot, false));
    	addSequential(new DriveToPointGroup(robot, toPoint));
    	addSequential(new WaitCommand(1));
    	//Grab sequence
    	addSequential(new OpenIntakeCommand(robot, false));
    	addSequential(new WaitCommand(0.5));
    	addSequential(new SetIntakeSpeedCommand(robot, 0));
    	addSequential(new AngleIntakeCommand(robot, true));
    	addSequential(new WaitCommand(1));
    	//Back up a teensy bit
    	/*toPoint.setLocation(180.00, 65.00);
    	addSequential(new DriveToPointGroup(robot, toPoint, true));
    	addSequential(new WaitCommand(1));
    	//Turn toward owned scale
    	if (robot.gameData.charAt(0) == 'L') {
    		addSequential(new RotateToAngleCommand(robot, -60));
    	}
    	else {
    		addSequential(new RotateToAngleCommand(robot, 60));
    	}*/
    }
}
