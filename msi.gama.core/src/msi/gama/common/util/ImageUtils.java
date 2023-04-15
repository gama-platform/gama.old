/*******************************************************************************************************
 *
 * ImageUtils.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.renderable.ParameterBlock;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.stream.ImageInputStream;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.io.Files;
import com.sun.media.jai.codec.FileSeekableStream;

import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class ImageUtils.
 */
public class ImageUtils {

	static {
		DEBUG.OFF();
	}

	/**
	 * The Class GifImageContainer.
	 */
	class GifImageContainer {

		/**
		 * File read status: No errors.
		 */
		public static final int STATUS_OK = 0;

		/**
		 * File read status: Error decoding file (may be partially decoded)
		 */
		public static final int STATUS_FORMAT_ERROR = 1;

		/**
		 * File read status: Unable to open source.
		 */
		public static final int STATUS_OPEN_ERROR = 2;

		/** The in. */
		protected BufferedInputStream in;

		/** The status. */
		protected int status;

		/** The width. */
		protected int width; // full image width

		/** The height. */
		protected int height; // full image height

		/** The gct flag. */
		protected boolean gctFlag; // global color table used

		/** The gct size. */
		protected int gctSize; // size of global color table

		// /** The loop count. */
		// protected int loopCount = 1; // iterations; 0 = repeat forever

		/** The gct. */
		protected int[] gct; // global color table

		/** The lct. */
		protected int[] lct; // local color table

		/** The act. */
		protected int[] act; // active color table

		/** The bg index. */
		protected int bgIndex; // background color index

		/** The bg color. */
		protected int bgColor; // background color

		/** The last bg color. */
		protected int lastBgColor; // previous bg color

		/** The lct flag. */
		protected boolean lctFlag; // local color table flag

		/** The interlace. */
		protected boolean interlace; // interlace flag

		/** The lct size. */
		protected int lctSize; // local color table size

		/** The ih. */
		protected int ix, iy, iw, ih; // current image rectangle

		/** The last rect. */
		protected Rectangle lastRect; // last image rect

		/** The image. */
		protected BufferedImage image; // current frame

		/** The last image. */
		protected BufferedImage lastImage; // previous frame

		/** The block. */
		protected final byte[] block = new byte[256]; // current data block

		/** The block size. */
		protected int blockSize = 0; // block size

		/** The dispose. */
		// last graphic control extension info
		protected int dispose = 0;

		/** The last dispose. */
		// 0=no action; 1=leave in place; 2=restore to bg; 3=restore to prev
		protected int lastDispose = 0;

		/** The transparency. */
		protected boolean transparency = false; // use transparent color

		/** The delay. */
		protected int delay = 0; // delay in milliseconds

		/** The trans index. */
		protected int transIndex; // transparent color index

		/** The Constant MaxStackSize. */
		protected static final int MaxStackSize = 4096;
		// max decoder pixel stack size

		/** The prefix. */
		// LZW decoder working arrays
		protected short[] prefix;

		/** The suffix. */
		protected byte[] suffix;

		/** The pixel stack. */
		protected byte[] pixelStack;

		/** The pixels. */
		protected byte[] pixels;

		/** The frames. */
		protected ArrayList<GifFrame> frames; // frames read from current file

		/** The frame count. */
		protected int frameCount;

		/** The current frame. */
		protected int currentFrame;

		/** The timer. */
		protected Timer timer;

		/**
		 * The Class GifFrame.
		 */
		record GifFrame(BufferedImage image, int delay) {}

		/**
		 * Gets the duration.
		 *
		 * @return the duration
		 */
		public int getDuration() { return frames.stream().mapToInt(GifFrame::delay).sum(); }

		/**
		 * Gets the number of frames read from file.
		 *
		 * @return frame count
		 */
		public int getFrameCount() { return frameCount; }

		/**
		 * Gets the first (or only) image read.
		 *
		 * @return BufferedImage containing first frame, or null if none.
		 */
		public BufferedImage getImage() { return getFrame(currentFrame % frameCount); }

