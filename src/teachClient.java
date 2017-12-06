import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.crypto.Mac;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Timer;
import javax.swing.JTextPane;
import java.io.*;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;

import java.util.HashMap;

public class teachClient extends JFrame {
	static StringBuilder bu;
	static ArrayList<ChatSocket>al;

	private JPanel contentPane;
	private JTextField textField;
	public static void sendbuilder(StringBuilder builder){
		bu=builder;
	}
	public static void sendSocket(ArrayList<ChatSocket> sll){
		al=sll;
	}
//	public static void printSocket(){
//		StringBuilder showbu=null;
//		for(Socket ss:al){
//			int flag=0;
//			try {
//				ss.sendUrgentData(0);
//				flag=1;
//			} catch (IOException e) {
////				e.printStackTrace();
//				flag=0;
//
//			}
//			if(flag==1) {
//				showbu.append(ss.toString()+'\n');
//				System.out.println(ss.getInetAddress());
//			}
//		}
//	}


	/**
	 * Launch the application.
	 */
	class MyTask extends java.util.TimerTask{

		private HashMap mapp;
		MyTask(HashMap mapp){
			this.mapp=mapp;
//				System.out.println(mapp.get("127.0.0.1"));
		}
		public void run(){
			System.out.println("________");
			StringBuilder showbu=new StringBuilder();
			for(ChatSocket ss:al){
				int flag=0;
				try {
					ss.socket.sendUrgentData(0);
					flag=1;
				} catch (IOException e) {
//				e.printStackTrace();
					flag=0;

				}
				if(flag==1) {
					System.out.println(ss.socket.getInetAddress());
					String name="wo";
//						System.out.println(mapp.get("127.0.0.1"));
					String ip=ss.socket.getInetAddress().toString().trim();
					String iph=ip.substring(1,ip.length());
					try {
						String macad=getMac.getMacAddress(iph);
						name = mapp.get(macad).toString();
						System.out.println(name+s);
					} catch (Exception e) {
						e.printStackTrace();
					}


					showbu.append(ip+" "+name+'\n');
				}else{
					ChatManager.getChatManager().vector.remove(ss);
				}
			}
			textArea.setText(showbu.toString());
		}
	}
	public static void main(String[] args) {
		new ServerListener().start();

//		new SendBreak().start();

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					teachClient frame = new teachClient();
					frame.initLink();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	class autoSendBreak extends Thread{
		Socket ss=null;
		boolean isconnect=true;
		autoSendBreak(Socket ss){
			this.ss=ss;
		}
		@Override


		public void run() {



			while (isconnect) {
				try {

					ss.sendUrgentData(0);
					System.out.println("发送心跳");
				} catch (IOException e) {
					isconnect=false;
					try {
						ss.close();
						ss=new Socket("127.0.0.1", 12370);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}

	public void initLink() {
		try {
			/*
			教师登录属于项目初始化内容。教师离线不影响项目运行
			 */
			socket = new Socket(textField.getText().toString(), 12370);
			socket = new Socket("127.0.0.1", 12370);

//			new autoSendBreak(socket).start();
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		map=getmap();
		LogIp();
		Timer timer = new Timer();
		timer.schedule(new MyTask(map), 1000, 2000);//在1秒后执行此任务,每次间隔2秒执行一次,如果传递一个Data参数,就可以在某个固定的时间执行这个任务

	}
	public HashMap getmap(){
		HashMap map=new HashMap();
		try {
			Workbook bookResource=Workbook.getWorkbook(new File("name.xls"));
			Sheet sheet1=bookResource.getSheet(0);
			int Rows=sheet1.getRows();
			int Colums=sheet1.getColumns();
			for(int i=1;i<Rows;i++){
				Cell Mac=sheet1.getCell(0,i);
				Cell name=sheet1.getCell(1,i);
//				System.out.print(Ip.getContents()+" "+name.getContents());
				map.put(Mac.getContents().toString(),name.getContents().toString());
//				System.out.println();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BiffException e) {
			e.printStackTrace();
		}
		return map;
	}
	public static void LogIp(){
		String shoubu=new String();
		for(ChatSocket ss:al){
			int flag=0;
			try {
				ss.socket.sendUrgentData(0);
				flag=1;
			} catch (IOException e) {
//				e.printStackTrace();
				flag=0;

			}
			if(flag==1) {
				shoubu+=ss.socket.getInetAddress().toString()+'\n';
//				System.out.println(ss.getInetAddress());
			}
		}

		String filepath="student.txt";
		File f=new File(filepath);
		try {
			if(!f.exists()){
				f.createNewFile();
			}
			BufferedWriter output=new BufferedWriter(new FileWriter(f));
			output.write(shoubu);
			output.flush();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	/*
	让锁定和解锁命令间隔发两次。
	 */
	public class sendDouble extends Thread{
		String comm="";
		sendDouble(String comm){
			this.comm=comm;
		}

		@Override
		public void run() {
			for(int i=1;i<=2;i++){
				ChatManager.getChatManager().publish(null,comm);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Create the frame.
	 */
	Socket socket=null;
	BufferedWriter writer=null;
	BufferedReader reader=null;
	private JTextArea textArea;
	private JTextField textField_1;
	private HashMap map;
	static int s=0;
	public teachClient() {
//		class MyTask extends java.util.TimerTask{
//
//			private HashMap mapp;
//			MyTask(HashMap mapp){
//				this.mapp=mapp;
////				System.out.println(mapp.get("127.0.0.1"));
//			}
//			public void run(){
//				System.out.println("________");
//				StringBuilder showbu=new StringBuilder();
//				for(Socket ss:al){
//					int flag=0;
//					try {
//						ss.sendUrgentData(0);
//						flag=1;
//					} catch (IOException e) {
////				e.printStackTrace();
//						flag=0;
//
//					}
//					if(flag==1) {
//						System.out.println(ss.getInetAddress());
//						String name="wo";
////						System.out.println(mapp.get("127.0.0.1"));
//						String ip=ss.getInetAddress().toString().trim();
//							name = mapp.get(ip).toString();
//						System.out.println(name+s);
//						showbu.append(ip+" "+name+'\n');
//					}
//				}
//				textArea.setText(showbu.toString());
//			}
//		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 443, 669);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		textField = new JTextField();
		try {
			String teacherIP = InetAddress.getLocalHost().getHostAddress();
			textField.setText(teacherIP);
			textField.setColumns(10);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}


		JLabel lblNewLabel = new JLabel("填写IP：");

		JButton button = new JButton("链接");
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					socket=new Socket(textField.getText().toString(),12370);
					writer=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();				}

			}
		});


		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "\u5B66\u751F\u6210\u5458\u5217\u8868", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		JButton button_1 = new JButton("锁定");
		button_1.addMouseListener(new MouseAdapter() {
			@SuppressWarnings("null")
			@Override
			public void mouseClicked(MouseEvent e) {

//				ChatManager.getChatManager().publish(null, "1");
				new sendDouble("1").start();
			}

		});

		JButton button_2 = new JButton("解锁");
		button_2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
//				ChatManager.getChatManager().publish(null, "2");
				new sendDouble("2").start();
			}
		});

		JButton button_3 = new JButton("停止");
		button_3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
//				ChatManager.getChatManager().publish(null,"3");
				new sendDouble("3").start();

			}
		});

		JButton btnNewButton_1 = new JButton("ͬ同步屏幕");
//		btnNewButton_1.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent arg0) {
//			}
//		});
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ServerComponent.StartThis();
			}
		});

