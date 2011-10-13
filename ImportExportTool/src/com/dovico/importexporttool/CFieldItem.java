package com.dovico.importexporttool;


public class CFieldItem {
	public enum FieldItemType { String, Number, Date };
	
	private int m_iOrder = 0; // Only an issue when POST/PUT data because, at that point, the order of the fields matters 
	private String m_sCaption = ""; // Display friendly name (e.g. Project ID rather than ProjectID for example)
	private String m_sElementName = "";
	private FieldItemType m_iFieldType = FieldItemType.String;
	private String m_sFieldValue = ""; // Value stored here when parsed from file going to REST API. Value also stored here when going from REST API to file.
	private boolean m_bIsAtRootElementLevel = true;
	private String m_sParentElementName = "";
	private String m_sRootElementName = "";
	
	
	// Overloaded constructor (simply calls our other overloaded constructor - indicates that this element is at the root of the main element that we will be 
	// parsing)
	public CFieldItem(int iOrder, String sCaption, String sElementName, FieldItemType iFieldType) { this(iOrder, sCaption, sElementName, iFieldType, true, "", ""); }
	
	
	// Overloaded constructor. Used when the field is not at the root of the main element that we will be parsing.
	//
	// For example some return data is in the following form:
	//	<Project>
	//		<ID>123</ID>
	//		<Client>
	//			<ID>100</ID>
	//			...
	//		</Client>
	//		<Name>Project 1</Name>
	//		...
	//	</Project>
	//
	// If we only wanted the Name value then we don't need to call this overload since that value is part of the main element. If we want the Client\ID value,
	// however, then that's where this overload comes into play. We specify the parent element of this item...in this case the parent element would be 'Client'.
	public CFieldItem(int iOrder, String sCaption, String sElementName, FieldItemType iFieldType, boolean bIsAtRootElementLevel, String sParentElementName) {
		// Call the overloaded version of this constructor to handle the setting of the field values
		this(iOrder, sCaption, sElementName, iFieldType, bIsAtRootElementLevel, sParentElementName, "");
	}
	
	
	// Overloaded constructor that accepts a Root Element Name value
	public CFieldItem(int iOrder, String sCaption, String sElementName, FieldItemType iFieldType, boolean bIsAtRootElementLevel, String sParentElementName, 
		String sRootElementName) {
		m_iOrder = iOrder;
		m_sCaption = sCaption;
		m_sElementName = sElementName;
		m_iFieldType = iFieldType;
		m_bIsAtRootElementLevel = bIsAtRootElementLevel;
		m_sParentElementName = sParentElementName;
		m_sRootElementName = sRootElementName;
	}
	
	
	// Returns the order for this field
	public int getOrder() { return m_iOrder; }


	// Tells the caller if the current field is a root element level field or not
	public boolean isAtRootElementLevel() { return m_bIsAtRootElementLevel; }


	// Returns the name of the element that is the parent of this field 
	public String getParentElementName() { return m_sParentElementName; }


	// Returns the root element's name (used by expense sheet/expense entries when we try to tell the field items apart - using parent element name
	// would not work because that will refer to the immediate parent in the event the data is for an item lower than the root element)
	public String getRootElementName() { return m_sRootElementName; }


	// Returns the name of the element for this field
	public String getElementName() { return m_sElementName; }	

		
	// Gets the field type
	public FieldItemType getFieldType() { return m_iFieldType; } 
	
	
	// Sets/Gets the value for this field (when it's been parsed from the file or REST API results)
	public void setValue(String sValue) { m_sFieldValue = sValue; }
	public String getValue() { return m_sFieldValue; }
	
	
	// This class will be used in a List so the following will allow it to show the proper text in the list
	@Override
	public String toString() { return m_sCaption; }
	
	
	// Helper method to check if two CFieldItem objects are really the same one
	public boolean equals(CFieldItem fiOtherFieldItem) {
		// If all of the field values match then...
		if(	(this.m_iOrder == fiOtherFieldItem.m_iOrder) &&
			(this.m_sCaption.equals(fiOtherFieldItem.m_sCaption)) &&
			(this.m_sElementName.equals(fiOtherFieldItem.m_sElementName)) &&
			(this.m_iFieldType == fiOtherFieldItem.m_iFieldType) &&
			(this.m_sFieldValue.equals(fiOtherFieldItem.m_sFieldValue)) &&
			(this.m_bIsAtRootElementLevel == fiOtherFieldItem.m_bIsAtRootElementLevel) &&
			(this.m_sParentElementName.equals(fiOtherFieldItem.m_sParentElementName)) &&
			(this.m_sRootElementName.equals(fiOtherFieldItem.m_sRootElementName)) ) 
		{ 
			return true;
		}
		else // One or more of the field values do not match...
		{ 
			return false; 
		} // End if
	}
}
