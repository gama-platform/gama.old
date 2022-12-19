/*******************************************************************************************************
 *
 * FileBasedGenerator.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import core.configuration.GenstarConfigurationFile;
import core.configuration.dictionary.AttributeDictionary;
import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.attribute.AttributeFactory;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.io.GSSurveyWrapper;
import core.metamodel.value.IValue;
import core.metamodel.value.numeric.RangeSpace;
import core.util.data.GSEnumDataType;
import core.util.exception.GSIllegalRangedData;
import core.util.random.GenstarRandom;
import espacedev.gaml.extensions.genstar.statement.GenerateStatement;
import espacedev.gaml.extensions.genstar.utils.GenStarGamaUtils;
import gospl.GosplPopulation;
import gospl.algo.IGosplConcept.EGosplAlgorithm;
import gospl.algo.sr.ds.DirectSamplingAlgo;
import gospl.distribution.GosplContingencyTable;
import gospl.distribution.GosplInputDataManager;
import gospl.distribution.exception.IllegalControlTotalException;
import gospl.distribution.exception.IllegalDistributionCreation;
import gospl.distribution.matrix.INDimensionalMatrix;
import gospl.distribution.matrix.control.AControl;
import gospl.distribution.matrix.coordinate.ACoordinate;
import gospl.generator.DistributionBasedGenerator;
import gospl.io.exception.InvalidSurveyFormatException;
import gospl.io.util.ReadDictionaryUtils;
import gospl.sampler.ISampler;
import gospl.sampler.sr.GosplBasicSampler;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gama.util.file.GamaCSVFile;
import msi.gaml.statements.Arguments;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import msi.gaml.variables.IVariable;

/**
 *
 * Genstar translation of Gama Delegate
 *
 * @author kevinchapuis
 *
 */
public class FileBasedGenerator implements IGenstarGenerator {

	/** The Constant INSTANCE. */
	// SINGLETONG
	private static final FileBasedGenerator INSTANCE = new FileBasedGenerator();

	/**
	 * Gets the single instance of FileBasedGenerator.
	 *
	 * @return single instance of FileBasedGenerator
	 */
	public static FileBasedGenerator getInstance() { return INSTANCE; }

	/** The type. */
	@SuppressWarnings ("rawtypes") final IType type;

	/**
	 * Instantiates a new file based generator.
	 */
	@SuppressWarnings ("unchecked")
	private FileBasedGenerator() {
		this.type = Types.LIST.of(Types.FILE);
	}

	@SuppressWarnings ("rawtypes")
	@Override
	public IType sourceType() {
		return type;
	}

	@SuppressWarnings ({ "rawtypes", "unchecked" })
	@Override
	public boolean sourceMatch(final IScope scope, final Object source) {
		return source instanceof IList && ((IList) source).stream(scope).allMatch(csv -> csv instanceof GamaCSVFile);
	}

	@SuppressWarnings ("unchecked")
	@Override
	public void generate(final IScope scope, final List<Map<String, Object>> inits, final Integer max,
			final Object source, final Object attributes, final Object algo, final Arguments init,
			final GenerateStatement generateStatement) {
		IAgent executor = scope.getAgent();
		msi.gama.metamodel.population.IPopulation<? extends IAgent> gamaPop =
				executor.getPopulationFor(generateStatement.getDescription().getSpeciesContext().getName());

		// --------
		// 1. Infer the type of data for each attributes (based on gaml type and values given)
		// -------
		IMap<String, IList<String>> atts = (IMap<String, IList<String>>) attributes;
		List<Attribute<? extends IValue>> gsAttributes = new ArrayList<>();
		for (String a : atts.getKeys()) {

			// Trying to infer the type of data given
			@SuppressWarnings ("rawtypes") IType gamaT = gamaPop.getVar(a).getType();
			GSEnumDataType gsT = ReadDictionaryUtils.detectIsRange(atts.get(a)) ? GSEnumDataType.Range
					: GenStarGamaUtils.toDataType(gamaT, false);

			Attribute<? extends IValue> newAttribute = null;
			try {
				newAttribute = AttributeFactory.getFactory().createAttribute(a, gsT, atts.get(a));
			} catch (GSIllegalRangedData e) {
				e.printStackTrace();
			}
			if (newAttribute != null) { gsAttributes.add(newAttribute); }
		}

		// --------
		// 1. Infer the type of data in files - contingency, frequency or sample
		// --------
		IList<GamaCSVFile> fileSources = (IList<GamaCSVFile>) source;
		List<GSSurveyWrapper> gsSurveys =
				fileSources.stream(scope).map(s -> GenStarGamaUtils.toSurveyWrapper(scope, s, gsAttributes)).toList();

		// Set Genstar random engine to be the one of Gama for seed purpose consistancy !
		GenstarRandom.setInstance(scope.getRandom().getGenerator());

		////////////////////////////////////////////////////////////////////////
		// Setup Gen* data
		////////////////////////////////////////////////////////////////////////

		// Create a basic empty Genstar population
		IPopulation<ADemoEntity, Attribute<? extends IValue>> population = new GosplPopulation();

		// retrieve all the required configuration to build a Genstar configuration
		Path baseDirectory = FileSystems.getDefault().getPath(".");

		GenstarConfigurationFile confFile = new GenstarConfigurationFile();
		confFile.setBaseDirectory(baseDirectory);
		confFile.setSurveyWrappers(gsSurveys);
		confFile.setDictionary(new AttributeDictionary(gsAttributes));

		////////////////////////////////////////////////////////////////////////
		// Gospl generation
		////////////////////////////////////////////////////////////////////////

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
				INDimensionalMatrix<Attribute<? extends IValue>, IValue, Double> distribution =
						manageRawData(scope, gdb);
				ISampler<ACoordinate<Attribute<? extends IValue>, IValue>> sampler = null;
				for (final Attribute<? extends IValue> attribute : gsAttributes) {
					if (attribute.getValueSpace() instanceof RangeSpace) {
						((RangeSpace) attribute.getValueSpace()).consolidateRanges();
					}
				}
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
				population = new DistributionBasedGenerator(sampler).generate(inferPopulationSize(max, gdb));
				break;
		}

