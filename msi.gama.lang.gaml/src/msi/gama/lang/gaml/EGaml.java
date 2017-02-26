/*********************************************************************************************
 *
 * 'EGaml.java, in plugin msi.gama.lang.gaml, is part of the source code of the GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.lang.gaml.gaml.ActionArguments;
import msi.gama.lang.gaml.gaml.ActionRef;
import msi.gama.lang.gaml.gaml.ArgumentDefinition;
import msi.gama.lang.gaml.gaml.ArgumentPair;
import msi.gama.lang.gaml.gaml.Array;
import msi.gama.lang.gaml.gaml.Block;
import msi.gama.lang.gaml.gaml.EquationRef;
import msi.gama.lang.gaml.gaml.Expression;
import msi.gama.lang.gaml.gaml.ExpressionList;
import msi.gama.lang.gaml.gaml.Facet;
import msi.gama.lang.gaml.gaml.Function;
import msi.gama.lang.gaml.gaml.GamlDefinition;
import msi.gama.lang.gaml.gaml.GamlFactory;
import msi.gama.lang.gaml.gaml.GamlPackage;
import msi.gama.lang.gaml.gaml.HeadlessExperiment;
import msi.gama.lang.gaml.gaml.If;
import msi.gama.lang.gaml.gaml.Model;
import msi.gama.lang.gaml.gaml.Parameter;
import msi.gama.lang.gaml.gaml.Point;
import msi.gama.lang.gaml.gaml.S_Definition;
import msi.gama.lang.gaml.gaml.S_Display;
import msi.gama.lang.gaml.gaml.S_Equations;
import msi.gama.lang.gaml.gaml.S_If;
import msi.gama.lang.gaml.gaml.SkillRef;
import msi.gama.lang.gaml.gaml.Statement;
import msi.gama.lang.gaml.gaml.StringLiteral;
import msi.gama.lang.gaml.gaml.TerminalExpression;
import msi.gama.lang.gaml.gaml.TypeRef;
import msi.gama.lang.gaml.gaml.Unary;
import msi.gama.lang.gaml.gaml.UnitName;
import msi.gama.lang.gaml.gaml.VariableRef;
import msi.gama.lang.gaml.gaml.impl.ActionArgumentsImpl;
import msi.gama.lang.gaml.gaml.impl.BlockImpl;
import msi.gama.lang.gaml.gaml.impl.ExpressionListImpl;
import msi.gama.lang.gaml.gaml.impl.HeadlessExperimentImpl;
import msi.gama.lang.gaml.gaml.impl.ModelImpl;
import msi.gama.lang.gaml.gaml.impl.S_EquationsImpl;
import msi.gama.lang.gaml.gaml.impl.S_IfImpl;
import msi.gama.lang.gaml.gaml.impl.StatementImpl;
import msi.gama.lang.gaml.gaml.util.GamlSwitch;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.compilation.ast.SyntacticFactory;

/**
 * The class EGaml. A bunch of utilities to work with the various GAML statements and expressions.
 *
 * @author drogoul
 * @since 2012
 *
 */
public class EGaml {

	/**
	 * Gets the name of a statement
	 *
	 * @param s
	 *            the s
	 * @return the name of
	 */
	public static String getNameOf(final Statement s) {
		if (s instanceof GamlDefinition) { return ((GamlDefinition) s).getName(); }
		if (s instanceof S_Display) { return ((S_Display) s).getName(); }
		return null;
	}

	public static String getNameOf(final HeadlessExperiment s) {
		return s.getName();
	}

	/**
	 * Gets the exprs out of an expression list
	 *
	 * @param o
	 *            the o
	 * @return the exprs of
	 */
	public static List<Expression> getExprsOf(final ExpressionList o) {
		if (o == null) { return Collections.EMPTY_LIST; }
		if (((ExpressionListImpl) o).eIsSet(GamlPackage.EXPRESSION_LIST__EXPRS)) { return o.getExprs(); }
		return Collections.EMPTY_LIST;
	}

	/**
	 * Gets the args out of the arguments of an action
	 *
	 * @param args
	 *            the args
	 * @return the args of
	 */
	public static List<ArgumentDefinition> getArgsOf(final ActionArguments args) {
		if (args == null) { return Collections.EMPTY_LIST; }
		if (((ActionArgumentsImpl) args).eIsSet(GamlPackage.ACTION_ARGUMENTS__ARGS)) { return args.getArgs(); }
		return Collections.EMPTY_LIST;
	}

