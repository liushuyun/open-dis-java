package edu.nps.moves.dis;

import java.util.*;
import java.io.*;

/**
 * Section 5.3.5.1. Information about a request for supplies. COMPLETE
 *
 * Copyright (c) 2008-2016, MOVES Institute, Naval Postgraduate School. All
 * rights reserved. This work is licensed under the BSD open source license,
 * available at https://www.movesinstitute.org/licenses/bsd.html
 *
 * @author DMcG
 */
public class ServiceRequestPdu extends LogisticsFamilyPdu implements Serializable {

    /**
     * Entity that is requesting service
     */
    protected EntityID requestingEntityID = new EntityID();

    /**
     * Entity that is providing the service
     */
    protected EntityID servicingEntityID = new EntityID();

    /**
     * type of service requested
     */
    protected short serviceTypeRequested;

    /**
     * How many requested
     */
    protected short numberOfSupplyTypes;

    /**
     * padding
     */
    protected short serviceRequestPadding = (short) 0;

    protected List< SupplyQuantity> supplies = new ArrayList< SupplyQuantity>();

    /**
     * Constructor
     */
    public ServiceRequestPdu() {
        setPduType((short) 5);
    }

    public int getMarshalledSize() {
        int marshalSize = 0;

        marshalSize = super.getMarshalledSize();
        marshalSize = marshalSize + requestingEntityID.getMarshalledSize();  // requestingEntityID
        marshalSize = marshalSize + servicingEntityID.getMarshalledSize();  // servicingEntityID
        marshalSize = marshalSize + 1;  // serviceTypeRequested
        marshalSize = marshalSize + 1;  // numberOfSupplyTypes
        marshalSize = marshalSize + 2;  // serviceRequestPadding
        for (int idx = 0; idx < supplies.size(); idx++) {
            SupplyQuantity listElement = supplies.get(idx);
            marshalSize = marshalSize + listElement.getMarshalledSize();
        }

        return marshalSize;
    }

    public void setRequestingEntityID(EntityID pRequestingEntityID) {
        requestingEntityID = pRequestingEntityID;
    }

    public EntityID getRequestingEntityID() {
        return requestingEntityID;
    }

    public void setServicingEntityID(EntityID pServicingEntityID) {
        servicingEntityID = pServicingEntityID;
    }

    public EntityID getServicingEntityID() {
        return servicingEntityID;
    }

    public void setServiceTypeRequested(short pServiceTypeRequested) {
        serviceTypeRequested = pServiceTypeRequested;
    }

    public short getServiceTypeRequested() {
        return serviceTypeRequested;
    }

    public short getNumberOfSupplyTypes() {
        return (short) supplies.size();
    }

    /**
     * Note that setting this value will not change the marshalled value. The
     * list whose length this describes is used for that purpose. The
     * getnumberOfSupplyTypes method will also be based on the actual list
     * length rather than this value. The method is simply here for java bean
     * completeness.
     */
    public void setNumberOfSupplyTypes(short pNumberOfSupplyTypes) {
        numberOfSupplyTypes = pNumberOfSupplyTypes;
    }

    public void setServiceRequestPadding(short pServiceRequestPadding) {
        serviceRequestPadding = pServiceRequestPadding;
    }

    public short getServiceRequestPadding() {
        return serviceRequestPadding;
    }

    public void setSupplies(List<SupplyQuantity> pSupplies) {
        supplies = pSupplies;
    }

    public List<SupplyQuantity> getSupplies() {
        return supplies;
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
        super.marshal(buff);
        requestingEntityID.marshal(buff);
        servicingEntityID.marshal(buff);
        buff.put((byte) serviceTypeRequested);
        buff.put((byte) supplies.size());
        buff.putShort((short) serviceRequestPadding);

        for (int idx = 0; idx < supplies.size(); idx++) {
            SupplyQuantity aSupplyQuantity = (SupplyQuantity) supplies.get(idx);
            aSupplyQuantity.marshal(buff);
        } // end of list marshalling

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
        super.unmarshal(buff);

        requestingEntityID.unmarshal(buff);
        servicingEntityID.unmarshal(buff);
        serviceTypeRequested = (short) (buff.get() & 0xFF);
        numberOfSupplyTypes = (short) (buff.get() & 0xFF);
        serviceRequestPadding = buff.getShort();
        for (int idx = 0; idx < numberOfSupplyTypes; idx++) {
            SupplyQuantity anX = new SupplyQuantity();
            anX.unmarshal(buff);
            supplies.add(anX);
        }

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

    @Override
    public boolean equalsImpl(Object obj) {
        boolean ivarsEqual = true;

        if (!(obj instanceof ServiceRequestPdu)) {
            return false;
        }

        final ServiceRequestPdu rhs = (ServiceRequestPdu) obj;

        if (!(requestingEntityID.equals(rhs.requestingEntityID))) {
            ivarsEqual = false;
        }
        if (!(servicingEntityID.equals(rhs.servicingEntityID))) {
            ivarsEqual = false;
        }
        if (!(serviceTypeRequested == rhs.serviceTypeRequested)) {
            ivarsEqual = false;
        }
        if (!(numberOfSupplyTypes == rhs.numberOfSupplyTypes)) {
            ivarsEqual = false;
        }
        if (!(serviceRequestPadding == rhs.serviceRequestPadding)) {
            ivarsEqual = false;
        }

        for (int idx = 0; idx < supplies.size(); idx++) {
            if (!(supplies.get(idx).equals(rhs.supplies.get(idx)))) {
                ivarsEqual = false;
            }
        }

        return ivarsEqual && super.equalsImpl(rhs);
    }
} // end of class
