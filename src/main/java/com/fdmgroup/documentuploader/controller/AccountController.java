package com.fdmgroup.documentuploader.controller;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fdmgroup.documentuploader.config.ApplicationProperties;
import com.fdmgroup.documentuploader.config.ApplicationProperties.RequestUris;
import com.fdmgroup.documentuploader.enums.AttributeName;
import com.fdmgroup.documentuploader.enums.ViewPath;
import com.fdmgroup.documentuploader.model.account.Account;
import com.fdmgroup.documentuploader.model.document.Document;
import com.fdmgroup.documentuploader.model.user.User;
import com.fdmgroup.documentuploader.service.account.AbstractAccountService;
import com.fdmgroup.documentuploader.service.account.AccountService;
import com.fdmgroup.documentuploader.service.document.AbstractDocumentService;

@Controller
@SessionAttributes({ "user", "account" })
@RequestMapping("${app.request-uris.account}")
@PreAuthorize("isAuthenticated()")
public class AccountController {

	private static final String REDIRECT = "redirect:";

	/**
	 * Used to retrieve messages from messages.properties.
	 */
	private final MessageSource messageSource;
	private final AbstractAccountService accountService;
	private final AbstractDocumentService documentService;
	private final RequestUris requestUris;
	
	@Autowired
	public AccountController(MessageSource messageSource, AccountService accountService, AbstractDocumentService documentService,
			ApplicationProperties applicationProperties) {
		super();
		this.messageSource = messageSource;
		this.accountService = accountService;
		this.documentService = documentService;
		this.requestUris = applicationProperties.getRequestUris();
	}

	@GetMapping("/{accountName}")
	public String toAccount(@PathVariable(value = "accountName") String name, HttpSession httpSession, Model model) {
		User user = (User) httpSession.getAttribute("user");
		List<Account> accounts = accountService.getAllAccountsByUserId(user.getId());
		Account account = accounts.stream().filter(a -> a.getName().equals(name)).collect(Collectors.toList()).get(0);
		model.addAttribute(AttributeName.ACCOUNT.getValue(), account);

		return ViewPath.ACCOUNT.getPath();
	}

	@PostMapping
	public String uploadFile(Account account, HttpSession httpSession,
			@RequestParam(value = "file") MultipartFile file) {
		Account updatedAccount = accountService.addFileToAccount(file, account);
		updateAccountInSession(httpSession, updatedAccount);

		return REDIRECT + requestUris.getAccount() + "/" + account.getName();
	}
	
	private void updateAccountInSession(HttpSession httpSession, Account account) {
		httpSession.setAttribute(AttributeName.ACCOUNT.getValue(), account);
	}
	
	@GetMapping("${app.request-uris.delete-document}" + "/{documentName}")
	public String deleteDocument(Account account, HttpSession httpSession, RedirectAttributes redirectAttributes,
			@PathVariable("documentName") String fileName) {
		Account updatedAccount = accountService.removeFileFromAccountByFileName(fileName, account);
		updateAccountInSession(httpSession, updatedAccount);
		redirectAttributes.addFlashAttribute(AttributeName.MESSAGE.getValue(), fileName + " " + 
				messageSource.getMessage("document.deleted", null, Locale.getDefault()));

		return REDIRECT + requestUris.getAccount() + "/" + account.getName();
	}

	@GetMapping("${app.request-uris.download-document}" + "/{documentName}")
	public ResponseEntity<Object> downloadDocument(@PathVariable("documentName") String fileName) {
		Optional<Document> optionalDocument = documentService.findByName(fileName);
		if (!optionalDocument.isPresent()) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		Document document = optionalDocument.get();
		byte[] contents = document.getContent();

		String fileNameWithExtension = fileName + "." + document.getExtension();

		MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();
		String mimeType = fileTypeMap.getContentType(fileNameWithExtension);

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentLength(contents.length);
		responseHeaders.setContentType(MediaType.valueOf(mimeType));
		responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileNameWithExtension);

		return new ResponseEntity<>(contents, responseHeaders, HttpStatus.OK);
	}
}
