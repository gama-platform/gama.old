/**
 * Created by drogoul, 6 déc. 2014
 * 
 */
package ummisco.gama.ui.modeling.editor;

import org.eclipse.swt.events.SelectionEvent;

/**
 * The class EditToolbarMenu.
 * 
 * @author drogoul
 * @since 6 déc. 2014
 * 
 */
public class EditToolbarMenuFactory {

	static final String EDITOR_KEY = "editor";

	static private EditToolbarMenuFactory instance = new EditToolbarMenuFactory();
	EditToolbarMenu colorMenu, templateMenu, builtInMenu, operatorsMenu;

	public EditToolbarMenu getColorMenu() {
		return colorMenu;
	}

	public EditToolbarMenu getTemplateMenu() {
		return templateMenu;
	}

	public EditToolbarMenu getBuiltInMenu() {
		return builtInMenu;
	}

	public EditToolbarMenu getOperatorsMenu() {
		return operatorsMenu;
	}

	public static EditToolbarMenuFactory getInstance() {
		return instance;
	}

	public void openColorMenu(final GamlEditor editor, final SelectionEvent trigger) {
		if ( colorMenu == null ) {
			colorMenu = new EditToolbarColorMenu();
		}
		colorMenu.open(editor, trigger);
	}

	public void openTemplateMenu(final GamlEditor editor, final SelectionEvent trigger) {
		if ( templateMenu == null ) {
			templateMenu = new EditToolbarTemplateMenu();
		}
		templateMenu.open(editor, trigger);
	}

	public void openBuiltInMenu(final GamlEditor editor, final SelectionEvent trigger) {
		if ( builtInMenu == null ) {
			builtInMenu = new EditToolbarBuiltinMenu();
		}
		builtInMenu.open(editor, trigger);
	}

	public void openOperatorsMenu(final GamlEditor editor, final SelectionEvent trigger) {
		if ( operatorsMenu == null ) {
			operatorsMenu = new EditToolbarOperatorsMenu();
		}
		operatorsMenu.open(editor, trigger);
	}

}
