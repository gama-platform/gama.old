package idees.gama.ui.editFrame;

import gama.EGamaObject;
import gama.EWorldAgent;
import idees.gama.features.edit.EditFeature;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public abstract class EditFrame extends ApplicationWindow {
	Diagram diagram;
	EditFeature ef;
	IFeatureProvider fp;
	String name;
	EGamaObject eobject;
	StyledText validationResult;
	Text textName;
	EditFrame frame;
	Shell shell;
	
	/**
	 * Create the application window.
	 */
	public EditFrame(Diagram diagram, IFeatureProvider fp, EditFeature ef,EGamaObject eobject, String name) {
		super(null);
		this.diagram = diagram;
		frame = this;
		this.fp = fp;
		this.ef = ef; 
		this.name = name;		
		this.eobject = eobject;
		addToolBar(SWT.FLAT | SWT.WRAP);
		addMenuBar();
		addStatusLine();
		
	}

	
	protected Canvas canvasValidation(Composite container) {
		Canvas canvasValidation = new Canvas(container, SWT.BORDER);
		validationResult = new StyledText(canvasValidation, SWT.BORDER);
		UtilEditFrame.buildCanvasValidation(container, canvasValidation, validationResult, fp, diagram, eobject);
		canvasValidation.setBounds(10, 580, 720, 95);
		return canvasValidation;
	}
	
	protected void groupName(Composite container) {
		Group group = new Group(container, SWT.NONE);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		group.setLayoutData(gridData);
		
		group.setLayout(new GridLayout(2, false));
	   
	    CLabel lblName = new CLabel(group, SWT.NONE);
		lblName.setText("Name:");
		
	    textName = new Text(group, SWT.BORDER);
	    GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = SWT.FILL;
		gridData2.grabExcessHorizontalSpace = true;
		textName.setLayoutData(gridData2);
		textName.setText(eobject.getName());
		if (eobject instanceof EWorldAgent) {
			textName.setEditable(false);
		}
		
	}
	
	protected Canvas canvasName(Composite container) {	
		Canvas canvasName = new Canvas(container, SWT.BORDER);
		textName = new Text(canvasName, SWT.BORDER);
		UtilEditFrame.buildCanvasName(container, canvasName, textName, eobject, ef);
		canvasName.setBounds(10, 10, 720, 30);
		return canvasName;
	}

	/**
	 * Create the menu manager.
	 * @return the menu manager
	 */
	@Override
	protected MenuManager createMenuManager() {
		MenuManager menuManager = new MenuManager("menu");
		return menuManager;
	}

	/**
	 * Create the toolbar manager.
	 * @return the toolbar manager
	 */
	@Override
	protected ToolBarManager createToolBarManager(int style) {
		ToolBarManager toolBarManager = new ToolBarManager(style);
		return toolBarManager;
	}

	/**
	 * Create the status line manager.
	 * @return the status line manager
	 */
	@Override
	protected StatusLineManager createStatusLineManager() {
		StatusLineManager statusLineManager = new StatusLineManager();
		return statusLineManager;
	}


	/**
	 * Configure the shell.
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		
		newShell.setText(name);
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(743, 727);
	}
	
	protected Canvas canvasOkCancel(Composite container) {
		//****** CANVAS OK CANCEL BUTTONS *********
		Canvas canvasOKCancel = new Canvas(container, SWT.BORDER);
		canvasOKCancel.setBounds(10, 460, 720, 30);	

		final Button buttonOK = new Button(canvasOKCancel, SWT.PUSH);
		buttonOK.setText("Ok");
		buttonOK.setBounds(150, 5, 80, 20);
		buttonOK.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				frame.save();
				frame.close();
			} 
		});

		Button buttonCancel = new Button(canvasOKCancel, SWT.PUSH);
		buttonCancel.setText("Cancel");
		buttonCancel.setBounds(350, 5, 80, 20);
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				frame.clean();
				frame.close();
			}
		});
		return canvasOKCancel;
	}
	
	protected Group groupOkCancel(Composite container) {
		//****** CANVAS OK CANCEL BUTTONS *********
		
		Group groupOkCancel = new Group(container, SWT.NONE);
		groupOkCancel.setText("Validation");
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		groupOkCancel.setLayoutData(gridData);
		
		groupOkCancel.setLayout(new GridLayout(2, true));
	   
		final Button buttonOK = new Button(groupOkCancel, SWT.PUSH);
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.CENTER;
		buttonOK.setLayoutData(gridData2);
		buttonOK.setText("Ok");
		buttonOK.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				frame.save();
				frame.close();
			} 
		});

		Button buttonCancel = new Button(groupOkCancel, SWT.PUSH);
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = GridData.CENTER;
		buttonOK.setLayoutData(gridData3);
		buttonCancel.setText("Cancel");
		buttonCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				frame.clean();
				frame.close();
			}
		});
		return groupOkCancel;
	}
	protected abstract void save();
	
	protected void handleShellCloseEvent() {
		// create dialog with ok and cancel button and info icon
		MessageBox dialog = 
		  new MessageBox(shell, SWT.ICON_WARNING | SWT.OK| SWT.CANCEL);
		dialog.setText("Warning");
		dialog.setMessage("You have unsaved data. Close the '"+ name +"' windows anyway?");

		int result = dialog.open();
		if (result == SWT.OK) {
			frame.clean();
			frame.close();
		}
	}
	
	

	@Override
	public void create() {
	    setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE );
	    super.create();
	    shell = getShell();
	    
	   
	}
	
	protected void clean() {
	}
	
}
