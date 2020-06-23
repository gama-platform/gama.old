/*******************************************************************************************************
 *
 * msi.gaml.descriptions.SymbolSerializer.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.descriptions;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Iterables;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.operators.Strings;
import msi.gaml.statements.Facets;

/**
 * Class IDescriptionSerializer.
 *
 * @author drogoul
 * @since 10 nov. 2014
 *
 */

public class SymbolSerializer<C extends SymbolDescription> implements IKeyword {

	protected SymbolSerializer() {}

	public static class VarSerializer extends SymbolSerializer<VariableDescription> {
		//
		// @Override
		// protected void collectMetaInformationInSymbol(final SymbolDescription desc, final GamlProperties plugins) {
		// plugins.put(GamlProperties.PLUGINS, desc.getDefiningPlugin());
		// // plugins.put(GamlProperties.STATEMENTS, desc.keyword);
		// }

		@Override
		protected void serializeKeyword(final SymbolDescription desc, final StringBuilder sb,
				final boolean includingBuiltIn) {
			String k = desc.getKeyword(); // desc.getFacets().getLabel(IKeyword.KEYWORD);
			if (!k.equals(PARAMETER)) {
				final String type = desc.getGamlType().serialize(false);
				if (!type.equals(UNKNOWN)) {
					k = type;
				}
			}
			sb.append(k).append(' ');
		}

		@Override
		protected String serializeFacetValue(final SymbolDescription s, final String key,
				final boolean includingBuiltIn) {
			if (key.equals(TYPE) || key.equals(OF) || key.equals(INDEX)) { return null; }
			if (key.equals(CONST) && s.hasFacet(CONST) && s.getFacet(key).serialize(includingBuiltIn).equals(FALSE)) {
				return null;
			}
			return super.serializeFacetValue(s, key, includingBuiltIn);
		}

		@Override
		protected String serializeFacetKey(final SymbolDescription s, final String key,
				final boolean includingBuiltIn) {
			if (key.equals(INIT)) { return "<- "; }
			return super.serializeFacetKey(s, key, includingBuiltIn);
		}

	}

	public static class SpeciesSerializer extends SymbolSerializer<SpeciesDescription> {

		@Override
		protected String serializeFacetValue(final SymbolDescription s, final String key,
				final boolean includingBuiltIn) {
			if (key.equals(SKILLS)) {
				final IExpressionDescription ed = s.getFacet(key);
				if (ed == null) { return null; }
				final Collection<String> strings = ed.getStrings(s, true);
				return strings.toString();
			}
			return super.serializeFacetValue(s, key, includingBuiltIn);
		}

		// @Override
		// protected void collectMetaInformationInSymbol(final SymbolDescription desc, final GamlProperties plugins) {
		// plugins.put(GamlProperties.PLUGINS, desc.getDefiningPlugin());
		// plugins.put(GamlProperties.SKILLS, ((SpeciesDescription) desc).getSkillsNames());
		// }
		//
		// @Override
		// protected void collectMetaInformationInFacetValue(final SymbolDescription desc, final String key,
		// final GamlProperties plugins) {
		// final IExpressionDescription ed = desc.getFacet(key);
		// if (ed == null) { return; }
		//
		// ed.collectMetaInformation(plugins);
		// }

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

		@Override
		protected void serializeKeyword(final SymbolDescription desc, final StringBuilder sb,
				final boolean includingBuiltIn) {
			sb.append("model ").append(desc.getName().replace(ModelDescription.MODEL_SUFFIX, "")).append(Strings.LN)
					.append(Strings.LN);
			sb.append("global ");
		}

		@Override
		protected void serializeChildren(final SymbolDescription d, final StringBuilder sb,
				final boolean includingBuiltIn) {
			final SpeciesDescription desc = (SpeciesDescription) d;
			sb.append(' ').append('{').append(Strings.LN);
			Iterable<? extends IDescription> children = desc.getAttributes();
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
		protected String serializeFacetValue(final SymbolDescription s, final String key,
				final boolean includingBuiltIn) {
			if (key.equals(NAME)) { return null; }
			return super.serializeFacetValue(s, key, includingBuiltIn);
		}

	}

