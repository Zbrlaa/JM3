package fr.utln.jmonkey.Projet;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;

public class Planet implements Corp{
	private String name;

	//Créés à l'init
	private Geometry planet;
	private Geometry orbite;
	private Node root;//Noyau autour du quel elle tourne
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
	private double temperatureK;
	private double inclinaisonAxiale;
	private double densite;
	private double rotation;

	//Constante pour echelonner
	public static double RAYON_MOYEN_TERRE = 6371.0084;
	public static double DEMI_GRAND_AXE_TERRE = 149598023;
	
	//Constructeur
	public Planet(String name, AssetManager assetManager){
		this.name = name;

		//Appel à l'api pour recup les infos des planetes
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
				densite = planetData.getDouble("densite");
				temperatureK = planetData.getDouble("temperatureK");
				rotation = planetData.getDouble("rotation");
			
			} else {
				System.out.println("Erreur lors de la récupération des données.");
			}
		}

		//Appel fonctions initialisation
		initNodes();
		initPlanet(assetManager);
		initOrbite(assetManager);
	}


	//Initialisations
	private void initPlanet(AssetManager assetManager){
		//Creation de la forme
		float r = (float)(rayonMoyen/(RAYON_MOYEN_TERRE));
		System.out.println(name + " " + r);
		Sphere sphere = new Sphere(32, 32, r);
		sphere.setTextureMode(Sphere.TextureMode.Projected);
		planet = new Geometry(name, sphere);
		
		//Ajout texture
		Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		mat.setTexture("DiffuseMap", assetManager.loadTexture("Planets/" + name + ".jpg"));
		planet.setMaterial(mat);
		node.attachChild(planet);
	}

	private void initNodes(){
		//Creation et rotation des noeuds
		node = new Node(name + "Node");
		root = new Node(name + "Root");

		root.attachChild(node);
		root.rotate(FastMath.DEG_TO_RAD*(float)inclination,0f,0f);
		node.rotate(FastMath.DEG_TO_RAD*(float)inclinaisonAxiale,0f,0f);
	}

	//Ajout et attachement des lunes
	public void addlune(Lune lune){
		if (lunes == null) {
			lunes = new ArrayList<>();
		}
		this.lunes.add(lune);
		this.node.attachChild(lune.getRoot());
	}

	//Mouvement elliptique
	public void rotate(double time){
		float e = (float)excentricite;
		float a = (float)(30 * demiGrandAxe/DEMI_GRAND_AXE_TERRE);
		float T = (float)periodeOrbitale;
		T *= 24 * 3600 * 1000;

		//Anomalies
		float M = (float) (2 * Math.PI * (time% T) / T);
		float theta = M + 2 * e * (float) Math.sin(M) + 1.25f * e * e * (float) Math.sin(2 * M);
		//Calcul coordonnées pour translation
		float x = a * (float) Math.cos(-theta) - e*a;
		float z = a * (float) Math.sqrt(1 - e * e) * (float) Math.sin(-theta);
		Vector3f newPos = new Vector3f(x, 0f, z);
		node.setLocalTranslation(newPos);
		// System.out.println(name+" "+x+" "+z);
	}

	//Rotation sur elle-meme
	public void rotateSelf(double time){
		//Calcul avec temps de rotation en jour puis rotate
		float angle = FastMath.DEG_TO_RAD * (float)time * 360f / ((float)rotation*3600f*1000);
		planet.rotate(0f, 0f, angle);
		// System.out.println("Rotate " + name + " " + angle);
	}

	//Mouvement des lunes
	public void rotateLune(double time){
		//Appel a leur propre fonction de deplacement
		if (lunes != null){
			for(Lune l : lunes){
				l.rotate(time);
			}
		}
	}

	//Creation des orbites avec un mesh selon mouvement elliptique
	public void initOrbite(AssetManager assetManager){
		int samples = 256;
		float a = (float)(30 * demiGrandAxe/DEMI_GRAND_AXE_TERRE);
		// System.out.println(name + " orbite " + demiGrandAxe + " " + a);
		float demiPetitAxe = a * (float) Math.sqrt(1 - Math.pow(excentricite, 2d));

		Mesh mesh = new Mesh();
		Vector3f[] vertices = new Vector3f[samples + 1]; // +1 pour fermer l'ellipse
		int[] indices = new int[samples * 2];

		for (int i = 0; i < samples; i++) {
			float angle = (float) (2 * Math.PI * i / samples);
			float x = (float) a * (float) Math.cos(angle) - (float)(excentricite)*a; // Correction du facteur d'échelle
			float y = demiPetitAxe * (float) Math.sin(angle);

			vertices[i] = new Vector3f(x, y, 0);
			indices[i * 2] = i;
			indices[i * 2 + 1] = (i + 1) % samples; // Relie les points
		}
		vertices[samples] = vertices[0]; 

		mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
		mesh.setBuffer(VertexBuffer.Type.Index, 2, BufferUtils.createIntBuffer(indices));
		mesh.setMode(Mesh.Mode.Lines); // Mode "Lines" pour dessiner une ellipse
		mesh.updateBound();
		mesh.updateCounts();

		orbite = new Geometry("Ellipse"+name, mesh);
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setTexture("ColorMap", assetManager.loadTexture("Planets/" + name + ".jpg"));
		orbite.setMaterial(mat);
		orbite.rotate(-FastMath.HALF_PI, 0, 0);
		root.attachChild(orbite);
	}

	//Ajout des anneaux avec un mesh
	public void addRings(AssetManager assetManager, String nomTexture) {
		Mesh ringMesh = new Mesh();
		int segments = 512;
		Vector3f[] sommets = new Vector3f[segments*2];
		Vector2f[] coords = new Vector2f[segments*2];

		int[] indices = new int[segments * 6];

		float rayonInterne =(float)(rayonMoyen/RAYON_MOYEN_TERRE) + 2f;
		float rayonExterne = (float)(rayonMoyen/RAYON_MOYEN_TERRE) + 10f;

		for (int i = 0; i < segments; i++) {
			float angle = (float) (i*FastMath.PI*2/segments);
			float cos = (float) FastMath.cos(angle);
			float sin = (float) FastMath.sin(angle);

			sommets[i*2] = new Vector3f(rayonInterne*cos,0,rayonInterne*sin);
			sommets[i*2+1] = new Vector3f(rayonExterne*cos,0,rayonExterne*sin);

			coords[i*2] = new Vector2f(0,i/(float) segments);
			coords[i*2+1] = new Vector2f(1,i/(float) segments);

			int next = (i + 1) % segments;
			
			indices[i * 6] = i * 2;
			indices[i * 6 + 1] = next * 2;
			indices[i * 6 + 2] = i * 2 + 1;

			indices[i * 6 + 3] = i * 2 + 1;
			indices[i * 6 + 4] = next * 2;
			indices[i * 6 + 5] = next * 2 + 1;
		}

		ringMesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(sommets));
		ringMesh.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(coords));
		ringMesh.setBuffer(VertexBuffer.Type.Index, 3, BufferUtils.createIntBuffer(indices));
		ringMesh.updateBound();

		Geometry ringGeo = new Geometry("Saturn Rings", ringMesh);
		Material ringMat = new Material(assetManager,
		"Common/MatDefs/Misc/Unshaded.j3md");
		Texture ringTex = assetManager.loadTexture("Planets/"+nomTexture+".png");
		ringTex.setWrap(Texture.WrapMode.EdgeClamp);

		ringMat.setTexture("ColorMap", ringTex);
		ringMat.getAdditionalRenderState().setBlendMode(com.jme3.material.RenderState.BlendMode.Alpha);
		ringMat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
		ringGeo.setMaterial(ringMat);
		ringGeo.setQueueBucket(RenderQueue.Bucket.Transparent);

		Node anneaux = new Node("anneaux");
		anneaux.attachChild(ringGeo);
		node.attachChild(anneaux);
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

	public Geometry getOrbite() {
		return orbite;
	}

	public double getDensite() {
		return densite;
	}

	public double getTemperatureK() {
		return temperatureK;
	}
}