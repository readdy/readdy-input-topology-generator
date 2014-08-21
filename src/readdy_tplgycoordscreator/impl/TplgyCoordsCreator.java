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
package readdy_tplgycoordscreator.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import readdy.api.io.in.par_global.IGlobalParameters;
import readdy.api.io.out.IDataReadyForOutput;
import readdy.api.sim.core.particle.IParticle;
import readdy.api.sim.core.particle.IParticleParameters;
import readdy.api.sim.core.pot.IPotentialManager;
import readdy.api.sim.core.pot.potentials.IPotential1;
import readdy.api.sim.top.group.IExtendedIdAndType;
import readdy.api.sim.top.group.IGroup;
import readdy.api.sim.top.group.IGroupParameters;
import readdy.impl.io.out.DataReadyForOutput;
import readdy.impl.io.out.TplgyCoordsFile_Writer;
import readdy.impl.io.out.XYZ_Writer;
import readdy.impl.sim.core.particle.Particle;
import readdy.impl.sim.top.group.ExtendedIdAndType;
import readdy.impl.sim.top.group.Group;
import readdy.impl.tools.StringTools;
import readdy_tplgycoordscreator.api.ITplgyCoordsCreator;
import readdy_tplgycoordscreator.api.coordCreator.IPotentialSpecificCoordsCreator;
import readdy_tplgycoordscreator.api.io.ITplgyGroupsFileWriter;
import readdy_tplgycoordscreator.impl.assembly.io.TplgyGroupsFileWriter;
import readdy_tplgycoordscreator.impl.coordCreator.P1_Cube_CoordCreator;
import readdy_tplgycoordscreator.impl.coordCreator.P1_Cylinder_CoordCreator;
import readdy_tplgycoordscreator.impl.coordCreator.P1_Disk_CoordCreator;
import readdy_tplgycoordscreator.impl.coordCreator.P1_Sphere_CoordCreator;
import readdy_tplgycoordscreator.impl.coordCreator.Rack_On_Sphere_CoordCreator;
import readdy_tplgycoordscreator.impl.coordCreator.Random_CoordCreator;
import readdy_tplgycoordscreator.impl.tools.CartesianCoordinates;
import statlab.base.util.DoubleArrays;


/**
 *
 * @author schoeneberg
 */
public class TplgyCoordsCreator implements ITplgyCoordsCreator {

    //--- given by input
    IPotentialManager potentialManager;
    IGroupParameters groupParameters;
    IParticleParameters particleParameters;
    HashMap<String, Integer> pOrGTypeName_to_copyNumber_map;
    HashMap<String, Boolean> typeName_to_pOrG_map = new HashMap();
    IGlobalParameters globalParameters;
    //--- computed
    HashMap<String, IPotential1> pTypeName_to_potDefiningParticlePosition = new HashMap();
    HashMap<String, GroupAnchor> gTypeName_to_groupAnchor = new HashMap();
    HashMap<String, IPotentialSpecificCoordsCreator> potTypeName_to_coordsCreator_map;
    ArrayList<IParticle> tplgyCoordinates = new ArrayList();
    ArrayList<IGroup> tplgyGroups = new ArrayList();
    boolean tplgyBuilt = false;
    ArrayList<ArrayList<String>> pDoc = new ArrayList();
    IDataReadyForOutput pDataReadyForOutput;
    IDataReadyForOutput gDataReadyForOutput;
    private String tplgyCoordsFileName;
    Random rand = new Random();
    String default_potTypeId = "";
    private String tplgyGroupsFileName;
    IPotentialSpecificCoordsCreator randomCoordCreator;
    IPotentialSpecificCoordsCreator p1_disk_CoordCreator;
    IPotentialSpecificCoordsCreator p1_cube_CoordCreator;
    IPotentialSpecificCoordsCreator p1_cylinder_CoordCreator;
    IPotentialSpecificCoordsCreator p1_sphere_CoordCreator;
    
    // what angle should the groups be turned to around their normal vectors
    // if this parameter is not given, they will be oriented randomly.
    Double orientationAngleAroundNormalForGroups = null;

    // ----- INPUT --------------------------------------------------------------------
    public void set_particleTypeCopyNumbers(HashMap<String, Integer> pTypeName_to_copyNumber_map) {
        this.pOrGTypeName_to_copyNumber_map = pTypeName_to_copyNumber_map;
    }

