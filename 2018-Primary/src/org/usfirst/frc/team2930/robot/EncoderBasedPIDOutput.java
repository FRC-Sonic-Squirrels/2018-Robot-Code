package org.usfirst.frc.team2930.robot;

import edu.wpi.first.wpilibj.Counter;

public class EncoderBasedPIDOutput extends SimplePIDOutput {
	
	private Counter[] counters;
	private double threshold;
	
	public EncoderBasedPIDOutput(double initialOut, int bufferSize, double threshold, Counter... counters) {
		super(initialOut, bufferSize);
		this.counters = counters;
		this.threshold = threshold;
	}
	
	public double getOutput() {
		int oldestIndex = count;
		int newestIndex = (count + output.length - 1) % output.length;
		
		for (Counter element : counters) {
			if (element.getStopped() && (Math.abs(output[oldestIndex]) > threshold)) {
				return 0;
			}
		}
		return output[newestIndex];
	}
}
