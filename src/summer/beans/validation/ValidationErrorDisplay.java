package src.summer.beans.validation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import src.summer.utils.HtmlUtil;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.util.List;

public class ValidationErrorDisplay {
    private final List<String> errors;

    public ValidationErrorDisplay(List<String> errors) {
        this.errors = errors;
    }

    /**
     * Convertit la liste des erreurs en code HTML sous forme de chaîne.
     *
     * @return Une chaîne HTML représentant la liste des erreurs.
     * @throws ParserConfigurationException Si une erreur survient lors de la création du document HTML.
     */
    public String toHtml() throws ParserConfigurationException, TransformerException {
        Document htmlDocument = HtmlUtil.createDocument("div");
        Element rootDiv = htmlDocument.getDocumentElement();

        // Ajout des classes CSS
        HtmlUtil.addCssClass(rootDiv, "alert alert-danger p-2");

        // Création de la liste des erreurs (ul)
        Element ul = htmlDocument.createElement("ul");
        HtmlUtil.addCssClass(ul, "mb-0");

        // Ajout des erreurs sous forme de <li> via stream
        errors.stream()
                .map(error -> createListItem(htmlDocument, error))
                .forEach(ul::appendChild);

        rootDiv.appendChild(ul);

        HtmlUtil.addComment(htmlDocument, rootDiv, "Validation Error");

        return HtmlUtil.documentToString(htmlDocument);
    }

    /**
     * Crée un élément <li> avec le message d'erreur.
     *
     * @param document Document HTML DOM
     * @param error    Message d'erreur à afficher
     * @return Élément <li> contenant le message d'erreur
     */
    private Element createListItem(Document document, String error) {
        Element li = document.createElement("li");
        li.setTextContent(error);
        return li;
    }
}
