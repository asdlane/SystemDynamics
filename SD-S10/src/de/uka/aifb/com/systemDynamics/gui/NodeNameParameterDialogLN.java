/* ======================================================================================================
 * SystemDynamics: Java application for modeling, visualization and execution of System Dynamics models
 * ======================================================================================================
 *
 * (C) Copyright 2007-2008, Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 *
 * Project Info:  http://sourceforge.net/projects/system-dynamics
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301, USA.
 */

package de.uka.aifb.com.systemDynamics.gui;

import de.uka.aifb.com.systemDynamics.SystemDynamics;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.ResourceBundle;
import javax.swing.*;

/**
 * This class implements a dialog for entering associated inputs for level node in particular.
 * 
 * @author Subbu Ramanathan, Tennenbaum Institute, Georgia Tech, USA
 * @author Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 * @version 1.0
 */
public class NodeNameParameterDialogLN extends JDialog implements ActionListener, FocusListener {
   
   private static final long serialVersionUID = 1L;
   
   private static Color COLOR_NEUTRAL_FIELDS = Color.WHITE;
   private static Color COLOR_WRONG_FIELDS = new Color(255, 160, 122);
   
   private ResourceBundle messages;
   
   private NumberFormat doubleNumberFormatter;
   
   private JTextField nodeNameField;
   private JTextField nodeParameterField;
   private JTextField minField;
   private JTextField maxField;
   private JRadioButton curve1;
   private JRadioButton curve2;
   private JRadioButton curve3;
   private ButtonGroup bgroup;
   
   private JButton okButton;
   private JButton cancelButton;
   
   private String nodeNameMessage;
   private String nodeParameterMessage;
   private String minMessage;		// Minimum value allowed across the 'entire simulation'
   private String maxMessage;		// Maximum value allowed across the 'entire simulation'
   private String curve1Message;	// Characteristic behavior of the level node (Non-Decreasing)
   private String curve2Message;	// Characteristic behavior of the level node (Non-Increasing)
   private String curve3Message;	// Characteristic behavior of the level node (NULL; no restrictions)
   private double minParameter;		// Minimum 'Start' value
   private double maxParameter;		// Maximum 'Start' value
   
   private NodeNameParameterLN verifiedNodeNameParameterLN;
   
   /**
    * Constructor.
    * 
    * @param start {@link de.uka.aifb.com.systemDynamics.SystemDynamics} instance
    * @param owner frame "owning" this dialog window
    * @param title dialog window title
    * @param nodeNameMessage message to explain node name field
    * @param nodeParameterMessage message to explain node parameter field
    * @param minParameter minimal allowed parameter value
    * @param maxParameter maximal allowed parameter value
    */
   private NodeNameParameterDialogLN(SystemDynamics start, JFrame owner, String title, 
		   								String nodeNameMessage, String nodeParameterMessage, 
		   								String minMessage, String maxMessage, 
                                                      				String curve1Message, String curve2Message, String curve3Message,
		   								double minParameter, double maxParameter) {
      // call constructor of super class
      super(owner, true);
      
      if (start == null) {
         throw new IllegalArgumentException("'start' must not be null.");
      }
      if (owner == null) {
         throw new IllegalArgumentException("'owner' must not be null.");
      }
      if (title == null) {
         throw new IllegalArgumentException("'title' must not be null.");
      }
      if (nodeNameMessage == null) {
         throw new IllegalArgumentException("'nodeNameMessage' must not be null.");
      }
      if (nodeParameterMessage == null) {
         throw new IllegalArgumentException("'nodeParameterMessage' must not be null.");
      }
      
      messages = start.getMessages();
      
      doubleNumberFormatter = NumberFormat.getInstance(start.getLocale());
      
      this.nodeNameMessage = nodeNameMessage;
      this.nodeParameterMessage = nodeParameterMessage;
      this.minMessage = minMessage;
      this.maxMessage = maxMessage;
      this.minParameter = minParameter;
      this.maxParameter = maxParameter;
      this.curve1Message = curve1Message;
      this.curve2Message = curve2Message;
      this.curve3Message = curve3Message;
      
      setTitle(title);
            
      getContentPane().add(createPanel());
      
      // set default button: OK button
      getRootPane().setDefaultButton(okButton);
      
      // set necessary size (not resizable)
      pack();
      setResizable(false);
      
      // set location
      Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
      int xStart = (int)((dimension.getWidth() - getWidth()) / 2);
      int yStart = (int)((dimension.getHeight() - getHeight()) / 2);
      setLocation(xStart, yStart);
            
      setVisible(true);
      setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
   }
   
