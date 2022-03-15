package spll.datamapper;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import core.metamodel.entity.AGeoEntity;
import core.metamodel.io.IGSGeofile;
import core.metamodel.value.IValue;
import spll.algo.ISPLRegressionAlgo;
import spll.algo.exception.IllegalRegressionException;
import spll.datamapper.matcher.ISPLMatcher;
import spll.datamapper.matcher.ISPLMatcherFactory;
import spll.datamapper.variable.ISPLVariable;

/**
 * TODO: force <T> generic to fit a regression style contract: either boolean (variable is present or not) 
 * or numeric (variable has a certain amount)
 * <p>
 * This object purpose is to setup a regression between a variable contains in a main geographic file
 * with variables contains in one or more ancillary geographic files. The mapping is based on a shared
 * geographical referent space: all file must use the same {@link CoordinateReferenceSystem} and overlap
 * -- at least only overlapped places will be processed.
 * <p> 
 * This object should be created using any {@link ASPLMapperBuilder}
 * 
 * @author kevinchapuis
 *
 * @param <F>
 * @param <Variable>
 * @param <T>
 */
public class SPLMapper<Variable extends ISPLVariable, T> {

	private ISPLRegressionAlgo<Variable, T> regFunction;
	private boolean setupReg;
	
	private ISPLMatcherFactory<Variable, T> matcherFactory;

	private IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue> mainSPLFile;
	private String targetProp;

	private Set<ISPLMatcher<Variable, T>> mapper = new HashSet<>();

	// --------------------- Constructor --------------------- //

	protected SPLMapper() { }

	// --------------------- Modifier --------------------- //

	protected void setRegAlgo(ISPLRegressionAlgo<Variable, T> regressionAlgorithm) {
		this.regFunction = regressionAlgorithm;
	}

	protected void setMatcherFactory(ISPLMatcherFactory<Variable, T> matcherFactory){
		this.matcherFactory = matcherFactory;
	}

	protected void setMainSPLFile(IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue> mainSPLFile){
		this.mainSPLFile = mainSPLFile;
	}

	protected void setMainProperty(String propertyName){
		this.targetProp = propertyName;
	}

	protected boolean insertMatchedVariable(IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue> regressorsFiles) 
			throws IOException, TransformException, InterruptedException, ExecutionException{
		boolean result = true;
		for(ISPLMatcher<Variable, T> matchedVariable : matcherFactory
				.getMatchers(mainSPLFile.getGeoEntity(), regressorsFiles))
			if(!insertMatchedVariable(matchedVariable) && result)
				result = false;
		return result;
	}

	protected boolean insertMatchedVariable(ISPLMatcher<Variable, T> matchedVariable) {
		return mapper.add(matchedVariable);
	}


	// --------------------- Accessor --------------------- //

	public Collection<? extends AGeoEntity<? extends IValue>> getAttributes() throws IOException{
		return mainSPLFile.getGeoEntity();
	}

	public Map<AGeoEntity<? extends IValue>, Set<ISPLMatcher<Variable, T>>> getVarMatrix() throws IOException {
		return getAttributes().stream().collect(Collectors.toMap(
				feat -> feat, 
				feat -> mapper.parallelStream().filter(map -> map.getEntity().equals(feat))
				.collect(Collectors.toSet())));
	}

	public Set<ISPLMatcher<Variable, T>> getVariableSet() {
		return Collections.unmodifiableSet(mapper);
	}

	// ------------------- Main Contract ------------------- //

	/**
	 * Gives the intercept of the regression
	 * 
	 * @return
	 * @throws IllegalRegressionException
	 * @throws IOException 
	 */
	public double getIntercept() throws IllegalRegressionException, IOException {
		this.setupRegression();
		return regFunction.getIntercept();
	}
	
	/**
	 * Operate regression given the data that have been setup for this mapper
	 * 
	 * @return
	 * @throws IllegalRegressionException
	 * @throws IOException 
	 */
	public Map<Variable, Double> getRegression() throws IllegalRegressionException, IOException {
		this.setupRegression();
		return regFunction.getRegressionParameter();
	}
	
	/**
	 * 
	 * TODO javadoc
	 * 
	 * @return
	 * @throws IllegalRegressionException
	 * @throws IOException 
	 */
	public Map<AGeoEntity<? extends IValue>, Double> getResidual() throws IllegalRegressionException, IOException {
		this.setupRegression();
		return regFunction.getResidual();
	}

	// ------------------- Inner utilities ------------------- //
	
	private void setupRegression() throws IllegalRegressionException, IOException{
		if(mapper.stream().anyMatch(var -> !var.getEntity().getPropertiesAttribute().contains(this.targetProp)))
			throw new IllegalRegressionException("Property "+this.targetProp+" is not present in each Feature of the main SPLMapper");
		if(mapper.stream().anyMatch(var -> !var.getEntity().getValueForAttribute(this.targetProp).getType().isNumericValue()))
			throw new IllegalArgumentException("Property value must be of numerical type in order to setup regression on");
		if(!setupReg){
			Collection<? extends AGeoEntity<? extends IValue>> geoData = mainSPLFile.getGeoEntity();
			regFunction.setupData(geoData.stream().collect(Collectors.toMap(feat -> feat, 
					feat -> feat.getNumericValueForAttribute(this.targetProp).doubleValue())), mapper);
			setupReg = true;
		}
	}
	
}
