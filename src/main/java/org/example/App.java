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
       String input = "(!x1 && !(x3 <> x2)) || ((x3 => !x4) && (x1 => (x2 || !x3)) && x4))";
       var lexer = LexerSetup.lexer;
       var parser = ParserSetup.parser;
       var simplificationInterpreter = SimplificationInterpreterSetup.interpreter;

       AstExpression<?> res  = simplificationInterpreter.run(lexer, parser, input);
        System.out.println(AstPrinter.printString(res));
    }

}
