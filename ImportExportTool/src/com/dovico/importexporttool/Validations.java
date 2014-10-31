package com.dovico.importexporttool;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.dovico.importexporttool.CFieldItem.FieldItemType;

public class Validations {

	public static void isDouble(String input) throws ParseException {

		  input = input.trim();

		  NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
		  ParsePosition parsePosition = new ParsePosition(0);
		  Number number = numberFormat.parse(input, parsePosition);

		  if(parsePosition.getIndex() != input.length()){
		    throw new ParseException("Invalid input", parsePosition.getIndex());
		  }
	}

	public static void length(String sValue, String caption) throws FormattingException {
		if (sValue.length() >= 250) {
			throw new FormattingException("%2s is too long", caption, sValue);
		}
	}
	
	public static void isDateInRange(String sValue, String caption, boolean customField) throws FormattingException {
		String sErrorMsg = "";
		
		// Dates are expected in the format: yyyy-MM-dd
		SimpleDateFormat sdfDate;
		
		if (customField) {
			sdfDate = new SimpleDateFormat(Constants.CUSTOM_DATE_FORMAT);
		} else {
			sdfDate = new SimpleDateFormat(Constants.XML_DATE_FORMAT);	
		}
		
		try {
			// Parse the date string to ensure it's in the proper format
			Date dtDate = sdfDate.parse(sValue);
			if(!validateDateRange(dtDate)){ sErrorMsg = ("The date '"+ sValue +"' does not fall within the required date range of 1900-01-01 to 2199-12-31"); }
		} catch (ParseException e) {
			sErrorMsg = ("The date '"+ sValue +"' must be in the format " + sdfDate.toPattern()); 
		}

		
		if(!sErrorMsg.isEmpty()){ throw new FormattingException(sErrorMsg, caption, sValue); }
	}

	private static boolean validateDateRange(Date dtValue){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 1900);
		cal.set(Calendar.MONTH, 0);//zero-based!
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.HOUR, 0); // Zero out the Hour
		cal.set(Calendar.MINUTE, 0); // Zero out the Minute
		cal.set(Calendar.SECOND, 0); // Zero out the Second
		cal.set(Calendar.MILLISECOND, 0); // Zero out the Millisecond		
		Date dtMinSysDate = cal.getTime();
		
		cal.set(Calendar.YEAR, 2199);
		cal.set(Calendar.MONTH, 11);//zero-based!
		cal.set(Calendar.DATE, 31);		
		Date dtMaxSysDate = cal.getTime();
		
		// Tell the calling function if the date falls outside of the system date range or not
		return !(dtValue.before(dtMinSysDate) || dtValue.after(dtMaxSysDate));
	}
}
