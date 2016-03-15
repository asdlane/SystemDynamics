package de.uka.aifb.com.systemDynamics.model;

public class ChartLevelNode{
	private String levelNodeIdRef;
	private String label;
	public ChartLevelNode(String levelNodeIdRef, String label) {
		this.levelNodeIdRef = levelNodeIdRef;
		this.label = label;
	}
	 
	public String getLevelNodeIdRef(){
		return levelNodeIdRef;
	}
	public String getLabel(){
		return label;
	}

}
