package idees.gama.ui.editFrame;

import gama.*;
import idees.gama.diagram.GamaDiagramEditor;
import idees.gama.features.edit.EditFeature;
import idees.gama.features.modelgeneration.ModelGenerator;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import msi.gama.util.TOrderedHashMap;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.*;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class EditDisplayFrame extends EditFrame {

	Table layerViewer;
	EditDisplayFrame frame;

	List<ESpecies> species;
	List<ESpecies> grids;
	ValidateText textColor;
	ValidateText textRefresh;

	Button btnCstCol;
	Button btnExpressionCol;
	Color color;
	int[] rgb;
	Label colorLabel;
	Button btnOpenGL;
	Button btnJava2D;

	Diagram diagram;
	final Map<ELayer, EditLayerFrame> layerFrames;

	/**
	 * Create the application window.
	 */
	public EditDisplayFrame(final Diagram diagram, final IFeatureProvider fp, final EditFeature eaf,
		final EGamaObject display, final String name) {
		super(diagram, fp, eaf, display, name == null ? "Display definition" : name);
		layerFrames = new TOrderedHashMap<ELayer, EditLayerFrame>();
		species = new ArrayList<ESpecies>();
		grids = new ArrayList<ESpecies>();
		this.diagram = diagram;
		rgb = new int[3];
		rgb[0] = rgb[1] = rgb[2] = 255;
		List<Shape> contents = diagram.getChildren();
		for ( Shape sh : contents ) {
			Object obj = fp.getBusinessObjectForPictogramElement(sh);
			if ( obj instanceof ESpecies ) {
				ESpecies spe = (ESpecies) obj;
				if ( spe.getTopology() != null && spe.getTopology() instanceof EGridTopology ) {
					grids.add((ESpecies) obj);
				} else {
					species.add((ESpecies) obj);
				}
			}
		}
		frame = this;

		updateLayerId();
		ModelGenerator.modelValidation(fp, diagram);

	}

	private static final Pattern DOUBLE_PATTERN = Pattern
		.compile("[\\x00-\\x20]*[+-]?(NaN|Infinity|((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)"
			+ "([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|"
			+ "(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))"
			+ "[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*");

	private void loadData() {
		EDisplay display = (EDisplay) eobject;
		for ( ELayer la : display.getLayers() ) {
			// layerViewer.add(la.getName());
			TableItem ti = new TableItem(layerViewer, SWT.NONE);
			ti.setText(la.getName());
			// ti.setBackground(new Color(frame.getShell().getDisplay(), 100,255,100));
			ti.setBackground(hasError(la) ? new Color(frame.getShell().getDisplay(), 255, 100, 100) : new Color(frame
				.getShell().getDisplay(), 100, 255, 100));

		}

		if ( display.getIsColorCst() != null ) {
			btnCstCol.setSelection(display.getIsColorCst());
			btnExpressionCol.setSelection(!display.getIsColorCst());
		}
		if ( display.getName() != null ) {
			textName.setText(display.getName());
		}
		if ( display.getColor() != null ) {
			textColor.setText(display.getColor());
		}
		if ( btnCstCol.getSelection() ) {
			textColor.setEnabled(false);
		}
		if ( display.getColorRBG().size() == 3 ) {
			rgb[0] = display.getColorRBG().get(0);
			rgb[1] = display.getColorRBG().get(1);
			rgb[2] = display.getColorRBG().get(2);

			color.dispose();
			color = new Color(frame.getShell().getDisplay(), new RGB(rgb[0], rgb[1], rgb[2]));
			colorLabel.setBackground(color);
		}
		if ( display.getOpengl() != null ) {
			btnOpenGL.setSelection(display.getOpengl());
			btnJava2D.setSelection(!display.getOpengl());
		}
		if ( display.getRefresh() != null ) {
			textRefresh.setText(display.getRefresh());
		}
		textName.setSaveData(true);
		textRefresh.setSaveData(true);
		textName.getLinkedVts().add(textRefresh);
		textColor.setSaveData(true);
		textName.getLinkedVts().add(textColor);

	}

	/**
	 * Create contents of the application window.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createContents(final Composite parent) {

		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		// ****** CANVAS NAME *********
		groupName(container);

		// ****** CANVAS LAYERS *********
		groupLayers(container);

		buildCanvasParam(container);

		// ****** CANVAS OK/CANCEL *********
		loadData();

		return container;
	}

	protected void groupLayers(final Composite container) {

		// ****** CANVAS LAYERS *********
		// ****** CANVAS LAYERS *********
		Group group = new Group(container, SWT.NONE);
		final GamaDiagramEditor diagramEditor = (GamaDiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();

		group.setLayout(new FillLayout(SWT.VERTICAL));
		group.setText("Display layers");

		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		group.setLayoutData(gridData);
		group.setLayout(new GridLayout(1, false));

		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = SWT.FILL;
		gridData2.verticalAlignment = SWT.FILL;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.grabExcessVerticalSpace = true;

		// layerViewer = new org.eclipse.swt.widgets.List(group, SWT.BORDER | SWT.V_SCROLL);
		layerViewer = new Table(group, SWT.BORDER | SWT.V_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION
		/* | SWT.HIDE_SELECTION */);
		layerViewer.setHeaderVisible(false);
		layerViewer.setLinesVisible(false);

		layerViewer.setLayoutData(gridData2);

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
			public void widgetSelected(final SelectionEvent e) {
				final ELayer elayer = gama.GamaFactory.eINSTANCE.createELayer();
				TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(eobject);
				if ( domain != null ) {
					domain.getCommandStack().execute(new RecordingCommand(domain) {

						@Override
						public void doExecute() {
							elayer.setName("Layer");
							elayer.setDisplay((EDisplay) eobject);
							diagram.eResource().getContents().add(elayer);
							TableItem ti = new TableItem(layerViewer, SWT.NONE);
							ti.setText(elayer.getName());
							ti.setBackground(hasError(elayer) ? new Color(frame.getShell().getDisplay(), 255, 100, 100)
								: new Color(frame.getShell().getDisplay(), 100, 255, 100));

							((EDisplay) eobject).getLayers().add(elayer);

						}
					});
				}

				ef.hasDoneChanges = true;

				EditLayerFrame eaf = new EditLayerFrame(elayer, frame, species, grids, false, diagram, fp, ef);
				layerFrames.put(elayer, eaf);
				eaf.open();
				frame.updateLayerId();
			}
		});

		Button editLayerBtn = new Button(containerButtons, SWT.BUTTON1);
		editLayerBtn.setText("Edit");
		editLayerBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if ( layerViewer.getSelectionCount() == 1 ) {
					updateLayerId();

					final int index = layerViewer.getSelectionIndex();
					ELayer layer = ((EDisplay) eobject).getLayers().get(index);
					EditLayerFrame eaf = layerFrames.get(layer);
					if ( eaf == null || eaf.getShell() == null || eaf.getShell().isDisposed() ) {
						eaf = new EditLayerFrame(layer, frame, species, grids, true, diagram, fp, ef);
						eaf.open();
						layerFrames.put(layer, eaf);

					} else {
						eaf.getShell().setFocus();
					}

				}
			}
		});

		Button removeLayerBtn = new Button(containerButtons, SWT.BUTTON1);
		removeLayerBtn.setText("Remove");
		removeLayerBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if ( layerViewer.getSelectionCount() == 1 ) {
					final int index = layerViewer.getSelectionIndex();
					layerViewer.remove(index);
					TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(eobject);
					if ( domain != null ) {
						domain.getCommandStack().execute(new RecordingCommand(domain) {

							@Override
							public void doExecute() {
								final ELayer lay = ((EDisplay) eobject).getLayers().remove(index);
								layerFrames.remove(lay);
								diagram.eResource().getContents().remove(lay);
								EcoreUtil.delete(lay);
							}
						});
					}
					frame.updateLayerId();

					save("");
					ModelGenerator.modelValidation(fp, diagram);
					diagramEditor.updateEObjectErrors();
					ef.hasDoneChanges = true;

				}
			}
		});
		Button btnUp = new Button(containerButtons, SWT.ARROW | SWT.UP);
		btnUp.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if ( layerViewer.getSelectionCount() == 1 ) {
					int index = layerViewer.getSelectionIndex();
					if ( index > 0 ) {
						((EDisplay) eobject).getLayers().move(index - 1, index);
						layerViewer.removeAll();
						for ( ELayer la : ((EDisplay) eobject).getLayers() ) {
							TableItem ti = new TableItem(layerViewer, SWT.NONE);
							ti.setText(la.getName());
							ti.setBackground(hasError(la) ? new Color(frame.getShell().getDisplay(), 255, 100, 100)
								: new Color(frame.getShell().getDisplay(), 100, 255, 100));
						}
					}
				}
			}
		});
		Button btnDown = new Button(containerButtons, SWT.ARROW | SWT.DOWN);
		btnDown.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if ( layerViewer.getSelectionCount() == 1 ) {
					int index = layerViewer.getSelectionIndex();
					if ( index < layerViewer.getItemCount() - 1 ) {
						((EDisplay) eobject).getLayers().move(index + 1, index);
						layerViewer.removeAll();
						for ( ELayer la : ((EDisplay) eobject).getLayers() ) {
							TableItem ti = new TableItem(layerViewer, SWT.NONE);
							ti.setText(la.getName());
							ti.setBackground(hasError(la) ? new Color(frame.getShell().getDisplay(), 255, 100, 100)
								: new Color(frame.getShell().getDisplay(), 100, 255, 100));
						}
					}
				}
			}
		});
	}

	public void updateLayerId() {
		final GamaDiagramEditor diagramEditor = (GamaDiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();
		List<String> loc = new ArrayList<String>();
		diagramEditor.buildLocation(eobject, loc);
		int size = loc.size();
		List<List<String>> ids = new ArrayList<List<String>>(diagramEditor.getIdsEObjects().keySet());
		for ( List<String> lid : ids ) {
			if ( lid.size() > size && lid.get(lid.size() - 2).equals(eobject.getName()) ) {
				diagramEditor.getIdsEObjects().remove(lid);
			}
		}
		for ( ELayer layer : ((EDisplay) eobject).getLayers() ) {
			if ( layer.getType() == null ) {
				continue;
			}
			List<String> key = new ArrayList<String>(loc);
			key.add(layer.getType());
			diagramEditor.getIdsEObjects().put(key, eobject);
		}
	}

	public void buildCanvasParam(final Composite container) {
		// ****** CANVAS PARAMETERS *********
		Group group = new Group(container, SWT.NONE);
		final GamaDiagramEditor diagramEditor = (GamaDiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();

		group.setLayout(new GridLayout(1, false));
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

			@Override
			public void widgetSelected(final SelectionEvent e) {
				textColor.setEnabled(false);
				if ( textColor.isSaveData() ) {
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

			@Override
			public void widgetSelected(final SelectionEvent event) {
				// Create the color-change dialog
				ColorDialog dlg = new ColorDialog(frame.getShell());

				// Set the selected color in the dialog from
				// user's selected color
				dlg.setRGB(colorLabel.getBackground().getRGB());

				// Change the title bar text
				dlg.setText("Choose a Color");

				// Open the dialog and retrieve the selected color
				RGB rgbL = dlg.open();
				if ( rgbL != null ) {
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
			public void widgetSelected(final SelectionEvent e) {
				textColor.setEnabled(true);
				if ( textColor.isSaveData() ) {
					save("");
					ModelGenerator.modelValidation(fp, diagram);
					diagramEditor.updateEObjectErrors();
				}
			}
		});

		// textColor = new Text(containerColor, SWT.BORDER);
		textColor =
			new ValidateText(containerColor, SWT.BORDER, diagram, fp, frame, diagramEditor, "background:", null, null);

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

		// textRefresh = new Text(containerRefresh, SWT.BORDER);
		textRefresh =
			new ValidateText(containerRefresh, SWT.BORDER, diagram, fp, frame, diagramEditor, "refresh_every:", null,
				null);

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
		btnJava2D.setSelection(true);
		btnJava2D.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if ( textColor.isSaveData() ) {
					save("");
					ModelGenerator.modelValidation(fp, diagram);
					diagramEditor.updateEObjectErrors();
				}
			}
		});

		btnOpenGL = new Button(cOpenGl, SWT.RADIO);
		btnOpenGL.setText("Open GL");
		btnOpenGL.setSelection(false);
		btnOpenGL.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if ( textColor.isSaveData() ) {
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

	public Table getLayerViewer() {
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
	}

	@Override
	protected void save(final String name) {
		TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(eobject);
		if ( domain != null ) {
			domain.getCommandStack().execute(new RecordingCommand(domain) {

				@Override
				public void doExecute() {
					if ( name.equals("name") ) {
						eobject.setName(textName.getText());
					} else {
						// modifyLayerOrder();
						modifyOtherProperties();
					}
				}
			});
		}
		ef.hasDoneChanges = true;

	}

	public void updateLayer() {
		layerViewer.removeAll();
		for ( ELayer elayer : ((EDisplay) eobject).getLayers() ) {
			TableItem ti = new TableItem(layerViewer, SWT.NONE);
			ti.setText(elayer.getName());
			ti.setBackground(hasError(elayer) ? new Color(frame.getShell().getDisplay(), 255, 100, 100) : new Color(
				frame.getShell().getDisplay(), 100, 255, 100));

		}
	}

	public boolean testBasicOk(final ELayer lay, final List<String> speciesStr) {
		return (lay.getType() == null || lay.getType().equals("species") && speciesStr.contains(lay.getSpecies())) &&
			isNumber(lay.getTransparency()) && isNumber(lay.getPosition_x()) && isNumber(lay.getPosition_y()) &&
			isNumber(lay.getSize_x()) && isNumber(lay.getSize_y()) &&
			!ModelGenerator.hasSyntaxError(fp, lay.getName(), true);
	}

	public boolean hasError(final ELayer elayer) {
		final GamaDiagramEditor diagramEditor = (GamaDiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();
		List<String> speciesStr = new ArrayList<String>();
		for ( ESpecies sp : species ) {
			speciesStr.add(sp.getName());
		}
		List<String> ids = new ArrayList<String>();
		boolean basicOk = testBasicOk(elayer, speciesStr);
		if ( basicOk ) { return false; }
		diagramEditor.buildLocation(elayer, ids);

		if ( diagramEditor.getErrorsLoc().isEmpty() && diagramEditor.getSyntaxErrorsLoc().isEmpty() ) { return false; }

		return diagramEditor.getErrorsLoc().containsKey(ids) || diagramEditor.getSyntaxErrorsLoc().containsKey(ids);
	}

	public List<ESpecies> getGrids() {
		return grids;
	}

	public static boolean isNumber(final String s) {
		return s == null || s.isEmpty() || DOUBLE_PATTERN.matcher(s).matches();
	}
}
