package idees.gama.types;

import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.IValue;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import weka.classifiers.Classifier;
import weka.core.Instances;

public class GamaClassifier  implements IValue{
	private Classifier classifier;
	private Instances dataset;

	private Map<String,IList<String>> valsNominal;
	
	
	@Override
	public String serialize(boolean includingBuiltIn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IType<?> getType() {
		return Types.get(GamaClassifierType.id);
	}

	@Override
	public String stringValue(IScope scope) throws GamaRuntimeException {
		if (classifier == null) return "empty classifier";
		return classifier.toString();
	}

	@Override
	public IValue copy(IScope scope) throws GamaRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	public Classifier getClassifier() {
		return classifier;
	}

	public void setClassifier(Classifier classifier) {
		this.classifier = classifier;
	}

	public Instances getDataset() {
		return dataset;
	}

	public void setDataset(Instances dataset) {
		this.dataset = dataset;
	}

	public Map<String, IList<String>> getValsNominal() {
		return valsNominal;
	}

	public void setValsNominal(Map<String, IList<String>> valsNominal) {
		this.valsNominal = valsNominal;
	}

	
	
}
