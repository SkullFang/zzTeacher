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
	ArrayList<ChatSocket> soketList=new ArrayList<>();
	@Override
	public void run() {
		System.out.println("server start");
		BufferedWriter writer;
		//1-65535
		try {
			ServerSocket serverSocket = new ServerSocket(12370);
			new ImageServer().getServerSocket(serverSocket);

			while (true) {
				//block
				Socket socket = serverSocket.accept();
//				String iphand=socket.getInetAddress().toString().trim();
//				String ip=iphand.substring(1,iphand.length());
//				getMac.getMacAddress(ip);



				writer=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//				JOptionPane.showMessageDialog(null, "有客户端链接\n");
				InetAddress student=socket.getInetAddress();
				builder.append(student.toString()+"\n");

				ChatSocket cs = new ChatSocket(socket);
				cs.start();
				ChatManager.getChatManager().add(cs);
				teachClient tc=new teachClient();
				soketList.add(cs);

//				new teachClient().sendbuilder(builder);
				tc.sendbuilder(builder);
				tc.sendSocket(soketList);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new ServerListener().start();
	}



}
