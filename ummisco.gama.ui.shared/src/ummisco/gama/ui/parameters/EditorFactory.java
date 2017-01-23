/*********************************************************************************************
 *
 * 'EditorFactory.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.parameters;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import msi.gama.kernel.experiment.ExperimentParameter;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaDate;
import msi.gama.util.file.IGamaFile;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;
import ummisco.gama.ui.interfaces.EditorListener;

@SuppressWarnings ({ "unchecked", "rawtypes" })
public class EditorFactory {

	private static final EditorFactory instance = new EditorFactory();

	public static EditorFactory getInstance() {
		return instance;
	}

	public static BooleanEditor create(final IScope scope, final Composite parent, final String title,
			final Boolean value, final EditorListener<Boolean> whenModified) {
		return new BooleanEditor(scope, parent, title, value, whenModified);
	}
	//
	// public static FontEditor create(final Composite parent, final String
	// title, final GamaFont value,
	// final EditorListener<GamaFont> whenModified) {
	// return new FontEditor(parent, title, value, whenModified);
	// }

	public static ColorEditor create(final IScope scope, final Composite parent, final String title,
			final java.awt.Color value, final EditorListener<java.awt.Color> whenModified) {
		return new ColorEditor(scope, parent, title, value, whenModified);
	}

	public static DateEditor create(final IScope scope, final Composite parent, final String title,
			final GamaDate value, final EditorListener<GamaDate> whenModified) {
		return new DateEditor(scope, parent, title, value, whenModified);
	}

	public static ExpressionEditor createExpression(final IScope scope, final Composite parent, final String title,
			final IExpression value, final EditorListener<IExpression> whenModified, final IType expectedType) {
		return new ExpressionEditor(scope, parent, title, value, whenModified, expectedType);
	}

	public static FileEditor createFile(final IScope scope, final Composite parent, final String title,
			final String value, final EditorListener<IGamaFile> whenModified) {
		return new FileEditor(scope, parent, title, value, whenModified);
	}

	public static FloatEditor create(final IScope scope, final Composite parent, final String title, final Double value,
			final Double min, final Double max, final Double step, final boolean canBeNull,
			final EditorListener<Double> whenModified) {
		return new FloatEditor(scope, parent, title, value, min, max, step, canBeNull, whenModified);
	}
	//
	// private static GenericEditor createGeneric(final Composite parent, final
	// String title, final Object value,
	// final EditorListener whenModified) {
	// return new GenericEditor(parent, title, value, whenModified);
	// }
	//
	// public static IntEditor create(final Composite parent, final String
	// title, final String unit, final Integer value,
	// final Integer min, final Integer max, final Integer step, final boolean
	// canBeNull,
	// final EditorListener<Integer> whenModified) {
	// return new IntEditor(parent, title, unit, value, min, max, step,
	// whenModified, canBeNull);
	// }
	//
	// public static ListEditor create(final Composite parent, final String
	// title, final java.util.List value,
	// final EditorListener<java.util.List> whenModified) {
	// return new ListEditor(parent, title, value, whenModified);
	// }
	//
	// public static MapEditor create(final Composite parent, final String
	// title, final java.util.Map value,
	// final EditorListener<java.util.Map> whenModified) {
	// return new MapEditor(parent, title, value, whenModified);
	// }

	// public static MatrixEditor create(final Composite parent, final String
	// title, final IMatrix value,
	// final EditorListener<IMatrix> whenModified) {
	// return new MatrixEditor(parent, title, value, whenModified);
	// }

	public static PointEditor create(final IScope scope, final Composite parent, final String title,
			final ILocation value, final EditorListener<ILocation> whenModified) {
		return new PointEditor(scope, parent, title, value, whenModified);
	}

	public static AbstractEditor create(final IScope scope, final Composite parent, final String title,
			final String value, final boolean asLabel, final EditorListener<String> whenModified) {
		if (asLabel) { return new LabelEditor(scope, parent, title, value, whenModified); }
		return new StringEditor(scope, parent, title, value, whenModified);
	}

	public static StringEditor choose(final IScope scope, final Composite parent, final String title,
			final String value, final boolean asLabel, final List<String> among,
			final EditorListener<String> whenModified) {
		return new StringEditor(scope, parent, title, value, among, whenModified, asLabel);
	}

	public static AbstractEditor create(final IScope scope, final Composite parent, final IParameter var) {
		return create(scope, parent, var, false, false);
	}

	public static AbstractEditor create(final IScope scope, final Composite parent, final IParameter var,
			final boolean isSubParameter, final boolean dontUseScope) {
		return create(scope, parent, var, null, isSubParameter, dontUseScope);
	}

	public static AbstractEditor create(final IScope scope, final Composite parent, final IParameter var,
			final EditorListener l, final boolean isSubParameter, final boolean dontUseScope) {
		final AbstractEditor ed = instance.create(scope, (IAgent) null, var, l);
		ed.isSubParameter(isSubParameter);
		ed.dontUseScope(dontUseScope);
		ed.createComposite(parent);
		return ed;
	}

	public AbstractEditor create(final IScope scope, final IAgent agent, final IParameter var, final EditorListener l) {
		final boolean canBeNull = var instanceof ExperimentParameter && ((ExperimentParameter) var).canBeNull();
		final IType t = var.getType();
		final int type = t.getType().id();
		if (t.isContainer() && t.getContentType().isAgentType()) { return new PopulationEditor(scope, agent, var, l); }
		if (t.isAgentType() || type == IType.AGENT) { return new AgentEditor(scope, agent, var, l); }
		switch (type) {
			case IType.BOOL:
				return new BooleanEditor(scope, agent, var, l);
			case IType.DATE:
				return new DateEditor(scope, agent, var, l);
			case IType.COLOR:
				return new ColorEditor(scope, agent, var, l);
			case IType.FLOAT:
				if (var.getMaxValue(scope) != null && var.getMinValue(scope) != null && var.acceptsSlider(scope))
					return new SliderEditor.Float(scope, agent, var, l);
				return new FloatEditor(scope, agent, var, canBeNull, l);
			case IType.INT:
				if (var.getMaxValue(scope) != null && var.getMinValue(scope) != null && var.acceptsSlider(scope))
					return new SliderEditor.Int(scope, agent, var, l);
				return new IntEditor(scope, agent, var, canBeNull, l);
			case IType.LIST:
				return new ListEditor(scope, agent, var, l);
			case IType.POINT:
				return new PointEditor(scope, agent, var, l);
			case IType.MAP:
				return new MapEditor(scope, agent, var, l);
			case IType.MATRIX:
				return new MatrixEditor(scope, agent, var, l);
			case IType.FILE:
				return new FileEditor(scope, agent, var, l);
			case IType.FONT:
				return new FontEditor(scope, agent, var, l);
			case IType.STRING:
				return new StringEditor(scope, agent, var, l);
			default:
				return new GenericEditor(scope, agent, var, l);
		}
	}
}
