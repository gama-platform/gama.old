/**
 * Created by drogoul, 22 avr. 2014
 *
 */
package msi.gaml.expressions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import msi.gama.precompiler.GamlProperties;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.LabelExpressionDescription;
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

	String name;

	// Already cached in IExpressionFactory.UNIT_EXPRS
	public static UnitConstantExpression create(final Object val, final IType<?> t, final String unit, final String doc,
			final String[] names) {

		switch (unit) {
		case "zoom":
			return new ZoomUnitExpression(unit, doc);
		case "pixels":
		case "px":
			return new PixelUnitExpression(unit, doc);
		case "display_width":
			return new DisplayWidthUnitExpression(doc);
		case "display_height":
			return new DisplayHeightUnitExpression(doc);
		case "view_x":
		case "view_y":
		case "view_width":
		case "view_height":
			return new ViewUnitExpression(unit, doc);
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
		}

		return new UnitConstantExpression(val, t, unit, doc, names);
	}

	final String documentation;
	final List<String> alternateNames;

	public UnitConstantExpression(final Object val, final IType<?> t, final String name, final String doc,
			final String[] names) {
		super(val, t);
		this.name = name;
		documentation = doc;
		alternateNames = new ArrayList<>();
		alternateNames.add(name);
		if (names != null) {
			alternateNames.addAll(Arrays.asList(names));
		}
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return "°" + name;
	}

	@Override
	public String getDocumentation() {
		return documentation;
	}

	@Override
	public String getTitle() {
		String s = "Unit " + serialize(false);
		if (alternateNames.size() > 1) {
			s += " (" + alternateNames + ")";
		}
		return s;
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
	public void setExpression(final IExpression expr) {
	}

	@Override
	public IExpression compile(final IDescription context) {
		return this;
	}

	@Override
	public IExpression getExpression() {
		return this;
	}

	@Override
	public IExpressionDescription compileAsLabel() {
		return LabelExpressionDescription.create(name);
	}

	@Override
	public boolean equalsString(final String o) {
		return name.equals(o);
	}

	@Override
	public EObject getTarget() {
		return null;
	}

	@Override
	public void setTarget(final EObject target) {
	}

	@Override
	public Set<String> getStrings(final IDescription context, final boolean skills) {
		return Collections.EMPTY_SET;
	}

	@Override
	public IExpressionDescription cleanCopy() {
		return this;
	}

	@Override
	public IType getDenotedType(final IDescription context) {
		return Types.NO_TYPE;
	}

}
