package com.dovico.importexporttool;

import java.io.BufferedWriter;
import java.util.ArrayList;

// Interface that all ExportFormat drop-downs must implement
public interface IExportFormatter {	
	// Helper functions for the Save As dialog to know how to save a file of this format
	public String getSaveAsFileFilterDescription();
	public String getSaveAsFileFilterExtension();
	
	// Objects derived from this Interface will be used in a drop-down list. The toString method must be overridden to display an appropriate value in the 
	// drop-down list.
	public String toString();
		
	
	// Function that is called if the fields should be written out to the file as headers. Return 'false' if there was an error (export stops) 
	public boolean WriteHeaders(ArrayList<CFieldItem> alFields, BufferedWriter bwWriter);
	
	// Function that is called to write out the field values to the file. Return 'false' if there was an error (export stops)
	public boolean WriteData(ArrayList<CFieldItem> alFields, BufferedWriter bwWriter);
}
