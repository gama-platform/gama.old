/*******************************************************************************************************
 *
 * MatrixBasedGenerator.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package espacedev.gaml.extensions.genstar.generator;

import static espacedev.gaml.extensions.genstar.utils.GenStarConstant.EPSILON;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.Functions;

import espacedev.gaml.extensions.genstar.statement.GenerateStatement;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gama.util.matrix.GamaFloatMatrix;
import msi.gama.util.matrix.GamaIntMatrix;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.operators.Maths;
import msi.gaml.operators.Random;
import msi.gaml.statements.Arguments;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * A generator that uses a gama matrix to register a distribution over two attributes
 *
 * @author kevinchapuis
 *
 */
public class MatrixBasedGenerator implements IGenstarGenerator {

	/** The instance. */
	private static MatrixBasedGenerator INSTANCE = new MatrixBasedGenerator();

	/** The type. */
	@SuppressWarnings ("rawtypes") IType type;

	/**
	 * Instantiates a new matrix based generator.
	 */
	private MatrixBasedGenerator() {
		type = Types.MATRIX;
	}

	/**
	 * Gets the single instance of MatrixBasedGenerator.
	 *
	 * @return single instance of MatrixBasedGenerator
	 */
	public static MatrixBasedGenerator getInstance() { return INSTANCE; }

	@SuppressWarnings ("rawtypes")
	@Override
	public IType sourceType() {
		return type;
	}

	@Override
	public boolean sourceMatch(final IScope scope, final Object source) {
		return source instanceof GamaFloatMatrix || source instanceof GamaIntMatrix;
	}

	@SuppressWarnings ({ "unchecked", "rawtypes" })
	@Override
	public void generate(final IScope scope, final List<Map<String, Object>> inits, final Integer max,
			final Object source, final Object attributes, final Object algo, final Arguments init,
			final GenerateStatement generateStatement) {

		IAgent executor = scope.getAgent();
		msi.gama.metamodel.population.IPopulation<? extends IAgent> gamaPop =
				executor.getPopulationFor(generateStatement.getDescription().getSpeciesContext().getName());

		IMap<String, IList<String>> atts = (IMap<String, IList<String>>) attributes;
		GamaFloatMatrix mat = GamaFloatMatrix.from(scope, (IMatrix) source);

		// Infer a distribution based on the matrix
		IMap<List<String>, Double> distrib = getDistributionFromMatrix(scope, mat, atts);

		// Infer type of gama value
		Map<String, IType> gamaT = atts.keySet().stream()
				.collect(Collectors.toMap(Functions.identity(), a -> gamaPop.getVar(a).getType()));

		int nb = max == null ? inferGenerateNumber(mat) : max;
		for (int i = 0; i < nb; i++) {
			final Map<String, Object> map = GamaMapFactory.create(Types.STRING, Types.NO_TYPE);
			List<String> vals = Random.opRndCoice(scope, distrib);
			for (String a : atts.keySet()) {
				String vs = vals.stream().filter(v -> atts.get(a).contains(v)).findFirst().get();
				map.put(a, gamaT.get(a).cast(scope, vs, null, false));
			}
			generateStatement.fillWithUserInit(scope, map);
			inits.add(map);
		}

	}

	/**
	 * Gets the distribution from matrix.
	 *
	 * @param scope
	 *            the scope
	 * @param mat
	 *            the mat
	 * @param atts
	 *            the atts
	 * @return the distribution from matrix
	 */
	@SuppressWarnings ("unchecked")
	private IMap<List<String>, Double> getDistributionFromMatrix(final IScope scope, final GamaFloatMatrix mat,
			final IMap<String, IList<String>> atts) {

		IMap<List<String>, Double> distr = GamaMapFactory.create();

		// Matrix Col is 0 - find corresponding attribute
		// Matrix Row is 1 - find corresponding attribute
		String colAtt = null, rowAtt = null;
		for (String att : atts.getKeys()) {
			if (mat.numCols == atts.get(att).size() && colAtt == null) {
				colAtt = att;
			} else if (mat.numRows == atts.get(att).size() && rowAtt == null) {
				rowAtt = att;
			} else {
				GamaRuntimeException.error("Attribute " + att + " values (" + atts.get(att) + ") mismatch matrix size "
						+ mat.numCols + ":" + mat.numRows, scope);
			}
		}

		for (int col = 0; col < mat.numCols; col++) {
			for (int row = 0; row < mat.numRows; row++) {
				distr.put(Arrays.asList(atts.get(colAtt).get(col), atts.get(rowAtt).get(row)),
						mat.get(scope, col, row));
			}
		}

		return distr;
	}

	/**
	 * Infer generate number.
	 *
	 * @param mat
	 *            the mat
	 * @return the int
	 */
	private int inferGenerateNumber(final GamaFloatMatrix mat) {
		double s = Arrays.stream(mat.getMatrix()).sum();
		return Math.abs(s - 1.0) < EPSILON ? 1 : Maths.round(s);
	}

}
