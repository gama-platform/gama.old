/*******************************************************************************************************
 *
 * Sanction.java, in msi.gaml.architecture.simplebdi, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
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
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class Sanction.
 */
@vars ({ @variable (
		name = "name",
		type = IType.STRING,
		doc = @doc ("The name of this sanction")),

})
public class Sanction implements IValue {

	/** The sanction statement. */
	private SanctionStatement sanctionStatement;

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	@getter ("name")
	public String getName() { return this.sanctionStatement.getName(); }

	/**
	 * Gets the sanction statement.
	 *
	 * @return the sanction statement
	 */
	public SanctionStatement getSanctionStatement() { return this.sanctionStatement; }

	/**
	 * Instantiates a new sanction.
	 */
	public Sanction() {
	}

	/**
	 * Instantiates a new sanction.
	 *
	 * @param statement the statement
	 */
	public Sanction(final SanctionStatement statement) {
		this.sanctionStatement = statement;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if ((obj == null) || (getClass() != obj.getClass())) return false;
		final Sanction other = (Sanction) obj;
		if (!Objects.equals(sanctionStatement, other.sanctionStatement)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(sanctionStatement);
	}

	@Override
	public String serializeToGaml(final boolean includingBuiltIn) {
		return "Sanction( " + sanctionStatement + ")";
	}

	@Override
	public IType<?> getGamlType() {
		return Types.get(SanctionType.id);
		// return null;
	}

	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return "Sanction( " + sanctionStatement + ")";
	}

	@Override
	public IValue copy(final IScope scope) throws GamaRuntimeException {
		return new Sanction(sanctionStatement);
	}

}
