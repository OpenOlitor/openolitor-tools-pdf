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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;


@RestController
public class ConvertController {

  @Autowired
  private PDFConverter pdfConverter;

	@RequestMapping(method = RequestMethod.POST, value = "/convert2pdf")
	public void handleFileUpload(@RequestParam("name") String name,
			@RequestParam("upload") MultipartFile file,
			// RedirectAttributes redirectAttributes,
			HttpServletResponse response) {
    try {
  		if (name.contains("/")) {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Folder separators not allowed in file." + name);
  			System.err.println("Folder separators not allowed in file: " + name);
  		}

  		if (!file.isEmpty()) {
  			pdfConverter.convert(file, name, response);
  		} else {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "The input-file was empty.");
  			System.err.println("File was empty: " + name);
  		}
    } catch (IOException e) {
      System.err.println("IOException: " + e.getMessage());
    }
	}
}
