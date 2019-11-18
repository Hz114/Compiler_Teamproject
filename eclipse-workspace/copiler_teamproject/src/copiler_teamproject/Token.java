package copiler_teamproject;

public class Token {
	
	private TokenType type;
	private String value ="";
	
	public static final Token eofTok = new Token(TokenType.Eof);
	public static final Token intTok = new Token(TokenType.Int);
	public static final Token charTok = new Token(TokenType.Char);
	public static final Token floatTok = new Token(TokenType.Float);
	public static final Token boolTok = new Token(TokenType.Bool);
	public static final Token trueTok = new Token(TokenType.True);
	public static final Token falseTok = new Token(TokenType.False);
	public static final Token ifTok = new Token(TokenType.If);
	public static final Token elseTok = new Token(TokenType.Else);
	public static final Token whileTok = new Token(TokenType.While);
	public static final Token ctrlEndTok = new Token(TokenType.ControlEnd);
	public static final Token eqTok = new Token(TokenType.Equal);
	public static final Token ltTok = new Token(TokenType.Less);
	public static final Token ltEqTok = new Token(TokenType.LessEqual);
	public static final Token gtTok = new Token(TokenType.Greater);
	public static final Token gtEqTok = new Token(TokenType.GreaterEqual);
	public static final Token notTok = new Token(TokenType.Not);
	public static final Token notEqTok = new Token(TokenType.NotEqual);
	public static final Token minusTok = new Token(TokenType.Minus);
	public static final Token plusTok = new Token(TokenType.Plus);
	public static final Token multTok = new Token(TokenType.Multiply);
	public static final Token divTok = new Token(TokenType.Divide);
	public static final Token remainTok = new Token(TokenType.Remain);
	public static final Token andTok = new Token(TokenType.And);
	public static final Token orTok = new Token(TokenType.Or);
	public static final Token lParenTok = new Token(TokenType.LeftParen);
	public static final Token rParenTok = new Token(TokenType.RightParen);
	public static final Token lBracketTok = new Token(TokenType.LeftBracket);
	public static final Token rBracketTok = new Token(TokenType.RightBracket);
	public static final Token commaTok = new Token(TokenType.Comma);
	public static final Token arrayExprTok = new Token(TokenType.ArrayExpr);
	public static final Token noMeanTok = new Token(TokenType.MeaningLess);
	
	private static final String[] reserved = {
			"정수", "소수", "명제", "문자", "참", "거짓",
			"만약", "그렇지않으면", "반복", "끝",
			"같다", "다르다", "작다", "작거나같다", "크다", "크거나같다",
			"거짓말쟁이", "마이너스", "빼기", "더하기", 
			"곱하기", "나누기", "나눈나머지", "그리고", "또는",
			"은", "는", "을", "를",
			"이라면", "라면", "이다", "다",
			"와", "과", "로", "보다"
	};
	
    private static final Token[] token = {
    		intTok, floatTok, boolTok, charTok, trueTok, falseTok,
    		ifTok, elseTok, whileTok, ctrlEndTok,
    		eqTok, notEqTok, ltTok, ltEqTok, gtTok, gtEqTok,
    		notTok, minusTok, minusTok, plusTok,
    		multTok, divTok, remainTok, andTok, orTok,
    		noMeanTok, noMeanTok, noMeanTok, noMeanTok, 
    		noMeanTok, noMeanTok, noMeanTok, noMeanTok, 
    		noMeanTok, noMeanTok, noMeanTok, noMeanTok, 
    };
	
	private Token (TokenType t, String v) {
		type = t;
		value = v;
	}
	private Token (TokenType t) {
		type = t;
	}
	private Token (Token token, String v) {
		type = token.type;
		value = v;
	}
	
	public TokenType type() {
		return type;
	}
	public String value() {
		return value;
	}
	
	public static Token keyword  ( String name ) {
        for (int i = 0; i < reserved.length; i++)
        	if (name.equals(reserved[i]))  return new Token(token[i], name);
        	//if (name.equals(reserved[i]))  return token[i];
        return mkIdentTok(name);
    } // keyword

    public static Token mkIdentTok (String name) {
        return new Token(TokenType.Identifier, name);
    }

    public static Token mkIntLiteral (String name) {
        return new Token(TokenType.IntLiteral, name);
    }

    public static Token mkFloatLiteral (String name) {
        return new Token(TokenType.FloatLiteral, name);
    }

    public static Token mkCharLiteral (String name) {
        return new Token(TokenType.CharLiteral, name);
    }

    public String toString ( ) {
        //if (type.compareTo(TokenType.Identifier) < 0) return value;
        return type + "\t" + value;
    } // toString

    public static void main (String[] args) {
        System.out.println(eofTok);
        System.out.println(whileTok);
    }
	
}
