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
import readdy.impl.sim.core.pot.potentials.P1_Cube;
import readdy_tplgycoordscreator.api.coordCreator.IPotentialSpecificCoordsCreator;
import readdy.impl.tools.StringTools;
import statlab.base.util.DoubleArrays;

/**
 *
 * @author schoeneberg
 */
public class P1_Cube_CoordCreator implements IPotentialSpecificCoordsCreator {

    IPotential1 pot;
    double[][] latticeBounds;
    HashMap<String, String> parameters;
    String[] essentialParameterKeys=P1_Cube.essentialParameterKeys;
    double[] origin, extension, multiplier;
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
        if (essentialParameterKeys != null) {
            this.parameters = potentialParameters;
            

            this.origin = StringTools.splitArrayString_convertToDouble(parameters.get(essentialParameterKeys[5]));
            if (origin.length != 3) {
                throw new RuntimeException("center vector dimension != 3");
            }

            this.extension = StringTools.splitArrayString_convertToDouble(parameters.get(essentialParameterKeys[6]));
            if (origin.length != 3) {
                throw new RuntimeException("extension vector dimension != 3");
            }



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

    private boolean validateCoords(double[] coords) {

        return true;

    }

    public double[] get_nextNormal() {
        double[] newVector = new double[]{
            rand.nextDouble(),
            rand.nextDouble(),
            rand.nextDouble()};
       
        newVector = DoubleArrays.normalize(newVector);
        
        return newVector;
    }

    public void set_potentialObject(IPotential1 pot) {
        this.pot = pot;
    }
}
