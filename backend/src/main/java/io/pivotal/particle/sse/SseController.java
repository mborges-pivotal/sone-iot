package io.pivotal.particle.sse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import io.pivotal.particle.BackendApplication;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Controller
public class SseController {

	private static Logger logger = LoggerFactory.getLogger(BackendApplication.class);

	private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

	@RequestMapping(path = "/events", method = RequestMethod.GET)
	public SseEmitter stream() throws IOException {

		SseEmitter emitter = new SseEmitter();
		logger.info("### Client connected ### " + emitter);

		emitters.add(emitter);

		emitter.onCompletion(() -> {
			emitters.remove(emitter);
		});

		return emitter;
	}

	public void sendParticleEvent(String name, String data) {

		logger.info("Sending event - " + name + " " + data);

		emitters.forEach((SseEmitter emitter) -> {
			try {
				emitter.send(SseEmitter.event().name(name).data(data));
			} catch (IOException e) {
				emitter.complete();
				emitters.remove(emitter);
				logger.warn("Problem sending message, probably broken pipe", e);
			}
		});

	}

}
