/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hufflzw;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import hufflzw.huffman.HuffmanInputStream;
import hufflzw.huffman.HuffmanOutputStream;
import hufflzw.huffman.HFreqTable;
import hufflzw.lzw.LZW;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author samuelweber
 */
public class HuffLzw {
    
    private static final LZW lzw = new LZW();

    public static void main(String args[]) throws IOException {
        
        
        if (args.length < 1)
            usage();

        try {
            if (args[0].equals("c"))
                doEncode(args);
            else if (args[0].equals("d"))
                doDecode(args);
            else if (args[0].equals("e"))
                calcEntropy(args);
            else
                usage();
        } catch (FileNotFoundException err) {
            System.err.println("Error: " + err.toString());
            usage();
        }

        System.exit(0);
    }

    public static void doEncode(String[] args) throws IOException {
        if (args.length < 3)
                usage();

        File inFile = new File(args[1]);
        File outFile = new File(args[2]);
        InputStream bin = new ByteArrayInputStream(lzwComp(args[1]));
        HuffmanOutputStream hout = new HuffmanOutputStream(new FileOutputStream(outFile));
        byte buf[] = new byte[4096];
        int len;

        while ((len = bin.read(buf)) != -1)
            hout.write(buf, 0, len);

        bin.close();
        hout.close();

        System.out.println("Compression: done");
        System.out.println("Original file size:     " + inFile.length());
        System.out.println("Compressed file size:   " + outFile.length());
        System.out.print("Compression efficiency: ");
        if (inFile.length() > outFile.length()) {
            System.out.format("%.2f%%\n", (100.0 - (((double) outFile.length() / (double) inFile.length()) * 100)));
        }
        else
            System.out.println("none");
    }

    public static void doDecode(String[] args) throws IOException {
            if (args.length < 3)
                usage();

            File inFile = new File(args[1]);
            
            HuffmanInputStream hin = new HuffmanInputStream(new FileInputStream(inFile));
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            
            byte buf[] = new byte[4096];
            int len;

            while ((len = hin.read(buf)) != -1)
                bout.write(buf, 0, len);

            hin.close();
            bout.close();
            
            File outFile = lzwDecomp(bout.toByteArray(), args[2]);
            System.out.println("Decompression: done");
            System.out.println("Original file size:     " + inFile.length());
            System.out.println("Decompressed file size: " + outFile.length());
    }

    public static void calcEntropy(String[] args) throws IOException {
            if (args.length < 2)
                    usage();

            InputStream in = new FileInputStream(args[1]);
            HFreqTable ftbl = new HFreqTable();
            int sym;

            while ((sym = in.read()) != -1)
                ftbl.add(sym);

            in.close();
            System.out.format("Entropy: %.2f\n", ftbl.entropy());
    }

    public static void usage() {
            System.err.println("USAGE: HuffmanDemo c|d|e");
            System.err.println("       c <input-file> <output-file>: " +
                    "encode input file and save");
            System.err.println("                        " +
                    "the results to output file");
            System.err.println("       d <input-file> <output-file>: " +
                    "decode input file and save");
            System.err.println("                        " +
                    "the results to output file");
            System.err.println("       e <input-file>: calculate an " +
                    "entropy of the symbols in input file");
            System.exit(1);
    }
    
    public static byte[] lzwComp(final String path) throws FileNotFoundException, IOException {
        File inFile = new File(path);
        final FileInputStream fos = new FileInputStream(inFile);
        byte[] lzwc = lzw.encode(IOUtils.toByteArray(fos));
        return lzwc;
    }
    
    public static File lzwDecomp(final byte[] content, String name) throws IOException {
        byte[] lzwd = lzw.decode(content);
        return generateFile(lzwd, name);
    }
    
    public static File generateFile(final byte[] data, final String name) throws IOException {
        
        final File out = new File(name);
        
        try (final OutputStream os = new FileOutputStream(out)) {
            os.write(data);
            os.close();
        }
        
        return out;
    }
    
}
