import static ratpack.groovy.Groovy.groovyTemplate
import static ratpack.groovy.Groovy.ratpack
import static ratpack.jackson.Jackson.json
import io.conop.JobService
import ratpack.form.Form
import ratpack.jackson.JacksonModule

ratpack {
	def service = new JobService()

	bindings {
		add new JacksonModule()
	}

	handlers {
		assets "public"

		get {	//-> /
			render groovyTemplate("index.html")
		}

		prefix("api") {	//-> /api
			prefix("jobs") {	//-> /api/jobs
				prefix(":id") {	//-> /api/jobs/:id
					handler {
						byMethod {
							get {
								def job = service.get(pathTokens?.id)
								if (job) {
									render json(job)
								} else {
									clientError(404)
								}
							}
							delete {
								def job = service.get(pathTokens?.id)
								if (job) {
									service.delete(job.id)
								} else {
									clientError(404)
								}
							}
						}
					}
				}
				handler {
					byMethod {
						get {
							render json(service.activeJobs)
						}
						post {
							Form form = parse(Form.class)
							if (form.source) {
								def job = service.add(form.source)
								response.status(201)
								response.headers.set("Location", "${launchConfig.publicAddress}/api/jobs/${job.id}")
								render json(job)
							} else {
								clientError(400)
							}
						}
					}
				}
			}
		}
	}
}