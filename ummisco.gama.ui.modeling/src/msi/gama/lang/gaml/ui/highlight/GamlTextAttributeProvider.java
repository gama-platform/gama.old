/**
 * Created by drogoul, 30 nov. 2020
 * 
 */
package msi.gama.lang.gaml.ui.highlight;

/**
 * The class GamlTextAttributeProvider.
 *
 * @author drogoul
 * @since 30 nov. 2020
 *
 */

import java.util.Arrays;
import java.util.HashMap;

import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.xtext.Constants;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfigurationAcceptor;
import org.eclipse.xtext.ui.editor.syntaxcoloring.ITextAttributeProvider;
import org.eclipse.xtext.ui.editor.syntaxcoloring.PreferenceStoreAccessor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.eclipse.xtext.ui.editor.utils.TextStyle;
import org.eclipse.xtext.util.Strings;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.preferences.GamaPreferences.Modeling;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaFont;
import msi.gaml.types.IType;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * @author Sebastian Zarnekow - Initial contribution and API
 */
@Singleton
public class GamlTextAttributeProvider
		implements ITextAttributeProvider, IHighlightingConfigurationAcceptor, IPropertyChangeListener {

	private final PreferenceStoreAccessor preferencesAccessor;
	private final HashMap<String, TextAttribute> attributes;
	private final IHighlightingConfiguration highlightingConfig;
	private @Inject @Named (Constants.LANGUAGE_NAME) String languageName;

	@Inject
	public GamlTextAttributeProvider(IHighlightingConfiguration highlightingConfig,
			IPreferenceStoreAccess preferenceStoreAccess, PreferenceStoreAccessor prefStoreAccessor) {
		this.highlightingConfig = highlightingConfig;
		this.preferencesAccessor = prefStoreAccessor;
		this.attributes = new HashMap<>();
		preferenceStoreAccess.getPreferenceStore().addPropertyChangeListener(this);
		initialize();
		configureHighlightingPreferences();
	}

	public static GamaFont getDefaultFont() {
		final var fd = PreferenceConverter.getFontData(EditorsPlugin.getDefault().getPreferenceStore(),
				JFaceResources.TEXT_FONT);
		return new GamaFont(fd.getName(), fd.getStyle(), fd.getHeight());
	}

	public static GamaFont getFont(final TextStyle ts) {
		final var fds = ts.getFontData();
		if (fds == null)
			return getDefaultFont();
		final var fd = fds[0];
		return new GamaFont(fd.getName(), fd.getStyle(), fd.getHeight());
	}

	private static boolean highlightInitialized;

	private void configureHighlightingPreferences() {
		if (highlightInitialized)
			return;
		highlightInitialized = true;
		// First we create and/or read the preferences
		highlightingConfig.configure((id, name, style) -> {
			final var pref = GamaPreferences
					.create("pref_" + id + "_font", name + " font", (() -> getFont(style)), IType.FONT, false)
					.in(Modeling.NAME, "Syntax coloring").onChange(font -> {
						applyFont(id, name, style, font);

					});
			applyFont(id, name, style, pref.getValue());
			final var pref2 =
					GamaPreferences
							.create("pref_" + id + "_color", "... and color",
									() -> GamaColors.toGamaColor(style.getColor()), IType.COLOR, false)
							.in(Modeling.NAME, "Syntax coloring").onChange(color -> {
								applyColor(id, name, style, color);
							});
			applyColor(id, name, style, pref2.getValue());
		});
	}

	private void applyFont(String id, String name, TextStyle style, GamaFont font) {
		// final var newStyle = oldStyle.copy();
		style.setFontData(new FontData(font.getFontName(), font.getSize(), font.getStyle()));
		GamlTextAttributeProvider.this.acceptDefaultHighlighting(id, name, style);
	}

	private void applyColor(String id, String name, TextStyle style, GamaColor color) {
		// final var newStyle = oldStyle.copy();
		style.setColor(new RGB(color.red(), color.green(), color.blue()));
		GamlTextAttributeProvider.this.acceptDefaultHighlighting(id, name, style);
	}

	private void initialize() {
		if (Display.getCurrent() == null) {

			WorkbenchHelper.asyncRun(() -> {
				attributes.clear();
				highlightingConfig.configure(GamlTextAttributeProvider.this);
			});

		} else {
			attributes.clear();
			highlightingConfig.configure(this);
		}
	}

	@Override
	public TextAttribute getAttribute(String id) {
		return attributes.get(id);
	}

	@Override
	public TextAttribute getMergedAttributes(String[] ids) {
		if (ids.length < 2)
			throw new IllegalStateException();
		final var mergedIds = getMergedIds(ids);
		var result = getAttribute(mergedIds);
		if (result == null) {
			for (final String id : ids) {
				result = merge(result, getAttribute(id));
			}
			if (result != null)
				attributes.put(mergedIds, result);
			else
				attributes.remove(mergedIds);
		}
		return result;
	}

	private TextAttribute merge(TextAttribute first, TextAttribute second) {
		if (first == null)
			return second;
		if (second == null)
			return first;
		final var style = first.getStyle() | second.getStyle();
		var fgColor = second.getForeground();
		if (fgColor == null)
			fgColor = first.getForeground();
		var bgColor = second.getBackground();
		if (bgColor == null)
			bgColor = first.getBackground();
		var font = second.getFont();
		if (font == null)
			font = first.getFont();
		return new TextAttribute(fgColor, bgColor, style, font);
	}

	public String getMergedIds(String[] ids) {
		return "$$$Merged:" + Strings.concat("/", Arrays.asList(ids)) + "$$$";
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().startsWith(PreferenceStoreAccessor.tokenTypeTag(languageName))) {
			initialize();
		}
	}

	@Override
	public void acceptDefaultHighlighting(String id, String name, TextStyle style) {
		this.attributes.put(id, createTextAttribute(id, style));
		// if (this.attributes.put(id, createTextAttribute(id, style)) != null)
		// throw new IllegalStateException("Id '" + id + "' has been used twice.");
	}

	protected TextAttribute createTextAttribute(String id, TextStyle defaultTextStyle) {
		final var textStyle = new TextStyle();
		preferencesAccessor.populateTextStyle(id, textStyle, defaultTextStyle);
		final var style = textStyle.getStyle();
		final var fontFromFontData = EditorUtils.fontFromFontData(textStyle.getFontData());
		return new TextAttribute(EditorUtils.colorFromRGB(textStyle.getColor()),
				EditorUtils.colorFromRGB(textStyle.getBackgroundColor()), style, fontFromFontData);
	}

}
