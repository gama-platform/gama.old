/**
 * Created by drogoul, 5 déc. 2014
 * 
 */
package msi.gama.lang.gaml.ui.editor;

import java.util.*;
import msi.gama.gui.swt.SwtGui;
import msi.gaml.operators.Strings;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.persistence.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PartInitException;

/**
 * The class EditToolbarTemplateMenu.
 * 
 * @author drogoul
 * @since 5 déc. 2014
 * 
 */
public class EditToolbarTemplateMenu extends EditToolbarMenu {

	abstract class Node {

		final Node parent;

		Node(final Node parent) {
			this.parent = parent;
		}

		abstract String getPath();

		abstract String getName();

		abstract Set<Node> getChildren();

		abstract void fillMenu(Menu m);

	}

	class TemplateNode extends Node {

		final String name;
		final String desc;
		final String pattern;
		final int rank;

		TemplateNode(final Node parent, final TemplatePersistenceData t, final int rank) {
			super(parent);
			name = t.getTemplate().getName();
			desc = t.getTemplate().getDescription();
			pattern = t.getTemplate().getPattern();
			this.rank = rank;
		}

		@Override
		String getName() {
			return name + ": " + desc;
		}

		@Override
		String getPath() {
			String s = parent.getPath();
			if ( s.isEmpty() ) { return "" + rank; }
			return s + "." + rank;
		}

		@Override
		Set<Node> getChildren() {
			return Collections.EMPTY_SET;
		}

		@Override
		void fillMenu(final Menu m) {
			Menu sub = sub(m, name, desc);
			action(sub, "Insert", new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					applyTemplate(store.getTemplateData(getPath()).getTemplate());
				}

			}).setTooltipText(pattern);
			action(sub, "Edit...", new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					editTemplate(getPath());
				}

			});
			action(sub, "Delete...", new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					store.delete(store.getTemplateData(getPath()));
					reset();
				}

			});
		}

	}

	class TemplateTree extends Node {

		final Set<Node> children = new LinkedHashSet();
		final String name;

		TemplateTree(final Node parent, final String name) {
			super(parent);
			this.name = name;
		}

		void add(final TemplatePersistenceData t) {
			String id = t.getId();
			List<String> path = new ArrayList(Arrays.asList(id.split("\\.")));
			add(t, path);
		}

		Node childWithName(final String s) {
			for ( Node n : getChildren() ) {
				if ( n.getName().equals(s) ) { return n; }
			}
			return null;
		}

		void add(final TemplatePersistenceData t, final List<String> path) {
			if ( path.size() == 0 ) {
				children.add(new TemplateNode(this, t, 1));
			} else if ( path.size() == 1 && Strings.isGamaNumber(path.get(0)) ) {
				children.add(new TemplateNode(this, t, Integer.decode(path.get(0))));
			} else {
				String name = path.remove(0);
				Node node = childWithName(name);
				if ( node == null ) {
					node = new TemplateTree(this, name);
					children.add(node);
				}
				((TemplateTree) node).add(t, path);
			}

		}

		@Override
		void fillMenu(final Menu parent) {
			Menu menu = sub(parent, getName());
			for ( Node node : children ) {
				node.fillMenu(menu);
			}
			sep(menu);
			action(menu, "Add new template here...", new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					String id = getEditor().getNewTemplateId(getPath());
					editTemplate(id);
				}

			});
		}

		@Override
		String getName() {
			return name;
		}

		@Override
		String getPath() {
			String s = parent.getPath();
			if ( s.isEmpty() ) { return getName(); }
			return s + "." + getName();
		}

		@Override
		Set<Node> getChildren() {
			return children;
		}

	}

	class TemplateRoot extends TemplateTree {

		TemplateRoot() {
			super(null, "");
		}

		@Override
		void fillMenu(final Menu parent) {
			for ( Node node : children ) {
				node.fillMenu(parent);
			}
		}

		void clear() {
			children.clear();
		}

		@Override
		String getPath() {
			return "";
		}

	}

	TemplateRoot tree = null;
	TemplateStore store;

	@Override
	protected void fillMenu() {
		if ( tree == null || tree.getChildren().isEmpty() ) {
			tree = new TemplateRoot();
			store = getEditor().getTemplateStore();
			TemplatePersistenceData[] templates = store.getTemplateData(false);
			for ( final TemplatePersistenceData t : templates ) {
				tree.add(t);
			}
		}
		tree.fillMenu(mainMenu);
	}

	@Override
	protected void reset() {
		super.reset();
		tree.clear();
		tree = null;
	}

	public void editTemplate(final String dataId) {
		TemplatePersistenceData data = store.getTemplateData(dataId);
		boolean edit = data != null;
		GamlEditor editor = getEditor();
		if ( data == null ) {
			data =
				new TemplatePersistenceData(new Template("", "", "msi.gama.lang.gaml.Gaml.Model",
					editor.getSelectedText(), true), true, dataId);
		}
		boolean succeed = editor.openEditTemplateDialog(data, edit);
		if ( succeed ) {
			reset();
		}

	}

	/**
	 * @param editor
	 */
	@Override
	protected void openView() {
		try {
			SwtGui.getPage().showView("msi.gama.lang.gaml.ui.templates");
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

}
