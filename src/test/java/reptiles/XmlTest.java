package reptiles;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @Auther: 大叔
 * @Time: 2019/4/9 11:16
 */
public class XmlTest {

    public static void main(String[] args) throws IOException {

        Document xml = DocumentHelper.createDocument();
        Element urlset = xml.addElement("urlset");
        Element url = urlset.addElement("url");

        Element loc = url.addElement("loc");
        loc.setText("https://baidu.com/");

        Element lastmod = url.addElement("lastmod");
        lastmod.setText("2019-04-09 11:31:58");

        OutputFormat prettyPrint = OutputFormat.createPrettyPrint();
        prettyPrint.setEncoding(StandardCharsets.UTF_8.name());

        FileWriter out = new FileWriter("E:/new.xml");

        //创建一个dom4j创建xml的对象
        XMLWriter writer = new XMLWriter(out, prettyPrint);
        //调用write方法将doc文档写到指定路径
        writer.write(xml);
        writer.close();
        System.out.print("生成XML文件成功");


    }

}
