package idees.gama.ui.editFrame;

import gama.*;
import idees.gama.diagram.GamaDiagramEditor;
import idees.gama.features.edit.EditFeature;
import idees.gama.features.modelgeneration.ModelGenerator;
import java.util.*;
import java.util.List;
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

public class EditExperimentFrame extends EditFrame {

	StyledText gamlCode;
	ESpecies species;
	Table table_params;
	Table table_monitors;
	List<String> variables;
	List<String> types_parameter_tot = Arrays.asList("int", "float", "string", "file", "list", "matrix", "map", "bool");
	Diagram diagram;

	/**
	 * Create the application window.
	 */
	public EditExperimentFrame(final Diagram diagram, final IFeatureProvider fp, final EditFeature eaf,
		final EExperiment experiment, final String name) {
		super(diagram, fp, eaf, experiment, name == null ? "Experiment definition" : name);
		this.species = experiment.getExperimentLink().getSpecies();
		this.diagram = diagram;
	}

	/**
	 * Create contents of the application window.
	 * @param parent
	 */
	@Override
	protected Control createContents(final Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		variables = new ArrayList<String>();
		for ( EVariable var : species.getVariables() ) {
			if ( types_parameter_tot.contains(var.getType()) ) {
				variables.add(var.getName());
			}
		}

		// ****** CANVAS NAME *********
		canvasName(container);

		// ****** CANVAS PARAMETER *********
		Canvas canvasParameter = canvasParameter(container);
		canvasParameter.setBounds(10, 50, 820, 205);

		// ****** CANVAS MONITORS *********
		Canvas canvasMonitors = canvasMonitor(container);
		canvasMonitors.setBounds(10, 270, 820, 205);

		return container;
	}

