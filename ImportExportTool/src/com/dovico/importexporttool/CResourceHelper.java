package com.dovico.importexporttool;

import java.util.ArrayList;

import com.dovico.commonlibrary.CRESTAPIHelper;


// NOTE: The Import and Export code still needs so work (export especially) so the following code may change between
// 		here and the final release

// A common location for the common resource data 
public class CResourceHelper {
	// Constants for the Root element names when importing
	public static String ROOT_ELEMENT_NAME_FOR_CLIENTS = "Clients";
	public static String ROOT_ELEMENT_NAME_FOR_PROJECTS = "Projects";
	public static String ROOT_ELEMENT_NAME_FOR_TASKS = "Tasks";
	public static String ROOT_ELEMENT_NAME_FOR_EMPLOYEES = "Employees";
	public static String ROOT_ELEMENT_NAME_FOR_TIME_ENTRIES = "TimeEntries";
	public static String ROOT_ELEMENT_NAME_FOR_EXPENSE_SHEETS = "ExpenseSheets";
	public static String ROOT_ELEMENT_NAME_FOR_EXPENSE_SHEET_ENTRIES = "ExpenseEntries";
		
	// Constants for the Main element names when exporting 
	public static String MAIN_ELEMENT_NAME_FOR_CLIENTS = "Client";
	public static String MAIN_ELEMENT_NAME_FOR_PROJECTS = "Project";
	public static String MAIN_ELEMENT_NAME_FOR_TASKS = "Task";
	public static String MAIN_ELEMENT_NAME_FOR_EMPLOYEES = "Employee";
	public static String MAIN_ELEMENT_NAME_FOR_TIME_ENTRIES = "TimeEntry";
	public static String MAIN_ELEMENT_NAME_FOR_EXPENSE_SHEETS = "ExpenseSheet";
	public static String MAIN_ELEMENT_NAME_FOR_EXPENSE_ENTRIES = "ExpenseEntry";	
	
	
	// Returns the Root Element Name based on the resource item
	public static String getRootElementNameForResource(String sResource) {
		String sRootElementName = "";
		
		// Get Main Element Name (e.g. if we're requesting Clients then this will be 'Client' which is the individual record objects that will be returned by the
		// REST API in that case) based on the selected Data Source value
		if(sResource.equals(Constants.API_RESOURCE_ITEM_CLIENTS)){ sRootElementName = ROOT_ELEMENT_NAME_FOR_CLIENTS; }
		else if(sResource.equals(Constants.API_RESOURCE_ITEM_PROJECTS)){ sRootElementName = ROOT_ELEMENT_NAME_FOR_PROJECTS; }
		else if(sResource.equals(Constants.API_RESOURCE_ITEM_TASKS)){ sRootElementName = ROOT_ELEMENT_NAME_FOR_TASKS; }
		else if(sResource.equals(Constants.API_RESOURCE_ITEM_EMPLOYEES)){ sRootElementName = ROOT_ELEMENT_NAME_FOR_EMPLOYEES; }
		else if(sResource.equals(Constants.API_RESOURCE_ITEM_TIME_ENTRIES)){ sRootElementName = ROOT_ELEMENT_NAME_FOR_TIME_ENTRIES; }
		else if(sResource.equals(Constants.API_RESOURCE_ITEM_EXPENSE_ENTRIES)){ sRootElementName = ROOT_ELEMENT_NAME_FOR_EXPENSE_SHEETS; }
	
		return sRootElementName;
	}
	
	
	// Returns the Main Element Name based on the resource item 
	public static String getMainElementNameForResource(String sResource) {
		String sMainElementName = "";
		
		// Get Main Element Name (e.g. if we're requesting Clients then this will be 'Client' which is the individual record objects that will be returned by the
		// REST API in that case) based on the selected Data Source value
		if(sResource.equals(Constants.API_RESOURCE_ITEM_CLIENTS)){ sMainElementName = MAIN_ELEMENT_NAME_FOR_CLIENTS; }
		else if(sResource.equals(Constants.API_RESOURCE_ITEM_PROJECTS)){ sMainElementName = MAIN_ELEMENT_NAME_FOR_PROJECTS; }
		else if(sResource.equals(Constants.API_RESOURCE_ITEM_TASKS)){ sMainElementName = MAIN_ELEMENT_NAME_FOR_TASKS; }
		else if(sResource.equals(Constants.API_RESOURCE_ITEM_EMPLOYEES)){ sMainElementName = MAIN_ELEMENT_NAME_FOR_EMPLOYEES; }
		else if(sResource.equals(Constants.API_RESOURCE_ITEM_TIME_ENTRIES)){ sMainElementName = MAIN_ELEMENT_NAME_FOR_TIME_ENTRIES; }
		else if(sResource.equals(Constants.API_RESOURCE_ITEM_EXPENSE_ENTRIES)){ sMainElementName = MAIN_ELEMENT_NAME_FOR_EXPENSE_SHEETS; }
	
		return sMainElementName;
	}
	
	
	// Returns the URI needed for the POST based on the resource item
	public static String getURIForResource(String sResource, boolean bReturnExportURI) {
		String sURI = "";
		
		// Build the proper URI based on the Data Source value
		if(sResource.equals(Constants.API_RESOURCE_ITEM_CLIENTS)){ sURI = CRESTAPIHelper.buildURI("Clients/", "", "1"); }
		else if(sResource.equals(Constants.API_RESOURCE_ITEM_PROJECTS)){ sURI = CRESTAPIHelper.buildURI("Projects/", "", "1"); }
		else if(sResource.equals(Constants.API_RESOURCE_ITEM_TASKS)){ sURI = CRESTAPIHelper.buildURI("Tasks/", "", "1"); }
		else if(sResource.equals(Constants.API_RESOURCE_ITEM_EMPLOYEES)){ sURI = CRESTAPIHelper.buildURI("Employees/", "", "1"); }
		else if(sResource.equals(Constants.API_RESOURCE_ITEM_TIME_ENTRIES)){ sURI = CRESTAPIHelper.buildURI("TimeEntries/", "", "1"); }
		else if(sResource.equals(Constants.API_RESOURCE_ITEM_EXPENSE_ENTRIES)){ 
			// If the URI was requested for an Export (GET) then...
			if(bReturnExportURI) { sURI = CRESTAPIHelper.buildURI("ExpenseEntries/", "", "1"); }
			else { // The URI is for an Import (POST)...
				// For Expense Entries, there are two POST URIs that can be used. The 'ExpenseEntries/' version requires that the Expense Sheet already exist.
				// We want to use the 'ExpenseEntries/Sheet/' version because we want the Expense Sheet created for the Expense Entries we specify. 
				sURI = CRESTAPIHelper.buildURI("ExpenseEntries/Sheet/", "", "1");
			} // End if(bReturnExportURI)
		} // End if
		
		// Return the URI to the caller
		return sURI;
	}
		

