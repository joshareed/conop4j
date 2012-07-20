package org.andrill.conop.search.listeners;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.andrill.conop.search.Event;
import org.andrill.conop.search.Solution;
import org.andrill.conop.search.util.TimerUtils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.common.collect.Maps;

public class ConopWebProgressListener extends AsyncListener {
	private String dataset = null;
	private String endpoint = "http://localhost:8080/conopweb/api/";
	private String name = null;
	private int next = 15;
	private String run = null;
	private Map<String, String> simulation = Maps.newHashMap();

	@Override
	public void configure(final Properties properties) {
		super.configure(properties);

		// require a dataset id
		dataset = properties.getProperty("conopweb.dataset");
		if ((dataset == null) || "".equals(dataset)) {
			System.err.println("No 'conopweb.dataset' configured, unable to send progress");
			dataset = null;
		}

		// override our default endpoint if specified
		if (properties.containsKey("conopweb.endpoint")) {
			endpoint = properties.getProperty("conopweb.endpoint");
		}

		// set our run id
		String date = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date());
		String user = System.getProperty("user.name");
		String host;
		try {
			host = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			host = "unknown_host";
		}

		run = properties.getProperty("conopweb.run");
		if ((run == null) || "".equals(run)) {
			// generate a run id
			run = user + ":" + host + ":" + date;
		}

		// set our name
		if (properties.containsKey("conopweb.name")) {
			name = properties.getProperty("conopweb.name");
		} else {
			name = user + " @ " + date;
		}

		// save our simulation
		for (Entry<Object, Object> e : properties.entrySet()) {
			simulation.put(e.getKey().toString(), e.getValue().toString());
		}
	}

	private String getProgressPayload(final double temp, final long iteration, final Solution best, final Solution s) {
		StringWriter out = new StringWriter();
		JsonGenerator json;
		try {
			json = new JsonFactory().createJsonGenerator(out);
			json.writeStartObject();
			json.writeNumberField("iteration", iteration);
			json.writeNumberField("time", TimerUtils.getCounter());
			json.writeNumberField("temp", temp);
			json.writeNumberField("score", best.getScore());
			json.writeStringField("objective", simulation.get("objective"));
			if (s != null) {
				json.writeFieldName("solution");
				json.writeRawValue(getSolutionPayload(s));
			}
			json.writeEndObject();
			json.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toString();
	}

	private String getRunPayload(final Solution s) {
		StringWriter out = new StringWriter();
		JsonGenerator json;
		try {
			json = new JsonFactory().createJsonGenerator(out);
			json.writeStartObject();
			json.writeStringField("id", run);
			json.writeStringField("dataset", dataset);
			json.writeStringField("name", name);
			json.writeFieldName("simulation");
			json.writeStartObject();
			for (Entry<String, String> e : simulation.entrySet()) {
				json.writeStringField(mangle(e.getKey()), e.getValue());
			}
			json.writeEndObject();
			json.writeEndObject();
			json.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toString();
	}

	private String getSolutionPayload(final Solution s) {
		StringWriter out = new StringWriter();
		JsonGenerator json;
		try {
			json = new JsonFactory().createJsonGenerator(out);
			json.writeStartObject();
			json.writeNumberField("score", s.getScore());
			json.writeArrayFieldStart("events");

			for (Event e : s.getEvents()) {
				json.writeStartObject();
				json.writeStringField("name", e.getName());
				json.writeNumberField("rank", s.getRank(e));
				json.writeNumberField("max", s.getMaxRank(e));
				json.writeNumberField("min", s.getMinRank(e));
				json.writeEndObject();
			}
			json.writeEndArray();
			json.writeEndObject();
			json.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toString();
	}

	private String mangle(final String str) {
		return str.replaceAll("\\.", "/");
	}

	private void post(final String fragment, final String payload) {
		URL url;
		OutputStream out = null;
		try {
			url = new URL(endpoint + fragment);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			out = conn.getOutputStream();
			out.write(payload.getBytes());
			out.flush();
			out.close();
			if (conn.getResponseCode() >= 400) {
				System.out.println("Error posting to ConopWeb: " + conn.getResponseMessage());
			}
			conn.disconnect();
		} catch (MalformedURLException e) {
			System.err.println("Invalid url: " + endpoint + fragment);
			dataset = null; // don't try again
		} catch (IOException e) {
			System.err.println("Error posting to ConopWeb: " + e.getMessage());
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException ignore) {
					// we tried to do the write thing
				}
			}
		}
	}

	@Override
	protected void run(final double temp, final long iteration, final Solution current, final Solution best) {
		// send temp, iteration, best score
		post("runs/" + run + "/progress", getProgressPayload(temp, iteration, best,
				(TimerUtils.getCounter() % 60) == 0 ? best : null));
	}

	@Override
	public void started(final Solution initial) {
		// post the run info and initial score
		post("runs", getRunPayload(initial));
	}

	@Override
	public void stopped(final Solution solution) {
		if (solution == null) {
			post("runs/" + run + "/solution", "{\"score\":null}");
		} else {
			post("runs/" + run + "/solution", getSolutionPayload(solution));
		}
	}

	@Override
	protected boolean test(final double temp, final long iteration, final Solution current, final Solution best) {
		if ((dataset != null) && (TimerUtils.getCounter() >= next)) {
			next += 15;
			return true;
		} else {
			return false;
		}
	}
}
