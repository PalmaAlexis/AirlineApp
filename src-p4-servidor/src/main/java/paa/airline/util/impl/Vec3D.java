package paa.airline.util.impl;

public class Vec3D {
    private double[] xyz;
    public Vec3D(double x, double y, double z) {
        this.xyz = new double[3];
        this.xyz[0] = x;
        this.xyz[1] = y;
        this.xyz[2] = z;
    }

    public static Vec3D fromSpherical(double longitude_deg, double latitude_deg, double radius) {
        double x = radius * Math.cos(Math.toRadians(longitude_deg)) * Math.cos(Math.toRadians(latitude_deg));
        double y = radius * Math.sin(Math.toRadians(longitude_deg)) * Math.cos(Math.toRadians(latitude_deg));
        double z = radius * Math.sin(Math.toRadians(latitude_deg));
        return new Vec3D(x, y, z);
    }

    public double getLength() {
        return Math.sqrt(this.dot(this));
    }

    public Vec3D getUnitVector() {
        return this.multiply(1.0 / this.getLength());
    }

    public double getLongitude() {
        return Math.toDegrees(Math.atan2(this.xyz[1], this.xyz[0]));
    }

    public double getLatitude() {
        return Math.toDegrees(Math.asin(this.getUnitVector().xyz[2]));
    }


    public double dot(Vec3D other) {
        return Vec3D.dotProduct(this, other);
    }

    public static double dotProduct(Vec3D a, Vec3D b) {
        return a.xyz[0] * b.xyz[0] + a.xyz[1] * b.xyz[1] + a.xyz[2] * b.xyz[2];
    }

    public Vec3D multiply(double scalar) {
        return new Vec3D(this.xyz[0] * scalar, this.xyz[1] * scalar, this.xyz[2] * scalar);
    }

    public Vec3D add(Vec3D other) {
        return Vec3D.sum(this, other);
    }

    public static Vec3D sum(Vec3D a, Vec3D b) {
        return new Vec3D(a.xyz[0] + b.xyz[0], a.xyz[1] + b.xyz[1], a.xyz[2] + b.xyz[2]);
    }

    public static Vec3D slerp(Vec3D from, Vec3D to, double fraction) {
        double fromLength = from.getLength();
        double toLength = to.getLength();
        double omega = Math.acos(from.dot(to) / fromLength / toLength);
        return Vec3D.sum(from.multiply(Math.sin((1.0 - fraction) * omega)),
                to.multiply(Math.sin(fraction * omega))).multiply(1.0 / Math.sin(omega));
    }
}
