/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.lang.gaml.ui.contentassist.antlr.internal;

// Hack: Use our own Lexer superclass by means of import.
// Currently there is no other way to specify the superclass for the lexer.
import org.antlr.runtime.*;
import org.eclipse.xtext.ui.editor.contentassist.antlr.internal.Lexer;

@SuppressWarnings("all")
public class InternalGamlLexer extends Lexer {

	public static final int RULE_BOOLEAN = 9;
	public static final int RULE_ID = 4;
	public static final int T__29 = 29;
	public static final int T__28 = 28;
	public static final int T__27 = 27;
	public static final int T__26 = 26;
	public static final int T__25 = 25;
	public static final int T__24 = 24;
	public static final int T__23 = 23;
	public static final int T__22 = 22;
	public static final int T__21 = 21;
	public static final int RULE_ANY_OTHER = 13;
	public static final int T__20 = 20;
	public static final int T__60 = 60;
	public static final int RULE_COLOR = 8;
	public static final int EOF = -1;
	public static final int T__55 = 55;
	public static final int T__56 = 56;
	public static final int T__19 = 19;
	public static final int T__57 = 57;
	public static final int T__58 = 58;
	public static final int T__16 = 16;
	public static final int T__51 = 51;
	public static final int T__15 = 15;
	public static final int T__52 = 52;
	public static final int T__18 = 18;
	public static final int T__53 = 53;
	public static final int T__54 = 54;
	public static final int T__17 = 17;
	public static final int T__14 = 14;
	public static final int T__59 = 59;
	public static final int RULE_INT = 7;
	public static final int T__50 = 50;
	public static final int T__42 = 42;
	public static final int T__43 = 43;
	public static final int T__40 = 40;
	public static final int T__41 = 41;
	public static final int T__46 = 46;
	public static final int T__47 = 47;
	public static final int T__44 = 44;
	public static final int T__45 = 45;
	public static final int T__48 = 48;
	public static final int T__49 = 49;
	public static final int RULE_SL_COMMENT = 11;
	public static final int RULE_DOUBLE = 6;
	public static final int RULE_ML_COMMENT = 10;
	public static final int T__30 = 30;
	public static final int T__31 = 31;
	public static final int T__32 = 32;
	public static final int RULE_STRING = 5;
	public static final int T__33 = 33;
	public static final int T__34 = 34;
	public static final int T__35 = 35;
	public static final int T__36 = 36;
	public static final int T__37 = 37;
	public static final int T__38 = 38;
	public static final int T__39 = 39;
	public static final int RULE_WS = 12;

	// delegates
	// delegators

	public InternalGamlLexer() {
		;
	}

	public InternalGamlLexer(final CharStream input) {
		this(input, new RecognizerSharedState());
	}

	public InternalGamlLexer(final CharStream input, final RecognizerSharedState state) {
		super(input, state);

	}

	public String getGrammarFileName() {
		return "../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g";
	}

