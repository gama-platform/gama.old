/*******************************************************************************************************
 *
 * GamlResourceInfoProvider.java, in msi.gama.lang.gaml, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.resource;

import static java.util.Arrays.asList;
import static msi.gama.lang.gaml.resource.GamlResourceServices.getResourceSet;
import static org.apache.commons.lang3.StringUtils.deleteWhitespace;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.uncapitalize;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.URI;

import com.google.inject.Singleton;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.lang.gaml.indexer.GamlResourceIndexer;
import msi.gama.util.file.GamlFileInfo;
import msi.gama.util.file.IGamlResourceInfoProvider;
import msi.gaml.compilation.ast.ISyntacticElement;
import msi.gaml.types.GamaFileType;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class GamlResourceInfoProvider.
 */
@Singleton
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamlResourceInfoProvider implements IGamlResourceInfoProvider {

	static {
		DEBUG.OFF();
	}

	/** The find tags. */
	public static Pattern findTags = Pattern.compile("Tags:\\s*(.*)");

	/** The find pragmes. */
	public static Pattern findPragmas = Pattern.compile("^\\s*@([^\\s@]+)");

	/** The find strings. */
	public static Pattern findStrings = Pattern.compile("('([^']*)')|(\"([^\"]*)\")");

	/** The find experiments. */
	public static Pattern findExperiments = Pattern.compile(
			"^\\s*experiment\\s+(?:'([^']*)'|\"([^\"]*)\"|(\\w+))(?:\\s+type:\\s*(\\w+))?(?:\\s+virtual:\\s*(\\w+))?");

	/** The instance. */
	public static GamlResourceInfoProvider INSTANCE = new GamlResourceInfoProvider();

	/**
	 * Gets the info.
	 *
	 * @param originalURI
	 *            the original URI
	 * @param r
	 *            the r
	 * @param stamp
	 *            the stamp
	 * @return the info
	 */
	@Override
	public GamlFileInfo getInfo(final URI originalURI, final long stamp) {

		Set<String> imports = null;
		Set<String> tags = null;
		Set<String> uses = null;
		Set<String> exps = null;

		for (final URI u : GamlResourceIndexer.directImportsOf(originalURI)) {
			if (imports == null) { imports = new LinkedHashSet(); }
			imports.add(u.deresolve(originalURI).toString());
		}

		try (InputStreamReader isr =
				new InputStreamReader(getResourceSet().getURIConverter().createInputStream(originalURI));
				BufferedReader reader = new BufferedReader(isr)) {

			String input;
			boolean processExperiments = true;
			while ((input = reader.readLine()) != null) {
				Matcher tagsMatcher = findTags.matcher(input);
				if (tagsMatcher.find()) {
					String tagList = tagsMatcher.group(1);
					tags = new HashSet(asList(split(uncapitalize(deleteWhitespace(tagList)), ',')));
				}
				Matcher pragmasMatcher = findPragmas.matcher(input);
				if (pragmasMatcher.find()) {
					String pragma = pragmasMatcher.group(1);
					// DEBUG.OUT("Pragma found = " + pragma);
					if (IKeyword.PRAGMA_NO_EXPERIMENT.equals(pragma)) { processExperiments = false; }
				}
				Matcher stringsMatcher = findStrings.matcher(input);
				while (stringsMatcher.find()) {
					String s = stringsMatcher.group();
					// DEBUG.OUT("Strings found = " + s);
					if (s.length() > 6) {
						s = s.substring(1, s.length() - 1);
						final URI u = URI.createFileURI(s);
						final String ext = u.fileExtension();
						if (ext != null && !ext.isBlank() && !"gaml".equals(ext)
								&& GamaFileType.managesExtension(ext)) {
							if (uses == null) { uses = new LinkedHashSet(); }
							DEBUG.OUT("===== Considers in uses : " + s);
							uses.add(s);
						}
					}
				}
				if (processExperiments) {
					Matcher experimentsMatcher = findExperiments.matcher(input);
					if (experimentsMatcher.find()) {
						String name = experimentsMatcher.group(1); // single quotes
						if (name == null) {
							name = experimentsMatcher.group(2); // double quotes
						}
						if (name == null) {
							name = experimentsMatcher.group(3); // no quotes
						}
						String type = experimentsMatcher.group(4);
						String virtual = experimentsMatcher.group(5);
						if (name != null && !IKeyword.TRUE.equals(virtual)) {
							// DEBUG.OUT("Experiment " + name + " type " + type + " virtual " + virtual);
							if (exps == null) { exps = new LinkedHashSet(); }
							if (IKeyword.BATCH.equals(type)) { name = GamlFileInfo.BATCH_PREFIX + name; }
							exps.add(name);
						}
					}
				}
			}

		} catch (final IOException e1) {
			e1.printStackTrace();
		}

		return new GamlFileInfo(stamp, imports, uses, exps, tags);

	}

	@Override
	public ISyntacticElement getContents(final URI uri) {
		return GamlResourceServices.getOrCreateSyntacticContents(uri);
	}

}
