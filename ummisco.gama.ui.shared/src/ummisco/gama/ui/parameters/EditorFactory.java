/*******************************************************************************************************
 *
 * EditorFactory.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.parameters;

import msi.gama.kernel.experiment.ExperimentParameter;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.experiment.InputParameter;
import msi.gama.kernel.experiment.TextStatement;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.MonitorOutput;
import msi.gama.runtime.IScope;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.UserCommandStatement;
import msi.gaml.types.IType;
import ummisco.gama.ui.interfaces.EditorListener;
import ummisco.gama.ui.interfaces.IParameterEditor;

/**
 * A factory for creating Editor objects.
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class EditorFactory {

	/** The Constant instance. */
	private static final EditorFactory instance = new EditorFactory();

	/**
	 * Gets the single instance of EditorFactory.
	 *
	 * @return single instance of EditorFactory
	 */
	public static EditorFactory getInstance() { return instance; }

	/**
	 * Creates the.
	 *
	 * @param scope
	 *            the scope
	 * @param parent
	 *            the parent
	 * @param title
	 *            the title
	 * @param value
	 *            the value
	 * @param whenModified
	 *            the when modified
	 * @return the boolean editor
	 */
	public static AbstractEditor create(final IScope scope, final EditorsGroup parent, final String title,
			final Boolean value, final EditorListener<Boolean> whenModified) {
		AbstractEditor ed = instance.create(scope, null, new InputParameter(title, value), whenModified);
		ed.createControls(parent);
		return ed;
	}

	/**
	 * Creates the.
	 *
	 * @param scope
	 *            the scope
	 * @param parent
	 *            the parent
	 * @param title
	 *            the title
	 * @param value
	 *            the value
	 * @param whenModified
	 *            the when modified
	 * @return the color editor
	 */
	public static AbstractEditor create(final IScope scope, final EditorsGroup parent, final String title,
			final java.awt.Color value, final EditorListener<java.awt.Color> whenModified) {
		AbstractEditor ed = instance.create(scope, null, new InputParameter(title, value), whenModified);
		ed.createControls(parent);
		return ed;
	}

	/**
	 * Creates a new Editor object.
	 *
	 * @param scope
	 *            the scope
	 * @param parent
	 *            the parent
	 * @param title
	 *            the title
	 * @param value
	 *            the value
	 * @param whenModified
	 *            the when modified
	 * @param expectedType
	 *            the expected type
	 * @return the expression editor
	 */
	public static ExpressionEditor createExpression(final IScope scope, final EditorsGroup parent, final String title,
			final IExpression value, final EditorListener whenModified, final IType expectedType) {
		ExpressionEditor ed =
				new ExpressionEditor(scope, null, new InputParameter(title, value), whenModified, expectedType);
		ed.createControls(parent);
		return ed;
	}

	/**
	 * Creates the.
	 *
	 * @param scope
	 *            the scope
	 * @param parent
	 *            the parent
	 * @param title
	 *            the title
	 * @param value
	 *            the value
	 * @param min
	 *            the min
	 * @param max
	 *            the max
	 * @param step
	 *            the step
	 * @param canBeNull
	 *            the can be null
	 * @param isSlider
	 *            the is slider
	 * @param whenModified
	 *            the when modified
	 * @return the abstract editor
	 */
	public static AbstractEditor create(final IScope scope, final EditorsGroup parent, final String title,
			final Double value, final Double min, final Double max, final Double step, final boolean canBeNull,
			final boolean isSlider, final EditorListener<Double> whenModified) {
		final InputParameter par = new InputParameter(title, value, min, max, step);
		AbstractEditor ed = isSlider ? new SliderEditor.Float(scope, null, par, whenModified)
				: new FloatEditor(scope, null, par, canBeNull, whenModified);
		ed.createControls(parent);
		return ed;
	}

	/**
	 * Creates the.
	 *
	 * @param scope
	 *            the scope
	 * @param parent
	 *            the parent
	 * @param title
	 *            the title
	 * @param unit
	 *            the unit
	 * @param value
	 *            the value
	 * @param min
	 *            the min
	 * @param max
	 *            the max
	 * @param step
	 *            the step
	 * @param whenModified
	 *            the when modified
	 * @return the int editor
	 */
	public static IntEditor create(final IScope scope, final EditorsGroup parent, final String title, final String unit,
			final Integer value, final Integer min, final Integer max, final Integer step,
			final EditorListener<Integer> whenModified) {
		IntEditor ed =
				new IntEditor(scope, null, new InputParameter(title, unit, value, min, max, step), false, whenModified);
		ed.createControls(parent);
		return ed;
	}

	/**
	 * Creates the.
	 *
	 * @param scope
	 *            the scope
	 * @param parent
	 *            the parent
	 * @param title
	 *            the title
	 * @param value
	 *            the value
	 * @param whenModified
	 *            the when modified
	 * @return the point editor
	 */
	public static AbstractEditor create(final IScope scope, final EditorsGroup parent, final String title,
			final GamaPoint value, final EditorListener<GamaPoint> whenModified) {
		AbstractEditor ed = instance.create(scope, null, new InputParameter(title, value), whenModified);
		ed.createControls(parent);
		return ed;
	}

	/**
	 * Creates the.
	 *
	 * @param scope
	 *            the scope
	 * @param parent
	 *            the parent
	 * @param title
	 *            the title
	 * @param value
	 *            the value
	 * @param asLabel
	 *            the as label
	 * @param whenModified
	 *            the when modified
	 * @return the abstract editor
	 */
	public static AbstractEditor create(final IScope scope, final EditorsGroup parent, final String title,
			final String value, final boolean asLabel, final EditorListener<String> whenModified) {
		InputParameter p = new InputParameter(title, value);
		AbstractEditor ed = asLabel ? new LabelEditor(scope, null, p, whenModified)
				: new StringEditor(scope, null, p, whenModified);
		ed.createControls(parent);
		return ed;
	}

	/**
	 * Creates the.
	 *
	 * @param scope
	 *            the scope
	 * @param parent
	 *            the parent
	 * @param var
	 *            the var
	 * @param isSubParameter
	 *            the is sub parameter
	 * @param dontUseScope
	 *            the dont use scope
	 * @return the abstract editor
	 */
	public static AbstractEditor create(final IScope scope, final EditorsGroup parent, final IParameter var,
			final boolean isSubParameter, final boolean dontUseScope) {
		return create(scope, parent, var, null, isSubParameter, dontUseScope);
	}

	/**
	 * Creates the.
	 *
	 * @param scope
	 *            the scope
	 * @param parent
	 *            the parent
	 * @param var
	 *            the var
	 * @param l
	 *            the l
	 * @param isSubParameter
	 *            the is sub parameter
	 * @param dontUseScope
	 *            the dont use scope
	 * @return the abstract editor
	 */
	public static AbstractEditor create(final IScope scope, final EditorsGroup parent, final IParameter var,
			final EditorListener l, final boolean isSubParameter, final boolean dontUseScope) {
		final AbstractEditor ed = instance.create(scope, (IAgent) null, var, l);
		ed.isSubParameter(isSubParameter);
		ed.dontUseScope(dontUseScope);
		ed.createControls(parent);
		return ed;
	}

	/**
	 * Creates the.
	 *
	 * @param scope
	 *            the scope
	 * @param agent
	 *            the agent
	 * @param disp
	 *            the disp
	 * @param l
	 *            the l
	 * @return the abstract editor
	 */
	public AbstractEditor create(final IScope scope, final IAgent agent, final IParameter var, final EditorListener l) {
		final boolean canBeNull = var instanceof ExperimentParameter && ((ExperimentParameter) var).canBeNull();
		final IType t = var.getType();
		final int type = t.getGamlType().id();
		if (t.isContainer() && t.getContentType().isAgentType()) return new PopulationEditor(scope, agent, var, l);
		if (t.isAgentType() || type == IType.AGENT) return new AgentEditor(scope, agent, var, l);
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
			case IType.MATRIX:
				return new MatrixEditor(scope, agent, var, l);
			case IType.FILE:
				return new FileEditor(scope, agent, var, l, false);
			case IType.DIRECTORY:
				return new FileEditor(scope, agent, var, l, true);
			case IType.FONT:
				return new FontEditor(scope, agent, var, l);
			case IType.STRING:
				return new StringEditor(scope, agent, var, l);
			// case IType.PAIR:
			// return new PairEditor(scope, agent, var, l);
			default:
				return new ExpressionBasedEditor(scope, agent, var, l);
		}
	}

	/**
	 * Creates the.
	 *
	 * @param scope
	 *            the scope
	 * @param command
	 *            the command
	 * @param selectionAdapter
	 *            the selection adapter
	 * @return the i parameter editor
	 */
	public IParameterEditor create(final IScope scope, final UserCommandStatement command,
			final EditorListener.Command selectionAdapter) {
		return new CommandEditor(scope, command, selectionAdapter);
	}

	/**
	 * Creates the.
	 *
	 * @param scope
	 *            the scope
	 * @param var
	 *            the var
	 * @return the i parameter editor
	 */
	public IParameterEditor create(final IScope scope, final TextStatement var) {
		return new TextDisplayer(scope, var);
	}

	/**
	 * Creates the.
	 *
	 * @param scope
	 *            the scope
	 * @param var
	 *            the var
	 * @return the monitor displayer
	 */
	public MonitorDisplayer create(final IScope scope, final MonitorOutput var) {
		return new MonitorDisplayer(scope, var);
	}
}
