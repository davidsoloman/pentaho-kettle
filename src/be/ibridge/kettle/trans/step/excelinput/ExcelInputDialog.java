 /**********************************************************************
 **                                                                   **
 **               This code belongs to the KETTLE project.            **
 **                                                                   **
 ** Kettle, from version 2.2 on, is released into the public domain   **
 ** under the Lesser GNU Public License (LGPL).                       **
 **                                                                   **
 ** For more details, please read the document LICENSE.txt, included  **
 ** in this project                                                   **
 **                                                                   **
 ** http://www.kettle.be                                              **
 ** info@kettle.be                                                    **
 **                                                                   **
 **********************************************************************/
 
/*
 * Created on 18-mei-2003
 *
 */

package be.ibridge.kettle.trans.step.excelinput;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import be.ibridge.kettle.core.ColumnInfo;
import be.ibridge.kettle.core.Const;
import be.ibridge.kettle.core.Props;
import be.ibridge.kettle.core.Row;
import be.ibridge.kettle.core.dialog.EnterListDialog;
import be.ibridge.kettle.core.dialog.EnterNumberDialog;
import be.ibridge.kettle.core.dialog.EnterSelectionDialog;
import be.ibridge.kettle.core.dialog.PreviewRowsDialog;
import be.ibridge.kettle.core.value.Value;
import be.ibridge.kettle.core.widget.TableView;
import be.ibridge.kettle.trans.Trans;
import be.ibridge.kettle.trans.TransMeta;
import be.ibridge.kettle.trans.TransPreviewFactory;
import be.ibridge.kettle.trans.dialog.TransPreviewProgressDialog;
import be.ibridge.kettle.trans.step.BaseStepDialog;
import be.ibridge.kettle.trans.step.BaseStepMeta;
import be.ibridge.kettle.trans.step.StepDialogInterface;
import be.ibridge.kettle.trans.step.fileinput.FileInputList;
import be.ibridge.kettle.trans.step.textfileinput.DirectoryDialogButtonListenerFactory;
import be.ibridge.kettle.trans.step.textfileinput.TextFileInputMeta;
import be.ibridge.kettle.trans.step.textfileinput.VariableButtonListenerFactory;


public class ExcelInputDialog extends BaseStepDialog implements StepDialogInterface
{
	private static final String[] YES_NO_COMBO = new String[] { Messages.getString("System.Combo.No"), Messages.getString("System.Combo.Yes") };
	
	private CTabFolder   wTabFolder;
	private FormData     fdTabFolder;
	
	private CTabItem     wFileTab, wSheetTab, wContentTab, wErrorTab, wFieldsTab;

	private Composite    wFileComp, wSheetComp, wContentComp, wErrorComp, wFieldsComp;
	private FormData     fdFileComp, fdSheetComp, fdContentComp, fdFieldsComp;

	private Label        wlFilename;
	private Button       wbbFilename; // Browse: add file or directory
	private Button       wbvFilename; // Variable
	private Button       wbdFilename; // Delete
	private Button       wbeFilename; // Edit
	private Button       wbaFilename; // Add or change
	private Text         wFilename;
	private FormData     fdlFilename, fdbFilename, fdbvFilename, fdbdFilename, fdbeFilename, fdbaFilename, fdFilename;

	private Label        wlFilenameList;
	private TableView    wFilenameList;
	private FormData     fdlFilenameList, fdFilenameList;

	private Label        wlFilemask;
	private Text         wFilemask;
	private FormData     fdlFilemask, fdFilemask;

	private Button       wbShowFiles;
	private FormData     fdbShowFiles;

	private Label        wlSheetnameList;
	private TableView    wSheetnameList;
	private FormData     fdlSheetnameList;

	private Button       wbGetSheets;
	private FormData     fdbGetSheets;

	private Label        wlHeader;
	private Button       wHeader;
	private FormData     fdlHeader, fdHeader;
	
	private Label        wlNoempty;
	private Button       wNoempty;
	private FormData     fdlNoempty, fdNoempty;

	private Label        wlStoponempty;
	private Button       wStoponempty;
	private FormData     fdlStoponempty, fdStoponempty;

	private Label        wlInclFilenameField;
	private Text         wInclFilenameField;
	private FormData     fdlInclFilenameField, fdInclFilenameField;

	private Label        wlInclSheetnameField;
	private Text         wInclSheetnameField;
	private FormData     fdlInclSheetnameField, fdInclSheetnameField;

	private Label        wlInclRownumField;
	private Text         wInclRownumField;
	private FormData     fdlInclRownumField, fdInclRownumField;
	
	private Label        wlLimit;
	private Text         wLimit;
	private FormData     fdlLimit, fdLimit;

	private Button       wbGetFields;

	private TableView    wFields;
	private FormData     fdFields;
	
	//	 ERROR HANDLING...
	private Label        wlStrictTypes;
    private Button       wStrictTypes;
    private FormData     fdlStrictTypes, fdStrictTypes;
	
	private Label        wlErrorIgnored;
    private Button       wErrorIgnored;
    private FormData     fdlErrorIgnored, fdErrorIgnored;
    
    private Label        wlSkipErrorLines;
    private Button       wSkipErrorLines;
    private FormData     fdlSkipErrorLines, fdSkipErrorLines;
    
//  New entries for intelligent error handling AKA replay functionality
    // Bad files destination directory
    private Label        wlWarningDestDir;
    private Button       wbbWarningDestDir; // Browse: add file or directory
    private Button       wbvWarningDestDir; // Variable
    private Text         wWarningDestDir;
    private FormData     fdlWarningDestDir, fdbWarningDestDir, fdbvWarningDestDir, fdWarningDestDir;
    private Label        wlWarningExt;
    private Text         wWarningExt;
    private FormData     fdlWarningDestExt, fdWarningDestExt;

    // Error messages files destination directory
    private Label        wlErrorDestDir;
    private Button       wbbErrorDestDir; // Browse: add file or directory
    private Button       wbvErrorDestDir; // Variable
    private Text         wErrorDestDir;
    private FormData     fdlErrorDestDir, fdbErrorDestDir, fdbvErrorDestDir, fdErrorDestDir;
    private Label        wlErrorExt;
    private Text         wErrorExt;
    private FormData     fdlErrorDestExt, fdErrorDestExt;

    // Line numbers files destination directory
    private Label        wlLineNrDestDir;
    private Button       wbbLineNrDestDir; // Browse: add file or directory
    private Button       wbvLineNrDestDir; // Variable
    private Text         wLineNrDestDir;
    private FormData     fdlLineNrDestDir, fdbLineNrDestDir, fdbvLineNrDestDir, fdLineNrDestDir;
    private Label        wlLineNrExt;
    private Text         wLineNrExt;
    private FormData     fdlLineNrDestExt, fdLineNrDestExt;

	private ExcelInputMeta input;
	private int middle;
	private int margin;
	
	private ModifyListener lsMod;
	
	public ExcelInputDialog(Shell parent, Object in, TransMeta transMeta, String sname)
	{
		super(parent, (BaseStepMeta)in, transMeta, sname);
		input=(ExcelInputMeta)in;
	}

	public String open()
	{
		Shell parent = getParent();
		Display display = parent.getDisplay();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN);
 		props.setLook(shell);

		lsMod = new ModifyListener() 
		{
			public void modifyText(ModifyEvent e) 
			{
				input.setChanged();
			}
		};
		changed         = input.hasChanged();
		
