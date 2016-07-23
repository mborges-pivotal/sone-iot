package io.pivotal.particle.cloud;

import lombok.Data;

@Data
public class Function {

	/*
	 * "id": "0123456789abcdef01234567", "name": "gongbot", "last_app": "",
	 * "connected": true,
	 */

	private String id;
	private String name;
	private String last_app;
	private boolean connected;

}
