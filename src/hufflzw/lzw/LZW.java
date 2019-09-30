/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hufflzw.lzw;

import java.util.HashMap;

/**
 *
 * @author samuelweber, gisela and dinah
 */
public class LZW {

    public byte[] encode(byte[] content) {
        // ----------------------------------------------------------------------------------------------------//
        // --------------------------------------VARIAVEIS-----------------------------------------------------//
        // ----------------------------------------------------------------------------------------------------//
        // define a hashmap e outras variaveis que serao utilizadas pelo
        // programa
        HashMap<String, Integer> dictionary = new HashMap<>();
        final StringBuilder builder = new StringBuilder(); // uso de string builder para melhorar performance
        byte inputByte;
        byte[] buffer = new byte[3];
        boolean onleft = true;

        // Dictionary size limit, builds dictionary
        for (int i = 0; i < 256; i++) {
            dictionary.put(Character.toString((char) i), i);
        }

        byte[] saida = new byte[contagemBufferEncode(content)];

        int j = 0;
        inputByte = content[j++];
        int i = new Byte(inputByte).intValue();
        if (i < 0) {
                i += 256;
        }
        char ch = (char) i;
        builder.setLength(0);
        builder.append(ch);

        int k = 0;
        // Le caractere por caractere
        while (true) {
            // Condicao de saida: Simulan um IOException
            if (j >= content.length) {
                String str12bit = to12bit(dictionary.get(builder.toString()));
                if (onleft) {
                    buffer[0] = (byte) Integer.parseInt(str12bit.substring(0, 8), 2);
                    buffer[1] = (byte) Integer.parseInt(str12bit.substring(8, 12) + "0000", 2);
                    saida[k++] = buffer[0];
                    saida[k++] = buffer[1];
                } else {
                    buffer[1] += (byte) Integer.parseInt(str12bit.substring(0, 4), 2);
                    buffer[2] = (byte) Integer.parseInt(str12bit.substring(4, 12), 2);

                    for (int b = 0; b < buffer.length; b++) {
                        saida[k++] = buffer[b];
                        buffer[b] = 0;
                    }
                }

                return saida;
            }
            inputByte = content[j++];
            i = new Byte(inputByte).intValue();

            if (i < 0) {
                i += 256;
            }
            ch = (char) i;

            // Se builder + ch estao no dicionario, concatena ch ao builder
            if (dictionary.containsKey(builder.toString() + ch)) {
                builder.append(ch);
            } else {
                String s12 = to12bit(dictionary.get(builder.toString()));

                // Coloca os 12 bits em um array e entao esceve no arquivo
                if (onleft) {
                    buffer[0] = (byte) Integer.parseInt(s12.substring(0, 8), 2);
                    buffer[1] = (byte) Integer.parseInt(s12.substring(8, 12) + "0000", 2);
                } else {
                    buffer[1] += (byte) Integer.parseInt(s12.substring(0, 4), 2);
                    buffer[2] = (byte) Integer.parseInt(s12.substring(4, 12), 2);
                    for (int b = 0; b < buffer.length; b++) {
                        saida[k++] = buffer[b];
                        buffer[b] = 0;
                    }
                }
                onleft = !onleft;

                // adiciona builder + ch ao dicionario
                if (dictionary.size() < 4096) {
                    builder.append(ch);
                    dictionary.put(builder.toString(), dictionary.size()); // HashMap sabe seu tamanho
                }

                // Set str to ch
                builder.setLength(0);
                builder.append(ch);
            }
        }
    }

    private String to12bit(int i) {
        String str = Integer.toBinaryString(i);
        while (str.length() < 12) {
            str = "0" + str;
        }
        return str;
    }

