/**
 * Created by drogoul, 4 déc. 2014
 *
 */
package msi.gama.lang.gaml.ui.editor;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import msi.gama.lang.gaml.indexer.IModelIndexer;
import msi.gama.runtime.GAMA;
import ummisco.gama.ui.controls.FlatButton;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.views.toolbar.GamaToolbar2;

/**
 * The class OtherExperimentsButton.
 *
 * @author drogoul
 * @since 4 déc. 2014
 *
 */
public class ImportedInButton {

	final GamlEditor editor;
	final GamaToolbar2 parent;
	ToolItem menu;
	final IModelIndexer indexer;

	public ImportedInButton(final GamlEditor editor, final GamaToolbar2 toolbar, final IModelIndexer indexer) {
		this.editor = editor;
		this.parent = toolbar;
		this.indexer = indexer;
		// if (AutoStartup.EDITOR_SHOW_OTHER.getValue()) {
		createButton();
		// }
	}

	private void createButton() {
		// parent.sep(5, SWT.RIGHT);
		menu = parent.menu(IGamaColors.BLUE, "Used in...", SWT.RIGHT);
		menu.getControl().setToolTipText("List of models in which this model is imported");
		((FlatButton) menu.getControl()).addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Menu old = (Menu) menu.getData();
				menu.setData(null);
				if (old != null) {
					old.dispose();
				}
				final Menu dropMenu = createImportersSubMenu();
				menu.setData(dropMenu);
				final Rectangle rect = menu.getBounds();
				Point pt = new Point(rect.x, rect.y + rect.height);
				pt = menu.getControl().toDisplay(pt);
				dropMenu.setLocation(pt.x, pt.y);
				dropMenu.setVisible(true);
			}

		});
		// setVisible(XtextGui.EDITOR_SHOW_OTHER.getValue());
	}

	private final SelectionAdapter adapter = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			final MenuItem mi = (MenuItem) e.widget;
			final URI uri = (URI) mi.getData("uri");
			GAMA.getGui().editModel(uri);
		}
	};

	public Menu createImportersSubMenu() {
		final Menu parentMenu = new Menu(this.parent);
		final Set<URI> importers = getImporters();
		if (importers.isEmpty()) {
			final MenuItem nothing = new MenuItem(parentMenu, SWT.PUSH);
			nothing.setText("No importers");
			nothing.setEnabled(false);
			return parentMenu;
		}
		for (final URI uri : importers) {
			final MenuItem modelItem = new MenuItem(parentMenu, SWT.CASCADE);
			modelItem.setText(URI.decode(uri.lastSegment()));
			modelItem.setImage(IGamaIcons.FILE_ICON.image());
			modelItem.setData("uri", uri);
			modelItem.addSelectionListener(adapter);
		}
		return parentMenu;
	}

	private Set<URI> getImporters() {
		final Set<URI> map = new LinkedHashSet();
		editor.getDocument().readOnly(new IUnitOfWork.Void<XtextResource>() {

			@Override
			public void process(final XtextResource resource) throws Exception {
				final String platformString = resource.getURI().toPlatformString(true);
				final URI uri = URI.createPlatformResourceURI(platformString, false);
				if (indexer.isImported(uri))
					map.addAll(indexer.directImportersOf(uri));
			}
		});
		return map;
	}

	/**
	 * @param showOtherEnabled
	 */
	public void setVisible(final boolean enabled) {
		if (enabled) {
			if (menu != null) {
				return;
			}
			createButton();
		} else {
			if (menu == null) {
				return;
			}
			menu.dispose();
			menu = null;
		}
	}

}
