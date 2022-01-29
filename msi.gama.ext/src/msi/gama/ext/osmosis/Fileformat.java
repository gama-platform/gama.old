/*******************************************************************************************************
 *
 * Fileformat.java, in msi.gama.ext, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
// source: src/main/protobuf/fileformat.proto

package msi.gama.ext.osmosis;

/**
 * The Class Fileformat.
 */
@SuppressWarnings ("deprecation")
public final class Fileformat {
	
	/**
	 * Instantiates a new fileformat.
	 */
	private Fileformat() {}

	/**
	 * Register all extensions.
	 *
	 * @param registry the registry
	 */
	public static void registerAllExtensions(final com.google.protobuf.ExtensionRegistryLite registry) {}

	/**
	 * The Interface BlobOrBuilder.
	 */
	public interface BlobOrBuilder extends
			// @@protoc_insertion_point(interface_extends:OSMPBF.Blob)
			com.google.protobuf.MessageLiteOrBuilder {

		/**
		 * <pre>
		 * No compression
		 * </pre>
		 *
		 * <code>optional bytes raw = 1;</code>
		 */
		boolean hasRaw();

		/**
		 * <pre>
		 * No compression
		 * </pre>
		 *
		 * <code>optional bytes raw = 1;</code>
		 */
		com.google.protobuf.ByteString getRaw();

		/**
		 * <pre>
		 * When compressed, the uncompressed size
		 * </pre>
		 *
		 * <code>optional int32 raw_size = 2;</code>
		 */
		boolean hasRawSize();

		/**
		 * <pre>
		 * When compressed, the uncompressed size
		 * </pre>
		 *
		 * <code>optional int32 raw_size = 2;</code>
		 */
		int getRawSize();

		/**
		 * <pre>
		 * Possible compressed versions of the data.
		 * </pre>
		 *
		 * <code>optional bytes zlib_data = 3;</code>
		 */
		boolean hasZlibData();

		/**
		 * <pre>
		 * Possible compressed versions of the data.
		 * </pre>
		 *
		 * <code>optional bytes zlib_data = 3;</code>
		 */
		com.google.protobuf.ByteString getZlibData();

		/**
		 * <pre>
		 * PROPOSED feature for LZMA compressed data. SUPPORT IS NOT REQUIRED.
		 * </pre>
		 *
		 * <code>optional bytes lzma_data = 4;</code>
		 */
		boolean hasLzmaData();

		/**
		 * <pre>
		 * PROPOSED feature for LZMA compressed data. SUPPORT IS NOT REQUIRED.
		 * </pre>
		 *
		 * <code>optional bytes lzma_data = 4;</code>
		 */
		com.google.protobuf.ByteString getLzmaData();

		/**
		 * <pre>
		 * Formerly used for bzip2 compressed data. Depreciated in 2010.
		 * </pre>
		 *
		 * <code>optional bytes OBSOLETE_bzip2_data = 5 [deprecated = true];</code>
		 */
		@java.lang.Deprecated
		boolean hasOBSOLETEBzip2Data();

		/**
		 * <pre>
		 * Formerly used for bzip2 compressed data. Depreciated in 2010.
		 * </pre>
		 *
		 * <code>optional bytes OBSOLETE_bzip2_data = 5 [deprecated = true];</code>
		 */
		@java.lang.Deprecated
		com.google.protobuf.ByteString getOBSOLETEBzip2Data();
	}

