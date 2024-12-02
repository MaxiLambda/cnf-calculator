package lincks.maximilian.tseitin.cnf;

import lincks.maximilian.parser.Parser;
import lincks.maximilian.parser.custom.InfixOp;
import lincks.maximilian.parser.custom.PrefixOp;
import lincks.maximilian.parser.example.ParserImpl;
import lincks.maximilian.parser.parser.ast.AstExpression;
import lincks.maximilian.parser.token.OperatorToken;
import lincks.maximilian.parser.token.Symbol;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static lincks.maximilian.tseitin.cnf.Symbols.*;

public class ParserSetup {
    //custom Operations
    private static final PrefixOp<AstExpression<?>> negate = new PrefixOp<>(negation, 1, 4);
    private static final InfixOp<AstExpression<?>> intersect = new InfixOp<>(intersection, 3);
    private static final InfixOp<AstExpression<?>> unite = new InfixOp<>(union, 2);
    private static final InfixOp<AstExpression<?>> imply = new InfixOp<>(implication, 1);
    private static final InfixOp<AstExpression<?>> equal = new InfixOp<>(equivalence, 0);

    private static final Map<Symbol, OperatorToken<AstExpression<?>>> operators = Stream.of(negate, equal, imply, unite, intersect)
            .collect(toMap(OperatorToken::getSymbol, Function.identity()));


    public static final Parser<AstExpression<?>> parser = new ParserImpl<>(operators);

}
