/*********************************************************************************************
 *
 * 'GamlAnnotations.java, in plugin ummisco.gama.annotations, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.precompiler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Class GamlAnnotations. GamlAnnotations contains several annotation classes that are used to tag the methods and
 * classes that will be used by GAML for instatiating its basic elements (statements, species, skills, variables,
 * operators, etc.). These annotations are parsed at compile-time to create the corresponding GAML elements. Some are
 * used at runtime as well.
 *
 * @author Alexis Drogoul
 * @since 8 juin 07
 *
 *
 */

public final class GamlAnnotations {

	/**
	 * An annotation for signalling an implementation of msi.gama.common.interfaces.IDisplaySurface as a potential
	 * display surface for GAMA models
	 *
	 * @author drogoul
	 *
	 */
	@Retention (RetentionPolicy.RUNTIME)
	@Target (ElementType.TYPE)
	@Inherited
	public static @interface display {

		/**
		 * The keyword that will allow to open this display in GAML (in "display type: keyword").
		 *
		 * @return
		 */
		String value();
	}

	@Retention (RetentionPolicy.RUNTIME)
	@Target (ElementType.TYPE)
	@Inherited
	public static @interface experiment {

		/**
		 * The keyword that will allow to open this display in GAML (in "display type: keyword").
		 *
		 * @return
		 */
		String value();
	}

	/**
	 *
	 * The class facets. Describes a list of facet used by a symbol (a statement, a declaration) in GAML. Can only be
	 * declared in classes annotated with symbol
	 *
	 * @author drogoul
	 * @since 2 juin 2012
	 *
	 */
	@Retention (RetentionPolicy.RUNTIME)
	@Target (ElementType.TYPE)
	@Inherited
	public static @interface facets {

		/**
		 * Value.
		 *
		 * @return an Array of @facet, each representing a facet name, type..
		 */
		facet[] value();

		/**
		 * Ommissible.
		 *
		 * @return the facet that can be safely omitted by the modeler (provided its value is the first following the
		 *         keyword of the statement).
		 */
		String omissible() default "name";

	}

	/**
	 *
	 * The class facet. Describes a facet in a list of facets
	 *
	 * @see facets
	 * @author drogoul
	 * @since 2 juin 2012
	 *
	 */
	@Retention (RetentionPolicy.RUNTIME)
	public static @interface facet {

		/**
		 * Name.
		 *
		 * @return the name of the facet. Must be unique within a symbol.
		 */
		String name();

		/**
		 * Type.
		 *
		 * @return The int values of the different types that can be taken by this facet.
		 * @see msi.gaml.types.IType
		 */

		int[] type();

		/**
		 * Of.
		 *
		 * @return The int representation of the content type of the facet (see IType#defaultContentType()). Only
		 *         applies to the types considered as containers
		 */
		int of()

		default 0;

		/**
		 * Index.
		 *
		 * @return The int representation of the index type of the facet (see IType#defaultKeyType()). Only applies to
		 *         the types considered as containers
		 */
		int index()

		default 0;

		/**
		 * Values.
		 *
		 * @return the values that can be taken by this facet. The value of the facet expression will be chosen among
		 *         the values described here
		 */
		String[] values() default {};

		/**
		 * Optional.
		 *
		 * @return whether or not this facet is optional or mandatory.
		 */

		boolean optional()

		default false;

		/**
		 * internal.
		 *
		 * @return whether this facet is for internal use only.
		 */
		boolean internal()

		default false;

		/**
		 * Doc.
		 *
		 * @return the documentation associated to the facet.
		 * @see doc
		 */
		doc[] doc() default {};

		/**
		 * RemoteContext.
		 *
		 * @return Indicates that the context of this facet is actually the one denoted by the statement it is attached
		 *         to. i.e. `self` will represent an agent of the species denoted by the statement, while `myself` will
		 *         represent the agent calling the statement
		 */

		boolean remote_context() default false;
	}

	/**
	 *
	 * The class type. Allows to declare a new datatype in GAML. Should annotate a class that implements IType<...> or
	 * subclasses GamaType<...>
	 *
	 * @author drogoul
	 * @since 2 juin 2012
	 *
	 */
	@Retention (RetentionPolicy.RUNTIME)
	@Inherited
	@Target (ElementType.TYPE)
	public static @interface type {

		/**
		 * Name.
		 *
		 * @return a String representing the type name in GAML
		 */
		String name();

		/**
		 * @return an array of strings, each representing this GAML word we can use to find the type in the website
		 *         search feature.
		 */

		String[] concept() default {};

