import java.io.IOException;
import java.net.*;

/**
 * Created by zhangyan on 2017/6/6.
 */
public class guangbo {
    public static void main(String[] args) {
        String host = "255.255.255.255";
        // /广播地址
        int port = 9999;
        //广播的目的端口
        String message = "test";
        //用于发送的字符串
        try
        {
            InetAddress adds = InetAddress.getByName(host);
            DatagramSocket ds = new DatagramSocket();
            DatagramPacket dp = new DatagramPacket(message.getBytes(),message.length(), adds, port);
            ds.send(dp);
            ds.close();
        } catch (UnknownHostException e) {e.printStackTrace();
        }
        catch (SocketException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
