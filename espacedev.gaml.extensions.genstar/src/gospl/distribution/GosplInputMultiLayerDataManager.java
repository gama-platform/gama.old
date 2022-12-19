/*******************************************************************************************************
 *
 * GosplInputMultiLayerDataManager.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gospl.distribution;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import core.configuration.GenstarConfigurationFile;
import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.io.GSSurveyType;
import core.metamodel.io.GSSurveyWrapper;
import core.metamodel.value.IValue;
import core.util.GSPerformanceUtil;
import gospl.GosplPopulation;
import gospl.distribution.exception.IllegalControlTotalException;
import gospl.distribution.exception.IllegalDistributionCreation;
import gospl.distribution.matrix.AFullNDimensionalMatrix;
import gospl.distribution.matrix.INDimensionalMatrix;
import gospl.io.GosplSurveyFactory;
import gospl.io.exception.InvalidSurveyFormatException;

/**
 * Manage multi-layer input data. Each population level should be associated to one manager
 * {@link GosplInputDataManager} that will be responsible for level related sample and table data management.
 * <p>
 * TODO : make a unique input data manager for multi layer and mono layer data
 *
 * @author kevinchapuis
 *
 */
public class GosplInputMultiLayerDataManager extends GosplInputDataManager {

	/** The configuration. */
	private GenstarConfigurationFile configuration;

	/** The data tables. */
	Map<Integer, Set<AFullNDimensionalMatrix<? extends Number>>> dataTables;
	
	/** The samples. */
	Map<Integer, GosplPopulation> samples;

	/**
	 * Instantiates a new gospl input multi layer data manager.
	 *
	 * @param configurationFile the configuration file
	 */
	public GosplInputMultiLayerDataManager(final GenstarConfigurationFile configurationFile) {
		super(configurationFile);
		if (configurationFile.getLevel() <= 1)
			throw new IllegalArgumentException("Given configuration file should at least contains two level [actual ="
					+ super.getConfiguration().getLevel() + "] of input data");
	}

	/**
	 * Instantiates a new gospl input multi layer data manager.
	 *
	 * @param configurationFilePath the configuration file path
	 * @throws IllegalArgumentException the illegal argument exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public GosplInputMultiLayerDataManager(final Path configurationFilePath)
			throws IllegalArgumentException, IOException {
		super(configurationFilePath);
		if (super.getConfiguration().getLevel() <= 1)
			throw new IllegalArgumentException("Given configuration file should at least contains two level [actual = "
					+ super.getConfiguration().getLevel() + "] of input data");
	}

	// --------------------- Generic contract -------------------- //

	@Override
	public void buildDataTables() throws InvalidFormatException, IOException, InvalidSurveyFormatException {
		GosplSurveyFactory sf = new GosplSurveyFactory();
		this.dataTables = new HashMap<>();
		for (int layer : this.configuration.getLayers()) {
			for (GSSurveyWrapper wrapper : this.configuration.getSurveyWrappers(layer)) {
				if (!GSSurveyType.Sample.equals(wrapper.getSurveyType())) {
					this.dataTables.put(layer,
							getDataTables(sf.getSurvey(wrapper, this.configuration.getBaseDirectory()),
									this.configuration.getDictionary()));
				}
			}
		}

	}

	@Override
	public void buildSamples() throws InvalidFormatException, IOException, InvalidSurveyFormatException {
		GosplSurveyFactory sf = new GosplSurveyFactory();
		this.samples = new HashMap<>();
		for (int layer : this.configuration.getLayers()) {
			for (final GSSurveyWrapper wrapper : this.configuration.getSurveyWrappers(layer))
				if (GSSurveyType.Sample.equals(wrapper.getSurveyType())) {
					this.samples.put(layer, getSample(sf.getSurvey(wrapper, this.configuration.getBaseDirectory()),
							this.configuration.getDictionary(), null, Collections.emptyMap()));
				}
		}
	}

	/**
	 * @throws IllegalControlTotalException
	 * @throws IllegalDistributionCreation
	 *
	 */
	@Override
	public INDimensionalMatrix<Attribute<? extends IValue>, IValue, Double> collapseDataTablesIntoDistribution()
			throws IllegalControlTotalException, IllegalDistributionCreation {
		return this.collapseDataTablesIntoDistribution(0);
	}