		/**
		 * @return the unique identifier for this type. User-added types can be chosen between IType.AVAILABLE_TYPE and
		 *         IType.SPECIES_TYPE (exclusive)
		 */
		int id();

		/**
		 * @return the list of Java Classes this type is "wrapping" (i.e. representing). The first one is the one that
		 *         will be used preferentially throughout GAMA. The other ones are to ensure compatibility, in
		 *         operators, with compatible Java classes (for instance, List and GamaList).
		 */
		@SuppressWarnings ("rawtypes")
		Class[] wraps();

		/**
		 * @return the kind of Variable used to store this type. see ISymbolKind.Variable.
		 */
		int kind()

		default ISymbolKind.Variable.REGULAR;

		/**
		 * internal.
		 *
		 * @return whether this type is for internal use only.
		 */
		boolean internal()

		default false;

		/**
		 * @return an array of strings, each representing a category in which this constant can be classified (for
		 *         documentation indexes)
		 */

		String[] category() default {};

		/**
		 * Doc.
		 *
		 * @return the documentation attached to this type
		 * @see doc
		 */
		doc[] doc() default {};

	}

	/**
	 *
	 * The class skill. Allows to define a new skill (class grouping variables and actions that can be used by agents).
	 *
	 * @see vars
	 * @see action
	 * @author drogoul
	 * @since 2 juin 2012
	 *
	 */
	@Retention (RetentionPolicy.RUNTIME)
	@Target (ElementType.TYPE)
	public static @interface skill {

		/**
		 * Name.
		 *
		 * @return a String representing the skill name in GAML (must be unique throughout GAML)
		 */
		String name();

		/**
		 * @return an array of strings, each representing this GAML word we can use to find the skill in the website
		 *         search feature.
		 */

		String[] concept() default {};

		/**
		 * Attach_to.
		 *
		 * @return An array of species names to which the skill will be automatically added (complements the "skills"
		 *         parameter of species)
		 * @see species
		 */
		String[] attach_to() default {};

		/**
		 * internal.
		 *
		 * @return whether this skill is for internal use only.
		 */
		boolean internal()

		default false;

		/**
		 * @return an array of strings, each representing a category in which this constant can be classified (for
		 *         documentation indexes)
		 */

		String[] category() default {};

		/**
		 * Doc.
		 *
		 * @return the documentation attached to this skill
		 * @see doc
		 */
		doc[] doc() default {};

	}

	/**
	 *
	 * The class inside. Used in conjunction with symbol. Provides a way to tell where this symbol should be located in
	 * a model (i.e. what its parents should be). Either direct symbol names (in symbols) or generic symbol kinds can be
	 * used
	 *
	 * @see symbol
	 * @see ISymbolKind
	 * @author drogoul
	 * @since 2 juin 2012
	 *
	 */
	@Retention (RetentionPolicy.RUNTIME)
	@Target (ElementType.TYPE)
	@Inherited
	public static @interface inside {

		String[] symbols() default {};

		int[] kinds() default {};
	}

	/**
	 *
	 * The class species. The class annotated with this annotation will be the support of a species of agents.
	 *
	 * @see vars
	 * @see action
	 * @author drogoul
	 * @since 2 juin 2012
	 *
	 */
	@Retention (RetentionPolicy.RUNTIME)
	@Target (ElementType.TYPE)
	public static @interface species {

		/**
		 * Name.
		 *
		 * @return The name of the species that will be created with this class as base. Must be unique throughout GAML.
		 */
		String name();

		/**
		 * @return an array of strings, each representing this GAML word we can use to find the species in the website
		 *         search feature.
		 */

		String[] concept() default {};

		/**
		 * Skills.
		 *
		 * @return An array of skill names that will be automatically attached to this species. Example:
		 *         <code> @species(value="animal" skills={"moving"}) </code>
		 * @see skill
		 */
		String[] skills() default {};

		/**
		 * internal.
		 *
		 * @return whether this species is for internal use only.
		 */
		boolean internal()

		default false;

		/**
		 * @return an array of strings, each representing a category in which this constant can be classified (for
		 *         documentation indexes)
		 */

		String[] category() default {};

		/**
		 * Doc.
		 *
		 * @return the documentation attached to this species
		 * @see doc
		 */
		doc[] doc() default {};
	}

	// /**
	// * The Interface args. Describes the names of the arguments passed to an action.
	// *
	// * @deprecated use action.args() instead
	// * @see action
	// */
	// @Retention (RetentionPolicy.RUNTIME)
	// @Target (ElementType.METHOD)
	// @Deprecated
	// public static @interface args {
	//
	// /**
	// * Value.
	// *
	// *
	// * @return an Array of strings, each representing an argument that can be passed to a action. Used to tag the
	// * method that will implement the action.
	// */
	// String[] names();
	//
	// }

