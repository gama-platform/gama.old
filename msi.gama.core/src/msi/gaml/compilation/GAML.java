/*******************************************************************************************************
 *
 * GAML.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.compilation;

import static com.google.common.collect.Iterables.addAll;
import static msi.gama.common.util.JavaUtils.collectImplementationClasses;
import static msi.gama.util.GamaMapFactory.create;
import static msi.gaml.factories.DescriptionFactory.getStatementProto;
import static msi.gaml.factories.DescriptionFactory.getStatementProtoNames;
import static msi.gaml.types.Types.getBuiltInSpecies;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;

import msi.gama.common.interfaces.ISkill;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IExecutionContext;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.Collector;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.ICollector;
import msi.gama.util.IContainer;
import msi.gama.util.IMap;
import msi.gama.util.file.GamlFileInfo;
import msi.gama.util.file.IGamlResourceInfoProvider;
import msi.gaml.compilation.ast.ISyntacticElement;
import msi.gaml.compilation.kernel.GamaSkillRegistry;
import msi.gaml.descriptions.ExperimentDescription;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IDescription.DescriptionVisitor;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.OperatorProto;
import msi.gaml.descriptions.SkillDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.descriptions.SymbolProto;
import msi.gaml.descriptions.TypeDescription;
import msi.gaml.expressions.GamlExpressionFactory;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IExpressionFactory;
import msi.gaml.expressions.units.UnitConstantExpression;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.factories.ModelFactory;
import msi.gaml.types.Signature;
import msi.gaml.types.Types;

/**
 * Class GAML. Static support for various GAML constructs and functions
 *
 * @author drogoul
 * @since 16 mai 2013
 *
 */
public class GAML {

	/** The operators. */
	public static final IMap<String, IMap<Signature, OperatorProto>> OPERATORS = GamaMapFactory.createUnordered();

	/** The iterators. */
	public static final Set<String> ITERATORS = new HashSet<>();

	/** The Constant CONSTANTS. */
	public static final Set<String> CONSTANTS = new HashSet<>();

	/** The Constant ADDITIONS. */
	public final static Multimap<Class, IDescription> ADDITIONS = HashMultimap.create();

	/** The Constant FIELDS. */
	public final static Multimap<Class, OperatorProto> FIELDS = HashMultimap.create();

	/** The units. */
	public static final Map<String, UnitConstantExpression> UNITS = new HashMap<>();

	/** The Constant VARTYPE2KEYWORDS. */
	public final static Multimap<Integer, String> VARTYPE2KEYWORDS = HashMultimap.create();

	/** The Constant LISTENERS_BY_CLASS. */
	public final static HashMultimap<Class, GamaHelper> LISTENERS_BY_CLASS = HashMultimap.create();

	/** The Constant LISTENERS_BY_NAME. */
	public final static HashMultimap<String, Class> LISTENERS_BY_NAME = HashMultimap.create();

	/** The expression factory. */
	public static volatile IExpressionFactory expressionFactory = null;

	/** The model factory. */
	public static volatile ModelFactory modelFactory = null;

	/** The info provider. */
	private static IGamlResourceInfoProvider infoProvider = null;

	/** The gaml ecore utils. */
	private static IGamlEcoreUtils gamlEcoreUtils = null;

	/**
	 * Not null.
	 *
	 * @param <T>
	 *            the generic type
	 * @param scope
	 *            the scope
	 * @param object
	 *            the object
	 * @return the t
	 */
	public static <T> T notNull(final IScope scope, final T object) {
		return notNull(scope, object, "Error: nil value detected");
	}

	/**
	 * Not null.
	 *
	 * @param <T>
	 *            the generic type
	 * @param scope
	 *            the scope
	 * @param object
	 *            the object
	 * @param error
	 *            the error
	 * @return the t
	 */
	public static <T> T notNull(final IScope scope, final T object, final String error) {
		if (object == null) throw GamaRuntimeException.error(error, scope);
		return object;
	}

	/**
	 * Empty check.
	 *
	 * @param <T>
	 *            the generic type
	 * @param scope
	 *            the scope
	 * @param container
	 *            the container
	 * @return the t
	 */
	@SuppressWarnings ("rawtypes")
	public static <T extends IContainer> T emptyCheck(final IScope scope, final T container) {
		if (notNull(scope, container).isEmpty(scope))
			throw GamaRuntimeException.error("Error: the container is empty", scope);
		return container;
	}

