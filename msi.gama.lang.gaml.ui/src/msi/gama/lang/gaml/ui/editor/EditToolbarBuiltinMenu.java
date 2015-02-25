/**
 * Created by drogoul, 5 déc. 2014
 * 
 */
package msi.gama.lang.gaml.ui.editor;

import java.util.*;
import msi.gama.common.interfaces.INamed;
import msi.gama.lang.gaml.ui.templates.GamlTemplateFactory;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.descriptions.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.Menu;

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
		Collections.sort(list, INamed.COMPARATOR);
		title("Built-in Species");
		for ( TypeDescription species : list ) {
			fillSpeciesSubmenu(sub(species.getName()), species);
		}
		List<String> strings = new ArrayList(AbstractGamlAdditions.getSkills());
		Collections.sort(strings, IGNORE_CASE);
		title("Skills");
		for ( String skill : strings ) {
			fillSkillSubmenu(sub(skill), skill, false);
		}
		strings = new ArrayList(AbstractGamlAdditions.getControls());
		Collections.sort(strings, IGNORE_CASE);
		title("Control Architectures");
		for ( String skill : strings ) {
			fillSkillSubmenu(sub(skill), skill, true);
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
