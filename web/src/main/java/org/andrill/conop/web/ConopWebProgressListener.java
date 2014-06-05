package org.andrill.conop.web;

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

import org.andrill.conop.core.CONOP;
import org.andrill.conop.core.Event;
import org.andrill.conop.core.RanksMatrix;
import org.andrill.conop.core.Simulation;
import org.andrill.conop.core.Solution;
import org.andrill.conop.core.listeners.AsyncListener;
import org.andrill.conop.core.util.TimerUtils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.common.collect.Maps;

public class ConopWebProgressListener extends AsyncListener {
	private String dataset = null;
	private String endpoint = "http://conop.io/api/";
	private String name = null;
	private int next = 15;
	private String run = null;
	private final Map<String, String> invariants = Maps.newHashMap();

	@Override
	public void configure(final Simulation simulation) {
		super.configure(simulation);

		// require a dataset id
		dataset = simulation.getProperty("conopweb.dataset");
		if ((dataset == null) || "".equals(dataset)) {
			System.err.println("No 'conopweb.dataset' configured, unable to send progress");
			dataset = null;
		}

		// override our default endpoint if specified
		endpoint = simulation.getProperty("conopweb.endpoint", endpoint);

		// set our run id
		String date = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date());
		String user = System.getProperty("user.name");
		String host;
		try {
			host = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			host = "unknown_host";
		}

		run = simulation.getProperty("conopweb.run");
		if ((run == null) || "".equals(run)) {
			// generate a run id
			run = user + ":" + host + ":" + date;
		}

		// set our name
		name = simulation.getProperty("conopweb.name", user + " @ " + date);

		// save our simulation
		for (String key : simulation.keys()) {
			invariants.put(key, simulation.getProperty(key));
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
			json.writeStringField("objective", invariants.get("objective"));
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
			for (Entry<String, String> e : invariants.entrySet()) {
				json.writeStringField(mangle(e.getKey()), e.getValue());
			}
			json.writeEndObject();
			json.writeStringField("conop4j", CONOP.VERSION);
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

			RanksMatrix ranks = s.getRun().getRanksMatrix();

			for (Event e : s.getEvents()) {
				json.writeStartObject();
				json.writeStringField("name", e.getName());
				json.writeNumberField("rank", s.getRank(e));
				json.writeNumberField("max", ranks.getMaxRank(e));
				json.writeNumberField("min", ranks.getMinRank(e));
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
		post("runs/" + run + "/progress", getProgressPayload(temp, iteration, best, (TimerUtils.getCounter() % 60) == 0 ? best : null));
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
