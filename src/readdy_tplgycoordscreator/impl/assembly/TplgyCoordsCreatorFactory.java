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
package readdy_tplgycoordscreator.impl.assembly;

import java.io.File;
import readdy_tplgycoordscreator.impl.TplgyCoordsCreator;
import readdy.api.assembly.IPotentialManagerFactory;
import readdy.api.io.in.tpl_pot.ITplgyPotentialsFileData;
import readdy.api.sim.core.pot.IPotentialManager;
import readdy.impl.assembly.PotentialManagerFactory;
import readdy.impl.io.in.tpl_pot.TplgyPotentialsFileParser;
import java.util.HashMap;
import readdy.api.assembly.IGroupParametersFactory;
import readdy.api.assembly.IParticleParametersFactory;
import readdy.api.assembly.IPotentialFactory;
import readdy.api.assembly.IPotentialInventoryFactory;
import readdy.api.io.in.par_global.IGlobalParameters;
import readdy.api.io.in.par_global.IParamGlobalFileParser;
import readdy.api.io.in.par_group.IParamGroupsFileData;
import readdy.api.io.in.par_group.IParamGroupsFileParser;
import readdy.api.io.in.par_particle.IParamParticlesFileData;
import readdy.api.io.in.par_particle.IParamParticlesFileParser;
import readdy.api.sim.core.particle.IParticleParameters;
import readdy.api.sim.core.pot.IPotentialInventory;
import readdy.api.sim.top.group.IGroupParameters;
import readdy.impl.assembly.GroupParametersFactory;
import readdy.impl.assembly.ParticleParametersFactory;
import readdy.impl.assembly.PotentialFactory;
import readdy.impl.assembly.PotentialInventoryFactory;
import readdy.impl.io.in.par_global.ParamGlobalFileParser;
import readdy.impl.io.in.par_group.ParamGroupsFileParser;
import readdy.impl.io.in.par_particle.ParamParticlesFileParser;
import readdy_tplgycoordscreator.api.ITplgyCoordsCreator;
import readdy_tplgycoordscreator.api.assembly.ITplgyCoordsCreatorFactory;
import readdy_tplgycoordscreator.api.io.IParticleNumberInput_Reader;
import readdy_tplgycoordscreator.impl.assembly.io.ParticleNumberInput_Reader;

/**
 *
 * @author schoeneberg
 */
public class TplgyCoordsCreatorFactory implements ITplgyCoordsCreatorFactory {

    IPotentialManager potentialManager;
    IParticleParameters particleParameters;
    HashMap<String, Integer> pTypeName_to_copyNumber_map;
    IGlobalParameters globalParameters;
    IGroupParameters groupParameters;
    HashMap<String, String> inputValues = new HashMap();
    String[] essentialInputKeys = new String[]{
        "input_copyNumbers",
        "output_tplgyCoords",
        "output_tplgyGroups",
        "param_global",
        "param_particles",
        "param_groups",
        "tplgy_potentials"};
    String[] optionalInputKeys = new String[]{
        "orientationAngleAroundNormalForGroups" /*double*/};

    
     
    public String[] getEssentialInputKeys() {
        return essentialInputKeys;
    }
    
     public String[] getOptionalInputKeys() {
        return optionalInputKeys;
    }

    public void set_inputValues(HashMap<String, String> inputValues) {
        this.inputValues = inputValues;
    }

