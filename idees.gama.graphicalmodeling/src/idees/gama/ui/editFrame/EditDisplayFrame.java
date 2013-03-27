package idees.gama.ui.editFrame;

import gama.EAspect;
import gama.EDisplay;
import gama.EGamaObject;
import gama.EGridTopology; 
import gama.ELayer;
import gama.ELayerAspect;
import gama.ESpecies;
import idees.gama.features.edit.EditFeature;

import java.util.ArrayList;
import java.util.List;

import msi.gama.util.GamaList;

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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class EditDisplayFrame extends EditFrame {

	StyledText gamlCode;
	org.eclipse.swt.widgets.List layerViewer;
	List<String> layerStrs;
	EditDisplayFrame frame;
	List<ELayer> layers;
	Text textColor;
	Text textRefresh;

	List<ESpecies> species;
	List<ESpecies> grids;

	Color color;

	/**
	 * Create the application window.
	 */
	public EditDisplayFrame(Diagram diagram, IFeatureProvider fp,
			EditFeature eaf, EGamaObject display, String name) {
		super(diagram, fp, eaf, display, name == null ? "Aspect definition"
				: name);
		species = new GamaList<ESpecies>();
		grids = new GamaList<ESpecies>();
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
		layers = new GamaList<ELayer>();
	}

	/**
	 * Create contents of the application window.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		layerStrs = new GamaList<String>();
		// ****** CANVAS NAME *********
		Canvas canvasName = canvasName(container);
		canvasName.setBounds(10, 10, 720, 30);

		// ****** CANVAS LAYERS *********
		Canvas canvasLayers = canvasLayers(container);
		canvasLayers.setBounds(10, 50, 720, 275);

		// ****** CANVAS PARAMETERS *********
		Canvas canvasParam = buildCanvasParam(container);
		canvasParam.setBounds(10, 335, 720, 100);

		// ****** CANVAS VALIDATION *********
		Canvas canvasValidation = canvasValidation(container);
		canvasValidation.setBounds(10, 445, 720, 95);
		return container;
	}

	protected Canvas canvasLayers(Composite container) {

		// ****** CANVAS LAYERS *********
		Canvas canvasLayers = new Canvas(container, SWT.BORDER);
		canvasLayers.setBounds(10, 515, 720, 275);

		layerViewer = new org.eclipse.swt.widgets.List(canvasLayers, SWT.BORDER
				| SWT.V_SCROLL);

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
				ELayer elayer = gama.GamaFactory.eINSTANCE.createELayer();
				elayer.setName("Layer");
				new EditLayerFrame(elayer, frame, species, grids);
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
					TransactionalEditingDomain domain = TransactionUtil
							.getEditingDomain(eobject);
					if (domain != null) {
						domain.getCommandStack().execute(
								new RecordingCommand(domain) {
									public void doExecute() {
										((EAspect) eobject).getLayerList()
												.remove(index);
										List<ELayerAspect> layers = new ArrayList<ELayerAspect>();
										for (ELayerAspect lay : layers) {
											if (((EAspect) eobject)
													.getLayerList().contains(
															lay.getName())) {
												layers.add(lay);
											}
										}
										((EAspect) eobject).getLayers().clear();
										((EAspect) eobject).getLayers().addAll(
												layers);
									}
								});
					}
					ef.hasDoneChanges = true;
				}
			}
		});
		Button btnUp = new Button(canvasLayers, SWT.ARROW | SWT.UP);
		btnUp.setBounds(320, 245, 105, 20);
		// btnUp.setText("Up");
		btnUp.setSelection(true);
		btnUp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (layerViewer.getSelectionCount() == 1) {
					String el = layerViewer.getSelection()[0];
					int index = layerViewer.getSelectionIndex();
					if (index > 0) {
						layerStrs.remove(el);
						layerStrs.add(index - 1, el);
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
		// btnDown.setText("Down");
		btnDown.setSelection(true);
		btnDown.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (layerViewer.getSelectionCount() == 1) {
					String el = layerViewer.getSelection()[0];
					int index = layerViewer.getSelectionIndex();
					if (index < layerViewer.getItemCount() - 1) {
						layerStrs.remove(el);
						layerStrs.add(index + 1, el);
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

	public Canvas buildCanvasParam(Composite container) {
		// ****** CANVAS PARAMETERS *********

		Canvas canvasParam = new Canvas(container, SWT.BORDER);
		canvasParam.setBounds(10, 50, 720, 100);

		// COLOR
		CLabel lblColor = new CLabel(canvasParam, SWT.NONE);
		lblColor.setBounds(10, 10, 60, 20);
		lblColor.setText("Color");

		textColor = new Text(canvasParam, SWT.BORDER);
		textColor.setBounds(425, 10, 250, 18);
		textColor.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				// modifyLocation(textColor.getText());
			}
		});

		// Start with Celtics green
		color = frame.getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE);

		// Use a label full of spaces to show the color
		final Label colorLabel = new Label(canvasParam, SWT.NONE);
		colorLabel.setText("    ");
		colorLabel.setBackground(color);
		colorLabel.setBounds(160, 10, 50, 18);

		Button button = new Button(canvasParam, SWT.PUSH);
		button.setText("Color...");
		button.setBounds(220, 10, 80, 20);
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
				RGB rgb = dlg.open();
				if (rgb != null) {
					// Dispose the old color, create the
					// new one, and set into the label
					color.dispose();
					color = new Color(frame.getShell().getDisplay(), rgb);
					colorLabel.setBackground(color);
				}
			}
		});
		Composite cColor = new Composite(canvasParam, SWT.NONE);
		cColor.setBounds(80, 10, 400, 18);

		Button btnCstCol = new Button(cColor, SWT.RADIO);
		btnCstCol.setBounds(0, 0, 80, 18);
		btnCstCol.setText("Constant");
		btnCstCol.setSelection(true);
		btnCstCol.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				textColor.setEnabled(false);
				// locStr = "random";
				// modifyLocation("any_location_in(world.shape)");
			}
		});

		Button btnExpressionCol = new Button(cColor, SWT.RADIO);
		btnExpressionCol.setBounds(260, 0, 85, 18);
		btnExpressionCol.setText("Expression:");
		btnExpressionCol.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textColor.setEnabled(true);
				// modifyLocation(textColor.getText());
			}
		});

		// REFRESH
		CLabel lblRefresh = new CLabel(canvasParam, SWT.NONE);
		lblRefresh.setBounds(10, 40, 60, 18);
		lblRefresh.setText("Refresh");

		textRefresh = new Text(canvasParam, SWT.BORDER);
		textRefresh.setText("1");
		textRefresh.setBounds(80, 40, 250, 18);
		textRefresh.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				TransactionalEditingDomain domain = TransactionUtil
						.getEditingDomain(eobject);
				if (domain != null) {
					domain.getCommandStack().execute(
							new RecordingCommand(domain) {
								public void doExecute() {
									((EDisplay) eobject).setRefresh(textRefresh.getText());
								}
							});
				}
				ef.hasDoneChanges = true;
			}
		});

		// OPENGL
		CLabel lblType = new CLabel(canvasParam, SWT.NONE);
		lblType.setBounds(10, 70, 60, 18);
		lblType.setText("Type");

		Composite cOpenGl = new Composite(canvasParam, SWT.NONE);
		cOpenGl.setBounds(80, 70, 400, 18);

		Button btnJava2D = new Button(cOpenGl, SWT.RADIO);
		btnJava2D.setBounds(0, 0, 85, 18);
		btnJava2D.setText("Java 2D");
		btnJava2D.setSelection(true);
		btnJava2D.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				TransactionalEditingDomain domain = TransactionUtil
						.getEditingDomain(eobject);
				if (domain != null) {
					domain.getCommandStack().execute(
							new RecordingCommand(domain) {
								public void doExecute() {
									((EDisplay) eobject).setOpengl(false);
								}
							});
				}
				ef.hasDoneChanges = true;
			}
		});

		Button btnOpenGL = new Button(cOpenGl, SWT.RADIO);
		btnOpenGL.setBounds(120, 0, 85, 18);
		btnOpenGL.setText("Open GL");
		btnOpenGL.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TransactionalEditingDomain domain = TransactionUtil
						.getEditingDomain(eobject);
				if (domain != null) {
					domain.getCommandStack().execute(
							new RecordingCommand(domain) {
								public void doExecute() {
									((EDisplay) eobject).setOpengl(true);
								}
							});
				}
				ef.hasDoneChanges = true;
			}
		});
		return canvasParam;

	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(743, 595);
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

	private void modifyLayerOrder() {
		TransactionalEditingDomain domain = TransactionUtil
				.getEditingDomain(eobject);
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
