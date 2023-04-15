/*******************************************************************************************************
 *
 * EGaml.java, in msi.gama.lang.gaml, is part of the source code of the GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.lang.gaml.gaml.Access;
import msi.gama.lang.gaml.gaml.ActionRef;
import msi.gama.lang.gaml.gaml.ArgumentDefinition;
import msi.gama.lang.gaml.gaml.ArgumentPair;
import msi.gama.lang.gaml.gaml.Array;
import msi.gama.lang.gaml.gaml.BinaryOperator;
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
import msi.gama.lang.gaml.gaml.S_Action;
import msi.gama.lang.gaml.gaml.S_Assignment;
import msi.gama.lang.gaml.gaml.S_Definition;
import msi.gama.lang.gaml.gaml.S_DirectAssignment;
import msi.gama.lang.gaml.gaml.S_Display;
import msi.gama.lang.gaml.gaml.S_Equations;
import msi.gama.lang.gaml.gaml.S_If;
import msi.gama.lang.gaml.gaml.S_Reflex;
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
import msi.gama.lang.gaml.gaml.impl.S_ActionImpl;
import msi.gama.lang.gaml.gaml.impl.S_EquationsImpl;
import msi.gama.lang.gaml.gaml.impl.S_IfImpl;
import msi.gama.lang.gaml.gaml.impl.StatementImpl;
import msi.gama.lang.gaml.gaml.util.GamlSwitch;
import msi.gama.util.GamaMapFactory;
import msi.gaml.compilation.IGamlEcoreUtils;
import msi.gaml.compilation.ast.SyntacticFactory;
import ummisco.gama.dev.utils.COUNTER;

/**
 * The class EGaml.getInstance(). A stateless class, bunch of utilities to work with the various GAML statements and
 * expressions.
 *
 * @author drogoul
 * @since 2012
 *
 */
public class EGaml implements IGamlEcoreUtils {

	/** The Constant instance. */
	private static final EGaml instance = new EGaml();

	/**
	 * Gets the single instance of EGaml.
	 *
	 * @return single instance of EGaml
	 */
	public static EGaml getInstance() { return instance; }

	@Override
	public String getNameOf(final EObject o) {
		if (o instanceof S_Reflex) {
			String s = ((S_Reflex) o).getName();
			if (s == null) return IKeyword.INTERNAL + getKeyOf(o) + COUNTER.COUNT();
		}
		if (o instanceof GamlDefinition) return ((GamlDefinition) o).getName();
		if (o instanceof S_Display) return ((S_Display) o).getName();
		if (o instanceof HeadlessExperiment) return ((HeadlessExperiment) o).getName();

		return null;
	}

	/**
	 * Gets the exprs out of an expression list
	 *
	 * @param o
	 *            the o
	 * @return the exprs of
	 */
	@Override
	public List<Expression> getExprsOf(final EObject o) {
		if (o instanceof ExpressionList && ((ExpressionListImpl) o).eIsSet(GamlPackage.EXPRESSION_LIST__EXPRS))
			return ((ExpressionList) o).getExprs();
		return Collections.EMPTY_LIST;
	}

	/**
	 * Gets the args out of the arguments of an action
	 *
	 * @param args
	 *            the args
	 * @return the args of
	 */
	@Override
	public List<ArgumentDefinition> getArgsOf(final EObject args) {
		if (args == null) return Collections.EMPTY_LIST;
		if (args instanceof ActionArgumentsImpl
				&& ((ActionArgumentsImpl) args).eIsSet(GamlPackage.ACTION_ARGUMENTS__ARGS))
			return ((ActionArgumentsImpl) args).getArgs();
		return Collections.EMPTY_LIST;
	}

	/**
	 * Gets the facets of a statement
	 *
	 * @param s
	 *            the s
	 * @return the facets of
	 */
	@Override
	public List<Facet> getFacetsOf(final EObject s) {
		if (s instanceof StatementImpl) {
			if (((StatementImpl) s).eIsSet(GamlPackage.STATEMENT__FACETS)) return ((StatementImpl) s).getFacets();
		} else if (s instanceof HeadlessExperimentImpl
				&& ((HeadlessExperimentImpl) s).eIsSet(GamlPackage.HEADLESS_EXPERIMENT__FACETS))
			return ((HeadlessExperimentImpl) s).getFacets();
		return Collections.EMPTY_LIST;
	}

	/**
	 * Gets the facets map of a statement
	 *
	 * @param s
	 *            the s
	 * @return the facets map of
	 */
	@Override
	public Map<String, Facet> getFacetsMapOf(final EObject s) {
		final List<? extends EObject> list = getFacetsOf(s);
		if (list.isEmpty()) return Collections.EMPTY_MAP;
		final Map<String, Facet> map = GamaMapFactory.create();
		for (final EObject f : list) { if (f instanceof Facet) { map.put(getKeyOf(f), (Facet) f); } }
		return map;
	}

