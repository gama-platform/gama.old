package msi.gama.lang.gaml.resource;

import static msi.gama.common.interfaces.IKeyword.*;
import java.util.*;
import msi.gama.common.interfaces.ISyntacticElement;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.utils.*;
import msi.gaml.descriptions.*;
import msi.gaml.factories.DescriptionFactory;
import org.eclipse.emf.common.util.*;
import org.eclipse.emf.ecore.EObject;

/**
 * 
 * The class GamlCompatibilityConverter. Performs a series of transformations between the EObject
 * based representation of GAML models and its representation based of ISyntactic contents in GAMA.
 * 
 * @author drogoul
 * @since 16 mars 2013
 * 
 */
public class GamlCompatibilityConverter {

	final GamlResource resource;

	public GamlCompatibilityConverter(GamlResource resource) {
		this.resource = resource;
	}

	private boolean doesNotDefineAttributes(String keyword) {
		SymbolProto p = DescriptionFactory.getProto(keyword);
		if ( p == null ) { return true; }
		int kind = p.getKind();
		return !GamlResource.STATEMENTS_WITH_ATTRIBUTES.contains(kind);
	}

	private final ISyntacticElement convStatement(final String upper, final Statement stm) {
		// We catch its keyword
		String keyword = EGaml.getKey.caseStatement(stm);
		if ( keyword == null ) {
			throw new NullPointerException(
				"Trying to convert a statement with a null keyword. Please debug to understand the cause.");
		} else {
			keyword = convertKeyword(keyword, upper);
		}
		final ISyntacticElement elt = new SyntacticStatement(keyword, stm);
		/**
		 * Some syntactic rewritings to remove ambiguities inherent to the grammar of GAML and
		 * translate the new compact syntax into the legacy facet-based one
		 */

		if ( stm instanceof S_Assignment ) {
			keyword = convertAssignment((S_Assignment) stm, keyword, elt, EGaml.getExprOf(stm));
		} else if ( stm instanceof S_Definition && !keyword.equals(ENVIRONMENT) &&
			!SymbolProto.nonTypeStatements.contains(keyword) ) {
			S_Definition def = (S_Definition) stm;
			// If we define a variable with this statement
			// COMPATIBILITY with environment not being declared anymore
			// Translation of "type<contents> ..." to "type of: contents..."
			TypeRef t = (TypeRef) def.getTkey();
			if ( t != null ) {
				TypeRef of = (TypeRef) t.getOf();
				if ( of != null ) {
					String type = EGaml.getKey.caseTypeRef(of);
					if ( type != null ) {
						addFacet(elt, OF, convExpr(type));
					}
				}
			}
			if ( doesNotDefineAttributes(upper) ) {
				// Translation of "type var ..." to "let var type: type ..." if we are not in a
				// top-level statement (i.e. not in the declaration of a species or an experiment)
				elt.setKeyword(LET);
				addFacet(elt, TYPE, convExpr(keyword));
				keyword = LET;
			} else {
				// Translation of "type1 ID1 (type2 ID2, type3 ID3) {...}" to
				// "action ID1 type: type1 { arg ID2 type: type2; arg ID3 type: type3; ...}"
				Block b = def.getBlock();
				if ( b != null && b.getFunction() == null ) {
					elt.setKeyword(ACTION);
					// TODO Constant String ?
					addFacet(elt, TYPE, convExpr(keyword));
					keyword = ACTION;
				}
				convertArgs(def.getArgs(), elt);
			}
		} else if ( stm instanceof S_Do ) {
			// Translation of "stm ID (ID1: V1, ID2:V2)" to "stm ID with:(ID1: V1, ID2:V2)"
			Expression e = stm.getExpr();
			addFacet(elt, ACTION, convExpr(EGaml.getKeyOf(e)));
			if ( e instanceof Function ) {
				Function f = (Function) stm.getExpr();
				Parameters p = f.getParameters();
				if ( p != null ) {
					addFacet(elt, WITH, convExpr(p));
				} else {
					ExpressionList list = f.getArgs();
					if ( list != null ) {
						addFacet(elt, WITH, convExpr(list));
					}
				}
			}
		} else if ( stm instanceof S_If ) {
			// If the statement is "if", we convert its potential "else" part and put it as a child
			// of the syntactic element (as GAML expects it)
			convElse((S_If) stm, elt);
		} else if ( stm instanceof S_Action ) {
			// Conversion of "action ID (type1 ID1 <- V1, type2 ID2)" to
			// "action ID {arg ID1 type: type1 default: V1; arg ID2 type: type2}"
			convertArgs(((S_Action) stm).getArgs(), elt);
		} else if ( stm instanceof S_Reflex ) {
			// We add the "when" facet to reflexes and inits if necessary
			S_Reflex ref = (S_Reflex) stm;
			if ( ref.getExpr() != null ) {
				addFacet(elt, WHEN, convExpr(ref.getExpr()));
			}
		}

		// We apply some conversions to the facets expressed in the statement
		convertFacets(stm, keyword, elt);

		if ( stm instanceof S_Var && (keyword.equals(CONST) || keyword.equals(VAR)) ) {
			// We modify the "var", "const" declarations in order to replace the
			// keyword by the type
			String type = elt.getLabel(TYPE);
			// GuiUtils.debug("Found as S_Var:" + elt.getKeyword() + " type: " + type);
			if ( type == null ) {
				resource.error("Facet 'type' is missing", elt, false);
				return elt;
			} else {
				elt.setKeyword(type);
			}
			if ( keyword.equals(CONST) ) {
				String constant = elt.getLabel(CONST);
				if ( constant != null && constant.equals(FALSE) ) {
					resource.error("Is this variable constant or not ?", elt, true);
				}
				elt.setFacet(CONST, ConstantExpressionDescription.create(true));
			}
		} else if ( stm instanceof S_Experiment ) {
			// We do it also for experiments, and change their name
			String type = elt.getLabel(TYPE);
			if ( type == null ) {
				resource.error("Facet 'type' is missing, set by default to 'gui'", elt, true);
				elt.setFacet(TYPE, ConstantExpressionDescription.create("gui"));
				elt.setKeyword("gui");
			} else {
				elt.setKeyword(type);
			}
			// We modify the names of experiments so as not to confuse them with species
			String name = elt.getLabel(NAME);
			elt.setFacet(NAME, convExpr("Experiment " + name));
		} else // TODO Change this by implementing only one class of methods (that delegates to
				// others)
		if ( keyword.equals(METHOD) ) {
			// We apply some conversion for methods (to get the name instead of the "method"
			// keyword)
			String type = elt.getLabel(NAME);
			if ( type != null ) {
				elt.setKeyword(type);
			}
		} else if ( stm instanceof S_Equations ) {
			convStatements(elt, ((S_Equations) stm).getEquations());
		}

		// We add the dependencies (only for variable declarations)
		assignDependencies(stm, keyword, elt);
		// We convert the block of statements (if any)
		convertBlock(stm, elt);

		return elt;
	}

