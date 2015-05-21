package edu.ar.itba.raytracer.parser;

import edu.ar.itba.raytracer.Instance;
import edu.ar.itba.raytracer.Material;
import edu.ar.itba.raytracer.MeshTriangle;
import edu.ar.itba.raytracer.shape.*;
import edu.ar.itba.raytracer.vector.Vector3;
import edu.ar.itba.raytracer.vector.Vector4;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShapeParser {

    public static Instance Parse(String line, Material material){
        Instance instance = null;
        if(line.contains("box")){
            instance = parseBox(line);
        }else if(line.contains("plane")){
            instance = parsePlane(line);
        }else if(line.contains("sphere")){
            instance = parseSphere(line);
        }
        return instance;
    }

    public static Instance ParseMesh(String line, Material Material, BufferedReader file) throws IOException {
        List<Vector4> normals = null, vertex = null;
        String normalsrx = "\"normal\\[([^\"]+)\\] N\" \\[";
        String vertexrx = "\"point\\[([^\"]+)\\] P\" \\[";
        String triindices = "\"integer\\[([^\"]+)\\] triindices\" \\[";
        Instance instance = null;
        Matcher m;
        if((m = Pattern.compile(normalsrx).matcher(line)).find()){
            int length = Integer.valueOf(m.group(1));
            normals = parseVectors(length, file);
        }
        if((m = Pattern.compile(vertexrx).matcher(line)).find()){
            int length = Integer.valueOf(m.group(1));
            vertex = parseVectors(length, file);
        }
        if((m = Pattern.compile(triindices).matcher(line)).find()){
            int length = Integer.valueOf(m.group(1));
            instance = new Instance(new Mesh(calculateTriangles(normals,vertex,length,file)));
        }
        return instance;
    }

    private static List<Vector4> parseVectors(int num, BufferedReader file) throws IOException {
        String line;
        List<Vector4> list = new LinkedList<>();
        String vector =  "(\\d?\\.\\d+) (\\d?\\.\\d+) (\\d?\\.\\d+)";
        Pattern pat = Pattern.compile(vector);
        Matcher m;
        while((line = file.readLine()) != null && !line.contains("]")){
            if((m = pat.matcher(line)).find()){
                list.add(new Vector3(Double.valueOf(m.group(1)),Double.valueOf(m.group(2)),Double.valueOf(m.group(3))));
            }
        }
        return list;
    }

    private static List<MeshTriangle> calculateTriangles(List<Vector4> normals, List<Vector4> vertex, int num,
                                                         BufferedReader file) throws IOException {
        String line;
        List<MeshTriangle> list = new LinkedList<>();
        String vector =  "(\\d?\\.\\d+) (\\d?\\.\\d+) (\\d?\\.\\d+)";
        int loc1 = -1, loc2 = -1, loc3 = -1;
        Pattern pat = Pattern.compile(vector);
        Matcher m;
        while((line = file.readLine()) != null && !line.contains("]")){
            if((m = pat.matcher(line)).find()){
                loc1 = Integer.valueOf(m.group(1));
                loc2 = Integer.valueOf(m.group(2));
                loc3 = Integer.valueOf(m.group(3));
                list.add(new MeshTriangle(vertex.get(loc1),vertex.get(loc2),vertex.get(loc3),
                        normals.get(loc1),normals.get(loc2),normals.get(loc3)));
            }
        }
        return list;
    }

    private static Instance parsePlane(String line){
        System.out.println("not implemented yet");
        return null;
//        Vector4 n = null;
//        String name;
//        String normal = "\"normal N\" \\[(\\d+\\.\\d+) (\\d+\\.\\d+) (\\d+\\.\\d+)\\]";
//        String namePattern = "\"string name\" \\[\"([^\"]+)\"\\]";
//        Matcher m;
//        if((m = Pattern.compile(normal).matcher(line)).find()){
//            n = new Vector4(Double.valueOf(m.group(1)), Double.valueOf(m.group(2)),Double.valueOf(m.group(3)),0);
//        }
//        if((m = Pattern.compile(namePattern).matcher(line)).find()){
//            name = m.group(1);
//        }
//        return new Plane(transform,properties,n);
    }

    private static Instance parseBox(String line){
        System.out.println("not implemented yet");
        return null;
//        double width = 0d, height = 0d, depth = 0d;
//        String name;
//        String widths = "\"float width\" \\[(\\d+\\.\\d+)\\]";
//        String heights = "\"float height\" \\[(\\d+\\.\\d+)\\]";
//        String depths = "\"float depth\" \\[(\\d+\\.\\d+)\\]";
//        String namePattern = "\"string name\" \\[\"([^\"]+)\"\\]";
//        Matcher m;
//        if((m = Pattern.compile(widths).matcher(line)).find()){
//            width = Double.valueOf(m.group(1));
//        }
//        if((m = Pattern.compile(heights).matcher(line)).find()){
//            height = Double.valueOf(m.group(1));
//        }
//        if((m = Pattern.compile(depths).matcher(line)).find()){
//            depth = Double.valueOf(m.group(1));
//        }
//        if((m = Pattern.compile(namePattern).matcher(line)).find()){
//            name = m.group(1);
//        }
//        //TODO: add real parameters
//        return new Box(transform,properties);
    }

    private static Instance parseSphere(String line){
        double radius = 1.0;
        String radiusPattern = "\"float radius\" \\[(\\d+\\.\\d+)\\]";
        Matcher m;
        if((m = Pattern.compile(radiusPattern).matcher(line)).find()){
            radius = Double.valueOf(m.group(1));
        }
        Instance instance = new Instance(new Sphere());
        instance.scale(radius, radius, radius);
        return instance;
    }

    private static Instance parseMesh(String line){
        String normalsrx = "\"normal\\[([^\"]+)\\] N\" \\[([^]]+)\\]";
        String pointsrx = "\"point\\[([^\"]+)\\] P\" \\[([^]]+)\\]";
        String triindices = "\"integer\\[([^\"]+)\\] triindices\" \\[([^]]+)\\]";
        ArrayList<Vector3> vertex = new ArrayList<>();
        ArrayList<Vector3> vertexNormal = new ArrayList<>();
        List<MeshTriangle> result = new LinkedList<>();
        int length = 0;
        Matcher m;
        if((m = Pattern.compile(pointsrx).matcher(line)).find()){
            length = Integer.valueOf(m.group(1));
            String[] splitted = m.group(2).split("\\s+");
            for(int i = 0; i<= length*3; i+=3){
                vertex.add(new Vector3(Double.valueOf(splitted[i]),Double.valueOf(splitted[i+1]),
                        Double.valueOf(splitted[i+2])));
            }
        }
        if((m = Pattern.compile(normalsrx).matcher(line)).find()){
            length = Integer.valueOf(m.group(1));
            String[] splitted = m.group(2).split("\\s+");
            for(int i = 0; i<= length*3; i+=3){
                vertexNormal.add(new Vector3(Double.valueOf(splitted[i]), Double.valueOf(splitted[i + 1]),
                        Double.valueOf(splitted[i + 2])));
            }
        }
        if((m = Pattern.compile(triindices).matcher(line)).find()){
            length = Integer.valueOf(m.group(1));
            String[] splitted = m.group(2).split("\\s+");
            for(int i = 0; i<= length*3; i+=3){
                result.add(new MeshTriangle(vertex.get(Integer.valueOf(splitted[i])),
                        vertex.get(Integer.valueOf(splitted[i+1])),vertex.get(Integer.valueOf(splitted[i+2])),
                        vertexNormal.get(Integer.valueOf(splitted[i])),
                        vertexNormal.get(Integer.valueOf(splitted[i+1])),vertexNormal.get(Integer.valueOf(splitted[i+2]))));
            }
        }

        return new Instance(new Mesh(result));
    }
}