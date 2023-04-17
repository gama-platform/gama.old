/*******************************************************************************************************
 *
 * GamlOutputConfigurationProvider.java, in msi.gama.lang.gaml, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.generator;

import static com.google.common.collect.Sets.newHashSet;
import java.util.Set;
import org.eclipse.xtext.generator.*;

/**
 * Class GamlOutputConfigurationProvider.
 *
 * @author drogoul
 * @since 4 janv. 2016
 *
 */
public class GamlOutputConfigurationProvider implements IOutputConfigurationProvider {

	/** The Constant META. */
	public final static String META = "METADATA_OUTPUT";

	/**
	 *
	 */
	public GamlOutputConfigurationProvider() {}

	/**
	 * Method getOutputConfigurations()
	 * @see org.eclipse.xtext.generator.IOutputConfigurationProvider#getOutputConfigurations()
	 */
	@Override
	public Set<OutputConfiguration> getOutputConfigurations() {
		OutputConfiguration defaultOutput = new OutputConfiguration(META);
		defaultOutput.setDescription("Metadata Folder");
		defaultOutput.setCanClearOutputDirectory(true);
		defaultOutput.setOutputDirectory("./.metadata");
		defaultOutput.setOverrideExistingResources(true);
		defaultOutput.setCreateOutputDirectory(true);
		defaultOutput.setCleanUpDerivedResources(true);
		defaultOutput.setSetDerivedProperty(true);
		defaultOutput.setKeepLocalHistory(true);
		return newHashSet(defaultOutput);
	}

}
