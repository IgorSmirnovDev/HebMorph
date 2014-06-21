package org.apache.lucene.analysis.hebrew;

import com.code972.hebmorph.MorphData;
import com.code972.hebmorph.datastructures.DictRadix;
import com.code972.hebmorph.hspell.Loader;
import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.junit.AfterClass;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class TestBase {
    private static DictRadix<MorphData> dict;

    protected synchronized DictRadix<MorphData> getDictionary() throws IOException {
        String hspellPath = null;
        if (dict == null) {
            ClassLoader classLoader = TermPositionVectorTest.class.getClassLoader();
            File folder = new File(classLoader.getResource("").getPath());
            while (true) {
                File tmp = new File(folder, "hspell-data-files");
                if (tmp.exists() && tmp.isDirectory()) {
                    hspellPath = tmp.toString();
                    break;
                }

                folder = folder.getParentFile();
                if (folder == null) break;
            }

            if (hspellPath == null)
                throw new IllegalArgumentException("path to hspell data folder couldn't be found");

            dict = new Loader(new File(hspellPath), true).loadDictionaryFromHSpellData();
        }
        return dict;
    }

    protected static File[] getTestFiles() throws IOException {
        List<String> lookedAt = new ArrayList<>();
        for (String s : new String[] { ".", "..", "../.." }){
            File f = new File(s + "/test-files");
            if (f.exists()) return f.listFiles();
            lookedAt.add(f.getCanonicalPath());
        }
        throw new IOException("Cannot find test data, looked at " + lookedAt);
    }

    @AfterClass
    public static void cleanupDictionary() {
        if (dict != null) {
            dict.clear();
            dict = null;
        }
    }
}
