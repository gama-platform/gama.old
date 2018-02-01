package msi.gaml.architecture.simplebdi;

import msi.gama.common.interfaces.IValue;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@vars ({ @var (
		name = "name",
		type = IType.STRING,
		doc = @doc ("The name of this norm")),
		@var (
				name = NormStatement.INTENTION,
				type = IType.NONE,
				doc = @doc ("A string representing the current intention of this BDI plan")),
		@var (
				name = SimpleBdiArchitecture.FINISHEDWHEN,
				type = IType.STRING),
		@var (
				name = SimpleBdiArchitecture.INSTANTANEAOUS,
				type = IType.STRING)
		/*
		 * @var(name = "value", type = IType.NONE),
		 * 
		 * @var(name = "parameters", type = IType.MAP),
		 */
		// @var(name = "values", type = IType.MAP), @var(name = "priority", type
		// = IType.FLOAT),
		// @var(name = "date", type = IType.FLOAT), @var(name = "subintentions",
		// type = IType.LIST),
		// @var(name = "on_hold_until", type = IType.NONE)
})

//Classe qui permet de définir les normes comme type, contenant le norm statement, sur l'exemple des plans
public class Norm implements IValue{

	private NormStatement normStatement;
	private Boolean isViolated;
	
	@getter ("name")
	public String getName() {
		return this.normStatement.getName();
	}
	
	@getter ("when")
	public String getWhen() {
		return this.normStatement._when.serialize(true);
	}
	
	@getter (NormStatement.INTENTION)
	public Predicate getIntention(final IScope scope) {
		return (Predicate) this.normStatement._intention.value(scope);
	}
	
	public NormStatement getNormStatement() {
		return this.normStatement;
	}
	
	public Boolean getViolated(){
		return this.isViolated;
	}
	
	public Norm(){
		super();
		isViolated = false;
	}
	
	public Norm(final NormStatement statement) {
		super();
		this.normStatement = statement;
		isViolated = false;
	}
	
	public void setViolation(final Boolean violation){
		this.isViolated = violation;
	}
	
	@Override
	public String serialize(boolean includingBuiltIn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		final Norm other = (Norm) obj;
		if (normStatement == null) {
			if (other.normStatement != null) { return false; }
		} else if (!normStatement.equals(other.normStatement)) { return false; }
		return true;
	}
	
	@Override
	public IType<?> getType() {
		return Types.get(NormType.id);
//		return null;
	}

	@Override
	public String stringValue(IScope scope) throws GamaRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IValue copy(IScope scope) throws GamaRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

}
