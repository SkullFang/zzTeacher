/**
 * 注意：
 * 如果分成2个线程，发送的单个数据包就太大，而导致发送错误
 */

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import javax.swing.JWindow;

/**
 * 这是服务器端，就是屏幕信息的发送方
 */

public class ServerComponent implements Runnable
{
    private int PORT = 9997;
    //定义了发送方发送数据包的编号最大值
    private int SEQMAX = 10;//发送图片最大序列号
    private int ImageBlockNumber = 4;//把图像分块：垂直方向分成ImageBlockNumber块
    private InetAddress multicastIA = null;//多播用的D类IP地址
    private int sendInterval = 10;//表示间隔多长时间发送一张图片
    private float compressRate = 0.5F;//画面的压缩比
    //--------上面是构造方法中可以更改的

    private Robot robot;
    //注意Robot这个类的使用，很重要的，这里可以控制鼠标运动和截图
    private Toolkit toolkit;
    private InetAddress myaddress = null;//服务端本机IP地址
    private BufferedImage bufferedImage = null;
    private BufferedImage[] subImages = null;//这个是把bufferedImage分成份后存放
    private int nowseq = -1;//当前发送的数据包图片的编号
    private boolean[] sendable = null;//用来标记发送线程当前是否可以发送
    private boolean toend;//用于终止发送各线程的控制信号

