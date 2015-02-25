package idees.gama.ui.editFrame;

import gama.*;
import idees.gama.diagram.GamaDiagramEditor;
import idees.gama.features.edit.EditSpeciesFeature;
import idees.gama.features.modelgeneration.ModelGenerator;
import java.util.*;
import java.util.List;
import msi.gama.util.GamaList;
import msi.gaml.compilation.AbstractGamlAdditions;
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

public class EditSpeciesFrame extends EditFrame {

	// Variables
	int cpt = 1;
	private Table table_vars;
	// private String[] types_base = {"int", "float", "string", "bool", "rgb", "pair", "list", "map", "file", "geometry", "path", "graph"};
	// private String[] types_base = new String[AbstractGamlAdditions.getAllFields().size()];
	private final List<String> types = new ArrayList<String>();

	// Shapes
	private CCombo comboShape;
	private final String[] type_shape = { "point", "polyline", "polygon", "circle", "square", "rectangle", "hexagon",
		"sphere", "expression" };
	private ValidateText textRadius;
	private ValidateText textHeight;
	private ValidateText textWidth;
	private ValidateText textSize;
	private ValidateText textPoints;
	private ValidateText textShape;
	private ValidateText textShapeUpdate;
	private ValidateText textShapeFunction;
	private StyledText textInit;
	private ValidateText textSchedules;
	Composite sizeComp;
	Composite radiusComp;
	Composite wHComp;
	Composite pointsComp;
	Composite expShapeComp;
	org.eclipse.swt.widgets.List reflexViewer;
	org.eclipse.swt.widgets.List skillsViewer;
	List<String> reflexStrs;
	List<String> skillsStrs;
	Button btnRndLoc;
	Button btnExpressionLoc;
	Button btnYesTorus;
	Button btnExpressionTorus;
	Button btnNoTorus;
	Button btnShapeNormal;
	Button btnShapeFct;
	// Torus
	private ValidateText textTorus;
	// private String torusStr;

	// Location
	private ValidateText textLoc;
	private ValidateText textLocUpdate;
	private ValidateText textLocFunction;

	// grid
	private CCombo comboNeighborhood;
	private ValidateText textNeighborhood;
	private ValidateText textNbCols;
	private ValidateText textNbRows;
	private final String[] type_neighborhood = { "4 (square - von Neumann)", "8 (square - Moore)", "6 (hexagon)",
		"expression" };

	Button btnLocNormal;
	Button btnLocFct;

	// bounds
	private final String[] type_bounds = { "width-height", "file", "expression" };
	private CCombo comboBounds;
	private ValidateText textBoundsWidth;
	private ValidateText textBoundsHeight;
	private ValidateText textBoundsExpression;
	private ValidateText textBoundsFile;
	Font titleFont;

	private final int CONST_WIDTH = 763;

	final EditFrame frame;

	// topology
	// private String[] type_topo = {"continuous", "grid", "graph_node", "graph_edge"};
	// private CCombo comboTopo;

	/**
	 * Create the application window.
	 */
	public EditSpeciesFrame(final Diagram diagram, final IFeatureProvider fp, final EditSpeciesFeature esf,
		final ESpecies species, final List<ESpecies> speciesList) {
		super(diagram, fp, esf, species, "Species definition");
		frame = this;
		skillsStrs = new ArrayList<String>();
		skillsStrs.addAll(AbstractGamlAdditions.getAllSkills());
		skillsStrs.removeAll(AbstractGamlAdditions.ARCHITECTURES);
		skillsStrs.remove("grid");

		reflexStrs = new ArrayList<String>();
		List<String> newReflex = new ArrayList<String>();
		for ( EReflexLink link : species.getReflexLinks() ) {
			if ( link.getTarget() == null ) {
				continue;
			}
			newReflex.add(link.getTarget().getName());
		}
		if ( species.getReflexList() == null || species.getReflexList().isEmpty() ) {
			reflexStrs.addAll(newReflex);
		} else {
			for ( String ref : species.getReflexList() ) {
				if ( newReflex.contains(ref) ) {
					reflexStrs.add(ref);
				}
			}
			for ( String ref : newReflex ) {
				if ( !reflexStrs.contains(ref) ) {
					reflexStrs.add(ref);
				}
			}
		}

		for ( Collection varType : AbstractGamlAdditions.VARTYPE2KEYWORDS.values() ) {
			types.addAll(varType);
		}
		for ( ESpecies sp : speciesList ) {
			types.add(sp.getName());
		}
		types.remove("unknown");
		types.remove("world");
		List<String> tt = new ArrayList<String>(types);
		for ( String ty : tt ) {
			types.add("list<" + ty + ">");
		}
	}

	/**
	 * Create contents of the application window.
	 * @param parent
	 */
	@Override
	protected Control createContents(final Composite parent) {
		final ScrolledComposite sc = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.BORDER);
		final Composite container = new Composite(sc, SWT.BORDER);
		// Composite container = new Composite(parent, SWT.SCROLL_PAGE);
		titleFont = new Font(getShell().getDisplay(), "Arial", 10, SWT.BOLD);

