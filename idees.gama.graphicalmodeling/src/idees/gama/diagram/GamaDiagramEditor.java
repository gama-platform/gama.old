package idees.gama.diagram;

import gama.EActionLink;
import gama.EAspectLink;
import gama.EDisplayLink;
import gama.EExperiment;
import gama.EExperimentLink;
import gama.EGamaObject;
import gama.EReflexLink;
import gama.ESpecies;
import gama.ESubSpeciesLink;
import gama.EVariable;
import gama.EWorldAgent;
import idees.gama.features.modelgeneration.ModelGenerator;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import msi.gama.common.util.GuiUtils;
import msi.gama.gui.swt.SwtGui;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.gaml.Model;
import msi.gama.lang.gaml.gaml.impl.S_ActionImpl;
import msi.gama.lang.gaml.gaml.impl.S_DefinitionImpl;
import msi.gama.lang.gaml.gaml.impl.S_DisplayImpl;
import msi.gama.lang.gaml.gaml.impl.S_ExperimentImpl;
import msi.gama.lang.gaml.gaml.impl.S_ReflexImpl;
import msi.gama.lang.gaml.gaml.impl.S_SpeciesImpl;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.lang.gaml.validation.IGamlBuilderListener;
import msi.gama.runtime.GAMA;
import msi.gama.util.GamaList;
import msi.gama.util.GamaMap;
import msi.gaml.compilation.GamlCompilationError;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.Shape;
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

