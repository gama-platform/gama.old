package msi.gama.runtime;

/**
 * Use this class to accumulate a series of execution results. Only the last one marked as 'passed' will be returned
 *
 * @author drogoul
 *
 */
public class AccumulatingExecutionResult implements ExecutionResult {

	boolean passed = true;
	Object value = null;

	/**
	 * Accepts an execution result
	 *
	 * @param e
	 *            the execution result
	 * @return true, if successful
	 */
	public boolean accept(final ExecutionResult e) {
		passed = passed && e.passed();
		if (passed) {
			this.value = e.getValue();
		}
		return passed;
	}

	@Override
	public boolean passed() {
		return passed;
	}

	@Override
	public Object getValue() {
		return value;
	}

}