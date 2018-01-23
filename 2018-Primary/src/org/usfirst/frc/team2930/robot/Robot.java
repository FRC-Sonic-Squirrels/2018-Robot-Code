/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team2930.robot;

import org.usfirst.frc.team2930.robot.commands.ExampleCommand;
import org.usfirst.frc.team2930.robot.commands.RotateToAngleCommand;
import org.usfirst.frc.team2930.robot.subsystems.ExampleSubsystem;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DoubleSolenoid;

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
	public DifferentialDrive johnBotsDriveTrainOfPain;
	public XboxController driveController, operateController;
	public AHRS gyro;
	public Encoder leftEncoder, rightEncoder;
	public Spark leftGrabber, rightGrabber;
	public PIDController PIDDrive, PIDRotate;
	public SimplePIDOutput PIDDriveOutput, PIDRotateOutput;
	public EncoderAveragePIDSource encoderAverage;
	public DoubleSolenoid plsWork;
	public Spark rightDrive, leftDrive;
	public void encoderReset() {
		leftEncoder.reset();
		rightEncoder.reset();
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
		rightDrive = new Spark(0);
		leftDrive = new Spark(1);
		rightDrive.setInverted(true);
		leftDrive.setInverted(true);
		johnBotsDriveTrainOfPain = new DifferentialDrive(leftDrive, rightDrive);
		driveController = new XboxController(0);
		operateController = new XboxController(1);
		gyro = new AHRS(SerialPort.Port.kMXP);
		leftEncoder = new Encoder(0, 1);
		rightEncoder = new Encoder(2, 3, true);
		leftGrabber = new Spark(4);
		rightGrabber = new Spark(5);
		plsWork = new DoubleSolenoid(0, 1);
		leftEncoder.setDistancePerPulse(0.0043970539738375);
		rightEncoder.setDistancePerPulse(0.0043970539738375);
		encoderAverage = new EncoderAveragePIDSource(leftEncoder, rightEncoder);
		PIDDriveOutput = new SimplePIDOutput(0);
		PIDRotateOutput = new SimplePIDOutput(0);
		leftEncoder.setPIDSourceType(PIDSourceType.kDisplacement);
		rightEncoder.setPIDSourceType(PIDSourceType.kDisplacement);
		PIDDrive = new PIDController(0.3, 0, 0.7, encoderAverage, PIDDriveOutput);
		PIDDrive.setAbsoluteTolerance(0.75);
		//PIDDrive.setToleranceBuffer(20);
		PIDRotate = new PIDController(0.02, 0, 0.04, gyro, PIDRotateOutput);
		//PIDRotate.setAbsoluteTolerance(5.0);
		//PIDRotate.setToleranceBuffer(20);
	}
	
	@Override
	public void robotPeriodic() {
		SmartDashboard.putNumber("Gyro Angle ", gyro.getYaw());
		SmartDashboard.putNumber("Rate of Turning ", gyro.getRate());
		SmartDashboard.putNumber("Left Distance ", leftEncoder.getDistance());
		SmartDashboard.putNumber("Right Distance ", rightEncoder.getDistance());
		SmartDashboard.putNumber("Distance ", average(leftEncoder.getDistance(), rightEncoder.getDistance()));
		SmartDashboard.putNumber("Speed ", average(leftEncoder.getRate(), rightEncoder.getRate()));
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
		//m_autonomousCommand = m_chooser.getSelected();
		//m_autonomousCommand = new DriveByDistanceCommand(this, 5, gyro.getAngle());
		m_autonomousCommand = new RotateToAngleCommand(this, 100);

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
		//gyro.reset();
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
		johnBotsDriveTrainOfPain.arcadeDrive(-driveController.getY(GenericHID.Hand.kLeft), driveController.getX(GenericHID.Hand.kRight));
		//rightGrabber.set(operateController.getY(GenericHID.Hand.kLeft));
		//leftGrabber.set(-operateController.getY(GenericHID.Hand.kLeft));
		if (operateController.getBButton()) {
			gyro.reset();
		}
		if (operateController.getAButton()) {
			plsWork.set(DoubleSolenoid.Value.kForward);
		}
	}
	

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
