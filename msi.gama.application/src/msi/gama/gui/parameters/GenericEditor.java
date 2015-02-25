/*********************************************************************************************
 * 
 * 
 * 'GenericEditor.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.parameters;

import msi.gama.common.interfaces.EditorListener;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gaml.types.*;
import org.eclipse.swt.widgets.Composite;

public class GenericEditor<T> extends ExpressionBasedEditor<T> {

	IType expectedType;

	GenericEditor(final IParameter param) {
		super(param);
		expectedType = param.getType();
	}

	GenericEditor(final IAgent agent, final IParameter param) {
		this(agent, param, null);
	}

	GenericEditor(final IAgent agent, final IParameter param, final EditorListener l) {
		super(agent, param, l);
		expectedType = param.getType();
	}

	GenericEditor(final Composite parent, final String title, final T value, final EditorListener whenModified) {
		// Convenience method
		super(new InputParameter(title, value), whenModified);
		expectedType = GamaType.of(value);
		this.createComposite(parent);
	}

	@Override
	public IType getExpectedType() {
		return expectedType;
	}

	@Override
	protected int[] getToolItems() {
		return new int[] { REVERT };
	}

}
