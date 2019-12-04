package compiler_teamproject;

public class Parser {
    // Recursive descent parser that inputs a C++Lite program and 
    // generates its abstract syntax.  Each method corresponds to
    // a concrete syntax grammar rule, which appears as a comment
    // at the beginning of the method.
  
    Token token;          // current token from the input stream
    Lexer lexer;
    
    TypeCheckerValue typeChecker = new TypeCheckerValue();
    
    public static int n;
    public static int assignFlag = 0;
    public static int pass = 0;
    public int codeLine;
    
    public Parser(Lexer ts) { // Open the C++Lite source program
        lexer = ts;                          // as a token stream, and
        token = lexer.next();            // retrieve its first Token
        
    }
  
    private String match (TokenType t) { // * return the string of a token if it matches with t *
    	
        String value = token.value();
        if (token.type().equals(t)){
            token = lexer.next();
        }
        else
            error(t);
        return value;
    }
  
    private void error(TokenType tok) {
    	codeLine = lexer.getLine();
        System.err.println("Syntax error: line: "+ codeLine + "\nexpecting: " + tok 
                           + "; saw: " + token);
        System.exit(1);
    }
  
    private void error(String tok) {
    	codeLine = lexer.getLine();
        System.err.println("Syntax error: line: "+ codeLine + "\nexpecting: " + tok 
                           + "; saw: " + token);
        System.exit(1);
    }
  
    public Program program() {
    	// 프로그램을 시작한다  크게 선언부와 실행부로 구성 
        // Program --> Declarations Statements
    	
    	Declarations d = declarations();
        Block b = statements();
        return new Program(d,b);
    }
  
    private Declarations declarations () {
    	// 선언부는 여러줄 선언 될수 있다 (0또는 n)
        // Declarations --> { Declaration }
    	
    	Declarations decls = new Declarations();
    	
    	while(isType()){
    		declaration(decls);
    	}
    	
        return decls;
    }
  
    private void declaration (Declarations decls) {
    	// 변수 선언부에 대해 Type과 identifier을 나눈다
        // Declaration --> Type Identifier (은/는)[ '['Integer']' [ '['Integer']' ] ]
    	//										{ ,Identifier  [ '['Integer']' [ '['Integer']'] ] } (이다/다)
        
    	Variable var;
    	Variable arrA, arrB; //배열의 크기를 저장하는 변수
    	Declaration decl ;
    	Type t = type();
   
    	int cnt = 0;
    	
    	match(TokenType.MeaningLess); // (은/는)
		
    	do {
    		
    		//배열 크기를 초기화 시킨다.
    		arrA = null;
    		arrB = null;
    		
    		if(cnt++ != 0) {
    			token = lexer.next();
    		}
    		
    		var = new Variable(match(TokenType.Identifier)); // 변수명
        	
        	if(token.type().equals(TokenType.LeftBracket)){ // [ 을 만났을 때 == 배열인 경우 
            	match(TokenType.LeftBracket);
            	arrA = new Variable(match(TokenType.IntLiteral));
            	match(TokenType.RightBracket);
            	if(token.type().equals(TokenType.LeftBracket)){ // [ 을 또 만났을 때 == 이차원 배열인 경우
            		match(TokenType.LeftBracket);
            		arrB = new Variable(match(TokenType.IntLiteral));
                	match(TokenType.RightBracket);
            	}
        	}    
        	if(arrA == null && arrB == null) { //배열이 아닌경우 
        		decl = new Declaration(var,t);
        		typeChecker.InsertTypeChecker(var,t);
        		decls.add(decl);
        	}else if(arrA != null && arrB == null) { //1차원 배열
        		decl = new Declaration(var,t, arrA);
        		typeChecker.InsertTypeChecker(var,t, arrA);
        		decls.add(decl);
        	}else if(arrA != null && arrB != null) { //2치원 배열
        		decl = new Declaration(var,t, arrA, arrB);
        		typeChecker.InsertTypeChecker(var,t, arrA, arrB);
        		decls.add(decl);
        	}
    	}while(token.type().equals(TokenType.Comma));
    	
    	match(TokenType.MeaningLess); // (이다/다)
    }
    	
    	
    
  
    private Type type () {
        // Type --> 정수 | 명제 | 소수 | 문자
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
        
        return t;          
    }
  
