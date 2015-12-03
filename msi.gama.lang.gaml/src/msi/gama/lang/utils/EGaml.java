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
package msi.gama.lang.utils;

import java.util.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.gaml.gaml.impl.*;
import msi.gama.lang.gaml.gaml.util.GamlSwitch;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.compilation.SyntacticFactory;

/**
 * The class EGaml. A bunch of utilities to work with the various GAML statements and expressions.
 *
 * @author drogoul
 * @since 7 f�vr. 2012
 *
 */
public class EGaml {

	public static final GamlSwitch<String> getKey = new GamlSwitch<String>() {

		@Override
		public String caseExpression(final Expression object) {
			return object.getOp();
		}

		@Override
		public String caseArgumentPair(final ArgumentPair object) {
			String s = object.getOp();
			if ( s.endsWith(":") ) {
				s = s.replace(':', ' ');
			}
			return s.trim();
		}

		@Override
		public String caseParameter(final Parameter object) {
			String s = getKeyOf(object.getLeft());
			if ( s == null ) {
				s = object.getBuiltInFacetKey();
			}
			if ( s.endsWith(":") ) {
				s = s.replace(':', ' ');
			}
			return s.trim();
		}

		@Override
		public String caseModel(final Model object) {
			return IKeyword.MODEL;
		}

		@Override
		public String caseStatement(final Statement object) {
			String s = object.getKey();
			if ( s == null && object instanceof S_Definition ) {
				TypeRef type = (TypeRef) ((S_Definition) object).getTkey();
				if ( type != null ) { return caseTypeRef(type); }
			}
			return s;
		}

		@Override
		public String caseFacet(final Facet object) {
			String s = object.getKey();
			if ( s.endsWith(":") ) {
				s = s.replace(':', ' ');
			}
			return s.trim();
		}

		@Override
		public String caseFunction(final Function object) {
			String s = object.getOp();
			if ( s == null ) { return caseActionRef((ActionRef) object.getAction()); }
			return s;
		}

		@Override
		public String caseVariableRef(final VariableRef object) {
			return NodeModelUtils.getTokenText(NodeModelUtils.getNode(object));
		}

		@Override
		public String caseUnitName(final UnitName object) {
			return NodeModelUtils.getTokenText(NodeModelUtils.getNode(object));
		}

		@Override
		public String caseTypeRef(final TypeRef object) {
			String s = NodeModelUtils.getTokenText(NodeModelUtils.getNode(object));

			if ( s.contains("<") ) {
				s = s.split("<")[0];
				// Special case for the 'species<xxx>' case
				if ( s.equals("species") ) {
					s = SyntacticFactory.SPECIES_VAR;
				}
			}
			return s;
		}

		@Override
		public String caseSkillRef(final SkillRef object) {
			String s = NodeModelUtils.getTokenText(NodeModelUtils.getNode(object));
			return s;
		}

		@Override
		public String caseActionRef(final ActionRef object) {
			return NodeModelUtils.getTokenText(NodeModelUtils.getNode(object));
		}

		@Override
		public String caseEquationRef(final EquationRef object) {
			return NodeModelUtils.getTokenText(NodeModelUtils.getNode(object));
		}

		@Override
		public String caseStringLiteral(final StringLiteral object) {
			return object.getOp();
		}

		@Override
		public String doSwitch(final EObject f) {
			if ( f == null ) { return null; }
			String result = super.doSwitch(f);
			return result;
		}

	};

	public static String getNameOf(final Statement s) {
		if ( s instanceof GamlDefinition ) { return ((GamlDefinition) s).getName(); }
		if ( s instanceof S_Experiment ) { return ((S_Experiment) s).getName(); }
		if ( s instanceof S_Display ) { return ((S_Display) s).getName(); }
		return null;
	}

	public static List<Expression> getExprsOf(final ExpressionList o) {
		if ( o == null ) { return Collections.EMPTY_LIST; }
		if ( ((ExpressionListImpl) o).eIsSet(GamlPackage.EXPRESSION_LIST__EXPRS) ) { return o.getExprs(); }
		return Collections.EMPTY_LIST;
	}

