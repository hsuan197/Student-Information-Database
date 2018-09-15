import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Dbms extends JFrame implements ActionListener{
	
	private static Connection conn;
	public static Statement stmt;
	
	public JTextArea[] inputlines;
	public JComboBox[] comboBox;
	private JButton okbtn;
	private JLabel title;
	private JTable table;
	private JScrollPane pane;
	
	private BtnListener btnlistener;
	
	public String [] searchBar = {"student", "class", "classroom", "dorm", "department"};
	private String[][] box_con = {	{"student_id", "student_name", "sex", "grade"},
									{"class_id", "class_name", "teacher"},
									{"classroom_id", "maxnum", "department_id"},	
									{"dorm_id", "sex", "maxnum"},	
									{"department_id", "department_name", "director"},
												
									{"major, department_name", "live, dorm_id", "assistant, class_id", "takeclass, class_id"},
									{"assistant, student_id", "takeclass, student_id", "participate, classroom_id", "participate, class_time", "participate, department_id"},
									{"participate, class_id", "participate, department_id", "participate, class_time"},
									{"live, student_id"},
									{"participate, class_id"}
	
								};
	
	
	public static void main(String[] args) throws ClassNotFoundException {
		try{
			Class.forName("com.mysql.jdbc.Driver");			
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbms_project?serverTimezone=UTC&useSSL=false", "root", "qqqqqaqq");
			stmt = conn.createStatement();			
		}catch(Exception e)	{
			System.out.println("connect fail");
		}
		Dbms frame = new Dbms();
		frame.setVisible(true);
	}
	
	public Dbms(){
		
		btnlistener = new BtnListener(this);
		
		/**********window**********/
		setSize(600, 400);
		setLayout(null);
		setTitle("Crystal University");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		/**********Bars**********/
		JMenu mysql_menu = new JMenu("MySQL");
		JMenuItem mysql = new JMenuItem("mysql");
		mysql.addActionListener(this);
		mysql_menu.add(mysql);
		
		JMenu search = new JMenu("Search");
		int a = 0;
		for (a=0; a < searchBar.length; a++) {
			JMenuItem menuitem = new JMenuItem(searchBar[a]);
			menuitem.addActionListener(this);
			search.add(menuitem);
		}
		
		JMenu course = new JMenu("Enroll/Drop");
		JMenuItem insert = new JMenuItem("enroll");
		insert.addActionListener(this);
		course.add(insert);
		JMenuItem delete = new JMenuItem("drop");
		delete.addActionListener(this);
		course.add(delete);	
		
		JMenu update_menu = new JMenu("Update");
		JMenuItem update = new JMenuItem("change student data");
		update.addActionListener(this);
		update_menu.add(update);
		
		JMenu commend_c = new JMenu("Command class");
		JMenuItem time = new JMenuItem("time");
		time.addActionListener(this);
		commend_c.add(time);
		JMenuItem popu = new JMenuItem("popular");
		popu.addActionListener(this);
		commend_c.add(popu);
		
		JMenu commend_d = new JMenu("Command dorm");		
		JMenuItem dist = new JMenuItem("distance");
		dist.addActionListener(this);
		commend_d.add(dist);
		
		JMenuBar bar = new JMenuBar();
		bar.add(mysql_menu);
		bar.add(search);
		bar.add(course);
		bar.add(update_menu);
		bar.add(commend_c);
		bar.add(commend_d);
		setJMenuBar (bar);
		
		/***label***/
		title = new JLabel();
		title.setLocation(10, 10);
		title.setSize(200,30);
		add(title);
		
		/***text area***/		
		inputlines = new JTextArea[2];
		inputlines[0] = new JTextArea();
		inputlines[1] = new JTextArea();
		add(inputlines[0]);
		add(inputlines[1]);
				
		/***box***/
		comboBox = new JComboBox[2];
		comboBox[0] = new JComboBox<String>();
		comboBox[1] = new JComboBox<String>();
		add(comboBox[0]);
		add(comboBox[1]);
		
		/***button***/
		okbtn = new JButton();
		okbtn.addActionListener(btnlistener);
		okbtn.setLocation(10, 290);
		okbtn.setSize(250, 30);
		add(okbtn);
				
		/***table***/
		table = new JTable();
		pane = new JScrollPane(table);
		getContentPane().add(pane);
	}
	
	/*listen to bar items*/
	public void actionPerformed(ActionEvent e){
		String command = e.getActionCommand();
		clean_interface();
		title.setText(command);
		okbtn.setText(command);
		
		//search bar
		int a;
		for (a=0; a< searchBar.length; a++) {
			if(command.equals(searchBar[a])) {
				search_interface(a);
				return;
			}
		}
		
		//others
		switch(command.charAt(0)) {
			case'm':mysql_interface();//mysql
				break;
			case'e':insert_interface(); //enroll
				break;
			case'd':
				if(command.charAt(1) == 'r') //drop
					drop();
				else //distance
					command2();
				break;
			case'c':update_interface(); //change student data
				break;
			case't':command(); //time
				break;
			case'p':popu_interface(); //popular
				break;
		}
	}	
	
	/*make items invisible*/
	void clean_interface() {
		int a;
		for(a=0; a<2; a++) {
			inputlines[a].setLocation(0,0);
			inputlines[a].setSize(0,0);
			inputlines[a].setText("");
			comboBox[a].setLocation(0,0);
			comboBox[a].setSize(0,0);
			comboBox[a].removeAllItems();
		}
		repaint();
	}
	
	/*interface for bar items*/
	void mysql_interface() {
		/*****GUI*****/
		inputlines[0].setLocation(10,50);
		inputlines[0].setSize(250, 220);
		inputlines[0].setText("");		
		repaint();
	}
	void search_interface(int table_id) {
		/*****put strings into ComboBoxes*****/
		int a;
		int len =  box_con[table_id].length;
		for(a=0; a < len; a++) {
			comboBox[0].addItem(box_con[table_id][a]);
		}
		len = box_con[table_id+5].length;
		for(a=0; a < len; a++) {
			comboBox[1].addItem(box_con[table_id+5][a]);
		}

		/*****GUI*****/
		comboBox[0].setLocation(10, 50);
		comboBox[0].setSize(250, 40);
		
		inputlines[0].setLocation(10,110);
		inputlines[0].setSize(250, 40);
		
		comboBox[1].setLocation(10, 170);
		comboBox[1].setSize(250, 40);
		
		inputlines[1].setLocation(10,230);
		inputlines[1].setSize(250, 40);		
		
		repaint();
	}
	void insert_interface() {
		/*****put student names into ComboBoxes*****/
		try {
			stmt.execute("select student_name from student");
			ResultSet rest = stmt.getResultSet();
			while(rest.next())
				comboBox[0].addItem(rest.getObject(1));
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		/*****GUI*****/
		comboBox[0].setLocation(10, 50);
		comboBox[0].setSize(250, 40);
		
		inputlines[0].setLocation(10,130);
		inputlines[0].setSize(250, 100);		
		repaint();
	}
	
	void drop() {
		/*****put student id into ComboBoxes*****/
		try {
			stmt.execute("select student_id from student");
			ResultSet rest = stmt.getResultSet();
			while(rest.next())
				comboBox[0].addItem(rest.getObject(1));
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		/*****GUI*****/
		comboBox[0].setLocation(10, 50);
		comboBox[0].setSize(250, 40);
		
		inputlines[0].setLocation(10,130);
		inputlines[0].setSize(250, 100);		
		repaint();
		
	}
	void update_interface() {
		/*****put student id into ComboBoxes*****/
		try {
			stmt.execute("select student_id from student");
			ResultSet rest = stmt.getResultSet();
			while(rest.next())
				comboBox[0].addItem(rest.getObject(1));
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		/*****to change attribute*****/
		comboBox[1].addItem("student_name");
		comboBox[1].addItem("sex");
		comboBox[1].addItem("grade");
		
		/*****GUI*****/
		comboBox[0].setLocation(10, 50);
		comboBox[0].setSize(250, 40);
		comboBox[1].setLocation(10, 110);
		comboBox[1].setSize(250, 40);
		inputlines[0].setLocation(10,170);
		inputlines[0].setSize(250, 100);
	}
	void command() {
		/*****mode selection*****/
		comboBox[0].addItem("in");		
		comboBox[0].addItem("not in");
		
		/*****GUI*****/
		comboBox[0].setLocation(10, 50);
		comboBox[0].setSize(250, 40);
		
		inputlines[0].setLocation(10,130);
		inputlines[0].setSize(250, 100);		
		repaint();
	}
	void command2() {
		/*****mode selection*****/
		comboBox[0].addItem("avg");
		comboBox[0].addItem("sum");
		
		comboBox[1].addItem("all");
		comboBox[1].addItem("min");
		comboBox[1].addItem("less than");
		
		/*****UI*****/
		comboBox[0].setLocation(10, 50);
		comboBox[0].setSize(250, 40);
		
		inputlines[0].setLocation(10,110);
		inputlines[0].setSize(250, 40);
		
		comboBox[1].setLocation(10, 170);
		comboBox[1].setSize(250, 40);
		
		inputlines[1].setLocation(10,230);
		inputlines[1].setSize(250, 40);
		
		repaint();
	}
	void popu_interface() {
		/*****put department names into ComboBoxes*****/
		try {
			stmt.execute("select department_name from department");
			ResultSet rest = stmt.getResultSet();
			while(rest.next()) {
				comboBox[0].addItem(rest.getObject(1));
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		/*****mode selection*****/
		comboBox[1].addItem("all");
		comboBox[1].addItem("popular");
		
		/*****UI*****/
		comboBox[0].setLocation(10, 80);
		comboBox[0].setSize(250, 40);
		comboBox[1].setLocation(10, 180);
		comboBox[1].setSize(250, 40);
		
		repaint();		
	}
	
	/*execute the instruction*/
	void useMysql(String instruction) {
		try {
			/*****execute*****/
			stmt.execute(instruction);
			ResultSet rest = stmt.getResultSet();
			System.out.println(instruction + '\n');
			
			/*****get column names*****/
			int col_num = rest.getMetaData().getColumnCount();
			String[] head = new String[col_num];
			int a;
			for(a = 0; a < col_num; a++)
				head[a]=rest.getMetaData().getColumnName(a+1);
			
			/*****get data*****/
			//get row number
			rest.last();
			int row_num = rest.getRow();
			Object[][] data = new Object[row_num][col_num];	
			rest.beforeFirst();
			
			//get data
			int b = 0;
			while(rest.next()) {
				for(a = 0; a < col_num; a++) {
					data[b][a] = rest.getObject(a+1);
				}
				b += 1;
			}
			
			/*****paint*****/
			paint_table(head, data);
		}catch(Exception e){
			
		}
	}

	/*paint a table to show the mysql result*/
	void paint_table(String[] head, Object[][] data) {
		/*****remove old result*****/
		remove(pane);
		
		/*****new table*****/
		table = new JTable(data, head);
		pane = new JScrollPane(table);
		pane.setLocation(275, 10);
		pane.setSize(300, 310);
		getContentPane().add(pane);
		
		repaint();
		System.gc();
	}
}