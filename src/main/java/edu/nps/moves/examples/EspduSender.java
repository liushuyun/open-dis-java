package edu.nps.moves.examples;

import java.io.*;
import java.net.*;

import com.google.gson.Gson;
import edu.nps.moves.dis.*;
import edu.nps.moves.disutil.CoordinateConversions;
import edu.nps.moves.disutil.DisConnection;
import edu.nps.moves.disutil.DisTime;
import java.util.concurrent.TimeUnit;

/**
 * Creates and sends ESPDUs in binary format.
 *
 * @author DMcG
 */
public class EspduSender {

    public static final String DEFAULT_MULTICAST_GROUP = "239.1.2.3";

    public static final int DIS_PORT = 3000;

    public static final int DIS_HEARTBEAT_SECS = 10;

    public static void main(String args[]) throws UnknownHostException, IOException, RuntimeException, InterruptedException {

//        DisConnection con = new DisConnection(DEFAULT_MULTICAST_GROUP, DIS_PORT);
        DisConnection con = new DisConnection("225.1.2.3", 9393);

        EntityStatePdu espdu = new EntityStatePdu();

        Gson gson = new Gson();
        // Initialize values in the Entity State PDU object.
        // The exercise ID is a way to differentiate between different virtual worlds on one network.
        // Note that some values (such as the PDU type and PDU family) are set automatically when you create the ESPDU.
        //初始化实体状态PDU对象中的值。
        //The exercise ID是在一个网络上区分不同虚拟世界的一种方法。
        //创建ESPDU时，系统会自动设置一些参数，如PDU类型、PDU族等。
        espdu.setExerciseID((short) 1);

        // The EID is the unique identifier for objects in the world. This 
        // EID should match up with the ID for the object specified in the 
        // VMRL/x3d/virtual world.
        // entityID是世界上对象的唯一标识符。这个
        // entityID应该与指定对象的ID匹配 在
        // VMRL/x3d/虚拟 世界 中。
        EntityID entityID = espdu.getEntityID();
        entityID.setSite(1);  // 0 is apparently not a valid site number, per the spec（根据规范，0显然不是一个有效的站点编号）
        entityID.setApplication(1);
        entityID.setEntity(22);

        // Set the entity type.
        // SISO has a big list of enumerations, so that by
        // specifying various numbers we can say this is an M1A2 American tank,
        // the USS Enterprise, and so on. We'll make this a tank. There is a 
        // separate project elsehwhere in this project that implements DIS 
        // enumerations in C++ and Java, but to keep things simple we just use
        // numbers here.
        //设置实体类型。
        // SISO有一个大的枚举列表，所以by
        //指定各种数字，我们可以说这是一辆M1A2美制坦克，
        //企业号，等等。我们把它做成一个坦克。有一个
        //在这个项目的其他地方单独的项目实现了DIS
        // c++和Java中的枚举，但为了保持简单，我们只使用
        //这里的数字。

        //1:2:225:1:9:10:0 =》car
        EntityType entityType = espdu.getEntityType();

        entityType.setEntityKind((short) 1);      // Platform (vs lifeform, munition, sensor, etc.)
        entityType.setDomain((short) 2);          // Land (vs air, surface, subsurface, space)
        entityType.setCountry(225);              // USA
        entityType.setCategory((short) 1);        // Tank
        entityType.setSubcategory((short) 9);     // M1 Abrams
        entityType.setSpec((short) 10);            // M1A2 Abrams
        entityType.setExtra((short) 0);            // M1A2 Abrams



        Marking m = new Marking();
        m.setCharactersString("T11");
        espdu.setMarking(m); // It's common to set the marking field to the callsign.（通常将标记字段设置为呼号。）

        DisTime disTime = DisTime.getInstance(); // TODO explain

        // ICBM coordinates for my office（我办公室的洲际弹道导弹坐标）
//        double lat = 36.595517;
//        double lon = -121.877000;
//        -81.588704,29.972218
        double lat = 29.972218;
        double lon = -81.588704;

        final int NUMBER_PDU_TO_SEND = 100; // For example purposes, stop after sending 100 times.（例如，发送100次后停止。）
            
        // Loop through sending N ESPDUs（通过发送N个espdu进行环路）
        System.out.println("This example will send " + NUMBER_PDU_TO_SEND + " Entity State PDU packets to " + DEFAULT_MULTICAST_GROUP + ". One packet every " + DIS_HEARTBEAT_SECS + " seconds.");
        for (int idx = 0; idx < NUMBER_PDU_TO_SEND; idx++) {

            if (idx % 2 == 1) {
                //1:2:225:1:5:5:0 =》car
                EntityType type = espdu.getEntityType();

                type.setEntityKind((short) 1);      // Platform (vs lifeform, munition, sensor, etc.)
                type.setDomain((short) 2);          // Land (vs air, surface, subsurface, space)
                type.setCountry(225);              // USA
                type.setCategory((short) 1);        // Tank
                type.setSubcategory((short) 5);     // M1 Abrams
                type.setSpec((short) 5);            // M1A2 Abrams
                type.setExtra((short) 0);            // M1A2 Abrams
            }
            // DIS time is a pain in the ass. DIS time units are 2^31-1 units per
            // hour, and time is set to DIS time units from the top of the hour. 
            // This means that if you start sending just before the top of the hour
            // the time units can roll over to zero as you are sending. The receivers
            // (escpecially homegrown ones) are often not able to detect rollover
            // and may start discarding packets as dupes or out of order. We use
            // an NPS timestamp here, hundredths of a second since the start of the
            // year. The DIS standard for time is often ignored in the wild; I've seen
            // people use Unix time (seconds since 1970) and more. Or you can
            // just stuff idx into the timestamp field to get something that is monotonically
            // increasing.
//            DIS时间是一个痛苦的屁股。DIS时间单位是2^31-1单位
//            小时，时间设置为DIS时间单位，从小时上方开始。
//            这意味着如果你在整点之前开始发送
//            时间单位可以滚动到零，因为你正在发送。的接收器
//                    (尤其是本土企业)往往无法检测到债务的翻转
//            并且可能会开始丢弃数据包，因为它们是伪造的或有问题的。我们使用
//            这里是NPS时间戳，从开始的百分之一秒
//            的一年。在野外，DIS时间标准经常被忽略;我看过
//            人们使用Unix时间(从1970年开始使用秒)甚至更多。或者你可以
//            只需将idx填入时间戳字段，即可获得单调的内容
//            增加。

            // Note that timestamp is used to detect duplicate and out of order packets. 
            // That means if you DON'T change the timestamp, many implementations will simply
            // discard subsequent packets that have an identical timestamp. Also, if they
            // receive a PDU with an timestamp lower than the last one they received, they
            // may discard it as an earlier, out-of-order PDU. So it is a good idea to
            // update the timestamp on ALL packets sent.
            // An alterative approach: actually follow the standard. It's a crazy concept,
            // but it might just work.
//            请注意，时间戳用于检测重复和乱序数据包。
//            这意味着如果你不改变时间戳，很多实现都会简单地改变
//            丢弃具有相同时间戳的后续数据包。同样，如果他们
//            如果接收到一个时间戳比上次接收到的时间戳低的PDU，它们就会返回
//            可以将其作为早期的无序PDU丢弃。所以这是个好主意
//            更新所有发送报文的时间戳。
//            另一种方法是:实际遵循标准。这是一个疯狂的概念，
//            但它可能会奏效。
            int timestamp = disTime.getDisAbsoluteTimestamp();
            espdu.setTimestamp(timestamp);

            double disCoordinates[] = CoordinateConversions.getXYZfromLatLonDegrees(lat, lon, 1.0);
            Vector3Double location = espdu.getEntityLocation();
            location.setX(disCoordinates[0]);
            location.setY(disCoordinates[1]);
            location.setZ(disCoordinates[2]);

            // Optionally, do some rotation of the entity（可选地，对实体进行一些旋转）
            /*
            Orientation orientation = espdu.getEntityOrientation();
            float psi = orientation.getPsi();
            psi = psi + idx;
            orientation.setPsi(psi);
            orientation.setTheta((float)(orientation.getTheta() + idx /2.0));
             */
            // Optionally, set the velocity, acceleration, and so on.（可选地，设置速度、加速度等。）
            
            con.send(espdu);
            System.out.println(gson.toJson(espdu));

            // Print some info about what was sent.
            System.out.println("Sent Entity State PDU #" + idx);
            System.out.println(" Id (Site,App,Id): [" + espdu.getEntityID().getSite() + ", " + espdu.getEntityID().getApplication() + ", " + espdu.getEntityID().getEntity() + "]");
            System.out.println(" Marking: " + espdu.getMarking().getCharactersString());
            System.out.println(" Location (X,Y,Z): [" + espdu.getEntityLocation().getX() + ", " + espdu.getEntityLocation().getY() + ", " + espdu.getEntityLocation().getZ() + "]");
            double lla[] = CoordinateConversions.xyzToLatLonDegrees(espdu.getEntityLocation().toArray());
            System.out.println(" Location (Lat,Lon,Alt): [" + lla[0] + ", " + lla[1] + ", " + lla[2] + "]");

            System.out.println("Sleeping for heartbeat of " + DIS_HEARTBEAT_SECS + " seconds.");
            Thread.sleep(TimeUnit.SECONDS.toMillis(DIS_HEARTBEAT_SECS));
        }
    }
}