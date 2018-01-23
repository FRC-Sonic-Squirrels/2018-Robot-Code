package org.usfirst.frc.team2930.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Encoder;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.networktables.*;
import edu.wpi.first.wpilibj.RobotDrive;
import org.usfirst.frc.team2930.robot.SimplePIDOutput;
import org.usfirst.frc.team2930.robot.EncoderAveragePIDSource;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */

public class Robot extends IterativeRobot {
	public static final double HIGHSPEEDTHRESHOLD = 5.75;
	public static final double LOWSPEEDTHRESHOLD = 4.25;	
	String autoSelected;
	String allianceSelected;
	SendableChooser<String> autoChooser = new SendableChooser<>();
	SendableChooser<String> allianceChooser = new SendableChooser<>();
	AHRS Gyro;
	DoubleSolenoid Shifter;
	DoubleSolenoid GearReleaser;
	DoubleSolenoid GearCatcher;
	Encoder LeftEncoder;
	Encoder RightEncoder;
	EncoderAveragePIDSource EncoderAverage;
	NetworkTable VisionDataTable;
	PIDController PIDDrive;
	PIDController PIDRotate;
	SimplePIDOutput PIDDriveOutput;
	SimplePIDOutput PIDRotateOutput;
	RobotDrive AllTheDrive;
	Spark Climber;
	Spark Climber2;
	XboxController DriveController;
	XboxController OperateController;
	boolean IsHighGear;
	double AverageSpeed;
	double AverageDistance;
	double waitTime;
	int switchState;
	//Vision Data
	boolean ComputedPosition;
	boolean DetectedTwoCentroids;
	String TimeStamp;
	double[] Rotation;
	double[] Translation;
	double targetDistance;
	double targetAngle;
	double targetArctan;
	double autonAngleToTarget;
	boolean visionPathCalculated;
	double AllianceAngling(double Angle) {
		if (allianceChooser.getSelected() == "redAlliance") {
			return Angle;
		}
		else {
			return -Angle;
		}
	}	
	double BoilerGearPath(double d) {
		if (d < 2) {
			return 0;
		}
		else {
			return ArcDegrees(-5, d-2);
		}
	}
   double LoadingGearPath(double d) {
   		if (d < 2) {
   			return 0;
   		}
   		else {
   			return ArcDegrees(5, d-2);
   		}
   	}
   double BoilerGearPathContinued(double d) {
		   return (ArcDegrees(-17, d) + AllianceAngling(30));
   }
   double LoadingGearPathContinued(double d) {
	   return (ArcDegrees(17, d) + AllianceAngling(-30));
   }
   double Average(double x, double y) {
	   return (x + y) / 2;
	}
	void EncoderReset() {
		RightEncoder.reset();
		LeftEncoder.reset();
	}
	double ArcDegrees(double r , double d) {
		return AllianceAngling(((180*d) / (Math.PI*r)));
	}   
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	
	@Override
	public void robotInit() {
		autoChooser.addDefault("Default Auto", "defaultAuto");
		//autoChooser.addObject("Boiler Gear With Curve", "BoilerGearCurve");
		autoChooser.addObject("Boiler Gear Without Curve", "BoilerGearLine");
		//autoChooser.addObject("Loading Gear With Curve", "LoadingGearCurve");
		autoChooser.addObject("Loading Gear Without Curve", "LoadingGearLine");
		autoChooser.addObject("Center Gear", "CenterGear");
		autoChooser.addObject("Test", "test");
		SmartDashboard.putData("Auto choices", autoChooser);
		allianceChooser.addDefault("Red Alliance", "redAlliance");
		allianceChooser.addObject("Blue Alliance", "blueAlliance");
		SmartDashboard.putData("Alliance", allianceChooser);
		DriveController = new XboxController(0);
		OperateController = new XboxController(1);
		Shifter = new DoubleSolenoid(0,1);
		GearReleaser = new DoubleSolenoid(2,3);
		GearCatcher = new DoubleSolenoid(4, 5);
		AllTheDrive = new RobotDrive(1,0);
		Climber = new Spark(4);
		Climber2 = new Spark(2);
		LeftEncoder = new Encoder(0,1,false);
		RightEncoder = new Encoder(2,3,true);
		try {
			/***********************************************************************
			 * navX-MXP:
			 * - Communication via RoboRIO MXP (SPI, I2C, TTL UART) and USB.            
			 * - See http://navx-mxp.kauailabs.com/guidance/selecting-an-interface.
			 * 
			 * navX-Micro:
			 * - Communication via I2C (RoboRIO MXP or Onboard) and USB.
			 * - See http://navx-micro.kauailabs.com/guidance/selecting-an-interface.
			 * 
			 * Multiple navX-model devices on a single robot are supported.
			 ************************************************************************/
            Gyro = new AHRS(SPI.Port.kMXP); 
        } catch (RuntimeException ex ) {
            DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
        }
		Gyro.reset();
		//Gyro.calibrate();
		VisionDataTable = NetworkTable.getTable("VisionDataTable");
		IsHighGear = true;
		switchState = 0;
		EncoderAverage = new EncoderAveragePIDSource(LeftEncoder, RightEncoder);
		PIDDriveOutput = new SimplePIDOutput(0);
		RightEncoder.setPIDSourceType(PIDSourceType.kDisplacement);
		PIDDrive = new PIDController(0.3, 0, 0.7, EncoderAverage, PIDDriveOutput);
		PIDDrive.setAbsoluteTolerance(0.75);
		PIDDrive.setToleranceBuffer(20);
		PIDRotateOutput = new SimplePIDOutput(0);
		PIDRotate = new PIDController(0.02, 0, 0.04, Gyro, PIDRotateOutput);
		PIDRotate.setAbsoluteTolerance(15.0);
		PIDRotate.setToleranceBuffer(20);
		RightEncoder.setDistancePerPulse(1 / 323.46875);
		LeftEncoder.setDistancePerPulse(1 / 323.46875);
		visionPathCalculated = false;
		DetectedTwoCentroids = false;
		ComputedPosition = false;
	}
	
