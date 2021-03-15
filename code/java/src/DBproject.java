/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class DBproject{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public DBproject(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");
			
			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}
	
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate (String sql) throws SQLException { 
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;
		
		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
		//creates a statement object 
		Statement stmt = this._connection.createStatement (); 
		
		//issues the query instruction 
		ResultSet rs = stmt.executeQuery (query); 
	 
		/*
		 * obtains the metadata object for the returned result set.  The metadata 
		 * contains row and column info. 
		*/ 
		ResultSetMetaData rsmd = rs.getMetaData (); 
		int numCol = rsmd.getColumnCount (); 
		int rowCount = 0; 
	 
		//iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>(); 
		while (rs.next()){
			List<String> record = new ArrayList<String>(); 
			for (int i=1; i<=numCol; ++i) 
				record.add(rs.getString (i)); 
			result.add(record); 
		}//end while 
		stmt.close (); 
		return result; 
	}//end executeQueryAndReturnResult
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();
		
		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + DBproject.class.getName() +
		            " <dbname> <port> <user>");
			return;
		}//end if
		
		DBproject esql = null;
		
		try{
			System.out.println("(1)");
			
			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}
			
			System.out.println("(2)");
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			
			esql = new DBproject (dbname, dbport, user, "");
			
			boolean keepon = true;
			while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. Add Ship");
				System.out.println("2. Add Captain");
				System.out.println("3. Add Cruise");
				System.out.println("4. Book Cruise");
				System.out.println("5. List number of available seats for a given Cruise.");
				System.out.println("6. List total number of repairs per Ship in descending order");
				System.out.println("7. Find total number of passengers with a given status");
				System.out.println("8. < EXIT");
				
				switch (readChoice()){
					case 1: AddShip(esql); break;
					case 2: AddCaptain(esql); break;
					case 3: AddCruise(esql); break;
					case 4: BookCruise(esql); break;
					case 5: ListNumberOfAvailableSeats(esql); break;
					case 6: ListsTotalNumberOfRepairsPerShip(esql); break;
					case 7: FindPassengersCountWithStatus(esql); break;
					case 8: keepon = false; break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if				
			}catch(Exception e){
				// ignored.
			}
		}
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice

	public static void AddShip(DBproject esql) {//1
	try{
		System.out.print("\tEnter Ship ID: ");
		String input_ID = in.readLine();
		System.out.print("\tEnter Make: ");
		String input_make = in.readLine();
		System.out.print("\tEnter Model: ");
		String input_model = in.readLine();
		System.out.print("\tEnter Age of Ship: ");
		String input_age = in.readLine();
		System.out.print("\tEnter Number of Seats on the Ship: ");
		String input_seats = in.readLine();
		String query = "INSERT INTO Ship (id, make, model, age, seats) VALUES (" + input_ID + ", '" + input_make + "', '" + input_model + "', " + input_age + ", " + input_seats + ")";
		
		esql.executeUpdate(query);
		System.out.println("Added Ship");
		}
	catch(Exception e){
		System.err.println(e.getMessage());
		}	
	}


	public static void AddCaptain(DBproject esql) {//2
	try{
                System.out.print("\tEnter Captain ID: ");
                String input_ID = in.readLine();
                System.out.print("\tEnter the Full Name of the Captain: ");
                String input_fullname = in.readLine();
                System.out.print("\tEnter the Nationality of the Captain: ");
                String input_nationality = in.readLine();
                String query = "INSERT INTO Captain(id, fullname, nationality) VALUES (" + input_ID + ", '" + input_fullname + "', '" + input_nationality + "')";

                esql.executeUpdate(query);
                System.out.println("Added Captain");
                }
        catch(Exception e){
                System.err.println(e.getMessage());
                }
        }

	public static void AddCruise(DBproject esql) {//3
	try{
                System.out.print("\t Enter Cruise number: ");
                String input_cnum = in.readLine();
                System.out.print("\t Enter the cost of this Cruise: $");
                String input_cost = in.readLine();
                System.out.print("\t Enter the number of tickets initially sold: ");
                String input_num_sold = in.readLine();
                System.out.print("\t Enter the number of stops: ");
                String input_num_stops = in.readLine();
                System.out.print("\t Enter the actual departure date (YYYY-MM-DD HR:MIN): ");
                String input_actual_departure_date = in.readLine();
                System.out.print("\t Enter the actual_arrival_date (YYYY-MM-DD HR:MIN): ");
                String input_actual_arrival_date = in.readLine();
                System.out.print("\t Enter the arrival_port: ");
                String input_arrival_port = in.readLine();
                System.out.print("\t Enter the departure_port: ");
                String input_departure_port = in.readLine();

                String query = "INSERT INTO Cruise(cnum, cost, num_sold, num_stops, actual_departure_date, actual_arrival_date, arrival_port, departure_port) VALUES (" + input_cnum + ", " + input_cost + ", " + input_num_sold + ", " + input_num_stops + ", '" + input_actual_departure_date + "', '" + input_actual_arrival_date + "', '" + input_arrival_port + "', '" + input_departure_port +  "')";
		//NOTE: actual_departure_date may or may not need single quotes.
                esql.executeUpdate(query);
                System.out.println("Added Cruise");
         	}
         catch(Exception e){
                System.err.println(e.getMessage());
                }
	}


	public static void BookCruise(DBproject esql) {//4
	try{
		System.out.println("\t Enter Customer information.");
                System.out.print("\t Enter Customer ID: ");
                String input_id = in.readLine();
                System.out.print("\t Enter your first name: ");
                String input_fname = in.readLine();
                System.out.print("\t Enter your last name: ");
                String input_lname = in.readLine();
                System.out.print("\t Enter your gender(M/F): ");
                String input_gtype = in.readLine();
                System.out.print("\t Enter your date of birth date (MM/DD/YYYY): ");
                String input_dob = in.readLine();
		System.out.print("\t Enter your address (123 Street St City): ");
		String input_address = in.readLine();
                System.out.print("\t Enter your phone number:");
                String input_phone = in.readLine();
                System.out.print("\t Enter your zipcode:");
                String input_zipcode = in.readLine();
		
		String query = "INSERT INTO Customer(id,fname,lname,gtype,dob,address,phone,zipcode) VALUES(" + input_id + ", '" + input_fname + "', '" + input_lname + "', '" + input_gtype + "','" + input_dob + "','" + input_address + "', '" + input_phone + "', '" + input_zipcode + "')";
                esql.executeUpdate(query);
                System.out.println("Added Customer");
	
		System.out.print("\t Enter today’s date:");
                String today_date = in.readLine();
		System.out.print("\t Enter the cruise you want(currently cnum):");
                String input_cnum = in.readLine();

		query = " SELECT C.num_sold, S.seats, C.actual_departure_date FROM Cruise C, CruiseInfo CI, Ship S WHERE C.cnum = " + input_cnum + " AND CI.cruise_id = C.cnum  AND S.id = CI.ship_id";
		List<List<String>> rs = esql.executeQueryAndReturnResult(query);

		//int today_year = Integer.parseInt(today_date[0])*1000 +Integer.parseInt(today_date[1]) * 100 + Integer.parseInt(today_date[2])*10 + Integer.parseInt(today_date[3]);
		//int today_month = Integer.parseInt(today_date[5])*10 + Integer.parseInt(today_date[6]);
		//int today_day = Integer.parseInt(today_date[8])*10 + Integer.parseInt(today_date[9]);
		int today_year = Integer.parseInt(today_date.substring(0,4));
		int today_month = Integer.parseInt(today_date.substring(5,7));
		int today_day = Integer.parseInt(today_date.substring(8,10));
		//int query_year = Integer.parseInt(rs.get(0).get(2)[0])*1000 + Integer.parseInt(rs.get(0).get(2)[1])*100 + Integer.parseInt(rs.get(0).get(2)[2])*10 + Integer.parseInt(rs.get(0).get(2)[3]);
		//int query_month = Integer.parseInt(rs.get(0).get(2)[5])*10 + Integer.parseInt(rs.get(0).get(2)[6]);
		//int query_day = Integer.parseInt(rs.get(0).get(2)[8])*10 + Integer.parseInt(rs.get(0).get(2)[9]);
		int query_year = Integer.parseInt(rs.get(0).get(2).substring(0,4));
		int query_month = Integer.parseInt(rs.get(0).get(2).substring(5,7));
		int query_day = Integer.parseInt(rs.get(0).get(2).substring(8,10));
		
		System.out.println("Today's Date: " + today_month + "/" + today_day + "/" + today_year);
		System.out.println("Query Date: " + query_month + "/" + query_day + "/" + query_year);

		System.out.print("\t Enter Reservation Number: ");
		String input_rnum = in.readLine();
		query = "INSERT INTO Reservation(rnum,ccid,cid,status) VALUES(" + input_rnum + ", " + input_id + ", " + input_cnum + ", ";

		if(today_year > query_year || (today_year == query_year && today_month > query_month) || (today_year == query_year && today_month == query_month && today_day >= query_day)){
			//today’s date is after the cruise has departed
			query += "'C');";
		}
		else if(Integer.parseInt(rs.get(0).get(0)) >= Integer.parseInt(rs.get(0).get(1))){
			//more sold than seats
			query += "'W');";
		}
		else if(Integer.parseInt(rs.get(0).get(0)) < Integer.parseInt(rs.get(0).get(1))){
			//less sold than seats
			query += "'R');";
		}
	
		esql.executeUpdate(query);
                System.out.println("Added Reservation"); 
	}//end try
	catch(Exception e){
                System.err.println(e.getMessage());
        }//end catch



		// Given a customer and a Cruise that he/she wants to book, add a reservation to the DB
	}

	public static void ListNumberOfAvailableSeats(DBproject esql) {//5
	try{
		System.out.print("\t Enter Cruise Number: ");
		String input_cnum = in.readLine();
		System.out.print("\t Enter Departure Date (YYYY-MM-DD HR:MIN): ");
		String input_date = in.readLine();

		List<List<String>> rs1 = esql.executeQueryAndReturnResult("SELECT C.num_sold FROM CruiseInfo CI, Cruise C, Ship S WHERE C.cnum = CI.cruise_id AND S.id = CI.ship_id AND '" + input_date + "'= C.actual_departure_date AND " + input_cnum + " = C.cnum");
			
		List<List<String>> rs2 = esql.executeQueryAndReturnResult("SELECT S.seats FROM CruiseInfo CI, Cruise C, Ship S WHERE C.cnum = CI.cruise_id AND S.id = CI.ship_id AND '" + input_date + "'= C.actual_departure_date AND " + input_cnum + " = C.cnum");

		int numsold = Integer.parseInt(rs1.get(0).get(0));
		int numseats = Integer.parseInt(rs2.get(0).get(0));
		int result = numseats - numsold;
		System.out.println("Available Seat(s): " + result);
		}
	catch(Exception e){
		System.err.println(e.getMessage());
		}
		// For Cruise number and date, find the number of availalbe seats (i.e. total Ship capacity minus booked seats )
	}

	public static void ListsTotalNumberOfRepairsPerShip(DBproject esql) {//6
	try{
		String query = "SELECT S.id, COUNT(R.rid) FROM Ship S, Repairs R WHERE S.id = R.ship_id GROUP BY S.id ORDER BY COUNT(R.rid) DESC";
		esql.executeQueryAndPrintResult(query);
		}//end try
	catch(Exception e){
		System.err.println(e.getMessage());
		}//end catch

		// Count number of repairs per Ships and list them in descending order
	}

	
	public static void FindPassengersCountWithStatus(DBproject esql) {//7
	try{
		System.out.print("\t Enter Cruise Number: ");
                String input_cnum = in.readLine();
		System.out.print("\t Enter Status You Would Like to Search For (W/C/R): ");
		String input_status = in.readLine();
		String query = "SELECT COUNT(Cu.id) FROM Cruise C, Customer Cu, Reservation R WHERE C.cnum = R.cid AND R.ccid = Cu.id AND R.status = '" + input_status + "' AND C.cnum =" + input_cnum;
		esql.executeQueryAndPrintResult(query);			
		}//end try
	catch(Exception e){
                System.err.println(e.getMessage());
                }//end catch		
		// Find how many passengers there are with a status (i.e. W,C,R) and list that number.
	}
}
