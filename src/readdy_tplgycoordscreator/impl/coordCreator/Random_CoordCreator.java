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
import readdy_tplgycoordscreator.api.coordCreator.IPotentialSpecificCoordsCreator;

/**
 *
 * @author schoeneberg
 */
public class Random_CoordCreator implements IPotentialSpecificCoordsCreator {

    IPotential1 pot;
    Random rand = new Random();
    double[][] latticeBounds;
    double[][] minAndLength;
    HashMap<String, String> potentialParameters;

    public double[] get_nextCoordinates() {
        double[] coords = new double[3];
        for (int i = 0; i < coords.length; i++) {
            double[] minAndLengthInThisDimension = minAndLength[i];
            double min = minAndLengthInThisDimension[0];
            double length = minAndLengthInThisDimension[1];
            double random = rand.nextDouble();
            coords[i] = min + random * length;
        }
        return coords;
    }

    public void set_latticeBounds(double[][] latticeBounds) {
        this.latticeBounds = latticeBounds;
        minAndLength = new double[latticeBounds.length][latticeBounds[0].length];
        for (int i = 0; i < latticeBounds.length; i++) {
            double[] latticeBoundsInThisDimension = latticeBounds[i];
            double border1 = latticeBoundsInThisDimension[0];
            double border2 = latticeBoundsInThisDimension[1];
            double min = border1 > border2 ? border2 : border1;
            double length = Math.abs(border2 - border1);
            minAndLength[i] = new double[]{min, length};
        }
    }

    public void set_parameters(HashMap<String, String> potentialParameters) {
        // this functionality is not necessary in this class
    }


    public double[] get_nextNormal() {
        return new double[]{rand.nextDouble(), rand.nextDouble(), rand.nextDouble()};
    }

    public void set_potentialObject(IPotential1 pot) {
        this.pot = pot;
    }
}
