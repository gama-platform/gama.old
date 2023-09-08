/*******************************************************************************************************
 *
 * GamlAnnotationImageProvider.java, in ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.decorators;

import static msi.gama.application.workbench.ThemeHelper.isDark;
import static org.eclipse.xtext.ui.editor.XtextEditor.ERROR_ANNOTATION_TYPE;
import static org.eclipse.xtext.ui.editor.XtextEditor.INFO_ANNOTATION_TYPE;
import static org.eclipse.xtext.ui.editor.XtextEditor.WARNING_ANNOTATION_TYPE;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.xtext.ui.editor.model.XtextMarkerAnnotationImageProvider;
import org.eclipse.xtext.ui.editor.validation.XtextAnnotation;

import com.google.inject.Inject;

import ummisco.gama.ui.resources.GamaIcon;
import ummisco.gama.ui.resources.IGamaIcons;

/**
 * The Class GamlAnnotationImageProvider.
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamlAnnotationImageProvider extends XtextMarkerAnnotationImageProvider {

	/** The Constant DELETED. */
	private static final GamaIcon DELETED = GamaIcon.named(IGamaIcons.MARKER_DELETED);

	/** The Constant ERROR. */
	private static final GamaIcon ERROR = GamaIcon.named(isDark() ? IGamaIcons.MARKER_ERROR_DARK : IGamaIcons.MARKER_ERROR);

	/** The Constant WARNING. */
	private static final GamaIcon WARNING = GamaIcon.named(IGamaIcons.MARKER_WARNING);

	/** The Constant INFO. */
	private static final GamaIcon INFO = GamaIcon.named(isDark() ? IGamaIcons.MARKER_INFO_DARK : IGamaIcons.MARKER_INFO);

	/** The Constant TASK. */
	private static final GamaIcon TASK = GamaIcon.named(IGamaIcons.MARKER_TASK);

	/** The Constant fixables. */
	private static final Map<String, GamaIcon> fixables = new HashMap() {

		{
			put(ERROR_ANNOTATION_TYPE, ERROR);
			put(WARNING_ANNOTATION_TYPE, WARNING);
			put(INFO_ANNOTATION_TYPE, INFO);
			put("org.eclipse.ui.workbench.texteditor.task", TASK);
		}
	};

	/** The Constant nonFixables. */
	private static final Map<String, GamaIcon> nonFixables = new HashMap() {

		{
			put(ERROR_ANNOTATION_TYPE, ERROR);
			put(WARNING_ANNOTATION_TYPE, WARNING);
			put(INFO_ANNOTATION_TYPE, INFO);
			put("org.eclipse.ui.workbench.texteditor.task", TASK);
		}
	};

	/** The Constant deleted. */
	private static final Map<String, GamaIcon> deleted = new HashMap() {

		{
			put(ERROR_ANNOTATION_TYPE, DELETED);
			put(WARNING_ANNOTATION_TYPE, DELETED);
			put(INFO_ANNOTATION_TYPE, DELETED);
			put("org.eclipse.ui.workbench.texteditor.task", DELETED);
		}
	};

	/**
	 * Instantiates a new gaml annotation image provider.
	 */
	@Inject
	public GamlAnnotationImageProvider() {}

	@Override
	public Image getManagedImage(final Annotation annotation) {

		// final AnnotationPreference pref;
		GamaIcon result = null;
		if (annotation.isMarkedDeleted()) {
			result = deleted.get(annotation.getType());
		} else if (annotation instanceof MarkerAnnotation ma) {
			result = getImage(annotation.getType());
			// if (ma.isQuickFixableStateSet() && ma.isQuickFixable()) {
			// result = fixables.get(annotation.getType());
			// } else {
			// result = nonFixables.get(annotation.getType());
			// }
		} else if (annotation instanceof ProjectionAnnotation)
			return null;
		// ProjectionAnnotation projection = (ProjectionAnnotation)
		// annotation;
		// if ( projection.isCollapsed() ) {
		// return GamaIcons.create ("marker.collapsed2").image();
		// } else {
		// return GamaIcons.create ("marker.expanded2").image();
		// }
		else if (annotation instanceof XtextAnnotation ma) {
			result = getImage(annotation.getType());
			// if (ma.isQuickFixable()) {
			// result = fixables.get(annotation.getType());
			// } else {
			// result = nonFixables.get(annotation.getType());
			// }
		}
		if (result != null) return result.image();
		// DEBUG.LOG("Image not found for type: " +
		// annotation.getType());
		return super.getManagedImage(annotation);
	}

	/**
	 * Gets the image.
	 *
	 * @param type
	 *            the type
	 * @return the image
	 */
	public GamaIcon getImage(final String type) {
		return switch (type) {
			case ERROR_ANNOTATION_TYPE -> ERROR;
			case WARNING_ANNOTATION_TYPE -> WARNING;
			case INFO_ANNOTATION_TYPE -> INFO;
			case "org.eclipse.ui.workbench.texteditor.task" -> TASK;
			default -> null;
		};
	}

}
