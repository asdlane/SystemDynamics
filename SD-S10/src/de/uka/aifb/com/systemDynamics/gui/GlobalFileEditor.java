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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;

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
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.uka.aifb.com.systemDynamics.SystemDynamics;
import de.uka.aifb.com.systemDynamics.model.PlanNode;


public class GlobalFileEditor extends JFrame implements WindowListener{
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
	private JPanel varsPanelPointer;
	private ResourceBundle messages;
	
	private HashMap<String,String> vars;
	
	private boolean graphModified = false;
//	   public static void main(String[] args) {
//
//			GlobalFileEditor globalFileEditor = new GlobalFileEditor();
//
//		   }
	
	public GlobalFileEditor(SystemDynamics start){		
		

		messages = start.getMessages();
		vars = new HashMap<String,String>();
		
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

//		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);

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
			varsPanelPointer = varsPanel;
//			JLabel heading = new JLabel("Variables");
//			varsPanel.add(heading);
			

			JScrollPane varScroll = new JScrollPane(varsPanel);
			
			contentPanel.add(varScroll,BorderLayout.CENTER);
			
			final JPanel commandsPanel = new JPanel();
//			commandsPanel.setLayout(new GridLayout(1,1));
			contentPanel.add(commandsPanel,BorderLayout.SOUTH);
			
			varsPanel.setLayout(new GridLayout(0,4));

		      JButton addButton = new JButton("Add new variable");
		      addButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						String name = JOptionPane.showInputDialog(null,"Variable Name:","Name",JOptionPane.PLAIN_MESSAGE);
						String value = JOptionPane.showInputDialog(null,"Variable Value:","Value",JOptionPane.PLAIN_MESSAGE);
						
						JPanel varPanel = new JPanel();
						varsPanel.add(varPanel);
						varPanel.setLayout(new GridLayout(2,1));

						vars.put(name, value);
						

						final JPanel editPanel = new JPanel();
						editPanel.setLayout(new GridLayout(0,2));
						JLabel label = new JLabel("Name");
						final JTextField text = new JTextField(name);
						JLabel label2 = new JLabel("Value");
						final JTextField text2 = new JTextField(value);
						
						
						final String[] attrs ={"",""};
						
						text.setEditable(false);
						text2.setEditable(false);
						
						editPanel.add(label);
						editPanel.add(text);
						editPanel.add(label2);
						editPanel.add(text2);
						
						editPanel.add(new JLabel());
						editPanel.add(new JLabel());
						editPanel.add(new JLabel());
						editPanel.add(new JLabel());
						editPanel.add(new JLabel());
						editPanel.add(new JLabel());
						editPanel.add(new JLabel());
						editPanel.add(new JLabel());

						varPanel.add(editPanel);
						
						
						final JPanel buttonPanel = new JPanel();
					    final JPanel buttonPanel2 = new JPanel();
					    final CardLayout card = new CardLayout();
					    final JPanel buttonsPanel = new JPanel(card);
					      
					    JButton okButton = new JButton("OK");
					    okButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {

								String name=text.getText();
								String value=text2.getText();
								
								vars.remove(attrs[0]);
								vars.put(name, value);
								
								text.setEditable(false);
								text2.setEditable(false);
								card.previous(buttonsPanel);
								graphModified = true;
							}

						});
						
					    buttonPanel.add(okButton);
					    
					    JButton cancelButton = new JButton("Cancel");
					    cancelButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {

								text.setText(attrs[0]);
								text2.setText(attrs[1]);
								
								text.setEditable(false);
								text2.setEditable(false);
								
								card.previous(buttonsPanel);
							}

					    });
					    buttonPanel.add(cancelButton);
						

					    JButton editButton = new JButton("Edit");
					    editButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {

								attrs[0]=text.getText();
								attrs[1]=text2.getText();
								
								text.setEditable(true);
								text2.setEditable(true);
								
								card.next(buttonsPanel);
							}

					    });

					    buttonPanel2.add(editButton);
					    

					    buttonsPanel.add("bp2",buttonPanel2);
					    buttonsPanel.add("bp1",buttonPanel);
					    varPanel.add(buttonsPanel);

						TitledBorder border = BorderFactory.createTitledBorder("Plan Node");
						varPanel.setBorder(border);
						
						graphModified = true;
						contentPanel.revalidate();
					}
				});
		    
		      commandsPanel.add(addButton);  
		      
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
			int reply = JOptionPane.YES_OPTION;
			if(graphModified != false){
				reply = JOptionPane.showConfirmDialog(null, "Opening a new document will cause any unsaved changes to be lost. \n Are you sure you would like to open a new document?", "Open", JOptionPane.YES_NO_OPTION);
			}
			if(reply == JOptionPane.YES_OPTION){
				int returnVal = fileChooser.showOpenDialog(GlobalFileEditor.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					// file was selected and 'OK' was pressed
					File file = fileChooser.getSelectedFile();
					if (file.exists()) {
						Scanner scanner = null;
	//					HashMap<String, String> nodes = new HashMap<String, String>();
						vars.clear();
						try {
							scanner = new Scanner(file);
							
							while(scanner.hasNextLine()){
					            String line = scanner.nextLine();
					            String tmp[] = line.split(",");
					            vars.put(tmp[0], tmp[1]);
					            System.out.println("node " + tmp[0] + " :" + tmp[1]);
					            
					        } 
	
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							JOptionPane.showMessageDialog(GlobalFileEditor.this,
									"Read File Exception",
									"THe format of the file is not right",
									JOptionPane.ERROR_MESSAGE);
						}
						
						scanner.close();
					
						
						final JPanel varsPanel = new JPanel();
						varsPanelPointer = varsPanel;
						JScrollPane varScroll = new JScrollPane(varsPanel);
						
						contentPanel.removeAll();
						contentPanel.add(varScroll,BorderLayout.CENTER);
						
						final JPanel commandsPanel = new JPanel();
	
						contentPanel.add(commandsPanel,BorderLayout.SOUTH);
						
						varsPanel.setLayout(new GridLayout(0,4));
	
						for(String var: vars.keySet()){
	
							JPanel varPanel = createVarPanel(var,vars.get(var));
							varsPanel.add(varPanel);
						}
						
					      JButton addButton = new JButton("Add new variable");
					      addButton.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									String name = JOptionPane.showInputDialog(null,"Variable Name:","Name",JOptionPane.PLAIN_MESSAGE);
									String value = JOptionPane.showInputDialog(null,"Variable Value:","Value",JOptionPane.PLAIN_MESSAGE);
									
									vars.put(name, value);
									
									JPanel varPanel = createVarPanel(name,value);
									varsPanel.add(varPanel);
									
									graphModified = true;
									
									contentPanel.revalidate();
								}
							});
					    
					      commandsPanel.add(addButton);  
					      
						
						addVarAction.setEnabled(true);
						saveAction.setEnabled(true);
						closeAction.setEnabled(true);
	
						contentPanel.revalidate();
	
						graphModified = false;
						

						addVarAction.setEnabled(true);
						saveAction.setEnabled(true);
						closeAction.setEnabled(true);
					}
				}
			}
		}
		
	}
	private JPanel createVarPanel(String name, String value){
			
			JPanel varPanel = new JPanel();
			varPanel.setLayout(new GridLayout(2,1));			

			final JPanel editPanel = new JPanel();
			editPanel.setLayout(new GridLayout(0,2));
			JLabel label = new JLabel("Name");
			final JTextField text = new JTextField(name);
			JLabel label2 = new JLabel("Value");
			final JTextField text2 = new JTextField(value);
			
			
			final String[] attrs ={"",""};
			
			text.setEditable(false);
			text2.setEditable(false);
			
			editPanel.add(label);
			editPanel.add(text);
			editPanel.add(label2);
			editPanel.add(text2);
			
			editPanel.add(new JLabel());
			editPanel.add(new JLabel());
			editPanel.add(new JLabel());
			editPanel.add(new JLabel());
			editPanel.add(new JLabel());
			editPanel.add(new JLabel());
			editPanel.add(new JLabel());
			editPanel.add(new JLabel());

			varPanel.add(editPanel);
			
			
			final JPanel buttonPanel = new JPanel();
		    final JPanel buttonPanel2 = new JPanel();
		    final CardLayout card = new CardLayout();
		    final JPanel buttonsPanel = new JPanel(card);
		      
		    JButton okButton = new JButton("OK");
		    okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					String name=text.getText();
					String value=text2.getText();
					
					vars.remove(attrs[0]);
					vars.put(name, value);
					
					text.setEditable(false);
					text2.setEditable(false);
					card.previous(buttonsPanel);
					graphModified = true;
				}

			});
			
		    buttonPanel.add(okButton);
		    
		    JButton cancelButton = new JButton("Cancel");
		    cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					text.setText(attrs[0]);
					text2.setText(attrs[1]);
					
					text.setEditable(false);
					text2.setEditable(false);
					
					card.previous(buttonsPanel);
				}

		    });
		    buttonPanel.add(cancelButton);
			

		    JButton editButton = new JButton("Edit");
		    editButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					attrs[0]=text.getText();
					attrs[1]=text2.getText();
					
					text.setEditable(true);
					text2.setEditable(true);
					
					card.next(buttonsPanel);
				}

		    });

		    buttonPanel2.add(editButton);
		    

		    buttonsPanel.add("bp2",buttonPanel2);
		    buttonsPanel.add("bp1",buttonPanel);
		    varPanel.add(buttonsPanel);

			TitledBorder border = BorderFactory.createTitledBorder("Level Node");
			varPanel.setBorder(border);
			return varPanel;
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
			int selectedOption = 0;
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				// file was selected and 'OK' was pressed
				file = fileChooser.getSelectedFile();

				// file name should end with ".xml"
				if (!file.getName().toLowerCase().endsWith(".txt")) {
					file = new File(file.getAbsolutePath() + ".txt");
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
					selectedOption = JOptionPane.showOptionDialog(null,
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
			
			String filename = file.getAbsolutePath();
			if(selectedOption==1){
				file.delete();
			}
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(new File(filename), true);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
				
				for(String name: vars.keySet()){
					bw.write(name+','+vars.get(name));
					bw.newLine();
				}
				bw.close();
				
				graphModified = false;
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

	}
		
	private class closeAction extends AbstractAction{
		public closeAction(String name, Icon icon, String toolTipText){
			super(name, icon);
			putValue(Action.SHORT_DESCRIPTION, toolTipText);
		}
		@Override

		public void actionPerformed(ActionEvent e){
			
			if (graphModified) {
				Object[] options = { messages.getString("MainFrame.Yes"), messages.getString("MainFrame.No") };
				int selectedOption = JOptionPane.showOptionDialog(GlobalFileEditor.this,
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
			
			contentPanel.removeAll();
			contentPanel.add(new JPanel(), BorderLayout.CENTER);

			addVarAction.setEnabled(false);
			saveAction.setEnabled(false);
			closeAction.setEnabled(false);

			contentPanel.revalidate();
			graphModified = false;
		}
	}
	
	private class addVarAction extends AbstractAction{
		public addVarAction(String name, Icon icon, String toolTipText){
			super(name, icon);
		}
		@Override

		public void actionPerformed(ActionEvent e){
			String name = JOptionPane.showInputDialog(null,"Variable Name:","Name",JOptionPane.PLAIN_MESSAGE);
			String value = JOptionPane.showInputDialog(null,"Variable Value:","Value",JOptionPane.PLAIN_MESSAGE);
			
			JPanel varPanel = new JPanel();
			varsPanelPointer.add(varPanel);
			varPanel.setLayout(new GridLayout(2,1));

			vars.put(name, value);
			

			final JPanel editPanel = new JPanel();
			editPanel.setLayout(new GridLayout(0,2));
			JLabel label = new JLabel("Name");
			final JTextField text = new JTextField(name);
			JLabel label2 = new JLabel("Value");
			final JTextField text2 = new JTextField(value);
			
			
			final String[] attrs ={"",""};
			
			text.setEditable(false);
			text2.setEditable(false);
			
			editPanel.add(label);
			editPanel.add(text);
			editPanel.add(label2);
			editPanel.add(text2);
			
			editPanel.add(new JLabel());
			editPanel.add(new JLabel());
			editPanel.add(new JLabel());
			editPanel.add(new JLabel());
			editPanel.add(new JLabel());
			editPanel.add(new JLabel());
			editPanel.add(new JLabel());
			editPanel.add(new JLabel());

			varPanel.add(editPanel);
			
			
			final JPanel buttonPanel = new JPanel();
		    final JPanel buttonPanel2 = new JPanel();
		    final CardLayout card = new CardLayout();
		    final JPanel buttonsPanel = new JPanel(card);
		      
		    JButton okButton = new JButton("OK");
		    okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					String name=text.getText();
					String value=text2.getText();
					
					vars.remove(attrs[0]);
					vars.put(name, value);
					
					text.setEditable(false);
					text2.setEditable(false);
					card.previous(buttonsPanel);
					graphModified = true;
				}

			});
			
		    buttonPanel.add(okButton);
		    
		    JButton cancelButton = new JButton("Cancel");
		    cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					text.setText(attrs[0]);
					text2.setText(attrs[1]);
					
					text.setEditable(false);
					text2.setEditable(false);
					
					card.previous(buttonsPanel);
				}

		    });
		    buttonPanel.add(cancelButton);
			

		    JButton editButton = new JButton("Edit");
		    editButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {

					attrs[0]=text.getText();
					attrs[1]=text2.getText();
					
					text.setEditable(true);
					text2.setEditable(true);
					
					card.next(buttonsPanel);
				}

		    });

		    buttonPanel2.add(editButton);
		    

		    buttonsPanel.add("bp2",buttonPanel2);
		    buttonsPanel.add("bp1",buttonPanel);
		    varPanel.add(buttonsPanel);

			TitledBorder border = BorderFactory.createTitledBorder("Plan Node");
			varPanel.setBorder(border);
			
			graphModified = true;
			contentPanel.revalidate();
			
		}
	}

	private class openHelpDocAction extends AbstractAction{
		public openHelpDocAction(String name, Icon icon, String toolTipText){
			super(name, icon);
		}
		@Override

		public void actionPerformed(ActionEvent e){
			
			HelpDocFrame helpDocFrame = new HelpDocFrame();
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
	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		if (graphModified) {
			Object[] options = { "Yes", "No" };
			int selectedOption = JOptionPane.showOptionDialog(GlobalFileEditor.this,
					"Closing will loose the changed file. Do you really want to close?",
					"Confirm closing",
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
		dispose();
	}
	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	
	
	

}
