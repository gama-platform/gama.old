/*******************************************************************************************************
 *
 * GamlSyntacticConverter.java, in msi.gama.lang.gaml, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.parsing;

import static msi.gama.common.interfaces.IKeyword.ACTION;
import static msi.gama.common.interfaces.IKeyword.ADD;
import static msi.gama.common.interfaces.IKeyword.ALL;
import static msi.gama.common.interfaces.IKeyword.ARG;
import static msi.gama.common.interfaces.IKeyword.AT;
import static msi.gama.common.interfaces.IKeyword.BATCH;
import static msi.gama.common.interfaces.IKeyword.DEFAULT;
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
import static msi.gama.common.interfaces.IKeyword.SPECIES_LAYER;
import static msi.gama.common.interfaces.IKeyword.PUT;
import static msi.gama.common.interfaces.IKeyword.REMOVE;
import static msi.gama.common.interfaces.IKeyword.SAVE;
import static msi.gama.common.interfaces.IKeyword.SAVE_BATCH;
import static msi.gama.common.interfaces.IKeyword.SET;
import static msi.gama.common.interfaces.IKeyword.SPECIES;
import static msi.gama.common.interfaces.IKeyword.SYNTHETIC;
import static msi.gama.common.interfaces.IKeyword.TITLE;
import static msi.gama.common.interfaces.IKeyword.TO;
import static msi.gama.common.interfaces.IKeyword.TYPE;
import static msi.gama.common.interfaces.IKeyword.VALUE;
import static msi.gama.common.interfaces.IKeyword.WHEN;
import static msi.gama.common.interfaces.IKeyword.WITH;
import static msi.gama.common.interfaces.IKeyword.ZERO;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.diagnostics.Diagnostic;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.lang.gaml.EGaml;
import msi.gama.lang.gaml.expression.ExpressionDescriptionBuilder;
import msi.gama.lang.gaml.gaml.Access;
import msi.gama.lang.gaml.gaml.ActionArguments;
import msi.gama.lang.gaml.gaml.ArgumentDefinition;
import msi.gama.lang.gaml.gaml.Block;
import msi.gama.lang.gaml.gaml.ExperimentFileStructure;
import msi.gama.lang.gaml.gaml.Expression;
import msi.gama.lang.gaml.gaml.ExpressionList;
import msi.gama.lang.gaml.gaml.Facet;
import msi.gama.lang.gaml.gaml.Function;
import msi.gama.lang.gaml.gaml.GamlPackage;
import msi.gama.lang.gaml.gaml.HeadlessExperiment;
import msi.gama.lang.gaml.gaml.Model;
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
import msi.gama.lang.gaml.gaml.S_Try;
import msi.gama.lang.gaml.gaml.StandaloneBlock;
import msi.gama.lang.gaml.gaml.Statement;
import msi.gama.lang.gaml.gaml.TypeRef;
import msi.gama.lang.gaml.gaml.VariableRef;
import msi.gama.lang.gaml.gaml.impl.ModelImpl;
import msi.gama.lang.gaml.resource.GamlResourceServices;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gaml.compilation.ast.ISyntacticElement;
import msi.gaml.compilation.ast.SyntacticFactory;
import msi.gaml.compilation.ast.SyntacticModelElement;
import msi.gaml.compilation.ast.SyntacticModelElement.SyntacticExperimentModelElement;
import msi.gaml.descriptions.ConstantExpressionDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.LabelExpressionDescription;
import msi.gaml.descriptions.OperatorExpressionDescription;
import msi.gaml.descriptions.SymbolProto;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.statements.Facets;

/**
 *
 * The class GamlCompatibilityConverter. Performs a series of transformations between the EObject based representation
 * of GAML models and the representation based on SyntacticElements in GAMA.
 *
 * @author drogoul
 * @since 16 mars 2013
 *
 */
public class GamlSyntacticConverter {

	/** The builder of proto-expressions (not yet compiled). */
	final static ExpressionDescriptionBuilder builder = new ExpressionDescriptionBuilder();

	/** The synthetic action. */
	private static int SYNTHETIC_ACTION = 0;

