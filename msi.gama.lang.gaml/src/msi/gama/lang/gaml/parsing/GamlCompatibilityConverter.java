/*********************************************************************************************
 *
 *
 * 'GamlCompatibilityConverter.java', in plugin 'msi.gama.lang.gaml', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.parsing;

import static msi.gama.common.interfaces.IKeyword.ACTION;
import static msi.gama.common.interfaces.IKeyword.ADD;
import static msi.gama.common.interfaces.IKeyword.ALL;
import static msi.gama.common.interfaces.IKeyword.ARG;
import static msi.gama.common.interfaces.IKeyword.AT;
import static msi.gama.common.interfaces.IKeyword.BATCH;
import static msi.gama.common.interfaces.IKeyword.DEFAULT;
import static msi.gama.common.interfaces.IKeyword.DEPENDS_ON;
import static msi.gama.common.interfaces.IKeyword.DISPLAY;
import static msi.gama.common.interfaces.IKeyword.ELSE;
import static msi.gama.common.interfaces.IKeyword.EQUATION;
import static msi.gama.common.interfaces.IKeyword.EQUATION_LEFT;
import static msi.gama.common.interfaces.IKeyword.EQUATION_OP;
import static msi.gama.common.interfaces.IKeyword.EQUATION_RIGHT;
import static msi.gama.common.interfaces.IKeyword.EXPERIMENT;
import static msi.gama.common.interfaces.IKeyword.FILE;
import static msi.gama.common.interfaces.IKeyword.FROM;
import static msi.gama.common.interfaces.IKeyword.FUNCTION;
import static msi.gama.common.interfaces.IKeyword.GRID;
import static msi.gama.common.interfaces.IKeyword.GRID_POPULATION;
import static msi.gama.common.interfaces.IKeyword.GUI_;
import static msi.gama.common.interfaces.IKeyword.IN;
import static msi.gama.common.interfaces.IKeyword.INDEX;
import static msi.gama.common.interfaces.IKeyword.INIT;
import static msi.gama.common.interfaces.IKeyword.INTERNAL_FUNCTION;
import static msi.gama.common.interfaces.IKeyword.ITEM;
import static msi.gama.common.interfaces.IKeyword.LET;
import static msi.gama.common.interfaces.IKeyword.METHOD;
import static msi.gama.common.interfaces.IKeyword.MODEL;
import static msi.gama.common.interfaces.IKeyword.NAME;
import static msi.gama.common.interfaces.IKeyword.OUTPUT;
import static msi.gama.common.interfaces.IKeyword.OUTPUT_FILE;
import static msi.gama.common.interfaces.IKeyword.POINT;
import static msi.gama.common.interfaces.IKeyword.POPULATION;
import static msi.gama.common.interfaces.IKeyword.PUT;
import static msi.gama.common.interfaces.IKeyword.REMOVE;
import static msi.gama.common.interfaces.IKeyword.SAVE;
import static msi.gama.common.interfaces.IKeyword.SAVE_BATCH;
import static msi.gama.common.interfaces.IKeyword.SET;
import static msi.gama.common.interfaces.IKeyword.SPECIES;
import static msi.gama.common.interfaces.IKeyword.TITLE;
import static msi.gama.common.interfaces.IKeyword.TO;
import static msi.gama.common.interfaces.IKeyword.TYPE;
import static msi.gama.common.interfaces.IKeyword.VALUE;
import static msi.gama.common.interfaces.IKeyword.WHEN;
import static msi.gama.common.interfaces.IKeyword.WITH;
import static msi.gama.common.interfaces.IKeyword.ZERO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.diagnostics.Diagnostic;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.validation.EObjectDiagnosticImpl;

import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.lang.gaml.gaml.Access;
import msi.gama.lang.gaml.gaml.ActionArguments;
import msi.gama.lang.gaml.gaml.ArgumentDefinition;
import msi.gama.lang.gaml.gaml.Block;
import msi.gama.lang.gaml.gaml.Expression;
import msi.gama.lang.gaml.gaml.ExpressionList;
import msi.gama.lang.gaml.gaml.Facet;
import msi.gama.lang.gaml.gaml.Function;
import msi.gama.lang.gaml.gaml.GamlPackage;
import msi.gama.lang.gaml.gaml.Import;
import msi.gama.lang.gaml.gaml.Model;
import msi.gama.lang.gaml.gaml.Parameters;
import msi.gama.lang.gaml.gaml.Pragma;
import msi.gama.lang.gaml.gaml.S_Action;
import msi.gama.lang.gaml.gaml.S_Assignment;
import msi.gama.lang.gaml.gaml.S_Definition;
import msi.gama.lang.gaml.gaml.S_Do;
import msi.gama.lang.gaml.gaml.S_Equations;
import msi.gama.lang.gaml.gaml.S_Experiment;
import msi.gama.lang.gaml.gaml.S_If;
import msi.gama.lang.gaml.gaml.S_Reflex;
import msi.gama.lang.gaml.gaml.S_Solve;
import msi.gama.lang.gaml.gaml.Statement;
import msi.gama.lang.gaml.gaml.TypeRef;
import msi.gama.lang.gaml.gaml.VariableRef;
import msi.gama.lang.gaml.gaml.impl.ModelImpl;
import msi.gama.lang.utils.EGaml;
import msi.gama.lang.utils.EcoreBasedExpressionDescription;
import msi.gama.precompiler.ISymbolKind;
import msi.gaml.compilation.ISyntacticElement;
import msi.gaml.compilation.SyntacticFactory;
import msi.gaml.compilation.SyntacticModelElement;
import msi.gaml.descriptions.ConstantExpressionDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.LabelExpressionDescription;
import msi.gaml.descriptions.OperatorExpressionDescription;
import msi.gaml.descriptions.StringListExpressionDescription;
import msi.gaml.descriptions.SymbolProto;
import msi.gaml.factories.DescriptionFactory;

/**
 *
 * The class GamlCompatibilityConverter. Performs a series of transformations
 * between the EObject based representation of GAML models and the
 * representation based on SyntacticElements in GAMA.
 *
 * @author drogoul
 * @since 16 mars 2013
 *
 */
