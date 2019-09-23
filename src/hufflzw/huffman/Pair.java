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
public class Pair<TypeOne, TypeTwo> {
    protected final TypeOne first;
    protected final TypeTwo second;

    public Pair(TypeOne first, TypeTwo second) {
        this.first = first;
        this.second = second;
    }

    public TypeOne getFirst() {
        return first;
    }

    public TypeTwo getSecond() {
        return second;
    }
}
