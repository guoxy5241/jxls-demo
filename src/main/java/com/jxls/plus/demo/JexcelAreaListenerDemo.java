package com.jxls.plus.demo;

import com.jxls.plus.area.XlsArea;
import com.jxls.plus.command.Command;
import com.jxls.plus.command.EachCommand;
import com.jxls.plus.command.IfCommand;
import com.jxls.plus.common.AreaRef;
import com.jxls.plus.common.CellRef;
import com.jxls.plus.common.Context;
import com.jxls.plus.demo.model.Department;
import com.jxls.plus.transform.Transformer;
import com.jxls.plus.util.TransformerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author Leonid Vysochyn
 *         Date: 2/16/12 5:39 PM
 */
public class JexcelAreaListenerDemo {
    static Logger logger = LoggerFactory.getLogger(JexcelAreaListenerDemo.class);
    private static String template = "each_if_demo.xls";
    private static String output = "target/jexcel_listener_demo_output.xls";

    public static void main(String[] args) throws IOException {
        logger.info("Executing jexcel area listener demo");
        execute();
    }

    public static void execute() throws IOException {
        List<Department> departments = EachIfCommandDemo.createDepartments();
        logger.info("Opening input stream");
        InputStream is = EachIfCommandDemo.class.getResourceAsStream(template);
        OutputStream os = new FileOutputStream(output);
        Transformer transformer = TransformerFactory.createTransformer(is, os);
        System.out.println("Creating area");
        XlsArea xlsArea = new XlsArea("Template!A1:G15", transformer);
        XlsArea departmentArea = new XlsArea("Template!A2:G12", transformer);
        EachCommand departmentEachCommand = new EachCommand("department", "departments", departmentArea);
        XlsArea employeeArea = new XlsArea("Template!A9:F9", transformer);
        XlsArea ifArea = new XlsArea("Template!A18:F18", transformer);
        XlsArea elseArea = new XlsArea("Template!A9:F9", transformer);
        IfCommand ifCommand = new IfCommand("employee.payment <= 2000",
                ifArea,
                elseArea);
        ifArea.addAreaListener(new JexcelSimpleAreaListener(ifArea));
        elseArea.addAreaListener(new JexcelSimpleAreaListener(elseArea));
        employeeArea.addCommand(new AreaRef("Template!A9:F9"), ifCommand);
        Command employeeEachCommand = new EachCommand( "employee", "department.staff", employeeArea);
        departmentArea.addCommand(new AreaRef("Template!A9:F9"), employeeEachCommand);
        xlsArea.addCommand(new AreaRef("Template!A2:F12"), departmentEachCommand);
        Context context = new Context();
        context.putVar("departments", departments);
        logger.info("Applying at cell " + new CellRef("Down!A1"));
        xlsArea.applyAt(new CellRef("Down!A1"), context);
        xlsArea.processFormulas();
        logger.info("Setting EachCommand direction to Right");
        departmentEachCommand.setDirection(EachCommand.Direction.RIGHT);
        logger.info("Applying at cell " + new CellRef("Right!A1"));
        xlsArea.reset();
        xlsArea.applyAt(new CellRef("Right!A1"), context);
        xlsArea.processFormulas();
        logger.info("Complete");
        transformer.write();
        logger.info("written to file");
        is.close();
        os.close();
    }
}
