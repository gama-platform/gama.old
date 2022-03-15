package spll.datamapper.normalizer;

import java.util.Map;
import java.util.Random;

import core.util.random.GenstarRandom;
import spll.entity.SpllFeature;

/**
 * TODO: make top value control, not just floor value 
 * 
 * @author kevinchapuis
 *
 */
public abstract class ASPLNormalizer {
	
	protected static final int ITER_LIMIT = 1000000;
	protected static final double EPSILON = 0.001;
	public static boolean LOGSYSO = true;
	
	protected double floorValue;
	
	protected Number noData;
	
	protected final Random random = GenstarRandom.getInstance();
	
	/**
	 * TODO: javadoc
	 * 
	 * @param floorValue
	 * @param noData
	 */
	public ASPLNormalizer(double floorValue, Number noData){
		this.floorValue = floorValue;
		this.noData = noData;
	}
	
	/**
	 * Compound method to fit matrix requirement, i.e. floor value, integer value and sum output
	 * 
	 * @param matrix
	 * @param output
	 * @param integer
	 * @return
	 */
	public float[][] process(float[][] matrix, float output, boolean integer){
		this.normalize(matrix, output);
		if(integer)
			this.round(matrix, output);
		return matrix;
	}
	
	/**
	 * Compound method to fit map requirement, i.e. floor value, integer value and sum output
	 * 
	 * @param featureOutput
	 * @param output
	 * @param integer
	 * @return
	 */
	public Map<SpllFeature, ? extends Number> process(Map<SpllFeature, Double> featureOutput, double output, boolean integer){
		Map<SpllFeature, Double> outputMap = this.normalize(featureOutput, output);
		if(integer)
			return this.round(featureOutput, output);
		return outputMap;
	}
	
	/**
	 * Normalize the content of a pixel format spll output <br>
	 * HINT: {@code float} type is forced by Geotools implementation of raster file
	 * 
	 * 
	 * @param matrix
	 * @param output
	 * @return
	 */
	public abstract float[][] normalize(float[][] matrix, float output);
	
	/**
	 * Round the value of pixels to fit integer value (stay in float format)
	 * 
	 * @param matrix
	 * @param output
	 * @return
	 */
	public abstract float[][] round(float[][] matrix, float output);
	
	/**
	 * TODO
	 * 
	 * @param featureOutput
	 * @param output
	 * @return
	 */
	public abstract Map<SpllFeature, Double> normalize(Map<SpllFeature, Double> featureOutput, double output);
	
	/**
	 * Round double values to integer and control sum to fit required output
	 * 
	 * @param featureOutput
	 * @param output
	 * @return
	 */
	public abstract Map<SpllFeature, Integer> round(Map<SpllFeature, Double> featureOutput, double output);
	
	// ------------------ shared utility ------------------ //
	
	protected boolean equalEpsilon(float value, double target) {
		return Math.abs(value - target) < EPSILON ? true : false;
	}

	protected boolean equalEpsilon(double value, double target) {
		return Math.abs(value - target) < EPSILON ? true : false;
	}
	
}