    public ITplgyCoordsCreator createTplgyCoordsCreator() {

        System.out.println("setup TopologyCoordsCreator...");
        //##############################################################################

        System.out.println("parse global_parameters...");
        IParamGlobalFileParser paramGlobalFileParser = new ParamGlobalFileParser();
        paramGlobalFileParser.parse(inputValues.get("param_global"));
        globalParameters = paramGlobalFileParser.get_globalParameters();


        //##############################################################################
        // ParticleParameters
        //##############################################################################

        System.out.println("parse param_particles...");

        IParamParticlesFileParser paramParticlesFileParser = new ParamParticlesFileParser();
        paramParticlesFileParser.parse(inputValues.get("param_particles"));
        IParamParticlesFileData paramParticlesFileData = paramParticlesFileParser.get_paramParticlesFileData();

        IParticleParametersFactory particleParamFactory = new ParticleParametersFactory();
        particleParamFactory.set_globalParameters(globalParameters);
        particleParamFactory.set_paramParticlesFileData(paramParticlesFileData);
        particleParameters = particleParamFactory.createParticleParameters();



        //##############################################################################
        // PotentialManager
        //##############################################################################

        IPotentialFactory potentialFactory = new PotentialFactory();
        IPotentialInventoryFactory potInvFactory = new PotentialInventoryFactory();
        potInvFactory.set_potentialFactory(potentialFactory);
        IPotentialInventory potentialInventory = potInvFactory.createPotentialInventory();


        TplgyPotentialsFileParser tplgyPotentialsFileParser = new TplgyPotentialsFileParser();
        tplgyPotentialsFileParser.parse(inputValues.get("tplgy_potentials"));
        ITplgyPotentialsFileData potFileData = tplgyPotentialsFileParser.get_tplgyPotentialsFileData();

        IPotentialManagerFactory potentialManagerFactory = new PotentialManagerFactory();

        potentialManagerFactory.set_potentialInventory(potentialInventory);
        potentialManagerFactory.set_tplgyPotentialsFileData(potFileData);
        potentialManagerFactory.set_particleParameters(particleParameters);

        potentialManager = potentialManagerFactory.createPotentialManager();
        System.out.println("... potentialManager generated.");

        //##############################################################################
        // param Groups
        //##############################################################################

        System.out.println("parse param_groups...");
        IParamGroupsFileParser paramGroupsFileParser = new ParamGroupsFileParser();
        paramGroupsFileParser.parse(inputValues.get("param_groups"));
        IParamGroupsFileData paramGroupsFileData = paramGroupsFileParser.get_paramGroupsFileData();


        IGroupParametersFactory groupParametersFactory = new GroupParametersFactory();
        groupParametersFactory.set_paramGroupsFileData(paramGroupsFileData);
        groupParametersFactory.set_particleParameters(particleParameters);
        groupParametersFactory.set_potentialInventory(potentialInventory);
        groupParameters = groupParametersFactory.createGroupParameters();


        //##############################################################################
        // Copy Number Input
        //##############################################################################
        System.out.println("parse input_copyNumbers...");
        IParticleNumberInput_Reader particleNumberInputReader = new ParticleNumberInput_Reader();
        File inputFile = new File(inputValues.get("input_copyNumbers"));
        System.out.println("parsing input_copyNumbers file: " + inputFile.getAbsolutePath());
        particleNumberInputReader.readFile(inputValues.get("input_copyNumbers"));
        pTypeName_to_copyNumber_map = particleNumberInputReader.get_particleId_to_copyNumber_map();
        System.out.println("... done.");


        //##############################################################################
        // Assemble everything
        //##############################################################################



        ITplgyCoordsCreator tplgyCoordsCreator = new TplgyCoordsCreator();
        tplgyCoordsCreator.set_paramGlobal(globalParameters);
        tplgyCoordsCreator.set_particleParameters(particleParameters);
        tplgyCoordsCreator.set_particleTypeCopyNumbers(pTypeName_to_copyNumber_map);
        tplgyCoordsCreator.set_potentialManager(potentialManager);
        tplgyCoordsCreator.set_groupParameters(groupParameters);
        tplgyCoordsCreator.set_tplgyCoordsFileName(inputValues.get("output_tplgyCoords"));
        tplgyCoordsCreator.set_tplgyGroupsFileName(inputValues.get("output_tplgyGroups"));
        if(inputValues.containsKey("orientationAngleAroundNormalForGroups")){
        tplgyCoordsCreator.set_orientationAngleAroundNormalForGroups(Double.parseDouble(inputValues.get("orientationAngleAroundNormalForGroups")));
        }

        System.out.println("... TopologyCoordsCreator setup done.");
        return tplgyCoordsCreator;
    }

   
}