	/**
	 * Collapse data tables into distribution.
	 *
	 * @param level the level
	 * @return the IN dimensional matrix< attribute<? extends I value>, I value, double>
	 * @throws IllegalControlTotalException the illegal control total exception
	 * @throws IllegalDistributionCreation the illegal distribution creation
	 */
	@SuppressWarnings ("unlikely-arg-type")
	public INDimensionalMatrix<Attribute<? extends IValue>, IValue, Double> collapseDataTablesIntoDistribution(
			final int level) throws IllegalControlTotalException, IllegalDistributionCreation {
		if (dataTables == null || dataTables.isEmpty()) throw new IllegalArgumentException(
				"To collapse matrices you must build at least one first: " + "see the buildDataTables method");

		if (dataTables.get(level).size() == 1)
			return getFrequency(dataTables.get(level).iterator().next(), Collections.emptySet());

		Set<AFullNDimensionalMatrix<? extends Number>> levelData = dataTables.get(level);

		final Set<AFullNDimensionalMatrix<Double>> fullMatrices = new HashSet<>();

		GSPerformanceUtil gspu = new GSPerformanceUtil("Proceed to distribution collapse");
		gspu.sysoStempPerformance(0, this);

		// Matrices that contain a record attribute
		for (AFullNDimensionalMatrix<? extends Number> recordMatrices : levelData.stream()
				.filter(mat -> mat.getDimensions().stream()
						.anyMatch(d -> this.configuration.getDictionary().getRecords().contains(d)))
				.collect(Collectors.toSet())) {
			fullMatrices.add(getTransposedRecord(this.getConfiguration().getDictionary(), recordMatrices));
		}

		gspu.sysoStempPerformance(1, this);
		gspu.sysoStempMessage("Collapse record attribute: done");

		// Matrices that do not contain any record attribute
		for (final AFullNDimensionalMatrix<? extends Number> mat : levelData.stream()
				.filter(mat -> mat.getDimensions().stream()
						.allMatch(d -> !isRecordAttribute(this.getConfiguration().getDictionary(), d)))
				.collect(Collectors.toSet())) {
			fullMatrices.add(getFrequency(mat, levelData));
		}

		gspu.sysoStempPerformance(2, this);
		gspu.sysoStempMessage("Transpose to frequency: done");

		return new GosplConditionalDistribution(fullMatrices);
	}

	/**
	 * Returns an unmodifiable view of input data tables, as a raw set of matrices
	 *
	 * @return
	 */
	@Override
	public Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, ? extends Number>> getRawDataTables() {
		return this.getRawDataTables(0);
	}

	/**
	 *
	 * @param level
	 * @return
	 */
	public Set<INDimensionalMatrix<Attribute<? extends IValue>, IValue, ? extends Number>>
			getRawDataTables(final int level) {
		return Collections.unmodifiableSet(this.dataTables.get(level));
	}

	/**
	 * Returns an unmodifiable view of input contingency tables. If there is not any contingency data in input tables,
	 * then return an empty set
	 *
	 * @return
	 */
	@Override
	public Set<AFullNDimensionalMatrix<Integer>> getContingencyTables() { return this.getContingencyTables(0); }

	/**
	 *
	 * @param level
	 * @return
	 */
	@SuppressWarnings ("unchecked")
	private Set<AFullNDimensionalMatrix<Integer>> getContingencyTables(final int level) {
		return this.dataTables.get(level).stream()
				.filter(matrix -> GSSurveyType.ContingencyTable.equals(matrix.getMetaDataType()))
				.map(matrix -> (AFullNDimensionalMatrix<Integer>) matrix).collect(Collectors.toSet());
	}

	/**
	 * Returns an unmodifiable view of input samples
	 *
	 * @return a set of {@link IPopulation}
	 */
	@Override
	public Set<IPopulation<ADemoEntity, Attribute<? extends IValue>>> getRawSamples() {
		return new HashSet<>(this.samples.values());
	}

	/**
	 * Access to raw sample in memory
	 *
	 * @param level
	 * @return {@link IPopulation} of layer {@code level}
	 */
	public IPopulation<ADemoEntity, Attribute<? extends IValue>> getRawSample(final int level) {
		return this.samples.get(level);
	}
}
