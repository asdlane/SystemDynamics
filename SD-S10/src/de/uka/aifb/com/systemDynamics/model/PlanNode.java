package de.uka.aifb.com.systemDynamics.model;

import java.util.HashSet;



public class PlanNode {
	private String id, name;
	private double startValue;
	private HashSet<PlanNodeIncrement> planNodeIncrements = new HashSet<PlanNodeIncrement>();
	public PlanNode(String id, String name, double startValue) {
		this.id = id;
		this.name = name;
		this.startValue = startValue;
	}
	public void createPlanNodeIncrement(String id, double length, double slope){
		planNodeIncrements.add(new PlanNodeIncrement(id, length, slope));
	}
	public void removePlanNodeIncrement(String id){

		PlanNodeIncrement remove = null;
		   
		   for(PlanNodeIncrement planNodeIncrement : planNodeIncrements){
			   if(planNodeIncrement.getId().equals(id)){
				   remove = planNodeIncrement;
			   }
		   }
			   
		   planNodeIncrements.remove(remove);
	}
	
	public HashSet<PlanNodeIncrement> getPlanNodeIncrements(){
		return planNodeIncrements;
	}
	public String getId(){
		return id;
	}
	public String getName(){
		return name;
	}
	public Double getStartValue(){
		return startValue;
	}
	public void setId(String id){
		this.id=id;
	}
	public void setName(String name){
		this.name=name;
	}
	public void setStartValue(double startValue){
		this.startValue = startValue;
	}
}
