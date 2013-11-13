/*******************************************************************************
 * Copyright (c) 2005-2008 Polarion Software.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Alexander Gurov - Initial API and implementation
 *******************************************************************************/

package org.polarion.team.svn.connector.svnkit;

import java.util.regex.*;
import org.eclipse.team.svn.core.connector.ISVNConnector;
import org.eclipse.team.svn.core.extension.factory.ISVNConnectorFactory;
import org.tmatesoft.svn.core.javahl.SVNClientImpl;

/**
 * Default implementation. Works with SVN Kit SVN connector.
 * 
 * @author <A HREF="www.polarion.org">Alexander Gurov</A>, POLARION.ORG
 * 
 * @version $Revision: $ $Date: $
 */
public class SVNKitConnectorFactory implements ISVNConnectorFactory {

	public static final String CLIENT_ID = "org.eclipse.team.svn.connector.svnkit16l";

	@Override
	public ISVNConnector newInstance() {
		return new SVNKitConnector();
	}

	@Override
	public String getName() {
		String format = "%1$s %2$s r%3$s (SVN %4$s compatible, all platforms)";
		/*
		 * Parse client version string
		 * Example: SVN/1.6.1 SVNKit/1.3.0-beta (http://svnkit.com/) r3589
		 */
		String fullClientVersion = this.getClientVersion();
		String versionRegex = "\\d+\\.\\d+\\.\\d+.*";
		Pattern regex =
			Pattern.compile("^SVN/(" + versionRegex + ")\\s+SVNKit/(" + versionRegex +
				")\\s+\\(http://svnkit.com/\\)\\s+r\\d+$");
		Matcher matcher = regex.matcher(fullClientVersion);
		if ( matcher.matches() ) {
			String svnVersion = matcher.group(1);
			String clientVersion = matcher.group(2);
			return String.format(format, "SVNKit", clientVersion, SVNClientImpl.versionRevisionNumber(), svnVersion);
		} else {
			// we can't parse so return ordinary name
			return "SVNKit";
		}
	}

	@Override
	public String getId() {
		return SVNKitConnectorFactory.CLIENT_ID;
	}

	@Override
	public String getClientVersion() {
		return SVNClientImpl.version();
	}

	@Override
	public String getVersion() {
		return "2.2.2.I20120210-1700";
	}

	@Override
	public String getCompatibilityVersion() {
		return "0.8.0.I20120413-1700";
		// return "0.7.9.I20111018-1700";
	}

	@Override
	public int getSupportedFeatures() {
		return OptionalFeatures.SSH_SETTINGS | OptionalFeatures.PROXY_SETTINGS | OptionalFeatures.ATOMIC_X_COMMIT;
	}

	@Override
	public int getSVNAPIVersion() {
		return APICompatibility.SVNAPI_1_6_x;
	}

	@Override
	public String toString() {
		return this.getId();
	}

}
