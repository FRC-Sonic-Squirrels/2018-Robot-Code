/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team2930.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
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
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends IterativeRobot {
	static final String kDefaultAuto = "Default";
	static final String kCustomAuto = "My Auto";
	String m_autoSelected;
	SendableChooser<String> m_chooser = new SendableChooser<>();
	DifferentialDrive JohnbotsDriveTrainOfPain;
	XboxController DriveController, OperateController;
	AHRS Gyro;
	Encoder RightEncoder/*, LeftEncoder*/;
	Spark LeftGrabber, RightGrabber;
	PIDController PIDDrive, PIDRotate;
	SimplePIDOutput PIDDriveOutput, PIDRotateOutput;
	EncoderAveragePIDSource EncoderAverage;
	public void encoderReset() {
		RightEncoder.reset();
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
		m_chooser.addDefault("Default Auto", kDefaultAuto);
		m_chooser.addObject("My Auto", kCustomAuto);
		SmartDashboard.putData("Auto choices", m_chooser);
		JohnbotsDriveTrainOfPain = new DifferentialDrive(new Spark(0), new Spark(1));
		DriveController = new XboxController(0);
		OperateController = new XboxController(1);
		Gyro = new AHRS(SerialPort.Port.kMXP);
		RightEncoder = new Encoder(9, 8);
		//LeftEncoder = new Encoder(2, 3);
		LeftGrabber = new Spark(4);
		RightGrabber = new Spark(5);
		RightEncoder.setDistancePerPulse(0.0043970539738375);
		//LeftEncoder.setDistancePerPulse(0.0043970539738375);
		EncoderAverage = new EncoderAveragePIDSource(RightEncoder/*LeftEncoder*/, RightEncoder);
		PIDDriveOutput = new SimplePIDOutput(0);
		PIDRotateOutput = new SimplePIDOutput(0);
		RightEncoder.setPIDSourceType(PIDSourceType.kDisplacement);
		//LeftEncoder.setPIDSourceType(PIDSourceType.kDisplacement);
		PIDDrive = new PIDController(0, 0, 0, EncoderAverage, PIDDriveOutput);
		PIDRotate = new PIDController(0, 0, 0, Gyro, PIDRotateOutput);
	}
	
	@Override
	public void robotPeriodic() {
		SmartDashboard.putNumber("Gyro Angle ", Gyro.getYaw());
		SmartDashboard.putNumber("Rate of Turning ", Gyro.getRate());
		SmartDashboard.putNumber("Distance ", /*average(*/RightEncoder.getDistance()/*, LeftEncoder.getDistance())*/);
		SmartDashboard.putNumber("Speed ", /*average(*/RightEncoder.getRate()/*, LeftEncoder.getRate())*/);
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * <p>You can add additional auto modes by adding additional comparisons to
	 * the switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		m_autoSelected = m_chooser.getSelected();
		//autoSelected = SmartDashboard.getString("Auto Selector", defaultAuto);
		System.out.println("Auto selected: " + m_autoSelected);
		Gyro.reset();
		encoderReset();
		String gameData;
		gameData = DriverStation.getInstance().getGameSpecificMessage();
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		switch (m_autoSelected) {
			case kCustomAuto:
				// Put custom auto code here
				break;
			case kDefaultAuto:
			default:
				break;
		}
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		JohnbotsDriveTrainOfPain.arcadeDrive(DriveController.getY(GenericHID.Hand.kLeft), DriveController.getX(GenericHID.Hand.kRight));
		RightGrabber.set(OperateController.getY(GenericHID.Hand.kLeft));
		LeftGrabber.set(-OperateController.getY(GenericHID.Hand.kLeft));
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}