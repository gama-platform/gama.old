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
import java.util.List;
import msi.gama.common.interfaces.IParameterEditor;
import msi.gama.gui.parameters.AbstractEditor;
import msi.gama.gui.swt.controls.ITooltipDisplayer;
import msi.gama.kernel.experiment.EditorsList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

public abstract class AttributesEditorsView<T> extends ExpandableItemsView<T> implements ITooltipDisplayer {

	private final Set<Label> labels = new LinkedHashSet();

	public final static int EDITORS_SPACING = 0;

	class ItemComposite extends Composite {

		public ItemComposite(final Composite parent, final int style) {
			super(parent, style);
			GridLayout layout = new GridLayout(2, false);
			layout.verticalSpacing = EDITORS_SPACING;
			setLayout(layout);
			// addControlListener(new ControlListener() {
			//
			// @Override
			// public void controlResized(final ControlEvent e) {
			// float averageWidth = 0;
			// float maxWidth = 0;
			// for ( Label l : labels ) {
			// int width = l.getText().length() * 8;
			// averageWidth += width;
			// if ( width > maxWidth ) {
			// maxWidth = width;
			// }
			// }
			// averageWidth /= labels.size();
			//
			// if ( averageWidth == 0.0 ) { return; }
			// averageWidth += 20;
			// averageWidth = Math.max(averageWidth, maxWidth / 2);
			// for ( Label l : labels ) {
			// // l.setSize(l.computeSize((int) averageWidth, SWT.DEFAULT));
			// ((GridData) l.getLayoutData()).widthHint = (int) averageWidth;
			// l.getParent().update();
			// l.getParent().layout();
			// }
			// update();
			// layout();
			// }
			//
			// @Override
			// public void controlMoved(final ControlEvent e) {}
			// });
		}

		public void addEditor(final AbstractEditor editor) {
			editor.createComposite(this);
			final Label label = editor.getLabel();
			// ((GridData) label.getLayoutData()).heightHint = 36;
			labels.add(label);
			label.addDisposeListener(new DisposeListener() {

				@Override
				public void widgetDisposed(final DisposeEvent e) {
					labels.remove(label);
				}
			});
		}

	}

	protected EditorsList<T> editors;

	@Override
	public String getItemDisplayName(final T obj, final String previousName) {
		if ( editors == null ) { return ""; }
		return editors.getItemDisplayName(obj, previousName);
	}

	@Override
	protected Composite createItemContentsFor(final T data) {
		Map<String, IParameterEditor> parameters = editors.getCategories().get(data);
		final ItemComposite compo = new ItemComposite(getViewer(), SWT.NONE);
		if ( parameters != null ) {
			List<AbstractEditor> list = new ArrayList(parameters.values());
			Collections.sort(list);
			for ( AbstractEditor gpParam : list ) {
				compo.addEditor(gpParam);
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
		if ( editors == null ) { return; }
		editors.removeItem(obj);
	}

	@Override
	public void pauseItem(final T obj) {
		if ( editors == null ) { return; }
		editors.pauseItem(obj);
	}

	@Override
	public void resumeItem(final T obj) {
		if ( editors == null ) { return; }
		editors.resumeItem(obj);
	}

	@Override
	public void focusItem(final T obj) {
		if ( editors == null ) { return; }
		editors.focusItem(obj);
	}

	@Override
	public List<T> getItems() {
		if ( editors == null ) { return Collections.EMPTY_LIST; }
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