public class GamlCompatibilityConverter {

	static final List<Integer> STATEMENTS_WITH_ATTRIBUTES = Arrays.asList(ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT,
			ISymbolKind.OUTPUT, ISymbolKind.MODEL);

	// private static final Set<String> EXTS =
	// GamaFileType.extensionsToFullType.keySet();

	public static SyntacticModelElement buildSyntacticContents(final EObject root, final Set<Diagnostic> errors) {
		if (root instanceof Block) {
			final SyntacticModelElement elt = (SyntacticModelElement) SyntacticFactory.create("model", root, true);
			convertBlock(elt, (Block) root, errors);
			return elt;
		}
		if (!(root instanceof Model)) {
			return null;
		}
		final ModelImpl m = (ModelImpl) root;
		final List<String> prgm = collectPragmas(m);
		final Object[] imps = collectImports(m);

		final SyntacticModelElement model = (SyntacticModelElement) SyntacticFactory.create(MODEL, m,
				EGaml.hasChildren(m), imps);
		model.setFacet(IKeyword.PRAGMA, ConstantExpressionDescription.create(prgm));
		model.setFacet(NAME, convertToLabel(null, m.getName()));
		convStatements(model, EGaml.getStatementsOf(m), errors);
		return model;
	}

	private static Object[] collectImports(final ModelImpl m) {
		Object[] imps = null;
		if (m.eIsSet(GamlPackage.MODEL__IMPORTS)) {
			final List<Import> imports = m.getImports();
			imps = new Object[imports.size()];
			for (int i = 0; i < imps.length; i++) {
				final URI uri = URI.createURI(imports.get(i).getImportURI(), false);
				imps[i] = uri;
			}
		}
		return imps;
	}

