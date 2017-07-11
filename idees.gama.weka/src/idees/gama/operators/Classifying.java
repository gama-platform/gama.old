package idees.gama.operators;

import java.util.Map;

import idees.gama.algorithms.CHAID;
import idees.gama.types.GamaClassifier;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMap;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.AODE;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.WAODE;
import weka.classifiers.functions.GaussianProcesses;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.RBFNetwork;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.rules.JRip;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomForest;
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
	@doc(value = "Build and train a CHAID classifier; use: train_j48(data, list of attributes, name of the class, for each nominal attribute and the class, their possible values, the parameters);"
			+ "two parameters can be defined: binary_split: Binary splits on nominal attributes? (default: false); split_point: split point value (default: 0.05)",
		examples = { @example(value = "train_j48(data, [ \"weight\", \"size\"],\"sexe\",[\"sexe\"::[\"M\", \"F\"]],map([\"binary_split\"::true, \"split_point\"::0.03]));",
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
				GamaRuntimeException.error(e.getMessage(), scope);
			}
			return buildClassifier(chaid,dataset,attributes,valsNominal);
		}
	
	
	@operator(value = { "train_jrip" }, content_type = IType.LIST, category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc(value = "Build and train a JRip classifier; use: train_jrip(data, list of attributes, name of the class, for each nominal attribute and the class, their possible values, the parameters);"
			+ "four parameters can be defined: nb_run:Nb of Runs of optimizations  (default: 2); "
			+ "min_nb_obj: The minimal number of instance weights within a split (default: 2.0); "
			+ "prunning: Whether use pruning, i.e. the data is clean or not (default: true); "
			+ "num_folds: The number of folds to split data into Grow and Prune for IREP (default: 3)",
		examples = { @example(value = "train_jrip(data, [ \"weight\", \"size\"],\"sexe\",[\"sexe\"::[\"M\", \"F\"]],map([\"prunning\"::true]));",
			isExecutable = false) },
		see = {"classify", "train_j48"})
	public static
		GamaClassifier primBuildJRIPClassifier(final IScope scope,
				final IContainer data, final IList<String> attributes, final String classIndex, final Map<String,IList<String>> valsNominal,
				final GamaMap<String, Object> parameters) throws GamaRuntimeException {
			
			Instances dataset = InstanceManagement.convertToInstances(scope,classIndex, attributes,valsNominal, data);
			JRip jrip = new JRip();
			jrip.setSeed(Math.abs(scope.getRandom().getSeed().intValue()));
			if (parameters != null) {
				Integer nbRun = Cast.asInt(scope,  parameters.get("nb_run"));
				Double minNumObj = Cast.asFloat(scope,  parameters.get("min_nb"));
				Boolean pruning = Cast.asBool(scope, parameters.get("prunning"));
				Integer numFolds = Cast.asInt(scope,  parameters.get("num_folds"));
				
				if (numFolds != null) jrip.setFolds(numFolds);
				if (minNumObj != null) jrip.setMinNo(minNumObj);
				if (nbRun != null) jrip.setOptimizations(nbRun);
				if (pruning != null) jrip.setUsePruning(pruning);
			}
			try {
				jrip.buildClassifier(dataset);
			} catch (Exception e) {
				GamaRuntimeException.error(e.getMessage(), scope);
			}
			return buildClassifier(jrip,dataset,attributes,valsNominal);
		}
	
	
	@operator(value = { "train_mlp" }, content_type = IType.LIST, category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc(value = "Build and train a Multi-layer perceptron classifier; use: train_mlp(data, list of attributes, name of the class, for each nominal attribute and the class, their possible values, the parameters);"
			,
		examples = { @example(value = "train_mlp(data, [ \"weight\", \"size\"],\"sexe\",[\"sexe\"::[\"M\", \"F\"]],map([]));",
			isExecutable = false) },
		see = {"classify", "train_j48"})
	public static GamaClassifier primBuildMLPClassifier(final IScope scope,
		final IContainer data, final IList<String> attributes, final String classIndex, final Map<String,IList<String>> valsNominal,
		final GamaMap<String, Object> parameters) throws GamaRuntimeException {
			
		Instances dataset = InstanceManagement.convertToInstances(scope,classIndex, attributes,valsNominal, data);
		MultilayerPerceptron mlp = new MultilayerPerceptron();
		mlp.setSeed(Math.abs(scope.getRandom().getSeed().intValue()));
		if (parameters != null) {
				
		}
		try {
			mlp.buildClassifier(dataset);
		} catch (Exception e) {
			GamaRuntimeException.error(e.getMessage(), scope);
		}
		return buildClassifier(mlp,dataset,attributes,valsNominal);
	}
	
	@operator(value = { "train_smo" }, content_type = IType.LIST, category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc(value = "Build and train a Support vector classifier; use: train_smo(data, list of attributes, name of the class, for each nominal attribute and the class, their possible values, the parameters);"
			,
		examples = { @example(value = "train_smo(data, [ \"weight\", \"size\"],\"sexe\",[\"sexe\"::[\"M\", \"F\"]],map([]));",
			isExecutable = false) },
		see = {"classify", "train_j48"})
	public static GamaClassifier primBuildSMOClassifier(final IScope scope,
		final IContainer data, final IList<String> attributes, final String classIndex, final Map<String,IList<String>> valsNominal,
		final GamaMap<String, Object> parameters) throws GamaRuntimeException {
			
		Instances dataset = InstanceManagement.convertToInstances(scope,classIndex, attributes,valsNominal, data);
		SMO smo = new SMO();
		smo.setRandomSeed(Math.abs(scope.getRandom().getSeed().intValue()));
		if (parameters != null) {
				
		}
		try {
			smo.buildClassifier(dataset);
		} catch (Exception e) {
			GamaRuntimeException.error(e.getMessage(), scope);
		}
		return buildClassifier(smo,dataset,attributes,valsNominal);
	}
	
	@operator(value = { "train_gauss" }, content_type = IType.LIST, category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc(value = "Build and train a GAUSSIAN Process Regression classifier; use: train_gauss(data, list of attributes, name of the class, for each nominal attribute and the class, their possible values);"
			,
		examples = { @example(value = "train_gauss(data,[ \"weight\", \"size\"],\"Age\");",
			isExecutable = false) },
		see = {"classify", "train_j48"})
	public static GamaClassifier primBuildGaussianProcessesClassifier(final IScope scope,
		final IContainer data, final IList<String> attributes, final String classIndex, final Map<String,IList<String>> valsNominal) throws GamaRuntimeException {
			
		Instances dataset = InstanceManagement.convertToInstances(scope,classIndex, attributes,valsNominal, data);
		GaussianProcesses classifier = new GaussianProcesses();
		
		try {
			classifier.buildClassifier(dataset);
		} catch (Exception e) {
			GamaRuntimeException.error(e.getMessage(), scope);
		}
		return buildClassifier(classifier,dataset,attributes,valsNominal);
	}
	
	
	@operator(value = { "train_reptree" }, content_type = IType.LIST, category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc(value = "Build and train a REP Decision/Regression classifier; use: train_reptree(data, list of attributes, name of the class, for each nominal attribute and the class, their possible values);"
			,
		examples = { @example(value = "train_reptree(data,[ \"weight\", \"size\"],\"Age\");",
			isExecutable = false) },
		see = {"classify", "train_j48"})
	public static GamaClassifier primBuildREPTREEClassifier(final IScope scope,
		final IContainer data, final IList<String> attributes, final String classIndex, final Map<String,IList<String>> valsNominal) throws GamaRuntimeException {
			
		Instances dataset = InstanceManagement.convertToInstances(scope,classIndex, attributes,valsNominal, data);
		REPTree classifier = new REPTree();
		classifier.setSeed(Math.abs(scope.getRandom().getSeed().intValue()));
		try {
			classifier.buildClassifier(dataset);
		} catch (Exception e) {
			GamaRuntimeException.error(e.getMessage(), scope);
		}
		return buildClassifier(classifier,dataset,attributes,valsNominal);
	}
	
	@operator(value = { "train_rbf" }, content_type = IType.LIST, category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc(value = "Build and train a normalized Gaussian radial basisbasis function network Regression classifier; use: train_gauss(data, list of attributes, name of the class, for each nominal attribute and the class, their possible values);"
			,
		examples = { @example(value = "train_gauss(data, [ \"weight\", \"size\"],\"Age\");",
			isExecutable = false) },
		see = {"classify", "train_j48"})
	public static GamaClassifier primBuildRBFNetworkClassifier(final IScope scope,
		final IContainer data, final IList<String> attributes, final String classIndex, final Map<String,IList<String>> valsNominal) throws GamaRuntimeException {
			
		Instances dataset = InstanceManagement.convertToInstances(scope,classIndex, attributes,valsNominal, data);
		RBFNetwork classifier = new RBFNetwork();
		
		try {
			classifier.buildClassifier(dataset);
		} catch (Exception e) {
			GamaRuntimeException.error(e.getMessage(), scope);
		}
		return buildClassifier(classifier,dataset,attributes,valsNominal);
	}
	
	@operator(value = { "train_bn" }, content_type = IType.LIST, category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc(value = "Build and train a Bayesian Network classifier; use: train_bn(data, list of attributes, name of the class, for each nominal attribute and the class, their possible values);"
			,
		examples = { @example(value = "train_bn(data, [ \"weight\", \"size\"],\"sexe\",[\"sexe\"::[\"M\", \"F\"]]);",
			isExecutable = false) },
		see = {"classify", "train_j48"})
	public static GamaClassifier primBuildBNClassifier(final IScope scope,
		final IContainer data, final IList<String> attributes, final String classIndex, final Map<String,IList<String>> valsNominal) throws GamaRuntimeException {
			
		Instances dataset = InstanceManagement.convertToInstances(scope,classIndex, attributes,valsNominal, data);
		BayesNet bn = new BayesNet();
		try {
			bn.buildClassifier(dataset);
		} catch (Exception e) {
			GamaRuntimeException.error(e.getMessage(), scope);
		}
		return buildClassifier(bn,dataset,attributes,valsNominal);
	}
	
	@operator(value = { "train_rf" }, content_type = IType.LIST, category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc(value = "Build and train a Random Forest classifier; use: train_rf(data, list of attributes, name of the class, for each nominal attribute and the class, their possible values);"
			,
		examples = { @example(value = "train_rf(data, [ \"weight\", \"size\"],\"sexe\",[\"sexe\"::[\"M\", \"F\"]]);",
			isExecutable = false) },
		see = {"classify", "train_j48"})
	public static GamaClassifier primBuildRandomForestClassifier(final IScope scope,
		final IContainer data, final IList<String> attributes, final String classIndex, final Map<String,IList<String>> valsNominal) throws GamaRuntimeException {
			
		Instances dataset = InstanceManagement.convertToInstances(scope,classIndex, attributes,valsNominal, data);
		RandomForest classifier = new RandomForest();
		classifier.setSeed(Math.abs(scope.getRandom().getSeed().intValue()));
		try {
			classifier.buildClassifier(dataset);
		} catch (Exception e) {
			GamaRuntimeException.error(e.getMessage(), scope);
		}
		return buildClassifier(classifier,dataset,attributes,valsNominal);
	}
	
	@operator(value = { "train_smo_reg" }, content_type = IType.LIST, category = { IOperatorCategory.STATISTICAL },
			concept = { IConcept.STATISTIC })
	@doc(value = "Build and train a Support Vector Regression classifier; use: train_smo_reg(data, list of attributes, name of the class, for each nominal attribute and the class, their possible values);"
			,
		examples = { @example(value = "train_smo_reg(data, [ \"weight\", \"size\"],\"Age\");",
			isExecutable = false) },
		see = {"classify", "train_j48"})
	public static GamaClassifier primBuildSMORegClassifier(final IScope scope,
		final IContainer data, final IList<String> attributes, final String classIndex, final Map<String,IList<String>> valsNominal) throws GamaRuntimeException {
			
		Instances dataset = InstanceManagement.convertToInstances(scope,classIndex, attributes,valsNominal, data);
		SMOreg classifier = new SMOreg();
		try {
			classifier.buildClassifier(dataset);
		} catch (Exception e) {
			GamaRuntimeException.error(e.getMessage(), scope);
		}
		return buildClassifier(classifier,dataset,attributes,valsNominal);
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
		examples = { @example(value = "train_j48(data, [ \"weight\", \"size\"],\"sexe\",[\"sexe\"::[\"M\", \"F\"]],map([\"unpruned\"::true]));",
			isExecutable = false) },
		see = { "classify","train_chaid" })
	public static
	GamaClassifier primBuildJ48Classifier(final IScope scope,
			final IContainer data, final IList<String> attributes, final String classIndex, final Map<String,IList<String>> valsNominal,
			final GamaMap<String, Object> parameters) throws GamaRuntimeException {
		Instances dataset = InstanceManagement.convertToInstances(scope,classIndex, attributes,valsNominal, data);
		J48 j48 = new J48();
		j48.setSeed(Math.abs(scope.getRandom().getSeed().intValue()));
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
			GamaRuntimeException.error(e.getMessage(), scope);
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
			GamaRuntimeException.error(e.getMessage(), scope);
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
			GamaRuntimeException.error(e.getMessage(), scope);
		}
		return null;
	}
	
	
	

}
