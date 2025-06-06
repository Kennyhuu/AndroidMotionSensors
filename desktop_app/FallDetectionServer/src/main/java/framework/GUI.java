package framework;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.LinkedList;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import server.DataObserver;
import server.EmergencyService;
import server.MovementData;
import server.Server;
import server.UserInterface;

public class GUI implements DataObserver, UserInterface, EmergencyService {

	private LinkedList<MovementData> dataList;
	private JPanel panel;
	//private Server server;
	
	public GUI() {
		dataList = new LinkedList<>();
		//server = new Server(this,this,this); 
		new Server(this,this,this);
	}
	
	public void setup(){
		JFrame frame = new JFrame("FallDetectionServer");
		
		panel = new JPanel(new BorderLayout()){
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D)g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				int GAP=25;
				int H=(700-4*GAP)/3;
				int W=(700-4*GAP)/2;
				for(int i=0;i<2;i++){
					for(int j=0;j<3;j++){
						g2.drawLine(GAP+i*(W+GAP+GAP),GAP+j*(H+GAP),GAP+i*(W+GAP+GAP),GAP+H+j*(H+GAP));
						g2.drawLine(GAP+i*(W+GAP+GAP),GAP+H/2+j*(H+GAP),GAP+W+i*(W+GAP+GAP),GAP+H/2+j*(H+GAP));
						
						float maxScale=0;
						
						synchronized(dataList){
							for(int k=0;k<dataList.size();k++)
								     if(i==0 && j==0 && maxScale<Math.abs(dataList.get(k).accX)) maxScale=Math.abs(dataList.get(k).accX);
								else if(i==0 && j==1 && maxScale<Math.abs(dataList.get(k).accY)) maxScale=Math.abs(dataList.get(k).accY);
								else if(i==0 && j==2 && maxScale<Math.abs(dataList.get(k).accZ)) maxScale=Math.abs(dataList.get(k).accZ);
							
								else if(i==1 && j==0 && maxScale<Math.abs(dataList.get(k).posX)) maxScale=Math.abs(dataList.get(k).posX);
								else if(i==1 && j==1 && maxScale<Math.abs(dataList.get(k).posY)) maxScale=Math.abs(dataList.get(k).posY);
								else if(i==1 && j==2 && maxScale<Math.abs(dataList.get(k).posZ)) maxScale=Math.abs(dataList.get(k).posZ);
							
							if(maxScale==0) maxScale=1;
							
							for(int k=0;k<dataList.size()-1;k++){
								float val=0,val2=0;
								if(i==0 && j==0) {val=dataList.get(k).accX;val2=dataList.get(k+1).accX;}
								if(i==0 && j==1) {val=dataList.get(k).accY;val2=dataList.get(k+1).accY;}
								if(i==0 && j==2) {val=dataList.get(k).accZ;val2=dataList.get(k+1).accZ;}
								if(i==1 && j==0) {val=dataList.get(k).posX;val2=dataList.get(k+1).posX;}
								if(i==1 && j==1) {val=dataList.get(k).posY;val2=dataList.get(k+1).posY;}
								if(i==1 && j==2) {val=dataList.get(k).posZ;val2=dataList.get(k+1).posZ;}

								g2.drawLine(GAP+i*(W+GAP+GAP)+k    *W/500,(int)(GAP+H/2+j*(H+GAP)-H/2*val/maxScale),
										    GAP+i*(W+GAP+GAP)+(k+1)*W/500,(int)(GAP+H/2+j*(H+GAP)-H/2*val2/maxScale));
							}
						}
					}
				}
				
				g2.drawString("Acc - X",GAP+W/2,GAP);
				g2.drawString("Acc - Y",GAP+W/2,GAP+1*(H+GAP));
				g2.drawString("Acc - Z",GAP+W/2,GAP+2*(H+GAP));
				g2.drawString("Gyro - X",GAP+W+GAP+GAP+W/2,GAP);
				g2.drawString("Gyro - Y",GAP+W+GAP+GAP+W/2,GAP+1*(H+GAP));
				g2.drawString("Gyro - Z",GAP+W+GAP+GAP+W/2,GAP+2*(H+GAP));
				
				g2.drawString("[m/s\u00B2]",GAP+5,GAP-5);
				g2.drawString("[m/s\u00B2]",GAP+5,GAP+1*(H+GAP)-5);
				g2.drawString("[m/s\u00B2]",GAP+5,GAP+2*(H+GAP)-5);
				g2.drawString("[\u00B0/s]",GAP+W+GAP+5+GAP,GAP-5);
				g2.drawString("[\u00B0/s]",GAP+W+GAP+5+GAP,GAP+1*(H+GAP)-5);
				g2.drawString("[\u00B0/s]",GAP+W+GAP+5+GAP,GAP+2*(H+GAP)-5);
				
				g2.drawString("[s]", GAP+W+5, GAP+H/2+15);
				g2.drawString("[s]", GAP+W+5, GAP+H/2+15+1*(H+GAP));
				g2.drawString("[s]", GAP+W+5, GAP+H/2+15+2*(H+GAP));
				g2.drawString("[s]", GAP+W+5+W+GAP+GAP, GAP+H/2+15);
				g2.drawString("[s]", GAP+W+5+W+GAP+GAP, GAP+H/2+15+1*(H+GAP));
				g2.drawString("[s]", GAP+W+5+W+GAP+GAP, GAP+H/2+15+2*(H+GAP));
			}
		};
		panel.setPreferredSize(new Dimension(700,700)); //700*700
		
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		/*java.util.Timer t= new java.util.Timer();
		t.scheduleAtFixedRate(new java.util.TimerTask(){
			@Override
			public void run(){
				java.util.Random rand = new java.util.Random(); 
				newData(new MovementData((rand.nextFloat()-0.5f)*2f,(rand.nextFloat()-0.5f)*2f,(rand.nextFloat()-0.5f)*2f,(rand.nextFloat()-0.5f)*2f,(rand.nextFloat()-0.5f)*2f,(rand.nextFloat()-0.5f)*2f));
			}
		}, (long)20,(long)20);*/
	}
	
	
	@Override
	public boolean newData(MovementData data) {
		synchronized(dataList){
			if(dataList.size()==500)
				dataList.remove();
			dataList.add(data);
		}
		panel.repaint();
		return true;
	}

	@Override
	public boolean callHelp() {
		JOptionPane.showMessageDialog(new JFrame(),
			    "We should call the ambulance.",
			    "Emergency situation",
			    JOptionPane.WARNING_MESSAGE);
		return true;
	}

	@Override
	public boolean checkUser() {
		Object[] options = {"Yes, I am fine","No, I need help"};
		JOptionPane pane = new JOptionPane(
			    "We detected a possible accident.\nIs everything all right?",
			    JOptionPane.QUESTION_MESSAGE,
			    JOptionPane.YES_NO_OPTION,
			    null,
			    options,
			    null);
		JDialog dlg = pane.createDialog("Falling detected");
		dlg.setAlwaysOnTop(true);
	    dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	    dlg.addComponentListener(new ComponentAdapter() {
	        @Override
	        public void componentShown(ComponentEvent e) {
	            super.componentShown(e);
	            Timer t = new Timer(1000 * 10, new ActionListener() {
	                @Override
	                public void actionPerformed(ActionEvent e) {
	                    dlg.setVisible(false);
	                }

	            });
	            t.start();
	        }
	    });
	    dlg.setVisible(true);
	    
	    Object selectedvalue = pane.getValue();
	    if (selectedvalue.equals(options[0]))
	        return true;
	    return false;
	}

	@Override
	public void noNewMessage() {
		JOptionPane.showMessageDialog(new JFrame(),
			    "Cannot see measurements. Please, check your device.",
			    "Lost messages",
			    JOptionPane.WARNING_MESSAGE);
	}

	@Override
	public void connectionLost() {
		JOptionPane.showMessageDialog(new JFrame(),
			    "Connection lost. Please, check your device.",
			    "Lost conenction",
			    JOptionPane.WARNING_MESSAGE);
	}
	
}
