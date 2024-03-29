package edu.ar.itba.raytracer.materials;

import edu.ar.itba.raytracer.Material;
import edu.ar.itba.raytracer.properties.Color;
import edu.ar.itba.raytracer.texture.ConstantColorTexture;
import edu.ar.itba.raytracer.texture.Texture;

public class Glass extends Material {

	public Glass(double refractionIndex) {
		// this(refractionIndex, new ConstantColorTexture(1, 1, 1),
		this(refractionIndex, new ConstantColorTexture(1, 1, 1),
				new ConstantColorTexture(0.588235, 0.670588, 0.729412));
	}

	public Glass(double refractionIndex, Texture transparency, Texture reflected) {
		super(ConstantColorTexture.BLACK, reflected,
				ConstantColorTexture.BLACK, 0d, transparency, refractionIndex);
	}
}
