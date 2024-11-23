package org.example;

import lincks.maximilian.parser.parser.ast.AstExpression;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
//       String input = "(!x1 && !(x3 <> x2)) || ((x3 => !x4) && (x1 => (x2 || !x3)) && x4))";
       String input = "(x && !y) || (z || (x && !w))";
       var lexer = LexerSetup.lexer;
       var parser = ParserSetup.parser;
       var simplificationInterpreter = SimplificationInterpreterSetup.interpreter;
       var tseitinInterpreter = TseitinInterpreterSetup.interpreter;
//
       var res  = simplificationInterpreter.run(lexer, parser, input);
        System.out.println(AstPrinter.printString(res));
        AstExpression<?> cnf = tseitinInterpreter.run((AstExpression<AstExpression<?>>) res);
        System.out.print(AstPrinter.printString(cnf));
        System.out.println(" && y" + (TseitinInterpreterSetup.getCounter() -1));


//        AstExpression<?> cnf = tseitinInterpreter.run(lexer, parser, input);
//
//        System.out.print(AstPrinter.printString(cnf));
//        System.out.println(" && y" + (TseitinInterpreterSetup.getCounter() -1));
//        System.out.println("--");
//        System.out.println("(!y0 || x) && (!y0 || !y) && (y0 || !x || !!y) && (!y1 || x) && (!y1 || !w) && (y1 || !x || !!w) && (y2 || !z) && (y2 || !y3) && (!y2 || z || y3) && (y4 || !y5) && (y4 || !y6) && (!y4 || y5 || y6) && y6");

    }



}
