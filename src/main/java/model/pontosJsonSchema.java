package model;

public class pontosJsonSchema {
	
	// Nota importante xReajustado diferentemente das outras coordenadas é number
	// pois é aplicado alguns procedimentos aritiméticos em cima dele, leia-se aplicação da equação inversa.
	
	private String schema = 
				"{\n" + 
				"    \"$schema\": \"http://json-schema.org/draft-04/schema#\",\n" + 
				"    \"title\": \"pontos\",\n" + 
				"    \"description\": \"coordenadas de calibração\",\n" + 
				"    \"type\": \"array\",\n" + 
				"\n" + 
				"    \"items\": {\n" + 
				"        \"type\": \"object\",\n" + 
				"        \"required\":[\"x\",\"y\",\"xReajustado\",\"habilitado\"],\n" + 
				"        \"properties\": {\n" + 
				"            \"x\": {\n" + 
				"                \"description\": \"Coordenada representada pelas amostras da garten\",\n" + 
				"                \"type\": \"string\"\n" + 
				"            },\n" + 
				"            \"y\": {\n" + 
				"                \"description\": \"Coordenada representada pelas amostras do laboratório\",\n" + 
				"                \"type\": \"string\"\n" + 
				"            },\n" + 
				"            \"habilitado\": {\n" + 
				"                \"description\": \"Booleano responsável por mostrar ou não no gráfico\",\n" + 
				"                \"type\": \"boolean\"\n" + 
				"            },\n" + 
				"            \"xReajustado\": {\n" + 
				"                \"description\": \"Coordenada das amostras garten reajustadas utilizando a equação inversa\",\n" + 
				"                \"type\": \"number\"\n" + 
				"            }\n" + 
				"        }\n" + 
				"    }\n" + 
				"}";
	
	
	public pontosJsonSchema() { 
	}


	public String getSchema() {
		return this.schema;
	}

}
