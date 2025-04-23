import burp.api.montoya.extension.ExtensionUnloadingHandler;

public class UnloadingHandler implements ExtensionUnloadingHandler {

    private HeaderJarHttpHandler handler;

    public UnloadingHandler(HeaderJarHttpHandler handler) {
        this.handler = handler;
    }

    public void extensionUnloaded() {
        // Loop through profiles to save in preferences (assuming there are 10 profiles)
        int profileCount = 10;

        for (int i = 0; i < profileCount; i++) {
            SingletonMAPI.getAPI().persistence().extensionData().setString("HeaderJar-" + i, this.handler.getRawString()[i]);
        }

    }
}
