package de.uka.aifb.com.systemDynamics.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class GlobalFileEditor extends JFrame{
	JFileChooser fileChooser;
	private static final String FILE_ICON = "resources/icon.png";
	private static final String FILE_OPEN_ICON = "resources/folder_page_white.png";
	private static final String CLOSE_ICON = "resources/cancel.png";
	private static final String FILE_SAVE_ICON = "resources/disk.png";
	private static final String FILE_NEW_ICON = "resources/page_white.png";
	private static final String ADD_VAR_ICON = "resources/add_var.png";
	private static final String HELP_DOC_ICON = "resources/help.png";
	private Action newAction;
	private Action openAction;
	private Action saveAction;
	private Action closeAction;
	private Action addVarAction;
	private Action openHelpDocAction;
	private JPanel contentPanel;
	
	public GlobalFileEditor(){		
		
		fileChooser = new JFileChooser();

		//allows JFileChooser to select both files and directories.
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		
		setTitle("Global File Editor");

		// set frame size and location

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		setSize((int)(dimension.getWidth() * 0.75), (int)(dimension.getHeight() * 0.75));
		setLocation((int)(dimension.getWidth() * 0.125), (int)(dimension.getHeight() * 0.125));

		// set icon
		setIconImage(new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_ICON)).getImage());

		// initialize actions
		initializeActions();


		contentPanel = new JPanel(new BorderLayout());
		
		// create tool bar
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(createToolBar(), BorderLayout.PAGE_START);

		getContentPane().add(contentPanel, BorderLayout.CENTER);

		setVisible(true);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	}
	private void initializeActions() {
		newAction = new NewAction("New Action",
				new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_NEW_ICON)),
				"New File");
		openAction = new openAction("Open File",
				new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_OPEN_ICON)),
				"Open File");
		
		saveAction = new saveAction("Save File", new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(FILE_SAVE_ICON)),
				"Save File");
		
		closeAction = new closeAction("Close File", new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(CLOSE_ICON)), "Close File");
		
		
		addVarAction = new addVarAction("Add Chart", new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(ADD_VAR_ICON)),
				"Add Chart");
		
		openHelpDocAction = new openHelpDocAction("Open Help Document", new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(HELP_DOC_ICON)),
				"Open Help Document");
				
		addVarAction.setEnabled(false);
		saveAction.setEnabled(false);
		closeAction.setEnabled(false);


	}
	private JToolBar createToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.setRollover(true);
		toolBar.setFloatable(false);

		toolBar.add(newAction);
		toolBar.add(openAction);
		toolBar.add(saveAction);
		toolBar.add(closeAction);
		toolBar.addSeparator();
		toolBar.add(addVarAction);
		toolBar.add(openHelpDocAction);
		
		return toolBar;

	}


	private class NewAction extends AbstractAction{
		public NewAction(String name, Icon icon, String toolTipText) {
			super(name, icon);

			putValue(Action.SHORT_DESCRIPTION, toolTipText);

		}
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
			contentPanel.removeAll();
			
			final JPanel varsPanel = new JPanel();
			varsPanel.setLayout(new GridLayout());

			JLabel heading = new JLabel("Variables");
			varsPanel.add(heading);
			

			JScrollPane varScroll = new JScrollPane(varsPanel);
			
			contentPanel.add(varScroll,BorderLayout.CENTER);
			
			final JPanel commandsPanel = new JPanel();
			varsPanel.setLayout(new GridLayout());
			

//			JLabel heading2 = new JLabel("Commands");
//			commandsPanel.add(heading2);
//			
//			JScrollPane commandsScroll = new JScrollPane(commandsPanel);
//			
//			contentPanel.add(commandsScroll,BorderLayout.EAST);
//			contentPanel.setVisible(true);
			
			addVarAction.setEnabled(true);
			saveAction.setEnabled(true);
			closeAction.setEnabled(true);

			contentPanel.revalidate();
			
		}

	}
	private class openAction extends AbstractAction{
		public openAction(String name, Icon icon, String toolTipText) {
			super(name, icon);

			putValue(Action.SHORT_DESCRIPTION, toolTipText);

		}
		@Override
		public void actionPerformed(ActionEvent e) {


			

		}

	}
	
	private class saveAction extends AbstractAction{
		public saveAction(String name, Icon icon, String toolTipText) {
			super(name, icon);

			putValue(Action.SHORT_DESCRIPTION, toolTipText);

		}
		@Override
		public void actionPerformed(ActionEvent e) {
			File file = null;
			fileChooser.setDialogTitle("Save File As");
			int returnVal = fileChooser.showSaveDialog(GlobalFileEditor.this);
			
		}

	}
		
	private class closeAction extends AbstractAction{
		public closeAction(String name, Icon icon, String toolTipText){
			super(name, icon);
			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}
		@Override

		public void actionPerformed(ActionEvent e){

			
		}
	}
	
	private class addVarAction extends AbstractAction{
		public addVarAction(String name, Icon icon, String toolTipText){
			super(name, icon);
		}
		@Override

		public void actionPerformed(ActionEvent e){

			
		}
	}

	private class openHelpDocAction extends AbstractAction{
		public openHelpDocAction(String name, Icon icon, String toolTipText){
			super(name, icon);
		}
		@Override

		public void actionPerformed(ActionEvent e){

			
		}
	}
	
	private JPanel createChartPanel(){
		
		final JPanel chartPanel = new JPanel();
		chartPanel.setLayout(new BorderLayout());//new GridLayout(1,2));
		chartPanel.addMouseListener(new MouseListener(){
		
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				
					
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		return chartPanel;
		
	}

	
	
	

}
