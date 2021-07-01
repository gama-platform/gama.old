package espacedev.gaml.extensions.genstar.generator;

import java.util.List;
import java.util.Map;

import espacedev.gaml.extensions.genstar.statement.GenerateStatement;
import msi.gama.runtime.IScope;
import msi.gaml.statements.Arguments;
import msi.gaml.types.IType;

/**
 * 
 * Interface called by the GenerateStatement to build a synthetic population based on 
 * a given Source of information
 * 
 * @author kevinchapuis
 *
 */
public interface IGenstarGenerator {
	
	/**
	 * Gives the type of the main source of data to inform generation process
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	IType sourceType();
	
	/**
	 * Tests if the source type fit the required type for this generator 
	 * @param scope
	 * @param source
	 * @return
	 */
	boolean sourceMatch(IScope scope, Object source);
	
	/**
	 * The main method to generate agents' attributes
	 * @param scope: the enclosing scope of agent generation
	 * @param inits: the list of agents mapping between attributes and generated values
	 * @param max: the number of agents' map to generate
	 * @param source: the source of information
	 * @param attributes: the attributes to bind from data to agent species
	 * @param init: ??
	 * @param generateStatement: central statement that monitor the generation of agents
	 */
	public void generate(IScope scope, List<Map<String, Object>> inits, Integer max,
			Object source, Object attributes, Object algo, 
			Arguments init, GenerateStatement generateStatement); 
	
}
