/*******************************************************************************
 * Copyright (c) 2005-2008 Polarion Software.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Alexander Gurov (Polarion Software) - initial API and implementation
 *******************************************************************************/

package org.tigris.subversion.javahl;

import java.text.*;
import java.util.*;
import org.eclipse.team.svn.core.connector.*;
import org.eclipse.team.svn.core.connector.SVNRevision.Kind;

/**
 * JavaHL <-> Subversive API conversions
 * 
 * @author Alexander Gurov
 */
public final class ConversionUtility {

	public static org.tigris.subversion.javahl.ProplistCallback convert(final ISVNPropertyCallback callback) {
		if ( callback == null ) { return null; }
		return new org.tigris.subversion.javahl.ProplistCallback() {

			@Override
			public void singlePath(final String path, final Map properties) {
				SVNProperty[] data = new SVNProperty[properties.size()];
				int i = 0;
				for ( Iterator it = properties.entrySet().iterator(); it.hasNext(); i++ ) {
					Map.Entry entry = (Map.Entry) it.next();
					data[i] = new SVNProperty((String) entry.getKey(), (String) entry.getValue());
				}
				callback.next(path, data);
			}
		};
	}

	public static Map convert(final Map revProps) {
		if ( revProps == null ) { return null; }
		Map retVal = new HashMap();
		if ( revProps.containsKey(SVNProperty.BuiltIn.REV_AUTHOR) ) {
			retVal.put(SVNProperty.BuiltIn.REV_AUTHOR, revProps.get(SVNProperty.BuiltIn.REV_AUTHOR));
		}
		if ( revProps.containsKey(SVNProperty.BuiltIn.REV_LOG) ) {
			retVal.put(SVNProperty.BuiltIn.REV_LOG, revProps.get(SVNProperty.BuiltIn.REV_LOG));
		}
		retVal.putAll(revProps);
		if ( retVal.containsKey(SVNProperty.BuiltIn.REV_DATE) ) {
			Date date = (Date) revProps.get(SVNProperty.BuiltIn.REV_DATE);
			retVal.put(SVNProperty.BuiltIn.REV_DATE, date == null ? null : new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSS z").format(date));
		}
		return retVal;
	}

	public static org.tigris.subversion.javahl.LogMessageCallback convert(final ISVNLogEntryCallback callback) {
		if ( callback == null ) { return null; }
		return new org.tigris.subversion.javahl.LogMessageCallback() {

			/*
			 * Copied from the LogDate class in order to avoid unsafe usage of a static DateFormat instance in the
			 * multi-threaded environment.
			 */
			private final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS z");
			private final Calendar date = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

			@Override
			public void singleMessage(final org.tigris.subversion.javahl.ChangePath[] changedPaths,
				final long revision, final Map revProps, final boolean hasChildren) {
				SVNLogEntry entry = this.convert(changedPaths, revision, revProps, hasChildren);
				callback.next(entry);
			}

			private SVNLogEntry convert(final org.tigris.subversion.javahl.ChangePath[] changedPaths,
				final long revision, final Map revProps, final boolean hasChildren) {
				if ( revProps == null ) {
					// null if no access rights...
					return new SVNLogEntry(revision, 0l, null, null, ConversionUtility.convert(changedPaths),
						hasChildren);
				}
				String dateStr = (String) revProps.get(SVNProperty.BuiltIn.REV_DATE);

				long date = 0;
				if ( dateStr != null && dateStr.length() == 27 && dateStr.charAt(26) == 'Z' ) {
					try {
						this.date.setTime(this.formatter.parse(dateStr.substring(0, 23) + " UTC"));
						date = this.date.getTimeInMillis();
					} catch (ParseException e) {
						// uninteresting in this context
					} catch (NumberFormatException e) {
						// uninteresting in this context
					}
				}
				return new SVNLogEntry(revision, date, (String) revProps.get(SVNProperty.BuiltIn.REV_AUTHOR),
					(String) revProps.get(SVNProperty.BuiltIn.REV_LOG), ConversionUtility.convert(changedPaths),
					hasChildren);
			}
		};
	}

	public static org.tigris.subversion.javahl.CopySource[] convert(final SVNEntryRevisionReference[] info) {
		if ( info == null ) { return null; }
		org.tigris.subversion.javahl.CopySource[] retVal = new org.tigris.subversion.javahl.CopySource[info.length];
		for ( int i = 0; i < info.length; i++ ) {
			retVal[i] = ConversionUtility.convert(info[i]);
		}
		return retVal;
	}

	public static org.tigris.subversion.javahl.CopySource convert(final SVNEntryRevisionReference info) {
		return info == null ? null : new org.tigris.subversion.javahl.CopySource(info.path,
			ConversionUtility.convert(info.revision), ConversionUtility.convert(info.pegRevision));
	}

	public static org.tigris.subversion.javahl.InfoCallback convert(final ISVNEntryInfoCallback callback) {
		if ( callback == null ) { return null; }
		return new org.tigris.subversion.javahl.InfoCallback() {

			@Override
			public void singleInfo(final org.tigris.subversion.javahl.Info2 info) {
				callback.next(ConversionUtility.convert(info));
			}
		};
	}

	public static org.tigris.subversion.javahl.StatusCallback convert(final ISVNEntryStatusCallback cb) {
		if ( cb == null ) { return null; }
		return new org.tigris.subversion.javahl.StatusCallback() {

			@Override
			public void doStatus(final org.tigris.subversion.javahl.Status st) {
				cb.next(ConversionUtility.convert(st));
			}
		};
	}

	public static org.tigris.subversion.javahl.ConflictResolverCallback convert(
		final ISVNConflictResolutionCallback callback) {
		if ( callback == null ) { return null; }
		return new org.tigris.subversion.javahl.ConflictResolverCallback() {

			@Override
			public org.tigris.subversion.javahl.ConflictResult resolve(
				final org.tigris.subversion.javahl.ConflictDescriptor descrip) throws SubversionException {
				try {
					return ConversionUtility.convert(callback.resolve(ConversionUtility.convert(descrip)));
				} catch (SVNConnectorException ex) {
					throw new SubversionException(ex.getMessage());
				}
			}
		};
	}

	public static SVNConflictDescriptor convert(final org.tigris.subversion.javahl.ConflictDescriptor descr) {
		return descr == null ? null
			: new SVNConflictDescriptor(descr.getPath(), descr.getKind(), descr.getNodeKind(), descr.getPropertyName(),
				descr.isBinary(), descr.getMIMEType(), descr.getAction(), descr.getReason(), descr.getOperation(),
				descr.getBasePath(), descr.getTheirPath(), descr.getMyPath(), descr.getMergedPath(),
				ConversionUtility.convert(descr.getSrcLeftVersion()), ConversionUtility.convert(descr
					.getSrcRightVersion()));
	}

	public static SVNConflictVersion convert(final org.tigris.subversion.javahl.ConflictVersion conflictVersion) {
		return conflictVersion == null ? null : new SVNConflictVersion(conflictVersion.getReposURL(),
			conflictVersion.getPegRevision(), conflictVersion.getPathInRepos(), conflictVersion.getNodeKind());
	}

	public static org.tigris.subversion.javahl.ConflictResult convert(final SVNConflictResolution result) {
		return result == null ? null
			: new org.tigris.subversion.javahl.ConflictResult(result.choice, result.mergedPath);
	}

	public static SVNMergeInfo convert(final org.tigris.subversion.javahl.Mergeinfo info) {
		if ( info == null ) { return null; }
		SVNMergeInfo retVal = new SVNMergeInfo();
		String[] paths = info.getPaths();
		if ( paths != null ) {
			for ( int i = 0; i < paths.length; i++ ) {
				retVal.addRevisions(paths[i], ConversionUtility.convert(info.getRevisionRange(paths[i])));
			}
		}
		return retVal;
	}

	public static org.tigris.subversion.javahl.RevisionRange[] convert(final SVNRevisionRange[] ranges) {
		if ( ranges == null ) { return null; }
		org.tigris.subversion.javahl.RevisionRange[] retVal =
			new org.tigris.subversion.javahl.RevisionRange[ranges.length];
		for ( int i = 0; i < ranges.length; i++ ) {
			retVal[i] = ConversionUtility.convert(ranges[i]);
		}
		return retVal;
	}

	public static SVNRevisionRange[] convert(final org.tigris.subversion.javahl.RevisionRange[] ranges) {
		if ( ranges == null ) { return null; }
		SVNRevisionRange[] retVal = new SVNRevisionRange[ranges.length];
		for ( int i = 0; i < ranges.length; i++ ) {
			retVal[i] = ConversionUtility.convert(ranges[i]);
		}
		return retVal;
	}

	public static SVNRevisionRange convert(final org.tigris.subversion.javahl.RevisionRange range) {
		return range == null ? null : new SVNRevisionRange(ConversionUtility.convert(range.getFromRevision()),
			ConversionUtility.convert(range.getToRevision()));
	}

	public static org.tigris.subversion.javahl.RevisionRange convert(final SVNRevisionRange range) {
		return range == null ? null : new org.tigris.subversion.javahl.RevisionRange(
			ConversionUtility.convert(range.from), ConversionUtility.convert(range.to));
	}

	public static SVNEntryInfo[] convert(final org.tigris.subversion.javahl.Info2[] infos) {
		if ( infos == null ) { return null; }
		SVNEntryInfo[] retVal = new SVNEntryInfo[infos.length];
		for ( int i = 0; i < infos.length; i++ ) {
			retVal[i] = ConversionUtility.convert(infos[i]);
		}
		return retVal;
	}

	public static SVNEntryInfo convert(final org.tigris.subversion.javahl.Info2 info) {
		return info == null ? null : new SVNEntryInfo(info.getPath(), null, info.getUrl(), info.getRev(),
			info.getKind(), info.getReposRootUrl(), info.getReposUUID(), info.getLastChangedRev(),
			info.getLastChangedDate() == null ? 0 : info.getLastChangedDate().getTime(), info.getLastChangedAuthor(),
			ConversionUtility.convert(info.getLock()), info.isHasWcInfo(), info.getSchedule(), info.getCopyFromUrl(),
			info.getCopyFromRev(), info.getTextTime() == null ? 0 : info.getTextTime().getTime(),
			info.getPropTime() == null ? 0 : info.getPropTime().getTime(), info.getChecksum() == null ? null
				: new SVNChecksum(SVNChecksum.LEGACY, info.getChecksum().getBytes()), null, 0, 0,
			ISVNConnector.Depth.UNKNOWN, info.getConflictDescriptor() == null ? null
				: new SVNConflictDescriptor[] { ConversionUtility.convert(info.getConflictDescriptor()) });
	}

	public static SVNLogPath[] convert(final org.tigris.subversion.javahl.ChangePath[] paths) {
		if ( paths == null ) { return null; }
		SVNLogPath[] retVal = new SVNLogPath[paths.length];
		for ( int i = 0; i < paths.length; i++ ) {
			retVal[i] = ConversionUtility.convert(paths[i]);
		}
		return retVal;
	}

	public static SVNLogPath convert(final org.tigris.subversion.javahl.ChangePath path) {
		return path == null ? null : new SVNLogPath(path.getPath(), path.getAction(), path.getCopySrcPath(),
			path.getCopySrcRevision());
	}

	public static SVNProperty[] convert(final org.tigris.subversion.javahl.PropertyData[] data) {
		if ( data == null ) { return null; }
		SVNProperty[] retVal = new SVNProperty[data.length];
		for ( int i = 0; i < data.length; i++ ) {
			retVal[i] = ConversionUtility.convert(data[i]);
		}
		return retVal;
	}

	public static SVNProperty convert(final org.tigris.subversion.javahl.PropertyData data) {
		if ( data == null ) { return null; }
		String value =
			data.getValue() != null ? data.getValue() : data.getData() != null ? new String(data.getData()) : null;
		return new SVNProperty(data.getName(), value);
	}

	public static SVNChangeStatus[] convert(final org.tigris.subversion.javahl.Status[] st) {
		if ( st == null ) { return null; }
		SVNChangeStatus[] retVal = new SVNChangeStatus[st.length];
		for ( int i = 0; i < st.length; i++ ) {
			retVal[i] = ConversionUtility.convert(st[i]);
		}
		return retVal;
	}

	public static SVNChangeStatus convert(final org.tigris.subversion.javahl.Status st) {
		return st == null ? null : new SVNChangeStatus(st.getPath(), st.getUrl(), st.getNodeKind(),
			st.getRevisionNumber(), st.getLastChangedRevisionNumber(), st.getLastChangedDate() == null ? 0 : st
				.getLastChangedDate().getTime(), st.getLastCommitAuthor(), st.getTextStatus(), st.getPropStatus(),
			st.getRepositoryTextStatus(), st.getRepositoryPropStatus(), st.isLocked(), st.isCopied(), st.isSwitched(),
			st.getLockToken() == null ? null : new SVNLock(st.getLockOwner(), st.getPath(), st.getLockToken(), st
				.getLockComment(), st.getLockCreationDate() == null ? 0 : st.getLockCreationDate().getTime(), 0),
			ConversionUtility.convert(st.getReposLock()), st.getReposLastCmtRevisionNumber(),
			st.getReposLastCmtDate() == null ? 0 : st.getReposLastCmtDate().getTime(), st.getReposKind(),
			st.getReposLastCmtAuthor(), st.isFileExternal(), st.getConflictNew() != null, st.getConflictNew() == null
				? null : new SVNConflictDescriptor[] { new SVNConflictDescriptor(st.getPath(),
					SVNConflictDescriptor.Kind.CONTENT, st.getNodeKind(), null, false, null,
					SVNConflictDescriptor.Action.MODIFY, SVNConflictDescriptor.Reason.MODIFIED,
					SVNConflictDescriptor.Operation.UPDATE, st.getConflictOld(), st.getConflictNew(),
					st.getConflictWorking(), null, null, null) }, null);
	}

	public static SVNLock convert(final org.tigris.subversion.javahl.Lock lock) {
		return lock == null ? null : new SVNLock(lock.getOwner(), lock.getPath(), lock.getToken(), lock.getComment(),
			lock.getCreationDate() == null ? 0 : lock.getCreationDate().getTime(), lock.getExpirationDate() == null ? 0
				: lock.getExpirationDate().getTime());
	}

	public static org.tigris.subversion.javahl.BlameCallback2 convert(final ISVNAnnotationCallback cb) {
		if ( cb == null ) { return null; }
		return new org.tigris.subversion.javahl.BlameCallback2() {

			@Override
			public void singleLine(final Date date, final long revision, final String author, final Date merged_date,
				final long merged_revision, final String merged_author, final String merged_path, final String line) {
				cb.annotate(line, new SVNAnnotationData(0, false, revision, date == null ? 0 : date.getTime(), author,
					merged_revision, merged_date == null ? 0 : merged_date.getTime(), merged_author, merged_path));
			}
		};
	}

	public static SVNRevision convert(final org.tigris.subversion.javahl.Revision rev) {
		if ( rev != null ) {
			switch (rev.getKind()) {
				case RevisionKind.base:
					return SVNRevision.BASE;
				case RevisionKind.committed:
					return SVNRevision.COMMITTED;
				case RevisionKind.head:
					return SVNRevision.HEAD;
				case RevisionKind.previous:
					return SVNRevision.PREVIOUS;
				case RevisionKind.working:
					return SVNRevision.WORKING;
				case RevisionKind.unspecified:
					return SVNRevision.START;
				case RevisionKind.number:
					return SVNRevision.fromNumber(((org.tigris.subversion.javahl.Revision.Number) rev).getNumber());
				case RevisionKind.date:
				default:
					return SVNRevision.fromDate(((org.tigris.subversion.javahl.Revision.DateSpec) rev).getDate()
						.getTime());
			}
		}
		return null;
	}

	public static org.tigris.subversion.javahl.Revision convert(final SVNRevision rev) {
		if ( rev != null ) {
			switch (rev.getKind()) {
				case Kind.BASE:
					return org.tigris.subversion.javahl.Revision.BASE;
				case Kind.COMMITTED:
					return org.tigris.subversion.javahl.Revision.COMMITTED;
				case Kind.HEAD:
					return org.tigris.subversion.javahl.Revision.HEAD;
				case Kind.PREVIOUS:
					return org.tigris.subversion.javahl.Revision.PREVIOUS;
				case Kind.WORKING:
					return org.tigris.subversion.javahl.Revision.WORKING;
				case Kind.START:
					return org.tigris.subversion.javahl.Revision.START;
				case Kind.NUMBER:
					return org.tigris.subversion.javahl.Revision.getInstance(((SVNRevision.Number) rev).getNumber());
				case Kind.DATE:
				default:
					return org.tigris.subversion.javahl.Revision.getInstance(new Date(((SVNRevision.Date) rev)
						.getDate()));
			}
		}
		return null;
	}

	public static SVNNotification convert(final org.tigris.subversion.javahl.NotifyInformation info) {
		return info == null ? null : new SVNNotification(info.getPath(), info.getAction(), info.getKind(),
			info.getMimeType(), ConversionUtility.convert(info.getLock()), info.getErrMsg(), info.getContentState(),
			info.getPropState(), info.getLockState(), info.getRevision());
	}

	public static ISVNNotificationCallback convert(final org.tigris.subversion.javahl.Notify2 notify2) {
		return notify2 == null ? null : ((Notify2Wrapper) notify2).getNotify2();
	}

	public static org.tigris.subversion.javahl.Notify2 convert(final ISVNNotificationCallback notify2) {
		return notify2 == null ? null : new Notify2Wrapper(notify2);
	}

	public static class Notify2Wrapper implements org.tigris.subversion.javahl.Notify2 {

		protected ISVNNotificationCallback notify;

		public Notify2Wrapper(final ISVNNotificationCallback notify) {
			this.notify = notify;
		}

		public ISVNNotificationCallback getNotify2() {
			return this.notify;
		}

		@Override
		public void onNotify(final org.tigris.subversion.javahl.NotifyInformation info) {
			this.notify.notify(ConversionUtility.convert(info));
		}
	}

	private ConversionUtility() {

	}
}
