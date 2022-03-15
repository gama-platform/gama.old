package spll.datamapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.opengis.referencing.operation.TransformException;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.operation.buffer.BufferParameters;
import org.locationtech.jts.precision.GeometryPrecisionReducer;

import core.metamodel.entity.AGeoEntity;
import core.metamodel.io.IGSGeofile;
import core.metamodel.value.IValue;
import core.metamodel.value.numeric.ContinuousValue;
import core.util.GSPerformanceUtil;
import spll.algo.ISPLRegressionAlgo;
import spll.algo.LMRegressionOLS;
import spll.algo.exception.IllegalRegressionException;
import spll.datamapper.exception.GSMapperException;
import spll.datamapper.matcher.SPLAreaMatcherFactory;
import spll.datamapper.normalizer.ASPLNormalizer;
import spll.datamapper.normalizer.SPLUniformNormalizer;
import spll.datamapper.variable.SPLVariable;
import spll.entity.SpllFeature;
import spll.entity.SpllPixel;
import spll.io.SPLRasterFile;
import spll.io.SPLVectorFile;

/**
 * TODO: javadoc
 * 
 * @author kevinchapuis
 *
 */
public class SPLAreaMapperBuilder extends ASPLMapperBuilder<SPLVariable, Double> {

	private SPLMapper<SPLVariable, Double> mapper;

	/**
	 * Simplest constructor that define default regression and normalizer algorithm
	 * 
	 * @param mainFile
	 * @param ancillaryFiles
	 * @param variables
	 */
	public SPLAreaMapperBuilder(IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue> mainFile, String mainAttribute,
			List<IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue>> ancillaryFiles, 
			Collection<? extends IValue> variables) {
		this(mainFile, mainAttribute, ancillaryFiles, variables, new LMRegressionOLS());
	}
	
	/**
	 * Constructor that enable custom regression algorithm
	 * 
	 * @param mainFile
	 * @param ancillaryFiles
	 * @param variables
	 * @param regAlgo
	 */
	public SPLAreaMapperBuilder(IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue> mainFile, String mainAttribute,
			List<IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue>> ancillaryFiles, 
			Collection<? extends IValue> variables, 
			ISPLRegressionAlgo<SPLVariable, Double> regAlgo) {
		this(mainFile, mainAttribute, ancillaryFiles, variables, regAlgo, 
				new SPLUniformNormalizer(0, SPLRasterFile.DEF_NODATA));
	}
	
	/**
	 * Constructor that enable full custom density estimation process
	 * 
	 * @param mainFile
	 * @param ancillaryFiles
	 * @param variables
	 * @param regAlgo
	 * @param normalizer
	 */
	public SPLAreaMapperBuilder(IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue> mainFile, String mainAttribute,
			List<IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue>> ancillaryFiles, 
			Collection<? extends IValue> variables, 
			ISPLRegressionAlgo<SPLVariable, Double> regAlgo,
			ASPLNormalizer normalizer) {
		super(mainFile, mainAttribute, ancillaryFiles);
		super.setRegressionAlgorithm(regAlgo);
		super.setMatcherFactory(new SPLAreaMatcherFactory(variables));
		super.setNormalizer(normalizer);
	}
	
	/////////////////////////////////////////////////////
	// :::::::::::::::: MAIN CONTRACT :::::::::::::::: // 
	/////////////////////////////////////////////////////
	
