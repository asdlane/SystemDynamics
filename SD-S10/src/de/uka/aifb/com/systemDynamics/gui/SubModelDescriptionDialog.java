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
import java.util.ResourceBundle;
import javax.swing.*;

/**
 * This class implements a dialog for adding description for a submodel
 * 
 * @author Jing Liu, Georgia Tech, US
 * @version 1.0
 */
public class SubModelDescriptionDialog extends JDialog implements ActionListener, FocusListener {
   
   private static final long serialVersionUID = 1L;
   
   private static Color COLOR_NEUTRAL_FIELDS = Color.WHITE;
   private static Color COLOR_WRONG_FIELDS = new Color(255, 160, 122);
   
   private ResourceBundle messages;
   
   private JTextArea descriptionArea;
   
   private JButton okButton;
   private JButton cancelButton;
   
   private String description;
   private String descriptionLabelName = "Desscription";
   private String verifiedDescription;
   
   /**
    * Constructor.
    * 
    * @param start @link{de.uka.aifb.com.systemDynamics.SystemDynamics} instance
    * @param owner frame "owning" this dialog window
    * @param title dialog window title
    * @param description description to a submodel
    */
   private SubModelDescriptionDialog(SystemDynamics start, JFrame owner, String title, String description) {
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
//      if (description == null) {
//          throw new IllegalArgumentException("'description' must not be null.");
//       }
      
      
      messages = start.getMessages();
      this.description = description;
      
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
    * @param description description to a submodel
    * @return node name - or <code>null</code> if dialog was closed without entering a node name
    */
   public static String showNodeNameDialog(SystemDynamics start, JFrame owner, String title,
                                           String description) {
      if (start == null) {
         throw new IllegalArgumentException("'start' must not be null.");
      }
      if (owner == null) {
         throw new IllegalArgumentException("'owner' must not be null.");
      }
      if (title == null) {
         throw new IllegalArgumentException("'title' must not be null.");
      }
//      if (description == null) {
//         throw new IllegalArgumentException("'description' must not be null.");
//      }
      
      SubModelDescriptionDialog subModelDescriptionDialog =
         new SubModelDescriptionDialog(start, owner, title, description);
      
      return subModelDescriptionDialog.verifiedDescription;
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
      JPanel inputPanel = new JPanel();
      inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      inputPanel.add(new JLabel(descriptionLabelName + ":"));
      descriptionArea = new JTextArea(description, 10 , 20);
      descriptionArea.addFocusListener(this);
      descriptionArea.setLineWrap(true);
      descriptionArea.setWrapStyleWord(true);
      inputPanel.add(new JScrollPane(descriptionArea));
      
      /*
       * button panel
       */
      JPanel buttonPanel = new JPanel();
      okButton = new JButton(messages.getString("SubModelDescriptionDialog.OKButton.Text"));
      okButton.addActionListener(this);
      buttonPanel.add(okButton);
      cancelButton = new JButton(messages.getString("SubModelDescriptionDialog.CancelButton.Text"));
      cancelButton.addActionListener(this);
      buttonPanel.add(cancelButton);
      
      // set together
      panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
      panel.add(inputPanel);
      panel.add(buttonPanel);
      
      return panel;
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
    	  
    	  verifiedDescription = descriptionArea.getText();
    	  
         /////////////       validate the description here    //////////////////////
    	  dispose();
    	  
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
          ((JTextArea)c).selectAll();
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