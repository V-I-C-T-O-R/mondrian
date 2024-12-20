/* Decompiler 10ms, total 319ms, lines 26 */
package mondrian.rolap;

import mondrian.olap.Util;
import mondrian.olap.Util.PropertyList;
import mondrian.util.ByteString;
import mondrian.util.StringKey;

public class SchemaContentKey extends StringKey {
    private SchemaContentKey(String s) {
        super(s);
    }

    static SchemaContentKey create(PropertyList connectInfo, String catalogUrl, String catalogContents) {
        String catalogContentProp = RolapConnectionProperties.CatalogContent.name();
        String dynamicSchemaProp = RolapConnectionProperties.DynamicSchemaProcessor.name();
        StringBuilder buf = new StringBuilder();
        if (Util.isEmpty(connectInfo.get(catalogContentProp)) && Util.isEmpty(connectInfo.get(dynamicSchemaProp))) {
            ConnectionKey.attributeValue(buf, "catalog", catalogUrl);
        } else {
            ConnectionKey.attributeValue(buf, "catalogStr", catalogContents);
        }

        return new SchemaContentKey((new ByteString(Util.digestMd5(buf.toString()))).toString());
    }
}