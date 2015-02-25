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
import msi.gama.common.interfaces.EditorListener;
import msi.gama.gui.swt.SwtGui;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.*;
import msi.gama.runtime.GAMA.InScope;
import msi.gama.util.GamaMap;
import msi.gaml.types.*;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;

public class MapEditor extends ExpressionBasedEditor<Map> {

	MapEditor(final IParameter param) {
		super(param);
	}

	MapEditor(final IAgent agent, final IParameter param) {
		this(agent, param, null);
	}

	MapEditor(final IAgent agent, final IParameter param, final EditorListener l) {
		super(agent, param, l);
	}

	MapEditor(final Composite parent, final String title, final Map value, final EditorListener<Map> whenModified) {
		// Convenience method
		super(new InputParameter(title, value), whenModified);
		this.createComposite(parent);
	}

	@Override
	public void applyEdit() {
		GAMA.run(new InScope.Void() {

			@Override
			public void process(final IScope scope) {
				MapEditorDialog mapParameterDialog =
					new MapEditorDialog(scope, SwtGui.getShell(), (GamaMap) currentValue);
				if ( mapParameterDialog.open() == IDialogConstants.OK_ID ) {
					modifyValue(mapParameterDialog.getMap());
				}
			}

		});
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
