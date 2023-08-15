import os
import os.path as osp
import itertools

from antlr4 import FileStream, CommonTokenStream, ParseTreeWalker
from analysis.utils.arguments import get_args

from analysis.grammars.grammars_registry import (PARSERS, LEXERS,
                                                        LISTENERS)


START_RULE = {
    'kt': 'kotlinFile',
}

SUPPORTED_LANGUAGES = {'kt'}

SIMILAR_NODES = {
    'IDENTIFIER': ['LITERAL', 'IDENTIFIER'],
    'CLASS_DECL': ['CLASS_DECL'],
    'CONSTRUCTOR_DECL': ['CONSTRUCTOR_DECL'],
    'PARAMETER_LIST': ['PARAMETER_LIST', 'VALUE_ARGUMENT_LIST'],
    'PARAMETER': ['PARAMETER'],
    'CONSTRUCTOR_CALL': ['CONSTRUCTOR_CALL'],
    'CLASS_BODY': ['CLASS_BODY', 'BODY'],
    'CLASS_MEMBER': ['CLASS_MEMBER'],
    'ENUM_DECL': ['ENUM_DECL'],
    'ENUM_BODY': ['ENUM_BODY'],
    'FUNCTION_DECL': ['FUNCTION_DECL'],
    'FUNCTION_BODY': ['FUNCTION_BODY'],
    'FUNCION_CALL': ['FUNCION_CALL'],
    'ATTRIBUTE_DECL': ['ATTRIBUTE_DECL', 'VARIABLE_DECL'],
    'VARIABLE_DECL': ['VARIABLE_DECL', 'ATTRIBUTE_DECL'],
    'GETTER': ['GETTER'],
    'SETTER': ['SETTER'],
    'TYPE_ALIAS': ['TYPE_ALIAS'],
    'PARAMETER_TYPE': ['PARAMETER_TYPE', 'TYPE'],
    'TYPE': ['TYPE', 'PARAMETER_TYPE', 'FUNCTION_TYPE', 'NULLABLE_TYPE',
             'USER_TYPE'],
    'NULLABLE_TYPE': ['NULLABLE_TYPE', 'TYPE'],
    'FUNCTION_TYPE': ['FUNCTION_TYPE', 'TYPE'],
    'USER_TYPE': ['USER_TYPE', 'TYPE'],
    'OR': ['OR'],
    'AND': ['AND'],
    'TERNARY': ['TERNARY'],
    'AS': ['AS'],
    'PREFIX': ['PREFIX'],
    'POSFIX': ['POSFIX'],
    'COLLECTION_INDEXING': ['COLLECTION_INDEXING'],
    'VALUE_ARGUMENT_LIST': ['PARAMETER_LIST', 'VALUE_ARGUMENT_LIST'],
    'VALUE_ARGUMENT': ['VALUE_ARGUMENT'],
    'TYPE_ARGUMENT_LIST': ['TYPE_ARGUMENT_LIST'],
    'TYPE_ARGUMENT': ['TYPE_ARGUMENT'],
    'STRING': ['STRING'],
    'COLLECTION': ['COLLECTION'],
    'SUPER_CALL': ['SUPER_CALL'],
    'CONDITION': ['CONDITION'],
    'BODY': ['BODY'],
    'TRY': ['TRY'],
    'CATCH': ['CATCH'],
    'FINALLY': ['FINALLY'],
    'LOOP_STATEMENT': ['LOOP_STATEMENT'],
    'JUMP_STATEMENT': ['JUMP_STATEMENT'],
    'EQUALITY_OPERATOR': ['EQUALITY_OPERATOR'],
    'COMPARISON_OPERATOR': ['COMPARISON_OPERATOR'],
    'MEMBER_OF': ['MEMBER_OF'],
    'IS_TYPE': ['IS_TYPE'],
    'ADDITIVE_OPERATOR': ['ADDITIVE_OPERATOR'],
    'MULTIPLICATIVE_OPERATOR': ['MULTIPLICATIVE_OPERATOR'],
    'LOGICAL_OPERATOR': ['LOGICAL_OPERATOR'],
    'ACCESS_OPERATOR': ['ACCESS_OPERATOR'],
    'VISIBILITY_MODIFIER': ['VISIBILITY_MODIFIER'],
    'CONST_DECL': ['CONST_DECL'],
    'INHERITANCE_MODIFIER': ['INHERITANCE_MODIFIER'],
    'LITERAL': ['IDENTIFIER', 'LITERAL'],
    'ASSIGNMENT_OPERATOR': ['ASSIGNMENT_OPERATOR'],
    'EXPRESSION': ['EXPRESSION'],
    'THROW': ['THROW'],
    'THIS': ['THIS'],
    'AWAIT_EXPRESSION': ['AWAIT_EXPRESSION'],
    'ASSERT': ['ASSERT'],
}

ADVANCE_NODES = {
    'TYPE',
    'BODY',
    'EXPRESSION',
}

STOP_NODES = {
    'FUNCTION_DECL',
    'FUNCTION_BODY',
    'CLASS_DECL',
    'CLASS_BODY',
}

def load_grammar(f):
    """Load grammar given the file."""
    _name, ext = os.path.splitext(f)
    ext = ext[1:]
    if ext not in LISTENERS:
        raise ValueError('The program does not support the file extension')
    input_stream = FileStream(f)
    lexer = LEXERS[ext](input_stream)
    stream = CommonTokenStream(lexer)
    parser = PARSERS[ext](stream)
    tree = getattr(parser, START_RULE[ext])()
    listener = LISTENERS[ext]()
    walker = ParseTreeWalker()
    walker.walk(listener, tree)
    #print(repr(listener.tree.children)) #tree in console
    print("------------ Loaded tree")
    return listener.tree


def discover_files(directory):
    """Discover supported files in the given directory."""
    res = []
    for root, _, files in os.walk(directory):
        for f in files:
            _, ext = osp.splitext(f)
            if ext[1:] in SUPPORTED_LANGUAGES:
                res.append(osp.join(root, f))
    return res



if __name__ == "__main__":
    args = get_args()
    dir_files = [] if args.d == '' else sum(
        [discover_files(d) for d in args.d], [])
    files = args.f + dir_files
    print('Files loaded: ', files)
    
    ecst_trees = []
    for f in files:
        tree = load_grammar(f)
        ecst_trees.append(tree)

    print(ecst_trees)
    