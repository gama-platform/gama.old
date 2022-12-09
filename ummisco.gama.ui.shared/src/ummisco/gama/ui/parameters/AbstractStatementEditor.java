/*******************************************************************************************************
 *
 * AbstractStatementEditor.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.parameters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import msi.gama.common.interfaces.IColored;
import msi.gama.common.interfaces.INamed;
import msi.gama.kernel.experiment.InputParameter;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import ummisco.gama.ui.controls.FlatButton;
import ummisco.gama.ui.interfaces.EditorListener;
import ummisco.gama.ui.resources.GamaColors;

/**
 * The Class AbstractStatementEditor.
 *
 * @param <T>
 *            the generic type
 */
public abstract class AbstractStatementEditor<T extends INamed & IColored> extends AbstractEditor<Object> {

	protected FlatButton textBox;

	/** The statement. */
	T statement;

	/**
	 * Instantiates a new abstract statement editor.
	 *
	 * @param scope
	 *            the scope
	 * @param command
	 *            the command
	 * @param l
	 *            the l
	 */
	public AbstractStatementEditor(final IScope scope, final T command, final EditorListener<Object> l) {
		super(scope, null, new InputParameter(command.getName(), null), l);
		this.statement = command;
	}

	/**
	 * Gets the statement.
	 *
	 * @return the statement
	 */
	public T getStatement() { return statement; }

	@Override
	protected int[] getToolItems() { return new int[0]; }

	@Override
	protected final Object retrieveValueOfParameter(final boolean b) throws GamaRuntimeException {
		return null;
	}

	@Override
	Color getEditorControlBackground() {
		GamaColor color = getStatement().getColor(getScope());
		return color == null ? super.getEditorControlBackground() : GamaColors.get(color).color();
	}

	@Override
	protected abstract FlatButton createCustomParameterControl(Composite comp) throws GamaRuntimeException;

	@Override
	protected void updateToolbar() {}

	@Override
	protected final void displayParameterValue() {}

	@Override
	protected GridData getEditorControlGridData() {
		final var d = new GridData(SWT.FILL, SWT.CENTER, false, false);
		d.minimumWidth = 50;
		return d;
	}

}
