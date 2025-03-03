package fr.utln.jmonkey.Projet;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Api {

	private static final String CACHE_FILE = "planetes.json";

	public static void main(String[] args) {
		// Api api = new Api();
		// String[] planetes = {"terre","mercure","venus","mars","jupiter","saturne","neptune","uranus"};
		// for(String planete : planetes){
		// 	JSONObject planetData = api.getPlanetData(planete);
		// 	if (planetData != null) {
		// 		System.out.println("Données récupérées : " + planetData.toString(2));
		// 	} else {
		// 		System.out.println("Erreur lors de la récupération des données.");
		// 	}
		// }
	}

	public JSONObject getPlanetData(String planetName) {
		JSONObject allPlanets = loadCache();

		if (allPlanets.has(planetName)) {
			return allPlanets.getJSONObject(planetName);
		}
		
		JSONObject planetData = fetchPlanetData(planetName);

		if (planetData != null) {
			allPlanets.put(planetName, planetData);
			saveCache(allPlanets);
		}

		return planetData;
	}

	private JSONObject fetchPlanetData(String planetName) {
		try {
			URL url = new URL("https://api.le-systeme-solaire.net/rest/bodies/" + planetName);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			int responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				try (BufferedReader br = new BufferedReader(
						new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = br.readLine()) != null) {
						response.append(line);
					}

					JSONObject jsonResponse = new JSONObject(response.toString());
					return extractUsefulData(jsonResponse);
				}
			} else {
				System.err.println("Erreur HTTP : " + responseCode);
			}
			conn.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private JSONObject extractUsefulData(JSONObject json) {
		JSONObject usefulData = new JSONObject();
		
		usefulData.put("name", json.getString("id"));
		usefulData.put("semiMajorAxis", json.getDouble("semimajorAxis"));
		usefulData.put("eccentricity", json.getDouble("eccentricity"));
		usefulData.put("orbitalPeriod", json.getDouble("sideralOrbit"));
		usefulData.put("inclination", json.getDouble("inclination"));
		usefulData.put("mass", json.getJSONObject("mass").getDouble("massValue") *
				Math.pow(10, json.getJSONObject("mass").getInt("massExponent")));
		usefulData.put("meanRadius", json.getDouble("meanRadius"));

		return usefulData;
	}

	private JSONObject loadCache() {
		File file = new File(CACHE_FILE);
		if (!file.exists()) return new JSONObject();

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			StringBuilder content = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line);
			}
			return new JSONObject(content.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new JSONObject();
	}

	private void saveCache(JSONObject data) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(CACHE_FILE))) {
			writer.write(data.toString(2)); // Format JSON lisible
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}