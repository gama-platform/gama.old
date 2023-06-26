/*******************************************************************************************************
 *
 * EditorPresentationMenu.java, in ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.modeling;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.menus.IWorkbenchContribution;
import org.eclipse.ui.services.IServiceLocator;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.preferences.Pref;
import msi.gama.lang.gaml.ui.editor.GamlEditor;
import ummisco.gama.ui.resources.GamaIcon;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The Class EditorMenu.
 */
public class EditorPresentationMenu extends ContributionItem implements IWorkbenchContribution {

	/** The mark pref. */
	Pref<Boolean> markPref;

	@Override
	public void initialize(final IServiceLocator serviceLocator) {}

	@Override
	public void fill(final Menu m, final int index) {
		final MenuItem menuItem = new MenuItem(m, SWT.CASCADE);
		menuItem.setText("Presentation");
		menuItem.setImage(GamaIcon.named("display/menu.presentation").image());
		final Menu menu = new Menu(menuItem);
		if (menuItem.getMenu() != null) { menuItem.getMenu().dispose(); }
		menuItem.setMenu(menu);
		menu.addListener(SWT.Show, e -> {
			markPref = GamaPreferences.get("pref_editor_mark_occurrences", Boolean.class);
			for (final MenuItem item : menu.getItems()) { item.dispose(); }
			if (getEditor() != null) {
				createLineToggle(menu);
				createFoldingToggle(menu);
				createMarkToggle(menu);
				createBoxToggle(menu);
				createOverviewToggle(menu);
				createWordWrapToggle(menu);
				// createWhiteSpaceToggle(menu);
			}
		});

	}

	/**
	 *
	 */
	private void createBoxToggle(final Menu menu) {
		final MenuItem box = new MenuItem(menu, SWT.PUSH);
		box.setText(" Toggle code sections colorization");
		box.setImage(GamaIcon.named("editor/toggle.box").image());
		box.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final boolean selection = !getEditor().isDecorationEnabled();
				getEditor().setDecorationEnabled(selection);
				getEditor().decorate(selection);
			}
		});

	}

	/**
	 * Creates the mark toggle.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param menu
	 *            the menu
	 * @date 26 juin 2023
	 */
	private void createMarkToggle(final Menu menu) {
		final MenuItem mark = new MenuItem(menu, SWT.PUSH);
		boolean selected = markPref.getValue();
		mark.setText(selected ? " Do not mark symbols occurences" : " Mark occurences of symbols");
		// mark.setSelection(markPref.getValue());
		mark.setImage(GamaIcon.named("editor/toggle.mark").image());

		mark.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				markPref.set(mark.getSelection()).save();
			}
		});

	}

	/**
	 * Creates the mark toggle.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param menu
	 *            the menu
	 * @date 26 juin 2023
	 */
	private void createWordWrapToggle(final Menu menu) {
		final MenuItem mark = new MenuItem(menu, SWT.PUSH);
		boolean selected = getEditor().isWordWrapEnabled();
		mark.setText(selected ? " Turn Word Wrap off" : " Turn Word Wrap on");
		mark.setImage(GamaIcon.named("editor/menu.delimiter").image());

		mark.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				getEditor().setWordWrap(mark.getSelection());
			}
		});

	}

	/**
	 * Creates the overview toggle.
	 *
	 * @param menu
	 *            the menu
	 */
	private void createOverviewToggle(final Menu menu) {
		final MenuItem overview = new MenuItem(menu, SWT.PUSH);
		boolean selected = getEditor().isOverviewRulerVisible();
		overview.setText(selected ? " Hide markers overview" : " Show markers overview");
		// overview.setSelection(selected);
		overview.setImage(GamaIcon.named("editor/toggle.overview").image());
		overview.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final boolean shown = getEditor().isOverviewRulerVisible();
				if (shown) {
					getEditor().hideOverviewRuler();
				} else {
					getEditor().showOverviewRuler();
				}
			}
		});

	}

	/**
	 *
	 */
	private void createFoldingToggle(final Menu menu) {
		final MenuItem folding = new MenuItem(menu, SWT.PUSH);
		boolean selected = getEditor().isRangeIndicatorEnabled();
		folding.setText(selected ? " Unfold code sections" : " Fold code sections");

		folding.setSelection(selected);
		folding.setImage(GamaIcon.named("editor/toggle.folding").image());
		folding.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				getEditor().getAction("FoldingToggle").run();
			}
		});

	}

	/**
	 *
	 */
	private void createLineToggle(final Menu menu) {
		final MenuItem line = new MenuItem(menu, SWT.PUSH);
		boolean selected = getEditor().isLineNumberRulerVisible();
		line.setText(selected ? " Hide line numbers" : " Display line numbers");

		// line.setSelection(selected);
		line.setImage(GamaIcon.named("editor/toggle.numbers").image());
		line.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				getEditor().getAction(ITextEditorActionConstants.LINENUMBERS_TOGGLE).run();
			}
		});

	}

	/**
	 * Gets the editor.
	 *
	 * @return the editor
	 */
	GamlEditor getEditor() { return (GamlEditor) WorkbenchHelper.getActiveEditor(); }
}