		FormLayout formLayout = new FormLayout ();
		formLayout.marginWidth  = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);
		shell.setText(Messages.getString("ExcelInputDialog.DialogTitle"));
		
		middle = props.getMiddlePct();
		margin = Const.MARGIN;

		// Stepname line
		wlStepname=new Label(shell, SWT.RIGHT);
		wlStepname.setText(Messages.getString("System.Label.StepName"));
 		props.setLook(wlStepname);
		fdlStepname=new FormData();
		fdlStepname.left = new FormAttachment(0, 0);
		fdlStepname.top  = new FormAttachment(0, margin);
		fdlStepname.right= new FormAttachment(middle, -margin);
		wlStepname.setLayoutData(fdlStepname);
		wStepname=new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wStepname.setText(stepname);
 		props.setLook(wStepname);
		wStepname.addModifyListener(lsMod);
		fdStepname=new FormData();
		fdStepname.left = new FormAttachment(middle, 0);
		fdStepname.top  = new FormAttachment(0, margin);
		fdStepname.right= new FormAttachment(100, 0);
		wStepname.setLayoutData(fdStepname);

		wTabFolder = new CTabFolder(shell, SWT.BORDER);
 		props.setLook(wTabFolder, Props.WIDGET_STYLE_TAB);
		
		//////////////////////////
		// START OF FILE TAB   ///
		//////////////////////////
		wFileTab=new CTabItem(wTabFolder, SWT.NONE);
		wFileTab.setText(Messages.getString("ExcelInputDialog.FileTab.TabTitle"));
		
		wFileComp = new Composite(wTabFolder, SWT.NONE);
 		props.setLook(wFileComp);

		FormLayout fileLayout = new FormLayout();
		fileLayout.marginWidth  = 3;
		fileLayout.marginHeight = 3;
		wFileComp.setLayout(fileLayout);

		// Filename line
		wlFilename=new Label(wFileComp, SWT.RIGHT);
		wlFilename.setText(Messages.getString("ExcelInputDialog.Filename.Label"));
 		props.setLook(wlFilename);
		fdlFilename=new FormData();
		fdlFilename.left = new FormAttachment(0, 0);
		fdlFilename.top  = new FormAttachment(0, 0);
		fdlFilename.right= new FormAttachment(middle, -margin);
		wlFilename.setLayoutData(fdlFilename);

		wbbFilename=new Button(wFileComp, SWT.PUSH| SWT.CENTER);
 		props.setLook(wbbFilename);
		wbbFilename.setText(Messages.getString("System.Button.Browse"));
		wbbFilename.setToolTipText(Messages.getString("System.Tooltip.BrowseForFileOrDirAndAdd"));
		fdbFilename=new FormData();
		fdbFilename.right= new FormAttachment(100, 0);
		fdbFilename.top  = new FormAttachment(0, 0);
		wbbFilename.setLayoutData(fdbFilename);

		wbvFilename=new Button(wFileComp, SWT.PUSH| SWT.CENTER);
 		props.setLook(wbvFilename);
		wbvFilename.setText(Messages.getString("System.Button.Variable"));
		wbvFilename.setToolTipText("Insert a variable in the filename or directory");
		fdbvFilename=new FormData();
		fdbvFilename.right= new FormAttachment(wbbFilename, -margin);
		fdbvFilename.top  = new FormAttachment(0, 0);
		wbvFilename.setLayoutData(fdbvFilename);

		wbaFilename=new Button(wFileComp, SWT.PUSH| SWT.CENTER);
 		props.setLook(wbaFilename);
		wbaFilename.setText(Messages.getString("ExcelInputDialog.FilenameAdd.Button"));
		wbaFilename.setToolTipText(Messages.getString("ExcelInputDialog.FilenameAdd.Tooltip"));
		fdbaFilename=new FormData();
		fdbaFilename.right= new FormAttachment(wbvFilename, -margin);
		fdbaFilename.top  = new FormAttachment(0, 0);
		wbaFilename.setLayoutData(fdbaFilename);

		wFilename=new Text(wFileComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wFilename);
		wFilename.addModifyListener(lsMod);
		fdFilename=new FormData();
		fdFilename.left = new FormAttachment(middle, 0);
		fdFilename.right= new FormAttachment(wbaFilename, -margin);
		fdFilename.top  = new FormAttachment(0, 0);
		wFilename.setLayoutData(fdFilename);

		wlFilemask=new Label(wFileComp, SWT.RIGHT);
		wlFilemask.setText(Messages.getString("ExcelInputDialog.Filemask.Label"));
 		props.setLook(wlFilemask);
		fdlFilemask=new FormData();
		fdlFilemask.left = new FormAttachment(0, 0);
		fdlFilemask.top  = new FormAttachment(wFilename, margin);
		fdlFilemask.right= new FormAttachment(middle, -margin);
		wlFilemask.setLayoutData(fdlFilemask);
		wFilemask=new Text(wFileComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wFilemask);
		wFilemask.addModifyListener(lsMod);
		fdFilemask=new FormData();
		fdFilemask.left = new FormAttachment(middle, 0);
		fdFilemask.top  = new FormAttachment(wFilename, margin);
		fdFilemask.right= new FormAttachment(100, 0);
		wFilemask.setLayoutData(fdFilemask);

		// Filename list line
		wlFilenameList=new Label(wFileComp, SWT.RIGHT);
		wlFilenameList.setText(Messages.getString("ExcelInputDialog.FilenameList.Label"));
 		props.setLook(wlFilenameList);
		fdlFilenameList=new FormData();
		fdlFilenameList.left = new FormAttachment(0, 0);
		fdlFilenameList.top  = new FormAttachment(wFilemask, margin);
		fdlFilenameList.right= new FormAttachment(middle, -margin);
		wlFilenameList.setLayoutData(fdlFilenameList);

		// Buttons to the right of the screen...
		wbdFilename=new Button(wFileComp, SWT.PUSH| SWT.CENTER);
 		props.setLook(wbdFilename);
		wbdFilename.setText(Messages.getString("ExcelInputDialog.FilenameDelete.Button"));
		wbdFilename.setToolTipText(Messages.getString("ExcelInputDialog.FilenameDelete.Tooltip"));
		fdbdFilename=new FormData();
		fdbdFilename.right = new FormAttachment(100, 0);
		fdbdFilename.top  = new FormAttachment (wFilemask, 40);
		wbdFilename.setLayoutData(fdbdFilename);

		wbeFilename=new Button(wFileComp, SWT.PUSH| SWT.CENTER);
 		props.setLook(wbeFilename);
		wbeFilename.setText(Messages.getString("ExcelInputDialog.FilenameEdit.Button"));
		wbeFilename.setToolTipText(Messages.getString("ExcelInputDialog.FilenameEdit.Button"));
		fdbeFilename=new FormData();
		fdbeFilename.right = new FormAttachment(100, 0);
		fdbeFilename.top  = new FormAttachment (wbdFilename, margin);
		wbeFilename.setLayoutData(fdbeFilename);

		wbShowFiles=new Button(wFileComp, SWT.PUSH| SWT.CENTER);
 		props.setLook(wbShowFiles);
		wbShowFiles.setText(Messages.getString("ExcelInputDialog.ShowFiles.Button"));
		fdbShowFiles=new FormData();
		fdbShowFiles.left   = new FormAttachment(middle, 0);
		fdbShowFiles.bottom = new FormAttachment(100, -margin);
		wbShowFiles.setLayoutData(fdbShowFiles);

		ColumnInfo[] colinfo=new ColumnInfo[3];
		colinfo[ 0]=new ColumnInfo(Messages.getString("ExcelInputDialog.FileDir.Column"),  ColumnInfo.COLUMN_TYPE_TEXT,    false);
		colinfo[ 1]=new ColumnInfo(Messages.getString("ExcelInputDialog.Wildcard.Column"),        ColumnInfo.COLUMN_TYPE_TEXT,    false );
		colinfo[ 1].setToolTip(Messages.getString("ExcelInputDialog.Wildcard.Tooltip"));
		colinfo[ 2]=new ColumnInfo(Messages.getString("ExcelInputDialog.Required.Column"),        ColumnInfo.COLUMN_TYPE_CCOMBO,  YES_NO_COMBO );
		colinfo[ 2].setToolTip(Messages.getString("ExcelInputDialog.Required.Tooltip"));
		
		wFilenameList = new TableView(wFileComp, 
						      SWT.FULL_SELECTION | SWT.SINGLE | SWT.BORDER, 
						      colinfo, 
						      input.getFileName().length,  
						      lsMod,
							  props
						      );
 		props.setLook(wFilenameList);
		fdFilenameList=new FormData();
		fdFilenameList.left   = new FormAttachment(middle, 0);
		fdFilenameList.right  = new FormAttachment(wbdFilename, -margin);
		fdFilenameList.top    = new FormAttachment(wFilemask, margin);
		fdFilenameList.bottom = new FormAttachment(wbShowFiles, -margin);
		wFilenameList.setLayoutData(fdFilenameList);

	
		fdFileComp=new FormData();
		fdFileComp.left  = new FormAttachment(0, 0);
		fdFileComp.top   = new FormAttachment(0, 0);
		fdFileComp.right = new FormAttachment(100, 0);
		fdFileComp.bottom= new FormAttachment(100, 0);
		wFileComp.setLayoutData(fdFileComp);
	
		wFileComp.layout();
		wFileTab.setControl(wFileComp);
		
		/////////////////////////////////////////////////////////////
		/// END OF FILE TAB
		/////////////////////////////////////////////////////////////

		//////////////////////////
		// START OF SHEET TAB  ///
		//////////////////////////
		wSheetTab=new CTabItem(wTabFolder, SWT.NONE);
		wSheetTab.setText(Messages.getString("ExcelInputDialog.SheetsTab.TabTitle"));
		
		wSheetComp = new Composite(wTabFolder, SWT.NONE);
 		props.setLook(wSheetComp);

		FormLayout sheetLayout = new FormLayout();
		sheetLayout.marginWidth  = 3;
		sheetLayout.marginHeight = 3;
		wSheetComp.setLayout(sheetLayout);
		
		wbGetSheets=new Button(wSheetComp, SWT.PUSH| SWT.CENTER);
 		props.setLook(wbGetSheets);
		wbGetSheets.setText(Messages.getString("ExcelInputDialog.GetSheets.Button"));
		fdbGetSheets=new FormData();
		fdbGetSheets.left   = new FormAttachment(middle, 0);
		fdbGetSheets.bottom = new FormAttachment(100, -margin);
		wbGetSheets.setLayoutData(fdbGetSheets);

		wlSheetnameList=new Label(wSheetComp, SWT.RIGHT);
		wlSheetnameList.setText(Messages.getString("ExcelInputDialog.SheetNameList.Label"));
 		props.setLook(wlSheetnameList);
		fdlSheetnameList=new FormData();
		fdlSheetnameList.left = new FormAttachment(0, 0);
		fdlSheetnameList.top  = new FormAttachment(wFilename, margin);
		fdlSheetnameList.right= new FormAttachment(middle, -margin);
		wlSheetnameList.setLayoutData(fdlSheetnameList);
		
		ColumnInfo[] shinfo=new ColumnInfo[3];
		shinfo[ 0]=new ColumnInfo(Messages.getString("ExcelInputDialog.SheetName.Column"),     ColumnInfo.COLUMN_TYPE_TEXT,    false);
		shinfo[ 1]=new ColumnInfo(Messages.getString("ExcelInputDialog.StartRow.Column"),      ColumnInfo.COLUMN_TYPE_TEXT,    false );
		shinfo[ 2]=new ColumnInfo(Messages.getString("ExcelInputDialog.StartColumn.Column"),   ColumnInfo.COLUMN_TYPE_TEXT,    false );
		
		wSheetnameList = new TableView(wSheetComp, 
						      SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER, 
						      shinfo, 
						      input.getSheetName().length,  
						      lsMod,
							  props
						      );
 		props.setLook(wSheetnameList);
		fdFilenameList=new FormData();
		fdFilenameList.left   = new FormAttachment(middle, 0);
		fdFilenameList.right  = new FormAttachment(100, 0);
		fdFilenameList.top    = new FormAttachment(0, 0);
		fdFilenameList.bottom = new FormAttachment(wbGetSheets, -margin);
		wSheetnameList.setLayoutData(fdFilenameList);
		
		fdSheetComp=new FormData();
		fdSheetComp.left  = new FormAttachment(0, 0);
		fdSheetComp.top   = new FormAttachment(0, 0);
		fdSheetComp.right = new FormAttachment(100, 0);
		fdSheetComp.bottom= new FormAttachment(100, 0);
		wSheetComp.setLayoutData(fdSheetComp);
	
		wSheetComp.layout();
		wSheetTab.setControl(wSheetComp);
		
		/////////////////////////////////////////////////////////////
		/// END OF SHEET TAB
		/////////////////////////////////////////////////////////////

		//////////////////////////
		// START OF CONTENT TAB///
		///
		wContentTab=new CTabItem(wTabFolder, SWT.NONE);
		wContentTab.setText(Messages.getString("ExcelInputDialog.ContentTab.TabTitle"));

		FormLayout contentLayout = new FormLayout ();
		contentLayout.marginWidth  = 3;
		contentLayout.marginHeight = 3;
		
		wContentComp = new Composite(wTabFolder, SWT.NONE);
 		props.setLook(wContentComp);
		wContentComp.setLayout(contentLayout);

		// Header checkbox
		wlHeader=new Label(wContentComp, SWT.RIGHT);
		wlHeader.setText(Messages.getString("ExcelInputDialog.Header.Label"));
 		props.setLook(wlHeader);
		fdlHeader=new FormData();
		fdlHeader.left = new FormAttachment(0, 0);
		fdlHeader.top  = new FormAttachment(0, 0);
		fdlHeader.right= new FormAttachment(middle, -margin);
		wlHeader.setLayoutData(fdlHeader);
		wHeader=new Button(wContentComp, SWT.CHECK);
 		props.setLook(wHeader);
		fdHeader=new FormData();
		fdHeader.left = new FormAttachment(middle, 0);
		fdHeader.top  = new FormAttachment(0, 0);
		fdHeader.right= new FormAttachment(100, 0);
		wHeader.setLayoutData(fdHeader);
		wHeader.addSelectionListener(new SelectionAdapter() 
	        {
				public void widgetSelected(SelectionEvent arg0)
				{
					enableFields();
				}
			});

		wlNoempty=new Label(wContentComp, SWT.RIGHT);
		wlNoempty.setText(Messages.getString("ExcelInputDialog.NoEmpty.Label"));
 		props.setLook(wlNoempty);
		fdlNoempty=new FormData();
		fdlNoempty.left = new FormAttachment(0, 0);
		fdlNoempty.top  = new FormAttachment(wHeader, margin);
		fdlNoempty.right= new FormAttachment(middle, -margin);
		wlNoempty.setLayoutData(fdlNoempty);
		wNoempty=new Button(wContentComp, SWT.CHECK );
 		props.setLook(wNoempty);
		wNoempty.setToolTipText(Messages.getString("ExcelInputDialog.NoEmpty.Tooltip"));
		fdNoempty=new FormData();
		fdNoempty.left = new FormAttachment(middle, 0);
		fdNoempty.top  = new FormAttachment(wHeader, margin);
		fdNoempty.right= new FormAttachment(100, 0);
		wNoempty.setLayoutData(fdNoempty);

		wlStoponempty=new Label(wContentComp, SWT.RIGHT);
		wlStoponempty.setText(Messages.getString("ExcelInputDialog.StopOnEmpty.Label"));
 		props.setLook(wlStoponempty);
		fdlStoponempty=new FormData();
		fdlStoponempty.left = new FormAttachment(0, 0);
		fdlStoponempty.top  = new FormAttachment(wNoempty, margin);
		fdlStoponempty.right= new FormAttachment(middle, -margin);
		wlStoponempty.setLayoutData(fdlStoponempty);
		wStoponempty=new Button(wContentComp, SWT.CHECK );
 		props.setLook(wStoponempty);
		wStoponempty.setToolTipText(Messages.getString("ExcelInputDialog.StopOnEmpty.Tooltip"));
		fdStoponempty=new FormData();
		fdStoponempty.left = new FormAttachment(middle, 0);
		fdStoponempty.top  = new FormAttachment(wNoempty, margin);
		fdStoponempty.right= new FormAttachment(100, 0);
		wStoponempty.setLayoutData(fdStoponempty);

		wlInclFilenameField=new Label(wContentComp, SWT.RIGHT);
		wlInclFilenameField.setText(Messages.getString("ExcelInputDialog.InclFilenameField.Label"));
 		props.setLook(wlInclFilenameField);
		fdlInclFilenameField=new FormData();
		fdlInclFilenameField.left  = new FormAttachment(0, 0);
		fdlInclFilenameField.top   = new FormAttachment(wStoponempty, margin);
		fdlInclFilenameField.right = new FormAttachment(middle, -margin);
		wlInclFilenameField.setLayoutData(fdlInclFilenameField);
		wInclFilenameField=new Text(wContentComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wInclFilenameField);
		wInclFilenameField.addModifyListener(lsMod);
		fdInclFilenameField=new FormData();
		fdInclFilenameField.left = new FormAttachment(middle, 0);
		fdInclFilenameField.top  = new FormAttachment(wStoponempty, margin);
		fdInclFilenameField.right= new FormAttachment(100, 0);
		wInclFilenameField.setLayoutData(fdInclFilenameField);

		wlInclSheetnameField=new Label(wContentComp, SWT.RIGHT);
		wlInclSheetnameField.setText(Messages.getString("ExcelInputDialog.InclSheetnameField.Label"));
 		props.setLook(wlInclSheetnameField);
		fdlInclSheetnameField=new FormData();
		fdlInclSheetnameField.left  = new FormAttachment(0, 0);
		fdlInclSheetnameField.top   = new FormAttachment(wInclFilenameField, margin);
		fdlInclSheetnameField.right = new FormAttachment(middle, -margin);
		wlInclSheetnameField.setLayoutData(fdlInclSheetnameField);
		wInclSheetnameField=new Text(wContentComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wInclSheetnameField);
		wInclSheetnameField.addModifyListener(lsMod);
		fdInclSheetnameField=new FormData();
		fdInclSheetnameField.left = new FormAttachment(middle, 0);
		fdInclSheetnameField.top  = new FormAttachment(wInclFilenameField, margin);
		fdInclSheetnameField.right= new FormAttachment(100, 0);
		wInclSheetnameField.setLayoutData(fdInclSheetnameField);
		
		wlInclRownumField=new Label(wContentComp, SWT.RIGHT);
		wlInclRownumField.setText(Messages.getString("ExcelInputDialog.InclRownumField.Label"));
 		props.setLook(wlInclRownumField);
		fdlInclRownumField=new FormData();
		fdlInclRownumField.left  = new FormAttachment(0, 0);
		fdlInclRownumField.top   = new FormAttachment(wInclSheetnameField, margin);
		fdlInclRownumField.right = new FormAttachment(middle, -margin);
		wlInclRownumField.setLayoutData(fdlInclRownumField);
		wInclRownumField=new Text(wContentComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wInclRownumField);
		wInclRownumField.addModifyListener(lsMod);
		fdInclRownumField=new FormData();
		fdInclRownumField.left = new FormAttachment(middle, 0);
		fdInclRownumField.top  = new FormAttachment(wInclSheetnameField, margin);
		fdInclRownumField.right= new FormAttachment(100, 0);
		wInclRownumField.setLayoutData(fdInclRownumField);

		wlLimit=new Label(wContentComp, SWT.RIGHT);
		wlLimit.setText(Messages.getString("ExcelInputDialog.Limit.Label"));
 		props.setLook(wlLimit);
		fdlLimit=new FormData();
		fdlLimit.left = new FormAttachment(0, 0);
		fdlLimit.top  = new FormAttachment(wInclRownumField, margin);
		fdlLimit.right= new FormAttachment(middle, -margin);
		wlLimit.setLayoutData(fdlLimit);
		wLimit=new Text(wContentComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
 		props.setLook(wLimit);
		wLimit.addModifyListener(lsMod);
		fdLimit=new FormData();
		fdLimit.left = new FormAttachment(middle, 0);
		fdLimit.top  = new FormAttachment(wInclRownumField, margin);
		fdLimit.right= new FormAttachment(100, 0);
		wLimit.setLayoutData(fdLimit);
		
		fdContentComp = new FormData();
		fdContentComp.left  = new FormAttachment(0, 0);
		fdContentComp.top   = new FormAttachment(0, 0);
		fdContentComp.right = new FormAttachment(100, 0);
		fdContentComp.bottom= new FormAttachment(100, 0);
		wContentComp.setLayoutData(fdContentComp);

		wContentComp.layout();
		wContentTab.setControl(wContentComp);


		/////////////////////////////////////////////////////////////
		/// END OF CONTENT TAB
		/////////////////////////////////////////////////////////////

		/////////////////////////////////////////////////////////////
		/// START OF CONTENT TAB
		/////////////////////////////////////////////////////////////

		addErrorTab();

		// Fields tab...
		//
		wFieldsTab = new CTabItem(wTabFolder, SWT.NONE);
		wFieldsTab.setText(Messages.getString("ExcelInputDialog.FieldsTab.TabTitle"));
		
		FormLayout fieldsLayout = new FormLayout ();
		fieldsLayout.marginWidth  = Const.FORM_MARGIN;
		fieldsLayout.marginHeight = Const.FORM_MARGIN;
		
		wFieldsComp = new Composite(wTabFolder, SWT.NONE);
		wFieldsComp.setLayout(fieldsLayout);

		wbGetFields=new Button(wFieldsComp, SWT.PUSH| SWT.CENTER);
 		props.setLook(wbGetFields);
		wbGetFields.setText(Messages.getString("ExcelInputDialog.GetFields.Button"));
		
		setButtonPositions(new Button[] { wbGetFields }, margin, null);

		enableFields();

		final int FieldsRows=input.getField().length;
		int FieldsWidth =600;
		int FieldsHeight=150;
		
		// Prepare a list of possible formats...
		String dats[] = Const.dateFormats;
		String nums[] = Const.numberFormats;
		int totsize = dats.length + nums.length;
		String formats[] = new String[totsize];
		for (int x=0;x<dats.length;x++) formats[x] = dats[x];
		for (int x=0;x<nums.length;x++) formats[dats.length+x] = nums[x];
		
		
		ColumnInfo[] colinf=new ColumnInfo[] { 
		    new ColumnInfo(Messages.getString("ExcelInputDialog.Name.Column"),       ColumnInfo.COLUMN_TYPE_TEXT,    false),
			new ColumnInfo(Messages.getString("ExcelInputDialog.Type.Column"),       ColumnInfo.COLUMN_TYPE_CCOMBO,  Value.getTypes() ),
			new ColumnInfo(Messages.getString("ExcelInputDialog.Length.Column"),     ColumnInfo.COLUMN_TYPE_TEXT,    false),
			new ColumnInfo(Messages.getString("ExcelInputDialog.Precision.Column"),  ColumnInfo.COLUMN_TYPE_TEXT,    false),
			new ColumnInfo(Messages.getString("ExcelInputDialog.TrimType.Column"),   ColumnInfo.COLUMN_TYPE_CCOMBO,  TextFileInputMeta.trimTypeDesc ),
			new ColumnInfo(Messages.getString("ExcelInputDialog.Repeat.Column"),     ColumnInfo.COLUMN_TYPE_CCOMBO,  new String[] { Messages.getString("System.Combo.Yes"), Messages.getString("System.Combo.No") } ),
			new ColumnInfo(Messages.getString("ExcelInputDialog.Format.Column"),     ColumnInfo.COLUMN_TYPE_CCOMBO,  Const.getConversionFormats() ),
			new ColumnInfo(Messages.getString("ExcelInputDialog.Currency.Column"),   ColumnInfo.COLUMN_TYPE_TEXT),
			new ColumnInfo(Messages.getString("ExcelInputDialog.Decimal.Column"),    ColumnInfo.COLUMN_TYPE_TEXT),
			new ColumnInfo(Messages.getString("ExcelInputDialog.Grouping.Column"),   ColumnInfo.COLUMN_TYPE_TEXT)
		};
		
		colinf[ 5].setToolTip(Messages.getString("ExcelInputDialog.Repeat.Tooltip"));
		
		wFields=new TableView(wFieldsComp, 
						      SWT.FULL_SELECTION | SWT.MULTI, 
						      colinf, 
						      FieldsRows,  
						      lsMod,
							  props
						      );
		wFields.setSize(FieldsWidth,FieldsHeight);

		fdFields=new FormData();
		fdFields.left  = new FormAttachment(0, 0);
		fdFields.top   = new FormAttachment(0, 0);
		fdFields.right = new FormAttachment(100, 0);
		fdFields.bottom= new FormAttachment(wbGetFields, -margin);
		wFields.setLayoutData(fdFields);

		fdFieldsComp=new FormData();
		fdFieldsComp.left  = new FormAttachment(0, 0);
		fdFieldsComp.top   = new FormAttachment(0, 0);
		fdFieldsComp.right = new FormAttachment(100, 0);
		fdFieldsComp.bottom= new FormAttachment(100, 0);
		wFieldsComp.setLayoutData(fdFieldsComp);
		
		wFieldsComp.layout();
		wFieldsTab.setControl(wFieldsComp);
 		props.setLook(wFieldsComp);
		
		fdTabFolder = new FormData();
		fdTabFolder.left  = new FormAttachment(0, 0);
		fdTabFolder.top   = new FormAttachment(wStepname, margin);
		fdTabFolder.right = new FormAttachment(100, 0);
		fdTabFolder.bottom= new FormAttachment(100, -50);
		wTabFolder.setLayoutData(fdTabFolder);
		

		wOK=new Button(shell, SWT.PUSH);
		wOK.setText(Messages.getString("System.Button.OK"));
		wPreview=new Button(shell, SWT.PUSH);
		wPreview.setText(Messages.getString("ExcelInputDialog.PreviewRows.Button"));
		wCancel=new Button(shell, SWT.PUSH);
		wCancel.setText(Messages.getString("System.Button.Cancel"));
		
		setButtonPositions(new Button[] { wOK, wPreview, wCancel }, margin, wTabFolder);

		// Add listeners
		lsOK       = new Listener() { public void handleEvent(Event e) { ok();     } };
		lsPreview  = new Listener() { public void handleEvent(Event e) { preview();   } };
		lsCancel   = new Listener() { public void handleEvent(Event e) { cancel();     } };
		
		wOK.addListener     (SWT.Selection, lsOK     );
		wPreview.addListener(SWT.Selection, lsPreview);
		wCancel.addListener (SWT.Selection, lsCancel );
		
		lsDef=new SelectionAdapter() { public void widgetDefaultSelected(SelectionEvent e) { ok(); } };
		
		wStepname.addSelectionListener( lsDef );
		// wFilename.addSelectionListener( lsDef );
		wLimit.addSelectionListener( lsDef );
		wInclRownumField.addSelectionListener( lsDef );
		wInclFilenameField.addSelectionListener( lsDef );
		wInclSheetnameField.addSelectionListener( lsDef );

		// Add the file to the list of files...
		SelectionAdapter selA = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				wFilenameList.add(new String[] { wFilename.getText(), wFilemask.getText() } );
				wFilename.setText("");
				wFilemask.setText("");
				wFilenameList.removeEmptyRows();
				wFilenameList.setRowNums();
                wFilenameList.optWidth(true);
			}
		};
		wbaFilename.addSelectionListener(selA);
		wFilename.addSelectionListener(selA);
		
		// Delete files from the list of files...
		wbdFilename.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				int idx[] = wFilenameList.getSelectionIndices();
				wFilenameList.remove(idx);
				wFilenameList.removeEmptyRows();
				wFilenameList.setRowNums();
			}
		});

		// Edit the selected file & remove from the list...
		wbeFilename.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				int idx = wFilenameList.getSelectionIndex();
				if (idx>=0)
				{
					String string[] = wFilenameList.getItem(idx);
					wFilename.setText(string[0]);
					wFilemask.setText(string[1]);
					wFilenameList.remove(idx);
				}
				wFilenameList.removeEmptyRows();
				wFilenameList.setRowNums();
			}
		});

		// Show the files that are selected at this time...
		wbShowFiles.addSelectionListener(new SelectionAdapter() 
			{
				public void widgetSelected(SelectionEvent e) 
				{
					ExcelInputMeta eii = new ExcelInputMeta();
					getInfo(eii);
					String[] files = eii.getFilePaths();
					if (files.length > 0)
					{
						EnterSelectionDialog esd = new EnterSelectionDialog(shell, props, files, Messages.getString("ExcelInputDialog.FilesRead.DialogTitle"), Messages.getString("ExcelInputDialog.FilesRead.DialogMessage"));
						esd.setViewOnly();
						esd.open();
					}
					else
					{
						MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR );
						mb.setMessage(Messages.getString("ExcelInputDialog.NoFilesFound.DialogMessage"));
						mb.setText(Messages.getString("System.Dialog.Error.Title"));
						mb.open(); 
					}
				}
			}
		);

		// Whenever something changes, set the tooltip to the expanded version of the filename:
		wFilename.addModifyListener(new ModifyListener()
			{
				public void modifyText(ModifyEvent e)
				{
					wFilename.setToolTipText(Const.replEnv( wFilename.getText() ) );
				}
			}
		);
		
		// Listen to the Variable... button
		wbvFilename.addSelectionListener
		(
			new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e) 
				{
					Properties sp = System.getProperties();
					Enumeration keys = sp.keys();
					int size = sp.values().size();
					String key[] = new String[size];
					String val[] = new String[size];
					String str[] = new String[size];
					int i=0;
					while (keys.hasMoreElements())
					{
						key[i] = (String)keys.nextElement();
						val[i] = sp.getProperty(key[i]);
						str[i] = key[i]+"  ["+val[i]+"]";
						i++;
					}
					
					EnterSelectionDialog esd = new EnterSelectionDialog(shell, props, str, Messages.getString("System.Dialog.SelectEnvironmentVar.Title"), Messages.getString("System.Dialog.SelectEnvironmentVar.Message"));
					if (esd.open()!=null)
					{
						int nr = esd.getSelectionNr();
						wFilename.insert("%%"+key[nr]+"%%");
						wFilename.setToolTipText(Const.replEnv( wFilename.getText() ) );
					}
				}
				
			}
		);


		// Listen to the Browse... button
		wbbFilename.addSelectionListener
		(
			new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e) 
				{
					if (wFilemask.getText()!=null && wFilemask.getText().length()>0) // A mask: a directory!
					{
						DirectoryDialog dialog = new DirectoryDialog(shell, SWT.OPEN);
						if (wFilename.getText()!=null)
						{
							String fpath = Const.replEnv(wFilename.getText());
							dialog.setFilterPath( fpath );
						}
						
						if (dialog.open()!=null)
						{
							String str= dialog.getFilterPath();
							wFilename.setText(str);
						}
					}
					else
					{
						FileDialog dialog = new FileDialog(shell, SWT.OPEN);
						dialog.setFilterExtensions(new String[] {"*.xls;*.XLS", "*"});
						if (wFilename.getText()!=null)
						{
							String fname = Const.replEnv(wFilename.getText());
							dialog.setFileName( fname );
						}
						
						dialog.setFilterNames(new String[] {Messages.getString("ExcelInputDialog.FilterNames.ExcelFiles"), Messages.getString("System.FileType.AllFiles")});
						
						if (dialog.open()!=null)
						{
							String str = dialog.getFilterPath()+System.getProperty("file.separator")+dialog.getFileName();
							wFilename.setText(str);
						}
					}
				}
			}
		);
		
		// Get a list of the sheetnames.
		wbGetSheets.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				getSheets();
			}
		});
		
		wbGetFields.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent arg0)
			{
				getFields();
			}
		});
		
		// Detect X or ALT-F4 or something that kills this window...
		shell.addShellListener(	new ShellAdapter() { public void shellClosed(ShellEvent e) { cancel(); } } );

		wTabFolder.setSelection(0);
		
		// Set the shell size, based upon previous time...
		setSize();
		getData(input);
		input.setChanged(changed);
		wFields.optWidth(true);
		
		shell.open();
		while (!shell.isDisposed())
		{
				if (!display.readAndDispatch()) display.sleep();
		}
		return stepname;
	}
	
	public void enableFields()
	{
		wbGetFields.setEnabled( wHeader.getSelection());
	}
	
	/**
	 * Read the data from the ExcelInputMeta object and show it in this dialog.
	 * 
	 * @param in The ExcelInputMeta object to obtain the data from.
	 */
	public void getData(ExcelInputMeta in)
	{
		if (in.getFileName() !=null) 
		{
			wFilenameList.removeAll();
			for (int i=0;i<in.getFileName().length;i++) 
			{
				wFilenameList.add(new String[] { in.getFileName()[i], in.getFileMask()[i] , in.getFileRequired()[i]} );
			}
			wFilenameList.removeEmptyRows();
			wFilenameList.setRowNums();
			wFilenameList.optWidth(true);
		}
		wHeader.setSelection(in.startsWithHeader());
		wNoempty.setSelection(in.ignoreEmptyRows());
		wStoponempty.setSelection(in.stopOnEmpty());
		if (in.getFileField()!=null) wInclFilenameField.setText(in.getFileField());
		if (in.getSheetField()!=null) wInclSheetnameField.setText(in.getSheetField());
		if (in.getRowNumberField()!=null) wInclRownumField.setText(in.getRowNumberField());
		wLimit.setText(""+in.getRowLimit());
		
		log.logDebug(toString(), "getting fields info...");
		for (int i=0;i<in.getField().length;i++)
		{
			TableItem item = wFields.table.getItem(i);
			String field    = in.getField()[i].getName();
			String type     = in.getField()[i].getTypeDesc();
			String length   = ""+in.getField()[i].getLength();
			String prec     = ""+in.getField()[i].getPrecision();
			String trim     = in.getField()[i].getTrimTypeDesc();
			String rep      = in.getField()[i].isRepeated()?Messages.getString("System.Combo.Yes"):Messages.getString("System.Combo.No");
			String format   = in.getField()[i].getFormat();
			String currency = in.getField()[i].getCurrencySymbol();
			String decimal  = in.getField()[i].getDecimalSymbol();
			String grouping = in.getField()[i].getGroupSymbol();
			
			if (field   !=null) item.setText( 1, field);
			if (type    !=null) item.setText( 2, type    );
			if (length  !=null) item.setText( 3, length  );
			if (prec    !=null) item.setText( 4, prec    );
			if (trim    !=null) item.setText( 5, trim    );
			if (rep     !=null) item.setText( 6, rep     );
			if (format  !=null) item.setText( 7, format  );
			if (currency!=null) item.setText( 8, currency);
			if (decimal !=null) item.setText( 9, decimal );
			if (grouping!=null) item.setText(10, grouping);
		}
		
		wFields.removeEmptyRows();
		wFields.setRowNums();
		wFields.optWidth(true);

		log.logDebug(toString(), "getting sheets info...");
		for (int i=0;i<in.getSheetName().length;i++)
		{
			TableItem item = wSheetnameList.table.getItem(i);
			String sheetname    =    in.getSheetName()[i];
			String startrow     = ""+in.getStartRow()[i];
			String startcol     = ""+in.getStartColumn()[i];
			
			if (sheetname!=null) item.setText( 1, sheetname);
			if (startrow!=null)  item.setText( 2, startrow);
			if (startcol!=null)  item.setText( 3, startcol);
		}
		wSheetnameList.removeEmptyRows();
		wSheetnameList.setRowNums();
		wSheetnameList.optWidth(true);
		
		//		 Error handling fields...
        wErrorIgnored.setSelection( in.isErrorIgnored() );
        wStrictTypes.setSelection( in.isStrictTypes() );
        wSkipErrorLines.setSelection( in.isErrorLineSkipped() );

        if (in.getWarningFilesDestinationDirectory()!=null) wWarningDestDir.setText(in.getWarningFilesDestinationDirectory());
        if (in.getBadLineFilesExtension()!=null) wWarningExt.setText(in.getBadLineFilesExtension());

        if (in.getErrorFilesDestinationDirectory()!=null) wErrorDestDir.setText(in.getErrorFilesDestinationDirectory());
        if (in.getErrorFilesExtension()!=null) wErrorExt.setText(in.getErrorFilesExtension());

        if (in.getLineNumberFilesDestinationDirectory()!=null) wLineNrDestDir.setText(in.getLineNumberFilesDestinationDirectory());
        if (in.getLineNumberFilesExtension()!=null) wLineNrExt.setText(in.getLineNumberFilesExtension());

		enableFields();
		
		wStepname.selectAll();
	}
	
	private void cancel()
	{
		stepname=null;
		input.setChanged(changed);
		dispose();
	}
	
	private void ok()
	{
		getInfo(input);
		dispose();
	}
	
	private void getInfo(ExcelInputMeta in)
	{
		stepname = wStepname.getText(); // return value

		// copy info to Meta class (input)
		in.setRowLimit( Const.toLong(wLimit.getText(), 0) );
		in.setFileField( wInclFilenameField.getText() );
		in.setSheetField( wInclSheetnameField.getText() );
		in.setRowNumberField( wInclRownumField.getText() );
		
		in.setStartsWithHeader( wHeader.getSelection() );
		in.setIgnoreEmptyRows( wNoempty.getSelection() );
		in.setStopOnEmpty( wStoponempty.getSelection() );

		int nrfiles    = wFilenameList.nrNonEmpty();
		int nrsheets   = wSheetnameList.nrNonEmpty();
		int nrfields   = wFields.nrNonEmpty();
		
		in.allocate(nrfiles, nrsheets, nrfields);

		for (int i=0;i<nrfiles;i++)
		{
			TableItem item = wFilenameList.getNonEmpty(i);
			in.getFileName()[i] = item.getText(1);
			in.getFileMask()[i] = item.getText(2);
			in.getFileRequired()[i] = item.getText(3);
		}

		for (int i=0;i<nrsheets;i++)
		{
			TableItem item = wSheetnameList.getNonEmpty(i);
			in.getSheetName()[i] = item.getText(1);
			in.getStartRow()[i]  = Const.toInt(item.getText(2),0);
			in.getStartColumn()[i]  = Const.toInt(item.getText(3),0);
		}

		for (int i=0;i<nrfields;i++)
		{
			TableItem item  = wFields.getNonEmpty(i);
			in.getField()[i] = new ExcelInputField();
			
			in.getField()[i].setName( item.getText(1) );
			in.getField()[i].setType( Value.getType(item.getText(2)) );
			String slength  = item.getText(3);
			String sprec    = item.getText(4);
			in.getField()[i].setTrimType( ExcelInputMeta.getTrimTypeByDesc(item.getText(5)) );
			in.getField()[i].setRepeated( Messages.getString("System.Combo.Yes").equalsIgnoreCase(item.getText(6)) );		

			in.getField()[i].setLength( Const.toInt(slength, -1) );
			in.getField()[i].setPrecision( Const.toInt(sprec, -1) );
			
			in.getField()[i].setFormat( item.getText(7) );
			in.getField()[i].setCurrencySymbol( item.getText(8) );
			in.getField()[i].setDecimalSymbol( item.getText(9) );
			in.getField()[i].setGroupSymbol( item.getText(10) );
		}	
		
		// Error handling fields...
		in.setStrictTypes( wStrictTypes.getSelection() );
        in.setErrorIgnored( wErrorIgnored.getSelection() );
        in.setErrorLineSkipped( wSkipErrorLines.getSelection() );
        
        in.setWarningFilesDestinationDirectory( wWarningDestDir.getText() );
        in.setBadLineFilesExtension( wWarningExt.getText() );
        in.setErrorFilesDestinationDirectory( wErrorDestDir.getText() );
        in.setErrorFilesExtension( wErrorExt.getText() );
        in.setLineNumberFilesDestinationDirectory( wLineNrDestDir.getText() );
        in.setLineNumberFilesExtension( wLineNrExt.getText() );
		
	}
	
	
	public ModifyListener getModifyListenerTooltipText(final Text textField)
    {
        return new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                textField.setToolTipText(Const.replEnv( textField.getText() ) );
            }
        };
    }
	
    private void addErrorTab()
    {
        //////////////////////////
        // START OF ERROR TAB  ///
        ///
        wErrorTab=new CTabItem(wTabFolder, SWT.NONE);
        wErrorTab.setText(Messages.getString("ExcelInputDialog.ErrorTab.TabTitle"));

        FormLayout errorLayout = new FormLayout ();
        errorLayout.marginWidth  = 3;
        errorLayout.marginHeight = 3;
        
        wErrorComp = new Composite(wTabFolder, SWT.NONE);
        props.setLook(wErrorComp);
        wErrorComp.setLayout(errorLayout);
        
        // ERROR HANDLING...
        // ErrorIgnored?
        wlStrictTypes = new Label(wErrorComp, SWT.RIGHT);
        wlStrictTypes.setText(Messages.getString("ExcelInputDialog.StrictTypes.Label"));
        props.setLook(wlStrictTypes);
        fdlStrictTypes = new FormData();
        fdlStrictTypes.left = new FormAttachment(0, 0);
        fdlStrictTypes.top = new FormAttachment(0, margin);
        fdlStrictTypes.right = new FormAttachment(middle, -margin);
        wlStrictTypes.setLayoutData(fdlStrictTypes);
        wStrictTypes = new Button(wErrorComp, SWT.CHECK);
        props.setLook(wStrictTypes);
        wStrictTypes.setToolTipText(Messages.getString("ExcelInputDialog.StrictTypes.Tooltip"));
        fdStrictTypes = new FormData();
        fdStrictTypes.left = new FormAttachment(middle, 0);
        fdStrictTypes.top = new FormAttachment(0, margin);
        wStrictTypes.setLayoutData(fdStrictTypes);
        Control previous = wStrictTypes;

        // ErrorIgnored?
        wlErrorIgnored = new Label(wErrorComp, SWT.RIGHT);
        wlErrorIgnored.setText(Messages.getString("ExcelInputDialog.ErrorIgnored.Label"));
        props.setLook(wlErrorIgnored);
        fdlErrorIgnored = new FormData();
        fdlErrorIgnored.left = new FormAttachment(0, 0);
        fdlErrorIgnored.top = new FormAttachment(previous, margin);
        fdlErrorIgnored.right = new FormAttachment(middle, -margin);
        wlErrorIgnored.setLayoutData(fdlErrorIgnored);
        wErrorIgnored = new Button(wErrorComp, SWT.CHECK);
        props.setLook(wErrorIgnored);
        wErrorIgnored.setToolTipText(Messages.getString("ExcelInputDialog.ErrorIgnored.Tooltip"));
        fdErrorIgnored = new FormData();
        fdErrorIgnored.left = new FormAttachment(middle, 0);
        fdErrorIgnored.top = new FormAttachment(previous, margin);
        wErrorIgnored.setLayoutData(fdErrorIgnored);
        previous = wErrorIgnored;

        // Skip error lines?
        wlSkipErrorLines = new Label(wErrorComp, SWT.RIGHT);
        wlSkipErrorLines.setText(Messages.getString("ExcelInputDialog.SkipErrorLines.Label"));
        props.setLook(wlSkipErrorLines);
        fdlSkipErrorLines = new FormData();
        fdlSkipErrorLines.left = new FormAttachment(0, 0);
        fdlSkipErrorLines.top = new FormAttachment(previous, margin);
        fdlSkipErrorLines.right = new FormAttachment(middle, -margin);
        wlSkipErrorLines.setLayoutData(fdlSkipErrorLines);
        wSkipErrorLines = new Button(wErrorComp, SWT.CHECK);
        props.setLook(wSkipErrorLines);
        wSkipErrorLines.setToolTipText(Messages.getString("ExcelInputDialog.SkipErrorLines.Tooltip"));
        fdSkipErrorLines = new FormData();
        fdSkipErrorLines.left = new FormAttachment(middle, 0);
        fdSkipErrorLines.top = new FormAttachment(previous, margin);
        wSkipErrorLines.setLayoutData(fdSkipErrorLines);
        
        previous = wSkipErrorLines;
        
        
        
        // Bad lines files directory + extention
        
        // WarningDestDir line
        wlWarningDestDir=new Label(wErrorComp, SWT.RIGHT);
        wlWarningDestDir.setText(Messages.getString("ExcelInputDialog.WarningDestDir.Label"));
        props.setLook(wlWarningDestDir);
        fdlWarningDestDir=new FormData();
        fdlWarningDestDir.left = new FormAttachment(0, 0);
        fdlWarningDestDir.top  = new FormAttachment(previous, margin*4);
        fdlWarningDestDir.right= new FormAttachment(middle, -margin);
        wlWarningDestDir.setLayoutData(fdlWarningDestDir);

        wbbWarningDestDir=new Button(wErrorComp, SWT.PUSH| SWT.CENTER);
        props.setLook(wbbWarningDestDir);
        wbbWarningDestDir.setText(Messages.getString("System.Button.Browse"));
        wbbWarningDestDir.setToolTipText(Messages.getString("System.Tooltip.BrowseForDir"));
        fdbWarningDestDir=new FormData();
        fdbWarningDestDir.right= new FormAttachment(100, 0);
        fdbWarningDestDir.top  = new FormAttachment(previous, margin*4);
        wbbWarningDestDir.setLayoutData(fdbWarningDestDir);

        wbvWarningDestDir=new Button(wErrorComp, SWT.PUSH| SWT.CENTER);
        props.setLook(wbvWarningDestDir);
        wbvWarningDestDir.setText(Messages.getString("System.Button.Variable"));
        wbvWarningDestDir.setToolTipText(Messages.getString("System.Tooltip.VariableToDir"));
        fdbvWarningDestDir=new FormData();
        fdbvWarningDestDir.right= new FormAttachment(wbbWarningDestDir, -margin);
        fdbvWarningDestDir.top  = new FormAttachment(previous, margin*4);
        wbvWarningDestDir.setLayoutData(fdbvWarningDestDir);

        wWarningExt=new Text(wErrorComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        props.setLook(wWarningExt);
        wWarningExt.addModifyListener(lsMod);
        fdWarningDestExt=new FormData();
        fdWarningDestExt.left = new FormAttachment(wbvWarningDestDir, -150);
        fdWarningDestExt.right= new FormAttachment(wbvWarningDestDir, -margin);
        fdWarningDestExt.top  = new FormAttachment(previous, margin*4);
        wWarningExt.setLayoutData(fdWarningDestExt);

        wlWarningExt=new Label(wErrorComp, SWT.RIGHT);
        wlWarningExt.setText(Messages.getString("System.Label.Extension"));
        props.setLook(wlWarningExt);
        fdlWarningDestExt=new FormData();
        fdlWarningDestExt.top  = new FormAttachment(previous, margin*4);
        fdlWarningDestExt.right= new FormAttachment(wWarningExt, -margin);
        wlWarningExt.setLayoutData(fdlWarningDestExt);

        wWarningDestDir=new Text(wErrorComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        props.setLook(wWarningDestDir);
        wWarningDestDir.addModifyListener(lsMod);
        fdWarningDestDir=new FormData();
        fdWarningDestDir.left = new FormAttachment(middle, 0);
        fdWarningDestDir.right= new FormAttachment(wlWarningExt, -margin);
        fdWarningDestDir.top  = new FormAttachment(previous, margin*4);
        wWarningDestDir.setLayoutData(fdWarningDestDir);
        
        // Listen to the Browse... button
        wbbWarningDestDir.addSelectionListener(DirectoryDialogButtonListenerFactory.getSelectionAdapter(shell, wWarningDestDir));

        // Listen to the Variable... button
        wbvWarningDestDir.addSelectionListener(VariableButtonListenerFactory.getSelectionAdapter(shell, wWarningDestDir));        
        
        // Whenever something changes, set the tooltip to the expanded version of the directory:
        wWarningDestDir.addModifyListener(getModifyListenerTooltipText(wWarningDestDir));
        
        


        
        // Error lines files directory + extention
        previous = wWarningDestDir;
        
        // ErrorDestDir line
        wlErrorDestDir=new Label(wErrorComp, SWT.RIGHT);
        wlErrorDestDir.setText(Messages.getString("ExcelInputDialog.ErrorDestDir.Label"));
        props.setLook(wlErrorDestDir);
        fdlErrorDestDir=new FormData();
        fdlErrorDestDir.left = new FormAttachment(0, 0);
        fdlErrorDestDir.top  = new FormAttachment(previous, margin);
        fdlErrorDestDir.right= new FormAttachment(middle, -margin);
        wlErrorDestDir.setLayoutData(fdlErrorDestDir);

        wbbErrorDestDir=new Button(wErrorComp, SWT.PUSH| SWT.CENTER);
        props.setLook(wbbErrorDestDir);
        wbbErrorDestDir.setText(Messages.getString("System.Button.Browse"));
        wbbErrorDestDir.setToolTipText(Messages.getString("System.Tooltip.BrowseForDir"));
        fdbErrorDestDir=new FormData();
        fdbErrorDestDir.right= new FormAttachment(100, 0);
        fdbErrorDestDir.top  = new FormAttachment(previous, margin);
        wbbErrorDestDir.setLayoutData(fdbErrorDestDir);

        wbvErrorDestDir=new Button(wErrorComp, SWT.PUSH| SWT.CENTER);
        props.setLook(wbvErrorDestDir);
        wbvErrorDestDir.setText(Messages.getString("System.Button.Variable"));
        wbvErrorDestDir.setToolTipText(Messages.getString("System.Tooltip.VariableToDir"));
        fdbvErrorDestDir=new FormData();
        fdbvErrorDestDir.right= new FormAttachment(wbbErrorDestDir, -margin);
        fdbvErrorDestDir.top  = new FormAttachment(previous, margin);
        wbvErrorDestDir.setLayoutData(fdbvErrorDestDir);

        wErrorExt=new Text(wErrorComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        props.setLook(wErrorExt);
        wErrorExt.addModifyListener(lsMod);
        fdErrorDestExt=new FormData();
        fdErrorDestExt.left = new FormAttachment(wbvErrorDestDir, -150);
        fdErrorDestExt.right= new FormAttachment(wbvErrorDestDir, -margin);
        fdErrorDestExt.top  = new FormAttachment(previous, margin);
        wErrorExt.setLayoutData(fdErrorDestExt);

        wlErrorExt=new Label(wErrorComp, SWT.RIGHT);
        wlErrorExt.setText(Messages.getString("System.Label.Extension"));
        props.setLook(wlErrorExt);
        fdlErrorDestExt=new FormData();
        fdlErrorDestExt.top  = new FormAttachment(previous, margin);
        fdlErrorDestExt.right= new FormAttachment(wErrorExt, -margin);
        wlErrorExt.setLayoutData(fdlErrorDestExt);

        wErrorDestDir=new Text(wErrorComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        props.setLook(wErrorDestDir);
        wErrorDestDir.addModifyListener(lsMod);
        fdErrorDestDir=new FormData();
        fdErrorDestDir.left = new FormAttachment(middle, 0);
        fdErrorDestDir.right= new FormAttachment(wlErrorExt, -margin);
        fdErrorDestDir.top  = new FormAttachment(previous, margin);
        wErrorDestDir.setLayoutData(fdErrorDestDir);
        
        // Listen to the Browse... button
        wbbErrorDestDir.addSelectionListener(DirectoryDialogButtonListenerFactory.getSelectionAdapter(shell, wErrorDestDir));

        // Listen to the Variable... button
        wbvErrorDestDir.addSelectionListener(VariableButtonListenerFactory.getSelectionAdapter(shell, wErrorDestDir));        
        
        // Whenever something changes, set the tooltip to the expanded version of the directory:
        wErrorDestDir.addModifyListener(getModifyListenerTooltipText(wErrorDestDir));

        // Line numbers files directory + extention
        previous = wErrorDestDir;
        
        // LineNrDestDir line
        wlLineNrDestDir=new Label(wErrorComp, SWT.RIGHT);
        wlLineNrDestDir.setText(Messages.getString("ExcelInputDialog.LineNrDestDir.Label"));
        props.setLook(wlLineNrDestDir);
        fdlLineNrDestDir=new FormData();
        fdlLineNrDestDir.left = new FormAttachment(0, 0);
        fdlLineNrDestDir.top  = new FormAttachment(previous, margin);
        fdlLineNrDestDir.right= new FormAttachment(middle, -margin);
        wlLineNrDestDir.setLayoutData(fdlLineNrDestDir);

        wbbLineNrDestDir=new Button(wErrorComp, SWT.PUSH| SWT.CENTER);
        props.setLook(wbbLineNrDestDir);
        wbbLineNrDestDir.setText(Messages.getString("System.Button.Browse"));
        wbbLineNrDestDir.setToolTipText(Messages.getString("System.Tooltip.BrowseForDir"));
        fdbLineNrDestDir=new FormData();
        fdbLineNrDestDir.right= new FormAttachment(100, 0);
        fdbLineNrDestDir.top  = new FormAttachment(previous, margin);
        wbbLineNrDestDir.setLayoutData(fdbLineNrDestDir);

        wbvLineNrDestDir=new Button(wErrorComp, SWT.PUSH| SWT.CENTER);
        props.setLook(wbvLineNrDestDir);
        wbvLineNrDestDir.setText(Messages.getString("System.Button.Variable"));
        wbvLineNrDestDir.setToolTipText(Messages.getString("System.Tooltip.VariableToDir"));
        fdbvLineNrDestDir=new FormData();
        fdbvLineNrDestDir.right= new FormAttachment(wbbLineNrDestDir, -margin);
        fdbvLineNrDestDir.top  = new FormAttachment(previous, margin);
        wbvLineNrDestDir.setLayoutData(fdbvLineNrDestDir);

        wLineNrExt=new Text(wErrorComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        props.setLook(wLineNrExt);
        wLineNrExt.addModifyListener(lsMod);
        fdLineNrDestExt=new FormData();
        fdLineNrDestExt.left = new FormAttachment(wbvLineNrDestDir, -150);
        fdLineNrDestExt.right= new FormAttachment(wbvLineNrDestDir, -margin);
        fdLineNrDestExt.top  = new FormAttachment(previous, margin);
        wLineNrExt.setLayoutData(fdLineNrDestExt);

        wlLineNrExt=new Label(wErrorComp, SWT.RIGHT);
        wlLineNrExt.setText(Messages.getString("System.Label.Extension"));
        props.setLook(wlLineNrExt);
        fdlLineNrDestExt=new FormData();
        fdlLineNrDestExt.top  = new FormAttachment(previous, margin);
        fdlLineNrDestExt.right= new FormAttachment(wLineNrExt, -margin);
        wlLineNrExt.setLayoutData(fdlLineNrDestExt);

        wLineNrDestDir=new Text(wErrorComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        props.setLook(wLineNrDestDir);
        wLineNrDestDir.addModifyListener(lsMod);
        fdLineNrDestDir=new FormData();
        fdLineNrDestDir.left = new FormAttachment(middle, 0);
        fdLineNrDestDir.right= new FormAttachment(wlLineNrExt, -margin);
        fdLineNrDestDir.top  = new FormAttachment(previous, margin);
        wLineNrDestDir.setLayoutData(fdLineNrDestDir);
        
        // Listen to the Browse... button
        wbbLineNrDestDir.addSelectionListener(DirectoryDialogButtonListenerFactory.getSelectionAdapter(shell, wLineNrDestDir));

        // Listen to the Variable... button
        wbvLineNrDestDir.addSelectionListener(VariableButtonListenerFactory.getSelectionAdapter(shell, wLineNrDestDir));        
        
        // Whenever something changes, set the tooltip to the expanded version of the directory:
        wLineNrDestDir.addModifyListener(getModifyListenerTooltipText(wLineNrDestDir));

        wErrorComp.layout();
        wErrorTab.setControl(wErrorComp);


        /////////////////////////////////////////////////////////////
        /// END OF CONTENT TAB
        /////////////////////////////////////////////////////////////

    }

	
	/**
	 * Preview the data generated by this step.
	 * This generates a transformation using this step & a dummy and previews it.
	 *
	 */
	private void preview()
	{
		// Create the excel reader step...
		ExcelInputMeta eii = new ExcelInputMeta();
		getInfo(eii);
        
        TransMeta previewMeta = TransPreviewFactory.generatePreviewTransformation(eii, wStepname.getText());
        
        EnterNumberDialog numberDialog = new EnterNumberDialog(shell, props, 500, Messages.getString("ExcelInputDialog.PreviewSize.DialogTitle"), Messages.getString("ExcelInputDialog.PreviewSize.DialogMessage"));
        int previewSize = numberDialog.open();
        if (previewSize>0)
        {
            TransPreviewProgressDialog progressDialog = new TransPreviewProgressDialog(shell, previewMeta, new String[] { wStepname.getText() }, new int[] { previewSize } );
            progressDialog.open();
            
            if (!progressDialog.isCancelled())
            {
                Trans trans = progressDialog.getTrans();
                String loggingText = progressDialog.getLoggingText();
                
                if (trans.getResult()!=null && trans.getResult().getNrErrors()>0)
                {
                    MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR );
                    mb.setMessage(Messages.getString("ExcelInputDialog.PreviewErrors.DialogMessage"));
                    mb.setText(Messages.getString("System.Dialog.Error.Title"));
                    mb.open(); 
                }
                
                PreviewRowsDialog prd =new PreviewRowsDialog(shell, SWT.NONE, wStepname.getText(), progressDialog.getPreviewRows(wStepname.getText()), loggingText);
                prd.open();
            }
        }
	}
		
	
	/**
	 * Get the names of the sheets from the Excel workbooks and let the user select some or all of them.
	 *
	 */
	public void getSheets()
	{
		ArrayList sheetnames = new ArrayList();
		
		ExcelInputMeta info = new ExcelInputMeta();
		getInfo(info);

		FileInputList fileList = info.getFileList();
		for (Iterator iter = fileList.getFiles().iterator(); iter.hasNext();) {
			File file = (File) iter.next();
			try
			{
				Workbook workbook = Workbook.getWorkbook(file);
				
				int nrSheets = workbook.getNumberOfSheets();
				for (int j=0;j<nrSheets;j++)
				{
					Sheet sheet = workbook.getSheet(j);
					String sheetname = sheet.getName();
					
					if (Const.indexOfString(sheetname, sheetnames)<0) sheetnames.add(sheetname);
				}
				
				workbook.close();
			}
			catch(Exception e)
			{
				MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR );
				mb.setMessage(Messages.getString("ExcelInputDialog.ErrorReadingFile.DialogMessage", file.getPath()));
				mb.setText(Messages.getString("System.Dialog.Error.Title"));
				mb.open(); 
			}
		}
		
		// Put it in an array:
		String lst[] = (String[])sheetnames.toArray(new String[sheetnames.size()]);
		
		// Let the user select the sheet-names...
		EnterListDialog esd = new EnterListDialog(shell, SWT.NONE, lst);
		String selection[] = esd.open();
		if (selection!=null)
		{
			for (int j=0;j<selection.length;j++)
			{
				wSheetnameList.add(new String[] { selection[j], "" } );
			}
			wSheetnameList.removeEmptyRows();
			wSheetnameList.setRowNums();
			wSheetnameList.optWidth(true);
		}


	}

	/**
	 * Get the list of fields in the Excel workbook and put the result in the fields table view.
	 *
	 */
	public void getFields()
	{
		Row fields = new Row();
		
		ExcelInputMeta info = new ExcelInputMeta();
		getInfo(info);

		FileInputList fileList = info.getFileList();
		for (Iterator iter = fileList.getFiles().iterator(); iter.hasNext();) {
			File file = (File) iter.next();
			try
			{
				Workbook workbook = Workbook.getWorkbook(file);
				
				int nrSheets = workbook.getNumberOfSheets();
				for (int j=0;j<nrSheets;j++)
				{
					Sheet sheet = workbook.getSheet(j);
					
					// See if it's a selected sheet:
					int sheetIndex = Const.indexOfString(sheet.getName(), info.getSheetName()); 
					if (sheetIndex>=0)
					{
						// We suppose it's the complete range we're looking for...
						int rownr=info.getStartRow()[sheetIndex];
						int startcol = info.getStartColumn()[sheetIndex];
						
						boolean stop=false;
						for (int colnr=startcol;colnr<256 && !stop;colnr++)
						{
							// System.out.println("Checking out (colnr, rownr) : ("+colnr+", "+rownr+")");
							
							try
							{
								String fieldname = null;
								int    fieldtype = Value.VALUE_TYPE_NONE;
		
								Cell cell = sheet.getCell(colnr, rownr);
								if (!cell.getType().equals( CellType.EMPTY ))
								{
									// We found a field.
									fieldname = cell.getContents();
								}
								
                                // System.out.println("Fieldname = "+fieldname);
                                
								Cell below = sheet.getCell(colnr, rownr+1);
								if (below.getType().equals(CellType.BOOLEAN))
								{
									fieldtype = Value.VALUE_TYPE_BOOLEAN;
								}
								else
								if (below.getType().equals(CellType.DATE))
								{
									fieldtype = Value.VALUE_TYPE_DATE;
								}
								else
								if (below.getType().equals(CellType.LABEL))
								{
									fieldtype = Value.VALUE_TYPE_STRING;
								}
								else
								if (below.getType().equals(CellType.NUMBER))
								{
									fieldtype = Value.VALUE_TYPE_NUMBER;
								}
                                
                                if (fieldname!=null && fieldtype==Value.VALUE_TYPE_NONE)
                                {
                                    fieldtype = Value.VALUE_TYPE_STRING;
                                }
								
								if (fieldname!=null && fieldtype!=Value.VALUE_TYPE_NONE)
								{
									Value field = new Value(fieldname, fieldtype);
									if (fields.searchValueIndex(field.getName())<0) fields.addValue(field);
								}
								else
								{
									if (fieldname==null) stop=true;
								}
							}
							catch(ArrayIndexOutOfBoundsException aioobe)
							{
                                // System.out.println("index out of bounds at column "+colnr+" : "+aioobe.toString());
								stop=true;
							}
						}
					}
				}
				
				workbook.close();
			}
			catch(Exception e)
			{
				MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR );
				mb.setMessage(Messages.getString("ExcelInputDialog.ErrorReadingFile2.DialogMessage", file.getPath(), e.toString()));
				mb.setText(Messages.getString("System.Dialog.Error.Title"));
				mb.setText("ERROR");
				mb.open(); 
			}
		}
		
		if (fields.size()>0)
		{
			for (int j=0;j<fields.size();j++)
			{
				Value field = fields.getValue(j);
				wFields.add(new String[] { field.getName(), field.getTypeDesc(), "-1", "-1", "none", "N" } );
			}
	
			wFields.removeEmptyRows();
			wFields.setRowNums();
			wFields.optWidth(true);
		}
		else
		{
			MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_WARNING);
			mb.setMessage(Messages.getString("ExcelInputDialog.UnableToFindFields.DialogMessage"));
			mb.setText(Messages.getString("ExcelInputDialog.UnableToFindFields.DialogTitle"));
			mb.open(); 
		}
	}

	public String toString()
	{
		return this.getClass().getName();
	}

}
