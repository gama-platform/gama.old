/*********************************************************************************************
 *
 * 'GridLayerHelper.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/

package ummisco.gama.ui.viewers.gis.geotools.tool;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.operation.MathTransform;

import ummisco.gama.ui.viewers.gis.geotools.utils.Utils;

/**
 * Helper class used by {@code InfoTool} to query {@code MapLayers} with raster
 * feature data ({@code GridCoverage2D} or {@code AbstractGridCoverage2DReader}
 * ).
 *
 * @see InfoTool
 * @see VectorLayerHelper
 *
 * @author Michael Bedward
 * @since 2.6
 *
 *
 *
 * @source $URL$
 */
public class GridLayerHelper extends InfoToolHelper<List<Number>> {
	protected final WeakReference<GridCoverage2D> covRef;

	/**
	 * Create a new helper to work with the given raster data source.
	 *
	 * @param content
	 *            the {@code MapContext} associated with this helper
	 * @param rasterSource
	 *            an instance of either {@code GridCoverage2D} or
	 *            {@code AbstractGridCoverage2DReader
	 */
	public GridLayerHelper(final MapContent content, final Layer layer) {
		super(content, null);

		Object rasterSource = null;
		try (FeatureIterator<?> iter = layer.getFeatureSource().getFeatures().features()) {
			final String gridAttrName = Utils.getGridAttributeName(layer);
			rasterSource = iter.next().getProperty(gridAttrName).getValue();
		} catch (final Exception ex) {
			throw new IllegalStateException("Unable to access raster feature data", ex);
		}

		GridCoverage2D cov = null;
		try {
			if (GridCoverage2DReader.class.isAssignableFrom(rasterSource.getClass())) {
				cov = ((GridCoverage2DReader) rasterSource).read(null);
			} else {
				cov = (GridCoverage2D) rasterSource;
			}

			this.covRef = new WeakReference<GridCoverage2D>(cov);

		} catch (final Exception ex) {
			throw new IllegalArgumentException(ex);
		}

		setCRS(cov.getCoordinateReferenceSystem());
	}

	/**
	 * Get band values at the given position
	 *
	 * @param pos
	 *            the location to query
	 *
	 * @param params
	 *            not used at present
	 *
	 * @return a {@code List} of band values; will be empty if {@code pos} was
	 *         outside the coverage bounds
	 *
	 * @throws Exception
	 *             if the grid coverage could not be queried
	 */
	@Override
	public List<Number> getInfo(final DirectPosition2D pos, final Object... params) throws Exception {

		final List<Number> list = new ArrayList<Number>();

		if (isValid()) {
			final GridCoverage2D cov = covRef.get();
			if (cov != null) {
				final ReferencedEnvelope env = new ReferencedEnvelope(cov.getEnvelope2D());
				final DirectPosition2D trPos = getTransformed(pos);
				if (env.contains(trPos)) {
					final Object objArray = cov.evaluate(trPos);
					final Number[] bandValues = asNumberArray(objArray);
					if (bandValues != null) {
						for (final Number value : bandValues) {
							list.add(value);
						}
					}
				}
			}
		}

		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isValid() {
		return getMapContent() != null && covRef != null && covRef.get() != null;
	}

	/**
	 * Convert the Object returned by
	 * {@linkplain GridCoverage2D#evaluate(DirectPosition)} into an array of
	 * {@code Numbers}.
	 *
	 * @param objArray
	 *            an Object representing a primitive array
	 *
	 * @return a new array of Numbers
	 */
	private Number[] asNumberArray(final Object objArray) {
		Number[] numbers = null;

		if (objArray instanceof byte[]) {
			final byte[] values = (byte[]) objArray;
			numbers = new Number[values.length];
			for (int i = 0; i < values.length; i++) {
				numbers[i] = values[i] & 0xff;
			}

		} else if (objArray instanceof int[]) {
			final int[] values = (int[]) objArray;
			numbers = new Number[values.length];
			for (int i = 0; i < values.length; i++) {
				numbers[i] = values[i];
			}

		} else if (objArray instanceof float[]) {
			final float[] values = (float[]) objArray;
			numbers = new Number[values.length];
			for (int i = 0; i < values.length; i++) {
				numbers[i] = values[i];
			}
		} else if (objArray instanceof double[]) {
			final double[] values = (double[]) objArray;
			numbers = new Number[values.length];
			for (int i = 0; i < values.length; i++) {
				numbers[i] = values[i];
			}
		}

		return numbers;
	}

	/**
	 * Transform the query position into the coordinate reference system of the
	 * data (if different to that of the {@code MapContext}).
	 *
	 * @param pos
	 *            query position in {@code MapContext} coordinates
	 *
	 * @return query position in data ({@code MapLayer}) coordinates
	 */
	private DirectPosition2D getTransformed(final DirectPosition2D pos) {
		if (isTransformRequired()) {
			final MathTransform tr = getTransform();
			if (tr == null) {
				throw new IllegalStateException("MathTransform should not be null");
			}

			try {
				return (DirectPosition2D) tr.transform(pos, null);
			} catch (final Exception ex) {
				throw new IllegalStateException(ex);
			}
		}

		return pos;
	}

}
