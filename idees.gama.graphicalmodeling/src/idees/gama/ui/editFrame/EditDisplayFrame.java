package idees.gama.ui.editFrame;

import gama.EDisplay;
import gama.EGamaObject;
import gama.EGridTopology; 
import gama.ELayer;
import gama.ESpecies;
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
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class EditDisplayFrame extends EditFrame {

	org.eclipse.swt.widgets.List layerViewer;
	EditDisplayFrame frame;
	List<ELayer> layers;
	
	List<ESpecies> species;
	List<ESpecies> grids;
	Text textColor;
	Text textRefresh;

	Button btnCstCol;
	Button btnExpressionCol;
	Color color;
	int[] rgb;
	Label colorLabel;
	Button btnOpenGL;
	Button btnJava2D;

	Diagram diagram;
	/**
	 * Create the application window.
	 */
	public EditDisplayFrame(Diagram diagram, IFeatureProvider fp,
			EditFeature eaf, EGamaObject display, String name) {
		super(diagram, fp, eaf, display, name == null ? "Display definition"
				: name);
		species = new GamaList<ESpecies>();
		grids = new GamaList<ESpecies>();
		layers = new GamaList<ELayer>();
		this.diagram = diagram;
		rgb = new int[3];
		rgb[0] = rgb[1] = rgb[2] = 255;
		List<Shape> contents = diagram.getChildren();
		for (Shape sh : contents) {
			Object obj = fp.getBusinessObjectForPictogramElement(sh);
			if (obj instanceof ESpecies)  {
				ESpecies spe = (ESpecies) obj;
				if (spe.getTopology() != null && spe.getTopology() instanceof EGridTopology) {
					grids.add((ESpecies) obj);
				} else  { 
					species.add((ESpecies) obj);
				}
			}
		}
		frame = this;
		
	}
	
	private void loadData() {
		EDisplay display = (EDisplay) eobject;
		layers.addAll(display.getLayers());
		for (ELayer la : layers) {
			layerViewer.add(la.getName());
		}
		if (display.getIsColorCst()!= null) {
			btnCstCol.setSelection(display.getIsColorCst());
			btnExpressionCol.setSelection(!display.getIsColorCst());
		}
		if (display.getName()!= null)
			textName.setText(display.getName());
		if (display.getColor()!= null)
			textColor.setText(display.getColor());
		if (btnCstCol.getSelection()) {
			textColor.setEnabled(false);
		}
		System.out.println("display.getColorRBG(): " + display.getColorRBG());
		if (display.getColorRBG().size() == 3) {
			rgb[0] = display.getColorRBG().get(0);
			rgb[1] = display.getColorRBG().get(1);
			rgb[2] = display.getColorRBG().get(2);
			
			color.dispose();
	         color = new Color(frame.getShell().getDisplay(), new RGB(rgb[0], rgb[1],rgb[2]));
	         colorLabel.setBackground(color);
		}
		System.out.println("isplay.getOpengl(): " + display.getOpengl());
		if (display.getOpengl()!= null) {
			btnOpenGL.setSelection(display.getOpengl());
			btnJava2D.setSelection(!display.getOpengl());
		}
		if (display.getRefresh()!= null)
			textRefresh.setText(display.getRefresh());
		System.out.println("AFTER LOADING display: " + display);
		((ValidateText)textName).setSaveData(true);
		((ValidateText)textRefresh).setSaveData(true);
		((ValidateText) textName).getLinkedVts().add((ValidateText) textRefresh);
		((ValidateText)textColor).setSaveData(true);
		((ValidateText) textName).getLinkedVts().add((ValidateText) textColor);

	}


	/**
	 * Create contents of the application window.
	 * 
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
		
		buildCanvasParam(container);
				
		//****** CANVAS OK/CANCEL *********
		//groupOkCancel(container);
		loadData();
		
		return container;
	}

	protected void groupLayers(Composite container) {

		//****** CANVAS LAYERS *********
		//****** CANVAS LAYERS *********
				Group group = new Group(container, SWT.NONE);
				
				group.setLayout( new FillLayout(SWT.VERTICAL));
			    group.setText("Display layers");
			    
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
				for (ELayer lay : layers) {
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
				addLayerBtn.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						final ELayer elayer = gama.GamaFactory.eINSTANCE.createELayer();
						TransactionalEditingDomain domain = TransactionUtil
									.getEditingDomain(eobject);
							if (domain != null) {
								domain.getCommandStack().execute(new RecordingCommand(domain) {
									public void doExecute() {
										elayer.setName("Layer");
										diagram.eResource().getContents().add(elayer);
										
									}
								});
							}
							ef.hasDoneChanges = true;
							new EditLayerFrame(elayer, frame, species, grids, false, diagram); 
						} 
				});
				
				Button editLayerBtn = new Button(containerButtons, SWT.BUTTON1);
				editLayerBtn.setText("Edit");
				editLayerBtn.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (layerViewer.getSelectionCount() == 1) {
							final int index = layerViewer.getSelectionIndex();
							new EditLayerFrame(layers.get(index), frame, species, grids, true, diagram); 
						}
					}
				});
				
				Button removeLayerBtn = new Button(containerButtons, SWT.BUTTON1);
				removeLayerBtn.setText("Remove");
				removeLayerBtn.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (layerViewer.getSelectionCount() == 1) {
							final int index = layerViewer.getSelectionIndex();
							layerViewer.remove(index);
							final ELayer lay =layers.remove(index);
							TransactionalEditingDomain domain = TransactionUtil
									.getEditingDomain(eobject);
							if (domain != null) {
								domain.getCommandStack().execute(new RecordingCommand(domain) {
									public void doExecute() {
										diagram.eResource().getContents().remove(lay);
										EcoreUtil.delete(lay);
									}
								});
							}
							ef.hasDoneChanges = true;
							
						}
					}
				});
				Button btnUp = new Button(containerButtons, SWT.ARROW | SWT.UP);
				btnUp.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (layerViewer.getSelectionCount() == 1) {
							int index = layerViewer.getSelectionIndex();
							if (index > 0) {
								ELayer lay = layers.remove(index);
								layers.add(index - 1, lay);
								layerViewer.removeAll();
								for (ELayer la : layers) {
									layerViewer.add(la.getName());
								}
								
							}	
						}
					}
				});
				Button btnDown = new Button(containerButtons, SWT.ARROW | SWT.DOWN);
				btnDown.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (layerViewer.getSelectionCount() == 1) {
							int index = layerViewer.getSelectionIndex();
							if (index < layerViewer.getItemCount() - 1) {
								
								ELayer lay = layers.remove(index);
								layers.add(index + 1, lay);
								layerViewer.removeAll();
								for (ELayer la : layers) {
									layerViewer.add(la.getName());
								}
							}	
						}
					}
				});
			}

	public void buildCanvasParam(Composite container) {
		// ****** CANVAS PARAMETERS *********
		Group group = new Group(container, SWT.NONE);
		final GamaDiagramEditor diagramEditor = ((GamaDiagramEditor)fp.getDiagramTypeProvider().getDiagramEditor());
        
		group.setLayout( new GridLayout(1, false));
	    group.setText("Display properties");
	    GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		group.setLayoutData(gridData);
	   
	    Composite containerColor = new Composite(group, SWT.NONE);
	    containerColor.setLayout(new GridLayout(6, false));
		 GridData gridData3 = new GridData();
		 gridData3.horizontalAlignment = SWT.FILL;
		 gridData3.grabExcessHorizontalSpace = true;
		 containerColor.setLayoutData(gridData3);

		// COLOR
		CLabel lblColor = new CLabel(containerColor, SWT.NONE);
		lblColor.setText("Background color:");

		btnCstCol = new Button(containerColor, SWT.RADIO);
		btnCstCol.setText("Constant");
		btnCstCol.setSelection(true);
		btnCstCol.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				textColor.setEnabled(false);
				if (((ValidateText)textColor).isSaveData()) {
					save("");
					 ModelGenerator.modelValidation(fp, diagram);
					 diagramEditor.updateEObjectErrors();
				}
			}
		});
 
		// Start with white

		rgb[0] = 255;
		rgb[1] = 255;
		rgb[2] = 255;
		
		color = new Color(frame.getShell().getDisplay(), new RGB(rgb[0], rgb[1], rgb[2]));

		// Use a label full of spaces to show the color
		colorLabel = new Label(containerColor, SWT.NONE);
		colorLabel.setText("                  ");
		colorLabel.setBackground(color);

		Button button = new Button(containerColor, SWT.PUSH);
		button.setText("Color...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				// Create the color-change dialog
				ColorDialog dlg = new ColorDialog(frame.getShell());

				// Set the selected color in the dialog from
				// user's selected color
				dlg.setRGB(colorLabel.getBackground().getRGB());

				// Change the title bar text
				dlg.setText("Choose a Color");

				// Open the dialog and retrieve the selected color
				RGB rgbL = dlg.open();
				if (rgbL != null) {
					rgb[0] = rgbL.red;
					rgb[1] = rgbL.green;
					rgb[2] = rgbL.blue;
					save("");
					 ModelGenerator.modelValidation(fp, diagram);
					 diagramEditor.updateEObjectErrors();
				
					
					// Dispose the old color, create the
					// new one, and set into the label
					color.dispose();
					color = new Color(frame.getShell().getDisplay(), rgbL);
					colorLabel.setBackground(color);
				}
			}
		});
		
		
		btnExpressionCol = new Button(containerColor, SWT.RADIO);
		btnExpressionCol.setText("Expression:");
		btnExpressionCol.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textColor.setEnabled(true);
				if (((ValidateText)textColor).isSaveData()) {
					save("");
					 ModelGenerator.modelValidation(fp, diagram);
					 diagramEditor.updateEObjectErrors();
				}
			}
		});

		//textColor = new Text(containerColor, SWT.BORDER);
		textColor = new ValidateText(containerColor, SWT.BORDER,diagram, fp,frame, diagramEditor, "background:", null, null);
     	
		GridData gridDataTC = new GridData();
		gridDataTC.horizontalAlignment = SWT.FILL;
		gridDataTC.grabExcessHorizontalSpace = true;
		textColor.setLayoutData(gridDataTC);
	
		// REFRESH
		Composite containerRefresh = new Composite(group, SWT.NONE);
		GridData gridDataRR = new GridData();
		gridDataRR.horizontalAlignment = SWT.FILL;
		gridDataRR.grabExcessHorizontalSpace = true;
		containerRefresh.setLayoutData(gridDataRR);
		containerRefresh.setLayout(new GridLayout(2, false));
		
		CLabel lblRefresh = new CLabel(containerRefresh, SWT.NONE);
		lblRefresh.setText("Refresh:");

		//textRefresh = new Text(containerRefresh, SWT.BORDER);
		textRefresh = new ValidateText(containerRefresh, SWT.BORDER,diagram, fp,frame, diagramEditor, "refresh_every:", null, null);
     	
		textRefresh.setText("1");
		 GridData gridDataR = new GridData();
		 gridDataR.horizontalAlignment = SWT.FILL;
		 gridDataR.grabExcessHorizontalSpace = true;
		 textRefresh.setLayoutData(gridDataR);
			
		// OPENGL
		Composite containerType = new Composite(group, SWT.NONE);
		GridData gridDataT = new GridData();
		gridDataT.horizontalAlignment = SWT.FILL;
		gridDataT.grabExcessHorizontalSpace = true;
		containerType.setLayoutData(gridDataT);
		containerType.setLayout(new GridLayout(3, false));
		
		CLabel lblType = new CLabel(containerType, SWT.NONE);
		lblType.setText("Type:");

		Composite cOpenGl = new Composite(containerType, SWT.NONE);
		 GridData gridDataOp = new GridData();
		 gridDataOp.horizontalAlignment = SWT.FILL;
		 gridDataOp.grabExcessHorizontalSpace = true;
		 cOpenGl.setLayoutData(gridDataOp);
		 cOpenGl.setLayout(new GridLayout(2, false));
		
		btnJava2D = new Button(cOpenGl, SWT.RADIO);
		btnJava2D.setText("Java 2D");
		btnJava2D.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (((ValidateText)textColor).isSaveData()) {
					save("");
					 ModelGenerator.modelValidation(fp, diagram);
					 diagramEditor.updateEObjectErrors();
				}
			}
		});

		btnOpenGL = new Button(cOpenGl, SWT.RADIO);
		btnOpenGL.setText("Open GL");
		btnOpenGL.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (((ValidateText)textColor).isSaveData()) {
					save("");
					 ModelGenerator.modelValidation(fp, diagram);
					 diagramEditor.updateEObjectErrors();
				}
			}
		});
		
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(743, 545);
	}

	public List<ELayer> getLayers() {
		return layers;
	}

	public void setLayers(List<ELayer> layers) {
		this.layers = layers;
	}

	public org.eclipse.swt.widgets.List getLayerViewer() {
		return layerViewer;
	}
	
	private void modifyOtherProperties() {
		EDisplay display = (EDisplay) eobject;
		display.setName(textName.getText());
		display.setIsColorCst(btnCstCol.getSelection());
		display.setColor(textColor.getText());
		display.getColorRBG().clear();
		display.getColorRBG().add(rgb[0]);
		display.getColorRBG().add(rgb[1]);
		display.getColorRBG().add(rgb[2]);
		display.setOpengl(btnOpenGL.getSelection());
		display.setRefresh(textRefresh.getText());
		System.out.println("eDisplay : " + eobject);
		
		/*String model = "display " + display.getName() ;
    	String refresh = ((display.getRefresh() == null)|| (display.getRefresh().isEmpty())|| display.getRefresh().equals("1")) ? "" : " refresh_every: " + display.getRefresh();
    	String type = (display.getOpengl() != null && display.getOpengl()) ? " type: opengl" : "";
    	String background = "";
    	if (rgb[0] != 255 || rgb[1] != 255 ||rgb[2] != 255 || !display.getIsColorCst()) {
    		if (display.getIsColorCst()) {
        		background += " background: rgb(" + display.getColorRBG() + ")" ;
    		} else {
    			background += " background: " + display.getColor();
    		}
    	}
    	
    	model +=  background + refresh + type+" {";
    	System.out.println("model: " + model);
    	display.setGamlCode(model);*/
    	
	}

	private void modifyLayerOrder() {
		EDisplay display = ((EDisplay) eobject);
		for (ELayer lay: display.getLayers()) {
			if (! layers.contains(lay)) {
				EcoreUtil.delete((EObject) lay, true);
			}
		}	
		display.getLayers().clear();
		display.getLayers().addAll(layers);
		display.getLayerList().clear();
		display.getLayerList().addAll(new GamaList<String>(layerViewer.getItems()));
	}

	@Override
	protected void save(final String name) {
		System.out.println("******* SAVE ******");
		TransactionalEditingDomain domain = TransactionUtil
				.getEditingDomain(eobject);
		if (domain != null) {
			domain.getCommandStack().execute(new RecordingCommand(domain) {
				public void doExecute() {
					if (name.equals("name")) {
	    	    		 eobject.setName(textName.getText());
	    	    	 } else {
	    	    		 modifyLayerOrder();
	    	    		 modifyOtherProperties();
	    	    	 }
				}
			});
		}
		ef.hasDoneChanges = true;
		System.out.println("******* RESULT ******");
		
		
	}
	
	protected void clean() {
		EDisplay display = ((EDisplay) eobject);
		for (ELayer lay: layers) {
			if (! display.getLayers().contains(lay)) {
				EcoreUtil.delete((EObject) lay, true);
			}
		}	
		layers.clear();
	}

}
