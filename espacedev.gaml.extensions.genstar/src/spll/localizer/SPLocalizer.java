/*******************************************************************************************************
 *
 * SPLocalizer.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package spll.localizer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.geotools.feature.SchemaException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.TransformException;

import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.attribute.AttributeFactory;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.entity.AGeoEntity;
import core.metamodel.io.IGSGeofile;
import core.metamodel.io.IGSGeofile.GeoGSFileType;
import core.metamodel.value.IValue;
import core.metamodel.value.numeric.IntegerValue;
import core.util.GSPerformanceUtil;
import core.util.exception.GenstarException;
import core.util.random.GenstarRandom;
import spll.SpllEntity;
import spll.SpllPopulation;
import spll.algo.LMRegressionOLS;
import spll.algo.exception.IllegalRegressionException;
import spll.datamapper.ASPLMapperBuilder;
import spll.datamapper.SPLAreaMapperBuilder;
import spll.datamapper.exception.GSMapperException;
import spll.datamapper.normalizer.SPLUniformNormalizer;
import spll.datamapper.variable.ISPLVariable;
import spll.entity.GeoEntityFactory;
import spll.entity.SpllFeature;
import spll.entity.SpllPixel;
import spll.io.SPLGeofileBuilder;
import spll.io.SPLGeofileBuilder.SPLGisFileExtension;
import spll.io.SPLRasterFile;
import spll.io.SPLVectorFile;
import spll.io.exception.InvalidGeoFormatException;
import spll.localizer.constraint.ISpatialConstraint;
import spll.localizer.constraint.SpatialConstraintLocalization;
import spll.localizer.distribution.ISpatialDistribution;
import spll.localizer.distribution.SpatialDistributionFactory;
import spll.localizer.linker.ISPLinker;
import spll.localizer.linker.SPLinker;
import spll.localizer.pointInalgo.PointInLocalizer;
import spll.localizer.pointInalgo.RandomPointInLocalizer;
import spll.util.SpllUtil;

/**
 * The Class SPLocalizer.
 */
public class SPLocalizer implements ISPLocalizer {

	/** The gspu. */
	/*
	 * Performance purpose logger
	 */
	protected GSPerformanceUtil gspu;

	/** The population. */
	protected IPopulation<ADemoEntity, Attribute<? extends IValue>> population;

	/** The match. */
	// main referenced area for placing the agents (e.g. Iris)
	protected IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue> match;

	/** The map. */
	// gives the number of entities per area (e.g. regression cells)
	protected IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue> map;

	/** The linker. */
	protected ISPLinker<ADemoEntity> linker; // Encapsulate spatial distribution and constraints to link entity and

	/** The localization constraint. */
	// spatial object
	protected SpatialConstraintLocalization localizationConstraint; // the localization constraint;

	/** The point in localizer. */
	protected PointInLocalizer pointInLocalizer; // allows to return one or several points in a geometry

	/** The key att map. */
	protected String keyAttMap; // name of the attribute that contains the number of entities in the map file

	/** The key att pop. */
	protected String keyAttPop; // name of the attribute that is used to store the id of the referenced area in the

	/** The key att match. */
	// population
	protected String keyAttMatch; // name of the attribute that is used to store the id of the referenced area in the
									// match file

	/** The rand. */
	protected Random rand;

	/**
	 * Private constructor to setup random engine
	 */
	private SPLocalizer() {
		this.rand = GenstarRandom.getInstance();
		this.pointInLocalizer = new RandomPointInLocalizer(rand);
		this.linker = new SPLinker<>(SpatialDistributionFactory.getInstance().getUniformDistribution());
	}

	/**
	 * Build a localizer based on a geographically grounded population
	 *
	 * @param population
	 */
	public SPLocalizer(final IPopulation<ADemoEntity, Attribute<? extends IValue>> population,
			final IGSGeofile<? extends AGeoEntity<? extends IValue>, IValue> geoFile) {
		this();
		this.population = population;
		this.localizationConstraint = new SpatialConstraintLocalization(null);
		this.localizationConstraint.setReferenceFile(geoFile);
		// this.linker.addConstraints(localizationConstraint);
	}

	///////////////////////////////////////////////////////////
	// ------------------- MAIN CONTRACT ------------------- //
	///////////////////////////////////////////////////////////

