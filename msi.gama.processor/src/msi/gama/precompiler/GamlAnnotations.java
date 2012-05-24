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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.precompiler;

import java.lang.annotation.*;

/**
 * The Class GamlAnnotations.
 * 
 * @author Alexis Drogoul, 8 juin 07
 * 
 *         These GamlAnnotations are used to tag the procedures and classes that will be used by
 *         GAML for defining variables and primitives. These annotations wil be subsequently parsed
 *         by the Species of the agents, in order to collect them.
 */

public final class GamlAnnotations {

	@Retention(RetentionPolicy.RUNTIME)
	// @Target(ElementType.TYPE)
	@Inherited
	public static @interface facets {

		/**
		 * @return an Array of @facet, each representing a facet name, type..
		 */
		facet[] value();

		/*
		 * The different combinations of facets that are allowed. Not completely functional yet.
		 */
		combination[] combinations() default {};

		/*
		 * Return the facet that can be safely omitted by the modeler (provided its value is the
		 * first following the keyword of the statement).
		 */
		String omissible();

		// gamlDoc doc() default essai;

	}

	@Retention(RetentionPolicy.RUNTIME)
	// @Target(ElementType.TYPE)
	public static @interface facet {

		/**
		 * @return the name of the facet. Must be unique within a symbol.
		 */
		String name();

		/**
		 * @return The string values of the different types that can be taken by this facet.
		 */

		String[] type();

		/**
		 * @return whether or not this facet is optional or mandatory.
		 */

		boolean optional() default false;

		/**
		 * @return the values that can be taken by this facet.
		 */
		String[] values() default {};

		/**
		 * @return the documentation associated to a facet.
		 */
		String doc() default "";
	}

	@Retention(RetentionPolicy.RUNTIME)
	public static @interface combination {

		/**
		 * Value.
		 * 
		 * @return an Array of String, each representing a possible combination of facets names
		 */
		String[] value();

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Inherited
	@Target(ElementType.TYPE)
	public static @interface type {

		/**
		 * @return a String representing the type name in Gaml
		 */
		String value();

		/**
		 * @return the unique (short) identifier for this type. User-added types can be chosen
		 *         between IType.AVAILABLE_TYPE and IType.SPECIES_TYPE (exclusive)
		 */
		short id();

		/**
		 * @return the list of Java Classes this type is "wrapping" (i.e. representing). The first
		 *         one is the one that will be used preferentially throughout GAMA. The other ones
		 *         are to ensure compatibility, in operators, with compatible Java classes (for
		 *         instance, List and GamaList).
		 */
		Class[] wraps();

		/**
		 * @return the kind of Variable used to store this type. see ISymbolKind.Variable.
		 */

		int kind() default ISymbolKind.Variable.REGULAR;

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public static @interface skill {

		/**
		 * Value.
		 * 
		 * @return a String representing the skill name in Gaml
		 */
		String[] value();

		/**
		 * Assign_to
		 * 
		 * @return An array of species names to which the skill will be automatically added
		 *         (complements the "skills" parameter of @species)
		 */
		String[] attach_to() default {};

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@Inherited
	public static @interface inside {

		/**
		 * Determines where the symbol should be located. Either direct symbol names (in symbols) or
		 * generic symbol kinds (in contexts, see ISymbolKind) can be used
		 * 
		 */

		String[] symbols() default {};

		int[] kinds() default {};
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@Inherited
	public static @interface remote_context {

		/**
		 * Indicates that the context of this command is actually an hybrid context: although it
		 * will be executed in a remote context, any temporary variables declared in the enclosing
		 * scopes should be passed on as if the command was executed in the current context.
		 */

	}

	/**
	 * The Interface species.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public static @interface species {

		/**
		 * Value.
		 * 
		 * @return The name of the species that will be created with this class as base.
		 */
		String value();

		/**
		 * Skills
		 * 
		 * @return An array of skill names that will be automatically attached to this species.
		 *         Example: <code> @species(value="animal" skills={"moving"}) </code>
		 */

		String[] skills() default {};
	}

	/**
	 * The Interface args.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public static @interface args {

		/**
		 * Value.
		 * 
		 * @return an Array of strings, each representing an argument that can be passed to a
		 *         action. Used to tag the method that will implement the action.
		 */
		String[] value();
	}

	/**
	 * The Interface action.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public static @interface action {

		/**
		 * Value.
		 * 
		 * @return the name of the action as it can be used in EMF. Used to tag the method that will
		 *         implement the action in Java.
		 */
		String value();
	}

	// /**
	// * The Interface vars.
	// */
	@Retention(RetentionPolicy.RUNTIME)
	// @Inherited
	// @Target(ElementType.TYPE)
	public static @interface vars {

		/**
		 * Value.
		 * 
		 * @return an Array of var, each representing a variable
		 */
		var[] value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	// @Target(ElementType.TYPE)
	public static @interface var {

		/**
		 * Value.
		 * 
		 * @return an Array of strings, each representing a variable name
		 */
		String name();

		String type();

		String of() default "";

		boolean constant() default false;

		String init() default "";

		String[] depends_on() default {};

		String species() default "";

		boolean freezable() default false; // TODO REMOVE!

