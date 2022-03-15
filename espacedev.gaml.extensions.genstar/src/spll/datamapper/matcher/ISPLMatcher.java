package spll.datamapper.matcher;

import core.metamodel.entity.AGeoEntity;
import core.metamodel.value.IValue;
import spll.datamapper.variable.ISPLVariable;

public interface ISPLMatcher<V extends ISPLVariable, T> {

	public String getName();
	
	public T getValue();
	
	public boolean expandValue(T expand);
	
	public V getVariable();

	public AGeoEntity<? extends IValue> getEntity();
	
	public String toString();

}
