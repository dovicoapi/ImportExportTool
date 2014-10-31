package com.dovico.importexporttool;

import java.util.List;

public abstract class RowValidator {
	void accumulate(CFieldItem item) {
		accumulate(item.getElementName(), item.getValue());
	}
	
	abstract void validate(List<String> errors);
	protected abstract void accumulate(String name, String value);
	
	void fixUp(List<CFieldItemMap> fields) {
	}
}
