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
	@doc(value = "a new predicate with the given properties (name, values)",
		examples = @example(value = "predicate(\"people to meet\", people1 )", test = false))
	public static Predicate newPredicate(final String name, final Map values) throws GamaRuntimeException {
		return new Predicate(name,values);
	}
	
	@operator(value = "new_predicate", can_be_const = true, category = { "BDI" })
	@doc(value = "a new predicate with the given properties (name, values)",
		examples = @example(value = "predicate(\"people to meet\", people1, [\"time\"::10])", test = false))
	public static Predicate newPredicate(final String name, final Map values, final Double priority) throws GamaRuntimeException {
		return new Predicate(name,/*value,*/priority,values);
	}
	
	@operator(value = "with_priority", can_be_const = true, category = { "BDI" })
	@doc(value = "change the priority of the given predicate",
		examples = @example(value = "predicate with_priority 2", test = false))
	public static Predicate withPriority(final Predicate predicate, final Double priority) throws GamaRuntimeException {
		predicate.priority = priority;
		return predicate;
	}
	
	@operator(value = "with_values", can_be_const = true, category = { "BDI" })
	@doc(value = "change the parameters of the given predicate",
		examples = @example(value = "predicate with_parameter [\"time\"::10]", test = false))
	public static Predicate withValues(final Predicate predicate, final Map values) throws GamaRuntimeException {
		predicate.values = values;
		return predicate;
	}
	

}
