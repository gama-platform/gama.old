/*******************************************************************************************************
 *
 * ImageSaver.java, in ummisco.gaml.extensions.image, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gaml.extensions.image;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.imageio.ImageIO;

import msi.gama.common.interfaces.ISaveDelegate;
import msi.gama.runtime.IScope;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The Class ImageSaver.
 */
public class ImageSaver extends msi.gaml.statements.save.ImageSaver implements ISaveDelegate {

	@Override
	public IType getDataType() { return Types.get(GamaImageType.ID); }

	@Override
	public void save(final IScope scope, final IExpression item, final File file, final String code,
			final boolean addHeader, final String t, final Object attributesToSave) throws IOException {
		GamaImage image = GamaImageType.staticCast(scope, item.value(scope), false);
		if (image == null) return;
		if (image.getAlpha(scope) && !"png".equals(t)) {

		}
		String type = "image".equals(t) ? "png" : "jpeg".equals(t) ? "jpg" : t;
		ImageIO.write(image, type, file);

	}

	@Override
	public Set<String> computeFileTypes() {
		return Set.of(ImageIO.getWriterFileSuffixes());
	}

}
