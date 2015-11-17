
package de.uka.aifb.com.systemDynamics.model;

import java.util.*;

/**
 * This class implements a System Dynamics model node representing a level.
 * 
 * All methods of AbstractNode and its subclasses are only visible within
 * this package. Only getter methods whose return values are not changeable
 * from outside can be public. All setter methods must be invoked using
 * an adequate method from class
 * {@link de.uka.aifb.com.systemDynamics.model.Model}.
 *
 * @author Joachim Melcher, University of Karlsruhe, AIFB
 * @version 1.0
 */
public class SharedNode extends AbstractNode{  
   
   private HashSet<RateNode> incomingFlows;
   private HashSet<RateNode> outgoingFlows;
   private String LocalSharedPointer;
   /**
    * Constructor.
    * 
    * @param nodeName node name
    * @param startValue start value (only from MIN_START_VALUE to MAX_START_VALUE!)
    * @param minValue min value allowed across the entire simulation
    * @param maxValue max value allowed across the entire simulation
    * @param curve characteristic behaviour of this LevelNode
    */
   protected SharedNode(String sharedPointer) {
	  setsharedPointer(sharedPointer);
      incomingFlows = new HashSet<RateNode>();
      outgoingFlows = new HashSet<RateNode>();
   }
   void setsharedPointer(String sharedPointer){
	   LocalSharedPointer = sharedPointer;
   }
   public String getSharedPointer(){
	   return LocalSharedPointer;
   }

/**
    * Helper method for creating new instances of this class. Called by JUnit test cases.
    * 
    * @param nodeName node name
    * @param startValue start value
    * @return created new instance
    */
   private static SharedNode createSharedNode(String sharedPointer) {
      return new SharedNode(sharedPointer);
   }
   
   
      /**
    * Adds the specified incoming flow.
    * 
    * @param incomingFlow incoming flow
    * @return <code>true</code> iff this incoming flow was not already element of the set of
    *         incoming flows
    */
   boolean addIncomingFlow(RateNode incomingFlow) {
      if (incomingFlow == null) {
         throw new IllegalArgumentException("'incomingFlow' must not be null.");
      }
      
      return incomingFlows.add(incomingFlow);
   }
   
   /**
    * Removes the specified incoming flow.
    * 
    * @param incomingFlow incoming flow
    * @return <code>true</code> iff this incoming flow was element of the set of incoming flows
    */
   boolean removeIncomingFlow(RateNode incomingFlow) {
      if (incomingFlow == null) {
         throw new IllegalArgumentException("'incomingFlow' must not be null.");
      }
      
      return incomingFlows.remove(incomingFlow);
   }
   
   /**
    * Gets the set of all incoming flows. A shallow clone of the set of incoming flows is returned.
    * 
    * @return incoming flows
    */
   public HashSet<RateNode> getIncomingFlows() {
      return (HashSet<RateNode>)incomingFlows.clone();
   }
   
   /**
    * Adds the specified outgoing flow.
    * 
    * @param outgoingFlow outgoing flow
    * @return <code>true</code> iff this outgoing flow was not already element of the set of
    *         outgoing flows
    */
   boolean addOutgoingFlow(RateNode outgoingFlow) {
      if (outgoingFlow == null) {
         throw new IllegalArgumentException("'outgoingFlow' must not be null.");
      }
      
      return outgoingFlows.add(outgoingFlow);
   }
   
   /**
    * Removes the specified outgoing flow.
    * 
    * @param outgoingFlow outgoing flow
    * @return <code>true</code> iff this outgoing flow was element of the set of outgoing flows
    */
   boolean removeOutgoingFlow(RateNode outgoingFlow) {
      if (outgoingFlow == null) {
         throw new IllegalArgumentException("'outgoingFlow' must not be null.");
      }
      
      return outgoingFlows.remove(outgoingFlow);
   }

   /**
    * Gets the set of all outgoing flows. A shallow clone of the set of outgoing flows is returned.
    * 
    * @return outgoing flows
    */
   public HashSet<RateNode> getOutgoingFlows() {
      return (HashSet<RateNode>)outgoingFlows.clone();
   }



@Override
public double getCurrentValue() {
	// TODO Auto-generated method stub
	return 0;
}



@Override
void computeNextValue() {
	// TODO Auto-generated method stub
	
}



   
}