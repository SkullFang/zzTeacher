import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Created by hadoop on 3/16/17.
 */
public class TestSwing extends JFrame{
    MyPanel1 mp=null;
    public TestSwing(){
        mp=new MyPanel1();
        this.add(mp);
        this.setSize(550, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public static void main(String[] args){
        new TestSwing();
    }
}
class MyPanel1 extends JPanel{

    Image image=null;

    public void paint(Graphics g){
        try {
            image= ImageIO.read(new File("/home/hadoop/job/java/1.jpg"));
            g.drawImage(image, 0, 0, 550, 400, null);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
