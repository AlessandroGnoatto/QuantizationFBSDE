package it.univr.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class App {

    public void convertExcelToCSV(Sheet sheet, String sheetName) {
        StringBuilder data = new StringBuilder();
        try {
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    CellType type = cell.getCellTypeEnum();
                    if (type == CellType.BOOLEAN) {
                        data.append(cell.getBooleanCellValue());
                    } else if (type == CellType.NUMERIC) {
                        data.append(cell.getNumericCellValue());
                    } else if (type == CellType.STRING) {
                        data.append(cell.getStringCellValue());
                    } else if (type == CellType.BLANK) {
                    } else {
                        data.append(cell + "");
                    }
                    data.append(",");
                }
                data.append('\n');
            }
            Files.write(Paths.get("exportgrids/"+"BS_2dim_20pts.csv"),
                data.toString().getBytes("UTF-8"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String [] args)
    {
        App app = new App();
        String path =  "exportgrids/"+"BS_2dim_20pts.xlsx";
        //InputStream inp = null;
        try (InputStream inp = new FileInputStream(path)) {
            Workbook wb = WorkbookFactory.create(inp);

            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                System.out.println(wb.getSheetAt(i).getSheetName());
                app.convertExcelToCSV(wb.getSheetAt(i), wb.getSheetAt(i).getSheetName());
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } 
    }
}