	/**
	 * Gets the absolute folder path of the resource passed in arguments. Used to get the path to the model files
	 *
	 * @param r
	 *            the r
	 * @return the absolute container folder path of
	 */
	public static String getAbsoluteContainerFolderPathOf(final Resource r) {
		URI uri = r.getURI();
		if (uri.isFile()) {
			uri = uri.trimSegments(1);
			return uri.toFileString();
		}
		if (uri.isPlatform()) {
			final IPath path = GamlResourceServices.getPathOf(r);
			final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			final IContainer folder = file.getParent();
			return folder.getLocation().toString();
		}
		return URI.decode(uri.toString());

	}

	/**
	 * Builds the syntactic contents of the root object passed to it.
	 *
	 * @param root
	 *            the root : either a standalone block, an experiment file or a model
	 * @param errors
	 *            the errors
	 * @return the i syntactic element
	 */
	public ISyntacticElement buildSyntacticContents(final EObject root, final Set<Diagnostic> errors) {
		if (root instanceof StandaloneBlock) {
			final SyntacticModelElement elt = SyntacticFactory.createSyntheticModel(root);
			convertBlock(elt, ((StandaloneBlock) root).getBlock(), errors);
			return elt;
		}
		if (root instanceof ExperimentFileStructure) {
			final HeadlessExperiment he = ((ExperimentFileStructure) root).getExp();
			final String path = getAbsoluteContainerFolderPathOf(root.eResource());
			final SyntacticExperimentModelElement exp = SyntacticFactory.createExperimentModel(root, he, path);
			convertFacets(he, exp.getExperiment(), errors);
			exp.setFacet(NAME, LabelExpressionDescription.create(exp.getExperiment().getName()));
			convStatements(exp.getExperiment(), EGaml.getInstance().getStatementsOf(he.getBlock()), errors);
			return exp;
		}
		if (!(root instanceof Model)) return null;
		final ModelImpl m = (ModelImpl) root;

		final String path = getAbsoluteContainerFolderPathOf(root.eResource());
		final SyntacticModelElement model =
				(SyntacticModelElement) SyntacticFactory.create(MODEL, m, EGaml.getInstance().hasChildren(m), path);
		final Map<String, List<String>> prgm = collectPragmas(m);
		if (prgm != null) { model.setFacet(IKeyword.PRAGMA, ConstantExpressionDescription.create(prgm)); }
		model.setFacet(NAME, convertToLabel(null, m.getName()));
		convStatements(model, EGaml.getInstance().getStatementsOf(m), errors);
		// model.printStats();
		model.compactModel();
		return model;
	}

	/**
	 * Collect pragmas.
	 *
	 * @param m
	 *            the m
	 * @return the list
	 */
	private Map<String, List<String>> collectPragmas(final ModelImpl m) {
		if (!m.eIsSet(GamlPackage.MODEL__PRAGMAS)) return null;
		final List<Pragma> pragmas = m.getPragmas();
		if (pragmas.isEmpty()) return null;
		Map<String, List<String>> result = GamaMapFactory.create();
		for (final Pragma p : pragmas) {
			ExpressionList plugins = p.getPlugins();
			if (plugins != null) {
				List<String> list = GamaListFactory.create();
				for (Expression exp : plugins.getExprs()) { list.add(EGaml.getInstance().toString(exp)); }
				result.put(p.getName(), list);
			} else {
				result.put(p.getName(), null);
			}
		}
		return result;
	}

	/**
	 * Does not define attributes.
	 *
	 * @param keyword
	 *            the keyword
	 * @return true, if successful
	 */
	private boolean doesNotDefineAttributes(final String keyword) {
		final SymbolProto p = DescriptionFactory.getProto(keyword, null);
		if (p == null) return true;
		final int kind = p.getKind();
		return !ISymbolKind.STATEMENTS_CONTAINING_ATTRIBUTES.contains(kind);
	}

