package copiler_teamproject;

public class Parser {
    // Recursive descent parser that inputs a C++Lite program and 
    // generates its abstract syntax.  Each method corresponds to
    // a concrete syntax grammar rule, which appears as a comment
    // at the beginning of the method.
  
    Token token;          // current token from the input stream
    Lexer lexer;
    public static int n;
    public static int assignFlag = 0;
    public static int pass = 0;
    Token token1;
    Token token2;
    Token token3;
  
    public Parser(Lexer ts) { // Open the C++Lite source program
        lexer = ts;                          // as a token stream, and
        token = lexer.next();            // retrieve its first Token
    }
  
    private String match (TokenType t) { // * return the string of a token if it matches with t *
    	
        String value = token.value();
        System.out.println("T = " + t);
        System.out.println("V = " + value);
       // System.out.println(t +" / " +  value);
        if (token.type().equals(t)){
            token = lexer.next();
      //  System.out.println("ooooo- - -"+token);
        }
        else
            error(t);
        return value;
    }
  
    private void error(TokenType tok) {
        System.err.println("Syntax error: expecting: " + tok 
                           + "; saw: " + token);
        System.exit(1);
    }
  
    private void error(String tok) {
        System.err.println("Syntax error: expecting: " + tok 
                           + "; saw: " + token);
        System.exit(1);
    }
  
    public Program program() {
        // Program --> void main ( ) '{' Declarations Statements '}'
    	
       // TokenType[ ] header = {TokenType.Int, TokenType.Main,
              //            TokenType.LeftParen, TokenType.RightParen};
     //   for (int i=0; i<header.length; i++)   // bypass "int main ( )"
       //     match(header[i]);
            
     //   match(TokenType.LeftBrace);
        Declarations d = declarations();
        Block b = statements();
        // student exercise
      //  match(TokenType.ControlEnd);
        return new Program(d,b);
        //return null;  // student exercise
        
       
    }
  
    private Declarations declarations () {
        // Declarations --> { Declaration }
    	Declarations decls = new Declarations();
    	
    	while(isType()){
    	//	System.out.println("declscheck");
    		declaration(decls);
    	}
    	
        return decls;  // student exercise
    }
  
    private void declaration (Declarations decls) {
        // Declaration  --> Type Identifier { , Identifier } ;
    	// Declaration → Type Identifier [ Integer [ x Integer ] ] { ,Identifier [ Integer [ x Integer ] ] }
        // student exercise
    	Variable var ;
    	Declaration decl ;
    	
    	Type t = type();
    	System.out.println("Type = " + t);
    	match(TokenType.MeaningLess);
    	var = new Variable(match(TokenType.Identifier));
    	System.out.println("Var = " + var);
    	if(token.type().equals(TokenType.LeftBracket)){
        	match(TokenType.LeftBracket);
        	Expression e = expression();
        	match(TokenType.RightBracket);
        	if(token.type().equals(TokenType.LeftBracket)){
        		match(TokenType.LeftBracket);
            	Expression e2 = expression();
            	match(TokenType.RightBracket);
        	}

    	}
    	decl = new Declaration(var,t);
    	decls.add(decl);
    	while(token.type().equals(TokenType.Comma)){
        	match(TokenType.Comma);
    	 	var = new Variable(match(TokenType.Identifier));
        	System.out.println("Var2 = " + var);
        	if(token.type().equals(TokenType.LeftBracket)){
            	match(TokenType.LeftBracket);
            	Expression e = expression();
            	match(TokenType.RightBracket);
            	if(token.type().equals(TokenType.LeftBracket)){
            		match(TokenType.LeftBracket);
                	Expression e2 = expression();
                	match(TokenType.RightBracket);
                	
            	}

        	}
        	decl = new Declaration(var,t);
        	decls.add(decl);
    	}
    	match(TokenType.MeaningLess);
    	System.out.println("333");

    //	match(TokenType.Semicolon);
    	//decls.add(decl);
    }
    	
    	
    
  
    private Type type () {
        // Type  -->  int | bool | float | char 
    	// Type → 정수 | 명제 | 수 | 문자
        Type t = null;
        if(token.type().equals(TokenType.Int)){
        	t = Type.INT;
        }
        else if(token.type().equals(TokenType.Bool)){
        	t = Type.BOOL;
        }
        else if(token.type().equals(TokenType.Float)){
        	t = Type.FLOAT;
        }
        else if(token.type().equals(TokenType.Char)){
        	t = Type.CHAR;
        }
        token = lexer.next();
        // student exercise
        return t;          
    }
  
