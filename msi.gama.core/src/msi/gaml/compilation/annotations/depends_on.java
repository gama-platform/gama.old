/**
 * 
 */
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
