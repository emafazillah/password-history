package com.ema.test.passwordhistory.web.rest;

import com.codahale.metrics.annotation.Timed;

import com.ema.test.passwordhistory.domain.User;
import com.ema.test.passwordhistory.repository.UserRepository;
import com.ema.test.passwordhistory.security.SecurityUtils;
import com.ema.test.passwordhistory.service.MailService;
import com.ema.test.passwordhistory.service.PasswordHistoryService;
import com.ema.test.passwordhistory.service.UserService;
import com.ema.test.passwordhistory.service.dto.PasswordHistoryDTO;
import com.ema.test.passwordhistory.service.dto.UserDTO;
import com.ema.test.passwordhistory.web.rest.vm.EmailAndPasswordVM;
import com.ema.test.passwordhistory.web.rest.vm.KeyAndPasswordVM;
import com.ema.test.passwordhistory.web.rest.vm.ManagedUserVM;
import com.ema.test.passwordhistory.web.rest.util.HeaderUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    private final UserRepository userRepository;

    private final UserService userService;

    private final MailService mailService;
    
    private final PasswordHistoryService passwordHistoryService;

    private static final String CHECK_ERROR_MESSAGE = "Incorrect password";

    public AccountResource(UserRepository userRepository, UserService userService,
            MailService mailService, PasswordHistoryService passwordHistoryService) {

        this.userRepository = userRepository;
        this.userService = userService;
        this.mailService = mailService;
        this.passwordHistoryService = passwordHistoryService;
    }

    /**
     * POST  /register : register the user.
     *
     * @param managedUserVM the managed user View Model
     * @return the ResponseEntity with status 201 (Created) if the user is registered or 400 (Bad Request) if the login or email is already in use
     */
    @PostMapping(path = "/register",
        produces={MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    @Timed
    public ResponseEntity registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) {

        HttpHeaders textPlainHeaders = new HttpHeaders();
        textPlainHeaders.setContentType(MediaType.TEXT_PLAIN);
        if (!checkPasswordLength(managedUserVM.getPassword())) {
            return new ResponseEntity<>(CHECK_ERROR_MESSAGE, HttpStatus.BAD_REQUEST);
        }
        return userRepository.findOneByLogin(managedUserVM.getLogin().toLowerCase())
            .map(user -> new ResponseEntity<>("login already in use", textPlainHeaders, HttpStatus.BAD_REQUEST))
            .orElseGet(() -> userRepository.findOneByEmail(managedUserVM.getEmail())
                .map(user -> new ResponseEntity<>("email address already in use", textPlainHeaders, HttpStatus.BAD_REQUEST))
                .orElseGet(() -> {
                    User user = userService
                        .createUser(managedUserVM.getLogin(), managedUserVM.getPassword(),
                            managedUserVM.getFirstName(), managedUserVM.getLastName(),
                            managedUserVM.getEmail().toLowerCase(), managedUserVM.getImageUrl(),
                            managedUserVM.getLangKey());

                    mailService.sendActivationEmail(user);
                    return new ResponseEntity<>(HttpStatus.CREATED);
                })
        );
    }

    /**
     * GET  /activate : activate the registered user.
     *
     * @param key the activation key
     * @return the ResponseEntity with status 200 (OK) and the activated user in body, or status 500 (Internal Server Error) if the user couldn't be activated
     */
    @GetMapping("/activate")
    @Timed
    public ResponseEntity<String> activateAccount(@RequestParam(value = "key") String key) {
        return userService.activateRegistration(key)
            .map(user -> new ResponseEntity<String>(HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    /**
     * GET  /authenticate : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request
     * @return the login if the user is authenticated
     */
    @GetMapping("/authenticate")
    @Timed
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * GET  /account : get the current user.
     *
     * @return the ResponseEntity with status 200 (OK) and the current user in body, or status 500 (Internal Server Error) if the user couldn't be returned
     */
    @GetMapping("/account")
    @Timed
    public ResponseEntity<UserDTO> getAccount() {
        return Optional.ofNullable(userService.getUserWithAuthorities())
            .map(user -> new ResponseEntity<>(new UserDTO(user), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    /**
     * POST  /account : update the current user information.
     *
     * @param userDTO the current user information
     * @return the ResponseEntity with status 200 (OK), or status 400 (Bad Request) or 500 (Internal Server Error) if the user couldn't be updated
     */
    @PostMapping("/account")
    @Timed
    public ResponseEntity saveAccount(@Valid @RequestBody UserDTO userDTO) {
        final String userLogin = SecurityUtils.getCurrentUserLogin();
        Optional<User> existingUser = userRepository.findOneByEmail(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getLogin().equalsIgnoreCase(userLogin))) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("user-management", "emailexists", "Email already in use")).body(null);
        }
        return userRepository
            .findOneByLogin(userLogin)
            .map(u -> {
                userService.updateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(),
                    userDTO.getLangKey(), userDTO.getImageUrl());
                return new ResponseEntity(HttpStatus.OK);
            })
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    /**
     * POST  /account/change_password : changes the current user's password
     *
     * @param password the new password
     * @return the ResponseEntity with status 200 (OK), or status 400 (Bad Request) if the new password is not strong enough
     */
    /* Original
    @PostMapping(path = "/account/change_password",
        produces = MediaType.TEXT_PLAIN_VALUE)
    @Timed
    public ResponseEntity changePassword(@RequestBody String password) {
        if (!checkPasswordLength(password)) {
            return new ResponseEntity<>(CHECK_ERROR_MESSAGE, HttpStatus.BAD_REQUEST);
        }
        userService.changePassword(password);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    */
    @PostMapping(path = "/account/change_password",
            produces = MediaType.TEXT_PLAIN_VALUE)
    @Timed
    public ResponseEntity changePassword(@RequestBody EmailAndPasswordVM emailAndPasswordVM) {
        if (!checkPasswordLength(emailAndPasswordVM.getPassword())) {
            return new ResponseEntity<>(CHECK_ERROR_MESSAGE, HttpStatus.BAD_REQUEST);
        }
        // Check password history
    	Optional<User> user = userRepository.findOneByEmail(emailAndPasswordVM.getEmail());
    	if (user.isPresent()) {
    		Boolean checkPasswordExist = false;
    		Optional<PasswordHistoryDTO> passwordHistoryDTO = passwordHistoryService.findOneByUserEmail(user.get().getEmail());
    		if (passwordHistoryDTO.isPresent()) {
    			// TODO
    			PasswordHistoryDTO phDTO = new PasswordHistoryDTO();
    			int countExisting = 0;
    			// Current password
    			if (passwordHistoryService.isPasswordHistoryExists(emailAndPasswordVM.getPassword(), user.get().getPassword())) 
    				countExisting++;
    			// History No 1
    			if (passwordHistoryService.isPasswordHistoryExists(emailAndPasswordVM.getPassword(), passwordHistoryDTO.get().getHistoryNo1())) 
    				countExisting++;
    			// History No 2
    			if (passwordHistoryService.isPasswordHistoryExists(emailAndPasswordVM.getPassword(), passwordHistoryDTO.get().getHistoryNo2())) 
    				countExisting++;
    			// History No 3
    			if (passwordHistoryService.isPasswordHistoryExists(emailAndPasswordVM.getPassword(), passwordHistoryDTO.get().getHistoryNo3())) 
    				countExisting++;
    			// History No 4
    			if (passwordHistoryService.isPasswordHistoryExists(emailAndPasswordVM.getPassword(), passwordHistoryDTO.get().getHistoryNo4())) 
    				countExisting++;
    			// History No 5
    			if (passwordHistoryService.isPasswordHistoryExists(emailAndPasswordVM.getPassword(), passwordHistoryDTO.get().getHistoryNo5())) 
    				countExisting++;
    			// TODO
    			if (countExisting > 0)
    				checkPasswordExist = true;
    			if (checkPasswordExist) {
    				return new ResponseEntity<>("PASSWORD EXIST", HttpStatus.BAD_REQUEST);
    			} else {
    				// TODO
    				phDTO.setUserId(user.get().getId());
    				phDTO.setUserLogin(user.get().getEmail());
    				passwordHistoryService.save(phDTO);
    				userService.changePasswordByUserEmail(user.get().getEmail(), emailAndPasswordVM.getPassword());
    				return new ResponseEntity<>("OK", HttpStatus.OK);
    			}
    		} else {
    			if (passwordHistoryService.isPasswordHistoryExists(emailAndPasswordVM.getPassword(), user.get().getPassword())) 
    				checkPasswordExist = true;
    			if (checkPasswordExist) {
    				return new ResponseEntity<>("PASSWORD EXIST", HttpStatus.BAD_REQUEST);
    			} else {
    				PasswordHistoryDTO phDTO = new PasswordHistoryDTO();
    				phDTO.setHistoryNo1(user.get().getPassword());
    				phDTO.setUserId(user.get().getId());
    				phDTO.setUserLogin(user.get().getEmail());
    				passwordHistoryService.save(phDTO);
    				userService.changePasswordByUserEmail(user.get().getEmail(), emailAndPasswordVM.getPassword());
    				return new ResponseEntity<>("OK", HttpStatus.OK);
    			}
    		}
    	} else {
    		return new ResponseEntity<>("USER NOT EXIST", HttpStatus.BAD_REQUEST);
    	}
    }    

    /**
     * POST   /account/reset_password/init : Send an email to reset the password of the user
     *
     * @param mail the mail of the user
     * @return the ResponseEntity with status 200 (OK) if the email was sent, or status 400 (Bad Request) if the email address is not registered
     */
    @PostMapping(path = "/account/reset_password/init",
        produces = MediaType.TEXT_PLAIN_VALUE)
    @Timed
    public ResponseEntity requestPasswordReset(@RequestBody String mail) {
        return userService.requestPasswordReset(mail)
            .map(user -> {
                mailService.sendPasswordResetMail(user);
                return new ResponseEntity<>("email was sent", HttpStatus.OK);
            }).orElse(new ResponseEntity<>("email address not registered", HttpStatus.BAD_REQUEST));
    }

    /**
     * POST   /account/reset_password/finish : Finish to reset the password of the user
     *
     * @param keyAndPassword the generated key and the new password
     * @return the ResponseEntity with status 200 (OK) if the password has been reset,
     * or status 400 (Bad Request) or 500 (Internal Server Error) if the password could not be reset
     */
    @PostMapping(path = "/account/reset_password/finish",
        produces = MediaType.TEXT_PLAIN_VALUE)
    @Timed
    public ResponseEntity<String> finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
        if (!checkPasswordLength(keyAndPassword.getNewPassword())) {
            return new ResponseEntity<>(CHECK_ERROR_MESSAGE, HttpStatus.BAD_REQUEST);
        }
        return userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey())
              .map(user -> new ResponseEntity<String>(HttpStatus.OK))
              .orElse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private boolean checkPasswordLength(String password) {
        return !StringUtils.isEmpty(password) &&
            password.length() >= ManagedUserVM.PASSWORD_MIN_LENGTH &&
            password.length() <= ManagedUserVM.PASSWORD_MAX_LENGTH;
    }
}
