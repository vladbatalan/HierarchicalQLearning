package timebender;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.time.LocalTime;

public class Logger {
    private static boolean isActive = true;
    private static Game game;

    public static void Print(String message){
        if(isActive) {
            LocalTime currentTime = LocalTime.now();
            String toDisplay = ("[%s]: " + message).formatted(currentTime);
            System.out.println(toDisplay);
        }
    }

    public static void Error(String message){
        if(isActive) {
            LocalTime currentTime = LocalTime.now();
            String toDisplay = ("[%s]: [ERROR]: " + message).formatted(currentTime);

            System.out.println();
            System.out.println(toDisplay);
            System.out.println();
        }
    }

    public static void PrintXML(String xmlString){
        if(isActive) {
            try {
                InputSource src = new InputSource(new StringReader(xmlString));
                Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src);

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                transformerFactory.setAttribute("indent-number", 1);
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");

                Writer out = new StringWriter();
                transformer.transform(new DOMSource(document), new StreamResult(out));
                System.out.println(out.toString());

            } catch (Exception e) {
                throw new RuntimeException("Error occurs when pretty-printing xml:\n" + xmlString, e);
            }
        }
    }

    public static void PrintEndline() {
        if(isActive) {
            System.out.println();
        }
    }

    public static void SetIsActive(boolean isActive){
        Logger.isActive = isActive;
    }

    public static void SetGame(Game game){
        Logger.game = game;
    }
}
