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

@RestController
public class ConvertController {

	@RequestMapping(method = RequestMethod.POST, value = "/convert2pdf")
	public void handleFileUpload(@RequestParam("name") String name,
			@RequestParam("upload") MultipartFile file,
			//RedirectAttributes redirectAttributes, 
			HttpServletResponse response) {
		if (name.contains("/")) {
			System.err.println("Folder separators not allowed in file: " + name);
		}

		if (!file.isEmpty()) {
			try {
				String fileName = "/tmp/" + name;
				BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(new File(fileName)));
				FileCopyUtils.copy(file.getInputStream(), stream);
				stream.close();
				System.out.println("File stored as: " + fileName);

				try {
					// Execute command
					System.out.println("File gets now converted: " + fileName);
					String[] command = {"soffice",  "--headless",  "--convert-to", "pdf", "--outdir",  "/tmp/",
							fileName };
					
					ProcessBuilder pb = new ProcessBuilder(command);
					pb.redirectOutput(Redirect.INHERIT);
					pb.redirectError(Redirect.INHERIT);
					Process child = pb.start();
					
					int exitVal = child.waitFor();

					response.setHeader("Content-Type", "application/pdf");
					String newFileName = fileName.replace(".odt", ".pdf");
					System.out.println("File got converted: " + newFileName + " : Exit value : " + exitVal);
					InputStream is = new FileInputStream(newFileName);
					// copy it to response's OutputStream
					org.apache.commons.io.IOUtils.copy(is,
							response.getOutputStream());
					response.flushBuffer();
			        new File(fileName).delete();
			        new File(newFileName).delete();
				} catch (IOException e) {
				}

			} catch (Exception e) {
				System.err.println("Exception occurred converting file: " + name + ": " + e.getMessage());
				e.printStackTrace();
			}
		} else {
			System.err.println("File was empty: " + name);
		}
	}

}
