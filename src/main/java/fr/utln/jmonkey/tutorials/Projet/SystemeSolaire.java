package fr.utln.jmonkey.tutorials.Projet;

import java.util.ArrayList;
import java.util.List;

import com.jme3.app.SimpleApplication;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.ProgressBar;


public class SystemeSolaire extends SimpleApplication {
	private List<Planet> planets;
	private float timeV;
	private int camPlanet;

	private ChaseCamera chaseCam;
	private Label timeLabel;

	private Geometry button;

	public static void main(String[] args){
		SystemeSolaire app = new SystemeSolaire();
		app.start();
	}

	@Override
	public void simpleInitApp(){
		camPlanet = 0;
		initButton();

		GuiGlobals.initialize(this); // Initialisation Lemur
		Label label = new Label("Système Solaire !!");
		label.setFontSize(20);
		label.setColor(ColorRGBA.White);
		label.setLocalTranslation(settings.getWidth()/2 , settings.getHeight() - 50, 0);
		guiNode.attachChild(label);


		timeV = 1;
		inputManager.addMapping("Increase", new KeyTrigger(KeyInput.KEY_UP));
		inputManager.addMapping("Decrease", new KeyTrigger(KeyInput.KEY_DOWN));
		inputManager.addListener(actionListener, "Increase", "Decrease");


		inputManager.addMapping("camPlus", new KeyTrigger(KeyInput.KEY_RIGHT));
		inputManager.addMapping("camMoins", new KeyTrigger(KeyInput.KEY_LEFT));
		inputManager.addListener(actionListenerChaseCam, "camPlus", "camMoins");


		planets = new ArrayList<>();

		//Soleil
		planets.add(new Planet(
			"Soleil", 2f, 0f,
			List.of(0f, 0f, 0f),
			List.of(0f, 0f, 0f),
			assetManager
		));

		//Mercure
		planets.add(new Planet(
			"Mercure", 0.4f, 10f,
			List.of(0f, 4.15f, 0f),
			List.of(0f, 0.0001f, 0f),
			assetManager
		));

		// Vénus
		planets.add(new Planet(
			"Venus", 0.95f, 18f,
			List.of(0f, 1.62f, 0f),
			List.of(0f, -0.00005f, 0f),
			assetManager
		));

		//Terre
		planets.add(new Planet(
			"Terre", 1.0f, 25f,
			List.of(0f, 1.0f, 0f),
			List.of(0f, 1.0f, 0f),
			assetManager
		));

		//Lune
		Planet lune = new Planet(
			"Lune", 0.27f, 3f,
			List.of(0f, 12.0f, 0f),
			List.of(0f, 0.1f, 0f),
			assetManager
		);
		planets.get(3).addMoon(lune);

		// Mars
		planets.add(new Planet(
			"Mars", 0.53f, 35f,
			List.of(0f, 0.53f, 0f),
			List.of(0f, 0.97f, 0f),
			assetManager
		));

		//Lunes de Mars Phobos Deimos
		Planet phobos = new Planet(
			"Lune", 0.05f, 2f,
			List.of(0f, 4.0f, 0f),
			List.of(0f, 0.2f, 0f),
			assetManager
		);
		planets.get(4).addMoon(phobos);
	
		Planet deimos = new Planet(
			"Lune", 0.04f, 4f,
			List.of(0f, 6.0f, 0f),
			List.of(0f, 0.2f, 0f),
			assetManager
		);
		planets.get(4).addMoon(deimos);

		//Jupiter
		planets.add(new Planet(
			"Jupiter", 11.2f, 60f,
			List.of(0f, 0.084f, 0f),
			List.of(0f, 2.5f, 0f),
			assetManager
		));

		//Lunes de Jupiter Io Europa
		Planet io = new Planet(
			"Lune", 0.3f, 4f,
			List.of(0f, 2.0f, 0f),  // Rotation autour de Jupiter
			List.of(0f, 0.1f, 0f),  // Rotation sur elle-même
			assetManager
		);
		planets.get(5).addMoon(io);
	
		Planet europa = new Planet(
			"Lune", 0.25f, 8f,
			List.of(0f, 3.0f, 0f),  // Rotation autour de Jupiter
			List.of(0f, 0.1f, 0f),  // Rotation sur elle-même
			assetManager
		);
		planets.get(5).addMoon(europa);

		//Saturne
		planets.add(new Planet(
			"Saturne", 9.45f, 80f,
			List.of(0f, 0.034f, 0f),
			List.of(0f, 2.3f, 0f),
			assetManager
		));

		//Uranus
		planets.add(new Planet(
			"Uranus", 4.0f, 100f,
			List.of(0f, 0.0119f, 0f),
			List.of(0f, 1.4f, 0f),
			assetManager
		));

		//Neptune
		planets.add(new Planet(
			"Neptune", 3.9f, 120f,
			List.of(0f, 0.006f, 0f),
			List.of(0f, 1.5f, 0f),
			assetManager
		));

		for(Planet p : planets){
			rootNode.attachChild(p.getRoot());
			p.getPlanet().rotate(-FastMath.HALF_PI, 0, 0);
		}

		gestionCam();
		timeLabel = new Label("Vue planete : " + planets.get(camPlanet).getName());
		timeLabel.setFontSize(20);
		timeLabel.setColor(ColorRGBA.White);
		timeLabel.setLocalTranslation(10, settings.getHeight() - 10, 0);
	
		guiNode.attachChild(timeLabel);
	}

