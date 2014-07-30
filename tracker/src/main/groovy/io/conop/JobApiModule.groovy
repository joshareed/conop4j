package io.conop

import static com.google.inject.Scopes.SINGLETON

import com.google.inject.AbstractModule

class JobApiModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(JobService).in(SINGLETON)
	}
}