	/**
	 * Conv statement.
	 *
	 * @param upper
	 *            the upper
	 * @param stm
	 *            the stm
	 * @param errors
	 *            the errors
	 * @return the i syntactic element
	 */
	private final ISyntacticElement convStatement(final ISyntacticElement upper, final Statement stm,
			final Set<Diagnostic> errors) {
		// We catch its keyword
		String keyword = EGaml.getInstance().getKeyOf(stm);
		if (keyword == null) throw new NullPointerException(
				"Trying to convert a statement with a null keyword. Please debug to understand the cause.");
		keyword = convertKeyword(keyword, upper.getKeyword());

		final boolean upperContainsAttributes = !doesNotDefineAttributes(upper.getKeyword());
		final boolean isVar = stm instanceof S_Definition && !DescriptionFactory.isStatementProto(keyword)
				&& upperContainsAttributes && !EGaml.getInstance().hasChildren(stm);

		final ISyntacticElement elt = isVar ? SyntacticFactory.createVar(keyword, ((S_Definition) stm).getName(), stm)
				: SyntacticFactory.create(keyword, stm, EGaml.getInstance().hasChildren(stm));

		if (stm instanceof S_Assignment) {
			keyword = convertAssignment((S_Assignment) stm, keyword, elt, stm.getExpr(), errors);
		} else if (stm instanceof S_Definition def && !DescriptionFactory.isStatementProto(keyword)) {
			// If we define a variable with this statement
			final TypeRef t = (TypeRef) def.getTkey();
			if (t != null) { addFacet(elt, TYPE, convExpr(t, errors), errors); }
			if (t != null && !upperContainsAttributes) {
				// Translation of "type var ..." to "let var type: type ..." if
				// we are not in a
				// top-level statement (i.e. not in the declaration of a species
				// or an experiment)
				elt.setKeyword(LET);
				keyword = LET;
			} else {
				// Translation of "type1 ID1 (type2 ID2, type3 ID3) {...}" to
				// "action ID1 type: type1 { arg ID2 type: type2; arg ID3 type:
				// type3; ...}"
				if (EGaml.getInstance().hasChildren(def)) {
					elt.setKeyword(ACTION);
					keyword = ACTION;
				}
				convertArgs(def.getArgs(), elt, errors);
			}
		} else if (stm instanceof S_Do) {
			processDo(stm, errors, elt);
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
		} else if (stm instanceof S_Reflex ref) {
			if (ref.getExpr() != null) { addFacet(elt, WHEN, convExpr(ref.getExpr(), errors), errors); }
		} else if (stm instanceof S_Solve) {
			final Expression e = stm.getExpr();
			addFacet(elt, EQUATION, convertToLabel(e, EGaml.getInstance().getKeyOf(e)), errors);
		} else if (stm instanceof S_Try) {
			convCatch((S_Try) stm, elt, errors);
		} else if (IKeyword.PARAMETER.equals(keyword)) { processParameter(stm, errors, elt); }

		// We apply some conversions to the facets expressed in the statement
		convertFacets(stm, keyword, elt, errors);

		if (stm instanceof S_Experiment) {
			processExperiment(elt);
		} else if (METHOD.equals(keyword)) {
			// We apply some conversion for methods (to get the name instead of
			// the "method" keyword)
			final String type = elt.getName();
			if (type != null) { elt.setKeyword(type); }
		} else if (stm instanceof S_Equations) { convStatements(elt, EGaml.getInstance().getEquationsOf(stm), errors); }
		// We convert the block of statements (if any)
		if (!IKeyword.PARAMETER.equals(keyword)) { convertBlock(elt, stm.getBlock(), errors); }
		return elt;
	}

	/**
	 * Process experiment.
	 *
	 * @param elt
	 *            the elt
	 */
	private void processExperiment(final ISyntacticElement elt) {
		// We do it also for experiments, and change their name
		final IExpressionDescription type = elt.getExpressionAt(TYPE);
		if (type == null) { elt.setFacet(TYPE, ConstantExpressionDescription.create(GUI_)); }
		// We modify the names of experiments so as not to confuse them with
		// species
		final String name = elt.getName();
		elt.setFacet(TITLE, convertToLabel(null, "Experiment " + name));
		elt.setFacet(NAME, convertToLabel(null, name));
	}

	/**
	 * Process parameter.
	 *
	 * @param stm
	 *            the stm
	 * @param errors
	 *            the errors
	 * @param elt
	 *            the elt
	 */
	private void processParameter(final Statement stm, final Set<Diagnostic> errors, final ISyntacticElement elt) {
		// As the description of parameters does not accept children, we move the block to the
		// 'on_change' facet.
		Block b = stm.getBlock();
		if (b != null) {
			final ISyntacticElement blockElt =
					SyntacticFactory.create(ACTION, new Facets(NAME, SYNTHETIC + SYNTHETIC_ACTION++), true);
			convertBlock(blockElt, b, errors);
			IExpressionDescription fexpr = convExpr(blockElt, errors);
			addFacet(elt, IKeyword.ON_CHANGE, fexpr, errors);
		}
	}

