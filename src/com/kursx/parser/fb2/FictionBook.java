package com.kursx.parser.fb2;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FictionBook {

    protected Xmlns[] xmlns;
    protected Description description;
    protected List<Body> bodies = new ArrayList<>();
    protected Map<String, Binary> binaries;

    public String encoding = "utf-8";

    public FictionBook() {}

    public FictionBook(File file) throws ParserConfigurationException, IOException, SAXException, OutOfMemoryError {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputStream inputStream = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new FileReader(file));
        boolean foundIllegalCharacters = false;
        try {
            String line = br.readLine().trim();
            if (!line.startsWith("<")) {
                foundIllegalCharacters = true;
            }
            while (!line.endsWith("?>")) {
                line += "\n" + br.readLine().trim();
            }
            int start = line.indexOf("encoding") + 8;
            String substring = line.substring(start);
            substring = substring.substring(substring.indexOf("\"") + 1);
            encoding = substring.substring(0, substring.indexOf("\"")).toLowerCase();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Document doc;
        if (foundIllegalCharacters) {
            StringBuilder text = new StringBuilder();
            br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            if (line != null && line.contains("<")) {
                line = line.substring(line.indexOf("<"));
            }
            while (line != null) {
                text.append(line);
                line = br.readLine();
            }
            br.close();
            doc = db.parse(new InputSource(new StringReader(text.toString())));
        } else {
            doc = db.parse(new InputSource(new InputStreamReader(inputStream, encoding)));
        }
        initXmlns(doc);
        description = new Description(doc);
        NodeList bodyNodes = doc.getElementsByTagName("body");
        for (int item = 0; item < bodyNodes.getLength(); item++) {
            bodies.add(new Body(bodyNodes.item(item)));
        }
        NodeList binary = doc.getElementsByTagName("binary");
        for (int item = 0; item < binary.getLength(); item++) {
            if (binaries == null) binaries = new HashMap<>();
            Binary binary1 = new Binary(binary.item(item));
            binaries.put(binary1.getId().replace("#", ""), binary1);
        }
    }

    // Новый конструктор для работы с данными в памяти
    public FictionBook(byte[] xmlData) throws ParserConfigurationException, IOException, SAXException {
        String encoding = detectEncodingFromBytes(xmlData);
        InputSource is = new InputSource(new ByteArrayInputStream(xmlData));
        is.setEncoding(encoding);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(is);

        initXmlns(doc);
        description = new Description(doc);
        NodeList bodyNodes = doc.getElementsByTagName("body");
        for (int item = 0; item < bodyNodes.getLength(); item++) {
            bodies.add(new Body(bodyNodes.item(item)));
        }
        NodeList binary = doc.getElementsByTagName("binary");
        for (int item = 0; item < binary.getLength(); item++) {
            if (binaries == null) binaries = new HashMap<>();
            Binary binary1 = new Binary(binary.item(item));
            binaries.put(binary1.getId().replace("#", ""), binary1);
        }
    }

    // Новый конструктор для работы со строкой XML
    public FictionBook(String xmlContent) throws ParserConfigurationException, IOException, SAXException {
        InputSource is = new InputSource(new StringReader(xmlContent));

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(is);

        initXmlns(doc);
        description = new Description(doc);
        NodeList bodyNodes = doc.getElementsByTagName("body");
        for (int item = 0; item < bodyNodes.getLength(); item++) {
            bodies.add(new Body(bodyNodes.item(item)));
        }
        NodeList binary = doc.getElementsByTagName("binary");
        for (int item = 0; item < binary.getLength(); item++) {
            if (binaries == null) binaries = new HashMap<>();
            Binary binary1 = new Binary(binary.item(item));
            binaries.put(binary1.getId().replace("#", ""), binary1);
        }
    }

    protected void setXmlns(ArrayList<Node> nodeList) {
        xmlns = new Xmlns[nodeList.size()];
        for (int index = 0; index < nodeList.size(); index++) {
            Node node = nodeList.get(index);
            xmlns[index] = new Xmlns(node);
        }
    }

    protected void initXmlns(Document doc) {
        NodeList fictionBook = doc.getElementsByTagName("FictionBook");
        ArrayList<Node> xmlns = new ArrayList<>();
        for (int item = 0; item < fictionBook.getLength(); item++) {
            NamedNodeMap map = fictionBook.item(item).getAttributes();
            for (int index = 0; index < map.getLength(); index++) {
                Node node = map.item(index);
                xmlns.add(node);
            }
        }
        setXmlns(xmlns);
    }

    // Метод для определения кодировки из байтового массива
    private String detectEncodingFromBytes(byte[] data) {
        // Анализируем первые 1024 байта для определения кодировки
        String header = new String(data, 0, Math.min(data.length, 1024), StandardCharsets.UTF_8);
        int encodingStart = header.indexOf("encoding=\"");
        if (encodingStart != -1) {
            encodingStart += 10; // Длина "encoding=""
            int encodingEnd = header.indexOf('"', encodingStart);
            if (encodingEnd != -1) {
                return header.substring(encodingStart, encodingEnd);
            }
        }
        // По умолчанию используем UTF-8
        return "UTF-8";
    }

    public ArrayList<Person> getAuthors() {
        return description.getDocumentInfo().getAuthors();
    }

    public Xmlns[] getXmlns() {
        return xmlns;
    }

    public Description getDescription() {
        return description;
    }

    public  Body getBody() {
        return getBody(null);
    }

    public  Body getNotes() {
        return getBody("notes");
    }

    public  Body getComments() {
        return getBody("comments");
    }

    private  Body getBody(String name) {
        for (Body body : bodies) {
            if ((name + "").equals(body.getName() + "")) {
                return body;
            }
        }
        return bodies.get(0);
    }

    public Map<String, Binary> getBinaries() {
        return binaries == null ? new HashMap<String, Binary>() : binaries;
    }

    public String getTitle() {
        return description.getTitleInfo().getBookTitle();
    }

    public String getLang() {
        return description.getTitleInfo().getLang();
    }

    public  Annotation getAnnotation() {
        return description.getTitleInfo().getAnnotation();
    }

    // Новый метод для доступа к bodies
    public List<Body> getBodies() {
        return Collections.unmodifiableList(bodies);
    }
}
