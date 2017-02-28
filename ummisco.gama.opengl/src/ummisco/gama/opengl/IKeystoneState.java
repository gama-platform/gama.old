package ummisco.gama.opengl;

import msi.gama.metamodel.shape.GamaPoint;

public interface IKeystoneState {

	int cornerSelected(GamaPoint mouse);

	int cornerHovered(GamaPoint mouse);

	void setCornerSelected(int c);

	void setCornerHovered(int c);

	GamaPoint[] getCoords();

	public void setUpCoords();

	void setKeystoneCoordinates(int cornerId, GamaPoint p);

	GamaPoint getKeystoneCoordinates(int cornerId);

	boolean drawKeystoneHelper();

	/**
	 * Returns whether keystoning should be enforced
	 * 
	 * @return
	 */
	boolean isKeystoneInAction();

	int getCornerSelected();

	void startDrawHelper();

	void stopDrawHelper();

	void resetCorner(int corner);

}