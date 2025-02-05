package fr.utln.jmonkey.tutorials.Projet;

import java.util.ArrayList;
import java.util.List;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

public class Planet {
	//A modif pour ajouter les models et textures

	//Obligatoire
	private String name;
	//private ColorRGBA color;
	private float size;
	private float distance;
	private List<Float> angles;//Rotation autour du root
	private List<Float> anglesSelf;//Rotation sur elle-même

	//Créés ultérieurement
	private Geometry planet;
	private Node root;//Noyau pour Translation/Rotation
	private Node node;//Noyau pour ses lunes
	private List<Planet> moons;

	
	//Constructeur
	public Planet(String name, float size, float distance, List<Float> angles, List<Float> anglesSelf,AssetManager assetManager){
		this.name = name;
		this.size = size;
		this.distance = distance;
		this.angles = angles;
		this.anglesSelf = anglesSelf;

		initNodes();
		initPlanet(assetManager);
	}


	//Initialisations
	private void initPlanet(AssetManager assetManager){
		Sphere sphere = new Sphere(32, 32, size);
		planet = new Geometry(name, sphere);
		
		//Utilisation de la texture
		String m;
		String mt;
		if(name.equals("Soleil")){
			m = "Misc/Unshaded";
			mt = "ColorMap";
		}
		else{
			m = "Light/Lighting";
			mt = "DiffuseMap";
		}

		Material mat = new Material(assetManager, "Common/MatDefs/" + m + ".j3md");
		mat.setTexture(mt, assetManager.loadTexture("Planets/" + name + ".jpg"));

		planet.setMaterial(mat);
		node.attachChild(planet);
	}

	private void initNodes(){
		node = new Node(name + "Node");
		root = new Node(name + "Root");

		root.attachChild(node);
		node.setLocalTranslation(distance, 0, 0);
	}

	public void addMoon(Planet moon){
		if (moons == null) {
			moons = new ArrayList<>();
		}
		this.moons.add(moon);
		this.node.attachChild(moon.getNode());
	}


	public void rotate(float tpf){
		root.rotate(tpf*angles.get(0), tpf*angles.get(1), tpf*angles.get(2));
	}

	public void rotateSelf(float tpf){
		node.rotate(tpf*anglesSelf.get(0), tpf*anglesSelf.get(1), tpf*anglesSelf.get(2));
	}

	public void rotateMoon(float tpf){
		if (moons != null){
			for(Planet m : moons){
				m.rotate(tpf);
				m.rotateSelf(tpf);
			}
		}
	}


	//Getters & Setters
	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}


	public float getSize(){
		return size;
	}

	public void setSize(float size){
		this.size = size;
	}


	public float getDistance(){
		return distance;
	}

	public void setDistance(float distance){
		this.distance = distance;
	}


	public List<Float> getAngles(){
		return angles;
	}

	public void setAngles(List<Float> angles){
		this.angles = angles;
	}

	public void setAngles(float x, float y, float z){
		setAngles(new ArrayList<>(List.of(x, y, z)));   
	}


	public List<Float> getAnglesSelf(){
		return anglesSelf;
	}

	public void setAnglesSelf(List<Float> anglesSelf){
		this.angles = anglesSelf;
	}

	public void setAnglesSelf(float x, float y, float z){
		setAnglesSelf(new ArrayList<>(List.of(x, y, z)));   
	}


	public Geometry getPlanet(){
		return planet;
	}

	public void setPlanet(Geometry planet){
		this.planet = planet;
	}


	public Node getRoot(){
		return root;
	}

	public void setRoot(Node root){
		this.root = root;
	}


	public Node getNode(){
		return node;
	}

	public void setNode(Node node){
		this.node = node;
	}


	public List<Planet> getMoons(){
		return moons;
	}

	public void setMoons(List<Planet> moons){
		this.moons = moons;
	}
}