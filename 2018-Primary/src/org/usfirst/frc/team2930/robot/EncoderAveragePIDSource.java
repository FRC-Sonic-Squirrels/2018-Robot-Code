package org.usfirst.frc.team2930.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

public class EncoderAveragePIDSource implements PIDSource {
	
	Encoder leftEncoder, rightEncoder;
	PIDSourceType SourceType;
	private double average(double x, double y) {
		return (x + y) / 2;
	}
	
	public EncoderAveragePIDSource(Encoder leftEncoder, Encoder rightEncoder) {
		this.rightEncoder = rightEncoder;
		this.leftEncoder = leftEncoder;
		SourceType = PIDSourceType.kDisplacement;
	}
	
	public void setPIDSourceType(PIDSourceType pidSource) {
		SourceType = pidSource;
	}
	
	public PIDSourceType getPIDSourceType() {
		return SourceType;
	}
	
	public double pidGet() {
		if (SourceType == PIDSourceType.kDisplacement) {
			return average(leftEncoder.getDistance(), rightEncoder.getDistance());
		}
		else {
			return average(leftEncoder.getRate(), rightEncoder.getRate());
		}
	}
}