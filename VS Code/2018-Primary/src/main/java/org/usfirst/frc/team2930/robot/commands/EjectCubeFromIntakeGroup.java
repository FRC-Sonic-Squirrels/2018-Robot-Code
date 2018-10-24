package org.usfirst.frc.team2930.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team2930.robot.Robot;

/**
 *
 */
public class EjectCubeFromIntakeGroup extends CommandGroup {

    public EjectCubeFromIntakeGroup(Robot robot) {
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
    	
    	addSequential(new MoveArmToPositionCommand(robot, robot.ARM_PLACING_VALUE));
    	addSequential(new WaitCommand(0.25));
    	addSequential(new ManipulateCPPSTTM(robot, true));
    	addSequential(new WaitCommand(0.25));
    	addSequential(new ManipulateCPPSTTM(robot, false));
    	addSequential(new MoveArmToPositionCommand(robot, 0));
    }
}
