/**
 * Created by zhangyan on 2017/5/15.
 */
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Reimage implements Runnable {
    /**
     * 表示要传到上一层的消息（实际上是上层类查看这个属性）
     */
    public String messageToUp = "";

    private int PORT = 9997;
    //定义了发送方发送数据包的编号最大值
    private int SEQMAX = 10;
    private int ImageBlockNumber = 4;//把图像分块：垂直方向分成ImageBlockNumber块

    //注意Robot这个类的使用，很重要的，这里可以控制鼠标运动和截图
    private Toolkit toolkit;
    private static BufferedImage[] subImages = null;//这个是把bufferedImage分成份后存放
    private int subHeight = 0;
    private int imageHeight = 800;//默认接收到的图片的高度为800
    private int imageWidth = 1280;//默认接收到的图片的宽度为1280
    private int nowseq = -1;//当前接收到的图片编号
    private JFrame showFrame = null;
    private JScrollPane scrollPane = null;
    private ShowPanel showPanel = null;
    private InetAddress multicastIA = null;//多播用的D类IP地址
    private boolean toend = false;
    private boolean isReceiving = false;//表示当前是不是正在接收服务端的数据
    private static Image image;

    /**
     * 默认构造方法
     * 默认将组播IP地址定为239.66.69.18，起始端口号9997，发送图片最大序列号10，4线程发送
     */
    public Reimage() {
//        try {
//            multicastIA = InetAddress.getByName("224.0.0.2");
//        } catch (UnknownHostException e) {
//            System.out.println("创建multicastIA异常");
//        }
        initCommonFrame();
    }

    /**
     * 此构造方法需要设定组播IP地址、端口号、发送图片最大序号、发送分的块数
     */
    public Reimage(String multicastIP, int port, int seqmax, int blocknumber) {
        try {
            multicastIA = InetAddress.getByName(multicastIP);
        } catch (UnknownHostException e) {
            System.out.println("创建multicastIA异常");
        }
        this.PORT = port;
        this.SEQMAX = seqmax;
        this.ImageBlockNumber = blocknumber;
        initCommonFrame();
    }

    //初始化部分
    private void initOther() {
        subHeight = imageHeight / ImageBlockNumber;//设置一个默认的分块高度
        subImages = new BufferedImage[ImageBlockNumber];
    }

    /**
     * 初始化标准窗口式的显示窗口
     */
    private void initCommonFrame() {
        showFrame = new JFrame("接收端");
        showFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        CloseWindowAdapter cwadapter = new CloseWindowAdapter();
        showFrame.addWindowListener(cwadapter);
        showFrame.setSize(1024, 768);
        showPanel = new ShowPanel();
        showFrame.add(showPanel);
        showFrame.setVisible(true);
        showFrame.addMouseListener(
                new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        int clickCount = e.getClickCount();
                        if (clickCount == 2) {
                            showFrame.dispose();
                            initFullScreenFrame();
                        }
                    }
                });
        initOther();
    }

    public void initFullScreenFrame() {
        showFrame = new FullScreenFrame();
        showPanel = new ShowPanel();
        showFrame.add(showPanel);
        showFrame.setVisible(true);
        initOther();
    }

    public void run() {
        this.toend = false;
        try {
            Receiver receiver=new Receiver(1);
            receiver.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        for (int i = 0; i < ImageBlockNumber; i++) {
//            try {
//                Receiver receiver = new Receiver(i);
//                receiver.start();
//            } catch (SocketException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    /**
     * 通过置标志位来结束接收线程
     */
    public void stopreceive() {
        this.toend = true;
        this.showFrame.dispose();
        this.showFrame = null;
        //这个对话框用于时间延迟，为了clientFrame的监测线程发现接收端的结束并置client = null
        JOptionPane.showMessageDialog(null, "接受线程正在关闭，请点击确认按钮", "提示", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 显示或隐藏接收窗口
     *
     * @param b
     */
    public void setShowFrameVisible(boolean b) {
        if (b) showFrame.setVisible(true);
        else showFrame.setVisible(false);
    }

    private class Receiver extends Thread {
        /**
         * 此接收方的编号
         */
        private int senderNumber = -1;
        private DatagramPacket packet = null;
        private MulticastSocket multicastSocket = null;//用于组播传送的
        private DatagramSocket dsocket=null;

        public Receiver(int senderNumber) throws SocketException, IOException {
//            this.senderNumber = senderNumber;
//            multicastSocket = new MulticastSocket(PORT + senderNumber);
//            multicastSocket.setReceiveBufferSize(409600);
//            multicastSocket.setSoTimeout(1000);//如果阻塞2秒还没接收到就超时
//            multicastSocket.joinGroup(multicastIA);
            dsocket=new DatagramSocket(7800);

        }

        public void run() {
            byte[] bytes = new byte[409600];
            packet=new DatagramPacket(bytes,bytes.length);
            try {
                dsocket.receive(packet);
                ByteArrayInputStream bis=new ByteArrayInputStream(packet.getData(),packet.getOffset(),packet.getLength());
                String info=new String(bytes,0,packet.getLength());
                System.out.print(info);
                image= ImageIO.read(bis);

            } catch (IOException e) {
                e.printStackTrace();
            }
            //System.out.println("进入线程了");
//            while (!toend) {
//                try {
//                    packet = new DatagramPacket(bytes, bytes.length);
//                    if (!isReceiving) messageToUp = "没有接收到数据";
//                    dsocket.receive(packet);
//                    ByteArrayInputStream input = new ByteArrayInputStream(packet.getData(), packet.getOffset(), packet.getLength());
//                    nowseq = input.read();
//                    senderNumber = input.read();
//                    System.out.println(nowseq);
//                    System.out.println(senderNumber);
//                    //System.out.println("nowseq="+nowseq+" senderNumber="+senderNumber);
//                    JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(input);
//                    subImages[senderNumber] = decoder.decodeAsBufferedImage();
//
//                    if (this.senderNumber == 0) {//0号线程负责处理屏幕大小的问题
//                        subHeight = subImages[senderNumber].getHeight();
//                        imageWidth = subImages[senderNumber].getWidth();
//                    }
//                    showPanel.repaint();//重新制作
//                    //下面获取主机名需要较长时间，不能每张图片都去应用
////                    messageToUp = "<html>收到数据，<br>来自主机：" + packet.getAddress().getHostAddress()
////                            + "/" + packet.getAddress().getHostName() + "</html>";
//                    isReceiving = true;
//                    if (isReceiving && showFrame != null && !showFrame.isVisible())
//                        showFrame.setVisible(true);
//
//                }    //try end
//                catch (Exception e) {
//                    System.err.println("客户端线程run出现错误");
//                }
//            }//end while
            //System.err.println("线程" + this.senderNumber + "退出了");
            messageToUp = "接收程序已终止";
        }
    }

    private class ShowPanel extends JPanel {
        public ShowPanel() {
            this.setSize(imageWidth, imageHeight);
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            this.setSize(imageWidth, subHeight * ImageBlockNumber);
            g.drawImage(image,1024,986,showFrame);
//            if (subImages != null) {
//                for (int i = 0; i < ImageBlockNumber; i++) {//将接收到的缓冲的图像显示出来
//                    g.drawImage(subImages[i], 0, subHeight * i, showFrame);
//                }
//            }
        }
    }

    private class CloseWindowAdapter extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            stopreceive();
        }
    }

    private class FullScreenFrame extends JFrame {
        public FullScreenFrame() {
            this.setUndecorated(true);
            this.setExtendedState(JFrame.MAXIMIZED_BOTH);
            this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            this.addMouseListener(
                    new MouseAdapter() {
                        public void mouseClicked(MouseEvent e) {
                            int clickCount = e.getClickCount();
                            if (clickCount == 2) {
                                showFrame.dispose();
                                initCommonFrame();
                            }
                        }
                    });
        }
    }

    public static void main(String[] args) {
        Reimage client = new Reimage("224.0.0.2", 7800, 10, 4);
        client.setShowFrameVisible(true);
        Thread clientThread = new Thread(client);
        clientThread.start();
    }
}