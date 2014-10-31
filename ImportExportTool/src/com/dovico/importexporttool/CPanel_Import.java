package com.dovico.importexporttool;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.DefaultTableModel;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.*;
import com.dovico.commonlibrary.APIRequestResult;
import com.dovico.commonlibrary.CRESTAPIHelper;
import com.dovico.commonlibrary.CXMLHelper;
import com.dovico.importexporttool.CFieldItem.FieldItemType;
import com.dovico.importexporttool.IImportFormatter.Result;

public class CPanel_Import extends JPanel {
	private static final long serialVersionUID = 1L;

	private JTextField m_txtImportFrom = null;
	private JComboBox m_ddlFormat = null;
	private JComboBox m_ddlDestination = null;

	private DefaultTableModel m_tmMappingModel = null;

	ArrayList<CFieldItem> m_alColumnsInTheFile = null; // Will hold the Columns
														// for the selected
														// import file
														// (populated when the
														// Map button is pressed
														// for the first time
														// after a file is
														// selected
	ArrayList<CFieldItemMap> m_alCurrentMappings = null; // Will hold the
															// mappings

	// Will hold a reference to the UI Logic parent class
	private CCommonUILogic m_UILogic = null;

	private JButton cmdBrowse;

	private JTable tblMapping;

	private JScrollPane spMapping;

	private JButton cmdMap;

	private JButton cmdImport;

	private JLabel loadingLabel;

