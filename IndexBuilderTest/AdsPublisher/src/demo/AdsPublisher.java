/**
 * 
 */
/**
 * @author nizeyu
 *
 */
package demo;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.rabbitmq.client.Channel;

public class AdsPublisher {
	
	private static String mAdsDataFilePath = "/Users/nizeyu/workspace/project/SearchAds/IndexBuilderTest/AdsPublisher/ads_0502.txt";
	
	private static void publishAdsInfo() throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare("q_ad", true, false, false, null);
        
        //load ads data
  		try (BufferedReader brAd = new BufferedReader(new FileReader(mAdsDataFilePath))) {
  			String line;
  			while ((line = brAd.readLine()) != null) {
  				System.out.println(" [x] Sent '" + line + "'");
  		        channel.basicPublish("", "q_ad", null, line.getBytes("UTF-8"));  				
  			}
  		} catch (IOException e) {
  			e.printStackTrace();
  		}
  		
        channel.close();
        connection.close();
    }
	
	public static void main(String[] args) throws Exception{
		publishAdsInfo();
    }
}