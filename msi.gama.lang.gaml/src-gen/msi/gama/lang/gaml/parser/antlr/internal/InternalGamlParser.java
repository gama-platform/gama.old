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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "RULE_ID", "RULE_STRING", "RULE_INTEGER", "RULE_DOUBLE", "RULE_COLOR", "RULE_BOOLEAN", "RULE_ML_COMMENT", "RULE_SL_COMMENT", "RULE_WS", "RULE_ANY_OTHER", "'model'", "'_gaml {'", "'}'", "'import'", "'_binary &'", "'&;'", "'_reserved &'", "'_unary &'", "'write'", "'warn'", "'error'", "'match'", "'match_one'", "'match_between'", "'capture'", "'release'", "'ask'", "'switch'", "'create'", "'add'", "'remove'", "'put'", "'save'", "'set'", "'return'", "';'", "'if'", "'else'", "':'", "'<-'", "'function'", "'->'", "'name:'", "'returns:'", "'action:'", "'{'", "'?'", "'or'", "'and'", "'!='", "'='", "'>='", "'<='", "'<'", "'>'", "'::'", "'+'", "'-'", "'*'", "'/'", "'^'", "'#'", "'!'", "'my'", "'the'", "'not'", "'.'", "'('", "')'", "'['", "','", "']'"
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
    public static final int T__75=75;
    public static final int T__74=74;
    public static final int T__73=73;
    public static final int RULE_INTEGER=6;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:76:1: ruleModel returns [EObject current=null] : (otherlv_0= 'model' ( (lv_name_1_0= RULE_ID ) ) ( (lv_imports_2_0= ruleImport ) )* ( (otherlv_3= '_gaml {' ( (lv_gaml_4_0= ruleGamlLangDef ) ) otherlv_5= '}' )? ( (lv_statements_6_0= ruleStatement ) )* ) ) ;
    public final EObject ruleModel() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_3=null;
        Token otherlv_5=null;
        EObject lv_imports_2_0 = null;

        EObject lv_gaml_4_0 = null;

        EObject lv_statements_6_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:79:28: ( (otherlv_0= 'model' ( (lv_name_1_0= RULE_ID ) ) ( (lv_imports_2_0= ruleImport ) )* ( (otherlv_3= '_gaml {' ( (lv_gaml_4_0= ruleGamlLangDef ) ) otherlv_5= '}' )? ( (lv_statements_6_0= ruleStatement ) )* ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:80:1: (otherlv_0= 'model' ( (lv_name_1_0= RULE_ID ) ) ( (lv_imports_2_0= ruleImport ) )* ( (otherlv_3= '_gaml {' ( (lv_gaml_4_0= ruleGamlLangDef ) ) otherlv_5= '}' )? ( (lv_statements_6_0= ruleStatement ) )* ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:80:1: (otherlv_0= 'model' ( (lv_name_1_0= RULE_ID ) ) ( (lv_imports_2_0= ruleImport ) )* ( (otherlv_3= '_gaml {' ( (lv_gaml_4_0= ruleGamlLangDef ) ) otherlv_5= '}' )? ( (lv_statements_6_0= ruleStatement ) )* ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:80:3: otherlv_0= 'model' ( (lv_name_1_0= RULE_ID ) ) ( (lv_imports_2_0= ruleImport ) )* ( (otherlv_3= '_gaml {' ( (lv_gaml_4_0= ruleGamlLangDef ) ) otherlv_5= '}' )? ( (lv_statements_6_0= ruleStatement ) )* )
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

                if ( (LA1_0==17) ) {
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

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:120:3: ( (otherlv_3= '_gaml {' ( (lv_gaml_4_0= ruleGamlLangDef ) ) otherlv_5= '}' )? ( (lv_statements_6_0= ruleStatement ) )* )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:120:4: (otherlv_3= '_gaml {' ( (lv_gaml_4_0= ruleGamlLangDef ) ) otherlv_5= '}' )? ( (lv_statements_6_0= ruleStatement ) )*
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:120:4: (otherlv_3= '_gaml {' ( (lv_gaml_4_0= ruleGamlLangDef ) ) otherlv_5= '}' )?
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==15) ) {
                alt2=1;
            }
            switch (alt2) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:120:6: otherlv_3= '_gaml {' ( (lv_gaml_4_0= ruleGamlLangDef ) ) otherlv_5= '}'
                    {
                    otherlv_3=(Token)match(input,15,FOLLOW_15_in_ruleModel180); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_3, grammarAccess.getModelAccess().get_gamlKeyword_3_0_0());
                          
                    }
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:124:1: ( (lv_gaml_4_0= ruleGamlLangDef ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:125:1: (lv_gaml_4_0= ruleGamlLangDef )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:125:1: (lv_gaml_4_0= ruleGamlLangDef )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:126:3: lv_gaml_4_0= ruleGamlLangDef
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getModelAccess().getGamlGamlLangDefParserRuleCall_3_0_1_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleGamlLangDef_in_ruleModel201);
                    lv_gaml_4_0=ruleGamlLangDef();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getModelRule());
                      	        }
                             		set(
                             			current, 
                             			"gaml",
                              		lv_gaml_4_0, 
                              		"GamlLangDef");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }

                    otherlv_5=(Token)match(input,16,FOLLOW_16_in_ruleModel213); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_5, grammarAccess.getModelAccess().getRightCurlyBracketKeyword_3_0_2());
                          
                    }

                    }
                    break;

            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:146:3: ( (lv_statements_6_0= ruleStatement ) )*
            loop3:
            do {
                int alt3=2;
                int LA3_0 = input.LA(1);

                if ( (LA3_0==RULE_ID||(LA3_0>=22 && LA3_0<=38)||LA3_0==40) ) {
                    alt3=1;
                }


                switch (alt3) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:147:1: (lv_statements_6_0= ruleStatement )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:147:1: (lv_statements_6_0= ruleStatement )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:148:3: lv_statements_6_0= ruleStatement
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getModelAccess().getStatementsStatementParserRuleCall_3_1_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleStatement_in_ruleModel236);
            	    lv_statements_6_0=ruleStatement();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getModelRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"statements",
            	              		lv_statements_6_0, 
            	              		"Statement");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


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
    // $ANTLR end "ruleModel"


    // $ANTLR start "entryRuleImport"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:172:1: entryRuleImport returns [EObject current=null] : iv_ruleImport= ruleImport EOF ;
    public final EObject entryRuleImport() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleImport = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:173:2: (iv_ruleImport= ruleImport EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:174:2: iv_ruleImport= ruleImport EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getImportRule()); 
            }
            pushFollow(FOLLOW_ruleImport_in_entryRuleImport274);
            iv_ruleImport=ruleImport();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleImport; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleImport284); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:181:1: ruleImport returns [EObject current=null] : (otherlv_0= 'import' ( (lv_importURI_1_0= RULE_STRING ) ) ) ;
    public final EObject ruleImport() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_importURI_1_0=null;

         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:184:28: ( (otherlv_0= 'import' ( (lv_importURI_1_0= RULE_STRING ) ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:185:1: (otherlv_0= 'import' ( (lv_importURI_1_0= RULE_STRING ) ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:185:1: (otherlv_0= 'import' ( (lv_importURI_1_0= RULE_STRING ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:185:3: otherlv_0= 'import' ( (lv_importURI_1_0= RULE_STRING ) )
            {
            otherlv_0=(Token)match(input,17,FOLLOW_17_in_ruleImport321); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_0, grammarAccess.getImportAccess().getImportKeyword_0());
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:189:1: ( (lv_importURI_1_0= RULE_STRING ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:190:1: (lv_importURI_1_0= RULE_STRING )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:190:1: (lv_importURI_1_0= RULE_STRING )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:191:3: lv_importURI_1_0= RULE_STRING
            {
            lv_importURI_1_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleImport338); if (state.failed) return current;
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


    // $ANTLR start "entryRuleGamlLangDef"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:215:1: entryRuleGamlLangDef returns [EObject current=null] : iv_ruleGamlLangDef= ruleGamlLangDef EOF ;
    public final EObject entryRuleGamlLangDef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleGamlLangDef = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:216:2: (iv_ruleGamlLangDef= ruleGamlLangDef EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:217:2: iv_ruleGamlLangDef= ruleGamlLangDef EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getGamlLangDefRule()); 
            }
            pushFollow(FOLLOW_ruleGamlLangDef_in_entryRuleGamlLangDef379);
            iv_ruleGamlLangDef=ruleGamlLangDef();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleGamlLangDef; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleGamlLangDef389); if (state.failed) return current;

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
    // $ANTLR end "entryRuleGamlLangDef"


    // $ANTLR start "ruleGamlLangDef"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:224:1: ruleGamlLangDef returns [EObject current=null] : ( ( (lv_b_0_0= ruleDefBinaryOp ) ) | ( (lv_r_1_0= ruleDefReserved ) ) | ( (lv_unaries_2_0= ruleDefUnary ) ) )+ ;
    public final EObject ruleGamlLangDef() throws RecognitionException {
        EObject current = null;

        EObject lv_b_0_0 = null;

        EObject lv_r_1_0 = null;

        EObject lv_unaries_2_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:227:28: ( ( ( (lv_b_0_0= ruleDefBinaryOp ) ) | ( (lv_r_1_0= ruleDefReserved ) ) | ( (lv_unaries_2_0= ruleDefUnary ) ) )+ )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:228:1: ( ( (lv_b_0_0= ruleDefBinaryOp ) ) | ( (lv_r_1_0= ruleDefReserved ) ) | ( (lv_unaries_2_0= ruleDefUnary ) ) )+
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:228:1: ( ( (lv_b_0_0= ruleDefBinaryOp ) ) | ( (lv_r_1_0= ruleDefReserved ) ) | ( (lv_unaries_2_0= ruleDefUnary ) ) )+
            int cnt4=0;
            loop4:
            do {
                int alt4=4;
                switch ( input.LA(1) ) {
                case 18:
                    {
                    alt4=1;
                    }
                    break;
                case 20:
                    {
                    alt4=2;
                    }
                    break;
                case 21:
                    {
                    alt4=3;
                    }
                    break;

                }

                switch (alt4) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:228:2: ( (lv_b_0_0= ruleDefBinaryOp ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:228:2: ( (lv_b_0_0= ruleDefBinaryOp ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:229:1: (lv_b_0_0= ruleDefBinaryOp )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:229:1: (lv_b_0_0= ruleDefBinaryOp )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:230:3: lv_b_0_0= ruleDefBinaryOp
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getGamlLangDefAccess().getBDefBinaryOpParserRuleCall_0_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleDefBinaryOp_in_ruleGamlLangDef435);
            	    lv_b_0_0=ruleDefBinaryOp();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getGamlLangDefRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"b",
            	              		lv_b_0_0, 
            	              		"DefBinaryOp");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }


            	    }
            	    break;
            	case 2 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:247:6: ( (lv_r_1_0= ruleDefReserved ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:247:6: ( (lv_r_1_0= ruleDefReserved ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:248:1: (lv_r_1_0= ruleDefReserved )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:248:1: (lv_r_1_0= ruleDefReserved )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:249:3: lv_r_1_0= ruleDefReserved
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getGamlLangDefAccess().getRDefReservedParserRuleCall_1_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleDefReserved_in_ruleGamlLangDef462);
            	    lv_r_1_0=ruleDefReserved();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getGamlLangDefRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"r",
            	              		lv_r_1_0, 
            	              		"DefReserved");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }


            	    }
            	    break;
            	case 3 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:266:6: ( (lv_unaries_2_0= ruleDefUnary ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:266:6: ( (lv_unaries_2_0= ruleDefUnary ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:267:1: (lv_unaries_2_0= ruleDefUnary )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:267:1: (lv_unaries_2_0= ruleDefUnary )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:268:3: lv_unaries_2_0= ruleDefUnary
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getGamlLangDefAccess().getUnariesDefUnaryParserRuleCall_2_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleDefUnary_in_ruleGamlLangDef489);
            	    lv_unaries_2_0=ruleDefUnary();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getGamlLangDefRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"unaries",
            	              		lv_unaries_2_0, 
            	              		"DefUnary");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt4 >= 1 ) break loop4;
            	    if (state.backtracking>0) {state.failed=true; return current;}
                        EarlyExitException eee =
                            new EarlyExitException(4, input);
                        throw eee;
                }
                cnt4++;
            } while (true);


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
    // $ANTLR end "ruleGamlLangDef"


    // $ANTLR start "entryRuleDefBinaryOp"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:292:1: entryRuleDefBinaryOp returns [EObject current=null] : iv_ruleDefBinaryOp= ruleDefBinaryOp EOF ;
    public final EObject entryRuleDefBinaryOp() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDefBinaryOp = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:293:2: (iv_ruleDefBinaryOp= ruleDefBinaryOp EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:294:2: iv_ruleDefBinaryOp= ruleDefBinaryOp EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getDefBinaryOpRule()); 
            }
            pushFollow(FOLLOW_ruleDefBinaryOp_in_entryRuleDefBinaryOp526);
            iv_ruleDefBinaryOp=ruleDefBinaryOp();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleDefBinaryOp; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleDefBinaryOp536); if (state.failed) return current;

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
    // $ANTLR end "entryRuleDefBinaryOp"


    // $ANTLR start "ruleDefBinaryOp"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:301:1: ruleDefBinaryOp returns [EObject current=null] : (otherlv_0= '_binary &' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '&;' ) ;
    public final EObject ruleDefBinaryOp() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;

         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:304:28: ( (otherlv_0= '_binary &' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '&;' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:305:1: (otherlv_0= '_binary &' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '&;' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:305:1: (otherlv_0= '_binary &' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '&;' )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:305:3: otherlv_0= '_binary &' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '&;'
            {
            otherlv_0=(Token)match(input,18,FOLLOW_18_in_ruleDefBinaryOp573); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_0, grammarAccess.getDefBinaryOpAccess().get_binaryKeyword_0());
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:309:1: ( (lv_name_1_0= RULE_ID ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:310:1: (lv_name_1_0= RULE_ID )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:310:1: (lv_name_1_0= RULE_ID )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:311:3: lv_name_1_0= RULE_ID
            {
            lv_name_1_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleDefBinaryOp590); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(lv_name_1_0, grammarAccess.getDefBinaryOpAccess().getNameIDTerminalRuleCall_1_0()); 
              		
            }
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElement(grammarAccess.getDefBinaryOpRule());
              	        }
                     		setWithLastConsumed(
                     			current, 
                     			"name",
                      		lv_name_1_0, 
                      		"ID");
              	    
            }

            }


            }

            otherlv_2=(Token)match(input,19,FOLLOW_19_in_ruleDefBinaryOp607); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_2, grammarAccess.getDefBinaryOpAccess().getAmpersandSemicolonKeyword_2());
                  
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
    // $ANTLR end "ruleDefBinaryOp"


    // $ANTLR start "entryRuleDefReserved"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:339:1: entryRuleDefReserved returns [EObject current=null] : iv_ruleDefReserved= ruleDefReserved EOF ;
    public final EObject entryRuleDefReserved() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDefReserved = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:340:2: (iv_ruleDefReserved= ruleDefReserved EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:341:2: iv_ruleDefReserved= ruleDefReserved EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getDefReservedRule()); 
            }
            pushFollow(FOLLOW_ruleDefReserved_in_entryRuleDefReserved643);
            iv_ruleDefReserved=ruleDefReserved();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleDefReserved; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleDefReserved653); if (state.failed) return current;

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
    // $ANTLR end "entryRuleDefReserved"


    // $ANTLR start "ruleDefReserved"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:348:1: ruleDefReserved returns [EObject current=null] : (otherlv_0= '_reserved &' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '&;' ) ;
    public final EObject ruleDefReserved() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;

         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:351:28: ( (otherlv_0= '_reserved &' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '&;' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:352:1: (otherlv_0= '_reserved &' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '&;' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:352:1: (otherlv_0= '_reserved &' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '&;' )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:352:3: otherlv_0= '_reserved &' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '&;'
            {
            otherlv_0=(Token)match(input,20,FOLLOW_20_in_ruleDefReserved690); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_0, grammarAccess.getDefReservedAccess().get_reservedKeyword_0());
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:356:1: ( (lv_name_1_0= RULE_ID ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:357:1: (lv_name_1_0= RULE_ID )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:357:1: (lv_name_1_0= RULE_ID )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:358:3: lv_name_1_0= RULE_ID
            {
            lv_name_1_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleDefReserved707); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(lv_name_1_0, grammarAccess.getDefReservedAccess().getNameIDTerminalRuleCall_1_0()); 
              		
            }
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElement(grammarAccess.getDefReservedRule());
              	        }
                     		setWithLastConsumed(
                     			current, 
                     			"name",
                      		lv_name_1_0, 
                      		"ID");
              	    
            }

            }


            }

            otherlv_2=(Token)match(input,19,FOLLOW_19_in_ruleDefReserved724); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_2, grammarAccess.getDefReservedAccess().getAmpersandSemicolonKeyword_2());
                  
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
    // $ANTLR end "ruleDefReserved"


    // $ANTLR start "entryRuleDefUnary"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:386:1: entryRuleDefUnary returns [EObject current=null] : iv_ruleDefUnary= ruleDefUnary EOF ;
    public final EObject entryRuleDefUnary() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDefUnary = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:387:2: (iv_ruleDefUnary= ruleDefUnary EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:388:2: iv_ruleDefUnary= ruleDefUnary EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getDefUnaryRule()); 
            }
            pushFollow(FOLLOW_ruleDefUnary_in_entryRuleDefUnary760);
            iv_ruleDefUnary=ruleDefUnary();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleDefUnary; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleDefUnary770); if (state.failed) return current;

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
    // $ANTLR end "entryRuleDefUnary"


    // $ANTLR start "ruleDefUnary"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:395:1: ruleDefUnary returns [EObject current=null] : (otherlv_0= '_unary &' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '&;' ) ;
    public final EObject ruleDefUnary() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token otherlv_2=null;

         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:398:28: ( (otherlv_0= '_unary &' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '&;' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:399:1: (otherlv_0= '_unary &' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '&;' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:399:1: (otherlv_0= '_unary &' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '&;' )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:399:3: otherlv_0= '_unary &' ( (lv_name_1_0= RULE_ID ) ) otherlv_2= '&;'
            {
            otherlv_0=(Token)match(input,21,FOLLOW_21_in_ruleDefUnary807); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_0, grammarAccess.getDefUnaryAccess().get_unaryKeyword_0());
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:403:1: ( (lv_name_1_0= RULE_ID ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:404:1: (lv_name_1_0= RULE_ID )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:404:1: (lv_name_1_0= RULE_ID )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:405:3: lv_name_1_0= RULE_ID
            {
            lv_name_1_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleDefUnary824); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(lv_name_1_0, grammarAccess.getDefUnaryAccess().getNameIDTerminalRuleCall_1_0()); 
              		
            }
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElement(grammarAccess.getDefUnaryRule());
              	        }
                     		setWithLastConsumed(
                     			current, 
                     			"name",
                      		lv_name_1_0, 
                      		"ID");
              	    
            }

            }


            }

            otherlv_2=(Token)match(input,19,FOLLOW_19_in_ruleDefUnary841); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_2, grammarAccess.getDefUnaryAccess().getAmpersandSemicolonKeyword_2());
                  
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
    // $ANTLR end "ruleDefUnary"


    // $ANTLR start "entryRuleBuiltIn"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:433:1: entryRuleBuiltIn returns [String current=null] : iv_ruleBuiltIn= ruleBuiltIn EOF ;
    public final String entryRuleBuiltIn() throws RecognitionException {
        String current = null;

        AntlrDatatypeRuleToken iv_ruleBuiltIn = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:434:2: (iv_ruleBuiltIn= ruleBuiltIn EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:435:2: iv_ruleBuiltIn= ruleBuiltIn EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getBuiltInRule()); 
            }
            pushFollow(FOLLOW_ruleBuiltIn_in_entryRuleBuiltIn878);
            iv_ruleBuiltIn=ruleBuiltIn();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleBuiltIn.getText(); 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleBuiltIn889); if (state.failed) return current;

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
    // $ANTLR end "entryRuleBuiltIn"


    // $ANTLR start "ruleBuiltIn"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:442:1: ruleBuiltIn returns [AntlrDatatypeRuleToken current=new AntlrDatatypeRuleToken()] : (kw= 'write' | kw= 'warn' | kw= 'error' | kw= 'match' | kw= 'match_one' | kw= 'match_between' | kw= 'capture' | kw= 'release' | kw= 'ask' | kw= 'switch' | kw= 'create' | kw= 'add' | kw= 'remove' | kw= 'put' | kw= 'save' | kw= 'set' | kw= 'return' ) ;
    public final AntlrDatatypeRuleToken ruleBuiltIn() throws RecognitionException {
        AntlrDatatypeRuleToken current = new AntlrDatatypeRuleToken();

        Token kw=null;

         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:445:28: ( (kw= 'write' | kw= 'warn' | kw= 'error' | kw= 'match' | kw= 'match_one' | kw= 'match_between' | kw= 'capture' | kw= 'release' | kw= 'ask' | kw= 'switch' | kw= 'create' | kw= 'add' | kw= 'remove' | kw= 'put' | kw= 'save' | kw= 'set' | kw= 'return' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:446:1: (kw= 'write' | kw= 'warn' | kw= 'error' | kw= 'match' | kw= 'match_one' | kw= 'match_between' | kw= 'capture' | kw= 'release' | kw= 'ask' | kw= 'switch' | kw= 'create' | kw= 'add' | kw= 'remove' | kw= 'put' | kw= 'save' | kw= 'set' | kw= 'return' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:446:1: (kw= 'write' | kw= 'warn' | kw= 'error' | kw= 'match' | kw= 'match_one' | kw= 'match_between' | kw= 'capture' | kw= 'release' | kw= 'ask' | kw= 'switch' | kw= 'create' | kw= 'add' | kw= 'remove' | kw= 'put' | kw= 'save' | kw= 'set' | kw= 'return' )
            int alt5=17;
            switch ( input.LA(1) ) {
            case 22:
                {
                alt5=1;
                }
                break;
            case 23:
                {
                alt5=2;
                }
                break;
            case 24:
                {
                alt5=3;
                }
                break;
            case 25:
                {
                alt5=4;
                }
                break;
            case 26:
                {
                alt5=5;
                }
                break;
            case 27:
                {
                alt5=6;
                }
                break;
            case 28:
                {
                alt5=7;
                }
                break;
            case 29:
                {
                alt5=8;
                }
                break;
            case 30:
                {
                alt5=9;
                }
                break;
            case 31:
                {
                alt5=10;
                }
                break;
            case 32:
                {
                alt5=11;
                }
                break;
            case 33:
                {
                alt5=12;
                }
                break;
            case 34:
                {
                alt5=13;
                }
                break;
            case 35:
                {
                alt5=14;
                }
                break;
            case 36:
                {
                alt5=15;
                }
                break;
            case 37:
                {
                alt5=16;
                }
                break;
            case 38:
                {
                alt5=17;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;
            }

            switch (alt5) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:447:2: kw= 'write'
                    {
                    kw=(Token)match(input,22,FOLLOW_22_in_ruleBuiltIn927); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInAccess().getWriteKeyword_0()); 
                          
                    }

                    }
                    break;
                case 2 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:454:2: kw= 'warn'
                    {
                    kw=(Token)match(input,23,FOLLOW_23_in_ruleBuiltIn946); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInAccess().getWarnKeyword_1()); 
                          
                    }

                    }
                    break;
                case 3 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:461:2: kw= 'error'
                    {
                    kw=(Token)match(input,24,FOLLOW_24_in_ruleBuiltIn965); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInAccess().getErrorKeyword_2()); 
                          
                    }

                    }
                    break;
                case 4 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:468:2: kw= 'match'
                    {
                    kw=(Token)match(input,25,FOLLOW_25_in_ruleBuiltIn984); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInAccess().getMatchKeyword_3()); 
                          
                    }

                    }
                    break;
                case 5 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:475:2: kw= 'match_one'
                    {
                    kw=(Token)match(input,26,FOLLOW_26_in_ruleBuiltIn1003); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInAccess().getMatch_oneKeyword_4()); 
                          
                    }

                    }
                    break;
                case 6 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:482:2: kw= 'match_between'
                    {
                    kw=(Token)match(input,27,FOLLOW_27_in_ruleBuiltIn1022); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInAccess().getMatch_betweenKeyword_5()); 
                          
                    }

                    }
                    break;
                case 7 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:489:2: kw= 'capture'
                    {
                    kw=(Token)match(input,28,FOLLOW_28_in_ruleBuiltIn1041); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInAccess().getCaptureKeyword_6()); 
                          
                    }

                    }
                    break;
                case 8 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:496:2: kw= 'release'
                    {
                    kw=(Token)match(input,29,FOLLOW_29_in_ruleBuiltIn1060); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInAccess().getReleaseKeyword_7()); 
                          
                    }

                    }
                    break;
                case 9 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:503:2: kw= 'ask'
                    {
                    kw=(Token)match(input,30,FOLLOW_30_in_ruleBuiltIn1079); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInAccess().getAskKeyword_8()); 
                          
                    }

                    }
                    break;
                case 10 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:510:2: kw= 'switch'
                    {
                    kw=(Token)match(input,31,FOLLOW_31_in_ruleBuiltIn1098); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInAccess().getSwitchKeyword_9()); 
                          
                    }

                    }
                    break;
                case 11 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:517:2: kw= 'create'
                    {
                    kw=(Token)match(input,32,FOLLOW_32_in_ruleBuiltIn1117); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInAccess().getCreateKeyword_10()); 
                          
                    }

                    }
                    break;
                case 12 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:524:2: kw= 'add'
                    {
                    kw=(Token)match(input,33,FOLLOW_33_in_ruleBuiltIn1136); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInAccess().getAddKeyword_11()); 
                          
                    }

                    }
                    break;
                case 13 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:531:2: kw= 'remove'
                    {
                    kw=(Token)match(input,34,FOLLOW_34_in_ruleBuiltIn1155); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInAccess().getRemoveKeyword_12()); 
                          
                    }

                    }
                    break;
                case 14 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:538:2: kw= 'put'
                    {
                    kw=(Token)match(input,35,FOLLOW_35_in_ruleBuiltIn1174); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInAccess().getPutKeyword_13()); 
                          
                    }

                    }
                    break;
                case 15 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:545:2: kw= 'save'
                    {
                    kw=(Token)match(input,36,FOLLOW_36_in_ruleBuiltIn1193); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInAccess().getSaveKeyword_14()); 
                          
                    }

                    }
                    break;
                case 16 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:552:2: kw= 'set'
                    {
                    kw=(Token)match(input,37,FOLLOW_37_in_ruleBuiltIn1212); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInAccess().getSetKeyword_15()); 
                          
                    }

                    }
                    break;
                case 17 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:559:2: kw= 'return'
                    {
                    kw=(Token)match(input,38,FOLLOW_38_in_ruleBuiltIn1231); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              current.merge(kw);
                              newLeafNode(kw, grammarAccess.getBuiltInAccess().getReturnKeyword_16()); 
                          
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
    // $ANTLR end "ruleBuiltIn"


    // $ANTLR start "entryRuleStatement"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:572:1: entryRuleStatement returns [EObject current=null] : iv_ruleStatement= ruleStatement EOF ;
    public final EObject entryRuleStatement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleStatement = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:573:2: (iv_ruleStatement= ruleStatement EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:574:2: iv_ruleStatement= ruleStatement EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getStatementRule()); 
            }
            pushFollow(FOLLOW_ruleStatement_in_entryRuleStatement1271);
            iv_ruleStatement=ruleStatement();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleStatement; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleStatement1281); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:581:1: ruleStatement returns [EObject current=null] : (this_IfEval_0= ruleIfEval | this_ClassicStatement_1= ruleClassicStatement | this_Definition_2= ruleDefinition ) ;
    public final EObject ruleStatement() throws RecognitionException {
        EObject current = null;

        EObject this_IfEval_0 = null;

        EObject this_ClassicStatement_1 = null;

        EObject this_Definition_2 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:584:28: ( (this_IfEval_0= ruleIfEval | this_ClassicStatement_1= ruleClassicStatement | this_Definition_2= ruleDefinition ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:585:1: (this_IfEval_0= ruleIfEval | this_ClassicStatement_1= ruleClassicStatement | this_Definition_2= ruleDefinition )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:585:1: (this_IfEval_0= ruleIfEval | this_ClassicStatement_1= ruleClassicStatement | this_Definition_2= ruleDefinition )
            int alt6=3;
            switch ( input.LA(1) ) {
            case 40:
                {
                alt6=1;
                }
                break;
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
            case 35:
            case 36:
            case 37:
            case 38:
                {
                alt6=2;
                }
                break;
            case RULE_ID:
                {
                alt6=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;
            }

            switch (alt6) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:586:5: this_IfEval_0= ruleIfEval
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getStatementAccess().getIfEvalParserRuleCall_0()); 
                          
                    }
                    pushFollow(FOLLOW_ruleIfEval_in_ruleStatement1328);
                    this_IfEval_0=ruleIfEval();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_IfEval_0; 
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 2 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:596:5: this_ClassicStatement_1= ruleClassicStatement
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getStatementAccess().getClassicStatementParserRuleCall_1()); 
                          
                    }
                    pushFollow(FOLLOW_ruleClassicStatement_in_ruleStatement1355);
                    this_ClassicStatement_1=ruleClassicStatement();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_ClassicStatement_1; 
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 3 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:606:5: this_Definition_2= ruleDefinition
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getStatementAccess().getDefinitionParserRuleCall_2()); 
                          
                    }
                    pushFollow(FOLLOW_ruleDefinition_in_ruleStatement1382);
                    this_Definition_2=ruleDefinition();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_Definition_2; 
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
    // $ANTLR end "ruleStatement"


    // $ANTLR start "entryRuleClassicStatement"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:622:1: entryRuleClassicStatement returns [EObject current=null] : iv_ruleClassicStatement= ruleClassicStatement EOF ;
    public final EObject entryRuleClassicStatement() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleClassicStatement = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:623:2: (iv_ruleClassicStatement= ruleClassicStatement EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:624:2: iv_ruleClassicStatement= ruleClassicStatement EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getClassicStatementRule()); 
            }
            pushFollow(FOLLOW_ruleClassicStatement_in_entryRuleClassicStatement1417);
            iv_ruleClassicStatement=ruleClassicStatement();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleClassicStatement; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleClassicStatement1427); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:631:1: ruleClassicStatement returns [EObject current=null] : ( ( (lv_key_0_0= ruleBuiltIn ) ) ( (lv_ref_1_0= ruleGamlFacetRef ) )? ( (lv_expr_2_0= ruleExpression ) ) ( (lv_facets_3_0= ruleFacetExpr ) )* ( ( (lv_block_4_0= ruleBlock ) ) | otherlv_5= ';' ) ) ;
    public final EObject ruleClassicStatement() throws RecognitionException {
        EObject current = null;

        Token otherlv_5=null;
        AntlrDatatypeRuleToken lv_key_0_0 = null;

        EObject lv_ref_1_0 = null;

        EObject lv_expr_2_0 = null;

        EObject lv_facets_3_0 = null;

        EObject lv_block_4_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:634:28: ( ( ( (lv_key_0_0= ruleBuiltIn ) ) ( (lv_ref_1_0= ruleGamlFacetRef ) )? ( (lv_expr_2_0= ruleExpression ) ) ( (lv_facets_3_0= ruleFacetExpr ) )* ( ( (lv_block_4_0= ruleBlock ) ) | otherlv_5= ';' ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:635:1: ( ( (lv_key_0_0= ruleBuiltIn ) ) ( (lv_ref_1_0= ruleGamlFacetRef ) )? ( (lv_expr_2_0= ruleExpression ) ) ( (lv_facets_3_0= ruleFacetExpr ) )* ( ( (lv_block_4_0= ruleBlock ) ) | otherlv_5= ';' ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:635:1: ( ( (lv_key_0_0= ruleBuiltIn ) ) ( (lv_ref_1_0= ruleGamlFacetRef ) )? ( (lv_expr_2_0= ruleExpression ) ) ( (lv_facets_3_0= ruleFacetExpr ) )* ( ( (lv_block_4_0= ruleBlock ) ) | otherlv_5= ';' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:635:2: ( (lv_key_0_0= ruleBuiltIn ) ) ( (lv_ref_1_0= ruleGamlFacetRef ) )? ( (lv_expr_2_0= ruleExpression ) ) ( (lv_facets_3_0= ruleFacetExpr ) )* ( ( (lv_block_4_0= ruleBlock ) ) | otherlv_5= ';' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:635:2: ( (lv_key_0_0= ruleBuiltIn ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:636:1: (lv_key_0_0= ruleBuiltIn )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:636:1: (lv_key_0_0= ruleBuiltIn )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:637:3: lv_key_0_0= ruleBuiltIn
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getClassicStatementAccess().getKeyBuiltInParserRuleCall_0_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleBuiltIn_in_ruleClassicStatement1473);
            lv_key_0_0=ruleBuiltIn();

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
                      		"BuiltIn");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:653:2: ( (lv_ref_1_0= ruleGamlFacetRef ) )?
            int alt7=2;
            int LA7_0 = input.LA(1);

            if ( (LA7_0==RULE_ID) ) {
                int LA7_1 = input.LA(2);

                if ( (LA7_1==42) ) {
                    alt7=1;
                }
            }
            else if ( (LA7_0==43) ) {
                alt7=1;
            }
            switch (alt7) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:654:1: (lv_ref_1_0= ruleGamlFacetRef )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:654:1: (lv_ref_1_0= ruleGamlFacetRef )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:655:3: lv_ref_1_0= ruleGamlFacetRef
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getClassicStatementAccess().getRefGamlFacetRefParserRuleCall_1_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleGamlFacetRef_in_ruleClassicStatement1494);
                    lv_ref_1_0=ruleGamlFacetRef();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getClassicStatementRule());
                      	        }
                             		set(
                             			current, 
                             			"ref",
                              		lv_ref_1_0, 
                              		"GamlFacetRef");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }
                    break;

            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:671:3: ( (lv_expr_2_0= ruleExpression ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:672:1: (lv_expr_2_0= ruleExpression )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:672:1: (lv_expr_2_0= ruleExpression )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:673:3: lv_expr_2_0= ruleExpression
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getClassicStatementAccess().getExprExpressionParserRuleCall_2_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleExpression_in_ruleClassicStatement1516);
            lv_expr_2_0=ruleExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getClassicStatementRule());
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

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:689:2: ( (lv_facets_3_0= ruleFacetExpr ) )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0==RULE_ID||(LA8_0>=43 && LA8_0<=48)) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:690:1: (lv_facets_3_0= ruleFacetExpr )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:690:1: (lv_facets_3_0= ruleFacetExpr )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:691:3: lv_facets_3_0= ruleFacetExpr
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getClassicStatementAccess().getFacetsFacetExprParserRuleCall_3_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleFacetExpr_in_ruleClassicStatement1537);
            	    lv_facets_3_0=ruleFacetExpr();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getClassicStatementRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"facets",
            	              		lv_facets_3_0, 
            	              		"FacetExpr");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:707:3: ( ( (lv_block_4_0= ruleBlock ) ) | otherlv_5= ';' )
            int alt9=2;
            int LA9_0 = input.LA(1);

            if ( (LA9_0==49) ) {
                alt9=1;
            }
            else if ( (LA9_0==39) ) {
                alt9=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;
            }
            switch (alt9) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:707:4: ( (lv_block_4_0= ruleBlock ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:707:4: ( (lv_block_4_0= ruleBlock ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:708:1: (lv_block_4_0= ruleBlock )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:708:1: (lv_block_4_0= ruleBlock )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:709:3: lv_block_4_0= ruleBlock
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getClassicStatementAccess().getBlockBlockParserRuleCall_4_0_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleBlock_in_ruleClassicStatement1560);
                    lv_block_4_0=ruleBlock();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getClassicStatementRule());
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:726:7: otherlv_5= ';'
                    {
                    otherlv_5=(Token)match(input,39,FOLLOW_39_in_ruleClassicStatement1578); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_5, grammarAccess.getClassicStatementAccess().getSemicolonKeyword_4_1());
                          
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


    // $ANTLR start "entryRuleIfEval"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:738:1: entryRuleIfEval returns [EObject current=null] : iv_ruleIfEval= ruleIfEval EOF ;
    public final EObject entryRuleIfEval() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleIfEval = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:739:2: (iv_ruleIfEval= ruleIfEval EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:740:2: iv_ruleIfEval= ruleIfEval EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getIfEvalRule()); 
            }
            pushFollow(FOLLOW_ruleIfEval_in_entryRuleIfEval1615);
            iv_ruleIfEval=ruleIfEval();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleIfEval; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleIfEval1625); if (state.failed) return current;

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
    // $ANTLR end "entryRuleIfEval"


    // $ANTLR start "ruleIfEval"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:747:1: ruleIfEval returns [EObject current=null] : ( ( (lv_key_0_0= 'if' ) ) ( (lv_ref_1_0= ruleGamlFacetRef ) )? ( (lv_expr_2_0= ruleExpression ) ) ( (lv_block_3_0= ruleBlock ) ) ( ( ( 'else' )=>otherlv_4= 'else' ) ( ( (lv_else_5_1= ruleStatement | lv_else_5_2= ruleBlock ) ) ) )? ) ;
    public final EObject ruleIfEval() throws RecognitionException {
        EObject current = null;

        Token lv_key_0_0=null;
        Token otherlv_4=null;
        EObject lv_ref_1_0 = null;

        EObject lv_expr_2_0 = null;

        EObject lv_block_3_0 = null;

        EObject lv_else_5_1 = null;

        EObject lv_else_5_2 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:750:28: ( ( ( (lv_key_0_0= 'if' ) ) ( (lv_ref_1_0= ruleGamlFacetRef ) )? ( (lv_expr_2_0= ruleExpression ) ) ( (lv_block_3_0= ruleBlock ) ) ( ( ( 'else' )=>otherlv_4= 'else' ) ( ( (lv_else_5_1= ruleStatement | lv_else_5_2= ruleBlock ) ) ) )? ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:751:1: ( ( (lv_key_0_0= 'if' ) ) ( (lv_ref_1_0= ruleGamlFacetRef ) )? ( (lv_expr_2_0= ruleExpression ) ) ( (lv_block_3_0= ruleBlock ) ) ( ( ( 'else' )=>otherlv_4= 'else' ) ( ( (lv_else_5_1= ruleStatement | lv_else_5_2= ruleBlock ) ) ) )? )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:751:1: ( ( (lv_key_0_0= 'if' ) ) ( (lv_ref_1_0= ruleGamlFacetRef ) )? ( (lv_expr_2_0= ruleExpression ) ) ( (lv_block_3_0= ruleBlock ) ) ( ( ( 'else' )=>otherlv_4= 'else' ) ( ( (lv_else_5_1= ruleStatement | lv_else_5_2= ruleBlock ) ) ) )? )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:751:2: ( (lv_key_0_0= 'if' ) ) ( (lv_ref_1_0= ruleGamlFacetRef ) )? ( (lv_expr_2_0= ruleExpression ) ) ( (lv_block_3_0= ruleBlock ) ) ( ( ( 'else' )=>otherlv_4= 'else' ) ( ( (lv_else_5_1= ruleStatement | lv_else_5_2= ruleBlock ) ) ) )?
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:751:2: ( (lv_key_0_0= 'if' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:752:1: (lv_key_0_0= 'if' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:752:1: (lv_key_0_0= 'if' )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:753:3: lv_key_0_0= 'if'
            {
            lv_key_0_0=(Token)match(input,40,FOLLOW_40_in_ruleIfEval1668); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                      newLeafNode(lv_key_0_0, grammarAccess.getIfEvalAccess().getKeyIfKeyword_0_0());
                  
            }
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElement(grammarAccess.getIfEvalRule());
              	        }
                     		setWithLastConsumed(current, "key", lv_key_0_0, "if");
              	    
            }

            }


            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:766:2: ( (lv_ref_1_0= ruleGamlFacetRef ) )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( (LA10_0==RULE_ID) ) {
                int LA10_1 = input.LA(2);

                if ( (LA10_1==42) ) {
                    alt10=1;
                }
            }
            else if ( (LA10_0==43) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:767:1: (lv_ref_1_0= ruleGamlFacetRef )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:767:1: (lv_ref_1_0= ruleGamlFacetRef )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:768:3: lv_ref_1_0= ruleGamlFacetRef
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getIfEvalAccess().getRefGamlFacetRefParserRuleCall_1_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleGamlFacetRef_in_ruleIfEval1702);
                    lv_ref_1_0=ruleGamlFacetRef();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getIfEvalRule());
                      	        }
                             		set(
                             			current, 
                             			"ref",
                              		lv_ref_1_0, 
                              		"GamlFacetRef");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }
                    break;

            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:784:3: ( (lv_expr_2_0= ruleExpression ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:785:1: (lv_expr_2_0= ruleExpression )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:785:1: (lv_expr_2_0= ruleExpression )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:786:3: lv_expr_2_0= ruleExpression
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getIfEvalAccess().getExprExpressionParserRuleCall_2_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleExpression_in_ruleIfEval1724);
            lv_expr_2_0=ruleExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getIfEvalRule());
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

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:802:2: ( (lv_block_3_0= ruleBlock ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:803:1: (lv_block_3_0= ruleBlock )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:803:1: (lv_block_3_0= ruleBlock )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:804:3: lv_block_3_0= ruleBlock
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getIfEvalAccess().getBlockBlockParserRuleCall_3_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleBlock_in_ruleIfEval1745);
            lv_block_3_0=ruleBlock();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getIfEvalRule());
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

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:820:2: ( ( ( 'else' )=>otherlv_4= 'else' ) ( ( (lv_else_5_1= ruleStatement | lv_else_5_2= ruleBlock ) ) ) )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==41) && (synpred1_InternalGaml())) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:820:3: ( ( 'else' )=>otherlv_4= 'else' ) ( ( (lv_else_5_1= ruleStatement | lv_else_5_2= ruleBlock ) ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:820:3: ( ( 'else' )=>otherlv_4= 'else' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:820:4: ( 'else' )=>otherlv_4= 'else'
                    {
                    otherlv_4=(Token)match(input,41,FOLLOW_41_in_ruleIfEval1766); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_4, grammarAccess.getIfEvalAccess().getElseKeyword_4_0());
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:825:2: ( ( (lv_else_5_1= ruleStatement | lv_else_5_2= ruleBlock ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:826:1: ( (lv_else_5_1= ruleStatement | lv_else_5_2= ruleBlock ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:826:1: ( (lv_else_5_1= ruleStatement | lv_else_5_2= ruleBlock ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:827:1: (lv_else_5_1= ruleStatement | lv_else_5_2= ruleBlock )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:827:1: (lv_else_5_1= ruleStatement | lv_else_5_2= ruleBlock )
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==RULE_ID||(LA11_0>=22 && LA11_0<=38)||LA11_0==40) ) {
                        alt11=1;
                    }
                    else if ( (LA11_0==49) ) {
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
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:828:3: lv_else_5_1= ruleStatement
                            {
                            if ( state.backtracking==0 ) {
                               
                              	        newCompositeNode(grammarAccess.getIfEvalAccess().getElseStatementParserRuleCall_4_1_0_0()); 
                              	    
                            }
                            pushFollow(FOLLOW_ruleStatement_in_ruleIfEval1790);
                            lv_else_5_1=ruleStatement();

                            state._fsp--;
                            if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElementForParent(grammarAccess.getIfEvalRule());
                              	        }
                                     		set(
                                     			current, 
                                     			"else",
                                      		lv_else_5_1, 
                                      		"Statement");
                              	        afterParserOrEnumRuleCall();
                              	    
                            }

                            }
                            break;
                        case 2 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:843:8: lv_else_5_2= ruleBlock
                            {
                            if ( state.backtracking==0 ) {
                               
                              	        newCompositeNode(grammarAccess.getIfEvalAccess().getElseBlockParserRuleCall_4_1_0_1()); 
                              	    
                            }
                            pushFollow(FOLLOW_ruleBlock_in_ruleIfEval1809);
                            lv_else_5_2=ruleBlock();

                            state._fsp--;
                            if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElementForParent(grammarAccess.getIfEvalRule());
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
    // $ANTLR end "ruleIfEval"


    // $ANTLR start "entryRuleDefinition"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:869:1: entryRuleDefinition returns [EObject current=null] : iv_ruleDefinition= ruleDefinition EOF ;
    public final EObject entryRuleDefinition() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDefinition = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:870:2: (iv_ruleDefinition= ruleDefinition EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:871:2: iv_ruleDefinition= ruleDefinition EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getDefinitionRule()); 
            }
            pushFollow(FOLLOW_ruleDefinition_in_entryRuleDefinition1850);
            iv_ruleDefinition=ruleDefinition();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleDefinition; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleDefinition1860); if (state.failed) return current;

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
    // $ANTLR end "entryRuleDefinition"


    // $ANTLR start "ruleDefinition"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:878:1: ruleDefinition returns [EObject current=null] : ( ( (lv_key_0_0= RULE_ID ) ) ( ( (lv_name_1_0= RULE_ID ) ) | ( (lv_name_2_0= RULE_STRING ) ) | ( (lv_name_3_0= ruleBuiltIn ) ) )? ( (lv_facets_4_0= ruleFacetExpr ) )* ( ( (lv_block_5_0= ruleBlock ) ) | otherlv_6= ';' ) ) ;
    public final EObject ruleDefinition() throws RecognitionException {
        EObject current = null;

        Token lv_key_0_0=null;
        Token lv_name_1_0=null;
        Token lv_name_2_0=null;
        Token otherlv_6=null;
        AntlrDatatypeRuleToken lv_name_3_0 = null;

        EObject lv_facets_4_0 = null;

        EObject lv_block_5_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:881:28: ( ( ( (lv_key_0_0= RULE_ID ) ) ( ( (lv_name_1_0= RULE_ID ) ) | ( (lv_name_2_0= RULE_STRING ) ) | ( (lv_name_3_0= ruleBuiltIn ) ) )? ( (lv_facets_4_0= ruleFacetExpr ) )* ( ( (lv_block_5_0= ruleBlock ) ) | otherlv_6= ';' ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:882:1: ( ( (lv_key_0_0= RULE_ID ) ) ( ( (lv_name_1_0= RULE_ID ) ) | ( (lv_name_2_0= RULE_STRING ) ) | ( (lv_name_3_0= ruleBuiltIn ) ) )? ( (lv_facets_4_0= ruleFacetExpr ) )* ( ( (lv_block_5_0= ruleBlock ) ) | otherlv_6= ';' ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:882:1: ( ( (lv_key_0_0= RULE_ID ) ) ( ( (lv_name_1_0= RULE_ID ) ) | ( (lv_name_2_0= RULE_STRING ) ) | ( (lv_name_3_0= ruleBuiltIn ) ) )? ( (lv_facets_4_0= ruleFacetExpr ) )* ( ( (lv_block_5_0= ruleBlock ) ) | otherlv_6= ';' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:882:2: ( (lv_key_0_0= RULE_ID ) ) ( ( (lv_name_1_0= RULE_ID ) ) | ( (lv_name_2_0= RULE_STRING ) ) | ( (lv_name_3_0= ruleBuiltIn ) ) )? ( (lv_facets_4_0= ruleFacetExpr ) )* ( ( (lv_block_5_0= ruleBlock ) ) | otherlv_6= ';' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:882:2: ( (lv_key_0_0= RULE_ID ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:883:1: (lv_key_0_0= RULE_ID )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:883:1: (lv_key_0_0= RULE_ID )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:884:3: lv_key_0_0= RULE_ID
            {
            lv_key_0_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleDefinition1902); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(lv_key_0_0, grammarAccess.getDefinitionAccess().getKeyIDTerminalRuleCall_0_0()); 
              		
            }
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElement(grammarAccess.getDefinitionRule());
              	        }
                     		setWithLastConsumed(
                     			current, 
                     			"key",
                      		lv_key_0_0, 
                      		"ID");
              	    
            }

            }


            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:900:2: ( ( (lv_name_1_0= RULE_ID ) ) | ( (lv_name_2_0= RULE_STRING ) ) | ( (lv_name_3_0= ruleBuiltIn ) ) )?
            int alt13=4;
            switch ( input.LA(1) ) {
                case RULE_ID:
                    {
                    int LA13_1 = input.LA(2);

                    if ( (LA13_1==RULE_ID||LA13_1==39||(LA13_1>=43 && LA13_1<=49)) ) {
                        alt13=1;
                    }
                    }
                    break;
                case RULE_STRING:
                    {
                    alt13=2;
                    }
                    break;
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
                case 35:
                case 36:
                case 37:
                case 38:
                    {
                    alt13=3;
                    }
                    break;
            }

            switch (alt13) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:900:3: ( (lv_name_1_0= RULE_ID ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:900:3: ( (lv_name_1_0= RULE_ID ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:901:1: (lv_name_1_0= RULE_ID )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:901:1: (lv_name_1_0= RULE_ID )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:902:3: lv_name_1_0= RULE_ID
                    {
                    lv_name_1_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleDefinition1925); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			newLeafNode(lv_name_1_0, grammarAccess.getDefinitionAccess().getNameIDTerminalRuleCall_1_0_0()); 
                      		
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getDefinitionRule());
                      	        }
                             		setWithLastConsumed(
                             			current, 
                             			"name",
                              		lv_name_1_0, 
                              		"ID");
                      	    
                    }

                    }


                    }


                    }
                    break;
                case 2 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:919:6: ( (lv_name_2_0= RULE_STRING ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:919:6: ( (lv_name_2_0= RULE_STRING ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:920:1: (lv_name_2_0= RULE_STRING )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:920:1: (lv_name_2_0= RULE_STRING )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:921:3: lv_name_2_0= RULE_STRING
                    {
                    lv_name_2_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleDefinition1953); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			newLeafNode(lv_name_2_0, grammarAccess.getDefinitionAccess().getNameSTRINGTerminalRuleCall_1_1_0()); 
                      		
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getDefinitionRule());
                      	        }
                             		setWithLastConsumed(
                             			current, 
                             			"name",
                              		lv_name_2_0, 
                              		"STRING");
                      	    
                    }

                    }


                    }


                    }
                    break;
                case 3 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:938:6: ( (lv_name_3_0= ruleBuiltIn ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:938:6: ( (lv_name_3_0= ruleBuiltIn ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:939:1: (lv_name_3_0= ruleBuiltIn )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:939:1: (lv_name_3_0= ruleBuiltIn )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:940:3: lv_name_3_0= ruleBuiltIn
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getDefinitionAccess().getNameBuiltInParserRuleCall_1_2_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleBuiltIn_in_ruleDefinition1985);
                    lv_name_3_0=ruleBuiltIn();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getDefinitionRule());
                      	        }
                             		set(
                             			current, 
                             			"name",
                              		lv_name_3_0, 
                              		"BuiltIn");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }
                    break;

            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:956:4: ( (lv_facets_4_0= ruleFacetExpr ) )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0==RULE_ID||(LA14_0>=43 && LA14_0<=48)) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:957:1: (lv_facets_4_0= ruleFacetExpr )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:957:1: (lv_facets_4_0= ruleFacetExpr )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:958:3: lv_facets_4_0= ruleFacetExpr
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getDefinitionAccess().getFacetsFacetExprParserRuleCall_2_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleFacetExpr_in_ruleDefinition2008);
            	    lv_facets_4_0=ruleFacetExpr();

            	    state._fsp--;
            	    if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElementForParent(grammarAccess.getDefinitionRule());
            	      	        }
            	             		add(
            	             			current, 
            	             			"facets",
            	              		lv_facets_4_0, 
            	              		"FacetExpr");
            	      	        afterParserOrEnumRuleCall();
            	      	    
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:974:3: ( ( (lv_block_5_0= ruleBlock ) ) | otherlv_6= ';' )
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==49) ) {
                alt15=1;
            }
            else if ( (LA15_0==39) ) {
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:974:4: ( (lv_block_5_0= ruleBlock ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:974:4: ( (lv_block_5_0= ruleBlock ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:975:1: (lv_block_5_0= ruleBlock )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:975:1: (lv_block_5_0= ruleBlock )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:976:3: lv_block_5_0= ruleBlock
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getDefinitionAccess().getBlockBlockParserRuleCall_3_0_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleBlock_in_ruleDefinition2031);
                    lv_block_5_0=ruleBlock();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getDefinitionRule());
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:993:7: otherlv_6= ';'
                    {
                    otherlv_6=(Token)match(input,39,FOLLOW_39_in_ruleDefinition2049); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_6, grammarAccess.getDefinitionAccess().getSemicolonKeyword_3_1());
                          
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
    // $ANTLR end "ruleDefinition"


    // $ANTLR start "entryRuleGamlFacetRef"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1007:1: entryRuleGamlFacetRef returns [EObject current=null] : iv_ruleGamlFacetRef= ruleGamlFacetRef EOF ;
    public final EObject entryRuleGamlFacetRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleGamlFacetRef = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1008:2: (iv_ruleGamlFacetRef= ruleGamlFacetRef EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1009:2: iv_ruleGamlFacetRef= ruleGamlFacetRef EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getGamlFacetRefRule()); 
            }
            pushFollow(FOLLOW_ruleGamlFacetRef_in_entryRuleGamlFacetRef2088);
            iv_ruleGamlFacetRef=ruleGamlFacetRef();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleGamlFacetRef; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleGamlFacetRef2098); if (state.failed) return current;

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
    // $ANTLR end "entryRuleGamlFacetRef"


    // $ANTLR start "ruleGamlFacetRef"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1016:1: ruleGamlFacetRef returns [EObject current=null] : ( ( ( (lv_ref_0_0= RULE_ID ) ) otherlv_1= ':' ) | ( (lv_ref_2_0= '<-' ) ) ) ;
    public final EObject ruleGamlFacetRef() throws RecognitionException {
        EObject current = null;

        Token lv_ref_0_0=null;
        Token otherlv_1=null;
        Token lv_ref_2_0=null;

         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1019:28: ( ( ( ( (lv_ref_0_0= RULE_ID ) ) otherlv_1= ':' ) | ( (lv_ref_2_0= '<-' ) ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1020:1: ( ( ( (lv_ref_0_0= RULE_ID ) ) otherlv_1= ':' ) | ( (lv_ref_2_0= '<-' ) ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1020:1: ( ( ( (lv_ref_0_0= RULE_ID ) ) otherlv_1= ':' ) | ( (lv_ref_2_0= '<-' ) ) )
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==RULE_ID) ) {
                alt16=1;
            }
            else if ( (LA16_0==43) ) {
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1020:2: ( ( (lv_ref_0_0= RULE_ID ) ) otherlv_1= ':' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1020:2: ( ( (lv_ref_0_0= RULE_ID ) ) otherlv_1= ':' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1020:3: ( (lv_ref_0_0= RULE_ID ) ) otherlv_1= ':'
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1020:3: ( (lv_ref_0_0= RULE_ID ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1021:1: (lv_ref_0_0= RULE_ID )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1021:1: (lv_ref_0_0= RULE_ID )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1022:3: lv_ref_0_0= RULE_ID
                    {
                    lv_ref_0_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleGamlFacetRef2141); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			newLeafNode(lv_ref_0_0, grammarAccess.getGamlFacetRefAccess().getRefIDTerminalRuleCall_0_0_0()); 
                      		
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getGamlFacetRefRule());
                      	        }
                             		setWithLastConsumed(
                             			current, 
                             			"ref",
                              		lv_ref_0_0, 
                              		"ID");
                      	    
                    }

                    }


                    }

                    otherlv_1=(Token)match(input,42,FOLLOW_42_in_ruleGamlFacetRef2158); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_1, grammarAccess.getGamlFacetRefAccess().getColonKeyword_0_1());
                          
                    }

                    }


                    }
                    break;
                case 2 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1043:6: ( (lv_ref_2_0= '<-' ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1043:6: ( (lv_ref_2_0= '<-' ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1044:1: (lv_ref_2_0= '<-' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1044:1: (lv_ref_2_0= '<-' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1045:3: lv_ref_2_0= '<-'
                    {
                    lv_ref_2_0=(Token)match(input,43,FOLLOW_43_in_ruleGamlFacetRef2183); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_ref_2_0, grammarAccess.getGamlFacetRefAccess().getRefLessThanSignHyphenMinusKeyword_1_0());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getGamlFacetRefRule());
                      	        }
                             		setWithLastConsumed(current, "ref", lv_ref_2_0, "<-");
                      	    
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
    // $ANTLR end "ruleGamlFacetRef"


    // $ANTLR start "entryRuleFunctionGamlFacetRef"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1066:1: entryRuleFunctionGamlFacetRef returns [EObject current=null] : iv_ruleFunctionGamlFacetRef= ruleFunctionGamlFacetRef EOF ;
    public final EObject entryRuleFunctionGamlFacetRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleFunctionGamlFacetRef = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1067:2: (iv_ruleFunctionGamlFacetRef= ruleFunctionGamlFacetRef EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1068:2: iv_ruleFunctionGamlFacetRef= ruleFunctionGamlFacetRef EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getFunctionGamlFacetRefRule()); 
            }
            pushFollow(FOLLOW_ruleFunctionGamlFacetRef_in_entryRuleFunctionGamlFacetRef2232);
            iv_ruleFunctionGamlFacetRef=ruleFunctionGamlFacetRef();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleFunctionGamlFacetRef; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleFunctionGamlFacetRef2242); if (state.failed) return current;

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
    // $ANTLR end "entryRuleFunctionGamlFacetRef"


    // $ANTLR start "ruleFunctionGamlFacetRef"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1075:1: ruleFunctionGamlFacetRef returns [EObject current=null] : ( ( ( (lv_ref_0_0= 'function' ) ) otherlv_1= ':' ) | ( (lv_ref_2_0= '->' ) ) ) ;
    public final EObject ruleFunctionGamlFacetRef() throws RecognitionException {
        EObject current = null;

        Token lv_ref_0_0=null;
        Token otherlv_1=null;
        Token lv_ref_2_0=null;

         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1078:28: ( ( ( ( (lv_ref_0_0= 'function' ) ) otherlv_1= ':' ) | ( (lv_ref_2_0= '->' ) ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1079:1: ( ( ( (lv_ref_0_0= 'function' ) ) otherlv_1= ':' ) | ( (lv_ref_2_0= '->' ) ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1079:1: ( ( ( (lv_ref_0_0= 'function' ) ) otherlv_1= ':' ) | ( (lv_ref_2_0= '->' ) ) )
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==44) ) {
                alt17=1;
            }
            else if ( (LA17_0==45) ) {
                alt17=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 17, 0, input);

                throw nvae;
            }
            switch (alt17) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1079:2: ( ( (lv_ref_0_0= 'function' ) ) otherlv_1= ':' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1079:2: ( ( (lv_ref_0_0= 'function' ) ) otherlv_1= ':' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1079:3: ( (lv_ref_0_0= 'function' ) ) otherlv_1= ':'
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1079:3: ( (lv_ref_0_0= 'function' ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1080:1: (lv_ref_0_0= 'function' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1080:1: (lv_ref_0_0= 'function' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1081:3: lv_ref_0_0= 'function'
                    {
                    lv_ref_0_0=(Token)match(input,44,FOLLOW_44_in_ruleFunctionGamlFacetRef2286); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_ref_0_0, grammarAccess.getFunctionGamlFacetRefAccess().getRefFunctionKeyword_0_0_0());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getFunctionGamlFacetRefRule());
                      	        }
                             		setWithLastConsumed(current, "ref", lv_ref_0_0, "function");
                      	    
                    }

                    }


                    }

                    otherlv_1=(Token)match(input,42,FOLLOW_42_in_ruleFunctionGamlFacetRef2311); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_1, grammarAccess.getFunctionGamlFacetRefAccess().getColonKeyword_0_1());
                          
                    }

                    }


                    }
                    break;
                case 2 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1099:6: ( (lv_ref_2_0= '->' ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1099:6: ( (lv_ref_2_0= '->' ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1100:1: (lv_ref_2_0= '->' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1100:1: (lv_ref_2_0= '->' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1101:3: lv_ref_2_0= '->'
                    {
                    lv_ref_2_0=(Token)match(input,45,FOLLOW_45_in_ruleFunctionGamlFacetRef2336); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_ref_2_0, grammarAccess.getFunctionGamlFacetRefAccess().getRefHyphenMinusGreaterThanSignKeyword_1_0());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getFunctionGamlFacetRefRule());
                      	        }
                             		setWithLastConsumed(current, "ref", lv_ref_2_0, "->");
                      	    
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
    // $ANTLR end "ruleFunctionGamlFacetRef"


    // $ANTLR start "entryRuleFacetExpr"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1122:1: entryRuleFacetExpr returns [EObject current=null] : iv_ruleFacetExpr= ruleFacetExpr EOF ;
    public final EObject entryRuleFacetExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleFacetExpr = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1123:2: (iv_ruleFacetExpr= ruleFacetExpr EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1124:2: iv_ruleFacetExpr= ruleFacetExpr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getFacetExprRule()); 
            }
            pushFollow(FOLLOW_ruleFacetExpr_in_entryRuleFacetExpr2385);
            iv_ruleFacetExpr=ruleFacetExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleFacetExpr; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleFacetExpr2395); if (state.failed) return current;

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
    // $ANTLR end "entryRuleFacetExpr"


    // $ANTLR start "ruleFacetExpr"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1131:1: ruleFacetExpr returns [EObject current=null] : (this_FunctionFacetExpr_0= ruleFunctionFacetExpr | this_DefinitionFacetExpr_1= ruleDefinitionFacetExpr | ( ( (lv_key_2_0= ruleGamlFacetRef ) ) ( (lv_expr_3_0= ruleExpression ) ) ) ) ;
    public final EObject ruleFacetExpr() throws RecognitionException {
        EObject current = null;

        EObject this_FunctionFacetExpr_0 = null;

        EObject this_DefinitionFacetExpr_1 = null;

        EObject lv_key_2_0 = null;

        EObject lv_expr_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1134:28: ( (this_FunctionFacetExpr_0= ruleFunctionFacetExpr | this_DefinitionFacetExpr_1= ruleDefinitionFacetExpr | ( ( (lv_key_2_0= ruleGamlFacetRef ) ) ( (lv_expr_3_0= ruleExpression ) ) ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1135:1: (this_FunctionFacetExpr_0= ruleFunctionFacetExpr | this_DefinitionFacetExpr_1= ruleDefinitionFacetExpr | ( ( (lv_key_2_0= ruleGamlFacetRef ) ) ( (lv_expr_3_0= ruleExpression ) ) ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1135:1: (this_FunctionFacetExpr_0= ruleFunctionFacetExpr | this_DefinitionFacetExpr_1= ruleDefinitionFacetExpr | ( ( (lv_key_2_0= ruleGamlFacetRef ) ) ( (lv_expr_3_0= ruleExpression ) ) ) )
            int alt18=3;
            switch ( input.LA(1) ) {
            case 44:
            case 45:
                {
                alt18=1;
                }
                break;
            case 46:
            case 47:
            case 48:
                {
                alt18=2;
                }
                break;
            case RULE_ID:
            case 43:
                {
                alt18=3;
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1136:5: this_FunctionFacetExpr_0= ruleFunctionFacetExpr
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getFacetExprAccess().getFunctionFacetExprParserRuleCall_0()); 
                          
                    }
                    pushFollow(FOLLOW_ruleFunctionFacetExpr_in_ruleFacetExpr2442);
                    this_FunctionFacetExpr_0=ruleFunctionFacetExpr();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_FunctionFacetExpr_0; 
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 2 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1146:5: this_DefinitionFacetExpr_1= ruleDefinitionFacetExpr
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getFacetExprAccess().getDefinitionFacetExprParserRuleCall_1()); 
                          
                    }
                    pushFollow(FOLLOW_ruleDefinitionFacetExpr_in_ruleFacetExpr2469);
                    this_DefinitionFacetExpr_1=ruleDefinitionFacetExpr();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_DefinitionFacetExpr_1; 
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 3 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1155:6: ( ( (lv_key_2_0= ruleGamlFacetRef ) ) ( (lv_expr_3_0= ruleExpression ) ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1155:6: ( ( (lv_key_2_0= ruleGamlFacetRef ) ) ( (lv_expr_3_0= ruleExpression ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1155:7: ( (lv_key_2_0= ruleGamlFacetRef ) ) ( (lv_expr_3_0= ruleExpression ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1155:7: ( (lv_key_2_0= ruleGamlFacetRef ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1156:1: (lv_key_2_0= ruleGamlFacetRef )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1156:1: (lv_key_2_0= ruleGamlFacetRef )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1157:3: lv_key_2_0= ruleGamlFacetRef
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getFacetExprAccess().getKeyGamlFacetRefParserRuleCall_2_0_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleGamlFacetRef_in_ruleFacetExpr2496);
                    lv_key_2_0=ruleGamlFacetRef();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getFacetExprRule());
                      	        }
                             		set(
                             			current, 
                             			"key",
                              		lv_key_2_0, 
                              		"GamlFacetRef");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1173:2: ( (lv_expr_3_0= ruleExpression ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1174:1: (lv_expr_3_0= ruleExpression )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1174:1: (lv_expr_3_0= ruleExpression )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1175:3: lv_expr_3_0= ruleExpression
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getFacetExprAccess().getExprExpressionParserRuleCall_2_1_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleExpression_in_ruleFacetExpr2517);
                    lv_expr_3_0=ruleExpression();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getFacetExprRule());
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
    // $ANTLR end "ruleFacetExpr"


    // $ANTLR start "entryRuleDefinitionFacetExpr"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1199:1: entryRuleDefinitionFacetExpr returns [EObject current=null] : iv_ruleDefinitionFacetExpr= ruleDefinitionFacetExpr EOF ;
    public final EObject entryRuleDefinitionFacetExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleDefinitionFacetExpr = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1200:2: (iv_ruleDefinitionFacetExpr= ruleDefinitionFacetExpr EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1201:2: iv_ruleDefinitionFacetExpr= ruleDefinitionFacetExpr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getDefinitionFacetExprRule()); 
            }
            pushFollow(FOLLOW_ruleDefinitionFacetExpr_in_entryRuleDefinitionFacetExpr2554);
            iv_ruleDefinitionFacetExpr=ruleDefinitionFacetExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleDefinitionFacetExpr; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleDefinitionFacetExpr2564); if (state.failed) return current;

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
    // $ANTLR end "entryRuleDefinitionFacetExpr"


    // $ANTLR start "ruleDefinitionFacetExpr"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1208:1: ruleDefinitionFacetExpr returns [EObject current=null] : (this_ReturnsFacetExpr_0= ruleReturnsFacetExpr | this_NameFacetExpr_1= ruleNameFacetExpr | this_ActionFacetExpr_2= ruleActionFacetExpr ) ;
    public final EObject ruleDefinitionFacetExpr() throws RecognitionException {
        EObject current = null;

        EObject this_ReturnsFacetExpr_0 = null;

        EObject this_NameFacetExpr_1 = null;

        EObject this_ActionFacetExpr_2 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1211:28: ( (this_ReturnsFacetExpr_0= ruleReturnsFacetExpr | this_NameFacetExpr_1= ruleNameFacetExpr | this_ActionFacetExpr_2= ruleActionFacetExpr ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1212:1: (this_ReturnsFacetExpr_0= ruleReturnsFacetExpr | this_NameFacetExpr_1= ruleNameFacetExpr | this_ActionFacetExpr_2= ruleActionFacetExpr )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1212:1: (this_ReturnsFacetExpr_0= ruleReturnsFacetExpr | this_NameFacetExpr_1= ruleNameFacetExpr | this_ActionFacetExpr_2= ruleActionFacetExpr )
            int alt19=3;
            switch ( input.LA(1) ) {
            case 47:
                {
                alt19=1;
                }
                break;
            case 46:
                {
                alt19=2;
                }
                break;
            case 48:
                {
                alt19=3;
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1213:5: this_ReturnsFacetExpr_0= ruleReturnsFacetExpr
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getDefinitionFacetExprAccess().getReturnsFacetExprParserRuleCall_0()); 
                          
                    }
                    pushFollow(FOLLOW_ruleReturnsFacetExpr_in_ruleDefinitionFacetExpr2611);
                    this_ReturnsFacetExpr_0=ruleReturnsFacetExpr();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_ReturnsFacetExpr_0; 
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 2 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1223:5: this_NameFacetExpr_1= ruleNameFacetExpr
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getDefinitionFacetExprAccess().getNameFacetExprParserRuleCall_1()); 
                          
                    }
                    pushFollow(FOLLOW_ruleNameFacetExpr_in_ruleDefinitionFacetExpr2638);
                    this_NameFacetExpr_1=ruleNameFacetExpr();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_NameFacetExpr_1; 
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 3 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1233:5: this_ActionFacetExpr_2= ruleActionFacetExpr
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getDefinitionFacetExprAccess().getActionFacetExprParserRuleCall_2()); 
                          
                    }
                    pushFollow(FOLLOW_ruleActionFacetExpr_in_ruleDefinitionFacetExpr2665);
                    this_ActionFacetExpr_2=ruleActionFacetExpr();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_ActionFacetExpr_2; 
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
    // $ANTLR end "ruleDefinitionFacetExpr"


    // $ANTLR start "entryRuleNameFacetExpr"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1249:1: entryRuleNameFacetExpr returns [EObject current=null] : iv_ruleNameFacetExpr= ruleNameFacetExpr EOF ;
    public final EObject entryRuleNameFacetExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleNameFacetExpr = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1250:2: (iv_ruleNameFacetExpr= ruleNameFacetExpr EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1251:2: iv_ruleNameFacetExpr= ruleNameFacetExpr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getNameFacetExprRule()); 
            }
            pushFollow(FOLLOW_ruleNameFacetExpr_in_entryRuleNameFacetExpr2700);
            iv_ruleNameFacetExpr=ruleNameFacetExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleNameFacetExpr; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleNameFacetExpr2710); if (state.failed) return current;

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
    // $ANTLR end "entryRuleNameFacetExpr"


    // $ANTLR start "ruleNameFacetExpr"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1258:1: ruleNameFacetExpr returns [EObject current=null] : (otherlv_0= 'name:' ( ( (lv_name_1_0= RULE_ID ) ) | ( (lv_name_2_0= RULE_STRING ) ) | ( (lv_name_3_0= ruleBuiltIn ) ) ) ) ;
    public final EObject ruleNameFacetExpr() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token lv_name_2_0=null;
        AntlrDatatypeRuleToken lv_name_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1261:28: ( (otherlv_0= 'name:' ( ( (lv_name_1_0= RULE_ID ) ) | ( (lv_name_2_0= RULE_STRING ) ) | ( (lv_name_3_0= ruleBuiltIn ) ) ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1262:1: (otherlv_0= 'name:' ( ( (lv_name_1_0= RULE_ID ) ) | ( (lv_name_2_0= RULE_STRING ) ) | ( (lv_name_3_0= ruleBuiltIn ) ) ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1262:1: (otherlv_0= 'name:' ( ( (lv_name_1_0= RULE_ID ) ) | ( (lv_name_2_0= RULE_STRING ) ) | ( (lv_name_3_0= ruleBuiltIn ) ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1262:3: otherlv_0= 'name:' ( ( (lv_name_1_0= RULE_ID ) ) | ( (lv_name_2_0= RULE_STRING ) ) | ( (lv_name_3_0= ruleBuiltIn ) ) )
            {
            otherlv_0=(Token)match(input,46,FOLLOW_46_in_ruleNameFacetExpr2747); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_0, grammarAccess.getNameFacetExprAccess().getNameKeyword_0());
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1266:1: ( ( (lv_name_1_0= RULE_ID ) ) | ( (lv_name_2_0= RULE_STRING ) ) | ( (lv_name_3_0= ruleBuiltIn ) ) )
            int alt20=3;
            switch ( input.LA(1) ) {
            case RULE_ID:
                {
                alt20=1;
                }
                break;
            case RULE_STRING:
                {
                alt20=2;
                }
                break;
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
            case 35:
            case 36:
            case 37:
            case 38:
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1266:2: ( (lv_name_1_0= RULE_ID ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1266:2: ( (lv_name_1_0= RULE_ID ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1267:1: (lv_name_1_0= RULE_ID )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1267:1: (lv_name_1_0= RULE_ID )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1268:3: lv_name_1_0= RULE_ID
                    {
                    lv_name_1_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleNameFacetExpr2765); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			newLeafNode(lv_name_1_0, grammarAccess.getNameFacetExprAccess().getNameIDTerminalRuleCall_1_0_0()); 
                      		
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getNameFacetExprRule());
                      	        }
                             		setWithLastConsumed(
                             			current, 
                             			"name",
                              		lv_name_1_0, 
                              		"ID");
                      	    
                    }

                    }


                    }


                    }
                    break;
                case 2 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1285:6: ( (lv_name_2_0= RULE_STRING ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1285:6: ( (lv_name_2_0= RULE_STRING ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1286:1: (lv_name_2_0= RULE_STRING )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1286:1: (lv_name_2_0= RULE_STRING )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1287:3: lv_name_2_0= RULE_STRING
                    {
                    lv_name_2_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleNameFacetExpr2793); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			newLeafNode(lv_name_2_0, grammarAccess.getNameFacetExprAccess().getNameSTRINGTerminalRuleCall_1_1_0()); 
                      		
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getNameFacetExprRule());
                      	        }
                             		setWithLastConsumed(
                             			current, 
                             			"name",
                              		lv_name_2_0, 
                              		"STRING");
                      	    
                    }

                    }


                    }


                    }
                    break;
                case 3 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1304:6: ( (lv_name_3_0= ruleBuiltIn ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1304:6: ( (lv_name_3_0= ruleBuiltIn ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1305:1: (lv_name_3_0= ruleBuiltIn )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1305:1: (lv_name_3_0= ruleBuiltIn )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1306:3: lv_name_3_0= ruleBuiltIn
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getNameFacetExprAccess().getNameBuiltInParserRuleCall_1_2_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleBuiltIn_in_ruleNameFacetExpr2825);
                    lv_name_3_0=ruleBuiltIn();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getNameFacetExprRule());
                      	        }
                             		set(
                             			current, 
                             			"name",
                              		lv_name_3_0, 
                              		"BuiltIn");
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
    // $ANTLR end "ruleNameFacetExpr"


    // $ANTLR start "entryRuleReturnsFacetExpr"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1330:1: entryRuleReturnsFacetExpr returns [EObject current=null] : iv_ruleReturnsFacetExpr= ruleReturnsFacetExpr EOF ;
    public final EObject entryRuleReturnsFacetExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleReturnsFacetExpr = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1331:2: (iv_ruleReturnsFacetExpr= ruleReturnsFacetExpr EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1332:2: iv_ruleReturnsFacetExpr= ruleReturnsFacetExpr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getReturnsFacetExprRule()); 
            }
            pushFollow(FOLLOW_ruleReturnsFacetExpr_in_entryRuleReturnsFacetExpr2862);
            iv_ruleReturnsFacetExpr=ruleReturnsFacetExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleReturnsFacetExpr; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleReturnsFacetExpr2872); if (state.failed) return current;

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
    // $ANTLR end "entryRuleReturnsFacetExpr"


    // $ANTLR start "ruleReturnsFacetExpr"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1339:1: ruleReturnsFacetExpr returns [EObject current=null] : (otherlv_0= 'returns:' ( (lv_name_1_0= RULE_ID ) ) ) ;
    public final EObject ruleReturnsFacetExpr() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;

         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1342:28: ( (otherlv_0= 'returns:' ( (lv_name_1_0= RULE_ID ) ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1343:1: (otherlv_0= 'returns:' ( (lv_name_1_0= RULE_ID ) ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1343:1: (otherlv_0= 'returns:' ( (lv_name_1_0= RULE_ID ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1343:3: otherlv_0= 'returns:' ( (lv_name_1_0= RULE_ID ) )
            {
            otherlv_0=(Token)match(input,47,FOLLOW_47_in_ruleReturnsFacetExpr2909); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_0, grammarAccess.getReturnsFacetExprAccess().getReturnsKeyword_0());
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1347:1: ( (lv_name_1_0= RULE_ID ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1348:1: (lv_name_1_0= RULE_ID )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1348:1: (lv_name_1_0= RULE_ID )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1349:3: lv_name_1_0= RULE_ID
            {
            lv_name_1_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleReturnsFacetExpr2926); if (state.failed) return current;
            if ( state.backtracking==0 ) {

              			newLeafNode(lv_name_1_0, grammarAccess.getReturnsFacetExprAccess().getNameIDTerminalRuleCall_1_0()); 
              		
            }
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElement(grammarAccess.getReturnsFacetExprRule());
              	        }
                     		setWithLastConsumed(
                     			current, 
                     			"name",
                      		lv_name_1_0, 
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
    // $ANTLR end "ruleReturnsFacetExpr"


    // $ANTLR start "entryRuleActionFacetExpr"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1373:1: entryRuleActionFacetExpr returns [EObject current=null] : iv_ruleActionFacetExpr= ruleActionFacetExpr EOF ;
    public final EObject entryRuleActionFacetExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleActionFacetExpr = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1374:2: (iv_ruleActionFacetExpr= ruleActionFacetExpr EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1375:2: iv_ruleActionFacetExpr= ruleActionFacetExpr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getActionFacetExprRule()); 
            }
            pushFollow(FOLLOW_ruleActionFacetExpr_in_entryRuleActionFacetExpr2967);
            iv_ruleActionFacetExpr=ruleActionFacetExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleActionFacetExpr; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleActionFacetExpr2977); if (state.failed) return current;

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
    // $ANTLR end "entryRuleActionFacetExpr"


    // $ANTLR start "ruleActionFacetExpr"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1382:1: ruleActionFacetExpr returns [EObject current=null] : (otherlv_0= 'action:' ( ( (lv_name_1_0= RULE_ID ) ) | ( (lv_name_2_0= RULE_STRING ) ) | ( (lv_name_3_0= ruleBuiltIn ) ) ) ) ;
    public final EObject ruleActionFacetExpr() throws RecognitionException {
        EObject current = null;

        Token otherlv_0=null;
        Token lv_name_1_0=null;
        Token lv_name_2_0=null;
        AntlrDatatypeRuleToken lv_name_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1385:28: ( (otherlv_0= 'action:' ( ( (lv_name_1_0= RULE_ID ) ) | ( (lv_name_2_0= RULE_STRING ) ) | ( (lv_name_3_0= ruleBuiltIn ) ) ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1386:1: (otherlv_0= 'action:' ( ( (lv_name_1_0= RULE_ID ) ) | ( (lv_name_2_0= RULE_STRING ) ) | ( (lv_name_3_0= ruleBuiltIn ) ) ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1386:1: (otherlv_0= 'action:' ( ( (lv_name_1_0= RULE_ID ) ) | ( (lv_name_2_0= RULE_STRING ) ) | ( (lv_name_3_0= ruleBuiltIn ) ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1386:3: otherlv_0= 'action:' ( ( (lv_name_1_0= RULE_ID ) ) | ( (lv_name_2_0= RULE_STRING ) ) | ( (lv_name_3_0= ruleBuiltIn ) ) )
            {
            otherlv_0=(Token)match(input,48,FOLLOW_48_in_ruleActionFacetExpr3014); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_0, grammarAccess.getActionFacetExprAccess().getActionKeyword_0());
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1390:1: ( ( (lv_name_1_0= RULE_ID ) ) | ( (lv_name_2_0= RULE_STRING ) ) | ( (lv_name_3_0= ruleBuiltIn ) ) )
            int alt21=3;
            switch ( input.LA(1) ) {
            case RULE_ID:
                {
                alt21=1;
                }
                break;
            case RULE_STRING:
                {
                alt21=2;
                }
                break;
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
            case 35:
            case 36:
            case 37:
            case 38:
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1390:2: ( (lv_name_1_0= RULE_ID ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1390:2: ( (lv_name_1_0= RULE_ID ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1391:1: (lv_name_1_0= RULE_ID )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1391:1: (lv_name_1_0= RULE_ID )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1392:3: lv_name_1_0= RULE_ID
                    {
                    lv_name_1_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleActionFacetExpr3032); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			newLeafNode(lv_name_1_0, grammarAccess.getActionFacetExprAccess().getNameIDTerminalRuleCall_1_0_0()); 
                      		
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getActionFacetExprRule());
                      	        }
                             		setWithLastConsumed(
                             			current, 
                             			"name",
                              		lv_name_1_0, 
                              		"ID");
                      	    
                    }

                    }


                    }


                    }
                    break;
                case 2 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1409:6: ( (lv_name_2_0= RULE_STRING ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1409:6: ( (lv_name_2_0= RULE_STRING ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1410:1: (lv_name_2_0= RULE_STRING )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1410:1: (lv_name_2_0= RULE_STRING )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1411:3: lv_name_2_0= RULE_STRING
                    {
                    lv_name_2_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleActionFacetExpr3060); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      			newLeafNode(lv_name_2_0, grammarAccess.getActionFacetExprAccess().getNameSTRINGTerminalRuleCall_1_1_0()); 
                      		
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getActionFacetExprRule());
                      	        }
                             		setWithLastConsumed(
                             			current, 
                             			"name",
                              		lv_name_2_0, 
                              		"STRING");
                      	    
                    }

                    }


                    }


                    }
                    break;
                case 3 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1428:6: ( (lv_name_3_0= ruleBuiltIn ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1428:6: ( (lv_name_3_0= ruleBuiltIn ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1429:1: (lv_name_3_0= ruleBuiltIn )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1429:1: (lv_name_3_0= ruleBuiltIn )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1430:3: lv_name_3_0= ruleBuiltIn
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getActionFacetExprAccess().getNameBuiltInParserRuleCall_1_2_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleBuiltIn_in_ruleActionFacetExpr3092);
                    lv_name_3_0=ruleBuiltIn();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getActionFacetExprRule());
                      	        }
                             		set(
                             			current, 
                             			"name",
                              		lv_name_3_0, 
                              		"BuiltIn");
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
    // $ANTLR end "ruleActionFacetExpr"


    // $ANTLR start "entryRuleFunctionFacetExpr"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1454:1: entryRuleFunctionFacetExpr returns [EObject current=null] : iv_ruleFunctionFacetExpr= ruleFunctionFacetExpr EOF ;
    public final EObject entryRuleFunctionFacetExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleFunctionFacetExpr = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1455:2: (iv_ruleFunctionFacetExpr= ruleFunctionFacetExpr EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1456:2: iv_ruleFunctionFacetExpr= ruleFunctionFacetExpr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getFunctionFacetExprRule()); 
            }
            pushFollow(FOLLOW_ruleFunctionFacetExpr_in_entryRuleFunctionFacetExpr3129);
            iv_ruleFunctionFacetExpr=ruleFunctionFacetExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleFunctionFacetExpr; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleFunctionFacetExpr3139); if (state.failed) return current;

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
    // $ANTLR end "entryRuleFunctionFacetExpr"


    // $ANTLR start "ruleFunctionFacetExpr"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1463:1: ruleFunctionFacetExpr returns [EObject current=null] : ( ( (lv_key_0_0= ruleFunctionGamlFacetRef ) ) otherlv_1= '{' ( (lv_expr_2_0= ruleExpression ) ) otherlv_3= '}' ) ;
    public final EObject ruleFunctionFacetExpr() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        EObject lv_key_0_0 = null;

        EObject lv_expr_2_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1466:28: ( ( ( (lv_key_0_0= ruleFunctionGamlFacetRef ) ) otherlv_1= '{' ( (lv_expr_2_0= ruleExpression ) ) otherlv_3= '}' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1467:1: ( ( (lv_key_0_0= ruleFunctionGamlFacetRef ) ) otherlv_1= '{' ( (lv_expr_2_0= ruleExpression ) ) otherlv_3= '}' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1467:1: ( ( (lv_key_0_0= ruleFunctionGamlFacetRef ) ) otherlv_1= '{' ( (lv_expr_2_0= ruleExpression ) ) otherlv_3= '}' )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1467:2: ( (lv_key_0_0= ruleFunctionGamlFacetRef ) ) otherlv_1= '{' ( (lv_expr_2_0= ruleExpression ) ) otherlv_3= '}'
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1467:2: ( (lv_key_0_0= ruleFunctionGamlFacetRef ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1468:1: (lv_key_0_0= ruleFunctionGamlFacetRef )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1468:1: (lv_key_0_0= ruleFunctionGamlFacetRef )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1469:3: lv_key_0_0= ruleFunctionGamlFacetRef
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getFunctionFacetExprAccess().getKeyFunctionGamlFacetRefParserRuleCall_0_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleFunctionGamlFacetRef_in_ruleFunctionFacetExpr3185);
            lv_key_0_0=ruleFunctionGamlFacetRef();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getFunctionFacetExprRule());
              	        }
                     		set(
                     			current, 
                     			"key",
                      		lv_key_0_0, 
                      		"FunctionGamlFacetRef");
              	        afterParserOrEnumRuleCall();
              	    
            }

            }


            }

            otherlv_1=(Token)match(input,49,FOLLOW_49_in_ruleFunctionFacetExpr3197); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_1, grammarAccess.getFunctionFacetExprAccess().getLeftCurlyBracketKeyword_1());
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1489:1: ( (lv_expr_2_0= ruleExpression ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1490:1: (lv_expr_2_0= ruleExpression )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1490:1: (lv_expr_2_0= ruleExpression )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1491:3: lv_expr_2_0= ruleExpression
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getFunctionFacetExprAccess().getExprExpressionParserRuleCall_2_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleExpression_in_ruleFunctionFacetExpr3218);
            lv_expr_2_0=ruleExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {

              	        if (current==null) {
              	            current = createModelElementForParent(grammarAccess.getFunctionFacetExprRule());
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

            otherlv_3=(Token)match(input,16,FOLLOW_16_in_ruleFunctionFacetExpr3230); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_3, grammarAccess.getFunctionFacetExprAccess().getRightCurlyBracketKeyword_3());
                  
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
    // $ANTLR end "ruleFunctionFacetExpr"


    // $ANTLR start "entryRuleBlock"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1519:1: entryRuleBlock returns [EObject current=null] : iv_ruleBlock= ruleBlock EOF ;
    public final EObject entryRuleBlock() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleBlock = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1520:2: (iv_ruleBlock= ruleBlock EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1521:2: iv_ruleBlock= ruleBlock EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getBlockRule()); 
            }
            pushFollow(FOLLOW_ruleBlock_in_entryRuleBlock3266);
            iv_ruleBlock=ruleBlock();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleBlock; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleBlock3276); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1528:1: ruleBlock returns [EObject current=null] : ( () otherlv_1= '{' ( (lv_statements_2_0= ruleStatement ) )* otherlv_3= '}' ) ;
    public final EObject ruleBlock() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        EObject lv_statements_2_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1531:28: ( ( () otherlv_1= '{' ( (lv_statements_2_0= ruleStatement ) )* otherlv_3= '}' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1532:1: ( () otherlv_1= '{' ( (lv_statements_2_0= ruleStatement ) )* otherlv_3= '}' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1532:1: ( () otherlv_1= '{' ( (lv_statements_2_0= ruleStatement ) )* otherlv_3= '}' )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1532:2: () otherlv_1= '{' ( (lv_statements_2_0= ruleStatement ) )* otherlv_3= '}'
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1532:2: ()
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1533:5: 
            {
            if ( state.backtracking==0 ) {

                      current = forceCreateModelElement(
                          grammarAccess.getBlockAccess().getBlockAction_0(),
                          current);
                  
            }

            }

            otherlv_1=(Token)match(input,49,FOLLOW_49_in_ruleBlock3322); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_1, grammarAccess.getBlockAccess().getLeftCurlyBracketKeyword_1());
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1542:1: ( (lv_statements_2_0= ruleStatement ) )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==RULE_ID||(LA22_0>=22 && LA22_0<=38)||LA22_0==40) ) {
                    alt22=1;
                }


                switch (alt22) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1543:1: (lv_statements_2_0= ruleStatement )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1543:1: (lv_statements_2_0= ruleStatement )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1544:3: lv_statements_2_0= ruleStatement
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getBlockAccess().getStatementsStatementParserRuleCall_2_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleStatement_in_ruleBlock3343);
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
            	    break loop22;
                }
            } while (true);

            otherlv_3=(Token)match(input,16,FOLLOW_16_in_ruleBlock3356); if (state.failed) return current;
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1572:1: entryRuleExpression returns [EObject current=null] : iv_ruleExpression= ruleExpression EOF ;
    public final EObject entryRuleExpression() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleExpression = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1573:2: (iv_ruleExpression= ruleExpression EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1574:2: iv_ruleExpression= ruleExpression EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getExpressionRule()); 
            }
            pushFollow(FOLLOW_ruleExpression_in_entryRuleExpression3392);
            iv_ruleExpression=ruleExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleExpression; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleExpression3402); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1581:1: ruleExpression returns [EObject current=null] : this_TernExp_0= ruleTernExp ;
    public final EObject ruleExpression() throws RecognitionException {
        EObject current = null;

        EObject this_TernExp_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1584:28: (this_TernExp_0= ruleTernExp )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1586:5: this_TernExp_0= ruleTernExp
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getExpressionAccess().getTernExpParserRuleCall()); 
                  
            }
            pushFollow(FOLLOW_ruleTernExp_in_ruleExpression3448);
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1602:1: entryRuleTernExp returns [EObject current=null] : iv_ruleTernExp= ruleTernExp EOF ;
    public final EObject entryRuleTernExp() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTernExp = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1603:2: (iv_ruleTernExp= ruleTernExp EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1604:2: iv_ruleTernExp= ruleTernExp EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getTernExpRule()); 
            }
            pushFollow(FOLLOW_ruleTernExp_in_entryRuleTernExp3482);
            iv_ruleTernExp=ruleTernExp();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleTernExp; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleTernExp3492); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1611:1: ruleTernExp returns [EObject current=null] : (this_OrExp_0= ruleOrExp ( () ( (lv_op_2_0= '?' ) ) ( (lv_right_3_0= ruleOrExp ) ) otherlv_4= ':' ( (lv_ifFalse_5_0= ruleOrExp ) ) )? ) ;
    public final EObject ruleTernExp() throws RecognitionException {
        EObject current = null;

        Token lv_op_2_0=null;
        Token otherlv_4=null;
        EObject this_OrExp_0 = null;

        EObject lv_right_3_0 = null;

        EObject lv_ifFalse_5_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1614:28: ( (this_OrExp_0= ruleOrExp ( () ( (lv_op_2_0= '?' ) ) ( (lv_right_3_0= ruleOrExp ) ) otherlv_4= ':' ( (lv_ifFalse_5_0= ruleOrExp ) ) )? ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1615:1: (this_OrExp_0= ruleOrExp ( () ( (lv_op_2_0= '?' ) ) ( (lv_right_3_0= ruleOrExp ) ) otherlv_4= ':' ( (lv_ifFalse_5_0= ruleOrExp ) ) )? )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1615:1: (this_OrExp_0= ruleOrExp ( () ( (lv_op_2_0= '?' ) ) ( (lv_right_3_0= ruleOrExp ) ) otherlv_4= ':' ( (lv_ifFalse_5_0= ruleOrExp ) ) )? )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1616:5: this_OrExp_0= ruleOrExp ( () ( (lv_op_2_0= '?' ) ) ( (lv_right_3_0= ruleOrExp ) ) otherlv_4= ':' ( (lv_ifFalse_5_0= ruleOrExp ) ) )?
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getTernExpAccess().getOrExpParserRuleCall_0()); 
                  
            }
            pushFollow(FOLLOW_ruleOrExp_in_ruleTernExp3539);
            this_OrExp_0=ruleOrExp();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_OrExp_0; 
                      afterParserOrEnumRuleCall();
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1624:1: ( () ( (lv_op_2_0= '?' ) ) ( (lv_right_3_0= ruleOrExp ) ) otherlv_4= ':' ( (lv_ifFalse_5_0= ruleOrExp ) ) )?
            int alt23=2;
            int LA23_0 = input.LA(1);

            if ( (LA23_0==50) ) {
                alt23=1;
            }
            switch (alt23) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1624:2: () ( (lv_op_2_0= '?' ) ) ( (lv_right_3_0= ruleOrExp ) ) otherlv_4= ':' ( (lv_ifFalse_5_0= ruleOrExp ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1624:2: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1625:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElementAndSet(
                                  grammarAccess.getTernExpAccess().getTernExpLeftAction_1_0(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1630:2: ( (lv_op_2_0= '?' ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1631:1: (lv_op_2_0= '?' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1631:1: (lv_op_2_0= '?' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1632:3: lv_op_2_0= '?'
                    {
                    lv_op_2_0=(Token)match(input,50,FOLLOW_50_in_ruleTernExp3566); if (state.failed) return current;
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

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1645:2: ( (lv_right_3_0= ruleOrExp ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1646:1: (lv_right_3_0= ruleOrExp )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1646:1: (lv_right_3_0= ruleOrExp )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1647:3: lv_right_3_0= ruleOrExp
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getTernExpAccess().getRightOrExpParserRuleCall_1_2_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleOrExp_in_ruleTernExp3600);
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

                    otherlv_4=(Token)match(input,42,FOLLOW_42_in_ruleTernExp3612); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_4, grammarAccess.getTernExpAccess().getColonKeyword_1_3());
                          
                    }
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1667:1: ( (lv_ifFalse_5_0= ruleOrExp ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1668:1: (lv_ifFalse_5_0= ruleOrExp )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1668:1: (lv_ifFalse_5_0= ruleOrExp )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1669:3: lv_ifFalse_5_0= ruleOrExp
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getTernExpAccess().getIfFalseOrExpParserRuleCall_1_4_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleOrExp_in_ruleTernExp3633);
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1693:1: entryRuleOrExp returns [EObject current=null] : iv_ruleOrExp= ruleOrExp EOF ;
    public final EObject entryRuleOrExp() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleOrExp = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1694:2: (iv_ruleOrExp= ruleOrExp EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1695:2: iv_ruleOrExp= ruleOrExp EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getOrExpRule()); 
            }
            pushFollow(FOLLOW_ruleOrExp_in_entryRuleOrExp3671);
            iv_ruleOrExp=ruleOrExp();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleOrExp; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleOrExp3681); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1702:1: ruleOrExp returns [EObject current=null] : (this_AndExp_0= ruleAndExp ( () ( (lv_op_2_0= 'or' ) ) ( (lv_right_3_0= ruleAndExp ) ) )* ) ;
    public final EObject ruleOrExp() throws RecognitionException {
        EObject current = null;

        Token lv_op_2_0=null;
        EObject this_AndExp_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1705:28: ( (this_AndExp_0= ruleAndExp ( () ( (lv_op_2_0= 'or' ) ) ( (lv_right_3_0= ruleAndExp ) ) )* ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1706:1: (this_AndExp_0= ruleAndExp ( () ( (lv_op_2_0= 'or' ) ) ( (lv_right_3_0= ruleAndExp ) ) )* )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1706:1: (this_AndExp_0= ruleAndExp ( () ( (lv_op_2_0= 'or' ) ) ( (lv_right_3_0= ruleAndExp ) ) )* )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1707:5: this_AndExp_0= ruleAndExp ( () ( (lv_op_2_0= 'or' ) ) ( (lv_right_3_0= ruleAndExp ) ) )*
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getOrExpAccess().getAndExpParserRuleCall_0()); 
                  
            }
            pushFollow(FOLLOW_ruleAndExp_in_ruleOrExp3728);
            this_AndExp_0=ruleAndExp();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_AndExp_0; 
                      afterParserOrEnumRuleCall();
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1715:1: ( () ( (lv_op_2_0= 'or' ) ) ( (lv_right_3_0= ruleAndExp ) ) )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( (LA24_0==51) ) {
                    alt24=1;
                }


                switch (alt24) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1715:2: () ( (lv_op_2_0= 'or' ) ) ( (lv_right_3_0= ruleAndExp ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1715:2: ()
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1716:5: 
            	    {
            	    if ( state.backtracking==0 ) {

            	              current = forceCreateModelElementAndSet(
            	                  grammarAccess.getOrExpAccess().getExpressionLeftAction_1_0(),
            	                  current);
            	          
            	    }

            	    }

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1721:2: ( (lv_op_2_0= 'or' ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1722:1: (lv_op_2_0= 'or' )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1722:1: (lv_op_2_0= 'or' )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1723:3: lv_op_2_0= 'or'
            	    {
            	    lv_op_2_0=(Token)match(input,51,FOLLOW_51_in_ruleOrExp3755); if (state.failed) return current;
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

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1736:2: ( (lv_right_3_0= ruleAndExp ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1737:1: (lv_right_3_0= ruleAndExp )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1737:1: (lv_right_3_0= ruleAndExp )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1738:3: lv_right_3_0= ruleAndExp
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getOrExpAccess().getRightAndExpParserRuleCall_1_2_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleAndExp_in_ruleOrExp3789);
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
            	    break loop24;
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1762:1: entryRuleAndExp returns [EObject current=null] : iv_ruleAndExp= ruleAndExp EOF ;
    public final EObject entryRuleAndExp() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAndExp = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1763:2: (iv_ruleAndExp= ruleAndExp EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1764:2: iv_ruleAndExp= ruleAndExp EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAndExpRule()); 
            }
            pushFollow(FOLLOW_ruleAndExp_in_entryRuleAndExp3827);
            iv_ruleAndExp=ruleAndExp();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAndExp; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleAndExp3837); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1771:1: ruleAndExp returns [EObject current=null] : (this_Relational_0= ruleRelational ( () ( (lv_op_2_0= 'and' ) ) ( (lv_right_3_0= ruleRelational ) ) )* ) ;
    public final EObject ruleAndExp() throws RecognitionException {
        EObject current = null;

        Token lv_op_2_0=null;
        EObject this_Relational_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1774:28: ( (this_Relational_0= ruleRelational ( () ( (lv_op_2_0= 'and' ) ) ( (lv_right_3_0= ruleRelational ) ) )* ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1775:1: (this_Relational_0= ruleRelational ( () ( (lv_op_2_0= 'and' ) ) ( (lv_right_3_0= ruleRelational ) ) )* )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1775:1: (this_Relational_0= ruleRelational ( () ( (lv_op_2_0= 'and' ) ) ( (lv_right_3_0= ruleRelational ) ) )* )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1776:5: this_Relational_0= ruleRelational ( () ( (lv_op_2_0= 'and' ) ) ( (lv_right_3_0= ruleRelational ) ) )*
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getAndExpAccess().getRelationalParserRuleCall_0()); 
                  
            }
            pushFollow(FOLLOW_ruleRelational_in_ruleAndExp3884);
            this_Relational_0=ruleRelational();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_Relational_0; 
                      afterParserOrEnumRuleCall();
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1784:1: ( () ( (lv_op_2_0= 'and' ) ) ( (lv_right_3_0= ruleRelational ) ) )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==52) ) {
                    alt25=1;
                }


                switch (alt25) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1784:2: () ( (lv_op_2_0= 'and' ) ) ( (lv_right_3_0= ruleRelational ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1784:2: ()
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1785:5: 
            	    {
            	    if ( state.backtracking==0 ) {

            	              current = forceCreateModelElementAndSet(
            	                  grammarAccess.getAndExpAccess().getExpressionLeftAction_1_0(),
            	                  current);
            	          
            	    }

            	    }

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1790:2: ( (lv_op_2_0= 'and' ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1791:1: (lv_op_2_0= 'and' )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1791:1: (lv_op_2_0= 'and' )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1792:3: lv_op_2_0= 'and'
            	    {
            	    lv_op_2_0=(Token)match(input,52,FOLLOW_52_in_ruleAndExp3911); if (state.failed) return current;
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

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1805:2: ( (lv_right_3_0= ruleRelational ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1806:1: (lv_right_3_0= ruleRelational )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1806:1: (lv_right_3_0= ruleRelational )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1807:3: lv_right_3_0= ruleRelational
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getAndExpAccess().getRightRelationalParserRuleCall_1_2_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleRelational_in_ruleAndExp3945);
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
            	    break loop25;
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1831:1: entryRuleRelational returns [EObject current=null] : iv_ruleRelational= ruleRelational EOF ;
    public final EObject entryRuleRelational() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleRelational = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1832:2: (iv_ruleRelational= ruleRelational EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1833:2: iv_ruleRelational= ruleRelational EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getRelationalRule()); 
            }
            pushFollow(FOLLOW_ruleRelational_in_entryRuleRelational3983);
            iv_ruleRelational=ruleRelational();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleRelational; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleRelational3993); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1840:1: ruleRelational returns [EObject current=null] : (this_PairExpr_0= rulePairExpr ( ( () ( ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) ) ) ) ( (lv_right_3_0= rulePairExpr ) ) )? ) ;
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
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1843:28: ( (this_PairExpr_0= rulePairExpr ( ( () ( ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) ) ) ) ( (lv_right_3_0= rulePairExpr ) ) )? ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1844:1: (this_PairExpr_0= rulePairExpr ( ( () ( ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) ) ) ) ( (lv_right_3_0= rulePairExpr ) ) )? )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1844:1: (this_PairExpr_0= rulePairExpr ( ( () ( ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) ) ) ) ( (lv_right_3_0= rulePairExpr ) ) )? )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1845:5: this_PairExpr_0= rulePairExpr ( ( () ( ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) ) ) ) ( (lv_right_3_0= rulePairExpr ) ) )?
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getRelationalAccess().getPairExprParserRuleCall_0()); 
                  
            }
            pushFollow(FOLLOW_rulePairExpr_in_ruleRelational4040);
            this_PairExpr_0=rulePairExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_PairExpr_0; 
                      afterParserOrEnumRuleCall();
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1853:1: ( ( () ( ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) ) ) ) ( (lv_right_3_0= rulePairExpr ) ) )?
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( ((LA27_0>=53 && LA27_0<=58)) ) {
                alt27=1;
            }
            switch (alt27) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1853:2: ( () ( ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) ) ) ) ( (lv_right_3_0= rulePairExpr ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1853:2: ( () ( ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1853:3: () ( ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1853:3: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1854:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElementAndSet(
                                  grammarAccess.getRelationalAccess().getExpressionLeftAction_1_0_0(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1859:2: ( ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1860:1: ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1860:1: ( (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1861:1: (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1861:1: (lv_op_2_1= '!=' | lv_op_2_2= '=' | lv_op_2_3= '>=' | lv_op_2_4= '<=' | lv_op_2_5= '<' | lv_op_2_6= '>' )
                    int alt26=6;
                    switch ( input.LA(1) ) {
                    case 53:
                        {
                        alt26=1;
                        }
                        break;
                    case 54:
                        {
                        alt26=2;
                        }
                        break;
                    case 55:
                        {
                        alt26=3;
                        }
                        break;
                    case 56:
                        {
                        alt26=4;
                        }
                        break;
                    case 57:
                        {
                        alt26=5;
                        }
                        break;
                    case 58:
                        {
                        alt26=6;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return current;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 26, 0, input);

                        throw nvae;
                    }

                    switch (alt26) {
                        case 1 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1862:3: lv_op_2_1= '!='
                            {
                            lv_op_2_1=(Token)match(input,53,FOLLOW_53_in_ruleRelational4070); if (state.failed) return current;
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
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1874:8: lv_op_2_2= '='
                            {
                            lv_op_2_2=(Token)match(input,54,FOLLOW_54_in_ruleRelational4099); if (state.failed) return current;
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
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1886:8: lv_op_2_3= '>='
                            {
                            lv_op_2_3=(Token)match(input,55,FOLLOW_55_in_ruleRelational4128); if (state.failed) return current;
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
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1898:8: lv_op_2_4= '<='
                            {
                            lv_op_2_4=(Token)match(input,56,FOLLOW_56_in_ruleRelational4157); if (state.failed) return current;
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
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1910:8: lv_op_2_5= '<'
                            {
                            lv_op_2_5=(Token)match(input,57,FOLLOW_57_in_ruleRelational4186); if (state.failed) return current;
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
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1922:8: lv_op_2_6= '>'
                            {
                            lv_op_2_6=(Token)match(input,58,FOLLOW_58_in_ruleRelational4215); if (state.failed) return current;
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

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1937:3: ( (lv_right_3_0= rulePairExpr ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1938:1: (lv_right_3_0= rulePairExpr )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1938:1: (lv_right_3_0= rulePairExpr )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1939:3: lv_right_3_0= rulePairExpr
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getRelationalAccess().getRightPairExprParserRuleCall_1_1_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_rulePairExpr_in_ruleRelational4253);
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1963:1: entryRuleArgPairExpr returns [EObject current=null] : iv_ruleArgPairExpr= ruleArgPairExpr EOF ;
    public final EObject entryRuleArgPairExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleArgPairExpr = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1964:2: (iv_ruleArgPairExpr= ruleArgPairExpr EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1965:2: iv_ruleArgPairExpr= ruleArgPairExpr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getArgPairExprRule()); 
            }
            pushFollow(FOLLOW_ruleArgPairExpr_in_entryRuleArgPairExpr4291);
            iv_ruleArgPairExpr=ruleArgPairExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleArgPairExpr; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleArgPairExpr4301); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1972:1: ruleArgPairExpr returns [EObject current=null] : ( () ( (lv_arg_1_0= RULE_ID ) ) ( (lv_op_2_0= '::' ) ) ( (lv_right_3_0= ruleAddition ) ) ) ;
    public final EObject ruleArgPairExpr() throws RecognitionException {
        EObject current = null;

        Token lv_arg_1_0=null;
        Token lv_op_2_0=null;
        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1975:28: ( ( () ( (lv_arg_1_0= RULE_ID ) ) ( (lv_op_2_0= '::' ) ) ( (lv_right_3_0= ruleAddition ) ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1976:1: ( () ( (lv_arg_1_0= RULE_ID ) ) ( (lv_op_2_0= '::' ) ) ( (lv_right_3_0= ruleAddition ) ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1976:1: ( () ( (lv_arg_1_0= RULE_ID ) ) ( (lv_op_2_0= '::' ) ) ( (lv_right_3_0= ruleAddition ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1976:2: () ( (lv_arg_1_0= RULE_ID ) ) ( (lv_op_2_0= '::' ) ) ( (lv_right_3_0= ruleAddition ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1976:2: ()
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1977:5: 
            {
            if ( state.backtracking==0 ) {

                      current = forceCreateModelElement(
                          grammarAccess.getArgPairExprAccess().getArgPairExprAction_0(),
                          current);
                  
            }

            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1982:2: ( (lv_arg_1_0= RULE_ID ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1983:1: (lv_arg_1_0= RULE_ID )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1983:1: (lv_arg_1_0= RULE_ID )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:1984:3: lv_arg_1_0= RULE_ID
            {
            lv_arg_1_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleArgPairExpr4352); if (state.failed) return current;
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

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2000:2: ( (lv_op_2_0= '::' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2001:1: (lv_op_2_0= '::' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2001:1: (lv_op_2_0= '::' )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2002:3: lv_op_2_0= '::'
            {
            lv_op_2_0=(Token)match(input,59,FOLLOW_59_in_ruleArgPairExpr4375); if (state.failed) return current;
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

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2015:2: ( (lv_right_3_0= ruleAddition ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2016:1: (lv_right_3_0= ruleAddition )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2016:1: (lv_right_3_0= ruleAddition )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2017:3: lv_right_3_0= ruleAddition
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getArgPairExprAccess().getRightAdditionParserRuleCall_3_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleAddition_in_ruleArgPairExpr4409);
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2041:1: entryRulePairExpr returns [EObject current=null] : iv_rulePairExpr= rulePairExpr EOF ;
    public final EObject entryRulePairExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePairExpr = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2042:2: (iv_rulePairExpr= rulePairExpr EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2043:2: iv_rulePairExpr= rulePairExpr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getPairExprRule()); 
            }
            pushFollow(FOLLOW_rulePairExpr_in_entryRulePairExpr4445);
            iv_rulePairExpr=rulePairExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_rulePairExpr; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRulePairExpr4455); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2050:1: rulePairExpr returns [EObject current=null] : ( ( ( ruleArgPairExpr )=>this_ArgPairExpr_0= ruleArgPairExpr ) | (this_Addition_1= ruleAddition ( ( () ( (lv_op_3_0= '::' ) ) ) ( (lv_right_4_0= ruleAddition ) ) )? ) ) ;
    public final EObject rulePairExpr() throws RecognitionException {
        EObject current = null;

        Token lv_op_3_0=null;
        EObject this_ArgPairExpr_0 = null;

        EObject this_Addition_1 = null;

        EObject lv_right_4_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2053:28: ( ( ( ( ruleArgPairExpr )=>this_ArgPairExpr_0= ruleArgPairExpr ) | (this_Addition_1= ruleAddition ( ( () ( (lv_op_3_0= '::' ) ) ) ( (lv_right_4_0= ruleAddition ) ) )? ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2054:1: ( ( ( ruleArgPairExpr )=>this_ArgPairExpr_0= ruleArgPairExpr ) | (this_Addition_1= ruleAddition ( ( () ( (lv_op_3_0= '::' ) ) ) ( (lv_right_4_0= ruleAddition ) ) )? ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2054:1: ( ( ( ruleArgPairExpr )=>this_ArgPairExpr_0= ruleArgPairExpr ) | (this_Addition_1= ruleAddition ( ( () ( (lv_op_3_0= '::' ) ) ) ( (lv_right_4_0= ruleAddition ) ) )? ) )
            int alt29=2;
            alt29 = dfa29.predict(input);
            switch (alt29) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2054:2: ( ( ruleArgPairExpr )=>this_ArgPairExpr_0= ruleArgPairExpr )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2054:2: ( ( ruleArgPairExpr )=>this_ArgPairExpr_0= ruleArgPairExpr )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2054:3: ( ruleArgPairExpr )=>this_ArgPairExpr_0= ruleArgPairExpr
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getPairExprAccess().getArgPairExprParserRuleCall_0()); 
                          
                    }
                    pushFollow(FOLLOW_ruleArgPairExpr_in_rulePairExpr4508);
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2064:6: (this_Addition_1= ruleAddition ( ( () ( (lv_op_3_0= '::' ) ) ) ( (lv_right_4_0= ruleAddition ) ) )? )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2064:6: (this_Addition_1= ruleAddition ( ( () ( (lv_op_3_0= '::' ) ) ) ( (lv_right_4_0= ruleAddition ) ) )? )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2065:5: this_Addition_1= ruleAddition ( ( () ( (lv_op_3_0= '::' ) ) ) ( (lv_right_4_0= ruleAddition ) ) )?
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getPairExprAccess().getAdditionParserRuleCall_1_0()); 
                          
                    }
                    pushFollow(FOLLOW_ruleAddition_in_rulePairExpr4537);
                    this_Addition_1=ruleAddition();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_Addition_1; 
                              afterParserOrEnumRuleCall();
                          
                    }
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2073:1: ( ( () ( (lv_op_3_0= '::' ) ) ) ( (lv_right_4_0= ruleAddition ) ) )?
                    int alt28=2;
                    int LA28_0 = input.LA(1);

                    if ( (LA28_0==59) ) {
                        alt28=1;
                    }
                    switch (alt28) {
                        case 1 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2073:2: ( () ( (lv_op_3_0= '::' ) ) ) ( (lv_right_4_0= ruleAddition ) )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2073:2: ( () ( (lv_op_3_0= '::' ) ) )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2073:3: () ( (lv_op_3_0= '::' ) )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2073:3: ()
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2074:5: 
                            {
                            if ( state.backtracking==0 ) {

                                      current = forceCreateModelElementAndSet(
                                          grammarAccess.getPairExprAccess().getPairExprLeftAction_1_1_0_0(),
                                          current);
                                  
                            }

                            }

                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2079:2: ( (lv_op_3_0= '::' ) )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2080:1: (lv_op_3_0= '::' )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2080:1: (lv_op_3_0= '::' )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2081:3: lv_op_3_0= '::'
                            {
                            lv_op_3_0=(Token)match(input,59,FOLLOW_59_in_rulePairExpr4565); if (state.failed) return current;
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

                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2094:3: ( (lv_right_4_0= ruleAddition ) )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2095:1: (lv_right_4_0= ruleAddition )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2095:1: (lv_right_4_0= ruleAddition )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2096:3: lv_right_4_0= ruleAddition
                            {
                            if ( state.backtracking==0 ) {
                               
                              	        newCompositeNode(grammarAccess.getPairExprAccess().getRightAdditionParserRuleCall_1_1_1_0()); 
                              	    
                            }
                            pushFollow(FOLLOW_ruleAddition_in_rulePairExpr4600);
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2120:1: entryRuleAddition returns [EObject current=null] : iv_ruleAddition= ruleAddition EOF ;
    public final EObject entryRuleAddition() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAddition = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2121:2: (iv_ruleAddition= ruleAddition EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2122:2: iv_ruleAddition= ruleAddition EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAdditionRule()); 
            }
            pushFollow(FOLLOW_ruleAddition_in_entryRuleAddition4639);
            iv_ruleAddition=ruleAddition();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAddition; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleAddition4649); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2129:1: ruleAddition returns [EObject current=null] : (this_Multiplication_0= ruleMultiplication ( ( () ( ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) ) ) ) ( (lv_right_3_0= ruleMultiplication ) ) )* ) ;
    public final EObject ruleAddition() throws RecognitionException {
        EObject current = null;

        Token lv_op_2_1=null;
        Token lv_op_2_2=null;
        EObject this_Multiplication_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2132:28: ( (this_Multiplication_0= ruleMultiplication ( ( () ( ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) ) ) ) ( (lv_right_3_0= ruleMultiplication ) ) )* ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2133:1: (this_Multiplication_0= ruleMultiplication ( ( () ( ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) ) ) ) ( (lv_right_3_0= ruleMultiplication ) ) )* )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2133:1: (this_Multiplication_0= ruleMultiplication ( ( () ( ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) ) ) ) ( (lv_right_3_0= ruleMultiplication ) ) )* )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2134:5: this_Multiplication_0= ruleMultiplication ( ( () ( ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) ) ) ) ( (lv_right_3_0= ruleMultiplication ) ) )*
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getAdditionAccess().getMultiplicationParserRuleCall_0()); 
                  
            }
            pushFollow(FOLLOW_ruleMultiplication_in_ruleAddition4696);
            this_Multiplication_0=ruleMultiplication();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_Multiplication_0; 
                      afterParserOrEnumRuleCall();
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2142:1: ( ( () ( ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) ) ) ) ( (lv_right_3_0= ruleMultiplication ) ) )*
            loop31:
            do {
                int alt31=2;
                int LA31_0 = input.LA(1);

                if ( ((LA31_0>=60 && LA31_0<=61)) ) {
                    alt31=1;
                }


                switch (alt31) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2142:2: ( () ( ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) ) ) ) ( (lv_right_3_0= ruleMultiplication ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2142:2: ( () ( ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) ) ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2142:3: () ( ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2142:3: ()
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2143:5: 
            	    {
            	    if ( state.backtracking==0 ) {

            	              current = forceCreateModelElementAndSet(
            	                  grammarAccess.getAdditionAccess().getExpressionLeftAction_1_0_0(),
            	                  current);
            	          
            	    }

            	    }

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2148:2: ( ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2149:1: ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2149:1: ( (lv_op_2_1= '+' | lv_op_2_2= '-' ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2150:1: (lv_op_2_1= '+' | lv_op_2_2= '-' )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2150:1: (lv_op_2_1= '+' | lv_op_2_2= '-' )
            	    int alt30=2;
            	    int LA30_0 = input.LA(1);

            	    if ( (LA30_0==60) ) {
            	        alt30=1;
            	    }
            	    else if ( (LA30_0==61) ) {
            	        alt30=2;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 30, 0, input);

            	        throw nvae;
            	    }
            	    switch (alt30) {
            	        case 1 :
            	            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2151:3: lv_op_2_1= '+'
            	            {
            	            lv_op_2_1=(Token)match(input,60,FOLLOW_60_in_ruleAddition4726); if (state.failed) return current;
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
            	            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2163:8: lv_op_2_2= '-'
            	            {
            	            lv_op_2_2=(Token)match(input,61,FOLLOW_61_in_ruleAddition4755); if (state.failed) return current;
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

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2178:3: ( (lv_right_3_0= ruleMultiplication ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2179:1: (lv_right_3_0= ruleMultiplication )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2179:1: (lv_right_3_0= ruleMultiplication )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2180:3: lv_right_3_0= ruleMultiplication
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getAdditionAccess().getRightMultiplicationParserRuleCall_1_1_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleMultiplication_in_ruleAddition4793);
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
            	    break loop31;
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2204:1: entryRuleMultiplication returns [EObject current=null] : iv_ruleMultiplication= ruleMultiplication EOF ;
    public final EObject entryRuleMultiplication() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleMultiplication = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2205:2: (iv_ruleMultiplication= ruleMultiplication EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2206:2: iv_ruleMultiplication= ruleMultiplication EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getMultiplicationRule()); 
            }
            pushFollow(FOLLOW_ruleMultiplication_in_entryRuleMultiplication4831);
            iv_ruleMultiplication=ruleMultiplication();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleMultiplication; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleMultiplication4841); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2213:1: ruleMultiplication returns [EObject current=null] : (this_GamlBinaryExpr_0= ruleGamlBinaryExpr ( ( () ( ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) ) ) ) ( (lv_right_3_0= ruleGamlBinaryExpr ) ) )* ) ;
    public final EObject ruleMultiplication() throws RecognitionException {
        EObject current = null;

        Token lv_op_2_1=null;
        Token lv_op_2_2=null;
        Token lv_op_2_3=null;
        EObject this_GamlBinaryExpr_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2216:28: ( (this_GamlBinaryExpr_0= ruleGamlBinaryExpr ( ( () ( ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) ) ) ) ( (lv_right_3_0= ruleGamlBinaryExpr ) ) )* ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2217:1: (this_GamlBinaryExpr_0= ruleGamlBinaryExpr ( ( () ( ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) ) ) ) ( (lv_right_3_0= ruleGamlBinaryExpr ) ) )* )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2217:1: (this_GamlBinaryExpr_0= ruleGamlBinaryExpr ( ( () ( ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) ) ) ) ( (lv_right_3_0= ruleGamlBinaryExpr ) ) )* )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2218:5: this_GamlBinaryExpr_0= ruleGamlBinaryExpr ( ( () ( ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) ) ) ) ( (lv_right_3_0= ruleGamlBinaryExpr ) ) )*
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getMultiplicationAccess().getGamlBinaryExprParserRuleCall_0()); 
                  
            }
            pushFollow(FOLLOW_ruleGamlBinaryExpr_in_ruleMultiplication4888);
            this_GamlBinaryExpr_0=ruleGamlBinaryExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_GamlBinaryExpr_0; 
                      afterParserOrEnumRuleCall();
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2226:1: ( ( () ( ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) ) ) ) ( (lv_right_3_0= ruleGamlBinaryExpr ) ) )*
            loop33:
            do {
                int alt33=2;
                int LA33_0 = input.LA(1);

                if ( ((LA33_0>=62 && LA33_0<=64)) ) {
                    alt33=1;
                }


                switch (alt33) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2226:2: ( () ( ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) ) ) ) ( (lv_right_3_0= ruleGamlBinaryExpr ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2226:2: ( () ( ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) ) ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2226:3: () ( ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2226:3: ()
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2227:5: 
            	    {
            	    if ( state.backtracking==0 ) {

            	              current = forceCreateModelElementAndSet(
            	                  grammarAccess.getMultiplicationAccess().getExpressionLeftAction_1_0_0(),
            	                  current);
            	          
            	    }

            	    }

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2232:2: ( ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2233:1: ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2233:1: ( (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2234:1: (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2234:1: (lv_op_2_1= '*' | lv_op_2_2= '/' | lv_op_2_3= '^' )
            	    int alt32=3;
            	    switch ( input.LA(1) ) {
            	    case 62:
            	        {
            	        alt32=1;
            	        }
            	        break;
            	    case 63:
            	        {
            	        alt32=2;
            	        }
            	        break;
            	    case 64:
            	        {
            	        alt32=3;
            	        }
            	        break;
            	    default:
            	        if (state.backtracking>0) {state.failed=true; return current;}
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 32, 0, input);

            	        throw nvae;
            	    }

            	    switch (alt32) {
            	        case 1 :
            	            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2235:3: lv_op_2_1= '*'
            	            {
            	            lv_op_2_1=(Token)match(input,62,FOLLOW_62_in_ruleMultiplication4918); if (state.failed) return current;
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
            	            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2247:8: lv_op_2_2= '/'
            	            {
            	            lv_op_2_2=(Token)match(input,63,FOLLOW_63_in_ruleMultiplication4947); if (state.failed) return current;
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
            	            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2259:8: lv_op_2_3= '^'
            	            {
            	            lv_op_2_3=(Token)match(input,64,FOLLOW_64_in_ruleMultiplication4976); if (state.failed) return current;
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

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2274:3: ( (lv_right_3_0= ruleGamlBinaryExpr ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2275:1: (lv_right_3_0= ruleGamlBinaryExpr )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2275:1: (lv_right_3_0= ruleGamlBinaryExpr )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2276:3: lv_right_3_0= ruleGamlBinaryExpr
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getMultiplicationAccess().getRightGamlBinaryExprParserRuleCall_1_1_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleGamlBinaryExpr_in_ruleMultiplication5014);
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
            	    break loop33;
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2300:1: entryRuleGamlBinaryExpr returns [EObject current=null] : iv_ruleGamlBinaryExpr= ruleGamlBinaryExpr EOF ;
    public final EObject entryRuleGamlBinaryExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleGamlBinaryExpr = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2301:2: (iv_ruleGamlBinaryExpr= ruleGamlBinaryExpr EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2302:2: iv_ruleGamlBinaryExpr= ruleGamlBinaryExpr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getGamlBinaryExprRule()); 
            }
            pushFollow(FOLLOW_ruleGamlBinaryExpr_in_entryRuleGamlBinaryExpr5052);
            iv_ruleGamlBinaryExpr=ruleGamlBinaryExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleGamlBinaryExpr; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleGamlBinaryExpr5062); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2309:1: ruleGamlBinaryExpr returns [EObject current=null] : (this_GamlUnitExpr_0= ruleGamlUnitExpr ( ( () ( (lv_op_2_0= RULE_ID ) ) ) ( (lv_right_3_0= ruleGamlUnitExpr ) ) )* ) ;
    public final EObject ruleGamlBinaryExpr() throws RecognitionException {
        EObject current = null;

        Token lv_op_2_0=null;
        EObject this_GamlUnitExpr_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2312:28: ( (this_GamlUnitExpr_0= ruleGamlUnitExpr ( ( () ( (lv_op_2_0= RULE_ID ) ) ) ( (lv_right_3_0= ruleGamlUnitExpr ) ) )* ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2313:1: (this_GamlUnitExpr_0= ruleGamlUnitExpr ( ( () ( (lv_op_2_0= RULE_ID ) ) ) ( (lv_right_3_0= ruleGamlUnitExpr ) ) )* )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2313:1: (this_GamlUnitExpr_0= ruleGamlUnitExpr ( ( () ( (lv_op_2_0= RULE_ID ) ) ) ( (lv_right_3_0= ruleGamlUnitExpr ) ) )* )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2314:5: this_GamlUnitExpr_0= ruleGamlUnitExpr ( ( () ( (lv_op_2_0= RULE_ID ) ) ) ( (lv_right_3_0= ruleGamlUnitExpr ) ) )*
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getGamlBinaryExprAccess().getGamlUnitExprParserRuleCall_0()); 
                  
            }
            pushFollow(FOLLOW_ruleGamlUnitExpr_in_ruleGamlBinaryExpr5109);
            this_GamlUnitExpr_0=ruleGamlUnitExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_GamlUnitExpr_0; 
                      afterParserOrEnumRuleCall();
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2322:1: ( ( () ( (lv_op_2_0= RULE_ID ) ) ) ( (lv_right_3_0= ruleGamlUnitExpr ) ) )*
            loop34:
            do {
                int alt34=2;
                int LA34_0 = input.LA(1);

                if ( (LA34_0==RULE_ID) ) {
                    int LA34_2 = input.LA(2);

                    if ( ((LA34_2>=RULE_ID && LA34_2<=RULE_BOOLEAN)||LA34_2==49||LA34_2==61||(LA34_2>=66 && LA34_2<=69)||LA34_2==71||LA34_2==73) ) {
                        alt34=1;
                    }


                }


                switch (alt34) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2322:2: ( () ( (lv_op_2_0= RULE_ID ) ) ) ( (lv_right_3_0= ruleGamlUnitExpr ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2322:2: ( () ( (lv_op_2_0= RULE_ID ) ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2322:3: () ( (lv_op_2_0= RULE_ID ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2322:3: ()
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2323:5: 
            	    {
            	    if ( state.backtracking==0 ) {

            	              current = forceCreateModelElementAndSet(
            	                  grammarAccess.getGamlBinaryExprAccess().getGamlBinaryExprLeftAction_1_0_0(),
            	                  current);
            	          
            	    }

            	    }

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2328:2: ( (lv_op_2_0= RULE_ID ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2329:1: (lv_op_2_0= RULE_ID )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2329:1: (lv_op_2_0= RULE_ID )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2330:3: lv_op_2_0= RULE_ID
            	    {
            	    lv_op_2_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleGamlBinaryExpr5136); if (state.failed) return current;
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

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2346:3: ( (lv_right_3_0= ruleGamlUnitExpr ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2347:1: (lv_right_3_0= ruleGamlUnitExpr )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2347:1: (lv_right_3_0= ruleGamlUnitExpr )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2348:3: lv_right_3_0= ruleGamlUnitExpr
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getGamlBinaryExprAccess().getRightGamlUnitExprParserRuleCall_1_1_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleGamlUnitExpr_in_ruleGamlBinaryExpr5163);
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
    // $ANTLR end "ruleGamlBinaryExpr"


    // $ANTLR start "entryRuleGamlUnitExpr"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2372:1: entryRuleGamlUnitExpr returns [EObject current=null] : iv_ruleGamlUnitExpr= ruleGamlUnitExpr EOF ;
    public final EObject entryRuleGamlUnitExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleGamlUnitExpr = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2373:2: (iv_ruleGamlUnitExpr= ruleGamlUnitExpr EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2374:2: iv_ruleGamlUnitExpr= ruleGamlUnitExpr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getGamlUnitExprRule()); 
            }
            pushFollow(FOLLOW_ruleGamlUnitExpr_in_entryRuleGamlUnitExpr5201);
            iv_ruleGamlUnitExpr=ruleGamlUnitExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleGamlUnitExpr; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleGamlUnitExpr5211); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2381:1: ruleGamlUnitExpr returns [EObject current=null] : (this_GamlUnaryExpr_0= ruleGamlUnaryExpr ( ( () ( (lv_op_2_0= '#' ) ) ) ( (lv_right_3_0= ruleUnitName ) ) )? ) ;
    public final EObject ruleGamlUnitExpr() throws RecognitionException {
        EObject current = null;

        Token lv_op_2_0=null;
        EObject this_GamlUnaryExpr_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2384:28: ( (this_GamlUnaryExpr_0= ruleGamlUnaryExpr ( ( () ( (lv_op_2_0= '#' ) ) ) ( (lv_right_3_0= ruleUnitName ) ) )? ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2385:1: (this_GamlUnaryExpr_0= ruleGamlUnaryExpr ( ( () ( (lv_op_2_0= '#' ) ) ) ( (lv_right_3_0= ruleUnitName ) ) )? )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2385:1: (this_GamlUnaryExpr_0= ruleGamlUnaryExpr ( ( () ( (lv_op_2_0= '#' ) ) ) ( (lv_right_3_0= ruleUnitName ) ) )? )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2386:5: this_GamlUnaryExpr_0= ruleGamlUnaryExpr ( ( () ( (lv_op_2_0= '#' ) ) ) ( (lv_right_3_0= ruleUnitName ) ) )?
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getGamlUnitExprAccess().getGamlUnaryExprParserRuleCall_0()); 
                  
            }
            pushFollow(FOLLOW_ruleGamlUnaryExpr_in_ruleGamlUnitExpr5258);
            this_GamlUnaryExpr_0=ruleGamlUnaryExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_GamlUnaryExpr_0; 
                      afterParserOrEnumRuleCall();
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2394:1: ( ( () ( (lv_op_2_0= '#' ) ) ) ( (lv_right_3_0= ruleUnitName ) ) )?
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==65) ) {
                alt35=1;
            }
            switch (alt35) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2394:2: ( () ( (lv_op_2_0= '#' ) ) ) ( (lv_right_3_0= ruleUnitName ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2394:2: ( () ( (lv_op_2_0= '#' ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2394:3: () ( (lv_op_2_0= '#' ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2394:3: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2395:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElementAndSet(
                                  grammarAccess.getGamlUnitExprAccess().getGamlUnitExprLeftAction_1_0_0(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2400:2: ( (lv_op_2_0= '#' ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2401:1: (lv_op_2_0= '#' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2401:1: (lv_op_2_0= '#' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2402:3: lv_op_2_0= '#'
                    {
                    lv_op_2_0=(Token)match(input,65,FOLLOW_65_in_ruleGamlUnitExpr5286); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_op_2_0, grammarAccess.getGamlUnitExprAccess().getOpNumberSignKeyword_1_0_1_0());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getGamlUnitExprRule());
                      	        }
                             		setWithLastConsumed(current, "op", lv_op_2_0, "#");
                      	    
                    }

                    }


                    }


                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2415:3: ( (lv_right_3_0= ruleUnitName ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2416:1: (lv_right_3_0= ruleUnitName )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2416:1: (lv_right_3_0= ruleUnitName )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2417:3: lv_right_3_0= ruleUnitName
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getGamlUnitExprAccess().getRightUnitNameParserRuleCall_1_1_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleUnitName_in_ruleGamlUnitExpr5321);
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2441:1: entryRuleGamlUnaryExpr returns [EObject current=null] : iv_ruleGamlUnaryExpr= ruleGamlUnaryExpr EOF ;
    public final EObject entryRuleGamlUnaryExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleGamlUnaryExpr = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2442:2: (iv_ruleGamlUnaryExpr= ruleGamlUnaryExpr EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2443:2: iv_ruleGamlUnaryExpr= ruleGamlUnaryExpr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getGamlUnaryExprRule()); 
            }
            pushFollow(FOLLOW_ruleGamlUnaryExpr_in_entryRuleGamlUnaryExpr5359);
            iv_ruleGamlUnaryExpr=ruleGamlUnaryExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleGamlUnaryExpr; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleGamlUnaryExpr5369); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2450:1: ruleGamlUnaryExpr returns [EObject current=null] : (this_PrePrimaryExpr_0= rulePrePrimaryExpr | ( () ( () ( ( (lv_op_3_1= '-' | lv_op_3_2= '!' | lv_op_3_3= 'my' | lv_op_3_4= 'the' | lv_op_3_5= 'not' ) ) ) ( (lv_right_4_0= ruleGamlUnaryExpr ) ) ) ) ) ;
    public final EObject ruleGamlUnaryExpr() throws RecognitionException {
        EObject current = null;

        Token lv_op_3_1=null;
        Token lv_op_3_2=null;
        Token lv_op_3_3=null;
        Token lv_op_3_4=null;
        Token lv_op_3_5=null;
        EObject this_PrePrimaryExpr_0 = null;

        EObject lv_right_4_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2453:28: ( (this_PrePrimaryExpr_0= rulePrePrimaryExpr | ( () ( () ( ( (lv_op_3_1= '-' | lv_op_3_2= '!' | lv_op_3_3= 'my' | lv_op_3_4= 'the' | lv_op_3_5= 'not' ) ) ) ( (lv_right_4_0= ruleGamlUnaryExpr ) ) ) ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2454:1: (this_PrePrimaryExpr_0= rulePrePrimaryExpr | ( () ( () ( ( (lv_op_3_1= '-' | lv_op_3_2= '!' | lv_op_3_3= 'my' | lv_op_3_4= 'the' | lv_op_3_5= 'not' ) ) ) ( (lv_right_4_0= ruleGamlUnaryExpr ) ) ) ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2454:1: (this_PrePrimaryExpr_0= rulePrePrimaryExpr | ( () ( () ( ( (lv_op_3_1= '-' | lv_op_3_2= '!' | lv_op_3_3= 'my' | lv_op_3_4= 'the' | lv_op_3_5= 'not' ) ) ) ( (lv_right_4_0= ruleGamlUnaryExpr ) ) ) ) )
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( ((LA37_0>=RULE_ID && LA37_0<=RULE_BOOLEAN)||LA37_0==49||LA37_0==71||LA37_0==73) ) {
                alt37=1;
            }
            else if ( (LA37_0==61||(LA37_0>=66 && LA37_0<=69)) ) {
                alt37=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 37, 0, input);

                throw nvae;
            }
            switch (alt37) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2455:5: this_PrePrimaryExpr_0= rulePrePrimaryExpr
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getGamlUnaryExprAccess().getPrePrimaryExprParserRuleCall_0()); 
                          
                    }
                    pushFollow(FOLLOW_rulePrePrimaryExpr_in_ruleGamlUnaryExpr5416);
                    this_PrePrimaryExpr_0=rulePrePrimaryExpr();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_PrePrimaryExpr_0; 
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 2 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2464:6: ( () ( () ( ( (lv_op_3_1= '-' | lv_op_3_2= '!' | lv_op_3_3= 'my' | lv_op_3_4= 'the' | lv_op_3_5= 'not' ) ) ) ( (lv_right_4_0= ruleGamlUnaryExpr ) ) ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2464:6: ( () ( () ( ( (lv_op_3_1= '-' | lv_op_3_2= '!' | lv_op_3_3= 'my' | lv_op_3_4= 'the' | lv_op_3_5= 'not' ) ) ) ( (lv_right_4_0= ruleGamlUnaryExpr ) ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2464:7: () ( () ( ( (lv_op_3_1= '-' | lv_op_3_2= '!' | lv_op_3_3= 'my' | lv_op_3_4= 'the' | lv_op_3_5= 'not' ) ) ) ( (lv_right_4_0= ruleGamlUnaryExpr ) ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2464:7: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2465:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getGamlUnaryExprAccess().getGamlUnaryExprAction_1_0(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2470:2: ( () ( ( (lv_op_3_1= '-' | lv_op_3_2= '!' | lv_op_3_3= 'my' | lv_op_3_4= 'the' | lv_op_3_5= 'not' ) ) ) ( (lv_right_4_0= ruleGamlUnaryExpr ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2470:3: () ( ( (lv_op_3_1= '-' | lv_op_3_2= '!' | lv_op_3_3= 'my' | lv_op_3_4= 'the' | lv_op_3_5= 'not' ) ) ) ( (lv_right_4_0= ruleGamlUnaryExpr ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2470:3: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2471:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElementAndSet(
                                  grammarAccess.getGamlUnaryExprAccess().getGamlUnaryExprLeftAction_1_1_0(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2476:2: ( ( (lv_op_3_1= '-' | lv_op_3_2= '!' | lv_op_3_3= 'my' | lv_op_3_4= 'the' | lv_op_3_5= 'not' ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2477:1: ( (lv_op_3_1= '-' | lv_op_3_2= '!' | lv_op_3_3= 'my' | lv_op_3_4= 'the' | lv_op_3_5= 'not' ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2477:1: ( (lv_op_3_1= '-' | lv_op_3_2= '!' | lv_op_3_3= 'my' | lv_op_3_4= 'the' | lv_op_3_5= 'not' ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2478:1: (lv_op_3_1= '-' | lv_op_3_2= '!' | lv_op_3_3= 'my' | lv_op_3_4= 'the' | lv_op_3_5= 'not' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2478:1: (lv_op_3_1= '-' | lv_op_3_2= '!' | lv_op_3_3= 'my' | lv_op_3_4= 'the' | lv_op_3_5= 'not' )
                    int alt36=5;
                    switch ( input.LA(1) ) {
                    case 61:
                        {
                        alt36=1;
                        }
                        break;
                    case 66:
                        {
                        alt36=2;
                        }
                        break;
                    case 67:
                        {
                        alt36=3;
                        }
                        break;
                    case 68:
                        {
                        alt36=4;
                        }
                        break;
                    case 69:
                        {
                        alt36=5;
                        }
                        break;
                    default:
                        if (state.backtracking>0) {state.failed=true; return current;}
                        NoViableAltException nvae =
                            new NoViableAltException("", 36, 0, input);

                        throw nvae;
                    }

                    switch (alt36) {
                        case 1 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2479:3: lv_op_3_1= '-'
                            {
                            lv_op_3_1=(Token)match(input,61,FOLLOW_61_in_ruleGamlUnaryExpr5461); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                                      newLeafNode(lv_op_3_1, grammarAccess.getGamlUnaryExprAccess().getOpHyphenMinusKeyword_1_1_1_0_0());
                                  
                            }
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElement(grammarAccess.getGamlUnaryExprRule());
                              	        }
                                     		setWithLastConsumed(current, "op", lv_op_3_1, null);
                              	    
                            }

                            }
                            break;
                        case 2 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2491:8: lv_op_3_2= '!'
                            {
                            lv_op_3_2=(Token)match(input,66,FOLLOW_66_in_ruleGamlUnaryExpr5490); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                                      newLeafNode(lv_op_3_2, grammarAccess.getGamlUnaryExprAccess().getOpExclamationMarkKeyword_1_1_1_0_1());
                                  
                            }
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElement(grammarAccess.getGamlUnaryExprRule());
                              	        }
                                     		setWithLastConsumed(current, "op", lv_op_3_2, null);
                              	    
                            }

                            }
                            break;
                        case 3 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2503:8: lv_op_3_3= 'my'
                            {
                            lv_op_3_3=(Token)match(input,67,FOLLOW_67_in_ruleGamlUnaryExpr5519); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                                      newLeafNode(lv_op_3_3, grammarAccess.getGamlUnaryExprAccess().getOpMyKeyword_1_1_1_0_2());
                                  
                            }
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElement(grammarAccess.getGamlUnaryExprRule());
                              	        }
                                     		setWithLastConsumed(current, "op", lv_op_3_3, null);
                              	    
                            }

                            }
                            break;
                        case 4 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2515:8: lv_op_3_4= 'the'
                            {
                            lv_op_3_4=(Token)match(input,68,FOLLOW_68_in_ruleGamlUnaryExpr5548); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                                      newLeafNode(lv_op_3_4, grammarAccess.getGamlUnaryExprAccess().getOpTheKeyword_1_1_1_0_3());
                                  
                            }
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElement(grammarAccess.getGamlUnaryExprRule());
                              	        }
                                     		setWithLastConsumed(current, "op", lv_op_3_4, null);
                              	    
                            }

                            }
                            break;
                        case 5 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2527:8: lv_op_3_5= 'not'
                            {
                            lv_op_3_5=(Token)match(input,69,FOLLOW_69_in_ruleGamlUnaryExpr5577); if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                                      newLeafNode(lv_op_3_5, grammarAccess.getGamlUnaryExprAccess().getOpNotKeyword_1_1_1_0_4());
                                  
                            }
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElement(grammarAccess.getGamlUnaryExprRule());
                              	        }
                                     		setWithLastConsumed(current, "op", lv_op_3_5, null);
                              	    
                            }

                            }
                            break;

                    }


                    }


                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2542:2: ( (lv_right_4_0= ruleGamlUnaryExpr ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2543:1: (lv_right_4_0= ruleGamlUnaryExpr )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2543:1: (lv_right_4_0= ruleGamlUnaryExpr )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2544:3: lv_right_4_0= ruleGamlUnaryExpr
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getGamlUnaryExprAccess().getRightGamlUnaryExprParserRuleCall_1_1_2_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleGamlUnaryExpr_in_ruleGamlUnaryExpr5614);
                    lv_right_4_0=ruleGamlUnaryExpr();

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
                              		"GamlUnaryExpr");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

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
    // $ANTLR end "ruleGamlUnaryExpr"


    // $ANTLR start "entryRulePrePrimaryExpr"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2568:1: entryRulePrePrimaryExpr returns [EObject current=null] : iv_rulePrePrimaryExpr= rulePrePrimaryExpr EOF ;
    public final EObject entryRulePrePrimaryExpr() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePrePrimaryExpr = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2569:2: (iv_rulePrePrimaryExpr= rulePrePrimaryExpr EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2570:2: iv_rulePrePrimaryExpr= rulePrePrimaryExpr EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getPrePrimaryExprRule()); 
            }
            pushFollow(FOLLOW_rulePrePrimaryExpr_in_entryRulePrePrimaryExpr5652);
            iv_rulePrePrimaryExpr=rulePrePrimaryExpr();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_rulePrePrimaryExpr; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRulePrePrimaryExpr5662); if (state.failed) return current;

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
    // $ANTLR end "entryRulePrePrimaryExpr"


    // $ANTLR start "rulePrePrimaryExpr"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2577:1: rulePrePrimaryExpr returns [EObject current=null] : (this_TerminalExpression_0= ruleTerminalExpression | this_MemberRef_1= ruleMemberRef ) ;
    public final EObject rulePrePrimaryExpr() throws RecognitionException {
        EObject current = null;

        EObject this_TerminalExpression_0 = null;

        EObject this_MemberRef_1 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2580:28: ( (this_TerminalExpression_0= ruleTerminalExpression | this_MemberRef_1= ruleMemberRef ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2581:1: (this_TerminalExpression_0= ruleTerminalExpression | this_MemberRef_1= ruleMemberRef )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2581:1: (this_TerminalExpression_0= ruleTerminalExpression | this_MemberRef_1= ruleMemberRef )
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( ((LA38_0>=RULE_STRING && LA38_0<=RULE_BOOLEAN)) ) {
                alt38=1;
            }
            else if ( (LA38_0==RULE_ID||LA38_0==49||LA38_0==71||LA38_0==73) ) {
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2582:5: this_TerminalExpression_0= ruleTerminalExpression
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getPrePrimaryExprAccess().getTerminalExpressionParserRuleCall_0()); 
                          
                    }
                    pushFollow(FOLLOW_ruleTerminalExpression_in_rulePrePrimaryExpr5709);
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2592:5: this_MemberRef_1= ruleMemberRef
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getPrePrimaryExprAccess().getMemberRefParserRuleCall_1()); 
                          
                    }
                    pushFollow(FOLLOW_ruleMemberRef_in_rulePrePrimaryExpr5736);
                    this_MemberRef_1=ruleMemberRef();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_MemberRef_1; 
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
    // $ANTLR end "rulePrePrimaryExpr"


    // $ANTLR start "entryRuleMemberRef"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2608:1: entryRuleMemberRef returns [EObject current=null] : iv_ruleMemberRef= ruleMemberRef EOF ;
    public final EObject entryRuleMemberRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleMemberRef = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2609:2: (iv_ruleMemberRef= ruleMemberRef EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2610:2: iv_ruleMemberRef= ruleMemberRef EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getMemberRefRule()); 
            }
            pushFollow(FOLLOW_ruleMemberRef_in_entryRuleMemberRef5771);
            iv_ruleMemberRef=ruleMemberRef();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleMemberRef; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleMemberRef5781); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2617:1: ruleMemberRef returns [EObject current=null] : (this_PrimaryExpression_0= rulePrimaryExpression ( () ( (lv_op_2_0= '.' ) ) ( (lv_right_3_0= ruleVariableRef ) ) )* ) ;
    public final EObject ruleMemberRef() throws RecognitionException {
        EObject current = null;

        Token lv_op_2_0=null;
        EObject this_PrimaryExpression_0 = null;

        EObject lv_right_3_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2620:28: ( (this_PrimaryExpression_0= rulePrimaryExpression ( () ( (lv_op_2_0= '.' ) ) ( (lv_right_3_0= ruleVariableRef ) ) )* ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2621:1: (this_PrimaryExpression_0= rulePrimaryExpression ( () ( (lv_op_2_0= '.' ) ) ( (lv_right_3_0= ruleVariableRef ) ) )* )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2621:1: (this_PrimaryExpression_0= rulePrimaryExpression ( () ( (lv_op_2_0= '.' ) ) ( (lv_right_3_0= ruleVariableRef ) ) )* )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2622:5: this_PrimaryExpression_0= rulePrimaryExpression ( () ( (lv_op_2_0= '.' ) ) ( (lv_right_3_0= ruleVariableRef ) ) )*
            {
            if ( state.backtracking==0 ) {
               
                      newCompositeNode(grammarAccess.getMemberRefAccess().getPrimaryExpressionParserRuleCall_0()); 
                  
            }
            pushFollow(FOLLOW_rulePrimaryExpression_in_ruleMemberRef5828);
            this_PrimaryExpression_0=rulePrimaryExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               
                      current = this_PrimaryExpression_0; 
                      afterParserOrEnumRuleCall();
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2630:1: ( () ( (lv_op_2_0= '.' ) ) ( (lv_right_3_0= ruleVariableRef ) ) )*
            loop39:
            do {
                int alt39=2;
                int LA39_0 = input.LA(1);

                if ( (LA39_0==70) ) {
                    alt39=1;
                }


                switch (alt39) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2630:2: () ( (lv_op_2_0= '.' ) ) ( (lv_right_3_0= ruleVariableRef ) )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2630:2: ()
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2631:5: 
            	    {
            	    if ( state.backtracking==0 ) {

            	              current = forceCreateModelElementAndSet(
            	                  grammarAccess.getMemberRefAccess().getMemberRefLeftAction_1_0(),
            	                  current);
            	          
            	    }

            	    }

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2636:2: ( (lv_op_2_0= '.' ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2637:1: (lv_op_2_0= '.' )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2637:1: (lv_op_2_0= '.' )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2638:3: lv_op_2_0= '.'
            	    {
            	    lv_op_2_0=(Token)match(input,70,FOLLOW_70_in_ruleMemberRef5855); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	              newLeafNode(lv_op_2_0, grammarAccess.getMemberRefAccess().getOpFullStopKeyword_1_1_0());
            	          
            	    }
            	    if ( state.backtracking==0 ) {

            	      	        if (current==null) {
            	      	            current = createModelElement(grammarAccess.getMemberRefRule());
            	      	        }
            	             		setWithLastConsumed(current, "op", lv_op_2_0, ".");
            	      	    
            	    }

            	    }


            	    }

            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2651:2: ( (lv_right_3_0= ruleVariableRef ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2652:1: (lv_right_3_0= ruleVariableRef )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2652:1: (lv_right_3_0= ruleVariableRef )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2653:3: lv_right_3_0= ruleVariableRef
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getMemberRefAccess().getRightVariableRefParserRuleCall_1_2_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleVariableRef_in_ruleMemberRef5889);
            	    lv_right_3_0=ruleVariableRef();

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
            	              		"VariableRef");
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
    // $ANTLR end "ruleMemberRef"


    // $ANTLR start "entryRulePrimaryExpression"
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2677:1: entryRulePrimaryExpression returns [EObject current=null] : iv_rulePrimaryExpression= rulePrimaryExpression EOF ;
    public final EObject entryRulePrimaryExpression() throws RecognitionException {
        EObject current = null;

        EObject iv_rulePrimaryExpression = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2678:2: (iv_rulePrimaryExpression= rulePrimaryExpression EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2679:2: iv_rulePrimaryExpression= rulePrimaryExpression EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getPrimaryExpressionRule()); 
            }
            pushFollow(FOLLOW_rulePrimaryExpression_in_entryRulePrimaryExpression5927);
            iv_rulePrimaryExpression=rulePrimaryExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_rulePrimaryExpression; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRulePrimaryExpression5937); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2686:1: rulePrimaryExpression returns [EObject current=null] : (this_AbstractRef_0= ruleAbstractRef | (otherlv_1= '(' this_Expression_2= ruleExpression otherlv_3= ')' ) | (otherlv_4= '[' () ( ( (lv_exprs_6_0= ruleExpression ) ) (otherlv_7= ',' ( (lv_exprs_8_0= ruleExpression ) ) )* )? otherlv_9= ']' ) | (otherlv_10= '{' () ( ( (lv_left_12_0= ruleExpression ) ) ( (lv_op_13_0= ',' ) ) ( (lv_right_14_0= ruleExpression ) ) ) otherlv_15= '}' ) ) ;
    public final EObject rulePrimaryExpression() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;
        Token otherlv_3=null;
        Token otherlv_4=null;
        Token otherlv_7=null;
        Token otherlv_9=null;
        Token otherlv_10=null;
        Token lv_op_13_0=null;
        Token otherlv_15=null;
        EObject this_AbstractRef_0 = null;

        EObject this_Expression_2 = null;

        EObject lv_exprs_6_0 = null;

        EObject lv_exprs_8_0 = null;

        EObject lv_left_12_0 = null;

        EObject lv_right_14_0 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2689:28: ( (this_AbstractRef_0= ruleAbstractRef | (otherlv_1= '(' this_Expression_2= ruleExpression otherlv_3= ')' ) | (otherlv_4= '[' () ( ( (lv_exprs_6_0= ruleExpression ) ) (otherlv_7= ',' ( (lv_exprs_8_0= ruleExpression ) ) )* )? otherlv_9= ']' ) | (otherlv_10= '{' () ( ( (lv_left_12_0= ruleExpression ) ) ( (lv_op_13_0= ',' ) ) ( (lv_right_14_0= ruleExpression ) ) ) otherlv_15= '}' ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2690:1: (this_AbstractRef_0= ruleAbstractRef | (otherlv_1= '(' this_Expression_2= ruleExpression otherlv_3= ')' ) | (otherlv_4= '[' () ( ( (lv_exprs_6_0= ruleExpression ) ) (otherlv_7= ',' ( (lv_exprs_8_0= ruleExpression ) ) )* )? otherlv_9= ']' ) | (otherlv_10= '{' () ( ( (lv_left_12_0= ruleExpression ) ) ( (lv_op_13_0= ',' ) ) ( (lv_right_14_0= ruleExpression ) ) ) otherlv_15= '}' ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2690:1: (this_AbstractRef_0= ruleAbstractRef | (otherlv_1= '(' this_Expression_2= ruleExpression otherlv_3= ')' ) | (otherlv_4= '[' () ( ( (lv_exprs_6_0= ruleExpression ) ) (otherlv_7= ',' ( (lv_exprs_8_0= ruleExpression ) ) )* )? otherlv_9= ']' ) | (otherlv_10= '{' () ( ( (lv_left_12_0= ruleExpression ) ) ( (lv_op_13_0= ',' ) ) ( (lv_right_14_0= ruleExpression ) ) ) otherlv_15= '}' ) )
            int alt42=4;
            switch ( input.LA(1) ) {
            case RULE_ID:
                {
                alt42=1;
                }
                break;
            case 71:
                {
                alt42=2;
                }
                break;
            case 73:
                {
                alt42=3;
                }
                break;
            case 49:
                {
                alt42=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 42, 0, input);

                throw nvae;
            }

            switch (alt42) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2691:5: this_AbstractRef_0= ruleAbstractRef
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getPrimaryExpressionAccess().getAbstractRefParserRuleCall_0()); 
                          
                    }
                    pushFollow(FOLLOW_ruleAbstractRef_in_rulePrimaryExpression5984);
                    this_AbstractRef_0=ruleAbstractRef();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_AbstractRef_0; 
                              afterParserOrEnumRuleCall();
                          
                    }

                    }
                    break;
                case 2 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2700:6: (otherlv_1= '(' this_Expression_2= ruleExpression otherlv_3= ')' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2700:6: (otherlv_1= '(' this_Expression_2= ruleExpression otherlv_3= ')' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2700:8: otherlv_1= '(' this_Expression_2= ruleExpression otherlv_3= ')'
                    {
                    otherlv_1=(Token)match(input,71,FOLLOW_71_in_rulePrimaryExpression6002); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_1, grammarAccess.getPrimaryExpressionAccess().getLeftParenthesisKeyword_1_0());
                          
                    }
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getPrimaryExpressionAccess().getExpressionParserRuleCall_1_1()); 
                          
                    }
                    pushFollow(FOLLOW_ruleExpression_in_rulePrimaryExpression6024);
                    this_Expression_2=ruleExpression();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {
                       
                              current = this_Expression_2; 
                              afterParserOrEnumRuleCall();
                          
                    }
                    otherlv_3=(Token)match(input,72,FOLLOW_72_in_rulePrimaryExpression6035); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_3, grammarAccess.getPrimaryExpressionAccess().getRightParenthesisKeyword_1_2());
                          
                    }

                    }


                    }
                    break;
                case 3 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2718:6: (otherlv_4= '[' () ( ( (lv_exprs_6_0= ruleExpression ) ) (otherlv_7= ',' ( (lv_exprs_8_0= ruleExpression ) ) )* )? otherlv_9= ']' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2718:6: (otherlv_4= '[' () ( ( (lv_exprs_6_0= ruleExpression ) ) (otherlv_7= ',' ( (lv_exprs_8_0= ruleExpression ) ) )* )? otherlv_9= ']' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2718:8: otherlv_4= '[' () ( ( (lv_exprs_6_0= ruleExpression ) ) (otherlv_7= ',' ( (lv_exprs_8_0= ruleExpression ) ) )* )? otherlv_9= ']'
                    {
                    otherlv_4=(Token)match(input,73,FOLLOW_73_in_rulePrimaryExpression6055); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_4, grammarAccess.getPrimaryExpressionAccess().getLeftSquareBracketKeyword_2_0());
                          
                    }
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2722:1: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2723:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getPrimaryExpressionAccess().getArrayAction_2_1(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2728:2: ( ( (lv_exprs_6_0= ruleExpression ) ) (otherlv_7= ',' ( (lv_exprs_8_0= ruleExpression ) ) )* )?
                    int alt41=2;
                    int LA41_0 = input.LA(1);

                    if ( ((LA41_0>=RULE_ID && LA41_0<=RULE_BOOLEAN)||LA41_0==49||LA41_0==61||(LA41_0>=66 && LA41_0<=69)||LA41_0==71||LA41_0==73) ) {
                        alt41=1;
                    }
                    switch (alt41) {
                        case 1 :
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2728:3: ( (lv_exprs_6_0= ruleExpression ) ) (otherlv_7= ',' ( (lv_exprs_8_0= ruleExpression ) ) )*
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2728:3: ( (lv_exprs_6_0= ruleExpression ) )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2729:1: (lv_exprs_6_0= ruleExpression )
                            {
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2729:1: (lv_exprs_6_0= ruleExpression )
                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2730:3: lv_exprs_6_0= ruleExpression
                            {
                            if ( state.backtracking==0 ) {
                               
                              	        newCompositeNode(grammarAccess.getPrimaryExpressionAccess().getExprsExpressionParserRuleCall_2_2_0_0()); 
                              	    
                            }
                            pushFollow(FOLLOW_ruleExpression_in_rulePrimaryExpression6086);
                            lv_exprs_6_0=ruleExpression();

                            state._fsp--;
                            if (state.failed) return current;
                            if ( state.backtracking==0 ) {

                              	        if (current==null) {
                              	            current = createModelElementForParent(grammarAccess.getPrimaryExpressionRule());
                              	        }
                                     		add(
                                     			current, 
                                     			"exprs",
                                      		lv_exprs_6_0, 
                                      		"Expression");
                              	        afterParserOrEnumRuleCall();
                              	    
                            }

                            }


                            }

                            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2746:2: (otherlv_7= ',' ( (lv_exprs_8_0= ruleExpression ) ) )*
                            loop40:
                            do {
                                int alt40=2;
                                int LA40_0 = input.LA(1);

                                if ( (LA40_0==74) ) {
                                    alt40=1;
                                }


                                switch (alt40) {
                            	case 1 :
                            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2746:4: otherlv_7= ',' ( (lv_exprs_8_0= ruleExpression ) )
                            	    {
                            	    otherlv_7=(Token)match(input,74,FOLLOW_74_in_rulePrimaryExpression6099); if (state.failed) return current;
                            	    if ( state.backtracking==0 ) {

                            	          	newLeafNode(otherlv_7, grammarAccess.getPrimaryExpressionAccess().getCommaKeyword_2_2_1_0());
                            	          
                            	    }
                            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2750:1: ( (lv_exprs_8_0= ruleExpression ) )
                            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2751:1: (lv_exprs_8_0= ruleExpression )
                            	    {
                            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2751:1: (lv_exprs_8_0= ruleExpression )
                            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2752:3: lv_exprs_8_0= ruleExpression
                            	    {
                            	    if ( state.backtracking==0 ) {
                            	       
                            	      	        newCompositeNode(grammarAccess.getPrimaryExpressionAccess().getExprsExpressionParserRuleCall_2_2_1_1_0()); 
                            	      	    
                            	    }
                            	    pushFollow(FOLLOW_ruleExpression_in_rulePrimaryExpression6120);
                            	    lv_exprs_8_0=ruleExpression();

                            	    state._fsp--;
                            	    if (state.failed) return current;
                            	    if ( state.backtracking==0 ) {

                            	      	        if (current==null) {
                            	      	            current = createModelElementForParent(grammarAccess.getPrimaryExpressionRule());
                            	      	        }
                            	             		add(
                            	             			current, 
                            	             			"exprs",
                            	              		lv_exprs_8_0, 
                            	              		"Expression");
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
                            break;

                    }

                    otherlv_9=(Token)match(input,75,FOLLOW_75_in_rulePrimaryExpression6136); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_9, grammarAccess.getPrimaryExpressionAccess().getRightSquareBracketKeyword_2_3());
                          
                    }

                    }


                    }
                    break;
                case 4 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2773:6: (otherlv_10= '{' () ( ( (lv_left_12_0= ruleExpression ) ) ( (lv_op_13_0= ',' ) ) ( (lv_right_14_0= ruleExpression ) ) ) otherlv_15= '}' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2773:6: (otherlv_10= '{' () ( ( (lv_left_12_0= ruleExpression ) ) ( (lv_op_13_0= ',' ) ) ( (lv_right_14_0= ruleExpression ) ) ) otherlv_15= '}' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2773:8: otherlv_10= '{' () ( ( (lv_left_12_0= ruleExpression ) ) ( (lv_op_13_0= ',' ) ) ( (lv_right_14_0= ruleExpression ) ) ) otherlv_15= '}'
                    {
                    otherlv_10=(Token)match(input,49,FOLLOW_49_in_rulePrimaryExpression6156); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_10, grammarAccess.getPrimaryExpressionAccess().getLeftCurlyBracketKeyword_3_0());
                          
                    }
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2777:1: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2778:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getPrimaryExpressionAccess().getPointAction_3_1(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2783:2: ( ( (lv_left_12_0= ruleExpression ) ) ( (lv_op_13_0= ',' ) ) ( (lv_right_14_0= ruleExpression ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2783:3: ( (lv_left_12_0= ruleExpression ) ) ( (lv_op_13_0= ',' ) ) ( (lv_right_14_0= ruleExpression ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2783:3: ( (lv_left_12_0= ruleExpression ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2784:1: (lv_left_12_0= ruleExpression )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2784:1: (lv_left_12_0= ruleExpression )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2785:3: lv_left_12_0= ruleExpression
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getPrimaryExpressionAccess().getLeftExpressionParserRuleCall_3_2_0_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleExpression_in_rulePrimaryExpression6187);
                    lv_left_12_0=ruleExpression();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getPrimaryExpressionRule());
                      	        }
                             		set(
                             			current, 
                             			"left",
                              		lv_left_12_0, 
                              		"Expression");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2801:2: ( (lv_op_13_0= ',' ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2802:1: (lv_op_13_0= ',' )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2802:1: (lv_op_13_0= ',' )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2803:3: lv_op_13_0= ','
                    {
                    lv_op_13_0=(Token)match(input,74,FOLLOW_74_in_rulePrimaryExpression6205); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                              newLeafNode(lv_op_13_0, grammarAccess.getPrimaryExpressionAccess().getOpCommaKeyword_3_2_1_0());
                          
                    }
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElement(grammarAccess.getPrimaryExpressionRule());
                      	        }
                             		setWithLastConsumed(current, "op", lv_op_13_0, ",");
                      	    
                    }

                    }


                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2816:2: ( (lv_right_14_0= ruleExpression ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2817:1: (lv_right_14_0= ruleExpression )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2817:1: (lv_right_14_0= ruleExpression )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2818:3: lv_right_14_0= ruleExpression
                    {
                    if ( state.backtracking==0 ) {
                       
                      	        newCompositeNode(grammarAccess.getPrimaryExpressionAccess().getRightExpressionParserRuleCall_3_2_2_0()); 
                      	    
                    }
                    pushFollow(FOLLOW_ruleExpression_in_rulePrimaryExpression6239);
                    lv_right_14_0=ruleExpression();

                    state._fsp--;
                    if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                      	        if (current==null) {
                      	            current = createModelElementForParent(grammarAccess.getPrimaryExpressionRule());
                      	        }
                             		set(
                             			current, 
                             			"right",
                              		lv_right_14_0, 
                              		"Expression");
                      	        afterParserOrEnumRuleCall();
                      	    
                    }

                    }


                    }


                    }

                    otherlv_15=(Token)match(input,16,FOLLOW_16_in_rulePrimaryExpression6252); if (state.failed) return current;
                    if ( state.backtracking==0 ) {

                          	newLeafNode(otherlv_15, grammarAccess.getPrimaryExpressionAccess().getRightCurlyBracketKeyword_3_3());
                          
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2846:1: entryRuleAbstractRef returns [EObject current=null] : iv_ruleAbstractRef= ruleAbstractRef EOF ;
    public final EObject entryRuleAbstractRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleAbstractRef = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2847:2: (iv_ruleAbstractRef= ruleAbstractRef EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2848:2: iv_ruleAbstractRef= ruleAbstractRef EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getAbstractRefRule()); 
            }
            pushFollow(FOLLOW_ruleAbstractRef_in_entryRuleAbstractRef6289);
            iv_ruleAbstractRef=ruleAbstractRef();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleAbstractRef; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleAbstractRef6299); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2855:1: ruleAbstractRef returns [EObject current=null] : (this_VariableRef_0= ruleVariableRef | this_Function_1= ruleFunction ) ;
    public final EObject ruleAbstractRef() throws RecognitionException {
        EObject current = null;

        EObject this_VariableRef_0 = null;

        EObject this_Function_1 = null;


         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2858:28: ( (this_VariableRef_0= ruleVariableRef | this_Function_1= ruleFunction ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2859:1: (this_VariableRef_0= ruleVariableRef | this_Function_1= ruleFunction )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2859:1: (this_VariableRef_0= ruleVariableRef | this_Function_1= ruleFunction )
            int alt43=2;
            int LA43_0 = input.LA(1);

            if ( (LA43_0==RULE_ID) ) {
                int LA43_1 = input.LA(2);

                if ( (LA43_1==EOF||LA43_1==RULE_ID||LA43_1==16||LA43_1==39||(LA43_1>=42 && LA43_1<=65)||LA43_1==70||LA43_1==72||(LA43_1>=74 && LA43_1<=75)) ) {
                    alt43=1;
                }
                else if ( (LA43_1==71) ) {
                    alt43=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return current;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 43, 1, input);

                    throw nvae;
                }
            }
            else {
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 43, 0, input);

                throw nvae;
            }
            switch (alt43) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2860:5: this_VariableRef_0= ruleVariableRef
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getAbstractRefAccess().getVariableRefParserRuleCall_0()); 
                          
                    }
                    pushFollow(FOLLOW_ruleVariableRef_in_ruleAbstractRef6346);
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2870:5: this_Function_1= ruleFunction
                    {
                    if ( state.backtracking==0 ) {
                       
                              newCompositeNode(grammarAccess.getAbstractRefAccess().getFunctionParserRuleCall_1()); 
                          
                    }
                    pushFollow(FOLLOW_ruleFunction_in_ruleAbstractRef6373);
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2886:1: entryRuleFunction returns [EObject current=null] : iv_ruleFunction= ruleFunction EOF ;
    public final EObject entryRuleFunction() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleFunction = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2887:2: (iv_ruleFunction= ruleFunction EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2888:2: iv_ruleFunction= ruleFunction EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getFunctionRule()); 
            }
            pushFollow(FOLLOW_ruleFunction_in_entryRuleFunction6408);
            iv_ruleFunction=ruleFunction();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleFunction; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleFunction6418); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2895:1: ruleFunction returns [EObject current=null] : ( () ( ( (lv_op_1_0= RULE_ID ) ) otherlv_2= '(' ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ')' ) ) ;
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
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2898:28: ( ( () ( ( (lv_op_1_0= RULE_ID ) ) otherlv_2= '(' ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ')' ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2899:1: ( () ( ( (lv_op_1_0= RULE_ID ) ) otherlv_2= '(' ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ')' ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2899:1: ( () ( ( (lv_op_1_0= RULE_ID ) ) otherlv_2= '(' ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ')' ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2899:2: () ( ( (lv_op_1_0= RULE_ID ) ) otherlv_2= '(' ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ')' )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2899:2: ()
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2900:5: 
            {
            if ( state.backtracking==0 ) {

                      current = forceCreateModelElement(
                          grammarAccess.getFunctionAccess().getFunctionAction_0(),
                          current);
                  
            }

            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2905:2: ( ( (lv_op_1_0= RULE_ID ) ) otherlv_2= '(' ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ')' )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2905:3: ( (lv_op_1_0= RULE_ID ) ) otherlv_2= '(' ( (lv_args_3_0= ruleExpression ) ) (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )* otherlv_6= ')'
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2905:3: ( (lv_op_1_0= RULE_ID ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2906:1: (lv_op_1_0= RULE_ID )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2906:1: (lv_op_1_0= RULE_ID )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2907:3: lv_op_1_0= RULE_ID
            {
            lv_op_1_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleFunction6470); if (state.failed) return current;
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

            otherlv_2=(Token)match(input,71,FOLLOW_71_in_ruleFunction6487); if (state.failed) return current;
            if ( state.backtracking==0 ) {

                  	newLeafNode(otherlv_2, grammarAccess.getFunctionAccess().getLeftParenthesisKeyword_1_1());
                  
            }
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2927:1: ( (lv_args_3_0= ruleExpression ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2928:1: (lv_args_3_0= ruleExpression )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2928:1: (lv_args_3_0= ruleExpression )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2929:3: lv_args_3_0= ruleExpression
            {
            if ( state.backtracking==0 ) {
               
              	        newCompositeNode(grammarAccess.getFunctionAccess().getArgsExpressionParserRuleCall_1_2_0()); 
              	    
            }
            pushFollow(FOLLOW_ruleExpression_in_ruleFunction6508);
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

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2945:2: (otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) ) )*
            loop44:
            do {
                int alt44=2;
                int LA44_0 = input.LA(1);

                if ( (LA44_0==74) ) {
                    alt44=1;
                }


                switch (alt44) {
            	case 1 :
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2945:4: otherlv_4= ',' ( (lv_args_5_0= ruleExpression ) )
            	    {
            	    otherlv_4=(Token)match(input,74,FOLLOW_74_in_ruleFunction6521); if (state.failed) return current;
            	    if ( state.backtracking==0 ) {

            	          	newLeafNode(otherlv_4, grammarAccess.getFunctionAccess().getCommaKeyword_1_3_0());
            	          
            	    }
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2949:1: ( (lv_args_5_0= ruleExpression ) )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2950:1: (lv_args_5_0= ruleExpression )
            	    {
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2950:1: (lv_args_5_0= ruleExpression )
            	    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2951:3: lv_args_5_0= ruleExpression
            	    {
            	    if ( state.backtracking==0 ) {
            	       
            	      	        newCompositeNode(grammarAccess.getFunctionAccess().getArgsExpressionParserRuleCall_1_3_1_0()); 
            	      	    
            	    }
            	    pushFollow(FOLLOW_ruleExpression_in_ruleFunction6542);
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
            	    break loop44;
                }
            } while (true);

            otherlv_6=(Token)match(input,72,FOLLOW_72_in_ruleFunction6556); if (state.failed) return current;
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2979:1: entryRuleUnitName returns [EObject current=null] : iv_ruleUnitName= ruleUnitName EOF ;
    public final EObject entryRuleUnitName() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleUnitName = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2980:2: (iv_ruleUnitName= ruleUnitName EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2981:2: iv_ruleUnitName= ruleUnitName EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getUnitNameRule()); 
            }
            pushFollow(FOLLOW_ruleUnitName_in_entryRuleUnitName6593);
            iv_ruleUnitName=ruleUnitName();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleUnitName; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleUnitName6603); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2988:1: ruleUnitName returns [EObject current=null] : ( () ( (lv_op_1_0= RULE_ID ) ) ) ;
    public final EObject ruleUnitName() throws RecognitionException {
        EObject current = null;

        Token lv_op_1_0=null;

         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2991:28: ( ( () ( (lv_op_1_0= RULE_ID ) ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2992:1: ( () ( (lv_op_1_0= RULE_ID ) ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2992:1: ( () ( (lv_op_1_0= RULE_ID ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2992:2: () ( (lv_op_1_0= RULE_ID ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2992:2: ()
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2993:5: 
            {
            if ( state.backtracking==0 ) {

                      current = forceCreateModelElement(
                          grammarAccess.getUnitNameAccess().getUnitNameAction_0(),
                          current);
                  
            }

            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2998:2: ( (lv_op_1_0= RULE_ID ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2999:1: (lv_op_1_0= RULE_ID )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2999:1: (lv_op_1_0= RULE_ID )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3000:3: lv_op_1_0= RULE_ID
            {
            lv_op_1_0=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleUnitName6654); if (state.failed) return current;
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3024:1: entryRuleVariableRef returns [EObject current=null] : iv_ruleVariableRef= ruleVariableRef EOF ;
    public final EObject entryRuleVariableRef() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleVariableRef = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3025:2: (iv_ruleVariableRef= ruleVariableRef EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3026:2: iv_ruleVariableRef= ruleVariableRef EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getVariableRefRule()); 
            }
            pushFollow(FOLLOW_ruleVariableRef_in_entryRuleVariableRef6695);
            iv_ruleVariableRef=ruleVariableRef();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleVariableRef; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleVariableRef6705); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3033:1: ruleVariableRef returns [EObject current=null] : ( () ( (otherlv_1= RULE_ID ) ) ) ;
    public final EObject ruleVariableRef() throws RecognitionException {
        EObject current = null;

        Token otherlv_1=null;

         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3036:28: ( ( () ( (otherlv_1= RULE_ID ) ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3037:1: ( () ( (otherlv_1= RULE_ID ) ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3037:1: ( () ( (otherlv_1= RULE_ID ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3037:2: () ( (otherlv_1= RULE_ID ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3037:2: ()
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3038:5: 
            {
            if ( state.backtracking==0 ) {

                      current = forceCreateModelElement(
                          grammarAccess.getVariableRefAccess().getVariableRefAction_0(),
                          current);
                  
            }

            }

            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3043:2: ( (otherlv_1= RULE_ID ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3044:1: (otherlv_1= RULE_ID )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3044:1: (otherlv_1= RULE_ID )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3045:3: otherlv_1= RULE_ID
            {
            if ( state.backtracking==0 ) {

              			if (current==null) {
              	            current = createModelElement(grammarAccess.getVariableRefRule());
              	        }
                      
            }
            otherlv_1=(Token)match(input,RULE_ID,FOLLOW_RULE_ID_in_ruleVariableRef6759); if (state.failed) return current;
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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3066:1: entryRuleTerminalExpression returns [EObject current=null] : iv_ruleTerminalExpression= ruleTerminalExpression EOF ;
    public final EObject entryRuleTerminalExpression() throws RecognitionException {
        EObject current = null;

        EObject iv_ruleTerminalExpression = null;


        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3067:2: (iv_ruleTerminalExpression= ruleTerminalExpression EOF )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3068:2: iv_ruleTerminalExpression= ruleTerminalExpression EOF
            {
            if ( state.backtracking==0 ) {
               newCompositeNode(grammarAccess.getTerminalExpressionRule()); 
            }
            pushFollow(FOLLOW_ruleTerminalExpression_in_entryRuleTerminalExpression6797);
            iv_ruleTerminalExpression=ruleTerminalExpression();

            state._fsp--;
            if (state.failed) return current;
            if ( state.backtracking==0 ) {
               current =iv_ruleTerminalExpression; 
            }
            match(input,EOF,FOLLOW_EOF_in_entryRuleTerminalExpression6807); if (state.failed) return current;

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
    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3075:1: ruleTerminalExpression returns [EObject current=null] : ( ( () ( (lv_value_1_0= RULE_INTEGER ) ) ) | ( () ( (lv_value_3_0= RULE_DOUBLE ) ) ) | ( () ( (lv_value_5_0= RULE_COLOR ) ) ) | ( () ( (lv_value_7_0= RULE_STRING ) ) ) | ( () ( (lv_value_9_0= RULE_BOOLEAN ) ) ) ) ;
    public final EObject ruleTerminalExpression() throws RecognitionException {
        EObject current = null;

        Token lv_value_1_0=null;
        Token lv_value_3_0=null;
        Token lv_value_5_0=null;
        Token lv_value_7_0=null;
        Token lv_value_9_0=null;

         enterRule(); 
            
        try {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3078:28: ( ( ( () ( (lv_value_1_0= RULE_INTEGER ) ) ) | ( () ( (lv_value_3_0= RULE_DOUBLE ) ) ) | ( () ( (lv_value_5_0= RULE_COLOR ) ) ) | ( () ( (lv_value_7_0= RULE_STRING ) ) ) | ( () ( (lv_value_9_0= RULE_BOOLEAN ) ) ) ) )
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3079:1: ( ( () ( (lv_value_1_0= RULE_INTEGER ) ) ) | ( () ( (lv_value_3_0= RULE_DOUBLE ) ) ) | ( () ( (lv_value_5_0= RULE_COLOR ) ) ) | ( () ( (lv_value_7_0= RULE_STRING ) ) ) | ( () ( (lv_value_9_0= RULE_BOOLEAN ) ) ) )
            {
            // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3079:1: ( ( () ( (lv_value_1_0= RULE_INTEGER ) ) ) | ( () ( (lv_value_3_0= RULE_DOUBLE ) ) ) | ( () ( (lv_value_5_0= RULE_COLOR ) ) ) | ( () ( (lv_value_7_0= RULE_STRING ) ) ) | ( () ( (lv_value_9_0= RULE_BOOLEAN ) ) ) )
            int alt45=5;
            switch ( input.LA(1) ) {
            case RULE_INTEGER:
                {
                alt45=1;
                }
                break;
            case RULE_DOUBLE:
                {
                alt45=2;
                }
                break;
            case RULE_COLOR:
                {
                alt45=3;
                }
                break;
            case RULE_STRING:
                {
                alt45=4;
                }
                break;
            case RULE_BOOLEAN:
                {
                alt45=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return current;}
                NoViableAltException nvae =
                    new NoViableAltException("", 45, 0, input);

                throw nvae;
            }

            switch (alt45) {
                case 1 :
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3079:2: ( () ( (lv_value_1_0= RULE_INTEGER ) ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3079:2: ( () ( (lv_value_1_0= RULE_INTEGER ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3079:3: () ( (lv_value_1_0= RULE_INTEGER ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3079:3: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3080:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getTerminalExpressionAccess().getIntLiteralAction_0_0(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3085:2: ( (lv_value_1_0= RULE_INTEGER ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3086:1: (lv_value_1_0= RULE_INTEGER )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3086:1: (lv_value_1_0= RULE_INTEGER )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3087:3: lv_value_1_0= RULE_INTEGER
                    {
                    lv_value_1_0=(Token)match(input,RULE_INTEGER,FOLLOW_RULE_INTEGER_in_ruleTerminalExpression6859); if (state.failed) return current;
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3104:6: ( () ( (lv_value_3_0= RULE_DOUBLE ) ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3104:6: ( () ( (lv_value_3_0= RULE_DOUBLE ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3104:7: () ( (lv_value_3_0= RULE_DOUBLE ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3104:7: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3105:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getTerminalExpressionAccess().getDoubleLiteralAction_1_0(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3110:2: ( (lv_value_3_0= RULE_DOUBLE ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3111:1: (lv_value_3_0= RULE_DOUBLE )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3111:1: (lv_value_3_0= RULE_DOUBLE )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3112:3: lv_value_3_0= RULE_DOUBLE
                    {
                    lv_value_3_0=(Token)match(input,RULE_DOUBLE,FOLLOW_RULE_DOUBLE_in_ruleTerminalExpression6898); if (state.failed) return current;
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3129:6: ( () ( (lv_value_5_0= RULE_COLOR ) ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3129:6: ( () ( (lv_value_5_0= RULE_COLOR ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3129:7: () ( (lv_value_5_0= RULE_COLOR ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3129:7: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3130:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getTerminalExpressionAccess().getColorLiteralAction_2_0(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3135:2: ( (lv_value_5_0= RULE_COLOR ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3136:1: (lv_value_5_0= RULE_COLOR )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3136:1: (lv_value_5_0= RULE_COLOR )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3137:3: lv_value_5_0= RULE_COLOR
                    {
                    lv_value_5_0=(Token)match(input,RULE_COLOR,FOLLOW_RULE_COLOR_in_ruleTerminalExpression6937); if (state.failed) return current;
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3154:6: ( () ( (lv_value_7_0= RULE_STRING ) ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3154:6: ( () ( (lv_value_7_0= RULE_STRING ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3154:7: () ( (lv_value_7_0= RULE_STRING ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3154:7: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3155:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getTerminalExpressionAccess().getStringLiteralAction_3_0(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3160:2: ( (lv_value_7_0= RULE_STRING ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3161:1: (lv_value_7_0= RULE_STRING )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3161:1: (lv_value_7_0= RULE_STRING )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3162:3: lv_value_7_0= RULE_STRING
                    {
                    lv_value_7_0=(Token)match(input,RULE_STRING,FOLLOW_RULE_STRING_in_ruleTerminalExpression6976); if (state.failed) return current;
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
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3179:6: ( () ( (lv_value_9_0= RULE_BOOLEAN ) ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3179:6: ( () ( (lv_value_9_0= RULE_BOOLEAN ) ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3179:7: () ( (lv_value_9_0= RULE_BOOLEAN ) )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3179:7: ()
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3180:5: 
                    {
                    if ( state.backtracking==0 ) {

                              current = forceCreateModelElement(
                                  grammarAccess.getTerminalExpressionAccess().getBooleanLiteralAction_4_0(),
                                  current);
                          
                    }

                    }

                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3185:2: ( (lv_value_9_0= RULE_BOOLEAN ) )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3186:1: (lv_value_9_0= RULE_BOOLEAN )
                    {
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3186:1: (lv_value_9_0= RULE_BOOLEAN )
                    // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:3187:3: lv_value_9_0= RULE_BOOLEAN
                    {
                    lv_value_9_0=(Token)match(input,RULE_BOOLEAN,FOLLOW_RULE_BOOLEAN_in_ruleTerminalExpression7015); if (state.failed) return current;
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
        // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:820:4: ( 'else' )
        // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:820:6: 'else'
        {
        match(input,41,FOLLOW_41_in_synpred1_InternalGaml1758); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_InternalGaml

    // $ANTLR start synpred2_InternalGaml
    public final void synpred2_InternalGaml_fragment() throws RecognitionException {   
        // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2054:3: ( ruleArgPairExpr )
        // ../msi.gama.lang.gaml/src-gen/msi/gama/lang/gaml/parser/antlr/internal/InternalGaml.g:2054:5: ruleArgPairExpr
        {
        pushFollow(FOLLOW_ruleArgPairExpr_in_synpred2_InternalGaml4492);
        ruleArgPairExpr();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred2_InternalGaml

    // Delegated rules

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


    protected DFA29 dfa29 = new DFA29(this);
    static final String DFA29_eotS =
        "\20\uffff";
    static final String DFA29_eofS =
        "\20\uffff";
    static final String DFA29_minS =
        "\1\4\1\0\16\uffff";
    static final String DFA29_maxS =
        "\1\111\1\0\16\uffff";
    static final String DFA29_acceptS =
        "\2\uffff\1\2\14\uffff\1\1";
    static final String DFA29_specialS =
        "\1\uffff\1\0\16\uffff}>";
    static final String[] DFA29_transitionS = {
            "\1\1\5\2\47\uffff\1\2\13\uffff\1\2\4\uffff\4\2\1\uffff\1\2\1"+
            "\uffff\1\2",
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
            ""
    };

    static final short[] DFA29_eot = DFA.unpackEncodedString(DFA29_eotS);
    static final short[] DFA29_eof = DFA.unpackEncodedString(DFA29_eofS);
    static final char[] DFA29_min = DFA.unpackEncodedStringToUnsignedChars(DFA29_minS);
    static final char[] DFA29_max = DFA.unpackEncodedStringToUnsignedChars(DFA29_maxS);
    static final short[] DFA29_accept = DFA.unpackEncodedString(DFA29_acceptS);
    static final short[] DFA29_special = DFA.unpackEncodedString(DFA29_specialS);
    static final short[][] DFA29_transition;

    static {
        int numStates = DFA29_transitionS.length;
        DFA29_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA29_transition[i] = DFA.unpackEncodedString(DFA29_transitionS[i]);
        }
    }

    class DFA29 extends DFA {

        public DFA29(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 29;
            this.eot = DFA29_eot;
            this.eof = DFA29_eof;
            this.min = DFA29_min;
            this.max = DFA29_max;
            this.accept = DFA29_accept;
            this.special = DFA29_special;
            this.transition = DFA29_transition;
        }
        public String getDescription() {
            return "2054:1: ( ( ( ruleArgPairExpr )=>this_ArgPairExpr_0= ruleArgPairExpr ) | (this_Addition_1= ruleAddition ( ( () ( (lv_op_3_0= '::' ) ) ) ( (lv_right_4_0= ruleAddition ) ) )? ) )";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA29_1 = input.LA(1);

                         
                        int index29_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_InternalGaml()) ) {s = 15;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index29_1);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 29, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_ruleModel_in_entryRuleModel75 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleModel85 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_14_in_ruleModel122 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleModel139 = new BitSet(new long[]{0x0000017FFFC28012L});
    public static final BitSet FOLLOW_ruleImport_in_ruleModel165 = new BitSet(new long[]{0x0000017FFFC28012L});
    public static final BitSet FOLLOW_15_in_ruleModel180 = new BitSet(new long[]{0x0000000000340000L});
    public static final BitSet FOLLOW_ruleGamlLangDef_in_ruleModel201 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_ruleModel213 = new BitSet(new long[]{0x0000017FFFC00012L});
    public static final BitSet FOLLOW_ruleStatement_in_ruleModel236 = new BitSet(new long[]{0x0000017FFFC00012L});
    public static final BitSet FOLLOW_ruleImport_in_entryRuleImport274 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleImport284 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_17_in_ruleImport321 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleImport338 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleGamlLangDef_in_entryRuleGamlLangDef379 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleGamlLangDef389 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDefBinaryOp_in_ruleGamlLangDef435 = new BitSet(new long[]{0x0000000000340002L});
    public static final BitSet FOLLOW_ruleDefReserved_in_ruleGamlLangDef462 = new BitSet(new long[]{0x0000000000340002L});
    public static final BitSet FOLLOW_ruleDefUnary_in_ruleGamlLangDef489 = new BitSet(new long[]{0x0000000000340002L});
    public static final BitSet FOLLOW_ruleDefBinaryOp_in_entryRuleDefBinaryOp526 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDefBinaryOp536 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_18_in_ruleDefBinaryOp573 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleDefBinaryOp590 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleDefBinaryOp607 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDefReserved_in_entryRuleDefReserved643 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDefReserved653 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_20_in_ruleDefReserved690 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleDefReserved707 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleDefReserved724 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDefUnary_in_entryRuleDefUnary760 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDefUnary770 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_21_in_ruleDefUnary807 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleDefUnary824 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_ruleDefUnary841 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleBuiltIn_in_entryRuleBuiltIn878 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleBuiltIn889 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_22_in_ruleBuiltIn927 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_23_in_ruleBuiltIn946 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_24_in_ruleBuiltIn965 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_ruleBuiltIn984 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_26_in_ruleBuiltIn1003 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_27_in_ruleBuiltIn1022 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_28_in_ruleBuiltIn1041 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_29_in_ruleBuiltIn1060 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_30_in_ruleBuiltIn1079 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_31_in_ruleBuiltIn1098 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_32_in_ruleBuiltIn1117 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_33_in_ruleBuiltIn1136 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_34_in_ruleBuiltIn1155 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_ruleBuiltIn1174 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_36_in_ruleBuiltIn1193 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_37_in_ruleBuiltIn1212 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_38_in_ruleBuiltIn1231 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleStatement_in_entryRuleStatement1271 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleStatement1281 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleIfEval_in_ruleStatement1328 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleClassicStatement_in_ruleStatement1355 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDefinition_in_ruleStatement1382 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleClassicStatement_in_entryRuleClassicStatement1417 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleClassicStatement1427 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleBuiltIn_in_ruleClassicStatement1473 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_ruleGamlFacetRef_in_ruleClassicStatement1494 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_ruleExpression_in_ruleClassicStatement1516 = new BitSet(new long[]{0x0003F88000000010L});
    public static final BitSet FOLLOW_ruleFacetExpr_in_ruleClassicStatement1537 = new BitSet(new long[]{0x0003F88000000010L});
    public static final BitSet FOLLOW_ruleBlock_in_ruleClassicStatement1560 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_39_in_ruleClassicStatement1578 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleIfEval_in_entryRuleIfEval1615 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleIfEval1625 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_40_in_ruleIfEval1668 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_ruleGamlFacetRef_in_ruleIfEval1702 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_ruleExpression_in_ruleIfEval1724 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_ruleBlock_in_ruleIfEval1745 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_41_in_ruleIfEval1766 = new BitSet(new long[]{0x0002017FFFC00010L});
    public static final BitSet FOLLOW_ruleStatement_in_ruleIfEval1790 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleBlock_in_ruleIfEval1809 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDefinition_in_entryRuleDefinition1850 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDefinition1860 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleDefinition1902 = new BitSet(new long[]{0x0003F8FFFFC00030L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleDefinition1925 = new BitSet(new long[]{0x0003F88000000010L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleDefinition1953 = new BitSet(new long[]{0x0003F88000000010L});
    public static final BitSet FOLLOW_ruleBuiltIn_in_ruleDefinition1985 = new BitSet(new long[]{0x0003F88000000010L});
    public static final BitSet FOLLOW_ruleFacetExpr_in_ruleDefinition2008 = new BitSet(new long[]{0x0003F88000000010L});
    public static final BitSet FOLLOW_ruleBlock_in_ruleDefinition2031 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_39_in_ruleDefinition2049 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleGamlFacetRef_in_entryRuleGamlFacetRef2088 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleGamlFacetRef2098 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleGamlFacetRef2141 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_ruleGamlFacetRef2158 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_43_in_ruleGamlFacetRef2183 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFunctionGamlFacetRef_in_entryRuleFunctionGamlFacetRef2232 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleFunctionGamlFacetRef2242 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_44_in_ruleFunctionGamlFacetRef2286 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_ruleFunctionGamlFacetRef2311 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_45_in_ruleFunctionGamlFacetRef2336 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFacetExpr_in_entryRuleFacetExpr2385 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleFacetExpr2395 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFunctionFacetExpr_in_ruleFacetExpr2442 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDefinitionFacetExpr_in_ruleFacetExpr2469 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleGamlFacetRef_in_ruleFacetExpr2496 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_ruleExpression_in_ruleFacetExpr2517 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleDefinitionFacetExpr_in_entryRuleDefinitionFacetExpr2554 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleDefinitionFacetExpr2564 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleReturnsFacetExpr_in_ruleDefinitionFacetExpr2611 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNameFacetExpr_in_ruleDefinitionFacetExpr2638 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleActionFacetExpr_in_ruleDefinitionFacetExpr2665 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleNameFacetExpr_in_entryRuleNameFacetExpr2700 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleNameFacetExpr2710 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_ruleNameFacetExpr2747 = new BitSet(new long[]{0x0000007FFFC00030L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleNameFacetExpr2765 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleNameFacetExpr2793 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleBuiltIn_in_ruleNameFacetExpr2825 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleReturnsFacetExpr_in_entryRuleReturnsFacetExpr2862 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleReturnsFacetExpr2872 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_47_in_ruleReturnsFacetExpr2909 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleReturnsFacetExpr2926 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleActionFacetExpr_in_entryRuleActionFacetExpr2967 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleActionFacetExpr2977 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_48_in_ruleActionFacetExpr3014 = new BitSet(new long[]{0x0000007FFFC00030L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleActionFacetExpr3032 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleActionFacetExpr3060 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleBuiltIn_in_ruleActionFacetExpr3092 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFunctionFacetExpr_in_entryRuleFunctionFacetExpr3129 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleFunctionFacetExpr3139 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFunctionGamlFacetRef_in_ruleFunctionFacetExpr3185 = new BitSet(new long[]{0x0002000000000000L});
    public static final BitSet FOLLOW_49_in_ruleFunctionFacetExpr3197 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_ruleExpression_in_ruleFunctionFacetExpr3218 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_ruleFunctionFacetExpr3230 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleBlock_in_entryRuleBlock3266 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleBlock3276 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_ruleBlock3322 = new BitSet(new long[]{0x0000017FFFC10010L});
    public static final BitSet FOLLOW_ruleStatement_in_ruleBlock3343 = new BitSet(new long[]{0x0000017FFFC10010L});
    public static final BitSet FOLLOW_16_in_ruleBlock3356 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleExpression_in_entryRuleExpression3392 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleExpression3402 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTernExp_in_ruleExpression3448 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTernExp_in_entryRuleTernExp3482 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTernExp3492 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOrExp_in_ruleTernExp3539 = new BitSet(new long[]{0x0004000000000002L});
    public static final BitSet FOLLOW_50_in_ruleTernExp3566 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_ruleOrExp_in_ruleTernExp3600 = new BitSet(new long[]{0x0000040000000000L});
    public static final BitSet FOLLOW_42_in_ruleTernExp3612 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_ruleOrExp_in_ruleTernExp3633 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleOrExp_in_entryRuleOrExp3671 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleOrExp3681 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAndExp_in_ruleOrExp3728 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_51_in_ruleOrExp3755 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_ruleAndExp_in_ruleOrExp3789 = new BitSet(new long[]{0x0008000000000002L});
    public static final BitSet FOLLOW_ruleAndExp_in_entryRuleAndExp3827 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAndExp3837 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleRelational_in_ruleAndExp3884 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_52_in_ruleAndExp3911 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_ruleRelational_in_ruleAndExp3945 = new BitSet(new long[]{0x0010000000000002L});
    public static final BitSet FOLLOW_ruleRelational_in_entryRuleRelational3983 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleRelational3993 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePairExpr_in_ruleRelational4040 = new BitSet(new long[]{0x07E0000000000002L});
    public static final BitSet FOLLOW_53_in_ruleRelational4070 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_54_in_ruleRelational4099 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_55_in_ruleRelational4128 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_56_in_ruleRelational4157 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_57_in_ruleRelational4186 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_58_in_ruleRelational4215 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_rulePairExpr_in_ruleRelational4253 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArgPairExpr_in_entryRuleArgPairExpr4291 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleArgPairExpr4301 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleArgPairExpr4352 = new BitSet(new long[]{0x0800000000000000L});
    public static final BitSet FOLLOW_59_in_ruleArgPairExpr4375 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_ruleAddition_in_ruleArgPairExpr4409 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePairExpr_in_entryRulePairExpr4445 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRulePairExpr4455 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArgPairExpr_in_rulePairExpr4508 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAddition_in_rulePairExpr4537 = new BitSet(new long[]{0x0800000000000002L});
    public static final BitSet FOLLOW_59_in_rulePairExpr4565 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_ruleAddition_in_rulePairExpr4600 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAddition_in_entryRuleAddition4639 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAddition4649 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleMultiplication_in_ruleAddition4696 = new BitSet(new long[]{0x3000000000000002L});
    public static final BitSet FOLLOW_60_in_ruleAddition4726 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_61_in_ruleAddition4755 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_ruleMultiplication_in_ruleAddition4793 = new BitSet(new long[]{0x3000000000000002L});
    public static final BitSet FOLLOW_ruleMultiplication_in_entryRuleMultiplication4831 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleMultiplication4841 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleGamlBinaryExpr_in_ruleMultiplication4888 = new BitSet(new long[]{0xC000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_62_in_ruleMultiplication4918 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_63_in_ruleMultiplication4947 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_64_in_ruleMultiplication4976 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_ruleGamlBinaryExpr_in_ruleMultiplication5014 = new BitSet(new long[]{0xC000000000000002L,0x0000000000000001L});
    public static final BitSet FOLLOW_ruleGamlBinaryExpr_in_entryRuleGamlBinaryExpr5052 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleGamlBinaryExpr5062 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleGamlUnitExpr_in_ruleGamlBinaryExpr5109 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleGamlBinaryExpr5136 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_ruleGamlUnitExpr_in_ruleGamlBinaryExpr5163 = new BitSet(new long[]{0x0000000000000012L});
    public static final BitSet FOLLOW_ruleGamlUnitExpr_in_entryRuleGamlUnitExpr5201 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleGamlUnitExpr5211 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleGamlUnaryExpr_in_ruleGamlUnitExpr5258 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_65_in_ruleGamlUnitExpr5286 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleUnitName_in_ruleGamlUnitExpr5321 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleGamlUnaryExpr_in_entryRuleGamlUnaryExpr5359 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleGamlUnaryExpr5369 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePrePrimaryExpr_in_ruleGamlUnaryExpr5416 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_61_in_ruleGamlUnaryExpr5461 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_66_in_ruleGamlUnaryExpr5490 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_67_in_ruleGamlUnaryExpr5519 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_68_in_ruleGamlUnaryExpr5548 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_69_in_ruleGamlUnaryExpr5577 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_ruleGamlUnaryExpr_in_ruleGamlUnaryExpr5614 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePrePrimaryExpr_in_entryRulePrePrimaryExpr5652 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRulePrePrimaryExpr5662 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTerminalExpression_in_rulePrePrimaryExpr5709 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleMemberRef_in_rulePrePrimaryExpr5736 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleMemberRef_in_entryRuleMemberRef5771 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleMemberRef5781 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_rulePrimaryExpression_in_ruleMemberRef5828 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_70_in_ruleMemberRef5855 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_ruleVariableRef_in_ruleMemberRef5889 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000040L});
    public static final BitSet FOLLOW_rulePrimaryExpression_in_entryRulePrimaryExpression5927 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRulePrimaryExpression5937 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAbstractRef_in_rulePrimaryExpression5984 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_71_in_rulePrimaryExpression6002 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_ruleExpression_in_rulePrimaryExpression6024 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000100L});
    public static final BitSet FOLLOW_72_in_rulePrimaryExpression6035 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_73_in_rulePrimaryExpression6055 = new BitSet(new long[]{0x20020800000003F0L,0x0000000000000ABCL});
    public static final BitSet FOLLOW_ruleExpression_in_rulePrimaryExpression6086 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000C00L});
    public static final BitSet FOLLOW_74_in_rulePrimaryExpression6099 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_ruleExpression_in_rulePrimaryExpression6120 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000C00L});
    public static final BitSet FOLLOW_75_in_rulePrimaryExpression6136 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_rulePrimaryExpression6156 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_ruleExpression_in_rulePrimaryExpression6187 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000400L});
    public static final BitSet FOLLOW_74_in_rulePrimaryExpression6205 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_ruleExpression_in_rulePrimaryExpression6239 = new BitSet(new long[]{0x0000000000010000L});
    public static final BitSet FOLLOW_16_in_rulePrimaryExpression6252 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleAbstractRef_in_entryRuleAbstractRef6289 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleAbstractRef6299 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleVariableRef_in_ruleAbstractRef6346 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFunction_in_ruleAbstractRef6373 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleFunction_in_entryRuleFunction6408 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleFunction6418 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleFunction6470 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000080L});
    public static final BitSet FOLLOW_71_in_ruleFunction6487 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_ruleExpression_in_ruleFunction6508 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000500L});
    public static final BitSet FOLLOW_74_in_ruleFunction6521 = new BitSet(new long[]{0x20020800000003F0L,0x00000000000002BCL});
    public static final BitSet FOLLOW_ruleExpression_in_ruleFunction6542 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000500L});
    public static final BitSet FOLLOW_72_in_ruleFunction6556 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleUnitName_in_entryRuleUnitName6593 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleUnitName6603 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleUnitName6654 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleVariableRef_in_entryRuleVariableRef6695 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleVariableRef6705 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_ID_in_ruleVariableRef6759 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleTerminalExpression_in_entryRuleTerminalExpression6797 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_entryRuleTerminalExpression6807 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_INTEGER_in_ruleTerminalExpression6859 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_DOUBLE_in_ruleTerminalExpression6898 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_COLOR_in_ruleTerminalExpression6937 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_STRING_in_ruleTerminalExpression6976 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RULE_BOOLEAN_in_ruleTerminalExpression7015 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_41_in_synpred1_InternalGaml1758 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ruleArgPairExpr_in_synpred2_InternalGaml4492 = new BitSet(new long[]{0x0000000000000002L});

}