	/**
	 * Gets the facets of a statement
	 *
	 * @param s
	 *            the s
	 * @return the facets of
	 */
	public static List<Facet> getFacetsOf(final Statement s) {
		if (((StatementImpl) s).eIsSet(GamlPackage.STATEMENT__FACETS)) { return s.getFacets(); }
		return Collections.EMPTY_LIST;
	}

	public static List<Facet> getFacetsOf(final HeadlessExperiment s) {
		if (((HeadlessExperimentImpl) s).eIsSet(GamlPackage.HEADLESS_EXPERIMENT__FACETS)) { return s.getFacets(); }
		return Collections.EMPTY_LIST;
	}

	/**
	 * Gets the facets map of a statement
	 *
	 * @param s
	 *            the s
	 * @return the facets map of
	 */
	public static Map<String, Facet> getFacetsMapOf(final Statement s) {
		final List<Facet> list = getFacetsOf(s);
		if (list.isEmpty()) { return Collections.EMPTY_MAP; }
		final Map<String, Facet> map = new TOrderedHashMap<String, Facet>();
		for (final Facet f : list) {
			map.put(getKeyOf(f), f);
		}
		return map;
	}

	/** The children switch. */
	private static GamlSwitch<Boolean> childrenSwitch = new GamlSwitch<Boolean>() {

		@Override
		public Boolean caseModel(final Model object) {
			return ((ModelImpl) object).eIsSet(GamlPackage.MODEL__BLOCK);
		}

		@Override
		public Boolean caseBlock(final Block object) {
			return ((BlockImpl) object).eIsSet(GamlPackage.BLOCK__STATEMENTS);
		}

		@Override
		public Boolean caseStatement(final Statement object) {
			return ((StatementImpl) object).eIsSet(GamlPackage.STATEMENT__BLOCK)
					&& ((StatementImpl) object).getBlock().getFunction() == null;
		}

		@Override
		public Boolean caseHeadlessExperiment(final HeadlessExperiment object) {
			return ((HeadlessExperimentImpl) object).eIsSet(GamlPackage.HEADLESS_EXPERIMENT__BLOCK)
					&& object.getBlock().getFunction() == null;
		}

		@Override
		public Boolean caseS_Equations(final S_Equations object) {
			return ((S_EquationsImpl) object).eIsSet(GamlPackage.SEQUATIONS__EQUATIONS);
		}

		@Override
		public Boolean caseS_If(final S_If object) {
			return caseStatement(object) || ((S_IfImpl) object).eIsSet(GamlPackage.SIF__ELSE);
		}

		@Override
		public Boolean defaultCase(final EObject object) {
			return false;
		}

	};

	/**
	 * Checks for children.
	 *
	 * @param obj
	 *            the obj
	 * @return true, if successful
	 */
	public static boolean hasChildren(final EObject obj) {
		return childrenSwitch.doSwitch(obj);
	}

	/**
	 * Gets the statements of a block
	 *
	 * @param block
	 *            the block
	 * @return the statements of
	 */
	public static List<? extends Statement> getStatementsOf(final Block block) {
		if (block != null
				&& ((BlockImpl) block).eIsSet(GamlPackage.BLOCK__STATEMENTS)) { return block.getStatements(); }
		return Collections.EMPTY_LIST;
	}

	/**
	 * Gets the statements of a model
	 *
	 * @param model
	 *            the model
	 * @return the statements of
	 */
	public static List<? extends Statement> getStatementsOf(final Model model) {
		if (model != null) { return getStatementsOf(model.getBlock()); }
		return Collections.EMPTY_LIST;
	}

	/**
	 * Gets the equations of a systems of equations
	 *
	 * @param stm
	 *            the stm
	 * @return the equations of
	 */
	public static List<? extends Statement> getEquationsOf(final S_Equations stm) {
		if (((S_EquationsImpl) stm).eIsSet(GamlPackage.SEQUATIONS__EQUATIONS)) { return stm.getEquations(); }
		return Collections.EMPTY_LIST;
	}

	/**
	 * Gets the key of an eObject
	 *
	 * @param f
	 *            the f
	 * @return the key of
	 */
	public static String getKeyOf(final EObject f) {
		if (f == null)
			return null;
		return getKeyOf(f, f.eClass());
	}

