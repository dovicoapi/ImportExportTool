package com.dovico.importexporttool;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JList;
import javax.swing.border.BevelBorder;


public class Dialog_ExportFields extends JDialog {
	private static final long serialVersionUID = 1L;
	
	private final JPanel contentPanel = new JPanel();

	private DefaultListModel m_lmAvailableFieldsModel = null;
	private JList m_lstAvailableFields = null; 
	
	private DefaultListModel m_lmSelectedFieldsModel = null;
	private JList m_lstSelectedFields = null;
	

	// Flag and helper method to know if this dialog was closed as a result of the OK button (if so, all validated correctly) or the Cancel button	
	private boolean m_bOKButtonPressed = false;
	
	
	
	// Main entry point for this dialog
	public static void main(String[] args) {
		try {
			Dialog_ExportFields dialog = new Dialog_ExportFields();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	// Default constructor
	public Dialog_ExportFields() {
		setModal(true);
		setResizable(false);
		setTitle("Add/Remove Fields for Export");
		setBounds(100, 100, 515, 233);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		
		// Available Fields controls
		JLabel lblAvailableFields = new JLabel("Available fields:");
		lblAvailableFields.setFont(new Font("Arial", Font.PLAIN, 11));
		lblAvailableFields.setBounds(10, 11, 84, 14);
		contentPanel.add(lblAvailableFields);
		
		m_lmAvailableFieldsModel = new DefaultListModel();
		m_lstAvailableFields = new JList(m_lmAvailableFieldsModel);
		m_lstAvailableFields.setFont(new Font("Arial", Font.PLAIN, 11));
				
		// List controls will expand/collapse as items are added/removed...they will not scroll. In order to get scrolling, as well as a UI that doesn't keep
		// growing/shrinking as it's used, we add the list to a scroll pane and add the scroll pane to the container.
		JScrollPane spAvailableFields = new JScrollPane(m_lstAvailableFields);
		spAvailableFields.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		spAvailableFields.setBounds(10, 28, 190, 124);
		contentPanel.add(spAvailableFields);
		
		
		// Selected Fields controls
		JLabel lblSelectedFields = new JLabel("Selected fields:");
		lblSelectedFields.setFont(new Font("Arial", Font.PLAIN, 11));
		lblSelectedFields.setBounds(270, 11, 96, 14);
		contentPanel.add(lblSelectedFields);
		
		m_lmSelectedFieldsModel = new DefaultListModel();
		m_lstSelectedFields = new JList(m_lmSelectedFieldsModel);
		m_lstSelectedFields.setFont(new Font("Arial", Font.PLAIN, 11));
		
		// List controls will expand/collapse as items are added/removed...they will not scroll. In order to get scrolling, as well as a UI that doesn't keep
		// growing/shrinking as it's used, we add the list to a scroll pane and add the scroll pane to the container.
		JScrollPane spSelectedFields = new JScrollPane(m_lstSelectedFields);
		spSelectedFields.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		spSelectedFields.setBounds(270, 28, 190, 124);
		contentPanel.add(spSelectedFields);


		// Buttons used to move list selections from Available to Selected lists and vice-versa		
		JButton cmdAddSelectedField = new JButton(">");
		cmdAddSelectedField.setFont(new Font("Arial", Font.PLAIN, 11));
		cmdAddSelectedField.setBounds(210, 37, 50, 23);
		cmdAddSelectedField.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent arg0) { OnClick_cmdAddSelectedField(); } 
		});
		contentPanel.add(cmdAddSelectedField);
		
