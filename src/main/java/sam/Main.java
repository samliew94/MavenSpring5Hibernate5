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
		
//			new Main(applicationContext);
			new Main(applicationContext.getBean(SessionFactory.class));
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
	private Main(ApplicationContext applicationContext) throws Exception
	{
		SessionFactory sessionFactory = applicationContext.getBean(SessionFactory.class);
		
		try (Session session = sessionFactory.openSession()) 
		{
			String query = "";

			Map selectedData = new HashMap();
			selectedData.put("product_specimens_id", 929697);
			selectedData.put("order_products_id", 2746610);

			Object productSpecimensId = 929697;
			Object orderProductsId = 2746610;

			String mainTestCode = "HEPBSAG";
			List subTestCodes = new ArrayList() {
				{
					add("HEPSBAB");
				}
			};

			// send "C" for this
			List rejectedTestCodes = new ArrayList() {
				{
					add("HIV");
				}
			};
			
			List newTestCodes = new ArrayList(); // send "N" for this
			newTestCodes.add(mainTestCode);
			newTestCodes.addAll(subTestCodes);
			String testCodes = String.join(",", newTestCodes);
			
			query = "SELECT "
					+ "ref_analyserlist.ref_analyserlist_id AS ref_analyserlist_id, "
					+ "ref_analyserlist.order_address AS url "
					+ "FROM "
					+ "order_products "
					+ "LEFT JOIN product_specimens ON product_specimens.order_products_id = order_products.order_products_id "
					+ "AND product_specimens.unit_code = '03' "
					+ "AND product_specimens.isactive = 1 "
					+ "AND product_specimens.created_by != 'wpd' "
					+ "JOIN ref_analyser_param_list ON ref_analyser_param_list.ref_product_id = order_products.ref_product_id "
					+ "AND ref_analyser_param_list.isactive = 1 "
					+ "AND ((ref_analyser_param_list.isdeleted = 0) OR ISNULL(ref_analyser_param_list.isdeleted)) "
					+ "JOIN ref_analyserlist ON ref_analyserlist.ref_analyserlist_id = ref_analyser_param_list.ref_analyserlist_id "
					+ "AND ref_analyserlist.order_address IS NOT NULL "
					+ "AND ref_analyserlist.isactive = 1 "
					+ "WHERE "
					+ "order_products.isactive = 1 "
					+ "AND order_products.isMigrated = 0 "
					+ "AND order_products.created_by <> 'wpd' "
					+ "AND order_products.unit_code = '03' "
					+ "AND order_products.order_products_id = :orderProductsId "
					+ "AND product_specimens.product_specimens_id = :productSpecimensId "
					;
			
			Object[] analyserData = (Object[]) session.createNativeQuery(query)
				.setParameter("orderProductsId", orderProductsId)
				.setParameter("productSpecimensId", productSpecimensId)
				.getResultStream().findFirst().orElse(null);
				;
				
			if (analyserData == null)
				return; // should be continue
			
			Object refAnalyserListId = analyserData[0];
			String url = (String) analyserData[1];
				
			query = "SELECT "
					+ "order_products.lab_no, "
					+ "patients.full_name AS NAME, "
					+ "patients.patient_id_no AS patient_id, "
					+ "IF(orders.isexternal = 0, patients.patient_id_no, patients.registration_no) AS practice_id, "
					+ "DATE_FORMAT(patients.birthdate , '%Y-%m-%d') AS birthdate, "
					+ "gender.generic_code AS sex, "
					+ "IF(orders.isexternal = 0, ref_location.name, ref_facility.name) AS location, "
					+ "IF(order_products.ref_priority_id = 2, 'S', 'R') AS priority, "
					+ "DATE_FORMAT(product_specimens.collected_datetime , '%Y-%m-%d %H:%i:%s') AS sampled_at, "
					+ "DATE_FORMAT(order_products.acknowledge_receipt_date , '%Y-%m-%d %H:%i:%s') AS delivered_at, "
					+ "'O' AS report_type, "
					+ "'0' AS skip "
					+ " "
					+ "FROM "
					+ "order_products "
					+ "JOIN orders ON orders.orders_id = order_products.orders_id "
					+ "AND orders.isactive = 1 "
					+ "AND orders.created_by != 'wpd' "
					+ "AND ((orders.isdeleted = 0) OR ISNULL(orders.isdeleted)) "
					+ "JOIN patients ON patients.patients_id = orders.patients_id "
					+ "AND patients.isactive = 1 "
					+ "AND ((patients.isdeleted = 0) OR ISNULL(patients.isdeleted)) "
					+ "JOIN ref_product ON ref_product.ref_product_id = order_products.ref_product_id "
					+ "AND ref_product.unit_code = '03' "
					+ "AND ref_product.isactive = 1 "
					+ "AND ((ref_product.isdeleted = 0) OR ISNULL(ref_product.isdeleted)) "
					+ "JOIN ref_productlistspecimen ON ref_productlistspecimen.ref_product_id = ref_product.ref_product_id "
					+ "AND ref_productlistspecimen.isactive = 1 "
					+ "AND ((ref_productlistspecimen.isdeleted = 0) OR ISNULL(ref_productlistspecimen.isdeleted)) "
					+ "JOIN ref_productlistgeneric ON ref_productlistgeneric.ref_product_id = ref_product.ref_product_id "
					+ "AND ref_productlistgeneric.performed_analyzer = 1 "
					+ "AND ref_productlistgeneric.isactive = 1 "
					+ "AND ((ref_productlistgeneric.isdeleted = 0) OR ISNULL(ref_productlistgeneric.isdeleted)) "
					+ "LEFT JOIN ref_generic_dataset gender ON gender.ref_generic_dataset_id = patients.ref_gender_id "
					+ "AND gender.generic_category = 'GENDER_TYPE' "
					+ "AND gender.isactive = 1 "
					+ "LEFT JOIN ref_location ON ref_location.ref_location_id = orders.ref_location_id "
					+ "AND ref_location.isactive = 1 "
					+ "LEFT JOIN ref_facility ON ref_facility.ref_facility_id = orders.ref_facility_id "
					+ "AND ref_facility.isactive = 1 "
					+ "AND ((ref_facility.isdeleted = 0) OR ISNULL(ref_facility.isdeleted)) "
					+ "LEFT JOIN product_specimens ON product_specimens.order_products_id =  order_products.order_products_id "
					+ "AND product_specimens.unit_code = '03' "
					+ "AND product_specimens.isactive = 1 "
					+ "AND product_specimens.created_by != 'wpd' "
					+ "JOIN ref_analyser_param_list ON ref_analyser_param_list.ref_product_id = ref_product.ref_product_id "
					+ "AND ref_analyser_param_list.isactive = 1 "
					+ "AND ((ref_analyser_param_list.isdeleted = 0) OR ISNULL(ref_analyser_param_list.isdeleted)) "
					+ "LEFT JOIN ref_analyserlist ON ref_analyserlist.ref_analyserlist_id = ref_analyser_param_list.ref_analyserlist_id "
					+ "AND ref_analyserlist.isactive = 1 "
					+ "AND ((ref_analyserlist.isdeleted = 0) OR ISNULL(ref_analyserlist.isdeleted)) "
					+ " "
					+ "WHERE "
					+ "order_products.isactive = 1 "
					+ "AND order_products.isMigrated = 0 "
					+ "AND order_products.created_by != 'wpd' "
					+ "AND order_products.unit_code = '03' "
					+ "AND order_products.order_products_id = :orderProductsId "
					+ "AND product_specimens.product_specimens_id = :productSpecimensId "
					+ "AND ref_analyserlist.ref_analyserlist_id = :analyserId "
					+ "AND order_products.haschild = 0 ";
					
			Object[] jsonData = (Object[]) session.createNativeQuery(query)
					.setParameter("orderProductsId", orderProductsId)
					.setParameter("productSpecimensId", productSpecimensId)
					.setParameter("analyserId", refAnalyserListId)
					.getResultStream().findFirst().orElse(null);
					;
			
			if (jsonData == null)
				return; // should be continue
			
			Map mainMap = new HashMap();
				List<Map> patientListMap = new ArrayList(); // mainMap has KvP patientListMap
					Map patientMap = new HashMap(); // patientListMap has one element patientMap
						List<Map> ordersListMap = new ArrayList(); // patientMap has KvP ordersListMap
							Map ordersMap = new HashMap(); // ordersListMap has one element ordersMap
			
			Object labNo		= jsonData[0]; 
			Object patientName 	= jsonData[1]; 
			Object patientId 	= jsonData[2]; 
			Object practiceId	= jsonData[3]; 
			Object birthDate	= jsonData[4]; 
			Object sex 			= jsonData[5]; 
			Object location		= jsonData[6]; 
			Object priority		= jsonData[7]; 
			Object sampledAt	= jsonData[8]; 
			Object deliveredAt	= jsonData[9]; 
			Object reportType	= jsonData[10];
			Object skip			= jsonData[11];
			
			String anaylserTransacUniqueId = "";

			if(skip.equals("0"))
			{	
				patientMap.put("practice_id", practiceId);
				patientMap.put("laboratory_id", labNo);
				patientMap.put("id", patientId);
				patientMap.put("name", patientName);
				patientMap.put("birthdate", birthDate);
				patientMap.put("sex", sex);
				patientMap.put("location", location);
				
				anaylserTransacUniqueId += labNo + "_" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
				
				ordersMap.put("sample_id", labNo);
				ordersMap.put("test", testCodes); // example input : 'HEPSAG', 'HEPSAB'
				ordersMap.put("priority", priority);
				ordersMap.put("sampled_at", sampledAt);
				ordersMap.put("action_code", "N"); // new order
				ordersMap.put("location", location);
				ordersMap.put("report_type", reportType);
				ordersMap.put("delivered_at", deliveredAt);
				ordersMap.put("unique_transaction", anaylserTransacUniqueId);
				
				ordersListMap.add(ordersMap);
				
				patientMap.put("orders", ordersListMap);
				
				patientListMap.add(patientMap);
			}
			
			mainMap.put("patients", patientListMap);
			
			//send to anaylser if the url to anaylser is not null
			if(url != null && !url.isEmpty())
			{
				ObjectMapper mapper = new ObjectMapper();
				String res1 = mapper
						.writerWithDefaultPrettyPrinter()
						.writeValueAsString(mainMap);
				
				if (rejectedTestCodes.isEmpty())
					return;						
				
				ordersMap.put("action_code", "C");
				ordersMap.put("test", String.join(",", rejectedTestCodes));
				
				String res2 = mapper
						.writerWithDefaultPrettyPrinter()
						.writeValueAsString(mainMap);
				
				System.out.println(res1);
				System.out.println(res2);
			}
			
		} catch (Exception e) {
			throw e;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
	public Main(SessionFactory sessionFactory) throws Exception 
	{
		try (Session session = sessionFactory.openSession()){
			
			OrderProducts orderProducts = session.get(OrderProducts.class, BigInteger.valueOf(2746610L));
			
			System.out.println(orderProducts.getRefProductId());
			
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	

}