	/**
	 * Tells if a facet is present in a statement
	 *
	 * @param s
	 *            the s
	 * @return the facets map of
	 */
	@Override
	public boolean hasFacet(final EObject s, final String facet) {
		final List<? extends EObject> list = getFacetsOf(s);
		if (list.isEmpty()) return false;
		for (final EObject f : list) {
			if (f instanceof Facet) {
				final String name = getKeyOf(f);
				if (facet.equals(name)) return true;
			}
		}
		return false;
	}

	/**
	 * Get one particular facet of a statement
	 *
	 * @param s
	 * @return
	 */
	@Override
	public Expression getExpressionAtKey(final EObject s, final String name) {
		if (s == null || name == null) return null;
		if ("value".equals(name) && s instanceof S_DirectAssignment) return ((S_DirectAssignment) s).getValue();
		final List<Facet> list = getFacetsOf(s);

		for (final Facet f : list) {
			final String key = getKeyOf(f);
			if (s instanceof Statement && ("value".equals(name) || "init".equals(name))) {
				if ("<-".equals(key)) return f.getExpr();
			} else if (s instanceof S_Assignment && "item".equals(name) && (key.contains("<") || key.contains(">")))
				return f.getExpr();
			if (name.equals(key)) return f.getExpr();
		}
		return null;
	}

	@Override
	public Expression getExprOf(final EObject s) {
		if (s instanceof Expression) return (Expression) s;
		if (s instanceof Statement) return ((Statement) s).getExpr();
		return null;
	}

