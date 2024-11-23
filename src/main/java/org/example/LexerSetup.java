package org.example;

import lincks.maximilian.impl.monad.MList;
import lincks.maximilian.parser.Lexer;
import lincks.maximilian.parser.example.LexerImpl;

import static org.example.Symbols.*;

public class LexerSetup {
    public static final Lexer<String> lexer = new LexerImpl(new MList<>(negation, implication, equivalence, union, intersection));

}