	@Override
	public void robotPeriodic() {
		AverageDistance = Average(RightEncoder.getDistance(), LeftEncoder.getDistance());		
		AverageSpeed = Average(RightEncoder.getRate(), LeftEncoder.getRate());
		SmartDashboard.putString("Hello World  ", "Ready, Player One");
		SmartDashboard.putNumber("Right Encoder Value  ", RightEncoder.getDistance());
		SmartDashboard.putNumber("Left Encoder Value  ", LeftEncoder.getDistance());
		SmartDashboard.putNumber("Average Distance  ", AverageDistance);
		SmartDashboard.putBoolean("Low Gear?  ", IsHighGear);
		SmartDashboard.putNumber("Right Speed  ", RightEncoder.getRate());
		SmartDashboard.putNumber("Left Speed  ", LeftEncoder.getRate());
		SmartDashboard.putNumber("Average Speed  ", AverageSpeed);
		SmartDashboard.putNumber("Autonomous State  ", switchState);
		SmartDashboard.putNumber("Gyro Angle in degrees  ", Gyro.getYaw());
		SmartDashboard.putNumber("Rate of Turning  ", Gyro.getRate());
		SmartDashboard.putNumber("Arc Degrees  ", ArcDegrees(3, AverageDistance));
		SmartDashboard.putNumber("PID Rotate Output  ", PIDRotateOutput.getOutput());
		SmartDashboard.putNumber("PID Drive Output  ", PIDDriveOutput.getOutput());
		SmartDashboard.putString("In Memoriam Mitchell  ", "Pineapple Mentality");
		autoSelected = autoChooser.getSelected();
		SmartDashboard.putData("Auto choices", autoChooser);
		SmartDashboard.putString("Auto Mode  ", autoSelected);
		allianceSelected = allianceChooser.getSelected();
		SmartDashboard.putData("Alliance", allianceChooser);
		
		//Vision Data
		double[] defaultArray = new double[1];
		defaultArray[0] = 360;
		ComputedPosition = VisionDataTable.getBoolean("ComputedPosition", false);
		SmartDashboard.putBoolean("ComputedPosition ", ComputedPosition);
		DetectedTwoCentroids = VisionDataTable.getBoolean("DetectedTwoCentroids", false);
		SmartDashboard.putBoolean("DetectedTwoCentroids ", DetectedTwoCentroids);
		TimeStamp = VisionDataTable.getString("TimeStamp", "no Time Stamp");
		SmartDashboard.putString("TimeStamp", TimeStamp);
		Translation = VisionDataTable.getNumberArray("Translation", defaultArray);
		if (Translation.length == 3) {
			targetDistance = Math.sqrt(Math.pow(Translation[0], 2) + Math.pow(Translation[2], 2)) / 12;
			targetArctan = Math.atan2(Translation[0], Translation[2]);
			targetAngle = targetArctan * (180/Math.PI);
			SmartDashboard.putNumber("TargetAngle ", targetAngle);
		}
		else {
			targetDistance = -1;
		}
		SmartDashboard.putNumber("TargetDistance ", targetDistance);
	}
	
	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	
	@Override
	public void autonomousInit() {
		System.out.println("Auto selected: " + autoSelected);
		switchState = 0;
		autonAngleToTarget = 0;
		Gyro.reset();
		EncoderReset();
		PIDDrive.reset();
		PIDDrive.enable();
		PIDRotate.reset();
		PIDRotate.enable();
		waitTime = 0;
	}

