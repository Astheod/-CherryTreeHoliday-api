package dev.peerat.training.cherrytreeholiday.services.auth;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import dev.peerat.framework.utils.json.JsonMap;
import dev.peerat.training.cherrytreeholiday.models.CherryUser;

public class GoogleAuth implements OAuthService{
	
	@Override
	public CherryUser auth(JsonMap json){
		try{
			URL url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?access_token="+json.get("token"));
			HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
			con.setRequestMethod("GET");
			con.setDoInput(true);
			int code = con.getResponseCode();
			if(code == 200){
				String content = "";
				BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String line;
				while((line = reader.readLine()) != null) content+=line;
				reader.close();
				JsonMap result = JSON_PARSER.parse(content);
				CherryUser user = new CherryUser(result.get("name"), result.get("email"), result.get("given_name"), result.get("family_name"));
				return user;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	

}
