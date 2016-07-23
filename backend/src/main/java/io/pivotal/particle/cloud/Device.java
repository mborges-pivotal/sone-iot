package io.pivotal.particle.cloud;

import java.time.ZonedDateTime;
import java.util.Map;

import lombok.Data;

/**
 * Device - represents a particle.io Cloud Device
 * 
 * @author mborges
 *
 */
@Data
public class Device {

	/*
	id: "3c0023000a47353138383138",
	name: "photon_borges",
	last_app: null,
	last_ip_address: "70.113.50.241",
	last_heard: "2016-07-22T23:19:41.005Z",
	product_id: 6,
	connected: true,
	platform_id: 6,
	cellular: false,
	status: "normal"
	 */
	
	private String id;
	private String name;
	private String last_app;
	private String last_ip_address;
	
	private ZonedDateTime last_heard;
	
	private int product_id;
	private boolean connected;
	private int platform_id;
	private boolean cellular;
	private String status;
	
	private Map<String, String> variables;
	private String[] functions;
	
	

}
