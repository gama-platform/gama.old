/**
 * Created by drogoul, 7 févr. 2012
 * 
 */
package msi.gama.lang.utils;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.lang.gaml.gaml.*;
import msi.gama.lang.gaml.gaml.impl.*;
import msi.gama.lang.gaml.gaml.util.GamlSwitch;
import msi.gaml.descriptions.IGamlDescription;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.*;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.IResourceServiceProvider;

/**
 * The class EGaml. A bunch of utilities to work with the various GAML statements and expressions.
 * 
 * @author drogoul
 * @since 7 févr. 2012
 * 
 */
public class EGaml {

	public static IGamlDescription getGamlDescription(final EObject object) {
		if ( object == null ) { return null; }
		for ( Adapter o : object.eAdapters() ) {
			if ( o instanceof IGamlDescription ) { return (IGamlDescription) o; }
		}
		return null;
	}

	public static <T> T getGamlDescription(final EObject object, final Class<T> preciseClass) {
		if ( object == null ) { return null; }
		for ( int i = 0, n = object.eAdapters().size(); i < n; i++ ) {
			Adapter a = object.eAdapters().get(i);
			if ( preciseClass.isAssignableFrom(a.getClass()) ) { return (T) a; }

		}
		return null;
	}

	public static void setGamlDescription(final EObject object, final IGamlDescription description) {
		if ( description == null ) { return; }
		IGamlDescription existing = getGamlDescription(object, description.getClass());
		if ( existing != null ) {
			object.eAdapters().remove(existing);
		}
		object.eAdapters().add(description);
	}

	public static void unsetGamlDescription(final EObject object, final IGamlDescription description) {
		if ( object == null ) { return; }
		object.eAdapters().remove(description);
	}

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

		// @Override
		// public String caseFacetRef(final FacetRef object) {
		// return object.getRef();
		// }

		@Override
		// public String caseFacetExpr(final FacetExpr object) {
		public String caseFacet(final Facet object) {
			String s = object.getKey();
			if ( s.endsWith(":") ) {
				s = s.replace(':', ' ');
			}
			return s.trim();
			// String ref = object.getKey();
			// return ref == null ? null : caseFacetRef(ref);

		}

