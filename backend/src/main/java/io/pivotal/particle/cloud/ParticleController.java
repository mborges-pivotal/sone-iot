package io.pivotal.particle.cloud;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ParticleController {

	@Value("${particle.baseUrl}")
	private String baseUrl;

	@Value("${particle.accessToken}")
	private String accessToken;

	/**
	 * devices
	 * 
	 * https://api.spark.io/v1/devices?access_token=
	 * 459b6cd6b7d6c32fa728ec9ceaf941bfdfcfb5f1
	 * 
	 * @return arrays of Devices
	 * @throws Exception
	 */
	@RequestMapping("/devices")
	public Device[] devices() throws Exception {

		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForObject(getBaseUrl(), Device[].class);

	}
	
	@RequestMapping("/device/{id}")
	public Device devices(@PathVariable String id) throws Exception {

		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForObject(getDeviceUrl(id) , Device.class);

	}
	
	@RequestMapping(value="/device/{id}/{name}", method = RequestMethod.GET)
	public Variable variable(@PathVariable String id, @PathVariable String name) throws Exception {

		RestTemplate restTemplate = new RestTemplate();
		return restTemplate.getForObject(getDeviceActionUrl(id, name) , Variable.class);

	}

	@RequestMapping(value="/device/{id}/{name}", method = RequestMethod.POST)
	public Function function(@PathVariable String id, @PathVariable String name, @RequestParam("args") String args) throws Exception {

		MultiValueMap<String, String> parms = new LinkedMultiValueMap<String, String>();
		parms.add("args", args);
		
		RestTemplate restTemplate = new RestTemplate();
		
		// Create the http entity for the request
		HttpEntity<MultiValueMap<String,String>> entity =
		            new HttpEntity<MultiValueMap<String, String>>(parms, null);
		
		return restTemplate.postForObject(getDeviceActionUrl(id, name), entity, Function.class);

	}

	
	////////////////////////////////////////////
	// Helper Methods
	////////////////////////////////////////////

	private String getBaseUrl() {
		return baseUrl + "?access_token=" + accessToken;
	}

	private String getDeviceUrl(String id) {
		return baseUrl + "/" + id + "?access_token=" + accessToken;
	}

	private String getDeviceActionUrl(String id, String name) {
		return baseUrl + "/" + id + "/" + name + "?access_token=" + accessToken;
	}
	
}
