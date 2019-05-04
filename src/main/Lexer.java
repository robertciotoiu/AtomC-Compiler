package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

	private Map<TokenType, String> regEx;

	private List<Token> result;

	public Lexer() {
		regEx = new TreeMap<TokenType, String>();
		launchRegEx();
		result = new ArrayList<Token>();
	}

	public void tokenize(String source) throws AnalyzerException {
		int position = 0;
		Token token = null;
		do {
			token = separateToken(source, position);
			if (token != null) {
				position = token.getEnd();
				result.add(token);
			}
		} while (token != null && position != source.length());
		if (position != source.length()) {
			throw new AnalyzerException("Lexical error at position # " + position, position);

		}
	}

	public List<Token> getTokens() {
		return result;
	}

	public List<Token> getFilteredTokens() {
		List<Token> filteredResult = new ArrayList<Token>();
		for (Token t : this.result) {
			if (!t.getTokenType().isAuxiliary()) {
				filteredResult.add(t);
			}
		}
		return filteredResult;
	}

	private Token separateToken(String source, int fromIndex) {
		if (fromIndex < 0 || fromIndex >= source.length()) {
			throw new IllegalArgumentException("Illegal index in the input stream!");
		}
		for (TokenType tokenType : TokenType.values()) {
			Pattern p = Pattern.compile(".{" + fromIndex + "}" + regEx.get(tokenType), Pattern.DOTALL);
			Matcher m = p.matcher(source);
			if (m.matches()) {
				String lexema = m.group(1);
				return new Token(fromIndex, fromIndex + lexema.length(), lexema, tokenType);
			}
		}

		return null;
	}

	private void launchRegEx() {
		regEx.put(TokenType.BlockComment, "(/\\*.*?\\*/).*");
		regEx.put(TokenType.LineComment, "(//(.*?)[\r$]?\n).*");
		regEx.put(TokenType.WhiteSpace, "( ).*");
		regEx.put(TokenType.OpenBrace, "(\\().*");
		regEx.put(TokenType.CloseBrace, "(\\)).*");
		regEx.put(TokenType.Semicolon, "(;).*");
		regEx.put(TokenType.Comma, "(,).*");
		regEx.put(TokenType.OpeningCurlyBrace, "(\\{).*");
		regEx.put(TokenType.ClosingCurlyBrace, "(\\}).*");
		regEx.put(TokenType.DoubleConstant, "\\b(\\d{1,9}\\.\\d{1,32})\\b.*");
		regEx.put(TokenType.IntConstant, "\\b(\\d{1,9})\\b.*");
		regEx.put(TokenType.Void, "\\b(void)\\b.*");
		regEx.put(TokenType.Int, "\\b(int)\\b.*");
		regEx.put(TokenType.Double, "\\b(int|double)\\b.*");
		regEx.put(TokenType.Tab, "(\\t).*");
		regEx.put(TokenType.NewLine, "(\\n).*");
		// regEx.put(TokenType.Public, "\\b(public)\\b.*");
		// regEx.put(TokenType.Private, "\\b(private)\\b.*");
		regEx.put(TokenType.False, "\\b(false)\\b.*");
		regEx.put(TokenType.True, "\\b(true)\\b.*");
		regEx.put(TokenType.Null, "\\b(null)\\b.*");
		regEx.put(TokenType.Return, "\\b(return)\\b.*");
		// regEx.put(TokenType.New, "\\b(new)\\b.*");
		// regEx.put(TokenType.Class, "\\b(class)\\b.*");
		regEx.put(TokenType.If, "\\b(if)\\b.*");
		regEx.put(TokenType.Else, "\\b(else)\\b.*");
		regEx.put(TokenType.While, "\\b(while)\\b.*");
		// regEx.put(TokenType.Static, "\\b(static)\\b.*");
		// regEx.put(TokenType.Point, "(\\.).*");
		regEx.put(TokenType.Plus, "(\\+{1}).*");
		regEx.put(TokenType.Minus, "(\\-{1}).*");
		regEx.put(TokenType.Multiply, "(\\*).*");
		regEx.put(TokenType.Divide, "(/).*");
		regEx.put(TokenType.EqualEqual, "(==).*");
		regEx.put(TokenType.LessEqual, "(<=).*");// done
		regEx.put(TokenType.GreaterEqual, "(>=).*");// done
		regEx.put(TokenType.Equal, "(=).*");
		regEx.put(TokenType.ExclameEqual, "(\\!=).*");
		regEx.put(TokenType.Greater, "(>).*");
		regEx.put(TokenType.Less, "(<).*");
		// my new regexes
		regEx.put(TokenType.Struct, "\\b(struct)\\b.*");// done
		regEx.put(TokenType.Break, "\\b(break)\\b.*");// done
		regEx.put(TokenType.Char, "\\b(char)\\b.*");// done
		regEx.put(TokenType.CharConstant, "['][']");// NOT done
		regEx.put(TokenType.For, "\\b(for)\\b.*");// done

		// regEx.put(TokenType.StringConstant, "(<).*");//NOT done
		regEx.put(TokenType.OpenStraightBrace, "(\\[).*");// done
		regEx.put(TokenType.CloseStraightBrace, "(\\]).*");// done
		regEx.put(TokenType.Dot, "(\\.).*");// done
		regEx.put(TokenType.And, "(&&).*");// done
		regEx.put(TokenType.Or, "(\\|\\|).*");// done
		regEx.put(TokenType.Not, "(\\!).*");// done
		regEx.put(TokenType.END, "\\b(END)\\b.*");// done

		regEx.put(TokenType.Identifier, "\\b([a-zA-Z]{1}[0-9a-zA-Z_]{0,31})\\b.*");

	}
}