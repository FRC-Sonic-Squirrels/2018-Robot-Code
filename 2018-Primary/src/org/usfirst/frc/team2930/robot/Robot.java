/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team2930.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team2930.robot.commands.ExampleCommand;
import org.usfirst.frc.team2930.robot.subsystems.ExampleSubsystem;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.XboxController;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSourceType;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * 
 * project.
 */
public class Robot extends TimedRobot {
	public static final ExampleSubsystem kExampleSubsystem
			= new ExampleSubsystem();
	public static OI m_oi;

	Command m_autonomousCommand;
	SendableChooser<Command> m_chooser = new SendableChooser<>();
	DifferentialDrive johnBotsDriveTrainOfPain;
	XboxController driveController, operateController;
	AHRS gyro;
	Encoder rightEncoder/*, leftEncoder*/;
	Spark leftGrabber, rightGrabber;
	PIDController PIDDrive, PIDRotate;
	SimplePIDOutput PIDDriveOutput, PIDRotateOutput;
	EncoderAveragePIDSource encoderAverage;
	public void encoderReset() {
		rightEncoder.reset();
		//LeftEncoder.reset();
	}
	public double average(double x, double y) {
		return (x + y) / 2;
	}

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		m_oi = new OI();
		m_chooser.addDefault("Default Auto", new ExampleCommand());
		// chooser.addObject("My Auto", new MyAutoCommand());
		SmartDashboard.putData("Auto mode", m_chooser);
		johnBotsDriveTrainOfPain = new DifferentialDrive(new Spark(0), new Spark(1));
		driveController = new XboxController(0);
		operateController = new XboxController(1);
		gyro = new AHRS(SerialPort.Port.kMXP);
		rightEncoder = new Encoder(9, 8);
		//leftEncoder = new Encoder(2, 3);
		leftGrabber = new Spark(4);
		rightGrabber = new Spark(5);
		rightEncoder.setDistancePerPulse(0.0043970539738375);
		//leftEncoder.setDistancePerPulse(0.0043970539738375);
		encoderAverage = new EncoderAveragePIDSource(rightEncoder/*leftEncoder*/, rightEncoder);
		PIDDriveOutput = new SimplePIDOutput(0);
		PIDRotateOutput = new SimplePIDOutput(0);
		rightEncoder.setPIDSourceType(PIDSourceType.kDisplacement);
		//leftEncoder.setPIDSourceType(PIDSourceType.kDisplacement);
		PIDDrive = new PIDController(0, 0, 0, encoderAverage, PIDDriveOutput);
		PIDRotate = new PIDController(0, 0, 0, gyro, PIDRotateOutput);
	}
	
	@Override
	public void robotPeriodic() {
		SmartDashboard.putNumber("Gyro Angle ", gyro.getYaw());
		SmartDashboard.putNumber("Rate of Turning ", gyro.getRate());
		SmartDashboard.putNumber("Distance ", /*average(*/rightEncoder.getDistance()/*, leftEncoder.getDistance())*/);
		SmartDashboard.putNumber("Speed ", /*average(*/rightEncoder.getRate()/*, leftEncoder.getRate())*/);
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {

	}

	@Override
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString code to get the auto name from the text box below the Gyro
	 *
	 * <p>You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons
	 * to the switch structure below with additional strings & commands.
	 */
	@Override
	public void autonomousInit() {
		m_autonomousCommand = m_chooser.getSelected();

		/*
		 * String autoSelected = SmartDashboard.getString("Auto Selector",
		 * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
		 * = new MyAutoCommand(); break; case "Default Auto": default:
		 * autonomousCommand = new ExampleCommand(); break; }
		 */

		// schedule the autonomous command (example)
		if (m_autonomousCommand != null) {
			m_autonomousCommand.start();
		}
		gyro.reset();
		encoderReset();
		String gameData;
		gameData = DriverStation.getInstance().getGameSpecificMessage();
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
	}

	@Override
	public void teleopInit() {
		// This makes sure that the autonomous stops running when
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.
		if (m_autonomousCommand != null) {
			m_autonomousCommand.cancel();
		}
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
		johnBotsDriveTrainOfPain.arcadeDrive(driveController.getY(GenericHID.Hand.kLeft), driveController.getX(GenericHID.Hand.kRight));
		rightGrabber.set(operateController.getY(GenericHID.Hand.kLeft));
		leftGrabber.set(-operateController.getY(GenericHID.Hand.kLeft));
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
