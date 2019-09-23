/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hufflzw.huffman;

/**
 *
 * @author samuelweber
 */
public class HuffMarkers {
    public final static int DEFAULT_SEGMENT_SIZE_KB = 200;
    public final static int HEADER_START = 0xABCDEF01;
    public final static int HEADER_END = 0xABCDEF0A;
    public final static int HUFFMAN_MAGIC3 = 0xABCDEF0F;
}
