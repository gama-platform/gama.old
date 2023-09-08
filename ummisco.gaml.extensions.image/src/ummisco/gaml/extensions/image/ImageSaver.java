/*******************************************************************************************************
 *
 * ImageSaver.java, in ummisco.gaml.extensions.image, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gaml.extensions.image;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import javax.imageio.ImageIO;

import com.google.common.collect.Sets;

import msi.gama.common.interfaces.ISaveDelegate;
import msi.gama.metamodel.topology.grid.GridPopulation;
import msi.gama.metamodel.topology.projection.IProjection;
import msi.gama.metamodel.topology.projection.ProjectionFactory;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gama.util.matrix.GamaField;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Maths;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.save.AbstractSaver;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class ImageSaver.
 */
public class ImageSaver extends AbstractSaver implements ISaveDelegate {

	/** The Constant FILE_FORMATS. */
	public static final Set<String> FILE_FORMATS = Set.of("image", "jpg", "jpeg", "png", "gif");

	/**
	 * Save.
	 *
	 * @param scope
	 *            the scope
	 * @param item
	 *            the item
	 * @param file
	 *            the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Override
	public void save(final IScope scope, final IExpression item, final File file, final String code,
			final boolean addHeader, final String type, final Object attributesToSave) throws IOException {
		File f = file;
		String path = f.getAbsolutePath();
		String t = "image".equals(type) ? "png" : "jpeg".equals(type) ? "jpg" : type;
		if ("image".equals(t)) { t = "png"; }
		if ("jpeg".equals(t)) { t = "jpg"; }
		if (!path.contains("." + t)) {
			path += "." + t;
			f = new File(path);
		}
		if (f.exists()) { f.delete(); }
		Object v = item.value(scope);
		boolean saved = false;
		if (v instanceof GamaField gf) {
			saveField(scope, gf, f, t);
			ProjectionFactory.saveTargetCRSAsPRJFile(scope, f.getAbsolutePath());
			saved = true;
		} else {
			final ISpecies species = Cast.asSpecies(scope, v);
			if (species != null && species.isGrid()) {
				saveGrid(scope, species, f, t);
				ProjectionFactory.saveTargetCRSAsPRJFile(scope, f.getAbsolutePath());
				saved = true;
			}
		}
		if (!saved) {
			GamaImage image = GamaImageType.staticCast(scope, item.value(scope), false);
			if (image == null) return;
			ImageIO.write(image, t, file);
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
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void saveGrid(final IScope scope, final ISpecies species, final File file, final String type)
			throws IOException {
		final GridPopulation gp = (GridPopulation) species.getPopulation(scope);
		final int cols = gp.getNbCols();
		final int rows = gp.getNbRows();
		IProjection worldProjection = scope.getSimulation().getProjectionFactory().getWorld();
		double x = worldProjection == null ? 0 : worldProjection.getProjectedEnvelope().getMinX();
		double y = worldProjection == null ? 0 : worldProjection.getProjectedEnvelope().getMinY();
		final double cw = gp.getAgent(0).getGeometry().getWidth();
		final double ch = gp.getAgent(0).getGeometry().getHeight();
		x += cw / 2;
		y += ch / 2;
		try (final FileWriter fw =
				new FileWriter(file.getAbsolutePath().replace(".png", ".pgw").replace(".jpg", ".jgw"));) {
			fw.write(cw + "\n0.0\n0.0\n" + ch + "\n" + x + "\n" + y);
		}
		final BufferedImage image = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_RGB);
		DataBufferInt buffer = (DataBufferInt) image.getRaster().getDataBuffer();
		final int[] imageData = buffer.getData();
		System.arraycopy(gp.getTopology().getPlaces().getDisplayData(), 0, imageData, 0, imageData.length);
		// see #3592
		// for (final Object g : gp.getAgents(scope).iterable(scope)) {
		// final IGridAgent ag = (IGridAgent) g;
		// image.setRGB(ag.getX(), rows - 1 - ag.getY(), ag.getColor().getRGB());
		// }
		ImageIO.write(image, type, file);

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
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void saveField(final IScope scope, final GamaField field, final File f, final String t) throws IOException {
		if (field.isEmpty(scope)) return;
		final int cols = field.numCols;
		final int rows = field.numRows;
		IProjection worldProjection = scope.getSimulation().getProjectionFactory().getWorld();
		double x = worldProjection == null ? 0 : worldProjection.getProjectedEnvelope().getMinX();
		double y = worldProjection == null ? 0 : worldProjection.getProjectedEnvelope().getMinY();
		final double cw =
				scope.getSimulation().getProjectionFactory().getWorld().getProjectedEnvelope().getWidth() / cols;
		final double ch =
				scope.getSimulation().getProjectionFactory().getWorld().getProjectedEnvelope().getHeight() / rows;
		x += cw / 2;
		y += ch / 2;
		try (final FileWriter fw =
				new FileWriter(f.getAbsolutePath().replace(".png", ".pgw").replace(".jpg", ".jgw"));) {
			fw.write(cw + "\n0.0\n0.0\n" + ch + "\n" + x + "\n" + y);
		}
		final BufferedImage image = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_RGB);
		double[] minmaxVal = field.getMinMax();
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				double v = field.get(scope, col, row);
				int vRef = Maths.round((v - minmaxVal[0]) / (minmaxVal[1] - minmaxVal[0]) * 255);
				image.setRGB(col, rows - 1 - row, GamaColor.get(vRef, vRef, vRef).getRGB());
			}
		}
		ImageIO.write(image, t, f);

	}

	/**
	 * Compute file types.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the sets the
	 * @date 29 août 2023
	 */
	@Override
	public Set<String> computeFileTypes() {
		return Sets.union(Set.of(ImageIO.getWriterFileSuffixes()), FILE_FORMATS);
	}

	@Override
	public IType getDataType() { return Types.get(GamaImageType.ID); }

}
