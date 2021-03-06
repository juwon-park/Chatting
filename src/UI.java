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
		tabbedPane2.setFont(new Font("넥슨 풋볼고딕 L", Font.PLAIN, 15));
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
		btnQuit.setFont(new Font("넥슨 풋볼고딕 L", Font.PLAIN, 15));
		btnQuit.setBackground(Color.WHITE);
		btnPanel.add(btnQuit);
		
		JButton btnConnect = new JButton("Connect");
		btnConnect.setBackground(Color.WHITE);
		btnConnect.setFont(new Font("넥슨 풋볼고딕 L", Font.PLAIN, 15));
		btnPanel.add(btnConnect);
		
		btnQuit.addActionListener(new QuitActionListener());		//프로그램 종료 이벤트리스너 추가
		btnConnect.addActionListener(new ConnectActionListener());	//프로그램 연결 이벤트리스너 추가
		
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

		// 정보/나가기 패널 
		JPanel noticePanel = new JPanel();
		noticePanel.setBackground(Color.WHITE);
		noticePanel.setBorder(new EmptyBorder(3, 0, 2, 0));
		noticePanel.setLayout(new BorderLayout(0, 0));
		chattingPane.add(noticePanel, BorderLayout.NORTH);
		
		// 내 닉네임 정보 라벨
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
	 	
	/**
	 * 스레드 클래스
	 * */	

	//클라이언트의 연결을 기다리는 스레드
	class ListenThread extends Thread
	{		
		public void run()
		{
			try 
			{	
				int PortNumMy = Integer.parseInt(txtServerPort.getText());		
				serverSocket = new ServerSocket(PortNumMy);						
				
				lblNotice.setText("연결 요청 대기 중");
				
				socket = serverSocket.accept();				
				showMessageDialog(tabbedPane,"연결요청이 들어왔습니다.","Notice",JOptionPane.INFORMATION_MESSAGE);					
				
				sendthread = new SenderThread(socket);				
				Thread receivethread = new ReceiverThread(socket);
				receivethread.start();

				tabbedPane.setSelectedIndex(1);	//UI 연결창으로 전환

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
	
	//서버에게 요청하는 스레드
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
				
				tabbedPane.setSelectedIndex(1);	//UI 전환								
								
				if( serverSocket != null)	//연결성공 시, 만약 서버 소켓을 열어 클라이언트의 요청을 기다리는 중이었다면 서버소켓 닫아주기 
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
				
				showMessageDialog(tabbedPane,"상대방과 채팅 연결에 실패했습니다.","Notice",JOptionPane.WARNING_MESSAGE);		
			}
		}
	}

	//전송 스레드
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
	
				txtArea.append("\n"+str); //화면에 전송한 메시지 띄우기		
				txtSend.setText("");	//입력 텍스트 필드 초기화
				btnSend.setEnabled(false); //전송 버튼 비활성화
				
			}
			catch(Exception e)
			{
				showMessageDialog(tabbedPane, "상대방과 연결이 끊어졌습니다.", "Notice", JOptionPane.WARNING_MESSAGE);
			
				tabbedPane.setSelectedIndex(0);	//시작화면으로 돌아가기		
				txtArea.setText("");
				txtSend.setText("");
				btnSend.setEnabled(false); 
			}
		}
	}

	//수신 스레드
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
					 
					
					if(str.equals("disconnect")) //상대방이 나가기한 경우
					{
						showMessageDialog(tabbedPane, "상대방이 대화방을 나갔습니다.", "Notice", JOptionPane.WARNING_MESSAGE);						
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
				showMessageDialog(tabbedPane, "상대방과 연결이 끊어졌습니다.", "Notice",JOptionPane.WARNING_MESSAGE);			
				
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
	 * 이벤트 리스너
	 * */	
	
	//연결 버튼
	class ConnectActionListener implements ActionListener
		{		
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(tabbedPane2.getSelectedIndex() == 0) // 서버 (클라이언트의 연결 대기하기)
				{
					nickname = txtNickname.getText();
					
					Thread listenthread = new ListenThread(); //클라이언트의 연결 요청을 기다리는 스레드
					listenthread.start();
				}
				else //클라이언트 (서버에 연결요청하기)
				{
					nickname = txtNickName2.getText();		
					
					Thread requestThread = new RequestThread(); //서버에게 연결 요청을 하는 스레드
					requestThread.start();
				}
				
				lblmyNickname.setText(" 나의 닉네임 : " + nickname);
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
			
			int result = JOptionPane.showConfirmDialog(tabbedPane,"정말 채팅을 종료하시겠습니까?.\n","Exit",JOptionPane.OK_CANCEL_OPTION );

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
			int result = JOptionPane.showConfirmDialog(tabbedPane,"정말 프로그램을 종료하시겠습니까?.\n","Quit",JOptionPane.OK_CANCEL_OPTION );

			if(result == JOptionPane.YES_OPTION) //프로그램 종료
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
			else //취소하거나 창을 닫으면 무시
				return;		

			
		}
	}

}