	/**
	 *
	 * The class action. Used to tag a method that will be considered as an action (or primitive) in GAML. The method
	 * must have the following signature: <code>Object methodName(IScope) throws GamaRuntimeException </code> and be
	 * contained in a class annotated with @species or @skill (or a related class, like a subclass or an interface)
	 *
	 * @see species
	 * @see skill
	 */
	@Retention (RetentionPolicy.RUNTIME)
	@Target (ElementType.METHOD)
	public static @interface action {

		/**
		 * Value.
		 *
		 * @return the name of the action as it can be used in GAML.
		 */
		String name();

		/**
		 * Abstract
		 */

		boolean virtual()

		default false;

		/**
		 * Args
		 *
		 * @return the list of arguments passed to this action. Each argument is an instance of arg
		 * @see arg
		 */
		arg[] args() default {};

		/**
		 * internal. Provisional annotation, not used anywhere for the moment.
		 *
		 * @return whether this action is for internal use only.
		 */
		boolean internal()

		default false;

		/**
		 * Doc.
		 *
		 * @return the documentation attached to this action (arguments are documented individually)
		 * @see doc
		 */
		doc[] doc() default {};
	}

	/**
	 *
	 * The class arg. Describes an argument passed to an action.
	 *
	 * @author drogoul
	 * @since 2 juin 2012
	 *
	 */
	@Retention (RetentionPolicy.RUNTIME)
	@Inherited
	public static @interface arg {

		/**
		 * Name.
		 *
		 * @return the name of the argument as it can be used in GAML
		 */
		String name()

		default "";

		/**
		 * Type.
		 *
		 * @return An array containing the textual representation of the types that can be taken by the argument (see
		 *         IType)
		 */
		int type()

		default 0;

		/**
		 * Optional.
		 *
		 * @return whether this argument is optional or not
		 * @change AD 31/08/13 : the default is now true.
		 */
		boolean optional()

		default true;

		/**
		 * Doc.
		 *
		 * @return the documentation attached to this argument
		 * @see doc
		 */
		doc[] doc() default {};
	}

	/**
	 *
	 * The class vars. Used to describe the variables defined by a @species, a @skill or the implementation class of
	 * a @type
	 *
	 * @author drogoul
	 * @since 2 juin 2012
	 *
	 */
	@Retention (RetentionPolicy.RUNTIME)
	public static @interface vars {

		/**
		 * Value.
		 *
		 * @return an Array of var instances, each representing a variable
		 * @see variable
		 */
		variable[] value();
	}

	/**
	 *
	 * The class var. Used to describe a single variable or field.
	 *
	 * @author drogoul
	 * @since 2 juin 2012
	 *
	 */
	@Retention (RetentionPolicy.SOURCE)
	@Target ({})
	public static @interface variable {

		/**
		 * Name.
		 *
		 * @return The name of the variable as it can be used in GAML.
		 */
		String name();

		/**
		 * Type.
		 *
		 * @return The textual representation of the type of the variable (see IType)
		 */
		int type();

		/**
		 * Of.
		 *
		 * @return The int representation of the content type of the variable (see IType#defaultContentType())
		 */
		int of()

		default 0;

		/**
		 * Index.
		 *
		 * @return The int representation of the index type of the variable (see IType#defaultKeyType())
		 */
		int index()

		default 0;

		/**
		 * Constant
		 *
		 * @return whether or not this variable should be considered as non modifiable
		 */
		boolean constant()

		default false;

		/**
		 * Init
		 *
		 * @return the initial value of this variable as a String that will be interpreted by GAML
		 */
		String init()

		default "";

		/**
		 * Depends_on.
		 *
		 * @return an array of String representing the names of the variables on which this variable depends (so that
		 *         they are computed before)
		 */
		String[] depends_on() default {};

		/**
		 * Species.
		 *
		 * @return the species of the variable value in case its type is IType.AGENT.
		 * @deprecated use type instead with the name of the species
		 */
		@Deprecated
		String species()

		default "";

		/**
		 * internal.
		 *
		 * @return whether this var is for internal use only.
		 */
		boolean internal()

		default false;

		/**
		 * Doc.
		 *
		 * @return the documentation attached to this variable
		 * @see doc
		 */
		doc[] doc() default {};
	}

