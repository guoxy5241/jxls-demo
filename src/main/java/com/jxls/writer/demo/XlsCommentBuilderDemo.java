package com.jxls.writer.demo;

import com.jxls.writer.area.Area;
import com.jxls.writer.builder.AreaBuilder;
import com.jxls.writer.builder.xls.XlsCommentAreaBuilder;
import com.jxls.writer.builder.xml.XmlAreaBuilder;
import com.jxls.writer.common.CellRef;
import com.jxls.writer.common.Context;
import com.jxls.writer.demo.model.Department;
import com.jxls.writer.transform.Transformer;
import com.jxls.writer.transform.poi.PoiTransformer;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author Leonid Vysochyn
 */
public class XlsCommentBuilderDemo {
    static Logger logger = LoggerFactory.getLogger(XlsCommentBuilderDemo.class);
    private static String template = "each_if_markup_demo.xls";
    private static String output = "target/each_if_xls_comment_builder_output.xls";

    public static void main(String[] args) throws IOException, InvalidFormatException {
        logger.info("Executing XLS Comment builder demo");
        execute();
    }

    public static void execute() throws IOException, InvalidFormatException {
        List<Department> departments = EachIfCommandDemo.createDepartments();
        logger.info("Opening input stream");
        InputStream is = XlsCommentBuilderDemo.class.getResourceAsStream(template);
        assert is != null;
        logger.info("Creating Workbook");
        Workbook workbook = WorkbookFactory.create(is);
        System.out.println("Creating areas");
        PoiTransformer transformer = PoiTransformer.createTransformer(workbook);
        AreaBuilder areaBuilder = new XlsCommentAreaBuilder(transformer);
        List<Area> xlsAreaList = areaBuilder.build();
        Area xlsArea = xlsAreaList.get(0);
        Context context = new Context();
        context.putVar("departments", departments);
        logger.info("Applying area " + xlsArea.getAreaRef() + " at cell " + new CellRef("Down!A1"));
        xlsArea.applyAt(new CellRef("Down!A1"), context);
        xlsArea.processFormulas();
        logger.info("Complete");
        OutputStream os = new FileOutputStream(output);
        workbook.write(os);
        logger.info("written to file");
        is.close();
        os.close();
    }

}