    private Statement statement() {
        // Statement -->  Assignment | IfStatement | WhileStatement
        Statement s = new Skip();
    
        if(token.type().equals(TokenType.Identifier)){ // assignment
        	s = assignment();
        }
        else if(token.type().equals(TokenType.If)){ // ifstatement
        	s = ifStatement();
	
        }
        else if(token.type().equals(TokenType.While)){ // whilestatement
        	s = whileStatement();
        }
        else if(token.type().equals(TokenType.Input)){ // inputstatement
        	s = inputStatement();
        }
        else if(token.type().equals(TokenType.Output)){ // outputstatement
        	s = outputStatement();
        }
        else{}
        return s;
    }
  
    private Block statements () {
        // Block --> '{' Statements '}'
    	Block b = new Block();
        while( token.type().equals(TokenType.While) ||
        		token.type().equals(TokenType.If) ||
        		token.type().equals(TokenType.Identifier) ||
        		token.type().equals(TokenType.Input) ||
        		token.type().equals(TokenType.Output)){
        	b.sts.add(statement());
        }
        
        if(!isStatement()) {		
            error("statement의 시작은 반복, 만약, 입력, 출력, 식별자 토큰만 허용합니다");
    	}   
        return b;
    }
  
    
    private Assignment assignment () {
    	// Assignment --> identifier (은 | 는) expression [이]다
    	// 배열 ex) 숫자[0][1]은 (3 더하기 ㄹㄹ) 나누기 수cc01이다
    	
    	assignFlag = 1;
    	Variable t = new Variable(match(TokenType.Identifier));
    	//e = new Variable(match(TokenType.Identifier));

    	Expression arrA = null, arrB = null;
    	
    	if(token.type().equals(TokenType.LeftBracket)){ // [ 을 만났을 때 == 배열인 경우 
        	match(TokenType.LeftBracket);
        	arrA = expression();
        	match(TokenType.RightBracket);
        	if(token.type().equals(TokenType.LeftBracket)){ // [ 을 또 만났을 때 == 이차원 배열인 경우
        		match(TokenType.LeftBracket);
        		arrB = expression();
            	match(TokenType.RightBracket);
        	}
    	}
    	
    	match(TokenType.MeaningLess); // (은|는)

    	Expression e = expression();
    	match(TokenType.MeaningLess); // (다|이다)
    	assignFlag = 0;

    	codeLine = lexer.getLine();
    	if(arrA != null && arrB != null) { //2차원 배열
    		typeChecker.valCheck(t.toString(), arrA, arrB, codeLine);
        	return new Assignment(t,e, arrA, arrB);
    	}else if(arrA != null && arrB == null) { //1차원 배열
    		typeChecker.valCheck(t.toString(), arrA, codeLine);
        	return new Assignment(t,e, arrA);
    	}else{ //기본 (배열이 아닌경우)
    		typeChecker.valCheck(t.toString(), codeLine);
        	return new Assignment(t,e);
    	}
    }
  
    private Conditional ifStatement () {
        // IfStatement --> 만약 'expression' (이라면 | 라면) statements 끝 [ 그렇지 않으면  statements 끝] 
    	Statement s = new Skip();

    	match(TokenType.If); // 만약
    	Expression e = expression();

    	match(TokenType.MeaningLessIf); // (이라면 |라면) 
    	s = statements();
    	System.out.println(token);
    	if(token.type().equals(TokenType.Else)){ // 그렇지않으면일 경우
    		match(TokenType.Else); // (그렇지않으면)
    		Statement elseS = statement();
        	match(TokenType.ControlEnd); // (끝)

    		return new Conditional(e,s,elseS); // 만약 'expression' (이라면 | 라면) statements 끝  그렇지 않으면  statements 끝
    	}
    	else {
        	match(TokenType.ControlEnd); // (끝)
        	System.out.println(token);
        	return new Conditional(e,s); // 만약 'expression' (이라면 | 라면) statements 끝 
    	}
    }
  
    private Loop whileStatement () {
        // WhileStatement --> 반복 'expression' (이라면 | 라면) statements (끝)
    	Statement s = new Skip();
    	
    	match(TokenType.While); // (반복)
    	Expression e = expression(); 
    	match(TokenType.MeaningLessIf); // (이라면 | 라면)
    	
    	s = statements(); 
    	match(TokenType.ControlEnd); // (끝)

    	return new Loop(e,s); 
    }
    
