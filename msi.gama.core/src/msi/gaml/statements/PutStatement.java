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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.statements;

import msi.gama.common.interfaces.*;
import msi.gama.precompiler.GamlAnnotations.combination;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IContainer;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.PutStatement.PutValidator;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 6 févr. 2010
 * 
 * @todo Description
 * 
 */

@facets(value = {
	@facet(name = IKeyword.AT, type = IType.NONE, optional = true),
	@facet(name = IKeyword.KEY, type = IType.NONE, optional = true),
	@facet(name = IKeyword.ALL, type = IType.NONE, optional = true),
	@facet(name = IKeyword.ITEM, type = IType.NONE, optional = true),
	@facet(name = IKeyword.EDGE, type = IType.NONE, optional = true),
	@facet(name = IKeyword.WEIGHT, type = IType.FLOAT, optional = true),
	@facet(name = IKeyword.IN, type = { IType.CONTAINER, IType.SPECIES, IType.AGENT, IType.GEOMETRY }, optional = false) }, combinations = {
	@combination({ IKeyword.AT, IKeyword.ITEM, IKeyword.IN }), @combination({ IKeyword.ALL, IKeyword.IN }) }, omissible = IKeyword.ITEM)
@symbol(name = IKeyword.PUT, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT }, symbols = IKeyword.CHART)
@validator(PutValidator.class)
public class PutStatement extends AddStatement {

	public static class PutValidator extends ContainerValidator {

		@Override
		public void validate(final IDescription cd) {
			Facets f = cd.getFacets();
			final IExpression index = f.getExpr(AT, KEY);
			final IExpression whole = f.getExpr(ALL);
			if ( whole != null && whole.getType().id() != IType.BOOL ) {
				cd.error("Put cannot be used to add several values", IGamlIssue.MISSING_FACET, ALL);
				return;
			}
			final boolean all = whole == null ? false : !whole.literalValue().equals(FALSE);
			if ( !all && index == null ) {
				cd.error("Put needs a valid index (facets 'at:' or 'key:') ", IGamlIssue.MISSING_FACET, AT);
			} else {
				super.validate(cd);
			}
		}

	}

	public PutStatement(final IDescription desc) {
		super(desc);
		setName("put in " + list.toGaml());
	}

	@Override
	protected void apply(final IScope scope, final Object object, final Object position,
		final IContainer.Modifiable container) throws GamaRuntimeException {
		// Object toPut = container.buildValue(scope, object, containerType);
		if ( !asAll ) {
			if ( !container.checkBounds(scope, position, false) ) { throw GamaRuntimeException.error("Index " +
				position + " out of bounds of " + list.toGaml()); }
			container.setValueAtIndex(scope, position, object);
		} else {
			container.setAllValues(scope, object);
		}
	}
}
