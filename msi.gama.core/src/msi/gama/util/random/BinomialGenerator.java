/*********************************************************************************************
 *
 *
 * 'BinomialGenerator.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit http://gama-platform.googlecode.com for license information and developers contact.
 *
 *
 **********************************************************************************************/
// Copyright 2006-2010 Daniel W. Dyer
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
package msi.gama.util.random;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;

/**
 * Discrete random sequence that follows a
 * <a href="http://en.wikipedia.org/wiki/Binomial_distribution" target="_top">binomial
 * distribution</a>.
 * @author Daniel Dyer
 */
public class BinomialGenerator implements NumberGenerator<Integer> {

	private final Random rng;
	private final NumberGenerator<Integer> n;
	private final NumberGenerator<Double> p;

	// Cache the fixed-point representation of p to avoid having to
	// recalculate it for each value generated. Only calculate it
	// if and when p changes.
	private transient BitString pBits;
	private transient double lastP;

	public final class BitString implements Cloneable, Serializable {

		private static final int WORD_LENGTH = 32;

		private final int length;

		/**
		 * Store the bits packed in an array of 32-bit ints. This field cannot
		 * be declared final because it must be cloneable.
		 */
		private int[] data;

		/**
		 * Creates a bit string of the specified length with all bits
		 * initially set to zero (off).
		 * @param length The number of bits.
		 */
		public BitString(final int length) {
			if ( length < 0 ) { throw new IllegalArgumentException("Length must be non-negative."); }
			this.length = length;
			this.data = new int[(length + WORD_LENGTH - 1) / WORD_LENGTH];
		}

		/**
		 * Creates a bit string of the specified length with each bit set
		 * randomly (the distribution of bits is uniform so long as the output
		 * from the provided RNG is also uniform). Using this constructor is
		 * more efficient than creating a bit string and then randomly setting
		 * each bit individually.
		 * @param length The number of bits.
		 * @param rng A source of randomness.
		 */
		public BitString(final int length, final Random rng) {
			this(length);
			// We can set bits 32 at a time rather than calling rng.nextBoolean()
			// and setting each one individually.
			for ( int i = 0; i < data.length; i++ ) {
				data[i] = rng.nextInt();
			}
			// If the last word is not fully utilised, zero any out-of-bounds bits.
			// This is necessary because the countSetBits() methods will count
			// out-of-bounds bits.
			int bitsUsed = length % WORD_LENGTH;
			if ( bitsUsed < WORD_LENGTH ) {
				int unusedBits = WORD_LENGTH - bitsUsed;
				int mask = 0xFFFFFFFF >>> unusedBits;
			data[data.length - 1] &= mask;
			}
		}

		/**
		 * Initialises the bit string from a character string of 1s and 0s
		 * in big-endian order.
		 * @param value A character string of ones and zeros.
		 */
		public BitString(final String value) {
			this(value.length());
			for ( int i = 0; i < value.length(); i++ ) {
				if ( value.charAt(i) == '1' ) {
					setBit(value.length() - (i + 1), true);
				} else if ( value.charAt(i) != '0' ) { throw new IllegalArgumentException(
					"Illegal character at position " + i); }
			}
		}

		/**
		 * @return The length of this bit string.
		 */
		public int getLength() {
			return length;
		}

		/**
		 * Returns the bit at the specified index.
		 * @param index The index of the bit to look-up (0 is the least-significant bit).
		 * @return A boolean indicating whether the bit is set or not.
		 * @throws IndexOutOfBoundsException If the specified index is not a bit
		 *             position in this bit string.
		 */
		public boolean getBit(final int index) {
			assertValidIndex(index);
			int word = index / WORD_LENGTH;
			int offset = index % WORD_LENGTH;
			return (data[word] & 1 << offset) != 0;
		}

		/**
		 * Sets the bit at the specified index.
		 * @param index The index of the bit to set (0 is the least-significant bit).
		 * @param set A boolean indicating whether the bit should be set or not.
		 * @throws IndexOutOfBoundsException If the specified index is not a bit
		 *             position in this bit string.
		 */
		public void setBit(final int index, final boolean set) {
			assertValidIndex(index);
			int word = index / WORD_LENGTH;
			int offset = index % WORD_LENGTH;
			if ( set ) {
				data[word] |= 1 << offset;
			} else // Unset the bit.
			{
				data[word] &= ~(1 << offset);
			}
		}