	// Returns a list of fields based on the resource specified and if the fields are for a GET (export) or a POST (import)
	public static void getAPIFieldsForResource(String sResource, boolean bReturnExportFields, ArrayList<CFieldItem> alReturnAPIFields){
		if(sResource.equals(Constants.API_RESOURCE_ITEM_CLIENTS)){ getAPIFieldsForClients(bReturnExportFields, alReturnAPIFields); }
		else if(sResource.equals(Constants.API_RESOURCE_ITEM_PROJECTS)){ getAPIFieldsForProjects(bReturnExportFields, alReturnAPIFields); } 
		else if(sResource.equals(Constants.API_RESOURCE_ITEM_TASKS)){ getAPIFieldsForTasks(bReturnExportFields, alReturnAPIFields); }
		else if(sResource.equals(Constants.API_RESOURCE_ITEM_EMPLOYEES)){ getAPIFieldsForEmployees(bReturnExportFields, alReturnAPIFields); }
		else if(sResource.equals(Constants.API_RESOURCE_ITEM_TIME_ENTRIES)){ getAPIFieldsForTimeEntries(bReturnExportFields, alReturnAPIFields); }
		else if(sResource.equals(Constants.API_RESOURCE_ITEM_EXPENSE_ENTRIES)){ getAPIFieldsForExpenseEntries(bReturnExportFields, alReturnAPIFields); }
	}
	

