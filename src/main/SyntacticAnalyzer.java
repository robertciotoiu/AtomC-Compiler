package main;

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

	int consume() {
		this.i++;
		return this.i;
	}

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
					i = consume();
				} else {
					state = 2;
				}
				break;
			case 2:
				if (declFunc(i)) {
					state = 1;
					i = consume();
				} else {
					state = 3;
				}
				break;
			case 3:
				if (declVar(i)) {
					state = 1;
					i = consume();
				} else {
					state = 4;
				}
				break;
			case 4:
				if (tokens.get(i).getTokenType().equals(TokenType.END)) {
					System.out.println("End of program\nThe program has been successfuly compilated");
					break;
				} else {
					throw new AnalyzerException("Expected END" + i, i);
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
					i = consume();
				} else {
					return false;
					// throw new AnalyzerException("Expected Struct"+ tokens.get(i).getBegin()+" and
					// at token: " +i);
				}
				break;
			case 1:
				if (tokens.get(i).getTokenType().equals(TokenType.Identifier)) {
					state = 2;
					i++;
					this.i = i;
				} else {
					throw new AnalyzerException(
							"Expected Identifier" + tokens.get(i).getBegin() + " and at token: " + i);
				}
				break;
			case 2:
				if (tokens.get(i).getTokenType().equals(TokenType.OpeningCurlyBrace)) {
					state = 3;
					i++;
					this.i = i;
				} else if (tokens.get(i).getTokenType().equals(TokenType.Identifier)) {
					return false;
				} else {
					throw new AnalyzerException(
							"Expected OpeningCurlyBrace" + tokens.get(i).getBegin() + " and at token: " + i);
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
					throw new AnalyzerException(
							"Expected ClosingCurlyBrace" + tokens.get(i).getBegin() + " and at token: " + i);
				}
				break;
			case 4:
				if (tokens.get(i).getTokenType().equals(TokenType.Semicolon)) {
					// i++;
					// this.i = i;
					return true;
				} else {
					throw new AnalyzerException(
							"Expected Semicolon" + tokens.get(i).getBegin() + " and at token: " + i);
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
					i = consume();
				} else {
					return false;
					// throw new AnalyzerException("Expected typeBase at:
					// "+ tokens.get(i).getBegin()+" and at token: " +i);
				}
				break;
			case 1:
				if (tokens.get(i).getTokenType().equals(TokenType.Identifier)) {
					state = 2;
					i = consume();
				} else {
					throw new AnalyzerException(
							"Expected Identifier at: " + tokens.get(i).getBegin() + " and at token: " + i);
				}
				break;
			case 2:
				if (arrayDecl(i)) {
					state = 2;
					i = consume();
				} else {
					state = 3;// no array, just skip
				}
				break;
			// from here somehow loop, we check for another declVar
			case 3:
				if (tokens.get(i).getTokenType().equals(TokenType.Comma)) {
					state = 4;
					i = consume();
				} else if (tokens.get(i).getTokenType().equals(TokenType.Semicolon)) {
					// i++;
					// this.i = i;
					return true;
				} else {
					throw new AnalyzerException(
							"Expected , or ; at: " + tokens.get(i).getBegin() + " and at token: " + i);
				}
				break;
			case 4:
				if (tokens.get(i).getTokenType().equals(TokenType.Identifier)) {
					state = 5;
					i = consume();
				} else {
					throw new AnalyzerException(
							"Expected Identifier at: " + tokens.get(i).getBegin() + " and at token: " + i);
				}
				break;
			case 5:
				if (arrayDecl(i)) {
					state = 5;
					i = consume();
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
					// i++;
					// this.i = i;
					return true;
				} else if (tokens.get(i).getTokenType().equals(TokenType.Double)) {
					state = 1;
					// i++;
					// this.i = i;
					return true;
				} else if (tokens.get(i).getTokenType().equals(TokenType.Char)) {
					state = 1;
					// i++;
					// this.i = i;
					return true;
				} else if (tokens.get(i).getTokenType().equals(TokenType.Struct)) {
					state = 1;
					i++;
				} else {
					return false;
					// throw new AnalyzerException("Expected INT | DOUBLE | CHAR | STRUCT at: ",
					// tokens.get(i).getBegin());
				}
			case 1:
				if (tokens.get(i).getTokenType().equals(TokenType.Identifier)) {
					this.i = i;
					return true;
				} else {
					throw new AnalyzerException(
							"Expected Identifier at: " + tokens.get(i).getBegin() + " and at token: " + i);
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
					i = consume();
				} else {
					return false;
					// throw new AnalyzerException("Expected OpenStraightBrace at:
					// "+ tokens.get(i).getBegin()+" and at token: " +i);
				}
				break;
			case 1:
				if (expr(i)) {
					state = 2;
					i = consume();
				} else {
					state = 2;

					// i = consume();
					// throw new AnalyzerException("Expected expr at: "+ tokens.get(i).getBegin()+"
					// and at token: " +i);
				}
				break;
			case 2:
				if (tokens.get(i).getTokenType().equals(TokenType.CloseStraightBrace)) {
					return true;
				} else {
					throw new AnalyzerException(
							"Expected CloseStraightBrace at: " + tokens.get(i).getBegin() + " and at token: " + i);
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
					i = consume();
				} else {
					throw new AnalyzerException(
							"Expected typeBase at: " + tokens.get(i).getBegin() + " and at token: " + i);
				}
				break;
			case 1:
				if (arrayDecl(i)) {
					return true;
				} else {
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
					i = consume();
				} else {
					state = 2;// verify if it's void
				}
				break;
			case 1:
				if (tokens.get(i).getTokenType().equals(TokenType.Multiply)) {
					state = 3;// vector
					i = consume();
				} else {
					state = 3;// not vector
				}
				break;
			case 2:
				if (tokens.get(i).getTokenType().equals(TokenType.Void)) {
					state = 3;
					i = consume();
				} else {
					// throw new AnalyzerException("Expected typeBase or void at: ",
					// tokens.get(i).getBegin());
				}
				break;
			case 3:
				if (tokens.get(i).getTokenType().equals(TokenType.Identifier)) {
					state = 4;
					i = consume();
				} else {
					throw new AnalyzerException(
							"Expected Identifier at: " + tokens.get(i).getBegin() + " and at token: " + i);
				}
				break;
			case 4:
				if (tokens.get(i).getTokenType().equals(TokenType.OpenBrace)) {
					state = 5;
					i = consume();
				} else {
					return false;// because this can be a declaration variable
					// throw new AnalyzerException("Expected OpenBrace at: ",
					// tokens.get(i).getBegin());
				}
				break;
			case 5:
				if (funcArg(i)) {
					state = 6;// go to verify if comma and other arguments
					i = consume();
				} else {
					state = 6;// no arguments, then verify if CloseBrace
				}
				break;
			case 6:
				if (tokens.get(i).getTokenType().equals(TokenType.Comma)) {
					state = 7;
					i = consume();
				} else if (tokens.get(i).getTokenType().equals(TokenType.CloseBrace)) {
					state = 8;
					i = consume();
				} else {
					throw new AnalyzerException(
							"Expected Comma OR CloseBrace at: " + tokens.get(i).getBegin() + " and at token: " + i);
				}
				break;
			case 7:
				if (funcArg(i)) {
					state = 6;// if another funcArg, go to case 6 to check next is comma or closebrace
					i = consume();
				} else {
					throw new AnalyzerException(
							"Expected another funcArg at: " + tokens.get(i).getBegin() + " and at token: " + i);
				}
				break;
			case 8:
				if (stmCompound(i)) {
					return true;
				} else {
					throw new AnalyzerException(
							"Expected stmCompound at: " + tokens.get(i).getBegin() + " and at token: " + i);
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
					i = consume();
				} else {
					return false;
					// throw new AnalyzerException("Expected typeBase at: "+
					// tokens.get(i).getBegin()+" and at token: " +i);
				}
				break;
			case 1:
				if (tokens.get(i).getTokenType().equals(TokenType.Identifier)) {
					state = 2;
					i = consume();
				} else {
					throw new AnalyzerException(
							"Expected Identifier at: " + tokens.get(i).getBegin() + " and at token: " + i);
				}
				break;
			case 2:
				if (arrayDecl(i)) {
					return true;// funcArg is simple;
				} else {
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
					return true;
				} else {
					state = 1;
				}
				break;
			case 1:
				if (ifStm(i)) {
					return true;
				} else {
					state = 2;
				}
				break;
			case 2:
				if (whileStm(i)) {
					return true;
				} else {
					state = 3;
				}
				break;
			case 3:
				if (forStm(i)) {
					return true;
				} else {
					state = 4;
				}
				break;
			case 4:
				if (breakStm(i)) {
					return true;
				} else {
					state = 5;
				}
				break;
			case 5:
				if (returnStm(i)) {
					return true;
				} else {
					state = 6;
				}
				break;
			case 6:
				if (exprStm(i)) {
					return true;
				} else {
					return false;
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
					i = consume();
				} else {
					return false;
				}
				break;
			case 1:
				if (tokens.get(i).getTokenType().equals(TokenType.OpenBrace)) {
					state = 2;
					i = consume();
				} else {
					throw new AnalyzerException(
							"Expected OpenBrace at: " + tokens.get(i).getBegin() + " and at token: " + i);
				}
				break;
			case 2:
				if (expr(i)) {
					state = 3;
					i = consume();
				} else {
					throw new AnalyzerException(
							"Expected expr at: " + tokens.get(i).getBegin() + " and at token: " + i);
				}
				break;
			case 3:
				if (tokens.get(i).getTokenType().equals(TokenType.CloseBrace)) {
					state = 4;
					i = consume();
				} else {
					throw new AnalyzerException(
							"Expected CloseBrace at: " + tokens.get(i).getBegin() + " and at token: " + i);
				}
				break;
			case 4:
				if (stm(i)) {
					state = 5;
					i = consume();
				} else {
					throw new AnalyzerException("Expected stm at: " + tokens.get(i).getBegin() + " and at token: " + i);
				}
				break;
			case 5:
				if (tokens.get(i).getTokenType().equals(TokenType.Else)) {
					state = 6;
					i = consume();
				} else {
					return true;
				}
				break;
			case 6:
				if (stm(i)) {
					return true;
				} else {
					throw new AnalyzerException(
							"Expected else stm at: " + tokens.get(i).getBegin() + " and at token: " + i);
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
					i = consume();
				} else {
					return false;
					// throw new AnalyzerException("Expected While at: "+ tokens.get(i).getBegin()+"
					// and at token: " +i);
				}
				break;
			case 1:
				if (tokens.get(i).getTokenType().equals(TokenType.OpenBrace)) {
					state = 2;
					i = consume();
				} else {
					throw new AnalyzerException(
							"Expected OpenBrace at: " + tokens.get(i).getBegin() + " and at token: " + i);
				}
				break;
			case 2:
				if (expr(i)) {
					state = 3;
					i = consume();
				} else {
					throw new AnalyzerException(
							"Expected expr at: " + tokens.get(i).getBegin() + " and at token: " + i);
				}
				break;
			case 3:
				if (tokens.get(i).getTokenType().equals(TokenType.CloseBrace)) {
					state = 4;
					i = consume();
				} else {
					throw new AnalyzerException(
							"Expected CloseBrace at: " + tokens.get(i).getBegin() + " and at token: " + i);
				}
				break;
			case 4:
				if (stm(i)) {
					return true;
				} else {
					throw new AnalyzerException("Expected stm at: " + tokens.get(i).getBegin() + " and at token: " + i);
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
					i = consume();
				} else {
					return false;
					// throw new AnalyzerException("Expected For at: "+ tokens.get(i).getBegin()+"
					// and at token: " +i);
				}
				break;
			case 1:
				if (tokens.get(i).getTokenType().equals(TokenType.OpenBrace)) {
					state = 2;
					i = consume();
				} else {
					throw new AnalyzerException(
							"Expected OpenBrace at: " + tokens.get(i).getBegin() + " and at token: " + i);
				}
				break;
			case 2:
				if (expr(i)) {
					state = 3;
					i = consume();
				} else {
					state = 3;
				}
				break;
			case 3:
				if (tokens.get(i).getTokenType().equals(TokenType.Semicolon)) {
					state = 4;
					i = consume();
				} else {
					throw new AnalyzerException(
							"Expected Semicolon at: " + tokens.get(i).getBegin() + " and at token: " + i);
				}
				break;
			case 4:
				if (expr(i)) {
					state = 5;
					i = consume();
				} else {
					state = 5;
				}
				break;
			case 5:
				if (tokens.get(i).getTokenType().equals(TokenType.Semicolon)) {
					state = 6;
					i = consume();
				} else {
					throw new AnalyzerException(
							"Expected Semicolon at: " + tokens.get(i).getBegin() + " and at token: " + i);
				}
				break;
			case 6:
				if (expr(i)) {
					state = 7;
					i = consume();
				} else {
					state = 7;
				}
				break;
			case 7:
				if (tokens.get(i).getTokenType().equals(TokenType.CloseBrace)) {
					state = 8;
					i = consume();
				} else {
					throw new AnalyzerException(
							"Expected CloseBrace at: " + tokens.get(i).getBegin() + " and at token: " + i);
				}
				break;
			case 8:
				if (stm(i)) {
					return true;
				} else {
					throw new AnalyzerException("Expected stm at: " + tokens.get(i).getBegin() + " and at token: " + i);
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
					i = consume();
				} else {
					return false;
					// throw new AnalyzerException("Expected Break at: "+ tokens.get(i).getBegin()+"
					// and at token: " +i);
				}
				break;
			case 1:
				if (tokens.get(i).getTokenType().equals(TokenType.Semicolon)) {
					return true;
				} else {
					throw new AnalyzerException(
							"Expected Semicolon at: " + tokens.get(i).getBegin() + " and at token: " + i);
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
					i = consume();
				} else {
					return false;
					// throw new AnalyzerException("Expected Return at: "+
					// tokens.get(i).getBegin()+" and at token: " +i);
				}
				break;
			case 1:
				if (expr(i)) {
					state = 2;
					i = consume();
				} else {
					state = 2;
				}
				break;
			case 2:
				if (tokens.get(i).getTokenType().equals(TokenType.Semicolon)) {
					return true;
				} else {
					throw new AnalyzerException(
							"Expected Semicolon at: " + tokens.get(i).getBegin() + " and at token: " + i);
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
					i = consume();
				} else {
					state = 1;
				}
				break;
			case 1:
				if (tokens.get(i).getTokenType().equals(TokenType.Semicolon)) {
					return true;
				} else {
					//return false;
					 throw new AnalyzerException("Expected Semicolon at: "+ tokens.get(i).getBegin()+" and at token: " +i);
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
					i = consume();
				} else {
					return false;
					// throw new AnalyzerException("Expected OpeningCurlyBrace at: "+
					// tokens.get(i).getBegin()+" and at token: " +i);
				}
				break;
			case 1:
				if (declVar(i)) {
					state = 1;
					i = consume();
				} else if (stm(i)) {
					state = 1;
					i = consume();
				} else {
					state = 2;
				}
				break;
			case 2:
				if (tokens.get(i).getTokenType().equals(TokenType.ClosingCurlyBrace)) {
					return true;
				} else {
					return false;
					// throw new AnalyzerException("Expected ClosingCurlyBrace at: "+
					// tokens.get(i).getBegin()+" and at token: " +i);
				}
			}
		}
	}

	public boolean expr(int i) throws AnalyzerException {
		if (exprAssign(i)) {
			return true;
		} else {
			return false;
		}
	}


	public boolean exprAssign(int i) throws AnalyzerException {
		// exprAssign: exprUnary ASSIGN exprAssign | exprOr ;
		int state = 0;
		int i_branch2 = 0;
		while (true) {
			switch (state) {
			case 0: {
				// i_branch2 = this.i;
				if (exprUnary(i)) {
					i_branch2 = i;
					
					state = 1;
					i = consume();
				} else {
					state = 3;
				}
				break;
			}
			case 1:
				if (tokens.get(i).getTokenType().equals(TokenType.Equal)) {
					state = 2;
					i = consume();
				} else {
					state = 3;// is exprOr
					i = i_branch2;
					this.i = i;
				}
				break;
			case 2:
				if (exprAssign(i)) {
					return true;
				} else {
					throw new AnalyzerException(
							"Expected exprAssign at: " + tokens.get(i).getBegin() + " and at token: " + i);
				}
			case 3:
				if (exprOr(i)) {
					return true;
				} else {
					return false;
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
					i = consume();
				} else if (exprUnary(i)) {
					return true;
				} else {
					return false;
					// throw new AnalyzerException("Expected OpenBrace | exprUnary at: "+
					// tokens.get(i).getBegin()+" and at token: " +i);
				}
				break;
			case 1:
				if (typeName(i)) {
					state = 2;
					i = consume();
				} else {
					throw new AnalyzerException(
							"Expected typeName at: " + tokens.get(i).getBegin() + " and at token: " + i);
				}
				break;
			case 2:
				if (tokens.get(i).getTokenType().equals(TokenType.CloseBrace)) {
					state = 3;
					i = consume();
				} else {
					throw new AnalyzerException(
							"Expected CloseBrace at: " + tokens.get(i).getBegin() + " and at token: " + i);
				}
				break;
			case 3:
				if (exprCast(i)) {
					return true;
				} else {
					throw new AnalyzerException(
							"Expected exprCast at: " + tokens.get(i).getBegin() + " and at token: " + i);
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
					i = consume();
				} else if (tokens.get(i).getTokenType().equals(TokenType.Not)) {
					state = 1;
					i = consume();
				} else if (exprPostfix(i)) {
					return true;
				} else {
					return false;// if we throw exception, exprAssign will false fail
					// throw new AnalyzerException("Expected Minus | Not at: "+
					// tokens.get(i).getBegin()+" and at token: " +i);
				}
				break;
			case 1:
				if (exprUnary(i)) {
					return true;
				} else {
					throw new AnalyzerException(
							"Expected exprUnary at: " + tokens.get(i).getBegin() + " and at token: " + i);
				}
			}
		}
	}

	public boolean exprPrimary(int i) throws AnalyzerException {
		/*
		 * exprPrimary: ID ( LPAR ( expr ( COMMA expr )* )? RPAR )? | CT_INT | CT_REAL |
		 * CT_CHAR | CT_STRING | LPAR expr RPAR ;
		 */
		int i_backup = 0;
		int state = 0;
		while (true) {
			switch (state) {
			case 0:
				if (tokens.get(i).getTokenType().equals(TokenType.Identifier)) {
					state = 1;
					i_backup = i;
					i = consume();
				} else if (tokens.get(i).getTokenType().equals(TokenType.IntConstant)) {
					return true;
				} else if (tokens.get(i).getTokenType().equals(TokenType.DoubleConstant)) {
					return true;
				} else if (tokens.get(i).getTokenType().equals(TokenType.CharConstant)) {
					return true;
				}
				// else if(tokens.get(i).getTokenType().equals(TokenType.StringConstant))
				// {
				// i++;
				// this.i = i; return true;
				// }
				else if (tokens.get(i).getTokenType().equals(TokenType.OpenBrace)) {
					state = 6;
					i = consume();
				} else {					
					return false;// not sure if not to throw an exception
					// throw new AnalyzerException(
					// "Expected Identifier | IntConstant | DoubleConstant | CharConstant |
					// StringConstant | OpenBrace at: ",
					// tokens.get(i).getBegin());
				}
				break;
			case 1:
				if (tokens.get(i).getTokenType().equals(TokenType.OpenBrace)) {
					i_backup = i;
					i = consume();
					state = 2;
				} else {
					//if(i_backup!=0 && (tokens.get(i+1).getTokenType().equals(TokenType.Semicolon) ||tokens.get(i+1).getTokenType().equals(TokenType.Equal)))
					if(i_backup!=0)
					{
					 i = i_backup;
					 this.i = i_backup;//this may not work #prettysure ,maybe I should add i = i_backup
					return true;
					}
					return false;
				}
				break;
			case 2:
				if (expr(i)) {
					state = 3;
					i = consume();
				} else {
					state = 3;
					if(i_backup!=0)
					{
						this.i = i_backup;
					}
				}
				break;
			case 3:
				if (tokens.get(i).getTokenType().equals(TokenType.Comma)) {
					state = 4;
					i = consume();
				} else if (tokens.get(i).getTokenType().equals(TokenType.CloseBrace)) {
					return true;
				} else {
					throw new AnalyzerException(
							"Expected Comma | CloseBrace at: " + tokens.get(i).getBegin() + " and at token: " + i);
				}
				break;
			case 4:
				if (expr(i)) {
					state = 3;
					i = consume();
				} else {
					throw new AnalyzerException(
							"Expected expr at: " + tokens.get(i).getBegin() + " and at token: " + i);
				}
				break;
			case 6:
				if(expr(i))
				{
					state = 7;
					i = consume();
				}
				else
				{
					throw new AnalyzerException(
							"Expected expr at: " + tokens.get(i).getBegin() + " and at token: " + i);
				}
				break;
			case 7:
				if(tokens.get(i).getTokenType().equals(TokenType.CloseBrace))
				{
					return true;
				}
				else
				{
					throw new AnalyzerException(
							"Expected expr at: " + tokens.get(i).getBegin() + " and at token: " + i);
				}
			}
		}
	}

	public boolean exprOr(int i) throws AnalyzerException {
		if (!exprAnd(i))
			return false;
		exprOr1(i);
		return true;
	}

	public void exprOr1(int i) throws AnalyzerException {
		if (tokens.get(i).getTokenType().equals(TokenType.Or)) {
			i = consume();
			if (!exprAnd(i))
				throw new AnalyzerException("Expected exprAnd at: " + tokens.get(i).getBegin() + " and at token: " + i);
			exprOr1(i);
		}
	}

	public boolean exprAnd(int i) throws AnalyzerException {
		if (!exprEq(i))
			return false;
		exprAnd1(i);
		return true;
	}

	public void exprAnd1(int i) throws AnalyzerException {
		if (tokens.get(i).getTokenType().equals(TokenType.And)) {
			i = consume();
			if (!exprEq(i))
				throw new AnalyzerException("Expected exprEq at: " + tokens.get(i).getBegin() + " and at token: " + i);
			exprOr1(i);
		}
	}

	public boolean exprEq(int i) throws AnalyzerException {
		if (!exprRel(i))
			return false;
		exprEq1(i);
		return true;
	}

	public void exprEq1(int i) throws AnalyzerException {
		if (tokens.get(i).getTokenType().equals(TokenType.Equal)) {
			i = consume();
		} else if (tokens.get(i).getTokenType().equals(TokenType.ExclameEqual)) {
			i = consume();
		} else
			return;
		if (!exprRel(i))
			throw new AnalyzerException("Expected exprRel at: " + tokens.get(i).getBegin() + " and at token: " + i);
		exprEq1(i);
	}

	public boolean exprRel(int i) throws AnalyzerException {
		if (!exprAdd(i))
			return false;
		exprRel1(i);
		return true;
	}

	public void exprRel1(int i) throws AnalyzerException {
		if (tokens.get(i).getTokenType().equals(TokenType.Less)) {
			i = consume();
		} else if (tokens.get(i).getTokenType().equals(TokenType.LessEqual)) {
			i = consume();
		} else if (tokens.get(i).getTokenType().equals(TokenType.Greater)) {
			i = consume();
		} else if (tokens.get(i).getTokenType().equals(TokenType.GreaterEqual)) {
			i = consume();
		} else
			return;
		if (!exprAdd(i))
			throw new AnalyzerException("Expected exprAdd at: " + tokens.get(i).getBegin() + " and at token: " + i);
		exprRel1(i);
	}

	public boolean exprAdd(int i) throws AnalyzerException {
		if (!exprMul(i))
			return false;
		exprAdd1(i);
		return true;
	}

	public void exprAdd1(int i) throws AnalyzerException {
		if (tokens.get(i).getTokenType().equals(TokenType.Plus)) {
			i = consume();
		} else if (tokens.get(i).getTokenType().equals(TokenType.Minus)) {
			i = consume();
		} else
			return;
		if (!exprMul(i))
			throw new AnalyzerException("Expected exprMul at: " + tokens.get(i).getBegin() + " and at token: " + i);
		exprAdd1(i);
	}

	public boolean exprMul(int i) throws AnalyzerException {
		if (!exprCast(i))
			return false;
		exprMul1(i);
		return true;
	}

	public void exprMul1(int i) throws AnalyzerException {
		if (tokens.get(i).getTokenType().equals(TokenType.Multiply)) {
			i = consume();
		} else if (tokens.get(i).getTokenType().equals(TokenType.Divide)) {
			i = consume();
		} else
			return;
		if (!exprCast(i))
			throw new AnalyzerException("Expected exprCast at: " + tokens.get(i).getBegin() + " and at token: " + i);
		exprMul1(i);
	}

	public boolean exprPostfix(int i) throws AnalyzerException {
		if (!exprPrimary(i))
			return false;
		exprPostfix1();
		return true;
	}

	public void exprPostfix1() throws AnalyzerException {
		if (tokens.get(i).getTokenType().equals(TokenType.OpenStraightBrace)) {
			if (!expr(i))
				throw new AnalyzerException(
						"Expected exprCast at: " + tokens.get(i).getBegin() + " and at token: " + i);
			if (!tokens.get(i).getTokenType().equals(TokenType.CloseStraightBrace))
				throw new AnalyzerException(
						"Expected exprCast at: " + tokens.get(i).getBegin() + " and at token: " + i);
			else
				i = consume();
		} else if (tokens.get(i).getTokenType().equals(TokenType.Dot)) {
			if (!tokens.get(i).getTokenType().equals(TokenType.Identifier))
				throw new AnalyzerException(
						"Expected exprCast at: " + tokens.get(i).getBegin() + " and at token: " + i);
			else
				i = consume();
		} else
			return;
		exprPostfix1();
	}
}