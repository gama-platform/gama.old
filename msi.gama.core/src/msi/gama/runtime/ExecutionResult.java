/*******************************************************************************************************
 *
 * ExecutionResult.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.runtime;

/**
 * The result of executions. 'passed' represents the success or failure of the computation, value its result
 *
 * @author drogoul
 *
 */
@FunctionalInterface
public interface ExecutionResult {
	
	/**
	 * The Interface WithValue.
	 */
	@FunctionalInterface
	interface WithValue extends ExecutionResult {

		/**
		 * Gets the value.
		 *
		 * @return the value
		 */
		@Override
		Object getValue();

		/**
		 * Passed.
		 *
		 * @return true, if successful
		 */
		@Override
		default boolean passed() {
			return true;
		}

	}

	/**
	 * With value.
	 *
	 * @param value the value
	 * @return the execution result
	 */
	// FACTORY METHODS
	static ExecutionResult withValue(final Object value) {
		if (value == null) { return PASSED_WITH_NULL; }
		return (WithValue) () -> value;
	}

	/**
	 * With value.
	 *
	 * @param value the value
	 * @return the execution result
	 */
	static ExecutionResult withValue(final boolean value) {
		return value ? PASSED : PASSED_WITH_FALSE;
	}

	/** The Constant PASSED. */
	ExecutionResult PASSED = () -> true;

	/** The passed with null. */
	ExecutionResult PASSED_WITH_NULL = (WithValue) () -> null;
	
	/** The passed with false. */
	ExecutionResult PASSED_WITH_FALSE = (WithValue) () -> false;

	/** The Constant FAILED. */
	ExecutionResult FAILED = () -> false;

	/**
	 * Passed.
	 *
	 * @return true, if successful
	 */
	boolean passed();

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	default Object getValue() {
		return passed();
	}

}