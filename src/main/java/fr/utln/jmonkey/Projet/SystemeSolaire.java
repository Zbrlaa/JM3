package fr.utln.jmonkey.Projet;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jme3.app.SimpleApplication;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.SpringGridLayout;

import de.lessvoid.nifty.layout.manager.HorizontalLayout;


// @SuppressWarnings("unused")
public class SystemeSolaire extends SimpleApplication {
	//Ensemble de tout les corps de mon systeme solaire
	private List<Corp> planets;
	
	//Variable liées au temps
	private double timeV;
	private double refTime;
	private double antTime;
	private double cptTime;
	private double actualTime;
	SimpleDateFormat formatDate;

	//Gestion camera
	private ChaseCamera chaseCam;
	private int camPlanet;

	//Affichage texte
	private Label planetLabel;
	private Label tailleLabel;
	private Label masseLabel;
	private Label graviteLabel;
	private Label temperatureLabel;
	private Label timeLabel;
	private Label dateLabel;
	
	public static void main(String[] args){
		SystemeSolaire app = new SystemeSolaire();
		app.start();
	}
	
	@Override
	public void simpleInitApp(){
		//Initialisation des temps et de la date
		formatDate = new SimpleDateFormat("dd|MM|yyyy HH:mm:ss");
		refTime = System.currentTimeMillis();
		antTime = System.currentTimeMillis();
		camPlanet = 0;
		timeV = 1;

		//Ajout ciel en skybox avec 6 fois la meme image
		Texture skyTexture = assetManager.loadTexture("Planets/sky1.jpg");
		Spatial sky = SkyFactory.createSky(assetManager, skyTexture, skyTexture, skyTexture, skyTexture, skyTexture, skyTexture);
		rootNode.attachChild(sky);

		//Initialisation du champs de texte / affichage de la date
		GuiGlobals.initialize(this); // Initialisation Lemur
		dateLabel = new Label("Date");
		dateLabel.setFontSize(30);
		dateLabel.setColor(ColorRGBA.Red);
		dateLabel.setLocalTranslation(settings.getWidth()/2 - 100 , settings.getHeight() - 50, 0);
		guiNode.attachChild(dateLabel);

		//Lumiere Soleil
		PointLight soleilLight = new PointLight();
		soleilLight.setPosition(new Vector3f(0, 0, 0));
		soleilLight.setColor(ColorRGBA.White);
		soleilLight.setRadius(0f);
		rootNode.addLight(soleilLight);

		// Lumière d'ambiance faible pour voir les asteroides et planetes éloignées
		AmbientLight ambient = new AmbientLight();
		ambient.setColor(ColorRGBA.White.mult(0.04f));
		rootNode.addLight(ambient);

		//Creation de la liste de Corps et création soleil + planetes
		planets = new ArrayList<>();
		planets.add(new Etoile("Soleil", 6f, assetManager));
		String[] P = {"Mercure", "Venus", "Terre", "Mars", "Jupiter", "Saturne", "Uranus", "Neptune"};
		for(String s : P){
			planets.add(new Planet(s, assetManager));
		}
	
		//Création des anneaux de Saturne
		((Planet)planets.get(6)).addRings(assetManager, "Anneaux_Sat");

		//Creation des lunes
		//Lune de la Terre
		Lune lune = new Lune("Lune", 27.3217, 5.15f, 0.16f, 1.5d, 1737d, 7.342 * Math.pow(10, 22), 250, 1.62, 384400, 0.055,27.32, assetManager);
		planets.add(lune);
		((Planet)planets.get(3)).addlune(lune);

		//Lunes de Mars Phobos Deimos
		Lune phobos = new Lune("Phobos", 0.3189, 1.08f, 0.06f, 0.65d,11.3, 1.07 * Math.pow(10, 16), 233, 0.0057, 9377, 0.015, 0.32, assetManager);
		Lune deimos = new Lune("Deimos", 1.2624, 1.79f, 0.04f, 0.4d, 6.2, 1.48 * Math.pow(10, 15), 233, 0.003, 23460, 0.0002, 1.26, assetManager);
		planets.add(phobos);
		planets.add(deimos);
		((Planet)planets.get(4)).addlune(phobos);
		((Planet)planets.get(4)).addlune(deimos);

		//Lunes de Jupiter Io Europa
		Lune io = new Lune("Io", 1.7691, 2.21f, 0.3f, 10.5d,1821, 8.93 * Math.pow(10, 22), 130, 1.79, 421700, 0.004, 1.77, assetManager);
		Lune europa = new Lune("Europa", 3.5512, 1.79f, 0.25f, 14d,1561, 4.8 * Math.pow(10, 22),102, 1.31, 671100,0.009, 3.55, assetManager);
		planets.add(io);
		planets.add(europa);
		((Planet)planets.get(5)).addlune(io);
		((Planet)planets.get(5)).addlune(europa);
		
		//Attachement de toute les planetes au root
		for(Corp p : planets){
			if(!p.getClass().equals(Lune.class)){
				rootNode.attachChild(p.getRoot());
				p.getPlanet().rotate(-FastMath.HALF_PI, 0, 0);
			}
		}

		//Creation des asteroides selon 3 modeles
		String[] models = {
			"Asteroides/Eros.glb",
			"Asteroides/Itokawa.glb",
			"Asteroides/Vesta.glb"
		};

		Asteroide asteroideGenerator = new Asteroide(assetManager, models);
		asteroideGenerator.generateAsteroids(rootNode);

	
		//Ajout d'un effet de brillance
		FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
		viewPort.addProcessor(fpp);
		BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Scene);
		fpp.addFilter(bloom);

