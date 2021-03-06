
package de.uka.aifb.com.systemDynamics.model;

import java.util.*;

import de.uka.aifb.com.systemDynamics.model.AuxiliaryNode.AuxiliaryNodeIterator;

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
public class SharedNode extends AbstractNode implements ASTElement{  
	private double currentValue;
	private ASTElement formula;
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
   protected SharedNode(String sharedPointer, double nodeVal) {
	  
	  setsharedPointer(sharedPointer);
      incomingFlows = new HashSet<RateNode>();
      outgoingFlows = new HashSet<RateNode>();
      currentValue = nodeVal;
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
   private static SharedNode createSharedNode(String sharedPointer, int nodeVal) {
      return new SharedNode(sharedPointer, nodeVal);
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

   /**
    * Sets the auxiliary node's formula. A deep copy of the formula is stored. So the stored formula
    * cannot be changed from outside. The formula can also be <code>null</code> in order to
    * delete the formula.
    * 
    * @param formula formula
    */
   void setFormula(ASTElement formula) {
      if (formula == null) {
         this.formula = null;
      } else {
         this.formula = (ASTElement)formula.clone();
      }
   }
   
   /**
    * Checks whether the auxiliary node has a formula.
    * 
    * @return <code>true</code> iff the auxiliary node has a formula
    */
   public boolean hasFormula() {
      return (formula != null);
   }
   
   /**
    * Gets the auxiliary node's formula. A deep copy of the formula is returned. So the stored
    * formula cannot be changed from outside.
    * 
    * @return auxiliary node's formula or <code>null</code> iff there is no formula
    */
   public ASTElement getFormula() {
      if (formula == null) {
         return null;
      }
      
      return (ASTElement)formula.clone();
   }

@Override
public double getCurrentValue() {
	// TODO Auto-generated method stub
	return currentValue;
}
public void setCurrentValue(Double newVal){
	currentValue = newVal;
}


@Override
void computeNextValue() {
	currentValue = formula.evaluate();
	
}



/**
 * Evaluates the ASTElement.
 * 
 * @return ASTElement value
 */
public double evaluate() {
   return currentValue;
}

/**
 * Gets all nodes in this AST subtree (inclusive this ASTElement).
 * 
 * @return set of all nodes in AST subtree
 */
public HashSet<AbstractNode> getAllNodesInASTSubtree() {
   HashSet<AbstractNode> nodeSet = new HashSet<AbstractNode>();
   nodeSet.add(this);
   return nodeSet;
}

/**
 * Gets a <code>String</code> representation of the node's formula.
 * 
 * @return <code>String</code> representation of the node's formula
 */
public String getStringRepresentation() {
   return getNodeName() + "(AN)";
}

/**
 * Gets a short <code>String</code> representation of the node's formula.
 * 
 * @param auxiliaryNode2id auxiliary node to id mapping
 * @param constantNode2id constant node to id mapping
 * @param levelNode2id level node to id mapping
 * @return short <code>String</code> representation of the node's formula
 */
public String getShortStringRepresentation(HashMap<AuxiliaryNode, Integer> auxiliaryNode2id,
                                           HashMap<ConstantNode, Integer> constantNode2id,
                                           HashMap<LevelNode, Integer> levelNode2id) {
  return "";
}

/**
 * Creates and returns a <b>deep</b> copy of this object. Only the nodes in the leaves
 * are not cloned.
 * 
 * @return a deep clone of this instance
 */
@Override
public Object clone() {
   // return 'this' AuxiliaryNode, no clone!
   return this;
}

/**
 * Returns an iterator over the subtree of this node (here: only this node).
 * 
 * @return iterator over the subtree of this node (here: only this node)
 */
public Iterator<ASTElement> iterator() {
   return new SharedNodeIterator(this);
}

/**
 * Inner class implementing the {@link java.util.Iterator} interface. 
 */
private class SharedNodeIterator implements Iterator<ASTElement> {
   
   private AuxiliaryNode auxiliaryNode;
private SharedNode sharedNode;
   
   /**
    * Constructor.
    * 
    * @param auxiliaryNode {@link de.uka.aifb.com.systemDynamics.model.AuxiliaryNode} instance
    */
   private SharedNodeIterator(SharedNode sharedNode) {
      if (auxiliaryNode == null) {
         throw new IllegalArgumentException("'auxiliaryNode' must not be null.");
      }
      
      this.sharedNode = sharedNode;
   }
   
   /**
    * Checks if there is a next element in this iteration.
    * 
    * @return <code>true</code> iff there is a next element
    */
   public boolean hasNext() {
      return sharedNode != null;
   }
   
   /**
    * Gets this iteration's next element.
    * 
    * @return next element
    */
   public ASTElement next() {
      if (sharedNode!= null) {
         SharedNode temp = sharedNode;
         sharedNode = null;
         return temp;
      } else {
         throw new NoSuchElementException();
      }
   }
   
   /**
    * Removes the element last returned by this iterator.
    */
   public void remove() {
      throw new UnsupportedOperationException();
   }
}


   
}