		/**
		 * Creates new frame image from current data (and previous frames as specified by their disposition codes).
		 */
		protected void setPixels() {
			// expose destination image's pixels as int array
			final int[] dest = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

			// fill in starting image contents based on last image's dispose code
			if (lastDispose > 0) {
				if (lastDispose == 3) {
					// use image before last
					final int n = frameCount - 2;
					if (n > 0) {
						lastImage = getFrame(n - 1);
					} else {
						lastImage = null;
					}
				}

				if (lastImage != null) {
					final int[] prev = ((DataBufferInt) lastImage.getRaster().getDataBuffer()).getData();
					System.arraycopy(prev, 0, dest, 0, width * height);
					// copy pixels

					if (lastDispose == 2) {
						// fill last image rect area with background color
						final Graphics2D g = image.createGraphics();
						Color c = null;
						if (transparency) {
							c = new Color(0, 0, 0, 0); // assume background is transparent
						} else {
							c = new Color(lastBgColor); // use given background color
						}
						g.setColor(c);
						g.setComposite(AlphaComposite.Src); // replace area
						g.fill(lastRect);
						g.dispose();
					}
				}
			}

			// copy each source line to the appropriate place in the destination
			int pass = 1;
			int inc = 8;
			int iline = 0;
			for (int i = 0; i < ih; i++) {
				int line = i;
				if (interlace) {
					if (iline >= ih) {
						pass++;
						switch (pass) {
							case 2:
								iline = 4;
								break;
							case 3:
								iline = 2;
								inc = 4;
								break;
							case 4:
								iline = 1;
								inc = 2;
						}
					}
					line = iline;
					iline += inc;
				}
				line += iy;
				if (line < height) {
					final int k = line * width;
					int dx = k + ix; // start of line in dest
					int dlim = dx + iw; // end of dest line
					if (k + width < dlim) {
						dlim = k + width; // past dest edge
					}
					int sx = i * iw; // start of line in source
					while (dx < dlim) {
						// map color and insert in destination
						final int index = pixels[sx++] & 0xff;
						final int c = act[index];
						if (c != 0) { dest[dx] = c; }
						dx++;
					}
				}
			}
		}

		/**
		 * Gets the image contents of frame n.
		 *
		 * @return BufferedImage representation of frame, or null if n is invalid.
		 */
		public BufferedImage getFrame(final int n) {
			BufferedImage im = null;
			if (n >= 0 && n < frameCount) { im = frames.get(n).image(); }
			return im;
		}

		/**
		 * Gets image size.
		 *
		 * @return GIF image dimensions
		 */
		public Dimension getFrameSize() { return new Dimension(width, height); }

		/**
		 * Reads GIF image from stream
		 *
		 * @param InputStream
		 *            containing GIF file.
		 * @return read status code (0 = no errors)
		 */
		public int read(final InputStream stream) {
			init();
			InputStream is = stream;
			if (is != null) {
				if (!(is instanceof BufferedInputStream)) { is = new BufferedInputStream(is); }
				in = (BufferedInputStream) is;
				readHeader();
				if (!err()) {
					readContents();
					if (frameCount < 0) { status = STATUS_FORMAT_ERROR; }
				}
			} else {
				status = STATUS_OPEN_ERROR;
			}
			try {
				if (is != null) { is.close(); }
			} catch (final IOException e) {

			}
			return status;
		}

