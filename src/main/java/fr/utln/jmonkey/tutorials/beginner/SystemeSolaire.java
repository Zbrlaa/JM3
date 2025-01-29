package fr.utln.jmonkey.tutorials.beginner;

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import com.jme3.light.PointLight;
import com.jme3.math.Vector3f;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.Geometry;
import com.jme3.material.Material;



public class SystemeSolaire extends SimpleApplication{

	private Geometry soleil;

	public SystemeSolaire(){}
	public static void main(String[] args) {
		SystemeSolaire app = new SystemeSolaire();
		app.start();
	}

	@Override
	public void simpleInitApp() {
		PointLight soleilLumiere = new PointLight();
		soleilLumiere.setPosition(new Vector3f(0, 0, 0)); // Soleil au centre
		soleilLumiere.setRadius(1000f); // Rayon d'influence de la lumière
		soleilLumiere.setColor(new ColorRGBA(1f, 1f, 0.8f, 1f)); // Lumière jaune/blanche
		rootNode.addLight(soleilLumiere);

		Sphere sphere = new Sphere(32, 32, 200); // Résolution et taille
		soleil = new Geometry("Soleil", sphere);

		Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		mat.setTexture("DiffuseMap", assetManager.loadTexture("Planets/sun.jpg"));
		soleil.setMaterial(mat);
		rootNode.attachChild(soleil);
	}

	@Override
	public void simpleUpdate(float tpf) {

		super.simpleUpdate(tpf);
	}

	@Override
	public void start() {
		AppSettings settings = new AppSettings(true);
		settings.setWidth(1900); // Largeur de la fenêtre
		settings.setHeight(1000); // Hauteur de la fenêtre
		settings.setFullscreen(false); // Désactive le mode plein écran
		setSettings(settings);
		super.start();
	}
}