    private Input inputStatement() {
        match(TokenType.Input);
        match(TokenType.MeaningLess);
        String id = match(TokenType.Identifier);
        
        Expression arr1 = null, arr2 = null;
        if(token.type().equals(TokenType.LeftBracket)) {
           match(TokenType.LeftBracket);
           arr1 = expression();
           match(TokenType.RightBracket);
           if(token.type().equals(TokenType.LeftBracket)) {
              match(TokenType.LeftBracket);
              arr2 = expression();
               match(TokenType.RightBracket);
           }
        }
        match(TokenType.MeaningLess);
        
        codeLine = lexer.getLine();
        if(arr1 != null && arr2 != null) { //2차원 배열
        	typeChecker.valCheck(id, arr1, arr2, codeLine);
            return new Input(id, arr1, arr2);
        }else if(arr1 != null && arr2 == null) { //1차원 배열
        	typeChecker.valCheck(id, arr1, codeLine);
        	return new Input(id, arr1);
        }else{ //기본 (배열이 아닌경우)
        	typeChecker.valCheck(id, codeLine);
        	return new Input(id);
        }
     }
     
     private Output outputStatement() {
        Output out = new Output();
        assignFlag = 1;
        match(TokenType.Output);
        match(TokenType.MeaningLess);
        out.add(expression());
        
        while(token.type().equals(TokenType.Comma)) {
           match(TokenType.Comma);
           out.add(expression());
           assignFlag = 0;
        }
        match(TokenType.MeaningLess);
        
        return out;
    }

    private Expression expression () {
        //Expression --> Conjunction { 또는 Conjunction }

    	Expression e = conjunction();
    	while(token.type().equals(TokenType.Or)){ // (또는)일 경우
            Operator op = new Operator(match(token.type()));
            Expression c2 = conjunction();
            e = new Binary(op, e, c2);
        }
        return e;
    }
  
    private Expression conjunction () {
    	// Conjunction --> Equality { 그리고 Equality }
    	
    	Expression e = equality();
	    while(token.type().equals(TokenType.And)){ // (그리고)일 경우 
	        Operator op = new Operator(match(token.type()));
	        Expression e2 = equality();
	        e = new Binary(op, e, e2);
	    }
	    return e;
    }
  
