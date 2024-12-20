/* Decompiler 2430ms, total 3680ms, lines 3173 */
package mondrian.olap;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.ref.Reference;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.RandomAccess;
import java.util.Set;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TreeSet;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mondrian.calc.Calc;
import mondrian.calc.CalcWriter;
import mondrian.mdx.DimensionExpr;
import mondrian.mdx.HierarchyExpr;
import mondrian.mdx.LevelExpr;
import mondrian.mdx.MemberExpr;
import mondrian.mdx.NamedSetExpr;
import mondrian.mdx.ParameterExpr;
import mondrian.mdx.QueryPrintWriter;
import mondrian.mdx.UnresolvedFunCall;
import mondrian.olap.Id.KeySegment;
import mondrian.olap.Id.NameSegment;
import mondrian.olap.Id.Quoting;
import mondrian.olap.Id.Segment;
import mondrian.olap.fun.FunUtil;
import mondrian.olap.fun.Resolver;
import mondrian.olap.fun.Resolver.Conversion;
import mondrian.olap.fun.sort.Sorter;
import mondrian.olap.type.Type;
import mondrian.resource.MondrianResource;
import mondrian.rolap.RolapCube;
import mondrian.rolap.RolapCubeDimension;
import mondrian.rolap.RolapLevel;
import mondrian.rolap.RolapMember;
import mondrian.rolap.RolapUtil;
import mondrian.spi.ProfileHandler;
import mondrian.spi.UserDefinedFunction;
import mondrian.util.ArraySortedSet;
import mondrian.util.ConcatenableList;
import mondrian.util.Pair;
import mondrian.util.UtilCompatible;
import mondrian.util.UtilCompatibleJdk16;
import org.apache.commons.collections.keyvalue.AbstractMapEntry;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.provider.http.HttpFileObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.WriterAppender;
import org.apache.logging.log4j.core.appender.WriterAppender.Builder;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.eigenbase.xom.XOMUtil;
import org.olap4j.impl.IdentifierParser;
import org.olap4j.impl.Olap4jUtil;
import org.olap4j.mdx.IdentifierNode;
import org.olap4j.mdx.IdentifierSegment;
import org.olap4j.mdx.ParseRegion;

public class Util extends XOMUtil {
    public static final String nl = System.getProperty("line.separator");
    private static final Logger LOGGER = LogManager.getLogger(Util.class);
    public static final Object nullValue = new Double(1.2345E-8D);
    public static final Object EmptyValue = new Double(-1.2345E-8D);
    private static long databaseMillis = 0L;
    private static final Random metaRandom;
    public static final UUID JVM_INSTANCE_UUID;
    public static final boolean IBM_JVM;
    public static final int JdbcVersion;
    public static final boolean Retrowoven;
    private static final UtilCompatible compatible;
    public static final boolean DEBUG = false;
    private static final Map<String, String> TIME_UNITS;
    private static final Util.Functor1 IDENTITY_FUNCTOR;
    private static final Util.Functor1 TRUE_FUNCTOR;
    private static final Util.Functor1 FALSE_FUNCTOR;

    public static boolean isNull(Object o) {
        return o == null || o == nullValue;
    }

    public static <T> boolean isSorted(List<T> list) {
        T prev = null;

        Object t;
        for(Iterator var2 = list.iterator(); var2.hasNext(); prev = (T)t) {
            t = var2.next();
            if (prev != null && ((Comparable)prev).compareTo(t) >= 0) {
                return false;
            }
        }

        return true;
    }

    public static byte[] digestSha256(String value) {
        MessageDigest algorithm;
        try {
            algorithm = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException var3) {
            throw new RuntimeException(var3);
        }

        return algorithm.digest(value.getBytes());
    }

    public static byte[] digestMd5(String value) {
        MessageDigest algorithm;
        try {
            algorithm = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException var3) {
            throw new RuntimeException(var3);
        }

        return algorithm.digest(value.getBytes());
    }

