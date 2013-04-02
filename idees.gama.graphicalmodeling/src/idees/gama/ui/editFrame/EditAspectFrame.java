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
		//****** CANVAS NAME *********
		Canvas canvasName = canvasName(container);
		canvasName.setBounds(10, 10, 720, 30);
		
		//****** CANVAS LAYERS *********
		Canvas canvasLayers = canvasLayers(container);
		canvasLayers.setBounds(10, 50, 720, 275);
				

		//****** CANVAS OK/CANCEL *********
		Canvas canvasOkCancel = canvasOkCancel(container);
		canvasOkCancel.setBounds(10, 335, 720, 30);
		
		return container;
	}
	
	protected Canvas canvasLayers(Composite container) {
		
		//****** CANVAS LAYERS *********
		Canvas canvasLayers = new Canvas(container, SWT.BORDER);
		canvasLayers.setBounds(10, 515, 720, 275);
				
		layerViewer = new org.eclipse.swt.widgets.List(canvasLayers, SWT.BORDER | SWT.V_SCROLL);
		
		for (ELayerAspect lay : layers) {
			layerViewer.add(lay.getName());
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
					
					new EditLayerAspectFrame(elayer, frame, false); 
				} 
		});
		
		Button editLayerBtn = new Button(canvasLayers, SWT.BUTTON1);
		editLayerBtn.setBounds(200, 245, 105, 20);
		editLayerBtn.setText("Edit");
		editLayerBtn.setSelection(true);
		editLayerBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (layerViewer.getSelectionCount() == 1) {
					final int index = layerViewer.getSelectionIndex();
					new EditLayerAspectFrame(layers.get(index), frame, true);
				}
			}
		});
		
		Button removeLayerBtn = new Button(canvasLayers, SWT.BUTTON1);
		removeLayerBtn.setBounds(320, 245, 105, 20);
		removeLayerBtn.setText("Remove");
		removeLayerBtn.setSelection(true);
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
		Button btnUp = new Button(canvasLayers, SWT.ARROW | SWT.UP);
		btnUp.setBounds(440, 245, 105, 20);
		//btnUp.setText("Up");
		btnUp.setSelection(true);
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
		Button btnDown = new Button(canvasLayers, SWT.ARROW | SWT.DOWN);
		btnDown.setBounds(560, 245, 105, 20);
		//btnDown.setText("Down");
		btnDown.setSelection(true);
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
		return canvasLayers;
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

