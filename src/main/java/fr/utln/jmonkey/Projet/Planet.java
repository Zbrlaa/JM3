package fr.utln.jmonkey.Projet;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

public class Planet implements Corp{
	//Obligatoire
	private String name;
	// private List<Float> anglesSelf;//Rotation sur elle-même

	//Créés ultérieurement
	private Geometry planet;
	private Node root;//Noyau pour Translation/Rotation
	private Node node;//Noyau pour ses lunes
	private List<Lune> lunes;

	//Remplis par l'Api
	private double demiGrandAxe;
	private double excentricite;
	private double periodeOrbitale;
	private double inclination;
	private double masse;
	private double rayonMoyen;
	private double gravite;
	private double inclinaisonAxiale;

	public static double RAYON_MOYEN_TERRE = 6371.0084;
	public static double DEMI_GRAND_AXE_TERRE = 149598023;
	
	//Constructeur
	public Planet(String name, AssetManager assetManager){
		this.name = name;

		if (Set.of("terre","mercure","venus","mars","jupiter","saturne","neptune","uranus").contains(name.toLowerCase())){
			Api api = new Api();
			JSONObject planetData = api.getPlanetData(name.toLowerCase());
			if (planetData != null) {
				demiGrandAxe = planetData.getDouble("demiGrandAxe");
				excentricite = planetData.getDouble("excentricite");
				periodeOrbitale = planetData.getDouble("periodeOrbitale");
				inclination = planetData.getDouble("inclination");
				masse = planetData.getDouble("masse");
				rayonMoyen = 0.5 * planetData.getDouble("rayonMoyen");
				gravite = planetData.getDouble("gravite");
				inclinaisonAxiale = planetData.getDouble("inclinaisonAxiale");
			} else {
				System.out.println("Erreur lors de la récupération des données.");
			}
		}

		initNodes();
		initPlanet(assetManager);
	}


	//Initialisations
	private void initPlanet(AssetManager assetManager){
		Sphere sphere = new Sphere(32, 32, (float)(rayonMoyen/(RAYON_MOYEN_TERRE)));
		planet = new Geometry(name, sphere);
		
		//Utilisation de la texture
		Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		mat.setTexture("DiffuseMap", assetManager.loadTexture("Planets/" + name + ".jpg"));

		planet.setMaterial(mat);
		node.attachChild(planet);
	}

	private void initNodes(){
		node = new Node(name + "Node");
		root = new Node(name + "Root");

		root.attachChild(node);
	}

	public void addlune(Lune lune){
		if (lunes == null) {
			lunes = new ArrayList<>();
		}
		this.lunes.add(lune);
		this.node.attachChild(lune.getNode());
	}


	public void rotate(double time){
		float e = (float)excentricite; // Excentricité de la Terre
		float a = (float)(30 * demiGrandAxe/DEMI_GRAND_AXE_TERRE); // Demi-grand axe
		float T = (float)periodeOrbitale; // Période orbitale
		T *= 24 * 3600;

		//Anomalie moyenne (M)
		float M = (float) (2 * Math.PI * (time% T) / T);
		//Anomalie vraie θ
		float theta = M + 2 * e * (float) Math.sin(M) + 1.25f * e * e * (float) Math.sin(2 * M);

		float x = a * (float) Math.cos(theta);
		float z = a * (float) Math.sqrt(1 - e * e) * (float) Math.sin(theta);

		Vector3f newPos = new Vector3f(x, 0f, z);
		node.setLocalTranslation(newPos);
	}

	public void rotateSelf(float tpf){
		// node.rotate(tpf*anglesSelf.get(0), tpf*anglesSelf.get(1), tpf*anglesSelf.get(2));
	}

	public void rotatelune(float time){
		if (lunes != null){
			for(Lune l : lunes){
				l.rotate(time);
				// m.rotateSelf(tpf);
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


	public List<Lune> getlunes(){
		return lunes;
	}


	public double size(){
		return rayonMoyen/RAYON_MOYEN_TERRE;
	}

	public double getDemiGrandAxe(){
		return demiGrandAxe;
	}


	public double getExcentricite() {
		return excentricite;
	}


	public double getPeriodeOrbitale() {
		return periodeOrbitale;
	}


	public double getInclination() {
		return inclination;
	}


	public double getMasse() {
		return masse;
	}


	public double getRayonMoyen() {
		return rayonMoyen;
	}


	public double getGravite() {
		return gravite;
	}


	public double getInclinaisonAxiale() {
		return inclinaisonAxiale;
	}
}