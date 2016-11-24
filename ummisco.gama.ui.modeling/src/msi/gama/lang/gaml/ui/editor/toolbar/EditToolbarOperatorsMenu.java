/*********************************************************************************************
 *
 * 'EditToolbarOperatorsMenu.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.editor.toolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.templates.Template;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;

import gnu.trove.map.hash.THashMap;
import msi.gama.lang.gaml.ui.AutoStartup;
import msi.gama.lang.gaml.ui.templates.GamlTemplateFactory;
import msi.gaml.descriptions.OperatorProto;
import msi.gaml.expressions.IExpressionCompiler;
import msi.gaml.types.Signature;
import ummisco.gama.ui.menus.GamaMenuItem;

/**
 * The class EditToolbarTemplateMenu.
 * 
 * @author drogoul
 * @since 5 déc. 2014
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class EditToolbarOperatorsMenu extends EditToolbarMenu {

	public static Boolean byName = null;

	@Override
	protected void fillMenu() {
		if (byName == null) {
			byName = AutoStartup.OPERATORS_MENU_SORT.getValue().equals("Name");
		}
		final Menu sub = sub("Sort by...");
		sep();
		check(sub, "Name", byName, new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				byName = true;
				reset();
			}
		});
		check(sub, "Category", !byName, new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				byName = false;
				reset();
			}
		});
		if (byName) {
			fillMenuByName();
		} else {
			fillMenuByCategory();
		}
	}

	protected void fillMenuByName() {
		final Map<String, THashMap<Signature, OperatorProto>> operators = IExpressionCompiler.OPERATORS;
		final List<String> nn = new ArrayList(operators.keySet());
		Collections.sort(nn, IGNORE_CASE);
		for (final String name : nn) {
			final List<OperatorProto> protos = new ArrayList<>();
			for (final Signature sig : operators.get(name).keySet()) {
				final OperatorProto proto = operators.get(name).get(sig);
				if (proto.getDeprecated() == null) {
					protos.add(proto);
				}
			}
			if (protos.isEmpty()) {
				continue;
			}
			final Menu name_menu = sub(name);
			for (final OperatorProto proto : protos) {
				final Template t = GamlTemplateFactory.from(proto);
				final GamaMenuItem item = action(name_menu,
						"(" + proto.signature.asPattern(false) + ") -> " + proto.returnType.serialize(true),
						new SelectionAdapter() {

							@Override
							public void widgetSelected(final SelectionEvent event) {
								applyTemplate(t);
							}
						});
				item.setTooltipText(t.getDescription());
			}
		}
	}

	protected void fillMenuByCategory() {
		final Map<String, THashMap<Signature, OperatorProto>> operators = IExpressionCompiler.OPERATORS;
		final Map<String, Map<String, Map<OperatorProto, Template>>> categories = new LinkedHashMap();
		final List<String> nn = new ArrayList(operators.keySet());
		Collections.sort(nn, IGNORE_CASE);
		for (final String name : nn) {
			final Map<Signature, OperatorProto> ops = operators.get(name);
			for (final Signature sig : ops.keySet()) {
				final OperatorProto proto = ops.get(sig);
				if (proto.getDeprecated() != null) {
					continue;
				}
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
					final GamaMenuItem item = action(name_menu,
							"(" + proto.signature.asPattern(false) + ") -> " + proto.returnType.serialize(true),
							new SelectionAdapter() {

								@Override
								public void widgetSelected(final SelectionEvent event) {
									applyTemplate(t);
								}
							});
					item.setTooltipText(t.getDescription());
				}

			}
		}

	}

	@Override
	protected void openView() {
	}

}
