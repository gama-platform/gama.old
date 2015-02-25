package idees.gama.ui.editFrame;

import gama.*;
import idees.gama.diagram.GamaDiagramEditor;
import idees.gama.features.edit.EditFeature;
import idees.gama.features.modelgeneration.ModelGenerator;
import java.util.*;
import java.util.List;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.util.GamaList;
import msi.gaml.types.Types;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.*;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

public class EditLayerFrame extends EditFrame {

	// Types
	private CCombo comboType;
	private final String[] type_shape = { "species", "grid", "agents", "image", "text", "chart" };
	private String[] species_list;
	private String[] grid_list;
	private Map<String, String[]> aspectsSpecies;
	private final String[] styles_layer = { IKeyword.LINE, IKeyword.WHISKER, IKeyword.AREA, IKeyword.BAR, IKeyword.DOT,
		IKeyword.STEP, IKeyword.SPLINE, IKeyword.STACK, IKeyword.THREE_D, IKeyword.RING, IKeyword.EXPLODED };
	private final String[] types_chart = { IKeyword.SERIES, IKeyword.HISTOGRAM, IKeyword.PIE, IKeyword.BOX_WHISKER,
		IKeyword.XY };

	private String[] aspects;
	EditLayerFrame layerFrame;

	private ValidateText textX;
	private ValidateText textY;
	private ValidateText positionX;
	private ValidateText positionY;
	private ValidateText textPath;
	private ValidateText textText;
	private ValidateText textSizeText;
	private ValidateText textAgents;

	private ValidateText transparency;
	private ValidateText textRefresh;

	Composite speciesComp;
	Composite chartComp;
	Composite gridComp;
	Composite agentsComp;
	Composite textComp;
	Composite imageComp;
	Composite shapeComp;

	private CCombo comboSpecies;
	private CCombo comboAspectsSpecies;
	private CCombo comboAspectsAgents;
	private CCombo comboGrid;
	private CCombo comboTypeChart;
	boolean ok = false;
	boolean edit;

	private Table table_chart_layers;

	EditDisplayFrame frame;
	EditLayerFrame cFrame;
	Color color;
	int[] rgb;

	Button btnShowLines;

	ValidateText textColorGrid;
	Button btnCstColGrid;
	Button btnExpressionGrid;
	Label colorLabelGrid;
	ValidateText textColorText;
	Button btnCstColText;
	Button btnExpressionText;
	Label colorLabelText;
	ValidateText textColorImage;
	Button btnCstColImage;
	Button btnExpressionImage;
	Label colorLabelImage;
	ValidateText textColorChart;
	Button btnExpressionChart;
	Button btnCstColChart;
	Label colorLabelChart;
	List<ESpecies> species;
	List<ESpecies> grids;
	Diagram diagram;

	public EditLayerFrame(final Diagram diagram, final IFeatureProvider fp, final EditFeature ef,
		final EGamaObject eobject, final String name) {
		super(diagram, fp, ef, eobject, name);
	}

	public EditLayerFrame(final ELayer elayer, final EditDisplayFrame asp, final List<ESpecies> species,
		final List<ESpecies> grids, final boolean edit, final Diagram diagram, final IFeatureProvider fp,
		final EditFeature ef) {
		super(diagram, fp, ef, elayer, "Edit Layer");
		frame = asp;
		cFrame = this;
		layerFrame = this;
		this.species = species;
		this.grids = grids;
		updateSpeciesAspect();
		rgb = new int[3];
		rgb[0] = rgb[1] = rgb[2] = 255;

		// init(elayer, asp, species, grids);
		this.diagram = diagram;
		this.edit = edit;

	}

	public void updateSpeciesAspect() {
		aspectsSpecies = new Hashtable<String, String[]>();
		species_list = new String[species.size()];
		List<String> aspectsL = new ArrayList<String>();
		for ( int i = 0; i < species_list.length; i++ ) {
			ESpecies sp = species.get(i);
			List<String> aspL = new ArrayList(aspectSpecies(aspectsL, sp));
			if ( aspL.isEmpty() ) {
				aspL.add("default");
			}
			aspectsSpecies.put(sp.getName(), aspL.toArray(new String[aspL.size()]));
			species_list[i] = sp.getName();
		}
		aspects = aspectsL.toArray(new String[aspectsL.size()]);
		grid_list = new String[grids.size()];
		for ( int i = 0; i < grid_list.length; i++ ) {
			grid_list[i] = grids.get(i).getName();
		}
	}

