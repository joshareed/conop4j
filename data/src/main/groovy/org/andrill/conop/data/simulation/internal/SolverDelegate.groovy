package org.andrill.conop.data.simulation.internal

import org.andrill.conop.core.constraints.EventConstraints
import org.andrill.conop.core.constraints.NullConstraints
import org.andrill.conop.core.internal.DefaultSolverConfiguration
import org.andrill.conop.core.internal.QueueSolver
import org.andrill.conop.core.internal.StandardSolver
import org.andrill.conop.core.listeners.ConsoleProgressListener
import org.andrill.conop.core.listeners.PositionsListener
import org.andrill.conop.core.listeners.SnapshotListener
import org.andrill.conop.core.listeners.StatsLoggerListener
import org.andrill.conop.core.listeners.StoppingListener
import org.andrill.conop.core.mutators.RandomMutator
import org.andrill.conop.core.penalties.MatrixPenalty
import org.andrill.conop.core.penalties.PlacementPenalty
import org.andrill.conop.core.schedules.ExponentialSchedule
import org.andrill.conop.core.schedules.LinearSchedule
import org.andrill.conop.core.schedules.TemperingSchedule

class SolverDelegate {
	def config = new DefaultSolverConfiguration()

	static CONSTRAINTS = [
		'null': NullConstraints.class.canonicalName,
		'event': EventConstraints.class.canonicalName
	]
	static MUTATORS = [
		'random': RandomMutator.class.canonicalName
	]
	static SCHEDULES = [
		'linear': LinearSchedule.class.canonicalName,
		'exponential': ExponentialSchedule.class.canonicalName,
		'tempering': TemperingSchedule.class.canonicalName
	]
	static PENALTIES = [
		'matrix': MatrixPenalty.class.canonicalName,
		'placement': PlacementPenalty.class.canonicalName
	]
	static LISTENERS = [
		'stopping': StoppingListener.class.canonicalName,
		'snapshot': SnapshotListener.class.canonicalName,
		'console': ConsoleProgressListener.class.canonicalName,
		'positions': PositionsListener.class.canonicalName,
		'stats': StatsLoggerListener.class.canonicalName
	]
	static SOLVERS = [
		'conop': StandardSolver.class.canonicalName,
		'qnop': QueueSolver.class.canonicalName
	]

	protected classFor(name, lookup) {
		def clazz = lookup[name] ?: name
		Class.forName(clazz)
	}

	def constraints(Map params = [:], String name) {
		config.configureConstraints(classFor(name, CONSTRAINTS), params)
	}

	def mutator(Map params = [:], String name) {
		config.configureMutator(classFor(name, MUTATORS), params)
	}

	def schedule(Map params = [:], String name) {
		config.configureSchedule(classFor(name, SCHEDULES), params)
	}

	def penalty(Map params = [:], String name) {
		config.configurePenalty(classFor(name, PENALTIES), params)
	}

	def listener(Map params = [:], String name) {
		config.configureListener(classFor(name, LISTENERS), params)
	}

	def solver(Map params = [:], String name) {
		config.configureSolver(classFor(name, SOLVERS), params)
	}
}