	private static List<String> collectPragmas(final ModelImpl m) {
		if (!m.eIsSet(GamlPackage.MODEL__PRAGMAS)) {
			return null;
		}
		final List<Pragma> pragmas = m.getPragmas();
		final List<String> result = new ArrayList();
		if (pragmas.isEmpty())
			return null;
		for (int i = 0; i < pragmas.size(); i++) {
			final String pragma = pragmas.get(i).getName();
			result.add(pragma);
		}
		return result;
	}

	private static boolean doesNotDefineAttributes(final String keyword) {
		final SymbolProto p = DescriptionFactory.getProto(keyword, null);
		if (p == null) {
			return true;
		}
		final int kind = p.getKind();
		return !STATEMENTS_WITH_ATTRIBUTES.contains(kind);
	}

	private static void addWarning(final String message, final EObject object, final Set<Diagnostic> errors) {
		if (!GamaPreferences.WARNINGS_ENABLED.getValue()) {
			return;
		}
		final Diagnostic d = new EObjectDiagnosticImpl(Severity.WARNING, "", message, object, null, 0, null);
		errors.add(d);
	}

	private static void addInfo(final String message, final EObject object, final Set<Diagnostic> errors) {
		if (!GamaPreferences.INFO_ENABLED.getValue()) {
			return;
		}
		final Diagnostic d = new EObjectDiagnosticImpl(Severity.INFO, "", message, object, null, 0, null);
		errors.add(d);
	}