		JButton cmdAddAllFields = new JButton(">>");
		cmdAddAllFields.setFont(new Font("Arial", Font.PLAIN, 11));
		cmdAddAllFields.setBounds(210, 64, 50, 23);
		cmdAddAllFields.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent arg0) { OnClick_cmdAddAllFields(); } 
		});
		contentPanel.add(cmdAddAllFields);
		
		JButton cmdRemoveSelectedField = new JButton("<");
		cmdRemoveSelectedField.setFont(new Font("Arial", Font.PLAIN, 11));
		cmdRemoveSelectedField.setBounds(210, 91, 50, 23);
		cmdRemoveSelectedField.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent arg0) { OnClick_cmdRemoveSelectedField(); } 
		});
		contentPanel.add(cmdRemoveSelectedField);
		
		JButton cmdRemoveAllFields = new JButton("<<");
		cmdRemoveAllFields.setFont(new Font("Arial", Font.PLAIN, 11));
		cmdRemoveAllFields.setBounds(210, 118, 50, 23);
		cmdRemoveAllFields.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent arg0) { OnClick_cmdRemoveAllFields(); } 
		});
		contentPanel.add(cmdRemoveAllFields);
		
		
		// Buttons used to move the Selected field selection Up or Down 
		CUpDownButton cmdMoveSelectedFieldUp = new CUpDownButton(true); 
		cmdMoveSelectedFieldUp.setFont(new Font("Arial", Font.PLAIN, 11));
		cmdMoveSelectedFieldUp.setBounds(470, 64, 30, 23);
		cmdMoveSelectedFieldUp.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent arg0) { OnClick_cmdMoveSelectedFieldUp(); } 
		});
		contentPanel.add(cmdMoveSelectedFieldUp);
		
		CUpDownButton cmdMoveSelectedFieldDown = new CUpDownButton(false);
		cmdMoveSelectedFieldDown.setFont(new Font("Arial", Font.PLAIN, 11));
		cmdMoveSelectedFieldDown.setBounds(470, 91, 30, 23);
		cmdMoveSelectedFieldDown.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent arg0) { OnClick_cmdMoveSelectedFieldDown(); } 
		});
		contentPanel.add(cmdMoveSelectedFieldDown);
		
		
		
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
			cmdCancel.addActionListener(new ActionListener() { 
				public void actionPerformed(ActionEvent arg0) { OnClick_cmdCancel(); } 
			});
			buttonPane.add(cmdCancel);
		}
	}
	
	
	// Call this function before you show this dialog so that the lists can be populated properly based on the Selected Data source and currently selected items	
	public void setDataSourceAndSelectedFields(String sSelectedDataSource, String consumerSecret, String userToken, Object[] arrSelectedFieldValues) {
		// First things first. Add the selected values to the Selected list (so that things are added in the order that they were last selected - if we did this
		// in the following loop then they would get added in the default sort order)
		for (Object objField : arrSelectedFieldValues) { m_lmSelectedFieldsModel.addElement(objField); } 
	
	
		// Create a Generic list for the Available fields for the selected Data Source and then get the field values
		ArrayList<CFieldItem> alAvailableFields = new ArrayList<CFieldItem>();
		CResourceHelper.getAPIFieldsForResource(sSelectedDataSource, consumerSecret, userToken, true, alAvailableFields);
				
		// Loop through the available fields...
		for (CFieldItem fiFieldItem : alAvailableFields) {
			// If the current field is NOT selected then add the field to the Available list (we already filled the Selected fields at the top of this function)
			if(!isAvailableFieldSelected(arrSelectedFieldValues, fiFieldItem)) { m_lmAvailableFieldsModel.addElement(fiFieldItem); } 
		} // End of the for ((CFieldItem fiFieldItem : alAvailableFields) loop.
	}
	
	
	// Helper to check if the available field is part of the selected values list
	private boolean isAvailableFieldSelected(Object[] arrSelectedFieldValues, CFieldItem fiFieldItem) {
		// Loop through the selected fields array... 
		for (Object objSelectedField : arrSelectedFieldValues) {
			// If the current array item matches the available field then the available field is currently selected
			if(((CFieldItem)objSelectedField).equals(fiFieldItem)) { return true; }
		} // End of the for (Object object : arrSelectedFieldValues) loop.
				
		// The available field is not selected because our loop did not find a match 
		return false;
	}
	
	
	// User clicked on the Add Selected Field (>) button
	private void OnClick_cmdAddSelectedField() {
		// We allow multiple selections so loop until there is nothing still selected in the Available Fields list...
		int iSelectedIndex = m_lstAvailableFields.getSelectedIndex();
		while(iSelectedIndex != -1) {
			moveFieldToOtherList(iSelectedIndex, m_lmAvailableFieldsModel, m_lmSelectedFieldsModel);
			iSelectedIndex = m_lstAvailableFields.getSelectedIndex();
		} // End of the while(iSelectedIndex != -1) loop. 
	}
	
	
	// User clicked on the Remove Selected Field (<) button
	private void OnClick_cmdRemoveSelectedField() {
		// We allow multiple selections so loop until there is nothing still selected in the Selected Fields list...
		int iSelectedIndex = m_lstSelectedFields.getSelectedIndex();
		while(iSelectedIndex != -1) {
			moveFieldToOtherList(iSelectedIndex, m_lmSelectedFieldsModel, m_lmAvailableFieldsModel);
			iSelectedIndex = m_lstSelectedFields.getSelectedIndex();
		} // End of the while(iSelectedIndex != -1) loop. 
	}


	// User clicked on the Add All Selected Fields (>>) button
	private void OnClick_cmdAddAllFields() {
		// Loop until we've moved all Available fields to the Selected fields list...
		int iListCount = m_lmAvailableFieldsModel.getSize();
		while(iListCount > 0) {
			// Add the item from the top of our Available fields list to the bottom of the Selected fields list. Remove the top item from our list. 
			m_lmSelectedFieldsModel.addElement(m_lmAvailableFieldsModel.get(0));
			m_lmAvailableFieldsModel.remove(0);
		
			// Adjust our counter to reflect that there is now one less item in our list
			iListCount--;
		} // End of the while(iListCount > 0) loop
	}
	
	
	// User clicked on the Remove All Selected Fields (<<) button
	private void OnClick_cmdRemoveAllFields() {
		// Loop until we've moved all Selected fields to the Available fields list...
		int iListCount = m_lmSelectedFieldsModel.getSize();
		while(iListCount > 0) {
			// Add the item from the top of our Selected fields list to the bottom of the Available fields list. Remove the top item from our list. 
			m_lmAvailableFieldsModel.addElement(m_lmSelectedFieldsModel.get(0));
			m_lmSelectedFieldsModel.remove(0);
		
			// Adjust our counter to reflect that there is now one less item in our list
			iListCount--;
		} // End of the while(iListCount > 0) loop
	}
	
	
	// Helper to move items from one list to another
	private void moveFieldToOtherList(int iSelectedIndex, DefaultListModel lmFrom, DefaultListModel lmTo) {
		// Add the From item to the To list and then remove it from the From list 
		lmTo.addElement(lmFrom.get(iSelectedIndex));			
		lmFrom.remove(iSelectedIndex);
	}
	
	
	// User clicked on the Move Selected Field Up button (only moves the top-most selected item)
	private void OnClick_cmdMoveSelectedFieldUp() {
		// If there is nothing selected then exit now
		int iSelectedIndex = m_lstSelectedFields.getSelectedIndex();
		if(iSelectedIndex == -1) { return; }
		
		// If the selected item is not the first item in the list AND there are at least two items in the list then... 		
		if((iSelectedIndex > 0) && (m_lmSelectedFieldsModel.getSize() >= 2)) {
			// Grab the value at the selected index and the value from the item above it too. 
			Object objValueTop = m_lmSelectedFieldsModel.get((iSelectedIndex - 1));
			Object objValueBottom = m_lmSelectedFieldsModel.get(iSelectedIndex);
			
			// Set the value for the top item to be the bottom item's value. Set the value for the bottom item to be what the top item had for a value.
			m_lmSelectedFieldsModel.set((iSelectedIndex - 1), objValueBottom);
			m_lmSelectedFieldsModel.set(iSelectedIndex, objValueTop);
			
			// Adjust the selected item in the list to follow the item that was just moved
			m_lstSelectedFields.setSelectedIndex((iSelectedIndex - 1));
		} // End if((iSelectedIndex > 0) && (m_lmSelectedFieldsModel.getSize() >= 2))
	}
	
	
	// User clicked on the Move Selected Field Down button
	private void OnClick_cmdMoveSelectedFieldDown() {
		// If there is nothing selected then exit now
		int iSelectedIndex = m_lstSelectedFields.getSelectedIndex();
		if(iSelectedIndex == -1) { return; }
		
		// If the selected item is not the last item in the list AND there are at least two items in the list then...
		int iListCount = m_lmSelectedFieldsModel.getSize();
		if((iSelectedIndex < (iListCount - 1)) && (iListCount >= 2)) {
			// Grab the value at the selected index and the value from the item below it too. 
			Object objValueTop = m_lmSelectedFieldsModel.get(iSelectedIndex);
			Object objValueBottom = m_lmSelectedFieldsModel.get((iSelectedIndex + 1));
			
			// Set the value for the top item to be the bottom item's value. Set the value for the bottom item to be what the top item had for a value.
			m_lmSelectedFieldsModel.set(iSelectedIndex, objValueBottom);
			m_lmSelectedFieldsModel.set((iSelectedIndex + 1), objValueTop);
		
			//Adjust the selected item in the list to follow the item that was just moved
			m_lstSelectedFields.setSelectedIndex((iSelectedIndex + 1));
		} // End if((iSelectedIndex < (iListCount - 1)) && (iListCount >= 2))
	}

	
	// User clicked on the OK button
	private void OnClick_cmdOK() {
		// If no fields have been selected then...
		if(m_lmSelectedFieldsModel.getSize() == 0) {
			JOptionPane.showMessageDialog(null, "Please select at least one field for export", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		} // End if(m_lmSelectedFieldsModel.getSize() == 0)
	
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
	

	// Returns the selected fields (should only be called if getClosedByOKButton returns true)
	public Object[] getSelectedFields() { return m_lmSelectedFieldsModel.toArray(); }
}