		//Initialisation des boutons et camera
		gestionCam();
		addBoutonTemps();
		addBoutonPlanetes();
		addBoutonReset();
	}
	
	@Override
	public void simpleUpdate(float tpf){
		//Calcul du temps écoulé et du temps "actuel" en fonction du facteur temps voulu
		double actualTime = System.currentTimeMillis();
		double passedTime = (actualTime-antTime)*timeV;
		cptTime += passedTime;
		antTime = actualTime;

		//Calcul de la date et changement de l'affichage
		double time = refTime+cptTime;
		Date date = new Date((long)(time));
		dateLabel.setText(formatDate.format(date));

		//Appel aux fonctions de mouvement
		for(Corp p : planets){
			if(p.getClass() == Planet.class){
				((Planet)p).rotate(time);
				((Planet)p).rotateLune(passedTime);
				((Planet)p).rotateSelf(passedTime);
			}
		}
	}
	
	@Override
	public void start(){
		//Enlever les stats hormis fps
		setDisplayStatView(false);
		setDisplayFps(true);

		//Gestion affichage
		AppSettings settings = new AppSettings(true);
		// settings.setWidth(1900); // Largeur de la fenêtre
		// settings.setHeight(1000); // Hauteur de la fenêtre
		settings.setFullscreen(true); // Désactive le mode plein écran
		settings.setCenterWindow(true);
		settings.setResolution(1920,1080);
		setSettings(settings);
		super.start();
	}
	
	//Initialisation de la camera
	private void gestionCam(){
		flyCam.setEnabled(false);
		cam.setFrustumFar(3000);
		chaseCam = new ChaseCamera(cam, planets.get(camPlanet).getPlanet(), inputManager);
		chaseCam.setHideCursorOnRotate(false);
		chaseCam.setInvertVerticalAxis(true);
		chaseCam.setZoomSensitivity(3f); // Sensibilité zoom
		chaseCam.setRotationSpeed(3f); // Vitesse de rotation
		chaseCam.setMinVerticalRotation(-FastMath.PI);
		changeCam(camPlanet);
		//chaseCam.setLookAtOffset(new Vector3f(0, 5, 0));
	}

	//Changement de la chasecam pour suivre la planete voulue
	private void changeCam(int camPlanet){
		float size = (float)(planets.get(camPlanet).size());
		chaseCam.setSpatial(planets.get(camPlanet).getPlanet());
		chaseCam.setDefaultDistance(size*25);
		if(planets.get(camPlanet).getName().equals("Deimos")){
			chaseCam.setDefaultDistance(size*40);//Car elle est toute petite
		}
		chaseCam.setMaxDistance(size*50);
		chaseCam.setMinDistance(size*5);
	}

	//Changement des infos planete affichées
	private void changeLabel(){
		Corp p = planets.get(Math.floorMod(camPlanet,planets.size()));
		planetLabel.setText(p.getName());
		tailleLabel.setText("Rayon : " + (int)p.getRayonMoyen() + " km");
		masseLabel.setText("Masse : " + String.format("%.3e",  p.getMasse()) + " kg");
		graviteLabel.setText("Gravité : " + p.getGravite() + " m/s2");
		temperatureLabel.setText("Temperature : " + p.getTemperatureK() + " K");
	}

	//Remettre la camera sur le soleil de loin
	private void resetCam(){
		camPlanet = 0;
		changeCam(camPlanet);
		changeLabel();

		chaseCam.setDefaultDistance(700);
		chaseCam.setMaxDistance(2000);
		chaseCam.setMinDistance(200);
	}

	//Ramener à l'instant présent et sans facteur temps
	private void resetTime(){
		refTime = System.currentTimeMillis();
		antTime = System.currentTimeMillis();
		cptTime = 0;
		timeV = 1;
		timeLabel.setText("x"+String.format("%.2f", timeV));
	}

	//Desactiver l'affichage des orbites
	private void hideOrbite() {
		for(Corp p : planets){
			if(p.getClass() == Planet.class){
				if(((Planet)p).getOrbite().getCullHint().equals(Spatial.CullHint.Always)){
					((Planet)p).getOrbite().setCullHint(Spatial.CullHint.Inherit);
				}
				else{
					((Planet)p).getOrbite().setCullHint(Spatial.CullHint.Always);
				}
			}
		}
	}
	
	//Ajout boutons pour gerer le temps et affichage du temps
	private void addBoutonTemps(){
		//Initialisation champ de texte
		Container container = new Container();

		timeLabel = new Label("x"+String.format("%.2f", timeV));
		timeLabel.setFontSize(20);
		timeLabel.setColor(ColorRGBA.White);

		//Fonction et bouton accelerer
		Button btnAccelerer = new Button(">>");
		btnAccelerer.addClickCommands(new Command<Button>() {
			@Override
			public void execute(Button source) {
				timeV *= 10f;
				timeLabel.setText("x"+String.format("%.2f", timeV));
			}
		});

		//Fonction et bouton ralentir
		Button btnRalentir = new Button("<<");
		btnRalentir.addClickCommands(new Command<Button>() {
			@Override
			public void execute(Button source) {
				timeV /= 10f;
				timeLabel.setText("x"+String.format("%.2f", timeV));
			}
		});

		//Gestion des conteneur et placement
		container.addChild(btnRalentir);
		container.addChild(timeLabel);
		container.addChild(btnAccelerer);
		guiNode.attachChild(container);
		container.setLocalTranslation(60, cam.getHeight() - 50, 0);
		container.setLayout(new SpringGridLayout(Axis.X, Axis.Y));
		btnRalentir.setLocalTranslation(0, 0, 0);
		timeLabel.setLocalTranslation(40, -10, 0);
		btnAccelerer.setLocalTranslation(0, -20, 0);
	}
	
	//Ajout boutons pour gerer la camera et affichage infos planete
	private void addBoutonPlanetes(){
		//Recuperation des info de la planete et initialisation des champs de texte
		Container container = new Container();
		Corp p = planets.get(Math.floorMod(camPlanet,planets.size()));

		planetLabel = new Label(p.getName());
		planetLabel.setFontSize(30);
		planetLabel.setColor(ColorRGBA.White);

		tailleLabel = new Label("Rayon : " + (int)p.getRayonMoyen() + " km");
		tailleLabel.setFontSize(20);
		tailleLabel.setColor(ColorRGBA.White);

		masseLabel = new Label("Masse : " + String.format("%.3e",  p.getMasse()) + " kg");
		masseLabel.setFontSize(20);
		masseLabel.setColor(ColorRGBA.White);

		graviteLabel = new Label("Gravité : " + p.getGravite() + " m/s2");
		graviteLabel.setFontSize(20);
		graviteLabel.setColor(ColorRGBA.White);

		temperatureLabel = new Label("Temperature : " + p.getTemperatureK() + " K");
		temperatureLabel.setFontSize(20);
		temperatureLabel.setColor(ColorRGBA.White);

		//Fonction et bouton planete suivante
		Button btnAccelerer = new Button("->");
		btnAccelerer.addClickCommands(new Command<Button>() {
			@Override
			public void execute(Button source) {
				camPlanet++;
				camPlanet = Math.floorMod(camPlanet,planets.size());//Pour modulo pas negatif
				changeCam(camPlanet);
				changeLabel();
			}
		});

		//Fonction et bouton planete précédente
		Button btnRalentir = new Button("<-");
		btnRalentir.addClickCommands(new Command<Button>() {
			@Override
			public void execute(Button source) {
				camPlanet--;
				camPlanet = Math.floorMod(camPlanet,planets.size());//Pour modulo pas negatif
				changeCam(camPlanet);
				changeLabel();
			}
		});

		//Gestion des conteneur et placement
		container.addChild(btnRalentir);
		container.addChild(planetLabel);
		container.addChild(btnAccelerer);
		container.addChild(tailleLabel);
		container.addChild(masseLabel);
		container.addChild(graviteLabel);
		container.addChild(temperatureLabel);

		guiNode.attachChild(container);
		container.setLocalTranslation(60, cam.getHeight() - 140, 0);
		container.setLayout(new SpringGridLayout(Axis.X, Axis.Y));
		btnRalentir.setLocalTranslation(0, 0, 0);
		planetLabel.setLocalTranslation(30, 10, 0);
		btnAccelerer.setLocalTranslation(160, 0, 0);

		tailleLabel.setLocalTranslation(0, -50, 0);
		masseLabel.setLocalTranslation(0, -90, 0);
		graviteLabel.setLocalTranslation(0, -130, 0);
		temperatureLabel.setLocalTranslation(0, -170, 0);
		
	}

	//Boutons pour inverser/reset le temps, reset la camera et cacher les orbites
	private void addBoutonReset(){
		Container container = new Container();

		//Bouton et fonction inversion
		Button btnInvertTime = new Button("Invert Time");
		btnInvertTime.addClickCommands(new Command<Button>() {
			@Override
			public void execute(Button source) {
				timeV *= -1;
				timeLabel.setText("x"+String.format("%.2f", timeV));
			}
		});

		//Bouton reset time avec appel à la dite fonction
		Button btnResetTime = new Button("Reset Time");
		btnResetTime.addClickCommands(new Command<Button>() {
			@Override
			public void execute(Button source) {
				resetTime();
			}
		});

		//Bouton reset cam avec appel à la dite fonction
		Button btnResetCam = new Button("Reset Cam");
		btnResetCam.addClickCommands(new Command<Button>() {
			@Override
			public void execute(Button source) {
				resetCam();
			}
		});

		//Bouton pour cacher les orbites avec appel à la dite fonction
		Button btnHideOrbite = new Button("Hide Orbite");
		btnHideOrbite.addClickCommands(new Command<Button>() {
			@Override
			public void execute(Button source) {
				hideOrbite();
			}
		});

		//Gestion des conteneur et placement
		container.addChild(btnInvertTime);
		container.addChild(btnResetTime);
		container.addChild(btnResetCam);
		container.addChild(btnHideOrbite);
		
		guiNode.attachChild(container);
		container.setLocalTranslation(60, cam.getHeight() - 600, 0);
	}
}