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
package msi.gaml.operators;

import msi.gama.internal.expressions.IExpressionParser;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.*;
import msi.gama.util.GamaPoint;

/**
 * Written by drogoul Modified on 11 déc. 2010
 * 
 * @todo Description
 * 
 */
public class Points {

	@operator(value = IExpressionParser.INTERNAL_POINT, priority = IPriority.TERNARY, can_be_const = true)
	// "special" operator introduced in the parser for the points
	public static GamaPoint toPoint(final Double a, final Double b) {
		return new GamaPoint(a, b);
	}

	@operator(value = IExpressionParser.INTERNAL_POINT, priority = IPriority.TERNARY, can_be_const = true)
	// "special" operator introduced in the parser for the points
	public static GamaPoint toPoint(final Integer a, final Double b) {
		return new GamaPoint(a, b);
	}

	@operator(value = IExpressionParser.INTERNAL_POINT, priority = IPriority.TERNARY, can_be_const = true)
	// "special" operator introduced in the parser for the points
	public static GamaPoint toPoint(final Double a, final Integer b) {
		return new GamaPoint(a, b);
	}

	@operator(value = IExpressionParser.INTERNAL_POINT, priority = IPriority.TERNARY, can_be_const = true)
	// "special" operator introduced in the parser for the points
	public static GamaPoint toPoint(final Integer a, final Integer b) {
		return new GamaPoint(a, b);
	}

	@operator(value = Maths.DIVIDE, can_be_const = true)
	public static GamaPoint divide(final GamaPoint p, final Double d) {
		return new GamaPoint(p.x / d, p.y / d);
	}

	@operator(value = Maths.DIVIDE, can_be_const = true)
	public static GamaPoint divide(final GamaPoint p, final Integer d) {
		return new GamaPoint(p.x / d.doubleValue(), p.y / d.doubleValue());
	}

	@operator(value = Maths.TIMES, can_be_const = true)
	public static GamaPoint multiply(final GamaPoint p1, final Double d) {
		return new GamaPoint(p1.x * d, p1.y * d);
	}

	@operator(value = Maths.TIMES, can_be_const = true)
	public static GamaPoint multiply(final GamaPoint p1, final Integer d) {
		return new GamaPoint(p1.x * d.doubleValue(), p1.y * d.doubleValue());
	}

	// ATTENTION: produit scalaire.
	@operator(value = Maths.TIMES, can_be_const = true)
	public static Double multiply(final GamaPoint p1, final GamaPoint p) {
		return p1.x * p.x + p1.y * p.y;
	}

	@operator(value = "norm", can_be_const = true)
	public static Double norm(final GamaPoint p) throws GamaRuntimeException {
		return Maths.sqrt(p.x * p.x + p.y * p.y);
	}

	@operator(value = Maths.PLUS, can_be_const = true)
	public static GamaPoint add(final GamaPoint p1, final GamaPoint p) {
		return new GamaPoint(p1.x + p.x, p1.y + p.y);
	}

	@operator(value = Maths.PLUS, can_be_const = true)
	public static GamaPoint add(final GamaPoint p1, final Double p) {
		return new GamaPoint(p1.x + p, p1.y + p);
	}

	@operator(value = Maths.PLUS, can_be_const = true)
	public static GamaPoint add(final GamaPoint p1, final Integer p) {
		return new GamaPoint(p1.x + p, p1.y + p);
	}

	@operator(value = Maths.MINUS, can_be_const = true)
	public static GamaPoint substract(final GamaPoint p1, final Double p) {
		return new GamaPoint(p1.x - p, p1.y - p);
	}

	@operator(value = Maths.MINUS, can_be_const = true)
	public static GamaPoint substract(final GamaPoint p1, final GamaPoint p) {
		return new GamaPoint(p1.x - p.x, p1.y - p.y);
	}

	@operator(value = Maths.MINUS, can_be_const = true)
	public static GamaPoint substract(final GamaPoint p1, final Integer p) {
		return new GamaPoint(p1.x - p, p1.y - p);
	}

}
