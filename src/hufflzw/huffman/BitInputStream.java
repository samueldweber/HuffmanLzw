/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hufflzw.huffman;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author samuelweber, gisela and dinah
 */
public class BitInputStream {
    protected InputStream in;
    protected int buf;
    protected int bitsInBuf;

    /**
     * Constructor of BitInputStream is very similar
     * to FilteredInputStream.
     * @see FilteredInputStream
     */
    public BitInputStream(InputStream in) {
        this.in = in;
        buf = bitsInBuf = 0;
    }

    /**
     * Read one bit from the stream.
     * @return next bit value or -1 if EOF is reached.
     */
    public int readBit() throws IOException {
        if (bitsInBuf == 0) {
            refreshBuffer();
            if (buf == -1) {
                buf = bitsInBuf = 0;
                return -1;
            }
        }

        int ret = (buf >> (8 - bitsInBuf)) & 0x1;
        bitsInBuf--;
        return ret;
    }

    /**
     * Closes the parent InputStream.
     * @see InputStream.close
     * @throws IOException
     */
    public void close() throws IOException {
        in.close();
    }

    protected void refreshBuffer() throws IOException {
        buf = in.read();
        bitsInBuf = 8;
    }
}
