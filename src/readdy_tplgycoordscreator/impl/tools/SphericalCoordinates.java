package readdy_tplgycoordscreator.impl.tools;
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

import java.util.Random;



/**
 *
 * @author schoeneberg
 */
public class SphericalCoordinates {
    Random rand = new Random();
    
    private double inverseCDF_for_theta(double rand){
        return 2 * Math.asin(Math.sqrt(rand));
    }

    public double[] getUniformlyDistributedThetaAndPhiAngles(){    

        double theta = inverseCDF_for_theta(rand.nextDouble());
        double phi= rand.nextDouble()*2*Math.PI;
        return new double[]{theta,phi};
    }

    public double[] sphericalToCartesian(double r,double theta,double phi){
        double x = r*Math.sin(theta)*Math.cos(phi);
        double y = r*Math.sin(theta)*Math.sin(phi);
        double z = r*Math.cos(theta);
        return new double[]{x,y,z};
    }
    
    public double[] getUniformlyDistributedPointOnOriginCenteredSphere(double radius){
        double[] thetaPhi = getUniformlyDistributedThetaAndPhiAngles();
        return sphericalToCartesian(radius,thetaPhi[0],thetaPhi[1]);
    }
}