    public static ExecutorService getExecutorService(int maximumPoolSize, int corePoolSize, long keepAliveTime, final String name, RejectedExecutionHandler rejectionPolicy) {
        ThreadFactory factory = new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger(0);

            public Thread newThread(Runnable r) {
                Thread t = Executors.defaultThreadFactory().newThread(r);
                t.setDaemon(true);
                t.setName(name + '_' + this.counter.incrementAndGet());
                return t;
            }
        };
        ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize > 0 ? maximumPoolSize : Integer.MAX_VALUE, keepAliveTime, TimeUnit.SECONDS, new LinkedBlockingQueue(), factory);
        if (rejectionPolicy != null) {
            executor.setRejectedExecutionHandler(rejectionPolicy);
        }

        return executor;
    }

    public static ScheduledExecutorService getScheduledExecutorService(int maxNbThreads, final String name) {
        return Executors.newScheduledThreadPool(maxNbThreads, new ThreadFactory() {
            final AtomicInteger counter = new AtomicInteger(0);

            public Thread newThread(Runnable r) {
                Thread thread = Executors.defaultThreadFactory().newThread(r);
                thread.setDaemon(true);
                thread.setName(name + '_' + this.counter.incrementAndGet());
                return thread;
            }
        });
    }

    /** @deprecated */
    public static String mdxEncodeString(String st) {
        StringBuilder retString = new StringBuilder(st.length() + 20);

        for(int i = 0; i < st.length(); ++i) {
            char c = st.charAt(i);
            if (c == ']' && i + 1 < st.length() && st.charAt(i + 1) != '.') {
                retString.append(']');
            }

            retString.append(c);
        }

        return retString.toString();
    }

    public static String quoteForMdx(String val) {
        StringBuilder buf = new StringBuilder(val.length() + 20);
        quoteForMdx(buf, val);
        return buf.toString();
    }

    public static StringBuilder quoteForMdx(StringBuilder buf, String val) {
        buf.append("\"");
        String s0 = replace(val, "\"", "\"\"");
        buf.append(s0);
        buf.append("\"");
        return buf;
    }

    public static String quoteMdxIdentifier(String id) {
        StringBuilder buf = new StringBuilder(id.length() + 20);
        quoteMdxIdentifier(id, buf);
        return buf.toString();
    }

    public static void quoteMdxIdentifier(String id, StringBuilder buf) {
        buf.append('[');
        int start = buf.length();
        buf.append(id);
        replace(buf, start, "]", "]]");
        buf.append(']');
    }

    public static String quoteMdxIdentifier(List<Segment> ids) {
        StringBuilder sb = new StringBuilder(64);
        quoteMdxIdentifier(ids, sb);
        return sb.toString();
    }

    public static void quoteMdxIdentifier(List<Segment> ids, StringBuilder sb) {
        for(int i = 0; i < ids.size(); ++i) {
            if (i > 0) {
                sb.append('.');
            }

            ((Segment)ids.get(i)).toString(sb);
        }

    }

    public static String quoteJavaString(String s) {
        return s == null ? "null" : "\"" + s.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\\"", "\\\\\"") + "\"";
    }

    public static boolean equals(Object s, Object t) {
        if (s == t) {
            return true;
        } else {
            return s != null && t != null ? s.equals(t) : false;
        }
    }

    public static boolean equals(String s, String t) {
        return equals((Object)s, (Object)t);
    }

    public static boolean equalName(String s, String t) {
        if (s == null) {
            return t == null;
        } else {
            boolean caseSensitive = MondrianProperties.instance().CaseSensitive.get();
            return caseSensitive ? s.equals(t) : s.equalsIgnoreCase(t);
        }
    }

    public static boolean equal(String s, String t, boolean matchCase) {
        return matchCase ? s.equals(t) : s.equalsIgnoreCase(t);
    }

    public static int caseSensitiveCompareName(String s, String t) {
        boolean caseSensitive = MondrianProperties.instance().CaseSensitive.get();
        if (caseSensitive) {
            return s.compareTo(t);
        } else {
            int v = s.compareToIgnoreCase(t);
            return v == 0 ? s.compareTo(t) : v;
        }
    }

    public static int compareName(String s, String t) {
        boolean caseSensitive = MondrianProperties.instance().CaseSensitive.get();
        return caseSensitive ? s.compareTo(t) : s.compareToIgnoreCase(t);
    }

    public static String normalizeName(String s) {
        return MondrianProperties.instance().CaseSensitive.get() ? s : s.toUpperCase();
    }

    public static int compareKey(Object k1, Object k2) {
        if (k1 instanceof Boolean) {
            k1 = k1.toString();
            k2 = k2.toString();
        }

        return ((Comparable)k1).compareTo(k2);
    }

    public static int compare(int i0, int i1) {
        return i0 < i1 ? -1 : (i0 == i1 ? 0 : 1);
    }

    public static String replace(String s, String find, String replace) {
        int found = s.indexOf(find);
        if (found == -1) {
            return s;
        } else {
            StringBuilder sb = new StringBuilder(s.length() + 20);
            int start = 0;
            char[] chars = s.toCharArray();
            int step = find.length();
            if (step == 0) {
                sb.append(s);
                replace(sb, 0, find, replace);
            } else {
                while(true) {
                    sb.append(chars, start, found - start);
                    if (found == s.length()) {
                        break;
                    }

                    sb.append(replace);
                    start = found + step;
                    found = s.indexOf(find, start);
                    if (found == -1) {
                        found = s.length();
                    }
                }
            }

            return sb.toString();
        }
    }

    public static StringBuilder replace(StringBuilder buf, int start, String find, String replace) {
        int findLength = find.length();
        int k;
        if (findLength == 0) {
            for(k = buf.length(); k >= 0; --k) {
                buf.insert(k, replace);
            }

            return buf;
        } else {
            int i;
            for(k = buf.length(); k > 0; k = i - findLength) {
                i = buf.lastIndexOf(find, k);
                if (i < start) {
                    break;
                }

                buf.replace(i, i + find.length(), replace);
            }

            return buf;
        }
    }

    public static List<Segment> parseIdentifier(String s) {
        return convert(IdentifierParser.parseIdentifier(s));
    }

    public static String implode(List<Segment> names) {
        StringBuilder sb = new StringBuilder(64);
        int i = 0;

        while(i < names.size()) {
            if (i > 0) {
                sb.append(".");
            }

            Segment segment = (Segment)names.get(i);
            switch(((Segment)segment).getQuoting()) {
                case UNQUOTED:
                    segment = new NameSegment(((NameSegment)segment).name);
                default:
                    ((Segment)segment).toString(sb);
                    ++i;
            }
        }

        return sb.toString();
    }

    public static String makeFqName(String name) {
        return quoteMdxIdentifier(name);
    }

    public static String makeFqName(OlapElement parent, String name) {
        if (parent == null) {
            return quoteMdxIdentifier(name);
        } else {
            StringBuilder buf = new StringBuilder(64);
            buf.append(parent.getUniqueName());
            buf.append('.');
            quoteMdxIdentifier(name, buf);
            return buf.toString();
        }
    }

    public static String makeFqName(String parentUniqueName, String name) {
        if (parentUniqueName == null) {
            return quoteMdxIdentifier(name);
        } else {
            StringBuilder buf = new StringBuilder(64);
            buf.append(parentUniqueName);
            buf.append('.');
            quoteMdxIdentifier(name, buf);
            return buf.toString();
        }
    }

    public static OlapElement lookupCompound(SchemaReader schemaReader, OlapElement parent, List<Segment> names, boolean failIfNotFound, int category) {
        return lookupCompound(schemaReader, parent, names, failIfNotFound, category, MatchType.EXACT);
    }

    public static OlapElement lookupCompound(SchemaReader schemaReader, OlapElement parent, List<Segment> names, boolean failIfNotFound, int category, MatchType matchType) {
        assertPrecondition(parent != null, "parent != null");
        if (LOGGER.isDebugEnabled()) {
            StringBuilder buf = new StringBuilder(64);
            buf.append("Util.lookupCompound: ");
            buf.append("parent.name=");
            buf.append(((OlapElement)parent).getName());
            buf.append(", category=");
            buf.append(Category.instance.getName(category));
            buf.append(", names=");
            quoteMdxIdentifier(names, buf);
            LOGGER.debug(buf.toString());
        }
        Member member = null;
        switch(category) {
            case 0:
            case 6:
                member = schemaReader.getCalculatedMember(names);
                if (member != null) {
                    return member;
                }
            default:
                switch(category) {
                    case 0:
                    case 8:
                        NamedSet namedSet = schemaReader.getNamedSet(names);
                        if (namedSet != null) {
                            return namedSet;
                        }
                    default:
                        int i = 0;

                        for(; i < names.size(); ++i) {
                            Object child;
                            NameSegment name;
                            if (names.get(i) instanceof NameSegment) {
                                name = (NameSegment)names.get(i);
                                child = schemaReader.getElementChild((OlapElement)parent, name, matchType);
                            } else if (parent instanceof RolapLevel && names.get(i) instanceof KeySegment && ((Segment)names.get(i)).getKeyParts().size() == 1) {
                                KeySegment keySegment = (KeySegment)names.get(i);
                                name = (NameSegment)keySegment.getKeyParts().get(0);
                                List<Member> levelMembers = schemaReader.getLevelMembers((Level)parent, false);
                                child = null;
                                Iterator var11 = levelMembers.iterator();

                                while(var11.hasNext()) {
                                    member = (Member)var11.next();
                                    if (((RolapMember)member).getKey().toString().equals(name.getName())) {
                                        child = member;
                                        break;
                                    }
                                }
                            } else {
                                name = null;
                                child = schemaReader.getElementChild((OlapElement)parent, name, matchType);
                            }

                            if (child instanceof Member && !matchType.isExact() && !equalName(((OlapElement)child).getName(), name.getName())) {
                                Member bestChild = (Member)child;

                                for(int j = i + 1; j < names.size(); ++j) {
                                    List<Member> childrenList = schemaReader.getMemberChildren(bestChild);
                                    Sorter.hierarchizeMemberList(childrenList, false);
                                    if (matchType == MatchType.AFTER) {
                                        bestChild = (Member)childrenList.get(0);
                                    } else {
                                        bestChild = (Member)childrenList.get(childrenList.size() - 1);
                                    }

                                    if (bestChild == null) {
                                        child = null;
                                        break;
                                    }
                                }

                                parent = bestChild;
                                break;
                            }

                            if (child == null) {
                                if (LOGGER.isDebugEnabled()) {
                                    LOGGER.debug("Util.lookupCompound: parent.name=" + ((OlapElement)parent).getName() + " has no child with name=" + name);
                                }

                                if (!failIfNotFound) {
                                    return null;
                                }

                                if (category == 6) {
                                    throw MondrianResource.instance().MemberNotFound.ex(quoteMdxIdentifier(names));
                                }

                                throw MondrianResource.instance().MdxChildObjectNotFound.ex(name.toString(), ((OlapElement)parent).getQualifiedName());
                            }

                            parent = (OlapElement)child;
                            if (matchType == MatchType.EXACT_SCHEMA) {
                                matchType = MatchType.EXACT;
                            }
                        }

                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Util.lookupCompound: found child.name=" + ((OlapElement)parent).getName() + ", child.class=" + parent.getClass().getName());
                        }

                        switch(category) {
                            case 0:
                                assertPostcondition(parent != null, "return != null");
                                return (OlapElement)parent;
                            case 1:
                            case 5:
                            default:
                                throw newInternal("Bad switch " + category);
                            case 2:
                                if (parent instanceof Dimension) {
                                    return (OlapElement)parent;
                                } else if (parent instanceof Hierarchy) {
                                    return ((OlapElement)parent).getDimension();
                                } else {
                                    if (failIfNotFound) {
                                        throw newError("Can not find dimension '" + implode(names) + "'");
                                    }

                                    return null;
                                }
                            case 3:
                                if (parent instanceof Hierarchy) {
                                    return (OlapElement)parent;
                                } else if (parent instanceof Dimension) {
                                    return ((OlapElement)parent).getHierarchy();
                                } else {
                                    if (failIfNotFound) {
                                        throw newError("Can not find hierarchy '" + implode(names) + "'");
                                    }

                                    return null;
                                }
                            case 4:
                                if (parent instanceof Level) {
                                    return (OlapElement)parent;
                                } else {
                                    if (failIfNotFound) {
                                        throw newError("Can not find level '" + implode(names) + "'");
                                    }

                                    return null;
                                }
                            case 6:
                                if (parent instanceof Member) {
                                    return (OlapElement)parent;
                                } else if (failIfNotFound) {
                                    throw MondrianResource.instance().MdxCantFindMember.ex(implode(names));
                                } else {
                                    return null;
                                }
                        }
                }
        }
    }

    public static OlapElement lookup(Query q, List<Segment> nameParts) {
        Exp exp = lookup(q, nameParts, false);
        if (exp instanceof MemberExpr) {
            MemberExpr memberExpr = (MemberExpr)exp;
            return memberExpr.getMember();
        } else if (exp instanceof LevelExpr) {
            LevelExpr levelExpr = (LevelExpr)exp;
            return levelExpr.getLevel();
        } else if (exp instanceof HierarchyExpr) {
            HierarchyExpr hierarchyExpr = (HierarchyExpr)exp;
            return hierarchyExpr.getHierarchy();
        } else if (exp instanceof DimensionExpr) {
            DimensionExpr dimensionExpr = (DimensionExpr)exp;
            return dimensionExpr.getDimension();
        } else {
            throw newInternal("Not an olap element: " + exp);
        }
    }

    public static Exp lookup(Query q, List<Segment> nameParts, boolean allowProp) {
        return lookup(q, q.getSchemaReader(true), nameParts, allowProp);
    }

    public static Exp lookup(Query q, SchemaReader schemaReader, List<Segment> segments, boolean allowProp) {
        String fullName = quoteMdxIdentifier(segments);
        SchemaReader schemaReaderSansAc = schemaReader.withoutAccessControl().withLocus();
        Cube cube = q.getCube();
        if (allowProp && segments.size() > 1) {
            List<Segment> segmentsButOne = segments.subList(0, segments.size() - 1);
            Segment lastSegment = (Segment)last(segments);
            String propertyName = lastSegment instanceof NameSegment ? ((NameSegment)lastSegment).getName() : null;
            Member member = (Member)schemaReaderSansAc.lookupCompound(cube, segmentsButOne, false, 6);
            if (member != null && propertyName != null && isValidProperty(propertyName, member.getLevel())) {
                return new UnresolvedFunCall(propertyName, Syntax.Property, new Exp[]{createExpr(member)});
            }

            Level level = (Level)schemaReaderSansAc.lookupCompound(cube, segmentsButOne, false, 4);
            if (level != null && propertyName != null && isValidProperty(propertyName, level)) {
                return new UnresolvedFunCall(propertyName, Syntax.Property, new Exp[]{createExpr(level)});
            }
        }

        OlapElement olapElement = schemaReaderSansAc.lookupCompound(cube, segments, false, 0);
        if (olapElement == null) {
            if (!q.ignoreInvalidMembers()) {
                throw MondrianResource.instance().MdxChildObjectNotFound.ex(fullName, cube.getQualifiedName());
            }

            int nameLen = segments.size() - 1;

            for(olapElement = null; nameLen > 0 && olapElement == null; --nameLen) {
                List<Segment> partialName = segments.subList(0, nameLen);
                olapElement = schemaReaderSansAc.lookupCompound(cube, partialName, false, 0);
            }

            if (olapElement == null) {
                throw MondrianResource.instance().MdxChildObjectNotFound.ex(fullName, cube.getQualifiedName());
            }

            olapElement = olapElement.getHierarchy().getNullMember();
        }

        Role role = schemaReader.getRole();
        if (!role.canAccess((OlapElement)olapElement)) {
            throw MondrianResource.instance().MdxChildObjectNotFound.ex(fullName, cube.getQualifiedName());
        } else {
            if (olapElement instanceof Member) {
                olapElement = schemaReader.substitute((Member)olapElement);
            }

            q.addMeasuresMembers((OlapElement)olapElement);
            return createExpr((OlapElement)olapElement);
        }
    }

    static Cube lookupCube(SchemaReader schemaReader, String cubeName, boolean fail) {
        Cube[] var3 = schemaReader.getCubes();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Cube cube = var3[var5];
            if (compareName(cube.getName(), cubeName) == 0) {
                return cube;
            }
        }

        if (fail) {
            throw MondrianResource.instance().MdxCubeNotFound.ex(cubeName);
        } else {
            return null;
        }
    }

    public static Exp createExpr(OlapElement element) {
        if (element instanceof Member) {
            Member member = (Member)element;
            return new MemberExpr(member);
        } else if (element instanceof Level) {
            Level level = (Level)element;
            return new LevelExpr(level);
        } else if (element instanceof Hierarchy) {
            Hierarchy hierarchy = (Hierarchy)element;
            return new HierarchyExpr(hierarchy);
        } else if (element instanceof Dimension) {
            Dimension dimension = (Dimension)element;
            return new DimensionExpr(dimension);
        } else if (element instanceof NamedSet) {
            NamedSet namedSet = (NamedSet)element;
            return new NamedSetExpr(namedSet);
        } else {
            throw newInternal("Unexpected element type: " + element);
        }
    }

    public static Member lookupHierarchyRootMember(SchemaReader reader, Hierarchy hierarchy, NameSegment memberName) {
        return lookupHierarchyRootMember(reader, hierarchy, memberName, MatchType.EXACT);
    }

    public static Member lookupHierarchyRootMember(SchemaReader reader, Hierarchy hierarchy, NameSegment memberName, MatchType matchType) {
        List<Member> rootMembers = reader.getHierarchyRootMembers(hierarchy);
        Member searchMember = null;
        if (!matchType.isExact() && !hierarchy.hasAll() && !rootMembers.isEmpty()) {
            searchMember = hierarchy.createMember((Member)null, ((Member)rootMembers.get(0)).getLevel(), memberName.name, (Formula)null);
        }

        int bestMatch = -1;
        int k = -1;
        Iterator var8 = rootMembers.iterator();

        while(true) {
            Member rootMember;
            int rc;
            do {
                do {
                    while(true) {
                        do {
                            if (!var8.hasNext()) {
                                if (matchType == MatchType.EXACT_SCHEMA) {
                                    return null;
                                }

                                if (matchType != MatchType.EXACT && bestMatch != -1) {
                                    return (Member)rootMembers.get(bestMatch);
                                }

                                return rootMembers.size() > 0 && ((Member)rootMembers.get(0)).isAll() ? reader.lookupMemberChildByName((Member)rootMembers.get(0), memberName, matchType) : null;
                            }

                            rootMember = (Member)var8.next();
                            ++k;
                            if (!matchType.isExact() && !hierarchy.hasAll()) {
                                rc = FunUtil.compareSiblingMembers(rootMember, searchMember);
                            } else {
                                rc = rootMember.getName().compareToIgnoreCase(memberName.name);
                            }

                            if (rc == 0) {
                                return rootMember;
                            }
                        } while(hierarchy.hasAll());

                        if (matchType == MatchType.BEFORE) {
                            break;
                        }

                        if (matchType == MatchType.AFTER && rc > 0 && (bestMatch == -1 || FunUtil.compareSiblingMembers(rootMember, (Member)rootMembers.get(bestMatch)) < 0)) {
                            bestMatch = k;
                        }
                    }
                } while(rc >= 0);
            } while(bestMatch != -1 && FunUtil.compareSiblingMembers(rootMember, (Member)rootMembers.get(bestMatch)) <= 0);

            bestMatch = k;
        }
    }

    public static Level lookupHierarchyLevel(Hierarchy hierarchy, String s) {
        Level[] levels = hierarchy.getLevels();
        Level[] var3 = levels;
        int var4 = levels.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Level level = var3[var5];
            if (level.getName().equalsIgnoreCase(s)) {
                return level;
            }
        }

        return null;
    }

    public static int getMemberOrdinalInParent(SchemaReader reader, Member member) {
        Member parent = member.getParentMember();
        List<Member> siblings = parent == null ? reader.getHierarchyRootMembers(member.getHierarchy()) : reader.getMemberChildren(parent);

        for(int i = 0; i < siblings.size(); ++i) {
            if (((Member)siblings.get(i)).equals(member)) {
                return i;
            }
        }

        throw newInternal("could not find member " + member + " amongst its siblings");
    }

    public static Member getFirstDescendantOnLevel(SchemaReader reader, Member parent, Level level) {
        Member m;
        List children;
        for(m = parent; m.getLevel() != level; m = (Member)children.get(0)) {
            children = reader.getMemberChildren(m);
        }

        return m;
    }

    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public static String singleQuoteString(String val) {
        StringBuilder buf = new StringBuilder(64);
        singleQuoteString(val, buf);
        return buf.toString();
    }

    public static void singleQuoteString(String val, StringBuilder buf) {
        buf.append('\'');
        String s0 = replace(val, "'", "''");
        buf.append(s0);
        buf.append('\'');
    }

    public static Random createRandom(long seed) {
        if (seed == 0L) {
            seed = (new Random()).nextLong();
            System.out.println("random: seed=" + seed);
        } else if (seed == -1L && metaRandom != null) {
            seed = metaRandom.nextLong();
        }

        return new Random(seed);
    }

    public static boolean isValidProperty(String propertyName, Level level) {
        return lookupProperty(level, propertyName) != null;
    }

    public static Property lookupProperty(Level level, String propertyName) {
        do {
            Property[] properties = level.getProperties();
            Property[] var3 = properties;
            int var4 = properties.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Property property = var3[var5];
                if (property.getName().equals(propertyName)) {
                    return property;
                }
            }

            level = level.getParentLevel();
        } while(level != null);

        boolean caseSensitive = MondrianProperties.instance().CaseSensitive.get();
        Property property = Property.lookup(propertyName, caseSensitive);
        if (property != null && property.isMemberProperty() && property.isStandard()) {
            return property;
        } else {
            return null;
        }
    }

    /** @deprecated */
    public static <T> T deprecated(T reason) {
        throw new UnsupportedOperationException(reason.toString());
    }

    /** @deprecated */
    public static <T> T deprecated(T reason, boolean fail) {
        if (fail) {
            throw new UnsupportedOperationException(reason.toString());
        } else {
            return reason;
        }
    }

    public static List<Member> addLevelCalculatedMembers(SchemaReader reader, Level level, List<Member> members) {
        List<Member> calcMembers = reader.getCalculatedMembers(level.getHierarchy());
        List<Member> calcMembersInThisLevel = new ArrayList();
        Iterator var5 = calcMembers.iterator();

        while(var5.hasNext()) {
            Member calcMember = (Member)var5.next();
            if (calcMember.getLevel().equals(level)) {
                calcMembersInThisLevel.add(calcMember);
            }
        }

        if (!calcMembersInThisLevel.isEmpty()) {
            List<Member> newMemberList = new ConcatenableList();
            newMemberList.addAll(members);
            newMemberList.addAll(calcMembersInThisLevel);
            return newMemberList;
        } else {
            return members;
        }
    }

    public static RuntimeException needToImplement(Object o) {
        throw new UnsupportedOperationException("need to implement " + o);
    }

    public static <T extends Enum<T>> RuntimeException badValue(Enum<T> anEnum) {
        return newInternal("Was not expecting value '" + anEnum + "' for enumeration '" + anEnum.getDeclaringClass().getName() + "' in this context");
    }

    public static String wildcardToRegexp(List<String> wildcards) {
        StringBuilder buf = new StringBuilder();
        Iterator var2 = wildcards.iterator();

        label57:
        while(var2.hasNext()) {
            String value = (String)var2.next();
            if (buf.length() > 0) {
                buf.append('|');
            }

            int i = 0;

            while(true) {
                while(true) {
                    int percent = value.indexOf(37, i);
                    int underscore = value.indexOf(95, i);
                    if (percent == -1 && underscore == -1) {
                        if (i < value.length()) {
                            buf.append(quotePattern(value.substring(i)));
                        }
                        continue label57;
                    }

                    if (underscore >= 0 && (underscore < percent || percent < 0)) {
                        if (i < underscore) {
                            buf.append(quotePattern(value.substring(i, underscore)));
                        }

                        buf.append('.');
                        i = underscore + 1;
                    } else {
                        if (percent < 0 || percent >= underscore && underscore >= 0) {
                            throw new IllegalArgumentException();
                        }

                        if (i < percent) {
                            buf.append(quotePattern(value.substring(i, percent)));
                        }

                        buf.append(".*");
                        i = percent + 1;
                    }
                }
            }
        }

        return buf.toString();
    }

    public static String camelToUpper(String s) {
        StringBuilder buf = new StringBuilder(s.length() + 10);
        int prevUpper = -1;

        for(int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > prevUpper + 1) {
                    buf.append('_');
                }

                prevUpper = i;
            } else {
                c = Character.toUpperCase(c);
            }

            buf.append(c);
        }

        return buf.toString();
    }

    public static List<String> parseCommaList(String nameCommaList) {
        if (nameCommaList.equals("")) {
            return Collections.emptyList();
        } else if (nameCommaList.endsWith(",")) {
            String zzz = "zzz";
            List<String> list = parseCommaList(nameCommaList + "zzz");
            String last = (String)list.get(list.size() - 1);
            if (last.equals("zzz")) {
                list.remove(list.size() - 1);
            } else {
                list.set(list.size() - 1, last.substring(0, last.length() - "zzz".length()));
            }

            return list;
        } else {
            List<String> names = new ArrayList();
            String[] strings = nameCommaList.split(",");
            String[] var3 = strings;
            int var4 = strings.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String string = var3[var5];
                int count = names.size();
                if (count > 0 && ((String)names.get(count - 1)).equals("")) {
                    if (count == 1) {
                        if (string.equals("")) {
                            names.add("");
                        } else {
                            names.set(0, "," + string);
                        }
                    } else {
                        names.set(count - 2, (String)names.get(count - 2) + "," + string);
                        names.remove(count - 1);
                    }
                } else {
                    names.add(string);
                }
            }

            return names;
        }
    }

    public static <T> T getAnnotation(Method method, String annotationClassName, T defaultValue) {
        return compatible.getAnnotation(method, annotationClassName, defaultValue);
    }

    public static void cancelStatement(Statement stmt) {
        compatible.cancelStatement(stmt);
    }

    public static Util.MemoryInfo getMemoryInfo() {
        return compatible.getMemoryInfo();
    }

    public static <T> String commaList(String s, List<T> list) {
        StringBuilder buf = new StringBuilder(s);
        buf.append("(");
        int k = -1;

        Object t;
        for(Iterator var4 = list.iterator(); var4.hasNext(); buf.append(t)) {
            t = var4.next();
            ++k;
            if (k > 0) {
                buf.append(", ");
            }
        }

        buf.append(")");
        return buf.toString();
    }

    public static String uniquify(String name, int maxLength, Collection<String> nameList) {
        assert name != null;

        if (name.length() > maxLength) {
            name = name.substring(0, maxLength);
        }

        if (nameList.contains(name)) {
            String aliasBase = name;
            int j = 0;

            label27:
            while(true) {
                while(true) {
                    name = aliasBase + j;
                    if (name.length() > maxLength) {
                        aliasBase = aliasBase.substring(0, aliasBase.length() - 1);
                    } else {
                        if (!nameList.contains(name)) {
                            break label27;
                        }

                        ++j;
                    }
                }
            }
        }

        nameList.add(name);
        return name;
    }

    public static <T> boolean areOccurencesEqual(Collection<T> collection) {
        Iterator<T> it = collection.iterator();
        if (!it.hasNext()) {
            return false;
        } else {
            Object first = it.next();

            Object t;
            do {
                if (!it.hasNext()) {
                    return true;
                }

                t = it.next();
            } while(t.equals(first));

            return false;
        }
    }

    public static <T> List<T> flatList(T... t) {
        return _flatList(t, false);
    }

    public static <T> List<T> flatListCopy(T... t) {
        return _flatList(t, true);
    }

    private static <T> List<T> _flatList(T[] t, boolean copy) {
        switch(t.length) {
            case 0:
                return Collections.emptyList();
            case 1:
                return Collections.singletonList(t[0]);
            case 2:
                return new Util.Flat2List(t[0], t[1]);
            case 3:
                return new Util.Flat3List(t[0], t[1], t[2]);
            default:
                return copy ? (List<T>)Arrays.asList((Object[])t.clone()) : Arrays.asList(t);
        }
    }

    public static <T> List<T> flatList(List<T> t) {
        switch(t.size()) {
            case 0:
                return Collections.emptyList();
            case 1:
                return Collections.singletonList(t.get(0));
            case 2:
                return new Util.Flat2List(t.get(0), t.get(1));
            case 3:
                return new Util.Flat3List(t.get(0), t.get(1), t.get(2));
            default:
                return (List<T>)Arrays.asList(t.toArray());
        }
    }

    public static Locale parseLocale(String localeString) {
        String[] strings = localeString.split("_");
        switch(strings.length) {
            case 1:
                return new Locale(strings[0]);
            case 2:
                return new Locale(strings[0], strings[1]);
            case 3:
                return new Locale(strings[0], strings[1], strings[2]);
            default:
                throw newInternal("bad locale string '" + localeString + "'");
        }
    }

    public static Pair<Long, TimeUnit> parseInterval(String s, TimeUnit unit) throws NumberFormatException {
        Iterator var3 = TIME_UNITS.entrySet().iterator();

        while(var3.hasNext()) {
            Entry<String, String> entry = (Entry)var3.next();
            String abbrev = (String)entry.getKey();
            if (s.endsWith(abbrev)) {
                String full = (String)entry.getValue();

                try {
                    unit = TimeUnit.valueOf(full);
                    s = s.substring(0, s.length() - abbrev.length());
                    break;
                } catch (IllegalArgumentException var9) {
                }
            }
        }

        if (unit == null) {
            throw new NumberFormatException("Invalid time interval '" + s + "'. Does not contain a time unit. (Suffix may be ns (nanoseconds), us (microseconds), ms (milliseconds), s (seconds), h (hours), d (days). For example, '20s' means 20 seconds.)");
        } else {
            try {
                return Pair.of((new BigDecimal(s)).longValue(), unit);
            } catch (NumberFormatException var8) {
                throw new NumberFormatException("Invalid time interval '" + s + "'");
            }
        }
    }

    public static List<Segment> convert(List<IdentifierSegment> olap4jSegmentList) {
        List<Segment> list = new ArrayList();
        Iterator var2 = olap4jSegmentList.iterator();

        while(var2.hasNext()) {
            IdentifierSegment olap4jSegment = (IdentifierSegment)var2.next();
            list.add(convert(olap4jSegment));
        }

        return list;
    }

    public static Segment convert(IdentifierSegment olap4jSegment) {
        return (Segment)(olap4jSegment instanceof org.olap4j.mdx.NameSegment ? convert((org.olap4j.mdx.NameSegment)olap4jSegment) : convert((org.olap4j.mdx.KeySegment)olap4jSegment));
    }

    private static KeySegment convert(final org.olap4j.mdx.KeySegment keySegment) {
        return new KeySegment(new AbstractList<NameSegment>() {
            public NameSegment get(int index) {
                return Util.convert((org.olap4j.mdx.NameSegment)keySegment.getKeyParts().get(index));
            }

            public int size() {
                return keySegment.getKeyParts().size();
            }
        });
    }

    private static NameSegment convert(org.olap4j.mdx.NameSegment nameSegment) {
        return new NameSegment(nameSegment.getName(), convert(nameSegment.getQuoting()));
    }

    private static Quoting convert(org.olap4j.mdx.Quoting quoting) {
        switch(quoting) {
            case QUOTED:
                return Quoting.QUOTED;
            case UNQUOTED:
                return Quoting.UNQUOTED;
            case KEY:
                return Quoting.KEY;
            default:
                throw unexpected(quoting);
        }
    }

    public static <T> Iterable<T> filter(final Iterable<T> iterable, Util.Functor1<Boolean, T>... conds) {
        final Util.Functor1<Boolean, T>[] conds2 = optimizeConditions(conds);
        return conds2.length == 0 ? iterable : new Iterable<T>() {
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    final Iterator<T> iterator = iterable.iterator();
                    T next;
                    boolean hasNext = this.moveToNext();

                    private boolean moveToNext() {
                        label20:
                        while(true) {
                            if (this.iterator.hasNext()) {
                                this.next = this.iterator.next();
                                Util.Functor1[] var1 = conds2;
                                int var2 = var1.length;

                                for(int var3 = 0; var3 < var2; ++var3) {
                                    Util.Functor1<Boolean, T> cond = var1[var3];
                                    if (!(Boolean)cond.apply(this.next)) {
                                        continue label20;
                                    }
                                }

                                return true;
                            }

                            return false;
                        }
                    }

                    public boolean hasNext() {
                        return this.hasNext;
                    }

                    public T next() {
                        T t = this.next;
                        this.hasNext = this.moveToNext();
                        return t;
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    private static <T> Util.Functor1<Boolean, T>[] optimizeConditions(Util.Functor1<Boolean, T>[] conds) {
        List<Util.Functor1<Boolean, T>> functor1List = new ArrayList(Arrays.asList(conds));
        Iterator funcIter = functor1List.iterator();

        while(funcIter.hasNext()) {
            Util.Functor1<Boolean, T> booleanTFunctor1 = (Util.Functor1)funcIter.next();
            if (booleanTFunctor1 == trueFunctor()) {
                funcIter.remove();
            }
        }

        if (functor1List.size() < conds.length) {
            return (Util.Functor1[])functor1List.toArray(new Util.Functor1[functor1List.size()]);
        } else {
            return conds;
        }
    }

    public static <T extends Comparable> List<T> sort(Collection<T> collection) {
        Object[] a = collection.toArray(new Object[collection.size()]);
        Arrays.sort(a);
        return cast(Arrays.asList(a));
    }

    public static <T> List<T> sort(Collection<T> collection, Comparator<T> comparator) {
        T[] a = (T[])collection.toArray(new Object[collection.size()]);
        Arrays.sort(a, comparator);
        return cast(Arrays.asList(a));
    }

    public static List<IdentifierSegment> toOlap4j(final List<Segment> segments) {
        return new AbstractList<IdentifierSegment>() {
            public IdentifierSegment get(int index) {
                return Util.toOlap4j((Segment)segments.get(index));
            }

            public int size() {
                return segments.size();
            }
        };
    }

    public static IdentifierSegment toOlap4j(Segment segment) {
        switch(segment.quoting) {
            case KEY:
                return toOlap4j((KeySegment)segment);
            default:
                return toOlap4j((NameSegment)segment);
        }
    }

    private static org.olap4j.mdx.KeySegment toOlap4j(final KeySegment keySegment) {
        return new org.olap4j.mdx.KeySegment(new AbstractList<org.olap4j.mdx.NameSegment>() {
            public org.olap4j.mdx.NameSegment get(int index) {
                return Util.toOlap4j((NameSegment)keySegment.subSegmentList.get(index));
            }

            public int size() {
                return keySegment.subSegmentList.size();
            }
        });
    }

    private static org.olap4j.mdx.NameSegment toOlap4j(NameSegment nameSegment) {
        return new org.olap4j.mdx.NameSegment((ParseRegion)null, nameSegment.name, toOlap4j(nameSegment.quoting));
    }

    public static org.olap4j.mdx.Quoting toOlap4j(Quoting quoting) {
        return org.olap4j.mdx.Quoting.valueOf(quoting.name());
    }

    public static boolean matches(IdentifierSegment segment, String name) {
        switch(segment.getQuoting()) {
            case QUOTED:
                return equalName(segment.getName(), name);
            case UNQUOTED:
                return segment.getName().equalsIgnoreCase(name);
            case KEY:
                return false;
            default:
                throw unexpected(segment.getQuoting());
        }
    }

    public static boolean matches(Member member, List<Segment> nameParts) {
        if (equalName(implode(nameParts), member.getUniqueName())) {
            return true;
        } else {
            Segment segment;
            for(segment = (Segment)nameParts.get(nameParts.size() - 1); member.getParentMember() != null; segment = (Segment)nameParts.get(nameParts.size() - 1)) {
                if (!segment.matches(member.getName())) {
                    return false;
                }

                member = member.getParentMember();
                nameParts = nameParts.subList(0, nameParts.size() - 1);
            }

            if (segment.matches(member.getName())) {
                return equalName(member.getHierarchy().getUniqueName(), implode(nameParts.subList(0, nameParts.size() - 1)));
            } else if (member.isAll()) {
                return equalName(member.getHierarchy().getUniqueName(), implode(nameParts));
            } else {
                return false;
            }
        }
    }

    public static RuntimeException newElementNotFoundException(int category, IdentifierNode identifierNode) {
        String type;
        switch(category) {
            case 0:
                type = "Element";
                break;
            case 6:
                return MondrianResource.instance().MemberNotFound.ex(identifierNode.toString());
            default:
                type = Category.instance().getDescription(category);
        }

        return newError(type + " '" + identifierNode + "' not found");
    }

    public static <T> T safeGet(Future<T> future, String message) {
        try {
            return future.get();
        } catch (InterruptedException var4) {
            throw newError(var4, message);
        } catch (ExecutionException var5) {
            Throwable cause = var5.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException)cause;
            } else if (cause instanceof Error) {
                throw (Error)cause;
            } else {
                throw newError(cause, message);
            }
        }
    }

    public static <T> Set<T> newIdentityHashSetFake() {
        final HashMap<T, Boolean> map = new HashMap();
        return new Set<T>() {
            public int size() {
                return map.size();
            }

            public boolean isEmpty() {
                return map.isEmpty();
            }

            public boolean contains(Object o) {
                return map.containsKey(o);
            }

            public Iterator<T> iterator() {
                return map.keySet().iterator();
            }

            public Object[] toArray() {
                return map.keySet().toArray();
            }

            public <T> T[] toArray(T[] a) {
                return map.keySet().toArray(a);
            }

            public boolean add(T t) {
                return map.put(t, Boolean.TRUE) == null;
            }

            public boolean remove(Object o) {
                return map.remove(o) == Boolean.TRUE;
            }

            public boolean containsAll(Collection<?> c) {
                return map.keySet().containsAll(c);
            }

            public boolean addAll(Collection<? extends T> c) {
                throw new UnsupportedOperationException();
            }

            public boolean retainAll(Collection<?> c) {
                throw new UnsupportedOperationException();
            }

            public boolean removeAll(Collection<?> c) {
                throw new UnsupportedOperationException();
            }

            public void clear() {
                map.clear();
            }
        };
    }

    public static Timer newTimer(String name, boolean isDaemon) {
        return compatible.newTimer(name, isDaemon);
    }

    public static <T extends Comparable<T>> int binarySearch(T[] ts, int start, int end, T t) {
        return compatible.binarySearch(ts, start, end, t);
    }

    public static <E extends Comparable> SortedSet<E> intersect(SortedSet<E> set1, SortedSet<E> set2) {
        if (set1.isEmpty()) {
            return set1;
        } else if (set2.isEmpty()) {
            return set2;
        } else if (set1 instanceof ArraySortedSet && set2 instanceof ArraySortedSet) {
            Comparable<?>[] result = new Comparable[Math.min(set1.size(), set2.size())];
            Iterator<E> it1 = set1.iterator();
            Iterator<E> it2 = set2.iterator();
            int i = 0;
            E e1 = (E)it1.next();
            Comparable e2 = (Comparable)it2.next();

            while(true) {
                int compare = e1.compareTo(e2);
                if (compare == 0) {
                    result[i++] = e1;
                    if (!it1.hasNext() || !it2.hasNext()) {
                        break;
                    }

                    e1 = (E)it1.next();
                    e2 = (Comparable)it2.next();
                } else if (compare == 1) {
                    if (!it2.hasNext()) {
                        break;
                    }

                    e2 = (Comparable)it2.next();
                } else {
                    if (!it1.hasNext()) {
                        break;
                    }

                    e1 = (E)it1.next();
                }
            }

            return new ArraySortedSet(result, 0, i);
        } else {
            TreeSet<E> set = new TreeSet(set1);
            set.retainAll(set2);
            return set;
        }
    }

    public static int compareIntegers(int i0, int i1) {
        return i0 < i1 ? -1 : (i0 == i1 ? 0 : 1);
    }

    public static <T> T last(List<T> list) {
        return list.get(list.size() - 1);
    }

    public static <T> T only(List<T> list) {
        if (list.size() != 1) {
            throw new IndexOutOfBoundsException("list " + list + " has " + list.size() + " elements, expected 1");
        } else {
            return list.get(0);
        }
    }

    public static SQLException close(ResultSet resultSet, Statement statement, Connection connection) {
        SQLException firstException = null;
        if (resultSet != null) {
            try {
                if (statement == null) {
                    statement = resultSet.getStatement();
                }

                resultSet.close();
            } catch (Throwable var5) {
                firstException = new SQLException();
                firstException.initCause(var5);
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (Throwable var7) {
                if (firstException == null) {
                    firstException = new SQLException();
                    firstException.initCause(var7);
                }
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (Throwable var6) {
                if (firstException == null) {
                    firstException = new SQLException();
                    firstException.initCause(var6);
                }
            }
        }

        return firstException;
    }

    public static BitSet bitSetBetween(int fromIndex, int toIndex) {
        BitSet bitSet = new BitSet();
        if (toIndex > fromIndex) {
            bitSet.set(fromIndex, toIndex);
        }

        return bitSet;
    }

    public static <T> T[] genericArray(Class<T> clazz, int size) {
        return (T[])Array.newInstance(clazz, size);
    }

    public static void assertTrue(boolean b) {
        if (!b) {
            throw newInternal("assert failed");
        }
    }

    public static void assertTrue(boolean b, String message) {
        if (!b) {
            throw newInternal("assert failed: " + message);
        }
    }

    public static RuntimeException newInternal(String message) {
        return MondrianResource.instance().Internal.ex(message);
    }

    public static RuntimeException newInternal(Throwable e, String message) {
        return MondrianResource.instance().Internal.ex(message, e);
    }

    public static RuntimeException newError(String message) {
        return newInternal(message);
    }

    public static RuntimeException newError(Throwable e, String message) {
        return newInternal(e, message);
    }

    public static RuntimeException unexpected(Enum value) {
        return newInternal("Was not expecting value '" + value + "' for enumeration '" + value.getClass().getName() + "' in this context");
    }

    public static void assertPrecondition(boolean b) {
        assertTrue(b);
    }

    public static void assertPrecondition(boolean b, String condition) {
        assertTrue(b, condition);
    }

    public static void assertPostcondition(boolean b) {
        assertTrue(b);
    }

    public static void assertPostcondition(boolean b, String condition) {
        assertTrue(b, condition);
    }

    public static String[] convertStackToString(Throwable e) {
        ArrayList list;
        for(list = new ArrayList(); e != null; e = e.getCause()) {
            String sMsg = getErrorMessage(e);
            list.add(sMsg);
        }

        return (String[])list.toArray(new String[list.size()]);
    }

    public static String getErrorMessage(Throwable err) {
        boolean prependClassName = !(err instanceof SQLException) && err.getClass() != Exception.class;
        return getErrorMessage(err, prependClassName);
    }

    public static String getErrorMessage(Throwable err, boolean prependClassName) {
        String errMsg = err.getMessage();
        if (errMsg != null && !(err instanceof RuntimeException)) {
            return prependClassName ? err.getClass().getName() + ": " + errMsg : errMsg;
        } else {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            err.printStackTrace(pw);
            return sw.toString();
        }
    }

    public static <T extends Throwable> T getMatchingCause(Throwable e, Class<T> clazz) {
        while(!clazz.isInstance(e)) {
            Throwable cause = e.getCause();
            if (cause == null || cause == e) {
                return null;
            }

            e = cause;
        }

        return (T)clazz.cast(e);
    }

    public static String unparse(Exp exp) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exp.unparse(pw);
        return sw.toString();
    }

    public static String unparse(Query query) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new QueryPrintWriter(sw);
        query.unparse(pw);
        return sw.toString();
    }

    public static URL toURL(File file) throws MalformedURLException {
        String path = file.getAbsolutePath();
        String fs = System.getProperty("file.separator");
        if (fs.length() == 1) {
            char sep = fs.charAt(0);
            if (sep != '/') {
                path = path.replace(sep, '/');
            }

            if (path.charAt(0) != '/') {
                path = '/' + path;
            }
        }

        path = "file://" + path;
        return new URL(path);
    }

    public static Util.PropertyList parseConnectString(String s) {
        return (new Util.ConnectStringParser(s)).parse();
    }

    public static int hash(int i, int j) {
        return i << 4 ^ j;
    }

    public static int hash(int h, Object o) {
        int k = o == null ? 0 : o.hashCode();
        return (h << 4 | h) ^ k;
    }

    public static int hashArray(int h, Object[] a) {
        if (a == null) {
            return hash(h, 19690429);
        } else if (a.length == 0) {
            return hash(h, 19690721);
        } else {
            Object[] var2 = a;
            int var3 = a.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Object anA = var2[var4];
                h = hash(h, anA);
            }

            return h;
        }
    }

    public static <T> T[] appendArrays(T[] a0, T[]... as) {
        int n = a0.length;
        T[][] var3 = as;
        int var4 = as.length;

        int var5;
        for(var5 = 0; var5 < var4; ++var5) {
            T[] a = var3[var5];
            n += a.length;
        }

        T[] copy = copyOf(a0, n);
        n = a0.length;
        T[][] var9 = as;
        var5 = as.length;

        for(int var10 = 0; var10 < var5; ++var10) {
            T[] a = var9[var10];
            System.arraycopy(a, 0, copy, n, a.length);
            n += a.length;
        }

        return copy;
    }

    public static <T> T[] append(T[] a, T o) {
        T[] a2 = copyOf(a, a.length + 1);
        a2[a.length] = o;
        return a2;
    }

    public static double[] copyOf(double[] original, int newLength) {
        double[] copy = new double[newLength];
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }

    public static int[] copyOf(int[] original, int newLength) {
        int[] copy = new int[newLength];
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }

    public static long[] copyOf(long[] original, int newLength) {
        long[] copy = new long[newLength];
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }

    public static <T> T[] copyOf(T[] original, int newLength) {
        return (T[])copyOf(original, newLength, original.getClass());
    }

    public static <T, U> T[] copyOf(U[] original, int newLength, Class<? extends Object[]> newType) {
        T[] copy = newType == Object[].class ? (T[])new Object[newLength] : (T[])Array.newInstance(newType.getComponentType(), newLength);
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }

    /** @deprecated */
    public static long dbTimeMillis() {
        return databaseMillis;
    }

    /** @deprecated */
    public static void addDatabaseTime(long millis) {
        databaseMillis += millis;
    }

    /** @deprecated */
    public static long nonDbTimeMillis() {
        long systemMillis = System.currentTimeMillis();
        return systemMillis - databaseMillis;
    }

    public static Validator createSimpleValidator(final FunTable funTable) {
        return new Validator() {
            public Query getQuery() {
                return null;
            }

            public SchemaReader getSchemaReader() {
                throw new UnsupportedOperationException();
            }

            public Exp validate(Exp exp, boolean scalar) {
                return exp;
            }

            public void validate(ParameterExpr parameterExpr) {
            }

            public void validate(MemberProperty memberProperty) {
            }

            public void validate(QueryAxis axis) {
            }

            public void validate(Formula formula) {
            }

            public FunDef getDef(Exp[] args, String name, Syntax syntax) {
                List<Resolver> resolvers = funTable.getResolvers(name, syntax);
                Resolver resolver = (Resolver)resolvers.get(0);
                List<Conversion> conversionList = new ArrayList();
                FunDef def = resolver.resolve(args, this, conversionList);

                assert conversionList.isEmpty();

                return def;
            }

            public boolean alwaysResolveFunDef() {
                return false;
            }

            public boolean canConvert(int ordinal, Exp fromExp, int to, List<Conversion> conversions) {
                return true;
            }

            public boolean requiresExpression() {
                return false;
            }

            public FunTable getFunTable() {
                return funTable;
            }

            public Parameter createOrLookupParam(boolean definition, String name, Type type, Exp defaultExp, String description) {
                return null;
            }
        };
    }

    public static String readFully(Reader rdr, int bufferSize) throws IOException {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("Buffer size must be greater than 0");
        } else {
            char[] buffer = new char[bufferSize];
            StringBuilder buf = new StringBuilder(bufferSize);

            int len;
            while((len = rdr.read(buffer)) != -1) {
                buf.append(buffer, 0, len);
            }

            return buf.toString();
        }
    }

    public static byte[] readFully(InputStream in, int bufferSize) throws IOException {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("Buffer size must be greater than 0");
        } else {
            byte[] buffer = new byte[bufferSize];
            ByteArrayOutputStream baos = new ByteArrayOutputStream(bufferSize);

            int len;
            while((len = in.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }

            return baos.toByteArray();
        }
    }

    public static String readURL(String urlStr, Map<String, String> map) throws IOException {
        if (urlStr.startsWith("inline:")) {
            String content = urlStr.substring("inline:".length());
            if (map != null) {
                content = replaceProperties(content, map);
            }

            return content;
        } else {
            URL url = new URL(urlStr);
            return readURL(url, map);
        }
    }

    public static String readURL(URL url) throws IOException {
        return readURL((URL)url, (Map)null);
    }

    public static String readURL(URL url, Map<String, String> map) throws IOException {
        Reader r = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
        boolean var3 = true;

        String var5;
        try {
            String xmlCatalog = readFully((Reader)r, 8096);
            xmlCatalog = replaceProperties(xmlCatalog, map);
            var5 = xmlCatalog;
        } finally {
            r.close();
        }

        return var5;
    }

    public static InputStream readVirtualFile(String url) throws FileSystemException {
        FileSystemManager fsManager = VFS.getManager();
        if (fsManager == null) {
            throw newError("Cannot get virtual file system manager");
        } else {
            if (url.startsWith("file://localhost")) {
                url = url.substring("file://localhost".length());
            }

            if (url.startsWith("file:")) {
                url = url.substring("file:".length());
            }

            if (url.startsWith("http")) {
                try {
                    return (new URL(url)).openStream();
                } catch (IOException var8) {
                    throw newError("Could not read URL: " + url);
                }
            } else {
                File userDir = (new File("")).getAbsoluteFile();
                FileObject file = fsManager.resolveFile(userDir, url);
                FileContent fileContent = null;

                try {
                    file.refresh();
                    if (file instanceof HttpFileObject && !file.getName().getURI().equals(url)) {
                        fsManager.getFilesCache().removeFile(file.getFileSystem(), file.getName());
                        file = fsManager.resolveFile(userDir, url);
                    }

                    if (!file.isReadable()) {
                        throw newError("Virtual file is not readable: " + url);
                    }

                    fileContent = file.getContent();
                } finally {
                    file.close();
                }

                if (fileContent == null) {
                    throw newError("Cannot get virtual file content: " + url);
                } else {
                    return fileContent.getInputStream();
                }
            }
        }
    }

    public static String readVirtualFileAsString(String catalogUrl) throws IOException {
        InputStream in = readVirtualFile(catalogUrl);

        String var2;
        try {
            var2 = IOUtils.toString(in);
        } finally {
            IOUtils.closeQuietly(in);
        }

        return var2;
    }

    public static Map<String, String> toMap(final Properties properties) {
        return new AbstractMap<String, String>() {
            public Set<Entry<String, String>> entrySet() {
                Map<String,String> newMap = new HashMap<>();
                for (Map.Entry<Object,Object> ele : properties.entrySet()){
                    newMap.put(String.valueOf(ele.getKey()), String.valueOf(ele.getValue()));
                }
                return newMap.entrySet();
            };
        };
    }

    public static String replaceProperties(String text, Map<String, String> env) {
        StringBuffer buf = new StringBuffer(text.length() + 200);
        Pattern pattern = Pattern.compile("\\$\\{([^${}]+)\\}");
        Matcher matcher = pattern.matcher(text);

        while(matcher.find()) {
            String varName = matcher.group(1);
            String varValue = (String)env.get(varName);
            if (varValue != null) {
                matcher.appendReplacement(buf, varValue);
            } else {
                matcher.appendReplacement(buf, "\\${$1}");
            }
        }

        matcher.appendTail(buf);
        return buf.toString();
    }

    public static String printMemory() {
        return printMemory((String)null);
    }

    public static String printMemory(String msg) {
        Runtime rt = Runtime.getRuntime();
        long freeMemory = rt.freeMemory();
        long totalMemory = rt.totalMemory();
        StringBuilder buf = new StringBuilder(64);
        buf.append("FREE_MEMORY:");
        if (msg != null) {
            buf.append(msg);
            buf.append(':');
        }

        buf.append(' ');
        buf.append(freeMemory / 1024L);
        buf.append("kb ");
        long hundredths = freeMemory * 10000L / totalMemory;
        buf.append(hundredths / 100L);
        hundredths %= 100L;
        if (hundredths >= 10L) {
            buf.append('.');
        } else {
            buf.append(".0");
        }

        buf.append(hundredths);
        buf.append('%');
        return buf.toString();
    }

    public static <T> Set<T> cast(Set<?> set) {
        return (Set<T>)set;
    }

    public static <T> List<T> cast(List<?> list) {
        return (List<T>)list;
    }

    public static <T> boolean canCast(Collection<?> collection, Class<T> clazz) {
        Iterator var2 = collection.iterator();

        Object o;
        do {
            if (!var2.hasNext()) {
                return true;
            }

            o = var2.next();
        } while(o == null || clazz.isInstance(o));

        return false;
    }

    public static <T> Iterable<T> castToIterable(final Object iterable) {
        return Retrowoven && !(iterable instanceof Iterable) ? new Iterable<T>() {
            public Iterator<T> iterator() {
                return ((Collection)iterable).iterator();
            }
        } : (Iterable)iterable;
    }

    public static <E extends Enum<E>> E lookup(Class<E> clazz, String name) {
        return (E)lookup(clazz, name, null);
    }

    public static <E extends Enum<E>> E lookup(Class<E> clazz, String name, E defaultValue) {
        if (name == null) {
            return defaultValue;
        } else {
            try {
                return Enum.valueOf(clazz, name);
            } catch (IllegalArgumentException var4) {
                return defaultValue;
            }
        }
    }

    public static BigDecimal makeBigDecimalFromDouble(double d) {
        return compatible.makeBigDecimalFromDouble(d);
    }

    public static String quotePattern(String s) {
        return compatible.quotePattern(s);
    }

    public static String generateUuidString() {
        return compatible.generateUuidString();
    }

    public static <T> T compileScript(Class<T> iface, String script, String engineName) {
        return compatible.compileScript(iface, script, engineName);
    }

    public static <T> void threadLocalRemove(ThreadLocal<T> threadLocal) {
        compatible.threadLocalRemove(threadLocal);
    }

    public static <T> Set<T> newIdentityHashSet() {
        return compatible.newIdentityHashSet();
    }

    public static UserDefinedFunction createUdf(Class<? extends UserDefinedFunction> udfClass, String functionName) {
        String className = udfClass.getName();
        String functionNameOrEmpty = functionName == null ? "" : functionName;
        Object[] args = new Object[0];
        if (Modifier.isPublic(udfClass.getModifiers()) && (udfClass.getEnclosingClass() == null || Modifier.isStatic(udfClass.getModifiers()))) {
            Constructor constructor;
            try {
                constructor = udfClass.getConstructor(String.class);
                if (Modifier.isPublic(constructor.getModifiers())) {
                    args = new Object[]{functionName};
                } else {
                    constructor = null;
                }
            } catch (NoSuchMethodException var13) {
                constructor = null;
            }

            if (constructor == null) {
                try {
                    constructor = udfClass.getConstructor();
                    if (Modifier.isPublic(constructor.getModifiers())) {
                        args = new Object[0];
                    } else {
                        constructor = null;
                    }
                } catch (NoSuchMethodException var12) {
                    constructor = null;
                }
            }

            if (constructor == null) {
                throw MondrianResource.instance().UdfClassWrongIface.ex(functionNameOrEmpty, className, UserDefinedFunction.class.getName());
            } else {
                try {
                    UserDefinedFunction udf = (UserDefinedFunction)constructor.newInstance(args);
                    return udf;
                } catch (InstantiationException var8) {
                    throw MondrianResource.instance().UdfClassWrongIface.ex(functionNameOrEmpty, className, UserDefinedFunction.class.getName());
                } catch (IllegalAccessException var9) {
                    throw MondrianResource.instance().UdfClassWrongIface.ex(functionName, className, UserDefinedFunction.class.getName());
                } catch (ClassCastException var10) {
                    throw MondrianResource.instance().UdfClassWrongIface.ex(functionNameOrEmpty, className, UserDefinedFunction.class.getName());
                } catch (InvocationTargetException var11) {
                    throw MondrianResource.instance().UdfClassWrongIface.ex(functionName, className, UserDefinedFunction.class.getName());
                }
            }
        } else {
            throw MondrianResource.instance().UdfClassMustBePublicAndStatic.ex(functionName, className);
        }
    }

    public static void checkCJResultLimit(long resultSize) {
        int resultLimit = MondrianProperties.instance().ResultLimit.get();
        if (resultLimit > 0 && (long)resultLimit < resultSize) {
            throw MondrianResource.instance().LimitExceededDuringCrossjoin.ex(resultSize, resultLimit);
        } else if (resultSize > 2147483647L) {
            throw MondrianResource.instance().LimitExceededDuringCrossjoin.ex(resultSize, Integer.MAX_VALUE);
        }
    }

    public static String convertOlap4jConnectStringToNativeMondrian(String url) {
        return url.startsWith("jdbc:mondrian:") ? "Provider=Mondrian; " + url.substring("jdbc:mondrian:".length()) : null;
    }

    public static boolean isBlank(String str) {
        int strLen;
        if (str != null && (strLen = str.length()) != 0) {
            for(int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    public static Role createRootRole(Schema schema) {
        RoleImpl role = new RoleImpl();
        role.grant(schema, Access.ALL);
        role.makeImmutable();
        return role;
    }

    public static Cube getDimensionCube(Dimension dimension) {
        Cube[] cubes = dimension.getSchema().getCubes();
        Cube[] var2 = cubes;
        int var3 = cubes.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Cube cube = var2[var4];
            Dimension[] var6 = cube.getDimensions();
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                Dimension dimension1 = var6[var8];
                if (dimension == dimension1) {
                    return cube;
                }

                if ((!(dimension instanceof RolapCubeDimension) || !dimension.equals(dimension1) || ((RolapCubeDimension)dimension1).getCube().equals(cube)) && cube instanceof RolapCube && ((RolapCube)cube).isVirtual() && dimension.equals(dimension1)) {
                    return cube;
                }
            }
        }

        return null;
    }

    public static URL getClosestResource(ClassLoader classLoader, String name) {
        URL resource = null;

        try {
            for(Enumeration resourceCandidates = classLoader.getResources(name); resourceCandidates.hasMoreElements(); resource = (URL)resourceCandidates.nextElement()) {
            }
        } catch (IOException var4) {
            discard(var4);
        }

        return resource;
    }

    public static <T> Util.Functor1<T, T> identityFunctor() {
        return IDENTITY_FUNCTOR;
    }

    public static <PT> Util.Functor1<Boolean, PT> trueFunctor() {
        return TRUE_FUNCTOR;
    }

    public static <PT> Util.Functor1<Boolean, PT> falseFunctor() {
        return FALSE_FUNCTOR;
    }

    public static <K, V> Map<K, V> toNullValuesMap(List<K> list) {
        return new Util.NullValuesMap(list);
    }

    public static void explain(ProfileHandler handler, String title, Calc calc, QueryTiming timing) {
        if (handler != null) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            CalcWriter calcWriter = new CalcWriter(printWriter, true);
            printWriter.println(title);
            if (calc != null) {
                calc.accept(calcWriter);
            }

            printWriter.close();
            handler.explain(stringWriter.toString(), timing);
        }
    }

    public static void addAppender(Appender appender, Logger logger, org.apache.logging.log4j.Level level) {
        LoggerContext ctx = (LoggerContext)LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        appender.start();
        config.addAppender(appender);
        LoggerConfig loggerConfig = config.getLoggerConfig(logger.getName());
        LoggerConfig specificConfig = loggerConfig;
        if (!loggerConfig.getName().equals(logger.getName())) {
            specificConfig = new LoggerConfig(logger.getName(), (org.apache.logging.log4j.Level)null, true);
            specificConfig.setParent(loggerConfig);
            config.addLogger(logger.getName(), specificConfig);
        }

        specificConfig.addAppender(appender, level, (Filter)null);
        ctx.updateLoggers();
    }

    public static void removeAppender(Appender appender, Logger logger) {
        appender.stop();
        LoggerContext ctx = (LoggerContext)LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(logger.getName());
        loggerConfig.removeAppender(appender.getName());
        ctx.updateLoggers();
    }

    public static Appender makeAppender(String name, StringWriter sw, String layout) {
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout().getConversionPattern();
        }

        return ((Builder)((Builder)WriterAppender.newBuilder().setName(name)).setLayout(PatternLayout.newBuilder().withPattern(layout).build())).setTarget(sw).build();
    }

    public static void setLevel(Logger logger, org.apache.logging.log4j.Level level) {
        Configurator.setLevel(logger.getName(), level);
    }

    static {
        metaRandom = createRandom((long)MondrianProperties.instance().TestSeed.get());
        JVM_INSTANCE_UUID = UUID.randomUUID();
        IBM_JVM = System.getProperties().getProperty("java.vendor").equals("IBM Corporation");
        JdbcVersion = System.getProperty("java.version").compareTo("1.7") >= 0 ? 1025 : (System.getProperty("java.version").compareTo("1.6") >= 0 ? 1024 : 768);
        Retrowoven = Access.class.getSuperclass().getName().equals("net.sourceforge.retroweaver.runtime.java.lang.Enum");
        compatible = new UtilCompatibleJdk16();
        TIME_UNITS = Olap4jUtil.mapOf("ns", "NANOSECONDS", new Object[]{"us", "MICROSECONDS", "ms", "MILLISECONDS", "s", "SECONDS", "m", "MINUTES", "h", "HOURS", "d", "DAYS"});
        IDENTITY_FUNCTOR = new Util.Functor1<Object, Object>() {
            public Object apply(Object param) {
                return param;
            }
        };
        TRUE_FUNCTOR = new Util.Functor1<Boolean, Object>() {
            public Boolean apply(Object param) {
                return true;
            }
        };
        FALSE_FUNCTOR = new Util.Functor1<Boolean, Object>() {
            public Boolean apply(Object param) {
                return false;
            }
        };
    }

    private static class NullValuesMap<K, V> extends AbstractMap<K, V> {
        private final List<K> list;

        private NullValuesMap(List<K> list) {
            this.list = Collections.unmodifiableList(list);
        }

        public Set<Entry<K, V>> entrySet() {
            return new AbstractSet<Entry<K, V>>() {
                public Iterator<Entry<K, V>> iterator() {
                    return new Iterator<Entry<K, V>>() {
                        private int pt = 0;

                        public void remove() {
                            throw new UnsupportedOperationException();
                        }

                        public Entry<K, V> next() {
                            return new AbstractMapEntry(NullValuesMap.this.list.get(this.pt++), (Object)null) {
                            };
                        }

                        public boolean hasNext() {
                            return this.pt < NullValuesMap.this.list.size();
                        }
                    };
                }

                public int size() {
                    return NullValuesMap.this.list.size();
                }

                public boolean contains(Object o) {
                    return o instanceof Entry && NullValuesMap.this.list.contains(((Entry)o).getKey());
                }
            };
        }

        public Set<K> keySet() {
            return new AbstractSet<K>() {
                public Iterator<K> iterator() {
                    return new Iterator<K>() {
                        private int pt = -1;

                        public void remove() {
                            throw new UnsupportedOperationException();
                        }

                        public K next() {
                            return NullValuesMap.this.list.get(++this.pt);
                        }

                        public boolean hasNext() {
                            return this.pt < NullValuesMap.this.list.size();
                        }
                    };
                }

                public int size() {
                    return NullValuesMap.this.list.size();
                }

                public boolean contains(Object o) {
                    return NullValuesMap.this.list.contains(o);
                }
            };
        }

        public Collection<V> values() {
            return new AbstractList<V>() {
                public V get(int index) {
                    return null;
                }

                public int size() {
                    return NullValuesMap.this.list.size();
                }

                public boolean contains(Object o) {
                    return o == null && this.size() > 0;
                }
            };
        }

        public V get(Object key) {
            return null;
        }

        public boolean containsKey(Object key) {
            return this.list.contains(key);
        }

        public boolean containsValue(Object o) {
            return o == null && this.size() > 0;
        }

        // $FF: synthetic method
        NullValuesMap(List x0, Object x1) {
            this(x0);
        }
    }

    public static class ByteMatcher {
        private final int[] matcher;
        public final byte[] key;

        public ByteMatcher(byte[] key) {
            this.key = key;
            this.matcher = this.compile(key);
        }

        public int match(byte[] a) {
            int j = 0;

            for(int i = 0; i < a.length; ++i) {
                while(j > 0 && this.key[j] != a[i]) {
                    j = this.matcher[j - 1];
                }

                if (a[i] == this.key[j]) {
                    ++j;
                }

                if (this.key.length == j) {
                    return i - this.key.length + 1;
                }
            }

            return -1;
        }

        private int[] compile(byte[] key) {
            int[] matcher = new int[key.length];
            int j = 0;

            for(int i = 1; i < key.length; ++i) {
                while(j > 0 && key[j] != key[i]) {
                    j = matcher[j - 1];
                }

                if (key[i] == key[j]) {
                    ++j;
                }

                matcher[i] = j;
            }

            return matcher;
        }
    }

    public static class SqlNullSafeComparator implements Comparator<Comparable> {
        public static final Util.SqlNullSafeComparator instance = new Util.SqlNullSafeComparator();

        private SqlNullSafeComparator() {
        }

        public int compare(Comparable o1, Comparable o2) {
            if (o1 == RolapUtil.sqlNullValue) {
                return -1;
            } else {
                return o2 == RolapUtil.sqlNullValue ? 1 : o1.compareTo(o2);
            }
        }
    }

    public interface MemoryInfo {
        Util.MemoryInfo.Usage get();

        public interface Usage {
            long getUsed();

            long getCommitted();

            long getMax();
        }
    }

    public interface Functor1<RT, PT> {
        RT apply(PT var1);
    }

    public static class GcIterator<T> implements Iterator<T> {
        private final Iterator<? extends Reference<T>> iterator;
        private boolean hasNext;
        private T next;

        public GcIterator(Iterator<? extends Reference<T>> iterator) {
            this.iterator = iterator;
            this.hasNext = true;
            this.moveToNext();
        }

        public static <T2> Iterable<T2> over(final Iterable<? extends Reference<T2>> referenceIterable) {
            return new Iterable<T2>() {
                public Iterator<T2> iterator() {
                    return new Util.GcIterator(referenceIterable.iterator());
                }
            };
        }

        private void moveToNext() {
            while(this.iterator.hasNext()) {
                Reference<T> ref = (Reference)this.iterator.next();
                this.next = ref.get();
                if (this.next != null) {
                    return;
                }

                this.iterator.remove();
            }

            this.hasNext = false;
        }

        public boolean hasNext() {
            return this.hasNext;
        }

        public T next() {
            T next1 = this.next;
            this.moveToNext();
            return next1;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    protected static class Flat3List<T> extends Util.AbstractFlatList<T> {
        private final T t0;
        private final T t1;
        private final T t2;

        Flat3List(T t0, T t1, T t2) {
            this.t0 = t0;
            this.t1 = t1;
            this.t2 = t2;

            assert t0 != null;

            assert t1 != null;

            assert t2 != null;

        }

        public String toString() {
            return "[" + this.t0 + ", " + this.t1 + ", " + this.t2 + "]";
        }

        public T get(int index) {
            switch(index) {
                case 0:
                    return this.t0;
                case 1:
                    return this.t1;
                case 2:
                    return this.t2;
                default:
                    throw new IndexOutOfBoundsException("index " + index);
            }
        }

        public int size() {
            return 3;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Util.Flat3List)) {
                return o.equals(this);
            } else {
                Util.Flat3List that = (Util.Flat3List)o;
                return Util.equals(this.t0, that.t0) && Util.equals(this.t1, that.t1) && Util.equals(this.t2, that.t2);
            }
        }

        public int hashCode() {
            int h = 1;
            h = h * 31 + this.t0.hashCode();
            h = h * 31 + this.t1.hashCode();
            h = h * 31 + this.t2.hashCode();
            return h;
        }

        public int indexOf(Object o) {
            if (this.t0.equals(o)) {
                return 0;
            } else if (this.t1.equals(o)) {
                return 1;
            } else {
                return this.t2.equals(o) ? 2 : -1;
            }
        }

        public int lastIndexOf(Object o) {
            if (this.t2.equals(o)) {
                return 2;
            } else if (this.t1.equals(o)) {
                return 1;
            } else {
                return this.t0.equals(o) ? 0 : -1;
            }
        }

        public <T2> T2[] toArray(T2[] a) {
            a[0] = (T2)this.t0;
            a[1] = (T2)this.t1;
            a[2] = (T2)this.t2;
            return a;
        }

        public Object[] toArray() {
            return new Object[]{this.t0, this.t1, this.t2};
        }
    }

    protected static class Flat2List<T> extends Util.AbstractFlatList<T> {
        private final T t0;
        private final T t1;

        Flat2List(T t0, T t1) {
            this.t0 = t0;
            this.t1 = t1;

            assert t0 != null;

            assert t1 != null;

        }

        public String toString() {
            return "[" + this.t0 + ", " + this.t1 + "]";
        }

        public T get(int index) {
            switch(index) {
                case 0:
                    return this.t0;
                case 1:
                    return this.t1;
                default:
                    throw new IndexOutOfBoundsException("index " + index);
            }
        }

        public int size() {
            return 2;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Util.Flat2List)) {
                return Arrays.asList(this.t0, this.t1).equals(o);
            } else {
                Util.Flat2List that = (Util.Flat2List)o;
                return Util.equals(this.t0, that.t0) && Util.equals(this.t1, that.t1);
            }
        }

        public int hashCode() {
            int h = 1;
            h = h * 31 + this.t0.hashCode();
            h = h * 31 + this.t1.hashCode();
            return h;
        }

        public int indexOf(Object o) {
            if (this.t0.equals(o)) {
                return 0;
            } else {
                return this.t1.equals(o) ? 1 : -1;
            }
        }

        public int lastIndexOf(Object o) {
            if (this.t1.equals(o)) {
                return 1;
            } else {
                return this.t0.equals(o) ? 0 : -1;
            }
        }

        public <T2> T2[] toArray(T2[] a) {
            a[0] = (T2)this.t0;
            a[1] = (T2)this.t1;
            return a;
        }

        public Object[] toArray() {
            return new Object[]{this.t0, this.t1};
        }
    }

    public abstract static class AbstractFlatList<T> implements List<T>, RandomAccess {
        protected final List<T> asArrayList() {
            return (List<T>)Arrays.asList(this.toArray());
        }

        public Iterator<T> iterator() {
            return this.asArrayList().iterator();
        }

        public ListIterator<T> listIterator() {
            return this.asArrayList().listIterator();
        }

        public boolean isEmpty() {
            return false;
        }

        public boolean add(Object t) {
            throw new UnsupportedOperationException();
        }

        public boolean addAll(Collection<? extends T> c) {
            throw new UnsupportedOperationException();
        }

        public boolean addAll(int index, Collection<? extends T> c) {
            throw new UnsupportedOperationException();
        }

        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        public void clear() {
            throw new UnsupportedOperationException();
        }

        public T set(int index, Object element) {
            throw new UnsupportedOperationException();
        }

        public void add(int index, Object element) {
            throw new UnsupportedOperationException();
        }

        public T remove(int index) {
            throw new UnsupportedOperationException();
        }

        public ListIterator<T> listIterator(int index) {
            return this.asArrayList().listIterator(index);
        }

        public List<T> subList(int fromIndex, int toIndex) {
            return this.asArrayList().subList(fromIndex, toIndex);
        }

        public boolean contains(Object o) {
            return this.indexOf(o) >= 0;
        }

        public boolean containsAll(Collection<?> c) {
            Iterator e = c.iterator();

            do {
                if (!e.hasNext()) {
                    return true;
                }
            } while(this.contains(e.next()));

            return false;
        }

        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }
    }

    private static class ConnectStringParser {
        private final String s;
        private final int n;
        private int i;
        private final StringBuilder nameBuf;
        private final StringBuilder valueBuf;

        private ConnectStringParser(String s) {
            this.s = s;
            this.i = 0;
            this.n = s.length();
            this.nameBuf = new StringBuilder(64);
            this.valueBuf = new StringBuilder(64);
        }

        Util.PropertyList parse() {
            Util.PropertyList list = new Util.PropertyList();

            while(this.i < this.n) {
                this.parsePair(list);
            }

            return list;
        }

        void parsePair(Util.PropertyList list) {
            String name = this.parseName();
            if (name != null) {
                String value;
                if (this.i >= this.n) {
                    value = "";
                } else if (this.s.charAt(this.i) == ';') {
                    ++this.i;
                    value = "";
                } else {
                    value = this.parseValue();
                }

                list.put(name, value);
            }
        }

        String parseName() {
            this.nameBuf.setLength(0);

            while(true) {
                while(true) {
                    char c = this.s.charAt(this.i);
                    switch(c) {
                        case ' ':
                            if (this.nameBuf.length() == 0) {
                                ++this.i;
                                if (this.i >= this.n) {
                                    return null;
                                }
                                break;
                            }
                        default:
                            this.nameBuf.append(c);
                            ++this.i;
                            if (this.i >= this.n) {
                                return this.nameBuf.toString().trim();
                            }
                            break;
                        case '=':
                            ++this.i;
                            if (this.i >= this.n || (c = this.s.charAt(this.i)) != '=') {
                                String name = this.nameBuf.toString();
                                name = name.trim();
                                return name;
                            }

                            ++this.i;
                            this.nameBuf.append(c);
                    }
                }
            }
        }

        String parseValue() {
            while(true) {
                char c;
                if ((c = this.s.charAt(this.i)) == ' ') {
                    ++this.i;
                    if (this.i < this.n) {
                        continue;
                    }

                    return "";
                }

                String value;
                if (c != '"' && c != '\'') {
                    int semi = this.s.indexOf(59, this.i);
                    if (semi >= 0) {
                        value = this.s.substring(this.i, semi);
                        this.i = semi + 1;
                    } else {
                        value = this.s.substring(this.i);
                        this.i = this.n;
                    }

                    return value.trim();
                }

                for(value = this.parseQuoted(c); this.i < this.n && this.s.charAt(this.i) == ' '; ++this.i) {
                }

                if (this.i >= this.n) {
                    return value;
                }

                if (this.s.charAt(this.i) == ';') {
                    ++this.i;
                    return value;
                }

                throw new RuntimeException("quoted value ended too soon, at position " + this.i + " in '" + this.s + "'");
            }
        }

        String parseQuoted(char q) {
            char c = this.s.charAt(this.i++);
            Util.assertTrue(c == q);
            this.valueBuf.setLength(0);

            while(true) {
                while(this.i < this.n) {
                    c = this.s.charAt(this.i);
                    if (c == q) {
                        ++this.i;
                        if (this.i >= this.n) {
                            return this.valueBuf.toString();
                        }

                        c = this.s.charAt(this.i);
                        if (c != q) {
                            return this.valueBuf.toString();
                        }

                        this.valueBuf.append(c);
                        ++this.i;
                    } else {
                        this.valueBuf.append(c);
                        ++this.i;
                    }
                }

                throw new RuntimeException("Connect string '" + this.s + "' contains unterminated quoted value '" + this.valueBuf.toString() + "'");
            }
        }

        // $FF: synthetic method
        ConnectStringParser(String x0, Object x1) {
            this(x0);
        }
    }

    public static class PropertyList implements Iterable<Pair<String, String>>, Serializable {
        List<Pair<String, String>> list = new ArrayList();

        public PropertyList() {
            this.list = new ArrayList();
        }

        private PropertyList(List<Pair<String, String>> list) {
            this.list = list;
        }

        public Util.PropertyList clone() {
            return new Util.PropertyList(new ArrayList(this.list));
        }

        public String get(String key) {
            return this.get(key, (String)null);
        }

        public String get(String key, String defaultValue) {
            int i = 0;

            for(int n = this.list.size(); i < n; ++i) {
                Pair<String, String> pair = (Pair)this.list.get(i);
                if (((String)pair.left).equalsIgnoreCase(key)) {
                    return (String)pair.right;
                }
            }

            return defaultValue;
        }

        public String put(String key, String value) {
            int i = 0;

            for(int n = this.list.size(); i < n; ++i) {
                Pair<String, String> pair = (Pair)this.list.get(i);
                if (((String)pair.left).equalsIgnoreCase(key)) {
                    String old = (String)pair.right;
                    if (!key.equalsIgnoreCase("Provider")) {
                        pair.right = value;
                    }

                    return old;
                }
            }

            this.list.add(new Pair(key, value));
            return null;
        }

        public boolean remove(String key) {
            boolean found = false;

            for(int i = 0; i < this.list.size(); ++i) {
                Pair<String, String> pair = (Pair)this.list.get(i);
                if (((String)pair.getKey()).equalsIgnoreCase(key)) {
                    this.list.remove(i);
                    found = true;
                    --i;
                }
            }

            return found;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(64);
            int i = 0;

            for(int n = this.list.size(); i < n; ++i) {
                Pair<String, String> pair = (Pair)this.list.get(i);
                if (i > 0) {
                    sb.append("; ");
                }

                sb.append((String)pair.left);
                sb.append('=');
                String right = (String)pair.right;
                if (right == null) {
                    sb.append("'null'");
                } else {
                    int needsQuote = right.indexOf(59);
                    if (needsQuote >= 0) {
                        if (right.charAt(0) != '\'') {
                            sb.append("'");
                        }

                        sb.append(Util.replace(right, "'", "''"));
                        if (right.charAt(right.length() - 1) != '\'') {
                            sb.append("'");
                        }
                    } else {
                        sb.append(right);
                    }
                }
            }

            return sb.toString();
        }

        public Iterator<Pair<String, String>> iterator() {
            return this.list.iterator();
        }
    }

    public static class ErrorCellValue {
        public String toString() {
            return "#ERR";
        }
    }
}