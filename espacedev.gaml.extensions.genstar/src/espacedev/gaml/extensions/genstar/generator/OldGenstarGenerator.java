/*******************************************************************************************************
 *
 * OldGenstarGenerator.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package espacedev.gaml.extensions.genstar.generator;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import core.configuration.GenstarConfigurationFile;
import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.value.IValue;
import core.util.random.GenstarRandom;
import espacedev.gaml.extensions.genstar.statement.GenerateStatement;
import espacedev.gaml.extensions.genstar.type.GamaPopGenerator;
import espacedev.gaml.extensions.genstar.utils.GenStarGamaUtils;
import gospl.GosplPopulation;
import gospl.algo.IGosplConcept.EGosplAlgorithm;
import gospl.algo.sr.ds.DirectSamplingAlgo;
import gospl.distribution.GosplInputDataManager;
import gospl.distribution.exception.IllegalControlTotalException;
import gospl.distribution.exception.IllegalDistributionCreation;
import gospl.distribution.matrix.INDimensionalMatrix;
import gospl.distribution.matrix.coordinate.ACoordinate;
import gospl.generator.DistributionBasedGenerator;
import gospl.io.exception.InvalidSurveyFormatException;
import gospl.sampler.ISampler;
import gospl.sampler.sr.GosplBasicSampler;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMapFactory;
import msi.gaml.statements.Arguments;
import msi.gaml.types.IType;
import msi.gaml.variables.IVariable;

/**
 * The Class OldGenstarGenerator.
 */
public class OldGenstarGenerator implements IGenstarGenerator {

	/** The Constant INSTANCE. */
	// SINGLETONG
	private static final OldGenstarGenerator INSTANCE = new OldGenstarGenerator();

	/**
	 * Gets the single instance of OldGenstarGenerator.
	 *
	 * @return single instance of OldGenstarGenerator
	 */
	public static OldGenstarGenerator getInstance() { return INSTANCE; }

	/**
	 * Instantiates a new old genstar generator.
	 */
	private OldGenstarGenerator() {
		this.type = null;
	}

	/** The type. */
	@SuppressWarnings ("rawtypes") IType type;

	@SuppressWarnings ("rawtypes")
	@Override
	public IType sourceType() {
		return this.type;
	}

	@Override
	public boolean sourceMatch(final IScope scope, final Object source) {
		return source instanceof GamaPopGenerator;
	}

	@SuppressWarnings ("unchecked")
	@Override
	public void generate(final IScope scope, final List<Map<String, Object>> inits, final Integer max,
			final Object source, final Object attributes, final Object algo, final Arguments init,
			final GenerateStatement generateStatement) {
		IAgent executor = scope.getAgent();
		msi.gama.metamodel.population.IPopulation<? extends IAgent> gamaPop =
				executor.getPopulationFor(generateStatement.getDescription().getSpeciesContext().getName());

		// Main object of the generation process
		GamaPopGenerator gen = (GamaPopGenerator) source;

		// Set Genstar random engine to be the one of Gama for seed purpose consistancy !
		GenstarRandom.setInstance(scope.getRandom().getGenerator());

		////////////////////////////////////////////////////////////////////////
		// Setup Gen* data
		////////////////////////////////////////////////////////////////////////

		GenstarConfigurationFile confFile = new GenstarConfigurationFile();
		confFile.setBaseDirectory(FileSystems.getDefault().getPath("."));
		confFile.setSurveyWrappers(gen.getInputFiles());
		confFile.setDictionary(gen.getInputAttributes());

		////////////////////////////////////////////////////////////////////////
		// Gospl generation
		////////////////////////////////////////////////////////////////////////

		// Create a basic empty Genstar population
		IPopulation<ADemoEntity, Attribute<? extends IValue>> population = new GosplPopulation();

		GosplInputDataManager gdb = new GosplInputDataManager(confFile);

		final EGosplAlgorithm gsAlgo =
				algo == null ? EGosplAlgorithm.DS : GenStarGamaUtils.toGosplAlgorithm(algo.toString());

		switch (gsAlgo.concept) {
			case CO:
				break;
			case MIXTURE:
				throw new UnsupportedOperationException(
						"Mixture population synthesis have not yet been ported from API to plugin ! "
								+ "request dev at https://github.com/ANRGenstar/genstar.gamaplugin ;)");
			case MULTILEVEL:
				throw new UnsupportedOperationException("I'll do it asap");
			case SR:
			default:
				// Build the n-dimensional matrix from raw data
				try {
					gdb.buildDataTables(); // Load and read input data
				} catch (final RuntimeException | IOException | InvalidSurveyFormatException
						| InvalidFormatException e) {
					throw GamaRuntimeException
							.error("Error in building dataTable for the IS algorithm. " + e.getMessage(), scope);
				}

				INDimensionalMatrix<Attribute<? extends IValue>, IValue, Double> distribution = null;
				try {
					distribution = gdb.collapseDataTablesIntoDistribution(); // Build a distribution from input data
				} catch (final IllegalDistributionCreation e1) {
					throw GamaRuntimeException
							.error("Error of distribution creation in collapsing DataTable into distibution. "
									+ e1.getMessage(), scope);
				} catch (final IllegalControlTotalException e1) {
					throw GamaRuntimeException.error(
							"Error of control in collapsing DataTable into distibution. " + e1.getMessage(), scope);
				}

				ISampler<ACoordinate<Attribute<? extends IValue>, IValue>> sampler = null;
				switch (gsAlgo) {
					case HS:
						break;
					case DS:
					default:
						try {
							sampler = new DirectSamplingAlgo().inferSRSampler(distribution, new GosplBasicSampler());
						} catch (final IllegalDistributionCreation e1) {
							throw GamaRuntimeException
									.error("Error of distribution creation in infering the sampler for " + gsAlgo.name
											+ " SR Based algorithm. " + e1.getMessage(), scope);
						}
						break;
				}
				population = new DistributionBasedGenerator(sampler)
						.generate(FileBasedGenerator.inferPopulationSize(max, gdb));
				break;
		}

		////////////////////////////////////////////////////////////////////////
		// Transpose Gen* entities to Gama species
		////////////////////////////////////////////////////////////////////////

		if (max > 0 && max < population.size()) { scope.getRandom().shuffleInPlace(population); }

		for (final ADemoEntity e : population) {
			@SuppressWarnings ("rawtypes") final Map map = GamaMapFactory.create();
			for (final Attribute<? extends IValue> attribute : population.getPopulationAttributes()) {
				IVariable var = gamaPop.getVar(attribute.getAttributeName());
				map.put(attribute.getAttributeName(),
						GenStarGamaUtils.toGAMAValue(scope, e.getValueForAttribute(attribute), true, var.getType()));
			}
			generateStatement.fillWithUserInit(scope, map);
			inits.add(map);
		}
	}

}
