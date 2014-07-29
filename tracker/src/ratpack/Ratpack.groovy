import static ratpack.groovy.Groovy.groovyTemplate
import static ratpack.groovy.Groovy.ratpack
import static ratpack.jackson.Jackson.json
import io.conop.JobService
import io.conop.TrackerModule
import ratpack.jackson.JacksonModule

ratpack {

	bindings {
		add new JacksonModule()
		add new TrackerModule()
	}

	handlers { JobService service ->
		assets "public"

		get {
			render groovyTemplate("index.html")
		}

		handler("api/jobs") {
			byMethod {
				get {
					render json(service.allJobs)
				}
				post {
					def job = service.add(request.body.text)
					job.url = "${launchConfig.publicAddress}/api/jobs/${job.id}".toString()

					response.status 201
					response.headers.set "Location", job.url
					render json(job)
				}
			}
		}

		handler("api/jobs/:id") {
			byMethod {
				get {
					def job = service.get(pathTokens?.id)
					if (job) {
						render json(job)
					} else {
						clientError 404
					}
				}
				post {
					def job = service.update(pathTokens?.id, request.body)
					if (job) {
						render json(job)
					} else {
						clientError 404
					}
				}
				delete {
					def job = service.get(pathTokens?.id)
					if (job) {
						service.delete(job.id)
						clientError 204
					} else {
						clientError 404
					}
				}
			}
		}
	}
}