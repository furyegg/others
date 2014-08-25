package com.lombardrisk.xbrl.render.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.lombardrisk.xbrl.checker.csv.CsvUtil;
import com.lombardrisk.xbrl.render.csv.OpenCodeEntry;
import com.lombardrisk.xbrl.render.csv.TableCodeOpenCodeEntry;
import com.lombardrisk.xbrl.render.csv.TableCodeOpenCodeEntryDescriptor;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.Map;

/**
 * Created by Cesar on 12/06/2014.
 */
public final class OpenCodeUtils {

    private static final boolean HEADER = true;

    private OpenCodeUtils() {
    }

    public static void marshallOpenCodeEntries(Multimap<String, OpenCodeEntry> multiMap, Writer writer) throws IOException {
        final List<TableCodeOpenCodeEntry> toExport = Lists.newArrayList();
        for (Map.Entry<String, OpenCodeEntry> entry : multiMap.entries()) {
            toExport.add(new TableCodeOpenCodeEntry(entry.getKey(), entry.getValue()));
        }
        CsvUtil.write(writer, new TableCodeOpenCodeEntryDescriptor(), toExport, HEADER);
    }

    public static Multimap<String, OpenCodeEntry> unMarshallOpenCodeEntries(Reader reader) throws IOException {
        Multimap<String, OpenCodeEntry> multiMap = HashMultimap.create();
        for (TableCodeOpenCodeEntry t : CsvUtil.parse(reader, new TableCodeOpenCodeEntryDescriptor(), HEADER)) {
            multiMap.put(t.getTableCode(), t.getOpenCodeEntry());
        }
        return multiMap;
    }
}
