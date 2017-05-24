/**
 * Created by zhangyan on 2017/5/15.
 */

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by zhangyan on 2017/5/15.
 */
public class testRe {
    private static BufferedImage subImage;
    private JFrame showFrame = null;
    private ShowPanel showPanel = null;
    public static void main(String[] args) {
        try {
            DatagramSocket socket=new DatagramSocket(7800);
            byte[] data=new byte[408000];
            DatagramPacket dp=new DatagramPacket(data,data.length);
            new  testRe().showFrame.setVisible(true);
            System.out.println("wait");
            socket.receive(dp);
            ByteArrayInputStream input= new ByteArrayInputStream(dp.getData(),dp.getOffset(),dp.getLength());
//            int one = input.read();
//            int two = input.read();
//            System.out.println(one);
//            System.out.println(two);
//            //System.out.println("nowseq="+nowseq+" senderNumber="+senderNumber);
//            JPEGImageDecoder decoder= JPEGCodec.createJPEGDecoder(input);
//            subImage = ImageIO.read(input);
//            String file="1.jpg";
//            OutputStream out=new FileOutputStream(file);
//            out.write(data);
//            out.flush();
//            out.close();
            System.out.println(data[0]);
            String info=new String(data,0,dp.getLength());
            System.out.print(info);

            socket.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//    public void setShowFrameVisible(boolean b)
//    {
////        if(b) showFrame.setVisible(true);
////        else showFrame.setVisible(false);
//    }
    private class ShowPanel extends JPanel {


        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(subImage,1024,98,showFrame);
//            if (subImages != null) {
//                for (int i = 0; i < ImageBlockNumber; i++) {//将接收到的缓冲的图像显示出来
//                    g.drawImage(subImages[i], 0, subHeight * i, showFrame);
//                }
//            }
        }
    }
    private class FullScreenFrame extends JFrame
    {
        public FullScreenFrame()
        {
            this.setUndecorated(true);
            this.setExtendedState(JFrame.MAXIMIZED_BOTH);
            this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            this.addMouseListener(
                    new MouseAdapter()
                    {
                        public void mouseClicked(MouseEvent e)
                        {
                            int clickCount = e.getClickCount();
                            if(clickCount == 2)
                            {
                                showFrame.dispose();
                                initCommonFrame();
                            }
                        }
                    });
        }
    }

    private void initCommonFrame()
    {
        showFrame = new JFrame("接收端");
        showFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        showFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//        CloseWindowAdapter cwadapter = new CloseWindowAdapter();
        showFrame.setSize(1024,768);
        showPanel = new ShowPanel();
        showFrame.add(showPanel);
        showFrame.setVisible(true);
        showFrame.addMouseListener(
                new MouseAdapter()
                {
                    public void mouseClicked(MouseEvent e)
                    {
                        int clickCount = e.getClickCount();
                        if(clickCount == 2)
                        {
                            showFrame.dispose();
                            initFullScreenFrame();
                        }
                    }
                });
        initOther();
    }
    public void initFullScreenFrame()
    {
        showFrame = new FullScreenFrame();
        showPanel = new ShowPanel();
        showFrame.add(showPanel);
        showFrame.setVisible(true);
        initOther();
    }
    private void initOther()
    {
        ;
    }

}
