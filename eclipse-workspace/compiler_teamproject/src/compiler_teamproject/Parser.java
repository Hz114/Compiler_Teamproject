package compiler_teamproject;

import java.util.ArrayList;

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
    	// ���α׷��� �����Ѵ�  ũ�� ����ο� ����η� ���� 
        // Program --> Declarations Statements
    	
    	Declarations d = declarations();
        Block b = statements();
        
        return new Program(d,b);
    }
  
    private Declarations declarations () {
    	// ����δ� ������ ���� �ɼ� �ִ� (0�Ǵ� n)
        // Declarations --> { Declaration }
    	
    	Declarations decls = new Declarations();
    	
    	while(isType()){
    		declaration(decls);
    	}
    	
        return decls;
    }
  
    private void declaration (Declarations decls) {
    	// ���� ����ο� ���� Type�� identifier�� ������
        // Declaration --> Type Identifier (��/��)[ '['Integer']' [ '['Integer']' ] ]
    	//										{ ,Identifier  [ '['Integer']' [ '['Integer']'] ] } (�̴�/��)
        
    	Variable var;
    	Variable arrA, arrB; //�迭�� ũ�⸦ �����ϴ� ����
    	Declaration decl ;
    	Type t = type();
   
    	int cnt = 0;
    	
    	match(TokenType.MeaningLess); // (��/��)
		
    	do {
    		
    		//�迭 ũ�⸦ �ʱ�ȭ ��Ų��.
    		arrA = null;
    		arrB = null;
    		
    		if(cnt++ != 0) {
    			token = lexer.next();
    		}
    		
    		var = new Variable(match(TokenType.Identifier)); // ������
        	
        	if(token.type().equals(TokenType.LeftBracket)){ // [ �� ������ �� == �迭�� ��� 
            	match(TokenType.LeftBracket);
            	arrA = new Variable(match(TokenType.IntLiteral));
            	match(TokenType.RightBracket);
            	if(token.type().equals(TokenType.LeftBracket)){ // [ �� �� ������ �� == ������ �迭�� ���
            		match(TokenType.LeftBracket);
            		arrB = new Variable(match(TokenType.IntLiteral));
                	match(TokenType.RightBracket);
            	}
        	}    
        	
        	if(arrA == null && arrB == null) { //�迭�� �ƴѰ�� 
        		decl = new Declaration(var,t);
        		decls.add(decl);
        	}else if(arrA != null && arrB == null) { //1���� �迭
        		decl = new Declaration(var,t, arrA);
        		decls.add(decl);
        	}else if(arrA != null && arrB != null) { //2ġ�� �迭
        		decl = new Declaration(var,t, arrA, arrB);
        		decls.add(decl);
        	}
    	}while(token.type().equals(TokenType.Comma));
    	
    	match(TokenType.MeaningLess); // (�̴�/��)
    }
    	
    	
    
  
    private Type type () {
        // Type --> ���� | ���� | �Ҽ� | ����
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
        
        return b;
    }
  
    
    private Assignment assignment () {
    	// Assignment --> identifier (�� | ��) expression [��]��
    	// �迭 ex) ����[0][1]�� (3 ���ϱ� ����) ������ ��cc01�̴�
    	
    	assignFlag = 1;
    	Variable t = new Variable(match(TokenType.Identifier));
    	//e = new Variable(match(TokenType.Identifier));
        
        //*****�迭Ȯ��*******
    	Expression arrA = null, arrB = null;
    	
    	if(token.type().equals(TokenType.LeftBracket)){ // [ �� ������ �� == �迭�� ��� 
        	match(TokenType.LeftBracket);
        	arrA = expression();
        	match(TokenType.RightBracket);
        	if(token.type().equals(TokenType.LeftBracket)){ // [ �� �� ������ �� == ������ �迭�� ���
        		match(TokenType.LeftBracket);
        		arrB = expression();
            	match(TokenType.RightBracket);
        	}
    	}
    	//*****�迭Ȯ�� ��*****
    	
    	match(TokenType.MeaningLess); // (��|��)

    	Expression e = expression();
    	match(TokenType.MeaningLess); // (��|�̴�)
    	assignFlag = 0;
    	

    	if(arrA != null && arrB != null) { //2���� �迭
        	return new Assignment(t,e, arrA, arrB);
    	}else if(arrA != null && arrB == null) { //1���� �迭
        	return new Assignment(t,e, arrA);
    	}else{ //�⺻ (�迭�� �ƴѰ��)
        	return new Assignment(t,e);
    	}
    }
  
    private Conditional ifStatement () {
        // IfStatement --> ���� 'expression' (�̶�� | ���) statements �� [ �׷��� ������  statements ��] 
    	Statement s = new Skip();

    	match(TokenType.If); // ����
    	Expression e = expression();

    	match(TokenType.MeaningLessIf); // (�̶�� |���) 
    	s = statements();
    	
    	if(token.type().equals(TokenType.Else)){ // �׷����������� ���
    		match(TokenType.Else); // (�׷���������)
    		Statement elseS = statement();
        	match(TokenType.ControlEnd); // (��)

    		return new Conditional(e,s,elseS); // ���� 'expression' (�̶�� | ���) statements ��  �׷��� ������  statements ��
    	}
    	else {
        	match(TokenType.ControlEnd); // (��)
        	return new Conditional(e,s); // ���� 'expression' (�̶�� | ���) statements �� 
    	}
    }
  
    private Loop whileStatement () {
        // WhileStatement --> �ݺ� 'expression' (�̶�� | ���) statements (��)
    	Statement s = new Skip();
    	
    	match(TokenType.While); // (�ݺ�)
    	Expression e = expression(); 
    	match(TokenType.MeaningLessIf); // (�̶�� | ���)
    	
    	s = statements(); 
    	match(TokenType.ControlEnd); // (��)

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
        match(TokenType.MeaningLess); // �߰�!!!!!!!!!!!!!!!
        
        if(arr1 != null && arr2 != null) { //2���� �迭
            return new Input(id, arr1, arr2);
        }else if(arr1 != null && arr2 == null) { //1���� �迭
            return new Input(id, arr1);
        }else{ //�⺻ (�迭�� �ƴѰ��)
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
        match(TokenType.MeaningLess); // �߰�!!!!!!!!!!!!!!!
        
        return out;
    }

    private Expression expression () {
        //Expression --> Conjunction { �Ǵ� Conjunction }

    	Expression e = conjunction();
    	while(token.type().equals(TokenType.Or)){ // (�Ǵ�)�� ���
            Operator op = new Operator(match(token.type()));
            Expression c2 = conjunction();
            e = new Binary(op, e, c2);
        }
        return e;
    }
  
    private Expression conjunction () {
    	// Conjunction --> Equality { �׸��� Equality }
    	
    	Expression e = equality();
	    while(token.type().equals(TokenType.And)){ // (�׸���)�� ��� 
	        Operator op = new Operator(match(token.type()));
	        Expression e2 = equality();
	        e = new Binary(op, e, e2);
	    }
	    return e;
    }
  
    private Expression equality () {
        // Equality -> Relation [ (�� | ��) Relation (�� | ��) EquOp ]
    	// �� ���� (������|�ٸ���) Ȯ��
    	
    	Expression e = relation();
    	if(token.type().equals(TokenType.MeaningLess) && assignFlag == 0
    			&& !token.type().equals(TokenType.MeaningLessIf) && pass == 0){
        	match(TokenType.MeaningLess); // (��|��)
        	Expression r2 = relation();
        	match(TokenType.MeaningLess); // (��|��)
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
        // Relation --> Addition [ (�� | ��) Addition ���� RelOp ]
    	// �� ���� ��� ���踦 Ȯ��
    	
    	Expression e = addition();
    	if(token.type().equals(TokenType.MeaningLess) && assignFlag == 0
    			&& !token.type().equals(TokenType.MeaningLessIf)
    			&& !token.type().equals(TokenType.MeaningLessThan)
    			&& !token.type().equals(TokenType.MeaningLessWith)){
    		
        	match(TokenType.MeaningLess); //(��|��)
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
    	// Term --> Factor { ( { MulOp Factor } | { (�� | ��) Factor �� ���� ������ } ) }
        Expression e = factor();
        while ((isMultiplyOp() ||token.type().equals(TokenType.MeaningLessRemain))) {
        	if(token.type().equals(TokenType.MeaningLessRemain)){
            	match(TokenType.MeaningLessRemain); //(��|��)
                Expression term2 = factor();
            	match(TokenType.MeaningLess); // (�� ���� ������)
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
        // �迭ex) ������ floatv[0] ���ϱ� 23�̴�
    	Expression e = null;
        
        if (token.type().equals(TokenType.Identifier)) {
            
        	String temp = match(TokenType.Identifier);
        	//e = new Variable(match(TokenType.Identifier));
          
            //*****�迭Ȯ��*******
        	
        	Expression arrA = null, arrB = null;
        	
        	if(token.type().equals(TokenType.LeftBracket)){ // [ �� ������ �� == �迭�� ��� 
            	match(TokenType.LeftBracket);
            	arrA = expression();
            	match(TokenType.RightBracket);
            	if(token.type().equals(TokenType.LeftBracket)){ // [ �� �� ������ �� == ������ �迭�� ���
            		match(TokenType.LeftBracket);
            		arrB = expression();
                	match(TokenType.RightBracket);
            	}
        	}
        	//*****�迭Ȯ�� ��*****
        	
        	if(arrA != null  && arrB != null) {
        		e = new Variable(temp, arrA, arrB);
        	}else if(arrA != null  && arrB == null) {
        		e = new Variable(temp, arrA);
        	}else {
        		e = new Variable(temp);
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
    
    public static void main(String args[]) {
    	System.out.println(System.getProperty("user.dir"));
        Parser parser  = new Parser(new Lexer("C:\\Users\\HYEJI\\Documents\\īī���� ���� ����\\test4.txt"));
        Program prog = parser.program();
        prog.display();           // display abstract syntax tree
    } //main

} // Parser