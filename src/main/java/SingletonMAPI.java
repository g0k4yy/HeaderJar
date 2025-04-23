import burp.api.montoya.MontoyaApi;

public final class SingletonMAPI {

    private static MontoyaApi INSTANCE;

    private SingletonMAPI() {}

    public static void initialize(MontoyaApi api) {
        if (INSTANCE ==  null) {
            INSTANCE = api;
        }
    }

    public static MontoyaApi getAPI() {
        return INSTANCE;
    }
}
