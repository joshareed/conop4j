package io.conop

import static com.google.inject.Scopes.SINGLETON

import com.google.inject.AbstractModule

class TrackerModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(JobService).in(SINGLETON)
	}
}