	/**
	 * Gets the key of an eObject in a given eClass
	 *
	 * @param object
	 *            the object
	 * @param clazz
	 *            the clazz
	 * @return the key of
	 */
	public static String getKeyOf(final EObject object, final EClass clazz) {
		String s;
		final int id = clazz.getClassifierID();
		switch (id) {
			case GamlPackage.EXPRESSION:
				return ((Expression) object).getOp();
			case GamlPackage.ARGUMENT_PAIR:
				s = ((ArgumentPair) object).getOp();
				return s.endsWith(":") ? s.substring(0, s.length() - 1) : s;
			case GamlPackage.PARAMETER:
				final Parameter p = (Parameter) object;
				s = getKeyOf(p.getLeft());
				if (s == null) {
					s = p.getBuiltInFacetKey();
				}
				return s.endsWith(":") ? s.substring(0, s.length() - 1) : s;
			case GamlPackage.MODEL:
				return IKeyword.MODEL;
			case GamlPackage.STATEMENT:
				s = ((Statement) object).getKey();
				if (s == null && object instanceof S_Definition) {
					final TypeRef type = (TypeRef) ((S_Definition) object).getTkey();
					if (type != null) { return getKeyOf(type); }
				}
				return s;
			case GamlPackage.FACET:
				s = ((Facet) object).getKey();
				return s.endsWith(":") ? s.substring(0, s.length() - 1) : s;
			case GamlPackage.FUNCTION:
				final Function ff = (Function) object;
				s = ff.getOp();
				if (s == null) { return getKeyOf(ff.getAction()); }
				return s;
			case GamlPackage.TYPE_REF:
				s = getNameOfRef(object);
				if (s.contains("<")) {
					s = s.split("<")[0];
					// Special case for the 'species<xxx>' case
					if (s.equals("species")) {
						s = SyntacticFactory.SPECIES_VAR;
					}
				}
				return s;
			case GamlPackage.VARIABLE_REF:
			case GamlPackage.UNIT_NAME:
			case GamlPackage.ACTION_REF:
			case GamlPackage.SKILL_REF:
			case GamlPackage.EQUATION_REF:
				return getNameOfRef(object);
			case GamlPackage.STRING_LITERAL:
				return ((StringLiteral) object).getOp();
			default:
				final List<EClass> eSuperTypes = clazz.getESuperTypes();
				return eSuperTypes.isEmpty() ? null : getKeyOf(object, eSuperTypes.get(0));
		}
	}

	/**
	 * Gets the name of the ref represented by this eObject
	 *
	 * @param o
	 *            the o
	 * @return the name of ref
	 */
	public static String getNameOfRef(final EObject o) {
		final ICompositeNode n = NodeModelUtils.getNode(o);
		if (n != null)
			return NodeModelUtils.getTokenText(n);
		if (o instanceof VariableRef) {
			return ((VariableRef) o).getRef().getName();
		} else if (o instanceof UnitName) {
			return ((UnitName) o).getRef().getName();
		} else if (o instanceof ActionRef) {
			return ((ActionRef) o).getRef().getName();
		} else if (o instanceof SkillRef) {
			return ((SkillRef) o).getRef().getName();
		} else if (o instanceof EquationRef) {
			return ((EquationRef) o).getRef().getName();
		} else if (o instanceof TypeRef) {
			return ((TypeRef) o).getRef().getName();
		} else
			return "";
	}

	/**
	 * Gets the factory for building eObjects
	 *
	 * @return the factory
	 */
	public static GamlFactory getFactory() {
		return (GamlFactory) GamlPackage.eINSTANCE.getEFactoryInstance();
	}

	/**
	 * Save an eObject into a string
	 *
	 * @param expr
	 *            the expr
	 * @return the string
	 */

	public static String toString(final EObject expr) {
		if (expr == null) { return null; }
		if (expr instanceof Statement) {
			return getNameOf((Statement) expr);
		} else if (expr instanceof Facet) { return ((Facet) expr).getName(); }

		if (!(expr instanceof Expression)) { return expr.toString(); }
		final StringBuilder serializer = new StringBuilder(100);
		serializer.setLength(0);
		serialize(serializer, (Expression) expr);
		return serializer.toString();
	}

