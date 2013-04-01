/**
 * Created by drogoul, 5 avr. 2012
 * 
 */
package msi.gama.common.util;

import java.util.List;
import msi.gaml.compilation.GamlCompilationError;

/**
 * The class IErrorCollector.
 * 
 * @author drogoul
 * @since 5 avr. 2012
 * 
 */
public interface IErrorCollector {

	public abstract void add(final GamlCompilationError error);

	public abstract List<GamlCompilationError> getErrors();

	public abstract List<GamlCompilationError> getWarnings();

	public abstract List<GamlCompilationError> getInfos();

}