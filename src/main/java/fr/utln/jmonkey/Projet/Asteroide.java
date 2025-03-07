package fr.utln.jmonkey.Projet;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.Node;
import java.util.Arrays;
import java.util.Random;

public class Asteroide{
	//Grande ceinture après Neptune
	private final float minRadius1 = 1100;
	private final float maxRadius1 = 1700;
	private final int numAsteroids1 = 2000;

	//Petite ceinture entre Mars et Jupiter
	private final float minRadius2 = 60;
	private final float maxRadius2 = 135;
	private final int numAsteroids2 = 400;

	private String[] asteroidModels;
	private Spatial[] asteroidSpatials;

	//Charge les différent models
	public Asteroide(AssetManager assetManager,String[] asteroidModels) {
		this.asteroidModels = asteroidModels;
		asteroidSpatials = new Spatial[asteroidModels.length];
		for (int i = 0; i < asteroidModels.length; i++) {
			asteroidSpatials[i] = assetManager.loadModel(asteroidModels[i]);
		}
	}

	//Genere nuages de points en clonant les models
	public void generateAsteroids(Node noeud) {
		Random random = new Random();

		//Génaration des astéroides pour la grande ceinture
		for (int i = 0; i < numAsteroids1; i++) {
			//Position aléatoire et calcul coordonnées
			float distance = minRadius1 + random.nextFloat() * (maxRadius1 - minRadius1);
			float angle = random.nextFloat() * 2 * (float) Math.PI;
			float x = distance * (float) Math.cos(angle);
			float z = distance * (float) Math.sin(angle);
			float y = (random.nextFloat() - 0.5f) * 100f;

			
			Vector3f position = new Vector3f(x, y, z);

			//Clonage et attachement
			String modelPath = asteroidModels[random.nextInt(asteroidModels.length)];
			int index = Arrays.asList(asteroidModels).indexOf(modelPath);
			Spatial asteroid = asteroidSpatials[index].clone();
			asteroid.rotate(random.nextFloat()*2*(float)Math.PI, random.nextFloat()*2*(float)Math.PI, random.nextFloat()*2*(float)Math.PI);//Rotation aléatoire
			asteroid.setLocalTranslation(position);
			asteroid.setLocalScale(0.0009f);
			noeud.attachChild(asteroid);
		}

		//Rebelote pour la petite ceinture
		random = new Random();
		for (int i = 0; i < numAsteroids2; i++) {
			float distance = minRadius2 + random.nextFloat() * (maxRadius2 - minRadius2);
			float angle = random.nextFloat() * 2 * (float) Math.PI;
			float x = distance * (float) Math.cos(angle);
			float z = distance * (float) Math.sin(angle);
			float y = (random.nextFloat() - 0.5f) * 10f;

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