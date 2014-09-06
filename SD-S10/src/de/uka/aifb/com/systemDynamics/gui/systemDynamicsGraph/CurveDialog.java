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

package de.uka.aifb.com.systemDynamics.gui.systemDynamicsGraph;

import de.uka.aifb.com.systemDynamics.SystemDynamics;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.ResourceBundle;
import javax.swing.*;

/**
 * This class implements a dialog for entering Min and Max values for a Level Node.
 * 
 * @author Subbu Ramanathan, Tennenbaum Institute, Georgia Tech, USA
 * @author Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 * @version 1.0
 */
public class CurveDialog extends JDialog implements ActionListener, FocusListener {
   
   private static final long serialVersionUID = 1L;
   
   private static Color COLOR_NEUTRAL_FIELDS = Color.WHITE;
   private static Color COLOR_WRONG_FIELDS = new Color(255, 160, 122);
   
   private ResourceBundle messages;
   
   private NumberFormat doubleNumberFormatter;
   
   private JRadioButton curve1;
   private JRadioButton curve2;
   private JRadioButton curve3;
   private ButtonGroup bgroup;
   
   private JButton okButton;
   private JButton cancelButton;
   
   private String curve1Message;
   private String curve2Message;
   private String curve3Message;
   private double initialCurve;
   
   private Double verifiedCurve;
   
   /**
    * Constructor.
    * 
    * @param start @link{de.uka.aifb.com.systemDynamics.SystemDynamics} instance
    * @param owner frame "owning" this dialog window
    * @param title dialog window title
    * @param MinMaxMessage message to explain min/max field
    * @param initialMinMax initial min/max
    */
   private CurveDialog(SystemDynamics start, JFrame owner, String title,
                               String curve1Message, String curve2Message, String curve3Message, double initialCurve) {
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
      
      messages = start.getMessages();
      
      doubleNumberFormatter = NumberFormat.getInstance(start.getLocale());
      
      this.curve1Message = curve1Message;
      this.curve2Message = curve2Message;
      this.curve3Message = curve3Message;
      this.initialCurve = initialCurve;
      
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
    * Shows a dialog for changing a node's parameter.
    * 
    * @param start @link{de.uka.aifb.com.systemDynamics.SystemDynamics} instance
    * @param owner frame "owning" this dialog window
    * @param title dialog window title
    * @param MinMaxMessage message to explain min/max field
    * @param initialMinMax initial min/max
    * @return new node parameter or <code>null</code> if no (valid) new node name was entered
    */
   public static Double showCurveDialog(SystemDynamics start, JFrame owner, String title,
                                           String curve1Message, String curve2Message, String curve3Message, double initialCurve) {
      if (start == null) {
         throw new IllegalArgumentException("'start' must not be null.");
      }
      if (owner == null) {
         throw new IllegalArgumentException("'owner' must not be null.");
      }
      if (title == null) {
         throw new IllegalArgumentException("'title' must not be null.");
      }
      
      CurveDialog curveDialog =
         new CurveDialog(start, owner, title, curve1Message, curve2Message, curve3Message, initialCurve);
      
      // null if new node parameter was not successfully verified
      return curveDialog.verifiedCurve;
   }
   
   /**
    * Creates the panel with the node name field.
    * 
    * @return panel with node name field
    */
   private JPanel createPanel() {
      JPanel panel = new JPanel();
            
      /*
       * radio button panel
       */
      curve1 = new JRadioButton(curve1Message, false);
      curve2 = new JRadioButton(curve2Message, false);
      curve3 = new JRadioButton(curve3Message, false);
      bgroup = new ButtonGroup();
      bgroup.add(curve1);
      bgroup.add(curve2);
      bgroup.add(curve3);
      if(initialCurve == 1)
    	  curve1.setSelected(true);
      else if(initialCurve == 2)
    	  curve2.setSelected(true);
      else
    	  curve3.setSelected(true);
      JPanel radiopanel = new JPanel();
      radiopanel.setLayout(new GridLayout(3,1));
      radiopanel.add(curve1);
      radiopanel.add(curve2);
      radiopanel.add(curve3);
      
      /*
       * button panel
       */
      JPanel buttonPanel = new JPanel();
      okButton = new JButton(messages.getString("NodeParameterDialog.OKButton.Text"));
      okButton.addActionListener(this);
      buttonPanel.add(okButton);
      cancelButton = new JButton(messages.getString("NodeParameterDialog.CancelButton.Text"));
      cancelButton.addActionListener(this);
      buttonPanel.add(cancelButton);
      
      // set together
      panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
      panel.add(radiopanel);
      panel.add(buttonPanel);
      
      return panel;
   }
   
   /**
    * Verifies the new curve.
    * 
    * @return <code>true</code> iff the new curve could be verified
    */
   private boolean verifyNewCurve() {
      // (1) set neutral background color
	   // avoided
      
      // (2) set the curve based on the radio button that is selected
		double curve = 3;
		if(curve1.isSelected())
			curve = 1;
		else if(curve2.isSelected())
			curve = 2;
		else if(curve3.isSelected())
			curve = 3;         
         
         // node parameter correct
         verifiedCurve = curve;
         return true;
   }
   
////////////////////////////////////////////////////////////////////////////////////////////////////
//                              methods of interface ActionListener
////////////////////////////////////////////////////////////////////////////////////////////////////

   /**
    * Performs the specified action event that is caused by the OK or cancel button.
    * 
    * @param e event
    */
   public void actionPerformed(ActionEvent e) {
      if (e.getSource() == okButton) {
         boolean curveVerified = verifyNewCurve();
         
         // close this dialog window if node parameter was verified successfully
         if (curveVerified) {
            dispose();
         }
      }
      
      if (e.getSource() == cancelButton) {
         // close this dialog windows
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
}
