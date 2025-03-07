package fr.utln.jmonkey.Projet;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.Node;
import java.util.Arrays;
import java.util.Random;

public class Asteroide{
	private final float minRadius1 = 1100;
	private final float maxRadius1 = 1700;

	private final float minRadius2 = 60;
	private final float maxRadius2 = 135;

	private final int numAsteroids1 = 2000;
	private final int numAsteroids2 = 400;

	private String[] asteroidModels;
	private Spatial[] asteroidSpatials;

	// Constructeur
	public Asteroide(AssetManager assetManager,String[] asteroidModels) {
		this.asteroidModels = asteroidModels;
		asteroidSpatials = new Spatial[asteroidModels.length];
		for (int i = 0; i < asteroidModels.length; i++) {
			asteroidSpatials[i] = assetManager.loadModel(asteroidModels[i]);
		}
	}

	// Générer un nuage de points dans l'anneau et ajouter les Spatials
	public void generateAsteroids(Node noeud) {
		Random random = new Random();

		for (int i = 0; i < numAsteroids1; i++) {
			// Calculer une position aléatoire dans l'anneau
			float distance = minRadius1 + random.nextFloat() * (maxRadius1 - minRadius1); // Rayon entre min et max
			float angle = random.nextFloat() * 2 * (float) Math.PI; // Angle autour du centre

			// Calculer les coordonnées X et Z pour la position dans le plan
			float x = distance * (float) Math.cos(angle);
			float z = distance * (float) Math.sin(angle);

			// Ajouter une petite variation sur l'axe Y
			float y = (random.nextFloat() - 0.5f) * 100f;

			// Positionner l'astéroïde à cette position
			Vector3f position = new Vector3f(x, y, z);

			String modelPath = asteroidModels[random.nextInt(asteroidModels.length)];
			int index = Arrays.asList(asteroidModels).indexOf(modelPath);
			Spatial asteroid = asteroidSpatials[index].clone();
			asteroid.rotate(random.nextFloat()*2*(float)Math.PI, random.nextFloat()*2*(float)Math.PI, random.nextFloat()*2*(float)Math.PI);
			asteroid.setLocalTranslation(position);
			asteroid.setLocalScale(0.0009f);
			noeud.attachChild(asteroid);
		}

		random = new Random();
		for (int i = 0; i < numAsteroids2; i++) {
			// Calculer une position aléatoire dans l'anneau
			float distance = minRadius2 + random.nextFloat() * (maxRadius2 - minRadius2); // Rayon entre min et max
			float angle = random.nextFloat() * 2 * (float) Math.PI; // Angle autour du centre

			// Calculer les coordonnées X et Z pour la position dans le plan
			float x = distance * (float) Math.cos(angle);
			float z = distance * (float) Math.sin(angle);

			// Ajouter une petite variation sur l'axe Y
			float y = (random.nextFloat() - 0.5f) * 10f;

			// Positionner l'astéroïde à cette position
			Vector3f position = new Vector3f(x, y, z);

			String modelPath = asteroidModels[random.nextInt(asteroidModels.length)];
			int index = Arrays.asList(asteroidModels).indexOf(modelPath);
			Spatial asteroid = asteroidSpatials[index].clone();
			asteroid.rotate(random.nextFloat()*2*(float)Math.PI, random.nextFloat()*2*(float)Math.PI, random.nextFloat()*2*(float)Math.PI);
			asteroid.setLocalTranslation(position);
			asteroid.setLocalScale(0.0003f);
			noeud.attachChild(asteroid);
		}
	}
}