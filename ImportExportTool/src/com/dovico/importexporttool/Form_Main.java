package com.dovico.importexporttool;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.prefs.Preferences;

import javax.swing.JFrame;


public class Form_Main {
	
	// The main window that will be displayed to the user
	private JFrame m_frmDovicoImportExport = null;
	
	// Our UI Logic class (handles creating the controls, wiring up event handlers as need be, etc)
	private CCommonUILogic m_UILogic = null;
	
	
	// Main entry point of the application (when run as a desktop application)
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Form_Main window = new Form_Main();
					window.m_frmDovicoImportExport.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	// Constructor
	public Form_Main() {
		// Create the main frame of the application
		m_frmDovicoImportExport = new JFrame();
		m_frmDovicoImportExport.addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) 
			{
				// Grab the Preferences values (have to do this here rather than in the CommonUILogic since this will break an unsigned applet)
				Preferences prefs = Preferences.userNodeForPackage(Form_Main.class);
				String sConsumerSecret = prefs.get(Constants.PREFS_KEY_CONSUMER_SECRET, "");
				String sDataAccessToken = prefs.get(Constants.PREFS_KEY_USER_TOKEN, "");
								
				m_UILogic.handlePageLoad(sConsumerSecret, sDataAccessToken); 
			}
		});
		m_frmDovicoImportExport.setTitle("DOVICO - Import/Export Tool");
		m_frmDovicoImportExport.setBounds(100, 100, 477, 380);
		m_frmDovicoImportExport.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		// Have the tab control, and related controls, created (NOTE: If you pass the content pane in as a parameter, it works at run-time but you can't use the
		// Google WindowBuilder Design tab - nothing shows up. When you return the root control, the JTabbedPane in this case, add add it to the content pane then
		// everything shows up but you still can't edit it using the Design tab.)
		m_UILogic = new CCommonUILogic(m_frmDovicoImportExport.getContentPane(), getActionListenerForSettingsChange());
	}
	
	
	// Action Listener for when the settings are changed (callback function from the CommonUILogic class)
	private ActionListener getActionListenerForSettingsChange(){
		return new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {  
				// Save the settings
				Preferences prefs = Preferences.userNodeForPackage(Form_Main.class);
				prefs.put(Constants.PREFS_KEY_CONSUMER_SECRET, m_UILogic.getConsumerSecret());
				prefs.put(Constants.PREFS_KEY_USER_TOKEN, m_UILogic.getDataAccessToken());
			}
		};
	}	
}
