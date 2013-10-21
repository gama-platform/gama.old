package idees.gama.ui.editFrame;

import gama.EAspect;
import gama.ELayerAspect;
import idees.gama.features.edit.EditFeature;

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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

public class EditAspectFrame extends EditFrame {
	
	StyledText gamlCode;
	org.eclipse.swt.widgets.List layerViewer;
	EditAspectFrame frame;
	List<ELayerAspect> layers;
	/**
	 * Create the application window.
	 */
	public EditAspectFrame(Diagram diagram, IFeatureProvider fp, EditFeature eaf, EAspect aspect, String name) {	
		super(diagram, fp, eaf,  aspect, name == null ? "Aspect definition" : name );
		frame = this;
		layers = new GamaList<ELayerAspect>();
		layers.addAll(aspect.getLayers());
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
		groupOkCancel(container);
		
		return container;
	}
	
	protected void groupLayers(Composite container) {
		
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
	 		
		layerViewer = new org.eclipse.swt.widgets.List(group, SWT.BORDER | SWT.V_SCROLL);
		layerViewer.setLayoutData(gridData2);
		for (ELayerAspect lay : layers) {
			layerViewer.add(lay.getName());
		}
		
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
					ELayerAspect elayer = gama.GamaFactory.eINSTANCE.createELayerAspect();
					elayer.setName("Layer");
					
					new EditLayerAspectFrame(elayer, frame, false); 
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
					new EditLayerAspectFrame(layers.get(index), frame, true);
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
					ELayerAspect lay =layers.remove(index);
					EcoreUtil.delete(lay);
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
						ELayerAspect lay = layers.remove(index);
						layers.add(index - 1, lay);
						layerViewer.removeAll();
						for (ELayerAspect la : layers) {
							layerViewer.add(la.getName());
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
						
						ELayerAspect lay = layers.remove(index);
						layers.add(index + 1, lay);
						layerViewer.removeAll();
						for (ELayerAspect la : layers) {
							layerViewer.add(la.getName());
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

	public org.eclipse.swt.widgets.List getLayerViewer() {
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

	
	private void modifyLayerOrder() {
		EAspect aspect = ((EAspect) eobject);
		for (ELayerAspect lay: aspect.getLayers()) {
			if (! layers.contains(lay)) {
				EcoreUtil.delete((EObject) lay, true);
			}
		}	
		aspect.getLayers().clear();
		aspect.getLayers().addAll(layers);
	}

	@Override
	protected void save() {
		 TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(eobject);
			if (domain != null) {
			    domain.getCommandStack().execute(new RecordingCommand(domain) {
			    	     public void doExecute() {
			    	    	 eobject.setName(textName.getText());
			    	    	 modifyLayerOrder();
			    	     }
			    	  });
			}
	    ef.hasDoneChanges = true;  
		
	}
	
	
	
}

