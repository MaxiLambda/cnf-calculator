package org.example;

import lincks.maximilian.impl.monad.MList;
import lincks.maximilian.impl.monad.Maybe;
import lincks.maximilian.parser.Interpreter;
import lincks.maximilian.parser.parser.ast.*;
import lincks.maximilian.parser.token.Symbol;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

import static org.example.Symbols.*;

public class TseitinInterpreterSetup {
    private static final Context<AstExpression<?>> context = new Context<>(Map.of(
            negation, TseitinInterpreterSetup::negation,
            union, TseitinInterpreterSetup::union,
            intersection, TseitinInterpreterSetup::intersection
    ));

    //TODO this needs resting after every run -> don't use static setup
    @Getter
    private static int counter = 0;
    public static final HashMap<String, Integer> newFormulaNr = new HashMap<>();

    public static final Interpreter<AstExpression<?>> interpreter = new Interpreter<>(TseitinInterpreterSetup::fromLiteral, context);

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
            case SymbolLiteral<?> v -> {
                //negate by returning new Expression with negation operator
                return new Expression<>(negation, new MList<>(v));
            }
            //real Seitin needs this, but if SimplificationInterpreter is run before, only SymbolLiterals are negated
            default -> throw new RuntimeException();
        }
    }

    private static Literal<AstExpression<?>> negation(MList<Literal<AstExpression<?>>> l) {
        AstExpression<?> clause = fromLiteral(l.head());
        return new ValueLiteral<>(neg(clause));
    }

    private static Literal<AstExpression<?>> union(MList<Literal<AstExpression<?>>> l) {
        var params = l.map(TseitinInterpreterSetup::fromLiteral);
        var G = l.head();
        var H = l.tail().head();

        var paramsAst = params.map(e -> (AstExpression<AstExpression<?>>) e);

        Maybe<AstExpression<AstExpression<?>>> seitinRoot = Maybe.nothing();

        AstExpression<AstExpression<?>> F = new Expression<>(union, paramsAst);
        int fNr = newFormulaNr.computeIfAbsent(AstPrinter.printString(F), x -> counter++);

        AstExpression<AstExpression<?>> h;
        AstExpression<AstExpression<?>> g;

        if (isLiteral(G)) {
            g = G;
        } else {
            seitinRoot = new Maybe<>(G);
            int gNr = newFormulaNr.computeIfAbsent(AstPrinter.printString(G), x -> counter++);
            g = new SymbolLiteral<>(new Symbol("y" + gNr));
        }
        if (isLiteral(H)) {
            h = H;
        } else {
            seitinRoot = new Maybe<>(seitinRoot.<AstExpression<AstExpression<?>>>map(ignore -> new Expression<>(intersection, paramsAst)).otherwise(H));
            int hNr = newFormulaNr.computeIfAbsent(AstPrinter.printString(H), x -> counter++);
            h = new SymbolLiteral<>(new Symbol("y" + hNr));
        }

        return seitinReplacement(fNr, seitinRoot, g, h);
    }

    private static Literal<AstExpression<?>> intersection(MList<Literal<AstExpression<?>>> l) {
        var params = l.map(TseitinInterpreterSetup::fromLiteral);
        var G = l.head();
        var H = l.tail().head();

        var paramsAst = params.map(e -> (AstExpression<AstExpression<?>>) e);

        Maybe<AstExpression<AstExpression<?>>> seitinRoot = Maybe.nothing();

        AstExpression<AstExpression<?>> F = new Expression<>(intersection, paramsAst);
        int fNr = newFormulaNr.computeIfAbsent(AstPrinter.printString(F), x -> counter++);

        AstExpression<AstExpression<?>> h;
        AstExpression<AstExpression<?>> g;

        if (isLiteral(G)) {
            g = G;
        } else {
            seitinRoot = new Maybe<>(G);
            int gNr = newFormulaNr.computeIfAbsent(AstPrinter.printString(G), x -> counter++);
            g = new SymbolLiteral<>(new Symbol("y" + gNr));
        }
        if (isLiteral(H)) {
            h = H;
        } else {
            seitinRoot = new Maybe<>(seitinRoot.map(ignore -> F).otherwise(H));
            int hNr = newFormulaNr.computeIfAbsent(AstPrinter.printString(H), x -> counter++);
            h = new SymbolLiteral<>(new Symbol("y" + hNr));
        }

        return seitinReplacement(fNr, seitinRoot, g, h);
    }

    private static Literal<AstExpression<?>> seitinReplacement(int fNr, Maybe<AstExpression<AstExpression<?>>> seitinRoot, AstExpression<AstExpression<?>>g, AstExpression<AstExpression<?>> h){
        AstExpression<AstExpression<?>> f = new SymbolLiteral<>(new Symbol("y" + fNr));
        AstExpression<AstExpression<?>> fNot = (AstExpression<AstExpression<?>>)SimplificationInterpreterSetup.interpreter.run(new Expression<>(negation, new MList<>(f)));
        AstExpression<AstExpression<?>> gNot = (AstExpression<AstExpression<?>>)SimplificationInterpreterSetup.interpreter.run(new Expression<>(negation, new MList<>(g)));
        AstExpression<AstExpression<?>> hNot = (AstExpression<AstExpression<?>>) SimplificationInterpreterSetup.interpreter.run(new Expression<>(negation, new MList<>(h)));

        var gNotAndhNot = new Expression<>(union, new MList<>(gNot, hNot));

        var litE1 = new Expression<>(union, new MList<>(fNot, g));
        var litE2 = new Expression<>(union, new MList<>(fNot, h));
        var litE3 = new Expression<>(union, new MList<>(f, gNotAndhNot));

        var litE4 = new Expression<>(intersection, new MList<>(litE1, litE2));
        var tseitinF = new Expression<>(intersection, new MList<>(litE4, litE3));

        Literal<AstExpression<?>> res = new ValueLiteral<>(
                seitinRoot.map(root -> new Expression<>(
                        intersection,
                        new MList<>(root, tseitinF))).otherwise(tseitinF));

        newFormulaNr.put(AstPrinter.printString(res),fNr);

        return res;
    }

    private static  boolean isLiteral(AstExpression<?> expression) {
        switch (expression) {
            case Expression<?> v -> {
                return v.getSymbol().equals(negation) && isLiteral(v.getArgs().head());
            }
            case ValueLiteral<?> v -> {
                return isLiteral((AstExpression<?>) v.getValue());
            }
            case SymbolLiteral<?> v -> {
                return true;
            }
        }
    }
}