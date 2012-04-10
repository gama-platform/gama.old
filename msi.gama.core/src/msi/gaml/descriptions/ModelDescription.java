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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.descriptions;

import java.io.File;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.FileUtils;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.*;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 16 mai 2010
 * 
 * @todo Description
 * 
 */
public class ModelDescription extends SymbolDescription {

	private static short id_provider;
	short id;
	private Map<String, ExperimentDescription> experiments;
	private IDescription output;
	private IDescription environment;
	TypesManager types;
	private String fileName;
	private String baseDirectory;
	private SpeciesDescription worldSpecies;

	public ModelDescription(final String fileName, final ISyntacticElement source) {
		super(IKeyword.MODEL, null, new ArrayList(), source, DescriptionFactory.getModelFactory()
			.getMetaDescriptionFor(null, IKeyword.MODEL));
		id = id_provider++;
		types = new TypesManager();
		setModelFileName(fileName);
	}

	public String constructModelRelativePath(final String filePath, final boolean mustExist) {
		try {
			return FileUtils.constructAbsoluteFilePath(filePath, fileName, mustExist);
		} catch (GamaRuntimeException e) {
			flagError(e.getMessage());
			return filePath;
		}
	}

	@Override
	protected void initFields() {
		experiments = new HashMap<String, ExperimentDescription>();
	}

	public short getId() {
		return id;
	}

	/**
	 * Gets the model file name.
	 * 
	 * @return the model file name
	 */
	public String getModelFileName() {
		return fileName;
	}

	public void setModelFileName(final String name) {
		fileName = name;
		setBaseDirectory(new File(name).getAbsoluteFile().getParent());
	}

	public void setBaseDirectory(final String name) {
		baseDirectory = name;
	}

	public String getBaseDirectory() {
		return baseDirectory;
	}

	public void addType(final SpeciesDescription species) {
		types.addType(species);
	}

	@Override
	public IDescription addChild(final IDescription child) {
		child.setSuperDescription(this);
		String keyword = child.getKeyword();
		if ( child instanceof ExperimentDescription ) {
			experiments.put(child.getName(), (ExperimentDescription) child);
		} else if ( child instanceof SpeciesDescription ) { // world_species
			worldSpecies = (SpeciesDescription) child;
			addType(worldSpecies);
		} else if ( keyword.equals(IKeyword.OUTPUT) ) {
			if ( output == null ) {
				output = child;
			} else {
				output.addChildren(child.getChildren());
				return child;
			}
		} else if ( keyword.equals(IKeyword.ENVIRONMENT) ) {
			if ( environment == null ) {
				environment = child;
			} else {
				environment.addChildren(child.getChildren());
				return child;
			}
		}

		children.add(child);
		return child;
	}

	@Override
	public SpeciesDescription getWorldSpecies() {
		return worldSpecies;
	}

	@Override
	protected boolean hasVar(final String name) {
		return getWorldSpecies().hasVar(name);
	}

	public boolean hasExperiment(final String name) {
		return experiments.containsKey(name);
	}

	@Override
	public IDescription getDescriptionDeclaringVar(final String name) {
		if ( hasVar(name) ) { return getWorldSpecies(); }
		return null;
	}

	@Override
	public IExpression getVarExpr(final String name, final IExpressionFactory factory) {
		return getWorldSpecies().getVarExpr(name, factory);
	}

	@Override
	public ModelDescription getModelDescription() {
		return this;
	}

	@Override
	public SpeciesDescription getSpeciesDescription(final String spec) {
		if (( spec == null ) || ( worldSpecies == null ) ) { return null; }

		return findSpecies(worldSpecies, spec);
	}

	/**
	 * Search for a species with the specified name.
	 * 
	 * The eligible species for the search may be the topSpecies itself or one of the micro-species
	 * of the topSpecies.
	 * 
	 * @param topSpecies the top species of a branch.
	 * @param specToFind the name of the species to be searched
	 * @return a species with the specified name or null.
	 */
	private SpeciesDescription findSpecies(final SpeciesDescription topSpecies,
		final String specToFind) {
		if ( topSpecies == null || specToFind == null ) { return null; }

		if ( topSpecies.getName().equals(specToFind) ) { return topSpecies; }

		List<SpeciesDescription> microSpecs = topSpecies.getMicroSpecies();
		
		for ( SpeciesDescription micro : microSpecs ) {
			if (micro.getName().equals(specToFind)) { return micro; }
		}

		/*
		 * Avoid infinite recursion.
		 * 
		 * When a species is a sub-species of its direct macro-species,
		 * it is a micro-species of itself thus this leads to infinite recursion.
		 */
		if (microSpecs.contains(topSpecies)) { microSpecs.remove(topSpecies); }
		
		SpeciesDescription retVal;
		for ( SpeciesDescription micro : microSpecs ) {
			retVal = findSpecies(micro, specToFind);
			if ( retVal != null ) { return retVal; }
		}

		return null;
	}

	@Override
	public IType getTypeOf(final String s) {
		return types.get(s);
	}

	public TypesManager getTypesManager() {
		return types;
	}

	@Override
	public SpeciesDescription getSpeciesContext() {
		return null; // return getWorldSpecies() ?
	}

}
