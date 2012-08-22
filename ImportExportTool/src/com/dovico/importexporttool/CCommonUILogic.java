package com.dovico.importexporttool;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.*;

import com.dovico.commonlibrary.CPanel_About;
import com.dovico.commonlibrary.CPanel_Settings;


public class CCommonUILogic {
	// Listener from the UI class that gets called when the settings data has changed so that it can update its UI as need be
	private ActionListener m_alSettingsChanged = null; 
	
	private JTabbedPane m_pTabControl = null;
	
	private CPanel_Settings m_pSettingsTab = null;
	private CPanel_About m_pAboutTab = null;
	private int m_iPreviousTabIndex = -1;
	private int m_iSettingsTabIndex = 2;
	
	private String m_sConsumerSecret = "";
	private String m_sDataAccessToken = "";
	private Long m_lEmployeeID = null;
	private String m_sEmployeeFirstName = "";
	private String m_sEmployeeLastName = "";
	
	
	// Overloaded constructor
	public CCommonUILogic(Container cContainer, ActionListener alSettingsChanged) {
		// Change the look from the Metal UI which I find kind of ugly
		try {
			// Loop through the various LookAndFeel items to see if 'Nimbus' exists. If yes then...
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        } // End if ("Nimbus".equals(info.getName()))
		    } // End of the for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) loop.
		}catch (Exception e) {
			// Switch the look to the system default
			try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } 
			catch (UnsupportedLookAndFeelException e2) { }
			catch (ClassNotFoundException e2) { }
			catch (InstantiationException e2) { }
			catch (IllegalAccessException e2) { }
		} // End of the catch (Exception e) statement.
		
		
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
		
		// Create our About Tab panel and add it to our tab control
		m_pAboutTab = new CPanel_About("Import/Export Tool", "1.2"); 
		m_pTabControl.addTab("About", null, m_pAboutTab, null);
	}
	
	
	// Called when the form is first displayed (windowOpened event)
	/// <history>
    /// <modified author="C. Gerard Gallant" date="2011-12-14" reason="Now receives lEmployeeID, sEmployeeFirstName, and sEmployeeLastName parameters. Also added logic to grab the employee id if we already have token values (for those who have already run this app before the employee id functionality was added in)"/>
	/// <modified author="C. Gerard Gallant" date="2012-04-20" reason="Added code to use the constant for the Consumer Secret if it exists. Code has also been added to tell the Settings pane not to show the Consumer Secret text box if the constant has a value."/>
	/// </history>
	public void handlePageLoad(String sConsumerSecret, String sDataAccessToken, Long lEmployeeID, String sEmployeeFirstName, String sEmployeeLastName) 
	{ 
		// We will hide the Consumer Secret field if the constant for the token is not an empty string. Pass the proper consumer secret value to our parent class
		// if the constant was specified. If not, use the token that was last saved by the user.
		boolean bHideConsumerSecretField = !Constants.CONSUMER_SECRET_API_TOKEN.isEmpty();
		
		// Remember the preferences specified
		m_sConsumerSecret = (bHideConsumerSecretField ? Constants.CONSUMER_SECRET_API_TOKEN : sConsumerSecret);
		m_sDataAccessToken = sDataAccessToken;
		m_lEmployeeID = lEmployeeID;
		m_sEmployeeFirstName = sEmployeeFirstName;
		m_sEmployeeLastName = sEmployeeLastName;
		
		// Determine if the tokens have values or not
		boolean bIsConsumerSecretEmpty = m_sConsumerSecret.isEmpty();
		boolean bIsDataAccessTokenEmpty = m_sDataAccessToken.isEmpty();
		
		
		// Tell the Settings pane what the settings are (we are not concerned about the logged in employee's First and Last name in this app but rather than have
		// to write upgrade code, like the code to come below, if that ever changes, we grab and store the values just in case)
		m_pSettingsTab.setSettingsData(m_sConsumerSecret, m_sDataAccessToken, Constants.API_VERSION_TARGETED, m_lEmployeeID, m_sEmployeeFirstName, 
				m_sEmployeeLastName, bHideConsumerSecretField);
		
		// If the Employee ID is 0 and the consumer secret and data access token have values then...(that means this application was run before the new functionality
		// was added to the settings panel to grab the employee id/name and we now need to grab the employee id/name)
		if(m_lEmployeeID == 0 && !bIsConsumerSecretEmpty && !bIsDataAccessTokenEmpty) {
			// We need to query the Employee ID and the easiest way to do so is to leverage the functionality now built into the settings panel. If for some reason
			// the validation fails then...
			if(!m_pSettingsTab.validateSettingsData()) {
				m_sConsumerSecret = "";
				m_sDataAccessToken = "";
			} else { // Validation was successful...
				// Grab the Employee ID obtained from the API and then update the UI class telling it that the settings have been changed so that they can be saved 
				m_lEmployeeID = m_pSettingsTab.getEmployeeID();
				m_sEmployeeFirstName = m_pSettingsTab.getEmployeeFirstName();
				m_sEmployeeLastName = m_pSettingsTab.getEmployeeLastName();
	    		m_alSettingsChanged.actionPerformed(null);
			} // End if(!m_pSettingsTab.validateSettingsData())
		} // End if(m_lEmployeeID == 0 && !bIsConsumerSecretEmpty && !bIsDataAccessTokenEmpty)
			
		
		// If either token value is empty then...
		if(bIsConsumerSecretEmpty || bIsDataAccessTokenEmpty) {
			// Make sure the Settings tab is selected
			m_iSettingsTabIndex = 2;
			m_pTabControl.setSelectedIndex(m_iSettingsTabIndex);
		} // End if(bIsConsumerSecretEmpty || bIsDataAccessTokenEmpty)
	}	
	
	
	// Tab's selection has been changed (NOTE: This gets called when the control is initially displayed and when the tab's selection is changed via code)
	/// <history>
    /// <modified author="C. Gerard Gallant" date="2011-12-14" reason="Now grabs the Employee ID, First Name, and Last Name from the settings panel"/>
    /// </history>
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
	    		m_lEmployeeID = m_pSettingsTab.getEmployeeID();
	    		m_sEmployeeFirstName = m_pSettingsTab.getEmployeeFirstName();
				m_sEmployeeLastName = m_pSettingsTab.getEmployeeLastName();
	    		
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
	public Long getEmployeeID() { return m_lEmployeeID; }
	public String getEmployeeFirstName() { return m_sEmployeeFirstName; }
	public String getEmployeeLastName() { return m_sEmployeeLastName; }
}
