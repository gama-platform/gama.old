/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.svn;

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