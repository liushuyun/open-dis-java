package edu.nps.moves.dis;

import java.util.*;
import java.io.*;

/**
 * 5.2.46. Intercom communcations parameters
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class IntercomCommunicationsParameters extends Object implements Serializable {

    /**
     * Type of intercom parameters record
     */
    protected int recordType;

    /**
     * length of record-specifid field, in octets
     */
    protected int recordLength;

    /**
     * variable length variablelist of data parameters
     */
    protected byte[] parameterValues = new byte[0];

    /**
     * Constructor
     */
    public IntercomCommunicationsParameters() {
    }

    public int getMarshalledSize() {
        int marshalSize = 0;

        marshalSize = marshalSize + 2;  // recordType
        marshalSize = marshalSize + 2;  // recordLength
        marshalSize = marshalSize + parameterValues.length;

        return marshalSize;
    }

    public void setRecordType(int pRecordType) {
        recordType = pRecordType;
    }

    public int getRecordType() {
        return recordType;
    }

    public int getRecordLength() {
        return parameterValues.length;
    }

    /**
     * Note that setting this value will not change the marshalled value. The
     * list whose length this describes is used for that purpose. The
     * getrecordLength method will also be based on the actual list length
     * rather than this value. The method is simply here for java bean
     * completeness.
     */
    public void setRecordLength(int pRecordLength) {
        recordLength = pRecordLength;
    }

    public void setParameterValues(byte[] pParameterValues) {
        parameterValues = pParameterValues;
    }

    public byte[] getParameterValues() {
        return parameterValues;
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
        buff.putShort((short) recordType);
        buff.putShort((short) parameterValues.length);
        buff.put(parameterValues);
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
        recordType = (int) (buff.getShort() & 0xFFFF);
        recordLength = (int) (buff.getShort() & 0xFFFF);
        parameterValues = new byte[recordLength];
        buff.get(parameterValues);
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

        if (!(obj instanceof IntercomCommunicationsParameters)) {
            return false;
        }

        final IntercomCommunicationsParameters rhs = (IntercomCommunicationsParameters) obj;

        if (!(recordType == rhs.recordType)) {
            ivarsEqual = false;
        }
        if (!(recordLength == rhs.recordLength)) {
            ivarsEqual = false;
        }
        if (!(Arrays.equals(parameterValues, rhs.parameterValues))) {
            ivarsEqual = false;
        }

        return ivarsEqual;
    }
} // end of class
