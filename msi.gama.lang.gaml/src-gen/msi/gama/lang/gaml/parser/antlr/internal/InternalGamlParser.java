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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_ID", "RULE_STRING", "RULE_INTEGER", "RULE_DOUBLE", "RULE_COLOR", "RULE_BOOLEAN", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'model'", "'import'", "'add'", "'ask'", "'capture'", "'create'", "'error'", "'match'", "'match_between'", "'match_one'", "'put'", "'release'", "'remove'", "'save'", "'set'", "'switch'", "'warn'", "'write'", "'if'", "'condition:'", "'else'", "';'", "'<'", "','", "'>'", "'return'", "'<-'", "'<<'", "'>>'", "'+='", "'-='", "'++'", "'--'", "':='", "':'", "'function:'", "'->'", "'{'", "'}'", "'name:'", "'returns:'", "'action:'", "'?'", "'or'", "'and'", "'!='", "'='", "'>='", "'<='", "'::'", "'+'", "'-'", "'*'", "'/'", "'^'", "'#'", "'\\u00B0'", "'!'", "'my'", "'the'", "'not'", "'['", "']'", "'.'", "'('", "')'"
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
    public static final int T__46=46;
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

                if ( ((LA2_0>=RULE_ID && LA2_0<=RULE_BOOLEAN)||(LA2_0>=16 && LA2_0<=32)||LA2_0==39||LA2_0==51||LA2_0==65||(LA2_0>=70 && LA2_0<=75)||LA2_0==78) ) {
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:198:1: ruleBuiltInStatementKey returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'add' | kw= 'ask' | kw= 'capture' | kw= 'create' | kw= 'error' | kw= 'match' | kw= 'match_between' | kw= 'match_one' | kw= 'put' | kw= 'release' | kw= 'remove' | kw= 'save' | kw= 'set' | kw= 'switch' | kw= 'warn' | kw= 'write' ) ;
    public final AntlrDatatypeRuleToken ruleBuiltInStatementKey() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;

         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:201:28: ( (kw= 'add' | kw= 'ask' | kw= 'capture' | kw= 'create' | kw= 'error' | kw= 'match' | kw= 'match_between' | kw= 'match_one' | kw= 'put' | kw= 'release' | kw= 'remove' | kw= 'save' | kw= 'set' | kw= 'switch' | kw= 'warn' | kw= 'write' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:202:1: (kw= 'add' | kw= 'ask' | kw= 'capture' | kw= 'create' | kw= 'error' | kw= 'match' | kw= 'match_between' | kw= 'match_one' | kw= 'put' | kw= 'release' | kw= 'remove' | kw= 'save' | kw= 'set' | kw= 'switch' | kw= 'warn' | kw= 'write' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:202:1: (kw= 'add' | kw= 'ask' | kw= 'capture' | kw= 'create' | kw= 'error' | kw= 'match' | kw= 'match_between' | kw= 'match_one' | kw= 'put' | kw= 'release' | kw= 'remove' | kw= 'save' | kw= 'set' | kw= 'switch' | kw= 'warn' | kw= 'write' )
            int alt3=16;
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:231:2: kw= 'error'
                    {
                    kw=(Token)match(input,20,FOLLOW_20_in_ruleBuiltInStatementKey455); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInStatementKeyAccess().getErrorKeyword_4()); 
                          
                    }

                    }
                    break;
                case 6 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:238:2: kw= 'match'
                    {
                    kw=(Token)match(input,21,FOLLOW_21_in_ruleBuiltInStatementKey474); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInStatementKeyAccess().getMatchKeyword_5()); 
                          
                    }

                    }
                    break;
                case 7 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:245:2: kw= 'match_between'
                    {
                    kw=(Token)match(input,22,FOLLOW_22_in_ruleBuiltInStatementKey493); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInStatementKeyAccess().getMatch_betweenKeyword_6()); 
                          
                    }

                    }
                    break;
                case 8 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:252:2: kw= 'match_one'
                    {
                    kw=(Token)match(input,23,FOLLOW_23_in_ruleBuiltInStatementKey512); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInStatementKeyAccess().getMatch_oneKeyword_7()); 
                          
                    }

                    }
                    break;
                case 9 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:259:2: kw= 'put'
                    {
                    kw=(Token)match(input,24,FOLLOW_24_in_ruleBuiltInStatementKey531); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInStatementKeyAccess().getPutKeyword_8()); 
                          
                    }

                    }
                    break;
                case 10 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:266:2: kw= 'release'
                    {
                    kw=(Token)match(input,25,FOLLOW_25_in_ruleBuiltInStatementKey550); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInStatementKeyAccess().getReleaseKeyword_9()); 
                          
                    }

                    }
                    break;
                case 11 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:273:2: kw= 'remove'
                    {
                    kw=(Token)match(input,26,FOLLOW_26_in_ruleBuiltInStatementKey569); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInStatementKeyAccess().getRemoveKeyword_10()); 
                          
                    }

                    }
                    break;
                case 12 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:280:2: kw= 'save'
                    {
                    kw=(Token)match(input,27,FOLLOW_27_in_ruleBuiltInStatementKey588); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInStatementKeyAccess().getSaveKeyword_11()); 
                          
                    }

                    }
                    break;
                case 13 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:287:2: kw= 'set'
                    {
                    kw=(Token)match(input,28,FOLLOW_28_in_ruleBuiltInStatementKey607); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInStatementKeyAccess().getSetKeyword_12()); 
                          
                    }

                    }
                    break;
                case 14 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:294:2: kw= 'switch'
                    {
                    kw=(Token)match(input,29,FOLLOW_29_in_ruleBuiltInStatementKey626); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInStatementKeyAccess().getSwitchKeyword_13()); 
                          
                    }

                    }
                    break;
                case 15 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:301:2: kw= 'warn'
                    {
                    kw=(Token)match(input,30,FOLLOW_30_in_ruleBuiltInStatementKey645); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInStatementKeyAccess().getWarnKeyword_14()); 
                          
                    }

                    }
                    break;
                case 16 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:308:2: kw= 'write'
                    {
                    kw=(Token)match(input,31,FOLLOW_31_in_ruleBuiltInStatementKey664); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInStatementKeyAccess().getWriteKeyword_15()); 
                          
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:321:1: entryRuleStatement returns [EObject current=null] : iv_ruleStatement= ruleStatement EOF ;
    public final EObject entryRuleStatement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleStatement = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:322:2: (iv_ruleStatement= ruleStatement EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:323:2: iv_ruleStatement= ruleStatement EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getStatementRule()); 
            }
            pushFollow(FOLLOW_ruleStatement_in_entryRuleStatement704);
            iv_ruleStatement=ruleStatement();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleStatement; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleStatement714); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:330:1: ruleStatement returns [EObject current=null] : ( ( ( ruleAssignmentStatement )=>this_AssignmentStatement_0= ruleAssignmentStatement ) | (this_ReturnStatement_1= ruleReturnStatement | this_IfStatement_2= ruleIfStatement | this_ClassicStatement_3= ruleClassicStatement | this_DefinitionStatement_4= ruleDefinitionStatement ) ) ;
    public final EObject ruleStatement() throws RecognitionException {
        EObject current = null;

        EObject this_AssignmentStatement_0 = null;

        EObject this_ReturnStatement_1 = null;

        EObject this_IfStatement_2 = null;

        EObject this_ClassicStatement_3 = null;

        EObject this_DefinitionStatement_4 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:333:28: ( ( ( ( ruleAssignmentStatement )=>this_AssignmentStatement_0= ruleAssignmentStatement ) | (this_ReturnStatement_1= ruleReturnStatement | this_IfStatement_2= ruleIfStatement | this_ClassicStatement_3= ruleClassicStatement | this_DefinitionStatement_4= ruleDefinitionStatement ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:334:1: ( ( ( ruleAssignmentStatement )=>this_AssignmentStatement_0= ruleAssignmentStatement ) | (this_ReturnStatement_1= ruleReturnStatement | this_IfStatement_2= ruleIfStatement | this_ClassicStatement_3= ruleClassicStatement | this_DefinitionStatement_4= ruleDefinitionStatement ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:334:1: ( ( ( ruleAssignmentStatement )=>this_AssignmentStatement_0= ruleAssignmentStatement ) | (this_ReturnStatement_1= ruleReturnStatement | this_IfStatement_2= ruleIfStatement | this_ClassicStatement_3= ruleClassicStatement | this_DefinitionStatement_4= ruleDefinitionStatement ) )
            int alt5=2;
            alt5 = dfa5.predict(input);
            switch (alt5) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:334:2: ( ( ruleAssignmentStatement )=>this_AssignmentStatement_0= ruleAssignmentStatement )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:334:2: ( ( ruleAssignmentStatement )=>this_AssignmentStatement_0= ruleAssignmentStatement )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:334:3: ( ruleAssignmentStatement )=>this_AssignmentStatement_0= ruleAssignmentStatement
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getStatementAccess().getAssignmentStatementParserRuleCall_0()); 
                          
                    }
                    pushFollow(FOLLOW_ruleAssignmentStatement_in_ruleStatement767);
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:344:6: (this_ReturnStatement_1= ruleReturnStatement | this_IfStatement_2= ruleIfStatement | this_ClassicStatement_3= ruleClassicStatement | this_DefinitionStatement_4= ruleDefinitionStatement )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:344:6: (this_ReturnStatement_1= ruleReturnStatement | this_IfStatement_2= ruleIfStatement | this_ClassicStatement_3= ruleClassicStatement | this_DefinitionStatement_4= ruleDefinitionStatement )
                    int alt4=4;
                    switch ( input.LA(1) ) {
                    case 39:
                        {
                        alt4=1;
                        }
                        break;
                    case 32:
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
                        {
                        alt4=3;
                        }
                        break;
                    case RULE_ID:
                        {
                        alt4=4;
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
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:345:5: this_ReturnStatement_1= ruleReturnStatement
                            {
                            if ( state.backtracking==0 ) {
                               
                                      newCompositeNode(grammarAccess.getStatementAccess().getReturnStatementParserRuleCall_1_0()); 
                                  
                            }
                            pushFollow(FOLLOW_ruleReturnStatement_in_ruleStatement796);
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
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:355:5: this_IfStatement_2= ruleIfStatement
                            {
                            if ( state.backtracking==0 ) {
                               
                                      newCompositeNode(grammarAccess.getStatementAccess().getIfStatementParserRuleCall_1_1()); 
                                  
                            }
                            pushFollow(FOLLOW_ruleIfStatement_in_ruleStatement823);
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
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:365:5: this_ClassicStatement_3= ruleClassicStatement
                            {
                            if ( state.backtracking==0 ) {
                               
                                      newCompositeNode(grammarAccess.getStatementAccess().getClassicStatementParserRuleCall_1_2()); 
                                  
                            }
                            pushFollow(FOLLOW_ruleClassicStatement_in_ruleStatement850);
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
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:375:5: this_DefinitionStatement_4= ruleDefinitionStatement
                            {
                            if ( state.backtracking==0 ) {
                               
                                      newCompositeNode(grammarAccess.getStatementAccess().getDefinitionStatementParserRuleCall_1_3()); 
                                  
                            }
                            pushFollow(FOLLOW_ruleDefinitionStatement_in_ruleStatement877);
                            this_DefinitionStatement_4=ruleDefinitionStatement();

                            state._fsp--;
                            if (state.failed) return current;
                            if ( state.backtracking==0 ) {
                               
                                      current = this_DefinitionStatement_4; 
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


    // $ANTLR start "entryRuleIfStatement"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:391:1: entryRuleIfStatement returns [EObject current=null] : iv_ruleIfStatement= ruleIfStatement EOF ;
    public final EObject entryRuleIfStatement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleIfStatement = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:392:2: (iv_ruleIfStatement= ruleIfStatement EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:393:2: iv_ruleIfStatement= ruleIfStatement EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getIfStatementRule()); 
            }
            pushFollow(FOLLOW_ruleIfStatement_in_entryRuleIfStatement913);
            iv_ruleIfStatement=ruleIfStatement();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleIfStatement; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleIfStatement923); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:400:1: ruleIfStatement returns [EObject current=null] : ( ( (lv_key_0_0= 'if' ) ) (otherlv_1= 'condition:' )? ( (lv_expr_2_0= ruleExpression ) ) ( (lv_block_3_0= ruleBlock ) ) ( ( ( 'else' )=>otherlv_4= 'else' ) ( ( (lv_else_5_1= ruleIfStatement | lv_else_5_2= ruleBlock ) ) ) )? ) ;
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
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:403:28: ( ( ( (lv_key_0_0= 'if' ) ) (otherlv_1= 'condition:' )? ( (lv_expr_2_0= ruleExpression ) ) ( (lv_block_3_0= ruleBlock ) ) ( ( ( 'else' )=>otherlv_4= 'else' ) ( ( (lv_else_5_1= ruleIfStatement | lv_else_5_2= ruleBlock ) ) ) )? ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:404:1: ( ( (lv_key_0_0= 'if' ) ) (otherlv_1= 'condition:' )? ( (lv_expr_2_0= ruleExpression ) ) ( (lv_block_3_0= ruleBlock ) ) ( ( ( 'else' )=>otherlv_4= 'else' ) ( ( (lv_else_5_1= ruleIfStatement | lv_else_5_2= ruleBlock ) ) ) )? )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:404:1: ( ( (lv_key_0_0= 'if' ) ) (otherlv_1= 'condition:' )? ( (lv_expr_2_0= ruleExpression ) ) ( (lv_block_3_0= ruleBlock ) ) ( ( ( 'else' )=>otherlv_4= 'else' ) ( ( (lv_else_5_1= ruleIfStatement | lv_else_5_2= ruleBlock ) ) ) )? )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:404:2: ( (lv_key_0_0= 'if' ) ) (otherlv_1= 'condition:' )? ( (lv_expr_2_0= ruleExpression ) ) ( (lv_block_3_0= ruleBlock ) ) ( ( ( 'else' )=>otherlv_4= 'else' ) ( ( (lv_else_5_1= ruleIfStatement | lv_else_5_2= ruleBlock ) ) ) )?
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:404:2: ( (lv_key_0_0= 'if' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:405:1: (lv_key_0_0= 'if' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:405:1: (lv_key_0_0= 'if' )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:406:3: lv_key_0_0= 'if'
            {
            lv_key_0_0=(Token)match(input,32,FOLLOW_32_in_ruleIfStatement966); if (state.failed) return current;
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

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:419:2: (otherlv_1= 'condition:' )?
            int alt6=2;
            int LA6_0 = input.LA(1);

            if ( (LA6_0==33) ) {
                alt6=1;
            }
            switch (alt6) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:419:4: otherlv_1= 'condition:'
                    {
                    otherlv_1=(Token)match(input,33,FOLLOW_33_in_ruleIfStatement992); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_1, grammarAccess.getIfStatementAccess().getConditionKeyword_1());
                          
                    }

                    }
                    break;

            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:423:3: ( (lv_expr_2_0= ruleExpression ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:424:1: (lv_expr_2_0= ruleExpression )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:424:1: (lv_expr_2_0= ruleExpression )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:425:3: lv_expr_2_0= ruleExpression
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getIfStatementAccess().getExprExpressionParserRuleCall_2_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleExpression_in_ruleIfStatement1015);
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

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:441:2: ( (lv_block_3_0= ruleBlock ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:442:1: (lv_block_3_0= ruleBlock )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:442:1: (lv_block_3_0= ruleBlock )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:443:3: lv_block_3_0= ruleBlock
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getIfStatementAccess().getBlockBlockParserRuleCall_3_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleBlock_in_ruleIfStatement1036);
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

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:459:2: ( ( ( 'else' )=>otherlv_4= 'else' ) ( ( (lv_else_5_1= ruleIfStatement | lv_else_5_2= ruleBlock ) ) ) )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==34) && (synpred2_InternalGaml())) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:459:3: ( ( 'else' )=>otherlv_4= 'else' ) ( ( (lv_else_5_1= ruleIfStatement | lv_else_5_2= ruleBlock ) ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:459:3: ( ( 'else' )=>otherlv_4= 'else' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:459:4: ( 'else' )=>otherlv_4= 'else'
                    {
                    otherlv_4=(Token)match(input,34,FOLLOW_34_in_ruleIfStatement1057); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_4, grammarAccess.getIfStatementAccess().getElseKeyword_4_0());
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:464:2: ( ( (lv_else_5_1= ruleIfStatement | lv_else_5_2= ruleBlock ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:465:1: ( (lv_else_5_1= ruleIfStatement | lv_else_5_2= ruleBlock ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:465:1: ( (lv_else_5_1= ruleIfStatement | lv_else_5_2= ruleBlock ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:466:1: (lv_else_5_1= ruleIfStatement | lv_else_5_2= ruleBlock )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:466:1: (lv_else_5_1= ruleIfStatement | lv_else_5_2= ruleBlock )
                    int alt7=2;
                    int LA7_0 = input.LA(1);

                    if ( (LA7_0==32) ) {
                        alt7=1;
                    }
                    else if ( (LA7_0==51) ) {
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
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:467:3: lv_else_5_1= ruleIfStatement
                            {
                            if ( state.backtracking==0 ) {
                               
                              	        newCompositeNode(grammarAccess.getIfStatementAccess().getElseIfStatementParserRuleCall_4_1_0_0()); 
                              	    
                            }
                            pushFollow(FOLLOW_ruleIfStatement_in_ruleIfStatement1081);
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
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:482:8: lv_else_5_2= ruleBlock
                            {
                            if ( state.backtracking==0 ) {
                               
                              	        newCompositeNode(grammarAccess.getIfStatementAccess().getElseBlockParserRuleCall_4_1_0_1()); 
                              	    
                            }
                            pushFollow(FOLLOW_ruleBlock_in_ruleIfStatement1100);
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:508:1: entryRuleClassicStatement returns [EObject current=null] : iv_ruleClassicStatement= ruleClassicStatement EOF ;
    public final EObject entryRuleClassicStatement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleClassicStatement = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:509:2: (iv_ruleClassicStatement= ruleClassicStatement EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:510:2: iv_ruleClassicStatement= ruleClassicStatement EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getClassicStatementRule()); 
            }
            pushFollow(FOLLOW_ruleClassicStatement_in_entryRuleClassicStatement1141);
            iv_ruleClassicStatement=ruleClassicStatement();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleClassicStatement; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleClassicStatement1151); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:517:1: ruleClassicStatement returns [EObject current=null] : ( ( (lv_key_0_0= ruleBuiltInStatementKey ) ) ( (lv_expr_1_0= ruleExpression ) ) ( (lv_facets_2_0= ruleFacet ) )* ( ( (lv_block_3_0= ruleBlock ) ) | otherlv_4= ';' ) ) ;
    public final EObject ruleClassicStatement() throws RecognitionException {
        EObject current = null;

        Token otherlv_4=null;
        AntlrDatatypeRuleToken lv_key_0_0 = null;

        EObject lv_expr_1_0 = null;

        EObject lv_facets_2_0 = null;

        EObject lv_block_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:520:28: ( ( ( (lv_key_0_0= ruleBuiltInStatementKey ) ) ( (lv_expr_1_0= ruleExpression ) ) ( (lv_facets_2_0= ruleFacet ) )* ( ( (lv_block_3_0= ruleBlock ) ) | otherlv_4= ';' ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:521:1: ( ( (lv_key_0_0= ruleBuiltInStatementKey ) ) ( (lv_expr_1_0= ruleExpression ) ) ( (lv_facets_2_0= ruleFacet ) )* ( ( (lv_block_3_0= ruleBlock ) ) | otherlv_4= ';' ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:521:1: ( ( (lv_key_0_0= ruleBuiltInStatementKey ) ) ( (lv_expr_1_0= ruleExpression ) ) ( (lv_facets_2_0= ruleFacet ) )* ( ( (lv_block_3_0= ruleBlock ) ) | otherlv_4= ';' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:521:2: ( (lv_key_0_0= ruleBuiltInStatementKey ) ) ( (lv_expr_1_0= ruleExpression ) ) ( (lv_facets_2_0= ruleFacet ) )* ( ( (lv_block_3_0= ruleBlock ) ) | otherlv_4= ';' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:521:2: ( (lv_key_0_0= ruleBuiltInStatementKey ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:522:1: (lv_key_0_0= ruleBuiltInStatementKey )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:522:1: (lv_key_0_0= ruleBuiltInStatementKey )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:523:3: lv_key_0_0= ruleBuiltInStatementKey
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getClassicStatementAccess().getKeyBuiltInStatementKeyParserRuleCall_0_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleBuiltInStatementKey_in_ruleClassicStatement1197);
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

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:539:2: ( (lv_expr_1_0= ruleExpression ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:540:1: (lv_expr_1_0= ruleExpression )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:540:1: (lv_expr_1_0= ruleExpression )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:541:3: lv_expr_1_0= ruleExpression
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getClassicStatementAccess().getExprExpressionParserRuleCall_1_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleExpression_in_ruleClassicStatement1218);
            lv_expr_1_0=ruleExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getClassicStatementRule());
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

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:557:2: ( (lv_facets_2_0= ruleFacet ) )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==RULE_ID||LA9_0==40||(LA9_0>=49 && LA9_0<=50)||(LA9_0>=53 && LA9_0<=55)) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:558:1: (lv_facets_2_0= ruleFacet )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:558:1: (lv_facets_2_0= ruleFacet )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:559:3: lv_facets_2_0= ruleFacet
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getClassicStatementAccess().getFacetsFacetParserRuleCall_2_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleFacet_in_ruleClassicStatement1239);
            	    lv_facets_2_0=ruleFacet();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getClassicStatementRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"facets",
            	              		lv_facets_2_0, 
            	              		"Facet");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:575:3: ( ( (lv_block_3_0= ruleBlock ) ) | otherlv_4= ';' )
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==51) ) {
                alt10=1;
            }
            else if ( (LA10_0==35) ) {
                alt10=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }
            switch (alt10) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:575:4: ( (lv_block_3_0= ruleBlock ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:575:4: ( (lv_block_3_0= ruleBlock ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:576:1: (lv_block_3_0= ruleBlock )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:576:1: (lv_block_3_0= ruleBlock )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:577:3: lv_block_3_0= ruleBlock
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getClassicStatementAccess().getBlockBlockParserRuleCall_3_0_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleBlock_in_ruleClassicStatement1262);
                    lv_block_3_0=ruleBlock();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getClassicStatementRule());
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


                    }
                    break;
                case 2 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:594:7: otherlv_4= ';'
                    {
                    otherlv_4=(Token)match(input,35,FOLLOW_35_in_ruleClassicStatement1280); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_4, grammarAccess.getClassicStatementAccess().getSemicolonKeyword_3_1());
                          
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:606:1: entryRuleDefinitionStatement returns [EObject current=null] : iv_ruleDefinitionStatement= ruleDefinitionStatement EOF ;
    public final EObject entryRuleDefinitionStatement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDefinitionStatement = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:607:2: (iv_ruleDefinitionStatement= ruleDefinitionStatement EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:608:2: iv_ruleDefinitionStatement= ruleDefinitionStatement EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getDefinitionStatementRule()); 
            }
            pushFollow(FOLLOW_ruleDefinitionStatement_in_entryRuleDefinitionStatement1317);
            iv_ruleDefinitionStatement=ruleDefinitionStatement();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleDefinitionStatement; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleDefinitionStatement1327); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:615:1: ruleDefinitionStatement returns [EObject current=null] : ( ( (lv_key_0_0= RULE_ID ) ) ( (lv_of_1_0= ruleContents ) )? ( ( (lv_name_2_1= RULE_ID | lv_name_2_2= RULE_STRING | lv_name_2_3= ruleBuiltInStatementKey ) ) )? ( (lv_facets_3_0= ruleFacet ) )* ( ( (lv_block_4_0= ruleBlock ) ) | otherlv_5= ';' ) ) ;
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
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:618:28: ( ( ( (lv_key_0_0= RULE_ID ) ) ( (lv_of_1_0= ruleContents ) )? ( ( (lv_name_2_1= RULE_ID | lv_name_2_2= RULE_STRING | lv_name_2_3= ruleBuiltInStatementKey ) ) )? ( (lv_facets_3_0= ruleFacet ) )* ( ( (lv_block_4_0= ruleBlock ) ) | otherlv_5= ';' ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:619:1: ( ( (lv_key_0_0= RULE_ID ) ) ( (lv_of_1_0= ruleContents ) )? ( ( (lv_name_2_1= RULE_ID | lv_name_2_2= RULE_STRING | lv_name_2_3= ruleBuiltInStatementKey ) ) )? ( (lv_facets_3_0= ruleFacet ) )* ( ( (lv_block_4_0= ruleBlock ) ) | otherlv_5= ';' ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:619:1: ( ( (lv_key_0_0= RULE_ID ) ) ( (lv_of_1_0= ruleContents ) )? ( ( (lv_name_2_1= RULE_ID | lv_name_2_2= RULE_STRING | lv_name_2_3= ruleBuiltInStatementKey ) ) )? ( (lv_facets_3_0= ruleFacet ) )* ( ( (lv_block_4_0= ruleBlock ) ) | otherlv_5= ';' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:619:2: ( (lv_key_0_0= RULE_ID ) ) ( (lv_of_1_0= ruleContents ) )? ( ( (lv_name_2_1= RULE_ID | lv_name_2_2= RULE_STRING | lv_name_2_3= ruleBuiltInStatementKey ) ) )? ( (lv_facets_3_0= ruleFacet ) )* ( ( (lv_block_4_0= ruleBlock ) ) | otherlv_5= ';' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:619:2: ( (lv_key_0_0= RULE_ID ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:620:1: (lv_key_0_0= RULE_ID )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:620:1: (lv_key_0_0= RULE_ID )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:621:3: lv_key_0_0= RULE_ID
            {
            lv_key_0_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleDefinitionStatement1369); if (state.failed) return current;
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

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:637:2: ( (lv_of_1_0= ruleContents ) )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==36) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:638:1: (lv_of_1_0= ruleContents )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:638:1: (lv_of_1_0= ruleContents )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:639:3: lv_of_1_0= ruleContents
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getDefinitionStatementAccess().getOfContentsParserRuleCall_1_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleContents_in_ruleDefinitionStatement1395);
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

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:655:3: ( ( (lv_name_2_1= RULE_ID | lv_name_2_2= RULE_STRING | lv_name_2_3= ruleBuiltInStatementKey ) ) )?
            int alt13=2;
            int LA13_0 = input.LA(1);

            if ( (LA13_0==RULE_ID) ) {
                int LA13_1 = input.LA(2);

                if ( (LA13_1==RULE_ID||LA13_1==35||LA13_1==40||(LA13_1>=49 && LA13_1<=51)||(LA13_1>=53 && LA13_1<=55)) ) {
                    alt13=1;
                }
            }
            else if ( (LA13_0==RULE_STRING||(LA13_0>=16 && LA13_0<=31)) ) {
                alt13=1;
            }
            switch (alt13) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:656:1: ( (lv_name_2_1= RULE_ID | lv_name_2_2= RULE_STRING | lv_name_2_3= ruleBuiltInStatementKey ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:656:1: ( (lv_name_2_1= RULE_ID | lv_name_2_2= RULE_STRING | lv_name_2_3= ruleBuiltInStatementKey ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:657:1: (lv_name_2_1= RULE_ID | lv_name_2_2= RULE_STRING | lv_name_2_3= ruleBuiltInStatementKey )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:657:1: (lv_name_2_1= RULE_ID | lv_name_2_2= RULE_STRING | lv_name_2_3= ruleBuiltInStatementKey )
                    int alt12=3;
                    switch ( input.LA(1) ) {
                    case RULE_ID:
                        {
                        alt12=1;
                        }
                        break;
                    case RULE_STRING:
                        {
                        alt12=2;
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
                        {
                        alt12=3;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return current;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 12, 0, input);

                        throw nvae;
                    }

                    switch (alt12) {
                        case 1 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:658:3: lv_name_2_1= RULE_ID
                            {
                            lv_name_2_1=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleDefinitionStatement1415); if (state.failed) return current;
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
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:673:8: lv_name_2_2= RULE_STRING
                            {
                            lv_name_2_2=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleDefinitionStatement1435); if (state.failed) return current;
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
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:688:8: lv_name_2_3= ruleBuiltInStatementKey
                            {
                            if ( state.backtracking==0 ) {
                               
                              	        newCompositeNode(grammarAccess.getDefinitionStatementAccess().getNameBuiltInStatementKeyParserRuleCall_2_0_2()); 
                              	    
                            }
                            pushFollow(FOLLOW_ruleBuiltInStatementKey_in_ruleDefinitionStatement1459);
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

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:706:3: ( (lv_facets_3_0= ruleFacet ) )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0==RULE_ID||LA14_0==40||(LA14_0>=49 && LA14_0<=50)||(LA14_0>=53 && LA14_0<=55)) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:707:1: (lv_facets_3_0= ruleFacet )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:707:1: (lv_facets_3_0= ruleFacet )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:708:3: lv_facets_3_0= ruleFacet
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getDefinitionStatementAccess().getFacetsFacetParserRuleCall_3_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleFacet_in_ruleDefinitionStatement1484);
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
            	    break loop14;
                }
            } while (true);

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:724:3: ( ( (lv_block_4_0= ruleBlock ) ) | otherlv_5= ';' )
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==51) ) {
                alt15=1;
            }
            else if ( (LA15_0==35) ) {
                alt15=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;
            }
            switch (alt15) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:724:4: ( (lv_block_4_0= ruleBlock ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:724:4: ( (lv_block_4_0= ruleBlock ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:725:1: (lv_block_4_0= ruleBlock )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:725:1: (lv_block_4_0= ruleBlock )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:726:3: lv_block_4_0= ruleBlock
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getDefinitionStatementAccess().getBlockBlockParserRuleCall_4_0_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleBlock_in_ruleDefinitionStatement1507);
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:743:7: otherlv_5= ';'
                    {
                    otherlv_5=(Token)match(input,35,FOLLOW_35_in_ruleDefinitionStatement1525); if (state.failed) return current;
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:755:1: entryRuleContents returns [EObject current=null] : iv_ruleContents= ruleContents EOF ;
    public final EObject entryRuleContents() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleContents = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:756:2: (iv_ruleContents= ruleContents EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:757:2: iv_ruleContents= ruleContents EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getContentsRule()); 
            }
            pushFollow(FOLLOW_ruleContents_in_entryRuleContents1562);
            iv_ruleContents=ruleContents();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleContents; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleContents1572); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:764:1: ruleContents returns [EObject current=null] : (otherlv_0= '<' ( (lv_type_1_0= RULE_ID ) ) (otherlv_2= ',' ( (lv_type2_3_0= RULE_ID ) ) )? otherlv_4= '>' ) ;
    public final EObject ruleContents() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_type_1_0=null;
        Token otherlv_2=null;
        Token lv_type2_3_0=null;
        Token otherlv_4=null;

         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:767:28: ( (otherlv_0= '<' ( (lv_type_1_0= RULE_ID ) ) (otherlv_2= ',' ( (lv_type2_3_0= RULE_ID ) ) )? otherlv_4= '>' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:768:1: (otherlv_0= '<' ( (lv_type_1_0= RULE_ID ) ) (otherlv_2= ',' ( (lv_type2_3_0= RULE_ID ) ) )? otherlv_4= '>' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:768:1: (otherlv_0= '<' ( (lv_type_1_0= RULE_ID ) ) (otherlv_2= ',' ( (lv_type2_3_0= RULE_ID ) ) )? otherlv_4= '>' )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:768:3: otherlv_0= '<' ( (lv_type_1_0= RULE_ID ) ) (otherlv_2= ',' ( (lv_type2_3_0= RULE_ID ) ) )? otherlv_4= '>'
            {
            otherlv_0=(Token)match(input,36,FOLLOW_36_in_ruleContents1609); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_0, grammarAccess.getContentsAccess().getLessThanSignKeyword_0());
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:772:1: ( (lv_type_1_0= RULE_ID ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:773:1: (lv_type_1_0= RULE_ID )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:773:1: (lv_type_1_0= RULE_ID )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:774:3: lv_type_1_0= RULE_ID
            {
            lv_type_1_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleContents1626); if (state.failed) return current;
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

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:790:2: (otherlv_2= ',' ( (lv_type2_3_0= RULE_ID ) ) )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==37) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:790:4: otherlv_2= ',' ( (lv_type2_3_0= RULE_ID ) )
                    {
                    otherlv_2=(Token)match(input,37,FOLLOW_37_in_ruleContents1644); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_2, grammarAccess.getContentsAccess().getCommaKeyword_2_0());
                          
                    }
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:794:1: ( (lv_type2_3_0= RULE_ID ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:795:1: (lv_type2_3_0= RULE_ID )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:795:1: (lv_type2_3_0= RULE_ID )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:796:3: lv_type2_3_0= RULE_ID
                    {
                    lv_type2_3_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleContents1661); if (state.failed) return current;
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

            otherlv_4=(Token)match(input,38,FOLLOW_38_in_ruleContents1680); if (state.failed) return current;
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:824:1: entryRuleReturnStatement returns [EObject current=null] : iv_ruleReturnStatement= ruleReturnStatement EOF ;
    public final EObject entryRuleReturnStatement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleReturnStatement = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:825:2: (iv_ruleReturnStatement= ruleReturnStatement EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:826:2: iv_ruleReturnStatement= ruleReturnStatement EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getReturnStatementRule()); 
            }
            pushFollow(FOLLOW_ruleReturnStatement_in_entryRuleReturnStatement1716);
            iv_ruleReturnStatement=ruleReturnStatement();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleReturnStatement; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleReturnStatement1726); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:833:1: ruleReturnStatement returns [EObject current=null] : ( ( (lv_key_0_0= 'return' ) ) ( (lv_expr_1_0= ruleExpression ) )? otherlv_2= ';' ) ;
    public final EObject ruleReturnStatement() throws RecognitionException {
        EObject current = null;

        Token lv_key_0_0=null;
        Token otherlv_2=null;
        EObject lv_expr_1_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:836:28: ( ( ( (lv_key_0_0= 'return' ) ) ( (lv_expr_1_0= ruleExpression ) )? otherlv_2= ';' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:837:1: ( ( (lv_key_0_0= 'return' ) ) ( (lv_expr_1_0= ruleExpression ) )? otherlv_2= ';' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:837:1: ( ( (lv_key_0_0= 'return' ) ) ( (lv_expr_1_0= ruleExpression ) )? otherlv_2= ';' )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:837:2: ( (lv_key_0_0= 'return' ) ) ( (lv_expr_1_0= ruleExpression ) )? otherlv_2= ';'
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:837:2: ( (lv_key_0_0= 'return' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:838:1: (lv_key_0_0= 'return' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:838:1: (lv_key_0_0= 'return' )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:839:3: lv_key_0_0= 'return'
            {
            lv_key_0_0=(Token)match(input,39,FOLLOW_39_in_ruleReturnStatement1769); if (state.failed) return current;
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

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:852:2: ( (lv_expr_1_0= ruleExpression ) )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( ((LA17_0>=RULE_ID && LA17_0<=RULE_BOOLEAN)||LA17_0==51||LA17_0==65||(LA17_0>=70 && LA17_0<=75)||LA17_0==78) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:853:1: (lv_expr_1_0= ruleExpression )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:853:1: (lv_expr_1_0= ruleExpression )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:854:3: lv_expr_1_0= ruleExpression
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getReturnStatementAccess().getExprExpressionParserRuleCall_1_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleExpression_in_ruleReturnStatement1803);
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

            otherlv_2=(Token)match(input,35,FOLLOW_35_in_ruleReturnStatement1816); if (state.failed) return current;
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:882:1: entryRuleAssignmentStatement returns [EObject current=null] : iv_ruleAssignmentStatement= ruleAssignmentStatement EOF ;
    public final EObject entryRuleAssignmentStatement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAssignmentStatement = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:883:2: (iv_ruleAssignmentStatement= ruleAssignmentStatement EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:884:2: iv_ruleAssignmentStatement= ruleAssignmentStatement EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAssignmentStatementRule()); 
            }
            pushFollow(FOLLOW_ruleAssignmentStatement_in_entryRuleAssignmentStatement1852);
            iv_ruleAssignmentStatement=ruleAssignmentStatement();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAssignmentStatement; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleAssignmentStatement1862); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:891:1: ruleAssignmentStatement returns [EObject current=null] : ( ( (lv_expr_0_0= ruleExpression ) ) ( ( (lv_key_1_1= '<-' | lv_key_1_2= '<<' | lv_key_1_3= '>>' | lv_key_1_4= '+=' | lv_key_1_5= '-=' | lv_key_1_6= '++' | lv_key_1_7= '--' | lv_key_1_8= ':=' ) ) ) ( (lv_value_2_0= ruleExpression ) ) ( (lv_facets_3_0= ruleFacet ) )* otherlv_4= ';' ) ;
    public final EObject ruleAssignmentStatement() throws RecognitionException {
        EObject current = null;

        Token lv_key_1_1=null;
        Token lv_key_1_2=null;
        Token lv_key_1_3=null;
        Token lv_key_1_4=null;
        Token lv_key_1_5=null;
        Token lv_key_1_6=null;
        Token lv_key_1_7=null;
        Token lv_key_1_8=null;
        Token otherlv_4=null;
        EObject lv_expr_0_0 = null;

        EObject lv_value_2_0 = null;

        EObject lv_facets_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:894:28: ( ( ( (lv_expr_0_0= ruleExpression ) ) ( ( (lv_key_1_1= '<-' | lv_key_1_2= '<<' | lv_key_1_3= '>>' | lv_key_1_4= '+=' | lv_key_1_5= '-=' | lv_key_1_6= '++' | lv_key_1_7= '--' | lv_key_1_8= ':=' ) ) ) ( (lv_value_2_0= ruleExpression ) ) ( (lv_facets_3_0= ruleFacet ) )* otherlv_4= ';' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:895:1: ( ( (lv_expr_0_0= ruleExpression ) ) ( ( (lv_key_1_1= '<-' | lv_key_1_2= '<<' | lv_key_1_3= '>>' | lv_key_1_4= '+=' | lv_key_1_5= '-=' | lv_key_1_6= '++' | lv_key_1_7= '--' | lv_key_1_8= ':=' ) ) ) ( (lv_value_2_0= ruleExpression ) ) ( (lv_facets_3_0= ruleFacet ) )* otherlv_4= ';' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:895:1: ( ( (lv_expr_0_0= ruleExpression ) ) ( ( (lv_key_1_1= '<-' | lv_key_1_2= '<<' | lv_key_1_3= '>>' | lv_key_1_4= '+=' | lv_key_1_5= '-=' | lv_key_1_6= '++' | lv_key_1_7= '--' | lv_key_1_8= ':=' ) ) ) ( (lv_value_2_0= ruleExpression ) ) ( (lv_facets_3_0= ruleFacet ) )* otherlv_4= ';' )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:895:2: ( (lv_expr_0_0= ruleExpression ) ) ( ( (lv_key_1_1= '<-' | lv_key_1_2= '<<' | lv_key_1_3= '>>' | lv_key_1_4= '+=' | lv_key_1_5= '-=' | lv_key_1_6= '++' | lv_key_1_7= '--' | lv_key_1_8= ':=' ) ) ) ( (lv_value_2_0= ruleExpression ) ) ( (lv_facets_3_0= ruleFacet ) )* otherlv_4= ';'
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:895:2: ( (lv_expr_0_0= ruleExpression ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:896:1: (lv_expr_0_0= ruleExpression )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:896:1: (lv_expr_0_0= ruleExpression )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:897:3: lv_expr_0_0= ruleExpression
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getAssignmentStatementAccess().getExprExpressionParserRuleCall_0_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleExpression_in_ruleAssignmentStatement1908);
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

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:913:2: ( ( (lv_key_1_1= '<-' | lv_key_1_2= '<<' | lv_key_1_3= '>>' | lv_key_1_4= '+=' | lv_key_1_5= '-=' | lv_key_1_6= '++' | lv_key_1_7= '--' | lv_key_1_8= ':=' ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:914:1: ( (lv_key_1_1= '<-' | lv_key_1_2= '<<' | lv_key_1_3= '>>' | lv_key_1_4= '+=' | lv_key_1_5= '-=' | lv_key_1_6= '++' | lv_key_1_7= '--' | lv_key_1_8= ':=' ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:914:1: ( (lv_key_1_1= '<-' | lv_key_1_2= '<<' | lv_key_1_3= '>>' | lv_key_1_4= '+=' | lv_key_1_5= '-=' | lv_key_1_6= '++' | lv_key_1_7= '--' | lv_key_1_8= ':=' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:915:1: (lv_key_1_1= '<-' | lv_key_1_2= '<<' | lv_key_1_3= '>>' | lv_key_1_4= '+=' | lv_key_1_5= '-=' | lv_key_1_6= '++' | lv_key_1_7= '--' | lv_key_1_8= ':=' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:915:1: (lv_key_1_1= '<-' | lv_key_1_2= '<<' | lv_key_1_3= '>>' | lv_key_1_4= '+=' | lv_key_1_5= '-=' | lv_key_1_6= '++' | lv_key_1_7= '--' | lv_key_1_8= ':=' )
            int alt18=8;
            switch ( input.LA(1) ) {
            case 40:
                {
                alt18=1;
                }
                break;
            case 41:
                {
                alt18=2;
                }
                break;
            case 42:
                {
                alt18=3;
                }
                break;
            case 43:
                {
                alt18=4;
                }
                break;
            case 44:
                {
                alt18=5;
                }
                break;
            case 45:
                {
                alt18=6;
                }
                break;
            case 46:
                {
                alt18=7;
                }
                break;
            case 47:
                {
                alt18=8;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:916:3: lv_key_1_1= '<-'
                    {
                    lv_key_1_1=(Token)match(input,40,FOLLOW_40_in_ruleAssignmentStatement1928); if (state.failed) return current;
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:928:8: lv_key_1_2= '<<'
                    {
                    lv_key_1_2=(Token)match(input,41,FOLLOW_41_in_ruleAssignmentStatement1957); if (state.failed) return current;
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:940:8: lv_key_1_3= '>>'
                    {
                    lv_key_1_3=(Token)match(input,42,FOLLOW_42_in_ruleAssignmentStatement1986); if (state.failed) return current;
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:952:8: lv_key_1_4= '+='
                    {
                    lv_key_1_4=(Token)match(input,43,FOLLOW_43_in_ruleAssignmentStatement2015); if (state.failed) return current;
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:964:8: lv_key_1_5= '-='
                    {
                    lv_key_1_5=(Token)match(input,44,FOLLOW_44_in_ruleAssignmentStatement2044); if (state.failed) return current;
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:976:8: lv_key_1_6= '++'
                    {
                    lv_key_1_6=(Token)match(input,45,FOLLOW_45_in_ruleAssignmentStatement2073); if (state.failed) return current;
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:988:8: lv_key_1_7= '--'
                    {
                    lv_key_1_7=(Token)match(input,46,FOLLOW_46_in_ruleAssignmentStatement2102); if (state.failed) return current;
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
                case 8 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1000:8: lv_key_1_8= ':='
                    {
                    lv_key_1_8=(Token)match(input,47,FOLLOW_47_in_ruleAssignmentStatement2131); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_key_1_8, grammarAccess.getAssignmentStatementAccess().getKeyColonEqualsSignKeyword_1_0_7());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getAssignmentStatementRule());
                      	        }
                             		setWithLastConsumed(current, "key", lv_key_1_8, null);
                      	    
                    }

                    }
                    break;

            }


            }


            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1015:2: ( (lv_value_2_0= ruleExpression ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1016:1: (lv_value_2_0= ruleExpression )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1016:1: (lv_value_2_0= ruleExpression )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1017:3: lv_value_2_0= ruleExpression
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getAssignmentStatementAccess().getValueExpressionParserRuleCall_2_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleExpression_in_ruleAssignmentStatement2168);
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

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1033:2: ( (lv_facets_3_0= ruleFacet ) )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==RULE_ID||LA19_0==40||(LA19_0>=49 && LA19_0<=50)||(LA19_0>=53 && LA19_0<=55)) ) {
                    alt19=1;
                }


                switch (alt19) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1034:1: (lv_facets_3_0= ruleFacet )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1034:1: (lv_facets_3_0= ruleFacet )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1035:3: lv_facets_3_0= ruleFacet
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getAssignmentStatementAccess().getFacetsFacetParserRuleCall_3_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleFacet_in_ruleAssignmentStatement2189);
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
            	    break loop19;
                }
            } while (true);

            otherlv_4=(Token)match(input,35,FOLLOW_35_in_ruleAssignmentStatement2202); if (state.failed) return current;
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1063:1: entryRuleFacet returns [EObject current=null] : iv_ruleFacet= ruleFacet EOF ;
    public final EObject entryRuleFacet() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleFacet = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1064:2: (iv_ruleFacet= ruleFacet EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1065:2: iv_ruleFacet= ruleFacet EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getFacetRule()); 
            }
            pushFollow(FOLLOW_ruleFacet_in_entryRuleFacet2238);
            iv_ruleFacet=ruleFacet();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleFacet; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleFacet2248); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1072:1: ruleFacet returns [EObject current=null] : (this_FunctionFacet_0= ruleFunctionFacet | this_DefinitionFacet_1= ruleDefinitionFacet | this_ClassicFacet_2= ruleClassicFacet ) ;
    public final EObject ruleFacet() throws RecognitionException {
        EObject current = null;

        EObject this_FunctionFacet_0 = null;

        EObject this_DefinitionFacet_1 = null;

        EObject this_ClassicFacet_2 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1075:28: ( (this_FunctionFacet_0= ruleFunctionFacet | this_DefinitionFacet_1= ruleDefinitionFacet | this_ClassicFacet_2= ruleClassicFacet ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1076:1: (this_FunctionFacet_0= ruleFunctionFacet | this_DefinitionFacet_1= ruleDefinitionFacet | this_ClassicFacet_2= ruleClassicFacet )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1076:1: (this_FunctionFacet_0= ruleFunctionFacet | this_DefinitionFacet_1= ruleDefinitionFacet | this_ClassicFacet_2= ruleClassicFacet )
            int alt20=3;
            switch ( input.LA(1) ) {
            case 49:
            case 50:
                {
                alt20=1;
                }
                break;
            case 53:
            case 54:
            case 55:
                {
                alt20=2;
                }
                break;
            case RULE_ID:
            case 40:
                {
                alt20=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;
            }

            switch (alt20) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1077:5: this_FunctionFacet_0= ruleFunctionFacet
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getFacetAccess().getFunctionFacetParserRuleCall_0()); 
                          
                    }
                    pushFollow(FOLLOW_ruleFunctionFacet_in_ruleFacet2295);
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1087:5: this_DefinitionFacet_1= ruleDefinitionFacet
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getFacetAccess().getDefinitionFacetParserRuleCall_1()); 
                          
                    }
                    pushFollow(FOLLOW_ruleDefinitionFacet_in_ruleFacet2322);
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1097:5: this_ClassicFacet_2= ruleClassicFacet
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getFacetAccess().getClassicFacetParserRuleCall_2()); 
                          
                    }
                    pushFollow(FOLLOW_ruleClassicFacet_in_ruleFacet2349);
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1113:1: entryRuleClassicFacet returns [EObject current=null] : iv_ruleClassicFacet= ruleClassicFacet EOF ;
    public final EObject entryRuleClassicFacet() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleClassicFacet = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1114:2: (iv_ruleClassicFacet= ruleClassicFacet EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1115:2: iv_ruleClassicFacet= ruleClassicFacet EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getClassicFacetRule()); 
            }
            pushFollow(FOLLOW_ruleClassicFacet_in_entryRuleClassicFacet2384);
            iv_ruleClassicFacet=ruleClassicFacet();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleClassicFacet; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleClassicFacet2394); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1122:1: ruleClassicFacet returns [EObject current=null] : ( ( ( ( (lv_key_0_0= RULE_ID ) ) otherlv_1= ':' ) | ( (lv_key_2_0= '<-' ) ) ) ( (lv_expr_3_0= ruleExpression ) ) ) ;
    public final EObject ruleClassicFacet() throws RecognitionException {
        EObject current = null;

        Token lv_key_0_0=null;
        Token otherlv_1=null;
        Token lv_key_2_0=null;
        EObject lv_expr_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1125:28: ( ( ( ( ( (lv_key_0_0= RULE_ID ) ) otherlv_1= ':' ) | ( (lv_key_2_0= '<-' ) ) ) ( (lv_expr_3_0= ruleExpression ) ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1126:1: ( ( ( ( (lv_key_0_0= RULE_ID ) ) otherlv_1= ':' ) | ( (lv_key_2_0= '<-' ) ) ) ( (lv_expr_3_0= ruleExpression ) ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1126:1: ( ( ( ( (lv_key_0_0= RULE_ID ) ) otherlv_1= ':' ) | ( (lv_key_2_0= '<-' ) ) ) ( (lv_expr_3_0= ruleExpression ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1126:2: ( ( ( (lv_key_0_0= RULE_ID ) ) otherlv_1= ':' ) | ( (lv_key_2_0= '<-' ) ) ) ( (lv_expr_3_0= ruleExpression ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1126:2: ( ( ( (lv_key_0_0= RULE_ID ) ) otherlv_1= ':' ) | ( (lv_key_2_0= '<-' ) ) )
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==RULE_ID) ) {
                alt21=1;
            }
            else if ( (LA21_0==40) ) {
                alt21=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                throw nvae;
            }
            switch (alt21) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1126:3: ( ( (lv_key_0_0= RULE_ID ) ) otherlv_1= ':' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1126:3: ( ( (lv_key_0_0= RULE_ID ) ) otherlv_1= ':' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1126:4: ( (lv_key_0_0= RULE_ID ) ) otherlv_1= ':'
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1126:4: ( (lv_key_0_0= RULE_ID ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1127:1: (lv_key_0_0= RULE_ID )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1127:1: (lv_key_0_0= RULE_ID )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1128:3: lv_key_0_0= RULE_ID
                    {
                    lv_key_0_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleClassicFacet2438); if (state.failed) return current;
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

                    otherlv_1=(Token)match(input,48,FOLLOW_48_in_ruleClassicFacet2455); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_1, grammarAccess.getClassicFacetAccess().getColonKeyword_0_0_1());
                          
                    }

                    }


                    }
                    break;
                case 2 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1149:6: ( (lv_key_2_0= '<-' ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1149:6: ( (lv_key_2_0= '<-' ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1150:1: (lv_key_2_0= '<-' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1150:1: (lv_key_2_0= '<-' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1151:3: lv_key_2_0= '<-'
                    {
                    lv_key_2_0=(Token)match(input,40,FOLLOW_40_in_ruleClassicFacet2480); if (state.failed) return current;
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

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1164:3: ( (lv_expr_3_0= ruleExpression ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1165:1: (lv_expr_3_0= ruleExpression )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1165:1: (lv_expr_3_0= ruleExpression )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1166:3: lv_expr_3_0= ruleExpression
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getClassicFacetAccess().getExprExpressionParserRuleCall_1_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleExpression_in_ruleClassicFacet2515);
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1190:1: entryRuleFunctionFacet returns [EObject current=null] : iv_ruleFunctionFacet= ruleFunctionFacet EOF ;
    public final EObject entryRuleFunctionFacet() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleFunctionFacet = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1191:2: (iv_ruleFunctionFacet= ruleFunctionFacet EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1192:2: iv_ruleFunctionFacet= ruleFunctionFacet EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getFunctionFacetRule()); 
            }
            pushFollow(FOLLOW_ruleFunctionFacet_in_entryRuleFunctionFacet2551);
            iv_ruleFunctionFacet=ruleFunctionFacet();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleFunctionFacet; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleFunctionFacet2561); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1199:1: ruleFunctionFacet returns [EObject current=null] : ( ( ( (lv_key_0_0= 'function:' ) ) | ( (lv_key_1_0= '->' ) ) ) otherlv_2= '{' ( (lv_expr_3_0= ruleExpression ) ) otherlv_4= '}' ) ;
    public final EObject ruleFunctionFacet() throws RecognitionException {
        EObject current = null;

        Token lv_key_0_0=null;
        Token lv_key_1_0=null;
        Token otherlv_2=null;
        Token otherlv_4=null;
        EObject lv_expr_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1202:28: ( ( ( ( (lv_key_0_0= 'function:' ) ) | ( (lv_key_1_0= '->' ) ) ) otherlv_2= '{' ( (lv_expr_3_0= ruleExpression ) ) otherlv_4= '}' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1203:1: ( ( ( (lv_key_0_0= 'function:' ) ) | ( (lv_key_1_0= '->' ) ) ) otherlv_2= '{' ( (lv_expr_3_0= ruleExpression ) ) otherlv_4= '}' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1203:1: ( ( ( (lv_key_0_0= 'function:' ) ) | ( (lv_key_1_0= '->' ) ) ) otherlv_2= '{' ( (lv_expr_3_0= ruleExpression ) ) otherlv_4= '}' )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1203:2: ( ( (lv_key_0_0= 'function:' ) ) | ( (lv_key_1_0= '->' ) ) ) otherlv_2= '{' ( (lv_expr_3_0= ruleExpression ) ) otherlv_4= '}'
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1203:2: ( ( (lv_key_0_0= 'function:' ) ) | ( (lv_key_1_0= '->' ) ) )
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==49) ) {
                alt22=1;
            }
            else if ( (LA22_0==50) ) {
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1203:3: ( (lv_key_0_0= 'function:' ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1203:3: ( (lv_key_0_0= 'function:' ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1204:1: (lv_key_0_0= 'function:' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1204:1: (lv_key_0_0= 'function:' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1205:3: lv_key_0_0= 'function:'
                    {
                    lv_key_0_0=(Token)match(input,49,FOLLOW_49_in_ruleFunctionFacet2605); if (state.failed) return current;
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1219:6: ( (lv_key_1_0= '->' ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1219:6: ( (lv_key_1_0= '->' ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1220:1: (lv_key_1_0= '->' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1220:1: (lv_key_1_0= '->' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1221:3: lv_key_1_0= '->'
                    {
                    lv_key_1_0=(Token)match(input,50,FOLLOW_50_in_ruleFunctionFacet2642); if (state.failed) return current;
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

            otherlv_2=(Token)match(input,51,FOLLOW_51_in_ruleFunctionFacet2668); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_2, grammarAccess.getFunctionFacetAccess().getLeftCurlyBracketKeyword_1());
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1238:1: ( (lv_expr_3_0= ruleExpression ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1239:1: (lv_expr_3_0= ruleExpression )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1239:1: (lv_expr_3_0= ruleExpression )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1240:3: lv_expr_3_0= ruleExpression
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getFunctionFacetAccess().getExprExpressionParserRuleCall_2_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleExpression_in_ruleFunctionFacet2689);
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

            otherlv_4=(Token)match(input,52,FOLLOW_52_in_ruleFunctionFacet2701); if (state.failed) return current;
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


    // $ANTLR start "entryRuleDefinitionFacet"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1268:1: entryRuleDefinitionFacet returns [EObject current=null] : iv_ruleDefinitionFacet= ruleDefinitionFacet EOF ;
    public final EObject entryRuleDefinitionFacet() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDefinitionFacet = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1269:2: (iv_ruleDefinitionFacet= ruleDefinitionFacet EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1270:2: iv_ruleDefinitionFacet= ruleDefinitionFacet EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getDefinitionFacetRule()); 
            }
            pushFollow(FOLLOW_ruleDefinitionFacet_in_entryRuleDefinitionFacet2737);
            iv_ruleDefinitionFacet=ruleDefinitionFacet();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleDefinitionFacet; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleDefinitionFacet2747); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1277:1: ruleDefinitionFacet returns [EObject current=null] : ( ( ( (lv_key_0_1= 'name:' | lv_key_0_2= 'returns:' | lv_key_0_3= 'action:' ) ) ) ( ( (lv_name_1_1= RULE_ID | lv_name_1_2= RULE_STRING | lv_name_1_3= ruleBuiltInStatementKey ) ) ) ) ;
    public final EObject ruleDefinitionFacet() throws RecognitionException {
        EObject current = null;

        Token lv_key_0_1=null;
        Token lv_key_0_2=null;
        Token lv_key_0_3=null;
        Token lv_name_1_1=null;
        Token lv_name_1_2=null;
        AntlrDatatypeRuleToken lv_name_1_3 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1280:28: ( ( ( ( (lv_key_0_1= 'name:' | lv_key_0_2= 'returns:' | lv_key_0_3= 'action:' ) ) ) ( ( (lv_name_1_1= RULE_ID | lv_name_1_2= RULE_STRING | lv_name_1_3= ruleBuiltInStatementKey ) ) ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1281:1: ( ( ( (lv_key_0_1= 'name:' | lv_key_0_2= 'returns:' | lv_key_0_3= 'action:' ) ) ) ( ( (lv_name_1_1= RULE_ID | lv_name_1_2= RULE_STRING | lv_name_1_3= ruleBuiltInStatementKey ) ) ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1281:1: ( ( ( (lv_key_0_1= 'name:' | lv_key_0_2= 'returns:' | lv_key_0_3= 'action:' ) ) ) ( ( (lv_name_1_1= RULE_ID | lv_name_1_2= RULE_STRING | lv_name_1_3= ruleBuiltInStatementKey ) ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1281:2: ( ( (lv_key_0_1= 'name:' | lv_key_0_2= 'returns:' | lv_key_0_3= 'action:' ) ) ) ( ( (lv_name_1_1= RULE_ID | lv_name_1_2= RULE_STRING | lv_name_1_3= ruleBuiltInStatementKey ) ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1281:2: ( ( (lv_key_0_1= 'name:' | lv_key_0_2= 'returns:' | lv_key_0_3= 'action:' ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1282:1: ( (lv_key_0_1= 'name:' | lv_key_0_2= 'returns:' | lv_key_0_3= 'action:' ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1282:1: ( (lv_key_0_1= 'name:' | lv_key_0_2= 'returns:' | lv_key_0_3= 'action:' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1283:1: (lv_key_0_1= 'name:' | lv_key_0_2= 'returns:' | lv_key_0_3= 'action:' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1283:1: (lv_key_0_1= 'name:' | lv_key_0_2= 'returns:' | lv_key_0_3= 'action:' )
            int alt23=3;
            switch ( input.LA(1) ) {
            case 53:
                {
                alt23=1;
                }
                break;
            case 54:
                {
                alt23=2;
                }
                break;
            case 55:
                {
                alt23=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 23, 0, input);

                throw nvae;
            }

            switch (alt23) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1284:3: lv_key_0_1= 'name:'
                    {
                    lv_key_0_1=(Token)match(input,53,FOLLOW_53_in_ruleDefinitionFacet2792); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_key_0_1, grammarAccess.getDefinitionFacetAccess().getKeyNameKeyword_0_0_0());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getDefinitionFacetRule());
                      	        }
                             		setWithLastConsumed(current, "key", lv_key_0_1, null);
                      	    
                    }

                    }
                    break;
                case 2 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1296:8: lv_key_0_2= 'returns:'
                    {
                    lv_key_0_2=(Token)match(input,54,FOLLOW_54_in_ruleDefinitionFacet2821); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_key_0_2, grammarAccess.getDefinitionFacetAccess().getKeyReturnsKeyword_0_0_1());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getDefinitionFacetRule());
                      	        }
                             		setWithLastConsumed(current, "key", lv_key_0_2, null);
                      	    
                    }

                    }
                    break;
                case 3 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1308:8: lv_key_0_3= 'action:'
                    {
                    lv_key_0_3=(Token)match(input,55,FOLLOW_55_in_ruleDefinitionFacet2850); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_key_0_3, grammarAccess.getDefinitionFacetAccess().getKeyActionKeyword_0_0_2());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getDefinitionFacetRule());
                      	        }
                             		setWithLastConsumed(current, "key", lv_key_0_3, null);
                      	    
                    }

                    }
                    break;

            }


            }


            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1323:2: ( ( (lv_name_1_1= RULE_ID | lv_name_1_2= RULE_STRING | lv_name_1_3= ruleBuiltInStatementKey ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1324:1: ( (lv_name_1_1= RULE_ID | lv_name_1_2= RULE_STRING | lv_name_1_3= ruleBuiltInStatementKey ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1324:1: ( (lv_name_1_1= RULE_ID | lv_name_1_2= RULE_STRING | lv_name_1_3= ruleBuiltInStatementKey ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1325:1: (lv_name_1_1= RULE_ID | lv_name_1_2= RULE_STRING | lv_name_1_3= ruleBuiltInStatementKey )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1325:1: (lv_name_1_1= RULE_ID | lv_name_1_2= RULE_STRING | lv_name_1_3= ruleBuiltInStatementKey )
            int alt24=3;
            switch ( input.LA(1) ) {
            case RULE_ID:
                {
                alt24=1;
                }
                break;
            case RULE_STRING:
                {
                alt24=2;
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1326:3: lv_name_1_1= RULE_ID
                    {
                    lv_name_1_1=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleDefinitionFacet2885); if (state.failed) return current;
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1341:8: lv_name_1_2= RULE_STRING
                    {
                    lv_name_1_2=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleDefinitionFacet2905); if (state.failed) return current;
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1356:8: lv_name_1_3= ruleBuiltInStatementKey
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getDefinitionFacetAccess().getNameBuiltInStatementKeyParserRuleCall_1_0_2()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleBuiltInStatementKey_in_ruleDefinitionFacet2929);
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1382:1: entryRuleBlock returns [EObject current=null] : iv_ruleBlock= ruleBlock EOF ;
    public final EObject entryRuleBlock() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleBlock = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1383:2: (iv_ruleBlock= ruleBlock EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1384:2: iv_ruleBlock= ruleBlock EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getBlockRule()); 
            }
            pushFollow(FOLLOW_ruleBlock_in_entryRuleBlock2968);
            iv_ruleBlock=ruleBlock();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleBlock; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleBlock2978); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1391:1: ruleBlock returns [EObject current=null] : ( () otherlv_1= '{' ( (lv_statements_2_0= ruleStatement ) )* otherlv_3= '}' ) ;
    public final EObject ruleBlock() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        EObject lv_statements_2_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1394:28: ( ( () otherlv_1= '{' ( (lv_statements_2_0= ruleStatement ) )* otherlv_3= '}' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1395:1: ( () otherlv_1= '{' ( (lv_statements_2_0= ruleStatement ) )* otherlv_3= '}' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1395:1: ( () otherlv_1= '{' ( (lv_statements_2_0= ruleStatement ) )* otherlv_3= '}' )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1395:2: () otherlv_1= '{' ( (lv_statements_2_0= ruleStatement ) )* otherlv_3= '}'
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1395:2: ()
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1396:5: 
            {
            if ( state.backtracking==0 ) {

                      current = forceCreateModelElement(
                          grammarAccess.getBlockAccess().getBlockAction_0(),
                          current);
                  
            }

            }

            otherlv_1=(Token)match(input,51,FOLLOW_51_in_ruleBlock3024); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_1, grammarAccess.getBlockAccess().getLeftCurlyBracketKeyword_1());
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1405:1: ( (lv_statements_2_0= ruleStatement ) )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( ((LA25_0>=RULE_ID && LA25_0<=RULE_BOOLEAN)||(LA25_0>=16 && LA25_0<=32)||LA25_0==39||LA25_0==51||LA25_0==65||(LA25_0>=70 && LA25_0<=75)||LA25_0==78) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1406:1: (lv_statements_2_0= ruleStatement )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1406:1: (lv_statements_2_0= ruleStatement )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1407:3: lv_statements_2_0= ruleStatement
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getBlockAccess().getStatementsStatementParserRuleCall_2_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleStatement_in_ruleBlock3045);
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
            	    break loop25;
                }
            } while (true);

            otherlv_3=(Token)match(input,52,FOLLOW_52_in_ruleBlock3058); if (state.failed) return current;
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1435:1: entryRuleExpression returns [EObject current=null] : iv_ruleExpression= ruleExpression EOF ;
    public final EObject entryRuleExpression() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleExpression = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1436:2: (iv_ruleExpression= ruleExpression EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1437:2: iv_ruleExpression= ruleExpression EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getExpressionRule()); 
            }
            pushFollow(FOLLOW_ruleExpression_in_entryRuleExpression3094);
            iv_ruleExpression=ruleExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleExpression; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleExpression3104); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1444:1: ruleExpression returns [EObject current=null] : this_TernExp_0= ruleTernExp ;
    public final EObject ruleExpression() throws RecognitionException {
        EObject current = null;

        EObject this_TernExp_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1447:28: (this_TernExp_0= ruleTernExp )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1449:5: this_TernExp_0= ruleTernExp
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getExpressionAccess().getTernExpParserRuleCall()); 
                  
            }
            pushFollow(FOLLOW_ruleTernExp_in_ruleExpression3150);
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1465:1: entryRuleTernExp returns [EObject current=null] : iv_ruleTernExp= ruleTernExp EOF ;
    public final EObject entryRuleTernExp() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTernExp = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1466:2: (iv_ruleTernExp= ruleTernExp EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1467:2: iv_ruleTernExp= ruleTernExp EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getTernExpRule()); 
            }
            pushFollow(FOLLOW_ruleTernExp_in_entryRuleTernExp3184);
            iv_ruleTernExp=ruleTernExp();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleTernExp; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleTernExp3194); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1474:1: ruleTernExp returns [EObject current=null] : (this_OrExp_0= ruleOrExp ( () ( (lv_op_2_0= '?' ) ) ( (lv_right_3_0= ruleOrExp ) ) otherlv_4= ':' ( (lv_ifFalse_5_0= ruleOrExp ) ) )? ) ;
    public final EObject ruleTernExp() throws RecognitionException {
        EObject current = null;

        Token lv_op_2_0=null;
        Token otherlv_4=null;
        EObject this_OrExp_0 = null;

        EObject lv_right_3_0 = null;

        EObject lv_ifFalse_5_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1477:28: ( (this_OrExp_0= ruleOrExp ( () ( (lv_op_2_0= '?' ) ) ( (lv_right_3_0= ruleOrExp ) ) otherlv_4= ':' ( (lv_ifFalse_5_0= ruleOrExp ) ) )? ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1478:1: (this_OrExp_0= ruleOrExp ( () ( (lv_op_2_0= '?' ) ) ( (lv_right_3_0= ruleOrExp ) ) otherlv_4= ':' ( (lv_ifFalse_5_0= ruleOrExp ) ) )? )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1478:1: (this_OrExp_0= ruleOrExp ( () ( (lv_op_2_0= '?' ) ) ( (lv_right_3_0= ruleOrExp ) ) otherlv_4= ':' ( (lv_ifFalse_5_0= ruleOrExp ) ) )? )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1479:5: this_OrExp_0= ruleOrExp ( () ( (lv_op_2_0= '?' ) ) ( (lv_right_3_0= ruleOrExp ) ) otherlv_4= ':' ( (lv_ifFalse_5_0= ruleOrExp ) ) )?
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getTernExpAccess().getOrExpParserRuleCall_0()); 
                  
            }
            pushFollow(FOLLOW_ruleOrExp_in_ruleTernExp3241);
            this_OrExp_0=ruleOrExp();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_OrExp_0; 
                      afterParserOrEnumRuleCall();
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1487:1: ( () ( (lv_op_2_0= '?' ) ) ( (lv_right_3_0= ruleOrExp ) ) otherlv_4= ':' ( (lv_ifFalse_5_0= ruleOrExp ) ) )?
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==56) ) {
                alt26=1;
            }
            switch (alt26) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1487:2: () ( (lv_op_2_0= '?' ) ) ( (lv_right_3_0= ruleOrExp ) ) otherlv_4= ':' ( (lv_ifFalse_5_0= ruleOrExp ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1487:2: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1488:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElementAndSet(
                                  grammarAccess.getTernExpAccess().getTernExpLeftAction_1_0(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1493:2: ( (lv_op_2_0= '?' ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1494:1: (lv_op_2_0= '?' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1494:1: (lv_op_2_0= '?' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1495:3: lv_op_2_0= '?'
                    {
                    lv_op_2_0=(Token)match(input,56,FOLLOW_56_in_ruleTernExp3268); if (state.failed) return current;
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

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1508:2: ( (lv_right_3_0= ruleOrExp ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1509:1: (lv_right_3_0= ruleOrExp )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1509:1: (lv_right_3_0= ruleOrExp )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1510:3: lv_right_3_0= ruleOrExp
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getTernExpAccess().getRightOrExpParserRuleCall_1_2_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleOrExp_in_ruleTernExp3302);
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

                    otherlv_4=(Token)match(input,48,FOLLOW_48_in_ruleTernExp3314); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_4, grammarAccess.getTernExpAccess().getColonKeyword_1_3());
                          
                    }
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1530:1: ( (lv_ifFalse_5_0= ruleOrExp ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1531:1: (lv_ifFalse_5_0= ruleOrExp )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1531:1: (lv_ifFalse_5_0= ruleOrExp )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1532:3: lv_ifFalse_5_0= ruleOrExp
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getTernExpAccess().getIfFalseOrExpParserRuleCall_1_4_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleOrExp_in_ruleTernExp3335);
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1556:1: entryRuleOrExp returns [EObject current=null] : iv_ruleOrExp= ruleOrExp EOF ;
    public final EObject entryRuleOrExp() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOrExp = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1557:2: (iv_ruleOrExp= ruleOrExp EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1558:2: iv_ruleOrExp= ruleOrExp EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getOrExpRule()); 
            }
            pushFollow(FOLLOW_ruleOrExp_in_entryRuleOrExp3373);
            iv_ruleOrExp=ruleOrExp();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleOrExp; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleOrExp3383); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1565:1: ruleOrExp returns [EObject current=null] : (this_AndExp_0= ruleAndExp ( () ( (lv_op_2_0= 'or' ) ) ( (lv_right_3_0= ruleAndExp ) ) )* ) ;
    public final EObject ruleOrExp() throws RecognitionException {
        EObject current = null;

        Token lv_op_2_0=null;
        EObject this_AndExp_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1568:28: ( (this_AndExp_0= ruleAndExp ( () ( (lv_op_2_0= 'or' ) ) ( (lv_right_3_0= ruleAndExp ) ) )* ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1569:1: (this_AndExp_0= ruleAndExp ( () ( (lv_op_2_0= 'or' ) ) ( (lv_right_3_0= ruleAndExp ) ) )* )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1569:1: (this_AndExp_0= ruleAndExp ( () ( (lv_op_2_0= 'or' ) ) ( (lv_right_3_0= ruleAndExp ) ) )* )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1570:5: this_AndExp_0= ruleAndExp ( () ( (lv_op_2_0= 'or' ) ) ( (lv_right_3_0= ruleAndExp ) ) )*
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getOrExpAccess().getAndExpParserRuleCall_0()); 
                  
            }
            pushFollow(FOLLOW_ruleAndExp_in_ruleOrExp3430);
            this_AndExp_0=ruleAndExp();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_AndExp_0; 
                      afterParserOrEnumRuleCall();
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1578:1: ( () ( (lv_op_2_0= 'or' ) ) ( (lv_right_3_0= ruleAndExp ) ) )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( (LA27_0==57) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1578:2: () ( (lv_op_2_0= 'or' ) ) ( (lv_right_3_0= ruleAndExp ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1578:2: ()
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1579:5: 
            	    {
            	    if ( state.backtracking==0 ) {

            	              current = forceCreateModelElementAndSet(
            	                  grammarAccess.getOrExpAccess().getExpressionLeftAction_1_0(),
            	                  current);
            	          
            	    }

            	    }

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1584:2: ( (lv_op_2_0= 'or' ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1585:1: (lv_op_2_0= 'or' )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1585:1: (lv_op_2_0= 'or' )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1586:3: lv_op_2_0= 'or'
            	    {
            	    lv_op_2_0=(Token)match(input,57,FOLLOW_57_in_ruleOrExp3457); if (state.failed) return current;
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

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1599:2: ( (lv_right_3_0= ruleAndExp ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1600:1: (lv_right_3_0= ruleAndExp )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1600:1: (lv_right_3_0= ruleAndExp )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1601:3: lv_right_3_0= ruleAndExp
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getOrExpAccess().getRightAndExpParserRuleCall_1_2_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleAndExp_in_ruleOrExp3491);
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
            	    break loop27;
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1625:1: entryRuleAndExp returns [EObject current=null] : iv_ruleAndExp= ruleAndExp EOF ;
    public final EObject entryRuleAndExp() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAndExp = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1626:2: (iv_ruleAndExp= ruleAndExp EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1627:2: iv_ruleAndExp= ruleAndExp EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAndExpRule()); 
            }
            pushFollow(FOLLOW_ruleAndExp_in_entryRuleAndExp3529);
            iv_ruleAndExp=ruleAndExp();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAndExp; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleAndExp3539); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1634:1: ruleAndExp returns [EObject current=null] : (this_Relational_0= ruleRelational ( () ( (lv_op_2_0= 'and' ) ) ( (lv_right_3_0= ruleRelational ) ) )* ) ;
    public final EObject ruleAndExp() throws RecognitionException {
        EObject current = null;

        Token lv_op_2_0=null;
        EObject this_Relational_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1637:28: ( (this_Relational_0= ruleRelational ( () ( (lv_op_2_0= 'and' ) ) ( (lv_right_3_0= ruleRelational ) ) )* ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1638:1: (this_Relational_0= ruleRelational ( () ( (lv_op_2_0= 'and' ) ) ( (lv_right_3_0= ruleRelational ) ) )* )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1638:1: (this_Relational_0= ruleRelational ( () ( (lv_op_2_0= 'and' ) ) ( (lv_right_3_0= ruleRelational ) ) )* )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1639:5: this_Relational_0= ruleRelational ( () ( (lv_op_2_0= 'and' ) ) ( (lv_right_3_0= ruleRelational ) ) )*
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getAndExpAccess().getRelationalParserRuleCall_0()); 
                  
            }
            pushFollow(FOLLOW_ruleRelational_in_ruleAndExp3586);
            this_Relational_0=ruleRelational();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_Relational_0; 
                      afterParserOrEnumRuleCall();
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1647:1: ( () ( (lv_op_2_0= 'and' ) ) ( (lv_right_3_0= ruleRelational ) ) )*
            loop28:
            do {
                int alt28=2;
                int LA28_0 = input.LA(1);

                if ( (LA28_0==58) ) {
                    alt28=1;
                }


                switch (alt28) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1647:2: () ( (lv_op_2_0= 'and' ) ) ( (lv_right_3_0= ruleRelational ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1647:2: ()
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1648:5: 
            	    {
            	    if ( state.backtracking==0 ) {

            	              current = forceCreateModelElementAndSet(
            	                  grammarAccess.getAndExpAccess().getExpressionLeftAction_1_0(),
            	                  current);
            	          
            	    }

            	    }

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1653:2: ( (lv_op_2_0= 'and' ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1654:1: (lv_op_2_0= 'and' )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1654:1: (lv_op_2_0= 'and' )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1655:3: lv_op_2_0= 'and'
            	    {
            	    lv_op_2_0=(Token)match(input,58,FOLLOW_58_in_ruleAndExp3613); if (state.failed) return current;
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

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1668:2: ( (lv_right_3_0= ruleRelational ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1669:1: (lv_right_3_0= ruleRelational )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1669:1: (lv_right_3_0= ruleRelational )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1670:3: lv_right_3_0= ruleRelational
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getAndExpAccess().getRightRelationalParserRuleCall_1_2_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleRelational_in_ruleAndExp3647);
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
    // $ANTLR end "ruleAndExp"


    // $ANTLR start "entryRuleRelational"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1694:1: entryRuleRelational returns [EObject current=null] : iv_ruleRelational= ruleRelational EOF ;
    public final EObject entryRuleRelational() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRelational = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1695:2: (iv_ruleRelational= ruleRelational EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1696:2: iv_ruleRelational= ruleRelational EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getRelationalRule()); 
            }
            pushFollow(FOLLOW_ruleRelational_in_entryRuleRelational3685);
            iv_ruleRelational=ruleRelational();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleRelational; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleRelational3695); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1703:1: ruleRelational returns [EObject current=null] : (this_PairExpr_0= rulePairExpr ( ( () ( ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) ) ) ) ( (lv_right_3_0= rulePairExpr ) ) )? ) ;
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
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1706:28: ( (this_PairExpr_0= rulePairExpr ( ( () ( ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) ) ) ) ( (lv_right_3_0= rulePairExpr ) ) )? ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1707:1: (this_PairExpr_0= rulePairExpr ( ( () ( ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) ) ) ) ( (lv_right_3_0= rulePairExpr ) ) )? )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1707:1: (this_PairExpr_0= rulePairExpr ( ( () ( ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) ) ) ) ( (lv_right_3_0= rulePairExpr ) ) )? )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1708:5: this_PairExpr_0= rulePairExpr ( ( () ( ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) ) ) ) ( (lv_right_3_0= rulePairExpr ) ) )?
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getRelationalAccess().getPairExprParserRuleCall_0()); 
                  
            }
            pushFollow(FOLLOW_rulePairExpr_in_ruleRelational3742);
            this_PairExpr_0=rulePairExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_PairExpr_0; 
                      afterParserOrEnumRuleCall();
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1716:1: ( ( () ( ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) ) ) ) ( (lv_right_3_0= rulePairExpr ) ) )?
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==36||LA30_0==38||(LA30_0>=59 && LA30_0<=62)) ) {
                alt30=1;
            }
            switch (alt30) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1716:2: ( () ( ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) ) ) ) ( (lv_right_3_0= rulePairExpr ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1716:2: ( () ( ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1716:3: () ( ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1716:3: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1717:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElementAndSet(
                                  grammarAccess.getRelationalAccess().getExpressionLeftAction_1_0_0(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1722:2: ( ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1723:1: ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1723:1: ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1724:1: (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1724:1: (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' )
                    int alt29=6;
                    switch ( input.LA(1) ) {
                    case 59:
                        {
                        alt29=1;
                        }
                        break;
                    case 60:
                        {
                        alt29=2;
                        }
                        break;
                    case 61:
                        {
                        alt29=3;
                        }
                        break;
                    case 62:
                        {
                        alt29=4;
                        }
                        break;
                    case 36:
                        {
                        alt29=5;
                        }
                        break;
                    case 38:
                        {
                        alt29=6;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return current;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 29, 0, input);

                        throw nvae;
                    }

                    switch (alt29) {
                        case 1 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1725:3: lv_op_2_1= '!='
                            {
                            lv_op_2_1=(Token)match(input,59,FOLLOW_59_in_ruleRelational3772); if (state.failed) return current;
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
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1737:8: lv_op_2_2= '='
                            {
                            lv_op_2_2=(Token)match(input,60,FOLLOW_60_in_ruleRelational3801); if (state.failed) return current;
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
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1749:8: lv_op_2_3= '>='
                            {
                            lv_op_2_3=(Token)match(input,61,FOLLOW_61_in_ruleRelational3830); if (state.failed) return current;
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
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1761:8: lv_op_2_4= '<='
                            {
                            lv_op_2_4=(Token)match(input,62,FOLLOW_62_in_ruleRelational3859); if (state.failed) return current;
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
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1773:8: lv_op_2_5= '<'
                            {
                            lv_op_2_5=(Token)match(input,36,FOLLOW_36_in_ruleRelational3888); if (state.failed) return current;
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
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1785:8: lv_op_2_6= '>'
                            {
                            lv_op_2_6=(Token)match(input,38,FOLLOW_38_in_ruleRelational3917); if (state.failed) return current;
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

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1800:3: ( (lv_right_3_0= rulePairExpr ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1801:1: (lv_right_3_0= rulePairExpr )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1801:1: (lv_right_3_0= rulePairExpr )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1802:3: lv_right_3_0= rulePairExpr
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getRelationalAccess().getRightPairExprParserRuleCall_1_1_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_rulePairExpr_in_ruleRelational3955);
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1826:1: entryRuleArgPairExpr returns [EObject current=null] : iv_ruleArgPairExpr= ruleArgPairExpr EOF ;
    public final EObject entryRuleArgPairExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleArgPairExpr = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1827:2: (iv_ruleArgPairExpr= ruleArgPairExpr EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1828:2: iv_ruleArgPairExpr= ruleArgPairExpr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getArgPairExprRule()); 
            }
            pushFollow(FOLLOW_ruleArgPairExpr_in_entryRuleArgPairExpr3993);
            iv_ruleArgPairExpr=ruleArgPairExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleArgPairExpr; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleArgPairExpr4003); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1835:1: ruleArgPairExpr returns [EObject current=null] : ( () ( (lv_arg_1_0= RULE_ID ) ) ( (lv_op_2_0= '::' ) ) ( (lv_right_3_0= ruleAddition ) ) ) ;
    public final EObject ruleArgPairExpr() throws RecognitionException {
        EObject current = null;

        Token lv_arg_1_0=null;
        Token lv_op_2_0=null;
        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1838:28: ( ( () ( (lv_arg_1_0= RULE_ID ) ) ( (lv_op_2_0= '::' ) ) ( (lv_right_3_0= ruleAddition ) ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1839:1: ( () ( (lv_arg_1_0= RULE_ID ) ) ( (lv_op_2_0= '::' ) ) ( (lv_right_3_0= ruleAddition ) ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1839:1: ( () ( (lv_arg_1_0= RULE_ID ) ) ( (lv_op_2_0= '::' ) ) ( (lv_right_3_0= ruleAddition ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1839:2: () ( (lv_arg_1_0= RULE_ID ) ) ( (lv_op_2_0= '::' ) ) ( (lv_right_3_0= ruleAddition ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1839:2: ()
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1840:5: 
            {
            if ( state.backtracking==0 ) {

                      current = forceCreateModelElement(
                          grammarAccess.getArgPairExprAccess().getArgPairExprAction_0(),
                          current);
                  
            }

            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1845:2: ( (lv_arg_1_0= RULE_ID ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1846:1: (lv_arg_1_0= RULE_ID )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1846:1: (lv_arg_1_0= RULE_ID )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1847:3: lv_arg_1_0= RULE_ID
            {
            lv_arg_1_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleArgPairExpr4054); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(lv_arg_1_0, grammarAccess.getArgPairExprAccess().getArgIDTerminalRuleCall_1_0()); 
              		
            }
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElement(grammarAccess.getArgPairExprRule());
              	        }
                     		setWithLastConsumed(
                     			current, 
                     			"arg",
                      		lv_arg_1_0, 
                      		"ID");
              	    
            }

            }


            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1863:2: ( (lv_op_2_0= '::' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1864:1: (lv_op_2_0= '::' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1864:1: (lv_op_2_0= '::' )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1865:3: lv_op_2_0= '::'
            {
            lv_op_2_0=(Token)match(input,63,FOLLOW_63_in_ruleArgPairExpr4077); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                      newLeafNode(lv_op_2_0, grammarAccess.getArgPairExprAccess().getOpColonColonKeyword_2_0());
                  
            }
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElement(grammarAccess.getArgPairExprRule());
              	        }
                     		setWithLastConsumed(current, "op", lv_op_2_0, "::");
              	    
            }

            }


            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1878:2: ( (lv_right_3_0= ruleAddition ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1879:1: (lv_right_3_0= ruleAddition )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1879:1: (lv_right_3_0= ruleAddition )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1880:3: lv_right_3_0= ruleAddition
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getArgPairExprAccess().getRightAdditionParserRuleCall_3_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleAddition_in_ruleArgPairExpr4111);
            lv_right_3_0=ruleAddition();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getArgPairExprRule());
              	        }
                     		set(
                     			current, 
                     			"right",
                      		lv_right_3_0, 
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1904:1: entryRulePairExpr returns [EObject current=null] : iv_rulePairExpr= rulePairExpr EOF ;
    public final EObject entryRulePairExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePairExpr = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1905:2: (iv_rulePairExpr= rulePairExpr EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1906:2: iv_rulePairExpr= rulePairExpr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getPairExprRule()); 
            }
            pushFollow(FOLLOW_rulePairExpr_in_entryRulePairExpr4147);
            iv_rulePairExpr=rulePairExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_rulePairExpr; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRulePairExpr4157); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1913:1: rulePairExpr returns [EObject current=null] : ( ( ( ruleArgPairExpr )=>this_ArgPairExpr_0= ruleArgPairExpr ) | (this_Addition_1= ruleAddition ( ( () ( (lv_op_3_0= '::' ) ) ) ( (lv_right_4_0= ruleAddition ) ) )? ) ) ;
    public final EObject rulePairExpr() throws RecognitionException {
        EObject current = null;

        Token lv_op_3_0=null;
        EObject this_ArgPairExpr_0 = null;

        EObject this_Addition_1 = null;

        EObject lv_right_4_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1916:28: ( ( ( ( ruleArgPairExpr )=>this_ArgPairExpr_0= ruleArgPairExpr ) | (this_Addition_1= ruleAddition ( ( () ( (lv_op_3_0= '::' ) ) ) ( (lv_right_4_0= ruleAddition ) ) )? ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1917:1: ( ( ( ruleArgPairExpr )=>this_ArgPairExpr_0= ruleArgPairExpr ) | (this_Addition_1= ruleAddition ( ( () ( (lv_op_3_0= '::' ) ) ) ( (lv_right_4_0= ruleAddition ) ) )? ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1917:1: ( ( ( ruleArgPairExpr )=>this_ArgPairExpr_0= ruleArgPairExpr ) | (this_Addition_1= ruleAddition ( ( () ( (lv_op_3_0= '::' ) ) ) ( (lv_right_4_0= ruleAddition ) ) )? ) )
            int alt32=2;
            alt32 = dfa32.predict(input);
            switch (alt32) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1917:2: ( ( ruleArgPairExpr )=>this_ArgPairExpr_0= ruleArgPairExpr )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1917:2: ( ( ruleArgPairExpr )=>this_ArgPairExpr_0= ruleArgPairExpr )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1917:3: ( ruleArgPairExpr )=>this_ArgPairExpr_0= ruleArgPairExpr
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getPairExprAccess().getArgPairExprParserRuleCall_0()); 
                          
                    }
                    pushFollow(FOLLOW_ruleArgPairExpr_in_rulePairExpr4210);
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1927:6: (this_Addition_1= ruleAddition ( ( () ( (lv_op_3_0= '::' ) ) ) ( (lv_right_4_0= ruleAddition ) ) )? )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1927:6: (this_Addition_1= ruleAddition ( ( () ( (lv_op_3_0= '::' ) ) ) ( (lv_right_4_0= ruleAddition ) ) )? )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1928:5: this_Addition_1= ruleAddition ( ( () ( (lv_op_3_0= '::' ) ) ) ( (lv_right_4_0= ruleAddition ) ) )?
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getPairExprAccess().getAdditionParserRuleCall_1_0()); 
                          
                    }
                    pushFollow(FOLLOW_ruleAddition_in_rulePairExpr4239);
                    this_Addition_1=ruleAddition();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_Addition_1; 
                              afterParserOrEnumRuleCall();
                          
                    }
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1936:1: ( ( () ( (lv_op_3_0= '::' ) ) ) ( (lv_right_4_0= ruleAddition ) ) )?
                    int alt31=2;
                    int LA31_0 = input.LA(1);

                    if ( (LA31_0==63) ) {
                        alt31=1;
                    }
                    switch (alt31) {
                        case 1 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1936:2: ( () ( (lv_op_3_0= '::' ) ) ) ( (lv_right_4_0= ruleAddition ) )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1936:2: ( () ( (lv_op_3_0= '::' ) ) )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1936:3: () ( (lv_op_3_0= '::' ) )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1936:3: ()
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1937:5: 
                            {
                            if ( state.backtracking==0 ) {

                                      current = forceCreateModelElementAndSet(
                                          grammarAccess.getPairExprAccess().getExpressionLeftAction_1_1_0_0(),
                                          current);
                                  
                            }

                            }

                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1942:2: ( (lv_op_3_0= '::' ) )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1943:1: (lv_op_3_0= '::' )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1943:1: (lv_op_3_0= '::' )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1944:3: lv_op_3_0= '::'
                            {
                            lv_op_3_0=(Token)match(input,63,FOLLOW_63_in_rulePairExpr4267); if (state.failed) return current;
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

                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1957:3: ( (lv_right_4_0= ruleAddition ) )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1958:1: (lv_right_4_0= ruleAddition )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1958:1: (lv_right_4_0= ruleAddition )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1959:3: lv_right_4_0= ruleAddition
                            {
                            if ( state.backtracking==0 ) {
                               
                              	        newCompositeNode(grammarAccess.getPairExprAccess().getRightAdditionParserRuleCall_1_1_1_0()); 
                              	    
                            }
                            pushFollow(FOLLOW_ruleAddition_in_rulePairExpr4302);
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1983:1: entryRuleAddition returns [EObject current=null] : iv_ruleAddition= ruleAddition EOF ;
    public final EObject entryRuleAddition() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAddition = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1984:2: (iv_ruleAddition= ruleAddition EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1985:2: iv_ruleAddition= ruleAddition EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAdditionRule()); 
            }
            pushFollow(FOLLOW_ruleAddition_in_entryRuleAddition4341);
            iv_ruleAddition=ruleAddition();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAddition; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleAddition4351); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1992:1: ruleAddition returns [EObject current=null] : (this_Multiplication_0= ruleMultiplication ( ( () ( ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) ) ) ) ( (lv_right_3_0= ruleMultiplication ) ) )* ) ;
    public final EObject ruleAddition() throws RecognitionException {
        EObject current = null;

        Token lv_op_2_1=null;
        Token lv_op_2_2=null;
        EObject this_Multiplication_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1995:28: ( (this_Multiplication_0= ruleMultiplication ( ( () ( ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) ) ) ) ( (lv_right_3_0= ruleMultiplication ) ) )* ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1996:1: (this_Multiplication_0= ruleMultiplication ( ( () ( ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) ) ) ) ( (lv_right_3_0= ruleMultiplication ) ) )* )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1996:1: (this_Multiplication_0= ruleMultiplication ( ( () ( ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) ) ) ) ( (lv_right_3_0= ruleMultiplication ) ) )* )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1997:5: this_Multiplication_0= ruleMultiplication ( ( () ( ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) ) ) ) ( (lv_right_3_0= ruleMultiplication ) ) )*
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getAdditionAccess().getMultiplicationParserRuleCall_0()); 
                  
            }
            pushFollow(FOLLOW_ruleMultiplication_in_ruleAddition4398);
            this_Multiplication_0=ruleMultiplication();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_Multiplication_0; 
                      afterParserOrEnumRuleCall();
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2005:1: ( ( () ( ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) ) ) ) ( (lv_right_3_0= ruleMultiplication ) ) )*
            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);

                if ( ((LA34_0>=64 && LA34_0<=65)) ) {
                    alt34=1;
                }


                switch (alt34) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2005:2: ( () ( ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) ) ) ) ( (lv_right_3_0= ruleMultiplication ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2005:2: ( () ( ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) ) ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2005:3: () ( ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2005:3: ()
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2006:5: 
            	    {
            	    if ( state.backtracking==0 ) {

            	              current = forceCreateModelElementAndSet(
            	                  grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0(),
            	                  current);
            	          
            	    }

            	    }

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2011:2: ( ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2012:1: ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2012:1: ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2013:1: (lv_op_2_1= '+' | lv_op_2_2= '-' )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2013:1: (lv_op_2_1= '+' | lv_op_2_2= '-' )
            	    int alt33=2;
            	    int LA33_0 = input.LA(1);

            	    if ( (LA33_0==64) ) {
            	        alt33=1;
            	    }
            	    else if ( (LA33_0==65) ) {
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
            	            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2014:3: lv_op_2_1= '+'
            	            {
            	            lv_op_2_1=(Token)match(input,64,FOLLOW_64_in_ruleAddition4428); if (state.failed) return current;
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
            	            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2026:8: lv_op_2_2= '-'
            	            {
            	            lv_op_2_2=(Token)match(input,65,FOLLOW_65_in_ruleAddition4457); if (state.failed) return current;
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

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2041:3: ( (lv_right_3_0= ruleMultiplication ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2042:1: (lv_right_3_0= ruleMultiplication )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2042:1: (lv_right_3_0= ruleMultiplication )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2043:3: lv_right_3_0= ruleMultiplication
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getAdditionAccess().getRightMultiplicationParserRuleCall_1_1_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleMultiplication_in_ruleAddition4495);
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
            	    break loop34;
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2067:1: entryRuleMultiplication returns [EObject current=null] : iv_ruleMultiplication= ruleMultiplication EOF ;
    public final EObject entryRuleMultiplication() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleMultiplication = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2068:2: (iv_ruleMultiplication= ruleMultiplication EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2069:2: iv_ruleMultiplication= ruleMultiplication EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getMultiplicationRule()); 
            }
            pushFollow(FOLLOW_ruleMultiplication_in_entryRuleMultiplication4533);
            iv_ruleMultiplication=ruleMultiplication();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleMultiplication; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleMultiplication4543); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2076:1: ruleMultiplication returns [EObject current=null] : (this_GamlBinaryExpr_0= ruleGamlBinaryExpr ( ( () ( ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) ) ) ) ( (lv_right_3_0= ruleGamlBinaryExpr ) ) )* ) ;
    public final EObject ruleMultiplication() throws RecognitionException {
        EObject current = null;

        Token lv_op_2_1=null;
        Token lv_op_2_2=null;
        Token lv_op_2_3=null;
        EObject this_GamlBinaryExpr_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2079:28: ( (this_GamlBinaryExpr_0= ruleGamlBinaryExpr ( ( () ( ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) ) ) ) ( (lv_right_3_0= ruleGamlBinaryExpr ) ) )* ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2080:1: (this_GamlBinaryExpr_0= ruleGamlBinaryExpr ( ( () ( ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) ) ) ) ( (lv_right_3_0= ruleGamlBinaryExpr ) ) )* )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2080:1: (this_GamlBinaryExpr_0= ruleGamlBinaryExpr ( ( () ( ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) ) ) ) ( (lv_right_3_0= ruleGamlBinaryExpr ) ) )* )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2081:5: this_GamlBinaryExpr_0= ruleGamlBinaryExpr ( ( () ( ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) ) ) ) ( (lv_right_3_0= ruleGamlBinaryExpr ) ) )*
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getMultiplicationAccess().getGamlBinaryExprParserRuleCall_0()); 
                  
            }
            pushFollow(FOLLOW_ruleGamlBinaryExpr_in_ruleMultiplication4590);
            this_GamlBinaryExpr_0=ruleGamlBinaryExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_GamlBinaryExpr_0; 
                      afterParserOrEnumRuleCall();
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2089:1: ( ( () ( ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) ) ) ) ( (lv_right_3_0= ruleGamlBinaryExpr ) ) )*
            loop36:
            do {
                int alt36=2;
                int LA36_0 = input.LA(1);

                if ( ((LA36_0>=66 && LA36_0<=68)) ) {
                    alt36=1;
                }


                switch (alt36) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2089:2: ( () ( ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) ) ) ) ( (lv_right_3_0= ruleGamlBinaryExpr ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2089:2: ( () ( ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) ) ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2089:3: () ( ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2089:3: ()
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2090:5: 
            	    {
            	    if ( state.backtracking==0 ) {

            	              current = forceCreateModelElementAndSet(
            	                  grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0(),
            	                  current);
            	          
            	    }

            	    }

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2095:2: ( ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2096:1: ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2096:1: ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2097:1: (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2097:1: (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' )
            	    int alt35=3;
            	    switch ( input.LA(1) ) {
            	    case 66:
            	        {
            	        alt35=1;
            	        }
            	        break;
            	    case 67:
            	        {
            	        alt35=2;
            	        }
            	        break;
            	    case 68:
            	        {
            	        alt35=3;
            	        }
            	        break;
            	    default:
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 35, 0, input);

            	        throw nvae;
            	    }

            	    switch (alt35) {
            	        case 1 :
            	            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2098:3: lv_op_2_1= '*'
            	            {
            	            lv_op_2_1=(Token)match(input,66,FOLLOW_66_in_ruleMultiplication4620); if (state.failed) return current;
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
            	            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2110:8: lv_op_2_2= '/'
            	            {
            	            lv_op_2_2=(Token)match(input,67,FOLLOW_67_in_ruleMultiplication4649); if (state.failed) return current;
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
            	            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2122:8: lv_op_2_3= '^'
            	            {
            	            lv_op_2_3=(Token)match(input,68,FOLLOW_68_in_ruleMultiplication4678); if (state.failed) return current;
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

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2137:3: ( (lv_right_3_0= ruleGamlBinaryExpr ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2138:1: (lv_right_3_0= ruleGamlBinaryExpr )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2138:1: (lv_right_3_0= ruleGamlBinaryExpr )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2139:3: lv_right_3_0= ruleGamlBinaryExpr
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getMultiplicationAccess().getRightGamlBinaryExprParserRuleCall_1_1_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleGamlBinaryExpr_in_ruleMultiplication4716);
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
            	    break loop36;
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2163:1: entryRuleGamlBinaryExpr returns [EObject current=null] : iv_ruleGamlBinaryExpr= ruleGamlBinaryExpr EOF ;
    public final EObject entryRuleGamlBinaryExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleGamlBinaryExpr = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2164:2: (iv_ruleGamlBinaryExpr= ruleGamlBinaryExpr EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2165:2: iv_ruleGamlBinaryExpr= ruleGamlBinaryExpr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getGamlBinaryExprRule()); 
            }
            pushFollow(FOLLOW_ruleGamlBinaryExpr_in_entryRuleGamlBinaryExpr4754);
            iv_ruleGamlBinaryExpr=ruleGamlBinaryExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleGamlBinaryExpr; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleGamlBinaryExpr4764); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2172:1: ruleGamlBinaryExpr returns [EObject current=null] : (this_GamlUnitExpr_0= ruleGamlUnitExpr ( ( () ( (lv_op_2_0= RULE_ID ) ) ) ( (lv_right_3_0= ruleGamlUnitExpr ) ) )* ) ;
    public final EObject ruleGamlBinaryExpr() throws RecognitionException {
        EObject current = null;

        Token lv_op_2_0=null;
        EObject this_GamlUnitExpr_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2175:28: ( (this_GamlUnitExpr_0= ruleGamlUnitExpr ( ( () ( (lv_op_2_0= RULE_ID ) ) ) ( (lv_right_3_0= ruleGamlUnitExpr ) ) )* ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2176:1: (this_GamlUnitExpr_0= ruleGamlUnitExpr ( ( () ( (lv_op_2_0= RULE_ID ) ) ) ( (lv_right_3_0= ruleGamlUnitExpr ) ) )* )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2176:1: (this_GamlUnitExpr_0= ruleGamlUnitExpr ( ( () ( (lv_op_2_0= RULE_ID ) ) ) ( (lv_right_3_0= ruleGamlUnitExpr ) ) )* )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2177:5: this_GamlUnitExpr_0= ruleGamlUnitExpr ( ( () ( (lv_op_2_0= RULE_ID ) ) ) ( (lv_right_3_0= ruleGamlUnitExpr ) ) )*
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getGamlBinaryExprAccess().getGamlUnitExprParserRuleCall_0()); 
                  
            }
            pushFollow(FOLLOW_ruleGamlUnitExpr_in_ruleGamlBinaryExpr4811);
            this_GamlUnitExpr_0=ruleGamlUnitExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_GamlUnitExpr_0; 
                      afterParserOrEnumRuleCall();
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2185:1: ( ( () ( (lv_op_2_0= RULE_ID ) ) ) ( (lv_right_3_0= ruleGamlUnitExpr ) ) )*
            loop37:
            do {
                int alt37=2;
                int LA37_0 = input.LA(1);

                if ( (LA37_0==RULE_ID) ) {
                    int LA37_2 = input.LA(2);

                    if ( ((LA37_2>=RULE_ID && LA37_2<=RULE_BOOLEAN)||LA37_2==51||LA37_2==65||(LA37_2>=70 && LA37_2<=75)||LA37_2==78) ) {
                        alt37=1;
                    }


                }


                switch (alt37) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2185:2: ( () ( (lv_op_2_0= RULE_ID ) ) ) ( (lv_right_3_0= ruleGamlUnitExpr ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2185:2: ( () ( (lv_op_2_0= RULE_ID ) ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2185:3: () ( (lv_op_2_0= RULE_ID ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2185:3: ()
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2186:5: 
            	    {
            	    if ( state.backtracking==0 ) {

            	              current = forceCreateModelElementAndSet(
            	                  grammarAccess.getGamlBinaryExprAccess().getGamlBinaryExprLeftAction_1_0_0(),
            	                  current);
            	          
            	    }

            	    }

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2191:2: ( (lv_op_2_0= RULE_ID ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2192:1: (lv_op_2_0= RULE_ID )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2192:1: (lv_op_2_0= RULE_ID )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2193:3: lv_op_2_0= RULE_ID
            	    {
            	    lv_op_2_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleGamlBinaryExpr4838); if (state.failed) return current;
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

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2209:3: ( (lv_right_3_0= ruleGamlUnitExpr ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2210:1: (lv_right_3_0= ruleGamlUnitExpr )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2210:1: (lv_right_3_0= ruleGamlUnitExpr )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2211:3: lv_right_3_0= ruleGamlUnitExpr
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getGamlBinaryExprAccess().getRightGamlUnitExprParserRuleCall_1_1_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleGamlUnitExpr_in_ruleGamlBinaryExpr4865);
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
    // $ANTLR end "ruleGamlBinaryExpr"


    // $ANTLR start "entryRuleGamlUnitExpr"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2235:1: entryRuleGamlUnitExpr returns [EObject current=null] : iv_ruleGamlUnitExpr= ruleGamlUnitExpr EOF ;
    public final EObject entryRuleGamlUnitExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleGamlUnitExpr = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2236:2: (iv_ruleGamlUnitExpr= ruleGamlUnitExpr EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2237:2: iv_ruleGamlUnitExpr= ruleGamlUnitExpr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getGamlUnitExprRule()); 
            }
            pushFollow(FOLLOW_ruleGamlUnitExpr_in_entryRuleGamlUnitExpr4903);
            iv_ruleGamlUnitExpr=ruleGamlUnitExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleGamlUnitExpr; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleGamlUnitExpr4913); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2244:1: ruleGamlUnitExpr returns [EObject current=null] : (this_GamlUnaryExpr_0= ruleGamlUnaryExpr ( ( () ( ( (lv_op_2_1= '#' | lv_op_2_2= '\\u00B0' ) ) ) ) ( (lv_right_3_0= ruleUnitName ) ) )? ) ;
    public final EObject ruleGamlUnitExpr() throws RecognitionException {
        EObject current = null;

        Token lv_op_2_1=null;
        Token lv_op_2_2=null;
        EObject this_GamlUnaryExpr_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2247:28: ( (this_GamlUnaryExpr_0= ruleGamlUnaryExpr ( ( () ( ( (lv_op_2_1= '#' | lv_op_2_2= '\\u00B0' ) ) ) ) ( (lv_right_3_0= ruleUnitName ) ) )? ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2248:1: (this_GamlUnaryExpr_0= ruleGamlUnaryExpr ( ( () ( ( (lv_op_2_1= '#' | lv_op_2_2= '\\u00B0' ) ) ) ) ( (lv_right_3_0= ruleUnitName ) ) )? )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2248:1: (this_GamlUnaryExpr_0= ruleGamlUnaryExpr ( ( () ( ( (lv_op_2_1= '#' | lv_op_2_2= '\\u00B0' ) ) ) ) ( (lv_right_3_0= ruleUnitName ) ) )? )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2249:5: this_GamlUnaryExpr_0= ruleGamlUnaryExpr ( ( () ( ( (lv_op_2_1= '#' | lv_op_2_2= '\\u00B0' ) ) ) ) ( (lv_right_3_0= ruleUnitName ) ) )?
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getGamlUnitExprAccess().getGamlUnaryExprParserRuleCall_0()); 
                  
            }
            pushFollow(FOLLOW_ruleGamlUnaryExpr_in_ruleGamlUnitExpr4960);
            this_GamlUnaryExpr_0=ruleGamlUnaryExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_GamlUnaryExpr_0; 
                      afterParserOrEnumRuleCall();
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2257:1: ( ( () ( ( (lv_op_2_1= '#' | lv_op_2_2= '\\u00B0' ) ) ) ) ( (lv_right_3_0= ruleUnitName ) ) )?
            int alt39=2;
            int LA39_0 = input.LA(1);

            if ( ((LA39_0>=69 && LA39_0<=70)) ) {
                alt39=1;
            }
            switch (alt39) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2257:2: ( () ( ( (lv_op_2_1= '#' | lv_op_2_2= '\\u00B0' ) ) ) ) ( (lv_right_3_0= ruleUnitName ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2257:2: ( () ( ( (lv_op_2_1= '#' | lv_op_2_2= '\\u00B0' ) ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2257:3: () ( ( (lv_op_2_1= '#' | lv_op_2_2= '\\u00B0' ) ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2257:3: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2258:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElementAndSet(
                                  grammarAccess.getGamlUnitExprAccess().getGamlUnitExprLeftAction_1_0_0(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2263:2: ( ( (lv_op_2_1= '#' | lv_op_2_2= '\\u00B0' ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2264:1: ( (lv_op_2_1= '#' | lv_op_2_2= '\\u00B0' ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2264:1: ( (lv_op_2_1= '#' | lv_op_2_2= '\\u00B0' ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2265:1: (lv_op_2_1= '#' | lv_op_2_2= '\\u00B0' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2265:1: (lv_op_2_1= '#' | lv_op_2_2= '\\u00B0' )
                    int alt38=2;
                    int LA38_0 = input.LA(1);

                    if ( (LA38_0==69) ) {
                        alt38=1;
                    }
                    else if ( (LA38_0==70) ) {
                        alt38=2;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return current;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 38, 0, input);

                        throw nvae;
                    }
                    switch (alt38) {
                        case 1 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2266:3: lv_op_2_1= '#'
                            {
                            lv_op_2_1=(Token)match(input,69,FOLLOW_69_in_ruleGamlUnitExpr4990); if (state.failed) return current;
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
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2278:8: lv_op_2_2= '\\u00B0'
                            {
                            lv_op_2_2=(Token)match(input,70,FOLLOW_70_in_ruleGamlUnitExpr5019); if (state.failed) return current;
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

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2293:3: ( (lv_right_3_0= ruleUnitName ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2294:1: (lv_right_3_0= ruleUnitName )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2294:1: (lv_right_3_0= ruleUnitName )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2295:3: lv_right_3_0= ruleUnitName
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getGamlUnitExprAccess().getRightUnitNameParserRuleCall_1_1_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleUnitName_in_ruleGamlUnitExpr5057);
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2319:1: entryRuleGamlUnaryExpr returns [EObject current=null] : iv_ruleGamlUnaryExpr= ruleGamlUnaryExpr EOF ;
    public final EObject entryRuleGamlUnaryExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleGamlUnaryExpr = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2320:2: (iv_ruleGamlUnaryExpr= ruleGamlUnaryExpr EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2321:2: iv_ruleGamlUnaryExpr= ruleGamlUnaryExpr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getGamlUnaryExprRule()); 
            }
            pushFollow(FOLLOW_ruleGamlUnaryExpr_in_entryRuleGamlUnaryExpr5095);
            iv_ruleGamlUnaryExpr=ruleGamlUnaryExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleGamlUnaryExpr; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleGamlUnaryExpr5105); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2328:1: ruleGamlUnaryExpr returns [EObject current=null] : (this_Access_0= ruleAccess | ( () ( ( () ( ( (lv_op_3_0= '\\u00B0' ) ) ( (lv_right_4_0= ruleUnitName ) ) ) ) | ( ( ( (lv_op_5_1= '-' | lv_op_5_2= '!' | lv_op_5_3= 'my' | lv_op_5_4= 'the' | lv_op_5_5= 'not' ) ) ) ( (lv_right_6_0= ruleGamlUnaryExpr ) ) ) ) ) ) ;
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
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2331:28: ( (this_Access_0= ruleAccess | ( () ( ( () ( ( (lv_op_3_0= '\\u00B0' ) ) ( (lv_right_4_0= ruleUnitName ) ) ) ) | ( ( ( (lv_op_5_1= '-' | lv_op_5_2= '!' | lv_op_5_3= 'my' | lv_op_5_4= 'the' | lv_op_5_5= 'not' ) ) ) ( (lv_right_6_0= ruleGamlUnaryExpr ) ) ) ) ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2332:1: (this_Access_0= ruleAccess | ( () ( ( () ( ( (lv_op_3_0= '\\u00B0' ) ) ( (lv_right_4_0= ruleUnitName ) ) ) ) | ( ( ( (lv_op_5_1= '-' | lv_op_5_2= '!' | lv_op_5_3= 'my' | lv_op_5_4= 'the' | lv_op_5_5= 'not' ) ) ) ( (lv_right_6_0= ruleGamlUnaryExpr ) ) ) ) ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2332:1: (this_Access_0= ruleAccess | ( () ( ( () ( ( (lv_op_3_0= '\\u00B0' ) ) ( (lv_right_4_0= ruleUnitName ) ) ) ) | ( ( ( (lv_op_5_1= '-' | lv_op_5_2= '!' | lv_op_5_3= 'my' | lv_op_5_4= 'the' | lv_op_5_5= 'not' ) ) ) ( (lv_right_6_0= ruleGamlUnaryExpr ) ) ) ) ) )
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( ((LA42_0>=RULE_ID && LA42_0<=RULE_BOOLEAN)||LA42_0==51||LA42_0==75||LA42_0==78) ) {
                alt42=1;
            }
            else if ( (LA42_0==65||(LA42_0>=70 && LA42_0<=74)) ) {
                alt42=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 42, 0, input);

                throw nvae;
            }
            switch (alt42) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2333:5: this_Access_0= ruleAccess
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getGamlUnaryExprAccess().getAccessParserRuleCall_0()); 
                          
                    }
                    pushFollow(FOLLOW_ruleAccess_in_ruleGamlUnaryExpr5152);
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2342:6: ( () ( ( () ( ( (lv_op_3_0= '\\u00B0' ) ) ( (lv_right_4_0= ruleUnitName ) ) ) ) | ( ( ( (lv_op_5_1= '-' | lv_op_5_2= '!' | lv_op_5_3= 'my' | lv_op_5_4= 'the' | lv_op_5_5= 'not' ) ) ) ( (lv_right_6_0= ruleGamlUnaryExpr ) ) ) ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2342:6: ( () ( ( () ( ( (lv_op_3_0= '\\u00B0' ) ) ( (lv_right_4_0= ruleUnitName ) ) ) ) | ( ( ( (lv_op_5_1= '-' | lv_op_5_2= '!' | lv_op_5_3= 'my' | lv_op_5_4= 'the' | lv_op_5_5= 'not' ) ) ) ( (lv_right_6_0= ruleGamlUnaryExpr ) ) ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2342:7: () ( ( () ( ( (lv_op_3_0= '\\u00B0' ) ) ( (lv_right_4_0= ruleUnitName ) ) ) ) | ( ( ( (lv_op_5_1= '-' | lv_op_5_2= '!' | lv_op_5_3= 'my' | lv_op_5_4= 'the' | lv_op_5_5= 'not' ) ) ) ( (lv_right_6_0= ruleGamlUnaryExpr ) ) ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2342:7: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2343:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getGamlUnaryExprAccess().getGamlUnaryExprAction_1_0(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2348:2: ( ( () ( ( (lv_op_3_0= '\\u00B0' ) ) ( (lv_right_4_0= ruleUnitName ) ) ) ) | ( ( ( (lv_op_5_1= '-' | lv_op_5_2= '!' | lv_op_5_3= 'my' | lv_op_5_4= 'the' | lv_op_5_5= 'not' ) ) ) ( (lv_right_6_0= ruleGamlUnaryExpr ) ) ) )
                    int alt41=2;
                    int LA41_0 = input.LA(1);

                    if ( (LA41_0==70) ) {
                        alt41=1;
                    }
                    else if ( (LA41_0==65||(LA41_0>=71 && LA41_0<=74)) ) {
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
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2348:3: ( () ( ( (lv_op_3_0= '\\u00B0' ) ) ( (lv_right_4_0= ruleUnitName ) ) ) )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2348:3: ( () ( ( (lv_op_3_0= '\\u00B0' ) ) ( (lv_right_4_0= ruleUnitName ) ) ) )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2348:4: () ( ( (lv_op_3_0= '\\u00B0' ) ) ( (lv_right_4_0= ruleUnitName ) ) )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2348:4: ()
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2349:5: 
                            {
                            if ( state.backtracking==0 ) {

                                      current = forceCreateModelElementAndSet(
                                          grammarAccess.getGamlUnaryExprAccess().getGamlUnaryExprLeftAction_1_1_0_0(),
                                          current);
                                  
                            }

                            }

                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2354:2: ( ( (lv_op_3_0= '\\u00B0' ) ) ( (lv_right_4_0= ruleUnitName ) ) )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2354:3: ( (lv_op_3_0= '\\u00B0' ) ) ( (lv_right_4_0= ruleUnitName ) )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2354:3: ( (lv_op_3_0= '\\u00B0' ) )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2355:1: (lv_op_3_0= '\\u00B0' )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2355:1: (lv_op_3_0= '\\u00B0' )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2356:3: lv_op_3_0= '\\u00B0'
                            {
                            lv_op_3_0=(Token)match(input,70,FOLLOW_70_in_ruleGamlUnaryExpr5197); if (state.failed) return current;
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

                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2369:2: ( (lv_right_4_0= ruleUnitName ) )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2370:1: (lv_right_4_0= ruleUnitName )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2370:1: (lv_right_4_0= ruleUnitName )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2371:3: lv_right_4_0= ruleUnitName
                            {
                            if ( state.backtracking==0 ) {
                               
                              	        newCompositeNode(grammarAccess.getGamlUnaryExprAccess().getRightUnitNameParserRuleCall_1_1_0_1_1_0()); 
                              	    
                            }
                            pushFollow(FOLLOW_ruleUnitName_in_ruleGamlUnaryExpr5231);
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
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2388:6: ( ( ( (lv_op_5_1= '-' | lv_op_5_2= '!' | lv_op_5_3= 'my' | lv_op_5_4= 'the' | lv_op_5_5= 'not' ) ) ) ( (lv_right_6_0= ruleGamlUnaryExpr ) ) )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2388:6: ( ( ( (lv_op_5_1= '-' | lv_op_5_2= '!' | lv_op_5_3= 'my' | lv_op_5_4= 'the' | lv_op_5_5= 'not' ) ) ) ( (lv_right_6_0= ruleGamlUnaryExpr ) ) )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2388:7: ( ( (lv_op_5_1= '-' | lv_op_5_2= '!' | lv_op_5_3= 'my' | lv_op_5_4= 'the' | lv_op_5_5= 'not' ) ) ) ( (lv_right_6_0= ruleGamlUnaryExpr ) )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2388:7: ( ( (lv_op_5_1= '-' | lv_op_5_2= '!' | lv_op_5_3= 'my' | lv_op_5_4= 'the' | lv_op_5_5= 'not' ) ) )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2389:1: ( (lv_op_5_1= '-' | lv_op_5_2= '!' | lv_op_5_3= 'my' | lv_op_5_4= 'the' | lv_op_5_5= 'not' ) )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2389:1: ( (lv_op_5_1= '-' | lv_op_5_2= '!' | lv_op_5_3= 'my' | lv_op_5_4= 'the' | lv_op_5_5= 'not' ) )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2390:1: (lv_op_5_1= '-' | lv_op_5_2= '!' | lv_op_5_3= 'my' | lv_op_5_4= 'the' | lv_op_5_5= 'not' )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2390:1: (lv_op_5_1= '-' | lv_op_5_2= '!' | lv_op_5_3= 'my' | lv_op_5_4= 'the' | lv_op_5_5= 'not' )
                            int alt40=5;
                            switch ( input.LA(1) ) {
                            case 65:
                                {
                                alt40=1;
                                }
                                break;
                            case 71:
                                {
                                alt40=2;
                                }
                                break;
                            case 72:
                                {
                                alt40=3;
                                }
                                break;
                            case 73:
                                {
                                alt40=4;
                                }
                                break;
                            case 74:
                                {
                                alt40=5;
                                }
                                break;
                            default:
                                if (state.backtracking>0) {state.failed=true; return current;}
                                NoViableAltException nvae =
                                    new NoViableAltException("", 40, 0, input);

                                throw nvae;
                            }

                            switch (alt40) {
                                case 1 :
                                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2391:3: lv_op_5_1= '-'
                                    {
                                    lv_op_5_1=(Token)match(input,65,FOLLOW_65_in_ruleGamlUnaryExpr5260); if (state.failed) return current;
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
                                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2403:8: lv_op_5_2= '!'
                                    {
                                    lv_op_5_2=(Token)match(input,71,FOLLOW_71_in_ruleGamlUnaryExpr5289); if (state.failed) return current;
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
                                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2415:8: lv_op_5_3= 'my'
                                    {
                                    lv_op_5_3=(Token)match(input,72,FOLLOW_72_in_ruleGamlUnaryExpr5318); if (state.failed) return current;
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
                                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2427:8: lv_op_5_4= 'the'
                                    {
                                    lv_op_5_4=(Token)match(input,73,FOLLOW_73_in_ruleGamlUnaryExpr5347); if (state.failed) return current;
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
                                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2439:8: lv_op_5_5= 'not'
                                    {
                                    lv_op_5_5=(Token)match(input,74,FOLLOW_74_in_ruleGamlUnaryExpr5376); if (state.failed) return current;
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

                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2454:2: ( (lv_right_6_0= ruleGamlUnaryExpr ) )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2455:1: (lv_right_6_0= ruleGamlUnaryExpr )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2455:1: (lv_right_6_0= ruleGamlUnaryExpr )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2456:3: lv_right_6_0= ruleGamlUnaryExpr
                            {
                            if ( state.backtracking==0 ) {
                               
                              	        newCompositeNode(grammarAccess.getGamlUnaryExprAccess().getRightGamlUnaryExprParserRuleCall_1_1_1_1_0()); 
                              	    
                            }
                            pushFollow(FOLLOW_ruleGamlUnaryExpr_in_ruleGamlUnaryExpr5413);
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2480:1: entryRuleAccess returns [EObject current=null] : iv_ruleAccess= ruleAccess EOF ;
    public final EObject entryRuleAccess() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAccess = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2481:2: (iv_ruleAccess= ruleAccess EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2482:2: iv_ruleAccess= ruleAccess EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAccessRule()); 
            }
            pushFollow(FOLLOW_ruleAccess_in_entryRuleAccess5452);
            iv_ruleAccess=ruleAccess();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAccess; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleAccess5462); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2489:1: ruleAccess returns [EObject current=null] : (this_MemberRef_0= ruleMemberRef ( ( () otherlv_2= '[' ) ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ']' )* ) ;
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
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2492:28: ( (this_MemberRef_0= ruleMemberRef ( ( () otherlv_2= '[' ) ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ']' )* ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2493:1: (this_MemberRef_0= ruleMemberRef ( ( () otherlv_2= '[' ) ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ']' )* )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2493:1: (this_MemberRef_0= ruleMemberRef ( ( () otherlv_2= '[' ) ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ']' )* )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2494:5: this_MemberRef_0= ruleMemberRef ( ( () otherlv_2= '[' ) ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ']' )*
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getAccessAccess().getMemberRefParserRuleCall_0()); 
                  
            }
            pushFollow(FOLLOW_ruleMemberRef_in_ruleAccess5509);
            this_MemberRef_0=ruleMemberRef();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_MemberRef_0; 
                      afterParserOrEnumRuleCall();
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2502:1: ( ( () otherlv_2= '[' ) ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ']' )*
            loop44:
            do {
                int alt44=2;
                int LA44_0 = input.LA(1);

                if ( (LA44_0==75) ) {
                    alt44=1;
                }


                switch (alt44) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2502:2: ( () otherlv_2= '[' ) ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ']'
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2502:2: ( () otherlv_2= '[' )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2502:3: () otherlv_2= '['
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2502:3: ()
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2503:5: 
            	    {
            	    if ( state.backtracking==0 ) {

            	              current = forceCreateModelElementAndSet(
            	                  grammarAccess.getAccessAccess().getAccessLeftAction_1_0_0(),
            	                  current);
            	          
            	    }

            	    }

            	    otherlv_2=(Token)match(input,75,FOLLOW_75_in_ruleAccess5531); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	          	newLeafNode(otherlv_2, grammarAccess.getAccessAccess().getLeftSquareBracketKeyword_1_0_1());
            	          
            	    }

            	    }

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2512:2: ( (lv_args_3_0= ruleExpression ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2513:1: (lv_args_3_0= ruleExpression )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2513:1: (lv_args_3_0= ruleExpression )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2514:3: lv_args_3_0= ruleExpression
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getAccessAccess().getArgsExpressionParserRuleCall_1_1_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleExpression_in_ruleAccess5553);
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

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2530:2: (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )*
            	    loop43:
            	    do {
            	        int alt43=2;
            	        int LA43_0 = input.LA(1);

            	        if ( (LA43_0==37) ) {
            	            alt43=1;
            	        }


            	        switch (alt43) {
            	    	case 1 :
            	    	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2530:4: otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) )
            	    	    {
            	    	    otherlv_4=(Token)match(input,37,FOLLOW_37_in_ruleAccess5566); if (state.failed) return current;
            	    	    if ( state.backtracking==0 ) {

            	    	          	newLeafNode(otherlv_4, grammarAccess.getAccessAccess().getCommaKeyword_1_2_0());
            	    	          
            	    	    }
            	    	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2534:1: ( (lv_args_5_0= ruleExpression ) )
            	    	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2535:1: (lv_args_5_0= ruleExpression )
            	    	    {
            	    	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2535:1: (lv_args_5_0= ruleExpression )
            	    	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2536:3: lv_args_5_0= ruleExpression
            	    	    {
            	    	    if ( state.backtracking==0 ) {
            	    	       
            	    	      	        newCompositeNode(grammarAccess.getAccessAccess().getArgsExpressionParserRuleCall_1_2_1_0()); 
            	    	      	    
            	    	    }
            	    	    pushFollow(FOLLOW_ruleExpression_in_ruleAccess5587);
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
            	    	    break loop43;
            	        }
            	    } while (true);

            	    otherlv_6=(Token)match(input,76,FOLLOW_76_in_ruleAccess5601); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	          	newLeafNode(otherlv_6, grammarAccess.getAccessAccess().getRightSquareBracketKeyword_1_3());
            	          
            	    }

            	    }
            	    break;

            	default :
            	    break loop44;
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2564:1: entryRuleMemberRef returns [EObject current=null] : iv_ruleMemberRef= ruleMemberRef EOF ;
    public final EObject entryRuleMemberRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleMemberRef = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2565:2: (iv_ruleMemberRef= ruleMemberRef EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2566:2: iv_ruleMemberRef= ruleMemberRef EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getMemberRefRule()); 
            }
            pushFollow(FOLLOW_ruleMemberRef_in_entryRuleMemberRef5639);
            iv_ruleMemberRef=ruleMemberRef();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleMemberRef; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleMemberRef5649); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2573:1: ruleMemberRef returns [EObject current=null] : (this_PrimaryExpression_0= rulePrimaryExpression ( () ( ( (lv_op_2_0= '.' ) ) ( (lv_right_3_0= rulePrimaryExpression ) ) ) )* ) ;
    public final EObject ruleMemberRef() throws RecognitionException {
        EObject current = null;

        Token lv_op_2_0=null;
        EObject this_PrimaryExpression_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2576:28: ( (this_PrimaryExpression_0= rulePrimaryExpression ( () ( ( (lv_op_2_0= '.' ) ) ( (lv_right_3_0= rulePrimaryExpression ) ) ) )* ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2577:1: (this_PrimaryExpression_0= rulePrimaryExpression ( () ( ( (lv_op_2_0= '.' ) ) ( (lv_right_3_0= rulePrimaryExpression ) ) ) )* )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2577:1: (this_PrimaryExpression_0= rulePrimaryExpression ( () ( ( (lv_op_2_0= '.' ) ) ( (lv_right_3_0= rulePrimaryExpression ) ) ) )* )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2578:5: this_PrimaryExpression_0= rulePrimaryExpression ( () ( ( (lv_op_2_0= '.' ) ) ( (lv_right_3_0= rulePrimaryExpression ) ) ) )*
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getMemberRefAccess().getPrimaryExpressionParserRuleCall_0()); 
                  
            }
            pushFollow(FOLLOW_rulePrimaryExpression_in_ruleMemberRef5696);
            this_PrimaryExpression_0=rulePrimaryExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_PrimaryExpression_0; 
                      afterParserOrEnumRuleCall();
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2586:1: ( () ( ( (lv_op_2_0= '.' ) ) ( (lv_right_3_0= rulePrimaryExpression ) ) ) )*
            loop45:
            do {
                int alt45=2;
                int LA45_0 = input.LA(1);

                if ( (LA45_0==77) ) {
                    alt45=1;
                }


                switch (alt45) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2586:2: () ( ( (lv_op_2_0= '.' ) ) ( (lv_right_3_0= rulePrimaryExpression ) ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2586:2: ()
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2587:5: 
            	    {
            	    if ( state.backtracking==0 ) {

            	              current = forceCreateModelElementAndSet(
            	                  grammarAccess.getMemberRefAccess().getMemberRefLeftAction_1_0(),
            	                  current);
            	          
            	    }

            	    }

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2592:2: ( ( (lv_op_2_0= '.' ) ) ( (lv_right_3_0= rulePrimaryExpression ) ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2592:3: ( (lv_op_2_0= '.' ) ) ( (lv_right_3_0= rulePrimaryExpression ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2592:3: ( (lv_op_2_0= '.' ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2593:1: (lv_op_2_0= '.' )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2593:1: (lv_op_2_0= '.' )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2594:3: lv_op_2_0= '.'
            	    {
            	    lv_op_2_0=(Token)match(input,77,FOLLOW_77_in_ruleMemberRef5724); if (state.failed) return current;
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

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2607:2: ( (lv_right_3_0= rulePrimaryExpression ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2608:1: (lv_right_3_0= rulePrimaryExpression )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2608:1: (lv_right_3_0= rulePrimaryExpression )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2609:3: lv_right_3_0= rulePrimaryExpression
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getMemberRefAccess().getRightPrimaryExpressionParserRuleCall_1_1_1_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_rulePrimaryExpression_in_ruleMemberRef5758);
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
            	    break loop45;
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2633:1: entryRulePrimaryExpression returns [EObject current=null] : iv_rulePrimaryExpression= rulePrimaryExpression EOF ;
    public final EObject entryRulePrimaryExpression() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePrimaryExpression = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2634:2: (iv_rulePrimaryExpression= rulePrimaryExpression EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2635:2: iv_rulePrimaryExpression= rulePrimaryExpression EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getPrimaryExpressionRule()); 
            }
            pushFollow(FOLLOW_rulePrimaryExpression_in_entryRulePrimaryExpression5797);
            iv_rulePrimaryExpression=rulePrimaryExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_rulePrimaryExpression; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRulePrimaryExpression5807); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2642:1: rulePrimaryExpression returns [EObject current=null] : (this_TerminalExpression_0= ruleTerminalExpression | this_AbstractRef_1= ruleAbstractRef | (otherlv_2= '(' this_Expression_3= ruleExpression otherlv_4= ')' ) | (otherlv_5= '[' () ( ( (lv_exprs_7_0= ruleExpression ) ) (otherlv_8= ',' ( (lv_exprs_9_0= ruleExpression ) ) )* )? otherlv_10= ']' ) | (otherlv_11= '{' () ( (lv_left_13_0= ruleExpression ) ) ( (lv_op_14_0= ',' ) ) ( (lv_right_15_0= ruleExpression ) ) (otherlv_16= ',' ( (lv_z_17_0= ruleExpression ) ) )? otherlv_18= '}' ) ) ;
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
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2645:28: ( (this_TerminalExpression_0= ruleTerminalExpression | this_AbstractRef_1= ruleAbstractRef | (otherlv_2= '(' this_Expression_3= ruleExpression otherlv_4= ')' ) | (otherlv_5= '[' () ( ( (lv_exprs_7_0= ruleExpression ) ) (otherlv_8= ',' ( (lv_exprs_9_0= ruleExpression ) ) )* )? otherlv_10= ']' ) | (otherlv_11= '{' () ( (lv_left_13_0= ruleExpression ) ) ( (lv_op_14_0= ',' ) ) ( (lv_right_15_0= ruleExpression ) ) (otherlv_16= ',' ( (lv_z_17_0= ruleExpression ) ) )? otherlv_18= '}' ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2646:1: (this_TerminalExpression_0= ruleTerminalExpression | this_AbstractRef_1= ruleAbstractRef | (otherlv_2= '(' this_Expression_3= ruleExpression otherlv_4= ')' ) | (otherlv_5= '[' () ( ( (lv_exprs_7_0= ruleExpression ) ) (otherlv_8= ',' ( (lv_exprs_9_0= ruleExpression ) ) )* )? otherlv_10= ']' ) | (otherlv_11= '{' () ( (lv_left_13_0= ruleExpression ) ) ( (lv_op_14_0= ',' ) ) ( (lv_right_15_0= ruleExpression ) ) (otherlv_16= ',' ( (lv_z_17_0= ruleExpression ) ) )? otherlv_18= '}' ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2646:1: (this_TerminalExpression_0= ruleTerminalExpression | this_AbstractRef_1= ruleAbstractRef | (otherlv_2= '(' this_Expression_3= ruleExpression otherlv_4= ')' ) | (otherlv_5= '[' () ( ( (lv_exprs_7_0= ruleExpression ) ) (otherlv_8= ',' ( (lv_exprs_9_0= ruleExpression ) ) )* )? otherlv_10= ']' ) | (otherlv_11= '{' () ( (lv_left_13_0= ruleExpression ) ) ( (lv_op_14_0= ',' ) ) ( (lv_right_15_0= ruleExpression ) ) (otherlv_16= ',' ( (lv_z_17_0= ruleExpression ) ) )? otherlv_18= '}' ) )
            int alt49=5;
            switch ( input.LA(1) ) {
            case RULE_STRING:
            case RULE_INTEGER:
            case RULE_DOUBLE:
            case RULE_COLOR:
            case RULE_BOOLEAN:
                {
                alt49=1;
                }
                break;
            case RULE_ID:
                {
                alt49=2;
                }
                break;
            case 78:
                {
                alt49=3;
                }
                break;
            case 75:
                {
                alt49=4;
                }
                break;
            case 51:
                {
                alt49=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 49, 0, input);

                throw nvae;
            }

            switch (alt49) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2647:5: this_TerminalExpression_0= ruleTerminalExpression
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getPrimaryExpressionAccess().getTerminalExpressionParserRuleCall_0()); 
                          
                    }
                    pushFollow(FOLLOW_ruleTerminalExpression_in_rulePrimaryExpression5854);
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2657:5: this_AbstractRef_1= ruleAbstractRef
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getPrimaryExpressionAccess().getAbstractRefParserRuleCall_1()); 
                          
                    }
                    pushFollow(FOLLOW_ruleAbstractRef_in_rulePrimaryExpression5881);
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2666:6: (otherlv_2= '(' this_Expression_3= ruleExpression otherlv_4= ')' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2666:6: (otherlv_2= '(' this_Expression_3= ruleExpression otherlv_4= ')' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2666:8: otherlv_2= '(' this_Expression_3= ruleExpression otherlv_4= ')'
                    {
                    otherlv_2=(Token)match(input,78,FOLLOW_78_in_rulePrimaryExpression5899); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_2, grammarAccess.getPrimaryExpressionAccess().getLeftParenthesisKeyword_2_0());
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getPrimaryExpressionAccess().getExpressionParserRuleCall_2_1()); 
                          
                    }
                    pushFollow(FOLLOW_ruleExpression_in_rulePrimaryExpression5921);
                    this_Expression_3=ruleExpression();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_Expression_3; 
                              afterParserOrEnumRuleCall();
                          
                    }
                    otherlv_4=(Token)match(input,79,FOLLOW_79_in_rulePrimaryExpression5932); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_4, grammarAccess.getPrimaryExpressionAccess().getRightParenthesisKeyword_2_2());
                          
                    }

                    }


                    }
                    break;
                case 4 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2684:6: (otherlv_5= '[' () ( ( (lv_exprs_7_0= ruleExpression ) ) (otherlv_8= ',' ( (lv_exprs_9_0= ruleExpression ) ) )* )? otherlv_10= ']' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2684:6: (otherlv_5= '[' () ( ( (lv_exprs_7_0= ruleExpression ) ) (otherlv_8= ',' ( (lv_exprs_9_0= ruleExpression ) ) )* )? otherlv_10= ']' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2684:8: otherlv_5= '[' () ( ( (lv_exprs_7_0= ruleExpression ) ) (otherlv_8= ',' ( (lv_exprs_9_0= ruleExpression ) ) )* )? otherlv_10= ']'
                    {
                    otherlv_5=(Token)match(input,75,FOLLOW_75_in_rulePrimaryExpression5952); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_5, grammarAccess.getPrimaryExpressionAccess().getLeftSquareBracketKeyword_3_0());
                          
                    }
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2688:1: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2689:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getPrimaryExpressionAccess().getArrayAction_3_1(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2694:2: ( ( (lv_exprs_7_0= ruleExpression ) ) (otherlv_8= ',' ( (lv_exprs_9_0= ruleExpression ) ) )* )?
                    int alt47=2;
                    int LA47_0 = input.LA(1);

                    if ( ((LA47_0>=RULE_ID && LA47_0<=RULE_BOOLEAN)||LA47_0==51||LA47_0==65||(LA47_0>=70 && LA47_0<=75)||LA47_0==78) ) {
                        alt47=1;
                    }
                    switch (alt47) {
                        case 1 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2694:3: ( (lv_exprs_7_0= ruleExpression ) ) (otherlv_8= ',' ( (lv_exprs_9_0= ruleExpression ) ) )*
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2694:3: ( (lv_exprs_7_0= ruleExpression ) )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2695:1: (lv_exprs_7_0= ruleExpression )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2695:1: (lv_exprs_7_0= ruleExpression )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2696:3: lv_exprs_7_0= ruleExpression
                            {
                            if ( state.backtracking==0 ) {
                               
                              	        newCompositeNode(grammarAccess.getPrimaryExpressionAccess().getExprsExpressionParserRuleCall_3_2_0_0()); 
                              	    
                            }
                            pushFollow(FOLLOW_ruleExpression_in_rulePrimaryExpression5983);
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

                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2712:2: (otherlv_8= ',' ( (lv_exprs_9_0= ruleExpression ) ) )*
                            loop46:
                            do {
                                int alt46=2;
                                int LA46_0 = input.LA(1);

                                if ( (LA46_0==37) ) {
                                    alt46=1;
                                }


                                switch (alt46) {
                            	case 1 :
                            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2712:4: otherlv_8= ',' ( (lv_exprs_9_0= ruleExpression ) )
                            	    {
                            	    otherlv_8=(Token)match(input,37,FOLLOW_37_in_rulePrimaryExpression5996); if (state.failed) return current;
                            	    if ( state.backtracking==0 ) {

                            	          	newLeafNode(otherlv_8, grammarAccess.getPrimaryExpressionAccess().getCommaKeyword_3_2_1_0());
                            	          
                            	    }
                            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2716:1: ( (lv_exprs_9_0= ruleExpression ) )
                            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2717:1: (lv_exprs_9_0= ruleExpression )
                            	    {
                            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2717:1: (lv_exprs_9_0= ruleExpression )
                            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2718:3: lv_exprs_9_0= ruleExpression
                            	    {
                            	    if ( state.backtracking==0 ) {
                            	       
                            	      	        newCompositeNode(grammarAccess.getPrimaryExpressionAccess().getExprsExpressionParserRuleCall_3_2_1_1_0()); 
                            	      	    
                            	    }
                            	    pushFollow(FOLLOW_ruleExpression_in_rulePrimaryExpression6017);
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
                            	    break loop46;
                                }
                            } while (true);


                            }
                            break;

                    }

                    otherlv_10=(Token)match(input,76,FOLLOW_76_in_rulePrimaryExpression6033); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_10, grammarAccess.getPrimaryExpressionAccess().getRightSquareBracketKeyword_3_3());
                          
                    }

                    }


                    }
                    break;
                case 5 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2739:6: (otherlv_11= '{' () ( (lv_left_13_0= ruleExpression ) ) ( (lv_op_14_0= ',' ) ) ( (lv_right_15_0= ruleExpression ) ) (otherlv_16= ',' ( (lv_z_17_0= ruleExpression ) ) )? otherlv_18= '}' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2739:6: (otherlv_11= '{' () ( (lv_left_13_0= ruleExpression ) ) ( (lv_op_14_0= ',' ) ) ( (lv_right_15_0= ruleExpression ) ) (otherlv_16= ',' ( (lv_z_17_0= ruleExpression ) ) )? otherlv_18= '}' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2739:8: otherlv_11= '{' () ( (lv_left_13_0= ruleExpression ) ) ( (lv_op_14_0= ',' ) ) ( (lv_right_15_0= ruleExpression ) ) (otherlv_16= ',' ( (lv_z_17_0= ruleExpression ) ) )? otherlv_18= '}'
                    {
                    otherlv_11=(Token)match(input,51,FOLLOW_51_in_rulePrimaryExpression6053); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_11, grammarAccess.getPrimaryExpressionAccess().getLeftCurlyBracketKeyword_4_0());
                          
                    }
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2743:1: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2744:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getPrimaryExpressionAccess().getPointAction_4_1(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2749:2: ( (lv_left_13_0= ruleExpression ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2750:1: (lv_left_13_0= ruleExpression )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2750:1: (lv_left_13_0= ruleExpression )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2751:3: lv_left_13_0= ruleExpression
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getPrimaryExpressionAccess().getLeftExpressionParserRuleCall_4_2_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleExpression_in_rulePrimaryExpression6083);
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

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2767:2: ( (lv_op_14_0= ',' ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2768:1: (lv_op_14_0= ',' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2768:1: (lv_op_14_0= ',' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2769:3: lv_op_14_0= ','
                    {
                    lv_op_14_0=(Token)match(input,37,FOLLOW_37_in_rulePrimaryExpression6101); if (state.failed) return current;
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

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2782:2: ( (lv_right_15_0= ruleExpression ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2783:1: (lv_right_15_0= ruleExpression )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2783:1: (lv_right_15_0= ruleExpression )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2784:3: lv_right_15_0= ruleExpression
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getPrimaryExpressionAccess().getRightExpressionParserRuleCall_4_4_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleExpression_in_rulePrimaryExpression6135);
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

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2800:2: (otherlv_16= ',' ( (lv_z_17_0= ruleExpression ) ) )?
                    int alt48=2;
                    int LA48_0 = input.LA(1);

                    if ( (LA48_0==37) ) {
                        alt48=1;
                    }
                    switch (alt48) {
                        case 1 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2800:4: otherlv_16= ',' ( (lv_z_17_0= ruleExpression ) )
                            {
                            otherlv_16=(Token)match(input,37,FOLLOW_37_in_rulePrimaryExpression6148); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                                  	newLeafNode(otherlv_16, grammarAccess.getPrimaryExpressionAccess().getCommaKeyword_4_5_0());
                                  
                            }
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2804:1: ( (lv_z_17_0= ruleExpression ) )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2805:1: (lv_z_17_0= ruleExpression )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2805:1: (lv_z_17_0= ruleExpression )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2806:3: lv_z_17_0= ruleExpression
                            {
                            if ( state.backtracking==0 ) {
                               
                              	        newCompositeNode(grammarAccess.getPrimaryExpressionAccess().getZExpressionParserRuleCall_4_5_1_0()); 
                              	    
                            }
                            pushFollow(FOLLOW_ruleExpression_in_rulePrimaryExpression6169);
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

                    otherlv_18=(Token)match(input,52,FOLLOW_52_in_rulePrimaryExpression6183); if (state.failed) return current;
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2834:1: entryRuleAbstractRef returns [EObject current=null] : iv_ruleAbstractRef= ruleAbstractRef EOF ;
    public final EObject entryRuleAbstractRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAbstractRef = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2835:2: (iv_ruleAbstractRef= ruleAbstractRef EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2836:2: iv_ruleAbstractRef= ruleAbstractRef EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAbstractRefRule()); 
            }
            pushFollow(FOLLOW_ruleAbstractRef_in_entryRuleAbstractRef6220);
            iv_ruleAbstractRef=ruleAbstractRef();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAbstractRef; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleAbstractRef6230); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2843:1: ruleAbstractRef returns [EObject current=null] : (this_VariableRef_0= ruleVariableRef | this_Function_1= ruleFunction ) ;
    public final EObject ruleAbstractRef() throws RecognitionException {
        EObject current = null;

        EObject this_VariableRef_0 = null;

        EObject this_Function_1 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2846:28: ( (this_VariableRef_0= ruleVariableRef | this_Function_1= ruleFunction ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2847:1: (this_VariableRef_0= ruleVariableRef | this_Function_1= ruleFunction )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2847:1: (this_VariableRef_0= ruleVariableRef | this_Function_1= ruleFunction )
            int alt50=2;
            int LA50_0 = input.LA(1);

            if ( (LA50_0==RULE_ID) ) {
                int LA50_1 = input.LA(2);

                if ( (LA50_1==78) ) {
                    alt50=2;
                }
                else if ( (LA50_1==EOF||LA50_1==RULE_ID||(LA50_1>=35 && LA50_1<=38)||(LA50_1>=40 && LA50_1<=70)||(LA50_1>=75 && LA50_1<=77)||LA50_1==79) ) {
                    alt50=1;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return current;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 50, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 50, 0, input);

                throw nvae;
            }
            switch (alt50) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2848:5: this_VariableRef_0= ruleVariableRef
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getAbstractRefAccess().getVariableRefParserRuleCall_0()); 
                          
                    }
                    pushFollow(FOLLOW_ruleVariableRef_in_ruleAbstractRef6277);
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2858:5: this_Function_1= ruleFunction
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getAbstractRefAccess().getFunctionParserRuleCall_1()); 
                          
                    }
                    pushFollow(FOLLOW_ruleFunction_in_ruleAbstractRef6304);
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2874:1: entryRuleFunction returns [EObject current=null] : iv_ruleFunction= ruleFunction EOF ;
    public final EObject entryRuleFunction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleFunction = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2875:2: (iv_ruleFunction= ruleFunction EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2876:2: iv_ruleFunction= ruleFunction EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getFunctionRule()); 
            }
            pushFollow(FOLLOW_ruleFunction_in_entryRuleFunction6339);
            iv_ruleFunction=ruleFunction();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleFunction; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleFunction6349); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2883:1: ruleFunction returns [EObject current=null] : ( () ( ( (lv_op_1_0= RULE_ID ) ) otherlv_2= '(' ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ')' ) ) ;
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
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2886:28: ( ( () ( ( (lv_op_1_0= RULE_ID ) ) otherlv_2= '(' ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ')' ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2887:1: ( () ( ( (lv_op_1_0= RULE_ID ) ) otherlv_2= '(' ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ')' ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2887:1: ( () ( ( (lv_op_1_0= RULE_ID ) ) otherlv_2= '(' ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ')' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2887:2: () ( ( (lv_op_1_0= RULE_ID ) ) otherlv_2= '(' ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ')' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2887:2: ()
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2888:5: 
            {
            if ( state.backtracking==0 ) {

                      current = forceCreateModelElement(
                          grammarAccess.getFunctionAccess().getFunctionAction_0(),
                          current);
                  
            }

            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2893:2: ( ( (lv_op_1_0= RULE_ID ) ) otherlv_2= '(' ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ')' )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2893:3: ( (lv_op_1_0= RULE_ID ) ) otherlv_2= '(' ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ')'
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2893:3: ( (lv_op_1_0= RULE_ID ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2894:1: (lv_op_1_0= RULE_ID )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2894:1: (lv_op_1_0= RULE_ID )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2895:3: lv_op_1_0= RULE_ID
            {
            lv_op_1_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleFunction6401); if (state.failed) return current;
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

            otherlv_2=(Token)match(input,78,FOLLOW_78_in_ruleFunction6418); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_2, grammarAccess.getFunctionAccess().getLeftParenthesisKeyword_1_1());
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2915:1: ( (lv_args_3_0= ruleExpression ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2916:1: (lv_args_3_0= ruleExpression )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2916:1: (lv_args_3_0= ruleExpression )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2917:3: lv_args_3_0= ruleExpression
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getFunctionAccess().getArgsExpressionParserRuleCall_1_2_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleExpression_in_ruleFunction6439);
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

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2933:2: (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )*
            loop51:
            do {
                int alt51=2;
                int LA51_0 = input.LA(1);

                if ( (LA51_0==37) ) {
                    alt51=1;
                }


                switch (alt51) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2933:4: otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) )
            	    {
            	    otherlv_4=(Token)match(input,37,FOLLOW_37_in_ruleFunction6452); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	          	newLeafNode(otherlv_4, grammarAccess.getFunctionAccess().getCommaKeyword_1_3_0());
            	          
            	    }
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2937:1: ( (lv_args_5_0= ruleExpression ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2938:1: (lv_args_5_0= ruleExpression )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2938:1: (lv_args_5_0= ruleExpression )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2939:3: lv_args_5_0= ruleExpression
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getFunctionAccess().getArgsExpressionParserRuleCall_1_3_1_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleExpression_in_ruleFunction6473);
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
            	    break loop51;
                }
            } while (true);

            otherlv_6=(Token)match(input,79,FOLLOW_79_in_ruleFunction6487); if (state.failed) return current;
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2967:1: entryRuleUnitName returns [EObject current=null] : iv_ruleUnitName= ruleUnitName EOF ;
    public final EObject entryRuleUnitName() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleUnitName = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2968:2: (iv_ruleUnitName= ruleUnitName EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2969:2: iv_ruleUnitName= ruleUnitName EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getUnitNameRule()); 
            }
            pushFollow(FOLLOW_ruleUnitName_in_entryRuleUnitName6524);
            iv_ruleUnitName=ruleUnitName();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleUnitName; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleUnitName6534); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2976:1: ruleUnitName returns [EObject current=null] : ( () ( (lv_op_1_0= RULE_ID ) ) ) ;
    public final EObject ruleUnitName() throws RecognitionException {
        EObject current = null;

        Token lv_op_1_0=null;

         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2979:28: ( ( () ( (lv_op_1_0= RULE_ID ) ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2980:1: ( () ( (lv_op_1_0= RULE_ID ) ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2980:1: ( () ( (lv_op_1_0= RULE_ID ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2980:2: () ( (lv_op_1_0= RULE_ID ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2980:2: ()
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2981:5: 
            {
            if ( state.backtracking==0 ) {

                      current = forceCreateModelElement(
                          grammarAccess.getUnitNameAccess().getUnitNameAction_0(),
                          current);
                  
            }

            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2986:2: ( (lv_op_1_0= RULE_ID ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2987:1: (lv_op_1_0= RULE_ID )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2987:1: (lv_op_1_0= RULE_ID )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2988:3: lv_op_1_0= RULE_ID
            {
            lv_op_1_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleUnitName6585); if (state.failed) return current;
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3012:1: entryRuleVariableRef returns [EObject current=null] : iv_ruleVariableRef= ruleVariableRef EOF ;
    public final EObject entryRuleVariableRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleVariableRef = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3013:2: (iv_ruleVariableRef= ruleVariableRef EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3014:2: iv_ruleVariableRef= ruleVariableRef EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getVariableRefRule()); 
            }
            pushFollow(FOLLOW_ruleVariableRef_in_entryRuleVariableRef6626);
            iv_ruleVariableRef=ruleVariableRef();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleVariableRef; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleVariableRef6636); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3021:1: ruleVariableRef returns [EObject current=null] : ( () ( (otherlv_1= RULE_ID ) ) ) ;
    public final EObject ruleVariableRef() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;

         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3024:28: ( ( () ( (otherlv_1= RULE_ID ) ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3025:1: ( () ( (otherlv_1= RULE_ID ) ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3025:1: ( () ( (otherlv_1= RULE_ID ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3025:2: () ( (otherlv_1= RULE_ID ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3025:2: ()
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3026:5: 
            {
            if ( state.backtracking==0 ) {

                      current = forceCreateModelElement(
                          grammarAccess.getVariableRefAccess().getVariableRefAction_0(),
                          current);
                  
            }

            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3031:2: ( (otherlv_1= RULE_ID ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3032:1: (otherlv_1= RULE_ID )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3032:1: (otherlv_1= RULE_ID )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3033:3: otherlv_1= RULE_ID
            {
            if ( state.backtracking==0 ) {

              			if (current==null) {
              	            current = createModelElement(grammarAccess.getVariableRefRule());
              	        }
                      
            }
            otherlv_1=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleVariableRef6690); if (state.failed) return current;
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3054:1: entryRuleTerminalExpression returns [EObject current=null] : iv_ruleTerminalExpression= ruleTerminalExpression EOF ;
    public final EObject entryRuleTerminalExpression() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTerminalExpression = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3055:2: (iv_ruleTerminalExpression= ruleTerminalExpression EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3056:2: iv_ruleTerminalExpression= ruleTerminalExpression EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getTerminalExpressionRule()); 
            }
            pushFollow(FOLLOW_ruleTerminalExpression_in_entryRuleTerminalExpression6728);
            iv_ruleTerminalExpression=ruleTerminalExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleTerminalExpression; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleTerminalExpression6738); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3063:1: ruleTerminalExpression returns [EObject current=null] : ( ( () ( (lv_value_1_0= RULE_INTEGER ) ) ) | ( () ( (lv_value_3_0= RULE_DOUBLE ) ) ) | ( () ( (lv_value_5_0= RULE_COLOR ) ) ) | ( () ( (lv_value_7_0= RULE_STRING ) ) ) | ( () ( (lv_value_9_0= RULE_BOOLEAN ) ) ) ) ;
    public final EObject ruleTerminalExpression() throws RecognitionException {
        EObject current = null;

        Token lv_value_1_0=null;
        Token lv_value_3_0=null;
        Token lv_value_5_0=null;
        Token lv_value_7_0=null;
        Token lv_value_9_0=null;

         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3066:28: ( ( ( () ( (lv_value_1_0= RULE_INTEGER ) ) ) | ( () ( (lv_value_3_0= RULE_DOUBLE ) ) ) | ( () ( (lv_value_5_0= RULE_COLOR ) ) ) | ( () ( (lv_value_7_0= RULE_STRING ) ) ) | ( () ( (lv_value_9_0= RULE_BOOLEAN ) ) ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3067:1: ( ( () ( (lv_value_1_0= RULE_INTEGER ) ) ) | ( () ( (lv_value_3_0= RULE_DOUBLE ) ) ) | ( () ( (lv_value_5_0= RULE_COLOR ) ) ) | ( () ( (lv_value_7_0= RULE_STRING ) ) ) | ( () ( (lv_value_9_0= RULE_BOOLEAN ) ) ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3067:1: ( ( () ( (lv_value_1_0= RULE_INTEGER ) ) ) | ( () ( (lv_value_3_0= RULE_DOUBLE ) ) ) | ( () ( (lv_value_5_0= RULE_COLOR ) ) ) | ( () ( (lv_value_7_0= RULE_STRING ) ) ) | ( () ( (lv_value_9_0= RULE_BOOLEAN ) ) ) )
            int alt52=5;
            switch ( input.LA(1) ) {
            case RULE_INTEGER:
                {
                alt52=1;
                }
                break;
            case RULE_DOUBLE:
                {
                alt52=2;
                }
                break;
            case RULE_COLOR:
                {
                alt52=3;
                }
                break;
            case RULE_STRING:
                {
                alt52=4;
                }
                break;
            case RULE_BOOLEAN:
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3067:2: ( () ( (lv_value_1_0= RULE_INTEGER ) ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3067:2: ( () ( (lv_value_1_0= RULE_INTEGER ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3067:3: () ( (lv_value_1_0= RULE_INTEGER ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3067:3: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3068:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getTerminalExpressionAccess().getIntLiteralAction_0_0(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3073:2: ( (lv_value_1_0= RULE_INTEGER ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3074:1: (lv_value_1_0= RULE_INTEGER )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3074:1: (lv_value_1_0= RULE_INTEGER )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3075:3: lv_value_1_0= RULE_INTEGER
                    {
                    lv_value_1_0=(Token)match(input,RULE_INTEGER,FOLLOW_RULE_INTEGER_in_ruleTerminalExpression6790); if (state.failed) return current;
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3092:6: ( () ( (lv_value_3_0= RULE_DOUBLE ) ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3092:6: ( () ( (lv_value_3_0= RULE_DOUBLE ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3092:7: () ( (lv_value_3_0= RULE_DOUBLE ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3092:7: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3093:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getTerminalExpressionAccess().getDoubleLiteralAction_1_0(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3098:2: ( (lv_value_3_0= RULE_DOUBLE ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3099:1: (lv_value_3_0= RULE_DOUBLE )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3099:1: (lv_value_3_0= RULE_DOUBLE )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3100:3: lv_value_3_0= RULE_DOUBLE
                    {
                    lv_value_3_0=(Token)match(input,RULE_DOUBLE,FOLLOW_RULE_DOUBLE_in_ruleTerminalExpression6829); if (state.failed) return current;
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3117:6: ( () ( (lv_value_5_0= RULE_COLOR ) ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3117:6: ( () ( (lv_value_5_0= RULE_COLOR ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3117:7: () ( (lv_value_5_0= RULE_COLOR ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3117:7: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3118:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getTerminalExpressionAccess().getColorLiteralAction_2_0(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3123:2: ( (lv_value_5_0= RULE_COLOR ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3124:1: (lv_value_5_0= RULE_COLOR )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3124:1: (lv_value_5_0= RULE_COLOR )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3125:3: lv_value_5_0= RULE_COLOR
                    {
                    lv_value_5_0=(Token)match(input,RULE_COLOR,FOLLOW_RULE_COLOR_in_ruleTerminalExpression6868); if (state.failed) return current;
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3142:6: ( () ( (lv_value_7_0= RULE_STRING ) ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3142:6: ( () ( (lv_value_7_0= RULE_STRING ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3142:7: () ( (lv_value_7_0= RULE_STRING ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3142:7: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3143:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getTerminalExpressionAccess().getStringLiteralAction_3_0(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3148:2: ( (lv_value_7_0= RULE_STRING ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3149:1: (lv_value_7_0= RULE_STRING )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3149:1: (lv_value_7_0= RULE_STRING )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3150:3: lv_value_7_0= RULE_STRING
                    {
                    lv_value_7_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleTerminalExpression6907); if (state.failed) return current;
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3167:6: ( () ( (lv_value_9_0= RULE_BOOLEAN ) ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3167:6: ( () ( (lv_value_9_0= RULE_BOOLEAN ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3167:7: () ( (lv_value_9_0= RULE_BOOLEAN ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3167:7: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3168:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getTerminalExpressionAccess().getBooleanLiteralAction_4_0(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3173:2: ( (lv_value_9_0= RULE_BOOLEAN ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3174:1: (lv_value_9_0= RULE_BOOLEAN )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3174:1: (lv_value_9_0= RULE_BOOLEAN )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3175:3: lv_value_9_0= RULE_BOOLEAN
                    {
                    lv_value_9_0=(Token)match(input,RULE_BOOLEAN,FOLLOW_RULE_BOOLEAN_in_ruleTerminalExpression6946); if (state.failed) return current;
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
        // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:334:3: ( ruleAssignmentStatement )
        // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:334:5: ruleAssignmentStatement
        {
        pushFollow(FOLLOW_ruleAssignmentStatement_in_synpred1_InternalGaml751);
        ruleAssignmentStatement();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_InternalGaml

    // $ANTLR start synpred2_InternalGaml
    public final void synpred2_InternalGaml_fragment() throws RecognitionException {   
        // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:459:4: ( 'else' )
        // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:459:6: 'else'
        {
        match(input,34,FOLLOW_34_in_synpred2_InternalGaml1049); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred2_InternalGaml

    // $ANTLR start synpred3_InternalGaml
    public final void synpred3_InternalGaml_fragment() throws RecognitionException {   
        // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1917:3: ( ruleArgPairExpr )
        // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1917:5: ruleArgPairExpr
        {
        pushFollow(FOLLOW_ruleArgPairExpr_in_synpred3_InternalGaml4194);
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
    protected DFA32 dfa32 = new DFA32(this);
    static final String DFA5_eotS =
        "\42\uffff";
    static final String DFA5_eofS =
        "\42\uffff";
    static final String DFA5_minS =
        "\1\4\1\0\40\uffff";
    static final String DFA5_maxS =
        "\1\116\1\0\40\uffff";
    static final String DFA5_acceptS =
        "\2\uffff\16\1\1\2\21\uffff";
    static final String DFA5_specialS =
        "\1\0\1\1\40\uffff}>";
    static final String[] DFA5_transitionS = {
            "\1\1\1\5\1\2\1\3\1\4\1\6\6\uffff\21\20\6\uffff\1\20\13\uffff"+
            "\1\11\15\uffff\1\13\4\uffff\1\12\1\14\1\15\1\16\1\17\1\10\2"+
            "\uffff\1\7",
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
            return "334:1: ( ( ( ruleAssignmentStatement )=>this_AssignmentStatement_0= ruleAssignmentStatement ) | (this_ReturnStatement_1= ruleReturnStatement | this_IfStatement_2= ruleIfStatement | this_ClassicStatement_3= ruleClassicStatement | this_DefinitionStatement_4= ruleDefinitionStatement ) )";
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

                        else if ( (LA5_0==RULE_INTEGER) && (synpred1_InternalGaml())) {s = 2;}

                        else if ( (LA5_0==RULE_DOUBLE) && (synpred1_InternalGaml())) {s = 3;}

                        else if ( (LA5_0==RULE_COLOR) && (synpred1_InternalGaml())) {s = 4;}

                        else if ( (LA5_0==RULE_STRING) && (synpred1_InternalGaml())) {s = 5;}

                        else if ( (LA5_0==RULE_BOOLEAN) && (synpred1_InternalGaml())) {s = 6;}

                        else if ( (LA5_0==78) && (synpred1_InternalGaml())) {s = 7;}

                        else if ( (LA5_0==75) && (synpred1_InternalGaml())) {s = 8;}

                        else if ( (LA5_0==51) && (synpred1_InternalGaml())) {s = 9;}

                        else if ( (LA5_0==70) && (synpred1_InternalGaml())) {s = 10;}

                        else if ( (LA5_0==65) && (synpred1_InternalGaml())) {s = 11;}

                        else if ( (LA5_0==71) && (synpred1_InternalGaml())) {s = 12;}

                        else if ( (LA5_0==72) && (synpred1_InternalGaml())) {s = 13;}

                        else if ( (LA5_0==73) && (synpred1_InternalGaml())) {s = 14;}

                        else if ( (LA5_0==74) && (synpred1_InternalGaml())) {s = 15;}

                        else if ( ((LA5_0>=16 && LA5_0<=32)||LA5_0==39) ) {s = 16;}

                         
                        input.seek(index5_0);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA5_1 = input.LA(1);

                         
                        int index5_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_InternalGaml()) ) {s = 15;}

                        else if ( (true) ) {s = 16;}

                         
                        input.seek(index5_1);
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
    static final String DFA32_eotS =
        "\21\uffff";
    static final String DFA32_eofS =
        "\21\uffff";
    static final String DFA32_minS =
        "\1\4\1\0\17\uffff";
    static final String DFA32_maxS =
        "\1\116\1\0\17\uffff";
    static final String DFA32_acceptS =
        "\2\uffff\1\2\15\uffff\1\1";
    static final String DFA32_specialS =
        "\1\uffff\1\0\17\uffff}>";
    static final String[] DFA32_transitionS = {
            "\1\1\5\2\51\uffff\1\2\15\uffff\1\2\4\uffff\6\2\2\uffff\1\2",
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
            ""
    };

    static final short[] DFA32_eot = DFA.unpackEncodedString(DFA32_eotS);
    static final short[] DFA32_eof = DFA.unpackEncodedString(DFA32_eofS);
    static final char[] DFA32_min = DFA.unpackEncodedStringToUnsignedChars(DFA32_minS);
    static final char[] DFA32_max = DFA.unpackEncodedStringToUnsignedChars(DFA32_maxS);
    static final short[] DFA32_accept = DFA.unpackEncodedString(DFA32_acceptS);
    static final short[] DFA32_special = DFA.unpackEncodedString(DFA32_specialS);
    static final short[][] DFA32_transition;

    static {
        int numStates = DFA32_transitionS.length;
        DFA32_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA32_transition[i] = DFA.unpackEncodedString(DFA32_transitionS[i]);
        }
    }

    class DFA32 extends DFA {

        public DFA32(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 32;
            this.eot = DFA32_eot;
            this.eof = DFA32_eof;
            this.min = DFA32_min;
            this.max = DFA32_max;
            this.accept = DFA32_accept;
            this.special = DFA32_special;
            this.transition = DFA32_transition;
        }
        public String getDescription() {
            return "1917:1: ( ( ( ruleArgPairExpr )=>this_ArgPairExpr_0= ruleArgPairExpr ) | (this_Addition_1= ruleAddition ( ( () ( (lv_op_3_0= '::' ) ) ) ( (lv_right_4_0= ruleAddition ) ) )? ) )";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA32_1 = input.LA(1);

                         
                        int index32_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred3_InternalGaml()) ) {s = 16;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index32_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 32, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_ruleModel_in_entryRuleModel75 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleModel85 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_14_in_ruleModel122 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleModel139 = new BitSet(new long[]{0x00080081FFFF83F2L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_ruleImport_in_ruleModel165 = new BitSet(new long[]{0x00080081FFFF83F2L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_ruleStatement_in_ruleModel187 = new BitSet(new long[]{0x00080081FFFF03F2L,0x0000000000004FC2L});
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
    public static final BitSet FOLLOW_ruleStatement_in_entryRuleStatement704 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleStatement714 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAssignmentStatement_in_ruleStatement767 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleReturnStatement_in_ruleStatement796 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleIfStatement_in_ruleStatement823 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleClassicStatement_in_ruleStatement850 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDefinitionStatement_in_ruleStatement877 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleIfStatement_in_entryRuleIfStatement913 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleIfStatement923 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_ruleIfStatement966 = new BitSet(new long[]{0x00080002000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_33_in_ruleIfStatement992 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_ruleExpression_in_ruleIfStatement1015 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_ruleBlock_in_ruleIfStatement1036 = new BitSet(new long[]{0x0000000400000002L});
    public static final BitSet FOLLOW_34_in_ruleIfStatement1057 = new BitSet(new long[]{0x0008000100000000L});
    public static final BitSet FOLLOW_ruleIfStatement_in_ruleIfStatement1081 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleBlock_in_ruleIfStatement1100 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleClassicStatement_in_entryRuleClassicStatement1141 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleClassicStatement1151 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleBuiltInStatementKey_in_ruleClassicStatement1197 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_ruleExpression_in_ruleClassicStatement1218 = new BitSet(new long[]{0x00EE010800000010L});
    public static final BitSet FOLLOW_ruleFacet_in_ruleClassicStatement1239 = new BitSet(new long[]{0x00EE010800000010L});
    public static final BitSet FOLLOW_ruleBlock_in_ruleClassicStatement1262 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_ruleClassicStatement1280 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDefinitionStatement_in_entryRuleDefinitionStatement1317 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDefinitionStatement1327 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleDefinitionStatement1369 = new BitSet(new long[]{0x00EE0118FFFF0030L});
    public static final BitSet FOLLOW_ruleContents_in_ruleDefinitionStatement1395 = new BitSet(new long[]{0x00EE0108FFFF0030L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleDefinitionStatement1415 = new BitSet(new long[]{0x00EE010800000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleDefinitionStatement1435 = new BitSet(new long[]{0x00EE010800000010L});
    public static final BitSet FOLLOW_ruleBuiltInStatementKey_in_ruleDefinitionStatement1459 = new BitSet(new long[]{0x00EE010800000010L});
    public static final BitSet FOLLOW_ruleFacet_in_ruleDefinitionStatement1484 = new BitSet(new long[]{0x00EE010800000010L});
    public static final BitSet FOLLOW_ruleBlock_in_ruleDefinitionStatement1507 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_ruleDefinitionStatement1525 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleContents_in_entryRuleContents1562 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleContents1572 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_ruleContents1609 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleContents1626 = new BitSet(new long[]{0x0000006000000000L});
    public static final BitSet FOLLOW_37_in_ruleContents1644 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleContents1661 = new BitSet(new long[]{0x0000004000000000L});
    public static final BitSet FOLLOW_38_in_ruleContents1680 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleReturnStatement_in_entryRuleReturnStatement1716 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleReturnStatement1726 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_39_in_ruleReturnStatement1769 = new BitSet(new long[]{0x00080008000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_ruleExpression_in_ruleReturnStatement1803 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_35_in_ruleReturnStatement1816 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAssignmentStatement_in_entryRuleAssignmentStatement1852 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAssignmentStatement1862 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleExpression_in_ruleAssignmentStatement1908 = new BitSet(new long[]{0x0000FF0000000000L});
    public static final BitSet FOLLOW_40_in_ruleAssignmentStatement1928 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_41_in_ruleAssignmentStatement1957 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_42_in_ruleAssignmentStatement1986 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_43_in_ruleAssignmentStatement2015 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_44_in_ruleAssignmentStatement2044 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_45_in_ruleAssignmentStatement2073 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_46_in_ruleAssignmentStatement2102 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_47_in_ruleAssignmentStatement2131 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_ruleExpression_in_ruleAssignmentStatement2168 = new BitSet(new long[]{0x00E6010800000010L});
    public static final BitSet FOLLOW_ruleFacet_in_ruleAssignmentStatement2189 = new BitSet(new long[]{0x00E6010800000010L});
    public static final BitSet FOLLOW_35_in_ruleAssignmentStatement2202 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFacet_in_entryRuleFacet2238 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleFacet2248 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFunctionFacet_in_ruleFacet2295 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDefinitionFacet_in_ruleFacet2322 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleClassicFacet_in_ruleFacet2349 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleClassicFacet_in_entryRuleClassicFacet2384 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleClassicFacet2394 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleClassicFacet2438 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_48_in_ruleClassicFacet2455 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_40_in_ruleClassicFacet2480 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_ruleExpression_in_ruleClassicFacet2515 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFunctionFacet_in_entryRuleFunctionFacet2551 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleFunctionFacet2561 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_ruleFunctionFacet2605 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_50_in_ruleFunctionFacet2642 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_51_in_ruleFunctionFacet2668 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_ruleExpression_in_ruleFunctionFacet2689 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_52_in_ruleFunctionFacet2701 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDefinitionFacet_in_entryRuleDefinitionFacet2737 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDefinitionFacet2747 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_53_in_ruleDefinitionFacet2792 = new BitSet(new long[]{0x00000000FFFF0030L});
    public static final BitSet FOLLOW_54_in_ruleDefinitionFacet2821 = new BitSet(new long[]{0x00000000FFFF0030L});
    public static final BitSet FOLLOW_55_in_ruleDefinitionFacet2850 = new BitSet(new long[]{0x00000000FFFF0030L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleDefinitionFacet2885 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleDefinitionFacet2905 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleBuiltInStatementKey_in_ruleDefinitionFacet2929 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleBlock_in_entryRuleBlock2968 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleBlock2978 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_ruleBlock3024 = new BitSet(new long[]{0x00180081FFFF03F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_ruleStatement_in_ruleBlock3045 = new BitSet(new long[]{0x00180081FFFF03F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_52_in_ruleBlock3058 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleExpression_in_entryRuleExpression3094 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleExpression3104 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTernExp_in_ruleExpression3150 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTernExp_in_entryRuleTernExp3184 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTernExp3194 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOrExp_in_ruleTernExp3241 = new BitSet(new long[]{0x0100000000000002L});
    public static final BitSet FOLLOW_56_in_ruleTernExp3268 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_ruleOrExp_in_ruleTernExp3302 = new BitSet(new long[]{0x0001000000000000L});
    public static final BitSet FOLLOW_48_in_ruleTernExp3314 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_ruleOrExp_in_ruleTernExp3335 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOrExp_in_entryRuleOrExp3373 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOrExp3383 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAndExp_in_ruleOrExp3430 = new BitSet(new long[]{0x0200000000000002L});
    public static final BitSet FOLLOW_57_in_ruleOrExp3457 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_ruleAndExp_in_ruleOrExp3491 = new BitSet(new long[]{0x0200000000000002L});
    public static final BitSet FOLLOW_ruleAndExp_in_entryRuleAndExp3529 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAndExp3539 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelational_in_ruleAndExp3586 = new BitSet(new long[]{0x0400000000000002L});
    public static final BitSet FOLLOW_58_in_ruleAndExp3613 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_ruleRelational_in_ruleAndExp3647 = new BitSet(new long[]{0x0400000000000002L});
    public static final BitSet FOLLOW_ruleRelational_in_entryRuleRelational3685 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRelational3695 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePairExpr_in_ruleRelational3742 = new BitSet(new long[]{0x7800005000000002L});
    public static final BitSet FOLLOW_59_in_ruleRelational3772 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_60_in_ruleRelational3801 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_61_in_ruleRelational3830 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_62_in_ruleRelational3859 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_36_in_ruleRelational3888 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_38_in_ruleRelational3917 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_rulePairExpr_in_ruleRelational3955 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArgPairExpr_in_entryRuleArgPairExpr3993 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleArgPairExpr4003 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleArgPairExpr4054 = new BitSet(new long[]{0x8000000000000000L});
    public static final BitSet FOLLOW_63_in_ruleArgPairExpr4077 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_ruleAddition_in_ruleArgPairExpr4111 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePairExpr_in_entryRulePairExpr4147 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRulePairExpr4157 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArgPairExpr_in_rulePairExpr4210 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAddition_in_rulePairExpr4239 = new BitSet(new long[]{0x8000000000000002L});
    public static final BitSet FOLLOW_63_in_rulePairExpr4267 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_ruleAddition_in_rulePairExpr4302 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAddition_in_entryRuleAddition4341 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAddition4351 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleMultiplication_in_ruleAddition4398 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000003L});
    public static final BitSet FOLLOW_64_in_ruleAddition4428 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_65_in_ruleAddition4457 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_ruleMultiplication_in_ruleAddition4495 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000003L});
    public static final BitSet FOLLOW_ruleMultiplication_in_entryRuleMultiplication4533 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleMultiplication4543 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleGamlBinaryExpr_in_ruleMultiplication4590 = new BitSet(new long[]{0x0000000000000002L,0x000000000000001CL});
    public static final BitSet FOLLOW_66_in_ruleMultiplication4620 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_67_in_ruleMultiplication4649 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_68_in_ruleMultiplication4678 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_ruleGamlBinaryExpr_in_ruleMultiplication4716 = new BitSet(new long[]{0x0000000000000002L,0x000000000000001CL});
    public static final BitSet FOLLOW_ruleGamlBinaryExpr_in_entryRuleGamlBinaryExpr4754 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleGamlBinaryExpr4764 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleGamlUnitExpr_in_ruleGamlBinaryExpr4811 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleGamlBinaryExpr4838 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_ruleGamlUnitExpr_in_ruleGamlBinaryExpr4865 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_ruleGamlUnitExpr_in_entryRuleGamlUnitExpr4903 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleGamlUnitExpr4913 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleGamlUnaryExpr_in_ruleGamlUnitExpr4960 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000060L});
    public static final BitSet FOLLOW_69_in_ruleGamlUnitExpr4990 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_70_in_ruleGamlUnitExpr5019 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleUnitName_in_ruleGamlUnitExpr5057 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleGamlUnaryExpr_in_entryRuleGamlUnaryExpr5095 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleGamlUnaryExpr5105 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAccess_in_ruleGamlUnaryExpr5152 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_70_in_ruleGamlUnaryExpr5197 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleUnitName_in_ruleGamlUnaryExpr5231 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_ruleGamlUnaryExpr5260 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_71_in_ruleGamlUnaryExpr5289 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_72_in_ruleGamlUnaryExpr5318 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_73_in_ruleGamlUnaryExpr5347 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_74_in_ruleGamlUnaryExpr5376 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_ruleGamlUnaryExpr_in_ruleGamlUnaryExpr5413 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAccess_in_entryRuleAccess5452 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAccess5462 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleMemberRef_in_ruleAccess5509 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000800L});
    public static final BitSet FOLLOW_75_in_ruleAccess5531 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_ruleExpression_in_ruleAccess5553 = new BitSet(new long[]{0x0000002000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_37_in_ruleAccess5566 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_ruleExpression_in_ruleAccess5587 = new BitSet(new long[]{0x0000002000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_76_in_ruleAccess5601 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000800L});
    public static final BitSet FOLLOW_ruleMemberRef_in_entryRuleMemberRef5639 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleMemberRef5649 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePrimaryExpression_in_ruleMemberRef5696 = new BitSet(new long[]{0x0000000000000002L,0x0000000000002000L});
    public static final BitSet FOLLOW_77_in_ruleMemberRef5724 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004800L});
    public static final BitSet FOLLOW_rulePrimaryExpression_in_ruleMemberRef5758 = new BitSet(new long[]{0x0000000000000002L,0x0000000000002000L});
    public static final BitSet FOLLOW_rulePrimaryExpression_in_entryRulePrimaryExpression5797 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRulePrimaryExpression5807 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTerminalExpression_in_rulePrimaryExpression5854 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAbstractRef_in_rulePrimaryExpression5881 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_78_in_rulePrimaryExpression5899 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_ruleExpression_in_rulePrimaryExpression5921 = new BitSet(new long[]{0x0000000000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_79_in_rulePrimaryExpression5932 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_75_in_rulePrimaryExpression5952 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000005FC2L});
    public static final BitSet FOLLOW_ruleExpression_in_rulePrimaryExpression5983 = new BitSet(new long[]{0x0000002000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_37_in_rulePrimaryExpression5996 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_ruleExpression_in_rulePrimaryExpression6017 = new BitSet(new long[]{0x0000002000000000L,0x0000000000001000L});
    public static final BitSet FOLLOW_76_in_rulePrimaryExpression6033 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_51_in_rulePrimaryExpression6053 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_ruleExpression_in_rulePrimaryExpression6083 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_rulePrimaryExpression6101 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_ruleExpression_in_rulePrimaryExpression6135 = new BitSet(new long[]{0x0010002000000000L});
    public static final BitSet FOLLOW_37_in_rulePrimaryExpression6148 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_ruleExpression_in_rulePrimaryExpression6169 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_52_in_rulePrimaryExpression6183 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAbstractRef_in_entryRuleAbstractRef6220 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAbstractRef6230 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleVariableRef_in_ruleAbstractRef6277 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFunction_in_ruleAbstractRef6304 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFunction_in_entryRuleFunction6339 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleFunction6349 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleFunction6401 = new BitSet(new long[]{0x0000000000000000L,0x0000000000004000L});
    public static final BitSet FOLLOW_78_in_ruleFunction6418 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_ruleExpression_in_ruleFunction6439 = new BitSet(new long[]{0x0000002000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_37_in_ruleFunction6452 = new BitSet(new long[]{0x00080000000003F0L,0x0000000000004FC2L});
    public static final BitSet FOLLOW_ruleExpression_in_ruleFunction6473 = new BitSet(new long[]{0x0000002000000000L,0x0000000000008000L});
    public static final BitSet FOLLOW_79_in_ruleFunction6487 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUnitName_in_entryRuleUnitName6524 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleUnitName6534 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleUnitName6585 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleVariableRef_in_entryRuleVariableRef6626 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleVariableRef6636 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleVariableRef6690 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTerminalExpression_in_entryRuleTerminalExpression6728 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTerminalExpression6738 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_INTEGER_in_ruleTerminalExpression6790 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DOUBLE_in_ruleTerminalExpression6829 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_COLOR_in_ruleTerminalExpression6868 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleTerminalExpression6907 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_BOOLEAN_in_ruleTerminalExpression6946 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAssignmentStatement_in_synpred1_InternalGaml751 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_synpred2_InternalGaml1049 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArgPairExpr_in_synpred3_InternalGaml4194 = new BitSet(new long[]{0x0000000000000002L});

}