	private static final ISyntacticElement convStatement(final ISyntacticElement upper, final Statement stm,
			final Set<Diagnostic> errors) {
		// We catch its keyword
		String keyword = EGaml.getKeyOf(stm);
		if (keyword == null) {
			throw new NullPointerException(
					"Trying to convert a statement with a null keyword. Please debug to understand the cause.");
		}

		// else if (keyword.equals(ENTITIES)) {
		// convertBlock(stm, upper, errors);
		// return null;
		// }

		else {
			keyword = convertKeyword(keyword, upper.getKeyword());
		}
		final ISyntacticElement elt = SyntacticFactory.create(keyword, stm, EGaml.hasChildren(stm));

		// if (keyword.equals(ENVIRONMENT)) {
		// convertBlock(stm, upper, errors);
		// } else

		if (stm instanceof S_Assignment) {
			keyword = convertAssignment((S_Assignment) stm, keyword, elt, stm.getExpr(), errors);
			// } else if ( stm instanceof S_Definition &&
			// !SymbolProto.nonTypeStatements.contains(keyword) ) {
		} else if (stm instanceof S_Definition && !DescriptionFactory.isStatementProto(keyword)) {
			final S_Definition def = (S_Definition) stm;
			// If we define a variable with this statement
			final TypeRef t = (TypeRef) def.getTkey();

			// 20/01/14: The type is now passed plainly
			if (t != null) {
				addFacet(elt, TYPE, convExpr(t, errors), errors);
			}
			// convertType(elt, t, errors);
			if (t != null && doesNotDefineAttributes(upper.getKeyword())) {
				// Translation of "type var ..." to "let var type: type ..." if
				// we are not in a
				// top-level statement (i.e. not in the declaration of a species
				// or an experiment)
				elt.setKeyword(LET);
				// addFacet(elt, TYPE, convertToConstantString(null, keyword),
				// errors);
				keyword = LET;
			} else {
				// Translation of "type1 ID1 (type2 ID2, type3 ID3) {...}" to
				// "action ID1 type: type1 { arg ID2 type: type2; arg ID3 type:
				// type3; ...}"
				final Block b = def.getBlock();
				if (b != null && b.getFunction() == null) {
					elt.setKeyword(ACTION);
					// addFacet(elt, TYPE, convertToConstantString(null,
					// keyword), errors);
					keyword = ACTION;
				}
				convertArgs(def.getArgs(), elt, errors);
			}
		} else if (stm instanceof S_Do) {
			// Translation of "stm ID (ID1: V1, ID2:V2)" to "stm ID with:(ID1:
			// V1, ID2:V2)"
			final Expression e = stm.getExpr();
			addFacet(elt, ACTION, convertToLabel(e, EGaml.getKeyOf(e)), errors);
			if (e instanceof Function) {
				addFacet(elt, INTERNAL_FUNCTION, convExpr(e, errors), errors);
				final Function f = (Function) e;
				final Parameters p = f.getParameters();
				if (p != null) {
					addFacet(elt, WITH, convExpr(p, errors), errors);
				} else {
					final ExpressionList list = f.getArgs();
					if (list != null) {
						addFacet(elt, WITH, convExpr(list, errors), errors);
					}
				}
			}
		} else if (stm instanceof S_If) {
			// If the statement is "if", we convert its potential "else" part
			// and put it as a child
			// of the syntactic element (as GAML expects it)
			convElse((S_If) stm, elt, errors);
		} else if (stm instanceof S_Action) {
			// Conversion of "action ID (type1 ID1 <- V1, type2 ID2)" to
			// "action ID {arg ID1 type: type1 default: V1; arg ID2 type:
			// type2}"
			convertArgs(((S_Action) stm).getArgs(), elt, errors);
		} else if (stm instanceof S_Reflex) {
			// We add the "when" facet to reflexes and inits if necessary
			final S_Reflex ref = (S_Reflex) stm;
			if (ref.getExpr() != null) {
				addFacet(elt, WHEN, convExpr(ref.getExpr(), errors), errors);
			}
		} else if (stm instanceof S_Solve) {
			final Expression e = stm.getExpr();
			addFacet(elt, EQUATION, convertToLabel(e, EGaml.getKeyOf(e)), errors);
		}

		// We apply some conversions to the facets expressed in the statement
		convertFacets(stm, keyword, elt, errors);

		// if (stm instanceof S_Var && (keyword.equals(CONST) ||
		// keyword.equals(VAR))) {
		// // We modify the "var", "const" declarations in order to replace the
		// // keyword by the type
		// final IExpressionDescription type = elt.getExpressionAt(TYPE);
		// if (type == null) {
		// addWarning("Facet 'type' is missing, set by default to 'unknown'",
		// stm, errors);
		// elt.setKeyword(UNKNOWN);
		// } else {
		//
		// // WARNING FALSE (type is now more TypeRef)
		// elt.setKeyword(type.toString());
		// }
		// if (keyword.equals(CONST)) {
		// final IExpressionDescription constant = elt.getExpressionAt(CONST);
		// if (constant != null && constant.toString().equals(FALSE)) {
		// addWarning("Is this variable constant or not ?", stm, errors);
		// }
		// elt.setFacet(CONST, ConstantExpressionDescription.create(true));
		// }
		// } else
		if (stm instanceof S_Experiment) {
			// We do it also for experiments, and change their name
			final IExpressionDescription type = elt.getExpressionAt(TYPE);
			if (type == null) {
				addInfo("Facet 'type' is missing, set by default to 'gui'", stm, errors);
				elt.setFacet(TYPE, ConstantExpressionDescription.create(GUI_));
			}
			// if ( type == null ) {
			// addWarning("Facet 'type' is missing, set by default to 'gui'",
			// stm, errors);
			// elt.setFacet(TYPE, ConstantExpressionDescription.create(GUI_));
			// elt.setKeyword(GUI_);
			// } else {
			// elt.setKeyword(type);
			// }
			// We modify the names of experiments so as not to confuse them with
			// species
			final String name = elt.getName();
			elt.setFacet(TITLE, convertToLabel(null, "Experiment " + name));
			elt.setFacet(NAME, convertToLabel(null, name));
		} else // TODO Change this by implementing only one class of methods
				// (that delegates to
				// others)
		if (keyword.equals(METHOD)) {
			// We apply some conversion for methods (to get the name instead of
			// the "method"
			// keyword)
			final String type = elt.getName();
			if (type != null) {
				elt.setKeyword(type);
			}
		} else if (stm instanceof S_Equations) {
			convStatements(elt, EGaml.getEquationsOf((S_Equations) stm), errors);
		}
		// We add the dependencies (only for variable declarations)
		assignDependencies(stm, keyword, elt, errors);
		// We convert the block of statements (if any)
		convertBlock(stm, elt, errors);

		return elt;
	}

