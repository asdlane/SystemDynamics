
package de.uka.aifb.com.systemDynamics.gui.systemDynamicsGraph;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;

import javax.swing.border.Border;

import org.jgraph.graph.*;

/**
 * This class implements a specialized graph cell for a System Dynamics level node.
 * 
 * @author Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 * @version 1.0
 */
public class SharedNodeGraphCell extends DefaultGraphCell {
   
   private static final long serialVersionUID = 1L;
   
   private static final double WIDTH = 80;
   private static final double HEIGHT = 40;
   private static final Color COLOR = Color.ORANGE;
   private static Color BORDER_COLOR = Color.BLACK;
   private Color purple = new Color(171,0,255); 
   /**
    * Constructor.
    * 
    * @param name node's name
    * @param x x coordinate of node's origin
    * @param y y coordinate of node's origin
 * @param learnerChangeable 
    */
   public SharedNodeGraphCell(double x, double y, AttributeMap vals, String sharedNodeName) {
      super("sharedNode", vals);
      addPort();
      
      // layout
      GraphConstants.setBounds(getAttributes(), new  Rectangle2D.Double(x, y, WIDTH, HEIGHT));
      GraphConstants.setSizeable(getAttributes(), false);
      GraphConstants.setBorderColor(getAttributes(), BORDER_COLOR);
      GraphConstants.setBackground(getAttributes(), Color.green);
      
      GraphConstants.setOpaque(getAttributes(), true);
   }
}