	/** The children switch. */
	private final GamlSwitch<Boolean> childrenSwitch = new GamlSwitch<>() {

		@Override
		public Boolean caseModel(final Model object) {
			return ((ModelImpl) object).eIsSet(GamlPackage.MODEL__BLOCK);
		}

		@Override
		public Boolean caseS_Action(final S_Action object) {
			if (((S_ActionImpl) object).eIsSet(GamlPackage.SACTION__ARGS)) return true;
			return caseStatement(object);
		}

		@Override
		public Boolean caseBlock(final Block object) {
			return ((BlockImpl) object).eIsSet(GamlPackage.BLOCK__STATEMENTS);
		}

		@Override
		public Boolean caseStatement(final Statement object) {
			return ((StatementImpl) object).eIsSet(GamlPackage.STATEMENT__BLOCK) || hasFacet(object, IKeyword.VIRTUAL)
			// && ((StatementImpl) object).getBlock().getFunction() == null
			;
		}

		@Override
		public Boolean caseHeadlessExperiment(final HeadlessExperiment object) {
			return ((HeadlessExperimentImpl) object).eIsSet(GamlPackage.HEADLESS_EXPERIMENT__BLOCK)
			// && object.getBlock().getFunction() == null
			;
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
	@Override
	public boolean hasChildren(final EObject obj) {
		return childrenSwitch.doSwitch(obj);
	}

	/**
	 * Gets the statements of a block
	 *
	 * @param block
	 *            the block
	 * @return the statements of
	 */
	@Override
	public List<Statement> getStatementsOf(final EObject block) {

		if (block instanceof BlockImpl) {
			if (((BlockImpl) block).eIsSet(GamlPackage.BLOCK__STATEMENTS)) return ((BlockImpl) block).getStatements();
		} else if (block instanceof Model) return getStatementsOf(((Model) block).getBlock());
		return Collections.EMPTY_LIST;
	}

	/**
	 * Gets the equations of a systems of equations
	 *
	 * @param stm
	 *            the stm
	 * @return the equations of
	 */
	@Override
	public List<S_Assignment> getEquationsOf(final EObject stm) {
		if (stm instanceof S_EquationsImpl && ((S_EquationsImpl) stm).eIsSet(GamlPackage.SEQUATIONS__EQUATIONS))
			return ((S_EquationsImpl) stm).getEquations();
		return Collections.EMPTY_LIST;
	}

	/**
	 * Gets the key of an eObject
	 *
	 * @param f
	 *            the f
	 * @return the key of
	 */
	@Override
	public String getKeyOf(final EObject f) {
		if (f == null) return null;
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
	@Override
	public String getKeyOf(final EObject object, final EClass clazz) {
		final int id = clazz.getClassifierID();
		return switch (id) {
			case GamlPackage.UNARY -> ((Unary) object).getOp();
			case GamlPackage.BINARY_OPERATOR -> ((BinaryOperator) object).getOp();
			case GamlPackage.ARGUMENT_PAIR -> getKeyOfArgumentPair((ArgumentPair) object);
			case GamlPackage.PARAMETER -> getKeyOfParameter((Parameter) object);
			case GamlPackage.MODEL -> IKeyword.MODEL;
			case GamlPackage.STATEMENT -> getKeyOfStatement(object);
			case GamlPackage.FACET -> getKeyOfFacet((Facet) object);
			case GamlPackage.FUNCTION -> getKeyOf(((Function) object).getLeft());
			case GamlPackage.TYPE_REF -> getKeyOfTypeRef((TypeRef) object);
			case GamlPackage.IF -> "?";
			case GamlPackage.VARIABLE_REF, GamlPackage.UNIT_NAME, GamlPackage.ACTION_REF, GamlPackage.SKILL_REF, GamlPackage.EQUATION_REF -> this.getNameOfRef(object);
			case GamlPackage.INT_LITERAL, GamlPackage.STRING_LITERAL, GamlPackage.DOUBLE_LITERAL, GamlPackage.RESERVED_LITERAL, GamlPackage.BOOLEAN_LITERAL, GamlPackage.TERMINAL_EXPRESSION -> ((TerminalExpression) object).getOp();
			default -> {
				final List<EClass> eSuperTypes = clazz.getESuperTypes();
				yield eSuperTypes.isEmpty() ? null : getKeyOf(object, eSuperTypes.get(0));
			}
		};
	}

	/**
	 * Gets the key of argument pair.
	 *
	 * @param object
	 *            the object
	 * @return the key of argument pair
	 */
	private String getKeyOfArgumentPair(final ArgumentPair object) {
		String s = object.getOp();
		return s.endsWith(":") ? s.substring(0, s.length() - 1) : s;
	}

	/**
	 * Gets the key of facet.
	 *
	 * @param object
	 *            the object
	 * @return the key of facet
	 */
	private String getKeyOfFacet(final Facet object) {
		String s = object.getKey();
		return s.endsWith(":") ? s.substring(0, s.length() - 1) : s;
	}

	/**
	 * Gets the key of type ref.
	 *
	 * @param object
	 *            the object
	 * @return the key of type ref
	 */
	private String getKeyOfTypeRef(final TypeRef object) {
		String s = getNameOfRef(object);
		if (s.contains("<")) {
			s = s.split("<")[0];
			// Special case for the 'species<xxx>' case
			if ("species".equals(s)) { s = SyntacticFactory.SPECIES_VAR; }
		}
		return s;

		// s = getNameOfRef(object);
		// if (s.contains("<")) {
		// s = s.split("<")[0];
		// // Special case for the 'species<xxx>' case
		// if ("species".equals(s)) { s = SyntacticFactory.SPECIES_VAR; }
		// }
		// return s;
	}

	/**
	 * Gets the key of parameter.
	 *
	 * @param object
	 *            the object
	 * @return the key of parameter
	 */
	private String getKeyOfParameter(final Parameter object) {
		String s;
		final Parameter p = object;
		s = getKeyOf(p.getLeft());
		if (s == null) { s = p.getBuiltInFacetKey(); }
		return s.endsWith(":") ? s.substring(0, s.length() - 1) : s;
	}

	/**
	 * Gets the key of statement.
	 *
	 * @param object
	 *            the object
	 * @return the key of statement
	 */
	private String getKeyOfStatement(final EObject object) {
		String s;
		s = ((Statement) object).getKey();
		if (s == null && object instanceof S_Definition) {
			final TypeRef type = (TypeRef) ((S_Definition) object).getTkey();
			if (type != null) return getKeyOf(type);
		}
		return s;
	}

	/**
	 * Gets the name of the ref represented by this eObject
	 *
	 * @param o
	 *            the o
	 * @return the name of ref
	 */
	@Override
	public String getNameOfRef(final EObject o) {
		final ICompositeNode n = NodeModelUtils.getNode(o);
		if (n != null) return NodeModelUtils.getTokenText(n);
		if (o instanceof VariableRef) return ((VariableRef) o).getRef().getName();
		if (o instanceof UnitName) return ((UnitName) o).getRef().getName();
		if (o instanceof ActionRef) return ((ActionRef) o).getRef().getName();
		if (o instanceof SkillRef) return ((SkillRef) o).getRef().getName();
		if (o instanceof EquationRef) return ((EquationRef) o).getRef().getName();
		if (o instanceof TypeRef) return ((TypeRef) o).getRef().getName();
		return "";
	}

	/**
	 * Gets the factory for building eObjects
	 *
	 * @return the factory
	 */
	public GamlFactory getFactory() { return (GamlFactory) GamlPackage.eINSTANCE.getEFactoryInstance(); }

	/**
	 * Save an eObject into a string
	 *
	 * @param expr
	 *            the expr
	 * @return the string
	 */

	@Override
	public String toString(final EObject expr) {
		if (expr == null) return null;
		if (expr instanceof Statement) return getNameOf(expr);
		if (expr instanceof Facet) return ((Facet) expr).getName();

		if (!(expr instanceof Expression)) return expr.toString();
		final StringBuilder serializer = new StringBuilder(100);
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
	private void serialize(final StringBuilder serializer, final Expression expr) {
		if (expr == null) {} else if (expr instanceof If) {
			serializer.append("(");
			serialize(serializer, ((If) expr).getLeft());
			serializer.append(")").append(((If) expr).getOp()).append("(");
			serialize(serializer, ((If) expr).getRight());
			serializer.append(")").append(":");
			serialize(serializer, ((If) expr).getIfFalse());
		} else if (expr instanceof StringLiteral) {
			serializer.append(((StringLiteral) expr).getOp());
		} else if (expr instanceof TerminalExpression) {
			serializer.append(((TerminalExpression) expr).getOp());
		} else if (expr instanceof Point) {
			serializer.append("{").append("(");
			serialize(serializer, ((Point) expr).getLeft());
			serializer.append(")").append(((Point) expr).getOp()).append("(");
			serialize(serializer, ((Point) expr).getRight());
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
			serializer.append(((Unary) expr).getOp()).append("(");
			serialize(serializer, ((Unary) expr).getRight());
			serializer.append(")");
		} else if (expr instanceof Function) {
			function(serializer, (Function) expr);
		} else if (expr instanceof Access access) {
			serialize(serializer, access.getLeft());
			serializer.append('.');
			serialize(serializer, access.getRight());
		}
		// else if ( expr instanceof FunctionRef ) {
		// function((FunctionRef) expr);
		// }
		else {
			// serializer.append("(");
			// serialize(serializer, expr.getLeft());
			// serializer.append(")").append(expr.getOp()).append("(");
			// serialize(serializer, expr.getRight());
			// serializer.append(")");
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
	private void function(final StringBuilder serializer, final Function expr) {
		final List<? extends EObject> args = getExprsOf(expr.getRight());
		final String opName = getKeyOf(expr.getLeft());
		switch (args.size()) {
			case 1:
				serializer.append(opName).append("(");
				serialize(serializer, (Expression) args.get(0));
				serializer.append(")");
				break;
			case 2:
				serializer.append("(");
				serialize(serializer, (Expression) args.get(0));
				serializer.append(")").append(opName).append("(");
				serialize(serializer, (Expression) args.get(1));
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
	private void array(final StringBuilder serializer, final List<? extends EObject> args, final boolean arguments) {
		// if arguments is true, parses the list to transform it into a map of
		// args
		// (starting at 1); Experimental right now
		// serializer.append("[");
		final int size = args.size();
		for (int i = 0; i < size; i++) {
			final Expression e = (Expression) args.get(i);
			if (arguments) { serializer.append("arg").append(i).append("::"); }
			serialize(serializer, e);
			if (i < size - 1) { serializer.append(","); }
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
	@Override
	public Statement getStatement(final EObject o) {
		if (o instanceof Statement) return (Statement) o;
		if (o instanceof TypeRef && o.eContainer() instanceof S_Definition
				&& ((S_Definition) o.eContainer()).getTkey() == o)
			return (Statement) o.eContainer();
		return null;

	}

	/**
	 * Checks if this statement includes a batch definition
	 *
	 * @param e
	 *            the e
	 * @return true, if is batch
	 */
	@Override
	public boolean isBatch(final EObject e) {
		if (e instanceof StatementImpl) {
			if (!((StatementImpl) e).eIsSet(GamlPackage.STATEMENT__FACETS)) return false;
			for (final Facet f : ((Statement) e).getFacets()) {
				if (IKeyword.TYPE.equals(getKeyOf(f))) {
					final String type = EGaml.getInstance().getKeyOf(f.getExpr());
					if (IKeyword.BATCH.equals(type) || IKeyword.TEST.equals(type)) return true;
				}
			}
		} else if (e instanceof HeadlessExperimentImpl) {
			if (!((HeadlessExperimentImpl) e).eIsSet(GamlPackage.HEADLESS_EXPERIMENT__FACETS)) return false;
			for (final Facet f : ((HeadlessExperimentImpl) e).getFacets()) {
				if (IKeyword.TYPE.equals(getKeyOf(f))) {
					final String type = EGaml.getInstance().getKeyOf(f.getExpr());
					if (IKeyword.BATCH.equals(type)) return true;
				}
			}
		}
		return false;

	}

	/**
	 * Creates the gaml definition.
	 *
	 * @param t
	 *            the t
	 * @param eClass
	 *            the e class
	 * @return the gaml definition
	 */
	public GamlDefinition createGamlDefinition(final String t, final EClass eClass) {
		GamlDefinition stub = (GamlDefinition) getFactory().create(eClass);
		stub.setName(t);
		return stub;
	}

}
