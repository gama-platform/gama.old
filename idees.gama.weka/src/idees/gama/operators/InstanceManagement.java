package idees.gama.operators;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMap;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gaml.operators.Cast;
import msi.gaml.types.Types;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class InstanceManagement {
	
	private static Attribute toAttribute(String att, final Map<String,IList<String>> valsNominal) {
		Attribute wa = null;
		if (valsNominal == null || !valsNominal.containsKey(att))
			wa = new Attribute(att);
		else {
			IList<String> vs = valsNominal.get(att);
			if (vs == null || vs.isEmpty()) {
				wa =new Attribute(att,(FastVector) null);
			} else {
				FastVector fv = new FastVector();
				for ( String v : vs ) fv.addElement(v);
				wa = new Attribute(att,fv);
			} 
		}
		return wa;
	}
	
	private static FastVector initAttributes(final IScope scope,final IList<String> attributes, final Map<String,IList<String>> valsNominal){
		FastVector attribs = new FastVector();
		for ( String att : attributes ) {
			attribs.addElement(toAttribute(att,valsNominal));
		}
		return attribs;
	}
		
	public static Instances convertToInstances(final IScope scope, final String classAtt,final IList<String> attributes, final Map<String,IList<String>> valsNominal, 
			final IContainer data) throws GamaRuntimeException {
		FastVector attribs = initAttributes(scope,attributes, valsNominal);
		Attribute classA = (classAtt == null) ? null : toAttribute(classAtt, valsNominal);
		
		if (classA != null) {
			attribs.addElement(classA);
		}
		Instances dataset = new Instances(scope.getAgent().getName(), attribs, data.length(scope));
		if (classA != null) {
			dataset.setClassIndex(attribs.size()-1);
		}
		
		if (!data.isEmpty(scope)) {
			if (data.firstValue(scope) instanceof IAgent) {
				IList<IAgent> ags = data.listValue(scope, Types.AGENT, false) ;
				for ( IAgent ag : ags) {
					dataset.add(createInstance(scope,ag,dataset,classA,valsNominal));
				}
			} else {
				for (Object v : data.iterable(scope)) {
					if (v instanceof GamaMap) {
						final GamaMap dataMap = (GamaMap) v;
						dataset.add(createInstance(scope,dataMap,dataset,classA,valsNominal));
					}
				}
			}
		}
		return dataset;
	}
	public static Instance createInstance(IScope scope, Map dataMap, Instances dataset, Attribute classA, Map<String,IList<String>> valsNominal) {
		Instance instance = new Instance(dataset.numAttributes());
		instance.setDataset(dataset);
		Enumeration<Attribute> attsEnum = dataset.enumerateAttributes();
		while (attsEnum.hasMoreElements()) {
			Attribute att = attsEnum.nextElement();
			Double var = null;
			if (valsNominal == null || valsNominal.isEmpty()) 
				var = Cast.asFloat(scope,dataMap.get(att.name()));
			else {
				IList<String> vs = valsNominal.get(att.name());
				if (vs != null && !vs.isEmpty()) {
					String val =dataMap.get(att.name()).toString();
					var = Double.valueOf(vs.indexOf(val));
				} else 
					var = Cast.asFloat(scope, dataMap.get(att.name()));
			}
			instance.setValue(att, var);
		}
		if (classA != null) {
			IList<String> vs = valsNominal.get(classA.name());
			Double var = null;
			if (valsNominal == null || valsNominal.isEmpty()) 
				var = Cast.asFloat(scope, dataMap.get(classA.name()));
			else {
				if (vs != null && !vs.isEmpty()) {
					String val = dataMap.get(classA.name()).toString();
					var = Double.valueOf(vs.indexOf(val));
				} else 
					var = Cast.asFloat(scope,dataMap.get(classA.name()));
			}
			instance.setClassValue(var);
		}
		return instance;
	}
	public static Instance createInstance(IScope scope, IAgent ag, Instances dataset, Attribute classA, Map<String,IList<String>> valsNominal) {
		Instance instance = new Instance(dataset.numAttributes());
		instance.setDataset(dataset);
		Enumeration<Attribute> attsEnum = dataset.enumerateAttributes();
		while (attsEnum.hasMoreElements()) {
			Attribute att = attsEnum.nextElement();
			Double var = null;
			if (valsNominal == null || valsNominal.isEmpty()) 
				var = Cast.asFloat(scope, ag.getDirectVarValue(scope, att.name()));
			else {
				IList<String> vs = valsNominal.get(att.name());
				if (vs != null && !vs.isEmpty()) {
					String val = ag.getDirectVarValue(scope, att.name()).toString();
					var = Double.valueOf(vs.indexOf(val));
				} else 
					var = Cast.asFloat(scope, ag.getDirectVarValue(scope, att.name()));
			}
			instance.setValue(att, var);
		}
		if (classA != null) {
			IList<String> vs = valsNominal.get(classA.name());
			Double var = null;
			if (valsNominal == null || valsNominal.isEmpty()) 
				var = Cast.asFloat(scope, ag.getDirectVarValue(scope, classA.name()));
			else {
				if (vs != null && !vs.isEmpty()) {
					String val = ag.getDirectVarValue(scope, classA.name()).toString();
					var = Double.valueOf(vs.indexOf(val));
				} else 
					var = Cast.asFloat(scope, ag.getDirectVarValue(scope, classA.name()));
			}
			instance.setClassValue(var);
		}
		return instance;
	}

}
