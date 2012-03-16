/**
 * Created by drogoul, 14 mars 2012
 * 
 */
package msi.gama.metamodel.population;

import java.util.List;

/**
 * The class IMetaPopulation.
 * 
 * @author drogoul
 * @since 14 mars 2012
 * 
 */
public interface IMetaPopulation extends IPopulation {

	List<IPopulation> getPopulations();

}