    public void set_particleParameters(IParticleParameters particleParameters) {
        this.particleParameters = particleParameters;
    }

    public void set_potentialManager(IPotentialManager potentialManager) {
        this.potentialManager = potentialManager;
    }

    public void set_paramGlobal(IGlobalParameters globalParameters) {
        this.globalParameters = globalParameters;
    }
    
     public void set_orientationAngleAroundNormalForGroups(double value) {
        orientationAngleAroundNormalForGroups = value;
    }

    // ----- SETUP --------------------------------------------------------------------
    private void setup() {
        // potential types that this creator is able to handle
        potTypeName_to_coordsCreator_map = new HashMap();
        randomCoordCreator = new Random_CoordCreator();
        randomCoordCreator.set_latticeBounds(globalParameters.get_latticeBounds());

        p1_disk_CoordCreator = new P1_Disk_CoordCreator();
        p1_disk_CoordCreator.set_latticeBounds(globalParameters.get_latticeBounds());

        p1_cube_CoordCreator = new P1_Cube_CoordCreator();
        p1_cube_CoordCreator.set_latticeBounds(globalParameters.get_latticeBounds());

        p1_cylinder_CoordCreator = new P1_Cylinder_CoordCreator();
        p1_cylinder_CoordCreator.set_latticeBounds(globalParameters.get_latticeBounds());

        p1_sphere_CoordCreator = new P1_Sphere_CoordCreator();
        p1_disk_CoordCreator.set_latticeBounds(globalParameters.get_latticeBounds());

        potTypeName_to_coordsCreator_map.put("", randomCoordCreator);
        potTypeName_to_coordsCreator_map.put("DISK", p1_disk_CoordCreator);
        potTypeName_to_coordsCreator_map.put("CUBE", p1_cube_CoordCreator);
        potTypeName_to_coordsCreator_map.put("CYLINDER", p1_cylinder_CoordCreator);
        potTypeName_to_coordsCreator_map.put("SPHERE", p1_sphere_CoordCreator);
    }

    // ----- PROCESSING --------------------------------------------------------------------
    public void createTplgyCoordinates() {
        if (allInputAvailable()) {

            setup();
            validateAllParticleAndGroupTypes();
            findForEachParticleAndGroupTypeThePotentialThatDefinesItsPosition();
            buildTplgy();

            tplgyBuilt = true;
        } else {
            throw new RuntimeException("not all input available");
        }
    }

    private boolean allInputAvailable() {
        return potentialManager != null
                && particleParameters != null
                && pOrGTypeName_to_copyNumber_map != null
                && globalParameters != null
                && groupParameters != null;
    }

    private void validateAllParticleAndGroupTypes() {
        for (String typeName : pOrGTypeName_to_copyNumber_map.keySet()) {

            if (particleParameters.doesTypeNameExist(typeName)) {
                typeName_to_pOrG_map.put(typeName, false);
            } else {
                if (groupParameters.doesTypeNameExist(typeName)) {
                    typeName_to_pOrG_map.put(typeName, true);
                } else {
                    throw new RuntimeException("the typeName '" + typeName + "' is unknown.");
                }
            }
        }
    }

    //--------------------------------------------------------------------------------------------------------------------
    private void findForEachParticleAndGroupTypeThePotentialThatDefinesItsPosition() {
        for (String typeName : pOrGTypeName_to_copyNumber_map.keySet()) {
            if (!isGroup(typeName)) {
                //particle

                // here we only have to find the potential that defines its position
                int pTypeId = particleParameters.getParticleTypeIdFromTypeName(typeName);
                IPotential1 potDefiningParticlePosition = getPotentialThatDefinesParticlePosition(pTypeId);
                this.pTypeName_to_potDefiningParticlePosition.put(typeName, potDefiningParticlePosition);
            } else {
                //group

                // here we first have to identify the anchor particle
                // afterwards we can find the potential that defines the position of the anchor particle
                int groupTypeId = groupParameters.getGroupTypeIdFromTypeName(typeName);
                IExtendedIdAndType groupAnchorParticle = getGroupAnchorParticle(groupTypeId);
                if (groupAnchorParticle != null) {
                    IPotential1 potdefiningAnchorPosition = getPotentialThatDefinesParticlePosition(groupAnchorParticle.get_type());
                    GroupAnchor groupAnchor = new GroupAnchor(groupAnchorParticle, potdefiningAnchorPosition);

                    this.gTypeName_to_groupAnchor.put(typeName, groupAnchor);
                }
            }
        }
    }

