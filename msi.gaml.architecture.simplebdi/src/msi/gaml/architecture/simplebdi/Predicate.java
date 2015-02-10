/*********************************************************************************************
 * 
 *
 * 'Predicate.java', in plugin 'msi.gaml.architecture.simplebdi', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.architecture.simplebdi;

import gnu.trove.map.hash.THashMap;

import java.util.Map;

import msi.gama.common.interfaces.IValue;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.IType;

@vars({ @var(name = "name", type = IType.STRING), @var(name = "value", type = IType.NONE), @var(name = "parameters", type = IType.MAP), @var(name = "priority", type = IType.FLOAT) })
public class Predicate implements IValue{

	String name;
	Object value;
	Map parameters;
	
	@getter("name")
	public String getName() {
	    return name;
	}
	
	@getter("value")
	public Object getValue() {
	    return value;
	}
	
	@getter("parameters")
	public Map getParameters() {
	    return parameters;
	}
	
	
	
	public Predicate(String name, Object value, Map parameters) {
		super();
		this.name = name;
		this.value = value;
		this.parameters = parameters;
	}

	
	public void setName(String name) {
		this.name = name;
	}
	
	public Predicate(String name, Object value) {
		super();
		this.name = name;
		this.value = value;
	}

	@Override
	public String toGaml() {
		return "predicate(" + name + ((value == null) ? "": ("," + value)) + ((parameters == null) ? "": ("," + parameters)) + ")";
	}

	@Override
	public String stringValue(IScope scope) throws GamaRuntimeException {
		return name + ((value == null) ? "": ("," + value)) + ((parameters == null) ? "": ("," + parameters));
	}

	@Override
	public IValue copy(IScope scope) throws GamaRuntimeException {
		return new Predicate(name,value,new THashMap(parameters));
	}

}
