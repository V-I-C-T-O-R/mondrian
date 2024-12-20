/* Decompiler 118ms, total 1187ms, lines 87 */
package mondrian.olap4j;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import mondrian.olap.Access;
import mondrian.olap.OlapElement;
import mondrian.olap.Schema;
import mondrian.rolap.RolapSchema;
import mondrian.rolap.SchemaContentKey;
import org.olap4j.OlapDatabaseMetaData;
import org.olap4j.OlapException;
import org.olap4j.impl.Named;
import org.olap4j.impl.NamedListImpl;
import org.olap4j.impl.Olap4jUtil;
import org.olap4j.metadata.Catalog;
import org.olap4j.metadata.Database;
import org.olap4j.metadata.NamedList;

class MondrianOlap4jCatalog extends MondrianOlap4jMetadataElement implements Catalog, Named {
    final MondrianOlap4jDatabaseMetaData olap4jDatabaseMetaData;
    final String name;
    final Map<String, RolapSchema> schemaMap;
    final MondrianOlap4jDatabase olap4jDatabase;

    MondrianOlap4jCatalog(MondrianOlap4jDatabaseMetaData olap4jDatabaseMetaData, String name, MondrianOlap4jDatabase database, Map<String, RolapSchema> schemaMap) {
        assert database != null;

        this.olap4jDatabaseMetaData = olap4jDatabaseMetaData;
        this.name = name;
        this.olap4jDatabase = database;
        this.schemaMap = schemaMap;
        Iterator var5 = schemaMap.entrySet().iterator();

        while(var5.hasNext()) {
            Entry<String, RolapSchema> entry = (Entry)var5.next();
            String schemaName = (String)entry.getKey();
            Schema schema = (Schema)entry.getValue();
            if (schemaName == null) {
                schemaName = schema.getName();
            }

            MondrianOlap4jSchema olap4jSchema = new MondrianOlap4jSchema(this, schemaName, schema);
            olap4jDatabaseMetaData.olap4jConnection.schemaMap.put((SchemaContentKey)((RolapSchema)schema).getKey().getKey(), olap4jSchema);
        }

    }

    public NamedList<org.olap4j.metadata.Schema> getSchemas() throws OlapException {
        NamedList<MondrianOlap4jSchema> list = new NamedListImpl();
        Iterator var2 = this.schemaMap.entrySet().iterator();

        while(var2.hasNext()) {
            Entry<String, RolapSchema> entry = (Entry)var2.next();
            String schemaName = (String)entry.getKey();
            Schema schema = (Schema)entry.getValue();
            MondrianOlap4jConnection oConn = (MondrianOlap4jConnection)this.olap4jDatabase.getOlapConnection();
            if (oConn.getMondrianConnection().getRole().getAccess(schema) != Access.NONE) {
                if (schemaName == null) {
                    schemaName = schema.getName();
                }

                MondrianOlap4jSchema olap4jSchema = new MondrianOlap4jSchema(this, schemaName, schema);
                list.add(olap4jSchema);
            }
        }

        return Olap4jUtil.cast(list);
    }

    public String getName() {
        return this.name;
    }

    public OlapDatabaseMetaData getMetaData() {
        return this.olap4jDatabaseMetaData;
    }

    public Database getDatabase() {
        return this.olap4jDatabase;
    }

    protected OlapElement getOlapElement() {
        return null;
    }
}