    private IPotential1 getPotentialThatDefinesParticlePosition(int pTypeId) {

        // if that is clear check by which potentials the particleType is affected
        // at first potentials are only considered here if they are of order 1
        IParticle dummyParticle = new Particle(0, pTypeId, new double[]{0, 0, 0});
        Iterator<IPotential1> potIterator = potentialManager.getPotentials(dummyParticle);
        ArrayList<IPotential1> potentialsAffectingParticle = new ArrayList();
        while (potIterator.hasNext()) {
            IPotential1 pot = potIterator.next();
            potentialsAffectingParticle.add(pot);
        }
        return decideByWhichPotentialTplgyShouldBeBuilt(potentialsAffectingParticle);
    }

    private IPotential1 decideByWhichPotentialTplgyShouldBeBuilt(ArrayList<IPotential1> potentialsAffectingParticle) {
        if (potentialsAffectingParticle.isEmpty()) {
            return null;
        } else {

            for (IPotential1 pot : potentialsAffectingParticle) {
                String potentialType = pot.get_type();
                String potentialSubType = pot.get_parameterMap().get("subtype");
                if (potentialType.contentEquals("DISK") && potentialSubType.contentEquals("attractive")) {
                    return pot;
                }
                if (potentialType.contentEquals("CUBE")) {
                    return pot;
                }
                if (potentialType.contentEquals("CYLINDER")) {
                    return pot;
                }
                if (potentialType.contentEquals("SPHERE")) {
                    return pot;
                }
            }
            return null;
        }
    }

    private boolean collisionDetected(int newTypeId, double[] newCoords) {

        for (IParticle p : tplgyCoordinates) {
            double dist = DoubleArrays.distance(newCoords, p.get_coords());
            double maxDist = particleParameters.get_pCollisionRadius(p.get_type(), newTypeId);
            //System.out.println("dist,maxDist "+dist+","+maxDist);
            if (dist < maxDist) {
                //System.out.println("collision detected!");
                return true;
            }
        }
        return false;

    }

    private boolean collisionDetected(ArrayList<IParticle> particleList) {

        for (IParticle p : tplgyCoordinates) {
            for (IParticle newP : particleList) {
                double dist = DoubleArrays.distance(newP.get_coords(), p.get_coords());
                double maxDist = particleParameters.get_pCollisionRadius(p.get_type(), newP.get_type());
                //System.out.println("dist,maxDist "+dist+","+maxDist);
                if (dist < maxDist) {
                    //System.out.println("collision detected!");
                    return true;
                }
            }
        }
        return false;

    }

    private boolean particleOutOfGeometryDetected(IPotential1 potential, ArrayList<IParticle> particleList) {
        if ("DISK".equals(potential.get_type())) {
            double[] potCenter = StringTools.splitArrayString_convertToDouble(potential.get_parameterMap().get("center"));
            double diskRadius = Double.parseDouble(potential.get_parameterMap().get("radius"));

            for (IParticle p : particleList) {
                double dist = DoubleArrays.distance(potCenter, p.get_coords());

                //System.out.println("dist,maxDist "+dist+","+maxDist);
                if (dist > diskRadius) {
                    //System.out.println("particle out of geometry detected!");
                    return true;
                }
            }
        }
        return false;

    }

    private IExtendedIdAndType getGroupAnchorParticle(int groupTypeId) {
        ArrayList<IExtendedIdAndType> possibleAnchors = new ArrayList();

        // find possible anchors
        // that means particles that are affected by potentials that are possible to be space defining
        // se decideByWhichPotentialTplgyShouldBeBuilt
        for (int i = 0; i < groupParameters.getBuildingBlocks(groupTypeId).size(); i++) {
            IExtendedIdAndType possibleAnchor = groupParameters.getBuildingBlocks(groupTypeId).get(i);
            // store the group internal id;
            possibleAnchor = new ExtendedIdAndType(possibleAnchor.get_isGroup(), i, possibleAnchor.get_type());
            if (possibleAnchor.get_isGroup() == false) {
                // it is a particle
                int pTypeId = possibleAnchor.get_type();
                IPotential1 anchorPotential = getPotentialThatDefinesParticlePosition(pTypeId);
                if (anchorPotential != null) {
                    possibleAnchors.add(possibleAnchor);
                }
            }

        }

        return decideWhichOfThePossibleOnesIsTheFinalAnchor(possibleAnchors);
    }

