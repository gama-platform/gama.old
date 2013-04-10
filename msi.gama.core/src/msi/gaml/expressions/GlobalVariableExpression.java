/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.expressions;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.types.IType;

public class GlobalVariableExpression extends VariableExpression {

	protected GlobalVariableExpression(final String n, final IType type, final IType contentType,
		IType keyType, final boolean notModifiable, final IDescription world) {
		super(n, type, contentType, keyType, notModifiable, world);
	}

	@Override
	public Object value(final IScope scope) throws GamaRuntimeException {
		return scope.getGlobalVarValue(getName());
	}

	@Override
	public void setVal(final IScope scope, final Object v, final boolean create)
		throws GamaRuntimeException {
		if ( isNotModifiable ) { return; }
		scope.setGlobalVarValue(getName(), v);
	}

	@Override
	public String getTitle() {
		IDescription desc = getDefinitionDescription();
		boolean isParameter =
			desc == null ? false : desc.getSpeciesContext().getVariable(getName()).isParameter();
		return "global " +
			(isParameter ? "parameter" : isNotModifiable ? "constant" : "attribute") + " " +
			getName() + " of type " + typeToString();
	}

	/**
	 * @see msi.gaml.expressions.IExpression#getDocumentation()
	 */
	@Override
	public String getDocumentation() {
		IDescription desc = getDefinitionDescription();
		return "Of type: " + type.toString() +
			(desc == null ? "<br>Built In" : "<br>Defined in " + desc.getTitle());
	}

}
