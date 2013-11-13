/*******************************************************************************
 * Copyright (c) 2005-2008 Polarion Software.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Alexander Gurov - Initial API and implementation
 * Pavel Zuev - peg revisions for compare operation
 * Micha Riser - [patch] JavaHLConnector creates a huge amount of short living threads
 *******************************************************************************/

package org.polarion.team.svn.connector.svnkit;

import java.io.*;
import java.util.*;
import org.eclipse.team.svn.core.SVNTeamPlugin;
import org.eclipse.team.svn.core.connector.*;
import org.eclipse.team.svn.core.connector.SVNConflictDescriptor.Action;
import org.eclipse.team.svn.core.utility.*;
import org.tigris.subversion.javahl.*;
import org.tmatesoft.svn.core.client.SVNClientEx;
import org.tmatesoft.svn.core.internal.io.svn.SVNSSHSession;
import org.tmatesoft.svn.core.internal.wc.SVNFileUtil;
import org.tmatesoft.svn.core.javahl.*;

/**
 * SVN connector wrapper
 * 
 * @author <A HREF="www.polarion.org">Alexander Gurov</A>, POLARION.ORG
 * 
 * @version $Revision: $ $Date: $
 */
public class SVNKitConnector implements ISVNConnector {

	protected static ProgressMonitorThread monitorWrapperThread;

	// protected SVNAdmin svnAdmin;
	protected SVNClientEx client;
	protected ISVNCredentialsPrompt prompt;
	protected Notify2Composite composite;
	protected ISVNNotificationCallback installedNotify2;

	protected ArrayList<ISVNCallListener> callListeners;

	public SVNKitConnector() {
		this.callListeners = new ArrayList<ISVNCallListener>();
		SVNFileUtil.setSleepForTimestamp(false);
		this.client = new SVNClientEx();
		this.client.notification2(ConversionUtility.convert(this.composite = new Notify2Composite()));
		// this.svnAdmin = new SVNAdmin();
		SVNSSHSession.setUsePersistentConnection(SVNTeamPlugin.instance().getOptionProvider().isPersistentSSHEnabled());
	}

	@Override
	public void addCallListener(final ISVNCallListener listener) {
		this.callListeners.add(listener);
	}

	@Override
	public void removeCallListener(final ISVNCallListener listener) {
		this.callListeners.remove(listener);
	}

