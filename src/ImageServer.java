import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import javax.imageio.*;
import javax.swing.*;
import java.net.Socket;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;

/**
*�ڷ�������������£������ͻ��ˣ������׽��ֽ���ͼ��
*/
public class ImageServer extends Thread {
	static ServerSocket ss;
	public void getServerSocket(ServerSocket serverSocket) {
		ss=serverSocket;
		// TODO Auto-generated method stub
		
	}

    public void run(){    
//    	try {
//			ss = new ServerSocket(12300);
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//        
        final ImageFrame frame = new ImageFrame(ss);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
       
        while(true){
        	try {
				frame.panel.getimage();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            frame.repaint();
        }        
    }


       
}

/** 
    A frame with an image panel
*/
@SuppressWarnings("serial")
class ImageFrame extends JFrame{
	public ImagePanel panel;
    public ImageFrame(ServerSocket ss){
   	    // get screen dimensions   	   
   	    Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;

        // center frame in screen
        setTitle("ImageTest");
        setLocation((screenWidth - DEFAULT_WIDTH) / 2, (screenHeight - DEFAULT_HEIGHT) / 2);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);

        // add panel to frame
        this.getContentPane().setLayout(null);
        panel = new ImagePanel(ss);
        panel.setSize(730,870);
        panel.setLocation(0, 0);
        add(panel);
//        jb = new JButton("����");
//        jb.setBounds(0,480,640,50);
//        add(jb);
//        saveimage saveaction = new saveimage(ss);
//        jb.addActionListener(saveaction);
    }

    public static final int DEFAULT_WIDTH = 730;
    public static final int DEFAULT_HEIGHT = 870;  
}

/**
   A panel that displays a tiled image
*/
@SuppressWarnings("serial")
class ImagePanel extends JPanel {     
    private ServerSocket ss;
    private Image image;
    private InputStream ins;
    private DatagramSocket ds;
	 
    public ImagePanel(ServerSocket ss) {  
	    this.ss = ss;
    }
    
    public void getimage() throws IOException{
    	Socket s = this.ss.accept();
        System.out.println("���ӳɹ�!");
        ds=new DatagramSocket(7800);
        byte[] data=new byte[480000];
        String info=new String(data,data.length);
        System.out.println(info);
        DatagramPacket dp=new DatagramPacket(data, data.length);
        ds.receive(dp);
        ByteArrayInputStream bis=new ByteArrayInputStream(dp.getData(), dp.getOffset(), dp.getLength());
        this.image=ImageIO.read(bis);
//        this.ins = s.getInputStream();
//		this.image = ImageIO.read(ins);
		this.ins.close();
    }
   
    public void paintComponent(Graphics g){  
        super.paintComponent(g);    
        if (image == null)
        {
        	System.out.println("null");
        }
        else{
        	System.out.println("success");
        g.drawImage(image, 0, 0, null);
        }
    }

}

//class saveimage implements ActionListener {
//	RandomAccessFile inFile = null;
//	byte byteBuffer[] = new byte[1024];
//	InputStream ins;
//	private ServerSocket ss;
//	
//	public saveimage(ServerSocket ss){
//		this.ss = ss;
//	}
//	
//	public void actionPerformed(ActionEvent event){
//        try {
//			Socket s = ss.accept();
//			ins = s.getInputStream();
//			
//			// �ļ�ѡ�����Ե�ǰ��Ŀ¼��
//	        JFileChooser jfc = new JFileChooser(".");
//	        jfc.showSaveDialog(new javax.swing.JFrame());
//	        // ��ȡ��ǰ��ѡ���ļ�����
//	        File savedFile = jfc.getSelectedFile();
//	        
//	        // �Ѿ�ѡ�����ļ�
//	        if (savedFile != null) {
//	            // ��ȡ�ļ������ݣ�����ÿ���Կ�ķ�ʽ��ȡ����
//	            try {
//					inFile = new RandomAccessFile(savedFile, "rw");
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				}
//	        }
//
//            int amount;
//            while ((amount = ins.read(byteBuffer)) != -1) {
//                inFile.write(byteBuffer, 0, amount);
//            }
//            inFile.close();
//            ins.close();
//            s.close();
//            javax.swing.JOptionPane.showMessageDialog(new javax.swing.JFrame(),
//                    "�ѽӱ���ɹ�", "��ʾ!", javax.swing.JOptionPane.PLAIN_MESSAGE);
//		} catch (IOException e) {
//
//			e.printStackTrace();
//		}
//	}
//}
