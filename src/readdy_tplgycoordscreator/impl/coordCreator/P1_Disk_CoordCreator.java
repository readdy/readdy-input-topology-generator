// ==========================================================================
//           ReaDDy - The Library for Reaction Diffusion Dynamics
// ==========================================================================
// Copyright (c) 2010-2013, Johannes Schöneberg, Frank Noé, FU Berlin
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
//     * Redistributions of source code must retain the above copyright
//       notice, this list of conditions and the following disclaimer.
//     * Redistributions in binary form must reproduce the above copyright
//       notice, this list of conditions and the following disclaimer in the
//       documentation and/or other materials provided with the distribution.
//     * Neither the name of Johannes Schöneberg or Frank Noé or the FU Berlin
//       nor the names of its contributors may be used to endorse or promote
//       products derived from this software without specific prior written
//       permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.
//
// ==========================================================================
package readdy_tplgycoordscreator.impl.coordCreator;

import java.util.HashMap;
import java.util.Random;
import readdy.api.sim.core.pot.potentials.IPotential1;
import readdy.impl.sim.core.pot.potentials.P1_Disk;
import readdy_tplgycoordscreator.api.coordCreator.IPotentialSpecificCoordsCreator;
import readdy.impl.tools.StringTools;
import statlab.base.util.DoubleArrays;

/**
 *
 * @author schoeneberg
 */
public class P1_Disk_CoordCreator implements IPotentialSpecificCoordsCreator {

    IPotential1 pot;
    double[][] latticeBounds;
    HashMap<String, String> parameters;
    double[] center, normal, u, v;
    double diskRadius;
    Random rand = new Random();

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
            this.parameters = potentialParameters;


            this.center = StringTools.splitArrayString_convertToDouble(parameters.get("center"));
            if (center.length != 3) {
                throw new RuntimeException("center vector dimension != 3");
            }

            double[] rawNormal = StringTools.splitArrayString_convertToDouble(parameters.get("normal"));
            if (rawNormal.length != 3) {
                throw new RuntimeException("normal vector dimension != 3");
            }
            this.normal = DoubleArrays.normalize(rawNormal);

            //this.distCenterToTheOrigin = DoubleArrays.dot(center, normal)/DoubleArrays.norm(normal);
            this.diskRadius = Double.parseDouble(parameters.get("radius"));


            // compute vectors u and v perpendicular to the disk plane.
            this.u = DoubleArrays.normalize(crossProduct(normal, new double[]{1, 1, 1}));
            this.v = DoubleArrays.normalize(crossProduct(normal, u));

    }

    private double[] crossProduct(double[] a, double[] b) {
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

    private double[] generateNewCoordinates() {
        double uSign = rand.nextBoolean() ? 1.0 : -1.0;
        double vSign = rand.nextBoolean() ? 1.0 : -1.0;
        double[] uHat = DoubleArrays.multiply(uSign * diskRadius * rand.nextDouble(), u);
        double[] vHat = DoubleArrays.multiply(vSign * diskRadius * rand.nextDouble(), v);
        return DoubleArrays.add(center, DoubleArrays.add(uHat, vHat));
    }

    private boolean validateCoords(double[] coords) {
        double distToCenterOfNewCoord = DoubleArrays.norm(DoubleArrays.subtract(center, coords));
        if (distToCenterOfNewCoord < diskRadius) {
            return true;
        } else {
            return false;
        }
    }

    public double[] get_nextNormal() {
        return normal;
    }

    public void set_potentialObject(IPotential1 pot) {
        this.pot = pot;
    }
}