		String doc() default "";
	}

	/**
	 * The Interface keyword.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public static @interface symbol {

		/**
		 * Value.
		 * 
		 * @return an Array of strings, each representing a possible keyword for a GAML statement.
		 *         Elements annotated by this annotation should also indicate what kind of symbol
		 *         they represent ( see ISymbolKind)
		 */
		String[] name() default {};

		int kind();

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public static @interface no_scope {
		// Indicates that the command (usually a sequence) does not define its own scope.
		// I.e. all the temporary variables defined in it are actually defined in the
		// super-scope
	}

	/**
	 * The Interface operator.
	 */
	/**
	 * Written by drogoul Modified on 9 aožt 2010
	 * 
	 * @todo Description
	 * 
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD, ElementType.TYPE })
	public static @interface operator {

		/**
		 * @return an Array of strings, each representing a possible keyword for a GAML operator.
		 * 
		 */
		String[] value();

		/**
		 * @return true if this operator should be treated as an iterator (i.e.requires initializing
		 *         the special variable "each" of WorldSkill within the method)
		 */

		boolean iterator() default false;

		/**
		 * @return true if this operator should be run on the agent rather than on its child (or
		 *         statically). Used for operators that rely on simulation or agent-dependent
		 *         structures (like random numbers, location of the agent, etc.)
		 */
		// Now directly computed from the method signature :
		// boolean context_dependent() default false;

		/**
		 * @return true if the right-hand child (for the moment) of the binary operator is to be
		 *         passed as an expression rather than a value (for a later evaluation within the
		 *         operator)
		 */
		// Now directly computed from the method signature :
		// boolean lazy_evaluation() default false;

		/**
		 * @return whether or not the operator can be evaluated as a constant if its child (resp.
		 *         children) is (resp. are) constant.
		 */
		boolean can_be_const() default false;

		/**
		 * @return the type of the content if the returned value is a container. Can be directly a
		 *         type in IType or one of the constants declared in ITypeProvider (in which case,
		 *         the content type is searched in this provider).
		 */
		short content_type() default ITypeProvider.NONE;

		/**
		 * @return the respective priority of the operator w.r.t. to the others. Priorities are
		 *         classified in several categories, and specified using one of the constants
		 *         declared in IPriority
		 */
		short priority() default IPriority.DEFAULT;

		/**
		 * 
		 * @return the type of the expression if it cannot be determined at compile time (i.e. when
		 *         the return type is "Object"). Can be directly a type in IType or one of the
		 *         constants declared in ITypeProvider (in which case, the type is searched in this
		 *         provider).
		 */
		short type() default ITypeProvider.NONE;
	}

	/**
	 * The Interface getter.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public static @interface getter {

		/**
		 * Value.
		 * 
		 * @return the name of the variable for which the annotated method is to be considered as a
		 *         getter.
		 */
		String var();

		/**
		 * Inits the.
		 * 
		 * @return true, if successful
		 */
		boolean initializer() default false;

	}

	/**
	 * The Interface setter.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public static @interface setter {

		/**
		 * Value.
		 * 
		 * @return the name of the variable for which the annotated method is to be considered as a
		 *         getter.
		 */
		String value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@Inherited
	public static @interface with_sequence {
		/**
		 * @return Indicates wether or not a sequence can ou should follow the symbol denoted by
		 *         this class.
		 */

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	// @Inherited
	public static @interface with_args {
		/**
		 * @return Indicates wether or not the symbol denoted by this class has arguments
		 */
	}

	/**
	 * The Interface return_type.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Inherited
	@Target(ElementType.TYPE)
	public static @interface base {

		/**
		 * @return The inner type (in terms of GAML) returned by the getter.
		 */
		Class value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Inherited
	@Target(ElementType.TYPE)
	public static @interface commands {

		/**
		 * @return The classes to register for new commands offered by a species.
		 */
		@SuppressWarnings("rawtypes")
		Class[] value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public static @interface handles {

		/**
		 * @return The symbol kinds defining the symbols this factory is intended to parse and
		 *         compile. see ISymbolKind.
		 */
		int[] value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public static @interface uses {

		/**
		 * @return The subfactories that this factory can invocate, based on the kind of symbols
		 *         they are handling. see ISymbolKind.
		 */
		int[] value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public static @interface reserved {

		/**
		 * @return an Array of strings, each representing a reserved variable.
		 */
		String[] value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE, ElementType.METHOD })
	@Inherited
	public static @interface doc {

		/**
		 * Value.
		 * 
		 * @return a String representing the description of the element
		 */
		String value() default "";

		String comment() default "";

		String[] specialCases() default {};

		String[] examples() default {};

		String[] see() default {};
	}

	@Retention(RetentionPolicy.RUNTIME)
	// @Target(ElementType.TYPE)
	@Inherited
	public static @interface docAction {

		String value() default "";

		arg[] args() default {};

		String returns() default "";

		String[] examples() default {};
	}

	@Retention(RetentionPolicy.RUNTIME)
	// @Target(ElementType.TYPE)
	@Inherited
	public static @interface arg {

		String name() default "";

		String type() default "";

		boolean optional() default false;

		String doc() default "";
	}
}
