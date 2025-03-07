package fr.utln.jmonkey.Projet;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

public class Lune implements Corp{
	private String name;
	private double revolution;
	private float inclinaisonOrbitale;
	private float size;
	private double rayonMoyen;
	private double distance;
	private double masse;
	private double gravite;
	private double temperatureK;

	private Geometry planet;
	private Node root;
	private Node node;

	public Lune(String name, double revolution, float inclinaisonOrbitale, float size, double distance, double rayonMoyen, double masse, double temperatureK, double gravite, AssetManager assetManager){
		this.name = name;
		this.revolution = revolution;;
		this.inclinaisonOrbitale = inclinaisonOrbitale;
		this.size = size;
		this.distance = distance;

		this.temperatureK=temperatureK;
		this.gravite=gravite;
		this.masse=masse;
		this.rayonMoyen=rayonMoyen;
		
		initNodes();
		initPlanet(assetManager);
	}

	private void initPlanet(AssetManager assetManager){
		Sphere sphere = new Sphere(32, 32, size);
		sphere.setTextureMode(Sphere.TextureMode.Projected);
		planet = new Geometry(name, sphere);
		
		//Utilisation de la texture
		Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		mat.setTexture("DiffuseMap", assetManager.loadTexture("Planets/"+ name +".jpg"));

		planet.setMaterial(mat);
		node.attachChild(planet);
	}

	private void initNodes(){
		node = new Node(name + "Node");
		root = new Node(name + "Root");

		root.attachChild(node);
		node.setLocalTranslation((float)distance, 0, 0);

		float inclinationRadians = FastMath.DEG_TO_RAD * inclinaisonOrbitale;
		// System.out.println(name + " " + inclinaisonOrbitale + " " + inclinationRadians);
		node.rotate(-FastMath.HALF_PI, 0, 0);
		root.rotate(inclinationRadians, 0f, 0f);
	}

	public void rotate(double time){
		float angle = FastMath.DEG_TO_RAD * (float)time * 360f / ((float)revolution*24f*3600f*1000f);
		root.rotate(0f, angle, 0f);
		// System.out.println("Rotate " + name + " " + angle);
	}

	public String getName(){
		return name;
	}

	public Node getNode(){
		return node;
	}

	public Node getRoot(){
		return root;
	}

	@Override
	public Geometry getPlanet() {
		return planet;
	}

	@Override
	public double size(){
		return size;
	}

	public double getRayonMoyen(){
		return rayonMoyen;
	}

	public double getMasse() {
		return masse;
	}

	public double getGravite() {
		return gravite;
	}

	public double getTemperatureK() {
		return temperatureK;
	}
}
