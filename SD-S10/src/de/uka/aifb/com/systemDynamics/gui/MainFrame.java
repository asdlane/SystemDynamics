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
import de.uka.aifb.com.systemDynamics.event.SystemDynamicsGraphModifiedEventListener;
import de.uka.aifb.com.systemDynamics.gui.systemDynamicsGraph.AuxiliaryNodeGraphCell;
import de.uka.aifb.com.systemDynamics.gui.systemDynamicsGraph.ColoredSourceSinkNodeGraphCell;
import de.uka.aifb.com.systemDynamics.gui.systemDynamicsGraph.ConstantNodeGraphCell;
import de.uka.aifb.com.systemDynamics.gui.systemDynamicsGraph.FlowEdge;
import de.uka.aifb.com.systemDynamics.gui.systemDynamicsGraph.LevelNodeGraphCell;
import de.uka.aifb.com.systemDynamics.gui.systemDynamicsGraph.RateNodeGraphCell;
import de.uka.aifb.com.systemDynamics.gui.systemDynamicsGraph.SharedNodeGraphCell;
import de.uka.aifb.com.systemDynamics.gui.systemDynamicsGraph.SourceSinkNodeGraphCell;
import de.uka.aifb.com.systemDynamics.gui.systemDynamicsGraph.SystemDynamicsGraph;
import de.uka.aifb.com.systemDynamics.model.*;
import de.uka.aifb.com.systemDynamics.xml.*;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.*;

import org.jgraph.*;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.GraphTransferable;

/*
 * Changes:
 * ========
 *
 * 2012-09-27: Changes in Model Name (adding today s date); by Subbu
 * 2012-09-20: Changes in NewLevelNode Action Performed; by Subbu
 * 2007-08-30: ExecuteModelAction.actionPerformed was rewritten: other text (without node name)
 *             for useless source/sink node
 * 2008-01-24: createMenuBar was rewritten: additional language 'Spanish'
 * 2008-01-24: actionPerformed was rewritten: additional code for Spanish GUI
 * 2014-09-15: changed zoom level to zoom in by a factor of 1.3 instead of 2.  replaced question 
 * 
 */

/**
 * This class implements the main frame of this application.
 * 
 * @author Subbu Ramanathan, Tennenbaum Institute, Georgia Tech, USA
 * @author Joachim Melcher, Institut AIFB, Universitaet Karlsruhe (TH), Germany
 * @version 1.2
 */
