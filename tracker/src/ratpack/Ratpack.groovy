import static ratpack.groovy.Groovy.groovyTemplate
import static ratpack.groovy.Groovy.ratpack
import static ratpack.jackson.Jackson.json
import io.conop.JobService
import io.conop.TrackerModule
import ratpack.form.Form
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

		prefix("api") {
			prefix("jobs") {
				prefix(":id") {
					// job-specific handler
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
							post {
								try {
									def job = service.update(pathTokens?.id, request.body)
									if (job) {
										render json(job)
									} else {
										clientError(404)
									}
								} catch (e) {
									e.printStackTrace()
								}
							}
							delete {
								def job = service.get(pathTokens?.id)
								if (job) {
									service.delete(job.id)
									render "OK"
								} else {
									clientError(404)
								}
							}
						}
					}
				}

				// job list handler
				handler {
					byMethod {
						get {
							render json(service.allJobs)
						}
						post {
							Form form = parse(Form.class)
							if (form.source) {
								def job = service.add(form.source)
								job.url = "${launchConfig.publicAddress}/api/jobs/${job.id}".toString()

								response.status(201)
								response.headers.set("Location", job.url)
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