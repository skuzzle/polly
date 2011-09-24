package core;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class XMLEntity {
    
    
    protected String getTextValue(Element ele, String tagName) {
        String textVal = null;
        if (ele == null) {
            return "";
        }
        NodeList nl = ele.getElementsByTagName(tagName);
        if(nl != null && nl.getLength() > 0) {
            Element el = (Element) nl.item(0);
            if (el.getFirstChild() == null) {
                return null;
            }
            textVal = el.getFirstChild().getNodeValue();
        }

        return textVal;
    }
    
    
}
