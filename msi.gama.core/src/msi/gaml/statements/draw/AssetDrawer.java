/*******************************************************************************************************
 *
 * FileDrawer.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.draw;

import java.awt.geom.Rectangle2D;

import org.locationtech.jts.geom.Envelope;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.Scaling3D;
import msi.gama.common.interfaces.IAsset;
import msi.gama.common.interfaces.IDrawDelegate;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.IImageProvider;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.runtime.IScope;
import msi.gama.runtime.IScope.IGraphicsScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.GamaGisFile;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class FileExecuter.
 */
@SuppressWarnings ({ "rawtypes" })
public class AssetDrawer implements IDrawDelegate {

	/**
	 * Execute on.
	 *
	 * @param scope
	 *            the scope
	 * @param g
	 *            the g
	 * @param data
	 *            the data
	 * @return the rectangle 2 D
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@Override
	public Rectangle2D executeOn(final IGraphicsScope scope, final DrawingData data, final IExpression... items)
			throws GamaRuntimeException {
		IGraphics g = scope.getGraphics();
		Object obj = items[0].value(scope);

		if (obj instanceof IAsset asset) {
			// TODO verify that we do not spend the processing time recreating the file...
			final AssetDrawingAttributes attributes = computeAttributes(scope, data, asset instanceof IImageProvider,
					asset instanceof GamaGisFile, g.is2D());
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
			return g.drawAsset(asset, attributes);
		}
		return null;
	}

	/**
	 * Compute attributes.
	 *
	 * @param scope
	 *            the scope
	 * @param data
	 *            the data
	 * @param isImage
	 *            the image file
	 * @param isGIS
	 *            the gis file
	 * @param twoD
	 *            the two D
	 * @return the file drawing attributes
	 */
	AssetDrawingAttributes computeAttributes(final IScope scope, final DrawingData data, final boolean isImage,
			final boolean isGIS, final boolean twoD) {
		final AssetDrawingAttributes attributes = new AssetDrawingAttributes(Scaling3D.of(data.size.get()),
				data.rotation.get(), data.getLocation(), data.color.get(), data.border.get(), scope.getAgent(),
				data.lineWidth.get(), isImage, data.lighting.get());
		// We push the location of the agent if none has been provided and if it is not a GIS file (where coordinates
		// are already provided, see Issue #2165)
		if (!isGIS && attributes.getLocation() == null) {
			attributes.setLocation(scope.getAgent().getLocation().clone());
		}
		if (twoD && isImage) {
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

	@Override
	public IType<?> typeDrawn() {
		return Types.FILE;
	}
}