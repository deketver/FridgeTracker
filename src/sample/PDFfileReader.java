package sample;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.List;
import java.lang.ProcessBuilder.*;

public class PDFfileReader
{
    /*

        public static void main(String a[]){
            try{

                String prg = "import sys\nprint int(sys.argv[1])+int(sys.argv[2])\n";
                BufferedWriter out = new BufferedWriter(new FileWriter("test1.py"));
                out.write(prg);
                out.close();
                int number1 = 10;
                int number2 = 32;

                ProcessBuilder pb = new ProcessBuilder("python","test1.py",""+number1,""+number2);
                Process p = pb.start();

                BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                int ret = Integer.parseInt(in.readLine());
                System.out.println("value is : "+ret);
            }catch(Exception e){System.out.println(e);}
        }

    public void callPythonSCript() throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder("python", resolvePythonScriptPath("hello.py"));
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();
        List<String> results = readProcessOutput(process.getInputStream());

        assertThat("Results should not be empty", results, is(not(empty())));
        assertThat("Results should contain output of script: ", results, hasItem(
                containsString("Hello Baeldung Readers!!")));

        int exitCode = process.waitFor();
        assertEquals("No errors should be detected", 0, exitCode);
    }


    public static void readKosik(String filename)
    {

    }
    */


}
