import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;
import java.util.Random;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;
import javax.swing.*;

public class ImageApp extends JFrame {
    public ImageApp() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(400, 300);
        setResizable(false);
        getContentPane().setLayout(null);
        ImagePanel panel = new ImagePanel();
        panel.setBounds(0, 0, 400, 300);
        getContentPane().add(panel);
        setVisible(true);

        panel.toShow();
        Random random = new Random();
        int rgb = random.nextInt(102400);
        while(true){
            getImagePixel("2.jpg", rgb);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    panel.toShow();
                    //panel.repaint();
                    panel.updateUI();
                    /*try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                    //panel.updateUI();
                }
            });
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public static void getImagePixel(String image, int newRGB){
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(new FileImageInputStream(
                    new File(image)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        int width = bi.getWidth();
        int height = bi.getHeight();
        Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpg");
        if(iter.hasNext()){
            ImageWriter writer = iter.next();
            ImageWriteParam param = writer.getDefaultWriteParam();
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    bi.setRGB(i, j, newRGB);
                }
            }
            try {

                FileImageOutputStream out = new FileImageOutputStream(new File(
                        "2.jpg"));
                writer.setOutput(out);
                writer.write(null, new IIOImage(bi, null, null), null);
                out.close();
                writer.dispose();
//图片写完毕，通知lable更新内容
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        new ImageApp();
    }
    class ImagePanel extends JPanel {
        public void paint(Graphics g) {
            super.paint(g);
// ImageIcon icon = new ImageIcon("D:\\1.jpg");
            //ImageIcon icon = new ImageIcon("/home/hadoop/job/java/3.jpg");
            //g.drawImage(icon.getImage(), 0, 0, 400, 300, this);
        }

        public void toShow(){
            System.out.println("ok");
            Graphics g = this.getGraphics();
            ImageIcon icon = new ImageIcon("2.jpg");
            g.drawImage(icon.getImage(), 0, 0, 400, 300, this);
        }
    }
}