	private static void convertBlock(final Statement stm, final ISyntacticElement elt, final Set<Diagnostic> errors) {
		final Block block = stm.getBlock();
		convertBlock(elt, block, errors);
	}

	public static void convertBlock(final ISyntacticElement elt, final Block block, final Set<Diagnostic> errors) {
		if (block != null) {
			final Expression function = block.getFunction();
			if (function != null) {
				// If it is a function (and not a regular block), we add it as a
				// facet
				addFacet(elt, FUNCTION, convExpr(function, errors), errors);
			} else {
				convStatements(elt, EGaml.getStatementsOf(block), errors);
			}
		}
	}

	private static void addFacet(final ISyntacticElement e, final String key, final IExpressionDescription expr,
			final Set<Diagnostic> errors) {
		if (e.hasFacet(key)) {
			// if ( key.equals(TYPE) ) {
			// scope.getGui().debug("GamlCompatibilityConverter.addFacet:");
			// }
			addWarning("Double definition of facet " + key + ". Only the last one will be considered", e.getElement(),
					errors);
		}
		e.setFacet(key, expr);
	}

	private static void assignDependencies(final Statement stm, final String keyword, final ISyntacticElement elt,
			final Set<Diagnostic> errors) {

		if (!DescriptionFactory.isStatementProto(keyword)) {
			final Set<String> s = varDependenciesOf(stm);
			if (s != null && !s.isEmpty()) {
				elt.setFacet(DEPENDS_ON, new StringListExpressionDescription(s));
			}

		}
	}

	private static void convElse(final S_If stm, final ISyntacticElement elt, final Set<Diagnostic> errors) {
		final EObject elseBlock = stm.getElse();
		if (elseBlock != null) {
			final ISyntacticElement elseElt = SyntacticFactory.create(ELSE, elseBlock, EGaml.hasChildren(elseBlock));
			if (elseBlock instanceof Statement) {
				elseElt.addChild(convStatement(elt, (Statement) elseBlock, errors));
			} else {
				convStatements(elseElt, EGaml.getStatementsOf((Block) elseBlock), errors);
			}
			elt.addChild(elseElt);
		}
	}

	private static void convertArgs(final ActionArguments args, final ISyntacticElement elt,
			final Set<Diagnostic> errors) {
		if (args != null) {
			for (final ArgumentDefinition def : EGaml.getArgsOf(args)) {
				final ISyntacticElement arg = SyntacticFactory.create(ARG, def, false);
				addFacet(arg, NAME, convertToLabel(null, def.getName()), errors);
				final EObject type = def.getType();
				addFacet(arg, TYPE, convExpr(type, errors), errors);
				final Expression e = def.getDefault();
				if (e != null) {
					addFacet(arg, DEFAULT, convExpr(e, errors), errors);
				}
				elt.addChild(arg);
			}
		}
	}

