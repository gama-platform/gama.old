package msi.gaml.statements.save;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

import msi.gama.metamodel.topology.grid.GamaSpatialMatrix.GridPopulation;
import msi.gama.metamodel.topology.projection.IProjection;
import msi.gama.metamodel.topology.projection.ProjectionFactory;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gama.util.matrix.GamaField;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Maths;
import msi.gaml.skills.GridSkill.IGridAgent;
import msi.gaml.species.ISpecies;

public class ImageSaver {

	public void save(final IScope scope, final IExpression item, final File file) throws IOException {
		if (file == null) return;
		File f = file;
		String path = f.getAbsolutePath();

		if (!path.contains("png")) {
			path += ".png";
			f = new File(path);
		}
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

	private void saveGrid(final IScope scope, final ISpecies species, final File file) throws IOException {
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
		try (final FileWriter fw = new FileWriter(file.getAbsolutePath().replace(".png", ".pgw"));) {
			fw.write(cw + "\n0.0\n0.0\n" + ch + "\n" + x + "\n" + y);
		}
		final BufferedImage image = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_RGB);
		for (final Object g : gp.getAgents(scope).iterable(scope)) {
			final IGridAgent ag = (IGridAgent) g;
			image.setRGB(ag.getX(), rows - 1 - ag.getY(), ag.getColor().getRGB());
		}
		ImageIO.write(image, "png", file);

	}

	private void saveField(final IScope scope, final GamaField field, final File f) throws IOException {
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
		try (final FileWriter fw = new FileWriter(f.getAbsolutePath().replace(".png", ".pgw"));) {
			fw.write(cw + "\n0.0\n0.0\n" + ch + "\n" + x + "\n" + y);
		}
		final BufferedImage image = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_RGB);
		double[] minmaxVal = field.getMinMax(null);
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				double v = field.get(scope, col, row);
				int vRef = Maths.round((v - minmaxVal[0]) / (minmaxVal[1] - minmaxVal[0]) * 255);
				image.setRGB(col, rows - 1 - row, new GamaColor(vRef, vRef, vRef).getRGB());
			}
		}
		ImageIO.write(image, "png", f);

	}
}