    private Statement statement() {
        // Statement -->  Assignment | IfStatement | WhileStatement
        Statement s = new Skip();
    
        if(token.type().equals(TokenType.Identifier)){ // assignment
        	//System.out.println("ass");

        	s = assignment();
        }
        else if(token.type().equals(TokenType.If)){ // ifstatement
        	s = ifStatement();
	
        }
        else if(token.type().equals(TokenType.While)){ // whilestatement
        	//System.out.println("while");
        	s = whileStatement();
        }
        else{
        }
        	//new Skip();
        System.out.println("stateEnd");
        // student exercise
        return s;
    }
  
    private Block statements () {
        // Block --> '{' Statements '}'
        Block b = new Block();
    	System.out.println("bk");

       // match(TokenType.LeftBrace);
        //Statement s = statement();
        //match(TokenType.RightBrace);
        while( token.type().equals(TokenType.While) ||
        		token.type().equals(TokenType.If) ||
        		token.type().equals(TokenType.Identifier)){
        	//System.out.println("s =="+ token);
        	b.sts.add(statement());
        	
        	
        }
        		
    //    b.sts.add(e);
    //    return new ArrayList<Statement>();


        // student exercise
        return b;
    }
  
    private Assignment assignment () {
        // Assignment --> Identifier = Expression ;
    	assignFlag = 1;
    	Variable t = new Variable(match(TokenType.Identifier));
    //	System.out.println("t==="+ t);

    	match(TokenType.MeaningLess);
    	//System.out.println("TTTTT");
    	Expression e = expression();
    	//System.out.println("TT====`TT"+e);

    	//match(TokenType.Semicolon);
    //	System.out.println("TT====`TT"+e);
    	match(TokenType.MeaningLess);
    	assignFlag = 0;
    	return new Assignment(t,e);
    	
   
       // return null;  // student exercise
    }
  
    private Conditional ifStatement () {
        // IfStatement --> if ( Expression ) Statement [ else Statement ]
    	// if statement --> 만약 'expression' (이라면 | 라면) statements 끝 
    	//                    [ 그렇지 않으면 statements 끝 ]
    	Statement s = new Skip();

    	match(TokenType.If);
    	//System.out.println("IFIIFIFIFF");

    	//match(TokenType.LeftParen);
    	Expression e = expression();
    //	match(TokenType.RightParen);
    	//System.out.println("IFIIFIFI333333FF");

    	match(TokenType.MeaningLessIf);

    	s = statements();
    	if(token.type().equals(TokenType.Else)){
    		match(TokenType.Else);
    		Statement elseS = statement();
        	match(TokenType.ControlEnd);

    		return new Conditional(e,s,elseS);
    		
    	}
    	else
        	match(TokenType.ControlEnd);

    		return new Conditional(e,s);
    	
      //  return null;  // student exercise
    }
  
    private Loop whileStatement () {
        // WhileStatement --> while ( Expression ) Statement
    	// while statement --> 반복 'expression' (이라면 | 라면) statements 끝
    	Statement s = new Skip();
    	match(TokenType.While);
    	//match(TokenType.LeftParen);
    	System.out.println("lll1");

    	Expression e = expression();
    	System.out.println("lll");
    	match(TokenType.MeaningLessIf);

    //	match(TokenType.RightParen);
    	s = statements();
    	match(TokenType.ControlEnd);

    	return new Loop(e,s);
    	
    	
     //   return null;  // student exercise
    }

