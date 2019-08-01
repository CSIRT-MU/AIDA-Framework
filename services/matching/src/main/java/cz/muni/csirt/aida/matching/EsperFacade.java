package cz.muni.csirt.aida.matching;

import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.module.Module;
import com.espertech.esper.common.client.module.ModuleItem;
import com.espertech.esper.common.client.soda.EPStatementObjectModel;
import com.espertech.esper.compiler.client.CompilerArguments;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.compiler.client.EPCompiler;
import com.espertech.esper.compiler.client.EPCompilerProvider;
import com.espertech.esper.runtime.client.EPDeployException;
import com.espertech.esper.runtime.client.EPDeployment;
import com.espertech.esper.runtime.client.EPRuntime;

import java.util.Arrays;
import java.util.List;

public class EsperFacade {

    private EsperFacade() {
    }

    public static Module wrapInModule(List<EPStatementObjectModel> statements) {
        Module module = new Module();
        for (EPStatementObjectModel statement : statements) {
            module.getItems().add(new ModuleItem(statement));
        }
        return module;
    }

    public static Module wrapInModule(EPStatementObjectModel... statement) {
        return wrapInModule(Arrays.asList(statement));
    }

    public static EPCompiled compile(Module module, CompilerArguments args) {
        EPCompiler compiler = EPCompilerProvider.getCompiler();

        try {
            return compiler.compile(module, args);
        }
        catch (EPCompileException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static EPDeployment deploy(EPRuntime runtime, EPCompiled compiled) {
        try {
            return runtime.getDeploymentService().deploy(compiled);
        }
        catch (EPDeployException ex) {
            throw new RuntimeException(ex);
        }
    }
}
