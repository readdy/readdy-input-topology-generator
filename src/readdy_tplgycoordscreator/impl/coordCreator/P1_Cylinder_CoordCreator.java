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
import readdy.impl.sim.core.pot.potentials.P1_Cylinder;
import readdy_tplgycoordscreator.api.coordCreator.IPotentialSpecificCoordsCreator;
import readdy.impl.tools.StringTools;

/**
 *
 * @author schoeneberg
 */
public class P1_Cylinder_CoordCreator implements IPotentialSpecificCoordsCreator {

    /*
     * the essential parameter keys for a cylinder potential
     *private String[] essentialParameterKeys =
    new String[]{"id", "name", "type", "subtype", "forceConst","center", "normal", "radius", "height"};
     */
    IPotential1 pot;
    double[][] latticeBounds;
    HashMap<String, String> parameters;
    String[] essentialParameterKeys=P1_Cylinder.essentialParameterKeys;
    double[] center, origin, extension;
    double radius, height;
    Random rand = new Random();

    public double[] get_nextCoordinates() {
        int trialCounter = 0;
        int maxNTrials = 10000;
        double[] coords = new double[3];
        boolean valid = false;
        while (!valid) {
            coords = generateNewCoordinates();
            valid = validateCoords(coords);
            trialCounter++;
            if (trialCounter > maxNTrials) {
                throw new RuntimeException("not possible to generate proper coordinates. Maybe the system is too dense.");
            }
        }
        return coords;
    }

    public void set_latticeBounds(double[][] latticeBounds) {
        this.latticeBounds = latticeBounds;
    }


    public void set_parameters(HashMap<String, String> potentialParameters) {
        if (essentialParameterKeys != null) {
            this.parameters = potentialParameters;


            this.center = StringTools.splitArrayString_convertToDouble(parameters.get(essentialParameterKeys[5]));
            if (center.length != 3) {
                throw new RuntimeException("center vector dimension != 3");
            }



            this.radius = Double.parseDouble(parameters.get(essentialParameterKeys[7]));
            this.height = Double.parseDouble(parameters.get(essentialParameterKeys[8]));

            /*
             * Origin here means the origin of a box that in any case encloses
             * the cylinder.
             */
            double halfEdgeLength = radius + 0.5 * height;
            origin = new double[]{center[0] - halfEdgeLength,
                        center[1] - halfEdgeLength,
                        center[2] - halfEdgeLength};
            /*
             * extension means here the edge length of the box that should enclose
             * the cylinder
             */
            extension = new double[]{2 * halfEdgeLength,
                        2 * halfEdgeLength,
                        2 * halfEdgeLength};

        } else {
            throw new RuntimeException("essentialParameterKeys are not present. Tey have to be set before.");
        }
    }

    private double[] generateNewCoordinates() {
        double[] newCoord = new double[]{
            rand.nextDouble() * extension[0] + origin[0],
            rand.nextDouble() * extension[1] + origin[1],
            rand.nextDouble() * extension[2] + origin[2]};
        return newCoord;
    }

    /**
     * in this case we generate particle within a box around the cylinder.
     * Particles are rejected if they feel a higher potential energy
     * related to their generated coordinate than 0.1 . 
     * @param coords
     * @return
     */
    private boolean validateCoords(double[] coords) {
        pot.set_coordinates(coords, 0);
        double[] gradient = pot.getGradient();
        for (int i = 0; i < gradient.length; i++) {
            if (Math.abs(gradient[i]) > 0.01) {
                return false;
            }
        }
        return true;
    }

    public double[] get_nextNormal() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void set_potentialObject(IPotential1 pot) {
        this.pot = pot;
    }
}
