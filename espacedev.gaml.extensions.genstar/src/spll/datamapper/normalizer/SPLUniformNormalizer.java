package spll.datamapper.normalizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import core.util.stats.GSBasicStats;
import core.util.stats.GSEnumStats;
import spll.entity.SpllFeature;

public class SPLUniformNormalizer extends ASPLNormalizer {
	
	public SPLUniformNormalizer(double floorValue, Number noData) {
		super(floorValue, noData);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Two step upscale:
	 * <ul>
	 * <li>up values below floor
	 * <li>normalize to fit targeted total output
	 * </ul>
	 * <p>
	 * WARNING: {@code pixelOutput} must be a complete matrix <br>
	 * WARNING: parallel implementation
	 * 
	 */
	@Override
	public float[][] normalize(float[][] matrix, float output) {
		if((float) new GSBasicStats<>(GSBasicStats.transpose(matrix), 
				Arrays.asList(noData.doubleValue())).getStat(GSEnumStats.min)[0] < floorValue){
			IntStream.range(0, matrix.length).parallel()
				.forEach(col -> IntStream.range(0, matrix[col].length)
					.forEach(row -> matrix[col][row] = normalizedFloor(matrix[col][row]))
			);

			float floorSum = GSBasicStats.transpose(matrix)
					.parallelStream().filter(val -> val == floorValue)
					.reduce(0d, Double::sum).floatValue();
			float nonFloorSum = GSBasicStats.transpose(matrix)
					.parallelStream().filter(val -> val > floorValue && val != noData.floatValue())
					.reduce(0d, Double::sum).floatValue();
			float normalizer = (output - floorSum) / nonFloorSum;

			IntStream.range(0, matrix.length).parallel()
				.forEach(col -> IntStream.range(0, matrix[col].length)
					.forEach(row -> matrix[col][row] = normalizedFactor(matrix[col][row], normalizer))
			);
		}
		return matrix;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * WARNING: {@code pixelOutput} must be a complete matrix <br>
	 * WARNING: parallel implementation
	 * 
	 */
	@Override
	public float[][] round(float[][] matrix, float output){
		// normalize to int: values are rounded to feet most proximal integer & overload are summed
		IntStream.range(0, matrix.length).parallel()
			.forEach(col -> IntStream.range(0, matrix[col].length)
				.forEach(row -> matrix[col][row] = normalizedToInt(matrix[col][row]))
		);

		double errorload = output - new GSBasicStats<>(GSBasicStats.transpose(matrix), 
				Arrays.asList(noData.doubleValue())).getStat(GSEnumStats.sum)[0];
		
		// uniformally spread errorload (can visit pixel multiple time)
		int iter = 0;
		while(Math.round(errorload) != 0 || iter++ > ITER_LIMIT + Math.abs(errorload)){
			int intX, intY;
			float currentVal;
			int update = errorload < 0 ? -1 : 1;
			do {
				intX = super.random.nextInt(matrix.length);
				intY = super.random.nextInt(matrix[intX].length);
				currentVal = matrix[intX][intY];
			} while(currentVal == noData.floatValue()
					|| currentVal == floorValue && errorload < 0);
			matrix[intX][intY] = currentVal + update;
			errorload -= update;
		}
		return matrix;
	}

	/**
	 * 
	 */
	@Override
	public Map<SpllFeature, Double> normalize(Map<SpllFeature, Double> featureOutput, double output) {

		// Two step upscale: (1) up values below floor (2) normalize to fit targeted total output
		if(featureOutput.values()
				.parallelStream().min((v1, v2) -> v1.compareTo(v2)).get() < floorValue){
			featureOutput.keySet().parallelStream()
			.forEach(feature -> featureOutput.put(feature, 
					normalizedFloor(featureOutput.get(feature))));
			
			float floorSum = featureOutput.values()
					.parallelStream().filter(val -> val == floorValue)
					.reduce(0d, Double::sum).floatValue();
			float nonFloorSum = featureOutput.values()
					.parallelStream().filter(val -> val > floorValue && val != noData.floatValue())
					.reduce(0d, Double::sum).floatValue();
			double normalizer = (output - floorSum) / nonFloorSum;

			featureOutput.keySet().parallelStream()
				.forEach(feature -> featureOutput.put(feature, normalizedFactor(featureOutput.get(feature), normalizer)));

		}

		return featureOutput;
	}
	
	/**
	 * 
	 */
	@Override
	public Map<SpllFeature, Integer> round(Map<SpllFeature, Double> featureOutput, double output) {
		Map<SpllFeature, Integer> featOut = new HashMap<>();
		// summed residue is spread to all non floor value
		featureOutput.keySet().parallelStream()
			.forEach(feature -> featOut.put(feature, (int) normalizedToInt(featureOutput.get(feature))));

		double errorload = output - new GSBasicStats<>(new ArrayList<>(featOut.values()), 
				Arrays.asList(noData.doubleValue())).getStat(GSEnumStats.sum)[0];
		
		// uniformally spread overload (can visit pixel multiple time)
		int iter = 0;
		List<SpllFeature> feats = featureOutput.entrySet()
				.parallelStream().filter(e -> e.getValue() != noData.doubleValue())
				.map(e -> e.getKey())
				.toList();
		while(Math.round(errorload) != 0 || iter++ > ITER_LIMIT + Math.abs(errorload)){
			SpllFeature feat = feats.get(super.random.nextInt(feats.size()));
			int update = errorload < 0 ? -1 : 1;
			featureOutput.put(feat, featureOutput.get(feat) + update);
			errorload -= update;
		}
		return featOut;
	}

	// ---------------------- inner utility ---------------------- //

	private float normalizedFloor(float value) {
		if(value < floorValue && value != noData.floatValue())
			return (float) floorValue;
		return value;
	}

	private double normalizedFloor(double value) {
		if(value < floorValue && value != noData.floatValue())
			return floorValue;
		return value;
	}
	
	private float normalizedFactor(float value, float factor){
		if(value > floorValue && value != noData.floatValue())
			return value * factor;
		return value;
	}
	
	private double normalizedFactor(double value, double factor){
		if(value > floorValue && value != noData.floatValue())
			return value * factor;
		return value;
	}

	private float normalizedToInt(float value){
		if(value == noData.floatValue())
			return value;
		float newValue = Math.round(value); 
		if(newValue < floorValue)
			newValue = (int) value + 1;
		return newValue;
	}

	private double normalizedToInt(double value){
		if(value == noData.doubleValue())
			return value;
		double newValue = Math.round(value); 
		if(newValue < floorValue)
			newValue = (int) value + 1;
		return newValue;
	}

}
