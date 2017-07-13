/**
 * Created by zhangyan on 2017/5/22.
 */
import javax.imageio.ImageIO;
import javax.sound.midi.Receiver;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


/**
 * Created by zhangyan on 2017/5/22.
 */
public class rePad implements Runnable{
    public String messageToUp="";

    private int PORT=7800;
    private int SEQMAX=10;
    private int ImageBlockNumber=1;

    private static Image image=null;
    private int subHeight=0;
    private int imageHeight=800;
    private int imageWidth=1280;
    private int nowseq=-1;
    private JFrame showFrame=null;
    private ShowPanel showPanel=null;
    private boolean toend=false;
    private boolean isReceiving=false;
    public rePad(int port,int seqmax,int blocknumber){
        this.PORT=port;
        this.SEQMAX=seqmax;
        this.ImageBlockNumber=blocknumber;
        initCommonFrame();
    }
    private void initOther(){
        subHeight=imageHeight/ImageBlockNumber;
    }
    private void initCommonFrame(){
        showFrame=new JFrame("学生屏幕显示器");
        showFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        CloseWindowAdapter cwadapter = new CloseWindowAdapter();
        showFrame.addWindowListener(cwadapter);
        showFrame.setSize(1000,1500);
        showPanel=new ShowPanel();
//        Dimension ds=showPanel.getPerferredSize();
//        subHeight=ds.height;
//        imageWidth=ds.width;
//        showPanel.setSize(imageWidth,imageHeight);
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

    private class CloseWindowAdapter extends WindowAdapter
    {
        public void windowClosing(WindowEvent e)
        {
            stopreceive();
        }
    }
    public class ShowPanel extends JPanel{
//        public ShowPanel(){
//
//
//            this.setSize(imageWidth,imageHeight);
//        }
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            this.setSize(imageWidth,subHeight*ImageBlockNumber);
            if(image!=null){
                g.drawImage(image,0,0,null);
            }
        }
        public Dimension getPerferredSize(){
            return new Dimension(image.getWidth(this),image.getHeight(this));
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
    public void stopreceive()
    {
        this.toend = true;
        this.showFrame.dispose();
        this.showFrame = null;
        //这个对话框用于时间延迟，为了clientFrame的监测线程发现接收端的结束并置client = null
        JOptionPane.showMessageDialog(null, "窗口要被关闭了", "提示", JOptionPane.INFORMATION_MESSAGE);
    }
    public void setShowFrameVisible(boolean b)
    {
        if(b) showFrame.setVisible(true);
        else showFrame.setVisible(false);
    }
    @Override
    public void run() {
        this.toend=false;
        for(int i=0;i<ImageBlockNumber;i++){
            Receiver receiver= null;
            try {
                receiver = new Receiver(i);
                receiver.start();
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
    }

    private class Receiver extends Thread{
        private int senderNumber=-1;
        private DatagramPacket packet=null;
        private DatagramSocket socket=null;

        public Receiver(int senderNumber) throws SocketException {
            this.senderNumber=senderNumber;
            socket=new DatagramSocket(7899);
            socket.setReceiveBufferSize(409600);
            socket.setSoTimeout(2000);
        }

        @Override
        public void run() {
            byte[] bytes=new byte[409600];
            while (!toend){
                try {
                    packet=new DatagramPacket(bytes,bytes.length);
                    socket.receive(packet);
                    ByteArrayInputStream input=new ByteArrayInputStream(packet.getData(),packet.getOffset(),packet.getLength());
                    FileOutputStream fops=new FileOutputStream("now_student.jpg");
                    fops.write(packet.getData());
//                    nowseq=input.read();
//                    senderNumber=input.read();
                    System.out.println(socket.getInetAddress());
//                    System.out.println(nowseq);
//                    System.out.println(senderNumber);
                    image= ImageIO.read(input);

//                    Dimension ds=showPanel.getPerferredSize();
//                    subHeight=ds.height;
//                    imageWidth=ds.width;

                    showPanel.repaint();

                    isReceiving=true;

                    if(isReceiving && showFrame != null && !showFrame.isVisible())
                        showFrame.setVisible(true);


                } catch (IOException e) {
                    System.err.println("客户端线程run出现错误");
                }
            }
            messageToUp = "接收程序已终止";
        }
    }

    public static void main(String[] args) {
        rePad client=new rePad(7899,10,2);
        client.setShowFrameVisible(true);
        Thread clientThread=new Thread(client);
        clientThread.start();
    }

}
