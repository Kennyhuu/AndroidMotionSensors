package framework;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

public class GUI {

	
	public void setup()
	{
	JFrame frame = new JFrame("Connections");
	
	JPanel panel = new JPanel(new BorderLayout());
	JPanel panel2 = new JPanel(new BorderLayout());
	JPanel panel3 = new JPanel();
	panel3.setPreferredSize(new Dimension(180,100));
	
	JList<String> list = new JList<>();
	list.setPreferredSize(new Dimension(180,500));
	list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	DefaultListModel<String> listModel = new DefaultListModel<>();
	list.setModel(listModel);
			
	
	JLabel conn_label = new JLabel("New connection:");
	conn_label.setHorizontalAlignment(JLabel.CENTER);
	conn_label.setPreferredSize(new Dimension(180,20));
	
	JTextField conn_text = new JTextField(15);
	
	JButton conn_button = new JButton("Add");
	conn_button.addActionListener(new ActionListener(){
		@Override
		public void actionPerformed(ActionEvent e){
			String s = conn_text.getText();
			if(s.length()>0 && s.length()<=20){
				conn_text.setText("");
				int i = listModel.getSize();
				listModel.add(i,s);
				list.setSelectedIndex(i);
				list.ensureIndexIsVisible(i);
			}
		}
	});
	
	JLabel temp = new JLabel("temp");
	temp.setPreferredSize(new Dimension(600,600));
	
	panel3.add(conn_label);
	panel3.add(conn_text);
	panel3.add(conn_button);
	panel2.add(list,BorderLayout.CENTER);
	panel2.add(panel3,BorderLayout.SOUTH);
	panel.add(panel2,BorderLayout.WEST);
	panel.add(temp,BorderLayout.CENTER);
	
	frame.add(panel);
	frame.pack();
	frame.setVisible(true);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
}
