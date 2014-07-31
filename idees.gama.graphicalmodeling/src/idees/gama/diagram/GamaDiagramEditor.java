package idees.gama.diagram;

import gama.EAction;
import gama.EActionLink;
import gama.EAspect;
import gama.EAspectLink;
import gama.EDisplay;
import gama.EDisplayLink;
import gama.EExperiment;
import gama.EExperimentLink;
import gama.EGamaObject;
import gama.EReflex;
import gama.EReflexLink;
import gama.ESpecies;
import gama.ESubSpeciesLink;
import gama.EVariable;
import gama.EWorldAgent;
import idees.gama.features.modelgeneration.ModelGenerator;
import idees.gama.ui.editFrame.ValidateStyledText;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import msi.gama.common.util.GuiUtils;
import msi.gama.gui.swt.SwtGui;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.gaml.Model;
import msi.gama.lang.gaml.gaml.Statement;
import msi.gama.lang.gaml.gaml.impl.BlockImpl;
import msi.gama.lang.gaml.gaml.impl.FacetImpl;
import msi.gama.lang.gaml.gaml.impl.S_ActionImpl;
import msi.gama.lang.gaml.gaml.impl.S_DefinitionImpl;
import msi.gama.lang.gaml.gaml.impl.S_DisplayImpl;
import msi.gama.lang.gaml.gaml.impl.S_ExperimentImpl;
import msi.gama.lang.gaml.gaml.impl.S_ReflexImpl;
import msi.gama.lang.gaml.gaml.impl.S_SpeciesImpl;
import msi.gama.lang.gaml.gaml.impl.VariableRefImpl;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.lang.gaml.validation.IGamlBuilderListener;
import msi.gama.runtime.GAMA;
import msi.gama.util.GamaList;
import msi.gama.util.GamaMap;
import msi.gaml.compilation.GamlCompilationError;
import msi.gaml.descriptions.ErrorCollector;

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

import com.google.inject.Key;

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
	GamaMap<List<String>, EObject> idsEObjects = new GamaMap<List<String>, EObject>();
	
	
	Map<List<String>,Map<String,String>> errorsLoc = new GamaMap<List<String>, Map<String,String>>();
	Map<List<String>,Map<String,String>> syntaxErrorsLoc = new GamaMap<List<String>, Map<String,String>>();

	static {
		FontData fd = Display.getDefault().getSystemFont().getFontData()[0];
		fd.setStyle(SWT.BOLD);
		labelFont = new Font(Display.getDefault(), fd);
	}

	public GamaDiagramEditor() {
		super();
	}

	@Override
	public void validationEnded(Set<String> experiments, ErrorCollector status) {
		updateExperiments(experiments, status.hasErrors());
		toRefresh = true;
	}
