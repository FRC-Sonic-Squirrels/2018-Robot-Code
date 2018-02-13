/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team2930.robot;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import org.usfirst.frc.team2930.robot.commands.*;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.filters.*;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DigitalInput;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * 
 * project.
 */
public class Robot extends TimedRobot {
	/*public static final ExampleSubsystem kExampleSubsystem
			= new ExampleSubsystem();
	public static OI m_oi;*/

	Command m_autonomousCommand;
	SendableChooser<Point2D.Double> startPosition = new SendableChooser<>();
	public Point2D.Double leftStartPoint = new Double(46.96, 19.31);
	public Point2D.Double centerStartPoint = new Double(167.58, 19.31);
	public Point2D.Double rightStartPoint = new Double(277.65, 19.31);
	public Point2D.Double currentPoint = new Double();
	SendableChooser<Boolean> doScaleAuton = new SendableChooser<>();
	SendableChooser<Boolean> doLongPaths = new SendableChooser<>();
	SendableChooser<Boolean> pickUpExtraCube = new SendableChooser<>();
	public AHRS gyro;
	public Encoder leftEncoder, rightEncoder, leftElevator, armEncoder;
	public Spark leftIntake, rightIntake;
	public Spark elevator1, elevator2;
	public Spark rightDrive, leftDrive;
	public Spark arm;
	public DoubleSolenoid copyrightedPatentPendingSquirrelThumbTM;
	public DoubleSolenoid shiftyBusiness;
	public DoubleSolenoid intakeAngle;
	public DigitalInput testSensor;
	public LinearDigitalFilter gyroFilter, encoderAverageRateFilter;
	public PIDController PIDDrive, PIDRotate, PIDElevator, PIDArm;
	public SimplePIDOutput PIDDriveOutput, PIDRotateOutput, PIDArmThings, PIDElevatorThings;
	public EncoderAveragePIDSource encoderAverage;
	public EncoderAveragePIDSource encoderAverageRate;
	public DifferentialDrive johnBotsDriveTrainOfPain;
	public XboxController driveController, operateController;
	public String gameData;
	public final double ELEVATOR_TOP_VALUE = 0;
	public final double ELEVATOR_BOTTOM_VALUE = 0;
	public final double ARM_TOP_VALUE = 0;
	public final double ARM_BOTTOM_VALUE = 0;
	//PDP.reset(new PowerDistributionPanel(0));
	//SmartDashboard.PutData(PDP.get());
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
		//m_oi = new OI();
		//m_chooser.addDefault("Default Auto", new ExampleCommand());
		//m_chooser.addObject("My Auto", new MyAutoCommand());
		startPosition.addDefault("SELECT START POSITION", null);
		startPosition.addObject("Center", centerStartPoint);
		startPosition.addObject("Left", leftStartPoint);
		startPosition.addObject("Right", rightStartPoint);
		SmartDashboard.putData("Where are we?", startPosition);
		doScaleAuton.addDefault("Don't do scale auto", false);
		doScaleAuton.addObject("Do scale auto", true);
		SmartDashboard.putData("Shall we do scale auton?", doScaleAuton);
		doLongPaths.addDefault("Don't cross the field", false);
		doLongPaths.addObject("Cross the field", true);
		SmartDashboard.putData("Shall we cross the field?", doLongPaths);
		leftDrive = new Spark(0);
		leftDrive.setInverted(true);
		rightDrive = new Spark(1);
		rightDrive.setInverted(true);
		rightIntake = new Spark(2);
		leftIntake = new Spark(3);
		//elevator1 = new Spark(4);
		//elevator2 = new Spark(5);
		arm = new Spark(6);
		johnBotsDriveTrainOfPain = new DifferentialDrive(leftDrive, rightDrive);
		driveController = new XboxController(0);
		operateController = new XboxController(1);
		gyro = new AHRS(SPI.Port.kMXP);
		leftEncoder = new Encoder(/*0, 1*/9, 8);
		leftEncoder.setDistancePerPulse(2896.625/120);
		leftEncoder.setPIDSourceType(PIDSourceType.kDisplacement);
		rightEncoder = new Encoder(/*2, 3*/7, 6, true);
		rightEncoder.setDistancePerPulse(2896.625/120);
		rightEncoder.setPIDSourceType(PIDSourceType.kDisplacement);
		encoderAverage = new EncoderAveragePIDSource(leftEncoder, rightEncoder);
		encoderAverageRate = new EncoderAveragePIDSource(leftEncoder, rightEncoder);
		encoderAverageRate.setPIDSourceType(PIDSourceType.kRate);
		//leftElevator = new Encoder(5, 4);
		//testSensor = new DigitalInput(4);
		copyrightedPatentPendingSquirrelThumbTM = new DoubleSolenoid(0, 1);
		shiftyBusiness = new DoubleSolenoid(2, 3);
		intakeAngle = new DoubleSolenoid(4, 5);
		PIDDriveOutput = new SimplePIDOutput(0, 1);
		encoderAverageRateFilter = LinearDigitalFilter.movingAverage(encoderAverageRate, 30);
		PIDDrive = new PIDController(0.3, 0, 0.2/*0.7*/, encoderAverage, PIDDriveOutput/*, 0.02*/);
		PIDDrive.setAbsoluteTolerance(12.0);
		PIDDrive.setOutputRange(-0.9, 0.9);
		gyroFilter = LinearDigitalFilter.movingAverage(gyro, 50);
		PIDRotateOutput = new SimplePIDOutput(0, 1);
		PIDRotate = new PIDController(0.04, 0, 0.12, gyro, PIDRotateOutput/*, 0.02*/);
		PIDRotate.setInputRange(-180, 180);
		PIDRotate.setContinuous();
		PIDRotate.setAbsoluteTolerance(20.0);
		PIDRotate.setOutputRange(-0.9, 0.9);
		/*PIDElevatorThings = new SimplePIDOutput(0, 1);
		PIDElevator = new PIDController(0, 0, 0, 0, leftElevator, PIDElevatorThings/*, 0.02*//*);
		PIDElevator.setAbsoluteTolerance(0);
		PIDElevator.setOutputRange(0.9, -0.9);
		PIDArmThings = new SimplePIDOutput(0, 1);
		PIDArm = new PIDController(0, 0, 0, 0, armEncoder, PIDArmThings/*, 0.02*//*);
		PIDArm.setAbsoluteTolerance(0);
		PIDArm.setOutputRange(0.9, -0.9);*/
		PowerDistributionPanel PowerDistributionPanel = new PowerDistributionPanel(0);
	}
	
	@Override
	public void robotPeriodic() {
		SmartDashboard.putNumber("Left Distance ", leftEncoder.getDistance());
		SmartDashboard.putNumber("Right Distance ", rightEncoder.getDistance());
		SmartDashboard.putNumber("Distance ", average(leftEncoder.getDistance(), rightEncoder.getDistance()));
		SmartDashboard.putNumber("Speed ", average(leftEncoder.getRate(), rightEncoder.getRate()));
		SmartDashboard.putNumber("Gyro Angle", gyro.getYaw());
		SmartDashboard.putNumber("Rate of Turning", gyro.getRate());
		SmartDashboard.putNumber("Gyro Temperature (VERY IMPORTANT)", gyro.getTempC());
		/*SmartDashboard.putNumber("Encoder thingy filter value", encoderAverageRateFilter.pidGet());
		SmartDashboard.putNumber("Gyro thingy filter value", gyroFilter.pidGet());*/
		//SmartDashboard.putBoolean("Sensor value", testSensor.get());
		SmartDashboard.putString("In memoriam Mitchell", "Pineapple Mentality");
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
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		//m_autonomousCommand = new DriveToPointGroup(this, 0, -5, true);
		//m_autonomousCommand = new RotateToAngleCommand(this, 90);
		//m_autonomousCommand = new ShapeOfPowerGroup(this);
		if (pickUpExtraCube.getSelected()) {
			if (startPosition.getSelected().equals(centerStartPoint)) {
				currentPoint.setLocation(centerStartPoint);
				m_autonomousCommand = new CenterAutonSwitchWithCubeGroup(this);
			}
			else if (startPosition.getSelected().equals(leftStartPoint)) {
				currentPoint.setLocation(leftStartPoint);
				if (doScaleAuton.getSelected() && gameData.charAt(1) == 'L') {
					m_autonomousCommand = new LeftAutonScaleWithCubeGroup(this);
				}
				/*else if (doScaleAuton.getSelected() && gameData.charAt(1) == 'R' && doLongPaths.getSelected()) {
					m_autonomousCommand = new LeftAutonOppositeScaleGroup(this);
				}
				else if (gameData.charAt(0) == 'L') {
					m_autonomousCommand = new LeftAutonSwitchGroup(this);
				}*/
				else {
					m_autonomousCommand = new DriveToPointGroup(this, 46.96, 166.89);
				}
			}
			else if (startPosition.getSelected().equals(rightStartPoint)) {
				currentPoint.setLocation(rightStartPoint);
				if (doScaleAuton.getSelected() && gameData.charAt(1) == 'R') {
					m_autonomousCommand = new RightAutonScaleWithCubeGroup(this);
				}
				/*else if (doScaleAuton.getSelected() && gameData.charAt(1) == 'L' && doLongPaths.getSelected()) {
					m_autonomousCommand = new RightAutonOppositeScaleGroup(this);
				}
				else if (gameData.charAt(0) == 'R') {
					m_autonomousCommand = new RightAutonSwitchGroup(this);
				}*/
				else {
					m_autonomousCommand = new DriveToPointGroup(this, 277.65, 166.89);
				}
			}
		}
		else {
			if (startPosition.getSelected().equals(centerStartPoint)) {
				currentPoint.setLocation(centerStartPoint);
				m_autonomousCommand = new CenterAutonSwitchGroup(this);
			}
			else if (startPosition.getSelected().equals(leftStartPoint)) {
				currentPoint.setLocation(leftStartPoint);
				if (doScaleAuton.getSelected() && gameData.charAt(1) == 'L') {
					m_autonomousCommand = new LeftAutonScaleGroup(this);
				}
				else if (doScaleAuton.getSelected() && gameData.charAt(1) == 'R' && doLongPaths.getSelected()) {
					m_autonomousCommand = new LeftAutonOppositeScaleGroup(this);
				}
				else if (gameData.charAt(0) == 'L') {
					m_autonomousCommand = new LeftAutonSwitchGroup(this);
				}
				else {
					m_autonomousCommand = new DriveToPointGroup(this, 46.96, 166.89);
				}
			}
			else if (startPosition.getSelected().equals(rightStartPoint)) {
				currentPoint.setLocation(rightStartPoint);
				if (doScaleAuton.getSelected() && gameData.charAt(1) == 'R') {
					m_autonomousCommand = new RightAutonScaleGroup(this);
				}
				else if (doScaleAuton.getSelected() && gameData.charAt(1) == 'L' && doLongPaths.getSelected()) {
					m_autonomousCommand = new RightAutonOppositeScaleGroup(this);
				}
				else if (gameData.charAt(0) == 'R') {
					m_autonomousCommand = new RightAutonSwitchGroup(this);
				}
				else {
					m_autonomousCommand = new DriveToPointGroup(this, 277.65, 166.89);
				}
			}
		}
		
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
		
		//Drive
		johnBotsDriveTrainOfPain.arcadeDrive(driveController.getY(GenericHID.Hand.kLeft), driveController.getX(GenericHID.Hand.kRight));
		
		//Gear shifting
		//Press 'A' to piston (driver)
		if (driveController.getBumper(GenericHID.Hand.kLeft)) {
			shiftyBusiness.set(DoubleSolenoid.Value.kForward);
		}
		else {
			shiftyBusiness.set(DoubleSolenoid.Value.kReverse);
		}
		
		//Intake
		if (operateController.getTriggerAxis(GenericHID.Hand.kLeft) > 0.1) {
			rightIntake.set(-operateController.getTriggerAxis(GenericHID.Hand.kLeft)*0.65);
			leftIntake.set(operateController.getTriggerAxis(GenericHID.Hand.kLeft)*0.65);
		}
		//Outtake
		else if (operateController.getTriggerAxis(GenericHID.Hand.kRight) > 0.1) {
			rightIntake.set(operateController.getTriggerAxis(GenericHID.Hand.kRight)*0.65);
			leftIntake.set(-operateController.getTriggerAxis(GenericHID.Hand.kRight)*0.65);
		}
		else {
			rightIntake.set(0);
			leftIntake.set(0);
		}
		
		//Move elevator up
		if (operateController.getBumper(GenericHID.Hand.kLeft)) {
			PIDElevator.enable();
			PIDElevator.setSetpoint(ELEVATOR_TOP_VALUE);
			elevator1.set(PIDElevatorThings.getOutput());
			elevator2.set(PIDElevatorThings.getOutput());
		}
		else if (operateController.getY(GenericHID.Hand.kLeft) > 0.1) {
			PIDElevator.disable();
			elevator1.set(operateController.getY(GenericHID.Hand.kLeft));
			elevator2.set(operateController.getY(GenericHID.Hand.kLeft));
		}
		//Move elevator down
		else if (operateController.getBumper(GenericHID.Hand.kRight)) {
			PIDElevator.enable();
			PIDElevator.setSetpoint(ELEVATOR_BOTTOM_VALUE);
			elevator1.set(PIDElevatorThings.getOutput());
			elevator2.set(PIDElevatorThings.getOutput());
		}
		else if (operateController.getY(GenericHID.Hand.kLeft) < -0.1) {
			PIDElevator.disable();
			elevator1.set(operateController.getY(GenericHID.Hand.kLeft));
			elevator2.set(operateController.getY(GenericHID.Hand.kLeft));
		}
		else {
			PIDElevator.disable();
			elevator1.set(0);
			elevator2.set(0);
		}
		
		//Move arm up
		if (operateController.getPOV() >= 45 && operateController.getPOV() <= 135) {
			PIDArm.enable();
			PIDArm.setSetpoint(ARM_TOP_VALUE);
			arm.set(PIDArmThings.getOutput());
		}
		else if (operateController.getY(GenericHID.Hand.kRight) > 0.1) {
			PIDArm.disable();
			arm.set(operateController.getY(GenericHID.Hand.kRight));
		}
		//Move arm down
		else if (operateController.getPOV() >= 225 && operateController.getPOV() <= 315) {
			PIDArm.enable();
			PIDArm.setSetpoint(ARM_BOTTOM_VALUE);
			arm.set(PIDArmThings.getOutput());
			arm.set(PIDArmThings.getOutput());
		}
		else if (operateController.getY(GenericHID.Hand.kRight) < -0.1) {
			PIDArm.disable();
			arm.set(operateController.getY(GenericHID.Hand.kRight));
		}
		else {
			PIDArm.disable();
			arm.set(0);
		}
		
		//Open or close the grasper
		//Press 'A' to piston (operator)
		if (operateController.getAButton()) {
			copyrightedPatentPendingSquirrelThumbTM.set(DoubleSolenoid.Value.kForward);
		}
		else {
			copyrightedPatentPendingSquirrelThumbTM.set(DoubleSolenoid.Value.kReverse);
		}
		
		//Intake up for grabber grabbing
		if (operateController.getXButton() || driveController.getXButton()) {
			intakeAngle.set(DoubleSolenoid.Value.kReverse);
		}
		//Intake up for switch tossing
		/*else if (operateController.getBButton() || driveController.getBButton()) {
			intakeAngle.set(/*Middle setting*//*);
		}*/
		//Intake down for cube vacuuming
		else {
			intakeAngle.set(DoubleSolenoid.Value.kForward);
		}
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