		/**
		 * Decodes LZW image data into pixel array. Adapted from John Cristy's ImageMagick.
		 */
		protected void decodeImageData() {
			final int NullCode = -1;
			final int npix = iw * ih;
			int available, clear, code_mask, code_size, end_of_information, in_code, old_code, bits, code, count, i,
					datum, data_size, first, top, bi, pi;
			if (pixels == null || pixels.length < npix) {
				pixels = new byte[npix]; // allocate new pixel array
			}
			if (prefix == null) { prefix = new short[MaxStackSize]; }
			if (suffix == null) { suffix = new byte[MaxStackSize]; }
			if (pixelStack == null) { pixelStack = new byte[MaxStackSize + 1]; }

			// Initialize GIF data stream decoder.
			data_size = read();
			clear = 1 << data_size;
			end_of_information = clear + 1;
			available = clear + 2;
			old_code = NullCode;
			code_size = data_size + 1;
			code_mask = (1 << code_size) - 1;
			for (code = 0; code < clear; code++) {
				prefix[code] = 0;
				suffix[code] = (byte) code;
			}

			// Decode GIF pixel stream.

			datum = bits = count = first = top = pi = bi = 0;

			for (i = 0; i < npix;) {
				if (top == 0) {
					if (bits < code_size) {
						// Load bytes until there are enough bits for a code.
						if (count == 0) {
							// Read a new data block.
							count = readBlock();
							if (count <= 0) { break; }
							bi = 0;
						}
						datum += (block[bi] & 0xff) << bits;
						bits += 8;
						bi++;
						count--;
						continue;
					}
					// Get the next code.
					code = datum & code_mask;
					datum >>= code_size;
					bits -= code_size;
					// Interpret the code
					if (code > available || code == end_of_information) { break; }
					if (code == clear) {
						// Reset decoder.
						code_size = data_size + 1;
						code_mask = (1 << code_size) - 1;
						available = clear + 2;
						old_code = NullCode;
						continue;
					}
					if (old_code == NullCode) {
						pixelStack[top++] = suffix[code];
						old_code = code;
						first = code;
						continue;
					}
					in_code = code;
					if (code == available) {
						pixelStack[top++] = (byte) first;
						code = old_code;
					}
					while (code > clear) {
						pixelStack[top++] = suffix[code];
						code = prefix[code];
					}
					first = suffix[code] & 0xff;
					// Add a new string to the string table,
					if (available >= MaxStackSize) { break; }
					pixelStack[top++] = (byte) first;
					prefix[available] = (short) old_code;
					suffix[available] = (byte) first;
					available++;
					if ((available & code_mask) == 0 && available < MaxStackSize) {
						code_size++;
						code_mask += available;
					}
					old_code = in_code;
				}
				// Pop a pixel off the pixel stack.
				top--;
				pixels[pi++] = pixelStack[top];
				i++;
			}
			for (i = pi; i < npix; i++) {
				pixels[i] = 0; // clear missing pixels
			}

		}

		/**
		 * Returns true if an error was encountered during reading/decoding
		 */
		protected boolean err() {
			return status != STATUS_OK;
		}

		/**
		 * Initializes or re-initializes reader
		 */
		protected void init() {
			status = STATUS_OK;
			frameCount = 0;
			frames = new ArrayList<>();
			gct = null;
			lct = null;
		}

		/**
		 * Reads a single byte from the input stream.
		 */
		protected int read() {
			int curByte = 0;
			try {
				curByte = in.read();
			} catch (final IOException e) {
				status = STATUS_FORMAT_ERROR;
			}
			return curByte;
		}

		/**
		 * Reads next variable length block from input.
		 *
		 * @return number of bytes stored in "buffer"
		 */
		protected int readBlock() {
			blockSize = read();
			int n = 0;
			if (blockSize > 0) {
				try {
					int count = 0;
					while (n < blockSize) {
						count = in.read(block, n, blockSize - n);
						if (count == -1) { break; }
						n += count;
					}
				} catch (final IOException e) {}
				if (n < blockSize) { status = STATUS_FORMAT_ERROR; }
			}
			return n;
		}

		/**
		 * Reads color table as 256 RGB integer values
		 *
		 * @param ncolors
		 *            int number of colors to read
		 * @return int array containing 256 colors (packed ARGB with full alpha)
		 */
		protected int[] readColorTable(final int ncolors) {
			final int nbytes = 3 * ncolors;
			int[] tab = null;
			final byte[] c = new byte[nbytes];
			int n = 0;
			try {
				n = in.read(c);
			} catch (final IOException e) {}
			if (n < nbytes) {
				status = STATUS_FORMAT_ERROR;
			} else {
				tab = new int[256]; // max size to avoid bounds checks
				int i = 0;
				int j = 0;
				while (i < ncolors) {
					final int r = c[j++] & 0xff;
					final int g = c[j++] & 0xff;
					final int b = c[j++] & 0xff;
					tab[i++] = 0xff000000 | r << 16 | g << 8 | b;
				}
			}
			return tab;
		}

