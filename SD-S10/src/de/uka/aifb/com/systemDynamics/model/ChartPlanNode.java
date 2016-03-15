package de.uka.aifb.com.systemDynamics.model;

public class ChartPlanNode {
	private String chartPlanNodeIdRef, label;
	public ChartPlanNode(String chartPlanNodeIdRef, String label) {
		this.chartPlanNodeIdRef = chartPlanNodeIdRef;
		this.label = label;
	}
	public String getchartPlanNodeIdRef(){
		return chartPlanNodeIdRef;
	}
	public String getLabel(){
		return label;
	}

}