		JButton btnNewButton_2 = new JButton("查看学生屏幕");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String sendmessage=textField_1.getText();
				ChatManager.getChatManager().publish(null, sendmessage.toString());
				rePad client=new rePad(7800,10,2);
				client.setShowFrameVisible(true);
				Thread clientThread=new Thread(client);
				clientThread.start();

			}
		});
//		textField_1 = new JTextField();
//		textField_1.setColumns(10);
//		GroupLayout gl_contentPane = new GroupLayout(contentPane);
//		gl_contentPane.setHorizontalGroup(
//				gl_contentPane.createParallelGroup(Alignment.TRAILING)
//						.addGroup(gl_contentPane.createSequentialGroup()
//								.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
//										.addGroup(gl_contentPane.createSequentialGroup()
//												.addGap(15)
//												.addComponent(lblNewLabel)
//												.addPreferredGap(ComponentPlacement.RELATED)
//												.addComponent(textField, GroupLayout.PREFERRED_SIZE, 208, GroupLayout.PREFERRED_SIZE)
//												.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
//												.addComponent(button, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE))
//										.addGroup(gl_contentPane.createSequentialGroup()
//												.addContainerGap()
//												.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
//														.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
//																.addComponent(btnNewButton_1, GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
//																.addComponent(button_1, GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
//																.addComponent(button_2, GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE))
//														.addComponent(btnNewButton_2))
//												.addGap(27)
//												.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
//														.addComponent(textField_1)
//														.addComponent(panel, GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE))
//												.addGap(13)))
//								.addContainerGap())
//		);
//		gl_contentPane.setVerticalGroup(
//				gl_contentPane.createParallelGroup(Alignment.LEADING)
//						.addGroup(gl_contentPane.createSequentialGroup()
//								.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
//										.addComponent(lblNewLabel)
//										.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
//										.addComponent(button))
//								.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
//										.addGroup(gl_contentPane.createSequentialGroup()
//												.addGap(38)
//												.addComponent(btnNewButton_1)
//												.addGap(18)
//												.addComponent(button_1)
//												.addGap(18)
//												.addComponent(button_2))
//										.addGroup(gl_contentPane.createSequentialGroup()
//												.addGap(18)
//												.addComponent(panel, GroupLayout.PREFERRED_SIZE, 534, GroupLayout.PREFERRED_SIZE)))
//								.addGap(12)
//								.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
//										.addComponent(btnNewButton_2)
//										.addComponent(textField_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
//								.addContainerGap())
//		);
//		panel.setLayout(new BorderLayout(0, 0));
//
//		JScrollPane scrollPane = new JScrollPane();
//		panel.add(scrollPane, BorderLayout.CENTER);
//
//		textArea = new JTextArea();
//		scrollPane.setViewportView(textArea);
//		contentPane.setLayout(gl_contentPane);
//	}

		textField_1 = new JTextField();
		textField_1.setColumns(10);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
				gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
								.addGap(15)
								.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
										.addGroup(gl_contentPane.createSequentialGroup()
												.addComponent(lblNewLabel)
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(textField, GroupLayout.PREFERRED_SIZE, 208, GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(button, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
												.addGap(44))
										.addGroup(gl_contentPane.createSequentialGroup()
												.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
														.addComponent(btnNewButton_1, GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
														.addComponent(btnNewButton_2, GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
														.addComponent(button_3, GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
														.addComponent(button_1, GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
														.addComponent(button_2, GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))
												.addGap(33)
												.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
														.addComponent(textField_1)
														.addComponent(panel, GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE))
												.addGap(19))))
		);
		gl_contentPane.setVerticalGroup(
				gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
								.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblNewLabel)
										.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(button))
								.addGap(33)
								.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
										.addGroup(gl_contentPane.createSequentialGroup()
												.addGap(32)
												.addComponent(btnNewButton_1)
												.addPreferredGap(ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
												.addComponent(button_1)
												.addGap(56)
												.addComponent(button_2)
												.addGap(283)
												.addComponent(button_3))
										.addComponent(panel, GroupLayout.PREFERRED_SIZE, 517, GroupLayout.PREFERRED_SIZE))
								.addGap(18)
								.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
										.addComponent(textField_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(btnNewButton_2))
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane, BorderLayout.CENTER);

		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		contentPane.setLayout(gl_contentPane);
	}
}