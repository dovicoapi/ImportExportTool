package com.dovico.importexporttool;

import java.io.File;
import javax.swing.filechooser.FileFilter;


public class CSaveAsFileFilter extends FileFilter {	
	private String m_sFileFilterDescription = "";
	private String m_sFileFilterExtension = "";
	
	// Overloaded constructor to tell this class what the Format is that we are to display
	public CSaveAsFileFilter(String sFileFilterDescription, String sFileFilterExtension) { 
		m_sFileFilterDescription = sFileFilterDescription;
		m_sFileFilterExtension = sFileFilterExtension;
	}
	
	
	// Function that is called to determine if the file/folder should be displayed in the dialog
	public boolean accept(File fFile) {
		// Allow directories/folders to be shown
		if(fFile.isDirectory()) { return true; }
		
		// Grab the file name. If we are dealing with the requested format then...
        String sFileName = fFile.getName();        
        return sFileName.toLowerCase().endsWith(m_sFileFilterExtension.toLowerCase()); 
    }
	
    public String getDescription() { return m_sFileFilterDescription; }
}
