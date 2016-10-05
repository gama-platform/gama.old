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
package ummisco.gama.ui.views.inspectors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import ummisco.gama.ui.experiment.parameters.EditorsList;
import ummisco.gama.ui.interfaces.IParameterEditor;
import ummisco.gama.ui.parameters.AbstractEditor;

public abstract class AttributesEditorsView<T> extends ExpandableItemsView<T> {

	final Set<Label> labels = new LinkedHashSet<Label>();

	public final static int EDITORS_SPACING = 0;

	class ItemComposite extends Composite {

		public ItemComposite(final Composite parent, final int style) {
			super(parent, style);
			final GridLayout layout = new GridLayout(2, false);
			layout.verticalSpacing = EDITORS_SPACING;
			setLayout(layout);
		}

		public void addEditor(final AbstractEditor<?> editor) {
			editor.createComposite(this);
			final Label label = editor.getLabel();
			// ((GridData) label.getLayoutData()).heightHint = 36;
			labels.add(label);
			label.addDisposeListener(e -> labels.remove(label));
		}

	}

	protected EditorsList<T> editors;

	@Override
	public String getItemDisplayName(final T obj, final String previousName) {
		if (editors == null) {
			return "";
		}
		return editors.getItemDisplayName(obj, previousName);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Composite createItemContentsFor(final T data) {
		final Map<String, IParameterEditor<?>> parameters = editors.getCategories().get(data);
		final ItemComposite compo = new ItemComposite(getViewer(), SWT.NONE);
		compo.setBackground(getViewer().getBackground());
		if (parameters != null) {
			final List<AbstractEditor> list = new ArrayList(parameters.values());
			Collections.sort(list);
			for (final AbstractEditor<?> gpParam : list) {
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
		if (editors == null) {
			return;
		}
		editors.removeItem(obj);
	}

	@Override
	public void pauseItem(final T obj) {
		if (editors == null) {
			return;
		}
		editors.pauseItem(obj);
	}

	@Override
	public void resumeItem(final T obj) {
		if (editors == null) {
			return;
		}
		editors.resumeItem(obj);
	}

	@Override
	public void focusItem(final T obj) {
		if (editors == null) {
			return;
		}
		editors.focusItem(obj);
	}

	@Override
	public List<T> getItems() {
		if (editors == null) {
			return Collections.EMPTY_LIST;
		}
		return editors.getItems();
	}

	@Override
	public void updateItemValues() {
		if (editors != null) {
			editors.updateItemValues();
		}
	}

}
