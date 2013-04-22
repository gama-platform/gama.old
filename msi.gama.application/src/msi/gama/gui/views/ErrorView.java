/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.views;

import java.util.*;
import java.util.List;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.parameters.EditorFactory;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class ErrorView extends ExpandableItemsView<GamaRuntimeException> {

	public static String ID = GuiUtils.ERROR_VIEW_ID;

	private final ArrayList<GamaRuntimeException> exceptions = new ArrayList();
	static private int numberOfDisplayedErrors = 10;
	static private boolean mostRecentFirst = true;
	static public boolean showErrors = true;

	// ParameterExpandItem parametersItem;

	@Override
	protected boolean areItemsClosable() {
		return true;
	}

	@Override
	public boolean addItem(final GamaRuntimeException e) {
		createItem(e, false);
		return true;
	}

	public void addNewError(final GamaRuntimeException e) {
		if ( e != null ) {
			exceptions.add(e);
		}
		if ( showErrors ) {
			reset();
			displayItems();
		}
	}

	/**
	 * @see msi.gama.gui.views.GamaViewPart#getToolbarActionsId()
	 */
	@Override
	protected Integer[] getToolbarActionsId() {
		// TODO Need to be defined and usable (not the case now)
		return new Integer[] { PAUSE /* , CLEAR */};
	}

	@Override
	public void ownCreatePartControl(final Composite view) {
		super.ownCreatePartControl(view);
		Composite intermediate = new Composite(view, SWT.VERTICAL);
		GridLayout parentLayout = new GridLayout(1, false);
		parentLayout.marginWidth = 0;
		parentLayout.marginHeight = 0;
		parentLayout.verticalSpacing = 0;
		intermediate.setLayout(parentLayout);

		// ExpandableComposite expandable = new ExpandableComposite(intermediate, SWT.SHADOW_IN);
		// expandable.setText("Preferences");
		Composite parameters = new Group(intermediate, SWT.None);
		// expandable.setClient(parameters);
		// expandable.addExpansionListener(new ExpansionAdapter() {
		//
		// @Override
		// public void expansionStateChanged(final ExpansionEvent e) {
		// view.layout(true);
		// }
		// });
		GridLayout layout = new GridLayout(2, false);

		parameters.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		layout.verticalSpacing = 0;
		parameters.setLayout(layout);

		// final IntEditor ed =
		EditorFactory.create(parameters, "Display last ", null, numberOfDisplayedErrors, 0, 100, 1, false,
			new EditorListener<Integer>() {

				@Override
				public void valueModified(final Integer newValue) {
					if ( newValue == numberOfDisplayedErrors ) { return; }
					numberOfDisplayedErrors = newValue;
					reset();
					displayItems();
				}

			});

		// EditorFactory.create(parameters, "Pause and reveal in editor",
		// GAMA.TREAT_ERRORS_AS_FATAL,
		// new EditorListener<Boolean>() {
		//
		// @Override
		// public void valueModified(final Boolean newValue) {
		//
		// GAMA.TREAT_ERRORS_AS_FATAL = newValue;
		// }
		//
		// });
		// EditorFactory.create(parameters, "Treat warnings as errors ",
		// GAMA.TREAT_WARNINGS_AS_ERRORS, new EditorListener<Boolean>() {
		//
		// @Override
		// public void valueModified(final Boolean newValue) {
		//
		// GAMA.TREAT_WARNINGS_AS_ERRORS = newValue;
		// }
		//
		// });

		EditorFactory.create(parameters, "Most recent first", mostRecentFirst, new EditorListener<Boolean>() {

			@Override
			public void valueModified(final Boolean newValue) {

				mostRecentFirst = newValue;
				reset();
				displayItems();
			}

		});
		// EditorFactory.create(parameters, "Show errors/warnings", showErrors,
		// new EditorListener<Boolean>() {
		//
		// @Override
		// public void valueModified(final Boolean newValue) {
		//
		// showErrors = newValue;
		// if ( showErrors ) {
		// reset();
		// displayItems();
		// }
		//
		// }
		//
		// });
		parameters.pack();
		parent = intermediate;
	}

	@Override
	protected Composite createItemContentsFor(final GamaRuntimeException e) {
		Composite compo = new Composite(getViewer(), SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		GridData firstColData = new GridData(SWT.FILL, SWT.FILL, true, false);
		layout.verticalSpacing = 5;
		compo.setLayout(layout);
		Table t = new Table(compo, SWT.V_SCROLL);
		t.setLayoutData(firstColData);
		java.util.List<String> strings = e.getContextAsList();
		// t.setLinesVisible(true);
		final TableColumn c = new TableColumn(t, SWT.NONE);
		c.setResizable(true);
		final TableColumn column2 = new TableColumn(t, SWT.NONE);
		for ( int i = 0; i < strings.size(); i++ ) {
			TableItem item = new TableItem(t, SWT.NONE);
			item.setText(new String[] { String.valueOf(i), strings.get(i) });
		}
		c.pack();
		column2.pack();
		t.pack();
		return compo;
	}

	@Override
	public void removeItem(final GamaRuntimeException obj) {
		exceptions.remove(obj);
	}

	@Override
	public void pauseItem(final GamaRuntimeException obj) {}

	@Override
	public void resumeItem(final GamaRuntimeException obj) {}

	@Override
	public String getItemDisplayName(final GamaRuntimeException obj, final String previousName) {
		StringBuilder sb = new StringBuilder(300);
		if ( obj instanceof GamaRuntimeException ) {
			String a = obj.getAgent();
			if ( a != null ) {
				sb.append(a).append(" at ");
			}
		}
		sb.append("cycle ").append(obj.getCycle()).append(ItemList.SEPARATION_CODE)
			.append(obj.isWarning() ? ItemList.WARNING_CODE : ItemList.ERROR_CODE).append(obj.getMessage());
		return sb.toString();
	}

	@Override
	public void focusItem(final GamaRuntimeException data) {}

	@Override
	public List<GamaRuntimeException> getItems() {
		List<GamaRuntimeException> errors = new ArrayList();
		int size = exceptions.size();
		if ( size == 0 ) { return errors; }
		int end = size;
		int begin = end - numberOfDisplayedErrors;
		begin = begin < 0 ? 0 : begin;

		errors.addAll(exceptions.subList(begin, end));
		if ( mostRecentFirst ) {
			Collections.reverse(errors);
		}
		return errors;
	}

	@Override
	public void updateItemValues() {}

	public void clearErrors() {
		this.reset();
		exceptions.clear();
		displayItems();
	}

}
