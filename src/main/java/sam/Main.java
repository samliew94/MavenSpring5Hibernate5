/** @author Sam Liew 11 Nov 2022 9:21:51 PM */
package sam;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import sam.models.OrderProducts;

public class Main {

	public static void main(String[] args) {
		
		try 
		{
			ApplicationContext applicationContext = new AnnotationConfigApplicationContext("sam"); 

//			for (String beanName : applicationContext.getBeanDefinitionNames())
//				System.out.println(beanName); 
		
			new Main(applicationContext.getBean(SessionFactory.class));
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
	public Main(SessionFactory sessionFactory) throws Exception 
	{
		try (Session session = sessionFactory.openSession()){
			
			Object res = session.createNativeQuery("SELECT 1").getResultStream().findFirst().get();
			
			System.out.println(res);
			
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	

}
