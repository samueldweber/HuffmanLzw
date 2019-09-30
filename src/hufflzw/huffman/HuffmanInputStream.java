/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hufflzw.huffman;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author samuelweber, gisela and dinah
 */
public class HuffmanInputStream extends FilterInputStream {
    protected byte[] segment;
    protected int bytesRead;
    protected boolean eof;

    /**
     * Initialize the huffman input stream.
     * @param in	parent input stream
     */
    public HuffmanInputStream(InputStream in) {
            super(in);
            bytesRead = 0;
            segment = null;
            eof = false;
    }

    @Override
    public int read() throws IOException {
        if (segment == null)
            readSegment();
        if (segment == null && eof)
            return -1;

        int ret = segment[bytesRead++];
        if (bytesRead == segment.length) {
            bytesRead = 0;
            segment = null;
        }

        return (ret & 0xff);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int rdLen;
        for (rdLen = 0; rdLen < len; rdLen++) {
            int val = read();
            if (val == -1) {
                if (rdLen == 0)
                    return -1;

                break;
            }

            b[off + rdLen] = (byte) val;
        }

        return rdLen;
    }

    // read next huffman segment
    protected void readSegment() throws IOException {
        DataInputStream dataIn = new DataInputStream(in);

        try {
            // ensure that segment header is valid
            // and read it (segment size and frequency table
            int magic = dataIn.readInt();
            if (magic != HuffMarkers.HEADER_START) {
                throw new IOException("Can't read segment header: magic1 mismatch");
            }

            int segSz = dataIn.readInt();
            segment = new byte[segSz];
            HFreqTable freqTable = HFreqTable.restore(dataIn);

            magic = dataIn.readInt();
            if (magic != HuffMarkers.HEADER_END)
                throw new IOException("Can't read segment header: magic2 mismatch");

            // decode the segment
            HuffmanDecoder dec = new HuffmanDecoder(freqTable);
            BitInputStream bitin = new BitInputStream(in);
            int bytesDecoded = 0;
            while (bytesDecoded < segSz) {
                int bitString = 0, length = 0;

                // continuously read the data bit by bit constructing the
                // bit string. On every iteration check whether the constructed
                // bit string of given length is a huffman code known to decoder.
                // If so, decode it, otherwise continue the process of building
                // bit string.
                while (!dec.hasCode(bitString, length)) {
                    int bit = bitin.readBit();
                    if (bit == -1)
                        break;

                    bitString |= (bit << length);
                    length++;
                    if (length >= 32)
                        throw new IOException("Huffman code is too long");
                }
                if (length != 0)
                    segment[bytesDecoded++] = (byte) dec.decode(bitString, length);
                else {
                    eof = true;
                }
            }
        }
        catch (EOFException eoerr) {
            eof = true;
        }
    }
}
