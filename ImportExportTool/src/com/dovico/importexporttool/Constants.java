package com.dovico.importexporttool;

public class Constants {
	// Key for the Consumer Secret field on the Settings dialog (if you leave this
	// blank, the Consumer Secret field will be visible allowing the user to specify
	// their
	// own Consumer Secret - If you want to keep the Consumer Secret from the users
	// of this app, place it here and the CPanel_SettingsEx class will tell the
	// settings
	// pane not to show the field)
	public static String CONSUMER_SECRET_API_TOKEN = "";

	// Keys for storing/caching data
	public static String PREFS_KEY_CONSUMER_SECRET = "ConsumerSecret";
	public static String PREFS_KEY_USER_TOKEN = "UserToken";
	public static String PREFS_KEY_EMPLOYEE_ID = "EmployeeID";
	public static String PREFS_KEY_EMPLOYEE_FIRST = "EmployeeFirstName";
	public static String PREFS_KEY_EMPLOYEE_LAST = "EmployeeLastName";
	public static String PREFS_IMPORT_PATH = "ImportPath";
	public static String PREFS_EXPORT_PATH = "ExportPath";
	public static String PREFS_KEY_IS_DB_V13 = "IsDBV13";

	public static String NEXT_PAGE_URI = "NextPageURI";
	public static String GET_EXPENSE_ENTRY_ITEMS_URI = "GetExpenseEntryItemsURI";

	public static String URI_NOT_AVAILABLE = "N/A";

	// Constants for the Data Source/Destination drop-downs
	public static String API_RESOURCE_ITEM_CLIENTS = "Clients";
	public static String API_RESOURCE_ITEM_PROJECTS = "Projects";
	public static String API_RESOURCE_ITEM_TASKS = "Tasks";
	public static String API_RESOURCE_ITEM_EMPLOYEES = "Employees";
	public static String API_RESOURCE_ITEM_TIME_ENTRIES = "Time Entries";
	public static String API_RESOURCE_ITEM_EXPENSE_CATEGORIES = "Expense Categories";
	public static String API_RESOURCE_ITEM_EXPENSE_ENTRIES = "Expense Entries";
	public static final String API_RESOURCE_ITEM_TEAMS = "Teams";

	public static String CUSTOM_FIELD_ID_CUSTOMER = "C";
	public static String CUSTOM_FIELD_ID_PROJECT = "P";
	public static String CUSTOM_FIELD_ID_TASK = "T";
	public static String CUSTOM_FIELD_ID_EMPLOYEE = "E";
	public static String CUSTOM_FIELD_ID_EXPENSECATEGORY = "A";
	public static String CUSTOM_FIELD_ID_TIMEENTRY = "M";
	public static String CUSTOM_FIELD_ID_EXPENSEENTRY = "X";
	public static final String CUSTOM_FIELD_ID_TEAMS = "G";

	public static Long ADMIN_TOKEN_EMPLOYEE_ID = 99L;

	// The REST API returns and expects dates in this format
	public static String XML_DATE_FORMAT = "yyyy-MM-dd";
	public static String CUSTOM_DATE_FORMAT = "DD/MM/yyyy";

	// The API version that we are targeting
	public static String API_VERSION_TARGETED = "4";
	public static String API_VERSION_TARGETED_EXPENSE_GET_TIMESHEET_DB = "7";

	public static String IMPORT_STATE_FILE_NAME = "importstate.xml";
	public static String EXPORT_STATE_FILE_NAME = "exportstate.xml";

	public static String EXCLUSIVE_CHOICE_NONE = "[None]";
}
