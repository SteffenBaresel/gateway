/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.siv.ksc.reporting;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Section;
import com.itextpdf.text.TabSettings;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import de.siv.ksc.modules.Base64Coder;
import de.siv.ksc.modules.Basics;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 *
 * @author sbaresel
 */
public class ReportingFunctions {
    
    static Properties props = null;
    private static Font headFont = new Font(Font.FontFamily.HELVETICA, 36, Font.NORMAL);
    private static Font subHeadFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
    private static Font notificationFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
    private static Font infoFont = new Font(Font.FontFamily.HELVETICA, 16, Font.NORMAL);
    private static Font infoHeadFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static Font tableHeadFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
    private static Font tableContFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
    
    /**
     * Creates a PDF document.
     * @param filename the path to the new PDF document
     * @throws    DocumentExcepticaton 
     * @throws    IOException 
     */
    public static void createPdf(HttpServletResponse response, String Cuid, String From, String To)
	throws DocumentException, IOException, NamingException, SQLException, BadElementException, FileNotFoundException, ParseException {
        Document document = new Document(PageSize.A4, 36, 36, 54, 54);
        PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
        
        HeaderFooter footer = new HeaderFooter();
        writer.setBoxSize("art", new Rectangle(36, 54, 559, 788));
        writer.setPageEvent(footer);
        
        document.open();
        addMetaData(document, Cuid);
        addTitlePage(document, Cuid, Basics.InterpreteDate(Base64Coder.decodeString(From)), Basics.InterpreteDate(Base64Coder.decodeString(To)));
        addContent(document, Cuid, Basics.InterpreteDate(Base64Coder.decodeString(From)), Basics.InterpreteDate(Base64Coder.decodeString(To)));
        addContact(writer, document, Cuid);
        document.close();
    }
    
    /* From Report */
    
    public static void createPdfFile(String Cuid, String From, String To, String File)
	throws DocumentException, IOException, NamingException, SQLException, BadElementException, FileNotFoundException, ParseException {
        Document document = new Document(PageSize.A4, 36, 36, 54, 54);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        
        HeaderFooter footer = new HeaderFooter();
        writer.setBoxSize("art", new Rectangle(36, 54, 559, 788));
        writer.setPageEvent(footer);
        
        document.open();
        addMetaData(document, Cuid);
        addTitlePage(document, Cuid, From, To);
        addContent(document, Cuid, From, To);
        addContact(writer, document, Cuid);
        document.close();
        
        FileOutputStream fos = new FileOutputStream(File);
        fos.write(baos.toByteArray());
        fos.close();
    }
    
    /*
     * Formatting
     */
    
    private static void addMetaData(Document document, String Cuid) throws NamingException, SQLException {
        /*
         * Gather Informations
         */
        
        // Get Customer Name
        String Cunm = "";
        Context ctx1 = new InitialContext(); 
        DataSource ds1  = (DataSource) ctx1.lookup("jdbc/repository"); 
        Connection cn1 = ds1.getConnection(); 
        
        String sqlGC1 = "select decode(cunm,'base64') from managed_service_cinfo where cuid=?";
        PreparedStatement ps1 = cn1.prepareStatement(sqlGC1);
        ps1.setInt(1,Integer.parseInt( Cuid ));
        ResultSet rs1 = ps1.executeQuery();
        
        while (rs1.next()) { 
            Cunm = Basics.encodePdf(rs1.getString(1));
        }
        
        cn1.close();
        
        document.addTitle("Servicereport: " + Cunm);
        document.addSubject("Übersicht der durchgeführten Wartungsarbeiten im Zusammenhang mit dem abgeschlossenen Wartungsvertrag.");
        document.addKeywords("Servicereport, SIV.AG");
        document.addAuthor("Steffen Baresel");
        document.addCreator("kVASy(R) System Control");
    }

