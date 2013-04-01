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
package msi.gama.kernel.experiment;

import java.io.PrintWriter;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.StringUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;

public abstract class EditorsList<T> implements ItemList<T> {

	/* Map to associate a category to each parameter */
	protected Map<T, Map<String, IParameterEditor>> categories;

	public EditorsList() {
		categories = new HashMap();
	}

	@Override
	public List<T> getItems() {
		return new GamaList(categories.keySet());
	}

	@Override
	public abstract String getItemDisplayName(final T obj, final String previousName);

	public abstract void add(final List<? extends IParameter> params, final IAgent agent);

	public Map<T, Map<String, IParameterEditor>> getCategories() {
		return categories;
	}

	public void revertToDefaultValue() {
		for ( Map<String, IParameterEditor> editors : categories.values() ) {
			for ( IParameterEditor ed : editors.values() ) {
				ed.revertToDefaultValue();
			}
		}
	}

	public void writeModifiedParameters(final PrintWriter pw) throws GamaRuntimeException {
		IScope scope = GAMA.obtainNewScope();
		for ( final IParameter vp : getChangedParameters().values() ) {
			String s =
				"<" + vp.getType().toString() + " name=\"" + vp.getName() + "\" init=\"" +
					StringUtils.toGaml(vp.value(scope)) + "\" ";
			s = s + "parameter = \"" + vp.getTitle() + "\" ";
			s = s + (vp.getCategory() != null ? " category=\"" + vp.getCategory() + "\"" : "");
			s = s + (vp.getMinValue() != null ? " min=\"" + vp.getMinValue() + "\" " : "");
			s = s + (vp.getMaxValue() != null ? " max=\"" + vp.getMaxValue() + "\" " : "");
			s = s + "/>";
			pw.println(s);
		}
		GAMA.releaseScope(scope);
	}

	private Map<String, IParameter> getChangedParameters() {
		Map<String, IParameter> result = new HashMap();
		for ( Map<String, IParameterEditor> editors : categories.values() ) {
			for ( IParameterEditor ed : editors.values() ) {
				if ( ed.isValueModified() ) {
					result.put(ed.getParam().getName(), ed.getParam());
				}
			}
		}
		return result.isEmpty() ? null : result;
	}

	@Override
	public void removeItem(final T name) {
		categories.remove(name);
	}

	@Override
	public void pauseItem(final T name) {}

	@Override
	public abstract void updateItemValues();

	@Override
	public void resumeItem(final T obj) {}

	@Override
	public void focusItem(final T obj) {}

}
