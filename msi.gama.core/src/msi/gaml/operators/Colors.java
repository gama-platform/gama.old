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
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.operators;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.*;
import msi.gama.util.GamaColor;

/**
 * Written by drogoul Modified on 10 déc. 2010
 * 
 * @todo Description
 * 
 */
public class Colors {

	@operator(value = IKeyword.PLUS, priority = IPriority.ADDITION, can_be_const = true)
	public static GamaColor add(final GamaColor c1, final GamaColor c2) {
		return new GamaColor(c1.getRed() + c2.getRed(), c1.getGreen() + c2.getGreen(),
			c1.getBlue() + c2.getBlue());
	}

	@operator(value = IKeyword.PLUS, priority = IPriority.ADDITION, can_be_const = true)
	public static GamaColor add(final GamaColor c, final Integer i) {
		return new GamaColor(c.getRed() + i, c.getGreen() + i, c.getBlue() + i);
	}

	@operator(value = IKeyword.MINUS, priority = IPriority.ADDITION, can_be_const = true)
	public static GamaColor substract(final GamaColor c, final Integer i) {
		return new GamaColor(c.getRed() - i, c.getGreen() - i, c.getBlue() - i);
	}

	@operator(value = IKeyword.MULTIPLY, priority = IPriority.PRODUCT, can_be_const = true)
	public static GamaColor multiply(final GamaColor c, final Integer i) {
		return new GamaColor(c.getRed() * i, c.getGreen() * i, c.getBlue() * i);
	}

	@operator(value = IKeyword.DIVIDE, priority = IPriority.PRODUCT, can_be_const = true)
	public static GamaColor divide(final GamaColor c, final Integer i) {
		return new GamaColor(c.getRed() / i, c.getGreen() / i, c.getBlue() / i);
	}

	@operator(value = IKeyword.DIVIDE, priority = IPriority.PRODUCT, can_be_const = true)
	public static GamaColor divide(final GamaColor c, final Double i) {
		return new GamaColor(Maths.round(c.getRed() / i), Maths.round(c.getGreen() / i),
			Maths.round(c.getBlue() / i));
	}

	@operator(value = IKeyword.MINUS, priority = IPriority.ADDITION, can_be_const = true)
	public static GamaColor substract(final GamaColor c1, final GamaColor c) {
		return new GamaColor(c1.getRed() - c.getRed(), c1.getGreen() - c.getGreen(), c1.getBlue() -
			c.getBlue());
	}

}
