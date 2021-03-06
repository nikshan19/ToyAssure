package com.increff.assure.util;

import com.increff.commons.Data.ChannelInvoiceData;
import com.increff.commons.Data.InvoiceData;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;

import static com.increff.commons.Constants.ConstantNames.*;

public class ChannelXmlUtil {


    public static String generatePdf(ChannelInvoiceData invoiceData) throws Exception {
        generateXml(new File(CHANNEL_INVOICE_XML_PATH),
                invoiceData, ChannelInvoiceData.class);
        return generatePDF(invoiceData.getOrderId(), new File(CHANNEL_INVOICE_XML_PATH),
                new StreamSource(CHANNEL_INVOICE_XSL_PATH));

    }


    //Generate PDF
    public static String generatePDF(Long orderId, File xml_file, StreamSource xsl_source) throws Exception {

        String path = PDF_BASE_ADDRESS+orderId+".pdf";
        File pdfFile = new File(path);
        pdfFile.createNewFile();
        FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
        // Setup a buffer to obtain the content length
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputStream os = new FileOutputStream(pdfFile);
        // Setup FOP
        Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(xsl_source);
        // Make sure the XSL transformation's result is piped through to FOP
        Result res = new SAXResult(fop.getDefaultHandler());

        // Setup input
        Source src = new StreamSource(xml_file);

        // Start the transformation and rendering process
        transformer.transform(src, res);

        byte[] bytes =  out.toByteArray();
        os.write(bytes);
        os.close();
        out.close();
        out.flush();

//        byte[] encodedBytes = Base64.getEncoder().encode(bytes);
//        HashMap<String, byte[]> hm = new HashMap<>();
//        hm.put(path, encodedBytes);
        return path;

    }

    //Generate XML
    public static void generateXml(File file,Object list,Class<?> class_type) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(class_type);
        Marshaller m = context.createMarshaller();
        // for pretty-print XML in JAXB
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(list, file);

    }

    public static String getDateTime(){
        LocalDateTime myDateObj = LocalDateTime.now();
        System.out.println("Before formatting: " + myDateObj);
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String formattedDate = myDateObj.format(myFormatObj);
        return formattedDate;
    }


}
