package com.nhncorp.mods.socket.io.impl.transports;

import com.nhncorp.mods.socket.io.impl.ClientData;
import com.nhncorp.mods.socket.io.impl.Manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.json.impl.Json;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * @see <a href="https://github.com/LearnBoost/socket.io/blob/master/lib/transports/jsonp-polling.js">jsonp-polling.js</a>
 * @author Keesun Baik
 */
public class JsonpPolling extends HttpPolling {

	private static final Logger log = LoggerFactory.getLogger(JsonpPolling.class);

	private String head;
	private String foot;

	public JsonpPolling(Manager manager, ClientData clientData)  {
		super(manager, clientData);
		this.head = "io.j";
		this.foot = ");";
	}

	@Override
	protected boolean isPostEncoded() {
		return true;
	}

	/**
	 * Performs the write.
	 *
	 * @see "JSONPPolling.prototype.doWrite"
	 * @param encodedPacket
	 */
	@Override
	protected void doWrite(String encodedPacket) {
		super.doWrite(encodedPacket);

		// JSON.stringfy(encodedPacket)
//		String result = JsonUtils.stringify(encodedPacket);
		String index = clientData.getParams().get("i");
		String result = Json.encode(encodedPacket);
		String data = (encodedPacket == null) ? "" : this.head + "[" + index + "](" + result + this.foot;

		this.response.setStatusCode(200);
		MultiMap headers = this.response.headers();
		headers.add("Content-Type", "text/javascript; charset=UTF-8");
		headers.add("Content-Length", String.valueOf(data.getBytes(Charset.forName("UTF-8")).length));
		headers.add("Connection", "Keep-Alive");
		headers.add("X-XSS-Protection", "0");
		response.write(data);
		log.debug(getName() + " writing " + data);
	}

	@Override
	protected String getName() {
		return "jsonppolling";
	}
}
