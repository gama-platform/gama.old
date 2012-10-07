package java2.testLibs;

import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PyInteger;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;
import org.python.core.codecs;

public class testJython {

    /**
    * @param args the command line arguments
    */
    public static void main(String[] args) throws PyException {

        // Create an instance of the PythonInterpreter
        PythonInterpreter interp = new PythonInterpreter();
//        System.out.println(codecs.getDefaultEncoding());
        
//        PyObject pEnc = interp.get("def search1(encoding): \ 
//    print 'search1: Searching for:', encoding
//    return None");
        //codecs.setDefaultEncoding("iso8859_2");

//        // The exec() method executes strings of code
//        interp.exec("import sys");
//        interp.exec("print sys");
//
//        // Set variable values within the PythonInterpreter instance
//        interp.set("a", new PyInteger(42));
//        interp.exec("print a");
//        interp.exec("x = 2+2");
//
//        // Obtain the value of an object from the PythonInterpreter and store it
//        // into a PyObject.
//        PyObject x = interp.get("x");
//        System.out.println("x: " + x); // iso-8859-1
       // interp.execfile("src/java2/testLibs/testEncodings.py");
//        interp.exec("import sys; sys.setdefaultencoding('latin-1')");

        
        interp = new PythonInterpreter(null, new PySystemState());

        PySystemState sysState = Py.getSystemState();
        sysState.path.append(new PyString("src/python/"));
        //sysState.path.append(new PyString("files/gen/wiki2wiki/template/"));

//        interp.exec("import subprocess");
//        interp.exec("subprocess.Popen('python /home/bgaudou/workspaceEclipse/msi.gama.documentation/src/python/statwiki.py')");
        
        sysState.argv.clear ();
        sysState.argv.append (new PyString ("statwiki.py"));      
        sysState.argv.append (new PyString ("--build"));
        sysState.argv.append (new PyString ("--d=files/gen/wiki2wiki"));        
        interp.execfile("src/python/statwiki.py");
    }

}
