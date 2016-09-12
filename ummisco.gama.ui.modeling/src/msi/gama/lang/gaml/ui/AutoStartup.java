package msi.gama.lang.gaml.ui;

import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import msi.gama.common.GamaPreferences;
import msi.gama.common.GamaPreferences.Entry;
import msi.gama.common.GamaPreferences.IPreferenceChangeListener;
import msi.gama.lang.gaml.GamlRuntimeModule;
import msi.gama.lang.gaml.ui.editor.EditToolbar;
import msi.gama.lang.gaml.ui.editor.EditToolbar.IToolbarVisitor;
import msi.gama.lang.gaml.ui.editor.EditToolbarOperatorsMenu;
import msi.gama.lang.gaml.ui.editor.GamlEditor;
import msi.gama.util.GamaFont;
import msi.gaml.types.IType;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class AutoStartup implements IStartup {

	public static GamaPreferences.Entry<String> OPERATORS_MENU_SORT = GamaPreferences
			.create("menu.operators.sort", "Sort operators menu by", "Category", IType.STRING).among("Name", "Category")
			.in(GamaPreferences.UI).group("Menus").addChangeListener(new IPreferenceChangeListener<String>() {

				@Override
				public boolean beforeValueChange(final String newValue) {
					return true;
				}

				@Override
				public void afterValueChange(final String newValue) {
					EditToolbarOperatorsMenu.byName = newValue.equals("Name");
					EditToolbar.visitToolbars(new IToolbarVisitor() {

						@Override
						public void visit(final EditToolbar toolbar) {
							toolbar.resetOperatorsMenu();
						}
					});
				}
			});
	public static final Entry<Boolean> CORE_CLOSE_CURLY = GamaPreferences
			.create("core.close.curly", "Automatically close curly brackets ( { )", true, IType.BOOL)
			.in(GamaPreferences.EDITOR).group("Options");
	public static final Entry<Boolean> CORE_CLOSE_SQUARE = GamaPreferences
			.create("core.close.square", "Automatically close square brackets ( [ )", true, IType.BOOL)
			.in(GamaPreferences.EDITOR).group("Options");
	public static final Entry<Boolean> CORE_CLOSE_PARENTHESES = GamaPreferences
			.create("core.close.parentheses", "Automatically close parentheses", true, IType.BOOL)
			.in(GamaPreferences.EDITOR).group("Options");
	public static final GamaPreferences.Entry<Boolean> EDITOR_CLEAN_UP = GamaPreferences
			.create("editor.cleanup.save", "Apply formatting to models on save", false, IType.BOOL)
			.in(GamaPreferences.EDITOR).group("Options");
	public static final GamaPreferences.Entry<Boolean> EDITOR_SAVE = GamaPreferences
			.create("editor.save.all", "Save all model files before lauching an experiment", true, IType.BOOL)
			.in(GamaPreferences.EDITOR).group("Options").activates("editor.save.ask");
	public static final GamaPreferences.Entry<Boolean> EDITOR_SAVE_ASK = GamaPreferences
			.create("editor.save.ask", "Ask before saving each file", false, IType.BOOL).in(GamaPreferences.EDITOR)
			.group("Options");
	public static final GamaPreferences.Entry<Boolean> EDITBOX_ENABLED = GamaPreferences
			.create("editor.editbox.enabled", "Turn on colorization of code sections by default", false, IType.BOOL)
			.in(GamaPreferences.EDITOR).group("Presentation");
	public static final GamaPreferences.Entry<Boolean> EDITOR_SHOW_TOOLBAR = GamaPreferences
			.create("editor.show.toolbar", "Show edition toolbar by default", true, IType.BOOL)
			.in(GamaPreferences.EDITOR).group("Toolbars");
	static final GamaPreferences.Entry<GamaFont> EDITOR_BASE_FONT = GamaPreferences
			.create("editor.font", "Font of editors", getDefaultFontData(), IType.FONT).in(GamaPreferences.EDITOR)
			.group("Presentation").addChangeListener(new IPreferenceChangeListener<GamaFont>() {

				@Override
				public boolean beforeValueChange(final GamaFont newValue) {
					return true;
				}

				@Override
				public void afterValueChange(final GamaFont font) {
					try {
						final FontData newValue = new FontData(font.getName(), font.getSize(), font.getStyle());
						PreferenceConverter.setValue(EditorsPlugin.getDefault().getPreferenceStore(),
								JFaceResources.TEXT_FONT, newValue);
					} catch (final Exception e) {
						// System.out.println("Exception ignored in Editor base
						// font afterValueChange: " + e.getMessage());
					}
					// IPreferencesService preferencesService =
					// Platform.getPreferencesService();
					// Preferences preferences =
					// preferencesService.getRootNode().node("/instance/" +
					// "org.eclipse.ui.workbench");
					// preferences.put("org.eclipse.jface.textfont",
					// newValue.toString());
					// try {
					// preferences.flush();
					// } catch (BackingStoreException e) {}
				}
			});
	public static final GamaPreferences.Entry<java.awt.Color> EDITOR_BACKGROUND_COLOR = GamaPreferences
			.create("editor.background.color", "Background color of editors", getDefaultBackground(), IType.COLOR)
			.in(GamaPreferences.EDITOR).group("Presentation")
			.addChangeListener(new IPreferenceChangeListener<java.awt.Color>() {

				@Override
				public boolean beforeValueChange(final java.awt.Color newValue) {
					return true;
				}

				@Override
				public void afterValueChange(final java.awt.Color c) {
					final RGB rgb = new RGB(c.getRed(), c.getGreen(), c.getBlue());
					PreferenceConverter.setValue(EditorsPlugin.getDefault().getPreferenceStore(),
							AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND, rgb);
					// IPreferencesService preferencesService =
					// Platform.getPreferencesService();
					// Preferences preferences =
					// preferencesService.getRootNode().node("/instance/" +
					// "org.eclipse.ui.workbench");
					// preferences.put("org.eclipse.jface.textfont",
					// newValue.toString());
					// try {
					// preferences.flush();
					// } catch (BackingStoreException e) {}
				}
			});
	public static final GamaPreferences.Entry<Boolean> EDITOR_SHOW_OTHER = GamaPreferences
			.create("editor.show.other", "Show other models' experiments in toolbar", false, IType.BOOL)
			.in(GamaPreferences.EDITOR).group("Toolbars").addChangeListener(new IPreferenceChangeListener<Boolean>() {

				@Override
				public boolean beforeValueChange(final Boolean newValue) {
					return true;
				}

				@Override
				public void afterValueChange(final Boolean newValue) {
					final IEditorReference[] eds = WorkbenchHelper.getPage().getEditorReferences();
					for (final IEditorReference ed : eds) {
						final IEditorPart e = ed.getEditor(false);
						if (e instanceof GamlEditor) {
							((GamlEditor) e).setShowOtherEnabled(newValue);
						}
					}
				}
			});

	private static java.awt.Color getDefaultBackground() {
		EditorsPlugin.getDefault().getPreferenceStore()
				.setValue(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT, false);
		final RGB rgb = PreferenceConverter.getColor(EditorsPlugin.getDefault().getPreferenceStore(),
				AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND);
		return new java.awt.Color(rgb.red, rgb.green, rgb.blue);
	}

	public static GamaFont getDefaultFontData() {
		final FontData fd = PreferenceConverter.getFontData(EditorsPlugin.getDefault().getPreferenceStore(),
				JFaceResources.TEXT_FONT);
		return new GamaFont(fd.getName(), fd.getStyle(), fd.getHeight());
	}

	@Override
	public void earlyStartup() {

		GamlRuntimeModule.staticInitialize();
		// try {
		// WorkspaceIndexer.INSTANCE.buildIndex();
		// ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.CLEAN_BUILD,
		// new NullProgressMonitor());
		// } catch (final CoreException e) {
		// e.printStackTrace();
		// }
	}

}
