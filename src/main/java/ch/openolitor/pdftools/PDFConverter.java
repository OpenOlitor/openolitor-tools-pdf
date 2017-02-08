package ch.openolitor.pdftools;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ProcessBuilder.Redirect;

import javax.servlet.http.HttpServletResponse;

import org.springframework.util.FileCopyUtils;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;
import org.springframework.web.multipart.MultipartFile;

@Component
@Scope("singleton")
public class PDFConverter {

  private final String syn = "this";


  public void convert(MultipartFile file, String name, HttpServletResponse response) throws IOException {
    synchronized(syn) {
      try {
        String fileName = "/tmp/" + name;
        BufferedOutputStream stream = new BufferedOutputStream(
            new FileOutputStream(new File(fileName)));
        FileCopyUtils.copy(file.getInputStream(), stream);
        stream.close();
        System.out.println("File stored as: " + fileName);
        // Execute command
        System.out.println("File gets now converted: " + fileName);
        String[] command = { "soffice", "--headless",
            "--convert-to", "pdf", "--outdir", "/tmp/",
            fileName };

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectOutput(Redirect.INHERIT);
        pb.redirectError(Redirect.INHERIT);
        Process child = pb.start();

        int exitVal = child.waitFor();

        String newFileName = fileName.replace(".odt", ".pdf");

        if (0 == exitVal) {
          response.setHeader("Content-Type", "application/pdf");
          System.out.println("File got converted: " + newFileName
              + " : Exit value : " + exitVal);
          InputStream is = new FileInputStream(newFileName);
          // copy it to response's OutputStream
          org.apache.commons.io.IOUtils.copy(is,
              response.getOutputStream());
          response.flushBuffer();
        } else {
          response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not execute command. Exit status code: " + exitVal);
        }

        new File(fileName).delete();
        new File(newFileName).delete();
      } catch (Exception e) {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Exception occurred converting file: " + name + ": " + e.getMessage());
        System.err.println("Exception occurred converting file: "
            + name + ": " + e.getMessage());
        e.printStackTrace();
      }
    }
  }

}
