/*******************************************************************************************************
 *
 * msi.gaml.compilation.annotations.serializer.java, in plugin msi.gama.core,
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

import msi.gaml.descriptions.SymbolSerializer;

/**
 * Allows to declare a custom serializer for Symbols (statements, var declarations, species ,experiments, etc.) This
 * serializer will be called instead of the standard serializer, superseding this last one. Serializers must be
 * sublasses of the SymbolSerializer class
 *
 * @author drogoul
 * @since 11 nov. 2014
 *
 */

@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.TYPE)
@Inherited
public @interface serializer {

	Class<? extends SymbolSerializer<?>> value();
}