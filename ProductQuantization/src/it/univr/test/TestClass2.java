package it.univr.test;

import java.io.IOException;

import java.util.Map;



import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import it.univr.model.HestonModel;
import it.univr.model.parameters.HestonParameterFunction;
import it.univr.productquantizer.ProductQuantizer;
import it.univr.quantizedprocess.ModelQuantizer;
import it.univr.quantizedprocess.QuantizedModel;

import static java.util.Arrays.asList;
public class TestClass2 {

	public static void main(String[] args) throws InvalidFormatException, IOException {

		HestonParameterFunction parameter = new HestonParameterFunction(0.04,2.3924,0.0929,0.6903,-0.82); 
		HestonModel x = new HestonModel(parameter);
		

		
		ProductQuantizer t = ProductQuantizer.buildWithCostantValues(asList(Math.log(100.0),0.0719), asList(20,10));
		

		ModelQuantizer z = new ModelQuantizer(x,20,1.0/20,t);
		QuantizedModel w = z.run();

		double d =0;
		for(Map.Entry<double[], Double> element : z.quantizedProcess.getDistributionOf(20).entrySet()) {
			
			d = d +  element.getValue();
		}
		System.out.println(d);

		
	}


}
