/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.util.file;

import msi.gama.kernel.experiment.*;
import msi.gama.kernel.model.IModel;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.expressions.GamlExpressionFactory;
import msi.gaml.operators.Files;
import msi.gaml.species.GamlSpecies;
import msi.gaml.types.GamaFileType;
import com.vividsolutions.jts.geom.Envelope;

/**
 * Written by drogoul Modified on 13 nov. 2011
 * 
 * @todo Description
 * 
 */
public class GAMLFile extends GamaFile<Integer, IModel> {

	private final IModel mymodel;
	/**
	 * @throws GamaRuntimeException
	 * @param scope
	 * @param pathName
	 */

	private IExperimentSpecies exp = null;

	public GAMLFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
		mymodel =
			((GamlExpressionFactory) GAML.getExpressionFactory()).getParser().createModelFromFile(getFile().getName());
		// ((ModelDescription) scope.getModel().getDescription())
		// .getTypesManager().addSpeciesType(
		// (ModelDescription) mymodel.getDescription());

		exp = mymodel.getExperiment("Experiment default");

	}

	public GamlSpecies getSpecies(final String name) {

		return (GamlSpecies) mymodel.getSpecies(name);
	}

	public IScope getModelScope() {
		if ( exp == null ) {
			((ExperimentSpecies) exp).createAgent();
		}
		return exp.getAgent().getScope();
	}

	public IExperimentSpecies getExperiment(final String exp_name) {
		if ( exp == null ) {
			exp = mymodel.getExperiment("Experiment " + exp_name);
			((ExperimentSpecies) exp).createAgent();
			// exp.getAgent().createSimulation(new ParametersSet(), false)
			// ._init_(exp.getAgent().getSimulation().getScope());

		}
		return exp;
	}

	@Override
	protected void checkValidity() throws GamaRuntimeException {
		super.checkValidity();
		if ( !GamaFileType.isGAML(getFile().getName()) ) { throw GamaRuntimeException.error("The extension " +
			this.getExtension() + " is not recognized for GAML files"); }
	}

	/**
	 * 
	 * @see msi.gama.util.GamaFile#_copy()
	 */
	@Override
	protected IGamaFile _copy(final IScope scope) {
		// TODO ? Will require to do a copy of the file. But how to get the new
		// name ? Or maybe just
		// as something usable like
		// let f type: file value: write(copy(f2))
		return null;
	}

	/**
	 * 
	 * @see msi.gama.util.GamaFile#_isFixedLength()
	 */
	// @Override
	// protected boolean _isFixedLength() {
	// return false;
	// }

	/**
	 * @see msi.gama.util.GamaFile#_toGaml()
	 */
	@Override
	public String getKeyword() {
		return Files.SHAPE;
	}

	/**
	 * @see msi.gama.util.GamaFile#fillBuffer()
	 */
	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if ( buffer != null ) { return; }
		buffer = new GamaList();

		((IList) buffer).add(mymodel);

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
