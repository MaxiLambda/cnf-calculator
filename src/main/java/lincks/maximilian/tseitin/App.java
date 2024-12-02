package lincks.maximilian.tseitin;

import lincks.maximilian.parser.parser.ast.AstExpression;
import lincks.maximilian.tseitin.cnf.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
       String input = "(!x1 && !(x3 <> x2)) || ((x3 => !x4) && (x1 => (x2 && !x3)) && x4))";
       var lexer = LexerSetup.lexer;
       var parser = ParserSetup.parser;
       var simplificationInterpreter = SimplificationInterpreterSetup.interpreter;
       var tseitinInterpreter = TseitinInterpreterSetup.interpreter;

       var res  = simplificationInterpreter.run(lexer, parser, input);
        System.out.println(AstPrinter.printString(res));
        AstExpression<?> cnf = tseitinInterpreter.run((AstExpression<AstExpression<?>>) res);
        System.out.print(AstPrinter.printString(cnf));
        System.out.println(" && y" + (TseitinInterpreterSetup.getCounter() -1));
    }



}