    private IExtendedIdAndType decideWhichOfThePossibleOnesIsTheFinalAnchor(ArrayList<IExtendedIdAndType> possibleAnchors) {
        // if there are multiple ones, thake randomly one.
        // this is just the first implementation
        if (possibleAnchors.isEmpty()) {
            return null;
        } else {
            int index = (int) Math.floor(rand.nextDouble() * possibleAnchors.size());
            return possibleAnchors.get(index);
        }
    }

    //--------------------------------------------------------------------------------------------------------------------
    private void buildTplgy() {
        System.out.println("=============================================================");
        System.out.println("start topology building...");
        System.out.println("-------------------------------------------------------------");
        // create random coordinates for particles where no potential
        // is specified. Create coordinates in the potentials otherwise
        pDoc.clear();
        // build the large ones first and then the smaller ones
        ArrayList<String> sortedTypeNamesWithRespectToTheirSize = getSortedTypeNames();

        for (String typeName : sortedTypeNamesWithRespectToTheirSize) {
            int copyNumber = pOrGTypeName_to_copyNumber_map.get(typeName);
            if (!isGroup(typeName)) {
                //particle
                System.out.println("-------------------------------------------------------------");
                System.out.println("build particle-tplgy for " + typeName + "...");

                // get the coordinates creator
                IPotential1 pot = null;
                if (pTypeName_to_potDefiningParticlePosition.containsKey(typeName) && pTypeName_to_potDefiningParticlePosition.get(typeName) != null) {
                    pot = pTypeName_to_potDefiningParticlePosition.get(typeName);
                }
                if (pot != null) {
                    System.out.println("... using the anchor potential: " + pot.get_name());
                } else {
                    System.out.println("... using the anchor potential: DEFAULT");
                }
                IPotentialSpecificCoordsCreator potSpecificCoordsCreator = get_potentialSpecificCoordsCreator(pot);

                // add the a particle with new coordinates to the tplgy
                int pTypeId = particleParameters.getParticleTypeIdFromTypeName(typeName);
                for (int i = 0; i < copyNumber; i++) {
                    double[] newCoordinates;
                    do {
                        newCoordinates = potSpecificCoordsCreator.get_nextCoordinates();
                    } while (collisionDetected(pTypeId, newCoordinates));

                    addParticleToTplgy(pTypeId, newCoordinates);
                    if (i % 10 == 0 || i == copyNumber - 1) {
                        System.out.println((i + 1) + "/" + copyNumber + " placed.");
                    }
                }
            } else {
                // group
                System.out.println("-------------------------------------------------------------");
                System.out.println("build group-tplgy for " + typeName + "...");


                int gTypeId = groupParameters.getGroupTypeIdFromTypeName(typeName);

                IPotential1 pot = null;


                IExtendedIdAndType groupAnchorParticle = groupParameters.getBuildingBlocks(gTypeId).get(0);
                if (gTypeName_to_groupAnchor.containsKey(typeName) && gTypeName_to_groupAnchor.get(typeName) != null) {
                    pot = gTypeName_to_groupAnchor.get(typeName).getAnchorPosDefiningPotential();
                    GroupAnchor groupAnchor = gTypeName_to_groupAnchor.get(typeName);
                    groupAnchor.print();
                    groupAnchorParticle = groupAnchor.getGroupAnchorParticle();
                }
                IPotentialSpecificCoordsCreator potSpecificCoordsCreator = get_potentialSpecificCoordsCreator(pot);


                for (int i = 0; i < copyNumber; i++) {
                    ArrayList<IParticle> positionedGroupMemberParticles = new ArrayList();
                    do {
                        String groupTypeName = groupParameters.getGroupTypeNameFromTypeId(gTypeId);

                        // rack positioning on a sphere:
                        // this only works because i know, that all racks are defined in their template
                        // coordinates in the way, that they stretch in the x dimension, and are 0 in z.
                        // if this would be different it no longer works.
                        if ("SPHERE".equals(pot.get_type()) && groupTypeName.contains("rack")) {
                            System.out.println("use special rack_on_sphere_coordCreator...");
                            Rack_On_Sphere_CoordCreator rack_on_sphere_coordCreator = new Rack_On_Sphere_CoordCreator();
                            HashMap<String, String> potentialParameters = pot.get_parameterMap();

                            rack_on_sphere_coordCreator.set_parameters(potentialParameters);
                            rack_on_sphere_coordCreator.set_potentialObject(pot);

                            positionedGroupMemberParticles = rack_on_sphere_coordCreator.positionRackOnSphere(
                                    groupParameters.getBuildingBlockTemplateCoordinates(gTypeId),
                                    groupParameters.getBuildingBlocks(gTypeId));

                        } else {
                            positionedGroupMemberParticles = generatePositionedMemberParticlesForGroup(gTypeId,
                                    groupAnchorParticle,
                                    potSpecificCoordsCreator.get_nextCoordinates(),
                                    potSpecificCoordsCreator.get_nextNormal());
                        }

                    } while (collisionDetected(positionedGroupMemberParticles) || particleOutOfGeometryDetected(pot, positionedGroupMemberParticles));
                    IGroup newGroup = addGroupToTplgy(gTypeId, positionedGroupMemberParticles);
                    if (i % 1 == 0 || i == copyNumber - 1) {
                        System.out.println((i + 1) + "/" + copyNumber + " placed.");
                    }
                }
            }
        }
        pDataReadyForOutput = new DataReadyForOutput(pDoc);
        System.out.println("-------------------------------------------------------------");
        System.out.println("tplgy built!");
        System.out.println("=============================================================");
    }

