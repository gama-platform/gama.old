/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.gui.application.svn;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

public class DialogSVNAccess extends ApplicationWindow {
	FormToolkit toolkit;
	Form form;

	public DialogSVNAccess(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.CLOSE);
  	}

  	@Override
	protected Control createContents(Composite parent) {
  		getShell().setText("Set up an account");
	    parent.setSize(460,230);
	    
	    Composite composite = new Composite(parent, SWT.NULL);
	  	composite.setLayout(new FillLayout());
	  	/* Sets up the toolkit. */
	  	toolkit = new FormToolkit(getShell().getDisplay());
	  	/* Creates a form instance. */
	  	form = toolkit.createForm(composite);
//    	form.setLayoutData(new GridData(GridData.FILL_BOTH));
	  	form.setText("To set up a GAMA SVN account access");

	  	form.getBody().setLayout(new TableWrapLayout());

	  	FormText text = toolkit.createFormText(form.getBody(), true);
     	// text.setLayoutData(new TableWrapData(TableWrapData.FILL));

	  	text.setText("<form><p>In order to join the collaborative development environment of GAMA and being able "
			   + "to share projects with the team, you have to send an email to : " 
			   + "<a href=\"mailto:alexis.drogoul@gmail.com?subject=Gama_SVN_account_request\"> alexis.drogoul@gmail.com</a></p>"
			   + "<p>Please, provide your name, email adress and the reason why you need to share projects using Gama. "
			   + "Your email address must be associated to a Google Account. Anyone can create a Google Account at: "  
			   + "http://www.google.com/accounts/NewAccount <br /><br />For more details, please visit http://code.google.com/p/gama-platform</p></form>", true, true);

	  
	  	text.addHyperlinkListener(new IHyperlinkListener() {

	  		@Override
			public void linkEntered(HyperlinkEvent e) {}
	  		@Override
			public void linkExited(HyperlinkEvent e) {}

	  		@Override
			public void linkActivated(HyperlinkEvent e) {
				try {
					String href = ((String)e.getHref());
		  			if(href.startsWith("mailto")) {
		  				if(Desktop.isDesktopSupported()) {
							if(Desktop.getDesktop().isSupported(Desktop.Action.MAIL))
								Desktop.getDesktop().mail(new URI(href));
		  				}
		  			}
		  			else {
		  				PlatformUI.getWorkbench().getBrowserSupport()
				  			.createBrowser(IWorkbenchBrowserSupport.AS_EXTERNAL, null, null, null)
				  			.openURL(new URL((String) e.getHref()));
		  			}
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				} catch (PartInitException e2) {
					e2.printStackTrace();
				}
  			}		  
	  	});

	  	text = toolkit.createFormText(form.getBody(), true);
	  	return composite;
  	}
}