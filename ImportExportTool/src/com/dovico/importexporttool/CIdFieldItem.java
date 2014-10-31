package com.dovico.importexporttool;

import org.w3c.dom.Element;

import com.dovico.commonlibrary.CXMLHelper;

public class CIdFieldItem extends CFieldItem {

	private SourceType m_source;
	
	public SourceType getSource() {
		return m_source;
	}

	public CIdFieldItem(int iOrder, String sCaption, String sElementName,
			FieldItemType iFieldType, SourceType source, boolean required) {
		
		super(iOrder, sCaption, sElementName, iFieldType, required);
		this.m_source = source;
	}

	public CIdFieldItem(int iOrder, String sCaption, String sElementName,
			FieldItemType iFieldType, CCustomFieldInfo info, SourceType source, boolean required) {

		super(iOrder, sCaption, sElementName, iFieldType, info, required);
		this.m_source = source;
	}

	public CIdFieldItem(int iOrder, String sCaption, String sElementName,
			FieldItemType iFieldType, boolean bIsAtRootElementLevel,
			String sParentElementName, SourceType source, boolean required) {
		
		super(iOrder, sCaption, sElementName, iFieldType,
				bIsAtRootElementLevel, sParentElementName, required);
		this.m_source = source;
	}

	public CIdFieldItem(int iOrder, String sCaption, String sElementName,
			FieldItemType iFieldType, boolean bIsAtRootElementLevel,
			String sParentElementName, String sRootElementName, SourceType source, boolean required) {
		
		super(iOrder, sCaption, sElementName, iFieldType,
				bIsAtRootElementLevel, sParentElementName, sRootElementName,
				required);
		this.m_source = source;
	}

	public CIdFieldItem(Element xeElement) {
		super(xeElement);
		m_source = SourceType.valueOf(CXMLHelper.getChildNodeValue(xeElement, "Source"));
	}

	@Override
	protected String addFieldsToXml() {
		return super.addFieldsToXml() + "<Source>" + m_source.toString() + "</Source>";
	}

	@Override
	protected void HandleNumberException(String sValue)
			throws FormattingException {
		throw new FormattingException("%s must be a number in the format 9999 (got %s)", this.getCaption(), sValue);
	}
	
	
}