	/**
	 * The Interface symbol. Represents a "symbol" in GAML, i.e. either a statement or a declaration (variable, species,
	 * model, etc.). Elements annotated by this annotation should indicate what kind of symbol they represent
	 *
	 * @see ISymbolKind
	 */
	@Retention (RetentionPolicy.SOURCE)
	@Target (ElementType.TYPE)
	public static @interface symbol {

		/**
		 * Name.
		 *
		 * @return an Array of strings, each representing a possible keyword for a GAML statement.
		 *
		 */
		String[] name() default {};

		/**
		 * @return an array of strings, each representing this GAML word we can use to find the statement in the website
		 *         search feature.
		 */

		String[] concept() default {};

		/**
		 * Kind.
		 *
		 * @return the kind of the annotated symbol.
		 * @see ISymbolKind
		 */
		int kind();

		/**
		 * NoScope.
		 *
		 * @return Indicates if the statement (usually a sequence) defines its own scope. Otherwise, all the temporary
		 *         variables defined in it are actually defined in the super-scope
		 */
		boolean with_scope() default true;

		/**
		 * WithSequence.
		 *
		 * @return Indicates wether or not a sequence can ou should follow the symbol denoted by this class.
		 */
		boolean with_sequence();

		/**
		 * WithArgs.
		 *
		 * @return Indicates wether or not the symbol denoted by this class will accept arguments
		 */
		boolean with_args() default false;

		/**
		 * RemoteContext.
		 *
		 * @return Indicates that the context of this statement is actually an hybrid context: although it will be
		 *         executed in a remote context, any temporary variables declared in the enclosing scopes should be
		 *         passed on as if the statement was executed in the current context.
		 */

		boolean remote_context() default false;

		/**
		 * Doc.
		 *
		 * @return the documentation attached to this symbol.
		 * @see doc
		 */
		doc[] doc() default {};

		/**
		 * internal.
		 *
		 * @return whether this symbol is for internal use only.
		 */
		boolean internal() default false;

		/**
		 *
		 * @return Indicates that this statement must be unique in its super context (for example, only one return is
		 *         allowed in the body of an action).
		 */
		boolean unique_in_context() default false;

		/**
		 *
		 * @return Indicates that only one statement with the same name should be allowed in the same super context
		 */
		boolean unique_name() default false;

		/**
		 * @return an array of strings, each representing a category in which this constant can be classified (for
		 *         documentation indexes)
		 */
		String[] category() default {};

	}

	/**
	 * Written by drogoul Modified on 9 august 2010
	 *
	 * Used to annotate methods that can be used as operators in GAML.
	 *
	 */
	@Retention (RetentionPolicy.RUNTIME)
	@Target ({ ElementType.METHOD, ElementType.TYPE })
	public static @interface operator {

		/**
		 * @return an array of strings, each representing this GAML word we can use to find the operator in the website
		 *         search feature.
		 */

		String[] concept() default {};

		/**
		 * @return an array of strings, each representing a category in which this operator can be classified (for
		 *         documentation indexes)
		 */

		String[] category() default {};

		/**
		 * @return an Array of strings, each representing a possible keyword for the operator. Does not need to be
		 *         unique throughout GAML
		 *
		 */
		String[] value();

		/**
		 * @return true if this operator should be treated as an iterator (i.e. allows the special variable "each" to be
		 *         used inside)
		 */

		boolean iterator() default false;

		/**
		 * @return whether or not the operator can be evaluated as a constant if its child (resp. children) is (resp.
		 *         are) constant.
		 */
		boolean can_be_const() default false;

		/**
		 * @return the type of the content if the returned value is a container. Can be directly a type in IType or one
		 *         of the constants declared in ITypeProvider (in which case, the content type is searched using this
		 *         provider).
		 * @see IType
		 * @see ITypeProvider
		 */
		int content_type() default ITypeProvider.NONE;

		/**
		 * @return the content type of the content if the returned value is a container of container (ex. a list of
		 *         list). Can be directly a type in IType or one of the constants declared in ITypeProvider (in which
		 *         case, the content type is searched using this provider).
		 * @see IType
		 * @see ITypeProvider
		 */
		int content_type_content_type() default ITypeProvider.NONE;

		/**
		 * @return the type of the index if the returned value is a container. Can be directly a type in IType or one of
		 *         the constants declared in ITypeProvider (in which case, the index type is searched using this
		 *         provider).
		 * @see IType
		 * @see ITypeProvider
		 */
		int index_type() default ITypeProvider.NONE;

		/**
		 * @return if the argument is a container, return the types expected for its contents. Should be an array of
		 *         IType.XXX.
		 * @see IType
		 * @see ITypeProvider
		 */
		int[] expected_content_type() default {};

