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
package readdy_tplgycoordscreator.api;

import java.util.HashMap;
import readdy.api.io.in.par_global.IGlobalParameters;
import readdy.api.sim.core.particle.IParticleParameters;
import readdy.api.sim.core.pot.IPotentialManager;
import readdy.api.sim.top.group.IGroupParameters;

/**
 *
 * @author schoeneberg
 */
public interface ITplgyCoordsCreator {

    // -- input
    public void set_particleTypeCopyNumbers(HashMap<String, Integer> pTypeName_to_copyNumber_map);

    public void set_paramGlobal(IGlobalParameters globalParameters);

    public void set_particleParameters(IParticleParameters particleParameters);

    public void set_groupParameters(IGroupParameters groupParameters);

    public void set_potentialManager(IPotentialManager potentialManager);

    public void set_tplgyCoordsFileName(String tplgyCoordsFileName);

    public void set_tplgyGroupsFileName(String tplgyGroupsFileName);
    
    // optional input
    public void set_orientationAngleAroundNormalForGroups(double value);

    // -- create the topology
    public void createTplgyCoordinates();

    // -- ouput possibilities
    public void write_tplgyCoordinates_file();
    // maybe later
    //public ITplgyCoordinatesFileData get_tplgyCoordinatesFileData();
}