	private void loadData() {
		ELayer elayer = (ELayer) eobject;
		if ( elayer.getType() != null ) {
			comboType.setText(elayer.getType());
		}
		if ( elayer.getSize_x() != null ) {
			textX.setText(elayer.getSize_x());
		}

		if ( elayer.getSize_y() != null ) {
			textY.setText(elayer.getSize_y());
		}
		if ( elayer.getPosition_x() != null ) {
			positionX.setText(elayer.getPosition_x());
		}
		if ( elayer.getPosition_y() != null ) {
			positionY.setText(elayer.getPosition_y());
		}
		if ( elayer.getFile() != null ) {
			textPath.setText(elayer.getFile());
		}
		if ( elayer.getRefresh() != null ) {
			textRefresh.setText(((ELayer) eobject).getRefresh());
		}
		if ( elayer.getTransparency() != null ) {
			transparency.setText(elayer.getTransparency());
		}
		if ( elayer.getText() != null ) {
			textText.setText(elayer.getText());
		}
		if ( elayer.getSize() != null ) {
			textSizeText.setText(elayer.getSize());
		}
		if ( elayer.getAgents() != null ) {
			textAgents.setText(elayer.getAgents());
		}
		if ( elayer.getSpecies() != null ) {
			List<String> ln = Arrays.asList(species_list);
			if ( ln.contains(elayer.getSpecies()) ) {
				comboSpecies.setText(elayer.getSpecies());
			} else {
				comboSpecies.setText("world");
			}
		}
		if ( elayer.getGrid() != null ) {
			comboGrid.setText(elayer.getGrid());
		}
		if ( elayer.getAspect() != null ) {
			List<String> la = Arrays.asList(aspectsSpecies.get(comboSpecies.getText()));
			if ( la.contains(elayer.getAspect()) ) {
				comboAspectsSpecies.setText(elayer.getAspect());
			} else {
				comboAspectsSpecies.setText("");
			}
			List<String> la2 = Arrays.asList(aspects);
			if ( la2.contains(elayer.getAspect()) ) {
				comboAspectsAgents.setText(elayer.getAspect());
			}
		}
		if ( elayer.getColor() != null ) {
			this.textColorChart.setText(elayer.getColor());
			this.textColorGrid.setText(elayer.getColor());
			this.textColorText.setText(elayer.getColor());
			this.textColorImage.setText(elayer.getColor());
		}
		if ( elayer.getIsColorCst() != null ) {
			boolean selected = elayer.getIsColorCst();
			this.btnCstColChart.setSelection(selected);
			this.btnCstColImage.setSelection(selected);
			this.btnCstColText.setSelection(selected);
			this.btnCstColGrid.setSelection(selected);
			this.textColorChart.setEnabled(!selected);
			this.textColorImage.setEnabled(!selected);
			this.textColorText.setEnabled(!selected);
			this.textColorGrid.setEnabled(!selected);
			this.btnExpressionChart.setSelection(!selected);
			this.btnExpressionImage.setSelection(!selected);
			this.btnExpressionText.setSelection(!selected);
			this.btnExpressionGrid.setSelection(!selected);
		}
		if ( elayer.getColorRBG() != null && elayer.getColorRBG().size() == 3 ) {
			rgb[0] = elayer.getColorRBG().get(0);
			rgb[1] = elayer.getColorRBG().get(1);
			rgb[2] = elayer.getColorRBG().get(2);
			color = new Color(frame.getShell().getDisplay(), new RGB(rgb[0], rgb[1], rgb[2]));
			colorLabelGrid.setBackground(color);
			colorLabelText.setBackground(color);
			colorLabelImage.setBackground(color);
			colorLabelChart.setBackground(color);

		}
		if ( elayer.getChart_type() != null ) {
			comboTypeChart.setText(elayer.getChart_type());
		}

		initTable();
		frame.updateLayerId();

	}

