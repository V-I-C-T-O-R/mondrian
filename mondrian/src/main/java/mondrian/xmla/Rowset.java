/* Decompiler 542ms, total 1416ms, lines 448 */
package mondrian.xmla;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mondrian.olap.Util;
import mondrian.olap.Util.Functor1;
import mondrian.xmla.RowsetDefinition.Column;
import mondrian.xmla.RowsetDefinition.Type;
import mondrian.xmla.XmlaUtil.Wildcard;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.olap4j.OlapConnection;
import org.olap4j.metadata.Catalog;

abstract class Rowset implements XmlaConstants {
    protected static final Logger LOGGER = LogManager.getLogger(Rowset.class);
    protected final RowsetDefinition rowsetDefinition;
    protected final Map<String, Object> restrictions;
    protected final Map<String, String> properties;
    protected final Map<String, String> extraProperties = new HashMap();
    protected final XmlaRequest request;
    protected final XmlaHandler handler;
    protected final boolean deep;

    Rowset(RowsetDefinition definition, XmlaRequest request, XmlaHandler handler) {
        this.rowsetDefinition = definition;
        this.restrictions = request.getRestrictions();
        this.properties = request.getProperties();
        this.request = request;
        this.handler = handler;
        ArrayList<Column> list = new ArrayList();

        Column column;
        for(Iterator var5 = this.restrictions.entrySet().iterator(); var5.hasNext(); list.add(column)) {
            Entry<String, Object> restrictionEntry = (Entry)var5.next();
            String restrictedColumn = (String)restrictionEntry.getKey();
            LOGGER.debug("Rowset<init>: restrictedColumn=\"" + restrictedColumn + "\"");
            column = definition.lookupColumn(restrictedColumn);
            if (column == null) {
                throw Util.newError("Rowset '" + definition.name() + "' does not contain column '" + restrictedColumn + "'");
            }

            if (!column.restriction) {
                throw Util.newError("Rowset '" + definition.name() + "' column '" + restrictedColumn + "' does not allow restrictions");
            }

            Object restriction = restrictionEntry.getValue();
            if (restriction instanceof List && ((List)restriction).size() > 1) {
                Type type = column.type;
                switch(type) {
                    case StringArray:
                    case EnumerationArray:
                    case StringSometimesArray:
                        break;
                    default:
                        throw Util.newError("Rowset '" + definition.name() + "' column '" + restrictedColumn + "' can only be restricted on one value at a time");
                }
            }
        }

        this.pruneRestrictions(list);
        boolean deep = false;
        Iterator var13 = this.properties.entrySet().iterator();

        while(var13.hasNext()) {
            Entry<String, String> propertyEntry = (Entry)var13.next();
            String propertyName = (String)propertyEntry.getKey();
            PropertyDefinition propertyDef = (PropertyDefinition)Util.lookup(PropertyDefinition.class, propertyName);
            if (propertyDef == null) {
                throw Util.newError("Rowset '" + definition.name() + "' does not support property '" + propertyName + "'");
            }

            String propertyValue = (String)propertyEntry.getValue();
            this.setProperty(propertyDef, propertyValue);
            if (propertyDef == PropertyDefinition.Deep) {
                deep = Boolean.valueOf(propertyValue);
            }
        }

        this.deep = deep;
    }

    protected ArrayList<Column> pruneRestrictions(ArrayList<Column> list) {
        return list;
    }

    protected void setProperty(PropertyDefinition propertyDef, String value) {
        switch(propertyDef) {
            case LocaleIdentifier:
                Locale locale = XmlaUtil.convertToLocale(value);
                if (locale != null) {
                    this.extraProperties.put("locale", locale.toString());
                }

                return;
            default:
                LOGGER.warn("Warning: Rowset '" + this.rowsetDefinition.name() + "' does not support property '" + propertyDef.name() + "' (value is '" + value + "')");
            case Format:
            case DataSourceInfo:
            case Catalog:
        }
    }

    public final void unparse(XmlaResponse response) throws XmlaException, SQLException {
        List<Rowset.Row> rows = new ArrayList();
        this.populate((XmlaResponse)response, (OlapConnection)null, (List)rows);
        Comparator<Rowset.Row> comparator = this.rowsetDefinition.getComparator();
        if (comparator != null) {
            Collections.sort(rows, comparator);
        }

        SaxWriter writer = response.getWriter();
        writer.startSequence((String)null, "row");
        Iterator var5 = rows.iterator();

        while(var5.hasNext()) {
            Rowset.Row row = (Rowset.Row)var5.next();
            this.emit(row, response);
        }

        writer.endSequence();
    }