		/**
		 *
		 * @return the type of the expression if it cannot be determined at compile time (i.e. when the return type is
		 *         "Object"). Can be directly a type in IType or one of the constants declared in ITypeProvider (in
		 *         which case, the type is searched using this provider).
		 * @see IType
		 * @see ITypeProvider
		 */
		int type() default ITypeProvider.NONE;

		/**
		 * internal.
		 *
		 * @return whether this operator is for internal use only.
		 */
		boolean internal() default false;

		/**
		 * Doc.
		 *
		 * @return the documentation attached to this operator.
		 * @see doc
		 */
		doc[] doc() default {};
	}

	/**
	 * The class getter. Indicates that a method is to be used as a getter for a variable defined in the class. The
	 * variable must be defined on its own (in vars).
	 *
	 * @see vars
	 */
	@Retention (RetentionPolicy.RUNTIME)
	@Target (ElementType.METHOD)
	public static @interface getter {

		/**
		 * Value.
		 *
		 * @return the name of the variable for which the annotated method is to be considered as a getter.
		 */
		String value();

		/**
		 * Initializer.
		 *
		 * @return whether or not this getter shoud also be used as an initializer
		 */
		boolean initializer() default false;

	}

	/**
	 * The class setter. Indicates that a method is to be used as a setter for a variable defined in the class. The
	 * variable must be defined on its own (in vars).
	 *
	 * @see vars
	 */
	@Retention (RetentionPolicy.RUNTIME)
	@Target (ElementType.METHOD)
	public static @interface setter {

		/**
		 * Value.
		 *
		 * @return the name of the variable for which the annotated method is to be considered as a getter.
		 */
		String value();
	}

	/**
	 * The class listener. Indicates that a method is to be used as a listener for a variable, even if this variable is
	 * not defined in the vars of this class. Allows to "listen" to the evolution of the variables managed by other
	 * skills. If the var doesn't exist, this annotation has no effect.
	 *
	 * @see vars
	 */
	@Retention (RetentionPolicy.RUNTIME)
	@Target (ElementType.METHOD)
	public static @interface listener {

		/**
		 * Value.
		 *
		 * @return the name of the variable for which the annotated method is to be considered as a listener.
		 */
		String value();
	}

	/**
	 *
	 * The class factory. Denotes that a class (that must implement ISymbolFactory) can be used to parse and compile
	 * specific kinds of symbols
	 *
	 * @see ISymbolFactory
	 * @see ISymbolKind
	 * @author drogoul
	 * @since 2 juin 2012
	 *
	 */

	@Retention (RetentionPolicy.SOURCE)
	@Target (ElementType.TYPE)
	public static @interface factory {

		/**
		 * @return The symbol kinds defining the symbols this factory is intended to parse and compile
		 * @see ISymbolKind
		 */
		int[] handles();

		/**
		 * @return The subfactories that this factory can invocate, based on the kind of symbols they are handling
		 * @deprecated This annotation is not used anymore and can be safely removed
		 * @see ISymbolKind
		 */
		@Deprecated
		int[] uses() default {};

	}

	/**
	 *
	 * The class doc. Provides a unified way of attaching documentation to the various GAML elements tagged by the other
	 * annotations. The documentation is automatically assembled at compile time and also used at runtime in GAML
	 * editors
	 *
	 * @author Benoit Gaudou
	 * @since 2 juin 2012
	 *
	 */

	@Retention (RetentionPolicy.RUNTIME)
	// @Target({ ElementType.TYPE, ElementType.METHOD })
	// @Inherited
	public static @interface doc {

		/**
		 * Value.
		 *
		 * @return a String representing the documentation of a GAML element
		 */
		String value()

		default "";

		/**
		 * masterDoc.
		 *
		 * @return a boolean representing the fact whether this instance of the operator is the master one, that is
		 *         whether its value will subsume the value of all other instances of it.
		 */
		boolean masterDoc()

		default false;

		/**
		 * Deprecated.
		 *
		 * @return a String indicating (if it is not empty) that the element is deprecated and defining, if possible,
		 *         what to use instead
		 */
		String deprecated()

		default "";

		/**
		 * Returns.
		 *
		 * @return the documentation concerning the value(s) returned by this element (if any).
		 */
		String returns()

		default "";

		/**
		 * Comment.
		 *
		 * @return An optional comment that will appear differently from the documentation itself.
		 */
		String comment()

		default "";

		/**
		 * Special_cases
		 *
		 * @return An array of String representing the documentation of the "special cases" in which the documented
		 *         element takes part
		 */
		String[] special_cases() default {};

