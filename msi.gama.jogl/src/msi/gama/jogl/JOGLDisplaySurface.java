package msi.gama.jogl;

import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import msi.gama.common.interfaces.IDisplay;
import msi.gama.common.interfaces.IDisplayManager;
import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.metamodel.shape.IShape;
import msi.gama.outputs.IDisplayOutput;


public final class JOGLDisplaySurface extends JPanel implements IDisplaySurface {

	@Override
	public BufferedImage getImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateDisplay() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int[] computeBoundsFrom(int width, int height) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean resizeImage(int width, int height) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void outputChanged(double env_width, double env_height,
			IDisplayOutput output) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void zoomIn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void zoomOut() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void zoomFit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IDisplayManager getManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void fireSelectionChanged(Object a) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusOn(IShape geometry, IDisplay display) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean canBeUpdated() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void canBeUpdated(boolean ok) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBackgroundColor(Color background) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPaused(boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isPaused() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setQualityRendering(boolean quality) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSynchronized(boolean checked) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setAutoSave(boolean autosave) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSnapshotFileName(String string) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void snapshot() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNavigator(Object swtNavigationPanel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getImageWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getImageHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setOrigin(int i, int j) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getOriginX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getOriginY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void initialize(double w, double h, IDisplayOutput layerDisplayOutput) {
		// TODO Auto-generated method stub
		
	}
	
}