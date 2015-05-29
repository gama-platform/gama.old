package msi.gaml.architecture.simplebdi;

import java.util.ArrayList;
import java.util.List;
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
	@doc(value = "a new predicate with the given is_true (name, is_true)",
		examples = @example(value = "predicate(\"hasWater\", true )", test = false))
	public static Predicate newPredicate(final String name, final Boolean ist) throws GamaRuntimeException {
		return new Predicate(name,values,ist);
	}
	
	@operator(value = "new_predicate", can_be_const = true, category = { "BDI" })
	@doc(value = "a new predicate with the given properties (name, values)",
		examples = @example(value = "predicate(\"people to meet\", people1, [\"time\"::10])", test = false))
	public static Predicate newPredicate(final String name, final Map values, final Double priority) throws GamaRuntimeException {
		return new Predicate(name,/*value,*/priority,values);
	}
	
	@operator(value = "new_predicate", can_be_const = true, category = { "BDI" })
	@doc(value = "a new predicate with the given properties (name, values, is_true)",
		examples = @example(value = "predicate(\"people to meet\", [\"time\"::10], true)", test = false))
	public static Predicate newPredicate(final String name, final Map values, final Boolean truth) throws GamaRuntimeException {
		return new Predicate(name,/*value,*/values,is_true);
	}
	
	@operator(value = "set_truth", can_be_const = true, category = { "BDI" })
	@doc(value = "change the is_true value of the given predicate",
		examples = @example(value = "predicate set_truth false", test = false))
	public static Predicate withTruth(final Predicate predicate, final Boolean truth) throws GamaRuntimeException {
		predicate.is_true = truth;
		return predicate;
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
	
	@operator(value = "and", can_be_const = true, category = { "BDI" })
	@doc(value = "create a new predicate from two others by including them as subintentions",
			examples = @example(value = "predicate1 and predicate2", test=false))
	public static Predicate and(final Predicate pred1,final Predicate pred2){
		Predicate tempPred=new Predicate(pred1.getName()+"_and_"+pred2.getName());
		List<Predicate> tempList= new ArrayList<Predicate>();
		tempList.add(pred1);
		tempList.add(pred2);
		tempPred.setSubintentions(tempList);
		return tempPred;
	}

}
