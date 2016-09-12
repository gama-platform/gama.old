/*********************************************************************************************
 * 
 * 
 * 'GamlQualifiedNameProvider.java', in plugin 'msi.gama.lang.gaml', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.lang.gaml.naming;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.util.IResourceScopeCache;

import com.google.inject.Inject;
import com.google.inject.Provider;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.lang.gaml.gaml.Access;
import msi.gama.lang.gaml.gaml.ActionArguments;
import msi.gama.lang.gaml.gaml.ActionDefinition;
import msi.gama.lang.gaml.gaml.ActionFakeDefinition;
import msi.gama.lang.gaml.gaml.ActionRef;
import msi.gama.lang.gaml.gaml.ArgumentDefinition;
import msi.gama.lang.gaml.gaml.ArgumentPair;
import msi.gama.lang.gaml.gaml.Array;
import msi.gama.lang.gaml.gaml.Binary;
import msi.gama.lang.gaml.gaml.Block;
import msi.gama.lang.gaml.gaml.BooleanLiteral;
import msi.gama.lang.gaml.gaml.Cast;
import msi.gama.lang.gaml.gaml.ColorLiteral;
import msi.gama.lang.gaml.gaml.DoubleLiteral;
import msi.gama.lang.gaml.gaml.Entry;
import msi.gama.lang.gaml.gaml.EquationDefinition;
import msi.gama.lang.gaml.gaml.EquationFakeDefinition;
import msi.gama.lang.gaml.gaml.EquationRef;
import msi.gama.lang.gaml.gaml.Expression;
import msi.gama.lang.gaml.gaml.ExpressionList;
import msi.gama.lang.gaml.gaml.Facet;
import msi.gama.lang.gaml.gaml.Function;
import msi.gama.lang.gaml.gaml.GamlDefinition;
import msi.gama.lang.gaml.gaml.If;
import msi.gama.lang.gaml.gaml.Import;
import msi.gama.lang.gaml.gaml.IntLiteral;
import msi.gama.lang.gaml.gaml.Model;
import msi.gama.lang.gaml.gaml.Pair;
import msi.gama.lang.gaml.gaml.Parameter;
import msi.gama.lang.gaml.gaml.Parameters;
import msi.gama.lang.gaml.gaml.Point;
import msi.gama.lang.gaml.gaml.Pragma;
import msi.gama.lang.gaml.gaml.ReservedLiteral;
import msi.gama.lang.gaml.gaml.S_Action;
import msi.gama.lang.gaml.gaml.S_Assignment;
import msi.gama.lang.gaml.gaml.S_Declaration;
import msi.gama.lang.gaml.gaml.S_Definition;
import msi.gama.lang.gaml.gaml.S_DirectAssignment;
import msi.gama.lang.gaml.gaml.S_Display;
import msi.gama.lang.gaml.gaml.S_Do;
import msi.gama.lang.gaml.gaml.S_Equations;
import msi.gama.lang.gaml.gaml.S_Experiment;
import msi.gama.lang.gaml.gaml.S_Global;
import msi.gama.lang.gaml.gaml.S_If;
import msi.gama.lang.gaml.gaml.S_Loop;
import msi.gama.lang.gaml.gaml.S_Other;
import msi.gama.lang.gaml.gaml.S_Reflex;
import msi.gama.lang.gaml.gaml.S_Return;
import msi.gama.lang.gaml.gaml.S_Set;
import msi.gama.lang.gaml.gaml.S_Solve;
import msi.gama.lang.gaml.gaml.S_Species;
import msi.gama.lang.gaml.gaml.S_Var;
import msi.gama.lang.gaml.gaml.SkillFakeDefinition;
import msi.gama.lang.gaml.gaml.SkillRef;
import msi.gama.lang.gaml.gaml.Statement;
import msi.gama.lang.gaml.gaml.StringEvaluator;
import msi.gama.lang.gaml.gaml.StringLiteral;
import msi.gama.lang.gaml.gaml.TerminalExpression;
import msi.gama.lang.gaml.gaml.TypeDefinition;
import msi.gama.lang.gaml.gaml.TypeFakeDefinition;
import msi.gama.lang.gaml.gaml.TypeInfo;
import msi.gama.lang.gaml.gaml.TypeRef;
import msi.gama.lang.gaml.gaml.Unary;
import msi.gama.lang.gaml.gaml.Unit;
import msi.gama.lang.gaml.gaml.UnitFakeDefinition;
import msi.gama.lang.gaml.gaml.UnitName;
import msi.gama.lang.gaml.gaml.VarDefinition;
import msi.gama.lang.gaml.gaml.VarFakeDefinition;
import msi.gama.lang.gaml.gaml.VariableRef;
import msi.gama.lang.gaml.gaml.speciesOrGridDisplayStatement;
import msi.gama.lang.gaml.gaml.util.GamlSwitch;
import msi.gaml.descriptions.ModelDescription;

/**
 * GAML Qualified Name provider.
 * 
 */
