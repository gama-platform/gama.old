package gama_analyzer;

import msi.gama.runtime.IScope;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class GamaScopeConverter implements Converter {

	@Override
	public boolean canConvert(Class arg0) {
		Class<?>[] allInterface=arg0.getInterfaces();
		for( Class<?> c:allInterface)
		{
			if(c.equals(IScope.class))
				return true;
		}
		return false;
	}

	@Override
	public void marshal(Object arg0, HierarchicalStreamWriter writer,
			MarshallingContext arg2) {
		IScope scope = (IScope) arg0;
		writer.startNode("IScope");
        writer.setValue(scope.getName().toString());
        writer.endNode();

	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext arg1) {
		 reader.moveDown();
		 String res = reader.getValue();
		 reader.moveUp();
		
		return res;
	}

}
