/*******************************************************************************************************
 *
 * BDIPlan.java, in msi.gaml.architecture.simplebdi, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.architecture.simplebdi;

import java.util.Objects;

import msi.gama.common.interfaces.IValue;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.json.Json;
import msi.gama.util.file.json.JsonValue;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class BDIPlan.
 */
@vars ({ @variable (
		name = "name",
		type = IType.STRING,
		doc = @doc ("The name of this BDI plan")),
		@variable (
				name = "todo",
				type = IType.STRING,
				doc = @doc ("represent the when facet of a plan")),
		@variable (
				name = SimpleBdiPlanStatement.INTENTION,
				type = MentalStateType.id,
				doc = @doc ("A string representing the current intention of this BDI plan")),
		@variable (
				name = SimpleBdiArchitecture.FINISHEDWHEN,
				type = IType.STRING,
				doc = @doc ("a string representing the finished condition of this plan")),
		@variable (
				name = SimpleBdiArchitecture.INSTANTANEAOUS,
				type = IType.STRING,
				doc = @doc ("indicates if the plan is instantaneous")) })
public class BDIPlan implements IValue {

	@Override
	public JsonValue serializeToJson(final Json json) {
		return json.typedObject(getGamlType(), "name", getName());
	}

	/** The planstatement. */
	private SimpleBdiPlanStatement planstatement;

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@getter ("name")
	public String getName() { return this.planstatement.getName(); }

	/**
	 * Gets the when.
	 *
	 * @return the when
	 */
	@getter ("todo")
	public String getWhen() { return this.planstatement._when.serializeToGaml(true); }

	/**
	 * Gets the finished when.
	 *
	 * @return the finished when
	 */
	@getter (SimpleBdiArchitecture.FINISHEDWHEN)
	public String getFinishedWhen() { return this.planstatement._executedwhen.serializeToGaml(true); }

	/**
	 * Gets the intention.
	 *
	 * @param scope
	 *            the scope
	 * @return the intention
	 */
	@getter (SimpleBdiPlanStatement.INTENTION)
	public Predicate getIntention(final IScope scope) {
		return (Predicate) this.planstatement._intention.value(scope);
	}

	/**
	 * Gets the instantaneous.
	 *
	 * @return the instantaneous
	 */
	@getter (SimpleBdiArchitecture.INSTANTANEAOUS)
	public String getInstantaneous() { return this.planstatement._instantaneous.serializeToGaml(true); }

	/**
	 * Gets the plan statement.
	 *
	 * @return the plan statement
	 */
	public SimpleBdiPlanStatement getPlanStatement() { return this.planstatement; }

	/**
	 * Instantiates a new BDI plan.
	 */
	public BDIPlan() {}

	/**
	 * Instantiates a new BDI plan.
	 *
	 * @param statement
	 *            the statement
	 */
	public BDIPlan(final SimpleBdiPlanStatement statement) {
		this.planstatement = statement;
	}

	/**
	 * Sets the simple bdi plan statement.
	 *
	 * @param statement
	 *            the new simple bdi plan statement
	 */
	public void setSimpleBdiPlanStatement(final SimpleBdiPlanStatement statement) {
		this.planstatement = statement;

	}

	@Override
	public String toString() {
		return serializeToGaml(true);
	}

	@Override
	public String serializeToGaml(final boolean includingBuiltIn) {
		return "BDIPlan(" + planstatement.getName()
		// +(values == null ? "" : "," + values) +
				+ ")";
	}

	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return "BDIPlan(" + planstatement.getName() + ")"
		// +(values == null ? "" : "," + values)
		;
	}

	@Override
	public IValue copy(final IScope scope) throws GamaRuntimeException {
		return new BDIPlan(planstatement);
	}

	/**
	 * Checks if is similar name.
	 *
	 * @param other
	 *            the other
	 * @return true, if is similar name
	 */
	public boolean isSimilarName(final BDIPlan other) {
		if (this == other) return true;
		if (other == null || !Objects.equals(planstatement, other.planstatement)) return false;
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(planstatement);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		final BDIPlan other = (BDIPlan) obj;
		if (!Objects.equals(planstatement, other.planstatement)) return false;
		return true;
	}

	/**
	 * Method getType()
	 *
	 * @see msi.gama.common.interfaces.ITyped#getGamlType()
	 */
	@Override
	public IType<?> getGamlType() { return Types.get(IType.TYPE_ID); }

}
