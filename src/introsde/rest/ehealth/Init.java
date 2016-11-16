package introsde.rest.ehealth;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import javax.servlet.ServletContextEvent;

import org.apache.commons.io.IOUtils;

public class Init implements javax.servlet.ServletContextListener{
	/**
     * Connect to a sample database
     *
     * @param fileName the database file name
     */
    public static void createNewDatabase(String fileName) {Connection c = null;
    Statement stmt = null;
    try {
      Class.forName("org.sqlite.JDBC");
      c = DriverManager.getConnection("jdbc:sqlite:database.sqlite");
      System.out.println("Opened database successfully");

      stmt = c.createStatement();
      
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      InputStream is = classLoader.getResourceAsStream("/table_person.txt");
      String sql = IOUtils.toString(is);       
      
      stmt.executeUpdate(sql);
      System.out.println("Person Table created successfully");
      
      is = classLoader.getResourceAsStream("/table_measure.txt");
      sql = IOUtils.toString(is);       
      
      stmt.executeUpdate(sql);
      System.out.println("Person Measure created successfully");      
      
      stmt.close();
      c.close();
      
    } catch ( Exception e ) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      System.exit(0);
    }
        }

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("initializing!!!");
		createNewDatabase("database.sqlite");
		
	}
}
