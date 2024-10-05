package dev.peerat.training.cherrytreeholiday.services.auth;

import dev.peerat.framework.utils.json.JsonMap;
import dev.peerat.framework.utils.json.JsonParser;
import dev.peerat.training.cherrytreeholiday.models.CherryUser;

public interface OAuthService{
	
	static JsonParser JSON_PARSER = new JsonParser();
	
	CherryUser auth(JsonMap json);

}
