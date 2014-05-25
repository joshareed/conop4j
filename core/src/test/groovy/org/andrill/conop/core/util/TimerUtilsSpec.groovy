package org.andrill.conop.core.util

import org.andrill.conop.core.util.TimerUtils;

import spock.lang.*

class TimerUtilsSpec extends Specification {

	def "test timer"() {
		expect: 'starts at 0'
		TimerUtils.counter == 0

		when: 'sleep'
		Thread.sleep(2250)

		then: 'counted to 2'
		TimerUtils.counter == 2
	}
}
