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
package readdy_tplgycoordscreator;

import java.util.HashMap;
import java.util.HashSet;
import readdy_tplgycoordscreator.api.ITplgyCoordsCreator;
import readdy_tplgycoordscreator.api.assembly.ITplgyCoordsCreatorFactory;
import readdy_tplgycoordscreator.impl.assembly.TplgyCoordsCreatorFactory;

/**
 *
 * @author schoeneberg
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    private static String[] essentialInputKeys;
    private static String[] optionalInputKeys;

    public static void main(String[] args) {
        System.out.println("<============<ReaDDy_TopologyCoordsCreator v=\"1.1\">===========>");
        System.out.println("<= (c) 2010-2013, Johannes Schoeneberg, Frank Noe, FU Berlin =>");
        System.out.println("<=============================================================>");
        ITplgyCoordsCreatorFactory tplgyCoordsCreatorFactory = new TplgyCoordsCreatorFactory();
        essentialInputKeys = tplgyCoordsCreatorFactory.getEssentialInputKeys();
        optionalInputKeys = tplgyCoordsCreatorFactory.getOptionalInputKeys();
        HashMap<String, String> inputValues = parseCommandLineArguments(args, essentialInputKeys,optionalInputKeys);

        tplgyCoordsCreatorFactory.set_inputValues(inputValues);
        ITplgyCoordsCreator coordsCreator = tplgyCoordsCreatorFactory.createTplgyCoordsCreator();
        coordsCreator.createTplgyCoordinates();
        coordsCreator.write_tplgyCoordinates_file();
        System.out.println("<===========</ReaDDy_TopologyCoordsCreator v=\"1.0\">===========>");
    }

    private static HashMap<String, String> parseCommandLineArguments(String[] args, String[] essentialInputFileKeys, String[] optionalInputFileKeys) {
        HashMap<String, String> inputValues = new HashMap();
        HashSet<String> essentialInputFileKeysSet = new HashSet();
        HashSet<String> optionalInputFileKeysSet = new HashSet();

        for (String essentialKey : essentialInputFileKeys) {
            essentialInputFileKeysSet.add("-" + essentialKey);
        }
        
        for (String optionalKey : optionalInputFileKeys) {
            optionalInputFileKeysSet.add("-" + optionalKey);
        }
        

        String currentFoundKey = "";

        for (String arg : args) {

            // first find the key
            if (currentFoundKey.contentEquals("")) {
                if (essentialInputFileKeysSet.contains(arg) || optionalInputFileKeysSet.contains(arg)) {
                    currentFoundKey = arg.replaceAll("-", "");
                    continue;
                }
            } else {
                inputValues.put(currentFoundKey, arg);
                currentFoundKey = "";
            }

        }

        for (String essentialKey : essentialInputFileKeys) {
            if (!inputValues.containsKey(essentialKey)) {
                printHelp();
                throw new RuntimeException("the essential parameter '-" + essentialKey + "' is not given.");
            }
        }

        return inputValues;
    }

    private static void printHelp() {
        System.out.println("help:");
        System.out.println("the following input keys are essential:");
        for (String s : essentialInputKeys) {
            System.out.println("-" + s);
        }
        System.out.println("the following input keys are optional:");
        for (String s : optionalInputKeys) {
            System.out.println("-" + s);
        }
    }
}
