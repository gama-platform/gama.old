/*********************************************************************************************
 *
 * 'GAML.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation platform. (c)
 * 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.URI;

import com.google.common.collect.Multimap;

import gnu.trove.map.hash.THashMap;
import msi.gama.common.interfaces.IGamlDescription;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IExecutionContext;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.GamlFileInfo;
import msi.gama.util.file.IGamlResourceInfoProvider;
import msi.gaml.compilation.GamlIdiomsProvider;
import msi.gaml.compilation.ast.ISyntacticElement;
import msi.gaml.compilation.kernel.GamaSkillRegistry;
import msi.gaml.descriptions.ActionDescription;
import msi.gaml.descriptions.ExperimentDescription;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IDescription.DescriptionVisitor;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.OperatorProto;
import msi.gaml.descriptions.SkillDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.SymbolProto;
import msi.gaml.descriptions.TypeDescription;
import msi.gaml.descriptions.VariableDescription;
import msi.gaml.expressions.GamlExpressionFactory;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IExpressionCompiler;
import msi.gaml.expressions.IExpressionFactory;
import msi.gaml.expressions.UnitConstantExpression;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.factories.ModelFactory;
import msi.gaml.operators.IUnits;
import msi.gaml.operators.Strings;
import msi.gaml.types.IType;
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

	public static IExpressionFactory expressionFactory = null;
	public static ModelFactory modelFactory = null;
	private static IGamlResourceInfoProvider infoProvider = null;

	public static <T> T notNull(final IScope scope, final T object) {
		return notNull(scope, object, "Error: nil value detected");
	}

	public static <T> T notNull(final IScope scope, final T object, final String error) {
		if (object == null) { throw GamaRuntimeException.error(error, scope); }
		return object;
	}

	private static String[] HTML_TAGS =
			{ "<br/>", "<br>", "<b>", "</b>", "<i>", "</i>", "<ul>", "</ul>", "<li>", "</li>" };
	private static String[] REPLACEMENTS = { Strings.LN, Strings.LN, "", "", "", "", "", "", Strings.LN + "- ", "" };

	public static String toText(final String s) {
		if (s == null)
			return "";
		return breakStringToLines(StringUtils.replaceEach(s, HTML_TAGS, REPLACEMENTS), 120, Strings.LN);
	}

	private static String multiLine(final String longString, final String splitter, final int maxLength) {
		return Arrays.stream(longString.split(splitter)).collect(ArrayList<String>::new, (l, s) -> {
			final Function<ArrayList<String>, Integer> id = list -> list.size() - 1;
			if (l.size() == 0
					|| l.get(id.apply(l)).length() != 0 && l.get(id.apply(l)).length() + s.length() >= maxLength)
				l.add("");
			l.set(id.apply(l), l.get(id.apply(l)) + (l.get(id.apply(l)).length() == 0 ? "" : splitter) + s);
		}, (l1, l2) -> l1.addAll(l2)).stream().reduce((s1, s2) -> s1 + "\n" + s2).get();
	}

	/**
	 * Indicates that a String search operation yielded no results.
	 */
	public static final int NOT_FOUND = -1;

	/**
	 * Version of lastIndexOf that uses regular expressions for searching. By Tomer Godinger.
	 * 
	 * @param str
	 *            String in which to search for the pattern.
	 * @param toFind
	 *            Pattern to locate.
	 * @return The index of the requested pattern, if found; NOT_FOUND (-1) otherwise.
	 */
	public static int lastIndexOfRegex(final String str, final String toFind) {
		final Pattern pattern = Pattern.compile(toFind);
		final Matcher matcher = pattern.matcher(str);

		// Default to the NOT_FOUND constant
		int lastIndex = NOT_FOUND;

		// Search for the given pattern
		while (matcher.find()) {
			lastIndex = matcher.start();
		}

		return lastIndex;
	}

	/**
	 * Finds the last index of the given regular expression pattern in the given string, starting from the given index
	 * (and conceptually going backwards). By Tomer Godinger.
	 * 
	 * @param str
	 *            String in which to search for the pattern.
	 * @param toFind
	 *            Pattern to locate.
	 * @param fromIndex
	 *            Maximum allowed index.
	 * @return The index of the requested pattern, if found; NOT_FOUND (-1) otherwise.
	 */
	public static int lastIndexOfRegex(final String str, final String toFind, final int fromIndex) {
		// Limit the search by searching on a suitable substring
		return lastIndexOfRegex(str.substring(0, fromIndex), toFind);
	}

	/**
	 * Breaks the given string into lines as best possible, each of which no longer than <code>maxLength</code>
	 * characters. By Tomer Godinger.
	 * 
	 * @param str
	 *            The string to break into lines.
	 * @param maxLength
	 *            Maximum length of each line.
	 * @param newLineString
	 *            The string to use for line breaking.
	 * @return The resulting multi-line string.
	 */
	public static String breakStringToLines(String str, final int maxLength, final String newLineString) {
		final StringBuilder result = new StringBuilder();
		while (str.length() > maxLength) {
			// Attempt to break on whitespace first,
			int breakingIndex = lastIndexOfRegex(str, "\\s", maxLength);

			// Then on other non-alphanumeric characters,
			if (breakingIndex == NOT_FOUND)
				breakingIndex = lastIndexOfRegex(str, "[^a-zA-Z0-9]", maxLength);

			// And if all else fails, break in the middle of the word
			if (breakingIndex == NOT_FOUND)
				breakingIndex = maxLength;

			// Append each prepared line to the builder
			result.append(str.substring(0, breakingIndex + 1));
			result.append(newLineString);

			// And start the next line
			str = str.substring(breakingIndex + 1);
		}

		// Check if there are any residual characters left
		if (str.length() > 0) {
			result.append(str);
		}

		// Return the resulting string
		return result.toString();
	}

	public static String getDocumentationOn(final String query) {
		final String keyword = StringUtils.removeEnd(StringUtils.removeStart(query.trim(), "#"), ":");
		final Multimap<GamlIdiomsProvider<?>, IGamlDescription> results = GamlIdiomsProvider.forName(keyword);
		if (results.isEmpty())
			return "No result found";
		final StringBuilder sb = new StringBuilder();
		final int max = results.keySet().stream().mapToInt(each -> each.name.length()).max().getAsInt();
		final String separator = StringUtils.repeat("—", max + 6).concat(Strings.LN);
		results.asMap().forEach((provider, list) -> {
			sb.append("").append(separator).append("|| ");
			sb.append(StringUtils.rightPad(provider.name, max));
			sb.append(" ||").append(Strings.LN).append(separator);
			for (final IGamlDescription d : list)
				sb.append("== ").append(toText(d.getTitle())).append(Strings.LN).append(toText(provider.document(d)))
						.append(Strings.LN);
		});

		return sb.toString();

		//
	}

	public static String getDocumentationOn2(final String query) {
		final String keyword = StringUtils.removeEnd(StringUtils.removeStart(query.trim(), "#"), ":");
		final THashMap<String, String> results = new THashMap<>();
		// Statements
		final SymbolProto p = DescriptionFactory.getStatementProto(keyword);
		if (p != null)
			results.put("Statement", p.getDocumentation());
		DescriptionFactory.visitStatementProtos((name, proto) -> {
			if (proto.getFacet(keyword) != null) {
				results.put("Facet of statement " + name, proto.getFacet(keyword).getDocumentation());
			}
		});
		final Set<String> types = new HashSet<>();
		final String[] facetDoc = { "" };
		DescriptionFactory.visitVarProtos((name, proto) -> {
			if (proto.getFacet(keyword) != null && types.size() < 4) {
				if (!Types.get(name).isAgentType() || name.equals(IKeyword.AGENT))
					types.add(name);
				facetDoc[0] = proto.getFacet(keyword).getDocumentation();
			}
		});
		if (!types.isEmpty()) {
			results.put("Facet of attribute declarations with types " + types + (types.size() == 4 ? " ..." : ""),
					facetDoc[0]);
		}
		// Operators
		final THashMap<Signature, OperatorProto> ops = IExpressionCompiler.OPERATORS.get(keyword);
		if (ops != null) {
			ops.forEachEntry((sig, proto) -> {
				results.put("Operator on " + sig.toString(), proto.getDocumentation());
				return true;
			});
		}
		// Built-in skills
		final SkillDescription sd = GamaSkillRegistry.INSTANCE.get(keyword);
		if (sd != null) {
			results.put("Skill", sd.getDocumentation());
		}
		GamaSkillRegistry.INSTANCE.visitSkills(new DescriptionVisitor<IDescription>() {

			@Override
			public boolean visit(final IDescription desc) {
				final SkillDescription sd = (SkillDescription) desc;
				final VariableDescription var = sd.getAttribute(keyword);
				if (var != null) {
					results.put("Attribute of skill " + desc.getName(), var.getDocumentation());
				}
				final ActionDescription action = sd.getAction(keyword);
				if (action != null) {
					results.put("Primitive of skill " + desc.getName(),
							action.getDocumentation().isEmpty() ? "" : ":" + action.getDocumentation());
				}
				return true;
			}
		});
		// Types
		final IType<?> t = Types.builtInTypes.containsType(keyword) ? Types.get(keyword) : null;
		if (t != null) {
			String tt = t.getDocumentation();
			if (tt == null)
				tt = "type " + keyword;
			results.put("Type", tt);
		}
		// Built-in species
		for (final TypeDescription td : Types.getBuiltInSpecies()) {
			if (td.getName().equals(keyword)) {
				results.put("Built-in species", ((SpeciesDescription) td).getDocumentationWithoutMeta());
			}
			final IDescription var = td.getOwnAttribute(keyword);
			if (var != null) {
				results.put("Attribute of built-in species " + td.getName(), var.getDocumentation());
			}
			final ActionDescription action = td.getOwnAction(keyword);
			if (action != null) {
				results.put("Primitive of built-in species " + td.getName(),
						action.getDocumentation().isEmpty() ? "" : ":" + action.getDocumentation());
			}
		}
		// Constants
		final UnitConstantExpression exp = IUnits.UNITS_EXPR.get(keyword);
		if (exp != null) {
			results.put("Constant", exp.getDocumentation());
		}
		if (results.isEmpty())
			return "No result found";
		final StringBuilder sb = new StringBuilder();
		final int max = results.keySet().stream().mapToInt(each -> each.length()).max().getAsInt();
		final String separator = StringUtils.repeat("—", max + 6).concat(Strings.LN);
		results.forEachEntry((sig, doc) -> {
			sb.append("").append(separator).append("|| ");
			sb.append(StringUtils.rightPad(sig, max));
			sb.append(" ||").append(Strings.LN).append(separator);
			sb.append(toText(doc)).append(Strings.LN);
			return true;
		});

		return sb.toString();

		//
	}

	@SuppressWarnings ("rawtypes")
	public static <T extends IContainer> T emptyCheck(final IScope scope, final T container) {
		if (notNull(scope, container)
				.isEmpty(scope)) { throw GamaRuntimeException.error("Error: the container is empty", scope); }
		return container;
	}

	/**
	 *
	 * Parsing and compiling GAML utilities
	 *
	 */

	public static ModelFactory getModelFactory() {
		if (modelFactory == null) {
			modelFactory = DescriptionFactory.getModelFactory();
		}
		return modelFactory;
	}

	public static IExpressionFactory getExpressionFactory() {
		if (expressionFactory == null) {
			expressionFactory = new GamlExpressionFactory();
		}
		return expressionFactory;
	}

	public static Object evaluateExpression(final String expression, final IAgent a) throws GamaRuntimeException {
		if (a == null) { return null; }
		if (expression == null
				|| expression.isEmpty()) { throw GamaRuntimeException.error("Enter a valid expression", a.getScope()); }
		final IExpression expr = compileExpression(expression, a, true);
		if (expr == null) { return null; }
		final IScope scope = a.getScope().copy("in temporary expression evaluator");
		final Object o = scope.evaluate(expr, a).getValue();
		GAMA.releaseScope(scope);
		return o;
	}

	public static IExpression compileExpression(final String expression, final IAgent agent,
			final boolean onlyExpression) throws GamaRuntimeException {
		if (agent == null)
			throw GamaRuntimeException.error("Agent is nil", GAMA.getRuntimeScope());
		final IExecutionContext tempContext = agent.getScope().getExecutionContext();
		return compileExpression(expression, agent, tempContext, onlyExpression);
	}

	public static IExpression compileExpression(final String expression, final IAgent agent,
			final IExecutionContext tempContext, final boolean onlyExpression) throws GamaRuntimeException {
		if (agent == null)
			throw GamaRuntimeException.error("Agent is nil", tempContext.getScope());
		final IDescription context = agent.getSpecies().getDescription();
		try {
			final IExpression result = getExpressionFactory().createExpr(expression, context, tempContext);
			return result;
		} catch (final Throwable e) {
			// Maybe it is a statement instead ?
			if (!onlyExpression)
				try {
					final IExpression result =
							getExpressionFactory().createTemporaryActionForAgent(agent, expression, tempContext);
					return result;
				} catch (final Throwable e2) {
					throw GamaRuntimeException.create(e2, tempContext.getScope());
				}
			else {
				throw GamaRuntimeException.create(e, tempContext.getScope());
			}
		}
	}

	public static ModelDescription getModelContext() {
		if (GAMA.getFrontmostController() == null) { return null; }
		return (ModelDescription) GAMA.getFrontmostController().getExperiment().getModel().getDescription();
	}

	public static ExperimentDescription getExperimentContext(final IAgent a) {
		if (a == null) { return null; }
		final IScope scope = a.getScope();
		final ITopLevelAgent agent = scope.getExperiment();
		if (agent == null)
			return null;
		return (ExperimentDescription) agent.getSpecies().getDescription();
	}

	public static void registerInfoProvider(final IGamlResourceInfoProvider info) {
		infoProvider = info;
	}

	public static GamlFileInfo getInfo(final URI uri, final long stamp) {
		return infoProvider.getInfo(uri, stamp);
	}

	public static ISyntacticElement getContents(final URI uri) {
		return infoProvider.getContents(uri);
	}

}
