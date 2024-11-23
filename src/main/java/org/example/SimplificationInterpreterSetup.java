package org.example;

import lincks.maximilian.impl.monad.MList;
import lincks.maximilian.parser.Interpreter;
import lincks.maximilian.parser.parser.ast.*;

import java.util.Map;

import static org.example.Symbols.*;

public class SimplificationInterpreterSetup {
    //Removes <> and => and propagates !
    private static final Context<AstExpression<?>> context = new Context<>(Map.of(
            negation, SimplificationInterpreterSetup::negation,
            union, SimplificationInterpreterSetup::union,
            intersection, SimplificationInterpreterSetup::intersection,
            implication, SimplificationInterpreterSetup::implication,
            equivalence, SimplificationInterpreterSetup::equivalence
    ));

    public static final Interpreter<AstExpression<?>> interpreter = new Interpreter<>(SimplificationInterpreterSetup::fromLiteral, context);

    private static AstExpression<?> fromLiteral(Literal<AstExpression<?>> l) {
        switch (l) {
            case SymbolLiteral<AstExpression<?>> v -> {
                return v;
            }
            case ValueLiteral<AstExpression<?>> v -> {
                return v.getValue();
            }
        }
    }

    private static AstExpression<?> neg(AstExpression<?> expr) {
        switch (expr) {
            case ValueLiteral<?> v -> {
                //negate the value by returning a negated expression - RECURSIVE
                return neg((AstExpression<?>) v.getValue());
            }
            case SymbolLiteral<?> v -> {
                //negate by returning new Expression with negation operator
                return new Expression<>(negation, new MList<>(v));
            }
            case Expression<?> v -> {
                if (v.getSymbol().equals(negation)) {
                    //remove negation by just returning the first argument of the negation
                    return v.getArgs().head();
                } else if (v.getSymbol().equals(union)) {
                    //!(a || b) -> (!a && !b)
                    return new Expression<>(intersection, v.getArgs().map(SimplificationInterpreterSetup::neg).map(e -> (AstExpression<AstExpression<?>>) e));
                } else if (v.getSymbol().equals(intersection)) {
                    //!(a && b) -> (!a || !b)
                    return new Expression<>(union, v.getArgs().map(SimplificationInterpreterSetup::neg).map(e -> (AstExpression<AstExpression<?>>) e));
                }
                throw new RuntimeException();
            }
        }
    }

    private static Literal<AstExpression<?>> negation(MList<Literal<AstExpression<?>>> l) {
        AstExpression<?> clause = fromLiteral(l.head());
        return new ValueLiteral<>(neg(clause));
    }

    private static Literal<AstExpression<?>> union(MList<Literal<AstExpression<?>>> l) {
        var params = l.map(SimplificationInterpreterSetup::fromLiteral).map(e -> (AstExpression<Object>) e);
        return new ValueLiteral<>(new Expression<>(union, params));
    }

    private static Literal<AstExpression<?>> intersection(MList<Literal<AstExpression<?>>> l) {
        var params = l.map(SimplificationInterpreterSetup::fromLiteral).map(e -> (AstExpression<Object>) e);
        return new ValueLiteral<>(new Expression<>(intersection, params));
    }

    private static Literal<AstExpression<?>> implication(MList<Literal<AstExpression<?>>> l) {
        var params = l.map(SimplificationInterpreterSetup::fromLiteral).map(e -> (AstExpression<Object>) e);
        var x = params.head();
        var y = params.tail().head();

        var xNeg = new Expression<>(negation, new MList<>(x));

        var impl = new Expression<>(union, new MList<>(xNeg, y));

        return new ValueLiteral<>(impl);
    }

    private static Literal<AstExpression<?>> equivalence(MList<Literal<AstExpression<?>>> l) {
        var params = l.map(SimplificationInterpreterSetup::fromLiteral).map(e -> (AstExpression) e).map(ValueLiteral::new);
        var x = params.head();
        var y = params.tail().head();

        var xNeg = new Expression<>(negation, new MList<>(x));
        var yNeg = new Expression<>(negation, new MList<>(y));

        var impl1 = new Expression<>(union, new MList<>(xNeg, y));
        var impl2 = new Expression<>(union, new MList<>(yNeg, x));

        var eq = new Expression<>(intersection, new MList<>(impl1, impl2));
        return new ValueLiteral<>(eq);
    }
}
