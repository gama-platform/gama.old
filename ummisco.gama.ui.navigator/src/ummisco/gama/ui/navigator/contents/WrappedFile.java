/*******************************************************************************************************
 *
 * WrappedFile.java, in ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.navigator.contents;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import msi.gama.runtime.GAMA;
import msi.gama.util.file.GamaShapeFile.ShapeInfo;
import msi.gama.util.file.IGamaFileMetaData;
import msi.gaml.compilation.kernel.GamaBundleLoader;
import msi.gaml.types.Types;
import ummisco.gama.ui.metadata.FileMetaDataProvider;
import ummisco.gama.ui.navigator.NavigatorContentProvider;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaIcon;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.utils.PreferencesHelper;

/**
 * The Class WrappedFile.
 */
public class WrappedFile extends WrappedResource<WrappedResource<?, ?>, IFile> {

	/** The file parent. */
	WrappedFile fileParent;

	/** The is shape file. */
	boolean isShapeFile;

	/** The is shape file support. */
	boolean isShapeFileSupport;

	/** The image. */
	ImageDescriptor image;

	/** The color. */
	Color color;

	/**
	 * Instantiates a new wrapped file.
	 *
	 * @param root
	 *            the root
	 * @param wrapped
	 *            the wrapped
	 */
	public WrappedFile(final WrappedContainer<?> root, final IFile wrapped) {
		super(root, wrapped);
		computeFileType();
		computeFileParent();
	}

	/**
	 * Compute file image.
	 */
	protected void computeFileImage() {
		final IFile f = getResource();
		if (GamaBundleLoader.HANDLED_FILE_EXTENSIONS.contains(f.getFileExtension())) {
			if (isShapeFileSupport) {
				image = GamaIcon.named(IGamaIcons.FILE_SHAPESUPPORT).descriptor();
			} else {
				image =  ImageDescriptor.createFromImage(DEFAULT_LABEL_PROVIDER.getImage(f));
			}
		} else {
			image = GamaIcon.named(IGamaIcons.FILE_TEXT).descriptor();
		}

	}

	/**
	 * Compute file type.
	 */
	protected void computeFileType() {
		final IFile f = getResource();
		isShapeFile = FileMetaDataProvider.SHAPEFILE_CT_ID.equals(FileMetaDataProvider.getContentTypeId(f));
		isShapeFileSupport =
				FileMetaDataProvider.SHAPEFILE_SUPPORT_CT_ID.equals(FileMetaDataProvider.getContentTypeId(f));
	}

	/**
	 * Compute file parent.
	 */
	private void computeFileParent() {
		if (isShapeFileSupport) {
			final IResource shape = FileMetaDataProvider.shapeFileSupportedBy(getResource());
			if (shape != null) { fileParent = (WrappedFile) getManager().findWrappedInstanceOf(shape); }
		}
	}

	@Override
	public WrappedResource<?, ?> getParent() {
		if (fileParent != null) return fileParent;
		return super.getParent();
	}

	@Override
	public boolean canBeDecorated() {
		return false;
	}

	@Override
	public boolean hasChildren() {
		return isShapeFile;
	}

	@Override
	public Object[] getNavigatorChildren() {
		if (NavigatorContentProvider.FILE_CHILDREN_ENABLED && (isGamaFile() || isShapeFile)) return getFileChildren();
		return EMPTY;
	}

	/**
	 * Gets the file children.
	 *
	 * @return the file children
	 */
	public Object[] getFileChildren() {
		final IFile p = getResource();
		try {
			final IContainer folder = p.getParent();
			final List<VirtualContent> sub = new ArrayList<>();
			for (final IResource r : folder.members()) {
				if (r instanceof IFile && FileMetaDataProvider.isSupport(p, (IFile) r)) {
					sub.add(getManager().findWrappedInstanceOf(r));
				}
			}
			final IGamaFileMetaData metaData = GAMA.getGui().getMetaDataProvider().getMetaData(p, false, false);
			Map<String, String> attributes;
			if (metaData instanceof ShapeInfo info && !(attributes = info.getAttributes()).isEmpty()) {
				Map<String, String> tags = new LinkedHashMap<>(attributes);
				attributes.forEach((k, v) -> {
					if (Types.AGENT.getSpecies().hasAttribute(k)) {
						tags.put(k, tags.get(k) + " <- built-in attribute of agents");
						color = GamaColors.system(SWT.COLOR_DARK_RED);
					}
				});
				final Tags wf = new Tags(this, tags, "Attributes", false);
				if (wf.getNavigatorChildren().length > 0) { sub.add(wf); }
			}
			return sub.toArray();
		} catch (final CoreException e) {
			e.printStackTrace();
		}
		return VirtualContent.EMPTY;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		if (image == null) { computeFileImage(); }
		return image;
	}

	@Override
	public Color getColor() { return color; }

	@Override
	public void getSuffix(final StringBuilder sb) {
		if (PreferencesHelper.NAVIGATOR_METADATA.getValue()) {
			final IGamaFileMetaData data = GAMA.getGui().getMetaDataProvider().getMetaData(getResource(), false, true);
			if (data != null) { data.appendSuffix(sb); }
		}
	}

	@Override
	public int countModels() {
		return 0;
	}

	/**
	 * Checks if is gama file.
	 *
	 * @return true, if is gama file
	 */
	public boolean isGamaFile() { return false; }

	@Override
	public VirtualContentType getType() { return VirtualContentType.FILE; }

}
