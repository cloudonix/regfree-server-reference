package io.cloudonix.regfree.server;

import java.util.Map.Entry;

import org.openapitools.client.model.InlineObject18;

import com.fasterxml.jackson.annotation.JsonProperty;

import tech.greenfield.vertx.irked.status.BadRequest;

public class MobileUserAgentRegistration {
	@JsonProperty("Phone Number")
	String phoneNumber;
	@JsonProperty("Phone Type")
	Phone type;
	@JsonProperty("Device ID")
	String deviceID;

	public String getType() {
		return type.getClass().getSimpleName();
	}

	@JsonProperty("Phone Type")
	public void decideType(String t) throws BadRequest {
		switch (t) {
		case "Android": {
			type = new Android();
			break;
		}
		case "iPhone": {
			type = new IPhone();
			break;
		}
		default:
			throw new BadRequest("Error: Phone type was neither Android or iPhone");
		}
	}

	public InlineObject18 convertToAPIobject() {
		return new InlineObject18().msisdn(phoneNumber);
	}
}
