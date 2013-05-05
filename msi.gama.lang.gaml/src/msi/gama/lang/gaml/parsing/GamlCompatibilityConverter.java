package msi.gama.lang.gaml.parsing;

import static msi.gama.common.interfaces.IKeyword.*;
import java.util.*;
import msi.gama.common.interfaces.ISyntacticElement;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.utils.*;
import msi.gama.precompiler.ISymbolKind;
import msi.gaml.compilation.SyntacticElement;
import msi.gaml.descriptions.*;
import msi.gaml.factories.DescriptionFactory;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.diagnostics.*;
import org.eclipse.xtext.validation.EObjectDiagnosticImpl;

/**
 * 
 * The class GamlCompatibilityConverter. Performs a series of transformations between the EObject
 * based representation of GAML models and the representation based on ISyntacticElements in GAMA.
 * 
 * @author drogoul
 * @since 16 mars 2013
 * 
 */
public class GamlCompatibilityConverter {

	static final List<Integer> STATEMENTS_WITH_ATTRIBUTES = Arrays.asList(ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT,
		ISymbolKind.OUTPUT, ISymbolKind.MODEL);

	public static ISyntacticElement buildSyntacticContents(EObject root, Set<Diagnostic> errors) {
		Model m = (Model) root;
		if ( m == null ) { throw new NullPointerException("The model of " + root.eResource() +
			" appears to be null. Please debug to understand the cause."); }
		ISyntacticElement syntacticContents = new SyntacticElement(MODEL, m);
		syntacticContents.setFacet(NAME, convExpr(m.getName()));
		convStatements(syntacticContents, EGaml.getStatementsOf(m), errors);
		return syntacticContents;
	}

	private static boolean doesNotDefineAttributes(String keyword) {
		SymbolProto p = DescriptionFactory.getProto(keyword);
		if ( p == null ) { return true; }
		int kind = p.getKind();
		return !STATEMENTS_WITH_ATTRIBUTES.contains(kind);
	}

	private static void addWarning(String message, EObject object, Set<Diagnostic> errors) {
		Diagnostic d = new EObjectDiagnosticImpl(Severity.WARNING, "", message, object, null, 0, null);
		errors.add(d);
	}

