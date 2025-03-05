package src.summer.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

public class HtmlUtil {

    /**
     * Convertit un document DOM en chaîne HTML.
     *
     * @param document Le document DOM à convertir.
     * @return Une chaîne HTML bien formée.
     */
    public static String documentToString(Document document) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "html");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));

        return writer.toString();
    }

    /**
     * Crée un nouveau document XML avec une balise racine spécifiée.
     *
     * @param rootTag Le nom de la balise racine du document (par exemple : "root", "data").
     * @return Un objet {@link Document} contenant la balise racine spécifiée.
     * @throws ParserConfigurationException Si une erreur survient lors de la création du document XML.
     */
    public static Document createDocument(String rootTag) throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element root = doc.createElement(rootTag);
        doc.appendChild(root);

        return doc;
    }

    /**
     * Ajoute une classe CSS à un élément HTML.
     *
     * @param element   L'élément HTML cible.
     * @param className La classe CSS à ajouter.
     */
    public static void addCssClass(Element element, String className) {
        String existingClasses = element.getAttribute("class");

        if (existingClasses.isEmpty()) {
            element.setAttribute("class", className);
        } else if (!existingClasses.contains(className)) {
            element.setAttribute("class", existingClasses + " " + className);
        }
    }

    /**
     * Ajoute un commentaire à un élément HTML existant.
     *
     * @param document Le document DOM auquel appartient l'élément.
     * @param element  L'élément HTML cible auquel le commentaire sera ajouté.
     * @param comment  Le texte du commentaire à insérer.
     */
    public static void addComment(
            Document document,
            Element element,
            String comment
    ) {
        element.appendChild(document.createComment(comment));
    }

}
