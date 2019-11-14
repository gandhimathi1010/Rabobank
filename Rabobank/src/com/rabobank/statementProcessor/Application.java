package com.rabobank.statementProcessor;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import com.rabobank.statementProcessor.*;

public class Application {

	public static void main(String[] args) {
		
		//input and output files in XML format
		File readFrom = new File("C:\\Users\\prash\\OneDrive\\Documents\\CTS\\ValidatingFiles\\xmls\\records.xml");
		File writeTo=new File("C:\\Users\\prash\\OneDrive\\Documents\\CTS\\ValidatingFiles\\xmls\\invalid.xml");
		
		Application app= new Application();
		List<Element> failedrecords=app.validate(readFrom);
		
		XMLWriter writer= new XMLWriter(writeTo);
		writer.writeAsXml(failedrecords);

	}

	//validating input xml and returning failed records
	public List<Element> validate(File inputfile) {
		final String RECORD = "record";
		final String REFERENCE = "reference";
		final String ACCOUNTNUMBER = "accountNumber";
		final String DESCRIPTION = "description";
		final String STARTBALANCE = "startBalance";
		final String MUTATION = "mutation";
		final String ENDBALANCE = "endBalance";
		
		//to store duplicate references
		List<Element> dupeRef=new ArrayList<Element>();
		//declaring a failed balance references
		List<Element> failedbal =new ArrayList<Element>();
		try {

			// creating the saxbuilder for input
			SAXBuilder saxbuildr = new SAXBuilder();

			// creating the document using saxbuilder
			Document doc = saxbuildr.build(inputfile);

			// creating a rootElement
			Element root = doc.getRootElement();
			System.out.println("Root Element: " + doc.getRootElement().getName());

			List<Element> childElements = root.getChildren();

			if (root.getName().equals("records")) {

				/**********************checking balance *************/
				for (int index = 0; index < childElements.size(); index++) {
					Element recordElement = childElements.get(index);

					double endbal = Double.parseDouble(recordElement.getChild(ENDBALANCE).getValue());
					
					if (endbal < 0) {
						failedbal.add(recordElement);
					}
					
				}
				/**********************checking reference*************/
				for(int i=0;i<childElements.size();i++){
					Element recordElement = childElements.get(i);
					for(int j=0;j<i;j++){
						if(childElements.get(j).getAttributeValue(REFERENCE).equals((recordElement.getAttributeValue(REFERENCE)))){
							dupeRef.add(recordElement);
						}
					}
				}
				dupeRef.addAll(failedbal);
				Iterator<Element> it=dupeRef.iterator();
				while(it.hasNext()){
					System.out.println(it.next().getAttributeValue(REFERENCE));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dupeRef;
	}

}
