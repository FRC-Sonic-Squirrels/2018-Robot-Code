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
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
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
	public Point2D.Double specialStartPoint = new Double(46.96, 19.31);
	public Point2D.Double currentPoint = new Double();
	SendableChooser<Boolean> doScaleAuton = new SendableChooser<>();
	SendableChooser<Boolean> doLongPaths = new SendableChooser<>();
	SendableChooser<Boolean> pickUpExtraCube = new SendableChooser<>();
	public AHRS gyro;
	public DigitalInput leftEncoderA, leftEncoderB, rightEncoderA, rightEncoderB, elevatorEncoderA, elevatorEncoderB, armEncoderA, armEncoderB;
	public Counter leftEncoderCounterA, leftEncoderCounterB, rightEncoderCounterA, rightEncoderCounterB, elevatorEncoderCounterA, elevatorEncoderCounterB, armEncoderCounterA, armEncoderCounterB;
	public Encoder leftEncoder, rightEncoder, elevatorEncoder, armEncoder;
	public Spark leftIntake, rightIntake;
	public Spark elevator1, elevator2;
	public Spark rightDrive, leftDrive;
	public Spark arm;
	public DoubleSolenoid copyrightedPatentPendingSquirrelThumbTM;
	public DoubleSolenoid shiftyBusiness;
	public DoubleSolenoid intakeAngle;
	public DoubleSolenoid intakeOpener;
	public DigitalInput testSensor;
	public LinearDigitalFilter gyroFilter, encoderAverageRateFilter;
	public PIDController drivePID, rotatePID, elevatorPID, armPID;
	public SimplePIDOutput drivePIDOutput, rotatePIDOutput, armPIDOutput, elevatorPIDOutput;
	public EncoderAveragePIDSource encoderAverage;
	public EncoderAveragePIDSource encoderAverageRate;
	public DifferentialDrive johnBotsDriveTrainOfPain;
	public XboxController driveController, operateController;
	public String gameData;
	public boolean isIntakeClosed = true;
	public boolean isGrasperClosed = true;
	public double intakeSpeed = 0;
	public final double ELEVATOR_TOP_VALUE = 20;
	public final double ELEVATOR_BOTTOM_VALUE = 3;
	public final double ARM_TOP_VALUE = 90;
	public final double ARM_BOTTOM_VALUE = 0;
	public void driveEncoderReset() {
		leftEncoder.reset();
		rightEncoder.reset();
	}
	private void flushBuffers() {
		drivePIDOutput.clearValues();
		rotatePIDOutput.clearValues();
		elevatorPIDOutput.clearValues();
		armPIDOutput.clearValues();
	}
	private double average(double x, double y) {
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
		startPosition.addDefault("SELECT START POSITION", specialStartPoint);
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
		pickUpExtraCube.addDefault("Don't grab extra cubes", false);
		pickUpExtraCube.addDefault("Pick up an extra cube", true);
		SmartDashboard.putData("Grab an extra cube?", pickUpExtraCube);
		SmartDashboard.putString("Elevator control type", "Manual");
		SmartDashboard.putString("Arm control type", "Manual");
		leftDrive = new Spark(0);
		leftDrive.setInverted(true);
		rightDrive = new Spark(1);
		rightDrive.setInverted(true);
		rightIntake = new Spark(2);
		leftIntake = new Spark(3);
		elevator1 = new Spark(4);
		elevator2 = new Spark(5);
		elevator2.setInverted(true);
		arm = new Spark(6);
		johnBotsDriveTrainOfPain = new DifferentialDrive(leftDrive, rightDrive);
		driveController = new XboxController(0);
		operateController = new XboxController(1);
		gyro = new AHRS(SPI.Port.kMXP);
		leftEncoderA = new DigitalInput(9);
		leftEncoderB = new DigitalInput(8);
		leftEncoderCounterA = new Counter(leftEncoderA);
		leftEncoderCounterA.setMaxPeriod(0.5);
		leftEncoderCounterB = new Counter(leftEncoderB);
		leftEncoderCounterB.setMaxPeriod(0.5);
		leftEncoder = new Encoder(leftEncoderA, leftEncoderB);
		leftEncoder.setDistancePerPulse(1.0/24.0);
		leftEncoder.setPIDSourceType(PIDSourceType.kDisplacement);
		rightEncoderA = new DigitalInput(7);
		rightEncoderB = new DigitalInput(6);
		rightEncoderCounterA = new Counter(rightEncoderA);
		rightEncoderCounterA.setMaxPeriod(0.5);
		rightEncoderCounterB = new Counter(rightEncoderB);
		rightEncoderCounterB.setMaxPeriod(0.5);
		rightEncoder = new Encoder(rightEncoderA, rightEncoderB, true);
		rightEncoder.setDistancePerPulse(1.0/24.0);
		rightEncoder.setPIDSourceType(PIDSourceType.kDisplacement);
		elevatorEncoderA = new DigitalInput(0);
		elevatorEncoderB = new DigitalInput(1);
		elevatorEncoderCounterA = new Counter(elevatorEncoderA);
		elevatorEncoderCounterA.setMaxPeriod(0.5);
		elevatorEncoderCounterB = new Counter(elevatorEncoderB);
		elevatorEncoderCounterB.setMaxPeriod(0.5);
		elevatorEncoder = new Encoder(elevatorEncoderA, elevatorEncoderB);
		elevatorEncoder.setDistancePerPulse(30.75 / 645.0);
		elevatorEncoder.setPIDSourceType(PIDSourceType.kDisplacement);
		elevatorEncoder.setMaxPeriod(0.5);
		armEncoderA = new DigitalInput(2);
		armEncoderB = new DigitalInput(3);
		armEncoderCounterA = new Counter(armEncoderA);
		armEncoderCounterA.setMaxPeriod(0.5);
		armEncoderCounterB = new Counter(armEncoderB);
		armEncoderCounterB.setMaxPeriod(0.5);
		armEncoder = new Encoder(armEncoderA, armEncoderB);
		armEncoder.setDistancePerPulse(90 / 30.75);
		armEncoder.setPIDSourceType(PIDSourceType.kDisplacement);
		armEncoder.setMaxPeriod(0.5);
		encoderAverage = new EncoderAveragePIDSource(leftEncoder, rightEncoder);
		encoderAverageRate = new EncoderAveragePIDSource(leftEncoder, rightEncoder);
		encoderAverageRate.setPIDSourceType(PIDSourceType.kRate);
		//leftElevator = new Encoder(5, 4);
		//testSensor = =new DigitalInput(4);
		intakeOpener = new DoubleSolenoid(0, 1);
		copyrightedPatentPendingSquirrelThumbTM = new DoubleSolenoid(2, 3);
		shiftyBusiness = new DoubleSolenoid(4, 5);
		intakeAngle = new DoubleSolenoid(6, 7);
		drivePIDOutput = new EncoderBasedPIDOutput(0, 5, 0.5, rightEncoderCounterA, rightEncoderCounterB, leftEncoderCounterA, leftEncoderCounterB);
		encoderAverageRateFilter = LinearDigitalFilter.movingAverage(encoderAverageRate, 30);
		drivePID = new PIDController(0.3, 0, 0.2, encoderAverage, drivePIDOutput/*, 0.02*/);
		drivePID.setAbsoluteTolerance(6.0);
		//PIDDrive.setOutputRange(-0.9, 0.9);
		gyroFilter = LinearDigitalFilter.movingAverage(gyro, 50);
		rotatePIDOutput = new SimplePIDOutput(0, 1);
		rotatePID = new PIDController(0.06, 0, 0.12, gyro, rotatePIDOutput/*, 0.02*/);
		rotatePID.setInputRange(-180, 180);
		rotatePID.setContinuous();
		rotatePID.setAbsoluteTolerance(10.0);
		//PIDRotate.setOutputRange(-0.9, 0.9);
		elevatorPIDOutput = new EncoderBasedPIDOutput(0, 5, 0.5, elevatorEncoderCounterA, elevatorEncoderCounterB);
		elevatorPID = new PIDController(0.25, 0, 0, 0, elevatorEncoder, elevatorPIDOutput/*, 0.02*/);
		elevatorPID.setAbsoluteTolerance(0);
		elevatorPID.setOutputRange(-0.5, 0.5);
		armPIDOutput = new EncoderBasedPIDOutput(0, 5, 0.5, armEncoderCounterA, armEncoderCounterB);
		armPID = new PIDController(0.125, 0, 0, 0, armEncoder, armPIDOutput/*, 0.02*/);
		armPID.setAbsoluteTolerance(10);
		armPID.setOutputRange(-0.5, 0.5);
		@SuppressWarnings("unused")
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
		SmartDashboard.putString("In memoriam Mitchell", "Pineapple Mentality");
		SmartDashboard.putNumber("Elevator height", elevatorEncoder.getDistance());
		SmartDashboard.putNumber("Arm height", armEncoder.getDistance());
		if (elevatorEncoder.getStopped()) {
			SmartDashboard.putString("Elevator control type", "Manual");	
		}
		else {
			SmartDashboard.putString("Elevator control type", "Automatic");
		}
		if (armEncoder.getStopped()) {
			SmartDashboard.putString("Arm control type", "Manual");
		}
		else {
			SmartDashboard.putString("Arm control type", "Automatic");
		}
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
		currentPoint.setLocation(0, 0);
		//m_autonomousCommand = new DriveToPointGroup(this, 0, 120);
		//m_autonomousCommand = new RotateToAngleCommand(this, 90);
		//m_autonomousCommand = new ShapeOfPowerGroup(this);
		if (startPosition.getSelected().equals(specialStartPoint)) {
			m_autonomousCommand = new DriveToPointGroup(this, 46.96, 166.89);
		}
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
					m_autonomousCommand = new RightAutonScaleWithCubeGroup(this);
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
		//copyrightedPatentPendingSquirrelThumbTM.set(DoubleSolenoid.Value.kForward);
		//intakeOpener.set(DoubleSolenoid.Value.kForward);
		elevatorEncoder.reset();
		armEncoder.reset();
		gyro.reset();
		driveEncoderReset();
		flushBuffers();
		drivePID.enable();
		drivePID.setSetpoint(0);
		rotatePID.enable();
		rotatePID.setSetpoint(0);
		elevatorPID.enable();
		elevatorPID.setSetpoint(0);
		armPID.enable();
		armPID.setSetpoint(0);
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		shiftyBusiness.set(DoubleSolenoid.Value.kForward);
		Scheduler.getInstance().run();
		johnBotsDriveTrainOfPain.arcadeDrive(-drivePIDOutput.getOutput(), rotatePIDOutput.getOutput());
		elevator1.set(-elevatorPIDOutput.getOutput());
		elevator2.set(-elevatorPIDOutput.getOutput());
		arm.set(-armPIDOutput.getOutput());
    	rightIntake.set(intakeSpeed);
    	leftIntake.set(-intakeSpeed);
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
		copyrightedPatentPendingSquirrelThumbTM.set(DoubleSolenoid.Value.kForward);
		intakeOpener.set(DoubleSolenoid.Value.kForward);
		drivePID.disable();
		rotatePID.disable();
		elevatorPID.disable();
		armPID.disable();
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
		//Press 'A' left bumper to piston (driver)
		if (driveController.getBumper(GenericHID.Hand.kLeft)) {
			shiftyBusiness.set(DoubleSolenoid.Value.kReverse);
		}
		else {
			shiftyBusiness.set(DoubleSolenoid.Value.kForward);
		}
		
		//Intake
		if (operateController.getTriggerAxis(GenericHID.Hand.kLeft) > 0.1) {
			rightIntake.set(operateController.getTriggerAxis(GenericHID.Hand.kLeft)*0.65);
			leftIntake.set(-operateController.getTriggerAxis(GenericHID.Hand.kLeft)*0.65);
		}
		//Outtake
		else if (operateController.getTriggerAxis(GenericHID.Hand.kRight) > 0.1) {
			rightIntake.set(-operateController.getTriggerAxis(GenericHID.Hand.kRight)*0.65);
			leftIntake.set(operateController.getTriggerAxis(GenericHID.Hand.kRight)*0.65);
		}
		else {
			rightIntake.set(0);
			leftIntake.set(0);
		}
		
		//Move elevator up
		if (operateController.getBumper(GenericHID.Hand.kRight)) {
			elevatorPID.enable();
			elevatorPID.setSetpoint(ELEVATOR_TOP_VALUE);
			elevator1.set(-elevatorPIDOutput.getOutput());
			elevator2.set(-elevatorPIDOutput.getOutput());
		}
		else if (operateController.getY(GenericHID.Hand.kRight) > 0.1) {
			elevatorPID.disable();
			elevator1.set(operateController.getY(GenericHID.Hand.kRight));
			elevator2.set(operateController.getY(GenericHID.Hand.kRight));
		}
		//Move elevator down
		else if (operateController.getBumper(GenericHID.Hand.kLeft)) {
			elevatorPID.enable();
			elevatorPID.setSetpoint(ELEVATOR_BOTTOM_VALUE);
			elevator1.set(-elevatorPIDOutput.getOutput());
			elevator2.set(-elevatorPIDOutput.getOutput());
		}
		else if (operateController.getY(GenericHID.Hand.kRight) < -0.1) {
			elevatorPID.disable();
			elevator1.set(operateController.getY(GenericHID.Hand.kRight));
			elevator2.set(operateController.getY(GenericHID.Hand.kRight));
		}
		else {
			elevatorPID.disable();
			elevator1.set(0);
			elevator2.set(0);
		}
		
		if (isGrasperClosed) {
			//Move arm up
			if (operateController.getPOV() >= 45 && operateController.getPOV() <= 135) {
				armPID.enable();
				armPID.setSetpoint(ARM_TOP_VALUE);
				arm.set(-armPIDOutput.getOutput());
			}
			else if (operateController.getY(GenericHID.Hand.kLeft) > 0.1) {
				armPID.disable();
				arm.set(operateController.getY(GenericHID.Hand.kLeft));
			}
			//Move arm down
			else if (operateController.getPOV() >= 225 && operateController.getPOV() <= 315) {
				armPID.enable();
				armPID.setSetpoint(ARM_BOTTOM_VALUE);
				arm.set(-armPIDOutput.getOutput());
			}
			else if (operateController.getY(GenericHID.Hand.kLeft) < -0.1) {
				armPID.disable();
				arm.set(operateController.getY(GenericHID.Hand.kLeft));
			}
			else {
				armPID.disable();
				arm.set(0);
			}
		}
		
		//Open or close the grasper
		//Press 'A' to piston (operator)
		if (operateController.getBButtonPressed()) {
			isGrasperClosed = !isGrasperClosed;
		}
		if (isGrasperClosed) {
			copyrightedPatentPendingSquirrelThumbTM.set(DoubleSolenoid.Value.kForward);
		}
		else {
			copyrightedPatentPendingSquirrelThumbTM.set(DoubleSolenoid.Value.kReverse);
		}
		
		//Intake up for grabber grabbing
		if (operateController.getXButton()) {
			intakeAngle.set(DoubleSolenoid.Value.kReverse);
		}
		//Intake down for cube vacuuming
		else {
			intakeAngle.set(DoubleSolenoid.Value.kForward);
		}
		
		//Intake open or closed for cube grabbing
		if (operateController.getAButtonPressed()) {
			isIntakeClosed = !isIntakeClosed;
		}
		if (isIntakeClosed) {
			intakeOpener.set(DoubleSolenoid.Value.kReverse);
		}
		else {
			intakeOpener.set(DoubleSolenoid.Value.kForward);
		}
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