	// $ANTLR start "T__14"
	public final void mT__14() throws RecognitionException {
		try {
			int _type = T__14;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:11:7:
			// ( ';' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:11:9:
			// ';'
			{
				match(';');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__14"

	// $ANTLR start "T__15"
	public final void mT__15() throws RecognitionException {
		try {
			int _type = T__15;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:12:7:
			// ( '-' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:12:9:
			// '-'
			{
				match('-');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__15"

	// $ANTLR start "T__16"
	public final void mT__16() throws RecognitionException {
		try {
			int _type = T__16;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:13:7:
			// ( '!' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:13:9:
			// '!'
			{
				match('!');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__16"

	// $ANTLR start "T__17"
	public final void mT__17() throws RecognitionException {
		try {
			int _type = T__17;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:14:7:
			// ( 'my' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:14:9:
			// 'my'
			{
				match("my");

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__17"

	// $ANTLR start "T__18"
	public final void mT__18() throws RecognitionException {
		try {
			int _type = T__18;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:15:7:
			// ( 'the' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:15:9:
			// 'the'
			{
				match("the");

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__18"

	// $ANTLR start "T__19"
	public final void mT__19() throws RecognitionException {
		try {
			int _type = T__19;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:16:7:
			// ( 'not' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:16:9:
			// 'not'
			{
				match("not");

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__19"

	// $ANTLR start "T__20"
	public final void mT__20() throws RecognitionException {
		try {
			int _type = T__20;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:17:7:
			// ( 'model' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:17:9:
			// 'model'
			{
				match("model");

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__20"

	// $ANTLR start "T__21"
	public final void mT__21() throws RecognitionException {
		try {
			int _type = T__21;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:18:7:
			// ( 'import' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:18:9:
			// 'import'
			{
				match("import");

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__21"

	// $ANTLR start "T__22"
	public final void mT__22() throws RecognitionException {
		try {
			int _type = T__22;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:19:7:
			// ( '_gaml' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:19:9:
			// '_gaml'
			{
				match("_gaml");

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__22"

	// $ANTLR start "T__23"
	public final void mT__23() throws RecognitionException {
		try {
			int _type = T__23;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:20:7:
			// ( '{' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:20:9:
			// '{'
			{
				match('{');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__23"

	// $ANTLR start "T__24"
	public final void mT__24() throws RecognitionException {
		try {
			int _type = T__24;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:21:7:
			// ( '}' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:21:9:
			// '}'
			{
				match('}');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__24"

	// $ANTLR start "T__25"
	public final void mT__25() throws RecognitionException {
		try {
			int _type = T__25;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:22:7:
			// ( '_keyword' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:22:9:
			// '_keyword'
			{
				match("_keyword");

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__25"

	// $ANTLR start "T__26"
	public final void mT__26() throws RecognitionException {
		try {
			int _type = T__26;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:23:7:
			// ( '_facets' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:23:9:
			// '_facets'
			{
				match("_facets");

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__26"

	// $ANTLR start "T__27"
	public final void mT__27() throws RecognitionException {
		try {
			int _type = T__27;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:24:7:
			// ( '[' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:24:9:
			// '['
			{
				match('[');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__27"

	// $ANTLR start "T__28"
	public final void mT__28() throws RecognitionException {
		try {
			int _type = T__28;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:25:7:
			// ( ']' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:25:9:
			// ']'
			{
				match(']');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__28"

	// $ANTLR start "T__29"
	public final void mT__29() throws RecognitionException {
		try {
			int _type = T__29;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:26:7:
			// ( ',' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:26:9:
			// ','
			{
				match(',');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__29"

	// $ANTLR start "T__30"
	public final void mT__30() throws RecognitionException {
		try {
			int _type = T__30;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:27:7:
			// ( '_children' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:27:9:
			// '_children'
			{
				match("_children");

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__30"

	// $ANTLR start "T__31"
	public final void mT__31() throws RecognitionException {
		try {
			int _type = T__31;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:28:7:
			// ( '_facet' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:28:9:
			// '_facet'
			{
				match("_facet");

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__31"

	// $ANTLR start "T__32"
	public final void mT__32() throws RecognitionException {
		try {
			int _type = T__32;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:29:7:
			// ( ':' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:29:9:
			// ':'
			{
				match(':');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__32"

	// $ANTLR start "T__33"
	public final void mT__33() throws RecognitionException {
		try {
			int _type = T__33;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:30:7:
			// ( '=' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:30:9:
			// '='
			{
				match('=');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__33"

	// $ANTLR start "T__34"
	public final void mT__34() throws RecognitionException {
		try {
			int _type = T__34;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:31:7:
			// ( '_binary' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:31:9:
			// '_binary'
			{
				match("_binary");

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__34"

	// $ANTLR start "T__35"
	public final void mT__35() throws RecognitionException {
		try {
			int _type = T__35;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:32:7:
			// ( '_reserved' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:32:9:
			// '_reserved'
			{
				match("_reserved");

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__35"

	// $ANTLR start "T__36"
	public final void mT__36() throws RecognitionException {
		try {
			int _type = T__36;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:33:7:
			// ( '_unit' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:33:9:
			// '_unit'
			{
				match("_unit");

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__36"

	// $ANTLR start "T__37"
	public final void mT__37() throws RecognitionException {
		try {
			int _type = T__37;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:34:7:
			// ( 'set' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:34:9:
			// 'set'
			{
				match("set");

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__37"

	// $ANTLR start "T__38"
	public final void mT__38() throws RecognitionException {
		try {
			int _type = T__38;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:35:7:
			// ( 'returns:' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:35:9:
			// 'returns:'
			{
				match("returns:");

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__38"

	// $ANTLR start "T__39"
	public final void mT__39() throws RecognitionException {
		try {
			int _type = T__39;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:36:7:
			// ( '+=' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:36:9:
			// '+='
			{
				match("+=");

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__39"

	// $ANTLR start "T__40"
	public final void mT__40() throws RecognitionException {
		try {
			int _type = T__40;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:37:7:
			// ( '-=' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:37:9:
			// '-='
			{
				match("-=");

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__40"

	// $ANTLR start "T__41"
	public final void mT__41() throws RecognitionException {
		try {
			int _type = T__41;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:38:7:
			// ( '*=' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:38:9:
			// '*='
			{
				match("*=");

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__41"

	// $ANTLR start "T__42"
	public final void mT__42() throws RecognitionException {
		try {
			int _type = T__42;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:39:7:
			// ( '/=' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:39:9:
			// '/='
			{
				match("/=");

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__42"

	// $ANTLR start "T__43"
	public final void mT__43() throws RecognitionException {
		try {
			int _type = T__43;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:40:7:
			// ( '?' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:40:9:
			// '?'
			{
				match('?');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__43"

	// $ANTLR start "T__44"
	public final void mT__44() throws RecognitionException {
		try {
			int _type = T__44;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:41:7:
			// ( 'or' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:41:9:
			// 'or'
			{
				match("or");

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__44"

	// $ANTLR start "T__45"
	public final void mT__45() throws RecognitionException {
		try {
			int _type = T__45;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:42:7:
			// ( 'and' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:42:9:
			// 'and'
			{
				match("and");

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__45"

	// $ANTLR start "T__46"
	public final void mT__46() throws RecognitionException {
		try {
			int _type = T__46;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:43:7:
			// ( '!=' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:43:9:
			// '!='
			{
				match("!=");

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__46"

	// $ANTLR start "T__47"
	public final void mT__47() throws RecognitionException {
		try {
			int _type = T__47;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:44:7:
			// ( '==' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:44:9:
			// '=='
			{
				match("==");

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__47"

	// $ANTLR start "T__48"
	public final void mT__48() throws RecognitionException {
		try {
			int _type = T__48;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:45:7:
			// ( '>=' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:45:9:
			// '>='
			{
				match(">=");

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__48"

	// $ANTLR start "T__49"
	public final void mT__49() throws RecognitionException {
		try {
			int _type = T__49;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:46:7:
			// ( '<=' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:46:9:
			// '<='
			{
				match("<=");

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__49"

	// $ANTLR start "T__50"
	public final void mT__50() throws RecognitionException {
		try {
			int _type = T__50;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:47:7:
			// ( '<' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:47:9:
			// '<'
			{
				match('<');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__50"

	// $ANTLR start "T__51"
	public final void mT__51() throws RecognitionException {
		try {
			int _type = T__51;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:48:7:
			// ( '>' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:48:9:
			// '>'
			{
				match('>');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__51"

	// $ANTLR start "T__52"
	public final void mT__52() throws RecognitionException {
		try {
			int _type = T__52;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:49:7:
			// ( '::' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:49:9:
			// '::'
			{
				match("::");

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__52"

	// $ANTLR start "T__53"
	public final void mT__53() throws RecognitionException {
		try {
			int _type = T__53;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:50:7:
			// ( '+' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:50:9:
			// '+'
			{
				match('+');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__53"

	// $ANTLR start "T__54"
	public final void mT__54() throws RecognitionException {
		try {
			int _type = T__54;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:51:7:
			// ( '*' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:51:9:
			// '*'
			{
				match('*');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__54"

	// $ANTLR start "T__55"
	public final void mT__55() throws RecognitionException {
		try {
			int _type = T__55;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:52:7:
			// ( '/' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:52:9:
			// '/'
			{
				match('/');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__55"

	// $ANTLR start "T__56"
	public final void mT__56() throws RecognitionException {
		try {
			int _type = T__56;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:53:7:
			// ( '^' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:53:9:
			// '^'
			{
				match('^');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__56"

	// $ANTLR start "T__57"
	public final void mT__57() throws RecognitionException {
		try {
			int _type = T__57;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:54:7:
			// ( '#' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:54:9:
			// '#'
			{
				match('#');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__57"

	// $ANTLR start "T__58"
	public final void mT__58() throws RecognitionException {
		try {
			int _type = T__58;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:55:7:
			// ( '.' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:55:9:
			// '.'
			{
				match('.');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__58"

	// $ANTLR start "T__59"
	public final void mT__59() throws RecognitionException {
		try {
			int _type = T__59;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:56:7:
			// ( '(' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:56:9:
			// '('
			{
				match('(');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__59"

	// $ANTLR start "T__60"
	public final void mT__60() throws RecognitionException {
		try {
			int _type = T__60;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:57:7:
			// ( ')' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:57:9:
			// ')'
			{
				match(')');

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "T__60"

	// $ANTLR start "RULE_INT"
	public final void mRULE_INT() throws RecognitionException {
		try {
			int _type = RULE_INT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10650:10:
			// ( ( '0' | '1' .. '9' ( '0' .. '9' )* ) )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10650:12:
			// ( '0' | '1' .. '9' ( '0' .. '9' )* )
			{
				// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10650:12:
				// ( '0' | '1' .. '9' ( '0' .. '9' )* )
				int alt2 = 2;
				int LA2_0 = input.LA(1);

				if ( LA2_0 == '0' ) {
					alt2 = 1;
				} else if ( LA2_0 >= '1' && LA2_0 <= '9' ) {
					alt2 = 2;
				} else {
					NoViableAltException nvae = new NoViableAltException("", 2, 0, input);

					throw nvae;
				}
				switch (alt2) {
					case 1:
					// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10650:13:
					// '0'
					{
						match('0');

					}
						break;
					case 2:
					// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10650:17:
					// '1' .. '9' ( '0' .. '9' )*
					{
						matchRange('1', '9');
						// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10650:26:
						// ( '0' .. '9' )*
						loop1: do {
							int alt1 = 2;
							int LA1_0 = input.LA(1);

							if ( LA1_0 >= '0' && LA1_0 <= '9' ) {
								alt1 = 1;
							}

							switch (alt1) {
								case 1:
								// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10650:27:
								// '0' .. '9'
								{
									matchRange('0', '9');

								}
									break;

								default:
									break loop1;
							}
						} while (true);

					}
						break;

				}

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "RULE_INT"

	// $ANTLR start "RULE_BOOLEAN"
	public final void mRULE_BOOLEAN() throws RecognitionException {
		try {
			int _type = RULE_BOOLEAN;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10652:14:
			// ( ( 'true' | 'false' ) )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10652:16:
			// ( 'true' | 'false' )
			{
				// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10652:16:
				// ( 'true' | 'false' )
				int alt3 = 2;
				int LA3_0 = input.LA(1);

				if ( LA3_0 == 't' ) {
					alt3 = 1;
				} else if ( LA3_0 == 'f' ) {
					alt3 = 2;
				} else {
					NoViableAltException nvae = new NoViableAltException("", 3, 0, input);

					throw nvae;
				}
				switch (alt3) {
					case 1:
					// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10652:17:
					// 'true'
					{
						match("true");

					}
						break;
					case 2:
					// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10652:24:
					// 'false'
					{
						match("false");

					}
						break;

				}

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "RULE_BOOLEAN"

	// $ANTLR start "RULE_ID"
	public final void mRULE_ID() throws RecognitionException {
		try {
			int _type = RULE_ID;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10654:9:
			// ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )+ ( '$' ( 'a' .. 'z' | 'A' .. 'Z' |
			// '_' | '0' .. '9' )+ )? )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10654:11:
			// ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )+ ( '$' ( 'a' .. 'z' | 'A' .. 'Z' | '_'
			// | '0' .. '9' )+ )?
			{
				// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10654:11:
				// ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )+
				int cnt4 = 0;
				loop4: do {
					int alt4 = 2;
					int LA4_0 = input.LA(1);

					if ( LA4_0 >= '0' && LA4_0 <= '9' || LA4_0 >= 'A' && LA4_0 <= 'Z' ||
						LA4_0 == '_' || LA4_0 >= 'a' && LA4_0 <= 'z' ) {
						alt4 = 1;
					}

					switch (alt4) {
						case 1:
						// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:
						{
							if ( input.LA(1) >= '0' && input.LA(1) <= '9' || input.LA(1) >= 'A' &&
								input.LA(1) <= 'Z' || input.LA(1) == '_' || input.LA(1) >= 'a' &&
								input.LA(1) <= 'z' ) {
								input.consume();

							} else {
								MismatchedSetException mse =
									new MismatchedSetException(null, input);
								recover(mse);
								throw mse;
							}

						}
							break;

						default:
							if ( cnt4 >= 1 ) {
								break loop4;
							}
							EarlyExitException eee = new EarlyExitException(4, input);
							throw eee;
					}
					cnt4++;
				} while (true);

				// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10654:45:
				// ( '$' ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )+ )?
				int alt6 = 2;
				int LA6_0 = input.LA(1);

				if ( LA6_0 == '$' ) {
					alt6 = 1;
				}
				switch (alt6) {
					case 1:
					// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10654:46:
					// '$' ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )+
					{
						match('$');
						// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10654:50:
						// ( 'a' .. 'z' | 'A' .. 'Z' | '_' | '0' .. '9' )+
						int cnt5 = 0;
						loop5: do {
							int alt5 = 2;
							int LA5_0 = input.LA(1);

							if ( LA5_0 >= '0' && LA5_0 <= '9' || LA5_0 >= 'A' && LA5_0 <= 'Z' ||
								LA5_0 == '_' || LA5_0 >= 'a' && LA5_0 <= 'z' ) {
								alt5 = 1;
							}

							switch (alt5) {
								case 1:
								// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:
								{
									if ( input.LA(1) >= '0' && input.LA(1) <= '9' ||
										input.LA(1) >= 'A' && input.LA(1) <= 'Z' ||
										input.LA(1) == '_' || input.LA(1) >= 'a' &&
										input.LA(1) <= 'z' ) {
										input.consume();

									} else {
										MismatchedSetException mse =
											new MismatchedSetException(null, input);
										recover(mse);
										throw mse;
									}

								}
									break;

								default:
									if ( cnt5 >= 1 ) {
										break loop5;
									}
									EarlyExitException eee = new EarlyExitException(5, input);
									throw eee;
							}
							cnt5++;
						} while (true);

					}
						break;

				}

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "RULE_ID"

	// $ANTLR start "RULE_COLOR"
	public final void mRULE_COLOR() throws RecognitionException {
		try {
			int _type = RULE_COLOR;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10656:12:
			// ( '#' ( '0' .. '9' | 'A' .. 'F' )+ )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10656:14:
			// '#' ( '0' .. '9' | 'A' .. 'F' )+
			{
				match('#');
				// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10656:18:
				// ( '0' .. '9' | 'A' .. 'F' )+
				int cnt7 = 0;
				loop7: do {
					int alt7 = 2;
					int LA7_0 = input.LA(1);

					if ( LA7_0 >= '0' && LA7_0 <= '9' || LA7_0 >= 'A' && LA7_0 <= 'F' ) {
						alt7 = 1;
					}

					switch (alt7) {
						case 1:
						// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:
						{
							if ( input.LA(1) >= '0' && input.LA(1) <= '9' || input.LA(1) >= 'A' &&
								input.LA(1) <= 'F' ) {
								input.consume();

							} else {
								MismatchedSetException mse =
									new MismatchedSetException(null, input);
								recover(mse);
								throw mse;
							}

						}
							break;

						default:
							if ( cnt7 >= 1 ) {
								break loop7;
							}
							EarlyExitException eee = new EarlyExitException(7, input);
							throw eee;
					}
					cnt7++;
				} while (true);

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "RULE_COLOR"

	// $ANTLR start "RULE_DOUBLE"
	public final void mRULE_DOUBLE() throws RecognitionException {
		try {
			int _type = RULE_DOUBLE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10658:13:
			// ( ( '1' .. '9' ( '0' .. '9' )* ( '.' ( '0' .. '9' )+ )? ( ( 'E' | 'e' ) ( '+' | '-'
			// )? ( '0' .. '9' )+ )? | '0' ( '.' ( '0' .. '9' )+ )? ( ( 'E' | 'e' ) ( '+' | '-' )? (
			// '0' .. '9' )+ )? ) )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10658:15:
			// ( '1' .. '9' ( '0' .. '9' )* ( '.' ( '0' .. '9' )+ )? ( ( 'E' | 'e' ) ( '+' | '-' )?
			// ( '0' .. '9' )+ )? | '0' ( '.' ( '0' .. '9' )+ )? ( ( 'E' | 'e' ) ( '+' | '-' )? (
			// '0' .. '9' )+ )? )
			{
				// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10658:15:
				// ( '1' .. '9' ( '0' .. '9' )* ( '.' ( '0' .. '9' )+ )? ( ( 'E' | 'e' ) ( '+' | '-'
				// )? ( '0' .. '9' )+ )? | '0' ( '.' ( '0' .. '9' )+ )? ( ( 'E' | 'e' ) ( '+' | '-'
				// )? ( '0' .. '9' )+ )? )
				int alt19 = 2;
				int LA19_0 = input.LA(1);

				if ( LA19_0 >= '1' && LA19_0 <= '9' ) {
					alt19 = 1;
				} else if ( LA19_0 == '0' ) {
					alt19 = 2;
				} else {
					NoViableAltException nvae = new NoViableAltException("", 19, 0, input);

					throw nvae;
				}
				switch (alt19) {
					case 1:
					// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10658:16:
					// '1' .. '9' ( '0' .. '9' )* ( '.' ( '0' .. '9' )+ )? ( ( 'E' | 'e' ) ( '+' |
					// '-' )? ( '0' .. '9' )+ )?
					{
						matchRange('1', '9');
						// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10658:25:
						// ( '0' .. '9' )*
						loop8: do {
							int alt8 = 2;
							int LA8_0 = input.LA(1);

							if ( LA8_0 >= '0' && LA8_0 <= '9' ) {
								alt8 = 1;
							}

							switch (alt8) {
								case 1:
								// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10658:26:
								// '0' .. '9'
								{
									matchRange('0', '9');

								}
									break;

								default:
									break loop8;
							}
						} while (true);

						// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10658:37:
						// ( '.' ( '0' .. '9' )+ )?
						int alt10 = 2;
						int LA10_0 = input.LA(1);

						if ( LA10_0 == '.' ) {
							alt10 = 1;
						}
						switch (alt10) {
							case 1:
							// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10658:38:
							// '.' ( '0' .. '9' )+
							{
								match('.');
								// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10658:42:
								// ( '0' .. '9' )+
								int cnt9 = 0;
								loop9: do {
									int alt9 = 2;
									int LA9_0 = input.LA(1);

									if ( LA9_0 >= '0' && LA9_0 <= '9' ) {
										alt9 = 1;
									}

									switch (alt9) {
										case 1:
										// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10658:43:
										// '0' .. '9'
										{
											matchRange('0', '9');

										}
											break;

										default:
											if ( cnt9 >= 1 ) {
												break loop9;
											}
											EarlyExitException eee =
												new EarlyExitException(9, input);
											throw eee;
									}
									cnt9++;
								} while (true);

							}
								break;

						}

						// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10658:56:
						// ( ( 'E' | 'e' ) ( '+' | '-' )? ( '0' .. '9' )+ )?
						int alt13 = 2;
						int LA13_0 = input.LA(1);

						if ( LA13_0 == 'E' || LA13_0 == 'e' ) {
							alt13 = 1;
						}
						switch (alt13) {
							case 1:
							// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10658:57:
							// ( 'E' | 'e' ) ( '+' | '-' )? ( '0' .. '9' )+
							{
								if ( input.LA(1) == 'E' || input.LA(1) == 'e' ) {
									input.consume();

								} else {
									MismatchedSetException mse =
										new MismatchedSetException(null, input);
									recover(mse);
									throw mse;
								}

								// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10658:67:
								// ( '+' | '-' )?
								int alt11 = 2;
								int LA11_0 = input.LA(1);

								if ( LA11_0 == '+' || LA11_0 == '-' ) {
									alt11 = 1;
								}
								switch (alt11) {
									case 1:
									// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:
									{
										if ( input.LA(1) == '+' || input.LA(1) == '-' ) {
											input.consume();

										} else {
											MismatchedSetException mse =
												new MismatchedSetException(null, input);
											recover(mse);
											throw mse;
										}

									}
										break;

								}

								// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10658:78:
								// ( '0' .. '9' )+
								int cnt12 = 0;
								loop12: do {
									int alt12 = 2;
									int LA12_0 = input.LA(1);

									if ( LA12_0 >= '0' && LA12_0 <= '9' ) {
										alt12 = 1;
									}

									switch (alt12) {
										case 1:
										// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10658:79:
										// '0' .. '9'
										{
											matchRange('0', '9');

										}
											break;

										default:
											if ( cnt12 >= 1 ) {
												break loop12;
											}
											EarlyExitException eee =
												new EarlyExitException(12, input);
											throw eee;
									}
									cnt12++;
								} while (true);

							}
								break;

						}

					}
						break;
					case 2:
					// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10658:92:
					// '0' ( '.' ( '0' .. '9' )+ )? ( ( 'E' | 'e' ) ( '+' | '-' )? ( '0' .. '9' )+
					// )?
					{
						match('0');
						// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10658:96:
						// ( '.' ( '0' .. '9' )+ )?
						int alt15 = 2;
						int LA15_0 = input.LA(1);

						if ( LA15_0 == '.' ) {
							alt15 = 1;
						}
						switch (alt15) {
							case 1:
							// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10658:97:
							// '.' ( '0' .. '9' )+
							{
								match('.');
								// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10658:101:
								// ( '0' .. '9' )+
								int cnt14 = 0;
								loop14: do {
									int alt14 = 2;
									int LA14_0 = input.LA(1);

									if ( LA14_0 >= '0' && LA14_0 <= '9' ) {
										alt14 = 1;
									}

									switch (alt14) {
										case 1:
										// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10658:102:
										// '0' .. '9'
										{
											matchRange('0', '9');

										}
											break;

										default:
											if ( cnt14 >= 1 ) {
												break loop14;
											}
											EarlyExitException eee =
												new EarlyExitException(14, input);
											throw eee;
									}
									cnt14++;
								} while (true);

							}
								break;

						}

						// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10658:115:
						// ( ( 'E' | 'e' ) ( '+' | '-' )? ( '0' .. '9' )+ )?
						int alt18 = 2;
						int LA18_0 = input.LA(1);

						if ( LA18_0 == 'E' || LA18_0 == 'e' ) {
							alt18 = 1;
						}
						switch (alt18) {
							case 1:
							// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10658:116:
							// ( 'E' | 'e' ) ( '+' | '-' )? ( '0' .. '9' )+
							{
								if ( input.LA(1) == 'E' || input.LA(1) == 'e' ) {
									input.consume();

								} else {
									MismatchedSetException mse =
										new MismatchedSetException(null, input);
									recover(mse);
									throw mse;
								}

								// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10658:126:
								// ( '+' | '-' )?
								int alt16 = 2;
								int LA16_0 = input.LA(1);

								if ( LA16_0 == '+' || LA16_0 == '-' ) {
									alt16 = 1;
								}
								switch (alt16) {
									case 1:
									// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:
									{
										if ( input.LA(1) == '+' || input.LA(1) == '-' ) {
											input.consume();

										} else {
											MismatchedSetException mse =
												new MismatchedSetException(null, input);
											recover(mse);
											throw mse;
										}

									}
										break;

								}

								// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10658:137:
								// ( '0' .. '9' )+
								int cnt17 = 0;
								loop17: do {
									int alt17 = 2;
									int LA17_0 = input.LA(1);

									if ( LA17_0 >= '0' && LA17_0 <= '9' ) {
										alt17 = 1;
									}

									switch (alt17) {
										case 1:
										// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10658:138:
										// '0' .. '9'
										{
											matchRange('0', '9');

										}
											break;

										default:
											if ( cnt17 >= 1 ) {
												break loop17;
											}
											EarlyExitException eee =
												new EarlyExitException(17, input);
											throw eee;
									}
									cnt17++;
								} while (true);

							}
								break;

						}

					}
						break;

				}

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "RULE_DOUBLE"

	// $ANTLR start "RULE_STRING"
	public final void mRULE_STRING() throws RecognitionException {
		try {
			int _type = RULE_STRING;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10660:13:
			// ( ( '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\"' | '\\\\' ) | ~ ( (
			// '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' |
			// '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' ) )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10660:15:
			// ( '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\"' | '\\\\' ) | ~ ( ( '\\\\'
			// | '\"' ) ) )* '\"' | '\\'' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\\'' |
			// '\\\\' ) | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' )
			{
				// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10660:15:
				// ( '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\"' | '\\\\' ) | ~ ( (
				// '\\\\' | '\"' ) ) )* '\"' | '\\'' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' |
				// '\\'' | '\\\\' ) | ~ ( ( '\\\\' | '\\'' ) ) )* '\\'' )
				int alt22 = 2;
				int LA22_0 = input.LA(1);

				if ( LA22_0 == '\"' ) {
					alt22 = 1;
				} else if ( LA22_0 == '\'' ) {
					alt22 = 2;
				} else {
					NoViableAltException nvae = new NoViableAltException("", 22, 0, input);

					throw nvae;
				}
				switch (alt22) {
					case 1:
					// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10660:16:
					// '\"' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\"' | '\\\\' ) | ~ ( (
					// '\\\\' | '\"' ) ) )* '\"'
					{
						match('\"');
						// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10660:20:
						// ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\"' | '\\\\' ) | ~ ( (
						// '\\\\' | '\"' ) ) )*
						loop20: do {
							int alt20 = 3;
							int LA20_0 = input.LA(1);

							if ( LA20_0 == '\\' ) {
								alt20 = 1;
							} else if ( LA20_0 >= '\u0000' && LA20_0 <= '!' || LA20_0 >= '#' &&
								LA20_0 <= '[' || LA20_0 >= ']' && LA20_0 <= '\uFFFF' ) {
								alt20 = 2;
							}

							switch (alt20) {
								case 1:
								// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10660:21:
								// '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\"' | '\\\\' )
								{
									match('\\');
									if ( input.LA(1) == '\"' || input.LA(1) == '\\' ||
										input.LA(1) == 'b' || input.LA(1) == 'f' ||
										input.LA(1) == 'n' || input.LA(1) == 'r' ||
										input.LA(1) >= 't' && input.LA(1) <= 'u' ) {
										input.consume();

									} else {
										MismatchedSetException mse =
											new MismatchedSetException(null, input);
										recover(mse);
										throw mse;
									}

								}
									break;
								case 2:
								// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10660:61:
								// ~ ( ( '\\\\' | '\"' ) )
								{
									if ( input.LA(1) >= '\u0000' && input.LA(1) <= '!' ||
										input.LA(1) >= '#' && input.LA(1) <= '[' ||
										input.LA(1) >= ']' && input.LA(1) <= '\uFFFF' ) {
										input.consume();

									} else {
										MismatchedSetException mse =
											new MismatchedSetException(null, input);
										recover(mse);
										throw mse;
									}

								}
									break;

								default:
									break loop20;
							}
						} while (true);

						match('\"');

					}
						break;
					case 2:
					// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10660:81:
					// '\\'' ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\\'' | '\\\\' ) | ~ ( (
					// '\\\\' | '\\'' ) ) )* '\\''
					{
						match('\'');
						// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10660:86:
						// ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\\'' | '\\\\' ) | ~ ( (
						// '\\\\' | '\\'' ) ) )*
						loop21: do {
							int alt21 = 3;
							int LA21_0 = input.LA(1);

							if ( LA21_0 == '\\' ) {
								alt21 = 1;
							} else if ( LA21_0 >= '\u0000' && LA21_0 <= '&' || LA21_0 >= '(' &&
								LA21_0 <= '[' || LA21_0 >= ']' && LA21_0 <= '\uFFFF' ) {
								alt21 = 2;
							}

							switch (alt21) {
								case 1:
								// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10660:87:
								// '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | 'u' | '\\'' | '\\\\' )
								{
									match('\\');
									if ( input.LA(1) == '\'' || input.LA(1) == '\\' ||
										input.LA(1) == 'b' || input.LA(1) == 'f' ||
										input.LA(1) == 'n' || input.LA(1) == 'r' ||
										input.LA(1) >= 't' && input.LA(1) <= 'u' ) {
										input.consume();

									} else {
										MismatchedSetException mse =
											new MismatchedSetException(null, input);
										recover(mse);
										throw mse;
									}

								}
									break;
								case 2:
								// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10660:128:
								// ~ ( ( '\\\\' | '\\'' ) )
								{
									if ( input.LA(1) >= '\u0000' && input.LA(1) <= '&' ||
										input.LA(1) >= '(' && input.LA(1) <= '[' ||
										input.LA(1) >= ']' && input.LA(1) <= '\uFFFF' ) {
										input.consume();

									} else {
										MismatchedSetException mse =
											new MismatchedSetException(null, input);
										recover(mse);
										throw mse;
									}

								}
									break;

								default:
									break loop21;
							}
						} while (true);

						match('\'');

					}
						break;

				}

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "RULE_STRING"

	// $ANTLR start "RULE_ML_COMMENT"
	public final void mRULE_ML_COMMENT() throws RecognitionException {
		try {
			int _type = RULE_ML_COMMENT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10662:17:
			// ( '/*' ( options {greedy=false; } : . )* '*/' )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10662:19:
			// '/*' ( options {greedy=false; } : . )* '*/'
			{
				match("/*");

				// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10662:24:
				// ( options {greedy=false; } : . )*
				loop23: do {
					int alt23 = 2;
					int LA23_0 = input.LA(1);

					if ( LA23_0 == '*' ) {
						int LA23_1 = input.LA(2);

						if ( LA23_1 == '/' ) {
							alt23 = 2;
						} else if ( LA23_1 >= '\u0000' && LA23_1 <= '.' || LA23_1 >= '0' &&
							LA23_1 <= '\uFFFF' ) {
							alt23 = 1;
						}

					} else if ( LA23_0 >= '\u0000' && LA23_0 <= ')' || LA23_0 >= '+' &&
						LA23_0 <= '\uFFFF' ) {
						alt23 = 1;
					}

					switch (alt23) {
						case 1:
						// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10662:52:
						// .
						{
							matchAny();

						}
							break;

						default:
							break loop23;
					}
				} while (true);

				match("*/");

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "RULE_ML_COMMENT"

	// $ANTLR start "RULE_SL_COMMENT"
	public final void mRULE_SL_COMMENT() throws RecognitionException {
		try {
			int _type = RULE_SL_COMMENT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10664:17:
			// ( '//' (~ ( ( '\\n' | '\\r' ) ) )* ( ( '\\r' )? '\\n' )? )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10664:19:
			// '//' (~ ( ( '\\n' | '\\r' ) ) )* ( ( '\\r' )? '\\n' )?
			{
				match("//");

				// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10664:24:
				// (~ ( ( '\\n' | '\\r' ) ) )*
				loop24: do {
					int alt24 = 2;
					int LA24_0 = input.LA(1);

					if ( LA24_0 >= '\u0000' && LA24_0 <= '\t' || LA24_0 >= '\u000B' &&
						LA24_0 <= '\f' || LA24_0 >= '\u000E' && LA24_0 <= '\uFFFF' ) {
						alt24 = 1;
					}

					switch (alt24) {
						case 1:
						// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10664:24:
						// ~ ( ( '\\n' | '\\r' ) )
						{
							if ( input.LA(1) >= '\u0000' && input.LA(1) <= '\t' ||
								input.LA(1) >= '\u000B' && input.LA(1) <= '\f' ||
								input.LA(1) >= '\u000E' && input.LA(1) <= '\uFFFF' ) {
								input.consume();

							} else {
								MismatchedSetException mse =
									new MismatchedSetException(null, input);
								recover(mse);
								throw mse;
							}

						}
							break;

						default:
							break loop24;
					}
				} while (true);

				// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10664:40:
				// ( ( '\\r' )? '\\n' )?
				int alt26 = 2;
				int LA26_0 = input.LA(1);

				if ( LA26_0 == '\n' || LA26_0 == '\r' ) {
					alt26 = 1;
				}
				switch (alt26) {
					case 1:
					// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10664:41:
					// ( '\\r' )? '\\n'
					{
						// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10664:41:
						// ( '\\r' )?
						int alt25 = 2;
						int LA25_0 = input.LA(1);

						if ( LA25_0 == '\r' ) {
							alt25 = 1;
						}
						switch (alt25) {
							case 1:
							// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10664:41:
							// '\\r'
							{
								match('\r');

							}
								break;

						}

						match('\n');

					}
						break;

				}

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "RULE_SL_COMMENT"

	// $ANTLR start "RULE_WS"
	public final void mRULE_WS() throws RecognitionException {
		try {
			int _type = RULE_WS;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10666:9:
			// ( ( ' ' | '\\t' | '\\r' | '\\n' )+ )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10666:11:
			// ( ' ' | '\\t' | '\\r' | '\\n' )+
			{
				// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10666:11:
				// ( ' ' | '\\t' | '\\r' | '\\n' )+
				int cnt27 = 0;
				loop27: do {
					int alt27 = 2;
					int LA27_0 = input.LA(1);

					if ( LA27_0 >= '\t' && LA27_0 <= '\n' || LA27_0 == '\r' || LA27_0 == ' ' ) {
						alt27 = 1;
					}

					switch (alt27) {
						case 1:
						// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:
						{
							if ( input.LA(1) >= '\t' && input.LA(1) <= '\n' ||
								input.LA(1) == '\r' || input.LA(1) == ' ' ) {
								input.consume();

							} else {
								MismatchedSetException mse =
									new MismatchedSetException(null, input);
								recover(mse);
								throw mse;
							}

						}
							break;

						default:
							if ( cnt27 >= 1 ) {
								break loop27;
							}
							EarlyExitException eee = new EarlyExitException(27, input);
							throw eee;
					}
					cnt27++;
				} while (true);

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "RULE_WS"

	// $ANTLR start "RULE_ANY_OTHER"
	public final void mRULE_ANY_OTHER() throws RecognitionException {
		try {
			int _type = RULE_ANY_OTHER;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10668:16:
			// ( . )
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:10668:18:
			// .
			{
				matchAny();

			}

			state.type = _type;
			state.channel = _channel;
		} finally {}
	}

	// $ANTLR end "RULE_ANY_OTHER"

	public void mTokens() throws RecognitionException {
		// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:8:
		// ( T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 |
		// T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | T__33 | T__34 | T__35 |
		// T__36 | T__37 | T__38 | T__39 | T__40 | T__41 | T__42 | T__43 | T__44 | T__45 | T__46 |
		// T__47 | T__48 | T__49 | T__50 | T__51 | T__52 | T__53 | T__54 | T__55 | T__56 | T__57 |
		// T__58 | T__59 | T__60 | RULE_INT | RULE_BOOLEAN | RULE_ID | RULE_COLOR | RULE_DOUBLE |
		// RULE_STRING | RULE_ML_COMMENT | RULE_SL_COMMENT | RULE_WS | RULE_ANY_OTHER )
		int alt28 = 57;
		alt28 = dfa28.predict(input);
		switch (alt28) {
			case 1:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:10:
			// T__14
			{
				mT__14();

			}
				break;
			case 2:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:16:
			// T__15
			{
				mT__15();

			}
				break;
			case 3:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:22:
			// T__16
			{
				mT__16();

			}
				break;
			case 4:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:28:
			// T__17
			{
				mT__17();

			}
				break;
			case 5:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:34:
			// T__18
			{
				mT__18();

			}
				break;
			case 6:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:40:
			// T__19
			{
				mT__19();

			}
				break;
			case 7:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:46:
			// T__20
			{
				mT__20();

			}
				break;
			case 8:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:52:
			// T__21
			{
				mT__21();

			}
				break;
			case 9:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:58:
			// T__22
			{
				mT__22();

			}
				break;
			case 10:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:64:
			// T__23
			{
				mT__23();

			}
				break;
			case 11:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:70:
			// T__24
			{
				mT__24();

			}
				break;
			case 12:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:76:
			// T__25
			{
				mT__25();

			}
				break;
			case 13:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:82:
			// T__26
			{
				mT__26();

			}
				break;
			case 14:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:88:
			// T__27
			{
				mT__27();

			}
				break;
			case 15:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:94:
			// T__28
			{
				mT__28();

			}
				break;
			case 16:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:100:
			// T__29
			{
				mT__29();

			}
				break;
			case 17:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:106:
			// T__30
			{
				mT__30();

			}
				break;
			case 18:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:112:
			// T__31
			{
				mT__31();

			}
				break;
			case 19:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:118:
			// T__32
			{
				mT__32();

			}
				break;
			case 20:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:124:
			// T__33
			{
				mT__33();

			}
				break;
			case 21:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:130:
			// T__34
			{
				mT__34();

			}
				break;
			case 22:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:136:
			// T__35
			{
				mT__35();

			}
				break;
			case 23:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:142:
			// T__36
			{
				mT__36();

			}
				break;
			case 24:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:148:
			// T__37
			{
				mT__37();

			}
				break;
			case 25:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:154:
			// T__38
			{
				mT__38();

			}
				break;
			case 26:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:160:
			// T__39
			{
				mT__39();

			}
				break;
			case 27:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:166:
			// T__40
			{
				mT__40();

			}
				break;
			case 28:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:172:
			// T__41
			{
				mT__41();

			}
				break;
			case 29:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:178:
			// T__42
			{
				mT__42();

			}
				break;
			case 30:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:184:
			// T__43
			{
				mT__43();

			}
				break;
			case 31:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:190:
			// T__44
			{
				mT__44();

			}
				break;
			case 32:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:196:
			// T__45
			{
				mT__45();

			}
				break;
			case 33:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:202:
			// T__46
			{
				mT__46();

			}
				break;
			case 34:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:208:
			// T__47
			{
				mT__47();

			}
				break;
			case 35:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:214:
			// T__48
			{
				mT__48();

			}
				break;
			case 36:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:220:
			// T__49
			{
				mT__49();

			}
				break;
			case 37:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:226:
			// T__50
			{
				mT__50();

			}
				break;
			case 38:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:232:
			// T__51
			{
				mT__51();

			}
				break;
			case 39:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:238:
			// T__52
			{
				mT__52();

			}
				break;
			case 40:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:244:
			// T__53
			{
				mT__53();

			}
				break;
			case 41:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:250:
			// T__54
			{
				mT__54();

			}
				break;
			case 42:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:256:
			// T__55
			{
				mT__55();

			}
				break;
			case 43:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:262:
			// T__56
			{
				mT__56();

			}
				break;
			case 44:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:268:
			// T__57
			{
				mT__57();

			}
				break;
			case 45:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:274:
			// T__58
			{
				mT__58();

			}
				break;
			case 46:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:280:
			// T__59
			{
				mT__59();

			}
				break;
			case 47:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:286:
			// T__60
			{
				mT__60();

			}
				break;
			case 48:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:292:
			// RULE_INT
			{
				mRULE_INT();

			}
				break;
			case 49:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:301:
			// RULE_BOOLEAN
			{
				mRULE_BOOLEAN();

			}
				break;
			case 50:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:314:
			// RULE_ID
			{
				mRULE_ID();

			}
				break;
			case 51:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:322:
			// RULE_COLOR
			{
				mRULE_COLOR();

			}
				break;
			case 52:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:333:
			// RULE_DOUBLE
			{
				mRULE_DOUBLE();

			}
				break;
			case 53:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:345:
			// RULE_STRING
			{
				mRULE_STRING();

			}
				break;
			case 54:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:357:
			// RULE_ML_COMMENT
			{
				mRULE_ML_COMMENT();

			}
				break;
			case 55:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:373:
			// RULE_SL_COMMENT
			{
				mRULE_SL_COMMENT();

			}
				break;
			case 56:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:389:
			// RULE_WS
			{
				mRULE_WS();

			}
				break;
			case 57:
			// ../msi.gama.lang.gaml.ui/src-gen/msi/gama/lang/gaml/ui/contentassist/antlr/internal/InternalGaml.g:1:397:
			// RULE_ANY_OTHER
			{
				mRULE_ANY_OTHER();

			}
				break;

		}

	}

	protected DFA28 dfa28 = new DFA28(this);
	static final String DFA28_eotS =
		"\2\uffff\1\51\1\53\5\56\5\uffff\1\100\1\102\2\56\1\106\1\110\1"
			+ "\114\1\uffff\2\56\1\121\1\123\1\uffff\1\126\3\uffff\2\132\1\56\1"
			+ "\uffff\2\46\7\uffff\1\142\1\56\1\uffff\13\56\11\uffff\2\56\11\uffff"
			+ "\1\161\1\56\13\uffff\1\56\1\uffff\1\132\2\56\3\uffff\1\56\1\167"
			+ "\1\56\1\171\10\56\1\u0082\1\56\1\uffff\1\u0084\4\56\1\uffff\1\u0087"
			+ "\1\uffff\10\56\1\uffff\1\56\1\uffff\1\56\1\u0092\1\uffff\1\56\1"
			+ "\u0094\5\56\1\u009a\1\56\1\u0087\1\uffff\1\u009c\1\uffff\1\56\1"
			+ "\u009f\3\56\1\uffff\1\56\1\uffff\1\56\1\u00a5\1\uffff\1\56\1\u00a7"
			+ "\2\56\1\u00aa\1\uffff\1\56\1\uffff\1\56\2\uffff\1\u00ad\1\u00ae" + "\2\uffff";
	static final String DFA28_eofS = "\u00af\uffff";
	static final String DFA28_minS =
		"\1\0\1\uffff\2\75\1\157\1\150\1\157\1\155\1\142\5\uffff\1\72\1"
			+ "\75\2\145\2\75\1\52\1\uffff\1\162\1\156\2\75\1\uffff\1\60\3\uffff"
			+ "\2\44\1\141\1\uffff\2\0\7\uffff\1\44\1\144\1\uffff\1\145\1\165\1"
			+ "\164\1\160\1\141\1\145\1\141\1\150\1\151\1\145\1\156\11\uffff\2"
			+ "\164\11\uffff\1\44\1\144\13\uffff\1\53\1\uffff\1\44\1\53\1\154\3"
			+ "\uffff\1\145\1\44\1\145\1\44\1\157\1\155\1\171\1\143\1\151\1\156"
			+ "\1\163\1\151\1\44\1\165\1\uffff\1\44\2\60\1\163\1\154\1\uffff\1"
			+ "\44\1\uffff\1\162\1\154\1\167\1\145\1\154\1\141\1\145\1\164\1\uffff"
			+ "\1\162\1\uffff\1\145\1\44\1\uffff\1\164\1\44\1\157\1\164\1\144\2"
			+ "\162\1\44\1\156\1\44\1\uffff\1\44\1\uffff\1\162\1\44\1\162\1\171"
			+ "\1\166\1\uffff\1\163\1\uffff\1\144\1\44\1\uffff\1\145\1\44\1\145"
			+ "\1\72\1\44\1\uffff\1\156\1\uffff\1\144\2\uffff\2\44\2\uffff";
	static final String DFA28_maxS =
		"\1\uffff\1\uffff\2\75\1\171\1\162\1\157\1\155\1\165\5\uffff\1\72"
			+ "\1\75\2\145\3\75\1\uffff\1\162\1\156\2\75\1\uffff\1\106\3\uffff"
			+ "\2\172\1\141\1\uffff\2\uffff\7\uffff\1\172\1\144\1\uffff\1\145\1"
			+ "\165\1\164\1\160\1\141\1\145\1\141\1\150\1\151\1\145\1\156\11\uffff"
			+ "\2\164\11\uffff\1\172\1\144\13\uffff\1\71\1\uffff\1\172\1\71\1\154"
			+ "\3\uffff\1\145\1\172\1\145\1\172\1\157\1\155\1\171\1\143\1\151\1"
			+ "\156\1\163\1\151\1\172\1\165\1\uffff\1\172\2\71\1\163\1\154\1\uffff"
			+ "\1\172\1\uffff\1\162\1\154\1\167\1\145\1\154\1\141\1\145\1\164\1"
			+ "\uffff\1\162\1\uffff\1\145\1\172\1\uffff\1\164\1\172\1\157\1\164"
			+ "\1\144\2\162\1\172\1\156\1\172\1\uffff\1\172\1\uffff\1\162\1\172"
			+ "\1\162\1\171\1\166\1\uffff\1\163\1\uffff\1\144\1\172\1\uffff\1\145"
			+ "\1\172\1\145\1\72\1\172\1\uffff\1\156\1\uffff\1\144\2\uffff\2\172" + "\2\uffff";
	static final String DFA28_acceptS =
		"\1\uffff\1\1\7\uffff\1\12\1\13\1\16\1\17\1\20\7\uffff\1\36\4\uffff"
			+ "\1\53\1\uffff\1\55\1\56\1\57\3\uffff\1\62\2\uffff\1\70\1\71\1\1"
			+ "\1\33\1\2\1\41\1\3\2\uffff\1\62\13\uffff\1\12\1\13\1\16\1\17\1\20"
			+ "\1\47\1\23\1\42\1\24\2\uffff\1\32\1\50\1\34\1\51\1\35\1\66\1\67"
			+ "\1\52\1\36\2\uffff\1\43\1\46\1\44\1\45\1\53\1\63\1\54\1\55\1\56"
			+ "\1\57\1\60\1\uffff\1\64\3\uffff\1\65\1\70\1\4\16\uffff\1\37\5\uffff"
			+ "\1\5\1\uffff\1\6\10\uffff\1\30\1\uffff\1\40\2\uffff\1\61\12\uffff"
			+ "\1\7\1\uffff\1\11\5\uffff\1\27\1\uffff\1\10\2\uffff\1\22\5\uffff"
			+ "\1\15\1\uffff\1\25\1\uffff\1\31\1\14\2\uffff\1\21\1\26";
	static final String DFA28_specialS = "\1\1\42\uffff\1\0\1\2\u008a\uffff}>";
	static final String[] DFA28_transitionS = {
		"\11\46\2\45\2\46\1\45\22\46\1\45\1\3\1\43\1\33\3\46\1\44\1"
			+ "\35\1\36\1\23\1\22\1\15\1\2\1\34\1\24\1\37\11\40\1\16\1\1\1"
			+ "\31\1\17\1\30\1\25\1\46\32\42\1\13\1\46\1\14\1\32\1\10\1\46"
			+ "\1\27\4\42\1\41\2\42\1\7\3\42\1\4\1\6\1\26\2\42\1\21\1\20\1"
			+ "\5\6\42\1\11\1\46\1\12\uff82\46",
		"",
		"\1\50",
		"\1\52",
		"\1\55\11\uffff\1\54",
		"\1\57\11\uffff\1\60",
		"\1\61",
		"\1\62",
		"\1\67\1\66\2\uffff\1\65\1\63\3\uffff\1\64\6\uffff\1\70\2\uffff" + "\1\71",
		"",
		"",
		"",
		"",
		"",
		"\1\77",
		"\1\101",
		"\1\103",
		"\1\104",
		"\1\105",
		"\1\107",
		"\1\112\4\uffff\1\113\15\uffff\1\111",
		"",
		"\1\116",
		"\1\117",
		"\1\120",
		"\1\122",
		"",
		"\12\125\7\uffff\6\125",
		"",
		"",
		"",
		"\1\56\11\uffff\1\134\1\uffff\12\56\7\uffff\4\56\1\133\25\56"
			+ "\4\uffff\1\56\1\uffff\4\56\1\133\25\56",
		"\1\56\11\uffff\1\134\1\uffff\12\135\7\uffff\4\56\1\136\25"
			+ "\56\4\uffff\1\56\1\uffff\4\56\1\136\25\56",
		"\1\137",
		"",
		"\0\140",
		"\0\140",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32" + "\56",
		"\1\143",
		"",
		"\1\144",
		"\1\145",
		"\1\146",
		"\1\147",
		"\1\150",
		"\1\151",
		"\1\152",
		"\1\153",
		"\1\154",
		"\1\155",
		"\1\156",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"\1\157",
		"\1\160",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32" + "\56",
		"\1\162",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"",
		"\1\134\1\uffff\1\134\2\uffff\12\163",
		"",
		"\1\56\11\uffff\1\134\1\uffff\12\135\7\uffff\4\56\1\136\25"
			+ "\56\4\uffff\1\56\1\uffff\4\56\1\136\25\56", "\1\134\1\uffff\1\134\2\uffff\12\164",
		"\1\165", "", "", "", "\1\166",
		"\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32" + "\56", "\1\170",
		"\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32" + "\56", "\1\172", "\1\173",
		"\1\174", "\1\175", "\1\176", "\1\177", "\1\u0080", "\1\u0081",
		"\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32" + "\56", "\1\u0083", "",
		"\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32" + "\56", "\12\163", "\12\164",
		"\1\u0085", "\1\u0086", "",
		"\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32" + "\56", "", "\1\u0088",
		"\1\u0089", "\1\u008a", "\1\u008b", "\1\u008c", "\1\u008d", "\1\u008e", "\1\u008f", "",
		"\1\u0090", "", "\1\u0091",
		"\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32" + "\56", "", "\1\u0093",
		"\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32" + "\56", "\1\u0095",
		"\1\u0096", "\1\u0097", "\1\u0098", "\1\u0099",
		"\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32" + "\56", "\1\u009b",
		"\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32" + "\56", "",
		"\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32" + "\56", "", "\1\u009d",
		"\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\22" + "\56\1\u009e\7\56",
		"\1\u00a0", "\1\u00a1", "\1\u00a2", "", "\1\u00a3", "", "\1\u00a4",
		"\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32" + "\56", "", "\1\u00a6",
		"\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32" + "\56", "\1\u00a8",
		"\1\u00a9", "\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32" + "\56", "",
		"\1\u00ab", "", "\1\u00ac", "", "",
		"\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32" + "\56",
		"\1\56\13\uffff\12\56\7\uffff\32\56\4\uffff\1\56\1\uffff\32" + "\56", "", "" };

	static final short[] DFA28_eot = DFA.unpackEncodedString(DFA28_eotS);
	static final short[] DFA28_eof = DFA.unpackEncodedString(DFA28_eofS);
	static final char[] DFA28_min = DFA.unpackEncodedStringToUnsignedChars(DFA28_minS);
	static final char[] DFA28_max = DFA.unpackEncodedStringToUnsignedChars(DFA28_maxS);
	static final short[] DFA28_accept = DFA.unpackEncodedString(DFA28_acceptS);
	static final short[] DFA28_special = DFA.unpackEncodedString(DFA28_specialS);
	static final short[][] DFA28_transition;

	static {
		int numStates = DFA28_transitionS.length;
		DFA28_transition = new short[numStates][];
		for ( int i = 0; i < numStates; i++ ) {
			DFA28_transition[i] = DFA.unpackEncodedString(DFA28_transitionS[i]);
		}
	}

	class DFA28 extends DFA {

		public DFA28(final BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 28;
			this.eot = DFA28_eot;
			this.eof = DFA28_eof;
			this.min = DFA28_min;
			this.max = DFA28_max;
			this.accept = DFA28_accept;
			this.special = DFA28_special;
			this.transition = DFA28_transition;
		}

		public String getDescription() {
			return "1:1: Tokens : ( T__14 | T__15 | T__16 | T__17 | T__18 | T__19 | T__20 | T__21 | T__22 | T__23 | T__24 | T__25 | T__26 | T__27 | T__28 | T__29 | T__30 | T__31 | T__32 | T__33 | T__34 | T__35 | T__36 | T__37 | T__38 | T__39 | T__40 | T__41 | T__42 | T__43 | T__44 | T__45 | T__46 | T__47 | T__48 | T__49 | T__50 | T__51 | T__52 | T__53 | T__54 | T__55 | T__56 | T__57 | T__58 | T__59 | T__60 | RULE_INT | RULE_BOOLEAN | RULE_ID | RULE_COLOR | RULE_DOUBLE | RULE_STRING | RULE_ML_COMMENT | RULE_SL_COMMENT | RULE_WS | RULE_ANY_OTHER );";
		}

		public int specialStateTransition(int s, final IntStream _input)
			throws NoViableAltException {
			IntStream input = _input;
			int _s = s;
			switch (s) {
				case 0:
					int LA28_35 = input.LA(1);

					s = -1;
					if ( LA28_35 >= '\u0000' && LA28_35 <= '\uFFFF' ) {
						s = 96;
					} else {
						s = 38;
					}

					if ( s >= 0 ) { return s; }
					break;
				case 1:
					int LA28_0 = input.LA(1);

					s = -1;
					if ( LA28_0 == ';' ) {
						s = 1;
					}

					else if ( LA28_0 == '-' ) {
						s = 2;
					}

					else if ( LA28_0 == '!' ) {
						s = 3;
					}

					else if ( LA28_0 == 'm' ) {
						s = 4;
					}

					else if ( LA28_0 == 't' ) {
						s = 5;
					}

					else if ( LA28_0 == 'n' ) {
						s = 6;
					}

					else if ( LA28_0 == 'i' ) {
						s = 7;
					}

					else if ( LA28_0 == '_' ) {
						s = 8;
					}

					else if ( LA28_0 == '{' ) {
						s = 9;
					}

					else if ( LA28_0 == '}' ) {
						s = 10;
					}

					else if ( LA28_0 == '[' ) {
						s = 11;
					}

					else if ( LA28_0 == ']' ) {
						s = 12;
					}

					else if ( LA28_0 == ',' ) {
						s = 13;
					}

					else if ( LA28_0 == ':' ) {
						s = 14;
					}

					else if ( LA28_0 == '=' ) {
						s = 15;
					}

					else if ( LA28_0 == 's' ) {
						s = 16;
					}

					else if ( LA28_0 == 'r' ) {
						s = 17;
					}

					else if ( LA28_0 == '+' ) {
						s = 18;
					}

					else if ( LA28_0 == '*' ) {
						s = 19;
					}

					else if ( LA28_0 == '/' ) {
						s = 20;
					}

					else if ( LA28_0 == '?' ) {
						s = 21;
					}

					else if ( LA28_0 == 'o' ) {
						s = 22;
					}

					else if ( LA28_0 == 'a' ) {
						s = 23;
					}

					else if ( LA28_0 == '>' ) {
						s = 24;
					}

					else if ( LA28_0 == '<' ) {
						s = 25;
					}

					else if ( LA28_0 == '^' ) {
						s = 26;
					}

					else if ( LA28_0 == '#' ) {
						s = 27;
					}

					else if ( LA28_0 == '.' ) {
						s = 28;
					}

					else if ( LA28_0 == '(' ) {
						s = 29;
					}

					else if ( LA28_0 == ')' ) {
						s = 30;
					}

					else if ( LA28_0 == '0' ) {
						s = 31;
					}

					else if ( LA28_0 >= '1' && LA28_0 <= '9' ) {
						s = 32;
					}

					else if ( LA28_0 == 'f' ) {
						s = 33;
					}

					else if ( LA28_0 >= 'A' && LA28_0 <= 'Z' || LA28_0 >= 'b' && LA28_0 <= 'e' ||
						LA28_0 >= 'g' && LA28_0 <= 'h' || LA28_0 >= 'j' && LA28_0 <= 'l' ||
						LA28_0 >= 'p' && LA28_0 <= 'q' || LA28_0 >= 'u' && LA28_0 <= 'z' ) {
						s = 34;
					}

					else if ( LA28_0 == '\"' ) {
						s = 35;
					}

					else if ( LA28_0 == '\'' ) {
						s = 36;
					}

					else if ( LA28_0 >= '\t' && LA28_0 <= '\n' || LA28_0 == '\r' || LA28_0 == ' ' ) {
						s = 37;
					}

					else if ( LA28_0 >= '\u0000' && LA28_0 <= '\b' || LA28_0 >= '\u000B' &&
						LA28_0 <= '\f' || LA28_0 >= '\u000E' && LA28_0 <= '\u001F' ||
						LA28_0 >= '$' && LA28_0 <= '&' || LA28_0 == '@' || LA28_0 == '\\' ||
						LA28_0 == '`' || LA28_0 == '|' || LA28_0 >= '~' && LA28_0 <= '\uFFFF' ) {
						s = 38;
					}

					if ( s >= 0 ) { return s; }
					break;
				case 2:
					int LA28_36 = input.LA(1);

					s = -1;
					if ( LA28_36 >= '\u0000' && LA28_36 <= '\uFFFF' ) {
						s = 96;
					} else {
						s = 38;
					}

					if ( s >= 0 ) { return s; }
					break;
			}
			NoViableAltException nvae = new NoViableAltException(getDescription(), 28, _s, input);
			error(nvae);
			throw nvae;
		}
	}

}