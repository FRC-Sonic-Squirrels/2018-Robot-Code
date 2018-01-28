package org.usfirst.frc.team2930.robot;

import edu.wpi.first.wpilibj.PIDController.Tolerance;
import edu.wpi.first.wpilibj.filters.LinearDigitalFilter;

public class LinearDigitalFilterTolerance implements Tolerance {
	
	private LinearDigitalFilter filter;
	
	public LinearDigitalFilterTolerance(LinearDigitalFilter filter) {
		this.filter = filter;
	}
	
	@Override
	public boolean onTarget() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
