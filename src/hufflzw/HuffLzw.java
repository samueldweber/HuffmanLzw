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
import java.nio.file.Files;
import java.util.zip.CRC32;

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
        final byte[] data = getData(args[1]);
        final String crc = crc(data);
        InputStream bin = new ByteArrayInputStream(lzwComp(data));
        HuffmanOutputStream hout = new HuffmanOutputStream(new FileOutputStream(outFile));
        byte buf[] = new byte[4096];
        int len;

        while ((len = bin.read(buf)) != -1)
            hout.write(buf, 0, len);

        bin.close();
        hout.close();

        System.out.println("Compressao: pronta");
        System.out.println("Tamanho original:     " + inFile.length());
        System.out.println("Tamanho comprimido:   " + outFile.length());
        System.out.println("CRC: " + crc);
        System.out.print("Eficiencia de compressao: ");
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
            
            final byte[] data = lzwDecomp(bout.toByteArray());
            
            
            File outFile = generateFile(data, args[2]);
            final String crc = crc(data);
            System.out.println("Decompressao: pronta");
            System.out.println("Tamanho original:     " + inFile.length());
            System.out.println("Tamanho decomprimido: " + outFile.length());
            System.out.println("CRC: " + crc);
    }

    public static void usage() {
            System.err.println("USo: HuffmanLzw c|d|e");
            System.err.println("       c <arq_p_comp> <arq_saida>: " +
                    "codifica arquivo e salva");
            System.err.println("                        " +
                    "os resultados para o arquivo de saida");
            System.err.println("       d <arq_p_decomp> <arq_saida>: " +
                    "decodificia arquivo e salva");
            System.err.println("                        " +
                    "os resultados para o arquivo de saida");
            System.exit(1);
    }
    
    public static byte[] lzwComp(final byte[] data) {
        byte[] lzwc = lzw.encode(data);
        return lzwc;
    }
    
    public static byte[] lzwDecomp(final byte[] content) throws IOException {
        byte[] lzwd = lzw.decode(content);
        return lzwd;
    }
    
    public static byte[] getData(final String path) throws FileNotFoundException, IOException {
        File inFile = new File(path);
        final byte[] out = Files.readAllBytes(inFile.toPath());
        return out;
    }
    
    public static File generateFile(final byte[] data, final String name) throws IOException {
        
        final File out = new File(name);
        
        try (final OutputStream os = new FileOutputStream(out)) {
            os.write(data);
            os.close();
        }
        
        return out;
    }
    
    public static String crc(final byte[] data) {

        final CRC32 crc32 = new CRC32();
        crc32.update(data);
        return String.valueOf(crc32.getValue());
    }
    
}
