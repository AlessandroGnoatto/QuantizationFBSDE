package it.univr.test;

import static java.util.Arrays.asList;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import it.univr.model.CorrelatedBlackScholesModel;
import it.univr.productquantizer.ProductQuantizer;
import it.univr.quantizedprocess.ModelQuantizer;
import it.univr.quantizedprocess.QuantizedModel;

import java.util.Map;
import java.util.Random;

public class TestEsportaGriglie2 {
	
	public static void main(String[] args) throws InvalidFormatException, IOException {
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("BS2dim20pts");
		int rowCount = 0;
		
		int numberOfSamples = 1000;
		double initialStock1;
		double initialStock2;
		double riskFree;
		double volatility1;
		double volatility2;
		double correlation;
		double proba;
		Random r = new Random(); 
		
		for(int j = 0; j < numberOfSamples; j++) {
			initialStock1 = 100 + r.nextDouble() * (100.0 - 50.0);
			initialStock2 = 100 + r.nextDouble() * (100.0 - 50.0);
			riskFree = 0.001 + r.nextDouble() * (0.1 - 0.001);
			volatility1 = 0.05 + r.nextDouble() * (0.75 - 0.05);
			volatility2 = 0.05 + r.nextDouble() * (0.75 - 0.05);
			correlation = -1.0 + r.nextDouble() * 2.0;
			
			CorrelatedBlackScholesModel x = new CorrelatedBlackScholesModel(riskFree,volatility1,volatility2,correlation);
			
			ProductQuantizer t = ProductQuantizer.buildWithCostantValues(asList(initialStock1,initialStock2), asList(20,20));
			  
			ModelQuantizer z = new ModelQuantizer(x,1,1,t);
			  
			QuantizedModel quantizedModel = z.run();
			
			Row row = sheet.createRow(rowCount);
			
			Cell cell = row.createCell(0);
			cell.setCellValue(initialStock1);
			cell = row.createCell(1);
			cell.setCellValue(initialStock2);
			cell = row.createCell(2);
			cell.setCellValue(riskFree);
			cell = row.createCell(3);
			cell.setCellValue(volatility1);
			cell = row.createCell(4);
			cell.setCellValue(volatility2);
			cell = row.createCell(5);
			cell.setCellValue(correlation);
			
			int columnCount = 6;
			
			for(Map.Entry<double[], Double> element : quantizedModel.getDistributionOf(quantizedModel.getNumberOfTimeSteps()).entrySet()) {
				
				/*for(int i = 0; i< element.getKey().length; i++) {
					System.out.print(element.getKey()[i] + " ");
				}*/
				//System.out.println();
				cell = row.createCell(columnCount);
				cell.setCellValue(element.getKey()[0]);
				columnCount++;
				cell = row.createCell(columnCount);
				cell.setCellValue(element.getKey()[1]);
				columnCount++;
				
			}
			
			
			
			for(Map.Entry<double[], Double> quantizerk : quantizedModel.getDistributionOf(0).entrySet()) {
				
				for(Map.Entry<double[], Double> element : quantizedModel.getDistributionOf(1).entrySet()) {
					proba = quantizedModel.getTransitionProbabilityLattice().getTransitionProbability(0, quantizerk.getKey(), element.getKey());
					cell = row.createCell(columnCount);
					cell.setCellValue(proba);
					columnCount++;
				}
			}
			
			
			
			try (FileOutputStream outputStream = new FileOutputStream("exportgrids/"+"BS_2dim_20pts.xlsx")) {
	            workbook.write(outputStream);
	        }
			
			rowCount++;
		}
	}

}
