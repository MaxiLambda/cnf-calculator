package org.example;

import lincks.maximilian.parser.parser.ast.AstExpression;
import lincks.maximilian.parser.parser.ast.Expression;
import lincks.maximilian.parser.parser.ast.SymbolLiteral;
import lincks.maximilian.parser.parser.ast.ValueLiteral;

import static org.example.Symbols.*;

public class AstPrinter {
    public static String printString(AstExpression<?> expr) {
        switch (expr) {
            case ValueLiteral<?> v -> {
                //negate the value by returning a negated expression - RECURSIVE
                return printString((AstExpression<?>) v.getValue());
            }
            case SymbolLiteral<?> v -> {
                //negate by returning new Expression with negation operator
                return v.getSymbol().symbol();
            }
            case Expression<?> v -> {
                if (v.getSymbol().equals(negation)) {
                    //remove negation by just returning the first argument of the negation
                    return "!%s".formatted(v.getArgs().map(AstPrinter::printString).head());
                } else if (v.getSymbol().equals(union)) {
                    //!(a || b) -> (!a && !b)
                    var args = v.getArgs().map(AstPrinter::printString);
                    return "(%s || %s)".formatted(args.head(), args.tail().head());
                } else if (v.getSymbol().equals(intersection)) {
                    //!(a || b) -> (!a && !b)
                    var args = v.getArgs().map(AstPrinter::printString);
                    return "%s && %s".formatted(args.head(), args.tail().head());
                }
                //TODO maybe implement implication and equivalence as well
                throw new RuntimeException();
            }
        }
    }
}
