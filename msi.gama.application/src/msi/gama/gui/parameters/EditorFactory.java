/*********************************************************************************************
 *
 *
 * 'EditorFactory.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.parameters;

import java.util.List;
import org.eclipse.swt.widgets.Composite;
import msi.gama.common.interfaces.*;
import msi.gama.kernel.experiment.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.util.GamaFont;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;

public class EditorFactory implements IEditorFactory {

	private static final EditorFactory instance = new EditorFactory();

	public static EditorFactory getInstance() {
		return instance;
	}

	public static BooleanEditor create(final Composite parent, final String title, final Boolean value,
		final EditorListener<Boolean> whenModified) {
		return new BooleanEditor(parent, title, value, whenModified);
	}

	public static FontEditor create(final Composite parent, final String title, final GamaFont value,
		final EditorListener<GamaFont> whenModified) {
		return new FontEditor(parent, title, value, whenModified);
	}

	public static ColorEditor create(final Composite parent, final String title, final java.awt.Color value,
		final EditorListener<java.awt.Color> whenModified) {
		return new ColorEditor(parent, title, value, whenModified);
	}

	public static ExpressionEditor createExpression(final Composite parent, final String title,
		final IExpression value, final EditorListener<IExpression> whenModified, final IType expectedType) {
		return new ExpressionEditor(parent, title, value, whenModified, expectedType);
	}

	public static FileEditor createFile(final Composite parent, final String title, final String value,
		final EditorListener<String> whenModified) {
		return new FileEditor(parent, title, value, whenModified);
	}

	public static FloatEditor create(final Composite parent, final String title, final Double value, final Double min,
		final Double max, final Double step, final boolean canBeNull, final EditorListener<Double> whenModified) {
		return new FloatEditor(parent, title, value, min, max, step, canBeNull, whenModified);
	}

	public static GenericEditor createGeneric(final Composite parent, final String title, final Object value,
		final EditorListener whenModified) {
		return new GenericEditor(parent, title, value, whenModified);
	}

	public static IntEditor create(final Composite parent, final String title, final String unit, final Integer value,
		final Integer min, final Integer max, final Integer step, final boolean canBeNull,
		final EditorListener<Integer> whenModified) {
		return new IntEditor(parent, title, unit, value, min, max, step, whenModified, canBeNull);
	}

	public static ListEditor create(final Composite parent, final String title, final java.util.List value,
		final EditorListener<java.util.List> whenModified) {
		return new ListEditor(parent, title, value, whenModified);
	}

	public static MapEditor create(final Composite parent, final String title, final java.util.Map value,
		final EditorListener<java.util.Map> whenModified) {
		return new MapEditor(parent, title, value, whenModified);
	}

	public static MatrixEditor create(final Composite parent, final String title, final IMatrix value,
		final EditorListener<IMatrix> whenModified) {
		return new MatrixEditor(parent, title, value, whenModified);
	}

	public static PointEditor create(final Composite parent, final String title, final ILocation value,
		final EditorListener<GamaPoint> whenModified) {
		return new PointEditor(parent, title, value, whenModified);
	}

	public static AbstractEditor create(final Composite parent, final String title, final String value,
		final boolean asLabel, final EditorListener<String> whenModified) {
		if ( asLabel ) { return new LabelEditor(parent, title, value, whenModified); }
		return new StringEditor(parent, title, value, whenModified);
	}

	public static StringEditor choose(final Composite parent, final String title, final String value,
		final boolean asLabel, final List<String> among, final EditorListener<String> whenModified) {
		return new StringEditor(parent, title, value, among, whenModified, asLabel);
	}

	public static AbstractEditor create(final Composite parent, final IParameter var) {
		return create(parent, var, false);
	}

	public static AbstractEditor create(final Composite parent, final IParameter var, final boolean isSubParameter) {
		return create(parent, var, null, isSubParameter);
	}

	public static AbstractEditor create(final Composite parent, final IParameter var, final EditorListener l,
		final boolean isSubParameter) {
		AbstractEditor ed = instance.create((IAgent) null, var, l);
		ed.isSubParameter(isSubParameter);
		ed.createComposite(parent);
		return ed;
	}

	@Override
	public AbstractEditor create(final IAgent agent, final IParameter var, final EditorListener l) {
		final boolean canBeNull = var instanceof ExperimentParameter && ((ExperimentParameter) var).canBeNull();
		final IType t = var.getType();
		final int type = t.getType().id();
		if ( t.isContainer() && t.getContentType().isAgentType() ) { return new PopulationEditor(agent, var, l); }
		if ( t.isAgentType() || type == IType.AGENT ) { return new AgentEditor(agent, var, l); }
		switch (type) {
			case IType.BOOL:
				return new BooleanEditor(agent, var, l);
			case IType.COLOR:
				return new ColorEditor(agent, var, l);
			case IType.FLOAT:
				return new FloatEditor(agent, var, canBeNull, l);
			case IType.INT:
				return new IntEditor(agent, var, canBeNull, l);
			case IType.LIST:
				return new ListEditor(agent, var, l);
			case IType.POINT:
				return new PointEditor(agent, var, l);
			case IType.MAP:
				return new MapEditor(agent, var, l);
			case IType.MATRIX:
				return new MatrixEditor(agent, var, l);
			case IType.FILE:
				return new FileEditor(agent, var, l);
			case IType.FONT:
				return new FontEditor(agent, var, l);
			case IType.STRING:
				return new StringEditor(agent, var, l);
			default:
				return new GenericEditor(agent, var, l);
		}
	}
}