    // ----- OUTPUT --------------------------------------------------------------------
    public void set_tplgyCoordsFileName(String tplgyCoordsFileName) {
        this.tplgyCoordsFileName = tplgyCoordsFileName;
    }

    public void set_tplgyGroupsFileName(String tplgyGroupsFileName) {
        this.tplgyGroupsFileName = tplgyGroupsFileName;
    }

    public void write_tplgyCoordinates_file() {
        if (tplgyBuilt) {
            System.out.println("generate output...");

            XYZ_Writer fileWriter_xyz = new XYZ_Writer();
            String xyzFileName = tplgyCoordsFileName.replace(".xml", "") + ".xyz";
            fileWriter_xyz.open(xyzFileName);
            fileWriter_xyz.write(0, pDataReadyForOutput);
            fileWriter_xyz.close();
            System.out.println("xyz file:");
            File file2 = new File(xyzFileName);
            System.out.println(file2.getAbsolutePath());


            TplgyCoordsFile_Writer fileWriter = new TplgyCoordsFile_Writer();
            fileWriter.open(tplgyCoordsFileName);
            fileWriter.write(0, pDataReadyForOutput);
            fileWriter.close();
            System.out.println("tplgy_coordinates file:");
            File file = new File(tplgyCoordsFileName);
            System.out.println(file.getAbsolutePath());


            ITplgyGroupsFileWriter tplgyGroupsFileWriter = new TplgyGroupsFileWriter();
            tplgyGroupsFileWriter.open(tplgyGroupsFileName);
            tplgyGroupsFileWriter.write(tplgyGroups);
            tplgyGroupsFileWriter.close();
            System.out.println("tplgy_groups file:");
            File file3 = new File(tplgyGroupsFileName);
            System.out.println(file3.getAbsolutePath());

            System.out.println("... done.");
        } else {
            throw new RuntimeException("This method can not be called befor the building of the topology itself.");
        }
    }

    private boolean isGroup(String pTypeName) {
        return typeName_to_pOrG_map.get(pTypeName);
    }

    private IPotentialSpecificCoordsCreator get_potentialSpecificCoordsCreator(IPotential1 pot) {
        IPotentialSpecificCoordsCreator potSpecificCoordsCreator;
        if (pot != null) {
            HashMap<String, String> potentialParameters = pot.get_parameterMap();
            String potentialTypeName = pot.get_type();
            if (potTypeName_to_coordsCreator_map.containsKey(potentialTypeName)) {
                potSpecificCoordsCreator = potTypeName_to_coordsCreator_map.get(potentialTypeName);
                potSpecificCoordsCreator.set_parameters(potentialParameters);
                potSpecificCoordsCreator.set_potentialObject(pot);

            
            } else {
                potSpecificCoordsCreator = potTypeName_to_coordsCreator_map.get(default_potTypeId);
            }
        } else {
            potSpecificCoordsCreator = potTypeName_to_coordsCreator_map.get(default_potTypeId);
        }
        return potSpecificCoordsCreator;
    }

