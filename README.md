This Burp Suite extension was developed to solve a problem I encountered during penetration testing. I needed to perform automated scans on a single endpoint using two different sets of cookies, which required frequently updating session cookies.

Burp Suite only supports a single cookie jar, and while it’s possible to work around this using session handling rules with match/replace configurations, the process is not user-friendly. It lacks a visual interface and requires constant manual updates with custom regex rules.

This extension allows you to easily update session headers—such as cookies—without writing any rules. It also supports swapping multiple headers at once by updating a single profile value, which is especially helpful in session-based tests like IDOR, particularly within Repeater or Intruder.

It simplifies session management and improves testing efficiency by removing the need for complex configurations.


How to Use:

1. Select your Profile from HeaderJar Tab:
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

For any advice or inquiry, feel free to reach out at: gokay.shn@gmail.com