		/**
		 * Main file parser. Reads GIF content blocks.
		 */
		protected void readContents() {
			// read GIF file content blocks
			boolean done = false;
			while (!done && !err()) {
				int code = read();
				switch (code) {
					case 0x2C: // image separator
						readImage();
						break;
					case 0x21: // extension
						code = read();
						switch (code) {
							case 0xf9: // graphics control extension
								readGraphicControlExt();
								break;

							case 0xff: // application extension
								readBlock();
								StringBuilder app = new StringBuilder();
								for (int i = 0; i < 11; i++) { app.append((char) block[i]); }
								if ("NETSCAPE2.0".equals(app.toString())) {
									readNetscapeExt();
								} else {
									skip(); // don't care
								}
								break;

							default: // uninteresting extension
								skip();
						}
						break;
					case 0x3b: // terminator
						done = true;
						break;
					case 0x00: // bad byte, but keep going and see what happens
						break;
					default:
						status = STATUS_FORMAT_ERROR;
				}
			}
			if (frameCount > 0 && getDuration() > 0) {
				timer = new Timer(true);
				timer.scheduleAtFixedRate(task, 0, getDuration() / frameCount);
			}
		}

		/** The task. */
		private final TimerTask task = new TimerTask() {

			@Override
			public void run() {
				currentFrame++;
			}
		};

		/**
		 * Reads Graphics Control Extension values
		 */
		protected void readGraphicControlExt() {
			read(); // block size
			final int packed = read(); // packed fields
			dispose = (packed & 0x1c) >> 2; // disposal method
			if (dispose == 0) {
				dispose = 1; // elect to keep old image if discretionary
			}
			transparency = (packed & 1) != 0;
			delay = readShort() * 10; // delay in milliseconds
			transIndex = read(); // transparent color index
			read(); // block terminator
		}

		/**
		 * Reads GIF file header information.
		 */
		protected void readHeader() {
			StringBuilder id = new StringBuilder();
			for (int i = 0; i < 6; i++) { id.append((char) read()); }
			if (!id.toString().startsWith("GIF")) {
				status = STATUS_FORMAT_ERROR;
				return;
			}
			readLSD();
			if (gctFlag && !err()) {
				gct = readColorTable(gctSize);
				bgColor = gct[bgIndex];
			}
		}

		/**
		 * Reads next frame image
		 */
		protected void readImage() {
			ix = readShort(); // (sub)image position & size
			iy = readShort();
			iw = readShort();
			ih = readShort();
			final int packed = read();
			lctFlag = (packed & 0x80) != 0; // 1 - local color table flag
			interlace = (packed & 0x40) != 0; // 2 - interlace flag
			// 3 - sort flag
			// 4-5 - reserved
			lctSize = 2 << (packed & 7); // 6-8 - local color table size
			if (lctFlag) {
				lct = readColorTable(lctSize); // read table
				act = lct; // make local table active
			} else {
				act = gct; // make global table active
				if (bgIndex == transIndex) { bgColor = 0; }
			}
			int save = 0;
			if (transparency) {
				save = act[transIndex];
				act[transIndex] = 0; // set transparent color if specified
			}
			if (act == null) {
				status = STATUS_FORMAT_ERROR; // no color table defined
			}
			if (err()) return;
			decodeImageData(); // decode pixel data
			skip();
			if (err()) return;
			frameCount++;
			// create new image to receive frame data
			image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);
			setPixels(); // transfer pixel data to image
			frames.add(new GifFrame(image, delay)); // add image to frame list
			if (transparency) { act[transIndex] = save; }
			resetFrame();

		}

		/**
		 * Reads Logical Screen Descriptor
		 */
		protected void readLSD() {
			// logical screen size
			width = readShort();
			height = readShort();
			// packed fields
			final int packed = read();
			gctFlag = (packed & 0x80) != 0; // 1 : global color table flag
			// 2-4 : color resolution
			// 5 : gct sort flag
			gctSize = 2 << (packed & 7); // 6-8 : gct size
			bgIndex = read();
			// background color index
			/* pixelAspect = */ read(); // pixel aspect ratio
		}

