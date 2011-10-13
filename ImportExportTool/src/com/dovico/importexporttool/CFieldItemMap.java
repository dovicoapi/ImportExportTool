package com.dovico.importexporttool;


public class CFieldItemMap {
	private CFieldItem m_fiSourceItem = null;
	private CFieldItem m_fiDestinationItem = null;
	
	public CFieldItemMap(CFieldItem fiSourceItem, CFieldItem fiDestinationItem) {
		m_fiSourceItem = fiSourceItem;
		m_fiDestinationItem = fiDestinationItem;
	}
	
	public CFieldItem getSourceItem() { return m_fiSourceItem; }
	public CFieldItem getDestinationItem() { return m_fiDestinationItem; }
}
