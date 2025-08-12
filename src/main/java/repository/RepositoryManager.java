package repository;

public class RepositoryManager {
    private static Storage<String, String> storage;

    private RepositoryManager() {}

    public static Storage<String, String> getInstance() {
        if (storage == null) {
            storage = new InMemoryStorage<>();
        }

        return storage;
    }
}
