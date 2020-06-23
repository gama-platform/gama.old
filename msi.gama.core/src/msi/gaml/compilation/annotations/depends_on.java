/*******************************************************************************************************
 *
 * msi.gaml.compilation.annotations.depends_on.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.compilation.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention (RUNTIME)
@Target (METHOD)
/**
 * An annotation which purpose is to specify, for operators, if they implicitely rely on the previous initialization of
 * one or several attributes (like, for instance, the cone() operator implicitly relying on the shape of the world being
 * already initialized)
 * 
 * @author drogoul
 *
 */
public @interface depends_on {

	String[] value() default {};
}
