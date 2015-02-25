package idees.gama.ui.editFrame;

import gama.*;
import idees.gama.diagram.GamaDiagramEditor;
import idees.gama.features.edit.EditFeature;
import idees.gama.features.modelgeneration.ModelGenerator;
import java.util.*;
import java.util.List;
import msi.gaml.compilation.AbstractGamlAdditions;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.*;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class EditActionFrame extends EditFrame {

	StyledText gamlCode;
	private Table table_vars;
	int cpt = 1;
	private final List<String> types = new ArrayList<String>();
	Font titleFont;
	CCombo returnType;

	/**
	 * Create the application window.
	 */
	public EditActionFrame(final Diagram diagram, final IFeatureProvider fp, final EditFeature eaf,
		final EGamaObject action, final String name, final List<ESpecies> speciesList) {
		super(diagram, fp, eaf, action, name == null ? "Action definition" : name);

		for ( Collection varType : AbstractGamlAdditions.VARTYPE2KEYWORDS.values() ) {
			types.addAll(varType);
		}
		for ( ESpecies sp : speciesList ) {
			types.add(sp.getName());
		}
		types.remove("unknown");
		types.remove("world");
	}

	public EditActionFrame(final Diagram diagram, final IFeatureProvider fp, final EditFeature eaf,
		final EGamaObject action, final String name) {
		super(diagram, fp, eaf, action, name == null ? "Action definition" : name);
	}

	/**
	 * Create contents of the application window.
	 * @param parent
	 */
	@Override
	protected Control createContents(final Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		titleFont = new Font(this.getShell().getDisplay(), "Arial", 10, SWT.BOLD);

		// ****** CANVAS NAME *********
		groupName(container);

		groupReturnType(container);

		groupVar(container);

		// ****** CANVAS GAMLCODE *********
		groupGamlCode(container);

		return container;
	}

	protected void groupReturnType(final Composite container) {
		final GamaDiagramEditor diagramEditor = (GamaDiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();

		// ****** CANVAS RETURN TYPE *********
		Group group = new Group(container, SWT.NONE);
		// group.setBounds(10, 50, 720, 50);
		// Group group = new Group(container, SWT.NONE);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		group.setLayoutData(gridData);

		group.setLayout(new GridLayout(2, false));

		CLabel lblName = new CLabel(group, SWT.NONE);
		lblName.setText("Return type:");

		// group.setLayout( new FillLayout(SWT.HORIZONTAL));
		group.setText("Return Type");

		returnType = new CCombo(group, SWT.READ_ONLY);
		returnType.add("returns nothing");
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = SWT.FILL;
		gridData2.grabExcessHorizontalSpace = true;
		returnType.setLayoutData(gridData2);

		for ( int i = 0, n = types.size(); i < n; i++ ) {
			returnType.add(types.get(i));
		}
		EAction action = (EAction) eobject;
		if ( action.getReturnType() == null || action.getReturnType().isEmpty() ) {
			returnType.setText("returns nothing");
		} else {
			returnType.setText(action.getReturnType());
		}
		returnType.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if ( textName.isSaveData() ) {
					// save("");
					// ModelGenerator.modelValidation(fp, diagram);
					// diagramEditor.updateEObjectErrors();
					((ValidateStyledText) gamlCode).applyModification();
				}
			}
		});

	}

	protected void groupGamlCode(final Composite container) {

		// ****** CANVAS GAMLCODE *********
		Group group = new Group(container, SWT.NONE);
		group.setBounds(10, 50, 720, 240);

		// group.setLayout( new FillLayout(SWT.HORIZONTAL));
		group.setText("Gaml code");

		/*
		 * GridData gridData = new GridData();
		 * gridData.horizontalAlignment = SWT.FILL;
		 * gridData.verticalAlignment = SWT.FILL;
		 * gridData.grabExcessHorizontalSpace = true;
		 * gridData.grabExcessVerticalSpace= true;
		 * group.setLayoutData(gridData);
		 * group.setLayout(new GridLayout(1, false));
		 * 
		 * GridData gridData2 = new GridData();
		 * gridData2.horizontalAlignment = SWT.FILL;
		 * gridData2.verticalAlignment = SWT.FILL;
		 * gridData2.grabExcessHorizontalSpace = true;
		 * gridData2.grabExcessVerticalSpace= true;
		 */

		GamaDiagramEditor diagramEditor = (GamaDiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();
		List<String> uselessName = new ArrayList<String>();
		uselessName.add("name");
		gamlCode = new ValidateStyledText(group, SWT.BORDER, diagram, fp, this, diagramEditor, "", uselessName);
		textName.getLinkedVsts().add((ValidateStyledText) gamlCode);
		// gamlCode.setLayoutData(gridData2);

		gamlCode.setBounds(5, 30, 700, 265);
		if ( ((EAction) eobject).getGamlCode() != null ) {
			gamlCode.setText(((EAction) eobject).getGamlCode());
		}
		gamlCode.setEditable(true);

		((ValidateStyledText) gamlCode).setSaveData(true);
		textName.getLinkedVsts().add((ValidateStyledText) gamlCode);
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(743, 680);
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
						EAction action = (EAction) eobject;
						action.setGamlCode(gamlCode == null ? "" : gamlCode.getText());
						action.setReturnType(returnType.getText().equals("returns nothing") ? "" : returnType.getText());
						modifyArguments();
					}
				}
			});
		}
		ModelGenerator.modelValidation(fp, diagram);
		ef.hasDoneChanges = true;

	}

	private void modifyArguments() {
		EAction action = (EAction) eobject;
		List<EVariable> vars = new ArrayList<EVariable>();
		vars.addAll(action.getVariables());
		action.getVariables().clear();
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
			action.getVariables().add(var);
			GamaDiagramEditor diagramEditor = (GamaDiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();
			diagramEditor.addEOject(var);

		}
	}

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
		tblclmnInitValue.setText("default value");

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
								save("arguments");
								// They selected an item; end the editing session
								combo.dispose();
							}
						});
					} else if ( column != 1 ) {
						// Create the Text object for our editor
						final GamaDiagramEditor diagramEditor =
							(GamaDiagramEditor) fp.getDiagramTypeProvider().getDiagramEditor();
						List<String> uselessName = new ArrayList<String>();

						String name = "name";
						switch (column) {
							case 2:
								name = "";
								uselessName.add("name");
								break;
						}

						final ValidateText text =
							new ValidateText(tableVars, SWT.BORDER, diagram, fp, frame, diagramEditor, name,
								uselessName, item.getText(0));
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
											name = "";
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
		for ( EVariable var : ((EAction) eobject).getVariables() ) {
			TableItem ti = new TableItem(table_vars, SWT.NONE);
			ti.setText(new String[] { var.getName(), var.getType(), var.getInit() });
			cpt++;
		}
	}

	public void groupVar(final Composite container) {
		// ****** CANVAS VARIABLES *********

		Group group = new Group(container, SWT.NONE);
		group.setBounds(10, 30, 720, 140);
		// group.setLayout( new FillLayout(SWT.HORIZONTAL));
		group.setText("arguments");

		table_vars = createTableEditor(group);
		table_vars.setBounds(10, 30, 700, 120);
		table_vars.setHeaderVisible(true);
		table_vars.setLinesVisible(true);

		initTable();

		CLabel lblVariables = new CLabel(group, SWT.NONE);
		lblVariables.setBounds(10, 5, 100, 20);
		lblVariables.setText("Arguments");
		lblVariables.setFont(titleFont);
		Button btnAddVariable = new Button(group, SWT.NONE);
		btnAddVariable.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				TableItem ti = new TableItem(table_vars, SWT.NONE);
				final String name = "arg" + cpt;
				ti.setText(new String[] { name, "int", "" });
				ti.setBackground(new Color(frame.getShell().getDisplay(), 100, 255, 100));
				cpt++;
				save("argument");
			}
		});
		btnAddVariable.setBounds(62, 158, 120, 28);
		btnAddVariable.setText("Add argument");

		Button btnDeleteVariable = new Button(group, SWT.NONE);
		btnDeleteVariable.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				int[] indices = table_vars.getSelectionIndices();
				table_vars.remove(indices);
				table_vars.redraw();
				save("variables");
			}
		});
		btnDeleteVariable.setBounds(183, 158, 130, 28);
		btnDeleteVariable.setText("Delete argument");

	}

}
