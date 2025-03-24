package edu.nps.moves.examples;

import com.google.gson.Gson;
import edu.nps.moves.disutil.*;
import edu.nps.moves.dis.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Example code that receives ESPDUs from the network in binary format.
 */
public class EspduReceiver {

    public static void main(String args[]) throws IOException, InterruptedException {

//        DisConnection con = new DisConnection(EspduSender.DEFAULT_MULTICAST_GROUP, EspduSender.DIS_PORT);
        DisConnection con = new DisConnection("225.1.2.3", 9393);
//        DisConnection con = new DisConnection("192.168.255.255", 9393);
//        DisConnection con = new DisConnection(9393);
        new Thread(con).start(); // In this thread we receive pdu's from the network and put them into a queue

        // In this thread we take pdu's off the queue and process them.
        new Runnable() {

            @Override
            public void run() {
                Gson gson = new Gson();
                while (!Thread.interrupted()) {
                    try {
                        Pdu pdu = con.getNext();
                        if (pdu != null) {
                            System.out.println("Received PDU of type: " + pdu.getClass().getName());
                            if (pdu instanceof EntityStatePdu) {
                                EntityStatePdu espdu = (EntityStatePdu) pdu;
                                System.out.println(" Marking: " + espdu.getMarking().getCharactersString());
                                EntityID eid = espdu.getEntityID();
                                System.out.println(" Site, App, Id: [" + eid.getSite() + ", " + eid.getApplication() + ", " + eid.getEntity() + "] ");
                                Vector3Double position = espdu.getEntityLocation();
                                System.out.println(" Location in DIS geocentric xyz coordinates: [" + position.getX() + ", " + position.getY() + ", " + position.getZ() + "]");
                                final double[] latlon = CoordinateConversions.xyzToLatLonDegrees(position.toArray());
                                System.out.println(" Location in Latitude Longitude Elevation: [" + latlon[0] + ", " + latlon[1] + ", " + latlon[2] + "]");
                                EntityType entityType = espdu.getEntityType();
                                System.out.println(" Entity type: " +
//                                        "种类："+
                                        entityType.getEntityKind() + ", " +
                                        entityType.getDomain() + ", " +
                                        entityType.getCountry() + ", " +
                                        entityType.getCategory() + ", " +
                                        entityType.getSubcategory() + ", " +
                                        entityType.getExtra());
                            }
                            if (pdu instanceof FirePdu) {
                                FirePdu firePdu = (FirePdu) pdu;
                                String firePduJsonString = gson.toJson(firePdu);
                                System.out.println(firePduJsonString);
//                                FirePdu firePdu1 = gson.fromJson(firePduJsonString, FirePdu.class);
                                System.out.println(" 事件ID: [" + firePdu.getEventID().getEventNumber() + "]");
                                EntityID eid = firePdu.getMunitionID();
                                System.out.println(" 弹药Id: [" + eid.getSite() + ", " + eid.getApplication() + ", " + eid.getEntity() + "] ");
                                eid = firePdu.getFiringEntityID();
                                System.out.println(" 开火实体Id: [" + eid.getSite() + ", " + eid.getApplication() + ", " + eid.getEntity() + "] ");

                                Vector3Double position = firePdu.getLocationInWorldCoordinates();
                                System.out.println(" 触发开火事件的位置: [" + position.getX() + ", " + position.getY() + ", " + position.getZ() + "]");
                                BurstDescriptor burstDescriptor = firePdu.getBurstDescriptor();
                                System.out.println(" 开火事件中使用的弹药: [" + burstDescriptor + "]");
//                                final double[] latlon = CoordinateConversions.xyzToLatLonDegrees(position.toArray());
                                Vector3Float velocity = firePdu.getVelocity();

                                eid = firePdu.getTargetEntityID();
                                System.out.println(" 目标实体Id: [" + eid.getSite() + ", " + eid.getApplication() + ", " + eid.getEntity() + "] ");
                                System.out.println(" 目标距离: [" + firePdu.getRangeToTarget() + "]");
                                System.out.println(" 弹药速率: [" + velocity.getX() + ", " + velocity.getY() + ", " + position.getZ() + "]");

                            } else if (pdu instanceof EntityStatePdu) {

                            }else {
                                System.out.println(gson.toJson(pdu));
                            }
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DisConnection.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }.run();
    }
}
