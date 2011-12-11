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
package msi.gama.gui.application.views;

import java.util.*;
import msi.gama.gui.parameters.*;
import msi.gama.util.GamaList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public abstract class AttributesEditorsView<T> extends ExpandableItemsView<T> {

	protected EditorsList<T> editors;

	@Override
	public String getItemDisplayName(final T obj, final String previousName) {
		return editors.getItemDisplayName(obj, previousName);
	}

	@Override
	protected Composite createItemContentsFor(final T data) {
		Map<String, AbstractEditor> parameters = editors.getCategories().get(data);
		Composite compo = new Composite(getViewer(), SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 0;
		compo.setLayout(layout);
		if ( parameters != null ) {
			List<AbstractEditor> list = new GamaList(parameters.values());
			Collections.sort(list);
			for ( AbstractEditor gpParam : list ) {
				gpParam.createComposite(compo);
			}
		}
		return compo;
	}

	@Override
	public void reset() {
		super.reset();
		editors = null;
	}

	@Override
	public void removeItem(final T obj) {
		editors.removeItem(obj);
	}

	@Override
	public void pauseItem(final T obj) {
		editors.pauseItem(obj);
	}

	@Override
	public void resumeItem(final T obj) {
		editors.resumeItem(obj);
	}

	@Override
	public void focusItem(final T obj) {
		editors.focusItem(obj);
	}

	@Override
	public List<T> getItems() {
		return editors.getItems();
	}

	@Override
	public void updateItemValues() {
		if ( editors != null ) {
			editors.updateItemValues();
		}
	}

	public Map<T, Map<String, AbstractEditor>> getCategories() {
		return editors.getCategories();
	}

}
