package org.usfirst.frc.team2930.robot.commands;

import org.usfirst.frc.team2930.robot.Robot;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class CenterAutonSwitchWithCubeGroup extends CommandGroup {

    public CenterAutonSwitchWithCubeGroup(Robot robot) {
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
    	Point2D.Double toPoint = new Double(167.58, 26.00);
    	addSequential(new DriveToPointGroup(robot, toPoint));
    	if (robot.gameData.charAt(0) == 'L') {
    		//Switch angled
    		toPoint.setLocation(110.34,  112.88);
    		addSequential(new DriveToPointGroup(robot, toPoint));
    		//Switch head-on
    		toPoint.setLocation(110.34, 119.89);
    		addSequential(new DriveToPointGroup(robot, toPoint));
    		//Eject cube
    		//UMMMMMMM CODE
    		//Back up
    		toPoint.setLocation(110.34, 95.75);
    		addSequential(new DriveToPointGroup(robot, toPoint));
    		//In front of cube
    		toPoint.setLocation(126.20, 105.84);
    		addSequential(new DriveToPointGroup(robot, toPoint));
    		//Grab cube
    		//UMMMMMMM CODE
    		//Back near switch
    		toPoint.setLocation(110.34, 95.75);
    		addSequential(new DriveToPointGroup(robot, toPoint));
    		//To switch
    		toPoint.setLocation(110.34, 119.98);
    		addSequential(new DriveToPointGroup(robot, toPoint));
    		//Eject cube
    		//UMMMMMMM CODE
    	}
    	else {
    		//Switch angled
        	toPoint.setLocation(216.04,  112.88);
        	addSequential(new DriveToPointGroup(robot, toPoint));
        	//Switch head-on
        	toPoint.setLocation(216.04, 119.89);
        	addSequential(new DriveToPointGroup(robot, toPoint));
        	//Eject cube
    		//UMMMMMMM CODE
    		//Back up
    		toPoint.setLocation(216.04, 95.75);
    		addSequential(new DriveToPointGroup(robot, toPoint));
    		//In front of cube
    		toPoint.setLocation(196.70, 105.84);
    		addSequential(new DriveToPointGroup(robot, toPoint));
    		//Grab cube
    		//UMMMMMMM CODE
    		//Back near switch
    		toPoint.setLocation(216.04, 95.75);
    		addSequential(new DriveToPointGroup(robot, toPoint));
    		//To switch
    		toPoint.setLocation(216.04, 119.98);
    		addSequential(new DriveToPointGroup(robot, toPoint));
    		//Eject cube
    		//UMMMMMMM CODE
    	}
    }
}