	private static final ISyntacticElement convStatement(final ISyntacticElement upper, final Statement stm,
		Set<Diagnostic> errors) {
		// We catch its keyword
		String keyword = EGaml.getKey.caseStatement(stm);
		if ( keyword == null ) {
			throw new NullPointerException(
				"Trying to convert a statement with a null keyword. Please debug to understand the cause.");
		} else if ( keyword.equals(ENTITIES) ) {
			convertBlock(stm, upper, errors);
			return null;
		} else {
			keyword = convertKeyword(keyword, upper.getKeyword());
		}
		final ISyntacticElement elt = new SyntacticElement(keyword, stm);
		/**
		 * Some syntactic rewritings to remove ambiguities inherent to the grammar of GAML and
		 * translate the new compact syntax into the legacy facet-based one
		 */
		if ( stm instanceof S_Species ) {
			elt.setCategory(ISyntacticElement.IS_SPECIES);
		} else if ( keyword.equals(GLOBAL) ) {
			elt.setCategory(ISyntacticElement.IS_GLOBAL);
		} else if ( stm instanceof S_Experiment ) {
			elt.setCategory(ISyntacticElement.IS_EXPERIMENT);
		} else if ( keyword.equals(ENVIRONMENT) ) {
			convertBlock(stm, upper, errors);
		} else if ( stm instanceof S_Assignment ) {
			keyword = convertAssignment((S_Assignment) stm, keyword, elt, stm.getExpr(), errors);
		} else if ( stm instanceof S_Definition && !SymbolProto.nonTypeStatements.contains(keyword) ) {
			S_Definition def = (S_Definition) stm;
			// If we define a variable with this statement
			TypeRef t = (TypeRef) def.getTkey();
			convertType(elt, t, errors);
			if ( t != null && doesNotDefineAttributes(upper.getKeyword()) ) {
				// Translation of "type var ..." to "let var type: type ..." if we are not in a
				// top-level statement (i.e. not in the declaration of a species or an experiment)
				elt.setKeyword(LET);
				addFacet(elt, TYPE, convExpr(keyword), errors);
				keyword = LET;
			} else {
				// Translation of "type1 ID1 (type2 ID2, type3 ID3) {...}" to
				// "action ID1 type: type1 { arg ID2 type: type2; arg ID3 type: type3; ...}"
				Block b = def.getBlock();
				if ( b != null && b.getFunction() == null ) {
					elt.setKeyword(ACTION);
					addFacet(elt, TYPE, convExpr(keyword), errors);
					keyword = ACTION;
				}
				convertArgs(def.getArgs(), elt, errors);
			}
		} else if ( stm instanceof S_Do ) {
			// Translation of "stm ID (ID1: V1, ID2:V2)" to "stm ID with:(ID1: V1, ID2:V2)"
			Expression e = stm.getExpr();
			addFacet(elt, ACTION, convExpr(EGaml.getKeyOf(e)), errors);
			if ( e instanceof Function ) {
				addFacet(elt, INTERNAL_FUNCTION, convExpr(e, errors), errors);
				Function f = (Function) e;
				Parameters p = f.getParameters();
				if ( p != null ) {
					addFacet(elt, WITH, convExpr(p, errors), errors);
				} else {
					ExpressionList list = f.getArgs();
					if ( list != null ) {
						addFacet(elt, WITH, convExpr(list, errors), errors);
					}
				}
			}
		} else if ( stm instanceof S_If ) {
			// If the statement is "if", we convert its potential "else" part and put it as a child
			// of the syntactic element (as GAML expects it)
			convElse((S_If) stm, elt, errors);
		} else if ( stm instanceof S_Action ) {
			// Conversion of "action ID (type1 ID1 <- V1, type2 ID2)" to
			// "action ID {arg ID1 type: type1 default: V1; arg ID2 type: type2}"
			convertArgs(((S_Action) stm).getArgs(), elt, errors);
		} else if ( stm instanceof S_Reflex ) {
			// We add the "when" facet to reflexes and inits if necessary
			S_Reflex ref = (S_Reflex) stm;
			if ( ref.getExpr() != null ) {
				addFacet(elt, WHEN, convExpr(ref.getExpr(), errors), errors);
			}
		}

		// We apply some conversions to the facets expressed in the statement
		convertFacets(stm, keyword, elt, errors);

		if ( stm instanceof S_Var && (keyword.equals(CONST) || keyword.equals(VAR)) ) {
			// We modify the "var", "const" declarations in order to replace the
			// keyword by the type
			String type = elt.getLabel(TYPE);
			if ( type == null ) {
				addWarning("Facet 'type' is missing, set by default to 'unknown'", stm, errors);
				elt.setKeyword(UNKNOWN);
			} else {
				elt.setKeyword(type);
			}
			if ( keyword.equals(CONST) ) {
				String constant = elt.getLabel(CONST);
				if ( constant != null && constant.equals(FALSE) ) {
					addWarning("Is this variable constant or not ?", stm, errors);
				}
				elt.setFacet(CONST, ConstantExpressionDescription.create(true));
			}
		} else if ( stm instanceof S_Experiment ) {
			// We do it also for experiments, and change their name
			String type = elt.getLabel(TYPE);
			if ( type == null ) {
				addWarning("Facet 'type' is missing, set by default to 'gui'", stm, errors);
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
			convStatements(elt, EGaml.getEquationsOf((S_Equations) stm), errors);
		}

		// We add the dependencies (only for variable declarations)
		assignDependencies(stm, keyword, elt, errors);
		// We convert the block of statements (if any)
		convertBlock(stm, elt, errors);

		return elt;
	}

	private static void convertType(final ISyntacticElement elt, TypeRef t, Set<Diagnostic> errors) {
		if ( t != null ) {
			TypeRef first = (TypeRef) t.getFirst();
			if ( first == null ) { return; }
			TypeRef second = (TypeRef) t.getSecond();
			// Translation of "type<contents> ..." to "type of: contents..."
			if ( second == null ) {
				String type = EGaml.getKey.caseTypeRef(first);
				if ( type != null ) {
					addFacet(elt, OF, convExpr(type), errors);
				}
			} else {
				String type = EGaml.getKey.caseTypeRef(second);
				if ( type != null ) {
					addFacet(elt, OF, convExpr(type), errors);
					// Translation of "type<i, c> ..." to "type of: c index: i..."
					type = EGaml.getKey.caseTypeRef(first);
					if ( type != null ) {
						addFacet(elt, INDEX, convExpr(type), errors);
					}
				}
			}
		}
	}

	private static void convertBlock(final Statement stm, final ISyntacticElement elt, Set<Diagnostic> errors) {
		Block block = stm.getBlock();
		if ( block != null ) {
			Expression function = block.getFunction();
			if ( function != null ) {
				// If it is a function (and not a regular block), we add it as a facet
				addFacet(elt, FUNCTION, convExpr(function, errors), errors);
			} else {
				convStatements(elt, EGaml.getStatementsOf(block), errors);
			}
		}
	}

	private static void addFacet(final ISyntacticElement e, final String key, final IExpressionDescription expr,
		Set<Diagnostic> errors) {
		if ( e.getFacet(key) != null ) {
			addWarning("Double definition of facet " + key + ". Only the last one will be considered", e.getElement(),
				errors);
		}
		e.setFacet(key, expr);
	}

	private static void assignDependencies(final Statement stm, final String keyword, final ISyntacticElement elt,
		Set<Diagnostic> errors) {
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
						addWarning("Duplicate declaration of type", stm, errors);
					} else {
						addWarning("Conflicting declaration of type (" + type + " and " + keyword +
							"), only the last one will be considered", stm, errors);
					}
				}
			}
		}
	}

	private static void convElse(final S_If stm, final ISyntacticElement elt, Set<Diagnostic> errors) {
		EObject elseBlock = stm.getElse();
		if ( elseBlock != null ) {
			ISyntacticElement elseElt = new SyntacticElement(ELSE, elseBlock);
			if ( elseBlock instanceof Statement ) {
				elseElt.addChild(convStatement(elt, (Statement) elseBlock, errors));
			} else {
				convStatements(elseElt, EGaml.getStatementsOf((Block) elseBlock), errors);
			}
			elt.addChild(elseElt);
		}
	}

	private static void convertArgs(final ActionArguments args, final ISyntacticElement elt, Set<Diagnostic> errors) {
		if ( args != null ) {
			for ( ArgumentDefinition def : EGaml.getArgsOf(args) ) {
				ISyntacticElement arg = new SyntacticElement(ARG, def);
				addFacet(arg, NAME, convExpr(def.getName()), errors);
				TypeRef type = (TypeRef) def.getType();
				addFacet(arg, TYPE, convExpr(EGaml.getKey.caseTypeRef(type)), errors);
				convertType(arg, type, errors);
				Expression e = def.getDefault();
				if ( e != null ) {
					addFacet(arg, DEFAULT, convExpr(e, errors), errors);
				}
				elt.addChild(arg);
			}
		}
	}

	private static String convertAssignment(final S_Assignment stm, String keyword, final ISyntacticElement elt,
		Expression expr, Set<Diagnostic> errors) {
		IExpressionDescription value = convExpr(stm.getValue(), errors);
		if ( keyword.equals("<-") || keyword.equals(SET) ) {
			// Translation of "container[index] <- value" to
			// "put item: value in: container at: index"
			if ( expr instanceof Access ) {
				elt.setKeyword(PUT);
				addFacet(elt, ITEM, value, errors);
				addFacet(elt, IN, convExpr(expr.getLeft(), errors), errors);
				List<Expression> args = EGaml.getExprsOf(((Access) expr).getArgs());
				int size = args.size();
				if ( size == 1 ) { // Integer index
					addFacet(elt, AT, convExpr(args.get(0), errors), errors);
				} else if ( size > 1 ) { // Point index
					IExpressionDescription p =
						new OperatorExpressionDescription("<->", convExpr(args.get(0), errors), convExpr(args.get(1),
							errors));
					addFacet(elt, AT, p, errors);
				} else {// size = 0 ? maybe "all: true" by default
					addFacet(elt, ALL, ConstantExpressionDescription.create(true), errors);
				}
				keyword = PUT;
			} else {
				// Translation of "var <- value" to "set var value: value"
				elt.setKeyword(SET);
				addFacet(elt, VALUE, value, errors);
				keyword = SET;
			}
		} else if ( keyword.equals("<<") || keyword.equals("+=") || keyword.equals("++") ) {
			// Translation of "container << item" or "container ++ item" to
			// "add item: item to: container"
			elt.setKeyword(ADD);
			addFacet(elt, TO, convExpr(expr, errors), errors);
			addFacet(elt, ITEM, value, errors);
			keyword = ADD;
		} else if ( keyword.equals("-=") || keyword.equals(">>") || keyword.equals("--") ) {
			// Translation of "container >> item" or "container -- item" to
			// "remove item: item from: container"
			elt.setKeyword(REMOVE);
			addFacet(elt, FROM, convExpr(expr, errors), errors);
			addFacet(elt, ITEM, value, errors);
			keyword = REMOVE;
		} else if ( keyword.equals(EQUATION_OP) ) {
			// Translation of equations "diff(x, t) = ax + b" to
			// "= left: diff(x, t) right: ax+b"
			addFacet(elt, EQUATION_LEFT, convExpr(expr, errors), errors);
			addFacet(elt, EQUATION_RIGHT, value, errors);
		}
		return keyword;
	}

	private static void convertFacets(final Statement stm, final String keyword, final ISyntacticElement elt,
		Set<Diagnostic> errors) {
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
					convertType(elt, (TypeRef) f.getExpr(), errors);
				}
			}
			// We compute (and convert) the expression attached to the facet
			FacetProto fp = p == null ? null : p.getPossibleFacets().get(fname);
			boolean label = fp == null ? false : fp.isLabel;
			IExpressionDescription fexpr = convExpr(f, label, errors);
			addFacet(elt, fname, fexpr, errors);
		}

		// We add the "default" (or omissible) facet to the syntactic element
		String def = stm.getFirstFacet();
		if ( def != null ) {
			if ( def.endsWith(":") ) {
				def = def.substring(0, def.length() - 1);
			}
		} else {
			def = DescriptionFactory.getOmissibleFacetForSymbol(keyword);
		}
		if ( def != null && !def.isEmpty() && elt.getFacet(def) == null ) {
			IExpressionDescription ed = findExpr(stm, errors);
			if ( ed != null ) {
				elt.setFacet(def, ed);
			}
		}
	}

	private static String convertKeyword(final String k, final String upper) {
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

	private static final IExpressionDescription convExpr(final EObject expr, Set<Diagnostic> errors) {
		if ( expr != null ) {

		return EcoreBasedExpressionDescription.create(expr, errors); }
		return null;
	}

	private static final IExpressionDescription convExpr(final Facet facet, boolean label, Set<Diagnostic> errors) {
		if ( facet != null ) {
			Expression expr = facet.getExpr();
			if ( expr != null ) { return label ? convExpr(EGaml.getKeyOf(expr)) : convExpr(expr, errors); }
			String name = facet.getName();
			if ( name != null ) { return convExpr(name); }
		}
		return null;
	}

	final static IExpressionDescription convExpr(final String string) {
		return LabelExpressionDescription.create(string);
	}

	final static void convStatements(final ISyntacticElement elt, final List<? extends Statement> ss,
		Set<Diagnostic> errors) {
		for ( final Statement stm : ss ) {
			ISyntacticElement child = convStatement(elt, stm, errors);
			if ( child != null ) {
				elt.addChild(child);
			}
		}
	}

	private static final IExpressionDescription findExpr(final Statement stm, Set<Diagnostic> errors) {
		if ( stm == null ) { return null; }
		// The order below should be important
		String name = EGaml.getNameOf(stm);
		if ( name != null ) { return convExpr(name); }
		Expression expr = stm.getExpr();
		if ( expr != null ) { return convExpr(expr, errors); }

		return null;
	}

	private static final Set<String> varDependenciesOf(final Statement s) {
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
