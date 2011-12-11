/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
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
package msi.gaml.operators;

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

	@operator(value = Maths.PLUS, priority = IPriority.ADDITION, can_be_const = true)
	public static GamaColor add(final GamaColor c1, final GamaColor c2) {
		return new GamaColor(c1.getRed() + c2.getRed(), c1.getGreen() + c2.getGreen(),
			c1.getBlue() + c2.getBlue());
	}

	@operator(value = Maths.PLUS, priority = IPriority.ADDITION, can_be_const = true)
	public static GamaColor add(final GamaColor c, final Integer i) {
		return new GamaColor(c.getRed() + i, c.getGreen() + i, c.getBlue() + i);
	}

	@operator(value = Maths.MINUS, priority = IPriority.ADDITION, can_be_const = true)
	public static GamaColor substract(final GamaColor c, final Integer i) {
		return new GamaColor(c.getRed() - i, c.getGreen() - i, c.getBlue() - i);
	}

	@operator(value = Maths.TIMES, priority = IPriority.PRODUCT, can_be_const = true)
	public static GamaColor multiply(final GamaColor c, final Integer i) {
		return new GamaColor(c.getRed() * i, c.getGreen() * i, c.getBlue() * i);
	}

	@operator(value = Maths.DIVIDE, priority = IPriority.PRODUCT, can_be_const = true)
	public static GamaColor divide(final GamaColor c, final Integer i) {
		return new GamaColor(c.getRed() / i, c.getGreen() / i, c.getBlue() / i);
	}

	@operator(value = Maths.DIVIDE, priority = IPriority.PRODUCT, can_be_const = true)
	public static GamaColor divide(final GamaColor c, final Double i) {
		return new GamaColor(Maths.round(c.getRed() / i), Maths.round(c.getGreen() / i),
			Maths.round(c.getBlue() / i));
	}

	@operator(value = Maths.MINUS, priority = IPriority.ADDITION, can_be_const = true)
	public static GamaColor substract(final GamaColor c1, final GamaColor c) {
		return new GamaColor(c1.getRed() - c.getRed(), c1.getGreen() - c.getGreen(), c1.getBlue() -
			c.getBlue());
	}

}