	/**
	 * Serialize an expression
	 *
	 * @param serializer
	 *            a string builder to which the expression should be appended
	 * @param expr
	 *            the expr
	 */
	private static void serialize(final StringBuilder serializer, final Expression expr) {
		if (expr == null) {
			return;
		} else if (expr instanceof If) {
			serializer.append("(");
			serialize(serializer, expr.getLeft());
			serializer.append(")").append(expr.getOp()).append("(");
			serialize(serializer, expr.getRight());
			serializer.append(")").append(":");
			serialize(serializer, ((If) expr).getIfFalse());
		} else if (expr instanceof StringLiteral) {
			serializer.append(((StringLiteral) expr).getOp());
		} else if (expr instanceof TerminalExpression) {
			serializer.append(((TerminalExpression) expr).getOp());
		} else if (expr instanceof Point) {
			serializer.append("{").append("(");
			serialize(serializer, expr.getLeft());
			serializer.append(")").append(expr.getOp()).append("(");
			serialize(serializer, expr.getRight());
			serializer.append(")");
			if (((Point) expr).getZ() != null) {
				serializer.append(',').append("(");
				serialize(serializer, ((Point) expr).getZ());
				serializer.append(")");
			}
			serializer.append("}");
		} else if (expr instanceof Array) {
			array(serializer, ((Array) expr).getExprs().getExprs(), false);
		} else if (expr instanceof VariableRef || expr instanceof TypeRef || expr instanceof SkillRef
				|| expr instanceof ActionRef || expr instanceof UnitName) {
			serializer.append(getKeyOf(expr));
		} else if (expr instanceof Unary) {
			serializer.append(expr.getOp()).append("(");
			serialize(serializer, expr.getRight());
			serializer.append(")");
		} else if (expr instanceof Function) {
			function(serializer, (Function) expr);
		}
		// else if ( expr instanceof FunctionRef ) {
		// function((FunctionRef) expr);
		// }
		else {
			serializer.append("(");
			serialize(serializer, expr.getLeft());
			serializer.append(")").append(expr.getOp()).append("(");
			serialize(serializer, expr.getRight());
			serializer.append(")");
		}
	}

	/**
	 * Serializes a function
	 *
	 * @param serializer
	 *            a string builder to which the function should be appended
	 * @param expr
	 *            the expr
	 */
	private static void function(final StringBuilder serializer, final Function expr) {
		final List<Expression> args = getExprsOf(expr.getArgs());
		final String opName = expr.getOp();
		switch (args.size()) {
			case 1:
				serializer.append(opName).append("(");
				serialize(serializer, args.get(0));
				serializer.append(")");
				break;
			case 2:
				serializer.append("(");
				serialize(serializer, args.get(0));
				serializer.append(")").append(opName).append("(");
				serialize(serializer, args.get(1));
				serializer.append(")");
				break;
			default:
				serializer.append(opName);
				serializer.append("(");
				array(serializer, args, true);
				serializer.append(")");
		}
	}

	/**
	 * Serializes a list of arguments
	 *
	 * @param serializer
	 *            a string builder to which the args should be appended
	 * @param args
	 *            the args
	 * @param arguments
	 *            the arguments
	 */
	private static void array(final StringBuilder serializer, final List<Expression> args, final boolean arguments) {
		// if arguments is true, parses the list to transform it into a map of
		// args
		// (starting at 1); Experimental right now
		// serializer.append("[");
		final int size = args.size();
		for (int i = 0; i < size; i++) {
			final Expression e = args.get(i);
			if (arguments) {
				serializer.append("arg").append(i).append("::");
			}
			serialize(serializer, e);
			if (i < size - 1) {
				serializer.append(",");
			}
		}
		// serializer.append("]");
	}

	/**
	 * Gets the statement equal to or including this eObject
	 *
	 * @param o
	 *            the o
	 * @return the statement
	 */
	public static Statement getStatement(final EObject o) {
		if (o instanceof Statement) { return (Statement) o; }
		if (o instanceof TypeRef && o.eContainer() instanceof S_Definition
				&& ((S_Definition) o.eContainer()).getTkey() == o) { return (Statement) o.eContainer(); }
		return null;

	}

	/**
	 * Checks if this statement includes a batch definition
	 *
	 * @param e
	 *            the e
	 * @return true, if is batch
	 */
	public static boolean isBatch(final Statement e) {
		if (!((StatementImpl) e).eIsSet(GamlPackage.STATEMENT__FACETS))
			return false;
		for (final Facet f : e.getFacets()) {
			if (getKeyOf(f).equals(IKeyword.TYPE)) {
				final String type = EGaml.getKeyOf(f.getExpr());
				if (IKeyword.BATCH.equals(type)) { return true; }
			}
		}
		return false;

	}

}