public class GamlQualifiedNameProvider extends IQualifiedNameProvider.AbstractImpl {

	private final static String NULL = "";

	@Inject
	private IResourceScopeCache cache;

	@Override
	public QualifiedName getFullyQualifiedName(final EObject obj) {
		if (obj == null)
			return null;
		return cache.get(obj, obj.eResource(), new Provider<QualifiedName>() {

			@Override
			public QualifiedName get() {
				return GamlQualifiedNameProvider.this.get(obj);
			}
		});
	}

	private QualifiedName get(final EObject input) {
		final String string = new GamlSwitch<String>() {

			@Override
			public String caseS_Reflex(final S_Reflex s) {
				if (s.getKey().equals(IKeyword.ASPECT))
					return s.getName();
				return NULL;
			}

			@Override
			public String casespeciesOrGridDisplayStatement(final speciesOrGridDisplayStatement s) {
				return NULL;
			}

			@Override
			public String caseS_Declaration(final S_Declaration s) {
				return s.getName();
			}

			@Override
			public String caseS_Definition(final S_Definition s) {
				return s.getName();
			}

			@Override
			public String caseS_Equations(final S_Equations s) {
				return s.getName();
			}

			@Override
			public String caseArgumentDefinition(final ArgumentDefinition a) {
				return a.getName();
			}

			@Override
			public String caseS_Species(final S_Species s) {
				return s.getName();
			}

			@Override
			public String caseS_Experiment(final S_Experiment s) {
				return s.getName();
			}

			@Override
			public String caseFacet(final Facet f) {
				return f.getName();
			}

			@Override
			public String caseModel(final Model o) {
				return o.getName() + ModelDescription.MODEL_SUFFIX;
			}

			@Override
			public String caseImport(final Import i) {
				return i.getName();
			}

			@Override
			public String caseS_Display(final S_Display s) {
				return NULL;
			}

			@Override
			public String defaultCase(final EObject e) {
				return NULL;
			}

			@Override
			public String caseEntry(final Entry object) {
				return NULL;
			}

			@Override
			public String caseStringEvaluator(final StringEvaluator object) {
				return NULL;
			}

			@Override
			public String caseBlock(final Block object) {
				return NULL;
			}

			@Override
			public String casePragma(final Pragma object) {
				return NULL;
			}

			@Override
			public String caseStatement(final Statement object) {
				return NULL;
			}

			@Override
			public String caseS_Global(final S_Global object) {
				return NULL;
			}
			//
			// @Override
			// public String caseS_Entities(final S_Entities object) {
			// return NULL;
			// }
			//
			// @Override
			// public String caseS_Environment(final S_Environment object) {
			// return NULL;
			// }

			@Override
			public String caseS_Do(final S_Do object) {
				return NULL;
			}

			@Override
			public String caseS_Loop(final S_Loop object) {
				return object.getName();
			}

			@Override
			public String caseS_If(final S_If object) {
				return NULL;
			}

			@Override
			public String caseS_Other(final S_Other object) {
				return NULL;
			}

			@Override
			public String caseS_Return(final S_Return object) {
				return NULL;
			}

			@Override
			public String caseS_Assignment(final S_Assignment object) {
				return NULL;
			}

			@Override
			public String caseS_DirectAssignment(final S_DirectAssignment object) {
				return NULL;
			}

			@Override
			public String caseS_Set(final S_Set object) {
				return NULL;
			}

			@Override
			public String caseS_Solve(final S_Solve object) {
				return NULL;
			}

			@Override
			public String caseParameters(final Parameters object) {
				return NULL;
			}

			@Override
			public String caseActionArguments(final ActionArguments object) {
				return NULL;
			}

			@Override
			public String caseExpression(final Expression object) {
				return NULL;
			}

			@Override
			public String caseArgumentPair(final ArgumentPair object) {
				return NULL;
			}

			@Override
			public String caseFunction(final Function object) {
				return NULL;
			}

			@Override
			public String caseExpressionList(final ExpressionList object) {
				return NULL;
			}

			@Override
			public String caseVariableRef(final VariableRef object) {
				return NULL;
			}

			@Override
			public String caseTypeInfo(final TypeInfo object) {
				return NULL;
			}

			@Override
			public String caseGamlDefinition(final GamlDefinition object) {
				return object.getName();
			}

			@Override
			public String caseEquationDefinition(final EquationDefinition object) {
				return object.getName();
			}

			@Override
			public String caseTypeDefinition(final TypeDefinition object) {
				return object.getName();
			}

			@Override
			public String caseVarDefinition(final VarDefinition object) {
				return object.getName();
			}

			@Override
			public String caseActionDefinition(final ActionDefinition object) {
				return object.getName();
			}

			@Override
			public String caseUnitFakeDefinition(final UnitFakeDefinition object) {
				return object.getName();
			}

			@Override
			public String caseTypeFakeDefinition(final TypeFakeDefinition object) {
				return object.getName();
			}

			@Override
			public String caseActionFakeDefinition(final ActionFakeDefinition object) {
				return object.getName();
			}

			@Override
			public String caseSkillFakeDefinition(final SkillFakeDefinition object) {
				return object.getName();
			}

			@Override
			public String caseVarFakeDefinition(final VarFakeDefinition object) {
				return object.getName();
			}

			@Override
			public String caseEquationFakeDefinition(final EquationFakeDefinition object) {
				return object.getName();
			}

			@Override
			public String caseTerminalExpression(final TerminalExpression object) {
				return NULL;
			}

			@Override
			public String caseS_Action(final S_Action object) {
				return object.getName();
			}

			@Override
			public String caseS_Var(final S_Var object) {
				return object.getName();
			}

			@Override
			public String casePair(final Pair object) {
				return NULL;
			}

			@Override
			public String caseIf(final If object) {
				return NULL;
			}

			@Override
			public String caseCast(final Cast object) {
				return NULL;
			}

			@Override
			public String caseBinary(final Binary object) {
				return NULL;
			}

			@Override
			public String caseUnit(final Unit object) {
				return NULL;
			}

			@Override
			public String caseUnary(final Unary object) {
				return NULL;
			}

			@Override
			public String caseAccess(final Access object) {
				return NULL;
			}

			@Override
			public String caseArray(final Array object) {
				return NULL;
			}

			@Override
			public String casePoint(final Point object) {
				return NULL;
			}

			@Override
			public String caseParameter(final Parameter object) {
				return NULL;
			}

			@Override
			public String caseUnitName(final UnitName object) {
				return NULL;
			}

			@Override
			public String caseTypeRef(final TypeRef object) {
				return NULL;
			}

			@Override
			public String caseSkillRef(final SkillRef object) {
				return NULL;
			}

			@Override
			public String caseActionRef(final ActionRef object) {
				return NULL;
			}

			@Override
			public String caseEquationRef(final EquationRef object) {
				return NULL;
			}

			@Override
			public String caseIntLiteral(final IntLiteral object) {
				return NULL;
			}

			@Override
			public String caseDoubleLiteral(final DoubleLiteral object) {
				return NULL;
			}

			@Override
			public String caseColorLiteral(final ColorLiteral object) {
				return NULL;
			}

			@Override
			public String caseStringLiteral(final StringLiteral object) {
				return NULL;
			}

			@Override
			public String caseBooleanLiteral(final BooleanLiteral object) {
				return NULL;
			}

			@Override
			public String caseReservedLiteral(final ReservedLiteral object) {
				return NULL;
			}

		}.doSwitch(input);
		if (string == null || string == NULL)
			return null;
		return QualifiedName.create(string);
	}

}