	@Override
	public String getConfigDirectory() throws SVNConnectorException {
		this.fireAsked(ISVNCallListener.GET_CONFIG_DIRECTORY, null);
		try {
			String retVal = this.client.getConfigDirectory();
			this.fireSucceeded(ISVNCallListener.GET_CONFIG_DIRECTORY, null, retVal);
			return retVal;
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.GET_CONFIG_DIRECTORY, null);
		}
		// unreachable code
		return null;
	}

	@Override
	public void setConfigDirectory(String configDir) throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("configDir", configDir);
		this.fireAsked(ISVNCallListener.SET_CONFIG_DIRECTORY, parameters);
		configDir = (String) parameters.get("configDir");

		try {
			this.client.setConfigDirectory(configDir);

			this.fireSucceeded(ISVNCallListener.GET_CONFIG_DIRECTORY, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.SET_CONFIG_DIRECTORY, parameters);
		}
	}

	@Override
	public void setUsername(String username) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("username", username);
		this.fireAsked(ISVNCallListener.SET_USERNAME, parameters);
		username = (String) parameters.get("username");

		this.client.username(username);

		this.fireSucceeded(ISVNCallListener.SET_USERNAME, parameters, null);
	}

	@Override
	public void setPassword(String password) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("password", password);
		this.fireAsked(ISVNCallListener.SET_USERNAME, parameters);
		password = (String) parameters.get("password");

		this.client.password(password);

		this.fireSucceeded(ISVNCallListener.SET_USERNAME, parameters, null);
	}

	@Override
	public boolean isCredentialsCacheEnabled() {
		this.fireAsked(ISVNCallListener.IS_CREDENTIALS_CACHE_ENABLED, null);

		boolean retVal = this.client.isCredentialsCacheEnabled();

		this.fireSucceeded(ISVNCallListener.IS_CREDENTIALS_CACHE_ENABLED, null, Boolean.valueOf(retVal));

		return retVal;
	}

	@Override
	public void setCredentialsCacheEnabled(boolean cacheCredentials) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("cacheCredentials", Boolean.valueOf(cacheCredentials));
		this.fireAsked(ISVNCallListener.SET_CREDENTIALS_CACHE_ENABLED, parameters);
		cacheCredentials = ((Boolean) parameters.get("cacheCredentials")).booleanValue();

		this.client.setCredentialsCacheEnabled(cacheCredentials);

		this.fireSucceeded(ISVNCallListener.SET_CREDENTIALS_CACHE_ENABLED, parameters, null);
	}

	@Override
	public void setPrompt(ISVNCredentialsPrompt prompt) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("prompt", prompt);
		this.fireAsked(ISVNCallListener.SET_PROMPT, parameters);
		prompt = (ISVNCredentialsPrompt) parameters.get("prompt");

		this.client.setPrompt(prompt == null ? null : new RepositoryInfoPrompt(this.prompt = prompt));

		this.fireSucceeded(ISVNCallListener.SET_PROMPT, parameters, null);
	}

	@Override
	public ISVNCredentialsPrompt getPrompt() {
		this.fireAsked(ISVNCallListener.GET_PROMPT, null);
		this.fireSucceeded(ISVNCallListener.GET_PROMPT, null, this.prompt);
		return this.prompt;
	}

	@Override
	public void setProxy(String host, int port, String username, String password) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("host", host);
		parameters.put("port", Integer.valueOf(port));
		parameters.put("username", username);
		parameters.put("password", password);
		this.fireAsked(ISVNCallListener.SET_PROXY, parameters);
		host = (String) parameters.get("host");
		port = ((Integer) parameters.get("port")).intValue();
		username = (String) parameters.get("username");
		password = (String) parameters.get("password");

		this.client.setProxy(host, port, username, password);

		this.fireSucceeded(ISVNCallListener.SET_PROXY, parameters, null);
	}

	@Override
	public void setClientSSLCertificate(String certPath, String passphrase) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("certPath", certPath);
		parameters.put("passphrase", passphrase);
		this.fireAsked(ISVNCallListener.SET_CLIENT_SSL_CERTIFICATE, parameters);
		certPath = (String) parameters.get("certPath");
		passphrase = (String) parameters.get("passphrase");

		this.client.setClientSSLCertificate(certPath, passphrase);

		this.fireSucceeded(ISVNCallListener.SET_CLIENT_SSL_CERTIFICATE, parameters, null);
	}

	@Override
	public boolean isSSLCertificateCacheEnabled() {
		this.fireAsked(ISVNCallListener.IS_SSL_CERTIFICATE_CACHE_ENABLED, null);

		boolean retVal = this.client.isSSLCertificateCacheEnabled();

		this.fireSucceeded(ISVNCallListener.IS_SSL_CERTIFICATE_CACHE_ENABLED, null, Boolean.valueOf(retVal));
		return retVal;
	}

	@Override
	public void setSSLCertificateCacheEnabled(boolean enabled) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("enabled", Boolean.valueOf(enabled));
		this.fireAsked(ISVNCallListener.SET_SSL_CERTIFICATE_CACHE_ENABLED, parameters);
		enabled = ((Boolean) parameters.get("enabled")).booleanValue();

		this.client.setSSLCertificateCacheEnabled(enabled);

		this.fireSucceeded(ISVNCallListener.SET_SSL_CERTIFICATE_CACHE_ENABLED, parameters, null);
	}

	@Override
	public void setSSHCredentials(String username, String privateKeyPath, String passphrase, int port) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("username", username);
		parameters.put("privateKeyPath", privateKeyPath);
		parameters.put("passphrase", passphrase);
		parameters.put("port", Integer.valueOf(port));
		this.fireAsked(ISVNCallListener.SET_SSH_CREDENTIALS, parameters);
		username = (String) parameters.get("username");
		privateKeyPath = (String) parameters.get("privateKeyPath");
		passphrase = (String) parameters.get("passphrase");
		port = ((Integer) parameters.get("port")).intValue();

		this.client.setSSHCredentials(username, privateKeyPath, passphrase, port);

		this.fireSucceeded(ISVNCallListener.SET_SSH_CREDENTIALS, parameters, null);
	}

	@Override
	public void setSSHCredentials(String username, String password, int port) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("username", username);
		parameters.put("password", password);
		parameters.put("port", Integer.valueOf(port));
		this.fireAsked(ISVNCallListener.SET_SSH_CREDENTIALS_PASSWORD, parameters);
		username = (String) parameters.get("username");
		password = (String) parameters.get("password");
		port = ((Integer) parameters.get("port")).intValue();

		this.client.setSSHCredentials(username, password, port);

		this.fireSucceeded(ISVNCallListener.SET_SSH_CREDENTIALS_PASSWORD, parameters, null);
	}

	@Override
	public void setCommitMissingFiles(boolean commitMissingFiles) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("commitMissingFiles", Boolean.valueOf(commitMissingFiles));
		this.fireAsked(ISVNCallListener.SET_COMMIT_MISSING_FILES, parameters);
		commitMissingFiles = ((Boolean) parameters.get("commitMissingFiles")).booleanValue();

		this.client.setCommitMissedFiles(commitMissingFiles);

		this.fireSucceeded(ISVNCallListener.SET_COMMIT_MISSING_FILES, parameters, null);
	}

	@Override
	public boolean isCommitMissingFiles() {
		this.fireAsked(ISVNCallListener.IS_COMMIT_MISSING_FILES, null);

		boolean retVal = this.client.isCommitMissingFile();

		this.fireSucceeded(ISVNCallListener.IS_COMMIT_MISSING_FILES, null, Boolean.valueOf(retVal));

		return retVal;
	}

	// public void setTouchUnresolved(boolean touchUnresolved) {
	// Map<String, Object> parameters = new HashMap<String, Object>();
	// parameters.put("touchUnresolved", Boolean.valueOf(touchUnresolved));
	// this.fireAsked(ISVNCallListener.SET_TOUCH_UNRESOLVED, parameters);
	// touchUnresolved = ((Boolean)parameters.get("touchUnresolved")).booleanValue();
	//
	// this.client.setTouchUnresolved(touchUnresolved);
	//
	// this.fireSucceeded(ISVNCallListener.SET_TOUCH_UNRESOLVED, parameters, null);
	// }

	// public boolean isTouchUnresolved() {
	// this.fireAsked(ISVNCallListener.IS_TOUCH_UNRESOLVED, null);
	//
	// boolean retVal = this.client.isTouchUnresolved();
	//
	// this.fireSucceeded(ISVNCallListener.IS_TOUCH_UNRESOLVED, null, Boolean.valueOf(retVal));
	//
	// return retVal;
	// }

	@Override
	public void setNotificationCallback(ISVNNotificationCallback notify) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("notify", notify);
		this.fireAsked(ISVNCallListener.SET_NOTIFICATION_CALLBACK, parameters);
		notify = (ISVNNotificationCallback) parameters.get("notify");

		if ( this.installedNotify2 != null ) {
			this.composite.remove(this.installedNotify2);
		}
		this.installedNotify2 = notify;
		if ( this.installedNotify2 != null ) {
			this.composite.add(this.installedNotify2);
		}

		this.fireSucceeded(ISVNCallListener.SET_NOTIFICATION_CALLBACK, parameters, null);
	}

	@Override
	public ISVNNotificationCallback getNotificationCallback() {
		this.fireAsked(ISVNCallListener.GET_NOTIFICATION_CALLBACK, null);
		this.fireSucceeded(ISVNCallListener.GET_NOTIFICATION_CALLBACK, null, this.installedNotify2);
		return this.installedNotify2;
	}

	@Override
	public long checkout(SVNEntryRevisionReference fromReference, String destPath, int depth, long options,
		ISVNProgressMonitor monitor) throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("fromReference", fromReference);
		parameters.put("destPath", destPath);
		parameters.put("depth", Integer.valueOf(depth));
		parameters.put("options", Long.valueOf(options));
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.CHECKOUT, parameters);
		fromReference = (SVNEntryRevisionReference) parameters.get("fromReference");
		destPath = (String) parameters.get("destPath");
		depth = ((Integer) parameters.get("depth")).intValue();
		options = ((Long) parameters.get("options")).longValue();
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			long retVal =
				this.client.checkout(fromReference.path, destPath, ConversionUtility.convert(fromReference.revision),
					ConversionUtility.convert(fromReference.pegRevision), depth,
					(options & Options.IGNORE_EXTERNALS) != 0, (options & Options.ALLOW_UNVERSIONED_OBSTRUCTIONS) != 0);
			this.fireSucceeded(ISVNCallListener.CHECKOUT, parameters, Long.valueOf(retVal));
			return retVal;
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.CHECKOUT, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
		// unreachable code
		return 0;
	}

	@Override
	public void lock(String[] path, String comment, long options, ISVNProgressMonitor monitor)
		throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("path", path);
		parameters.put("comment", comment);
		parameters.put("options", Long.valueOf(options));
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.LOCK, parameters);
		path = (String[]) parameters.get("path");
		comment = (String) parameters.get("comment");
		options = ((Long) parameters.get("options")).longValue();
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			this.client.lock(path, comment, (options & Options.FORCE) != 0);

			this.fireSucceeded(ISVNCallListener.LOCK, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.LOCK, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public void unlock(String[] path, long options, ISVNProgressMonitor monitor) throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("path", path);
		parameters.put("options", Long.valueOf(options));
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.UNLOCK, parameters);
		path = (String[]) parameters.get("path");
		options = ((Long) parameters.get("options")).longValue();
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			this.client.unlock(path, (options & Options.FORCE) != 0);

			this.fireSucceeded(ISVNCallListener.UNLOCK, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.UNLOCK, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public void add(String path, int depth, long options, ISVNProgressMonitor monitor) throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("path", path);
		parameters.put("depth", Integer.valueOf(depth));
		parameters.put("options", Long.valueOf(options));
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.ADD, parameters);
		path = (String) parameters.get("path");
		depth = ((Integer) parameters.get("depth")).intValue();
		options = ((Long) parameters.get("options")).longValue();
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			this.client.add(path, depth, (options & Options.FORCE) != 0, (options & Options.INCLUDE_IGNORED) != 0,
				(options & Options.INCLUDE_PARENTS) != 0);

			this.fireSucceeded(ISVNCallListener.ADD, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.ADD, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public long[] commit(String[] path, String message, String[] changelistNames, int depth, long options,
		Map revProps, ISVNProgressMonitor monitor) throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("path", path);
		parameters.put("message", message);
		parameters.put("changelistNames", changelistNames);
		parameters.put("depth", Integer.valueOf(depth));
		parameters.put("options", Long.valueOf(options));
		parameters.put("revProps", revProps);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.COMMIT, parameters);
		path = (String[]) parameters.get("path");
		message = (String) parameters.get("message");
		changelistNames = (String[]) parameters.get("changelistNames");
		depth = ((Integer) parameters.get("depth")).intValue();
		options = ((Long) parameters.get("options")).longValue();
		revProps = (Map) parameters.get("revProps");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			long[] retVal =
				this.client.commit(path, message, depth, (options & Options.KEEP_LOCKS) != 0,
					(options & Options.KEEP_CHANGE_LIST) != 0, changelistNames, ConversionUtility.convert(revProps),
					true);

			this.fireSucceeded(ISVNCallListener.COMMIT, parameters, retVal);

			return retVal;
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.COMMIT, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
		// unreachable code
		return null;
	}

	@Override
	public long[] update(String[] path, SVNRevision revision, int depth, long options, ISVNProgressMonitor monitor)
		throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("path", path);
		parameters.put("revision", revision);
		parameters.put("depth", Integer.valueOf(depth));
		parameters.put("options", Long.valueOf(options));
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.UPDATE, parameters);
		path = (String[]) parameters.get("path");
		revision = (SVNRevision) parameters.get("revision");
		depth = ((Integer) parameters.get("depth")).intValue();
		options = ((Long) parameters.get("options")).longValue();
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			long[] retVal =
				this.client.update(path, ConversionUtility.convert(revision), depth,
					(options & Options.DEPTH_IS_STICKY) != 0, (options & Options.IGNORE_EXTERNALS) != 0,
					(options & Options.ALLOW_UNVERSIONED_OBSTRUCTIONS) != 0);

			this.fireSucceeded(ISVNCallListener.UPDATE, parameters, retVal);

			return retVal;
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.UPDATE, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
		// unreachable code
		return null;
	}

	@Override
	public long doSwitch(String path, SVNEntryRevisionReference toReference, int depth, long options,
		ISVNProgressMonitor monitor) throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("path", path);
		parameters.put("toReference", toReference);
		parameters.put("depth", Integer.valueOf(depth));
		parameters.put("options", Long.valueOf(options));
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.DO_SWITCH, parameters);
		path = (String) parameters.get("path");
		toReference = (SVNEntryRevisionReference) parameters.get("toReference");
		depth = ((Integer) parameters.get("depth")).intValue();
		options = ((Long) parameters.get("options")).longValue();
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			long retVal =
				this.client.doSwitch(path, toReference.path, ConversionUtility.convert(toReference.revision),
					ConversionUtility.convert(toReference.pegRevision), depth,
					(options & Options.DEPTH_IS_STICKY) != 0, (options & Options.IGNORE_EXTERNALS) != 0,
					(options & Options.ALLOW_UNVERSIONED_OBSTRUCTIONS) != 0);

			this.fireSucceeded(ISVNCallListener.DO_SWITCH, parameters, Long.valueOf(retVal));

			return retVal;
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.DO_SWITCH, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
		// unreachable code
		return 0;
	}

	@Override
	public void revert(String path, int depth, String[] changelistNames, ISVNProgressMonitor monitor)
		throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("path", path);
		parameters.put("depth", Integer.valueOf(depth));
		parameters.put("changelistNames", changelistNames);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.REVERT, parameters);
		path = (String) parameters.get("path");
		depth = ((Integer) parameters.get("depth")).intValue();
		changelistNames = (String[]) parameters.get("changelistNames");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			this.client.revert(path, depth, changelistNames);

			this.fireSucceeded(ISVNCallListener.REVERT, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.REVERT, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public void status(String path, int depth, long options, String[] changelistNames,
		ISVNEntryStatusCallback callback, ISVNProgressMonitor monitor) throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("path", path);
		parameters.put("depth", Integer.valueOf(depth));
		parameters.put("options", Long.valueOf(options));
		parameters.put("changelistNames", changelistNames);
		parameters.put("callback", callback);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.STATUS, parameters);
		path = (String) parameters.get("path");
		depth = ((Integer) parameters.get("depth")).intValue();
		options = ((Long) parameters.get("options")).longValue();
		changelistNames = (String[]) parameters.get("changelistNames");
		callback = (ISVNEntryStatusCallback) parameters.get("callback");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			this.client.status(path, depth, (options & Options.SERVER_SIDE) != 0,
				(options & Options.INCLUDE_UNCHANGED) != 0, (options & Options.INCLUDE_IGNORED) != 0,
				(options & Options.IGNORE_EXTERNALS) != 0, changelistNames, ConversionUtility.convert(callback));

			this.fireSucceeded(ISVNCallListener.STATUS, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.STATUS, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public void relocate(String from, String to, String path, int depth, ISVNProgressMonitor monitor)
		throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("from", from);
		parameters.put("to", to);
		parameters.put("path", path);
		parameters.put("depth", Integer.valueOf(depth));
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.RELOCATE, parameters);
		from = (String) parameters.get("from");
		to = (String) parameters.get("to");
		path = (String) parameters.get("path");
		depth = ((Integer) parameters.get("depth")).intValue();
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			this.client.relocate(from, to, path, depth == Depth.INFINITY);

			this.fireSucceeded(ISVNCallListener.RELOCATE, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.RELOCATE, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public void cleanup(String path, ISVNProgressMonitor monitor) throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("path", path);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.CLEANUP, parameters);
		path = (String) parameters.get("path");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			this.client.cleanup(path);

			this.fireSucceeded(ISVNCallListener.CLEANUP, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.CLEANUP, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public void merge(SVNEntryRevisionReference reference1, SVNEntryRevisionReference reference2, String localPath,
		int depth, long options, ISVNProgressMonitor monitor) throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("reference1", reference1);
		parameters.put("reference2", reference2);
		parameters.put("localPath", localPath);
		parameters.put("depth", Integer.valueOf(depth));
		parameters.put("options", Long.valueOf(options));
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.MERGE_TWO_URLS, parameters);
		reference1 = (SVNEntryRevisionReference) parameters.get("reference1");
		reference2 = (SVNEntryRevisionReference) parameters.get("reference2");
		localPath = (String) parameters.get("localPath");
		depth = ((Integer) parameters.get("depth")).intValue();
		options = ((Long) parameters.get("options")).longValue();
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			this.client.merge(reference1.path, ConversionUtility.convert(reference1.revision), reference2.path,
				ConversionUtility.convert(reference2.revision), localPath, (options & Options.FORCE) != 0, depth,
				(options & Options.IGNORE_ANCESTRY) != 0, (options & Options.SIMULATE) != 0,
				(options & Options.RECORD_ONLY) != 0);

			this.fireSucceeded(ISVNCallListener.MERGE_TWO_URLS, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.MERGE_TWO_URLS, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public void merge(SVNEntryReference reference, SVNRevisionRange[] revisions, String localPath, int depth,
		long options, ISVNProgressMonitor monitor) throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("reference", reference);
		parameters.put("revisions", revisions);
		parameters.put("localPath", localPath);
		parameters.put("depth", Integer.valueOf(depth));
		parameters.put("options", Long.valueOf(options));
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.MERGE, parameters);
		reference = (SVNEntryReference) parameters.get("reference");
		revisions = (SVNRevisionRange[]) parameters.get("revisions");
		localPath = (String) parameters.get("localPath");
		depth = ((Integer) parameters.get("depth")).intValue();
		options = ((Long) parameters.get("options")).longValue();
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			this.client.merge(reference.path, ConversionUtility.convert(reference.pegRevision),
				ConversionUtility.convert(revisions), localPath, (options & Options.FORCE) != 0, depth,
				(options & Options.IGNORE_ANCESTRY) != 0, (options & Options.SIMULATE) != 0,
				(options & Options.RECORD_ONLY) != 0);

			this.fireSucceeded(ISVNCallListener.MERGE, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.MERGE, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public void mergeReintegrate(SVNEntryReference reference, String localPath, long options,
		ISVNProgressMonitor monitor) throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("reference", reference);
		parameters.put("localPath", localPath);
		parameters.put("options", Long.valueOf(options));
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.MERGE_REINTEGRATE, parameters);
		reference = (SVNEntryReference) parameters.get("reference");
		localPath = (String) parameters.get("localPath");
		options = ((Long) parameters.get("options")).longValue();
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			this.client.mergeReintegrate(reference.path, ConversionUtility.convert(reference.pegRevision), localPath,
				(options & Options.SIMULATE) != 0);

			this.fireSucceeded(ISVNCallListener.MERGE_REINTEGRATE, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.MERGE_REINTEGRATE, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public SVNMergeInfo getMergeInfo(SVNEntryReference reference, ISVNProgressMonitor monitor)
		throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("reference", reference);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.GET_MERGE_INFO, parameters);
		reference = (SVNEntryReference) parameters.get("reference");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			SVNMergeInfo retVal =
				ConversionUtility.convert(this.client.getMergeinfo(reference.path,
					ConversionUtility.convert(reference.pegRevision)));

			this.fireSucceeded(ISVNCallListener.GET_MERGE_INFO, parameters, retVal);

			return retVal;
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.GET_MERGE_INFO, parameters);
		} catch (SubversionException ex) {
			this.handleSubversionException(ex, ISVNCallListener.GET_MERGE_INFO, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
		// unreachable code
		return null;
	}

	@Override
	public void getMergeInfoLog(int logKind, SVNEntryReference reference, SVNEntryReference mergeSourceReference,
		String[] revProps, final int depth, long options, ISVNLogEntryCallback cb, ISVNProgressMonitor monitor)
		throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("logKind", Integer.valueOf(logKind));
		parameters.put("reference", reference);
		parameters.put("mergeSourceReference", mergeSourceReference);
		parameters.put("revProps", revProps);
		parameters.put("options", Long.valueOf(options));
		parameters.put("cb", cb);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.GET_MERGE_INFO_LOG, parameters);
		logKind = ((Integer) parameters.get("logKind")).intValue();
		reference = (SVNEntryReference) parameters.get("reference");
		mergeSourceReference = (SVNEntryReference) parameters.get("mergeSourceReference");
		revProps = (String[]) parameters.get("revProps");
		options = ((Long) parameters.get("options")).longValue();
		cb = (ISVNLogEntryCallback) parameters.get("cb");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			this.client.getMergeinfoLog(logKind, reference.path, ConversionUtility.convert(reference.pegRevision),
				mergeSourceReference.path, ConversionUtility.convert(mergeSourceReference.pegRevision),
				(options & Options.DISCOVER_PATHS) != 0, revProps, ConversionUtility.convert(cb));

			this.fireSucceeded(ISVNCallListener.GET_MERGE_INFO_LOG, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.GET_MERGE_INFO_LOG, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public String[] suggestMergeSources(SVNEntryReference reference, ISVNProgressMonitor monitor)
		throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("reference", reference);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.SUGGEST_MERGE_SOURCES, parameters);
		reference = (SVNEntryReference) parameters.get("reference");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			String[] retVal =
				this.client.suggestMergeSources(reference.path, ConversionUtility.convert(reference.pegRevision));

			this.fireSucceeded(ISVNCallListener.SUGGEST_MERGE_SOURCES, parameters, retVal);

			return retVal;
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.SUGGEST_MERGE_SOURCES, parameters);
		} catch (SubversionException ex) {
			this.handleSubversionException(ex, ISVNCallListener.SUGGEST_MERGE_SOURCES, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
		// unreachable code
		return null;
	}

	@Override
	public void resolve(String path, int conflictResult, int depth, ISVNProgressMonitor monitor)
		throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("path", path);
		parameters.put("conflictResult", Integer.valueOf(conflictResult));
		parameters.put("depth", Integer.valueOf(depth));
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.RESOLVE, parameters);
		path = (String) parameters.get("path");
		conflictResult = ((Integer) parameters.get("conflictResult")).intValue();
		depth = ((Integer) parameters.get("depth")).intValue();
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			this.client.resolve(path, depth, conflictResult);

			this.fireSucceeded(ISVNCallListener.RESOLVE, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.RESOLVE, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public void setConflictResolver(ISVNConflictResolutionCallback listener) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("listener", listener);
		this.fireAsked(ISVNCallListener.SET_CONFLICT_RESOLVER, parameters);
		listener = (ISVNConflictResolutionCallback) parameters.get("listener");

		this.client.setConflictResolver(ConversionUtility.convert(listener));

		this.fireSucceeded(ISVNCallListener.SET_CONFLICT_RESOLVER, parameters, null);
	}

	@Override
	public void addToChangeList(String[] paths, String changelist, int depth, String[] changelistNames,
		ISVNProgressMonitor monitor) throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("paths", paths);
		parameters.put("changelist", changelist);
		parameters.put("depth", Integer.valueOf(depth));
		parameters.put("changelistNames", changelistNames);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.ADD_TO_CHANGE_LIST, parameters);
		paths = (String[]) parameters.get("paths");
		changelist = (String) parameters.get("changelist");
		depth = ((Integer) parameters.get("depth")).intValue();
		changelistNames = (String[]) parameters.get("changelistNames");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			this.client.addToChangelist(paths, changelist, depth, changelistNames);

			this.fireSucceeded(ISVNCallListener.ADD_TO_CHANGE_LIST, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.ADD_TO_CHANGE_LIST, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public void removeFromChangeLists(String[] paths, int depth, String[] changelistNames, ISVNProgressMonitor monitor)
		throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("paths", paths);
		parameters.put("depth", Integer.valueOf(depth));
		parameters.put("changelistNames", changelistNames);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.REMOVE_FROM_CHANGE_LISTS, parameters);
		paths = (String[]) parameters.get("paths");
		depth = ((Integer) parameters.get("depth")).intValue();
		changelistNames = (String[]) parameters.get("changelistNames");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			this.client.removeFromChangelists(paths, depth, changelistNames);

			this.fireSucceeded(ISVNCallListener.REMOVE_FROM_CHANGE_LISTS, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.REMOVE_FROM_CHANGE_LISTS, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public void dumpChangeLists(String[] changeLists, String rootPath, int depth, final ISVNChangeListCallback cb,
		ISVNProgressMonitor monitor) throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("changeLists", changeLists);
		parameters.put("rootPath", rootPath);
		parameters.put("depth", Integer.valueOf(depth));
		parameters.put("cb", cb);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.DUMP_CHANGE_LISTS, parameters);
		changeLists = (String[]) parameters.get("changeLists");
		rootPath = (String) parameters.get("rootPath");
		depth = ((Integer) parameters.get("depth")).intValue();
		final ISVNChangeListCallback cb1 = (ISVNChangeListCallback) parameters.get("cb");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			this.client.getChangelists(rootPath, changeLists, depth, new ChangelistCallback() {

				@Override
				public void doChangelist(final String path, final String changelist) {
					cb1.next(path, changelist);
				}
			});

			this.fireSucceeded(ISVNCallListener.DUMP_CHANGE_LISTS, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.DUMP_CHANGE_LISTS, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public void merge(final SVNEntryReference reference, final SVNRevisionRange[] revisions, final String mergePath,
		final SVNMergeStatus[] mergeStatus, final long options, final ISVNProgressMonitor monitor)
		throws SVNConnectorException {
		this.merge(reference, null, revisions, mergePath, mergeStatus, options, monitor);
	}

	@Override
	public void merge(final SVNEntryRevisionReference reference1, final SVNEntryRevisionReference reference2,
		final String mergePath, final SVNMergeStatus[] mergeStatus, final long options,
		final ISVNProgressMonitor monitor) throws SVNConnectorException {
		this.merge(reference1, reference2, null, mergePath, mergeStatus, options, monitor);
	}

	@Override
	public void mergeStatus(final SVNEntryReference reference, final SVNRevisionRange[] revisions, final String path,
		final int depth, final long options, final ISVNMergeStatusCallback cb, final ISVNProgressMonitor monitor)
		throws SVNConnectorException {
		this.mergeStatus(reference, null, revisions, path, depth, options, cb, monitor);
	}

	@Override
	public void mergeStatus(final SVNEntryRevisionReference reference1, final SVNEntryRevisionReference reference2,
		final String path, final int depth, final long options, final ISVNMergeStatusCallback cb,
		final ISVNProgressMonitor monitor) throws SVNConnectorException {
		this.mergeStatus(reference1, reference2, null, path, depth, options, cb, monitor);
	}

	@Override
	public void merge(final SVNEntryReference reference, final String mergePath, final SVNMergeStatus[] mergeStatus,
		final long options, final ISVNProgressMonitor monitor) throws SVNConnectorException {
		this.merge(reference, null, null, mergePath, mergeStatus, options, monitor);
	}

	@Override
	public void mergeStatus(final SVNEntryReference reference, final String mergePath, final long options,
		final ISVNMergeStatusCallback cb, final ISVNProgressMonitor monitor) throws SVNConnectorException {
		this.mergeStatus(reference, null, null, mergePath, ISVNConnector.Depth.INFINITY, options, cb, monitor);
	}

	protected void merge(final SVNEntryReference reference1, final SVNEntryRevisionReference reference2,
		final SVNRevisionRange[] revisions, final String mergePath, final SVNMergeStatus[] mergeStatus,
		final long options, final ISVNProgressMonitor monitor) throws SVNConnectorException {
		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			for ( int i = 0; i < mergeStatus.length && !monitor.isActivityCancelled(); i++ ) {
				// TODO recordOnly does not work for deletions and additions. Is it right?
				File localFile = new File(mergeStatus[i].path);
				if ( mergeStatus[i].textStatus == org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.DELETED ) {
					this.client.remove(new String[] { mergeStatus[i].path }, null, (options & Options.FORCE) != 0,
						false, null);
				} else if ( mergeStatus[i].textStatus == org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.ADDED ||
					mergeStatus[i].textStatus == org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.MODIFIED &&
					((options & ISVNConnector.Options.FORCE) != 0 || !localFile.exists()) ||
					mergeStatus[i].textStatus == org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.CONFLICTED &&
					(options & ISVNConnector.Options.FORCE) != 0 ) {
					if ( localFile.exists() ) {
						localFile.delete();// do not perform node replacement, just replace node content
					}
					for ( File parent = localFile.getParentFile(); !parent.exists() && parent.getParentFile() != null; ) {
						File tmp = parent.getParentFile();
						if ( tmp.exists() ) {
							localFile.getParentFile().mkdirs();
							this.client.add(parent.getAbsolutePath(), Depth.INFINITY, true, false, false);
							break;
						}
						parent = tmp;
					}
					CopySource src =
						new CopySource(mergeStatus[i].endUrl, ConversionUtility.convert(reference2 == null
							? revisions != null ? revisions[revisions.length - 1].to : reference1.pegRevision
							: reference2.revision), ConversionUtility.convert(reference2 == null
							? reference1.pegRevision : reference2.pegRevision));
					File tmp = File.createTempFile("mergeCopyTarget", ".tmp", localFile.getParentFile());
					tmp.delete();
					this.client.copy(new CopySource[] { src }, tmp.getAbsolutePath(), null, true, false, null);
					PropertyData[] data = this.client.properties(tmp.getAbsolutePath());
					this.client.revert(tmp.getAbsolutePath(), Depth.INFINITY, null);
					tmp.renameTo(new File(mergeStatus[i].path));
					try {
						this.client.add(mergeStatus[i].path, Depth.INFINITY, true, false, false);
					} catch (ClientException ex) {
						// could be thrown if node already added
					}
					if ( data != null ) {
						for ( PropertyData d : data ) {
							if ( d.getValue() != null ) {
								this.client.propertySet(mergeStatus[i].path, d.getName(), d.getValue(), false);
							} else {
								this.client.propertySet(mergeStatus[i].path, d.getName(), d.getData(), false);
							}
						}
					}
				} else if ( reference2 != null ) {
					this.client.merge(mergeStatus[i].startUrl,
						ConversionUtility.convert(((SVNEntryRevisionReference) reference1).revision),
						mergeStatus[i].endUrl, ConversionUtility.convert(reference2.revision), mergeStatus[i].path,
						(options & Options.FORCE) != 0, Depth.FILES, (options & Options.IGNORE_ANCESTRY) != 0, false,
						(options & Options.RECORD_ONLY) != 0);
				} else if ( revisions != null ) {
					this.client.merge(mergeStatus[i].endUrl, ConversionUtility.convert(reference1.pegRevision),
						ConversionUtility.convert(revisions), mergeStatus[i].path, (options & Options.FORCE) != 0,
						Depth.FILES, (options & Options.IGNORE_ANCESTRY) != 0, false,
						(options & Options.RECORD_ONLY) != 0);
				} else {
					this.client.mergeReintegrate(mergeStatus[i].endUrl,
						ConversionUtility.convert(reference1.pegRevision), mergeStatus[i].path, false);
				}
			}
		} catch (ClientException ex) {
			this.handleClientException(ex, null, null);
		} catch (IOException e) {
			throw new SVNConnectorException(e);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	protected void mergeStatus(final SVNEntryReference reference1, final SVNEntryRevisionReference reference2,
		final SVNRevisionRange[] revisions, final String path, final int depth, final long options,
		final ISVNMergeStatusCallback cb, final ISVNProgressMonitor monitor) throws SVNConnectorException {
		final ArrayList<SVNNotification> tmp = new ArrayList<SVNNotification>();
		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor) {

			@Override
			public void notify(final SVNNotification arg0) {
				super.notify(arg0);
				tmp.add(arg0);
			}
		};
		try {
			this.composite.add(wrapper);
			wrapper.start();

			if ( reference2 != null ) {
				this.client.merge(reference1.path,
					ConversionUtility.convert(((SVNEntryRevisionReference) reference1).revision), reference2.path,
					ConversionUtility.convert(reference2.revision), path, (options & Options.FORCE) != 0, depth,
					(options & Options.IGNORE_ANCESTRY) != 0, (options & Options.SIMULATE) != 0,
					(options & Options.RECORD_ONLY) != 0);
			} else if ( revisions != null ) {
				this.client.merge(reference1.path, ConversionUtility.convert(reference1.pegRevision),
					ConversionUtility.convert(revisions), path, (options & Options.FORCE) != 0, depth,
					(options & Options.IGNORE_ANCESTRY) != 0, (options & Options.SIMULATE) != 0,
					(options & Options.RECORD_ONLY) != 0);
			} else {
				this.client.mergeReintegrate(reference1.path, ConversionUtility.convert(reference1.pegRevision), path,
					(options & Options.SIMULATE) != 0);
			}

			SVNRevision from =
				reference2 == null ? revisions != null ? revisions[0].from : SVNRevision.fromNumber(1)
					: ((SVNEntryRevisionReference) reference1).revision;
			SVNRevision to =
				reference2 == null ? revisions != null ? revisions[revisions.length - 1].to : reference1.pegRevision
					: reference2.revision;
			if ( from.getKind() != SVNRevision.Kind.NUMBER ) {
				SVNLogEntry[] entries =
					SVNUtility.logEntries(this, reference1, from, SVNRevision.fromNumber(1),
						ISVNConnector.Options.NONE, ISVNConnector.EMPTY_LOG_ENTRY_PROPS, 1, monitor);
				from = SVNRevision.fromNumber(entries[0].revision);
			}
			if ( to.getKind() != SVNRevision.Kind.NUMBER ) {
				SVNLogEntry[] entries =
					SVNUtility.logEntries(this, reference2 == null ? reference1 : reference2, to,
						SVNRevision.fromNumber(1), ISVNConnector.Options.NONE, ISVNConnector.EMPTY_LOG_ENTRY_PROPS, 1,
						monitor);
				to = SVNRevision.fromNumber(entries[0].revision);
			}
			// tag creation revision greater than last changed revision of CopiedFromURL
			if ( reference2 != null ) {
				if ( from.equals(to) ) {
					from = SVNRevision.fromNumber(((SVNRevision.Number) to).getNumber() - 1);
				}
			}
			boolean reversed =
				reference2 == null ? SVNUtility.compareRevisions(from, to, new SVNEntryRevisionReference(
					reference1.path, reference1.pegRevision, from), new SVNEntryRevisionReference(reference1.path,
					reference1.pegRevision, to), this) == 1 : SVNUtility.compareRevisions(from, to,
					(SVNEntryRevisionReference) reference1, reference2, this) == 1;

			int i = 0;
			String startUrlPref = reference1.path;
			String endUrlPref = reference2 == null ? reference1.path : reference2.path;
			SVNLogEntry[] allMsgs =
				reversed ? SVNUtility.logEntries(this,
					reference2 == null ? reference1 : this.getValidReference(reference2, from, monitor), from, to,
					ISVNConnector.Options.DISCOVER_PATHS, ISVNConnector.DEFAULT_LOG_ENTRY_PROPS, 0, monitor)
					: SVNUtility.logEntries(this, reference2 == null ? reference1 : reference2, to, from,
						ISVNConnector.Options.DISCOVER_PATHS, ISVNConnector.DEFAULT_LOG_ENTRY_PROPS, 0, monitor);
			long minRev = ((SVNRevision.Number) (reversed ? to : from)).getNumber();
			for ( Iterator<SVNNotification> it = tmp.iterator(); it.hasNext() && !monitor.isActivityCancelled(); i++ ) {
				SVNNotification state = it.next();
				int kind = state.kind;

				String tPath = state.path.substring(path.length());
				String startUrl = SVNUtility.normalizeURL(startUrlPref + tPath);
				String endUrl = SVNUtility.normalizeURL(endUrlPref + tPath);
				boolean skipped =
					state.action == org.eclipse.team.svn.core.connector.SVNNotification.PerformedAction.SKIP;

				int cState = org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.NONE;
				int pState = org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.NONE;

				boolean hasTreeConflict =
					state.action == org.eclipse.team.svn.core.connector.SVNNotification.PerformedAction.TREE_CONFLICT;
				SVNConflictDescriptor treeConflict = null;
				if ( hasTreeConflict ) {
					SVNEntryInfo[] infos =
						SVNUtility.info(this, new SVNEntryRevisionReference(state.path), ISVNConnector.Depth.EMPTY,
							monitor);
					if ( infos.length > 0 ) {
						treeConflict = infos[0].treeConflicts[0];
						kind = infos[0].kind;

						if ( treeConflict.conflictKind == SVNConflictDescriptor.Kind.CONTENT ) {
							cState = org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.CONFLICTED;
						} else {
							pState = org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.CONFLICTED;
						}
					}
				}

				if ( state.action == org.eclipse.team.svn.core.connector.SVNNotification.PerformedAction.UPDATE_ADD ) {
					cState = org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.ADDED;
				} else if ( state.action == org.eclipse.team.svn.core.connector.SVNNotification.PerformedAction.UPDATE_DELETE ) {
					cState = org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.DELETED;
				} else if ( state.action == org.eclipse.team.svn.core.connector.SVNNotification.PerformedAction.UPDATE_UPDATE ) {
					pState =
						state.propState == org.eclipse.team.svn.core.connector.SVNNotification.NodeStatus.CHANGED
							? org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.MODIFIED
							: state.propState == org.eclipse.team.svn.core.connector.SVNNotification.NodeStatus.CONFLICTED
								? org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.CONFLICTED
								: org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.NONE;
					cState =
						state.contentState == org.eclipse.team.svn.core.connector.SVNNotification.NodeStatus.CHANGED ||
							state.contentState == org.eclipse.team.svn.core.connector.SVNNotification.NodeStatus.MERGED
							? org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.MODIFIED
							: state.contentState == org.eclipse.team.svn.core.connector.SVNNotification.NodeStatus.CONFLICTED
								? org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.CONFLICTED
								: org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.NONE;
				} else if ( state.action == org.eclipse.team.svn.core.connector.SVNNotification.PerformedAction.SKIP ) {
					if ( state.contentState == org.eclipse.team.svn.core.connector.SVNNotification.NodeStatus.MISSING ) {
						try {
							SVNRevision pegRev = reference1.pegRevision;
							if ( reference2 != null ) {
								pegRev = reference2.pegRevision;
							}
							SVNUtility.info(this, new SVNEntryRevisionReference(endUrl, pegRev, to),
								ISVNConnector.Depth.EMPTY, monitor);
							pState = org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.MODIFIED;
							cState = org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.MODIFIED;
						} catch (Exception ex) {
							cState = org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.DELETED;
						}
					} else if ( state.contentState == org.eclipse.team.svn.core.connector.SVNNotification.NodeStatus.OBSTRUCTED ) {
						cState = org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.ADDED;
					}
				}

				if ( cState != org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.NONE ||
					pState != org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.NONE || hasTreeConflict ) {
					long startRevision = SVNRevision.INVALID_REVISION_NUMBER;
					long endRevision = SVNRevision.INVALID_REVISION_NUMBER;
					long date = 0;
					String author = null;
					String message = null;
					monitor.progress(i, tmp.size(), SVNKitConnector.makeItemState(state));
					if ( cState == org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.ADDED &&
						!reversed ||
						cState == org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.DELETED &&
						reversed ||
						hasTreeConflict &&
						(treeConflict.action == Action.ADD && !reversed || treeConflict.action == Action.DELETE &&
							reversed) ) {
						int idx = this.getLogIndex(allMsgs, endUrl, false);
						if ( idx != -1 ) {
							endRevision = allMsgs[idx].revision;
							date = allMsgs[idx].date;
							author = allMsgs[idx].author;
							message = allMsgs[idx].message;
						}
					} else if ( cState == org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.MODIFIED ||
						cState == org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.CONFLICTED ||
						pState == org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.MODIFIED ||
						pState == org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.CONFLICTED ||
						hasTreeConflict && treeConflict.action == Action.MODIFY ) {
						int idx = this.getLogIndex(allMsgs, endUrl, false);
						if ( idx != -1 ) {
							endRevision = allMsgs[idx].revision;
							date = allMsgs[idx].date;
							author = allMsgs[idx].author;
							message = allMsgs[idx].message;
						}
						idx = this.getLogIndex(allMsgs, startUrl, true);
						startRevision = idx != -1 ? Math.max(allMsgs[idx].revision, minRev) : minRev;
					} else {
						int idx = this.getLogIndex(allMsgs, endUrl, false);
						if ( idx != -1 ) {
							endRevision = allMsgs[idx].revision;
							date = allMsgs[idx].date;
							author = allMsgs[idx].author;
							message = allMsgs[idx].message;
						} else {
							idx = this.getLogIndex(allMsgs, startUrl, false);
							if ( idx != -1 ) {
								endUrl = startUrl;
								endRevision = allMsgs[idx].revision;
								date = allMsgs[idx].date;
								author = allMsgs[idx].author;
								message = allMsgs[idx].message;
							}
						}
						idx = this.getLogIndex(allMsgs, startUrl, true);
						startRevision = idx != -1 ? Math.max(allMsgs[idx].revision, minRev) : minRev;
					}
					if ( reversed ) {
						startRevision = endRevision;
						endRevision = allMsgs[allMsgs.length - 1].revision;
						date = allMsgs[allMsgs.length - 1].date;
						author = allMsgs[allMsgs.length - 1].author;
						message = allMsgs[allMsgs.length - 1].message;
					}
					cb.next(new SVNMergeStatus(startUrl, endUrl, state.path, kind, cState, pState, startRevision,
						endRevision, date, author, message, skipped, hasTreeConflict, treeConflict));
				}
			}
		} catch (ClientException ex) {
			this.handleClientException(ex, null, null);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	protected SVNEntryReference getValidReference(SVNEntryReference referenceToExisting,
		final SVNRevision lastRevision, final ISVNProgressMonitor monitor) throws SVNConnectorException {
		if ( referenceToExisting.pegRevision.getKind() != SVNRevision.Kind.HEAD &&
			referenceToExisting.pegRevision.getKind() != SVNRevision.Kind.NUMBER ) { throw new RuntimeException(
			"Unexpected revision kind. Kind: " + referenceToExisting.pegRevision.getKind()); }
		if ( lastRevision.getKind() != SVNRevision.Kind.NUMBER ) { throw new RuntimeException(
			"Unexpected last revision kind. Kind: " + lastRevision.getKind()); }

		if ( referenceToExisting.pegRevision.getKind() == SVNRevision.Kind.HEAD ) { return referenceToExisting; }

		long start = ((SVNRevision.Number) referenceToExisting.pegRevision).getNumber();
		long end = ((SVNRevision.Number) lastRevision).getNumber();
		while (end > start) {
			referenceToExisting = this.getLastValidReference(referenceToExisting, lastRevision, monitor);
			if ( !referenceToExisting.pegRevision.equals(lastRevision) ) {
				start = ((SVNRevision.Number) referenceToExisting.pegRevision).getNumber() + 1;
				SVNEntryReference tRef = new SVNEntryReference(referenceToExisting.path, SVNRevision.fromNumber(start));
				while (!this.exists(tRef, monitor)) {
					tRef = new SVNEntryReference(tRef.path.substring(0, tRef.path.lastIndexOf("/")), tRef.pegRevision);
				}
				SVNLogEntry[] log =
					SVNUtility.logEntries(this, tRef, tRef.pegRevision, referenceToExisting.pegRevision,
						ISVNConnector.Options.DISCOVER_PATHS, ISVNConnector.DEFAULT_LOG_ENTRY_PROPS, 0, monitor);
				SVNLogPath[] paths = log[0].changedPaths;
				boolean renamed = false;
				if ( paths != null ) {
					String decodedUrl = SVNUtility.decodeURL(referenceToExisting.path);
					for ( int k = 0; k < paths.length; k++ ) {
						if ( paths[k].copiedFromPath != null ) {
							int idx = decodedUrl.indexOf(paths[k].copiedFromPath);
							if ( idx != -1 &&
								(decodedUrl.charAt(idx + paths[k].copiedFromPath.length()) == '/' || decodedUrl
									.endsWith(paths[k].copiedFromPath)) ) {
								decodedUrl =
									decodedUrl.substring(0, idx) + paths[k].path +
										decodedUrl.substring(idx + paths[k].copiedFromPath.length());
								tRef = new SVNEntryReference(SVNUtility.encodeURL(decodedUrl), tRef.pegRevision);
								renamed = true;
								break;
							}
						}
					}
				}
				referenceToExisting = tRef;
				if ( !renamed ) { return referenceToExisting; }
			}
		}
		return referenceToExisting;
	}

	protected SVNEntryReference getLastValidReference(SVNEntryReference referenceToExisting,
		final SVNRevision lastRevision, final ISVNProgressMonitor monitor) {
		long start = ((SVNRevision.Number) referenceToExisting.pegRevision).getNumber();
		long end = ((SVNRevision.Number) lastRevision).getNumber();
		do {
			long middle = end - (end - start) / 2; // long is largest type and (end + start) could out of type ranges
			SVNEntryReference tRef = new SVNEntryReference(referenceToExisting.path, SVNRevision.fromNumber(middle));
			if ( this.exists(tRef, monitor) ) {
				start = middle;
				referenceToExisting = tRef;
			} else {
				if ( end - start == 1 ) {
					break;
				}
				end = middle;
			}
		} while (end > start);
		return referenceToExisting;
	}

	protected boolean exists(final SVNEntryReference reference, final ISVNProgressMonitor monitor) {
		try {
			SVNUtility.logEntries(this, reference, reference.pegRevision, reference.pegRevision,
				ISVNConnector.Options.NONE, ISVNConnector.EMPTY_LOG_ENTRY_PROPS, 1, monitor);
			return true;
		} catch (SVNConnectorException e) {
			return false;
		}
	}

	protected int getLogIndex(final SVNLogEntry[] msgs, final String url, final boolean last) {
		String decodedUrl = SVNUtility.decodeURL(url);
		int retVal = -1;
		for ( int j = 0; j < msgs.length; j++ ) {
			SVNLogPath[] paths = msgs[j].changedPaths;
			if ( paths != null ) {
				int maxPathIdx = -1, maxPathLen = 0;
				for ( int k = 0; k < paths.length; k++ ) {
					if ( paths[k] != null && decodedUrl.endsWith(paths[k].path) ) {
						if ( last ) {
							if ( paths[k].copiedFromPath != null ) {
								maxPathIdx = k;
								maxPathLen = paths[k].path.length();
							}
							retVal = paths[k].action == SVNLogPath.ChangeType.ADDED ? j : -1;
						} else {
							return j;
						}
					} else if ( paths[k].copiedFromPath != null ) {
						int idx = decodedUrl.indexOf(paths[k].path);
						if ( idx != -1 &&
							(decodedUrl.charAt(idx + paths[k].path.length()) == '/' || decodedUrl
								.endsWith(paths[k].path)) && paths[k].path.length() > maxPathLen ) {
							maxPathIdx = k;
							maxPathLen = paths[k].path.length();
						}
					}
				}
				if ( maxPathIdx != -1 ) {
					int idx = decodedUrl.indexOf(paths[maxPathIdx].path);
					decodedUrl =
						decodedUrl.substring(0, idx) + paths[maxPathIdx].copiedFromPath +
							decodedUrl.substring(idx + paths[maxPathIdx].path.length());
				}
			}
		}
		return retVal;
	}

	@Override
	public void doImport(String path, String url, String message, int depth, long options, Map revProps,
		ISVNProgressMonitor monitor) throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("path", path);
		parameters.put("url", url);
		parameters.put("message", message);
		parameters.put("depth", Integer.valueOf(depth));
		parameters.put("options", Long.valueOf(options));
		parameters.put("revProps", revProps);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.DO_IMPORT, parameters);
		path = (String) parameters.get("path");
		url = (String) parameters.get("url");
		message = (String) parameters.get("message");
		depth = ((Integer) parameters.get("depth")).intValue();
		options = ((Long) parameters.get("options")).longValue();
		revProps = (Map) parameters.get("revProps");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			this.client.doImport(path, url, message, depth, (options & Options.INCLUDE_IGNORED) != 0,
				(options & Options.IGNORE_UNKNOWN_NODE_TYPES) != 0, ConversionUtility.convert(revProps));

			this.fireSucceeded(ISVNCallListener.DO_IMPORT, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.DO_IMPORT, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public long doExport(SVNEntryRevisionReference fromReference, String destPath, String nativeEOL, int depth,
		long options, ISVNProgressMonitor monitor) throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("fromReference", fromReference);
		parameters.put("destPath", destPath);
		parameters.put("nativeEOL", nativeEOL);
		parameters.put("depth", Integer.valueOf(depth));
		parameters.put("options", Long.valueOf(options));
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.DO_EXPORT, parameters);
		fromReference = (SVNEntryRevisionReference) parameters.get("fromReference");
		destPath = (String) parameters.get("destPath");
		nativeEOL = (String) parameters.get("nativeEOL");
		depth = ((Integer) parameters.get("depth")).intValue();
		options = ((Long) parameters.get("options")).longValue();
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			long retVal =
				this.client.doExport(fromReference.path, destPath, ConversionUtility.convert(fromReference.revision),
					ConversionUtility.convert(fromReference.pegRevision), (options & Options.FORCE) != 0,
					(options & Options.IGNORE_EXTERNALS) != 0, depth, nativeEOL);

			this.fireSucceeded(ISVNCallListener.DO_EXPORT, parameters, Long.valueOf(retVal));

			return retVal;
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.DO_EXPORT, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
		// unreachable code
		return 0;
	}

	@Override
	public void diff(SVNEntryRevisionReference reference1, SVNEntryRevisionReference reference2, String relativeToDir,
		String outFileName, int depth, long options, String[] changelistNames, ISVNProgressMonitor monitor)
		throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("reference1", reference1);
		parameters.put("reference2", reference2);
		parameters.put("relativeToDir", relativeToDir);
		parameters.put("outFileName", outFileName);
		parameters.put("depth", Integer.valueOf(depth));
		parameters.put("options", Long.valueOf(options));
		parameters.put("changelistNames", changelistNames);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.DIFF_TWO_URLS, parameters);
		reference1 = (SVNEntryRevisionReference) parameters.get("reference1");
		reference2 = (SVNEntryRevisionReference) parameters.get("reference2");
		relativeToDir = (String) parameters.get("relativeToDir");
		outFileName = (String) parameters.get("outFileName");
		depth = ((Integer) parameters.get("depth")).intValue();
		options = ((Long) parameters.get("options")).longValue();
		changelistNames = (String[]) parameters.get("changelistNames");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			this.client.diff(reference1.path, ConversionUtility.convert(reference1.revision), reference2.path,
				ConversionUtility.convert(reference2.revision), relativeToDir, outFileName, depth, changelistNames,
				(options & Options.IGNORE_ANCESTRY) != 0, (options & Options.SKIP_DELETED) != 0,
				(options & Options.FORCE) != 0);

			this.fireSucceeded(ISVNCallListener.DIFF_TWO_URLS, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.DIFF_TWO_URLS, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public void diff(SVNEntryReference reference, SVNRevision revision1, SVNRevision revision2, String relativeToDir,
		String outFileName, int depth, long options, String[] changelistNames, ISVNProgressMonitor monitor)
		throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("reference", reference);
		parameters.put("revision1", revision1);
		parameters.put("revision2", revision2);
		parameters.put("relativeToDir", relativeToDir);
		parameters.put("outFileName", outFileName);
		parameters.put("depth", Integer.valueOf(depth));
		parameters.put("options", Long.valueOf(options));
		parameters.put("changelistNames", changelistNames);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.DIFF, parameters);
		reference = (SVNEntryReference) parameters.get("reference");
		revision1 = (SVNRevision) parameters.get("revision1");
		revision2 = (SVNRevision) parameters.get("revision2");
		relativeToDir = (String) parameters.get("relativeToDir");
		outFileName = (String) parameters.get("outFileName");
		depth = ((Integer) parameters.get("depth")).intValue();
		options = ((Long) parameters.get("options")).longValue();
		changelistNames = (String[]) parameters.get("changelistNames");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			this.client.diff(reference.path, ConversionUtility.convert(reference.pegRevision),
				ConversionUtility.convert(revision1), ConversionUtility.convert(revision2), relativeToDir, outFileName,
				depth, changelistNames, (options & Options.IGNORE_ANCESTRY) != 0,
				(options & Options.SKIP_DELETED) != 0, (options & Options.FORCE) != 0);

			this.fireSucceeded(ISVNCallListener.DIFF, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.DIFF, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public void diffStatus(SVNEntryRevisionReference reference1, SVNEntryRevisionReference reference2, int depth,
		long options, String[] changelistNames, ISVNDiffStatusCallback cb, ISVNProgressMonitor monitor)
		throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("reference1", reference1);
		parameters.put("reference2", reference2);
		parameters.put("depth", Integer.valueOf(depth));
		parameters.put("options", Long.valueOf(options));
		parameters.put("changelistNames", changelistNames);
		parameters.put("cb", cb);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.DIFF_STATUS_TWO_URLS, parameters);
		reference1 = (SVNEntryRevisionReference) parameters.get("reference1");
		reference2 = (SVNEntryRevisionReference) parameters.get("reference2");
		depth = ((Integer) parameters.get("depth")).intValue();
		options = ((Long) parameters.get("options")).longValue();
		changelistNames = (String[]) parameters.get("changelistNames");
		cb = (ISVNDiffStatusCallback) parameters.get("cb");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		SVNEntryInfo[] infos = SVNUtility.info(this, reference1, Depth.EMPTY, monitor);
		boolean isFile = infos.length > 0 && infos[0] != null && infos[0].kind == SVNEntry.Kind.FILE;
		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();

			DiffCallback callback = new DiffCallback(reference1.path, reference2.path, isFile, cb);
			this.client.diffSummarize(reference1.path, ConversionUtility.convert(reference1.revision), reference2.path,
				ConversionUtility.convert(reference2.revision), depth, changelistNames,
				(options & Options.IGNORE_ANCESTRY) != 0, callback);
			callback.doLastDiff();

			this.fireSucceeded(ISVNCallListener.DIFF_STATUS_TWO_URLS, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.DIFF_STATUS_TWO_URLS, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public void diffStatus(SVNEntryReference reference, SVNRevision revision1, SVNRevision revision2, int depth,
		long options, String[] changelistNames, ISVNDiffStatusCallback cb, ISVNProgressMonitor monitor)
		throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("reference", reference);
		parameters.put("revision1", revision1);
		parameters.put("revision2", revision2);
		parameters.put("depth", Integer.valueOf(depth));
		parameters.put("options", Long.valueOf(options));
		parameters.put("changelistNames", changelistNames);
		parameters.put("cb", cb);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.DIFF_STATUS, parameters);
		reference = (SVNEntryReference) parameters.get("reference");
		revision1 = (SVNRevision) parameters.get("revision1");
		revision2 = (SVNRevision) parameters.get("revision2");
		depth = ((Integer) parameters.get("depth")).intValue();
		options = ((Long) parameters.get("options")).longValue();
		changelistNames = (String[]) parameters.get("changelistNames");
		cb = (ISVNDiffStatusCallback) parameters.get("cb");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		SVNEntryInfo[] infos =
			SVNUtility.info(this, new SVNEntryRevisionReference(reference, revision1), Depth.EMPTY, monitor);
		boolean isFile = infos.length > 0 && infos[0] != null && infos[0].kind == SVNEntry.Kind.FILE;
		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();

			DiffCallback callback = new DiffCallback(reference.path, reference.path, isFile, cb);
			this.client.diffSummarize(reference.path, ConversionUtility.convert(reference.pegRevision),
				ConversionUtility.convert(revision1), ConversionUtility.convert(revision2), depth, changelistNames,
				(options & Options.IGNORE_ANCESTRY) != 0, callback);
			callback.doLastDiff();

			this.fireSucceeded(ISVNCallListener.DIFF_STATUS, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.DIFF_STATUS, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public void info(SVNEntryRevisionReference reference, int depth, String[] changelistNames,
		ISVNEntryInfoCallback cb, ISVNProgressMonitor monitor) throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("reference", reference);
		parameters.put("depth", Integer.valueOf(depth));
		parameters.put("changelistNames", changelistNames);
		parameters.put("cb", cb);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.INFO, parameters);
		reference = (SVNEntryRevisionReference) parameters.get("reference");
		depth = ((Integer) parameters.get("depth")).intValue();
		changelistNames = (String[]) parameters.get("changelistNames");
		cb = (ISVNEntryInfoCallback) parameters.get("cb");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			this.client
				.info2(reference.path, ConversionUtility.convert(reference.revision),
					ConversionUtility.convert(reference.pegRevision), depth, changelistNames,
					ConversionUtility.convert(cb));

			this.fireSucceeded(ISVNCallListener.INFO, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.INFO, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public void streamFileContent(SVNEntryRevisionReference reference, int bufferSize, OutputStream stream,
		ISVNProgressMonitor monitor) throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("reference", reference);
		parameters.put("bufferSize", Integer.valueOf(bufferSize));
		parameters.put("stream", stream);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.STREAM_FILE_CONTENT, parameters);
		reference = (SVNEntryRevisionReference) parameters.get("reference");
		bufferSize = ((Integer) parameters.get("bufferSize")).intValue();
		stream = (OutputStream) parameters.get("stream");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			this.client.streamFileContent(reference.path, ConversionUtility.convert(reference.revision),
				ConversionUtility.convert(reference.pegRevision), bufferSize, stream);

			this.fireSucceeded(ISVNCallListener.STREAM_FILE_CONTENT, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.STREAM_FILE_CONTENT, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public void mkdir(String[] path, String message, long options, Map revProps, ISVNProgressMonitor monitor)
		throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("path", path);
		parameters.put("message", message);
		parameters.put("options", Long.valueOf(options));
		parameters.put("revProps", revProps);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.MKDIR, parameters);
		path = (String[]) parameters.get("path");
		message = (String) parameters.get("message");
		options = ((Long) parameters.get("options")).longValue();
		revProps = (Map) parameters.get("revProps");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			this.client.mkdir(path, message, (options & Options.INCLUDE_PARENTS) != 0,
				ConversionUtility.convert(revProps));

			this.fireSucceeded(ISVNCallListener.MKDIR, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.MKDIR, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public void move(String[] srcPaths, String dstPath, long options, ISVNProgressMonitor monitor)
		throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("srcPaths", srcPaths);
		parameters.put("dstPath", dstPath);
		parameters.put("options", Long.valueOf(options));
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.MOVE_LOCAL, parameters);
		srcPaths = (String[]) parameters.get("srcPaths");
		dstPath = (String) parameters.get("dstPath");
		options = ((Long) parameters.get("options")).longValue();
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			for ( int i = 0; i < srcPaths.length && !monitor.isActivityCancelled(); i++ ) {
				this.client.move(srcPaths[i], dstPath, (options & Options.FORCE) != 0);
			}

			this.fireSucceeded(ISVNCallListener.MOVE_LOCAL, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.MOVE_LOCAL, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public void move(SVNEntryReference[] srcPaths, String dstPath, String message, long options, Map revProps,
		ISVNProgressMonitor monitor) throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("srcPaths", srcPaths);
		parameters.put("dstPath", dstPath);
		parameters.put("message", message);
		parameters.put("options", Long.valueOf(options));
		parameters.put("revProps", revProps);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.MOVE, parameters);
		srcPaths = (SVNEntryReference[]) parameters.get("srcPaths");
		dstPath = (String) parameters.get("dstPath");
		message = (String) parameters.get("message");
		options = ((Long) parameters.get("options")).longValue();
		revProps = (Map) parameters.get("revProps");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			ArrayList<String> src = new ArrayList<String>();
			for ( SVNEntryReference current : srcPaths ) {
				src.add(current.path);
			}
			this.client.move(src.toArray(new String[0]), dstPath, message, (options & Options.FORCE) != 0,
				(options & Options.INTERPRET_AS_CHILD) != 0, (options & Options.INCLUDE_PARENTS) != 0,
				ConversionUtility.convert(revProps));

			this.fireSucceeded(ISVNCallListener.MOVE, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.MOVE, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public void copy(String[] srcPaths, String destPath, SVNRevision revision, ISVNProgressMonitor monitor)
		throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("srcPaths", srcPaths);
		parameters.put("destPath", destPath);
		parameters.put("revision", revision);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.COPY_LOCAL, parameters);
		srcPaths = (String[]) parameters.get("srcPaths");
		destPath = (String) parameters.get("destPath");
		revision = (SVNRevision) parameters.get("revision");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			CopySource[] srcs = new CopySource[srcPaths.length];
			for ( int i = 0; i < srcPaths.length; i++ ) {
				srcs[i] = new CopySource(srcPaths[i], ConversionUtility.convert(revision), null);
			}
			this.client.copy(srcs, destPath, null, true, false, null);

			this.fireSucceeded(ISVNCallListener.COPY_LOCAL, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.COPY_LOCAL, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public void copy(SVNEntryRevisionReference[] srcPaths, String destPath, String message, long options, Map revProps,
		ISVNProgressMonitor monitor) throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("srcPaths", srcPaths);
		parameters.put("destPath", destPath);
		parameters.put("message", message);
		parameters.put("options", Long.valueOf(options));
		parameters.put("revProps", revProps);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.COPY, parameters);
		srcPaths = (SVNEntryRevisionReference[]) parameters.get("srcPaths");
		destPath = (String) parameters.get("destPath");
		message = (String) parameters.get("message");
		options = ((Long) parameters.get("options")).longValue();
		revProps = (Map) parameters.get("revProps");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			this.client.copy(ConversionUtility.convert(srcPaths), destPath, message,
				(options & Options.INTERPRET_AS_CHILD) != 0, (options & Options.INCLUDE_PARENTS) != 0,
				ConversionUtility.convert(revProps));

			this.fireSucceeded(ISVNCallListener.COPY, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.COPY, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public void remove(String[] path, String message, long options, Map revProps, ISVNProgressMonitor monitor)
		throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("path", path);
		parameters.put("message", message);
		parameters.put("options", Long.valueOf(options));
		parameters.put("revProps", revProps);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.REMOVE, parameters);
		path = (String[]) parameters.get("path");
		message = (String) parameters.get("message");
		options = ((Long) parameters.get("options")).longValue();
		revProps = (Map) parameters.get("revProps");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			this.client.remove(path, message, (options & Options.FORCE) != 0, (options & Options.KEEP_LOCAL) != 0,
				ConversionUtility.convert(revProps));

			this.fireSucceeded(ISVNCallListener.REMOVE, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.REMOVE, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public void logEntries(SVNEntryReference reference, SVNRevisionRange[] revisionRanges, String[] revProps,
		long limit, long options, ISVNLogEntryCallback cb, ISVNProgressMonitor monitor) throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("reference", reference);
		parameters.put("revisionRanges", revisionRanges);
		parameters.put("revProps", revProps);
		parameters.put("limit", Long.valueOf(limit));
		parameters.put("options", Long.valueOf(options));
		parameters.put("cb", cb);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.LOG_ENTRIES, parameters);
		reference = (SVNEntryReference) parameters.get("reference");
		revisionRanges = (SVNRevisionRange[]) parameters.get("revisionRanges");
		revProps = (String[]) parameters.get("revProps");
		limit = ((Long) parameters.get("limit")).longValue();
		options = ((Long) parameters.get("options")).longValue();
		cb = (ISVNLogEntryCallback) parameters.get("cb");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			this.client.logMessages(reference.path, ConversionUtility.convert(reference.pegRevision),
				ConversionUtility.convert(revisionRanges), (options & Options.STOP_ON_COPY) != 0,
				(options & Options.DISCOVER_PATHS) != 0, (options & Options.INCLUDE_MERGED_REVISIONS) != 0, revProps,
				limit, ConversionUtility.convert(cb));

			this.fireSucceeded(ISVNCallListener.LOG_ENTRIES, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.LOG_ENTRIES, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public void annotate(SVNEntryReference reference, SVNRevision revisionStart, SVNRevision revisionEnd, long options,
		ISVNAnnotationCallback callback, ISVNProgressMonitor monitor) throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("reference", reference);
		parameters.put("revisionStart", revisionStart);
		parameters.put("revisionEnd", revisionEnd);
		parameters.put("options", Long.valueOf(options));
		parameters.put("callback", callback);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.ANNOTATE, parameters);
		reference = (SVNEntryReference) parameters.get("reference");
		revisionStart = (SVNRevision) parameters.get("revisionStart");
		revisionEnd = (SVNRevision) parameters.get("revisionEnd");
		options = ((Long) parameters.get("options")).longValue();
		callback = (ISVNAnnotationCallback) parameters.get("callback");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			this.client.blame(reference.path, ConversionUtility.convert(reference.pegRevision),
				ConversionUtility.convert(revisionStart), ConversionUtility.convert(revisionEnd),
				(options & Options.IGNORE_MIME_TYPE) != 0, (options & Options.INCLUDE_MERGED_REVISIONS) != 0,
				ConversionUtility.convert(callback));

			this.fireSucceeded(ISVNCallListener.ANNOTATE, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.ANNOTATE, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public void list(SVNEntryRevisionReference reference, int depth, int direntFields, long options,
		final ISVNEntryCallback cb, ISVNProgressMonitor monitor) throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("reference", reference);
		parameters.put("depth", Integer.valueOf(depth));
		parameters.put("direntFields", Integer.valueOf(direntFields));
		parameters.put("options", Long.valueOf(options));
		parameters.put("cb", cb);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.LIST, parameters);
		reference = (SVNEntryRevisionReference) parameters.get("reference");
		depth = ((Integer) parameters.get("depth")).intValue();
		direntFields = ((Integer) parameters.get("direntFields")).intValue();
		options = ((Long) parameters.get("options")).longValue();
		final ISVNEntryCallback cb1 = (ISVNEntryCallback) parameters.get("cb");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			this.client.list(reference.path, ConversionUtility.convert(reference.revision),
				ConversionUtility.convert(reference.pegRevision), depth, direntFields,
				(options & Options.FETCH_LOCKS) != 0, new ListCallback() {

					@Override
					public void doEntry(final org.tigris.subversion.javahl.DirEntry dirent,
						final org.tigris.subversion.javahl.Lock lock) {
						String path = dirent.getPath();
						if ( path != null && path.length() != 0 ||
							dirent.getNodeKind() == org.tigris.subversion.javahl.NodeKind.file ) {
							Date date = dirent.getLastChanged();
							cb1.next(new SVNEntry(path, dirent.getLastChangedRevisionNumber(), date == null ? 0 : date
								.getTime(), dirent.getLastAuthor(), dirent.getHasProps(), dirent.getNodeKind(), dirent
								.getSize(), ConversionUtility.convert(lock)));
						}
					}
				});

			this.fireSucceeded(ISVNCallListener.LIST, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.LIST, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public void getProperties(SVNEntryRevisionReference reference, int depth, String[] changelistNames,
		ISVNPropertyCallback callback, ISVNProgressMonitor monitor) throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("reference", reference);
		parameters.put("depth", Integer.valueOf(depth));
		parameters.put("changelistNames", changelistNames);
		parameters.put("callback", callback);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.GET_PROPERTIES, parameters);
		reference = (SVNEntryRevisionReference) parameters.get("reference");
		depth = ((Integer) parameters.get("depth")).intValue();
		changelistNames = (String[]) parameters.get("changelistNames");
		callback = (ISVNPropertyCallback) parameters.get("callback");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			this.client.properties(reference.path, ConversionUtility.convert(reference.revision),
				ConversionUtility.convert(reference.pegRevision), depth, changelistNames,
				ConversionUtility.convert(callback));

			this.fireSucceeded(ISVNCallListener.GET_PROPERTIES, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.GET_PROPERTIES, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public SVNProperty getProperty(SVNEntryRevisionReference reference, String name, ISVNProgressMonitor monitor)
		throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("reference", reference);
		parameters.put("name", name);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.GET_PROPERTY, parameters);
		reference = (SVNEntryRevisionReference) parameters.get("reference");
		name = (String) parameters.get("name");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			SVNProperty retVal =
				ConversionUtility.convert(this.client.propertyGet(reference.path, name,
					ConversionUtility.convert(reference.revision), ConversionUtility.convert(reference.pegRevision)));

			this.fireSucceeded(ISVNCallListener.GET_PROPERTY, parameters, retVal);

			return retVal;
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.GET_PROPERTY, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
		// unreachable code
		return null;
	}

	@Override
	public void removeProperty(String[] path, String name, int depth, final long options, String[] changelistNames,
		ISVNProgressMonitor monitor) throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("path", path);
		parameters.put("name", name);
		parameters.put("depth", Integer.valueOf(depth));
		parameters.put("changelistNames", changelistNames);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.REMOVE_PROPERTY, parameters);
		path = (String[]) parameters.get("path");
		name = (String) parameters.get("name");
		depth = ((Integer) parameters.get("depth")).intValue();
		changelistNames = (String[]) parameters.get("changelistNames");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();

			for ( int i = 0; i < path.length && !monitor.isActivityCancelled(); i++ ) {
				this.client.propertyRemove(path[i], name, depth, changelistNames);
			}

			this.fireSucceeded(ISVNCallListener.REMOVE_PROPERTY, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.REMOVE_PROPERTY, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public void setProperty(String[] path, SVNProperty property, int depth, long options, String[] changelistNames,
		ISVNProgressMonitor monitor) throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("path", path);
		parameters.put("property", property);
		parameters.put("depth", Integer.valueOf(depth));
		parameters.put("options", Long.valueOf(options));
		parameters.put("changelistNames", changelistNames);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.SET_PROPERTY, parameters);
		path = (String[]) parameters.get("path");
		property = (SVNProperty) parameters.get("property");
		depth = ((Integer) parameters.get("depth")).intValue();
		options = ((Long) parameters.get("options")).longValue();
		changelistNames = (String[]) parameters.get("changelistNames");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			for ( int i = 0; i < path.length && !monitor.isActivityCancelled(); i++ ) {
				this.client.propertySet(path[i], property.name, property.value, depth, changelistNames,
					(options & Options.FORCE) != 0, null);
			}

			this.fireSucceeded(ISVNCallListener.SET_PROPERTY, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.SET_PROPERTY, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public SVNProperty[] getRevisionProperties(SVNEntryReference reference, ISVNProgressMonitor monitor)
		throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("reference", reference);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.GET_REVISION_PROPERTIES, parameters);
		reference = (SVNEntryReference) parameters.get("reference");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();

			SVNProperty[] retVal =
				ConversionUtility.convert(this.client.revProperties(reference.path,
					ConversionUtility.convert(reference.pegRevision)));

			this.fireSucceeded(ISVNCallListener.GET_REVISION_PROPERTIES, parameters, retVal);

			return retVal;
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.GET_REVISION_PROPERTIES, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
		// unreachable code
		return null;
	}

	@Override
	public SVNProperty getRevisionProperty(SVNEntryReference reference, String name, ISVNProgressMonitor monitor)
		throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("reference", reference);
		parameters.put("name", name);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.GET_REVISION_PROPERTY, parameters);
		reference = (SVNEntryReference) parameters.get("reference");
		name = (String) parameters.get("name");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			SVNProperty retVal =
				ConversionUtility.convert(this.client.revProperty(reference.path, name,
					ConversionUtility.convert(reference.pegRevision)));

			this.fireSucceeded(ISVNCallListener.GET_REVISION_PROPERTY, parameters, retVal);

			return retVal;
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.GET_REVISION_PROPERTY, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
		// unreachable code
		return null;
	}

	@Override
	public void setRevisionProperty(SVNEntryReference reference, SVNProperty property, String originalValue,
		long options, ISVNProgressMonitor monitor) throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("reference", reference);
		parameters.put("property", property);
		parameters.put("originalValue", originalValue);
		parameters.put("options", Long.valueOf(options));
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.SET_REVISION_PROPERTY, parameters);
		reference = (SVNEntryReference) parameters.get("reference");
		property = (SVNProperty) parameters.get("property");
		originalValue = (String) parameters.get("originalValue");
		options = ((Long) parameters.get("options")).longValue();
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		try {
			this.composite.add(wrapper);
			wrapper.start();
			this.client.setRevProperty(reference.path, property.name, ConversionUtility.convert(reference.pegRevision),
				property.value, originalValue, (options & Options.FORCE) != 0);

			this.fireSucceeded(ISVNCallListener.SET_REVISION_PROPERTY, parameters, null);
		} catch (ClientException ex) {
			this.handleClientException(ex, ISVNCallListener.SET_REVISION_PROPERTY, parameters);
		} finally {
			wrapper.interrupt();
			this.composite.remove(wrapper);
		}
	}

	@Override
	public void createRepository(String repositoryPath, String repositoryType, ISVNProgressMonitor monitor)
		throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("repositoryPath", repositoryPath);
		parameters.put("repositoryType", repositoryType);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.CREATE_REPOSITORY, parameters);
		repositoryPath = (String) parameters.get("repositoryPath");
		repositoryType = (String) parameters.get("repositoryType");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		// ProgressMonitorWrapper wrapper = new ProgressMonitorWrapper(monitor);
		// try {
		// this.composite.add(wrapper);
		// wrapper.start();
		//
		// String fsType = repositoryType == null ? ISVNConnector.REPOSITORY_FSTYPE_FSFS : repositoryType;
		// this.svnAdmin.create(repositoryPath, false, false, null, fsType);
		//
		// this.fireSucceeded(ISVNCallListener.CREATE_REPOSITORY, parameters, null);
		// }
		// catch (ClientException ex) {
		// this.handleClientException(ex, ISVNCallListener.CREATE_REPOSITORY, parameters);
		// }
		// finally {
		// wrapper.interrupt();
		// this.composite.remove(wrapper);
		// }
	}

	@Override
	public void upgrade(String path, ISVNProgressMonitor monitor) throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("path", path);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.UPGRADE, parameters);
		path = (String) parameters.get("path");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		this.fireFailed(ISVNCallListener.UPGRADE, parameters, null);
	}

	@Override
	public void patch(String patchPath, String targetPath, int stripCount, long options, ISVNPatchCallback callback,
		ISVNProgressMonitor monitor) throws SVNConnectorException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("patchPath", patchPath);
		parameters.put("targetPath", targetPath);
		parameters.put("stripCount", Integer.valueOf(stripCount));
		parameters.put("options", Long.valueOf(options));
		parameters.put("callback", callback);
		parameters.put("monitor", monitor);
		this.fireAsked(ISVNCallListener.PATCH, parameters);
		patchPath = (String) parameters.get("patchPath");
		targetPath = (String) parameters.get("targetPath");
		stripCount = ((Integer) parameters.get("stripCount")).intValue();
		options = ((Long) parameters.get("options")).longValue();
		callback = (ISVNPatchCallback) parameters.get("callback");
		monitor = (ISVNProgressMonitor) parameters.get("monitor");

		this.fireFailed(ISVNCallListener.PATCH, parameters, null);
	}

	@Override
	public void dispose() {
		this.client.dispose();
		// this.svnAdmin.dispose();
	}

	protected void handleSubversionException(final SubversionException ex, final String methodName,
		final Map<String, Object> parameters) throws SVNConnectorException {
		SVNConnectorException exception = new SVNConnectorException(ex.getMessage(), ex);
		this.fireFailed(methodName, parameters, exception);
		throw exception;
	}

	protected void handleClientException(final ClientException ex, final String methodName,
		final Map<String, Object> parameters) throws SVNConnectorException {
		String msg = ex.getMessage();
		SVNConnectorException exception = null;
		if ( this.findConflict(ex) ) {
			exception = new SVNConnectorUnresolvedConflictException(msg, ex);
		}
		if ( this.findCancel(ex) ) {
			exception = new SVNConnectorCancelException(msg, ex);
		}
		if ( this.findAuthentication(ex) ) {
			exception = new SVNConnectorAuthenticationException(msg, ex);
		}
		if ( exception == null ) {
			exception = new SVNConnectorException(msg, ex.getAprError(), ex);
		}
		if ( methodName != null ) {
			this.fireFailed(methodName, parameters, exception);
		}
		throw exception;
	}

	protected boolean findAuthentication(final ClientException t) {
		return t.getAprError() == SVNErrorCodes.raNotAuthorized;
	}

	protected boolean findCancel(final ClientException t) {
		return t.getAprError() == SVNErrorCodes.cancelled;
	}

	protected boolean findConflict(final ClientException t) {
		return t.getAprError() == SVNErrorCodes.fsConflict || t.getAprError() == SVNErrorCodes.fsTxnOutOfDate;
	}

	private class DiffCallback implements DiffSummaryReceiver {

		private final String prev;
		private final String next;
		private final boolean isFile;
		private SVNDiffStatus savedDiff;
		private final ISVNDiffStatusCallback cb;

		public DiffCallback(final String prev, final String next, final boolean isFile, final ISVNDiffStatusCallback cb) {
			this.prev = SVNUtility.decodeURL(prev);
			this.next = SVNUtility.decodeURL(next);
			this.isFile = isFile;
			this.cb = cb;
		}

		@Override
		public void onSummary(final DiffSummary descriptor) {
			int changeType = org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.NORMAL;
			if ( descriptor.getDiffKind() == DiffSummary.DiffKind.ADDED ) {
				changeType = org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.ADDED;
			} else if ( descriptor.getDiffKind() == DiffSummary.DiffKind.DELETED ) {
				changeType = org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.DELETED;
			} else if ( descriptor.getDiffKind() == DiffSummary.DiffKind.MODIFIED ) {
				changeType = org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.MODIFIED;
			}
			int propChangeType =
				descriptor.propsChanged() ? org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.MODIFIED
					: org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.NORMAL;
			if ( changeType != org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.NORMAL ||
				propChangeType != org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.NORMAL ) {
				String tPath1 = descriptor.getPath();
				String tPath2 = tPath1;
				if ( tPath1.length() == 0 || this.isFile ) {
					tPath1 = this.prev;
					tPath2 = this.next;
				} else {
					tPath1 = this.prev + "/" + tPath1;
					tPath2 = this.next + "/" + tPath2;
				}
				SVNDiffStatus status =
					new SVNDiffStatus(SVNUtility.encodeURL(tPath1), SVNUtility.encodeURL(tPath2),
						descriptor.getNodeKind(), changeType, propChangeType);
				if ( this.savedDiff != null ) {
					if ( this.savedDiff.pathPrev.equals(status.pathPrev) &&
						this.savedDiff.pathNext.equals(status.pathNext) &&
						this.savedDiff.textStatus == org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.DELETED &&
						status.textStatus == org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.ADDED ) {
						this.savedDiff =
							new SVNDiffStatus(SVNUtility.encodeURL(tPath1), SVNUtility.encodeURL(tPath2),
								descriptor.getNodeKind(),
								org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.REPLACED,
								org.eclipse.team.svn.core.connector.SVNEntryStatus.Kind.NORMAL);
						status = null;
					}
					this.cb.next(this.savedDiff);
				}
				this.savedDiff = status;
			}
		}

		public void doLastDiff() {
			if ( this.savedDiff != null ) {
				this.cb.next(this.savedDiff);
			}
		}
	}

	protected class RepositoryInfoPrompt implements PromptUserPasswordSSL, PromptUserPasswordSSH,
		PromptUserPasswordProxy {

		protected ISVNCredentialsPrompt prompt;

		public RepositoryInfoPrompt(final ISVNCredentialsPrompt prompt) {
			this.prompt = prompt;
		}

		@Override
		public boolean prompt(final String realm, final String username, final boolean maySave) {
			return this.prompt(realm, username);
		}

		@Override
		public boolean prompt(final String realm, final String username) {
			return this.prompt.prompt(null, realm);
		}

		@Override
		public boolean promptSSL(final String realm, final boolean maySave) {
			return this.prompt.promptSSL(null, realm);
		}

		@Override
		public boolean promptSSH(final String realm, final String username, final int sshPort, final boolean maySave) {
			return this.prompt.promptSSH(null, realm);
		}

		@Override
		public int askTrustSSLServer(final String info, final boolean allowPermanently) {
			return this.prompt.askTrustSSLServer(null, info, allowPermanently);
		}

		@Override
		public String getUsername() {
			return this.prompt.getUsername();
		}

		@Override
		public String getPassword() {
			return this.prompt.getPassword();
		}

		@Override
		public String getSSHPrivateKeyPath() {
			return this.prompt.getSSHPrivateKeyPath();
		}

		@Override
		public String getSSHPrivateKeyPassphrase() {
			return this.prompt.getSSHPrivateKeyPassphrase();
		}

		@Override
		public int getSSHPort() {
			return this.prompt.getSSHPort();
		}

		@Override
		public String getSSLClientCertPath() {
			return this.prompt.getSSLClientCertPath();
		}

		@Override
		public String getSSLClientCertPassword() {
			return this.prompt.getSSLClientCertPassword();
		}

		@Override
		public boolean promptProxy(final String url, final boolean maySave) {
			return this.prompt.promptProxy(null);
		}

		@Override
		public String getProxyHost() {
			return this.prompt.getProxyHost();
		}

		@Override
		public int getProxyPort() {
			return this.prompt.getProxyPort();
		}

		@Override
		public String getProxyUserName() {
			return this.prompt.getProxyUserName();
		}

		@Override
		public String getProxyPassword() {
			return this.prompt.getProxyPassword();
		}

		@Override
		public boolean askYesNo(final String realm, final String question, final boolean yesIsDefault) {
			return false;
		}

		@Override
		public String askQuestion(final String realm, final String question, final boolean showAnswer,
			final boolean maySave) {
			return null;
		}

		@Override
		public String askQuestion(final String realm, final String question, final boolean showAnswer) {
			return null;
		}

		@Override
		public boolean userAllowedSave() {
			return false;
		}

	}

	public static ISVNProgressMonitor.ItemState makeItemState(final SVNNotification arg0) {
		return new ISVNProgressMonitor.ItemState(arg0.path, arg0.action, arg0.kind, arg0.mimeType, arg0.contentState,
			arg0.propState, arg0.revision, arg0.errMsg);
	}

	protected synchronized static ProgressMonitorThread getProgressMonitorThread() {
		if ( SVNKitConnector.monitorWrapperThread == null ) {
			SVNKitConnector.monitorWrapperThread = new ProgressMonitorThread();
			SVNKitConnector.monitorWrapperThread.start();
		}
		return SVNKitConnector.monitorWrapperThread;
	}

	protected static class ProgressMonitorThread extends Thread {

		private final List<ProgressMonitorWrapper> monitors = new ArrayList<ProgressMonitorWrapper>();

		public ProgressMonitorThread() {
			super("SVN Kit 1.2 Connector");
		}

		public void add(final ProgressMonitorWrapper monitor) {
			synchronized (this.monitors) {
				this.monitors.add(monitor);
				this.monitors.notify();
			}
		}

		public void remove(final ProgressMonitorWrapper monitor) {
			synchronized (this.monitors) {
				this.monitors.remove(monitor);
			}
		}

		@Override
		public void run() {
			while (!this.isInterrupted()) {
				this.checkForActivityCancelled();

				try {
					synchronized (this.monitors) {
						if ( this.monitors.size() == 0 ) {
							this.monitors.wait();
						} else {
							this.monitors.wait(100);
						}
					}
				} catch (InterruptedException ex) {
					break;
				}
			}
		}

		private void checkForActivityCancelled() {
			ProgressMonitorWrapper[] monitors;
			synchronized (this.monitors) {
				monitors = this.monitors.toArray(new ProgressMonitorWrapper[this.monitors.size()]);
			}
			for ( ProgressMonitorWrapper monitor : monitors ) {
				if ( !monitor.isCanceled() && monitor.monitor.isActivityCancelled() ) {
					monitor.cancel();
				}
			}
		}

	}

	protected class ProgressMonitorWrapper implements ISVNNotificationCallback {

		protected ISVNProgressMonitor monitor;
		protected int current;
		protected boolean isCanceled;

		public ProgressMonitorWrapper(final ISVNProgressMonitor monitor) {
			this.monitor = monitor;
			this.current = 0;
			this.isCanceled = false;
		}

		public void cancel() {
			try {
				this.isCanceled = true;
				SVNKitConnector.this.client.cancelOperation();
			} catch (Exception e) {}
		}

		@Override
		public void notify(final SVNNotification arg0) {
			this.monitor.progress(this.current++, ISVNProgressMonitor.TOTAL_UNKNOWN,
				SVNKitConnector.makeItemState(arg0));
		}

		public void start() {
			SVNKitConnector.getProgressMonitorThread().add(this);
		}

		public void interrupt() {
			SVNKitConnector.getProgressMonitorThread().remove(this);
		}

		public boolean isCanceled() {
			return this.isCanceled;
		}

	}

	protected void fireAsked(final String methodName, final Map<String, Object> parameters) {
		for ( ISVNCallListener listener : this.callListeners.toArray(new ISVNCallListener[0]) ) {
			listener.asked(methodName, parameters);
		}
	}

	protected void fireSucceeded(final String methodName, final Map<String, Object> parameters, final Object returnValue) {
		for ( ISVNCallListener listener : this.callListeners.toArray(new ISVNCallListener[0]) ) {
			listener.succeeded(methodName, parameters, returnValue);
		}
	}

	protected void fireFailed(final String methodName, final Map<String, Object> parameters,
		final SVNConnectorException exception) {
		for ( ISVNCallListener listener : this.callListeners.toArray(new ISVNCallListener[0]) ) {
			listener.failed(methodName, parameters, exception);
		}
	}

}