	private void convertBlock(final Statement stm, final ISyntacticElement elt) {
		if ( EGaml.getBlockOf(stm) != null ) {
			Block block = stm.getBlock();
			if ( block != null ) {
				Expression function = EGaml.getBlockOf(stm).getFunction();
				if ( function != null ) {
					// If it is a function (and not a regular block), we add it as a facet
					addFacet(elt, FUNCTION, convExpr(function));
				} else {
					convStatements(elt, block.getStatements());
				}
			}
		}
	}

	private void addFacet(final ISyntacticElement e, final String key,
		final IExpressionDescription expr) {
		if ( e.getFacet(key) != null ) {
			resource.error("Double definition of facet " + key +
				". Only the last one will be considered", e, true);
		}
		e.setFacet(key, expr);
	}

	private void assignDependencies(final Statement stm, final String keyword,
		final ISyntacticElement elt) {
		// COMPATIBILITY with the definition of environment
		if ( !SymbolProto.nonTypeStatements.contains(keyword) ) {
			Set<String> s = varDependenciesOf(stm);
			if ( s != null && !s.isEmpty() ) {
				elt.setFacet(DEPENDS_ON, new StringListExpressionDescription(s));
			}
			if ( !(stm instanceof S_Var) ) {
				String type = elt.getLabel(TYPE);
				if ( type != null ) {
					if ( type.equals(keyword) ) {
						resource.error("Duplicate declaration of type", elt, true);
					} else {
						resource.error("Conflicting declaration of type (" + type + " and " +
							keyword + "), only the last one will be considered", elt, true);
					}
				}
			}
		}
	}

