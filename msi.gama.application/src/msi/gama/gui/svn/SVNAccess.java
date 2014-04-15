/*********************************************************************************************
 * 
 *
 * 'SVNAccess.java', in plugin 'msi.gama.application', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.svn;

import java.io.File;
import java.util.*;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.PlatformUI;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.*;
import org.tmatesoft.svn.core.wc.*;

public class SVNAccess {

	private static SVNClientManager ourClientManager;
	private static ISVNEventHandler myCommitEventHandler;
	private static ISVNEventHandler myUpdateEventHandler;
	private static ISVNEventHandler myWCEventHandler;

	long committedRevision;

	/* Anonymous credentials */
	private final String name = "anonymous";
	private final String password = "anonymous";

	private static List<SVNDirEntry> fileEntries = new ArrayList<SVNDirEntry>();

	public SVNURL createAccessToRepositoryLocation(final String url) {

		/*
		 * Set up the library to use the protocol which we would like to access a repository
		 * through.
		 */
		DAVRepositoryFactory.setup();

		SVNURL repositoryURL = null;
		/*
		 * SVNURL is a wrapper for URL strings that refer to repository locations.
		 */
		try {
			repositoryURL = SVNURL.parseURIEncoded(url);
		} catch (SVNException e) {
			e.printStackTrace();
		}

		/* Creating custom handlers that will process events */
		myCommitEventHandler = new CommitEventHandler();
		myUpdateEventHandler = new UpdateEventHandler();
		myWCEventHandler = new WCEventHandler();

		/*
		 * Creates a default run-time configuration options driver. Default options created in this
		 * way use the Subversion run-time configuration area
		 */
		DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);

		/*
		 * Creates an instance of SVNClientManager providing authentication information (name,
		 * password) and an options driver
		 */
		ourClientManager = SVNClientManager.newInstance(options, name, password);

		/*
		 * Sets a custom event handler for operations of an SVNCommitClient instance
		 */
		ourClientManager.getCommitClient().setEventHandler(myCommitEventHandler);

		/*
		 * Sets a custom event handler for operations of an SVNUpdateClient instance
		 */
		ourClientManager.getUpdateClient().setEventHandler(myUpdateEventHandler);

		/* Sets a custom event handler for operations of an SVNWCClient instance */
		ourClientManager.getWCClient().setEventHandler(myWCEventHandler);

		return repositoryURL;
	}

	/**
	 * @throws SVNException
	 *             Scan the given repository and return desired entries
	 * 
	 * @param repoUrl the repository url you want to scan
	 * @param monitor
	 * @return a list of all html and snapshots entries from the given repository url
	 */
	public List<SVNDirEntry> getDocsAndSnapshotsEntries(final String repoUrl, final IProgressMonitor monitor)
		throws SVNException {
		SVNRepository repository = null;
		List<SVNDirEntry> entries = new ArrayList<SVNDirEntry>();
		boolean snapshots = false;

		repository = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(repoUrl));
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(name, password);
		repository.setAuthenticationManager(authManager);
		System.out.println("Repository Root: " + repository.getRepositoryRoot(true));
		SVNNodeKind nodeKind = repository.checkPath("", -1);
		if ( nodeKind == SVNNodeKind.NONE ) {
			System.err.println("There is no entry at '" + repoUrl + "'.");
			System.exit(1);
		} else if ( nodeKind == SVNNodeKind.FILE ) {
			System.err.println("The entry at '" + repoUrl + "' is a file while a directory was expected.");
			System.exit(1);
		}

		monitor.worked(500);
		monitor.setTaskName("Scanning the repository  ...");
		entries = getEntries(repository, "", snapshots, monitor);
		return entries;
	}

	/**
	 * Recursive method to get all the desired entries from the given repository
	 * 
	 * @param repository
	 * @param path
	 * @param snapshots
	 * @param monitor
	 * @return
	 * @throws SVNException
	 */
	@SuppressWarnings("rawtypes")
	public static List<SVNDirEntry> getEntries(final SVNRepository repository, final String path, boolean snapshots,
		final IProgressMonitor monitor) throws SVNException {
		Collection entries = repository.getDir(path, -1, null, (Collection) null);
		Iterator iterator = entries.iterator();
		while (iterator.hasNext()) {
			SVNDirEntry entry = (SVNDirEntry) iterator.next();

			if ( entry.getName().endsWith(".html") || snapshots ) {
				monitor.subTask("Importing " + entry.getName());
				monitor.worked(500);
				fileEntries.add(entry);

				// System.out.println("/" + (path.equals("") ? "" : path + "/")
				// + entry.getName()
				// + " ( author: '" + entry.getAuthor() + "'; revision: "
				// + entry.getRevision() + "; date: " + entry.getDate() + ")");
			}

			if ( entry.getKind() == SVNNodeKind.DIR ) {
				snapshots = entry.getName().equals("snapshots");
				getEntries(repository, path.equals("") ? entry.getName() : path + "/" + entry.getName(), snapshots,
					monitor);
			}
		}
		return fileEntries;
	}

	// public void importProjectToSVN(final IResource resource, SVNURL url)
	// throws SVNException {
	// /* Project directory selected */
	// File projectDir = new File(resource.getLocation().toString());
	// try {
	// /*
	// * Recursively imports an unversioned directory project into a
	// * repository and displays what revision the repository was
	// * committed to
	// */
	// System.out.println("Importing/Committing of " + resource.getName()
	// + " in progress ....");
	// committedRevision = importDirectory(
	// projectDir,
	// url,
	// "First import of the project '" + resource.getName()
	// + "' to the Gama Models Library", true).getNewRevision();
	//
	// } catch (SVNException svne) {
	// /* Add new entries file and/or directory of the project */
	// addEntriesToSVN(projectDir);
	//
	// SVNCommitClient commitClient = ourClientManager.getCommitClient();
	// /*
	// * Instruct SVNCommitClient to commit "missing" files and
	// * directories
	// */
	// commitClient.setCommitParameters(new DefaultSVNCommitParameters() {
	// @Override
	// public Action onMissingFile(File file) {
	// /* Default action is to "skip" missing file */
	// return DELETE;
	// }
	//
	// @Override
	// public Action onMissingDirectory(File file) {
	// /* Default action is to "skip" missing directory */
	// return DELETE;
	// }
	// });
	//
	// System.out.println("Status for '" + projectDir.getAbsolutePath() + "':");
	// try {
	// /*
	// * gets and shows status information for the WC directory.
	// * status will be recursive on wcDir, will also cover the
	// * repository, won't cover unmodified entries, will disregard
	// * 'svn:ignore' property ignores (if any), will ignore externals
	// * definitions.
	// */
	// showStatus(projectDir, true, true, false, true, false);
	// } catch (SVNException svne1) {
	// error("error while recursively performing status for '"
	// + projectDir.getAbsolutePath() + "'", svne1);
	// }
	//
	// commitProjectToSVN(projectDir);
	//
	// PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
	// @Override
	// public void run() {
	// MessageDialog.openInformation(getShell(), "Commit complete", "Project "
	// + resource.getProject().getName() + " has been committed to the SVN");
	// }
	// });
	//
	// return;
	// }
	//
	// checkoutProjectFromSVN(url, projectDir, true);
	// PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
	// @Override
	// public void run() {
	// MessageDialog.openInformation(getShell(), "Import complete", "Project "
	// + resource.getProject().getName() + " has been imported to the SVN");
	// }
	// });
	//
	// System.out.println("Project " + resource.getName() +
	// " imported to revision "
	// + committedRevision);
	// }

	private Shell getShell() {
		return Display.getDefault().getActiveShell();
	}

	/**
	 * Checkout the project from the given SVNURL and create a working copy or this project
	 * 
	 * @param url of the project you want to checkout
	 * @param projectDir destination directory of the working copy
	 * @param isRecursive true if you want a recursive checkout of all the children also
	 * @return true if success, false if not
	 */
	public boolean checkoutProjectFromSVN(final SVNURL url, final File projectDir, final boolean isRecursive) {
		System.out.println("Checking out a working copy from '" + url + "'...");
		try {
			/*
			 * Recursively checks out a working copy from url into wcDir. SVNRevision.HEAD means the
			 * latest revision to be checked out. Checkout it so that we have .svn If directory
			 * already existed in repository, do recursive checkout
			 */
			checkout(url, SVNRevision.HEAD, projectDir);
		} catch (SVNException svne) {
			SVNErrorMessage mess = svne.getErrorMessage();
			/* If there is no internet connexion, show up a dialog */
			if ( mess.getErrorCode().getCode() == 175002 ) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {
						MessageDialog
							.openError(getShell(), "Error while checking out", "Check your internet connexion");
					}
				});
			}
			error("Error while checking out a working copy for the location '" + url + "'", svne);
			return false;
		}
		return true;
	}

	private static long checkout(final SVNURL url, final SVNRevision revision, final File destPath) throws SVNException {

		SVNUpdateClient updateClient = ourClientManager.getUpdateClient();
		/* Sets externals not to be ignored during the checkout */
		updateClient.setIgnoreExternals(false);
		/* Returns the number of the revision at which the working copy is */
		return updateClient.doCheckout(url, destPath, revision, revision, SVNDepth.INFINITY, true);
	}

	// public void updateProjectFromSVN(File projectDir) {
	// System.out.println("Updating '" + projectDir.getAbsolutePath() + "'...");
	// try {
	// /*
	// * recursively updates wcDir to the latest revision
	// * (SVNRevision.HEAD)
	// */
	// update(projectDir, SVNRevision.HEAD);
	// } catch (SVNException svne) {
	// error("error while recursively updating the working copy at '"
	// + projectDir.getAbsolutePath() + "'", svne);
	// }
	// System.out.println("");
	// }

	// public void commitProjectToSVN(File projectDir) {
	// System.out.println("Committing changes for '" + projectDir.getName() +
	// "'...");
	// try {
	// /**
	// * Commits changes in projectDir to the repository with not leaving
	// * items locked (if any) after the commit succeeds.
	// */
	// committedRevision = commit(projectDir, true,
	// "Project " + projectDir.getName() +
	// " has been committed").getNewRevision();
	// } catch (SVNException svne) {
	// error("Error while committing changes to the working copy at '" +
	// projectDir.getName()
	// + "'", svne);
	// }
	// System.out.println("Committed to revision " + committedRevision);
	// System.out.println();
	// }

	// public void addEntriesToSVN(File projectDir) {
	// try {
	// /* Recursively schedules projectDir for addition */
	// addEntries(projectDir);
	// } catch (SVNException svne) {
	// error("Error while recursively adding entries in '" +
	// projectDir.getName() + "'", svne);
	// }
	// }

	/** Displays error informations */
	private static void error(final String message, final Exception e) {
		System.err.println(message + (e != null ? ": " + e.getMessage() : ""));
	}

	// /** This method imports a local directory to a repository */
	// private static SVNCommitInfo importDirectory(File localPath, SVNURL
	// dstURL,
	// String commitMessage, boolean isRecursive) throws SVNException {
	//
	// return ourClientManager.getCommitClient().doImport(localPath, dstURL,
	// commitMessage, null,
	// true, true, SVNDepth.INFINITY);
	// }
	//
	// /** Commits changes in a working copy to a repository. */
	// private static SVNCommitInfo commit(File path, boolean keepLocks, String
	// commitMessage)
	// throws SVNException {
	//
	// /*
	// * Returns SVNCommitInfo containing information on the new revision
	// * committed
	// */
	// return ourClientManager.getCommitClient().doCommit(new File[] { path },
	// keepLocks,
	// commitMessage, null, null, true, false, SVNDepth.INFINITY);
	// }
	//
	// private static long update(File path, SVNRevision revision) throws
	// SVNException {
	//
	// SVNUpdateClient updateClient = ourClientManager.getUpdateClient();
	// /* Sets externals not to be ignored during the update */
	// updateClient.setIgnoreExternals(false);
	// /* Returns the number of the revision wcPath was updated to */
	// return updateClient.doUpdate(path, revision, SVNDepth.INFINITY, true,
	// true);
	// }
	//
	// private static void addEntries(File path) throws SVNException {
	//
	// ourClientManager.getWCClient().doAdd(path, true, false, false,
	// SVNDepth.INFINITY, false,
	// false);
	// }
	//
	// private static void showStatus(File path, boolean isRecursive, boolean
	// isRemote,
	// boolean isReportAll, boolean isIncludeIgnored, boolean
	// isCollectParentExternals)
	// throws SVNException {
	//
	// ourClientManager.getStatusClient().doStatus(path, SVNRevision.WORKING,
	// SVNDepth.INFINITY,
	// isRemote, isReportAll, isIncludeIgnored, isCollectParentExternals,
	// new StatusHandler(isRemote), null);
	// }
	//
	// /**
	// * Recursively displays info for directory at the given working revision
	// and
	// * depth
	// */
	// private static void showInfo(File path, SVNRevision revision, SVNDepth
	// depth)
	// throws SVNException {
	// try {
	// ISVNInfoHandler handler = new InfoHandler();
	// ourClientManager.getWCClient().doInfo(path, revision, revision, depth,
	// null, handler);
	// } catch (SVNException svne) {
	// error("Error while recursively getting info for the working copy at'"
	// + path.getAbsolutePath() + "'", svne);
	// }
	//
	// }
}
