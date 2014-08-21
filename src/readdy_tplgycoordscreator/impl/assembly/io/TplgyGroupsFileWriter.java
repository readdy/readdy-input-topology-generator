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
package readdy_tplgycoordscreator.impl.assembly.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import readdy.api.sim.core.particle.IParticle;
import readdy.api.sim.top.group.IGroup;
import readdy.impl.sim.ReaDDySimulator;
import readdy_tplgycoordscreator.api.io.ITplgyGroupsFileWriter;

/**
 *
 * @author schoeneberg
 */
public class TplgyGroupsFileWriter implements ITplgyGroupsFileWriter {
    /*
     * To change this template, choose Tools | Templates
     * and open the template in the editor.
     */
    
    private final int[] version = ReaDDySimulator.version;

    private BufferedWriter out;

    public void write(ArrayList<IGroup> tplgyGroups) {
        try {
            out.write("<tplgy_groups version=\""+version[0]+"."+version[1]+"\">\n");
            for (IGroup group : tplgyGroups) {
                printLine(group);
            }
            out.write("</tplgy_groups>");
            flush();
        } catch (IOException ex) {
            Logger.getLogger(TplgyGroupsFileWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void open(String outFileName) {

        try {
            out = new BufferedWriter(new FileWriter(outFileName));
        } catch (IOException e) {
        }
    }

//<p id="0" type="0" c="[0.0000,5.0000,2.5000]"/>
    private void printLine(IGroup group) {
        try {
            // skip the first data entry which is the particle ID and start with
            // the type


            out.write("<g id=\"" + group.get_id() + "\" ");
            out.write("type=\"" + group.get_typeId() + "\" internalAndParticleId=\"");

            ArrayList<IParticle> positionedMemberParticles = group.get_positionedMemberParticles();
            out.write("[");
            for (int i = 0; i < positionedMemberParticles.size(); i++) {
                int internalId = i;
                int particleId = positionedMemberParticles.get(i).get_id();


                if (i == positionedMemberParticles.size() - 1) {
                    out.write("[" + internalId + "," + particleId + "]");
                } else {
                    out.write("[" + internalId + "," + particleId + "];");
                }
            }
            out.write("]");
            out.write("\"/>");

            out.write("\n");
        } catch (IOException ex) {
            Logger.getLogger(TplgyGroupsFileWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void flush() {
        try {
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(TplgyGroupsFileWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void close() {
        try {
            out.flush();
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(TplgyGroupsFileWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
