package compiler_teamproject;

public enum TokenType {
	Int, Char, Float, Bool, True, False,
	If, Else, While, ControlEnd,
	Equal, Less, LessEqual, Greater, GreaterEqual,
    Not, NotEqual, Plus, Minus, Equals,
    Multiply, Divide, Remain, And, Or,
	Identifier, IntLiteral, FloatLiteral, CharLiteral,
	LeftParen, RightParen, LeftBracket, RightBracket,
	Comma, ArrayExpr, MeaningLess, Eof, MeaningLessRemain, MeaningLessIf
	,MeaningLessThan, MeaningLessWith
	
}
