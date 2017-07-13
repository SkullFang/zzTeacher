import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by zhangyan on 2017/7/5.
 */
public class textWrite {
    public static void main(String[] args) {
        String filepath="student.txt";
        File f=new File(filepath);
        try {
            if(!f.exists()){
                f.createNewFile();
            }
            String a="zhangyan";
            String b="120";
            BufferedWriter output=new BufferedWriter(new FileWriter(f));
            output.write(a);
            output.write(b);
            output.flush();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
