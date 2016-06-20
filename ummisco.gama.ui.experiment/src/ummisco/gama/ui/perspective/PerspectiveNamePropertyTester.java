package ummisco.gama.ui.perspective;

import org.eclipse.core.expressions.PropertyTester;

public class PerspectiveNamePropertyTester extends PropertyTester {

	@Override
	public boolean test(final Object receiver, final String property, final Object[] args, final Object expectedValue) {
		final String s = receiver instanceof String ? (String) receiver : "";
		final String in = expectedValue instanceof String ? (String) expectedValue : "";
		return s.contains(in);
	}

}
