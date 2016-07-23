package io.pivotal.particle.cloud;

import java.time.ZonedDateTime;

import lombok.Data;

@Data
public class Variable {
	
	/*
	 * {
cmd: "VarReturn",
name: "temperature",
result: 30.827838827838818,
coreInfo: {
last_app: "",
last_heard: "2016-07-23T00:23:53.391Z",
connected: true,
last_handshake_at: "2016-07-22T23:19:41.005Z",
deviceID: "3c0023000a47353138383138",
product_id: 6
}
}
	 */
	
	private String cmd;
	private String name;
	private String result;
	
	private CoreInfo coreInfo;
	
	@Data
	public static class CoreInfo {
		
		private String last_app;
		private ZonedDateTime last_heard;
		private boolean connected;
		private ZonedDateTime last_handshake_at;
		private String deviceID;
		private int product_id;		
		
	}

}
