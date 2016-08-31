/**
 * Created by drogoul, 10 nov. 2014
 *
 */
package msi.gaml.descriptions;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
import msi.gama.precompiler.GamlProperties;
import msi.gaml.descriptions.IDescription.DescriptionVisitor;
import msi.gaml.descriptions.IDescription.FacetVisitor;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.operators.Strings;

/**
 * Class IDescriptionSerializer.
 *
 * @author drogoul
 * @since 10 nov. 2014
 *
 */

public class SymbolSerializer<C extends SymbolDescription> implements IKeyword {

	final static SymbolSerializer instance = new SymbolSerializer();

	public static SymbolSerializer getInstance() {
		return instance;
	}

	protected SymbolSerializer() {
	}

	public static class VarSerializer extends SymbolSerializer<VariableDescription> {

		final static VarSerializer instance = new VarSerializer();

		public static VarSerializer getInstance() {
			return instance;
		}

		protected VarSerializer() {
		}

		@Override
		protected void collectMetaInformationInSymbol(final VariableDescription desc, final GamlProperties plugins) {
			plugins.put(GamlProperties.PLUGINS, desc.getDefiningPlugin());
			// plugins.put(GamlProperties.STATEMENTS, desc.keyword);
		}

		@Override
		protected void serializeKeyword(final VariableDescription desc, final StringBuilder sb,
				final boolean includingBuiltIn) {
			String k = desc.getKeyword(); // desc.getFacets().getLabel(IKeyword.KEYWORD);
			if (!k.equals(PARAMETER)) {
				final String type = desc.getType().serialize(false);
				if (!type.equals(UNKNOWN)) {
					k = type;
				}
			}
			sb.append(k).append(' ');
		}

		@Override
		protected String serializeFacetValue(final VariableDescription s, final String key,
				final boolean includingBuiltIn) {
			if (key.equals(TYPE) || key.equals(OF) || key.equals(INDEX)) {
				return null;
			}
			if (key.equals(CONST) && s.hasFacet(CONST) && s.getFacet(key).serialize(includingBuiltIn).equals(FALSE)) {
				return null;
			}
			return super.serializeFacetValue(s, key, includingBuiltIn);
		}

		@Override
		protected String serializeFacetKey(final VariableDescription s, final String key,
				final boolean includingBuiltIn) {
			if (key.equals(INIT)) {
				return "<- ";
			}
			return super.serializeFacetKey(s, key, includingBuiltIn);
		}

	}

	public static class SpeciesSerializer extends SymbolSerializer<SpeciesDescription> {

		final static SpeciesSerializer instance = new SpeciesSerializer();

		public static SpeciesSerializer getInstance() {
			return instance;
		}

		private SpeciesSerializer() {

		}

		@Override
		protected String serializeFacetValue(final SpeciesDescription s, final String key,
				final boolean includingBuiltIn) {
			if (key.equals(SKILLS)) {
				final IExpressionDescription ed = s.getFacet(key);
				if (ed == null) {
					return null;
				}
				final Set<String> strings = ed.getStrings(s, true);
				return strings.toString();
			}
			return super.serializeFacetValue(s, key, includingBuiltIn);
		}

		@Override
		protected void collectMetaInformationInSymbol(final SpeciesDescription desc, final GamlProperties plugins) {
			plugins.put(GamlProperties.PLUGINS, desc.getDefiningPlugin());
			plugins.put(GamlProperties.SKILLS, desc.getSkillsNames());
			// plugins.put(GamlProperties.STATEMENTS, desc.keyword);
		}

		@Override
		protected void collectMetaInformationInFacetValue(final SpeciesDescription desc, final String key,
				final GamlProperties plugins) {
			// if (key.equals(SKILLS)) {
			// System.out.println();
			// }
			final IExpressionDescription ed = desc.getFacet(key);
			if (ed == null) {
				return;
			}

			ed.collectMetaInformation(plugins);
		}

		// @Override
		// protected void collectPluginsInFacetValue(final SpeciesDescription s,
		// final String key,
		// final Set<String> plugins) {
		// if ( key.equals(SKILLS) ) {
		// IExpressionDescription ed = s.getFacets().get(key);
		// if ( ed == null ) { return; }
		// Set<String> strings = ed.getStrings(s, true);
		// for ( String name : strings ) {
		// ISkill sk = AbstractGamlAdditions.getSkillInstanceFor(name);
		// if ( sk != null ) {
		// plugins.add(sk.getDefiningPlugin());
		// }
		// }
		// } else if ( key.equals(CONTROL) ) {
		// IExpressionDescription ed = s.getFacets().get(key);
		// if ( ed == null ) { return; }
		// String name = ed.getExpression().literalValue();
		// ISkill sk = AbstractGamlAdditions.getSkillInstanceFor(name);
		// if ( sk != null ) {
		// plugins.add(sk.getDefiningPlugin());
		// }
		// } else {
		// super.collectPluginsInFacetValue(s, key, plugins);
		// }
		// }
		//
	}