	/**
	 * Protobuf type {@code OSMPBF.Blob}
	 */
	public static final class Blob extends com.google.protobuf.GeneratedMessageLite<Blob, Blob.Builder> implements
			// @@protoc_insertion_point(message_implements:OSMPBF.Blob)
			BlobOrBuilder {
		
		/**
		 * Instantiates a new blob.
		 */
		private Blob() {
			raw_ = com.google.protobuf.ByteString.EMPTY;
			zlibData_ = com.google.protobuf.ByteString.EMPTY;
			lzmaData_ = com.google.protobuf.ByteString.EMPTY;
			oBSOLETEBzip2Data_ = com.google.protobuf.ByteString.EMPTY;
		}

		/** The bit field 0. */
		private int bitField0_;
		
		/** The Constant RAW_FIELD_NUMBER. */
		public static final int RAW_FIELD_NUMBER = 1;
		
		/** The raw. */
		private com.google.protobuf.ByteString raw_;

		/**
		 * <pre>
		 * No compression
		 * </pre>
		 *
		 * <code>optional bytes raw = 1;</code>
		 */
		@java.lang.Override
		public boolean hasRaw() {
			return (bitField0_ & 0x00000001) == 0x00000001;
		}

		/**
		 * <pre>
		 * No compression
		 * </pre>
		 *
		 * <code>optional bytes raw = 1;</code>
		 */
		@java.lang.Override
		public com.google.protobuf.ByteString getRaw() { return raw_; }

		/**
		 * <pre>
		 * No compression
		 * </pre>
		 *
		 * <code>optional bytes raw = 1;</code>
		 */
		private void setRaw(final com.google.protobuf.ByteString value) {
			if (value == null) throw new NullPointerException();
			bitField0_ |= 0x00000001;
			raw_ = value;
		}

		/**
		 * <pre>
		 * No compression
		 * </pre>
		 *
		 * <code>optional bytes raw = 1;</code>
		 */
		private void clearRaw() {
			bitField0_ = bitField0_ & ~0x00000001;
			raw_ = getDefaultInstance().getRaw();
		}

		/** The Constant RAW_SIZE_FIELD_NUMBER. */
		public static final int RAW_SIZE_FIELD_NUMBER = 2;
		
		/** The raw size. */
		private int rawSize_;

		/**
		 * <pre>
		 * When compressed, the uncompressed size
		 * </pre>
		 *
		 * <code>optional int32 raw_size = 2;</code>
		 */
		@java.lang.Override
		public boolean hasRawSize() {
			return (bitField0_ & 0x00000002) == 0x00000002;
		}

		/**
		 * <pre>
		 * When compressed, the uncompressed size
		 * </pre>
		 *
		 * <code>optional int32 raw_size = 2;</code>
		 */
		@java.lang.Override
		public int getRawSize() { return rawSize_; }

		/**
		 * <pre>
		 * When compressed, the uncompressed size
		 * </pre>
		 *
		 * <code>optional int32 raw_size = 2;</code>
		 */
		private void setRawSize(final int value) {
			bitField0_ |= 0x00000002;
			rawSize_ = value;
		}

		/**
		 * <pre>
		 * When compressed, the uncompressed size
		 * </pre>
		 *
		 * <code>optional int32 raw_size = 2;</code>
		 */
		private void clearRawSize() {
			bitField0_ = bitField0_ & ~0x00000002;
			rawSize_ = 0;
		}

		/** The Constant ZLIB_DATA_FIELD_NUMBER. */
		public static final int ZLIB_DATA_FIELD_NUMBER = 3;
		
		/** The zlib data. */
		private com.google.protobuf.ByteString zlibData_;

		/**
		 * <pre>
		 * Possible compressed versions of the data.
		 * </pre>
		 *
		 * <code>optional bytes zlib_data = 3;</code>
		 */
		@java.lang.Override
		public boolean hasZlibData() {
			return (bitField0_ & 0x00000004) == 0x00000004;
		}

		/**
		 * <pre>
		 * Possible compressed versions of the data.
		 * </pre>
		 *
		 * <code>optional bytes zlib_data = 3;</code>
		 */
		@java.lang.Override
		public com.google.protobuf.ByteString getZlibData() { return zlibData_; }

		/**
		 * <pre>
		 * Possible compressed versions of the data.
		 * </pre>
		 *
		 * <code>optional bytes zlib_data = 3;</code>
		 */
		private void setZlibData(final com.google.protobuf.ByteString value) {
			if (value == null) throw new NullPointerException();
			bitField0_ |= 0x00000004;
			zlibData_ = value;
		}

		/**
		 * <pre>
		 * Possible compressed versions of the data.
		 * </pre>
		 *
		 * <code>optional bytes zlib_data = 3;</code>
		 */
		private void clearZlibData() {
			bitField0_ = bitField0_ & ~0x00000004;
			zlibData_ = getDefaultInstance().getZlibData();
		}

		/** The Constant LZMA_DATA_FIELD_NUMBER. */
		public static final int LZMA_DATA_FIELD_NUMBER = 4;
		
		/** The lzma data. */
		private com.google.protobuf.ByteString lzmaData_;

		/**
		 * <pre>
		 * PROPOSED feature for LZMA compressed data. SUPPORT IS NOT REQUIRED.
		 * </pre>
		 *
		 * <code>optional bytes lzma_data = 4;</code>
		 */
		@java.lang.Override
		public boolean hasLzmaData() {
			return (bitField0_ & 0x00000008) == 0x00000008;
		}

		/**
		 * <pre>
		 * PROPOSED feature for LZMA compressed data. SUPPORT IS NOT REQUIRED.
		 * </pre>
		 *
		 * <code>optional bytes lzma_data = 4;</code>
		 */
		@java.lang.Override
		public com.google.protobuf.ByteString getLzmaData() { return lzmaData_; }

		/**
		 * <pre>
		 * PROPOSED feature for LZMA compressed data. SUPPORT IS NOT REQUIRED.
		 * </pre>
		 *
		 * <code>optional bytes lzma_data = 4;</code>
		 */
		private void setLzmaData(final com.google.protobuf.ByteString value) {
			if (value == null) throw new NullPointerException();
			bitField0_ |= 0x00000008;
			lzmaData_ = value;
		}

		/**
		 * <pre>
		 * PROPOSED feature for LZMA compressed data. SUPPORT IS NOT REQUIRED.
		 * </pre>
		 *
		 * <code>optional bytes lzma_data = 4;</code>
		 */
		private void clearLzmaData() {
			bitField0_ = bitField0_ & ~0x00000008;
			lzmaData_ = getDefaultInstance().getLzmaData();
		}

		/** The Constant OBSOLETE_BZIP2_DATA_FIELD_NUMBER. */
		public static final int OBSOLETE_BZIP2_DATA_FIELD_NUMBER = 5;
		
		/** The o BSOLETE bzip 2 data. */
		private com.google.protobuf.ByteString oBSOLETEBzip2Data_;

		/**
		 * <pre>
		 * Formerly used for bzip2 compressed data. Depreciated in 2010.
		 * </pre>
		 *
		 * <code>optional bytes OBSOLETE_bzip2_data = 5 [deprecated = true];</code>
		 */
		@java.lang.Override
		@java.lang.Deprecated
		public boolean hasOBSOLETEBzip2Data() {
			return (bitField0_ & 0x00000010) == 0x00000010;
		}

		/**
		 * <pre>
		 * Formerly used for bzip2 compressed data. Depreciated in 2010.
		 * </pre>
		 *
		 * <code>optional bytes OBSOLETE_bzip2_data = 5 [deprecated = true];</code>
		 */
		@java.lang.Override
		@java.lang.Deprecated
		public com.google.protobuf.ByteString getOBSOLETEBzip2Data() { return oBSOLETEBzip2Data_; }

		/**
		 * <pre>
		 * Formerly used for bzip2 compressed data. Depreciated in 2010.
		 * </pre>
		 *
		 * <code>optional bytes OBSOLETE_bzip2_data = 5 [deprecated = true];</code>
		 */
		private void setOBSOLETEBzip2Data(final com.google.protobuf.ByteString value) {
			if (value == null) throw new NullPointerException();
			bitField0_ |= 0x00000010;
			oBSOLETEBzip2Data_ = value;
		}

		/**
		 * <pre>
		 * Formerly used for bzip2 compressed data. Depreciated in 2010.
		 * </pre>
		 *
		 * <code>optional bytes OBSOLETE_bzip2_data = 5 [deprecated = true];</code>
		 */
		private void clearOBSOLETEBzip2Data() {
			bitField0_ = bitField0_ & ~0x00000010;
			oBSOLETEBzip2Data_ = getDefaultInstance().getOBSOLETEBzip2Data();
		}

		@java.lang.Override
		public void writeTo(final com.google.protobuf.CodedOutputStream output) throws java.io.IOException {
			if ((bitField0_ & 0x00000001) == 0x00000001) { output.writeBytes(1, raw_); }
			if ((bitField0_ & 0x00000002) == 0x00000002) { output.writeInt32(2, rawSize_); }
			if ((bitField0_ & 0x00000004) == 0x00000004) { output.writeBytes(3, zlibData_); }
			if ((bitField0_ & 0x00000008) == 0x00000008) { output.writeBytes(4, lzmaData_); }
			if ((bitField0_ & 0x00000010) == 0x00000010) { output.writeBytes(5, oBSOLETEBzip2Data_); }
			unknownFields.writeTo(output);
		}

		@java.lang.Override
		public int getSerializedSize() {
			int size = memoizedSerializedSize;
			if (size != -1) return size;

			size = 0;
			if ((bitField0_ & 0x00000001) == 0x00000001) {
				size += com.google.protobuf.CodedOutputStream.computeBytesSize(1, raw_);
			}
			if ((bitField0_ & 0x00000002) == 0x00000002) {
				size += com.google.protobuf.CodedOutputStream.computeInt32Size(2, rawSize_);
			}
			if ((bitField0_ & 0x00000004) == 0x00000004) {
				size += com.google.protobuf.CodedOutputStream.computeBytesSize(3, zlibData_);
			}
			if ((bitField0_ & 0x00000008) == 0x00000008) {
				size += com.google.protobuf.CodedOutputStream.computeBytesSize(4, lzmaData_);
			}
			if ((bitField0_ & 0x00000010) == 0x00000010) {
				size += com.google.protobuf.CodedOutputStream.computeBytesSize(5, oBSOLETEBzip2Data_);
			}
			size += unknownFields.getSerializedSize();
			memoizedSerializedSize = size;
			return size;
		}

		/**
		 * Parses the from.
		 *
		 * @param data the data
		 * @return the blob
		 * @throws InvalidProtocolBufferException the invalid protocol buffer exception
		 */
		public static Blob parseFrom(final java.nio.ByteBuffer data)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return com.google.protobuf.GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
		}

