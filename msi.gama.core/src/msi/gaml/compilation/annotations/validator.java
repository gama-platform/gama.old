/*******************************************************************************************************
 *
 * msi.gaml.compilation.annotations.validator.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.compilation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import msi.gaml.compilation.IValidator;

/**
 * Allows to declare a custom validator for Symbols and Operators. This validator, if declared on subclasses of Symbol,
 * will be called after the standard validation is done. The validator must be subclass of IDescriptionValidator in that
 * case. If invoked on operators, the class should be a subclass of IOperatorValidator.
 *
 * @author drogoul
 * @since 11 nov. 2014
 *
 */

@Retention (RetentionPolicy.RUNTIME)
@Target ({ ElementType.METHOD, ElementType.TYPE })
@Inherited
@SuppressWarnings ({ "rawtypes" })
public @interface validator {

	Class<? extends IValidator> value();
}