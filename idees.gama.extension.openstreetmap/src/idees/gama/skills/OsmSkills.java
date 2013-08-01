package idees.gama.skills;


import idees.gama.io.OsmReader;

import java.io.FileNotFoundException;
import java.util.List;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.file.GamaFile;
import msi.gaml.skills.Skill;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;

@skill(name = "osm") 
public class OsmSkills extends Skill {
	
	@action(name = "load_osm", args = {
			@arg(name = "file", type = IType.FILE, optional = false, doc = @doc("file to load")) , 
			@arg(name = "road_species", type = IType.SPECIES, optional = true, doc = @doc("the species of the road agent")),
			@arg(name = "building_species", type = IType.SPECIES, optional = true, doc = @doc("the species of the building agent")),
			@arg(name = "split_lines", type = IType.BOOL, optional = true, doc = @doc("if false, the lines are not split at intersections")) }, 
			doc = @doc(value = "load a OSM file and create the corresponding agents", returns = "list of created agents",examples = { "list<agent> <- load_osm(my_osm_file);" }))
	public List<IAgent> loadOSMFile(final IScope scope) throws GamaRuntimeException {
		List<IAgent> createdAgents = new GamaList<IAgent>();
		
		@SuppressWarnings("rawtypes")
		final GamaFile file = (GamaFile) scope.getArg("file", IType.NONE);
		if (file == null) {
			return createdAgents;
		}
		final IAgent executor = scope.getAgentScope();
		
		final ISpecies roadSpecies = (ISpecies) scope.getArg("road_species", IType.SPECIES);
		final ISpecies rs = roadSpecies != null ? roadSpecies : scope.getModel().getSpecies("osm_road");
		IPopulation roadPop = executor.getPopulationFor(rs);
		
		final ISpecies buildingSpecies = (ISpecies) scope.getArg("building_species", IType.SPECIES);
		final ISpecies bs = buildingSpecies != null ? buildingSpecies : scope.getModel().getSpecies("osm_building");
		IPopulation buildingPop = executor.getPopulationFor(bs);
		Boolean splitLines = true;
		if (scope.hasArg("split_lines")) {
			splitLines = (Boolean) scope.getArg("split_lines", IType.BOOL);
		}	
		try {
			OsmReader reader = new OsmReader();
			reader.loadFile(file.getFile(),splitLines);
			createdAgents = reader.buildAgents(scope, roadPop,buildingPop,splitLines);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return createdAgents;
	}
	
	
      

}
		


