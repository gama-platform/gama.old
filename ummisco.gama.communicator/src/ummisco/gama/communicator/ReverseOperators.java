package ummisco.gama.communicator;

import com.thoughtworks.xstream.XStream;

import msi.gama.runtime.IScope;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import ummisco.gama.communicator.common.remoteObject.GamaAgentConverter;
import ummisco.gama.communicator.common.remoteObject.GamaScopeConverter;
import ummisco.gama.communicator.common.remoteObject.GamaSimulationAgentConverter;
import ummisco.gama.serializer.gamaType.converters.GamaMapConverter;
import ummisco.gama.serializer.gamaType.converters.GamaPairConverter;
import ummisco.gama.serializer.gamaType.converters.GamaPointConverter;

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