   /**
    * Shows a dialog for changing a node's name.
    * 
    * @param start @link{de.uka.aifb.com.systemDynamics.SystemDynamics} instance
    * @param owner frame "owning" this dialog window
    * @param title dialog window title
    * @param nodeNameMessage message to explain node name field
    * @param nodeParameterMessage message to explain node parameter field
    * @param minParameter minimal allowed parameter value
    * @param maxParameter maximal allowed parameter value
    * @return node name and node parameter
    */
   public static NodeNameParameterLN showNodeNameDialog(SystemDynamics start, JFrame owner, String title,
                                                      String nodeNameMessage, String nodeParameterMessage,
                                                      String minMessage, String maxMessage,
                                                      String curve1Message, String curve2Message, String curve3Message,
                                                      double minParameter, double maxParameter) {
      if (start == null) {
         throw new IllegalArgumentException("'start' must not be null.");
      }
      if (owner == null) {
         throw new IllegalArgumentException("'owner' must not be null.");
      }
      if (title == null) {
         throw new IllegalArgumentException("'title' must not be null.");
      }
      if (nodeNameMessage == null) {
         throw new IllegalArgumentException("'nodeNameMessage' must not be null.");
      }
      if (nodeParameterMessage == null) {
         throw new IllegalArgumentException("'nodeParameterMessage' must not be null.");
      }
      
      NodeNameParameterDialogLN nodeNameParameterDialogLN =
         new NodeNameParameterDialogLN(start, owner, title, nodeNameMessage, nodeParameterMessage, minMessage, maxMessage, curve1Message, curve2Message,
        		 						curve3Message, minParameter, maxParameter);
      
      return nodeNameParameterDialogLN.verifiedNodeNameParameterLN;
   }
   