		ESpecies species = (ESpecies) eobject;
		boolean init = !(species.getTopology() instanceof EGridTopology);
		commonCompositeHeader(container, init);
		sc.setContent(container);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		int sizeHeader = init ? 200 : 40;
		// CANVAS SIZE : NAME:30, VAR:200, TORUS:30, LOC:130, REFLEX:110, SHAPE:220, SKILLS: 110 BOUNDS: 80 GRID: 80
		if ( eobject instanceof EWorldAgent ) {
			Canvas canvasTor = canvasTorus(container);
			canvasTor.setLocation(10, 160 + sizeHeader);
			Canvas canvasBounds = canvasBounds(container);
			canvasBounds.setLocation(10, 200 + sizeHeader);
			Composite comp = commonCompositeEnd(container);
			comp.setLocation(0, 290 + sizeHeader);
			container.setSize(730, 680 + sizeHeader);
			sc.setMinSize(container.computeSize(730, 680 + sizeHeader));
		} else if ( species.getTopology() instanceof EGridTopology ) {
			gridTopo(container);
			container.setSize(730, 630 + sizeHeader);
			sc.setMinSize(container.computeSize(730, 630 + sizeHeader));
		} else if ( species.getTopology() instanceof EGraphTopologyNode ) {
			graphNodeTopo(container);
			container.setSize(730, 920 + sizeHeader);
			sc.setMinSize(container.computeSize(730, 920 + sizeHeader));
		} else if ( species.getTopology() instanceof EGraphTopologyEdge ) {
			graphEdgeTopo(container);
			container.setSize(730, 920 + sizeHeader);
			sc.setMinSize(container.computeSize(730, 920 + sizeHeader));
		} else {
			graphNodeContinuous(container);
			container.setSize(730, 920 + sizeHeader);
			sc.setMinSize(container.computeSize(730, 920 + sizeHeader));
		}
		return container;
	}

	private Composite commonCompositeHeader(final Composite container, final boolean init) {
		// CANVAS SIZE : NAME:30, SKILLS: 110 //INIT : 150
		Composite comp = new Composite(container, SWT.NONE);
		comp.setBounds(0, 0, 730, init ? 350 : 200);
		Canvas canvasName = canvasName(comp);
		canvasName.setLocation(10, 10);
		Canvas canvasSchedules = canvasSchedules(comp);
		canvasSchedules.setLocation(10, 50);
		Canvas canvasSkills = canvasSkills(comp);
		canvasSkills.setLocation(10, 90);
		if ( init ) {
			Canvas canvasInit = canvasInit(comp);
			canvasInit.setLocation(10, 200);
		}
		return comp;
	}

	protected Canvas canvasSchedules(final Composite container) {
		Canvas canvasSchedules = new Canvas(container, SWT.BORDER);
		canvasSchedules.setBounds(10, 10, 720, 30);

		CLabel lblSchedules = new CLabel(canvasSchedules, SWT.NONE);
		lblSchedules.setBounds(10, 5, 60, 20);
		lblSchedules.setText("Schedules");
		GamaDiagramEditor diagramEditor = (GamaDiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();
		textSchedules =
			new ValidateText(canvasSchedules, SWT.BORDER, diagram, fp, frame, diagramEditor, "schedules:", null, null);
		textSchedules.setBounds(70, 5, 300, 20);
		String val = ((ESpecies) eobject).getSchedules();
		textSchedules.setText(val == null ? "" : val);
		textSchedules.setSaveData(true);
		return canvasSchedules;
	}

	private Composite commonCompositeEnd(final Composite container) {
		// VAR:200, REFLEX:110, OKCANCEL: 30
		Composite comp = new Composite(container, SWT.NONE);
		comp.setBounds(0, 160, 730, 370);
		Canvas canvasVar = canvasVariables(comp);
		Canvas canvasRef = canvasReflex(comp);
		// Canvas canvasOkCancel = canvasOkCancel(comp);
		canvasVar.setLocation(10, 0);
		canvasRef.setLocation(10, 210);
		// canvasOkCancel.setLocation(10, 330);
		return comp;
	}

	private Composite gridTopo(final Composite container) {
		// VAR:200, REFLEX:110, OKCANCEL: 30 //END: 370
		Composite comp = new Composite(container, SWT.NONE);
		comp.setBounds(0, 200, 730, 500);
		Canvas canvasTor = canvasTorus(comp);
		canvasTor.setLocation(10, 90);
		Canvas canvasGrid = canvasGrid(comp);
		canvasGrid.setLocation(10, 0);
		Composite compEnd = commonCompositeEnd(comp);
		compEnd.setLocation(0, 140);
		return comp;
	}

	private Composite graphNodeTopo(final Composite container) {
		// VAR:200, REFLEX:110, OKCANCEL: 30 //END: 370
		Composite comp = new Composite(container, SWT.NONE);
		comp.setBounds(0, 160, 730, 910);
		Canvas canvasLoc = canvasLocation(comp);
		canvasLoc.setLocation(10, 160);
		Canvas canvasShape = canvasShape(comp);
		canvasShape.setLocation(10, 300);
		Composite compEnd = commonCompositeEnd(comp);
		compEnd.setLocation(0, 530);
		return comp;
	}

	private Composite graphEdgeTopo(final Composite container) {
		// VAR:200, REFLEX:110, OKCANCEL: 30 //END: 370
		Composite comp = new Composite(container, SWT.NONE);
		comp.setBounds(0, 160, 730, 910);
		Canvas canvasLoc = canvasLocation(comp);
		canvasLoc.setLocation(10, 160);
		Canvas canvasShape = canvasShape(comp);
		canvasShape.setLocation(10, 300);
		Composite compEnd = commonCompositeEnd(comp);
		compEnd.setLocation(0, 530);
		return comp;
	}

	private Composite graphNodeContinuous(final Composite container) {
		// VAR:200, REFLEX:110, OKCANCEL: 30 //END: 370
		Composite comp = new Composite(container, SWT.NONE);
		comp.setBounds(0, 160, 730, 910);
		Canvas canvasLoc = canvasLocation(comp);
		canvasLoc.setLocation(10, 160);
		Canvas canvasShape = canvasShape(comp);
		canvasShape.setLocation(10, 300);
		Composite compEnd = commonCompositeEnd(comp);
		compEnd.setLocation(0, 530);
		return comp;
	}

	private EVariable getEVariable(final String name) {
		if ( ((ESpecies) eobject).getVariables() == null || ((ESpecies) eobject).getVariables().isEmpty() ) { return null; }
		for ( EVariable var : ((ESpecies) eobject).getVariables() ) {
			if ( var == null ) {
				continue;
			}
			if ( name.equals(var.getName()) ) { return var; }
		}
		return null;
	}

	private void modifyVariables() {
		ESpecies species = (ESpecies) eobject;
		List<EVariable> vars = new ArrayList<EVariable>();
		vars.addAll(species.getVariables());
		species.getVariables().clear();
		for ( EVariable var : vars ) {
			GamaDiagramEditor diagramEditor = (GamaDiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();
			diagramEditor.removeEOject(var);
			EcoreUtil.delete(var, true);

		}
		for ( final TableItem item : table_vars.getItems() ) {
			final EVariable var = gama.GamaFactory.eINSTANCE.createEVariable();
			var.setName(item.getText(0));
			var.setType(item.getText(1));
			var.setInit(item.getText(2));
			var.setUpdate(item.getText(3));
			var.setFunction(item.getText(4));
			var.setMin(item.getText(5));
			var.setMax(item.getText(6));
			species.getVariables().add(var);
			GamaDiagramEditor diagramEditor = (GamaDiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();
			diagramEditor.addEOject(var);

		}
	}

	private void modifyShape() {
		ESpecies species = (ESpecies) eobject;
		final EVariable var =
			getEVariable("shape") == null ? gama.GamaFactory.eINSTANCE.createEVariable() : getEVariable("shape");
		var.setName("shape");
		var.setType("geometry");
		String initVal = "";
		String val = comboShape.getText();

		species.setShapeFunction(textShapeFunction.getText());
		species.setShapeUpdate(textShapeUpdate.getText());
		species.setShapeIsFunction(btnShapeFct.getSelection());
		species.setShapeType(val);
		species.setPoints(textPoints.getText());
		species.setRadius(textRadius.getText());
		species.setSize(textSize.getText());
		species.setHeigth(textHeight.getText());
		species.setWidth(textWidth.getText());
		species.setExpressionShape(textShape.getText());
		if ( val.equals("point") && (textShapeUpdate.getText() == null || textShapeUpdate.getText().isEmpty()) &&
			(textShapeFunction.getText() == null || textShapeFunction.getText().isEmpty()) &&
			(textShape.getText() == null || textShape.getText().isEmpty()) ) {
			species.getVariables().remove(var);
			EcoreUtil.delete(var, true);
			return;
		} else if ( val.equals("polyline") || val.equals("polygon") ) {
			initVal = val + "(" + textPoints.getText() + ")";
		} else if ( val.equals("circle") || val.equals("sphere") ) {
			initVal = val + "(" + textRadius.getText() + ")";
		} else if ( val.equals("square") ) {
			initVal = val + "(" + textSize.getText() + ")";
		} else if ( val.equals("rectangle") || val.equals("hexagon") ) {
			initVal = val + "({" + textWidth.getText() + "," + textHeight.getText() + "})";
		} else if ( val.equals("expression") ) {
			initVal = textShape.getText();
		}

		if ( btnShapeFct.getSelection() ) {
			var.setFunction(textShapeFunction.getText());
		} else {
			var.setInit(initVal);
			var.setUpdate(textShapeUpdate.getText());
		}
		if ( !species.getVariables().contains(var) ) {
			species.getVariables().add(var);
		}
	}

	private void modifyLocation() {
		ESpecies species = (ESpecies) eobject;
		species.setExpressionLoc(textLoc.getText());
		species.setLocationFunction(textLocFunction.getText());
		species.setLocationUpdate(textLocUpdate.getText());
		species.setLocationIsFunction(btnLocFct.getSelection());
		species.setLocationType(btnRndLoc.getSelection() ? "random" : "expression");

		if ( btnRndLoc.getSelection() && species.getLocationFunction().isEmpty() &&
			species.getLocationUpdate().isEmpty() ) {
			final EVariable var = getEVariable("location");
			if ( var != null ) {
				species.getVariables().remove(var);
				EcoreUtil.delete(var, true);
			}
		} else {
			final EVariable var =
				getEVariable("location") == null ? gama.GamaFactory.eINSTANCE.createEVariable()
					: getEVariable("location");
			var.setName("location");
			var.setType("point");
			var.setInit(btnRndLoc.getSelection() || !btnLocNormal.getSelection() ? "" : textLoc.getText());
			var.setFunction(btnLocNormal.getSelection() ? "" : textLocFunction.getText());
			var.setUpdate(btnLocNormal.getSelection() ? textLocUpdate.getText() : "");
			if ( !species.getVariables().contains(var) ) {
				species.getVariables().add(var);
			}
		}

	}

	private void modifyIsTorus() {
		ESpecies species = (ESpecies) eobject;
		if ( btnYesTorus.getSelection() ) {
			species.setTorus("true");
			species.setTorusType("yes");
		} else if ( btnNoTorus.getSelection() ) {
			species.setTorus("false");
			species.setTorusType("no");
		} else {
			species.setTorus(textTorus.getText());
			species.setTorusType("expression");
		}

		species.setExpressionTorus(textTorus.getText());
	}

	private void modifySchedules() {
		ESpecies species = (ESpecies) eobject;
		species.setSchedules(textSchedules.getText());
	}

	private void modifyReflexOrder() {
		((ESpecies) eobject).getReflexList().clear();
		((ESpecies) eobject).getReflexList().addAll(reflexStrs);
	}

	private void modifyGridProperties() {
		// System.out.println("LALALALA");
		ESpecies species = (ESpecies) eobject;
		EGridTopology gridTopo = (EGridTopology) species.getTopology();
		gridTopo.setNb_columns(textNbCols.getText());
		gridTopo.setNb_rows(textNbRows.getText());
		gridTopo.setNeighbourhoodType(comboNeighborhood.getText());
		gridTopo.setNeighbourhood(textNeighborhood.getText());
		// System.out.println("gridTopo;getNb_columns: " + gridTopo.getNb_columns());
	}

	private void modifyBounds() {
		EWorldAgent world = (EWorldAgent) eobject;
		world.setBoundsType(comboBounds.getText());
		// System.out.println("textBoundsExpression.getText(): " + textBoundsExpression.getText());
		world.setBoundsExpression(textBoundsExpression.getText());
		world.setBoundsHeigth(textBoundsHeight.getText());
		world.setBoundsWidth(textBoundsWidth.getText());
		world.setBoundsPath(textBoundsFile.getText());
	}

	/**
	 * Creates the main window's contents
	 * 
	 * @param shell the main window
	 */
	private Table createTableEditor(final Composite container) {
		// Create the table
		final Table tableVars = new Table(container, SWT.SINGLE | SWT.FULL_SELECTION
		/* | SWT.HIDE_SELECTION */);
		tableVars.setHeaderVisible(true);
		tableVars.setLinesVisible(true);

		tableVars.addListener(SWT.MeasureItem, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				event.height = 20;
				// TableItem item = (TableItem) event.item;
				// System.out.println("item.getBackground(): " + item.getBackground());
				// event.gc.setBackground(item.getBackground());
			}
		});

		TableColumn tblclmnName = new TableColumn(tableVars, SWT.NONE);
		tblclmnName.setWidth(100);
		tblclmnName.setText("Name");

		TableColumn tblclmnType = new TableColumn(tableVars, SWT.NONE);
		tblclmnType.setWidth(100);
		tblclmnType.setText("Type");

		TableColumn tblclmnInitValue = new TableColumn(tableVars, SWT.NONE);
		tblclmnInitValue.setWidth(100);
		tblclmnInitValue.setText("init value");

		TableColumn tblclmnUpdate = new TableColumn(tableVars, SWT.NONE);
		tblclmnUpdate.setWidth(100);
		tblclmnUpdate.setText("update");

		TableColumn tblclmnFunction = new TableColumn(tableVars, SWT.NONE);
		tblclmnFunction.setWidth(100);
		tblclmnFunction.setText("function");

		TableColumn tblclmnMin = new TableColumn(tableVars, SWT.NONE);
		tblclmnMin.setWidth(100);
		tblclmnMin.setText("min");

		TableColumn tblclmnMax = new TableColumn(tableVars, SWT.NONE);
		tblclmnMax.setWidth(100);
		tblclmnMax.setText("max");

		// Create an editor object to use for text editing
		final TableEditor editor = new TableEditor(tableVars);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;

		// Use a mouse listener, not a selection listener, since we're interested
		// in the selected column as well as row
		tableVars.addMouseListener(new MouseAdapter() {

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
				final TableItem item = tableVars.getItem(pt);

				if ( item != null ) {
					// Determine which column was selected
					int column = -1;
					for ( int i = 0, n = tableVars.getColumnCount(); i < n; i++ ) {
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
						final CCombo combo = new CCombo(tableVars, SWT.READ_ONLY);
						for ( int i = 0, n = types.size(); i < n; i++ ) {
							combo.add(types.get(i));
						}

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
								save("variables");
								// They selected an item; end the editing session
								combo.dispose();
							}
						});
					} else if ( column != 1 ) {
						// Create the Text object for our editor
						final GamaDiagramEditor diagramEditor =
							(GamaDiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();
						String name = "name";
						switch (column) {
							case 2:
								name = "<-";
								break;
							case 3:
								name = "update:";
								break;
							case 4:
								name = "->";
								break;
							case 5:
								name = "min:";
								break;
							case 6:
								name = "max:";
								break;
						}

						final ValidateText text =
							new ValidateText(tableVars, SWT.BORDER, diagram, fp, frame, diagramEditor, name, null, item
								.getText(0));
						// text.setAllErrors(true);

						// Listener[] lis = text.getListeners(SWT.Modify);
						// for (Listener li : lis) {text.removeModifyListener((ModifyListener) li);}
						item.setBackground(column, text.getBackground());

						// final Text text = new Text(tableVars, SWT.NONE);
						// text.setForeground(item.getForeground());

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
								save("variables");
								text.applyModification();

								item.setBackground(col, text.getBackground());
								for ( int i = 2; i < table_vars.getColumnCount(); i++ ) {
									if ( i == col ) {
										continue;
									}
									String name = "name";
									switch (i) {
										case 2:
											name = "<-";
											break;
										case 3:
											name = "update:";
											break;
										case 4:
											name = "->";
											break;
										case 5:
											name = "min:";
											break;
										case 6:
											name = "max:";
											break;
									}
									String error = diagramEditor.containErrors(text.getLoc(), name, null);
									// System.out.println("error = " + error);
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
				} else {
					tableVars.deselectAll();
				}
			}
		});
		return tableVars;
	}

	void initTable() {
		for ( EVariable var : ((ESpecies) eobject).getVariables() ) {
			if ( var.getName().equals("shape") || var.getName().equals("location") ) {
				continue;
			}
			TableItem ti = new TableItem(table_vars, SWT.NONE);
			ti.setText(new String[] { var.getName(), var.getType(), var.getInit(), var.getUpdate(), var.getFunction(),
				var.getMin(), var.getMax() });
			cpt++;
		}
	}

	public Canvas canvasVariables(final Composite container) {
		// ****** CANVAS VARIABLES *********
		Canvas canvasVariable = new Canvas(container, SWT.BORDER);
		canvasVariable.setBounds(10, 250, 720, 200);

		table_vars = createTableEditor(canvasVariable);
		table_vars.setBounds(10, 30, 700, 120);
		table_vars.setHeaderVisible(true);
		table_vars.setLinesVisible(true);

		initTable();

		CLabel lblVariables = new CLabel(canvasVariable, SWT.NONE);
		lblVariables.setBounds(10, 5, 100, 20);
		lblVariables.setText("Variables");
		lblVariables.setFont(titleFont);
		Button btnAddVariable = new Button(canvasVariable, SWT.NONE);
		btnAddVariable.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				TableItem ti = new TableItem(table_vars, SWT.NONE);
				final String name = "var_name" + cpt;
				ti.setText(new String[] { name, "int", "", "", "", "", "" });
				ti.setBackground(new Color(frame.getShell().getDisplay(), 100, 255, 100));
				cpt++;
				save("variables");
			}
		});
		btnAddVariable.setBounds(62, 162, 94, 28);
		btnAddVariable.setText("Add variable");

		Button btnDeleteVariable = new Button(canvasVariable, SWT.NONE);
		btnDeleteVariable.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				int[] indices = table_vars.getSelectionIndices();
				table_vars.remove(indices);
				table_vars.redraw();
				save("variables");
			}
		});
		btnDeleteVariable.setBounds(163, 162, 112, 28);
		btnDeleteVariable.setText("Delete variable");
		return canvasVariable;
	}

	public Composite shapeInitValue(final Composite container) {
		final GamaDiagramEditor diagramEditor = (GamaDiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();

		// Shape
		final Composite shapeComp = new Composite(container, SWT.BORDER);
		shapeComp.setForeground(this.getShell().getDisplay().getSystemColor(SWT.COLOR_BLACK));
		shapeComp.setBounds(10, 60, 700, 110);
		CLabel lblInitShape = new CLabel(shapeComp, SWT.NONE);
		lblInitShape.setBounds(5, 5, 80, 20);
		lblInitShape.setText("Init value");

		comboShape = new CCombo(shapeComp, SWT.BORDER);
		comboShape.setBounds(90, 5, 300, 20);
		comboShape.setItems(type_shape);
		// comboShape.setText("point");
		// "point", "polyline", "polygon", "circle", "square", "rectangle", "hexagon", "sphere", "expression"
		comboShape.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				String val = comboShape.getText();
				if ( val.equals("point") ) {
					sizeComp.setVisible(false);
					sizeComp.setEnabled(false);
					radiusComp.setVisible(false);
					radiusComp.setEnabled(false);
					wHComp.setVisible(false);
					wHComp.setEnabled(false);
					pointsComp.setVisible(false);
					pointsComp.setEnabled(false);
					expShapeComp.setVisible(false);
					expShapeComp.setEnabled(false);
				} else if ( val.equals("polyline") || val.equals("polygon") ) {
					sizeComp.setVisible(false);
					sizeComp.setEnabled(false);
					radiusComp.setVisible(false);
					radiusComp.setEnabled(false);
					wHComp.setVisible(false);
					wHComp.setEnabled(false);
					pointsComp.setVisible(true);
					pointsComp.setEnabled(true);
					expShapeComp.setVisible(false);
					expShapeComp.setEnabled(false);
				} else if ( val.equals("circle") || val.equals("sphere") ) {
					sizeComp.setVisible(false);
					sizeComp.setEnabled(false);
					radiusComp.setVisible(true);
					radiusComp.setEnabled(true);
					pointsComp.setVisible(false);
					pointsComp.setEnabled(false);
					wHComp.setVisible(false);
					wHComp.setEnabled(false);
					expShapeComp.setVisible(false);
					expShapeComp.setEnabled(false);
					expShapeComp.setEnabled(false);
				} else if ( val.equals("square") ) {
					sizeComp.setVisible(true);
					sizeComp.setEnabled(true);
					radiusComp.setVisible(false);
					radiusComp.setEnabled(false);
					pointsComp.setVisible(false);
					pointsComp.setEnabled(false);
					wHComp.setVisible(false);
					wHComp.setEnabled(false);
					expShapeComp.setVisible(false);
					expShapeComp.setEnabled(false);
				} else if ( val.equals("rectangle") || val.equals("hexagon") ) {
					sizeComp.setVisible(false);
					sizeComp.setEnabled(false);
					radiusComp.setVisible(false);
					radiusComp.setEnabled(false);
					pointsComp.setVisible(false);
					pointsComp.setEnabled(false);
					wHComp.setVisible(true);
					wHComp.setEnabled(true);
					expShapeComp.setVisible(false);
					expShapeComp.setEnabled(false);
				} else if ( val.equals("expression") ) {
					sizeComp.setVisible(false);
					sizeComp.setEnabled(false);
					radiusComp.setVisible(false);
					radiusComp.setEnabled(false);
					pointsComp.setVisible(false);
					pointsComp.setEnabled(false);
					wHComp.setVisible(false);
					wHComp.setEnabled(false);
					expShapeComp.setVisible(true);
					expShapeComp.setEnabled(true);
				}
				shapeComp.pack();
				if ( textShapeUpdate.isSaveData() ) {
					save("");
					ModelGenerator.modelValidation(fp, diagram);
					diagramEditor.updateEObjectErrors();
				}
			}

		});

		// Square
		sizeComp = new Composite(shapeComp, SWT.NONE);
		sizeComp.setVisible(false);
		sizeComp.setEnabled(false);
		sizeComp.setBounds(20, 40, 600, 60);
		CLabel lblSize = new CLabel(sizeComp, SWT.NONE);
		lblSize.setBounds(0, 0, 60, 20);
		lblSize.setText("Size");

		textSize = new ValidateText(sizeComp, SWT.BORDER, diagram, fp, frame, diagramEditor, "", null, "shape");

		textSize.setBounds(70, 0, 300, 20);
		textSize.setText("1.0");

		// Circle - Sphere
		radiusComp = new Composite(shapeComp, SWT.NONE);
		radiusComp.setVisible(false);
		radiusComp.setEnabled(false);
		radiusComp.setBounds(20, 40, 600, 60);
		CLabel lblRadius = new CLabel(radiusComp, SWT.NONE);
		lblRadius.setBounds(0, 0, 60, 20);
		lblRadius.setText("Radius");

		textRadius = new ValidateText(radiusComp, SWT.BORDER, diagram, fp, frame, diagramEditor, "", null, "shape");
		textRadius.setBounds(70, 0, 300, 20);
		textRadius.setText("1.0");

		// Hexagon - Rectangle
		wHComp = new Composite(shapeComp, SWT.NONE);
		wHComp.setBounds(20, 40, 600, 60);
		wHComp.setVisible(false);
		wHComp.setEnabled(false);
		CLabel lblHeight = new CLabel(wHComp, SWT.NONE);
		lblHeight.setBounds(0, 30, 60, 20);
		lblHeight.setText("Height");

		textHeight = new ValidateText(wHComp, SWT.BORDER, diagram, fp, frame, diagramEditor, "", null, "shape");
		textHeight.setBounds(70, 30, 300, 20);
		textHeight.setText("1.0");

		CLabel lblWidth = new CLabel(wHComp, SWT.NONE);
		lblWidth.setBounds(0, 0, 60, 20);
		lblWidth.setText("Width");

		textWidth = new ValidateText(wHComp, SWT.BORDER, diagram, fp, frame, diagramEditor, "", null, "shape");
		textWidth.setBounds(70, 0, 300, 20);
		textWidth.setText("1.0");

		// Polygon, Polyline
		pointsComp = new Composite(shapeComp, SWT.NONE);
		pointsComp.setVisible(false);
		pointsComp.setEnabled(false);
		pointsComp.setBounds(20, 40, 600, 60);
		CLabel lblPoints = new CLabel(pointsComp, SWT.NONE);
		lblPoints.setBounds(0, 0, 60, 20);
		lblPoints.setText("Points");

		textPoints = new ValidateText(pointsComp, SWT.BORDER, diagram, fp, frame, diagramEditor, "", null, "shape");
		textPoints.setBounds(70, 0, 300, 20);
		textPoints.setText("[{0.0,0.0},{0.0,1.0},{1.0,1.0},{1.0,0.0}]");

		// Expression shape
		expShapeComp = new Composite(shapeComp, SWT.NONE);
		expShapeComp.setVisible(false);
		expShapeComp.setEnabled(false);
		expShapeComp.setBounds(20, 50, 600, 60);
		CLabel lblExpShape = new CLabel(expShapeComp, SWT.NONE);
		lblExpShape.setBounds(0, 0, 70, 20);
		lblExpShape.setText("Expression");

		textShape = new ValidateText(expShapeComp, SWT.BORDER, diagram, fp, frame, diagramEditor, "", null, "shape");
		textShape.setBounds(70, 0, 300, 20);
		return shapeComp;
	}

	public Canvas canvasShape(final Composite container) {
		// ****** CANVAS SHAPE *********
		ESpecies species = (ESpecies) eobject;
		final GamaDiagramEditor diagramEditor = (GamaDiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();

		Canvas canvasShape = new Canvas(container, SWT.BORDER);
		canvasShape.setBounds(10, 50, 720, 220);

		CLabel lblShape = new CLabel(canvasShape, SWT.NONE);
		lblShape.setBounds(5, 5, 50, 20);
		lblShape.setText("Shape");
		lblShape.setFont(titleFont);
		final Composite intVal = shapeInitValue(canvasShape);

		CLabel lblUpdate = new CLabel(canvasShape, SWT.NONE);
		lblUpdate.setBounds(5, 190, 60, 20);
		lblUpdate.setText("Update");

		textShapeUpdate =
			new ValidateText(canvasShape, SWT.BORDER, diagram, fp, frame, diagramEditor, "", null, "shape");

		textShapeUpdate.setBounds(70, 190, 300, 20);

		btnShapeNormal = new Button(canvasShape, SWT.RADIO);
		btnShapeNormal.setBounds(5, 30, 100, 20);
		btnShapeNormal.setText("Normal");
		btnShapeNormal.setSelection(false);

		btnShapeNormal.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				intVal.setEnabled(true);
				intVal.setVisible(true);
				textShapeUpdate.setEnabled(true);
				textShapeFunction.setEnabled(false);
				if ( textShapeUpdate.isSaveData() ) {
					save("");
					ModelGenerator.modelValidation(fp, diagram);
					diagramEditor.updateEObjectErrors();
				}
			}
		});

		btnShapeFct = new Button(canvasShape, SWT.RADIO);
		btnShapeFct.setBounds(150, 30, 80, 20);
		btnShapeFct.setText("Function");
		btnShapeFct.setSelection(false);
		btnShapeFct.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				intVal.setEnabled(false);
				intVal.setVisible(false);
				textShapeUpdate.setEnabled(false);
				textShapeFunction.setEnabled(true);
				if ( textShapeUpdate.isSaveData() ) {
					save("");
					ModelGenerator.modelValidation(fp, diagram);
					diagramEditor.updateEObjectErrors();
				}
			}
		});
		textShapeFunction =
			new ValidateText(canvasShape, SWT.BORDER, diagram, fp, frame, diagramEditor, "", null, "shape");

		textShapeFunction.setBounds(240, 30, 300, 18);
		textShapeFunction.setEnabled(false);

		String st = "point";
		if ( species.getShapeType() != null && !species.getShapeType().isEmpty() ) {
			st = species.getShapeType();
			if ( st.equals("polyline") || st.equals("polygon") ) {
				pointsComp.setVisible(true);
				pointsComp.setEnabled(true);
			} else if ( st.equals("circle") || st.equals("sphere") ) {
				radiusComp.setVisible(true);
				radiusComp.setEnabled(true);
			} else if ( st.equals("square") ) {
				sizeComp.setVisible(true);
				sizeComp.setEnabled(true);
			} else if ( st.equals("rectangle") || st.equals("hexagon") ) {
				wHComp.setVisible(true);
				wHComp.setEnabled(true);
			} else if ( st.equals("expression") ) {
				expShapeComp.setVisible(true);
				expShapeComp.setEnabled(true);
			}
		}
		comboShape.setText(st);
		if ( species.getPoints() != null && !species.getPoints().isEmpty() ) {
			textPoints.setText(species.getPoints());
		}
		if ( species.getRadius() != null && !species.getRadius().isEmpty() ) {
			textRadius.setText(species.getRadius());
		}
		if ( species.getSize() != null && !species.getSize().isEmpty() ) {
			textSize.setText(species.getSize());
		}
		if ( species.getHeigth() != null && !species.getHeigth().isEmpty() ) {
			textHeight.setText(species.getHeigth());
		}
		if ( species.getWidth() != null && !species.getWidth().isEmpty() ) {
			textWidth.setText(species.getWidth());
		}
		if ( species.getExpressionShape() != null && !species.getExpressionShape().isEmpty() ) {
			textShape.setText(species.getExpressionShape());
		}
		if ( species.getShapeFunction() != null && !species.getShapeFunction().isEmpty() ) {
			textShapeFunction.setText(species.getShapeFunction());
		}
		if ( species.getShapeUpdate() != null && !species.getShapeUpdate().isEmpty() ) {
			textShapeUpdate.setText(species.getShapeUpdate());
		}
		if ( species.getShapeIsFunction() != null && species.getShapeIsFunction() ) {
			btnShapeFct.setSelection(true);
			intVal.setEnabled(false);
			intVal.setVisible(false);
			textShapeUpdate.setEnabled(false);
			textShapeFunction.setEnabled(true);
		} else {
			btnShapeNormal.setSelection(true);
			intVal.setEnabled(true);
			intVal.setVisible(true);
			textShapeUpdate.setEnabled(true);
			textShapeFunction.setEnabled(false);
		}
		textShapeFunction.setSaveData(true);
		textShapeUpdate.setSaveData(true);
		textPoints.setSaveData(true);
		textRadius.setSaveData(true);
		textSize.setSaveData(true);
		textHeight.setSaveData(true);
		textWidth.setSaveData(true);
		textShape.setSaveData(true);

		textName.getLinkedVts().add(textShapeFunction);
		textName.getLinkedVts().add(textShapeUpdate);
		textName.getLinkedVts().add(textPoints);
		textName.getLinkedVts().add(textRadius);
		textName.getLinkedVts().add(textSize);
		textName.getLinkedVts().add(textHeight);
		textName.getLinkedVts().add(textWidth);
		textName.getLinkedVts().add(textShape);

		return canvasShape;

	}

	public Canvas canvasTorus(final Composite container) {
		// ****** CANVAS TORUS *********
		ESpecies species = (ESpecies) eobject;
		final GamaDiagramEditor diagramEditor = (GamaDiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();

		Canvas canvasTorus = new Canvas(container, SWT.BORDER);
		canvasTorus.setBounds(10, 160, 720, 30);
		// is Torus

		CLabel lblIsTorus = new CLabel(canvasTorus, SWT.NONE);
		lblIsTorus.setBounds(10, 5, 75, 20);
		lblIsTorus.setText("is Torus?");
		lblIsTorus.setFont(titleFont);

		Composite cTor = new Composite(canvasTorus, SWT.NONE);
		cTor.setBounds(90, 5, 185, 18);

		btnYesTorus = new Button(cTor, SWT.RADIO);
		btnYesTorus.setBounds(0, 0, 50, 18);
		btnYesTorus.setText("Yes");
		btnYesTorus.setSelection(false);

		btnYesTorus.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				textTorus.setEnabled(false);
				if ( textTorus.isSaveData() ) {
					save("torus:");
					ModelGenerator.modelValidation(fp, diagram);
					diagramEditor.updateEObjectErrors();
				}
			}
		});

		btnNoTorus = new Button(cTor, SWT.RADIO);
		btnNoTorus.setBounds(50, 0, 50, 18);
		btnNoTorus.setText("No");
		btnNoTorus.setSelection(false);
		btnNoTorus.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				textTorus.setEnabled(false);
				if ( textTorus.isSaveData() ) {
					save("torus:");
					ModelGenerator.modelValidation(fp, diagram);
					diagramEditor.updateEObjectErrors();
				}
			}
		});

		btnExpressionTorus = new Button(cTor, SWT.RADIO);
		btnExpressionTorus.setBounds(100, 0, 85, 18);
		btnExpressionTorus.setText("Expression:");
		btnExpressionTorus.setSelection(false);
		btnExpressionTorus.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				textTorus.setEnabled(true);
				if ( textTorus.isSaveData() ) {
					save("torus:");
					ModelGenerator.modelValidation(fp, diagram);
					diagramEditor.updateEObjectErrors();
				}
			}
		});

		textTorus = new ValidateText(canvasTorus, SWT.BORDER, diagram, fp, frame, diagramEditor, "torus:", null, null);
		textTorus.setBounds(290, 5, 300, 18);
		textTorus.setEnabled(false);

		if ( species.getTorusType() == null || species.getTorusType().isEmpty() ) {
			btnNoTorus.setSelection(true);
		} else {
			textTorus.setText(species.getExpressionTorus());
			if ( species.getTorusType().equals("yes") ) {
				btnYesTorus.setSelection(true);
			} else if ( species.getTorusType().equals("no") ) {
				btnNoTorus.setSelection(true);
			} else {
				btnExpressionTorus.setSelection(true);
				textTorus.setEnabled(true);
			}
		}
		textTorus.setSaveData(true);
		textName.getLinkedVts().add(textTorus);

		return canvasTorus;

	}

	public Composite initValueLocComp(final Composite container, final ESpecies species) {
		Composite cLoc = new Composite(container, SWT.BORDER);
		cLoc.setBounds(5, 60, 600, 30);
		CLabel lblLocation = new CLabel(cLoc, SWT.NONE);
		lblLocation.setBounds(5, 5, 80, 20);
		lblLocation.setText("Init value");

		btnRndLoc = new Button(cLoc, SWT.RADIO);
		btnRndLoc.setBounds(90, 5, 100, 20);
		btnRndLoc.setText("Random");
		if ( species.getLocationType() != null && !species.getLocationType().isEmpty() ) {
			btnRndLoc.setSelection(species.getLocationType().equals("random"));
		} else {
			btnRndLoc.setSelection(true);
		}
		final GamaDiagramEditor diagramEditor = (GamaDiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();

		btnRndLoc.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				textLoc.setEnabled(false);
				if ( textLocUpdate.isSaveData() ) {
					save("location");
					ModelGenerator.modelValidation(fp, diagram);
					diagramEditor.updateEObjectErrors();
				}
			}
		});

		btnExpressionLoc = new Button(cLoc, SWT.RADIO);
		btnExpressionLoc.setBounds(200, 5, 85, 20);
		btnExpressionLoc.setText("Expression:");
		btnExpressionLoc.setSelection(!btnRndLoc.getSelection());
		btnExpressionLoc.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				textLoc.setEnabled(true);
				if ( textLocUpdate.isSaveData() ) {
					save("location");
					ModelGenerator.modelValidation(fp, diagram);
					diagramEditor.updateEObjectErrors();
				}
			}
		});

		textLoc = new ValidateText(cLoc, SWT.BORDER, diagram, fp, frame, diagramEditor, "", null, "location");

		textLoc.setBounds(300, 5, 270, 20);
		textLoc.setEnabled(false);

		return cLoc;
	}

	public Canvas canvasGrid(final Composite container) {
		// ****** CANVAS GRID *********
		ESpecies species = (ESpecies) eobject;
		final GamaDiagramEditor diagramEditor = (GamaDiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();

		EGridTopology gridTopo = (EGridTopology) species.getTopology();
		Canvas canvasGrid = new Canvas(container, SWT.BORDER);
		canvasGrid.setBounds(10, 130, 720, 80);
		// Location
		CLabel lblgrid = new CLabel(canvasGrid, SWT.NONE);
		lblgrid.setBounds(10, 5, 150, 20);
		lblgrid.setText("Grid properties");
		lblgrid.setFont(titleFont);

		CLabel lblneigh = new CLabel(canvasGrid, SWT.NONE);
		lblneigh.setBounds(10, 25, 100, 20);
		lblneigh.setText("Neighborhood");

		comboNeighborhood = new CCombo(canvasGrid, SWT.BORDER);
		comboNeighborhood.setBounds(110, 25, 200, 20);
		comboNeighborhood.setItems(type_neighborhood);
		comboNeighborhood.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if ( comboNeighborhood.getText().equals("expression") ) {
					textNeighborhood.setEnabled(true);
				} else {
					textNeighborhood.setEnabled(false);
				}
				if ( textNeighborhood.isSaveData() ) {
					save("neighbours:");
					ModelGenerator.modelValidation(fp, diagram);
					diagramEditor.updateEObjectErrors();
				}
			}
		});
		// textNeighborhood = new Text(canvasGrid, SWT.BORDER);
		textNeighborhood =
			new ValidateText(canvasGrid, SWT.BORDER, diagram, fp, frame, diagramEditor, "neighbours:", null, null);

		textNeighborhood.setBounds(330, 25, 220, 20);
		textNeighborhood.setEnabled(false);

		CLabel lblrow = new CLabel(canvasGrid, SWT.NONE);
		lblrow.setBounds(10, 50, 100, 20);
		lblrow.setText("Number of rows");
		// textNbCols = new Text(canvasGrid, SWT.BORDER);
		textNbCols = new ValidateText(canvasGrid, SWT.BORDER, diagram, fp, frame, diagramEditor, "height:", null, null);

		textNbCols.setBounds(110, 50, 150, 20);

		CLabel lblcol = new CLabel(canvasGrid, SWT.NONE);
		lblcol.setBounds(300, 50, 120, 20);
		lblcol.setText("Number of columns");
		// textNbRows = new Text(canvasGrid, SWT.BORDER);
		textNbRows = new ValidateText(canvasGrid, SWT.BORDER, diagram, fp, frame, diagramEditor, "width:", null, null);
		textNbRows.setBounds(430, 50, 180, 20);

		if ( gridTopo.getNb_columns() != null ) {
			textNbCols.setText(gridTopo.getNb_columns());
		} else {
			textNbCols.setText("100");
		}
		if ( gridTopo.getNb_rows() != null ) {
			textNbRows.setText(gridTopo.getNb_rows());
		} else {
			textNbRows.setText("100");
		}
		if ( gridTopo.getNeighbourhood() != null ) {
			textNeighborhood.setText(gridTopo.getNeighbourhood());
		} else {
			textNeighborhood.setText("");
		}
		if ( gridTopo.getNeighbourhoodType() != null && !gridTopo.getNeighbourhoodType().isEmpty() ) {
			comboNeighborhood.setText(gridTopo.getNeighbourhoodType());
			if ( comboNeighborhood.getText().equals("expression") ) {
				textNeighborhood.setEnabled(true);
			} else {
				textNeighborhood.setEnabled(false);
			}
		} else {
			comboNeighborhood.setText(type_neighborhood[0]);

		}
		textNeighborhood.setSaveData(true);
		textNbRows.setSaveData(true);
		textNbCols.setSaveData(true);

		textName.getLinkedVts().add(textNbCols);
		textName.getLinkedVts().add(textNbRows);
		textName.getLinkedVts().add(textNeighborhood);

		return canvasGrid;
	}

	public Canvas canvasBounds(final Composite container) {
		// ****** CANVAS BOUNDS *********
		EWorldAgent world = (EWorldAgent) eobject;
		final GamaDiagramEditor diagramEditor = (GamaDiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();

		Canvas canvasBounds = new Canvas(container, SWT.BORDER);
		canvasBounds.setBounds(10, 130, 720, 80);

		CLabel lblbounds = new CLabel(canvasBounds, SWT.NONE);
		lblbounds.setBounds(10, 5, 100, 20);
		lblbounds.setText("Bounds");
		lblbounds.setFont(titleFont);

		CLabel lblval = new CLabel(canvasBounds, SWT.NONE);
		lblval.setBounds(10, 25, 80, 20);
		lblval.setText("Value type");

		final Composite widthHeightComp = new Composite(canvasBounds, SWT.NONE);
		widthHeightComp.setBounds(60, 50, 720, 20);
		widthHeightComp.setEnabled(true);
		widthHeightComp.setVisible(true);
		CLabel lblwidth = new CLabel(widthHeightComp, SWT.NONE);
		lblwidth.setBounds(10, 0, 60, 20);
		lblwidth.setText("Width");
		textBoundsWidth =
			new ValidateText(widthHeightComp, SWT.BORDER, diagram, fp, frame, diagramEditor, "", null, "shape");
		textBoundsWidth.setBounds(80, 0, 150, 20);

		CLabel lblHeight = new CLabel(widthHeightComp, SWT.NONE);
		lblHeight.setBounds(260, 0, 60, 20);
		lblHeight.setText("Height");
		textBoundsHeight =
			new ValidateText(widthHeightComp, SWT.BORDER, diagram, fp, frame, diagramEditor, "", null, "shape");
		textBoundsHeight.setBounds(330, 0, 150, 20);

		final Composite fileComp = new Composite(canvasBounds, SWT.NONE);
		fileComp.setBounds(60, 50, 720, 20);
		fileComp.setEnabled(false);
		fileComp.setVisible(false);
		CLabel lblPath = new CLabel(fileComp, SWT.NONE);
		lblPath.setBounds(10, 0, 100, 20);
		lblPath.setText("Path");
		textBoundsFile = new ValidateText(fileComp, SWT.BORDER, diagram, fp, frame, diagramEditor, "", null, "shape");
		textBoundsFile.setBounds(110, 0, 150, 20);
		textBoundsFile.setString(true);

		final Composite expComp = new Composite(canvasBounds, SWT.NONE);
		expComp.setBounds(60, 50, 720, 20);
		expComp.setEnabled(false);
		expComp.setVisible(false);
		CLabel lblExp = new CLabel(expComp, SWT.NONE);
		lblExp.setBounds(10, 0, 100, 20);
		lblExp.setText("Expression");
		textBoundsExpression =
			new ValidateText(expComp, SWT.BORDER, diagram, fp, frame, diagramEditor, "", null, "shape");

		textBoundsExpression.setBounds(110, 0, 150, 20);

		comboBounds = new CCombo(canvasBounds, SWT.BORDER);
		comboBounds.setBounds(90, 25, 200, 20);
		comboBounds.setItems(type_bounds);
		comboBounds.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if ( comboBounds.getText().equals("expression") ) {
					expComp.setEnabled(true);
					expComp.setVisible(true);
					fileComp.setEnabled(false);
					fileComp.setVisible(false);
					widthHeightComp.setEnabled(false);
					widthHeightComp.setVisible(false);
				} else if ( comboBounds.getText().equals("width-height") ) {
					expComp.setEnabled(false);
					expComp.setVisible(false);
					fileComp.setEnabled(false);
					fileComp.setVisible(false);
					widthHeightComp.setEnabled(true);
					widthHeightComp.setVisible(true);
				} else {
					expComp.setEnabled(false);
					expComp.setVisible(false);
					fileComp.setEnabled(true);
					fileComp.setVisible(true);
					widthHeightComp.setEnabled(false);
					widthHeightComp.setVisible(false);
				}
				if ( textBoundsExpression.isSaveData() ) {
					save("shape");
					ModelGenerator.modelValidation(fp, diagram);
					diagramEditor.updateEObjectErrors();
				}
			}
		});

		if ( world.getBoundsType() != null && !world.getBoundsType().isEmpty() ) {
			comboBounds.setText(world.getBoundsType());
			if ( comboBounds.getText().equals("expression") ) {
				expComp.setEnabled(true);
				expComp.setVisible(true);
				fileComp.setEnabled(false);
				fileComp.setVisible(false);
				widthHeightComp.setEnabled(false);
				widthHeightComp.setVisible(false);
			} else if ( comboBounds.getText().equals("width-height") ) {
				expComp.setEnabled(false);
				expComp.setVisible(false);
				fileComp.setEnabled(false);
				fileComp.setVisible(false);
				widthHeightComp.setEnabled(true);
				widthHeightComp.setVisible(true);
			} else {
				expComp.setEnabled(false);
				expComp.setVisible(false);
				fileComp.setEnabled(true);
				fileComp.setVisible(true);
				widthHeightComp.setEnabled(false);
				widthHeightComp.setVisible(false);
			}
		} else {
			comboBounds.setText(type_bounds[0]);
		}
		if ( world.getBoundsExpression() != null ) {
			textBoundsExpression.setText(world.getBoundsExpression());
		} else {
			textBoundsExpression.setText("");
		}
		if ( world.getBoundsHeigth() != null ) {
			textBoundsHeight.setText(world.getBoundsHeigth());
		} else {
			textBoundsHeight.setText("100.0");
		}
		if ( world.getBoundsWidth() != null ) {
			textBoundsWidth.setText(world.getBoundsWidth());
		} else {
			textBoundsWidth.setText("100.0");
		}
		if ( world.getBoundsPath() != null ) {
			textBoundsFile.setText(world.getBoundsPath());
		} else {
			textBoundsFile.setText("../includes/shapefile.shp");
		}

		textName.getLinkedVts().add(textBoundsExpression);
		textName.getLinkedVts().add(textBoundsHeight);
		textName.getLinkedVts().add(textBoundsWidth);
		textName.getLinkedVts().add(textBoundsFile);

		textBoundsExpression.setSaveData(true);
		textBoundsHeight.setSaveData(true);
		textBoundsWidth.setSaveData(true);
		textBoundsFile.setSaveData(true);

		return canvasBounds;
	}

	public Canvas canvasLocation(final Composite container) {
		// ****** CANVAS LOCATION *********
		ESpecies species = (ESpecies) eobject;
		Canvas canvasLocation = new Canvas(container, SWT.BORDER);
		canvasLocation.setBounds(10, 130, 720, 130);
		// Location
		CLabel lblLocation = new CLabel(canvasLocation, SWT.NONE);
		lblLocation.setBounds(10, 5, 80, 20);
		lblLocation.setText("Location");
		lblLocation.setFont(titleFont);
		final Composite cLoc = initValueLocComp(canvasLocation, species);

		CLabel lblUpdate = new CLabel(canvasLocation, SWT.NONE);
		lblUpdate.setBounds(5, 100, 60, 20);
		lblUpdate.setText("Update");

		final GamaDiagramEditor diagramEditor = (GamaDiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();

		// textLocUpdate = new Text(canvasLocation, SWT.BORDER);
		textLocUpdate =
			new ValidateText(canvasLocation, SWT.BORDER, diagram, fp, frame, diagramEditor, "", null, "location");

		textLocUpdate.setBounds(70, 100, 300, 20);

		btnLocNormal = new Button(canvasLocation, SWT.RADIO);
		btnLocNormal.setBounds(5, 30, 100, 20);
		btnLocNormal.setText("Normal");

		btnLocNormal.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				cLoc.setEnabled(true);
				cLoc.setVisible(true);
				textLocUpdate.setEnabled(true);
				textLocFunction.setEnabled(false);
				if ( textLocUpdate.isSaveData() ) {
					save("location");
					ModelGenerator.modelValidation(fp, diagram);
					diagramEditor.updateEObjectErrors();
				}
			}
		});

		btnLocFct = new Button(canvasLocation, SWT.RADIO);
		btnLocFct.setBounds(150, 30, 80, 20);
		btnLocFct.setText("Function");
		btnLocFct.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				cLoc.setEnabled(false);
				cLoc.setVisible(false);
				textLocUpdate.setEnabled(false);
				textLocFunction.setEnabled(true);
				if ( textLocUpdate.isSaveData() ) {
					save("location");
					ModelGenerator.modelValidation(fp, diagram);
					diagramEditor.updateEObjectErrors();
				}
			}
		});
		// textLocFunction = new Text(canvasLocation, SWT.BORDER);
		textLocFunction =
			new ValidateText(canvasLocation, SWT.BORDER, diagram, fp, frame, diagramEditor, "", null, "location");

		textLocFunction.setBounds(240, 30, 300, 18);
		textLocFunction.setEnabled(false);

		if ( species.getLocationType() == null || species.getLocationType().isEmpty() ) {
			btnRndLoc.setSelection(true);
		} else {
			textLoc.setText(species.getExpressionLoc());
			if ( species.getLocationType().equals("random") ) {
				btnRndLoc.setSelection(true);
			} else {
				btnExpressionLoc.setSelection(true);
				textLoc.setEnabled(true);
			}
		}

		if ( species.getLocationIsFunction() != null && species.getLocationIsFunction() ) {
			btnLocFct.setSelection(true);
			btnLocNormal.setSelection(false);
			cLoc.setEnabled(false);
			cLoc.setVisible(false);
			textLocUpdate.setEnabled(false);
			textLocFunction.setEnabled(true);
		} else {
			btnLocNormal.setSelection(true);
			btnLocFct.setSelection(false);
			cLoc.setEnabled(true);
			cLoc.setVisible(true);
			textLocUpdate.setEnabled(true);
			textLocFunction.setEnabled(false);
		}

		if ( species.getLocationFunction() != null && !species.getLocationFunction().isEmpty() ) {
			textLocFunction.setText(species.getLocationFunction());
		}
		if ( species.getLocationUpdate() != null && !species.getLocationUpdate().isEmpty() ) {
			textLocUpdate.setText(species.getLocationUpdate());
		}
		textLocFunction.setSaveData(true);
		textLocUpdate.setSaveData(true);
		textLoc.setSaveData(true);
		textName.getLinkedVts().add(textLocFunction);
		textName.getLinkedVts().add(textLocUpdate);
		textName.getLinkedVts().add(textLoc);

		return canvasLocation;
	}

	public Canvas canvasSkills(final Composite container) {
		// ****** CANVAS SKILLS *********
		final GamaDiagramEditor diagramEditor = (GamaDiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();

		Canvas canvasSkills = new Canvas(container, SWT.BORDER);
		canvasSkills.setBounds(10, 460, 720, 100);

		CLabel lblSkills = new CLabel(canvasSkills, SWT.NONE);
		lblSkills.setBounds(5, 5, 100, 20);
		lblSkills.setText("Skills");
		lblSkills.setFont(titleFont);

		CLabel lblAvSkills = new CLabel(canvasSkills, SWT.NONE);
		lblAvSkills.setBounds(5, 23, 100, 20);
		lblAvSkills.setText("Available Skills");

		CLabel lblSelectSkills = new CLabel(canvasSkills, SWT.NONE);
		lblSelectSkills.setBounds(325, 23, 100, 20);
		lblSelectSkills.setText("Selected Skills");

		final org.eclipse.swt.widgets.List listAvSkills =
			new org.eclipse.swt.widgets.List(canvasSkills, SWT.BORDER | SWT.V_SCROLL);
		listAvSkills.setBounds(5, 45, 250, 45);
		for ( String ski : skillsStrs ) {
			listAvSkills.add(ski);
		}
		skillsViewer = new org.eclipse.swt.widgets.List(canvasSkills, SWT.BORDER | SWT.V_SCROLL);
		skillsViewer.setBounds(325, 45, 250, 45);

		Button btnRigth = new Button(canvasSkills, SWT.ARROW | SWT.RIGHT);
		btnRigth.setBounds(265, 50, 50, 15);
		btnRigth.setSelection(true);
		btnRigth.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if ( listAvSkills.getSelectionCount() > 0 ) {
					String[] els = listAvSkills.getSelection();
					for ( String el : els ) {
						skillsViewer.add(el);
					}
					listAvSkills.remove(listAvSkills.getSelectionIndices());
					listAvSkills.redraw();
					skillsViewer.redraw();
					save("skills");
					ModelGenerator.modelValidation(fp, diagram);
					diagramEditor.updateEObjectErrors();
				}
			}
		});
		Button btnLeft = new Button(canvasSkills, SWT.ARROW | SWT.LEFT);
		btnLeft.setBounds(265, 70, 50, 15);
		btnLeft.setSelection(true);
		btnLeft.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if ( skillsViewer.getSelectionCount() > 0 ) {
					String[] els = skillsViewer.getSelection();
					for ( String el : els ) {
						listAvSkills.add(el);
					}
					skillsViewer.remove(skillsViewer.getSelectionIndices());
					listAvSkills.redraw();
					skillsViewer.redraw();
					save("skills");
					ModelGenerator.modelValidation(fp, diagram);
					diagramEditor.updateEObjectErrors();
				}
			}
		});
		if ( ((ESpecies) eobject).getSkills() != null ) {
			for ( String sk : ((ESpecies) eobject).getSkills() ) {
				skillsStrs.remove(sk);
				skillsViewer.add(sk);
				listAvSkills.remove(sk);

			}
			skillsViewer.redraw();
			listAvSkills.redraw();

		}
		return canvasSkills;
	}

	protected Canvas canvasInit(final Composite container) {

		// ****** CANVAS INIT *********
		Canvas canvasInit = new Canvas(container, SWT.BORDER);
		canvasInit.setBounds(10, 515, 720, 150);
		GamaDiagramEditor diagramEditor = (GamaDiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();

		textInit = new ValidateStyledText(canvasInit, SWT.BORDER, diagram, fp, this, diagramEditor, "init", null);
		textName.getLinkedVsts().add((ValidateStyledText) textInit);
		textInit.setBounds(5, 30, 700, 110);
		if ( ((ESpecies) eobject).getInit() != null ) {
			textInit.setText(((ESpecies) eobject).getInit());
		}
		textInit.setEditable(true);
		((ValidateStyledText) textInit).setSaveData(true);

		CLabel lblCompilation = new CLabel(canvasInit, SWT.NONE);
		lblCompilation.setText("Init block");
		lblCompilation.setBounds(5, 5, 70, 20);
		lblCompilation.setFont(titleFont);
		return canvasInit;
	}

	public Canvas canvasReflex(final Composite container) {
		// ****** CANVAS REFLEX ORDER *********
		Canvas canvasReflexOrder = new Canvas(container, SWT.BORDER);
		canvasReflexOrder.setBounds(10, 460, 720, 110);

		reflexViewer = new org.eclipse.swt.widgets.List(canvasReflexOrder, SWT.BORDER | SWT.V_SCROLL);

		for ( String ref : reflexStrs ) {
			reflexViewer.add(ref);
		}

		reflexViewer.setBounds(5, 30, 700, 45);
		CLabel lblReflexOrder = new CLabel(canvasReflexOrder, SWT.NONE);
		lblReflexOrder.setBounds(5, 5, 100, 20);
		lblReflexOrder.setText("Reflex order");
		lblReflexOrder.setFont(titleFont);

		Button btnUp = new Button(canvasReflexOrder, SWT.ARROW | SWT.UP);
		btnUp.setBounds(80, 85, 105, 20);
		btnUp.setText("Up");
		btnUp.setSelection(true);
		btnUp.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if ( reflexViewer.getSelectionCount() == 1 ) {
					String el = reflexViewer.getSelection()[0];
					int index = reflexViewer.getSelectionIndex();
					if ( index > 0 ) {
						reflexStrs.remove(el);
						reflexStrs.add(index - 1, el);
						reflexViewer.removeAll();
						for ( String ref : reflexStrs ) {
							reflexViewer.add(ref);
						}
					}
				}
			}
		});
		Button btnDown = new Button(canvasReflexOrder, SWT.ARROW | SWT.DOWN);
		btnDown.setBounds(200, 85, 105, 20);
		btnDown.setText("Down");
		btnDown.setSelection(true);
		btnDown.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if ( reflexViewer.getSelectionCount() == 1 ) {
					String el = reflexViewer.getSelection()[0];
					int index = reflexViewer.getSelectionIndex();
					if ( index < reflexViewer.getItemCount() - 1 ) {
						reflexStrs.remove(el);
						reflexStrs.add(index + 1, el);
						reflexViewer.removeAll();
						for ( String ref : reflexStrs ) {
							reflexViewer.add(ref);
						}

					}
				}
			}
		});
		return canvasReflexOrder;
	}

	@Override
	protected void save(final String name) {
		final ESpecies species = (ESpecies) eobject;
		TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(eobject);
		if ( domain != null ) {
			domain.getCommandStack().execute(new RecordingCommand(domain) {

				@Override
				public void doExecute() {
					// System.out.println("totot name: " + name);

					if ( name.equals("name") ) {
						eobject.setName(textName.getText());
					} else if ( name.equals("init") ) {
						((ESpecies) eobject).setInit(textInit.getText());
					} else if ( name.equals("reflex order") ) {
						modifyReflexOrder();

					} else if ( name.equals("variables") ) {
						modifyVariables();
					} else if ( name.equals("skills") ) {
						species.getSkills().clear();
						species.getSkills().addAll(Arrays.asList(skillsViewer.getItems()));
						// System.out.println("species: " + species);
					} else if ( name.equals("torus:") ) {
						modifyIsTorus();
					} else if ( name.equals("schedules:") ) {
						modifySchedules();
					} else if ( name.equals("width:") || name.equals("height:") || name.equals("neighbours:") ) {
						modifyGridProperties();
					} else {
						if ( eobject instanceof EWorldAgent ) {
							modifyBounds();
						} else if ( ((ESpecies) eobject).getTopology() instanceof EContinuousTopology ) {
							modifyLocation();
							// System.out.println("modify shape");
							modifyShape();
						}
					}

				}
			});
		}

		ef.hasDoneChanges = true;
		ModelGenerator.modelValidation(fp, diagram);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(CONST_WIDTH, 600);
	}

	@Override
	public void create() {
		setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE);
		super.create();
		shell = getShell();
		shell.addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(final ControlEvent e) {
				Rectangle rect = shell.getBounds();
				if ( rect.width != CONST_WIDTH ) {
					shell.setBounds(rect.x, rect.y, CONST_WIDTH, rect.height);
				}
			}
		});

	}

}