	public static List<ArgumentDefinition> getArgsOf(final ActionArguments args) {
		if ( args == null ) { return Collections.EMPTY_LIST; }
		if ( ((ActionArgumentsImpl) args).eIsSet(GamlPackage.ACTION_ARGUMENTS__ARGS) ) { return args.getArgs(); }
		return Collections.EMPTY_LIST;
	}

	public static List<Facet> getFacetsOf(final Statement s) {
		if ( ((StatementImpl) s).eIsSet(GamlPackage.STATEMENT__FACETS) ) { return s.getFacets(); }
		return Collections.EMPTY_LIST;
	}

	public static Map<String, Facet> getFacetsMapOf(final Statement s) {
		List<Facet> list = getFacetsOf(s);
		if ( list.isEmpty() ) { return Collections.EMPTY_MAP; }
		Map<String, Facet> map = new TOrderedHashMap<String, Facet>();
		for ( Facet f : list ) {
			map.put(getKeyOf(f), f);
		}
		return map;
	}

	public static boolean hasChildren(final EObject obj) {
		if ( obj == null ) { return false; }
		if ( obj instanceof S_Equations ) { return ((S_EquationsImpl) obj).eIsSet(GamlPackage.SEQUATIONS__EQUATIONS); }
		if ( obj instanceof Block ) { return ((BlockImpl) obj).eIsSet(GamlPackage.BLOCK__STATEMENTS); }
		if ( obj instanceof Model ) { return ((ModelImpl) obj).eIsSet(GamlPackage.MODEL__BLOCK); }
		if ( obj instanceof Statement ) {
			boolean hasBlock = ((StatementImpl) obj).eIsSet(GamlPackage.STATEMENT__BLOCK);
			if ( hasBlock ) { return true; }
			if ( obj instanceof S_If ) { return ((S_IfImpl) obj).eIsSet(GamlPackage.SIF__ELSE); }
		}
		return false;
	}

	public static List<? extends Statement> getStatementsOf(final Block block) {
		if ( block != null &&
			((BlockImpl) block).eIsSet(GamlPackage.BLOCK__STATEMENTS) ) { return block.getStatements(); }
		return Collections.EMPTY_LIST;
	}

	public static List<? extends Statement> getStatementsOf(final Model model) {
		if ( model != null ) { return getStatementsOf(model.getBlock()); }
		return Collections.EMPTY_LIST;
	}

	public static List<? extends Statement> getEquationsOf(final S_Equations stm) {
		if ( ((S_EquationsImpl) stm).eIsSet(GamlPackage.SEQUATIONS__EQUATIONS) ) { return stm.getEquations(); }
		return Collections.EMPTY_LIST;
	}

	public static String getKeyOf(final EObject f) {
		return getKey.doSwitch(f);
	}

	public static GamlFactory getFactory() {
		return (GamlFactory) GamlPackage.eINSTANCE.getEFactoryInstance();
	}

	// final static StringBuilder serializer = new StringBuilder(100);

	public static String toString(final EObject expr) {
		if ( expr == null ) { return null; }
		if ( expr instanceof Statement ) {
			return getNameOf((Statement) expr);
		} else if ( expr instanceof Facet ) { return ((Facet) expr).getName(); }

		if ( !(expr instanceof Expression) ) { return expr.toString(); }
		StringBuilder serializer = new StringBuilder(100);
		serializer.setLength(0);
		serialize(serializer, (Expression) expr);
		return serializer.toString();
	}

