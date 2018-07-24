package ummisco.gama.ui.navigator.contents;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.xml.type.internal.DataValue.URI;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import msi.gama.runtime.GAMA;
import msi.gama.util.file.IGamaFileMetaData;
import msi.gaml.statements.test.AbstractSummary;
import msi.gaml.statements.test.CompoundSummary;
import ummisco.gama.ui.resources.GamaFonts;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.resources.IGamaIcons;

public class WrappedProject extends WrappedContainer<IProject> implements IAdaptable {

	private String plugin;
	final boolean isTest;

	public WrappedProject(final TopLevelFolder parent, final IProject wrapped) {
		super(parent, wrapped);
		isTest = parent instanceof TestModelsFolder;
	}

	@Override
	public boolean canBeDecorated() {
		return true;
	}

	@Override
	public boolean isOpen() {
		return super.isOpen() && getResource().isOpen();
	}

	@Override
	public boolean handleDoubleClick() {
		if (!isOpen()) {
			try {
				getResource().open(null);
			} catch (final CoreException e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	@Override
	public Object[] getNavigatorChildren() {
		return isOpen() ? super.getNavigatorChildren() : EMPTY;
	}

	@Override
	public Image getImage() {
		return GamaIcons.create(IGamaIcons.FOLDER_PROJECT).image();
	}

	@Override
	public Color getColor() {
		return IGamaColors.GRAY_LABEL.color();
	}

	@Override
	public Font getFont() {
		return GamaFonts.getNavigHeaderFont();
	}

	@Override
	public void getSuffix(final StringBuilder sb) {
		if (!isOpen()) {
			sb.append("closed");
			return;
		}

		if (getPlugin() != null && !getPlugin().isEmpty())
			sb.append(getPlugin()).append(", ");
		if (isTestProject()) {
			getTestSuffix(sb);
		} else
			super.getSuffix(sb);
	}

	private void getTestSuffix(final StringBuilder sb) {
		final org.eclipse.emf.common.util.URI emfURI =
				org.eclipse.emf.common.util.URI.createPlatformResourceURI(URI.encode(getName()), false);
		final String result = getSuffixOfTestSummary(emfURI);
		if (result.isEmpty())
			super.getSuffix(sb);
		else {
			sb.append(result);
		}
	}

	public String getSuffixOfTestSummary(final org.eclipse.emf.common.util.URI uri) {
		final CompoundSummary<?, ?> summary = getManager().getTestsSummary();
		if (summary == null)
			return "";
		final List<AbstractSummary<?>> list = new ArrayList<>();
		summary.getSubSummariesBelongingTo(uri, list);
		final CompoundSummary<?, ?> result = new CompoundSummary<>(list);
		return result.getStringSummary();
	}

	private boolean isTestProject() {
		return isTest;
	}

	@Override
	public VirtualContentType getType() {
		return VirtualContentType.PROJECT;
	}

	String getPlugin() {
		if (plugin == null) {
			final IGamaFileMetaData data = GAMA.getGui().getMetaDataProvider().getMetaData(getResource(), false, false);
			if (data != null) {
				setPlugin(data.getSuffix());
			} else
				setPlugin("");
		}
		return plugin;
	}

	void setPlugin(final String plugin) {
		this.plugin = plugin;
	}

	@Override
	public WrappedProject getProject() {
		return this;
	}

}