	// Default constructor
	public CPanel_Import(CCommonUILogic UILogic) {
		// Remember the reference to the UI Logic parent class
		m_UILogic = UILogic;

		this.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("65px"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("25dlu"),
				FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("25dlu"),
				FormFactory.RELATED_GAP_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, RowSpec.decode("125px"),
				RowSpec.decode("75px"), FormFactory.DEFAULT_ROWSPEC,
				RowSpec.decode("default:grow"), }));

		// Import From controls:
		JLabel lblImportFrom = new JLabel("Import from:");
		lblImportFrom.setFont(new Font("Arial", Font.PLAIN, 11));
		this.add(lblImportFrom, "2, 2, left, default");

		m_txtImportFrom = new JTextField();
		m_txtImportFrom.setEditable(false);
		m_txtImportFrom.setFont(new Font("Arial", Font.PLAIN, 11));
		m_txtImportFrom.setColumns(10);
		m_txtImportFrom.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				OnClick_cmdBrowse();
			} // Call the Browse button's code when the text box is clicked
		});
		this.add(m_txtImportFrom, "4, 2, 3, 1, fill, default");

		cmdBrowse = new JButton("...");
		cmdBrowse.setToolTipText("Choose the file to import.");
		cmdBrowse.setFont(new Font("Arial", Font.PLAIN, 11));
		cmdBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				OnClick_cmdBrowse();
			}
		});
		this.add(cmdBrowse, "8, 2");

		// Format controls:
		JLabel lblFormat = new JLabel("Format:");
		lblFormat.setFont(new Font("Arial", Font.PLAIN, 11));
		this.add(lblFormat, "2, 4, left, default");

		m_ddlFormat = new JComboBox(getFormatValues());
		m_ddlFormat.setFont(new Font("Arial", Font.PLAIN, 11));
		this.add(m_ddlFormat, "4, 4, 5, 1, fill, default");

		// Destination controls:
		JLabel lblDestination = new JLabel("Destination:");
		lblDestination.setFont(new Font("Arial", Font.PLAIN, 11));
		this.add(lblDestination, "2, 6, left, default");

		m_ddlDestination = new JComboBox(getDestinationValues());
		m_ddlDestination.setFont(new Font("Arial", Font.PLAIN, 11));
		m_ddlDestination.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				OnSelChanged_ddlDestination();
			}
		});
		this.add(m_ddlDestination, "4, 6, 5, 1, fill, default");

		m_tmMappingModel = new DefaultTableModel(getMappingColumnNames(), 0);

		// Mapping controls:
		JLabel lblMapping = new JLabel("Mapping:");
		lblMapping.setFont(new Font("Arial", Font.PLAIN, 11));
		this.add(lblMapping, "2, 8, left, top");

		tblMapping = new JTable(m_tmMappingModel);
		tblMapping.setShowGrid(false);
		tblMapping.setFont(new Font("Arial", Font.PLAIN, 11));

		spMapping = new JScrollPane(tblMapping);
		tblMapping.setFillsViewportHeight(true);
		spMapping.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
				null, null));
		this.add(spMapping, "4, 8, fill, fill");

		cmdMap = new JButton("Map...");
		cmdMap.setFont(new Font("Arial", Font.PLAIN, 11));
		cmdMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				OnClick_cmdMap();
			}
		});
		this.add(cmdMap, "6, 8, 3, 1, default, top");

		// Import button
		cmdImport = new JButton("Import");
		cmdImport.setFont(new Font("Arial", Font.PLAIN, 11));
		cmdImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				OnClick_cmdImport();
			}
		});
		this.add(cmdImport, "6, 10, 3, 1");

		URL imgsrc = getClass().getResource("/loading2.gif");
		ImageIcon loading = new ImageIcon(imgsrc);
		loadingLabel = new JLabel(loading);
		loadingLabel.setVisible(false);
		// loadingLabel.setSize(100, 100);
		this.add(loadingLabel, "2, 10, left, default");

		// Load in the state information (field values and selections from the
		// last time this view was used) and re-populate the fields
		loadState();
	}

	// Returns the available values for the Format drop-down
	private IImportFormatter[] getFormatValues() {
		IImportFormatter[] arrItems = { new CFormatterCSV() };
		return arrItems;
	}

	// Returns the available values for the Destination drop-down
	private String[] getDestinationValues() {
		String[] arrItems = { Constants.API_RESOURCE_ITEM_CLIENTS,
				Constants.API_RESOURCE_ITEM_EMPLOYEES,
				Constants.API_RESOURCE_ITEM_EXPENSE_CATEGORIES,
				Constants.API_RESOURCE_ITEM_EXPENSE_ENTRIES,
				Constants.API_RESOURCE_ITEM_PROJECTS,
				Constants.API_RESOURCE_ITEM_TASKS,
				Constants.API_RESOURCE_ITEM_TEAMS,
				Constants.API_RESOURCE_ITEM_TIME_ENTRIES};
		return arrItems;
	}

	// Returns the names for the columns in the Mapping table
	private String[] getMappingColumnNames() {
		String[] arrColNames = { "Source", "Destination" };
		return arrColNames;
	}

	// Called when the user clicks on the Browse button
	private void OnClick_cmdBrowse() {
		// Create a File Open object. Show the File Open dialog. If the user
		// clicked OK/Save then...
		JFileChooser dlgFileOpen = new JFileChooser();

		String filename = m_txtImportFrom.getText();
		String importPath = m_UILogic.getExportPath();

		if (filename != null && !filename.isEmpty()) {
			dlgFileOpen.setCurrentDirectory(new File(filename));
		} else if (importPath != "" && !importPath.isEmpty()) {
			dlgFileOpen.setCurrentDirectory(new File(importPath));
		}

		if (dlgFileOpen.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			// Put the path for the file name into the text box and clear the
			// mapping table since it most likely does not match the data in the
			// newly selected
			// file.
			String filePath = dlgFileOpen.getSelectedFile().getPath();
			m_txtImportFrom.setText(filePath);
			clearRowsFromMappingTable();
			m_alColumnsInTheFile = null;
			m_UILogic.setImportPath(filePath);
		} // End if(dlgFileOpen.showOpenDialog(this) ==
			// JFileChooser.APPROVE_OPTION)
	}

	// Helper method for clearing all rows from the mapping table
	private void clearRowsFromMappingTable() {
		// Loop until there are no more rows in the table by removing the first
		// row during each loop...
		while (m_tmMappingModel.getRowCount() > 0) {
			m_tmMappingModel.removeRow(0);
		}

		// Empty the ArrayList object too
		if (m_alCurrentMappings != null) {
			m_alCurrentMappings.clear();
		}
	}

	// Called when the selection changes for the Destination drop-down. Clear
	// the mapping table since the destination mapping no longer matches
	private void OnSelChanged_ddlDestination() {
		clearRowsFromMappingTable();
	}

	// Called when the user clicks on the Map button
	private void OnClick_cmdMap() {
		// If there is no Import From path then...
		String sFilePath = m_txtImportFrom.getText();
		if (sFilePath.isEmpty()) {
			JOptionPane.showMessageDialog(null,
					"Please specify a file to import", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		} // End if(sFilePath.isEmpty())

		// Have the following function load in the column information from the
		// file (will only be loaded in the first time the function is called
		// after the file
		// is chosen - a check will be made to see if m_alColumnsInTheFile is
		// null or not). If there was an issue then exit now.
		if (!getColumnsFromFile(sFilePath)) {
			return;
		}

		// If the mappings list has not yet been created then create it now
		if (m_alCurrentMappings == null) {
			m_alCurrentMappings = new ArrayList<CFieldItemMap>();
		}

		// Create an instance of our Add/Remove Fields dialog. Tell the dialog
		// which Data Source and which Fields are selected
		Dialog_ImportFieldMapping dlgMapping = new Dialog_ImportFieldMapping();
		String userToken = m_UILogic.getDataAccessToken();
		String consumerSecret = Constants.CONSUMER_SECRET_API_TOKEN;
		dlgMapping.setSourceColumnsDestinationAndMapping(consumerSecret,
				userToken, m_alColumnsInTheFile,
				(String) m_ddlDestination.getSelectedItem(),
				m_alCurrentMappings);
		dlgMapping.setVisible(true);

		// If the dialog was closed as a result of the user clicking on the OK
		// button then...
		if (dlgMapping.getClosedByOKButton()) {
			// Remove all of the rows from our Mapping table
			clearRowsFromMappingTable();

			// Get the list of mappings from the dialog and then add it to the
			// list of mappings here
			dlgMapping.getSelectedMappings(m_alCurrentMappings);
			for (CFieldItemMap fiFieldItemMap : m_alCurrentMappings) {
				m_tmMappingModel.addRow(new Object[] {
						fiFieldItemMap.getSourceItem(),
						fiFieldItemMap.getDestinationItem() });
			} // End of the for (CFieldItemMap fiFieldItemMap :
				// m_alCurrentMappings) loop.
		} // End if(dlgFields.getClosedByOKButton())
	}

	// Reads in the columns from the file
	private boolean getColumnsFromFile(String sFilePath) {
		// If the columns have already been loaded in then we don't need to
		// continue
		if (m_alColumnsInTheFile != null) {
			return true;
		}

		m_alColumnsInTheFile = new ArrayList<CFieldItem>();
		ImportResult iResult = ImportResult.ALLOK;
		BufferedReader brReader = null;

		try {
			// Get the selected formatter object from the Format drop-down
			IImportFormatter fFormatter = (IImportFormatter) m_ddlFormat
					.getSelectedItem();

			// Open the file for reading
			FileReader frReader = new FileReader(sFilePath);
			brReader = new BufferedReader(frReader);

			// Ask the formatter to read in the header information
			iResult = fFormatter.ReadHeaders(brReader, m_alColumnsInTheFile);
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			iResult = new ImportResult(Result.Error, e.getMessage());
		} finally { // Happens whether or not there was an exception...
			// If we have a reader object then make sure it's closed
			try {
				if (brReader != null) {
					brReader.close();
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
				iResult = new ImportResult(Result.Error, e.getMessage());
			}
		} // End of the finally block

		// If there was an error then...
		if (iResult.getStatus() == Result.Error) {
			m_alColumnsInTheFile = null;
			return false; // Indicate that this function call was not successful
		} else { // No error...
			return true; // Indicate that this function call was successful
		} // End if(iResult == IImportFormatter.Result.Error)
	}

	// Called when the user clicks on the Import button
	// / <history>
	// / <modified author="C. Gerard Gallant" date="2011-12-15"
	// reason="Added a prompt if the logged in user token is not the admin user token and we are importing time/expenses. When using the Admin token, time/expenses are imported as approved time/expenses. Any other user token results in the time/expenses being imported as unsubmitted and I felt it was important for the user to be aware of that before they imported the data."/>
	// / </history>
	private void OnClick_cmdImport() {
		// If the validation fails then...
		if (!validateForImport()) {
			return;
		}

		// Save the current field values and selections
		saveState();

		initializeValidation();
		clearValidationState();

		final String sResource = (String) m_ddlDestination.getSelectedItem();
		final String sRootElementName = CResourceHelper
				.getRootElementNameForResource(sResource);
		final String sMainElementName = CResourceHelper
				.getMainElementNameForResource(sResource);

		// Grab the logged in employee id. If the logged in user's token is not
		// the admin token then...
		final Long lEmployeeID = m_UILogic.getEmployeeID();

		if (lEmployeeID != Constants.ADMIN_TOKEN_EMPLOYEE_ID) {
			String sWarningMsg = "";

			// If we're importing time entries then...
			if (sResource.equals(Constants.API_RESOURCE_ITEM_TIME_ENTRIES)) {
				sWarningMsg = "WARNING: The time will be imported as 'unsubmitted' because the user token specified is not the Administrator Data Access Token. Continue with the import?";
			} else if (sResource
					.equals(Constants.API_RESOURCE_ITEM_EXPENSE_ENTRIES)) {// If
																			// we're
																			// importing
																			// expense
																			// entries...
				sWarningMsg = "WARNING: The expenses will be imported as 'unsubmitted' because the user token specified is not the Administrator Data Access Token. Continue with the import?";
			} // End if

			// If we have a warning message then...
			if (!sWarningMsg.isEmpty()) {
				// Ask the user if he/she wants to continue with the import. If
				// NO then exit now.
				if (JOptionPane.showConfirmDialog(null, sWarningMsg, "Warning",
						JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
					return;
				}
			} // End if(!sWarningMsg.isEmpty())
		} // End if(lEmployeeID != Constants.ADMIN_TOKEN_EMPLOYEE_ID)

		// Determine if we're importing expense data and if the logged in user
		// is the Admin user token
		final boolean bImportingExpenses = sResource
				.equals(Constants.API_RESOURCE_ITEM_EXPENSE_ENTRIES);

		final Hashtable<Component, Boolean> previousState = setState(null,
				false);

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				ImportResult iResult = ImportResult.ALLOK;
				BufferedReader brReader = null;
				boolean formatErrors = false;

				try {
					// Get the selected formatter object from the Format
					// drop-down
					IImportFormatter fFormatter = (IImportFormatter) m_ddlFormat
							.getSelectedItem();

					// Open the file for reading
					String importFilename = m_txtImportFrom.getText();
					FileReader frReader = new FileReader(importFilename);
					brReader = new BufferedReader(frReader);
					boolean bFirstLine = true;
					boolean bHaveError = false;

					// Start off the root XML (e.g. <Clients>) for the request
					// that will
					// be passed to the REST API
					String sXML = ("<" + sRootElementName + ">");

					int lineNum = 1;

					Writer formatLog = setupValidationLogger(importFilename);

					// Loop while all is OK (returns EndOfFile if the end of the
					// file is
					// reached. Returns Error if there was a problem)
					do {
						// Make sure the values in the Destination fields are
						// cleared so
						// that a previous loop's data does not impact the
						// current loop.
						clearMappingValues();
						lineNum++;
						// Read in the current record. If we hit the end of the
						// file
						// then...(no record was read in so exit the loop now)
						iResult = fFormatter.ReadRecord(brReader, bFirstLine,
								m_alColumnsInTheFile, m_alCurrentMappings);

						// Change the flag to no longer indicate that we're at
						// the first
						// line in the file
						bFirstLine = false;

						if (iResult != ImportResult.ENDOFFILE
								&& iResult.getStatus() != Result.Error) {
							ImportResult iLogicResult = LogicValidaiton(
									sResource, m_alCurrentMappings);
							int count = iLogicResult.getFormatErrors().size();
							if (count > 0) {
								ArrayList<String> newErrors = new ArrayList<String>(
										iResult.getFormatErrors());
								newErrors.addAll(iLogicResult.getFormatErrors());
								iResult = new ImportResult(Result.FormatError,
										newErrors);
							}
						}

						if (iResult == ImportResult.ENDOFFILE) {
							break;
						} else if (iResult.getStatus() == IImportFormatter.Result.Error) {
							bHaveError = true;
							break;
						} else if (iResult.getStatus() == Result.FormatError) {
							logFormatErrors(formatLog, lineNum,
									iResult.getFormatErrors());
							formatErrors = true;
							continue;
						}

						if (!formatErrors) {
							// Build up the XML for the current Main Element
							// (e.g.
							// <Client>...</Client>)
							sXML += buildXMLForMainElement(sMainElementName,
									bImportingExpenses, m_alCurrentMappings);
						}
					} while (iResult == ImportResult.ALLOK
							|| iResult.getStatus() == Result.FormatError);

					try {
						formatLog.flush();
						formatLog.close();
					} catch (IOException e) {
					}

					// Only proceed if all is ok...
					if (!bHaveError && !formatErrors) {
						// Close off the root XML (e.g. </Clients>) and send the
						// XML to
						// the REST API to have the data inserted (POST). If an
						// error
						// was displayed to the user
						// then flag that there was an error so that the user
						// doesn't
						// get the 'Done' prompt (so that the user doesn't have
						// to deal
						// with two prompts)
						sXML += ("</" + sRootElementName + ">");

						// This comes in handy from time-to-time so I'm leaving
						// it here
						// for debugging purposes.
						// logXML(sXML);

						String sURI = CResourceHelper.getURIForResource(
								sResource, false, lEmployeeID, null, null);// the
																			// dates
																			// are
																			// not
																			// used
																			// by
																			// an
																			// import
						APIRequestResult arResult = CRESTAPIHelper
								.makeAPIRequest(sURI, "POST", sXML,
										m_UILogic.getConsumerSecret(),
										m_UILogic.getDataAccessToken());
						if (arResult.getDisplayedError()) {
							iResult = new ImportResult(Result.Error);
						}

						// Log the response from the call
						logResponse(arResult);

					} // End if(!bHaveError)

					if (formatErrors)
						ShowValidationErrorBox(importFilename);

					// if (formatErrors) JOptionPane.showMessageDialog(null,
					// "There were validation errors in the input file.\nNothing was imported.\nPlease check log file.",
					// "Formatting Errors", JOptionPane.WARNING_MESSAGE);
				} catch (FileNotFoundException e) {
					JOptionPane.showMessageDialog(null, e.getMessage(),
							"Error", JOptionPane.ERROR_MESSAGE);
					iResult = new ImportResult(Result.Error, e.getMessage());
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(),
							"Error", JOptionPane.ERROR_MESSAGE);
					iResult = new ImportResult(Result.Error, e1.getMessage());
				} finally { // Happens whether or not there was an exception...
					// If we have a reader object then make sure it's closed
					try {
						if (brReader != null) {
							brReader.close();
						}
					} catch (IOException e) {
						JOptionPane.showMessageDialog(null, e.getMessage(),
								"Error", JOptionPane.ERROR_MESSAGE);
						iResult = new ImportResult(Result.Error, e.getMessage());
					}
				} // End of the finally block

				// If there were no errors then tell the user we're done
				if (iResult.getStatus() != Result.Error && !formatErrors) {
					JOptionPane.showMessageDialog(null, "Import completed successfully",
							"Import Complete", JOptionPane.INFORMATION_MESSAGE);
				}

				setState(previousState, true);
			}
		});

		thread.start();
	}

	private void clearValidationState() {
		timeEntries.clear();
		validationLookup.clear();
	}

	protected Writer setupValidationLogger(String importFilename)
			throws IOException {
		String logFilename = validationLogFilename(importFilename);
		Writer formatLog = new BufferedWriter(new FileWriter(logFilename));
		return formatLog;
	}

	protected String validationLogFilename(String importFilename) {
		String logFilename = importFilename.substring(
				importFilename.lastIndexOf('\\') + 1).replace('.', '_')
				+ "_validation.log";
		return logFilename;
	}

	protected void ShowValidationErrorBox(String importFilename) {
		// for copying style
		JLabel label = new JLabel();
		Font font = label.getFont();

		// create some css from the label's font
		StringBuffer style = new StringBuffer("font-family:" + font.getFamily()
				+ ";");
		style.append("font-weight:" + (font.isBold() ? "bold" : "normal") + ";");
		style.append("font-size:" + font.getSize() + "pt;");

		// html content
		JEditorPane ep = new JEditorPane("text/html", "<html><body style=\""
				+ style + "\">"
				+ "There were validation errors in the input file.<br/>"
				+ "<a href=\"file://" + validationLogFilename(importFilename)
				+ "\">Click here</a> to check the log file for details."
				+ "</body></html>");

		// handle link events
		ep.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
					String logUri = e.getURL().toString();
					String logFilename = logUri.substring(logUri
							.lastIndexOf('/') + 1);
					try {
						Desktop.getDesktop().open(new File(logFilename));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		ep.setEditable(false);
		ep.setBackground(label.getBackground());

		// show
		JOptionPane.showMessageDialog(null, ep);
	}

	private HashMap<String, HashMap<String, Boolean>> validationLookup = new HashMap<String, HashMap<String, Boolean>>();
	private HashMap<Long, HashMap<Date, Double>> timeEntries = new HashMap<Long, HashMap<Date, Double>>();
	private final HashMap<String, RowValidator> validations = new HashMap<String, RowValidator>();

	private final void initializeValidation() {
		validations.put(Constants.API_RESOURCE_ITEM_TIME_ENTRIES,
				new RowValidator() {

					private double empHours;
					private long empId;
					private Date empDate;

					@Override
					public void validate(List<String> errors) {
						if (empDate != null && empHours > 0 && empId > 0) {
							HashMap<Date, Double> entries;
							if (!timeEntries.containsKey(empId)) {
								entries = new HashMap<Date, Double>();
								timeEntries.put(empId, entries);
							} else {
								entries = timeEntries.get(empId);
							}

							double total = 0;

							if (!entries.containsKey(empDate)) {
								total = empHours;
							} else {
								total = entries.get(empDate) + empHours;
								entries.remove(empDate);
							}

							if (total > 24) {
								errors.add(String
										.format("Employee (ID #%d) logged more than 24 hours on %tY-%tm-%td",
												empId, empDate, empDate,
												empDate));
								total = 24;
							}

							entries.put(empDate, total);

							empHours = 0;
							empId = 0;
							empDate = null;

						} else {
							if (empDate == null) {
								errors.add(String.format("Date is required, but not specified."));
							}
							if (empHours <= 0) {
								errors.add(String.format("Total Hours must be greater than 0, currently %.1f", empHours));
							}
							if (empId == 0) {
								errors.add("Employee Id was not specified");
							}
						}
					}

					@Override
					public void accumulate(String elemName, String value) {
						if (elemName.equalsIgnoreCase("TotalHours")) {
							try {
								empHours = Conversions.toDouble(value);
							} catch (ParseException e) {
								// We don't do anything as we should have
								// already validated this and it could result in
								// double error messages.
							}
						}
						if (elemName.equalsIgnoreCase("EmployeeId")) {
							try {
								empId = Long.parseLong(value);
							} catch (Exception e) {
								System.out.print(e.getMessage());
								// We don't do anything as we should have
								// already validated this and it could result in
								// double error messages.
							}
						}
						if (elemName.equalsIgnoreCase("Date")) {
							try {
								empDate = Conversions.toDate(value, false);
							} catch (ParseException e) {
								// We don't do anything as we should have
								// already validated this and it could result in
								// double error messages.
							}
						}
					}
				});

		validations.put(Constants.API_RESOURCE_ITEM_EXPENSE_CATEGORIES,
				new RowValidator() {

					private boolean hasTUnit;
					private boolean hasDescription;

					@Override
					public void accumulate(String name, String value) {
						if (name.equalsIgnoreCase("Unit")
								&& value.equalsIgnoreCase("T")) {
							hasTUnit = true;
						}
						if (name.equalsIgnoreCase("UnitDescription")
								&& !value.isEmpty()) {
							hasDescription = true;
						}
					}

					@Override
					public void validate(List<String> errors) {
						if (hasTUnit && !hasDescription) {
							errors.add("When Unit = T, Description is required");
						}
						hasTUnit = false;
						hasDescription = false;
					}
				});

		validations.put(Constants.API_RESOURCE_ITEM_PROJECTS,
				new RowValidator() {

					private boolean fixedBilling;
					private boolean fixedCost;

					private CFieldItem fixedCostField;

					@Override
					void validate(List<String> errors) {
						if (fixedBilling && !fixedCost) {
							errors.add("Billing By set to \"Fixed Cost\", but no Fixed Cost value provided");
						}
						fixedBilling = false;
						fixedCost = false;
					}

					@Override
					protected void accumulate(String name, String value) {
						if (name.equalsIgnoreCase("BillingBy")
								&& value.equalsIgnoreCase("F")) {
							fixedBilling = true;
						}
						if (name.equalsIgnoreCase("Amount") && !value.isEmpty()) {
							fixedCost = true;
						}
					}
				});

	}

	private ImportResult LogicValidaiton(String sResource,
			ArrayList<CFieldItemMap> mappings) {

		List<String> errors = new ArrayList<String>();

		RowValidator validator = null;

		if (validations.containsKey(sResource)) {
			validator = validations.get(sResource);
		}

		for (CFieldItemMap map : mappings) {
			CFieldItem item = map.getDestinationItem();
			if (item instanceof CIdFieldItem) {
				CIdFieldItem idField = (CIdFieldItem) item;
				String source = idField.getSource().toString();
				String id = idField.getValue().trim();
				boolean performServerLookup = true;

				if (validationLookup.containsKey(source)) {
					HashMap<String, Boolean> resourceList = validationLookup
							.get(source);
					if (resourceList.containsKey(id)) {
						performServerLookup = false;
						if (!resourceList.get(id)) {
							errors.add(String.format(
									"the Id %s was not found in the %s table.",
									id, source));
						}
					}
				}

				if (performServerLookup) {
					if (id.isEmpty()) {
						errors.add(String.format(
								"{0} is an Id field but is empty.",
								idField.getCaption(), source));
					} else {
						String uri = CRESTAPIHelper.buildURI(source + "/" + id,
								"", "3");
						APIRequestResult result = CRESTAPIHelper
								.makeAPIRequest(uri, "GET", null,
										m_UILogic.getConsumerSecret(),
										m_UILogic.getDataAccessToken());
						Document doc = result.getResultDocument();
						NodeList list = doc.getElementsByTagName(source);
						if (list.getLength() == 0) {
							errors.add(String.format(
									"the Id '%s' was not found in the %s table.",
									id, source));
						} else {
							Node root = doc.getElementsByTagName(source)
									.item(0);
							NodeList children = root.getChildNodes();

							HashMap<String, Boolean> target;
							if (!validationLookup.containsKey(source)) {
								target = new HashMap<String, Boolean>();
								validationLookup.put(source, target);
							} else {
								target = validationLookup.get(source);
							}

							if (children.getLength() < 1) {
								errors.add(String
										.format("the Id %s was not found in the %s table.",
												id, source));
								target.put(id, false);
							} else {
								target.put(id, true);
							}
						}
					}
				}
			}

			if (validator != null) {
				validator.accumulate(item);
			}

		}

		if (validator != null) {
			validator.fixUp(mappings);
			validator.validate(errors);
		}

		return new ImportResult(errors.size() > 0 ? Result.FormatError
				: Result.AllOK, errors);
	}

	private void logFormatErrors(Writer formatLog, int lineNum,
			List<String> formatErrors) {
		try {
			StringBuilder builder = new StringBuilder();

			for (String error : formatErrors) {
				if (builder.length() > 0)
					builder.append(", ");
				builder.append(error);
			}

			formatLog.write(String.format("Line %d: %s", lineNum,
					builder.toString()));
			formatLog.write("\r\n");
		} catch (IOException e) {
		}
	}

	private void logXML(String sXML) {
		FileWriter fwWriter = null;
		try {
			// Write out the file
			fwWriter = new FileWriter("ImportData.xml", false);
			fwWriter.write(sXML);
		} catch (IOException e) {
		} finally {
			try {
				if (fwWriter != null) {
					fwWriter.flush();
					fwWriter.close();
				}
			} catch (IOException e) {
			}
		}
	}

	// Validates to make sure the necessary data is present in order to do an
	// Import
	private boolean validateForImport() {
		// If there is no Import From path then...
		if (m_txtImportFrom.getText().isEmpty()) {
			JOptionPane.showMessageDialog(null,
					"Please specify a file to import", "Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		} // End if(m_txtImportFrom.getText().isEmpty())

		// If no fields have been mapped then...
		if (m_tmMappingModel.getRowCount() == 0) {
			JOptionPane.showMessageDialog(null,
					"Please map at least one field for import", "Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		} // End if(m_tmMappingModel.getRowCount() == 0)

		// We made it to this point. All is OK.
		return true;
	}

	// Make sure the values in the Destination fields are cleared so that a
	// previous loop's data does not impact the current loop.
	// Also remove any mappings added during fixUp.
	private void clearMappingValues() {
		try {
			// Loop through the mappings list clearing each Destination item's
			// value

			ArrayList<CFieldItemMap> toRemove = new ArrayList<CFieldItemMap>();

			for (CFieldItemMap fiFieldItemMap : m_alCurrentMappings) {
				if (fiFieldItemMap.getSourceItem() == null)
					toRemove.add(fiFieldItemMap);
				else
					fiFieldItemMap.getDestinationItem().setValue("", true);
			}

			for (CFieldItemMap item : toRemove) {
				m_alCurrentMappings.remove(item);
			}
		} catch (Exception e) {
		}
	}

	// Helper to build up the XML for a Main Element
	// / <history>
	// / <modified author="C. Gerard Gallant" date="2012-03-28"
	// reason="I should write a string builder class for XML (if one doesn't already exist) because I forgot to encode the fiDestination.getValue() value for special characters like '&' and '<'."/>
	// / <modified author="C. Gerard Gallant" date="2012-05-31"
	// reason="Changed the CXMLHelper function call from fixXmlString to encodeTextForElement because the encoding of the single & double quote characters was causing a parse error in the REST API and a Bad Request error to be returned. The import would fail if any of the entries had a single or double quote character."/>
	// / </history>
	private String buildXMLForMainElement(String sMainElementName,
			boolean bImportingExpenses,
			ArrayList<CFieldItemMap> alCurrentMappings) {
		CFieldItem fiDestination = null;
		String sElementName = "";
		boolean bExpenseEntryOpeningTagsAdded = false; // Don't want to add
														// closing tags if no
														// opening tags were
														// added in the loop
		boolean bFixedCostOpeningTagsAdded = false;
		boolean bCustomFieldsOpeningTagAdded = false;
		boolean bImportingProjects = sMainElementName == CResourceHelper.MAIN_ELEMENT_NAME_FOR_PROJECTS;

		// Start off our main element (e.g. <Client>)
		String sReturnXML = ("<" + sMainElementName + ">");

		// Loop through our ArrayList of mappings (the same source column could
		// be mapped to multiple destination fields so we can't simply stop
		// looping if we
		// find a match)...
		for (CFieldItemMap fiFieldItemMap : m_alCurrentMappings) {
			// Grab the current destination item and grab the destination item's
			// element name
			fiDestination = fiFieldItemMap.getDestinationItem();
			sElementName = fiDestination.getElementName();

			// If we're importing Expenses AND have not yet added the opening
			// tags for the Expense Entries AND we're no longer dealing with the
			// main element
			// then...(we've hit the list of expense entries)
			if (bImportingExpenses
					&& !bExpenseEntryOpeningTagsAdded
					&& !fiDestination.getRootElementName().equals(
							sMainElementName)) {
				// Start off our <ExpenseEntries><ExpenseEntry> node and flag
				// that we have now added the opening tags
				sReturnXML += ("<"
						+ CResourceHelper.ROOT_ELEMENT_NAME_FOR_EXPENSE_SHEET_ENTRIES
						+ "><"
						+ CResourceHelper.MAIN_ELEMENT_NAME_FOR_EXPENSE_ENTRIES + ">");
				bExpenseEntryOpeningTagsAdded = true;
			} // End if(bImportingExpenses && !bExpenseEntryOpeningTagsAdded &&
				// !fiDestination.getRootElementName().equals(sMainElementName))

			if (bImportingProjects
					&& !bFixedCostOpeningTagsAdded
					&& fiDestination.getRootElementName().equals(
							CResourceHelper.ROOT_ELEMENT_NAME_FOR_FIXEDCOSTS)) {

				if (fiDestination.getValue().isEmpty())
					continue;

				sReturnXML += ("<"
						+ CResourceHelper.ROOT_ELEMENT_NAME_FOR_FIXEDCOSTS
						+ "><"
						+ CResourceHelper.MAIN_ELEMENT_NAME_FOR_FIXEDCOST + ">");
				sReturnXML += "<ID>-1</ID>";
				bFixedCostOpeningTagsAdded = true;
			}

			if (bFixedCostOpeningTagsAdded
					&& !fiDestination.getRootElementName().equals(
							CResourceHelper.ROOT_ELEMENT_NAME_FOR_FIXEDCOSTS)) {
				sReturnXML += ("</"
						+ CResourceHelper.MAIN_ELEMENT_NAME_FOR_FIXEDCOST + ">");
				sReturnXML += ("</"
						+ CResourceHelper.ROOT_ELEMENT_NAME_FOR_FIXEDCOSTS + ">");
				bFixedCostOpeningTagsAdded = false;
			}

			if (fiDestination.isCustomTemplate()) {
				if (!bCustomFieldsOpeningTagAdded) {
					sReturnXML += "<"
							+ CResourceHelper.ROOT_ELEMENT_NAME_FOR_CUSTOMFIELDS
							+ ">";
					bCustomFieldsOpeningTagAdded = true;
				}
				sReturnXML += processCustomField(sMainElementName,
						fiDestination);
				continue;
			} else if (bCustomFieldsOpeningTagAdded) {
				sReturnXML += "</"
						+ CResourceHelper.ROOT_ELEMENT_NAME_FOR_CUSTOMFIELDS
						+ ">";
			}

			// Build up the current element's XML containing the current
			// destination item's value (e.g. <ID>100</ID>)
			sReturnXML += ("<" + sElementName + ">"
					+ CXMLHelper.encodeTextForElement(fiDestination.getValue())
					+ "</" + sElementName + ">");
		} // End of the for (CFieldItemMap fiFieldItemMap : m_alCurrentMappings)
			// loop.

		if (bCustomFieldsOpeningTagAdded) {
			sReturnXML += "</"
					+ CResourceHelper.ROOT_ELEMENT_NAME_FOR_CUSTOMFIELDS + ">";
		}
		// If Expense Entries were added then close off our </ExpenseEntries>
		// and </ExpenseEntry> nodes
		if (bExpenseEntryOpeningTagsAdded) {
			sReturnXML += ("</"
					+ CResourceHelper.MAIN_ELEMENT_NAME_FOR_EXPENSE_ENTRIES
					+ "></"
					+ CResourceHelper.ROOT_ELEMENT_NAME_FOR_EXPENSE_SHEET_ENTRIES + ">");
		}

		if (bFixedCostOpeningTagsAdded) {
			sReturnXML += ("</"
					+ CResourceHelper.MAIN_ELEMENT_NAME_FOR_FIXEDCOST + ">");
			sReturnXML += ("</"
					+ CResourceHelper.ROOT_ELEMENT_NAME_FOR_FIXEDCOSTS + ">");
		}
		// Close off our main element (e.g. </Client>) and return the XML to the
		// caller
		sReturnXML += ("</" + sMainElementName + ">");
		return sReturnXML;
	}

	private String processCustomField(String sMainElementName,
			CFieldItem fiDestination) {
		CCustomFieldInfo info = fiDestination.getCustomInfo();
		StringBuffer buffer = new StringBuffer();
		buffer.append("<CustomField><ID>");
		buffer.append(GetDefaultIdForType(sMainElementName));
		buffer.append("</ID><TemplateID>");
		buffer.append(info.getId());
		buffer.append("</TemplateID><Values>");
		List<String> iter = ConvertToValues(fiDestination);
		for (String value : iter) {
			buffer.append("<Value>");
			buffer.append(CXMLHelper.encodeTextForElement(value));
			buffer.append("</Value>");
		}
		buffer.append("</Values></CustomField>");
		return buffer.toString();
	}

	private List<String> ConvertToValues(CFieldItem destination) {
		if (destination.getFieldType() == FieldItemType.MultipleChoice
				|| destination.getFieldType() == FieldItemType.ExclusiveChoice) {
			String values = destination.getValue();
			return Arrays.asList(values.split("\\|"));
		} else {
			return Arrays.asList(new String[] { destination.getValue() });
		}
	}

	private String GetDefaultIdForType(String sMainElementName) {
		if ((sMainElementName
				.equalsIgnoreCase(CResourceHelper.MAIN_ELEMENT_NAME_FOR_EXPENSE_ENTRIES) || sMainElementName
				.equalsIgnoreCase(CResourceHelper.MAIN_ELEMENT_NAME_FOR_TIME_ENTRIES))
				&& m_UILogic.getEmployeeID() != Constants.ADMIN_TOKEN_EMPLOYEE_ID) {

			return "FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF";
		} else {
			return "-1";
		}
	}

	// Logs the response from the API call
	private void logResponse(APIRequestResult aRequestResult) {
		String sData = "";
		boolean bHadError = false;

		StringWriter swWriter = null;
		FileWriter fwWriter = null;

		try {
			// If there was an error, grab the error message
			if (aRequestResult.getHadRequestError()) {
				sData = aRequestResult.getRequestErrorMessage();
				bHadError = true;
			} else { // There was no error from the API call...
				// Get the XML from the Xml Document
				swWriter = new StringWriter();
				StreamResult srResult = new StreamResult(swWriter);
				DOMSource domSource = new DOMSource(aRequestResult
						.getResultDocument().getDocumentElement());

				Transformer tTrans = TransformerFactory.newInstance()
						.newTransformer();
				tTrans.setOutputProperty(
						"{http://xml.apache.org/xslt}indent-amount", "2");
				tTrans.setOutputProperty(OutputKeys.INDENT, "yes");
				tTrans.transform(domSource, srResult);

				sData = swWriter.toString();
				swWriter.close();
				swWriter = null;
			} // End f(aRequestResult.getHadRequestError())

			// Write out the file
			fwWriter = new FileWriter(getFileNameForResultOutput(bHadError),
					false);
			fwWriter.write(sData);
		} catch (TransformerConfigurationException e) {
		} catch (TransformerFactoryConfigurationError e) {
		} catch (IOException e) {
		} catch (TransformerException e) {
		} finally {
			try {
				if (swWriter != null) {
					swWriter.close();
				}
				if (fwWriter != null) {
					fwWriter.flush();
					fwWriter.close();
				}
			} catch (IOException e) {
			}
		}
	}

	// Returns a file name for the output (e.g. 2012_1_23__2_34_23_Success.xml)
	private String getFileNameForResultOutput(boolean bHadError) {

		Calendar dtToday = Calendar.getInstance();
		return (Integer.toString(dtToday.get(Calendar.YEAR)) + "_"
				+ Integer.toString(dtToday.get(Calendar.MONTH)) + "_"
				+ Integer.toString(dtToday.get(Calendar.DAY_OF_MONTH)) + "__"
				+ Integer.toString(dtToday.get(Calendar.HOUR)) + "_"
				+ Integer.toString(dtToday.get(Calendar.MINUTE)) + "_"
				+ Integer.toString(dtToday.get(Calendar.SECOND)) + "__" + (bHadError ? "Error.txt"
					: "Success.xml"));
	}

	private void loadState() {
		BufferedReader brReader = null;

		try {
			// Read in the XML file's state information
			DocumentBuilderFactory dbfFactory = DocumentBuilderFactory
					.newInstance();
			Document xdDoc = dbfFactory.newDocumentBuilder().parse(
					Constants.IMPORT_STATE_FILE_NAME);

			Element xeDocElement = xdDoc.getDocumentElement();

			// Set the File Path and Destination values
			m_txtImportFrom.setText(CXMLHelper.getChildNodeValue(xeDocElement,
					"FilePath"));
			m_ddlDestination.setSelectedItem(CXMLHelper.getChildNodeValue(
					xeDocElement, "Destination"));

			// Load in the 'Columns in the File' values from our XML...
			m_alColumnsInTheFile = new ArrayList<CFieldItem>();

			Element xeRoot = (Element) xeDocElement.getElementsByTagName(
					"ColumnsInFile").item(0);
			NodeList xnlElements = xeRoot.getElementsByTagName("FieldItem");
			int iIndex = 0, iCount = xnlElements.getLength();
			for (iIndex = 0; iIndex < iCount; iIndex++) {
				m_alColumnsInTheFile.add(new CFieldItem((Element) xnlElements
						.item(iIndex)));
			}

			// Make sure the current mappings array is cleared and then load it
			// from the file
			clearRowsFromMappingTable();
			m_alCurrentMappings = new ArrayList<CFieldItemMap>();

			CFieldItemMap fiFieldItemMap = null;
			xeRoot = (Element) xeDocElement.getElementsByTagName(
					"CurrentMappings").item(0);
			xnlElements = xeRoot.getElementsByTagName("FieldItemMap");
			iCount = xnlElements.getLength();
			for (iIndex = 0; iIndex < iCount; iIndex++) {
				// Create a new Field Item Map object from the current
				// XmlElement and add it to our array list and grid model
				fiFieldItemMap = new CFieldItemMap(
						(Element) xnlElements.item(iIndex));
				m_alCurrentMappings.add(fiFieldItemMap);
				m_tmMappingModel.addRow(new Object[] {
						fiFieldItemMap.getSourceItem(),
						fiFieldItemMap.getDestinationItem() });
			} // End of the for(iIndex = 0; iIndex < iCount; iIndex++) loop.

		} catch (SAXException e) {
		} /*
		 * Don't throw errors. The state information is a convenience so the
		 * user doesn't have to re-enter/select the information. If the state
		 * information file fails to load don't throw errors.
		 */
		catch (IOException e) {
		} /* Don't throw errors */
		catch (ParserConfigurationException e) {
		}/* Don't throw errors */
		finally { // Happens whether or not there was an exception...
			// If we have a reader object then make sure it's closed
			try {
				if (brReader != null) {
					brReader.close();
				}
			} catch (IOException e) {
			} /* Don't throw errors */
		} // End of the finally block

	}

	private void saveState() {
		BufferedWriter bwWriter = null;

		try {
			// Open up the state information file (overwrite the existing file
			// if it exists)
			FileWriter fwWriter = new FileWriter(
					Constants.IMPORT_STATE_FILE_NAME, false);
			bwWriter = new BufferedWriter(fwWriter);

			// Write the file path and destination to the file
			bwWriter.write(("<?xml version=\"1.0\" encoding=\"UTF-8\" ?><State><FilePath>"
					+ m_txtImportFrom.getText()
					+ "</FilePath><Destination>"
					+ (String) m_ddlDestination.getSelectedItem() + "</Destination><ColumnsInFile>"));

			// Loop through the file's columns adding them to the XML
			String sXML = "";
			for (CFieldItem fiFieldItem : m_alColumnsInTheFile) {
				sXML += fiFieldItem.toXML();
			}

			// Write out the File Columns XML to the file
			bwWriter.write((sXML + "</ColumnsInFile><CurrentMappings>"));

			// Loop through the mappings adding them to the XML
			sXML = "";
			for (CFieldItemMap fiFieldItemMap : m_alCurrentMappings) {
				sXML += fiFieldItemMap.toXML();
			}

			// Write out the Mappings XML to the file
			bwWriter.write((sXML + "</CurrentMappings></State>"));
		} catch (IOException e) {
		} /*
		 * Don't throw errors. The state information is a convenience so the
		 * user doesn't have to re-enter/select the information. If the state
		 * information file fails to load don't throw errors.
		 */
		finally { // Happens whether or not there was an exception...
			// Close the BufferedWriter
			try {
				if (bwWriter != null) {
					bwWriter.flush();
					bwWriter.close();
				}
			} catch (IOException e) {
			} /* Don't throw errors */
		} // End of finally block
	}

	protected Hashtable<Component, Boolean> setState(
			Hashtable<Component, Boolean> previousState, boolean enabled) {
		Hashtable<Component, Boolean> states = new Hashtable<Component, Boolean>();
		states.put(m_ddlDestination, m_ddlDestination.isEnabled());
		states.put(m_ddlFormat, m_ddlFormat.isEnabled());
		states.put(m_txtImportFrom, m_txtImportFrom.isEnabled());
		states.put(cmdBrowse, cmdBrowse.isEnabled());
		states.put(cmdImport, cmdImport.isEnabled());
		states.put(cmdMap, cmdMap.isEnabled());
		states.put(spMapping, spMapping.isEnabled());
		states.put(tblMapping, tblMapping.isEnabled());

		enable(m_ddlDestination, previousState, enabled);
		enable(m_ddlFormat, previousState, enabled);
		enable(m_txtImportFrom, previousState, enabled);
		enable(cmdBrowse, previousState, enabled);
		enable(cmdImport, previousState, enabled);
		enable(cmdMap, previousState, enabled);
		enable(spMapping, previousState, enabled);
		enable(tblMapping, previousState, enabled);

		loadingLabel.setVisible(!enabled);
		m_UILogic.disableTabs(!enabled);

		return states;
	}

	private void enable(Component c, Hashtable<Component, Boolean> s, boolean e) {
		if (s != null && s.containsKey(c)) {
			c.setEnabled(s.get(c));
		} else {
			c.setEnabled(e);
		}
	}
}
