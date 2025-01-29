package fr.utln.jmonkey.tutorials.beginner;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.jme3.material.Material;
import com.jme3.texture.Texture;


/** Petit test attachement objet random.
 * Ciel + voiture inshallah */
public class HelloShawn extends SimpleApplication{

	private Spatial bateau;
	private Node sinj;
	private Spatial jaime;
	private Spatial jaime2;
	private float time=0;
	public static void main(String[] args) {
		HelloShawn app = new HelloShawn();
		app.start();
	}

	public HelloShawn(){
	}

	@Override
	public void simpleInitApp(){
		DirectionalLight sun = new DirectionalLight();
		sun.setDirection(new Vector3f(-1, -1, -1).normalizeLocal());
		rootNode.addLight(sun);

		Texture west = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_west.jpg");
		Texture east = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_east.jpg");
		Texture north = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_north.jpg");
		Texture south = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_south.jpg");
		Texture up = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_up.jpg");
		Texture down = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_down.jpg");

		Spatial sky = SkyFactory.createSky(assetManager, west, east, north, south, up, down);
		rootNode.attachChild(sky);

		bateau = assetManager.loadModel("Models/Boat/boat.j3o");
		bateau.setLocalTranslation(0, 0, 0); // Position initiale
		rootNode.attachChild(bateau); // Ajouter le bateau à la scène

	// Assigner les touches pour le mouvement
	// inputManager.addMapping("Avancer", new KeyTrigger(KeyInput.KEY_W));  // ZQSD ou WASD
	// inputManager.addMapping("Reculer", new KeyTrigger(KeyInput.KEY_S));
	// inputManager.addMapping("Gauche", new KeyTrigger(KeyInput.KEY_A));
	// inputManager.addMapping("Droite", new KeyTrigger(KeyInput.KEY_D));

	// Associer les actions aux touches
	//inputManager.addListener(analogListener, "Avancer", "Reculer", "Gauche", "Droite");



		sinj = new Node("Sinj");

		jaime = assetManager.loadModel("Models/Sinbad/SinbadOldAnim.j3o");
		jaime.setLocalTranslation(0, 0, 0);
		jaime.scale(2f);
		sinj.attachChild(jaime);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setTexture("ColorMap", assetManager.loadTexture("Models/Sinbad/sinbad_body.jpg"));
		jaime.setMaterial(mat);

		jaime2 = assetManager.loadModel("Models/Jaime/Jaime.j3o");
		jaime2.setLocalTranslation(0, 0, 0);
		jaime2.scale(2f);
		rootNode.attachChild(jaime2);

		Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat2.setTexture("ColorMap", assetManager.loadTexture("Models/Jaime/diffuseMap.jpg"));
		jaime2.setMaterial(mat2);

		ParticleEmitter fire = new ParticleEmitter("Emitter", ParticleMesh.Type.Triangle, 30);
		Material mat_red = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
		mat_red.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/flame.png"));
		fire.setMaterial(mat_red);
		fire.setImagesX(2);
		fire.setImagesY(2); // 2x2 texture animation
		fire.setEndColor(  new ColorRGBA(1f, 0f, 0f, 1f));
		fire.setStartColor(new ColorRGBA(1f, 1f, 0f, 0.5f));
		fire.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2, 0));
		fire.setStartSize(1.5f);
		fire.setEndSize(0.1f);
		fire.setGravity(0, 0, 0);
		fire.setLowLife(1f);
		fire.setHighLife(3f);
		fire.getParticleInfluencer().setVelocityVariation(0.3f);
		sinj.attachChild(fire);

		rootNode.attachChild(sinj);

		bateau.setLocalTranslation(0, -5, 0);
	}

	@Override
	public void simpleUpdate(float tpf) {
		time += tpf;
		jaime2.move(0, 0, -tpf);
		jaime2.rotate(0,0,tpf);
		sinj.move(0, 0, FastMath.cos(time*FastMath.PI)/6);
		sinj.rotate(0,0,-tpf);
	}

	@Override
	public void start() {
		AppSettings settings = new AppSettings(true);
		settings.setWidth(1920); // Largeur de la fenêtre
		settings.setHeight(1080); // Hauteur de la fenêtre
		settings.setFullscreen(false); // Désactive le mode plein écran
		setSettings(settings);
		super.start();
	}
}