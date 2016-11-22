package introsde.rest.ehealth;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.io.IOUtils;

public class MyFilter implements Filter {

	public static void createNewDatabase(String fileName) {
		Connection c = null;
		System.out.println("EXECUTING FILTER");
		Statement stmt = null;
		try {
			//try to connect to the db, if it does not exists create it
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:database.sqlite");
			stmt = c.createStatement();
			// look for the necessary tables
			String sql = "SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND (name='Measure' OR name='Person')";
			
			if (stmt.executeQuery(sql).getInt(1) != 2) {
				// there aren't both Pearson and Measure
				System.out.println("Tables are not yet in the db, creating... ");
				// retrieve queries to create and populate the person table from file
				ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
				InputStream is = classLoader.getResourceAsStream("/table_person.txt");
				sql = IOUtils.toString(is);

				stmt.executeUpdate(sql);
				System.out.println("Person Table created successfully");
				// retrieve queries to create and populate the measure table from file
				is = classLoader.getResourceAsStream("/table_measure.txt");
				sql = IOUtils.toString(is);

				stmt.executeUpdate(sql);
				System.out.println("Person Measure created successfully");
			} else
				System.out.println("Tables already in the db");

			stmt.close();
			c.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		createNewDatabase("database.sqlite");
		chain.doFilter(req, res);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

}