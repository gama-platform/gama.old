/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.application.svn;

import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.wc.ISVNInfoHandler;
import org.tmatesoft.svn.core.wc.SVNInfo;

/*
 * An implementation of ISVNInfoHandler that is  used  in  WorkingCopy.java  to 
 * display  info  on  a  working  copy path.  This implementation is passed  to
 * 
 * SVNWCClient.doInfo(File path, SVNRevision revision, boolean recursive, 
 * ISVNInfoHandler handler) 
 * 
 * For each item to be processed doInfo(..) collects information and creates an 
 * SVNInfo which keeps that information. Then  doInfo(..)  calls  implementor's 
 * handler.handleInfo(SVNInfo) where it passes the gathered info.
 */
public class InfoHandler implements ISVNInfoHandler {
    /*
     * This is an implementation  of  ISVNInfoHandler.handleInfo(SVNInfo info).
     * Just prints out information on a Working Copy path in the manner of  the
     * native SVN command line client.
     */
    @Override
	public void handleInfo(SVNInfo info) {
        System.out.println("-----------------INFO-----------------");
        System.out.println("Local Path: " + info.getFile().getPath());
        System.out.println("URL: " + info.getURL());
        if (info.isRemote() && info.getRepositoryRootURL() != null) {
            System.out.println("Repository Root URL: "
                    + info.getRepositoryRootURL());
        }
        if(info.getRepositoryUUID() != null){
            System.out.println("Repository UUID: " + info.getRepositoryUUID());
        }
        System.out.println("Revision: " + info.getRevision().getNumber());
        System.out.println("Node Kind: " + info.getKind().toString());
        if(!info.isRemote()){
            System.out.println("Schedule: "
                    + (info.getSchedule() != null ? info.getSchedule() : "normal"));
        }
        System.out.println("Last Changed Author: " + info.getAuthor());
        System.out.println("Last Changed Revision: "
                + info.getCommittedRevision().getNumber());
        System.out.println("Last Changed Date: " + info.getCommittedDate());
        if (info.getPropTime() != null) {
            System.out
                    .println("Properties Last Updated: " + info.getPropTime());
        }
        if (info.getKind() == SVNNodeKind.FILE && info.getChecksum() != null) {
            if (info.getTextTime() != null) {
                System.out.println("Text Last Updated: " + info.getTextTime());
            }
            System.out.println("Checksum: " + info.getChecksum());
        }
        if (info.getLock() != null) {
            if (info.getLock().getID() != null) {
                System.out.println("Lock Token: " + info.getLock().getID());
            }
            System.out.println("Lock Owner: " + info.getLock().getOwner());
            System.out.println("Lock Created: "
                    + info.getLock().getCreationDate());
            if (info.getLock().getExpirationDate() != null) {
                System.out.println("Lock Expires: "
                        + info.getLock().getExpirationDate());
            }
            if (info.getLock().getComment() != null) {
                System.out.println("Lock Comment: "
                        + info.getLock().getComment());
            }
        }
    }
}