package idees.gama.ui.editFrame;

import gama.EAspect;
import gama.EGamaObject;
import gama.ELayerAspect;
import idees.gama.features.edit.EditFeature;

import java.util.ArrayList;
import java.util.List;

import msi.gama.util.GamaList;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class EditAspectFrame extends EditFrame {
	
	StyledText gamlCode;
	org.eclipse.swt.widgets.List layerViewer;
	List<String> layerStrs;
	EditAspectFrame frame;
	List<ELayerAspect> layers;
	/**
	 * Create the application window.
	 */
	public EditAspectFrame(Diagram diagram, IFeatureProvider fp, EditFeature eaf, EGamaObject aspect, String name) {	
		super(diagram, fp, eaf,  aspect, name == null ? "Aspect definition" : name );
		frame = this;
		layers = new GamaList<ELayerAspect>();
	}

	/**
	 * Create contents of the application window.
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		layerStrs = new GamaList<String>();
		//****** CANVAS NAME *********
		Canvas canvasName = canvasName(container);
		canvasName.setBounds(10, 10, 720, 30);
		
		//****** CANVAS LAYERS *********
		Canvas canvasLayers = canvasLayers(container);
		canvasLayers.setBounds(10, 50, 720, 275);
				
				
		//****** CANVAS VALIDATION *********
		Canvas canvasValidation = canvasValidation(container);
		canvasValidation.setBounds(10, 335, 720, 95);
		return container;
	}
	
	protected Canvas canvasLayers(Composite container) {
		
		//****** CANVAS LAYERS *********
		Canvas canvasLayers = new Canvas(container, SWT.BORDER);
		canvasLayers.setBounds(10, 515, 720, 275);
				
		layerViewer = new org.eclipse.swt.widgets.List(canvasLayers, SWT.BORDER | SWT.V_SCROLL);
		
		for (String lay : layerStrs) {
			layerViewer.add(lay);
		}
		
		layerViewer.setBounds(5, 30, 700, 200);
		CLabel lblReflexOrder = new CLabel(canvasLayers, SWT.NONE);
		lblReflexOrder.setBounds(5, 5, 100, 20);
		lblReflexOrder.setText("Layers");
		
		Button addLayerBtn = new Button(canvasLayers, SWT.BUTTON1);
		addLayerBtn.setBounds(80, 245, 105, 20);
		addLayerBtn.setText("Add");
		addLayerBtn.setSelection(true);
		addLayerBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
					ELayerAspect elayer = gama.GamaFactory.eINSTANCE.createELayerAspect();
					elayer.setName("Layer");
					new EditLayerAspectFrame(elayer, frame); 
				} 
		});
		Button removeLayerBtn = new Button(canvasLayers, SWT.BUTTON1);
		removeLayerBtn.setBounds(200, 245, 105, 20);
		removeLayerBtn.setText("Remove");
		removeLayerBtn.setSelection(true);
		removeLayerBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (layerViewer.getSelectionCount() == 1) {
					String el = layerViewer.getSelection()[0];
					final int index = layerViewer.getSelectionIndex();
					layerStrs.remove(el);
					layerViewer.remove(index);
					TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(eobject);
					if (domain != null) {
						 domain.getCommandStack().execute(new RecordingCommand(domain) {
		            	     public void doExecute() {
		            	    	((EAspect) eobject).getLayerList().remove(index);
		            	    	List<ELayerAspect> layers= new ArrayList<ELayerAspect>();
		            	    	for (ELayerAspect lay : layers) {
		            	    		if (((EAspect) eobject).getLayerList().contains(lay.getName())) {
		            	    			 layers.add(lay);
		            	    		}
		            	    	}
		            	    	((EAspect) eobject).getLayers().clear();
		            	    	((EAspect) eobject).getLayers().addAll(layers);
		            	     }
		            	  });
					}
					ef.hasDoneChanges = true;
				}
			}
		});
		Button btnUp = new Button(canvasLayers, SWT.ARROW | SWT.UP);
		btnUp.setBounds(320, 245, 105, 20);
		//btnUp.setText("Up");
		btnUp.setSelection(true);
		btnUp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (layerViewer.getSelectionCount() == 1) {
					String el = layerViewer.getSelection()[0];
					int index = layerViewer.getSelectionIndex();
					if (index > 0) {
						layerStrs.remove(el);
						layerStrs.add( index - 1, el);
						layerViewer.removeAll();
						for (String ref : layerStrs) {
							layerViewer.add(ref);
						}
						modifyLayerOrder();
					}	
				}
			}
		});
		Button btnDown = new Button(canvasLayers, SWT.ARROW | SWT.DOWN);
		btnDown.setBounds(440, 245, 105, 20);
		//btnDown.setText("Down");
		btnDown.setSelection(true);
		btnDown.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (layerViewer.getSelectionCount() == 1) {
					String el = layerViewer.getSelection()[0];
					int index = layerViewer.getSelectionIndex();
					if (index < layerViewer.getItemCount() - 1) {
						layerStrs.remove(el);
						layerStrs.add( index + 1, el);
						layerViewer.removeAll();
						for (String ref : layerStrs) {
							layerViewer.add(ref);
						}
						modifyLayerOrder();
					}	
				}
			}
		});
		return canvasLayers;
	}
	
	
	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(743, 475);
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
	
	private void modifyLayerOrder() {
		 TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(eobject);
			if (domain != null) {
			    domain.getCommandStack().execute(new RecordingCommand(domain) {
			    	     public void doExecute() {
			    	 		((EAspect) eobject).getLayerList().clear();
			    	 		((EAspect) eobject).getLayerList().addAll(layerStrs);
			    	     }
			    	  });
			}
	    ef.hasDoneChanges = true;  
	}
	
	
	
}

