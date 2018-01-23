package org.usfirst.frc.team2930.robot;

import edu.wpi.first.wpilibj.PIDOutput;

public class SimplePIDOutput implements PIDOutput {
	
	private double output;
	
	public SimplePIDOutput (double initialout) {
		output = initialout;
	}
	
	public void pidWrite(double value) {
		output = value;
	}
	
	public double getOutput() {
		return output;
	}
}