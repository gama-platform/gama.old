/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.gui.parameters;

import java.util.List;
import msi.gama.interfaces.*;
import msi.gama.kernel.experiment.ExperimentParameter;
import msi.gama.util.GamaPoint;
import org.eclipse.swt.widgets.Composite;

public class EditorFactory {

	public static BooleanEditor create(final Composite parent, final String title,
		final Boolean value, final EditorListener<Boolean> whenModified) {
		return new BooleanEditor(parent, title, value, whenModified);
	}

	public static ColorEditor create(final Composite parent, final String title,
		final java.awt.Color value, final EditorListener<java.awt.Color> whenModified) {
		return new ColorEditor(parent, title, value, whenModified);
	}

	public static ExpressionEditor createExpression(final Composite parent, final String title,
		final Object value, final EditorListener<IExpression> whenModified, final IType expectedType) {
		return new ExpressionEditor(parent, title, value, whenModified, expectedType);
	}

	public static FileEditor createFile(final Composite parent, final String title,
		final Object value, final EditorListener<String> whenModified) {
		return new FileEditor(parent, title, value, whenModified);
	}

	public static FloatEditor create(final Composite parent, final String title,
		final Double value, final Double min, final Double max, final Double step,
		final boolean canBeNull, final EditorListener<Double> whenModified) {
		return new FloatEditor(parent, title, value, min, max, step, canBeNull, whenModified);
	}

	public static GenericEditor createGeneric(final Composite parent, final String title,
		final Object value, final EditorListener whenModified) {
		return new GenericEditor(parent, title, value, whenModified);
	}

	public static IntEditor create(final Composite parent, final String title, final String unit,
		final Integer value, final Integer min, final Integer max, final Integer step,
		final boolean canBeNull, final EditorListener<Integer> whenModified) {
		return new IntEditor(parent, title, unit, value, min, max, step, whenModified, canBeNull);
	}

	public static ListEditor create(final Composite parent, final String title,
		final java.util.List value, final EditorListener<java.util.List> whenModified) {
		return new ListEditor(parent, title, value, whenModified);
	}

	public static MapEditor create(final Composite parent, final String title,
		final java.util.Map value, final EditorListener<java.util.Map> whenModified) {
		return new MapEditor(parent, title, value, whenModified);
	}

	public static MatrixEditor create(final Composite parent, final String title,
		final IMatrix value, final EditorListener<IMatrix> whenModified) {
		return new MatrixEditor(parent, title, value, whenModified);
	}

	public static PointEditor create(final Composite parent, final String title,
		final GamaPoint value, final EditorListener<GamaPoint> whenModified) {
		return new PointEditor(parent, title, value, whenModified);
	}

	public static StringEditor create(final Composite parent, final String title,
		final String value, final boolean asLabel, final EditorListener<String> whenModified) {
		return new StringEditor(parent, title, value, whenModified, asLabel);
	}

	public static StringEditor choose(final Composite parent, final String title,
		final String value, final boolean asLabel, final List<String> among,
		final EditorListener<String> whenModified) {
		return new StringEditor(parent, title, value, among, whenModified, asLabel);
	}

	public static AbstractEditor create(final Composite parent, final IParameter var) {
		AbstractEditor ed = create((IAgent) null, var);
		ed.createComposite(parent);
		return ed;
	}

	public static AbstractEditor create(final IAgent agent, final IParameter var) {
		final boolean canBeNull =
			var instanceof ExperimentParameter ? ((ExperimentParameter) var).canBeNull() : false;
		final short type = var.type().id();
		AbstractEditor gp =
			type == IType.BOOL ? new BooleanEditor(agent, var) : type == IType.COLOR
				? new ColorEditor(agent, var) : type == IType.FLOAT ? new FloatEditor(agent, var,
					canBeNull) : type == IType.INT ? new IntEditor(agent, var, canBeNull)
					: type == IType.LIST ? new ListEditor(agent, var) : type == IType.POINT
						? new PointEditor(agent, var) : type == IType.MAP ? new MapEditor(agent,
							var) : type == IType.MATRIX ? new MatrixEditor(agent, var)
							: type == IType.FILE ? new FileEditor(agent, var)
								: type == IType.STRING ? new StringEditor(agent, var)
									: new GenericEditor(agent, var);
		return gp;
	}
}
