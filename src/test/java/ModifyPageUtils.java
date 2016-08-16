import java.io.*;

/**
 * Created by liubin on 2015/12/3.
 */
public class ModifyPageUtils {
    public static String copyright="/*\n" +
                        " * Copyright 2014-2015 jingqubao All rights reserved.\n" +
                        " * \n" +
                        " * Support: http://www.jingqubao.com\n" +
                        " * \n" +
                        " * License: licensed\n" +
                        " * \n" +
                        " */\n";
    public static void main(String args[]){
    	String fileDir = "D:\\workplace\\shop\\src\\main\\webapp";
    	modifyFiles(fileDir);
    }
    private boolean copyFile(){
        boolean isSuccess = true;
        return isSuccess;
    }

    private static void changeContent(File file, String newFileName){
         BufferedReader br = null;
         BufferedWriter bw = null;
         try {
             br = new BufferedReader(new FileReader(file));
             bw = new BufferedWriter(new FileWriter(new File( newFileName )));
             String line = "";
             boolean hasCopyright = false;
             while( (line = br.readLine()) != null){
            	if (line.contains("SHOP++")) {//author
          			line = line.replace("SHOP++", "JQB SHOP");
          			hasCopyright = true;
          		} else if(line.contains("net.shopxx")){
          			line = line.replace("net.shopxx", "com.jqb.shop");
          			hasCopyright = true;
          		} else if ( line.contains("2005-2013") ) {
          			line = line.replace("2005-2013", "2014-2015"); 
          			hasCopyright = true;
          		} else if ( line.contains("shopxx.net") ) {
          			line = line.replace("shopxx.net", "jingqubao.com"); 
          			hasCopyright = true;
          		} 
            	bw.write(line + "\n");
             }
             if(hasCopyright){
            	 System.out.println("deal with : " + newFileName);
             }
         } catch (Exception e) {
             e.printStackTrace();
         } finally {  
             try {
 				br.close();
 				bw.close();
 			} catch (IOException e) {
 				e.printStackTrace();
 			}
         }  
    }
    
    private static void copyFile(File file){
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String fileName = file.getAbsolutePath();
       	 	String newFileName = fileName.replace("net" + File.separator + "shopxx", "com" + File.separator + "jqb" + File.separator + "shop");
            bw = new BufferedWriter(new FileWriter(new File( newFileName )));
            String line = "";
            while( (line = br.readLine()) != null){
            	bw.write(line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {  
            try {
				br.close();
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }  
   }
    
    /*
     * 递归目录下目录及其文件
     */
    static void modifyFiles(String filePath){
       File root = new File(filePath);
       if(root.isDirectory()){
    	   File[] files = root.listFiles();
           for(File file:files){     
            if(file.isDirectory()){
            	//递归目录
            	String dirName = root.getAbsolutePath();
            	String newDirName = dirName.replace("net" + File.separator + "shopxx", "com" + File.separator + "jqb" + File.separator + "shop");
            	File fileDir = new File(newDirName);
            	fileDir.mkdir();
            	modifyFiles(file.getAbsolutePath());
            	System.out.println("show "+filePath+"son files;"+file.getAbsolutePath());
            }else{
            	System.out.println("show "+filePath+"son files : "+file.getAbsolutePath());
            	String fileName = file.getAbsolutePath();
            	if (fileName.endsWith("jsp") || fileName.endsWith("css") || fileName.endsWith("xml")
            			|| fileName.endsWith("properties") || fileName.endsWith("html") || fileName.endsWith("ftl") || fileName.endsWith("sql")) {
            		String tmpName = fileName+"_bak";
                	File tmpFile = new File(tmpName);
                	file.renameTo( tmpFile );
                	changeContent(tmpFile,fileName);
                	tmpFile.delete();
            	}
            }     
           }
       } else {
    	   String fileName = root.getAbsolutePath();
    	   if (fileName.endsWith("jsp") || fileName.endsWith("css") || fileName.endsWith("xml")
       			|| fileName.endsWith("properties") || fileName.endsWith("html") || fileName.endsWith("ftl") || fileName.endsWith("sql")) {
       		   String tmpName = fileName+"_bak";
		       File tmpFile = new File(tmpName);
		       root.renameTo( tmpFile );
		       changeContent(tmpFile,fileName);
		       tmpFile.delete();
    	   }
       }
    }
    
    static void listFiles(String filePath){
        File root = new File(filePath);
        if(root.isDirectory()){
     	   File[] files = root.listFiles();
            for(File file:files){     
             if(file.isDirectory()){
            	 String dirName = root.getAbsolutePath();
            	 String newDirName = dirName.replace("net" + File.separator + "shopxx", "com" + File.separator + "jqb" + File.separator + "shop");
             	//递归目录
             	System.out.println("show "+filePath+"son dir : "+newDirName);
             }else{
            	 String fileName = root.getAbsolutePath();
            	 String newFileName = fileName.replace("net" + File.separator + "shopxx", "com" + File.separator + "jqb" + File.separator + "shop");
             	System.out.println("show "+filePath+" files :"+newFileName);
             }     
            }
        } else {
        	String fileName = root.getAbsolutePath();
        	String newFileName = fileName.replace("net" + File.separator + "shopxx", "com" + File.separator + "jqb" + File.separator + "shop");
        	System.out.println("show"+filePath+"son files:"+newFileName);
        }
     }
}