	public static class ExperimentSerializer extends SymbolSerializer<ExperimentDescription> {

	}

	public static class StatementSerializer extends SymbolSerializer<StatementDescription> {
		//
		// @Override
		// protected void collectMetaInformationInFacets(final SymbolDescription desc, final GamlProperties plugins) {
		// super.collectMetaInformationInFacets(desc, plugins);
		// // if (desc.formalArgs == null || desc.formalArgs.isEmpty()) {
		// // return;
		// // }
		// // for (final StatementDescription arg : desc.formalArgs.values()) {
		// // collectMetaInformation(arg, plugins);
		// // }
		// }

		@Override
		protected void serializeFacets(final SymbolDescription s, final StringBuilder sb,
				final boolean includingBuiltIn) {
			super.serializeFacets(s, sb, includingBuiltIn);
			serializeArgs(s, sb, includingBuiltIn);

		}

		protected void serializeArgs(final SymbolDescription s, final StringBuilder sb,
				final boolean includingBuiltIn) {
			final StatementDescription desc = (StatementDescription) s;

			final Iterable<IDescription> formalArgs = desc.getFormalArgs();
			if (!Iterables.isEmpty(formalArgs)) {
				sb.append("(");
				for (final IDescription arg : formalArgs) {
					serializeArg(desc, arg, sb, includingBuiltIn);
					sb.append(", ");
				}
				sb.setLength(sb.length() - 2);
				sb.append(")");
			} else {
				final Facets passedArgs = desc.getPassedArgs();
				if (passedArgs.isEmpty()) { return; }
				sb.append("(");
				passedArgs.forEachFacet((name, value) -> {
					if (Strings.isGamaNumber(name)) {
						sb.append(value.serialize(includingBuiltIn));
					} else {
						sb.append(name).append(":").append(value.serialize(includingBuiltIn));
					}
					sb.append(", ");
					return true;
				});
				sb.setLength(sb.length() - 2);
				sb.append(")");

			}
		}

		protected void serializeArg(final IDescription desc, final IDescription arg, final StringBuilder sb,
				final boolean includingBuiltIn) {
			// normally never called as it is redefined for action, do and
			// create
		}

	}

	public static final Set<String> uselessFacets =
			new HashSet<>(Arrays.asList(/* DEPENDS_ON, KEYWORD, */INTERNAL_FUNCTION, WITH));

	/**
	 * Method serialize()
	 *
	 * @see msi.gaml.descriptions.IDescriptionSerializer#serialize(msi.gaml.descriptions.IDescription)
	 */
	public final String serialize(final SymbolDescription symbolDescription, final boolean includingBuiltIn) {
		if (symbolDescription.isBuiltIn() && !includingBuiltIn) { return ""; }
		final StringBuilder sb = new StringBuilder();
		serialize(symbolDescription, sb, includingBuiltIn);
		return sb.toString();
	}

	public final void serializeNoRecursion(final StringBuilder sb, final IDescription symbolDescription,
			final boolean includingBuiltIn) {
		serializeKeyword((SymbolDescription) symbolDescription, sb, includingBuiltIn);
		serializeFacets((SymbolDescription) symbolDescription, sb, includingBuiltIn);
	}

	protected void serialize(final SymbolDescription symbolDescription, final StringBuilder sb,
			final boolean includingBuiltIn) {
		serializeKeyword(symbolDescription, sb, includingBuiltIn);
		serializeFacets(symbolDescription, sb, includingBuiltIn);
		serializeChildren(symbolDescription, sb, includingBuiltIn);
	}

