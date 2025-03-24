package edu.nps.moves.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import edu.nps.moves.examples.EspduSender2;
import edu.nps.moves.examples.EspduSender3;

import java.io.IOException;
import java.net.*;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ExternalSignalListener implements Runnable {

    private DatagramSocket socket;
    private volatile boolean running;    //控制ExternalSignalListener的运行状态
                                         //初始化了一个长度为1024字节的数组作为接收数据的缓冲区。这意味着这个缓冲区最多可以一次性接收1024字节的数据
    private byte[] buf = new byte[1024]; // 缓冲区大小可根据需要调整
//    private byte[] buf = new byte[16384]; // 缓冲区大小可根据需要调整
    private Thread thread;


	private final Gson gson;
	private EspduSender2 espduSender2;


    //构造函数，指定监听的端口
    public ExternalSignalListener(int port) throws SocketException, UnknownHostException {
//        socket = new DatagramSocket(port);
        socket = new DatagramSocket(port, InetAddress.getByName("127.0.0.1"));
        this.thread = new Thread(this);//创建新线程

		this.espduSender2 = new EspduSender2();
		this.gson = new Gson();
    }

	@Override
    public void run()
	{
		//System.out.println("run() ------");

		//ExternalSignalListener运行状态设置为true
        running = true;

        //循环执行监听
        while (running)
        {
        	//使用上面的缓冲区来存储接收到的数据。这个包的最大数据长度被限制为缓冲区的大小，即1024字节。
        	DatagramPacket packet = new DatagramPacket(buf, buf.length);

            try
            {
              //阻塞调用，它等待直到一个UDP数据包到达,并将其内容存储到之前创建的 DatagramPacket 的缓冲区中
                socket.receive(packet);

              //packet.getLength() 返回实际接收到的数据长度，这可能小于或等于缓冲区大小（256字节）。
              //因此，这行代码只转换实际接收到的数据长度的内容，而不是整个缓冲区的内容。
                String received = new String(packet.getData(), 0, packet.getLength());

                // 处理接收到的数据
                processSignal(received);

					continue;



            } catch (IOException e) {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, (String)null, e);

                if (!running) {
                    // 如果不是因为运行状态被设置为false而导致的异常，则打印错误信息
					System.out.println("IOException: " + e.getMessage());
                }
            }
        }
        socket.close();
    }

    public void processSignal(String signal_jsonString) throws IOException
    {
//    	System.out.println("processSignal() ------");

   		// 将收到的数据进行解析，因为外部信号是以jason形式发送过来的，所以此处需要利用Fastjson进行解析
    	// 从JSON字符串解析回Map
    	Gson gson = new GsonBuilder()
				.setPrettyPrinting() // 设置Gson输出格式化为易读的格式
				.create();
		Map<String, String> receivedSignal_ValueMap;
		try {
			receivedSignal_ValueMap = gson.fromJson(signal_jsonString.trim(), new TypeToken<Map<String, String>>() {}.getType());
			System.out.println(gson.toJson(receivedSignal_ValueMap));
		}catch (Exception e) {
			System.out.println("msg invalid!");
			System.out.println(e.getMessage());
			return;
		}

    	//获取接收信号的名字，判断在待接收信号中是否有这个信号类型
    	String receivedSignal_name = receivedSignal_ValueMap.get("pdu");

		switch (receivedSignal_name){
			case "entityStatePdu":
//				EspduSender2 espduSender2 = new EspduSender2();
                if (espduSender2.status().equals(Thread.State.NEW)) {
                    espduSender2.starting();
                }
//				Vector3Float position = new Vector3Float(
//						Float.parseFloat(receivedSignal_ValueMap.get("x")),
//						Float.parseFloat(receivedSignal_ValueMap.get("y")),
//						Float.parseFloat(receivedSignal_ValueMap.get("z"))
//				);

				break;
			case "firePdu":
//                if (espduSender3.status().equals(Thread.State.NEW)
//                        || espduSender3.status().equals(Thread.State.TERMINATED)) {
//                    espduSender3.starting();
//                }

                new EspduSender3(receivedSignal_ValueMap).starting();
				break;
		}


//    	boolean found = this.simListener.waitComingSignals.containsValue(receivedSignal_name);
//
//        //如果在带发送信号集合中发现有这类信号，则创建对应的信号实例发送到对应的仿真上下文中
//    	if(found)
//    	{
//    		//System.out.println("waitComingSignals have this signal: "+receivedSignal_name);
//
//
//
//
//    	}else {
//    		System.out.println("waitComingSignals dont have this signal: "+receivedSignal_name);
//    	}

    }

    public void startListening() {
        thread.start();
        //System.out.println("startListening() ------");
    }

    public void stopListening() {

    	terminate();

        try {
        		thread.join(); // 等待线程结束
        		//System.out.println("stopListening() ");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 重新设置中断状态
            System.out.println("Thread was interrupted, failed to complete cleanly");
        }
    }

    private void terminate() {
        running = false;
        socket.close(); // 关闭socket会导致 socket.receive() 抛出SocketException
    }

}
