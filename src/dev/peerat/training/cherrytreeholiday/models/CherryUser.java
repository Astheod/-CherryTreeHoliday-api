package dev.peerat.training.cherrytreeholiday.models;

import org.jose4j.jwt.JwtClaims;

import dev.peerat.framework.User;

public class CherryUser extends User{
	
	private String pseudo;
	private String email;
	private String firstName;
	private String lastName;

	public CherryUser(JwtClaims claims){
		this.pseudo = claims.getClaimValueAsString("pseudo");
	}
	
	public CherryUser(String pseudo){
		this.pseudo = pseudo;
	}
	
	public CherryUser(String pseudo, String email, String firstName, String lastName){
		this(pseudo);
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	
	public String getPseudo() {
		return pseudo;
	}


	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getLastName() {
		return lastName;
	}


	public void setSurname(String lastName) {
		this.lastName = lastName;
	}


	@Override
	public void write(JwtClaims claims){
		claims.setStringClaim("pseudo", pseudo);
	}
}
