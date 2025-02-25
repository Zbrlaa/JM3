package fr.utln.jmonkey.tutorials.Projet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class API {
	public static void main(String[] args) {
		API api = new API();
		api.requete();
	}

	public API(){
		super();
	}

	public void requete(){
		try {
			// Configuration de la connexion
			URL url = new URL("https://api.le-systeme-solaire.net/rest/bodies?filter[]=isPlanet,eq,true");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			
			// Vérification du code de réponse
			int responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				// Lecture de la réponse
				try (BufferedReader br = new BufferedReader(
						new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = br.readLine()) != null) {
						response.append(line);
					}
					System.out.println("Response: " + response.toString());
				}
			} else {
				System.err.println("Error: " + responseCode);
			}
			conn.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}