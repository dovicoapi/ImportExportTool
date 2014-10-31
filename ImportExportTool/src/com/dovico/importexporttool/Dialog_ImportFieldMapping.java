package com.dovico.importexporttool;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ListSelectionModel;


public class Dialog_ImportFieldMapping extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private final JPanel contentPanel = new JPanel();
	
	private DefaultListModel m_lmSourceModel = null;
	private JList m_lstSource = null;
			
	private DefaultListModel m_lmDestinationModel = null;
	private JList m_lstDestination = null; 
	
	private DefaultTableModel m_tmMappingModel = null;
	private JTable m_tblMapping = null;

	
	// Flag and helper method to know if this dialog was closed as a result of the OK button (if so, all validated correctly) or the Cancel button	
	private boolean m_bOKButtonPressed = false;
	
	
	
	// Main entry point for this dialog
	public static void main(String[] args) {
		try {
			Dialog_ImportFieldMapping dialog = new Dialog_ImportFieldMapping();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	// Default constructor
	public Dialog_ImportFieldMapping() {
		setModal(true);
		setResizable(false);
		setTitle("Add/Remove Mapping for Import");
		setBounds(100, 100, 555, 472);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		
		// Source controls:
		JLabel lblSource = new JLabel("Source:");
		lblSource.setFont(new Font("Arial", Font.PLAIN, 11));
		lblSource.setBounds(10, 11, 121, 14);
		contentPanel.add(lblSource);
		
		m_lmSourceModel = new DefaultListModel();
		m_lstSource = new JList(m_lmSourceModel);
		m_lstSource.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		m_lstSource.setFont(new Font("Arial", Font.PLAIN, 11));
				
		// List controls will expand/collapse as items are added/removed...they will not scroll. In order to get scrolling, as well as a UI that doesn't keep
		// growing/shrinking as it's used, we add the list to a scroll pane and add the scroll pane to the container.
		JScrollPane spSource = new JScrollPane(m_lstSource);
		spSource.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		spSource.setBounds(10, 28, 212, 155);
		contentPanel.add(spSource);

		
		// Destination controls:
		JLabel lblDestination = new JLabel("Destination:");
		lblDestination.setFont(new Font("Arial", Font.PLAIN, 11));
		lblDestination.setBounds(243, 11, 97, 14);
		contentPanel.add(lblDestination);
		
		m_lmDestinationModel = new DefaultListModel();
		m_lstDestination = new JList(m_lmDestinationModel);
		m_lstDestination.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		m_lstDestination.setFont(new Font("Arial", Font.PLAIN, 11));
				
		// List controls will expand/collapse as items are added/removed...they will not scroll. In order to get scrolling, as well as a UI that doesn't keep
		// growing/shrinking as it's used, we add the list to a scroll pane and add the scroll pane to the container.
		JScrollPane spDestination = new JScrollPane(m_lstDestination);
		spDestination.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		spDestination.setBounds(243, 28, 212, 155);
		contentPanel.add(spDestination);
				
		JButton cmdMap = new JButton("Map");
		cmdMap.setFont(new Font("Arial", Font.PLAIN, 11));
		cmdMap.setBounds(465, 28, 71, 23);
		cmdMap.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent arg0) { OnClick_cmdMap(); } 
		});
		contentPanel.add(cmdMap);
		
		
		// Mapping controls
		JLabel lblMapping = new JLabel("Mapping:");
		lblMapping.setFont(new Font("Arial", Font.PLAIN, 11));
		lblMapping.setBounds(10, 202, 121, 14);
		contentPanel.add(lblMapping);
		
		m_tmMappingModel = new DefaultTableModel(getMappingColumnNames(), 0);
		m_tblMapping = new JTable(m_tmMappingModel);
		m_tblMapping.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		m_tblMapping.setShowGrid(false);
		m_tblMapping.setFont(new Font("Arial", Font.PLAIN, 11));		
		
		JScrollPane spMapping = new JScrollPane(m_tblMapping);
		m_tblMapping.setFillsViewportHeight(true);
		spMapping.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		spMapping.setBounds(10, 219, 445, 155);
		contentPanel.add(spMapping);
				
		JButton cmdRemoveMap = new JButton("Remove");
		cmdRemoveMap.setFont(new Font("Arial", Font.PLAIN, 11));
		cmdRemoveMap.setBounds(465, 217, 71, 23);
		cmdRemoveMap.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent arg0) { OnClick_cmdRemoveMap(); } 
		});
		contentPanel.add(cmdRemoveMap);
		
		JLabel lblAreRequired = new JLabel("* required fields for the import");
		lblAreRequired.setFont(new Font("Arial", Font.PLAIN, 11));
		lblAreRequired.setBounds(10, 386, 192, 14);
		contentPanel.add(lblAreRequired);
				
		
		// The OK and Cancel buttons
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		{
			JButton cmdOK = new JButton("OK");
			cmdOK.setFont(new Font("Arial", Font.PLAIN, 11));
			cmdOK.setActionCommand("OK");
			cmdOK.addActionListener(new ActionListener() { 
				public void actionPerformed(ActionEvent arg0) { OnClick_cmdOK(); } 
			});
			buttonPane.add(cmdOK);
						
			getRootPane().setDefaultButton(cmdOK);
		}
		{
			JButton cmdCancel = new JButton("Cancel");
			cmdCancel.setFont(new Font("Arial", Font.PLAIN, 11));
			cmdCancel.setActionCommand("Cancel");
			cmdCancel.addActionListener(new ActionListener() { 
				public void actionPerformed(ActionEvent arg0) { OnClick_cmdCancel(); } 
			});
			buttonPane.add(cmdCancel);
		}
	}
	
	
	// Returns the names for the columns in the Mapping table
	private String[] getMappingColumnNames() {
		String[] arrColNames = { "Source", "Destination" };
		return arrColNames;
	}
	
	
	// Call this function before you show this dialog so that the lists are populated properly based on the Selected file and currently selected items	
	public void setSourceColumnsDestinationAndMapping(String consumerSecret, String userToken, ArrayList<CFieldItem> alColumnsFromTheFile, String sDestination, ArrayList<CFieldItemMap> alCurrentMappings){
		// Fill the source list with the columns that are in the file, fill the destination list with the fields that match the selected destination, and then
		// fill the mapping table with the current mappings
		fillSourceList(alColumnsFromTheFile);
		fillDestinationList(sDestination, consumerSecret, userToken);
		fillMappingTable(alCurrentMappings);				
	}
	
	
	// Helper that fills the Source list with the Columns from the file
	private void fillSourceList(ArrayList<CFieldItem> alColumnsFromTheFile) {
		// Loop through the file's columns adding them to the Source list
		for (CFieldItem fiFieldItem : alColumnsFromTheFile) { m_lmSourceModel.addElement(fiFieldItem); }
	}
	
	
	// Helper that fills the Destination list with the fields available for the selected destination
	private void fillDestinationList(String sDestination, String consumerSecret, String userToken) {
		// Create a Generic list for the Destination fields. Get the fields based on the specified destination passed in
		ArrayList<CFieldItem> alDestinationFields = new ArrayList<CFieldItem>();
		CResourceHelper.getAPIFieldsForResource(sDestination, consumerSecret, userToken, false, alDestinationFields);
		
		// Loop through the fields adding them to the Destination list
		for (CFieldItem fiFieldItem : alDestinationFields) { m_lmDestinationModel.addElement(fiFieldItem); }
	}
	
		
	// Fill the mapping table with the current mappings
	private void fillMappingTable(ArrayList<CFieldItemMap> alCurrentMappings) {
		// Loop through the mappings adding them to the table...
		for (CFieldItemMap fiFieldItemMap : alCurrentMappings) { 
			m_tmMappingModel.addRow(new Object[] { fiFieldItemMap.getSourceItem(), fiFieldItemMap.getDestinationItem() });
		} // End of the for (CFieldItemMap fiFieldItemMap : alCurrentMappings) loop.
	}
	
	
	// User clicked on the Map button
	private void OnClick_cmdMap() {
		// Ensure we have a selection in the Source list
		int iSelectedSourceIndex = m_lstSource.getSelectedIndex();
		if(iSelectedSourceIndex == -1){
			JOptionPane.showMessageDialog(null, "Please select an item from the Source list", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		} // End if(iSelectedSourceIndex == -1)
		
		// Ensure we have a selection in the Destination list
		int iSelectedDestinationIndex = m_lstDestination.getSelectedIndex();
		if(iSelectedDestinationIndex == -1){
			JOptionPane.showMessageDialog(null, "Please select an item from the Destination list", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		} // End if(iSelectedDestinationIndex == -1)
		
		
		// NOTE:	When it comes to POST/PUT requests, the order of the fields matters. If they are supplied out of order, the REST API's framework will ignore
		// 			the values! We need to ensure the rows are added to the grid in the proper order based on the selected destination item's order value.
		CFieldItem fiSelectedDestination = (CFieldItem)m_lmDestinationModel.get(iSelectedDestinationIndex);
		CFieldItem fiMapDestination = null;
		int iOrder = fiSelectedDestination.getOrder();
		int iInsertRowIndex = 0;
		
		// Loop through the current rows in the Mapping table...
		int iRowCount = m_tmMappingModel.getRowCount();
		for(int iRowIndex = 0; iRowIndex < iRowCount; iRowIndex++) {
			// Grab the destination object from the current row
			fiMapDestination = (CFieldItem)m_tmMappingModel.getValueAt(iRowIndex, 1);
			
			// If the order of the item we're about to add to the grid is less than the order of the item that we're looking at then...
			if(iOrder < fiMapDestination.getOrder()) {
				// Set the index to be the current row's index so the new row gets positioned before this one. Exit the loop.
				iInsertRowIndex = iRowIndex;
				break;
			} else { // The grid item's order is either equal to or less than the order we're looking for...
				// Adjust the index to be after the current row (just in case this was the last row in the grid)
				iInsertRowIndex = (iRowIndex + 1); 
			} // End if(iOrder < fiMapDestination.getOrder())			
		} // End of the for(int iRowIndex = 0; iRowIndex < iRowCount; iRowIndex++) loop.
		
		// Insert the mapping row at the proper index
		m_tmMappingModel.insertRow(iInsertRowIndex, new Object[] { m_lmSourceModel.get(iSelectedSourceIndex), fiSelectedDestination });
	}
	
	
	// User clicked on the Remove Map button
	private void OnClick_cmdRemoveMap() {
		int iSelectedRowIndex = m_tblMapping.getSelectedRow();
		if(iSelectedRowIndex == -1){
			JOptionPane.showMessageDialog(null, "Please select an item from the Mapping table", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		} // End if(iSelectedRowIndex == -1)
		
		// Remove the selected row
		m_tmMappingModel.removeRow(iSelectedRowIndex);
	}
	
	
	
	// User clicked on the OK button
	@SuppressWarnings("unchecked")
	private void OnClick_cmdOK() {		
		// If no fields have been mapped then...
		if(m_tmMappingModel.getRowCount() == 0) {
			JOptionPane.showMessageDialog(null, "Please map at least one field for import", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		} // End if(m_tmMappingModel.getRowCount() == 0)
		
		for(CFieldItem field : (ArrayList<CFieldItem>)java.util.Collections.list(m_lmDestinationModel.elements())) {
			if (field.isRequired()) {
				boolean found = false;
				for(int iRowIndex = 0; iRowIndex < m_tmMappingModel.getRowCount(); iRowIndex++) {
					CFieldItem fiMapDestination = (CFieldItem)m_tmMappingModel.getValueAt(iRowIndex, 1);
					if (fiMapDestination.getElementName().equals(field.getElementName())) {
						found = true;
						break;
					}
				}
				if (!found) {
					JOptionPane.showMessageDialog(null, String.format("%s is a required field and must be mapped", field.getCaption()), "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
		}
		
		// Flag that the OK button has been pressed and then hide this window so that the caller can now continue processing 
		m_bOKButtonPressed = true;
		this.setVisible(false); 
	}
	
	
	// User clicked on the Cancel button
	private void OnClick_cmdCancel() {
		// Flag that the OK button has not been pressed and then hide this window so that the caller can now continue processing
		m_bOKButtonPressed = false;
		this.setVisible(false); 
	}
	

	// Window that displayed this dialog can use this function to determine if this window was closed as a result of the OK or Cancel button click
	public boolean getClosedByOKButton() { return m_bOKButtonPressed; }
	
	
	// Returns the list of selected Mappings
	public void getSelectedMappings(ArrayList<CFieldItemMap> alReturnMappings) {
		CFieldItem fiSource = null;
		CFieldItem fiDestination = null;
		
		// Loop through the items in the Mapping table...
		int iRowCount = m_tmMappingModel.getRowCount();
		for(int iRowIndex = 0; iRowIndex < iRowCount; iRowIndex++) {
			// Grab the source and destination objects from the current row
			fiSource = (CFieldItem)m_tmMappingModel.getValueAt(iRowIndex, 0);
			fiDestination = (CFieldItem)m_tmMappingModel.getValueAt(iRowIndex, 1);
			
			// Add the current mapping to the return ArrayList
			alReturnMappings.add(new CFieldItemMap(fiSource, fiDestination)); 
		} // End of the for(int iRowIndex = 0; iRowIndex < iRowCount; iRowIndex++) loop.
	}
}
