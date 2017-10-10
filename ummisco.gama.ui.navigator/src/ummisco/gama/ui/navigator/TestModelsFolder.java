/*********************************************************************************************
 *
 * 'PluginsModelsFolder.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.navigator;

import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;

import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.utils.PlatformHelper;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class TestModelsFolder extends TopLevelFolder {

	public TestModelsFolder(final Object root, final String name) {
		super(root, name);
	}

	@Override
	public Image getImage() {
		if (PlatformHelper.isGtk())
			return GamaIcons.create(IGamaIcons.FOLDER_TEST_16).image();
		return GamaIcons.create(IGamaIcons.FOLDER_TEST).image(); // FOLDER_PLUGIN
	}

	@Override
	public Image getImageForStatus() {
		return GamaIcons.create("navigator/folder.status.test").image();
	}

	@Override
	public String getMessageForStatus() {
		return "Click here to run all the tests";
	}

	@Override
	public GamaUIColor getColorForStatus() {
		return IGamaColors.NEUTRAL;
	}

	/**
	 * Method accepts()
	 * 
	 * @see ummisco.gama.ui.navigator.TopLevelFolder#accepts(org.eclipse.core.resources.IProjectDescription)
	 */
	@Override
	protected boolean accepts(final IProjectDescription desc) {
		return desc.hasNature(WorkbenchHelper.TEST_NATURE);
	}

	/**
	 * Method getModelsLocation()
	 * 
	 * @see ummisco.gama.ui.navigator.TopLevelFolder#getModelsLocation()
	 */
	@Override
	protected Location getModelsLocation() {
		return Location.Tests;
	}

	@Override
	public SelectionListener getSelectionListenerForStatus() {
		return new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				TestsRunner.start();
			}

		};
	}

}
