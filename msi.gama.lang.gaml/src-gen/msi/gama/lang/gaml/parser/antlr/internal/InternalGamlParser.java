package msi.gama.lang.gaml.parser.antlr.internal; 

import org.eclipse.xtext.*;
import org.eclipse.xtext.parser.*;
import org.eclipse.xtext.parser.impl.*;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.parser.antlr.AbstractInternalAntlrParser;
import org.eclipse.xtext.parser.antlr.XtextTokenStream;
import org.eclipse.xtext.parser.antlr.XtextTokenStream.HiddenTokens;
import org.eclipse.xtext.parser.antlr.AntlrDatatypeRuleToken;
import msi.gama.lang.gaml.services.GamlGrammarAccess;



import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
@SuppressWarnings("all")
public class InternalGamlParser extends AbstractInternalAntlrParser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_ID", "RULE_STRING", "RULE_INTEGER", "RULE_DOUBLE", "RULE_COLOR", "RULE_BOOLEAN", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'model'", "'import'", "'add'", "'ask'", "'capture'", "'create'", "'draw'", "'error'", "'match'", "'match_between'", "'match_one'", "'put'", "'release'", "'remove'", "'save'", "'set'", "'switch'", "'warn'", "'write'", "'display_population'", "'display_grid'", "'='", "';'", "'if'", "'condition:'", "'else'", "':'", "'<'", "','", "'>'", "'return'", "'<-'", "'<<'", "'>>'", "'+='", "'-='", "'++'", "'--'", "'function:'", "'->'", "'{'", "'}'", "'name:'", "'returns:'", "'action:'", "'?'", "'or'", "'and'", "'!='", "'>='", "'<='", "'::'", "'+'", "'-'", "'*'", "'/'", "'^'", "'#'", "'\\u00B0'", "'!'", "'my'", "'the'", "'not'", "'['", "']'", "'.'", "'('", "')'"
    };
    public static final int T__68=68;
    public static final int RULE_BOOLEAN=9;
    public static final int T__69=69;
    public static final int RULE_ID=4;
    public static final int T__66=66;
    public static final int T__67=67;
    public static final int T__64=64;
    public static final int T__29=29;
    public static final int T__65=65;
    public static final int T__28=28;
    public static final int T__62=62;
    public static final int T__27=27;
    public static final int T__63=63;
    public static final int T__26=26;
    public static final int T__25=25;
    public static final int T__24=24;
    public static final int T__23=23;
    public static final int T__22=22;
    public static final int RULE_ANY_OTHER=13;
    public static final int T__21=21;
    public static final int T__20=20;
    public static final int T__61=61;
    public static final int RULE_COLOR=8;
    public static final int T__60=60;
    public static final int EOF=-1;
    public static final int T__55=55;
    public static final int T__56=56;
    public static final int T__19=19;
    public static final int T__57=57;
    public static final int T__58=58;
    public static final int T__16=16;
    public static final int T__51=51;
    public static final int T__15=15;
    public static final int T__52=52;
    public static final int T__18=18;
    public static final int T__53=53;
    public static final int T__54=54;
    public static final int T__17=17;
    public static final int T__14=14;
    public static final int T__59=59;
    public static final int T__50=50;
    public static final int T__42=42;
    public static final int T__43=43;
    public static final int T__40=40;
    public static final int T__41=41;
    public static final int T__80=80;
    public static final int T__46=46;
    public static final int T__81=81;
    public static final int T__47=47;
    public static final int T__44=44;
    public static final int T__45=45;
    public static final int T__48=48;
    public static final int T__49=49;
    public static final int RULE_SL_COMMENT=11;
    public static final int RULE_DOUBLE=7;
    public static final int RULE_ML_COMMENT=10;
    public static final int T__30=30;
    public static final int T__31=31;
    public static final int RULE_STRING=5;
    public static final int T__32=32;
    public static final int T__71=71;
    public static final int T__33=33;
    public static final int T__72=72;
    public static final int T__34=34;
    public static final int T__35=35;
    public static final int T__70=70;
    public static final int T__36=36;
    public static final int T__37=37;
    public static final int T__38=38;
    public static final int T__39=39;
    public static final int RULE_WS=12;
    public static final int T__76=76;
    public static final int T__75=75;
    public static final int T__74=74;
    public static final int T__73=73;
    public static final int RULE_INTEGER=6;
    public static final int T__79=79;
    public static final int T__78=78;
    public static final int T__77=77;

    // delegates
    // delegators


        public InternalGamlParser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public InternalGamlParser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return InternalGamlParser.tokenNames; }
    public String getGrammarFileName() { return "../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g"; }



     	private GamlGrammarAccess grammarAccess;
     	
        public InternalGamlParser(TokenStream input, GamlGrammarAccess grammarAccess) {
            this(input);
            this.grammarAccess = grammarAccess;
            registerRules(grammarAccess.getGrammar());
        }
        
        @Override
        protected String getFirstRuleName() {
        	return "Model";	
       	}
       	
       	@Override
       	protected GamlGrammarAccess getGrammarAccess() {
       		return grammarAccess;
       	}



    // $ANTLR start "entryRuleModel"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:67:1: entryRuleModel returns [EObject current=null] : iv_ruleModel= ruleModel EOF ;
    public final EObject entryRuleModel() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleModel = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:68:2: (iv_ruleModel= ruleModel EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:69:2: iv_ruleModel= ruleModel EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getModelRule()); 
            }
            pushFollow(FOLLOW_ruleModel_in_entryRuleModel75);
            iv_ruleModel=ruleModel();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleModel; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleModel85); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleModel"


    // $ANTLR start "ruleModel"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:76:1: ruleModel returns [EObject current=null] : (otherlv_0= 'model' ( (lv_name_1_0= RULE_ID ) ) ( (lv_imports_2_0= ruleImport ) )* ( (lv_statements_3_0= ruleStatement ) )* ) ;
    public final EObject ruleModel() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        EObject lv_imports_2_0 = null;

        EObject lv_statements_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:79:28: ( (otherlv_0= 'model' ( (lv_name_1_0= RULE_ID ) ) ( (lv_imports_2_0= ruleImport ) )* ( (lv_statements_3_0= ruleStatement ) )* ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:80:1: (otherlv_0= 'model' ( (lv_name_1_0= RULE_ID ) ) ( (lv_imports_2_0= ruleImport ) )* ( (lv_statements_3_0= ruleStatement ) )* )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:80:1: (otherlv_0= 'model' ( (lv_name_1_0= RULE_ID ) ) ( (lv_imports_2_0= ruleImport ) )* ( (lv_statements_3_0= ruleStatement ) )* )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:80:3: otherlv_0= 'model' ( (lv_name_1_0= RULE_ID ) ) ( (lv_imports_2_0= ruleImport ) )* ( (lv_statements_3_0= ruleStatement ) )*
            {
            otherlv_0=(Token)match(input,14,FOLLOW_14_in_ruleModel122); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_0, grammarAccess.getModelAccess().getModelKeyword_0());
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:84:1: ( (lv_name_1_0= RULE_ID ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:85:1: (lv_name_1_0= RULE_ID )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:85:1: (lv_name_1_0= RULE_ID )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:86:3: lv_name_1_0= RULE_ID
            {
            lv_name_1_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleModel139); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(lv_name_1_0, grammarAccess.getModelAccess().getNameIDTerminalRuleCall_1_0()); 
              		
            }
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElement(grammarAccess.getModelRule());
              	        }
                     		setWithLastConsumed(
                     			current, 
                     			"name",
                      		lv_name_1_0, 
                      		"ID");
              	    
            }

            }


            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:102:2: ( (lv_imports_2_0= ruleImport ) )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==15) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:103:1: (lv_imports_2_0= ruleImport )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:103:1: (lv_imports_2_0= ruleImport )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:104:3: lv_imports_2_0= ruleImport
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getModelAccess().getImportsImportParserRuleCall_2_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleImport_in_ruleModel165);
            	    lv_imports_2_0=ruleImport();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getModelRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"imports",
            	              		lv_imports_2_0, 
            	              		"Import");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:120:3: ( (lv_statements_3_0= ruleStatement ) )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>=RULE_ID && LA2_0<=RULE_BOOLEAN)||(LA2_0>=16 && LA2_0<=34)||LA2_0==37||LA2_0==44||LA2_0==54||(LA2_0>=56 && LA2_0<=58)||LA2_0==67||(LA2_0>=72 && LA2_0<=77)||LA2_0==80) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:121:1: (lv_statements_3_0= ruleStatement )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:121:1: (lv_statements_3_0= ruleStatement )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:122:3: lv_statements_3_0= ruleStatement
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getModelAccess().getStatementsStatementParserRuleCall_3_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleStatement_in_ruleModel187);
            	    lv_statements_3_0=ruleStatement();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getModelRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"statements",
            	              		lv_statements_3_0, 
            	              		"Statement");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleModel"


    // $ANTLR start "entryRuleImport"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:146:1: entryRuleImport returns [EObject current=null] : iv_ruleImport= ruleImport EOF ;
    public final EObject entryRuleImport() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleImport = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:147:2: (iv_ruleImport= ruleImport EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:148:2: iv_ruleImport= ruleImport EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getImportRule()); 
            }
            pushFollow(FOLLOW_ruleImport_in_entryRuleImport224);
            iv_ruleImport=ruleImport();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleImport; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleImport234); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleImport"


    // $ANTLR start "ruleImport"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:155:1: ruleImport returns [EObject current=null] : (otherlv_0= 'import' ( (lv_importURI_1_0= RULE_STRING ) ) ) ;
    public final EObject ruleImport() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_importURI_1_0=null;

         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:158:28: ( (otherlv_0= 'import' ( (lv_importURI_1_0= RULE_STRING ) ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:159:1: (otherlv_0= 'import' ( (lv_importURI_1_0= RULE_STRING ) ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:159:1: (otherlv_0= 'import' ( (lv_importURI_1_0= RULE_STRING ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:159:3: otherlv_0= 'import' ( (lv_importURI_1_0= RULE_STRING ) )
            {
            otherlv_0=(Token)match(input,15,FOLLOW_15_in_ruleImport271); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_0, grammarAccess.getImportAccess().getImportKeyword_0());
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:163:1: ( (lv_importURI_1_0= RULE_STRING ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:164:1: (lv_importURI_1_0= RULE_STRING )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:164:1: (lv_importURI_1_0= RULE_STRING )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:165:3: lv_importURI_1_0= RULE_STRING
            {
            lv_importURI_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleImport288); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(lv_importURI_1_0, grammarAccess.getImportAccess().getImportURISTRINGTerminalRuleCall_1_0()); 
              		
            }
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElement(grammarAccess.getImportRule());
              	        }
                     		setWithLastConsumed(
                     			current, 
                     			"importURI",
                      		lv_importURI_1_0, 
                      		"STRING");
              	    
            }

            }


            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleImport"


    // $ANTLR start "entryRuleBuiltInStatementKey"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:189:1: entryRuleBuiltInStatementKey returns [String current=null] : iv_ruleBuiltInStatementKey= ruleBuiltInStatementKey EOF ;
    public final String entryRuleBuiltInStatementKey() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleBuiltInStatementKey = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:190:2: (iv_ruleBuiltInStatementKey= ruleBuiltInStatementKey EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:191:2: iv_ruleBuiltInStatementKey= ruleBuiltInStatementKey EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getBuiltInStatementKeyRule()); 
            }
            pushFollow(FOLLOW_ruleBuiltInStatementKey_in_entryRuleBuiltInStatementKey330);
            iv_ruleBuiltInStatementKey=ruleBuiltInStatementKey();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleBuiltInStatementKey.getText(); 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleBuiltInStatementKey341); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleBuiltInStatementKey"


    // $ANTLR start "ruleBuiltInStatementKey"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:198:1: ruleBuiltInStatementKey returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'add' | kw= 'ask' | kw= 'capture' | kw= 'create' | kw= 'draw' | kw= 'error' | kw= 'match' | kw= 'match_between' | kw= 'match_one' | kw= 'put' | kw= 'release' | kw= 'remove' | kw= 'save' | kw= 'set' | kw= 'switch' | kw= 'warn' | kw= 'write' | kw= 'display_population' | kw= 'display_grid' ) ;
    public final AntlrDatatypeRuleToken ruleBuiltInStatementKey() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;

         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:201:28: ( (kw= 'add' | kw= 'ask' | kw= 'capture' | kw= 'create' | kw= 'draw' | kw= 'error' | kw= 'match' | kw= 'match_between' | kw= 'match_one' | kw= 'put' | kw= 'release' | kw= 'remove' | kw= 'save' | kw= 'set' | kw= 'switch' | kw= 'warn' | kw= 'write' | kw= 'display_population' | kw= 'display_grid' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:202:1: (kw= 'add' | kw= 'ask' | kw= 'capture' | kw= 'create' | kw= 'draw' | kw= 'error' | kw= 'match' | kw= 'match_between' | kw= 'match_one' | kw= 'put' | kw= 'release' | kw= 'remove' | kw= 'save' | kw= 'set' | kw= 'switch' | kw= 'warn' | kw= 'write' | kw= 'display_population' | kw= 'display_grid' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:202:1: (kw= 'add' | kw= 'ask' | kw= 'capture' | kw= 'create' | kw= 'draw' | kw= 'error' | kw= 'match' | kw= 'match_between' | kw= 'match_one' | kw= 'put' | kw= 'release' | kw= 'remove' | kw= 'save' | kw= 'set' | kw= 'switch' | kw= 'warn' | kw= 'write' | kw= 'display_population' | kw= 'display_grid' )
            int alt3=19;
            switch ( input.LA(1) ) {
            case 16:
                {
                alt3=1;
                }
                break;
            case 17:
                {
                alt3=2;
                }
                break;
            case 18:
                {
                alt3=3;
                }
                break;
            case 19:
                {
                alt3=4;
                }
                break;
            case 20:
                {
                alt3=5;
                }
                break;
            case 21:
                {
                alt3=6;
                }
                break;
            case 22:
                {
                alt3=7;
                }
                break;
            case 23:
                {
                alt3=8;
                }
                break;
            case 24:
                {
                alt3=9;
                }
                break;
            case 25:
                {
                alt3=10;
                }
                break;
            case 26:
                {
                alt3=11;
                }
                break;
            case 27:
                {
                alt3=12;
                }
                break;
            case 28:
                {
                alt3=13;
                }
                break;
            case 29:
                {
                alt3=14;
                }
                break;
            case 30:
                {
                alt3=15;
                }
                break;
            case 31:
                {
                alt3=16;
                }
                break;
            case 32:
                {
                alt3=17;
                }
                break;
            case 33:
                {
                alt3=18;
                }
                break;
            case 34:
                {
                alt3=19;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;
            }

            switch (alt3) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:203:2: kw= 'add'
                    {
                    kw=(Token)match(input,16,FOLLOW_16_in_ruleBuiltInStatementKey379); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInStatementKeyAccess().getAddKeyword_0()); 
                          
                    }

                    }
                    break;
                case 2 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:210:2: kw= 'ask'
                    {
                    kw=(Token)match(input,17,FOLLOW_17_in_ruleBuiltInStatementKey398); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInStatementKeyAccess().getAskKeyword_1()); 
                          
                    }

                    }
                    break;
                case 3 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:217:2: kw= 'capture'
                    {
                    kw=(Token)match(input,18,FOLLOW_18_in_ruleBuiltInStatementKey417); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInStatementKeyAccess().getCaptureKeyword_2()); 
                          
                    }

                    }
                    break;
                case 4 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:224:2: kw= 'create'
                    {
                    kw=(Token)match(input,19,FOLLOW_19_in_ruleBuiltInStatementKey436); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInStatementKeyAccess().getCreateKeyword_3()); 
                          
                    }

                    }
                    break;
                case 5 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:231:2: kw= 'draw'
                    {
                    kw=(Token)match(input,20,FOLLOW_20_in_ruleBuiltInStatementKey455); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInStatementKeyAccess().getDrawKeyword_4()); 
                          
                    }

                    }
                    break;
                case 6 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:238:2: kw= 'error'
                    {
                    kw=(Token)match(input,21,FOLLOW_21_in_ruleBuiltInStatementKey474); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInStatementKeyAccess().getErrorKeyword_5()); 
                          
                    }

                    }
                    break;
                case 7 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:245:2: kw= 'match'
                    {
                    kw=(Token)match(input,22,FOLLOW_22_in_ruleBuiltInStatementKey493); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInStatementKeyAccess().getMatchKeyword_6()); 
                          
                    }

                    }
                    break;
                case 8 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:252:2: kw= 'match_between'
                    {
                    kw=(Token)match(input,23,FOLLOW_23_in_ruleBuiltInStatementKey512); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInStatementKeyAccess().getMatch_betweenKeyword_7()); 
                          
                    }

                    }
                    break;
                case 9 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:259:2: kw= 'match_one'
                    {
                    kw=(Token)match(input,24,FOLLOW_24_in_ruleBuiltInStatementKey531); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInStatementKeyAccess().getMatch_oneKeyword_8()); 
                          
                    }

                    }
                    break;
                case 10 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:266:2: kw= 'put'
                    {
                    kw=(Token)match(input,25,FOLLOW_25_in_ruleBuiltInStatementKey550); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInStatementKeyAccess().getPutKeyword_9()); 
                          
                    }

                    }
                    break;
                case 11 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:273:2: kw= 'release'
                    {
                    kw=(Token)match(input,26,FOLLOW_26_in_ruleBuiltInStatementKey569); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInStatementKeyAccess().getReleaseKeyword_10()); 
                          
                    }

                    }
                    break;
                case 12 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:280:2: kw= 'remove'
                    {
                    kw=(Token)match(input,27,FOLLOW_27_in_ruleBuiltInStatementKey588); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInStatementKeyAccess().getRemoveKeyword_11()); 
                          
                    }

                    }
                    break;
                case 13 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:287:2: kw= 'save'
                    {
                    kw=(Token)match(input,28,FOLLOW_28_in_ruleBuiltInStatementKey607); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInStatementKeyAccess().getSaveKeyword_12()); 
                          
                    }

                    }
                    break;
                case 14 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:294:2: kw= 'set'
                    {
                    kw=(Token)match(input,29,FOLLOW_29_in_ruleBuiltInStatementKey626); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInStatementKeyAccess().getSetKeyword_13()); 
                          
                    }

                    }
                    break;
                case 15 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:301:2: kw= 'switch'
                    {
                    kw=(Token)match(input,30,FOLLOW_30_in_ruleBuiltInStatementKey645); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInStatementKeyAccess().getSwitchKeyword_14()); 
                          
                    }

                    }
                    break;
                case 16 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:308:2: kw= 'warn'
                    {
                    kw=(Token)match(input,31,FOLLOW_31_in_ruleBuiltInStatementKey664); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInStatementKeyAccess().getWarnKeyword_15()); 
                          
                    }

                    }
                    break;
                case 17 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:315:2: kw= 'write'
                    {
                    kw=(Token)match(input,32,FOLLOW_32_in_ruleBuiltInStatementKey683); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInStatementKeyAccess().getWriteKeyword_16()); 
                          
                    }

                    }
                    break;
                case 18 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:322:2: kw= 'display_population'
                    {
                    kw=(Token)match(input,33,FOLLOW_33_in_ruleBuiltInStatementKey702); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInStatementKeyAccess().getDisplay_populationKeyword_17()); 
                          
                    }

                    }
                    break;
                case 19 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:329:2: kw= 'display_grid'
                    {
                    kw=(Token)match(input,34,FOLLOW_34_in_ruleBuiltInStatementKey721); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInStatementKeyAccess().getDisplay_gridKeyword_18()); 
                          
                    }

                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleBuiltInStatementKey"


    // $ANTLR start "entryRuleStatement"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:342:1: entryRuleStatement returns [EObject current=null] : iv_ruleStatement= ruleStatement EOF ;
    public final EObject entryRuleStatement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleStatement = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:343:2: (iv_ruleStatement= ruleStatement EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:344:2: iv_ruleStatement= ruleStatement EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getStatementRule()); 
            }
            pushFollow(FOLLOW_ruleStatement_in_entryRuleStatement761);
            iv_ruleStatement=ruleStatement();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleStatement; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleStatement771); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleStatement"


    // $ANTLR start "ruleStatement"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:351:1: ruleStatement returns [EObject current=null] : ( ( ( ruleAssignmentStatement )=>this_AssignmentStatement_0= ruleAssignmentStatement ) | (this_ReturnStatement_1= ruleReturnStatement | this_IfStatement_2= ruleIfStatement | this_ClassicStatement_3= ruleClassicStatement | this_DefinitionStatement_4= ruleDefinitionStatement | this_Equation_5= ruleEquation ) ) ;
    public final EObject ruleStatement() throws RecognitionException {
        EObject current = null;

        EObject this_AssignmentStatement_0 = null;

        EObject this_ReturnStatement_1 = null;

        EObject this_IfStatement_2 = null;

        EObject this_ClassicStatement_3 = null;

        EObject this_DefinitionStatement_4 = null;

        EObject this_Equation_5 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:354:28: ( ( ( ( ruleAssignmentStatement )=>this_AssignmentStatement_0= ruleAssignmentStatement ) | (this_ReturnStatement_1= ruleReturnStatement | this_IfStatement_2= ruleIfStatement | this_ClassicStatement_3= ruleClassicStatement | this_DefinitionStatement_4= ruleDefinitionStatement | this_Equation_5= ruleEquation ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:355:1: ( ( ( ruleAssignmentStatement )=>this_AssignmentStatement_0= ruleAssignmentStatement ) | (this_ReturnStatement_1= ruleReturnStatement | this_IfStatement_2= ruleIfStatement | this_ClassicStatement_3= ruleClassicStatement | this_DefinitionStatement_4= ruleDefinitionStatement | this_Equation_5= ruleEquation ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:355:1: ( ( ( ruleAssignmentStatement )=>this_AssignmentStatement_0= ruleAssignmentStatement ) | (this_ReturnStatement_1= ruleReturnStatement | this_IfStatement_2= ruleIfStatement | this_ClassicStatement_3= ruleClassicStatement | this_DefinitionStatement_4= ruleDefinitionStatement | this_Equation_5= ruleEquation ) )
            int alt5=2;
            alt5 = dfa5.predict(input);
            switch (alt5) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:355:2: ( ( ruleAssignmentStatement )=>this_AssignmentStatement_0= ruleAssignmentStatement )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:355:2: ( ( ruleAssignmentStatement )=>this_AssignmentStatement_0= ruleAssignmentStatement )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:355:3: ( ruleAssignmentStatement )=>this_AssignmentStatement_0= ruleAssignmentStatement
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getStatementAccess().getAssignmentStatementParserRuleCall_0()); 
                          
                    }
                    pushFollow(FOLLOW_ruleAssignmentStatement_in_ruleStatement824);
                    this_AssignmentStatement_0=ruleAssignmentStatement();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_AssignmentStatement_0; 
                              afterParserOrEnumRuleCall();
                          
                    }

                    }


                    }
                    break;
                case 2 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:365:6: (this_ReturnStatement_1= ruleReturnStatement | this_IfStatement_2= ruleIfStatement | this_ClassicStatement_3= ruleClassicStatement | this_DefinitionStatement_4= ruleDefinitionStatement | this_Equation_5= ruleEquation )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:365:6: (this_ReturnStatement_1= ruleReturnStatement | this_IfStatement_2= ruleIfStatement | this_ClassicStatement_3= ruleClassicStatement | this_DefinitionStatement_4= ruleDefinitionStatement | this_Equation_5= ruleEquation )
                    int alt4=5;
                    switch ( input.LA(1) ) {
                    case 44:
                        {
                        alt4=1;
                        }
                        break;
                    case 37:
                        {
                        alt4=2;
                        }
                        break;
                    case 16:
                    case 17:
                    case 18:
                    case 19:
                    case 20:
                    case 21:
                    case 22:
                    case 23:
                    case 24:
                    case 25:
                    case 26:
                    case 27:
                    case 28:
                    case 29:
                    case 30:
                    case 31:
                    case 32:
                    case 33:
                    case 34:
                        {
                        alt4=3;
                        }
                        break;
                    case RULE_ID:
                        {
                        int LA4_4 = input.LA(2);

                        if ( (LA4_4==80) ) {
                            alt4=5;
                        }
                        else if ( ((LA4_4>=RULE_ID && LA4_4<=RULE_STRING)||(LA4_4>=16 && LA4_4<=34)||LA4_4==36||LA4_4==41||LA4_4==45||(LA4_4>=52 && LA4_4<=54)||(LA4_4>=56 && LA4_4<=58)) ) {
                            alt4=4;
                        }
                        else {
                            if (state.backtracking>0) {state.failed=true; return current;}
                            NoViableAltException nvae =
                                new NoViableAltException("", 4, 4, input);

                            throw nvae;
                        }
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return current;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 4, 0, input);

                        throw nvae;
                    }

                    switch (alt4) {
                        case 1 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:366:5: this_ReturnStatement_1= ruleReturnStatement
                            {
                            if ( state.backtracking==0 ) {
                               
                                      newCompositeNode(grammarAccess.getStatementAccess().getReturnStatementParserRuleCall_1_0()); 
                                  
                            }
                            pushFollow(FOLLOW_ruleReturnStatement_in_ruleStatement853);
                            this_ReturnStatement_1=ruleReturnStatement();

                            state._fsp--;
                            if (state.failed) return current;
                            if ( state.backtracking==0 ) {
                               
                                      current = this_ReturnStatement_1; 
                                      afterParserOrEnumRuleCall();
                                  
                            }

                            }
                            break;
                        case 2 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:376:5: this_IfStatement_2= ruleIfStatement
                            {
                            if ( state.backtracking==0 ) {
                               
                                      newCompositeNode(grammarAccess.getStatementAccess().getIfStatementParserRuleCall_1_1()); 
                                  
                            }
                            pushFollow(FOLLOW_ruleIfStatement_in_ruleStatement880);
                            this_IfStatement_2=ruleIfStatement();

                            state._fsp--;
                            if (state.failed) return current;
                            if ( state.backtracking==0 ) {
                               
                                      current = this_IfStatement_2; 
                                      afterParserOrEnumRuleCall();
                                  
                            }

                            }
                            break;
                        case 3 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:386:5: this_ClassicStatement_3= ruleClassicStatement
                            {
                            if ( state.backtracking==0 ) {
                               
                                      newCompositeNode(grammarAccess.getStatementAccess().getClassicStatementParserRuleCall_1_2()); 
                                  
                            }
                            pushFollow(FOLLOW_ruleClassicStatement_in_ruleStatement907);
                            this_ClassicStatement_3=ruleClassicStatement();

                            state._fsp--;
                            if (state.failed) return current;
                            if ( state.backtracking==0 ) {
                               
                                      current = this_ClassicStatement_3; 
                                      afterParserOrEnumRuleCall();
                                  
                            }

                            }
                            break;
                        case 4 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:396:5: this_DefinitionStatement_4= ruleDefinitionStatement
                            {
                            if ( state.backtracking==0 ) {
                               
                                      newCompositeNode(grammarAccess.getStatementAccess().getDefinitionStatementParserRuleCall_1_3()); 
                                  
                            }
                            pushFollow(FOLLOW_ruleDefinitionStatement_in_ruleStatement934);
                            this_DefinitionStatement_4=ruleDefinitionStatement();

                            state._fsp--;
                            if (state.failed) return current;
                            if ( state.backtracking==0 ) {
                               
                                      current = this_DefinitionStatement_4; 
                                      afterParserOrEnumRuleCall();
                                  
                            }

                            }
                            break;
                        case 5 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:406:5: this_Equation_5= ruleEquation
                            {
                            if ( state.backtracking==0 ) {
                               
                                      newCompositeNode(grammarAccess.getStatementAccess().getEquationParserRuleCall_1_4()); 
                                  
                            }
                            pushFollow(FOLLOW_ruleEquation_in_ruleStatement961);
                            this_Equation_5=ruleEquation();

                            state._fsp--;
                            if (state.failed) return current;
                            if ( state.backtracking==0 ) {
                               
                                      current = this_Equation_5; 
                                      afterParserOrEnumRuleCall();
                                  
                            }

                            }
                            break;

                    }


                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleStatement"


    // $ANTLR start "entryRuleEquation"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:422:1: entryRuleEquation returns [EObject current=null] : iv_ruleEquation= ruleEquation EOF ;
    public final EObject entryRuleEquation() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleEquation = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:423:2: (iv_ruleEquation= ruleEquation EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:424:2: iv_ruleEquation= ruleEquation EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getEquationRule()); 
            }
            pushFollow(FOLLOW_ruleEquation_in_entryRuleEquation997);
            iv_ruleEquation=ruleEquation();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleEquation; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleEquation1007); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleEquation"


    // $ANTLR start "ruleEquation"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:431:1: ruleEquation returns [EObject current=null] : ( ( (lv_function_0_0= ruleFunction ) ) ( (lv_key_1_0= '=' ) ) ( (lv_expr_2_0= ruleExpression ) ) otherlv_3= ';' ) ;
    public final EObject ruleEquation() throws RecognitionException {
        EObject current = null;

        Token lv_key_1_0=null;
        Token otherlv_3=null;
        EObject lv_function_0_0 = null;

        EObject lv_expr_2_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:434:28: ( ( ( (lv_function_0_0= ruleFunction ) ) ( (lv_key_1_0= '=' ) ) ( (lv_expr_2_0= ruleExpression ) ) otherlv_3= ';' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:435:1: ( ( (lv_function_0_0= ruleFunction ) ) ( (lv_key_1_0= '=' ) ) ( (lv_expr_2_0= ruleExpression ) ) otherlv_3= ';' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:435:1: ( ( (lv_function_0_0= ruleFunction ) ) ( (lv_key_1_0= '=' ) ) ( (lv_expr_2_0= ruleExpression ) ) otherlv_3= ';' )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:435:2: ( (lv_function_0_0= ruleFunction ) ) ( (lv_key_1_0= '=' ) ) ( (lv_expr_2_0= ruleExpression ) ) otherlv_3= ';'
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:435:2: ( (lv_function_0_0= ruleFunction ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:436:1: (lv_function_0_0= ruleFunction )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:436:1: (lv_function_0_0= ruleFunction )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:437:3: lv_function_0_0= ruleFunction
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getEquationAccess().getFunctionFunctionParserRuleCall_0_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleFunction_in_ruleEquation1053);
            lv_function_0_0=ruleFunction();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getEquationRule());
              	        }
                     		set(
                     			current, 
                     			"function",
                      		lv_function_0_0, 
                      		"Function");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:453:2: ( (lv_key_1_0= '=' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:454:1: (lv_key_1_0= '=' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:454:1: (lv_key_1_0= '=' )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:455:3: lv_key_1_0= '='
            {
            lv_key_1_0=(Token)match(input,35,FOLLOW_35_in_ruleEquation1071); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                      newLeafNode(lv_key_1_0, grammarAccess.getEquationAccess().getKeyEqualsSignKeyword_1_0());
                  
            }
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElement(grammarAccess.getEquationRule());
              	        }
                     		setWithLastConsumed(current, "key", lv_key_1_0, "=");
              	    
            }

            }


            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:468:2: ( (lv_expr_2_0= ruleExpression ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:469:1: (lv_expr_2_0= ruleExpression )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:469:1: (lv_expr_2_0= ruleExpression )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:470:3: lv_expr_2_0= ruleExpression
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getEquationAccess().getExprExpressionParserRuleCall_2_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleExpression_in_ruleEquation1105);
            lv_expr_2_0=ruleExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getEquationRule());
              	        }
                     		set(
                     			current, 
                     			"expr",
                      		lv_expr_2_0, 
                      		"Expression");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            otherlv_3=(Token)match(input,36,FOLLOW_36_in_ruleEquation1117); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_3, grammarAccess.getEquationAccess().getSemicolonKeyword_3());
                  
            }

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleEquation"


    // $ANTLR start "entryRuleIfStatement"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:498:1: entryRuleIfStatement returns [EObject current=null] : iv_ruleIfStatement= ruleIfStatement EOF ;
    public final EObject entryRuleIfStatement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleIfStatement = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:499:2: (iv_ruleIfStatement= ruleIfStatement EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:500:2: iv_ruleIfStatement= ruleIfStatement EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getIfStatementRule()); 
            }
            pushFollow(FOLLOW_ruleIfStatement_in_entryRuleIfStatement1153);
            iv_ruleIfStatement=ruleIfStatement();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleIfStatement; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleIfStatement1163); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleIfStatement"


    // $ANTLR start "ruleIfStatement"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:507:1: ruleIfStatement returns [EObject current=null] : ( ( (lv_key_0_0= 'if' ) ) (otherlv_1= 'condition:' )? ( (lv_expr_2_0= ruleExpression ) ) ( (lv_block_3_0= ruleBlock ) ) ( ( ( 'else' )=>otherlv_4= 'else' ) ( ( (lv_else_5_1= ruleIfStatement | lv_else_5_2= ruleBlock ) ) ) )? ) ;
    public final EObject ruleIfStatement() throws RecognitionException {
        EObject current = null;

        Token lv_key_0_0=null;
        Token otherlv_1=null;
        Token otherlv_4=null;
        EObject lv_expr_2_0 = null;

        EObject lv_block_3_0 = null;

        EObject lv_else_5_1 = null;

        EObject lv_else_5_2 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:510:28: ( ( ( (lv_key_0_0= 'if' ) ) (otherlv_1= 'condition:' )? ( (lv_expr_2_0= ruleExpression ) ) ( (lv_block_3_0= ruleBlock ) ) ( ( ( 'else' )=>otherlv_4= 'else' ) ( ( (lv_else_5_1= ruleIfStatement | lv_else_5_2= ruleBlock ) ) ) )? ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:511:1: ( ( (lv_key_0_0= 'if' ) ) (otherlv_1= 'condition:' )? ( (lv_expr_2_0= ruleExpression ) ) ( (lv_block_3_0= ruleBlock ) ) ( ( ( 'else' )=>otherlv_4= 'else' ) ( ( (lv_else_5_1= ruleIfStatement | lv_else_5_2= ruleBlock ) ) ) )? )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:511:1: ( ( (lv_key_0_0= 'if' ) ) (otherlv_1= 'condition:' )? ( (lv_expr_2_0= ruleExpression ) ) ( (lv_block_3_0= ruleBlock ) ) ( ( ( 'else' )=>otherlv_4= 'else' ) ( ( (lv_else_5_1= ruleIfStatement | lv_else_5_2= ruleBlock ) ) ) )? )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:511:2: ( (lv_key_0_0= 'if' ) ) (otherlv_1= 'condition:' )? ( (lv_expr_2_0= ruleExpression ) ) ( (lv_block_3_0= ruleBlock ) ) ( ( ( 'else' )=>otherlv_4= 'else' ) ( ( (lv_else_5_1= ruleIfStatement | lv_else_5_2= ruleBlock ) ) ) )?
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:511:2: ( (lv_key_0_0= 'if' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:512:1: (lv_key_0_0= 'if' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:512:1: (lv_key_0_0= 'if' )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:513:3: lv_key_0_0= 'if'
            {
            lv_key_0_0=(Token)match(input,37,FOLLOW_37_in_ruleIfStatement1206); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                      newLeafNode(lv_key_0_0, grammarAccess.getIfStatementAccess().getKeyIfKeyword_0_0());
                  
            }
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElement(grammarAccess.getIfStatementRule());
              	        }
                     		setWithLastConsumed(current, "key", lv_key_0_0, "if");
              	    
            }

            }


            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:526:2: (otherlv_1= 'condition:' )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==38) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:526:4: otherlv_1= 'condition:'
                    {
                    otherlv_1=(Token)match(input,38,FOLLOW_38_in_ruleIfStatement1232); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_1, grammarAccess.getIfStatementAccess().getConditionKeyword_1());
                          
                    }

                    }
                    break;

            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:530:3: ( (lv_expr_2_0= ruleExpression ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:531:1: (lv_expr_2_0= ruleExpression )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:531:1: (lv_expr_2_0= ruleExpression )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:532:3: lv_expr_2_0= ruleExpression
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getIfStatementAccess().getExprExpressionParserRuleCall_2_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleExpression_in_ruleIfStatement1255);
            lv_expr_2_0=ruleExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getIfStatementRule());
              	        }
                     		set(
                     			current, 
                     			"expr",
                      		lv_expr_2_0, 
                      		"Expression");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:548:2: ( (lv_block_3_0= ruleBlock ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:549:1: (lv_block_3_0= ruleBlock )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:549:1: (lv_block_3_0= ruleBlock )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:550:3: lv_block_3_0= ruleBlock
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getIfStatementAccess().getBlockBlockParserRuleCall_3_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleBlock_in_ruleIfStatement1276);
            lv_block_3_0=ruleBlock();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getIfStatementRule());
              	        }
                     		set(
                     			current, 
                     			"block",
                      		lv_block_3_0, 
                      		"Block");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:566:2: ( ( ( 'else' )=>otherlv_4= 'else' ) ( ( (lv_else_5_1= ruleIfStatement | lv_else_5_2= ruleBlock ) ) ) )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==39) && (synpred2_InternalGaml())) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:566:3: ( ( 'else' )=>otherlv_4= 'else' ) ( ( (lv_else_5_1= ruleIfStatement | lv_else_5_2= ruleBlock ) ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:566:3: ( ( 'else' )=>otherlv_4= 'else' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:566:4: ( 'else' )=>otherlv_4= 'else'
                    {
                    otherlv_4=(Token)match(input,39,FOLLOW_39_in_ruleIfStatement1297); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_4, grammarAccess.getIfStatementAccess().getElseKeyword_4_0());
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:571:2: ( ( (lv_else_5_1= ruleIfStatement | lv_else_5_2= ruleBlock ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:572:1: ( (lv_else_5_1= ruleIfStatement | lv_else_5_2= ruleBlock ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:572:1: ( (lv_else_5_1= ruleIfStatement | lv_else_5_2= ruleBlock ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:573:1: (lv_else_5_1= ruleIfStatement | lv_else_5_2= ruleBlock )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:573:1: (lv_else_5_1= ruleIfStatement | lv_else_5_2= ruleBlock )
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0==37) ) {
                        alt7=1;
                    }
                    else if ( (LA7_0==54) ) {
                        alt7=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return current;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 7, 0, input);

                        throw nvae;
                    }
                    switch (alt7) {
                        case 1 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:574:3: lv_else_5_1= ruleIfStatement
                            {
                            if ( state.backtracking==0 ) {
                               
                              	        newCompositeNode(grammarAccess.getIfStatementAccess().getElseIfStatementParserRuleCall_4_1_0_0()); 
                              	    
                            }
                            pushFollow(FOLLOW_ruleIfStatement_in_ruleIfStatement1321);
                            lv_else_5_1=ruleIfStatement();

                            state._fsp--;
                            if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElementForParent(grammarAccess.getIfStatementRule());
                              	        }
                                     		set(
                                     			current, 
                                     			"else",
                                      		lv_else_5_1, 
                                      		"IfStatement");
                              	        afterParserOrEnumRuleCall();
                              	    
                            }

                            }
                            break;
                        case 2 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:589:8: lv_else_5_2= ruleBlock
                            {
                            if ( state.backtracking==0 ) {
                               
                              	        newCompositeNode(grammarAccess.getIfStatementAccess().getElseBlockParserRuleCall_4_1_0_1()); 
                              	    
                            }
                            pushFollow(FOLLOW_ruleBlock_in_ruleIfStatement1340);
                            lv_else_5_2=ruleBlock();

                            state._fsp--;
                            if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElementForParent(grammarAccess.getIfStatementRule());
                              	        }
                                     		set(
                                     			current, 
                                     			"else",
                                      		lv_else_5_2, 
                                      		"Block");
                              	        afterParserOrEnumRuleCall();
                              	    
                            }

                            }
                            break;

                    }


                    }


                    }


                    }
                    break;

            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleIfStatement"


    // $ANTLR start "entryRuleClassicStatement"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:615:1: entryRuleClassicStatement returns [EObject current=null] : iv_ruleClassicStatement= ruleClassicStatement EOF ;
    public final EObject entryRuleClassicStatement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleClassicStatement = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:616:2: (iv_ruleClassicStatement= ruleClassicStatement EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:617:2: iv_ruleClassicStatement= ruleClassicStatement EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getClassicStatementRule()); 
            }
            pushFollow(FOLLOW_ruleClassicStatement_in_entryRuleClassicStatement1381);
            iv_ruleClassicStatement=ruleClassicStatement();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleClassicStatement; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleClassicStatement1391); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleClassicStatement"


    // $ANTLR start "ruleClassicStatement"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:624:1: ruleClassicStatement returns [EObject current=null] : ( ( (lv_key_0_0= ruleBuiltInStatementKey ) ) (this_ID_1= RULE_ID otherlv_2= ':' )? ( (lv_expr_3_0= ruleExpression ) ) ( (lv_facets_4_0= ruleFacet ) )* ( ( (lv_block_5_0= ruleBlock ) ) | otherlv_6= ';' ) ) ;
    public final EObject ruleClassicStatement() throws RecognitionException {
        EObject current = null;

        Token this_ID_1=null;
        Token otherlv_2=null;
        Token otherlv_6=null;
        AntlrDatatypeRuleToken lv_key_0_0 = null;

        EObject lv_expr_3_0 = null;

        EObject lv_facets_4_0 = null;

        EObject lv_block_5_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:627:28: ( ( ( (lv_key_0_0= ruleBuiltInStatementKey ) ) (this_ID_1= RULE_ID otherlv_2= ':' )? ( (lv_expr_3_0= ruleExpression ) ) ( (lv_facets_4_0= ruleFacet ) )* ( ( (lv_block_5_0= ruleBlock ) ) | otherlv_6= ';' ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:628:1: ( ( (lv_key_0_0= ruleBuiltInStatementKey ) ) (this_ID_1= RULE_ID otherlv_2= ':' )? ( (lv_expr_3_0= ruleExpression ) ) ( (lv_facets_4_0= ruleFacet ) )* ( ( (lv_block_5_0= ruleBlock ) ) | otherlv_6= ';' ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:628:1: ( ( (lv_key_0_0= ruleBuiltInStatementKey ) ) (this_ID_1= RULE_ID otherlv_2= ':' )? ( (lv_expr_3_0= ruleExpression ) ) ( (lv_facets_4_0= ruleFacet ) )* ( ( (lv_block_5_0= ruleBlock ) ) | otherlv_6= ';' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:628:2: ( (lv_key_0_0= ruleBuiltInStatementKey ) ) (this_ID_1= RULE_ID otherlv_2= ':' )? ( (lv_expr_3_0= ruleExpression ) ) ( (lv_facets_4_0= ruleFacet ) )* ( ( (lv_block_5_0= ruleBlock ) ) | otherlv_6= ';' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:628:2: ( (lv_key_0_0= ruleBuiltInStatementKey ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:629:1: (lv_key_0_0= ruleBuiltInStatementKey )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:629:1: (lv_key_0_0= ruleBuiltInStatementKey )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:630:3: lv_key_0_0= ruleBuiltInStatementKey
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getClassicStatementAccess().getKeyBuiltInStatementKeyParserRuleCall_0_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleBuiltInStatementKey_in_ruleClassicStatement1437);
            lv_key_0_0=ruleBuiltInStatementKey();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getClassicStatementRule());
              	        }
                     		set(
                     			current, 
                     			"key",
                      		lv_key_0_0, 
                      		"BuiltInStatementKey");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:646:2: (this_ID_1= RULE_ID otherlv_2= ':' )?
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==RULE_ID) ) {
                int LA9_1 = input.LA(2);

                if ( (LA9_1==40) ) {
                    alt9=1;
                }
            }
            switch (alt9) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:646:3: this_ID_1= RULE_ID otherlv_2= ':'
                    {
                    this_ID_1=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleClassicStatement1449); if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                          newLeafNode(this_ID_1, grammarAccess.getClassicStatementAccess().getIDTerminalRuleCall_1_0()); 
                          
                    }
                    otherlv_2=(Token)match(input,40,FOLLOW_40_in_ruleClassicStatement1460); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_2, grammarAccess.getClassicStatementAccess().getColonKeyword_1_1());
                          
                    }

                    }
                    break;

            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:654:3: ( (lv_expr_3_0= ruleExpression ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:655:1: (lv_expr_3_0= ruleExpression )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:655:1: (lv_expr_3_0= ruleExpression )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:656:3: lv_expr_3_0= ruleExpression
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getClassicStatementAccess().getExprExpressionParserRuleCall_2_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleExpression_in_ruleClassicStatement1483);
            lv_expr_3_0=ruleExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getClassicStatementRule());
              	        }
                     		set(
                     			current, 
                     			"expr",
                      		lv_expr_3_0, 
                      		"Expression");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:672:2: ( (lv_facets_4_0= ruleFacet ) )*
            loop10:
            do {
                int alt10=2;
                int LA10_0 = input.LA(1);

                if ( (LA10_0==RULE_ID||LA10_0==45||(LA10_0>=52 && LA10_0<=53)||(LA10_0>=56 && LA10_0<=58)) ) {
                    alt10=1;
                }


                switch (alt10) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:673:1: (lv_facets_4_0= ruleFacet )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:673:1: (lv_facets_4_0= ruleFacet )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:674:3: lv_facets_4_0= ruleFacet
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getClassicStatementAccess().getFacetsFacetParserRuleCall_3_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleFacet_in_ruleClassicStatement1504);
            	    lv_facets_4_0=ruleFacet();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getClassicStatementRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"facets",
            	              		lv_facets_4_0, 
            	              		"Facet");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:690:3: ( ( (lv_block_5_0= ruleBlock ) ) | otherlv_6= ';' )
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==54) ) {
                alt11=1;
            }
            else if ( (LA11_0==36) ) {
                alt11=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }
            switch (alt11) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:690:4: ( (lv_block_5_0= ruleBlock ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:690:4: ( (lv_block_5_0= ruleBlock ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:691:1: (lv_block_5_0= ruleBlock )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:691:1: (lv_block_5_0= ruleBlock )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:692:3: lv_block_5_0= ruleBlock
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getClassicStatementAccess().getBlockBlockParserRuleCall_4_0_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleBlock_in_ruleClassicStatement1527);
                    lv_block_5_0=ruleBlock();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getClassicStatementRule());
                      	        }
                             		set(
                             			current, 
                             			"block",
                              		lv_block_5_0, 
                              		"Block");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }
                    break;
                case 2 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:709:7: otherlv_6= ';'
                    {
                    otherlv_6=(Token)match(input,36,FOLLOW_36_in_ruleClassicStatement1545); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_6, grammarAccess.getClassicStatementAccess().getSemicolonKeyword_4_1());
                          
                    }

                    }
                    break;

            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleClassicStatement"


    // $ANTLR start "entryRuleDefinitionStatement"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:721:1: entryRuleDefinitionStatement returns [EObject current=null] : iv_ruleDefinitionStatement= ruleDefinitionStatement EOF ;
    public final EObject entryRuleDefinitionStatement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDefinitionStatement = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:722:2: (iv_ruleDefinitionStatement= ruleDefinitionStatement EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:723:2: iv_ruleDefinitionStatement= ruleDefinitionStatement EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getDefinitionStatementRule()); 
            }
            pushFollow(FOLLOW_ruleDefinitionStatement_in_entryRuleDefinitionStatement1582);
            iv_ruleDefinitionStatement=ruleDefinitionStatement();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleDefinitionStatement; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleDefinitionStatement1592); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleDefinitionStatement"


    // $ANTLR start "ruleDefinitionStatement"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:730:1: ruleDefinitionStatement returns [EObject current=null] : ( ( (lv_key_0_0= RULE_ID ) ) ( (lv_of_1_0= ruleContents ) )? ( ( (lv_name_2_1= RULE_ID | lv_name_2_2= RULE_STRING | lv_name_2_3= ruleBuiltInStatementKey ) ) )? ( (lv_facets_3_0= ruleFacet ) )* ( ( (lv_block_4_0= ruleBlock ) ) | otherlv_5= ';' ) ) ;
    public final EObject ruleDefinitionStatement() throws RecognitionException {
        EObject current = null;

        Token lv_key_0_0=null;
        Token lv_name_2_1=null;
        Token lv_name_2_2=null;
        Token otherlv_5=null;
        EObject lv_of_1_0 = null;

        AntlrDatatypeRuleToken lv_name_2_3 = null;

        EObject lv_facets_3_0 = null;

        EObject lv_block_4_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:733:28: ( ( ( (lv_key_0_0= RULE_ID ) ) ( (lv_of_1_0= ruleContents ) )? ( ( (lv_name_2_1= RULE_ID | lv_name_2_2= RULE_STRING | lv_name_2_3= ruleBuiltInStatementKey ) ) )? ( (lv_facets_3_0= ruleFacet ) )* ( ( (lv_block_4_0= ruleBlock ) ) | otherlv_5= ';' ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:734:1: ( ( (lv_key_0_0= RULE_ID ) ) ( (lv_of_1_0= ruleContents ) )? ( ( (lv_name_2_1= RULE_ID | lv_name_2_2= RULE_STRING | lv_name_2_3= ruleBuiltInStatementKey ) ) )? ( (lv_facets_3_0= ruleFacet ) )* ( ( (lv_block_4_0= ruleBlock ) ) | otherlv_5= ';' ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:734:1: ( ( (lv_key_0_0= RULE_ID ) ) ( (lv_of_1_0= ruleContents ) )? ( ( (lv_name_2_1= RULE_ID | lv_name_2_2= RULE_STRING | lv_name_2_3= ruleBuiltInStatementKey ) ) )? ( (lv_facets_3_0= ruleFacet ) )* ( ( (lv_block_4_0= ruleBlock ) ) | otherlv_5= ';' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:734:2: ( (lv_key_0_0= RULE_ID ) ) ( (lv_of_1_0= ruleContents ) )? ( ( (lv_name_2_1= RULE_ID | lv_name_2_2= RULE_STRING | lv_name_2_3= ruleBuiltInStatementKey ) ) )? ( (lv_facets_3_0= ruleFacet ) )* ( ( (lv_block_4_0= ruleBlock ) ) | otherlv_5= ';' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:734:2: ( (lv_key_0_0= RULE_ID ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:735:1: (lv_key_0_0= RULE_ID )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:735:1: (lv_key_0_0= RULE_ID )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:736:3: lv_key_0_0= RULE_ID
            {
            lv_key_0_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleDefinitionStatement1634); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(lv_key_0_0, grammarAccess.getDefinitionStatementAccess().getKeyIDTerminalRuleCall_0_0()); 
              		
            }
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElement(grammarAccess.getDefinitionStatementRule());
              	        }
                     		setWithLastConsumed(
                     			current, 
                     			"key",
                      		lv_key_0_0, 
                      		"ID");
              	    
            }

            }


            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:752:2: ( (lv_of_1_0= ruleContents ) )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==41) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:753:1: (lv_of_1_0= ruleContents )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:753:1: (lv_of_1_0= ruleContents )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:754:3: lv_of_1_0= ruleContents
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getDefinitionStatementAccess().getOfContentsParserRuleCall_1_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleContents_in_ruleDefinitionStatement1660);
                    lv_of_1_0=ruleContents();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getDefinitionStatementRule());
                      	        }
                             		set(
                             			current, 
                             			"of",
                              		lv_of_1_0, 
                              		"Contents");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }
                    break;

            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:770:3: ( ( (lv_name_2_1= RULE_ID | lv_name_2_2= RULE_STRING | lv_name_2_3= ruleBuiltInStatementKey ) ) )?
            int alt14=2;
            int LA14_0 = input.LA(1);

            if ( (LA14_0==RULE_ID) ) {
                int LA14_1 = input.LA(2);

                if ( (LA14_1==RULE_ID||LA14_1==36||LA14_1==45||(LA14_1>=52 && LA14_1<=54)||(LA14_1>=56 && LA14_1<=58)) ) {
                    alt14=1;
                }
            }
            else if ( (LA14_0==RULE_STRING||(LA14_0>=16 && LA14_0<=34)) ) {
                alt14=1;
            }
            switch (alt14) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:771:1: ( (lv_name_2_1= RULE_ID | lv_name_2_2= RULE_STRING | lv_name_2_3= ruleBuiltInStatementKey ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:771:1: ( (lv_name_2_1= RULE_ID | lv_name_2_2= RULE_STRING | lv_name_2_3= ruleBuiltInStatementKey ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:772:1: (lv_name_2_1= RULE_ID | lv_name_2_2= RULE_STRING | lv_name_2_3= ruleBuiltInStatementKey )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:772:1: (lv_name_2_1= RULE_ID | lv_name_2_2= RULE_STRING | lv_name_2_3= ruleBuiltInStatementKey )
                    int alt13=3;
                    switch ( input.LA(1) ) {
                    case RULE_ID:
                        {
                        alt13=1;
                        }
                        break;
                    case RULE_STRING:
                        {
                        alt13=2;
                        }
                        break;
                    case 16:
                    case 17:
                    case 18:
                    case 19:
                    case 20:
                    case 21:
                    case 22:
                    case 23:
                    case 24:
                    case 25:
                    case 26:
                    case 27:
                    case 28:
                    case 29:
                    case 30:
                    case 31:
                    case 32:
                    case 33:
                    case 34:
                        {
                        alt13=3;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return current;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 13, 0, input);

                        throw nvae;
                    }

                    switch (alt13) {
                        case 1 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:773:3: lv_name_2_1= RULE_ID
                            {
                            lv_name_2_1=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleDefinitionStatement1680); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              			newLeafNode(lv_name_2_1, grammarAccess.getDefinitionStatementAccess().getNameIDTerminalRuleCall_2_0_0()); 
                              		
                            }
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElement(grammarAccess.getDefinitionStatementRule());
                              	        }
                                     		setWithLastConsumed(
                                     			current, 
                                     			"name",
                                      		lv_name_2_1, 
                                      		"ID");
                              	    
                            }

                            }
                            break;
                        case 2 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:788:8: lv_name_2_2= RULE_STRING
                            {
                            lv_name_2_2=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleDefinitionStatement1700); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              			newLeafNode(lv_name_2_2, grammarAccess.getDefinitionStatementAccess().getNameSTRINGTerminalRuleCall_2_0_1()); 
                              		
                            }
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElement(grammarAccess.getDefinitionStatementRule());
                              	        }
                                     		setWithLastConsumed(
                                     			current, 
                                     			"name",
                                      		lv_name_2_2, 
                                      		"STRING");
                              	    
                            }

                            }
                            break;
                        case 3 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:803:8: lv_name_2_3= ruleBuiltInStatementKey
                            {
                            if ( state.backtracking==0 ) {
                               
                              	        newCompositeNode(grammarAccess.getDefinitionStatementAccess().getNameBuiltInStatementKeyParserRuleCall_2_0_2()); 
                              	    
                            }
                            pushFollow(FOLLOW_ruleBuiltInStatementKey_in_ruleDefinitionStatement1724);
                            lv_name_2_3=ruleBuiltInStatementKey();

                            state._fsp--;
                            if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElementForParent(grammarAccess.getDefinitionStatementRule());
                              	        }
                                     		set(
                                     			current, 
                                     			"name",
                                      		lv_name_2_3, 
                                      		"BuiltInStatementKey");
                              	        afterParserOrEnumRuleCall();
                              	    
                            }

                            }
                            break;

                    }


                    }


                    }
                    break;

            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:821:3: ( (lv_facets_3_0= ruleFacet ) )*
            loop15:
            do {
                int alt15=2;
                int LA15_0 = input.LA(1);

                if ( (LA15_0==RULE_ID||LA15_0==45||(LA15_0>=52 && LA15_0<=53)||(LA15_0>=56 && LA15_0<=58)) ) {
                    alt15=1;
                }


                switch (alt15) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:822:1: (lv_facets_3_0= ruleFacet )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:822:1: (lv_facets_3_0= ruleFacet )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:823:3: lv_facets_3_0= ruleFacet
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getDefinitionStatementAccess().getFacetsFacetParserRuleCall_3_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleFacet_in_ruleDefinitionStatement1749);
            	    lv_facets_3_0=ruleFacet();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getDefinitionStatementRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"facets",
            	              		lv_facets_3_0, 
            	              		"Facet");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop15;
                }
            } while (true);

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:839:3: ( ( (lv_block_4_0= ruleBlock ) ) | otherlv_5= ';' )
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==54) ) {
                alt16=1;
            }
            else if ( (LA16_0==36) ) {
                alt16=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 16, 0, input);

                throw nvae;
            }
            switch (alt16) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:839:4: ( (lv_block_4_0= ruleBlock ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:839:4: ( (lv_block_4_0= ruleBlock ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:840:1: (lv_block_4_0= ruleBlock )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:840:1: (lv_block_4_0= ruleBlock )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:841:3: lv_block_4_0= ruleBlock
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getDefinitionStatementAccess().getBlockBlockParserRuleCall_4_0_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleBlock_in_ruleDefinitionStatement1772);
                    lv_block_4_0=ruleBlock();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getDefinitionStatementRule());
                      	        }
                             		set(
                             			current, 
                             			"block",
                              		lv_block_4_0, 
                              		"Block");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }
                    break;
                case 2 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:858:7: otherlv_5= ';'
                    {
                    otherlv_5=(Token)match(input,36,FOLLOW_36_in_ruleDefinitionStatement1790); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_5, grammarAccess.getDefinitionStatementAccess().getSemicolonKeyword_4_1());
                          
                    }

                    }
                    break;

            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleDefinitionStatement"


    // $ANTLR start "entryRuleContents"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:870:1: entryRuleContents returns [EObject current=null] : iv_ruleContents= ruleContents EOF ;
    public final EObject entryRuleContents() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleContents = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:871:2: (iv_ruleContents= ruleContents EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:872:2: iv_ruleContents= ruleContents EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getContentsRule()); 
            }
            pushFollow(FOLLOW_ruleContents_in_entryRuleContents1827);
            iv_ruleContents=ruleContents();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleContents; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleContents1837); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleContents"


    // $ANTLR start "ruleContents"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:879:1: ruleContents returns [EObject current=null] : (otherlv_0= '<' ( (lv_type_1_0= RULE_ID ) ) (otherlv_2= ',' ( (lv_type2_3_0= RULE_ID ) ) )? otherlv_4= '>' ) ;
    public final EObject ruleContents() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_type_1_0=null;
        Token otherlv_2=null;
        Token lv_type2_3_0=null;
        Token otherlv_4=null;

         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:882:28: ( (otherlv_0= '<' ( (lv_type_1_0= RULE_ID ) ) (otherlv_2= ',' ( (lv_type2_3_0= RULE_ID ) ) )? otherlv_4= '>' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:883:1: (otherlv_0= '<' ( (lv_type_1_0= RULE_ID ) ) (otherlv_2= ',' ( (lv_type2_3_0= RULE_ID ) ) )? otherlv_4= '>' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:883:1: (otherlv_0= '<' ( (lv_type_1_0= RULE_ID ) ) (otherlv_2= ',' ( (lv_type2_3_0= RULE_ID ) ) )? otherlv_4= '>' )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:883:3: otherlv_0= '<' ( (lv_type_1_0= RULE_ID ) ) (otherlv_2= ',' ( (lv_type2_3_0= RULE_ID ) ) )? otherlv_4= '>'
            {
            otherlv_0=(Token)match(input,41,FOLLOW_41_in_ruleContents1874); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_0, grammarAccess.getContentsAccess().getLessThanSignKeyword_0());
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:887:1: ( (lv_type_1_0= RULE_ID ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:888:1: (lv_type_1_0= RULE_ID )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:888:1: (lv_type_1_0= RULE_ID )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:889:3: lv_type_1_0= RULE_ID
            {
            lv_type_1_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleContents1891); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(lv_type_1_0, grammarAccess.getContentsAccess().getTypeIDTerminalRuleCall_1_0()); 
              		
            }
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElement(grammarAccess.getContentsRule());
              	        }
                     		setWithLastConsumed(
                     			current, 
                     			"type",
                      		lv_type_1_0, 
                      		"ID");
              	    
            }

            }


            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:905:2: (otherlv_2= ',' ( (lv_type2_3_0= RULE_ID ) ) )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==42) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:905:4: otherlv_2= ',' ( (lv_type2_3_0= RULE_ID ) )
                    {
                    otherlv_2=(Token)match(input,42,FOLLOW_42_in_ruleContents1909); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_2, grammarAccess.getContentsAccess().getCommaKeyword_2_0());
                          
                    }
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:909:1: ( (lv_type2_3_0= RULE_ID ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:910:1: (lv_type2_3_0= RULE_ID )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:910:1: (lv_type2_3_0= RULE_ID )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:911:3: lv_type2_3_0= RULE_ID
                    {
                    lv_type2_3_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleContents1926); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			newLeafNode(lv_type2_3_0, grammarAccess.getContentsAccess().getType2IDTerminalRuleCall_2_1_0()); 
                      		
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getContentsRule());
                      	        }
                             		setWithLastConsumed(
                             			current, 
                             			"type2",
                              		lv_type2_3_0, 
                              		"ID");
                      	    
                    }

                    }


                    }


                    }
                    break;

            }

            otherlv_4=(Token)match(input,43,FOLLOW_43_in_ruleContents1945); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_4, grammarAccess.getContentsAccess().getGreaterThanSignKeyword_3());
                  
            }

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleContents"


    // $ANTLR start "entryRuleReturnStatement"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:939:1: entryRuleReturnStatement returns [EObject current=null] : iv_ruleReturnStatement= ruleReturnStatement EOF ;
    public final EObject entryRuleReturnStatement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleReturnStatement = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:940:2: (iv_ruleReturnStatement= ruleReturnStatement EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:941:2: iv_ruleReturnStatement= ruleReturnStatement EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getReturnStatementRule()); 
            }
            pushFollow(FOLLOW_ruleReturnStatement_in_entryRuleReturnStatement1981);
            iv_ruleReturnStatement=ruleReturnStatement();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleReturnStatement; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleReturnStatement1991); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleReturnStatement"


    // $ANTLR start "ruleReturnStatement"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:948:1: ruleReturnStatement returns [EObject current=null] : ( ( (lv_key_0_0= 'return' ) ) ( (lv_expr_1_0= ruleExpression ) )? otherlv_2= ';' ) ;
    public final EObject ruleReturnStatement() throws RecognitionException {
        EObject current = null;

        Token lv_key_0_0=null;
        Token otherlv_2=null;
        EObject lv_expr_1_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:951:28: ( ( ( (lv_key_0_0= 'return' ) ) ( (lv_expr_1_0= ruleExpression ) )? otherlv_2= ';' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:952:1: ( ( (lv_key_0_0= 'return' ) ) ( (lv_expr_1_0= ruleExpression ) )? otherlv_2= ';' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:952:1: ( ( (lv_key_0_0= 'return' ) ) ( (lv_expr_1_0= ruleExpression ) )? otherlv_2= ';' )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:952:2: ( (lv_key_0_0= 'return' ) ) ( (lv_expr_1_0= ruleExpression ) )? otherlv_2= ';'
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:952:2: ( (lv_key_0_0= 'return' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:953:1: (lv_key_0_0= 'return' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:953:1: (lv_key_0_0= 'return' )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:954:3: lv_key_0_0= 'return'
            {
            lv_key_0_0=(Token)match(input,44,FOLLOW_44_in_ruleReturnStatement2034); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                      newLeafNode(lv_key_0_0, grammarAccess.getReturnStatementAccess().getKeyReturnKeyword_0_0());
                  
            }
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElement(grammarAccess.getReturnStatementRule());
              	        }
                     		setWithLastConsumed(current, "key", lv_key_0_0, "return");
              	    
            }

            }


            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:967:2: ( (lv_expr_1_0= ruleExpression ) )?
            int alt18=2;
            int LA18_0 = input.LA(1);

            if ( ((LA18_0>=RULE_ID && LA18_0<=RULE_BOOLEAN)||(LA18_0>=16 && LA18_0<=34)||LA18_0==54||(LA18_0>=56 && LA18_0<=58)||LA18_0==67||(LA18_0>=72 && LA18_0<=77)||LA18_0==80) ) {
                alt18=1;
            }
            switch (alt18) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:968:1: (lv_expr_1_0= ruleExpression )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:968:1: (lv_expr_1_0= ruleExpression )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:969:3: lv_expr_1_0= ruleExpression
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getReturnStatementAccess().getExprExpressionParserRuleCall_1_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleExpression_in_ruleReturnStatement2068);
                    lv_expr_1_0=ruleExpression();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getReturnStatementRule());
                      	        }
                             		set(
                             			current, 
                             			"expr",
                              		lv_expr_1_0, 
                              		"Expression");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }
                    break;

            }

            otherlv_2=(Token)match(input,36,FOLLOW_36_in_ruleReturnStatement2081); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_2, grammarAccess.getReturnStatementAccess().getSemicolonKeyword_2());
                  
            }

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleReturnStatement"


    // $ANTLR start "entryRuleAssignmentStatement"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:997:1: entryRuleAssignmentStatement returns [EObject current=null] : iv_ruleAssignmentStatement= ruleAssignmentStatement EOF ;
    public final EObject entryRuleAssignmentStatement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAssignmentStatement = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:998:2: (iv_ruleAssignmentStatement= ruleAssignmentStatement EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:999:2: iv_ruleAssignmentStatement= ruleAssignmentStatement EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAssignmentStatementRule()); 
            }
            pushFollow(FOLLOW_ruleAssignmentStatement_in_entryRuleAssignmentStatement2117);
            iv_ruleAssignmentStatement=ruleAssignmentStatement();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAssignmentStatement; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleAssignmentStatement2127); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAssignmentStatement"


    // $ANTLR start "ruleAssignmentStatement"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1006:1: ruleAssignmentStatement returns [EObject current=null] : ( ( (lv_expr_0_0= ruleExpression ) ) ( ( (lv_key_1_1= '<-' | lv_key_1_2= '<<' | lv_key_1_3= '>>' | lv_key_1_4= '+=' | lv_key_1_5= '-=' | lv_key_1_6= '++' | lv_key_1_7= '--' ) ) ) ( (lv_value_2_0= ruleExpression ) ) ( (lv_facets_3_0= ruleFacet ) )* otherlv_4= ';' ) ;
    public final EObject ruleAssignmentStatement() throws RecognitionException {
        EObject current = null;

        Token lv_key_1_1=null;
        Token lv_key_1_2=null;
        Token lv_key_1_3=null;
        Token lv_key_1_4=null;
        Token lv_key_1_5=null;
        Token lv_key_1_6=null;
        Token lv_key_1_7=null;
        Token otherlv_4=null;
        EObject lv_expr_0_0 = null;

        EObject lv_value_2_0 = null;

        EObject lv_facets_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1009:28: ( ( ( (lv_expr_0_0= ruleExpression ) ) ( ( (lv_key_1_1= '<-' | lv_key_1_2= '<<' | lv_key_1_3= '>>' | lv_key_1_4= '+=' | lv_key_1_5= '-=' | lv_key_1_6= '++' | lv_key_1_7= '--' ) ) ) ( (lv_value_2_0= ruleExpression ) ) ( (lv_facets_3_0= ruleFacet ) )* otherlv_4= ';' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1010:1: ( ( (lv_expr_0_0= ruleExpression ) ) ( ( (lv_key_1_1= '<-' | lv_key_1_2= '<<' | lv_key_1_3= '>>' | lv_key_1_4= '+=' | lv_key_1_5= '-=' | lv_key_1_6= '++' | lv_key_1_7= '--' ) ) ) ( (lv_value_2_0= ruleExpression ) ) ( (lv_facets_3_0= ruleFacet ) )* otherlv_4= ';' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1010:1: ( ( (lv_expr_0_0= ruleExpression ) ) ( ( (lv_key_1_1= '<-' | lv_key_1_2= '<<' | lv_key_1_3= '>>' | lv_key_1_4= '+=' | lv_key_1_5= '-=' | lv_key_1_6= '++' | lv_key_1_7= '--' ) ) ) ( (lv_value_2_0= ruleExpression ) ) ( (lv_facets_3_0= ruleFacet ) )* otherlv_4= ';' )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1010:2: ( (lv_expr_0_0= ruleExpression ) ) ( ( (lv_key_1_1= '<-' | lv_key_1_2= '<<' | lv_key_1_3= '>>' | lv_key_1_4= '+=' | lv_key_1_5= '-=' | lv_key_1_6= '++' | lv_key_1_7= '--' ) ) ) ( (lv_value_2_0= ruleExpression ) ) ( (lv_facets_3_0= ruleFacet ) )* otherlv_4= ';'
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1010:2: ( (lv_expr_0_0= ruleExpression ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1011:1: (lv_expr_0_0= ruleExpression )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1011:1: (lv_expr_0_0= ruleExpression )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1012:3: lv_expr_0_0= ruleExpression
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getAssignmentStatementAccess().getExprExpressionParserRuleCall_0_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleExpression_in_ruleAssignmentStatement2173);
            lv_expr_0_0=ruleExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getAssignmentStatementRule());
              	        }
                     		set(
                     			current, 
                     			"expr",
                      		lv_expr_0_0, 
                      		"Expression");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1028:2: ( ( (lv_key_1_1= '<-' | lv_key_1_2= '<<' | lv_key_1_3= '>>' | lv_key_1_4= '+=' | lv_key_1_5= '-=' | lv_key_1_6= '++' | lv_key_1_7= '--' ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1029:1: ( (lv_key_1_1= '<-' | lv_key_1_2= '<<' | lv_key_1_3= '>>' | lv_key_1_4= '+=' | lv_key_1_5= '-=' | lv_key_1_6= '++' | lv_key_1_7= '--' ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1029:1: ( (lv_key_1_1= '<-' | lv_key_1_2= '<<' | lv_key_1_3= '>>' | lv_key_1_4= '+=' | lv_key_1_5= '-=' | lv_key_1_6= '++' | lv_key_1_7= '--' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1030:1: (lv_key_1_1= '<-' | lv_key_1_2= '<<' | lv_key_1_3= '>>' | lv_key_1_4= '+=' | lv_key_1_5= '-=' | lv_key_1_6= '++' | lv_key_1_7= '--' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1030:1: (lv_key_1_1= '<-' | lv_key_1_2= '<<' | lv_key_1_3= '>>' | lv_key_1_4= '+=' | lv_key_1_5= '-=' | lv_key_1_6= '++' | lv_key_1_7= '--' )
            int alt19=7;
            switch ( input.LA(1) ) {
            case 45:
                {
                alt19=1;
                }
                break;
            case 46:
                {
                alt19=2;
                }
                break;
            case 47:
                {
                alt19=3;
                }
                break;
            case 48:
                {
                alt19=4;
                }
                break;
            case 49:
                {
                alt19=5;
                }
                break;
            case 50:
                {
                alt19=6;
                }
                break;
            case 51:
                {
                alt19=7;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 19, 0, input);

                throw nvae;
            }

            switch (alt19) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1031:3: lv_key_1_1= '<-'
                    {
                    lv_key_1_1=(Token)match(input,45,FOLLOW_45_in_ruleAssignmentStatement2193); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_key_1_1, grammarAccess.getAssignmentStatementAccess().getKeyLessThanSignHyphenMinusKeyword_1_0_0());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getAssignmentStatementRule());
                      	        }
                             		setWithLastConsumed(current, "key", lv_key_1_1, null);
                      	    
                    }

                    }
                    break;
                case 2 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1043:8: lv_key_1_2= '<<'
                    {
                    lv_key_1_2=(Token)match(input,46,FOLLOW_46_in_ruleAssignmentStatement2222); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_key_1_2, grammarAccess.getAssignmentStatementAccess().getKeyLessThanSignLessThanSignKeyword_1_0_1());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getAssignmentStatementRule());
                      	        }
                             		setWithLastConsumed(current, "key", lv_key_1_2, null);
                      	    
                    }

                    }
                    break;
                case 3 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1055:8: lv_key_1_3= '>>'
                    {
                    lv_key_1_3=(Token)match(input,47,FOLLOW_47_in_ruleAssignmentStatement2251); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_key_1_3, grammarAccess.getAssignmentStatementAccess().getKeyGreaterThanSignGreaterThanSignKeyword_1_0_2());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getAssignmentStatementRule());
                      	        }
                             		setWithLastConsumed(current, "key", lv_key_1_3, null);
                      	    
                    }

                    }
                    break;
                case 4 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1067:8: lv_key_1_4= '+='
                    {
                    lv_key_1_4=(Token)match(input,48,FOLLOW_48_in_ruleAssignmentStatement2280); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_key_1_4, grammarAccess.getAssignmentStatementAccess().getKeyPlusSignEqualsSignKeyword_1_0_3());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getAssignmentStatementRule());
                      	        }
                             		setWithLastConsumed(current, "key", lv_key_1_4, null);
                      	    
                    }

                    }
                    break;
                case 5 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1079:8: lv_key_1_5= '-='
                    {
                    lv_key_1_5=(Token)match(input,49,FOLLOW_49_in_ruleAssignmentStatement2309); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_key_1_5, grammarAccess.getAssignmentStatementAccess().getKeyHyphenMinusEqualsSignKeyword_1_0_4());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getAssignmentStatementRule());
                      	        }
                             		setWithLastConsumed(current, "key", lv_key_1_5, null);
                      	    
                    }

                    }
                    break;
                case 6 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1091:8: lv_key_1_6= '++'
                    {
                    lv_key_1_6=(Token)match(input,50,FOLLOW_50_in_ruleAssignmentStatement2338); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_key_1_6, grammarAccess.getAssignmentStatementAccess().getKeyPlusSignPlusSignKeyword_1_0_5());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getAssignmentStatementRule());
                      	        }
                             		setWithLastConsumed(current, "key", lv_key_1_6, null);
                      	    
                    }

                    }
                    break;
                case 7 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1103:8: lv_key_1_7= '--'
                    {
                    lv_key_1_7=(Token)match(input,51,FOLLOW_51_in_ruleAssignmentStatement2367); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_key_1_7, grammarAccess.getAssignmentStatementAccess().getKeyHyphenMinusHyphenMinusKeyword_1_0_6());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getAssignmentStatementRule());
                      	        }
                             		setWithLastConsumed(current, "key", lv_key_1_7, null);
                      	    
                    }

                    }
                    break;

            }


            }


            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1118:2: ( (lv_value_2_0= ruleExpression ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1119:1: (lv_value_2_0= ruleExpression )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1119:1: (lv_value_2_0= ruleExpression )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1120:3: lv_value_2_0= ruleExpression
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getAssignmentStatementAccess().getValueExpressionParserRuleCall_2_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleExpression_in_ruleAssignmentStatement2404);
            lv_value_2_0=ruleExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getAssignmentStatementRule());
              	        }
                     		set(
                     			current, 
                     			"value",
                      		lv_value_2_0, 
                      		"Expression");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1136:2: ( (lv_facets_3_0= ruleFacet ) )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==RULE_ID||LA20_0==45||(LA20_0>=52 && LA20_0<=53)||(LA20_0>=56 && LA20_0<=58)) ) {
                    alt20=1;
                }


                switch (alt20) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1137:1: (lv_facets_3_0= ruleFacet )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1137:1: (lv_facets_3_0= ruleFacet )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1138:3: lv_facets_3_0= ruleFacet
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getAssignmentStatementAccess().getFacetsFacetParserRuleCall_3_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleFacet_in_ruleAssignmentStatement2425);
            	    lv_facets_3_0=ruleFacet();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getAssignmentStatementRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"facets",
            	              		lv_facets_3_0, 
            	              		"Facet");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop20;
                }
            } while (true);

            otherlv_4=(Token)match(input,36,FOLLOW_36_in_ruleAssignmentStatement2438); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_4, grammarAccess.getAssignmentStatementAccess().getSemicolonKeyword_4());
                  
            }

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAssignmentStatement"


    // $ANTLR start "entryRuleFacet"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1166:1: entryRuleFacet returns [EObject current=null] : iv_ruleFacet= ruleFacet EOF ;
    public final EObject entryRuleFacet() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleFacet = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1167:2: (iv_ruleFacet= ruleFacet EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1168:2: iv_ruleFacet= ruleFacet EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getFacetRule()); 
            }
            pushFollow(FOLLOW_ruleFacet_in_entryRuleFacet2474);
            iv_ruleFacet=ruleFacet();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleFacet; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleFacet2484); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleFacet"


    // $ANTLR start "ruleFacet"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1175:1: ruleFacet returns [EObject current=null] : (this_FunctionFacet_0= ruleFunctionFacet | this_DefinitionFacet_1= ruleDefinitionFacet | this_ClassicFacet_2= ruleClassicFacet ) ;
    public final EObject ruleFacet() throws RecognitionException {
        EObject current = null;

        EObject this_FunctionFacet_0 = null;

        EObject this_DefinitionFacet_1 = null;

        EObject this_ClassicFacet_2 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1178:28: ( (this_FunctionFacet_0= ruleFunctionFacet | this_DefinitionFacet_1= ruleDefinitionFacet | this_ClassicFacet_2= ruleClassicFacet ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1179:1: (this_FunctionFacet_0= ruleFunctionFacet | this_DefinitionFacet_1= ruleDefinitionFacet | this_ClassicFacet_2= ruleClassicFacet )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1179:1: (this_FunctionFacet_0= ruleFunctionFacet | this_DefinitionFacet_1= ruleDefinitionFacet | this_ClassicFacet_2= ruleClassicFacet )
            int alt21=3;
            switch ( input.LA(1) ) {
            case 52:
            case 53:
                {
                alt21=1;
                }
                break;
            case 56:
            case 57:
            case 58:
                {
                alt21=2;
                }
                break;
            case RULE_ID:
            case 45:
                {
                alt21=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                throw nvae;
            }

            switch (alt21) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1180:5: this_FunctionFacet_0= ruleFunctionFacet
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getFacetAccess().getFunctionFacetParserRuleCall_0()); 
                          
                    }
                    pushFollow(FOLLOW_ruleFunctionFacet_in_ruleFacet2531);
                    this_FunctionFacet_0=ruleFunctionFacet();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_FunctionFacet_0; 
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 2 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1190:5: this_DefinitionFacet_1= ruleDefinitionFacet
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getFacetAccess().getDefinitionFacetParserRuleCall_1()); 
                          
                    }
                    pushFollow(FOLLOW_ruleDefinitionFacet_in_ruleFacet2558);
                    this_DefinitionFacet_1=ruleDefinitionFacet();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_DefinitionFacet_1; 
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 3 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1200:5: this_ClassicFacet_2= ruleClassicFacet
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getFacetAccess().getClassicFacetParserRuleCall_2()); 
                          
                    }
                    pushFollow(FOLLOW_ruleClassicFacet_in_ruleFacet2585);
                    this_ClassicFacet_2=ruleClassicFacet();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_ClassicFacet_2; 
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleFacet"


    // $ANTLR start "entryRuleClassicFacet"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1216:1: entryRuleClassicFacet returns [EObject current=null] : iv_ruleClassicFacet= ruleClassicFacet EOF ;
    public final EObject entryRuleClassicFacet() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleClassicFacet = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1217:2: (iv_ruleClassicFacet= ruleClassicFacet EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1218:2: iv_ruleClassicFacet= ruleClassicFacet EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getClassicFacetRule()); 
            }
            pushFollow(FOLLOW_ruleClassicFacet_in_entryRuleClassicFacet2620);
            iv_ruleClassicFacet=ruleClassicFacet();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleClassicFacet; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleClassicFacet2630); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleClassicFacet"


    // $ANTLR start "ruleClassicFacet"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1225:1: ruleClassicFacet returns [EObject current=null] : ( ( ( ( (lv_key_0_0= RULE_ID ) ) otherlv_1= ':' ) | ( (lv_key_2_0= '<-' ) ) ) ( (lv_expr_3_0= ruleExpression ) ) ) ;
    public final EObject ruleClassicFacet() throws RecognitionException {
        EObject current = null;

        Token lv_key_0_0=null;
        Token otherlv_1=null;
        Token lv_key_2_0=null;
        EObject lv_expr_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1228:28: ( ( ( ( ( (lv_key_0_0= RULE_ID ) ) otherlv_1= ':' ) | ( (lv_key_2_0= '<-' ) ) ) ( (lv_expr_3_0= ruleExpression ) ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1229:1: ( ( ( ( (lv_key_0_0= RULE_ID ) ) otherlv_1= ':' ) | ( (lv_key_2_0= '<-' ) ) ) ( (lv_expr_3_0= ruleExpression ) ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1229:1: ( ( ( ( (lv_key_0_0= RULE_ID ) ) otherlv_1= ':' ) | ( (lv_key_2_0= '<-' ) ) ) ( (lv_expr_3_0= ruleExpression ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1229:2: ( ( ( (lv_key_0_0= RULE_ID ) ) otherlv_1= ':' ) | ( (lv_key_2_0= '<-' ) ) ) ( (lv_expr_3_0= ruleExpression ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1229:2: ( ( ( (lv_key_0_0= RULE_ID ) ) otherlv_1= ':' ) | ( (lv_key_2_0= '<-' ) ) )
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==RULE_ID) ) {
                alt22=1;
            }
            else if ( (LA22_0==45) ) {
                alt22=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 22, 0, input);

                throw nvae;
            }
            switch (alt22) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1229:3: ( ( (lv_key_0_0= RULE_ID ) ) otherlv_1= ':' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1229:3: ( ( (lv_key_0_0= RULE_ID ) ) otherlv_1= ':' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1229:4: ( (lv_key_0_0= RULE_ID ) ) otherlv_1= ':'
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1229:4: ( (lv_key_0_0= RULE_ID ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1230:1: (lv_key_0_0= RULE_ID )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1230:1: (lv_key_0_0= RULE_ID )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1231:3: lv_key_0_0= RULE_ID
                    {
                    lv_key_0_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleClassicFacet2674); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			newLeafNode(lv_key_0_0, grammarAccess.getClassicFacetAccess().getKeyIDTerminalRuleCall_0_0_0_0()); 
                      		
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getClassicFacetRule());
                      	        }
                             		setWithLastConsumed(
                             			current, 
                             			"key",
                              		lv_key_0_0, 
                              		"ID");
                      	    
                    }

                    }


                    }

                    otherlv_1=(Token)match(input,40,FOLLOW_40_in_ruleClassicFacet2691); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_1, grammarAccess.getClassicFacetAccess().getColonKeyword_0_0_1());
                          
                    }

                    }


                    }
                    break;
                case 2 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1252:6: ( (lv_key_2_0= '<-' ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1252:6: ( (lv_key_2_0= '<-' ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1253:1: (lv_key_2_0= '<-' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1253:1: (lv_key_2_0= '<-' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1254:3: lv_key_2_0= '<-'
                    {
                    lv_key_2_0=(Token)match(input,45,FOLLOW_45_in_ruleClassicFacet2716); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_key_2_0, grammarAccess.getClassicFacetAccess().getKeyLessThanSignHyphenMinusKeyword_0_1_0());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getClassicFacetRule());
                      	        }
                             		setWithLastConsumed(current, "key", lv_key_2_0, "<-");
                      	    
                    }

                    }


                    }


                    }
                    break;

            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1267:3: ( (lv_expr_3_0= ruleExpression ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1268:1: (lv_expr_3_0= ruleExpression )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1268:1: (lv_expr_3_0= ruleExpression )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1269:3: lv_expr_3_0= ruleExpression
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getClassicFacetAccess().getExprExpressionParserRuleCall_1_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleExpression_in_ruleClassicFacet2751);
            lv_expr_3_0=ruleExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getClassicFacetRule());
              	        }
                     		set(
                     			current, 
                     			"expr",
                      		lv_expr_3_0, 
                      		"Expression");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleClassicFacet"


    // $ANTLR start "entryRuleFunctionFacet"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1293:1: entryRuleFunctionFacet returns [EObject current=null] : iv_ruleFunctionFacet= ruleFunctionFacet EOF ;
    public final EObject entryRuleFunctionFacet() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleFunctionFacet = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1294:2: (iv_ruleFunctionFacet= ruleFunctionFacet EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1295:2: iv_ruleFunctionFacet= ruleFunctionFacet EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getFunctionFacetRule()); 
            }
            pushFollow(FOLLOW_ruleFunctionFacet_in_entryRuleFunctionFacet2787);
            iv_ruleFunctionFacet=ruleFunctionFacet();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleFunctionFacet; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleFunctionFacet2797); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleFunctionFacet"


    // $ANTLR start "ruleFunctionFacet"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1302:1: ruleFunctionFacet returns [EObject current=null] : ( ( ( (lv_key_0_0= 'function:' ) ) | ( (lv_key_1_0= '->' ) ) ) otherlv_2= '{' ( (lv_expr_3_0= ruleExpression ) ) otherlv_4= '}' ) ;
    public final EObject ruleFunctionFacet() throws RecognitionException {
        EObject current = null;

        Token lv_key_0_0=null;
        Token lv_key_1_0=null;
        Token otherlv_2=null;
        Token otherlv_4=null;
        EObject lv_expr_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1305:28: ( ( ( ( (lv_key_0_0= 'function:' ) ) | ( (lv_key_1_0= '->' ) ) ) otherlv_2= '{' ( (lv_expr_3_0= ruleExpression ) ) otherlv_4= '}' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1306:1: ( ( ( (lv_key_0_0= 'function:' ) ) | ( (lv_key_1_0= '->' ) ) ) otherlv_2= '{' ( (lv_expr_3_0= ruleExpression ) ) otherlv_4= '}' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1306:1: ( ( ( (lv_key_0_0= 'function:' ) ) | ( (lv_key_1_0= '->' ) ) ) otherlv_2= '{' ( (lv_expr_3_0= ruleExpression ) ) otherlv_4= '}' )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1306:2: ( ( (lv_key_0_0= 'function:' ) ) | ( (lv_key_1_0= '->' ) ) ) otherlv_2= '{' ( (lv_expr_3_0= ruleExpression ) ) otherlv_4= '}'
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1306:2: ( ( (lv_key_0_0= 'function:' ) ) | ( (lv_key_1_0= '->' ) ) )
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==52) ) {
                alt23=1;
            }
            else if ( (LA23_0==53) ) {
                alt23=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 23, 0, input);

                throw nvae;
            }
            switch (alt23) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1306:3: ( (lv_key_0_0= 'function:' ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1306:3: ( (lv_key_0_0= 'function:' ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1307:1: (lv_key_0_0= 'function:' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1307:1: (lv_key_0_0= 'function:' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1308:3: lv_key_0_0= 'function:'
                    {
                    lv_key_0_0=(Token)match(input,52,FOLLOW_52_in_ruleFunctionFacet2841); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_key_0_0, grammarAccess.getFunctionFacetAccess().getKeyFunctionKeyword_0_0_0());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getFunctionFacetRule());
                      	        }
                             		setWithLastConsumed(current, "key", lv_key_0_0, "function:");
                      	    
                    }

                    }


                    }


                    }
                    break;
                case 2 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1322:6: ( (lv_key_1_0= '->' ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1322:6: ( (lv_key_1_0= '->' ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1323:1: (lv_key_1_0= '->' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1323:1: (lv_key_1_0= '->' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1324:3: lv_key_1_0= '->'
                    {
                    lv_key_1_0=(Token)match(input,53,FOLLOW_53_in_ruleFunctionFacet2878); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_key_1_0, grammarAccess.getFunctionFacetAccess().getKeyHyphenMinusGreaterThanSignKeyword_0_1_0());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getFunctionFacetRule());
                      	        }
                             		setWithLastConsumed(current, "key", lv_key_1_0, "->");
                      	    
                    }

                    }


                    }


                    }
                    break;

            }

            otherlv_2=(Token)match(input,54,FOLLOW_54_in_ruleFunctionFacet2904); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_2, grammarAccess.getFunctionFacetAccess().getLeftCurlyBracketKeyword_1());
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1341:1: ( (lv_expr_3_0= ruleExpression ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1342:1: (lv_expr_3_0= ruleExpression )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1342:1: (lv_expr_3_0= ruleExpression )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1343:3: lv_expr_3_0= ruleExpression
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getFunctionFacetAccess().getExprExpressionParserRuleCall_2_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleExpression_in_ruleFunctionFacet2925);
            lv_expr_3_0=ruleExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getFunctionFacetRule());
              	        }
                     		set(
                     			current, 
                     			"expr",
                      		lv_expr_3_0, 
                      		"Expression");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            otherlv_4=(Token)match(input,55,FOLLOW_55_in_ruleFunctionFacet2937); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_4, grammarAccess.getFunctionFacetAccess().getRightCurlyBracketKeyword_3());
                  
            }

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleFunctionFacet"


    // $ANTLR start "entryRuleDefinitionFacetKey"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1371:1: entryRuleDefinitionFacetKey returns [String current=null] : iv_ruleDefinitionFacetKey= ruleDefinitionFacetKey EOF ;
    public final String entryRuleDefinitionFacetKey() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleDefinitionFacetKey = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1372:2: (iv_ruleDefinitionFacetKey= ruleDefinitionFacetKey EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1373:2: iv_ruleDefinitionFacetKey= ruleDefinitionFacetKey EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getDefinitionFacetKeyRule()); 
            }
            pushFollow(FOLLOW_ruleDefinitionFacetKey_in_entryRuleDefinitionFacetKey2974);
            iv_ruleDefinitionFacetKey=ruleDefinitionFacetKey();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleDefinitionFacetKey.getText(); 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleDefinitionFacetKey2985); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleDefinitionFacetKey"


    // $ANTLR start "ruleDefinitionFacetKey"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1380:1: ruleDefinitionFacetKey returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'name:' | kw= 'returns:' | kw= 'action:' ) ;
    public final AntlrDatatypeRuleToken ruleDefinitionFacetKey() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;

         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1383:28: ( (kw= 'name:' | kw= 'returns:' | kw= 'action:' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1384:1: (kw= 'name:' | kw= 'returns:' | kw= 'action:' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1384:1: (kw= 'name:' | kw= 'returns:' | kw= 'action:' )
            int alt24=3;
            switch ( input.LA(1) ) {
            case 56:
                {
                alt24=1;
                }
                break;
            case 57:
                {
                alt24=2;
                }
                break;
            case 58:
                {
                alt24=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 24, 0, input);

                throw nvae;
            }

            switch (alt24) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1385:2: kw= 'name:'
                    {
                    kw=(Token)match(input,56,FOLLOW_56_in_ruleDefinitionFacetKey3023); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getDefinitionFacetKeyAccess().getNameKeyword_0()); 
                          
                    }

                    }
                    break;
                case 2 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1392:2: kw= 'returns:'
                    {
                    kw=(Token)match(input,57,FOLLOW_57_in_ruleDefinitionFacetKey3042); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getDefinitionFacetKeyAccess().getReturnsKeyword_1()); 
                          
                    }

                    }
                    break;
                case 3 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1399:2: kw= 'action:'
                    {
                    kw=(Token)match(input,58,FOLLOW_58_in_ruleDefinitionFacetKey3061); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getDefinitionFacetKeyAccess().getActionKeyword_2()); 
                          
                    }

                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleDefinitionFacetKey"


    // $ANTLR start "entryRuleDefinitionFacet"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1412:1: entryRuleDefinitionFacet returns [EObject current=null] : iv_ruleDefinitionFacet= ruleDefinitionFacet EOF ;
    public final EObject entryRuleDefinitionFacet() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDefinitionFacet = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1413:2: (iv_ruleDefinitionFacet= ruleDefinitionFacet EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1414:2: iv_ruleDefinitionFacet= ruleDefinitionFacet EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getDefinitionFacetRule()); 
            }
            pushFollow(FOLLOW_ruleDefinitionFacet_in_entryRuleDefinitionFacet3101);
            iv_ruleDefinitionFacet=ruleDefinitionFacet();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleDefinitionFacet; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleDefinitionFacet3111); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleDefinitionFacet"


    // $ANTLR start "ruleDefinitionFacet"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1421:1: ruleDefinitionFacet returns [EObject current=null] : ( ( (lv_key_0_0= ruleDefinitionFacetKey ) ) ( ( (lv_name_1_1= RULE_ID | lv_name_1_2= RULE_STRING | lv_name_1_3= ruleBuiltInStatementKey ) ) ) ) ;
    public final EObject ruleDefinitionFacet() throws RecognitionException {
        EObject current = null;

        Token lv_name_1_1=null;
        Token lv_name_1_2=null;
        AntlrDatatypeRuleToken lv_key_0_0 = null;

        AntlrDatatypeRuleToken lv_name_1_3 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1424:28: ( ( ( (lv_key_0_0= ruleDefinitionFacetKey ) ) ( ( (lv_name_1_1= RULE_ID | lv_name_1_2= RULE_STRING | lv_name_1_3= ruleBuiltInStatementKey ) ) ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1425:1: ( ( (lv_key_0_0= ruleDefinitionFacetKey ) ) ( ( (lv_name_1_1= RULE_ID | lv_name_1_2= RULE_STRING | lv_name_1_3= ruleBuiltInStatementKey ) ) ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1425:1: ( ( (lv_key_0_0= ruleDefinitionFacetKey ) ) ( ( (lv_name_1_1= RULE_ID | lv_name_1_2= RULE_STRING | lv_name_1_3= ruleBuiltInStatementKey ) ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1425:2: ( (lv_key_0_0= ruleDefinitionFacetKey ) ) ( ( (lv_name_1_1= RULE_ID | lv_name_1_2= RULE_STRING | lv_name_1_3= ruleBuiltInStatementKey ) ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1425:2: ( (lv_key_0_0= ruleDefinitionFacetKey ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1426:1: (lv_key_0_0= ruleDefinitionFacetKey )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1426:1: (lv_key_0_0= ruleDefinitionFacetKey )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1427:3: lv_key_0_0= ruleDefinitionFacetKey
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getDefinitionFacetAccess().getKeyDefinitionFacetKeyParserRuleCall_0_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleDefinitionFacetKey_in_ruleDefinitionFacet3157);
            lv_key_0_0=ruleDefinitionFacetKey();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getDefinitionFacetRule());
              	        }
                     		set(
                     			current, 
                     			"key",
                      		lv_key_0_0, 
                      		"DefinitionFacetKey");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1443:2: ( ( (lv_name_1_1= RULE_ID | lv_name_1_2= RULE_STRING | lv_name_1_3= ruleBuiltInStatementKey ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1444:1: ( (lv_name_1_1= RULE_ID | lv_name_1_2= RULE_STRING | lv_name_1_3= ruleBuiltInStatementKey ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1444:1: ( (lv_name_1_1= RULE_ID | lv_name_1_2= RULE_STRING | lv_name_1_3= ruleBuiltInStatementKey ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1445:1: (lv_name_1_1= RULE_ID | lv_name_1_2= RULE_STRING | lv_name_1_3= ruleBuiltInStatementKey )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1445:1: (lv_name_1_1= RULE_ID | lv_name_1_2= RULE_STRING | lv_name_1_3= ruleBuiltInStatementKey )
            int alt25=3;
            switch ( input.LA(1) ) {
            case RULE_ID:
                {
                alt25=1;
                }
                break;
            case RULE_STRING:
                {
                alt25=2;
                }
                break;
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
                {
                alt25=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 25, 0, input);

                throw nvae;
            }

            switch (alt25) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1446:3: lv_name_1_1= RULE_ID
                    {
                    lv_name_1_1=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleDefinitionFacet3176); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			newLeafNode(lv_name_1_1, grammarAccess.getDefinitionFacetAccess().getNameIDTerminalRuleCall_1_0_0()); 
                      		
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getDefinitionFacetRule());
                      	        }
                             		setWithLastConsumed(
                             			current, 
                             			"name",
                              		lv_name_1_1, 
                              		"ID");
                      	    
                    }

                    }
                    break;
                case 2 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1461:8: lv_name_1_2= RULE_STRING
                    {
                    lv_name_1_2=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleDefinitionFacet3196); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			newLeafNode(lv_name_1_2, grammarAccess.getDefinitionFacetAccess().getNameSTRINGTerminalRuleCall_1_0_1()); 
                      		
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getDefinitionFacetRule());
                      	        }
                             		setWithLastConsumed(
                             			current, 
                             			"name",
                              		lv_name_1_2, 
                              		"STRING");
                      	    
                    }

                    }
                    break;
                case 3 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1476:8: lv_name_1_3= ruleBuiltInStatementKey
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getDefinitionFacetAccess().getNameBuiltInStatementKeyParserRuleCall_1_0_2()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleBuiltInStatementKey_in_ruleDefinitionFacet3220);
                    lv_name_1_3=ruleBuiltInStatementKey();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getDefinitionFacetRule());
                      	        }
                             		set(
                             			current, 
                             			"name",
                              		lv_name_1_3, 
                              		"BuiltInStatementKey");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }
                    break;

            }


            }


            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleDefinitionFacet"


    // $ANTLR start "entryRuleBlock"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1502:1: entryRuleBlock returns [EObject current=null] : iv_ruleBlock= ruleBlock EOF ;
    public final EObject entryRuleBlock() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleBlock = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1503:2: (iv_ruleBlock= ruleBlock EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1504:2: iv_ruleBlock= ruleBlock EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getBlockRule()); 
            }
            pushFollow(FOLLOW_ruleBlock_in_entryRuleBlock3259);
            iv_ruleBlock=ruleBlock();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleBlock; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleBlock3269); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleBlock"


    // $ANTLR start "ruleBlock"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1511:1: ruleBlock returns [EObject current=null] : ( () otherlv_1= '{' ( (lv_statements_2_0= ruleStatement ) )* otherlv_3= '}' ) ;
    public final EObject ruleBlock() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        EObject lv_statements_2_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1514:28: ( ( () otherlv_1= '{' ( (lv_statements_2_0= ruleStatement ) )* otherlv_3= '}' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1515:1: ( () otherlv_1= '{' ( (lv_statements_2_0= ruleStatement ) )* otherlv_3= '}' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1515:1: ( () otherlv_1= '{' ( (lv_statements_2_0= ruleStatement ) )* otherlv_3= '}' )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1515:2: () otherlv_1= '{' ( (lv_statements_2_0= ruleStatement ) )* otherlv_3= '}'
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1515:2: ()
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1516:5: 
            {
            if ( state.backtracking==0 ) {

                      current = forceCreateModelElement(
                          grammarAccess.getBlockAccess().getBlockAction_0(),
                          current);
                  
            }

            }

            otherlv_1=(Token)match(input,54,FOLLOW_54_in_ruleBlock3315); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_1, grammarAccess.getBlockAccess().getLeftCurlyBracketKeyword_1());
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1525:1: ( (lv_statements_2_0= ruleStatement ) )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( ((LA26_0>=RULE_ID && LA26_0<=RULE_BOOLEAN)||(LA26_0>=16 && LA26_0<=34)||LA26_0==37||LA26_0==44||LA26_0==54||(LA26_0>=56 && LA26_0<=58)||LA26_0==67||(LA26_0>=72 && LA26_0<=77)||LA26_0==80) ) {
                    alt26=1;
                }


                switch (alt26) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1526:1: (lv_statements_2_0= ruleStatement )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1526:1: (lv_statements_2_0= ruleStatement )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1527:3: lv_statements_2_0= ruleStatement
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getBlockAccess().getStatementsStatementParserRuleCall_2_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleStatement_in_ruleBlock3336);
            	    lv_statements_2_0=ruleStatement();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getBlockRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"statements",
            	              		lv_statements_2_0, 
            	              		"Statement");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop26;
                }
            } while (true);

            otherlv_3=(Token)match(input,55,FOLLOW_55_in_ruleBlock3349); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_3, grammarAccess.getBlockAccess().getRightCurlyBracketKeyword_3());
                  
            }

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleBlock"


    // $ANTLR start "entryRuleExpression"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1555:1: entryRuleExpression returns [EObject current=null] : iv_ruleExpression= ruleExpression EOF ;
    public final EObject entryRuleExpression() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleExpression = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1556:2: (iv_ruleExpression= ruleExpression EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1557:2: iv_ruleExpression= ruleExpression EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getExpressionRule()); 
            }
            pushFollow(FOLLOW_ruleExpression_in_entryRuleExpression3385);
            iv_ruleExpression=ruleExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleExpression; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleExpression3395); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleExpression"


    // $ANTLR start "ruleExpression"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1564:1: ruleExpression returns [EObject current=null] : this_TernExp_0= ruleTernExp ;
    public final EObject ruleExpression() throws RecognitionException {
        EObject current = null;

        EObject this_TernExp_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1567:28: (this_TernExp_0= ruleTernExp )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1569:5: this_TernExp_0= ruleTernExp
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getExpressionAccess().getTernExpParserRuleCall()); 
                  
            }
            pushFollow(FOLLOW_ruleTernExp_in_ruleExpression3441);
            this_TernExp_0=ruleTernExp();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_TernExp_0; 
                      afterParserOrEnumRuleCall();
                  
            }

            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleExpression"


    // $ANTLR start "entryRuleTernExp"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1585:1: entryRuleTernExp returns [EObject current=null] : iv_ruleTernExp= ruleTernExp EOF ;
    public final EObject entryRuleTernExp() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTernExp = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1586:2: (iv_ruleTernExp= ruleTernExp EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1587:2: iv_ruleTernExp= ruleTernExp EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getTernExpRule()); 
            }
            pushFollow(FOLLOW_ruleTernExp_in_entryRuleTernExp3475);
            iv_ruleTernExp=ruleTernExp();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleTernExp; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleTernExp3485); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTernExp"


    // $ANTLR start "ruleTernExp"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1594:1: ruleTernExp returns [EObject current=null] : (this_OrExp_0= ruleOrExp ( () ( (lv_op_2_0= '?' ) ) ( (lv_right_3_0= ruleOrExp ) ) otherlv_4= ':' ( (lv_ifFalse_5_0= ruleOrExp ) ) )? ) ;
    public final EObject ruleTernExp() throws RecognitionException {
        EObject current = null;

        Token lv_op_2_0=null;
        Token otherlv_4=null;
        EObject this_OrExp_0 = null;

        EObject lv_right_3_0 = null;

        EObject lv_ifFalse_5_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1597:28: ( (this_OrExp_0= ruleOrExp ( () ( (lv_op_2_0= '?' ) ) ( (lv_right_3_0= ruleOrExp ) ) otherlv_4= ':' ( (lv_ifFalse_5_0= ruleOrExp ) ) )? ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1598:1: (this_OrExp_0= ruleOrExp ( () ( (lv_op_2_0= '?' ) ) ( (lv_right_3_0= ruleOrExp ) ) otherlv_4= ':' ( (lv_ifFalse_5_0= ruleOrExp ) ) )? )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1598:1: (this_OrExp_0= ruleOrExp ( () ( (lv_op_2_0= '?' ) ) ( (lv_right_3_0= ruleOrExp ) ) otherlv_4= ':' ( (lv_ifFalse_5_0= ruleOrExp ) ) )? )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1599:5: this_OrExp_0= ruleOrExp ( () ( (lv_op_2_0= '?' ) ) ( (lv_right_3_0= ruleOrExp ) ) otherlv_4= ':' ( (lv_ifFalse_5_0= ruleOrExp ) ) )?
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getTernExpAccess().getOrExpParserRuleCall_0()); 
                  
            }
            pushFollow(FOLLOW_ruleOrExp_in_ruleTernExp3532);
            this_OrExp_0=ruleOrExp();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_OrExp_0; 
                      afterParserOrEnumRuleCall();
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1607:1: ( () ( (lv_op_2_0= '?' ) ) ( (lv_right_3_0= ruleOrExp ) ) otherlv_4= ':' ( (lv_ifFalse_5_0= ruleOrExp ) ) )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==59) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1607:2: () ( (lv_op_2_0= '?' ) ) ( (lv_right_3_0= ruleOrExp ) ) otherlv_4= ':' ( (lv_ifFalse_5_0= ruleOrExp ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1607:2: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1608:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElementAndSet(
                                  grammarAccess.getTernExpAccess().getTernExpLeftAction_1_0(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1613:2: ( (lv_op_2_0= '?' ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1614:1: (lv_op_2_0= '?' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1614:1: (lv_op_2_0= '?' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1615:3: lv_op_2_0= '?'
                    {
                    lv_op_2_0=(Token)match(input,59,FOLLOW_59_in_ruleTernExp3559); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_op_2_0, grammarAccess.getTernExpAccess().getOpQuestionMarkKeyword_1_1_0());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getTernExpRule());
                      	        }
                             		setWithLastConsumed(current, "op", lv_op_2_0, "?");
                      	    
                    }

                    }


                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1628:2: ( (lv_right_3_0= ruleOrExp ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1629:1: (lv_right_3_0= ruleOrExp )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1629:1: (lv_right_3_0= ruleOrExp )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1630:3: lv_right_3_0= ruleOrExp
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getTernExpAccess().getRightOrExpParserRuleCall_1_2_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleOrExp_in_ruleTernExp3593);
                    lv_right_3_0=ruleOrExp();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getTernExpRule());
                      	        }
                             		set(
                             			current, 
                             			"right",
                              		lv_right_3_0, 
                              		"OrExp");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }

                    otherlv_4=(Token)match(input,40,FOLLOW_40_in_ruleTernExp3605); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_4, grammarAccess.getTernExpAccess().getColonKeyword_1_3());
                          
                    }
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1650:1: ( (lv_ifFalse_5_0= ruleOrExp ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1651:1: (lv_ifFalse_5_0= ruleOrExp )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1651:1: (lv_ifFalse_5_0= ruleOrExp )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1652:3: lv_ifFalse_5_0= ruleOrExp
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getTernExpAccess().getIfFalseOrExpParserRuleCall_1_4_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleOrExp_in_ruleTernExp3626);
                    lv_ifFalse_5_0=ruleOrExp();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getTernExpRule());
                      	        }
                             		set(
                             			current, 
                             			"ifFalse",
                              		lv_ifFalse_5_0, 
                              		"OrExp");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }
                    break;

            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTernExp"


    // $ANTLR start "entryRuleOrExp"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1676:1: entryRuleOrExp returns [EObject current=null] : iv_ruleOrExp= ruleOrExp EOF ;
    public final EObject entryRuleOrExp() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOrExp = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1677:2: (iv_ruleOrExp= ruleOrExp EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1678:2: iv_ruleOrExp= ruleOrExp EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getOrExpRule()); 
            }
            pushFollow(FOLLOW_ruleOrExp_in_entryRuleOrExp3664);
            iv_ruleOrExp=ruleOrExp();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleOrExp; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleOrExp3674); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleOrExp"


    // $ANTLR start "ruleOrExp"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1685:1: ruleOrExp returns [EObject current=null] : (this_AndExp_0= ruleAndExp ( () ( (lv_op_2_0= 'or' ) ) ( (lv_right_3_0= ruleAndExp ) ) )* ) ;
    public final EObject ruleOrExp() throws RecognitionException {
        EObject current = null;

        Token lv_op_2_0=null;
        EObject this_AndExp_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1688:28: ( (this_AndExp_0= ruleAndExp ( () ( (lv_op_2_0= 'or' ) ) ( (lv_right_3_0= ruleAndExp ) ) )* ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1689:1: (this_AndExp_0= ruleAndExp ( () ( (lv_op_2_0= 'or' ) ) ( (lv_right_3_0= ruleAndExp ) ) )* )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1689:1: (this_AndExp_0= ruleAndExp ( () ( (lv_op_2_0= 'or' ) ) ( (lv_right_3_0= ruleAndExp ) ) )* )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1690:5: this_AndExp_0= ruleAndExp ( () ( (lv_op_2_0= 'or' ) ) ( (lv_right_3_0= ruleAndExp ) ) )*
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getOrExpAccess().getAndExpParserRuleCall_0()); 
                  
            }
            pushFollow(FOLLOW_ruleAndExp_in_ruleOrExp3721);
            this_AndExp_0=ruleAndExp();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_AndExp_0; 
                      afterParserOrEnumRuleCall();
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1698:1: ( () ( (lv_op_2_0= 'or' ) ) ( (lv_right_3_0= ruleAndExp ) ) )*
            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);

                if ( (LA28_0==60) ) {
                    alt28=1;
                }


                switch (alt28) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1698:2: () ( (lv_op_2_0= 'or' ) ) ( (lv_right_3_0= ruleAndExp ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1698:2: ()
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1699:5: 
            	    {
            	    if ( state.backtracking==0 ) {

            	              current = forceCreateModelElementAndSet(
            	                  grammarAccess.getOrExpAccess().getExpressionLeftAction_1_0(),
            	                  current);
            	          
            	    }

            	    }

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1704:2: ( (lv_op_2_0= 'or' ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1705:1: (lv_op_2_0= 'or' )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1705:1: (lv_op_2_0= 'or' )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1706:3: lv_op_2_0= 'or'
            	    {
            	    lv_op_2_0=(Token)match(input,60,FOLLOW_60_in_ruleOrExp3748); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	              newLeafNode(lv_op_2_0, grammarAccess.getOrExpAccess().getOpOrKeyword_1_1_0());
            	          
            	    }
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElement(grammarAccess.getOrExpRule());
            	      	        }
            	             		setWithLastConsumed(current, "op", lv_op_2_0, "or");
            	      	    
            	    }

            	    }


            	    }

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1719:2: ( (lv_right_3_0= ruleAndExp ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1720:1: (lv_right_3_0= ruleAndExp )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1720:1: (lv_right_3_0= ruleAndExp )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1721:3: lv_right_3_0= ruleAndExp
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getOrExpAccess().getRightAndExpParserRuleCall_1_2_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleAndExp_in_ruleOrExp3782);
            	    lv_right_3_0=ruleAndExp();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getOrExpRule());
            	      	        }
            	             		set(
            	             			current, 
            	             			"right",
            	              		lv_right_3_0, 
            	              		"AndExp");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop28;
                }
            } while (true);


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleOrExp"


    // $ANTLR start "entryRuleAndExp"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1745:1: entryRuleAndExp returns [EObject current=null] : iv_ruleAndExp= ruleAndExp EOF ;
    public final EObject entryRuleAndExp() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAndExp = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1746:2: (iv_ruleAndExp= ruleAndExp EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1747:2: iv_ruleAndExp= ruleAndExp EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAndExpRule()); 
            }
            pushFollow(FOLLOW_ruleAndExp_in_entryRuleAndExp3820);
            iv_ruleAndExp=ruleAndExp();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAndExp; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleAndExp3830); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAndExp"


    // $ANTLR start "ruleAndExp"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1754:1: ruleAndExp returns [EObject current=null] : (this_Relational_0= ruleRelational ( () ( (lv_op_2_0= 'and' ) ) ( (lv_right_3_0= ruleRelational ) ) )* ) ;
    public final EObject ruleAndExp() throws RecognitionException {
        EObject current = null;

        Token lv_op_2_0=null;
        EObject this_Relational_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1757:28: ( (this_Relational_0= ruleRelational ( () ( (lv_op_2_0= 'and' ) ) ( (lv_right_3_0= ruleRelational ) ) )* ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1758:1: (this_Relational_0= ruleRelational ( () ( (lv_op_2_0= 'and' ) ) ( (lv_right_3_0= ruleRelational ) ) )* )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1758:1: (this_Relational_0= ruleRelational ( () ( (lv_op_2_0= 'and' ) ) ( (lv_right_3_0= ruleRelational ) ) )* )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1759:5: this_Relational_0= ruleRelational ( () ( (lv_op_2_0= 'and' ) ) ( (lv_right_3_0= ruleRelational ) ) )*
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getAndExpAccess().getRelationalParserRuleCall_0()); 
                  
            }
            pushFollow(FOLLOW_ruleRelational_in_ruleAndExp3877);
            this_Relational_0=ruleRelational();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_Relational_0; 
                      afterParserOrEnumRuleCall();
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1767:1: ( () ( (lv_op_2_0= 'and' ) ) ( (lv_right_3_0= ruleRelational ) ) )*
            loop29:
            do {
                int alt29=2;
                int LA29_0 = input.LA(1);

                if ( (LA29_0==61) ) {
                    alt29=1;
                }


                switch (alt29) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1767:2: () ( (lv_op_2_0= 'and' ) ) ( (lv_right_3_0= ruleRelational ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1767:2: ()
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1768:5: 
            	    {
            	    if ( state.backtracking==0 ) {

            	              current = forceCreateModelElementAndSet(
            	                  grammarAccess.getAndExpAccess().getExpressionLeftAction_1_0(),
            	                  current);
            	          
            	    }

            	    }

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1773:2: ( (lv_op_2_0= 'and' ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1774:1: (lv_op_2_0= 'and' )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1774:1: (lv_op_2_0= 'and' )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1775:3: lv_op_2_0= 'and'
            	    {
            	    lv_op_2_0=(Token)match(input,61,FOLLOW_61_in_ruleAndExp3904); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	              newLeafNode(lv_op_2_0, grammarAccess.getAndExpAccess().getOpAndKeyword_1_1_0());
            	          
            	    }
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElement(grammarAccess.getAndExpRule());
            	      	        }
            	             		setWithLastConsumed(current, "op", lv_op_2_0, "and");
            	      	    
            	    }

            	    }


            	    }

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1788:2: ( (lv_right_3_0= ruleRelational ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1789:1: (lv_right_3_0= ruleRelational )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1789:1: (lv_right_3_0= ruleRelational )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1790:3: lv_right_3_0= ruleRelational
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getAndExpAccess().getRightRelationalParserRuleCall_1_2_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleRelational_in_ruleAndExp3938);
            	    lv_right_3_0=ruleRelational();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getAndExpRule());
            	      	        }
            	             		set(
            	             			current, 
            	             			"right",
            	              		lv_right_3_0, 
            	              		"Relational");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop29;
                }
            } while (true);


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAndExp"


    // $ANTLR start "entryRuleRelational"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1814:1: entryRuleRelational returns [EObject current=null] : iv_ruleRelational= ruleRelational EOF ;
    public final EObject entryRuleRelational() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRelational = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1815:2: (iv_ruleRelational= ruleRelational EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1816:2: iv_ruleRelational= ruleRelational EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getRelationalRule()); 
            }
            pushFollow(FOLLOW_ruleRelational_in_entryRuleRelational3976);
            iv_ruleRelational=ruleRelational();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleRelational; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleRelational3986); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleRelational"


    // $ANTLR start "ruleRelational"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1823:1: ruleRelational returns [EObject current=null] : (this_PairExpr_0= rulePairExpr ( ( () ( ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) ) ) ) ( (lv_right_3_0= rulePairExpr ) ) )? ) ;
    public final EObject ruleRelational() throws RecognitionException {
        EObject current = null;

        Token lv_op_2_1=null;
        Token lv_op_2_2=null;
        Token lv_op_2_3=null;
        Token lv_op_2_4=null;
        Token lv_op_2_5=null;
        Token lv_op_2_6=null;
        EObject this_PairExpr_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1826:28: ( (this_PairExpr_0= rulePairExpr ( ( () ( ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) ) ) ) ( (lv_right_3_0= rulePairExpr ) ) )? ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1827:1: (this_PairExpr_0= rulePairExpr ( ( () ( ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) ) ) ) ( (lv_right_3_0= rulePairExpr ) ) )? )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1827:1: (this_PairExpr_0= rulePairExpr ( ( () ( ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) ) ) ) ( (lv_right_3_0= rulePairExpr ) ) )? )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1828:5: this_PairExpr_0= rulePairExpr ( ( () ( ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) ) ) ) ( (lv_right_3_0= rulePairExpr ) ) )?
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getRelationalAccess().getPairExprParserRuleCall_0()); 
                  
            }
            pushFollow(FOLLOW_rulePairExpr_in_ruleRelational4033);
            this_PairExpr_0=rulePairExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_PairExpr_0; 
                      afterParserOrEnumRuleCall();
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1836:1: ( ( () ( ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) ) ) ) ( (lv_right_3_0= rulePairExpr ) ) )?
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==35||LA31_0==41||LA31_0==43||(LA31_0>=62 && LA31_0<=64)) ) {
                alt31=1;
            }
            switch (alt31) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1836:2: ( () ( ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) ) ) ) ( (lv_right_3_0= rulePairExpr ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1836:2: ( () ( ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1836:3: () ( ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1836:3: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1837:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElementAndSet(
                                  grammarAccess.getRelationalAccess().getExpressionLeftAction_1_0_0(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1842:2: ( ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1843:1: ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1843:1: ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1844:1: (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1844:1: (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' )
                    int alt30=6;
                    switch ( input.LA(1) ) {
                    case 62:
                        {
                        alt30=1;
                        }
                        break;
                    case 35:
                        {
                        alt30=2;
                        }
                        break;
                    case 63:
                        {
                        alt30=3;
                        }
                        break;
                    case 64:
                        {
                        alt30=4;
                        }
                        break;
                    case 41:
                        {
                        alt30=5;
                        }
                        break;
                    case 43:
                        {
                        alt30=6;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return current;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 30, 0, input);

                        throw nvae;
                    }

                    switch (alt30) {
                        case 1 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1845:3: lv_op_2_1= '!='
                            {
                            lv_op_2_1=(Token)match(input,62,FOLLOW_62_in_ruleRelational4063); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                                      newLeafNode(lv_op_2_1, grammarAccess.getRelationalAccess().getOpExclamationMarkEqualsSignKeyword_1_0_1_0_0());
                                  
                            }
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElement(grammarAccess.getRelationalRule());
                              	        }
                                     		setWithLastConsumed(current, "op", lv_op_2_1, null);
                              	    
                            }

                            }
                            break;
                        case 2 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1857:8: lv_op_2_2= '='
                            {
                            lv_op_2_2=(Token)match(input,35,FOLLOW_35_in_ruleRelational4092); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                                      newLeafNode(lv_op_2_2, grammarAccess.getRelationalAccess().getOpEqualsSignKeyword_1_0_1_0_1());
                                  
                            }
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElement(grammarAccess.getRelationalRule());
                              	        }
                                     		setWithLastConsumed(current, "op", lv_op_2_2, null);
                              	    
                            }

                            }
                            break;
                        case 3 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1869:8: lv_op_2_3= '>='
                            {
                            lv_op_2_3=(Token)match(input,63,FOLLOW_63_in_ruleRelational4121); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                                      newLeafNode(lv_op_2_3, grammarAccess.getRelationalAccess().getOpGreaterThanSignEqualsSignKeyword_1_0_1_0_2());
                                  
                            }
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElement(grammarAccess.getRelationalRule());
                              	        }
                                     		setWithLastConsumed(current, "op", lv_op_2_3, null);
                              	    
                            }

                            }
                            break;
                        case 4 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1881:8: lv_op_2_4= '<='
                            {
                            lv_op_2_4=(Token)match(input,64,FOLLOW_64_in_ruleRelational4150); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                                      newLeafNode(lv_op_2_4, grammarAccess.getRelationalAccess().getOpLessThanSignEqualsSignKeyword_1_0_1_0_3());
                                  
                            }
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElement(grammarAccess.getRelationalRule());
                              	        }
                                     		setWithLastConsumed(current, "op", lv_op_2_4, null);
                              	    
                            }

                            }
                            break;
                        case 5 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1893:8: lv_op_2_5= '<'
                            {
                            lv_op_2_5=(Token)match(input,41,FOLLOW_41_in_ruleRelational4179); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                                      newLeafNode(lv_op_2_5, grammarAccess.getRelationalAccess().getOpLessThanSignKeyword_1_0_1_0_4());
                                  
                            }
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElement(grammarAccess.getRelationalRule());
                              	        }
                                     		setWithLastConsumed(current, "op", lv_op_2_5, null);
                              	    
                            }

                            }
                            break;
                        case 6 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1905:8: lv_op_2_6= '>'
                            {
                            lv_op_2_6=(Token)match(input,43,FOLLOW_43_in_ruleRelational4208); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                                      newLeafNode(lv_op_2_6, grammarAccess.getRelationalAccess().getOpGreaterThanSignKeyword_1_0_1_0_5());
                                  
                            }
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElement(grammarAccess.getRelationalRule());
                              	        }
                                     		setWithLastConsumed(current, "op", lv_op_2_6, null);
                              	    
                            }

                            }
                            break;

                    }


                    }


                    }


                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1920:3: ( (lv_right_3_0= rulePairExpr ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1921:1: (lv_right_3_0= rulePairExpr )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1921:1: (lv_right_3_0= rulePairExpr )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1922:3: lv_right_3_0= rulePairExpr
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getRelationalAccess().getRightPairExprParserRuleCall_1_1_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_rulePairExpr_in_ruleRelational4246);
                    lv_right_3_0=rulePairExpr();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getRelationalRule());
                      	        }
                             		set(
                             			current, 
                             			"right",
                              		lv_right_3_0, 
                              		"PairExpr");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }
                    break;

            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleRelational"


    // $ANTLR start "entryRuleArgPairExpr"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1946:1: entryRuleArgPairExpr returns [EObject current=null] : iv_ruleArgPairExpr= ruleArgPairExpr EOF ;
    public final EObject entryRuleArgPairExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleArgPairExpr = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1947:2: (iv_ruleArgPairExpr= ruleArgPairExpr EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1948:2: iv_ruleArgPairExpr= ruleArgPairExpr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getArgPairExprRule()); 
            }
            pushFollow(FOLLOW_ruleArgPairExpr_in_entryRuleArgPairExpr4284);
            iv_ruleArgPairExpr=ruleArgPairExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleArgPairExpr; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleArgPairExpr4294); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleArgPairExpr"


    // $ANTLR start "ruleArgPairExpr"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1955:1: ruleArgPairExpr returns [EObject current=null] : ( () ( ( ( ( (lv_arg_1_1= RULE_ID | lv_arg_1_2= ruleBuiltInStatementKey ) ) ) ( (lv_op_2_0= '::' ) ) ) | ( ( (lv_arg_3_0= ruleDefinitionFacetKey ) ) ( (lv_op_4_0= ':' ) ) ) ) ( (lv_right_5_0= ruleAddition ) ) ) ;
    public final EObject ruleArgPairExpr() throws RecognitionException {
        EObject current = null;

        Token lv_arg_1_1=null;
        Token lv_op_2_0=null;
        Token lv_op_4_0=null;
        AntlrDatatypeRuleToken lv_arg_1_2 = null;

        AntlrDatatypeRuleToken lv_arg_3_0 = null;

        EObject lv_right_5_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1958:28: ( ( () ( ( ( ( (lv_arg_1_1= RULE_ID | lv_arg_1_2= ruleBuiltInStatementKey ) ) ) ( (lv_op_2_0= '::' ) ) ) | ( ( (lv_arg_3_0= ruleDefinitionFacetKey ) ) ( (lv_op_4_0= ':' ) ) ) ) ( (lv_right_5_0= ruleAddition ) ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1959:1: ( () ( ( ( ( (lv_arg_1_1= RULE_ID | lv_arg_1_2= ruleBuiltInStatementKey ) ) ) ( (lv_op_2_0= '::' ) ) ) | ( ( (lv_arg_3_0= ruleDefinitionFacetKey ) ) ( (lv_op_4_0= ':' ) ) ) ) ( (lv_right_5_0= ruleAddition ) ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1959:1: ( () ( ( ( ( (lv_arg_1_1= RULE_ID | lv_arg_1_2= ruleBuiltInStatementKey ) ) ) ( (lv_op_2_0= '::' ) ) ) | ( ( (lv_arg_3_0= ruleDefinitionFacetKey ) ) ( (lv_op_4_0= ':' ) ) ) ) ( (lv_right_5_0= ruleAddition ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1959:2: () ( ( ( ( (lv_arg_1_1= RULE_ID | lv_arg_1_2= ruleBuiltInStatementKey ) ) ) ( (lv_op_2_0= '::' ) ) ) | ( ( (lv_arg_3_0= ruleDefinitionFacetKey ) ) ( (lv_op_4_0= ':' ) ) ) ) ( (lv_right_5_0= ruleAddition ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1959:2: ()
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1960:5: 
            {
            if ( state.backtracking==0 ) {

                      current = forceCreateModelElement(
                          grammarAccess.getArgPairExprAccess().getArgPairExprAction_0(),
                          current);
                  
            }

            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1965:2: ( ( ( ( (lv_arg_1_1= RULE_ID | lv_arg_1_2= ruleBuiltInStatementKey ) ) ) ( (lv_op_2_0= '::' ) ) ) | ( ( (lv_arg_3_0= ruleDefinitionFacetKey ) ) ( (lv_op_4_0= ':' ) ) ) )
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==RULE_ID||(LA33_0>=16 && LA33_0<=34)) ) {
                alt33=1;
            }
            else if ( ((LA33_0>=56 && LA33_0<=58)) ) {
                alt33=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 33, 0, input);

                throw nvae;
            }
            switch (alt33) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1965:3: ( ( ( (lv_arg_1_1= RULE_ID | lv_arg_1_2= ruleBuiltInStatementKey ) ) ) ( (lv_op_2_0= '::' ) ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1965:3: ( ( ( (lv_arg_1_1= RULE_ID | lv_arg_1_2= ruleBuiltInStatementKey ) ) ) ( (lv_op_2_0= '::' ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1965:4: ( ( (lv_arg_1_1= RULE_ID | lv_arg_1_2= ruleBuiltInStatementKey ) ) ) ( (lv_op_2_0= '::' ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1965:4: ( ( (lv_arg_1_1= RULE_ID | lv_arg_1_2= ruleBuiltInStatementKey ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1966:1: ( (lv_arg_1_1= RULE_ID | lv_arg_1_2= ruleBuiltInStatementKey ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1966:1: ( (lv_arg_1_1= RULE_ID | lv_arg_1_2= ruleBuiltInStatementKey ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1967:1: (lv_arg_1_1= RULE_ID | lv_arg_1_2= ruleBuiltInStatementKey )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1967:1: (lv_arg_1_1= RULE_ID | lv_arg_1_2= ruleBuiltInStatementKey )
                    int alt32=2;
                    int LA32_0 = input.LA(1);

                    if ( (LA32_0==RULE_ID) ) {
                        alt32=1;
                    }
                    else if ( ((LA32_0>=16 && LA32_0<=34)) ) {
                        alt32=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return current;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 32, 0, input);

                        throw nvae;
                    }
                    switch (alt32) {
                        case 1 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1968:3: lv_arg_1_1= RULE_ID
                            {
                            lv_arg_1_1=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleArgPairExpr4349); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              			newLeafNode(lv_arg_1_1, grammarAccess.getArgPairExprAccess().getArgIDTerminalRuleCall_1_0_0_0_0()); 
                              		
                            }
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElement(grammarAccess.getArgPairExprRule());
                              	        }
                                     		setWithLastConsumed(
                                     			current, 
                                     			"arg",
                                      		lv_arg_1_1, 
                                      		"ID");
                              	    
                            }

                            }
                            break;
                        case 2 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1983:8: lv_arg_1_2= ruleBuiltInStatementKey
                            {
                            if ( state.backtracking==0 ) {
                               
                              	        newCompositeNode(grammarAccess.getArgPairExprAccess().getArgBuiltInStatementKeyParserRuleCall_1_0_0_0_1()); 
                              	    
                            }
                            pushFollow(FOLLOW_ruleBuiltInStatementKey_in_ruleArgPairExpr4373);
                            lv_arg_1_2=ruleBuiltInStatementKey();

                            state._fsp--;
                            if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElementForParent(grammarAccess.getArgPairExprRule());
                              	        }
                                     		set(
                                     			current, 
                                     			"arg",
                                      		lv_arg_1_2, 
                                      		"BuiltInStatementKey");
                              	        afterParserOrEnumRuleCall();
                              	    
                            }

                            }
                            break;

                    }


                    }


                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2001:2: ( (lv_op_2_0= '::' ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2002:1: (lv_op_2_0= '::' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2002:1: (lv_op_2_0= '::' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2003:3: lv_op_2_0= '::'
                    {
                    lv_op_2_0=(Token)match(input,65,FOLLOW_65_in_ruleArgPairExpr4394); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_op_2_0, grammarAccess.getArgPairExprAccess().getOpColonColonKeyword_1_0_1_0());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getArgPairExprRule());
                      	        }
                             		setWithLastConsumed(current, "op", lv_op_2_0, "::");
                      	    
                    }

                    }


                    }


                    }


                    }
                    break;
                case 2 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2017:6: ( ( (lv_arg_3_0= ruleDefinitionFacetKey ) ) ( (lv_op_4_0= ':' ) ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2017:6: ( ( (lv_arg_3_0= ruleDefinitionFacetKey ) ) ( (lv_op_4_0= ':' ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2017:7: ( (lv_arg_3_0= ruleDefinitionFacetKey ) ) ( (lv_op_4_0= ':' ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2017:7: ( (lv_arg_3_0= ruleDefinitionFacetKey ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2018:1: (lv_arg_3_0= ruleDefinitionFacetKey )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2018:1: (lv_arg_3_0= ruleDefinitionFacetKey )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2019:3: lv_arg_3_0= ruleDefinitionFacetKey
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getArgPairExprAccess().getArgDefinitionFacetKeyParserRuleCall_1_1_0_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleDefinitionFacetKey_in_ruleArgPairExpr4436);
                    lv_arg_3_0=ruleDefinitionFacetKey();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getArgPairExprRule());
                      	        }
                             		set(
                             			current, 
                             			"arg",
                              		lv_arg_3_0, 
                              		"DefinitionFacetKey");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2035:2: ( (lv_op_4_0= ':' ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2036:1: (lv_op_4_0= ':' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2036:1: (lv_op_4_0= ':' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2037:3: lv_op_4_0= ':'
                    {
                    lv_op_4_0=(Token)match(input,40,FOLLOW_40_in_ruleArgPairExpr4454); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_op_4_0, grammarAccess.getArgPairExprAccess().getOpColonKeyword_1_1_1_0());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getArgPairExprRule());
                      	        }
                             		setWithLastConsumed(current, "op", lv_op_4_0, ":");
                      	    
                    }

                    }


                    }


                    }


                    }
                    break;

            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2050:4: ( (lv_right_5_0= ruleAddition ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2051:1: (lv_right_5_0= ruleAddition )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2051:1: (lv_right_5_0= ruleAddition )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2052:3: lv_right_5_0= ruleAddition
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getArgPairExprAccess().getRightAdditionParserRuleCall_2_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleAddition_in_ruleArgPairExpr4490);
            lv_right_5_0=ruleAddition();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getArgPairExprRule());
              	        }
                     		set(
                     			current, 
                     			"right",
                      		lv_right_5_0, 
                      		"Addition");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleArgPairExpr"


    // $ANTLR start "entryRulePairExpr"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2076:1: entryRulePairExpr returns [EObject current=null] : iv_rulePairExpr= rulePairExpr EOF ;
    public final EObject entryRulePairExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePairExpr = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2077:2: (iv_rulePairExpr= rulePairExpr EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2078:2: iv_rulePairExpr= rulePairExpr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getPairExprRule()); 
            }
            pushFollow(FOLLOW_rulePairExpr_in_entryRulePairExpr4526);
            iv_rulePairExpr=rulePairExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_rulePairExpr; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRulePairExpr4536); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRulePairExpr"


    // $ANTLR start "rulePairExpr"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2085:1: rulePairExpr returns [EObject current=null] : ( ( ( ruleArgPairExpr )=>this_ArgPairExpr_0= ruleArgPairExpr ) | (this_Addition_1= ruleAddition ( ( () ( (lv_op_3_0= '::' ) ) ) ( (lv_right_4_0= ruleAddition ) ) )? ) ) ;
    public final EObject rulePairExpr() throws RecognitionException {
        EObject current = null;

        Token lv_op_3_0=null;
        EObject this_ArgPairExpr_0 = null;

        EObject this_Addition_1 = null;

        EObject lv_right_4_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2088:28: ( ( ( ( ruleArgPairExpr )=>this_ArgPairExpr_0= ruleArgPairExpr ) | (this_Addition_1= ruleAddition ( ( () ( (lv_op_3_0= '::' ) ) ) ( (lv_right_4_0= ruleAddition ) ) )? ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2089:1: ( ( ( ruleArgPairExpr )=>this_ArgPairExpr_0= ruleArgPairExpr ) | (this_Addition_1= ruleAddition ( ( () ( (lv_op_3_0= '::' ) ) ) ( (lv_right_4_0= ruleAddition ) ) )? ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2089:1: ( ( ( ruleArgPairExpr )=>this_ArgPairExpr_0= ruleArgPairExpr ) | (this_Addition_1= ruleAddition ( ( () ( (lv_op_3_0= '::' ) ) ) ( (lv_right_4_0= ruleAddition ) ) )? ) )
            int alt35=2;
            alt35 = dfa35.predict(input);
            switch (alt35) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2089:2: ( ( ruleArgPairExpr )=>this_ArgPairExpr_0= ruleArgPairExpr )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2089:2: ( ( ruleArgPairExpr )=>this_ArgPairExpr_0= ruleArgPairExpr )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2089:3: ( ruleArgPairExpr )=>this_ArgPairExpr_0= ruleArgPairExpr
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getPairExprAccess().getArgPairExprParserRuleCall_0()); 
                          
                    }
                    pushFollow(FOLLOW_ruleArgPairExpr_in_rulePairExpr4589);
                    this_ArgPairExpr_0=ruleArgPairExpr();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_ArgPairExpr_0; 
                              afterParserOrEnumRuleCall();
                          
                    }

                    }


                    }
                    break;
                case 2 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2099:6: (this_Addition_1= ruleAddition ( ( () ( (lv_op_3_0= '::' ) ) ) ( (lv_right_4_0= ruleAddition ) ) )? )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2099:6: (this_Addition_1= ruleAddition ( ( () ( (lv_op_3_0= '::' ) ) ) ( (lv_right_4_0= ruleAddition ) ) )? )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2100:5: this_Addition_1= ruleAddition ( ( () ( (lv_op_3_0= '::' ) ) ) ( (lv_right_4_0= ruleAddition ) ) )?
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getPairExprAccess().getAdditionParserRuleCall_1_0()); 
                          
                    }
                    pushFollow(FOLLOW_ruleAddition_in_rulePairExpr4618);
                    this_Addition_1=ruleAddition();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_Addition_1; 
                              afterParserOrEnumRuleCall();
                          
                    }
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2108:1: ( ( () ( (lv_op_3_0= '::' ) ) ) ( (lv_right_4_0= ruleAddition ) ) )?
                    int alt34=2;
                    int LA34_0 = input.LA(1);

                    if ( (LA34_0==65) ) {
                        alt34=1;
                    }
                    switch (alt34) {
                        case 1 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2108:2: ( () ( (lv_op_3_0= '::' ) ) ) ( (lv_right_4_0= ruleAddition ) )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2108:2: ( () ( (lv_op_3_0= '::' ) ) )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2108:3: () ( (lv_op_3_0= '::' ) )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2108:3: ()
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2109:5: 
                            {
                            if ( state.backtracking==0 ) {

                                      current = forceCreateModelElementAndSet(
                                          grammarAccess.getPairExprAccess().getPairExprLeftAction_1_1_0_0(),
                                          current);
                                  
                            }

                            }

                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2114:2: ( (lv_op_3_0= '::' ) )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2115:1: (lv_op_3_0= '::' )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2115:1: (lv_op_3_0= '::' )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2116:3: lv_op_3_0= '::'
                            {
                            lv_op_3_0=(Token)match(input,65,FOLLOW_65_in_rulePairExpr4646); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                                      newLeafNode(lv_op_3_0, grammarAccess.getPairExprAccess().getOpColonColonKeyword_1_1_0_1_0());
                                  
                            }
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElement(grammarAccess.getPairExprRule());
                              	        }
                                     		setWithLastConsumed(current, "op", lv_op_3_0, "::");
                              	    
                            }

                            }


                            }


                            }

                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2129:3: ( (lv_right_4_0= ruleAddition ) )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2130:1: (lv_right_4_0= ruleAddition )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2130:1: (lv_right_4_0= ruleAddition )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2131:3: lv_right_4_0= ruleAddition
                            {
                            if ( state.backtracking==0 ) {
                               
                              	        newCompositeNode(grammarAccess.getPairExprAccess().getRightAdditionParserRuleCall_1_1_1_0()); 
                              	    
                            }
                            pushFollow(FOLLOW_ruleAddition_in_rulePairExpr4681);
                            lv_right_4_0=ruleAddition();

                            state._fsp--;
                            if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElementForParent(grammarAccess.getPairExprRule());
                              	        }
                                     		set(
                                     			current, 
                                     			"right",
                                      		lv_right_4_0, 
                                      		"Addition");
                              	        afterParserOrEnumRuleCall();
                              	    
                            }

                            }


                            }


                            }
                            break;

                    }


                    }


                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "rulePairExpr"


    // $ANTLR start "entryRuleAddition"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2155:1: entryRuleAddition returns [EObject current=null] : iv_ruleAddition= ruleAddition EOF ;
    public final EObject entryRuleAddition() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAddition = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2156:2: (iv_ruleAddition= ruleAddition EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2157:2: iv_ruleAddition= ruleAddition EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAdditionRule()); 
            }
            pushFollow(FOLLOW_ruleAddition_in_entryRuleAddition4720);
            iv_ruleAddition=ruleAddition();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAddition; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleAddition4730); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAddition"


    // $ANTLR start "ruleAddition"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2164:1: ruleAddition returns [EObject current=null] : (this_Multiplication_0= ruleMultiplication ( ( () ( ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) ) ) ) ( (lv_right_3_0= ruleMultiplication ) ) )* ) ;
    public final EObject ruleAddition() throws RecognitionException {
        EObject current = null;

        Token lv_op_2_1=null;
        Token lv_op_2_2=null;
        EObject this_Multiplication_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2167:28: ( (this_Multiplication_0= ruleMultiplication ( ( () ( ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) ) ) ) ( (lv_right_3_0= ruleMultiplication ) ) )* ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2168:1: (this_Multiplication_0= ruleMultiplication ( ( () ( ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) ) ) ) ( (lv_right_3_0= ruleMultiplication ) ) )* )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2168:1: (this_Multiplication_0= ruleMultiplication ( ( () ( ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) ) ) ) ( (lv_right_3_0= ruleMultiplication ) ) )* )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2169:5: this_Multiplication_0= ruleMultiplication ( ( () ( ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) ) ) ) ( (lv_right_3_0= ruleMultiplication ) ) )*
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getAdditionAccess().getMultiplicationParserRuleCall_0()); 
                  
            }
            pushFollow(FOLLOW_ruleMultiplication_in_ruleAddition4777);
            this_Multiplication_0=ruleMultiplication();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_Multiplication_0; 
                      afterParserOrEnumRuleCall();
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2177:1: ( ( () ( ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) ) ) ) ( (lv_right_3_0= ruleMultiplication ) ) )*
            loop37:
            do {
                int alt37=2;
                int LA37_0 = input.LA(1);

                if ( ((LA37_0>=66 && LA37_0<=67)) ) {
                    alt37=1;
                }


                switch (alt37) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2177:2: ( () ( ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) ) ) ) ( (lv_right_3_0= ruleMultiplication ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2177:2: ( () ( ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) ) ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2177:3: () ( ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2177:3: ()
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2178:5: 
            	    {
            	    if ( state.backtracking==0 ) {

            	              current = forceCreateModelElementAndSet(
            	                  grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0(),
            	                  current);
            	          
            	    }

            	    }

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2183:2: ( ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2184:1: ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2184:1: ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2185:1: (lv_op_2_1= '+' | lv_op_2_2= '-' )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2185:1: (lv_op_2_1= '+' | lv_op_2_2= '-' )
            	    int alt36=2;
            	    int LA36_0 = input.LA(1);

            	    if ( (LA36_0==66) ) {
            	        alt36=1;
            	    }
            	    else if ( (LA36_0==67) ) {
            	        alt36=2;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 36, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt36) {
            	        case 1 :
            	            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2186:3: lv_op_2_1= '+'
            	            {
            	            lv_op_2_1=(Token)match(input,66,FOLLOW_66_in_ruleAddition4807); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	                      newLeafNode(lv_op_2_1, grammarAccess.getAdditionAccess().getOpPlusSignKeyword_1_0_1_0_0());
            	                  
            	            }
            	            if ( state.backtracking==0 ) {

            	              	        if (current==null) {
            	              	            current = createModelElement(grammarAccess.getAdditionRule());
            	              	        }
            	                     		setWithLastConsumed(current, "op", lv_op_2_1, null);
            	              	    
            	            }

            	            }
            	            break;
            	        case 2 :
            	            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2198:8: lv_op_2_2= '-'
            	            {
            	            lv_op_2_2=(Token)match(input,67,FOLLOW_67_in_ruleAddition4836); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	                      newLeafNode(lv_op_2_2, grammarAccess.getAdditionAccess().getOpHyphenMinusKeyword_1_0_1_0_1());
            	                  
            	            }
            	            if ( state.backtracking==0 ) {

            	              	        if (current==null) {
            	              	            current = createModelElement(grammarAccess.getAdditionRule());
            	              	        }
            	                     		setWithLastConsumed(current, "op", lv_op_2_2, null);
            	              	    
            	            }

            	            }
            	            break;

            	    }


            	    }


            	    }


            	    }

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2213:3: ( (lv_right_3_0= ruleMultiplication ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2214:1: (lv_right_3_0= ruleMultiplication )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2214:1: (lv_right_3_0= ruleMultiplication )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2215:3: lv_right_3_0= ruleMultiplication
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getAdditionAccess().getRightMultiplicationParserRuleCall_1_1_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleMultiplication_in_ruleAddition4874);
            	    lv_right_3_0=ruleMultiplication();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getAdditionRule());
            	      	        }
            	             		set(
            	             			current, 
            	             			"right",
            	              		lv_right_3_0, 
            	              		"Multiplication");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop37;
                }
            } while (true);


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAddition"


    // $ANTLR start "entryRuleMultiplication"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2239:1: entryRuleMultiplication returns [EObject current=null] : iv_ruleMultiplication= ruleMultiplication EOF ;
    public final EObject entryRuleMultiplication() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleMultiplication = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2240:2: (iv_ruleMultiplication= ruleMultiplication EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2241:2: iv_ruleMultiplication= ruleMultiplication EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getMultiplicationRule()); 
            }
            pushFollow(FOLLOW_ruleMultiplication_in_entryRuleMultiplication4912);
            iv_ruleMultiplication=ruleMultiplication();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleMultiplication; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleMultiplication4922); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleMultiplication"


    // $ANTLR start "ruleMultiplication"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2248:1: ruleMultiplication returns [EObject current=null] : (this_GamlBinaryExpr_0= ruleGamlBinaryExpr ( ( () ( ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) ) ) ) ( (lv_right_3_0= ruleGamlBinaryExpr ) ) )* ) ;
    public final EObject ruleMultiplication() throws RecognitionException {
        EObject current = null;

        Token lv_op_2_1=null;
        Token lv_op_2_2=null;
        Token lv_op_2_3=null;
        EObject this_GamlBinaryExpr_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2251:28: ( (this_GamlBinaryExpr_0= ruleGamlBinaryExpr ( ( () ( ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) ) ) ) ( (lv_right_3_0= ruleGamlBinaryExpr ) ) )* ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2252:1: (this_GamlBinaryExpr_0= ruleGamlBinaryExpr ( ( () ( ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) ) ) ) ( (lv_right_3_0= ruleGamlBinaryExpr ) ) )* )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2252:1: (this_GamlBinaryExpr_0= ruleGamlBinaryExpr ( ( () ( ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) ) ) ) ( (lv_right_3_0= ruleGamlBinaryExpr ) ) )* )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2253:5: this_GamlBinaryExpr_0= ruleGamlBinaryExpr ( ( () ( ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) ) ) ) ( (lv_right_3_0= ruleGamlBinaryExpr ) ) )*
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getMultiplicationAccess().getGamlBinaryExprParserRuleCall_0()); 
                  
            }
            pushFollow(FOLLOW_ruleGamlBinaryExpr_in_ruleMultiplication4969);
            this_GamlBinaryExpr_0=ruleGamlBinaryExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_GamlBinaryExpr_0; 
                      afterParserOrEnumRuleCall();
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2261:1: ( ( () ( ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) ) ) ) ( (lv_right_3_0= ruleGamlBinaryExpr ) ) )*
            loop39:
            do {
                int alt39=2;
                int LA39_0 = input.LA(1);

                if ( ((LA39_0>=68 && LA39_0<=70)) ) {
                    alt39=1;
                }


                switch (alt39) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2261:2: ( () ( ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) ) ) ) ( (lv_right_3_0= ruleGamlBinaryExpr ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2261:2: ( () ( ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) ) ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2261:3: () ( ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2261:3: ()
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2262:5: 
            	    {
            	    if ( state.backtracking==0 ) {

            	              current = forceCreateModelElementAndSet(
            	                  grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0(),
            	                  current);
            	          
            	    }

            	    }

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2267:2: ( ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2268:1: ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2268:1: ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2269:1: (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2269:1: (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' )
            	    int alt38=3;
            	    switch ( input.LA(1) ) {
            	    case 68:
            	        {
            	        alt38=1;
            	        }
            	        break;
            	    case 69:
            	        {
            	        alt38=2;
            	        }
            	        break;
            	    case 70:
            	        {
            	        alt38=3;
            	        }
            	        break;
            	    default:
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 38, 0, input);

            	        throw nvae;
            	    }

            	    switch (alt38) {
            	        case 1 :
            	            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2270:3: lv_op_2_1= '*'
            	            {
            	            lv_op_2_1=(Token)match(input,68,FOLLOW_68_in_ruleMultiplication4999); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	                      newLeafNode(lv_op_2_1, grammarAccess.getMultiplicationAccess().getOpAsteriskKeyword_1_0_1_0_0());
            	                  
            	            }
            	            if ( state.backtracking==0 ) {

            	              	        if (current==null) {
            	              	            current = createModelElement(grammarAccess.getMultiplicationRule());
            	              	        }
            	                     		setWithLastConsumed(current, "op", lv_op_2_1, null);
            	              	    
            	            }

            	            }
            	            break;
            	        case 2 :
            	            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2282:8: lv_op_2_2= '/'
            	            {
            	            lv_op_2_2=(Token)match(input,69,FOLLOW_69_in_ruleMultiplication5028); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	                      newLeafNode(lv_op_2_2, grammarAccess.getMultiplicationAccess().getOpSolidusKeyword_1_0_1_0_1());
            	                  
            	            }
            	            if ( state.backtracking==0 ) {

            	              	        if (current==null) {
            	              	            current = createModelElement(grammarAccess.getMultiplicationRule());
            	              	        }
            	                     		setWithLastConsumed(current, "op", lv_op_2_2, null);
            	              	    
            	            }

            	            }
            	            break;
            	        case 3 :
            	            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2294:8: lv_op_2_3= '^'
            	            {
            	            lv_op_2_3=(Token)match(input,70,FOLLOW_70_in_ruleMultiplication5057); if (state.failed) return current;
            	            if ( state.backtracking==0 ) {

            	                      newLeafNode(lv_op_2_3, grammarAccess.getMultiplicationAccess().getOpCircumflexAccentKeyword_1_0_1_0_2());
            	                  
            	            }
            	            if ( state.backtracking==0 ) {

            	              	        if (current==null) {
            	              	            current = createModelElement(grammarAccess.getMultiplicationRule());
            	              	        }
            	                     		setWithLastConsumed(current, "op", lv_op_2_3, null);
            	              	    
            	            }

            	            }
            	            break;

            	    }


            	    }


            	    }


            	    }

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2309:3: ( (lv_right_3_0= ruleGamlBinaryExpr ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2310:1: (lv_right_3_0= ruleGamlBinaryExpr )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2310:1: (lv_right_3_0= ruleGamlBinaryExpr )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2311:3: lv_right_3_0= ruleGamlBinaryExpr
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getMultiplicationAccess().getRightGamlBinaryExprParserRuleCall_1_1_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleGamlBinaryExpr_in_ruleMultiplication5095);
            	    lv_right_3_0=ruleGamlBinaryExpr();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getMultiplicationRule());
            	      	        }
            	             		set(
            	             			current, 
            	             			"right",
            	              		lv_right_3_0, 
            	              		"GamlBinaryExpr");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop39;
                }
            } while (true);


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleMultiplication"


    // $ANTLR start "entryRuleGamlBinaryExpr"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2335:1: entryRuleGamlBinaryExpr returns [EObject current=null] : iv_ruleGamlBinaryExpr= ruleGamlBinaryExpr EOF ;
    public final EObject entryRuleGamlBinaryExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleGamlBinaryExpr = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2336:2: (iv_ruleGamlBinaryExpr= ruleGamlBinaryExpr EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2337:2: iv_ruleGamlBinaryExpr= ruleGamlBinaryExpr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getGamlBinaryExprRule()); 
            }
            pushFollow(FOLLOW_ruleGamlBinaryExpr_in_entryRuleGamlBinaryExpr5133);
            iv_ruleGamlBinaryExpr=ruleGamlBinaryExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleGamlBinaryExpr; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleGamlBinaryExpr5143); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleGamlBinaryExpr"


    // $ANTLR start "ruleGamlBinaryExpr"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2344:1: ruleGamlBinaryExpr returns [EObject current=null] : (this_GamlUnitExpr_0= ruleGamlUnitExpr ( ( () ( (lv_op_2_0= RULE_ID ) ) ) ( (lv_right_3_0= ruleGamlUnitExpr ) ) )* ) ;
    public final EObject ruleGamlBinaryExpr() throws RecognitionException {
        EObject current = null;

        Token lv_op_2_0=null;
        EObject this_GamlUnitExpr_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2347:28: ( (this_GamlUnitExpr_0= ruleGamlUnitExpr ( ( () ( (lv_op_2_0= RULE_ID ) ) ) ( (lv_right_3_0= ruleGamlUnitExpr ) ) )* ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2348:1: (this_GamlUnitExpr_0= ruleGamlUnitExpr ( ( () ( (lv_op_2_0= RULE_ID ) ) ) ( (lv_right_3_0= ruleGamlUnitExpr ) ) )* )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2348:1: (this_GamlUnitExpr_0= ruleGamlUnitExpr ( ( () ( (lv_op_2_0= RULE_ID ) ) ) ( (lv_right_3_0= ruleGamlUnitExpr ) ) )* )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2349:5: this_GamlUnitExpr_0= ruleGamlUnitExpr ( ( () ( (lv_op_2_0= RULE_ID ) ) ) ( (lv_right_3_0= ruleGamlUnitExpr ) ) )*
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getGamlBinaryExprAccess().getGamlUnitExprParserRuleCall_0()); 
                  
            }
            pushFollow(FOLLOW_ruleGamlUnitExpr_in_ruleGamlBinaryExpr5190);
            this_GamlUnitExpr_0=ruleGamlUnitExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_GamlUnitExpr_0; 
                      afterParserOrEnumRuleCall();
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2357:1: ( ( () ( (lv_op_2_0= RULE_ID ) ) ) ( (lv_right_3_0= ruleGamlUnitExpr ) ) )*
            loop40:
            do {
                int alt40=2;
                int LA40_0 = input.LA(1);

                if ( (LA40_0==RULE_ID) ) {
                    int LA40_2 = input.LA(2);

                    if ( ((LA40_2>=RULE_ID && LA40_2<=RULE_BOOLEAN)||LA40_2==54||LA40_2==67||(LA40_2>=72 && LA40_2<=77)||LA40_2==80) ) {
                        alt40=1;
                    }


                }


                switch (alt40) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2357:2: ( () ( (lv_op_2_0= RULE_ID ) ) ) ( (lv_right_3_0= ruleGamlUnitExpr ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2357:2: ( () ( (lv_op_2_0= RULE_ID ) ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2357:3: () ( (lv_op_2_0= RULE_ID ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2357:3: ()
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2358:5: 
            	    {
            	    if ( state.backtracking==0 ) {

            	              current = forceCreateModelElementAndSet(
            	                  grammarAccess.getGamlBinaryExprAccess().getGamlBinaryExprLeftAction_1_0_0(),
            	                  current);
            	          
            	    }

            	    }

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2363:2: ( (lv_op_2_0= RULE_ID ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2364:1: (lv_op_2_0= RULE_ID )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2364:1: (lv_op_2_0= RULE_ID )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2365:3: lv_op_2_0= RULE_ID
            	    {
            	    lv_op_2_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleGamlBinaryExpr5217); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      			newLeafNode(lv_op_2_0, grammarAccess.getGamlBinaryExprAccess().getOpIDTerminalRuleCall_1_0_1_0()); 
            	      		
            	    }
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElement(grammarAccess.getGamlBinaryExprRule());
            	      	        }
            	             		setWithLastConsumed(
            	             			current, 
            	             			"op",
            	              		lv_op_2_0, 
            	              		"ID");
            	      	    
            	    }

            	    }


            	    }


            	    }

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2381:3: ( (lv_right_3_0= ruleGamlUnitExpr ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2382:1: (lv_right_3_0= ruleGamlUnitExpr )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2382:1: (lv_right_3_0= ruleGamlUnitExpr )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2383:3: lv_right_3_0= ruleGamlUnitExpr
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getGamlBinaryExprAccess().getRightGamlUnitExprParserRuleCall_1_1_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleGamlUnitExpr_in_ruleGamlBinaryExpr5244);
            	    lv_right_3_0=ruleGamlUnitExpr();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getGamlBinaryExprRule());
            	      	        }
            	             		set(
            	             			current, 
            	             			"right",
            	              		lv_right_3_0, 
            	              		"GamlUnitExpr");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop40;
                }
            } while (true);


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleGamlBinaryExpr"


    // $ANTLR start "entryRuleGamlUnitExpr"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2407:1: entryRuleGamlUnitExpr returns [EObject current=null] : iv_ruleGamlUnitExpr= ruleGamlUnitExpr EOF ;
    public final EObject entryRuleGamlUnitExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleGamlUnitExpr = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2408:2: (iv_ruleGamlUnitExpr= ruleGamlUnitExpr EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2409:2: iv_ruleGamlUnitExpr= ruleGamlUnitExpr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getGamlUnitExprRule()); 
            }
            pushFollow(FOLLOW_ruleGamlUnitExpr_in_entryRuleGamlUnitExpr5282);
            iv_ruleGamlUnitExpr=ruleGamlUnitExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleGamlUnitExpr; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleGamlUnitExpr5292); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleGamlUnitExpr"


    // $ANTLR start "ruleGamlUnitExpr"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2416:1: ruleGamlUnitExpr returns [EObject current=null] : (this_GamlUnaryExpr_0= ruleGamlUnaryExpr ( ( () ( ( (lv_op_2_1= '#' | lv_op_2_2= '\\u00B0' ) ) ) ) ( (lv_right_3_0= ruleUnitName ) ) )? ) ;
    public final EObject ruleGamlUnitExpr() throws RecognitionException {
        EObject current = null;

        Token lv_op_2_1=null;
        Token lv_op_2_2=null;
        EObject this_GamlUnaryExpr_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2419:28: ( (this_GamlUnaryExpr_0= ruleGamlUnaryExpr ( ( () ( ( (lv_op_2_1= '#' | lv_op_2_2= '\\u00B0' ) ) ) ) ( (lv_right_3_0= ruleUnitName ) ) )? ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2420:1: (this_GamlUnaryExpr_0= ruleGamlUnaryExpr ( ( () ( ( (lv_op_2_1= '#' | lv_op_2_2= '\\u00B0' ) ) ) ) ( (lv_right_3_0= ruleUnitName ) ) )? )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2420:1: (this_GamlUnaryExpr_0= ruleGamlUnaryExpr ( ( () ( ( (lv_op_2_1= '#' | lv_op_2_2= '\\u00B0' ) ) ) ) ( (lv_right_3_0= ruleUnitName ) ) )? )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2421:5: this_GamlUnaryExpr_0= ruleGamlUnaryExpr ( ( () ( ( (lv_op_2_1= '#' | lv_op_2_2= '\\u00B0' ) ) ) ) ( (lv_right_3_0= ruleUnitName ) ) )?
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getGamlUnitExprAccess().getGamlUnaryExprParserRuleCall_0()); 
                  
            }
            pushFollow(FOLLOW_ruleGamlUnaryExpr_in_ruleGamlUnitExpr5339);
            this_GamlUnaryExpr_0=ruleGamlUnaryExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_GamlUnaryExpr_0; 
                      afterParserOrEnumRuleCall();
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2429:1: ( ( () ( ( (lv_op_2_1= '#' | lv_op_2_2= '\\u00B0' ) ) ) ) ( (lv_right_3_0= ruleUnitName ) ) )?
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( ((LA42_0>=71 && LA42_0<=72)) ) {
                alt42=1;
            }
            switch (alt42) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2429:2: ( () ( ( (lv_op_2_1= '#' | lv_op_2_2= '\\u00B0' ) ) ) ) ( (lv_right_3_0= ruleUnitName ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2429:2: ( () ( ( (lv_op_2_1= '#' | lv_op_2_2= '\\u00B0' ) ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2429:3: () ( ( (lv_op_2_1= '#' | lv_op_2_2= '\\u00B0' ) ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2429:3: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2430:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElementAndSet(
                                  grammarAccess.getGamlUnitExprAccess().getGamlUnitExprLeftAction_1_0_0(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2435:2: ( ( (lv_op_2_1= '#' | lv_op_2_2= '\\u00B0' ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2436:1: ( (lv_op_2_1= '#' | lv_op_2_2= '\\u00B0' ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2436:1: ( (lv_op_2_1= '#' | lv_op_2_2= '\\u00B0' ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2437:1: (lv_op_2_1= '#' | lv_op_2_2= '\\u00B0' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2437:1: (lv_op_2_1= '#' | lv_op_2_2= '\\u00B0' )
                    int alt41=2;
                    int LA41_0 = input.LA(1);

                    if ( (LA41_0==71) ) {
                        alt41=1;
                    }
                    else if ( (LA41_0==72) ) {
                        alt41=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return current;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 41, 0, input);

                        throw nvae;
                    }
                    switch (alt41) {
                        case 1 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2438:3: lv_op_2_1= '#'
                            {
                            lv_op_2_1=(Token)match(input,71,FOLLOW_71_in_ruleGamlUnitExpr5369); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                                      newLeafNode(lv_op_2_1, grammarAccess.getGamlUnitExprAccess().getOpNumberSignKeyword_1_0_1_0_0());
                                  
                            }
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElement(grammarAccess.getGamlUnitExprRule());
                              	        }
                                     		setWithLastConsumed(current, "op", lv_op_2_1, null);
                              	    
                            }

                            }
                            break;
                        case 2 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2450:8: lv_op_2_2= '\\u00B0'
                            {
                            lv_op_2_2=(Token)match(input,72,FOLLOW_72_in_ruleGamlUnitExpr5398); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                                      newLeafNode(lv_op_2_2, grammarAccess.getGamlUnitExprAccess().getOpDegreeSignKeyword_1_0_1_0_1());
                                  
                            }
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElement(grammarAccess.getGamlUnitExprRule());
                              	        }
                                     		setWithLastConsumed(current, "op", lv_op_2_2, null);
                              	    
                            }

                            }
                            break;

                    }


                    }


                    }


                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2465:3: ( (lv_right_3_0= ruleUnitName ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2466:1: (lv_right_3_0= ruleUnitName )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2466:1: (lv_right_3_0= ruleUnitName )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2467:3: lv_right_3_0= ruleUnitName
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getGamlUnitExprAccess().getRightUnitNameParserRuleCall_1_1_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleUnitName_in_ruleGamlUnitExpr5436);
                    lv_right_3_0=ruleUnitName();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getGamlUnitExprRule());
                      	        }
                             		set(
                             			current, 
                             			"right",
                              		lv_right_3_0, 
                              		"UnitName");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }
                    break;

            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleGamlUnitExpr"


    // $ANTLR start "entryRuleGamlUnaryExpr"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2491:1: entryRuleGamlUnaryExpr returns [EObject current=null] : iv_ruleGamlUnaryExpr= ruleGamlUnaryExpr EOF ;
    public final EObject entryRuleGamlUnaryExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleGamlUnaryExpr = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2492:2: (iv_ruleGamlUnaryExpr= ruleGamlUnaryExpr EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2493:2: iv_ruleGamlUnaryExpr= ruleGamlUnaryExpr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getGamlUnaryExprRule()); 
            }
            pushFollow(FOLLOW_ruleGamlUnaryExpr_in_entryRuleGamlUnaryExpr5474);
            iv_ruleGamlUnaryExpr=ruleGamlUnaryExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleGamlUnaryExpr; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleGamlUnaryExpr5484); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleGamlUnaryExpr"


    // $ANTLR start "ruleGamlUnaryExpr"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2500:1: ruleGamlUnaryExpr returns [EObject current=null] : (this_Access_0= ruleAccess | ( () ( ( () ( ( (lv_op_3_0= '\\u00B0' ) ) ( (lv_right_4_0= ruleUnitName ) ) ) ) | ( ( ( (lv_op_5_1= '-' | lv_op_5_2= '!' | lv_op_5_3= 'my' | lv_op_5_4= 'the' | lv_op_5_5= 'not' ) ) ) ( (lv_right_6_0= ruleGamlUnaryExpr ) ) ) ) ) ) ;
    public final EObject ruleGamlUnaryExpr() throws RecognitionException {
        EObject current = null;

        Token lv_op_3_0=null;
        Token lv_op_5_1=null;
        Token lv_op_5_2=null;
        Token lv_op_5_3=null;
        Token lv_op_5_4=null;
        Token lv_op_5_5=null;
        EObject this_Access_0 = null;

        EObject lv_right_4_0 = null;

        EObject lv_right_6_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2503:28: ( (this_Access_0= ruleAccess | ( () ( ( () ( ( (lv_op_3_0= '\\u00B0' ) ) ( (lv_right_4_0= ruleUnitName ) ) ) ) | ( ( ( (lv_op_5_1= '-' | lv_op_5_2= '!' | lv_op_5_3= 'my' | lv_op_5_4= 'the' | lv_op_5_5= 'not' ) ) ) ( (lv_right_6_0= ruleGamlUnaryExpr ) ) ) ) ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2504:1: (this_Access_0= ruleAccess | ( () ( ( () ( ( (lv_op_3_0= '\\u00B0' ) ) ( (lv_right_4_0= ruleUnitName ) ) ) ) | ( ( ( (lv_op_5_1= '-' | lv_op_5_2= '!' | lv_op_5_3= 'my' | lv_op_5_4= 'the' | lv_op_5_5= 'not' ) ) ) ( (lv_right_6_0= ruleGamlUnaryExpr ) ) ) ) ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2504:1: (this_Access_0= ruleAccess | ( () ( ( () ( ( (lv_op_3_0= '\\u00B0' ) ) ( (lv_right_4_0= ruleUnitName ) ) ) ) | ( ( ( (lv_op_5_1= '-' | lv_op_5_2= '!' | lv_op_5_3= 'my' | lv_op_5_4= 'the' | lv_op_5_5= 'not' ) ) ) ( (lv_right_6_0= ruleGamlUnaryExpr ) ) ) ) ) )
            int alt45=2;
            int LA45_0 = input.LA(1);

            if ( ((LA45_0>=RULE_ID && LA45_0<=RULE_BOOLEAN)||LA45_0==54||LA45_0==77||LA45_0==80) ) {
                alt45=1;
            }
            else if ( (LA45_0==67||(LA45_0>=72 && LA45_0<=76)) ) {
                alt45=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 45, 0, input);

                throw nvae;
            }
            switch (alt45) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2505:5: this_Access_0= ruleAccess
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getGamlUnaryExprAccess().getAccessParserRuleCall_0()); 
                          
                    }
                    pushFollow(FOLLOW_ruleAccess_in_ruleGamlUnaryExpr5531);
                    this_Access_0=ruleAccess();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_Access_0; 
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 2 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2514:6: ( () ( ( () ( ( (lv_op_3_0= '\\u00B0' ) ) ( (lv_right_4_0= ruleUnitName ) ) ) ) | ( ( ( (lv_op_5_1= '-' | lv_op_5_2= '!' | lv_op_5_3= 'my' | lv_op_5_4= 'the' | lv_op_5_5= 'not' ) ) ) ( (lv_right_6_0= ruleGamlUnaryExpr ) ) ) ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2514:6: ( () ( ( () ( ( (lv_op_3_0= '\\u00B0' ) ) ( (lv_right_4_0= ruleUnitName ) ) ) ) | ( ( ( (lv_op_5_1= '-' | lv_op_5_2= '!' | lv_op_5_3= 'my' | lv_op_5_4= 'the' | lv_op_5_5= 'not' ) ) ) ( (lv_right_6_0= ruleGamlUnaryExpr ) ) ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2514:7: () ( ( () ( ( (lv_op_3_0= '\\u00B0' ) ) ( (lv_right_4_0= ruleUnitName ) ) ) ) | ( ( ( (lv_op_5_1= '-' | lv_op_5_2= '!' | lv_op_5_3= 'my' | lv_op_5_4= 'the' | lv_op_5_5= 'not' ) ) ) ( (lv_right_6_0= ruleGamlUnaryExpr ) ) ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2514:7: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2515:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getGamlUnaryExprAccess().getGamlUnaryExprAction_1_0(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2520:2: ( ( () ( ( (lv_op_3_0= '\\u00B0' ) ) ( (lv_right_4_0= ruleUnitName ) ) ) ) | ( ( ( (lv_op_5_1= '-' | lv_op_5_2= '!' | lv_op_5_3= 'my' | lv_op_5_4= 'the' | lv_op_5_5= 'not' ) ) ) ( (lv_right_6_0= ruleGamlUnaryExpr ) ) ) )
                    int alt44=2;
                    int LA44_0 = input.LA(1);

                    if ( (LA44_0==72) ) {
                        alt44=1;
                    }
                    else if ( (LA44_0==67||(LA44_0>=73 && LA44_0<=76)) ) {
                        alt44=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return current;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 44, 0, input);

                        throw nvae;
                    }
                    switch (alt44) {
                        case 1 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2520:3: ( () ( ( (lv_op_3_0= '\\u00B0' ) ) ( (lv_right_4_0= ruleUnitName ) ) ) )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2520:3: ( () ( ( (lv_op_3_0= '\\u00B0' ) ) ( (lv_right_4_0= ruleUnitName ) ) ) )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2520:4: () ( ( (lv_op_3_0= '\\u00B0' ) ) ( (lv_right_4_0= ruleUnitName ) ) )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2520:4: ()
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2521:5: 
                            {
                            if ( state.backtracking==0 ) {

                                      current = forceCreateModelElementAndSet(
                                          grammarAccess.getGamlUnaryExprAccess().getGamlUnaryExprLeftAction_1_1_0_0(),
                                          current);
                                  
                            }

                            }

                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2526:2: ( ( (lv_op_3_0= '\\u00B0' ) ) ( (lv_right_4_0= ruleUnitName ) ) )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2526:3: ( (lv_op_3_0= '\\u00B0' ) ) ( (lv_right_4_0= ruleUnitName ) )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2526:3: ( (lv_op_3_0= '\\u00B0' ) )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2527:1: (lv_op_3_0= '\\u00B0' )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2527:1: (lv_op_3_0= '\\u00B0' )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2528:3: lv_op_3_0= '\\u00B0'
                            {
                            lv_op_3_0=(Token)match(input,72,FOLLOW_72_in_ruleGamlUnaryExpr5576); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                                      newLeafNode(lv_op_3_0, grammarAccess.getGamlUnaryExprAccess().getOpDegreeSignKeyword_1_1_0_1_0_0());
                                  
                            }
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElement(grammarAccess.getGamlUnaryExprRule());
                              	        }
                                     		setWithLastConsumed(current, "op", lv_op_3_0, "\u00B0");
                              	    
                            }

                            }


                            }

                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2541:2: ( (lv_right_4_0= ruleUnitName ) )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2542:1: (lv_right_4_0= ruleUnitName )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2542:1: (lv_right_4_0= ruleUnitName )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2543:3: lv_right_4_0= ruleUnitName
                            {
                            if ( state.backtracking==0 ) {
                               
                              	        newCompositeNode(grammarAccess.getGamlUnaryExprAccess().getRightUnitNameParserRuleCall_1_1_0_1_1_0()); 
                              	    
                            }
                            pushFollow(FOLLOW_ruleUnitName_in_ruleGamlUnaryExpr5610);
                            lv_right_4_0=ruleUnitName();

                            state._fsp--;
                            if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElementForParent(grammarAccess.getGamlUnaryExprRule());
                              	        }
                                     		set(
                                     			current, 
                                     			"right",
                                      		lv_right_4_0, 
                                      		"UnitName");
                              	        afterParserOrEnumRuleCall();
                              	    
                            }

                            }


                            }


                            }


                            }


                            }
                            break;
                        case 2 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2560:6: ( ( ( (lv_op_5_1= '-' | lv_op_5_2= '!' | lv_op_5_3= 'my' | lv_op_5_4= 'the' | lv_op_5_5= 'not' ) ) ) ( (lv_right_6_0= ruleGamlUnaryExpr ) ) )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2560:6: ( ( ( (lv_op_5_1= '-' | lv_op_5_2= '!' | lv_op_5_3= 'my' | lv_op_5_4= 'the' | lv_op_5_5= 'not' ) ) ) ( (lv_right_6_0= ruleGamlUnaryExpr ) ) )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2560:7: ( ( (lv_op_5_1= '-' | lv_op_5_2= '!' | lv_op_5_3= 'my' | lv_op_5_4= 'the' | lv_op_5_5= 'not' ) ) ) ( (lv_right_6_0= ruleGamlUnaryExpr ) )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2560:7: ( ( (lv_op_5_1= '-' | lv_op_5_2= '!' | lv_op_5_3= 'my' | lv_op_5_4= 'the' | lv_op_5_5= 'not' ) ) )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2561:1: ( (lv_op_5_1= '-' | lv_op_5_2= '!' | lv_op_5_3= 'my' | lv_op_5_4= 'the' | lv_op_5_5= 'not' ) )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2561:1: ( (lv_op_5_1= '-' | lv_op_5_2= '!' | lv_op_5_3= 'my' | lv_op_5_4= 'the' | lv_op_5_5= 'not' ) )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2562:1: (lv_op_5_1= '-' | lv_op_5_2= '!' | lv_op_5_3= 'my' | lv_op_5_4= 'the' | lv_op_5_5= 'not' )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2562:1: (lv_op_5_1= '-' | lv_op_5_2= '!' | lv_op_5_3= 'my' | lv_op_5_4= 'the' | lv_op_5_5= 'not' )
                            int alt43=5;
                            switch ( input.LA(1) ) {
                            case 67:
                                {
                                alt43=1;
                                }
                                break;
                            case 73:
                                {
                                alt43=2;
                                }
                                break;
                            case 74:
                                {
                                alt43=3;
                                }
                                break;
                            case 75:
                                {
                                alt43=4;
                                }
                                break;
                            case 76:
                                {
                                alt43=5;
                                }
                                break;
                            default:
                                if (state.backtracking>0) {state.failed=true; return current;}
                                NoViableAltException nvae =
                                    new NoViableAltException("", 43, 0, input);

                                throw nvae;
                            }

                            switch (alt43) {
                                case 1 :
                                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2563:3: lv_op_5_1= '-'
                                    {
                                    lv_op_5_1=(Token)match(input,67,FOLLOW_67_in_ruleGamlUnaryExpr5639); if (state.failed) return current;
                                    if ( state.backtracking==0 ) {

                                              newLeafNode(lv_op_5_1, grammarAccess.getGamlUnaryExprAccess().getOpHyphenMinusKeyword_1_1_1_0_0_0());
                                          
                                    }
                                    if ( state.backtracking==0 ) {

                                      	        if (current==null) {
                                      	            current = createModelElement(grammarAccess.getGamlUnaryExprRule());
                                      	        }
                                             		setWithLastConsumed(current, "op", lv_op_5_1, null);
                                      	    
                                    }

                                    }
                                    break;
                                case 2 :
                                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2575:8: lv_op_5_2= '!'
                                    {
                                    lv_op_5_2=(Token)match(input,73,FOLLOW_73_in_ruleGamlUnaryExpr5668); if (state.failed) return current;
                                    if ( state.backtracking==0 ) {

                                              newLeafNode(lv_op_5_2, grammarAccess.getGamlUnaryExprAccess().getOpExclamationMarkKeyword_1_1_1_0_0_1());
                                          
                                    }
                                    if ( state.backtracking==0 ) {

                                      	        if (current==null) {
                                      	            current = createModelElement(grammarAccess.getGamlUnaryExprRule());
                                      	        }
                                             		setWithLastConsumed(current, "op", lv_op_5_2, null);
                                      	    
                                    }

                                    }
                                    break;
                                case 3 :
                                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2587:8: lv_op_5_3= 'my'
                                    {
                                    lv_op_5_3=(Token)match(input,74,FOLLOW_74_in_ruleGamlUnaryExpr5697); if (state.failed) return current;
                                    if ( state.backtracking==0 ) {

                                              newLeafNode(lv_op_5_3, grammarAccess.getGamlUnaryExprAccess().getOpMyKeyword_1_1_1_0_0_2());
                                          
                                    }
                                    if ( state.backtracking==0 ) {

                                      	        if (current==null) {
                                      	            current = createModelElement(grammarAccess.getGamlUnaryExprRule());
                                      	        }
                                             		setWithLastConsumed(current, "op", lv_op_5_3, null);
                                      	    
                                    }

                                    }
                                    break;
                                case 4 :
                                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2599:8: lv_op_5_4= 'the'
                                    {
                                    lv_op_5_4=(Token)match(input,75,FOLLOW_75_in_ruleGamlUnaryExpr5726); if (state.failed) return current;
                                    if ( state.backtracking==0 ) {

                                              newLeafNode(lv_op_5_4, grammarAccess.getGamlUnaryExprAccess().getOpTheKeyword_1_1_1_0_0_3());
                                          
                                    }
                                    if ( state.backtracking==0 ) {

                                      	        if (current==null) {
                                      	            current = createModelElement(grammarAccess.getGamlUnaryExprRule());
                                      	        }
                                             		setWithLastConsumed(current, "op", lv_op_5_4, null);
                                      	    
                                    }

                                    }
                                    break;
                                case 5 :
                                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2611:8: lv_op_5_5= 'not'
                                    {
                                    lv_op_5_5=(Token)match(input,76,FOLLOW_76_in_ruleGamlUnaryExpr5755); if (state.failed) return current;
                                    if ( state.backtracking==0 ) {

                                              newLeafNode(lv_op_5_5, grammarAccess.getGamlUnaryExprAccess().getOpNotKeyword_1_1_1_0_0_4());
                                          
                                    }
                                    if ( state.backtracking==0 ) {

                                      	        if (current==null) {
                                      	            current = createModelElement(grammarAccess.getGamlUnaryExprRule());
                                      	        }
                                             		setWithLastConsumed(current, "op", lv_op_5_5, null);
                                      	    
                                    }

                                    }
                                    break;

                            }


                            }


                            }

                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2626:2: ( (lv_right_6_0= ruleGamlUnaryExpr ) )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2627:1: (lv_right_6_0= ruleGamlUnaryExpr )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2627:1: (lv_right_6_0= ruleGamlUnaryExpr )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2628:3: lv_right_6_0= ruleGamlUnaryExpr
                            {
                            if ( state.backtracking==0 ) {
                               
                              	        newCompositeNode(grammarAccess.getGamlUnaryExprAccess().getRightGamlUnaryExprParserRuleCall_1_1_1_1_0()); 
                              	    
                            }
                            pushFollow(FOLLOW_ruleGamlUnaryExpr_in_ruleGamlUnaryExpr5792);
                            lv_right_6_0=ruleGamlUnaryExpr();

                            state._fsp--;
                            if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElementForParent(grammarAccess.getGamlUnaryExprRule());
                              	        }
                                     		set(
                                     			current, 
                                     			"right",
                                      		lv_right_6_0, 
                                      		"GamlUnaryExpr");
                              	        afterParserOrEnumRuleCall();
                              	    
                            }

                            }


                            }


                            }


                            }
                            break;

                    }


                    }


                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleGamlUnaryExpr"


    // $ANTLR start "entryRuleAccess"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2652:1: entryRuleAccess returns [EObject current=null] : iv_ruleAccess= ruleAccess EOF ;
    public final EObject entryRuleAccess() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAccess = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2653:2: (iv_ruleAccess= ruleAccess EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2654:2: iv_ruleAccess= ruleAccess EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAccessRule()); 
            }
            pushFollow(FOLLOW_ruleAccess_in_entryRuleAccess5831);
            iv_ruleAccess=ruleAccess();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAccess; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleAccess5841); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAccess"


    // $ANTLR start "ruleAccess"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2661:1: ruleAccess returns [EObject current=null] : (this_MemberRef_0= ruleMemberRef ( ( () otherlv_2= '[' ) ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ']' )* ) ;
    public final EObject ruleAccess() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        Token otherlv_4=null;
        Token otherlv_6=null;
        EObject this_MemberRef_0 = null;

        EObject lv_args_3_0 = null;

        EObject lv_args_5_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2664:28: ( (this_MemberRef_0= ruleMemberRef ( ( () otherlv_2= '[' ) ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ']' )* ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2665:1: (this_MemberRef_0= ruleMemberRef ( ( () otherlv_2= '[' ) ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ']' )* )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2665:1: (this_MemberRef_0= ruleMemberRef ( ( () otherlv_2= '[' ) ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ']' )* )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2666:5: this_MemberRef_0= ruleMemberRef ( ( () otherlv_2= '[' ) ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ']' )*
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getAccessAccess().getMemberRefParserRuleCall_0()); 
                  
            }
            pushFollow(FOLLOW_ruleMemberRef_in_ruleAccess5888);
            this_MemberRef_0=ruleMemberRef();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_MemberRef_0; 
                      afterParserOrEnumRuleCall();
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2674:1: ( ( () otherlv_2= '[' ) ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ']' )*
            loop47:
            do {
                int alt47=2;
                int LA47_0 = input.LA(1);

                if ( (LA47_0==77) ) {
                    alt47=1;
                }


                switch (alt47) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2674:2: ( () otherlv_2= '[' ) ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ']'
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2674:2: ( () otherlv_2= '[' )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2674:3: () otherlv_2= '['
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2674:3: ()
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2675:5: 
            	    {
            	    if ( state.backtracking==0 ) {

            	              current = forceCreateModelElementAndSet(
            	                  grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0(),
            	                  current);
            	          
            	    }

            	    }

            	    otherlv_2=(Token)match(input,77,FOLLOW_77_in_ruleAccess5910); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	          	newLeafNode(otherlv_2, grammarAccess.getAccessAccess().getLeftSquareBracketKeyword_1_0_1());
            	          
            	    }

            	    }

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2684:2: ( (lv_args_3_0= ruleExpression ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2685:1: (lv_args_3_0= ruleExpression )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2685:1: (lv_args_3_0= ruleExpression )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2686:3: lv_args_3_0= ruleExpression
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getAccessAccess().getArgsExpressionParserRuleCall_1_1_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleExpression_in_ruleAccess5932);
            	    lv_args_3_0=ruleExpression();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getAccessRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"args",
            	              		lv_args_3_0, 
            	              		"Expression");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2702:2: (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )*
            	    loop46:
            	    do {
            	        int alt46=2;
            	        int LA46_0 = input.LA(1);

            	        if ( (LA46_0==42) ) {
            	            alt46=1;
            	        }


            	        switch (alt46) {
            	    	case 1 :
            	    	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2702:4: otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) )
            	    	    {
            	    	    otherlv_4=(Token)match(input,42,FOLLOW_42_in_ruleAccess5945); if (state.failed) return current;
            	    	    if ( state.backtracking==0 ) {

            	    	          	newLeafNode(otherlv_4, grammarAccess.getAccessAccess().getCommaKeyword_1_2_0());
            	    	          
            	    	    }
            	    	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2706:1: ( (lv_args_5_0= ruleExpression ) )
            	    	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2707:1: (lv_args_5_0= ruleExpression )
            	    	    {
            	    	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2707:1: (lv_args_5_0= ruleExpression )
            	    	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2708:3: lv_args_5_0= ruleExpression
            	    	    {
            	    	    if ( state.backtracking==0 ) {
            	    	       
            	    	      	        newCompositeNode(grammarAccess.getAccessAccess().getArgsExpressionParserRuleCall_1_2_1_0()); 
            	    	      	    
            	    	    }
            	    	    pushFollow(FOLLOW_ruleExpression_in_ruleAccess5966);
            	    	    lv_args_5_0=ruleExpression();

            	    	    state._fsp--;
            	    	    if (state.failed) return current;
            	    	    if ( state.backtracking==0 ) {

            	    	      	        if (current==null) {
            	    	      	            current = createModelElementForParent(grammarAccess.getAccessRule());
            	    	      	        }
            	    	             		add(
            	    	             			current, 
            	    	             			"args",
            	    	              		lv_args_5_0, 
            	    	              		"Expression");
            	    	      	        afterParserOrEnumRuleCall();
            	    	      	    
            	    	    }

            	    	    }


            	    	    }


            	    	    }
            	    	    break;

            	    	default :
            	    	    break loop46;
            	        }
            	    } while (true);

            	    otherlv_6=(Token)match(input,78,FOLLOW_78_in_ruleAccess5980); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	          	newLeafNode(otherlv_6, grammarAccess.getAccessAccess().getRightSquareBracketKeyword_1_3());
            	          
            	    }

            	    }
            	    break;

            	default :
            	    break loop47;
                }
            } while (true);


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAccess"


    // $ANTLR start "entryRuleMemberRef"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2736:1: entryRuleMemberRef returns [EObject current=null] : iv_ruleMemberRef= ruleMemberRef EOF ;
    public final EObject entryRuleMemberRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleMemberRef = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2737:2: (iv_ruleMemberRef= ruleMemberRef EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2738:2: iv_ruleMemberRef= ruleMemberRef EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getMemberRefRule()); 
            }
            pushFollow(FOLLOW_ruleMemberRef_in_entryRuleMemberRef6018);
            iv_ruleMemberRef=ruleMemberRef();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleMemberRef; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleMemberRef6028); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleMemberRef"


    // $ANTLR start "ruleMemberRef"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2745:1: ruleMemberRef returns [EObject current=null] : (this_PrimaryExpression_0= rulePrimaryExpression ( () ( ( (lv_op_2_0= '.' ) ) ( (lv_right_3_0= rulePrimaryExpression ) ) ) )* ) ;
    public final EObject ruleMemberRef() throws RecognitionException {
        EObject current = null;

        Token lv_op_2_0=null;
        EObject this_PrimaryExpression_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2748:28: ( (this_PrimaryExpression_0= rulePrimaryExpression ( () ( ( (lv_op_2_0= '.' ) ) ( (lv_right_3_0= rulePrimaryExpression ) ) ) )* ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2749:1: (this_PrimaryExpression_0= rulePrimaryExpression ( () ( ( (lv_op_2_0= '.' ) ) ( (lv_right_3_0= rulePrimaryExpression ) ) ) )* )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2749:1: (this_PrimaryExpression_0= rulePrimaryExpression ( () ( ( (lv_op_2_0= '.' ) ) ( (lv_right_3_0= rulePrimaryExpression ) ) ) )* )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2750:5: this_PrimaryExpression_0= rulePrimaryExpression ( () ( ( (lv_op_2_0= '.' ) ) ( (lv_right_3_0= rulePrimaryExpression ) ) ) )*
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getMemberRefAccess().getPrimaryExpressionParserRuleCall_0()); 
                  
            }
            pushFollow(FOLLOW_rulePrimaryExpression_in_ruleMemberRef6075);
            this_PrimaryExpression_0=rulePrimaryExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_PrimaryExpression_0; 
                      afterParserOrEnumRuleCall();
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2758:1: ( () ( ( (lv_op_2_0= '.' ) ) ( (lv_right_3_0= rulePrimaryExpression ) ) ) )*
            loop48:
            do {
                int alt48=2;
                int LA48_0 = input.LA(1);

                if ( (LA48_0==79) ) {
                    alt48=1;
                }


                switch (alt48) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2758:2: () ( ( (lv_op_2_0= '.' ) ) ( (lv_right_3_0= rulePrimaryExpression ) ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2758:2: ()
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2759:5: 
            	    {
            	    if ( state.backtracking==0 ) {

            	              current = forceCreateModelElementAndSet(
            	                  grammarAccess.getMemberRefAccess().getMemberRefLeftAction_1_0(),
            	                  current);
            	          
            	    }

            	    }

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2764:2: ( ( (lv_op_2_0= '.' ) ) ( (lv_right_3_0= rulePrimaryExpression ) ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2764:3: ( (lv_op_2_0= '.' ) ) ( (lv_right_3_0= rulePrimaryExpression ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2764:3: ( (lv_op_2_0= '.' ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2765:1: (lv_op_2_0= '.' )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2765:1: (lv_op_2_0= '.' )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2766:3: lv_op_2_0= '.'
            	    {
            	    lv_op_2_0=(Token)match(input,79,FOLLOW_79_in_ruleMemberRef6103); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	              newLeafNode(lv_op_2_0, grammarAccess.getMemberRefAccess().getOpFullStopKeyword_1_1_0_0());
            	          
            	    }
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElement(grammarAccess.getMemberRefRule());
            	      	        }
            	             		setWithLastConsumed(current, "op", lv_op_2_0, ".");
            	      	    
            	    }

            	    }


            	    }

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2779:2: ( (lv_right_3_0= rulePrimaryExpression ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2780:1: (lv_right_3_0= rulePrimaryExpression )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2780:1: (lv_right_3_0= rulePrimaryExpression )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2781:3: lv_right_3_0= rulePrimaryExpression
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getMemberRefAccess().getRightPrimaryExpressionParserRuleCall_1_1_1_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_rulePrimaryExpression_in_ruleMemberRef6137);
            	    lv_right_3_0=rulePrimaryExpression();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getMemberRefRule());
            	      	        }
            	             		set(
            	             			current, 
            	             			"right",
            	              		lv_right_3_0, 
            	              		"PrimaryExpression");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop48;
                }
            } while (true);


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleMemberRef"


    // $ANTLR start "entryRulePrimaryExpression"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2805:1: entryRulePrimaryExpression returns [EObject current=null] : iv_rulePrimaryExpression= rulePrimaryExpression EOF ;
    public final EObject entryRulePrimaryExpression() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePrimaryExpression = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2806:2: (iv_rulePrimaryExpression= rulePrimaryExpression EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2807:2: iv_rulePrimaryExpression= rulePrimaryExpression EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getPrimaryExpressionRule()); 
            }
            pushFollow(FOLLOW_rulePrimaryExpression_in_entryRulePrimaryExpression6176);
            iv_rulePrimaryExpression=rulePrimaryExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_rulePrimaryExpression; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRulePrimaryExpression6186); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRulePrimaryExpression"


    // $ANTLR start "rulePrimaryExpression"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2814:1: rulePrimaryExpression returns [EObject current=null] : (this_TerminalExpression_0= ruleTerminalExpression | this_AbstractRef_1= ruleAbstractRef | (otherlv_2= '(' this_Expression_3= ruleExpression otherlv_4= ')' ) | (otherlv_5= '[' () ( ( (lv_exprs_7_0= ruleExpression ) ) (otherlv_8= ',' ( (lv_exprs_9_0= ruleExpression ) ) )* )? otherlv_10= ']' ) | (otherlv_11= '{' () ( (lv_left_13_0= ruleExpression ) ) ( (lv_op_14_0= ',' ) ) ( (lv_right_15_0= ruleExpression ) ) (otherlv_16= ',' ( (lv_z_17_0= ruleExpression ) ) )? otherlv_18= '}' ) ) ;
    public final EObject rulePrimaryExpression() throws RecognitionException {
        EObject current = null;

        Token otherlv_2=null;
        Token otherlv_4=null;
        Token otherlv_5=null;
        Token otherlv_8=null;
        Token otherlv_10=null;
        Token otherlv_11=null;
        Token lv_op_14_0=null;
        Token otherlv_16=null;
        Token otherlv_18=null;
        EObject this_TerminalExpression_0 = null;

        EObject this_AbstractRef_1 = null;

        EObject this_Expression_3 = null;

        EObject lv_exprs_7_0 = null;

        EObject lv_exprs_9_0 = null;

        EObject lv_left_13_0 = null;

        EObject lv_right_15_0 = null;

        EObject lv_z_17_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2817:28: ( (this_TerminalExpression_0= ruleTerminalExpression | this_AbstractRef_1= ruleAbstractRef | (otherlv_2= '(' this_Expression_3= ruleExpression otherlv_4= ')' ) | (otherlv_5= '[' () ( ( (lv_exprs_7_0= ruleExpression ) ) (otherlv_8= ',' ( (lv_exprs_9_0= ruleExpression ) ) )* )? otherlv_10= ']' ) | (otherlv_11= '{' () ( (lv_left_13_0= ruleExpression ) ) ( (lv_op_14_0= ',' ) ) ( (lv_right_15_0= ruleExpression ) ) (otherlv_16= ',' ( (lv_z_17_0= ruleExpression ) ) )? otherlv_18= '}' ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2818:1: (this_TerminalExpression_0= ruleTerminalExpression | this_AbstractRef_1= ruleAbstractRef | (otherlv_2= '(' this_Expression_3= ruleExpression otherlv_4= ')' ) | (otherlv_5= '[' () ( ( (lv_exprs_7_0= ruleExpression ) ) (otherlv_8= ',' ( (lv_exprs_9_0= ruleExpression ) ) )* )? otherlv_10= ']' ) | (otherlv_11= '{' () ( (lv_left_13_0= ruleExpression ) ) ( (lv_op_14_0= ',' ) ) ( (lv_right_15_0= ruleExpression ) ) (otherlv_16= ',' ( (lv_z_17_0= ruleExpression ) ) )? otherlv_18= '}' ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2818:1: (this_TerminalExpression_0= ruleTerminalExpression | this_AbstractRef_1= ruleAbstractRef | (otherlv_2= '(' this_Expression_3= ruleExpression otherlv_4= ')' ) | (otherlv_5= '[' () ( ( (lv_exprs_7_0= ruleExpression ) ) (otherlv_8= ',' ( (lv_exprs_9_0= ruleExpression ) ) )* )? otherlv_10= ']' ) | (otherlv_11= '{' () ( (lv_left_13_0= ruleExpression ) ) ( (lv_op_14_0= ',' ) ) ( (lv_right_15_0= ruleExpression ) ) (otherlv_16= ',' ( (lv_z_17_0= ruleExpression ) ) )? otherlv_18= '}' ) )
            int alt52=5;
            switch ( input.LA(1) ) {
            case RULE_STRING:
            case RULE_INTEGER:
            case RULE_DOUBLE:
            case RULE_COLOR:
            case RULE_BOOLEAN:
                {
                alt52=1;
                }
                break;
            case RULE_ID:
                {
                alt52=2;
                }
                break;
            case 80:
                {
                alt52=3;
                }
                break;
            case 77:
                {
                alt52=4;
                }
                break;
            case 54:
                {
                alt52=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 52, 0, input);

                throw nvae;
            }

            switch (alt52) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2819:5: this_TerminalExpression_0= ruleTerminalExpression
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getPrimaryExpressionAccess().getTerminalExpressionParserRuleCall_0()); 
                          
                    }
                    pushFollow(FOLLOW_ruleTerminalExpression_in_rulePrimaryExpression6233);
                    this_TerminalExpression_0=ruleTerminalExpression();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_TerminalExpression_0; 
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 2 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2829:5: this_AbstractRef_1= ruleAbstractRef
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getPrimaryExpressionAccess().getAbstractRefParserRuleCall_1()); 
                          
                    }
                    pushFollow(FOLLOW_ruleAbstractRef_in_rulePrimaryExpression6260);
                    this_AbstractRef_1=ruleAbstractRef();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_AbstractRef_1; 
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 3 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2838:6: (otherlv_2= '(' this_Expression_3= ruleExpression otherlv_4= ')' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2838:6: (otherlv_2= '(' this_Expression_3= ruleExpression otherlv_4= ')' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2838:8: otherlv_2= '(' this_Expression_3= ruleExpression otherlv_4= ')'
                    {
                    otherlv_2=(Token)match(input,80,FOLLOW_80_in_rulePrimaryExpression6278); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_2, grammarAccess.getPrimaryExpressionAccess().getLeftParenthesisKeyword_2_0());
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getPrimaryExpressionAccess().getExpressionParserRuleCall_2_1()); 
                          
                    }
                    pushFollow(FOLLOW_ruleExpression_in_rulePrimaryExpression6300);
                    this_Expression_3=ruleExpression();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_Expression_3; 
                              afterParserOrEnumRuleCall();
                          
                    }
                    otherlv_4=(Token)match(input,81,FOLLOW_81_in_rulePrimaryExpression6311); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_4, grammarAccess.getPrimaryExpressionAccess().getRightParenthesisKeyword_2_2());
                          
                    }

                    }


                    }
                    break;
                case 4 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2856:6: (otherlv_5= '[' () ( ( (lv_exprs_7_0= ruleExpression ) ) (otherlv_8= ',' ( (lv_exprs_9_0= ruleExpression ) ) )* )? otherlv_10= ']' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2856:6: (otherlv_5= '[' () ( ( (lv_exprs_7_0= ruleExpression ) ) (otherlv_8= ',' ( (lv_exprs_9_0= ruleExpression ) ) )* )? otherlv_10= ']' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2856:8: otherlv_5= '[' () ( ( (lv_exprs_7_0= ruleExpression ) ) (otherlv_8= ',' ( (lv_exprs_9_0= ruleExpression ) ) )* )? otherlv_10= ']'
                    {
                    otherlv_5=(Token)match(input,77,FOLLOW_77_in_rulePrimaryExpression6331); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_5, grammarAccess.getPrimaryExpressionAccess().getLeftSquareBracketKeyword_3_0());
                          
                    }
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2860:1: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2861:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getPrimaryExpressionAccess().getArrayAction_3_1(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2866:2: ( ( (lv_exprs_7_0= ruleExpression ) ) (otherlv_8= ',' ( (lv_exprs_9_0= ruleExpression ) ) )* )?
                    int alt50=2;
                    int LA50_0 = input.LA(1);

                    if ( ((LA50_0>=RULE_ID && LA50_0<=RULE_BOOLEAN)||(LA50_0>=16 && LA50_0<=34)||LA50_0==54||(LA50_0>=56 && LA50_0<=58)||LA50_0==67||(LA50_0>=72 && LA50_0<=77)||LA50_0==80) ) {
                        alt50=1;
                    }
                    switch (alt50) {
                        case 1 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2866:3: ( (lv_exprs_7_0= ruleExpression ) ) (otherlv_8= ',' ( (lv_exprs_9_0= ruleExpression ) ) )*
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2866:3: ( (lv_exprs_7_0= ruleExpression ) )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2867:1: (lv_exprs_7_0= ruleExpression )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2867:1: (lv_exprs_7_0= ruleExpression )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2868:3: lv_exprs_7_0= ruleExpression
                            {
                            if ( state.backtracking==0 ) {
                               
                              	        newCompositeNode(grammarAccess.getPrimaryExpressionAccess().getExprsExpressionParserRuleCall_3_2_0_0()); 
                              	    
                            }
                            pushFollow(FOLLOW_ruleExpression_in_rulePrimaryExpression6362);
                            lv_exprs_7_0=ruleExpression();

                            state._fsp--;
                            if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElementForParent(grammarAccess.getPrimaryExpressionRule());
                              	        }
                                     		add(
                                     			current, 
                                     			"exprs",
                                      		lv_exprs_7_0, 
                                      		"Expression");
                              	        afterParserOrEnumRuleCall();
                              	    
                            }

                            }


                            }

                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2884:2: (otherlv_8= ',' ( (lv_exprs_9_0= ruleExpression ) ) )*
                            loop49:
                            do {
                                int alt49=2;
                                int LA49_0 = input.LA(1);

                                if ( (LA49_0==42) ) {
                                    alt49=1;
                                }


                                switch (alt49) {
                            	case 1 :
                            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2884:4: otherlv_8= ',' ( (lv_exprs_9_0= ruleExpression ) )
                            	    {
                            	    otherlv_8=(Token)match(input,42,FOLLOW_42_in_rulePrimaryExpression6375); if (state.failed) return current;
                            	    if ( state.backtracking==0 ) {

                            	          	newLeafNode(otherlv_8, grammarAccess.getPrimaryExpressionAccess().getCommaKeyword_3_2_1_0());
                            	          
                            	    }
                            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2888:1: ( (lv_exprs_9_0= ruleExpression ) )
                            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2889:1: (lv_exprs_9_0= ruleExpression )
                            	    {
                            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2889:1: (lv_exprs_9_0= ruleExpression )
                            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2890:3: lv_exprs_9_0= ruleExpression
                            	    {
                            	    if ( state.backtracking==0 ) {
                            	       
                            	      	        newCompositeNode(grammarAccess.getPrimaryExpressionAccess().getExprsExpressionParserRuleCall_3_2_1_1_0()); 
                            	      	    
                            	    }
                            	    pushFollow(FOLLOW_ruleExpression_in_rulePrimaryExpression6396);
                            	    lv_exprs_9_0=ruleExpression();

                            	    state._fsp--;
                            	    if (state.failed) return current;
                            	    if ( state.backtracking==0 ) {

                            	      	        if (current==null) {
                            	      	            current = createModelElementForParent(grammarAccess.getPrimaryExpressionRule());
                            	      	        }
                            	             		add(
                            	             			current, 
                            	             			"exprs",
                            	              		lv_exprs_9_0, 
                            	              		"Expression");
                            	      	        afterParserOrEnumRuleCall();
                            	      	    
                            	    }

                            	    }


                            	    }


                            	    }
                            	    break;

                            	default :
                            	    break loop49;
                                }
                            } while (true);


                            }
                            break;

                    }

                    otherlv_10=(Token)match(input,78,FOLLOW_78_in_rulePrimaryExpression6412); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_10, grammarAccess.getPrimaryExpressionAccess().getRightSquareBracketKeyword_3_3());
                          
                    }

                    }


                    }
                    break;
                case 5 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2911:6: (otherlv_11= '{' () ( (lv_left_13_0= ruleExpression ) ) ( (lv_op_14_0= ',' ) ) ( (lv_right_15_0= ruleExpression ) ) (otherlv_16= ',' ( (lv_z_17_0= ruleExpression ) ) )? otherlv_18= '}' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2911:6: (otherlv_11= '{' () ( (lv_left_13_0= ruleExpression ) ) ( (lv_op_14_0= ',' ) ) ( (lv_right_15_0= ruleExpression ) ) (otherlv_16= ',' ( (lv_z_17_0= ruleExpression ) ) )? otherlv_18= '}' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2911:8: otherlv_11= '{' () ( (lv_left_13_0= ruleExpression ) ) ( (lv_op_14_0= ',' ) ) ( (lv_right_15_0= ruleExpression ) ) (otherlv_16= ',' ( (lv_z_17_0= ruleExpression ) ) )? otherlv_18= '}'
                    {
                    otherlv_11=(Token)match(input,54,FOLLOW_54_in_rulePrimaryExpression6432); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_11, grammarAccess.getPrimaryExpressionAccess().getLeftCurlyBracketKeyword_4_0());
                          
                    }
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2915:1: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2916:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getPrimaryExpressionAccess().getPointAction_4_1(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2921:2: ( (lv_left_13_0= ruleExpression ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2922:1: (lv_left_13_0= ruleExpression )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2922:1: (lv_left_13_0= ruleExpression )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2923:3: lv_left_13_0= ruleExpression
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getPrimaryExpressionAccess().getLeftExpressionParserRuleCall_4_2_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleExpression_in_rulePrimaryExpression6462);
                    lv_left_13_0=ruleExpression();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getPrimaryExpressionRule());
                      	        }
                             		set(
                             			current, 
                             			"left",
                              		lv_left_13_0, 
                              		"Expression");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2939:2: ( (lv_op_14_0= ',' ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2940:1: (lv_op_14_0= ',' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2940:1: (lv_op_14_0= ',' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2941:3: lv_op_14_0= ','
                    {
                    lv_op_14_0=(Token)match(input,42,FOLLOW_42_in_rulePrimaryExpression6480); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_op_14_0, grammarAccess.getPrimaryExpressionAccess().getOpCommaKeyword_4_3_0());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getPrimaryExpressionRule());
                      	        }
                             		setWithLastConsumed(current, "op", lv_op_14_0, ",");
                      	    
                    }

                    }


                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2954:2: ( (lv_right_15_0= ruleExpression ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2955:1: (lv_right_15_0= ruleExpression )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2955:1: (lv_right_15_0= ruleExpression )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2956:3: lv_right_15_0= ruleExpression
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getPrimaryExpressionAccess().getRightExpressionParserRuleCall_4_4_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleExpression_in_rulePrimaryExpression6514);
                    lv_right_15_0=ruleExpression();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getPrimaryExpressionRule());
                      	        }
                             		set(
                             			current, 
                             			"right",
                              		lv_right_15_0, 
                              		"Expression");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2972:2: (otherlv_16= ',' ( (lv_z_17_0= ruleExpression ) ) )?
                    int alt51=2;
                    int LA51_0 = input.LA(1);

                    if ( (LA51_0==42) ) {
                        alt51=1;
                    }
                    switch (alt51) {
                        case 1 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2972:4: otherlv_16= ',' ( (lv_z_17_0= ruleExpression ) )
                            {
                            otherlv_16=(Token)match(input,42,FOLLOW_42_in_rulePrimaryExpression6527); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                                  	newLeafNode(otherlv_16, grammarAccess.getPrimaryExpressionAccess().getCommaKeyword_4_5_0());
                                  
                            }
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2976:1: ( (lv_z_17_0= ruleExpression ) )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2977:1: (lv_z_17_0= ruleExpression )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2977:1: (lv_z_17_0= ruleExpression )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2978:3: lv_z_17_0= ruleExpression
                            {
                            if ( state.backtracking==0 ) {
                               
                              	        newCompositeNode(grammarAccess.getPrimaryExpressionAccess().getZExpressionParserRuleCall_4_5_1_0()); 
                              	    
                            }
                            pushFollow(FOLLOW_ruleExpression_in_rulePrimaryExpression6548);
                            lv_z_17_0=ruleExpression();

                            state._fsp--;
                            if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElementForParent(grammarAccess.getPrimaryExpressionRule());
                              	        }
                                     		set(
                                     			current, 
                                     			"z",
                                      		lv_z_17_0, 
                                      		"Expression");
                              	        afterParserOrEnumRuleCall();
                              	    
                            }

                            }


                            }


                            }
                            break;

                    }

                    otherlv_18=(Token)match(input,55,FOLLOW_55_in_rulePrimaryExpression6562); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_18, grammarAccess.getPrimaryExpressionAccess().getRightCurlyBracketKeyword_4_6());
                          
                    }

                    }


                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "rulePrimaryExpression"


    // $ANTLR start "entryRuleAbstractRef"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3006:1: entryRuleAbstractRef returns [EObject current=null] : iv_ruleAbstractRef= ruleAbstractRef EOF ;
    public final EObject entryRuleAbstractRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAbstractRef = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3007:2: (iv_ruleAbstractRef= ruleAbstractRef EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3008:2: iv_ruleAbstractRef= ruleAbstractRef EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAbstractRefRule()); 
            }
            pushFollow(FOLLOW_ruleAbstractRef_in_entryRuleAbstractRef6599);
            iv_ruleAbstractRef=ruleAbstractRef();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAbstractRef; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleAbstractRef6609); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleAbstractRef"


    // $ANTLR start "ruleAbstractRef"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3015:1: ruleAbstractRef returns [EObject current=null] : (this_VariableRef_0= ruleVariableRef | this_Function_1= ruleFunction ) ;
    public final EObject ruleAbstractRef() throws RecognitionException {
        EObject current = null;

        EObject this_VariableRef_0 = null;

        EObject this_Function_1 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3018:28: ( (this_VariableRef_0= ruleVariableRef | this_Function_1= ruleFunction ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3019:1: (this_VariableRef_0= ruleVariableRef | this_Function_1= ruleFunction )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3019:1: (this_VariableRef_0= ruleVariableRef | this_Function_1= ruleFunction )
            int alt53=2;
            int LA53_0 = input.LA(1);

            if ( (LA53_0==RULE_ID) ) {
                int LA53_1 = input.LA(2);

                if ( (LA53_1==EOF||LA53_1==RULE_ID||(LA53_1>=35 && LA53_1<=36)||(LA53_1>=40 && LA53_1<=43)||(LA53_1>=45 && LA53_1<=72)||(LA53_1>=77 && LA53_1<=79)||LA53_1==81) ) {
                    alt53=1;
                }
                else if ( (LA53_1==80) ) {
                    alt53=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return current;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 53, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 53, 0, input);

                throw nvae;
            }
            switch (alt53) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3020:5: this_VariableRef_0= ruleVariableRef
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getAbstractRefAccess().getVariableRefParserRuleCall_0()); 
                          
                    }
                    pushFollow(FOLLOW_ruleVariableRef_in_ruleAbstractRef6656);
                    this_VariableRef_0=ruleVariableRef();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_VariableRef_0; 
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 2 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3030:5: this_Function_1= ruleFunction
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getAbstractRefAccess().getFunctionParserRuleCall_1()); 
                          
                    }
                    pushFollow(FOLLOW_ruleFunction_in_ruleAbstractRef6683);
                    this_Function_1=ruleFunction();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_Function_1; 
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleAbstractRef"


    // $ANTLR start "entryRuleFunction"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3046:1: entryRuleFunction returns [EObject current=null] : iv_ruleFunction= ruleFunction EOF ;
    public final EObject entryRuleFunction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleFunction = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3047:2: (iv_ruleFunction= ruleFunction EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3048:2: iv_ruleFunction= ruleFunction EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getFunctionRule()); 
            }
            pushFollow(FOLLOW_ruleFunction_in_entryRuleFunction6718);
            iv_ruleFunction=ruleFunction();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleFunction; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleFunction6728); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleFunction"


    // $ANTLR start "ruleFunction"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3055:1: ruleFunction returns [EObject current=null] : ( () ( ( (lv_op_1_0= RULE_ID ) ) otherlv_2= '(' ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ')' ) ) ;
    public final EObject ruleFunction() throws RecognitionException {
        EObject current = null;

        Token lv_op_1_0=null;
        Token otherlv_2=null;
        Token otherlv_4=null;
        Token otherlv_6=null;
        EObject lv_args_3_0 = null;

        EObject lv_args_5_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3058:28: ( ( () ( ( (lv_op_1_0= RULE_ID ) ) otherlv_2= '(' ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ')' ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3059:1: ( () ( ( (lv_op_1_0= RULE_ID ) ) otherlv_2= '(' ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ')' ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3059:1: ( () ( ( (lv_op_1_0= RULE_ID ) ) otherlv_2= '(' ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ')' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3059:2: () ( ( (lv_op_1_0= RULE_ID ) ) otherlv_2= '(' ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ')' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3059:2: ()
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3060:5: 
            {
            if ( state.backtracking==0 ) {

                      current = forceCreateModelElement(
                          grammarAccess.getFunctionAccess().getFunctionAction_0(),
                          current);
                  
            }

            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3065:2: ( ( (lv_op_1_0= RULE_ID ) ) otherlv_2= '(' ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ')' )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3065:3: ( (lv_op_1_0= RULE_ID ) ) otherlv_2= '(' ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ')'
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3065:3: ( (lv_op_1_0= RULE_ID ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3066:1: (lv_op_1_0= RULE_ID )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3066:1: (lv_op_1_0= RULE_ID )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3067:3: lv_op_1_0= RULE_ID
            {
            lv_op_1_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleFunction6780); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(lv_op_1_0, grammarAccess.getFunctionAccess().getOpIDTerminalRuleCall_1_0_0()); 
              		
            }
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElement(grammarAccess.getFunctionRule());
              	        }
                     		setWithLastConsumed(
                     			current, 
                     			"op",
                      		lv_op_1_0, 
                      		"ID");
              	    
            }

            }


            }

            otherlv_2=(Token)match(input,80,FOLLOW_80_in_ruleFunction6797); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_2, grammarAccess.getFunctionAccess().getLeftParenthesisKeyword_1_1());
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3087:1: ( (lv_args_3_0= ruleExpression ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3088:1: (lv_args_3_0= ruleExpression )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3088:1: (lv_args_3_0= ruleExpression )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3089:3: lv_args_3_0= ruleExpression
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getFunctionAccess().getArgsExpressionParserRuleCall_1_2_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleExpression_in_ruleFunction6818);
            lv_args_3_0=ruleExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getFunctionRule());
              	        }
                     		add(
                     			current, 
                     			"args",
                      		lv_args_3_0, 
                      		"Expression");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3105:2: (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )*
            loop54:
            do {
                int alt54=2;
                int LA54_0 = input.LA(1);

                if ( (LA54_0==42) ) {
                    alt54=1;
                }


                switch (alt54) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3105:4: otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) )
            	    {
            	    otherlv_4=(Token)match(input,42,FOLLOW_42_in_ruleFunction6831); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	          	newLeafNode(otherlv_4, grammarAccess.getFunctionAccess().getCommaKeyword_1_3_0());
            	          
            	    }
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3109:1: ( (lv_args_5_0= ruleExpression ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3110:1: (lv_args_5_0= ruleExpression )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3110:1: (lv_args_5_0= ruleExpression )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3111:3: lv_args_5_0= ruleExpression
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getFunctionAccess().getArgsExpressionParserRuleCall_1_3_1_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleExpression_in_ruleFunction6852);
            	    lv_args_5_0=ruleExpression();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getFunctionRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"args",
            	              		lv_args_5_0, 
            	              		"Expression");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    break loop54;
                }
            } while (true);

            otherlv_6=(Token)match(input,81,FOLLOW_81_in_ruleFunction6866); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_6, grammarAccess.getFunctionAccess().getRightParenthesisKeyword_1_4());
                  
            }

            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleFunction"


    // $ANTLR start "entryRuleUnitName"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3139:1: entryRuleUnitName returns [EObject current=null] : iv_ruleUnitName= ruleUnitName EOF ;
    public final EObject entryRuleUnitName() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleUnitName = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3140:2: (iv_ruleUnitName= ruleUnitName EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3141:2: iv_ruleUnitName= ruleUnitName EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getUnitNameRule()); 
            }
            pushFollow(FOLLOW_ruleUnitName_in_entryRuleUnitName6903);
            iv_ruleUnitName=ruleUnitName();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleUnitName; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleUnitName6913); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleUnitName"


    // $ANTLR start "ruleUnitName"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3148:1: ruleUnitName returns [EObject current=null] : ( () ( (lv_op_1_0= RULE_ID ) ) ) ;
    public final EObject ruleUnitName() throws RecognitionException {
        EObject current = null;

        Token lv_op_1_0=null;

         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3151:28: ( ( () ( (lv_op_1_0= RULE_ID ) ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3152:1: ( () ( (lv_op_1_0= RULE_ID ) ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3152:1: ( () ( (lv_op_1_0= RULE_ID ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3152:2: () ( (lv_op_1_0= RULE_ID ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3152:2: ()
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3153:5: 
            {
            if ( state.backtracking==0 ) {

                      current = forceCreateModelElement(
                          grammarAccess.getUnitNameAccess().getUnitNameAction_0(),
                          current);
                  
            }

            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3158:2: ( (lv_op_1_0= RULE_ID ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3159:1: (lv_op_1_0= RULE_ID )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3159:1: (lv_op_1_0= RULE_ID )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3160:3: lv_op_1_0= RULE_ID
            {
            lv_op_1_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleUnitName6964); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(lv_op_1_0, grammarAccess.getUnitNameAccess().getOpIDTerminalRuleCall_1_0()); 
              		
            }
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElement(grammarAccess.getUnitNameRule());
              	        }
                     		setWithLastConsumed(
                     			current, 
                     			"op",
                      		lv_op_1_0, 
                      		"ID");
              	    
            }

            }


            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleUnitName"


    // $ANTLR start "entryRuleVariableRef"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3184:1: entryRuleVariableRef returns [EObject current=null] : iv_ruleVariableRef= ruleVariableRef EOF ;
    public final EObject entryRuleVariableRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleVariableRef = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3185:2: (iv_ruleVariableRef= ruleVariableRef EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3186:2: iv_ruleVariableRef= ruleVariableRef EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getVariableRefRule()); 
            }
            pushFollow(FOLLOW_ruleVariableRef_in_entryRuleVariableRef7005);
            iv_ruleVariableRef=ruleVariableRef();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleVariableRef; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleVariableRef7015); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleVariableRef"


    // $ANTLR start "ruleVariableRef"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3193:1: ruleVariableRef returns [EObject current=null] : ( () ( (otherlv_1= RULE_ID ) ) ) ;
    public final EObject ruleVariableRef() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;

         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3196:28: ( ( () ( (otherlv_1= RULE_ID ) ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3197:1: ( () ( (otherlv_1= RULE_ID ) ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3197:1: ( () ( (otherlv_1= RULE_ID ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3197:2: () ( (otherlv_1= RULE_ID ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3197:2: ()
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3198:5: 
            {
            if ( state.backtracking==0 ) {

                      current = forceCreateModelElement(
                          grammarAccess.getVariableRefAccess().getVariableRefAction_0(),
                          current);
                  
            }

            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3203:2: ( (otherlv_1= RULE_ID ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3204:1: (otherlv_1= RULE_ID )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3204:1: (otherlv_1= RULE_ID )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3205:3: otherlv_1= RULE_ID
            {
            if ( state.backtracking==0 ) {

              			if (current==null) {
              	            current = createModelElement(grammarAccess.getVariableRefRule());
              	        }
                      
            }
            otherlv_1=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleVariableRef7069); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              		newLeafNode(otherlv_1, grammarAccess.getVariableRefAccess().getRefGamlVarRefCrossReference_1_0()); 
              	
            }

            }


            }


            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleVariableRef"


    // $ANTLR start "entryRuleTerminalExpression"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3226:1: entryRuleTerminalExpression returns [EObject current=null] : iv_ruleTerminalExpression= ruleTerminalExpression EOF ;
    public final EObject entryRuleTerminalExpression() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTerminalExpression = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3227:2: (iv_ruleTerminalExpression= ruleTerminalExpression EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3228:2: iv_ruleTerminalExpression= ruleTerminalExpression EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getTerminalExpressionRule()); 
            }
            pushFollow(FOLLOW_ruleTerminalExpression_in_entryRuleTerminalExpression7107);
            iv_ruleTerminalExpression=ruleTerminalExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleTerminalExpression; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleTerminalExpression7117); if (state.failed) return current;

            }

        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "entryRuleTerminalExpression"


    // $ANTLR start "ruleTerminalExpression"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3235:1: ruleTerminalExpression returns [EObject current=null] : ( ( () ( (lv_value_1_0= RULE_INTEGER ) ) ) | ( () ( (lv_value_3_0= RULE_DOUBLE ) ) ) | ( () ( (lv_value_5_0= RULE_COLOR ) ) ) | ( () ( (lv_value_7_0= RULE_STRING ) ) ) | ( () ( (lv_value_9_0= RULE_BOOLEAN ) ) ) ) ;
    public final EObject ruleTerminalExpression() throws RecognitionException {
        EObject current = null;

        Token lv_value_1_0=null;
        Token lv_value_3_0=null;
        Token lv_value_5_0=null;
        Token lv_value_7_0=null;
        Token lv_value_9_0=null;

         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3238:28: ( ( ( () ( (lv_value_1_0= RULE_INTEGER ) ) ) | ( () ( (lv_value_3_0= RULE_DOUBLE ) ) ) | ( () ( (lv_value_5_0= RULE_COLOR ) ) ) | ( () ( (lv_value_7_0= RULE_STRING ) ) ) | ( () ( (lv_value_9_0= RULE_BOOLEAN ) ) ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3239:1: ( ( () ( (lv_value_1_0= RULE_INTEGER ) ) ) | ( () ( (lv_value_3_0= RULE_DOUBLE ) ) ) | ( () ( (lv_value_5_0= RULE_COLOR ) ) ) | ( () ( (lv_value_7_0= RULE_STRING ) ) ) | ( () ( (lv_value_9_0= RULE_BOOLEAN ) ) ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3239:1: ( ( () ( (lv_value_1_0= RULE_INTEGER ) ) ) | ( () ( (lv_value_3_0= RULE_DOUBLE ) ) ) | ( () ( (lv_value_5_0= RULE_COLOR ) ) ) | ( () ( (lv_value_7_0= RULE_STRING ) ) ) | ( () ( (lv_value_9_0= RULE_BOOLEAN ) ) ) )
            int alt55=5;
            switch ( input.LA(1) ) {
            case RULE_INTEGER:
                {
                alt55=1;
                }
                break;
            case RULE_DOUBLE:
                {
                alt55=2;
                }
                break;
            case RULE_COLOR:
                {
                alt55=3;
                }
                break;
            case RULE_STRING:
                {
                alt55=4;
                }
                break;
            case RULE_BOOLEAN:
                {
                alt55=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 55, 0, input);

                throw nvae;
            }

            switch (alt55) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3239:2: ( () ( (lv_value_1_0= RULE_INTEGER ) ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3239:2: ( () ( (lv_value_1_0= RULE_INTEGER ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3239:3: () ( (lv_value_1_0= RULE_INTEGER ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3239:3: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3240:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getTerminalExpressionAccess().getIntLiteralAction_0_0(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3245:2: ( (lv_value_1_0= RULE_INTEGER ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3246:1: (lv_value_1_0= RULE_INTEGER )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3246:1: (lv_value_1_0= RULE_INTEGER )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3247:3: lv_value_1_0= RULE_INTEGER
                    {
                    lv_value_1_0=(Token)match(input,RULE_INTEGER,FOLLOW_RULE_INTEGER_in_ruleTerminalExpression7169); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			newLeafNode(lv_value_1_0, grammarAccess.getTerminalExpressionAccess().getValueINTEGERTerminalRuleCall_0_1_0()); 
                      		
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getTerminalExpressionRule());
                      	        }
                             		setWithLastConsumed(
                             			current, 
                             			"value",
                              		lv_value_1_0, 
                              		"INTEGER");
                      	    
                    }

                    }


                    }


                    }


                    }
                    break;
                case 2 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3264:6: ( () ( (lv_value_3_0= RULE_DOUBLE ) ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3264:6: ( () ( (lv_value_3_0= RULE_DOUBLE ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3264:7: () ( (lv_value_3_0= RULE_DOUBLE ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3264:7: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3265:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getTerminalExpressionAccess().getDoubleLiteralAction_1_0(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3270:2: ( (lv_value_3_0= RULE_DOUBLE ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3271:1: (lv_value_3_0= RULE_DOUBLE )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3271:1: (lv_value_3_0= RULE_DOUBLE )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3272:3: lv_value_3_0= RULE_DOUBLE
                    {
                    lv_value_3_0=(Token)match(input,RULE_DOUBLE,FOLLOW_RULE_DOUBLE_in_ruleTerminalExpression7208); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			newLeafNode(lv_value_3_0, grammarAccess.getTerminalExpressionAccess().getValueDOUBLETerminalRuleCall_1_1_0()); 
                      		
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getTerminalExpressionRule());
                      	        }
                             		setWithLastConsumed(
                             			current, 
                             			"value",
                              		lv_value_3_0, 
                              		"DOUBLE");
                      	    
                    }

                    }


                    }


                    }


                    }
                    break;
                case 3 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3289:6: ( () ( (lv_value_5_0= RULE_COLOR ) ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3289:6: ( () ( (lv_value_5_0= RULE_COLOR ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3289:7: () ( (lv_value_5_0= RULE_COLOR ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3289:7: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3290:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getTerminalExpressionAccess().getColorLiteralAction_2_0(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3295:2: ( (lv_value_5_0= RULE_COLOR ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3296:1: (lv_value_5_0= RULE_COLOR )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3296:1: (lv_value_5_0= RULE_COLOR )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3297:3: lv_value_5_0= RULE_COLOR
                    {
                    lv_value_5_0=(Token)match(input,RULE_COLOR,FOLLOW_RULE_COLOR_in_ruleTerminalExpression7247); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			newLeafNode(lv_value_5_0, grammarAccess.getTerminalExpressionAccess().getValueCOLORTerminalRuleCall_2_1_0()); 
                      		
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getTerminalExpressionRule());
                      	        }
                             		setWithLastConsumed(
                             			current, 
                             			"value",
                              		lv_value_5_0, 
                              		"COLOR");
                      	    
                    }

                    }


                    }


                    }


                    }
                    break;
                case 4 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3314:6: ( () ( (lv_value_7_0= RULE_STRING ) ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3314:6: ( () ( (lv_value_7_0= RULE_STRING ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3314:7: () ( (lv_value_7_0= RULE_STRING ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3314:7: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3315:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getTerminalExpressionAccess().getStringLiteralAction_3_0(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3320:2: ( (lv_value_7_0= RULE_STRING ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3321:1: (lv_value_7_0= RULE_STRING )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3321:1: (lv_value_7_0= RULE_STRING )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3322:3: lv_value_7_0= RULE_STRING
                    {
                    lv_value_7_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleTerminalExpression7286); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			newLeafNode(lv_value_7_0, grammarAccess.getTerminalExpressionAccess().getValueSTRINGTerminalRuleCall_3_1_0()); 
                      		
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getTerminalExpressionRule());
                      	        }
                             		setWithLastConsumed(
                             			current, 
                             			"value",
                              		lv_value_7_0, 
                              		"STRING");
                      	    
                    }

                    }


                    }


                    }


                    }
                    break;
                case 5 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3339:6: ( () ( (lv_value_9_0= RULE_BOOLEAN ) ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3339:6: ( () ( (lv_value_9_0= RULE_BOOLEAN ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3339:7: () ( (lv_value_9_0= RULE_BOOLEAN ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3339:7: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3340:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getTerminalExpressionAccess().getBooleanLiteralAction_4_0(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3345:2: ( (lv_value_9_0= RULE_BOOLEAN ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3346:1: (lv_value_9_0= RULE_BOOLEAN )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3346:1: (lv_value_9_0= RULE_BOOLEAN )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3347:3: lv_value_9_0= RULE_BOOLEAN
                    {
                    lv_value_9_0=(Token)match(input,RULE_BOOLEAN,FOLLOW_RULE_BOOLEAN_in_ruleTerminalExpression7325); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			newLeafNode(lv_value_9_0, grammarAccess.getTerminalExpressionAccess().getValueBOOLEANTerminalRuleCall_4_1_0()); 
                      		
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getTerminalExpressionRule());
                      	        }
                             		setWithLastConsumed(
                             			current, 
                             			"value",
                              		lv_value_9_0, 
                              		"BOOLEAN");
                      	    
                    }

                    }


                    }


                    }


                    }
                    break;

            }


            }

            if ( state.backtracking==0 ) {
               leaveRule(); 
            }
        }
         
            catch (RecognitionException re) { 
                recover(input,re); 
                appendSkippedTokens();
            } 
        finally {
        }
        return current;
    }
    // $ANTLR end "ruleTerminalExpression"

    // $ANTLR start synpred1_InternalGaml
    public final void synpred1_InternalGaml_fragment() throws RecognitionException {   
        // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:355:3: ( ruleAssignmentStatement )
        // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:355:5: ruleAssignmentStatement
        {
        pushFollow(FOLLOW_ruleAssignmentStatement_in_synpred1_InternalGaml808);
        ruleAssignmentStatement();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_InternalGaml

    // $ANTLR start synpred2_InternalGaml
    public final void synpred2_InternalGaml_fragment() throws RecognitionException {   
        // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:566:4: ( 'else' )
        // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:566:6: 'else'
        {
        match(input,39,FOLLOW_39_in_synpred2_InternalGaml1289); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred2_InternalGaml

    // $ANTLR start synpred3_InternalGaml
    public final void synpred3_InternalGaml_fragment() throws RecognitionException {   
        // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2089:3: ( ruleArgPairExpr )
        // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2089:5: ruleArgPairExpr
        {
        pushFollow(FOLLOW_ruleArgPairExpr_in_synpred3_InternalGaml4573);
        ruleArgPairExpr();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred3_InternalGaml

    // Delegated rules

    public final boolean synpred3_InternalGaml() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred3_InternalGaml_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred1_InternalGaml() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_InternalGaml_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred2_InternalGaml() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_InternalGaml_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA5 dfa5 = new DFA5(this);
    protected DFA35 dfa35 = new DFA35(this);
    static final String DFA5_eotS =
        "\50\uffff";
    static final String DFA5_eofS =
        "\50\uffff";
    static final String DFA5_minS =
        "\1\4\24\0\23\uffff";
    static final String DFA5_maxS =
        "\1\120\24\0\23\uffff";
    static final String DFA5_acceptS =
        "\25\uffff\21\1\1\2\1\uffff";
    static final String DFA5_specialS =
        "\1\0\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13\1\14\1\15\1"+
        "\16\1\17\1\20\1\21\1\22\1\23\1\24\23\uffff}>";
    static final String[] DFA5_transitionS = {
            "\1\1\1\33\1\30\1\31\1\32\1\34\6\uffff\1\2\1\3\1\4\1\5\1\6\1"+
            "\7\1\10\1\11\1\12\1\13\1\14\1\15\1\16\1\17\1\20\1\21\1\22\1"+
            "\23\1\24\2\uffff\1\46\6\uffff\1\46\11\uffff\1\37\1\uffff\1\25"+
            "\1\26\1\27\10\uffff\1\41\4\uffff\1\40\1\42\1\43\1\44\1\45\1"+
            "\36\2\uffff\1\35",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
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
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
    };

    static final short[] DFA5_eot = DFA.unpackEncodedString(DFA5_eotS);
    static final short[] DFA5_eof = DFA.unpackEncodedString(DFA5_eofS);
    static final char[] DFA5_min = DFA.unpackEncodedStringToUnsignedChars(DFA5_minS);
    static final char[] DFA5_max = DFA.unpackEncodedStringToUnsignedChars(DFA5_maxS);
    static final short[] DFA5_accept = DFA.unpackEncodedString(DFA5_acceptS);
    static final short[] DFA5_special = DFA.unpackEncodedString(DFA5_specialS);
    static final short[][] DFA5_transition;

    static {
        int numStates = DFA5_transitionS.length;
        DFA5_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA5_transition[i] = DFA.unpackEncodedString(DFA5_transitionS[i]);
        }
    }

    class DFA5 extends DFA {

        public DFA5(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 5;
            this.eot = DFA5_eot;
            this.eof = DFA5_eof;
            this.min = DFA5_min;
            this.max = DFA5_max;
            this.accept = DFA5_accept;
            this.special = DFA5_special;
            this.transition = DFA5_transition;
        }
        public String getDescription() {
            return "355:1: ( ( ( ruleAssignmentStatement )=>this_AssignmentStatement_0= ruleAssignmentStatement ) | (this_ReturnStatement_1= ruleReturnStatement | this_IfStatement_2= ruleIfStatement | this_ClassicStatement_3= ruleClassicStatement | this_DefinitionStatement_4= ruleDefinitionStatement | this_Equation_5= ruleEquation ) )";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA5_0 = input.LA(1);

                         
                        int index5_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA5_0==RULE_ID) ) {s = 1;}

                        else if ( (LA5_0==16) ) {s = 2;}

                        else if ( (LA5_0==17) ) {s = 3;}

                        else if ( (LA5_0==18) ) {s = 4;}

                        else if ( (LA5_0==19) ) {s = 5;}

                        else if ( (LA5_0==20) ) {s = 6;}

                        else if ( (LA5_0==21) ) {s = 7;}

                        else if ( (LA5_0==22) ) {s = 8;}

                        else if ( (LA5_0==23) ) {s = 9;}

                        else if ( (LA5_0==24) ) {s = 10;}

                        else if ( (LA5_0==25) ) {s = 11;}

                        else if ( (LA5_0==26) ) {s = 12;}

                        else if ( (LA5_0==27) ) {s = 13;}

                        else if ( (LA5_0==28) ) {s = 14;}

                        else if ( (LA5_0==29) ) {s = 15;}

                        else if ( (LA5_0==30) ) {s = 16;}

                        else if ( (LA5_0==31) ) {s = 17;}

                        else if ( (LA5_0==32) ) {s = 18;}

                        else if ( (LA5_0==33) ) {s = 19;}

                        else if ( (LA5_0==34) ) {s = 20;}

                        else if ( (LA5_0==56) && (synpred1_InternalGaml())) {s = 21;}

                        else if ( (LA5_0==57) && (synpred1_InternalGaml())) {s = 22;}

                        else if ( (LA5_0==58) && (synpred1_InternalGaml())) {s = 23;}

                        else if ( (LA5_0==RULE_INTEGER) && (synpred1_InternalGaml())) {s = 24;}

                        else if ( (LA5_0==RULE_DOUBLE) && (synpred1_InternalGaml())) {s = 25;}

                        else if ( (LA5_0==RULE_COLOR) && (synpred1_InternalGaml())) {s = 26;}

                        else if ( (LA5_0==RULE_STRING) && (synpred1_InternalGaml())) {s = 27;}

                        else if ( (LA5_0==RULE_BOOLEAN) && (synpred1_InternalGaml())) {s = 28;}

                        else if ( (LA5_0==80) && (synpred1_InternalGaml())) {s = 29;}

                        else if ( (LA5_0==77) && (synpred1_InternalGaml())) {s = 30;}

                        else if ( (LA5_0==54) && (synpred1_InternalGaml())) {s = 31;}

                        else if ( (LA5_0==72) && (synpred1_InternalGaml())) {s = 32;}

                        else if ( (LA5_0==67) && (synpred1_InternalGaml())) {s = 33;}

                        else if ( (LA5_0==73) && (synpred1_InternalGaml())) {s = 34;}

                        else if ( (LA5_0==74) && (synpred1_InternalGaml())) {s = 35;}

                        else if ( (LA5_0==75) && (synpred1_InternalGaml())) {s = 36;}

                        else if ( (LA5_0==76) && (synpred1_InternalGaml())) {s = 37;}

                        else if ( (LA5_0==37||LA5_0==44) ) {s = 38;}

                         
                        input.seek(index5_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA5_1 = input.LA(1);

                         
                        int index5_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_InternalGaml()) ) {s = 37;}

                        else if ( (true) ) {s = 38;}

                         
                        input.seek(index5_1);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA5_2 = input.LA(1);

                         
                        int index5_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_InternalGaml()) ) {s = 37;}

                        else if ( (true) ) {s = 38;}

                         
                        input.seek(index5_2);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA5_3 = input.LA(1);

                         
                        int index5_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_InternalGaml()) ) {s = 37;}

                        else if ( (true) ) {s = 38;}

                         
                        input.seek(index5_3);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA5_4 = input.LA(1);

                         
                        int index5_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_InternalGaml()) ) {s = 37;}

                        else if ( (true) ) {s = 38;}

                         
                        input.seek(index5_4);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA5_5 = input.LA(1);

                         
                        int index5_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_InternalGaml()) ) {s = 37;}

                        else if ( (true) ) {s = 38;}

                         
                        input.seek(index5_5);
                        if ( s>=0 ) return s;
                        break;
                    case 6 : 
                        int LA5_6 = input.LA(1);

                         
                        int index5_6 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_InternalGaml()) ) {s = 37;}

                        else if ( (true) ) {s = 38;}

                         
                        input.seek(index5_6);
                        if ( s>=0 ) return s;
                        break;
                    case 7 : 
                        int LA5_7 = input.LA(1);

                         
                        int index5_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_InternalGaml()) ) {s = 37;}

                        else if ( (true) ) {s = 38;}

                         
                        input.seek(index5_7);
                        if ( s>=0 ) return s;
                        break;
                    case 8 : 
                        int LA5_8 = input.LA(1);

                         
                        int index5_8 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_InternalGaml()) ) {s = 37;}

                        else if ( (true) ) {s = 38;}

                         
                        input.seek(index5_8);
                        if ( s>=0 ) return s;
                        break;
                    case 9 : 
                        int LA5_9 = input.LA(1);

                         
                        int index5_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_InternalGaml()) ) {s = 37;}

                        else if ( (true) ) {s = 38;}

                         
                        input.seek(index5_9);
                        if ( s>=0 ) return s;
                        break;
                    case 10 : 
                        int LA5_10 = input.LA(1);

                         
                        int index5_10 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_InternalGaml()) ) {s = 37;}

                        else if ( (true) ) {s = 38;}

                         
                        input.seek(index5_10);
                        if ( s>=0 ) return s;
                        break;
                    case 11 : 
                        int LA5_11 = input.LA(1);

                         
                        int index5_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_InternalGaml()) ) {s = 37;}

                        else if ( (true) ) {s = 38;}

                         
                        input.seek(index5_11);
                        if ( s>=0 ) return s;
                        break;
                    case 12 : 
                        int LA5_12 = input.LA(1);

                         
                        int index5_12 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_InternalGaml()) ) {s = 37;}

                        else if ( (true) ) {s = 38;}

                         
                        input.seek(index5_12);
                        if ( s>=0 ) return s;
                        break;
                    case 13 : 
                        int LA5_13 = input.LA(1);

                         
                        int index5_13 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_InternalGaml()) ) {s = 37;}

                        else if ( (true) ) {s = 38;}

                         
                        input.seek(index5_13);
                        if ( s>=0 ) return s;
                        break;
                    case 14 : 
                        int LA5_14 = input.LA(1);

                         
                        int index5_14 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_InternalGaml()) ) {s = 37;}

                        else if ( (true) ) {s = 38;}

                         
                        input.seek(index5_14);
                        if ( s>=0 ) return s;
                        break;
                    case 15 : 
                        int LA5_15 = input.LA(1);

                         
                        int index5_15 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_InternalGaml()) ) {s = 37;}

                        else if ( (true) ) {s = 38;}

                         
                        input.seek(index5_15);
                        if ( s>=0 ) return s;
                        break;
                    case 16 : 
                        int LA5_16 = input.LA(1);

                         
                        int index5_16 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_InternalGaml()) ) {s = 37;}

                        else if ( (true) ) {s = 38;}

                         
                        input.seek(index5_16);
                        if ( s>=0 ) return s;
                        break;
                    case 17 : 
                        int LA5_17 = input.LA(1);

                         
                        int index5_17 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_InternalGaml()) ) {s = 37;}

                        else if ( (true) ) {s = 38;}

                         
                        input.seek(index5_17);
                        if ( s>=0 ) return s;
                        break;
                    case 18 : 
                        int LA5_18 = input.LA(1);

                         
                        int index5_18 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_InternalGaml()) ) {s = 37;}

                        else if ( (true) ) {s = 38;}

                         
                        input.seek(index5_18);
                        if ( s>=0 ) return s;
                        break;
                    case 19 : 
                        int LA5_19 = input.LA(1);

                         
                        int index5_19 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_InternalGaml()) ) {s = 37;}

                        else if ( (true) ) {s = 38;}

                         
                        input.seek(index5_19);
                        if ( s>=0 ) return s;
                        break;
                    case 20 : 
                        int LA5_20 = input.LA(1);

                         
                        int index5_20 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_InternalGaml()) ) {s = 37;}

                        else if ( (true) ) {s = 38;}

                         
                        input.seek(index5_20);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 5, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA35_eotS =
        "\46\uffff";
    static final String DFA35_eofS =
        "\46\uffff";
    static final String DFA35_minS =
        "\1\4\1\0\44\uffff";
    static final String DFA35_maxS =
        "\1\120\1\0\44\uffff";
    static final String DFA35_acceptS =
        "\2\uffff\26\1\1\2\15\uffff";
    static final String DFA35_specialS =
        "\1\0\1\1\44\uffff}>";
    static final String[] DFA35_transitionS = {
            "\1\1\5\30\6\uffff\1\2\1\3\1\4\1\5\1\6\1\7\1\10\1\11\1\12\1\13"+
            "\1\14\1\15\1\16\1\17\1\20\1\21\1\22\1\23\1\24\23\uffff\1\30"+
            "\1\uffff\1\25\1\26\1\27\10\uffff\1\30\4\uffff\6\30\2\uffff\1"+
            "\30",
            "\1\uffff",
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
            "",
            "",
            ""
    };

    static final short[] DFA35_eot = DFA.unpackEncodedString(DFA35_eotS);
    static final short[] DFA35_eof = DFA.unpackEncodedString(DFA35_eofS);
    static final char[] DFA35_min = DFA.unpackEncodedStringToUnsignedChars(DFA35_minS);
    static final char[] DFA35_max = DFA.unpackEncodedStringToUnsignedChars(DFA35_maxS);
    static final short[] DFA35_accept = DFA.unpackEncodedString(DFA35_acceptS);
    static final short[] DFA35_special = DFA.unpackEncodedString(DFA35_specialS);
    static final short[][] DFA35_transition;

    static {
        int numStates = DFA35_transitionS.length;
        DFA35_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA35_transition[i] = DFA.unpackEncodedString(DFA35_transitionS[i]);
        }
    }

    class DFA35 extends DFA {

        public DFA35(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 35;
            this.eot = DFA35_eot;
            this.eof = DFA35_eof;
            this.min = DFA35_min;
            this.max = DFA35_max;
            this.accept = DFA35_accept;
            this.special = DFA35_special;
            this.transition = DFA35_transition;
        }
        public String getDescription() {
            return "2089:1: ( ( ( ruleArgPairExpr )=>this_ArgPairExpr_0= ruleArgPairExpr ) | (this_Addition_1= ruleAddition ( ( () ( (lv_op_3_0= '::' ) ) ) ( (lv_right_4_0= ruleAddition ) ) )? ) )";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA35_0 = input.LA(1);

                         
                        int index35_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA35_0==RULE_ID) ) {s = 1;}

                        else if ( (LA35_0==16) && (synpred3_InternalGaml())) {s = 2;}

                        else if ( (LA35_0==17) && (synpred3_InternalGaml())) {s = 3;}

                        else if ( (LA35_0==18) && (synpred3_InternalGaml())) {s = 4;}

                        else if ( (LA35_0==19) && (synpred3_InternalGaml())) {s = 5;}

                        else if ( (LA35_0==20) && (synpred3_InternalGaml())) {s = 6;}

                        else if ( (LA35_0==21) && (synpred3_InternalGaml())) {s = 7;}

                        else if ( (LA35_0==22) && (synpred3_InternalGaml())) {s = 8;}

                        else if ( (LA35_0==23) && (synpred3_InternalGaml())) {s = 9;}

                        else if ( (LA35_0==24) && (synpred3_InternalGaml())) {s = 10;}

                        else if ( (LA35_0==25) && (synpred3_InternalGaml())) {s = 11;}

                        else if ( (LA35_0==26) && (synpred3_InternalGaml())) {s = 12;}

                        else if ( (LA35_0==27) && (synpred3_InternalGaml())) {s = 13;}

                        else if ( (LA35_0==28) && (synpred3_InternalGaml())) {s = 14;}

                        else if ( (LA35_0==29) && (synpred3_InternalGaml())) {s = 15;}

                        else if ( (LA35_0==30) && (synpred3_InternalGaml())) {s = 16;}

                        else if ( (LA35_0==31) && (synpred3_InternalGaml())) {s = 17;}

                        else if ( (LA35_0==32) && (synpred3_InternalGaml())) {s = 18;}

                        else if ( (LA35_0==33) && (synpred3_InternalGaml())) {s = 19;}

                        else if ( (LA35_0==34) && (synpred3_InternalGaml())) {s = 20;}

                        else if ( (LA35_0==56) && (synpred3_InternalGaml())) {s = 21;}

                        else if ( (LA35_0==57) && (synpred3_InternalGaml())) {s = 22;}

                        else if ( (LA35_0==58) && (synpred3_InternalGaml())) {s = 23;}

                        else if ( ((LA35_0>=RULE_STRING && LA35_0<=RULE_BOOLEAN)||LA35_0==54||LA35_0==67||(LA35_0>=72 && LA35_0<=77)||LA35_0==80) ) {s = 24;}

                         
                        input.seek(index35_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA35_1 = input.LA(1);

                         
                        int index35_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred3_InternalGaml()) ) {s = 23;}

                        else if ( (true) ) {s = 24;}

                         
                        input.seek(index35_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 35, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_ruleModel_in_entryRuleModel75 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleModel85 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_14_in_ruleModel122 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleModel139 = new BitSet(new long[]{0x07401027FFFF83F2L,0x0000000000013F08L});
    public static final BitSet FOLLOW_ruleImport_in_ruleModel165 = new BitSet(new long[]{0x07401027FFFF83F2L,0x0000000000013F08L});
    public static final BitSet FOLLOW_ruleStatement_in_ruleModel187 = new BitSet(new long[]{0x07401027FFFF03F2L,0x0000000000013F08L});
    public static final BitSet FOLLOW_ruleImport_in_entryRuleImport224 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleImport234 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_15_in_ruleImport271 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleImport288 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleBuiltInStatementKey_in_entryRuleBuiltInStatementKey330 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleBuiltInStatementKey341 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_16_in_ruleBuiltInStatementKey379 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_ruleBuiltInStatementKey398 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_18_in_ruleBuiltInStatementKey417 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_19_in_ruleBuiltInStatementKey436 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_ruleBuiltInStatementKey455 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_ruleBuiltInStatementKey474 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_ruleBuiltInStatementKey493 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_ruleBuiltInStatementKey512 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_24_in_ruleBuiltInStatementKey531 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_ruleBuiltInStatementKey550 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_ruleBuiltInStatementKey569 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_ruleBuiltInStatementKey588 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_ruleBuiltInStatementKey607 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_ruleBuiltInStatementKey626 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_ruleBuiltInStatementKey645 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_ruleBuiltInStatementKey664 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_ruleBuiltInStatementKey683 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_ruleBuiltInStatementKey702 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_ruleBuiltInStatementKey721 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleStatement_in_entryRuleStatement761 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleStatement771 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAssignmentStatement_in_ruleStatement824 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleReturnStatement_in_ruleStatement853 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleIfStatement_in_ruleStatement880 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleClassicStatement_in_ruleStatement907 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDefinitionStatement_in_ruleStatement934 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEquation_in_ruleStatement961 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleEquation_in_entryRuleEquation997 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleEquation1007 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFunction_in_ruleEquation1053 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_ruleEquation1071 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_ruleExpression_in_ruleEquation1105 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_36_in_ruleEquation1117 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleIfStatement_in_entryRuleIfStatement1153 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleIfStatement1163 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_ruleIfStatement1206 = new BitSet(new long[]{0x07400047FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_38_in_ruleIfStatement1232 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_ruleExpression_in_ruleIfStatement1255 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_ruleBlock_in_ruleIfStatement1276 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_39_in_ruleIfStatement1297 = new BitSet(new long[]{0x0040002000000000L});
    public static final BitSet FOLLOW_ruleIfStatement_in_ruleIfStatement1321 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleBlock_in_ruleIfStatement1340 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleClassicStatement_in_entryRuleClassicStatement1381 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleClassicStatement1391 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleBuiltInStatementKey_in_ruleClassicStatement1437 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleClassicStatement1449 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_ruleClassicStatement1460 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_ruleExpression_in_ruleClassicStatement1483 = new BitSet(new long[]{0x07702017FFFF0010L});
    public static final BitSet FOLLOW_ruleFacet_in_ruleClassicStatement1504 = new BitSet(new long[]{0x07702017FFFF0010L});
    public static final BitSet FOLLOW_ruleBlock_in_ruleClassicStatement1527 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_ruleClassicStatement1545 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDefinitionStatement_in_entryRuleDefinitionStatement1582 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDefinitionStatement1592 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleDefinitionStatement1634 = new BitSet(new long[]{0x07702217FFFF0030L});
    public static final BitSet FOLLOW_ruleContents_in_ruleDefinitionStatement1660 = new BitSet(new long[]{0x07702017FFFF0030L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleDefinitionStatement1680 = new BitSet(new long[]{0x07702017FFFF0010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleDefinitionStatement1700 = new BitSet(new long[]{0x07702017FFFF0010L});
    public static final BitSet FOLLOW_ruleBuiltInStatementKey_in_ruleDefinitionStatement1724 = new BitSet(new long[]{0x07702017FFFF0010L});
    public static final BitSet FOLLOW_ruleFacet_in_ruleDefinitionStatement1749 = new BitSet(new long[]{0x07702017FFFF0010L});
    public static final BitSet FOLLOW_ruleBlock_in_ruleDefinitionStatement1772 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_ruleDefinitionStatement1790 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleContents_in_entryRuleContents1827 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleContents1837 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_ruleContents1874 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleContents1891 = new BitSet(new long[]{0x00000C0000000000L});
    public static final BitSet FOLLOW_42_in_ruleContents1909 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleContents1926 = new BitSet(new long[]{0x0000080000000000L});
    public static final BitSet FOLLOW_43_in_ruleContents1945 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleReturnStatement_in_entryRuleReturnStatement1981 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleReturnStatement1991 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_ruleReturnStatement2034 = new BitSet(new long[]{0x07400017FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_ruleExpression_in_ruleReturnStatement2068 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_36_in_ruleReturnStatement2081 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAssignmentStatement_in_entryRuleAssignmentStatement2117 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAssignmentStatement2127 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleExpression_in_ruleAssignmentStatement2173 = new BitSet(new long[]{0x000FE00000000000L});
    public static final BitSet FOLLOW_45_in_ruleAssignmentStatement2193 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_46_in_ruleAssignmentStatement2222 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_47_in_ruleAssignmentStatement2251 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_48_in_ruleAssignmentStatement2280 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_49_in_ruleAssignmentStatement2309 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_50_in_ruleAssignmentStatement2338 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_51_in_ruleAssignmentStatement2367 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_ruleExpression_in_ruleAssignmentStatement2404 = new BitSet(new long[]{0x07302017FFFF0010L});
    public static final BitSet FOLLOW_ruleFacet_in_ruleAssignmentStatement2425 = new BitSet(new long[]{0x07302017FFFF0010L});
    public static final BitSet FOLLOW_36_in_ruleAssignmentStatement2438 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFacet_in_entryRuleFacet2474 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleFacet2484 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFunctionFacet_in_ruleFacet2531 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDefinitionFacet_in_ruleFacet2558 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleClassicFacet_in_ruleFacet2585 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleClassicFacet_in_entryRuleClassicFacet2620 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleClassicFacet2630 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleClassicFacet2674 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_ruleClassicFacet2691 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_45_in_ruleClassicFacet2716 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_ruleExpression_in_ruleClassicFacet2751 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFunctionFacet_in_entryRuleFunctionFacet2787 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleFunctionFacet2797 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_52_in_ruleFunctionFacet2841 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_53_in_ruleFunctionFacet2878 = new BitSet(new long[]{0x0040000000000000L});
    public static final BitSet FOLLOW_54_in_ruleFunctionFacet2904 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_ruleExpression_in_ruleFunctionFacet2925 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_55_in_ruleFunctionFacet2937 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDefinitionFacetKey_in_entryRuleDefinitionFacetKey2974 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDefinitionFacetKey2985 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_56_in_ruleDefinitionFacetKey3023 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_57_in_ruleDefinitionFacetKey3042 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_58_in_ruleDefinitionFacetKey3061 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDefinitionFacet_in_entryRuleDefinitionFacet3101 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDefinitionFacet3111 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDefinitionFacetKey_in_ruleDefinitionFacet3157 = new BitSet(new long[]{0x00000007FFFF0030L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleDefinitionFacet3176 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleDefinitionFacet3196 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleBuiltInStatementKey_in_ruleDefinitionFacet3220 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleBlock_in_entryRuleBlock3259 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleBlock3269 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_54_in_ruleBlock3315 = new BitSet(new long[]{0x07C01027FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_ruleStatement_in_ruleBlock3336 = new BitSet(new long[]{0x07C01027FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_55_in_ruleBlock3349 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleExpression_in_entryRuleExpression3385 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleExpression3395 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTernExp_in_ruleExpression3441 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTernExp_in_entryRuleTernExp3475 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTernExp3485 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOrExp_in_ruleTernExp3532 = new BitSet(new long[]{0x0800000000000002L});
    public static final BitSet FOLLOW_59_in_ruleTernExp3559 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_ruleOrExp_in_ruleTernExp3593 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_ruleTernExp3605 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_ruleOrExp_in_ruleTernExp3626 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOrExp_in_entryRuleOrExp3664 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOrExp3674 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAndExp_in_ruleOrExp3721 = new BitSet(new long[]{0x1000000000000002L});
    public static final BitSet FOLLOW_60_in_ruleOrExp3748 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_ruleAndExp_in_ruleOrExp3782 = new BitSet(new long[]{0x1000000000000002L});
    public static final BitSet FOLLOW_ruleAndExp_in_entryRuleAndExp3820 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAndExp3830 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelational_in_ruleAndExp3877 = new BitSet(new long[]{0x2000000000000002L});
    public static final BitSet FOLLOW_61_in_ruleAndExp3904 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_ruleRelational_in_ruleAndExp3938 = new BitSet(new long[]{0x2000000000000002L});
    public static final BitSet FOLLOW_ruleRelational_in_entryRuleRelational3976 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRelational3986 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePairExpr_in_ruleRelational4033 = new BitSet(new long[]{0xC0000A0800000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_62_in_ruleRelational4063 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_35_in_ruleRelational4092 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_63_in_ruleRelational4121 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_64_in_ruleRelational4150 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_41_in_ruleRelational4179 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_43_in_ruleRelational4208 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_rulePairExpr_in_ruleRelational4246 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArgPairExpr_in_entryRuleArgPairExpr4284 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleArgPairExpr4294 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleArgPairExpr4349 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_ruleBuiltInStatementKey_in_ruleArgPairExpr4373 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_ruleArgPairExpr4394 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_ruleDefinitionFacetKey_in_ruleArgPairExpr4436 = new BitSet(new long[]{0x0000010000000000L});
    public static final BitSet FOLLOW_40_in_ruleArgPairExpr4454 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_ruleAddition_in_ruleArgPairExpr4490 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePairExpr_in_entryRulePairExpr4526 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRulePairExpr4536 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArgPairExpr_in_rulePairExpr4589 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAddition_in_rulePairExpr4618 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_rulePairExpr4646 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_ruleAddition_in_rulePairExpr4681 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAddition_in_entryRuleAddition4720 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAddition4730 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleMultiplication_in_ruleAddition4777 = new BitSet(new long[]{0x0000000000000002L,0x000000000000000CL});
    public static final BitSet FOLLOW_66_in_ruleAddition4807 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_67_in_ruleAddition4836 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_ruleMultiplication_in_ruleAddition4874 = new BitSet(new long[]{0x0000000000000002L,0x000000000000000CL});
    public static final BitSet FOLLOW_ruleMultiplication_in_entryRuleMultiplication4912 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleMultiplication4922 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleGamlBinaryExpr_in_ruleMultiplication4969 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000070L});
    public static final BitSet FOLLOW_68_in_ruleMultiplication4999 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_69_in_ruleMultiplication5028 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_70_in_ruleMultiplication5057 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_ruleGamlBinaryExpr_in_ruleMultiplication5095 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000070L});
    public static final BitSet FOLLOW_ruleGamlBinaryExpr_in_entryRuleGamlBinaryExpr5133 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleGamlBinaryExpr5143 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleGamlUnitExpr_in_ruleGamlBinaryExpr5190 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleGamlBinaryExpr5217 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_ruleGamlUnitExpr_in_ruleGamlBinaryExpr5244 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_ruleGamlUnitExpr_in_entryRuleGamlUnitExpr5282 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleGamlUnitExpr5292 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleGamlUnaryExpr_in_ruleGamlUnitExpr5339 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000180L});
    public static final BitSet FOLLOW_71_in_ruleGamlUnitExpr5369 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_72_in_ruleGamlUnitExpr5398 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleUnitName_in_ruleGamlUnitExpr5436 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleGamlUnaryExpr_in_entryRuleGamlUnaryExpr5474 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleGamlUnaryExpr5484 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAccess_in_ruleGamlUnaryExpr5531 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_72_in_ruleGamlUnaryExpr5576 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleUnitName_in_ruleGamlUnaryExpr5610 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_67_in_ruleGamlUnaryExpr5639 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_73_in_ruleGamlUnaryExpr5668 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_74_in_ruleGamlUnaryExpr5697 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_75_in_ruleGamlUnaryExpr5726 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_76_in_ruleGamlUnaryExpr5755 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_ruleGamlUnaryExpr_in_ruleGamlUnaryExpr5792 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAccess_in_entryRuleAccess5831 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAccess5841 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleMemberRef_in_ruleAccess5888 = new BitSet(new long[]{0x0000000000000002L,0x0000000000002000L});
    public static final BitSet FOLLOW_77_in_ruleAccess5910 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_ruleExpression_in_ruleAccess5932 = new BitSet(new long[]{0x0000040000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_42_in_ruleAccess5945 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_ruleExpression_in_ruleAccess5966 = new BitSet(new long[]{0x0000040000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_78_in_ruleAccess5980 = new BitSet(new long[]{0x0000000000000002L,0x0000000000002000L});
    public static final BitSet FOLLOW_ruleMemberRef_in_entryRuleMemberRef6018 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleMemberRef6028 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePrimaryExpression_in_ruleMemberRef6075 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_79_in_ruleMemberRef6103 = new BitSet(new long[]{0x00400000000003F0L,0x0000000000012000L});
    public static final BitSet FOLLOW_rulePrimaryExpression_in_ruleMemberRef6137 = new BitSet(new long[]{0x0000000000000002L,0x0000000000008000L});
    public static final BitSet FOLLOW_rulePrimaryExpression_in_entryRulePrimaryExpression6176 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRulePrimaryExpression6186 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTerminalExpression_in_rulePrimaryExpression6233 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAbstractRef_in_rulePrimaryExpression6260 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_80_in_rulePrimaryExpression6278 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_ruleExpression_in_rulePrimaryExpression6300 = new BitSet(new long[]{0x0000000000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_81_in_rulePrimaryExpression6311 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_77_in_rulePrimaryExpression6331 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000017F08L});
    public static final BitSet FOLLOW_ruleExpression_in_rulePrimaryExpression6362 = new BitSet(new long[]{0x0000040000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_42_in_rulePrimaryExpression6375 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_ruleExpression_in_rulePrimaryExpression6396 = new BitSet(new long[]{0x0000040000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_78_in_rulePrimaryExpression6412 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_54_in_rulePrimaryExpression6432 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_ruleExpression_in_rulePrimaryExpression6462 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_rulePrimaryExpression6480 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_ruleExpression_in_rulePrimaryExpression6514 = new BitSet(new long[]{0x0080040000000000L});
    public static final BitSet FOLLOW_42_in_rulePrimaryExpression6527 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_ruleExpression_in_rulePrimaryExpression6548 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_55_in_rulePrimaryExpression6562 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAbstractRef_in_entryRuleAbstractRef6599 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAbstractRef6609 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleVariableRef_in_ruleAbstractRef6656 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFunction_in_ruleAbstractRef6683 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFunction_in_entryRuleFunction6718 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleFunction6728 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleFunction6780 = new BitSet(new long[]{0x0000000000000000L,0x0000000000010000L});
    public static final BitSet FOLLOW_80_in_ruleFunction6797 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_ruleExpression_in_ruleFunction6818 = new BitSet(new long[]{0x0000040000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_42_in_ruleFunction6831 = new BitSet(new long[]{0x07400007FFFF03F0L,0x0000000000013F08L});
    public static final BitSet FOLLOW_ruleExpression_in_ruleFunction6852 = new BitSet(new long[]{0x0000040000000000L,0x0000000000020000L});
    public static final BitSet FOLLOW_81_in_ruleFunction6866 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUnitName_in_entryRuleUnitName6903 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleUnitName6913 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleUnitName6964 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleVariableRef_in_entryRuleVariableRef7005 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleVariableRef7015 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleVariableRef7069 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTerminalExpression_in_entryRuleTerminalExpression7107 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTerminalExpression7117 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_INTEGER_in_ruleTerminalExpression7169 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DOUBLE_in_ruleTerminalExpression7208 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_COLOR_in_ruleTerminalExpression7247 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleTerminalExpression7286 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_BOOLEAN_in_ruleTerminalExpression7325 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAssignmentStatement_in_synpred1_InternalGaml808 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_39_in_synpred2_InternalGaml1289 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArgPairExpr_in_synpred3_InternalGaml4573 = new BitSet(new long[]{0x0000000000000002L});

}