/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
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
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.FileUtils;
import msi.gama.precompiler.GamaProcessor;
import msi.gaml.commands.Facets;
import msi.gaml.compilation.GamlException;
import msi.gaml.expressions.*;
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

	public ModelDescription(final String fileName) throws GamlException {
		super(IKeyword.MODEL, null, new Facets(), new ArrayList(), null);
		id = id_provider++;
		types = new TypesManager();
		setModelFileName(fileName);
	}

	public String constructModelRelativePath(final String filePath, final boolean mustExist) {
		try {
			return FileUtils.constructAbsoluteFilePath(filePath, fileName, mustExist);
		} catch (GamlException e) {
			e.printStackTrace();
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

	public void addType(final SpeciesDescription species) throws GamlException {
		try {
			types.addType(species.getName(), species.getJavaBase());
		} catch (GamlException ge) {
			throw new GamlException(ge.getMessage(), species.getSourceInformation());
		}
	}

	@Override
	public IDescription addChild(final IDescription child) throws GamlException {
		child.setSuperDescription(this);
		String keyword = child.getKeyword();
		if ( child instanceof SpeciesDescription ) { // world_species
			worldSpecies = (SpeciesDescription) child;
			addType(worldSpecies);
		} else if ( keyword.equals(IKeyword.OUTPUT) ) {
			if ( output == null ) {
				output = child;
			} else {
				output.addChildren(child.getChildren());
				return child;
			}
		} else if ( keyword.equals(IKeyword.EXPERIMENT) ) {
			experiments.put(child.getName(), (ExperimentDescription) child);
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
		if ( spec == null ) { return null; }

		return findSpecies(worldSpecies, spec);
	}

	/**
	 * Search for a species with the specified name.
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

		SpeciesDescription retVal;

		List<SpeciesDescription> microSpecs = topSpecies.getMicroSpecies();
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

	public void verifyVarName(final String name, final short modelId) throws GamlException {
		if ( name == null ) { throw new GamlException(
			"The attribute 'name' is missing. Variables must be named."); }
		if ( IExpressionParser.RESERVED.contains(name) ) { throw new GamlException(
			name +
				" is a reserved keyword. It cannot be used as a variable name. Reserved keywords are: " +
				IExpressionParser.RESERVED); }
		if ( IExpressionParser.BINARIES.containsKey(name) ) { throw new GamlException(name +
			" is a binary operator name. It cannot be used as a variable name"); }
		if ( IExpressionParser.UNARIES.containsKey(name) ) { throw new GamlException(name +
			" is a unary operator name. It cannot be used as a variable name"); }
		if ( types.getTypeNames().contains(name) ) { throw new GamlException(name +
			" is a type name. It cannot be used as a variable name. Types in this model are :" +
			types.getTypeNames()); }
		if ( GamaProcessor.UNITS.containsKey(name) ) { throw new GamlException(name +
			" is a unit name. It cannot be used as a variable name. Units in this model are :" +
			String.valueOf(GamaProcessor.UNITS.keySet())); }
	}

	@Override
	public SpeciesDescription getSpeciesContext() {
		return null; // return getWorldSpecies() ?
	}

}