		/**
		 * Inverts the value of the bit at the specified index.
		 * @param index The bit to flip (0 is the least-significant bit).
		 * @throws IndexOutOfBoundsException If the specified index is not a bit
		 *             position in this bit string.
		 */
		public void flipBit(final int index) {
			assertValidIndex(index);
			int word = index / WORD_LENGTH;
			int offset = index % WORD_LENGTH;
			data[word] ^= 1 << offset;
		}

		/**
		 * Helper method to check whether a bit index is valid or not.
		 * @param index The index to check.
		 * @throws IndexOutOfBoundsException If the index is not valid.
		 */
		private void assertValidIndex(final int index) {
			if ( index >= length || index < 0 ) { throw new IndexOutOfBoundsException("Invalid index: " + index +
				" (length: " + length + ")"); }
		}

		/**
		 * @return The number of bits that are 1s rather than 0s.
		 */
		public int countSetBits() {
			int count = 0;
			for ( int x : data ) {
				while (x != 0) {
					x &= x - 1; // Unsets the least significant on bit.
					++count; // Count how many times we have to unset a bit before x equals zero.
				}
			}
			return count;
		}

		/**
		 * @return The number of bits that are 0s rather than 1s.
		 */
		public int countUnsetBits() {
			return length - countSetBits();
		}

		/**
		 * Interprets this bit string as being a binary numeric value and returns
		 * the integer that it represents.
		 * @return A {@link BigInteger} that contains the numeric value represented
		 *         by this bit string.
		 */
		public BigInteger toNumber() {
			return new BigInteger(toString(), 2);
		}

		/**
		 * An efficient method for exchanging data between two bit strings. Both bit strings must
		 * be long enough that they contain the full length of the specified substring.
		 * @param other The bitstring with which this bitstring should swap bits.
		 * @param start The start position for the substrings to be exchanged. All bit
		 *            indices are big-endian, which means position 0 is the rightmost bit.
		 * @param length The number of contiguous bits to swap.
		 */
		public void swapSubstring(final BitString other, final int start, final int length) {
			assertValidIndex(start);
			other.assertValidIndex(start);

			int word = start / WORD_LENGTH;

			int partialWordSize = (WORD_LENGTH - start) % WORD_LENGTH;
			if ( partialWordSize > 0 ) {
				swapBits(other, word, 0xFFFFFFFF << WORD_LENGTH - partialWordSize);
				++word;
			}

			int remainingBits = length - partialWordSize;
			int stop = remainingBits / WORD_LENGTH;
			for ( int i = word; i < stop; i++ ) {
				int temp = data[i];
				data[i] = other.data[i];
				other.data[i] = temp;
			}

			remainingBits %= WORD_LENGTH;
			if ( remainingBits > 0 ) {
				swapBits(other, word, 0xFFFFFFFF >>> WORD_LENGTH - remainingBits);
			}
		}

		/**
		 * @param other The BitString to exchange bits with.
		 * @param word The word index of the word that will be swapped between the two bit strings.
		 * @param swapMask A mask that specifies which bits in the word will be swapped.
		 */
		private void swapBits(final BitString other, final int word, final int swapMask) {
			int preserveMask = ~swapMask;
			int preservedThis = data[word] & preserveMask;
			int preservedThat = other.data[word] & preserveMask;
			int swapThis = data[word] & swapMask;
			int swapThat = other.data[word] & swapMask;
			data[word] = preservedThis | swapThat;
			other.data[word] = preservedThat | swapThis;
		}

		/**
		 * Creates a textual representation of this bit string in big-endian
		 * order (index 0 is the right-most bit).
		 * @return This bit string rendered as a String of 1s and 0s.
		 */
		@Override
		public String toString() {
			StringBuilder buffer = new StringBuilder();
			for ( int i = length - 1; i >= 0; i-- ) {
				buffer.append(getBit(i) ? '1' : '0');
			}
			return buffer.toString();
		}

		/**
		 * @return An identical copy of this bit string.
		 */
		@Override
		public BitString clone() {
			try {
				BitString clone = (BitString) super.clone();
				clone.data = data.clone();
				return clone;
			} catch (CloneNotSupportedException ex) {
				// Not possible.
				throw (Error) new InternalError("Cloning failed.").initCause(ex);
			}
		}