/*public void validationEnded(Set<String> experiments, boolean withErrors) {
		
	}*/

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
	
	/*public void getEObjects(EObject obj, List<String> current, GamaMap<List<String>, EObject> tot) {
		if (obj == null) return;
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
			getEObjects(o, cu2, tot);
		}
	}*/
	
	public void setErrors(List<GamlCompilationError> errors/*, ValidateStyledText vst*/) {
		this.errors = errors;
		/*final IFeatureProvider fp = getDiagramTypeProvider().getFeatureProvider();
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
		if (errors.isEmpty() || worldAgents.isEmpty()) return;*/
		//boolean hasSyntaxError = false;
		for (GamlCompilationError error : errors) {
			EObject toto = error.getStatement();
			System.out.println("error.getStatement() : " + error.getStatement());
			
			/*if (error.toString().equals("Syntax errors detected ")) {
				System.out.println("la");
				//hasSyntaxError = true;
				if (vst != null) {
					Map<String,String> locs = syntaxErrorsLoc.get(vst.getLoc());
					if (locs == null) 
						locs = new GamaMap<String, String>();
					locs.put(vst.getNameLoc(), error.toString());
					syntaxErrorsLoc.put(vst.getLoc(), locs);
					System.out.println("ici");
				}
			}
			System.out.println("syntaxErrorsLoc : " + syntaxErrorsLoc);*/
			GamaList<String> ids = new GamaList<String>();
			String fist_obj = buildLocation(toto,ids);
			while (!ids.isEmpty()) {
				if (!idsEObjects.getKeys().contains(ids)) {
					ids.remove(ids.size()-1);
				} else {
					break;
				}
			}
			Map<String,String> locs = errorsLoc.get(ids) ;
			
			if (locs == null) {
				locs = new GamaMap<String,String>();
			}
			System.out.println("error.getCode() : " + error.getCode());
			String key = (error.getCode().equals("gaml.duplicate.definition.issue") || error.getCode().equals("gaml.duplicate.name.issue")) ? "name":fist_obj;
			locs.put(key, (locs.containsKey(key) ? locs.get(key) : "") + "\n" +error.toString());
			if (error.toString().equals("Syntax errors detected ")) {
				if (syntaxErrorsLoc.isEmpty())
					syntaxErrorsLoc.put(ids, locs);
			} else 
				errorsLoc.put(ids, locs);
			/*final EObject obj = idsEObjects.get(ids);
			
			System.out.println("error :" + error.toString() + "-error.getCode(): " + error.getCode() + " error.getStatement():" + error.getStatement());
			for (String data: error.getData()) {
				System.out.println("data: " + data);
			}
			System.out.println("tot : " + idsEObjects + " ids: " + ids);
			System.out.println("obj : " + obj );*/
			/*if (obj != null) {
				TransactionalEditingDomain domain2 = TransactionUtil
						.getEditingDomain(getDiagram());
				if (domain2 != null) {
					domain2.getCommandStack().execute(
							new RecordingCommand(domain2) {
								public void doExecute() {
									if (obj instanceof EGamaObject) {
										((EGamaObject) obj).setHasError(true);
										((EGamaObject) obj).setError(erStr);
									}	
									else if(obj instanceof EVariable) {
										EVariable var =  ((EVariable) obj);
										var.setHasError(true);
										var.setError(erStr);
										((EGamaObject) var.eContainer()).setHasError(true);
										((EGamaObject)  var.eContainer()).setError(erStr);
									}
								}
							}
						);
				}
			}*/
		}
		toRefresh = false;
		/*if (!hasSyntaxError) {
			syntaxErrorsLoc.clear();
		} else {
			for (List<String> ids : syntaxErrorsLoc.keySet()) {
				final EObject obj = idsEObjects.get(ids);
				if (obj != null) {
					TransactionalEditingDomain domain3 = TransactionUtil
							.getEditingDomain(getDiagram());
					if (domain3 != null) {
						domain3.getCommandStack().execute(
								new RecordingCommand(domain3) {
									public void doExecute() {
										if (obj instanceof EGamaObject) {
											((EGamaObject) obj).setHasError(true);
											((EGamaObject) obj).setError("Syntax errors detected");
										}	
									}
								}
							);
					}
				}
			}
			
		}*/
		//this.getDiagramTypeProvider().getFeatureProvider().getUpdateFeature(context).hasDoneChanges = true;
		updateEObjectErrors();
	}

	public String buildLocation(EObject toto, List<String> ids) {
		String fist_obj = null;
		do {
			System.out.println("toto: " + toto);
			if (toto instanceof S_ReflexImpl) {
				S_ReflexImpl vv = (S_ReflexImpl) toto;
				if (vv.getName() != null)
					ids.add(0,vv.getName());
				if (fist_obj == null) {
					if(vv.getName() != null)
						fist_obj = vv.getName();
					else if (vv.getKey() != null) {
						fist_obj = vv.getKey();
					}
				}
			} else if (toto instanceof S_SpeciesImpl) {
				S_SpeciesImpl vv = (S_SpeciesImpl) toto;
				ids.add(0,vv.getName());
				if (fist_obj == null) {
					fist_obj = vv.getName();		
				}
			 
		} else if (toto instanceof FacetImpl) {
			FacetImpl vv = (FacetImpl) toto;
			System.out.println("vv :" + vv.getKey() + ";");
			if (vv.getKey().equals("torus:") || vv.getKey().equals("width:") ||  vv.getKey().equals("height:") || vv.getKey().equals("neighbours:") 
					|| vv.getKey().equals("among:") || vv.getKey().equals("->")  || vv.getKey().equals("<-")  || vv.getKey().equals("update:")   || vv.getKey().equals("min:")  || vv.getKey().equals("max:")  || vv.getKey().equals("step:") ) {
				fist_obj = vv.getKey();		
			}
		}else if (toto instanceof S_ActionImpl) {
				S_ActionImpl vv = (S_ActionImpl) toto;
				ids.add(0,vv.getName());
				if (fist_obj == null) {
					fist_obj = vv.getName();		
				}
			} else if (toto instanceof S_DisplayImpl) {
				S_DisplayImpl vv = (S_DisplayImpl) toto;
				ids.add(0,vv.getName());
				if (fist_obj == null) {
					fist_obj = vv.getName();	
				}
			} else if (toto instanceof BlockImpl) {
				BlockImpl vv = (BlockImpl) toto;
				//System.out.println("block:" + vv );
				//System.out.println("block getFunction:" + vv.getFunction() );
				//System.out.println("block getStatements:" + vv.getStatements() );
				if (vv.getStatements() != null) {
					for (Statement st : vv.getStatements()) {
						if (st != null && st.getKey() != null && st.getKey().equals("parameter")) {
							ids.add(0,st.getExpr().getOp());
							//System.out.println("st.getExpr " + st.getExpr());
							//System.out.println("st.getFacets " + st.getFacets());

							//System.out.println("st.getBlock " + st.getBlock());
						}
					}
				}
				
			} else if (toto instanceof S_DefinitionImpl) {
				S_DefinitionImpl vv = (S_DefinitionImpl) toto;
				ids.add(0,vv.getName());
			} else if (toto instanceof S_ExperimentImpl) {
				S_ExperimentImpl vv = (S_ExperimentImpl) toto;
				ids.add(0,vv.getName());
				if (fist_obj == null) {
					fist_obj = vv.getName();
				}
			} else if (toto instanceof VariableRefImpl) {
				VariableRefImpl vv = (VariableRefImpl) toto;
				System.out.println("var:" + vv );
				System.out.println("var getRef:" + vv.getRef() );
				
			/*} else if (toto instanceof S_DefinitionImpl) {
				S_DefinitionImpl vv = (S_DefinitionImpl) toto;
				ids.add(0,vv.getName()); 
				if (fist_obj == null) {
					fist_obj = vv.getName();	
				}*/
			} else if (toto instanceof EGamaObject) {
				EGamaObject vv = (EGamaObject) toto;
				ids.add(0,vv.getName()); 
				if (fist_obj == null) {
					fist_obj = vv.getName();	
				}
			}
			if (toto instanceof EAction) {
				toto = ((EAction) toto).getActionLinks().get(0).getSpecies();
			} else if (toto instanceof EReflex) {
				toto = ((EReflex) toto).getReflexLinks().get(0).getSpecies();
			} else if (toto instanceof EAspect) {
				toto = ((EAspect) toto).getAspectLinks().get(0).getSpecies();
			} else if (toto instanceof EExperiment) {
				toto = ((EExperiment) toto).getExperimentLink().getSpecies();
			} else if (toto instanceof EDisplay) {
				toto = ((EDisplay) toto).getDisplayLink().getExperiment();
			} else if (toto instanceof ESpecies) {
				if (!((ESpecies) toto).getName().equals("world"))
					toto = ((ESpecies) toto).getMacroSpeciesLinks().get(0).getMacro();
				else toto = null;
			}else {
				toto = toto != null ? toto.eContainer() : toto;
			}
			
		} while ((toto != null) && !(toto instanceof Model)) ;
		if (!ids.contains("world"))
			ids.add(0, "world");
		System.out.println("ids: " + ids);
		return fist_obj;
	}
	public String containErrors (List<String> location, String name, List<String> uselessName) {
		System.out.println("syntaxErrorsLoc: " + syntaxErrorsLoc);
		boolean noName = name.equals("");
		System.out.println("location: " + location + " name: " + name);
		/*if (!(syntaxErrorsLoc == null || syntaxErrorsLoc.isEmpty()|| !syntaxErrorsLoc.containsKey(location))) {
			if (noName) {
				Map<String, String> er = syntaxErrorsLoc.get(location);
				if (uselessName != null) {
					for (String val: uselessName) 
						er.remove(val);
				}
				System.out.println("er : " + er);
				if (! er.isEmpty()) {
					List<String> l = new GamaList<String>(er.values());
					return l.get(0);
				}
					
			} else if (syntaxErrorsLoc.get(location).containsKey(name))
				return syntaxErrorsLoc.get(location).get(name);
		}*/
			
		System.out.println("errorsLoc: " + errorsLoc);
		if (errorsLoc == null || errorsLoc.isEmpty()|| !errorsLoc.containsKey(location))
			return "";
		
		if (noName) {
			Map<String, String> er = errorsLoc.get(location);
			if (uselessName != null) {
				for (String val: uselessName) 
					er.remove(val);
			}
			System.out.println("er 2: " + er);
			if (! er.isEmpty()) {
				List<String> l = new GamaList<String>(er.values());
				return l.get(0);
			}
		} else if (errorsLoc.get(location).containsKey(name))
			return errorsLoc.get(location).get(name);

		return "";
	}
	
	public List<String> computeIds(final EObject obj){
		EObject toto = obj;
		List<String> ids = new GamaList<String>();
		do {
			if (toto != null) {
				if (toto instanceof EGamaObject)
					ids.add(0,((EGamaObject) toto).getName());
				else if (toto instanceof EVariable)
					ids.add(0,((EVariable) toto).getName());
				if (toto instanceof EAction) {
					toto = ((EAction) toto).getActionLinks().get(0).getSpecies();
				} else if (toto instanceof EReflex) {
					toto = ((EReflex) toto).getReflexLinks().get(0).getSpecies();
				} else if (toto instanceof EAspect) {
					toto = ((EAspect) toto).getAspectLinks().get(0).getSpecies();
				} else if (toto instanceof EExperiment) {
					toto = ((EExperiment) toto).getExperimentLink().getSpecies();
				} else if (toto instanceof EDisplay) {
					toto = ((EDisplay) toto).getDisplayLink().getExperiment();
				} else if (toto instanceof ESpecies) {
					if (!((ESpecies) toto).getName().equals("world"))
						toto = ((ESpecies) toto).getMacroSpeciesLinks().get(0).getMacro();
					else toto = null;
				} else {
					toto = (EObject) toto.eContainer();
				}
			}	
		} while ((toto != null) && !(toto instanceof Model)) ;
		if (!ids.contains("world")) 
			ids.add(0,"world");
		return ids;
	}
	public void addEOject(final EObject obj){
		List<String> ids = computeIds(obj);
		idsEObjects.put(ids, obj instanceof EVariable ?((EVariable) obj).eContainer() :obj);
		System.out.println("add object: " + obj + " ids: " + ids + " idsEObjects: " + idsEObjects.keySet());
	}
	
	public void addEOject(final EObject obj, String name){
		List<String> ids = computeIds(obj);
		ids.add(name);
		idsEObjects.put(ids, obj );
	}
	public void removeEOject(final EObject obj){
		List<String> ids = computeIds(obj);
		idsEObjects.remove(ids);
	}
	
	public void updateEObjectErrors() {
		TransactionalEditingDomain domain = TransactionUtil
				.getEditingDomain(getDiagram());
		if (domain != null) {
			domain.getCommandStack().execute(
				new RecordingCommand(domain) {
					public void doExecute() {
		
		initIdsEObjects();
		System.out.println("idsEObjects : " + idsEObjects);
		System.out.println("errorsLoc : " + errorsLoc);
		System.out.println("syntaxErrorsLoc : " + syntaxErrorsLoc);
		List<List<String>> vals = new GamaList<List<String>>(idsEObjects.keySet());
		Map<List<String>, EObject> valM = new GamaMap<List<String>, EObject>(idsEObjects);
		for (final EObject bo : valM.values()) {
			if (bo instanceof EGamaObject)
				((EGamaObject) bo).setHasError(false);
			else if (bo instanceof EVariable)
				((EVariable) bo).setHasError(false);
		}
		for (final List<String> ids : vals) {
			final EObject obj = valM.get(ids);
			System.out.println("ids: " + ids + " obj: " + obj);
			if (obj == null) continue;
			final boolean val = syntaxErrorsLoc.containsKey(ids) || errorsLoc.containsKey(ids);
			System.out.println("val = " +val);
				if (val && obj instanceof EGamaObject)
					((EGamaObject) obj).setHasError(val);	
				System.out.println(((EGamaObject) obj).getHasError());
			}
		}
		}
			);
		}		
	}

	public Map<List<String>, Map<String,String>> getErrorsLoc() {
		return errorsLoc;
	}

	public Map<List<String>, Map<String, String>> getSyntaxErrorsLoc() {
		return syntaxErrorsLoc;
	}

	public GamaMap<List<String>, EObject> getIdsEObjects() {
		return idsEObjects;
	}
	
	public void initIdsEObjects() {
		System.out.println("initIdsEObjects");
		if (diagram != null && !diagram.getChildren().isEmpty() && idsEObjects.isEmpty()) {
			for (Shape obj : getDiagram().getChildren()) {
				Object bo = featureProvider.getBusinessObjectForPictogramElement(obj);
				System.out.println("obj : " + bo);
				if (bo instanceof EObject) {
					addEOject((EObject) bo);
					
				}
				if (bo instanceof ESpecies) {
					boolean shape = false;
					boolean location = false;
					for (EVariable v : ((ESpecies) bo).getVariables()) {
						addEOject(v);
						if (location || v.getName().equals("location")) 
							location = true;
						if (shape || v.getName().equals("shape")) 
							shape = true;
					}
					if (! shape) {
						addEOject((EObject) bo, "shape");
					}
					if (! location) {
						addEOject((EObject) bo, "location");
					}
				}
			}
		}
	}
	
	public void updateErrors(List<String> oldId, List<String> newId){
		Map<String,String> eMap = errorsLoc.remove(oldId);
		if (eMap != null)
			errorsLoc.put(newId, eMap);
		
		Map<String,String> eMap2 = syntaxErrorsLoc.remove(oldId);
		if (eMap2 != null)
			syntaxErrorsLoc.put(newId, eMap2);
	
		System.out.println("idsEObjects: " + idsEObjects);
		EObject obj = idsEObjects.remove(oldId);
		if (obj != null)
			idsEObjects.put(newId, obj);
		System.out.println("idsEObjects apres: " + idsEObjects);
	}

}
