/*********************************************************************************************
 *
 *
 * 'GAMLFile.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.util.file;

import static org.apache.commons.lang.StringUtils.*;
import java.util.*;
import com.vividsolutions.jts.geom.Envelope;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.model.IModel;
import msi.gama.outputs.IOutput;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.expressions.*;
import msi.gaml.species.GamlSpecies;
import msi.gaml.types.*;

// import scala.actors.threadpool.Arrays;

/**
 * Written by drogoul Modified on 13 nov. 2011
 *
 * @todo Description
 *
 */
@file(name = "gaml",
	extensions = { "gaml" },
	buffer_type = IType.LIST,
	buffer_content = IType.SPECIES,
	buffer_index = IType.INT)
public class GAMLFile extends GamaFile<IList<IModel>, IModel, Integer, IModel> {

	public static class GamlInfo extends GamaFileMetaData {

		public static String BATCH_PREFIX = "***";

		public final Collection<String> experiments;
		public final Collection<String> imports;
		public final Collection<String> uses;

		public GamlInfo(final long stamp, final Collection<String> imports, final Collection<String> uses,
			final Collection<String> exps) {
			super(stamp);
			this.imports = imports;
			this.uses = uses;
			this.experiments = exps;
		}

		public GamlInfo(final String propertyString) {
			super(propertyString);
			String[] values = split(propertyString);
			imports = Arrays.asList(splitByWholeSeparatorPreserveAllTokens(values[1], SUB_DELIMITER));
			uses = Arrays.asList(splitByWholeSeparatorPreserveAllTokens(values[2], SUB_DELIMITER));
			experiments = Arrays.asList(splitByWholeSeparatorPreserveAllTokens(values[3], SUB_DELIMITER));
		}

		/**
		 * Method getSuffix()
		 * @see msi.gama.util.file.GamaFileMetaInformation#getSuffix()
		 */
		@Override
		public String getSuffix() {
			int expCount = experiments.size();
			if ( expCount > 0 ) { return "" + (expCount == 1 ? "1 experiment" : expCount + " experiments"); }

			return "no experiment";
		}

		@Override
		public String toPropertyString() {
			StringBuilder sb = new StringBuilder();
			sb.append(super.toPropertyString()).append(DELIMITER);
			sb.append(join(imports, SUB_DELIMITER)).append(DELIMITER);
			sb.append(join(uses, SUB_DELIMITER)).append(DELIMITER);
			sb.append(join(experiments, SUB_DELIMITER)).append(DELIMITER);
			return sb.toString();

		}

		@Override
		public String getDocumentation() {
			return "GAML model file with " + getSuffix();
		}

	}

	private IModel mymodel = null;
	/**
	 * @throws GamaRuntimeException
	 * @param scope
	 * @param pathName
	 */
	// private GamaList sharedResource;
	private String experimentName = "default";
	// private String controllerName = "default";
	private String aliasName = "";
	private final boolean initDisplay = false;

	// private IExperimentPlan exp = null;
	public GAMLFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}

	@Override
	public IContainerType getType() {
		return Types.FILE.of(Types.INT, Types.SPECIES);
	}

	public GAMLFile(final IScope scope, final String pathName, final String expName, final String cName)
		throws GamaRuntimeException {
		super(scope, pathName);
		experimentName = expName;
		aliasName = cName;
		ModelDescription mm = ((GamlExpressionFactory) GAML.getExpressionFactory()).getParser()
			.createModelDescriptionFromFile(getFile().getName());
		mm.setAlias(aliasName);
		mymodel = (IModel) mm.compile();
		// multithread
		// controllerName = experimentName + aliasName;

		// initDisplay = false;
	}

	@Override
	public IList<String> getAttributes(final IScope scope) {
		// TODO what to return ?
		return GamaListFactory.EMPTY_LIST;
	}

	public GamlSpecies getSpecies(final String name) {

		return (GamlSpecies) mymodel.getSpecies(name);
	}

	public IExperimentPlan createExperiment(final String expName) {
		IExperimentPlan exp = mymodel.getExperiment("Experiment " + expName);
		for ( IOutput o : exp.getSimulationOutputs().getOutputs().values() ) {
			o.setName(o.getName() + "#" + aliasName);
		}
		for ( IOutput o : exp.getExperimentOutputs().getOutputs().values() ) {
			o.setName(o.getName() + "#" + aliasName);
		}
		// ((ExperimentPlan) exp).setControllerName(aliasName);
		// multithread
		// eliminate conflict in close-open current Layer to initialize displays

		GAMA.addGuiExperiment(exp);

		// GAMA.getController(aliasName).newExperiment(exp);
		// singlethread
		// GAMA.getController(controllerName).addExperiment(
		// experimentName + comodelName, exp);
		return exp;
	}

	public void execute(final IScope scope, final IExpression with_exp, final IExpression param_input,
		final IExpression param_output, final IExpression reset, final IExpression repeat,
		final IExpression stopCondition, final IExpression share) {
		// if ( GAMA.getController(aliasName) == null ) {
		// IExperimentController fec = new ExperimentController(new FrontEndScheduler());
		// GAMA.addController(aliasName, fec);
		// }
		// multithread
		// if ( GAMA.getController(aliasName).getExperiment() == null ) {

		// singlethread
		// if (GAMA.getController(controllerName)
		// .getExperiment(
		// experimentName) == null) {

		IExperimentPlan experiment = createExperiment(experimentName);
		// }

		if ( !initDisplay ) {
			if ( param_input != null ) {
				GamaMap in = (GamaMap) param_input.value(scope);
				for ( int i = 0; i < in.getKeys().size(); i++ ) {
					experiment.getModel().getVar(in.getKeys().get(i).toString()).setValue(null, in.getValues().get(i));
				}
			}
			GAMA.openExperiment(experiment);
		}
	}

	/**
	 * @see msi.gama.util.GamaFile#fillBuffer()
	 */
	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if ( getBuffer() != null ) { return; }
		setBuffer(GamaListFactory.<IModel> create(Types.SPECIES));
		// IModel mymodel= ((GamlExpressionFactory) GAML.getExpressionFactory())
		// .getParser().createModelFromFile(getFile().getName());
		if ( mymodel == null ) {
			ModelDescription mm = ((GamlExpressionFactory) GAML.getExpressionFactory()).getParser()
				.createModelDescriptionFromFile(getFile().getName());
			mymodel = (IModel) mm.compile();
		}
		((IList) getBuffer()).add(mymodel.getSpecies());
	}

	public IModel getModel() {
		return mymodel;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.util.GamaFile#flushBuffer()
	 */
	@Override
	protected void flushBuffer() throws GamaRuntimeException {
		// TODO Regarder ce qu'il y a dans la commande "save" pour sauvegarder
		// les fichiers.
		// Merger progressivement save et le syst�me de fichiers afin de ne plus
		// d�pendre de �a.

	}

	@Override
	public Envelope computeEnvelope(final IScope scope) {

		return null;

	}

}
