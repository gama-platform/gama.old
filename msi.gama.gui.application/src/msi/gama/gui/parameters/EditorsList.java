/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.gui.parameters;

import java.io.PrintWriter;
import java.util.*;
import msi.gama.gui.application.views.ItemList;
import msi.gama.interfaces.*;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.util.*;

public abstract class EditorsList<T> implements ItemList<T> {

	/* Map to associate a category to each parameter */
	Map<T, Map<String, AbstractEditor>> categories;

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

	public Map<T, Map<String, AbstractEditor>> getCategories() {
		return categories;
	}

	public void revertToDefaultValue() {
		for ( Map<String, AbstractEditor> editors : categories.values() ) {
			for ( AbstractEditor ed : editors.values() ) {
				ed.revertToDefaultValue();
			}
		}
	}

	public void writeModifiedParameters(final PrintWriter pw) throws GamaRuntimeException {
		IScope scope = GAMA.obtainNewScope();
		for ( final IParameter vp : getChangedParameters().values() ) {
			String s =
				"<" + vp.type().toString() + " name=\"" + vp.getName() + "\" init=\"" +
					Cast.toGaml(vp.value(scope)) + "\" ";
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
		for ( Map<String, AbstractEditor> editors : categories.values() ) {
			for ( AbstractEditor ed : editors.values() ) {
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
