package com.jxls.writer.demo;

import com.jxls.writer.area.XlsArea;
import com.jxls.writer.command.Command;
import com.jxls.writer.command.EachCommand;
import com.jxls.writer.command.IfCommand;
import com.jxls.writer.command.ImageCommand;
import com.jxls.writer.common.AreaRef;
import com.jxls.writer.common.CellRef;
import com.jxls.writer.common.Context;
import com.jxls.writer.common.ImageType;
import com.jxls.writer.demo.model.Department;
import com.jxls.writer.transform.Transformer;
import com.jxls.writer.transform.poi.PoiTransformer;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

/**
 * @author Leonid Vysochyn
 */
public class ImageDemo {
    static Logger logger = LoggerFactory.getLogger(ImageDemo.class);
    private static String template = "image_demo.xlsx";
    private static String output = "target/image_output.xlsx";

    public static void main(String[] args) throws IOException, InvalidFormatException {
        logger.info("Executing Image demo");
        execute();
    }

    public static void execute() throws IOException, InvalidFormatException {
        logger.info("Opening input stream");
        InputStream is = ImageDemo.class.getResourceAsStream(template);
        assert is != null;
        logger.info("Creating Workbook");
        Workbook workbook = WorkbookFactory.create(is);
        Transformer poiTransformer = PoiTransformer.createTransformer(workbook);
        XlsArea xlsArea = new XlsArea("Sheet1!A1:N30", poiTransformer);
        Context context = new Context();
        InputStream imageInputStream = ImageDemo.class.getResourceAsStream("car.jpg");
        byte[] imageBytes = IOUtils.toByteArray(imageInputStream);
        context.putVar("image", imageBytes);
        XlsArea imgArea = new XlsArea("Sheet1!A5:D15", poiTransformer);
        xlsArea.addCommand("Sheet1!A4:D15", new ImageCommand("image", ImageType.JPEG).addArea(imgArea));
        xlsArea.applyAt(new CellRef("Sheet2!A1"), context);
        OutputStream os = new FileOutputStream(output);
        workbook.write(os);
        logger.info("written to file");
        is.close();
        os.close();
    }
}