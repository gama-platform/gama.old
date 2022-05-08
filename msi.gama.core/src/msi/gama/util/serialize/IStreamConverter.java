package msi.gama.util.serialize;

import msi.gama.runtime.IScope;

public interface IStreamConverter {
	 abstract String convertObjectToJSONStream(final IScope scope, final Object o);
}
