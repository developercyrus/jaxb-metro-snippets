package javaee.jaxb.metro.example1;

import static org.junit.Assert.assertEquals;
import javase.jaxb.metro.example1.Client;
import javase.jaxb.metro.example1.gen.ExpenseT;
import javase.jaxb.metro.example1.gen.ItemListT;
import javase.jaxb.metro.example1.gen.ItemT;
import javase.jaxb.metro.example1.gen.ObjectFactory;
import javase.jaxb.metro.example1.gen.UserT;

import org.junit.Before;
import org.junit.Test;

public class ClientIT {
	private ExpenseT original = null;
	
	@Before
    public void setup() throws Exception {    	
		ObjectFactory factory = new ObjectFactory();

    	UserT user = factory.createUserT();
        user.setUserFirstName("Peter");
        
        ItemT item = factory.createItemT();
        item.setItemName("Seagate External HDD");
        item.setPurchasedOn("May 13, 2015");
        item.setAmount(new Double("1200.5"));

        ItemListT itemList = factory.createItemListT();
        itemList.getItem().add(item);

        original = factory.createExpenseT();
        original.setUser(user);
        original.setItems(itemList);
    }  
	
    @Test
    public void test1() throws Exception {    	
    	String xml = Client.object2Xml(original);
    	ExpenseT actual = Client.xml2Object(xml);
    	    	
    	System.out.println(Client.object2XmlIndent(original));
    	System.out.println(actual.getUser().getUserFirstName());
    	System.out.println(actual.getItems().getItem().get(0).getItemName());
    	System.out.println(actual.getItems().getItem().get(0).getPurchasedOn());
    	System.out.println(actual.getItems().getItem().get(0).getAmount());
        
        assertEquals("Peter", actual.getUser().getUserFirstName());
        assertEquals("Seagate External HDD"	, actual.getItems().getItem().get(0).getItemName());
        assertEquals("May 13, 2015"			, actual.getItems().getItem().get(0).getPurchasedOn());
        assertEquals(1200.5d				, actual.getItems().getItem().get(0).getAmount(),	0.0001);
    }  
}
