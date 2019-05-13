package model;

public class loginJsonSchema {
	
	// Nota importante xReajustado diferentemente das outras coordenadas é number
	// pois é aplicado alguns procedimentos aritiméticos em cima dele, leia-se aplicação da equação inversa.
	
	private final String schema = 
				"{\n" + 
				"    \"$schema\": \"http://json-schema.org/draft-04/schema#\",\n" + 
				"    \"title\": \"Login\",\n" + 
				"    \"description\": \"Ĩnformações de login do usuário\",\n" + 
				"    \"type\": \"object\",\n" + 
				"    \"required\": [\"login\", \"senha\"],\n" + 
				"    \"properties\": {\n" + 
				"        \"login\": {\n" + 
				"            \"description\": \"Login do usuário\",\n" + 
				"            \"type\": \"string\"\n" + 
				"        },\n" + 
				"        \"senha\": {\n" + 
				"            \"description\": \"Senha do usuário\",\n" + 
				"            \"type\": \"string\"\n" + 
				"        }\n" + 
				"    }\n" + 
				"}";
	
	
	public loginJsonSchema() { 
	}


	public String getSchema() {
		return this.schema;
	}

}
