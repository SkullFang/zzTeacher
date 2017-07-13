import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class ServerListener extends Thread {
//
//	public List<String>  ipList = new ArrayList<>();
//	
//	public List<String> getIpList() {
//		return ipList;
//	}
//
//	public void setIpList(List<String> ipList) {
//		this.ipList = ipList;
//	}
	StringBuilder builder=new StringBuilder();
	ArrayList<Socket> soketList=new ArrayList<>();
	@Override
	public void run() {
		BufferedWriter writer;
		//1-65535
		try {
			ServerSocket serverSocket = new ServerSocket(12370);
			new ImageServer().getServerSocket(serverSocket);
			while (true) {
				//block
				Socket socket = serverSocket.accept();
				writer=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//				JOptionPane.showMessageDialog(null, "有客户端链接\n");
				InetAddress student=socket.getInetAddress();
				builder.append(student.toString()+"\n");
				soketList.add(socket);
				ChatSocket cs = new ChatSocket(socket);
				cs.start();
				ChatManager.getChatManager().add(cs);
				teachClient tc=new teachClient();
//				new teachClient().sendbuilder(builder);
				tc.sendbuilder(builder);
				tc.sendSocket(soketList);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
