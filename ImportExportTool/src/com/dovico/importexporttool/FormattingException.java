package com.dovico.importexporttool;

public class FormattingException extends Exception {
	private String caption;
	private String value;

	public FormattingException(String message, String caption, String value) {
		super(String.format(message, caption, value));
		this.caption = caption;
		this.value = value;
	}

	public String getCaption() {
		return caption;
	}

	public String getValue() {
		return value;
	}
}
