/*******************************************************************************************************
 *
 * OperatorsReferenceMenu.java, in ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.reference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.templates.Template;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.lang.gaml.ui.templates.GamlTemplateFactory;
import msi.gama.util.IMap;
import msi.gaml.compilation.GAML;
import msi.gaml.descriptions.OperatorProto;
import msi.gaml.types.Signature;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaIcons;

/**
 * The class EditToolbarTemplateMenu.
 *
 * @author drogoul
 * @since 5 déc. 2014
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class OperatorsReferenceMenu extends GamlReferenceMenu {

	/** The by name. */
	public static Boolean byName = null;

	@Override
	protected void fillMenu() {
		if (byName == null) { byName = "Name".equals(GamaPreferences.Modeling.OPERATORS_MENU_SORT.getValue()); }
		// final Menu sub = sub("Sort by...");
		// sep();
		// check(sub, "Name", byName, new SelectionAdapter() {
		//
		// @Override
		// public void widgetSelected(final SelectionEvent event) {
		// byName = true;
		// reset();
		// }
		// });
		// check(sub, "Category", !byName, new SelectionAdapter() {
		//
		// @Override
		// public void widgetSelected(final SelectionEvent event) {
		// byName = false;
		// reset();
		// }
		// });
		if (byName) {
			fillMenuByName();
		} else {
			fillMenuByCategory();
		}
	}

	/**
	 * Fill menu by name.
	 */
	protected void fillMenuByName() {
		final Map<String, IMap<Signature, OperatorProto>> operators = GAML.OPERATORS;
		final List<String> nn = new ArrayList(operators.keySet());
		Collections.sort(nn, IGNORE_CASE);
		for (final String name : nn) {
			final List<OperatorProto> protos = new ArrayList<>();
			for (final Signature sig : operators.get(name).keySet()) {
				final OperatorProto proto = operators.get(name).get(sig);
				if (proto.getDeprecated() == null) { protos.add(proto); }
			}
			if (protos.isEmpty()) { continue; }
			final Menu name_menu = sub(name);
			for (final OperatorProto proto : protos) {
				final Template t = GamlTemplateFactory.from(proto);
				final MenuItem item = action(name_menu,
						"(" + proto.signature.asPattern(false) + ") -> " + proto.returnType.serialize(true),
						new SelectionAdapter() {

							@Override
							public void widgetSelected(final SelectionEvent event) {
								applyTemplate(t);
							}
						});
				item.setToolTipText(t.getDescription());
			}
		}
	}

	/**
	 * Fill menu by category.
	 */
	protected void fillMenuByCategory() {
		final Map<String, IMap<Signature, OperatorProto>> operators = GAML.OPERATORS;
		final Map<String, Map<String, Map<OperatorProto, Template>>> categories = new LinkedHashMap();
		final List<String> nn = new ArrayList(operators.keySet());
		Collections.sort(nn, IGNORE_CASE);
		for (final String name : nn) {
			final Map<Signature, OperatorProto> ops = operators.get(name);
			for (final Signature sig : ops.keySet()) {
				final OperatorProto proto = ops.get(sig);
				if (proto.getDeprecated() != null) { continue; }
				final String category = proto.getCategory().replace("-related", "");
				Map<String, Map<OperatorProto, Template>> names = categories.get(category);
				if (names == null) {
					names = new LinkedHashMap();
					categories.put(category, names);
				}
				Map<OperatorProto, Template> templates = names.get(name);
				if (templates == null) {
					templates = new LinkedHashMap();
					names.put(name, templates);
				}
				templates.put(proto, GamlTemplateFactory.from(proto));
			}
		}
		final List<String> cc = new ArrayList(categories.keySet());
		Collections.sort(cc, IGNORE_CASE);
		for (final String category : cc) {
			final Menu category_menu = sub(category);
			final List<String> nn2 = new ArrayList(categories.get(category).keySet());
			Collections.sort(nn2, IGNORE_CASE);
			for (final String name : nn2) {
				final List<OperatorProto> protos = new ArrayList(categories.get(category).get(name).keySet());
				//
				final Menu name_menu = sub(category_menu, name);
				for (final OperatorProto proto : protos) {
					final Template t = categories.get(category).get(name).get(proto);
					final MenuItem item = action(name_menu,
							"(" + proto.signature.asPattern(false) + ") -> " + proto.returnType.serialize(true),
							new SelectionAdapter() {

								@Override
								public void widgetSelected(final SelectionEvent event) {
									applyTemplate(t);
								}
							});
					item.setToolTipText(t.getDescription());
				}

			}
		}

	}

	@Override
	protected void openView() {}

	/**
	 * @see msi.gama.lang.gaml.ui.reference.GamlReferenceMenu#getImage()
	 */
	@Override
	protected Image getImage() { return GamaIcons.create(IGamaIcons.REFERENCE_OPERATORS).image(); }

	/**
	 * @see msi.gama.lang.gaml.ui.reference.GamlReferenceMenu#getTitle()
	 */
	@Override
	protected String getTitle() { return "Operators"; }

}
