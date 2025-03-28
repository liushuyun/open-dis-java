package edu.nps.moves.dis;

import java.io.*;

/**
 * Section 5.2.15. Specifies the character set used in the first byte, followed
 * by 11 characters of text data.
 * 指定在第一个字节中使用的字符集，后跟11个字符的文本数据。
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class Marking extends Object implements Serializable {

    public static final int MARKING_STRING_LENGTH = 11;

    /**
     * The character set
     */
    protected short characterSet;
    
    /**
     * The characters
     */
    protected byte[] characters = new byte[MARKING_STRING_LENGTH];

    /**
     * Constructor
     */
    public Marking() {
    }

    public int getMarshalledSize() {
        int marshalSize = 0;

        marshalSize = marshalSize + 1;  // characterSet
        marshalSize = marshalSize + MARKING_STRING_LENGTH;  // characters

        return marshalSize;
    }

    public void setCharacterSet(short pCharacterSet) {
        characterSet = pCharacterSet;
    }

    public short getCharacterSet() {
        return characterSet;
    }

    /**
     * Ensure what is set does not go over 11 characters, and anything under 11
     * characters zero-fills. post-processing patch
     *
     * @param pCharacters an array of characters to set
     */
    public void setCharacters(byte[] pCharacters) {
        if (pCharacters.length >= characters.length) {
            System.arraycopy(pCharacters, 0, characters, 0, characters.length);
        } else {
            int pCharactersLength = pCharacters.length;
            System.arraycopy(pCharacters, 0, characters, 0, pCharactersLength);
            for (int ix = pCharactersLength; ix < characters.length; ix++) {
                // Ensure all zeros in unfilled fields
                characters[ix] = 0;
            }
        }
    }

    public byte[] getCharacters() {
        return characters;
    }

    /**
     * An added conveniece method (added by patch): accepts a string, and either
     * truncates or zero-fills it to fit into the 11-byte character marking
     * field.
     *
     * @param marking the marking string, converted internally into a character
     * array that is exactly 11 bytes long
     */
    public void setCharactersString(String marking) {
        byte[] buff = marking.getBytes();
        this.setCharacters(buff);
    }

    /**
     * Post-processing added convenience method. Converts the byte array of
     * characters to a string. This uses the platform's default charset, rather
     * than respecting the charset specified in the other field. For the most
     * part this will work, unless you're in some wacky foreign country, in
     * which case you should start speaking English.
     *
     * @return character array converted to a string
     */
    public String getCharactersString() {
        String charString = new String(characters);

        return charString;
    }

    /**
     * Packs a Pdu into the ByteBuffer.
     *
     * @throws java.nio.BufferOverflowException if buff is too small
     * @throws java.nio.ReadOnlyBufferException if buff is read only
     * @see java.nio.ByteBuffer
     * @param buff The ByteBuffer at the position to begin writing
     * @since ??
     */
    public void marshal(java.nio.ByteBuffer buff) {
        buff.put((byte) characterSet);

        for (int idx = 0; idx < characters.length; idx++) {
            buff.put((byte) characters[idx]);
        } // end of array marshaling

    } // end of marshal method

    /**
     * Unpacks a Pdu from the underlying data.
     *
     * @throws java.nio.BufferUnderflowException if buff is too small
     * @see java.nio.ByteBuffer
     * @param buff The ByteBuffer at the position to begin reading
     * @since ??
     */
    public void unmarshal(java.nio.ByteBuffer buff) {
        characterSet = (short) (buff.get() & 0xFF);
        for (int idx = 0; idx < characters.length; idx++) {
            characters[idx] = buff.get();
        } // end of array unmarshaling
    } // end of unmarshal method 


    /*
  * The equals method doesn't always work--mostly it works only on classes that consist only of primitives. Be careful.
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        return equalsImpl(obj);
    }

    /**
     * Compare all fields that contribute to the state, ignoring transient and
     * static fields, for <code>this</code> and the supplied object
     *
     * @param obj the object to compare to
     * @return true if the objects are equal, false otherwise.
     */
    public boolean equalsImpl(Object obj) {
        boolean ivarsEqual = true;

        if (!(obj instanceof Marking)) {
            return false;
        }

        final Marking rhs = (Marking) obj;

        if (!(characterSet == rhs.characterSet)) {
            ivarsEqual = false;
        }

        for (int idx = 0; idx < 11; idx++) {
            if (!(characters[idx] == rhs.characters[idx])) {
                ivarsEqual = false;
            }
        }

        return ivarsEqual;
    }
} // end of class
