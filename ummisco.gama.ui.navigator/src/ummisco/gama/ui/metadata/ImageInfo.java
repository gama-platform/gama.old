package ummisco.gama.ui.metadata;

import java.util.HashMap;
import java.util.Map;

import msi.gama.util.file.GamaFileMetaData;
import msi.gaml.operators.Strings;

/**
 * The Class ImageInfo.
 */
public class ImageInfo extends GamaFileMetaData {

	/** The Constant formatsShortNames. */
	public final static Map<Integer, String> formatsShortNames = new HashMap<>() {

		{
			// Hack: Corresponds to SWT.IMAGE_xxx + ImagePropertyPage
			// constants
			put(0, "BMP");
			put(1, "BMP");
			put(7, "BMP");
			put(2, "GIF");
			put(4, "JPEG");
			put(5, "PNG");
			put(3, "ICO");
			put(6, "TIFF");
			put(-1, "Unknown Format");
			put(8, "ASCII");
			put(9, "PGM");
		}
	};

	/** The type. */
	private final int type;

	/** The width. */
	private final int width;

	/** The height. */
	private final int height;

	/**
	 * Instantiates a new image info.
	 *
	 * @param modificationStamp
	 *            the modification stamp
	 * @param origType
	 *            the orig type
	 * @param origWidth
	 *            the orig width
	 * @param origHeight
	 *            the orig height
	 */
	public ImageInfo(final long modificationStamp, /* final Object thumbnail, */final int origType,
			final int origWidth, final int origHeight) {
		super(modificationStamp);
		this.type = origType;
		this.width = origWidth;
		this.height = origHeight;
	}

	/**
	 * Instantiates a new image info.
	 *
	 * @param propertyString
	 *            the property string
	 */
	public ImageInfo(final String propertyString) {
		super(propertyString);
		final String[] segments = split(propertyString);
		type = Integer.parseInt(segments[1]);
		width = Integer.parseInt(segments[2]);
		height = Integer.parseInt(segments[3]);
	}

	/**
	 * Gets the short label.
	 *
	 * @param type
	 *            the type
	 * @return the short label
	 */
	public String getShortLabel(final int type) {
		return formatsShortNames.containsKey(type) ? formatsShortNames.get(type) : formatsShortNames.get(-1);
	}

	@Override
	public String getSuffix() { return "" + width + "x" + height + ", " + getShortLabel(type) + ""; }

	@Override
	public void appendSuffix(final StringBuilder sb) {
		sb.append(width).append("x").append(height).append(SUFFIX_DEL).append(getShortLabel(type));
	}

	@Override
	public String getDocumentation() {
		final StringBuilder sb = new StringBuilder();
		sb.append(getShortLabel(type)).append(" Image File").append(Strings.LN);
		sb.append("Dimensions: ").append(width + " pixels x " + height + " pixels").append(Strings.LN);
		return sb.toString();
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public int getType() { return type; }

	@Override
	public String toPropertyString() {
		return super.toPropertyString() + DELIMITER + type + DELIMITER + width + DELIMITER + height;
	}
}