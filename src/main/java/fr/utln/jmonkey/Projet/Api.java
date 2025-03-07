package fr.utln.jmonkey.Projet;

import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Api {
	private static final String CACHE_FILE = "planetes.json";

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
			@SuppressWarnings("deprecation")
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
		usefulData.put("demiGrandAxe", json.getDouble("semimajorAxis"));
		usefulData.put("excentricite", json.getDouble("eccentricity"));
		usefulData.put("periodeOrbitale", json.getDouble("sideralOrbit"));
		usefulData.put("inclination", json.getDouble("inclination"));
		usefulData.put("masse", json.getJSONObject("mass").getDouble("massValue") *
				Math.pow(10, json.getJSONObject("mass").getInt("massExponent")));
		usefulData.put("rayonMoyen", json.getDouble("meanRadius"));
		usefulData.put("gravite", json.getDouble("gravity"));
		usefulData.put("inclinaisonAxiale", json.getDouble("axialTilt"));
		usefulData.put("densite", json.getDouble("density"));
		usefulData.put("temperatureK", json.getDouble("avgTemp"));
		usefulData.put("rotation", json.getDouble("sideralRotation"));

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