	protected void serializeKeyword(final SymbolDescription symbolDescription, final StringBuilder sb,
			final boolean includingBuiltIn) {
		sb.append(symbolDescription.getKeyword()).append(' ');
	}

	protected void serializeChildren(final SymbolDescription symbolDescription, final StringBuilder sb,
			final boolean includingBuiltIn) {

		final StringBuilder childBuilder = new StringBuilder();
		symbolDescription.visitChildren(desc -> {
			serializeChild(desc, childBuilder, includingBuiltIn);
			return true;
		});
		if (childBuilder.length() == 0) {
			sb.append(';');
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

	protected void serializeFacets(final SymbolDescription symbolDescription, final StringBuilder sb,
			final boolean includingBuiltIn) {
		final String omit = DescriptionFactory.getOmissibleFacetForSymbol(symbolDescription.getKeyword());
		final String expr = serializeFacetValue(symbolDescription, omit, includingBuiltIn);
		if (expr != null) {
			sb.append(expr).append(" ");
		}
		symbolDescription.visitFacets((key, b) -> {

			if (key.equals(omit)) { return true; }
			final String expr1 = serializeFacetValue(symbolDescription, key, includingBuiltIn);
			if (expr1 != null) {
				sb.append(serializeFacetKey(symbolDescription, key, includingBuiltIn)).append(expr1).append(" ");
			}

			return true;
		});
	}

	protected String serializeFacetKey(final SymbolDescription symbolDescription, final String key,
			final boolean includingBuiltIn) {
		return key + ": ";
	}

	/**
	 * Return null to exclude a facet
	 *
	 * @param symbolDescription
	 * @param key
	 * @return
	 */
	protected String serializeFacetValue(final SymbolDescription symbolDescription, final String key,
			final boolean includingBuiltIn) {
		if (uselessFacets.contains(key)) { return null; }
		final IExpressionDescription ed = symbolDescription.getFacet(key);
		if (ed == null) { return null; }
		String exprString = ed.serialize(includingBuiltIn);
		if (exprString.startsWith(INTERNAL)) { return null; }
		if (ed instanceof LabelExpressionDescription) {
			final boolean isId = symbolDescription.getMeta().isId(key);
			if (!isId) {
				exprString = StringUtils.toGamlString(exprString);
			}
		}
		return exprString;

	}

	// protected void collectMetaInformation(final SymbolDescription desc, final GamlProperties plugins) {
	// collectMetaInformationInSymbol(desc, plugins);
	// collectMetaInformationInFacets(desc, plugins);
	// collectMetaInformationInChildren(desc, plugins);
	// desc.getGamlType().collectMetaInformation(plugins);
	// }

	// protected void collectMetaInformationInSymbol(final SymbolDescription desc, final GamlProperties plugins) {
	// plugins.put(GamlProperties.PLUGINS, desc.getDefiningPlugin());
	// plugins.put(GamlProperties.STATEMENTS, desc.getKeyword());
	// }

	/**
	 * @param desc
	 * @param plugins
	 */
	// protected void collectMetaInformationInFacets(final SymbolDescription desc, final GamlProperties plugins) {
	// desc.visitFacets((key, exp) -> {
	// collectMetaInformationInFacetValue(desc, key, plugins);
	// return true;
	// });
	// }

	/**
	 * @param desc
	 * @param key
	 * @param plugins
	 */
	// protected void collectMetaInformationInFacetValue(final SymbolDescription desc, final String key,
	// final GamlProperties plugins) {
	// final IExpressionDescription ed = desc.getFacet(key);
	// if (ed == null) { return; }
	// ed.collectMetaInformation(plugins);
	// }

	/**
	 * @param desc
	 * @param plugins
	 */
	// protected void collectMetaInformationInChildren(final SymbolDescription desc, final GamlProperties plugins) {
	// desc.visitChildren(s -> {
	// s.collectMetaInformation(plugins);
	// return true;
	// });
	//
	// }

}
