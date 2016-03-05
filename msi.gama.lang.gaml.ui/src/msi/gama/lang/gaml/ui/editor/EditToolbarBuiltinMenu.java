/**
 * Created by drogoul, 5 déc. 2014
 *
 */
package msi.gama.lang.gaml.ui.editor;

import java.util.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.Menu;
import msi.gama.common.interfaces.INamed;
import msi.gama.lang.gaml.ui.templates.GamlTemplateFactory;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.descriptions.*;
import msi.gaml.types.Types;

/**
 * The class EditToolbarTemplateMenu.
 *
 * @author drogoul
 * @since 5 déc. 2014
 *
 */
public class EditToolbarBuiltinMenu extends EditToolbarMenu {

	@Override
	protected void fillMenu() {
		List<TypeDescription> list = new ArrayList(ModelDescription.ROOT.getTypesManager().getAllSpecies());
		List<String> speciesList = new ArrayList();
		Collections.sort(list, INamed.COMPARATOR);
		Menu m = sub("Built-in species");
		for ( TypeDescription species : list ) {
			speciesList.add(species.getName());
			fillSpeciesSubmenu(sub(m, species.getName()), species);
		}
		List<String> skills = new ArrayList(AbstractGamlAdditions.getSkills());
		Collections.sort(skills, IGNORE_CASE);
		m = sub("Skills");
		for ( String skill : skills ) {
			fillSkillSubmenu(sub(m, skill), skill, false);
		}
		List<String> controls = new ArrayList(AbstractGamlAdditions.getControls());
		Collections.sort(controls, IGNORE_CASE);
		m = sub("Control architectures");
		for ( String skill : controls ) {
			fillSkillSubmenu(sub(m, skill), skill, true);
		}
		List<String> types = new ArrayList(Types.getTypeNames());
		types.removeAll(speciesList);
		Collections.sort(types, IGNORE_CASE);
		m = sub("Types");
		List<String> fileTypes = new ArrayList();
		for ( String type : types ) {
			if ( type.contains("_file") ) {
				fileTypes.add(type);
			}
		}
		types.removeAll(fileTypes);
		for ( String type : types ) {
			fillTypeSubmenu(sub(m, type), type);
		}
		m = sub("File types");
		for ( String type : fileTypes ) {
			fillTypeSubmenu(sub(m, type), type);
		}
	}

	/**
	 * @param sub
	 * @param type
	 */
	private void fillTypeSubmenu(final Menu submenu, final String type) {
		action(submenu, "Insert new attribute with this type", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				applyTemplate(GamlTemplateFactory.attributeWithType(type));
			}

		});
		Map<String, OperatorProto> getters = Types.get(type).getFieldGetters();
		List<String> names = new ArrayList(getters.keySet());
		if ( !names.isEmpty() ) {
			Collections.sort(names);
			title(submenu, "Attributes");
			for ( String getter : names ) {
				fillProtoSubMenu(sub(submenu, getter), getters.get(getter));
			}
		}
	}

	private void fillSkillSubmenu(final Menu submenu, final String skill, final boolean isControl) {
		action(submenu, "Insert name", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				getEditor().insertText(skill);
			}

		});
		action(submenu, "Insert new species with this " + (isControl ? "control" : "skill"), new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if ( !isControl ) {
					applyTemplate(GamlTemplateFactory.speciesWithSkill(skill));
				} else {
					applyTemplate(GamlTemplateFactory.speciesWithControl(skill));
				}
			}

		});
		List<IDescription> vars = new ArrayList(AbstractGamlAdditions.getVariablesForSkill(skill));
		Collections.sort(vars, INamed.COMPARATOR);
		if ( !vars.isEmpty() ) {
			title(submenu, "Attributes");
			for ( IDescription variable : vars ) {
				fillIDescriptionSubMenu(sub(submenu, variable.getName() + " (" + variable.getType() + ")"), variable);
			}
		}
		List<IDescription> actions = new ArrayList(AbstractGamlAdditions.getActionsForSkill(skill));
		Collections.sort(actions, INamed.COMPARATOR);
		if ( !actions.isEmpty() ) {
			title(submenu, "Primitives");
			for ( IDescription action : actions ) {
				fillIDescriptionSubMenu(sub(submenu, action.getName()), action);
			}
		}
		if ( isControl ) {
			List<SymbolProto> controls = new ArrayList(AbstractGamlAdditions.getStatementsForSkill(skill));
			Collections.sort(controls, INamed.COMPARATOR);
			if ( !controls.isEmpty() ) {
				title(submenu, "Control statements");
				for ( SymbolProto control : controls ) {
					fillProtoSubMenu(sub(submenu, control.getName()), control);
				}
			}
		}
	}

	private void fillProtoSubMenu(final Menu menu, final SymbolProto statement) {
		action(menu, "Insert statement name", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				getEditor().insertText(statement.getName());
			}

		});

	}

	private void fillProtoSubMenu(final Menu menu, final OperatorProto attribute) {
		action(menu, "Insert attribute name", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				getEditor().insertText(attribute.getName());
			}

		});;

	}

	private void fillSpeciesSubmenu(final Menu submenu, final TypeDescription species) {
		action(submenu, "Insert name", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				getEditor().insertText(species.getName());
			}

		});
		action(submenu, "Insert new child species", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				applyTemplate(GamlTemplateFactory.speciesWithParent(species));
			}

		});
		List<String> vars = new ArrayList(species.getVarNames());
		Collections.sort(vars, IGNORE_CASE);
		if ( !vars.isEmpty() ) {
			title(submenu, "Attributes");
			for ( String v : vars ) {
				final VariableDescription variable = species.getVariable(v);
				if ( !variable.isSyntheticSpeciesContainer() && variable.getOriginName().endsWith(species.getName()) ) {
					fillIDescriptionSubMenu(sub(submenu, v + " (" + variable.getType() + ")"), variable);
				}
			}
		}
		List<String> actions = new ArrayList(species.getActionNames());
		Collections.sort(actions, IGNORE_CASE);
		if ( !actions.isEmpty() ) {
			title(submenu, "Primitives");
			for ( String v : actions ) {
				final StatementDescription prim = species.getAction(v);
				if ( prim.getOriginName().endsWith(species.getName()) ) {
					fillIDescriptionSubMenu(sub(submenu, v), prim);
				}
			}
		}
	}

	private void fillIDescriptionSubMenu(final Menu submenu, final IDescription v) {
		boolean isVar = v instanceof VariableDescription;
		action(submenu, "Insert name", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				getEditor().insertText(v.getName());
			}

		});
		if ( isVar ) {
			action(submenu, "Insert redefinition", new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					getEditor().insertText(v.serialize(true));
				}

			});
		} else {
			action(submenu, "Insert call", new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					getEditor().applyTemplate(GamlTemplateFactory.callToAction((StatementDescription) v));
				}

			});
		}
	}

	@Override
	protected void openView() {}

}
