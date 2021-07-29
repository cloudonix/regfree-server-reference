package io.cloudonix.regfree.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.BadAttributeValueExpException;

import org.openapitools.client.ApiException;
import org.openapitools.client.Pair;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.auth.ApiKeyAuth;
import org.openapitools.client.auth.Authentication;
import org.openapitools.client.auth.HttpBearerAuth;
import org.openapitools.client.model.InlineObject27;
import org.openapitools.client.model.InlineObject31;
import org.openapitools.client.model.OutgoingCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.BodyHandler;
import tech.greenfield.vertx.irked.Controller;
import tech.greenfield.vertx.irked.Request;
import tech.greenfield.vertx.irked.annotations.Delete;
import tech.greenfield.vertx.irked.annotations.Endpoint;
import tech.greenfield.vertx.irked.annotations.Get;
import tech.greenfield.vertx.irked.annotations.OnFail;
import tech.greenfield.vertx.irked.annotations.Patch;
import tech.greenfield.vertx.irked.annotations.Post;
import tech.greenfield.vertx.irked.status.BadRequest;
import tech.greenfield.vertx.irked.status.InternalServerError;

public class MobileUserAgentAPI extends Controller {
	@Endpoint("/*")
	BodyHandler bodyHandler = BodyHandler.create();

	private ArrayList<MobileUserAgentRegistration> users = new ArrayList<>();
	static private String tenant = "Cloudonix Staging";
	static private String domain = "test-user-reg.cloudonix.io";
	static private String apiKey = "XIB777D0AB7EDA4189BFF0D04127DEADDC";
	static private Logger log = LoggerFactory.getLogger(MobileUserAgentAPI.class);

	@Post("/register")
	public void createUser(Request req) throws BadRequest, ApiException {
		var body = req.getBodyAsJson();
		if (body == null)
			throw new BadRequest("Error: request did not include a body");
		MobileUserAgentRegistration newUser = req.getBodyAs(MobileUserAgentRegistration.class);
		for (var i : users)
			if (i.phoneNumber.equals(newUser.phoneNumber)) {
				users.remove(i);
				break;
			}
		users.add(newUser);

		DefaultApi cloudonixAPI = createAPI();

		try {
			var rspn = cloudonixAPI.postTenantsTenantNameDomainsDomainSubscribersWithHttpInfo(tenant, domain,
					newUser.convertToAPIobject());
			log.info("Creating subscriber response: " + rspn);
			req.send(newUser);
		} catch (ApiException e) {
			switch (e.getCode()) {
			case 409:
				req.send(newUser);
				break;
			default:
				req.send(new InternalServerError("Error " + e.getCode() + " " + e.getResponseBody()));
			}
		}
	}

	private DefaultApi createAPI() {
		DefaultApi cloudonixAPI = new DefaultApi();
		cloudonixAPI.getApiClient().setBasePath("https://api.staging.cloudonix.io");
		ApiKeyAuth auth = (ApiKeyAuth) cloudonixAPI.getApiClient().getAuthentication("API-Key");
		auth.setApiKey(apiKey);
		auth.setApiKeyPrefix("Bearer");
		return cloudonixAPI;
	}

	@Get("/users")
	public void getUsers(Request req) {
		req.send(users);
	}

	@Post("/call")
	public void createCall(Request req) throws BadRequest, ApiException {
		var body = req.getBodyAsJson();
		if (body == null)
			throw new BadRequest("Error: request did not include a body");

		Call call = req.getBodyAs(Call.class);
		String dst = call.destination;
		String number = call.number;
		DefaultApi cloudonixAPI = createAPI();

		try {
			var rspn = cloudonixAPI.postCallsDomainOutgoingWithHttpInfo(domain, number, createCallObject(dst));
			log.info("Creating call: " + rspn);
			JsonObject token =  new JsonObject();
			token.put("Token", rspn.getData().getToken());
			req.send(token);
		} catch (ApiException e) {
			req.send(new InternalServerError("Error " + e.getCode() + " " + e.getResponseBody()));
		}
	}

	private OutgoingCall createCallObject(String dst) {
		var info = new OutgoingCall();
		info.setDestination(dst);
		return info;
	}

	@Endpoint("/*")
	@OnFail
	WebHandler failureHandler = Request.failureHandler();

}
