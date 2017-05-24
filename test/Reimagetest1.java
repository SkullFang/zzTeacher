import com.sun.javafx.iio.ImageFrame;
import com.sun.xml.internal.ws.api.server.Adapter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by zhangyan on 2017/5/15.
 */
public class Reimagetest1{


    public static void main(String[] args){
        try {
            DatagramSocket socket=new DatagramSocket(7800);
            byte[] data=new byte[408000];
            DatagramPacket dp=new DatagramPacket(data,data.length);
            System.out.println("wait");
            socket.receive(dp);
            ByteArrayInputStream input= new ByteArrayInputStream(dp.getData(),dp.getOffset(),dp.getLength());
            String file="123.jpg";
            OutputStream out=new FileOutputStream(file);
            out.write(data);
            out.flush();
            out.close();
            System.out.println(data[0]);
            String info=new String(data,0,dp.getLength());
            System.out.print(info);
            final ImageFram fram=new ImageFram(socket,dp);
            fram.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            fram.setVisible(true);
            fram.panel.getimage();
            fram.repaint();
//            socket.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}




class ImageFram extends JFrame{
        public ImagePanel panel;
        private static final int DEFAULT_WIDTH=300;
        private static final int DEFAULT_HEIGHT=200;
        private static Image image;
        public ImageFram(DatagramSocket ss,DatagramPacket dp){
            Toolkit kit= Toolkit.getDefaultToolkit();
            Dimension screenSize=kit.getScreenSize();
            int screenHeight=screenSize.height;
            int screenWidth=screenSize.width;

            setTitle("ImageTest");
            setLocation((screenWidth-DEFAULT_WIDTH)/2,(screenHeight-DEFAULT_HEIGHT)/2);
            setSize(DEFAULT_WIDTH,DEFAULT_HEIGHT);

            this.getContentPane().setLayout(null);
            panel =new ImagePanel(ss,dp);
            panel.setSize(640,480);
            panel.setLocation(0,0);
            add(panel);
        }
    }
class ImagePanel extends JPanel{
    private Image image;
    private ByteArrayInputStream bis;
    private DatagramSocket ss;
    private DatagramPacket dp;
    public ImagePanel(DatagramSocket ss, DatagramPacket dp){
        this.ss=ss;
        this.dp=dp;
    }
    public void getimage(){
        try {
            ss.receive(dp);
            ByteArrayInputStream input= new ByteArrayInputStream(dp.getData(),dp.getOffset(),dp.getLength());

            this.image= ImageIO.read(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if(image != null)
        {

                g.drawImage(image, 0, 0, null);

        }
    }
}


