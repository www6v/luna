package com.yhd.arch.tuna.service;
import com.yihaodian.architecture.jumper.common.consumer.ConsumerType;
import com.yihaodian.architecture.jumper.common.inner.exceptions.SendFailedException;
import com.yihaodian.architecture.jumper.common.message.Destination;
import com.yihaodian.architecture.jumper.common.message.Message;
import com.yihaodian.architecture.jumper.consumer.BackoutMessageException;
import com.yihaodian.architecture.jumper.consumer.Consumer;
import com.yihaodian.architecture.jumper.consumer.ConsumerConfig;
import com.yihaodian.architecture.jumper.consumer.MessageListener;
import com.yihaodian.architecture.jumper.consumer.NeedResendException;
import com.yihaodian.architecture.jumper.consumer.impl.ConsumerFactoryImpl;

/**
 * @author zfq
 *
 */
public class JumperConsumerTest {

	public JumperConsumerTest() {
		System.setProperty("jumper.debug", "false");
		if(System.getProperty("global.config.path")==null) {
			System.setProperty("global.config.path", "/data/M00/tuna/config_nh");

			startConsumer();
		}
	}

	public void startConsumer(){
		ConsumerConfig  config = new ConsumerConfig();
		config.setConsumerType(ConsumerType.AUTO_ACKNOWLEDGE);
		config.setThreadPoolSize(8);
		int consumerCount = 6;
		for(int i = 4 ; i < consumerCount; i++){
			Consumer consumer = ConsumerFactoryImpl.getInstance().
					createConsumer(Destination.topic("cs_queue"), "zfq_", config);
			ConsumerMessage consumerMessage = new ConsumerMessage(consumer);
			Thread t = new Thread(consumerMessage);
			t.start();
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new JumperConsumerTest();
	}

}
class ConsumerMessage implements Runnable{
	Consumer c = null;
	ConsumerMessage(Consumer c){
		this.c = c;
	}

	public void run(){
		try {
			consume();
		} catch (SendFailedException e) {
			e.printStackTrace();
		}
	}

	public void consume() throws SendFailedException {

		c.setListener(new MessageListener() {
			@Override
			public void onMessage(Message msg) throws BackoutMessageException,
					NeedResendException {
				System.out.println(c+"  <><><><><><><> "+msg);
			}
		});

		c.start();
	}

}