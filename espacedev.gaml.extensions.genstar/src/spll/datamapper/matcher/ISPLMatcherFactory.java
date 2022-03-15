package spll.datamapper.matcher;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.opengis.referencing.operation.TransformException;

import core.metamodel.entity.AGeoEntity;
import core.metamodel.io.IGSGeofile;
import core.metamodel.value.IValue;
import spll.datamapper.variable.ISPLVariable;

public interface ISPLMatcherFactory<V extends ISPLVariable, T> {

	public List<ISPLMatcher<V, T>> getMatchers(AGeoEntity<? extends IValue> geoData, 
			IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue> ancillaryFile) 
					throws IOException, TransformException, InterruptedException, ExecutionException;

	public List<ISPLMatcher<V, T>> getMatchers(Collection<? extends AGeoEntity<? extends IValue>> geoData, 
			IGSGeofile<? extends AGeoEntity<? extends IValue>, ? extends IValue> regressorsFiles) 
					throws IOException, TransformException, InterruptedException, ExecutionException;
	
}
