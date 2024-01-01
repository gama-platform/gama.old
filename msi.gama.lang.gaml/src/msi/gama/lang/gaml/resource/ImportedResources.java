package msi.gama.lang.gaml.resource;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;
import static java.util.Collections.singleton;
import static msi.gaml.compilation.GAML.getModelFactory;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import msi.gaml.compilation.ast.ISyntacticElement;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.ValidationContext;

/**
 * The Class ImportedResources.
 */
public class ImportedResources {
	/** The micromodels. */
	public ListMultimap<String, GamlResource> micromodels;
	/** The imports. */
	public Set<GamlResource> imports;

	/**
	 * Adds the.
	 *
	 * @param alias
	 *            the alias
	 * @param resource
	 *            the resource
	 */
	public void add(final String alias, final GamlResource resource) {
		if (alias == null) {
			addOwnImport(resource);
		} else {
			addMicroModel(alias, resource);
		}
	}

	/**
	 * Adds the micro model.
	 *
	 * @param alias
	 *            the alias
	 * @param resource
	 *            the resource
	 */
	private void addMicroModel(final String alias, final GamlResource resource) {
		if (micromodels == null) { micromodels = ArrayListMultimap.create(); }
		micromodels.put(alias, resource);
	}

	/**
	 * Adds the own import.
	 *
	 * @param resource
	 *            the resource
	 */
	private void addOwnImport(final GamlResource resource) {
		if (imports == null) { imports = Sets.newLinkedHashSet(); }
		imports.add(resource);
	}

	/**
	 * Compute direct imports.
	 *
	 * @param syntacticContents
	 *            the syntactic contents
	 * @return the iterable
	 */
	public Iterable<ISyntacticElement> computeDirectImports(final ISyntacticElement syntacticContents) {
		return imports == null ? singleton(syntacticContents)
				: concat(singleton(syntacticContents), transform(imports, GamlResource.TO_SYNTACTIC_CONTENTS));
	}

	/**
	 * Compute micro models.
	 *
	 * @return the map
	 */
	public Map<String, ModelDescription> computeMicroModels(final String project, final String model,
			final ValidationContext context) {
		if (micromodels == null) return null;
		Map<String, ModelDescription> result = Maps.newHashMap();

		for (final String aliasName : micromodels.keySet()) {
			final ModelDescription mic = getModelFactory().createModelDescription(project, model,
					transform(micromodels.get(aliasName), GamlResource.TO_SYNTACTIC_CONTENTS), context, null);
			mic.setAlias(aliasName);
			result.put(aliasName, mic);
		}
		return result;
	}
}