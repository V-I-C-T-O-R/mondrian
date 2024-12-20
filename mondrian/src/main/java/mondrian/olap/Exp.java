/* Decompiler 7ms, total 469ms, lines 24 */
package mondrian.olap;

import java.io.PrintWriter;
import mondrian.calc.Calc;
import mondrian.calc.ExpCompiler;
import mondrian.mdx.MdxVisitor;
import mondrian.olap.type.Type;

public interface Exp {
    Exp clone();

    int getCategory();

    Type getType();

    void unparse(PrintWriter var1);

    Exp accept(Validator var1);

    Calc accept(ExpCompiler var1);

    Object accept(MdxVisitor var1);
}