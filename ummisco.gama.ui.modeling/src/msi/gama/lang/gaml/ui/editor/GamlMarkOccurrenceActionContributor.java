/*********************************************************************************************
 *
 * 'GamlMarkOccurrenceActionContributor.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA
 * modeling and simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.editor;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.occurrences.MarkOccurrenceActionContributor;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreInitializer;

import com.google.inject.Singleton;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.preferences.IPreferenceChangeListener;
import msi.gama.common.preferences.Pref;
import msi.gaml.types.IType;

/**
 * The class GamlMarkOccurrenceActionContributor.
 *
 * @author drogoul
 * @since 12 sept. 2013
 *
 */
@Singleton
public class GamlMarkOccurrenceActionContributor extends MarkOccurrenceActionContributor
		implements IPreferenceStoreInitializer {

	IPreferenceStoreAccess access;

	@Override
	public void contributeActions(final XtextEditor editor) {
		super.contributeActions(editor);
		final IToolBarManager toolBarManager = editor.getEditorSite().getActionBars().getToolBarManager();
		final IContributionItem item = toolBarManager.find(getAction().getId());
		if (item != null) {
			toolBarManager.remove(item);
		}

	}

	// Preference here is an instance variable, but only one will be created as this class is a singleton.
	public final Pref<Boolean> EDITOR_MARK_OCCURRENCES =
			GamaPreferences.create("pref_editor_mark_occurrences", "Mark occurrences of symbols", true, IType.BOOL)
					.in(GamaPreferences.Modeling.NAME, GamaPreferences.Modeling.TEXT)
					.addChangeListener(new IPreferenceChangeListener<Boolean>() {

						@Override
						public boolean beforeValueChange(final Boolean newValue) {
							return true;
						}

						@Override
						public void afterValueChange(final Boolean newValue) {
							stateChanged(newValue);
						}
					});

	@Override
	public void initialize(final IPreferenceStoreAccess preferenceStoreAccess) {
		access = preferenceStoreAccess;
		preferenceStoreAccess.getWritablePreferenceStore().setDefault(getPreferenceKey(),
				EDITOR_MARK_OCCURRENCES.getValue());
		preferenceStoreAccess.getWritablePreferenceStore().setValue(getPreferenceKey(),
				EDITOR_MARK_OCCURRENCES.getValue());
	}
}
