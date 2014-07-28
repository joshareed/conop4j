package io.conop

import groovy.json.JsonOutput
import groovy.util.logging.Slf4j

import java.util.concurrent.locks.ReentrantLock

import org.andrill.conop.core.Configuration
import org.andrill.conop.core.Solution
import org.andrill.conop.core.listeners.AsyncListener
import org.andrill.conop.core.util.TimerUtils

@Slf4j
class AgentListener extends AsyncListener {
	protected long next = 0
	protected ReentrantLock lock = new ReentrantLock()
	protected URL api = null
	protected long iterations = 0

	@Override
	public void configure(Configuration config) {
		String url = config.get "api", ""
		if (url) {
			api = new URL(url)
		}
	}

	@Override
	protected void run(double temp, long iteration, Solution current, Solution best) {
		if (lock.tryLock()) {
			try {
				next = TimerUtils.counter + 60
				updateTracker(temp, iteration, best)
			} catch (e) {
				log.debug "Error in AgentListener", e
			} finally {
				lock.unlock()
			}
		}
	}

	protected void updateTracker(double temp, long iterations, Solution best) {
		log.info "Sending best solution to ${api}"
		def payload = [
			temp: temp,
			iterations: (iterations - this.iterations),
			solution: [
				score: best.score,
				events: best.events.collect {
					[name: it.name]
				}
			]
		]
		this.iterations = iterations

		try {
			// POST to the server
			def conn = api.openConnection()
			conn.doInput = true
			conn.doOutput = true
			conn.useCaches = false
			conn.requestMethod = 'POST'
			conn.setRequestProperty "Content-Type", "application/json"
			conn.connect()
			conn.outputStream << JsonOutput.toJson(payload)
			conn.inputStream.close()
		} catch (e) {
			log.info "Error posting update to {}", api
			log.debug "Error posting update", e
			e.printStackTrace()
		}
	}

	@Override
	protected boolean test(double temp, long iteration, Solution current, Solution best) {
		api != null && TimerUtils.counter > next
	}

}
