/*********************************************************************************************
 *
 * 'Material.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.operators;

import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.util.*;

/**
 * Written by mazarsju
 *
 * @todo Description
 *
 */
public class Material {

	@operator(value = "material", can_be_const = true, category = { /*TODO*/ }, concept = { /*TODO*/ })
	@doc(value = "Returns"/*TODO*/,
	masterDoc = true,
	usages = @usage(""),
	examples = @example(value = "", equals = ""),
	see = "")
	public static GamaMaterial material(final double damper, final double reflectivity) {
		return new GamaMaterial(damper,reflectivity);
	}
}
