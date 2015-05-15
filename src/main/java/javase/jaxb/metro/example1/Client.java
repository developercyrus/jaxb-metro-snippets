package javase.jaxb.metro.example1;

import javase.jaxb.metro.example1.gen.ExpenseT;
import javase.jaxb.metro.example1.gen.ItemListT;
import javase.jaxb.metro.example1.gen.ItemT;
import javase.jaxb.metro.example1.gen.ObjectFactory;
import javase.jaxb.metro.example1.gen.UserT;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;

public class Client {
    public static void main(String[] args) throws JAXBException, TransformerException {
    	ExpenseT original = createObject();
    	String xml = object2Xml(original);
    	ExpenseT converted = xml2Object(xml);
    	
    	System.out.println(xml);
    	System.out.println(object2XmlIndent(original));
    	System.out.println(original.getUser().getUserFirstName());
    	System.out.println(converted.getUser().getUserFirstName());
    }
    
    public static String object2Xml(ExpenseT expense) throws JAXBException {
    	Writer w = new StringWriter();    	
    	// it does not require @XmlRootElement
        /*
        JAXBContext context = JAXBContext.newInstance("javaee.jaxb.metro.example1.gen");
        JAXBElement<ExpenseT> element = factory.createExpenseReport(expense);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(element, w);
        */

        // it requires @XmlRootElement
        JAXBContext c = JAXBContext.newInstance(ExpenseT.class);
        c.createMarshaller().marshal(expense, w);
		
        return w.toString();
    }
    
    public static String object2XmlIndent(ExpenseT expense) throws JAXBException {
    	Writer w = new StringWriter();    	

        // it requires @XmlRootElement
        JAXBContext c = JAXBContext.newInstance(ExpenseT.class);        
        Marshaller marshaller = c.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(expense, w);
		
        return w.toString();
    }
    
    public static ExpenseT xml2Object(String xml) throws JAXBException {
    	Reader r = new StringReader(xml);
        
        JAXBContext c = JAXBContext.newInstance(ExpenseT.class);
        ExpenseT expense = (ExpenseT) c.createUnmarshaller().unmarshal(r);
        
        return expense;
    }
    
    public static ExpenseT createObject() {
    	ObjectFactory factory = new ObjectFactory();

    	UserT user = factory.createUserT();
        user.setUserFirstName("Sanaulla");
        
        ItemT item = factory.createItemT();
        item.setItemName("Seagate External HDD");
        item.setPurchasedOn("August 24, 2010");
        item.setAmount(new Double("6776.5"));

        ItemListT itemList = factory.createItemListT();
        itemList.getItem().add(item);

        ExpenseT expense = factory.createExpenseT();
        expense.setUser(user);
        expense.setItems(itemList);
        
        return expense;
    }
}