	public void buildColorComposite(final Composite compositeColor, final ValidateText textColor,
		final Label colorLabel, final Button button, final Button btnCstCol, final Button btnExpressionCol,
		final String text) {
		// COLOR
		final GamaDiagramEditor diagramEditor = (GamaDiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();
		compositeColor.setSize(700, 20);

		// COLOR
		CLabel lblColor = new CLabel(compositeColor, SWT.NONE);
		lblColor.setText("Color:");
		lblColor.setBounds(0, 0, 110, 20);
		textColor.setEnabled(false);
		button.setText("Color...");
		button.setBounds(250, 0, 80, 20);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				// Create the color-change dialog
				ColorDialog dlg = new ColorDialog(cFrame.getShell());

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

		btnCstCol.setText("Constant");
		btnCstCol.setSelection(true);
		btnCstCol.setBounds(0, 0, 85, 18);
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

		color = new Color(frame.getShell().getDisplay(), new RGB(rgb[0], rgb[1], rgb[2]));

		// Use a label full of spaces to show the color
		colorLabel.setText("                  ");
		colorLabel.setBackground(color);
		colorLabel.setBounds(190, 0, 50, 18);

		btnExpressionCol.setText("Expression:");
		btnExpressionCol.setBounds(260, 0, 85, 18);
		btnExpressionCol.setSelection(true);
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
		textColor.setBounds(465, 0, 200, 18);

	}

	private Set<String> aspectSpecies(final List<String> aspectsL, final ESpecies sp) {
		Set<String> aspL = new HashSet<String>();
		for ( EAspectLink al : sp.getAspectLinks() ) {
			String apN = al.getAspect().getName();
			aspL.add(apN);
			if ( !aspectsL.contains(apN) ) {
				aspectsL.add(apN);
			}
		}
		if ( sp.getInheritsFrom() != null ) {
			aspL.addAll(aspectSpecies(aspectsL, sp.getInheritsFrom()));
		}
		return aspL;
	}

	@Override
	protected Control createContents(final Composite parent) {

		Composite comp = new Composite(parent, SWT.NONE);
		comp.setBounds(0, 0, 740, 390);

		canvasName(comp, false);
		buildCanvasTopo(comp);

		Canvas canvas = canvasProperties(comp);
		canvas.setLocation(10, 310);
		comp.pack();
		if ( edit ) {
			loadData();
			updateVisible();
		}

		textX.setSaveData(true);
		textY.setSaveData(true);
		positionX.setSaveData(true);
		positionY.setSaveData(true);
		textPath.setSaveData(true);
		textText.setSaveData(true);
		textSizeText.setSaveData(true);
		textAgents.setSaveData(true);
		transparency.setSaveData(true);
		textRefresh.setSaveData(true);

		textColorGrid.setSaveData(true);
		textColorText.setSaveData(true);
		textColorChart.setSaveData(true);
		textColorImage.setSaveData(true);

		if ( !edit ) {
			updateError();
			save("");
		}

		return comp;
	}

	@Override
	protected void save(final String name) {
		final ELayer elayer = (ELayer) eobject;
		TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(eobject);
		if ( domain != null ) {
			domain.getCommandStack().execute(new RecordingCommand(domain) {

				@Override
				public void doExecute() {
					modifyChartLayers();
					elayer.setType(comboType.getText());
					elayer.setName(textName.getText());
					elayer.setSize_x(textX.getText());
					elayer.setSize_y(textY.getText());
					elayer.setPosition_x(positionX.getText());
					elayer.setPosition_y(positionY.getText());

					elayer.setRefresh(textRefresh.getText());
					elayer.setTransparency(transparency.getText());
					elayer.setFile(textPath.getText());
					elayer.setText(textText.getText());
					elayer.setSize(textSizeText.getText());
					elayer.setAgents(textAgents.getText());
					elayer.setSpecies(comboSpecies.getText());
					elayer.setGrid(comboGrid.getText());
					elayer.setAspect(comboType.getText().equals("species") ? comboAspectsSpecies.getText()
						: comboAspectsAgents.getText());
					elayer.setChart_type(comboTypeChart.getText());
					elayer.getColorRBG().clear();
					elayer.getColorRBG().add(rgb[0]);
					elayer.getColorRBG().add(rgb[1]);
					elayer.getColorRBG().add(rgb[2]);
					elayer.setShowLines(btnShowLines.getSelection());
					if ( elayer.getType().equals("image") ) {
						elayer.setIsColorCst(btnCstColImage.getSelection());
						// if (btnCstColImage.getSelection())
						// elayer.setColor("rgb(" + rgb[0] + "," + rgb[1] + "," + rgb[2] +")");
						// else
						elayer.setColor(textColorImage.getText());
					} else if ( elayer.getType().equals("text") ) {
						elayer.setIsColorCst(btnCstColText.getSelection());
						// if (btnCstColText.getSelection())
						// elayer.setColor("rgb(" + rgb[0] + "," + rgb[1] + "," + rgb[2] +")");
						// else
						elayer.setColor(textColorText.getText());
					} else if ( elayer.getType().equals("chart") ) {
						elayer.setIsColorCst(btnCstColChart.getSelection());
						// if (btnCstColChart.getSelection())
						// elayer.setColor("rgb(" + rgb[0] + "," + rgb[1] + "," + rgb[2] +")");
						// else
						elayer.setColor(textColorChart.getText());
					} else if ( elayer.getType().equals("grid") ) {
						elayer.setIsColorCst(btnCstColGrid.getSelection());
						// if (btnCstColGrid.getSelection())
						// elayer.setColor("rgb(" + rgb[0] + "," + rgb[1] + "," + rgb[2] +")");
						// else
						elayer.setColor(textColorGrid.getText());
					}

				}
			});
		}
	}

	@Override
	public void updateError() {
		frame.updateLayerId();
		frame.updateLayer();
	}

	private void modifyChartLayers() {
		for ( EChartLayer cl : ((ELayer) eobject).getChartlayers() ) {
			diagram.eResource().getContents().remove(cl);
			EcoreUtil.delete(cl);
		}
		((ELayer) eobject).getChartlayers().clear();

		if ( table_chart_layers != null ) {
			for ( final TableItem item : table_chart_layers.getItems() ) {
				final EChartLayer var = gama.GamaFactory.eINSTANCE.createEChartLayer();
				diagram.eResource().getContents().add(var);
				var.setName(item.getText(0));
				var.setStyle(item.getText(1));
				var.setColor(item.getText(2));
				var.setValue(item.getText(3));
				((ELayer) eobject).getChartlayers().add(var);
			}
		}

	}

	public Canvas canvasProperties(final Composite container) {
		final GamaDiagramEditor diagramEditor = (GamaDiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();
		Canvas canvasProp = new Canvas(container, SWT.BORDER);
		canvasProp.setSize(720, 130);
		CLabel lblPosition = new CLabel(canvasProp, SWT.NONE);
		lblPosition.setBounds(10, 10, 90, 20);
		lblPosition.setText("Position");

		CLabel lblPositionX = new CLabel(canvasProp, SWT.NONE);
		lblPositionX.setBounds(100, 10, 100, 20);
		lblPositionX.setText("X ([0.0,1.0])");

		positionX = new ValidateText(canvasProp, SWT.BORDER, diagram, fp, this, diagramEditor, "position:", null, null);
		positionX.setBounds(200, 10, 100, 20);
		positionX.setText("0.0");

		CLabel lblPositionY = new CLabel(canvasProp, SWT.NONE);
		lblPositionY.setBounds(400, 10, 100, 20);
		lblPositionY.setText("Y ([0.0,1.0])");

		positionY = new ValidateText(canvasProp, SWT.BORDER, diagram, fp, this, diagramEditor, "position:", null, null);
		positionY.setBounds(500, 10, 100, 20);
		positionY.setText("0.0");

		CLabel lblSize = new CLabel(canvasProp, SWT.NONE);
		lblSize.setBounds(10, 40, 90, 20);
		lblSize.setText("Size");

		CLabel lblSizeX = new CLabel(canvasProp, SWT.NONE);
		lblSizeX.setBounds(100, 40, 100, 20);
		lblSizeX.setText("width ([0.0,1.0])");

		textX = new ValidateText(canvasProp, SWT.BORDER, diagram, fp, this, diagramEditor, "size:", null, null);
		textX.setBounds(200, 40, 100, 20);
		textX.setText("1.0");

		CLabel lblSizeY = new CLabel(canvasProp, SWT.NONE);
		lblSizeY.setBounds(400, 40, 100, 20);
		lblSizeY.setText("height ([0.0,1.0])");

		textY = new ValidateText(canvasProp, SWT.BORDER, diagram, fp, this, diagramEditor, "size:", null, null);
		textY.setBounds(500, 40, 100, 20);
		textY.setText("1.0");

		CLabel lblTransp = new CLabel(canvasProp, SWT.NONE);
		lblTransp.setBounds(10, 70, 90, 20);
		lblTransp.setText("Transparency");

		transparency =
			new ValidateText(canvasProp, SWT.BORDER, diagram, fp, this, diagramEditor, "transparency:", null, null);
		transparency.setBounds(100, 70, 200, 20);
		transparency.setText("0.0");

		CLabel lblRefresh = new CLabel(canvasProp, SWT.NONE);
		lblRefresh.setBounds(10, 100, 90, 20);
		lblRefresh.setText("Refresh");

		textRefresh =
			new ValidateText(canvasProp, SWT.BORDER, diagram, fp, this, diagramEditor, "refresh:", null, null);
		textRefresh.setBounds(100, 100, 200, 20);
		textRefresh.setText("true");

		return canvasProp;
	}

	private void updateVisible() {
		String val = comboType.getText();
		int size = textX.getLoc().size() - 1;
		textX.getLoc().remove(size);
		textX.getLoc().add(val);
		textY.getLoc().remove(size);
		textY.getLoc().add(val);
		positionX.getLoc().remove(size);
		positionX.getLoc().add(val);
		positionY.getLoc().remove(size);
		positionY.getLoc().add(val);
		textPath.getLoc().remove(size);
		textPath.getLoc().add(val);
		textText.getLoc().remove(size);
		textText.getLoc().add(val);
		textSizeText.getLoc().remove(size);
		textSizeText.getLoc().add(val);
		textAgents.getLoc().remove(size);
		textAgents.getLoc().add(val);
		transparency.getLoc().remove(size);
		transparency.getLoc().add(val);
		textColorChart.getLoc().remove(size);
		textColorChart.getLoc().add(val);
		textColorText.getLoc().remove(size);
		textColorText.getLoc().add(val);
		textColorGrid.getLoc().remove(size);
		textColorGrid.getLoc().add(val);
		textColorImage.getLoc().remove(size);
		textColorImage.getLoc().add(val);

		if ( val.equals("species") ) {
			speciesComp.setVisible(true);
			speciesComp.setEnabled(true);
			imageComp.setVisible(false);
			imageComp.setEnabled(false);
			textComp.setEnabled(false);
			textComp.setVisible(false);
			gridComp.setEnabled(false);
			gridComp.setVisible(false);
			agentsComp.setEnabled(false);
			agentsComp.setVisible(false);
			chartComp.setEnabled(false);
			chartComp.setVisible(false);
		} else if ( val.equals("grid") ) {
			speciesComp.setVisible(false);
			speciesComp.setEnabled(false);
			imageComp.setVisible(false);
			imageComp.setEnabled(false);
			textComp.setEnabled(false);
			textComp.setVisible(false);
			gridComp.setEnabled(true);
			gridComp.setVisible(true);
			agentsComp.setEnabled(false);
			agentsComp.setVisible(false);
			chartComp.setEnabled(false);
			chartComp.setVisible(false);
		} else if ( val.equals("agents") ) {
			speciesComp.setVisible(false);
			speciesComp.setEnabled(false);
			imageComp.setVisible(false);
			imageComp.setEnabled(false);
			textComp.setEnabled(false);
			textComp.setVisible(false);
			gridComp.setEnabled(false);
			gridComp.setVisible(false);
			agentsComp.setEnabled(true);
			agentsComp.setVisible(true);
			chartComp.setEnabled(false);
			chartComp.setVisible(false);
		} else if ( val.equals("image") ) {
			speciesComp.setVisible(false);
			speciesComp.setEnabled(false);
			imageComp.setVisible(true);
			imageComp.setEnabled(true);
			textComp.setEnabled(false);
			textComp.setVisible(false);
			gridComp.setEnabled(false);
			gridComp.setVisible(false);
			agentsComp.setEnabled(false);
			agentsComp.setVisible(false);
			chartComp.setEnabled(false);
			chartComp.setVisible(false);
		} else if ( val.equals("text") ) {
			speciesComp.setVisible(false);
			speciesComp.setEnabled(false);
			imageComp.setVisible(false);
			imageComp.setEnabled(false);
			textComp.setEnabled(true);
			textComp.setVisible(true);
			gridComp.setEnabled(false);
			gridComp.setVisible(false);
			agentsComp.setEnabled(false);
			agentsComp.setVisible(false);
			chartComp.setEnabled(false);
			chartComp.setVisible(false);
		} else if ( val.equals("chart") ) {
			speciesComp.setVisible(false);
			speciesComp.setEnabled(false);
			imageComp.setVisible(false);
			imageComp.setEnabled(false);
			textComp.setEnabled(false);
			textComp.setVisible(false);
			gridComp.setEnabled(false);
			gridComp.setVisible(false);
			agentsComp.setEnabled(false);
			agentsComp.setVisible(false);
			chartComp.setEnabled(true);
			chartComp.setVisible(true);
		}
		shapeComp.pack();

	}

	public void buildCanvasTopo(final Composite container) {
		// ****** CANVAS TYPE *********
		final GamaDiagramEditor diagramEditor = (GamaDiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();

		Canvas canvasTopo = new Canvas(container, SWT.BORDER);
		canvasTopo.setBounds(10, 50, 720, 250);

		// Shape
		shapeComp = new Composite(canvasTopo, SWT.BORDER);
		shapeComp.setBounds(10, 5, 700, 190);
		CLabel lblShape = new CLabel(shapeComp, SWT.NONE);
		lblShape.setBounds(5, 5, 50, 20);
		lblShape.setText("Type");

		comboType = new CCombo(shapeComp, SWT.BORDER);
		comboType.setBounds(60, 5, 300, 20);
		if ( !frame.getGrids().isEmpty() ) {
			comboType.setItems(type_shape);
		} else {
			String[] type_shape2 = { "species", "agents", "image", "text", "chart" };
			comboType.setItems(type_shape2);
		}
		comboType.setText("species");
		// "point", "polyline", "polygon", "circle", "square", "rectangle",
		// "hexagon", "sphere", "expression"
		comboType.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				updateVisible();
				if ( textX.isSaveData() ) {
					save("");
				}
				frame.updateLayerId();
				ModelGenerator.modelValidation(fp, diagram);
				diagramEditor.updateEObjectErrors();
			}

		});

		// Species
		speciesComp = new Composite(shapeComp, SWT.NONE);
		speciesComp.setVisible(true);
		speciesComp.setEnabled(true);
		speciesComp.setBounds(20, 40, 680, 180);
		CLabel lblSpecies = new CLabel(speciesComp, SWT.NONE);
		lblSpecies.setBounds(0, 0, 60, 20);
		lblSpecies.setText("Species");

		comboSpecies = new CCombo(speciesComp, SWT.BORDER);
		comboSpecies.setItems(species_list);
		comboSpecies.setBounds(70, 0, 300, 20);
		if ( species_list.length > 0 ) {
			comboSpecies.setText(species_list[0]);
		}
		comboSpecies.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent event) {
				comboAspectsSpecies.setItems(aspectsSpecies.get(comboSpecies.getText()));
				comboAspectsSpecies.setText(aspectsSpecies.get(comboSpecies.getText())[0]);
				if ( textX.isSaveData() ) {
					save("");
				}

				ModelGenerator.modelValidation(fp, diagram);
				diagramEditor.updateEObjectErrors();
			}
		});

		CLabel lblAspect = new CLabel(speciesComp, SWT.NONE);
		lblAspect.setBounds(0, 30, 60, 20);
		lblAspect.setText("Aspect");

		comboAspectsSpecies = new CCombo(speciesComp, SWT.BORDER);
		comboAspectsSpecies.setEditable(false);
		comboAspectsSpecies.setItems(aspectsSpecies.get(comboSpecies.getText()));
		comboAspectsSpecies.setBounds(70, 30, 300, 20);
		if ( species_list.length > 0 ) {
			comboAspectsSpecies.setText(aspectsSpecies.get(comboSpecies.getText())[0]);
		}

		comboAspectsSpecies.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent event) {
				if ( textX.isSaveData() ) {
					save("");
				}

				ModelGenerator.modelValidation(fp, diagram);
				diagramEditor.updateEObjectErrors();
			}
		});
		// Grid
		gridComp = new Composite(shapeComp, SWT.NONE);
		gridComp.setVisible(false);
		gridComp.setEnabled(false);
		gridComp.setBounds(20, 40, 670, 190);
		CLabel lblGrid = new CLabel(gridComp, SWT.NONE);
		lblGrid.setBounds(0, 0, 60, 20);
		lblGrid.setText("grid");

		comboGrid = new CCombo(gridComp, SWT.BORDER);
		comboGrid.setItems(grid_list);
		comboGrid.setBounds(70, 0, 300, 20);
		if ( grid_list.length > 0 ) {
			comboGrid.setText(grid_list[0]);
		}

		CLabel lblSL = new CLabel(gridComp, SWT.NONE);
		lblSL.setBounds(0, 40, 90, 20);
		lblSL.setText("Show Lines");
		btnShowLines = new Button(gridComp, SWT.CHECK);
		btnShowLines.setBounds(100, 40, 20, 20);
		btnShowLines.setSelection(((ELayer) eobject).isShowLines());
		btnShowLines.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if ( textX.isSaveData() ) {
					save("");
				}

				ModelGenerator.modelValidation(fp, diagram);
				diagramEditor.updateEObjectErrors();

			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				if ( textX.isSaveData() ) {
					save("");
				}

				ModelGenerator.modelValidation(fp, diagram);
				diagramEditor.updateEObjectErrors();

			}
		});
		Composite ccg = new Composite(gridComp, SWT.NONE);
		textColorGrid = new ValidateText(ccg, SWT.NONE, diagram, fp, this, diagramEditor, "color:", null, null);
		colorLabelGrid = new Label(ccg, SWT.NONE);
		Button button1 = new Button(ccg, SWT.PUSH);

		Composite cColor1 = new Composite(ccg, SWT.NONE);
		cColor1.setBounds(110, 0, 400, 18);
		btnCstColGrid = new Button(cColor1, SWT.RADIO);
		btnExpressionGrid = new Button(cColor1, SWT.RADIO);

		buildColorComposite(ccg, textColorGrid, colorLabelGrid, button1, btnCstColGrid, btnExpressionGrid, "line color");
		ccg.setLocation(0, 70);

		// Image
		imageComp = new Composite(shapeComp, SWT.NONE);
		imageComp.setBounds(20, 40, 680, 180);
		imageComp.setVisible(false);
		imageComp.setEnabled(false);
		CLabel lblPath = new CLabel(imageComp, SWT.NONE);
		lblPath.setBounds(0, 0, 60, 20);
		lblPath.setText("Path");

		textPath = new ValidateText(imageComp, SWT.BORDER, diagram, fp, this, diagramEditor, "file:", null, null);
		textPath.setString(true);
		textPath.setBounds(70, 0, 300, 20);
		textPath.setText("../images/background.png");

		Composite cci = new Composite(imageComp, SWT.NONE);
		textColorImage = new ValidateText(cci, SWT.NONE, diagram, fp, this, diagramEditor, "color:", null, null);
		colorLabelImage = new Label(cci, SWT.NONE);
		Button button2 = new Button(cci, SWT.PUSH);
		Composite cColor2 = new Composite(cci, SWT.NONE);
		cColor2.setBounds(110, 0, 400, 18);
		btnCstColImage = new Button(cColor2, SWT.RADIO);
		btnExpressionImage = new Button(cColor2, SWT.RADIO);
		buildColorComposite(cci, textColorImage, colorLabelImage, button2, btnCstColImage, btnExpressionImage, "color");
		cci.setLocation(0, 30);

		// Text
		textComp = new Composite(shapeComp, SWT.NONE);
		textComp.setBounds(20, 40, 680, 180);
		textComp.setVisible(false);
		textComp.setEnabled(false);
		CLabel lbltext = new CLabel(textComp, SWT.NONE);
		lbltext.setBounds(0, 0, 60, 20);
		lbltext.setText("Text");

		textText = new ValidateText(textComp, SWT.BORDER, diagram, fp, this, diagramEditor, "text:", null, null);
		textText.setBounds(70, 0, 300, 20);
		textText.setString(true);
		textText.setText("");

		CLabel lblSizeTxt = new CLabel(textComp, SWT.NONE);
		lblSizeTxt.setBounds(0, 30, 60, 20);
		lblSizeTxt.setText("Size");

		textSizeText = new ValidateText(textComp, SWT.BORDER, diagram, fp, this, diagramEditor, "size:", null, null);
		textSizeText.setBounds(70, 30, 300, 20);
		textSizeText.setText("1.0");

		Composite cct = new Composite(textComp, SWT.NONE);
		textColorText = new ValidateText(cct, SWT.NONE, diagram, fp, this, diagramEditor, "color:", null, null);;
		colorLabelText = new Label(cct, SWT.NONE);
		Button button3 = new Button(cct, SWT.PUSH);
		Composite cColor3 = new Composite(cct, SWT.NONE);
		cColor3.setBounds(110, 0, 400, 18);
		btnCstColText = new Button(cColor3, SWT.RADIO);
		btnExpressionText = new Button(cColor3, SWT.RADIO);
		buildColorComposite(cct, textColorText, colorLabelText, button3, btnCstColText, btnExpressionText, "color");
		cct.setLocation(0, 60);

		// Agents
		agentsComp = new Composite(shapeComp, SWT.NONE);
		agentsComp.setVisible(false);
		agentsComp.setEnabled(false);
		agentsComp.setBounds(20, 40, 680, 180);
		CLabel lblAgents = new CLabel(agentsComp, SWT.NONE);
		lblAgents.setBounds(0, 0, 60, 20);
		lblAgents.setText("agents");

		textAgents = new ValidateText(agentsComp, SWT.BORDER, diagram, fp, this, diagramEditor, "value:", null, null);
		textAgents.setBounds(70, 0, 300, 20);
		textAgents.setText("[]");

		CLabel lblAspectA = new CLabel(agentsComp, SWT.NONE);
		lblAspectA.setBounds(0, 30, 60, 20);
		lblAspectA.setText("Aspect");

		comboAspectsAgents = new CCombo(agentsComp, SWT.BORDER);
		comboAspectsAgents.setEditable(false);
		comboAspectsAgents.setItems(aspects);
		comboAspectsAgents.setBounds(70, 30, 300, 20);
		if ( aspects.length > 0 ) {
			comboAspectsAgents.setText(aspects[0]);
		}
		comboAspectsAgents.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent event) {
				if ( textX.isSaveData() ) {
					save("");
				}

				ModelGenerator.modelValidation(fp, diagram);
				diagramEditor.updateEObjectErrors();
			}
		});

		// Chart
		chartComp = new Composite(shapeComp, SWT.NONE);
		chartComp.setVisible(false);
		chartComp.setEnabled(false);
		chartComp.setBounds(20, 40, 680, 180);

		Composite ccc = new Composite(chartComp, SWT.NONE);
		textColorChart = new ValidateText(ccc, SWT.NONE, diagram, fp, this, diagramEditor, "background:", null, null);
		btnCstColChart = new Button(ccc, SWT.BORDER);
		colorLabelChart = new Label(ccc, SWT.NONE);
		Button button4 = new Button(ccc, SWT.PUSH);
		Composite cColor4 = new Composite(ccc, SWT.NONE);
		cColor4.setBounds(110, 0, 400, 18);
		btnCstColChart = new Button(cColor4, SWT.RADIO);
		btnExpressionChart = new Button(cColor4, SWT.RADIO);
		buildColorComposite(ccc, textColorChart, colorLabelChart, button4, btnCstColChart, btnExpressionChart,
			"background color");
		ccc.setLocation(0, 0);

		CLabel lbltypeChart = new CLabel(chartComp, SWT.NONE);
		lbltypeChart.setBounds(0, 30, 80, 20);
		lbltypeChart.setText("Chart type");

		comboTypeChart = new CCombo(chartComp, SWT.BORDER);
		comboTypeChart.setItems(types_chart);
		comboTypeChart.setBounds(90, 30, 200, 20);
		if ( types_chart.length > 0 ) {
			comboTypeChart.setText(types_chart[0]);
		}

		Canvas canvasCL = canvasChartLayer(chartComp);
		canvasCL.setLocation(0, 70);
	}

	public Canvas canvasChartLayer(final Composite container) {
		// ****** CANVAS CHART LAYER *********
		Canvas canvasChartLayer = new Canvas(container, SWT.NONE);
		canvasChartLayer.setSize(720, 120);
		final GamaDiagramEditor diagramEditor = (GamaDiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();

		table_chart_layers = createTableEditor(canvasChartLayer);
		table_chart_layers.setBounds(10, 0, 660, 80);
		table_chart_layers.setHeaderVisible(true);
		table_chart_layers.setLinesVisible(true);
		table_chart_layers.setLinesVisible(true);

		Button btnAddChartLayer = new Button(canvasChartLayer, SWT.NONE);
		btnAddChartLayer.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				TableItem ti = new TableItem(table_chart_layers, SWT.NONE);
				final String name = "data_name";
				ti.setText(new String[] { name, styles_layer[0], "", "" });
				save("");

			}
		});
		btnAddChartLayer.setBounds(62, 90, 94, 20);
		btnAddChartLayer.setText("Add data");

		Button btnDeleteChartLayer = new Button(canvasChartLayer, SWT.NONE);
		btnDeleteChartLayer.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				int[] indices = table_chart_layers.getSelectionIndices();
				table_chart_layers.remove(indices);
				table_chart_layers.redraw();
				save("");
				ModelGenerator.modelValidation(fp, diagram);
				diagramEditor.updateEObjectErrors();
				frame.updateLayer();
			}
		});
		btnDeleteChartLayer.setBounds(163, 90, 112, 20);
		btnDeleteChartLayer.setText("Delete data");
		return canvasChartLayer;
	}

	/**
	 * Creates the main window's contents
	 * 
	 * @param shell the main window
	 */
	private Table createTableEditor(final Composite container) {
		// Create the table
		final Table tableChartLayer = new Table(container, SWT.SINGLE | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		tableChartLayer.setHeaderVisible(true);
		tableChartLayer.setLinesVisible(true);

		TableColumn tblclmnName = new TableColumn(tableChartLayer, SWT.NONE);
		tblclmnName.setWidth(230);
		tblclmnName.setText("Name");

		TableColumn tblclmnType = new TableColumn(tableChartLayer, SWT.NONE);
		tblclmnType.setWidth(100);
		tblclmnType.setText("Style");

		TableColumn tblclmnColor = new TableColumn(tableChartLayer, SWT.NONE);
		tblclmnColor.setWidth(100);
		tblclmnColor.setText("Color");

		TableColumn tblclmnValue = new TableColumn(tableChartLayer, SWT.NONE);
		tblclmnValue.setWidth(230);
		tblclmnValue.setText("Value");

		// Create an editor object to use for text editing
		final TableEditor editor = new TableEditor(tableChartLayer);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;

		// Use a mouse listener, not a selection listener, since we're interested
		// in the selected column as well as row
		tableChartLayer.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(final MouseEvent event) {
				// Dispose any existing editor
				Control old = editor.getEditor();
				if ( old != null ) {
					old.dispose();
				}

				// Determine where the mouse was clicked
				Point pt = new Point(event.x, event.y);

				// Determine which row was selected
				final TableItem item = tableChartLayer.getItem(pt);
				if ( item != null ) {
					// Determine which column was selected
					int column = -1;
					for ( int i = 0, n = tableChartLayer.getColumnCount(); i < n; i++ ) {
						Rectangle rect = item.getBounds(i);
						if ( rect.contains(pt) ) {
							// This is the selected column
							column = i;
							break;
						}
					}

					// Column 2 holds dropdowns
					if ( column == 1 ) {
						// Create the dropdown and add data to it
						final CCombo combo = new CCombo(tableChartLayer, SWT.READ_ONLY);
						combo.setItems(styles_layer);

						// Select the previously selected item from the cell
						combo.select(combo.indexOf(item.getText(column)));

						// Compute the width for the editor
						// Also, compute the column width, so that the dropdown fits
						// editor.minimumWidth = combo.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
						// table.getColumn(column).setWidth(editor.minimumWidth);

						// Set the focus on the dropdown and set into the editor
						combo.setFocus();
						editor.setEditor(combo, item, column);

						// Add a listener to set the selected item back into the cell
						final int col = column;
						combo.addSelectionListener(new SelectionAdapter() {

							@Override
							public void widgetSelected(final SelectionEvent event) {
								item.setText(col, combo.getText());
								// They selected an item; end the editing session
								combo.dispose();
							}
						});
					} else if ( column != 1 ) {
						// Create the Text object for our editor
						final GamaDiagramEditor diagramEditor =
							(GamaDiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();
						String name = "legend:";
						switch (column) {
							case 2:
								name = "color:";
								break;
							case 3:
								name = "";
								break;
						}
						final ValidateText text =
							new ValidateText(tableChartLayer, SWT.NONE, diagram, fp, cFrame, diagramEditor, name, null,
								null);
						item.setBackground(column, text.getBackground());

						text.setForeground(item.getForeground());
						if ( column == 0 ) {
							text.setString(true);
						}
						// Transfer any text from the cell to the Text control,
						// set the color to match this row, select the text,
						// and set focus to the control
						text.setText(item.getText(column));
						text.setForeground(item.getForeground());
						text.selectAll();
						text.setFocus();

						// Recalculate the minimum width for the editor
						editor.minimumWidth = text.getBounds().width;

						// Set the control into the editor
						editor.setEditor(text, item, column);

						// Add a handler to transfer the text back to the cell
						// any time it's modified
						final int col = column;
						text.addModifyListener(new ModifyListener() {

							@Override
							public void modifyText(final ModifyEvent event) {
								// Set the text of the editor's control back into the cell

								item.setText(col, text.getText());
								save("");
								text.applyModification();

								item.setBackground(col, text.getBackground());
								for ( int i = 2; i < tableChartLayer.getColumnCount(); i++ ) {
									if ( i == col ) {
										continue;
									}
									String name = "legend:";
									switch (col) {
										case 2:
											name = "color:";
											break;
										case 3:
											name = "";
											break;
									}
									String error = diagramEditor.containErrors(text.getLoc(), name, null);
									String textI = item.getText(i);
									if ( error != null && !error.isEmpty() ) {
										item.setBackground(i, new Color(text.getDisplay(), 255, 100, 100));
									} else if ( !textI.contains(";") && !textI.contains("{") && !textI.contains("}") ) {
										item.setBackground(i, new Color(text.getDisplay(), 100, 255, 100));
									}
								}
							}
						});
					}
				}
			}
		});
		return tableChartLayer;
	}

	void initTable() {
		if ( ((ELayer) eobject).getChartlayers() == null ) { return; }
		for ( EChartLayer var : ((ELayer) eobject).getChartlayers() ) {
			TableItem ti = new TableItem(table_chart_layers, SWT.NONE);
			ti.setText(new String[] { var.getName(), var.getStyle(), var.getColor(), var.getValue() });
		}
	}

	@Override
	protected Point getInitialSize() {
		return new Point(743, 510);
	}

}
