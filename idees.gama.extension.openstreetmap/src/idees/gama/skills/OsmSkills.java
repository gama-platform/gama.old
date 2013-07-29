package idees.gama.skills;


import idees.gama.io.OsmReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.task.v0_6.RunnableSource;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.xml.common.CompressionMethod;
import org.openstreetmap.osmosis.xml.v0_6.XmlReader;

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
import msi.gaml.operators.Cast;
import msi.gaml.skills.Skill;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;

@skill(name = "osm") 
public class OsmSkills extends Skill {
	
	@action(name = "load_osm", args = {
			@arg(name = "file", type = IType.FILE, optional = false, doc = @doc("file to load")) , 
			@arg(name = "road_species", type = IType.SPECIES, optional = true, doc = @doc("the species of the road agent")),
			@arg(name = "building_species", type = IType.SPECIES, optional = true, doc = @doc("the species of the building agent"))}, 
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
		try {
			OsmReader reader = new OsmReader();
			reader.loadFile(file.getFile());
			createdAgents = reader.buildAgents(scope, roadPop,buildingPop);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return createdAgents;
	}
	
	
      

}
		


