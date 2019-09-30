/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hufflzw.huffman;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author samuelweber, gisela and dinah
 */
public class HuffmanDecoder {
    protected final HFreqTable freqTable;
    protected final HuffmanTree htree;
    protected final Map<Long, Integer> symMap;

    /**
     * A constructor of huffman decoder.
     * Note, that the frequency table must be the same
     * that was used to encode data.
     * @param freqTable	a frequency table that will be used to decode data
     */
    public HuffmanDecoder(HFreqTable freqTable) {
        this.freqTable = freqTable;
        htree = new HuffmanTree(freqTable);
        symMap = new HashMap<Long, Integer>();
        fillSymMap(htree.getRoot(), 0, 0);
    }

    /**
     * Check whether a bit string of given length can
     * be decoded by the decoder
     * @param bitString	a bit string
     * @param length	a length of the bit string
     * @return	true if decoder can decode the bitstring
     */
    public boolean hasCode(int bitString, int length) {
        return symMap.containsKey(bitStringToKey(bitString, length));
    }

    /**
     * Decode the huffman code contained in bit string of
     * given length
     * @param bitString	a bit string (huffman code)
     * @param length	a length of the bit string
     * @return	decoded symbol
     */
    public int decode(int bitString, int length) {
        return symMap.get(bitStringToKey(bitString, length));
    }

    /**
     * @return huffman tree of the decoder
     * @see HuffmanTree
     */
    public final HuffmanTree getTree() {
        return htree;
    }

    protected long bitStringToKey(int bitString, int length) {
        return (long)bitString | ((long)length << 32); 
    }

    // build a table of huffman codes and correspongin symbols by
    // iterating over huffman tree.
    protected void fillSymMap(HuffmanNode hnode, int bitString, int length) {
        if (!hnode.isLeaf()) {
            fillSymMap(hnode.getLeft(), bitString, length + 1);
            fillSymMap(hnode.getRight(), bitString | (1 << length), length + 1);
        }
        else {
            // Huffman code is encoeded to 64 bit word, where lower 32 bits
            // store the binary huffman code and higher 32 bits store its length.
            symMap.put(bitStringToKey(bitString, length), hnode.getSymbol());
        }
    }
}
