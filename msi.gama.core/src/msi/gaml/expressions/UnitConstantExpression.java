/**
 * Created by drogoul, 22 avr. 2014
 *
 */
package msi.gaml.expressions;

import java.util.*;
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
		if ( unit.equals("zoom") ) { return new ZoomUnitExpression(unit, doc); }
		if ( unit.equals("pixels") || unit.equals("px") ) { return new PixelUnitExpression(unit, doc); }
		if ( unit.equals("display_width") ) { return new DisplayWidthUnitExpression(doc); }
		if ( unit.equals("display_height") ) { return new DisplayHeightUnitExpression(doc); }
		if ( unit.equals("view_x") || unit.equals("view_y") || unit.equals("view_width") ||
			unit.equals("view_height") ) { return new ViewUnitExpression(unit, doc); }
		if ( unit.equals("now") ) { return new NowUnitExpression(unit, doc); }
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
		if ( names != null ) {
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
		if ( alternateNames.size() > 1 ) {
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
