package com.dovico.importexporttool;

import org.w3c.dom.Element;

import com.dovico.commonlibrary.CXMLHelper;

public class CFieldItemMap {
	private CFieldItem m_fiSourceItem = null;
	private CFieldItem m_fiDestinationItem = null;
	
	public CFieldItemMap(CFieldItem fiSourceItem, CFieldItem fiDestinationItem) {
		m_fiSourceItem = fiSourceItem;
		m_fiDestinationItem = fiDestinationItem;
	}

	// Overloaded constructor for when we're pulling the saved state data back in when re-opening the view
	public CFieldItemMap(Element xeElement){ setFieldsFromElementValues(xeElement); }
	
	public CFieldItem getSourceItem() { return m_fiSourceItem; }
	public CFieldItem getDestinationItem() { return m_fiDestinationItem; }
	
	
	public String toXML(){
		return ("<FieldItemMap><Source>"+ m_fiSourceItem.toXML() + "</Source><Destination>" + m_fiDestinationItem.toXML() +"</Destination></FieldItemMap>");
	}
	private void setFieldsFromElementValues(Element xeElement){
		// Grab the 'Source' element and from that pass the constructor the 'FieldItem' element
		Element xeRoot = (Element)xeElement.getElementsByTagName("Source").item(0);		
		m_fiSourceItem = new CFieldItem((Element)xeRoot.getElementsByTagName("FieldItem").item(0));
		
		// Grab the 'Destination' element and from that pass the constructor the 'FieldItem' element
		xeRoot = (Element)xeElement.getElementsByTagName("Destination").item(0);
		Element fieldItem =(Element)xeRoot.getElementsByTagName("FieldItem").item(0);
		if (CXMLHelper.getChildNode(fieldItem, "Source") != null) {
			m_fiDestinationItem = new CIdFieldItem(fieldItem);
		} else {
			m_fiDestinationItem = new CFieldItem(fieldItem);
		}
	}

	@Override
	public String toString() {
		return this.m_fiSourceItem != null ? this.m_fiSourceItem.toString() : "" + " -> " + this.m_fiDestinationItem.toString();
	}
}
