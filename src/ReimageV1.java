import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JLabel;

public class ReimageV1 extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ReimageV1 frame = new ReimageV1();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ReimageV1() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JButton btnLink = new JButton("link");
		btnLink.addMouseListener(new MouseAdapter() {
			Socket socket;
			InputStream ins;
			String srcFile;
			//(3.5-10)
			@Override
			public void mouseClicked(MouseEvent arg0) {
					 System.out.println("dd");
				
			}
		});
		contentPane.add(btnLink, BorderLayout.NORTH);
		
		JButton btnShow = new JButton("show");
		contentPane.add(btnShow, BorderLayout.SOUTH);
		
		JLabel lblNewLabel = new JLabel(" ");
		contentPane.add(lblNewLabel, BorderLayout.CENTER);
	}

}
