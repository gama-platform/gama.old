package msi.gama.runtime;

/**
 * The result of executions. 'passed' represents the success or failure of the computation, value its result
 *
 * @author drogoul
 *
 */
@FunctionalInterface
public interface ExecutionResult {
	@FunctionalInterface
	interface WithValue extends ExecutionResult {

		@Override
		Object getValue();

		@Override
		default boolean passed() {
			return true;
		}

	}

	// FACTORY METHODS
	static ExecutionResult withValue(final Object value) {
		if (value == null) { return PASSED_WITH_NULL; }
		return (WithValue) () -> value;
	}

	static ExecutionResult withValue(final boolean value) {
		return value ? PASSED : PASSED_WITH_FALSE;
	}

	/** The Constant PASSED. */
	ExecutionResult PASSED = () -> true;

	ExecutionResult PASSED_WITH_NULL = (WithValue) () -> null;
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