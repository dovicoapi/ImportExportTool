package com.dovico.importexporttool;

import com.dovico.commonlibrary.CXMLHelper;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.sql.Savepoint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;



public class CFieldItem {
	public enum FieldItemType { String, Number, Date, MultipleChoice, ExclusiveChoice };
	
	private int m_iOrder = 0; // Only an issue when POST/PUT data because, at that point, the order of the fields matters 
	private String m_sCaption = ""; // Display friendly name (e.g. Project ID rather than ProjectID for example)
	private String m_sElementName = "";
	private FieldItemType m_iFieldType = FieldItemType.String;
	private String m_sFieldValue = ""; // Value stored here when parsed from file going to REST API. Value also stored here when going from REST API to file.
	private boolean m_bIsAtRootElementLevel = true;
	private String m_sParentElementName = "";
	private String m_sRootElementName = "";
	private CCustomFieldInfo m_custFieldInfo;
	private boolean m_required;
	private int m_stringLength = 250;
	
	// Overloaded constructor (simply calls our other overloaded constructor - indicates that this element is at the root of the main element that we will be 
	// parsing)
	public CFieldItem(int iOrder, String sCaption, String sElementName, FieldItemType iFieldType, boolean required) { this(iOrder, sCaption, sElementName, iFieldType, true, "", "", required); }

	public CFieldItem(int iOrder, String sCaption, String sElementName, FieldItemType iFieldType, CCustomFieldInfo info, boolean required) { 
		this(iOrder, sCaption, sElementName, iFieldType, true, "", "", required);
		this.m_custFieldInfo = info;
		this.m_bIsAtRootElementLevel = false;
	}
	
	
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
	public CFieldItem(int iOrder, String sCaption, String sElementName, FieldItemType iFieldType, boolean bIsAtRootElementLevel, String sParentElementName, boolean required) {
		// Call the overloaded version of this constructor to handle the setting of the field values
		this(iOrder, sCaption, sElementName, iFieldType, bIsAtRootElementLevel, sParentElementName, "", required);
	}
	
	
	// Overloaded constructor that accepts a Root Element Name value
	public CFieldItem(int iOrder, String sCaption, String sElementName, FieldItemType iFieldType, boolean bIsAtRootElementLevel, String sParentElementName, 
		String sRootElementName, boolean required) {
		m_iOrder = iOrder;
		m_sCaption = sCaption;
		m_sElementName = sElementName;
		m_iFieldType = iFieldType;
		m_bIsAtRootElementLevel = bIsAtRootElementLevel;
		m_sParentElementName = sParentElementName;
		m_sRootElementName = sRootElementName;
		m_required = required;
	}
	
	// Overloaded constructor for when we're pulling the saved state data back in when re-opening the view
	public CFieldItem(Element xeElement){ setFieldsFromElementValues(xeElement); }
	
	
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
	public void setValue(String sValue, boolean bClearingValue) throws FormattingException { 
		// Only check if we're not being called to clear the value AND if we're a date column then...(make sure the value passed in is formatted correctly)
		if(!bClearingValue) {
			switch(this.m_iFieldType) {
				case Number:
					try {
						Validations.isDouble(sValue);
					} catch (ParseException ex) {
						HandleNumberException(sValue);
					}
					break;
				case String:
					//Validations.length(sValue, m_sCaption);
					if (sValue.length() > m_stringLength) {
						sValue = sValue.substring(0, m_stringLength);
					}
					break;
				case Date:
					Validations.isDateInRange(sValue, m_sCaption, this.m_custFieldInfo != null && m_custFieldInfo.getType() == FieldItemType.Date);
					break;
				default:
					if(m_custFieldInfo != null) {
						m_custFieldInfo.validate(this.m_sCaption, sValue);
					}
					break;
			}
		}

		m_sFieldValue = sValue;	
	}
	
	protected void HandleNumberException(String sValue) throws FormattingException {
		throw new FormattingException("%s must be a number in the format 9999.99 (got %s)", this.m_sCaption, sValue);
	}

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
	
	public String toXML(){
		String result = "<FieldItem>";
		result += addFieldsToXml();
		
		if (m_custFieldInfo != null) {
			result += m_custFieldInfo.toXML();
		}
		
		result += "</FieldItem>";
				
		return result;
	}

	protected String addFieldsToXml() {
		return "<Order>"+ Integer.toString(m_iOrder) + 
				"</Order><Caption>" + CXMLHelper.encodeTextForElement(m_sCaption) +
				"</Caption><ElementName>"+ CXMLHelper.encodeTextForElement(m_sElementName) +
				"</ElementName><FieldType>" + getFieldTypeAsString() +
				"</FieldType><IsRoot>"+ (m_bIsAtRootElementLevel ? "T" : "F") + 
				"</IsRoot><ParentElementName>"+ CXMLHelper.encodeTextForElement(m_sParentElementName) +
				"</ParentElementName><RootElementName>" + CXMLHelper.encodeTextForElement(m_sRootElementName)+
				"</RootElementName><Required>" + m_required + "</Required>"
				+"<StringLength>"+ Integer.toString(m_stringLength) + "</StringLength>";
	}
	
	private String getFieldTypeAsString(){
		switch(m_iFieldType) {
		case Number:
			return "N";
		case String:
			return "S";
		case Date:
			return "D";
		case ExclusiveChoice:
			return "X";
		default:
			return "M";
		}
	}
	
	private FieldItemType getFieldTypeFromString(String sValue){		
		switch(sValue.charAt(0)) {
		case 'N':
			return FieldItemType.Number;
		case 'S':
			return FieldItemType.String;
		case 'D':
			return FieldItemType.Date;
		case 'X':
			return FieldItemType.ExclusiveChoice;
		default:
			return FieldItemType.MultipleChoice;
		}
	}

	private void setFieldsFromElementValues(Element xeElement){
		// Get the field values from the element
		m_iOrder = Integer.parseInt(CXMLHelper.getChildNodeValue(xeElement, "Order", "0"), 10);
		m_sCaption = CXMLHelper.getChildNodeValue(xeElement, "Caption", "");
		m_sElementName = CXMLHelper.getChildNodeValue(xeElement, "ElementName", "");
		m_iFieldType = getFieldTypeFromString(CXMLHelper.getChildNodeValue(xeElement, "FieldType", "S"));
		m_bIsAtRootElementLevel = CXMLHelper.getChildNodeValue(xeElement, "IsRoot", "T").equals("T");
		m_sParentElementName= CXMLHelper.getChildNodeValue(xeElement, "ParentElementName", "");
		m_sRootElementName = CXMLHelper.getChildNodeValue(xeElement, "RootElementName", "");
		m_required = Boolean.parseBoolean(CXMLHelper.getChildNodeValue(xeElement, "Required"));
		m_stringLength = Integer.parseInt(CXMLHelper.getChildNodeValue(xeElement, "StringLength", "250"));
		Node customFields = CXMLHelper.getChildNode(xeElement, "CustomField");
		if (customFields != null) {
			m_custFieldInfo = CCustomFieldInfo.fromXML(xeElement);
		}
	}

	public boolean isCustomTemplate() {
		return m_custFieldInfo != null;
	}
	
	public boolean isRequired() {
		return m_required || (isCustomTemplate() && this.m_custFieldInfo.isRequired());
	}

	public CCustomFieldInfo getCustomInfo() {
		return m_custFieldInfo;
	}

	public String getCaption() {
		return m_sCaption;
	}
	
	public CFieldItem setStringLength(int length) {
		m_stringLength = length;
		return this;
	}
}
