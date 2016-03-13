package de.uka.aifb.com.systemDynamics.model;

public class ChartLevelNode{
	private String levelNodeIdRef;
	private String label;
	public ChartLevelNode(String levelNodeIdRef, String label) {
		this.levelNodeIdRef = levelNodeIdRef;
		this.label = label;
	}
	 
	private static ChartLevelNode createChartLevelNode(String levelNodeIdRef, String label){
		return new ChartLevelNode(levelNodeIdRef, label);
	}
	

}
