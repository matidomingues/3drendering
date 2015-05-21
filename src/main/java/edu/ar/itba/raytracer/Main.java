package edu.ar.itba.raytracer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;

import edu.ar.itba.raytracer.light.DirectionalLight;
import edu.ar.itba.raytracer.light.LightProperties;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.properties.Transform;
import edu.ar.itba.raytracer.shape.Mesh;
import edu.ar.itba.raytracer.shape.Sphere;
import edu.ar.itba.raytracer.shape.Triangle;
import edu.ar.itba.raytracer.texture.ConstantColorTexture;
import edu.ar.itba.raytracer.texture.Texture;
import edu.ar.itba.raytracer.vector.Vector4;

public class Main {

	@Parameters
	public static class RayTracerParameters {
		@Parameter(names = "-i", required = true, description = "Nombre del archivo de entrada (definición de la escena)")
		private String input;
		@Parameter(names = "-o", description = "Nombre del archivo de salida, incluyendo su extensión. "
				+ "En caso de no indicarlo usará el nombre del archivo de input reemplazando la extensión "
				+ "y usando el formato PNG.")
		private String output = input;
		@Parameter(names = "-time", description = "Mostrará el tiempo empleado en el render")
		private boolean time = false;
		@Parameter(names = "-aa", required = true, description = "Cantidad de muestras de antialiasing.")
		private int aaSamples;
		@Parameter(names = "-benchmark", description = "Realizar el render completo n veces consecutivas.")
		private int benchmark = 1;
		@Parameter(names = "-d", description = "Define el ray depth de reflejos y refracciones.")
		private int rayDepth = 1;
	}

	public static void main(String[] args) throws Exception {
		final RayTracerParameters parameters = new RayTracerParameters();
		final JCommander jCommander = new JCommander(parameters);
		jCommander.setProgramName("Ray tracer");
		try {
			jCommander.parse(args);
		} catch (final ParameterException e) {
			jCommander.usage();
			throw e;
		}

		if (parameters.output == null) {
			final String input = parameters.input;
			final int extension = input.lastIndexOf('.');
			if (extension == -1) {
				parameters.output = input + ".png";
			} else {
				parameters.output = input.substring(0, extension) + ".png";
			}
		}

		System.out.println(parameters.output);

		Scanner s = new Scanner(System.in);
		final int height = 480;
		final int width = 640;
		BufferedImage image = null;
		// Thread.sleep(10000);
		Camera c = loadTestScene();
		// while (true) {
		for (int i = 0; i < parameters.benchmark; i++) {
			image = c.render(width, height);
		}
		// try {
		// String l = s.nextLine();
		// x = Integer.parseInt(l);
		// l = s.nextLine();
		// y = Integer.parseInt(l);
		// } catch (Exception e) {
		//
		// }
		ImageIO.write(image, "png", new File("pic.png"));
		// }

	}

	public static int x;
	public static int y;

	private static Mesh parseBunny() throws Exception {
		final Scanner scanner = new Scanner(
				Paths.get("C:\\Program Files\\Eclipse\\workspace\\cg-2015-05\\bunny.obj"));
		final List<Vector4> vertexes = new ArrayList<>();
		final List<Vector4> vertexNormals = new ArrayList<>();
		final Collection<List<Integer>> normalMap = new ArrayList<>();
		final List<MeshTriangle> triangles = new ArrayList<>();
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double minZ = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		double maxZ = -Double.MAX_VALUE;
		int i = 0;
		while (scanner.hasNextLine()) {
			final String line = scanner.nextLine();
			final String[] tokens = line.split("\\s+");
			if (tokens[0].equals("v")) {
				final double x = Double.parseDouble(tokens[1]);
				final double y = Double.parseDouble(tokens[2]);
				final double z = Double.parseDouble(tokens[3]);
				if (x > maxX) {
					maxX = x;
				}
				if (x < minX) {
					minX = x;
				}
				if (y < minY) {
					minY = y;
				}
				if (z < minZ) {
					minZ = z;
				}
				if (y > maxY) {
					maxY = y;
				}
				if (z > maxZ) {
					maxZ = z;
				}
				System.out.println(i + " " + x + " " + y + " " + z);
				i++;
				vertexes.add(new Vector4(x, y, z, 1));
			} else if (tokens[0].equals("f")) {
				if (tokens.length > 4) {
					System.out.println(Arrays.toString(tokens));
					throw new AssertionError();
				}
				final String[] p1s = tokens[1].split("//");
				final String[] p2s = tokens[2].split("//");
				final String[] p3s = tokens[3].split("//");

				final int p1 = Integer.parseInt(p1s[0]);
				final int p2 = Integer.parseInt(p2s[0]);
				final int p3 = Integer.parseInt(p3s[0]);

				normalMap.add(Arrays.asList(p1, p2, p3,
						Integer.parseInt(p1s[1]), Integer.parseInt(p2s[1]),
						Integer.parseInt(p3s[1])));
			} else if (tokens[0].equals("vn")) {
				final double p1 = Double.parseDouble(tokens[1]);
				final double p2 = Double.parseDouble(tokens[2]);
				final double p3 = Double.parseDouble(tokens[3]);
				vertexNormals.add(new Vector4(p1, p2, p3, 0));
			}
		}

		for (final List<Integer> c : normalMap) {
			triangles.add(new MeshTriangle(vertexes.get(c.get(0) - 1), vertexes
					.get(c.get(1) - 1), vertexes.get(c.get(2) - 1),
					vertexNormals.get(c.get(0) - 1),
					vertexNormals.get(c.get(1) - 1),
					vertexNormals.get(c.get(2) - 1)));
		}
		System.out.println(minX);
		System.out.println(minY);
		System.out.println(minZ);

		System.out.println(maxX);
		System.out.println(maxY);
		System.out.println(maxZ);

		System.out.println(triangles.size());

		return new Mesh(triangles);
	}

