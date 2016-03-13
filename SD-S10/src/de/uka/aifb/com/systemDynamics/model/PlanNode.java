package de.uka.aifb.com.systemDynamics.model;

public class PlanNode {
	private String id, name;
	private double startValue;
	public PlanNode(String id, String name, double startValue) {
		this.id = id;
		this.name = name;
		this.startValue = startValue;
	}

}
