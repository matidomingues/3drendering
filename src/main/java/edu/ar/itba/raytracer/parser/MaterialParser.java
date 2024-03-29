package edu.ar.itba.raytracer.parser;

import edu.ar.itba.raytracer.Material;

import edu.ar.itba.raytracer.materials.Glass;
import edu.ar.itba.raytracer.materials.Matte;
import edu.ar.itba.raytracer.materials.Metal2;
import edu.ar.itba.raytracer.materials.Mirror;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.texture.ConstantColorTexture;
import edu.ar.itba.raytracer.texture.Texture;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MaterialParser {

    final static String kdcolor = "\"color Kd\" \\[(\\d?\\.\\d+) (\\d?\\.\\d+) (\\d?\\.\\d+)\\]";
    final static String kdtexture = "\"texture Kd\" \\[\"([^\"]+)\"\\]";
    final static String krcolor = "\"color Kr\" \\[(\\d?\\.\\d+) (\\d?\\.\\d+) (\\d?\\.\\d+)\\]";
    final static String krtexture = "\"texture Kr\" \\[\"([^\"]+)\"\\]";
    final static String ktcolor = "\"color Kt\" \\[(\\d?\\.\\d+) (\\d?\\.\\d+) (\\d?\\.\\d+)\\]";
    final static String kttexture = "\"texture Kt\" \\[\"([^\"]+)\"\\]";
    final static String roughnessrx = "\"float roughness\" \\[(\\d?\\.\\d+)\\]";
    final static String uroughnessrx = "\"float uroughness\" \\[(\\d?\\.\\d+)\\]";
    final static String vroughnessrx = "\"float vroughness\" \\[(\\d?\\.\\d+)\\]";
    final static String fresnelrx = "\"float ior\" \\[(\\d+\\.\\d+)\\]";
    final static String index = "\"float index\" \\[(\\d*\\.\\d+)\\]";

    public static Material Parse(String line,Map<String,Texture> textureMap){
        Material mat = new Matte(new Color(1,1,1));
        if(line.contains("matte")){
            mat = parseMatte(line, textureMap);
        }else if(line.contains("glass")){
            mat = parseGlass(line, textureMap);
        }else if(line.contains("mirror")){
            mat = parseMirror(line, textureMap);
        }else if(line.contains("metal2")){
            mat = parseMetal2(line, textureMap);
        }
        return mat;
    }

    public static void parseNamedMaterial(String line, Map<String,Material> materialMap, Map<String,Texture> textureMap){
        String materialNamerx = "MakeNamedMaterial \"([^\"]+)\"";
        String materialName;
        Matcher m;
        if((m = Pattern.compile(materialNamerx).matcher(line)).find()) {
            materialName = m.group(1);
            materialMap.put(materialName, Parse(line,textureMap));
        }
    }

    public static Material getNamedMaterial(String line, Map<String,Material> materialMap){
        String materialNamerx = "NamedMaterial \"([^\"]+)\"";
        String materialName = null;
        Matcher m;
        if((m = Pattern.compile(materialNamerx).matcher(line)).find()) {
            materialName = m.group(1);
        }
        if(materialName != null){
            return materialMap.get(materialName);
        }
        return null;
    }

    private static Material parseMatte(String line, Map<String,Texture> textureMap){
        Texture diffuseColor = new ConstantColorTexture(new Color(1,1,1));
        Matcher m;
        if((m = Pattern.compile(kdcolor).matcher(line)).find()) {
            diffuseColor = new ConstantColorTexture(new Color(Double.valueOf(m.group(1)),
                    Double.valueOf(m.group(2)), Double.valueOf(m.group(3))));
        }
        if((m= Pattern.compile(kdtexture).matcher(line)).find()) {
            diffuseColor = textureMap.get(m.group(1));
        }
        return new Matte(diffuseColor);
    }

    private static Material parseGlass(String line, Map<String,Texture> textureMap){
        Texture reflectivity = new ConstantColorTexture(0.588235, 0.670588, 0.729412);
        Texture transmited =  new ConstantColorTexture(new Color(1.0,1.0,1.0));
        double refractionIndex = 1.5;
        Matcher m;
        if((m = Pattern.compile(krcolor).matcher(line)).find()) {
            reflectivity = new ConstantColorTexture(new Color(Double.valueOf(m.group(1)),
                    Double.valueOf(m.group(2)), Double.valueOf(m.group(3))));
        }
        if((m= Pattern.compile(krtexture).matcher(line)).find()) {
            reflectivity = textureMap.get(m.group(1));
        }
        if((m = Pattern.compile(ktcolor).matcher(line)).find()) {
            transmited = new ConstantColorTexture(new Color(Double.valueOf(m.group(1)),
                    Double.valueOf(m.group(2)), Double.valueOf(m.group(3))));
        }
        if((m = Pattern.compile(kttexture).matcher(line)).find()) {
            transmited = textureMap.get(m.group(1));
        }

        if((m = Pattern.compile(index).matcher(line)).find()) {
            refractionIndex = Double.valueOf(m.group(1));
        }
        return new Glass(refractionIndex, transmited, reflectivity);
    }

    private static Material parseMirror(String line, Map<String,Texture> textureMap){
        Texture reflectivity = new ConstantColorTexture(new Color(1,1,1));
        Matcher m = Pattern.compile(krcolor).matcher(line);
        if(m.find()) {
            reflectivity = new ConstantColorTexture(new Color(Double.valueOf(m.group(1)),
                    Double.valueOf(m.group(2)), Double.valueOf(m.group(3))));
        }
        if((m= Pattern.compile(krtexture).matcher(line)).find()) {
            reflectivity = textureMap.get(m.group(1));
        }
        return new Mirror(reflectivity);
    }

    private static Material parseMetal2(String line, Map<String,Texture> textureMap){
        double roughness = 0.0001, uroughness = 0.001, vroughness = 0.001, ior = 0.88;
        boolean simpleRough = true;
        Texture reflectivity = new ConstantColorTexture(new Color(1,1,1));
        Texture diffuse = new ConstantColorTexture(new Color(0,0,0));
        Matcher m;
        if((m = Pattern.compile(roughnessrx).matcher(line)).find()) {
            roughness = Double.valueOf(m.group(1));
        }
        if((m = Pattern.compile(roughnessrx).matcher(line)).find()) {
            roughness = Double.valueOf(m.group(1));
            simpleRough = false;
        }
        if((m = Pattern.compile(uroughnessrx).matcher(line)).find()) {
            uroughness = Double.valueOf(m.group(1));
            simpleRough = false;
        }
        if((m = Pattern.compile(vroughnessrx).matcher(line)).find()) {
            vroughness = Double.valueOf(m.group(1));
        }
        if((m = Pattern.compile(fresnelrx).matcher(line)).find()) {
            ior = Double.valueOf(m.group(1));
        }
        if(!simpleRough){
            roughness = Math.max(uroughness,vroughness);
        }
        if((m = Pattern.compile(krcolor).matcher(line)).find()) {
            reflectivity = new ConstantColorTexture(new Color(Double.valueOf(m.group(1)),
                    Double.valueOf(m.group(2)), Double.valueOf(m.group(3))));
        }
        if((m= Pattern.compile(krtexture).matcher(line)).find()) {
            reflectivity = textureMap.get(m.group(1));
        }
        if((m = Pattern.compile(kdcolor).matcher(line)).find()) {
            diffuse = new ConstantColorTexture(new Color(Double.valueOf(m.group(1)),
                    Double.valueOf(m.group(2)), Double.valueOf(m.group(3))));
        }
        if((m= Pattern.compile(kdtexture).matcher(line)).find()) {
            diffuse = textureMap.get(m.group(1));
        }
        return new Metal2(reflectivity, diffuse, roughness, ior);
    }
}
