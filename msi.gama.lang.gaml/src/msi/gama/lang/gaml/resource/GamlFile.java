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
package msi.gama.lang.gaml.resource;

import org.eclipse.emf.common.util.URI;

import com.vividsolutions.jts.geom.Envelope;

import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.validation.GamlModelBuilder;
import msi.gama.outputs.IOutput;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.IList;
import msi.gama.util.file.GamaFile;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IContainerType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Written by drogoul Modified on 13 nov. 2011
 *
 * @todo Description
 *
 */
@file(name = "gaml", extensions = {
		"gaml" }, buffer_type = IType.LIST, buffer_content = IType.SPECIES, buffer_index = IType.INT, concept = {
				IConcept.FILE })
public class GamlFile extends GamaFile<IList<IModel>, IModel, Integer, IModel> {

	private final IModel mymodel;
	/**
	 * @throws GamaRuntimeException
	 * @param scope
	 * @param pathName
	 */
	private final String experimentName;

	private final String aliasName;

	public GamlFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
		experimentName = "";
		aliasName = "";
		mymodel = GamlModelBuilder.compile(URI.createURI(getPath(scope), false), null);
	}

	@Override
	public IContainerType getType() {
		return Types.FILE.of(Types.INT, Types.SPECIES);
	}

	public GamlFile(final IScope scope, final String pathName, final String expName, final String cName)
			throws GamaRuntimeException {
		super(scope, pathName);
		experimentName = expName;
		aliasName = cName;
		mymodel = GamlModelBuilder.compile(URI.createURI(getPath(scope), false), null);
		((ModelDescription) mymodel.getDescription()).setAlias(aliasName);
	}

	@Override
	public IList<String> getAttributes(final IScope scope) {
		return GamaListFactory.create();
	}

	public IExperimentPlan createExperiment(final String expName) {
		final IExperimentPlan exp = mymodel.getExperiment("Experiment " + expName);
		for (final IOutput o : exp.getOriginalSimulationOutputs().getOutputs().values()) {
			o.setName(o.getName() + "#" + aliasName);
		}
		for (final IOutput o : exp.getExperimentOutputs().getOutputs().values()) {
			o.setName(o.getName() + "#" + aliasName);
		}

		GAMA.addGuiExperiment(exp);

		return exp;
	}

	public void execute(final IScope scope, final IExpression with_exp, final IExpression param_input,
			final IExpression param_output, final IExpression reset, final IExpression repeat,
			final IExpression stopCondition, final IExpression share) {
		final IExperimentPlan experiment = createExperiment(experimentName);

		if (param_input != null) {
			final GamaMap in = Cast.asMap(scope, param_input.value(scope), true);
			for (int i = 0; i < in.getKeys().size(); i++) {
				experiment.getModel().getVar(in.getKeys().get(i).toString()).setValue(null, in.getValues().get(i));
			}
		}
		GAMA.openExperiment(experiment);
	}

	/**
	 * @see msi.gama.util.GamaFile#fillBuffer()
	 */
	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if (getBuffer() != null) {
			return;
		}
		setBuffer(GamaListFactory.<IModel> create(Types.SPECIES));
		((IList) getBuffer()).add(mymodel.getSpecies());
	}

	@Override
	public Envelope computeEnvelope(final IScope scope) {
		return null;
	}

}
