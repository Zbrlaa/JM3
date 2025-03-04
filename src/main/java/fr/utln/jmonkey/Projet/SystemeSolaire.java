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


@SuppressWarnings("unused")
public class SystemeSolaire extends SimpleApplication {
	private List<Corp> planets;
	
	private int timeV;
	private double refTime;
	private double antTime;
	private double cptTime;
	private double actualTime;

	private int camPlanet;
	private Label planetLabel;
	private Label timeLabel;
	private Label dateLabel;
	private ChaseCamera chaseCam;

	private Geometry button;
	SimpleDateFormat formatDate;

	public static void main(String[] args){
		SystemeSolaire app = new SystemeSolaire();
		app.start();
	}

	@Override
	public void simpleInitApp(){
		formatDate = new SimpleDateFormat("dd|MM|yyyy HH:mm:ss");
		refTime = System.currentTimeMillis();
		antTime = System.currentTimeMillis();
		camPlanet = 0;
		timeV = 1;

		//Ciel
		Texture skyTexture = assetManager.loadTexture("Planets/sky1.jpg");
		Spatial sky = SkyFactory.createSky(assetManager, skyTexture, skyTexture, skyTexture, skyTexture, skyTexture, skyTexture);
		rootNode.attachChild(sky);

		//Date
		GuiGlobals.initialize(this); // Initialisation Lemur
		dateLabel = new Label("Date");
		dateLabel.setFontSize(30);
		dateLabel.setColor(ColorRGBA.Red);
		dateLabel.setLocalTranslation(settings.getWidth()/2 - 100 , settings.getHeight() - 50, 0);
		guiNode.attachChild(dateLabel);

		//Lumiere Soleil
		PointLight soleilLight = new PointLight();
		soleilLight.setPosition(new Vector3f(0, 0, 0));
		soleilLight.setColor(ColorRGBA.White.mult(10f));
		soleilLight.setRadius(2000f);
		rootNode.addLight(soleilLight);

		// Lumière d'ambiance faible
		AmbientLight ambient = new AmbientLight();
		ambient.setColor(ColorRGBA.White.mult(0.04f));
		rootNode.addLight(ambient);

		
		planets = new ArrayList<>();
		planets.add(new Etoile("Soleil", 6f, assetManager));
		planets.add(new Planet("Mercure", assetManager));
		planets.add(new Planet("Venus", assetManager));
		planets.add(new Planet("Terre", assetManager));
		((Planet)planets.get(3)).addlune(new Lune("Lune", 27.3217, 5.15f, 1737.4f, 384400d, assetManager));
		planets.add(new Planet("Mars", assetManager));
		planets.add(new Planet("Jupiter", assetManager));
		planets.add(new Planet("Saturne", assetManager));
		planets.add(new Planet("Uranus", assetManager));
		planets.add(new Planet("Neptune", assetManager));



		//Lunes de Mars Phobos Deimos
		//Lunes de Jupiter Io Europa

		for(Corp p : planets){
			rootNode.attachChild(p.getRoot());
			p.getPlanet().rotate(-FastMath.HALF_PI, 0, 0);
		}

		gestionCam();
	
		// // Création du PostProcessor
		// FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
		// viewPort.addProcessor(fpp);

		// // Ajout d'un effet de Glow (brillance)
		// BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Scene);
		// fpp.addFilter(bloom);

		addBoutonTemps();
		addBoutonPlanetes();
		addBoutonReset();
	}

	@Override
	public void simpleUpdate(float tpf){
		double actualTime = System.currentTimeMillis();
		cptTime += (actualTime-antTime)*timeV;
		antTime = actualTime;

		double time = refTime+cptTime;
		Date date = new Date((long)(time));
		dateLabel.setText(formatDate.format(date));

		for(Corp p : planets){
			if (!p.getName().equals("Soleil")){
				((Planet)p).rotate(time);
				// p.rotateSelf(time);
				// p.rotateMoon(time);
			}
		}
	}

	@Override
	public void start(){
		// setShowSettings(true);

		setDisplayStatView(false);
		setDisplayFps(true);

		AppSettings settings = new AppSettings(true);
		// settings.setWidth(1900); // Largeur de la fenêtre
		// settings.setHeight(1000); // Hauteur de la fenêtre
		settings.setFullscreen(true); // Désactive le mode plein écran
		settings.setCenterWindow(true);
		settings.setResolution(1920,1080);
		setSettings(settings);
		super.start();
	}

	private void gestionCam(){
		flyCam.setEnabled(false);
		chaseCam = new ChaseCamera(cam, planets.get(camPlanet).getPlanet(), inputManager);
		chaseCam.setHideCursorOnRotate(false);
		chaseCam.setInvertVerticalAxis(true);
		chaseCam.setZoomSensitivity(2f); // Sensibilité zoom
		chaseCam.setRotationSpeed(3f); // Vitesse de rotation
		
		changeCam(camPlanet);
		//chaseCam.setLookAtOffset(new Vector3f(0, 5, 0));
	}

