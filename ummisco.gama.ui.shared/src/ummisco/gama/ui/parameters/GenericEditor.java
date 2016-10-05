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
package ummisco.gama.ui.parameters;

import org.eclipse.swt.widgets.Composite;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;
import ummisco.gama.ui.interfaces.EditorListener;

public class GenericEditor<T> extends ExpressionBasedEditor<T> {

	IType<?> expectedType;

	GenericEditor(final IScope scope, final IParameter param) {
		super(scope, param);
		expectedType = param.getType();
	}

	GenericEditor(final IScope scope, final IAgent agent, final IParameter param) {
		this(scope, agent, param, null);
	}

	GenericEditor(final IScope scope, final IAgent agent, final IParameter param, final EditorListener<T> l) {
		super(scope, agent, param, l);
		expectedType = param.getType();
	}

	GenericEditor(final IScope scope, final Composite parent, final String title, final T value,
			final EditorListener<T> whenModified) {
		// Convenience method
		super(scope, new InputParameter(title, value), whenModified);
		expectedType = GamaType.of(value);
		this.createComposite(parent);
	}

	@Override
	public IType<?> getExpectedType() {
		return expectedType;
	}

	@Override
	protected int[] getToolItems() {
		return new int[] { REVERT };
	}

}
