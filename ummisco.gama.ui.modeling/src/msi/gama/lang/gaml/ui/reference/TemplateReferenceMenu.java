/*******************************************************************************************************
 *
 * TemplateReferenceMenu.java, in ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.reference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.persistence.TemplatePersistenceData;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
// import org.eclipse.text.templates.TemplatePersistenceData;
import org.eclipse.ui.PartInitException;

import msi.gama.lang.gaml.ui.editor.GamlEditor;
import msi.gaml.operators.Strings;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The class EditToolbarTemplateMenu.
 *
 * @author drogoul
 * @since 5 déc. 2014
 *
 */

@SuppressWarnings ("deprecation")
public class TemplateReferenceMenu extends GamlReferenceMenu {

	/**
	 * The Class Node.
	 */
	abstract class Node {

		/** The parent. */
		final Node parent;

		/**
		 * Instantiates a new node.
		 *
		 * @param parent the parent
		 */
		Node(final Node parent) {
			this.parent = parent;
		}

		/**
		 * Gets the path.
		 *
		 * @return the path
		 */
		abstract String getPath();

		/**
		 * Gets the name.
		 *
		 * @return the name
		 */
		abstract String getName();

		/**
		 * Gets the children.
		 *
		 * @return the children
		 */
		abstract Set<Node> getChildren();

		/**
		 * Fill menu.
		 *
		 * @param m the m
		 */
		abstract void fillMenu(Menu m);

	}

	/**
	 * The Class TemplateNode.
	 */
	class TemplateNode extends Node {

		/** The name. */
		final String name;
		
		/** The desc. */
		final String desc;
		
		/** The pattern. */
		final String pattern;
		
		/** The rank. */
		final int rank;

		/**
		 * Instantiates a new template node.
		 *
		 * @param parent the parent
		 * @param t the t
		 * @param rank the rank
		 */
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
			final String s = parent.getPath();
			if (s.isEmpty()) { return "" + rank; }
			return s + "." + rank;
		}

		@Override
		Set<Node> getChildren() {
			return Collections.EMPTY_SET;
		}

		@Override
		void fillMenu(final Menu m) {
			final Menu sub = sub(m, name, desc);
			action(sub, "Insert", new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					DEBUG.OUT("PATH: " + getPath());
					DEBUG.OUT("NAME: " + getName());
					applyTemplate(store.getTemplateData(getPath()).getTemplate());
				}

			}).setToolTipText(pattern);
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

	/**
	 * The Class TemplateTree.
	 */
	class TemplateTree extends Node {

		/** The children. */
		final Set<Node> children = new LinkedHashSet<>();
		
		/** The name. */
		final String name;

		/**
		 * Instantiates a new template tree.
		 *
		 * @param parent the parent
		 * @param name the name
		 */
		TemplateTree(final Node parent, final String name) {
			super(parent);
			this.name = name;
		}

		/**
		 * Adds the.
		 *
		 * @param t the t
		 */
		void add(final TemplatePersistenceData t) {
			final String id = t.getId();
			final List<String> path = new ArrayList<>(Arrays.asList(id.split("\\.")));
			add(t, path);
		}

		/**
		 * Child with name.
		 *
		 * @param s the s
		 * @return the node
		 */
		Node childWithName(final String s) {
			for (final Node n : getChildren()) {
				if (n.getName().equals(s)) { return n; }
			}
			return null;
		}

		/**
		 * Adds the.
		 *
		 * @param t the t
		 * @param path the path
		 */
		void add(final TemplatePersistenceData t, final List<String> path) {
			if (path.size() == 0) {
				children.add(new TemplateNode(this, t, 1));
			} else if (path.size() == 1 && Strings.isGamaNumber(path.get(0))) {
				children.add(new TemplateNode(this, t, Integer.decode(path.get(0))));
			} else {
				final String name = path.remove(0);
				Node node = childWithName(name);
				if (node == null) {
					node = new TemplateTree(this, name);
					children.add(node);
				}
				((TemplateTree) node).add(t, path);
			}

		}

		@Override
		void fillMenu(final Menu parent) {
			final Menu menu = sub(parent, getName());
			for (final Node node : children) {
				node.fillMenu(menu);
			}
			sep(menu);
			action(menu, "Add new template here...", new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					final String id = getEditor().getNewTemplateId(getPath());
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
			final String s = parent.getPath();
			if (s.isEmpty()) { return getName(); }
			return s + "." + getName();
		}

		@Override
		Set<Node> getChildren() {
			return children;
		}

	}

	/**
	 * The Class TemplateRoot.
	 */
	class TemplateRoot extends TemplateTree {

		/**
		 * Instantiates a new template root.
		 */
		TemplateRoot() {
			super(null, "");
		}

		@Override
		void fillMenu(final Menu parent) {
			for (final Node node : children) {
				node.fillMenu(parent);
			}
		}

		/**
		 * Clear.
		 */
		void clear() {
			children.clear();
		}

		@Override
		String getPath() {
			return "";
		}

	}

	/** The tree. */
	TemplateRoot tree = null;
	
	/** The store. */
	TemplateStore store;

	@Override
	protected void fillMenu() {

		if (tree == null || tree.getChildren().isEmpty()) {
			tree = new TemplateRoot();
			store = getEditor().getTemplateStore();
			final TemplatePersistenceData[] templates = store.getTemplateData(false);
			for (final TemplatePersistenceData t : templates) {
				tree.add(t);
			}
		}
		tree.fillMenu(mainMenu);
	}

	@Override
	public void reset() {
		super.reset();
		tree.clear();
		tree = null;
	}

	/**
	 * Edits the template.
	 *
	 * @param dataId the data id
	 */
	public void editTemplate(final String dataId) {
		TemplatePersistenceData data = store.getTemplateData(dataId);
		final boolean edit = data != null;
		final GamlEditor editor = getEditor();
		if (data == null) {
			data = new TemplatePersistenceData(
					new Template("", "", "msi.gama.lang.gaml.Gaml.Model", editor.getSelectedText(), true), true,
					dataId);
		}
		final boolean succeed = editor.openEditTemplateDialog(data, edit);
		if (succeed) {
			reset();
		}

	}

	/**
	 * @param editor
	 */
	@Override
	protected void openView() {
		try {
			WorkbenchHelper.getPage().showView("msi.gama.lang.gaml.ui.templates");
		} catch (final PartInitException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see msi.gama.lang.gaml.ui.reference.GamlReferenceMenu#getImage()
	 */
	@Override
	protected Image getImage() {
		return GamaIcons.create("reference.templates").image();
	}

	/**
	 * @see msi.gama.lang.gaml.ui.reference.GamlReferenceMenu#getTitle()
	 */
	@Override
	protected String getTitle() {
		return "Templates";
	}

}