		/**
		 * @return True if the argument is a BitString instance and both bit
		 *         strings are the same length with identical bits set/unset.
		 */
		@Override
		public boolean equals(final Object o) {
			if ( this == o ) { return true; }
			if ( o == null || getClass() != o.getClass() ) { return false; }

			BitString bitString = (BitString) o;

			return length == bitString.length && Arrays.equals(data, bitString.data);
		}

		/**
		 * Over-ridden to be consistent with {@link #equals(Object)}.
		 */
		@Override
		public int hashCode() {
			int result = length;
			result = 31 * result + Arrays.hashCode(data);
			return result;
		}
	}

	/**
	 * <p>
	 * Creates a generator of binomially-distributed values. The number of trials ({@literal n}) and the probability of success in each trial ({@literal p}) are determined by the provided
	 * {@link NumberGenerator}s. This means that the statistical parameters of this generator may change over time. One example of where this is useful is if the {@literal n} and {@literal p}
	 * generators are attached to GUI controls that allow a user to tweak the parameters while a program is running.
	 * </p>
	 * <p>
	 * To create a Binomial generator with a constant {@literal n} and {@literal p}, use the {@link #BinomialGenerator(int, double, Random)} constructor instead.
	 * </p>
	 * @param n A {@link NumberGenerator} that provides the number of trials for
	 *            the Binomial distribution used for the next generated value. This generator
	 *            must produce only positive values.
	 * @param p A {@link NumberGenerator} that provides the probability of succes
	 *            in a single trial for the Binomial distribution used for the next
	 *            generated value. This generator must produce only values in the range 0 - 1.
	 * @param rng The source of randomness.
	 */
	public BinomialGenerator(final NumberGenerator<Integer> n, final NumberGenerator<Double> p, final Random rng) {
		this.n = n;
		this.p = p;
		this.rng = rng;
	}

	/**
	 * Creates a generator of binomially-distributed values from a distribution
	 * with the specified parameters.
	 * @param n The number of trials (and therefore the maximum possible value returned
	 *            by this sequence).
	 * @param p The probability (between 0 and 1) of success in any one trial.
	 * @param rng The source of randomness used to generate the binomial values.
	 */
	public BinomialGenerator(final int n, final double p, final Random rng) {
		this(new ConstantGenerator<Integer>(n), new ConstantGenerator<Double>(p), rng);
		if ( n <= 0 ) { throw new IllegalArgumentException("n must be a positive integer."); }
		if ( p <= 0 || p >= 1 ) { throw new IllegalArgumentException("p must be between 0 and 1."); }
	}

	/**
	 * Generate the next binomial value from the current values of {@literal n} and {@literal p}.
	 * The algorithm used is from
	 * The Art of Computer Programming Volume 2 (Seminumerical Algorithms)
	 * by Donald Knuth (page 589 in the Third Edition) where it is
	 * credited to J.H. Ahrens.
	 */
	@Override
	public Integer nextValue() {
		// Regenerate the fixed point representation of p if it has changed.
		double newP = p.nextValue();
		if ( pBits == null || newP != lastP ) {
			lastP = newP;
			final double value = newP;
			if ( value < 0.0d || value >= 1.0d ) { throw new IllegalArgumentException("Value must be between 0 and 1."); }
			StringBuilder bits = new StringBuilder(64);
			double bitValue = 0.5d;
			double d = value;
			while (d > 0) {
				if ( d >= bitValue ) {
					bits.append('1');
					d -= bitValue;
				} else {
					bits.append('0');
				}
				bitValue /= 2;
			}
			pBits = new BitString(bits.toString());
		}

		int trials = n.nextValue();
		int totalSuccesses = 0;
		int pIndex = pBits.getLength() - 1;

		while (trials > 0 && pIndex >= 0) {
			int successes = binomialWithEvenProbability(trials);
			trials -= successes;
			if ( pBits.getBit(pIndex) ) {
				totalSuccesses += successes;
			}
			--pIndex;
		}

		return totalSuccesses;
	}

	/**
	 * Generating binomial values when {@literal p = 0.5} is straightforward.
	 * It simply a case of generating {@literal n} random bits and
	 * counting how many are 1s.
	 * @param n The number of trials.
	 * @return The number of successful outcomes from {@literal n} trials.
	 */
	private int binomialWithEvenProbability(final int n) {
		BitString bits = new BitString(n, rng);
		return bits.countSetBits();
	}
}
