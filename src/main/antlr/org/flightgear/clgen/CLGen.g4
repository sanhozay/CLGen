grammar CLGen;

// ----------------------------------------------------------------------------
// Parser
// ----------------------------------------------------------------------------

specification
    : project? (declaration | item | checklist)*
    ;

// ----------------------------------------------------------------------------
// Project
// ----------------------------------------------------------------------------

project
    : 'project' '(' STRING ')' projectProperties
    ;

projectProperties
    : '{' (projectProperty ';')* '}'
    | projectProperty? ';'
    ;

projectProperty
    : 'author' '(' STRING ')'                               # Author
    ;

// ----------------------------------------------------------------------------
// Item
// ----------------------------------------------------------------------------

item
    : 'item' '(' STRING ')' '{' itemElement* '}'
    ;

itemElement
    : declaration
    | state
    | marker
    ;

declaration
    : ID '=' STRING ';'
    ;

state
    : 'state' '(' STRING (',' stateCondition)? ')' bindingDefinition
    ;

stateCondition
    : condition
    ;

condition
    : '!' condition                                         # NotCondition
    | condition '&&' condition                              # AndCondition
    | condition '||' condition                              # OrCondition
    | '(' condition ')'                                     # ParenCondition
    | terminal OP terminal                                  # BinaryCondition
    | terminal                                              # UnaryCondition
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
    : '{' (binding ';')* '}'
    | binding? ';'
    | binding? {
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
    ;

number
    : (INTEGER | DOUBLE)
    ;

// ----------------------------------------------------------------------------
// Checklist
// ----------------------------------------------------------------------------

checklist
    : 'checklist' '(' STRING ')' '{' checks '}'
    ;

checks
    : page*
    | check*
    | page check {
        notifyErrorListeners("Checks in paged checklists must be defined inside a page");
    }
    ;

page
    : 'page' '{' check* '}'
    ;

check
    : 'text' '(' ')' ';'                                    # Spacer
    | 'text' '(' STRING ')' ';'                             # Subtitle
    | 'check' '(' STRING ',' STRING (',' STRING)* ')' ';'   # NormalCheck
    ;

// ----------------------------------------------------------------------------
// Lexer
// ----------------------------------------------------------------------------

fragment IDCHR  : [-A-Za-z_];
fragment DIGIT  : [0-9];
fragment ESC    : '\\' ["\\];

COMMENT         : '#' .*? '\n' -> skip;

BOOLEAN         : ('true'|'false');
DOUBLE          : '-'? DIGIT+ '.' DIGIT+;
INTEGER         : '-'? DIGIT+;
STRING          : '"' (ESC | .)*? '"';

OP              : ('=='|'!='|'<'|'>'|'<='|'>=');

ID              : IDCHR (IDCHR | DIGIT)*;

WHITESPACE      : [ \t\n\r] -> skip;

ANYCHAR         : . ;