		/**
		 * Reads Netscape extenstion to obtain iteration count
		 */
		protected void readNetscapeExt() {
			do {
				readBlock();
				// if (block[0] == 1) {
				// loop count sub-block
				// final int b1 = block[1] & 0xff;
				// final int b2 = block[2] & 0xff;
				// loopCount = b2 << 8 | b1;
				// }
			} while (blockSize > 0 && !err());
		}

		/**
		 * Reads next 16-bit value, LSB first
		 */
		protected int readShort() {
			// read 16-bit value, LSB first
			return read() | read() << 8;
		}

		/**
		 * Resets frame state for reading next image.
		 */
		protected void resetFrame() {
			lastDispose = dispose;
			lastRect = new Rectangle(ix, iy, iw, ih);
			lastImage = image;
			lastBgColor = bgColor;
			lct = null;
		}

		/**
		 * Skips variable length blocks up to and including next zero length block.
		 */
		protected void skip() {
			do { readBlock(); } while (blockSize > 0 && !err());
		}

		/**
		 * Dispose.
		 */
		public void dispose() {
			if (timer != null) { timer.cancel(); }
			if (frames != null) { frames.clear(); }
			if (image != null) { image.flush(); }
			if (lastImage != null) { lastImage.flush(); }

		}

	}

	/** The no image. */
	private static BufferedImage NO_IMAGE;

	/** The cache. */
	private final Cache<String, BufferedImage> cache = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES)
			.removalListener(notification -> ((BufferedImage) notification.getValue()).flush()).build();

	/** The open GL cache. */
	private final Cache<String, BufferedImage> openGLCache =
			CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES)
					.removalListener(notification -> ((BufferedImage) notification.getValue()).flush()).build();

	/** The gif cache. */
	private final Cache<String, GifImageContainer> gifCache =
			CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES)
					.removalListener(notification -> ((GifImageContainer) notification.getValue()).dispose()).build();

	/** The cached GC. */
	private static GraphicsConfiguration cachedGC;

	/**
	 * Gets the cached GC.
	 *
	 * @return the cached GC
	 */
	public static GraphicsConfiguration getCachedGC() {
		if (cachedGC == null) {
			DEBUG.OUT("Creating cached Graphics Configuration");
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			DEBUG.OUT("Local Graphics Environment selected");
			GraphicsDevice gd = ge.getDefaultScreenDevice();
			DEBUG.OUT("Default Graphics Device selected");
			cachedGC = gd.getDefaultConfiguration();
			DEBUG.OUT("Default Graphics Configuration selected");
		}
		return cachedGC;
	}

	/**
	 * Gets the no image.
	 *
	 * @return the no image
	 */
	public static BufferedImage getNoImage() {
		if (NO_IMAGE == null) { NO_IMAGE = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB); }
		return NO_IMAGE;
	}

	/** The Constant tiffExt. */
	private static final List<String> tiffExt = Arrays.asList(".tiff", ".tif", ".TIF", ".TIFF");

	/** The Constant gifExt. */
	private static final List<String> gifExt = Arrays.asList(".gif", ".GIF");

	/** The instance. */
	private static ImageUtils instance = new ImageUtils();

	/**
	 * Gets the single instance of ImageUtils.
	 *
	 * @return single instance of ImageUtils
	 */
	public static ImageUtils getInstance() { return instance; }

	/**
	 * Instantiates a new image utils.
	 */
	private ImageUtils() {}

	/**
	 * fileName is supposed to be already absolute, and to have been checked before, as the calls come from GamaFile
	 *
	 * @param scope
	 * @param fileName
	 * @param useCache
	 * @param extension
	 * @return
	 * @throws IOException
	 */
	public BufferedImage getImageFromFile(final IScope scope, final String fileName, final boolean useCache,
			final boolean forOpenGL, final IIOReadProgressListener listener, final String extension)
			throws IOException {
		if (useCache) {
			final BufferedImage image = cache.getIfPresent(fileName);
			if (image != null) return image;
			final GifImageContainer gif = gifCache.getIfPresent(fileName);
			if (gif != null) return gif.getImage();
		}
		// final String s = scope != null ? FileUtils.constructAbsoluteFilePath(scope, fileName, true) : fileName;
		final File f = new File(fileName);
		final BufferedImage result = getImageFromFile(f, useCache, forOpenGL, listener, extension);
		return result == getNoImage() ? null : result;
	}

	/**
	 * Gets the frame count.
	 *
	 * @param path
	 *            the path
	 * @return the frame count
	 */
	public int getFrameCount(final String path) {
		final GifImageContainer gif = gifCache.getIfPresent(path);
		if (gif == null) return 1;
		return gif.getFrameCount();
	}

	// /**
	// * Gets the duration.
	// *
	// * @param path
	// * the path
	// * @return the duration
	// */
	// public int getDuration(final String path) {
	// final GifDecoder gif = gifCache.getIfPresent(path);
	// if (gif == null) return 0;
	// return gif.getDuration();
	// }

	/**
	 * Private read from file.
	 *
	 * @param file
	 *            the file
	 * @param forOpenGL
	 *            the for open GL
	 * @param listener
	 *            the listener
	 * @param extension
	 *            the optional extension
	 *
	 * @return the buffered image
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private BufferedImage privateReadFromFile(final File file, final boolean forOpenGL,
			final IIOReadProgressListener listener, final String extension) throws IOException {
		// DEBUG.OUT("READING " + file.getName());
		BufferedImage result = getNoImage();
		if (file == null) return result;
		final String name = file.getName();
		String ext = null;
		if (name.contains(".")) { ext = name.substring(file.getName().lastIndexOf('.')); }
		if (tiffExt.contains(ext)) {
			try (FileSeekableStream stream = new FileSeekableStream(file.getAbsolutePath())) {
				/**
				 * AD TODO : decodeParam is not used ...
				 */
				// final TIFFDecodeParam decodeParam = new TIFFDecodeParam();
				// decodeParam.setDecodePaletteAsShorts(true);
				final ParameterBlock params = new ParameterBlock();
				params.add(stream);
				final RenderedOp image1 = JAI.create("tiff", params);
				return image1.getAsBufferedImage();
			}
		}
		if (gifExt.contains(ext)) {
			final GifImageContainer d = new GifImageContainer();
			d.read(new FileInputStream(file.getAbsolutePath()));
			return d.getImage();
		}

		try {
			result = forOpenGL ? ioRead(file, listener, extension)
					: toCompatibleImage(ioRead(file, listener, extension));
		} catch (final Exception e) {
			return getNoImage();
		}
		return result;
	}

	/**
	 * Io read.
	 *
	 * @param file
	 *            the file
	 * @param listener
	 *            the listener
	 * @return the buffered image
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private BufferedImage ioRead(final File file, final IIOReadProgressListener listener, final String extension)
			throws IOException {
		ImageReader imageReader = null;
		ImageInputStream imageInputStream = null;
		try {
			Iterator<ImageReader> readers = ImageIO
					.getImageReadersBySuffix(extension != null ? extension : Files.getFileExtension(file.getName()));
			imageReader = readers.next();
			imageInputStream = ImageIO.createImageInputStream(file);

		} catch (final Exception e) {
			Iterator<ImageReader> readers = ImageIO.getImageReadersBySuffix("jpg");
			imageReader = readers.next();
			imageInputStream = ImageIO.createImageInputStream(file);
		}

		imageReader.setInput(imageInputStream, false);
		if (listener != null) { imageReader.addIIOReadProgressListener(listener); }
		return imageReader.read(0);
	}

	/**
	 * Private read gif from file.
	 *
	 * @param file
	 *            the file
	 * @return the gif decoder
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private GifImageContainer privateReadGifFromFile(final File file) throws IOException {
		final GifImageContainer d = new GifImageContainer();
		d.read(new FileInputStream(file.getAbsolutePath()));
		return d;
	}

	/**
	 * Gets the image from file.
	 *
	 * @param file
	 *            the file
	 * @param useCache
	 *            the use cache
	 * @param forOpenGL
	 *            the for open GL
	 * @param listener
	 *            the listener
	 * @param extension
	 *            the optional extension
	 * @return the image from file
	 */
	public BufferedImage getImageFromFile(final File file, final boolean useCache, final boolean forOpenGL,
			final IIOReadProgressListener listener, final String extension) {
		final BufferedImage image;
		String name, ext = null;
		try {
			name = file.getName();
			if (name.contains(".")) { ext = name.substring(file.getName().lastIndexOf('.')); }
			if (gifExt.contains(ext)) {
				if (useCache) {
					image = gifCache.get(file.getAbsolutePath(), () -> privateReadGifFromFile(file)).getImage();
				} else {
					image = privateReadGifFromFile(file).getImage();
				}
			} else if (useCache) {
				if (forOpenGL) {
					image = openGLCache.get(file.getAbsolutePath(),
							() -> privateReadFromFile(file, true, listener, extension));
				} else {
					image = cache.get(file.getAbsolutePath(),
							() -> privateReadFromFile(file, false, listener, extension));
				}
			} else {
				image = privateReadFromFile(file, forOpenGL, listener, extension);
			}
			return image == getNoImage() ? null : image;
		} catch (final ExecutionException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** The no graphics environment. */
	static boolean NO_GRAPHICS_ENVIRONMENT = false;

	/**
	 * Creates the premultiplied blank image.
	 *
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @return the buffered image
	 */
	public static BufferedImage createPremultipliedBlankImage(final int width, final int height) {
		return new BufferedImage(width != 0 ? width : 1024, height != 0 ? height : 1024,
				BufferedImage.TYPE_INT_ARGB_PRE);
	}

	/**
	 * Creates the compatible image.
	 *
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @param forOpenGL
	 *            the for open GL
	 * @return the buffered image
	 */
	public static BufferedImage createCompatibleImage(final int width, final int height, final boolean forOpenGL) {
		if (forOpenGL) return createPremultipliedBlankImage(width, height);
		BufferedImage new_image = null;
		if (NO_GRAPHICS_ENVIRONMENT || GAMA.isInHeadLessMode() || GraphicsEnvironment.isHeadless()) {
			new_image = new BufferedImage(width != 0 ? width : 1024, height != 0 ? height : 1024,
					BufferedImage.TYPE_INT_ARGB);
		} else {
			new_image = getCachedGC().createCompatibleImage(width, height);
		}
		return new_image;
	}

	/**
	 * To compatible image.
	 *
	 * @param image
	 *            the image
	 * @return the buffered image
	 */
	public static BufferedImage toCompatibleImage(final BufferedImage image) {
		/*
		 * if image is already compatible and optimized for current system settings, simply return it
		 */
		if (NO_GRAPHICS_ENVIRONMENT || GAMA.isInHeadLessMode() || GraphicsEnvironment.isHeadless()
				|| image.getColorModel().equals(getCachedGC().getColorModel()))
			return image;

		// image is not optimized, so create a new image that is
		final BufferedImage new_image =
				getCachedGC().createCompatibleImage(image.getWidth(), image.getHeight(), image.getTransparency());
		// new BufferedImage(image.getWidth() != 0 ? image.getWidth() : 1024,
		// image.getHeight() != 0 ? image.getHeight() : 1024, BufferedImage.TYPE_INT_ARGB);
		// get the graphics context of the new image to draw the old image on
		final Graphics2D g2d = (Graphics2D) new_image.getGraphics();

		// actually draw the image and dispose of context no longer needed
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();

		// return the new optimized image
		return new_image;
	}

	/**
	 * Clear cache.
	 *
	 * @param pathName
	 *            the path name
	 */
	public void clearCache(final String pathName) {
		cache.invalidate(pathName);
		openGLCache.invalidate(pathName);

	}

	/**
	 * Force cache image.
	 *
	 * @param im
	 *            the im
	 * @param filename
	 *            the filename
	 */
	public void forceCacheImage(final BufferedImage im, final String filename, final boolean forOpenGL) {
		if (forOpenGL) {
			openGLCache.put(filename, im);
		} else {
			cache.put(filename, im);
		}

	}

}