	// Returns the available fields for Clients
	private static void getAPIFieldsForClients(boolean bReturnExportFields, ArrayList<CFieldItem> alReturnAPIFields) {
		// Add the available fields for Client data
		int iOrder = 0;
		if(bReturnExportFields) { alReturnAPIFields.add(new CFieldItem(iOrder++, "ID", "ID", CFieldItem.FieldItemType.Number)); }// Only part of a GET
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Name", "Name", CFieldItem.FieldItemType.String));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Abbreviation", "Abbreviation", CFieldItem.FieldItemType.String));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Contact", "Contact", CFieldItem.FieldItemType.String));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Email", "Email", CFieldItem.FieldItemType.String));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Archive", "Archive", CFieldItem.FieldItemType.String));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Integrate", "Integrate", CFieldItem.FieldItemType.String));
	}

	
	// Returns the available fields for Projects
	private static void getAPIFieldsForProjects(boolean bReturnExportFields, ArrayList<CFieldItem> alReturnAPIFields) {
		// Add the available fields for Project data
		int iOrder = 0;
		if(bReturnExportFields) { 
			// ID is only part of a GET. Client ID is only a sub-node (<Client><ID></ID>...</Client>) on GETs
			alReturnAPIFields.add(new CFieldItem(iOrder++, "ID", "ID", CFieldItem.FieldItemType.Number)); // Only part of a GET
			alReturnAPIFields.add(new CFieldItem(iOrder++, "Client ID", "ID", CFieldItem.FieldItemType.Number, false, "Client"));
		} else { // For POSTs, ClientID is an element of the main node.
			alReturnAPIFields.add(new CFieldItem(iOrder++, "Client ID", "ClientID", CFieldItem.FieldItemType.Number)); 
		} // End if(bReturnExportFields)
		
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Name", "Name", CFieldItem.FieldItemType.String));
		
		// For GETs, Leader ID is a sub-node (<Leader><ID></ID>...</Leader>). For POSTs, LeaderID is an element of the main node.
		if(bReturnExportFields) { alReturnAPIFields.add(new CFieldItem(iOrder++, "Leader ID", "ID", CFieldItem.FieldItemType.Number, false, "Leader")); } 
		else { alReturnAPIFields.add(new CFieldItem(iOrder++, "Leader ID", "LeaderID", CFieldItem.FieldItemType.Number)); }
		
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Description", "Description", CFieldItem.FieldItemType.String));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Status", "Status", CFieldItem.FieldItemType.String));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Start Date", "StartDate", CFieldItem.FieldItemType.Date));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "End Date", "EndDate", CFieldItem.FieldItemType.Date));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Billing By", "BillingBy", CFieldItem.FieldItemType.String));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Fixed Cost", "FixedCost", CFieldItem.FieldItemType.Number));
		
		// For GETs, Currency ID is a sub-node (<Currency><ID></ID>...</Currency>). For POSTs, CurrencyID is an element of the main node.
		if(bReturnExportFields) { alReturnAPIFields.add(new CFieldItem(iOrder++, "Currency ID", "ID", CFieldItem.FieldItemType.Number, false, "Currency")); }
		else { alReturnAPIFields.add(new CFieldItem(iOrder++, "Currency ID", "CurrencyID", CFieldItem.FieldItemType.Number)); }
		
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Budget Rate Date", "BudgetRateDate", CFieldItem.FieldItemType.Date));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Hide Tasks", "HideTasks", CFieldItem.FieldItemType.String));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Prevent Entries", "PreventEntries", CFieldItem.FieldItemType.String));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Time Billable by Default", "TimeBillableByDefault", CFieldItem.FieldItemType.String));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Expenses Billable by Default", "ExpensesBillableByDefault", CFieldItem.FieldItemType.String));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Linked", "Linked", CFieldItem.FieldItemType.String));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "MSPConfig", "MSPConfig", CFieldItem.FieldItemType.String));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Archive", "Archive", CFieldItem.FieldItemType.String));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Integrate", "Integrate", CFieldItem.FieldItemType.String));
	}
	
	
	// Returns the available fields for Tasks
	private static void getAPIFieldsForTasks(boolean bReturnExportFields, ArrayList<CFieldItem> alReturnAPIFields) {
		// Add the available fields for Task data
		int iOrder = 0;
		if(bReturnExportFields) { alReturnAPIFields.add(new CFieldItem(iOrder++, "ID", "ID", CFieldItem.FieldItemType.Number)); }// Only part of a GET
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Name", "Name", CFieldItem.FieldItemType.String));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Description", "Description", CFieldItem.FieldItemType.String));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Force Description", "ForceDescription", CFieldItem.FieldItemType.String));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Global", "Global", CFieldItem.FieldItemType.String));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Prorate", "Prorate", CFieldItem.FieldItemType.Number));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "WBS", "WBS", CFieldItem.FieldItemType.String));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Archive", "Archive", CFieldItem.FieldItemType.String));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Integrate", "Integrate", CFieldItem.FieldItemType.String));
	}	
	
	
	// Returns the available fields for Employees
	private static void getAPIFieldsForEmployees(boolean bReturnExportFields, ArrayList<CFieldItem> alReturnAPIFields) {
		// Add the available fields for Employee data
		int iOrder = 0;
		if(bReturnExportFields) { alReturnAPIFields.add(new CFieldItem(iOrder++, "ID", "ID", CFieldItem.FieldItemType.Number)); }// Only part of a GET
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Last Name", "LastName", CFieldItem.FieldItemType.String));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "First Name", "FirstName", CFieldItem.FieldItemType.String));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Wage", "Wage", CFieldItem.FieldItemType.Number));
		
		// For GETs, Wage Currency ID is a sub-node (<WageCurrency><ID></ID>...</WageCurrency>). For POSTs, WageCurrencyID is an element of the main node.
		if(bReturnExportFields) { alReturnAPIFields.add(new CFieldItem(iOrder++, "Wage Currency ID", "ID", CFieldItem.FieldItemType.Number, false, "WageCurrency")); }
		else { alReturnAPIFields.add(new CFieldItem(iOrder++, "Wage Currency ID", "WageCurrencyID", CFieldItem.FieldItemType.Number)); }

		alReturnAPIFields.add(new CFieldItem(iOrder++, "Wage Changed Date", "WageChangedDate", CFieldItem.FieldItemType.Date));		
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Charge", "Charge", CFieldItem.FieldItemType.Number));
		
		// For GETs, Charge Currency ID is a sub-node (<ChargeCurrency><ID></ID>...</ChargeCurrency>). For POSTs, ChargeCurrencyID is an element of the main node.
		if(bReturnExportFields) { alReturnAPIFields.add(new CFieldItem(iOrder++, "Charge Currency ID", "ID", CFieldItem.FieldItemType.Number, false, "ChargeCurrency"));  }
		else { alReturnAPIFields.add(new CFieldItem(iOrder++, "Charge Currency ID", "ChargeCurrencyID", CFieldItem.FieldItemType.Number)); }
		
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Charge Changed Date", "ChargeChangedDate", CFieldItem.FieldItemType.Date));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Start Date", "StartDate", CFieldItem.FieldItemType.Date));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "End Date", "EndDate", CFieldItem.FieldItemType.Date));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Number", "Number", CFieldItem.FieldItemType.String));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Email", "Email", CFieldItem.FieldItemType.String));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "WorkDays", "WorkDays", CFieldItem.FieldItemType.String));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Hours", "Hours", CFieldItem.FieldItemType.Number));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Allow Alternate Approvals", "AltApproval", CFieldItem.FieldItemType.String));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Notify Time to Approve", "NotificationTime", CFieldItem.FieldItemType.String));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Notify Expenses to Approve", "NotificationExpense", CFieldItem.FieldItemType.String));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Notify Time/Expenses Rejected", "NotificationRejected", CFieldItem.FieldItemType.String));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Archive", "Archive", CFieldItem.FieldItemType.String));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Integrate", "Integrate", CFieldItem.FieldItemType.String));
	}
	
		
	// Returns the available fields for Time Entries
	private static void getAPIFieldsForTimeEntries(boolean bReturnExportFields, ArrayList<CFieldItem> alReturnAPIFields) {
		// Add the available fields for Time Entry data
		int iOrder = 0;
		if(bReturnExportFields) { 
			// The ID, Sheet ID, Sheet Status, and Rejected Reason are only part of GETs.
			//
			// A string is used for the ID in this case because the API prefixes it with a 'T' or 'M' to know which type of time entry it is for paging purposes 
			// (T for un-approved time, M for approved time). 
			alReturnAPIFields.add(new CFieldItem(iOrder++, "ID", "ID", CFieldItem.FieldItemType.String));
			alReturnAPIFields.add(new CFieldItem(iOrder++, "Sheet ID", "ID", CFieldItem.FieldItemType.Number, false, "Sheet"));
			alReturnAPIFields.add(new CFieldItem(iOrder++, "Sheet Status", "Status", CFieldItem.FieldItemType.String, false, "Sheet"));
			alReturnAPIFields.add(new CFieldItem(iOrder++, "Rejected Reason", "RejectedReason", CFieldItem.FieldItemType.String, false, "Sheet"));
			alReturnAPIFields.add(new CFieldItem(iOrder++, "Project ID", "ID", CFieldItem.FieldItemType.Number, false, "Project"));
			alReturnAPIFields.add(new CFieldItem(iOrder++, "Task ID", "ID", CFieldItem.FieldItemType.Number, false, "Task"));
			alReturnAPIFields.add(new CFieldItem(iOrder++, "Employee ID", "ID", CFieldItem.FieldItemType.Number, false, "Employee"));
		} else { // For POSTs, the ProjectID, TaskID, and EmployeeID are elements of the main node...
			alReturnAPIFields.add(new CFieldItem(iOrder++, "Project ID", "ProjectID", CFieldItem.FieldItemType.Number));
			alReturnAPIFields.add(new CFieldItem(iOrder++, "Task ID", "TaskID", CFieldItem.FieldItemType.Number));
			alReturnAPIFields.add(new CFieldItem(iOrder++, "Employee ID", "EmployeeID", CFieldItem.FieldItemType.Number));
		} // End if(bReturnExportFields)
		
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Date", "Date", CFieldItem.FieldItemType.Date));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Start Time", "StartTime", CFieldItem.FieldItemType.String));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Stop Time", "StopTime", CFieldItem.FieldItemType.String));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Total Hours", "TotalHours", CFieldItem.FieldItemType.Number));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Description", "Description", CFieldItem.FieldItemType.String));
		
		// The Integrate value for Time Entries can only be specified for time that is approved via a PUT. POSTs create un-submitted/unapproved time
		if(bReturnExportFields) { alReturnAPIFields.add(new CFieldItem(iOrder++, "Integrate", "Integrate", CFieldItem.FieldItemType.String)); }		
	}
		
		
	// Returns the available fields for Expense Entries
	private static void getAPIFieldsForExpenseEntries(boolean bReturnExportFields, ArrayList<CFieldItem> alReturnAPIFields) {
		// Expense Entry data is in two parts: The Sheet and the Entries
		int iOrder = 0;
		
		//------------------------------
		// Sheet elements
		//------------------------------
		// Add the available fields for Expense Sheet data (NOTE: usually you don't specify the Root Element Name but, in this case, we have two different root 
		// elements - ExpenseSheet and ExpenseEntry. We use the Root Element Name to distinguish the two types of fields)		
		if(bReturnExportFields) { 
			// The Sheet ID and Status are only part of GETs			
			alReturnAPIFields.add(new CFieldItem(iOrder++, "Sheet ID", "ID", CFieldItem.FieldItemType.Number, true, "", MAIN_ELEMENT_NAME_FOR_EXPENSE_SHEETS));
			alReturnAPIFields.add(new CFieldItem(iOrder++, "Employee ID", "ID", CFieldItem.FieldItemType.Number, false, "Employee", MAIN_ELEMENT_NAME_FOR_EXPENSE_SHEETS));
			alReturnAPIFields.add(new CFieldItem(iOrder++, "Manager ID", "ID", CFieldItem.FieldItemType.Number, false, "Manager", MAIN_ELEMENT_NAME_FOR_EXPENSE_SHEETS));
			alReturnAPIFields.add(new CFieldItem(iOrder++, "Project ID", "ID", CFieldItem.FieldItemType.Number, false, "Project", MAIN_ELEMENT_NAME_FOR_EXPENSE_SHEETS));
			alReturnAPIFields.add(new CFieldItem(iOrder++, "Status", "Status", CFieldItem.FieldItemType.String, true, "", MAIN_ELEMENT_NAME_FOR_EXPENSE_SHEETS));
		} else { // For POSTs, the EmployeeID, ManagerID, and ProjectID are elements of the main node...
			alReturnAPIFields.add(new CFieldItem(iOrder++, "Employee ID", "EmployeeID", CFieldItem.FieldItemType.Number, true, "", MAIN_ELEMENT_NAME_FOR_EXPENSE_SHEETS));
			alReturnAPIFields.add(new CFieldItem(iOrder++, "Manager ID", "ManagerID", CFieldItem.FieldItemType.Number, true, "", MAIN_ELEMENT_NAME_FOR_EXPENSE_SHEETS));
			alReturnAPIFields.add(new CFieldItem(iOrder++, "Project ID", "ProjectID", CFieldItem.FieldItemType.Number, true, "", MAIN_ELEMENT_NAME_FOR_EXPENSE_SHEETS));
		} // End if(bReturnExportFields)
		
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Title", "Title", CFieldItem.FieldItemType.String, true, "", MAIN_ELEMENT_NAME_FOR_EXPENSE_SHEETS));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Description", "Description", CFieldItem.FieldItemType.String, true, "", MAIN_ELEMENT_NAME_FOR_EXPENSE_SHEETS));

		// The Check Date, Check Number, and Approver fields are only part of GETs
		if(bReturnExportFields) { 
			alReturnAPIFields.add(new CFieldItem(iOrder++, "Check Date", "CheckDate", CFieldItem.FieldItemType.Date, true, "", MAIN_ELEMENT_NAME_FOR_EXPENSE_SHEETS));
			alReturnAPIFields.add(new CFieldItem(iOrder++, "Check Number", "CheckNumber", CFieldItem.FieldItemType.String, true, "", MAIN_ELEMENT_NAME_FOR_EXPENSE_SHEETS));
			alReturnAPIFields.add(new CFieldItem(iOrder++, "Approver", "Approver", CFieldItem.FieldItemType.String, true, "", MAIN_ELEMENT_NAME_FOR_EXPENSE_SHEETS));
		} // End if(bReturnExportFields)
				
		
		//------------------------------
		// Entry elements
		//------------------------------
		// Add the available fields for Expense Entry data
		if(bReturnExportFields) {
			// ID is only part of GETs
			alReturnAPIFields.add(new CFieldItem(iOrder++, "Expense Entry ID", "ID", CFieldItem.FieldItemType.Number, true, "", MAIN_ELEMENT_NAME_FOR_EXPENSE_ENTRIES));
			alReturnAPIFields.add(new CFieldItem(iOrder++, "Expense Category ID", "ID", CFieldItem.FieldItemType.Number, false, "ExpenseCategory", MAIN_ELEMENT_NAME_FOR_EXPENSE_ENTRIES));
		} else { // For POSTs, the ExpenseCategoryID is an element of the main node...
			alReturnAPIFields.add(new CFieldItem(iOrder++, "Expense Category ID", "ExpenseCategoryID", CFieldItem.FieldItemType.Number, true, "", MAIN_ELEMENT_NAME_FOR_EXPENSE_ENTRIES));
		} // End if(bReturnExportFields)
		
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Date", "Date", CFieldItem.FieldItemType.Date, true, "", MAIN_ELEMENT_NAME_FOR_EXPENSE_ENTRIES));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Purchase Order", "PurchaseOrder", CFieldItem.FieldItemType.String, true, "", MAIN_ELEMENT_NAME_FOR_EXPENSE_ENTRIES));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Reference Number", "ReferenceNumber", CFieldItem.FieldItemType.String, true, "", MAIN_ELEMENT_NAME_FOR_EXPENSE_ENTRIES));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Quantity", "Quantity", CFieldItem.FieldItemType.Number, true, "", MAIN_ELEMENT_NAME_FOR_EXPENSE_ENTRIES));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Cost per Unit", "CostPerUnit", CFieldItem.FieldItemType.Number, true, "", MAIN_ELEMENT_NAME_FOR_EXPENSE_ENTRIES));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Amount", "Amount", CFieldItem.FieldItemType.Number, true, "", MAIN_ELEMENT_NAME_FOR_EXPENSE_ENTRIES));
		
		// For GETs, Currency ID is a sub-node (<Currency><ID></ID>...</Currency>). For POSTs, CurrencyID is an element of the main node.
		if(bReturnExportFields) { alReturnAPIFields.add(new CFieldItem(iOrder++, "Currency ID", "ID", CFieldItem.FieldItemType.Number, false, "Currency", MAIN_ELEMENT_NAME_FOR_EXPENSE_ENTRIES)); } 
		else { alReturnAPIFields.add(new CFieldItem(iOrder++, "Currency ID", "CurrencyID", CFieldItem.FieldItemType.Number, true, "", MAIN_ELEMENT_NAME_FOR_EXPENSE_ENTRIES)); }
		
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Markup", "Markup", CFieldItem.FieldItemType.Number, true, "", MAIN_ELEMENT_NAME_FOR_EXPENSE_ENTRIES));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Fixed Amount", "FixedAmount", CFieldItem.FieldItemType.Number, true, "", MAIN_ELEMENT_NAME_FOR_EXPENSE_ENTRIES));
		
		// For GETs, Fixed Currency ID is a sub-node (<FixedCurrency><ID></ID>...</FixedCurrency>). For POSTs, FixedCurrencyID is an element of the main node.
		if(bReturnExportFields) { alReturnAPIFields.add(new CFieldItem(iOrder++, "Fixed Currency ID", "ID", CFieldItem.FieldItemType.Number, false, "FixedCurrency", MAIN_ELEMENT_NAME_FOR_EXPENSE_ENTRIES)); } 
		else { alReturnAPIFields.add(new CFieldItem(iOrder++, "Fixed Currency ID", "FixedCurrencyID", CFieldItem.FieldItemType.Number, true, "", MAIN_ELEMENT_NAME_FOR_EXPENSE_ENTRIES)); } 
		
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Tax 1", "Tax1", CFieldItem.FieldItemType.Number, true, "", MAIN_ELEMENT_NAME_FOR_EXPENSE_ENTRIES));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Tax 2", "Tax2", CFieldItem.FieldItemType.Number, true, "", MAIN_ELEMENT_NAME_FOR_EXPENSE_ENTRIES));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Description", "Description", CFieldItem.FieldItemType.String, true, "", MAIN_ELEMENT_NAME_FOR_EXPENSE_ENTRIES));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Reimbursable", "Reimbursable", CFieldItem.FieldItemType.String, true, "", MAIN_ELEMENT_NAME_FOR_EXPENSE_ENTRIES));
		alReturnAPIFields.add(new CFieldItem(iOrder++, "Billable", "Billable", CFieldItem.FieldItemType.String, true, "", MAIN_ELEMENT_NAME_FOR_EXPENSE_ENTRIES));
				
		// The Attachment Name is GET only. The Archive and Integrate values for Expense Entries can only be specified for expenses that are approved via a PUT. 
		// POSTs create un-submitted/unapproved expenses.
		if(bReturnExportFields) {
			alReturnAPIFields.add(new CFieldItem(iOrder++, "Attachment Name", "AttachmentName", CFieldItem.FieldItemType.String, true, "", MAIN_ELEMENT_NAME_FOR_EXPENSE_ENTRIES));
			alReturnAPIFields.add(new CFieldItem(iOrder++, "Archive", "Archive", CFieldItem.FieldItemType.String, true, "", MAIN_ELEMENT_NAME_FOR_EXPENSE_ENTRIES));
			alReturnAPIFields.add(new CFieldItem(iOrder++, "Integrate", "Integrate", CFieldItem.FieldItemType.String, true, "", MAIN_ELEMENT_NAME_FOR_EXPENSE_ENTRIES));
		} // End if(bReturnExportFields)
	}
}