public class MainFrame extends JFrame implements ActionListener, ChangeListener,
SystemDynamicsGraphModifiedEventListener,
WindowListener {

	private static final long serialVersionUID = 1L;

	private static final double DEFAULT_COORDINATE = 20.0;

	private static final String FILE_ICON = "resources/icon.png";
	private static final String FILE_GPL_TEXT = "resources/license-GPL.txt";
	private static final String FILE_LGPL_TEXT = "resources/license-LGPL.txt";
	private static final String FILE_CC_BY_TEXT = "resources/license-CC-BY-2.5.txt";

	private static final String FILE_NEW_ICON = "resources/page_white.png";
	private static final String FILE_OPEN_ICON = "resources/folder_page_white.png";
	private static final String CUT_ICON = "resources/scissors.png";
	private static final String COPY_ICON = "resources/copy.png";
	private static final String PASTE_ICON = "resources/paste.png";
	private static final String FILE_SAVE_ICON = "resources/disk.png";
	private static final String	CHART_ICON = "resources/chart.png";
	
	private static final String FILE_NEW_AN_ICON = "resources/new_auxiliary_node_en_US.png";
	private static final String FILE_NEW_AN_de_DE_ICON = "resources/new_auxiliary_node_de_DE.png";
	private static final String FILE_NEW_CN_ICON = "resources/new_constant_node_en_US.png";
	private static final String FILE_NEW_CN_de_DE_ICON = "resources/new_constant_node_de_DE.png";
	private static final String FILE_NEW_LN_ICON = "resources/new_level_node_en_US.png";
	private static final String FILE_NEW_LN_de_DE_ICON = "resources/new_level_node_de_DE.png";
	private static final String FILE_NEW_RN_ICON = "resources/new_rate_node_en_US.png";
	private static final String FILE_NEW_RN_de_DE_ICON = "resources/new_rate_node_de_DE.png";
	private static final String FILE_NEW_SSN_ICON = "resources/new_source_sink_node_en_US.png";
	private static final String FILE_NEW_SSN_de_DE_ICON = "resources/new_source_sink_node_de_DE.png";
	private static final String SUBMODEL_icon = "resources/submodel.png";

	private static final String FILE_ENTER_ADD_FLOW_MODE_ICON = "resources/disconnect.png";
	private static final String FILE_LEAVE_ADD_FLOW_MODE_ICON = "resources/connect.png";
	private static final String FILE_CHANGE_MODEL_NAME_ICON = "resources/page_white_edit.png";
	private static final String Colored_SourceSink_Icon = "resources/cloud-icon.gif";
	private static final String FILE_EXECUTE_MODEL_ICON = "resources/page_white_gear.png";
	private static final String FILE_CANCEL_EXEUTE_MODEL_ICON = "resources/cancel.png";
	private static final String ARCHIVE_ICON = "resources/archive.png";
	private static final String IMPORT_ICON = "resources/import.png";
	private static final String SHARE_ICON = "resources/share.png";

	private static final String FILE_ZOOM_STANDARD_ICON = "resources/zoom.png";
	private static final String FILE_ZOOM_IN_ICON = "resources/zoom_in.png";
	private static final String FILE_ZOOM_OUT_ICON = "resources/zoom_out.png";

	private static final String LANGUAGE_ENGLISH = "English";
	private static final String LANGUAGE_GERMAN = "Deutsch";
	private static final String LANGUAGE_SPANISH = "Español";
	
	private static final String ADD_DESCRIPTION_ICON = "resources/description.png";

	private SystemDynamics start;

	private ResourceBundle messages;

	private ArrayList<SystemDynamicsGraph> graph;
	private SystemDynamicsGraph graphNew;
	private boolean graphModified;

	private JPanel contentPanel;
	private JTabbedPane tabbedPane;
	private JScrollPane scrollPane;
	private JPanel modelPanel;

	private File xmlFile;
	private String fileName;

	private Action newAction;
	private Action openAction;
	private Action closeAction;
	private Action cutAction;
	private Action copyAction;
	private Action pasteAction;
	private Action saveAction;
	private Action saveAsAction;
	private Action newAuxiliaryNodeAction;
	private Action newConstantNodeAction;
	private Action newLevelNodeAction;
	private Action newRateNodeAction;
	private Action newSourceSinkNodeAction;
	private Action newColoredSourceSinkNodeAction;
	private Action toggleAddFlowAction;
	private Action changeModelNameAction;
	private Action executeModelAction;
	private Action exitExecuteModelAction;
	private Action zoomStandardAction;
	private Action zoomInAction;
	private Action zoomOutAction;
	private Action ArchiveSubmodelAction;
	private Action cutActionFunction = javax.swing.TransferHandler.getCutAction();
	private Action copyActionFunction = javax.swing.TransferHandler.getCopyAction();
	private Action pasteActionFunction = javax.swing.TransferHandler.getPasteAction();
	private Action newSubmodelAction;
	private Action importAction;
	private Action shareAction;
	private Action addDescriptionAction;
	private Action chartDesignerAction;
	private JCheckBoxMenuItem addFlowModeCheckBoxMenuItem;
	private JRadioButtonMenuItem rbMenuItemEnglish;
	private JRadioButtonMenuItem rbMenuItemGerman;
	private JRadioButtonMenuItem rbMenuItemSpanish;
	private JLabel GraphNumber = new JLabel("");

	private JFileChooser fileChooser;
	private Random rand = new Random();
	private ArrayList<Color> SubmodelColors= new ArrayList<Color>();


	//File[] selectedFiles;
	File selectedFiles;
	/**
	 * Constructor.
	 * 
	 * @param start {@link de.uka.aifb.com.systemDynamics.SystemDynamics} instance
	 */
	public MainFrame(SystemDynamics start) {
		graph = new ArrayList<SystemDynamicsGraph>();
		if (start == null) {
			throw new IllegalArgumentException("'start' must not be null.");
		}


		this.start = start;
		messages = start.getMessages();

		graphModified = false;

		fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new XMLFileFilter(start));
		//allows JFileChooser to select both files and directories.
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		setTitle(messages.getString("MainFrame.Title"));

		// set frame size and location
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		setSize((int)(dimension.getWidth() * 0.75), (int)(dimension.getHeight() * 0.75));
		setLocation((int)(dimension.getWidth() * 0.125), (int)(dimension.getHeight() * 0.125));

		// set icon
		setIconImage(new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_ICON)).getImage());

		// initialize actions
		initializeActions();

		// create menu
		setJMenuBar(createMenuBar());

		// create tool bar
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(createToolBar(), BorderLayout.PAGE_START);
		contentPanel = new JPanel(new BorderLayout());
		modelPanel = new JPanel(new GridLayout(1,4));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		registerDelAction();

		setVisible(true);

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);

	}

	//ADDED BY JOHN HINKEL.  The registerDelAction and askconfirm methods
	//allow you to capture the delete key for file deletion.  code modified from
	//http://java-demos.blogspot.com/2013/07/how-to-delete-file-through-jfilechooser.html

	private void registerDelAction()
	{
		// Create AbstractAction
		// It is an implementation of javax.swing.Action
		AbstractAction a=new AbstractAction(){

			// Write the handler
			public void actionPerformed(ActionEvent ae)
			{
				JFileChooser jf=(JFileChooser)ae.getSource();
				//allow the filechooser to select both files and directories
				jf.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				try
				{           	

					// Get the selected file
					selectedFiles = jf.getSelectedFile();

					// If some file is selected

					if(selectedFiles!=null)
					{
						// If user confirms to delete
						if(askConfirm()==JOptionPane.YES_OPTION)
						{

							// Call Files.delete(), if any problem occurs
							// the exception can be printed, it can be
							// analysed                       

							File f = new File(selectedFiles.getPath().toString());
							f.delete();


							// Rescan the directory after deletion
							jf.rescanCurrentDirectory();

						}
					}
				}catch(Exception e){
					System.out.println(e);
				}
			}
		};

		// Get action map and map, "delAction" with a
		fileChooser.getActionMap().put("delAction",a);

		// Get input map when fileChooser is in focused window and put a keystroke DELETE
		// associate the key stroke (DELETE) (here) with "delAction"
		fileChooser.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DELETE"),"delAction");

	}


	public int askConfirm()
	{
		// Ask the user whether he/she wants to confirm deleting
		// Return the option chosen by the user either YES/NO
		return JOptionPane.showConfirmDialog(this,"Are you sure want to delete this file?","Confirm",JOptionPane.YES_NO_OPTION);
	}
	/**
	 * Initializes the used actions.
	 */
	private void initializeActions() {
		newAction = new NewAction(messages.getString("MainFrame.MenuBar.File.New"),
				new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_ICON)),
				messages.getString("MainFrame.MenuBar.File.New"));
		openAction = new OpenAction(messages.getString("MainFrame.MenuBar.File.Open"),
				new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_OPEN_ICON)),
				messages.getString("MainFrame.MenuBar.File.Open"));

		closeAction = new CloseAction(messages.getString("MainFrame.MenuBar.File.Close"));
		cutAction = new CutAction("Cut", new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(CUT_ICON)), "Cut",cutActionFunction);
		copyAction = new CopyAction("Copy", new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(COPY_ICON)), "Copy",copyActionFunction);
		pasteAction = new PasteAction("Paste", new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(PASTE_ICON)), "Paste",pasteActionFunction);
		closeAction.setEnabled(false);
		shareAction = new shareAction("Share", new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(SHARE_ICON)), "Share");
		saveAction = new SaveAction(messages.getString("MainFrame.MenuBar.File.Save"),
				new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_SAVE_ICON)),
				messages.getString("MainFrame.MenuBar.File.Save"));
		saveAction.setEnabled(false);
		saveAsAction = new SaveAsAction(messages.getString("MainFrame.MenuBar.File.SaveAs"));
		saveAsAction.setEnabled(false);
		cutAction.setEnabled(false);
		copyAction.setEnabled(false);
		pasteAction.setEnabled(false);
		shareAction.setEnabled(false);
		
		chartDesignerAction = new chartDesignerAction("ChartDesigner", new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(CHART_ICON)), "Enter Chart Designer");
		chartDesignerAction.setEnabled(false);
		newSubmodelAction = new NewSubmodelAction("New Submodel", new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(SUBMODEL_icon)), "Create New Submodel");
		newSubmodelAction.setEnabled(false);
		importAction = new importAction("Import", new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(IMPORT_ICON)), "Import Submodel");
		ArchiveSubmodelAction = new ArchiveSubmodelAction("Archive Submodel", new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(ARCHIVE_ICON)),"Archive Submodel");
		ArchiveSubmodelAction.setEnabled(false);
		if (start.getLocale() == Locale.GERMANY) {
			newAuxiliaryNodeAction = new NewAuxiliaryNodeAction(messages.getString("MainFrame.MenuBar.Edit.NewAuxiliaryNode"),
					new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_AN_de_DE_ICON)),
					messages.getString("MainFrame.MenuBar.Edit.NewAuxiliaryNode"));
			newAuxiliaryNodeAction.setEnabled(false);
			newConstantNodeAction = new NewConstantNodeAction(messages.getString("MainFrame.MenuBar.Edit.NewConstantNode"),
					new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_CN_de_DE_ICON)),
					messages.getString("MainFrame.MenuBar.Edit.NewConstantNode"));
			newConstantNodeAction.setEnabled(false);
			newLevelNodeAction = new NewLevelNodeAction(messages.getString("MainFrame.MenuBar.Edit.NewLevelNode"),
					new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_LN_de_DE_ICON)),
					messages.getString("MainFrame.MenuBar.Edit.NewLevelNode"));
			newLevelNodeAction.setEnabled(false);
			newRateNodeAction = new NewRateNodeAction(messages.getString("MainFrame.MenuBar.Edit.NewRateNode"),
					new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_RN_de_DE_ICON)),
					messages.getString("MainFrame.MenuBar.Edit.NewRateNode"));
			newRateNodeAction.setEnabled(false);
			newSourceSinkNodeAction = new NewSourceSinkNodeAction(messages.getString("MainFrame.MenuBar.Edit.NewSourceSinkNode"),
					new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_SSN_de_DE_ICON)),
					messages.getString("MainFrame.MenuBar.Edit.NewSourceSinkNode"));
			newColoredSourceSinkNodeAction = new NewColoredSourceSinkNodeAction("Colored SourceSinkNode",
					new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(Colored_SourceSink_Icon)),
					messages.getString("Colored SourceSinkNode"));
			newColoredSourceSinkNodeAction.setEnabled(false);
			newSourceSinkNodeAction.setEnabled(false);
		} else {
			// english
			newAuxiliaryNodeAction = new NewAuxiliaryNodeAction(messages.getString("MainFrame.MenuBar.Edit.NewAuxiliaryNode"),
					new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_AN_ICON)),
					messages.getString("MainFrame.MenuBar.Edit.NewAuxiliaryNode"));
			newAuxiliaryNodeAction.setEnabled(false);
			newConstantNodeAction = new NewConstantNodeAction(messages.getString("MainFrame.MenuBar.Edit.NewConstantNode"),
					new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_CN_ICON)),
					messages.getString("MainFrame.MenuBar.Edit.NewConstantNode"));
			newConstantNodeAction.setEnabled(false);
			newLevelNodeAction = new NewLevelNodeAction(messages.getString("MainFrame.MenuBar.Edit.NewLevelNode"),
					new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_LN_ICON)),
					messages.getString("MainFrame.MenuBar.Edit.NewLevelNode"));
			newLevelNodeAction.setEnabled(false);
			newRateNodeAction = new NewRateNodeAction(messages.getString("MainFrame.MenuBar.Edit.NewRateNode"),
					new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_RN_ICON)),
					messages.getString("MainFrame.MenuBar.Edit.NewRateNode"));
			newRateNodeAction.setEnabled(false);
			newSourceSinkNodeAction = new NewSourceSinkNodeAction(messages.getString("MainFrame.MenuBar.Edit.NewSourceSinkNode"),
					new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_SSN_ICON)),
					messages.getString("MainFrame.MenuBar.Edit.NewSourceSinkNode"));
			newSourceSinkNodeAction.setEnabled(false);
			newColoredSourceSinkNodeAction = new NewColoredSourceSinkNodeAction("Colored SourceSinkNode",
					new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(Colored_SourceSink_Icon)),
					"Colored SourceSinkNode");
			newColoredSourceSinkNodeAction.setEnabled(false);
		}

		toggleAddFlowAction = new ToggleAddFlowAction(messages.getString("MainFrame.MenuBar.Edit.ToggleAddFlowMode.EnterAddFlowMode"),
				new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_ENTER_ADD_FLOW_MODE_ICON)),
				messages.getString("MainFrame.MenuBar.Edit.ToggleAddFlowMode.EnterAddFlowMode"));
		toggleAddFlowAction.setEnabled(false);

		changeModelNameAction = new ChangeModelNameAction(messages.getString("MainFrame.MenuBar.Edit.ChangeModelName"),
				new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_CHANGE_MODEL_NAME_ICON)),
				messages.getString("MainFrame.MenuBar.Edit.ChangeModelName"));
		changeModelNameAction.setEnabled(false);

		executeModelAction = new ExecuteModelAction(messages.getString("MainFrame.MenuBar.Edit.ExecuteModel"),
				new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_EXECUTE_MODEL_ICON)),
				messages.getString("MainFrame.MenuBar.Edit.ExecuteModel"));
		executeModelAction.setEnabled(false);
		exitExecuteModelAction = new exitExecuteModelAction(messages.getString("MainFrame.MenuBar.Edit.ExecuteModel"),
				new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_CANCEL_EXEUTE_MODEL_ICON)),
				messages.getString("MainFrame.MenuBar.Edit.ExecuteModel"));
		exitExecuteModelAction.setEnabled(false);

		zoomStandardAction = new ZoomStandardAction(messages.getString("MainFrame.MenuBar.View.ZoomStandard"),
				new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_ZOOM_STANDARD_ICON)),
				messages.getString("MainFrame.MenuBar.View.ZoomStandard"));
		zoomStandardAction.setEnabled(false);
		zoomInAction = new ZoomInAction(messages.getString("MainFrame.MenuBar.View.ZoomIn"),
				new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_ZOOM_IN_ICON)),
				messages.getString("MainFrame.MenuBar.View.ZoomIn"));
		zoomInAction.setEnabled(false);
		zoomOutAction = new ZoomOutAction(messages.getString("MainFrame.MenuBar.View.ZoomOut"),
				new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_ZOOM_OUT_ICON)),
				messages.getString("MainFrame.MenuBar.View.ZoomOut"));
		zoomOutAction.setEnabled(false);

		addDescriptionAction = new AddDescriptionAction("Add description", new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(ADD_DESCRIPTION_ICON)),"Add description to submodel");
		addDescriptionAction.setEnabled(false);
	}

	/**
	 * Creates a new menu bar.
	 * 
	 * @return menu bar
	 */
	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		// create file menu
		JMenu fileMenu = new JMenu(messages.getString("MainFrame.MenuBar.File"));
		JMenuItem menuItem = new JMenuItem(newAction);
		menuItem.setToolTipText(null);
		fileMenu.add(menuItem);
		menuItem = new JMenuItem(openAction);
		menuItem.setToolTipText(null);
		fileMenu.add(menuItem);
		fileMenu.addSeparator();
		fileMenu.add(closeAction);
		fileMenu.addSeparator();
		menuItem = new JMenuItem(saveAction);
		menuItem.setToolTipText(null);
		fileMenu.add(menuItem);
		fileMenu.add(saveAsAction);
		fileMenu.addSeparator();
		JMenuItem exitMenuItem = new JMenuItem(messages.getString("MainFrame.MenuBar.File.Exit"));
		exitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (graphModified) {
					Object[] options = { messages.getString("MainFrame.Yes"), messages.getString("MainFrame.No") };
					int selectedOption = JOptionPane.showOptionDialog(MainFrame.this,
							messages.getString("MainFrame.ConfirmExiting.Message"),
							messages.getString("MainFrame.ConfirmExiting.Title"),
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							null, // don't use a custom Icon
							options,
							options[1]); // default button title

					if (selectedOption == 1) {
						// do not close graph
						return;
					}
				}

				// exit application with "normal" exit code 0
				System.exit(0);      
			}
		});
		fileMenu.add(exitMenuItem);
		menuBar.add(fileMenu);

		// create edit menu
		JMenu editMenu = new JMenu(messages.getString("MainFrame.MenuBar.Edit"));
		menuItem = new JMenuItem(newAuxiliaryNodeAction);
		menuItem.setToolTipText(null);
		editMenu.add(menuItem);
		menuItem = new JMenuItem(newConstantNodeAction);
		menuItem.setToolTipText(null);
		editMenu.add(menuItem);
		menuItem = new JMenuItem(newLevelNodeAction);
		menuItem.setToolTipText(null);
		editMenu.add(menuItem);
		menuItem = new JMenuItem(newRateNodeAction);
		menuItem.setToolTipText(null);
		editMenu.add(menuItem);
		menuItem = new JMenuItem(newSourceSinkNodeAction);
		menuItem.setToolTipText(null);
		editMenu.add(menuItem);
		editMenu.addSeparator();
		addFlowModeCheckBoxMenuItem = new JCheckBoxMenuItem(toggleAddFlowAction);
		addFlowModeCheckBoxMenuItem.setToolTipText(null);
		editMenu.add(addFlowModeCheckBoxMenuItem);
		editMenu.addSeparator();
		menuItem = new JMenuItem(changeModelNameAction);
		menuItem.setToolTipText(null);
		editMenu.add(menuItem);
		editMenu.addSeparator();
		menuItem = new JMenuItem(executeModelAction);
		menuItem.setToolTipText(null);
		editMenu.add(menuItem);
		menuBar.add(editMenu);

		// create view menu
		JMenu viewMenu = new JMenu(messages.getString("MainFrame.MenuBar.View"));
		menuItem = new JMenuItem(zoomStandardAction);
		menuItem.setToolTipText(null);
		viewMenu.add(menuItem);
		menuItem = new JMenuItem(zoomInAction);
		menuItem.setToolTipText(null);
		viewMenu.add(menuItem);
		menuItem = new JMenuItem(zoomOutAction);
		menuItem.setToolTipText(null);
		viewMenu.add(menuItem);
		menuBar.add(viewMenu);

		// create options menu
		JMenu optionsMenu = new JMenu(messages.getString("MainFrame.MenuBar.Options"));
		menuBar.add(optionsMenu);

		// create options -> language menu
		JMenu languageMenu = new JMenu(messages.getString("MainFrame.MenuBar.Options.Language"));
		ButtonGroup group = new ButtonGroup();
		rbMenuItemGerman = new JRadioButtonMenuItem(LANGUAGE_GERMAN);
		if (start.getLocale() == Locale.GERMANY) {
			rbMenuItemGerman.setSelected(true);
		}
		rbMenuItemGerman.addActionListener(this);
		group.add(rbMenuItemGerman);
		languageMenu.add(rbMenuItemGerman);
		rbMenuItemEnglish = new JRadioButtonMenuItem(LANGUAGE_ENGLISH);
		if (start.getLocale() == Locale.US) {
			rbMenuItemEnglish.setSelected(true);
		}
		rbMenuItemEnglish.addActionListener(this);
		group.add(rbMenuItemEnglish);
		languageMenu.add(rbMenuItemEnglish);
		rbMenuItemSpanish = new JRadioButtonMenuItem(LANGUAGE_SPANISH);
		if (start.getLocale().equals(new Locale("es", "ES"))) {
			rbMenuItemSpanish.setSelected(true);
		}
		rbMenuItemSpanish.addActionListener(this);
		group.add(rbMenuItemSpanish);
		languageMenu.add(rbMenuItemSpanish);
		optionsMenu.add(languageMenu);

		// create help menu
		JMenu helpMenu = new JMenu(messages.getString("MainFrame.MenuBar.Help"));
		JMenuItem explanationMenuItem = new JMenuItem(messages.getString("MainFrame.MenuBar.Help.Explanation"));
		explanationMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,
						messages.getString("MainFrame.MenuBar.Help.Explanation.Message"),
						messages.getString("MainFrame.MenuBar.Help.Explanation"),
						JOptionPane.INFORMATION_MESSAGE);            
			}
		});
		helpMenu.add(explanationMenuItem);
		helpMenu.addSeparator();
		JMenuItem aboutMenuItem = new JMenuItem(messages.getString("MainFrame.MenuBar.Help.About"));
		aboutMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JPanel panel = new JPanel(new BorderLayout());
				JLabel label = new JLabel(messages.getString("MainFrame.MenuBar.Help.About.Message"));
				label.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
				panel.add(label, BorderLayout.PAGE_START);

				JPanel subPanel = new JPanel(new BorderLayout());
				JLabel licenseLabel = new JLabel(messages.getString("MainFrame.MenuBar.Help.About.License"));
				licenseLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
				subPanel.add(licenseLabel, BorderLayout.PAGE_START);

				JTextArea textAreaGPL = new JTextArea(15, 83);
				InputStream inGPL = null;
				InputStreamReader readerGPL = null;
				try {
					try {
						inGPL = Thread.currentThread().getContextClassLoader().getResourceAsStream(FILE_GPL_TEXT);
						readerGPL = new InputStreamReader(inGPL);
						textAreaGPL.read(readerGPL, null);
					} catch (IOException exception) {
						textAreaGPL.setText(messages.getString("MainFrame.MenuBar.Help.About.License.GPL.Error"));
					} catch (NullPointerException exception) {
						textAreaGPL.setText(messages.getString("MainFrame.MenuBar.Help.About.License.GPL.Error"));
					} finally {
						if (readerGPL != null) {
							readerGPL.close();
						}
						if (inGPL != null) {
							inGPL.close();
						}
					}
				} catch (IOException exception) {
					// catch IOException of 'fileReader.close()' -> do nothing
				}
				textAreaGPL.setEditable(false);
				JScrollPane areaScrollPaneGPL = new JScrollPane(textAreaGPL);

				JTextArea textAreaLGPL = new JTextArea(15, 83);
				InputStream inLGPL = null;
				InputStreamReader readerLGPL = null;
				try {
					try {
						inLGPL = Thread.currentThread().getContextClassLoader().getResourceAsStream(FILE_LGPL_TEXT);
						readerLGPL = new InputStreamReader(inLGPL);
						textAreaLGPL.read(readerLGPL, null);
					} catch (IOException exception) {
						textAreaLGPL.setText(messages.getString("MainFrame.MenuBar.Help.About.License.LGPL.Error"));
					} catch (NullPointerException exception) {
						textAreaLGPL.setText(messages.getString("MainFrame.MenuBar.Help.About.License.LGPL.Error"));
					} finally {
						if (readerLGPL != null) {
							readerLGPL.close();
						}
						if (inLGPL != null) {
							inLGPL.close();
						}
					}
				} catch (IOException exception) {
					// catch IOException of 'fileReader.close()' -> do nothing
				}
				textAreaLGPL.setEditable(false);
				JScrollPane areaScrollPaneLGPL = new JScrollPane(textAreaLGPL);

				JTextArea textAreaCCBY = new JTextArea(15, 83);
				InputStream inCCBY = null;
				InputStreamReader readerCCBY = null;
				try {
					try {
						inCCBY = Thread.currentThread().getContextClassLoader().getResourceAsStream(FILE_CC_BY_TEXT);
						readerCCBY = new InputStreamReader(inCCBY);
						textAreaCCBY.read(readerCCBY, null);
					} catch (IOException exception) {
						textAreaCCBY.setText(messages.getString("MainFrame.MenuBar.Help.About.License.CCBY.Error"));
					} catch (NullPointerException exception) {
						textAreaCCBY.setText(messages.getString("MainFrame.MenuBar.Help.About.License.CCBY.Error"));
					} finally {
						if (readerCCBY != null) {
							readerCCBY.close();
						}
						if (inCCBY != null) {
							inCCBY.close();
						}
					}
				} catch (IOException exception) {
					// catch IOException of 'fileReader.close()' -> do nothing
				}
				textAreaCCBY.setEditable(false);
				JScrollPane areaScrollPaneCCBY = new JScrollPane(textAreaCCBY);

				JTabbedPane tabbedPane = new JTabbedPane();
				tabbedPane.add(messages.getString("MainFrame.MenuBar.Help.About.License.GPL"), areaScrollPaneGPL);
				tabbedPane.add(messages.getString("MainFrame.MenuBar.Help.About.License.LGPL"), areaScrollPaneLGPL);
				tabbedPane.add(messages.getString("MainFrame.MenuBar.Help.About.License.CCBY"), areaScrollPaneCCBY);

				subPanel.add(tabbedPane, BorderLayout.CENTER);
				subPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));



				JPanel helpPanel = new JPanel();
				helpPanel.add(subPanel);
				helpPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

				panel.add(helpPanel, BorderLayout.CENTER);

				JOptionPane.showMessageDialog(null, panel,
						messages.getString("MainFrame.MenuBar.Help.About"),
						JOptionPane.PLAIN_MESSAGE);            
			}
		});
		helpMenu.add(aboutMenuItem);
		menuBar.add(helpMenu);

		return menuBar;      
	}

	/**
	 * Creates a new tool bar.
	 * 
	 * @return tool bar
	 */
	private JToolBar createToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.setRollover(true);
		toolBar.setFloatable(false);

		toolBar.add(newAction);
		toolBar.add(openAction);
		toolBar.add(importAction);
		toolBar.add(saveAction);
		toolBar.add(ArchiveSubmodelAction);

		toolBar.addSeparator();

		toolBar.add(newAuxiliaryNodeAction);
		toolBar.add(newConstantNodeAction);
		toolBar.add(newLevelNodeAction);
		toolBar.add(newRateNodeAction);
		toolBar.add(newSourceSinkNodeAction);
		toolBar.add(newColoredSourceSinkNodeAction);
		toolBar.add(newSubmodelAction);
		toolBar.add(toggleAddFlowAction);
		toolBar.add(changeModelNameAction);

		toolBar.addSeparator();

		toolBar.add(executeModelAction);
		toolBar.add(exitExecuteModelAction);
		toolBar.add(chartDesignerAction);

		toolBar.addSeparator();

		toolBar.add(zoomStandardAction);
		toolBar.add(zoomInAction);
		toolBar.add(zoomOutAction);
		toolBar.add(cutAction);
		toolBar.add(copyAction);
		toolBar.add(pasteAction);
		toolBar.add(shareAction);
		toolBar.addSeparator();
		
		toolBar.add(addDescriptionAction);
		
		toolBar.addSeparator();
		
		GraphNumber.setFont(new Font(GraphNumber.getFont().getName(), Font.PLAIN, 30));
		toolBar.add(GraphNumber);

		return toolBar;

	}

	/**
	 * Creates the title for this frame.
	 * 
	 * @param modelName model name (or <code>null</code> if no model opened)
	 * @param changed model changed (ignored if first parameter is <code>null</code>)
	 * @return title for this frame
	 */
	private String createTitle(String modelName, boolean changed) {
		StringBuffer title = new StringBuffer();
		title.append(messages.getString("MainFrame.Title"));

		if (modelName != null) {
			String zoomString = ((int)(graph.get(0).getScale() * 100)) + "%";
			title.append(" - ");
			title.append(modelName);
			title.append(" (" + messages.getString("MainFrame.Title.Zoom") + " " + zoomString + ") - ");
			title.append("[ " + fileName + " ");
			if (changed) {
				title.append("*");
			}
			title.append("]");
		}

		return title.toString();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	//                           methods of interface ActionListener
	////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Performs the specified action event that is caused by one of the language selection
	 * <code>JRadioButtonMenuItem</code>s.
	 * 
	 * @param e event
	 */
	public void actionPerformed(ActionEvent e) {
		boolean changed = false;

		if (e.getSource() == rbMenuItemEnglish) {
			// change language to English
			start.storeNewLocale(Locale.US);
			changed = true;
		}

		if (e.getSource() == rbMenuItemGerman) {
			// change language to German
			start.storeNewLocale(Locale.GERMANY);
			changed = true;
		}

		if (e.getSource() == rbMenuItemSpanish) {
			// change language to Spanish
			start.storeNewLocale(new Locale("es", "ES"));
			changed = true;
		}

		if (changed) {
			JOptionPane.showMessageDialog(null,
					messages.getString("MainFrame.MenuBar.Options.Language.Message"),
					messages.getString("MainFrame.MenuBar.Options.Language"),
					JOptionPane.INFORMATION_MESSAGE);  
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	//                           methods of interface ChangeListener
	////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Performs the specified change event that is caused by the tabbed pane.
	 * 
	 * @param e event
	 */
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == tabbedPane) {
			if (tabbedPane.getSelectedIndex() == 0) {
				// tab "model" selected
				zoomStandardAction.setEnabled(true);
				zoomInAction.setEnabled(true);
				zoomOutAction.setEnabled(true);

			} else {
				// tab "chart" or "export" selected
				zoomStandardAction.setEnabled(false);
				zoomInAction.setEnabled(false);
				zoomOutAction.setEnabled(false);
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	//                  methods of interface SystemDynamicsGraphModifiedEventListener
	////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Performs a graph modified event.
	 */
	public void performGraphModifiedEvent() {
		if (!graphModified) {
			graphModified = true;

			setTitle(createTitle(graph.get(0).getModelName(), graphModified));

			saveAction.setEnabled(true);
			saveAsAction.setEnabled(true);
			ArchiveSubmodelAction.setEnabled(true);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	//                             methods of interface WindowListener
	////////////////////////////////////////////////////////////////////////////////////////////////////

	public void windowActivated(WindowEvent e) {
		// do noting
	}

	public void windowClosed(WindowEvent e) {
		// do nothing
	}

	public void windowClosing(WindowEvent e) {
		if (graphModified) {
			Object[] options = { messages.getString("MainFrame.Yes"), messages.getString("MainFrame.No") };
			int selectedOption = JOptionPane.showOptionDialog(MainFrame.this,
					messages.getString("MainFrame.ConfirmExiting.Message"),
					messages.getString("MainFrame.ConfirmExiting.Title"),
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null, // don't use a custom Icon
					options,
					options[1]); // default button title

			if (selectedOption == 1) {
				// do not close graph
				return;
			}
		}

		// exit application with "normal" exit code 0
		System.exit(0);      
	}

	public void windowDeactivated(WindowEvent e) {
		// do nothing
	}

	public void windowDeiconified(WindowEvent e) {
		// do nothing
	}

	public void windowIconified(WindowEvent e) {
		// do nothing
	}

	public void windowOpened(WindowEvent e) {
		// do nothing
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	//                                           Actions
	////////////////////////////////////////////////////////////////////////////////////////////////////   

	private class NewAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public NewAction(String name, Icon icon, String toolTipText) {
			super(name, icon);

			putValue(Action.SHORT_DESCRIPTION, toolTipText);

		}

		public void actionPerformed(ActionEvent e) {
			int reply = JOptionPane.YES_OPTION;
			//changes reply if "no" option is selected.
			//only asks the question if there are components in the graph
			if(graphModified != false){
				reply = JOptionPane.showConfirmDialog(null, "Creating a new document will cause any unsaved changes to be lost. \n Are you sure you would like to create a new document?", "Create", JOptionPane.YES_NO_OPTION);
			}
			if(reply==JOptionPane.YES_OPTION){
				modelPanel.setLayout(new GridLayout(1,4));
				modelPanel.removeAll();
				contentPanel.remove(modelPanel);
				SubmodelColors.removeAll(SubmodelColors);
				graph.removeAll(graph);


				contentPanel.revalidate();
				String modelName =
						ModelNameDialog.showModelNameDialog(start, MainFrame.this,
								messages.getString("MainFrame.MenuBar.File.NewModel"),
								messages.getString("MainFrame.MenuBar.File.NewModelName"),
								"");

				graphNew = new SystemDynamicsGraph(start, MainFrame.this);
				graph.add(graphNew);
				GraphNumber.setText("1");
				if (modelName != null) {
					DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z");
					Date today = Calendar.getInstance().getTime();
					String modelDate = df.format(today);
					modelName += " ";
					modelName += modelDate;
					graph.get(0).setModelName(modelName);
					graph.get(0).addSystemDynamicsGraphModifiedEventListener(MainFrame.this);
					scrollPane = new JScrollPane(graph.get(0));

					int red = rand.nextInt(256);
					int green = rand.nextInt(256);
					int blue = rand.nextInt(256);
					for(int i=0;i<SubmodelColors.size();i++){
						if(Math.abs(SubmodelColors.get(i).getRed()-red) < 40){
							red = rand.nextInt(256);
						}
						if(Math.abs(SubmodelColors.get(i).getBlue()-blue) < 30){
							blue = rand.nextInt(256);
						}
						if(Math.abs(SubmodelColors.get(i).getGreen()-green) < 20){
							green = rand.nextInt(256);
						}
					}
					Color randomColor = new Color(red, green, blue);
					SubmodelColors.add(randomColor);

					Border SubmodelColor = BorderFactory.createLineBorder(SubmodelColors.get(SubmodelColors.size()-1),15);

					graph.get(graph.size()-1).setBorder(SubmodelColor);
					graph.get(0).setSize(400,400);
					scrollPane.setPreferredSize(new Dimension(400,400));
					graph.get(0).addMouseListener(new MouseListener(){

						@Override
						public void mouseClicked(MouseEvent e) {}

						@Override
						public void mouseEntered(MouseEvent e) {
							//change label whenever mouse enters graph
							GraphNumber.setText(Integer.toString(1));

						}

						@Override
						public void mouseExited(MouseEvent e) {}

						@Override
						public void mousePressed(MouseEvent e) {}

						@Override
						public void mouseReleased(MouseEvent e) {}

					});
					contentPanel.removeAll();
					modelPanel.add(scrollPane);

					contentPanel.add(modelPanel,BorderLayout.CENTER);

					getContentPane().validate();

					fileName = messages.getString("MainFrame.NewDocument");
					setTitle(createTitle(graph.get(0).getModelName(), false));

					newAction.setEnabled(true);
					openAction.setEnabled(true);
					closeAction.setEnabled(true);
					saveAction.setEnabled(false);
					saveAsAction.setEnabled(false);
					newSubmodelAction.setEnabled(true);
					ArchiveSubmodelAction.setEnabled(false);
					newAuxiliaryNodeAction.setEnabled(true);
					newConstantNodeAction.setEnabled(true);
					newLevelNodeAction.setEnabled(true);
					newRateNodeAction.setEnabled(true);
					newSourceSinkNodeAction.setEnabled(true);
					newColoredSourceSinkNodeAction.setEnabled(true);
					toggleAddFlowAction.setEnabled(true);
					changeModelNameAction.setEnabled(true);
					executeModelAction.setEnabled(true);
					shareAction.setEnabled(true);
					zoomStandardAction.setEnabled(true);
					zoomInAction.setEnabled(true);
					zoomOutAction.setEnabled(true);
					cutAction.setEnabled(true);
					copyAction.setEnabled(true);
					pasteAction.setEnabled(true);
					chartDesignerAction.setEnabled(true);
					addDescriptionAction.setEnabled(true);
				}
			}
			modelPanel.add(scrollPane);
		}

	}
	private class chartDesignerAction extends AbstractAction{
		public chartDesignerAction(String name, Icon icon, String toolTipText){
			super(name, icon);
			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			ArrayList<Model> model = new ArrayList<Model>();
			for(int i=0;i<graph.size();i++){
				model.add(graph.get(i).model);
			}
			ChartMainFrame ChartDesigner = new ChartMainFrame(model);
			
		}
		
	}
	private class NewSubmodelAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public NewSubmodelAction(String name, Icon icon, String toolTipText) {
			super(name, icon);

			putValue(Action.SHORT_DESCRIPTION, toolTipText);

		}

		public void actionPerformed(ActionEvent e) {
			//create a new submodel graph
			final SystemDynamicsGraph newSubmodel = new SystemDynamicsGraph(start,MainFrame.this);

			newSubmodel.addMouseListener(new MouseListener(){

				@Override
				public void mouseClicked(MouseEvent e) {}

				@Override
				public void mouseEntered(MouseEvent e) {
					//Change label on toolbar to show graph number
					for(int i=0;i<graph.size();i++){
						if(graph.get(i).equals(newSubmodel)){

							GraphNumber.setText(Integer.toString(i+1));
						}
					}

				}

				@Override
				public void mouseExited(MouseEvent e) {}

				@Override
				public void mousePressed(MouseEvent e) {}

				@Override
				public void mouseReleased(MouseEvent e) {}

			});
			//add it to the model
			graph.add(newSubmodel);

			//create scroll pane for the new submodel

			JScrollPane submodelScroll = new JScrollPane(graph.get(graph.size()-1));

			//random submodel color is created for the working session and then added to an overall list to be referenced later.


			int red = rand.nextInt(256);
			int green = rand.nextInt(256);
			int blue = rand.nextInt(256);
			for(int i=0;i<SubmodelColors.size();i++){
				if(Math.abs(SubmodelColors.get(i).getRed()-red) < 40){
					red = rand.nextInt(256);
				}
				if(Math.abs(SubmodelColors.get(i).getBlue()-blue) < 30){
					blue = rand.nextInt(256);
				}
				if(Math.abs(SubmodelColors.get(i).getGreen()-green) < 20){
					green = rand.nextInt(256);
				}
			}
			Color randomColor = new Color(red, green, blue);

			SubmodelColors.add(randomColor);
			GraphNumber.setText(Integer.toString(graph.size()));

			Border SubmodelColor = BorderFactory.createLineBorder(SubmodelColors.get(SubmodelColors.size()-1),15);

			graph.get(graph.size()-1).setBorder(SubmodelColor);
			graph.get(graph.size()-1).setSize(400,400);
			graph.get(graph.size()-1).addSystemDynamicsGraphModifiedEventListener(MainFrame.this);

			//add it to the model panel 

			modelPanel.add(submodelScroll);

			//reconfigure layout for 4 or more submodels
			if(graph.size()>=4){
				modelPanel.setLayout(new GridLayout(2,4));
			}
			//force layout to recalculate now that a new component has been added.
			modelPanel.revalidate();

			saveAction.setEnabled(true);
			ArchiveSubmodelAction.setEnabled(true);
		}

	}
	private class importAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public importAction(String name, Icon icon, String toolTipText) {
			super(name, icon);

			putValue(Action.SHORT_DESCRIPTION, toolTipText);

		}

		public void actionPerformed(ActionEvent e) {
			saveAction.setEnabled(false);
			int returnVal = fileChooser.showOpenDialog(MainFrame.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				// file was selected and 'OK' was pressed
				File file = fileChooser.getSelectedFile();
				try {
					ArrayList<SystemDynamicsGraph> newGraphs = new ArrayList<SystemDynamicsGraph>();
					newGraphs = XMLModelReader.readXMLSystemDynamicsGraph(file.getAbsolutePath(), start, MainFrame.this);
					modelPanel.removeAll();
					modelPanel.revalidate();
					for(int j=0;j<newGraphs.size();j++){
						graph.add(newGraphs.get(j));
						SubmodelColors.add(newGraphs.get(j).borderColor);
					}

					ArrayList<Model> graphModels = new ArrayList<Model>();
					graphModels = XMLModelReader.readXMLModel(file.getAbsolutePath());
					
					for(int i=0;i<graphModels.size();i++){
						graph.get(i).model = graphModels.get(i);
					}
					System.out.println(graph.size());
					for(int i=0;i<graph.size();i++){

						graph.get(i).addSystemDynamicsGraphModifiedEventListener(MainFrame.this);

					}


				} catch (AuxiliaryNodesCycleDependencyException excep) {
					JOptionPane.showMessageDialog(MainFrame.this,
							messages.getString("MainFrame.OpenFile.AuxiliaryNodesCycleDependencyException.Text"),
							messages.getString("MainFrame.OpenFile.Error"),
							JOptionPane.ERROR_MESSAGE);

					return;
				} catch (XMLNodeParameterOutOfRangeException excep) {
					JOptionPane.showMessageDialog(MainFrame.this,
							messages.getString("MainFrame.OpenFile.NodeParameterOutOfRangeException.Text1") + " '" + excep.getXMLNodeId() + "' " + messages.getString("MainFrame.OpenFile.NodeParameterOutOfRangeException.Text2"),
							messages.getString("MainFrame.OpenFile.Error"),
							JOptionPane.ERROR_MESSAGE);

					return;
				} catch (XMLRateNodeFlowException excep) {
					JOptionPane.showMessageDialog(MainFrame.this,
							messages.getString("MainFrame.OpenFile.RateNodeFlowException.Text1") + " '" + excep.getXMLNodeId() + "' " + messages.getString("MainFrame.OpenFile.RateNodeFlowException.Text2"),
							messages.getString("MainFrame.OpenFile.Error"),
							JOptionPane.ERROR_MESSAGE);

					return;
				} catch (XMLUselessNodeException excep) {
					JOptionPane.showMessageDialog(MainFrame.this,
							messages.getString("MainFrame.OpenFile.UselessNodeException.Text1") + " '" + excep.getXMLNodeId() + "' " + messages.getString("MainFrame.OpenFile.UselessNodeException.Text2"),
							messages.getString("MainFrame.OpenFile.Error"),
							JOptionPane.ERROR_MESSAGE);

					return;
				} catch (XMLModelReaderWriterException excep) {
					JOptionPane.showMessageDialog(MainFrame.this,
							messages.getString("MainFrame.OpenFile.XMLModelReaderWriterException.Text") + " " + excep.getException().getMessage(),
							messages.getString("MainFrame.OpenFile.Error"),
							JOptionPane.ERROR_MESSAGE);

					return;
				}

				// opening successful
				xmlFile = file;


				for(int i=0;i<graph.size();i++){
					JScrollPane submodelScroll = new JScrollPane(graph.get(i));
					final SystemDynamicsGraph Submodel = graph.get(i);
					modelPanel.add(submodelScroll);
					graph.get(i).addMouseListener(new MouseListener(){

						@Override
						public void mouseClicked(MouseEvent e) {}

						@Override
						public void mouseEntered(MouseEvent e) {
							//Change label on toolbar to show graph number
							for(int j=0;j<graph.size();j++){

								if(graph.get(j).equals(Submodel)){

									GraphNumber.setText(Integer.toString(j+1));
								}
							}

						}

						@Override
						public void mouseExited(MouseEvent e) {}

						@Override
						public void mousePressed(MouseEvent e) {}

						@Override
						public void mouseReleased(MouseEvent e) {}

					});
				}

				//reconfigure layout for 4 or more submodels
				contentPanel.removeAll();
				contentPanel.revalidate();

				if(graph.size()>=4){
					modelPanel.setLayout(new GridLayout(2,4));
				}
				contentPanel.add(modelPanel,BorderLayout.CENTER);

				getContentPane().validate();

				//force layout to recalculate now that a new component has been added.
				modelPanel.revalidate();
				JOptionPane.showMessageDialog(MainFrame.this, "SubModel Successfully imported.  Don't forget to change the names of shared variables (if any)");
				saveAction.setEnabled(true);
				ArchiveSubmodelAction.setEnabled(true);

				fileName = xmlFile.getAbsolutePath();
				setTitle(createTitle(graph.get(0).getModelName(), false));

				newAction.setEnabled(true);
				closeAction.setEnabled(true);
				saveAction.setEnabled(false);
				saveAsAction.setEnabled(true);

				newAuxiliaryNodeAction.setEnabled(true);
				newConstantNodeAction.setEnabled(true);
				newLevelNodeAction.setEnabled(true);
				newRateNodeAction.setEnabled(true);
				newSourceSinkNodeAction.setEnabled(true);
				newColoredSourceSinkNodeAction.setEnabled(true);
				toggleAddFlowAction.setEnabled(true);
				changeModelNameAction.setEnabled(true);
				executeModelAction.setEnabled(true);
				newSubmodelAction.setEnabled(true);
				zoomStandardAction.setEnabled(true);
				zoomInAction.setEnabled(true);
				zoomOutAction.setEnabled(true);
				cutAction.setEnabled(true);
				copyAction.setEnabled(true);
				pasteAction.setEnabled(true);

				try {
					int i=0;
					for (SystemDynamicsGraph subGraph : graph) {

						if(subGraph.getModel().getRootCount() != 0){
							subGraph.validateModel(i);
						}
						i++;
					}
				} catch (AuxiliaryNodesCycleDependencyException excep) {
					JOptionPane.showMessageDialog(MainFrame.this,
							messages.getString("MainFrame.SaveFile.AuxiliaryNodesCycleDependencyException.Text"),
							messages.getString("MainFrame.SaveFile.Error"),
							JOptionPane.ERROR_MESSAGE);

					return;
				} catch (NoFormulaException excep) {
					JOptionPane.showMessageDialog(MainFrame.this,
							messages.getString("MainFrame.SaveFile.NoFormulaException.Text1") + " '" + excep.getNodeWithourFormula().getNodeName() + "' " + messages.getString("MainFrame.SaveFile.NoFormulaException.Text2"),
							messages.getString("MainFrame.SaveFile.Error"),
							JOptionPane.ERROR_MESSAGE);

					return;
				} catch (NoLevelNodeException excep) {
					JOptionPane.showMessageDialog(MainFrame.this,
							messages.getString("MainFrame.SaveFile.NoLevelNodeException.Text"),
							messages.getString("MainFrame.SaveFile.Error"),
							JOptionPane.ERROR_MESSAGE);

					return;
				} catch (RateNodeFlowException excep) {
					JOptionPane.showMessageDialog(MainFrame.this,
							messages.getString("MainFrame.SaveFile.RateNodeFlowException.Text1") + " '" + excep.getProblematicRateNode().getNodeName() + "' " + messages.getString("MainFrame.SaveFile.RateNodeFlowException.Text2"),
							messages.getString("MainFrame.SaveFile.Error"),
							JOptionPane.ERROR_MESSAGE);

					return;
				} catch (UselessNodeException excep ) {
					if(excep.getUselessNode().getClass().toString().equals("class de.uka.aifb.com.systemDynamics.model.SourceSinkNode")){
						JOptionPane.showMessageDialog(MainFrame.this,
								"Invalid model:" + " A Source/Sink node " + messages.getString("MainFrame.SaveFile.UselessNodeException.Text2"),
								messages.getString("MainFrame.SaveFile.Error"),
								JOptionPane.ERROR_MESSAGE);
						return;

					}else{
						JOptionPane.showMessageDialog(MainFrame.this,
								messages.getString("MainFrame.SaveFile.UselessNodeException.Text1") + " '" + excep.getUselessNode().getNodeName() + "' " + messages.getString("MainFrame.SaveFile.UselessNodeException.Text2"),
								messages.getString("MainFrame.SaveFile.Error"),
								JOptionPane.ERROR_MESSAGE);

						return;
					}			
				}
				modelPanel.revalidate();

			}

		}

	}
	private class OpenAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public OpenAction(String name, Icon icon, String toolTipText) {
			super(name, icon);

			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}

		public void actionPerformed(ActionEvent e) {
			int reply = JOptionPane.YES_OPTION;
			//changes reply if "no" option is selected.
			//only asks the question if there are components in the graph
			if(graphModified != false){
				reply = JOptionPane.showConfirmDialog(null, "Opening a new document will cause any unsaved changes to be lost. \n Are you sure you would like to open a new document?", "Open", JOptionPane.YES_NO_OPTION);
			}
			if(reply == JOptionPane.YES_OPTION){
				int returnVal = fileChooser.showOpenDialog(MainFrame.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					// file was selected and 'OK' was pressed
					File file = fileChooser.getSelectedFile();
					try {
						//TODO
						//graph = XMLModelReader.readSystemDynamicsGraph(file.getAbsolutePath(), start, MainFrame.this);
						//graph.get(0).addSystemDynamicsGraphModifiedEventLIstener(MainFrame.this);
						//DELETE FOLLOWING 4 LINES
						
						graph = XMLModelReader.readXMLSystemDynamicsGraph(file.getAbsolutePath(), start, MainFrame.this);
						System.out.println("FINISHED READING GRAPH");
						
//						ArrayList<Model> graphModels = new ArrayList<Model>();
//						graphModels = XMLModelReader.readXMLModel(file.getAbsolutePath());
//						
//						for(int i=0;i<graphModels.size();i++){
//							graph.get(i).model = graphModels.get(i);
//						}
						
						for(int i=0;i<graph.size();i++){
							graph.get(i).addSystemDynamicsGraphModifiedEventListener(MainFrame.this);
							System.out.println(graph.get(0).model.getLevelNodes().size());
						}


					} catch (AuxiliaryNodesCycleDependencyException excep) {
						JOptionPane.showMessageDialog(MainFrame.this,
								messages.getString("MainFrame.OpenFile.AuxiliaryNodesCycleDependencyException.Text"),
								messages.getString("MainFrame.OpenFile.Error"),
								JOptionPane.ERROR_MESSAGE);

						return;
					} catch (XMLNodeParameterOutOfRangeException excep) {
						JOptionPane.showMessageDialog(MainFrame.this,
								messages.getString("MainFrame.OpenFile.NodeParameterOutOfRangeException.Text1") + " '" + excep.getXMLNodeId() + "' " + messages.getString("MainFrame.OpenFile.NodeParameterOutOfRangeException.Text2"),
								messages.getString("MainFrame.OpenFile.Error"),
								JOptionPane.ERROR_MESSAGE);

						return;
					} catch (XMLRateNodeFlowException excep) {
						JOptionPane.showMessageDialog(MainFrame.this,
								messages.getString("MainFrame.OpenFile.RateNodeFlowException.Text1") + " '" + excep.getXMLNodeId() + "' " + messages.getString("MainFrame.OpenFile.RateNodeFlowException.Text2"),
								messages.getString("MainFrame.OpenFile.Error"),
								JOptionPane.ERROR_MESSAGE);

						return;
					} catch (XMLUselessNodeException excep) {
						JOptionPane.showMessageDialog(MainFrame.this,
								messages.getString("MainFrame.OpenFile.UselessNodeException.Text1") + " '" + excep.getXMLNodeId() + "' " + messages.getString("MainFrame.OpenFile.UselessNodeException.Text2"),
								messages.getString("MainFrame.OpenFile.Error"),
								JOptionPane.ERROR_MESSAGE);

						return;
					} catch (XMLModelReaderWriterException excep) {
						JOptionPane.showMessageDialog(MainFrame.this,
								messages.getString("MainFrame.OpenFile.XMLModelReaderWriterException.Text") + " " + excep.getException().getMessage(),
								messages.getString("MainFrame.OpenFile.Error"),
								JOptionPane.ERROR_MESSAGE);

						return;
					}

					// opening successful
					xmlFile = file;
					modelPanel.removeAll();
					modelPanel.revalidate();
					for(int i=0;i<graph.size();i++){
						JScrollPane submodelScroll = new JScrollPane(graph.get(i));
						final SystemDynamicsGraph Submodel = graph.get(i);
						modelPanel.add(submodelScroll);
						graph.get(i).revalidate();
						graph.get(i).repaint();
						graph.get(i).enableInputMethods(true);
						graph.get(i).addMouseListener(new MouseListener(){
						
							@Override
							public void mouseClicked(MouseEvent e) {}

							@Override
							public void mouseEntered(MouseEvent e) {
								//Change label on toolbar to show graph number
								for(int j=0;j<graph.size();j++){

									if(graph.get(j).equals(Submodel)){

										GraphNumber.setText(Integer.toString(j+1));
									}
								}

							}

							@Override
							public void mouseExited(MouseEvent e) {}

							@Override
							public void mousePressed(MouseEvent e) {}

							@Override
							public void mouseReleased(MouseEvent e) {}

						});
					}

					//reconfigure layout for 4 or more submodels
					contentPanel.removeAll();
					contentPanel.revalidate();
					
					if(graph.size()>=4){
						modelPanel.setLayout(new GridLayout(2,4));
					}
					contentPanel.add(modelPanel,BorderLayout.CENTER);

					getContentPane().validate();
					modelPanel.repaint();
					for(int i=0;i<graph.size();i++){
						SubmodelColors.add(graph.get(i).borderColor);
					}
					//force layout to recalculate now that a new component has been added.
					modelPanel.revalidate();
					JOptionPane.showMessageDialog(MainFrame.this, "Model Successfully opened.  Don't forget to change the names of shared variables (if any)");
					saveAction.setEnabled(true);
					ArchiveSubmodelAction.setEnabled(true);

					fileName = xmlFile.getAbsolutePath();
					setTitle(createTitle(graph.get(0).getModelName(), false));

					newAction.setEnabled(true);
					closeAction.setEnabled(true);
					saveAction.setEnabled(false);
					saveAsAction.setEnabled(true);

					newAuxiliaryNodeAction.setEnabled(true);
					newConstantNodeAction.setEnabled(true);
					newLevelNodeAction.setEnabled(true);
					newRateNodeAction.setEnabled(true);
					newSourceSinkNodeAction.setEnabled(true);
					newColoredSourceSinkNodeAction.setEnabled(true);
					toggleAddFlowAction.setEnabled(true);
					changeModelNameAction.setEnabled(true);
					executeModelAction.setEnabled(true);
					shareAction.setEnabled(true);
					zoomStandardAction.setEnabled(true);
					zoomInAction.setEnabled(true);
					zoomOutAction.setEnabled(true);
					cutAction.setEnabled(true);
					copyAction.setEnabled(true);
					pasteAction.setEnabled(true);
					newSubmodelAction.setEnabled(true);
					chartDesignerAction.setEnabled(true);
					addDescriptionAction.setEnabled(true);
					try {
						int i=0;
						for (SystemDynamicsGraph subGraph : graph) {

							if(subGraph.getModel().getRootCount() != 0){
								subGraph.validateModel(i);
							}
							i++;
						}
					} catch (AuxiliaryNodesCycleDependencyException excep) {
						JOptionPane.showMessageDialog(MainFrame.this,
								messages.getString("MainFrame.SaveFile.AuxiliaryNodesCycleDependencyException.Text"),
								messages.getString("MainFrame.SaveFile.Error"),
								JOptionPane.ERROR_MESSAGE);

						return;
					} catch (NoFormulaException excep) {
						JOptionPane.showMessageDialog(MainFrame.this,
								messages.getString("MainFrame.SaveFile.NoFormulaException.Text1") + " '" + excep.getNodeWithourFormula().getNodeName() + "' " + messages.getString("MainFrame.SaveFile.NoFormulaException.Text2"),
								messages.getString("MainFrame.SaveFile.Error"),
								JOptionPane.ERROR_MESSAGE);

						return;
					} catch (NoLevelNodeException excep) {
						JOptionPane.showMessageDialog(MainFrame.this,
								messages.getString("MainFrame.SaveFile.NoLevelNodeException.Text"),
								messages.getString("MainFrame.SaveFile.Error"),
								JOptionPane.ERROR_MESSAGE);

						return;
					} catch (RateNodeFlowException excep) {
						JOptionPane.showMessageDialog(MainFrame.this,
								messages.getString("MainFrame.SaveFile.RateNodeFlowException.Text1") + " '" + excep.getProblematicRateNode().getNodeName() + "' " + messages.getString("MainFrame.SaveFile.RateNodeFlowException.Text2"),
								messages.getString("MainFrame.SaveFile.Error"),
								JOptionPane.ERROR_MESSAGE);

						return;
					} catch (UselessNodeException excep ) {
						if(excep.getUselessNode().getClass().toString().equals("class de.uka.aifb.com.systemDynamics.model.SourceSinkNode")){
							JOptionPane.showMessageDialog(MainFrame.this,
									"Invalid model:" + " A Source/Sink node " + messages.getString("MainFrame.SaveFile.UselessNodeException.Text2"),
									messages.getString("MainFrame.SaveFile.Error"),
									JOptionPane.ERROR_MESSAGE);
							return;

						}else{
							JOptionPane.showMessageDialog(MainFrame.this,
									messages.getString("MainFrame.SaveFile.UselessNodeException.Text1") + " '" + excep.getUselessNode().getNodeName() + "' " + messages.getString("MainFrame.SaveFile.UselessNodeException.Text2"),
									messages.getString("MainFrame.SaveFile.Error"),
									JOptionPane.ERROR_MESSAGE);

							return;
						}			
					}


				}
			}
		}
	}

	private class CloseAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public CloseAction(String name) {
			super(name);
		}

		public void actionPerformed(ActionEvent e) {
			if (graphModified) {
				Object[] options = { messages.getString("MainFrame.Yes"), messages.getString("MainFrame.No") };
				int selectedOption = JOptionPane.showOptionDialog(MainFrame.this,
						messages.getString("MainFrame.ConfirmClosing.Message"),
						messages.getString("MainFrame.ConfirmClosing.Title"),
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null, // don't use a custom Icon
						options,
						options[1]); // default button title

				if (selectedOption == 1) {
					// do not close graph
					return;
				}
			}

			if (((ToggleAddFlowAction)toggleAddFlowAction).isInAddFlowMode()) {
				// first leave "add flow mode" -> then close model
				toggleAddFlowAction.actionPerformed(null);            
			}

			contentPanel.removeAll();
			contentPanel.add(new JPanel(), BorderLayout.CENTER);
			getContentPane().validate();

			fileName = null;
			setTitle(createTitle(null, false));

			graph.removeAll(graph);
			graphModified = false;
			xmlFile = null;
			scrollPane = null;
			tabbedPane = null;

			// reset file chooser (otherwise, old selected file is pre-selected and could be overwritten!)
			File currentDirectory = fileChooser.getCurrentDirectory();
			fileChooser = new JFileChooser();
			fileChooser.setFileFilter(new XMLFileFilter(start));
			fileChooser.setCurrentDirectory(currentDirectory);

			newAction.setEnabled(true);
			openAction.setEnabled(true);
			closeAction.setEnabled(false);
			saveAction.setEnabled(false);
			saveAsAction.setEnabled(false);

			newAuxiliaryNodeAction.setEnabled(false);
			newConstantNodeAction.setEnabled(false);
			newLevelNodeAction.setEnabled(false);
			newRateNodeAction.setEnabled(false);
			newSourceSinkNodeAction.setEnabled(false);
			newColoredSourceSinkNodeAction.setEnabled(false);
			toggleAddFlowAction.setEnabled(false);
			addFlowModeCheckBoxMenuItem.setState(false);
			changeModelNameAction.setEnabled(false);
			executeModelAction.setEnabled(false);

			zoomStandardAction.setEnabled(false);
			zoomInAction.setEnabled(false);
			zoomOutAction.setEnabled(false);
			cutAction.setEnabled(false);
			copyAction.setEnabled(false);
			pasteAction.setEnabled(false);
			//reset graph number indicator
			GraphNumber.setText("");
		}
	}
	public class ArchiveSubmodelAction extends AbstractAction{
		private static final long serialVersionUID = 1L;
		public ArchiveSubmodelAction(String name, Icon icon, String toolTipText) {
			super(name, icon);

			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}
		public void actionPerformed(ActionEvent e){
			ArrayList<Integer> SubmodelNumbers = new ArrayList<Integer>();
			for(int i=1;i<=graph.size();i++){
				SubmodelNumbers.add(i);
			}
			JFrame frame = new JFrame("InputDialog");
			Object[] choices = SubmodelNumbers.toArray();

			int archiveIndex = (Integer)JOptionPane.showInputDialog(frame,"Which submodel should be archived?","Archive",JOptionPane.PLAIN_MESSAGE,null,choices,choices[0]);
			archiveIndex = archiveIndex-1;
			File file = xmlFile;
			
				int returnVal = fileChooser.showSaveDialog(MainFrame.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					// file was selected and 'OK' was pressed
					file = fileChooser.getSelectedFile();

					// file name should end with ".xml"
					if (!file.getName().toLowerCase().endsWith(".xml")) {
						file = new File(file.getAbsolutePath() + ".xml");
					}

					// check if existing file should be overwritten -> ask for confirmation!
					if (file.exists()) {
						Object[] options = { messages.getString("MainFrame.Yes"), messages.getString("MainFrame.No") };
						int selectedOption = JOptionPane.showOptionDialog(MainFrame.this,
								messages.getString("MainFrame.ConfirmOverwriting.Message"),
								messages.getString("MainFrame.ConfirmOverwriting.Title"),
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE,
								null, // don't use a custom Icon
								options,
								options[1]); // default button title

						if (selectedOption == 1) {
							// do not save
							return;
						}
					}

			}
			try {
				ArrayList<SystemDynamicsGraph> submodelGraph = new ArrayList<SystemDynamicsGraph>();
				submodelGraph.add(graph.get(archiveIndex));
				Color submodelColorsingle = SubmodelColors.get(archiveIndex);
				ArrayList<Color> submodelColor = new ArrayList<Color>();
				submodelColor.add(submodelColorsingle);
				graph.get(archiveIndex).storeToXML(file.getAbsolutePath(), submodelGraph, submodelColor, false, true);

			} catch (AuxiliaryNodesCycleDependencyException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoFormulaException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (NoLevelNodeException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (RateNodeFlowException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UselessNodeException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (XMLModelReaderWriterException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
	}
	private class SaveAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public SaveAction(String name, Icon icon, String toolTipText) {
			super(name, icon);

			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}

		public void actionPerformed(ActionEvent e) {
			File file = xmlFile;
			if (file == null) {
				int returnVal = fileChooser.showSaveDialog(MainFrame.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					// file was selected and 'OK' was pressed
					file = fileChooser.getSelectedFile();

					// file name should end with ".xml"
					if (!file.getName().toLowerCase().endsWith(".xml")) {
						file = new File(file.getAbsolutePath() + ".xml");
					}

					// check if existing file should be overwritten -> ask for confirmation!
					if (file.exists()) {
						Object[] options = { messages.getString("MainFrame.Yes"), messages.getString("MainFrame.No") };
						int selectedOption = JOptionPane.showOptionDialog(MainFrame.this,
								messages.getString("MainFrame.ConfirmOverwriting.Message"),
								messages.getString("MainFrame.ConfirmOverwriting.Title"),
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE,
								null, // don't use a custom Icon
								options,
								options[1]); // default button title

						if (selectedOption == 1) {
							// do not save
							return;
						}
					}
				} else {
					// no file selected
					return;
				}
			}

			try {
				//***************STORETOXML MIGHT NEED TO BE MODIFIED TO TAKE THE ARRAY LIST AND BUILD THE XML FROM THE ARRAYLIST OF GRAPHS INSTEAD!!!******************
				//TODO: 
				//graph.get(0).storeToXML(file.getAbsolutePath, graph);
				//open file and write model tag in here itself

				graph.get(0).storeToXML(file.getAbsolutePath(), graph, SubmodelColors, true, false);


				//				graph.get(0).storeToXML(file.getAbsolutePath());				
				//graph.get(0).storeToXML(file.getAbsolutePath, graph,SubmodelColors.get(0).red, SubmodelColors.get(0).green, SubmodelColors.get(0).blue);

				//graph.get(0).storeToXML(file.getAbsolutePath());				

			} catch (AuxiliaryNodesCycleDependencyException excep) {
				JOptionPane.showMessageDialog(MainFrame.this,
						messages.getString("MainFrame.SaveFile.AuxiliaryNodesCycleDependencyException.Text"),
						messages.getString("MainFrame.SaveFile.Error"),
						JOptionPane.ERROR_MESSAGE);

				return;
			} catch (NoFormulaException excep) {
				JOptionPane.showMessageDialog(MainFrame.this,
						messages.getString("MainFrame.SaveFile.NoFormulaException.Text1") + " '" + excep.getNodeWithourFormula().getNodeName() + "' " + messages.getString("MainFrame.SaveFile.NoFormulaException.Text2"),
						messages.getString("MainFrame.SaveFile.Error"),
						JOptionPane.ERROR_MESSAGE);

				return;
			} catch (NoLevelNodeException excep) {
				JOptionPane.showMessageDialog(MainFrame.this,
						messages.getString("MainFrame.SaveFile.NoLevelNodeException.Text"),
						messages.getString("MainFrame.SaveFile.Error"),
						JOptionPane.ERROR_MESSAGE);

				return;
			} catch (RateNodeFlowException excep) {
				JOptionPane.showMessageDialog(MainFrame.this,
						messages.getString("MainFrame.SaveFile.RateNodeFlowException.Text1") + " '" + excep.getProblematicRateNode().getNodeName() + "' " + messages.getString("MainFrame.SaveFile.RateNodeFlowException.Text2"),
						messages.getString("MainFrame.SaveFile.Error"),
						JOptionPane.ERROR_MESSAGE);

				return;
			} catch (UselessNodeException excep ) {
				if(excep.getUselessNode().getClass().toString().equals("class de.uka.aifb.com.systemDynamics.model.SourceSinkNode")){
					JOptionPane.showMessageDialog(MainFrame.this,
							"Invalid model:" + " A Source/Sink node " + messages.getString("MainFrame.SaveFile.UselessNodeException.Text2"),
							messages.getString("MainFrame.SaveFile.Error"),
							JOptionPane.ERROR_MESSAGE);
					return;

				}else{
					JOptionPane.showMessageDialog(MainFrame.this,
							messages.getString("MainFrame.SaveFile.UselessNodeException.Text1") + " '" + excep.getUselessNode().getNodeName() + "' " + messages.getString("MainFrame.SaveFile.UselessNodeException.Text2"),
							messages.getString("MainFrame.SaveFile.Error"),
							JOptionPane.ERROR_MESSAGE);

					return;
				}			
			} catch (XMLModelReaderWriterException excep) {
				JOptionPane.showMessageDialog(MainFrame.this,
						messages.getString("MainFrame.SaveFile.XMLModelReaderWriterException.Text") + " " + excep.getException().getMessage(),
						messages.getString("MainFrame.SaveFile.Error"),
						JOptionPane.ERROR_MESSAGE);

				return;
			}

			// saving was successful
			xmlFile = file;
			graphModified = false;
			saveAction.setEnabled(false);

			fileName = xmlFile.getAbsolutePath();
			JOptionPane.showMessageDialog(MainFrame.this, "Save successful.", "Save Successful", JOptionPane.INFORMATION_MESSAGE);
			setTitle(createTitle(graph.get(0).getModelName(), graphModified));
			int i=0;
			try {
				for (SystemDynamicsGraph subGraph : graph) {

					if(subGraph.getModel().getRootCount() != 0){
						subGraph.validateModel(i);
					}
					i++;
				}
			} catch (AuxiliaryNodesCycleDependencyException excep) {
				JOptionPane.showMessageDialog(MainFrame.this,
						messages.getString("MainFrame.SaveFile.AuxiliaryNodesCycleDependencyException.Text")  + "Submodel Number: " + Integer.toString(i+1),
						messages.getString("MainFrame.SaveFile.Error"),
						JOptionPane.ERROR_MESSAGE);

				return;
			} catch (NoFormulaException excep) {
				JOptionPane.showMessageDialog(MainFrame.this,
						messages.getString("MainFrame.SaveFile.NoFormulaException.Text1") + " '" + excep.getNodeWithourFormula().getNodeName() + "' " + messages.getString("MainFrame.SaveFile.NoFormulaException.Text2")   + "Submodel Number: " + Integer.toString(i+1),
						messages.getString("MainFrame.SaveFile.Error"),
						JOptionPane.ERROR_MESSAGE);

				return;
			} catch (NoLevelNodeException excep) {
				JOptionPane.showMessageDialog(MainFrame.this,
						messages.getString("MainFrame.SaveFile.NoLevelNodeException.Text")   + "Submodel Number: " + Integer.toString(i+1),
						messages.getString("MainFrame.SaveFile.Error"),
						JOptionPane.ERROR_MESSAGE);

				return;
			} catch (RateNodeFlowException excep) {
				JOptionPane.showMessageDialog(MainFrame.this,
						messages.getString("MainFrame.SaveFile.RateNodeFlowException.Text1") + " '" + excep.getProblematicRateNode().getNodeName() + "' " + messages.getString("MainFrame.SaveFile.RateNodeFlowException.Text2")   + "Submodel Number: " + Integer.toString(i+1),
						messages.getString("MainFrame.SaveFile.Error"),
						JOptionPane.ERROR_MESSAGE);

				return;
			} catch (UselessNodeException excep ) {

				if (excep.getUselessNode() instanceof SourceSinkNode){
					JOptionPane.showMessageDialog(MainFrame.this,
							"Useless node exception caused by a SourceSinkNode. "  + "Submodel Number: " + Integer.toString(i+1),
							messages.getString("MainFrame.SaveFile.Error"),
							JOptionPane.ERROR_MESSAGE);

				}else{
					JOptionPane.showMessageDialog(MainFrame.this,
							messages.getString("MainFrame.SaveFile.UselessNodeException.Text1") + " '" + excep.getUselessNode().getNodeName() + "' " + messages.getString("MainFrame.SaveFile.UselessNodeException.Text2")   + "Submodel Number: " + Integer.toString(i+1),
							messages.getString("MainFrame.SaveFile.Error"),
							JOptionPane.ERROR_MESSAGE);
				}
				return;
			}

		}
	}

	private class SaveAsAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public SaveAsAction(String name) {
			super(name);
		}

		public void actionPerformed(ActionEvent e) {
			File file = null;
			fileChooser.setDialogTitle(messages.getString("MainFrame.MenuBar.File.SaveAs"));
			int returnVal = fileChooser.showSaveDialog(MainFrame.this);
			int selectedOption = 0;
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				// file was selected and 'OK' was pressed
				file = fileChooser.getSelectedFile();

				// file name should end with ".xml"
				if (!file.getName().toLowerCase().endsWith(".xml")) {
					file = new File(file.getAbsolutePath() + ".xml");
				}

				// check if existing file should be overwritten -> ask for confirmation!
				if (file.exists()) {
					PrintWriter writer = null;
					try {
						writer = new PrintWriter(file);
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					writer.print("");
					writer.close();
					Object[] options = { messages.getString("MainFrame.Yes"), messages.getString("MainFrame.No") };
					selectedOption = JOptionPane.showOptionDialog(MainFrame.this,
							messages.getString("MainFrame.ConfirmOverwriting.Message"),
							messages.getString("MainFrame.ConfirmOverwriting.Title"),
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							null, // don't use a custom Icon
							options,
							options[1]); // default button title

					if (selectedOption == 1) {
						// do not save
						return;
					}
				}
			} else {
				// no file selected
				return;
			}

			try {
				//***************STORETOXML MIGHT NEED TO BE MODIFIED TO TAKE THE ARRAY LIST AND BUILD THE XML FROM THE ARRAYLIST OF GRAPHS INSTEAD!!!******************				
				//TODO: 
				//graph.get(0).storeToXML(file.getAbsolutePath(),graph);
				if (selectedOption==1){
					graph.get(0).storeToXML(file.getAbsolutePath(), graph, SubmodelColors, true, false);
				}
				else{
					graph.get(0).storeToXML(file.getAbsolutePath(), graph, SubmodelColors, false, false);
				}

				//				graph.get(0).storeToXML(file.getAbsolutePath());
			} catch (AuxiliaryNodesCycleDependencyException excep) {
				JOptionPane.showMessageDialog(MainFrame.this,
						messages.getString("MainFrame.SaveFile.AuxiliaryNodesCycleDependencyException.Text"),
						messages.getString("MainFrame.SaveFile.Error"),
						JOptionPane.ERROR_MESSAGE);

				return;
			} catch (NoFormulaException excep) {
				JOptionPane.showMessageDialog(MainFrame.this,
						messages.getString("MainFrame.SaveFile.NoFormulaException.Text1") + " '" + excep.getNodeWithourFormula().getNodeName() + "' " + messages.getString("MainFrame.SaveFile.NoFormulaException.Text2"),
						messages.getString("MainFrame.SaveFile.Error"),
						JOptionPane.ERROR_MESSAGE);

				return;
			} catch (NoLevelNodeException excep) {
				JOptionPane.showMessageDialog(MainFrame.this,
						messages.getString("MainFrame.SaveFile.NoLevelNodeException.Text"),
						messages.getString("MainFrame.SaveFile.Error"),
						JOptionPane.ERROR_MESSAGE);

				return;
			} catch (RateNodeFlowException excep) {
				JOptionPane.showMessageDialog(MainFrame.this,
						messages.getString("MainFrame.SaveFile.RateNodeFlowException.Text1") + " '" + excep.getProblematicRateNode().getNodeName() + "' " + messages.getString("MainFrame.SaveFile.RateNodeFlowException.Text2"),
						messages.getString("MainFrame.SaveFile.Error"),
						JOptionPane.ERROR_MESSAGE);

				return;
			} catch (UselessNodeException excep ) {
				//if UselessNodeException is caused by a Source/Sink node, then Source/Sink nodes don't have names, which causes getNodeName() to throw an error.
				//the if statement accounts for this case.

				if(excep.getUselessNode().getClass().toString().equals("class de.uka.aifb.com.systemDynamics.model.SourceSinkNode")){
					JOptionPane.showMessageDialog(MainFrame.this,
							"Invalid model:" + " A Source/Sink node " + messages.getString("MainFrame.SaveFile.UselessNodeException.Text2"),
							messages.getString("MainFrame.SaveFile.Error"),
							JOptionPane.ERROR_MESSAGE);
					return;

				}else{
					JOptionPane.showMessageDialog(MainFrame.this,
							messages.getString("MainFrame.SaveFile.UselessNodeException.Text1") + " '" + excep.getUselessNode().getNodeName() + "' " + messages.getString("MainFrame.SaveFile.UselessNodeException.Text2"),
							messages.getString("MainFrame.SaveFile.Error"),
							JOptionPane.ERROR_MESSAGE);

					return;
				}				
			} catch (XMLModelReaderWriterException excep) {
				JOptionPane.showMessageDialog(MainFrame.this,
						messages.getString("MainFrame.SaveFile.XMLModelReaderWriterException.Text") + " " + excep.getException().getMessage(),
						messages.getString("MainFrame.SaveFile.Error"),
						JOptionPane.ERROR_MESSAGE);

				return;
			} 

			// saving was successful
			xmlFile = file;
			graphModified = false;
			saveAction.setEnabled(false);
			int i=0;
			try {

				for (SystemDynamicsGraph subGraph : graph) {

					if(subGraph.getModel().getRootCount() != 0){
						subGraph.validateModel(i);
					}
					i++;
				}
			} catch (AuxiliaryNodesCycleDependencyException excep) {
				JOptionPane.showMessageDialog(MainFrame.this,
						messages.getString("MainFrame.SaveFile.AuxiliaryNodesCycleDependencyException.Text")   + "Submodel Number: " + Integer.toString(i+1),
						messages.getString("MainFrame.SaveFile.Error"),
						JOptionPane.ERROR_MESSAGE);

				return;
			} catch (NoFormulaException excep) {
				JOptionPane.showMessageDialog(MainFrame.this,
						messages.getString("MainFrame.SaveFile.NoFormulaException.Text1") + " '" + excep.getNodeWithourFormula().getNodeName() + "' " + messages.getString("MainFrame.SaveFile.NoFormulaException.Text2")  + "Submodel Number: " + Integer.toString(i+1),
						messages.getString("MainFrame.SaveFile.Error"),
						JOptionPane.ERROR_MESSAGE);

				return;
			} catch (NoLevelNodeException excep) {
				JOptionPane.showMessageDialog(MainFrame.this,
						messages.getString("MainFrame.SaveFile.NoLevelNodeException.Text")  + "Submodel Number: " + Integer.toString(i+1),
						messages.getString("MainFrame.SaveFile.Error"),
						JOptionPane.ERROR_MESSAGE);

				return;
			} catch (RateNodeFlowException excep) {
				JOptionPane.showMessageDialog(MainFrame.this,
						messages.getString("MainFrame.SaveFile.RateNodeFlowException.Text1") + " '" + excep.getProblematicRateNode().getNodeName() + "' " + messages.getString("MainFrame.SaveFile.RateNodeFlowException.Text2")  + "Submodel Number: " + Integer.toString(i+1),
						messages.getString("MainFrame.SaveFile.Error"),
						JOptionPane.ERROR_MESSAGE);

				return;
			} catch (UselessNodeException excep ) {

				if (excep.getUselessNode() instanceof SourceSinkNode){
					JOptionPane.showMessageDialog(MainFrame.this,
							"Useless node exception caused by a SourceSinkNode. "   + "Submodel Number: " + Integer.toString(i+1),
							messages.getString("MainFrame.SaveFile.Error"),
							JOptionPane.ERROR_MESSAGE);

				}else{
					JOptionPane.showMessageDialog(MainFrame.this,
							messages.getString("MainFrame.SaveFile.UselessNodeException.Text1") + " '" + excep.getUselessNode().getNodeName() + "' " + messages.getString("MainFrame.SaveFile.UselessNodeException.Text2")  + "Submodel Number: " + Integer.toString(i+1),
							messages.getString("MainFrame.SaveFile.Error"),
							JOptionPane.ERROR_MESSAGE);
				}
				return;
			}
			fileName = xmlFile.getAbsolutePath();
			setTitle(createTitle(graph.get(0).getModelName(), graphModified));
		}
	}

	private class NewAuxiliaryNodeAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public NewAuxiliaryNodeAction(String name, Icon icon, String toolTipText) {
			super(name, icon);

			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}

		public void actionPerformed(ActionEvent e) {
			String nodeName =
					NodeNameDialog.showNodeNameDialog(start, MainFrame.this,
							messages.getString("MainFrame.MenuBar.Edit.NewAuxiliaryNode"),
							messages.getString("MainFrame.MenuBar.Edit.NewNodeName"));

			if (nodeName != null) {
				//gets the reference numbers for all submodels and adds them an array to be translated to an object later (in the if portion of the if...else statement that follows).
				ArrayList<Integer> SubmodelNumbers = new ArrayList<Integer>();
				for(int i=1;i<=graph.size();i++){
					SubmodelNumbers.add(i);
				}
				//JFrame LearnerDecidableframe = new JFrame("InputDialog");
				//Object[] LeanerDecidablechoices = {"no","yes"};
				//String LearnerDecidable = (String) JOptionPane.showInputDialog(LearnerDecidableframe,"Should this node be Learner Decidable?","Leaner Decidable?",JOptionPane.PLAIN_MESSAGE,null,LeanerDecidablechoices,LeanerDecidablechoices[0]);
				if(graph.size()==1){
					//if the node is learner decidable, send true.  else, send false.
					graph.get(0).createAuxiliaryNodeGraphCell(nodeName, MainFrame.DEFAULT_COORDINATE, MainFrame.DEFAULT_COORDINATE, false, false);

					
				}
				else{
					//accounts for if user cancels the insert
					try{
						JFrame frame = new JFrame("InputDialog");
						Object[] choices = SubmodelNumbers.toArray();

						int subModelIndex = (Integer)JOptionPane.showInputDialog(frame,"To which submodel?","Add Auxiliary Node",JOptionPane.PLAIN_MESSAGE,null,choices,choices[0]);
					
					
						graph.get(subModelIndex-1).createAuxiliaryNodeGraphCell(nodeName, MainFrame.DEFAULT_COORDINATE, MainFrame.DEFAULT_COORDINATE, false, false);
					
					}catch(Exception ex){

					}
				}

			}

		}
	}

	private class NewConstantNodeAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public NewConstantNodeAction(String name, Icon icon, String toolTipText) {
			super(name, icon);

			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}

		public void actionPerformed(ActionEvent e) {
			NodeNameParameterDialog.NodeNameParameter newNodeNameParameter =
					NodeNameParameterDialog.showNodeNameDialog(start, MainFrame.this,
							messages.getString("MainFrame.MenuBar.Edit.NewConstantNode"),
							messages.getString("MainFrame.MenuBar.Edit.NewNodeName"),
							messages.getString("MainFrame.MenuBar.Edit.NewNodeParameter.ConstantNode"),
							ConstantNode.MIN_CONSTANT,
							ConstantNode.MAX_CONSTANT);

			if (newNodeNameParameter != null) {
				//gets the reference numbers for all submodels and adds them an array to be translated to an object later (in the if portion of the if...else statement that follows).
				ArrayList<Integer> SubmodelNumbers = new ArrayList<Integer>();
				for(int i=1;i<=graph.size();i++){
					SubmodelNumbers.add(i);
				}
				JFrame LearnerDecidableframe = new JFrame("InputDialog");
				Object[] LeanerDecidablechoices = {"no","yes"};
				String LearnerDecidable = (String) JOptionPane.showInputDialog(LearnerDecidableframe,"Should this node be Learner Decidable?","Leaner Decidable?",JOptionPane.PLAIN_MESSAGE,null,LeanerDecidablechoices,LeanerDecidablechoices[0]);
				//lets you insert nodes into a single model that doesn't have submodels.
				if(graph.size()==1){
					if(LearnerDecidable.equals("yes")){
						graph.get(0).createConstantNodeGraphCell(newNodeNameParameter.getNodeName(),
								newNodeNameParameter.getNodeParameter(),
								MainFrame.DEFAULT_COORDINATE, MainFrame.DEFAULT_COORDINATE, true, false);
					}else{
						graph.get(0).createConstantNodeGraphCell(newNodeNameParameter.getNodeName(),
								newNodeNameParameter.getNodeParameter(),
								MainFrame.DEFAULT_COORDINATE, MainFrame.DEFAULT_COORDINATE, false, false);
					}
				}
				else{
					//accounts for if user cancels the insert
					try{

						JFrame frame = new JFrame("InputDialog");
						Object[] choices = SubmodelNumbers.toArray();

						int subModelIndex = (Integer)JOptionPane.showInputDialog(frame,"To which submodel?","Add Constant Node",JOptionPane.PLAIN_MESSAGE,null,choices,choices[0]);
						if(LearnerDecidable.equals("yes")){
							graph.get(subModelIndex-1).createConstantNodeGraphCell(newNodeNameParameter.getNodeName(),
									newNodeNameParameter.getNodeParameter(),
									MainFrame.DEFAULT_COORDINATE, MainFrame.DEFAULT_COORDINATE, true, false);
						}
						else{
							graph.get(subModelIndex-1).createConstantNodeGraphCell(newNodeNameParameter.getNodeName(),
									newNodeNameParameter.getNodeParameter(),
									MainFrame.DEFAULT_COORDINATE, MainFrame.DEFAULT_COORDINATE, false, false);
						}
					}catch(Exception ex){

					}
				}

			}
		}
	}

	private class NewLevelNodeAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public NewLevelNodeAction(String name, Icon icon, String toolTipText) {
			super(name, icon);

			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}

		public void actionPerformed(ActionEvent e) {
			NodeNameParameterDialogLN.NodeNameParameterLN newNodeNameParameter =
					NodeNameParameterDialogLN.showNodeNameDialog(start, MainFrame.this,
							messages.getString("MainFrame.MenuBar.Edit.NewLevelNode"),
							messages.getString("MainFrame.MenuBar.Edit.NewNodeName"),
							messages.getString("MainFrame.MenuBar.Edit.NewNodeParameter.LevelNode"),
							"Minimum Value",
							"Maximum Value",
							"Non-Decreasing",
							"Non-Increasing",
							"NULL",
							LevelNode.MIN_START_VALUE,
							LevelNode.MAX_START_VALUE);
			if (newNodeNameParameter != null) {
				//gets the reference numbers for all submodels and adds them an array to be translated to an object later (in the if portion of the if...else statement that follows).
				ArrayList<Integer> SubmodelNumbers = new ArrayList<Integer>();
				for(int i=1;i<=graph.size();i++){
					SubmodelNumbers.add(i);
				}
				JFrame LearnerDecidableframe = new JFrame("InputDialog");
				Object[] LeanerDecidablechoices = {"no","yes"};
				String LearnerDecidable = (String) JOptionPane.showInputDialog(LearnerDecidableframe,"Should this node be Learner Decidable?","Leaner Decidable?",JOptionPane.PLAIN_MESSAGE,null,LeanerDecidablechoices,LeanerDecidablechoices[0]);
				
				//lets you insert nodes into a single model that doesn't have submodels.
				if(graph.size()==1){
					if(LearnerDecidable.equals("yes")){
						graph.get(0).createLevelNodeGraphCell(newNodeNameParameter.getNodeName(),
								newNodeNameParameter.getNodeParameter(),
								newNodeNameParameter.getMinParameter(),
								newNodeNameParameter.getMaxParameter(),
								newNodeNameParameter.getCurveParameter(),
								MainFrame.DEFAULT_COORDINATE, MainFrame.DEFAULT_COORDINATE, true, false);
					}
					else{
						graph.get(0).createLevelNodeGraphCell(newNodeNameParameter.getNodeName(),
								newNodeNameParameter.getNodeParameter(),
								newNodeNameParameter.getMinParameter(),
								newNodeNameParameter.getMaxParameter(),
								newNodeNameParameter.getCurveParameter(),
								MainFrame.DEFAULT_COORDINATE, MainFrame.DEFAULT_COORDINATE, false, false);
					}
				}
				else{
					//accounts for if user cancels the insert
					try{
						JFrame frame = new JFrame("InputDialog");
						Object[] choices = SubmodelNumbers.toArray();

						int subModelIndex = (Integer)JOptionPane.showInputDialog(frame,"To which submodel?","Add Constant Node",JOptionPane.PLAIN_MESSAGE,null,choices,choices[0]);
						if(LearnerDecidable.equals("yes")){
							graph.get(subModelIndex - 1).createLevelNodeGraphCell(newNodeNameParameter.getNodeName(),
									newNodeNameParameter.getNodeParameter(),
									newNodeNameParameter.getMinParameter(),
									newNodeNameParameter.getMaxParameter(),
									newNodeNameParameter.getCurveParameter(),
									MainFrame.DEFAULT_COORDINATE, MainFrame.DEFAULT_COORDINATE, true, false);
						}
						else{
							graph.get(subModelIndex - 1).createLevelNodeGraphCell(newNodeNameParameter.getNodeName(),
									newNodeNameParameter.getNodeParameter(),
									newNodeNameParameter.getMinParameter(),
									newNodeNameParameter.getMaxParameter(),
									newNodeNameParameter.getCurveParameter(),
									MainFrame.DEFAULT_COORDINATE, MainFrame.DEFAULT_COORDINATE, false, false);
						}

					}catch(Exception ex){

					}
				}


			}
		}
	}

	private class NewRateNodeAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public NewRateNodeAction(String name, Icon icon, String toolTipText) {
			super(name, icon);

			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}

		public void actionPerformed(ActionEvent e) {
			String nodeName =
					NodeNameDialog.showNodeNameDialog(start, MainFrame.this,
							messages.getString("MainFrame.MenuBar.Edit.NewRateNode"),
							messages.getString("MainFrame.MenuBar.Edit.NewNodeName"));

			if (nodeName != null) {
				//gets the reference numbers for all submodels and adds them an array to be translated to an object later (in the if portion of the if...else statement that follows).
				ArrayList<Integer> SubmodelNumbers = new ArrayList<Integer>();
				for(int i=1;i<=graph.size();i++){
					SubmodelNumbers.add(i);
				}
				//JFrame LearnerDecidableframe = new JFrame("InputDialog");
				//Object[] LeanerDecidablechoices = {"no","yes"};
				//String LearnerDecidable = (String) JOptionPane.showInputDialog(LearnerDecidableframe,"Should this node be Learner Decidable?","Leaner Decidable?",JOptionPane.PLAIN_MESSAGE,null,LeanerDecidablechoices,LeanerDecidablechoices[0]);

				//lets you insert nodes into a single model that doesn't have submodels.
				if(graph.size()==1){
					
						graph.get(0).createRateNodeGraphCell(nodeName, MainFrame.DEFAULT_COORDINATE, MainFrame.DEFAULT_COORDINATE, false, false);
					

				}
				else{
					//accounts for if user cancels the insert
					try{
						JFrame frame = new JFrame("InputDialog");
						Object[] choices = SubmodelNumbers.toArray();

						int subModelIndex = (Integer)JOptionPane.showInputDialog(frame,"To which submodel?","Add Constant Node",JOptionPane.PLAIN_MESSAGE,null,choices,choices[0]);
						
							graph.get(subModelIndex - 1).createRateNodeGraphCell(nodeName, MainFrame.DEFAULT_COORDINATE, MainFrame.DEFAULT_COORDINATE, false, false);
						

					}catch(Exception ex){

					}
				}

			}
		}
	}

	private class NewSourceSinkNodeAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public NewSourceSinkNodeAction(String name, Icon icon, String toolTipText) {
			super(name, icon);

			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}

		public void actionPerformed(ActionEvent e) {
			//gets the reference numbers for all submodels and adds them an array to be translated to an object later (in the if portion of the if...else statement that follows).
			ArrayList<Integer> SubmodelNumbers = new ArrayList<Integer>();
			for(int i=1;i<=graph.size();i++){
				SubmodelNumbers.add(i);
			}

			if(graph.size()==1){
				graph.get(0).createSourceSinkNodeGraphCell(MainFrame.DEFAULT_COORDINATE, MainFrame.DEFAULT_COORDINATE, false, "SN");
			}
			else{
				//accounts for if user cancels the insert
				try{
					JFrame frame = new JFrame("InputDialog");
					Object[] choices = SubmodelNumbers.toArray();
					int subModelIndex = (Integer)JOptionPane.showInputDialog(frame,"To which submodel?","Add SourceSink Node",JOptionPane.PLAIN_MESSAGE,null,choices,choices[0]);
					graph.get(subModelIndex-1).createSourceSinkNodeGraphCell(MainFrame.DEFAULT_COORDINATE, MainFrame.DEFAULT_COORDINATE, false, "SN");
				}catch(Exception ex){

				}
			}
		}
	}
	private class NewColoredSourceSinkNodeAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public NewColoredSourceSinkNodeAction(String name, Icon icon, String toolTipText) {
			super(name, icon);

			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}

		public void actionPerformed(ActionEvent e) {
			//gets the reference numbers for all submodels and adds them an array to be translated to an object later (in the if portion of the if...else statement that follows).
			ArrayList<Integer> SubmodelNumbers = new ArrayList<Integer>();
			for(int i=1;i<=graph.size();i++){
				SubmodelNumbers.add(i);
			}

			if(graph.size()==1){
				JOptionPane.showMessageDialog(null, "Colored SourceSink Nodes can only be added with two or more Submodels");
			}
			else{
				//accounts for if user cancels the insert
				try{
					JFrame frame = new JFrame("InputDialog");
					Object[] choices = SubmodelNumbers.toArray();
					int subModelIndex = (Integer)JOptionPane.showInputDialog(frame,"To which submodel?","Add SourceSink Node",JOptionPane.PLAIN_MESSAGE,null,choices,choices[0]);
					int colorIndex = (Integer)JOptionPane.showInputDialog(frame,"Link the current submodel to which Submodel?","Link to A Submodel",JOptionPane.PLAIN_MESSAGE,null,choices,choices[0]);
					//create colored sourcesink, passing the color and the index of the graph it will link to
					graph.get(subModelIndex-1).createColoredSourceSinkNodeGraphCell(MainFrame.DEFAULT_COORDINATE, MainFrame.DEFAULT_COORDINATE, SubmodelColors.get(colorIndex-1), colorIndex);
				}catch(Exception ex){

				}
			}
		}
	}

	private class ToggleAddFlowAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		private boolean inAddFlowMode;
		private int subModelIndex = 0;

		public ToggleAddFlowAction(String name, Icon icon, String toolTipText) {
			super(name, icon);

			putValue(Action.SHORT_DESCRIPTION, toolTipText);

			inAddFlowMode = false;
		}

		public boolean isInAddFlowMode() {
			return inAddFlowMode;
		}

		public void actionPerformed(ActionEvent e) {
			// toggle ports visibility

			ArrayList<Integer> SubmodelNumbers = new ArrayList<Integer>();
			for(int i=1;i<=graph.size();i++){
				SubmodelNumbers.add(i);
			}
			JFrame frame = new JFrame("InputDialog");
			Object[] choices = SubmodelNumbers.toArray();
			if (subModelIndex == 0){
				subModelIndex = (Integer)JOptionPane.showInputDialog(frame,"Enter Add Flow mode for which submodel?","Enter Add Flow mode",JOptionPane.PLAIN_MESSAGE,null,choices,choices[0]);
			}

			graph.get(subModelIndex - 1).setPortsVisible(!graph.get(subModelIndex - 1).isPortsVisible());
			inAddFlowMode = !inAddFlowMode;
			if (inAddFlowMode) {
				putValue(Action.SMALL_ICON, new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_LEAVE_ADD_FLOW_MODE_ICON)));
				putValue(Action.NAME, messages.getString("MainFrame.MenuBar.Edit.ToggleAddFlowMode.LeaveAddFlowMode"));
				putValue(Action.SHORT_DESCRIPTION, messages.getString("MainFrame.MenuBar.Edit.ToggleAddFlowMode.LeaveAddFlowMode"));
				addFlowModeCheckBoxMenuItem.setToolTipText(null);
				contentPanel.revalidate();
				contentPanel.repaint();
				modelPanel.revalidate();
				modelPanel.repaint();
			} else {
				// not in "add flow modus"
				subModelIndex = 0;
				contentPanel.remove(modelPanel);
				contentPanel.add(modelPanel);
				contentPanel.revalidate();
				putValue(Action.SMALL_ICON, new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_ENTER_ADD_FLOW_MODE_ICON)));
				putValue(Action.NAME, messages.getString("MainFrame.MenuBar.Edit.ToggleAddFlowMode.EnterAddFlowMode"));
				putValue(Action.SHORT_DESCRIPTION, messages.getString("MainFrame.MenuBar.Edit.ToggleAddFlowMode.EnterAddFlowMode"));
				addFlowModeCheckBoxMenuItem.setToolTipText(null);
			}
			modelPanel.revalidate();
			modelPanel.repaint();
			contentPanel.revalidate();
			contentPanel.repaint();

		}
	}

	private class ChangeModelNameAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ChangeModelNameAction(String name, Icon icon, String toolTipText) {
			super(name, icon);

			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}
		public void actionPerformed(ActionEvent e) {
			String newModelName =
					ModelNameDialog.showModelNameDialog(start, MainFrame.this,
							messages.getString("MainFrame.MenuBar.Edit.ChangeModelName"),
							messages.getString("MainFrame.MenuBar.Edit.NewModelName"),
							graph.get(0).getModelName());

			if (newModelName != null) {
				DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z");
				Date today = Calendar.getInstance().getTime();
				String modelDate = df.format(today);
				newModelName += " ";
				newModelName += modelDate;

				graph.get(0).setModelName(newModelName);

				setTitle(createTitle(graph.get(0).getModelName(), graphModified));
			}
		}
	}

	private class ExecuteModelAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ExecuteModelAction(String name, Icon icon, String toolTipText) {
			super(name, icon);

			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}

		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(null, "Please Save your file before entering the Parameter Tuner");
			saveAsAction.actionPerformed(null);
			int i=0;
			try {
				//**********************************************************TODO:***********************************************************
				//graph.get(0).validateModelAndSetUnchangeable(graph);
				//graph.get(0).validateModelAndSetUnchangeable();

				for (SystemDynamicsGraph subGraph : graph) {
					if(subGraph.getModel().getRootCount() != 0){
						//subGraph.validateModelAndSetUnchangeable(i);
						subGraph.validateModel(i);
					}
					i++;
				}
			} catch (AuxiliaryNodesCycleDependencyException excep) {
				JOptionPane.showMessageDialog(MainFrame.this,
						messages.getString("MainFrame.MenuBar.Edit.ExecuteModel.AuxiliaryNodesCycleDependencyException.Text")  + "Submodel Number: " + Integer.toString(i+1),
						messages.getString("MainFrame.MenuBar.Edit.ExecuteModel.Error"),
						JOptionPane.ERROR_MESSAGE);

				return;
			} catch (NoFormulaException excep) {
				JOptionPane.showMessageDialog(MainFrame.this,
						messages.getString("MainFrame.MenuBar.Edit.ExecuteModel.NoFormulaException.Text1") + " '" + excep.getNodeWithourFormula().getNodeName() + "' " + messages.getString("MainFrame.SaveFile.NoFormulaException.Text2")  + "Submodel Number: " + Integer.toString(i+1),
						messages.getString("MainFrame.MenuBar.Edit.ExecuteModel.Error"),
						JOptionPane.ERROR_MESSAGE);

				return;
			} catch (NoLevelNodeException excep) {
				JOptionPane.showMessageDialog(MainFrame.this,
						messages.getString("MainFrame.MenuBar.Edit.ExecuteModel.NoLevelNodeException.Text")  + "Submodel Number: " + Integer.toString(i+1),
						messages.getString("MainFrame.MenuBar.Edit.ExecuteModel.Error"),
						JOptionPane.ERROR_MESSAGE);

				return;
			} catch (RateNodeFlowException excep) {
				JOptionPane.showMessageDialog(MainFrame.this,
						messages.getString("MainFrame.MenuBar.Edit.ExecuteModel.RateNodeFlowException.Text1") + " '" + excep.getProblematicRateNode().getNodeName() + "' " + messages.getString("MainFrame.SaveFile.RateNodeFlowException.Text2")  + "Submodel Number: " + Integer.toString(i+1),
						messages.getString("MainFrame.MenuBar.Edit.ExecuteModel.Error"),
						JOptionPane.ERROR_MESSAGE);

				return;
			} catch (UselessNodeException excep ) {
				if (excep.getUselessNode() instanceof SourceSinkNode) {
					JOptionPane.showMessageDialog(MainFrame.this,
							messages.getString("MainFrame.MenuBar.Edit.ExecuteModel.UselessNodeException.SourceSinkNodeText")  + "Submodel Number: " + Integer.toString(i+1),
							messages.getString("MainFrame.MenuBar.Edit.ExecuteModel.Error"),
							JOptionPane.ERROR_MESSAGE);
				} else { 
					JOptionPane.showMessageDialog(MainFrame.this,
							messages.getString("MainFrame.MenuBar.Edit.ExecuteModel.UselessNodeException.Text1") + " '" + excep.getUselessNode().getNodeName() + "' " + messages.getString("MainFrame.SaveFile.UselessNodeException.Text2")  + "Submodel Number: " + Integer.toString(i+1),
							messages.getString("MainFrame.MenuBar.Edit.ExecuteModel.Error"),
							JOptionPane.ERROR_MESSAGE);
				}

				return;
			}

			// successful
			newAuxiliaryNodeAction.setEnabled(false);
			newConstantNodeAction.setEnabled(false);
			newLevelNodeAction.setEnabled(false);
			newRateNodeAction.setEnabled(false);
			newSourceSinkNodeAction.setEnabled(false);
			newColoredSourceSinkNodeAction.setEnabled(false);
			toggleAddFlowAction.setEnabled(false);
			changeModelNameAction.setEnabled(false);
			executeModelAction.setEnabled(false);

			tabbedPane = new JTabbedPane();
			tabbedPane.addTab(messages.getString("MainFrame.TabbedPane.Model"), modelPanel);
			final ArrayList<ModelExecutionChartPanel> chartPanelAllSubmodels = new ArrayList<ModelExecutionChartPanel>();
			ArrayList<ExportPanel> exportPanelAllSubmodels = new ArrayList<ExportPanel>();
			
			
			
			for(int j=0;j<graph.size();j++){
				
				ModelExecutionChartPanel chartPanelIndividual = graph.get(j).getChartPanel(j+1);
				ExportPanel exportPanel = graph.get(j).getExportPanel();	
				chartPanelAllSubmodels.add(chartPanelIndividual);
				exportPanelAllSubmodels.add(exportPanel);
				graph.get(j).setExecutionChartPanel(chartPanelIndividual);
			}
			JPanel overallExecutePanel = new JPanel();
			
			final JTextField roundsTextModel = new JTextField("1");
			final JLabel roundsLabel = new JLabel("Number of Rounds to execute");
			roundsTextModel.setPreferredSize(new Dimension(200,20));
			JButton ResetButton = new JButton ("Reset");
			ResetButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
						//reset();
					for(int i=0;i<chartPanelAllSubmodels.size();i++){
						chartPanelAllSubmodels.get(i).reset();
					}
						
				}
			});
			JButton ModelExecButton = new JButton("Execute Model");
			 ModelExecButton.addActionListener(new ActionListener() {
		         public void actionPerformed(ActionEvent e) {
		        	 
		        	 executeInOrder(chartPanelAllSubmodels);
		         }
		         
		         private void executeInOrder(ArrayList<ModelExecutionChartPanel> chartPanelAllSubmodels){
		        	 ArrayList<ModelExecutionChartPanel> orderedChartPanels = new ArrayList<ModelExecutionChartPanel>();
		        	 HashMap<ModelExecutionChartPanel, HashSet<ModelExecutionChartPanel>> adjacentList = getAdjacentListOfModelExecutionChartPanel(chartPanelAllSubmodels);
		        	 HashMap<ModelExecutionChartPanel, Integer> numberOfPredecessorsMap = getNumberOfPredecessorsMap(chartPanelAllSubmodels);
		        	      
		        	      while (!numberOfPredecessorsMap.isEmpty()) {
		        	         for (ModelExecutionChartPanel ecp : numberOfPredecessorsMap.keySet()) {
		        	            if (numberOfPredecessorsMap.get(ecp) == 0) {
		        	            	orderedChartPanels.add(ecp);
		        
		        	               HashSet<ModelExecutionChartPanel> dependantModelExecutionChartPanel = adjacentList.get(ecp);
		        	               if (dependantModelExecutionChartPanel != null) {
		        	                  for (ModelExecutionChartPanel chartPanel : dependantModelExecutionChartPanel) {
		        	                     int numberOfPredecessors = numberOfPredecessorsMap.get(chartPanel);
		        	                     numberOfPredecessors--;
		        	                     numberOfPredecessorsMap.put(chartPanel, numberOfPredecessors);
		        	                  }
		        	               }
		        	               
		        	               numberOfPredecessorsMap.remove(ecp);
		        	               
		        	               break;
		        	            }
		        	         }
		        	      }
		        	      
		        	 
		        	 for(int i=0;i<orderedChartPanels.size();i++){
		        		 JPanel roundsPanel;
		        		try {
		        		roundsPanel = (JPanel)orderedChartPanels.get(i).getComponent(3);
		        		}
		        		catch(Exception e2) {
		        			roundsPanel = (JPanel)orderedChartPanels.get(i).getComponent(2);
		        		}
		 				JTextField roundsText = (JTextField)roundsPanel.getComponent(1);		 				
		 				roundsText.setText(roundsTextModel.getText());
		 				
		 				orderedChartPanels.get(i).getExecutionButton().doClick();
		        	 }
		         }

				private HashMap<ModelExecutionChartPanel, Integer> getNumberOfPredecessorsMap(ArrayList<ModelExecutionChartPanel> chartPanelAllSubmodels) {
					HashMap<ModelExecutionChartPanel, Integer> map = new HashMap<ModelExecutionChartPanel, Integer>();
					
					for (ModelExecutionChartPanel chartPanel : chartPanelAllSubmodels) {
				    	  
				    	  HashSet<ModelExecutionChartPanel> allChartPanelsThisDependsOn= new HashSet<ModelExecutionChartPanel>();
				    	  
				    	  for(SharedNode sn: chartPanel.getModel().getSharedNodes()){
				    		  for(int i=0;i<chartPanelAllSubmodels.size();i++){
				    				for(AuxiliaryNode an: chartPanelAllSubmodels.get(i).getModel().getAuxiliaryNodes()){
				    		 	  	  if(sn.getSource() == an){
				    		 	  		allChartPanelsThisDependsOn.add(chartPanelAllSubmodels.get(i));
				    		 	  	  }
				    		 	  	  break;
				    				 }
				    				for(LevelNode ln: chartPanelAllSubmodels.get(i).getModel().getLevelNodes()){
					    		 	  if(sn.getSource() == ln){
					    		 	  		allChartPanelsThisDependsOn.add(chartPanelAllSubmodels.get(i));
					    		 	  }
					    		 	  break;
				    				}
				    		  }
				    	  }
				    	  
				    	  map.put(chartPanel, allChartPanelsThisDependsOn.size());
				      }
					
					return map;
				}

				private HashMap<ModelExecutionChartPanel, HashSet<ModelExecutionChartPanel>> getAdjacentListOfModelExecutionChartPanel(ArrayList<ModelExecutionChartPanel> chartPanelAllSubmodels) {
					HashMap<ModelExecutionChartPanel, HashSet<ModelExecutionChartPanel>> adjacentList =
					         new HashMap<ModelExecutionChartPanel, HashSet<ModelExecutionChartPanel>>();
					      
					      for (ModelExecutionChartPanel chartPanel : chartPanelAllSubmodels) {
					    	  HashSet<ModelExecutionChartPanel> allChartPanelsDependOnThis= new HashSet<ModelExecutionChartPanel>();
					    	  
					    	  for(int i=0;i<chartPanelAllSubmodels.size();i++){	
					    		  for(SharedNode sn: chartPanelAllSubmodels.get(i).getModel().getSharedNodes()){
					    				for(AuxiliaryNode an: chartPanel.getModel().getAuxiliaryNodes()){
					    		 	  	  if(sn.getSource() == an){
					    		 	  		allChartPanelsDependOnThis.add(chartPanelAllSubmodels.get(i));
					    		 	  	  }
					    		 	  	  break;
					    				 }
					    				for(LevelNode ln: chartPanel.getModel().getLevelNodes()){
						    		 	  if(sn.getSource() == ln){
						    		 	  	allChartPanelsDependOnThis.add(chartPanelAllSubmodels.get(i));
						    		 	  }
						    		 	  break;
					    				}
					    		  }
					    	  }
					    	  
					    	  adjacentList.put(chartPanel, allChartPanelsDependOnThis);
					      }
					return adjacentList;
				}
			 });
			 		
		      
			 overallExecutePanel.add(roundsLabel);
			 overallExecutePanel.add(roundsTextModel);
			 overallExecutePanel.add(ModelExecButton);
			 overallExecutePanel.add(ResetButton);
			 tabbedPane.addTab("Model Execute", overallExecutePanel); 
			for(int k=0;k<chartPanelAllSubmodels.size();k++){
				chartPanelAllSubmodels.get(k).getExecutionButton().setVisible(false);
				JPanel roundsPanel;
				//accounts for whether there are shared nodes or not.... If there are not shared nodes,
				//then the component at index 2 is the graph instead of the component at index 3.
				try {
					roundsPanel = (JPanel)chartPanelAllSubmodels.get(k).getComponent(3);
				}
				catch(Exception e2) {
					roundsPanel = (JPanel)chartPanelAllSubmodels.get(k).getComponent(2);
				}
				JTextField roundsText = (JTextField)roundsPanel.getComponent(1);
				roundsText.setVisible(false);
			}
			
			
			
			for(int j=0;j<graph.size();j++){
				tabbedPane.addTab(messages.getString("MainFrame.TabbedPane.Chart") + " SM"+(j+1), chartPanelAllSubmodels.get(j));
				tabbedPane.addTab(messages.getString("MainFrame.TabbedPane.Export")+ " SM"+(j+1), exportPanelAllSubmodels.get(j));
	
			}
			tabbedPane.addChangeListener(MainFrame.this);
			tabbedPane.setSelectedIndex(1);

			//MainFrame.this.getRootPane().setDefaultButton(chartPanelAllSubmodels.get(0).getExecutionButton());

			contentPanel.removeAll();
			contentPanel.add(tabbedPane);

			MainFrame.this.getContentPane().validate();
			exitExecuteModelAction.setEnabled(true);
		}
		public void reset(){
			JOptionPane.showMessageDialog(null, "Please reopen your file to complete the reset.");
			openAction.actionPerformed(null);
			actionPerformed(null);
		}
		
	}

	private class exitExecuteModelAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public exitExecuteModelAction(String name, Icon icon, String toolTipText) {
			super(name, icon);

			putValue(Action.SHORT_DESCRIPTION, "Return to model view");
		}

		public void actionPerformed(ActionEvent e) {			

			//reset everything.  Remove the tabbedPane and scrollPane
			newAuxiliaryNodeAction.setEnabled(true);
			newConstantNodeAction.setEnabled(true);
			newLevelNodeAction.setEnabled(true);
			newRateNodeAction.setEnabled(true);
			newSourceSinkNodeAction.setEnabled(true);
			newColoredSourceSinkNodeAction.setEnabled(true);
			toggleAddFlowAction.setEnabled(true);
			changeModelNameAction.setEnabled(true);
			executeModelAction.setEnabled(true);
			
			contentPanel.remove(tabbedPane);
			contentPanel.add(modelPanel);
			contentPanel.repaint();
			exitExecuteModelAction.setEnabled(false);

			MainFrame.this.getContentPane().validate();
		}
	}

	private class ZoomStandardAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ZoomStandardAction(String name, Icon icon, String toolTipText) {
			super(name, icon);

			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}

		public void actionPerformed(ActionEvent e) {
			for(int i=0;i<graph.size();i++){
				graph.get(i).setScale(1.0);
			}

			setTitle(createTitle(graph.get(0).getModelName(), graphModified));
		}
	}

	private class ZoomInAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ZoomInAction(String name, Icon icon, String toolTipText) {
			super(name, icon);

			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}

		public void actionPerformed(ActionEvent e) {
			for(int i=0;i<graph.size();i++){
				graph.get(i).setScale(1.3 * graph.get(i).getScale());
			}
			setTitle(createTitle(graph.get(0).getModelName(), graphModified));
		}
	}

	private class ZoomOutAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ZoomOutAction(String name, Icon icon, String toolTipText) {
			super(name, icon);

			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}

		public void actionPerformed(ActionEvent e) {
			for(int i=0;i<graph.size();i++){
				graph.get(i).setScale(graph.get(i).getScale() / 1.3);	
			}


			setTitle(createTitle(graph.get(0).getModelName(), graphModified));
		}
	}
	private class CutAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		protected Action action;
		private CutAction(String name, Icon icon, String toolTipText, Action a){
			super(name, icon);
			putValue(Action.SHORT_DESCRIPTION, toolTipText);
			this.action = a;
		}
		public void actionPerformed(ActionEvent e){

			Object[] cells = {};
			int breakVal = 0;
			for(int j=0;j<graph.size();j++){
				if(cells.length == 0){
					cells = graph.get(j).getSelectionCells();
					breakVal = j;

				}


			}

			graph.get(breakVal).getModel().remove(cells);



			File fout = new File("Clipboard.txt");
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(fout);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			try{
				for(int i=0; i<cells.length;i++){
					//for each graph cell, get the appropriate attributes from that cell's attribute map.  then, write all the attributes
					//to the clipboard as one line with each attribute delimited by a comma (helps when pasteAction reads the values in from the 
					//clipboard).
					if (cells[i] instanceof AuxiliaryNodeGraphCell) {

						String name = ((AuxiliaryNodeGraphCell)cells[i]).getAttributes().get("name").toString();					

						bw.write("Auxiliary," + name);
						bw.newLine();
					}
					else if (cells[i] instanceof LevelNodeGraphCell){
						String name = ((LevelNodeGraphCell)cells[i]).getAttributes().get("name").toString();
						String startVal = ((LevelNodeGraphCell)cells[i]).getAttributes().get("startVal").toString();
						String minVal = ((LevelNodeGraphCell)cells[i]).getAttributes().get("minVal").toString();
						String maxVal = ((LevelNodeGraphCell)cells[i]).getAttributes().get("maxVal").toString();
						String curve = ((LevelNodeGraphCell)cells[i]).getAttributes().get("curve").toString();

						bw.write("Level," + name + "," + startVal + "," + minVal + "," + maxVal + "," + curve);
						bw.newLine();

					}
					else if(cells[i] instanceof SourceSinkNodeGraphCell){					
						bw.write("SourceSink");
						bw.newLine();
					}
					else if(cells[i] instanceof ColoredSourceSinkNodeGraphCell){					

						String colorIndex = ((ColoredSourceSinkNodeGraphCell)cells[i]).getAttributes().get("colorIndex").toString();
						String[] color = ((ColoredSourceSinkNodeGraphCell)cells[i]).getAttributes().get("color").toString().split(", ");

						bw.write("coloredSourceSink," + colorIndex + "," + color[0] + "," + color[1] + "," + color[2]);
						bw.newLine();
					}
					else if(cells[i] instanceof ConstantNodeGraphCell){
						String name = ((ConstantNodeGraphCell)cells[i]).getAttributes().get("name").toString();
						String values = ((ConstantNodeGraphCell)cells[i]).getAttributes().get("constval").toString();
						bw.write("Constant,"+name+","+values);
						bw.newLine();
					}
					else if(cells[i] instanceof RateNodeGraphCell){
						String name = ((RateNodeGraphCell)cells[i]).getAttributes().get("name").toString();
						bw.write("Rate,"+name);
					}
					else if(cells[i] instanceof FlowEdge){
						FlowEdge edge = (FlowEdge)cells[i];
						final Object edgeSource = graph.get(0).getModel().getParent(graph.get(0).getModel().getSource(edge));
						final Object edgeTarget = graph.get(0).getModel().getParent(graph.get(0).getModel().getTarget(edge));
						if (edgeSource instanceof SourceSinkNodeGraphCell && edgeTarget instanceof RateNodeGraphCell) {
							graph.get(breakVal).removeFlow((SourceSinkNodeGraphCell)edgeSource, (RateNodeGraphCell)edgeTarget);
						}
						if (edgeSource instanceof RateNodeGraphCell && edgeTarget instanceof LevelNodeGraphCell) {
							graph.get(breakVal).removeFlow((RateNodeGraphCell)edgeSource, (LevelNodeGraphCell)edgeTarget);
						}
						if (edgeSource instanceof LevelNodeGraphCell && edgeTarget instanceof RateNodeGraphCell) {
							graph.get(breakVal).removeFlow((LevelNodeGraphCell)edgeSource, (RateNodeGraphCell)edgeTarget);
						}
						if (edgeSource instanceof RateNodeGraphCell && edgeTarget instanceof SourceSinkNodeGraphCell) {
							graph.get(breakVal).removeFlow((RateNodeGraphCell)edgeSource, (SourceSinkNodeGraphCell)edgeTarget);
						}
					}
				}
			}catch(IOException e1){

			}
			try {
				bw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}


		}

	}
	private class CopyAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		protected Action action;
		private CopyAction(String name, Icon icon, String toolTipText, Action a){
			super(name, icon);
			putValue(Action.SHORT_DESCRIPTION, toolTipText);
			this.action = a;
		}
		//same code as CutAction, but doesn't remove any graph cells.
		public void actionPerformed(ActionEvent e){
			Object[] cells = {};

			for(int j=0;j<graph.size();j++){
				if(cells.length == 0){
					cells = graph.get(j).getSelectionCells();

				}


			}

			File fout = new File("Clipboard.txt");
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(fout);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
			try{
				for(int i=0; i<cells.length;i++){
					if (cells[i] instanceof AuxiliaryNodeGraphCell) {
						String name = ((AuxiliaryNodeGraphCell)cells[i]).getAttributes().get("name").toString();					

						bw.write("Auxiliary," + name);
						bw.newLine();
					}
					else if (cells[i] instanceof LevelNodeGraphCell){
						String name = ((LevelNodeGraphCell)cells[i]).getAttributes().get("name").toString();
						String startVal = ((LevelNodeGraphCell)cells[i]).getAttributes().get("startVal").toString();
						String minVal = ((LevelNodeGraphCell)cells[i]).getAttributes().get("minVal").toString();
						String maxVal = ((LevelNodeGraphCell)cells[i]).getAttributes().get("maxVal").toString();
						String curve = ((LevelNodeGraphCell)cells[i]).getAttributes().get("curve").toString();

						bw.write("Level," + name + "," + startVal + "," + minVal + "," + maxVal + "," + curve);
						bw.newLine();

					}
					else if(cells[i] instanceof SourceSinkNodeGraphCell){					
						bw.write("SourceSink");
						bw.newLine();
					}
					else if(cells[i] instanceof ColoredSourceSinkNodeGraphCell){					

						String colorIndex = ((ColoredSourceSinkNodeGraphCell)cells[i]).getAttributes().get("colorIndex").toString();
						String[] color = ((ColoredSourceSinkNodeGraphCell)cells[i]).getAttributes().get("color").toString().split(", ");

						bw.write("coloredSourceSink," + colorIndex + "," + color[0] + "," + color[1] + "," + color[2]);
						bw.newLine();
					}
					else if(cells[i] instanceof ConstantNodeGraphCell){
						String name = ((ConstantNodeGraphCell)cells[i]).getAttributes().get("name").toString();
						String values = ((ConstantNodeGraphCell)cells[i]).getAttributes().get("constval").toString();
						bw.write("Constant,"+name+","+values);
						bw.newLine();
					}
					else if(cells[i] instanceof RateNodeGraphCell){
						String name = ((RateNodeGraphCell)cells[i]).getAttributes().get("name").toString();
						bw.write("Rate,"+name);
					}
				}
			}catch(IOException e1){

			}
			try {
				bw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}
	private class PasteAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		protected Action action;
		private PasteAction(String name, Icon icon, String toolTipText, Action a){
			super(name, icon);
			putValue(Action.SHORT_DESCRIPTION, toolTipText);
			this.action = a;
		}
		public void actionPerformed(ActionEvent e){
			FileInputStream fis = null;
			BufferedReader br = null;
			ArrayList<String> pasteCells = new ArrayList<String>(); 
			try {
				fis = new FileInputStream("Clipboard.txt");
				br = new BufferedReader(new InputStreamReader(fis));
				String line = null;
				while((line = br.readLine()) != null){
					pasteCells.add(line);
				}
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			ArrayList<Integer> SubmodelNumbers = new ArrayList<Integer>();
			for(int i=1;i<=graph.size();i++){
				SubmodelNumbers.add(i);
			}
			JFrame frame = new JFrame("InputDialog");
			Object[] choices = SubmodelNumbers.toArray();
			int subModelIndex = (Integer)JOptionPane.showInputDialog(frame,"Paste into which submodel?","Paste",JOptionPane.PLAIN_MESSAGE,null,choices,choices[0]);
			//reads in from copyClipboard.txt and, based on which indicator is read in, creates the right node.
			for(int i=0; i<pasteCells.size();i++){
				if (pasteCells.get(i).contains("Auxiliary")) {
					String[] attributes = pasteCells.get(i).split(",");

					graph.get(subModelIndex-1).createAuxiliaryNodeGraphCell(attributes[1], MainFrame.DEFAULT_COORDINATE * (1+i), MainFrame.DEFAULT_COORDINATE* (1+i), false, false);

				}
				else if (pasteCells.get(i).contains("Level")){
					String[] attributes = pasteCells.get(i).split(",");

					graph.get(subModelIndex-1).createLevelNodeGraphCell(attributes[1], Double.parseDouble(attributes[2]),Double.parseDouble(attributes[3]), Double.parseDouble(attributes[4]), Double.parseDouble(attributes[5]), MainFrame.DEFAULT_COORDINATE* (1+i), MainFrame.DEFAULT_COORDINATE* (1+i), false, false);
				}
				else if(pasteCells.get(i).contains("coloredSourceSink")){					
					String[] attributes = pasteCells.get(i).split(",");
					Color nodeColor = new Color(Integer.parseInt(attributes[2]),Integer.parseInt(attributes[3]),Integer.parseInt(attributes[4]));
					graph.get(subModelIndex-1).createColoredSourceSinkNodeGraphCell(MainFrame.DEFAULT_COORDINATE * (1+i), MainFrame.DEFAULT_COORDINATE* (1+i), nodeColor, Integer.parseInt(attributes[1]));

				}
				else if(pasteCells.get(i).contains("SourceSink")){
					graph.get(subModelIndex-1).createSourceSinkNodeGraphCell(MainFrame.DEFAULT_COORDINATE* (1+i), MainFrame.DEFAULT_COORDINATE* (1+i), false, "SN");
				}

				else if(pasteCells.get(i).contains("Constant")){
					String[] attributes = pasteCells.get(i).split(",");
					graph.get(subModelIndex-1).createConstantNodeGraphCell(attributes[1], Double.parseDouble(attributes[2]), MainFrame.DEFAULT_COORDINATE* (1+i), MainFrame.DEFAULT_COORDINATE* (1+i), false, false);

				}

				else if(pasteCells.get(i).contains("Rate")){
					String[] attributes = pasteCells.get(i).split(",");
					graph.get(subModelIndex-1).createRateNodeGraphCell(attributes[1], MainFrame.DEFAULT_COORDINATE* (1+i), MainFrame.DEFAULT_COORDINATE* (1+i), false, false);

				}
			}

		}

	}
	private class shareAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
		private shareAction(String name, Icon icon, String toolTipText){
			super(name, icon);
			putValue(Action.SHORT_DESCRIPTION, toolTipText);
			
		}

		public void actionPerformed(ActionEvent e){
			Object[] cells = {};
			ArrayList<Integer> SubmodelNumbers = new ArrayList<Integer>();
			for(int i=1;i<=graph.size();i++){
				SubmodelNumbers.add(i);
			}
			JFrame frame = new JFrame("InputDialog");
			Object[] choices = SubmodelNumbers.toArray();
			int subModelIndex = (Integer)JOptionPane.showInputDialog(frame,"Share with which submodel?","Share",JOptionPane.PLAIN_MESSAGE,null,choices,choices[0]);
			subModelIndex = subModelIndex-1;
			int shareSubModel = 0;
			for(int j=0;j<graph.size();j++){
				if(cells.length == 0){
					cells = graph.get(j).getSelectionCells();
					if(cells.length==0){
						shareSubModel = j;
					}
						
				}
				


			}
			String sharedPointerLocal = "";
			for(int i=0; i<cells.length;i++){
//				SharedNodeGraphCell cell = null;
				DefaultGraphCell source = (DefaultGraphCell) cells[i];
				
				if (cells[i] instanceof AuxiliaryNodeGraphCell) {
					sharedPointerLocal = ((AuxiliaryNodeGraphCell)cells[i]).getAttributes().get("name").toString();
					
					graph.get(subModelIndex).createSharedNodeGraphCell(MainFrame.DEFAULT_COORDINATE,MainFrame.DEFAULT_COORDINATE,shareSubModel,sharedPointerLocal, "Auxiliary", -1,graph.get(shareSubModel).getModelNode(source));
					
				}
				else if (cells[i] instanceof LevelNodeGraphCell){
					sharedPointerLocal = ((LevelNodeGraphCell)cells[i]).getAttributes().get("name").toString();
					Double startVal = (Double)((LevelNodeGraphCell)cells[i]).getAttributes().get("startVal");
					
					graph.get(subModelIndex).createSharedNodeGraphCell(MainFrame.DEFAULT_COORDINATE,MainFrame.DEFAULT_COORDINATE,shareSubModel,sharedPointerLocal, "Level", startVal, graph.get(shareSubModel).getModelNode(source));

				}
				
				else if(cells[i] instanceof ConstantNodeGraphCell){
					sharedPointerLocal = ((ConstantNodeGraphCell)cells[i]).getAttributes().get("name").toString();
					double constVal = (Double)((ConstantNodeGraphCell)cells[i]).getAttributes().get("constval");
					graph.get(subModelIndex).createSharedNodeGraphCell(MainFrame.DEFAULT_COORDINATE,MainFrame.DEFAULT_COORDINATE,shareSubModel,sharedPointerLocal, "Constant", constVal, graph.get(shareSubModel).getModelNode(source));
				}
				
			}
		}

	}

	private class AddDescriptionAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		
		private AddDescriptionAction(String name, Icon icon, String toolTipText){
			super(name, icon);
			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}
		public void actionPerformed(ActionEvent e){
			
			ArrayList<Integer> SubmodelNumbers = new ArrayList<Integer>();
			for(int i=1;i<=graph.size();i++){
				SubmodelNumbers.add(i);
			}
			JFrame frame = new JFrame("InputDialog");
			Object[] choices = SubmodelNumbers.toArray();
			Object subModelIndex = JOptionPane.showInputDialog(frame,"Add description into which submodel?","Add",JOptionPane.PLAIN_MESSAGE,null,choices,choices[0]);
			if(subModelIndex != null){
				subModelIndex = (Integer) subModelIndex;
				String description =
						SubModelDescriptionDialog.showNodeNameDialog(start, MainFrame.this,
								messages.getString("MainFrame.ToolBar.Add.Description"),
								graph.get((Integer) subModelIndex - 1).getModelDescription());
				graph.get((Integer) subModelIndex - 1).setModelDescription(description);
				System.out.println(description+"   "+subModelIndex);
			}

		}

	}
	
	
	
	
	
	// This will change the source of the actionevent to graph.
	public class EventRedirector extends AbstractAction {

		protected Action action;

		// Construct the "Wrapper" Action
		public EventRedirector(Action a) {
			super("", (ImageIcon) a.getValue(Action.SMALL_ICON));
			this.action = a;
		}

		// Redirect the Actionevent
		public void actionPerformed(ActionEvent e) {
			e = new ActionEvent(graph, e.getID(), e.getActionCommand(), e
					.getModifiers());
			action.actionPerformed(e);
		}
	}
	
}
