package com.biscofil.defcon2016.map;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.util.LongSparseArray;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Tile;
import com.google.android.gms.maps.model.TileProvider;
import com.google.maps.android.geometry.Bounds;
import com.google.maps.android.geometry.Point;
import com.google.maps.android.quadtree.PointQuadTree;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by bisco on 07/12/2016.
 */

public class MyHeatmapTileProvider implements TileProvider {
    public static final int DEFAULT_RADIUS = 20;
    public static final double DEFAULT_OPACITY = 0.7D;
    private static final int[] DEFAULT_GRADIENT_COLORS = new int[]{Color.rgb(102, 225, 0), Color.rgb(255, 0, 0)};
    private static final float[] DEFAULT_GRADIENT_START_POINTS = new float[]{0.2F, 1.0F};
    public static final MyGradient DEFAULT_GRADIENT;
    static final double WORLD_WIDTH = 1.0D;
    private static final int TILE_DIM = 512;
    private static final int SCREEN_SIZE = 1280;
    private static final int DEFAULT_MIN_ZOOM = 5;
    private static final int DEFAULT_MAX_ZOOM = 11;
    private static final int MAX_ZOOM_LEVEL = 22;
    private static final int MIN_RADIUS = 10;
    private static final int MAX_RADIUS = 50;
    private PointQuadTree<ValuedLatLng> mTree;
    private Collection<ValuedLatLng> mData;
    private Bounds mBounds;
    private int mRadius;
    private MyGradient mGradient;
    private int[] mColorMap;
    private double[] mKernel;
    private double mOpacity;
    private double[] mMaxIntensity;

    Bitmap bitmap1;


    private MyHeatmapTileProvider(MyHeatmapTileProvider.Builder builder) {
        this.mData = builder.data;
        this.mRadius = builder.radius;
        this.mGradient = builder.gradient;
        this.mOpacity = builder.opacity;
        this.mKernel = generateKernel(this.mRadius, (double) this.mRadius / 3.0D);
        this.setGradient(this.mGradient);
        this.setValuedData(this.mData);
    }

    public void setValuedData(Collection<ValuedLatLng> data) {
        this.mData = data;
        if (this.mData.isEmpty()) {
            throw new IllegalArgumentException("No input points.");
        } else {
            this.mBounds = getBounds(this.mData);
            this.mTree = new PointQuadTree(this.mBounds);
            Iterator var2 = this.mData.iterator();
            while (var2.hasNext()) {
                ValuedLatLng l = (ValuedLatLng) var2.next();
                this.mTree.add(l);
            }
            this.mMaxIntensity = this.getMaxIntensities(this.mRadius);
        }
    }

    private static Collection<ValuedLatLng> wrapData(Collection<LatLng> data) {
        ArrayList valuedData = new ArrayList();
        Iterator var2 = data.iterator();
        while (var2.hasNext()) {
            LatLng l = (LatLng) var2.next();
            valuedData.add(new ValuedLatLng(l));
        }
        return valuedData;
    }

    public Tile getTile(int x, int y, int zoom) {

        double tileWidth = 1.0D / Math.pow(2.0D, (double) zoom);
        double padding = tileWidth * (double) this.mRadius / 512.0D;
        double tileWidthPadded = tileWidth + 2.0D * padding;
        double bucketWidth = tileWidthPadded / (double) (512 + this.mRadius * 2);
        double minX = (double) x * tileWidth - padding;
        double maxX = (double) (x + 1) * tileWidth + padding;
        double minY = (double) y * tileWidth - padding;
        double maxY = (double) (y + 1) * tileWidth + padding;
        double xOffset = 0.0D;
        Object wrappedPoints = new ArrayList();
        Bounds tileBounds;
        if (minX < 0.0D) {
            tileBounds = new Bounds(minX + 1.0D, 1.0D, minY, maxY);
            xOffset = -1.0D;
            wrappedPoints = this.mTree.search(tileBounds);
        } else if (maxX > 1.0D) {
            tileBounds = new Bounds(0.0D, maxX - 1.0D, minY, maxY);
            xOffset = 1.0D;
            wrappedPoints = this.mTree.search(tileBounds);
        }

        tileBounds = new Bounds(minX, maxX, minY, maxY);
        Bounds paddedBounds = new Bounds(this.mBounds.minX - padding, this.mBounds.maxX + padding, this.mBounds.minY - padding, this.mBounds.maxY + padding);
        if (!tileBounds.intersects(paddedBounds)) {
            return TileProvider.NO_TILE;
        } else {
            Collection points = this.mTree.search(tileBounds);
            if (points.isEmpty()) {
                return TileProvider.NO_TILE;
            } else {
                double[][] intensity = new double[512 + this.mRadius * 2][512 + this.mRadius * 2];

                Iterator convolved;
                ValuedLatLng bitmap;
                Point p;
                int bucketX;
                int bucketY;
                for (convolved = points.iterator(); convolved.hasNext(); intensity[bucketX][bucketY] += bitmap.getValue()) {
                    bitmap = (ValuedLatLng) convolved.next();
                    p = bitmap.getPoint();
                    bucketX = (int) ((p.x - minX) / bucketWidth);
                    bucketY = (int) ((p.y - minY) / bucketWidth);
                }

                for (convolved = ((Collection) wrappedPoints).iterator(); convolved.hasNext(); intensity[bucketX][bucketY] += bitmap.getValue()) {
                    bitmap = (ValuedLatLng) convolved.next();
                    p = bitmap.getPoint();
                    bucketX = (int) ((p.x + xOffset - minX) / bucketWidth);
                    bucketY = (int) ((p.y - minY) / bucketWidth);
                }

                double[][] convolved1 = convolve(intensity, this.mKernel);

                bitmap1 = colorize(convolved1, this.mColorMap, this.mMaxIntensity[zoom]);

                //aaa

                Log.d("BISCO", "useless");

                return convertBitmap(bitmap1);
            }
        }
    }

