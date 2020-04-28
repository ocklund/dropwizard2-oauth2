package com.example.demo.dw2oauth2.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.With;
import lombok.experimental.Wither;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@AllArgsConstructor
@With
public class AccessToken {
	@JsonProperty("access_token_id")
	@NotNull
	private UUID accessTokenId;

	@JsonProperty("user_id")
	@NotNull
	private Long userId;

	@JsonProperty("last_access_utc")
	@NotNull
	private DateTime lastAccessUTC;
}
