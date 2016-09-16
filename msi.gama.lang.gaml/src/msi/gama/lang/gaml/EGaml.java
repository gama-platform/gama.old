/*********************************************************************************************
 *
 *
 * 'EGaml.java', in plugin 'msi.gama.lang.gaml', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.lang.gaml;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.IResourceServiceProvider;

import com.google.inject.Injector;

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
import msi.gama.lang.gaml.gaml.impl.ModelImpl;
import msi.gama.lang.gaml.gaml.impl.S_EquationsImpl;
import msi.gama.lang.gaml.gaml.impl.S_IfImpl;
import msi.gama.lang.gaml.gaml.impl.StatementImpl;
import msi.gama.lang.gaml.gaml.util.GamlSwitch;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.compilation.SyntacticFactory;

/**
 * The class EGaml. A bunch of utilities to work with the various GAML
 * statements and expressions.
 *
 * @author drogoul
 * @since 7 f�vr. 2012
 *
 */
public class EGaml {

	public static String getNameOf(final Statement s) {
		if (s instanceof GamlDefinition) {
			return ((GamlDefinition) s).getName();
		}
		if (s instanceof S_Display) {
			return ((S_Display) s).getName();
		}
		return null;
	}

	public static List<Expression> getExprsOf(final ExpressionList o) {
		if (o == null) {
			return Collections.EMPTY_LIST;
		}
		if (((ExpressionListImpl) o).eIsSet(GamlPackage.EXPRESSION_LIST__EXPRS)) {
			return o.getExprs();
		}
		return Collections.EMPTY_LIST;
	}

	public static List<ArgumentDefinition> getArgsOf(final ActionArguments args) {
		if (args == null) {
			return Collections.EMPTY_LIST;
		}
		if (((ActionArgumentsImpl) args).eIsSet(GamlPackage.ACTION_ARGUMENTS__ARGS)) {
			return args.getArgs();
		}
		return Collections.EMPTY_LIST;
	}

	public static List<Facet> getFacetsOf(final Statement s) {
		if (((StatementImpl) s).eIsSet(GamlPackage.STATEMENT__FACETS)) {
			return s.getFacets();
		}
		return Collections.EMPTY_LIST;
	}

	public static Map<String, Facet> getFacetsMapOf(final Statement s) {
		final List<Facet> list = getFacetsOf(s);
		if (list.isEmpty()) {
			return Collections.EMPTY_MAP;
		}
		final Map<String, Facet> map = new TOrderedHashMap<String, Facet>();
		for (final Facet f : list) {
			map.put(getKeyOf(f), f);
		}
		return map;
	}

	private static GamlSwitch<Boolean> childrenSwitch = new GamlSwitch() {

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

	public static boolean hasChildren(final EObject obj) {
		return childrenSwitch.doSwitch(obj);
	}

	public static List<? extends Statement> getStatementsOf(final Block block) {
		if (block != null && ((BlockImpl) block).eIsSet(GamlPackage.BLOCK__STATEMENTS)) {
			return block.getStatements();
		}
		return Collections.EMPTY_LIST;
	}

	public static List<? extends Statement> getStatementsOf(final Model model) {
		if (model != null) {
			return getStatementsOf(model.getBlock());
		}
		return Collections.EMPTY_LIST;
	}

	public static List<? extends Statement> getEquationsOf(final S_Equations stm) {
		if (((S_EquationsImpl) stm).eIsSet(GamlPackage.SEQUATIONS__EQUATIONS)) {
			return stm.getEquations();
		}
		return Collections.EMPTY_LIST;
	}

	public static String getKeyOf(final EObject f) {
		if (f == null)
			return null;
		return getKeyOf(f, f.eClass());
	}

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
				if (type != null) {
					return getKeyOf(type);
				}
			}
			return s;
		case GamlPackage.FACET:
			s = ((Facet) object).getKey();
			return s.endsWith(":") ? s.substring(0, s.length() - 1) : s;
		case GamlPackage.FUNCTION:
			final Function ff = (Function) object;
			s = ff.getOp();
			if (s == null) {
				return getKeyOf(ff.getAction());
			}
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

	public static GamlFactory getFactory() {
		return (GamlFactory) GamlPackage.eINSTANCE.getEFactoryInstance();
	}

	/**
	 * ===== SERIALIZATION
	 */

	public static String toString(final EObject expr) {
		if (expr == null) {
			return null;
		}
		if (expr instanceof Statement) {
			return getNameOf((Statement) expr);
		} else if (expr instanceof Facet) {
			return ((Facet) expr).getName();
		}

		if (!(expr instanceof Expression)) {
			return expr.toString();
		}
		final StringBuilder serializer = new StringBuilder(100);
		serializer.setLength(0);
		serialize(serializer, (Expression) expr);
		return serializer.toString();
	}

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

	private static IResourceServiceProvider serviceProvider;
	// private static Injector dependencyInjector;

	public static <T> T getInstance(final Class<T> c) {
		if (serviceProvider == null) {
			// if (dependencyInjector != null)
			// return dependencyInjector.getInstance(c);
			try {
				serviceProvider = IResourceServiceProvider.Registry.INSTANCE
						.getResourceServiceProvider(URI.createPlatformResourceURI("dummy/dummy.gaml", false));
			} catch (final Exception e) {
				System.out.println("Exception in initializing injector: " + e.getMessage());
			}
		}
		return serviceProvider.get(c);
	}

	public static Statement getStatement(final EObject o) {
		if (o instanceof Statement) {
			return (Statement) o;
		}
		if (o instanceof TypeRef && o.eContainer() instanceof S_Definition
				&& ((S_Definition) o.eContainer()).getTkey() == o) {
			return (Statement) o.eContainer();
		}
		return null;

	}

	public static void initializeInjector(final Injector injector2) {
		// dependencyInjector = injector2;
	}

}
