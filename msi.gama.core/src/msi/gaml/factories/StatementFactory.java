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
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.factories;

import static msi.gaml.factories.DescriptionValidator.*;
import static msi.gaml.factories.VariableValidator.*;
import java.util.List;
import msi.gama.common.interfaces.*;
import msi.gama.precompiler.GamlAnnotations.factory;
import msi.gama.precompiler.*;
import msi.gaml.architecture.finite_state_machine.*;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.*;
import msi.gaml.statements.*;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 8 f√©vr. 2010
 * 
 * @todo Description
 * 
 */
@factory(handles = { ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.SINGLE_STATEMENT,
	ISymbolKind.BEHAVIOR, ISymbolKind.ACTION })
public class StatementFactory extends SymbolFactory implements IKeyword {

	public StatementFactory(final List<Integer> handles) {
		super(handles);
	}

	@Override
	protected StatementDescription buildDescription(final ISyntacticElement source,
		final IChildrenProvider cp, final IDescription superDesc, final SymbolProto md) {
		return new StatementDescription(source.getKeyword(), superDesc, cp, md.hasScope(),
			md.hasArgs(), source);
	}

	@Override
	protected void privateValidate(final IDescription desc) {
		// GuiUtils.debug("Validating statement " + desc);
		super.privateValidate(desc);
		String kw = desc.getKeyword();
		StatementDescription cd = (StatementDescription) desc;
		if ( kw.equals(ARG) || kw.equals(LET) || kw.equals(ACTION) || kw.equals(REFLEX) ) {
			assertNameIsNotReserved(cd); // Actions, reflexes, states, etc.
			assertNameIsNotTypeOrSpecies(cd);
			// assertNameIsUniqueInSuperDescription(desc);
		} else if ( kw.equals(STATE) ) {
			assertFacetValueIsUniqueInSuperDescription(cd, FsmStateStatement.INITIAL,
				GamlExpressionFactory.TRUE_EXPR);
			assertFacetValueIsUniqueInSuperDescription(cd, FsmStateStatement.FINAL,
				GamlExpressionFactory.TRUE_EXPR);
			assertAtLeastOneChildWithFacetValueInSuperDescription(cd, FsmStateStatement.INITIAL,
				GamlExpressionFactory.TRUE_EXPR);
		} else if ( kw.equals(DO) ) {
			assertActionIsExisting(cd, ACTION);
		} else if ( kw.equals(FsmTransitionStatement.TRANSITION) ) {
			assertBehaviorIsExisting(cd, TO);
		}
		if ( kw.equals(SET) || kw.equals(LET) ) {
			assertAssignmentIsOk(cd);
		} // else if ( desc.getMeta().nameIsUnique() ) {
			// assertNameIsUniqueInSuperDescription(cd);
		// }
		else if ( desc.getMeta().isUnique() ) {
			assertKeywordIsUniqueInSuperDescription(cd);
		} else if ( kw.equals(CAPTURE) ) {
			assertMicroSpeciesIsVisible(cd, AS);
		} else if ( kw.equals(MIGRATE) ) {
			assertMicroSpeciesIsVisible(cd, TARGET);
		} else if ( kw.equals(CREATE) ) {
			String s = cd.getFacets().getLabel(SPECIES);
			SpeciesDescription species = cd.getSpeciesDescription(s);
			if ( species != null ) {
				if ( species.isAbstract() ) {
					cd.flagError("Species " + s + " is abstract and cannot be instantiated");
				} else if ( species.isMirror() ) {
					cd.flagError("Species " + s + " is a mirror and cannot be instantiated");
				}
			}
		}
	}

	/**
	 * @param cd
	 */

	@Override
	protected void privateValidateChildren(final IDescription desc) {
		if ( ((StatementDescription) desc).hasArgs() ) {
			validateArgs(desc);
		}
		IDescription previousEnclosingDescription = null;
		if ( desc.getMeta().isRemoteContext() ) {
			String actualSpecies = computeSpecies(desc);
			if ( actualSpecies != null ) {
				IType t = desc.getSpeciesContext().getType();
				desc.addTemp(MYSELF, t, t);
				previousEnclosingDescription = desc.getSuperDescription();
				desc.setSuperDescription(desc.getSpeciesDescription(actualSpecies));
			}
		}
		super.privateValidateChildren(desc);
		if ( previousEnclosingDescription != null ) {
			desc.setSuperDescription(previousEnclosingDescription);
		}
	}

	@Override
	protected List<ISymbol> privateCompileChildren(final IDescription desc) {
		if ( desc.getMeta().isRemoteContext() ) {
			String actualSpecies = computeSpecies(desc);
			if ( actualSpecies != null ) {
				IType t = desc.getSpeciesContext().getType();
				desc.addTemp(MYSELF, t, t);
				desc.setSuperDescription(desc.getSpeciesDescription(actualSpecies));
			}
		}
		return super.privateCompileChildren(desc);
	}

