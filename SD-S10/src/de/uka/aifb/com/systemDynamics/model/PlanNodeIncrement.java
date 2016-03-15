package de.uka.aifb.com.systemDynamics.model;

public class PlanNodeIncrement {
	private String id;
	private double length, slope;
	public PlanNodeIncrement(String id, double length, double slope){
		this.id = id;
		this.length = length;
		this.slope = slope;
	}
	public String getId(){
		return id;
	}
	public double getLength(){
		return length;
	}
	public double getSlope(){
		return slope;
	}
}
