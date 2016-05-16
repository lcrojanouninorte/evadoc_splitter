/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evadoc_splitter;

import static com.oracle.jrockit.jfr.ContentType.Timestamp;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javax.swing.Spring.height;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.PDFTextStripperByArea;

/**
 *
 * @author Developer
 */

public class pdfsplitter {
    String path = "";
    //coordenadas comentarios:
    int x_coment_id;
    int y_coment_id;
    PDDocument document;
    private String current_time;
    public HashMap<String, String> local_id_to_name = new HashMap<String, String>();
    public HashMap<String, String> local_programa_to_division = new HashMap<String, String>();
    private String tipo = "";
    private boolean has_division = false;
    private String root = "";
    public  pdfsplitter(String path, HashMap<String, String> caller_id_to_name,HashMap<String, String> caller_program_to_div, String root){
       
        try { 
            this.path = path;
            File file = new File(root+"/..");
            System.out.println();
            this.root= file.getCanonicalPath()+"//";
            this.document = PDDocument.load(this.path);
            this.local_id_to_name = caller_id_to_name;
            this.local_programa_to_division = caller_program_to_div;
            if(local_programa_to_division.size()>0){
                this.has_division = true;
            }else{
                this.has_division = false;
            }
        } catch (IOException ex) {
            Logger.getLogger(pdfsplitter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void split(String current_time){
        
        HashMap<String, String> teacher_to_name = new HashMap<String, String>();
        HashMap<String, String> program_to_division = new HashMap<String, String>();
        try {
            
            //open pdf
            this.document = PDDocument.load(this.path);
            this.current_time = current_time;
            
            //OBTENER EL TIPO DE REPORTE Y CORTAR ESPECIFICAMENTE.
              String type ="";
            if(this.tipo.equals("")){
                type = get_report_type();
            }else{
                type = this.tipo;
            }
            
//::::::::::tipo comentario::::::::::::::::::::::::
            if(type.equals("GZRDEVA")){//tipo comentario
                
                //si es pregrado ordenar por dpto, si es postgrado ordenar por programa
                
                //obtener tipo de evaluaccion:
                Rectangle2D region_eval_tipo = new Rectangle2D.Double(70, 170, 75, 20);
                PDPage page_from =   (PDPage)this.document.getDocumentCatalog().getAllPages().get(0);
                String tipo_comentarios = get_text_by_area(region_eval_tipo, page_from, 0);
                if(tipo_comentarios.equals("EVADOCPOS")){
                    //organizar por programa
                    Rectangle2D region_programa = new Rectangle2D.Double(390, 155, 250, 20);
                    Rectangle2D region_periodo = new Rectangle2D.Double(250, 60, 100, 20);
                    Rectangle2D region_id = new Rectangle2D.Double(125, 155, 55, 12);
                    Rectangle2D region_nombre= new Rectangle2D.Double(170, 155, 155, 20);
                    int numberOfPages = this.document.getNumberOfPages();
                   // String regionName = "region_type";
                    split_report(region_programa, region_periodo, region_id,region_nombre, "Consolidado Comentarios",numberOfPages);
                
                }else{
                    //en cualquier otro caso (pregrados) organizar por depto
                    Rectangle2D region_dpto = new Rectangle2D.Double(90, 110, 500, 20); //ahora es el dpto en ves de programa
                    Rectangle2D region_periodo = new Rectangle2D.Double(250, 60, 100, 20);
                    Rectangle2D region_id = new Rectangle2D.Double(125, 155, 55, 12);
                    Rectangle2D region_nombre= new Rectangle2D.Double(170, 155, 155, 20);
                    int numberOfPages = this.document.getNumberOfPages();
                   // String regionName = "region_type";
                    split_report(region_dpto, region_periodo, region_id,region_nombre, "Consolidado Comentarios",numberOfPages);
                
                
                }
                
              
            }
//::::::::::::Catedra:::::::::::::::::::::      
            
            if(type.equals( "GZRDPRP")){//tipo reporte catedra postgrado
                //organizar por programa
                Rectangle2D region_programa = new Rectangle2D.Double(140, 110, 500, 20);
                Rectangle2D region_periodo = new Rectangle2D.Double(275, 60, 60, 20);
                Rectangle2D region_id = new Rectangle2D.Double(50, 140, 75, 20);
                Rectangle2D region_nombre= new Rectangle2D.Double(120, 140, 155, 20);
                int numberOfPages = this.document.getNumberOfPages();
              // String regionName = "region_type";
                split_report(region_programa, region_periodo, region_id,region_nombre, "Consolidado Catedra", numberOfPages);
            }
            
                        
            if(type.equals( "GZRDEPR")){//tipo reporte catedra pregrado
                //organizar por dpto
                Rectangle2D region_dpto = new Rectangle2D.Double(120, 100, 500, 20);
                Rectangle2D region_periodo = new Rectangle2D.Double(275, 60, 60, 20);
                Rectangle2D region_id = new Rectangle2D.Double(50, 120, 75, 20);
                Rectangle2D region_nombre= new Rectangle2D.Double(120, 120, 155, 20);
                int numberOfPages = this.document.getNumberOfPages();
              // String regionName = "region_type";
                split_report(region_dpto, region_periodo, region_id,region_nombre, "Consolidado Catedra", numberOfPages);
            }
            //consolidado division postgrado
            if(type.equals("GZRDDIP")){
                  //organizar por div
                Rectangle2D region_div = new Rectangle2D.Double(65, 100, 500, 20);
                Rectangle2D region_periodo = new Rectangle2D.Double(330, 60, 100, 20);
                Rectangle2D region_programa = new Rectangle2D.Double(0, 150, 150, 12);
                Rectangle2D region_nombre= new Rectangle2D.Double(120, 120, 155, 20);
                int numberOfPages = this.document.getNumberOfPages();
                // String regionName = "region_type";
                split_report_consolidado_division(region_div, region_periodo, region_programa, "Consolidado Division", numberOfPages);
            }
              //consolidado division pregrado
            if(type.equals("GZRDEDI")){
                  //organizar por div
                Rectangle2D region_div = new Rectangle2D.Double(65, 100, 500, 20);
                Rectangle2D region_periodo = new Rectangle2D.Double(290, 60, 30, 20);
                Rectangle2D region_programa = new Rectangle2D.Double(0, 150, 150, 12);
                Rectangle2D region_nombre= new Rectangle2D.Double(120, 120, 155, 20);
                int numberOfPages = this.document.getNumberOfPages();
                // String regionName = "region_type";
                split_report_consolidado_division(region_div, region_periodo, region_programa, "Consolidado Division", numberOfPages);
            }
            
               
            
            
            //get pdf type by extracting baner ID from coordinates.
            
           //si es comentario... if type == "GZRDEVA"
            
            //pdf.split by id in cordenates
           //si es resultado... else GZRDPRP
            //split by id in coordinates
            int numberOfPages = this.document.getNumberOfPages();
             System.out.println("Selected file: " + numberOfPages);
        } catch (IOException ex) {
            Logger.getLogger(pdfsplitter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public void splitByPages(int inicio, int fin){
    }
    
    public void createFolder(String path){  
    
    }

    private String get_report_type() {
      String type = "";
      try { 
            PDPage firstPage = (PDPage)this.document.getDocumentCatalog().getAllPages().get(0);
            //verificar si es comentario
            Rectangle2D region = new Rectangle2D.Double(0, 730, 300, 20);
            String regionName = "region_id_comentarios";
            PDFTextStripperByArea stripper;
            stripper = new PDFTextStripperByArea();
            stripper.addRegion(regionName, region);
            stripper.extractRegions(firstPage);
            type = stripper.getTextForRegion( regionName ).trim();
            //verificar si no es comentario
            if(!type.equals("GZRDEVA")){
                //verificar si es reporte
                region = new Rectangle2D.Double(0, 570, 300, 20);
                regionName = "region_id_reporte";
                stripper = new PDFTextStripperByArea();
                stripper.addRegion(regionName, region);
                stripper.extractRegions(firstPage);
                type = stripper.getTextForRegion( regionName ).trim();   
            }
            
           if(!type.equals("GZRDEVA") && !type.equals("GZRDPRP") && !type.equals("GZRDEPR") ){ 
               type = "";
           }
            
            
       } catch (IOException ex) {
            Logger.getLogger(pdfsplitter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return type; //To change body of generated methods, choose Tools | Templates.
    }
    public String get_text_by_area(Rectangle2D region,  int page) {
      String type = "";
      int numberOfPages = this.document.getNumberOfPages();
      try { 
        for (int i=0;i<numberOfPages;i++){

                    PDPage firstPage = (PDPage)document.getDocumentCatalog().getAllPages().get(i);
                    //Rectangle2D region = new Rectangle2D.Double(0, 100, 300, 100);
                    String regionName = "region_id_"+i;
                    PDFTextStripperByArea stripper;
                    stripper = new PDFTextStripperByArea();
                    stripper.addRegion(regionName, region);
                    stripper.extractRegions(firstPage);
                    type += stripper.getTextForRegion(regionName)+"-"+i+"\n";
        }  
        this.document.close();
      } catch (IOException ex) {
            Logger.getLogger(pdfsplitter.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        
        return type.trim(); //To change body of generated methods, choose Tools | Templates.
    }
    public String get_text_by_rentangle(Rectangle2D region,  int page) {
      String type = "";
      int numberOfPages = this.document.getNumberOfPages();
      try { 


                    PDPage firstPage = (PDPage)document.getDocumentCatalog().getAllPages().get(page);
                    //Rectangle2D region = new Rectangle2D.Double(0, 100, 300, 100);
                    String regionName = "region_id_"+page;
                    PDFTextStripperByArea stripper;
                    stripper = new PDFTextStripperByArea();
                    stripper.addRegion(regionName, region);
                    stripper.extractRegions(firstPage);
                    type += stripper.getTextForRegion(regionName);
        
        this.document.close();
      } catch (IOException ex) {
            Logger.getLogger(pdfsplitter.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        
        return type.trim(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    public String get_text_by_area(Rectangle2D region,  PDPage page, int id) {
      String text = "";
   
      try { 


                    //PDPage firstPage = (PDPage)document.getDocumentCatalog().getAllPages().get(i);
                    //Rectangle2D region = new Rectangle2D.Double(0, 100, 300, 100);
                    String regionName = "region_id_"+id;
                    PDFTextStripperByArea stripper;
                    stripper = new PDFTextStripperByArea();
                    stripper.addRegion(regionName, region);
                    stripper.extractRegions(page);
                    text = stripper.getTextForRegion(regionName);
        
        //this.document.close();
      } catch (IOException ex) {
            Logger.getLogger(pdfsplitter.class.getName()).log(Level.SEVERE, null, ex);
        }
       
        
        return text.trim(); //To change body of generated methods, choose Tools | Templates.
    }

    private void split_report(Rectangle2D programa_area, Rectangle2D periodo_area, Rectangle2D id_area,Rectangle2D nombre_area, String type, int numberOfPages) {
       // pages = this.doc
        
        //crear hash id=>nombre que se guarda en la variable global del programa principal
        
        try {
//iteracion n - 1
            // Create a new empty document
            PDDocument document_to = new PDDocument();
            //añadir la primera pagina
            PDPage page_from =   (PDPage)this.document.getDocumentCatalog().getAllPages().get(0);
            document_to.addPage( page_from );      
            //obtener datos de la primera pagina
            String programa = get_text_by_area(programa_area, page_from,1);
            String id = get_text_by_area(id_area, page_from, 0);
            String periodo = get_text_by_area(periodo_area, page_from,2);
            String nombre = get_text_by_area(nombre_area, page_from, 0);
            
            String path = "";
            String id_to_name ;
  
            File folder;

            
            //si solo hay una pagina.
            if(numberOfPages==1){
             //document_to.save(type+"_"+programa+"_"+id+"_.pdf");
            }

             //TODO: verificar que ninguno es vacio
//iteracion n, se comprara n con n-1
             //desde la segunda pagina verificar si el id es el mismo y terminar cuando cambie
             for(int i = 1; i<numberOfPages; i++){
                 page_from =   (PDPage)this.document.getDocumentCatalog().getAllPages().get(i);
                 String id_next = get_text_by_area(id_area, page_from,i);
                 
                 //verificar si es hoja vacia
                 


                 //verificar que id_next sea igual que id
                 if(id_next.equals(id)){
                     //si es igual al anterior añadir la pagina al documento
                    document_to.addPage( page_from );
                 }else{     
                //momento para crear un hash, que relacione id con nombre, 
                //con el fin de referenciar por id y no por nombre.
                
                 set_id_name_hash(id, nombre); //actualizar relacion id => nombre
                // Save the newly created document
                 //obtener nombre del id en cuestion:
                 id_to_name = this.local_id_to_name.get(id);               
                //verificar si esta disponible la informacion sobre la division                
                 if(this.has_division){
                     String division = this.local_programa_to_division.get(programa);
                   //  path = this.root+"Resultados_"+this.current_time + "\\" +division+"\\"+periodo+"\\"+programa+"\\"+id_to_name;
                   path = this.root+"RES_"+periodo + "\\" +division+"\\"+programa+"\\"+id_to_name;
                 }else{
                  //  path = this.root+"Resultados_"+this.current_time + "\\" +programa+"\\"+periodo+"\\"+id_to_name;
                   path = this.root+"RES_"+periodo +  "\\" +programa+"\\"+id_to_name;
                 }
                 //create directory if not exist:
                 folder = new File(path);
                 if (!folder.exists()) { 
                    folder.mkdirs();
                 }

                 document_to.save(path+"\\"+type+".pdf");
                
                //Preparacion para la siguiente iteracion 
                //crear un nuevo documento
                 document_to = new PDDocument();
                 //añadir la pagina encontrada que es diferente a la anterior
                 page_from =   (PDPage)this.document.getDocumentCatalog().getAllPages().get(i);
                 document_to.addPage( page_from );
                 //obtener datos de la  pagina
                 id = get_text_by_area(id_area, page_from,i);
                 while(id == null){
                  i++;
                  page_from =   (PDPage)this.document.getDocumentCatalog().getAllPages().get(i);
                 }
                 id = get_text_by_area(id_area, page_from,i);
                 programa = get_text_by_area(programa_area, page_from,i);
                 periodo = get_text_by_area(periodo_area, page_from,i);
                 nombre = get_text_by_area(nombre_area, page_from,i);
                 set_id_name_hash(id, nombre); 
                 }
                 
                 //añadir el profesor de la ultima pagina
                 

             }
             
             
           //:::::::ultima iteracion:::::
           
             //momento para crear un hash, que relacione id con nombre, 
            //con el fin de referenciar por id y no por nombre.
             
            id_to_name = this.local_id_to_name.get(id);
            if(this.has_division){
                     String division = this.local_programa_to_division.get(programa);
                    // path = this.root+"Resultados_"+this.current_time + "\\" +division+"\\"+periodo+"\\"+programa+"\\"+id_to_name;
                    path = this.root+"RES_"+periodo + "\\" +division+"\\"+programa+"\\"+id_to_name;
            }else{
                    // path = this.root+"Resultados_"+this.current_time + "\\" +programa+"\\"+periodo+"\\"+id_to_name;
                    path = this.root+"RES_"+periodo +  "\\" +programa+"\\"+id_to_name;
            }
            folder = new File(path);
            //obtener nombre del id en cuestion:
            if (!folder.exists()) { 
                folder.mkdirs();
            }

             document_to.save(path+"\\"+type+".pdf");

        
        } catch (IOException ex) {
            Logger.getLogger(pdfsplitter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (COSVisitorException ex) {
            Logger.getLogger(pdfsplitter.class.getName()).log(Level.SEVERE, null, ex);
        }
        

        
        
       //get periodo
        
       //get programa
         
    }

    //caso especial para el consolidado por division
    private void split_report_consolidado_division(Rectangle2D region_div, Rectangle2D region_periodo, Rectangle2D region_programa, String type, int numberOfPages) {
       // pages = this.doc
        
        //crear hash id=>nombre que se guarda en la variable global del programa principal
        Rectangle2D next_region_programa = region_programa;
        try {
//iteracion n - 1
            // Create a new empty document
            PDDocument document_to = new PDDocument();
            //añadir la primera pagina
            PDPage page_from =   (PDPage)this.document.getDocumentCatalog().getAllPages().get(0);
            document_to.addPage( page_from );      
            //obtener datos de la primera pagina
            String division = get_text_by_area(region_div, page_from,1);
            String periodo = get_text_by_area(region_periodo, page_from,2);
            hash_programs(region_programa, page_from, division);
            
            //si solo hay una pagina.
            if(numberOfPages==1){
             //document_to.save(type+"_"+programa+"_"+id+"_.pdf");
            }
             //TODO: verificar que ninguno es vacio
            
//iteracion n, se comprara n con n-1
             //desde la segunda pagina verificar si division es vacio o diferente y terminar cuando cambie
             for(int i = 1; i<numberOfPages; i++){
                 page_from =   (PDPage)this.document.getDocumentCatalog().getAllPages().get(i);
                 String division_next = get_text_by_area(region_div, page_from,i);
                 //verificar que div_next sea igual que div, o vacio, por que la hoja vacia hace parte de div
                 if(division_next.equals(division) || division_next.equals("") ){
                     
                     //si es igual al anterior añadir la pagina al documento
                    document_to.addPage( page_from );
                    //obtener programas de las siguientes hojasn
                    if(!division_next.equals("") ){
                        //si es la hoja vacia no buscar programas.
                        hash_programs(region_programa, page_from, division);
                    }
                 }else{ 
                     //crear una estructura de resultados, division, periodo consolidado div.
                     
                 //File folder = new File(this.root+"Resultados_"+this.current_time + "\\" +division+"\\"+"\\"+periodo+"\\");
                 File folder = new File("RES_"+periodo + "\\" +division+"\\");
                 if (!folder.exists()) { 
                    folder.mkdirs();
                 }

                // document_to.save(this.root+"Resultados_"+this.current_time + "\\" +division+"\\"+"\\"+periodo+"\\"+type+".pdf");
                document_to.save("RES_"+periodo  + "\\" +division+"\\"+type+".pdf");
                
                //Preparacion para la siguiente iteracion 
                //crear un nuevo documento
                 document_to = new PDDocument();
                 //añadir la pagina encontrada que es diferente a la anterior
                 page_from =   (PDPage)this.document.getDocumentCatalog().getAllPages().get(i);
                 document_to.addPage( page_from );
                 //obtener datos de la  pagina
                 division = get_text_by_area(region_div, page_from,i);
                 periodo = get_text_by_area(region_periodo, page_from,i);
                 hash_programs(region_programa, page_from, division);


                 }

             }
             //ultima iteracion
             //create directory if not exist:
            // File folder = new File(this.root+"Resultados_"+this.current_time + "\\" +division+"\\"+periodo+"\\");
            File folder = new File("RES_"+periodo  + "\\" +division+"\\");
             if (!folder.exists()) { 
                folder.mkdirs();
             }
            // document_to.save(this.root+"Resultados_"+this.current_time + "\\" +division+"\\"+periodo+"\\"+"\\"+type+".pdf");
            document_to.save("RES_"+periodo +  "\\" +division+"\\"+type+".pdf");

        
        } catch (IOException ex) {
            Logger.getLogger(pdfsplitter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (COSVisitorException ex) {
            Logger.getLogger(pdfsplitter.class.getName()).log(Level.SEVERE, null, ex);
        }
        

        
        
       //get periodo
        
       //get programa
         
    }
    
    private void hash_programs(Rectangle2D region_programa, PDPage page_from, String division){
        String programa = get_text_by_area(region_programa, page_from, 0);
        Rectangle2D next_region_programa = new Rectangle2D.Double(region_programa.getX(), region_programa.getY(), region_programa.getWidth(), region_programa.getHeight());
        while(!programa.toLowerCase().equals("") && !programa.toLowerCase().equals("división")){
            set_programa_division_hash(programa,division);
            next_region_programa.setRect(next_region_programa.getX(), next_region_programa.getY() + 12, next_region_programa.getWidth(), next_region_programa.getHeight());
            programa = get_text_by_area(next_region_programa, page_from, 0);
           programa.trim();
           programa.toLowerCase();
        }
       
    }
    

    private void set_id_name_hash(String id, String nombre) {
        
        String value = this.local_id_to_name.get(id.trim());
        if (value != null) {
            int i = 0;
            //no escribir nombre, pues ya existe una relacion id => nombre
        } else {
            // insertar una relacion id=> nombre
            this.local_id_to_name.put(id.trim(), nombre.trim());	
        }

    }
    private void set_programa_division_hash(String programa, String division) {
        
        String value = this.local_programa_to_division.get(programa.trim());
        if (value != null) {
            //no escribir nombre, pues ya existe una relacion id => nombre
        } else {
            // insertar una relacion id=> nombre
            this.local_programa_to_division.put(programa.trim(), division.trim());	
        }

    }

    HashMap<String, String> get_local_id_to_name() {
        return this.local_id_to_name;
    }
    HashMap<String, String> get_local_programa_to_division() {
        return this.local_programa_to_division;
    }

    void set_type(String tipo) {
        this.tipo = tipo;
    }
    
    
}