    private Expression expression () {
        // Expression --> Conjunction { || Conjunction }
    	//Expression -> Conjunction { 또는 Conjunction }
    	System.out.println("startttl");

    	Expression e = conjunction();
    	while(token.type().equals(TokenType.Or)){
            Operator op = new Operator(match(token.type()));
            Expression c2 = conjunction();
            e = new Binary(op, e, c2);
        }
        return e;
      
	
       // return null;  // student exercise
    }
  
    private Expression conjunction () {
        // Conjunction --> Equality { && Equality }
    	// Conjunction -> Equality { 그리고 Equality }
    	Expression e = equality();
	      while(token.type().equals(TokenType.And)){
	          Operator op = new Operator(match(token.type()));
	          Expression e2 = equality();
	          e = new Binary(op, e, e2);
	      }
	      return e;
	
       // return null;  // student exercise
    }
  
    private Expression equality () {
        // Equality --> Relation [ EquOp Relation ]
    	// Equality -> Relation [ (은 | 는) Relation (와 | 과) EquOp ]
    	
    	Expression e = relation();
    	if(token.type().equals(TokenType.MeaningLess) && assignFlag == 0
    			&& !token.type().equals(TokenType.MeaningLessIf)
    					&& pass == 0){
        	match(TokenType.MeaningLess);
        	Expression r2 = relation();
        	match(TokenType.MeaningLess);
        	//System.out.println("ttt");
        	if(isEqualityOp()){
        		Operator op = new Operator(match(token.type()));
                // Expression r2 = relation();
                 System.out.println("lllllllllllll");

                 e = new Binary(op, e, r2);
                 return e;
        	
        	}
        }
        System.out.println("passssss");
        pass = 0 ;

    	return e;
      //  return null;  // student exercise
    }
    
    
    private Expression relation (){
        // Relation --> Addition [RelOp Addition]
    	// Relation -> Addition [ (은 | 는) Addition 보다 RelOp ]

    	Expression e = addition();
    	if(token.type().equals(TokenType.MeaningLess) && assignFlag == 0
    			&& !token.type().equals(TokenType.MeaningLessIf)
    			&& !token.type().equals(TokenType.MeaningLessThan)
    			&& !token.type().equals(TokenType.MeaningLessWith)){
    		
        	match(TokenType.MeaningLess);
            Expression r  = relation();
        	//System.out.println("MMMM2222M");
        	if(token.type().equals(TokenType.MeaningLessWith) 
        			|| token.type().equals(TokenType.MeaningLessThan) ){
        		if(token.type().equals(TokenType.MeaningLessThan)){
        			match(TokenType.MeaningLessThan);
                	System.out.println("ttthannn");
                	if (isRelationalOp()) {
                        Operator op = new Operator(match(token.type()));
                        System.out.println("op1 = "+ op);
                      //  Expression a2 = addition();
                        e = new Binary(op, e, r);
                    }
        		}
        		else{
        			match(TokenType.MeaningLessWith);
        			if(isEqualityOp()){
            		Operator op = new Operator(match(token.type()));
                    // Expression r2 = relation();
                     e = new Binary(op, e, r);
                     pass = 1;
                   //  return e;
            	
            	}
        			
        		}
        	}
        	
        }
    	
    	return e;
    }
  
    private Expression addition () {
        // Addition --> Term { AddOp Term }
        Expression e = term();

        while (isAddOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term2 = term();
            e = new Binary(op, e, term2);
        }
        return e;
    }
  
    private Expression term () {
        // Term --> Factor { MultiplyOp Factor }
    	// Term -> Factor { ( { MulOp Factor } | { (을 | 를) Factor 로 나눈 나머지 } ) }
        Expression e = factor();
        while ((isMultiplyOp() ||token.type().equals(TokenType.MeaningLessRemain))
        		 ) {
        	if(token.type().equals(TokenType.MeaningLessRemain)){
            	match(TokenType.MeaningLessRemain);
                Expression term2 = factor();
            	match(TokenType.MeaningLess);
	            Operator op = new Operator(match(token.type()));
	            e = new Binary(op, e, term2);

        	}
        	else{
        		System.out.println("cattt222t");

	            Operator op = new Operator(match(token.type()));
	            Expression term2 = factor();
	            e = new Binary(op, e, term2);
        	}
        }
        return e;
    }
  
