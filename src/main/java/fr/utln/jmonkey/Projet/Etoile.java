package fr.utln.jmonkey.Projet;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

//Unpeu comme la classe planete mais utilisée seulement pour creer le soleil (seule étoile)
public class Etoile implements Corp{
	//Info rentrée à la main et non par l'api
	//N'a besoin que de très peu de methode
	private String name;
	private Geometry planet;
	private Node root;
	private Node node;
	private float radius;
	private double rayonMoyen;
	private double masse;
	private double gravite;
	private double temperatureK;

	public Etoile(String name, float radius, AssetManager assetManager){
		this.name = name;
		this.radius = radius;
		this.rayonMoyen = 696340;
		this.masse = 1.989 * Math.pow(10, 30);
		this.temperatureK = 5778;
		this.gravite = 274;
	
		initNodes();
		initPlanet(assetManager);
	}

	//Initialisations
	private void initPlanet(AssetManager assetManager){
		Sphere sphere = new Sphere(32, 32, radius);
		sphere.setTextureMode(Sphere.TextureMode.Projected);
		planet = new Geometry(name, sphere);
		
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setTexture("ColorMap", assetManager.loadTexture("Planets/" + name + ".jpg"));

		planet.setMaterial(mat);
		node.attachChild(planet);
	}

	private void initNodes(){
		node = new Node(name + "Node");
		root = new Node(name + "Root");

		root.attachChild(node);
	}

	public String getName(){
		return name;
	}

	public Geometry getPlanet(){
		return planet;
	}

	public Node getRoot(){
		return root;
	}

	public Node getNode(){
		return node;
	}

	public double size(){
		return (double)radius;
	}
	
	public double getRayonMoyen() {
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