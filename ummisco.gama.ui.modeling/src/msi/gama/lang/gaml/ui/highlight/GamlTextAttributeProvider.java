/*******************************************************************************************************
 *
 * GamlTextAttributeProvider.java, in ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.highlight;

import static org.eclipse.xtext.ui.editor.utils.EditorUtils.colorFromRGB;
import static org.eclipse.xtext.ui.editor.utils.EditorUtils.fontFromFontData;

/**
 * The class GamlTextAttributeProvider.
 *
 * @author drogoul
 * @since 30 nov. 2020
 *
 */

import java.util.Arrays;
import java.util.HashMap;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.xtext.Constants;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfigurationAcceptor;
import org.eclipse.xtext.ui.editor.syntaxcoloring.ITextAttributeProvider;
import org.eclipse.xtext.ui.editor.syntaxcoloring.PreferenceStoreAccessor;
import org.eclipse.xtext.ui.editor.utils.TextStyle;
import org.eclipse.xtext.util.Strings;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import msi.gama.application.workbench.ThemeHelper;
import msi.gama.util.GamaFont;

/**
 * @author Sebastian Zarnekow - Initial contribution and API
 */
@Singleton
public class GamlTextAttributeProvider implements ITextAttributeProvider, IHighlightingConfigurationAcceptor {

	/** The attributes. */
	// private final PreferenceStoreAccessor preferencesAccessor;
	private final HashMap<String, TextAttribute> attributes;

	/** The highlighting config. */
	private final GamlHighlightingConfiguration highlightingConfig;

	/** The language name. */
	private @Inject @Named (Constants.LANGUAGE_NAME) String languageName;

	// public static GamaFont getDefaultFont() {
	// final var fd = PreferenceConverter.getFontData(EditorsPlugin.getDefault().getPreferenceStore(),
	// JFaceResources.TEXT_FONT);
	// return new GamaFont(fd.getName(), fd.getStyle(), fd.getHeight());
	// }

	/**
	 * Gets the font.
	 *
	 * @param ts
	 *            the ts
	 * @return the font
	 */
	public static GamaFont getFont(final TextStyle ts) {
		final var fds = ts.getFontData();
		// if (fds == null)
		// return getDefaultFont();
		if (fds == null) return null;
		final var fd = fds[0];
		return new GamaFont(fd.getName(), fd.getStyle(), fd.getHeight());
	}

	/**
	 * Instantiates a new gaml text attribute provider.
	 *
	 * @param c
	 *            the c
	 * @param preferenceStoreAccess
	 *            the preference store access
	 * @param prefStoreAccessor
	 *            the pref store accessor
	 */
	@Inject
	public GamlTextAttributeProvider(final IHighlightingConfiguration c,
			final IPreferenceStoreAccess preferenceStoreAccess, final PreferenceStoreAccessor prefStoreAccessor) {
		highlightingConfig = (GamlHighlightingConfiguration) c;
		this.attributes = new HashMap<>();
		// we first create the preferences
		// configureHighlightingPreferences();
		// preferenceStoreAccess.getPreferenceStore().addPropertyChangeListener(this);
		initialize();
		ThemeHelper.addListener(isLight -> {
			highlightingConfig.changeTo(isLight);
			initialize();
		});
	}

	/**
	 * Initialize.
	 */
	private void initialize() {

		// WorkbenchHelper.asyncRun(() -> {
		attributes.clear();
		highlightingConfig.configure(GamlTextAttributeProvider.this);
		// });

		// if (Display.getCurrent() == null) {
		//
		// WorkbenchHelper.asyncRun(() -> {
		// attributes.clear();
		// highlightingConfig.configure(GamlTextAttributeProvider.this);
		// });
		//
		// } else {
		// attributes.clear();
		// highlightingConfig.configure(this);
		// }
	}

	@Override
	public TextAttribute getAttribute(final String id) {
		return attributes.get(id);
	}

	@Override
	public TextAttribute getMergedAttributes(final String[] ids) {
		if (ids.length < 2) throw new IllegalStateException();
		final var mergedIds = getMergedIds(ids);
		var result = getAttribute(mergedIds);
		if (result == null) {
			for (final String id : ids) { result = merge(result, getAttribute(id)); }
			if (result != null) {
				attributes.put(mergedIds, result);
			} else {
				attributes.remove(mergedIds);
			}
		}
		return result;
	}

	/**
	 * Merge.
	 *
	 * @param first
	 *            the first
	 * @param second
	 *            the second
	 * @return the text attribute
	 */
	private TextAttribute merge(final TextAttribute first, final TextAttribute second) {
		if (first == null) return second;
		if (second == null) return first;
		final var style = first.getStyle() | second.getStyle();
		var fgColor = second.getForeground();
		if (fgColor == null) { fgColor = first.getForeground(); }
		var bgColor = second.getBackground();
		if (bgColor == null) { bgColor = first.getBackground(); }
		var font = second.getFont();
		if (font == null) { font = first.getFont(); }
		return new TextAttribute(fgColor, bgColor, style, font);
	}

	/**
	 * Gets the merged ids.
	 *
	 * @param ids
	 *            the ids
	 * @return the merged ids
	 */
	public String getMergedIds(final String[] ids) {
		return "$$$Merged:" + Strings.concat("/", Arrays.asList(ids)) + "$$$";
	}

	@Override
	public void acceptDefaultHighlighting(final String id, final String name, final TextStyle style) {
		this.attributes.put(id, createTextAttribute(id, style));
	}

	/**
	 * Creates the text attribute.
	 *
	 * @param id
	 *            the id
	 * @param textStyle
	 *            the text style
	 * @return the text attribute
	 */
	protected TextAttribute createTextAttribute(final String id, final TextStyle textStyle) {
		return new TextAttribute(colorFromRGB(textStyle.getColor()), colorFromRGB(textStyle.getBackgroundColor()),
				textStyle.getStyle(), fontFromFontData(textStyle.getFontData()));
	}

	// public void configureHighlightingPreferences() {
	// final List<String> ids = new ArrayList<>();
	// // First we create and/or read the preferences
	// highlightingConfig.configure((id, name, style) -> {
	// final var pref = GamaPreferences
	// .create("pref_" + id + "_font", name + " font", () -> getFont(style), IType.FONT, false)
	// .in(Modeling.NAME, "Syntax coloring").onChange(font -> {
	// System.out.println("Pref " + "pref_" + id + "_font changed");
	// applyFont(id, name, style, font);
	// });
	// applyFont(id, name, style, pref.getValue());
	//
	// final var pref2 =
	// GamaPreferences
	// .create("pref_" + id + "_color", "... and color",
	// () -> GamaColors.toGamaColor(style.getColor()), IType.COLOR, false)
	// .in(Modeling.NAME, "Syntax coloring").onChange(color -> {
	// System.out.println("Pref " + "pref_" + id + "_color changed to " + color);
	// applyColor(id, name, style, color);
	// });
	// applyColor(id, name, style, pref2.getValue());
	// ids.add(pref.getKey());
	// ids.add(pref2.getKey());
	// });
	// // ThemeHelper.CORE_THEME_LIGHT.refreshes(ids.toArray(new String[0]));
	// }

}
