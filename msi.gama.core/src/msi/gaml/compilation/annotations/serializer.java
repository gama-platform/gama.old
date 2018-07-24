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