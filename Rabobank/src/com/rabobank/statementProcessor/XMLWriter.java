package com.rabobank.statementProcessor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.jdom2.Element;

public class XMLWriter {

	File outputfile = null;

	public XMLWriter(File foutputFile) {
		this.outputfile = foutputFile;
	}

	public void writeAsXml(List<Element> frecords) {
		final String ROOT = "records";
		final String RECORD = "record";
		final String REFERENCE = "reference";
		final String ACCOUNTNUMBER = "accountNumber";
		final String DESCRIPTION = "description";
		final String STARTBALANCE = "startBalance";
		final String MUTATION = "mutation";
		final String ENDBALANCE = "endBalance";
		
		XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();

		try {

			XMLEventWriter xmlEventWriter = xmlOutputFactory.createXMLEventWriter(new FileOutputStream(outputfile),"UTF-8");

			XMLEventFactory eventFactory = XMLEventFactory.newInstance();

			XMLEvent end = eventFactory.createDTD("\n");

			// starting the XML document
			StartDocument startDocument = eventFactory.createStartDocument();
			xmlEventWriter.add(startDocument);
			xmlEventWriter.add(end);

			// creating rootElement
			StartElement configStartElement = eventFactory.createStartElement("", "", ROOT);
			xmlEventWriter.add(configStartElement);
			xmlEventWriter.add(end);

			// converting list to map for mapping keys to values and helping to create nodes
			Map<String, String> invalidValues = new HashMap<String, String>();

			// iterating through the invalid records
			for (Element rec : frecords) {

				// writing record element in xml
				StartElement configStartRecord = eventFactory.createStartElement("", "", RECORD);
				xmlEventWriter.add(configStartRecord);

				invalidValues.put(REFERENCE, rec.getAttributeValue(REFERENCE));
				invalidValues.put(DESCRIPTION,rec.getChild(DESCRIPTION).getValue());

				// Write the element nodes
				Set<String> elementNodes = invalidValues.keySet();

				for (String key : elementNodes) {

					if (key.equalsIgnoreCase(REFERENCE)) {

						//creating reference attribute and adding to xml
						Attribute attr = eventFactory.createAttribute(REFERENCE, invalidValues.get(key));
						xmlEventWriter.add(attr);
					}

					if (key.equalsIgnoreCase(DESCRIPTION)) {

						// creating description child node 
						createNode(xmlEventWriter, key, invalidValues.get(key));
					}


				}

				xmlEventWriter.add(eventFactory.createEndElement("", "", RECORD));
				xmlEventWriter.add(end);
			}

			xmlEventWriter.add(eventFactory.createEndElement("", "", ROOT));
			xmlEventWriter.add(end);

			xmlEventWriter.add(eventFactory.createEndDocument());
			xmlEventWriter.close();

		} catch (XMLStreamException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static void createNode(XMLEventWriter eventWriter, String element, String value) throws XMLStreamException {

		// creating nodes
		XMLEventFactory xmlEventFactory = XMLEventFactory.newInstance();
		XMLEvent end = xmlEventFactory.createDTD("\n");
		XMLEvent tab = xmlEventFactory.createDTD("\t");

		// Create Start node
		StartElement sElement = xmlEventFactory.createStartElement("", "", element);
		eventWriter.add(tab);
		eventWriter.add(sElement);

		// Create Content
		Characters characters = xmlEventFactory.createCharacters(value);
		eventWriter.add(characters);

		// Create End node
		EndElement eElement = xmlEventFactory.createEndElement("", "", element);
		eventWriter.add(eElement);
		eventWriter.add(end);

	}

}