	/**
	 * @param desc
	 */
	private void validateArgs(final IDescription desc) {
		privateCompileArgs((StatementDescription) desc);
	}

	@Override
	protected Arguments privateCompileArgs(final StatementDescription cd) {
		Arguments ca = new Arguments();
		String keyword = cd.getKeyword();
		boolean isCreate =
			keyword.equals(CREATE) || keyword.equals(DO) || keyword.equals(PRIMITIVE);
		Facets argFacets;
		for ( IDescription sd : cd.getArgs() ) {
			argFacets = sd.getFacets();
			String name = sd.getName();
			IExpression e = null;
			IDescription superDesc = cd.getSuperDescription();
			try {
				IExpressionDescription ed = argFacets.get(VALUE);
				if ( ed != null ) {
					e = ed.compile(superDesc);
				} else {
					ed = argFacets.get(DEFAULT);
					if ( ed != null ) {
						e = ed.compile(superDesc);
					}
				}
			} catch (RuntimeException e1) {
				cd.flagError("Error in compiling argument " + name + ": " + e1.getMessage(), name);
				e1.printStackTrace();
				return ca;
			}
			ca.put(name, e);
			IType type = sd.getTypeNamed(argFacets.getLabel(TYPE));
			if ( type == Types.NO_TYPE && e != null ) {
				type = e.getType();
			}
			if ( !isCreate ) {
				// Special case for the calls (create, do, primitives) as the "arguments" passed
				// should not be part of the context
				cd.addTemp(name, type, e == null ? Types.NO_TYPE : e.getContentType());
			}

		}
		if ( cd.getKeyword().equals(IKeyword.DO) ) {
			cd.verifyArgs(cd.getFacets().getLabel(IKeyword.ACTION), ca);
		}
		return ca;
	}

	@Override
	protected void compileFacet(final String tag, final IDescription sd) {
		// GuiUtils.debug("Compiling facet " + tag);
		if ( sd.getMeta().isFacetDeclaringANewTemp(tag) ) {
			Facets ff = sd.getFacets();
			IType type = sd.getTypeNamed(ff.getLabel(TYPE));
			IType contentType = sd.getTypeNamed(ff.getLabel(AS));
			if ( contentType == Types.NO_TYPE ) {
				contentType = sd.getTypeNamed(ff.getLabel(SPECIES));
				if ( contentType == Types.NO_TYPE ) {
					contentType = sd.getTypeNamed(ff.getLabel(OF));
				}
			}

			if ( type == Types.NO_TYPE ) {
				if ( ff.containsKey(VALUE) ) {
					compileFacet(VALUE, sd);
					IExpression expr = ff.getExpr(VALUE);
					if ( expr != null ) {
						type = expr.getType();
					}
				} else if ( ff.containsKey(OVER) ) {
					compileFacet(OVER, sd);
					IExpression expr = ff.getExpr(OVER);
					if ( expr != null ) {
						type = expr.getContentType();
					}
				} else if ( ff.containsKey(FROM) && ff.containsKey(TO) ) {
					compileFacet(FROM, sd);
					IExpression expr = ff.getExpr(FROM);
					if ( expr != null ) {
						type = expr.getType();
					}
				}
			}
			if ( contentType == Types.NO_TYPE ) {
				if ( ff.containsKey(VALUE) ) {
					compileFacet(VALUE, sd);
					IExpression expr = ff.getExpr(VALUE);
					if ( expr != null ) {
						contentType = expr.getContentType();
					}
				}
				if ( contentType == Types.NO_TYPE ) {
					contentType = type.defaultContentType();
				}
			}

			if ( type == Types.NO_TYPE && contentType != Types.NO_TYPE ) {
				type = Types.get(IType.CONTAINER);
			}
			IVarExpression exp =
				((StatementDescription) sd).addNewTempIfNecessary(tag, type, contentType);
			ff.put(tag, exp);
		} else {
			super.compileFacet(tag, sd);
		}
	}

	private String computeSpecies(final IDescription ce) {
		// TODO is there a way to extract the species from a constant expression (like
		// species("ant")) ? cf. Issue 145
		IType type = null;
		Facets ff = ce.getFacets();
		IExpression speciesFacet = ff.getExpr(SPECIES, ff.getExpr(AS, ff.getExpr(TARGET)));
		if ( speciesFacet != null ) {
			IType t = speciesFacet.getContentType();
			if ( t.isSpeciesType() ) {
				type = t;
			}
		}
		return type == null ? null : type.getSpeciesName();
	}

}
