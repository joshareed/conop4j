package org.andrill.conop.core.schedules

import org.andrill.conop.core.Configuration
import org.andrill.conop.core.Solution

import spock.lang.Specification

class TemperingScheduleSpec extends Specification {

	def "test initial state"() {
		given: 'a tempering schedule'
		def schedule = new TemperingSchedule()

		expect: 'defaults'
		schedule.initial == 1000
		schedule.delta == 0.01
		schedule.steps == 100
		schedule.current == 1000
	}


	def "test configuration"() {
		given: 'a configuration'
		def config = new Configuration(
				'initial': 250,
				'delta': 5,
				'steps': 1
				)

		and: 'a tempering schedule'
		def schedule = new TemperingSchedule()

		when: 'configure'
		schedule.configure(config)

		then: 'everything set properly'
		schedule.initial == 250
		schedule.delta == 5
		schedule.steps == 1
		schedule.current == 250
		schedule.temperTo == 125
		schedule.temperWhen == Math.log10(125)
	}

	def "next works as expected"() {
		given: 'a tempering schedule'
		def schedule = new TemperingSchedule()
		schedule.initial = 10
		schedule.delta = 1
		schedule.steps = 2
		schedule.current = 10
		schedule.temperTo = 5
		schedule.temperWhen = 5

		when: 'next 1'
		def temp = schedule.next(sol(1000))

		then:
		temp == 10
		schedule.count == 1
		schedule.score == 1000

		when: 'next 2'
		temp = schedule.next(sol(1001))

		then:
		temp == 10
		schedule.count == 2
		schedule.score == 1000

		when: 'next 3'
		temp = schedule.next(sol(1001))

		then:
		temp < 1
		schedule.count == 0
		schedule.score == 1000

		when: 'next 4'
		temp = schedule.next(sol(500))

		then:
		temp < 1
		schedule.count == 0
		schedule.score == 500

		when: 'next 5'
		temp = schedule.next(sol(501))
		temp = schedule.next(sol(501))
		temp = schedule.next(sol(501))

		then:
		temp == 5
		schedule.score == 500

		when: 'next 6'
		schedule.current = 0.001
		temp = schedule.next(sol(501))

		then:
		temp == 0
	}

	private sol(double score) {
		def solution = new Solution(null, [])
		solution.score = score
		solution
	}
}
