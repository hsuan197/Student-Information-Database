import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;

public class BtnListener implements ActionListener {
	private Dbms frame;
	
	public BtnListener(Dbms frame) {
		this.frame = frame;
	}
	
	/*listen to button*/
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		//Search
		int a;
		int len = frame.searchBar.length;
		for (a=0; a < len; a++)
			if(command.equals(frame.searchBar[a])) {
				search(a);
				return;
			}
		switch(command.charAt(0)) {
			case'm': frame.useMysql(frame.inputlines[0].getText());//mysql
				break;
			case'e': enroll(); //enroll
				break;
			case'd':
				if(command.charAt(1) == 'r') //drop
					drop();
				else //distance
					command_distance();
				break;
			case'c': update(); //change student data
				break;
			case't': command_time(); //time
				break;
			case'p': popular(); //popular
				break;
		}
	}
	
	/*make instructions*/
	public void search(int id) {
		//select
		String ins = "select ";
		switch(id)
		{
			case 0:	ins += "student.student_id, student.student_name, student.sex, student.grade\n"; break;
			case 1:	ins += "class.class_id, class.class_name, class.teacher\n"; break;
			case 2:	ins += "classroom.classroom_id, classroom.maxnum, classroom.department_id\n"; break;
			case 3:	ins += "dorm.dorm_id, dorm.sex, dorm.maxnum\n"; break;
			case 4:	ins += "department.department_id, department.department_name, department.director\n"; break;
		}
		
		//from
		boolean box1 = !frame.inputlines[0].getText().isEmpty();	
		boolean box2 = !frame.inputlines[1].getText().isEmpty();
		
		String[] choose = frame.comboBox[1].getSelectedItem().toString().split(", ");
		ins += "from " + frame.searchBar[id];
		
		//where
		if (box2)
			ins += ", " + choose[0] + "\n";
		
		if(box1) {
			ins += " where " + frame.searchBar[id] + "." +  frame.comboBox[0].getSelectedItem() + " = "+ '"' + frame.inputlines[0].getText()+ '"';
			if(box2)
				ins += " AND " + choose[0] + "." + choose[1] + " = " + '"' + frame.inputlines[1].getText() + '"' +
					" AND " + frame.searchBar[id] + "." + frame.searchBar[id] + "_id = " + choose[0]+ "." + frame.searchBar[id] + "_id";
		}
		else if(box2)
			ins += " where "  + choose[0] + "." + choose[1] + " = " + '"' + frame.inputlines[1].getText() + '"' +
				" AND " + frame.searchBar[id] + "." + frame.searchBar[id] + "_id = " + choose[0]+ "." + frame.searchBar[id] + "_id";
		
		/*****execute*****/
		frame.useMysql(ins);
	}
	
	void enroll() {
		String student_name = '"' + frame.comboBox[0].getSelectedItem().toString() + '"';
		String class_name = '"' + frame.inputlines[0].getText()+ '"';
		
		//school have this class
		String class_inq = "SELECT * FROM class WHERE class_name = " + class_name;

		//student haven't enrolled this class
		String student_class_inq ="SELECT class.class_name, student.student_name FROM takeclass, student, class\n" +
				"WHERE student.student_name = " + student_name +
				" AND student.student_id = takeclass.student_id" +
				" AND class_name = " + class_name +
				" AND class.class_id = takeclass.class_id";
		
		//get the max takeclass.num
		String num;
		try{		
			ResultSet rest;
			frame.stmt.execute("select max(num) from takeclass");
			rest = frame.stmt.getResultSet();
			rest.next();			
			num = Integer.toString(rest.getInt(1) + 1);
		}catch(Exception e)	{
			return;
		}
		
		//insert
		String insert =  "INSERT INTO takeclass (num, student_id, class_id)\n" +
					"SELECT " + num + ", student.student_id, class.class_id" +'\n'+
					"FROM student, class\n"+
					"WHERE exists ( " + class_inq + ")\n" +
					" AND not exists ( " + student_class_inq + ')' +
					" AND student.student_name = " + student_name +
					" AND class.class_name = " + class_name;
		
		/*****execute*****/
		frame.useMysql(insert);
		
		/*****show result*****/
		student_class_inq ="SELECT class.class_name, student.student_name FROM takeclass, student, class\n" +
				"WHERE student.student_name = " + student_name +
				" AND student.student_id = takeclass.student_id" +
				" AND class.class_id = takeclass.class_id";
		frame.useMysql(student_class_inq);
	}
	
	void drop() {
		String student_id = '"' + frame.comboBox[0].getSelectedItem().toString() + '"';
		String class_id = '"' + frame.inputlines[0].getText()+ '"';
		
		String delete = "DELETE FROM takeclass \n WHERE " +
				"student_id = " + student_id +
				"AND class_id = " + class_id;

		/*****execute*****/
		frame.useMysql(delete);
		
		/*****show result*****/
		String student_class_inq ="SELECT class.class_name, student.student_name FROM takeclass, student, class\n" +
				"WHERE student.student_id = " + student_id +
				" AND student.student_id = takeclass.student_id" +
				" AND class.class_id = takeclass.class_id";
		frame.useMysql(student_class_inq);
	}

	void update() {
		/*****avoid change data to null*****/
		String change = frame.inputlines[0].getText();
		if (change.isEmpty()) {
			frame.useMysql("select * from student");
			return;
		}
		
		String student_id = '"' + frame.comboBox[0].getSelectedItem().toString() + '"';
		String attribute = frame.comboBox[1].getSelectedItem().toString();
		change = '"' + change + '"';
		
		String ins = "UPDATE student\n" +
				"SET " + attribute  + " = " + change +
				"\nWHERE student_id = " + student_id;
		
		/*****execute*****/
		frame.useMysql(ins);
		
		/*****show result*****/
		frame.useMysql("select * from student");
	}

	void command_time() {
		String[] time = frame.inputlines[0].getText().split(" ");		
		
		String ins = "select participate.class_id, participate.department_id, participate.classroom_id , participate.class_time, class.class_name\n"
				+ "from participate, class\n";

		//format
		String all_time = '"' + time[0] + '"';
		int a;
		for(a=1; a < time.length; a++) {
			all_time += ", \"" + time[a] + '"';
		}
		
		ins +="where class_time " +	frame.comboBox[0].getSelectedItem() //in or not in
				+ " ( " + all_time + ")" + " AND participate.class_id = class.class_id ";
		
		/*****exe*****/
		frame.useMysql(ins);
	}
	
	void command_distance() {
		String[] classroom = frame.inputlines[0].getText().split(" ");		
		
		//format
		String all_classroom = '"' + classroom[0] + '"';
		int a;
		int len = classroom.length;
		for(a=1; a < len; a++)
			all_classroom += ", \"" + classroom[a] + '"';
		
		//all avg or sum distance 
		String ins = "select dorm_id, ";
		ins += frame.comboBox[0].getSelectedItem().toString() + "(distance) as distance from distance\n";
		ins += "where classroom_id in (" + all_classroom + ") \n";
		ins += "group by dorm_id ";
		
		//min or less than
		switch(frame.comboBox[1].getSelectedIndex()) {
			case 1:	ins = "select dorm_id, min(distance)\n" + 
					"from(" + ins + ") a ";
					break;
			case 2: if(!frame.inputlines[1].getText().isEmpty())
						ins += "having " + frame.comboBox[0].getSelectedItem().toString() + 
							"(distance) < " + frame.inputlines[1].getText(); 
					break;
		}
		System.out.println(ins);
		/*****execute*****/
		frame.useMysql(ins);
	}

	void popular() {
		//student number of all class
		String ins = "select takeclass.class_id, teacher, count(takeclass.student_id) as student_num\n" + 
				"from takeclass, participate, class, department\n" + 
				"where takeclass.class_id = class.class_id\n" + 
				"AND participate.class_id = class.class_id\n" + 
				"AND department.department_id = participate.department_id\n" + 
				"AND department.department_name = \"" +  frame.comboBox[0].getSelectedItem().toString() +"\"\n" +
				"group by(takeclass.class_id)";
		
		//max student number
		if(frame.comboBox[1].getSelectedIndex() > 0)
			ins = "select class_id, teacher, max(student_num)\n" +
					"from (" + ins +") a";

		/*****execute*****/
		frame.useMysql(ins);
	}
}