package io.conop

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j

import java.util.concurrent.locks.ReentrantLock

import org.andrill.conop.core.Configuration
import org.andrill.conop.core.Dataset
import org.andrill.conop.core.Solution
import org.andrill.conop.core.internal.DefaultEvent
import org.andrill.conop.core.listeners.AsyncListener
import org.andrill.conop.core.listeners.PositionsMatrix
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

	protected buildStatsJson(temp, iterations, best) {
		[
			temperature: temp,
			iterations: (iterations - this.iterations),
			score: best.score
		]
	}

	protected buildSolutionJson(Solution best) {
		def json = [
			score: best.score,
			events: []]
		def matrix = context.get(PositionsMatrix)

		best.events.each { event ->
			def e = [
				name: event.name,
				positions: [
					'final': best.getPosition(event),
					min: best.getPosition(event),
					max: best.getPosition(event)
				]
			]

			if (matrix) {
				def range = matrix.getRange(event)
				e.positions.min = range[0]
				e.positions.max = range[1]
			}

			json.events << e
		}

		json
	}

	protected void updateTracker(double temp, long iterations, Solution best) {
		log.info "Sending best solution to ${api}"

		try {
			def payload = [
				stats: buildStatsJson(temp, iterations, best),
				solution: buildSolutionJson(best)
			]
			this.iterations = iterations

			// POST to the server
			def conn = api.openConnection()
			conn.doInput = true
			conn.doOutput = true
			conn.useCaches = false
			conn.requestMethod = 'POST'
			conn.setRequestProperty "Content-Type", "application/json"
			conn.connect()
			conn.outputStream << JsonOutput.toJson(payload)

			def json = new JsonSlurper().parse(conn.inputStream)
			if (json?.solution?.score < best.score) {
				def events = json?.solution?.events.collect {
					new DefaultEvent(it.name)
				}

				def next = new Solution(context.get(Dataset), events)
				next.score = json.solution.score
				context.best = next
			}
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
