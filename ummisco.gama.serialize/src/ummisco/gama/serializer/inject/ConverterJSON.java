package ummisco.gama.serializer.inject;

import msi.gama.runtime.IScope;
import msi.gama.util.serialize.IStreamConverter;
import ummisco.gama.serializer.factory.StreamConverter;

public class ConverterJSON implements IStreamConverter {
	
	@Override
	public String convertObjectToJSONStream(IScope scope, Object o) {
		return StreamConverter.convertObjectToJSONStream(scope,o);
	}
}