    public final void populate(XmlaResponse response, OlapConnection connection, List<Rowset.Row> rows) throws XmlaException {
        boolean ourConnection = false;

        try {
            if (this.needConnection() && connection == null) {
                connection = this.handler.getConnection(this.request, this.extraProperties);
                ourConnection = true;
            }

            this.populateImpl(response, connection, rows);
        } catch (SQLException var13) {
            throw new XmlaException("00UE001", "Internal Error", "SqlException:", var13);
        } finally {
            if (connection != null && ourConnection) {
                try {
                    connection.close();
                } catch (SQLException var12) {
                }
            }

        }

    }

    protected boolean needConnection() {
        return true;
    }

    protected abstract void populateImpl(XmlaResponse var1, OlapConnection var2, List<Rowset.Row> var3) throws XmlaException, SQLException;

    protected final boolean addRow(Rowset.Row row, List<Rowset.Row> rows) throws XmlaException {
        return rows.add(row);
    }

    protected void emit(Rowset.Row row, XmlaResponse response) throws XmlaException, SQLException {
        SaxWriter writer = response.getWriter();
        writer.startElement("row");
        Column[] var4 = this.rowsetDefinition.columnDefinitions;
        int var5 = var4.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            Column column = var4[var6];
            Object value = row.get(column.name);
            if (value == null) {
                if (!column.nullable) {
                    throw new XmlaException("Client", "00HSBB16", "XMLA SOAP non-nullable column", Util.newInternal("Value required for column " + column.name + " of rowset " + this.rowsetDefinition.name()));
                }
            } else {
                int var21;
                int var23;
                if (value instanceof Rowset.XmlElement[]) {
                    Rowset.XmlElement[] elements = (Rowset.XmlElement[])value;
                    Rowset.XmlElement[] var19 = elements;
                    var21 = elements.length;

                    for(var23 = 0; var23 < var21; ++var23) {
                        Rowset.XmlElement element = var19[var23];
                        this.emitXmlElement(writer, element);
                    }
                } else if (value instanceof Object[]) {
                    Object[] values = (Object[])value;
                    Object[] var18 = values;
                    var21 = values.length;

                    for(var23 = 0; var23 < var21; ++var23) {
                        Object value1 = var18[var23];
                        writer.startElement(column.name);
                        writer.characters(value1.toString());
                        writer.endElement();
                    }
                } else if (value instanceof List) {
                    List values = (List)value;
                    Iterator var17 = values.iterator();

                    while(var17.hasNext()) {
                        Object value1 = var17.next();
                        if (value1 instanceof Rowset.XmlElement) {
                            Rowset.XmlElement xmlElement = (Rowset.XmlElement)value1;
                            this.emitXmlElement(writer, xmlElement);
                        } else {
                            writer.startElement(column.name);
                            writer.characters(value1.toString());
                            writer.endElement();
                        }
                    }
                } else if (!(value instanceof Rowset)) {
                    writer.textElement(column.name, value);
                } else {
                    Rowset rowset = (Rowset)value;
                    List<Rowset.Row> rows = new ArrayList();
                    rowset.populate((XmlaResponse)response, (OlapConnection)null, (List)rows);
                    writer.startSequence(column.name, "row");
                    Iterator var11 = rows.iterator();

                    while(var11.hasNext()) {
                        Rowset.Row row1 = (Rowset.Row)var11.next();
                        rowset.emit(row1, response);
                    }

                    writer.endSequence();
                }
            }
        }

