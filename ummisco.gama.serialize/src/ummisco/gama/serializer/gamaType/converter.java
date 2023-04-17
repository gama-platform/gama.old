/*******************************************************************************************************
 *
 * converter.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.gamaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ummisco.gama.serializer.gamaType.converters.IGamaConverter;

/**
 * The Annotation converter.
 */
@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.TYPE)

public @interface converter {

	/** The class to use as converter. */
	Class<? extends IGamaConverter> value();
}
