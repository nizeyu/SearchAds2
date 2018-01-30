/**
 * 
 */
/**
 * @author nizeyu
 *
 */
package demo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import org.json.*;
import com.rabbitmq.client.*;

public class AdsEngine {
	private String mBudgetFilePath;
	private IndexBuilder indexBuilder;
	private String mMemcachedServer;
	private int mMemcachedPortal;
	private String mysql_host;
	private String mysql_db;
	private String mysql_user;
	private String mysql_pass;
	private final static String IN_QUEUE_NAME = "q_ad";
	
	public AdsEngine(String budgetDataFilePath,String memcachedServer,int memcachedPortal,String mysqlHost,String mysqlDb,String user,String pass)
	{
		mBudgetFilePath = budgetDataFilePath;
		mMemcachedServer = memcachedServer;
		mMemcachedPortal = memcachedPortal;
		mysql_host = mysqlHost;
		mysql_db = mysqlDb;	
		mysql_user = user;
		mysql_pass = pass;	
		indexBuilder = new IndexBuilder(memcachedServer,memcachedPortal,mysql_host,mysql_db,mysql_user,mysql_pass);
	}
	
	public Boolean initBudget()
	{
		//load budget data
		try (BufferedReader brBudget = new BufferedReader(new FileReader(mBudgetFilePath))) {
			String line;
			while ((line = brBudget.readLine()) != null) {
				JSONObject campaignJson = new JSONObject(line);
				Long campaignId = campaignJson.getLong("campaignId");
				double budget = campaignJson.getDouble("budget");
				Campaign camp = new Campaign();
				camp.campaignId = campaignId;
				camp.budget = budget;
				indexBuilder.updateBudget(camp);
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public Boolean initAd() throws IOException, TimeoutException, InterruptedException
	{
		//load ads data
		ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel inChannel = connection.createChannel();
        inChannel.queueDeclare(IN_QUEUE_NAME, true, false, false, null);
        inChannel.basicQos(10); // Per consumer limit
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        
        //callback
        Consumer consumer = new DefaultConsumer(inChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                try {
                        String message = new String(body, "UTF-8");
                        System.out.println(" [x] Received '" + message + "'");
                        
                        JSONObject adJson = new JSONObject(message);
        				Ad ad = new Ad(); 
        				if(!adJson.isNull("adId") && !adJson.isNull("campaignId")) {
	        				ad.adId = adJson.getLong("adId");
	        				ad.campaignId = adJson.getLong("campaignId");
	        				ad.brand = adJson.isNull("brand") ? "" : adJson.getString("brand");
	        				ad.price = adJson.isNull("price") ? 100.0 : adJson.getDouble("price");
	        				ad.thumbnail = adJson.isNull("thumbnail") ? "" : adJson.getString("thumbnail");
	        				ad.title = adJson.isNull("title") ? "" : adJson.getString("title");
	        				ad.detail_url = adJson.isNull("detail_url") ? "" : adJson.getString("detail_url");						
	        				ad.bidPrice = adJson.isNull("bidPrice") ? 1.0 : adJson.getDouble("bidPrice");
	        				ad.pClick = adJson.isNull("pClick") ? 0.0 : adJson.getDouble("pClick");
	        				ad.category =  adJson.isNull("category") ? "" : adJson.getString("category");
	        				ad.description = adJson.isNull("description") ? "" : adJson.getString("description");
	        				ad.keyWords = new ArrayList<String>();
	        				JSONArray keyWords = adJson.isNull("keyWords") ? null :  adJson.getJSONArray("keyWords");
	        				for(int j = 0; j < keyWords.length();j++)
	        				{
	        					ad.keyWords.add(keyWords.getString(j));
	        				}
        				}
        				indexBuilder.buildForwardIndex(ad);
        				indexBuilder.buildInvertIndex(ad);
                        Thread.sleep(10);
                }catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }  catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        inChannel.basicConsume(IN_QUEUE_NAME, true, consumer);
		//indexBuilder.Close();
		return true;
	}
}