public class GamaDiagramEditor extends DiagramEditor implements
		IGamlBuilderListener {
	private static final int INITIAL_BUTTONS = 20;
	public static final Color COLOR_TEXT = Display.getDefault().getSystemColor(
			SWT.COLOR_BLACK);
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
	boolean toRefresh = true;

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
		toRefresh = true;
	}

	private void updateExperiments(final Set<String> newExperiments,
			final boolean withErrors) {
		if (withErrors == true && wasOK == false) {
			return;
		}
		Set<String> oldNames = new LinkedHashSet(completeNamesOfExperiments);
		if (inited && wasOK && !withErrors && oldNames.equals(newExperiments)) {
			return;
		}
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
		if (size > 6) {
			// We remove "Experiment".
			for (String s : completeNamesOfExperiments) {
				abbreviations.add(s.replaceFirst("Experiment ", ""));
			}
		} else if (size > 4) {
			// We replace "Experiment" by "Exp."
			for (String s : completeNamesOfExperiments) {
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
				if (toolbar == null || toolbar.isDisposed()) {
					return;
				}
				for (Button b : buttons) {
					if (b.isVisible()) {
						hideButton(b);
					}
				}
				if (ok) {
					int size = abbreviations.size();
					if (size == 0) {
						setStatus(
								"Model is functional, but no experiments have been defined.",
								ok);
					} else {
						setStatus(size == 1 ? "Run :" : "Run :", ok);
					}
					int i = 0;
					for (String e : abbreviations) {
						enableButton(i++, e);
					}
				} else {
					setStatus(
							"Error(s) detected. Impossible to run any experiment",
							ok);
				}

				toolbar.layout(true);
			}
		});

	}

	private void enableButton(final int index, final String text) {
		if (text == null) {
			return;
		}
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
		Color c = ok ? abbreviations.size() == 0 ? SwtGui.getWarningColor()
				: SwtGui.getOkColor() : SwtGui.getErrorColor();
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

		for (int i = 0; i < INITIAL_BUTTONS; i++) {
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
			if (diagram != null && !diagram.getChildren().isEmpty()) {
				IModel model = ModelGenerator.modelGeneration(featureProvider,
						diagram);
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
	}

	@Override
	public void initRefresh() {
		
		if (toRefresh) {
			diagram = getDiagram();
			featureProvider = this.getDiagramTypeProvider().getFeatureProvider();
			if (diagram != null && !diagram.getChildren().isEmpty())
				ModelGenerator.modelValidation(this.getDiagramTypeProvider()
					.getFeatureProvider(), diagram);
		} 
		super.initRefresh();
		this.refreshPalette();

	}

	public Diagram getDiagram() {
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
		if (resource != null)
			resource.setListener(GamaDiagramEditor.this);
	}

	public List<GamlCompilationError> getErrors() {
		return errors;
	}
	
	public void getEObjects(EObject obj, List<String> current, GamaMap<List<String>, EObject> tot) {
		String objStr = obj instanceof EGamaObject ? ((EGamaObject) obj).getName() :((EVariable) obj).getName() ;
		current.add(objStr);
		tot.put(current, obj);
		List<EObject> eObjs = new GamaList<EObject>();
		if (obj instanceof ESpecies) {
			ESpecies sp = (ESpecies) obj;
			for (ESubSpeciesLink ob : sp.getMicroSpeciesLinks())  {eObjs.add(ob.getMicro());}
			for (EActionLink ob : sp.getActionLinks())  {eObjs.add(ob.getAction());}
			for (EAspectLink ob : sp.getAspectLinks())  {eObjs.add(ob.getAspect());}
			for (EReflexLink ob : sp.getReflexLinks())  {eObjs.add(ob.getReflex());}
			for (EVariable ob : sp.getVariables())  {eObjs.add(ob);}
		}
		if (obj instanceof EWorldAgent) {
			EWorldAgent sp = (EWorldAgent) obj;
			for (EExperimentLink ob : sp.getExperimentLinks())  {eObjs.add(ob.getExperiment());}
		}
		if (obj instanceof EExperiment) {
			EExperiment sp = (EExperiment) obj;
			for (EDisplayLink ob : sp.getDisplayLinks())  {eObjs.add(ob.getDisplay());}
		}
		for (EObject o : eObjs) {
			GamaList<String> cu2 = new GamaList<String>(current);
			getEObjects((EGamaObject) o, cu2, tot);
		}
	}

	public void setErrors(List<GamlCompilationError> errors) {
		this.errors = errors;
		final IFeatureProvider fp = getDiagramTypeProvider().getFeatureProvider();
		final List<Shape> contents = diagram.getChildren();
		if (contents == null) return;			
		final List<EWorldAgent> worldAgents = new GamaList<EWorldAgent>();
			TransactionalEditingDomain domain = TransactionUtil
					.getEditingDomain(getDiagram());
			if (domain != null) {
				domain.getCommandStack().execute(
						new RecordingCommand(domain) {
							public void doExecute() {
			for (Shape obj : contents) {
				final Object bo = fp.getBusinessObjectForPictogramElement(obj);
				if (bo instanceof EGamaObject) {
					((EGamaObject) bo).setHasError(false);
					((EGamaObject) bo).setError("");
				}	
				else if(bo instanceof EVariable) {
					((EVariable) bo).setHasError(false);
					((EVariable) bo).setError("");
				}
				if (bo instanceof EWorldAgent) {
					worldAgents.add((EWorldAgent) bo);
				}
			}
			}});
		}
		if (errors.isEmpty() || worldAgents.isEmpty()) return;
		EWorldAgent worldAgent = worldAgents.get(0);
		GamaMap<List<String>, EObject> tot = new GamaMap<List<String>, EObject>();
		getEObjects(worldAgent, new GamaList<String>(), tot);
		for (GamlCompilationError error : errors) {
			EObject toto = error.getStatement();
			GamaList<String> ids = new GamaList<String>();
			final String erStr = error.getCode();
			do {
				if (toto instanceof S_ReflexImpl) {
					S_ReflexImpl vv = (S_ReflexImpl) toto;
					if (vv.getName() != null)
						ids.add(0,vv.getName());
				} else if (toto instanceof S_SpeciesImpl) {
					S_SpeciesImpl vv = (S_SpeciesImpl) toto;
					ids.add(0,vv.getName());
				} else if (toto instanceof S_ActionImpl) {
					S_ActionImpl vv = (S_ActionImpl) toto;
					ids.add(0,vv.getName());
				} else if (toto instanceof S_DisplayImpl) {
					S_DisplayImpl vv = (S_DisplayImpl) toto;
					ids.add(0,vv.getName());
				} else if (toto instanceof S_ExperimentImpl) {
					S_ExperimentImpl vv = (S_ExperimentImpl) toto;
					ids.add(0,vv.getName());
				} else if (toto instanceof S_DefinitionImpl) {
					S_DefinitionImpl vv = (S_DefinitionImpl) toto;
					ids.add(0,vv.getName()); 
				}
				
				toto = toto.eContainer();
			} while (!(toto instanceof Model)) ;
			ids.add(0,worldAgents.get(0).getName());
			final EObject obj = tot.get(ids);
			if (obj != null) {
				TransactionalEditingDomain domain2 = TransactionUtil
						.getEditingDomain(getDiagram());
				if (domain2 != null) {
					domain2.getCommandStack().execute(
							new RecordingCommand(domain) {
								public void doExecute() {
									if (obj instanceof EGamaObject) {
										((EGamaObject) obj).setHasError(true);
										((EGamaObject) obj).setError(erStr);
									}	
									else if(obj instanceof EVariable) {
										EVariable var =  ((EVariable) obj);
										var.setHasError(true);
										var.setError(erStr);
										
									}
								}
							}
						);
				}
			}
		}
		toRefresh = false;
		
		//this.getDiagramTypeProvider().getFeatureProvider().getUpdateFeature(context).hasDoneChanges = true;

	}

}