	private static void serialize(final StringBuilder serializer, final Expression expr) {
		if ( expr == null ) {
			return;
		} else if ( expr instanceof If ) {
			serializer.append("(");
			serialize(serializer, expr.getLeft());
			serializer.append(")").append(expr.getOp()).append("(");
			serialize(serializer, expr.getRight());
			serializer.append(")").append(":");
			serialize(serializer, ((If) expr).getIfFalse());
		} else if ( expr instanceof StringLiteral ) {
			serializer.append(((StringLiteral) expr).getOp());
		} else if ( expr instanceof TerminalExpression ) {
			serializer.append(((TerminalExpression) expr).getOp());
		} else if ( expr instanceof Point ) {
			serializer.append("{").append("(");
			serialize(serializer, expr.getLeft());
			serializer.append(")").append(expr.getOp()).append("(");
			serialize(serializer, expr.getRight());
			serializer.append(")");
			if ( ((Point) expr).getZ() != null ) {
				serializer.append(',').append("(");
				serialize(serializer, ((Point) expr).getZ());
				serializer.append(")");
			}
			serializer.append("}");
		} else if ( expr instanceof Array ) {
			array(serializer, ((Array) expr).getExprs().getExprs(), false);
		} else if ( expr instanceof VariableRef || expr instanceof TypeRef || expr instanceof SkillRef ||
			expr instanceof ActionRef || expr instanceof UnitName ) {
			serializer.append(getKeyOf(expr));
		} else if ( expr instanceof Unary ) {
			serializer.append(expr.getOp()).append("(");
			serialize(serializer, expr.getRight());
			serializer.append(")");
		} else if ( expr instanceof Function ) {
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
		List<Expression> args = getExprsOf(expr.getArgs());
		// String opName = EGaml.getKeyOf(expr.getLeft());
		String opName = expr.getOp();
		if ( args.size() == 1 ) {
			serializer.append(opName).append("(");
			serialize(serializer, args.get(0));
			serializer.append(")");
		} else if ( args.size() == 2 ) {
			serializer.append("(");
			serialize(serializer, args.get(0));
			serializer.append(")").append(opName).append("(");
			serialize(serializer, args.get(1));
			serializer.append(")");
		} else {
			serializer.append(opName);
			serializer.append("(");
			array(serializer, args, true);
			serializer.append(")");
		}
	}

	private static void array(final StringBuilder serializer, final List<Expression> args, final boolean arguments) {
		// if arguments is true, parses the list to transform it into a map of args
		// (starting at 1); Experimental right now
		// serializer.append("[");
		int size = args.size();
		for ( int i = 0; i < size; i++ ) {
			Expression e = args.get(i);
			if ( arguments ) {
				serializer.append("arg").append(i).append("::");
			}
			serialize(serializer, e);
			if ( i < size - 1 ) {
				serializer.append(",");
			}
		}
		// serializer.append("]");
	}

	private static IResourceServiceProvider injector;

	public static <T> T getInstance(final Class<T> c) {
		if ( injector == null ) {
			injector = IResourceServiceProvider.Registry.INSTANCE
				.getResourceServiceProvider(URI.createPlatformResourceURI("dummy/dummy.gaml", false));
		}
		return injector.get(c);
	}

	public static Statement getStatement(final EObject o) {
		if ( o instanceof Statement ) { return (Statement) o; }
		if ( o instanceof TypeRef && o.eContainer() instanceof S_Definition &&
			((S_Definition) o.eContainer()).getTkey() == o ) { return (Statement) o.eContainer(); }
		return null;

	}
	//
	// public static int getGamlDocIndex(final EObject object) {
	// if ( object == null ) { return -1; }
	// if ( object instanceof Statement ) { return ((Statement) object).getGamlDoc(); }
	// if ( object instanceof Expression ) { return ((Expression) object).getGamlDoc(); }
	// if ( object instanceof Facet ) { return ((Facet) object).getGamlDoc(); }
	// if ( object instanceof Model ) { return ((Model) object).getGamlDoc(); }
	// System.err.println(">>> Trying to get the gaml doc from object of type " + object.getClass());
	// return -1;
	// }
	//
	// // Returns the index if ok or -1 if not
	//
	// public static int setGamlDocIndex(final EObject object, final int index) {
	// if ( object == null ) { return -1; }
	// if ( object instanceof Statement ) {
	// ((Statement) object).setGamlDoc(index);
	// return index;
	// }
	// if ( object instanceof Expression ) {
	// ((Expression) object).setGamlDoc(index);
	// return index;
	// }
	// if ( object instanceof Facet ) {
	// ((Facet) object).setGamlDoc(index);
	// return index;
	// }
	// if ( object instanceof Model ) {
	// ((Model) object).setGamlDoc(index);
	// return index;
	// }
	// System.err.println(">>> Trying to set the gaml doc to object of type " + object.getClass());
	// return -1;
	// }

}
