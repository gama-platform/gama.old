package idees.gama.diagram;

import idees.gama.features.modelgeneration.ModelGenerator;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import msi.gama.common.util.GuiUtils;
import msi.gama.gui.swt.SwtGui;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.lang.gaml.validation.IGamlBuilderListener;
import msi.gama.runtime.GAMA;
import msi.gaml.compilation.GamlCompilationError;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class GamaDiagramEditor extends DiagramEditor implements IGamlBuilderListener{
	private static final int INITIAL_BUTTONS = 20;
	public static final Color COLOR_TEXT = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
	private static Font labelFont;
	
	
	GamlResource resource;
	List<String> abbreviations = new ArrayList();
	List<String> completeNamesOfExperiments = new ArrayList();
	boolean wasOK = true, inited = false;
	Composite toolbar, parent, indicator;
	Button[] buttons = new Button[INITIAL_BUTTONS];
	CLabel status;
	Button menu;
	Diagram diagram;
	IFeatureProvider featureProvider;
	List<GamlCompilationError> errors;
	
	static {
		FontData fd = Display.getDefault().getSystemFont().getFontData()[0];
		fd.setStyle(SWT.BOLD);
		labelFont = new Font(Display.getDefault(), fd);
	}
	
	public GamaDiagramEditor() {
		super();
	}
	@Override
	public void validationEnded(Set<String> experiments, boolean withErrors) {
		updateExperiments(experiments, withErrors);
	}
	
	private void updateExperiments(final Set<String> newExperiments, final boolean withErrors) {
		if ( withErrors == true && wasOK == false ) { return; }
		Set<String> oldNames = new LinkedHashSet(completeNamesOfExperiments);
		if ( inited && wasOK && !withErrors && oldNames.equals(newExperiments) ) { return; }
		inited = true;
		wasOK = !withErrors;
		completeNamesOfExperiments = new ArrayList(newExperiments);
		buildAbbreviations();
		updateToolbar(wasOK);
	}
	
	private void buildAbbreviations() {
		// Very simple method used here
		int size = completeNamesOfExperiments.size();
		abbreviations.clear();
		if ( size > 6 ) {
			// We remove "Experiment".
			for ( String s : completeNamesOfExperiments ) {
				abbreviations.add(s.replaceFirst("Experiment ", ""));
			}
		} else if ( size > 4 ) {
			// We replace "Experiment" by "Exp."
			for ( String s : completeNamesOfExperiments ) {
				abbreviations.add(s.replaceFirst("Experiment", "Exp."));
			}
		} else {
			// We copy the names as it is
			abbreviations.addAll(completeNamesOfExperiments);
		}
	}
	
	private void updateToolbar(final boolean ok) {

		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				if ( toolbar == null || toolbar.isDisposed() ) { return; }
				for ( Button b : buttons ) {
					if ( b.isVisible() ) {
						hideButton(b);
					}
				}
				if ( ok ) {
					int size = abbreviations.size();
					if ( size == 0 ) {
						setStatus("Model is functional, but no experiments have been defined.", ok);
					} else {
						setStatus(size == 1 ? "Run :" : "Run :", ok);
					}
					int i = 0;
					for ( String e : abbreviations ) {
						enableButton(i++, e);
					}
				} else {
					setStatus("Error(s) detected. Impossible to run any experiment", ok);
				}

				toolbar.layout(true);
			}
		});

	}
	
	private void enableButton(final int index, final String text) {
		if ( text == null ) { return; }
		((GridData) buttons[index].getLayoutData()).exclude = false;
		buttons[index].setVisible(true);
		buttons[index].setText(text);
		buttons[index].pack();
	}

	private void hideButton(final Button b) {
		((GridData) b.getLayoutData()).exclude = true;
		b.setVisible(false);
	}

	private void setStatus(final String text, final boolean ok) {
		Color c =
			ok ? abbreviations.size() == 0 ? SwtGui.getWarningColor() : SwtGui.getOkColor() : SwtGui.getErrorColor();
		indicator.setBackground(c);
		status.setText(text);
	}
	
	
	
	public void createPartControlXP(final Composite parent) {
		this.parent = parent;
		
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;	
		parent.setLayout(layout);
		
		toolbar = new Composite(parent, SWT.NONE);
		
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.heightHint = 26;
		data.horizontalIndent = 10;
		toolbar.setLayoutData(data);
		layout = new GridLayout(INITIAL_BUTTONS + 2, false);
		layout.horizontalSpacing = 2;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		
		Composite others = new Composite(parent, SWT.None);
		data = new GridData(SWT.RIGHT, SWT.FILL, false, false);
		data.heightHint = 26;
		others.setLayoutData(data);
		layout = new GridLayout(2, false);
		others.setLayout(layout);

		indicator = new Composite(parent, SWT.None);
		data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.horizontalSpan = 2;
		data.heightHint = 8;
		indicator.setLayoutData(data);
		FillLayout layout2 = new FillLayout();
		layout2.marginWidth = 12;
		layout2.marginHeight = 0;
		indicator.setLayout(layout2);

		status = new CLabel(toolbar, SWT.NONE);
		status.setFont(labelFont);
		data = new GridData(SWT.FILL, SWT.CENTER, false, false);
		data.minimumHeight = SWT.DEFAULT;
		status.setLayoutData(data);
		status.setForeground(COLOR_TEXT);

		for ( int i = 0; i < INITIAL_BUTTONS; i++ ) {
			buttons[i] = new Button(toolbar, SWT.PUSH);
			data = new GridData(SWT.LEFT, SWT.CENTER, false, false);
			buttons[i].setLayoutData(data);
			buttons[i].setText("Experiment " + i);
			buttons[i].addSelectionListener(listener);
			hideButton(buttons[i]);
		}
		
		// Asking the editor to fill the rest
		final Composite parent2 = new Composite(parent, SWT.NONE);
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalSpan = 2;
		parent2.setLayoutData(data);
		parent2.setLayout(new FillLayout());
		super.createPartControl(parent2);	
	
	}
		
	private final SelectionListener listener = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent evt) {
			diagram = getDiagram();
			String xp = ((Button) evt.getSource()).getText();
			if (diagram != null && !diagram.getChildren().isEmpty())
			{	IModel model = ModelGenerator.modelGeneration(featureProvider, diagram);
				if (model != null) {
					GuiUtils.openSimulationPerspective();
					GAMA.controller.newExperiment(xp, model);
				}
			}
			
		}
	};


	@Override
	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
		diagram = getDiagram();
		if (diagram != null && !diagram.getChildren().isEmpty())
			ModelGenerator.modelValidation(this.getDiagramTypeProvider().getFeatureProvider(), diagram);
	}
	@Override
	public void initRefresh() {
		diagram = getDiagram();
		featureProvider = this.getDiagramTypeProvider().getFeatureProvider();
		if (diagram != null && !diagram.getChildren().isEmpty())
			ModelGenerator.modelValidation(this.getDiagramTypeProvider().getFeatureProvider(), diagram);
		super.initRefresh();
		this.refreshPalette();
		
	}
	
	public Diagram getDiagram(){
		Diagram diag = diagram;
		if (diagram == null) 
			diag = this.getDiagramTypeProvider().getDiagram();
		return diag;
	}
	
	@Override
	public void createPartControl(final Composite parent) {
		
		createPartControlXP(parent);
	}

	public GamlResource getResource() {
		return resource;
	}
	public void setResource(GamlResource resource) {
		this.resource = resource;
		if (resource != null) resource.setListener(GamaDiagramEditor.this);
	}
	public List<GamlCompilationError> getErrors() {
		return errors;
	}
	public void setErrors(List<GamlCompilationError> errors) {
		this.errors = errors;

	}
	
	
	
}
