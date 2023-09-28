/*******************************************************************************************************
 *
 * MMFBytez.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package org.nustaq.offheap.bytez.malloc;

import java.io.File;
import java.io.RandomAccessFile;
import java.lang.ref.Cleaner;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;

/**
 * Bytez allocated inside a memory mapped file. Some Mmap file stuff handling is copied from OpenHFT library (too big to
 * depend on for fst), a great tool for all kind of binary/low level java stuff. Check it out at github.
 */
@SuppressWarnings ("unchecked")
public class MMFBytez extends MallocBytez {

	/** The file. */
	private File file;

	/** The file channel. */
	private FileChannel fileChannel;

	/** The file channel class. */
	@SuppressWarnings ("unchecked") private static Class<? extends FileChannel> fileChannelClass;

	static {
		try {
			fileChannelClass = (Class<? extends FileChannel>) Class.forName("sun.nio.ch.FileChannelImpl");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	/** The cleaner. */
	private Cleaner cleaner;

	/**
	 * Instantiates a new MMF bytez.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param filePath
	 *            the file path
	 * @param length
	 *            the length
	 * @param clearFile
	 *            the clear file
	 * @throws Exception
	 *             the exception
	 * @date 27 sept. 2023
	 */
	public MMFBytez(final String filePath, final long length, final boolean clearFile) throws Exception {
		super(0, 0);
		init(filePath, length, clearFile);
	}

	/**
	 * Inits the.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param file
	 *            the file
	 * @param length
	 *            the length
	 * @param clearFile
	 *            the clear file
	 * @throws Exception
	 *             the exception
	 * @date 27 sept. 2023
	 */
	protected void init(final String file, long length, final boolean clearFile) throws Exception {
		File f = new File(file);
		if (f.exists() && clearFile) { f.delete(); }
		this.file = f;

		if (f.exists()) { length = f.length(); }

		RandomAccessFile raf = new RandomAccessFile(f, "rw");
		raf.setLength(length); // FIXME: see stackoverflow. does not work always
		FileChannel fileChannel = raf.getChannel();
		fileChannelClass = fileChannel.getClass();
		this.fileChannel = raf.getChannel();
		this.baseAdress = map0(fileChannel, imodeFor(FileChannel.MapMode.READ_WRITE), 0L, length);
		this.length = length;
		this.cleaner = Cleaner.create();
		cleaner.register(this, new Unmapper(baseAdress, length, fileChannel));
	}

	/**
	 * Free and close.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 27 sept. 2023
	 */
	public void freeAndClose() {
		// CleanerImpl.
	}

	/**
	 * hack to update underlying file in slices handed out to app
	 */
	public void _setMMFData(final File file, final FileChannel fileChannel, final Cleaner cleaner) {
		this.file = file;
		this.fileChannel = fileChannel;
		this.cleaner = cleaner;
	}

	/**
	 * Gets the file.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the file
	 * @date 27 sept. 2023
	 */
	public File getFile() { return file; }

	/**
	 * Gets the file channel.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the file channel
	 * @date 27 sept. 2023
	 */
	public FileChannel getFileChannel() { return fileChannel; }

	/**
	 * Gets the cleaner.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the cleaner
	 * @date 27 sept. 2023
	 */
	public Cleaner getCleaner() { return cleaner; }

	/**
	 * stuff copied from OpenHFT library (too big to depend on for fst) ...
	 *
	 * Copyright 2013 Peter Lawrey
	 *
	 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
	 * with the License. You may obtain a copy of the License at
	 *
	 * http://www.apache.org/licenses/LICENSE-2.0
	 *
	 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
	 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
	 * the specific language governing permissions and limitations under the License.
	 */

	/**
	 * @param fileChannel
	 * @param imode
	 * @param start
	 * @param size
	 * @return
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */

	private static final int MAP_RO = 0;

	/** The Constant MAP_RW. */
	private static final int MAP_RW = 1;

	/** The Constant MAP_PV. */
	private static final int MAP_PV = 2;

	/**
	 * Map 0.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param fileChannel
	 *            the file channel
	 * @param imode
	 *            the imode
	 * @param start
	 *            the start
	 * @param size
	 *            the size
	 * @return the long
	 * @throws NoSuchMethodException
	 *             the no such method exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws InvocationTargetException
	 *             the invocation target exception
	 * @date 27 sept. 2023
	 */
	private static long map0(final FileChannel fileChannel, final int imode, final long start, final long size)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Method map0 = fileChannelClass.getDeclaredMethod("map0", int.class, long.class, long.class);
		if (map0 != null) {
			map0.setAccessible(true);
			return (Long) map0.invoke(fileChannel, imode, start, size);
		}
		return 0l;
	}

	/**
	 * Unmap 0.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param address
	 *            the address
	 * @param size
	 *            the size
	 * @throws Exception
	 *             the exception
	 * @date 27 sept. 2023
	 */
	private static void unmap0(final long address, final long size) throws Exception {
		Method unmap0 = fileChannelClass.getDeclaredMethod("unmap0", long.class, long.class);
		unmap0.setAccessible(true);
		unmap0.invoke(null, address, size);
	}

	/**
	 * Imode for.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param mode
	 *            the mode
	 * @return the int
	 * @date 27 sept. 2023
	 */
	private static int imodeFor(final FileChannel.MapMode mode) {
		int imode = -1;
		if (mode == FileChannel.MapMode.READ_ONLY) {
			imode = MAP_RO;
		} else if (mode == FileChannel.MapMode.READ_WRITE) {
			imode = MAP_RW;
		} else if (mode == FileChannel.MapMode.PRIVATE) { imode = MAP_PV; }
		assert imode >= 0;
		return imode;
	}

	/**
	 * The Class Unmapper.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 27 sept. 2023
	 */
	static class Unmapper implements Runnable {

		/** The size. */
		private final long size;

		/** The channel. */
		private final FileChannel channel;

		/** The address. */
		private volatile long address;

		/**
		 * Instantiates a new unmapper.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param address
		 *            the address
		 * @param size
		 *            the size
		 * @param channel
		 *            the channel
		 * @date 27 sept. 2023
		 */
		Unmapper(final long address, final long size, final FileChannel channel) {
			assert address != 0;
			this.address = address;
			this.size = size;
			this.channel = channel;
		}

		@Override
		public void run() {
			if (address == 0) return;

			try {
				unmap0(address, size);
				address = 0;

				if (channel.isOpen()) { channel.close(); }

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
