package com.dovico.importexporttool;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.dovico.commonlibrary.APIRequestResult;
import com.dovico.commonlibrary.CDatePicker;
import com.dovico.commonlibrary.CRESTAPIHelper;
import com.dovico.commonlibrary.CXMLHelper;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;


public class CPanel_Export extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JComboBox m_ddlDataSource = null;
	private JButton m_cmdDateRangeStart = null;
	private JButton m_cmdDateRangeFinish = null;
	private JComboBox m_ddlFormat = null; 
	private JTextField m_txtSaveAs = null;
		
	private DefaultListModel m_lmFieldsModel = null;
	private JList m_lstFields = null;	
	
	// Will hold a reference to the UI Logic parent class
	private CCommonUILogic m_UILogic = null;
	
	// The Start and End dates of the date range when dealing with the Time Entry export
	private Date m_dtDateRangeStart = null;
	private Date m_dtDateRangeEnd = null;

	
	// Default constructor
	public CPanel_Export(CCommonUILogic UILogic) {
		// Remember the reference to the UI Logic parent class
		m_UILogic = UILogic;
		
		// Default the date range to be today's date
		m_dtDateRangeStart = new Date();
		m_dtDateRangeEnd = new Date();
		
		
		this.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("65px"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("25dlu"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("25dlu"),
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("125px:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				RowSpec.decode("41px"),
				FormFactory.DEFAULT_ROWSPEC,
				RowSpec.decode("default:grow"),}));
		
			
		// Data Source controls:
		JLabel lblDataSource = new JLabel("Data source:");
		lblDataSource.setFont(new Font("Arial", Font.PLAIN, 11));
		this.add(lblDataSource, "2, 2, left, default");
		
		m_ddlDataSource = new JComboBox(getDataSourceValues());
		m_ddlDataSource.setFont(new Font("Arial", Font.PLAIN, 11));
		m_ddlDataSource.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent arg0) { OnSelChanged_ddlDataSource(); }
		});
		this.add(m_ddlDataSource, "4, 2, 9, 1, fill, default");

		
		// Export Field controls:
		JLabel lblNewLabel = new JLabel("Export fields:");
		lblNewLabel.setFont(new Font("Arial", Font.PLAIN, 11));
		this.add(lblNewLabel, "2, 4, default, top");
		
		m_lmFieldsModel = new DefaultListModel();
		m_lstFields = new JList(m_lmFieldsModel);
		m_lstFields.setFont(new Font("Arial", Font.PLAIN, 11));
		
		// List controls will expand/collapse as items are added/removed...they will not scroll. In order to get scrolling, as well as a UI that doesn't keep
		// growing/shrinking as it's used, we add the list to a scroll pane and add the scroll pane to the container.
		JScrollPane spFields = new JScrollPane(m_lstFields);
		spFields.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		this.add(spFields, "4, 4, 5, 1, fill, fill");
		
		
		JButton cmdFields = new JButton("Fields...");
		cmdFields.setToolTipText("Add/remove fields");
		cmdFields.setFont(new Font("Arial", Font.PLAIN, 11));
		cmdFields.setVerticalAlignment(SwingConstants.TOP);
		cmdFields.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent arg0) { OnClick_cmdFields(); } 
		});		
		this.add(cmdFields, "10, 4, 3, 1, default, top");
		
		
		// Date Range controls:
		JLabel lblDateRange = new JLabel("Date Range:");
		lblDateRange.setFont(new Font("Arial", Font.PLAIN, 11));
		this.add(lblDateRange, "2, 6, left, default");
		
		m_cmdDateRangeStart = new JButton("N/A");
		m_cmdDateRangeStart.setFont(new Font("Arial", Font.PLAIN, 11));
		m_cmdDateRangeStart.setEnabled(false);
		m_cmdDateRangeStart.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent arg0) { OnClick_cmdDateRangeStart(); } 
		});
		add(m_cmdDateRangeStart, "4, 6");
		
		JLabel lblDateRangeSep = new JLabel("-");
		lblDateRangeSep.setFont(new Font("Arial", Font.PLAIN, 11));
		add(lblDateRangeSep, "6, 6");
		
		m_cmdDateRangeFinish = new JButton("N/A");
		m_cmdDateRangeFinish.setFont(new Font("Arial", Font.PLAIN, 11));
		m_cmdDateRangeFinish.setEnabled(false);
		m_cmdDateRangeFinish.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent arg0) { OnClick_cmdDateRangeFinish(); } 
		});
		add(m_cmdDateRangeFinish, "8, 6");
		
		
		// Format controls:
		JLabel lblFormat = new JLabel("Format:");
		lblFormat.setFont(new Font("Arial", Font.PLAIN, 11));
		this.add(lblFormat, "2, 8, left, default");
		
		m_ddlFormat = new JComboBox(getFormatValues());
		m_ddlFormat.setFont(new Font("Arial", Font.PLAIN, 11));
		m_ddlFormat.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent arg0) { OnSelChanged_ddlFormat(); }
		});
		this.add(m_ddlFormat, "4, 8, 9, 1, fill, default");
		
		
		// Save As controls:
		JLabel lblSaveAs = new JLabel("Save As:");
		lblSaveAs.setFont(new Font("Arial", Font.PLAIN, 11));
		this.add(lblSaveAs, "2, 10, left, default");
		
		m_txtSaveAs = new JTextField();
		m_txtSaveAs.setEditable(false);
		m_txtSaveAs.setFont(new Font("Arial", Font.PLAIN, 11));
		m_txtSaveAs.setColumns(10);
		m_txtSaveAs.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) { OnClick_cmdSaveAs(); } // Call the Save As button's code when the text box is clicked
		});
		this.add(m_txtSaveAs, "4, 10, 7, 1, fill, default");
		
		JButton cmdSaveAs = new JButton("...");
		cmdSaveAs.setToolTipText("Choose the file save location.");
		cmdSaveAs.setFont(new Font("Arial", Font.PLAIN, 11));
		cmdSaveAs.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent arg0) { OnClick_cmdSaveAs(); } 
		});
		this.add(cmdSaveAs, "12, 10");
		
		
		// Export button
		JButton cmdExport = new JButton("Export");
		cmdExport.setFont(new Font("Arial", Font.PLAIN, 11));
		cmdExport.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent arg0) { OnClick_cmdExport(); } 
		});
		this.add(cmdExport, "10, 12, 3, 1");
	}
	
		
	// Returns the available values for the Data Source drop-down
	private String[] getDataSourceValues() {
		String[] arrItems = { 
			Constants.API_RESOURCE_ITEM_CLIENTS, 
			Constants.API_RESOURCE_ITEM_PROJECTS, 
			Constants.API_RESOURCE_ITEM_TASKS,
			Constants.API_RESOURCE_ITEM_EMPLOYEES, 
			Constants.API_RESOURCE_ITEM_TIME_ENTRIES, 
			Constants.API_RESOURCE_ITEM_EXPENSE_ENTRIES 
		};
		return arrItems;
	}
	
	
	// Returns the available values for the Format drop-down
	private IExportFormatter[] getFormatValues() {
		IExportFormatter[] arrItems = {  new CFormatterCSV() };
		return arrItems;
	}
	
	
	// Called when the selection changes for the Data Source drop-down
	private void OnSelChanged_ddlDataSource() {
		// Make sure the Fields list is emptied because the current fields no longer belong to the item that is now selected 
		m_lmFieldsModel.clear(); 
		
		boolean bEnableDateRangeControls = false;
		String sStartDateCaption = "N/A", sFinishDateCaption = "N/A";
		
		// If the Time Entry selection was chosen then...
		String sResource = (String)m_ddlDataSource.getSelectedItem();
		if(sResource.equals(Constants.API_RESOURCE_ITEM_TIME_ENTRIES)){
			// Flag that we want the Date Range controls enabled
			bEnableDateRangeControls = true;
			sStartDateCaption = getCaptionFromDate(m_dtDateRangeStart);
			sFinishDateCaption = getCaptionFromDate(m_dtDateRangeEnd);
		} // End if(sResource.equals(Constants.API_RESOURCE_ITEM_TIME_ENTRIES))
		
		
		// Adjust the Start date range button's enabled state and caption 
		m_cmdDateRangeStart.setEnabled(bEnableDateRangeControls);
		m_cmdDateRangeStart.setText(sStartDateCaption);
		
		// Adjust the Finish date range button's enabled state and caption
		m_cmdDateRangeFinish.setEnabled(bEnableDateRangeControls);
		m_cmdDateRangeFinish.setText(sFinishDateCaption);
	}

	
	// Helper function to return the text needed for a button's caption based on a date's value
	private String getCaptionFromDate(Date dtValue) {
		// Create a date formatter object and display the date as the caption on the button
		SimpleDateFormat fFormatter = new SimpleDateFormat("MMMM d, yyyy");
		return fFormatter.format(dtValue);
	}
	
	
	// Called when the user clicks on the Start Date Range button
	/// <history>
	/// <modified author="C. Gerard Gallant" date="2012-04-12" reason="I modified the CDatePicker class to work with a TableCellEditor and some of the changes required me to come back and tweak this code"/>
	/// </history>
	private void OnClick_cmdDateRangeStart() {
		// Create our Date Picker object and show it
		CDatePicker dlgDate = new CDatePicker(this.getParent(), null, "Date Range - Start", m_dtDateRangeStart);
		dlgDate.setVisible(true);
		
		// Grab the selected date. If a date was selected then...
		Date dtSelection = dlgDate.getSelectedDate();
		if(dtSelection != null) {
			// Remember the selection and then cause the button's caption to indicate the selected date
			m_dtDateRangeStart = dtSelection;
			m_cmdDateRangeStart.setText(getCaptionFromDate(dtSelection));
		} // End if(dtSelection != null)
	}
	
	
	// Called when the user clicks on the Finish Date Range button
	/// <history>
	/// <modified author="C. Gerard Gallant" date="2012-04-12" reason="I modified the CDatePicker class to work with a TableCellEditor and some of the changes required me to come back and tweak this code"/>
	/// </history>
	private void OnClick_cmdDateRangeFinish() { 
		// Create our Date Picker object (the dialog automatically displays modal)
		CDatePicker dlgDate = new CDatePicker(this.getParent(), null, "Date Range - Finish", m_dtDateRangeEnd);
		dlgDate.setVisible(true);
		
		// Grab the selected date. If a date was selected then...
		Date dtSelection = dlgDate.getSelectedDate();
		if(dtSelection != null) {
			// Remember the selection and then cause the button's caption to indicate the selected date
			m_dtDateRangeEnd = dtSelection;
			m_cmdDateRangeFinish.setText(getCaptionFromDate(dtSelection));
		} // End if(dtSelection != null)
	}
	
	
	// Called when the selection changes for the Format drop-down. Make sure the Save As text box is cleared because the format no longer applies to the item that
	// is now selected
	private void OnSelChanged_ddlFormat() { m_txtSaveAs.setText(""); }
	
	
	// Called when the user clicks on the 'Fields...' button
	private void OnClick_cmdFields() {
		// Create an instance of our Add/Remove Fields dialog. Tell the dialog which Data Source and which Fields are selected 
		Dialog_ExportFields dlgFields = new Dialog_ExportFields();
		dlgFields.setDataSourceAndSelectedFields((String)m_ddlDataSource.getSelectedItem(), m_lmFieldsModel.toArray());
		dlgFields.setVisible(true);
		
		// If the dialog was closed as a result of the user clicking on the OK button then...
		if(dlgFields.getClosedByOKButton()){
			// Remove everything from our Fields list
			m_lmFieldsModel.clear();
			
			// Get the list of selected fields from the dialog and loop through the list adding each field to our Fields list here.
			Object[] arrFields = dlgFields.getSelectedFields();
			for (Object objField : arrFields) { m_lmFieldsModel.addElement(objField); }
		} // End if(dlgFields.getClosedByOKButton())
	}
		
	
	// Called when the user clicks on the Save As button 
	private void OnClick_cmdSaveAs() {
		// Grab the selected Format item
		IExportFormatter fFormatter = (IExportFormatter)m_ddlFormat.getSelectedItem();
		String sFileFilterExtension = fFormatter.getSaveAsFileFilterExtension();
		
		// Create a File Save As object. Add in the file filter object telling it what the selected Format is. 
		JFileChooser dlgFileSaveAs = new JFileChooser();
		dlgFileSaveAs.addChoosableFileFilter(new CSaveAsFileFilter(fFormatter.getSaveAsFileFilterDescription(), sFileFilterExtension));
		
		// Show the File Save As dialog. If the user clicked OK/Save then...
		if(dlgFileSaveAs.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			// Grab the Path (and file name). Convert the path to lower case for more accurate comparisons 
			String sPath = dlgFileSaveAs.getSelectedFile().getPath();
			String sLowerCasePath = sPath.toLowerCase();
			
			// If the extension is not present then add it on to the end of the file name
			if(!sLowerCasePath.endsWith(sFileFilterExtension.toLowerCase())) { sPath = sPath.concat(sFileFilterExtension); } 
			
			// Put the path for the file name into the text box
			m_txtSaveAs.setText(sPath);
		} // End if(dlgFileSaveAs.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
	}
	
	
	// User clicked on the Export button
	private void OnClick_cmdExport() {
		// If the validation fails then...
		if(!validateForExport()) { return; }
		
		
		boolean bErrorHappened = false;
		BufferedWriter bwWriter = null;
				
		try {
			// Get the selected formatter object from the Format drop-down and get the selected DataSource (if we are exporting Clients, Projects, Tasks, etc)
			IExportFormatter fFormatter = (IExportFormatter)m_ddlFormat.getSelectedItem();
			String sDataSource = (String)m_ddlDataSource.getSelectedItem();

			// Get the URI needed for the Resource requested (employee id is currently only used for Time/Expense Imports to know if it should try to send the time/
			// expense entries as approved entries or not; the date range start/end dates are only used by the export of time entries at this point)
			String sURI = CResourceHelper.getURIForResource(sDataSource, true, m_UILogic.getEmployeeID(), m_dtDateRangeStart, m_dtDateRangeEnd);
			String sMainElementName = CResourceHelper.getMainElementNameForResource(sDataSource);			
			ArrayList<CFieldItem> alFields = getFieldItemsArrayList();
			
			// Find out if we are exporting Expense data and, if so, if one or more of the selected fields are for Expense Entry items 
			boolean bExportDataWithExpenseEntryItems = areAnyFieldsForExpenseEntryItems(sDataSource, alFields);
			
			
			// Open the file for writing (not appending - overwriting any data that might have been there from a previous export). Create a BufferedWriter object
			// to make writing to the file easier.
			FileWriter fwWriter = new FileWriter(m_txtSaveAs.getText(), false);
			bwWriter = new BufferedWriter(fwWriter);						
			
			// Write out the column headers. If there was an issue flag that there was an error.
			if(!fFormatter.WriteHeaders(alFields, bwWriter)) { bErrorHappened = true; }
			else { // There were no issues in writing out the headers...
				// Export the data specified by the Data Source drop-down and Fields list (pass in the URI needed for the first page of data, the name of the main
				// element that the field values are to be grabbed from, the formatter object that handles writing to the file, the list of selected fields, and 
				// the BufferedWriter object so that the necessary data can be written to the file)
				if(!exportData(sURI, bExportDataWithExpenseEntryItems, alFields, sMainElementName, fFormatter, bwWriter)) { bErrorHappened = true; }
			} // End if			 
		} 
		catch (IOException e) { 
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			bErrorHappened = true;
		}
		finally { // Happens whether or not there was an exception...
            try {
            	// Close the BufferedWriter
                if(bwWriter != null) { 
                	bwWriter.flush(); 
                	bwWriter.close(); 
                }
            } catch (IOException e) {
            	JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    			bErrorHappened = true;
            }
		} // End of finally block

		
		// If there were no errors then tell the user we're done
		if(!bErrorHappened){ JOptionPane.showMessageDialog(null, "Done", "Export Complete", JOptionPane.INFORMATION_MESSAGE); }
	}
	
	
	// Validates to make sure the necessary data is present in order to do an Export
	private boolean validateForExport() {
		// If there are no fields selected then...
		if(m_lmFieldsModel.getSize() == 0) {
			JOptionPane.showMessageDialog(null, "Please select at least one field for export", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		} // End if(m_lmFieldsModel.getSize() == 0)
		
		// If there is no Save As path then...
		if(m_txtSaveAs.getText().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Please specify a Save As file name", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		} // End if(m_txtSaveAs.getText().isEmpty())
		
		
		// We made it to this point. All is OK.
		return true;
	}
	
	
	// Returns a String ArrayList for the selected Fields rather than the standard Object array
	private ArrayList<CFieldItem> getFieldItemsArrayList() {
		// Object that will hold our list of fields
		ArrayList<CFieldItem> alFields = new ArrayList<CFieldItem>();
		
		// Loop through the items in the model, converting each Object into a String and adding it to the ArrayList
		int iCount = m_lmFieldsModel.getSize();
		for(int iIndex = 0; iIndex < iCount; iIndex++){ alFields.add((CFieldItem)m_lmFieldsModel.get(iIndex)); }
		
		// Return the list of fields to the caller
		return alFields;
	}
	
	
	// Returns if we are exporting expense data and if so, if any of the fields selected are for expense entries themselves
	private boolean areAnyFieldsForExpenseEntryItems(String sDataSource, ArrayList<CFieldItem> alFields) {	
		// If we are exporting expenses then...
		if(sDataSource.equals(Constants.API_RESOURCE_ITEM_EXPENSE_ENTRIES)){
			// Now we need to find out if the selected fields are just Expense Sheet items or if there are any Expense Entry items selected. Loop through the 
			// fields to see if any of them have the Top Level Parent item set to ExpenseEntry...
			for (CFieldItem fiFieldItem : alFields) {
				// If the current field's root item is ExpenseEntry then...
				if(fiFieldItem.getRootElementName().equals(CResourceHelper.MAIN_ELEMENT_NAME_FOR_EXPENSE_ENTRIES)) { return true; }
			} // End of the for (CFieldItem fiFieldItem : alFields) loop.
		} // End if(sDataSource.equals(Constants.API_RESOURCE_ITEM_EXPENSE_ENTRIES))
		
		
		// Tell the caller we are either not exporting expenses or the expenses we do not have any expense entries to export
		return false;
	}
 
	
	// Returns 'true' if there were no errors and 'false' if there were
	private boolean exportData(String sURI, boolean bExportDataWithExpenseEntryItems, ArrayList<CFieldItem> alFields, String sMainElementName, 
			IExportFormatter fFormatter, BufferedWriter bwWriter) {
		// Request the page of data from the REST API. If no data was returned then exit now 
		APIRequestResult arResult = CRESTAPIHelper.makeAPIRequest(sURI, "GET", null, m_UILogic.getConsumerSecret(), m_UILogic.getDataAccessToken());
		Document xdDoc = arResult.getResultDocument();
		if(xdDoc == null) { return true; }
		

		// Grab the root element and get the Next Page URI from it
		Element xeDocElement = xdDoc.getDocumentElement();
		String sNextPageURI = CXMLHelper.getChildNodeValue(xeDocElement, Constants.NEXT_PAGE_URI, Constants.URI_NOT_AVAILABLE);

		
		Element xeMainElement = null;
		
		// Grab the list of Main Element nodes (e.g. if Clients were requested then the main element node is 'Client') and loop through them...
		NodeList xnlMainElements = xeDocElement.getElementsByTagName(sMainElementName);
		int iNodeListCount = xnlMainElements.getLength();
		for(int iNodeListCounter = 0; iNodeListCounter < iNodeListCount; iNodeListCounter++) {
			// Grab the current Main Element and clear the values from the Fields (just in case the previous loop had data that this loop does not - especially
			// important when dealing with the expense entry export)
			xeMainElement = (Element)xnlMainElements.item(iNodeListCounter);
			clearFieldValues(alFields);
			
			
			// If we are exporting expenses and there are one or more expense entry fields selected for export (we don't want to do the following if we don't have
			// to because requesting the expense entry data requires another call to the API - the more HTTP calls you do, the slower the code)
			if(bExportDataWithExpenseEntryItems) { 
				// Load in the field data for the Expense Sheet/Entries requested (exits this function if there was an issue)
				if(!exportDataForExpenseEntries(CXMLHelper.getChildNodeValue(xeMainElement, Constants.GET_EXPENSE_ENTRY_ITEMS_URI), alFields, fFormatter, bwWriter)) { 
					return false; 
				} // End if
			} 
			else { // We're either not doing an expense export OR the current expense sheet has no expense entries...
				// Loop through the requested fields grabbing each field's value...
				for (CFieldItem fiFieldItem : alFields) { grabFieldItemValueFromElement(xeMainElement, fiFieldItem); }
				
				// Pass the field values off to the formatter class to have them saved to the file appropriately. If there was an issue then exit now.
				if(!fFormatter.WriteData(alFields, bwWriter)) { return false; }
			} // End if(!sGetExpenseEntryItemsURI.equals(Constants.URI_NOT_AVAILABLE))
		} // End of the for(int iNodeListCounter = 0; iNodeListCounter < iNodeListCount; iNodeListCounter++) loop.
		
		
		// Make sure the values are cleared for the fields just in case we are done exporting data (if we don't do this, when the Add/Remove Fields pop-up is
		// displayed, it checks to see if the available field matches any of the items we have already added here - the comparison calls CFieldItem.equals which
		// checks all properties including the Value. The Available fields have an empty value so if there are values left here, the CFieldItem.equals function
		// will return false)
		clearFieldValues(alFields);
  
				
		// If there is another page of data to load then...
		if(!sNextPageURI.equals(Constants.URI_NOT_AVAILABLE)) {
			// Load in the next page of data
			return exportData(sNextPageURI, bExportDataWithExpenseEntryItems, alFields, sMainElementName, fFormatter, bwWriter); 
		} 
		else { // No more data to load (we're done)...
			// All is OK
			return true;
		} // End if(!sNextPageURI.equals(Constants.URI_NOT_AVAILABLE))
	}
	
	
	// Clears the values in each field so that a previous export doesn't contaminate the current export 
	private void clearFieldValues(ArrayList<CFieldItem> alFields) {
		// Loop through the fields clearing the values 
		for (CFieldItem fiFieldItem : alFields) { fiFieldItem.setValue(""); }
	}
		
	
	// Similar to the 'exportData' function but only for Expense Sheets with Expense Entries (not called by Expense Sheets that have no expense entries)
	private boolean exportDataForExpenseEntries(String sURI, ArrayList<CFieldItem> alFields, IExportFormatter fFormatter, BufferedWriter bwWriter) {
		// Request the page of data from the REST API (data should always be returned because the GetExpenseEntryItemsURI field should hold 'N/A' if the expense
		// sheet has no entries and this function is only called if the value is not 'N/A')
		APIRequestResult arResult = CRESTAPIHelper.makeAPIRequest(sURI, "GET", null, m_UILogic.getConsumerSecret(), m_UILogic.getDataAccessToken());	
		Element xeDocElement = arResult.getResultDocument().getDocumentElement();
		
		
		// --------------
		// Step 1: Expense Sheet data
		// --------------
		// Grab the list of Expense Sheet Element nodes and then grab the first node (there is only the one Expense Sheet element node in this case since we've 
		// requested a specific expense sheet) 
		NodeList xnlMainElements = xeDocElement.getElementsByTagName(CResourceHelper.MAIN_ELEMENT_NAME_FOR_EXPENSE_SHEETS);
		Element xeMainElement = (Element)xnlMainElements.item(0);
		
		// Loop through the requested fields loading in the Expense Sheet field information...
		for (CFieldItem fiFieldItem : alFields) {
			// If the current field is an Expense Sheet field then grab it's values from the expense sheet element
			if(fiFieldItem.getRootElementName().equals(CResourceHelper.MAIN_ELEMENT_NAME_FOR_EXPENSE_SHEETS)){  
				grabFieldItemValueFromElement(xeMainElement, fiFieldItem);
			} // End if(fiFieldItem.getRootElementName().equals(Constants.MAIN_ELEMENT_NAME_FOR_EXPENSE_SHEETS))
		} // End of the for (CFieldItem fiFieldItem : alFields) loop.
				
		
		// --------------
		// Step 2: Expense Entry data
		// --------------
		// We know that we have at least one Expense Entry field to fill (this function would not be called if that wasn't the case) so grab the ExpenseEntry
		// node list and loop through it...
		xnlMainElements = xeMainElement.getElementsByTagName(CResourceHelper.MAIN_ELEMENT_NAME_FOR_EXPENSE_ENTRIES);
		int iExpenseEntryCount = xnlMainElements.getLength();
		for(int iExpenseEntryCounter = 0; iExpenseEntryCounter < iExpenseEntryCount; iExpenseEntryCounter++) {
			// Grab the current Main Element (ExpenseEntry in this case)
			xeMainElement = (Element)xnlMainElements.item(iExpenseEntryCounter);
		
			// Loop through the requested fields loading in the Expense Entry field information...
			for (CFieldItem fiFieldItem : alFields) {
				// If the current field is an Expense Entry field then grab it's values from the current expense entry element
				if(fiFieldItem.getRootElementName().equals(CResourceHelper.MAIN_ELEMENT_NAME_FOR_EXPENSE_ENTRIES)){  
					grabFieldItemValueFromElement(xeMainElement, fiFieldItem);
				} // End if(fiFieldItem.getRootElementName().equals(Constants.MAIN_ELEMENT_NAME_FOR_EXPENSE_ENTRIES))
			} // End of the for (CFieldItem fiFieldItem : alFields) loop.

			
 			// Pass the field values off to the formatter class to have them saved to the file appropriately. If there was an issue then exit now.
			if(!fFormatter.WriteData(alFields, bwWriter)) { return false; }
		} // End of the for(int iExpenseEntryCounter = 0; iExpenseEntryCounter < iExpenseEntryCount; iExpenseEntryCounter++) loop.
		
				
		// If the sheet had no expense entries then...(the loop above would not have written out any sheet data)
		if(iExpenseEntryCount == 0){
			// Make sure that at least the Sheet's information is output. If there's an issue with the save then exit now.
			if(!fFormatter.WriteData(alFields, bwWriter)) { return false; }
		} // End if(iExpenseEntryCount == 0)
				
		
		// All is OK
		return true;
	}
		
	
	// Helper to grab the value for a field
	private void grabFieldItemValueFromElement(Element xeMainElement, CFieldItem fiFieldItem) {
		// By default the parent element is the main element
		Element xeFieldParentElement = xeMainElement;
					
		// If the current field is NOT at the root of the Main Element then...
		if(!fiFieldItem.isAtRootElementLevel()) {
			// Grab the current field's parent element. If it was found then grab the parent element from the node list					
			NodeList xnlFieldParentElements = xeMainElement.getElementsByTagName(fiFieldItem.getParentElementName());
			if(xnlFieldParentElements.getLength() > 0) { xeFieldParentElement = (Element)xnlFieldParentElements.item(0); }
		} // End if(!fiFieldItem.isAtRootElementLevel())
		
		// Grab the field item's value
		fiFieldItem.setValue(CXMLHelper.getChildNodeValue(xeFieldParentElement, fiFieldItem.getElementName()));	
	}
}
