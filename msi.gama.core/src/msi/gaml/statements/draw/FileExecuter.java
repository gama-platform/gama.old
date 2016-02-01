/**
 * Created by drogoul, 28 janv. 2016
 *
 */
package msi.gaml.statements.draw;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.*;
import msi.gaml.statements.draw.DrawingData.DrawingAttributes;

class FileExecuter extends DrawExecuter {

	private final GamaImageFile constImg;
	private BufferedImage workImage;
	private Graphics2D g2d;

	FileExecuter(final IExpression item) throws GamaRuntimeException {
		constImg = (GamaImageFile) (item.isConst() ? Cast.as(item, IGamaFile.class, false) : null);
	}

	@Override
		Rectangle2D executeOn(final IScope scope, final IExpression item, final IGraphics g,
			final DrawingAttributes attributes) throws GamaRuntimeException {

		// We push the location of the agent if none has been provided
		attributes.setLocationIfAbsent(new GamaPoint(scope.getAgentScope().getLocation()));
		//
		final GamaFile file = (GamaFile) item.value(scope);
		if ( file.getExtension().equals("obj") || file.getExtension().equals("svg") ) {
			// File fmtl = new File(file.getFile().getAbsolutePath().replaceAll(".obj", ".mtl"));
			// if ( !fmtl.exists() ) {
			// GAMA.reportError(scope, GamaRuntimeException.warning("No " + fmtl.toString() + " found", scope),
			// false);
			// }
			// Color color = attributes.color;
			// GamaPair<Double, GamaPoint> rot = attributes.rotation;
			return g.drawFile((GamaGeometryFile) file, attributes);
			//
			// if ( rot != null && rot.key != null ) {
			// return g.drawFile(scope, file, color, attributes.location, attributes.size, rot,
			// ((Gama3DGeometryFile) file).getInitRotation());
			// } else {
			// return g.drawFile(scope, file, color, attributes.location, attributes.size,
			// ((Gama3DGeometryFile) file).getInitRotation());
			// }
			// }
			// if ( file.getExtension().equals("svg") ) {
			// Color color = attributes.color;
			// GamaPair<Double, GamaPoint> rot = attributes.rotation;
			// if ( rot != null && rot.key != null ) {
			// return g.drawFile(scope, file, color, attributes.location, attributes.size, rot);
			// } else {
			// return g.drawFile(scope, file, color, attributes.location, attributes.size, null);
			// }
		} else { // Use for Image
			final ILocation from = attributes.location;
			final Double displayWidth = attributes.size.getX();
			final GamaImageFile imageFile = constImg == null ? (GamaImageFile) item.value(scope) : constImg;
			final BufferedImage img = imageFile.getImage(scope);
			final int image_width = img.getWidth();
			final int image_height = img.getHeight();
			final double ratio = image_width / (double) image_height;
			final int displayHeight = Maths.round(displayWidth / ratio);
			final int x = (int) (from.getX() - displayWidth / 2);
			final int y = (int) (from.getY() - displayHeight / 2d);
			// No grid line
			attributes.border = null;
			// New size
			attributes.size = new GamaPoint(displayWidth, displayHeight);
			// New location
			attributes.location = new GamaPoint(x, y, from.getZ());
			if ( attributes.hasColor ) {
				final Color c = attributes.color;
				if ( workImage == null || workImage.getWidth() != image_width ||
					workImage.getHeight() != image_height ) {
					if ( workImage != null ) {
						workImage.flush();
					}
					workImage = new BufferedImage(image_width, image_height, BufferedImage.TYPE_INT_ARGB);
					if ( g2d != null ) {
						g2d.dispose();
					}
					g2d = workImage.createGraphics();
					g2d.drawImage(img, 0, 0, null);
				} else if ( constImg == null ) {
					g2d.drawImage(img, 0, 0, null);
				}
				g2d.setPaint(c);
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP));
				g2d.fillRect(0, 0, image_width, image_height);

				final Rectangle2D result = g.drawImage(workImage, attributes);
				workImage.flush();
				return result;
			}
			return g.drawImage(img, attributes);
		}

	}
}