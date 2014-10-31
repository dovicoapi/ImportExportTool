package com.dovico.importexporttool;

import java.util.ArrayList;
import java.util.List;

import com.dovico.importexporttool.IImportFormatter.Result;

public class ImportResult {
	private String errorMessage;
	private Result status;
	private List<String> formatErrors;

	public ImportResult(Result st, String errorMessage) {
		this(st);
		this.errorMessage = errorMessage;
	}
	
	public ImportResult(Result st) {
		this.status = st;
	}
	
	public ImportResult(Result st, List<String> formatErrors) {
		this(st);
		this.formatErrors = formatErrors;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	public Result getStatus() {
		return status;
	}

	public List<String> getFormatErrors() {
		if (formatErrors == null) formatErrors = new ArrayList<String>();
		return formatErrors;
	}
	
	public static ImportResult ALLOK = new ImportResult(Result.AllOK);
	public static ImportResult ENDOFFILE = new ImportResult(Result.EndOfFile);
}