	/**
	 * Process do.
	 *
	 * @param stm
	 *            the stm
	 * @param errors
	 *            the errors
	 * @param elt
	 *            the elt
	 */
	private void processDo(final Statement stm, final Set<Diagnostic> errors, final ISyntacticElement elt) {
		// Translation of "stm ID (ID1: V1, ID2:V2)" to "stm ID with:(ID1:
		// V1, ID2:V2)"
		final Expression e = stm.getExpr();
		addFacet(elt, ACTION, convertToLabel(e, EGaml.getInstance().getKeyOf(e)), errors);
		// Systematically adds the internal function (see #2915) in order to have the right documentation
		// TODO AD: verify that 'ACTION' is still necessary in that case
		addFacet(elt, INTERNAL_FUNCTION, convExpr(e, errors), errors);
		if (e instanceof Function f) {
			final ExpressionList list = f.getRight();
			if (list != null) { addFacet(elt, WITH, convExpr(list, errors), errors); }

		}
	}

	/**
	 * Convert block.
	 *
	 * @param elt
	 *            the elt
	 * @param block
	 *            the block
	 * @param errors
	 *            the errors
	 */
	public void convertBlock(final ISyntacticElement elt, final Block block, final Set<Diagnostic> errors) {
		if (block != null) { convStatements(elt, EGaml.getInstance().getStatementsOf(block), errors); }
	}

	/**
	 * Adds the facet.
	 *
	 * @param e
	 *            the e
	 * @param key
	 *            the key
	 * @param expr
	 *            the expr
	 * @param errors
	 *            the errors
	 */
	private void addFacet(final ISyntacticElement e, final String key, final IExpressionDescription expr,
			final Set<Diagnostic> errors) {
		if (e.hasFacet(key)) {
			e.setFacet(IGamlIssue.DOUBLED_CODE + key, expr);
		} else {
			e.setFacet(key, expr);
		}
	}

	/**
	 * Conv else.
	 *
	 * @param stm
	 *            the stm
	 * @param elt
	 *            the elt
	 * @param errors
	 *            the errors
	 */
	private void convElse(final S_If stm, final ISyntacticElement elt, final Set<Diagnostic> errors) {
		final EObject elseBlock = stm.getElse();
		if (elseBlock != null) {
			final ISyntacticElement elseElt =
					SyntacticFactory.create(ELSE, elseBlock, EGaml.getInstance().hasChildren(elseBlock));
			if (elseBlock instanceof Statement) {
				elseElt.addChild(convStatement(elt, (Statement) elseBlock, errors));
			} else {
				convStatements(elseElt, EGaml.getInstance().getStatementsOf(elseBlock), errors);
			}
			elt.addChild(elseElt);
		}
	}

	/**
	 * Conv catch.
	 *
	 * @param stm
	 *            the stm
	 * @param elt
	 *            the elt
	 * @param errors
	 *            the errors
	 */
	private void convCatch(final S_Try stm, final ISyntacticElement elt, final Set<Diagnostic> errors) {
		final EObject catchBlock = stm.getCatch();
		if (catchBlock != null) {
			final ISyntacticElement catchElt =
					SyntacticFactory.create(IKeyword.CATCH, catchBlock, EGaml.getInstance().hasChildren(catchBlock));
			convStatements(catchElt, EGaml.getInstance().getStatementsOf(catchBlock), errors);
			elt.addChild(catchElt);
		}
	}

	/**
	 * Convert args.
	 *
	 * @param args
	 *            the args
	 * @param elt
	 *            the elt
	 * @param errors
	 *            the errors
	 */
	private void convertArgs(final ActionArguments args, final ISyntacticElement elt, final Set<Diagnostic> errors) {
		if (args != null) {
			for (final ArgumentDefinition def : EGaml.getInstance().getArgsOf(args)) {
				final ISyntacticElement arg = SyntacticFactory.create(ARG, def, false);
				addFacet(arg, NAME, convertToLabel(null, def.getName()), errors);
				final EObject type = def.getType();
				addFacet(arg, TYPE, convExpr(type, errors), errors);
				final Expression e = def.getDefault();
				if (e != null) { addFacet(arg, DEFAULT, convExpr(e, errors), errors); }
				elt.addChild(arg);
			}
		}
	}

