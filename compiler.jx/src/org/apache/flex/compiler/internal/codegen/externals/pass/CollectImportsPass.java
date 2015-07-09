package org.apache.flex.compiler.internal.codegen.externals.pass;

import java.util.List;
import java.util.Map;

import org.apache.flex.compiler.internal.codegen.externals.reference.*;

import com.google.javascript.jscomp.AbstractCompiler;
import com.google.javascript.jscomp.NodeTraversal;
import com.google.javascript.rhino.Node;

/**
 * @author: Frederic Thomas Date: 05/07/2015 Time: 18:16
 */
public class CollectImportsPass extends AbstractCompilerPass
{
    public CollectImportsPass(final ReferenceModel model, AbstractCompiler compiler)
    {
        super(model, compiler);
    }

    @Override
    public void process(Node externs, Node root)
    {
        for (ClassReference reference : model.getClasses())
        {
            collectClassImports(reference);
        }

        for (FunctionReference reference : model.getFunctions())
        {
            collectFunctionImports(reference);
        }
    }

    private void collectClassImports(ClassReference reference)
    {
        final MethodReference constructor = reference.getConstructor();
        final List<ClassReference> superClasses = reference.getSuperClasses();
        final List<ClassReference> interfaces = reference.getInterfaces();
        final List<ClassReference> extendedInterfaces = reference.getExtendedInterfaces();
        final Map<String, FieldReference> fields = reference.getFields();
        final Map<String, MethodReference> methods = reference.getMethods();

        for (ClassReference superClass : superClasses)
        {
            if (model.isExcludedClass(superClass) == null)
            {
                addClassImport(reference, superClass);
            }
        }

        for (ClassReference _interface : interfaces)
        {
            if (model.isExcludedClass(_interface) == null)
            {
                addClassImport(reference, _interface);
            }
        }

        for (ClassReference _interface : extendedInterfaces)
        {
            if (model.isExcludedClass(_interface) == null)
            {
                addClassImport(reference, _interface);
            }
        }

        for (FieldReference field : fields.values())
        {
            if (field.isExcluded() == null)
            {
                addClassImport(reference, getType(field));
            }
        }

        if (constructor != null)
        {
            for (ParameterReference parameterReference : constructor.getParameters())
            {
                addClassImport(reference, getType(parameterReference));
            }
        }

        for (MethodReference method : methods.values())
        {
            if (method.isExcluded() == null)
            {
                addClassImport(reference, getReturnType(method));

                for (ParameterReference parameterReference : method.getParameters())
                {
                    addClassImport(reference, getType(parameterReference));
                }
            }
        }
    }

    private void addClassImport(final ClassReference thisReference, final ClassReference referenceToImport)
    {
        if (canImport(referenceToImport))
        {
            final String thisPackageName = thisReference.getPackageName();
            final String importPackageName = referenceToImport.getPackageName();

            if (!importPackageName.equals(thisPackageName))
            {
                thisReference.addImport(referenceToImport);
            }
        }
    }

    private void collectFunctionImports(final FunctionReference function)
    {
        if (function.isExcluded() == null)
        {
            ClassReference returnType = getReturnType(function);

            if (canImport(returnType))
            {
                function.addImport(returnType);
            }

            for (ParameterReference parameterReference : function.getParameters())
            {
                ClassReference type = getType(parameterReference);

                if (canImport(type))
                {
                    function.addImport(type);
                }
            }
        }
    }

    private ClassReference getType(final FieldReference field)
    {
        return model.getClassReference(field.toTypeString());
    }

    private ClassReference getReturnType(final MethodReference method)
    {
        return model.getClassReference(method.transformReturnString());
    }

    private ClassReference getReturnType(final FunctionReference function)
    {
        return model.getClassReference(function.transformReturnString());
    }

    private ClassReference getType(final ParameterReference parameter)
    {
        return model.getClassReference(parameter.getQualifiedName());
    }

    private boolean canImport(ClassReference reference)
    {
        return reference != null && reference.isQualifiedName() && model.isExcludedClass(reference) == null;
    }

    @Override
    public boolean shouldTraverse(final NodeTraversal nodeTraversal, final Node n, final Node parent) {
        return false;
    }

    @Override
    public void visit(final NodeTraversal t, final Node n, final Node parent)
    {
    }
}
