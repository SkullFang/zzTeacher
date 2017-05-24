
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by zhangyan on 2017/5/17.
 */
public class GUI_test3 {
    private static Image image;
    public static void main(String[] args) {
        new GUI_test3().getImage();
        ImageFrame iif=new ImageFrame();
        iif.setVisible(true);

    }
    public void re(){

    }
    public void getImage(){
        try {
            DatagramSocket socket=new DatagramSocket(7800);
            byte[] data=new byte[408000];
            DatagramPacket dp=new DatagramPacket(data,data.length);
            System.out.println("wait");
            socket.receive(dp);
            ByteArrayInputStream input= new ByteArrayInputStream(dp.getData(),dp.getOffset(),dp.getLength());
            image= ImageIO.read(input);
//            System.out.println(data[0]);
//            String info=new String(data,0,dp.getLength());
//            System.out.print(info);
//            String file="4.jpg";
//            OutputStream out=new FileOutputStream(file);
//            out.write(data);
//            out.flush();
//            out.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        image=new ImageIcon("/Users/zhangyan/Desktop/wKgBpVU_TLaAI6eCAASqCRNYhdw39.jpeg").getImage();

    }
    public static class ImageFrame extends JFrame{
        public ImageFrame(){
            setTitle("image-test");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            Toolkit tk=Toolkit.getDefaultToolkit();
            Dimension ds=tk.getScreenSize();
            int width=ds.width;
            int heigh=ds.height;
            Imagepanel ip=new Imagepanel();
            Dimension si=ip.getPerferredSize();
            System.out.print(si.width);
            setSize(si.width,si.height);
            System.out.println(si.width+si.height);
            add(ip);
//            pack();
            setLocation(width/4,heigh/8);
            setLocationByPlatform(true);
        }
    }
    public static class Imagepanel extends JPanel{

        public void paintComponent(Graphics g){
            super.paintComponent(g);
            g.drawImage(image,0,0,null);
        }
        public Dimension getPerferredSize(){
            return new Dimension(image.getWidth(this),image.getHeight(this));
        }
    }
}
