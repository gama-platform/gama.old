package spll.datamapper.variable;

import core.metamodel.value.IValue;

public interface ISPLVariable {

	public IValue getValue();
	
	public String getStringValue();
	
	public String getName();
	
}