    public void setGradient(MyGradient gradient) {
        this.mGradient = gradient;
        this.mColorMap = gradient.generateColorMap(this.mOpacity);
    }

    public void setRadius(int radius) {
        this.mRadius = radius;
        this.mKernel = generateKernel(this.mRadius, (double) this.mRadius / 3.0D);
        this.mMaxIntensity = this.getMaxIntensities(this.mRadius);
    }

    public void setOpacity(double opacity) {
        this.mOpacity = opacity;
        this.setGradient(this.mGradient);
    }

    private double[] getMaxIntensities(int radius) {
        double[] maxIntensityArray = new double[22];

        int i;
        for (i = 5; i < 11; ++i) {
            maxIntensityArray[i] = getMaxValue(this.mData, this.mBounds, radius, (int) (1280.0D * Math.pow(2.0D, (double) (i - 3))));
            if (i == 5) {
                for (int j = 0; j < i; ++j) {
                    maxIntensityArray[j] = maxIntensityArray[i];
                }
            }
        }

        for (i = 11; i < 22; ++i) {
            maxIntensityArray[i] = maxIntensityArray[10];
        }

        return maxIntensityArray;
    }

    private static Tile convertBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();
        return new Tile(512, 512, bitmapdata);
    }

    static Bounds getBounds(Collection<ValuedLatLng> points) {
        Iterator iter = points.iterator();
        ValuedLatLng first = (ValuedLatLng) iter.next();
        double minX = first.getPoint().x;
        double maxX = first.getPoint().x;
        double minY = first.getPoint().y;
        double maxY = first.getPoint().y;

        while (iter.hasNext()) {
            ValuedLatLng l = (ValuedLatLng) iter.next();
            double x = l.getPoint().x;
            double y = l.getPoint().y;
            if (x < minX) {
                minX = x;
            }

            if (x > maxX) {
                maxX = x;
            }

            if (y < minY) {
                minY = y;
            }

            if (y > maxY) {
                maxY = y;
            }
        }

        return new Bounds(minX, maxX, minY, maxY);
    }

    static double[] generateKernel(int radius, double sd) {
        double[] kernel = new double[radius * 2 + 1];
        for (int i = -radius; i <= radius; ++i) {
            kernel[i + radius] = Math.exp((double) (-i * i) / (2.0D * sd * sd));
        }
        return kernel;
    }

    static double[][] convolve(double[][] grid, double[] kernel) {
        int radius = (int) Math.floor((double) kernel.length / 2.0D);
        int dimOld = grid.length;
        int dim = dimOld - 2 * radius;
        int lowerLimit = radius;
        int upperLimit = radius + dim - 1;
        double[][] intermediate = new double[dimOld][dimOld];

        int x;
        int y;
        int initial;
        double val;
        for (x = 0; x < dimOld; ++x) {
            for (y = 0; y < dimOld; ++y) {
                val = grid[x][y];
                if (val != 0.0D) {
                    int xUpperLimit = (upperLimit < x + radius ? upperLimit : x + radius) + 1;
                    initial = lowerLimit > x - radius ? lowerLimit : x - radius;

                    for (int x2 = initial; x2 < xUpperLimit; ++x2) {
                        intermediate[x2][y] += val * kernel[x2 - (x - radius)];
                    }
                }
            }
        }

        double[][] outputGrid = new double[dim][dim];

        for (x = lowerLimit; x < upperLimit + 1; ++x) {
            for (y = 0; y < dimOld; ++y) {
                val = intermediate[x][y];
                if (val != 0.0D) {
                    int yUpperLimit = (upperLimit < y + radius ? upperLimit : y + radius) + 1;
                    initial = lowerLimit > y - radius ? lowerLimit : y - radius;

                    for (int y2 = initial; y2 < yUpperLimit; ++y2) {
                        outputGrid[x - radius][y2 - radius] += val * kernel[y2 - (y - radius)];
                    }
                }
            }
        }


        return outputGrid;
    }

    static Bitmap colorize(double[][] grid, int[] colorMap, double max) {
        int maxColor = colorMap[colorMap.length - 1];
        double colorMapScaling = (double) (colorMap.length - 1) / max;
        int dim = grid.length;
        int[] colors = new int[dim * dim];

        for (int i = 0; i < dim; ++i) {
            for (int j = 0; j < dim; ++j) {

                double val = grid[j][i];
                int index = i * dim + j;

                int col = (int) (val * colorMapScaling);
                if (val != 0.0D) {
                    if (col < colorMap.length) {
                        colors[index] = colorMap[col];
                    } else {
                        colors[index] = maxColor;
                    }
                } else {
                    colors[index] = 0;
                }
            }
        }

        Bitmap tile = Bitmap.createBitmap(dim, dim, Bitmap.Config.ARGB_8888);
        tile.setPixels(colors, 0, dim, 0, 0, dim, dim);
        return tile;
    }

    static double getMaxValue(Collection<ValuedLatLng> points, Bounds bounds, int radius, int screenDim) {
        double minX = bounds.minX;
        double maxX = bounds.maxX;
        double minY = bounds.minY;
        double maxY = bounds.maxY;
        double boundsDim = maxX - minX > maxY - minY ? maxX - minX : maxY - minY;
        int nBuckets = (int) ((double) (screenDim / (2 * radius)) + 0.5D);
        double scale = (double) nBuckets / boundsDim;
        LongSparseArray buckets = new LongSparseArray();
        double max = 0.0D;
        Iterator var24 = points.iterator();

        while (var24.hasNext()) {
            ValuedLatLng l = (ValuedLatLng) var24.next();
            double x = l.getPoint().x;
            double y = l.getPoint().y;
            int xBucket = (int) ((x - minX) * scale);
            int yBucket = (int) ((y - minY) * scale);
            LongSparseArray column = (LongSparseArray) buckets.get((long) xBucket);
            if (column == null) {
                column = new LongSparseArray();
                buckets.put((long) xBucket, column);
            }

            Double value = (Double) column.get((long) yBucket);
            if (value == null) {
                value = Double.valueOf(0.0D);
            }

            value = Double.valueOf(value.doubleValue() + l.getValue());
            column.put((long) yBucket, value);
            if (value.doubleValue() > max) {
                max = value.doubleValue();
            }
        }

        return max;
    }

    static {
        DEFAULT_GRADIENT = new MyGradient(DEFAULT_GRADIENT_COLORS, DEFAULT_GRADIENT_START_POINTS);
    }

    public static class Builder {
        private Collection<ValuedLatLng> data;
        private int radius = 20;
        private MyGradient gradient;
        private double opacity;

        public Builder() {
            this.gradient = MyHeatmapTileProvider.DEFAULT_GRADIENT;
            this.opacity = 0.7D;
        }

        public MyHeatmapTileProvider.Builder valuedData(Collection<ValuedLatLng> val) {
            this.data = val;
            if (this.data.isEmpty()) {
                throw new IllegalArgumentException("No input points.");
            } else {
                return this;
            }
        }

        public MyHeatmapTileProvider.Builder radius(int val) {
            this.radius = val;
            if (this.radius >= 10 && this.radius <= 50) {
                return this;
            } else {
                throw new IllegalArgumentException("Radius not within bounds.");
            }
        }

        public MyHeatmapTileProvider.Builder gradient(MyGradient val) {
            this.gradient = val;
            return this;
        }

        public MyHeatmapTileProvider.Builder opacity(double val) {
            this.opacity = val;
            if (this.opacity >= 0.0D && this.opacity <= 1.0D) {
                return this;
            } else {
                throw new IllegalArgumentException("Opacity must be in range [0, 1]");
            }
        }

        public MyHeatmapTileProvider build() {
            if (this.data == null) {
                throw new IllegalStateException("No input data: you must use either .data or .weightedData before building");
            } else {
                return new MyHeatmapTileProvider(this);
            }
        }
    }
}