	/**
	 *
	 * Parsing and compiling GAML utilities
	 *
	 */

	public static ModelFactory getModelFactory() {
		if (modelFactory == null) { modelFactory = DescriptionFactory.getModelFactory(); }
		return modelFactory;
	}

	/**
	 * Gets the expression factory.
	 *
	 * @return the expression factory
	 */
	public static IExpressionFactory getExpressionFactory() {
		if (expressionFactory == null) { expressionFactory = new GamlExpressionFactory(); }
		return expressionFactory;
	}

	/**
	 * Evaluate expression.
	 *
	 * @param expression
	 *            the expression
	 * @param a
	 *            the a
	 * @return the object
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public static Object evaluateExpression(final String expression, final IAgent a) throws GamaRuntimeException {
		if (a == null) return null;
		if (expression == null || expression.isEmpty())
			throw GamaRuntimeException.error("Enter a valid expression", a.getScope());
		final IExpression expr = compileExpression(expression, a, true);
		if (expr == null) return null;
		final IScope scope = a.getScope().copy("in temporary expression evaluator");
		final Object o = scope.evaluate(expr, a).getValue();
		GAMA.releaseScope(scope);
		return o;
	}

	/**
	 * Compile expression.
	 *
	 * @param expression
	 *            the expression
	 * @param agent
	 *            the agent
	 * @param onlyExpression
	 *            the only expression
	 * @return the i expression
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public static IExpression compileExpression(final String expression, final IAgent agent,
			final boolean onlyExpression) throws GamaRuntimeException {
		if (agent == null) throw GamaRuntimeException.error("Agent is nil", GAMA.getRuntimeScope());
		final IExecutionContext tempContext = agent.getScope().getExecutionContext();
		return compileExpression(expression, agent, tempContext, onlyExpression);
	}

	/**
	 * Compile expression.
	 *
	 * @param expression
	 *            the expression
	 * @param agent
	 *            the agent
	 * @param tempContext
	 *            the temp context
	 * @param onlyExpression
	 *            the only expression
	 * @return the i expression
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public static IExpression compileExpression(final String expression, final IAgent agent,
			final IExecutionContext tempContext, final boolean onlyExpression) throws GamaRuntimeException {
		if (agent == null) throw GamaRuntimeException.error("Agent is nil", tempContext.getScope());
		final IDescription context = agent.getSpecies().getDescription();
		try {
			return getExpressionFactory().createExpr(expression, context, tempContext);
		} catch (final Throwable e) {
			// Maybe it is a statement instead ?
			if (onlyExpression) throw GamaRuntimeException.create(e, tempContext.getScope());
			try {
				return getExpressionFactory().createTemporaryActionForAgent(agent, expression, tempContext);
			} catch (final Throwable e2) {
				throw GamaRuntimeException.create(e2, tempContext.getScope());
			}
		}
	}

	/**
	 * Gets the model context.
	 *
	 * @return the model context
	 */
	public static ModelDescription getModelContext() {
		IExperimentPlan experiment = GAMA.getExperiment();
		if (experiment == null) return null;
		return experiment.getModel().getDescription();
	}

	/**
	 * Gets the experiment context.
	 *
	 * @param a
	 *            the a
	 * @return the experiment context
	 */
	public static ExperimentDescription getExperimentContext(final IAgent a) {
		if (a == null) return null;
		final IScope scope = a.getScope();
		final ITopLevelAgent agent = scope.getExperiment();
		if (agent == null) return null;
		return (ExperimentDescription) agent.getSpecies().getDescription();
	}

	/**
	 * Register info provider.
	 *
	 * @param info
	 *            the info
	 */
	public static void registerInfoProvider(final IGamlResourceInfoProvider info) {
		infoProvider = info;
	}

	/**
	 * Register gaml ecore utils.
	 *
	 * @param utils
	 *            the utils
	 */
	public static void registerGamlEcoreUtils(final IGamlEcoreUtils utils) {
		gamlEcoreUtils = utils;
	}

	/**
	 * Gets the ecore utils.
	 *
	 * @return the ecore utils
	 */
	public static IGamlEcoreUtils getEcoreUtils() { return gamlEcoreUtils; }

