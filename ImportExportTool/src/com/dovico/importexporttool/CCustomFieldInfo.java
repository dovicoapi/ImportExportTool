package com.dovico.importexporttool;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.dovico.commonlibrary.CXMLHelper;
import com.dovico.importexporttool.CFieldItem.FieldItemType;

public class CCustomFieldInfo {
	private int m_Id;
	private boolean m_Required;
	private boolean m_Hide;
	private List<Value> m_Values = new ArrayList<Value>();
	private FieldItemType m_fieldType;
	
	public CCustomFieldInfo(int id, boolean required, boolean hide, FieldItemType type) {
		this.m_Hide = hide;
		this.m_Required = required;
		this.m_Id = id;
		this.m_fieldType = type;
	}
	
	public int getId() {
		return m_Id;
	}
	
	public boolean isRequired() {
		return m_Required;
	}
	
	public boolean isHide() {
		return m_Hide;
	}

	public void addDefaultValue(String value) {
		m_Values.add(new Value(true, value));
	}
	
	public void addValue(String value, boolean _default) {
		m_Values.add(new Value(_default, value));
	}
	
	public void setType(FieldItemType fieldType) {
		this.m_fieldType = fieldType;
	}
	
	public FieldItemType getType() {
		return m_fieldType;
	}

	public class Value {
		private boolean _default;
		private String value;
		
		public Value(boolean _default, String value) {
			this._default = _default;
			this.value = value;
		}
		
		public boolean isDefault() {
			return _default;
		}
		public String getValue() {
			return value;
		}

		@Override
		public String toString() {
			return value + (_default ? "+" : "");
		}
	}

	public String toXML() {
		StringBuilder builder = new StringBuilder();
		
		builder.append(String.format("<CustomField><TemplateID>%d</TemplateID><Required>%b</Required><Hide>%b</Hide><FieldType>%s</FieldType><Values>", m_Id, m_Required, m_Hide, m_fieldType.toString()));
		
		for(Value v : m_Values) {
			builder.append(String.format("<Value><Default>%b</Default><Text>%s</Text></Value>", v.isDefault(), CXMLHelper.encodeTextForElement(v.getValue())));
		}
		
		builder.append("</Values></CustomField>");
		
		return builder.toString();
	}
	
	public static CCustomFieldInfo fromXML(Element xeElement) {
		Element cf = (Element)CXMLHelper.getChildNode(xeElement, "CustomField");
		
		int id = Integer.parseInt(CXMLHelper.getChildNodeValue(cf, "TemplateID"));
		boolean required = Boolean.parseBoolean(CXMLHelper.getChildNodeValue(cf, "Required"));
		boolean hide = Boolean.getBoolean(CXMLHelper.getChildNodeValue(cf, "Hide"));
		FieldItemType fieldType = FieldItemType.valueOf(CXMLHelper.getChildNodeValue(cf, "FieldType"));
		
		CCustomFieldInfo info = new CCustomFieldInfo(id, required, hide, fieldType);
		
		Element valueElement = (Element)CXMLHelper.getChildNode(cf, "Values");
		NodeList values = valueElement.getElementsByTagName("Value");
		
		for(int i = 0; i < values.getLength(); i++) {
			Element valueNode = (Element)values.item(i);
			boolean def = Boolean.parseBoolean(CXMLHelper.getChildNodeValue(valueNode, "Default"));
			String value = CXMLHelper.getChildNodeValue(valueNode, "Text");
			info.addValue(value, def);
		}
		
		return info;
	}

	@Override
	public String toString() {
		return String.format("%s%s [%d]", m_fieldType.toString(), m_Required ? "*" : "", m_Id);
	}

	public void validate(String caption, String sValue) throws FormattingException {
		switch(getType()) {
		case MultipleChoice:
			String[] values = sValue.split("\\|");
			if (isRequired() && values.length == 0) throw new FormattingException("%s must have a least one option selected", caption, sValue);
			validateMultiChoice(caption, values);
			break;
		case ExclusiveChoice:
			String[] xvalues = sValue.split("\\|");
			
			switch (xvalues.length) {
			case 0:
				if (isRequired())
					throw new FormattingException("%s must have a least one option selected", caption, sValue);
				break;
			case 1:
				if (xvalues[0].equalsIgnoreCase(Constants.EXCLUSIVE_CHOICE_NONE)) {
					if (isRequired())
						throw new FormattingException("%s must have a least one option selected", caption, sValue);
				} else {
					validateMultiChoice(caption, xvalues);					
				}
				break;
			default:
					throw new FormattingException("%s only allows one option to be selected", caption, sValue);
			}			
		}
	}

	protected void validateMultiChoice(String caption, String[] values)
			throws FormattingException {
		for(String value : values) {
			boolean found = false;
			for(Value valueType : m_Values) {
				if (valueType.getValue().equalsIgnoreCase(value)) {
					found = true;
					break;
				}					
			}
			if (!found)
				throw new FormattingException("%s does not accept %s as a value.", caption, value);
		}
	}
}