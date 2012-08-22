package com.dovico.importexporttool;

import com.dovico.commonlibrary.CXMLHelper;
import org.w3c.dom.Element;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



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
	public void setValue(String sValue, boolean bClearingValue) throws Exception { 
		// Only check if we're not being called to clear the value AND if we're a date column then...(make sure the value passed in is formatted correctly)
		if(!bClearingValue && m_iFieldType.equals(FieldItemType.Date)) { validateDate(sValue); }
		
		m_sFieldValue = sValue;	
	}
	public String getValue() { return m_sFieldValue; }
	
	private void validateDate(String sValue) throws Exception{
		String sErrorMsg = "";
		
		// Dates are expected in the format: yyyy-MM-dd
		SimpleDateFormat sdfDate = new SimpleDateFormat(Constants.XML_DATE_FORMAT);
		try {
			// Parse the date string to ensure it's in the proper format
			Date dtDate = sdfDate.parse(sValue);
			if(!validateDateRange(dtDate)){ sErrorMsg = ("The date '"+ sValue +"' does not fall within the required date range of 1900-01-01 to 2199-12-31"); }
		} catch (ParseException e) { sErrorMsg = ("The date '"+ sValue +"' must be in the format yyyy-MM-dd."); }

		
		if(!sErrorMsg.isEmpty()){ throw new Exception(sErrorMsg); }
	
	}

	private boolean validateDateRange(Date dtValue){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 1900);
		cal.set(Calendar.MONTH, 0);//zero-based!
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.HOUR, 0); // Zero out the Hour
		cal.set(Calendar.MINUTE, 0); // Zero out the Minute
		cal.set(Calendar.SECOND, 0); // Zero out the Second
		cal.set(Calendar.MILLISECOND, 0); // Zero out the Millisecond		
		Date dtMinSysDate = cal.getTime();
		
		cal.set(Calendar.YEAR, 2199);
		cal.set(Calendar.MONTH, 11);//zero-based!
		cal.set(Calendar.DATE, 31);		
		Date dtMaxSysDate = cal.getTime();
		
		// Tell the calling function if the date falls outside of the system date range or not
		return !(dtValue.before(dtMinSysDate) || dtValue.after(dtMaxSysDate));
	}
	
	
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
		return "<FieldItem><Order>"+ Integer.toString(m_iOrder) + 
				"</Order><Caption>" + CXMLHelper.encodeTextForElement(m_sCaption) +
				"</Caption><ElementName>"+ CXMLHelper.encodeTextForElement(m_sElementName) +
				"</ElementName><FieldType>" + getFieldTypeAsString() +
				"</FieldType><IsRoot>"+ (m_bIsAtRootElementLevel ? "T" : "F") + 
				"</IsRoot><ParentElementName>"+ CXMLHelper.encodeTextForElement(m_sParentElementName) +
				"</ParentElementName><RootElementName>" + CXMLHelper.encodeTextForElement(m_sRootElementName)+
				"</RootElementName></FieldItem>";
	}
	
	private String getFieldTypeAsString(){
		if(m_iFieldType == FieldItemType.Number) { return "N"; }// Number
		else if(m_iFieldType == FieldItemType.Date){  return "D"; }// Date
		else { return "S"; } // String
	}
	private FieldItemType getFieldTypeFromString(String sValue){		
		if(sValue.equals("N")) { return FieldItemType.Number; }//Number
		else if(sValue.equals("D")){ return FieldItemType.Date; }// date
		else{ return FieldItemType.String; } // String
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
	}
}
