/*********************************************************************************************
 *
 * 'FileExecuter.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.statements.draw;

import java.awt.geom.Rectangle2D;

import com.vividsolutions.jts.geom.Envelope;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.Scaling3D;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.GamaFile;
import msi.gama.util.file.GamaGisFile;
import msi.gama.util.file.GamaImageFile;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.Types;

@SuppressWarnings ({ "rawtypes" })
class FileExecuter extends DrawExecuter {

	private final GamaFile constImg;

	FileExecuter(final IExpression item) throws GamaRuntimeException {
		super(item);
		constImg = item.isConst() ? (GamaFile) Types.FILE.cast(null, item.getConstValue(), null, false) : null;
	}

	@Override
	Rectangle2D executeOn(final IScope scope, final IGraphics g, final DrawingData data) throws GamaRuntimeException {
		final GamaFile file = constImg == null ? (GamaFile) item.value(scope) : constImg;
		if (file == null) { return null; }
		final FileDrawingAttributes attributes =
				computeAttributes(scope, data, file instanceof GamaImageFile, file instanceof GamaGisFile);

		// XXX EXPERIMENTAL See Issue #1521
		if (GamaPreferences.Displays.DISPLAY_ONLY_VISIBLE.getValue()
				&& /* !GAMA.isInHeadLessMode() */ !scope.getExperiment().isHeadless()) {
			final Scaling3D size = attributes.getSize();
			if (size != null) {
				// if a size is provided
				final Envelope3D expected = Envelope3D.of(attributes.getLocation());
				expected.expandBy(size.getX() / 2, size.getY() / 2);
				final Envelope visible = g.getVisibleRegion();
				if (visible != null) {
					if (!visible.intersects(expected)) { return null; }
				}
			}
			// XXX EXPERIMENTAL
		}

		return g.drawFile(file, attributes);
	}

	FileDrawingAttributes computeAttributes(final IScope scope, final DrawingData data, final boolean imageFile,
			final boolean gisFile) {
		final FileDrawingAttributes attributes = new FileDrawingAttributes(Scaling3D.of(data.size.value),
				data.rotation.value, data.location.value, data.getCurrentColor(), data.border.value, scope.getAgent(),
				data.lineWidth.value, imageFile, data.lighting.value);
		// We push the location of the agent if none has been provided and if it is not a GIS file (where coordinates
		// are already provided, see Issue #2165)
		if (!gisFile) {
			attributes.setLocationIfAbsent(new GamaPoint(scope.getAgent().getLocation()));
		}
		if (imageFile) {
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