	public static class ModelSerializer extends SpeciesSerializer {

		final static ModelSerializer instance = new ModelSerializer();

		public static ModelSerializer getInstance() {
			return instance;
		}

		private ModelSerializer() {
		}

		@Override
		protected void serializeKeyword(final SpeciesDescription desc, final StringBuilder sb,
				final boolean includingBuiltIn) {
			sb.append("model ").append(desc.getName().replace(ModelDescription.MODEL_SUFFIX, "")).append(Strings.LN)
					.append(Strings.LN);
			sb.append("global ");
		}

		@Override
		protected void serializeChildren(final SpeciesDescription desc, final StringBuilder sb,
				final boolean includingBuiltIn) {
			sb.append(' ').append('{').append(Strings.LN);
			Collection<? extends IDescription> children = desc.getAttributes();
			sb.append(Strings.LN);
			sb.append("// Global attributes of ").append(desc.getName()).append(Strings.LN);
			for (final IDescription s : children) {
				serializeChild(s, sb, includingBuiltIn);
			}
			children = desc.getActions();
			sb.append(Strings.LN);
			sb.append("// Global actions of ").append(desc.getName()).append(Strings.LN);
			for (final IDescription s : children) {
				serializeChild(s, sb, includingBuiltIn);
			}
			children = desc.getBehaviors();
			sb.append(Strings.LN);
			sb.append("// Behaviors of ").append(desc.getName()).append(Strings.LN);
			for (final IDescription s : children) {
				serializeChild(s, sb, includingBuiltIn);
			}
			children = desc.getAspects();
			sb.append(Strings.LN);
			sb.append("// Aspects of ").append(desc.getName()).append(Strings.LN);
			for (final IDescription s : children) {
				serializeChild(s, sb, includingBuiltIn);
			}
			sb.append('}').append(Strings.LN);
			if (desc.hasMicroSpecies()) {
				children = desc.getMicroSpecies().values();
				for (final IDescription s : children) {
					sb.append(Strings.LN);
					serializeChild(s, sb, includingBuiltIn);
				}
			}

			children = ((ModelDescription) desc).getExperiments();
			for (final IDescription s : children) {
				sb.append(Strings.LN);
				serializeChild(s, sb, includingBuiltIn);
			}
		}

		@Override
		protected String serializeFacetValue(final SpeciesDescription s, final String key,
				final boolean includingBuiltIn) {
			if (key.equals(NAME)) {
				return null;
			}
			return super.serializeFacetValue(s, key, includingBuiltIn);
		}

	}

	public static class ExperimentSerializer extends SymbolSerializer<ExperimentDescription> {

	}

	public static class StatementSerializer extends SymbolSerializer<StatementDescription> {

		final static StatementSerializer instance = new StatementSerializer();

		public static StatementSerializer getInstance() {
			return instance;
		}

		protected StatementSerializer() {
		}

		@Override
		protected void collectMetaInformationInFacets(final StatementDescription desc, final GamlProperties plugins) {
			super.collectMetaInformationInFacets(desc, plugins);
			if (desc.args == null || desc.args.isEmpty()) {
				return;
			}
			for (final StatementDescription arg : desc.args.values()) {
				collectMetaInformation(arg, plugins);
			}
		}

		@Override
		protected void serializeFacets(final StatementDescription s, final StringBuilder sb,
				final boolean includingBuiltIn) {
			super.serializeFacets(s, sb, includingBuiltIn);
			serializeArgs(s, sb, includingBuiltIn);

		}

		protected void serializeArgs(final StatementDescription desc, final StringBuilder sb,
				final boolean includingBuiltIn) {
			if (desc.args == null || desc.args.isEmpty()) {
				return;
			}
			sb.append("(");
			for (final StatementDescription arg : desc.args.values()) {
				serializeArg(desc, arg, sb, includingBuiltIn);
				sb.append(", ");
			}
			sb.setLength(sb.length() - 2);
			sb.append(")");
		}

		protected void serializeArg(final StatementDescription desc, final StatementDescription arg,
				final StringBuilder sb, final boolean includingBuiltIn) {
			// normally never called as it is redefined for action, do and
			// create
		}

	}

	public static final Set<String> uselessFacets = new HashSet(
			Arrays.asList(/* DEPENDS_ON, KEYWORD, */INTERNAL_FUNCTION, WITH));

	/**
	 * Method serialize()
	 * 
	 * @see msi.gaml.descriptions.IDescriptionSerializer#serialize(msi.gaml.descriptions.IDescription)
	 */
	public final String serialize(final C description, final boolean includingBuiltIn) {
		if (description.isBuiltIn() && !includingBuiltIn) {
			return "";
		}
		final StringBuilder sb = new StringBuilder();
		serialize(description, sb, includingBuiltIn);
		return sb.toString();
	}

	protected void serialize(final C desc, final StringBuilder sb, final boolean includingBuiltIn) {
		serializeKeyword(desc, sb, includingBuiltIn);
		serializeFacets(desc, sb, includingBuiltIn);
		serializeChildren(desc, sb, includingBuiltIn);
	}