	private static String convertAssignment(final S_Assignment stm, final String originalKeyword,
			final ISyntacticElement elt, final Expression expr, final Set<Diagnostic> errors) {
		final IExpressionDescription value = convExpr(stm.getValue(), errors);
		String keyword = originalKeyword;
		if (keyword.endsWith("<-") || keyword.equals(SET)) {
			// Translation of "container[index] <- value" to
			// "put item: value in: container at: index"
			// 20/1/14: Translation of container[index] +<- value" to
			// "add item: value in: container at: index"
			if (expr instanceof Access && expr.getOp().equals("[")) {
				final String kw = keyword.equals("+<-") ? ADD : PUT;
				final String to = keyword.equals("+<-") ? TO : IN;
				elt.setKeyword(kw);
				addFacet(elt, ITEM, value, errors);
				addFacet(elt, to, convExpr(expr.getLeft(), errors), errors);
				final List<Expression> args = EGaml.getExprsOf(((Access) expr).getArgs());
				if (args.size() == 0) {
					// Add facet all: true when no index is provided
					addFacet(elt, ALL, ConstantExpressionDescription.create(true), errors);
				} else {
					if (args.size() == 1) { // Integer index
						addFacet(elt, AT, convExpr(args.get(0), errors), errors);
					} else { // Point index
						final IExpressionDescription p = new OperatorExpressionDescription(POINT,
								convExpr(args.get(0), errors), convExpr(args.get(1), errors));
						addFacet(elt, AT, p, errors);
					}
				}
				keyword = kw;
			} else {
				// Translation of "var <- value" to "set var value: value"
				elt.setKeyword(SET);
				addFacet(elt, VALUE, value, errors);
				keyword = SET;
			}
		} else if (keyword.startsWith("<<") || keyword.equals("<+")) {
			// Translation of "container <+ item" or "container << item" to "add
			// item: item to: container"
			// 08/01/14: Addition of the "<<+" (add all)
			elt.setKeyword(ADD);
			addFacet(elt, TO, convExpr(expr, errors), errors);
			addFacet(elt, ITEM, value, errors);
			if (keyword.equals("<<+")) {
				addFacet(elt, ALL, ConstantExpressionDescription.create(true), errors);
			}
			keyword = ADD;
		} else if (keyword.startsWith(">>") || keyword.equals(">-")) {
			// Translation of "container >> item" or "container >- item" to
			// "remove item: item from: container"
			// 08/01/14: Addition of the ">>-" keyword (remove all)
			elt.setKeyword(REMOVE);
			// 20/01/14: Addition of the access [] to remove from the index
			if (expr instanceof Access && expr.getOp().equals("[")
					&& EGaml.getExprsOf(((Access) expr).getArgs()).size() == 0) {
				addFacet(elt, FROM, convExpr(expr.getLeft(), errors), errors);
				addFacet(elt, INDEX, value, errors);
			} else {
				addFacet(elt, FROM, convExpr(expr, errors), errors);
				addFacet(elt, ITEM, value, errors);
			}
			if (keyword.equals(">>-")) {
				addFacet(elt, ALL, ConstantExpressionDescription.create(true), errors);
			}
			keyword = REMOVE;
		} else if (keyword.equals(EQUATION_OP)) {
			// conversion of left member (either a var or a function)
			IExpressionDescription left = null;
			if (expr instanceof VariableRef) {
				left = new OperatorExpressionDescription(ZERO, convExpr(expr, errors));
			} else {
				left = convExpr(expr, errors);
			}
			addFacet(elt, EQUATION_LEFT, left, errors);
			// Translation of right member
			addFacet(elt, EQUATION_RIGHT, value, errors);
		}
		return keyword;
	}

