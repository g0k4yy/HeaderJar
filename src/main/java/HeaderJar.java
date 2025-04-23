import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.ui.UserInterface;

import java.util.Arrays;

public class HeaderJar implements BurpExtension{

    private UserInterface userInterface;

    @Override
    public void initialize(MontoyaApi api) {

        SingletonMAPI.initialize(api);

        userInterface = api.userInterface();
        api.extension().setName("HeaderJar");
        api.logging().logToOutput("EXTENSION Installed.");

        String headerForUI = """ 
                How to Use:

                1. Select your Profile:
                   - Choose a profile (0-9) from the dropdown list.

                2. Enter Custom Header:
                   - In the input field, enter the header you'd like to use for the selected profile.
                   - Example: Cookie: newCookie123
                   - You can enter any text, whether it's a standard HTTP header, non-standard header, or even gibberish.

                3. Header Reformatting:
                   - The extension **does not validate headers**. It accepts all inputs as-is.
                   - However, it **automatically reformats** headers to ensure a space exists after the colon (`:`).
                   - Example:
                     - If you enter `Cookie:abc123`, it will be converted to `Cookie: abc123`.
                     - If you enter `A:B`, it will be converted to `A: B`.

                4. Add the X-HeaderJar Header to Your Request:
                   - In your HTTP request, add the following header:
                     X-HeaderJar: {profileNumber}
                   - Replace `{profileNumber}` with the profile you selected (e.g., `X-HeaderJar: 0` for the first profile).

                5. How It Works:
                   - When the request is sent, the HeaderJar extension will:
                     - Look for the `X-HeaderJar` header in the request.
                     - Check which profile is selected (from 0 to 9).
                     - Add or override the headers for the selected profile (e.g., `Cookie` header with your custom value).
                     - Remove the `X-HeaderJar` header before sending the request.

                6. Example Usage:
                   - If you selected Profile 0 and entered `Cookie: newCookie123`:
                     - Add `X-HeaderJar: 0` to your request.
                     - The `Cookie` header in the request will be replaced with `Cookie: newCookie123`.

                7. Important Notes:
                   - The `X-HeaderJar` header will be removed automatically after processing.
                   - Only the headers defined for the selected profile will be added or overridden.
                   - The reformatting process ensures consistency but does not validate header content or syntax.
                   - Scanner payloads will be overwritten because of the replacing. Due to that you can not audit an insertion point that updated by HeaderJar.

                For any advice or inquiry, feel free to reach out at: example.dev.mail at symbol gmail.com
                """;
 // first string to show for UI if it can't find any
        
        int profileCount = 10;

        String [] rawString = new String[profileCount]; // Array for rawStrings

        // Check Preferences for Existing Strings

        for (int i = 0; i < profileCount; i++) {
            if (api.persistence().extensionData().stringKeys().contains("HeaderJar-" + i)){
                rawString[i] = api.persistence().extensionData().getString("HeaderJar-" + i);
                if (i == 0) {
                	headerForUI = """ 
                            How to Use:

                            1. Select your Profile:
                               - Choose a profile (0-9) from the dropdown list.

                            2. Enter Custom Header:
                               - In the input field, enter the header you'd like to use for the selected profile.
                               - Example: Cookie: newCookie123
                               - You can enter any text, whether it's a standard HTTP header, non-standard header, or even gibberish.

                            3. Header Reformatting:
                               - The extension **does not validate headers**. It accepts all inputs as-is.
                               - However, it **automatically reformats** headers to ensure a space exists after the colon (`:`).
                               - Example:
                                 - If you enter `Cookie:abc123`, it will be converted to `Cookie: abc123`.
                                 - If you enter `A:B`, it will be converted to `A: B`.

                            4. Add the X-HeaderJar Header to Your Request:
                               - In your HTTP request, add the following header:
                                 X-HeaderJar: {profileNumber}
                               - Replace `{profileNumber}` with the profile you selected (e.g., `X-HeaderJar: 0` for the first profile).

                            5. How It Works:
                               - When the request is sent, the HeaderJar extension will:
                                 - Look for the `X-HeaderJar` header in the request.
                                 - Check which profile is selected (from 0 to 9).
                                 - Add or override the headers for the selected profile (e.g., `Cookie` header with your custom value).
                                 - Remove the `X-HeaderJar` header before sending the request.

                            6. Example Usage:
                               - If you selected Profile 0 and entered `Cookie: newCookie123`:
                                 - Add `X-HeaderJar: 0` to your request.
                                 - The `Cookie` header in the request will be replaced with `Cookie: newCookie123`.

                            7. Important Notes:
                               - The `X-HeaderJar` header will be removed automatically after processing.
                               - Only the headers defined for the selected profile will be added or overridden.
                               - The reformatting process ensures consistency but does not validate header content or syntax.
                               - Scanner payloads will be overwritten because of the replacing. Due to that you can not audit an insertion point that updated by HeaderJar.
                               - For any advice or inquiry, feel free to reach out at: gokay.shn@gmail.com
                            """;

                }
            }
        }


        // Creation of UI/ Creating TAB / Assigning first header to show for UI
        HeaderJarUserInterface ui = new HeaderJarUserInterface(api);
        HeaderJarHttpHandler handler = new HeaderJarHttpHandler(ui, rawString,api);
        ui.setHTTPHandler(handler);
        api.userInterface().registerSuiteTab("HeaderJar", ui.getUI());
        ui.setHeaderForUi(headerForUI);


        //HANDLER AND API CONNECTIONS

        api.http().registerHttpHandler(handler);
        api.extension().registerUnloadingHandler(new UnloadingHandler(handler));

    }

}
