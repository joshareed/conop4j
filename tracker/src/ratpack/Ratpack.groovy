import static ratpack.groovy.Groovy.groovyTemplate
import static ratpack.groovy.Groovy.ratpack
import static ratpack.jackson.Jackson.json
import io.conop.JobApiModule
import io.conop.JobService
import ratpack.jackson.JacksonModule

ratpack {

	bindings {
		add new JacksonModule()
		add new JobApiModule()
	}

	handlers { JobService service ->

		handler("api/jobs") {
			byMethod {
				get {
					render json(service.allJobs)
				}
				post {
					def job = service.add(request.body.text)
					if (job) {
						response.status 201
						response.headers.set "Location", job.url
						render json(job)
					} else {
						response.status 400
						render "Invalid simulation file"
					}
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