		/**
		 * Examples
		 *
		 * @return An array of String representing some examples or use-cases about how to use this element
		 */
		example[] examples() default {};

		/**
		 * See.
		 *
		 * @return An array of String representing cross-references to other elements in GAML
		 */
		String[] see() default {};

		/**
		 * usages.
		 *
		 * @return An array of usages representing possible usage of the element in GAML
		 */
		usage[] usages() default {};

	}

	/**
	 *
	 * The class @usage. This replaces @special_cases and @examples, and unifies the doc for operators, statements and
	 * others.
	 *
	 * An @usage can also be used for defining a template for a GAML structure, and in that case requires the following
	 * to be defined :
	 *
	 * A name (attribute "name"), optional, but better A description (attribute "value"), optional A menu name
	 * (attribute "menu"), optional A hierarchical path within this menu (attribute "path"), optional A pattern
	 * (attribute "pattern" or concatenation of the @example present in "examples" that define "isPattern" as true)
	 *
	 * (see <code>org.eclipse.jface.text.templates.Template</code>) These templates are then classified and accessible
	 * during runtime for editing models
	 *
	 *
	 * @author Benoit Gaudou & Alexis Drogoul
	 * @since 19 juin 2013 + 12/2014
	 *
	 */

	@Retention (RetentionPolicy.RUNTIME)
	// @Target({ ElementType.TYPE, ElementType.METHOD })
	// @Inherited
	public static @interface usage {

		static final String GENERAL = "General";
		static final String STATEMENT = "Statement";
		static final String OPERATOR = "Operator";
		static final String MODEL = "Model";
		static final String SPECIES = "Species";
		static final String EXPERIMENT = "Experiment";
		static final String DEFINITION = "Attribute";
		static final String CUSTOM = "Custom";
		static final String NULL = "";

		/**
		 * Value, the description of the usage.
		 *
		 * Note that for usages aiming at defining templates, the description is displayed on a tooltip in the editor.
		 * The use of the path allows to remove unecessary explanations. For instance, instead of writing : description=
		 * "This template illustrates the use of a complex form of the "create " statement, which reads agents from a
		 * shape file and uses the tabular data of the file to initialize their attributes"
		 *
		 * choose: name="Create agents from shapefile" menu=STATEMENT; path={"Create", "Complex forms"} description=
		 * "Read agents from a shape file and initialize their attributes"
		 *
		 * If no description is provided, GAMA will try to grab it from the context where the template is defined (in
		 * the documentation, for example)
		 *
		 *
		 * @return a String representing one usage of the keyword
		 */
		String value();

		/**
		 * Define the top-level menu where this template should appear. Users are free to use other names than the
		 * provided constants if necessary (i.e. "My templates"). When no menu is defined, GAMA tries to guess it from
		 * the context where the template is defined
		 */
		String menu()

		default NULL;

		/**
		 * The path indicates where to put this template in the menu. For instance, the following annotation:
		 *
		 * @template { menu = STATEMENT; path = {"Control", "If"} }
		 *
		 *           will put the template in a menu called "If", within "Control", within the top menu "Statement" When
		 *           no path is defined, GAMA will try to guess it from the context where the template is defined (i.e.
		 *           keyword of the statement, etc.)
		 *
		 */
		String[] path() default {};

		/**
		 * The name of the template should be both concise (as it will appear in a menu) and precise (to remove
		 * ambiguities between templates).
		 */
		String name()

		default NULL;

		/**
		 * Examples
		 *
		 * @return An array of String representing some examples or use-cases about how to use this element, related to
		 *         the particular usage above
		 */
		example[] examples() default {};

		/**
		 * Pattern. Alternatively, the contents of the usage can be descried using a @pattern (rather than an array
		 * of @example). The formatting of this string depends entirely on the user (e.g. including \n and \t for
		 * indentation, for instance).
		 */

		String pattern() default NULL;
	}

	@Retention (RetentionPolicy.RUNTIME)
	@Target ({})
	// @Inherited
	public static @interface example {

		/**
		 * Value.
		 *
		 * @return a String representing the expression to test or display
		 */
		String value()

		default "";

		/**
		 * var
		 *
		 * @return The variable that will be tested in the equals, if it is omitted a default variable will be used.
		 */
		String var()

		default "";

		/**
		 * equals
		 *
		 * @return The value to which the value will be compared
		 */
		String equals()

		default "";

		/**
		 * returnType
		 *
		 * @return The type of the value that should be tested
		 */
		String returnType()

		default "";

		/**
		 * isnot
		 *
		 * @return The value to which the value will be compared
		 */
		String isNot()

