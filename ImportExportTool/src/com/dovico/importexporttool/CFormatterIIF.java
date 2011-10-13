package com.dovico.importexporttool;

import java.io.BufferedWriter;
import java.util.ArrayList;


//NOTE: The Export code still needs work so the following code will change between here and the final release

public class CFormatterIIF implements IExportFormatter {
	// Helper functions for the Save As dialog to know how to save a file of this format
	public String getSaveAsFileFilterDescription() { return "QuickBooks IIF Files (*.IIF)"; }
	public String getSaveAsFileFilterExtension() { return ".IIF"; }
	
	// This class will be used in a drop-down list. The toString method must be overridden to display an appropriate value in the list.
	public String toString() { return getSaveAsFileFilterDescription(); }
	
	
	// Function that is called if the fields should be written out to the file as headers
	public boolean WriteHeaders(ArrayList<CFieldItem> alFields, BufferedWriter bwWriter) { return true; }
	
	// Function that is called to write out the field values to the file
	public boolean WriteData(ArrayList<CFieldItem> alFields, BufferedWriter bwWriter) { return true; }
}