    private Expression factor() {
        // Factor --> [ UnaryOp ] Primary 
        if (isUnaryOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term = primary();
            return new Unary(op, term);
        }
        else return primary();
    }
  
    private Expression primary () {
        // Primary --> Identifier | Literal | ( Expression )
        //             | Type ( Expression )
    	// Primary -> Identifier [ [Expression] ] 
    	//             | Literal | ( Expression ) | 
        Expression e = null;
        if (token.type().equals(TokenType.Identifier)) {
            e = new Variable(match(TokenType.Identifier));
        } 
        else if (isLiteral()) {
        	
            e = literal();

        } 
        else if (token.type().equals(TokenType.LeftParen)) {
            token = lexer.next();
            e = expression();       
            match(TokenType.RightParen);
        } 
        else if (isType( )) {
            Operator op = new Operator(match(token.type()));
            match(TokenType.LeftParen);
            Expression term = expression();
            match(TokenType.RightParen);
            e = new Unary(op, term);
        } 
        else error("Identifier | Literal | ( | Type");
   

        return e;
    }

    private Value literal( ) {
    	Value value = null;
    	if (token.type().equals(TokenType.IntLiteral)) {
    		value = new IntValue(Integer.parseInt(match(TokenType.IntLiteral)));
    	}
    	else if (token.type().equals(TokenType.FloatLiteral)) {
    		value = new FloatValue(Float.parseFloat(match(TokenType.FloatLiteral)));
    	}
    	else if (token.type().equals(TokenType.CharLiteral)) {
    		value = new CharValue(match(TokenType.CharLiteral).charAt(0));
    	}

    	else if (isBooleanLiteral()) {
    		if (token.type().equals(TokenType.True)) {
    		    match(TokenType.True); 
    		    } 
    		else if (token.type().equals(TokenType.False)) {
    		    match(TokenType.False);
    		    }
    		
    		
    		    value = new BoolValue(Boolean.valueOf(token.value()));
    		    } 
    	
        return value;  // student exercise
    }
  

    private boolean isAddOp( ) {
        return token.type().equals(TokenType.Plus) ||
               token.type().equals(TokenType.Minus);
    }
    
    private boolean isMultiplyOp( ) {
        return token.type().equals(TokenType.Multiply) ||
               token.type().equals(TokenType.Divide);
    }
    
    private boolean isUnaryOp( ) {
        return token.type().equals(TokenType.Not) ||
               token.type().equals(TokenType.Minus);
    }
    
    private boolean isEqualityOp( ) {
        return token.type().equals(TokenType.Equals) ||
            token.type().equals(TokenType.NotEqual);
    }
    
    private boolean isRelationalOp( ) {
        return token.type().equals(TokenType.Less) ||
               token.type().equals(TokenType.LessEqual) || 
               token.type().equals(TokenType.Greater) ||
               token.type().equals(TokenType.GreaterEqual);
    }
    
    private boolean isType( ) {
        return token.type().equals(TokenType.Int)
            || token.type().equals(TokenType.Bool) 
            || token.type().equals(TokenType.Float)
            || token.type().equals(TokenType.Char);
    }
    
    private boolean isLiteral( ) {
        return token.type().equals(TokenType.IntLiteral) ||
            isBooleanLiteral() ||
            token.type().equals(TokenType.FloatLiteral) ||
            token.type().equals(TokenType.CharLiteral);
    }
    
    private boolean isBooleanLiteral( ) {
        return token.type().equals(TokenType.True) ||
            token.type().equals(TokenType.False);
    }
    
    public static void main(String args[]) {
        Parser parser  = new Parser(new Lexer("/Users/choejaeyun/Downloads/Compiler/test.txt"));
        Program prog = parser.program();
        prog.display();           // display abstract syntax tree
    } //main

} // Parser