	private void changeCam(int camPlanet){
		float size = (float)(planets.get(camPlanet).size());
		chaseCam.setSpatial(planets.get(camPlanet).getPlanet());
		chaseCam.setDefaultDistance(size*10);
		chaseCam.setMaxDistance(size*50);
		chaseCam.setMinDistance(size*5);
	}


	private void resetCam(){
		camPlanet = 0;
		changeCam(camPlanet);
		planetLabel.setText(planets.get(Math.floorMod(camPlanet,planets.size())).getName());

		chaseCam.setDefaultDistance(700);
		chaseCam.setMaxDistance(2000);
		chaseCam.setMinDistance(200);
	}

	private void resetTime(){
		refTime = System.currentTimeMillis();
		antTime = System.currentTimeMillis();
		cptTime = 0;
		timeV = 1;
		timeLabel.setText("x"+(int)timeV);

		// for(Planet p : planets){
		// 	p.rotate(time);
		// 	p.rotateSelf(time);
		// 	p.rotateMoon(time);
		// }
	}

	
	private void addBoutonTemps(){
		Container container = new Container();

		timeLabel = new Label("x"+(int)timeV);
		timeLabel.setFontSize(20);
		timeLabel.setColor(ColorRGBA.White);

		Button btnAccelerer = new Button("->");
		btnAccelerer.addClickCommands(new Command<Button>() {
			@Override
			public void execute(Button source) {
				if(timeV == -1){
					timeV = 1;
				}
				else if(timeV < -1){
					timeV /= 10;
				}
				else{
					timeV *= 10;
				}
				timeLabel.setText("x"+(int)timeV);
			}
		});

		Button btnRalentir = new Button("<-");
		btnRalentir.addClickCommands(new Command<Button>() {
			@Override
			public void execute(Button source) {
				if(timeV == 1){
					timeV = -1;
				}
				else if(timeV > 1){
					timeV /= 10;
				}
				else{
					timeV *= 10;
				}
				timeLabel.setText("x"+(int)timeV);
			}
		});

		
		container.addChild(btnRalentir);
		container.addChild(timeLabel);
		container.addChild(btnAccelerer);
		guiNode.attachChild(container);
		container.setLocalTranslation(60, cam.getHeight() - 100, 0);
		container.setLayout(new SpringGridLayout(Axis.X, Axis.Y));
		btnRalentir.setLocalTranslation(0, 0, 0);
		timeLabel.setLocalTranslation(30, 0, 0);
		btnAccelerer.setLocalTranslation(80, 0, 0);
	}


	private void addBoutonPlanetes(){
		Container container = new Container();

		planetLabel = new Label(planets.get(Math.floorMod(camPlanet,planets.size())).getName());
		planetLabel.setFontSize(20);
		planetLabel.setColor(ColorRGBA.White);

		Button btnAccelerer = new Button("->");
		btnAccelerer.addClickCommands(new Command<Button>() {
			@Override
			public void execute(Button source) {
				camPlanet++;
				camPlanet = Math.floorMod(camPlanet,planets.size());//Pour modulo pas negatif
				changeCam(camPlanet);
				planetLabel.setText(planets.get(Math.floorMod(camPlanet,planets.size())).getName());
			}
		});

		Button btnRalentir = new Button("<-");
		btnRalentir.addClickCommands(new Command<Button>() {
			@Override
			public void execute(Button source) {
				camPlanet--;
				camPlanet = Math.floorMod(camPlanet,planets.size());//Pour modulo pas negatif
				changeCam(camPlanet);
				planetLabel.setText(planets.get(Math.floorMod(camPlanet,planets.size())).getName());
			}
		});

		container.addChild(btnRalentir);
		container.addChild(planetLabel);
		container.addChild(btnAccelerer);
		guiNode.attachChild(container);
		container.setLocalTranslation(60, cam.getHeight() - 140, 0);
		container.setLayout(new SpringGridLayout(Axis.X, Axis.Y));
		btnRalentir.setLocalTranslation(0, 0, 0);
		planetLabel.setLocalTranslation(30, 0, 0);
		btnAccelerer.setLocalTranslation(120, 0, 0);
		// container.setBorder(null);
	}


	private void addBoutonReset(){
		Container container = new Container();

		Button btnResetTime = new Button("Reset Time");
		btnResetTime.addClickCommands(new Command<Button>() {
			@Override
			public void execute(Button source) {
				resetTime();
			}
		});

		Button btnResetCam = new Button("Reset Cam");
		btnResetCam.addClickCommands(new Command<Button>() {
			@Override
			public void execute(Button source) {
				resetCam();
			}
		});

		container.addChild(btnResetTime);
		container.addChild(btnResetCam);
		guiNode.attachChild(container);
		container.setLocalTranslation(60, cam.getHeight() - 180, 0);
	}
}