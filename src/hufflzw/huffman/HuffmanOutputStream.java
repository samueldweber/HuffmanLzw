/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hufflzw.huffman;

import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author samuelweber
 */
public class HuffmanOutputStream extends FilterOutputStream {
    protected byte[] segment;
    protected int bytesWritten;

    /**
     * Initialize a huffman output stream.
     * Note: default segment size will be used for buffer
     * @param out	parent output stream
     * @see FilterOutputStream
     * @see HuffMarkers
     */
    public HuffmanOutputStream(OutputStream out) {
        super(out);
        constructHuffman(HuffMarkers.DEFAULT_SEGMENT_SIZE_KB);
    }

    /**
     * Initialize huffman output stream with the given segment
     * size.
     * @param out	parent output stream
     * @param bufsizeKB	segment size in KBs.
     * @see FilterOutputStream
     */
    public HuffmanOutputStream(OutputStream out, int bufsizeKB) {
        super(out);
        constructHuffman(bufsizeKB);
    }

    private void constructHuffman(int segmentSizeKb) {
        segment = new byte[segmentSizeKb * 1024];
        bytesWritten = 0;
    }

    /**
     * @return internal huffer size (in bytes) of HuffmanOutputStream
     */
    public int getBufSize() {
        return segment.length;
    }

    @Override
    public void write(int b) throws IOException {
        segment[bytesWritten++] = (byte) b;
        if (bytesWritten == segment.length)
            writeSegment();
    }

    @Override
    public void flush() throws IOException {
            writeSegment();
    }

    protected void writeSegment() throws IOException {
        if (bytesWritten == 0)
                return;

        // either buffer is overflowed or flush() is called
        // calculate frequencies of buffered data
        HFreqTable freqTable = new HFreqTable();
        for (int i = 0; i < bytesWritten; i++)
                freqTable.add(segment[i]);

        // write segment header at first
        DataOutputStream dataOut = new DataOutputStream(out);
        dataOut.writeInt(HuffMarkers.HEADER_START);
        dataOut.writeInt(bytesWritten);
        freqTable.save(dataOut);
        dataOut.writeInt(HuffMarkers.HEADER_END);

        // then write the encoded data
        BitOutputStream bitout = new BitOutputStream(out);
        HuffmanEncoder enc = new HuffmanEncoder(freqTable);
        for (int i = 0; i < bytesWritten; i++) {
                Pair<Integer, Integer> bstr = enc.encode(segment[i]);
                bitout.write(bstr.getFirst(), bstr.getSecond());
        }

        bitout.flush();
        bytesWritten = 0;
    }
}
