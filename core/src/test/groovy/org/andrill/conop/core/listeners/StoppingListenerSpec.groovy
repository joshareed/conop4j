package org.andrill.conop.core.listeners

import org.andrill.conop.core.*
import org.andrill.conop.core.listeners.Listener.Mode
import org.andrill.conop.core.util.TimerUtils

import spock.lang.Specification

class StoppingListenerSpec extends Specification {

	def "initial state"() {
		given: 'a listener'
		def listener = new StoppingListener()

		expect: 'initial configuration'
		listener.stopIteration == -1
		listener.stopProgressIteration == -1
		listener.stopProgressTime == -1
		listener.stopThreshold == -1
		listener.stopThresholdIteration == -1
		listener.stopThresholdTime == -1
		listener.stopTime == -1

		and: 'initial state'
		listener.bestScore == Double.MAX_VALUE
		listener.currentIteration == -1
		listener.lastProgressIteration == -1
		listener.lastProgressTime == -1

		and: 'mode'
		listener.mode == Mode.ANY
	}

	def "test configure"() {
		given: 'a listener'
		def listener = new StoppingListener()

		and: 'a simulation'
		def config = new Configuration(
				"time": 1,
				"steps": 2,
				"progressTime": 3,
				"progressSteps": 4,
				"threshold": 5,
				"thresholdTime": 6,
				"thresholdSteps": 7
				)

		when: 'configure'
		listener.configure(config)

		then: 'initial configuration'
		listener.stopIteration == 2
		listener.stopProgressIteration == 4
		listener.stopProgressTime == 180000
		listener.stopThreshold == 5
		listener.stopThresholdIteration == 7
		listener.stopThresholdTime == 360000
		listener.stopTime == 60000
	}

	def "test abort"() {
		given: 'a listener'
		def listener = new StoppingListener()

		when: 'abort'
		listener.abort("Foo")

		then: 'throws exception'
		def e = thrown(HaltedException)
		e.message == 'Foo'
	}

	def "test stopped"() {
		given: 'a listener'
		def listener = new StoppingListener()

		when: 'stop'
		listener.stop("Bar")

		then: 'throws exception'
		def e = thrown(RuntimeException)
		e.message == 'Bar'
	}

	def "test minutes"() {
		given: 'a listener'
		def listener = new StoppingListener()

		when: 'minutes'
		def minutes = listener.minutes(3 * 60)

		then: 'throws exception'
		minutes == 3
	}

	def "test started"() {
		given: 'a listener'
		def listener = new StoppingListener()
		listener.currentIteration = 100
		listener.lastProgressIteration = 100
		listener.lastProgressTime = 100

		when: 'started'
		listener.started(null)

		then: 'throws exception'
		listener.currentIteration == 0
		listener.lastProgressIteration == 0
		listener.lastProgressTime == 0
	}

	def "test stopping conditions"() {
		given: 'a listener'
		def listener = new StoppingListener()

		and: 'a run'
		def run = RunFixtures.simpleRun()

		expect:
		listener.currentIteration == -1

		when: 'started'
		listener.started(null)

		then:
		listener.currentIteration == 0

		when: 'tried'
		def solution = Solution.initial(run)
		solution.score = 1000
		listener.tried(1000, solution, solution)

		then:
		listener.currentIteration == 1
		listener.bestScore == 1000
		listener.lastProgressIteration == 1
		listener.lastProgressTime >= 0

		when: 'stop iteration'
		listener.currentIteration = 10
		listener.stopIteration = 1
		listener.tried(1000, solution, solution)

		then:
		def e = thrown(RuntimeException)
		e.message == "Stopped because iteration 1 was reached"

		when: 'stop time'
		listener.stopIteration = -1
		listener.stopTime = 60
		TimerUtils.thread.counter = 61
		listener.tried(1000, solution, solution)

		then:
		e = thrown(RuntimeException)
		e.message == "Stopped because run time of 1 minutes was reached"

		when: 'progress iteration'
		listener.stopTime = -1
		listener.stopProgressIteration = 1
		listener.tried(1000, solution, solution)

		then:
		e = thrown(RuntimeException)
		e.message == "Stopped because no progress was made in 1 iterations"

		when: 'stop progress time'
		listener.stopProgressIteration = -1
		listener.stopProgressTime = 60
		listener.lastProgressTime = 1
		listener.tried(1000, solution, solution)

		then:
		e = thrown(RuntimeException)
		e.message == "Stopped because no progress was made in 1 minutes"

		when: 'stop threshold iterations'
		listener.stopProgressTime = -1
		listener.stopThreshold = 100.0
		listener.stopThresholdIteration = 1
		listener.tried(1000, solution, solution)

		then:
		e = thrown(HaltedException)
		e.message == "Stopped because simulation did not reach score threshold of 100.0 in 1 iterations"

		when: 'stop threshold time'
		listener.stopThresholdIteration = -1
		listener.stopThresholdTime = 60
		listener.tried(1000, solution, solution)

		then:
		e = thrown(HaltedException)
		e.message == "Stopped because simulation did not reach score threshold of 100.0 in 1 minutes"
	}
}
