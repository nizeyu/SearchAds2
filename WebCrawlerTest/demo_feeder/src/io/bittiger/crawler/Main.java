package io.bittiger.crawler;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

public class Main {
    private static String QUEUE_NAME = "q_demo";


    private static void topic_exchange() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare("e_topic_demo", "topic",true);

        String message = "topic 1.info message cs504-1704";
        //wild card
        String routingKey = "*.info";

        channel.basicPublish("e_topic_demo", routingKey, null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");
        System.out.println(" [x] Sent '" + routingKey + "':'" + message + "'");

        channel.close();
        connection.close();

    }

    private static void fanout_exchange() throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare("e_fanout_demo2", "fanout",true);

        String message = "broadcast message3";

        channel.basicPublish("e_fanout_demo2", "", null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");

        channel.close();
        connection.close();
    }

    private static void queue_withoutexchange() throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //channel.exchangeDeclare();
        channel.queueDeclare("q_demo", true, false, false, null);
        String msg = "hello, CS504 student";
        System.out.println(" [x] Sent '" + msg + "'");

        channel.basicPublish("", "q_demo", null, msg.getBytes("UTF-8"));

        Channel channel2 = connection.createChannel();
        String msg2 = "hello, rabbit2";
        System.out.println(" [x] Sent '" + msg2 + "'");
        channel2.exchangeDeclare("ex_test" , "direct", true );

        channel2.queueDeclare("q_test", true, false, false, null);
        channel2.basicPublish("ex_test", "", null, msg2.getBytes("UTF-8"));

        channel2.close();
        channel.close();
        connection.close();

    }

    private static void publishCrawlerFeed() throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare("q_feeds", true, false, false, null);
        //class Feed {String category; String url;}
        String msg = "Outdoor Furniture, 22.5, 8100, 15";
        System.out.println(" [x] Sent '" + msg + "'");

        channel.basicPublish("", "q_feeds", null, msg.getBytes("UTF-8"));
        channel.close();
        connection.close();
    }

    private static void publish_direct_exchange() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare("e_test_demo","direct", true);
        String msg = "direct message from cs504-1704 for msg2";
        channel.basicPublish("e_test_demo", "", null, msg.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + msg + "'");

        channel.close();
        connection.close();

    }


    public static void main(String[] args) throws Exception{
        //publish_direct_exchange();
        //queue_withoutexchange();

        //fanout_exchange();
        //topic_exchange();
        publishCrawlerFeed();
    }
}