    private Expression equality () {
        // Equality -> Relation [ (은 | 는) Relation (와 | 과) EquOp ]
    	// 두 식이 (같은지|다른지) 확인
    	
    	Expression e = relation();
    	if(token.type().equals(TokenType.MeaningLess) && assignFlag == 0
    			&& !token.type().equals(TokenType.MeaningLessIf) && pass == 0){
        	match(TokenType.MeaningLess); // (은|는)
        	Expression r2 = relation();
        	if(token.type().equals(TokenType.MeaningLessIf)){
        		Operator op = new Operator(match(token.type()));
                e = new Binary(op, e, r2);
                return e;
        	}
        	
        	match(TokenType.MeaningLess);
        	
        	if(isEqualityOp()){
        		Operator op = new Operator(match(token.type()));
                e = new Binary(op, e, r2);
                return e;
        	}
        }
    	
        pass = 0 ;
        
    	return e;
    }
    
    
    private Expression relation (){
        // Relation --> Addition [ (은 | 는) Addition 보다 RelOp ]
    	// 두 식의 대소 관계를 확인
    	
    	Expression e = addition();
    	if(token.type().equals(TokenType.MeaningLess) && assignFlag == 0
    			&& !token.type().equals(TokenType.MeaningLessIf)
    			&& !token.type().equals(TokenType.MeaningLessThan)
    			&& !token.type().equals(TokenType.MeaningLessWith)){
    		
        	match(TokenType.MeaningLess); //(은|는)
            Expression r  = relation();
        	
            if(token.type().equals(TokenType.MeaningLessWith) || token.type().equals(TokenType.MeaningLessThan) ){
        		if(token.type().equals(TokenType.MeaningLessThan)){
        			match(TokenType.MeaningLessThan);
                	if (isRelationalOp()) {
                        Operator op = new Operator(match(token.type()));
                        e = new Binary(op, e, r);
                    }
        		}
        		else{
        			match(TokenType.MeaningLessWith);
        			if(isEqualityOp()){
	            		Operator op = new Operator(match(token.type()));
	                    e = new Binary(op, e, r);
	                    pass = 1;           	
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
    	// Term --> Factor { ( { MulOp Factor } | { (을 | 를) Factor 로 나눈 나머지 } ) }
        Expression e = factor();
        while ((isMultiplyOp() ||token.type().equals(TokenType.MeaningLessRemain))) {
        	if(token.type().equals(TokenType.MeaningLessRemain)){
            	match(TokenType.MeaningLessRemain); //(을|를)
                Expression term2 = factor();
            	match(TokenType.MeaningLess); // (로 나눈 나머지)
	            Operator op = new Operator(match(token.type()));
	            e = new Binary(op, e, term2);
        	}
        	else{
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
    	// Primary --> Identifier [ [Expression] ] | Literal | ( Expression ) | 
        // 배열ex) ㄹㄹ은 floatv[0] 더하기 23이다
    	Expression e = null;
        
        if (token.type().equals(TokenType.Identifier)) {
            
        	String temp = match(TokenType.Identifier);

        	Expression arrA = null, arrB = null;
        	
        	if(token.type().equals(TokenType.LeftBracket)){ // [ 을 만났을 때 == 배열인 경우 
            	match(TokenType.LeftBracket);
            	arrA = expression();
            	match(TokenType.RightBracket);
            	if(token.type().equals(TokenType.LeftBracket)){ // [ 을 또 만났을 때 == 이차원 배열인 경우
            		match(TokenType.LeftBracket);
            		arrB = expression();
                	match(TokenType.RightBracket);
            	}
        	}
        	
        	codeLine = lexer.getLine();
        	if(arrA != null  && arrB != null) {
        		e = new Variable(temp, arrA, arrB);
        		typeChecker.valCheck(temp, arrA, arrB, codeLine);
        	}else if(arrA != null  && arrB == null) {
        		e = new Variable(temp, arrA);
        		typeChecker.valCheck(temp, arrA, codeLine);
        	}else {
        		e = new Variable(temp);
        		typeChecker.valCheck(temp, codeLine);
        	}
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
        else{
        	codeLine = lexer.getLine();
        	error(codeLine + " Identifier | Literal | ( | Type");
        }
   
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
    	else if (token.type().equals(TokenType.True)){
    		value = new BoolValue(true);
		    match(TokenType.True); 
		}
	    else if(token.type().equals(TokenType.False)){
			value = new BoolValue(false);
		    match(TokenType.False); 
		}
	    else{
        	codeLine = lexer.getLine();
	    	error(codeLine + " ilegal literal");
	    }

    	
        return value;
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
        return token.type().equals(TokenType.Less) 		
        	|| token.type().equals(TokenType.LessEqual)
        	|| token.type().equals(TokenType.Greater)
        	|| token.type().equals(TokenType.GreaterEqual);
    }
    
    private boolean isType( ) {
        return token.type().equals(TokenType.Int)
            || token.type().equals(TokenType.Bool) 
            || token.type().equals(TokenType.Float)
            || token.type().equals(TokenType.Char);
    }
    
    private boolean isLiteral( ) {
        return token.type().equals(TokenType.IntLiteral)
        	|| token.type().equals(TokenType.FloatLiteral)
        	|| token.type().equals(TokenType.CharLiteral)
        	|| isBooleanLiteral();
    }
    
    private boolean isBooleanLiteral( ) {
        return token.type().equals(TokenType.True)
        	|| token.type().equals(TokenType.False);
    }
    
    private boolean isStatement() {
        return token.type().equals(TokenType.While) ||
               token.type().equals(TokenType.If) ||
               token.type().equals(TokenType.Identifier) ||
               token.type().equals(TokenType.Input) ||
               token.type().equals(TokenType.Output) ||
               token.type().equals(TokenType.ControlEnd) ||
               token.type().equals(TokenType.Eof);
     }
    
    public static void main(String args[]) {
    	System.out.println(System.getProperty("user.dir"));
    	//Parser parser  = new Parser(lexer);
    	Parser parser  = new Parser(new Lexer("C:\\Users\\HYEJI\\eclipse-workspace\\compiler_teamproject_save\\src\\compiler_teamproject_save\\test.txt"));
        Program prog = parser.program();
        prog.display();           // display abstract syntax tree
    } //main

} // Parser