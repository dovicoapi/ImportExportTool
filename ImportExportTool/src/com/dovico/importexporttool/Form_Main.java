package com.dovico.importexporttool;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.prefs.Preferences;

import javax.swing.JFrame;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.dovico.commonlibrary.APIRequestResult;
import com.dovico.commonlibrary.CRESTAPIHelper;
import com.dovico.commonlibrary.CXMLHelper;


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
				
				String sCompanyName = prefs.get("CompanyName", "");
				String sUserName = prefs.get("UserName", "");
				String sDataAccessToken = prefs.get(Constants.PREFS_KEY_USER_TOKEN, "");
				String sEmployeeID = prefs.get(Constants.PREFS_KEY_EMPLOYEE_ID, "0");
				String sEmployeeFirstName = prefs.get(Constants.PREFS_KEY_EMPLOYEE_FIRST, "");
				String sEmployeeLastName = prefs.get(Constants.PREFS_KEY_EMPLOYEE_LAST, "");
				String importPath = prefs.get(Constants.PREFS_IMPORT_PATH, "");
				String exportPath = prefs.get(Constants.PREFS_EXPORT_PATH, "");
				boolean isDBV13 = (prefs.get(Constants.PREFS_KEY_IS_DB_V13, "") == "T");
												
				m_UILogic.handlePageLoad(sDataAccessToken, sCompanyName, isDBV13, sUserName, "", Long.valueOf(sEmployeeID), sEmployeeFirstName, sEmployeeLastName, importPath, exportPath); 
			}
		});
		m_frmDovicoImportExport.setTitle("DOVICO - Import/Export Tool");
		m_frmDovicoImportExport.setBounds(100, 100, 477, 440);
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
				// Grab the current Consumer Secret we have. If it matches our constant then clear the variable so that we don't save the value potentially
				// exposing sensitive information
				String sConsumerSecretToSave = m_UILogic.getConsumerSecret();
				if(sConsumerSecretToSave.equals(Constants.CONSUMER_SECRET_API_TOKEN)){ sConsumerSecretToSave = ""; }
				
				// Determine if we're connected to a v13 or Timesheet database				
				m_UILogic.setIsDBV13(isDatabaseV13());
				
				// Save the settings
				Preferences prefs = Preferences.userNodeForPackage(Form_Main.class);
				
				prefs.put("CompanyName",  m_UILogic.getCompanyName());
				prefs.put("UserName", m_UILogic.getUserName());
				prefs.put(Constants.PREFS_KEY_USER_TOKEN, m_UILogic.getDataAccessToken());
				prefs.put(Constants.PREFS_KEY_EMPLOYEE_ID, Long.toString(m_UILogic.getEmployeeID()));
				prefs.put(Constants.PREFS_KEY_EMPLOYEE_FIRST, m_UILogic.getEmployeeFirstName());
				prefs.put(Constants.PREFS_KEY_EMPLOYEE_LAST, m_UILogic.getEmployeeLastName());
				prefs.put(Constants.PREFS_EXPORT_PATH, m_UILogic.getExportPath());
				prefs.put(Constants.PREFS_IMPORT_PATH, m_UILogic.getImportPath());
				prefs.put(Constants.PREFS_KEY_IS_DB_V13, (m_UILogic.getIsDBV13() ? "T" :"F"));
			}
		};
	}
	
	private boolean isDatabaseV13() {
		String sURI = CRESTAPIHelper.buildURI("ApiInfo/", "", Constants.API_VERSION_TARGETED);
		
		// Request the ApiInfo record (info of the person logging in)
		APIRequestResult arResult = CRESTAPIHelper.makeAPIRequest(sURI, "GET", null, m_UILogic.getConsumerSecret(), m_UILogic.getDataAccessToken());	
		Element xeDocElement = arResult.getResultDocument().getDocumentElement();
		
		// From the Result element, get the first element APIInfo
		NodeList xnlElements = xeDocElement.getElementsByTagName("APIInfo");
		Element xeAPIInfo = (Element)xnlElements.item(0);
		String BuildValue = CXMLHelper.getChildNodeValue(xeAPIInfo, "Build", "");

		// Tell the caller if the current database is a v13 or Timesheet database (Timesheet starts with 14.)
		return BuildValue.startsWith("13.");
	}	
}
