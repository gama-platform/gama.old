package msi.gaml.architecture.simplebdi;

import java.util.Map;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class Operators {

	@operator(value = "new_predicate", can_be_const = true, category = { "BDI" })
	@doc(value = "a new predicate with the given properties (name)",
		examples = @example(value = "predicate(\"people to meet\")", test = false))
	public static Predicate newPredicate(final String name) throws GamaRuntimeException {
		return new Predicate(name);
	}
	
	@operator(value = "new_predicate", can_be_const = true, category = { "BDI" })
	@doc(value = "a new predicate with the given properties (name, value)",
		examples = @example(value = "predicate(\"people to meet\", people1 )", test = false))
	public static Predicate newPredicate(final String name, final Object value) throws GamaRuntimeException {
		return new Predicate(name,value);
	}
	
	@operator(value = "new_predicate", can_be_const = true, category = { "BDI" })
	@doc(value = "a new predicate with the given properties (name, value, priority)",
		examples = @example(value = "predicate(\"people to meet\", people1, 2 )", test = false))
	public static Predicate newPredicate(final String name, final Object value, final Double priority) throws GamaRuntimeException {
		return new Predicate(name,value,priority);
	}
	
	@operator(value = "new_predicate", can_be_const = true, category = { "BDI" })
	@doc(value = "a new predicate with the given properties (name, value, parameters)",
		examples = @example(value = "predicate(\"people to meet\", people1, [\"time\"::10])", test = false))
	public static Predicate newPredicate(final String name, final Object value, final Map parameters, final Double priority) throws GamaRuntimeException {
		return new Predicate(name,value,priority,parameters);
	}
	
	@operator(value = "with_priority", can_be_const = true, category = { "BDI" })
	@doc(value = "change the priority of the given predicate",
		examples = @example(value = "predicate with_priority 2", test = false))
	public static Predicate withPriority(final Predicate predicate, final Double priority) throws GamaRuntimeException {
		predicate.priority = priority;
		return predicate;
	}
	
	@operator(value = "with_parameters", can_be_const = true, category = { "BDI" })
	@doc(value = "change the parameters of the given predicate",
		examples = @example(value = "predicate with_parameter [\"time\"::10]", test = false))
	public static Predicate withParameters(final Predicate predicate, final Map parameters) throws GamaRuntimeException {
		predicate.parameters = parameters;
		return predicate;
	}

	@operator(value = "with_value", can_be_const = true, category = { "BDI" })
	@doc(value = "change the value of the given predicate",
		examples = @example(value = "predicate with_value people1", test = false))
	public static Predicate withValue(final Predicate predicate, final Object value) throws GamaRuntimeException {
		predicate.value = value;
		return predicate;
	}
	

}
