/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package readdy_tplgycoordscreator.impl;


import java.util.HashMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import readdy.api.io.in.par_global.IGlobalParameters;
import readdy.api.sim.core.particle.IParticleParameters;
import readdy.api.sim.core.pot.IPotentialManager;
import readdy.api.sim.top.group.IGroupParameters;
import readdy_tplgycoordscreator.api.ITplgyCoordsCreator;
import readdy_tplgycoordscreator.api.assembly.ITplgyCoordsCreatorFactory;
import readdy_tplgycoordscreator.impl.assembly.TplgyCoordsCreatorFactory;

/**
 *
 * @author schoeneberg
 */
public class TplgyCoordsCreatorTest {



    private static IPotentialManager potentialManager;
    private static IParticleParameters particleParameters;
    private static IGroupParameters groupParameters;
    private static HashMap<Integer, Integer> pTypId_to_copyNumber_map;
    private static IGlobalParameters globalParameters;
    private static ITplgyCoordsCreator tplgyCoordsCreator;

    @BeforeClass
    public static void setUpClass() throws Exception {
        //##############################################################################
        // GlobalParameters
        //##############################################################################

        
        String paramGlobalFilename = "./test/testInputFiles/param_global.xml";
        

        String paramParticlesFilename = "./test/testInputFiles/param_particles.xml";

        String paramGroupsFilename = "./test/testInputFiles/param_groups.xml";

        String tplPotentialsFilename = "./test/testInputFiles/param_potentialTemplates.xml";
  

        String tplgyPotentialFilename = "./test/testInputFiles/tplgy_potentials.xml";
      
        String copyNumberInputFilename = "./test/testInputFiles/in_copyNumbers.csv";

        String outputTplgyCoordsFilename = "./test/testInputFiles/x_racks_tplgy_coordinates.xml";

        String outputTplgyGroupsFilename = "./test/testInputFiles/x_racks_tplgy_groups.xml";

        //##############################################################################
        // TplgyCoordsCreator
        //##############################################################################

        ITplgyCoordsCreatorFactory tplgyCoordsCreatorFactory = new TplgyCoordsCreatorFactory();
        HashMap<String,String>inputFilenames = new HashMap();

        inputFilenames.put("input_copyNumbers",copyNumberInputFilename);
        inputFilenames.put("output_tplgyCoords",outputTplgyCoordsFilename);
        inputFilenames.put("output_tplgyGroups",outputTplgyGroupsFilename);
        inputFilenames.put("param_global",paramGlobalFilename);
        inputFilenames.put("param_particles",paramParticlesFilename);
        inputFilenames.put("param_groups",paramGroupsFilename);
        inputFilenames.put("tplgy_potentials",tplgyPotentialFilename);
        inputFilenames.put("param_potentialTemplates",tplPotentialsFilename);
        tplgyCoordsCreatorFactory.set_inputValues(inputFilenames);

        tplgyCoordsCreator = tplgyCoordsCreatorFactory.createTplgyCoordsCreator();
        tplgyCoordsCreator.createTplgyCoordinates();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {
    }

    /**
     * Test of createTplgyCoordinates method, of class TplgyCoordsCreator.
     */
    @Test
    public void test_write_tplgyCoordinates_file() {
        System.out.println("write_tplgyCoordinates_file");
        tplgyCoordsCreator.write_tplgyCoordinates_file();

    }

}
