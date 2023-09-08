/*******************************************************************************************************
 *
 * ASCSaver.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.save;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Set;

import msi.gama.metamodel.topology.grid.GridPopulation;
import msi.gama.metamodel.topology.projection.ProjectionFactory;
import msi.gama.runtime.IScope;
import msi.gama.util.matrix.GamaField;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Comparison;
import msi.gaml.operators.Strings;
import msi.gaml.species.ISpecies;

/**
 * The Class ASCSaver.
 */
public class ASCSaver extends AbstractSaver {

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
		if (file.exists()) { file.delete(); }
		try {
			save(scope, item, new FileWriter(file));
		} finally {
			ProjectionFactory.saveTargetCRSAsPRJFile(scope, file.getAbsolutePath());
		}
	}

	/**
	 * Save.
	 *
	 * @param scope
	 *            the scope
	 * @param item
	 *            the item
	 * @param stream
	 *            the stream
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void save(final IScope scope, final IExpression item, final OutputStream stream) throws IOException {
		if (stream == null) return;
		save(scope, item, new OutputStreamWriter(stream));
	}

	/**
	 * Save.
	 *
	 * @param scope
	 *            the scope
	 * @param item
	 *            the item
	 * @param fw
	 *            the fw
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void save(final IScope scope, final IExpression item, final Writer fw) throws IOException {
		try (fw) {
			Object v = item.value(scope);
			if (v instanceof GamaField gf) {
				saveField(scope, gf, fw);
			} else {
				final ISpecies species = Cast.asSpecies(scope, v);
				if (species == null || !species.isGrid()) return;
				saveGrid(scope, species, fw);
			}
		}
	}

	/**
	 * Save grid.
	 *
	 * @param scope
	 *            the scope
	 * @param species
	 *            the species
	 * @param fw
	 *            the fw
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void saveGrid(final IScope scope, final ISpecies species, final Writer fw) throws IOException {

		StringBuilder headerBuilder = new StringBuilder();
		final GridPopulation gp = (GridPopulation) species.getPopulation(scope);
		final int nbCols = gp.getNbCols();
		final int nbRows = gp.getNbRows();
		headerBuilder.append("ncols         ").append(nbCols).append(Strings.LN);
		headerBuilder.append("nrows         ").append(nbRows).append(Strings.LN);

		final boolean nullProjection = scope.getSimulation().getProjectionFactory().getWorld() == null;
		headerBuilder.append("xllcorner     ")
				.append(nullProjection ? "0"
						: scope.getSimulation().getProjectionFactory().getWorld().getProjectedEnvelope().getMinX())
				.append(Strings.LN);
		headerBuilder.append("yllcorner     ")
				.append(nullProjection ? "0"
						: scope.getSimulation().getProjectionFactory().getWorld().getProjectedEnvelope().getMinY())
				.append(Strings.LN);
		final double dx = scope.getSimulation().getEnvelope().getWidth() / nbCols;
		final double dy = scope.getSimulation().getEnvelope().getHeight() / nbRows;
		if (Comparison.equal(dx, dy)) {
			headerBuilder.append("cellsize      ").append(dx).append(Strings.LN);
		} else {
			headerBuilder.append("dx            ").append(dx).append(Strings.LN);
			headerBuilder.append("dy            ").append(dy).append(Strings.LN);
		}
		fw.write(headerBuilder.toString());

		for (int i = 0; i < nbRows; i++) {
			StringBuilder val = new StringBuilder();
			for (int j = 0; j < nbCols; j++) { val.append(gp.getGridValue(j, i)).append(" "); }
			fw.write(val.append(Strings.LN).toString());
		}

	}

	/**
	 * Save field.
	 *
	 * @param scope
	 *            the scope
	 * @param field
	 *            the field
	 * @param fw
	 *            the fw
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void saveField(final IScope scope, final GamaField field, final Writer fw) throws IOException {

		if (field == null || field.isEmpty(scope)) return;

		StringBuilder theHeader = new StringBuilder();
		final int nbCols = field.numCols;
		final int nbRows = field.numRows;
		theHeader.append("ncols         ").append(nbCols).append(Strings.LN);
		theHeader.append("nrows         ").append(nbRows).append(Strings.LN);
		final boolean nullProjection = scope.getSimulation().getProjectionFactory().getWorld() == null;
		theHeader.append("xllcorner     ")
				.append(nullProjection ? "0"
						: scope.getSimulation().getProjectionFactory().getWorld().getProjectedEnvelope().getMinX())
				.append(Strings.LN);
		theHeader.append("yllcorner     ")
				.append(nullProjection ? "0"
						: scope.getSimulation().getProjectionFactory().getWorld().getProjectedEnvelope().getMinY())
				.append(Strings.LN);
		final double dx = scope.getSimulation().getEnvelope().getWidth() / nbCols;
		final double dy = scope.getSimulation().getEnvelope().getHeight() / nbRows;
		if (Comparison.equal(dx, dy)) {
			theHeader.append("cellsize      ").append(dx).append(Strings.LN);
		} else {
			theHeader.append("dx            ").append(dx).append(Strings.LN);
			theHeader.append("dy            ").append(dy).append(Strings.LN);
		}
		fw.write(theHeader.toString());

		for (int i = 0; i < nbRows; i++) {
			StringBuilder val = new StringBuilder();
			for (int j = 0; j < nbCols; j++) { val.append(field.get(scope, j, i)).append(" "); }
			fw.write(val.append(Strings.LN).toString());
		}

	}

	/**
	 * Compute file types.
	 *
	 * @return the string[]
	 */
	@Override
	public Set<String> computeFileTypes() {
		return Set.of("asc");
	}

}