		////////////////////////////////////////////////////////////////////////
		// Transpose Gen* entities to Gama species
		////////////////////////////////////////////////////////////////////////

		if (max > 0 && max < population.size()) { scope.getRandom().shuffleInPlace(population); }

		for (final ADemoEntity e : population) {
			@SuppressWarnings ("rawtypes") final Map map = GamaMapFactory.create();
			for (final Attribute<? extends IValue> attribute : gsAttributes) {
				IVariable variable = gamaPop.getVar(attribute.getAttributeName());
				map.put(attribute.getAttributeName(),
						GenStarGamaUtils.toGAMAValue(scope, e.getValueForAttribute(attribute), true, variable.getType()));

			}
			generateStatement.fillWithUserInit(scope, map);
			inits.add(map);
		}

	}

	// -------------------------------------------------------------------- //

	// SR Utils

	/**
	 * Construct a n-dimensional matrix based on raw data
	 *
	 * @param scope
	 * @param gdb
	 * @return
	 */
	public static INDimensionalMatrix<Attribute<? extends IValue>, IValue, Double> manageRawData(final IScope scope,
			final GosplInputDataManager gdb) {
		try {
			gdb.buildDataTables(); // Load and read input data
		} catch (final RuntimeException | InvalidFormatException | IOException | InvalidSurveyFormatException e) {
			throw GamaRuntimeException.error("Error in building dataTable for the IS algorithm. " + e.getMessage(),
					scope);
		}

		INDimensionalMatrix<Attribute<? extends IValue>, IValue, Double> distribution = null;
		try {
			distribution = gdb.collapseDataTablesIntoDistribution(); // Build a distribution from input data
		} catch (final IllegalDistributionCreation e1) {
			throw GamaRuntimeException.error(
					"Error of distribution creation in collapsing DataTable into distibution. " + e1.getMessage(),
					scope);
		} catch (final IllegalControlTotalException e1) {
			throw GamaRuntimeException
					.error("Error of control in collapsing DataTable into distibution. " + e1.getMessage(), scope);
		}
		return distribution;
	}

	/**
	 * Try to find a good fit in the data to decide the proper number of synthetic population size, i.e. in the case
	 * there is contingencies
	 *
	 * @param requestedSize
	 * @param gdb
	 * @return
	 */
	public static int inferPopulationSize(int requestedSize, final GosplInputDataManager gdb) {
		// DEFINE THE POPULATION SIZE
		if (requestedSize < 0) {
			int min = Integer.MAX_VALUE;
			for (INDimensionalMatrix<Attribute<? extends IValue>, IValue, ? extends Number> mat : gdb
					.getRawDataTables()) {
				if (mat instanceof GosplContingencyTable cmat) {
					min = Math.min(min, cmat.getMatrix().values().stream().mapToInt(AControl::getValue).sum());
				}
			}
			if (min < Integer.MAX_VALUE) {
				requestedSize = min;
			} else {
				requestedSize = 1;
			}
		}
		return requestedSize <= 0 ? 1 : requestedSize;
	}

}