	@Override
	public SPLMapper<SPLVariable, Double> buildMapper() throws IOException, TransformException, InterruptedException, ExecutionException {
		if(mapper == null){
			mapper = new SPLMapper<>();
			mapper.setMainSPLFile(mainFile);
			mapper.setMainProperty(super.getMainAttribute());
			mapper.setRegAlgo(regressionAlgorithm);
			mapper.setMatcherFactory(matcherFactory);
			for(IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue> file : ancillaryFiles)
				mapper.insertMatchedVariable(file);
		}
		return mapper;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * WARNING: for performance purpose parallel stream are used !
	 * @throws GSMapperException 
	 * @throws IOException 
	 * 
	 */
	@Override
	protected float[][] buildOutput(SPLRasterFile outputFormat, boolean intersect, boolean integer, Number targetPop) 
			throws IllegalRegressionException, TransformException, 
			IndexOutOfBoundsException, GSMapperException, IOException {
		if(mapper == null)
			throw new IllegalAccessError("Cannot create output before a SPLMapper has been built and regression done");
		if(!ancillaryFiles.contains(outputFormat))
			throw new IllegalArgumentException("output format file must be one of ancillary files use to proceed regression");

		GSPerformanceUtil gspu = new GSPerformanceUtil("Start processing regression data to output raster");

		// Define output format
		int rows = outputFormat.getRowNumber();
		int columns = outputFormat.getColumnNumber();
		float[][] pixels = new float[columns][rows];

		// Store regression result
		Map<SPLVariable, Double> regCoef = mapper.getRegression();
		double intercept = mapper.getIntercept();
		
		// Correction for each pixel (does not exclude noData pixels)
		Map<AGeoEntity<? extends IValue>, Double> pixCorrection = mapper.getResidual().entrySet()
				.stream().collect(Collectors.toMap(e -> e.getKey(), 
					e -> (e.getValue() + intercept) / outputFormat.getGeoEntityWithin(e.getKey().getGeometry()).size()));
		
		if(pixCorrection.values().stream().anyMatch(value -> value.isInfinite() || value.isNaN()))
			throw new GSMapperException(outputFormat.toString()+" output format file does not cover all geographical entity !\n"+
					Arrays.toString(pixCorrection.entrySet().stream().filter(e -> e.getValue().isInfinite() || e.getValue().isNaN())
					.map(e -> e.getKey().getGenstarName()+" - "+e.getValue()).toArray()));
			
		// Define utilities
		Collection<IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue>> ancillaries = new ArrayList<>(super.ancillaryFiles);
		Collection<? extends AGeoEntity<? extends IValue>> mainGeoData = super.mainFile.getGeoEntity();
		ancillaries.remove(outputFormat);

		// Iterate over pixels to apply regression coefficient
		IntStream.range(0, columns).parallel().forEach(
				x -> IntStream.range(0, rows).forEach(
						y -> pixels[x][y] = (float) this.computePixelWithinOutput(x, y, outputFormat,  ancillaries,
								mainGeoData, regCoef, pixCorrection, gspu, intersect)
						)
				);
		
		// debug purpose
		pixelRendered = 0;
		
		super.normalizer.process(pixels, targetPop.floatValue(), integer);
		
		return pixels;
	}

	@Override
	protected Map<SpllFeature, Number> buildOutput(SPLVectorFile formatFile, boolean intersect, boolean integer, Number tagetPopulation) {
		// TODO Auto-generated method stub
		
		if(mapper == null)
			throw new IllegalAccessError("Cannot create output before a SPLMapper has been built and regression done");
		if(!ancillaryFiles.contains(formatFile))
			throw new IllegalArgumentException("output format file must be one of ancillary files use to proceed regression");
		
		return null;
	}

	/////////////////////////////////////////////////////////////////////////////
	// --------------------------- INNER UTILITIES --------------------------- //
	/////////////////////////////////////////////////////////////////////////////
	
	// INNER UTILITY PIXEL PROCESS COUNT
	private static int pixelRendered = 0;
	
	private double computePixelWithinOutput(int x, int y, SPLRasterFile geotiff, 
			Collection<IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue>> ancillaries,
			Collection<? extends AGeoEntity<? extends IValue>> mainFeatures, 
					Map<SPLVariable, Double> regCoef, Map<AGeoEntity<? extends IValue>, Double> pixResidual,
			GSPerformanceUtil gspu, boolean intersect) {
		// Output progression
		int prop10for100 = Math.round(Math.round(geotiff.getRowNumber() * geotiff.getColumnNumber() * 0.1d));
		if((++pixelRendered+1) % prop10for100 == 0)
			gspu.sysoStempPerformance((pixelRendered+1) / (prop10for100 * 10.0), this);

		// Get the current pixel value
		SpllPixel refPixel = null;
		try {
			refPixel = geotiff.getPixel(x, y);
		} catch (TransformException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// Get the related feature in main space features
		Point pixelLocation = refPixel.getLocation();
		Optional<? extends AGeoEntity<? extends IValue>> opFeature = mainFeatures.stream()
				.filter(ft -> pixelLocation.within(ft.getGeometry())).findFirst();

		if(!opFeature.isPresent())
			return SPLRasterFile.DEF_NODATA.floatValue();
		if(intersect)
			return computePixelIntersectOutput(refPixel, geotiff, ancillaries, mainFeatures, regCoef, pixResidual);
		return computePixelWithin(refPixel, geotiff, ancillaries, opFeature.get(), regCoef, pixResidual.get(opFeature.get()));
		
	}
	
	// --------------------------- INNER ALGORITHM --------------------------- //
	
	/*
	 * WARNING: the within function used define inclusion as: 
	 * centroide of {@code refPixel} geometry is within the referent geometry
	 */
	private double computePixelWithin(SpllPixel refPixel, SPLRasterFile geotiff, 
			Collection<IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue>> ancillaries,
			AGeoEntity<? extends IValue> entity, Map<SPLVariable, Double> regCoef, double corCoef){

		// Retain info about pixel and his context
		Geometry pixGeom = refPixel.getGeometry();
		Collection<ContinuousValue> pixData = refPixel.getValues();
		Collection<IValue> coefVal = regCoef.keySet()
				.stream().map(var -> var.getValue()).collect(Collectors.toSet());
		if(pixData.stream().allMatch(val -> val.getActualValue() == geotiff.getNoDataValue() || !coefVal.contains(val)) && ancillaries.isEmpty())
			return SPLRasterFile.DEF_NODATA.floatValue();
		double pixArea = refPixel.getArea();

		// Setup output value for the pixel based on pixels' band values
		double output = regCoef.entrySet().stream().filter(var -> pixData.contains(var.getKey().getValue()))
				.mapToDouble(var -> var.getValue() * pixArea).sum();

		// Iterate over other explanatory variables to update pixels value
		for(IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue> otherExplanVarFile : ancillaries){
			Iterator<? extends AGeoEntity<? extends IValue>> otherItt = otherExplanVarFile
					.getGeoEntityIteratorWithin(pixGeom);
			while(otherItt.hasNext()){
				AGeoEntity<? extends IValue> other = otherItt.next();
				Set<SPLVariable> otherValues = regCoef.keySet()
						.stream().filter(var -> other.getValues().contains(var.getValue()))
						.collect(Collectors.toSet());
				output += otherValues.stream().mapToDouble(val -> regCoef.get(val) * pixArea).sum();
			}
		}

		return output + corCoef;
	}

	/*
	 * WARNING: intersection area calculation is very computation demanding, so this method is pretty slow 
	 */
	private double computePixelIntersectOutput(SpllPixel refPixel, SPLRasterFile geotiff, 
			Collection<IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue>> ancillaries,
			Collection<? extends AGeoEntity<? extends IValue>> mainFeatures, 
			Map<SPLVariable, Double> regCoef, Map<AGeoEntity<? extends IValue>, Double> pixResidual) {

		// Retain main feature the pixel is within
		Geometry pixGeom = refPixel.getGeometry(); 
		List<? extends AGeoEntity<? extends IValue>> feats = mainFeatures.stream()
				.filter(ft -> ft.getGeometry().intersects(pixGeom))
				.collect(Collectors.toList());
		if(feats.isEmpty())
			return SPLRasterFile.DEF_NODATA.floatValue();

		// Get the values contain in the pixel bands
		Collection<ContinuousValue> pixData = refPixel.getValues();
		double pixArea = refPixel.getArea();

		// Setup output value for the pixel based on pixels' band values
		double output = regCoef.entrySet().stream().filter(var -> pixData.contains(var.getKey().getValue()))
				.mapToDouble(var -> var.getValue() * pixArea).sum();

		// Iterate over other explanatory variables to update pixels value
		for(IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue> otherExplanVarFile : ancillaries){
			Iterator<? extends AGeoEntity<? extends IValue>> otherItt = otherExplanVarFile
					.getGeoEntityIteratorIntersect(pixGeom);
			while(otherItt.hasNext()){
				AGeoEntity<? extends IValue> other = otherItt.next();
				Set<SPLVariable> otherValues = regCoef.keySet()
						.stream().filter(var -> other.getValues().contains(var.getValue()))
						.collect(Collectors.toSet());
				output += otherValues.stream().mapToDouble(val -> 
				regCoef.get(val) * other.getGeometry().intersection(pixGeom).getArea()).sum();
			}
		}

		// Compute corrected value based on output data (to balanced for unknown determinant information)
		// Intersection correction try /catch clause come from GAMA
		float correctedOutput = 0f; 
		for(AGeoEntity<? extends IValue> entity : feats){
			Geometry fGeom = entity.getGeometry();
			Geometry intersectGeom = null;
			try {
				intersectGeom = fGeom.intersection(pixGeom);
			} catch (final Exception ex) {
				try {
					final PrecisionModel pm = new PrecisionModel(PrecisionModel.FLOATING_SINGLE);
					intersectGeom = GeometryPrecisionReducer.reducePointwise(fGeom, pm)
							.intersection(GeometryPrecisionReducer.reducePointwise(pixGeom, pm));
				} catch (final Exception e) {
					// AD 12/04/13 : Addition of a third method in case of
					// exception
					try {
						intersectGeom = fGeom.buffer(0.01, BufferParameters.DEFAULT_QUADRANT_SEGMENTS, BufferParameters.CAP_FLAT)
								.intersection(pixGeom.buffer(0.01, BufferParameters.DEFAULT_QUADRANT_SEGMENTS,
										BufferParameters.CAP_FLAT));
					} catch (final Exception e2) {
						intersectGeom = null;
					}
				}
			}
			correctedOutput += Math.round(output * intersectGeom.getArea() / pixGeom.getArea() + pixResidual.get(entity));
		}
		return correctedOutput;
	}

}