		default "";

		/**
		 * raises
		 *
		 * @return The exception or warning that the expression could raise.
		 */
		String raises()

		default "";

		/**
		 * isTestOnly
		 *
		 * @return isTestOnly specifies that the example should not be included into the documentation.
		 */
		boolean isTestOnly()

		default false;

		/**
		 * isExecutable
		 *
		 * @return isExecutable specifies that the example is correct GAML code that can be executed.
		 */
		boolean isExecutable()

		default true;

		/**
		 * test
		 *
		 * @return test specifies that the example is will be tested with the equals.
		 */
		boolean test()

		default true;

		/**
		 * @return whether or not this example should be treated as part of a pattern (see @usage). If true, the
		 *         developers might want to consider writing the example line (and its associated lines) using template
		 *         variables (e.g. ${my_agent})
		 */
		boolean isPattern() default false;
	}

	@Retention (RetentionPolicy.RUNTIME)
	public static @interface tests {
		test[] value() default {};
	}

	/**
	 * no_test should be used to indicate that a GAML artefact does not need to be provided with tests (either with
	 * the @test annotation or with @example). It will prevent the compiler from producing warnings in that case.
	 *
	 * @author drogoul
	 *
	 */
	@Retention (RetentionPolicy.SOURCE)
	public static @interface no_test {

		Reason value() default Reason.NONE;
	}

	/***
	 * The test annotation is intended to provide a simpler syntax than @example for producing tests. It allows to write
	 * unit tests for any GAML artefact. These tests are then automatically generated by the annotation processor and
	 * tested during the compilation of GAMA, to ensure that the code is correct (and that no regressions affect it).
	 * Tests are grouped together in test experiments (one experiment per GAMA plugin) that are automatically retrieved
	 * by GAMA (so that they are also available in the UI or headless version of the platform). Depending on where they
	 * are located, they are automatically named after the operator, constant, statement they annotate. For instance,
	 * for the '+' operator, this annotation will produce:
	 *
	 * @test("10+10=20") -> test "+" { assert 10+10=20;}
	 *
	 * It is possible to give them a more explicit name:
	 *
	 * @test(value="10+10 = 20" name="int addition") -> test "int addition" { assert 10+10=20; }
	 *
	 * Several @test() annotations can be attached to an operator or constant. In that case, the corresponding
	 * assertions are grouped together in a single GAML test statement. Example:
	 *
	 * @test("10+10=20") @test("0+0 = 0")
	 *
	 * will produce the following GAML test statement:
	 *
	 * test "+" { assert 10+10=20; assert 0+0=0; }
	 *
	 * If they are named, the name of the test statement is a concatenation of their names:
	 *
	 * @test(value="10+10=20" name="int addition" ) @test(value="10+0 = 10" name="zero neutrality")
	 *
	 * produces:
	 *
	 * test "int addition and zero neutrality" { assert 10+10=20; assert 10 + 0 = 10; }
	 *
	 * test annotations accept multi-line tests, provided that the last line is a boolean expression (i.e. an
	 * assertion). Simply separate (as you would do in GAML) the lines by a semi-colon. If there are no assertion (i.e.
	 * the last line ends with a semi-colon), the lines are simply copied to the GAML code. This can be handy if one
	 * wants to initialize a bunch of variables, for instance. These statements can even contain complete 'assert'
	 * statements.
	 *
	 * Example:
	 *
	 * @test("int a <- 10; int b <- 10;") @test("a+b = 20") @test("a+0 = a") @test("int c<- 0; assert c+b = b;")
	 *
	 * produces:
	 *
	 * test "+" { int a <- 10; int b <- 10; assert a+b = 20; assert a+0 = a; int c<- 0; assert c+b = 0; }
	 *
	 * Note that no syntax verification occurs when writing these annotations. It is up to the programmer to produce
	 * syntactically correct codes, otherwise the automated validation of models (which occurs whenever the code is
	 * committed) will fail.
	 *
	 * @author drogoul
	 *
	 */
	@Retention (RetentionPolicy.RUNTIME)
	@Repeatable (tests.class)

