import java.net.URL;
import java.net.MalformedURLException;
//import symantec.itools.lang.Context;
import java.io.*;


//  05/15/97    CAR Added ieAnchorHack to work around an IE problem with anchor symbols in URLs.

/**
 * This class is used in conjunction with class symantec.itools.OS.Context to
 * provide URLs that are relative to an applet’s or application’s
 * document base. For applets the document base is the URL of the document
 * that the applet is embedded in. For applications the document base is the
 * same as the “user.dir” system property.
 */
public class RelativeURL
{
    /**
     * Don’t use, this is an all-static class.
     */
    public RelativeURL() {
    }

    /**
     * Determines the absolute URL given a relative URL.
     * If the spec parameter is relative, it is considered to be relative
     * to the current document base as determined by getDocumentBase() in
     * class symantec.itools.lang.Context.
     *
     * @param spec a possibly relative URL
     * @return the absolute URL equivalent to the given relativeURL
     * @exception MalformedURLException
     * if cannot generate the resultant URL due to a bad spec parameter
     * or a bad document base
     */
    public static URL getURL(String spec)
        throws MalformedURLException
    {

        // InternetExplorer for some reason strips out a single anchor symbol (#)
        // when the URL is passed to showDocument() so we double up the symbol (##)
 
        URL documentBase = Context.getDocumentBase();

        if(documentBase != null && spec.indexOf("//") == -1)
        {
            return new URL(documentBase,spec);
        }

        return new URL(spec);
    }

}