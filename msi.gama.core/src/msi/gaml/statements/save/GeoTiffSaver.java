/*******************************************************************************************************
 *
 * GeoTiffSaver.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.save;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.geometry.Envelope2D;
import org.opengis.coverage.grid.GridCoverageWriter;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import msi.gama.metamodel.topology.grid.GridPopulation;
import msi.gama.metamodel.topology.projection.IProjection;
import msi.gama.metamodel.topology.projection.ProjectionFactory;
import msi.gama.runtime.IScope;
import msi.gama.util.matrix.GamaField;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;

/**
 * The Class GeoTiffSaver.
 */
public class GeoTiffSaver extends AbstractSaver {

	/** The Constant GEOTIFF. */
	private static final String GEOTIFF = "geotiff";

	/**
	 * Save.
	 *
	 * @param scope
	 *            the scope
	 * @param item
	 *            the item
	 * @param file
	 *            the file
	 * @param code
	 *            the code
	 * @param addHeader
	 *            the add header
	 * @param type
	 *            the type
	 * @param attributesToSave
	 *            the attributes to save
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Override
	public void save(final IScope scope, final IExpression item, final File file, final String code,
			final boolean addHeader, final String type, final Object attributesToSave) throws IOException {
		if (file == null) return;
		File f = file;
		if (f.exists()) { f.delete(); }
		try {
			Object v = item.value(scope);
			if (v instanceof GamaField gf) {
				saveField(scope, gf, f);
			} else {
				final ISpecies species = Cast.asSpecies(scope, v);
				if (species == null || !species.isGrid()) return;
				saveGrid(scope, species, f);
			}
		} finally {
			ProjectionFactory.saveTargetCRSAsPRJFile(scope, f.getAbsolutePath());
		}
	}

	/**
	 * Save grid.
	 *
	 * @param scope
	 *            the scope
	 * @param species
	 *            the species
	 * @param file
	 *            the file
	 * @throws IllegalArgumentException
	 *             the illegal argument exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void saveGrid(final IScope scope, final ISpecies species, final File file)
			throws IllegalArgumentException, IOException {
		final GridPopulation gp = (GridPopulation) species.getPopulation(scope);
		final int cols = gp.getNbCols();
		final int rows = gp.getNbRows();
		IProjection worldProjection = scope.getSimulation().getProjectionFactory().getWorld();
		CoordinateReferenceSystem crs = ProjectionFactory.getTargetCRSOrDefault(scope);
		double x = worldProjection == null ? 0 : worldProjection.getProjectedEnvelope().getMinX();
		double y = worldProjection == null ? 0 : worldProjection.getProjectedEnvelope().getMinY();

		final float[][] imagePixelData = new float[rows][cols];
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) { imagePixelData[row][col] = gp.getGridValue(col, row).floatValue(); }

		}
		final double width = scope.getSimulation().getEnvelope().getWidth();
		final double height = scope.getSimulation().getEnvelope().getHeight();

		Envelope2D refEnvelope;
		refEnvelope = new Envelope2D(crs, x, y, width, height);

		// In order to fix issue #2793, it seems that (before the GAMA 1.8 release), GAMA is only able,
		// to read GeoTiff files with Byte format data.
		// The use of the following create from org.geotools.coverage.grid.GridCoverageFactory, will produce a
		// dataset of floats.
		// This is perfectly possible for the GeoTiff, but as GAMA can only read Byte format GeoTiff files, we limit
		// the save to this
		// specific format of data.
		final GridCoverage2D coverage = new GridCoverageFactory().create("data", imagePixelData, refEnvelope);
		// final GridCoverage2D coverage = createCoverageByteFromFloat("data", imagePixelData, refEnvelope);

		final GeoTiffFormat format = new GeoTiffFormat();
		final GridCoverageWriter writer = format.getWriter(file);
		writer.write(coverage, (GeneralParameterValue[]) null);

	}

	/**
	 * Save field.
	 *
	 * @param scope
	 *            the scope
	 * @param field
	 *            the field
	 * @param f
	 *            the f
	 * @throws IllegalArgumentException
	 *             the illegal argument exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void saveField(final IScope scope, final GamaField field, final File f)
			throws IllegalArgumentException, IOException {
		if (field.isEmpty(scope)) return;
		final int cols = field.numCols;
		final int rows = field.numRows;
		IProjection worldProjection = scope.getSimulation().getProjectionFactory().getWorld();
		double x = worldProjection == null ? 0 : worldProjection.getProjectedEnvelope().getMinX();
		double y = worldProjection == null ? 0 : worldProjection.getProjectedEnvelope().getMinY();
		CoordinateReferenceSystem crs = ProjectionFactory.getTargetCRSOrDefault(scope);
		final float[][] imagePixelData = new float[rows][cols];
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) { imagePixelData[row][col] = field.get(scope, col, row).floatValue(); }
		}
		final double width = scope.getSimulation().getEnvelope().getWidth();
		final double height = scope.getSimulation().getEnvelope().getHeight();
		Envelope2D refEnvelope;
		refEnvelope = new Envelope2D(crs, x, y, width, height);

		// In order to fix issue #2793, it seems that (before the GAMA 1.8 release), GAMA is only able,
		// to read GeoTiff files with Byte format data.
		// The use of the following create from org.geotools.coverage.grid.GridCoverageFactory, will produce a
		// dataset of floats.
		// This is perfectly possible for the GeoTiff, but as GAMA can only read Byte format GeoTiff files, we limit
		// the save to this
		// specific format of data.
		final GridCoverage2D coverage = new GridCoverageFactory().create("data", imagePixelData, refEnvelope);
		final GeoTiffFormat format = new GeoTiffFormat();
		final GridCoverageWriter writer = format.getWriter(f);
		writer.write(coverage, (GeneralParameterValue[]) null);

	}

	@Override
	public Set<String> computeFileTypes() {
		return Set.of(GEOTIFF);
	}
}
