package ummisco.gama.ui.navigator.contents;

import static ummisco.gama.ui.metadata.FileMetaDataProvider.SHAPEFILE_CT_ID;
import static ummisco.gama.ui.metadata.FileMetaDataProvider.SHAPEFILE_SUPPORT_CT_ID;
import static ummisco.gama.ui.metadata.FileMetaDataProvider.getContentTypeId;
import static ummisco.gama.ui.metadata.FileMetaDataProvider.isSupport;
import static ummisco.gama.ui.metadata.FileMetaDataProvider.shapeFileSupportedBy;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import msi.gama.runtime.GAMA;
import msi.gama.util.file.IGamaFileMetaData;
import msi.gaml.compilation.kernel.GamaBundleLoader;
import ummisco.gama.ui.navigator.NavigatorContentProvider;
import ummisco.gama.ui.resources.GamaFonts;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.utils.PreferencesHelper;

public class WrappedFile extends WrappedResource<WrappedResource<?, ?>, IFile> {

	WrappedFile fileParent;
	boolean isShapeFile;
	boolean isShapeFileSupport;
	Image image;
	final Font font;

	public WrappedFile(final WrappedContainer<?> root, final IFile wrapped) {
		super(root, wrapped);
		computeFileType();
		computeFileParent();
		font = wrapped.isLinked() ? GamaFonts.getNavigLinkFont() : GamaFonts.getNavigFileFont();
	}

	protected void computeFileImage() {
		final IFile f = getResource();
		if (GamaBundleLoader.HANDLED_FILE_EXTENSIONS.contains(f.getFileExtension())) {
			if (isShapeFileSupport) {
				image = GamaIcons.create("file.shapesupport2").image();
			} else {
				image = DEFAULT_LABEL_PROVIDER.getImage(f);
			}
		} else {
			image = GamaIcons.create("file.text2").image();
		}

	}

	protected void computeFileType() {
		final IFile f = getResource();
		isShapeFile = SHAPEFILE_CT_ID.equals(getContentTypeId(f));
		isShapeFileSupport = SHAPEFILE_SUPPORT_CT_ID.equals(getContentTypeId(f));
	}

	private void computeFileParent() {
		if (isShapeFileSupport) {
			final IResource shape = shapeFileSupportedBy(getResource());
			if (shape != null) {
				fileParent = (WrappedFile) getMapper().findWrappedInstanceOf(shape);
			}
		}
	}

	@Override
	public WrappedResource<?, ?> getParent() {
		if (fileParent != null) { return fileParent; }
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
		if (NavigatorContentProvider.FILE_CHILDREN_ENABLED) {
			if (isGamaFile() || isShapeFile) { return getFileChildren(); }
		}
		return EMPTY;
	}

	public Object[] getFileChildren() {
		final IFile p = getResource();
		try {
			final IContainer folder = p.getParent();
			final List<WrappedFile> sub = new ArrayList<>();
			for (final IResource r : folder.members()) {
				if (r instanceof IFile && isSupport(p, (IFile) r)) {
					sub.add((WrappedFile) getMapper().findWrappedInstanceOf(r));
				}
			}
			return sub.toArray();
		} catch (final CoreException e) {
			e.printStackTrace();
		}
		return VirtualContent.EMPTY;
	}

	@Override
	public Font getFont() {
		return font;
	}

	@Override
	public Image getImage() {
		if (image == null) {
			computeFileImage();
		}
		return image;
	}

	@Override
	public Color getColor() {
		return null;
	}

	@Override
	public void getSuffix(final StringBuilder sb) {
		if (PreferencesHelper.NAVIGATOR_METADATA.getValue()) {
			final IGamaFileMetaData data = GAMA.getGui().getMetaDataProvider().getMetaData(getResource(), false, true);
			if (data != null) {
				data.appendSuffix(sb);
			}
			if (getResource().isLinked()) {
				if (sb.length() > 0) {
					sb.append(" - ");
				}
				sb.append(getResource().getLocation());
			}
		}
	}

	@Override
	public int countModels() {
		return 0;
	}

	public boolean isGamaFile() {
		return false;
	}

	@Override
	public VirtualContentType getType() {
		return VirtualContentType.FILE;
	}

}