	protected Canvas canvasParameter(final Composite container) {
		final GamaDiagramEditor diagramEditor = (GamaDiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();

		// ****** CANVAS PARAMETER *********
		Canvas canvasParameter = new Canvas(container, SWT.BORDER);
		canvasParameter.setBounds(10, 515, 820, 205);

		CLabel lblCompilation = new CLabel(canvasParameter, SWT.NONE);
		lblCompilation.setText("Parameters");
		lblCompilation.setBounds(5, 5, 70, 20);

		table_params = createTableEditor(canvasParameter);
		table_params.setBounds(10, 30, 800, 130);
		table_params.setHeaderVisible(true);
		table_params.setLinesVisible(true);
		table_params.setLinesVisible(true);
		initTableParam();

		CLabel lblVariables = new CLabel(canvasParameter, SWT.NONE);
		lblVariables.setBounds(10, 5, 100, 20);
		lblVariables.setText("Parameters");
		Button btnAddVariable = new Button(canvasParameter, SWT.NONE);
		btnAddVariable.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if ( variables == null || variables.isEmpty() ) { return; }
				TableItem ti = new TableItem(table_params, SWT.NONE);
				final String var = variables.get(0);
				ti.setText(new String[] { var, var, "", "", "", "", "" });
				ti.setBackground(new Color(frame.getShell().getDisplay(), 100, 255, 100));
				List<String> locs = new ArrayList(textName.getLoc());
				locs.add(var);
				diagramEditor.getIdsEObjects().put(locs, eobject);
				save("variables");
				ModelGenerator.modelValidation(fp, diagram);
				diagramEditor.updateEObjectErrors();
				final ValidateText text =
					new ValidateText(table_params, SWT.BORDER, diagram, fp, frame, diagramEditor, "", null, var);
				ti.setBackground(3, text.getBackground());
				text.dispose();

				table_params.redraw();
			}
		});
		btnAddVariable.setBounds(50, 175, 130, 20);
		btnAddVariable.setText("Add parameter");

		Button btnDeleteVariable = new Button(canvasParameter, SWT.NONE);
		btnDeleteVariable.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				int[] indices = table_params.getSelectionIndices();
				for ( int i : indices ) {
					List<String> locs = new ArrayList<String>(textName.getLoc());
					locs.add(table_params.getItem(i).getText(0));
					diagramEditor.getIdsEObjects().remove(locs);
				}
				table_params.remove(indices);
				save("variables");
				ModelGenerator.modelValidation(fp, diagram);
				diagramEditor.updateEObjectErrors();
				table_params.redraw();

			}
		});
		btnDeleteVariable.setBounds(220, 175, 130, 20);
		btnDeleteVariable.setText("Delete parameter");
		return canvasParameter;
	}

	protected Canvas canvasMonitor(final Composite container) {
		final GamaDiagramEditor diagramEditor = (GamaDiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();

		// ****** CANVAS Monitor *********
		Canvas canvasMonitor = new Canvas(container, SWT.BORDER);
		canvasMonitor.setBounds(10, 415, 820, 205);

		CLabel lblCompilation = new CLabel(canvasMonitor, SWT.NONE);
		lblCompilation.setText("Monitors");
		lblCompilation.setBounds(5, 5, 70, 20);

		table_monitors = createTableEditorMonitors(canvasMonitor);
		table_monitors.setBounds(10, 30, 800, 130);
		table_monitors.setHeaderVisible(true);
		table_monitors.setLinesVisible(true);
		table_monitors.setLinesVisible(true);
		initTableMonitor();

		CLabel lblVariables = new CLabel(canvasMonitor, SWT.NONE);
		lblVariables.setBounds(10, 5, 100, 20);
		lblVariables.setText("Monitors");
		Button btnAddVariable = new Button(canvasMonitor, SWT.NONE);
		btnAddVariable.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				TableItem ti = new TableItem(table_monitors, SWT.NONE);
				ti.setText(new String[] { "monitor", "0" });
				ti.setBackground(new Color(frame.getShell().getDisplay(), 100, 255, 100));
				List<String> locs = new ArrayList<String>(textName.getLoc());
				locs.add("monitor");
				diagramEditor.getIdsEObjects().put(locs, eobject);
				save("monitors");

				table_monitors.redraw();
			}
		});
		btnAddVariable.setBounds(50, 175, 130, 20);
		btnAddVariable.setText("Add monitor");

		Button btnDeleteVariable = new Button(canvasMonitor, SWT.NONE);
		btnDeleteVariable.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				int[] indices = table_monitors.getSelectionIndices();
				for ( int i : indices ) {
					List<String> locs = new ArrayList<String>(textName.getLoc());
					locs.add(table_monitors.getItem(i).getText(0));
					diagramEditor.getIdsEObjects().remove(locs);
				}
				table_monitors.remove(indices);
				save("monitors");
				ModelGenerator.modelValidation(fp, diagram);
				diagramEditor.updateEObjectErrors();
				table_monitors.redraw();

			}
		});
		btnDeleteVariable.setBounds(220, 175, 130, 20);
		btnDeleteVariable.setText("Delete monitor");
		return canvasMonitor;
	}

	void initTableParam() {
		for ( EParameter var : ((EExperiment) eobject).getParameters() ) {
			TableItem ti = new TableItem(table_params, SWT.NONE);
			ti.setText(new String[] { var.getVariable(), var.getName(), var.getCategory(), var.getInit(), var.getMin(),
				var.getMax(), var.getStep(), var.getAmong() });
		}
	}

	void initTableMonitor() {
		for ( EMonitor mon : ((EExperiment) eobject).getMonitors() ) {
			TableItem ti = new TableItem(table_monitors, SWT.NONE);
			ti.setText(new String[] { mon.getName(), mon.getValue() });
		}
	}

	/**
	 * Creates the main window's contents
	 * 
	 * @param shell the main window
	 */
	private Table createTableEditor(final Composite container) {
		// Create the table
		final GamaDiagramEditor diagramEditor = (GamaDiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();

		final Table tableVars = new Table(container, SWT.SINGLE | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		tableVars.setHeaderVisible(true);
		tableVars.setLinesVisible(true);

		tableVars.addListener(SWT.MeasureItem, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				event.height = 20;
			}
		});

		TableColumn tblclmnVar = new TableColumn(tableVars, SWT.NONE);
		tblclmnVar.setWidth(100);
		tblclmnVar.setText("variable");

		TableColumn tblclmnText = new TableColumn(tableVars, SWT.NONE);
		tblclmnText.setWidth(100);
		tblclmnText.setText("text");

		TableColumn tblclmnCategory = new TableColumn(tableVars, SWT.NONE);
		tblclmnCategory.setWidth(100);
		tblclmnCategory.setText("category");

		TableColumn tblclmnInitValue = new TableColumn(tableVars, SWT.NONE);
		tblclmnInitValue.setWidth(100);
		tblclmnInitValue.setText("init value");

		TableColumn tblclmnMin = new TableColumn(tableVars, SWT.NONE);
		tblclmnMin.setWidth(100);
		tblclmnMin.setText("min");

		TableColumn tblclmnMax = new TableColumn(tableVars, SWT.NONE);
		tblclmnMax.setWidth(100);
		tblclmnMax.setText("max");

		TableColumn tblclmnStep = new TableColumn(tableVars, SWT.NONE);
		tblclmnStep.setWidth(100);
		tblclmnStep.setText("step");

		TableColumn tblclmnAmong = new TableColumn(tableVars, SWT.NONE);
		tblclmnAmong.setWidth(100);
		tblclmnAmong.setText("among");

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

					if ( column == 0 ) {
						// Create the dropdown and add data to it
						final CCombo combo = new CCombo(tableVars, SWT.READ_ONLY);
						for ( int i = 0, n = variables.size(); i < n; i++ ) {
							combo.add(variables.get(i));
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
								List<String> locs = new ArrayList<String>(textName.getLoc());
								locs.add(item.getText(col));
								diagramEditor.getIdsEObjects().remove(locs);

								item.setText(col, combo.getText());

								locs = new ArrayList<String>(textName.getLoc());
								locs.add(item.getText(col));
								diagramEditor.getIdsEObjects().put(locs, eobject);

								save("variables");
								ModelGenerator.modelValidation(fp, diagram);
								diagramEditor.updateEObjectErrors();
								// They selected an item; end the editing session
								combo.dispose();

							}
						});
					} else {
						// Create the Text object for our editor
						final GamaDiagramEditor diagramEditor =
							(GamaDiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();
						String name = "";
						switch (column) {
							case 1:
								name = "legend:";
								break;
							case 2:
								name = "category:";
								break;
							case 3:
								name = "";
								break;
							case 4:
								name = "min:";
								break;
							case 5:
								name = "max:";
								break;
							case 6:
								name = "step:";
								break;
							case 7:
								name = "among:";
								break;
						}
						final ValidateText text =
							new ValidateText(tableVars, SWT.BORDER, diagram, fp, frame, diagramEditor, name, null, item
								.getText(0));
						text.setString(column == 1 || column == 2);
						item.setBackground(column, text.getBackground());

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
								save("parameters");
								text.applyModification();
								if ( diagramEditor.getErrorsLoc().isEmpty() &&
									diagramEditor.getSyntaxErrorsLoc().isEmpty() ) {
									item.setBackground(text.getBackground());
								} else {
									item.setBackground(col, text.getBackground());
								}

								item.setBackground(col, text.getBackground());
								for ( int i = 1; i < table_params.getColumnCount(); i++ ) {
									if ( i == col ) {
										continue;
									}
									String name = "";
									switch (i) {
										case 1:
											name = "legend:";
											break;
										case 2:
											name = "category:";
											break;
										case 3:
											name = "<-";
											break;
										case 4:
											name = "min:";
											break;
										case 5:
											name = "max:";
											break;
										case 6:
											name = "step:";
											break;
										case 7:
											name = "among:";
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
				} else {
					tableVars.deselectAll();
				}
			}
		});
		return tableVars;
	}

	private Table createTableEditorMonitors(final Composite container) {
		// Create the table

		final Table tableVars = new Table(container, SWT.SINGLE | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		tableVars.setHeaderVisible(true);
		tableVars.setLinesVisible(true);

		tableVars.addListener(SWT.MeasureItem, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				event.height = 20;
			}
		});

		TableColumn tblclmnVar = new TableColumn(tableVars, SWT.NONE);
		tblclmnVar.setWidth(400);
		tblclmnVar.setText("text");

		TableColumn tblclmnText = new TableColumn(tableVars, SWT.NONE);
		tblclmnText.setWidth(400);
		tblclmnText.setText("value");

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
					final GamaDiagramEditor diagramEditor =
						(GamaDiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();

					final ValidateText text =
						new ValidateText(tableVars, SWT.BORDER, diagram, fp, frame, diagramEditor, "", null, item
							.getText(0));
					text.setString(column == 0);
					item.setBackground(column, text.getBackground());

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
							save("monitors");
							text.applyModification();
							item.setBackground(col, text.getBackground());

						}
					});

				} else {
					tableVars.deselectAll();
				}
			}
		});
		return tableVars;
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(850, 550);
	}

	@Override
	protected void save(final String name) {
		TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(eobject);
		if ( domain != null ) {
			domain.getCommandStack().execute(new RecordingCommand(domain) {

				@Override
				public void doExecute() {
					eobject.setName(textName.getText());
					EExperiment xp = (EExperiment) eobject;
					List<EParameter> params = new ArrayList<EParameter>();
					params.addAll(xp.getParameters());
					xp.getParameters().clear();
					for ( EParameter par : params ) {
						diagram.eResource().getContents().remove(par);
						EcoreUtil.delete(par, true);
					}
					List<EMonitor> mons = new ArrayList<EMonitor>();
					mons.addAll(xp.getMonitors());
					xp.getMonitors().clear();
					for ( EMonitor mon : mons ) {
						diagram.eResource().getContents().remove(mon);
						EcoreUtil.delete(mon, true);
					}
					for ( final TableItem item : table_params.getItems() ) {
						final EParameter par = gama.GamaFactory.eINSTANCE.createEParameter();
						diagram.eResource().getContents().add(par);
						par.setVariable(item.getText(0));
						par.setName(item.getText(1));
						par.setCategory(item.getText(2));
						par.setInit(item.getText(3));
						par.setMin(item.getText(4));
						par.setMax(item.getText(5));
						par.setStep(item.getText(6));
						par.setAmong(item.getText(7));
						xp.getParameters().add(par);
					}
					for ( final TableItem item : table_monitors.getItems() ) {
						final EMonitor mon = gama.GamaFactory.eINSTANCE.createEMonitor();
						diagram.eResource().getContents().add(mon);
						mon.setName(item.getText(0));
						mon.setValue(item.getText(1));
						xp.getMonitors().add(mon);
					}
				}

			});
		}

		ef.hasDoneChanges = true;
		ModelGenerator.modelValidation(fp, diagram);
	}

	@Override
	public void create() {
		setShellStyle(SWT.DIALOG_TRIM);
		super.create();
		shell = getShell();

	}

}
