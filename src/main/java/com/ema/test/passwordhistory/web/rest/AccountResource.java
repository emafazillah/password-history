package com.ema.test.passwordhistory.web.rest;


import com.ema.test.passwordhistory.domain.User;
import com.ema.test.passwordhistory.repository.UserRepository;
import com.ema.test.passwordhistory.security.SecurityUtils;
import com.ema.test.passwordhistory.service.MailService;
import com.ema.test.passwordhistory.service.PasswordHistoryService;
import com.ema.test.passwordhistory.service.UserService;
import com.ema.test.passwordhistory.service.dto.PasswordHistoryDTO;
import com.ema.test.passwordhistory.service.dto.UserDTO;
import com.ema.test.passwordhistory.web.rest.vm.EmailAndPasswordVM;
import com.ema.test.passwordhistory.web.rest.errors.*;
import com.ema.test.passwordhistory.web.rest.vm.KeyAndPasswordVM;
import com.ema.test.passwordhistory.web.rest.vm.ManagedUserVM;

import io.micrometer.core.annotation.Timed;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static class AccountResourceException extends RuntimeException {
        private AccountResourceException(String message) {
            super(message);
        }
    }

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
     * {@code POST  /register} : register the user.
     *
     * @param managedUserVM the managed user View Model.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already used.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) {
        if (!checkPasswordLength(managedUserVM.getPassword())) {
            throw new InvalidPasswordException();
        }
        User user = userService.registerUser(managedUserVM, managedUserVM.getPassword());
        mailService.sendActivationEmail(user);
    }

    /**
     * {@code GET  /activate} : activate the registered user.
     *
     * @param key the activation key.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be activated.
     */
    @GetMapping("/activate")
    public void activateAccount(@RequestParam(value = "key") String key) {
        Optional<User> user = userService.activateRegistration(key);
        if (!user.isPresent()) {
            throw new AccountResourceException("No user was found for this activation key");
        }
    }

    /**
     * {@code GET  /authenticate} : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request.
     * @return the login if the user is authenticated.
     */
    @GetMapping("/authenticate")
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * {@code GET  /account} : get the current user.
     *
     * @return the current user.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @GetMapping("/account")
    public UserDTO getAccount() {
        return userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new AccountResourceException("User could not be found"));
    }

    /**
     * {@code POST  /account} : update the current user information.
     *
     * @param userDTO the current user information.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user login wasn't found.
     */
    @PostMapping("/account")
    public void saveAccount(@Valid @RequestBody UserDTO userDTO) {
        String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new AccountResourceException("Current user login not found"));
        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getLogin().equalsIgnoreCase(userLogin))) {
            throw new EmailAlreadyUsedException();
        }
        Optional<User> user = userRepository.findOneByLogin(userLogin);
        if (!user.isPresent()) {
            throw new AccountResourceException("User could not be found");
        }
        userService.updateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(),
            userDTO.getLangKey(), userDTO.getImageUrl());
    }

    /**
     * {@code POST  /account/change-password} : changes the current user's password.
     *
     * @param passwordChangeDto current and new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the new password is incorrect.
     */
    @PostMapping(path = "/account/change_password",
            produces = MediaType.TEXT_PLAIN_VALUE)
    @Timed
    public ResponseEntity changePassword(@RequestBody EmailAndPasswordVM emailAndPasswordVM) {
        if (!checkPasswordLength(emailAndPasswordVM.getPassword())) {
            return new ResponseEntity<>(CHECK_ERROR_MESSAGE, HttpStatus.BAD_REQUEST);
        }
        // Check password history
    	Optional<User> user = userRepository.findOneByEmailIgnoreCase(emailAndPasswordVM.getEmail());
    	if (user.isPresent()) {
    		Boolean checkPasswordExist = false;
    		Optional<PasswordHistoryDTO> passwordHistoryDTO = passwordHistoryService.findOneByUserEmail(user.get().getEmail());
    		if (passwordHistoryDTO.isPresent()) {
    			PasswordHistoryDTO phDTO = new PasswordHistoryDTO();
    			int countExisting = 0;
    			// Current password
    			String currentPassword = user.get().getPassword();
    			if (passwordHistoryService.isPasswordHistoryExists(emailAndPasswordVM.getPassword(), currentPassword)) {
    				countExisting++;
    			// History No 1
    			} else if (passwordHistoryService.isPasswordHistoryExists(emailAndPasswordVM.getPassword(), passwordHistoryDTO.get().getHistoryNo1())) {
    				countExisting++;
    			} else if (passwordHistoryDTO.get().getHistoryNo1() == null) {
    				phDTO.setHistoryNo1(currentPassword);
    				phDTO.setHistoryNo2(passwordHistoryDTO.get().getHistoryNo2());
    				phDTO.setHistoryNo3(passwordHistoryDTO.get().getHistoryNo3());
    				phDTO.setHistoryNo4(passwordHistoryDTO.get().getHistoryNo4());
    				phDTO.setHistoryNo5(passwordHistoryDTO.get().getHistoryNo5());
    			// History No 2
    			} else if (passwordHistoryService.isPasswordHistoryExists(emailAndPasswordVM.getPassword(), passwordHistoryDTO.get().getHistoryNo2())) {
    				countExisting++;
    			} else if (passwordHistoryDTO.get().getHistoryNo2() == null) {
    				phDTO.setHistoryNo1(passwordHistoryDTO.get().getHistoryNo1());
    				phDTO.setHistoryNo2(currentPassword);
    				phDTO.setHistoryNo3(passwordHistoryDTO.get().getHistoryNo3());
    				phDTO.setHistoryNo4(passwordHistoryDTO.get().getHistoryNo4());
    				phDTO.setHistoryNo5(passwordHistoryDTO.get().getHistoryNo5());
    			// History No 3
    			} else if (passwordHistoryService.isPasswordHistoryExists(emailAndPasswordVM.getPassword(), passwordHistoryDTO.get().getHistoryNo3())) {
    				countExisting++;
    			} else if (passwordHistoryDTO.get().getHistoryNo3() == null) {
    				phDTO.setHistoryNo1(passwordHistoryDTO.get().getHistoryNo1());
    				phDTO.setHistoryNo2(passwordHistoryDTO.get().getHistoryNo2());
    				phDTO.setHistoryNo3(currentPassword);
    				phDTO.setHistoryNo4(passwordHistoryDTO.get().getHistoryNo4());
    				phDTO.setHistoryNo5(passwordHistoryDTO.get().getHistoryNo5());
    			// History No 4
    			} else if (passwordHistoryService.isPasswordHistoryExists(emailAndPasswordVM.getPassword(), passwordHistoryDTO.get().getHistoryNo4())) {
    				countExisting++;
    			} else if (passwordHistoryDTO.get().getHistoryNo4() == null) {
    				phDTO.setHistoryNo1(passwordHistoryDTO.get().getHistoryNo1());
    				phDTO.setHistoryNo2(passwordHistoryDTO.get().getHistoryNo2());
    				phDTO.setHistoryNo3(passwordHistoryDTO.get().getHistoryNo3());
    				phDTO.setHistoryNo4(currentPassword);
    				phDTO.setHistoryNo5(passwordHistoryDTO.get().getHistoryNo5());
    			// History No 5
    			} else if (passwordHistoryService.isPasswordHistoryExists(emailAndPasswordVM.getPassword(), passwordHistoryDTO.get().getHistoryNo5())) {
    				countExisting++;
    			} else if (passwordHistoryDTO.get().getHistoryNo5() == null) {
    				phDTO.setHistoryNo1(passwordHistoryDTO.get().getHistoryNo1());
    				phDTO.setHistoryNo2(passwordHistoryDTO.get().getHistoryNo2());
    				phDTO.setHistoryNo3(passwordHistoryDTO.get().getHistoryNo3());
    				phDTO.setHistoryNo4(passwordHistoryDTO.get().getHistoryNo4());
    				phDTO.setHistoryNo5(currentPassword);
    			} else {
    				// Rotate to left
    				phDTO.setHistoryNo1(passwordHistoryDTO.get().getHistoryNo2());
    				phDTO.setHistoryNo2(passwordHistoryDTO.get().getHistoryNo3());
    				phDTO.setHistoryNo3(passwordHistoryDTO.get().getHistoryNo4());
    				phDTO.setHistoryNo4(passwordHistoryDTO.get().getHistoryNo5());
    				phDTO.setHistoryNo5(currentPassword);
    			}
    			if (countExisting > 0)
    				checkPasswordExist = true;
    			if (checkPasswordExist) {
    				return new ResponseEntity<>("PASSWORD EXIST", HttpStatus.BAD_REQUEST);
    			} else {
    				phDTO.setId(passwordHistoryDTO.get().getId());
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
     * {@code POST   /account/reset-password/init} : Send an email to reset the password of the user.
     *
     * @param mail the mail of the user.
     * @throws EmailNotFoundException {@code 400 (Bad Request)} if the email address is not registered.
     */
    @PostMapping(path = "/account/reset-password/init")
    public void requestPasswordReset(@RequestBody String mail) {
       mailService.sendPasswordResetMail(
           userService.requestPasswordReset(mail)
               .orElseThrow(EmailNotFoundException::new)
       );
    }

    /**
     * {@code POST   /account/reset-password/finish} : Finish to reset the password of the user.
     *
     * @param keyAndPassword the generated key and the new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the password could not be reset.
     */
    @PostMapping(path = "/account/reset-password/finish")
    public void finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
        if (!checkPasswordLength(keyAndPassword.getNewPassword())) {
            throw new InvalidPasswordException();
        }
        Optional<User> user =
            userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());

        if (!user.isPresent()) {
            throw new AccountResourceException("No user was found for this reset key");
        }
    }

    private static boolean checkPasswordLength(String password) {
        return !StringUtils.isEmpty(password) &&
            password.length() >= ManagedUserVM.PASSWORD_MIN_LENGTH &&
            password.length() <= ManagedUserVM.PASSWORD_MAX_LENGTH;
    }
}
