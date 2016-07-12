/**
 * Created by drogoul, 22 avr. 2014
 *
 */
package msi.gaml.expressions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import msi.gama.precompiler.GamlProperties;
import msi.gaml.types.IType;

/**
 * Class UnitConstantExpression.
 *
 * @author drogoul
 * @since 22 avr. 2014
 *
 */
public class UnitConstantExpression extends ConstantExpression {

	public static UnitConstantExpression create(final Object val, final IType t, final String unit, final String doc,
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

	public UnitConstantExpression(final Object val, final IType t, final String name, final String doc,
			final String[] names) {
		super(val, t);
		this.name = name;
		documentation = doc;
		alternateNames = new ArrayList();
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

}