	public ISyntacticElement buildSyntacticContents() {
		Model m = (Model) resource.getContents().get(0);
		if ( m == null ) { throw new NullPointerException("The model of " + resource +
			" appears to be null. Please debug to understand the cause."); }
		ISyntacticElement syntacticContents = new SyntacticStatement(MODEL, m);
		syntacticContents.setFacet(NAME, convExpr(m.getName()));
		for ( final Import i : m.getImports() ) {
			final ISyntacticElement include = new SyntacticStatement(INCLUDE, i);
			include.setFacet(FILE, convExpr(i.getImportURI()));
			syntacticContents.addChild(include);
		}
		convStatements(syntacticContents, m.getStatements());
		return syntacticContents;
	}

	private final void convElse(final S_If stm, final ISyntacticElement elt) {
		EObject elseBlock = stm.getElse();
		if ( elseBlock != null ) {
			ISyntacticElement elseElt = new SyntacticStatement(ELSE, elseBlock);
			if ( elseBlock instanceof Statement ) {
				elseElt.addChild(convStatement(IF, (Statement) elseBlock));
			} else {
				convStatements(elseElt, ((Block) elseBlock).getStatements());
			}
			elt.addChild(elseElt);
		}
	}

	private void convertArgs(final ActionArguments args, final ISyntacticElement elt) {
		if ( args != null ) {
			for ( ArgumentDefinition def : args.getArgs() ) {
				ISyntacticElement arg = new SyntacticStatement(ARG, def);
				addFacet(arg, NAME, convExpr(def.getName()));
				TypeRef type = (TypeRef) def.getType();
				addFacet(arg, TYPE, convExpr(EGaml.getKey.caseTypeRef(type)));
				if ( type.getOf() != null ) {
					addFacet(arg, OF, convExpr(EGaml.getKey.caseTypeRef((TypeRef) type.getOf())));
				}
				if ( def.getDefault() != null ) {
					addFacet(arg, DEFAULT, convExpr(def.getDefault()));
				}
				elt.addChild(arg);
			}
		}
	}

	private String convertAssignment(final S_Assignment stm, String keyword,
		final ISyntacticElement elt, Expression expr) {
		S_Assignment stm1 = stm;
		IExpressionDescription value = convExpr(stm1.getValue());
		if ( keyword.equals("<-") || keyword.equals(SET) ) {
			// Translation of "container[index] <- value" to
			// "put item: value in: container at: index"
			if ( expr instanceof Access ) {
				elt.setKeyword(PUT);
				addFacet(elt, ITEM, value);
				addFacet(elt, IN, convExpr(expr.getLeft()));
				List<Expression> args = EGaml.getExprsOf(((Access) expr).getArgs());
				int size = args.size();
				if ( size == 1 ) { // Integer index
					addFacet(elt, AT, convExpr(args.get(0)));
				} else if ( size > 1 ) { // Point index
					IExpressionDescription p =
						new OperatorExpressionDescription("<->", convExpr(args.get(0)),
							convExpr(args.get(1)));
					addFacet(elt, AT, p);
				} else {// size = 0 ? maybe "all: true" by default
					addFacet(elt, ALL, ConstantExpressionDescription.create(true));
				}
				keyword = PUT;
			} else {
				// Translation of "var <- value" to "set var value: value"
				elt.setKeyword(SET);
				addFacet(elt, VALUE, value);
				keyword = SET;
			}
		} else if ( keyword.equals("<<") || keyword.equals("+=") || keyword.equals("++") ) {
			// Translation of "container << item" or "container ++ item" to
			// "add item: item to: container"
			elt.setKeyword(ADD);
			addFacet(elt, TO, convExpr(expr));
			addFacet(elt, ITEM, value);
			keyword = ADD;
		} else if ( keyword.equals("-=") || keyword.equals(">>") || keyword.equals("--") ) {
			// Translation of "container >> item" or "container -- item" to
			// "remove item: item from: container"
			elt.setKeyword(REMOVE);
			addFacet(elt, FROM, convExpr(expr));
			addFacet(elt, ITEM, value);
			keyword = REMOVE;
		} else if ( keyword.equals(EQUATION_OP) ) {
			// Translation of equations "diff(x, t) = ax + b" to
			// "= left: diff(x, t) right: ax+b"
			addFacet(elt, EQUATION_LEFT, convExpr(expr));
			addFacet(elt, EQUATION_RIGHT, value);
		}
		return keyword;
	}

