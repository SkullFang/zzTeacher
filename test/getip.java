import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by zhangyan on 2017/5/24.
 */
public class getip {
    public static void main(String[] args) {
        try {
            InetAddress addr=InetAddress.getLocalHost();
            System.out.println(addr.getHostAddress().toString());
            System.out.println(addr.getHostName().toString());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
