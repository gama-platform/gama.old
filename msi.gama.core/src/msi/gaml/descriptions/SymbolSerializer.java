/**
 * Created by drogoul, 10 nov. 2014
 * 
 */
package msi.gaml.descriptions;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
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

	public static class VarSerializer extends SymbolSerializer<VariableDescription> {

		@Override
		protected void serializeKeyword(final VariableDescription desc, final StringBuilder sb) {
			String k = desc.getFacets().getLabel(IKeyword.KEYWORD);
			if ( !k.equals(PARAMETER) && !k.equals(SIGNAL) ) {
				String type = desc.getType().toGaml();
				if ( !type.equals(UNKNOWN) ) {
					k = type;
				}
			}
			sb.append(k).append(' ');
		}

		@Override
		protected String serializeFacetValue(final VariableDescription s, final String key) {
			if ( key.equals(TYPE) || key.equals(OF) || key.equals(INDEX) ) { return null; }
			return super.serializeFacetValue(s, key);
		}

		@Override
		protected String serializeFacetKey(final VariableDescription s, final String key) {
			if ( key.equals(INIT) ) { return "<- "; }
			return super.serializeFacetKey(s, key);
		}
	}

	public static class SpeciesSerializer extends SymbolSerializer<SpeciesDescription> {

		@Override
		protected String serializeFacetValue(final SpeciesDescription s, final String key) {
			if ( key.equals(SKILLS) ) {
				IExpressionDescription ed = s.getFacets().get(key);
				if ( ed == null ) { return null; }
				Set<String> strings = ed.getStrings(s, true);
				return strings.toString();
			}
			return super.serializeFacetValue(s, key);
		}

	}

	public static class ModelSerializer extends SpeciesSerializer {

		@Override
		protected void serializeKeyword(final SpeciesDescription desc, final StringBuilder sb) {
			sb.append("model ").append(desc.getName().replace("_model", "")).append(Strings.LN).append(Strings.LN);
			sb.append("global ");
		}

		@Override
		protected void serializeChildren(final SpeciesDescription desc, final StringBuilder sb) {
			sb.append(' ').append('{').append(Strings.LN);
			Collection<? extends IDescription> children = desc.getVariables().values();
			sb.append(Strings.LN);
			sb.append("// Global attributes of ").append(desc.getName()).append(Strings.LN);
			for ( IDescription s : children ) {
				serializeChild(s, sb);
			}
			children = desc.getActions();
			sb.append(Strings.LN);
			sb.append("// Global actions of ").append(desc.getName()).append(Strings.LN);
			for ( IDescription s : children ) {
				serializeChild(s, sb);
			}
			children = desc.getBehaviors();
			sb.append(Strings.LN);
			sb.append("// Behaviors of ").append(desc.getName()).append(Strings.LN);
			for ( IDescription s : children ) {
				serializeChild(s, sb);
			}
			children = desc.getAspects();
			sb.append(Strings.LN);
			sb.append("// Aspects of ").append(desc.getName()).append(Strings.LN);
			for ( IDescription s : children ) {
				serializeChild(s, sb);
			}
			sb.append('}').append(Strings.LN);

			children = desc.getMicroSpecies().values();
			for ( IDescription s : children ) {
				sb.append(Strings.LN);
				serializeChild(s, sb);
			}

			children = ((ModelDescription) desc).getExperiments();
			for ( IDescription s : children ) {
				sb.append(Strings.LN);
				serializeChild(s, sb);
			}
		}

		@Override
		protected String serializeFacetValue(final SpeciesDescription s, final String key) {
			if ( key.equals(NAME) ) { return null; }
			return super.serializeFacetValue(s, key);
		}

	}

	public static class ExperimentSerializer extends SymbolSerializer<ExperimentDescription> {

	}

	public static class StatementSerializer extends SymbolSerializer<StatementDescription> {

		@Override
		protected void serializeFacets(final StatementDescription s, final StringBuilder sb) {
			super.serializeFacets(s, sb);
			serializeArgs(s, sb);

		}

		protected void serializeArgs(final StatementDescription desc, final StringBuilder sb) {
			if ( desc.args == null || desc.args.isEmpty() ) { return; }
			sb.append("(");
			for ( StatementDescription arg : desc.args.values() ) {
				serializeArg(desc, arg, sb);
				sb.append(", ");
			}
			sb.setLength(sb.length() - 2);
			sb.append(")");
		}

		protected void serializeArg(final StatementDescription desc, final StatementDescription arg,
			final StringBuilder sb) {
			// normally never called as it is redefined for action, do and create
		}

	}

	protected static final Set<String> uselessFacets = new HashSet(Arrays.asList(DEPENDS_ON, KEYWORD,
		INTERNAL_FUNCTION, WITH));

	/**
	 * Method serialize()
	 * @see msi.gaml.descriptions.IDescriptionSerializer#serialize(msi.gaml.descriptions.IDescription)
	 */
	public final String serialize(final C description) {
		if ( description.isBuiltIn() ) { return ""; }
		StringBuilder sb = new StringBuilder();
		serialize(description, sb);
		return sb.toString();
	}

	protected void serialize(final C desc, final StringBuilder sb) {
		serializeKeyword(desc, sb);
		serializeFacets(desc, sb);
		serializeChildren(desc, sb);
	}

	protected void serializeKeyword(final C desc, final StringBuilder sb) {
		sb.append(desc.getKeyword()).append(' ');
	}

	protected void serializeChildren(final C desc, final StringBuilder sb) {
		List<IDescription> children = desc.getChildren();
		if ( children.isEmpty() ) {
			sb.append(";");
			return;
		}
		sb.append(' ').append('{').append(Strings.LN);
		for ( IDescription s : children ) {
			serializeChild(s, sb);
		}
		sb.append('}').append(Strings.LN);

	}

	protected void serializeChild(final IDescription s, final StringBuilder sb) {
		String gaml = s.toGaml();
		if ( gaml != null && gaml.length() > 0 ) {
			sb.append(Strings.indent(s.toGaml(), 1)).append(Strings.LN);
		}
	}

	protected void serializeFacets(final C s, final StringBuilder sb) {
		String omit = DescriptionFactory.getOmissibleFacetForSymbol(s.getKeyword());
		String expr = serializeFacetValue(s, omit);
		if ( expr != null ) {
			sb.append(expr).append(" ");
		}
		for ( final String key : s.getFacets().keySet() ) {
			if ( key.equals(omit) ) {
				continue;
			}
			expr = serializeFacetValue(s, key);
			if ( expr != null ) {
				sb.append(serializeFacetKey(s, key)).append(expr).append(" ");
			}
		}
	}

	protected String serializeFacetKey(final C s, final String key) {
		return key + ": ";
	}

	/**
	 * Return null to exclude a facet
	 * @param s
	 * @param key
	 * @return
	 */
	protected String serializeFacetValue(final C s, final String key) {
		if ( uselessFacets.contains(key) ) { return null; }
		IExpressionDescription ed = s.getFacets().get(key);
		if ( ed == null ) { return null; }
		String exprString = ed.toGaml();
		// if ( ed.isConstant() && ed.getExpression().getType().id() == IType.STRING ) {
		// if ( s.getMeta().getPossibleFacets().get(key).types[0] != IType.LABEL ) {
		// exprString = StringUtils.toJavaString(exprString);
		// }
		// }
		if ( exprString.startsWith(INTERNAL) ) { return null; }
		if ( ed instanceof LabelExpressionDescription ) {

			// boolean isLabel = s.getMeta().isLabel(key);
			boolean isId = s.getMeta().isId(key);
			if ( !isId ) {
				exprString = StringUtils.toGamlString(exprString);
			}
		}
		return exprString;

	}
}
