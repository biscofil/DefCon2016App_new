package com.biscofil.defcon2016.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.geometry.Point;
import com.google.maps.android.projection.SphericalMercatorProjection;
import com.google.maps.android.quadtree.PointQuadTree;

/**
 * Created by bisco on 07/12/2016.
 */

public class ValuedLatLng implements PointQuadTree.Item {
    public static final double DEFAULT_INTENSITY = 1.0D;
    private static final SphericalMercatorProjection sProjection = new SphericalMercatorProjection(1.0D);
    private Point mPoint;
    private double mValue;

    public ValuedLatLng(LatLng latLng, double value) {
        this.mPoint = sProjection.toPoint(latLng);
        if (value >= 0.0D) {
            this.mValue = value;
        } else {
            this.mValue = 1.0f;
        }

    }

    public ValuedLatLng(LatLng latLng) {
        this(latLng, 1.0f);
    }

    public Point getPoint() {
        return this.mPoint;
    }

    public double getValue() {
        return this.mValue;
    }
}
