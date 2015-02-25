package msi.gama.gui.navigator.shapefiles;

import gnu.trove.map.hash.THashMap;
import msi.gama.gui.navigator.NavigatorBaseLighweightDecorator;
import msi.gama.gui.swt.SwtGui;
import org.eclipse.core.resources.*;
import org.eclipse.jface.viewers.IDecoration;

public class LightweightShapefileSupportDecorator extends NavigatorBaseLighweightDecorator {

	public static final THashMap<String, String> longNames = new THashMap() {

		{
			put("prj", "Projection data");
			put("shx", "Index data");
			put("dbf", "Attribute data");
			put("xml", "Metadata");
			put("sbn", "Query data");
			put("sbx", "Query data");
			put("qix", "Query data");
			put("qpj", "QGis project");
			put("fix", "Feature index");
		}
	};

	@Override
	public void decorate(final Object element, final IDecoration decoration) {
		IFile file = SwtGui.adaptTo(element, IFile.class, IFile.class);
		if ( file == null ) { return; }
		IResource r = shapeFileSupportedBy(file);
		if ( r == null ) { return; }
		String ext = file.getFileExtension();
		String type = longNames.containsKey(ext) ? longNames.get(ext) : "Data";
		decoration.addSuffix(" (" + type + " for '" + r.getName() + "')");
	}

	public static boolean isSupport(final IFile shapefile, final IFile other) {
		IResource r = shapeFileSupportedBy(other);
		return shapefile.equals(r);
	}

	public static IResource shapeFileSupportedBy(final IFile r) {
		String fileName = r.getName();
		// Special case for these odd files
		if ( fileName.endsWith(".shp.xml") ) {
			fileName = fileName.replace(".xml", "");
		} else {
			String extension = r.getFileExtension();
			if ( !longNames.contains(extension) ) { return null; }
			fileName = fileName.replace(extension, "shp");
		}
		return r.getParent().findMember(fileName);
	}

}
