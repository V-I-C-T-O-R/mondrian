/* Decompiler 164ms, total 1061ms, lines 324 */
package mondrian.rolap.aggmatcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import mondrian.olap.MondrianProperties;
import mondrian.recorder.ListRecorder;
import mondrian.recorder.MessageRecorder;
import mondrian.recorder.RecorderException;
import mondrian.resource.MondrianResource;
import mondrian.rolap.RolapStar;
import mondrian.rolap.aggmatcher.DefaultDef.AggRule;
import mondrian.rolap.aggmatcher.DefaultDef.AggRules;
import mondrian.rolap.aggmatcher.DefaultDef.FactCountMatch;
import mondrian.rolap.aggmatcher.DefaultDef.ForeignKeyMatch;
import mondrian.rolap.aggmatcher.DefaultDef.IgnoreMap;
import mondrian.rolap.aggmatcher.DefaultDef.TableMatch;
import mondrian.rolap.aggmatcher.JdbcSchema.Table;
import mondrian.rolap.aggmatcher.Recognizer.Matcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eigenbase.util.property.Property;
import org.eigenbase.util.property.Trigger;
import org.eigenbase.util.property.Trigger.VetoRT;
import org.eigenbase.xom.DOMWrapper;
import org.eigenbase.xom.Parser;
import org.eigenbase.xom.XOMException;
import org.eigenbase.xom.XOMUtil;

public class DefaultRules {
    private static final Logger LOGGER = LogManager.getLogger(DefaultRules.class);
    private static final MondrianResource mres = MondrianResource.instance();
    private static DefaultRules instance = null;
    private final AggRules rules;
    private final Map<String, Matcher> factToPattern;
    private final Map<String, Matcher> foreignKeyMatcherMap;
    private Matcher ignoreMatcherMap;
    private Matcher factCountMatcher;
    private String tag;

    public static synchronized DefaultRules getInstance() {
        if (instance == null) {
            InputStream inStream = getAggRuleInputStream();
            if (inStream == null) {
                return null;
            }

            AggRules defs = makeAggRules(inStream);
            ListRecorder reclists = new ListRecorder();

            try {
                defs.validate(reclists);
            } catch (RecorderException var6) {
            }

            reclists.logWarningMessage(LOGGER);
            reclists.logErrorMessage(LOGGER);
            if (reclists.hasErrors()) {
                reclists.throwRTException();
            }

            String tag = MondrianProperties.instance().AggregateRuleTag.get();
            AggRule aggrule = defs.getAggRule(tag);
            if (aggrule == null) {
                throw mres.MissingDefaultAggRule.ex(tag);
            }

            DefaultRules rules = new DefaultRules(defs);
            rules.setTag(tag);
            instance = rules;
        }

        return instance;
    }

    private static InputStream getAggRuleInputStream() {
        String aggRules = MondrianProperties.instance().AggregateRules.get();
        InputStream inStream = DefaultRules.class.getResourceAsStream(aggRules);
        if (inStream == null) {
            try {
                URL url = new URL(aggRules);
                inStream = url.openStream();
            } catch (MalformedURLException var3) {
            } catch (IOException var4) {
            }
        }

        if (inStream == null) {
            LOGGER.warn(mres.CouldNotLoadDefaultAggregateRules.str(aggRules));
        }

        return inStream;
    }

    protected static AggRules makeAggRules(File file) {
        DOMWrapper def = makeDOMWrapper(file);

        try {
            AggRules rules = new AggRules(def);
            return rules;
        } catch (XOMException var3) {
            throw mres.AggRuleParse.ex(file.getName(), var3);
        }
    }

    protected static AggRules makeAggRules(URL url) {
        DOMWrapper def = makeDOMWrapper(url);

        try {
            AggRules rules = new AggRules(def);
            return rules;
        } catch (XOMException var3) {
            throw mres.AggRuleParse.ex(url.toString(), var3);
        }
    }

    protected static AggRules makeAggRules(InputStream inStream) {
        DOMWrapper def = makeDOMWrapper(inStream);

        try {
            AggRules rules = new AggRules(def);
            return rules;
        } catch (XOMException var3) {
            throw mres.AggRuleParse.ex("InputStream", var3);
        }
    }

    protected static AggRules makeAggRules(String text, String name) {
        DOMWrapper def = makeDOMWrapper(text, name);

        try {
            AggRules rules = new AggRules(def);
            return rules;
        } catch (XOMException var4) {
            throw mres.AggRuleParse.ex(name, var4);
        }
    }

    protected static DOMWrapper makeDOMWrapper(File file) {
        try {
            return makeDOMWrapper(file.toURL());
        } catch (MalformedURLException var2) {
            throw mres.AggRuleParse.ex(file.getName(), var2);
        }
    }

    protected static DOMWrapper makeDOMWrapper(URL url) {
        try {
            Parser xmlParser = XOMUtil.createDefaultParser();
            DOMWrapper def = xmlParser.parse(url);
            return def;
        } catch (XOMException var3) {
            throw mres.AggRuleParse.ex(url.toString(), var3);
        }
    }