	@Override
	public SpllPopulation localisePopulation() {
		SpllPopulation outputPopulation = new SpllPopulation(population, localizationConstraint.getReferenceFile());
		try {
			// case where the referenced file is not defined
			if (match == null) {
				List<SpllEntity> entities = new ArrayList<>(outputPopulation);

				// case where there is no information about the number of entities in specific spatial areas
				if (keyAttMap == null || map == null) {
					localizationInNest(entities, null);
				}
				// case where we have information about the number of entities per specific areas (entityNbAreas)
				else {
					localizationInNestWithNumbers(entities, null);
				}
			}
			// case where the referenced file is defined
			else {
				for (AGeoEntity<? extends IValue> globalfeature : match.getGeoEntity()) {
					String valKeyAtt = globalfeature.getValueForAttribute(keyAttMatch).getStringValue();

					List<SpllEntity> entities = outputPopulation.stream()
							.filter(s -> s.getValueForAttribute(keyAttPop).getStringValue().equals(valKeyAtt)).toList();

					if (keyAttMap == null || map == null) {
						localizationInNest(entities, globalfeature.getProxyGeometry());
					} else {
						localizationInNestWithNumbers(entities, globalfeature.getProxyGeometry());
					}

				}
			}
			outputPopulation.removeIf(a -> a.getLocation() == null);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return outputPopulation;
	}

	@Override
	public SpllPopulation linkPopulation(final SpllPopulation pop, final ISPLinker<SpllEntity> theLinker,
			final Collection<? extends AGeoEntity<? extends IValue>> linkedPlaces,
			final Attribute<? extends IValue> attribute) {
		pop.forEach(entity -> entity.addLinkedPlaces(attribute.getAttributeName(),
				theLinker.getCandidate(entity, linkedPlaces).orElseGet(null)));
		return pop;
	}

	// ----------------------------------------------------- //
	// ---------------------- MATHCER ---------------------- //
	// ----------------------------------------------------- //

	@Override
	public void setMatcher(final IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue> match,
			final String keyAttPop, final String keyAttMatch) {
		if (!match.isCoordinateCompliant(localizationConstraint.getReferenceFile())) throw new IllegalArgumentException(
				"The Coordinate Referent System of matcher does not fit population's geography:\n" + "Match = "
						+ match.getWKTCoordinateReferentSystem() + "\n" + "Geography = "
						+ localizationConstraint.getReferenceFile().getWKTCoordinateReferentSystem());

		if (match.getGeoAttributes().stream().noneMatch(att -> att.getAttributeName().equals(keyAttMatch)))
			throw new IllegalArgumentException("The match file does not contain any attribute named " + keyAttMatch
					+ "while this name has been setup to be the key attribute match");
		if (population.getPopulationAttributes().stream().noneMatch(att -> att.getAttributeName().equals(keyAttPop)))
			throw new IllegalArgumentException("The population does not contains any attribute named " + keyAttPop
					+ " while this name has been setup to be the key attribute population");
		this.match = match;
		this.keyAttPop = keyAttPop;
		this.keyAttMatch = keyAttMatch;
	}

	@Override
	public void setMatcher(final IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue> match,
			final String keyAttPop, final String keyAttMatch, final double releaseLimit, final double releaseStep,
			final int priority) {
		this.setMatcher(match, keyAttPop, keyAttMatch);
		this.localizationConstraint.setMaxIncrease(releaseLimit);
		this.localizationConstraint.setIncreaseStep(releaseStep);
		this.localizationConstraint.setPriority(priority);
	}

	@Override
	public IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue> estimateMatcher(final File destination)
			throws MismatchedDimensionException, IllegalArgumentException, IOException, TransformException,
			SchemaException {
		if (this.match == null) throw new NullPointerException(
				"To call for a matcher, you need first to set one while match variable is null");

		// Logger to track process
		gspu = new GSPerformanceUtil("Create a file to store entity-space match (called 'matcher')");
		Map<? extends AGeoEntity<? extends IValue>, Number> transfer =
				this.estimateMatches(this.match, this.keyAttMatch, this.keyAttPop);
		final Attribute<? extends IValue> transferAttribute =
				AttributeFactory.getFactory().createIntegerAttribute("count");
		return this.match.transferTo(destination, transfer, transferAttribute);
	}

	// ----------------------------------------------------- //
	// ----------------------- MAPPER ---------------------- //
	// ----------------------------------------------------- //

	/**
	 * Gets the mapper output.
	 *
	 * @return the mapper output
	 */
	public IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue> getMapperOutput() { return map; }

	@Override
	public void setMapper(final IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue> map,
			final String mapAttribute) {
		this.map = map;
		this.keyAttMap = mapAttribute;
	}

	@Override
	public void setMapper(
			final List<IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue>> ancillaryFileList,
			final List<? extends IValue> varList, final LMRegressionOLS lmRegressionOLS,
			final SPLUniformNormalizer splUniformNormalizer) throws IndexOutOfBoundsException, IOException,
			TransformException, InterruptedException, ExecutionException, IllegalRegressionException, GSMapperException,
			SchemaException, MismatchedDimensionException, IllegalArgumentException, InvalidGeoFormatException {
		String keyAttribute = "count";
		File tmp = File.createTempFile("match", "." + (GeoGSFileType.VECTOR.equals(match.getGeoGSFileType())
				? SPLGisFileExtension.shp.toString() : SPLGisFileExtension.tif.toString()));
		tmp.deleteOnExit();

		this.setMapper(this.estimateMatcher(tmp), keyAttribute, ancillaryFileList, varList, lmRegressionOLS,
				splUniformNormalizer);

	}

	@Override
	public void setMapper(final IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue> mainMapper,
			final String mainAttribute,
			final List<IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue>> ancillaryFileList,
			final List<? extends IValue> varList, final LMRegressionOLS lmRegressionOLS,
			final SPLUniformNormalizer splUniformNormalizer) throws IndexOutOfBoundsException, IOException,
			TransformException, InterruptedException, ExecutionException, IllegalRegressionException, GSMapperException,
			SchemaException, MismatchedDimensionException, IllegalArgumentException, InvalidGeoFormatException {
		this.setMapper(new SPLAreaMapperBuilder(mainMapper, mainAttribute, ancillaryFileList, varList, lmRegressionOLS,
				splUniformNormalizer));

	}

	/**
	 * Sets the mapper.
	 *
	 * @param splMapperBuilder
	 *            the spl mapper builder
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws TransformException
	 *             the transform exception
	 * @throws InterruptedException
	 *             the interrupted exception
	 * @throws ExecutionException
	 *             the execution exception
	 * @throws IllegalRegressionException
	 *             the illegal regression exception
	 * @throws IndexOutOfBoundsException
	 *             the index out of bounds exception
	 * @throws GSMapperException
	 *             the GS mapper exception
	 * @throws SchemaException
	 *             the schema exception
	 * @throws MismatchedDimensionException
	 *             the mismatched dimension exception
	 * @throws IllegalArgumentException
	 *             the illegal argument exception
	 * @throws InvalidGeoFormatException
	 *             the invalid geo format exception
	 */
	/*
	 * Inner utility set mapper from regression
	 *
	 */
	private void setMapper(final ASPLMapperBuilder<? extends ISPLVariable, ? extends Number> splMapperBuilder)
			throws IOException, TransformException, InterruptedException, ExecutionException,
			IllegalRegressionException, IndexOutOfBoundsException, GSMapperException, MismatchedDimensionException,
			IllegalArgumentException, InvalidGeoFormatException {
		splMapperBuilder.buildMapper();
		switch (splMapperBuilder.getAncillaryFiles().get(0).getGeoGSFileType()) {
			case RASTER:
				File tmpRaster = Files.createTempFile("regression_raster_output", ".tif").toFile();
				tmpRaster.deleteOnExit();
				this.setMapper(splMapperBuilder.buildOutput(tmpRaster,
						(SPLRasterFile) splMapperBuilder.getAncillaryFiles().get(0), false, true,
						(double) population.size()), GeoEntityFactory.ATTRIBUTE_PIXEL_BAND + 0);
				break;
			case VECTOR:
				File tmpVector = Files.createTempFile("regression_vector_output", ".shp").toFile();
				tmpVector.deleteOnExit();
				this.setMapper(splMapperBuilder.buildOutput(tmpVector,
						(SPLRasterFile) splMapperBuilder.getAncillaryFiles().get(0), false, true,
						(double) population.size()), splMapperBuilder.getMainAttribute());
				break;
			default:
				throw new IllegalArgumentException(
						"Ancillary could not be resolve to a proper geo file type (" + GeoGSFileType.values() + ")");
		}
	}

	/**
	 * Clear map cache.
	 */
	public void clearMapCache() {
		if (map instanceof SPLRasterFile file) { file.clearCache(); }
	}

	// ----------------------------------------------------- //
	// -------------------- CONSTRAINTS -------------------- //
	// ----------------------------------------------------- //

	@Override
	public void setConstraints(final List<ISpatialConstraint> constraints) {
		this.linker.setConstraints(constraints);
	}

	@Override
	public void addConstraint(final ISpatialConstraint constraint) {
		this.linker.addConstraints(constraint);
	}

	@Override
	public List<ISpatialConstraint> getConstraints() { return linker.getConstraints(); }

	/**
	 * The first constraint that allows to select only a limited number of nests to locate in according to a enclosing
	 * geometry
	 *
	 * @return
	 */
	public SpatialConstraintLocalization getLocalizationConstraint() { return localizationConstraint; }

	// ----------------------------------------------------- //
	// ------------------- DISTRIBUTION -------------------- //
	// ----------------------------------------------------- //

	@Override
	public ISpatialDistribution<ADemoEntity> getDistribution() { return linker.getDistribution(); }

	@Override
	public void setDistribution(final ISpatialDistribution<ADemoEntity> candidatesDistribution) {
		this.linker.setDistribution(candidatesDistribution);
	}

	// ----------------------------------------------------- //
	// ------------------ POINT LOCALIZER ------------------ //
	// ----------------------------------------------------- //

	/**
	 * Define the {@link PointInLocalizer} that will bind synthetic entities with precise coordinate (point)
	 *
	 * @param pointInLocalizer
	 */
	public void setPointInLocalizer(final PointInLocalizer pointInLocalizer) {
		this.pointInLocalizer = pointInLocalizer;
	}

	/**
	 * Get the {@link PointInLocalizer} that will place synthetic entities at a precise coordinate (point)
	 *
	 * @return
	 */
	public PointInLocalizer getPointInLocalizer() { return pointInLocalizer; }

	/////////////////////////////////////////////////////
	// --------------- INNER UTILITIES --------------- //
	/////////////////////////////////////////////////////

	// set to all the entities given as argument, a given nest chosen randomly in the possible geoEntities
	// of the localisation shapefile (all if not bounds is defined, only the one in the bounds if the one is not null)
	/**
	 * describe the algorithm PLZ !
	 *
	 * To be revised absolutly, because there is incoherences: e.g. loop over constraints to loop over constraint again,
	 * and then localize updating constraint.
	 *
	 * @param entities
	 * @param spatialBounds
	 * @throws IOException
	 * @throws TransformException
	 */
	private void localizationInNest(final Collection<SpllEntity> entities, final Geometry spatialBounds)
			throws IOException {

		localizationConstraint.setBounds(spatialBounds);
		List<AGeoEntity<? extends IValue>> possibleNests = localizationConstraint.getCandidates(
				localizationConstraint.getReferenceFile().getGeoEntity().stream().collect(Collectors.toList()));

		if (linker.getConstraints().isEmpty()) {

			localizationInNestOp(entities, possibleNests, null);

		} else {

			List<ISpatialConstraint> otherConstraints =
					Stream.concat(linker.getConstraints().stream(), Stream.of(localizationConstraint))
							.sorted(Comparator.comparing(ISpatialConstraint::getPriority)).toList();

			Collection<SpllEntity> remainingEntities = new ArrayList<>(entities);
			for (ISpatialConstraint cr : otherConstraints) {
				while (!cr.isConstraintLimitReach()) {

					List<AGeoEntity<? extends IValue>> candidates =
							new ArrayList<>(localizationConstraint.getReferenceFile().getGeoEntity());
					for (ISpatialConstraint constraint : otherConstraints) {
						candidates = constraint.getCandidates(candidates);
					}

					remainingEntities = localizationInNestOp(remainingEntities, candidates, null);
					if (remainingEntities == null || remainingEntities.isEmpty()) return;
					cr.relaxConstraint(possibleNests);

				}
			}
		}
	}

	/**
	 * Localization in nest op.
	 *
	 * @param entities
	 *            the entities
	 * @param possibleNests
	 *            the possible nests
	 * @param val
	 *            the val
	 * @return the list
	 */
	private List<SpllEntity> localizationInNestOp(final Collection<SpllEntity> entities,
			final List<AGeoEntity<? extends IValue>> possibleNests, Long val) {
		Collection<SpllEntity> chosenEntities = null;
		if (val != null) {
			List<SpllEntity> ens = new ArrayList<>(entities);
			chosenEntities = new ArrayList<>();
			val = Math.min(val, ens.size());
			for (int i = 0; i < val; i++) {
				int index = rand.nextInt(ens.size());
				SpllEntity entity = ens.get(index);
				chosenEntities.add(entity);
				ens.remove(entity);
			}
		} else {
			chosenEntities = entities;
		}
		for (SpllEntity entity : chosenEntities) {
			if (possibleNests.isEmpty()) { break; }
			Optional<AGeoEntity<? extends IValue>> oNest = linker.getCandidate(entity, possibleNests);
			boolean removeObject = false;
			if (oNest.isPresent()) {
				AGeoEntity<? extends IValue> nest = oNest.get();
				for (ISpatialConstraint constraint : linker.getConstraints()) {
					removeObject = removeObject || constraint.updateConstraint(nest);
				}
				if (removeObject) { possibleNests.remove(0); }
				entity.setNest(nest);
				entity.setLocation(pointInLocalizer.pointIn(nest.getProxyGeometry()));
			}

		}
		return entities.stream().filter(a -> a.getLocation() == null).toList();
	}

	// For each area concerned of the entityNbAreas shapefile (all if not bounds is defined, only the one in the bounds
	// if the one is not null),
	// define the number of entities from the entities list to locate inside, then try to set a nest to this randomly
	// chosen number of entities.
	/**
	 * Localization in nest with numbers.
	 *
	 * @param entities
	 *            the entities
	 * @param spatialBounds
	 *            the spatial bounds
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws TransformException
	 *             the transform exception
	 */
	// NOTE: if no nest is located inside the area, not entities will be located inside.
	@SuppressWarnings ("unchecked")
	private void localizationInNestWithNumbers(final List<SpllEntity> entities, final Geometry spatialBounds)
			throws IOException {
		List<ISpatialConstraint> otherConstraints = new ArrayList<>(linker.getConstraints());

		List<? extends AGeoEntity<? extends IValue>> areas =
				new ArrayList<>(spatialBounds == null ? map.getGeoEntity() : map.getGeoEntityWithin(spatialBounds));
		Collections.shuffle(areas, GenstarRandom.getInstance());

		Map<String, Double> vals = map.getGeoEntity().stream().collect(Collectors.toMap(AGeoEntity::getGenstarName,
				e -> e.getNumericValueForAttribute(keyAttMap).doubleValue()));
		Map<String, Double> vals2 = areas.stream().collect(Collectors.toMap(AGeoEntity::getGenstarName,
				e -> e.getNumericValueForAttribute(keyAttMap).doubleValue()));

		if (GeoGSFileType.RASTER.equals(map.getGeoGSFileType())) {
			double unknowVal = ((SPLRasterFile) map).getNoDataValue();
			List<String> es = new ArrayList<>(vals.keySet());
			for (String e : es) { if (vals.get(e).doubleValue() == unknowVal) { vals.remove(e); } }
			List<String> es2 = new ArrayList<>(vals2.keySet());
			for (String e : es2) { if (vals2.get(e).doubleValue() == unknowVal) { vals2.remove(e); } }
		}

		Double tot = vals.values().stream().mapToDouble(s -> s).sum();
		Double tot2 = vals2.values().stream().mapToDouble(s -> s).sum();
		if (tot == 0) return;
		Collection<SpllEntity> remainingEntities = entities;
		for (AGeoEntity<? extends IValue> feature : areas) {
			if (GeoGSFileType.RASTER.equals(map.getGeoGSFileType()) && !vals.containsKey(feature.getGenstarName())) {
				continue;
			}
			localizationConstraint.setBounds(feature.getProxyGeometry());
			long val =
					Math.round(population.size() * vals.get(feature.getGenstarName()) / tot * entities.size() / tot2);
			if (entities.isEmpty()) { break; }
			for (ISpatialConstraint cr : linker.getConstraints()) {
				while (!remainingEntities.isEmpty() && !cr.isConstraintLimitReach()) {
					List<AGeoEntity<? extends IValue>> possibleNestsInit = localizationConstraint.getCandidates(null);
					List<AGeoEntity<? extends IValue>> possibleNests = new ArrayList<>(possibleNestsInit);
					for (ISpatialConstraint constraint : otherConstraints) {
						possibleNests = constraint.getCandidates(possibleNests);
					}
					remainingEntities = localizationInNestOp(remainingEntities, possibleNests, val);
					if (!remainingEntities.isEmpty()) {
						cr.relaxConstraint((Collection<AGeoEntity<? extends IValue>>) localizationConstraint
								.getReferenceFile().getGeoEntity());
					}
				}
				if (remainingEntities.isEmpty()) { break; }
			}
		}
	}

	// ----------------------------- MOVE PART OF THESE METHOD INTO FACTORY / BUILDER

	/**
	 * Estimate matches.
	 *
	 * @param matchFile
	 *            the match file
	 * @param keyAttributeSpace
	 *            the key attribute space
	 * @param keyAttributePopulation
	 *            the key attribute population
	 * @return the map<? extends A geo entity<? extends I value>, number>
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	/*
	 * Estimate the number of match between population and space through the key attribute link
	 */
	protected Map<? extends AGeoEntity<? extends IValue>, Number> estimateMatches(
			final IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue> matchFile,
			final String keyAttributeSpace, final String keyAttributePopulation) throws IOException {
		// Collection of entity to match
		Collection<? extends AGeoEntity<? extends IValue>> entities = matchFile.getGeoEntity();

		// Setup key attribute of entity mapped to the number of match
		Map<String, Integer> attMatches = entities.stream()
				.collect(Collectors.toMap(e -> e.getValueForAttribute(keyAttributeSpace).getStringValue(), e -> 0));

		// Test if each entity has it's own key attribute, and if not through an exception
		if (attMatches.size() != entities.size()) throw new IllegalArgumentException(
				"Define matcher does not fit key attribute contract: some entity has the same key value");

		// DOES THE MATCH
		population.stream().map(e -> e.getValueForAttribute(keyAttributePopulation))
				.filter(v -> attMatches.containsKey(v.getStringValue()))
				.forEach(value -> attMatches.put(value.getStringValue(), attMatches.get(value.getStringValue()) + 1));

		this.gspu.sysoStempPerformance("Matches (" + attMatches.size() + ") have been counted (Total = "
				+ attMatches.values().stream().reduce(0, (i1, i2) -> i1 + i2).intValue() + ") !", this);

		// Bind each key attribute with its entity to fasten further processes
		return entities.stream().collect(Collectors.toMap(e -> e,
				e -> attMatches.get(e.getValueForAttribute(keyAttributeSpace).getStringValue())));
	}

	/**
	 * Creates the match file.
	 *
	 * @param output
	 *            the output
	 * @param template
	 *            the template
	 * @param eMatches
	 *            the e matches
	 * @return the SPL raster file
	 * @throws MismatchedDimensionException
	 *             the mismatched dimension exception
	 * @throws IllegalArgumentException
	 *             the illegal argument exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws TransformException
	 *             the transform exception
	 * @throws SchemaException
	 *             the schema exception
	 * @throws InvalidGeoFormatException
	 *             the invalid geo format exception
	 */
	/*
	 * Create a raster match file from a number of matches (eMatches) and a key attribute: parameter file for areal
	 * interpolation
	 */
	protected SPLRasterFile createMatchFile(final File output, final SPLRasterFile template,
			final Map<AGeoEntity<? extends IValue>, Number> eMatches)
			throws MismatchedDimensionException, IllegalArgumentException, IOException, TransformException {
		float[][] pixels = new float[template.getColumnNumber()][template.getRowNumber()];
		eMatches.entrySet().stream()
				.forEach(e -> pixels[((SpllPixel) e.getKey()).getGridX()][((SpllPixel) e.getKey()).getGridY()] =
						e.getValue().floatValue());

		this.gspu.sysoStempPerformance(
				"Matches have been stored in a raster file (" + pixels[0].length * pixels.length + " pixels) !", this);

		return new SPLGeofileBuilder().setFile(output).setRasterBands(pixels).setNoData(template.getNoDataValue())
				.setReferenceEnvelope(new ReferencedEnvelope(template.getEnvelope(),
						SpllUtil.getCRSfromWKT(template.getWKTCoordinateReferentSystem())))
				.buildRasterfile();
	}

	/**
	 * Creates the match file.
	 *
	 * @param output
	 *            the output
	 * @param matchFile
	 *            the match file
	 * @param eMatches
	 *            the e matches
	 * @param keyAttMatcher
	 *            the key att match
	 * @return the SPL vector file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws SchemaException
	 *             the schema exception
	 * @throws InvalidGeoFormatException
	 *             the invalid geo format exception
	 */
	/*
	 * Create a vector file from a number of matches and a key attribute: parameter file for areal interpolation
	 */
	protected SPLVectorFile createMatchFile(final File output, final SPLVectorFile matchFile,
			final Map<AGeoEntity<? extends IValue>, Number> eMatches, final String keyAttMatcher)
			throws IOException, SchemaException {
		Optional<Attribute<? extends IValue>> keyAtt = matchFile.getGeoAttributes().stream()
				.filter(att -> att.getAttributeName().equals(keyAttMatcher)).findFirst();
		if (!keyAtt.isPresent()) throw new IllegalArgumentException(
				"key attribute matcher " + keyAttMatcher + " does not exist in proposed matched file");
		if (!eMatches.keySet().stream().allMatch(entity -> entity.getPropertiesAttribute().contains(keyAttMatcher))
				|| !eMatches.keySet().stream()
						.allMatch(entity -> entity.getValueForAttribute(keyAtt.get().getAttributeName()) != null))
			throw new IllegalArgumentException("Matches entity must contain attribute " + keyAttMatcher);

		Attribute<? extends IValue> key = keyAtt.get();
		Attribute<IntegerValue> contAtt =
				AttributeFactory.getFactory().createIntegerAttribute(GeoEntityFactory.ATTRIBUTE_FEATURE_POP);

		// Transpose entity-contingency map into a collection of feature
		Collection<SpllFeature> features = constructFeatureCollection(eMatches, contAtt, key,
				matchFile.getStore().getSchema(matchFile.getStore().getTypeNames()[0]));

		this.gspu.sysoStempPerformance("Matches have been stored in a vector file (" + features.size() + " features) !",
				this);
		Set<SpllFeature> categoricalValue = features.stream().filter(
				feat -> !feat.getValueForAttribute(GeoEntityFactory.ATTRIBUTE_FEATURE_POP).getType().isNumericValue())
				.collect(Collectors.toSet());
		if (!categoricalValue.isEmpty()) throw new GenstarException(
				categoricalValue.size() + " created feature are not numerical: " + categoricalValue.stream()
						.map(gsf -> gsf.getValueForAttribute(key).getStringValue()).collect(Collectors.joining("; ")));
		this.gspu.sysoStempPerformance("Total population count is " + features.stream()
				.mapToDouble(
						feat -> feat.getNumericValueForAttribute(GeoEntityFactory.ATTRIBUTE_FEATURE_POP).intValue())
				.sum(), this);

		return new SPLGeofileBuilder().setFile(output).setFeatures(features).buildShapeFile();
	}

	/**
	 * Construct feature collection.
	 *
	 * @param eMatches
	 *            the e matches
	 * @param contAtt
	 *            the cont att
	 * @param keyAtt
	 *            the key att
	 * @param featType
	 *            the feat type
	 * @return the collection
	 */
	/*
	 * Create a set of GSFeature
	 */
	protected Collection<SpllFeature> constructFeatureCollection(
			final Map<AGeoEntity<? extends IValue>, Number> eMatches, final Attribute<IntegerValue> contAtt,
			final Attribute<? extends IValue> keyAtt, final SimpleFeatureType featType) {
		GeoEntityFactory ef = new GeoEntityFactory(Stream.of(contAtt, keyAtt).collect(Collectors.toSet()), featType);
		Collection<SpllFeature> features = new HashSet<>();
		for (Entry<AGeoEntity<? extends IValue>, Number> entry : eMatches.entrySet()) {
			Map<Attribute<? extends IValue>, IValue> theMap = new HashMap<>();
			theMap.put(contAtt, contAtt.getValueSpace().addValue(entry.getValue().toString()));
			AGeoEntity<? extends IValue> entity = entry.getKey();
			theMap.put(keyAtt, entity.getValueForAttribute(keyAtt.getAttributeName()));
			features.add(ef.createGeoEntity(entity.getProxyGeometry(), theMap));
		}
		return features;
	}

}