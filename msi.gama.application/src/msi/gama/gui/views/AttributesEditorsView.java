/*********************************************************************************************
 * 
 * 
 * 'AttributesEditorsView.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.views;

import java.util.*;
import msi.gama.common.interfaces.IParameterEditor;
import msi.gama.gui.parameters.AbstractEditor;
import msi.gama.kernel.experiment.EditorsList;
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
		Map<String, IParameterEditor> parameters = editors.getCategories().get(data);
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
	//
	// public Map<T, THashMap<String, IParameterEditor>> getCategories() {
	// return editors.getCategories();
	// }

}
