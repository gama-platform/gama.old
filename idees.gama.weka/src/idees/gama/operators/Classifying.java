package idees.gama.operators;

import java.util.Hashtable;
import java.util.Map;

import idees.gama.algorithms.CHAID;
import idees.gama.types.GamaClassifier;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.GamaMap;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;

public class Classifying {
	
	
	private static GamaClassifier buildClassifier(Classifier classifWeka,Instances dataset ,final IList<String> attributes,  final Map<String,IList<String>> valsNominal ) {
		GamaClassifier classifier = new GamaClassifier();
		classifier.setClassifier(classifWeka);
		classifier.setDataset(dataset);
		classifier.setValsNominal(valsNominal);
		return classifier;
	}
	
	@operator(value = { "train_chaid" }, content_type = IType.LIST, category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc(value = "Build and train a CHAID classifier; use: train_chaid(data, list of attributes, name of the class, for each nominal attribute and the class, their possible values, the parameters);"
			+ "two parameters can be defined: binary_split: Binary splits on nominal attributes? (default: false); split_point: split point value (default: 0.05)",
		examples = { @example(value = "train_j48(data, [ \"weight\", \"size\"],\"sexe\",[\"sexe\"::[\"M\", \"F\"]],map([binary_split::true, split_point::0.03]));",
			isExecutable = false) },
		see = {"classify", "train_j48"})
	public static
		GamaClassifier primBuildCHAIDClassifier(final IScope scope,
				final IContainer data, final IList<String> attributes, final String classIndex, final Map<String,IList<String>> valsNominal,
				final GamaMap<String, Object> parameters) throws GamaRuntimeException {
			
			Instances dataset = InstanceManagement.convertToInstances(scope,classIndex, attributes,valsNominal, data);
			CHAID chaid = new CHAID();
			if (parameters != null) {
				Boolean binarySplit = Cast.asBool(scope, parameters.get("binary_split"));
				Double splitPoint = Cast.asFloat(scope, parameters.get("split_point"));
				if (binarySplit != null)
					chaid.setBinarySplits(binarySplit);
				if (splitPoint != null)
					chaid.setSplitPoint(splitPoint);
			}
			try {
				chaid.buildClassifier(dataset);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return buildClassifier(chaid,dataset,attributes,valsNominal);
		}
	
	
	@operator(value = { "train_j48" }, content_type = IType.LIST, category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc(value = "Build and train a J48 classifier; use: train_j48(data, list of attributes, name of the class, for each nominal attribute and the class, their possible values, the parameters);"
			+ "Eight parameters can be defined: binary_split: Binary splits on nominal attributes? (default: false); "
			+ "unpruned: Unpruned tree? (default: false); "
			+ "reduced_error_pruning: Use reduced error pruning? (default: false); "
			+ "sub_tree_raising: Subtree raising to be performed? (default: false); "
			+ "laplace: Determines whether probabilities are smoothed using Laplace correction when predictions are generated (default: false); "
			+ "min_nb_obj: Minimum number of instances (default: 2); "
			+ "numFolds: Number of folds for reduced error pruning (default: 3); "
			+ "confidence_factor: Confidence level (default: 0.25)",
		examples = { @example(value = "train_j48(data, [ \"weight\", \"size\"],\"sexe\",[\"sexe\"::[\"M\", \"F\"]],map([unpruned::true]));",
			isExecutable = false) },
		see = { "classify","train_chaid" })
	public static
	GamaClassifier primBuildJ48Classifier(final IScope scope,
			final IContainer data, final IList<String> attributes, final String classIndex, final Map<String,IList<String>> valsNominal,
			final GamaMap<String, Object> parameters) throws GamaRuntimeException {
		Instances dataset = InstanceManagement.convertToInstances(scope,classIndex, attributes,valsNominal, data);
		J48 j48 = new J48();
		j48.setSeed(scope.getRandom().getSeed().intValue());
		if (parameters != null) {
			Boolean binarySplit = Cast.asBool(scope, parameters.get("binary_split"));
			Double confidenceFactor = Cast.asFloat(scope, parameters.get("confidence_factor"));
			Integer minNumObj = Cast.asInt(scope,  parameters.get("min_nb_obj"));
			Boolean reduceError = Cast.asBool(scope, parameters.get("reduced_error_pruning"));
			Boolean subtreeRaising = Cast.asBool(scope, parameters.get("sub_tree_raising"));
			Boolean unpruned = Cast.asBool(scope, parameters.get("unpruned"));
			Boolean useLaplace = Cast.asBool(scope, parameters.get("laplace"));
			Integer numFolds = Cast.asInt(scope,  parameters.get("num_folds"));
			if (binarySplit != null)
				j48.setBinarySplits(binarySplit);
			if (confidenceFactor != null)
				j48.setConfidenceFactor(confidenceFactor.floatValue());
			if (confidenceFactor != null)
				j48.setMinNumObj(minNumObj);
			if (numFolds != null)
				j48.setNumFolds(numFolds);
			if (reduceError != null) j48.setReducedErrorPruning(reduceError);
			if (subtreeRaising != null) j48.setSubtreeRaising(subtreeRaising);
			if (unpruned != null) j48.setUnpruned(unpruned);
			if (useLaplace != null) j48.setUseLaplace(useLaplace);
		}
		try {
			j48.buildClassifier(dataset);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return buildClassifier(j48,dataset,attributes,valsNominal);
	}

	@operator(value = { "classify" }, content_type = IType.LIST, category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc(value = "use a classifier to classify an instance; use: train_j48(classifier, map of attribute values)",
		examples = { @example(value = "my_classifier classify [\"weight\"::65,\"size\"::175]",
			isExecutable = false) },
		see = {"train_chaid", "train_j48"})
	public static
		String primClassify(final IScope scope,GamaClassifier classifier, IAgent ag ) throws GamaRuntimeException {
		if (classifier == null || classifier.getClassifier() == null) return null;
		Instance instance = InstanceManagement.createInstance(scope,ag,classifier.getDataset(),classifier.getDataset().classAttribute(),classifier.getValsNominal());
		instance.setDataset(classifier.getDataset());
	 
		try { 
			double val =  classifier.getClassifier().classifyInstance(instance);
			if(classifier.getDataset().classAttribute().isNominal())
				return classifier.getDataset().classAttribute().value((int)val);
			return String.valueOf(val);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	@operator(value = { "classify" }, content_type = IType.LIST, category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc(value = "use a classifier to classify an instance; use: classify(classifier, an agent)",
	examples = { @example(value = "my_classifier classify self",
		isExecutable = false) },
	see = {"train_chaid", "train_j48"})
	public static
		String primClassify(final IScope scope,GamaClassifier classifier, GamaMap vals ) throws GamaRuntimeException {
		if (classifier == null || classifier.getClassifier() == null) return null;
			Instance instance = InstanceManagement.createInstance(scope,vals,classifier.getDataset(),null,classifier.getValsNominal());
		instance.setDataset(classifier.getDataset());
		try { 
			double val =  classifier.getClassifier().classifyInstance(instance);
			if(classifier.getDataset().classAttribute().isNominal())
				return classifier.getDataset().classAttribute().value((int)val);
			return String.valueOf(val);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	

}
