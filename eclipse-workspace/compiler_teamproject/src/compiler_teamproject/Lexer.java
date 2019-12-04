package compiler_teamproject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class Lexer {
   
    private char ch = ' '; 
    private BufferedReader input;
    private String line = "";
    private int col = 1;
    private final char eolnCh = '\n';
    private final char eofCh = '\004';
    private static final String[] postpositions = { 
   		 "Àº", "´Â","ÀÌ", "°¡",  "À»", "¸¦","°°´Ù","´Ù¸£´Ù",
             "ÀÌ´Ù", "´Ù","¸é","¶ó¸é","ÀÌ¶ó¸é","Âü", "°ÅÁþ",
            "¿Í", "°ú", "·Î", "º¸´Ù","À¸·Î"
   };
    

    
    private int codeLine = 1;
    
    private Token postposition = null;
    
    public Lexer (String fileName) { // source filename
    	input = null;
                
    	try {
    	   	input = new BufferedReader(
                  new InputStreamReader(
                        new FileInputStream(fileName),"UTF-8"
                        )
                  );
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
            System.exit(1);
        }
        catch (UnsupportedEncodingException e) {
           System.out.println("Unsupported Encoding: " + fileName);
            System.exit(1);
        }
    }
    
    private char nextChar() { // Return next char
        if (ch == eofCh)
            error("Attempt to read past end of file");
        col++;
        if (col >= line.length()) {
            try {
                line = input.readLine( );
            } catch (IOException e) {
                System.err.println(e);
                System.exit(1);
            } // try
            if (line == null) // at end of file
                line = "" + eofCh;
            else {
                // System.out.println(lineno + ":\t" + line);
                line += eolnCh;
            } // if line
            col = 0;
        } // if col
        return line.charAt(col);
    }
    
    public Token next() {
       if(postposition != null){
          Token temp = postposition;
          postposition = null;
          return temp;
       }
       do {
          if(isLetter(ch)) {
             String spelling = concat(true, true);
             String[] seperated = seperatePostposition(spelling);
         //    System.out.println("--"+seperated[0]+"--"+seperated[1]);
             if(seperated[1].length() > 0){
                if(seperated[0].length() == 0)
                   return Token.keyword(seperated[1]);
                postposition = Token.keyword(seperated[1]);
                return Token.keyword(seperated[0]);
             }
             return Token.keyword(seperated[0]);
          }
          else if(isDigit(ch)) {
             String number = concat(false, true);
             if(ch != '.')
                return Token.mkIntLiteral(number);
             number += concat(false, true);
             return Token.mkFloatLiteral(number);
          }
          else switch (ch) {
            case ' ': case '\t': case '\r': case eolnCh:
                if(ch == eolnCh) {
                	codeLine++;
                }
            	ch = nextChar();
                break;
            /**
            case eolnCh: 
                ch = nextChar();
                line += 1;
                break;
            **/
            case '/':
               ch = nextChar();
                if (ch != '/')  error("not correct token, processing comment //");
                // comment
                do {
                    ch = nextChar();
                } while (ch != eolnCh);
                ch = nextChar();
                break;
            case '\'':  // char literal
                char ch1 = nextChar();
                nextChar(); // get '
                ch = nextChar();
                return Token.mkCharLiteral("" + ch1);
            case eofCh: return Token.eofTok;
            case '(' : ch = nextChar();
              return Token.lParenTok;
           case ')' : ch = nextChar();
              return Token.rParenTok;
           case '[' : ch = nextChar();
              return Token.lBracketTok;   
           case ']' : ch = nextChar();
              return Token.rBracketTok;
           case 'x' : ch = nextChar();
              return Token.arrayExprTok;
           case ',' : ch = nextChar();
            return Token.commaTok;
         default: error("Illegal character" + ch);
          } // switch
       } while (true);
    } // next
    
   private boolean isKorean(char c) {
      return c >='°¡' && c <= 'ÆR' ||
            c >=12593 && c <= 12686;
   }
    
    private boolean isLetter(char c) {
       return isKorean(c) || c>='a' && c<='z' || c>='A' && c<='Z';
    }
    
    private boolean isDigit(char c) {
        return (c >= '0' && c <='9');  // student exercise
    }
    
    private String concat(boolean letter, boolean digit) {
        String r = "";
        do {
            r += ch;
            ch = nextChar();
        } while (letter && isLetter(ch) || digit && isDigit(ch));
        return r;
    }
    
    private String[] seperatePostposition(String spelling) {
       String[] seperated = new String[2];
       int size = spelling.length() - 1;
       int i;
       if(size < 3)
          i = 0;
       else 
          i = size - 3;
       for(; i<= size; i++) {
          String candidate = spelling.substring(i);
          if(isPostposition(candidate)) {
             seperated[0] = spelling.substring(0, i);
             seperated[1] = spelling.substring(i);
             return seperated;
          }
       }
       seperated[0] = spelling;
       seperated[1] = "";
       return seperated;
    }
    private boolean isPostposition(String candidate) {
       for(String pp : postpositions) {
          if(candidate.equals(pp))
             return true;
       }
       return false;
    }

    
    public void error (String msg) {
        System.err.print(line);
        System.err.println("Error: column " + col + " " + msg);
        System.exit(1);
    }
    
    public int getLine() {
    	return codeLine;
    }

    static public void main ( String[] argv ) {
    	Lexer lexer = new Lexer("C:\\Users\\HYEJI\\eclipse-workspace\\compiler_teamproject_save\\src\\compiler_teamproject_save\\test.txt");
        Token tok = lexer.next( );
        while (tok != Token.eofTok) {
            System.out.println(tok.toString());
            tok = lexer.next( );
        } 
    } // main


}
