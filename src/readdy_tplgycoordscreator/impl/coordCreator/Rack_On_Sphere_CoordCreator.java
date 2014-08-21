/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package readdy_tplgycoordscreator.impl.coordCreator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import readdy.api.sim.core.particle.IParticle;
import readdy.api.sim.core.pot.potentials.IPotential1;
import readdy.api.sim.top.group.IExtendedIdAndType;
import readdy.impl.sim.core.particle.Particle;
import readdy.impl.sim.core.pot.potentials.P1_Sphere;
import readdy.impl.tools.StringTools;
import readdy_tplgycoordscreator.api.coordCreator.IPotentialSpecificCoordsCreator;
import readdy_tplgycoordscreator.impl.tools.CartesianCoordinates;
import readdy_tplgycoordscreator.impl.tools.SphericalCoordinates;
import statlab.base.util.DoubleArrays;

/**
 *
 * @author johannesschoeneberg
 */
public class Rack_On_Sphere_CoordCreator implements IPotentialSpecificCoordsCreator {

    IPotential1 pot;
    double[][] latticeBounds;
    HashMap<String, String> parameters;
    String[] essentialParameterKeys=P1_Sphere.essentialParameterKeys;
    double[] origin;
    double radius;
    Random rand = new Random();
    SphericalCoordinates sphericalCoords = new SphericalCoordinates();

    public double[] get_nextCoordinates() {
        double[] coords = new double[3];
        boolean valid = false;
        while (!valid) {
            coords = generateNewCoordinates();
            valid = validateCoords(coords);
        }
        return coords;
    }

    public void set_latticeBounds(double[][] latticeBounds) {
        this.latticeBounds = latticeBounds;
    }


    public void set_parameters(HashMap<String, String> potentialParameters) {
        if (essentialParameterKeys != null) {
            this.parameters = potentialParameters;


            this.origin = StringTools.splitArrayString_convertToDouble(parameters.get(essentialParameterKeys[5]));
            if (origin.length != 3) {
                throw new RuntimeException("center vector dimension != 3");
            }

            this.radius = Double.parseDouble(parameters.get(essentialParameterKeys[6]));


        } else {
            throw new RuntimeException("essentialParameterKeys are not present. Tey have to be set before.");
        }
    }

    private double[] generateNewCoordinates() {

        double[] newCoords = sphericalCoords.getUniformlyDistributedPointOnOriginCenteredSphere(radius);
        // translate the coords to the actual sphere origin
        return DoubleArrays.add(origin, newCoords);

    }

    private boolean validateCoords(double[] coords) {

        return true;

    }

    public double[] get_nextNormal() {
        double[] normal = new double[3];
        double theta_rand = Math.PI * rand.nextDouble(); // longitude
        double phi_rand = 2 * Math.PI * rand.nextDouble(); // latitude
        normal = sphericalCoords.sphericalToCartesian(radius, theta_rand, phi_rand);
        return normal;
    }

    public void set_potentialObject(IPotential1 pot) {
        this.pot = pot;
    }