	private static void convertFacets(final Statement stm, final String keyword, final ISyntacticElement elt,
			final Set<Diagnostic> errors) {
		final SymbolProto p = DescriptionFactory.getProto(keyword, null);
		for (final Facet f : EGaml.getFacetsOf(stm)) {
			String fname = EGaml.getKeyOf(f);

			// We change the "<-" and "->" symbols into full names
			if (fname.equals("<-")) {
				fname = keyword.equals(LET) || keyword.equals(SET) ? VALUE : INIT;
			} else if (fname.equals("->")) {
				fname = FUNCTION;
			}

			// We compute (and convert) the expression attached to the facet
			final boolean label = p == null ? false : p.isLabel(fname);
			final IExpressionDescription fexpr = convExpr(f, label, errors);
			addFacet(elt, fname, fexpr, errors);
		}

		// We add the "default" (or omissible) facet to the syntactic element
		String def = stm.getFirstFacet();
		if (def != null) {
			if (def.endsWith(":")) {
				def = def.substring(0, def.length() - 1);
			}
		} else {
			def = DescriptionFactory.getOmissibleFacetForSymbol(keyword);
		}
		if (def != null && !def.isEmpty() && !elt.hasFacet(def)) {
			final IExpressionDescription ed = findExpr(stm, errors);
			if (ed != null) {
				elt.setFacet(def, ed);
			}
		}
	}

	private static String convertKeyword(final String k, final String upper) {
		String keyword = k;
		if ((upper.equals(BATCH) || upper.equals(EXPERIMENT)) && keyword.equals(SAVE)) {
			keyword = SAVE_BATCH;
		} else if (upper.equals(OUTPUT) && keyword.equals(FILE)) {
			keyword = OUTPUT_FILE;
		} else if (upper.equals(DISPLAY) || upper.equals(POPULATION)) {
			if (keyword.equals(SPECIES)) {
				keyword = POPULATION;
			} else if (keyword.equals(GRID)) {
				keyword = GRID_POPULATION;
			}
		}
		return keyword;
	}

	private static final IExpressionDescription convExpr(final EObject expr, final Set<Diagnostic> errors) {
		if (expr == null) {
			return null;
		}
		final IExpressionDescription result = EcoreBasedExpressionDescription.create(expr, errors);
		return result;
	}

	private static final IExpressionDescription convExpr(final Facet facet, final boolean label,
			final Set<Diagnostic> errors) {
		if (facet != null) {
			final Expression expr = facet.getExpr();
			if (expr != null) {
				return label ? convertToLabel(expr, EGaml.getKeyOf(expr)) : convExpr(expr, errors);
			}
			final String name = facet.getName();
			// TODO Verify the use of "facet"
			if (name != null) {
				return convertToLabel(null, name);
			}
		}
		return null;
	}

	final static IExpressionDescription convertToLabel(final EObject target, final String string) {
		final IExpressionDescription ed = LabelExpressionDescription.create(string);
		ed.setTarget(target);
		if (target != null) {
			DescriptionFactory.setGamlDocumentation(target, ed.getExpression());
		}
		return ed;
	}

	final static void convStatements(final ISyntacticElement elt, final List<? extends Statement> ss,
			final Set<Diagnostic> errors) {
		for (final Statement stm : ss) {
			final ISyntacticElement child = convStatement(elt, stm, errors);
			if (child != null) {
				elt.addChild(child);
			}
		}
	}

	private static final IExpressionDescription findExpr(final Statement stm, final Set<Diagnostic> errors) {
		if (stm == null) {
			return null;
		}
		// The order below should be important
		final String name = EGaml.getNameOf(stm);
		if (name != null) {
			return convertToLabel(stm, name);
		}
		final Expression expr = stm.getExpr();
		if (expr != null) {
			return convExpr(expr, errors);
		}
		return null;
	}

	private static final Set<String> varDependenciesOf(final Statement s) {
		final Set<String> list = new HashSet();
		for (final Facet facet : EGaml.getFacetsOf(s)) {
			final Expression expr = facet.getExpr();
			if (expr != null) {
				if (expr instanceof VariableRef) {
					list.add(EGaml.getKeyOf(expr));
				} else {
					for (final TreeIterator<EObject> tree = expr.eAllContents(); tree.hasNext();) {
						final EObject obj = tree.next();
						if (obj instanceof VariableRef) {
							list.add(EGaml.getKeyOf(obj));
						}
					}
				}
			}
		}
		if (list.isEmpty()) {
			return null;
		}
		return list;
	}

}
