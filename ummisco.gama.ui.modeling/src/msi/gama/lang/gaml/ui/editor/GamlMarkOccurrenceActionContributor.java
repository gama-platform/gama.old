/*******************************************************************************************************
 *
 * GamlMarkOccurrenceActionContributor.java, in ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.editor;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.occurrences.MarkOccurrenceActionContributor;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreInitializer;

import com.google.inject.Singleton;

import msi.gama.common.preferences.GamaPreferences;

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

	/** The access. */
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

	@Override
	public void initialize(final IPreferenceStoreAccess preferenceStoreAccess) {
		GamaPreferences.Modeling.EDITOR_MARK_OCCURRENCES.onChange(newValue -> stateChanged(newValue));
		access = preferenceStoreAccess;
		preferenceStoreAccess.getWritablePreferenceStore().setDefault(getPreferenceKey(),
				GamaPreferences.Modeling.EDITOR_MARK_OCCURRENCES.getValue());
		preferenceStoreAccess.getWritablePreferenceStore().setValue(getPreferenceKey(),
				GamaPreferences.Modeling.EDITOR_MARK_OCCURRENCES.getValue());
	}
}