    public ArrayList<IParticle> positionRackOnSphere(
            ArrayList<double[]> groupTemplateCoordinates, // particle coordinates
            ArrayList<IExtendedIdAndType> buildingBlocks  // particle type
            ) {

        // the new position of the center of the rack is the following one:
        // {r, theta, phi}
        double[] newPositionOnSphere = {radius,rand.nextDouble()*Math.PI,rand.nextDouble()*Math.PI*2};
        
        
        // rack positioning on a sphere:
        // this only works because i know, that all racks are defined in their template
        // coordinates in the way, that they stretch in the x dimension, and are 0 in z.
        // if this would be different it no longer works.


        //------------------------------------------------------------------------
        //1) center all coordinates in their center of mass
        //------------------------------------------------------------------------
        double[] centerOfMass = new double[]{0, 0, 0};
        for (double[] coords : groupTemplateCoordinates) {
            DoubleArrays.add(centerOfMass, coords);
        }
        centerOfMass = DoubleArrays.multiply(groupTemplateCoordinates.size(), centerOfMass);

        ArrayList<double[]> centeredCoords = new ArrayList();
        for (double[] coords : groupTemplateCoordinates) {
            centeredCoords.add(DoubleArrays.subtract(coords, centerOfMass));
        }

        //------------------------------------------------------------------------
        //2) Transformation to the North Pole and Curvature
        //------------------------------------------------------------------------
        ArrayList<double[]> coordinatesCurved = new ArrayList();
        for (double[] coord : centeredCoords) {
            //determine the x value of the centered coords
            double distX = coord[0];

            // determine the angle which would lie in between points on a sphere 
            // of given radius if they were distX apart from each other
            double angle = distX / radius;

            //determine the coordinates of the point, if it would lie on a 
            //sphere of the given radius
            double newZCoord = Math.cos(angle) * radius;
            double newXCoord = Math.sin(angle) * radius;
            // its the other way around with sine and cosine functions than
            // expected from first glance, since we are not measuring the angle
            // between the x axis and the angle, but between the y axis and the
            //angle.This switches sine and cosine around

            double[] newCoords = new double[]{newXCoord, coord[1], newZCoord};
            coordinatesCurved.add(newCoords);
        }

        //------------------------------------------------------------------------
        //3) Arbitrary Rotation at the north pole
        //------------------------------------------------------------------------
        ArrayList<double[]> coordinatesCurvedRotated = new ArrayList();
        double rotationAngle = Math.PI * rand.nextDouble();
        double[] rotationVector = new double[]{0, 0, 1};

        double[][] rotationMatrix = CartesianCoordinates.getArbitraryRotMatrix(rotationAngle, rotationVector);
        for (double[] coord : coordinatesCurved) {
            coordinatesCurvedRotated.add(CartesianCoordinates.matrixVectorMult_right(rotationMatrix, coord));
        }

        //------------------------------------------------------------------------
        //4) Rotation in Theta dimension around X to the intended point
        //------------------------------------------------------------------------
        ArrayList<double[]> coordinatesCurvedRotated_thetaRotated = new ArrayList();
        double rotationAngle2 = newPositionOnSphere[1]; // theta value of the new position
        double[] rotationVector2 = new double[]{1, 0, 0}; // rotate around the x axis

        double[][] rotationMatrix2 = CartesianCoordinates.getArbitraryRotMatrix(rotationAngle2, rotationVector2);
        for (double[] coord : coordinatesCurvedRotated) {
            coordinatesCurvedRotated_thetaRotated.add(CartesianCoordinates.matrixVectorMult_right(rotationMatrix2, coord));
        }
        
        //------------------------------------------------------------------------
        //5) Rotation in Theta dimension around X to the intended point
        //------------------------------------------------------------------------
        ArrayList<double[]> coordinatesCurvedRotated_thetaRotated_phiRotated = new ArrayList();
        double rotationAngle3 = newPositionOnSphere[2]; // phi value of the new position
        double[] rotationVector3 = new double[]{0, 0, 1}; // rotate around the z axis

        double[][] rotationMatrix3 = CartesianCoordinates.getArbitraryRotMatrix(rotationAngle3, rotationVector3);
        for (double[] coord : coordinatesCurvedRotated_thetaRotated) {
            coordinatesCurvedRotated_thetaRotated_phiRotated.add(CartesianCoordinates.matrixVectorMult_right(rotationMatrix3, coord));
        }
        
        //------------------------------------------------------------------------
        //Output
        //------------------------------------------------------------------------
        ArrayList<IParticle> result = new ArrayList();
        for (int i = 0; i < coordinatesCurvedRotated_thetaRotated_phiRotated.size(); i++) {
            int type = buildingBlocks.get(i).get_type();
            double[] coord = coordinatesCurvedRotated_thetaRotated_phiRotated.get(i);
            IParticle newParticle = new Particle(-1, type,coord);
            result.add(newParticle);
        }

        return result;
    }

}
