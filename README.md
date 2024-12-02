# CNF Calculator

There are no guarantees for anything, this is just a toy program to solve assignments from my SAT solving course at
university and to test another library i wrote.

This program can turn logical expressions into an equivalent expression in cnf (not simplified):

The following operators are available, everything else is treated as a literal.

* negation `!`
* implication `=>`
* equivalence `<>`
* union/or `||`
* intersection/and `&&`

# HINT 1

You have to change the string inside App to supply different expressions.

# HINT 2

This project only works if you clone and install the monad-experiment and the monad-parser library from this
[repo](https://github.com/MaxiLambda/monad-experiment).