    private static void addTitlePage(Document document, String Cuid, String From, String To)
        throws DocumentException, NamingException, SQLException {
        
        /*
         * Gather Informations
         */
        
        // Get Customer Name And Address
        String Cuaddr = "";
        String Cunr = "";
        String Cunm = "";
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        
        String sqlGC = "select decode(cunm,'base64'),cunr,decode(cuaddr,'base64') from managed_service_cinfo where cuid=?";
        PreparedStatement ps = cn.prepareStatement(sqlGC);
        ps.setInt(1,Integer.parseInt( Cuid ));
        ResultSet rs = ps.executeQuery();
        
        while (rs.next()) { 
            Cunm = Basics.encodePdf(rs.getString(1));
            Cunr = Basics.encodePdf(rs.getString(2));
            Cuaddr = Basics.encodePdf(rs.getString(3));
        }
        
        cn.close();
        
        /*
         * Build PDF
         */
        
        /* Head Image */
        
        /*
         * Check if Windows
         */
        
        String RESOURCE;
    
        File f = new File("C:/Windows");
        if (f.isDirectory()) {
            RESOURCE = "C:/app/oracle/Middleware/images/Deckblatt.png";
        } else {
            RESOURCE = "/u01/app/oracle/Middleware/images/Deckblatt.png";
        }
        
        Image img;
        try {
            img = Image.getInstance(RESOURCE);
            img.scaleToFit(525,150);
            img.setAbsolutePosition(50,770);
            document.add(img);
        } catch (BadElementException ex) {
            Logger.getLogger(ReportingFunctions.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ReportingFunctions.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReportingFunctions.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Central Output
        Paragraph preface = new Paragraph();
        // We add one empty line
        addEmptyLine(preface, 8);
        // Lets write a big header
        
        // Headline
        Paragraph headline = new Paragraph("Servicereport", headFont);
        headline.setAlignment(Element.ALIGN_CENTER);
        
        // Add Headline to Document
        preface.add(headline);
        
        // Sub Headline
        
        Paragraph subheadline = new Paragraph("Vom " + From + " bis " + To, subHeadFont);
        subheadline.setSpacingBefore(3);
        subheadline.setAlignment(Element.ALIGN_CENTER);
        
        // Add Sub Headline to Document
        preface.add(subheadline);
        addEmptyLine(preface, 5);

        // Sub Headline
        Paragraph description = new Paragraph("Erstellt von der SIV.AG für die", subHeadFont);
        description.setAlignment(Element.ALIGN_CENTER);
        
        // Add Sub Headline to Document
        preface.add(description);
        addEmptyLine(preface, 1);
        
        // Sub Customer Name
        Paragraph cname = new Paragraph(Cunm, infoFont);
        cname.setAlignment(Element.ALIGN_CENTER);
        
        // Add Sub Headline to Document
        preface.add(cname);
        addEmptyLine(preface, 1);
        
        // Sub Customer Name
        Paragraph cuaddr = new Paragraph(Cuaddr, infoFont);
        cuaddr.setAlignment(Element.ALIGN_CENTER);
        
        // Add Sub Headline to Document
        preface.add(cuaddr);        
        
        addEmptyLine(preface, 12);
        
        // info Line
        Paragraph info = new Paragraph("Firma:", notificationFont);
        info.setTabSettings(new TabSettings(56f));
        info.add(Chunk.TABBING);
        info.add(new Chunk("SIV.AG (2015)"));
        info.setAlignment(Element.ALIGN_LEFT);
        
        // Add info Line to Document
        preface.add(info);
        
        Date date = new Date();
        Timestamp time = new Timestamp (date.getTime());
        
        // version Line
        Paragraph version = new Paragraph("Version:", notificationFont);
        version.setTabSettings(new TabSettings(56f));
        version.add(Chunk.TABBING);
        //version.add(new Chunk(time.toString()));
        version.add(Long.toString(System.currentTimeMillis()));
        version.setAlignment(Element.ALIGN_LEFT);
        
        // Add version Line to Document
        preface.add(version);
                
        document.add(preface);
        // Start a new page
        document.newPage();
    }

    private static void addContent(Document document, String Cuid, String From, String To) throws DocumentException, BadElementException, NamingException, SQLException, FileNotFoundException, FileNotFoundException, IOException, ParseException {
        Chapter chapterMaintenance = new Chapter(new Paragraph("Wartungsarbeiten", infoFont), 1);
        Paragraph paraMaintenanceD = new Paragraph("Kontinuierliche Arbeiten", infoHeadFont);
        paraMaintenanceD.setSpacingBefore(10);
        Section subMaintenanceD = chapterMaintenance.addSection(paraMaintenanceD);
        
        Context ctx3 = new InitialContext(); 
        DataSource ds3  = (DataSource) ctx3.lookup("jdbc/repository"); 
        Connection cn3 = ds3.getConnection(); 
        
        String sqlGC3 = "select decode(b.mactions,'base64') from managed_service_ccontracts a, class_contracttypes b where a.cttyid=b.cttyid and a.cuid=?";
        PreparedStatement ps3 = cn3.prepareStatement(sqlGC3);
        ps3.setInt(1,Integer.parseInt( Cuid ));
        ResultSet rs3 = ps3.executeQuery();
        
        while (rs3.next()) { 
            
            if (rs3.getString(1) != null) {
            
            Paragraph cw = new Paragraph(Basics.encodePdf(rs3.getString(1)), tableContFont);
            cw.setSpacingBefore(5);
            cw.setAlignment(Element.ALIGN_LEFT);
            cw.setIndentationLeft(25);
            cw.setIndentationRight(25);
            cw.setSpacingBefore(25);
            cw.setSpacingAfter(5);
            
            // Add Sub Headline to Document
            subMaintenanceD.add(cw);
            
            }
            
        }
        
        cn3.close();
        
        Paragraph paraMaintenanceE = new Paragraph("Sonderarbeiten", infoHeadFont);
        Section subMaintenanceE = chapterMaintenance.addSection(paraMaintenanceE);
        paraMaintenanceE.setSpacingBefore(10);
        createTable(subMaintenanceE, Cuid, From, To);
        
        document.add(chapterMaintenance);

    }
    
    private static void createTable(Section subCatPart, String Cuid, String From, String To)
        throws BadElementException, DocumentException, NamingException, SQLException, FileNotFoundException, IOException, ParseException {
        PdfPTable table = new PdfPTable(2);
        
        table.setWidthPercentage(90);
        table.setSpacingBefore(25);
        table.setSpacingAfter(5);
        table.setWidths(new int[]{1,2});
                
        PdfPCell c1 = new PdfPCell(new Phrase("Datum", tableHeadFont));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c1.setBorderWidth(1);
        c1.setPadding(5);
        c1.setBackgroundColor(BaseColor.GRAY);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Bemerkungen", tableHeadFont));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c1.setBorderWidth(1);
        c1.setPadding(5);
        c1.setBackgroundColor(BaseColor.GRAY);
        table.addCell(c1);

        table.setHeaderRows(1);

        /*
         * Gather Informations
         */
        
        Context ctx2 = new InitialContext(); 
        DataSource ds2  = (DataSource) ctx2.lookup("jdbc/repository"); 
        Connection cn2 = ds2.getConnection(); 
        
        //String sqlGC22 = "select to_timestamp(b.utim),decode(b.comt,'base64') from managed_service_cinfo a, managed_service_cservices b where a.cuid = b.cuid and a.cuid=" + Cuid + " and b.utim>" + Basics.ReportLongConvertDate(From) + " and b.utim<" + Basics.ReportLongConvertDate(To) + " order by 1 desc";
        //System.out.println(sqlGC22);
        
        String sqlGC2 = "select to_timestamp(b.utim),decode(b.comt,'base64') from managed_service_cinfo a, managed_service_cservices b where a.cuid = b.cuid and a.cuid=? and b.utim>? and b.utim<? order by 1 desc";
        PreparedStatement ps2 = cn2.prepareStatement(sqlGC2);
        ps2.setInt(1,Integer.parseInt( Cuid ));
        ps2.setLong(2, Basics.ReportLongConvertDate(From));
        ps2.setLong(3, Basics.ReportLongConvertDate(To));
        ResultSet rs2 = ps2.executeQuery();
        
        while (rs2.next()) { 
            PdfPCell c2 = new PdfPCell(new Phrase(Basics.encodePdf(rs2.getString(1)), tableContFont));
            c2.setHorizontalAlignment(Element.ALIGN_LEFT);
            c2.setVerticalAlignment(Element.ALIGN_TOP);
            c2.setBorderWidth(1);
            c2.setPadding(5);
            table.addCell(c2);

            c2 = new PdfPCell(new Phrase(Basics.encodePdf(rs2.getString(2)), tableContFont));
            c2.setHorizontalAlignment(Element.ALIGN_LEFT);
            c2.setVerticalAlignment(Element.ALIGN_TOP);
            c2.setBorderWidth(1);
            c2.setPadding(5);
            table.addCell(c2);
        }
        
        cn2.close();
        
        subCatPart.add(table);

    }

    private static void createList(Section subCatPart) {
        List list = new List(true, false, 10);
        list.add(new ListItem("First point"));
        list.add(new ListItem("Second point"));
        list.add(new ListItem("Third point"));
        subCatPart.add(list);
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }
    
    private static void addContact(PdfWriter writer, Document document, String Cuid)
        throws DocumentException, NamingException, SQLException, IOException {
        
        document.newPage();
        Chapter chapterMaintenance = new Chapter(new Paragraph("Bemerkungen", infoFont), 2);
        
        /* Bemerkungen */
        
        Context ctx = new InitialContext(); 
        DataSource ds  = (DataSource) ctx.lookup("jdbc/repository"); 
        Connection cn = ds.getConnection(); 
        
        String sqlGC = "SELECT DECODE(VALTEXT,'base64') FROM config_reporting WHERE KEY=encode('LastPageComment','base64')";
        PreparedStatement ps = cn.prepareStatement(sqlGC);
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            
            if (rs.getString(1) != null) {
            
            String str = Basics.encodePdf(rs.getString(1));
            
            Paragraph text = new Paragraph(str, tableContFont);
                    text.setSpacingBefore(5);
                    text.setAlignment(Element.ALIGN_LEFT);
                    text.setIndentationLeft(25);
                    text.setIndentationRight(25);
                    text.setSpacingBefore(25);
                    text.setSpacingAfter(5);

                // Add Headline to Document
                chapterMaintenance.add(text);
                
            }   
            
        }
        
        cn.close();
        
        /* Contracts */
        
        Connection cnCo = ds.getConnection(); 
        
        String sqlGCCo = "select decode(b.reptext,'base64') from managed_service_ccontracts a,class_contracttypes b where a.cttyid=b.cttyid AND a.cuid=?";
        PreparedStatement psCo = cnCo.prepareStatement(sqlGCCo);
        psCo.setInt(1,Integer.parseInt( Cuid ));
        ResultSet rsCo = psCo.executeQuery();
        
        while (rsCo.next()) {
            
            if (rsCo.getString(1) != null) {
                String str = Basics.encodePdf(rsCo.getString(1));
            
            Paragraph text = new Paragraph(str, tableContFont);
                    text.setSpacingBefore(5);
                    text.setAlignment(Element.ALIGN_LEFT);
                    text.setIndentationLeft(25);
                    text.setIndentationRight(25);
                    text.setSpacingBefore(25);
                    text.setSpacingAfter(5);

                // Add Headline to Document
                chapterMaintenance.add(text);
            
            }
                    
        }
        
        cnCo.close();
        
        /* Customer */
        
        Connection cnCu = ds.getConnection(); 
        
        String sqlGCCu = "SELECT DECODE(REPTEXT,'base64') from managed_service_cinfo where cuid=?";
        PreparedStatement psCu = cnCu.prepareStatement(sqlGCCu);
        psCu.setInt(1,Integer.parseInt( Cuid ));
        ResultSet rsCu = psCu.executeQuery();
        
        if (rsCu.next()) {
            
            if (rsCu.getString(1) != null) {
            
            String str = Basics.encodePdf(rsCu.getString(1));
            
            Paragraph text = new Paragraph(str, tableContFont);
                    text.setSpacingBefore(5);
                    text.setAlignment(Element.ALIGN_LEFT);
                    text.setIndentationLeft(25);
                    text.setIndentationRight(25);
                    text.setSpacingBefore(25);
                    text.setSpacingAfter(5);

                // Add Headline to Document
                chapterMaintenance.add(text);
            
            }
                    
        }
        
        cnCu.close();
        
        Connection cn2 = ds.getConnection(); 
        
        String sqlGC2 = "SELECT DECODE(VALTEXT,'base64') FROM config_reporting WHERE KEY=encode('LastPageContactsComment','base64')";
        PreparedStatement ps2 = cn2.prepareStatement(sqlGC2);
        ResultSet rs2 = ps2.executeQuery();
        
        if (rs2.next()) {
            
            if (rs2.getString(1) != null) {
            
            // Contacts
            Paragraph text2 = new Paragraph("Ansprechpartner", infoHeadFont);
                text2.setSpacingBefore(5);
                text2.setAlignment(Element.ALIGN_LEFT);
                text2.setIndentationLeft(25);
                text2.setIndentationRight(25);
                text2.setSpacingBefore(25);
                text2.setSpacingAfter(5);
        
            // Add Headline to Document
            chapterMaintenance.add(text2);
                
            String str = Basics.encodePdf(rs2.getString(1));
            
            Paragraph text = new Paragraph(str, tableContFont);
                    text.setSpacingBefore(5);
                    text.setAlignment(Element.ALIGN_LEFT);
                    text.setIndentationLeft(50);
                    text.setIndentationRight(25);
                    text.setSpacingBefore(5);
                    text.setSpacingAfter(5);

                // Add Headline to Document
                chapterMaintenance.add(text);
            
            }
        }
        
        cn2.close();
        
        document.add(chapterMaintenance);
    }
}
