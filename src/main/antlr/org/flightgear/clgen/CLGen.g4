grammar CLGen;

// ----------------------------------------------------------------------------
// Lexer
// ----------------------------------------------------------------------------

fragment IDCHR: [-A-Za-z_];
fragment DIGIT: [0-9];

BOOLEAN: ('true'|'false') ;

COMMENT: '#' .*? '\n' -> skip;

OP: ('=='|'!='|'<'|'>'|'<='|'>=');

ID: IDCHR (IDCHR | DIGIT)*;

INTEGER: '-'? DIGIT+;

DOUBLE: '-'? DIGIT+ '.' DIGIT+;

fragment ESC: '\\' ["\\];
STRING: '"' (ESC | .)*? '"';

WHITESPACE: [ \t\n\r] -> skip;

ANYCHAR: . ;

// ----------------------------------------------------------------------------
// Parser
// ----------------------------------------------------------------------------

specification
    : author? items checklists
    ;

author
    : 'author' '(' STRING ')' ';'
    | 'author' '(' STRING ')' {
        notifyErrorListeners("Unexpected input, did you forget a ';'?");
    }
    ;

items
    : /* empty */
    | item items
    ;

item
    : 'item' '(' STRING ')' '{' itemElements '}'
    ;

itemElements
    : /* empty */
    | itemElement itemElements
    ;

itemElement
    : declaration
    | state
    | marker
    ;

declaration
    : ID '=' STRING ';'
    | ID '=' STRING {
        notifyErrorListeners("Unexpected input, did you forget a ';'?");
    }
    ;

state
    : 'state' '(' STRING (',' conditionRoot)? ')' bindingDefinition
    ;

conditionRoot
    : condition
    ;

condition
    : '!' condition                                         # NotCondition
    | condition '&&' condition                              # AndCondition
    | condition '||' condition                              # OrCondition
    | '(' condition ')'                                     # ParenCondition
    | terminal OP terminal                                  # BinaryExpression
    | terminal                                              # SimpleExpression
    | condition '|' condition {
        notifyErrorListeners("Invalid operator '|', did you mean '||'?");
    } # ConditionError
    | condition '&' condition {
        notifyErrorListeners("Invalid operator '&', did you mean '&&'?");
    } # ConditionError
    | terminal '=' terminal {
        notifyErrorListeners("Invalid operator '=', did you mean '=='?");
    } # ConditionError
    ;

terminal
    : INTEGER                                               # IntegerTerminal
    | DOUBLE                                                # DoubleTerminal
    | BOOLEAN                                               # BooleanTerminal
    | STRING                                                # StringTerminal
    | ID                                                    # IdTerminal
    ;

bindingDefinition
    : '{' bindings '}'
    | binding? ';'
    | binding? {
        notifyErrorListeners("Unexpected input, did you forget a ';'?");
    }
    ;

bindings
    : /* empty */
    | binding ';' bindings
    | binding bindings {
        notifyErrorListeners("Unexpected input, did you forget a ';'?");
    }
    ;

binding
    : bindingAction                                         # SimpleBinding
    | 'if' '(' bindingCondition ')' bindingAction           # ConditionalBinding
    ;

bindingCondition
    : condition
    ;

bindingAction
    : ID '=' INTEGER                                        # AssignInt
    | ID '=' DOUBLE                                         # AssignDouble
    | ID '=' BOOLEAN                                        # AssignBool
    | ID '=' STRING                                         # AssignString
    | ID '=' ID                                             # AssignId
    | 'fgcommand' '(' STRING (',' parameter)* ')'           # Command
    ;

parameter
    : ID '=' INTEGER                                        # IntParam
    | ID '=' DOUBLE                                         # DoubleParam
    | ID '=' BOOLEAN                                        # BoolParam
    | ID '=' STRING                                         # StringParam
    | ID '=' ID                                             # IdParam
    | ID {
        notifyErrorListeners("Missing parameter name");
    }                                                       # ParameterError
    ;

marker
    : 'marker' '(' number ',' number ',' number ',' number ')' ';'
    | 'marker' '(' number ',' number ',' number ',' number ')' {
        notifyErrorListeners("Unexpected input, did you forget a ';'?");
    }
    ;

number
    : (INTEGER | DOUBLE)
    ;

checklists
    : /* empty */
    | checklist checklists
    ;

checklist
    : 'checklist' '(' STRING ')' '{' check_definition '}'
    ;

check_definition
    : pages
    | checks
    ;

checks
    : /* empty */
    | check checks
    ;

pages
    : /* empty */
    | page pages
    | page 'check' {
        notifyErrorListeners("Checks in paged checklists must be defined inside a page");
    }
    ;

page
    : 'page' '{' checks '}'
    ;

check
    : 'check' '(' STRING ',' STRING (',' STRING)* ')' ';'
    | 'check' '(' STRING ',' STRING (',' STRING)* ')' {
        notifyErrorListeners("Unexpected input, did you forget a ';'?");
    }
    ;
