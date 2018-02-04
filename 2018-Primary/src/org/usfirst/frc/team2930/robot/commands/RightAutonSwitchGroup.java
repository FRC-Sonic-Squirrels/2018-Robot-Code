package org.usfirst.frc.team2930.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team2930.robot.Robot;
import org.usfirst.frc.team2930.robot.commands.*;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

/**
 *
 */
public class RightAutonSwitchGroup extends CommandGroup {

    public RightAutonSwitchGroup(Robot robot) {
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
    	
    	//Beside switch
    	Point2D.Double toPoint = new Double(277.65, 166.89);
    	addSequential(new DriveToPointGroup(robot, toPoint.getX(), toPoint.getY()));
    	//Touching switch
    	toPoint.setLocation(258.85, 166.89);
    	addSequential(new DriveToPointGroup(robot, toPoint.getX(), toPoint.getY()));
    }
}
