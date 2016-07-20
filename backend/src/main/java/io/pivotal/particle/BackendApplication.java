package io.pivotal.particle;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.glassfish.jersey.media.sse.EventInput;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.pivotal.particle.sse.SseController;

/**
 * BackendApplication - based on https://github.com/cedricziel/demo-sse-spring-boot
 * 
 * @author mborges
 *
 */
@SpringBootApplication
public class BackendApplication {

	private static Logger logger = LoggerFactory.getLogger(BackendApplication.class);

	private ExecutorService executorService;

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	///////////////////
	// Client listener background thread - for now using SSE client, then RabbitMQ
	// 
	// @see http://programtalk.com/java/start-background-thread-using-spring-on/
	///////////////////
	
	@Autowired
	private SseController service;

	private volatile boolean running = false;

	private final static String baseUrl = "https://api.spark.io/v1/devices/";

	@Value("${particle.deviceId}")
	private String deviceId;

	@Value("${particle.accessToken}")
	private String accessToken;

	@Value("${particle.eventName}")
	private String eventName = ".*"; // default to all events

	@PostConstruct
	public void init() {

		logger.info("############ init()");

		executorService = Executors.newSingleThreadExecutor();
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Pattern r = Pattern.compile(eventName);

					Client client = ClientBuilder.newBuilder().register(SseFeature.class).build();
					WebTarget target = client.target(getEventSourceUrl());

					EventInput eventInput = target.request().get(EventInput.class);

					running = true;
					while (!eventInput.isClosed() && running) {
						final InboundEvent inboundEvent = eventInput.read();

						if (inboundEvent == null) {
							logger.info("Connection has been closed");
							break;
						}

						String name = inboundEvent.getName();
						if (name != null && r.matcher(name).matches()) {
							String data = inboundEvent.readData(String.class);
							logger.info("EventName: {} - {}", name, data);
							service.sendParticleEvent(name, data);
						}
					}
				} catch (Exception e) {
					logger.error("error: ", e);
				}
			}
		});
		executorService.shutdown();
	}

	@PreDestroy
	public void beandestroy() {

		logger.info("############ destroy()");
		running = false;

		if (executorService != null) {
			executorService.shutdownNow();
		}
	}

	////////////////////////////////////////////
	// Helper Methods
	////////////////////////////////////////////

	private String getEventSourceUrl() {
		return baseUrl + deviceId + "/events?access_token=" + accessToken;
	}
}