	/**
	 * Convert assignment.
	 *
	 * @param stm
	 *            the stm
	 * @param originalKeyword
	 *            the original keyword
	 * @param elt
	 *            the elt
	 * @param expr
	 *            the expr
	 * @param errors
	 *            the errors
	 * @return the string
	 */
	private String convertAssignment(final S_Assignment stm, final String originalKeyword, final ISyntacticElement elt,
			final Expression expr, final Set<Diagnostic> errors) {
		final IExpressionDescription value = convExpr(stm.getValue(), errors);
		String keyword = originalKeyword;
		if (keyword.endsWith("<-") || SET.equals(keyword)) {
			keyword = processBasicAssignment(elt, expr, errors, value, keyword);
		} else if (keyword.startsWith("<<") || "<+".equals(keyword)) {
			// Translation of "container <+ item" or "container << item" to "add
			// item: item to: container"
			// 08/01/14: Addition of the "<<+" (add all)
			keyword = processAdditiveAssignment(elt, expr, errors, value, keyword);
		} else if (keyword.startsWith(">>") || ">-".equals(keyword)) {
			keyword = processRemovalAssignment(elt, expr, errors, value, keyword);
		} else if (EQUATION_OP.equals(keyword)) { processEquationAssignment(elt, expr, errors, value); }
		return keyword;
	}

