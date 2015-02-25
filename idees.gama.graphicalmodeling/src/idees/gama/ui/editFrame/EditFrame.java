package idees.gama.ui.editFrame;

import gama.*;
import idees.gama.diagram.GamaDiagramEditor;
import idees.gama.features.edit.EditFeature;
import java.util.ArrayList;
import msi.gama.lang.gaml.gaml.Model;
import msi.gama.lang.gaml.gaml.impl.*;
import msi.gaml.compilation.GamlCompilationError;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.jface.action.*;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public abstract class EditFrame extends ApplicationWindow {

	Diagram diagram;
	EditFeature ef;
	IFeatureProvider fp;
	String name;
	EGamaObject eobject;
	StyledText validationResult;
	ValidateText textName;
	EditFrame frame;
	Shell shell;

	/**
	 * Create the application window.
	 */
	public EditFrame(final Diagram diagram, final IFeatureProvider fp, final EditFeature ef, final EGamaObject eobject,
		final String name) {
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

	static public String fromErrorToString(final GamlCompilationError error) {
		String result = "Error concerning: ";
		EObject toto = error.getStatement();
		java.util.List<String> ids = new ArrayList<String>();
		do {
			if ( toto instanceof VariableRefImpl ) {
				VariableRefImpl vv = (VariableRefImpl) toto;
				ids.add(0, vv.getRef().getName());
			} else if ( toto instanceof S_ReflexImpl ) {
				S_ReflexImpl vv = (S_ReflexImpl) toto;
				if ( vv.getName() != null ) {
					ids.add(0, vv.getName());
				} else {
					ids.add(0, "init");
				}
			} else if ( toto instanceof S_SpeciesImpl ) {
				S_SpeciesImpl vv = (S_SpeciesImpl) toto;
				ids.add(0, vv.getName());
			} else if ( toto instanceof S_ActionImpl ) {
				S_ActionImpl vv = (S_ActionImpl) toto;
				ids.add(0, vv.getName());
			} else if ( toto instanceof S_DisplayImpl ) {
				S_DisplayImpl vv = (S_DisplayImpl) toto;
				ids.add(0, vv.getName());
			} else if ( toto instanceof S_ExperimentImpl ) {
				S_ExperimentImpl vv = (S_ExperimentImpl) toto;
				ids.add(0, vv.getName());
			} else if ( toto instanceof S_DefinitionImpl ) {
				S_DefinitionImpl vv = (S_DefinitionImpl) toto;
				ids.add(0, vv.getName());
			}
			toto = toto.eContainer();
		} while (!(toto instanceof Model));
		for ( int i = 0; i < ids.size(); i++ ) {
			String id = ids.get(i);
			result += "->" + id;
		}
		result += " : " + error.toString();
		return result;
	}

	/*
	 * protected void canvasValidation(Composite container) {
	 * Group group = new Group(container, SWT.BORDER);
	 * group.setLayout( new FillLayout(SWT.HORIZONTAL));
	 * group.setText("GAML code compilation result");
	 * 
	 * GridData gridData = new GridData();
	 * gridData.horizontalAlignment = SWT.FILL;
	 * gridData.verticalAlignment = SWT.FILL;
	 * gridData.grabExcessHorizontalSpace = true;
	 * gridData.grabExcessVerticalSpace= true;
	 * group.setLayoutData(gridData);
	 * group.setLayout(new GridLayout(1, false));
	 * 
	 * GridData gridData2 = new GridData();
	 * gridData2.horizontalAlignment = SWT.FILL;
	 * gridData2.verticalAlignment = SWT.FILL;
	 * gridData2.grabExcessHorizontalSpace = true;
	 * gridData2.grabExcessVerticalSpace= true;
	 * 
	 * validationResult = new StyledText(group, SWT.BORDER);
	 * validationResult.setLayoutData(gridData2);
	 * validationResult.setEditable(false);
	 * 
	 * Button btnValidate = new Button(group, SWT.NONE);
	 * btnValidate.addSelectionListener(new SelectionAdapter() {
	 * 
	 * @Override
	 * public void widgetSelected(SelectionEvent e) {
	 * save();
	 * List<GamlCompilationError> errors = ModelGenerator.modelValidation(fp, diagram);
	 * String eC = "";
	 * StyleRange styleRange = new StyleRange();
	 * if (errors.isEmpty()) {
	 * eC = "No compilation error";
	 * styleRange.start = 0;
	 * styleRange.length = eC.length();
	 * styleRange.fontStyle = SWT.BOLD;
	 * styleRange.foreground = getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN);
	 * 
	 * }
	 * else {
	 * for (GamlCompilationError val : errors) eC += fromErrorToString(val) + "\n";
	 * styleRange.start = 0;
	 * styleRange.length = eC.length();
	 * styleRange.fontStyle = SWT.BOLD;
	 * styleRange.foreground = getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_RED);
	 * 
	 * }
	 * validationResult.setText(eC);
	 * validationResult.setStyleRange(styleRange);
	 * }
	 * });
	 * btnValidate.setText("Validate");
	 * }
	 */
	protected void groupName(final Composite container) {
		groupName(container, true);
	}

	protected void groupName(final Composite container, final boolean hasNameFaeture) {
		Group group = new Group(container, SWT.NONE);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		group.setLayoutData(gridData);

		group.setLayout(new GridLayout(2, false));

		CLabel lblName = new CLabel(group, SWT.NONE);
		lblName.setText("Name:");

		GamaDiagramEditor diagramEditor = (GamaDiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();
		textName = new ValidateText(group, SWT.BORDER, diagram, fp, this, diagramEditor, "name", null, null);
		textName.setNameFeature(hasNameFaeture);
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = SWT.FILL;
		gridData2.grabExcessHorizontalSpace = true;
		textName.setLayoutData(gridData2);
		textName.setText(eobject.getName());
		if ( eobject instanceof EWorldAgent ) {
			textName.setEditable(false);
		}
		textName.setSaveData(true);

	}

	protected Canvas canvasName(final Composite container) {
		return canvasName(container, true);
	}

	protected Canvas canvasName(final Composite container, final boolean hasNameFaeture) {
		Canvas canvasName = new Canvas(container, SWT.BORDER);
		GamaDiagramEditor diagramEditor = (GamaDiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();
		textName = new ValidateText(canvasName, SWT.BORDER, diagram, fp, this, diagramEditor, "name", null, null);
		textName.setNameFeature(hasNameFaeture);
		UtilEditFrame.buildCanvasName(container, canvasName, textName, eobject, ef);
		canvasName.setBounds(10, 10, 720, 30);
		textName.setSaveData(true);
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
	protected ToolBarManager createToolBarManager(final int style) {
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
	protected void configureShell(final Shell newShell) {
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

	/*
	 * protected Canvas canvasOkCancel(Composite container) {
	 * //****** CANVAS OK CANCEL BUTTONS *********
	 * Canvas canvasOKCancel = new Canvas(container, SWT.BORDER);
	 * canvasOKCancel.setBounds(10, 460, 720, 30);
	 * 
	 * final Button buttonOK = new Button(canvasOKCancel, SWT.PUSH);
	 * buttonOK.setText("Ok");
	 * buttonOK.setBounds(150, 5, 80, 20);
	 * buttonOK.addSelectionListener(new SelectionAdapter() {
	 * 
	 * @Override
	 * public void widgetSelected(SelectionEvent e) {
	 * 
	 * frame.save();
	 * frame.close();
	 * }
	 * });
	 * 
	 * Button buttonCancel = new Button(canvasOKCancel, SWT.PUSH);
	 * buttonCancel.setText("Cancel");
	 * buttonCancel.setBounds(350, 5, 80, 20);
	 * buttonCancel.addSelectionListener(new SelectionAdapter() {
	 * 
	 * @Override
	 * public void widgetSelected(SelectionEvent e) {
	 * frame.clean();
	 * frame.close();
	 * }
	 * });
	 * return canvasOKCancel;
	 * }
	 */

	/*
	 * protected Group groupOkCancel(Composite container) {
	 * //****** CANVAS OK CANCEL BUTTONS *********
	 * 
	 * Group groupOkCancel = new Group(container, SWT.NONE);
	 * groupOkCancel.setText("Validation");
	 * GridData gridData = new GridData();
	 * gridData.horizontalAlignment = GridData.FILL;
	 * gridData.grabExcessHorizontalSpace = true;
	 * groupOkCancel.setLayoutData(gridData);
	 * 
	 * groupOkCancel.setLayout(new GridLayout(2, true));
	 * 
	 * final Button buttonOK = new Button(groupOkCancel, SWT.PUSH);
	 * GridData gridData2 = new GridData();
	 * gridData2.horizontalAlignment = GridData.CENTER;
	 * buttonOK.setLayoutData(gridData2);
	 * buttonOK.setText("Ok");
	 * buttonOK.addSelectionListener(new SelectionAdapter() {
	 * 
	 * @Override
	 * public void widgetSelected(SelectionEvent e) {
	 * 
	 * frame.save();
	 * frame.close();
	 * }
	 * });
	 * 
	 * Button buttonCancel = new Button(groupOkCancel, SWT.PUSH);
	 * GridData gridData3 = new GridData();
	 * gridData3.horizontalAlignment = GridData.CENTER;
	 * buttonOK.setLayoutData(gridData3);
	 * buttonCancel.setText("Cancel");
	 * buttonCancel.addSelectionListener(new SelectionAdapter() {
	 * 
	 * @Override
	 * public void widgetSelected(SelectionEvent e) {
	 * frame.clean();
	 * frame.close();
	 * }
	 * });
	 * return groupOkCancel;
	 * }
	 */
	protected abstract void save(String name);

	@Override
	protected void handleShellCloseEvent() {
		// create dialog with ok and cancel button and info icon
		frame.clean();
		frame.close();
	}

	@Override
	public void create() {
		setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE);
		super.create();
		shell = getShell();

	}

	protected void clean() {

	}

	public void updateError() {

	}

}