        writer.endElement();
    }

    private void emitXmlElement(SaxWriter writer, Rowset.XmlElement element) {
        if (element.attributes == null) {
            writer.startElement(element.tag);
        } else {
            writer.startElement(element.tag, element.attributes);
        }

        if (element.text == null) {
            Rowset.XmlElement[] var3 = element.children;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Rowset.XmlElement aChildren = var3[var5];
                this.emitXmlElement(writer, aChildren);
            }
        } else {
            writer.characters(element.text);
        }

        writer.endElement();
    }

    protected <E> void populate(Class<E> clazz, List<Rowset.Row> rows, Comparator<E> comparator) throws XmlaException {
        E[] enumsSortedByName = (E[])clazz.getEnumConstants().clone();
        Arrays.sort(enumsSortedByName, comparator);
        Object[] var5 = enumsSortedByName;
        int var6 = enumsSortedByName.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            E anEnum = (E)var5[var7];
            Rowset.Row row = new Rowset.Row();
            Column[] var10 = this.rowsetDefinition.columnDefinitions;
            int var11 = var10.length;

            for(int var12 = 0; var12 < var11; ++var12) {
                Column column = var10[var12];
                row.names.add(column.name);
                row.values.add(column.get(anEnum));
            }

            rows.add(row);
        }

    }

    <E> Functor1<Boolean, E> makeCondition(Column column) {
        return this.makeCondition(Util.identityFunctor(), column);
    }

    <E, V> Functor1<Boolean, E> makeCondition(final Functor1<V, ? super E> getter, Column column) {
        Object restriction = this.restrictions.get(column.name);
        if (restriction == null) {
            return Util.trueFunctor();
        } else if (restriction instanceof Wildcard) {
            Wildcard wildcard = (Wildcard)restriction;
            String regexp = Util.wildcardToRegexp(Collections.singletonList(wildcard.pattern));
            final Matcher matcher = Pattern.compile(regexp).matcher("");
            return new Functor1<Boolean, E>() {
                public Boolean apply(E element) {
                    V value = getter.apply(element);
                    return matcher.reset(String.valueOf(value)).matches();
                }
            };
        } else if (restriction instanceof List) {
            final List<V> requiredValues = (List)restriction;
            return new Functor1<Boolean, E>() {
                public Boolean apply(E element) {
                    if (element == null) {
                        return requiredValues.contains("");
                    } else {
                        V value = getter.apply(element);
                        return requiredValues.contains(value);
                    }
                }
            };
        } else {
            throw Util.newInternal("unexpected restriction type: " + restriction.getClass());
        }
    }

    String getRestrictionValueAsString(Column column) {
        Object restriction = this.restrictions.get(column.name);
        if (restriction instanceof List) {
            List<String> rval = (List)restriction;
            if (rval.size() == 1) {
                return (String)rval.get(0);
            }
        }

        return null;
    }

    int getRestrictionValueAsInt(Column column) {
        Object restriction = this.restrictions.get(column.name);
        if (restriction instanceof List) {
            List<String> rval = (List)restriction;
            if (rval.size() == 1) {
                try {
                    return Integer.parseInt((String)rval.get(0));
                } catch (NumberFormatException var5) {
                    LOGGER.info("Rowset.getRestrictionValue: bad integer restriction \"" + rval + "\"");
                    return -1;
                }
            }
        }

        return -1;
    }

    protected boolean isRestricted(Column column) {
        return this.restrictions.get(column.name) != null;
    }

    protected Functor1<Boolean, Catalog> catNameCond() {
        Map<String, String> properties = this.request.getProperties();
        final String catalogName = (String)properties.get(PropertyDefinition.Catalog.name());
        return catalogName != null ? new Functor1<Boolean, Catalog>() {
            public Boolean apply(Catalog catalog) {
                return catalog.getName().equals(catalogName);
            }
        } : Util.trueFunctor();
    }

    public List<String> getRestriction(Column column) {
        Object restriction = this.restrictions.get(column.name);
        if (restriction == null) {
            return null;
        } else {
            ArrayList restrictionList;
            if (restriction instanceof List) {
                restrictionList = new ArrayList();
                Iterator var4 = ((List)restriction).iterator();

                while(var4.hasNext()) {
                    Object o = var4.next();
                    restrictionList.add(o.toString());
                }
            } else {
                restrictionList = new ArrayList();
                restrictionList.add(restriction.toString());
            }

            return restrictionList;
        }
    }

    protected static class XmlElement {
        private final String tag;
        private final Object[] attributes;
        private final String text;
        private final Rowset.XmlElement[] children;

        XmlElement(String tag, Object[] attributes, String text) {
            this(tag, attributes, text, (Rowset.XmlElement[])null);
        }

        XmlElement(String tag, Object[] attributes, Rowset.XmlElement[] children) {
            this(tag, attributes, (String)null, children);
        }

        private XmlElement(String tag, Object[] attributes, String text, Rowset.XmlElement[] children) {
            assert attributes == null || attributes.length % 2 == 0;

            this.tag = tag;
            this.attributes = attributes;
            this.text = text;
            this.children = children;
        }
    }

    protected static class Row {
        private final ArrayList<String> names = new ArrayList();
        private final ArrayList<Object> values = new ArrayList();

        Row() {
        }

        void set(String name, Object value) {
            this.names.add(name);
            this.values.add(value);
        }

        public Object get(String name) {
            int i = this.names.indexOf(name);
            return i < 0 ? null : this.values.get(i);
        }
    }
}