		/**
		 * Parses the from.
		 *
		 * @param data the data
		 * @param extensionRegistry the extension registry
		 * @return the blob
		 * @throws InvalidProtocolBufferException the invalid protocol buffer exception
		 */
		public static Blob parseFrom(final java.nio.ByteBuffer data,
				final com.google.protobuf.ExtensionRegistryLite extensionRegistry)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return com.google.protobuf.GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
		}

		/**
		 * Parses the from.
		 *
		 * @param data the data
		 * @return the blob
		 * @throws InvalidProtocolBufferException the invalid protocol buffer exception
		 */
		public static Blob parseFrom(final com.google.protobuf.ByteString data)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return com.google.protobuf.GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
		}

		/**
		 * Parses the from.
		 *
		 * @param data the data
		 * @param extensionRegistry the extension registry
		 * @return the blob
		 * @throws InvalidProtocolBufferException the invalid protocol buffer exception
		 */
		public static Blob parseFrom(final com.google.protobuf.ByteString data,
				final com.google.protobuf.ExtensionRegistryLite extensionRegistry)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return com.google.protobuf.GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
		}

		/**
		 * Parses the from.
		 *
		 * @param data the data
		 * @return the blob
		 * @throws InvalidProtocolBufferException the invalid protocol buffer exception
		 */
		public static Blob parseFrom(final byte[] data) throws com.google.protobuf.InvalidProtocolBufferException {
			return com.google.protobuf.GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
		}

		/**
		 * Parses the from.
		 *
		 * @param data the data
		 * @param extensionRegistry the extension registry
		 * @return the blob
		 * @throws InvalidProtocolBufferException the invalid protocol buffer exception
		 */
		public static Blob parseFrom(final byte[] data,
				final com.google.protobuf.ExtensionRegistryLite extensionRegistry)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return com.google.protobuf.GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
		}

		/**
		 * Parses the from.
		 *
		 * @param input the input
		 * @return the blob
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public static Blob parseFrom(final java.io.InputStream input) throws java.io.IOException {
			return com.google.protobuf.GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
		}

		/**
		 * Parses the from.
		 *
		 * @param input the input
		 * @param extensionRegistry the extension registry
		 * @return the blob
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public static Blob parseFrom(final java.io.InputStream input,
				final com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
			return com.google.protobuf.GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
		}

		/**
		 * Parses the delimited from.
		 *
		 * @param input the input
		 * @return the blob
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public static Blob parseDelimitedFrom(final java.io.InputStream input) throws java.io.IOException {
			return parseDelimitedFrom(DEFAULT_INSTANCE, input);
		}

		/**
		 * Parses the delimited from.
		 *
		 * @param input the input
		 * @param extensionRegistry the extension registry
		 * @return the blob
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public static Blob parseDelimitedFrom(final java.io.InputStream input,
				final com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
			return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
		}

		/**
		 * Parses the from.
		 *
		 * @param input the input
		 * @return the blob
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public static Blob parseFrom(final com.google.protobuf.CodedInputStream input) throws java.io.IOException {
			return com.google.protobuf.GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
		}

		/**
		 * Parses the from.
		 *
		 * @param input the input
		 * @param extensionRegistry the extension registry
		 * @return the blob
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public static Blob parseFrom(final com.google.protobuf.CodedInputStream input,
				final com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
			return com.google.protobuf.GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
		}

		/**
		 * New builder.
		 *
		 * @return the builder
		 */
		public static Builder newBuilder() {
			return Blob.newBuilder();
		}

		// public static Builder newBuilder(final Blob prototype) {
		// return DEFAULT_INSTANCE.newBuilder(prototype);
		// }

		/**
		 * Protobuf type {@code OSMPBF.Blob}
		 */
		public static final class Builder
				extends com.google.protobuf.GeneratedMessageLite.Builder<Fileformat.Blob, Builder> implements
				// @@protoc_insertion_point(builder_implements:OSMPBF.Blob)
				BlobOrBuilder {
			
			/**
			 * Instantiates a new builder.
			 */
			// Construct using Blob.newBuilder()
			private Builder() {
				super(DEFAULT_INSTANCE);
			}

			/**
			 * <pre>
			 * No compression
			 * </pre>
			 *
			 * <code>optional bytes raw = 1;</code>
			 */
			@java.lang.Override
			public boolean hasRaw() {
				return instance.hasRaw();
			}

			/**
			 * <pre>
			 * No compression
			 * </pre>
			 *
			 * <code>optional bytes raw = 1;</code>
			 */
			@java.lang.Override
			public com.google.protobuf.ByteString getRaw() { return instance.getRaw(); }

			/**
			 * <pre>
			 * No compression
			 * </pre>
			 *
			 * <code>optional bytes raw = 1;</code>
			 */
			public Builder setRaw(final com.google.protobuf.ByteString value) {
				copyOnWrite();
				instance.setRaw(value);
				return this;
			}

			/**
			 * <pre>
			 * No compression
			 * </pre>
			 *
			 * <code>optional bytes raw = 1;</code>
			 */
			public Builder clearRaw() {
				copyOnWrite();
				instance.clearRaw();
				return this;
			}

			/**
			 * <pre>
			 * When compressed, the uncompressed size
			 * </pre>
			 *
			 * <code>optional int32 raw_size = 2;</code>
			 */
			@java.lang.Override
			public boolean hasRawSize() {
				return instance.hasRawSize();
			}

			/**
			 * <pre>
			 * When compressed, the uncompressed size
			 * </pre>
			 *
			 * <code>optional int32 raw_size = 2;</code>
			 */
			@java.lang.Override
			public int getRawSize() { return instance.getRawSize(); }

			/**
			 * <pre>
			 * When compressed, the uncompressed size
			 * </pre>
			 *
			 * <code>optional int32 raw_size = 2;</code>
			 */
			public Builder setRawSize(final int value) {
				copyOnWrite();
				instance.setRawSize(value);
				return this;
			}

			/**
			 * <pre>
			 * When compressed, the uncompressed size
			 * </pre>
			 *
			 * <code>optional int32 raw_size = 2;</code>
			 */
			public Builder clearRawSize() {
				copyOnWrite();
				instance.clearRawSize();
				return this;
			}

			/**
			 * <pre>
			 * Possible compressed versions of the data.
			 * </pre>
			 *
			 * <code>optional bytes zlib_data = 3;</code>
			 */
			@java.lang.Override
			public boolean hasZlibData() {
				return instance.hasZlibData();
			}

			/**
			 * <pre>
			 * Possible compressed versions of the data.
			 * </pre>
			 *
			 * <code>optional bytes zlib_data = 3;</code>
			 */
			@java.lang.Override
			public com.google.protobuf.ByteString getZlibData() { return instance.getZlibData(); }

			/**
			 * <pre>
			 * Possible compressed versions of the data.
			 * </pre>
			 *
			 * <code>optional bytes zlib_data = 3;</code>
			 */
			public Builder setZlibData(final com.google.protobuf.ByteString value) {
				copyOnWrite();
				instance.setZlibData(value);
				return this;
			}

			/**
			 * <pre>
			 * Possible compressed versions of the data.
			 * </pre>
			 *
			 * <code>optional bytes zlib_data = 3;</code>
			 */
			public Builder clearZlibData() {
				copyOnWrite();
				instance.clearZlibData();
				return this;
			}

			/**
			 * <pre>
			 * PROPOSED feature for LZMA compressed data. SUPPORT IS NOT REQUIRED.
			 * </pre>
			 *
			 * <code>optional bytes lzma_data = 4;</code>
			 */
			@java.lang.Override
			public boolean hasLzmaData() {
				return instance.hasLzmaData();
			}

			/**
			 * <pre>
			 * PROPOSED feature for LZMA compressed data. SUPPORT IS NOT REQUIRED.
			 * </pre>
			 *
			 * <code>optional bytes lzma_data = 4;</code>
			 */
			@java.lang.Override
			public com.google.protobuf.ByteString getLzmaData() { return instance.getLzmaData(); }

			/**
			 * <pre>
			 * PROPOSED feature for LZMA compressed data. SUPPORT IS NOT REQUIRED.
			 * </pre>
			 *
			 * <code>optional bytes lzma_data = 4;</code>
			 */
			public Builder setLzmaData(final com.google.protobuf.ByteString value) {
				copyOnWrite();
				instance.setLzmaData(value);
				return this;
			}

			/**
			 * <pre>
			 * PROPOSED feature for LZMA compressed data. SUPPORT IS NOT REQUIRED.
			 * </pre>
			 *
			 * <code>optional bytes lzma_data = 4;</code>
			 */
			public Builder clearLzmaData() {
				copyOnWrite();
				instance.clearLzmaData();
				return this;
			}

			/**
			 * <pre>
			 * Formerly used for bzip2 compressed data. Depreciated in 2010.
			 * </pre>
			 *
			 * <code>optional bytes OBSOLETE_bzip2_data = 5 [deprecated = true];</code>
			 */
			@java.lang.Override
			@java.lang.Deprecated
			public boolean hasOBSOLETEBzip2Data() {
				return instance.hasOBSOLETEBzip2Data();
			}

			/**
			 * <pre>
			 * Formerly used for bzip2 compressed data. Depreciated in 2010.
			 * </pre>
			 *
			 * <code>optional bytes OBSOLETE_bzip2_data = 5 [deprecated = true];</code>
			 */
			@java.lang.Override
			@java.lang.Deprecated
			public com.google.protobuf.ByteString getOBSOLETEBzip2Data() { return instance.getOBSOLETEBzip2Data(); }

			/**
			 * <pre>
			 * Formerly used for bzip2 compressed data. Depreciated in 2010.
			 * </pre>
			 *
			 * <code>optional bytes OBSOLETE_bzip2_data = 5 [deprecated = true];</code>
			 */
			@java.lang.Deprecated
			public Builder setOBSOLETEBzip2Data(final com.google.protobuf.ByteString value) {
				copyOnWrite();
				instance.setOBSOLETEBzip2Data(value);
				return this;
			}

			/**
			 * <pre>
			 * Formerly used for bzip2 compressed data. Depreciated in 2010.
			 * </pre>
			 *
			 * <code>optional bytes OBSOLETE_bzip2_data = 5 [deprecated = true];</code>
			 */
			@java.lang.Deprecated
			public Builder clearOBSOLETEBzip2Data() {
				copyOnWrite();
				instance.clearOBSOLETEBzip2Data();
				return this;
			}

			// @@protoc_insertion_point(builder_scope:OSMPBF.Blob)
		}

		@java.lang.Override
		@java.lang.SuppressWarnings ({ "unchecked", "fallthrough" })
		protected java.lang.Object dynamicMethod(final com.google.protobuf.GeneratedMessageLite.MethodToInvoke method,
				final java.lang.Object arg0, final java.lang.Object arg1) {
			switch (method) {
				case NEW_MUTABLE_INSTANCE: {
					return new Blob();
				}
				case NEW_BUILDER: {
					return new Builder();
				}
				case IS_INITIALIZED: {
					return DEFAULT_INSTANCE;
				}
				case MAKE_IMMUTABLE: {
					return null;
				}
				case VISIT: {
					final Visitor visitor = (Visitor) arg0;
					final Blob other = (Blob) arg1;
					raw_ = visitor.visitByteString(hasRaw(), raw_, other.hasRaw(), other.raw_);
					rawSize_ = visitor.visitInt(hasRawSize(), rawSize_, other.hasRawSize(), other.rawSize_);
					zlibData_ = visitor.visitByteString(hasZlibData(), zlibData_, other.hasZlibData(), other.zlibData_);
					lzmaData_ = visitor.visitByteString(hasLzmaData(), lzmaData_, other.hasLzmaData(), other.lzmaData_);
					oBSOLETEBzip2Data_ = visitor.visitByteString(hasOBSOLETEBzip2Data(), oBSOLETEBzip2Data_,
							other.hasOBSOLETEBzip2Data(), other.oBSOLETEBzip2Data_);
					if (visitor == com.google.protobuf.GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
						bitField0_ |= other.bitField0_;
					}
					return this;
				}
				case MERGE_FROM_STREAM: {
					final com.google.protobuf.CodedInputStream input = (com.google.protobuf.CodedInputStream) arg0;
					final com.google.protobuf.ExtensionRegistryLite extensionRegistry =
							(com.google.protobuf.ExtensionRegistryLite) arg1;
					if (extensionRegistry == null) throw new java.lang.NullPointerException();
					try {
						boolean done = false;
						while (!done) {
							final int tag = input.readTag();
							switch (tag) {
								case 0:
									done = true;
									break;
								case 10: {
									bitField0_ |= 0x00000001;
									raw_ = input.readBytes();
									break;
								}
								case 16: {
									bitField0_ |= 0x00000002;
									rawSize_ = input.readInt32();
									break;
								}
								case 26: {
									bitField0_ |= 0x00000004;
									zlibData_ = input.readBytes();
									break;
								}
								case 34: {
									bitField0_ |= 0x00000008;
									lzmaData_ = input.readBytes();
									break;
								}
								case 42: {
									bitField0_ |= 0x00000010;
									oBSOLETEBzip2Data_ = input.readBytes();
									break;
								}
								default: {
									if (!parseUnknownField(tag, input)) { done = true; }
									break;
								}
							}
						}
					} catch (final com.google.protobuf.InvalidProtocolBufferException e) {
						throw new RuntimeException(e.setUnfinishedMessage(this));
					} catch (final java.io.IOException e) {
						throw new RuntimeException(
								new com.google.protobuf.InvalidProtocolBufferException(e.getMessage())
										.setUnfinishedMessage(this));
					} finally {}
				}
				// fall through
				case GET_DEFAULT_INSTANCE: {
					return DEFAULT_INSTANCE;
				}
				case GET_PARSER: {
					com.google.protobuf.Parser<Blob> parser = PARSER;
					if (parser == null) {
						synchronized (Blob.class) {
							parser = PARSER;
							if (parser == null) {
								parser = new DefaultInstanceBasedParser(DEFAULT_INSTANCE);
								PARSER = parser;
							}
						}
					}
					return parser;
				}
				case GET_MEMOIZED_IS_INITIALIZED: {
					return (byte) 1;
				}
				case SET_MEMOIZED_IS_INITIALIZED: {
					return null;
				}
			}
			throw new UnsupportedOperationException();
		}

		/** The Constant DEFAULT_INSTANCE. */
		// @@protoc_insertion_point(class_scope:OSMPBF.Blob)
		static final Blob DEFAULT_INSTANCE;
		static {
			// New instances are implicitly immutable so no need to make
			// immutable.
			DEFAULT_INSTANCE = new Blob();
		}

		/**
		 * Gets the default instance.
		 *
		 * @return the default instance
		 */
		public static Blob getDefaultInstance() { return DEFAULT_INSTANCE; }

		/** The parser. */
		private static volatile com.google.protobuf.Parser<Blob> PARSER;

		/**
		 * Parser.
		 *
		 * @return the com.google.protobuf. parser
		 */
		public static com.google.protobuf.Parser<Blob> parser() {
			return DEFAULT_INSTANCE.getParserForType();
		}
	}

	/**
	 * The Interface BlobHeaderOrBuilder.
	 */
	public interface BlobHeaderOrBuilder extends
			// @@protoc_insertion_point(interface_extends:OSMPBF.BlobHeader)
			com.google.protobuf.MessageLiteOrBuilder {

		/**
		 * <code>required string type = 1;</code>
		 */
		boolean hasType();

		/**
		 * <code>required string type = 1;</code>
		 */
		java.lang.String getType();

		/**
		 * <code>required string type = 1;</code>
		 */
		com.google.protobuf.ByteString getTypeBytes();

		/**
		 * <code>optional bytes indexdata = 2;</code>
		 */
		boolean hasIndexdata();

		/**
		 * <code>optional bytes indexdata = 2;</code>
		 */
		com.google.protobuf.ByteString getIndexdata();

		/**
		 * <code>required int32 datasize = 3;</code>
		 */
		boolean hasDatasize();

		/**
		 * <code>required int32 datasize = 3;</code>
		 */
		int getDatasize();
	}

	/**
	 * Protobuf type {@code OSMPBF.BlobHeader}
	 */
	public static final class BlobHeader
			extends com.google.protobuf.GeneratedMessageLite<BlobHeader, BlobHeader.Builder> implements
			// @@protoc_insertion_point(message_implements:OSMPBF.BlobHeader)
			BlobHeaderOrBuilder {
		
		/**
		 * Instantiates a new blob header.
		 */
		private BlobHeader() {
			type_ = "";
			indexdata_ = com.google.protobuf.ByteString.EMPTY;
		}

		/** The bit field 0. */
		private int bitField0_;
		
		/** The Constant TYPE_FIELD_NUMBER. */
		public static final int TYPE_FIELD_NUMBER = 1;
		
		/** The type. */
		private java.lang.String type_;

		/**
		 * <code>required string type = 1;</code>
		 */
		@java.lang.Override
		public boolean hasType() {
			return (bitField0_ & 0x00000001) == 0x00000001;
		}

		/**
		 * <code>required string type = 1;</code>
		 */
		@java.lang.Override
		public java.lang.String getType() { return type_; }

		/**
		 * <code>required string type = 1;</code>
		 */
		@java.lang.Override
		public com.google.protobuf.ByteString getTypeBytes() {
			return com.google.protobuf.ByteString.copyFromUtf8(type_);
		}

		/**
		 * <code>required string type = 1;</code>
		 */
		private void setType(final java.lang.String value) {
			if (value == null) throw new NullPointerException();
			bitField0_ |= 0x00000001;
			type_ = value;
		}

		/**
		 * <code>required string type = 1;</code>
		 */
		private void clearType() {
			bitField0_ = bitField0_ & ~0x00000001;
			type_ = getDefaultInstance().getType();
		}

		/**
		 * <code>required string type = 1;</code>
		 */
		private void setTypeBytes(final com.google.protobuf.ByteString value) {
			if (value == null) throw new NullPointerException();
			bitField0_ |= 0x00000001;
			type_ = value.toStringUtf8();
		}

		/** The Constant INDEXDATA_FIELD_NUMBER. */
		public static final int INDEXDATA_FIELD_NUMBER = 2;
		
		/** The indexdata. */
		private com.google.protobuf.ByteString indexdata_;

		/**
		 * <code>optional bytes indexdata = 2;</code>
		 */
		@java.lang.Override
		public boolean hasIndexdata() {
			return (bitField0_ & 0x00000002) == 0x00000002;
		}

		/**
		 * <code>optional bytes indexdata = 2;</code>
		 */
		@java.lang.Override
		public com.google.protobuf.ByteString getIndexdata() { return indexdata_; }

		/**
		 * <code>optional bytes indexdata = 2;</code>
		 */
		private void setIndexdata(final com.google.protobuf.ByteString value) {
			if (value == null) throw new NullPointerException();
			bitField0_ |= 0x00000002;
			indexdata_ = value;
		}

		/**
		 * <code>optional bytes indexdata = 2;</code>
		 */
		private void clearIndexdata() {
			bitField0_ = bitField0_ & ~0x00000002;
			indexdata_ = getDefaultInstance().getIndexdata();
		}

		/** The Constant DATASIZE_FIELD_NUMBER. */
		public static final int DATASIZE_FIELD_NUMBER = 3;
		
		/** The datasize. */
		private int datasize_;

		/**
		 * <code>required int32 datasize = 3;</code>
		 */
		@java.lang.Override
		public boolean hasDatasize() {
			return (bitField0_ & 0x00000004) == 0x00000004;
		}

		/**
		 * <code>required int32 datasize = 3;</code>
		 */
		@java.lang.Override
		public int getDatasize() { return datasize_; }

		/**
		 * <code>required int32 datasize = 3;</code>
		 */
		private void setDatasize(final int value) {
			bitField0_ |= 0x00000004;
			datasize_ = value;
		}

		/**
		 * <code>required int32 datasize = 3;</code>
		 */
		private void clearDatasize() {
			bitField0_ = bitField0_ & ~0x00000004;
			datasize_ = 0;
		}

		@java.lang.Override
		public void writeTo(final com.google.protobuf.CodedOutputStream output) throws java.io.IOException {
			if ((bitField0_ & 0x00000001) == 0x00000001) { output.writeString(1, getType()); }
			if ((bitField0_ & 0x00000002) == 0x00000002) { output.writeBytes(2, indexdata_); }
			if ((bitField0_ & 0x00000004) == 0x00000004) { output.writeInt32(3, datasize_); }
			unknownFields.writeTo(output);
		}

		@java.lang.Override
		public int getSerializedSize() {
			int size = memoizedSerializedSize;
			if (size != -1) return size;

			size = 0;
			if ((bitField0_ & 0x00000001) == 0x00000001) {
				size += com.google.protobuf.CodedOutputStream.computeStringSize(1, getType());
			}
			if ((bitField0_ & 0x00000002) == 0x00000002) {
				size += com.google.protobuf.CodedOutputStream.computeBytesSize(2, indexdata_);
			}
			if ((bitField0_ & 0x00000004) == 0x00000004) {
				size += com.google.protobuf.CodedOutputStream.computeInt32Size(3, datasize_);
			}
			size += unknownFields.getSerializedSize();
			memoizedSerializedSize = size;
			return size;
		}

		/**
		 * Parses the from.
		 *
		 * @param data the data
		 * @return the blob header
		 * @throws InvalidProtocolBufferException the invalid protocol buffer exception
		 */
		public static BlobHeader parseFrom(final java.nio.ByteBuffer data)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return com.google.protobuf.GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
		}

		/**
		 * Parses the from.
		 *
		 * @param data the data
		 * @param extensionRegistry the extension registry
		 * @return the blob header
		 * @throws InvalidProtocolBufferException the invalid protocol buffer exception
		 */
		public static BlobHeader parseFrom(final java.nio.ByteBuffer data,
				final com.google.protobuf.ExtensionRegistryLite extensionRegistry)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return com.google.protobuf.GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
		}

		/**
		 * Parses the from.
		 *
		 * @param data the data
		 * @return the blob header
		 * @throws InvalidProtocolBufferException the invalid protocol buffer exception
		 */
		public static BlobHeader parseFrom(final com.google.protobuf.ByteString data)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return com.google.protobuf.GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
		}

		/**
		 * Parses the from.
		 *
		 * @param data the data
		 * @param extensionRegistry the extension registry
		 * @return the blob header
		 * @throws InvalidProtocolBufferException the invalid protocol buffer exception
		 */
		public static BlobHeader parseFrom(final com.google.protobuf.ByteString data,
				final com.google.protobuf.ExtensionRegistryLite extensionRegistry)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return com.google.protobuf.GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
		}

		/**
		 * Parses the from.
		 *
		 * @param data the data
		 * @return the blob header
		 * @throws InvalidProtocolBufferException the invalid protocol buffer exception
		 */
		public static BlobHeader parseFrom(final byte[] data)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return com.google.protobuf.GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data);
		}

		/**
		 * Parses the from.
		 *
		 * @param data the data
		 * @param extensionRegistry the extension registry
		 * @return the blob header
		 * @throws InvalidProtocolBufferException the invalid protocol buffer exception
		 */
		public static BlobHeader parseFrom(final byte[] data,
				final com.google.protobuf.ExtensionRegistryLite extensionRegistry)
				throws com.google.protobuf.InvalidProtocolBufferException {
			return com.google.protobuf.GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, data, extensionRegistry);
		}

		/**
		 * Parses the from.
		 *
		 * @param input the input
		 * @return the blob header
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public static BlobHeader parseFrom(final java.io.InputStream input) throws java.io.IOException {
			return com.google.protobuf.GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
		}

		/**
		 * Parses the from.
		 *
		 * @param input the input
		 * @param extensionRegistry the extension registry
		 * @return the blob header
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public static BlobHeader parseFrom(final java.io.InputStream input,
				final com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
			return com.google.protobuf.GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
		}

		/**
		 * Parses the delimited from.
		 *
		 * @param input the input
		 * @return the blob header
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public static BlobHeader parseDelimitedFrom(final java.io.InputStream input) throws java.io.IOException {
			return parseDelimitedFrom(DEFAULT_INSTANCE, input);
		}

		/**
		 * Parses the delimited from.
		 *
		 * @param input the input
		 * @param extensionRegistry the extension registry
		 * @return the blob header
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public static BlobHeader parseDelimitedFrom(final java.io.InputStream input,
				final com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
			return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
		}

		/**
		 * Parses the from.
		 *
		 * @param input the input
		 * @return the blob header
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public static BlobHeader parseFrom(final com.google.protobuf.CodedInputStream input)
				throws java.io.IOException {
			return com.google.protobuf.GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input);
		}

		/**
		 * Parses the from.
		 *
		 * @param input the input
		 * @param extensionRegistry the extension registry
		 * @return the blob header
		 * @throws IOException Signals that an I/O exception has occurred.
		 */
		public static BlobHeader parseFrom(final com.google.protobuf.CodedInputStream input,
				final com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
			return com.google.protobuf.GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, input, extensionRegistry);
		}

		/**
		 * New builder.
		 *
		 * @return the builder
		 */
		public static Builder newBuilder() {
			return BlobHeader.newBuilder();
		}

		/**
		 * New builder.
		 *
		 * @param prototype the prototype
		 * @return the builder
		 */
		public static Builder newBuilder(final BlobHeader prototype) {
			return BlobHeader.newBuilder(prototype);
		}

		/**
		 * Protobuf type {@code OSMPBF.BlobHeader}
		 */
		public static final class Builder extends com.google.protobuf.GeneratedMessageLite.Builder<BlobHeader, Builder>
				implements
				// @@protoc_insertion_point(builder_implements:OSMPBF.BlobHeader)
				BlobHeaderOrBuilder {
			
			/**
			 * Instantiates a new builder.
			 */
			// Construct using BlobHeader.newBuilder()
			private Builder() {
				super(DEFAULT_INSTANCE);
			}

			/**
			 * <code>required string type = 1;</code>
			 */
			@java.lang.Override
			public boolean hasType() {
				return instance.hasType();
			}

			/**
			 * <code>required string type = 1;</code>
			 */
			@java.lang.Override
			public java.lang.String getType() { return instance.getType(); }

			/**
			 * <code>required string type = 1;</code>
			 */
			@java.lang.Override
			public com.google.protobuf.ByteString getTypeBytes() { return instance.getTypeBytes(); }

			/**
			 * <code>required string type = 1;</code>
			 */
			public Builder setType(final java.lang.String value) {
				copyOnWrite();
				instance.setType(value);
				return this;
			}

			/**
			 * <code>required string type = 1;</code>
			 */
			public Builder clearType() {
				copyOnWrite();
				instance.clearType();
				return this;
			}

			/**
			 * <code>required string type = 1;</code>
			 */
			public Builder setTypeBytes(final com.google.protobuf.ByteString value) {
				copyOnWrite();
				instance.setTypeBytes(value);
				return this;
			}

			/**
			 * <code>optional bytes indexdata = 2;</code>
			 */
			@java.lang.Override
			public boolean hasIndexdata() {
				return instance.hasIndexdata();
			}

			/**
			 * <code>optional bytes indexdata = 2;</code>
			 */
			@java.lang.Override
			public com.google.protobuf.ByteString getIndexdata() { return instance.getIndexdata(); }

			/**
			 * <code>optional bytes indexdata = 2;</code>
			 */
			public Builder setIndexdata(final com.google.protobuf.ByteString value) {
				copyOnWrite();
				instance.setIndexdata(value);
				return this;
			}

			/**
			 * <code>optional bytes indexdata = 2;</code>
			 */
			public Builder clearIndexdata() {
				copyOnWrite();
				instance.clearIndexdata();
				return this;
			}

			/**
			 * <code>required int32 datasize = 3;</code>
			 */
			@java.lang.Override
			public boolean hasDatasize() {
				return instance.hasDatasize();
			}

			/**
			 * <code>required int32 datasize = 3;</code>
			 */
			@java.lang.Override
			public int getDatasize() { return instance.getDatasize(); }

			/**
			 * <code>required int32 datasize = 3;</code>
			 */
			public Builder setDatasize(final int value) {
				copyOnWrite();
				instance.setDatasize(value);
				return this;
			}

			/**
			 * <code>required int32 datasize = 3;</code>
			 */
			public Builder clearDatasize() {
				copyOnWrite();
				instance.clearDatasize();
				return this;
			}

			// @@protoc_insertion_point(builder_scope:OSMPBF.BlobHeader)
		}

		/** The memoized is initialized. */
		private byte memoizedIsInitialized = 2;

		@java.lang.Override
		@java.lang.SuppressWarnings ({ "unchecked", "fallthrough" })
		protected java.lang.Object dynamicMethod(final com.google.protobuf.GeneratedMessageLite.MethodToInvoke method,
				final java.lang.Object arg0, final java.lang.Object arg1) {
			switch (method) {
				case NEW_MUTABLE_INSTANCE: {
					return new BlobHeader();
				}
				case NEW_BUILDER: {
					return new Builder();
				}
				case IS_INITIALIZED: {
					final byte isInitialized = memoizedIsInitialized;
					if (isInitialized == 1) return DEFAULT_INSTANCE;
					if ((isInitialized == 0) || !hasType() || !hasDatasize()) return null;
					return DEFAULT_INSTANCE;

				}
				case MAKE_IMMUTABLE: {
					return null;
				}
				case VISIT: {
					final Visitor visitor = (Visitor) arg0;
					final BlobHeader other = (BlobHeader) arg1;
					type_ = visitor.visitString(hasType(), type_, other.hasType(), other.type_);
					indexdata_ =
							visitor.visitByteString(hasIndexdata(), indexdata_, other.hasIndexdata(), other.indexdata_);
					datasize_ = visitor.visitInt(hasDatasize(), datasize_, other.hasDatasize(), other.datasize_);
					if (visitor == com.google.protobuf.GeneratedMessageLite.MergeFromVisitor.INSTANCE) {
						bitField0_ |= other.bitField0_;
					}
					return this;
				}
				case MERGE_FROM_STREAM: {
					final com.google.protobuf.CodedInputStream input = (com.google.protobuf.CodedInputStream) arg0;
					final com.google.protobuf.ExtensionRegistryLite extensionRegistry =
							(com.google.protobuf.ExtensionRegistryLite) arg1;
					if (extensionRegistry == null) throw new java.lang.NullPointerException();
					try {
						boolean done = false;
						while (!done) {
							final int tag = input.readTag();
							switch (tag) {
								case 0:
									done = true;
									break;
								case 10: {
									final java.lang.String s = input.readString();
									bitField0_ |= 0x00000001;
									type_ = s;
									break;
								}
								case 18: {
									bitField0_ |= 0x00000002;
									indexdata_ = input.readBytes();
									break;
								}
								case 24: {
									bitField0_ |= 0x00000004;
									datasize_ = input.readInt32();
									break;
								}
								default: {
									if (!parseUnknownField(tag, input)) { done = true; }
									break;
								}
							}
						}
					} catch (final com.google.protobuf.InvalidProtocolBufferException e) {
						throw new RuntimeException(e.setUnfinishedMessage(this));
					} catch (final java.io.IOException e) {
						throw new RuntimeException(
								new com.google.protobuf.InvalidProtocolBufferException(e.getMessage())
										.setUnfinishedMessage(this));
					} finally {}
				}
				// fall through
				case GET_DEFAULT_INSTANCE: {
					return DEFAULT_INSTANCE;
				}
				case GET_PARSER: {
					com.google.protobuf.Parser<BlobHeader> parser = PARSER;
					if (parser == null) {
						synchronized (BlobHeader.class) {
							parser = PARSER;
							if (parser == null) {
								parser = new DefaultInstanceBasedParser(DEFAULT_INSTANCE);
								PARSER = parser;
							}
						}
					}
					return parser;
				}
				case GET_MEMOIZED_IS_INITIALIZED: {
					return memoizedIsInitialized;
				}
				case SET_MEMOIZED_IS_INITIALIZED: {
					memoizedIsInitialized = (byte) (arg0 == null ? 0 : 1);
					return null;
				}
			}
			throw new UnsupportedOperationException();
		}

		/** The Constant DEFAULT_INSTANCE. */
		// @@protoc_insertion_point(class_scope:OSMPBF.BlobHeader)
		private static final BlobHeader DEFAULT_INSTANCE;
		static {
			// New instances are implicitly immutable so no need to make
			// immutable.
			DEFAULT_INSTANCE = new BlobHeader();
		}

		/**
		 * Gets the default instance.
		 *
		 * @return the default instance
		 */
		public static BlobHeader getDefaultInstance() { return DEFAULT_INSTANCE; }

		/** The parser. */
		private static volatile com.google.protobuf.Parser<BlobHeader> PARSER;

		/**
		 * Parser.
		 *
		 * @return the com.google.protobuf. parser
		 */
		public static com.google.protobuf.Parser<BlobHeader> parser() {
			return DEFAULT_INSTANCE.getParserForType();
		}
	}

	static {}

	// @@protoc_insertion_point(outer_class_scope)
}
