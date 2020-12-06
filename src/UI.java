import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class UI extends JFrame {

	private JTabbedPane tabbedPane;
	private JTabbedPane tabbedPane2;
		
	private JTextField txtSend;
	private JTextArea txtArea;
	
	private JButton btnSend;
	private JButton btnExit;
	
	private JTextField txtNickname;		
	private JTextField txtServerPort;

	private JTextField txtNickName2;
	private JTextField txtServerIp;
	private JTextField txtServerPort2;

	private String nickname;
	private JLabel lblmyNickname;
	private JLabel lblNotice;
	
	private Thread sendthread;
	private Socket socket;
	private ServerSocket serverSocket;
	
	
	/**	Launch the application. */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UI frame = new UI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/** Create the frame. */
	public UI() {
		
		setTitle("CHATTING");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 613);	

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBorder(new EmptyBorder(-25, 0, 0, 0));
		setContentPane(tabbedPane);

		initConnectPane(); 		//���� ��û �г� ������Ʈ�� �ʱ�ȭ
		initChattingPane();		//ä�� �г� ������Ʈ�� �ʱ�ȭ	
	}

	void initConnectPane()
	{	
		JPanel connectPane = new JPanel();
		connectPane.setBackground(Color.WHITE);
		connectPane.setBorder(new EmptyBorder(80, 30, 50, 30));
		connectPane.setLayout(new BorderLayout(0, 0));		

		//���α׷� ���� ��
		JLabel lblChatting = new JLabel("CHATTING");
		lblChatting.setHorizontalAlignment(SwingConstants.CENTER);
		lblChatting.setFont(new Font("�ؽ� ǲ����� B", Font.PLAIN, 34));
		connectPane.add(lblChatting, BorderLayout.NORTH);
		
		//�Է°� �г�
		JLabel lblNickname = new JLabel("Nickname"); 
		JLabel lblPortNumMy = new JLabel("Server Port");
		txtNickname = new JTextField();		
		txtServerPort = new JTextField();

		JPanel ServerPanel = new JPanel();			
		ServerPanel.setBackground(Color.WHITE);
		ServerPanel.setBorder(new EmptyBorder(100, 50, 100, 20));
		ServerPanel.setLayout(new GridLayout(0, 2,0,30));

		ServerPanel.add(lblNickname);
		ServerPanel.add(txtNickname);
		ServerPanel.add(lblPortNumMy);
		ServerPanel.add(txtServerPort);


		JLabel lblNickname2 = new JLabel("Nickname");
		JLabel lblIpAddr = new JLabel("Server IP");
		JLabel lblPortNum = new JLabel("Server Port");
		txtNickName2 = new JTextField();
		txtServerIp = new JTextField();		
		txtServerPort2 = new JTextField();

		JPanel ClientPanel = new JPanel();	
		ClientPanel.setBorder(new EmptyBorder(80, 50, 80, 20));
		ClientPanel.setBackground(Color.WHITE);
		ClientPanel.setLayout(new GridLayout(0, 2, 0, 30));

		ClientPanel.add(lblNickname2);
		ClientPanel.add(txtNickName2);
		ClientPanel.add(lblIpAddr);
		ClientPanel.add(txtServerIp);
		ClientPanel.add(lblPortNum);
		ClientPanel.add(txtServerPort2);
		
	
		tabbedPane2 = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane2.setBorder(new EmptyBorder(10, 0, 0, 0));
		tabbedPane2.setFont(new Font("�ؽ� ǲ����� L", Font.PLAIN, 15));
		tabbedPane2.addTab("Server", null, ServerPanel, null);
		tabbedPane2.addTab("Client", null, ClientPanel, null);
		
		connectPane.add(tabbedPane2, BorderLayout.CENTER);	
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		connectPane.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 10));
		
		JPanel btnPanel = new JPanel();
		panel.add(btnPanel, BorderLayout.CENTER);
		btnPanel.setBackground(Color.WHITE);
		btnPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		JButton btnQuit = new JButton("Quit");
		btnQuit.setForeground(new Color(255, 99, 71));
		btnQuit.setFont(new Font("�ؽ� ǲ����� L", Font.PLAIN, 15));
		btnQuit.setBackground(Color.WHITE);
		btnPanel.add(btnQuit);
		
		JButton btnConnect = new JButton("Connect");
		btnConnect.setBackground(Color.WHITE);
		btnConnect.setFont(new Font("�ؽ� ǲ����� L", Font.PLAIN, 15));
		btnPanel.add(btnConnect);
		
		btnQuit.addActionListener(new QuitActionListener());		//���α׷� ���� �̺�Ʈ������ �߰�
		btnConnect.addActionListener(new ConnectActionListener());	//���α׷� ���� �̺�Ʈ������ �߰�
		
		lblNotice = new JLabel();
		lblNotice.setText(" ");
		lblNotice.setHorizontalAlignment(SwingConstants.CENTER);
		lblNotice.setVerticalAlignment(SwingConstants.BOTTOM);
		panel.add(lblNotice, BorderLayout.NORTH);
		lblNotice.setForeground(Color.RED);
		
		tabbedPane.addTab("connectPane",connectPane);		
	}

	void initChattingPane() 
	{
		JPanel chattingPane = new JPanel();
		chattingPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		chattingPane.setLayout(new BorderLayout(0, 0));

		// ����/������ �г� 
		JPanel noticePanel = new JPanel();
		noticePanel.setBackground(Color.WHITE);
		noticePanel.setBorder(new EmptyBorder(3, 0, 2, 0));
		noticePanel.setLayout(new BorderLayout(0, 0));
		chattingPane.add(noticePanel, BorderLayout.NORTH);
		
		// �� �г��� ���� ��
		lblmyNickname = new JLabel();
		lblmyNickname.setForeground(SystemColor.activeCaption);
		lblmyNickname.setFont(new Font("�����ٸ����", Font.PLAIN, 15));
		noticePanel.add(lblmyNickname, BorderLayout.CENTER);

		//ä�ù� ������ ��ư
		btnExit = new JButton("������");	
		btnExit.setBackground(Color.RED);
		btnExit.setForeground(new Color(255, 255, 255));
		btnExit.setFont(new Font("�����ٸ����", Font.BOLD, 15));
		noticePanel.add(btnExit, BorderLayout.EAST);

		//ä�� ȭ��
		txtArea = new JTextArea();
		txtArea.setBackground(Color.WHITE);
		txtArea.setEditable(false);
		txtArea.setFont(new Font("�����ٸ���� Light", Font.PLAIN, 15));
		
		JScrollPane scrollPane = new JScrollPane(txtArea);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		chattingPane.add(scrollPane, BorderLayout.CENTER);	
		
		//���� ��ư
		btnSend = new JButton("����");
		btnSend.setFont(new Font("�����ٸ����", Font.BOLD, 15));
		btnSend.setEnabled(false);

		//����â �г�
		JPanel sendPanel = new JPanel();
		chattingPane.add(sendPanel, BorderLayout.SOUTH);
		sendPanel.setLayout(new BorderLayout(0, 0));

		//���� �޽��� �Է� �ؽ�Ʈ �ʵ�
		txtSend = new JTextField();
		sendPanel.add(txtSend, BorderLayout.CENTER);	
		sendPanel.add(btnSend, BorderLayout.EAST);

		
		txtSend.addActionListener(new SendActionListener());	//���� �̺�Ʈ������ �߰�
		txtSend.addKeyListener(new SendkeyListener());			//���� ��ư Ȱ��ȭ/��Ȱ��ȭ Ű������ �߰�
		btnSend.addActionListener(new SendActionListener());	//���� �̺�Ʈ������ �߰� 
		btnExit.addActionListener(new ExitActionListener());    //ä�� ������ �̺�Ʈ������ �߰�
	
		tabbedPane.addTab("chattingPane",chattingPane);		

	}
	 	
	/**
	 * ������ Ŭ����
	 * */	

	//Ŭ���̾�Ʈ�� ������ ��ٸ��� ������
	class ListenThread extends Thread
	{		
		public void run()
		{
			try 
			{	
				int PortNumMy = Integer.parseInt(txtServerPort.getText());		
				serverSocket = new ServerSocket(PortNumMy);						
				
				lblNotice.setText("���� ��û ��� ��");
				
				socket = serverSocket.accept();				
				showMessageDialog(tabbedPane,"�����û�� ���Խ��ϴ�.","Notice",JOptionPane.INFORMATION_MESSAGE);					
				
				sendthread = new SenderThread(socket);				
				Thread receivethread = new ReceiverThread(socket);
				receivethread.start();

				tabbedPane.setSelectedIndex(1);	//UI ����â���� ��ȯ

			}
			catch(Exception ignored)
			{
				
			}
			finally 
			{
				try
				{
					serverSocket.close();
				}
				catch(Exception ignored2)
				{

				}
				
				lblNotice.setText(" ");
				
			}
		}
	}
	
	//�������� ��û�ϴ� ������
	class RequestThread extends Thread
	{
		
		public void run()
		{
			try 
			{	
				socket = new Socket(txtServerIp.getText(), Integer.parseInt(txtServerPort2.getText()));			
				sendthread = new SenderThread(socket);
				Thread receivethread = new ReceiverThread(socket);
				receivethread.start();
				
				tabbedPane.setSelectedIndex(1);	//UI ��ȯ								
								
				if( serverSocket != null)	//���Ἲ�� ��, ���� ���� ������ ���� Ŭ���̾�Ʈ�� ��û�� ��ٸ��� ���̾��ٸ� �������� �ݾ��ֱ� 
				{
					try 
					{	
						serverSocket.close(); 
						serverSocket = null;
					}
					catch (IOException ignored) { }
				}
			}
			catch(Exception ex)
			{
				System.out.println(ex.getMessage());
				
				showMessageDialog(tabbedPane,"����� ä�� ���ῡ �����߽��ϴ�.","Notice",JOptionPane.WARNING_MESSAGE);		
			}
		}
	}

	//���� ������
	class SenderThread extends Thread {

		Socket socket;
		String str;
		boolean isDisconneted;
		
		SenderThread(Socket socket)
		{
			this.socket = socket;
			this.isDisconneted = false;
		}

		SenderThread(Socket socket, String str)
		{
			this.socket = socket;
			this.isDisconneted = true;
			this.str = str;
		}
		
		public void run()
		{
			try
			{			
				PrintWriter writer = new PrintWriter(socket.getOutputStream());									

				if(!this.isDisconneted)
					this.str = nickname + " > "+txtSend.getText();
				
				writer.println(str);
				writer.flush();	
	
				txtArea.append("\n"+str); //ȭ�鿡 ������ �޽��� ����		
				txtSend.setText("");	//�Է� �ؽ�Ʈ �ʵ� �ʱ�ȭ
				btnSend.setEnabled(false); //���� ��ư ��Ȱ��ȭ
				
			}
			catch(Exception e)
			{
				showMessageDialog(tabbedPane, "����� ������ ���������ϴ�.", "Notice", JOptionPane.WARNING_MESSAGE);
			
				tabbedPane.setSelectedIndex(0);	//����ȭ������ ���ư���		
				txtArea.setText("");
				txtSend.setText("");
				btnSend.setEnabled(false); 
			}
		}
	}

	//���� ������
	class ReceiverThread extends Thread {

		Socket socket;

		ReceiverThread(Socket socket)
		{
			this.socket = socket;
		}

		public void run()
		{
			try
			{
				BufferedReader reader = new BufferedReader( new InputStreamReader(socket.getInputStream()));

				while(true)
				{
					String str = reader.readLine();

					if(str == null)
						break;
					 
					
					if(str.equals("disconnect")) //������ �������� ���
					{
						showMessageDialog(tabbedPane, "������ ��ȭ���� �������ϴ�.", "Notice", JOptionPane.WARNING_MESSAGE);						
						socket.close();						
						txtArea.setText("");
						txtSend.setText("");						
						tabbedPane.setSelectedIndex(0);	
						break;
					}

					txtArea.append("\n"+str); //ȭ�鿡 ������ �޽��� ����
				}
			}
			catch(Exception e) 
			{
				showMessageDialog(tabbedPane, "����� ������ ���������ϴ�.", "Notice",JOptionPane.WARNING_MESSAGE);			
				
				try
				{
					socket.close();
				}
				catch(Exception ignored)
				{
					
				}
				
				txtArea.setText("");
				txtSend.setText("");						
				tabbedPane.setSelectedIndex(0);				
			}
		}
	}
	
	/**
	 * �̺�Ʈ ������
	 * */	
	
	//���� ��ư
	class ConnectActionListener implements ActionListener
		{		
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(tabbedPane2.getSelectedIndex() == 0) // ���� (Ŭ���̾�Ʈ�� ���� ����ϱ�)
				{
					nickname = txtNickname.getText();
					
					Thread listenthread = new ListenThread(); //Ŭ���̾�Ʈ�� ���� ��û�� ��ٸ��� ������
					listenthread.start();
				}
				else //Ŭ���̾�Ʈ (������ �����û�ϱ�)
				{
					nickname = txtNickName2.getText();		
					
					Thread requestThread = new RequestThread(); //�������� ���� ��û�� �ϴ� ������
					requestThread.start();
				}
				
				lblmyNickname.setText(" ���� �г��� : " + nickname);
			}
		}	

	//���� ��ư, �Է� �ؽ�Ʈ �ʵ�-����   
	class SendActionListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{				
			//����â�� �Էµ� ������ ������ �˻� ��ư�� �����ų� �˻�â���� ���͸� �ĵ� ����
			if(txtSend.getText().trim().length()==0) return;					
			sendthread.run();					
		}					
	}   	

	//���� ��ư Ȱ��ȭ / ��Ȱ��ȭ 
	class SendkeyListener implements KeyListener
	{ 		
		@Override
		public void keyReleased(KeyEvent e)
		{
			boolean btnSendEnabled = (txtSend.getText().trim().length() > 0)?true:false;
			btnSend.setEnabled(btnSendEnabled);	
		}

		@Override
		public void keyPressed(KeyEvent e){}

		@Override
		public void keyTyped(KeyEvent e){}     
	} 

	//ä��â ������ ��ư
	class ExitActionListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{					
			
			int result = JOptionPane.showConfirmDialog(tabbedPane,"���� ä���� �����Ͻðڽ��ϱ�?.\n","Exit",JOptionPane.OK_CANCEL_OPTION );

			if(result == JOptionPane.YES_OPTION) //ä�� ����
			{
				Thread sendthread = new SenderThread(socket, "disconnect");
				sendthread.run();
				
				tabbedPane.setSelectedIndex(0);			
				txtArea.setText("");
				txtSend.setText("");
				
				try 
				{
					socket.close();
				} 
				catch (Exception ignored) 
				{
					
				}			
			}			 				
			else //����ϰų� â�� ������ ����
				return;					
		}
	}

	//���α׷� ���� ��ư
	class QuitActionListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{		
			int result = JOptionPane.showConfirmDialog(tabbedPane,"���� ���α׷��� �����Ͻðڽ��ϱ�?.\n","Quit",JOptionPane.OK_CANCEL_OPTION );

			if(result == JOptionPane.YES_OPTION) //���α׷� ����
			{
				System.exit(0);
								
				try 
				{
					serverSocket.close();
					socket.close();
				}
				catch(Exception ex)
				{
					
				}
			}
			else //����ϰų� â�� ������ ����
				return;		

			
		}
	}

}
