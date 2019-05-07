package main;

	public enum TokenType {
		BlockComment,

		LineComment,

		WhiteSpace,

		Tab,

		NewLine,

		CloseBrace,

		OpenBrace,

		OpeningCurlyBrace,

		ClosingCurlyBrace,

		DoubleConstant,

		IntConstant,

		Plus,

		Minus,

		Multiply,

		Divide,

//		Point,
		LessEqual,
		
		GreaterEqual,

		EqualEqual,

		Equal,

		ExclameEqual,

		Greater,

		Less,

		//Static,

		//Public,

		//Private,

		Int,

		Double,

		Void,

		False,

		True,

		Null,

		Return,

		//New,

		Class,

		If,

		While,
		
		For,

		Else,

		Semicolon,

		Comma,
		
		//my modify
		Struct,
		
		Break,
		
		Char,
		
		CharConstant,
		
		//String,
		
		//StringConstant,
		
		OpenStraightBrace,
		
		CloseStraightBrace,
		
		Dot,
		
		And,
		
		Or,
		
		Not,
		
		END,

		Identifier;

//46 - 
		
		public boolean isAuxiliary() {
			return this == BlockComment || this == LineComment || this == NewLine || this == Tab
					|| this == WhiteSpace;
		}
}
