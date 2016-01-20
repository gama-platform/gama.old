package ummisco.gama.communicator;

import com.thoughtworks.xstream.XStream;

import msi.gama.runtime.IScope;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import ummisco.gama.serializer.gamaType.converters.GamaAgentConverter;
import ummisco.gama.serializer.gamaType.converters.GamaMapConverter;
import ummisco.gama.serializer.gamaType.converters.GamaPairConverter;
import ummisco.gama.serializer.gamaType.converters.GamaPointConverter;
import ummisco.gama.serializer.gamaType.converters.GamaScopeConverter;
import ummisco.gama.serializer.gamaType.converters.GamaSimulationAgentConverter;

public class ReverseOperators {
	@operator(value = "saveScope")
	@doc("")
	public static String coucouScope(IScope scope, int i) {
		XStream xstream = new XStream();
		xstream.registerConverter(new GamaAgentConverter());
		xstream.registerConverter(new GamaScopeConverter());
		xstream.registerConverter(new GamaPointConverter());
		xstream.registerConverter(new GamaPairConverter());
		xstream.registerConverter(new GamaMapConverter());
		xstream.registerConverter(new GamaSimulationAgentConverter());
		
		return (String) xstream.toXML(scope);
	}
}
