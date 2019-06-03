# openolitor-tools-pdf

Not used anymore since [eugenmayer/jodconverter](https://github.com/EugenMayer/docker-image-jodconverter) does a gread job!

This porject is ready to be deployed on a Cloud Foundry infrastructure. It transforms OpenDocument files sent to [URL]/convert2pdf to PDF and returns the created document.

The POST request must contain two parameters:

- name=[filename]
- upload=[file]

You may try to convert a file using this command-line:

```shell
curl -v -F "name=test.odt" -F "upload=@test.odt" http://[URL]/convert2pdf
```

The transformation is executed by LibreOffice. The space attributed to the Cloud Foundry instance must be 1GB as the LibreOffice and the needed OpenJDK packages are as large as 850MB.
