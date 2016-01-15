/**
 * Created by drogoul, 28 avr. 2014
 *
 */
package msi.gama.gui.viewers.html;

import java.io.IOException;
import java.net.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.*;
import org.osgi.framework.Bundle;
import msi.gama.common.GamaPreferences;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.swt.*;
import msi.gama.gui.swt.controls.GamaToolbar2;
import msi.gama.gui.views.IToolbarDecoratedView;
import msi.gama.gui.views.actions.GamaToolbarFactory;

/**
 * Class BrowserEditor.
 *
 * @author drogoul
 * @since 28 avr. 2014
 *
 */
public class HtmlViewer extends EditorPart implements IToolbarDecoratedView {

	private static String HOME_URL = null;

	public static void openWelcomePage(final boolean ifEmpty) {
		if ( ifEmpty && SwtGui.getPage().getActiveEditor() != null ) { return; }
		if ( ifEmpty && !GamaPreferences.CORE_SHOW_PAGE.getValue() ) { return; }
		if ( HOME_URL == null ) {
			Bundle bundle = Platform.getBundle("msi.gama.ext");
			URL url = bundle.getEntry("/images/welcome.html");
			try {
				url = FileLocator.toFileURL(url);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			HOME_URL = url.toString();
		}
		if ( HOME_URL != null ) {
			GuiUtils.showWebEditor(HOME_URL, null);
		}
	}

	Browser browser;
	// private GamaToolbar leftToolbar;
	private GamaToolbar2 toolbar;
	ToolItem back, forward, home;

	public HtmlViewer() {}

	@Override
	public void doSave(final IProgressMonitor monitor) {}

	@Override
	public void doSaveAs() {}

	@Override
	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(final Composite parent) {
		Composite compo = GamaToolbarFactory.createToolbars(this, parent);
		browser = new Browser(compo, SWT.NONE);
		browser.addProgressListener(new ProgressListener() {

			@Override
			public void changed(final ProgressEvent arg0) {}

			@Override
			public void completed(final ProgressEvent event) {
				toolbar.status((Image) null, browser.getUrl(), IGamaColors.NEUTRAL, SWT.LEFT);
				checkButtons();
			}
		});
		parent.layout();
		if ( getEditorInput() instanceof FileEditorInput ) {
			FileEditorInput input = (FileEditorInput) getEditorInput();
			try {
				this.setUrl(input.getURI().toURL().toString());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}

	public void setUrl(final String url) {
		browser.setUrl(url);
		this.setPartName(url.substring(url.lastIndexOf('/') + 1));
		checkButtons();
	}

	/**
	 *
	 */
	private void checkButtons() {
		back.setEnabled(browser.isBackEnabled());
		forward.setEnabled(browser.isForwardEnabled());
	}

	public void setHtml(final String html) {
		browser.setText(html, true);
		this.setPartName("HTML Viewer");
		toolbar.wipe(SWT.LEFT, true);
	}

	@Override
	public void setFocus() {
		browser.setFocus();
	}

	public Control getSizableFontControl() {
		return browser;
	}

	/**
	 * Method createToolItem()
	 * @see msi.gama.gui.views.IToolbarDecoratedView#createToolItem(int, msi.gama.gui.swt.controls.GamaToolbar2)
	 */
	@Override
	public void createToolItems(final GamaToolbar2 tb) {
		this.toolbar = tb;

		back = tb.button("browser/back", "Back", "Go to previous page in history", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				browser.back();
				checkButtons();
			}

		}, SWT.RIGHT);
		home = tb.button("browser/home", "Home", "Go back to the welcome page", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				openWelcomePage(false);
				checkButtons();
			}

		}, SWT.RIGHT);
		forward = tb.button("browser/forward", "Forward", "Go to next page in history", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				browser.forward();
				checkButtons();
			}

		}, SWT.RIGHT);
		tb.sep(GamaToolbarFactory.TOOLBAR_SEP, SWT.RIGHT);
		tb.button("browser/refresh", "Refresh", "Refresh current page", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				browser.refresh();
			}

		}, SWT.RIGHT);
		tb.button("browser/stop", "Stop", "Stop loading page", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				browser.stop();
			}

		}, SWT.RIGHT);

	}

	@Override
	public void setToogle(final Action toggle) {}

}
