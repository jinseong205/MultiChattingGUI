package java_client;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

class LoginGUI extends JFrame implements ActionListener{
	
	static JTextField id_field;
	static JPasswordField pw_field;
	JLabel idlabel,pwlabel;
	JButton bt;
	
	WriteThread wt;	
	MainGUI mg;
	
	public LoginGUI(){}
	
	public LoginGUI(WriteThread wt, MainGUI mg) {
	
		super("Login");		
		this.wt = wt;
		this.mg = mg;
		
		Container c = getContentPane();
		setSize(200,220);
		
		setLayout(null);
		
		
		idlabel = new JLabel("ID");
		idlabel.setBounds(25, 0, 20, 30);
		c.add(idlabel);
		
		pwlabel = new JLabel("PW");
		pwlabel.setBounds(25, 60, 20, 30);
		c.add(pwlabel);
		
		id_field = new JTextField(15);
		id_field.setBounds(25, 30, 150, 30);
		c.add(id_field);
 
		pw_field = new JPasswordField(15);
		pw_field.setBounds(25, 90, 150, 30);
		c.add(pw_field);
		
		
		bt = new JButton("login");
		bt.setBounds(50, 130, 100, 30);
		bt.addActionListener(this);
		c.add(bt);
		
		bt.addActionListener(this);
		
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {		
		wt.sendMsg();	
		mg.isFirst = false;
		mg.setVisible(true);
		this.dispose();
	}
	static public String getID(){
		return id_field.getText();
	}
	static public String getPW() {
	       char[] pass = pw_field.getPassword();
	        String pw = new String(pass);
	        return pw;
	}
}


public class MainGUI extends JFrame implements ActionListener{
	JTextArea txt_area = new JTextArea();
	JScrollPane scroll = new JScrollPane(txt_area);
	JTextField txt_field = new JTextField(15);
	JButton btnSend = new JButton("submit");
	JButton btnExit = new JButton("close");
	JPanel p1 = new JPanel();
	Socket socket;
	WriteThread wt;
	boolean isFirst=true;
	boolean isLast=false;
	
	
	public MainGUI(Socket socket) {
		super("chat program");
		this.socket = socket;
		wt = new WriteThread(this);
		new LoginGUI(wt, this);
		
		
		add("Center",scroll);
		p1.add(txt_field);
		p1.add(btnSend);
		p1.add(btnExit);
		
		add("South", p1);
		
		//create actionListener
		btnSend.addActionListener(this);
		btnExit.addActionListener(this);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(300, 300, 350, 300);
		setVisible(false);	
	}
	
	public void actionPerformed(ActionEvent e){
		String id = LoginGUI.getID();
		//send button action
		if(e.getSource()==btnSend){
			if(txt_field.getText().equals("")){
				return;
			}			
			//txt_area.append(txt_field.getText());
			wt.sendMsg();
			txt_field.setText("");
		}else{
			isLast = true;
			wt.sendMsg();
			this.dispose();
			
		}
	}
}
