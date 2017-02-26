package comp2601.carleton.edu.comp2601a2client;

/**
 * Created by maximkuzmenko on 2017-02-25.
 */

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import android.graphics.Bitmap;

public class MemoryCache {
    private Map&lt;String, SoftReference&lt;Bitmap&gt;&gt; cache=Collections.synchronizedMap(new HashMap&lt;String, SoftReference&lt;Bitmap&gt;&gt;());

    public Bitmap get(String id){
        if(!cache.containsKey(id))
            return null;
        SoftReference&lt;Bitmap&gt; ref=cache.get(id);
        return ref.get();
    }

    public void put(String id, Bitmap bitmap){
        cache.put(id, new SoftReference&lt;Bitmap&gt;(bitmap));
    }

    public void clear() {
        cache.clear();
    }
}