	/**
	 * This function is called periodically during autonomous
	 */
	
	@Override
	public void autonomousPeriodic() {
		VisionDataTable.putBoolean("RunVision", true);
		switch (autoSelected) {
		case "BoilerGearLine":
			switch (switchState) {
			case 0:
				PIDDrive.setSetpoint(6.6);
				PIDRotate.setSetpoint(0);
				switchState = 10;
				break;
				
			case 10:
				AllTheDrive.arcadeDrive(-PIDDriveOutput.getOutput(), PIDRotateOutput.getOutput(), false);
				if (PIDDrive.onTarget()) {
					switchState = 20;
					PIDDrive.disable();
					PIDRotate.setSetpoint(AllianceAngling(-60));
				}
				break;
				
			case 20:
				AllTheDrive.arcadeDrive(0, PIDRotateOutput.getOutput(), false);
				if (PIDRotate.onTarget()) {
					switchState = 30;
					PIDDrive.disable();
					PIDRotate.disable();
				}
				break;
				
			case 30:
				if (ComputedPosition && DetectedTwoCentroids) {
					PIDDrive.enable();
					PIDRotate.enable();
					PIDDrive.setSetpoint(AverageDistance + targetDistance - 1);
					autonAngleToTarget = targetAngle + Gyro.getYaw();
					PIDRotate.setSetpoint(autonAngleToTarget);
					switchState = 40;
				}
				break;
				
			case 40:
				AllTheDrive.arcadeDrive(-PIDDriveOutput.getOutput(), PIDRotateOutput.getOutput(), false);
				if (PIDDrive.onTarget()) {
					if (waitTime < 100) {
						waitTime++;
						if (waitTime < 20) {
							PIDRotate.setSetpoint(autonAngleToTarget);
						} else if (waitTime < 30) {
							PIDRotate.setSetpoint(autonAngleToTarget + 12);
						} else if (waitTime < 40) {
							PIDRotate.setSetpoint(autonAngleToTarget - 12);
						} else if (waitTime < 70) {
							PIDRotate.setSetpoint(autonAngleToTarget);
						} else {
							GearReleaser.set(DoubleSolenoid.Value.kForward);
						}						
					}
					else {
						waitTime = 0;
						EncoderReset();
						PIDDrive.setSetpoint(-2.5);
						PIDRotate.setSetpoint(AllianceAngling(-60));
						switchState = 50;
					}
				}
				break;
			
			case 50:
				AllTheDrive.arcadeDrive(-PIDDriveOutput.getOutput(), PIDRotateOutput.getOutput(), false);
				if (PIDDrive.onTarget()) {
					switchState = 60;
					GearReleaser.set(DoubleSolenoid.Value.kReverse);
					PIDDrive.disable();
					PIDRotate.setSetpoint(AllianceAngling(30));
				}
				break;
				
			case 60:
				AllTheDrive.arcadeDrive(0, PIDRotateOutput.getOutput(), false);
				if (PIDRotate.onTarget()) {
					switchState = 70;
					EncoderReset();
					PIDDrive.enable();
					PIDRotate.enable();
					PIDDrive.setSetpoint(26.70);
				}
				break;
				
			case 70:
				PIDRotate.setSetpoint(BoilerGearPathContinued(AverageDistance));
				AllTheDrive.arcadeDrive(-PIDDriveOutput.getOutput(), PIDRotateOutput.getOutput(), false);
				if (PIDDrive.onTarget()) {
					switchState = 80;
				}
				break;
			}
			break;
		
		case "LoadingGearLine":
			switch (switchState) {
			case 0:
				PIDDrive.setSetpoint(6.6);
				PIDRotate.setSetpoint(0);
				switchState = 10;
				break;
				
			case 10:
				AllTheDrive.arcadeDrive(-PIDDriveOutput.getOutput(), PIDRotateOutput.getOutput(), false);
				if (PIDDrive.onTarget()) {
					switchState = 20;
					PIDDrive.disable();
					PIDRotate.setSetpoint(AllianceAngling(60));
				}
				break;
				
			case 20:
				AllTheDrive.arcadeDrive(0, PIDRotateOutput.getOutput(), false);
				if (PIDRotate.onTarget()) {
					switchState = 30;
					PIDDrive.disable();
					PIDRotate.disable();
				}
				break;

			case 30:
				if (ComputedPosition && DetectedTwoCentroids) {
					PIDDrive.enable();
					PIDRotate.enable();
					PIDDrive.setSetpoint(AverageDistance + targetDistance - 1);
					autonAngleToTarget = targetAngle + Gyro.getYaw();
					PIDRotate.setSetpoint(autonAngleToTarget);
					switchState = 40;
				}
				break;
				
			case 40:
				AllTheDrive.arcadeDrive(-PIDDriveOutput.getOutput(), PIDRotateOutput.getOutput(), false);
				if (PIDDrive.onTarget()) {
					if (waitTime < 100) {
						waitTime++;
						if (waitTime < 20) {
							PIDRotate.setSetpoint(autonAngleToTarget);
						} else if (waitTime < 30) {
							PIDRotate.setSetpoint(autonAngleToTarget + 12);
						} else if (waitTime < 40) {
							PIDRotate.setSetpoint(autonAngleToTarget - 12);
						} else if (waitTime < 70) {
							PIDRotate.setSetpoint(autonAngleToTarget);
						} else {
							GearReleaser.set(DoubleSolenoid.Value.kForward);
						}						
					}
					else {
						waitTime = 0;
						EncoderReset();
						PIDDrive.setSetpoint(-2.5);
						PIDRotate.setSetpoint(AllianceAngling(60));
						switchState = 50;
					}
				}
				break;
				
			case 50:
				AllTheDrive.arcadeDrive(-PIDDriveOutput.getOutput(), PIDRotateOutput.getOutput(), false);
				if (PIDDrive.onTarget()) {
					GearReleaser.set(DoubleSolenoid.Value.kReverse);
					PIDDrive.disable();
					PIDRotate.setSetpoint(AllianceAngling(-30));
					switchState = 60;
				}
				break;
				
			case 60:
				AllTheDrive.arcadeDrive(0, PIDRotateOutput.getOutput(), false);
				if (PIDRotate.onTarget()) {
					switchState = 70;
					EncoderReset();
					PIDDrive.enable();
					PIDDrive.setSetpoint(30.16);
				}
				break;
				
			case 70:
				PIDRotate.setSetpoint(LoadingGearPathContinued(AverageDistance));
				AllTheDrive.arcadeDrive(-PIDDriveOutput.getOutput(), PIDRotateOutput.getOutput(), false);
				if (PIDDrive.onTarget()) {
					switchState = 80;
				}
				break;
			}
			break;

		case "CenterGear":
			switch (switchState) {
			case 0:
				PIDDrive.setSetpoint(6.5);
				PIDRotate.setSetpoint(0);
				switchState = 10;
				autonAngleToTarget = 0;
				break;
				
			case 10:
				AllTheDrive.arcadeDrive(-PIDDriveOutput.getOutput(), PIDRotateOutput.getOutput(), false);
				if (PIDDrive.onTarget()) {
					if (waitTime < 100) {
						waitTime++;
						if (waitTime < 20) {
							PIDRotate.setSetpoint(autonAngleToTarget);
						} else if (waitTime < 30) {
							PIDRotate.setSetpoint(autonAngleToTarget + 12);
						} else if (waitTime < 40) {
							PIDRotate.setSetpoint(autonAngleToTarget - 12);
						} else if (waitTime < 70) {
							PIDRotate.setSetpoint(autonAngleToTarget);
						} else {
							GearReleaser.set(DoubleSolenoid.Value.kForward);
						}						
					}
					else {
						waitTime = 0;
						EncoderReset();
						PIDDrive.setSetpoint(-2.5);
						PIDRotate.setSetpoint(0);
						switchState = 20;
					}
				}
				break;
				
			case 20:
				AllTheDrive.arcadeDrive(-PIDDriveOutput.getOutput(), PIDRotateOutput.getOutput(), false);
				if (PIDDrive.onTarget()) {
					GearReleaser.set(DoubleSolenoid.Value.kReverse);
					PIDDrive.disable();
					PIDRotate.disable();
					switchState = 80;
				}
				break;
				
			}
			break;
		
		case "test":
			switch (switchState) {
			case 0:
				GearReleaser.set(DoubleSolenoid.Value.kForward);
				if (waitTime < 50) {
					waitTime++;
				}
				else {
					waitTime = 0;
					switchState = 1;
				}
				break;
				
			case 1:
				GearReleaser.set(DoubleSolenoid.Value.kReverse);
				break;
			}
			break;
		}
	}
	
