package edu.ar.itba.raytracer.materials;

import edu.ar.itba.raytracer.Material;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.texture.ConstantColorTexture;
import edu.ar.itba.raytracer.texture.Texture;

public class Metal2 extends Material{

    public Metal2(final Texture kr, final double roughness){
        super(new ConstantColorTexture(new Color(0,0,0)),
                new ConstantColorTexture(0,0,0),kr
                ,999*Math.abs(1-roughness),0d,0d);
    }
}
