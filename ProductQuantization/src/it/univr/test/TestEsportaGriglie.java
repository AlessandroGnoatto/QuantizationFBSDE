package it.univr.test;

import static java.util.Arrays.asList;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.Random;

import it.univr.model.BlackScholesModel;
import it.univr.productquantizer.ProductQuantizer;
import it.univr.products.EuropeanOption;
import it.univr.quantizedprocess.ModelQuantizer;
import it.univr.quantizedprocess.QuantizedModel;

public class TestEsportaGriglie {
	public static void main(String[] args) throws InvalidFormatException, IOException {
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("BS20pts");
		int rowCount = 0;
		
		int numberOfSamples = 1000;
		double initialStock;
		double riskFree;
		double volatility;
		Random r = new Random();
		
		for(int j = 0; j < numberOfSamples; j++) {
			initialStock = 100 + r.nextDouble() * (100.0 - 50.0);
			riskFree = 0.001 + r.nextDouble() * (0.1 - 0.001);
			volatility = 0.05 + r.nextDouble() * (0.75 - 0.05);
			
			BlackScholesModel x = new BlackScholesModel(riskFree,volatility);

			//asList(100) -> [100] initial value of the stock.
			
			ProductQuantizer t =
					ProductQuantizer.buildWithCostantValues(asList(initialStock), asList(20));

			ModelQuantizer z = new ModelQuantizer(x,2,1.0/2,t);

			QuantizedModel w = z.run();
			
			Row row = sheet.createRow(rowCount);
			
			
			Cell cell = row.createCell(0);
			cell.setCellValue(initialStock);
			cell = row.createCell(1);
			cell.setCellValue(riskFree);
			cell = row.createCell(2);
			cell.setCellValue(volatility);
			
			int columnCount = 3;

			//Optimal points
			for(Map.Entry<Double, Double> i: w.getMarginalDistributionOf(1).entrySet()) {
				cell = row.createCell(columnCount);
				cell.setCellValue(i.getKey());
				columnCount++;
			}
			
			//Probabilities 
			for(Map.Entry<Double, Double> i: w.getMarginalDistributionOf(1).entrySet()) {
				cell = row.createCell(columnCount);
				cell.setCellValue(i.getValue());
				columnCount++;
			}
			
			try (FileOutputStream outputStream = new FileOutputStream("exportgrids/"+"BS_20pts.xlsx")) {
	            workbook.write(outputStream);
	        }
			
			rowCount++;
		}
		

	
		/*EuropeanOption d = new EuropeanOption(90,'c');
		System.out.println();
		System.out.println(d.evaluate(w));*/


		/*AmericanOption q = new AmericanOption(100.0);

			System.out.println(q.evaluate(w));*/		  
	}

}
