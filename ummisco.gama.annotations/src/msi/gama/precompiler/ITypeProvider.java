/*********************************************************************************************
 *
 * 'ITypeProvider.java, in plugin ummisco.gama.annotations, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.precompiler;

/**
 * Written by drogoul Modified on 2 ao�t 2010. See IType
 *
 * @todo Description
 *
 */
public interface ITypeProvider {
	/**
	 * Internal value used to distinguish fixed constants from the "index-based" constants below
	 */
	static final int INDEXED_TYPES = -100;
	/**
	 * The type returned is the type of the argument at index 1...n (i.e., 1 for the first element, 2 for the second,
	 * etc.) so that, for instance, TYPE_AT_INDEX + 2 represents the type of the SECOND argument (equivalent to
	 * SECOND_TYPE). Musth be followed by "+ n" with n>0 to be significant.
	 */
	static final int TYPE_AT_INDEX = -200;
	/**
	 * The type returned is the contents type of the argument at index 1...n (i.e., 1 for the first element, 2 for the
	 * second, etc.) so that, for instance, CONTENT_TYPE_AT_INDEX + 1 represents the contents type of the FIRST argument
	 * (equivalent to FIRST_CONTENT_TYPE). Must be followed by "+ n" with n>0 to be significant.
	 */
	static final int CONTENT_TYPE_AT_INDEX = -300;
	/**
	 * The type returned is the type denoted by the argument at index 1...n (i.e., 1 for the first element, 2 for the
	 * second, etc.) so that, for instance, DENOTED_TYPE_AT_INDEX + 2 represents the type denoted by the SECOND argument
	 * (equivalent to SECOND_TYPE). Musth be followed by "+ n" with n>0 to be significant.
	 */
	static final int DENOTED_TYPE_AT_INDEX = -350;
	/**
	 * The type returned is the key type of the argument at index 1...n (i.e., 1 for the first element, 2 for the
	 * second, etc.) so that, for instance, KEY_TYPE_AT_INDEX + 3 represents the key type of the THIRD argument. Musth
	 * be followed by "+ n" with n>0 to be significant.
	 */
	static final int KEY_TYPE_AT_INDEX = -400;
	/**
	 * This constant can be used to indicate that the int type is superseded by float (i.e. if the type is int, then
	 * float should be returned)
	 */
	static final int FLOAT_IN_CASE_OF_INT = -1000;
	/**
	 * The type returned is the content type of the first child of the expression
	 *
	 * @deprecated use CONTENT_TYPE_AT_INDEX + 1 instead (or OWNER_CONTENT_TYPE in case of a field)
	 */

	@Deprecated static final int FIRST_CONTENT_TYPE = CONTENT_TYPE_AT_INDEX + 1;
	/**
	 * The type returned is the content type of the first child (owner) of the expression
	 */
	static final int OWNER_CONTENT_TYPE = FIRST_CONTENT_TYPE;
	/**
	 * The type returned is the key type of the first child of the expression
	 *
	 * @deprecated use KEY_TYPE_AT_INDEX + 1 instead (or OWNER_KEY_TYPE in case of a field)
	 */
	@Deprecated static final int FIRST_KEY_TYPE = KEY_TYPE_AT_INDEX + 1;
	/**
	 * The type returned is the key type of the first child (owner) of the expression
	 */
	static final int OWNER_KEY_TYPE = FIRST_KEY_TYPE;
	/**
	 * The type returned is the content type of the second child of the expression
	 *
	 * @deprecated use CONTENT_TYPE_AT_INDEX+2 instead
	 */
	@Deprecated static final int SECOND_CONTENT_TYPE = CONTENT_TYPE_AT_INDEX + 2;
	/**
	 * The type returned is the key type of the second child of the expression
	 *
	 * @deprecated use KEY_TYPE_AT_INDEX + 2 instead
	 */
	@Deprecated static final int SECOND_KEY_TYPE = KEY_TYPE_AT_INDEX + 2;
	/**
	 * The type returned is the type denoted by the second child
	 */
	static final int SECOND_DENOTED_TYPE = -32;
	/**
	 * The type returned is the type of the first child of the expression
	 *
	 * @deprecated use TYPE_AT_INDEX + 1 instead (or OWNER_TYPE in case of a field)
	 */
	@Deprecated static final int FIRST_TYPE = TYPE_AT_INDEX + 1;
	/**
	 * For variables, represents the type of the owner (i.e. the species) holding this attribute
	 */
	static final int OWNER_TYPE = FIRST_TYPE;
	/**
	 * The type returned is the type of the second child of the expression
	 *
	 * @deprecated use TYPE_AT_INDEX + 2 instead
	 */
	@Deprecated static final int SECOND_TYPE = TYPE_AT_INDEX + 2;
	/**
	 * The type returned is the type of the second child of the expression or its contents type if it is a container
	 */
	static final int SECOND_CONTENT_TYPE_OR_TYPE = -25;
	/**
	 * The type returned is the type of the first child of the expression or its contents type if it is a container
	 */
	static final int FIRST_CONTENT_TYPE_OR_TYPE = -26;
	/**
	 * The type returned is the type of the current model (simulation)
	 */
	static final int MODEL_TYPE = -27;
	/**
	 * The generic type experiment (which does not exist as a type -- at least not yet)
	 */
	static final int EXPERIMENT_TYPE = -31;
	/**
	 * The type of the agents mirrored by a species
	 */
	static final int MIRROR_TYPE = -28;
	/**
	 * The type returned is the type of the macro-agent
	 */
	static final int MACRO_TYPE = -29;
	/**
	 * The type returned is the type of the internal buffer of the object (when it is a file)
	 */
	static final int WRAPPED = -30;
	/**
	 * The type returned is the type of the expression itself (i.e. species)
	 */
	static final int TYPE = -14;
	/**
	 * The type should not (or cannot) be computed
	 */
	static final int NONE = -13;
	/**
	 * The type returned is the common supertype of both operands (which must match, otherwise unknown is returned).
	 */
	static final int BOTH = -21;
	/**
	 * The type returned is the common supertype of all operands (which must match, otherwise unknown is returned)
	 */
	static final int ALL = BOTH;
	/**
	 * The type returned is the content type of the first element of the child (if the child is a container) --
	 * EXPERIMENTAL RIGHT NOW (and probably limited to the matrix and as_matrix operators) e.g. : matrix ([[4, 5, 6],[1,
	 * 2, 4]]) should get int as a content type.
	 */
	static final int FIRST_ELEMENT_CONTENT_TYPE = -22;

}