    /**
     * 默认构造方法
     * 默认将组播IP地址定为239.66.69.18，起始端口号9997，发送图片最大序列号10，4线程发送
     */
    public ServerComponent()
    {
        try
        {
            multicastIA = InetAddress.getByName("224.0.0.2");
            PORT = 9997;
            SEQMAX = 10;
            ImageBlockNumber = 4;
            initServer();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * 此构造方法需要设定组播IP地址、端口号、发送图片最大序号、发送分的块数
     */
    public ServerComponent(String multiIP,int port,int seqmax,int imageBlockNumber)
    {
        try
        {
            multicastIA = InetAddress.getByName(multiIP.trim());
            PORT = port;
            SEQMAX = seqmax;
            ImageBlockNumber = imageBlockNumber;
            initServer();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    //通用初始化方法
    private void initServer() throws AWTException, UnknownHostException
    {
        robot = new Robot();
        toolkit = Toolkit.getDefaultToolkit();
        sendable = new boolean[ImageBlockNumber];//四个线程，标记是否可以发送。
        for(int i=0;i<sendable.length;i++)
            sendable[i] = false;  //初始化为否
        myaddress = InetAddress.getLocalHost();//获得自己的ip
        toend = false;  //这是一个控制信号，用来控制结束发送。
    }

    /**
     * gainImage方法用来获取屏幕截图，并把截图分成ImageBlockNumber块
     */
    private void gainImage()
    {
        Dimension screenDimension = toolkit.getScreenSize(); //用于封装对象组件的高度和宽度。
        int subHeight = screenDimension.height/ImageBlockNumber;//表示每块的高度
        bufferedImage = robot.createScreenCapture(new Rectangle(screenDimension));
        subImages = new BufferedImage[ImageBlockNumber];
        for(int i=0;i<ImageBlockNumber;i++)
        {
            subImages[i] = bufferedImage.getSubimage(0, i*subHeight, screenDimension.width, subHeight);
        }// bufferedImage.getSubimage 方法c创建一个举行区域定义的子图像
    }
    /**
     * 创建发送线程，并启动进行发送
     */
    private void createSenders()
    {
        for(int i=0;i<ImageBlockNumber;i++)
        {
            try
            {
                Sender sender = new Sender(i);
                sender.start();
            }
            catch(SocketException e){System.err.println("socket = new DatagramSocket(PORT)错误，无法创建发送线程");}
            catch(IOException e){System.err.println("添加组播成员错误");}
        }
    }
    /**
     * 设置发送图片的间隔时间
     * @param time 间隔时间，单位为毫秒
     */
    public void setSendInterval(int time)
    {
        this.sendInterval = time;
    }
    /**
     * 修改压缩比
     */
    public void setCompressRate(float rate)
    {
        this.compressRate = rate;
    }
    /**
     * 停止发送，退出此类对象线程和其相应的发送线程
     */
    public void stopSend()
    {
        this.toend = true;
    }
    public void run()
    {
        this.toend = false;
        this.gainImage();  //获得图片
        createSenders(); //创建子线程
        while(!toend)
        {
            this.gainImage();
            //////////////////////////////////////////////////////////////////////////////
            //System.out.println("获得了图片了");
            this.nowseq = (nowseq+1)%SEQMAX;
            //置许可发送数组标志位为全部可以发送
            for(int i=0;i<sendable.length;i++)
                sendable[i] = true;
            while(true)
            {
                boolean isfinished = true;
                for(int i=0;i<sendable.length;i++)
                {
                    if(sendable[i]) isfinished = false;//有一个没发送完就没全完成
                }
                if(isfinished) break;//完成此次图片的发送，就可以准备发送下一幅图片了
                try
                {//相当于等待发送完成时间，就是最小截图时间
                    Thread.sleep(sendInterval);
                }
                catch(InterruptedException e){System.err.println("Thread.sleep出现错误");}
            }
        }

    }

    /**
     * 此类用来完成图片发送一块的任务，使用多线程
     * @author me
     */
    private class Sender extends Thread
    {
        /**
         * 此发送方的编号
         */
        private int senderNumber = -1;
        /**
         * 此线程要发送的图片块
         */
        private BufferedImage subBufferedImage = null;
        private DatagramPacket packet = null;
        private MulticastSocket multicastSocket = null;//用于组播传送的
        public Sender(int senderNumber) throws SocketException, IOException
        {
            this.senderNumber = senderNumber;
            multicastSocket = new MulticastSocket(PORT+senderNumber);//不同端口发送数据
            multicastSocket.setSendBufferSize(409600); //设置buffer
            multicastSocket.joinGroup(multicastIA); //加入广播组
        }
        /**
         * 准备要发送的数据报文
         */
        public void prepareDatagram()//打包
        {
            this.subBufferedImage = subImages[senderNumber];
            ByteArrayOutputStream output=new ByteArrayOutputStream();//数据流
            output.write(nowseq);//加入一个字节表示传送的是哪张图片
            output.write(this.senderNumber);//加入一个字节表示传送的是图片中的哪个部分
            //将位图格式的图片压缩成JPEG格式，以减少传输的数据量
            JPEGEncodeParam param=JPEGCodec.getDefaultJPEGEncodeParam(subBufferedImage);
            param.setQuality(compressRate,false);
            JPEGImageEncoder encoder=JPEGCodec.createJPEGEncoder(output,param);
            try
            {
                encoder.encode(subBufferedImage);
                encoder.getOutputStream().close();
                packet = new DatagramPacket(output.toByteArray(),output.size(),multicastIA,PORT+senderNumber);
            }
            catch(SocketException e){System.err.println("Socket错误");}
            catch(IOException e){System.err.println("IO错误：可能是JPEG编码出现问题");}
        }

        public void run()
        {
            while(!toend)//如果主控线程要求结束的话，发送线程也要退出
            {
                if(sendable[senderNumber])
                {//可以发送
                    //准备数据包
                    prepareDatagram();
                    try
                    {//发送数据包
                        multicastSocket.send(packet);
                        System.out.println("线程"+this.senderNumber+"发送了");
                    }
                    catch(IOException e){System.err.println("发送线程"+senderNumber+"数据包发送异常");}
                    sendable[senderNumber] = false;
                }
                else
                {//还不能发送，先阻塞一段时间
                    try
                    {
                        Thread.sleep(sendInterval);
                    }
                    catch(InterruptedException e){System.err.println("Thread.sleep出现错误");}
                }
            }
        }
    }

    public static void main(String []args)
    {
        ServerComponent server = new ServerComponent();
        Thread serverThread = new Thread(server);
        serverThread.start();
    }
    public static void StartThis(){
        ServerComponent server = new ServerComponent();
        Thread serverThread = new Thread(server);
        serverThread.start();
    }


}
/*
class MyWindow extends JWindow
{
    private BufferedImage bufferedImage = null;
    public MyWindow(BufferedImage bufferedImage)
    {
        this.setBounds(0, 0, 800, 800);
        this.setVisible(true);
        this.bufferedImage = bufferedImage;
        this.repaint();
    }

    public void paint(Graphics g)
    {
        super.paintComponents(g);
        g.drawImage(bufferedImage, 10, 10, this);
        g.drawString("看到了吗", 50, 50);
    }
}
 * */