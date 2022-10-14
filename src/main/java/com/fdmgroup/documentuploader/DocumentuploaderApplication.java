package com.fdmgroup.documentuploader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.PropertySource;

/**
 * Root of the document uploader application which was developed by the authors listed below.
 * 
 * @author Noah Anderson
 * @author Roy Coates
 *
 */
@SpringBootApplication
@ConfigurationPropertiesScan("com.fdmgroup.documentuploader.config")
@PropertySource(value = { "classpath:/paths.properties" })
public class DocumentuploaderApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocumentuploaderApplication.class, args);
	}
}