    protected static DOMWrapper makeDOMWrapper(InputStream inStream) {
        try {
            Parser xmlParser = XOMUtil.createDefaultParser();
            DOMWrapper def = xmlParser.parse(inStream);
            return def;
        } catch (XOMException var3) {
            throw mres.AggRuleParse.ex("InputStream", var3);
        }
    }

    protected static DOMWrapper makeDOMWrapper(String text, String name) {
        try {
            Parser xmlParser = XOMUtil.createDefaultParser();
            DOMWrapper def = xmlParser.parse(text);
            return def;
        } catch (XOMException var4) {
            throw mres.AggRuleParse.ex(name, var4);
        }
    }

    private DefaultRules(AggRules rules) {
        this.rules = rules;
        this.factToPattern = new HashMap();
        this.foreignKeyMatcherMap = new HashMap();
        this.tag = MondrianProperties.instance().AggregateRuleTag.getDefaultValue();
    }

    public void validate(MessageRecorder msgRecorder) {
        this.rules.validate(msgRecorder);
    }

    private void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return this.tag;
    }

    public AggRule getAggRule() {
        return this.getAggRule(this.getTag());
    }

    public AggRule getAggRule(String tag) {
        return this.rules.getAggRule(tag);
    }

    public Matcher getTableMatcher(String tableName) {
        Matcher matcher = (Matcher)this.factToPattern.get(tableName);
        if (matcher == null) {
            AggRule rule = this.getAggRule();
            TableMatch tableMatch = rule.getTableMatch();
            matcher = tableMatch.getMatcher(tableName);
            this.factToPattern.put(tableName, matcher);
        }

        return matcher;
    }

    public Matcher getIgnoreMatcher() {
        if (this.ignoreMatcherMap == null) {
            AggRule rule = this.getAggRule();
            IgnoreMap ignoreMatch = rule.getIgnoreMap();
            if (ignoreMatch == null) {
                this.ignoreMatcherMap = new Matcher() {
                    public boolean matches(String name) {
                        return false;
                    }
                };
            } else {
                this.ignoreMatcherMap = ignoreMatch.getMatcher();
            }
        }

        return this.ignoreMatcherMap;
    }

    public Matcher getFactCountMatcher() {
        if (this.factCountMatcher == null) {
            AggRule rule = this.getAggRule();
            FactCountMatch factCountMatch = rule.getFactCountMatch();
            this.factCountMatcher = factCountMatch.getMatcher();
        }

        return this.factCountMatcher;
    }

    public Matcher getForeignKeyMatcher(String foreignKeyName) {
        Matcher matcher = (Matcher)this.foreignKeyMatcherMap.get(foreignKeyName);
        if (matcher == null) {
            AggRule rule = this.getAggRule();
            ForeignKeyMatch foreignKeyMatch = rule.getForeignKeyMatch();
            matcher = foreignKeyMatch.getMatcher(foreignKeyName);
            this.foreignKeyMatcherMap.put(foreignKeyName, matcher);
        }

        return matcher;
    }

    public boolean matchesTableName(String factTableName, String name) {
        Matcher matcher = this.getTableMatcher(factTableName);
        return matcher.matches(name);
    }

    public Matcher getMeasureMatcher(String measureName, String measureColumnName, String aggregateName) {
        AggRule rule = this.getAggRule();
        Matcher matcher = rule.getMeasureMap().getMatcher(measureName, measureColumnName, aggregateName);
        return matcher;
    }

    public Matcher getLevelMatcher(String usagePrefix, String hierarchyName, String levelName, String levelColumnName) {
        AggRule rule = this.getAggRule();
        Matcher matcher = rule.getLevelMap().getMatcher(usagePrefix, hierarchyName, levelName, levelColumnName);
        return matcher;
    }

    public boolean columnsOK(RolapStar star, Table dbFactTable, Table aggTable, MessageRecorder msgRecorder) {
        Recognizer cb = new DefaultRecognizer(this, star, dbFactTable, aggTable, msgRecorder);
        return cb.check();
    }

    static {
        Trigger trigger = new Trigger() {
            public boolean isPersistent() {
                return true;
            }

            public int phase() {
                return 1;
            }

            public void execute(Property property, String value) {
                Class var3 = DefaultRules.class;
                synchronized(DefaultRules.class) {
                    DefaultRules oldInstance = DefaultRules.instance;
                    DefaultRules.instance = null;
                    DefaultRules newInstance = null;
                    Exception ex = null;

                    try {
                        newInstance = DefaultRules.getInstance();
                    } catch (Exception var9) {
                        ex = var9;
                    }

                    if (ex != null) {
                        DefaultRules.instance = oldInstance;
                        throw new VetoRT(ex);
                    } else if (newInstance == null) {
                        DefaultRules.instance = oldInstance;
                        String msg = DefaultRules.mres.FailedCreateNewDefaultAggregateRules.str(property.getPath(), value);
                        throw new VetoRT(msg);
                    } else {
                        DefaultRules.instance = newInstance;
                    }
                }
            }
        };
        MondrianProperties properties = MondrianProperties.instance();
        properties.AggregateRules.addTrigger(trigger);
        properties.AggregateRuleTag.addTrigger(trigger);
    }
}