	public static @interface test {
		/**
		 *
		 * @return the GAML expression or list of statements that need to be tested using the 'assert' statement. For
		 *         instance, '@test( "10+10 = 20")' will generate a test containing the statement 'assert 10+10 = 20;'
		 *
		 *         To generate a multi-line test, use semi-colons to separate the statements (like in GAML). For
		 *         instance '@test( "int a <- 10; int b <- 10; a+b = 20") will generate the following test:
		 *
		 *         test plus { int a <- 10; int b <- 10; assert a+b = 20; }
		 *
		 *         If the string ends with a semi-colon, no attempt is made to infer the 'assert' statement and the
		 *         whole GAML code is simply copied into the test. This can be used to initialize variables, but also to
		 *         generate tests that will "abort" instead of "failing". For instance, consider these two annotations:
		 *
		 *         '@test("int a <- 0; float b <- 100 / a ;")' // when tested, a runtime error will make this test abort
		 *         '@test("int a <- 0; !is_error(100 / a)")' // when tested, the runtime error will be caught but the
		 *         test will fail (as the division by zero IS an error)
		 *
		 *         An empty value will produce an empty test statement (unless the @test annotation is preceded or
		 *         followed by other @test annotations)
		 *
		 */
		String value();

		/**
		 *
		 * @return the name of the test (see the general comment for the effect of naming tests). It is empty by
		 *         default.
		 */
		String name()

		default "";

		/**
		 *
		 * @return whether the test, if false, will fail or simply emit a warning. 'false' by default.
		 *
		 *         '@test("10+10 = 100" warning=true) will not be recorded as failing (i.e. it will not break the
		 *         compilation)
		 *
		 *         Note that warning=true has no effect if the value of the test does not contain assertions (and it
		 *         simply influences the last assertion if there is one)
		 */
		boolean warning() default false;

	}

	@Retention (RetentionPolicy.RUNTIME)
	@Target ({ ElementType.TYPE })
	@Inherited
	public static @interface file {

		/**
		 * The name of this type of files. This name will be used to generate two operators: name+"_file" and
		 * "is_"+name. The first operator may have variants taking one or several arguments, depending on the @builder
		 * annotations present on the class.
		 *
		 * @return a (human-understandable) string describing this type of files, suitable for use in composed operator
		 *         names (e.g. "shape", "image"...)
		 *
		 */
		String name();

		/**
		 * @return an array of strings, each representing this GAML word we can use to find the operators related to
		 *         this file in the website search feature.
		 */

		String[] concept() default {};

		/**
		 * The list of file extensions allowed for this type of files. These extensions will be used to check the
		 * validity of the file path, but also to generate the correct type of file when a path is passed to the generic
		 * "file" operator.
		 *
		 * @return an array of extensions (without the '.' delimiter) or an empty array if no specific extensions are
		 *         associated to this type of files (e.g. ["png","jpg","jpeg"...])
		 */
		String[] extensions();

		/**
		 * @return the type of the content of the buffer. Can be directly a type in IType or one of the constants
		 *         declared in ITypeProvider (in which case, the content type is searched using this provider).
		 * @see IType
		 * @see ITypeProvider
		 */
		int buffer_content()

		default ITypeProvider.NONE;

		/**
		 * @return the type of the index of the buffer. Can be directly a type in IType or one of the constants declared
		 *         in ITypeProvider (in which case, the index type is searched using this provider).
		 * @see IType
		 * @see ITypeProvider
		 */
		int buffer_index()

		default ITypeProvider.NONE;

		/**
		 *
		 * @return the type of the buffer. Can be directly a type in IType or one of the constants declared in
		 *         ITypeProvider (in which case, the type is searched using this provider).
		 * @see IType
		 * @see ITypeProvider
		 */
		int buffer_type()

		default ITypeProvider.NONE;

		/**
		 * @return an array of strings, each representing a category in which this constant can be classified (for
		 *         documentation indexes)
		 */

		String[] category() default {};

		doc[] doc() default {};
	}

	/**
	 * Written by gaudou Modified on 24 mars 2014
	 *
	 * Used to annotate fields that are used as constants in GAML.
	 *
	 */
	@Retention (RetentionPolicy.RUNTIME)
	@Target ({ ElementType.FIELD })
	public static @interface constant {

		/**
		 * @return an array of strings, each representing a category in which this constant can be classified (for
		 *         documentation indexes)
		 */

		String[] category() default {};

		/**
		 * @return an array of strings, each representing this GAML word we can use to find the constant in the website
		 *         search feature.
		 */

		String[] concept() default {};

		/**
		 * @return a string representing the basic keyword for the constant. Does not need to be unique throughout GAML
		 *
		 */
		String value();

		/**
		 * @return an Array of strings, each representing a possible alternative name for the constant. Does not need to
		 *         be unique throughout GAML.
		 *
		 **/
		String[] altNames() default {};

		/**
		 * Doc.
		 *
		 * @return the documentation attached to this constant.
		 * @see doc
		 */
		doc[] doc() default {};
	}

}