    private IParticle addParticleToTplgy(int pTypeId, double[] coordinates) {
        int newParticleId = tplgyCoordinates.size();
        IParticle newParticle = new Particle(newParticleId, pTypeId, coordinates);
        tplgyCoordinates.add(newParticle);
        ArrayList<String> line = new ArrayList();
        line.add(newParticle.get_id() + "");
        line.add(newParticle.get_type() + "");
        line.add(newParticle.get_coords()[0] + "");
        line.add(newParticle.get_coords()[1] + "");
        line.add(newParticle.get_coords()[2] + "");
        pDoc.add(line);

        return newParticle;

    }

    private ArrayList<IParticle> generatePositionedMemberParticlesForGroup(
            int gTypeId,
            IExtendedIdAndType groupAnchorParticle,
            double[] newCoordinatesForAnchor,
            double[] newNormalVectorForConstruction) {

        ArrayList<IParticle> positionedGroupMemberParticles = new ArrayList();


        double[] templateNormal = groupParameters.getGroupTemplateNormalVector(gTypeId);
        double[] templateOrigin = groupParameters.getGroupTemplateOriginVector(gTypeId);
        ArrayList<double[]> groupTemplateCoordinates = groupParameters.getBuildingBlockTemplateCoordinates(gTypeId);
        double[] templateAnchorCoords = groupTemplateCoordinates.get(groupAnchorParticle.get_id());


        // rotation parameters
        // ----------------------------------------------------------------------------
        // rotation due to the underlying potential perpendicular to the templateNormal
        // ----------------------------------------------------------------------------
        double rotationAngle = Math.acos(DoubleArrays.dot(templateNormal, newNormalVectorForConstruction) / (DoubleArrays.norm(templateNormal) * DoubleArrays.norm(newNormalVectorForConstruction)));

        // perpendicular vector to both normal vectors
        double[] rotationVector = CartesianCoordinates.crossProduct(templateNormal, newNormalVectorForConstruction);
        double[][] rotationMatrix = CartesianCoordinates.getArbitraryRotMatrix(rotationAngle, rotationVector);

        // ----------------------------------------------------------------------------
        // rotation due to random orientation of the molecule
        // ----------------------------------------------------------------------------
        // apply a rotation around the new normal vector
        //double randomOrientationRotationAngle = rand.nextDouble() * 2 * Math.PI;
        double rotationAngleAroundNormal = rand.nextDouble() * 2 * Math.PI;
        if(orientationAngleAroundNormalForGroups != null){
        //double rotationAngle = 0.375 * 2 * Math.PI;  
            rotationAngleAroundNormal= orientationAngleAroundNormalForGroups;
        }
        double[][] randomOrientationRotationMatrix = CartesianCoordinates.getArbitraryRotMatrix(rotationAngleAroundNormal, newNormalVectorForConstruction);


        // translation parameters
        //double[] translationVectorToNewOrigin = DoubleArrays.add(DoubleArrays.subtract(templateOrigin, templateAnchorCoords), newCoordinatesForAnchor);
        double[] oldAnchorToOriVector = DoubleArrays.subtract(templateOrigin, templateAnchorCoords);
        double[] translationVectorToNewOrigin = DoubleArrays.add(newCoordinatesForAnchor, CartesianCoordinates.matrixVectorMult_right(rotationMatrix, oldAnchorToOriVector));



        // iterate over all building blocks and create new particles
        for (int i = 0; i < groupParameters.getBuildingBlocks(gTypeId).size(); i++) {



            IExtendedIdAndType buildingBlock = groupParameters.getBuildingBlocks(gTypeId).get(i);


            if (buildingBlock.get_isGroup() == true) {
                throw new RuntimeException("this is not yet implemented and is probably left out anyways.");
            } else {
                int internalId = buildingBlock.get_id();
                int typeId = buildingBlock.get_type();
                double[] buildingBlockTemplateCoords = groupTemplateCoordinates.get(i);
                // apply rotation and translation
                /*
                double[] newParticleCoords_notRandomlyOriented = DoubleArrays.add(translationVectorToNewOrigin, CartesianCoordinates.matrixVectorMult_right(rotationMatrix, buildingBlockTemplateCoords));                

                double[] newParticleCoords = CartesianCoordinates.matrixVectorMult_right(randomOrientationRotationMatrix, newParticleCoords_notRandomlyOriented);
                IParticle newParticle = new Particle(-1, typeId, newParticleCoords_notRandomlyOriented);
                */

                double[] newParticleCoords_notRandomlyOriented_notTranslated = CartesianCoordinates.matrixVectorMult_right(rotationMatrix, buildingBlockTemplateCoords);                
                double[] newParticleCoords_randomlyOriented_notTranslated = CartesianCoordinates.matrixVectorMult_right(randomOrientationRotationMatrix, newParticleCoords_notRandomlyOriented_notTranslated);                

                double[] newParticleCoords_randomlyOriented_translated = DoubleArrays.add(translationVectorToNewOrigin, newParticleCoords_randomlyOriented_notTranslated);                
                
                
                IParticle newParticle = new Particle(-1, typeId, newParticleCoords_randomlyOriented_translated);
                positionedGroupMemberParticles.add(internalId, newParticle);
            }
        }

        return positionedGroupMemberParticles;

    }

