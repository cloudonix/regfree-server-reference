package io.cloudonix.regfree.server;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Call {
	@JsonProperty("Destination")
	String destination;
	@JsonProperty("Phone Number")
	String number;
}
