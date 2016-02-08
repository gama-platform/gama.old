/*********************************************************************************************
 *
 *
 * 'EditorsList.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.kernel.experiment;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.metamodel.agent.IAgent;

public abstract class EditorsList<T> implements ItemList<T> {

	/* Map to associate a category to each parameter */
	protected final Map<T, Map<String, IParameterEditor>> categories = new LinkedHashMap();

	@Override
	public List<T> getItems() {
		return new ArrayList(categories.keySet());
	}

	@Override
	public abstract String getItemDisplayName(final T obj, final String previousName);

	public abstract void add(final Collection<? extends IParameter> params, final IAgent agent);

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

	// public void writeModifiedParameters(final PrintWriter pw) throws GamaRuntimeException {
	// IScope scope = GAMA.obtainNewScope();
	// for ( final IParameter vp : getChangedParameters().values() ) {
	// String s =
	// "<" + vp.getType().toString() + " name=\"" + vp.getName() + "\" init=\"" +
	// StringUtils.toGaml(vp.value(scope), false) + "\" ";
	// s = s + "parameter = \"" + vp.getTitle() + "\" ";
	// s = s + (vp.getCategory() != null ? " category=\"" + vp.getCategory() + "\"" : "");
	// s = s + (vp.getMinValue() != null ? " min=\"" + vp.getMinValue() + "\" " : "");
	// s = s + (vp.getMaxValue() != null ? " max=\"" + vp.getMaxValue() + "\" " : "");
	// s = s + "/>";
	// pw.println(s);
	// }
	// GAMA.releaseScope(scope);
	// }

	// private Map<String, IParameter> getChangedParameters() {
	// Map<String, IParameter> result = new THashMap();
	// for ( Map<String, IParameterEditor> editors : categories.values() ) {
	// for ( IParameterEditor ed : editors.values() ) {
	// if ( ed.isValueModified() ) {
	// result.put(ed.getParam().getName(), ed.getParam());
	// }
	// }
	// }
	// return result.isEmpty() ? null : result;
	// }

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

	@Override
	public void makeItemVisible(final T obj, final boolean b) {}

	@Override
	public void makeItemSelectable(final T obj, final boolean b) {}

}
