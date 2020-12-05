import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
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
	private JTextField txtPortNumMy;

	private JTextField txtNickName2;
	private JTextField txtIpAddr;
	private JTextField txtPortNum;

	private String nickname;
	private JLabel lblmyNickname;

	private Thread sendthread;
	private Socket socket;
	
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
		setTitle("CHAT");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 613);	

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBorder(new EmptyBorder(-25, 0, 0, 0));
		setContentPane(tabbedPane);

		initConnectPane(); 		//연결 요청 패널 컴포넌트들 초기화
		initChattingPane();		//채팅 패널 컴포넌트들 초기화	
	}

	void initConnectPane()
	{	
		JPanel connectPane = new JPanel();
		connectPane.setBackground(Color.WHITE);
		connectPane.setBorder(new EmptyBorder(80, 30, 50, 30));
		connectPane.setLayout(new BorderLayout(0, 0));		

		//프로그램 제목 라벨
		JLabel lblChatting = new JLabel("CHATTING");
		lblChatting.setHorizontalAlignment(SwingConstants.CENTER);
		lblChatting.setFont(new Font("넥슨 풋볼고딕 B", Font.PLAIN, 34));
		connectPane.add(lblChatting, BorderLayout.NORTH);
		
		//입력값 패널
		JLabel lblNickname = new JLabel("Nickname"); 
		JLabel lblPortNumMy = new JLabel("My Port");
		txtNickname = new JTextField();		
		txtPortNumMy = new JTextField();

		JPanel ServerPanel = new JPanel();			
		ServerPanel.setBackground(Color.WHITE);
		ServerPanel.setBorder(new EmptyBorder(100, 50, 100, 20));
		ServerPanel.setLayout(new GridLayout(0, 2,0,30));

		ServerPanel.add(lblNickname);
		ServerPanel.add(txtNickname);
		ServerPanel.add(lblPortNumMy);
		ServerPanel.add(txtPortNumMy);


		JLabel lblNickname2 = new JLabel("Nickname");
		JLabel lblIpAddr = new JLabel("Friend's IP");
		JLabel lblPortNum = new JLabel("Friend's Port");
		txtNickName2 = new JTextField();
		txtIpAddr = new JTextField();		
		txtPortNum = new JTextField();

		JPanel ClientPanel = new JPanel();	
		ClientPanel.setBorder(new EmptyBorder(80, 50, 80, 20));
		ClientPanel.setBackground(Color.WHITE);
		ClientPanel.setLayout(new GridLayout(0, 2, 0, 30));

		ClientPanel.add(lblNickname2);
		ClientPanel.add(txtNickName2);
		ClientPanel.add(lblIpAddr);
		ClientPanel.add(txtIpAddr);
		ClientPanel.add(lblPortNum);
		ClientPanel.add(txtPortNum);
		
	
		tabbedPane2 = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane2.setBorder(new EmptyBorder(10, 0, 0, 0));
		tabbedPane2.setFont(new Font("넥슨 풋볼고딕 L", Font.PLAIN, 15));
		tabbedPane2.addTab("대기하기", null, ServerPanel, null);
		tabbedPane2.addTab("접속하기", null, ClientPanel, null);
		
		connectPane.add(tabbedPane2, BorderLayout.CENTER);	
		
		JPanel btnPanel = new JPanel();
		btnPanel.setBackground(Color.WHITE);
		connectPane.add(btnPanel, BorderLayout.SOUTH);
		btnPanel.setLayout(new GridLayout(0, 2, 15, 20));
		
		JButton btnQuit = new JButton("Quit");
		btnQuit.setForeground(new Color(255, 99, 71));
		btnQuit.setFont(new Font("넥슨 풋볼고딕 L", Font.PLAIN, 15));
		btnQuit.setBackground(Color.WHITE);
		btnPanel.add(btnQuit);
		
		JButton btnConnect = new JButton("Connect");
		btnConnect.setBackground(Color.WHITE);
		btnConnect.setFont(new Font("넥슨 풋볼고딕 L", Font.PLAIN, 15));
		btnPanel.add(btnConnect);
		
		btnQuit.addActionListener(new QuitActionListener());		//프로그램 종료 이벤트리스너 추가
		btnConnect.addActionListener(new ConnectActionListener());	//프로그램 연결 이벤트리스너 추가
		
		tabbedPane.addTab("connectPane",connectPane);
	
		//임시
		txtNickname.setText("서버");		
		txtPortNumMy.setText("9000");
		txtNickName2.setText("클라");
		txtIpAddr.setText("127.0.0.1");
		txtPortNum.setText("9000");
	}

	void initChattingPane() 
	{
		JPanel chattingPane = new JPanel();
		chattingPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		chattingPane.setLayout(new BorderLayout(0, 0));

		// 알림/나가기 패널
		JPanel noticePanel = new JPanel();
		noticePanel.setBackground(Color.WHITE);
		noticePanel.setBorder(new EmptyBorder(3, 0, 2, 0));
		noticePanel.setLayout(new BorderLayout(0, 0));
		chattingPane.add(noticePanel, BorderLayout.NORTH);
		
		lblmyNickname = new JLabel();
		lblmyNickname.setForeground(SystemColor.activeCaption);
		lblmyNickname.setFont(new Font("나눔바른고딕", Font.PLAIN, 15));
		noticePanel.add(lblmyNickname, BorderLayout.CENTER);

		//채팅방 나가기 버튼
		btnExit = new JButton("나가기");	
		btnExit.setBackground(Color.RED);
		btnExit.setForeground(new Color(255, 255, 255));
		btnExit.setFont(new Font("나눔바른고딕", Font.BOLD, 15));
		noticePanel.add(btnExit, BorderLayout.EAST);

		//채팅 화면
		txtArea = new JTextArea();
		txtArea.setBackground(Color.WHITE);
		txtArea.setEditable(false);
		txtArea.setFont(new Font("나눔바른고딕 Light", Font.PLAIN, 15));

		JScrollPane scrollPane = new JScrollPane(txtArea);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		chattingPane.add(scrollPane, BorderLayout.CENTER);	
		
		//전송 버튼
		btnSend = new JButton("전송");
		btnSend.setFont(new Font("나눔바른고딕", Font.BOLD, 15));
		btnSend.setEnabled(false);

		//전송창 패널
		JPanel sendPanel = new JPanel();
		chattingPane.add(sendPanel, BorderLayout.SOUTH);
		sendPanel.setLayout(new BorderLayout(0, 0));

		//전송 메시지 입력 텍스트 필드
		txtSend = new JTextField();
		sendPanel.add(txtSend, BorderLayout.CENTER);	
		sendPanel.add(btnSend, BorderLayout.EAST);

		txtSend.addActionListener(new SendActionListener());	//전송 이벤트리스너 추가
		txtSend.addKeyListener(new SendkeyListener());			//전송 버튼 활성화/비활성화 키리스너 추가
		btnSend.addActionListener(new SendActionListener());	//전송 이벤트리스너 추가 
		btnExit.addActionListener(new ExitActionListener());    //채팅 나가기 이벤트리스너 추가
	
		tabbedPane.addTab("chattingPane",chattingPane);		

	}
	 
	//연결 버튼
	class ConnectActionListener implements ActionListener
	{		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(tabbedPane2.getSelectedIndex() == 0) // 대기하기
			{
				nickname = txtNickname.getText();
				
				Thread listenthread = new ListenThread();
				listenthread.start();
			}
			else //접속하기
			{
				try 
				{	
					nickname = txtNickName2.getText();		
					socket = new Socket(txtIpAddr.getText(), Integer.parseInt(txtPortNum.getText()));
					sendthread = new SenderThread(socket);
					Thread receivethread = new ReceiverThread(socket);

					receivethread.start();
					tabbedPane.setSelectedIndex(1);	//UI 전환
				}
				catch(Exception ex)
				{
					showMessageDialog(null,"상대방과 채팅 연결에 실패했습니다.","Notice",JOptionPane.WARNING_MESSAGE);		
				}
			}
			
			lblmyNickname.setText(" 나의 닉네임 : " + nickname);
		}
	}	

	//클라이언트의 연결을 기다리는 스레드
	class ListenThread extends Thread
	{
		ServerSocket serverSocket = null;
		
		public void run()
		{
			try 
			{				
				int PortNumMy = Integer.parseInt(txtPortNumMy.getText());		
				serverSocket = new ServerSocket(PortNumMy);						
				socket = serverSocket.accept();

				showMessageDialog(null,"연결요청이 들어왔습니다.","Notice",JOptionPane.INFORMATION_MESSAGE);					

				sendthread = new SenderThread(socket);				
				Thread receivethread = new ReceiverThread(socket);
				receivethread.start();	

				tabbedPane.setSelectedIndex(1);	//UI 연결창으로 전환

			}
			catch(Exception ex)
			{
				System.out.println(ex.getMessage());
			}
			finally 
			{
				try
				{
					serverSocket.close();
				}
				catch(Exception ignored)
				{

				}
			}
		}
	}

	//전송 스레드
	class SenderThread extends Thread {

		private Socket socket;
		private String str;
		private boolean isDisconneted;
		
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
	
				txtArea.append("\n"+str); //화면에 전송한 메시지 띄우기
				
				txtSend.setText("");	//입력 텍스트 필드 초기화
				btnSend.setEnabled(false); //전송 버튼 비활성화
			}
			catch(Exception e)
			{
				showMessageDialog(null, "상대방과 연결이 끊어졌습니다.", "Notice", JOptionPane.WARNING_MESSAGE);
			
				tabbedPane.setSelectedIndex(0);			
				txtArea.setText("");
				txtSend.setText("");
			}
		}
	}

	//수신 스레드
	class ReceiverThread extends Thread {

		private Socket socket;

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
					 
					
					if(str.equals("disconnect")) //상대방이 나가기한 경우
					{
						showMessageDialog(null, "상대방이 대화방을 나갔습니다.", "Notice", JOptionPane.WARNING_MESSAGE);						
						socket.close();						
						txtArea.setText("");
						txtSend.setText("");						
						tabbedPane.setSelectedIndex(0);	
						break;
					}

					txtArea.append("\n"+str); //화면에 수신한 메시지 띄우기
				}
			}
			catch(Exception e) 
			{
				showMessageDialog(null, "상대방과 연결이 끊어졌습니다.", "Notice",JOptionPane.WARNING_MESSAGE);			
				
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
	
	//전송 버튼, 입력 텍스트 필드-엔터   
	class SendActionListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{				
			//전송창에 입력된 내용이 없으면 검색 버튼을 누르거나 검색창에서 엔터를 쳐도 무시
			if(txtSend.getText().trim().length()==0) return;					
			sendthread.run();					
		}					
	}   	

	//전송 버튼 활성화 / 비활성화 
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

	//채팅창 나가기 버튼
	class ExitActionListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{					
			
			int result = JOptionPane.showConfirmDialog(null,"정말 채팅을 종료하시겠습니까?.\n","Exit",JOptionPane.OK_CANCEL_OPTION );

			if(result == JOptionPane.YES_OPTION) //채팅 종료
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
			else //취소하거나 창을 닫으면 무시
				return;					
		}
	}

	//프로그램 종료 버튼
	class QuitActionListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{		
			int result = JOptionPane.showConfirmDialog(null,"정말 프로그램을 종료하시겠습니까?.\n","Quit",JOptionPane.OK_CANCEL_OPTION );

			if(result == JOptionPane.YES_OPTION) //프로그램 종료
				System.exit(0);		 				
			else //취소하거나 창을 닫으면 무시
				return;		

			
		}
	}

}