   /**
    * Creates the panel with the node name field.
    * 
    * @return panel with node name field
    */
   private JPanel createPanel() {
      JPanel panel = new JPanel();
            
      /*
       * input panel
       */
      JPanel inputPanel = new JPanel(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      JLabel nodeNameMessageLabel = new JLabel(nodeNameMessage + ":");
      c.anchor = GridBagConstraints.EAST;
      c.gridx = 0;
      c.gridy = 0;
      c.insets = new Insets(0, 0, 0, 5);
      inputPanel.add(nodeNameMessageLabel, c);
      nodeNameField = new JTextField("", 20);
      nodeNameField.addFocusListener(this);
      c.anchor = GridBagConstraints.CENTER; // default!
      c.gridx = 1;
      c.gridy = 0;
      c.insets = new Insets(0, 0, 0, 0);
      inputPanel.add(nodeNameField, c);
      JLabel nodeParameterMessageLabel = new JLabel(nodeParameterMessage + ":");
      c.anchor = GridBagConstraints.EAST;
      c.gridx = 0;
      c.gridy = 1;
      c.insets = new Insets(0, 0, 0, 5);
      inputPanel.add(nodeParameterMessageLabel, c);
      nodeParameterField = new JTextField("", 20);
      nodeParameterField.addFocusListener(this);
      c.gridx = 1;
      c.gridy = 1;
      c.insets = new Insets(0, 0, 0, 0);
      inputPanel.add(nodeParameterField, c);

      JLabel minMessageLabel = new JLabel(minMessage + ":");
      c.anchor = GridBagConstraints.EAST;
      c.gridx = 0;
      c.gridy = 2;
      c.insets = new Insets(0, 0, 0, 5);
      inputPanel.add(minMessageLabel, c);
      minField = new JTextField("", 20);
      minField.addFocusListener(this);
      c.gridx = 1;
      c.gridy = 2;
      c.insets = new Insets(0, 0, 0, 0);
      inputPanel.add(minField, c);
      
      JLabel maxMessageLabel = new JLabel(maxMessage + ":");
      c.anchor = GridBagConstraints.EAST;
      c.gridx = 0;
      c.gridy = 3;
      c.insets = new Insets(0, 0, 0, 5);
      inputPanel.add(maxMessageLabel, c);
      maxField = new JTextField("", 20);
      maxField.addFocusListener(this);
      c.gridx = 1;
      c.gridy = 3;
      c.insets = new Insets(0, 0, 0, 0);
      inputPanel.add(maxField, c);

      /*
       * radio button panel
       */
      curve1 = new JRadioButton(curve1Message, false);
      curve2 = new JRadioButton(curve2Message, false);
      curve3 = new JRadioButton(curve3Message, true);
      bgroup = new ButtonGroup();
      bgroup.add(curve1);
      bgroup.add(curve2);
      bgroup.add(curve3);
      JPanel radiopanel = new JPanel();
      radiopanel.setLayout(new GridBagLayout());
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.gridy = GridBagConstraints.RELATIVE;
      gbc.anchor = GridBagConstraints.WEST;
      radiopanel.add(curve1,gbc);
      radiopanel.add(curve2,gbc);
      radiopanel.add(curve3,gbc);
            
      /*
       * button panel
       */
      JPanel buttonPanel = new JPanel();
      okButton = new JButton(messages.getString("NodeNameParameterDialog.OKButton.Text"));
      okButton.addActionListener(this);
      buttonPanel.add(okButton);
      cancelButton = new JButton(messages.getString("NodeNameParameterDialog.CancelButton.Text"));
      cancelButton.addActionListener(this);
      buttonPanel.add(cancelButton);
      
      // set together
      panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
      panel.add(inputPanel);
      panel.add(radiopanel);
      panel.add(buttonPanel);
      
      return panel;
   }
   
   /**
    * Verifies the entered node name and node parameter.
    * 
    * @return <code>true</code> iff the entered node name and node parameter could be verified
    */
   private boolean verifyNewNodeNameParameter() {
      // (1) set neutral background color
      nodeNameField.setBackground(COLOR_NEUTRAL_FIELDS);
      nodeParameterField.setBackground(COLOR_NEUTRAL_FIELDS);
      
      // (2) check node name in field
      String nodeName = nodeNameField.getText();
      if (nodeName == null || nodeName.length() == 0) {
         nodeNameField.setBackground(COLOR_WRONG_FIELDS);
         JOptionPane.showMessageDialog(null,
                                       messages.getString("NodeNameParameterDialog.NoNodeNameErrorMessage"),
                                       messages.getString("NodeNameParameterDialog.NoNodeNameErrorTitle"),
                                       JOptionPane.ERROR_MESSAGE);
         
         // exit method
         return false;
      }
      
      // (3) check node parameter in field
      double nodeParameter;
      try {
         nodeParameter = doubleNumberFormatter.parse(nodeParameterField.getText()).doubleValue();
         if (nodeParameter < minParameter || nodeParameter > maxParameter) {
            // not in allowed parameter range
            nodeParameterField.setBackground(COLOR_WRONG_FIELDS);
            JOptionPane.showMessageDialog(null,
                                          messages.getString("NodeNameParameterDialog.NotInParameterRangeErrorMessage"),
                                          messages.getString("NodeNameParameterDialog.NotInParameterRangeErrorTitle"),
                                          JOptionPane.ERROR_MESSAGE);
            
            // exit method
            return false;
         }
      } catch (ParseException e) {
         // not a number
         nodeParameterField.setBackground(COLOR_WRONG_FIELDS);
         JOptionPane.showMessageDialog(null,
                                       messages.getString("NodeNameParameterDialog.NotNumberErrorMessage"),
                                       messages.getString("NodeNameParameterDialog.NotNumberErrorTitle"),
                                       JOptionPane.ERROR_MESSAGE);
         
         // exit method
         return false;
      }

	double minParameter, maxParameter;
      	String minName = minField.getText();
      	String maxName = maxField.getText();
	if (minName == null || minName.length() == 0)
		minParameter = 0;			// if 0; no restrictions on the min value allowed across the entire simulation
	else
	{
		try {
         	minParameter = doubleNumberFormatter.parse(minField.getText()).doubleValue();
	    } catch (ParseException e) {
	          // not a number
	          minField.setBackground(COLOR_WRONG_FIELDS);
	          JOptionPane.showMessageDialog(null,
	                                        messages.getString("NodeNameParameterDialog.NotNumberErrorMessage"),
	                                        messages.getString("NodeNameParameterDialog.NotNumberErrorTitle"),
	                                        JOptionPane.ERROR_MESSAGE);
	          
	          // exit method
	          return false;
	    }
	}
	if (maxName == null || maxName.length() == 0)
		maxParameter = 0;			// if 0; no restrictions on the max value allowed across the entire simulation
	else
	{
		try {
         	maxParameter = doubleNumberFormatter.parse(maxField.getText()).doubleValue();
	    } catch (ParseException e) {
	          // not a number
	          maxField.setBackground(COLOR_WRONG_FIELDS);
	          JOptionPane.showMessageDialog(null,
	                                        messages.getString("NodeNameParameterDialog.NotNumberErrorMessage"),
	                                        messages.getString("NodeNameParameterDialog.NotNumberErrorTitle"),
	                                        JOptionPane.ERROR_MESSAGE);
	          
	          // exit method
	          return false;
	    }
	}
	
	double curve = 3;
	if(curve1.isSelected())
		curve = 1;
	else if(curve2.isSelected())
		curve = 2;
	else if(curve3.isSelected())
		curve = 3;

	// node name and node parameter correct  
	verifiedNodeNameParameterLN = new NodeNameParameterLN(nodeName, nodeParameter, minParameter, maxParameter, curve);
      
	return true;
   }
   
////////////////////////////////////////////////////////////////////////////////////////////////////
//                              methods of interface ActionListener
////////////////////////////////////////////////////////////////////////////////////////////////////

   /**
    * Performs the specified action event that is caused by the OK button.
    * 
    * @param e event
    */
   public void actionPerformed(ActionEvent e) {
      if (e.getSource() == okButton) {
         boolean nodeNameParameterVerified = verifyNewNodeNameParameter();
         
         // close this dialog window if node name and node parameter were verified successfully
         if (nodeNameParameterVerified) {
            dispose();
         }
      }
      
      if (e.getSource() == cancelButton) {
         dispose();
      }
   }
   
////////////////////////////////////////////////////////////////////////////////////////////////////
//                               methods of interface FocusListener
////////////////////////////////////////////////////////////////////////////////////////////////////
   
   /**
    * Performs a gained focus event.
    * 
    * @param e event
    */
   public void focusGained(FocusEvent e) {
      Component c = e.getComponent();
      if (c instanceof JTextField) {
          ((JTextField)c).selectAll();
      }
   }
   
   /**
    * Performs a lost focus event.
    * 
    * @param e event
    */
   public void focusLost(FocusEvent e) {
      // do nothing
   }

////////////////////////////////////////////////////////////////////////////////////////////////////
//                                         inner class
////////////////////////////////////////////////////////////////////////////////////////////////////
   
   /**
    * Inner class for storing entered node name and node parameter.
    */
   public static class NodeNameParameterLN {
      
      private String nodeName;
      private double nodeParameter;
      private double minParameter;
      private double maxParameter;
      private double curve;
      
      /**
       * Constructor.
       * 
       * @param nodeName node name
       * @param nodeParameter node parameter
       */
      public NodeNameParameterLN(String nodeName, double nodeParameter, double minParameter, double maxParameter, double curve) {
         if (nodeName == null) {
            throw new IllegalArgumentException("'nodeName' must not be null.");
         }
         
         this.nodeName = nodeName;
         this.nodeParameter = nodeParameter;
         this.minParameter = minParameter;
         this.maxParameter = maxParameter;
         this.curve = curve;
      }
      
      /**
       * Gets the entered node name.
       * 
       * @return node name
       */
      public String getNodeName() {
         return nodeName;
      }
      
      /**
       * Gets the entered node parameter.
       * 
       * @return node parameter
       */
      public double getNodeParameter() {
         return nodeParameter;
      }
      
      /**
       * Gets the entered min parameter.
       * 
       * @return min parameter
       */
      public double getMinParameter() {
         return minParameter;
      }
      
      /**
       * Gets the entered max parameter.
       * 
       * @return max parameter
       */
      public double getMaxParameter() {
         return maxParameter;
      }

      /**
       * Gets the characteristic behaviour of this LevelNode.
       * 
       * @return curve
       */
      public double getCurveParameter() {
          return curve;
       }
   }
}
