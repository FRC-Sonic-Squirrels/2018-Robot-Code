package org.usfirst.frc.team2930.robot;

import edu.wpi.first.wpilibj.PIDOutput;

public class SimplePIDOutput implements PIDOutput {
	
	protected double[] output;
	private double initialOut;
	protected int count;
	
	public SimplePIDOutput(double initialOut, int bufferSize) {
		output = new double[bufferSize];
		for (int i = 0; i < bufferSize; i++) {
			output[i] = initialOut;
		}
		count = 0;
		this.initialOut = initialOut;
	}
	
	public void pidWrite(double value) {
		output[count] = value;
		count++;
		count %= output.length;
	}
	
	public double getOutput() {
		double rval = 0.0;
		for (int i = 0; i < output.length; i++) {
			rval += output[i];
		}
		rval /= output.length;
		return rval;
	}
	
	public void clearValues() {
		for (int i = 0; i < output.length; i++) {
			output[i] = initialOut;
		}
	}
}