	/**
	 * Process equation assignment.
	 *
	 * @param elt
	 *            the elt
	 * @param expr
	 *            the expr
	 * @param errors
	 *            the errors
	 * @param value
	 *            the value
	 */
	private void processEquationAssignment(final ISyntacticElement elt, final Expression expr,
			final Set<Diagnostic> errors, final IExpressionDescription value) {
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

	/**
	 * Process removal assignment.
	 *
	 * @param elt
	 *            the elt
	 * @param expr
	 *            the expr
	 * @param errors
	 *            the errors
	 * @param value
	 *            the value
	 * @param keyword
	 *            the keyword
	 * @return the string
	 */
	private String processRemovalAssignment(final ISyntacticElement elt, final Expression expr,
			final Set<Diagnostic> errors, final IExpressionDescription value, final String keyword) {
		// Translation of "container >> item" or "container >- item" to
		// "remove item: item from: container"
		// 08/01/14: Addition of the ">>-" keyword (remove all)
		elt.setKeyword(REMOVE);
		// 20/01/14: Addition of the access [] to remove from the index
		if (expr instanceof Access && "[".equals(((Access) expr).getOp())
				&& EGaml.getInstance().getExprsOf(((Access) expr).getRight()).size() == 0) {
			addFacet(elt, FROM, convExpr(((Access) expr).getLeft(), errors), errors);
			addFacet(elt, INDEX, value, errors);
		} else {
			addFacet(elt, FROM, convExpr(expr, errors), errors);
			addFacet(elt, ITEM, value, errors);
		}
		if (">>-".equals(keyword)) { addFacet(elt, ALL, ConstantExpressionDescription.create(true), errors); }
		return REMOVE;
	}

	/**
	 * Process additive assignment.
	 *
	 * @param elt
	 *            the elt
	 * @param expr
	 *            the expr
	 * @param errors
	 *            the errors
	 * @param value
	 *            the value
	 * @param keyword
	 *            the keyword
	 * @return the string
	 */
	private String processAdditiveAssignment(final ISyntacticElement elt, final Expression expr,
			final Set<Diagnostic> errors, final IExpressionDescription value, final String keyword) {
		elt.setKeyword(ADD);
		addFacet(elt, TO, convExpr(expr, errors), errors);
		addFacet(elt, ITEM, value, errors);
		if ("<<+".equals(keyword)) { addFacet(elt, ALL, ConstantExpressionDescription.create(true), errors); }
		return ADD;
	}

	/**
	 * Process basic assignment.
	 *
	 * @param elt
	 *            the elt
	 * @param expr
	 *            the expr
	 * @param errors
	 *            the errors
	 * @param value
	 *            the value
	 * @param keyword
	 *            the keyword
	 * @return the string
	 */
	private String processBasicAssignment(final ISyntacticElement elt, final Expression expr,
			final Set<Diagnostic> errors, final IExpressionDescription value, String keyword) {
		// Translation of "container[index] <- value" to
		// "put item: value in: container at: index"
		// 20/1/14: Translation of container[index] +<- value" to
		// "add item: value in: container at: index"
		if (expr instanceof Access && "[".equals(((Access) expr).getOp())) {
			final String kw = "+<-".equals(keyword) ? ADD : PUT;
			final String to = "+<-".equals(keyword) ? TO : IN;
			elt.setKeyword(kw);
			addFacet(elt, ITEM, value, errors);
			addFacet(elt, to, convExpr(((Access) expr).getLeft(), errors), errors);
			final List<Expression> args = EGaml.getInstance().getExprsOf(((Access) expr).getRight());
			if (args.size() == 0) {
				// Add facet all: true when no index is provided
				addFacet(elt, ALL, ConstantExpressionDescription.create(true), errors);
			} else if (args.size() == 1) { // Integer index -- or pair index see #3099
				addFacet(elt, AT, convExpr(args.get(0), errors), errors);
			} else { // Point index
				final IExpressionDescription p = new OperatorExpressionDescription("internal_list",
						convExpr(args.get(0), errors), convExpr(args.get(1), errors));
				addFacet(elt, AT, p, errors);
			}
			keyword = kw;
		} else {
			// Translation of "var <- value" to "set var value: value"
			elt.setKeyword(SET);
			addFacet(elt, VALUE, value, errors);
			keyword = SET;
		}
		return keyword;
	}

	/**
	 * Convert facets.
	 *
	 * @param stm
	 *            the stm
	 * @param keyword
	 *            the keyword
	 * @param elt
	 *            the elt
	 * @param errors
	 *            the errors
	 */
	private void convertFacets(final Statement stm, final String keyword, final ISyntacticElement elt,
			final Set<Diagnostic> errors) {
		final SymbolProto p = DescriptionFactory.getProto(keyword, null);
		for (final Facet f : EGaml.getInstance().getFacetsOf(stm)) {
			String fname = EGaml.getInstance().getKeyOf(f);

			// We change the "<-" and "->" symbols into full names
			if ("<-".equals(fname)) {
				fname = LET.equals(keyword) || SET.equals(keyword) ? VALUE : INIT;
			} else if ("->".equals(fname)) { fname = FUNCTION; }

			// We compute (and convert) the expression attached to the facet
			final boolean label = p == null ? false : p.isLabel(fname);
			final IExpressionDescription fexpr = convExpr(f, label, errors);
			addFacet(elt, fname, fexpr, errors);
		}

		// We add the "default" (or omissible) facet to the syntactic element
		String def = stm.getFirstFacet();
		if (def != null) {
			if (def.endsWith(":")) { def = def.substring(0, def.length() - 1); }
		} else {
			def = DescriptionFactory.getOmissibleFacetForSymbol(keyword);
		}
		if (def != null && !def.isEmpty() && !elt.hasFacet(def)) {
			final IExpressionDescription ed = findExpr(stm, errors);
			if (ed != null) { elt.setFacet(def, ed); }
		}
	}

	/**
	 * Convert facets.
	 *
	 * @param stm
	 *            the stm
	 * @param elt
	 *            the elt
	 * @param errors
	 *            the errors
	 */
	private void convertFacets(final HeadlessExperiment stm, final ISyntacticElement elt,
			final Set<Diagnostic> errors) {
		final SymbolProto p = DescriptionFactory.getProto(EXPERIMENT, null);
		for (final Facet f : EGaml.getInstance().getFacetsOf(stm)) {
			final String fname = EGaml.getInstance().getKeyOf(f);

			// We compute (and convert) the expression attached to the facet
			final boolean label = p == null ? false : p.isLabel(fname);
			final IExpressionDescription fexpr = convExpr(f, label, errors);
			addFacet(elt, fname, fexpr, errors);
		}
		final IExpressionDescription ed = findExpr(stm, errors);
		addFacet(elt, NAME, ed, errors);
		addFacet(elt, TITLE, ed, errors);
		// if (!elt.hasFacet(TYPE)) { addFacet(elt, TYPE, convertToLabel(null, HEADLESS_UI), errors); }
	}

	/**
	 * Convert keyword.
	 *
	 * @param k
	 *            the k
	 * @param upper
	 *            the upper
	 * @return the string
	 */
	private String convertKeyword(final String k, final String upper) {
		String keyword = k;
		if ((BATCH.equals(upper) || EXPERIMENT.equals(upper)) && SAVE.equals(keyword)) {
			keyword = SAVE_BATCH;
		} else if (OUTPUT.equals(upper) && FILE.equals(keyword)) {
			keyword = OUTPUT_FILE;
		} else if (DISPLAY.equals(upper) || SPECIES_LAYER.equals(upper)) {
			if (SPECIES.equals(keyword)) {
				keyword = SPECIES_LAYER;
			} else if (GRID.equals(keyword)) {
				keyword = IKeyword.GRID_LAYER;
			} else if (IKeyword.IMAGE.equals(keyword)) { keyword = IKeyword.IMAGE_LAYER; }
		}
		return keyword;
	}

	/**
	 * Conv expr.
	 *
	 * @param expr
	 *            the expr
	 * @param errors
	 *            the errors
	 * @return the i expression description
	 */
	private final IExpressionDescription convExpr(final EObject expr, final Set<Diagnostic> errors) {
		if (expr == null) return null;
		return builder.create(expr/* , errors */);
	}

	/**
	 * Conv expr.
	 *
	 * @param expr
	 *            the expr
	 * @param errors
	 *            the errors
	 * @return the i expression description
	 */
	private final IExpressionDescription convExpr(final ISyntacticElement expr, final Set<Diagnostic> errors) {
		if (expr == null) return null;
		return ExpressionDescriptionBuilder.create(expr, errors);
	}

	/**
	 * Conv expr.
	 *
	 * @param facet
	 *            the facet
	 * @param label
	 *            the label
	 * @param errors
	 *            the errors
	 * @return the i expression description
	 */
	private final IExpressionDescription convExpr(final Facet facet, final boolean label,
			final Set<Diagnostic> errors) {
		if (facet != null) {
			final Expression expr = facet.getExpr();
			if (expr == null && facet.getBlock() != null) {
				final Block b = facet.getBlock();
				final ISyntacticElement elt =
						SyntacticFactory.create(ACTION, new Facets(NAME, SYNTHETIC + SYNTHETIC_ACTION++), true);
				convertBlock(elt, b, errors);
				return convExpr(elt, errors);
			}
			if (expr != null)
				return label ? convertToLabel(expr, EGaml.getInstance().getKeyOf(expr)) : convExpr(expr, errors);
			final String name = facet.getName();
			// TODO Verify the use of "facet"
			if (name != null) return convertToLabel(null, name);
		}
		return null;
	}

	/**
	 * Convert to label.
	 *
	 * @param target
	 *            the target
	 * @param string
	 *            the string
	 * @return the i expression description
	 */
	final IExpressionDescription convertToLabel(final EObject target, final String string) {

		final IExpressionDescription ed = LabelExpressionDescription.create(string);
		ed.setTarget(target);
		if (target != null) {
			// GamlResourceServices.getResourceDocumenter().setGamlDocumentation(target, ed.getExpression(), true);
		}
		return ed;
	}

	/**
	 * Conv statements.
	 *
	 * @param elt
	 *            the elt
	 * @param ss
	 *            the ss
	 * @param errors
	 *            the errors
	 */
	final void convStatements(final ISyntacticElement elt, final List<? extends Statement> ss,
			final Set<Diagnostic> errors) {
		for (final Statement stm : ss) {
			if (IKeyword.GLOBAL.equals(EGaml.getInstance().getKeyOf(stm))) {
				convStatements(elt, EGaml.getInstance().getStatementsOf(stm.getBlock()), errors);
				convertFacets(stm, IKeyword.GLOBAL, elt, errors);
			} else {
				final ISyntacticElement child = convStatement(elt, stm, errors);
				if (child != null) { elt.addChild(child); }
			}
		}
	}

	/**
	 * Find expr.
	 *
	 * @param stm
	 *            the stm
	 * @param errors
	 *            the errors
	 * @return the i expression description
	 */
	private final IExpressionDescription findExpr(final Statement stm, final Set<Diagnostic> errors) {
		if (stm == null) return null;
		// The order below should be important
		final String name = EGaml.getInstance().getNameOf(stm);
		if (name != null) return convertToLabel(stm, name);
		final Expression expr = stm.getExpr();
		if (expr != null) return convExpr(expr, errors);
		return null;
	}

	/**
	 * Find expr.
	 *
	 * @param stm
	 *            the stm
	 * @param errors
	 *            the errors
	 * @return the i expression description
	 */
	private final IExpressionDescription findExpr(final HeadlessExperiment stm, final Set<Diagnostic> errors) {
		if (stm == null) return null;
		return convertToLabel(stm, EGaml.getInstance().getNameOf(stm));

	}

}