	/**
	 * Gets the info.
	 *
	 * @param uri
	 *            the uri
	 * @param stamp
	 *            the stamp
	 * @return the info
	 */
	public static GamlFileInfo getInfo(final URI uri, final long stamp) {
		return infoProvider.getInfo(uri, stamp);
	}

	/**
	 * Gets the info.
	 *
	 * @param uri
	 *            the uri
	 * @return the info
	 */
	public static GamlFileInfo getInfo(final URI uri) {
		return infoProvider.getInfo(uri);
	}

	/**
	 * Gets the contents.
	 *
	 * @param uri
	 *            the uri
	 * @return the contents
	 */
	public static ISyntacticElement getContents(final URI uri) {
		return infoProvider.getContents(uri);
	}

	/**
	 * Gets the all fields.
	 *
	 * @param clazz
	 *            the clazz
	 * @return the all fields
	 */
	public static Map<String, OperatorProto> getAllFields(final Class clazz) {
		final List<Class> classes = collectImplementationClasses(clazz, Collections.EMPTY_SET, FIELDS.keySet());
		final Map<String, OperatorProto> fieldsMap = create();
		for (final Class c : classes) {
			for (final OperatorProto desc : FIELDS.get(c)) { fieldsMap.put(desc.getName(), desc); }
		}
		return fieldsMap;
	}

	/**
	 * Gets the all children of.
	 *
	 * @param base
	 *            the base
	 * @param skills
	 *            the skills
	 * @return the all children of
	 */
	public static Iterable<IDescription> getAllChildrenOf(final Class base,
			final Iterable<Class<? extends ISkill>> skills) {
		final List<Class> classes = collectImplementationClasses(base, skills, ADDITIONS.keySet());
		try (ICollector<IDescription> list = Collector.getList()) {
			for (Class c : classes) { list.addAll(ADDITIONS.get(c)); }
			return list;
		}
	}

	/**
	 * Gets the all fields.
	 *
	 * @return the all fields
	 */
	public static Collection<OperatorProto> getAllFields() { return FIELDS.values(); }

	/**
	 * Gets the all vars.
	 *
	 * @return the all vars
	 */
	public static Collection<IDescription> getAllVars() {
		final HashSet<IDescription> result = new HashSet<>();

		final DescriptionVisitor<IDescription> varVisitor = desc -> {
			result.add(desc);
			return true;
		};

		final DescriptionVisitor<IDescription> actionVisitor = desc -> {
			addAll(result, ((StatementDescription) desc).getFormalArgs());
			return true;
		};

		for (final TypeDescription desc : Types.getBuiltInSpecies()) {
			desc.visitOwnAttributes(varVisitor);
			desc.visitOwnActions(actionVisitor);

		}
		GamaSkillRegistry.INSTANCE.visitSkills(desc -> {
			((TypeDescription) desc).visitOwnAttributes(varVisitor);
			((TypeDescription) desc).visitOwnActions(actionVisitor);
			return true;
		});

		return result;
	}

	/**
	 * Gets the statements for skill.
	 *
	 * @param s
	 *            the s
	 * @return the statements for skill
	 */
	public static Collection<SymbolProto> getStatementsForSkill(final String s) {
		final Set<SymbolProto> result = new LinkedHashSet<>();
		for (final String p : getStatementProtoNames()) {
			final SymbolProto proto = getStatementProto(p, s);
			if (proto != null && proto.shouldBeDefinedIn(s)) { result.add(proto); }
		}
		return result;
	}

	/**
	 * Gets the all actions.
	 *
	 * @return the all actions
	 */
	public static Collection<IDescription> getAllActions() {
		SetMultimap<String, IDescription> result = MultimapBuilder.hashKeys().linkedHashSetValues().build();

		final DescriptionVisitor<IDescription> visitor = desc -> {
			result.put(desc.getName(), desc);
			return true;
		};

		for (final TypeDescription s : getBuiltInSpecies()) { s.visitOwnActions(visitor); }
		GamaSkillRegistry.INSTANCE.visitSkills(desc -> {
			((SkillDescription) desc).visitOwnActions(visitor);
			return true;
		});
		return result.values();
	}

	/**
	 * @param name
	 * @return
	 */
	public static boolean isUnaryOperator(final String name) {
		if (!OPERATORS.containsKey(name)) return false;
		final Map<Signature, OperatorProto> map = OPERATORS.get(name);
		for (final Signature s : map.keySet()) { if (s.isUnary()) return true; }
		return false;
	}

}
