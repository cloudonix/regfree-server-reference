 
package io.cloudonix.regfree.server;

import io.vertx.core.json.JsonObject;
import tech.greenfield.vertx.irked.Controller;
import tech.greenfield.vertx.irked.annotations.Endpoint;

public class RegFreeAPI extends Controller {

	@Endpoint("/")
	WebHandler start = r -> r.sendJSON(new JsonObject().put("version", 1)); 

}