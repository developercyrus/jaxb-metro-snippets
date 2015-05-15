package javase.jaxb.metro.example2;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javase.jaxb.metro.example2.gen.ConversionRate;
import javase.jaxb.metro.example2.gen.ConversionRateResponse;
import javase.jaxb.metro.example2.gen.Currency;
import javase.jaxb.metro.example2.gen.ObjectFactory;

public class Client {
	
	public static void main(String args[]) throws Exception {
        System.out.println(USD2HKD());
    }
	
	public static double USD2HKD() throws SAXException, IOException, ParserConfigurationException, Exception {
		ConversionRate conversionRate = new ObjectFactory().createConversionRate();
        conversionRate.setFromCurrency(Currency.USD);
        conversionRate.setToCurrency(Currency.HKD);
        
        //Soap client can be used with Spring WebServiceTemplate, very handy, http://myshittycode.com/category/maven/jaxb-2-maven-plugin/
        SOAPMessage soapRequest = createSOAPRequest(jaxb2Doc(conversionRate));
        SOAPMessage soapResponse = createSOAPResponse(soapRequest);
        
        System.out.println("Request SOAP Message: " + format(soapRequest));
        System.out.println("Response SOAP Message: " + format(soapResponse));
        
        ConversionRateResponse c = node2Jaxb(soap2Node(soapResponse));

        return c.getConversionRateResult();
	}
	
	// constructed as DOM, serve for SOAPBody
	/*
	 	<ConversionRate xmlns="http://www.webserviceX.NET/">
            <FromCurrency>USD</FromCurrency>
            <ToCurrency>HKD</ToCurrency>
        </ConversionRate>
	 */
	public static Document jaxb2Doc(ConversionRate conversionRate) throws JAXBException {
		// this preserve namespace, http://stackoverflow.com/a/17078723
		DOMResult res = new DOMResult();
	    JAXBContext c = JAXBContext.newInstance(ConversionRate.class);
	    c.createMarshaller().marshal(conversionRate, res);
	    Document doc = (Document) res.getNode();
	    return doc;
	}


    private static SOAPMessage createSOAPRequest(Document doc) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        // This is case sensitive
        String serverURI = "http://www.webserviceX.NET/";

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // SOAP Body
        SOAPBody soapBody = envelope.getBody();
        soapBody.addDocument(doc);
        
        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", serverURI  + "ConversionRate");
        
        soapMessage.saveChanges();

        return soapMessage;
    }
        
    public static SOAPMessage createSOAPResponse(SOAPMessage soapRequest) throws Exception {
    	SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        String url = "http://www.webservicex.net/CurrencyConvertor.asmx";
        SOAPMessage soapResponse = soapConnection.call(soapRequest, url);

        return soapResponse;
    }
    
    
    // extracted as DOM, and serve unmarshall to jaxb object
    /*
		<ConversionRateResponse xmlns="http://www.webserviceX.NET/">
         	<ConversionRateResult>7.7514</ConversionRateResult>
      	</ConversionRateResponse>
     */
    public static Node soap2Node(SOAPMessage soapResponse) throws SOAPException {
    	return soapResponse.getSOAPBody().getElementsByTagName("ConversionRateResponse").item(0);
    }
    
    
    public static ConversionRateResponse node2Jaxb(Node node) throws JAXBException {
    	JAXBContext c = JAXBContext.newInstance(ConversionRateResponse.class);
    	ConversionRateResponse conversionRateResponse = (ConversionRateResponse)c.createUnmarshaller().unmarshal(node);
    	
    	return conversionRateResponse;
    }
 
    
    public static String format(SOAPMessage message) {
        StringWriter sw = new StringWriter();
        try {
            TransformerFactory.newInstance().newTransformer().transform(new DOMSource(message.getSOAPPart()), new StreamResult(sw));
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
        // after transform , it appends with <?xml version="1.0" encoding="UTF-8" standalone="no"?> at the very beginning
        return sw.toString();
    }
}