	protected void serializeKeyword(final C desc, final StringBuilder sb, final boolean includingBuiltIn) {
		sb.append(desc.getKeyword()).append(' ');
	}

	protected void serializeChildren(final C desc, final StringBuilder sb, final boolean includingBuiltIn) {

		final StringBuilder childBuilder = new StringBuilder();
		desc.visitChildren(new DescriptionVisitor<IDescription>() {

			@Override
			public boolean visit(final IDescription desc) {
				serializeChild(desc, childBuilder, includingBuiltIn);
				return true;
			}
		});
		if (childBuilder.length() == 0) {
			sb.append(';');
			return;
		} else {
			sb.append(' ').append('{').append(Strings.LN);
			sb.append(childBuilder);
			sb.append('}').append(Strings.LN);
		}

	}

	protected void serializeChild(final IDescription s, final StringBuilder sb, final boolean includingBuiltIn) {
		final String gaml = s.serialize(false);
		if (gaml != null && gaml.length() > 0) {
			sb.append(Strings.indent(s.serialize(includingBuiltIn), 1)).append(Strings.LN);
		}
	}

	protected void serializeFacets(final C s, final StringBuilder sb, final boolean includingBuiltIn) {
		final String omit = DescriptionFactory.getOmissibleFacetForSymbol(s.getKeyword());
		final String expr = serializeFacetValue(s, omit, includingBuiltIn);
		if (expr != null) {
			sb.append(expr).append(" ");
		}
		s.visitFacets(new FacetVisitor() {

			@Override
			public boolean visit(final String key, final IExpressionDescription b) {

				if (key.equals(omit)) {
					return true;
				}
				final String expr = serializeFacetValue(s, key, includingBuiltIn);
				if (expr != null) {
					sb.append(serializeFacetKey(s, key, includingBuiltIn)).append(expr).append(" ");
				}

				return true;
			}
		});
	}

	protected String serializeFacetKey(final C s, final String key, final boolean includingBuiltIn) {
		return key + ": ";
	}

	/**
	 * Return null to exclude a facet
	 * 
	 * @param s
	 * @param key
	 * @return
	 */
	protected String serializeFacetValue(final C s, final String key, final boolean includingBuiltIn) {
		if (uselessFacets.contains(key)) {
			return null;
		}
		final IExpressionDescription ed = s.getFacet(key);
		if (ed == null) {
			return null;
		}
		String exprString = ed.serialize(includingBuiltIn);
		// if ( ed.isConstant() && ed.getExpression().getType().id() ==
		// IType.STRING ) {
		// if ( s.getMeta().getPossibleFacets().get(key).types[0] != IType.LABEL
		// ) {
		// exprString = StringUtils.toJavaString(exprString);
		// }
		// }
		// if (key.equals(VIRTUAL) && ed.isConst() && ed.equalsString(FALSE)) {
		// return null;
		// }
		if (exprString.startsWith(INTERNAL)) {
			return null;
		}
		if (ed instanceof LabelExpressionDescription) {
			// boolean isLabel = s.getMeta().isLabel(key);
			final boolean isId = s.getMeta().isId(key);
			if (!isId) {
				exprString = StringUtils.toGamlString(exprString);
			}
		}
		return exprString;

	}

	protected void collectMetaInformation(final C desc, final GamlProperties plugins) {
		collectMetaInformationInSymbol(desc, plugins);
		collectMetaInformationInFacets(desc, plugins);
		collectMetaInformationInChildren(desc, plugins);
		desc.getType().collectMetaInformation(plugins);
	}

	protected void collectMetaInformationInSymbol(final C desc, final GamlProperties plugins) {
		plugins.put(GamlProperties.PLUGINS, desc.getDefiningPlugin());
		plugins.put(GamlProperties.STATEMENTS, desc.getKeyword());
	}

	/**
	 * @param desc
	 * @param plugins
	 */
	protected void collectMetaInformationInFacets(final C desc, final GamlProperties plugins) {
		desc.visitFacets(new FacetVisitor() {

			@Override
			public boolean visit(final String key, final IExpressionDescription exp) {
				collectMetaInformationInFacetValue(desc, key, plugins);
				return true;
			}
		});
	}

	/**
	 * @param desc
	 * @param key
	 * @param plugins
	 */
	protected void collectMetaInformationInFacetValue(final C desc, final String key, final GamlProperties plugins) {
		final IExpressionDescription ed = desc.getFacet(key);
		if (ed == null) {
			return;
		}
		ed.collectMetaInformation(plugins);
	}

	/**
	 * @param desc
	 * @param plugins
	 */
	protected void collectMetaInformationInChildren(final C desc, final GamlProperties plugins) {
		desc.visitChildren(new DescriptionVisitor<IDescription>() {

			@Override
			public boolean visit(final IDescription s) {
				s.collectMetaInformation(plugins);
				return true;
			}
		});

	}

}
