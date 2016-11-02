/*********************************************************************************************
 *
 * 'CSVTokenScanner.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.viewers.csv.text;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;

import ummisco.gama.ui.viewers.csv.text.CSVToken.CSVTokenType;

public class CSVTokenScanner implements ITokenScanner {

    /** Delimiter */
    private final char m_delimiter;

    /** Document */
    private IDocument m_document;

    /** Document initial offset */
    private int m_docOffset;

    /** Document range */
    private int m_docLength;

    /** Current offset */
    private int m_currentOffset;

    /** Current column */
    private int m_currentColumn;

    /** Current token offset*/
    private int m_tokenOffset;

    /** Current token length */
    private int m_tokenLength;

    /**
     * Constructor
     *
     * @param separator
     *            the separator to use for scanning tokens
     */
    public CSVTokenScanner(char separator) {
        m_delimiter = separator;
    }

    public void setRange(IDocument document, int offset, int length) {
        m_document = document;
        m_docOffset = offset;
        m_docLength = length;

        // Initialize current offset and current column
        m_currentColumn = 0;
        m_currentOffset = offset;

        m_tokenOffset = offset;
        m_tokenLength = 0;
    }

    public IToken nextToken() {
        if (m_currentOffset >= (m_docOffset + m_docLength)) {
            return Token.EOF;
        }

        IToken result = Token.UNDEFINED;
        int startOffset = m_currentOffset;
        int length = 0;
        try {
            // Get line information
            IRegion lineRegion = m_document.getLineInformationOfOffset(m_currentOffset);
            int lineMinOffset = lineRegion.getOffset();
            int lineMaxOffset = lineMinOffset + lineRegion.getLength();
            // Read char
            char current = m_document.getChar(m_currentOffset++);
            length++;
            // Check if we are at the end of the line
            if (current == '\n')
            {
                result = Token.WHITESPACE;
                m_tokenOffset = startOffset;
                m_tokenLength = length;
                m_currentColumn = 0;
            }
            else if (current == m_delimiter) {
                result = new CSVToken(CSVTokenType.SEPARATOR,
                                      m_currentColumn++);
                m_tokenOffset = startOffset;
                m_tokenLength = length;
                //System.out.println("Token found [" + m_document.get(m_tokenOffset, m_tokenLength) + "]");
            } else {
                // Look for the next delimiter or the next line
                boolean scan = true;
                while (scan) {
                    // check if we are at the end of the line
                    if (m_currentOffset >= lineMaxOffset) {
                        CSVTokenType type = ((m_currentColumn % 2) == 0) ? CSVTokenType.ODD_COLUMN : CSVTokenType.EVEN_COLUMN;
                        result = new CSVToken(type, m_currentColumn);

                        m_tokenOffset = startOffset;
                        m_tokenLength = length;
                        m_currentColumn= 0;
                        //System.out.println("Token found [" + m_document.get(m_tokenOffset, m_tokenLength) + "]");
                        scan = false;
                    }
                    else
                    {
                        // iterate until a delimiter is found
                        char next = m_document.getChar(m_currentOffset);
                        if (next == m_delimiter) {
                            CSVTokenType type = ((m_currentColumn % 2) == 0) ? CSVTokenType.ODD_COLUMN : CSVTokenType.EVEN_COLUMN;
                            result = new CSVToken(type, m_currentColumn);

                            m_tokenOffset = startOffset;
                            m_tokenLength = length;
                            //System.out.println("Token found [" + m_document.get(m_tokenOffset, m_tokenLength) + "]");
                            scan = false;
                        }
                        else
                        {
                            m_currentOffset++;
                            length++;
                        }
                    }
                }
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
            result = Token.EOF;
        }
        return result;
    }

    public int getTokenOffset() {
        return m_tokenOffset;
    }

    public int getTokenLength() {
        return m_tokenLength;
    }
}