		@Override
		public String caseFunction(final Function object) {
			// 6v4:
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
			return s.split("<")[0];
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
			return super.doSwitch(f);
		}

	};

	public static String getNameOf(final Statement s) {
		if ( s instanceof GamlDefinition ) { return ((GamlDefinition) s).getName(); }
		return null;
	}

	public static Expression getExprOf(final Statement s) {
		return s.getExpr();
	}

	public static ActionArguments getArgsOf(final S_Definition stm) {
		return stm.getArgs();
	}

	public static Expression getFunctionOf(final Statement stm) {
		// Prendre en compte le cas "type var function: expression".
		Expression expr = null;
		if ( getBlockOf(stm) != null ) {
			expr = getBlockOf(stm).getFunction();
		}
		return expr;
	}

	public static List<Expression> getExprsOf(final ExpressionList o) {
		if ( o == null ) { return Collections.EMPTY_LIST; }
		if ( ((ExpressionListImpl) o).eIsSet(GamlPackage.EXPRESSION_LIST__EXPRS) ) { return o
			.getExprs(); }
		return Collections.EMPTY_LIST;
	}

	public static Expression getValueOf(final Statement s) {
		if ( s instanceof S_Assignment ) { return ((S_Assignment) s).getValue(); }
		return null;
	}

	public static EList<Facet> getFacetsOf(final Statement s) {
		if ( ((StatementImpl) s).eIsSet(GamlPackage.STATEMENT__FACETS) ) { return s.getFacets(); }
		return (EList<Facet>) ECollections.EMPTY_ELIST;
	}

	public static Block getBlockOf(final Statement s) {
		return s.getBlock();
	}

	public static String getKeyOf(final EObject f) {
		return getKey.doSwitch(f);
	}

	public static GamlFactory getFactory() {
		return (GamlFactory) GamlPackage.eINSTANCE.getEFactoryInstance();
	}

	final static StringBuilder serializer = new StringBuilder(100);

	public static String toString(final EObject expr) {
		if ( expr == null ) { return null; }
		if ( expr instanceof Statement ) {
			return getNameOf((Statement) expr);
		} else if ( expr instanceof Facet ) { return ((Facet) expr).getName(); }

		if ( !(expr instanceof Expression) ) { return expr.toString(); }
		serializer.setLength(0);
		serialize((Expression) expr);
		return serializer.toString();
	}

	private static void serialize(final Expression expr) {
		if ( expr == null ) {
			return;
		} else if ( expr instanceof If ) {
			serializer.append("(");
			serialize(expr.getLeft());
			serializer.append(")").append(expr.getOp()).append("(");
			serialize(expr.getRight());
			serializer.append(")").append(":");
			serialize(((If) expr).getIfFalse());
		} else if ( expr instanceof StringLiteral ) {
			serializer.append(((StringLiteral) expr).getOp());
		} else if ( expr instanceof TerminalExpression ) {
			serializer.append(((TerminalExpression) expr).getOp());
		} else if ( expr instanceof Point ) {
			serializer.append("{").append("(");
			serialize(expr.getLeft());
			serializer.append(")").append(expr.getOp()).append("(");
			serialize(expr.getRight());
			serializer.append(")");
			if ( ((Point) expr).getZ() != null ) {
				serializer.append(',').append("(");
				serialize(((Point) expr).getZ());
				serializer.append(")");
			}
			serializer.append("}");
		} else if ( expr instanceof Array ) {
			array(((Array) expr).getExprs().getExprs(), false);
		} else if ( expr instanceof VariableRef || expr instanceof TypeRef ||
			expr instanceof SkillRef || expr instanceof ActionRef || expr instanceof UnitName ) {
			serializer.append(getKeyOf(expr));
		} else if ( expr instanceof Unary ) {
			serializer.append(expr.getOp()).append("(");
			serialize(expr.getRight());
			serializer.append(")");
		} else if ( expr instanceof Function ) {
			function((Function) expr);
		}
		// else if ( expr instanceof FunctionRef ) {
		// function((FunctionRef) expr);
		// }
		else {
			serializer.append("(");
			serialize(expr.getLeft());
			serializer.append(")").append(expr.getOp()).append("(");
			serialize(expr.getRight());
			serializer.append(")");
		}
	}

	private static void function(final Function expr) {
		List<Expression> args = getExprsOf(expr.getArgs());
		// String opName = EGaml.getKeyOf(expr.getLeft());
		String opName = expr.getOp();
		if ( args.size() == 1 ) {
			serializer.append(opName).append("(");
			serialize(args.get(0));
			serializer.append(")");
		} else if ( args.size() == 2 ) {
			serializer.append("(");
			serialize(args.get(0));
			serializer.append(")").append(opName).append("(");
			serialize(args.get(1));
			serializer.append(")");
		} else {
			serializer.append(opName);
			serializer.append("(");
			array(args, true);
			serializer.append(")");
		}
	}

	private static void array(final List<Expression> args, final boolean arguments) {
		// if arguments is true, parses the list to transform it into a map of args
		// (starting at 1); Experimental right now
		// serializer.append("[");
		int size = args.size();
		for ( int i = 0; i < size; i++ ) {
			Expression e = args.get(i);
			if ( arguments ) {
				serializer.append("arg").append(i).append("::");
			}
			serialize(e);
			if ( i < size - 1 ) {
				serializer.append(",");
			}
		}
		// serializer.append("]");
	}

	private static IResourceServiceProvider injector;

	public static <T> T getInstance(final Class<T> c) {
		if ( injector == null ) {
			injector =
				IResourceServiceProvider.Registry.INSTANCE.getResourceServiceProvider(URI
					.createPlatformResourceURI("dummy/dummy.gaml", false));
		}
		return injector.get(c);
	}

}