    private IGroup addGroupToTplgy(int gTypeId, ArrayList<IParticle> positionedGroupMemberParticles) {
        ArrayList<IParticle> finalPositionedGroupMemberParticle = new ArrayList();
        for (IParticle p : positionedGroupMemberParticles) {
            int typeId = p.get_type();
            double[] coords = p.get_coords();
            finalPositionedGroupMemberParticle.add(addParticleToTplgy(typeId, coords));
        }


        int newGroupId = tplgyGroups.size();
        IGroup newGroup = new Group(newGroupId, gTypeId, finalPositionedGroupMemberParticle);
        tplgyGroups.add(newGroup);
        return newGroup;

    }

    public void set_groupParameters(IGroupParameters groupParameters) {
        this.groupParameters = groupParameters;
    }

    private ArrayList<String> getSortedTypeNames() {
        // i know this is not elegant but it works
        ArrayList<String> result = new ArrayList();
        ArrayList<String> toBeAssigned = new ArrayList();
        ArrayList<Double> toBeAssignedRadius = new ArrayList();
        for (String typeName : pOrGTypeName_to_copyNumber_map.keySet()) {
            if (!isGroup(typeName)) {
                toBeAssigned.add(typeName);
                toBeAssignedRadius.add(particleParameters.get_pCollisionRadius(particleParameters.getParticleTypeIdFromTypeName(typeName)));
            } else {
                // groups come first, period.
                toBeAssigned.add(typeName);
                int groupTypeId = groupParameters.getGroupTypeIdFromTypeName(typeName);
                int nGroupMembers = groupParameters.getBuildingBlocks(groupTypeId).size();
                toBeAssignedRadius.add(1000 + 2. * nGroupMembers);
            }

        }

        double currentLargestRadius = 0.;
        int currentLargestRadiusPosition = -1;
        while (!toBeAssigned.isEmpty()) {
            int i = 0;
            currentLargestRadius = 0.;
            for (double r : toBeAssignedRadius) {
                if (r >= currentLargestRadius) {
                    currentLargestRadius = r;
                    currentLargestRadiusPosition = i;
                }
                i++;
            }
            result.add(toBeAssigned.get(currentLargestRadiusPosition));
            toBeAssigned.remove(currentLargestRadiusPosition);
            toBeAssignedRadius.remove(currentLargestRadiusPosition);
        }
        System.out.println("<order of coordinate generation (larger entities first)>");
        for (String typeName : result) {
            System.out.println(typeName);
        }
        System.out.println("</order of coordinate generation (larger entities first)>");
        return result;


    }

   
}
class GroupAnchor {

    IExtendedIdAndType groupAnchorParticle;
    IPotential1 anchorPosDefiningPotential;

    public IPotential1 getAnchorPosDefiningPotential() {
        return anchorPosDefiningPotential;
    }

    public IExtendedIdAndType getGroupAnchorParticle() {
        return groupAnchorParticle;
    }

    GroupAnchor(IExtendedIdAndType groupAnchorParticle, IPotential1 anchorPosDefiningPotential) {
        this.groupAnchorParticle = groupAnchorParticle;
        this.anchorPosDefiningPotential = anchorPosDefiningPotential;
    }

    public void print() {
        System.out.println("<groupAnchor>");
        groupAnchorParticle.print();
        System.out.println("... using the anchor potential: " + anchorPosDefiningPotential.get_name());
        System.out.println("</groupAnchor>");
    }
}
