/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package readdy_tplgycoordscreator.impl.tools;

/**
 *
 * @author johannesschoeneberg
 */
public class CartesianCoordinates {

    public CartesianCoordinates() {
    }

    ;

    public static double[][] getArbitraryRotMatrix(double alpha, double[] rotAxis) {
        double v1 = rotAxis[0];
        double v2 = rotAxis[1];
        double v3 = rotAxis[2];

        double cosa = Math.cos(alpha);
        double sina = Math.sin(alpha);

        return new double[][]{new double[]{(cosa + v1 * v1 * (1 - cosa)),
                        (v1 * v2 * (1 - cosa) - v3 * sina),
                        (v1 * v3 * (1 - cosa) + v2 * sina)},
                    new double[]{
                        (v2 * v1 * (1 - cosa) + v3 * sina),
                        (cosa + v2 * v2 * (1 - cosa)),
                        (v2 * v3 * (1 - cosa) - v1 * sina)},
                    new double[]{
                        (v3 * v1 * (1 - cosa) - v2 * sina),
                        (v3 * v2 * (1 - cosa) + v1 * sina),
                        (cosa + v3 * v3 * (1 - cosa))}};
    }

    /**
     * Matrix vector multiplication M*v
     *
     * @param M
     * @param v
     * @return
     */
    public static double[] matrixVectorMult_right(double[][] M, double[] v) {
        return new double[]{
                    (M[0][0] * v[0] + M[0][1] * v[1] + M[0][2] * v[2]),
                    (M[1][0] * v[0] + M[1][1] * v[1] + M[1][2] * v[2]),
                    (M[2][0] * v[0] + M[2][1] * v[1] + M[2][2] * v[2])};

    }

    /**
     * rotates a vector
     *
     * @param toBeRotated
     * @param alpha
     * @param rotAxis
     * @return
     */
    public static double[] rotateVector(double[] toBeRotated, double alpha, double[] rotAxis) {
        double[][] R = getArbitraryRotMatrix(alpha, rotAxis);
        return matrixVectorMult_right(R, toBeRotated);
    }

    public static double[] crossProduct(double[] a, double[] b) {
        if (a.length == b.length && a.length == 3) {
            double[] product = new double[3];
            product[0] = a[1] * b[2] - a[2] * b[1];
            product[1] = a[2] * b[0] - a[0] * b[2];
            product[2] = a[0] * b[1] - a[1] * b[0];
            return product;
        } else {
            throw new RuntimeException("vectors not compatible");
        }
    }
}