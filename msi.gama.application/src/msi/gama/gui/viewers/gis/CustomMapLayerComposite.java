package msi.gama.gui.viewers.gis;

import java.lang.reflect.Field;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.geotools.swt.MapLayerComposite;
import org.geotools.swt.control.MaplayerTableViewer;

public class CustomMapLayerComposite extends MapLayerComposite {

	public CustomMapLayerComposite(Composite parent, int style) {
		super(parent, style);
		Group group = (Group) this.getChildren()[0];
		for (Control c : group.getChildren()) {
			if (c instanceof Composite) {
				for (Control c2 : ((Composite)c).getChildren()) {
					if (c2 instanceof Button && ((Button) c2).getToolTipText().equals("Remove layer")) {
						c2.setVisible(false);
						break;
					}
				}
			}
		}
	}
	
	 MaplayerTableViewer getMapLayerTableViewer(){
		Field privateField;
		try {
			privateField = MapLayerComposite.class.getDeclaredField("mapLayerTableViewer");
			privateField.setAccessible(true);
			return (MaplayerTableViewer)privateField.get(this);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
		
	}

}
