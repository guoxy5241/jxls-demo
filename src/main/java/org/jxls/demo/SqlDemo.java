package org.jxls.demo;

import org.jxls.common.Context;
import org.jxls.demo.guide.Employee;
import org.jxls.demo.guide.ObjectCollectionDemo;
import org.jxls.jdbc.JdbcHelper;
import org.jxls.util.JxlsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * Created by Leonid Vysochyn on 17-Oct-15.
 */
public class SqlDemo {
    public static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    public static final String CONNECTION_URL = "jdbc:derby:memory:testDB;create=true";

    static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) throws ParseException, IOException, ClassNotFoundException, SQLException {
        logger.info("Running SQL demo");
        Class.forName(DRIVER);
        initData();
        try (Connection conn = DriverManager.getConnection(CONNECTION_URL)){
            JdbcHelper queryRunner = new JdbcHelper(conn);
            List mapList = queryRunner.query("select * from employee");
            for (Object obj : mapList) {
                Map map = (Map) obj;
                System.out.println("Name: " + map.get("name"));
            }
            JdbcHelper jdbcHelper = new JdbcHelper(conn);
            try(InputStream is = SqlDemo.class.getResourceAsStream("sql_demo_template.xls")) {
                try (OutputStream os = new FileOutputStream("target/sql_demo_output.xls")) {
                    Context context = new Context();
                    context.putVar("conn", conn);
                    context.putVar("sql", jdbcHelper);
                    JxlsHelper.getInstance().processTemplate(is, os, context);
                }
            }
        }
    }

    private static void initData() throws SQLException, ParseException {
        String createTableSlq = "CREATE TABLE employee (" +
                "id INT NOT NULL, " +
                "name VARCHAR(20) NOT NULL, " +
                "PRIMARY KEY (id))";
        String insertSql = "INSERT INTO employee VALUES (?,?)";
        List<Employee> employees = ObjectCollectionDemo.generateSampleEmployeeData();
        try (Connection conn = DriverManager.getConnection(CONNECTION_URL)) {
            try(Statement stmt = conn.createStatement()){
                stmt.executeUpdate(createTableSlq);
                int k = 1;
                try(PreparedStatement insertStmt = conn.prepareStatement(insertSql)){
                    for (Employee employee : employees) {
                        insertStmt.setInt(1, k++);
                        insertStmt.setString(2, employee.getName());
                        insertStmt.executeUpdate();
                    }

                }
            }

        }
    }

}
