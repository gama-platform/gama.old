package msi.gama.gui.viewers.gis;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.geotools.swt.MapLayerComposite;

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

}
