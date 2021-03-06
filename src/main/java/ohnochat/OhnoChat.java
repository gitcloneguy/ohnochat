package ohnochat;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;

import ohnochat.utils.EchoServer;


public class OhnoChat extends javax.swing.JFrame {
	private static final long serialVersionUID = 1L;
	ServerSocket ss;
    Socket s;
    JTextField text=new JTextField();
    JButton send=new JButton();
    JTextArea messages=new JTextArea();
    PrintWriter pout;
    BufferedReader br;
    ActionListener al;
    String ipstring;
    boolean ready2send=false;
    OhnoChat pt;
    boolean HorC;
    String cliOrServ;
    public OhnoChat(boolean hostOrConnect,String ip)
    {
        ipstring=ip;
        setLayout(null);
        setSize(500,500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        add(text);
        text.setLocation(5,5);
        text.setSize(getWidth()-40-60,30);
        text.setEnabled(false);
        add(send);
        send.setLocation(10+text.getWidth(),text.getY());
        send.setText("Send");
        send.setSize(60,30);
     
	    add(messages);
	    messages.setEditable(false);
	    messages.setBorder(new EtchedBorder());
	    messages.setLocation(5,text.getHeight()+text.getY()+5);
	    messages.setSize(getWidth()-30,getHeight()-text.getY()-text.getHeight()-50);
	        
		al=new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
				 ready2send=true;
			 }
		 	};
		        
		 send.addActionListener(al);
	     pt=this;
	     HorC=hostOrConnect;
	     if(HorC)
	    	 cliOrServ="Server";
		 else
		     cliOrServ="Client";
		 Messenger.start();      
	 }
	      
    @org.junit.jupiter.api.Test
	    public static void main(String args[]) { 
	    	int inp=JOptionPane.showConfirmDialog(null,"Do you want to host the chat?\nYes - Act as server\nNo - Act as client","Want to host a chat?",JOptionPane.YES_NO_OPTION);
	        if(inp==0) {
	            new OhnoChat(true,null).setVisible(true);
	            }	
	        else {
	            String ipstring=JOptionPane.showInputDialog("Please enter the ip address");
	            
	            try{
	                InetAddress.getByName(ipstring);
	                new OhnoChat(false,ipstring).setVisible(true);
	            }catch(Exception e){JOptionPane.showMessageDialog(null,"Invalid or Unreachable IP");}
	            }
	        
	        
	    }
	    
	    String cliport=JOptionPane.showInputDialog("Please enter port no.");
        int b =Integer.parseInt(cliport);


	Thread Messenger=new Thread(){
		public void run() {
			try{
				if(HorC) {
					try {
					String port=JOptionPane.showInputDialog("Please enter the port number");
					int i = Integer.parseInt(port);
					messages.setText("Waiting for an incoming connection.\nEnter my ip at client side.\nMy ip: "+InetAddress.getLocalHost().getHostAddress()+"\nPort that must be opened if clients arent on your local network is "+i); 
					new EchoServer(i);
					s = new Socket("127.0.0.1",i);
					s.setKeepAlive(true);
					}
					catch(Exception e) {
						JOptionPane.showMessageDialog(pt, e.getMessage(), "a",JOptionPane.ERROR_MESSAGE);
						System.exit(0);
					}
				}
				else {
					messages.setText("Connecting to:"+ipstring+":"+b); 
					s=new Socket(InetAddress.getByName(ipstring),b);
				}
				text.setEnabled(true);
				pout = new PrintWriter(s.getOutputStream(),true);
				br=new BufferedReader(new InputStreamReader(s.getInputStream()));
				messages.setText(messages.getText()+"\nConnected to:"+s.getInetAddress().getHostAddress()+":"+s.getPort());
				while(true)
				{
					if(ready2send==true)
					{
						pout.println(cliOrServ+": "+text.getText().trim());
						messages.setText(messages.getText()+"\nMe: "+text.getText());
						text.setText("");
						ready2send=false;
					}
					if(br.ready())
					{
						messages.setText(messages.getText()+"\n"+br.readLine());
					}
					TimeUnit.MILLISECONDS.sleep(8);
				}
			}
			catch(Exception ex) {
				JOptionPane.showMessageDialog(pt, ex.getMessage(), "a",JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
		};

	};


}
