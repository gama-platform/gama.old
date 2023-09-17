/*******************************************************************************************************
 *
 * UnitConstantExpression.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.expressions.units;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import msi.gama.precompiler.GamlProperties;
import msi.gama.runtime.IScope;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.LabelExpressionDescription;
import msi.gaml.expressions.ConstantExpression;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Class UnitConstantExpression.
 *
 * @author drogoul
 * @since 22 avr. 2014
 *
 */
public class UnitConstantExpression extends ConstantExpression implements IExpressionDescription {

	/** The name. */
	String name;

	/**
	 * Creates the.
	 *
	 * @param val
	 *            the val
	 * @param t
	 *            the t
	 * @param unit
	 *            the unit
	 * @param doc
	 *            the doc
	 * @param isTime
	 *            the is time
	 * @param names
	 *            the names
	 * @return the unit constant expression
	 */
	// Already cached in IExpressionFactory.UNIT_EXPRS
	public static UnitConstantExpression create(final Object val, final IType<?> t, final String unit, final String doc,
			final boolean isTime, final String[] names) {

		switch (unit) {
			case "zoom":
				return new ZoomUnitExpression(unit, doc);
			case "fullscreen":
				return new FullScreenExpression(unit, doc);
			case "hidpi":
				return new HiDPIExpression(unit, doc);
			case "pixels":
			case "px":
				return new PixelUnitExpression(unit, doc);
			case "display_width":
				return new DisplayWidthUnitExpression(doc);
			case "display_height":
				return new DisplayHeightUnitExpression(doc);
			case "now":
				return new NowUnitExpression(unit, doc);
			case "camera_location":
				return new CameraPositionUnitExpression(doc);
			case "camera_target":
				return new CameraTargetUnitExpression(doc);
			case "camera_orientation":
				return new CameraOrientationUnitExpression(doc);
			case "user_location":
				return new UserLocationUnitExpression(doc);
			case "user_location_in_display":
				return new UserLocationInDisplayUnitExpression(doc);
			case "current_error":
				return new CurrentErrorUnitExpression(doc);

		}
		if (isTime) return new TimeUnitConstantExpression(val, t, unit, doc, names);
		return new UnitConstantExpression(val, t, unit, doc, names);
	}

	/** The documentation. */
	Doc documentation;

	/** The alternate names. */
	final List<String> alternateNames;

	/** The is deprecated. */
	private boolean isDeprecated;

	/**
	 * Instantiates a new unit constant expression.
	 *
	 * @param val
	 *            the val
	 * @param t
	 *            the t
	 * @param name
	 *            the name
	 * @param doc
	 *            the doc
	 * @param names
	 *            the names
	 */
	public UnitConstantExpression(final Object val, final IType<?> t, final String name, final String doc,
			final String[] names) {
		super(val, t);
		this.name = name;
		documentation = new ConstantDoc(doc);
		alternateNames = new ArrayList<>();
		alternateNames.add(name);
		if (names != null) { alternateNames.addAll(Arrays.asList(names)); }
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return "#" + name;
	}

	@Override
	public Doc getDocumentation() { return documentation; }

	@Override
	public String getName() { return name; }

	@Override
	public void setName(final String n) { this.name = n; }

	@Override
	public String getTitle() {
		String prefix;
		if (type.equals(Types.COLOR)) {
			prefix = "Constant color ";
		} else if (getClass().equals(UnitConstantExpression.class)) {
			prefix = "Constant ";
		} else {
			prefix = "Mutable value ";
		}
		StringBuilder s = new StringBuilder().append(prefix).append(serialize(false));
		if (alternateNames.size() > 1) { s.append(" (").append(alternateNames).append(")"); }
		return s.toString();
	}

	@Override
	public boolean shouldBeParenthesized() {
		return false;
	}

	@Override
	public void collectMetaInformation(final GamlProperties meta) {
		meta.put(GamlProperties.CONSTANTS, name);
	}

	@Override
	public void setExpression(final IExpression expr) {}

	@Override
	public IExpression compile(final IDescription context) {
		return getExpression();
	}

	@Override
	public IExpression getExpression() { return this; }

	@Override
	public IExpressionDescription compileAsLabel() {
		return LabelExpressionDescription.create(name);
	}

	@Override
	public boolean equalsString(final String o) {
		return name.equals(o);
	}

	@Override
	public EObject getTarget() { return null; }

	@Override
	public void setTarget(final EObject target) {}

	@Override
	public Set<String> getStrings(final IDescription context, final boolean skills) {
		return Collections.EMPTY_SET;
	}

	@Override
	public IExpressionDescription cleanCopy() {
		return this;
	}

	@Override
	public IType<?> getDenotedType(final IDescription context) {
		return Types.NO_TYPE;
	}

	/**
	 * Sets the deprecated.
	 *
	 * @param deprecated
	 *            the new deprecated
	 */
	public void setDeprecated(final String deprecated) {
		isDeprecated = true;
		documentation.prepend("Deprecated: " + deprecated + ". ");
	}

	/**
	 * Checks if is deprecated.
	 *
	 * @return true, if is deprecated
	 */
	public boolean isDeprecated() { return isDeprecated; }

	@Override
	public IExpression resolveAgainst(final IScope scope) {
		return getExpression();
	}

}
