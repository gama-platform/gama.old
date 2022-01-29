/*******************************************************************************************************
 *
 * AbstractStatementEditor.java, in ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.parameters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import ummisco.gama.ui.controls.FlatButton;
import ummisco.gama.ui.interfaces.EditorListener;

/**
 * The Class AbstractStatementEditor.
 *
 * @param <T> the generic type
 */
public abstract class AbstractStatementEditor<T> extends AbstractEditor<Object> {

	/** The text box. */
	protected FlatButton textBox;
	
	/** The statement. */
	T statement;

	/**
	 * Instantiates a new abstract statement editor.
	 *
	 * @param scope the scope
	 * @param command the command
	 * @param l the l
	 */
	public AbstractStatementEditor(final IScope scope, final T command, final EditorListener<Object> l) {
		super(scope, l);
		this.statement = command;
	}

	/**
	 * Gets the statement.
	 *
	 * @return the statement
	 */
	public T getStatement() {
		return statement;
	}

	/**
	 * Sets the statement.
	 *
	 * @param s the new statement
	 */
	public void setStatement(final T s) {
		statement = s;
	}

	@Override
	protected final int[] getToolItems() {
		return new int[0];
	}

	@Override
	protected final Object retrieveValueOfParameter() throws GamaRuntimeException {
		return null;
	}

	@Override
	protected void updateToolbar() {}

	@Override
	public void createControls(final EditorsGroup parent) {
		this.parent = parent;
		internalModification = true;
		// Create the label of the value editor
		editorLabel = createEditorLabel();
		// Create the composite that will hold the value editor and the toolbar
		createValueComposite();
		// Create and initialize the value editor
		editorControl = createEditorControl();

		if (isSubParameter) {
			editorLabel = new EditorLabel(this, composite, name, isSubParameter);
			editorLabel.setHorizontalAlignment(SWT.LEAD);
		}
		// Create and initialize the toolbar associated with the value editor
		editorToolbar = createEditorToolbar();
		internalModification = false;
		parent.layout();
	}

	@Override
	EditorLabel createEditorLabel() {
		if (!isSubParameter)
			return super.createEditorLabel();
		else
			return new EditorLabel(this, parent, " ", isSubParameter);
	}

	@Override
	protected final void displayParameterValue() {}

	@Override
	protected GridData getParameterGridData() {
		final var d = new GridData(SWT.FILL, SWT.CENTER, false, false);
		d.minimumWidth = 50;
		return d;
	}

}