    private int contagemBufferEncode(byte[] content) {
        HashMap<String, Integer> dictionary = new HashMap<>();
        final StringBuilder builder = new StringBuilder();
        byte inputByte;
        byte[] buffer = new byte[3];
        boolean onleft = true;

        for (int i = 0; i < 256; i++) {
            dictionary.put(Character.toString((char) i), i);
        }

        int contador = 0;

        int j = 0;
        inputByte = content[j++];
        int i = new Byte(inputByte).intValue();
        if (i < 0) {
            i += 256;
        }
        char ch = (char) i;
        builder.setLength(0);
        builder.append(ch);

        // Le byte a byte
        while (true) {
            // Condigo de saida: Simula um IOException
            if (j >= content.length) {
                String str12bit = to12bit(dictionary.get(builder.toString()));
                if (onleft) {
                    buffer[0] = (byte) Integer.parseInt(str12bit.substring(0, 8), 2);
                    buffer[1] = (byte) Integer.parseInt(str12bit.substring(8, 12) + "0000", 2);
                    contador++;
                    contador++;
                } else {
                    buffer[1] += (byte) Integer.parseInt(str12bit.substring(0, 4), 2);
                    buffer[2] = (byte) Integer.parseInt(str12bit.substring(4, 12), 2);

                    for (int b = 0; b < buffer.length; b++) {
                        contador++;
                        buffer[b] = 0;
                    }
                }

                return contador;
            }
            inputByte = content[j++];
            i = new Byte(inputByte).intValue();

            if (i < 0) {
                i += 256;
            }
            ch = (char) i;

            // Se builder + ch estao no dicionario, concatena ch ao builder
            if (dictionary.containsKey(builder.toString() + ch)) {
                builder.append(ch);
            } else {
                String s12 = to12bit(dictionary.get(builder.toString()));

                // Coloca os 12 bits em um array e entao escreve isto no arquivo
                if (onleft) {
                    buffer[0] = (byte) Integer.parseInt(s12.substring(0, 8), 2);
                    buffer[1] = (byte) Integer.parseInt(s12.substring(8, 12) + "0000", 2);
                } else {
                    buffer[1] += (byte) Integer.parseInt(s12.substring(0, 4), 2);
                    buffer[2] = (byte) Integer.parseInt(s12.substring(4, 12), 2);
                    for (int b = 0; b < buffer.length; b++) {
                        contador++;
                        buffer[b] = 0;
                    }
                }
                onleft = !onleft;

                // adiciona builder + ch ao dicionario
                if (dictionary.size() < 4096) {
                    builder.append(ch);
                    dictionary.put(builder.toString(), dictionary.size());
                }

                // Define ch como conteu do builder
                builder.setLength(0);
                builder.append(ch);
            }
        }
    }

    public byte[] decode(byte[] content) {
        // ----------------------------------------------------------------------------------------------------//
        // --------------------------------------VARIAVEIS-----------------------------------------------------//
        // ----------------------------------------------------------------------------------------------------//
        // define a hashmap e outras variaveis que serao utilizadas pelo
        // programa
        HashMap<Integer, String> dictionary = new HashMap<>();
        String[] Array_char;
        int dictSize = 256;
        int currword;
        int priorword;
        byte[] buffer = new byte[3];
        boolean onleft = true;

        // Dicionario recebe ate 4k
        Array_char = new String[4096];

        for (int i = 0; i < 256; i++) {
            dictionary.put(i, Character.toString((char) i));
            Array_char[i] = Character.toString((char) i);
        }

        byte[] saida = new byte[contagemBufferDecode(content)];

        int j = 0;
        int k = 0;
        // Recebe a primeira palavra no codigo e utiliza o caractere
        // correspodente
        buffer[0] = content[j++];
        buffer[1] = content[j++];
        priorword = getvalue(buffer[0], buffer[1], onleft);
        onleft = !onleft;
        byte[] b = Array_char[priorword].getBytes();
        for (int l = 0; l < b.length; l++) {
            saida[k++] = b[l];
        }

        // A cada tres bytes lidos gera os caracteres correspodentes
        try{
            while (true) {
                if (j >= content.length) {
                    return saida;
                }
                if (onleft) {
                    buffer[0] = content[j++];
                    buffer[1] = content[j++];
                    currword = getvalue(buffer[0], buffer[1], onleft);
                } else {
                    buffer[2] = content[j++];
                    currword = getvalue(buffer[1], buffer[2], onleft);
                }
                onleft = !onleft;

                if (currword >= dictSize) {
                    if (dictSize < 4096) {
                        Array_char[dictSize] = Array_char[priorword] + Array_char[priorword].charAt(0);
                    }
                    dictSize++;
                    b = (Array_char[priorword] + Array_char[priorword].charAt(0)).getBytes();
                    for (int l = 0; l < b.length; l++) {
                        saida[k++] = b[l];
                    }
                    // out.writeBytes(Array_char[priorword]
                    // + Array_char[priorword].charAt(0));
                } else {
                    if (dictSize < 4096) {
                        Array_char[dictSize] = Array_char[priorword] + Array_char[currword].charAt(0);
                    }
                    dictSize++;
                    b = Array_char[currword].getBytes();
                    for (int l = 0; l < b.length; l++) {
                        saida[k++] = b[l];
                    }
                    // out.writeBytes(Array_char[currword]);
                }
                priorword = currword;
            }
        }
        catch(IndexOutOfBoundsException e) { // chegou ao fim do byte[]
            return saida;
        }

    }

