package com.dovico.importexporttool;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class Conversions {

	public static double toDouble(String value) throws ParseException {
		Validations.isDouble(value);

		value = value.trim();

		NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
		Number number;
		number = numberFormat.parse(value);
		return number.doubleValue();
	}

	public static Date toDate(String value, boolean customField) throws ParseException {
		// Dates are expected in the format: yyyy-MM-dd
		SimpleDateFormat sdfDate;

		if (customField) {
			sdfDate = new SimpleDateFormat(Constants.CUSTOM_DATE_FORMAT);
		} else {
			sdfDate = new SimpleDateFormat(Constants.XML_DATE_FORMAT);
		}

		Date dtDate = sdfDate.parse(value);
		return dtDate;
	}
}
