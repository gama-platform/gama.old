/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.parameters;

import java.util.List;
import msi.gama.common.interfaces.*;
import msi.gama.kernel.experiment.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;
import org.eclipse.swt.widgets.Composite;

public class EditorFactory implements IEditorFactory {

	private static final EditorFactory instance = new EditorFactory();

	public static EditorFactory getInstance() {
		return instance;
	}

	public static BooleanEditor create(final Composite parent, final String title, final Boolean value,
		final EditorListener<Boolean> whenModified) {
		return new BooleanEditor(parent, title, value, whenModified);
	}

	public static ColorEditor create(final Composite parent, final String title, final java.awt.Color value,
		final EditorListener<java.awt.Color> whenModified) {
		return new ColorEditor(parent, title, value, whenModified);
	}

	public static ExpressionEditor createExpression(final Composite parent, final String title, final Object value,
		final EditorListener<IExpression> whenModified, final IType expectedType) {
		return new ExpressionEditor(parent, title, value, whenModified, expectedType);
	}

	public static FileEditor createFile(final Composite parent, final String title, final Object value,
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

	public static StringEditor create(final Composite parent, final String title, final String value,
		final boolean asLabel, final EditorListener<String> whenModified) {
		return new StringEditor(parent, title, value, whenModified, asLabel);
	}

	public static StringEditor choose(final Composite parent, final String title, final String value,
		final boolean asLabel, final List<String> among, final EditorListener<String> whenModified) {
		return new StringEditor(parent, title, value, among, whenModified, asLabel);
	}

	public static AbstractEditor create(final Composite parent, final IParameter var) {
		return create(parent, var, null);
	}

	public static AbstractEditor create(final Composite parent, final IParameter var, final EditorListener l) {
		AbstractEditor ed = instance.create((IAgent) null, var, l);
		ed.createComposite(parent);
		return ed;
	}

	@Override
	public AbstractEditor create(final IAgent agent, final IParameter var, final EditorListener l) {
		final boolean canBeNull = var instanceof ExperimentParameter ? ((ExperimentParameter) var).canBeNull() : false;
		final int type = var.getType().id();
		// final int contentType = var.getContentType().id();
		boolean isPopulation = var.getType().hasContents() && var.getContentType().isSpeciesType();
		AbstractEditor gp =
			isPopulation ? new PopulationEditor(agent, var, l) : var.getType().isSpeciesType() | type == IType.AGENT
				? new AgentEditor(agent, var, l) : type == IType.BOOL ? new BooleanEditor(agent, var, l)
					: type == IType.COLOR ? new ColorEditor(agent, var, l) : type == IType.FLOAT ? new FloatEditor(
						agent, var, canBeNull, l) : type == IType.INT ? new IntEditor(agent, var, canBeNull, l)
						: type == IType.LIST ? new ListEditor(agent, var, l) : type == IType.POINT ? new PointEditor(
							agent, var, l) : type == IType.MAP ? new MapEditor(agent, var, l) : type == IType.MATRIX
							? new MatrixEditor(agent, var, l) : type == IType.FILE ? new FileEditor(agent, var, l)
								: type == IType.STRING ? new StringEditor(agent, var, l) : new GenericEditor(agent,
									var, l);
		return gp;
	}
}
