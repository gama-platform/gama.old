package idees.gama.ui.editFrame;

import gama.EAspect;
import gama.EDisplay;
import gama.ELayer;
import gama.ELayerAspect;
import idees.gama.diagram.GamaDiagramEditor;
import idees.gama.features.edit.EditFeature;
import idees.gama.features.modelgeneration.ModelGenerator;

import java.util.List;

import msi.gama.util.GamaList;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class EditAspectFrame extends EditFrame {
	
	StyledText gamlCode;
	Table layerViewer;
	EditAspectFrame frame;
	List<ELayerAspect> layers;
	Diagram diagram;
	/**
	 * Create the application window.
	 */
	public EditAspectFrame(Diagram diagram, IFeatureProvider fp, EditFeature eaf, EAspect aspect, String name) {	
		super(diagram, fp, eaf,  aspect, name == null ? "Aspect definition" : name );
		frame = this;
		layers = new GamaList<ELayerAspect>();
		layers.addAll(aspect.getLayers());
		this.diagram = diagram;
	}

	/**
	 * Create contents of the application window.
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		//****** CANVAS NAME *********
		groupName(container);
		
		//****** CANVAS LAYERS *********
		groupLayers(container);
				
		//****** CANVAS OK/CANCEL *********
		//groupOkCancel(container);
		
		return container;
	}
	
	protected void groupLayers(Composite container) {
		final GamaDiagramEditor diagramEditor = ((GamaDiagramEditor)fp.getDiagramTypeProvider().getDiagramEditor());
		List<String> vals = new GamaList<String>();
		diagramEditor.buildLocation(eobject, vals);
		vals.add("draw");
		diagramEditor.getIdsEObjects().put(vals, eobject);
		//****** CANVAS LAYERS *********
		Group group = new Group(container, SWT.NONE);
		
		group.setLayout( new FillLayout(SWT.VERTICAL));
	    group.setText("Aspect layers");
	    
	   GridData gridData = new GridData();
	   gridData.horizontalAlignment = SWT.FILL;
	   gridData.verticalAlignment = SWT.FILL;
	   gridData.grabExcessHorizontalSpace = true;
	   gridData.grabExcessVerticalSpace= true;
	   group.setLayoutData(gridData);
	   group.setLayout(new GridLayout(1, false));
	   
	   GridData gridData2 = new GridData();
	   gridData2.horizontalAlignment = SWT.FILL;
	   gridData2.verticalAlignment = SWT.FILL;
	   gridData2.grabExcessHorizontalSpace = true;
	   gridData2.grabExcessVerticalSpace= true;
	 		
		//layerViewer = new org.eclipse.swt.widgets.List(group, SWT.BORDER | SWT.V_SCROLL);
	   layerViewer = new Table(group,  SWT.BORDER | SWT.V_SCROLL|SWT.SINGLE | SWT.FULL_SELECTION
		       /* | SWT.HIDE_SELECTION*/);
	   layerViewer.setHeaderVisible(false);
		layerViewer.setLinesVisible(false);
		   
		layerViewer.setLayoutData(gridData2);
		
		final EAspect aspect = (EAspect) eobject;
		for (ELayerAspect la : aspect.getLayers()) {
			//layerViewer.add(la.getName());
			TableItem ti =  new TableItem(layerViewer, SWT.NONE);
			ti.setText(la.getName());
			//ti.setBackground(new Color(frame.getShell().getDisplay(), 100,255,100));
			ti.setBackground(hasError(la) ?  new Color(frame.getShell().getDisplay(), 255,100,100): new Color(frame.getShell().getDisplay(), 100,255,100));
			
		}
		/*for (ELayerAspect lay : layers) {
			layerViewer.add(lay.getName());
		}*/
		
		Composite containerButtons = new Composite(group, SWT.NONE);
		containerButtons.setLayout(new FillLayout(SWT.HORIZONTAL));
		 GridData gridData3 = new GridData();
		 gridData3.horizontalAlignment = SWT.FILL;
		 gridData3.grabExcessHorizontalSpace = true;
		 containerButtons.setLayoutData(gridData3);
		 
		Button addLayerBtn = new Button(containerButtons, SWT.BUTTON1);
		addLayerBtn.setText("Add");
		addLayerBtn.setSelection(false);
		addLayerBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final ELayerAspect elayer = gama.GamaFactory.eINSTANCE.createELayerAspect();
				TransactionalEditingDomain domain = TransactionUtil
						.getEditingDomain(eobject);
				if (domain != null) {
					domain.getCommandStack().execute(new RecordingCommand(domain) {
						public void doExecute() {
							elayer.setName("Layer");
							elayer.setAspect((EAspect) eobject);
							diagram.eResource().getContents().add(elayer);
							TableItem ti =  new TableItem(layerViewer, SWT.NONE);
							ti.setText(elayer.getName());
							ti.setBackground(hasError(elayer) ?  new Color(frame.getShell().getDisplay(), 255,100,100): new Color(frame.getShell().getDisplay(), 100,255,100));
							((EAspect) eobject).getLayers().add(elayer);
							
						}
					});
				}
					EditLayerAspectFrame eaf = new EditLayerAspectFrame(elayer, frame, false, diagram,fp,ef); 
					eaf.open();
				} 
		});
		
		Button editLayerBtn = new Button(containerButtons, SWT.BUTTON1);
		editLayerBtn.setText("Edit");
		editLayerBtn.setSelection(false);
		editLayerBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (layerViewer.getSelectionCount() == 1) {
					final int index = layerViewer.getSelectionIndex();
					ELayerAspect layer = ((EAspect) eobject).getLayers().get(index);
					EditLayerAspectFrame eaf = new EditLayerAspectFrame(layer, frame, true, diagram,fp,ef);
					eaf.open();
				}
			}
		});
		
		Button removeLayerBtn = new Button(containerButtons, SWT.BUTTON1);
		removeLayerBtn.setText("Remove");
		removeLayerBtn.setSelection(false);
		removeLayerBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (layerViewer.getSelectionCount() == 1) {
					final int index = layerViewer.getSelectionIndex();
					layerViewer.remove(index);
					final ELayerAspect lay =layers.remove(index);
					TransactionalEditingDomain domain = TransactionUtil
							.getEditingDomain(eobject);
					if (domain != null) {
						domain.getCommandStack().execute(new RecordingCommand(domain) {
							public void doExecute() {
								aspect.getLayers().remove(index);
								diagram.eResource().getContents().remove(lay);
								EcoreUtil.delete(lay);
								
							}
						});
					}
					
					save("");
					 ModelGenerator.modelValidation(fp, diagram);
					 diagramEditor.updateEObjectErrors();
					ef.hasDoneChanges = true;
				}
			}
		});
		Button btnUp = new Button(containerButtons, SWT.ARROW | SWT.UP);
		//btnUp.setText("Up");
		btnUp.setSelection(false);
		btnUp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (layerViewer.getSelectionCount() == 1) {
					int index = layerViewer.getSelectionIndex();
					if (index > 0) {
						((EDisplay) eobject).getLayers().move(index - 1, index);
						layerViewer.removeAll();
						for (ELayerAspect la : ((EAspect) eobject).getLayers()) {
							TableItem ti =  new TableItem(layerViewer, SWT.NONE);
							ti.setText(la.getName());
							ti.setBackground(hasError(la) ?  new Color(frame.getShell().getDisplay(), 255,100,100): new Color(frame.getShell().getDisplay(), 100,255,100));
						}	
					}	
				}
			}
		});
		Button btnDown = new Button(containerButtons, SWT.ARROW | SWT.DOWN);
		//btnDown.setText("Down");
		btnDown.setSelection(false);
		btnDown.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (layerViewer.getSelectionCount() == 1) {
					int index = layerViewer.getSelectionIndex();
					if (index < layerViewer.getItemCount() - 1) {
						((EDisplay) eobject).getLayers().move(index + 1, index);
						layerViewer.removeAll();
						for (ELayerAspect la : ((EAspect) eobject).getLayers()) {
							TableItem ti =  new TableItem(layerViewer, SWT.NONE);
							ti.setText(la.getName());
							ti.setBackground(hasError(la) ?  new Color(frame.getShell().getDisplay(), 255,100,100): new Color(frame.getShell().getDisplay(), 100,255,100));
						}
					}	
				}
			}
		});
	}
	
	
	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(743, 430);
	}

	public List<ELayerAspect> getLayers() {
		return layers;
	}

	public void setLayers(List<ELayerAspect> layers) {
		this.layers = layers;
	}

	public Table getLayerViewer() {
		return layerViewer;
	}
	
	protected void clean() {
		EAspect aspect = ((EAspect) eobject);
		for (ELayerAspect lay: layers) {
			if (! aspect.getLayers().contains(lay)) {
				EcoreUtil.delete((EObject) lay, true);
			}
		}	
		layers.clear();
	}

	

	@Override
	protected void save(String name) {
		 TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(eobject);
			if (domain != null) {
			    domain.getCommandStack().execute(new RecordingCommand(domain) {
			    	     public void doExecute() {
			    	    	 eobject.setName(textName.getText());
			    	  
			    	     }
			    	  });
			}
	    ef.hasDoneChanges = true;  
	    ModelGenerator.modelValidation(fp, diagram);
	}
	
	public boolean hasError(ELayerAspect elayer) {
		final GamaDiagramEditor diagramEditor = ((GamaDiagramEditor)fp.getDiagramTypeProvider().getDiagramEditor());
		List<String> ids = new GamaList<String>();
		diagramEditor.buildLocation(elayer, ids);
		
		if (diagramEditor.getErrorsLoc().isEmpty() && diagramEditor.getSyntaxErrorsLoc().isEmpty())
			return false;
		
		return diagramEditor.getErrorsLoc().containsKey(ids) || diagramEditor.getSyntaxErrorsLoc().containsKey(ids);
	}
	
	public void updateLayer() {
		layerViewer.removeAll();
		for (ELayerAspect elayer : ((EAspect) eobject).getLayers()) {
			TableItem ti =  new TableItem(layerViewer, SWT.NONE);
			ti.setText(elayer.getName());
			ti.setBackground(hasError(elayer) ?  new Color(frame.getShell().getDisplay(), 255,100,100): new Color(frame.getShell().getDisplay(), 100,255,100));
			
		}
	}
	
}

