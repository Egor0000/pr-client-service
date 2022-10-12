package md.utm.isa.pr.clientservice.util;

import md.utm.isa.pr.clientservice.dto.Food;
import md.utm.isa.pr.clientservice.enums.CookingApparatus;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuUtil {
    public static List<Food> getMenu() {
        try {
            List<Food> foods = new ArrayList<>();

            InputStream xmlFile = (MenuUtil.class.getResourceAsStream("/foods.xml"));

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = factory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("food");

            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) nNode;

                    NodeList childList = elem.getChildNodes();

                    Map<String, String> keyValue = new HashMap<>();

                    for (int j = 0; j < childList.getLength(); j++) {
                        Node childNode = childList.item(j);
                        if (childNode != null && childNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element childElem = (Element) childNode;
                            String key = childElem.getNodeName();

                            String value = childElem.getTextContent();

                            if (value != null) {
                                keyValue.put(key, value);
                            }
                        }
                    }

                    Food food = createFood(keyValue);
                    foods.add(food);
                }
            }

            return foods;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static Food createFood(Map<String, String> keyValue) {
        Food food = new Food();

        for (Map.Entry<String, String> entry : keyValue.entrySet()) {
            switch (entry.getKey()) {
                case "id":
                    food.setId(Long.parseLong(entry.getValue()));
                    break;
                case "name":
                    food.setName(entry.getValue());
                    break;
                case "preparation-time":
                    food.setPreparationTime(Long.parseLong(entry.getValue()));
                    break;
                case "complexity":
                    food.setComplexity(Long.parseLong(entry.getValue()));
                    break;
                case "cooking-aparatus":
                    food.setCookingApparatus(CookingApparatus.getByName(entry.getValue()));
                    break;
            }
        }

        return food;
    }
}
