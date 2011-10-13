package com.dovico.importexporttool;

import java.io.BufferedReader;
import java.util.ArrayList;


public interface IImportFormatter {
	public enum Result { AllOK, Error, EndOfFile };
	
	// Function that is called to read in the file's column headers (you don't have to return actual headers but you need to return something that allows the user
	// to map the values in the file to the fields that will be sent to the REST API - e.g. maybe you return the column data from the first row rather than headers)
	public Result ReadHeaders(BufferedReader brReader, ArrayList<CFieldItem> alReturnColumnsInTheFile);
	
	// Function that is called to read in one record from the file at a time.
	// A flag is passed in indicating if this function is being called for the first line (bFirstLine - for some files, the first line is the column headers so this
	// flag indicates to the function if it should skip a line). 
	//
	// Return a Result.EndOfFile when the end of the file has been reached
	// If there was an error, return Result.Error
	// Otherwise, return Result.AllOK
	public Result ReadRecord(BufferedReader brReader, boolean bFirstLine, ArrayList<CFieldItem> alColumnsInTheFile, ArrayList<CFieldItemMap> alCurrentMappings);
}
