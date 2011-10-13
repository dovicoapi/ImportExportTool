package com.dovico.importexporttool;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.*;

import com.dovico.commonlibrary.CPanel_Settings;


public class CCommonUILogic {
	// Listener from the UI class that gets called when the settings data has changed so that it can update its UI as need be
	private ActionListener m_alSettingsChanged = null; 
	
	private JTabbedPane m_pTabControl = null;
	
	private CPanel_Settings m_pSettingsTab = null;
	private int m_iPreviousTabIndex = -1;
	private int m_iSettingsTabIndex = 2;
	
	private String m_sConsumerSecret = "";
	private String m_sDataAccessToken = "";
	
	
	
	// Overloaded constructor
	public CCommonUILogic(Container cContainer, ActionListener alSettingsChanged) {
		// I don't like the look of the default 'Metal' UI. Here we tell the UI Manager to use the default look of the OS we're on
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } 
		catch (UnsupportedLookAndFeelException e) { }
		catch (ClassNotFoundException e) { }
		catch (InstantiationException e) { }
		catch (IllegalAccessException e) { }
		
		
		// Remember the action listener for when the settings are changed (so that we can tell the proper class that the settings have changed and they need to be
		// saved
		m_alSettingsChanged = alSettingsChanged;
		
		
		// Cause the controls to be created
		initializeControls(cContainer);
	}
	
	
	// Initialization function to call to have the controls created and added to the container specified
	private void initializeControls(Container cContainer){
		// Create our tab control
		m_pTabControl = new JTabbedPane();
		m_pTabControl.setFont(new Font("Arial", Font.PLAIN, 11));
		m_pTabControl.addChangeListener(new ChangeListener() {		    
		    public void stateChanged(ChangeEvent evt) { handleTabsChanged(); }
		});
		
		// Add the tab control to the container passed in
		cContainer.add(m_pTabControl);

		
		// Create our Export Tab panel and add it to our tab control
		CPanel_Export pExportTab = new CPanel_Export(this);
		m_pTabControl.addTab("Export", null, pExportTab, null);
		
		// Create our Import Tab panel and add it to our tab control
		CPanel_Import pImportTab = new CPanel_Import(this); 
		m_pTabControl.addTab("Import", null, pImportTab, null);
		
		// Create our Settings Tab panel and add it to our tab control
		m_pSettingsTab = new CPanel_Settings(); 
		m_pTabControl.addTab("Settings", null, m_pSettingsTab, null);
	}
	
	
	// Called when the form is first displayed (windowOpened event)
	public void handlePageLoad(String sConsumerSecret, String sDataAccessToken) 
	{ 
		// Remember the preferences specified
		m_sConsumerSecret = sConsumerSecret;
		m_sDataAccessToken = sDataAccessToken;
		
		// Tell the Settings pane what the settings are
		m_pSettingsTab.setSettingsData(m_sConsumerSecret, m_sDataAccessToken);
		
		
		// If either value is empty then...
		if(m_sConsumerSecret.isEmpty() || m_sDataAccessToken.isEmpty()) {
			// Make sure the Settings tab is selected
			m_iSettingsTabIndex = 2;
			m_pTabControl.setSelectedIndex(m_iSettingsTabIndex);
		} // End if(m_sConsumerSecret.isEmpty() || m_sDataAccessToken.isEmpty())
	}	
	
	
	// Tab's selection has been changed (NOTE: This gets called when the control is initially displayed and when the tab's selection is changed via code)
	private void handleTabsChanged() {
		// If the previous tab was the Settings tab then...(user just tabbed off of the settings tab
	    if(m_iPreviousTabIndex == m_iSettingsTabIndex)
	    {
	    	// If everything validates OK for the Settings tab then...
	    	if(m_pSettingsTab.validateSettingsData()) 
	    	{		        				        		
	    		// Grab the new settings values
	    		m_sConsumerSecret = m_pSettingsTab.getConsumerSecret();
	    		m_sDataAccessToken = m_pSettingsTab.getDataAccessToken();
	    		
	    		// Update the UI class telling it that the settings have been changed (so that the settings can be saved and data reloaded - we need to do it this
	    		// way rather than handling the load/save in the Setting panel because if the settings panel is used by an Applet, having a reference to 
	    		// 'java.util.prefs.Preferences' will throw an exception)
	    		m_alSettingsChanged.actionPerformed(null);
	    	} 
	    	else // Validation failed... 
	    	{ 		        	
	    		// Reselect the Settings tab (indicate that the previous index is not the settings tab so that the validation is not hit again) 
	    		m_iPreviousTabIndex = -1; 
	    		m_pTabControl.setSelectedIndex(m_iSettingsTabIndex);
	    	} // End if(m_pSettingsTab.validateSettingsData())
	    } // End if(m_iPreviousTabIndex == m_iSettingsTabIndex)
	    
	    
	    // Remember the selected tab index
	    m_iPreviousTabIndex = m_pTabControl.getSelectedIndex();
	}
	
		
	// Methods returning the setting values
	public String getConsumerSecret() { return m_sConsumerSecret; }
	public String getDataAccessToken() { return m_sDataAccessToken; }
}
