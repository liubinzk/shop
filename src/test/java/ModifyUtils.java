import java.io.*;

/**
 * Created by liubin on 2015/12/3.
 */
public class ModifyUtils {
    public static String copyright="/*\n" +
                        " * Copyright 2014-2015 jingqubao All rights reserved.\n" +
                        " * \n" +
                        " * Support: http://www.jingqubao.com\n" +
                        " * \n" +
                        " * License: licensed\n" +
                        " * \n" +
                        " */\n";
    public static void main(String args[]){
    	modifyFiles("src/main/java/net/shopxx", "package", copyright);
//    	listFiles("src/main/java/net/shopxx/");
//    	
//    	String name="src/main/java/net/shopxx/plugin/paypal/PaypalController.java";
//    	File file = new File(name);
//    	String fileName = file.getAbsolutePath();
//    	System.out.println(fileName.substring(fileName.lastIndexOf("shopxx")+7));
//    	String newFileName = fileName.replace("net" + File.separator + "shopxx", "com" + File.separator + "jqb" + File.separator + "shop");
//    	System.out.println(newFileName);
    }
    private boolean copyFile(){
        boolean isSuccess = true;
        return isSuccess;
    }

    private static void changeContent(File file, String token, String content){
    	 StringBuffer sb = new StringBuffer();
         BufferedReader br = null;
         BufferedWriter bw = null;
         try {
             br = new BufferedReader(new FileReader(file));
             String fileName = file.getAbsolutePath();
        	 String newFileName = fileName.replace("net" + File.separator + "shopxx", "com" + File.separator + "jqb" + File.separator + "shop");
             File dirFile = new File(newFileName.substring(0,newFileName.lastIndexOf(file.getName())));
             if ( !dirFile.exists()) {
            	 dirFile.mkdirs();
             }
             String packageStr = newFileName.substring(newFileName.indexOf("com"),newFileName.lastIndexOf(file.getName())-1);
             packageStr =packageStr.replace(File.separator, ".");
             bw = new BufferedWriter(new FileWriter(new File( newFileName )));
             String line = "";
             //增加coypright
             bw.write(copyright);
             boolean canWrite = false;
             while( (line = br.readLine()) != null){
             	if(canWrite) {
             		//package
             		if (line.contains("author SHOP++ Team")) {//author
             			line = line.replace("author SHOP++ Team", "author JQB Team");
             		} if(line.contains("net.shopxx")){
             			line = line.replace("net.shopxx", "com.jqb.shop");
             		}
             		bw.write(line + "\n");
             	} else {
             		if(line.startsWith(token)){
             			line = "package " + packageStr + ";";
             			bw.write(line + "\n");
             			canWrite = true;
             		}
             	}
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
    static void modifyFiles(String filePath,String token,String content){
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
            	modifyFiles(file.getAbsolutePath(),token, content);
            	System.out.println("show "+filePath+"son files:"+file.getAbsolutePath());
            }else{
            	System.out.println("show "+filePath+"son files:"+file.getAbsolutePath());
            	if ( file.getName().endsWith("java") ) {
            		changeContent(file, token, content);
            	} else {
            		copyFile(file);
            	}
            }     
           }
       } else {
    	   if(root.getName().endsWith("java")){
    		   changeContent(root, token, content);
    	   } else {
    		   copyFile(root);
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
             	System.out.println("show"+filePath+"son dir or files :"+newDirName);
             }else{
            	 String fileName = root.getAbsolutePath();
            	 String newFileName = fileName.replace("net" + File.separator + "shopxx", "com" + File.separator + "jqb" + File.separator + "shop");
             	System.out.println("show"+filePath+"son dir or files :"+newFileName);
             }     
            }
        } else {
        	String fileName = root.getAbsolutePath();
        	String newFileName = fileName.replace("net" + File.separator + "shopxx", "com" + File.separator + "jqb" + File.separator + "shop");
        	System.out.println("show"+filePath+"son dir or files :"+newFileName);
        }
     }
}