	private void convertFacets(final Statement stm, final String keyword,
		final ISyntacticElement elt) {
		SymbolProto p = DescriptionFactory.getProto(keyword);
		for ( Facet f : EGaml.getFacetsOf(stm) ) {
			String fname = EGaml.getKey.caseFacet(f);
			// We change the "<-" and "->" symbols into full names
			if ( fname.equals("<-") ) {
				fname = keyword.equals(LET) || keyword.equals(SET) ? VALUE : INIT;
			} else if ( fname.equals("->") ) {
				fname = FUNCTION;
			} else if ( fname.equals(TYPE) ) {
				// We convert type: ss<tt> to type: ss of: tt
				if ( f.getExpr() instanceof TypeRef ) {
					TypeRef of = (TypeRef) ((TypeRef) f.getExpr()).getOf();
					if ( of != null ) {
						addFacet(elt, OF, convExpr(EGaml.getKey.caseTypeRef(of)));
					}
				}
			}
			// We compute (and convert) the expression attached to the facet
			FacetProto fp = p == null ? null : p.getPossibleFacets().get(fname);
			boolean label = fp == null ? false : fp.isLabel;
			IExpressionDescription fexpr = convExpr(f, label);
			addFacet(elt, fname, fexpr);
		}

		// We add the "default" (or omissible) facet to the syntactic element
		String def = DescriptionFactory.getOmissibleFacetForSymbol(keyword);
		if ( def != null && !def.isEmpty() && elt.getFacet(def) == null ) {
			IExpressionDescription ed = findExpr(stm);
			if ( ed != null ) {
				elt.setFacet(def, ed);
			}
		}
	}

	private String convertKeyword(final String k, final String upper) {
		String keyword = k;
		if ( upper.equals(BATCH) && keyword.equals(SAVE) ) {
			keyword = SAVE_BATCH;
		} else if ( upper.equals(OUTPUT) && keyword.equals(FILE) ) {
			keyword = OUTPUT_FILE;
		} else if ( upper.equals(DISPLAY) || upper.equals(POPULATION) ) {
			if ( keyword.equals(SPECIES) ) {
				keyword = POPULATION;
			} else if ( keyword.equals(GRID) ) {
				keyword = GRID_POPULATION;
			}
		}
		return keyword;
	}

	private final IExpressionDescription convExpr(final EObject expr) {
		if ( expr != null ) { return new EcoreBasedExpressionDescription(expr); }
		return null;
	}

	private final IExpressionDescription convExpr(final Facet facet, boolean label) {
		if ( facet != null ) {
			Expression expr = facet.getExpr();
			if ( expr != null ) { return label ? convExpr(EGaml.getKeyOf(expr)) : convExpr(expr); }
			String name = facet.getName();
			if ( name != null ) { return convExpr(name); }
		}
		return null;
	}

	final IExpressionDescription convExpr(final String string) {
		return LabelExpressionDescription.create(string);
		// return convExpr(EGaml.createTerminal(string));
	}

	final void convStatements(final ISyntacticElement elt, final EList<? extends Statement> ss) {
		for ( final Statement stm : ss ) {
			elt.addChild(convStatement(elt.getKeyword(), stm));
		}
	}

	private final IExpressionDescription findExpr(final Statement stm) {
		if ( stm == null ) { return null; }
		// The order below should be important
		String name = EGaml.getNameOf(stm);
		if ( name != null ) { return convExpr(name); }
		Expression expr = EGaml.getExprOf(stm);
		if ( expr != null ) { return convExpr(expr); }

		return null;
	}

	private final Set<String> varDependenciesOf(final Statement s) {
		Set<String> list = new HashSet();
		for ( Facet facet : EGaml.getFacetsOf(s) ) {
			Expression expr = facet.getExpr();
			if ( expr != null ) {
				if ( expr instanceof VariableRef ) {
					list.add(EGaml.getKey.caseVariableRef((VariableRef) expr));
				} else {
					for ( TreeIterator<EObject> tree = expr.eAllContents(); tree.hasNext(); ) {
						EObject obj = tree.next();
						if ( obj instanceof VariableRef ) {
							list.add(EGaml.getKey.caseVariableRef((VariableRef) obj));
						}
					}
				}
			}
		}
		if ( list.isEmpty() ) { return null; }
		return list;
	}

}