	private static Camera loadTestScene() throws Exception {
		final Scene scene = new Scene(new Color(1,1,1));
		final Transform cameraTransform = new Transform();
		cameraTransform.setPosition(new Vector4(-1.5, 0, 0, 1));
		cameraTransform.setRotation(new Vector4(0, 90, 0, 0));
		final Camera camera = scene.addCamera(640, 480, 60, cameraTransform);

		// Instance i = new Instance(new Sphere2());
		// i.material = new Material(new Color(1, 0, 0), 1, 1, 1, 1, 0, 1);
		// i.translate(-2.5, 0, 0);
		// scene.add(i);
		//
		// Instance i1 = new Instance(new Sphere2());
		// i1.material = new Material(new Color(1, .5, 0), 1, 1, 1, 1, 0, 1);
		// scene.add(i1);
		//
		// Instance i2 = new Instance(new Sphere2());
		// i2.material = new Material(new Color(1, 1, 0), 1, 1, 1, 1, 0, 1);
		// i2.translate(2.5, 0, 0);
		// scene.add(i2);
		//
		// Instance i3 = new Instance(new Sphere2());
		// i3.material = new Material(new Color(0, 1, 0), 1, 1, 1, 1, 0, 1);
		// i3.translate(5, 0, 0);
		// scene.add(i3);

		// Instance i5 = new Instance(new Triangle(new Vector4(0, 0, -4, 1),
		// new Vector4(1, 0, -4, 1), new Vector4(0, 1, -4, 1)));
		// i5.material = new Material(new Color(0, 0, 1), 1, 1, 1, 50, 0, 1);
		// // i2.translate(-2, 0, 0);
		// // i5.rotateY(20);
		// i5.scale(2, 1, 1);
		// scene.add(i5);

		// final List<Triangle> triangles = new ArrayList<>();
		// for (int i = 0; i < 1; i++) {
		// for (int j = 0; j < 1; j++) {
		// for (int k = 0; k < 1; k++) {
		// triangles.add(new Triangle(new Vector4(-.5 + i, -.5 + j,
		// -.5 + k, 1), new Vector4(.5 + i, -.5 + j, -.5 + k,
		// 1), new Vector4(-.5 + i, .5 + j, -.5 + k, 1),
		// new Vector4(0, 0, -1, 0)));
		// triangles.add(new Triangle(new Vector4(.5 + i, -.5 + j, -.5
		// + k, 1), new Vector4(-.5 + i, .5 + j, -.5 + k, 1),
		// new Vector4(.5 + i, .5 + j, -.5 + k, 1),
		// new Vector4(0, 0, -1, 0)));
		// triangles.add(new Triangle(new Vector4(.5 + i, .5 + j, -.5
		// + k, 1), new Vector4(.5 + i, .5 + j, .5 + k, 1),
		// new Vector4(.5 + i, -.5 + j, .5 + k, 1),
		// new Vector4(1, 0, 0, 0)));
		// triangles.add(new Triangle(new Vector4(.5 + i, -.5 + j, -.5
		// + k, 1), new Vector4(.5 + i, .5 + j, -.5 + k, 1),
		// new Vector4(.5 + i, -.5 + j, .5 + k, 1),
		// new Vector4(1, 0, 0, 0)));
		// triangles.add(new Triangle(new Vector4(-.5 + i, .5 + j, -.5
		// + k, 1), new Vector4(-.5 + i, -.5 + j, -.5 + k, 1),
		// new Vector4(-.5 + i, -.5 + j, .5 + k, 1),
		// new Vector4(-1, 0, 0, 0)));
		// triangles.add(new Triangle(new Vector4(-.5 + i, .5 + j, -.5
		// + k, 1), new Vector4(-.5 + i, .5 + j, .5 + k, 1),
		// new Vector4(-.5 + i, -.5 + j, .5 + k, 1),
		// new Vector4(-1, 0, 0, 0)));
		// triangles.add(new Triangle(new Vector4(-.5 + i, -.5 + j,
		// .5 + k, 1),
		// new Vector4(.5 + i, -.5 + j, .5 + k, 1),
		// new Vector4(-.5 + i, .5 + j, .5 + k, 1),
		// new Vector4(0, 0, 1, 0)));
		// triangles.add(new Triangle(new Vector4(.5 + i, -.5 + j,
		// .5 + k, 1),
		// new Vector4(-.5 + i, .5 + j, .5 + k, 1),
		// new Vector4(.5 + i, .5 + j, .5 + k, 1),
		// new Vector4(0, 0, 1, 0)));
		// triangles.add(new Triangle(new Vector4(-.5 + i, -.5 + j,
		// -.5 + k, 1), new Vector4(.5 + i, -.5 + j, -.5 + k,
		// 1), new Vector4(-.5 + i, -.5 + j, .5 + k, 1),
		// new Vector4(0, -1, 0, 0)));
		// triangles.add(new Triangle(new Vector4(.5 + i, -.5 + j, -.5
		// + k, 1), new Vector4(.5 + i, -.5 + j, .5 + k, 1),
		// new Vector4(-.5 + i, -.5 + j, .5 + k, 1),
		// new Vector4(0, -1, 0, 0)));
		// triangles.add(new Triangle(new Vector4(-.5 + i, .5 + j, -.5
		// + k, 1), new Vector4(.5 + i, .5 + j, -.5 + k, 1),
		// new Vector4(-.5 + i, .5 + j, .5 + k, 1),
		// new Vector4(0, -1, 0, 0)));
		// triangles.add(new Triangle(new Vector4(.5 + i, .5 + j, -.5
		// + k, 1), new Vector4(.5 + i, .5 + j, .5 + k, 1),
		// new Vector4(-.5 + i, .5 + j, .5 + k, 1),
		// new Vector4(0, -1, 0, 0)));
		// }
		// }
		// }
		// // triangles.add(new Triangle(new Vector4(-1, -.5, -.5, 1), new
		// Vector4(
		// -1, .5, -.5, 1), new Vector4(-.5, -.5, -.5, 1), new Vector4(0,
		// 0, -1, 0)));

		// for (int i = 0; i < 9; i++) {
		// final Instance i7 = new Instance(new Sphere());
		// final Color c = new Color(Math.random(), Math.random(),
		// Math.random());
		// // System.out.println(c);
		// BufferedImage image = ImageIO
		// .read(new File(
		// "C:\\Program Files\\Eclipse\\workspace\\cg-2015-05\\earth.jpg"));
		// Texture t = new ImageTexture(image, new SphericalTextureMapping());
		// i7.material = new Material(t, t, 0, 50, 0, 1);
		// final double x = Math.random() * 20 - 10;
		// final double y = Math.random() * 20 - 10;
		// final double z = Math.random() * 20 - 5;
		// i7.translate(i - 5, 0, 0);
		// // System.out.println("Translation x = " + x + ", y = " + y +
		// // ", z = " + z);
		// // i7.translate(0, 0, 0);
		// scene.add(i7);
		// }

		// for (int i = 0; i < 10; i += 2) {

		final Instance ii = new Instance(parseBunny());

		// ii.rotateX(-138);
		// ii.rotateY(30);
		// ii.translate(0, -1, 0);
		// BufferedImage image = ImageIO
		// .read(new File(
		// "C:\\Program Files\\Eclipse\\workspace\\cg-2015-05\\earth.jpg"));
		// Texture t = new ImageTexture(image, new
		// SphericalTextureMapping());
		// Texture t = new ConstantColorTexture(new Color(.5, .5, .5));
		// ii.material = new Material(t, t, new ConstantColorTexture(new
		// Color(0,0,0)), 50, 0, 1);
		Texture t = new ConstantColorTexture(1,1,1);
		ii.material = Material.GOLD;// new Material(t, t, t, 0, 0, 1);
		 ii.rotateX(-140);
		scene.add(ii);
		// }

		final long start = System.currentTimeMillis();
		KdTree tree = KdTree.from(scene);
		System.out.println("Finished building tree in "
				+ (System.currentTimeMillis() - start));

//		final Transform lightTransform = new Transform();
//		lightTransform.setPosition(new Vector4(-2, 5, -5, 1));
//		final LightProperties lightProperties = new LightProperties(new Color(
//				1f, 1f, 1f));
//		scene.addLight(lightTransform, lightProperties);

		final Transform light2Transform = new Transform();
		light2Transform.setPosition(new Vector4(-2, 5, 5, 1));
		final LightProperties light2Properties = new LightProperties(new Color(
				1f, 1f, 1f));
		scene.addLight(new DirectionalLight(new Vector4(0,-1,1,0), light2Properties));

		// final Transform lightTransform2 = new Transform();
		// lightTransform2.setPosition(new Vector4(-3, 10, 6, 1));
		// final LightProperties lightProperties2 = new LightProperties(new
		// Color(
		// 1, 1, 1));
		// scene.addLight(lightTransform2, lightProperties2);

		scene.setTree(tree);

		return camera;
	}

}