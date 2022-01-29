/*******************************************************************************************************
 *
 * FileExecuter.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.statements.draw;

import java.awt.geom.Rectangle2D;

import org.locationtech.jts.geom.Envelope;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.Scaling3D;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.runtime.IScope;
import msi.gama.runtime.IScope.IGraphicsScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.GamaFile;
import msi.gama.util.file.GamaGisFile;
import msi.gama.util.file.GamaImageFile;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.Types;

/**
 * The Class FileExecuter.
 */
@SuppressWarnings ({ "rawtypes" })
class FileExecuter extends DrawExecuter {

	/** The const img. */
	private final GamaFile constImg;

	/**
	 * Instantiates a new file executer.
	 *
	 * @param item the item
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	FileExecuter(final IExpression item) throws GamaRuntimeException {
		super(item);
		constImg = item.isConst() ? (GamaFile) Types.FILE.cast(null, item.getConstValue(), null, false) : null;
	}

	@Override
	Rectangle2D executeOn(final IGraphicsScope scope, final IGraphics g, final DrawingData data)
			throws GamaRuntimeException {
		final GamaFile file = constImg == null ? (GamaFile) item.value(scope) : constImg;
		if (file == null) return null;
		final FileDrawingAttributes attributes =
				computeAttributes(scope, data, file instanceof GamaImageFile, file instanceof GamaGisFile, g.is2D());

		// XXX EXPERIMENTAL See Issue #1521
		if (GamaPreferences.Displays.DISPLAY_ONLY_VISIBLE.getValue()
				&& /* !GAMA.isInHeadLessMode() */ !scope.getExperiment().isHeadless()) {
			final Scaling3D size = attributes.getSize();
			if (size != null) {
				// if a size is provided
				final Envelope3D expected = Envelope3D.of(attributes.getLocation());
				expected.expandBy(size.getX() / 2, size.getY() / 2);
				final Envelope visible = g.getVisibleRegion();
				if (visible != null && !visible.intersects(expected)) return null;
			}
			// XXX EXPERIMENTAL
		}

		return g.drawFile(file, attributes);
	}

	/**
	 * Compute attributes.
	 *
	 * @param scope the scope
	 * @param data the data
	 * @param imageFile the image file
	 * @param gisFile the gis file
	 * @param twoD the two D
	 * @return the file drawing attributes
	 */
	FileDrawingAttributes computeAttributes(final IScope scope, final DrawingData data, final boolean imageFile,
			final boolean gisFile, final boolean twoD) {
		final FileDrawingAttributes attributes = new FileDrawingAttributes(Scaling3D.of(data.size.get()),
				data.rotation.get(), data.getLocation(), data.color.get(), data.border.get(), scope.getAgent(),
				data.lineWidth.get(), imageFile, data.lighting.get());
		// We push the location of the agent if none has been provided and if it is not a GIS file (where coordinates
		// are already provided, see Issue #2165)
		if (!gisFile && attributes.getLocation() == null) {
			attributes.setLocation(scope.getAgent().getLocation().clone());
		}
		if (twoD && imageFile) {
			// If the size is provided, we automatically center the file
			final Scaling3D size = attributes.getSize();
			if (size != null) {
				// New location
				attributes
						.setLocation(attributes.getLocation().minus(size.getX() / 2, size.getY() / 2, size.getZ() / 2));
			}
		}

		return attributes;
	}
}