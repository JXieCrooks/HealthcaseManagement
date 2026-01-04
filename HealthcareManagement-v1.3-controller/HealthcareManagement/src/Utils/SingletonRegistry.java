package Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton Registry
 */
public class SingletonRegistry {

    private static SingletonRegistry instance;
    private Map<String, Object> registry;

    private SingletonRegistry() {
        registry = new HashMap<>();
    }

    // Get instance
    public static synchronized SingletonRegistry getInstance() {
        if (instance == null) {
            instance = new SingletonRegistry();
        }
        return instance;
    }

    // Register Instance
    public void register(String key, Object obj) {
        registry.put(key, obj);
    }

    // get Instance
    public Object get(String key) {
        return registry.get(key);
    }
}
