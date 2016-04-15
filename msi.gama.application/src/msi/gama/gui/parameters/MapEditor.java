/*********************************************************************************************
 * 
 * 
 * 'MapEditor.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.parameters;

import java.util.Map;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import msi.gama.common.interfaces.EditorListener;
import msi.gama.gui.swt.SwtGui;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaMap;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

public class MapEditor extends ExpressionBasedEditor<Map> {

	MapEditor(final IScope scope, final IParameter param) {
		super(scope, param);
	}

	MapEditor(final IScope scope, final IAgent agent, final IParameter param) {
		this(scope, agent, param, null);
	}

	MapEditor(final IScope scope, final IAgent agent, final IParameter param, final EditorListener l) {
		super(scope, agent, param, l);
	}

	MapEditor(final IScope scope, final Composite parent, final String title, final Map value,
		final EditorListener<Map> whenModified) {
		// Convenience method
		super(scope, new InputParameter(title, value), whenModified);
		this.createComposite(parent);
	}

	@Override
	public void applyEdit() {

		final MapEditorDialog mapParameterDialog =
			new MapEditorDialog(getScope(), SwtGui.getShell(), (GamaMap) currentValue);
		if ( mapParameterDialog.open() == IDialogConstants.OK_ID ) {
			modifyValue(mapParameterDialog.getMap());
		}
	}

	@Override
	public IType getExpectedType() {
		return Types.MAP;
	}

	@Override
	protected int[] getToolItems() {
		return new int[] { EDIT, REVERT };
	}

}