	@Override
	public void simpleUpdate(float tpf){
		float time = tpf * timeV;

		for(Planet p : planets){
			p.rotate(time);
			p.rotateSelf(time);
			p.rotateMoon(time);
		}

		timeLabel.setText("Vue planete : " + planets.get(camPlanet).getName());
	}

	@Override
	public void start(){
		AppSettings settings = new AppSettings(true);
		settings.setWidth(1900); // Largeur de la fenêtre
		settings.setHeight(1000); // Hauteur de la fenêtre
		settings.setFullscreen(true); // Désactive le mode plein écran
		setSettings(settings);
		super.start();
	}

	// ActionListener pour gérer l'augmentation et la diminution de la variable
	private ActionListener actionListener = new ActionListener(){
		@Override
		public void onAction(String name, boolean isPressed, float tpf) {
			if (isPressed) {
				if (name.equals("Increase")) {
					timeV += 1;
				}
				if (name.equals("Decrease")) {
					timeV -= 1;
				}
			}
		}
	};

	private ActionListener actionListenerChaseCam = new ActionListener(){
		@Override
		public void onAction(String name, boolean isPressed, float tpf) {
			if (isPressed) {
				if (name.equals("camPlus")) {
					camPlanet += 1;
					if (camPlanet>8) {
						camPlanet -=9;
					}
					changeCam(camPlanet);
				}
				if (name.equals("camMoins")) {
					camPlanet -= 1;
					if (camPlanet<0) {
						camPlanet +=9;
					}
					changeCam(camPlanet);
				}
			}
		}
	};

	private void gestionCam(){
		// flyCam.setEnabled(false);
		// flyCam.setDragToRotate(true);
		// cam.setLocation(new Vector3f(0, 150, 0));
		// cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);

		flyCam.setEnabled(false);
		chaseCam = new ChaseCamera(cam, planets.get(camPlanet).getPlanet(), inputManager);
		chaseCam.setDefaultDistance(50f); // Distance initiale
		chaseCam.setMaxDistance(200f); // Distance max
		chaseCam.setMinDistance(10f); // Distance min
		chaseCam.setZoomSensitivity(2f); // Sensibilité zoom
		chaseCam.setRotationSpeed(3f); // Vitesse de rotation
		chaseCam.setLookAtOffset(new Vector3f(0, 5, 0));
	}

	private void changeCam(int camPlanet){
		chaseCam.setSpatial(planets.get(camPlanet).getPlanet());
	}

	private void initButton() {
		Quad quad = new Quad(200, 10); // Taille du bouton
		button = new Geometry("Button", quad);
		
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue); // Bouton bleu
		button.setMaterial(mat);
		
		button.setLocalTranslation(100, 100, 0); // Position en bas de l'écran
		guiNode.attachChild(button);
	}
}