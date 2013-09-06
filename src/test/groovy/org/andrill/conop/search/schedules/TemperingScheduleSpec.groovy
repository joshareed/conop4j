package org.andrill.conop.search.schedules

import org.andrill.conop.search.Simulation
import org.andrill.conop.search.Solution

import spock.lang.Specification

class TemperingScheduleSpec extends Specification {

	def "test initial state"() {
		given: 'a tempering schedule'
		def schedule = new TemperingSchedule()

		expect: 'defaults'
		schedule.initial == 1000
		schedule.factor == 0.01
		schedule.minStepsPer == 100
		schedule.current == 1000
	}


	def "test configuration"() {
		given: 'a simulation'
		def simulation = new Simulation(new Properties(), null)
		simulation.setProperty('schedule.initial', '250')
		simulation.setProperty('schedule.delta', '5')
		simulation.setProperty('schedule.stepsPer', '1')

		and: 'a tempering schedule'
		def schedule = new TemperingSchedule()

		when: 'configure'
		schedule.configure(simulation)

		then: 'everything set properly'
		schedule.initial == 250
		schedule.factor == 5
		schedule.minStepsPer == 1
		schedule.current == 250
		schedule.temperTo == 125
		schedule.temperWhen == Math.log10(125)
	}

	def "next works as expected"() {
		given: 'a tempering schedule'
		def schedule = new TemperingSchedule()
		schedule.initial = 10
		schedule.factor = 1
		schedule.minStepsPer = 2
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
