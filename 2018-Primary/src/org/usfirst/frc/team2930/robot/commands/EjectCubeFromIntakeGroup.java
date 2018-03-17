package org.usfirst.frc.team2930.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team2930.robot.Robot;

/**
 *
 */
public class EjectIntakeCubeGroup extends CommandGroup {

    public EjectIntakeCubeGroup(Robot robot) {
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
    	
    	addSequential(new SetIntakeSpeedCommand(robot, -1));
    	addSequential(new MoveArmToPositionCommand(robot, 30));
    	addSequential(new WaitCommand(0.25));
    	addSequential(new SetIntakeSpeedCommand(robot, 0));
    	addSequential(new MoveArmToPositionCommand(robot, 0));
    }
}
