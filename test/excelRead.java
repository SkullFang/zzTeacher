/**
 * Created by zhangyan on 2017/7/5.
 *
 */
import java.io.*;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;

import java.util.HashMap;
import java.util.Map;
public class excelRead {
    public static void main(String[] args) {
        HashMap map=new HashMap();
        try {
            Workbook bookResource=Workbook.getWorkbook(new File("name.xls"));
            Sheet sheet1=bookResource.getSheet(0);
            int Rows=sheet1.getRows();
            int Colums=sheet1.getColumns();
            for(int i=1;i<Rows;i++){
                Cell Ip=sheet1.getCell(0,i);
                Cell name=sheet1.getCell(1,i);
                System.out.print(Ip.getContents()+" "+name.getContents());
                map.put(Ip.getContents().toString(),name.getContents().toString());
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
        System.out.println(map.get("192.168.1.104"));
    }
}

