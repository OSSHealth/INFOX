import DependencyGraph.AnalyzingRepository;
import Util.ParsingMacros;
import org.rosuda.JRI.Rengine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


/**
 * Created by shuruiz on 6/2/2016.
 */
public class Main {


    static final String FS = File.separator;

    /**
     * Main method for testing the INFOX method
     *
     * @param args
     */
    public static void main(String[] args) {
        /**  Step 1， parsing source code to find independent macros, and then generate different macro combinations as ground truth**/
//        ParsingMacros parsingMacros = new ParsingMacros();
//        parsingMacros.generatingTestCases_differentMacroCombination();

        /** Step 2, running tests from each generated folders from step 1 **/

        /**     Initialize Rengine to call igraph R library.      **/
        Rengine re = new Rengine(new String[]{"--vanilla"}, false, null);
        if (!re.waitForR()) {
            System.out.println("Cannot load R");
            return;
        }
        AnalyzingRepository analyzeRepo = new AnalyzingRepository();
        /**  parse different repositories under testCasesDir  **/
        try {

            String testCasesDir = "/Users/shuruiz/Work/MarlinRepo/testMarlin";

            Files.walk(Paths.get(testCasesDir), 1).forEach(filePath -> {
                if (Files.isDirectory(filePath) && !filePath.toString().equals(testCasesDir)) {
                    String sourcecodeDir = filePath.toString() + FS;

                    for (int numOfTargetMacro =3; numOfTargetMacro <= 3; numOfTargetMacro++) {
                        /** generating the parameters for creating dependency graph  **/
                        ArrayList<int[]> parameterArray = getParameterSetting(numOfTargetMacro);

                        for (int[] param : parameterArray) {
                            String analysisDirName = "";
                            if (param[5] == 1) {
                                analysisDirName = "testINFOX";
                            } else if (param[5] == 2) {
                                analysisDirName = "testMS";
                            } else if (param[5] == 3) {
                                analysisDirName = "testMS_plus_CF_Hierachy";
                            } else if (param[5] == 4) {
                                analysisDirName = "testINFOX_NO_DefUse";
                            } else if (param[5] == 5) {
                                analysisDirName = "testINFOX_NO_ControlF";
                            } else if (param[5] == 6) {
                                analysisDirName = "testINFOX_NO_Hierarchy";
                            } else if (param[5] == 7) {
                                analysisDirName = "testINFOX_NO_Consec";
                            } else if (param[5] == 8) {
                                analysisDirName = "testMS_NO_Consec";
                            }

                            /**  testCase specifys the repository that need to be parsed.  **/
                            //TODO: set subdir name for multiple tests
                            for (int i = 2; i <= 2; i++) {
                                /** 1 -- INFOX,
                                 *  2--MS,
                                 *  3--MS+CF+HIE (NO spliting, joining),
                                 *  4--INFOX-(DEF_USE)
                                 *  5--INFOX-(CONTROL_FLOW),
                                 *  6--INFOX-(HIERARCHY)
                                 *  7--INFOX-(Consecutive)
                                 *  8--MS-(Consecutive)
                                 *  **/
                                String testCaseDir = sourcecodeDir + analysisDirName + FS + numOfTargetMacro + "macros" + FS + i + FS;
                                String testCaseDir_macrosFromOneFile = sourcecodeDir + analysisDirName + FS + numOfTargetMacro + "macros_oneFile" + FS + i + FS;

                                System.out.println(testCaseDir);
                                analyzeRepo.analyzeRepository(sourcecodeDir, analysisDirName, testCaseDir, param, re);
                                System.out.println(testCaseDir_macrosFromOneFile);
                                analyzeRepo.analyzeRepository(sourcecodeDir, analysisDirName, testCaseDir_macrosFromOneFile, param, re);
                            }
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * This function generates all the possible combination of the parameters for generating different dependency graphs.
     * the parameters array stores different parameters for creating dependency graphs.
     * 0. numOfTargetMacro
     * 1. numberOfCuts
     * 2. groundTruth : IFDEF / REAL  (1-- ifdef, 0 --- Real)
     * 3. Consecutive lines: T/F  (1/0)
     * 4. Directed Edge: T/F (1/0)
     * 5. 8 different methods
     *
     * @param numOfTargetMacro int (the number of macros randomly selected from marco list, equals to size of targetMacroList)
     * @return parameterArray
     */
    private static ArrayList<int[]> getParameterSetting(int numOfTargetMacro) {

        ArrayList<int[]> parameterArray = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            int[] param = new int[6];
            param[0] = numOfTargetMacro;
//            param[1] = numOfTargetMacro + 3;  // int numberOfCuts = numOfTargetMacro + 3;
            param[1] = 3;  // int numberOfCuts = numOfTargetMacro + 3;
            param[2] = 1;
            param[3] = 1;
            param[4] = 0;
            param[5] = i;
//            param[5] = 2;
            parameterArray.add(param);

        }
        return parameterArray;
    }


}