	public void teleopInit() {
		EncoderReset();
		PIDDrive.reset();
		PIDRotate.reset();
	}
	
	/**
	 * This function is called periodically during operator control
	 */
	
	@Override
	public void teleopPeriodic() {
		//At start
		VisionDataTable.putBoolean("RunVision", true);
		if (DriveController.getAButton()) {
			if (visionPathCalculated) {
				AllTheDrive.arcadeDrive(-PIDDriveOutput.getOutput(), PIDRotateOutput.getOutput(), false);
			}
			else {
				if (DetectedTwoCentroids && ComputedPosition) {
					visionPathCalculated = true;
					PIDDrive.enable();
					PIDRotate.enable();
					PIDDrive.setSetpoint(AverageDistance + targetDistance - 1);
					PIDRotate.setSetpoint(targetAngle + Gyro.getYaw());
					AllTheDrive.arcadeDrive(-PIDDriveOutput.getOutput(), PIDRotateOutput.getOutput(), false);
				}
				else {
					PIDDrive.disable();
					PIDRotate.disable();
					double moveValue = DriveController.getY(GenericHID.Hand.kLeft);
					double rotateValue = DriveController.getX(GenericHID.Hand.kRight);
					AllTheDrive.arcadeDrive(moveValue, rotateValue, true);
					EncoderReset();
				}
			}
		}
		else if (DriveController.getXButton()) {
			PIDDrive.disable();
			PIDRotate.enable();
			PIDRotate.setSetpoint(-60);
			double moveValue = DriveController.getY(GenericHID.Hand.kLeft);
			AllTheDrive.arcadeDrive(moveValue, PIDRotateOutput.getOutput(), false);
		}
		else if (DriveController.getYButton() /*DriveController.getAButton()*/) {
			PIDDrive.disable();
			PIDRotate.enable();
			PIDRotate.setSetpoint(0);
			double moveValue = DriveController.getY(GenericHID.Hand.kLeft);
			AllTheDrive.arcadeDrive(moveValue, PIDRotateOutput.getOutput(), false);
		}
		else if (DriveController.getBButton()) {
			PIDDrive.disable();
			PIDRotate.enable();
			PIDRotate.setSetpoint(60);
			double moveValue = DriveController.getY(GenericHID.Hand.kLeft);
			AllTheDrive.arcadeDrive(moveValue, PIDRotateOutput.getOutput(), false);
		}
		else if (DriveController.getBumper(GenericHID.Hand.kLeft)) {
			PIDDrive.disable();
			PIDRotate.enable();
			PIDRotate.setSetpoint(-90);
			double moveValue = DriveController.getY(GenericHID.Hand.kLeft);
			AllTheDrive.arcadeDrive(moveValue, PIDRotateOutput.getOutput(), false);
		}
		else {
			PIDDrive.disable();
			PIDRotate.disable();
			double moveValue = DriveController.getY(GenericHID.Hand.kLeft);
			double rotateValue = DriveController.getX(GenericHID.Hand.kRight);
			AllTheDrive.arcadeDrive(moveValue, rotateValue, true);
			EncoderReset();
		}

		//Shift if right bumper is pressed
		if (DriveController.getBumper(GenericHID.Hand.kRight)) {
			Shifter.set(DoubleSolenoid.Value.kReverse);
			IsHighGear = false;
		}
		else {
			Shifter.set(DoubleSolenoid.Value.kForward);
			IsHighGear = true;
		}
		
		//Rumble if vision
		if (DetectedTwoCentroids && ComputedPosition) {
			DriveController.setRumble(GenericHID.RumbleType.kLeftRumble, 2930);
			DriveController.setRumble(GenericHID.RumbleType.kRightRumble, 2930);
		}
		else {
			DriveController.setRumble(GenericHID.RumbleType.kLeftRumble, -2930);
			DriveController.setRumble(GenericHID.RumbleType.kRightRumble, -2930);
		}
		if (!DriveController.getAButton()) {
			visionPathCalculated = false;
		}

		//Operator Controller Commands
			
		//Set ClimbMotor output equal to left trigger (LT)
		double climberOutput = OperateController.getTriggerAxis(GenericHID.Hand.kLeft) +
				DriveController.getTriggerAxis(GenericHID.Hand.kLeft);
		Climber.set(climberOutput);
		Climber2.set(climberOutput);

		//Push the gear (Press A to piston)
		if (DriveController.getStartButton()) {
			GearReleaser.set(DoubleSolenoid.Value.kForward);
		}
		else if (OperateController.getAButton()) {
			GearReleaser.set(DoubleSolenoid.Value.kForward);
		}
		else {
			GearReleaser.set(DoubleSolenoid.Value.kReverse);
		}
		
		//Catch the gear (Press X to piston)
		if (DriveController.getBackButton()) {
			GearCatcher.set(DoubleSolenoid.Value.kForward);
		}
		else if (OperateController.getBButton()) {
			GearCatcher.set(DoubleSolenoid.Value.kForward);
		}
		else {
			GearCatcher.set(DoubleSolenoid.Value.kReverse);
		}
	}

	public void disabledInit() {
		DriveController.setRumble(GenericHID.RumbleType.kLeftRumble, -2930);
		DriveController.setRumble(GenericHID.RumbleType.kRightRumble, -2930);
	}

	public void disabledPeriodic() {
		if (DriveController.getAButton()) {
			VisionDataTable.putBoolean("RunVision", true);
		}
		else {
			VisionDataTable.putBoolean("RunVision", false);
		}
	}
	
	/**
	* This function is called periodically during test mode
	*/
	
	@Override
	public void testPeriodic() {
	}
}