    private int contagemBufferDecode(byte[] content) {
        // ----------------------------------------------------------------------------------------------------//
        // --------------------------------------VARIAVEIS-----------------------------------------------------//
        // ----------------------------------------------------------------------------------------------------//
        // define a hashmap e outras variaveis que serao utilizadas pelo
        // programa
        HashMap<Integer, String> dictionary = new HashMap<>();
        String[] Array_char;
        int dictSize = 256;
        int currword;
        int priorword;
        byte[] buffer = new byte[3];
        boolean onleft = true;

        // Dicionario recebe ate 4k
        Array_char = new String[4096];

        for (int i = 0; i < 256; i++) {
            dictionary.put(i, Character.toString((char) i));
            Array_char[i] = Character.toString((char) i);
        }

        int contador = 0;

        int j = 0;
        // Recebe a primeira palavra no codigo e utiliza o caractere
        // correspodente
        buffer[0] = content[j++];
        buffer[1] = content[j++];
        priorword = getvalue(buffer[0], buffer[1], onleft);
        onleft = !onleft;
        contador += Array_char[priorword].length();

        // A cada tres bytes lidos gera os caracteres correspodentes
        while (true) {
            if (j >= content.length) {
                return contador;
            }
            if (onleft) {
                buffer[0] = content[j++];
                buffer[1] = content[j++];
                currword = getvalue(buffer[0], buffer[1], onleft);
            } else {
                buffer[2] = content[j++];
                currword = getvalue(buffer[1], buffer[2], onleft);
            }
            onleft = !onleft;

            if (currword >= dictSize) {
                if (dictSize < 4096) {
                    Array_char[dictSize] = Array_char[priorword] + Array_char[priorword].charAt(0);
                }
                dictSize++;
                contador += Array_char[priorword].length() + 1;
            } else {
                if (dictSize < 4096) {
                    Array_char[dictSize] = Array_char[priorword] + Array_char[currword].charAt(0);
                }
                dictSize++;
                contador += Array_char[currword].length();
            }
            priorword = currword;
        }
    }

    private int getvalue(byte b1, byte b2, boolean onleft) {
        String temp1 = Integer.toBinaryString(b1);
        String temp2 = Integer.toBinaryString(b2);

        while (temp1.length() < 8) {
            temp1 = "0" + temp1;
        }
        if (temp1.length() == 32) {
            temp1 = temp1.substring(24, 32);
        }
        while (temp2.length() < 8) {
            temp2 = "0" + temp2;
        }
        if (temp2.length() == 32) {
            temp2 = temp2.substring(24, 32);
        }

        if (onleft) {
            return Integer.parseInt(temp1 + temp2.substring(0, 4), 2);
        } else {
            return Integer.parseInt(temp1.substring(4, 8) + temp2, 2);
        }

    }
}
