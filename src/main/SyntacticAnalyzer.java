package main;

/*
 * 
 * 
unit: ( declStruct | declFunc | declVar )* END ;
declStruct: STRUCT ID LACC declVar* RACC SEMICOLON ;

declVar:  typeBase ID arrayDecl? ( COMMA ID arrayDecl? )* SEMICOLON ;

typeBase: INT | DOUBLE | CHAR | STRUCT ID ;

arrayDecl: LBRACKET expr? RBRACKET ;

typeName: typeBase arrayDecl? ;

//Because in AtomC there are no pointers,
//in order to respect the C syntax which does not allow a function to return vectors
//an AtomC function can return the construction "type *", which is equivalent with "type []".
//In this way the AtomC functions can return vectors references
declFunc: ( typeBase MUL? | VOID ) ID 
                    LPAR ( funcArg ( COMMA funcArg )* )? RPAR 
                    stmCompound ;

funcArg: typeBase ID arrayDecl? ;

stm: stmCompound 
       | IF LPAR expr RPAR stm ( ELSE stm )?
       | WHILE LPAR expr RPAR stm
       | FOR LPAR expr? SEMICOLON expr? SEMICOLON expr? RPAR stm
       | BREAK SEMICOLON
       | RETURN expr? SEMICOLON
       | expr? SEMICOLON ;

stmCompound: LACC ( declVar | stm )* RACC ;

expr: exprAssign ;

exprAssign: exprUnary ASSIGN exprAssign | exprOr ;

exprOr: exprOr OR exprAnd | exprAnd ;

exprAnd: exprAnd AND exprEq | exprEq ;

exprEq: exprEq ( EQUAL | NOTEQ ) exprRel | exprRel ;

exprRel: exprRel ( LESS | LESSEQ | GREATER | GREATEREQ ) exprAdd | exprAdd ;

exprAdd: exprAdd ( ADD | SUB ) exprMul | exprMul ;

exprMul: exprMul ( MUL | DIV ) exprCast | exprCast ;

exprCast: LPAR typeName RPAR exprCast | exprUnary ;

exprUnary: ( SUB | NOT ) exprUnary | exprPostfix ;

exprPostfix: exprPostfix LBRACKET expr RBRACKET
       | exprPostfix DOT ID 
       | exprPrimary ;

exprPrimary: ID ( LPAR ( expr ( COMMA expr )* )? RPAR )?
       | CT_INT
       | CT_REAL 
       | CT_CHAR 
       | CT_STRING 
       | LPAR expr RPAR ;

 */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class SyntacticAnalyzer {
	List<Token> tokens = new ArrayList<Token>();
	Map<String, String> variables = new TreeMap<String, String>();
	int i;

	public SyntacticAnalyzer(List<Token> tokens) {
		this.tokens = tokens;
	}

	// syntax from CT lab
	public void unit(int i) throws AnalyzerException {
		// unit: ( declStruct | declFunc | declVar )* END ;
		int state = 1;
		while (true) {
			switch (state) {
			case 1:
				if (declStruct(i))// true
				{
					state = 1;
					this.i++;
					i = this.i;
				} else {
					state = 2;
				}
				break;
			case 2:
				if (declFunc(i)) {
					state = 1;
					this.i++;
					i = this.i;
				} else {
					state = 3;
				}
				break;
			case 3:
				if (declVar(i)) {
					state = 1;
					this.i++;
					i = this.i;
				} else {
					state = 4;
				}
				break;
			case 4:
				if (tokens.get(i).getTokenType().equals(TokenType.END)) {
					System.out.println("End of program\nThe program has been successfuly compilated");
					break;
				} else {
					throw new AnalyzerException("Expected END");
				}
			}
		}
	}

	public boolean declStruct(int i) throws AnalyzerException {
		// declStruct: STRUCT ID LACC declVar* RACC SEMICOLON ;
		int state = 0;
		while (true) {
			switch (state) {
			case 0:
				if (tokens.get(i).getTokenType().equals(TokenType.Struct)) {
					state = 1;
					this.i++;
					i = this.i;
				} else {
					i++;
					this.i = i;
					return false;
					// throw new AnalyzerException("Expected Struct",tokens.get(i).getBegin());
				}
				break;
			case 1:
				if (tokens.get(i).getTokenType().equals(TokenType.Identifier)) {
					state = 2;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected Identifier", tokens.get(i).getBegin());
				}
				break;
			case 2:
				if (tokens.get(i).getTokenType().equals(TokenType.OpeningCurlyBrace)) {
					state = 3;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected OpeningCurlyBrace", tokens.get(i).getBegin());
				}
				break;
			case 3:
				if (declVar(i)) {
					state = 3;
					this.i++;
					i = this.i;
				} else if (tokens.get(i).getTokenType().equals(TokenType.ClosingCurlyBrace)) {
					state = 4;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected ClosingCurlyBrace", tokens.get(i).getBegin());
				}
				break;
			case 4:
				if (tokens.get(i).getTokenType().equals(TokenType.Semicolon)) {
					i++;
					this.i = i;
					return true;
				} else {
					throw new AnalyzerException("Expected Semicolon", tokens.get(i).getBegin());
				}
			}
		}
	}

	public boolean declVar(int i) throws AnalyzerException {
		// declVar: typeBase ID arrayDecl? ( COMMA ID arrayDecl? )* SEMICOLON ;
		int state = 0;
		while (true) {
			switch (state) {
			case 0:
				if (typeBase(i)) {
					state = 1;
					this.i++;
					i = this.i;
				} else {
					return false;
					// throw new AnalyzerException("Expected typeBase at:
					// ",tokens.get(i).getBegin());
				}
				break;
			case 1:
				if (tokens.get(i).getTokenType().equals(TokenType.Identifier)) {
					state = 2;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected Identifier at: ", tokens.get(i).getBegin());
				}
				break;
			case 2:
				if (arrayDecl(i)) {
					state = 2;
					this.i++;
					i = this.i;
				} else {
					state = 3;// no array, just skip
				}
				break;
			// from here somehow loop, we check for another declVar
			case 3:
				if (tokens.get(i).getTokenType().equals(TokenType.Comma)) {
					state = 4;
					i++;
					this.i = i;
				} else if (tokens.get(i).getTokenType().equals(TokenType.Semicolon)) {
					i++;
					this.i = i;
					return true;
				} else {
					throw new AnalyzerException("Expected , or ; at: ", tokens.get(i).getBegin());
				}
				break;
			case 4:
				if (tokens.get(i).getTokenType().equals(TokenType.Identifier)) {
					state = 5;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected Identifier at: ", tokens.get(i).getBegin());
				}
				break;
			case 5:
				if (arrayDecl(i)) {
					state = 5;
					this.i++;
					i = this.i;
				} else {
					state = 3;// no array, go back to case 3 to check if: another declVar or semicolon
					// i++;//not sure
				}
				break;
			}
		}
	}

	public boolean typeBase(int i) throws AnalyzerException {
		// declVar: typeBase ID arrayDecl? ( COMMA ID arrayDecl? )* SEMICOLON ;
		int state = 0;
		while (true) {
			switch (state) {
			case 0:
				if (tokens.get(i).getTokenType().equals(TokenType.Int)) {
					state = 1;
					i++;
					this.i = i;
				} else if (tokens.get(i).getTokenType().equals(TokenType.Double)) {
					state = 1;
					i++;
					this.i = i;
				} else if (tokens.get(i).getTokenType().equals(TokenType.Char)) {
					state = 1;
					i++;
					this.i = i;
				} else if (tokens.get(i).getTokenType().equals(TokenType.Struct)) {
					state = 1;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected INT | DOUBLE | CHAR | STRUCT at: ", tokens.get(i).getBegin());
				}
				break;
			case 1:
				if (tokens.get(i).getTokenType().equals(TokenType.Identifier)) {
					this.i = i;
					return true;
				} else {
					throw new AnalyzerException("Expected Identifier at: ", tokens.get(i).getBegin());
				}
			}
		}
	}

	public boolean arrayDecl(int i) throws AnalyzerException {
		int state = 0;
		while (true) {
			switch (state) {
			case 0:
				if (tokens.get(i).getTokenType().equals(TokenType.OpenStraightBrace)) {
					state = 1;
					i++;
					this.i = i;
				} else {
					return false;
					// throw new AnalyzerException("Expected OpenStraightBrace at:
					// ",tokens.get(i).getBegin());
				}
				break;
			case 1:
				if (expr(i)) {
					state = 2;
					this.i++;
					i = this.i;
				} else {
					throw new AnalyzerException("Expected expr at: ", tokens.get(i).getBegin());
				}
				break;
			case 2:
				if (tokens.get(i).getTokenType().equals(TokenType.CloseStraightBrace)) {
					this.i = i;
					return true;

				} else {
					throw new AnalyzerException("Expected CloseStraightBrace at: ", tokens.get(i).getBegin());
				}
			}
		}
	}

	public boolean typeName(int i) throws AnalyzerException {
		// typeName: typeBase arrayDecl? ;
		int state = 0;
		while (true) {
			switch (state) {
			case 0:
				if (typeBase(i)) {
					state = 1;
					this.i++;
					i = this.i;
				} else {
					throw new AnalyzerException("Expected typeBase at: ", tokens.get(i).getBegin());
				}
				break;
			case 1:
				if (arrayDecl(i)) {
					this.i = i;// vector
					return true;
				} else {
					this.i = i;// not vector
					return true;
				}
			}
		}
	}

	public boolean declFunc(int i) throws AnalyzerException {
		/*
		 * eclFunc: ( typeBase MUL? | VOID ) ID LPAR ( funcArg ( COMMA funcArg )* )?
		 * RPAR stmCompound ;
		 */
		int state = 0;
		while (true) {
			switch (state) {
			case 0:
				if (typeBase(i)) {
					state = 1;// verify if type
					this.i++;
					i = this.i;
				} else {
					state = 2;// verify if it's void
				}
				break;
			case 1:
				if (tokens.get(i).getTokenType().equals(TokenType.Multiply)) {
					state = 3;// vector
					i++;
					this.i = i;
				} else {
					state = 3;// not vector
					i++;
					this.i = i;
				}
				break;
			case 2:
				if (tokens.get(i).getTokenType().equals(TokenType.Void)) {
					state = 3;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected typeBase or void at: ", tokens.get(i).getBegin());
				}
				break;
			case 3:
				if (tokens.get(i).getTokenType().equals(TokenType.Identifier)) {
					state = 4;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected Identifier at: ", tokens.get(i).getBegin());
				}
				break;
			case 4:
				if (tokens.get(i).getTokenType().equals(TokenType.OpenBrace)) {
					state = 5;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected OpenBrace at: ", tokens.get(i).getBegin());
				}
				break;
			case 5:
				if (funcArg(i)) {
					state = 6;// go to verify if comma and other arguments
					this.i++;
					i = this.i;
				} else {
					state = 6;// no arguments, then verify if CloseBrace
				}
				break;
			case 6:
				if (tokens.get(i).getTokenType().equals(TokenType.Comma)) {
					state = 7;
					i++;
					this.i = i;
				} else if (tokens.get(i).getTokenType().equals(TokenType.CloseBrace)) {
					state = 8;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected Comma OR CloseBrace at: ", tokens.get(i).getBegin());
				}
				break;
			case 7:
				if (funcArg(i)) {
					state = 6;// if another funcArg, go to case 6 to check next is comma or closebrace
					this.i++;
					i = this.i;
				} else {
					throw new AnalyzerException("Expected another funcArg at: ", tokens.get(i).getBegin());
				}
				break;
			case 8:
				if (stmCompound(i)) {
					this.i++;
					i = this.i;
					return true;
				} else {
					throw new AnalyzerException("Expected stmCompound at: ", tokens.get(i).getBegin());
				}
			}
		}
	}

	public boolean funcArg(int i) throws AnalyzerException {
		/*
		 * funcArg: typeBase ID arrayDecl? ;
		 */
		int state = 0;
		while (true) {
			switch (state) {
			case 0:
				if (typeBase(i)) {
					state = 1;
					this.i++;
					i = this.i;
				} else {
					throw new AnalyzerException("Expected typeBase at: ", tokens.get(i).getBegin());
				}
				break;
			case 1:
				if (tokens.get(i).getTokenType().equals(TokenType.Identifier)) {
					state = 2;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected Identifier at: ", tokens.get(i).getBegin());
				}
				break;
			case 2:
				if (arrayDecl(i)) {
					this.i = i;
					return true;// funcArg is simple;
				} else {
					this.i = i;
					return true;// funcArg is arrayType;
				}
			}
		}
	}

	public boolean stm(int i) throws AnalyzerException {
		/*
		 * stm: stmCompound | IF LPAR expr RPAR stm ( ELSE stm )? | WHILE LPAR expr RPAR
		 * stm | FOR LPAR expr? SEMICOLON expr? SEMICOLON expr? RPAR stm | BREAK
		 * SEMICOLON | RETURN expr? SEMICOLON | expr? SEMICOLON ;
		 */
		int state = 0;
		while (true) {
			switch (state) {
			case 0:
				if (stmCompound(i)) {
					this.i++;
					i = this.i;
					return true;
				} else {
					state = 1;
				}
				break;
			case 1:
				if (ifStm(i)) {
					this.i++;
					i = this.i;
					return true;
				} else {
					state = 2;
				}
				break;
			case 2:
				if (whileStm(i)) {
					this.i++;
					i = this.i;
					return true;
				} else {
					state = 3;
				}
				break;
			case 3:
				if (forStm(i)) {
					this.i++;
					i = this.i;
					return true;
				} else {
					state = 4;
				}
				break;
			case 4:
				if (breakStm(i)) {
					this.i++;
					i = this.i;
					return true;
				} else {
					state = 5;
				}
				break;
			case 5:
				if (returnStm(i)) {
					this.i++;
					i = this.i;
					return true;
				} else {
					state = 6;
				}
				break;
			case 6:
				if (exprStm(i)) {
					this.i++;
					i = this.i;
					return true;
				} else {
					throw new AnalyzerException("Expected stm at: ", tokens.get(i).getBegin());
				}
			}
		}
	}

	public boolean ifStm(int i) throws AnalyzerException {
		// IF LPAR expr RPAR stm ( ELSE stm )?
		int state = 0;
		while (true) {
			switch (state) {
			case 0:
				if (tokens.get(i).getTokenType().equals(TokenType.If)) {
					state = 1;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected If at: ", tokens.get(i).getBegin());
				}
				break;
			case 1:
				if (tokens.get(i).getTokenType().equals(TokenType.OpenBrace)) {
					state = 2;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected OpenBrace at: ", tokens.get(i).getBegin());
				}
				break;
			case 2:
				if (expr(i)) {
					state = 3;
					this.i++;
					i = this.i;
				} else {
					throw new AnalyzerException("Expected expr at: ", tokens.get(i).getBegin());
				}
				break;
			case 3:
				if (tokens.get(i).getTokenType().equals(TokenType.CloseBrace)) {
					state = 4;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected CloseBrace at: ", tokens.get(i).getBegin());
				}
				break;
			case 4:
				if (stm(i)) {
					state = 5;
					this.i++;
					i = this.i;
				} else {
					throw new AnalyzerException("Expected stm at: ", tokens.get(i).getBegin());
				}
				break;
			case 5:
				if (tokens.get(i).getTokenType().equals(TokenType.Else)) {
					state = 6;
					i++;
					this.i = i;
				} else {
					this.i = i;
					return true;
				}
				break;
			case 6:
				if (stm(i)) {
					this.i = i;
					return true;
				} else {
					throw new AnalyzerException("Expected else stm at: ", tokens.get(i).getBegin());
				}
			}
		}

	}

	public boolean whileStm(int i) throws AnalyzerException {
		int state = 0;
		while (true) {
			switch (state) {
			case 0:
				if (tokens.get(i).getTokenType().equals(TokenType.While)) {
					state = 1;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected While at: ", tokens.get(i).getBegin());
				}
				break;
			case 1:
				if (tokens.get(i).getTokenType().equals(TokenType.OpenBrace)) {
					state = 2;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected OpenBrace at: ", tokens.get(i).getBegin());
				}
				break;
			case 2:
				if (expr(i)) {
					state = 3;
					this.i++;
					i = this.i;
				} else {
					throw new AnalyzerException("Expected expr at: ", tokens.get(i).getBegin());
				}
				break;
			case 3:
				if (tokens.get(i).getTokenType().equals(TokenType.CloseBrace)) {
					state = 4;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected CloseBrace at: ", tokens.get(i).getBegin());
				}
				break;
			case 4:
				if (stm(i)) {
					this.i = i;
					return true;
				} else {
					throw new AnalyzerException("Expected stm at: ", tokens.get(i).getBegin());
				}
			}
		}
	}

	public boolean forStm(int i) throws AnalyzerException {
		int state = 0;
		while (true) {
			switch (state) {
			case 0:
				if (tokens.get(i).getTokenType().equals(TokenType.For)) {
					state = 1;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected For at: ", tokens.get(i).getBegin());
				}
				break;
			case 1:
				if (tokens.get(i).getTokenType().equals(TokenType.OpenBrace)) {
					state = 2;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected OpenBrace at: ", tokens.get(i).getBegin());
				}
				break;
			case 2:
				if (expr(i)) {
					state = 3;
					this.i++;
					i = this.i;
				} else {
					state = 3;
				}
				break;
			case 3:
				if (tokens.get(i).getTokenType().equals(TokenType.Semicolon)) {
					state = 4;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected Semicolon at: ", tokens.get(i).getBegin());
				}
				break;
			case 4:
				if (expr(i)) {
					state = 5;
					this.i++;
					i = this.i;
				} else {
					state = 5;
				}
				break;
			case 5:
				if (tokens.get(i).getTokenType().equals(TokenType.Semicolon)) {
					state = 6;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected Semicolon at: ", tokens.get(i).getBegin());
				}
				break;
			case 6:
				if (expr(i)) {
					state = 7;
					this.i++;
					i = this.i;
				} else {
					state = 7;
				}
				break;
			case 7:
				if (tokens.get(i).getTokenType().equals(TokenType.CloseBrace)) {
					state = 8;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected CloseBrace at: ", tokens.get(i).getBegin());
				}
				break;
			case 8:
				if (stm(i)) {
					this.i++;
					i = this.i;
					return true;
				} else {
					throw new AnalyzerException("Expected stm at: ", tokens.get(i).getBegin());
				}
			}
		}
	}

	public boolean breakStm(int i) throws AnalyzerException {
		// BREAK SEMICOLON
		int state = 0;
		while (true) {
			switch (state) {
			case 0:
				if (tokens.get(i).getTokenType().equals(TokenType.Break)) {
					state = 1;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected Break at: ", tokens.get(i).getBegin());
				}
				break;
			case 1:
				if (tokens.get(i).getTokenType().equals(TokenType.Semicolon)) {
					i++;
					this.i = i;
					return true;
				} else {
					throw new AnalyzerException("Expected Semicolon at: ", tokens.get(i).getBegin());
				}
			}
		}

	}

	public boolean returnStm(int i) throws AnalyzerException {
		int state = 0;
		while (true) {
			switch (state) {
			case 0:
				if (tokens.get(i).getTokenType().equals(TokenType.Return)) {
					state = 1;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected Return at: ", tokens.get(i).getBegin());
				}
				break;
			case 1:
				if (expr(i)) {
					state = 2;
					this.i++;
					i = this.i;
				} else {
					state = 2;
				}
				break;
			case 2:
				if (tokens.get(i).getTokenType().equals(TokenType.Semicolon)) {
					i++;
					this.i = i;
					return true;
				} else {
					throw new AnalyzerException("Expected Semicolon at: ", tokens.get(i).getBegin());
				}
			}
		}
	}

	public boolean exprStm(int i) throws AnalyzerException {
		// expr? SEMICOLON
		int state = 0;
		while (true) {
			switch (state) {
			case 0:
				if (expr(i)) {
					state = 1;
					this.i++;
					i = this.i;
				} else {
					state = 1;
				}
				break;
			case 1:
				if (tokens.get(i).getTokenType().equals(TokenType.Semicolon)) {
					i++;
					this.i = i;
					return true;
				} else {
					throw new AnalyzerException("Expected Semicolon at: ", tokens.get(i).getBegin());
				}
			}
		}
	}

	public boolean stmCompound(int i) throws AnalyzerException {
		// stmCompound: LACC ( declVar | stm )* RACC ;
		int state = 0;
		while (true) {
			switch (state) {
			case 0:
				if (tokens.get(i).getTokenType().equals(TokenType.OpeningCurlyBrace)) {
					state = 1;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected OpeningCurlyBrace at: ", tokens.get(i).getBegin());
				}
				break;
			case 1:
				if (declVar(i)) {
					state = 1;
					this.i++;
					i = this.i;
				} else if (stm(i)) {
					state = 1;
					this.i++;
					i = this.i;
				} else {
					state = 2;
				}
				break;
			case 2:
				if (tokens.get(i).getTokenType().equals(TokenType.ClosingCurlyBrace)) {
					i++;
					this.i = i;
					return true;
				} else {
					throw new AnalyzerException("Expected ClosingCurlyBrace at: ", tokens.get(i).getBegin());
				}
			}
		}
	}

	public boolean expr(int i) throws AnalyzerException {
		int state = 0;
		while (true) {
			switch (state) {
			case 0:
				if (exprAssign(i)) {
					this.i++;
					i = this.i;
					return true;
				} else {
					throw new AnalyzerException("Expected exprAssign at: ", tokens.get(i).getBegin());
				}
			}
		}
	}

	public boolean exprAssign(int i) throws AnalyzerException {
		// exprAssign: exprUnary ASSIGN exprAssign | exprOr ;
		int state = 0;
		while (true) {
			switch (state) {
			case 0:
				if (exprUnary(i)) {
					state = 1;
					this.i++;
					i = this.i;
				} else {
					throw new AnalyzerException("Expected exprUnary at: ", tokens.get(i).getBegin());
				}
				break;
			case 1:
				if (tokens.get(i).getTokenType().equals(TokenType.Equal)) {
					state = 2;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected Equal at: ", tokens.get(i).getBegin());
				}
				break;
			case 2:
				if (exprAssign(i)) {
					this.i++;
					i = this.i;
					return true;
				} else if (exprOr(i)) {
					this.i++;
					i = this.i;
					return true;
				} else {
					throw new AnalyzerException("Expected exprAssign | exprOr at: ", tokens.get(i).getBegin());
				}
			}
		}
	}

	public boolean exprOr(int i) throws AnalyzerException {
		// exprOr: (exprOr OR exprAnd) | exprAnd ;
		int state = 0;
		while (true) {
			switch (state) {
			case 0:
				if (exprOr(i)) {
					state = 1;
					this.i++;
					i = this.i;
				} else if (exprAnd(i)) {
					this.i++;
					i = this.i;
					return true;
				} else {
					throw new AnalyzerException("Expected exprOr | exprAnd at: ", tokens.get(i).getBegin());
				}
				break;
			case 1:
				if (tokens.get(i).getTokenType().equals(TokenType.Or)) {
					state = 2;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected Or at: ", tokens.get(i).getBegin());
				}
				break;
			case 2:
				if (exprAnd(i)) {
					this.i++;
					i = this.i;
					return true;
				} else {
					throw new AnalyzerException("Expected exprAnd at: ", tokens.get(i).getBegin());
				}
			}
		}
	}

	public boolean exprAnd(int i) throws AnalyzerException {
		// exprAnd: (exprAnd AND exprEq) | exprEq ;
		int state = 0;
		while (true) {
			switch (state) {
			case 0:
				if (exprAnd(i)) {
					state = 1;
					this.i++;
					i = this.i;
				} else if (exprEq(i)) {
					this.i++;
					i = this.i;
					return true;
				} else {
					throw new AnalyzerException("Expected exprAnd | exprEq at: ", tokens.get(i).getBegin());
				}
				break;
			case 1:
				if (tokens.get(i).getTokenType().equals(TokenType.And)) {
					state = 2;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected And at: ", tokens.get(i).getBegin());
				}
				break;
			case 2:
				if (exprEq(i)) {
					this.i++;
					i = this.i;
					return true;
				} else {
					throw new AnalyzerException("Expected exprEq at: ", tokens.get(i).getBegin());
				}
			}
		}
	}

	public boolean exprEq(int i) throws AnalyzerException {
		// exprEq: exprEq ( EQUAL | NOTEQ ) exprRel | exprRel ;
		int state = 0;
		while (true) {
			switch (state) {
			case 0:
				if (exprEq(i)) {
					state = 1;
					this.i++;
					i = this.i;
				} else if (exprRel(i)) {
					this.i++;
					i = this.i;
					return true;
				} else {
					throw new AnalyzerException("Expected exprEq | exprRel at: ", tokens.get(i).getBegin());
				}
				break;
			case 1:
				if (tokens.get(i).getTokenType().equals(TokenType.Equal)) {
					state = 2;
					i++;
					this.i = i;
				} else if (tokens.get(i).getTokenType().equals(TokenType.ExclameEqual)) {
					state = 2;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected Equal | ExclameEqual at: ", tokens.get(i).getBegin());
				}
				break;
			case 2:
				if (exprRel(i)) {
					this.i++;
					i = this.i;
					return true;
				} else {
					throw new AnalyzerException("Expected exprRel at: ", tokens.get(i).getBegin());
				}
			}
		}
	}

	public boolean exprRel(int i) throws AnalyzerException {
		// exprRel ( LESS | LESSEQ | GREATER | GREATEREQ ) exprAdd | exprAdd ;
		int state = 0;
		while (true) {
			switch (state) {
			case 0:
				if (exprRel(i)) {
					state = 1;
					this.i++;
					i = this.i;
				} else if (exprAdd(i)) {
					this.i++;
					i = this.i;
					return true;
				} else {
					throw new AnalyzerException("Expected exprRel | exprAdd at: ", tokens.get(i).getBegin());
				}
				break;
			case 1:
				if (tokens.get(i).getTokenType().equals(TokenType.Less)) {
					state = 2;
					i++;
					this.i = i;
				} else if (tokens.get(i).getTokenType().equals(TokenType.LessEqual)) {
					state = 2;
					i++;
					this.i = i;
				} else if (tokens.get(i).getTokenType().equals(TokenType.Greater)) {
					state = 2;
					i++;
					this.i = i;
				} else if (tokens.get(i).getTokenType().equals(TokenType.GreaterEqual)) {
					state = 2;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected Less | LessEqual | Greater | GreaterEqual at: ",
							tokens.get(i).getBegin());
				}
				break;
			case 2:
				if (exprAdd(i)) {
					this.i++;
					i = this.i;
					return true;
				} else {
					throw new AnalyzerException("Expected exprAdd at: ", tokens.get(i).getBegin());
				}
			}
		}
	}

	public boolean exprAdd(int i) throws AnalyzerException {
		// exprAdd: exprAdd ( ADD | SUB ) exprMul | exprMul ;
		int state = 0;
		while (true) {
			switch (state) {
			case 0:
				if (exprAdd(i)) {
					state = 1;
					this.i++;
					i = this.i;
				} else if (exprMul(i)) {
					this.i++;
					i = this.i;
					return true;
				} else {
					throw new AnalyzerException("Expected exprEq | exprMul at: ", tokens.get(i).getBegin());
				}
				break;
			case 1:
				if (tokens.get(i).getTokenType().equals(TokenType.Plus)) {
					state = 2;
					i++;
					this.i = i;
				} else if (tokens.get(i).getTokenType().equals(TokenType.Minus)) {
					state = 2;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected Plus | Minus at: ", tokens.get(i).getBegin());
				}
				break;
			case 2:
				if (exprMul(i)) {
					this.i++;
					i = this.i;
					return true;
				} else {
					throw new AnalyzerException("Expected exprMul at: ", tokens.get(i).getBegin());
				}
			}
		}
	}

	public boolean exprMul(int i) throws AnalyzerException {
		// exprAdd: exprAdd ( ADD | SUB ) exprMul | exprMul ;
		int state = 0;
		while (true) {
			switch (state) {
			case 0:
				if (exprMul(i)) {
					state = 1;
					this.i++;
					i = this.i;
				} else if (exprCast(i)) {
					this.i++;
					i = this.i;
					return true;
				} else {
					throw new AnalyzerException("Expected exprAdd | exprCast at: ", tokens.get(i).getBegin());
				}
				break;
			case 1:
				if (tokens.get(i).getTokenType().equals(TokenType.Multiply)) {
					state = 2;
					i++;
					this.i = i;
				} else if (tokens.get(i).getTokenType().equals(TokenType.Divide)) {
					state = 2;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected Plus | Minus at: ", tokens.get(i).getBegin());
				}
				break;
			case 2:
				if (exprCast(i)) {
					this.i++;
					i = this.i;
					return true;
				} else {
					throw new AnalyzerException("Expected exprCast at: ", tokens.get(i).getBegin());
				}
			}
		}
	}

	public boolean exprCast(int i) throws AnalyzerException {
		// exprCast: LPAR typeName RPAR exprCast | exprUnary ;
		int state = 0;
		while (true) {
			switch (state) {
			case 0:
				if (tokens.get(i).getTokenType().equals(TokenType.OpenBrace)) {
					state = 1;
					i++;
					this.i = i;
				} else if (exprUnary(i)) {
					this.i++;
					i = this.i;
					return true;
				} else {
					throw new AnalyzerException("Expected OpenBrace | exprUnary at: ", tokens.get(i).getBegin());
				}
				break;
			case 1:
				if (typeName(i)) {
					state = 2;
					this.i++;
					i = this.i;
				} else {
					throw new AnalyzerException("Expected typeName at: ", tokens.get(i).getBegin());
				}
				break;
			case 2:
				if (tokens.get(i).getTokenType().equals(TokenType.CloseBrace)) {
					state = 3;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected CloseBrace at: ", tokens.get(i).getBegin());
				}
				break;
			case 3:
				if (exprCast(i)) {
					this.i++;
					i = this.i;
					return true;
				} else {
					throw new AnalyzerException("Expected exprCast at: ", tokens.get(i).getBegin());
				}
			}
		}
	}

	public boolean exprUnary(int i) throws AnalyzerException {
		// exprCast: LPAR typeName RPAR exprCast | exprUnary ;
		int state = 0;
		while (true) {
			switch (state) {
			case 0:
				if (tokens.get(i).getTokenType().equals(TokenType.Minus)) {
					state = 1;
					i++;
					this.i = i;
				} else if (tokens.get(i).getTokenType().equals(TokenType.Not)) {
					state = 1;
					i++;
					this.i = i;
				} else if (exprPostfix(i)) {
					this.i++;
					i = this.i;
					return true;
				} else {
					throw new AnalyzerException("Expected Minus | Not at: ", tokens.get(i).getBegin());
				}
				break;
			case 1:
				if (exprUnary(i)) {
					this.i++;
					i = this.i;
					return true;
				} else {
					throw new AnalyzerException("Expected exprUnary at: ", tokens.get(i).getBegin());
				}
			}
		}
	}

	public boolean exprPostfix(int i) throws AnalyzerException {
		/*
		 * exprPostfix: exprPostfix LBRACKET expr RBRACKET | exprPostfix DOT ID |
		 * exprPrimary ;
		 * 
		 */
		int state = 0;
		while (true) {
			switch (state) {
			case 0:
				if (exprPostfix(i)) {
					state = 1;
					this.i++;
					i = this.i;
				} else if (exprPrimary(i)) {
					this.i++;
					i = this.i;
					return true;
				} else {
					throw new AnalyzerException("Expected exprPostfix | exprPrimary at: ", tokens.get(i).getBegin());
				}
				break;
			case 1:
				if (tokens.get(i).getTokenType().equals(TokenType.OpenBrace)) {
					state = 2;
					i++;
					this.i = i;
				} else if (tokens.get(i).getTokenType().equals(TokenType.Dot)) {
					state = 4;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException("Expected OpenBrace | Dot at: ", tokens.get(i).getBegin());
				}
				break;
			case 2:
				if (expr(i)) {
					state = 3;
					this.i++;
					i = this.i;
				} else {
					throw new AnalyzerException("Expected expr at: ", tokens.get(i).getBegin());
				}
				break;
			case 3:
				if (tokens.get(i).getTokenType().equals(TokenType.CloseBrace)) {
					i++;
					this.i = i;
					return true;
				} else {
					throw new AnalyzerException("Expected CloseBrace at: ", tokens.get(i).getBegin());
				}
			case 4:
				if (tokens.get(i).getTokenType().equals(TokenType.Identifier)) {
					i++;
					this.i = i;
					return true;
				} else {
					throw new AnalyzerException("Expected Identifier at: ", tokens.get(i).getBegin());
				}
			}
		}
	}

	public boolean exprPrimary(int i) throws AnalyzerException {
		/*
		 * exprPrimary: ID ( LPAR ( expr ( COMMA expr )* )? RPAR )? | CT_INT | CT_REAL |
		 * CT_CHAR | CT_STRING | LPAR expr RPAR ;
		 */
		int state = 0;
		while (true) {
			switch (state) {
			case 0:
				if (tokens.get(i).getTokenType().equals(TokenType.Identifier)) {
					state = 1;
					i++;
					this.i = i;
				} else if (tokens.get(i).getTokenType().equals(TokenType.IntConstant)) {
					i++;
					this.i = i;
					return true;
				} else if (tokens.get(i).getTokenType().equals(TokenType.DoubleConstant)) {
					i++;
					this.i = i;
					return true;
				} else if (tokens.get(i).getTokenType().equals(TokenType.CharConstant)) {
					i++;
					this.i = i;
					return true;
				}
				// else if(tokens.get(i).getTokenType().equals(TokenType.StringConstant))
				// {
				// i++;
				// this.i = i; return true;
				// }
				else if (tokens.get(i).getTokenType().equals(TokenType.OpenBrace)) {
					i++;
					this.i = i;
					state = 6;
				} else {
					throw new AnalyzerException(
							"Expected Identifier | IntConstant | DoubleConstant | CharConstant | StringConstant | OpenBrace at: ",
							tokens.get(i).getBegin());
				}
				break;
			case 1:
				if (tokens.get(i).getTokenType().equals(TokenType.OpenBrace)) {
					i++;
					this.i = i;
					state = 2;
				} else {
					i++;
					this.i = i;
					return true;
				}
				break;
			case 2:
				if (expr(i)) {
					state = 3;
					this.i++;
					i = this.i;
				} else {
					state = 4;
				}
				break;
			case 3:
				if (tokens.get(i).getTokenType().equals(TokenType.Comma)) {
					state = 4;
					i++;
					this.i = i;
				} else if (tokens.get(i).getTokenType().equals(TokenType.CloseBrace)) {
					i++;
					this.i = i;
					return true;
				} else {
					throw new AnalyzerException("Expected Comma | CloseBrace at: ", tokens.get(i).getBegin());
				}
				break;
			case 4:
				if (expr(i)) {
					state = 3;
					this.i++;
					i = this.i;
				} else {
					throw new AnalyzerException("Expected expr at: ", tokens.get(i).getBegin());
				}
				break;
			}
		}
	}
}
