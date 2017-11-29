package ummisco.gama.ui.navigator.contents;

import static msi.gama.common.GamlFileExtension.isExperiment;
import static msi.gama.common.GamlFileExtension.isGaml;
import static ummisco.gama.ui.metadata.FileMetaDataProvider.GAML_CT_ID;
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
import org.eclipse.emf.common.util.URI;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import msi.gama.common.GamlFileExtension;
import msi.gama.runtime.GAMA;
import msi.gama.util.GAML;
import msi.gama.util.file.GamlFileInfo;
import msi.gama.util.file.IGamaFileMetaData;
import msi.gaml.compilation.ast.ISyntacticElement;
import msi.gaml.compilation.kernel.GamaBundleLoader;
import ummisco.gama.ui.navigator.NavigatorContentProvider;
import ummisco.gama.ui.resources.GamaFonts;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.utils.PreferencesHelper;

public class WrappedFile extends WrappedResource<IFile> {

	WrappedResource<?> fileParent;
	boolean isGamaFile;
	boolean isExperiment;
	boolean isShapeFile;
	boolean isShapeFileSupport;
	Image image;

	public WrappedFile(final Object root, final IFile wrapped) {
		super(root, wrapped);
		computeFileType();
		computeFileParent();
		computeFileImage();
	}

	private void computeFileImage() {
		final IFile f = getResource();
		if (isExperiment) {
			image = GamaIcons.create("file.experiment2").image();
		} else if (GamaBundleLoader.HANDLED_FILE_EXTENSIONS.contains(f.getFileExtension())) {
			if (isShapeFileSupport) {
				image = GamaIcons.create("file.shapesupport2").image();
			} else {
				image = DEFAULT_LABEL_PROVIDER.getImage(f);
			}
		} else {
			image = GamaIcons.create("file.text2").image();
		}

	}

	private void computeFileType() {
		final IFile f = getResource();
		isExperiment = isExperiment(f.getName());
		isGamaFile = isExperiment || GAML_CT_ID.equals(getContentTypeId(f)) || isGaml(f.getName());
		isShapeFile = SHAPEFILE_CT_ID.equals(getContentTypeId(f));
		isShapeFileSupport = SHAPEFILE_SUPPORT_CT_ID.equals(getContentTypeId(f));
	}

	private void computeFileParent() {
		if (isShapeFileSupport) {
			final IResource shape = shapeFileSupportedBy(getResource());
			if (shape != null) {
				fileParent = getMapper().findWrappedInstanceOf(shape);
			}
		}

	}

	@Override
	public Object getParent() {
		if (fileParent != null)
			return fileParent;
		return super.getParent();
	}

	@Override
	public boolean canBeDecorated() {
		return isGamaFile;
	}

	@Override
	public boolean hasChildren() {
		return isGamaFile || isShapeFile;
	}

	@Override
	public Object[] getNavigatorChildren() {
		if (NavigatorContentProvider.FILE_CHILDREN_ENABLED) {
			if (isGamaFile)
				return getGamlFileChildren();
			if (isShapeFile)
				return getShapeFileChildren();
		}
		return EMPTY;
	}

	public Object[] getGamlFileChildren() {
		final IFile p = getResource();
		final IGamaFileMetaData metaData = GAMA.getGui().getMetaDataProvider().getMetaData(p, false, false);
		if (metaData instanceof GamlFileInfo) {
			final GamlFileInfo info = (GamlFileInfo) metaData;
			final List<VirtualContent> l = new ArrayList<>();
			final String path = p.getFullPath().toOSString();
			final ISyntacticElement element = GAML.getContents(URI.createPlatformResourceURI(path, true));
			if (element != null) {
				if (!GamlFileExtension.isExperiment(path)) {
					l.add(new WrappedModelContent(p, element));
				}
				element.visitExperiments(exp -> l.add(new WrappedExperimentContent(p, exp)));
			}
			if (!info.getImports().isEmpty()) {
				final Category wf = new Category(p, info.getImports(), "Imports");
				if (wf.getNavigatorChildren().length > 0)
					l.add(wf);
			}
			if (!info.getUses().isEmpty()) {
				final Category wf = new Category(p, info.getUses(), "Uses");
				if (wf.getNavigatorChildren().length > 0)
					l.add(wf);
			}
			return l.toArray();
		}
		return VirtualContent.EMPTY;
	}

	public Object[] getShapeFileChildren() {
		final IFile p = getResource();
		try {
			final IContainer folder = p.getParent();
			final List<IResource> sub = new ArrayList<>();
			for (final IResource r : folder.members()) {
				if (r instanceof IFile && isSupport(p, (IFile) r)) {
					sub.add(r);
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
		return GamaFonts.getNavigFileFont();
	}

	@Override
	public Image getImage() {
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
		}
	}

	@Override
	public int countModels() {
		return isGamaFile ? 1 : 0;
	}

	public boolean isGamaFile() {
		return isGamaFile;
	}

	@Override
	public VirtualContentType getType() {
		return VirtualContentType.FILE;
	}

}
